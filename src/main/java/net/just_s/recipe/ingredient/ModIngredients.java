package net.just_s.recipe.ingredient;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

public class ModIngredients {
	public static final CustomIngredientSerializer<AnyItemIngredient> ANY_ITEM_SERIALIZER = new AnyItemIngredient.Serializer();

	public static void init() {
		CustomIngredientSerializer.register(ANY_ITEM_SERIALIZER);
	}
}
