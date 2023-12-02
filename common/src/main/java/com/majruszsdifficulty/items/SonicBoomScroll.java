package com.majruszsdifficulty.items;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnItemAttributeTooltip;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Range;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.data.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SonicBoomScroll extends ScrollItem {
	private static int ATTACK_DAMAGE = 14;
	private static Range< Integer > ATTACK_RANGE = Range.of( 12, 30 );

	static {
		OnItemAttributeTooltip.listen( SonicBoomScroll::addSpellInfo )
			.addCondition( data->data.itemStack.getItem() instanceof SonicBoomScroll );

		Serializables.getStatic( Config.Items.class )
			.define( "sonic_boom_scroll", SonicBoomScroll.class );

		Serializables.getStatic( SonicBoomScroll.class )
			.define( "attack_damage", Reader.integer(), ()->ATTACK_DAMAGE, v->ATTACK_DAMAGE = Range.of( 1, 100 ).clamp( v ) )
			.define( "attack_range", Reader.range( Reader.integer() ), ()->ATTACK_RANGE, v->ATTACK_RANGE = Range.of( 1, 100 ).clamp( v ) );
	}

	@Override
	protected void useScroll( ItemStack itemStack, Level level, LivingEntity entity, float useRatio ) {
		super.useScroll( itemStack, level, entity, useRatio );

		if( !Side.isLogicalServer() ) {
			return;
		}

		AnyPos direction = EntityHelper.getLookDirection( entity );
		int attackRange = ( int )ATTACK_RANGE.lerp( useRatio );
		for( int idx = 0; idx < attackRange; ++idx ) {
			Vec3 position = direction.mul( idx ).add( entity.getEyePosition( 0.75f ) ).vec3();

			ParticleEmitter.of( ParticleTypes.SONIC_BOOM )
				.position( position )
				.emit( level );
			level.getEntitiesOfClass( LivingEntity.class, AABB.ofSize( position, 4.0, 4.0, 4.0 ), target->!target.equals( entity ) )
				.forEach( target->{
					Vec3 knockbackDirection = direction.mul( -1.0, 0.0, -1.0 ).vec3();

					target.hurt( level.damageSources().indirectMagic( entity, entity ), ATTACK_DAMAGE );
					target.knockback( 1.0, knockbackDirection.x, knockbackDirection.z );
				} );
		}
	}

	@Override
	protected SoundEvent getPrepareSound() {
		return SoundEvents.WARDEN_SONIC_CHARGE;
	}

	@Override
	protected SoundEvent getCastSound() {
		return SoundEvents.WARDEN_SONIC_BOOM;
	}

	private static void addSpellInfo( OnItemAttributeTooltip data ) {
		List.of(
			TextHelper.translatable( "majruszsdifficulty.scrolls.attack_damage", ATTACK_DAMAGE ).withStyle( ChatFormatting.DARK_GREEN ),
			TextHelper.translatable( "majruszsdifficulty.scrolls.attack_range", "%d-%d".formatted( ATTACK_RANGE.from, ATTACK_RANGE.to ) )
				.withStyle( ChatFormatting.DARK_GREEN )
		).forEach( component->data.add( EquipmentSlot.MAINHAND, component ) );
	}
}
