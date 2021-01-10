package com.majruszs_difficulty.events.attack_effects;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.ConfigHandler.Config;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class SpiderPoisonOnAttack extends OnAttackBase {
	public SpiderPoisonOnAttack() {
		super( SpiderEntity.class, GameState.Mode.EXPERT, true, Effects.POISON );
	}

	@Override
	protected boolean isEnabled() {
		return false;
	}

	@Override
	protected double getChance() {
		return 0;
	}

	@Override
	protected int getDurationInTicks() {
		return 0;
	}

	@Override
	protected int getAmplifier() {
		return 0;
	}
}
/*
@Mod.EventBusSubscriber
public class SpiderPoisonAttack {
	@SubscribeEvent
	public static void onAttack( LivingHurtEvent event ) {
		LivingEntity target = event.getEntityLiving();
		if( !shouldExecute( event.getSource(), target ) )
			return;

		ServerWorld world = ( ServerWorld )target.getEntityWorld();

		int poisonDurationInTicks = getPoisonDuration( world.getDifficulty() );

		if( getPoisonChance( target, world ) > MajruszsDifficulty.RANDOM.nextDouble() )
			target.addPotionEffect( new EffectInstance( Effects.POISON, poisonDurationInTicks, 0 ) );
	}

	protected static boolean shouldExecute( DamageSource source, LivingEntity target ) {
		Entity attacker = source.getTrueSource();

		if( !GameState.atLeast( GameState.Mode.EXPERT ) )
			return false;

		if( !( attacker instanceof SpiderEntity ) )
			return false;

		if( Config.isDisabled( Config.Features.SPIDER_POISON ) )
			return false;

		return target.getEntityWorld() instanceof ServerWorld;
	}

	protected static int getPoisonDuration( Difficulty difficulty ) {
		switch( difficulty ) {
			default:
				return 0;
			case NORMAL:
				return MajruszsHelper.secondsToTicks( 7.0 );
			case HARD:
				return MajruszsHelper.secondsToTicks( 15.0 );
		}
	}

	protected static double getPoisonChance( LivingEntity target, ServerWorld world ) {
		return MajruszsHelper.getClampedRegionalDifficulty( target, world ) * Config.getChance( Config.Chances.SPIDER_POISON );
	}
}
*/