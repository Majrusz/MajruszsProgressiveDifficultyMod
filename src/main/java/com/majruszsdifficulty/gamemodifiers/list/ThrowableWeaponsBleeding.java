package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.Config;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.GameModifierHelper;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrownTrident;

public class ThrowableWeaponsBleeding extends GameModifier {
	static final Config.Bleeding BLEEDING = new Config.Bleeding();
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 0.4, false ) );
		ON_DAMAGED.addCondition( new ICondition.IsLivingBeing() );
		ON_DAMAGED.addCondition( new ICondition.ArmorDependentChance() );
		ON_DAMAGED.addCondition( new ICondition.Context<>( OnDamagedContext.Data.class, data->data.source.getDirectEntity() instanceof Arrow || data.source.getDirectEntity() instanceof ThrownTrident ) );
		ON_DAMAGED.addConfig( BLEEDING );
	}

	public ThrowableWeaponsBleeding() {
		super( "ThrowableWeaponsBleeding", "All throwable sharp items (arrows, trident etc.) may inflict bleeding.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			GameModifierHelper.applyBleeding( damagedData, BLEEDING );
		}
	}
}
