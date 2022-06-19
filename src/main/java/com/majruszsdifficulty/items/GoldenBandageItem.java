package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.config.DurationConfig;
import com.mlib.effects.EffectHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GoldenBandageItem extends BandageItem {
	protected final DurationConfig immunityDuration;

	public GoldenBandageItem() {
		super( "GoldenBandage", 1, Rarity.RARE );

		this.immunityDuration = new DurationConfig( "immunity_duration", "Duration in seconds of Bleeding Immunity effect.", false, 90.0, 1.0, 600.0 );
		this.configGroup.addConfigs( this.immunityDuration );
	}

	@Override
	protected void applyEffects( LivingEntity target ) {
		super.applyEffects( target );
		EffectHelper.applyEffectIfPossible( target, Registries.BLEEDING_IMMUNITY.get(), getImmunityDuration(), 0 );
	}

	public int getImmunityDuration() {
		return this.immunityDuration.getDuration();
	}
}
