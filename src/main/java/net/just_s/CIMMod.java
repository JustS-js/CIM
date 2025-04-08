package net.just_s;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CIMMod implements DedicatedServerModInitializer {
	public static final String MOD_ID = "cim";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	//public static SmithingModelRecipe.Serializer SMITHING_MODEL;

	@Override
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		/*SMITHING_MODEL = Registry.register(
				Registries.RECIPE_SERIALIZER,
				Identifier.of(MOD_ID, "smithing_model"),
				new SmithingModelRecipe.Serializer()
		);*/
		LOGGER.info("Hello Fabric world!");
	}
}
