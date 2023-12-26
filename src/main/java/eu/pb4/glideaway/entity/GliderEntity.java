package eu.pb4.glideaway.entity;

import eu.pb4.glideaway.item.GlideItems;
import eu.pb4.glideaway.item.HangGliderItem;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class GliderEntity extends Entity implements PolymerEntity {
    private static final TrackedData<Float> ROLL = DataTracker.registerData(GliderEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private ItemStack itemStack = GlideItems.HANG_GLIDER.getDefaultStack();
    private ItemStack modelStack = GlideItems.HANG_GLIDER.getDefaultStack();
    private int damageTimer;

    private ElementHolder holder = new ElementHolder();

    public static boolean create(World world, LivingEntity rider, ItemStack stack, Hand hand) {
        if (rider != null && rider.hasVehicle()) {
            return false;
        }
        stack.damage((int) Math.max(0, -rider.getVelocity().y * 50 * (90 - Math.abs(MathHelper.clamp(rider.getPitch(), -30, 80))) / 90), rider, player -> player.sendEquipmentBreakStatus(hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));

        if (stack.isEmpty()) {
            return false;
        }

        var entity = new GliderEntity(GlideEntities.GLIDER, world);
        var sitting = rider.getDimensions(EntityPose.SITTING);
        var currentDim = rider.getDimensions(rider.getPose());
        entity.setItemStack(stack);
        entity.setPosition(rider.getPos().add(0, currentDim.height - sitting.height - rider.getRidingOffset(entity), 0));
        entity.setYaw(rider.getYaw());
        entity.setPitch(rider.getPitch());
        entity.setVelocity(rider.getVelocity().add(rider.getRotationVector().multiply(0.2, 0.02, 0.2).multiply(rider.isSneaking() ? 2 : 1)));

        world.spawnEntity(entity);

        if (!rider.isSneaking()) {
            rider.startRiding(entity);
        }
        return true;
    }

    public void setItemStack(ItemStack stack) {
        this.itemStack = stack;
        this.modelStack = stack.copy();
    }

    public GliderEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setInvisible(true);
        var interaction = InteractionElement.redirect(this);
        interaction.setSize(0.8f, 1.2f);
        this.holder.addElement(interaction);
    }

    @Override
    protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f();
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
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
                livingEntity.setStackInHand(Hand.MAIN_HAND, this.getItemStack());
            } else if (livingEntity.getStackInHand(Hand.OFF_HAND).isEmpty()) {
                livingEntity.setStackInHand(Hand.OFF_HAND, this.getItemStack());
            } else if (!(entity instanceof PlayerEntity player && player.giveItemStack(this.getItemStack()))) {
                this.dropStack(this.getItemStack());
            }
        } else {
            this.dropStack(this.getItemStack());
        }
        this.setItemStack(ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        super.tick();
        var passenger = this.getFirstPassenger();

        if (passenger == null && this.holder.getAttachment() == null) {
            EntityAttachment.of(this.holder, this);
            VirtualEntityUtils.addVirtualPassenger(this, this.holder.getEntityIds().getInt(0));
        } else if (passenger != null && this.holder.getAttachment() != null) {
            VirtualEntityUtils.removeVirtualPassenger(this, this.holder.getEntityIds().getInt(0));
            this.holder.destroy();
        }

        if ((this.isOnGround() || (passenger != null && passenger.isOnGround())) && this.age > 10) {
            this.giveOrDrop(passenger);
            this.discard();
            return;
        }

        if (passenger != null) {
            this.setYaw(MathHelper.lerpAngleDegrees(0.15f, this.getYaw(), passenger.getYaw()));
            this.setPitch(MathHelper.lerpAngleDegrees(0.15f, this.getPitch(), MathHelper.clamp(passenger.getPitch(), -30, 80)));
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
                this.addVelocity(0,  ((32 - i) / 32f) * 0.04, 0);
                dmgFrequency = 1;
            } else if (state.isOf(Blocks.LAVA) && i < 4) {
                this.addVelocity(0,  ((4 - i) / 4f) * 0.01, 0);
                dmgTimeout = 2;
                dmgFrequency = 1;
                dmg = 2;
            } else if (state.isSideSolidFullSquare(serverWorld, mut, Direction.UP)) {
                break;
            }

            mut.move(0, -1, 0);
        }


        double gravity = 0.075;
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
                this.discard();
            }
        } else {
            this.setVelocity(velocity.multiply(0.985F, 0.96F, 0.985F));
        }
        int i = ++this.damageTimer;
        if ((i % dmgTimeout == 0 && (i / dmgFrequency) % dmgTimeout == 0) || this.isOnFire()) {
            if (itemStack.damage(dmg, this.random, null)) {
                this.setItemStack(ItemStack.EMPTY);
                if (passenger != null) {
                    passenger.stopRiding();
                }

                this.discard();
                return;
            }
            this.emitGameEvent(GameEvent.ELYTRA_GLIDE);
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
                passenger.damage(this.getDamageSources().flyIntoWall(), o);
            }
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
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        if (initial) {
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.TELEPORTATION_DURATION, 2));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.INTERPOLATION_DURATION, 2));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.Item.ITEM, this.modelStack));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.TRANSLATION, new Vector3f(0, 1.2f, -0.05f)));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.SCALE, new Vector3f(1.5f)));
        }

        for (var entry : data.toArray(new DataTracker.SerializedEntry<?>[0])) {
            if (entry.id() == ROLL.getId()) {
                data.remove(entry);
                data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.LEFT_ROTATION, new Quaternionf().rotateZ((Float) entry.value())));
                data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.START_INTERPOLATION, 0));
            }
        }
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(ROLL, 0f);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        setItemStack(ItemStack.fromNbt(nbt.getCompound("stack")));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.put("stack", this.itemStack.writeNbt(new NbtCompound()));
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
