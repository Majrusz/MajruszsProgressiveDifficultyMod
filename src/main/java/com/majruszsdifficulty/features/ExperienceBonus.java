package com.majruszsdifficulty.features;

import com.majruszsdifficulty.Instances;
import com.majruszsdifficulty.config.GameStateDoubleConfig;
import com.mlib.Random;
import com.mlib.config.ConfigGroup;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.majruszsdifficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Increasing experience from any source. */
@Mod.EventBusSubscriber
public class ExperienceBonus {
	protected final ConfigGroup experienceGroup;
	protected final GameStateDoubleConfig bonusMultiplier;

	public ExperienceBonus() {
		String expComment = "Extra experience multiplier.";
		String groupComment = "Experience bonuses.";
		this.bonusMultiplier = new GameStateDoubleConfig( "BonusMultiplier", expComment, 0.0, 0.25, 0.5, 0.0, 10.0 );

		this.experienceGroup = FEATURES_GROUP.addGroup( new ConfigGroup( "Experience", groupComment ) );
		this.experienceGroup.addConfig( this.bonusMultiplier );
	}

	@SubscribeEvent
	public static void onXPPickUp( PlayerXpEvent.PickupXp event ) {
		int bonusExperience = Instances.EXPERIENCE_BONUS.getExtraExperience( event.getOrb() );
		if( bonusExperience <= 0 )
			return;

		Player player = event.getPlayer();
		player.giveExperiencePoints( bonusExperience );
	}

	/** Returns final experience amount after applying game state bonus. */
	private int getExtraExperience( ExperienceOrb experienceOrb ) {
		return Random.roundRandomly( getExperienceMultiplier() * experienceOrb.getValue() );
	}

	/** Returns extra experience depending on current game state. */
	private double getExperienceMultiplier() {
		return this.bonusMultiplier.getCurrentGameStateValue();
	}
}
