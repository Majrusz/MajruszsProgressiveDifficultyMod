package com.majruszsdifficulty.treasurebag.listeners;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntityDamaged;
import com.majruszlibrary.events.OnEntityDied;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.registry.Registries;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.items.TreasureBag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Supplier;

public class KillRewarder {
	private static final Map< String, Supplier< TreasureBag > > REWARDS = Map.of(
		"minecraft:elder_guardian", MajruszsDifficulty.ELDER_GUARDIAN_TREASURE_BAG_ITEM,
		"minecraft:ender_dragon", MajruszsDifficulty.ENDER_DRAGON_TREASURE_BAG_ITEM,
		"minecraft:warden", MajruszsDifficulty.WARDEN_TREASURE_BAG_ITEM,
		"minecraft:wither", MajruszsDifficulty.WITHER_TREASURE_BAG_ITEM
	);

	static {
		OnEntityDamaged.listen( KillRewarder::markForReward )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.attacker instanceof Player )
			.addCondition( data->REWARDS.containsKey( Registries.ENTITY_TYPES.getId( data.target.getType() ).toString() ) );

		OnEntityDied.listen( KillRewarder::giveTreasureBag )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->REWARDS.containsKey( Registries.ENTITY_TYPES.getId( data.target.getType() ).toString() ) );

		Serializables.get( DamageInfo.class )
			.define( "TreasureBagPlayersToReward", Reader.list( Reader.uuid() ), s->new ArrayList<>( s.uuids ), ( s, v )->s.uuids = new HashSet<>( v ) );
	}

	private static void markForReward( OnEntityDamaged data ) {
		Serializables.modify( new DamageInfo(), EntityHelper.getOrCreateExtraTag( data.target ), damageInfo->damageInfo.uuids.add( data.attacker.getUUID() ) );
	}

	private static void giveTreasureBag( OnEntityDied data ) {
		TreasureBag treasureBag = REWARDS.get( Registries.ENTITY_TYPES.getId( data.target.getType() ).toString() ).get();
		Serializables.read( new DamageInfo(), EntityHelper.getOrCreateExtraTag( data.target ) ).uuids.forEach( uuid->{
			Player player = data.getLevel().getPlayerByUUID( uuid );
			if( player != null ) {
				ItemHelper.giveToPlayer( new ItemStack( treasureBag ), player );
			}
		} );
	}

	private static class DamageInfo {
		public Set< UUID > uuids = new HashSet<>();
	}
}
