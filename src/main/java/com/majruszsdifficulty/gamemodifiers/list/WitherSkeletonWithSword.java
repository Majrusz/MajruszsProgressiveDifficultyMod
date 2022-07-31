package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.DifficultyModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.configs.ItemStackConfig;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.gamemodifiers.data.OnSpawnedData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.WitherSkeleton;

public class WitherSkeletonWithSword extends DifficultyModifier {
	final ItemStackConfig witherSword = new ItemStackConfig( "WitherSword", Registries.WITHER_SWORD::get, EquipmentSlot.MAINHAND, 0.5, 0.01, 0.2 );

	public WitherSkeletonWithSword() {
		super( DifficultyModifier.DEFAULT, "WitherSkeletonWithSword", "Wither Skeleton may spawn with the Wither Sword." );

		OnSpawnedContext onSpawned = new OnSpawnedContext( this::giveWitherSword );
		onSpawned.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.target instanceof WitherSkeleton )
			.addConfig( this.witherSword );

		this.addContext( onSpawned );
	}

	private void giveWitherSword( OnSpawnedData data ) {
		WitherSkeleton skeleton = ( WitherSkeleton )data.target;
		this.witherSword.tryToEquip( skeleton, GameStage.getRegionalDifficulty( skeleton ) );
	}
}
