package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.StringListConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnLoot;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.majruszsdifficulty.GameStage.Stage;

@AutoInstance
public class DoubleLoot extends GameModifier {
	static final ParticleHandler AWARD = new ParticleHandler( ParticleTypes.HAPPY_VILLAGER, ()->new Vec3( 0.5, 1, 0.5 ), ()->0.1f );
	final StringListConfig forbiddenItems = new StringListConfig( "minecraft:nether_star", "minecraft:totem_of_undying" );

	public DoubleLoot() {
		super( Registries.Modifiers.DEFAULT );

		new OnDoubleLootContext( this::doubleLoot, 0.0, Stage.NORMAL )
			.name( "NormalMode" )
			.comment( "Determines the chance on Normal Mode." )
			.insertTo( this );

		new OnDoubleLootContext( this::doubleLoot, 0.2, Stage.EXPERT )
			.name( "ExpertMode" )
			.comment( "Determines the chance on Expert Mode." )
			.insertTo( this );

		new OnDoubleLootContext( this::doubleLoot, 0.4, Stage.MASTER )
			.name( "MasterMode" )
			.comment( "Determines the chance on Master Mode." )
			.insertTo( this );

		this.addConfig( this.forbiddenItems.name( "forbidden_items" ).comment( "List of items that cannot be duplicated." ) );
		this.name( "DoubleLoot" ).comment( "Gives a chance to double the loot." );
	}

	private void doubleLoot( OnLoot.Data data ) {
		assert data.entity != null && data.lastDamagePlayer != null;

		boolean doubledAtLeastOneItem = replaceLoot( data.generatedLoot );
		if( doubledAtLeastOneItem && data.level != null ) {
			AWARD.spawn( data.level, data.entity.position().add( 0.0, 0.5, 0.0 ), 12 );
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

	private static class OnDoubleLootContext extends OnLoot.Context {
		public OnDoubleLootContext( Consumer< OnLoot.Data > consumer, double chance, Stage stage ) {
			super( consumer );

			this.addCondition( new CustomConditions.GameStageExact<>( stage ) )
				.addCondition( new CustomConditions.CRDChance<>( chance, false ) )
				.addCondition( OnLoot.HAS_LAST_DAMAGE_PLAYER )
				.addCondition( OnLoot.HAS_ENTITY );
		}
	}
}
