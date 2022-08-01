package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.DifficultyModifier;
import com.mlib.Utility;
import com.mlib.config.StringListConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.gamemodifiers.contexts.OnLootContext;
import com.mlib.gamemodifiers.data.OnLootData;
import com.mlib.gamemodifiers.parameters.ContextParameters;
import com.mlib.gamemodifiers.parameters.Priority;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class DoubleLoot extends DifficultyModifier {
	static final ParticleHandler AWARD = new ParticleHandler( ParticleTypes.HAPPY_VILLAGER, new Vec3( 0.5, 0.5, 0.5 ), () -> 0.1f );
	final StringListConfig forbiddenItems = new StringListConfig( "forbidden_items", "List of items that cannot be duplicated.", false, "minecraft:nether_star", "minecraft:totem_of_undying" );

	public DoubleLoot() {
		super( DifficultyModifier.DEFAULT, "DoubleLoot", "Gives a chance to double the loot." );

		OnLootContext onLootNormal = new OnLootContext( this::doubleLoot, new ContextParameters( Priority.NORMAL, "NormalMode", "" ) );
		onLootNormal.addCondition( new CustomConditions.GameStageExact( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance( 0.0, false ) )
			.addCondition( OnLootContext.HAS_LAST_DAMAGE_PLAYER )
			.addCondition( OnLootContext.HAS_ENTITY );

		OnLootContext onLootExpert = new OnLootContext( this::doubleLoot, new ContextParameters( Priority.NORMAL, "ExpertMode", "" ) );
		onLootExpert.addCondition( new CustomConditions.GameStageExact( GameStage.Stage.EXPERT ) )
			.addCondition( new CustomConditions.CRDChance( 0.2, false ) )
			.addCondition( OnLootContext.HAS_LAST_DAMAGE_PLAYER )
			.addCondition( OnLootContext.HAS_ENTITY );

		OnLootContext onLootMaster = new OnLootContext( this::doubleLoot, new ContextParameters( Priority.NORMAL, "MasterMode", "" ) );
		onLootMaster.addCondition( new CustomConditions.GameStageExact( GameStage.Stage.MASTER ) )
			.addCondition( new CustomConditions.CRDChance( 0.4, false ) )
			.addCondition( OnLootContext.HAS_LAST_DAMAGE_PLAYER )
			.addCondition( OnLootContext.HAS_ENTITY );

		this.addContexts( onLootNormal, onLootExpert, onLootMaster );
		this.addConfig( this.forbiddenItems );
	}

	private void doubleLoot( OnLootData data ) {
		assert data.entity != null && data.lastDamagePlayer != null;

		boolean doubledAtLeastOneItem = replaceLoot( data.generatedLoot );
		if( doubledAtLeastOneItem && data.level != null ) {
			AWARD.spawn( data.level, data.entity.position().add( 0.0, 0.5, 0.0 ), 8 );
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
