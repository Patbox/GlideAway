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
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.DistanceTrigger;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class GliderEntity extends Entity implements PolymerEntity {
    public static final DistanceTrigger FLY_WITH_GLIDER = CriteriaTriggers.register("glideaway:fly_with_glider", new DistanceTrigger());
    private static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(GliderEntity.class, EntityDataSerializers.FLOAT);
    private ItemStack itemStack = GlideItems.HANG_GLIDER.getDefaultInstance();
    private ItemStack modelStack = GlideItems.HANG_GLIDER.getDefaultInstance();
    private int damageTimer;
    private int lastAttack = -999;

    private final ElementHolder holder = new ElementHolder();
    private int soundTimer;
    private int attacks;
    private boolean noDamage = false;
    private boolean hasLanded = false;

    private Vec3 startingPosition = Vec3.ZERO;

    public static boolean create(ServerLevel world, LivingEntity rider, ItemStack stack, InteractionHand hand) {
        if (rider.isPassenger() || (rider.isShiftKeyDown() && !world.getGameRules().get(GlideGamerules.ALLOW_SNEAK_RELEASE))) {
            return false;
        }
        var noDamage = rider instanceof Player player && player.isCreative();

        if (!noDamage) {
            stack.hurtAndBreak((int) Math.max(0, -rider.getDeltaMovement().y
                            * world.getGameRules().get(GlideGamerules.INITIAL_VELOCITY_GLIDER_DAMAGE)
                            * (90 - Math.abs(Mth.clamp(rider.getXRot(), -30, 80))) / 90),
                    rider, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }

        if (stack.isEmpty()) {
            return false;
        }

        var entity = new GliderEntity(GlideEntities.GLIDER, world);
        var sitting = rider.getDimensions(Pose.SITTING);
        var currentDim = rider.getDimensions(rider.getPose());
        entity.setItemStack(stack);
        entity.setPos(rider.position().add(0, currentDim.height() - sitting.height(), 0));
        entity.startingPosition = entity.position();
        entity.setYRot(rider.getYRot());
        entity.setXRot(rider.getXRot());
        entity.setDeltaMovement(rider.getDeltaMovement().add(rider.getLookAngle().multiply(0.2, 0.02, 0.2).scale(rider.isShiftKeyDown() ? 2 : 1)));
        entity.noDamage = noDamage;
        world.addFreshEntity(entity);
        entity.playSound(GlideSoundEvents.HANG_GLIDER_OPENS, 0.8f, entity.random.nextFloat() * 0.2f + 1.2f);

        if (!rider.isShiftKeyDown()) {
            rider.startRiding(entity);
        }
        return true;
    }

    public static ItemStack createDispenser(BlockSource pointer, ItemStack stack) {
        Direction direction = pointer.state().getValue(DispenserBlock.FACING);
        ServerLevel serverWorld = pointer.level();
        Vec3 vec3d = pointer.center();

        var entity = new GliderEntity(GlideEntities.GLIDER, serverWorld);
        entity.setItemStack(stack.copyWithCount(1));
        entity.setPos(vec3d.x, vec3d.y - 0.5f, vec3d.z);
        entity.setYRot(direction.getAxis() == Direction.Axis.Y ? 0 : direction.get2DDataValue());
        entity.setXRot(direction.getAxis() != Direction.Axis.Y ? 0 : (direction == Direction.UP ? -90 : 90));
        entity.setDeltaMovement(Vec3.atLowerCornerOf(direction.getUnitVec3i()).scale(0.6));

        serverWorld.addFreshEntity(entity);
        entity.playSound(GlideSoundEvents.HANG_GLIDER_OPENS, 0.8f, entity.random.nextFloat() * 0.2f + 1.2f);
        stack.shrink(1);
        return stack;
    }

    public void setItemStack(ItemStack stack) {
        this.itemStack = stack;
        this.modelStack = stack.getItem().getDefaultInstance();
        this.modelStack.set(DataComponents.DYED_COLOR, stack.get(DataComponents.DYED_COLOR));
        this.modelStack.set(DataComponents.BASE_COLOR, stack.get(DataComponents.BASE_COLOR));
        this.modelStack.set(DataComponents.ITEM_MODEL, stack.get(DataComponents.ITEM_MODEL));
        this.modelStack.set(DataComponents.CUSTOM_MODEL_DATA, stack.get(DataComponents.CUSTOM_MODEL_DATA));
    }

    public GliderEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.setInvisible(true);
        var interaction = InteractionElement.redirect(this);
        interaction.setSize(0.8f, 1.2f);
        this.holder.addElement(interaction);
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (source.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND)) {
            if (this.tickCount - this.lastAttack > 30) {
                this.attacks = 0;
            } else if (this.attacks == 2) {
                this.giveOrDrop(this.getFirstPassenger());
                this.discard();
            }

            this.attacks++;
            this.lastAttack = this.tickCount;
            var sign = Math.signum(this.entityData.get(ROLL));
            if (sign == 0) {
                sign = 1;
            }

            this.entityData.set(ROLL, -sign * Math.min(Math.abs(this.entityData.get(ROLL) + 0.2f), 1) );
            return true;
        } else if (source.is(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS)) {
            this.giveOrDrop(this.getFirstPassenger());
            this.discard();
            return true;
        } else if (source.is(DamageTypeTags.IGNITES_ARMOR_STANDS)) {
            this.damageStack((int) (amount * 2));
            return true;
        }

        return false;
    }

    public void damageStack(int i) {
        if (this.noDamage) {
            return;
        }

        var serverWorld = (ServerLevel) this.level();
        itemStack.hurtAndBreak(i, serverWorld, null, (item) -> {
            var old = this.itemStack;
            old.grow(1);
            this.setItemStack(ItemStack.EMPTY);
            if (this.getFirstPassenger() != null) {
                this.getFirstPassenger().stopRiding();
            }
            serverWorld.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, old), this.getX(), this.getY() + 0.5, this.getZ(), 80, 1, 1, 1, 0.1);
            this.discard();
        });
    }

    @Override
    public boolean skipAttackInteraction(Entity attacker) {
        return false;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return Vec3.ZERO;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public void giveOrDrop(@Nullable Entity entity) {
        if (this.level() instanceof ServerLevel world) {
            if (this.isRemoved()) {
                return;
            }

            if (entity instanceof LivingEntity livingEntity && entity.isAlive()) {
                if (livingEntity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                    livingEntity.setItemInHand(InteractionHand.MAIN_HAND, this.getItemStack());
                } else if (livingEntity.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
                    livingEntity.setItemInHand(InteractionHand.OFF_HAND, this.getItemStack());
                } else if (!(entity instanceof Player player && player.addItem(this.getItemStack()))) {
                    this.spawnAtLocation(world, this.getItemStack());
                }
            } else {
                this.spawnAtLocation(world, this.getItemStack());
            }
            this.setItemStack(ItemStack.EMPTY);
            this.discard();
        }
    }

    @Override
    public void tick() {
        if (!(this.level() instanceof ServerLevel serverWorld)) {
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

        if ((this.onGround() || (passenger != null && passenger.onGround())) && this.tickCount > 10) {
            this.hasLanded = true;
            this.giveOrDrop(passenger);
            return;
        }

        if (passenger != null) {
            this.setYRot(Mth.rotLerp(0.175f, this.getYRot(), passenger.getYRot()));
            this.setXRot(Mth.rotLerp(0.175f, this.getXRot(), Mth.clamp(passenger.getXRot(), -30, 80)));
            var roll = Mth.clamp((-Mth.degreesDifference(passenger.getYRot(), this.getYRot())) * Mth.DEG_TO_RAD * 0.5f, -1f, 1f);

            if (Math.abs(roll - this.getEntityData().get(ROLL)) > Mth.DEG_TO_RAD / 2) {
                this.getEntityData().set(ROLL, roll);
            }
        } else {
            var roll = this.getEntityData().get(ROLL) * 0.98f;

            if (Math.abs(roll - this.getEntityData().get(ROLL)) > Mth.DEG_TO_RAD / 2 || Math.abs(roll) > Mth.DEG_TO_RAD / 2) {
                this.getEntityData().set(ROLL, roll);
            }
        }

        int dmgTimeout = serverWorld.dimension() == Level.NETHER ? 6 : 10;
        int dmgFrequency = 2;
        int dmg = 1;

        var mut = this.blockPosition().mutable();
        for (int i = 0; i < 32; i++) {
            var state = serverWorld.getBlockState(mut);
            if (state.is(Blocks.FIRE) || state.is(Blocks.CAMPFIRE) && i < 24) {
                this.push(0,  ((32 - i) / 32f) * serverWorld.getGameRules().get(GlideGamerules.FIRE_BOOST), 0);
                dmgFrequency = 1;
            } else if (state.is(Blocks.LAVA) && i < 6) {
                this.push(0,  ((6 - i) / 6f) * serverWorld.getGameRules().get(GlideGamerules.LAVA_BOOST), 0);
                dmgTimeout = 2;
                dmgFrequency = 1;
                dmg = 2;
            } else if (state.isFaceSturdy(serverWorld, mut, Direction.UP)) {
                break;
            }

            mut.move(0, -1, 0);
        }


        double gravity = 0.068;
        if (serverWorld.isRainingAt(this.blockPosition())) {
            gravity = 0.09;
        } else if (GlideDimensionTypeTags.isIn(serverWorld, GlideDimensionTypeTags.HIGH_GRAVITY)) {
            gravity = 0.084;
        } else if (GlideDimensionTypeTags.isIn(serverWorld, GlideDimensionTypeTags.LOW_GRAVITY)) {
            gravity = 0.056;
        }

        this.checkFallDistanceAccumulation();
        if (passenger != null) {
            passenger.checkFallDistanceAccumulation();
        }
        Vec3 velocity = this.getDeltaMovement();
        Vec3 rotationVector = this.getLookAngle();
        float pitch = this.getXRot() * (float) (Math.PI / 180.0);
        double rotationLength = Math.sqrt(rotationVector.x * rotationVector.x + rotationVector.z * rotationVector.z);
        double horizontalVelocity = velocity.horizontalDistance();
        double rotationVectorLength = rotationVector.length();
        double cosPitch = Math.cos((double)pitch);
        cosPitch = cosPitch * cosPitch * Math.min(1.0, rotationVectorLength / 0.4);
        velocity = this.getDeltaMovement().add(0.0, gravity * (-1.0 + cosPitch * 0.75), 0.0);
        if (velocity.y < 0.0 && rotationLength > 0.0) {
            double m = velocity.y * -0.1 * cosPitch;
            velocity = velocity.add(rotationVector.x * m / rotationLength, m, rotationVector.z * m / rotationLength);
        }

        if (pitch < 0.0F && rotationLength > 0.0) {
            double m = horizontalVelocity * (double)(-Mth.sin(pitch)) * 0.04;
            velocity = velocity.add(-rotationVector.x * m / rotationLength, m * 3.2, -rotationVector.z * m / rotationLength);
        }

        if (rotationLength > 0.0) {
            velocity = velocity.add((rotationVector.x / rotationLength * horizontalVelocity - velocity.x) * 0.1, 0.0, (rotationVector.z / rotationLength * horizontalVelocity - velocity.z) * 0.1);
        }

        if (this.isInLiquid()) {
            this.setDeltaMovement(velocity.multiply(0.8F, 0.8F, 0.8F));

            if (this.getDeltaMovement().horizontalDistance() < 0.04) {
                if (passenger != null) {
                    passenger.stopRiding();
                }
                this.giveOrDrop(passenger);
                return;
            }
        } else {
            this.setDeltaMovement(velocity.multiply(0.985F, 0.96F, 0.985F));
        }

        if (passenger instanceof ServerPlayer player && this.tickCount > 20 * 5 && this.soundTimer++ % 20 * 4 == 0) {
            var l = this.getDeltaMovement().length();

            if (l > 0.05 && serverWorld.getGameRules().get(GlideGamerules.WIND_SOUND)) {
                player.connection.send(new ClientboundSoundEntityPacket(GlideSoundEvents.WIND, SoundSource.AMBIENT, player, (float) Mth.clamp(l, 0.05, 0.8), this.random.nextFloat() * 0.2f + 0.9f, this.random.nextLong()));
            }
        }

        if (passenger instanceof ServerPlayer player) {
            GliderEntity.FLY_WITH_GLIDER.trigger(player, this.startingPosition);
        }

        if (this.itemStack.getItem() instanceof HangGliderItem gliderItem) {
            gliderItem.tickGlider(serverWorld, this, passenger, this.itemStack);
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        if (this.horizontalCollision && passenger != null) {
            double m = this.getDeltaMovement().horizontalDistance();
            double n = horizontalVelocity - m;
            float o = (float)(n * 10.0 - 3.0);
            if (o > 0.0F) {
                //this.playSound(passenger.getFallSound((int)o), 1.0F, 1.0F);
                passenger.hurtServer(serverWorld, this.damageSources().flyIntoWall(), o);
            }
        }

        int i = ++this.damageTimer;
        if ((i % dmgTimeout == 0 && (i / dmgFrequency) % dmgTimeout == 0) || this.isOnFire()) {
            this.damageStack(dmg);
            this.gameEvent(GameEvent.ELYTRA_GLIDE);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.getFirstPassenger() != null) {
            return InteractionResult.FAIL;
        }

        player.startRiding(this);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onBelowWorld() {
        if (GlideDimensionTypeTags.isIn(this.level(), GlideDimensionTypeTags.VOID_PICKUP)) {
            this.giveOrDrop(this.getFirstPassenger());
        } else {
            super.onBelowWorld();
        }
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        if (initial) {
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.TELEPORTATION_DURATION, 2));
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.INTERPOLATION_DURATION, 3));
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.Item.ITEM, this.modelStack));
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.TRANSLATION, new Vector3f(0, 1.2f, -0.05f)));
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.SCALE, new Vector3f(1.5f)));
        }

        for (var entry : data.toArray(new SynchedEntityData.DataValue<?>[0])) {
            if (entry.id() == ROLL.id()) {
                data.remove(entry);
                data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.LEFT_ROTATION, new Quaternionf().rotateZ((Float) entry.value())));
                data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.START_INTERPOLATION, 0));
            }
        }
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ROLL, 0f);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        setItemStack(view.read("stack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
        this.noDamage = view.getBooleanOr("no_damage", false);
        this.startingPosition = view.read("starting_position", Vec3.CODEC).orElse(this.position());
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        if (!this.itemStack.isEmpty()) {
            view.store("stack", ItemStack.OPTIONAL_CODEC, this.itemStack);
        }
        view.store("starting_position", Vec3.CODEC, this.startingPosition);
        view.putBoolean("no_damage", this.noDamage);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public boolean hasCurseOfBinding() {
        return EnchantmentHelper.has(this.itemStack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
    }

    public boolean hasLanded() {
        return this.hasLanded || this.isRemoved();
    }
}
