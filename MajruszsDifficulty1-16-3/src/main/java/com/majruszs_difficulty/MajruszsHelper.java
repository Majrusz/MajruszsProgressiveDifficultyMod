package com.majruszs_difficulty;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class MajruszsHelper {
	public static boolean isHostile( LivingEntity livingEntity ) {
		ModifiableAttributeInstance damageAttribute = livingEntity.getAttribute( Attributes.field_233823_f_ );

		if( damageAttribute != null && damageAttribute.getValue() > 0.0D )
			return true;

		return false;
	}

	public static double getClampedRegionalDifficulty( LivingEntity livingEntity, ServerWorld world ) {
		return world.getDifficultyForLocation( new BlockPos( livingEntity.getPositionVec() ) )
			.getClampedAdditionalDifficulty();
	}

	public static final int ticksInSecond = 20;

	public static int secondsToTicks( double seconds ) {
		return ( int )( seconds * ticksInSecond );
	}

	public static final int ticksInMinute = ticksInSecond * 60;

	public static int minutesToTicks( double minutes ) {
		return ( int )( minutes * ticksInMinute );
	}
}
