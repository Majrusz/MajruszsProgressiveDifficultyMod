package com.majruszsdifficulty.items;

import com.majruszlibrary.events.OnItemTooltip;
import com.majruszlibrary.events.OnPlayerInteracted;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class UndeadBattleStandard extends Item {
	static {
		OnPlayerInteracted.listen( UndeadBattleStandard::tryToSpawn )
			.addCondition( data->data.itemStack.getItem() instanceof UndeadBattleStandard );

		OnItemTooltip.listen( UndeadBattleStandard::addTooltip )
			.addCondition( data->data.itemStack.getItem() instanceof UndeadBattleStandard );
	}

	public UndeadBattleStandard() {
		super( new Properties().stacksTo( 1 ).rarity( Rarity.UNCOMMON ) );
	}

	private static void tryToSpawn( OnPlayerInteracted data ) {
		if( Side.isLogicalServer() && UndeadArmyHelper.tryToSpawn( data.player ) ) {
			ItemHelper.consumeItemOnUse( data.itemStack, data.player );
		}

		data.finish();
	}

	private static void addTooltip( OnItemTooltip data ) {
		data.components.add( TextHelper.translatable( "item.majruszsdifficulty.undead_battle_standard.item_tooltip1" ).withStyle( ChatFormatting.GRAY ) );
		if( !data.isAdvanced() ) {
			return;
		}

		data.components.add( TextHelper.translatable( "item.majruszsdifficulty.undead_battle_standard.item_tooltip2" ).withStyle( ChatFormatting.GRAY ) );
		data.components.add( TextHelper.translatable( "item.majruszsdifficulty.undead_battle_standard.item_tooltip3" ).withStyle( ChatFormatting.GRAY ) );
	}
}
