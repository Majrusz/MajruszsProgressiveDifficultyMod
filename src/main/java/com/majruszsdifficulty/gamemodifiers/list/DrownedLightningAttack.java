package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.projectile.ThrownTrident;

@AutoInstance
public class DrownedLightningAttack extends GameModifier {
	public DrownedLightningAttack() {
		super( Registries.Modifiers.DEFAULT );

		OnDamaged.listen( this::spawnLightningBolt )
			.addCondition( CustomConditions.gameStageAtLeast( GameStage.EXPERT ) )
			.addCondition( Condition.excludable() )
			.addCondition( Condition.predicate( data->data.attacker instanceof Drowned ) )
			.addCondition( Condition.predicate( data->data.source.getDirectEntity() instanceof ThrownTrident ) )
			.addCondition( Condition.predicate( data->LevelHelper.isEntityOutsideWhenItIsRaining( data.target ) ) )
			.insertTo( this );

		this.name( "DrownedLightningAttack" ).comment( "Drowned trident throw may spawn a lightning bolt when it rains." );
	}

	private void spawnLightningBolt( OnDamaged.Data data ) {
		LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( data.target.level );
		if( lightningBolt == null )
			return;

		lightningBolt.absMoveTo( data.target.getX(), data.target.getY(), data.target.getZ() );
		data.target.level.addFreshEntity( lightningBolt );
	}
}
