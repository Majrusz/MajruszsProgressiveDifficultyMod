package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.StringListConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.Context;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnLoot;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@AutoInstance
public class DoubleLoot extends GameModifier {
	static final ParticleHandler AWARD = new ParticleHandler( ParticleTypes.HAPPY_VILLAGER, ()->new Vec3( 0.5, 1, 0.5 ), ()->0.1f );
	final StringListConfig forbiddenItems = new StringListConfig( "minecraft:nether_star", "minecraft:totem_of_undying" );

	public DoubleLoot() {
		super( Registries.Modifiers.DEFAULT );

		OnDoubleLoot.listen( this::doubleLoot, 0.0, GameStage.NORMAL )
			.name( "NormalMode" )
			.comment( "Determines the chance on Normal Mode." )
			.insertTo( this );

		OnDoubleLoot.listen( this::doubleLoot, 0.2, GameStage.EXPERT )
			.name( "ExpertMode" )
			.comment( "Determines the chance on Expert Mode." )
			.insertTo( this );

		OnDoubleLoot.listen( this::doubleLoot, 0.4, GameStage.MASTER )
			.name( "MasterMode" )
			.comment( "Determines the chance on Master Mode." )
			.insertTo( this );

		this.addConfig( this.forbiddenItems.name( "forbidden_items" ).comment( "List of items that cannot be duplicated." ) );
		this.name( "DoubleLoot" ).comment( "Gives a chance to double the loot." );
	}

	private void doubleLoot( OnLoot.Data data ) {
		assert data.entity != null && data.lastDamagePlayer != null;

		boolean doubledAtLeastOneItem = replaceLoot( data.generatedLoot );
		if( doubledAtLeastOneItem && data.getServerLevel() != null ) {
			AWARD.spawn( data.getServerLevel(), data.entity.position().add( 0.0, 0.5, 0.0 ), 12 );
		}
	}

	private boolean replaceLoot( List< ItemStack > generatedLoot ) {
		boolean doubledAtLeastOneItem = false;
		List< ItemStack > doubledLoot = new ArrayList<>();
		for( ItemStack itemStack : generatedLoot ) {
			doubledLoot.add( itemStack );
			if( this.isAllowed( itemStack ) ) {
				doubledLoot.add( itemStack );
				doubledAtLeastOneItem = true;
			}
		}
		generatedLoot.clear();
		generatedLoot.addAll( doubledLoot );

		return doubledAtLeastOneItem;
	}

	private boolean isAllowed( ItemStack itemStack ) {
		return !itemStack.isEmpty() && !this.forbiddenItems.contains( Utility.getRegistryString( itemStack ) );
	}

	private static class OnDoubleLoot {
		public static Context< OnLoot.Data > listen( Consumer< OnLoot.Data > consumer, double chance, GameStage stage ) {
			return OnLoot.listen( consumer )
				.addCondition( CustomConditions.gameStage( stage ) )
				.addCondition( Condition.chanceCRD( chance, false ) )
				.addCondition( OnLoot.hasLastDamagePlayer() )
				.addCondition( OnLoot.hasEntity() );
		}
	}
}
