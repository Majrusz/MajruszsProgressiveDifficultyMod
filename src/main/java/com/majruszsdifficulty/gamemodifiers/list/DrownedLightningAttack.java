package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnDamaged;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.projectile.ThrownTrident;

@AutoInstance
public class DrownedLightningAttack {
	public DrownedLightningAttack() {
		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
			.name( "DrownedLightningAttack" )
			.comment( "Drowned trident throw may spawn a lightning bolt when it rains." );

		OnDamaged.listen( this::spawnLightningBolt )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.EXPERT ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.attacker instanceof Drowned ) )
			.addCondition( Condition.predicate( data->data.source.getDirectEntity() instanceof ThrownTrident ) )
			.addCondition( Condition.predicate( data->LevelHelper.isEntityOutside( data.target ) ) )
			.addCondition( Condition.predicate( data->LevelHelper.isRainingAt( data.target ) ) )
			.insertTo( group );
	}

	private void spawnLightningBolt( OnDamaged.Data data ) {
		LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( data.target.level() );
		if( lightningBolt == null )
			return;

		lightningBolt.absMoveTo( data.target.getX(), data.target.getY(), data.target.getZ() );
		data.target.level().addFreshEntity( lightningBolt );
	}
}
