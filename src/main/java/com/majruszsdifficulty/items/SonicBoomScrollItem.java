package com.majruszsdifficulty.items;

import com.mlib.entities.EntityHelper;
import com.mlib.math.AnyPos;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SonicBoomScrollItem extends ScrollItem {
	@Override
	protected void useScroll( ItemStack itemStack, Level level, LivingEntity entity, float useRatio ) {
		super.useScroll( itemStack, level, entity, useRatio );

		if( !( level instanceof ServerLevel serverLevel ) ) {
			return;
		}

		AnyPos direction = AnyPos.from( 1, 0, 0 ).rot( EntityHelper.getLookRotation( entity ) );
		for( int i = 0; i < 16 + ( int )( 32 * useRatio ); ++i ) {
			Vec3 position = direction.mul( i ).add( entity.getEyePosition( 0.75f ) ).vec3();

			serverLevel.sendParticles( ParticleTypes.SONIC_BOOM, position.x, position.y, position.z, 1, 0.0, 0.0, 0.0, 0.0 );
			EntityHelper.getEntitiesInBox( LivingEntity.class, serverLevel, position, 2.0, target->!target.equals( entity ) )
				.forEach( target->{
					Vec3 knockbackDirection = direction.mul( -1.0, 0.0, -1.0 ).vec3();

					target.hurt( level.damageSources().sonicBoom( entity ), 14.0f );
					target.knockback( 1.0, knockbackDirection.x, knockbackDirection.z );
				} );
		}
	}

	@Override
	protected Component getEffectTooltip() {
		return Component.translatable( "item.majruszsdifficulty.sonic_boom_scroll.effect" ).withStyle( ChatFormatting.BLUE );
	}

	@Override
	protected SoundEvent getPrepareSound() {
		return SoundEvents.WARDEN_SONIC_CHARGE;
	}

	@Override
	protected SoundEvent getCastSound() {
		return SoundEvents.WARDEN_SONIC_BOOM;
	}
}
