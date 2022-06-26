package com.majruszsdifficulty.treasurebags;

import com.majruszsdifficulty.MajruszsHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class UpdatePlayerList {
	@SubscribeEvent
	public static void onHit( LivingHurtEvent event ) {
		Player player = MajruszsHelper.getPlayerFromDamageSource( event.getSource() );
		LivingEntity target = event.getEntityLiving();

		if( player == null || !TreasureBagManager.hasTreasureBag( target.getType() ) )
			return;

		ListTag listNBT = getOrCreateList( target );
		CompoundTag playerNBT = getPlayerCompound( player );

		if( !isPlayerInList( player, listNBT ) )
			listNBT.add( playerNBT );

		CompoundTag data = target.getPersistentData();
		data.put( TreasureBagManager.TREASURE_BAG_TAG, listNBT );
	}

	protected static ListTag getOrCreateList( LivingEntity entity ) {
		CompoundTag data = entity.getPersistentData();

		return ( data.contains( TreasureBagManager.TREASURE_BAG_TAG ) ? data.getList( TreasureBagManager.TREASURE_BAG_TAG, 10 ) : new ListTag() );
	}

	protected static String getPlayerUUID( Player player ) {
		return String.valueOf( player.getUUID() );
	}

	protected static CompoundTag getPlayerCompound( Player player ) {
		CompoundTag nbt = new CompoundTag();
		nbt.putString( TreasureBagManager.PLAYER_TAG, getPlayerUUID( player ) );

		return nbt;
	}

	protected static boolean isPlayerInList( Player player, ListTag listNBT ) {
		String uuid = getPlayerUUID( player );

		for( int i = 0; i < listNBT.size(); i++ )
			if( listNBT.getCompound( i ).getString( TreasureBagManager.PLAYER_TAG ).equals( uuid ) )
				return true;

		return false;
	}
}
