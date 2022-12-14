package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.config.StringListConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.gamemodifiers.contexts.OnLoot;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static com.majruszsdifficulty.GameStage.Stage;

public class DoubleLoot extends GameModifier {
	static final ParticleHandler AWARD = new ParticleHandler( ParticleTypes.HAPPY_VILLAGER, ()->new Vec3( 0.5, 1, 0.5 ), ()->0.1f );
	final StringListConfig forbiddenItems = new StringListConfig( "forbidden_items", "List of items that cannot be duplicated.", false, "minecraft:nether_star", "minecraft:totem_of_undying" );

	public DoubleLoot() {
		super( Registries.Modifiers.DEFAULT, "DoubleLoot", "Gives a chance to double the loot." );

		this.generateContext( 0.0, Stage.NORMAL, "NormalMode", "Determines the chance on Normal Mode." );
		this.generateContext( 0.2, Stage.EXPERT, "ExpertMode", "Determines the chance on Expert Mode." );
		this.generateContext( 0.4, Stage.MASTER, "MasterMode", "Determines the chance on Master Mode." );
		this.addConfig( this.forbiddenItems );
	}

	private void generateContext( double chance, Stage stage, String configName, String configComment ) {
		OnLoot.Context onLoot = new OnLoot.Context( this::doubleLoot, configName, configComment );
		onLoot.addCondition( new CustomConditions.GameStageExact<>( stage ) )
			.addCondition( new CustomConditions.CRDChance<>( chance, false ) )
			.addCondition( OnLoot.HAS_LAST_DAMAGE_PLAYER )
			.addCondition( OnLoot.HAS_ENTITY );

		this.addContext( onLoot );
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
}
