package com.majruszsdifficulty.features.end_items.when_damaged;

import com.majruszsdifficulty.features.end_items.EndItems;
import com.majruszsdifficulty.features.when_damaged.WhenDamagedApplyBleedingBase;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/** Makes certain End Tools have an extra chance to inflict Bleeding effect. */
public class EndToolsBleedingOnHurt extends WhenDamagedApplyBleedingBase {
	private static final String CONFIG_NAME = "EndToolsBleeding";
	private static final String CONFIG_COMMENT = "End Axe, End Sword and End Hoe have a greater chance to inflict Bleeding.";

	public EndToolsBleedingOnHurt() {
		super( CONFIG_NAME, CONFIG_COMMENT, 0.5, 24.0 );
	}

	/** Checking if all conditions were met. */
	@Override
	public boolean shouldBeExecuted( @Nullable LivingEntity attacker, LivingEntity target, DamageSource damageSource ) {
		return attacker != null && EndItems.haveExtraBleedingChance( attacker.getMainHandItem().getItem() ) && super.shouldBeExecuted( attacker,
			target, damageSource
		);
	}
}