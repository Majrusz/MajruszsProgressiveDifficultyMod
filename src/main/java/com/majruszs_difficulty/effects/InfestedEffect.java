package com.majruszs_difficulty.effects;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.entities.ParasiteEntity;
import com.mlib.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/** Effect that spawns parasites after it expires. */
@Mod.EventBusSubscriber
public class InfestedEffect extends Effect {
	public InfestedEffect() {
		super( EffectType.HARMFUL, 0xff161616 );
	}
	/** Removes default milk bucket from curative items. */
	@Override
	public List< ItemStack > getCurativeItems() {

		return new ArrayList<>();
	}

	@SubscribeEvent
	public static void onEffectExpired( PotionEvent.PotionExpiryEvent event ) {
		EffectInstance effectInstance = event.getPotionEffect();
		LivingEntity target = event.getEntityLiving();
		if( effectInstance == null || !Instances.INFESTED.equals( effectInstance.getPotion() ) || !( target.world instanceof ServerWorld ) )
			return;

		BlockPos targetPosition = target.getPosition();
		ServerWorld world = ( ServerWorld )target.world;
		for( int i = 0; i < 2; ++i ) {
			Vector3d offset = Random.getRandomVector3d( -1.0, 1.0, 0.0, 0.0, -1.0, 1.0 );
			BlockPos parasitePosition = targetPosition.add( offset.x, 0.0, offset.z );
			Entity entity = ParasiteEntity.type.spawn( world, null, null, parasitePosition, SpawnReason.EVENT, true, true );
			if( !( entity instanceof ParasiteEntity ) )
				continue;

			ParasiteEntity parasite = ( ParasiteEntity )entity;
			parasite.setAttackTarget( target );
			ParasiteEntity.spawnEffects( world, parasitePosition );
		}
	}
}
