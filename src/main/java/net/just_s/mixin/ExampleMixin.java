package net.just_s.mixin;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SmithingTransformRecipe.class)
public class ExampleMixin {
	@Inject(at = @At("HEAD"), method = "craft", cancellable = true)
	private void cim$injectCraftData(SmithingRecipeInput smithingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup, CallbackInfoReturnable<ItemStack> cir) {
		if (!smithingRecipeInput.addition().isOf(Items.NAME_TAG)) {
			return;
		}
		ItemStack nameTag = smithingRecipeInput.addition();
		ComponentMap componentMap = nameTag.getComponents();

		Text customName = componentMap.get(DataComponentTypes.CUSTOM_NAME);
		if (customName == null) {
			return;
		}

		String customNameAsString = customName.getString();
		ItemStack returnableStack = smithingRecipeInput.base();

		CustomModelDataComponent modelComponent = new CustomModelDataComponent(
				List.of(), 					// Float
				List.of(), 					// Boolean
				List.of(customNameAsString),// String
				List.of()					// Integer
		);
		ComponentChanges componentChanges = ComponentChanges.builder()
															.add(DataComponentTypes.CUSTOM_MODEL_DATA, modelComponent)
															.build();
		returnableStack.applyChanges(componentChanges);
		cir.setReturnValue(returnableStack);
	}
}
