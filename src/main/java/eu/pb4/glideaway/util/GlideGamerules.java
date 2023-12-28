package eu.pb4.glideaway.util;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.world.GameRules;

public class GlideGamerules {
    public static final GameRules.Key<GameRules.BooleanRule> PICK_HANG_GLIDER = GameRuleRegistry.register("glideaway:pick_hang_glider", GameRules.Category.MISC,
            GameRuleFactory.createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanRule> WIND_SOUND = GameRuleRegistry.register("glideaway:wind_sound", GameRules.Category.MISC,
            GameRuleFactory.createBooleanRule(true));

    public static final GameRules.Key<GameRules.BooleanRule> ALLOW_SNEAK_RELEASE = GameRuleRegistry.register("glideaway:allow_sneak_release", GameRules.Category.MISC,
            GameRuleFactory.createBooleanRule(true));


    public static final GameRules.Key<DoubleRule> INITIAL_VELOCITY_GLIDER_DAMAGE = GameRuleRegistry.register("glideaway:initial_velocity_glider_damage", GameRules.Category.MISC,
            GameRuleFactory.createDoubleRule(50, 0));

    public static final GameRules.Key<DoubleRule> FIRE_BOOST = GameRuleRegistry.register("glideaway:fire_velocity_boost", GameRules.Category.MISC,
            GameRuleFactory.createDoubleRule(0.04, 0, 1));

    public static final GameRules.Key<DoubleRule> LAVA_BOOST = GameRuleRegistry.register("glideaway:lava_velocity_boost", GameRules.Category.MISC,
            GameRuleFactory.createDoubleRule(0.01, 0, 1));
    public static void register() {}
}
