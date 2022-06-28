package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.configs.ItemStackConfig;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.WitherSkeleton;

public class WitherSkeletonWithSword extends GameModifier {
	static final ItemStackConfig WITHER_SWORD = new ItemStackConfig( "WitherSword", Registries.WITHER_SWORD::get, EquipmentSlot.MAINHAND, 0.5, 0.01, 0.2 );
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext( WitherSkeletonWithSword::giveWitherSword );

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof WitherSkeleton ) );
		ON_SPAWNED.addConfig( WITHER_SWORD );
	}

	public WitherSkeletonWithSword() {
		super( GameModifier.DEFAULT, "WitherSkeletonWithSword", "Wither Skeleton may spawn with the Wither Sword.", ON_SPAWNED );
	}

	private static void giveWitherSword( com.mlib.gamemodifiers.GameModifier gameModifier, OnSpawnedContext.Data data ) {
		WitherSkeleton skeleton = ( WitherSkeleton )data.target;
		WITHER_SWORD.tryToEquip( skeleton, GameStage.getRegionalDifficulty( skeleton ) );
	}
}
