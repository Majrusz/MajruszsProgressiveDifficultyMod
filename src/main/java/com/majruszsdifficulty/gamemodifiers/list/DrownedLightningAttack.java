package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import com.mlib.items.ItemHelper;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.TridentItem;

public class DrownedLightningAttack extends GameModifier {
	static final DamagedContext ON_DAMAGED = new DamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.EXPERT ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 0.6, true ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->data.attacker instanceof Drowned ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->ItemHelper.hasInMainHand( data.attacker, TridentItem.class ) ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->LevelHelper.isEntityOutsideWhenItIsRaining( data.target ) ) );
	}

	public DrownedLightningAttack() {
		super( "DrownedLightningAttack", "Drowned trident throw may spawn a lightning bolt when it rains.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof DamagedContext.Data damagedData ) {
			LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create( damagedData.level );
			if( lightningBolt == null )
				return;

			lightningBolt.absMoveTo( damagedData.target.getX(), damagedData.target.getY(), damagedData.target.getZ() );
			damagedData.level.addFreshEntity( lightningBolt );
		}
	}
}
