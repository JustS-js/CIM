package net.just_s.recipe.ingredient;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.just_s.CIMMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.stream.Stream;

// We can use this just on server because recipes` ingredients are not directly synced with clients
public class AnyItemIngredient implements CustomIngredient {
	public static final AnyItemIngredient INSTANCE = new AnyItemIngredient();

	private AnyItemIngredient() {
	}

	@Override
	public boolean test(ItemStack stack) {
		return !stack.isEmpty();
	}

	// Required for ability to place items in smithing slots
	@Override
	public Stream<Holder<Item>> items() {
		return BuiltInRegistries.ITEM.stream().filter(i -> i != Items.AIR).map(BuiltInRegistries.ITEM::wrapAsHolder);
	}

	// Faster than matching in the list of all items
	@Override
	public boolean requiresTesting() {
		return true;
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return ModIngredients.ANY_ITEM_SERIALIZER;
	}

	public static class Serializer implements CustomIngredientSerializer<AnyItemIngredient> {
		public static final Identifier ID = CIMMod.id("any_item");
		private static final MapCodec<AnyItemIngredient> CODEC = MapCodec.unit(INSTANCE);
		private static final StreamCodec<RegistryFriendlyByteBuf, AnyItemIngredient> PACKET_CODEC = StreamCodec.unit(INSTANCE);

		@Override
		public Identifier getIdentifier() {
			return ID;
		}

		@Override
		public MapCodec<AnyItemIngredient> getCodec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, AnyItemIngredient> getStreamCodec() {
			return PACKET_CODEC;
		}
	}
}
