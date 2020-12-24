package com.majruszs_difficulty.events;

import com.majruszs_difficulty.GameState;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpiderPoisonAttack {
	@SubscribeEvent
	public static void onAttack( LivingHurtEvent event ) {
		if( !GameState.atLeast( GameState.Mode.EXPERT ) )
			return;

		Entity attacker = event.getSource()
			.getTrueSource();
		LivingEntity target = event.getEntityLiving();

		if( attacker == null )
			return;

		if( !( attacker instanceof SpiderEntity ) )
			return;

		if( !( target.getEntityWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )target.getEntityWorld();
		Difficulty current = world.getDifficulty();

		int poisonDurationInTicks;
		if( current == Difficulty.NORMAL )
			poisonDurationInTicks = MajruszsHelper.secondsToTicks( 7.0D );
		else if( current == Difficulty.HARD )
			poisonDurationInTicks = MajruszsHelper.secondsToTicks( 15.0D );
		else
			return;

		double chance = MajruszsHelper.getClampedRegionalDifficulty( target, world ) * 0.25D;

		if( chance > MajruszsDifficulty.RANDOM.nextDouble() )
			target.addPotionEffect( new EffectInstance( Effects.POISON, poisonDurationInTicks, 0 ) );
	}
}
