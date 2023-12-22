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
              "parent": "intotheskies:block/base_|TYPE|",
              "textures": {
                "planks": "|PLANKS|",
                "logs": "|LOG|"
              }
            }
            """;

    private static final String ITEM_MODEL_JSON = """
            {
              "parent": "intotheskies:block/|I|"
            }
            """;

    private void writeBlocksAndItems(BiConsumer<String, byte[]> writer) {

    }

    @Override
    public String getName() {
        return "intotheskies:assets";
    }
}
