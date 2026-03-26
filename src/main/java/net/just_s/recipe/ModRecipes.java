package net.just_s.recipe;

import net.just_s.CIMMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipes {
	public static final RecipeSerializer<CustomItemModelSmithingRecipe> CUSTOM_ITEM_MODEL_SMITHING_SERIALIZER = new RecipeSerializer<>(
			CustomItemModelSmithingRecipe.CODEC,
			deprecatedRecipeStreamCodec()
	);

	public static void init() {
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CIMMod.id("smithing"), CUSTOM_ITEM_MODEL_SMITHING_SERIALIZER);
	}

	public static <B, T> StreamCodec<B, T> deprecatedRecipeStreamCodec() {
		return StreamCodec.ofMember((_, _) -> {
			throw new IllegalStateException("Recipe stream codecs are deprecated");
		}, _ -> {
			throw new IllegalStateException("Recipe stream codecs are deprecated");
		});
	}
}
