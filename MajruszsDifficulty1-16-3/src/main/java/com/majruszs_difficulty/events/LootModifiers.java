package com.majruszs_difficulty.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsDifficulty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber( modid = MajruszsDifficulty.MOD_ID, bus = EventBusSubscriber.Bus.MOD )
public class LootModifiers {
	@SubscribeEvent
	public static void registerModifierSerializers( final RegistryEvent.Register< GlobalLootModifierSerializer< ? > > event ) {
		event.getRegistry()
			.register( new IncreaseLoot.Serializer().setRegistryName( new ResourceLocation( MajruszsDifficulty.MOD_ID, "increase_loot" ) ) );
	}

	private static class IncreaseLoot extends LootModifier {
		private final double expertModeChance;
		private final double masterModeChance;
		private final List< Item > forbiddenItemsToDuplicate;

		public IncreaseLoot( ILootCondition[] conditions, double expertModeChance, double masterModeChance, List< Item > forbiddenItemsToDuplicate
		) {
			super( conditions );

			this.expertModeChance = expertModeChance;
			this.masterModeChance = masterModeChance;
			this.forbiddenItemsToDuplicate = forbiddenItemsToDuplicate;
		}

		@Nonnull
		@Override
		public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
			double chance;
			switch( GameState.getCurrentMode() ) {
				default:
					return generatedLoot;
				case EXPERT:
					chance = this.expertModeChance;
					break;
				case MASTER:
					chance = this.masterModeChance;
					break;
			}

			if( chance < MajruszsDifficulty.RANDOM.nextDouble() )
				return generatedLoot;

			List< ItemStack > doubleLoot = new ArrayList<>();
			for( ItemStack stack : generatedLoot ) {
				boolean isItemForbidden = false;
				for( Item forbidden : this.forbiddenItemsToDuplicate )
					if( forbidden.equals( stack.getItem() ) ) {
						isItemForbidden = true;
						break;
					}

				for( int i = 0; i < 2 && ( i < 1 || !isItemForbidden ); i++ )
					doubleLoot.add( stack );
			}

			return doubleLoot;
		}

		private static class Serializer extends GlobalLootModifierSerializer< IncreaseLoot > {
			@Override
			public IncreaseLoot read( ResourceLocation name, JsonObject object, ILootCondition[] conditions ) {
				double expertModeChance = JSONUtils.getFloat( object, "expert_chance" );
				double masterModeChance = JSONUtils.getFloat( object, "master_chance" );
				JsonArray items = JSONUtils.getJsonArray( object, "forbidden_items" );

				List< Item > forbiddenItemsToDuplicate = new ArrayList<>();
				for( int i = 0; i < items.size(); i++ ) {
					JsonObject item = items.get( i )
						.getAsJsonObject();

					forbiddenItemsToDuplicate.add( ForgeRegistries.ITEMS.getValue( new ResourceLocation( item.get( "name" )
						.getAsString() ) ) );
				}

				return new IncreaseLoot( conditions, expertModeChance, masterModeChance, forbiddenItemsToDuplicate );
			}

			@Override
			public JsonObject write( IncreaseLoot instance ) {
				return null;
			}
		}
	}
}
