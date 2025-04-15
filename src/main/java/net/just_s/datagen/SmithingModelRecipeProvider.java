package net.just_s.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.just_s.recipe.CustomItemModelSmithingRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SmithingModelRecipeProvider extends FabricRecipeProvider {
	SmithingModelRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
		return new RecipeGenerator(wrapperLookup, recipeExporter) {
			@Override
			public void generate() {
				offerNameTagCustomModelRecipe();
			}

			private void offerNameTagCustomModelRecipe() {
				RegistryKey<Recipe<?>> recipeKey = RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("custom_model_recipe"));

				Advancement.Builder builder = exporter.getAdvancementBuilder()
						.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
						.rewards(AdvancementRewards.Builder.recipe(recipeKey))
						.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
				Objects.requireNonNull(builder);
				CustomItemModelSmithingRecipe smithingTransformRecipe = new CustomItemModelSmithingRecipe(Ingredient.ofItem(Items.NAME_TAG));
				exporter.accept(recipeKey, smithingTransformRecipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + RecipeCategory.MISC.getName() + "/")));
			}
		};
	}

	@Override
	public String getName() {
		return "SmithingModelRecipeProvider";
	}
}
