package eu.pb4.glideaway.entity;

import com.mojang.datafixers.util.Pair;
import eu.pb4.glideaway.item.GlideItems;
import eu.pb4.glideaway.item.HangGliderItem;
import eu.pb4.glideaway.util.GlideDimensionTypeTags;
import eu.pb4.glideaway.util.GlideGamerules;
import eu.pb4.glideaway.util.GlideSoundEvents;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.TravelCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class GliderEntity extends Entity implements PolymerEntity {
    public static final TravelCriterion FLY_WITH_GLIDER = Criteria.register("glideaway:fly_with_glider", new TravelCriterion());
    private static final TrackedData<Float> ROLL = DataTracker.registerData(GliderEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private ItemStack itemStack = GlideItems.HANG_GLIDER.getDefaultStack();
    private ItemStack modelStack = GlideItems.HANG_GLIDER.getDefaultStack();
    private int damageTimer;
    private int lastAttack = -999;

    private final ElementHolder holder = new ElementHolder();
    private int soundTimer;
    private int attacks;
    private boolean noDamage = false;
    private boolean hasLanded = false;

    private Vec3d startingPosition = Vec3d.ZERO;

    public static boolean create(ServerWorld world, LivingEntity rider, ItemStack stack, Hand hand) {
        if (rider.hasVehicle() || (rider.isSneaking() && !world.getGameRules().getBoolean(GlideGamerules.ALLOW_SNEAK_RELEASE))) {
            return false;
        }
        var noDamage = rider instanceof PlayerEntity player && player.isCreative();

        if (!noDamage) {
            stack.damage((int) Math.max(0, -rider.getVelocity().y
                            * world.getGameRules().get(GlideGamerules.INITIAL_VELOCITY_GLIDER_DAMAGE).get()
                            * (90 - Math.abs(MathHelper.clamp(rider.getPitch(), -30, 80))) / 90),
                    rider, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }

        if (stack.isEmpty()) {
            return false;
        }

        var entity = new GliderEntity(GlideEntities.GLIDER, world);
        var sitting = rider.getDimensions(EntityPose.SITTING);
        var currentDim = rider.getDimensions(rider.getPose());
        entity.setItemStack(stack);
        entity.setPosition(rider.getPos().add(0, currentDim.height() - sitting.height(), 0));
        entity.startingPosition = entity.getPos();
        entity.setYaw(rider.getYaw());
        entity.setPitch(rider.getPitch());
        entity.setVelocity(rider.getVelocity().add(rider.getRotationVector().multiply(0.2, 0.02, 0.2).multiply(rider.isSneaking() ? 2 : 1)));
        entity.noDamage = noDamage;
        world.spawnEntity(entity);
        entity.playSound(GlideSoundEvents.HANG_GLIDER_OPENS, 0.8f, entity.random.nextFloat() * 0.2f + 1.2f);

        if (!rider.isSneaking()) {
            rider.startRiding(entity);
        }
        return true;
    }

    public static ItemStack createDispenser(BlockPointer pointer, ItemStack stack) {
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        ServerWorld serverWorld = pointer.world();
        Vec3d vec3d = pointer.centerPos();

        var entity = new GliderEntity(GlideEntities.GLIDER, serverWorld);
        entity.setItemStack(stack.copyWithCount(1));
        entity.setPosition(vec3d.x, vec3d.y - 0.5f, vec3d.z);
        entity.setYaw(direction.getAxis() == Direction.Axis.Y ? 0 : direction.getHorizontalQuarterTurns());
        entity.setPitch(direction.getAxis() != Direction.Axis.Y ? 0 : (direction == Direction.UP ? -90 : 90));
        entity.setVelocity(Vec3d.of(direction.getVector()).multiply(0.6));

        serverWorld.spawnEntity(entity);
        entity.playSound(GlideSoundEvents.HANG_GLIDER_OPENS, 0.8f, entity.random.nextFloat() * 0.2f + 1.2f);
        stack.decrement(1);
        return stack;
    }

    public void setItemStack(ItemStack stack) {
        this.itemStack = stack;
        this.modelStack = stack.getItem().getDefaultStack();
        this.modelStack.set(DataComponentTypes.DYED_COLOR, stack.get(DataComponentTypes.DYED_COLOR));
        this.modelStack.set(DataComponentTypes.BASE_COLOR, stack.get(DataComponentTypes.BASE_COLOR));
        this.modelStack.set(DataComponentTypes.ITEM_MODEL, stack.get(DataComponentTypes.ITEM_MODEL));
        this.modelStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, stack.get(DataComponentTypes.CUSTOM_MODEL_DATA));
    }

    public GliderEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setInvisible(true);
        var interaction = InteractionElement.redirect(this);
        interaction.setSize(0.8f, 1.2f);
        this.holder.addElement(interaction);
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.CAN_BREAK_ARMOR_STAND)) {
            if (this.age - this.lastAttack > 30) {
                this.attacks = 0;
            } else if (this.attacks == 2) {
                this.giveOrDrop(this.getFirstPassenger());
                this.discard();
            }

            this.attacks++;
            this.lastAttack = this.age;
            var sign = Math.signum(this.dataTracker.get(ROLL));
            if (sign == 0) {
                sign = 1;
            }

            this.dataTracker.set(ROLL, -sign * Math.min(Math.abs(this.dataTracker.get(ROLL) + 0.2f), 1) );
            return true;
        } else if (source.isIn(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS)) {
            this.giveOrDrop(this.getFirstPassenger());
            this.discard();
            return true;
        } else if (source.isIn(DamageTypeTags.IGNITES_ARMOR_STANDS)) {
            this.damageStack((int) (amount * 2));
            return true;
        }

        return false;
    }

    public void damageStack(int i) {
        if (this.noDamage) {
            return;
        }

        var serverWorld = (ServerWorld) this.getWorld();
        itemStack.damage(i, serverWorld, null, (item) -> {
            var old = this.itemStack;
            old.increment(1);
            this.setItemStack(ItemStack.EMPTY);
            if (this.getFirstPassenger() != null) {
                this.getFirstPassenger().stopRiding();
            }
            serverWorld.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, old), this.getX(), this.getY() + 0.5, this.getZ(), 80, 1, 1, 1, 0.1);
            this.discard();
        });
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        return false;
    }

    @Override
    protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return Vec3d.ZERO;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public boolean canHit() {
        return false;
    }

    public void giveOrDrop(@Nullable Entity entity) {
        if (getWorld() instanceof ServerWorld world) {
            if (this.isRemoved()) {
                return;
            }

            if (entity instanceof LivingEntity livingEntity && entity.isAlive()) {
                if (livingEntity.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
                    livingEntity.setStackInHand(Hand.MAIN_HAND, this.getItemStack());
                } else if (livingEntity.getStackInHand(Hand.OFF_HAND).isEmpty()) {
                    livingEntity.setStackInHand(Hand.OFF_HAND, this.getItemStack());
                } else if (!(entity instanceof PlayerEntity player && player.giveItemStack(this.getItemStack()))) {
                    this.dropStack(world, this.getItemStack());
                }
            } else {
                this.dropStack(world, this.getItemStack());
            }
            this.setItemStack(ItemStack.EMPTY);
            this.discard();
        }
    }

    @Override
    public void tick() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        super.tick();
        var passenger = this.getFirstPassenger();

        if (passenger != null && !passenger.isAlive()) {
            passenger.stopRiding();
            passenger = null;
        }

        if (passenger == null && this.holder.getAttachment() == null) {
            EntityAttachment.of(this.holder, this);
            VirtualEntityUtils.addVirtualPassenger(this, this.holder.getEntityIds().getInt(0));
        } else if (passenger != null && this.holder.getAttachment() != null) {
            VirtualEntityUtils.removeVirtualPassenger(this, this.holder.getEntityIds().getInt(0));
            this.holder.destroy();
        }

        if ((this.isOnGround() || (passenger != null && passenger.isOnGround())) && this.age > 10) {
            this.hasLanded = true;
            this.giveOrDrop(passenger);
            return;
        }

        if (passenger != null) {
            this.setYaw(MathHelper.lerpAngleDegrees(0.175f, this.getYaw(), passenger.getYaw()));
            this.setPitch(MathHelper.lerpAngleDegrees(0.175f, this.getPitch(), MathHelper.clamp(passenger.getPitch(), -30, 80)));
            var roll = MathHelper.clamp((-MathHelper.subtractAngles(passenger.getYaw(), this.getYaw())) * MathHelper.RADIANS_PER_DEGREE * 0.5f, -1f, 1f);

            if (Math.abs(roll - this.getDataTracker().get(ROLL)) > MathHelper.RADIANS_PER_DEGREE / 2) {
                this.getDataTracker().set(ROLL, roll);
            }
        } else {
            var roll = this.getDataTracker().get(ROLL) * 0.98f;

            if (Math.abs(roll - this.getDataTracker().get(ROLL)) > MathHelper.RADIANS_PER_DEGREE / 2 || Math.abs(roll) > MathHelper.RADIANS_PER_DEGREE / 2) {
                this.getDataTracker().set(ROLL, roll);
            }
        }

        int dmgTimeout = serverWorld.getRegistryKey() == World.NETHER ? 6 : 10;
        int dmgFrequency = 2;
        int dmg = 1;

        var mut = this.getBlockPos().mutableCopy();
        for (int i = 0; i < 32; i++) {
            var state = serverWorld.getBlockState(mut);
            if (state.isOf(Blocks.FIRE) || state.isOf(Blocks.CAMPFIRE) && i < 24) {
                this.addVelocity(0,  ((32 - i) / 32f) * serverWorld.getGameRules().get(GlideGamerules.FIRE_BOOST).get(), 0);
                dmgFrequency = 1;
            } else if (state.isOf(Blocks.LAVA) && i < 6) {
                this.addVelocity(0,  ((6 - i) / 6f) * serverWorld.getGameRules().get(GlideGamerules.LAVA_BOOST).get(), 0);
                dmgTimeout = 2;
                dmgFrequency = 1;
                dmg = 2;
            } else if (state.isSideSolidFullSquare(serverWorld, mut, Direction.UP)) {
                break;
            }

            mut.move(0, -1, 0);
        }


        double gravity = 0.068;
        if (serverWorld.hasRain(this.getBlockPos())) {
            gravity = 0.09;
        } else if (GlideDimensionTypeTags.isIn(serverWorld, GlideDimensionTypeTags.HIGH_GRAVITY)) {
            gravity = 0.084;
        } else if (GlideDimensionTypeTags.isIn(serverWorld, GlideDimensionTypeTags.LOW_GRAVITY)) {
            gravity = 0.056;
        }

        this.limitFallDistance();
        if (passenger != null) {
            passenger.limitFallDistance();
        }
        Vec3d velocity = this.getVelocity();
        Vec3d rotationVector = this.getRotationVector();
        float pitch = this.getPitch() * (float) (Math.PI / 180.0);
        double rotationLength = Math.sqrt(rotationVector.x * rotationVector.x + rotationVector.z * rotationVector.z);
        double horizontalVelocity = velocity.horizontalLength();
        double rotationVectorLength = rotationVector.length();
        double cosPitch = Math.cos((double)pitch);
        cosPitch = cosPitch * cosPitch * Math.min(1.0, rotationVectorLength / 0.4);
        velocity = this.getVelocity().add(0.0, gravity * (-1.0 + cosPitch * 0.75), 0.0);
        if (velocity.y < 0.0 && rotationLength > 0.0) {
            double m = velocity.y * -0.1 * cosPitch;
            velocity = velocity.add(rotationVector.x * m / rotationLength, m, rotationVector.z * m / rotationLength);
        }

        if (pitch < 0.0F && rotationLength > 0.0) {
            double m = horizontalVelocity * (double)(-MathHelper.sin(pitch)) * 0.04;
            velocity = velocity.add(-rotationVector.x * m / rotationLength, m * 3.2, -rotationVector.z * m / rotationLength);
        }

        if (rotationLength > 0.0) {
            velocity = velocity.add((rotationVector.x / rotationLength * horizontalVelocity - velocity.x) * 0.1, 0.0, (rotationVector.z / rotationLength * horizontalVelocity - velocity.z) * 0.1);
        }

        if (this.isInFluid()) {
            this.setVelocity(velocity.multiply(0.8F, 0.8F, 0.8F));

            if (this.getVelocity().horizontalLength() < 0.04) {
                if (passenger != null) {
                    passenger.stopRiding();
                }
                this.giveOrDrop(passenger);
                return;
            }
        } else {
            this.setVelocity(velocity.multiply(0.985F, 0.96F, 0.985F));
        }

        if (passenger instanceof ServerPlayerEntity player && this.age > 20 * 5 && this.soundTimer++ % 20 * 4 == 0) {
            var l = this.getVelocity().length();

            if (l > 0.05 && serverWorld.getGameRules().getBoolean(GlideGamerules.WIND_SOUND)) {
                player.networkHandler.sendPacket(new PlaySoundFromEntityS2CPacket(GlideSoundEvents.WIND, SoundCategory.AMBIENT, player, (float) MathHelper.clamp(l, 0.05, 0.8), this.random.nextFloat() * 0.2f + 0.9f, this.random.nextLong()));
            }
        }

        if (passenger instanceof ServerPlayerEntity player) {
            GliderEntity.FLY_WITH_GLIDER.trigger(player, this.startingPosition);
        }

        if (this.itemStack.getItem() instanceof HangGliderItem gliderItem) {
            gliderItem.tickGlider(serverWorld, this, passenger, this.itemStack);
        }

        this.move(MovementType.SELF, this.getVelocity());
        if (this.horizontalCollision && passenger != null) {
            double m = this.getVelocity().horizontalLength();
            double n = horizontalVelocity - m;
            float o = (float)(n * 10.0 - 3.0);
            if (o > 0.0F) {
                //this.playSound(passenger.getFallSound((int)o), 1.0F, 1.0F);
                passenger.damage(serverWorld, this.getDamageSources().flyIntoWall(), o);
            }
        }

        int i = ++this.damageTimer;
        if ((i % dmgTimeout == 0 && (i / dmgFrequency) % dmgTimeout == 0) || this.isOnFire()) {
            this.damageStack(dmg);
            this.emitGameEvent(GameEvent.ELYTRA_GLIDE);
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (this.getFirstPassenger() != null) {
            return ActionResult.FAIL;
        }

        player.startRiding(this);

        return ActionResult.SUCCESS;
    }

    @Override
    protected void tickInVoid() {
        if (GlideDimensionTypeTags.isIn(this.getWorld(), GlideDimensionTypeTags.VOID_PICKUP)) {
            this.giveOrDrop(this.getFirstPassenger());
        } else {
            super.tickInVoid();
        }
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        if (initial) {
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.TELEPORTATION_DURATION, 2));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.INTERPOLATION_DURATION, 3));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.Item.ITEM, this.modelStack));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.TRANSLATION, new Vector3f(0, 1.2f, -0.05f)));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.SCALE, new Vector3f(1.5f)));
        }

        for (var entry : data.toArray(new DataTracker.SerializedEntry<?>[0])) {
            if (entry.id() == ROLL.id()) {
                data.remove(entry);
                data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.LEFT_ROTATION, new Quaternionf().rotateZ((Float) entry.value())));
                data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.START_INTERPOLATION, 0));
            }
        }
    }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(ROLL, 0f);
    }

    @Override
    protected void readCustomData(ReadView view) {
        setItemStack(view.read("stack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
        this.noDamage = view.getBoolean("no_damage", false);
        this.startingPosition = view.read("starting_position", Vec3d.CODEC).orElse(this.getPos());
    }

    @Override
    protected void writeCustomData(WriteView view) {
        if (!this.itemStack.isEmpty()) {
            view.put("stack", ItemStack.OPTIONAL_CODEC, this.itemStack);
        }
        view.put("starting_position", Vec3d.CODEC, this.startingPosition);
        view.putBoolean("no_damage", this.noDamage);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public boolean hasCurseOfBinding() {
        return EnchantmentHelper.hasAnyEnchantmentsWith(this.itemStack, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE);
    }

    public boolean hasLanded() {
        return this.hasLanded || this.isRemoved();
    }
}
