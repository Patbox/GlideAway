package eu.pb4.glideaway.datagen;

import com.google.common.hash.HashCode;
import eu.pb4.glideaway.item.GlideItems;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.ConstantTintSource;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.DyeTintSource;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registries;
import net.minecraft.util.Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static eu.pb4.glideaway.ModInit.id;

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
            writeData(assetWriter);
        }, Util.getMainWorkerExecutor());
    }

    private void writeData(BiConsumer<String, byte[]> writer) {
        for (var item : List.of(GlideItems.AZALEA_HANG_GLIDER, GlideItems.SCULK_HANG_GLIDER, GlideItems.CHERRY_HANG_GLIDER,
                GlideItems.WIND_IN_A_BOTTLE, GlideItems.INFINITE_WIND_IN_A_BOTTLE)) {
            var id = Registries.ITEM.getId(item);

            writer.accept(AssetPaths.itemAsset(id), new ItemAsset(
                    new BasicItemModel(id.withPrefixedPath("item/")),
                    ItemAsset.Properties.DEFAULT).toJson().getBytes(StandardCharsets.UTF_8));
        }

        writer.accept(AssetPaths.itemAsset(id("hang_glider")), new ItemAsset(
                new BasicItemModel(id("item/hang_glider"), List.of(new ConstantTintSource(0xFFFFFF), new DyeTintSource(0xFFFFFFFF))),
                ItemAsset.Properties.DEFAULT).toJson().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getName() {
        return "intotheskies:assets";
    }
}
