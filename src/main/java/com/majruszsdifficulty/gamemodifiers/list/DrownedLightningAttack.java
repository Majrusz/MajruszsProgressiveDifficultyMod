package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.projectile.ThrownTrident;

public class DrownedLightningAttack extends GameModifier {
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.EXPERT ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.attacker instanceof Drowned ) );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.source.getDirectEntity() instanceof ThrownTrident ) );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->LevelHelper.isEntityOutsideWhenItIsRaining( data.target ) ) );
	}

	public DrownedLightningAttack() {
		super( GameModifier.DEFAULT, "DrownedLightningAttack", "Drowned trident throw may spawn a lightning bolt when it rains.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( damagedData.target.level );
			if( lightningBolt == null )
				return;

			lightningBolt.absMoveTo( damagedData.target.getX(), damagedData.target.getY(), damagedData.target.getZ() );
			damagedData.target.level.addFreshEntity( lightningBolt );
		}
	}
}
