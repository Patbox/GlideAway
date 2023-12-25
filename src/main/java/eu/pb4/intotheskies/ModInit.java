package eu.pb4.intotheskies;

import eu.pb4.factorytools.impl.DebugData;

import eu.pb4.intotheskies.entity.GliderEntity;
import eu.pb4.intotheskies.entity.SkiesEntities;
import eu.pb4.intotheskies.polydex.PolydexCompat;
import eu.pb4.intotheskies.recipe.SkiesRecipeSerializers;
import eu.pb4.intotheskies.recipe.SkiesRecipeTypes;
import eu.pb4.intotheskies.ui.GuiTextures;
import eu.pb4.intotheskies.ui.UiResourceCreator;
import eu.pb4.intotheskies.util.*;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.intotheskies.block.SkiesBlockEntities;
import eu.pb4.intotheskies.block.SkiesBlocks;
import eu.pb4.intotheskies.item.SkiesItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModInit implements ModInitializer {
	public static final String ID = "intotheskies";
	public static final String VERSION = FabricLoader.getInstance().getModContainer(ID).get().getMetadata().getVersion().getFriendlyString();
	public static final Logger LOGGER = LoggerFactory.getLogger("Into The Skies");
    public static final boolean DEV_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();
    public static final boolean DEV_MODE = VERSION.contains("-dev.") || DEV_ENV;
    @SuppressWarnings("PointlessBooleanExpression")
	public static final boolean DYNAMIC_ASSETS = true && DEV_ENV;

    public static Identifier id(String path) {
		return new Identifier(ID, path);
	}

	@Override
	public void onInitialize() {
		if (VERSION.contains("-dev.")) {
			LOGGER.warn("=====================================================");
			LOGGER.warn("You are using development version of Into The Skies!");
			LOGGER.warn("Support is limited, as features might be unfinished!");
			LOGGER.warn("You are on your own!");
			LOGGER.warn("=====================================================");
		}

		SkiesBlocks.register();
		SkiesBlockEntities.register();
		SkiesItems.register();
		SkiesEntities.register();
		SkiesRecipeTypes.register();
		SkiesRecipeSerializers.register();
		SkiesUtils.register();
		DebugData.register();

		initModels();
		UiResourceCreator.setup();
		GuiTextures.register();
		PolydexCompat.register();
		PolymerResourcePackUtils.addModAssets(ID);
		PolymerResourcePackUtils.markAsRequired();

	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void initModels() {
		GliderEntity.BASE_MODEL.isEmpty();
	}
}
