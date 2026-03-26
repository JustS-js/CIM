package net.just_s.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.just_s.CIMMod;
import net.just_s.recipe.ingredient.AnyItemIngredient;
import net.minecraft.Optionull;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CustomItemModelSmithingRecipe implements SmithingRecipe {
	private final Ingredient addition;

	public CustomItemModelSmithingRecipe(Ingredient addition) {
		this.addition = addition;
	}

	@Override
	public boolean matches(SmithingRecipeInput input, Level world) {
		return input.template().isEmpty()
				&& !input.base().isEmpty()
				&& addition.test(input.addition());
	}

	@Override
	public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
		return craft(input.base(), Optionull.map(input.addition().get(DataComponents.CUSTOM_NAME), Component::getString));
	}

	@Override
	public RecipeSerializer<CustomItemModelSmithingRecipe> getSerializer() {
		return ModRecipes.CUSTOM_ITEM_MODEL_SMITHING_SERIALIZER;
	}

	@Override
	public PlacementInfo placementInfo() {
		return PlacementInfo.NOT_PLACEABLE;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public Optional<Ingredient> templateIngredient() {
		return Optional.empty();
	}

	@Override
	public Ingredient baseIngredient() {
		return AnyItemIngredient.INSTANCE.toVanilla();
	}

	@Override
	public Optional<Ingredient> additionIngredient() {
		return Optional.of(addition);
	}

	public static ItemStack craft(ItemStack input, @Nullable String customModelText) {
		ItemStack result = input.copyWithCount(1);
		DataComponentPatch componentChanges;
		if (customModelText != null) {
			// Has CustomName -> Adding CustomItemModel
			componentChanges = modifyComponentChanges(result, customModelText);
		} else {
			// Does not have CustomName -> Removing CustomItemModel
			componentChanges = resetComponentChanges(result);
		}
		result.applyComponentsAndValidate(componentChanges);
		return result;
	}

	private static DataComponentPatch modifyComponentChanges(ItemStack item, String customModelDataString) {
		CustomModelData modelComponent = new CustomModelData(
				List.of(), 						// Float
				List.of(), 						// Boolean
				List.of(customModelDataString),	// String
				List.of()						// Integer
		);
		DataComponentPatch.Builder componentBuilder = DataComponentPatch.builder();
		componentBuilder = componentBuilder.set(DataComponents.CUSTOM_MODEL_DATA, modelComponent);

		if (ResourceLocation.isValidPath(customModelDataString) && item.getPrototype().has(DataComponents.EQUIPPABLE) && shouldApplyEquippable(item)) {
			DataComponentMap componentMapReturnable = item.getComponents();
			Equippable returnableEquippableComponent = componentMapReturnable.get(DataComponents.EQUIPPABLE);

			Equippable newEquippableComponent = new Equippable(
					returnableEquippableComponent.slot(),
					returnableEquippableComponent.equipSound(),
					Optional.of(ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(CIMMod.MOD_ID, customModelDataString))),
					returnableEquippableComponent.cameraOverlay(),
					returnableEquippableComponent.allowedEntities(),
					returnableEquippableComponent.dispensable(),
					returnableEquippableComponent.swappable(),
					returnableEquippableComponent.damageOnHurt(),
					returnableEquippableComponent.equipOnInteract()
			);
			componentBuilder.set(DataComponents.EQUIPPABLE, newEquippableComponent);
		}
		return componentBuilder.build();
	}

	private static DataComponentPatch resetComponentChanges(ItemStack item) {
		DataComponentPatch.Builder componentBuilder = DataComponentPatch.builder()
				.remove(DataComponents.CUSTOM_MODEL_DATA);

		if (item.getPrototype().has(DataComponents.EQUIPPABLE) && shouldApplyEquippable(item)) {
			Equippable newEquippableComponent = item.getPrototype().get(DataComponents.EQUIPPABLE);
			componentBuilder.set(DataComponents.EQUIPPABLE, newEquippableComponent);
		}

		return componentBuilder.build();
	}

	private static boolean shouldApplyEquippable(ItemStack item) {
		// armor, elytra
		return item.is(ItemTags.EQUIPPABLE_ENCHANTABLE) && !item.is(Items.CARVED_PUMPKIN) && !item.is(ItemTags.SKULLS);
	}

	public static class Serializer implements RecipeSerializer<CustomItemModelSmithingRecipe> {
		private static final MapCodec<CustomItemModelSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Ingredient.CODEC.fieldOf("addition").forGetter(r -> r.addition)
		).apply(instance, CustomItemModelSmithingRecipe::new));
		private static final StreamCodec<RegistryFriendlyByteBuf, CustomItemModelSmithingRecipe> PACKET_CODEC = ModRecipes.deprecatedRecipePacketCodec();

		@Override
		public MapCodec<CustomItemModelSmithingRecipe> codec() {
			return CODEC;
		}

		@Override
		@Deprecated
		public StreamCodec<RegistryFriendlyByteBuf, CustomItemModelSmithingRecipe> streamCodec() {
			return PACKET_CODEC;
		}
	}
}
