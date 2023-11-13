package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.data.EffectDef;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.mlib.time.TimeHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import org.jetbrains.annotations.Nullable;

public class BleedingEffect extends MobEffect {
	public static boolean apply( LivingEntity target, @Nullable LivingEntity attacker ) {
		EffectDef effectDef = BleedingEffect.getCurrentEffect( GameStageHelper.determineGameStage( target.level(), target.position() ) );

		return target.addEffect( new MobEffectInstance( TimeHelper.toTicks( effectDef.duration ), effectDef.amplifier, attacker ) );
	}

	public static EffectDef getCurrentEffect( GameStage gameStage ) {
		return MajruszsDifficulty.CONFIG.bleeding.effects.get( gameStage );
	}

	public static boolean isEnabled() {
		return MajruszsDifficulty.CONFIG.bleeding.isEnabled;
	}

	public static boolean isImmune( LivingEntity entity ) {
		return MajruszsDifficulty.CONFIG.bleeding.areUndeadImmuneByDefault
			&& entity instanceof Mob mob
			&& mob.getMobType().equals( MobType.UNDEAD )
			|| MajruszsDifficulty.CONFIG.bleeding.immuneMobs.contains( entity.getType() );
	}

	public BleedingEffect() {
		super( MobEffectCategory.HARMFUL, 0xffdd5555 );
	}

	@Override
	public void applyEffectTick( LivingEntity entity, int amplifier ) {}

	@Override
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {}

	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		return false;
	}

	/** Bleeding effect instance that stores information about the causer of bleeding. (required for converting villager to zombie villager etc.) */
	public static class MobEffectInstance extends net.minecraft.world.effect.MobEffectInstance {
		public final @Nullable Entity damageSourceEntity;

		public MobEffectInstance( int duration, int amplifier, @Nullable LivingEntity attacker ) {
			super( MajruszsDifficulty.BLEEDING.get(), duration, amplifier, false, false, true );

			this.damageSourceEntity = attacker;
		}
	}
}
