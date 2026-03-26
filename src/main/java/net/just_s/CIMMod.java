package net.just_s;

import net.fabricmc.api.ModInitializer;
import net.just_s.recipe.ModRecipes;
import net.just_s.recipe.ingredient.ModIngredients;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CIMMod implements ModInitializer {
	public static final String MOD_ID = "cim";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModIngredients.init();
		ModRecipes.init();
		LOGGER.info("Custom Item Models loaded!");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
