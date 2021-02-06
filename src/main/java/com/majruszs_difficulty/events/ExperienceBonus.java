package com.majruszs_difficulty.events;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.config.GameStateDoubleConfig;
import com.mlib.Random;
import com.mlib.config.ConfigGroup;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Increasing experience from any source. */
@Mod.EventBusSubscriber
public class ExperienceBonus {
	protected final ConfigGroup experienceGroup;
	protected final GameStateDoubleConfig bonusMultiplier;

	public ExperienceBonus() {
		String exp_comment = "Extra experience multiplier.";
		String group_comment = "Experience bonuses.";
		this.bonusMultiplier = new GameStateDoubleConfig( "BonusMultiplier", exp_comment, 0.0, 0.25, 0.5, 0.0, 10.0 );

		this.experienceGroup = FEATURES_GROUP.addGroup( new ConfigGroup( "Experience", group_comment ) );
		this.experienceGroup.addConfig( this.bonusMultiplier );
	}

	@SubscribeEvent
	public static void onXPPickUp( PlayerXpEvent.PickupXp event ) {
		int bonusExperience = Instances.EXPERIENCE_BONUS.getExtraExperience( event.getOrb() );
		if( bonusExperience <= 0 )
			return;

		PlayerEntity player = event.getPlayer();
		player.giveExperiencePoints( bonusExperience );
	}

	/** Returns final experience amount after applying game state bonus. */
	private int getExtraExperience( ExperienceOrbEntity experienceOrb ) {
		return Random.randomizeExperience( getExperienceMultiplier() * experienceOrb.getXpValue() );
	}

	/** Returns extra experience depending on current game state. */
	private double getExperienceMultiplier() {
		return this.bonusMultiplier.getCurrentGameStateValue();
	}
}
