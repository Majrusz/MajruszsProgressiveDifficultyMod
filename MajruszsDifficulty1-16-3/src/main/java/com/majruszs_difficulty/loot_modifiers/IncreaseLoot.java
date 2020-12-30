package com.majruszs_difficulty.loot_modifiers;

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
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class IncreaseLoot extends LootModifier {
	private final double expertModeChance;
	private final double masterModeChance;
	private final List< Item > forbiddenItemsToDuplicate;

	public IncreaseLoot( ILootCondition[] conditions, double expertModeChance, double masterModeChance, List< Item > forbiddenItems ) {
		super( conditions );

		this.expertModeChance = expertModeChance;
		this.masterModeChance = masterModeChance;
		this.forbiddenItemsToDuplicate = forbiddenItems;
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		double chance = getDuplicateBonusChance();
		if( chance <= MajruszsDifficulty.RANDOM.nextDouble() )
			return generatedLoot;

		return doubleLoot( generatedLoot );
	}

	protected double getDuplicateBonusChance() {
		switch( GameState.getCurrentMode() ) {
			default:
				return 0.0;
			case EXPERT:
				return this.expertModeChance;
			case MASTER:
				return this.masterModeChance;
		}
	}

	protected List< ItemStack > doubleLoot( List< ItemStack > generatedLoot ) {
		List< ItemStack > doubledLoot = new ArrayList<>();
		for( ItemStack itemStack : generatedLoot ) {
			boolean isItemForbidden = isForbidden( itemStack.getItem() );

			for( int i = 0; i < 2 && ( i < 1 || !isItemForbidden ); i++ )
				doubledLoot.add( itemStack );
		}
		return doubledLoot;
	}

	protected boolean isForbidden( Item item ) {
		for( Item forbidden : this.forbiddenItemsToDuplicate )
			if( forbidden.equals( item ) )
				return true;

		return false;
	}

	public static class Serializer extends GlobalLootModifierSerializer< IncreaseLoot > {
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