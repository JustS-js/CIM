package net.just_s.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.just_s.CIMMod;
import net.just_s.recipe.ingredient.AnyItemIngredient;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CustomItemModelSmithingRecipe implements SmithingRecipe {
	private final Ingredient addition;

	public CustomItemModelSmithingRecipe(Ingredient addition) {
		this.addition = addition;
	}

	@Override
	public boolean matches(SmithingRecipeInput input, World world) {
		return input.template().isEmpty()
				&& !input.base().isEmpty()
				&& addition.test(input.addition());
	}

	@Override
	public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
		return craft(input.base(), Nullables.map(input.addition().get(DataComponentTypes.CUSTOM_NAME), Text::getString));
	}

	@Override
	public RecipeSerializer<CustomItemModelSmithingRecipe> getSerializer() {
		return ModRecipes.CUSTOM_ITEM_MODEL_SMITHING_SERIALIZER;
	}

	@Override
	public IngredientPlacement getIngredientPlacement() {
		return IngredientPlacement.NONE;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	@Override
	public Optional<Ingredient> template() {
		return Optional.empty();
	}

	@Override
	public Optional<Ingredient> base() {
		return Optional.of(AnyItemIngredient.INSTANCE.toVanilla());
	}

	@Override
	public Optional<Ingredient> addition() {
		return Optional.of(addition);
	}

	public static ItemStack craft(ItemStack input, @Nullable String customModelText) {
		ItemStack result = input.copyWithCount(1);
		ComponentChanges componentChanges;
		if (customModelText != null) {
			// Has CustomName -> Adding CustomItemModel
			componentChanges = modifyComponentChanges(result, customModelText);
		} else {
			// Does not have CustomName -> Removing CustomItemModel
			componentChanges = resetComponentChanges(result);
		}
		result.applyChanges(componentChanges);
		return result;
	}

	private static ComponentChanges modifyComponentChanges(ItemStack item, String customModelDataString) {
		CustomModelDataComponent modelComponent = new CustomModelDataComponent(
				List.of(), 						// Float
				List.of(), 						// Boolean
				List.of(customModelDataString),	// String
				List.of()						// Integer
		);
		ComponentChanges.Builder componentBuilder = ComponentChanges.builder();
		componentBuilder = componentBuilder.add(DataComponentTypes.CUSTOM_MODEL_DATA, modelComponent);

		if (Identifier.isPathValid(customModelDataString) && item.getDefaultComponents().contains(DataComponentTypes.EQUIPPABLE) && shouldApplyEquippable(item)) {
			ComponentMap componentMapReturnable = item.getComponents();
			EquippableComponent returnableEquippableComponent = componentMapReturnable.get(DataComponentTypes.EQUIPPABLE);

			EquippableComponent newEquippableComponent = new EquippableComponent(
					returnableEquippableComponent.slot(),
					returnableEquippableComponent.equipSound(),
					Optional.of(RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(CIMMod.MOD_ID, customModelDataString))),
					returnableEquippableComponent.cameraOverlay(),
					returnableEquippableComponent.allowedEntities(),
					returnableEquippableComponent.dispensable(),
					returnableEquippableComponent.swappable(),
					returnableEquippableComponent.damageOnHurt()
			);
			componentBuilder.add(DataComponentTypes.EQUIPPABLE, newEquippableComponent);
		}
		return componentBuilder.build();
	}

	private static ComponentChanges resetComponentChanges(ItemStack item) {
		ComponentChanges.Builder componentBuilder = ComponentChanges.builder()
				.remove(DataComponentTypes.CUSTOM_MODEL_DATA);

		if (item.getDefaultComponents().contains(DataComponentTypes.EQUIPPABLE) && shouldApplyEquippable(item)) {
			EquippableComponent newEquippableComponent = item.getDefaultComponents().get(DataComponentTypes.EQUIPPABLE);
			componentBuilder.add(DataComponentTypes.EQUIPPABLE, newEquippableComponent);
		}

		return componentBuilder.build();
	}

	private static boolean shouldApplyEquippable(ItemStack item) {
		// armor, elytra
		return item.isIn(ItemTags.EQUIPPABLE_ENCHANTABLE) && !item.isOf(Items.CARVED_PUMPKIN) && !item.isIn(ItemTags.SKULLS);
	}

	public static class Serializer implements RecipeSerializer<CustomItemModelSmithingRecipe> {
		private static final MapCodec<CustomItemModelSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Ingredient.CODEC.fieldOf("addition").forGetter(r -> r.addition)
		).apply(instance, CustomItemModelSmithingRecipe::new));
		private static final PacketCodec<RegistryByteBuf, CustomItemModelSmithingRecipe> PACKET_CODEC = ModRecipes.deprecatedRecipePacketCodec();

		@Override
		public MapCodec<CustomItemModelSmithingRecipe> codec() {
			return CODEC;
		}

		@Override
		@Deprecated
		public PacketCodec<RegistryByteBuf, CustomItemModelSmithingRecipe> packetCodec() {
			return PACKET_CODEC;
		}
	}
}
