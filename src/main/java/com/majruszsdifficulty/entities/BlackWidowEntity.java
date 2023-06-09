package com.majruszsdifficulty.entities;

import com.majruszsdifficulty.Registries;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.SoundHandler;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnEntityTick;
import com.mlib.gamemodifiers.contexts.OnItemTooltip;
import com.mlib.math.Range;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class BlackWidowEntity extends Spider {
	public static Supplier< EntityType< BlackWidowEntity > > createSupplier() {
		return ()->EntityType.Builder.of( BlackWidowEntity::new, MobCategory.MONSTER )
			.sized( 0.7f, 0.35f )
			.build( "black_widow" );
	}

	public static AttributeSupplier getAttributeMap() {
		return Mob.createMobAttributes()
			.add( Attributes.MAX_HEALTH, 12.0 )
			.add( Attributes.MOVEMENT_SPEED, 0.3 )
			.add( Attributes.ATTACK_DAMAGE, 4.0 )
			.build();
	}

	public BlackWidowEntity( EntityType< ? extends BlackWidowEntity > type, Level world ) {
		super( type, world );
	}

	@Override
	public int getExperienceReward() {
		return Random.nextInt( 3 );
	}

	@Override
	public void playSound( SoundEvent sound, float volume, float pitch ) {
		if( this.isSilent() ) {
			return;
		}
		float randomizedVolume = SoundHandler.randomized( volume * 0.6f ).get();
		float randomizedPitch = SoundHandler.randomized( pitch * 1.4f ).get();

		this.level().playSound( null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), randomizedVolume, randomizedPitch );
	}

	@Override
	protected float getStandingEyeHeight( Pose pose, EntityDimensions dimensions ) {
		return 0.2f;
	}

	@AutoInstance
	public static class WebAbility {
		final DoubleConfig delay = new DoubleConfig( 30.0, new Range<>( 5.0, 600.0 ) );

		public WebAbility() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.DEFAULT )
				.name( "BlackWidowWebAbility" )
				.comment( "Black Widow spawns the web when in combat." );

			OnEntityTick.listen( this::spawnWeb )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.excludable() )
				.addCondition( Condition.predicate( data->data.entity instanceof BlackWidowEntity ) )
				.addCondition( Condition.predicate( this::ticksHavePassed ) )
				.addConfig( this.delay.name( "delay" ).comment( "Duration between creating a new web (in seconds)." ) )
				.insertTo( group );
		}

		private void spawnWeb( OnEntityTick.Data data ) {
			data.getServerLevel().setBlock( data.entity.blockPosition(), Blocks.COBWEB.defaultBlockState(), 3 );
		}

		private boolean ticksHavePassed( OnEntityTick.Data data ) {
			return data.entity.tickCount % this.delay.asTicks() == 0;
		}
	}

	@Deprecated
	@AutoInstance
	public static class TempTooltip {
		public TempTooltip() {
			OnItemTooltip.listen( this::addTooltip )
				.addCondition( Condition.predicate( data->data.itemStack.getItem().equals( Registries.BLACK_WIDOW_SPAWN_EGG.get() ) ) );
		}

		private void addTooltip( OnItemTooltip.Data data ) {
			data.tooltip.add( Component.literal( "This mob is not finished yet, coming soon in the next major update!" )
				.withStyle( ChatFormatting.RED ) );
		}
	}
}
