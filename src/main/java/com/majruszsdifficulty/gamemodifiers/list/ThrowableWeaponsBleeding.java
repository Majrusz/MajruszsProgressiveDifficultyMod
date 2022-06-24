package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.CustomConfigs;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrownTrident;

public class ThrowableWeaponsBleeding extends GameModifier {
	static final CustomConfigs.Bleeding BLEEDING = new CustomConfigs.Bleeding();
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext();

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.NORMAL ) );
		ON_DAMAGED.addCondition( new Condition.Chance( 0.4 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.IsLivingBeing() );
		ON_DAMAGED.addCondition( new Condition.ArmorDependentChance() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.source.getDirectEntity() instanceof Arrow || data.source.getDirectEntity() instanceof ThrownTrident ) );
		ON_DAMAGED.addConfig( BLEEDING );
	}

	public ThrowableWeaponsBleeding() {
		super( GameModifier.DEFAULT, "ThrowableWeaponsBleeding", "All throwable sharp items (arrows, trident etc.) may inflict bleeding.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof OnDamagedContext.Data damagedData ) {
			BLEEDING.apply( damagedData );
		}
	}
}
