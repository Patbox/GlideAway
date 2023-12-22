package eu.pb4.intotheskies.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class GliderEntity extends Entity implements PolymerEntity {
    public static boolean create(World world, Entity player, ItemStack stack) {
        if (player.hasVehicle()) {
            return false;
        }

        var entity = new GliderEntity(SkiesEntities.GLIDER, world);
        var sitting = player.getDimensions(EntityPose.SITTING);
        var currentDim = player.getDimensions(player.getPose());
        entity.setPosition(player.getPos().add(0, currentDim.height - sitting.height, 0));
        entity.setYaw(player.getYaw());
        world.spawnEntity(entity);
        player.startRiding(entity);

        return true;
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
        if (!this.hasPassengers() || this.isOnGround()) {
            this.discard();
            return;
        }

        this.setYaw(Objects.requireNonNull(this.getFirstPassenger()).getYaw());
        this.setPitch(Math.max(Objects.requireNonNull(this.getFirstPassenger()).getPitch(), 0));
        this.setVelocity(Vec3d.fromPolar(this.getPitch(), this.getYaw()).multiply(0.5));
        this.move(MovementType.SELF, this.getVelocity());
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        if (initial) {
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.TELEPORTATION_DURATION, 3));
        }
    }

    @Override
    protected void initDataTracker() {}

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
}
