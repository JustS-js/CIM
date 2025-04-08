package net.just_s.mixin;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(SmithingTransformRecipe.class)
public class SmithingTransformRecipeMixin {
	@Inject(at = @At("HEAD"), method = "craft*", cancellable = true)
	private void cim$injectCraftData(SmithingRecipeInput smithingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup, CallbackInfoReturnable<ItemStack> cir) {
		if (!smithingRecipeInput.addition().isOf(Items.NAME_TAG)) {
			return;
		}
		ItemStack returnableStack = smithingRecipeInput.base().copy();
		if (returnableStack == null) {
			return;
		}

		ItemStack nameTag = smithingRecipeInput.addition();
		ComponentMap componentMap = nameTag.getComponents();

		ComponentChanges componentChanges;
		Text customName = componentMap.get(DataComponentTypes.CUSTOM_NAME);
		if (customName != null) {
			// Has CustomName -> Adding CustomItemModel
			String customNameAsString = customName.getString();
			componentChanges = modifyComponentChanges(returnableStack, customNameAsString);
		} else {
			// Does not have CustomName -> Removing CustomItemModel
			componentChanges = resetComponentChanges(returnableStack);
		}

		returnableStack.applyChanges(componentChanges);
		cir.setReturnValue(returnableStack);
	}

	@Unique
	private ComponentChanges modifyComponentChanges(ItemStack item, String customModelDataString) {
		CustomModelDataComponent modelComponent = new CustomModelDataComponent(
				List.of(), 						// Float
				List.of(), 						// Boolean
				List.of(customModelDataString),	// String
				List.of()						// Integer
		);
		ComponentChanges.Builder componentBuilder = ComponentChanges.builder();
		componentBuilder = componentBuilder.add(DataComponentTypes.CUSTOM_MODEL_DATA, modelComponent);

		if (item.getDefaultComponents().contains(DataComponentTypes.EQUIPPABLE)) {
			ComponentMap componentMapReturnable = item.getComponents();
			EquippableComponent returnableEquippableComponent = componentMapReturnable.get(DataComponentTypes.EQUIPPABLE);

			EquippableComponent newEquippableComponent = new EquippableComponent(
					returnableEquippableComponent.slot(),
					returnableEquippableComponent.equipSound(),
					Optional.of(RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(customModelDataString))),
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

	@Unique
	private ComponentChanges resetComponentChanges(ItemStack item) {
		ComponentChanges.Builder componentBuilder = ComponentChanges.builder();
		componentBuilder = componentBuilder.remove(DataComponentTypes.CUSTOM_MODEL_DATA);

		if (item.getDefaultComponents().contains(DataComponentTypes.EQUIPPABLE)) {
			EquippableComponent newEquippableComponent = item.getDefaultComponents().get(DataComponentTypes.EQUIPPABLE);
			componentBuilder.add(DataComponentTypes.EQUIPPABLE, newEquippableComponent);
		}

		return componentBuilder.build();
	}
}
