package com.majruszsdifficulty.effects;

import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.contexts.OnEntityDamaged;
import com.majruszlibrary.contexts.base.Condition;
import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.entity.EffectHelper;
import com.majruszlibrary.time.TimeHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class GlassRegenerationEffect extends MobEffect {
	public GlassRegenerationEffect() {
		super( MobEffectCategory.BENEFICIAL, 0xffcd5cab );
	}

	@Override
	public void applyEffectTick( LivingEntity entity, int amplifier ) {
		if( entity.getHealth() < entity.getMaxHealth() ) {
			entity.heal( 1.0f );
		}
	}

	@Override
	public void applyInstantenousEffect( @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {
		this.applyEffectTick( entity, amplifier );
	}

	@Override
	public boolean isDurationEffectTick( int duration, int amplifier ) {
		int cooldown = TimeHelper.toTicks( 8.0 ) >> amplifier;

		return cooldown <= 0 || duration % cooldown == 0;
	}

	@AutoInstance
	public static class GlassRegeneration {
		private static final SoundEmitter GLASS_BREAK = SoundEmitter.of( SoundEvents.GLASS_BREAK )
			.source( SoundSource.PLAYERS )
			.volume( SoundEmitter.randomized( 0.25f ) );

		public GlassRegeneration() {
			OnEntityDamaged.listen( this::removeEffect )
				.addCondition( Condition.isLogicalServer() )
				.addCondition( data->EffectHelper.has( MajruszsDifficulty.GLASS_REGENERATION, data.target ) );
		}

		private void removeEffect( OnEntityDamaged data ) {
			data.target.removeEffect( MajruszsDifficulty.GLASS_REGENERATION.get() );
			GLASS_BREAK.position( data.target.position() ).emit( data.getServerLevel() );
		}
	}
}
