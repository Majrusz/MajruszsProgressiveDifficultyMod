package com.majruszsdifficulty.loot_modifiers;

import com.google.gson.JsonObject;
import com.majruszsdifficulty.Instances;
import com.majruszsdifficulty.items.EndShardItem;
import com.majruszsdifficulty.items.EndShardLocatorItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;

/** Removes certain items from loot if they are disabled. */
public class DisableCertainLoot extends LootModifier {
	public DisableCertainLoot( LootItemCondition[] conditions ) {
		super( conditions );
	}

	@Nonnull
	@Override
	public ObjectArrayList< ItemStack > doApply( ObjectArrayList< ItemStack > generatedLoot, LootContext context ) {
		if( Instances.END_SHARD_ORE.isEnabled() )
			return generatedLoot;

		ObjectArrayList< ItemStack > newLoot = new ObjectArrayList<>();
		for( ItemStack loot : generatedLoot ) {
			if( isForbiddenItem( loot.getItem() ) )
				continue;

			newLoot.add( loot );
		}

		return newLoot;
	}

	public static boolean isForbiddenItem( Item item ) {
		return item instanceof EndShardItem || item instanceof EndShardLocatorItem;
	}

	public static class Serializer extends GlobalLootModifierSerializer< DisableCertainLoot > {
		@Override
		public DisableCertainLoot read( ResourceLocation name, JsonObject object, LootItemCondition[] conditions ) {
			return new DisableCertainLoot( conditions );
		}

		@Override
		public JsonObject write( DisableCertainLoot instance ) {
			return null;
		}
	}
}