package eu.pb4.intotheskies.entity;

import eu.pb4.factorytools.api.resourcepack.BaseItemProvider;
import eu.pb4.intotheskies.item.SkiesItems;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

import static eu.pb4.intotheskies.util.SkiesUtils.id;

public class GliderEntity extends Entity implements PolymerEntity {
    private static final TrackedData<Float> ROLL = DataTracker.registerData(GliderEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final ItemStack BASE_MODEL = BaseItemProvider.requestModel(Items.LEATHER_BOOTS, id("pb_model/glider"));
    private ItemStack itemStack = ItemStack.EMPTY;
    private ItemStack modelStack = BASE_MODEL.copy();
    private int damageTimer;

    public static boolean create(World world, LivingEntity rider, ItemStack stack, Hand hand) {
        if (rider.hasVehicle()) {
            return false;
        }
        stack.damage((int) Math.max(0, -rider.getVelocity().y * 50 * (90 - Math.abs(MathHelper.clamp(rider.getPitch(), -30, 80))) / 90), rider, player -> player.sendEquipmentBreakStatus(hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));

        if (stack.isEmpty()) {
            return false;
        }

        var entity = new GliderEntity(SkiesEntities.GLIDER, world);
        var sitting = rider.getDimensions(EntityPose.SITTING);
        var currentDim = rider.getDimensions(rider.getPose());
        entity.setItemStack(stack);
        entity.setPosition(rider.getPos().add(0, currentDim.height - sitting.height, 0));
        entity.setYaw(rider.getYaw());
        entity.setPitch(rider.getPitch());
        entity.setVelocity(rider.getVelocity());

        world.spawnEntity(entity);
        rider.startRiding(entity);
        return true;
    }

    public void setItemStack(ItemStack stack) {
        this.itemStack = stack;
        this.modelStack = BASE_MODEL.copy();

        ((DyeableItem) this.modelStack.getItem()).setColor(this.modelStack, SkiesItems.GLIDER.getColor(stack));
    }

    public GliderEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setInvisible(true);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.hasPassengers()) {
            this.dropStack(this.getItemStack());
            this.setItemStack(ItemStack.EMPTY);
            this.discard();
            return;
        }

        var passenger = Objects.requireNonNull(this.getFirstPassenger());

        if ((this.horizontalCollision || this.isOnGround() || passenger.isOnGround()) && this.age > 5) {
            passenger.stopRiding();
            return;
        }

        this.setYaw(MathHelper.lerpAngleDegrees(0.1f, this.getYaw(), passenger.getYaw()));
        this.setPitch(MathHelper.lerpAngleDegrees(0.1f, this.getPitch(), MathHelper.clamp(passenger.getPitch(), -30, 80)));


        var roll = MathHelper.clamp((-MathHelper.subtractAngles(passenger.getYaw(), this.getYaw())) * MathHelper.RADIANS_PER_DEGREE * 0.5f, -1f, 1f);

        if (Math.abs(roll - this.getDataTracker().get(ROLL)) > MathHelper.RADIANS_PER_DEGREE / 2) {
            this.getDataTracker().set(ROLL, roll);
        }

        passenger.limitFallDistance();
        double gravity = 0.07;
        this.limitFallDistance();
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
                passenger.stopRiding();
            }
        } else {
            this.setVelocity(velocity.multiply(0.985F, 0.96F, 0.985F));
        }
        int i = this.damageTimer++ + 1;
        if (i % 10 == 0 && (i / 10) % 2 == 0) {
            if (itemStack.damage(1, this.random, null)) {
                this.setItemStack(ItemStack.EMPTY);
                passenger.stopRiding();
                this.discard();
                return;
            }
            this.emitGameEvent(GameEvent.ELYTRA_GLIDE);
        }

        this.move(MovementType.SELF, this.getVelocity());
        if (this.horizontalCollision && !this.getWorld().isClient) {
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
