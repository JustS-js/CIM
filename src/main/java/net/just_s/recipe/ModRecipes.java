package net.just_s.recipe;

import net.just_s.CIMMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipes {
	public static final RecipeSerializer<CustomItemModelSmithingRecipe> CUSTOM_ITEM_MODEL_SMITHING_SERIALIZER = new CustomItemModelSmithingRecipe.Serializer();

	public static void init() {
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CIMMod.id("smithing"), CUSTOM_ITEM_MODEL_SMITHING_SERIALIZER);
	}

	public static <B, T> StreamCodec<B, T> deprecatedRecipePacketCodec() {
		return StreamCodec.ofMember((o1, o2) -> {
			throw new IllegalStateException("Recipe packet codecs are deprecated");
		}, o -> {
			throw new IllegalStateException("Recipe packet codecs are deprecated");
		});
	}
}
