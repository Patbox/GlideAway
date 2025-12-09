package eu.pb4.glideaway.datagen;

import com.google.common.hash.HashCode;
import eu.pb4.glideaway.item.GlideItems;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.ConstantTintSource;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.DyeTintSource;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static eu.pb4.glideaway.ModInit.id;

class CustomAssetProvider implements DataProvider {
    private final PackOutput output;

    public CustomAssetProvider(FabricDataOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        BiConsumer<String, byte[]> assetWriter = (path, data) -> {
            try {
                writer.writeIfNeeded(this.output.getOutputFolder().resolve(path), data, HashCode.fromBytes(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        return CompletableFuture.runAsync(() -> {
            writeData(assetWriter);
        }, Util.backgroundExecutor());
    }

    private void writeData(BiConsumer<String, byte[]> writer) {
        for (var item : List.of(GlideItems.AZALEA_HANG_GLIDER, GlideItems.TATER_HANG_GLIDER, GlideItems.PHANTOM_HANG_GLIDER, GlideItems.SCULK_HANG_GLIDER, GlideItems.CHERRY_HANG_GLIDER,
                GlideItems.WIND_IN_A_BOTTLE, GlideItems.INFINITE_WIND_IN_A_BOTTLE)) {
            var id = BuiltInRegistries.ITEM.getKey(item);

            writer.accept(AssetPaths.itemAsset(id), new ItemAsset(
                    new BasicItemModel(id.withPrefix("item/")),
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
