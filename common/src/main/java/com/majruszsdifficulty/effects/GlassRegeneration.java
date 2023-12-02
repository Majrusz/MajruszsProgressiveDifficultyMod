package com.majruszsdifficulty.effects;

import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.entity.EffectHelper;
import com.majruszlibrary.events.OnEntityDamaged;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class GlassRegeneration extends MobEffect {
	private static final SoundEmitter GLASS_BREAK = SoundEmitter.of( SoundEvents.GLASS_BREAK )
		.source( SoundSource.PLAYERS )
		.volume( SoundEmitter.randomized( 0.25f ) );

	static {
		OnEntityDamaged.listen( GlassRegeneration::removeOnHit )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->EffectHelper.has( MajruszsDifficulty.GLASS_REGENERATION_EFFECT, data.target ) );
	}

	public GlassRegeneration() {
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

	private static void removeOnHit( OnEntityDamaged data ) {
		data.target.removeEffect( MajruszsDifficulty.GLASS_REGENERATION_EFFECT.get() );
		GLASS_BREAK.position( data.target.position() ).emit( data.getServerLevel() );
	}
}
