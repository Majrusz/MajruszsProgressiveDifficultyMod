package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.GameModifierHelper;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import net.minecraft.world.damagesource.DamageSource;

public class CactusBleeding extends GameModifier {
	static final Config.Bleeding BLEEDING = new Config.Bleeding();
	static final DamagedContext ON_DAMAGED = new DamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 0.5, false ) );
		ON_DAMAGED.addCondition( new ICondition.IsLivingBeing() );
		ON_DAMAGED.addCondition( new ICondition.ArmorDependentChance() );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->data.source.equals( DamageSource.CACTUS ) ) );
		ON_DAMAGED.addCondition( new DamagedContext.DirectDamage() );
		ON_DAMAGED.addConfig( BLEEDING );
	}

	public CactusBleeding() {
		super( "CactusBleeding", "Cactus damage may inflict bleeding.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof DamagedContext.Data damagedData ) {
			GameModifierHelper.applyBleeding( damagedData, BLEEDING );
		}
	}
}
