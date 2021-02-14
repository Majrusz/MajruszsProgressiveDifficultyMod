package com.majruszs_difficulty.loot_modifiers;

import com.google.gson.JsonObject;
import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.items.EndShardItem;
import com.majruszs_difficulty.items.EndShardLocatorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/** Removes certain items from loot if they are disabled. */
public class DisableCertainLoot extends LootModifier {
	public DisableCertainLoot( ILootCondition[] conditions ) {
		super( conditions );
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		if( Instances.END_SHARD_ORE.isEnabled() )
			return generatedLoot;

		List< ItemStack > newLoot = new ArrayList<>();
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
		public DisableCertainLoot read( ResourceLocation name, JsonObject object, ILootCondition[] conditions ) {
			return new DisableCertainLoot( conditions );
		}

		@Override
		public JsonObject write( DisableCertainLoot instance ) {
			return null;
		}
	}
}