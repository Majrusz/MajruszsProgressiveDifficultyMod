package com.majruszs_difficulty.events;

import com.majruszs_difficulty.Instances;
import com.mlib.MajruszLibrary;
import com.mlib.TimeConverter;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.EffectHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Inflicts Nausea and Slowness when player takes damage from falling. */
@Mod.EventBusSubscriber
public class FallDamageWithNegativeEffects {
	protected final ConfigGroup fallGroup;
	protected final AvailabilityConfig isNauseaEnabled;
	protected final AvailabilityConfig isSlownessEnabled;
	protected final DoubleConfig minimumDistance;

	public FallDamageWithNegativeEffects() {
		String nausea_comment = "Is applying Nausea enabled?";
		String slowness_comment = "Is applying Slowness enabled?";
		String distance_comment = "Minimum distance required for applying effects.";
		String group_comment = "Applies Nausea and Slowness on fall.";
		this.isNauseaEnabled = new AvailabilityConfig( "nausea", nausea_comment, false, true );
		this.isSlownessEnabled = new AvailabilityConfig( "slowness", slowness_comment, false, true );
		this.minimumDistance = new DoubleConfig( "minimum_distance", distance_comment, false, 7.0, 2.0, 100.0 );

		this.fallGroup = FEATURES_GROUP.addGroup( new ConfigGroup( "FallEffects", group_comment ) );
		this.fallGroup.addConfigs( this.isNauseaEnabled, this.isSlownessEnabled, this.minimumDistance );
	}

	@SubscribeEvent
	public static void onFall( LivingFallEvent event ) {
		double distance = event.getDistance();
		MajruszLibrary.LOGGER.debug( event.getDamageMultiplier() );
		if( distance < Instances.FALL_DAMAGE_EFFECTS.minimumDistance.get() || true )
			return;

		LivingEntity livingEntity = event.getEntityLiving();
		if( Instances.FALL_DAMAGE_EFFECTS.isSlownessEnabled.isEnabled() )
			EffectHelper.applyEffectIfPossible( livingEntity, Effects.SLOWNESS, TimeConverter.secondsToTicks( 10.0 + distance * 0.5 ),
				( int )( distance / 15.0 )
			);
		if( Instances.FALL_DAMAGE_EFFECTS.isNauseaEnabled.isEnabled() )
			EffectHelper.applyEffectIfPossible( livingEntity, Effects.NAUSEA, TimeConverter.secondsToTicks( 6.0 ), ( int )( distance / 5.0 ) );
	}
}
