package net.just_s;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SmithingModelRecipe implements SmithingRecipe {
    final Optional<Ingredient> template;
    final Optional<Ingredient> base;
    final Optional<Ingredient> addition;
    @Nullable
    private IngredientPlacement ingredientPlacement;

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return null;
    }

    @Override
    public RecipeSerializer<? extends SmithingRecipe> getSerializer() {
        return ExampleMod;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return null;
    }

    @Override
    public Optional<Ingredient> template() {
        return Optional.empty();
    }

    @Override
    public Optional<Ingredient> base() {
        return Optional.empty();
    }

    @Override
    public Optional<Ingredient> addition() {
        return Optional.empty();
    }

    public static class Serializer implements RecipeSerializer<SmithingTrimRecipe> {
        private static final MapCodec<SmithingTrimRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Ingredient.CODEC.optionalFieldOf("template").forGetter((recipe) -> recipe.template), Ingredient.CODEC.optionalFieldOf("base").forGetter((recipe) -> recipe.base), Ingredient.CODEC.optionalFieldOf("addition").forGetter((recipe) -> recipe.addition)).apply(instance, SmithingTrimRecipe::new));
        public static final PacketCodec<RegistryByteBuf, SmithingTrimRecipe> PACKET_CODEC;

        public Serializer() {
        }

        public MapCodec<SmithingTrimRecipe> codec() {
            return CODEC;
        }

        public PacketCodec<RegistryByteBuf, SmithingTrimRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        static {
            PACKET_CODEC = PacketCodec.tuple(Ingredient.OPTIONAL_PACKET_CODEC, (recipe) -> recipe.template, Ingredient.OPTIONAL_PACKET_CODEC, (recipe) -> recipe.base, Ingredient.OPTIONAL_PACKET_CODEC, (recipe) -> recipe.addition, SmithingTrimRecipe::new);
        }
    }
}
