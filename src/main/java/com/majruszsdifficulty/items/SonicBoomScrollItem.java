package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.annotations.AutoInstance;
import com.mlib.effects.ParticleHandler;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnItemAttributeTooltip;
import com.mlib.math.AnyPos;
import com.mlib.math.Range;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SonicBoomScrollItem extends ScrollItem {
	public static int ATTACK_DAMAGE = 16;
	public static Range< Integer > ATTACK_RANGE = new Range<>( 12, 30 );

	@Override
	protected void useScroll( ItemStack itemStack, Level level, LivingEntity entity, float useRatio ) {
		super.useScroll( itemStack, level, entity, useRatio );

		if( !( level instanceof ServerLevel serverLevel ) ) {
			return;
		}

		AnyPos direction = AnyPos.from( 1, 0, 0 ).rot( EntityHelper.getLookRotation( entity ) );
		for( int i = 0; i < ( int )Mth.lerp( useRatio, ATTACK_RANGE.from, ATTACK_RANGE.to ); ++i ) {
			Vec3 position = direction.mul( i ).add( entity.getEyePosition( 0.75f ) ).vec3();

			ParticleHandler.SONIC_BOOM.spawn( serverLevel, position, 1 );
			EntityHelper.getEntitiesInBox( LivingEntity.class, serverLevel, position, 4.0, target->!target.equals( entity ) )
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

	@AutoInstance
	public static class Tooltip {
		public Tooltip() {
			OnItemAttributeTooltip.listen( this::addSpellInfo )
				.addCondition( Condition.predicate( data->data.itemStack.is( Registries.SONIC_BOOM_SCROLL.get() ) ) );
		}

		private void addSpellInfo( OnItemAttributeTooltip.Data data ) {
			List.of(
				Component.translatable( "majruszsdifficulty.scrolls.attack_damage", ATTACK_DAMAGE ).withStyle( ChatFormatting.DARK_GREEN ),
				Component.translatable( "majruszsdifficulty.scrolls.attack_range", "%d-%d".formatted( ATTACK_RANGE.from, ATTACK_RANGE.to ) )
					.withStyle( ChatFormatting.DARK_GREEN )
			).forEach( component->data.add( EquipmentSlot.MAINHAND, component ) );
		}
	}

}
