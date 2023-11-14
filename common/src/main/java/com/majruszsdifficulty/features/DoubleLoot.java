package com.majruszsdifficulty.features;

import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.mlib.annotation.AutoInstance;
import com.mlib.collection.DefaultMap;
import com.mlib.contexts.OnLootGenerated;
import com.mlib.data.Serializables;
import com.mlib.emitter.ParticleEmitter;
import com.mlib.math.Random;
import com.mlib.math.Range;
import com.mlib.registry.Registries;
import com.mlib.text.RegexString;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AutoInstance
public class DoubleLoot {
	private GameStageValue< Float > chance = GameStageValue.of(
		DefaultMap.defaultEntry( 0.0f ),
		DefaultMap.entry( GameStage.EXPERT_ID, 0.05f ),
		DefaultMap.entry( GameStage.MASTER_ID, 0.1f )
	);
	private List< RegexString > blacklistedItems = List.of(
		new RegexString( "minecraft:nether_star" ),
		new RegexString( "minecraft:totem_of_undying" )
	);

	public DoubleLoot() {
		OnLootGenerated.listen( this::doubleLoot )
			.addCondition( data->data.lastDamagePlayer != null )
			.addCondition( data->data.entity != null )
			.addCondition( data->Random.check( this.chance.get( GameStageHelper.determineGameStage( data ) ) ) );

		Serializables.get( Config.Features.class )
			.define( "double_loot", subconfig->{
				subconfig.defineFloatMap( "chance", s->this.chance.get(), ( s, v )->this.chance.set( Range.CHANCE.clamp( v ) ) );
				subconfig.defineStringList( "blacklisted_items", s->RegexString.toString( this.blacklistedItems ), ( s, v )->this.blacklistedItems = RegexString.toRegex( v ) );
			} );
	}

	private void doubleLoot( OnLootGenerated data ) {
		if( this.replaceLoot( data.generatedLoot ) ) {
			ParticleEmitter.of( ParticleTypes.HAPPY_VILLAGER )
				.count( 6 )
				.sizeBased( data.entity )
				.emit( data.getServerLevel() );
		}
	}

	private boolean replaceLoot( List< ItemStack > generatedLoot ) {
		List< ItemStack > extraLoot = new ArrayList<>();
		generatedLoot.forEach( itemStack->{
			if( this.isAllowed( itemStack ) ) {
				extraLoot.add( itemStack );
			}
		} );
		generatedLoot.addAll( extraLoot );

		return !extraLoot.isEmpty();
	}

	private boolean isAllowed( ItemStack itemStack ) {
		if( itemStack.isEmpty() ) {
			return false;
		}

		String id = Registries.get( itemStack.getItem() ).toString();
		return this.blacklistedItems.stream().noneMatch( regex->regex.matches( id ) );
	}
}
