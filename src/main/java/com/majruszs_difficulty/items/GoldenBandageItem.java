package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.mlib.config.DurationConfig;
import com.mlib.effects.EffectHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.common.Mod;

/** Simple bandage item that removes bleeding effect and gives regeneration for few seconds. */
@Mod.EventBusSubscriber
public class GoldenBandageItem extends BandageItem {
	protected final DurationConfig immunityDuration;

	public GoldenBandageItem() {
		super( "Golden Bandage", 1, Rarity.RARE );

		String durationComment = "Duration in seconds of Bleeding Immunity effect.";
		this.immunityDuration = new DurationConfig( "immunity_duration", durationComment, false, 90.0, 1.0, 600.0 );
		this.configGroup.addConfigs( this.immunityDuration );
	}

	/** Applies Regeneration and Bleeding immunity on target depending on current mod settings. */
	@Override
	protected void applyEffects( LivingEntity target ) {
		super.applyEffects( target );
		EffectHelper.applyEffectIfPossible( target, Instances.BLEEDING_IMMUNITY, getImmunityDuration(), 0 );
	}

	/** Returns duration in ticks of Bleeding immunity effect. */
	public int getImmunityDuration() {
		return this.immunityDuration.getDuration();
	}
}
