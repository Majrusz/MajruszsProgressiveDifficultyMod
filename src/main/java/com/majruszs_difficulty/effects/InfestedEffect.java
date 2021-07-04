package com.majruszs_difficulty.effects;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.entities.ParasiteEntity;
import com.mlib.Random;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.effects.EffectHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Effect that spawns parasites after it expires. */
@Mod.EventBusSubscriber
public class InfestedEffect extends Effect {
	protected final ConfigGroup effectGroup;
	protected final DurationConfig duration;
	protected final DoubleConfig damage;
	protected final IntegerConfig maximumAmplifier;

	public InfestedEffect() {
		super( EffectType.HARMFUL, 0xff161616 );

		String durationComment = "Duration of this effect whenever it is applied by Parasite. (in seconds)";
		this.duration = new DurationConfig( "duration", durationComment, false, 15.0, 1.0, 120.0 );

		String damageComment = "Damage dealt after this effect expires. (per level) (1.0 = half a heart)";
		this.damage = new DurationConfig( "damage", damageComment, false, 1.0, 0.0, 20.0 );

		String amplifierComment = "Maximum amplifier that can be applied by Parasite.";
		this.maximumAmplifier = new IntegerConfig( "maximum_amplifier", amplifierComment, false, 4, 0, 100 );

		this.effectGroup = FEATURES_GROUP.addGroup( new ConfigGroup( "Infested", "Infested potion effect." ) );
		this.effectGroup.addConfigs( this.duration, this.damage, this.maximumAmplifier );
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
		InfestedEffect infestedEffect = Instances.INFESTED;
		if( effectInstance == null || !infestedEffect.equals( effectInstance.getPotion() ) || !( target.world instanceof ServerWorld ) )
			return;

		infestedEffect.spawnParasites( effectInstance, target, ( ServerWorld )target.world );
		infestedEffect.damageEntity( effectInstance, target );
	}

	/** Applies Infested on given target or if it has one then it increases the amplifier. */
	public void applyTo( LivingEntity target ) {
		EffectInstance currentEffect = target.getActivePotionEffect( this );
		int amplifier = currentEffect != null ? Math.min( currentEffect.getAmplifier() + 1, this.maximumAmplifier.get() ) : 0;

		EffectHelper.applyEffectIfPossible( target, this, this.duration.getDuration(), amplifier );
	}

	/** Checks whether given target can have Infested effect. */
	public boolean canBeAppliedTo( LivingEntity target ) {
		EffectInstance currentEffect = target.getActivePotionEffect( this );

		return currentEffect == null || currentEffect.getAmplifier() < this.maximumAmplifier.get();
	}

	/** Spawns parasites depending on potion amplifier and near the target. */
	protected void spawnParasites( EffectInstance effectInstance, LivingEntity target, ServerWorld world ) {
		int effectAmplifier = effectInstance.getAmplifier();
		BlockPos targetPosition = target.getPosition();
		for( int i = 0; i < effectAmplifier + 2; ++i ) {
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

	/** Damages the target depending on potion amplifier. */
	protected void damageEntity( EffectInstance effectInstance, LivingEntity target ) {
		target.attackEntityFrom( DamageSource.MAGIC, ( float )( ( effectInstance.getAmplifier() + 1 ) * this.damage.get() ) );
	}
}
