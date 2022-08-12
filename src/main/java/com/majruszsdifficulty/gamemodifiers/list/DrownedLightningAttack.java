package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.projectile.ThrownTrident;

public class DrownedLightningAttack extends GameModifier {
	public DrownedLightningAttack() {
		super( Registries.Modifiers.DEFAULT, "DrownedLightningAttack", "Drowned trident throw may spawn a lightning bolt when it rains." );

		OnDamagedContext onDamaged = new OnDamagedContext( this::spawnLightningBolt );
		onDamaged.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.attacker instanceof Drowned )
			.addCondition( data->data.source.getDirectEntity() instanceof ThrownTrident )
			.addCondition( data->LevelHelper.isEntityOutsideWhenItIsRaining( data.target ) );

		this.addContext( onDamaged );
	}

	private void spawnLightningBolt( OnDamagedData data ) {
		LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( data.target.level );
		if( lightningBolt == null )
			return;

		lightningBolt.absMoveTo( data.target.getX(), data.target.getY(), data.target.getZ() );
		data.target.level.addFreshEntity( lightningBolt );
	}
}
