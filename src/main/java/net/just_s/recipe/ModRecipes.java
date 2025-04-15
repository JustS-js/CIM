package net.just_s.recipe;

import net.just_s.CIMMod;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModRecipes {
	public static final RecipeSerializer<CustomItemModelSmithingRecipe> CUSTOM_ITEM_MODEL_SMITHING_SERIALIZER = new CustomItemModelSmithingRecipe.Serializer();

	public static void init() {
		Registry.register(Registries.RECIPE_SERIALIZER, CIMMod.id("smithing"), CUSTOM_ITEM_MODEL_SMITHING_SERIALIZER);
	}

	public static <B, T> PacketCodec<B, T> deprecatedRecipePacketCodec() {
		return PacketCodec.of((o1, o2) -> {
			throw new IllegalStateException("Recipe packet codecs are deprecated");
		}, o -> {
			throw new IllegalStateException("Recipe packet codecs are deprecated");
		});
	}
}
