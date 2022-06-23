package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.projectile.ThrownTrident;

public class DrownedLightningAttack extends GameModifier {
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.EXPERT ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 0.6, true ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( OnDamagedContext.Data.class, data->data.attacker instanceof Drowned ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( OnDamagedContext.Data.class, data->data.source.getDirectEntity() instanceof ThrownTrident ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( OnDamagedContext.Data.class, data->LevelHelper.isEntityOutsideWhenItIsRaining( data.target ) ) );
	}

	public DrownedLightningAttack() {
		super( "DrownedLightningAttack", "Drowned trident throw may spawn a lightning bolt when it rains.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( damagedData.level );
			if( lightningBolt == null )
				return;

			lightningBolt.absMoveTo( damagedData.target.getX(), damagedData.target.getY(), damagedData.target.getZ() );
			damagedData.level.addFreshEntity( lightningBolt );
		}
	}
}
