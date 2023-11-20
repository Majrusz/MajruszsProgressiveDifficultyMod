package com.majruszsdifficulty.effects;

import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.data.EffectDef;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BleedingEffect extends MobEffect {
	public static boolean apply( LivingEntity target, @Nullable LivingEntity attacker ) {
		EffectDef effectDef = BleedingEffect.getCurrentEffect( GameStageHelper.determineGameStage( target.level(), target.position() ) );

		return target.addEffect( new MobEffectInstance( TimeHelper.toTicks( effectDef.duration ), effectDef.amplifier, attacker ) );
	}

	public static EffectDef getCurrentEffect( GameStage gameStage ) {
		return Config.EFFECTS.get( gameStage );
	}

	public static boolean isEnabled() {
		return Config.IS_ENABLED;
	}

	public static boolean isImmune( LivingEntity entity ) {
		return Config.ARE_UNDEAD_IMMUNE_BY_DEFAULT
			&& entity instanceof Mob mob
			&& mob.getMobType().equals( MobType.UNDEAD )
			|| Config.IMMUNE_MOBS.contains( entity.getType() );
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

	public static class Config {
		public static boolean IS_ENABLED = true;
		public static boolean ARE_UNDEAD_IMMUNE_BY_DEFAULT = true;
		public static List< EntityType< ? > > IMMUNE_MOBS = new ArrayList<>();
		public static GameStageValue< EffectDef > EFFECTS = GameStageValue.of(
			DefaultMap.defaultEntry( new EffectDef( MajruszsDifficulty.BLEEDING, 0, 24.0f ) ),
			DefaultMap.entry( GameStage.EXPERT_ID, new EffectDef( MajruszsDifficulty.BLEEDING, 1, 24.0f ) ),
			DefaultMap.entry( GameStage.MASTER_ID, new EffectDef( MajruszsDifficulty.BLEEDING, 2, 24.0f ) )
		);

		static {
			Serializables.getStatic( com.majruszsdifficulty.data.Config.class )
				.define( "bleeding", Config.class );

			Serializables.getStatic( Config.class )
				.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
				.define( "are_undead_immune_by_default", Reader.bool(), ()->ARE_UNDEAD_IMMUNE_BY_DEFAULT, v->ARE_UNDEAD_IMMUNE_BY_DEFAULT = v )
				.define( "immune_mobs", Reader.list( Reader.entityType() ), ()->IMMUNE_MOBS, v->IMMUNE_MOBS = v )
				.define( "effect", Reader.map( Reader.custom( EffectDef::new ) ), ()->EFFECTS.get(), v->EFFECTS.set( v ) )
				.define( "sources", Sources.class );
		}

		public static class Sources {}
	}
}
