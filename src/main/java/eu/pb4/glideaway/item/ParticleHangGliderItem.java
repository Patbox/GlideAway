package eu.pb4.glideaway.item;

import eu.pb4.glideaway.entity.GliderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;

public class ParticleHangGliderItem extends HangGliderItem {
    private final ParticleEffect particleEffect;

    public ParticleHangGliderItem(Settings settings, ParticleEffect particleEffect) {
        super(Items.WOODEN_PICKAXE, settings);
        this.particleEffect = particleEffect;
    }

    @Override
    public void tickGlider(ServerWorld world, GliderEntity entity, Entity passenger, ItemStack itemStack) {
        if (entity.age % 2 == 0) {
            world.spawnParticles(this.particleEffect, entity.getX(), entity.getY() + 1.5, entity.getZ(), Math.max((int) (entity.getVelocity().lengthSquared() * 3f), 2), 0.8, 0, 0.8, 0);
        }
    }
}
