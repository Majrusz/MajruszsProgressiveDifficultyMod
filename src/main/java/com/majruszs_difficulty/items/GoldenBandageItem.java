package com.majruszs_difficulty.items;

import com.mlib.config.DurationConfig;
import com.mlib.config.IntegerConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.common.Mod;

/** Simple bandage item that removes bleeding effect and gives regeneration for few seconds. */
@Mod.EventBusSubscriber
public class GoldenBandageItem extends BandageItem {
	protected final DurationConfig immunityDuration;
	protected final IntegerConfig immunityAmplifier;

	public GoldenBandageItem() {
		super( "Golden Bandage", 1, Rarity.RARE );

		String durationComment = "Duration in seconds of Bleeding Immunity effect.";
		String amplifierComment = "Level/amplifier of Bleeding Immunity effect.";
		this.immunityDuration = new DurationConfig( "immunity_duration", durationComment, false, 90.0, 1.0, 600.0 );
		this.immunityAmplifier = new IntegerConfig( "immunity_amplifier", amplifierComment, false, 0, 0, 10 );
		this.configGroup.addConfigs( this.immunityDuration, this.immunityAmplifier );
	}

	/** Applies Regeneration and Bleeding immunity on target depending on current mod settings. */
	@Override
	protected void applyEffects( LivingEntity target ) {
		super.applyEffects( target );
		// EffectHelper.applyEffectIfPossible( target, Effects.REGENERATION, getRegenerationDuration(), getRegenerationAmplifier() );
	}
}
