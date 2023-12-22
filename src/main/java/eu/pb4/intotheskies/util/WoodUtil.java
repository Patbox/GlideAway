package eu.pb4.intotheskies.util;

import net.minecraft.block.WoodType;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class WoodUtil {
    public static boolean isWood(WoodType type) {
        return !isHyphae(type) && !isBlock(type);
    }

    public static boolean isHyphae(WoodType type) {
        return type == WoodType.CRIMSON || type == WoodType.WARPED;
    }

    public static boolean isBlock(WoodType type) {
        return type == WoodType.BAMBOO;
    }


    public static String getLogName(WoodType type) {
        if (isBlock(type)) {
            return type.name() + "_block";
        }

        if (isHyphae(type)) {
            return type.name() + "_stem";
        }

        return type.name() + "_log";
    }

    public static <T> void forEach(Map<WoodType, T> map, Consumer<T> consumer) {
        WoodType.stream().forEach(x -> {
            var y = map.get(x);
            if (y != null) {
                consumer.accept(y);
            }
        });
    }

    public static <T> void forEach(List<Map<WoodType, ?>> list, Consumer<T> consumer) {
        WoodType.stream().forEach(x -> {
            for (var map : list) {
                var y = map.get(x);
                if (y != null) {
                    consumer.accept((T) y);
                }
            }
        });
    }
}
