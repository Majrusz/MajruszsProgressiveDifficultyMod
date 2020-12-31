package com.majruszs_difficulty.events.treasure_bag;

import com.majruszs_difficulty.events.TreasureBagManager;
import com.majruszs_difficulty.items.TreasureBagItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class UpdatePlayerList {
	@SubscribeEvent
	public static void onHit( LivingHurtEvent event ) {
		DamageSource source = event.getSource();
		LivingEntity target = event.getEntityLiving();

		if( source.getTrueSource() instanceof PlayerEntity )
			return;

		PlayerEntity player = ( PlayerEntity )source.getTrueSource();

		if( player == null )
			return;

		ListNBT listNBT;
		if( target.getPersistentData().contains( "PlayersToReward" ) )
			listNBT = target.getPersistentData().getList( "PlayersToReward", 10 );
		else
			listNBT = new ListNBT();

		String uuid = String.valueOf( player.getUniqueID() );
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString( "UUID", uuid );

		boolean hasValue = false;
		for( int i = 0; i < listNBT.size(); i++ )
			if( listNBT.getCompound( i ).getString( "UUID" ).equals( uuid ) )
				hasValue = true;

		if( !hasValue )
			listNBT.add( nbt );

		target.getPersistentData().put( "PlayersToReward", listNBT );
	}

	@SubscribeEvent
	public static void onDie( LivingDeathEvent event ) {
		LivingEntity target = event.getEntityLiving();
		Item treasureBag = TreasureBagManager.getTreasureBag( target.getType() );

		if( treasureBag == null )
			return;
	}
}
