package com.majruszsdifficulty.effects;

import com.majruszlibrary.entity.EffectDef;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.effects.bleeding.BleedingConfig;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.Nullable;

public class Bleeding extends MobEffect {
	public static boolean apply( LivingEntity target, @Nullable LivingEntity attacker ) {
		EffectDef effectDef = Bleeding.getCurrentEffect( GameStageHelper.determineGameStage( target.level(), target.position() ) );

		return target.addEffect( new MobEffectInstance( TimeHelper.toTicks( effectDef.duration ), effectDef.amplifier, attacker ) );
	}

	public static EffectDef getCurrentEffect( GameStage gameStage ) {
		return BleedingConfig.EFFECTS.get( gameStage );
	}

	public static boolean isEnabled() {
		return BleedingConfig.IS_ENABLED;
	}

	public static boolean canApplyTo( LivingEntity entity ) {
		return BleedingConfig.IS_APPLICABLE_TO_ANIMALS && entity instanceof Animal
			|| BleedingConfig.IS_APPLICABLE_TO_ILLAGERS && entity instanceof Mob mob && mob.getMobType() == MobType.ILLAGER
			|| BleedingConfig.OTHER_APPLICABLE_MOBS.contains( entity.getType() );
	}

	public Bleeding() {
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
			super( MajruszsDifficulty.Effects.BLEEDING.get(), duration, amplifier, false, false, true );

			this.damageSourceEntity = attacker;
		}
	}
}
