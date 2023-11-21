package com.majruszsdifficulty.features;

import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.events.OnLootGenerated;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszlibrary.registry.Registries;
import com.majruszlibrary.text.RegexString;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DoubleLoot {
	private static final GameStageValue< Float > CHANCE = GameStageValue.of(
		DefaultMap.defaultEntry( 0.0f ),
		DefaultMap.entry( GameStage.EXPERT_ID, 0.05f ),
		DefaultMap.entry( GameStage.MASTER_ID, 0.1f )
	);
	private static List< RegexString > BLACKLISTED_ITEMS = List.of(
		new RegexString( "minecraft:nether_star" ),
		new RegexString( "minecraft:totem_of_undying" )
	);

	static {
		OnLootGenerated.listen( DoubleLoot::doubleLoot )
			.addCondition( data->data.lastDamagePlayer != null )
			.addCondition( data->data.entity != null )
			.addCondition( data->Random.check( CHANCE.get( GameStageHelper.determineGameStage( data ) ) ) );

		Serializables.getStatic( Config.Features.class )
			.define( "double_loot", DoubleLoot.class );

		Serializables.getStatic( DoubleLoot.class )
			.define( "chance", Reader.map( Reader.number() ), ()->CHANCE.get(), v->CHANCE.set( Range.CHANCE.clamp( v ) ) )
			.define( "blacklisted_items", Reader.list( Reader.string() ), ()->RegexString.toString( BLACKLISTED_ITEMS ), v->BLACKLISTED_ITEMS = RegexString.toRegex( v ) );
	}

	private static void doubleLoot( OnLootGenerated data ) {
		if( DoubleLoot.replaceLoot( data.generatedLoot ) ) {
			ParticleEmitter.of( ParticleTypes.HAPPY_VILLAGER )
				.count( 6 )
				.sizeBased( data.entity )
				.emit( data.getServerLevel() );
		}
	}

	private static boolean replaceLoot( List< ItemStack > generatedLoot ) {
		List< ItemStack > extraLoot = new ArrayList<>();
		generatedLoot.forEach( itemStack->{
			if( DoubleLoot.isAllowed( itemStack ) ) {
				extraLoot.add( itemStack );
			}
		} );
		generatedLoot.addAll( extraLoot );

		return !extraLoot.isEmpty();
	}

	private static boolean isAllowed( ItemStack itemStack ) {
		if( itemStack.isEmpty() ) {
			return false;
		}

		String id = Registries.get( itemStack.getItem() ).toString();
		return BLACKLISTED_ITEMS.stream().noneMatch( regex->regex.matches( id ) );
	}
}
