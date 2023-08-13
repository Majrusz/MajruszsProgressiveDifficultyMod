package com.majruszsdifficulty.features;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.contexts.base.CustomConditions;
import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.Utility;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.StringListConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.Context;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnLoot;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@AutoInstance
public class DoubleLoot {
	static final ParticleHandler AWARD = new ParticleHandler( ParticleTypes.HAPPY_VILLAGER, ()->new Vec3( 0.25, 0.5, 0.25 ), ()->0.1f );
	final StringListConfig forbiddenItems = new StringListConfig( "minecraft:nether_star", "minecraft:totem_of_undying" );

	public DoubleLoot() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "DoubleLoot" )
			.comment( "Gives a chance to double the loot." )
			.addConfig( this.forbiddenItems.name( "forbidden_items" ).comment( "List of items that cannot be duplicated." ) );

		OnDoubleLoot.listen( this::doubleLoot, 0.0, GameStage.NORMAL )
			.name( "NormalMode" )
			.comment( "Determines the chance on Normal Mode." )
			.insertTo( group );

		OnDoubleLoot.listen( this::doubleLoot, 0.1, GameStage.EXPERT )
			.name( "ExpertMode" )
			.comment( "Determines the chance on Expert Mode." )
			.insertTo( group );

		OnDoubleLoot.listen( this::doubleLoot, 0.2, GameStage.MASTER )
			.name( "MasterMode" )
			.comment( "Determines the chance on Master Mode." )
			.insertTo( group );
	}

	private void doubleLoot( OnLoot.Data data ) {
		assert data.entity != null && data.lastDamagePlayer != null;

		boolean doubledAtLeastOneItem = this.replaceLoot( data.generatedLoot );
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
