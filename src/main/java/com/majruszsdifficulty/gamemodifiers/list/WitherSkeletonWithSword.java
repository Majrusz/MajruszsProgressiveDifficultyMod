package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawnedContext;
import com.mlib.items.ItemHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;

public class WitherSkeletonWithSword extends GameModifier {
	static final OnSpawnedContext ON_SPAWNED = new OnSpawnedContext();

	static {
		ON_SPAWNED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_SPAWNED.addCondition( new CustomConditions.CRDChance( 0.5 ) );
		ON_SPAWNED.addCondition( new Condition.Excludable() );
		ON_SPAWNED.addCondition( new Condition.ContextOnSpawned( data->data.target instanceof WitherSkeleton ) );
	}

	final DoubleConfig dropChance;

	public WitherSkeletonWithSword() {
		super( GameModifier.DEFAULT, "WitherSkeletonWithSword", "Wither Skeleton may spawn with the Wither Sword.", ON_SPAWNED );
		this.dropChance = new DoubleConfig( "drop_chance", "Chance for Wither Sword to drop.", false, 0.01, 0.0, 1.0 );
		this.configGroup.addConfig( this.dropChance );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnSpawnedContext.Data spawnedData ) {
			WitherSkeleton skeleton = ( WitherSkeleton )spawnedData.target;
			ItemStack witherSword = new ItemStack( Registries.WITHER_SWORD.get() );
			ItemHelper.damageAndEnchantItem( witherSword, GameStage.getRegionalDifficulty( skeleton ), true, 0.5 );
			skeleton.setItemSlot( EquipmentSlot.MAINHAND, witherSword );
			skeleton.setDropChance( EquipmentSlot.MAINHAND, this.dropChance.get().floatValue() );
		}
	}
}
