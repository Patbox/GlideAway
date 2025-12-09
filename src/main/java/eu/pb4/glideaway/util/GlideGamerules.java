package eu.pb4.glideaway.util;


import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public class GlideGamerules {
    public static final GameRule<Boolean> PICK_HANG_GLIDER = GameRuleBuilder.forBoolean(false).category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.parse("glideaway:pick_hang_glider"));

    public static final GameRule<Boolean> WIND_SOUND = GameRuleBuilder.forBoolean(true).category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.parse("glideaway:wind_sound"));

    public static final GameRule<Boolean> ALLOW_SNEAK_RELEASE = GameRuleBuilder.forBoolean(true).category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.parse("glideaway:allow_sneak_release"));

    public static final GameRule<Boolean> ALLOW_FIREWORK_BOOST = GameRuleBuilder.forBoolean(false).category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.parse("glideaway:allow_firework_boost"));

    public static final GameRule<Double> INITIAL_VELOCITY_GLIDER_DAMAGE = GameRuleBuilder.forDouble(50)
            .minValue(0d).category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.parse("glideaway:initial_velocity_glider_damage"));

    public static final GameRule<Double> FIRE_BOOST = GameRuleBuilder.forDouble(0.06)
            .range(0d, 1d).category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.parse("glideaway:fire_velocity_boost"));

    public static final GameRule<Double> LAVA_BOOST = GameRuleBuilder.forDouble(0.02)
            .range(0d, 1d).category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.parse("glideaway:lava_velocity_boost"));

    public static void register() {}
}
