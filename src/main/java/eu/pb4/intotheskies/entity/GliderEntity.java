package eu.pb4.intotheskies.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class GliderEntity extends Entity implements PolymerEntity {
    public static boolean create(World world, BlockPos pos, double yOffset, Direction direction, Entity player) {
        if (!world.getEntitiesByClass(GliderEntity.class, new Box(pos), x -> true).isEmpty()) {
            return false;
        }

        var entity = new GliderEntity(SkiesEntities.GLIDER, world);
        entity.setPosition(pos.getX() + 0.5, pos.getY() + 0.5 + yOffset, pos.getZ() + 0.5);
        entity.setYaw(direction.asRotation());
        world.spawnEntity(entity);
        player.startRiding(entity);
        /*if (MathHelper.angleBetween(direction.asRotation(), player.getYaw()) > 90) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw() - 180, 0,
                        Set.of(PositionFlag.X, PositionFlag.Y, PositionFlag.Z, PositionFlag.X_ROT));
            }
            player.setYaw(player.getYaw() - 180);
        }*/

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
        if (!this.hasPassengers()) {
            this.discard();
        }
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.BLOCK_DISPLAY;
    }

    @Override
    protected void initDataTracker() {}

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
}
