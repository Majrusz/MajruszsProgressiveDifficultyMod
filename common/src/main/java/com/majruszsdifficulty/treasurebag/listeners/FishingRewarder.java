package com.majruszsdifficulty.treasurebag.listeners;

import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnItemFished;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.ItemHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.item.ItemStack;

public class FishingRewarder {
	public static GameStageValue< Integer > FISH_REQUIREMENT = GameStageValue.of(
		DefaultMap.defaultEntry( 20 ),
		DefaultMap.entry( GameStage.EXPERT_ID, 15 ),
		DefaultMap.entry( GameStage.MASTER_ID, 10 )
	);

	static {
		OnItemFished.listen( FishingRewarder::updateItemsFished )
			.addCondition( Condition.isLogicalServer() );

		Serializables.get( FishingInfo.class )
			.define( "TreasureBagFishesLeft", Reader.integer(), s->s.fishesLeft, ( s, v )->s.fishesLeft = v );
	}

	private static void updateItemsFished( OnItemFished data ) {
		Serializables.modify( new FishingInfo(), EntityHelper.getOrCreateExtraTag( data.player ), fishingInfo->{
			if( fishingInfo.fishesLeft <= 0 ) {
				fishingInfo.fishesLeft = FISH_REQUIREMENT.get( GameStageHelper.determineGameStage( data.player ) );
			}

			--fishingInfo.fishesLeft;
			if( fishingInfo.fishesLeft == 0 ) {
				ItemHelper.giveToPlayer( new ItemStack( MajruszsDifficulty.Items.ANGLER_TREASURE_BAG.get() ), data.player );
			}
		} );
	}

	private static class FishingInfo {
		public int fishesLeft = 0;
	}
}
