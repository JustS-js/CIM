package net.just_s.recipe.ingredient;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.just_s.CIMMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

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
	public Stream<RegistryEntry<Item>> getMatchingItems() {
		return Registries.ITEM.stream().filter(i -> i != Items.AIR).map(Registries.ITEM::getEntry);
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
		private static final PacketCodec<RegistryByteBuf, AnyItemIngredient> PACKET_CODEC = PacketCodec.unit(INSTANCE);

		@Override
		public Identifier getIdentifier() {
			return ID;
		}

		@Override
		public MapCodec<AnyItemIngredient> getCodec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, AnyItemIngredient> getPacketCodec() {
			return PACKET_CODEC;
		}
	}
}
