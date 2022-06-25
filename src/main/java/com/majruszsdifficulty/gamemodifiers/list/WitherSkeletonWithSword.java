package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.CustomConfigs;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.Config;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.items.ItemHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;

public class WitherSkeletonWithSword extends GameModifier {
	static final Config.ItemStack WITHER_SWORD = new Config.ItemStack( "WitherSword", Registries.WITHER_SWORD::get, EquipmentSlot.MAINHAND, 0.5, 0.01, 0.2 );
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext();

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof WitherSkeleton ) );
		ON_SPAWNED.addConfig( WITHER_SWORD );
	}

	public WitherSkeletonWithSword() {
		super( GameModifier.DEFAULT, "WitherSkeletonWithSword", "Wither Skeleton may spawn with the Wither Sword.", ON_SPAWNED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnSpawnedContext.Data spawnedData ) {
			WitherSkeleton skeleton = ( WitherSkeleton )spawnedData.target;
			WITHER_SWORD.tryToEquip( skeleton, GameStage.getRegionalDifficulty( skeleton ) );
		}
	}
}
