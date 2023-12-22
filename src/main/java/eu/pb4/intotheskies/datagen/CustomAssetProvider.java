package eu.pb4.intotheskies.datagen;

import com.google.common.hash.HashCode;
import eu.pb4.intotheskies.item.SkiesItems;
import eu.pb4.intotheskies.ui.UiResourceCreator;
import eu.pb4.intotheskies.util.WoodUtil;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

class CustomAssetProvider implements DataProvider {
    private final DataOutput output;

    public CustomAssetProvider(FabricDataOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        BiConsumer<String, byte[]> assetWriter = (path, data) -> {
            try {
                writer.write(this.output.getPath().resolve(path), data, HashCode.fromBytes(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        return CompletableFuture.runAsync(() -> {
            UiResourceCreator.generateAssets(assetWriter);

            writeBlocksAndItems(assetWriter);
        }, Util.getMainWorkerExecutor());
    }

    private static final String BASE_MODEL_JSON = """
            {
              "parent": "polydecorations:block/base_|TYPE|",
              "textures": {
                "planks": "|PLANKS|",
                "logs": "|LOG|"
              }
            }
            """;

    private static final String ITEM_MODEL_JSON = """
            {
              "parent": "polydecorations:block/|I|"
            }
            """;

    private void writeBlocksAndItems(BiConsumer<String, byte[]> writer) {
        var t = new StringBuilder();
        SkiesItems.SHELF.forEach((type, item) -> {
            writer.accept("assets/intotheskies/models/block/" + type.name() + "_shelf.json", BASE_MODEL_JSON
                    .replace("|TYPE|", "shelf")
                    .replace("|PLANKS|", "minecraft:block/" + type.name() + "_planks")
                    .replace("|LOG|", "minecraft:block/" + WoodUtil.getLogName(type))
                    .getBytes(StandardCharsets.UTF_8)
            );

            writer.accept("assets/intotheskies/models/item/" + type.name() + "_shelf.json", ITEM_MODEL_JSON
                    .replace("|I|", type.name() + "_shelf")
                    .getBytes(StandardCharsets.UTF_8)
            );
        });

        SkiesItems.BENCH.forEach((type, item) -> {
            writer.accept("assets/intotheskies/models/block/" + type.name() + "_bench.json", BASE_MODEL_JSON
                    .replace("|TYPE|", "bench")
                    .replace("|PLANKS|", "minecraft:block/" + type.name() + "_planks")
                    .replace("|LOG|", "minecraft:block/" + WoodUtil.getLogName(type))
                    .getBytes(StandardCharsets.UTF_8)
            );
            writer.accept("assets/intotheskies/models/block/" + type.name() + "_bench_left.json", BASE_MODEL_JSON
                    .replace("|TYPE|", "bench_left")
                    .replace("|PLANKS|", "minecraft:block/" + type.name() + "_planks")
                    .replace("|LOG|", "minecraft:block/" + WoodUtil.getLogName(type))
                    .getBytes(StandardCharsets.UTF_8)
            );
            writer.accept("assets/intotheskies/models/block/" + type.name() + "_bench_right.json", BASE_MODEL_JSON
                    .replace("|TYPE|", "bench_right")
                    .replace("|PLANKS|", "minecraft:block/" + type.name() + "_planks")
                    .replace("|LOG|", "minecraft:block/" + WoodUtil.getLogName(type))
                    .getBytes(StandardCharsets.UTF_8)
            );
            writer.accept("assets/intotheskies/models/block/" + type.name() + "_bench_middle.json", BASE_MODEL_JSON
                    .replace("|TYPE|", "bench_middle")
                    .replace("|PLANKS|", "minecraft:block/" + type.name() + "_planks")
                    .replace("|LOG|", "minecraft:block/" + WoodUtil.getLogName(type))
                    .getBytes(StandardCharsets.UTF_8)
            );

            writer.accept("assets/intotheskies/models/item/" + type.name() + "_bench.json", ITEM_MODEL_JSON
                    .replace("|I|", type.name() + "_bench")
                    .getBytes(StandardCharsets.UTF_8)
            );

            writer.accept("assets/intotheskies/models/block/" + type.name() + "_sign_post.json", BASE_MODEL_JSON
                    .replace("|TYPE|", "sign_post")
                    .replace("|PLANKS|", "minecraft:entity/signs/" + type.name())
                    .replace("|LOG|", "minecraft:block/" + WoodUtil.getLogName(type))
                    .getBytes(StandardCharsets.UTF_8)
            );

            writer.accept("assets/intotheskies/models/item/" + type.name() + "_sign_post.json", ITEM_MODEL_JSON
                    .replace("|I|", type.name() + "_sign_post")
                    .getBytes(StandardCharsets.UTF_8)
            );
        });
    }

    @Override
    public String getName() {
        return "polydecorations:assets";
    }
}
