package com.majruszs_difficulty.events.treasure_bag;

import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class UpdatePlayerList {
	@SubscribeEvent
	public static void onHit( LivingHurtEvent event ) {
		PlayerEntity player = MajruszsHelper.getPlayerFromDamageSource( event.getSource() );
		LivingEntity target = event.getEntityLiving();

		if( player == null || !TreasureBagManager.hasTreasureBag( target.getType() ) )
			return;

		ListNBT listNBT = getOrCreateList( target );
		CompoundNBT playerNBT = getPlayerCompound( player );

		if( !isPlayerInList( player, listNBT ) )
			listNBT.add( playerNBT );

		CompoundNBT data = target.getPersistentData();
		data.put( TreasureBagManager.TREASURE_BAG_TAG, listNBT );
	}

	protected static ListNBT getOrCreateList( LivingEntity entity ) {
		CompoundNBT data = entity.getPersistentData();

		return ( data.contains( TreasureBagManager.TREASURE_BAG_TAG ) ? data.getList( TreasureBagManager.TREASURE_BAG_TAG, 10 ) : new ListNBT() );
	}

	protected static String getPlayerUUID( PlayerEntity player ) {
		return String.valueOf( player.getUniqueID() );
	}

	protected static CompoundNBT getPlayerCompound( PlayerEntity player ) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString( TreasureBagManager.PLAYER_TAG, getPlayerUUID( player ) );

		return nbt;
	}

	protected static boolean isPlayerInList( PlayerEntity player, ListNBT listNBT ) {
		String uuid = getPlayerUUID( player );

		for( int i = 0; i < listNBT.size(); i++ )
			if( listNBT.getCompound( i )
				.getString( TreasureBagManager.PLAYER_TAG )
				.equals( uuid ) )
				return true;

		return false;
	}
}
