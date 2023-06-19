package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.data.SerializableHelper;
import com.mlib.data.SerializableStructure;
import com.mlib.effects.SoundHandler;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnPreDamaged;
import com.mlib.items.ItemHelper;
import com.mlib.levels.LevelHelper;
import com.mlib.math.AnyPos;
import com.mlib.math.Range;
import com.mlib.time.TimeHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.IArmPoseTransformer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EvokerFangScrollItem extends Item {
	public static final SoundHandler PREPARE_SOUND = new SoundHandler( SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.PLAYERS );
	public static final SoundHandler CAST_SOUND = new SoundHandler( SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS );

	public EvokerFangScrollItem() {
		super( new Properties().rarity( Rarity.UNCOMMON ).stacksTo( 1 ) );
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level level, Player player, InteractionHand hand ) {
		PREPARE_SOUND.play( level, player.position() );

		return ItemUtils.startUsingInstantly( level, player, hand );
	}

	@Override
	public ItemStack finishUsingItem( ItemStack itemStack, Level level, LivingEntity entity ) {
		this.summonEvokerFangs( level, entity, this.getUseDuration( itemStack ) / 10 );

		return itemStack;
	}

	@Override
	public void releaseUsing( ItemStack itemStack, Level level, LivingEntity entity, int ticksLeft ) {
		this.summonEvokerFangs( level, entity, ( this.getUseDuration( itemStack ) - ticksLeft ) / 10 );
	}

	@Override
	public UseAnim getUseAnimation( ItemStack itemStack ) {
		return UseAnim.CUSTOM;
	}

	@Override
	public int getUseDuration( ItemStack itemStack ) {
		return Utility.secondsToTicks( 3.0 );
	}

	@Override
	public void appendHoverText( ItemStack itemStack, @Nullable Level level, List< Component > components, TooltipFlag flag ) {
		components.add( CommonComponents.EMPTY );
		components.add( Component.translatable( "potion.whenDrank" ).withStyle( ChatFormatting.DARK_PURPLE ) );
		components.add( Component.translatable( "item.majruszsdifficulty.evoker_fang_scroll.effect" ).withStyle( ChatFormatting.BLUE ) );
	}

	@Override
	public void initializeClient( Consumer< IClientItemExtensions > consumer ) {
		consumer.accept( new IClientItemExtensions() {
			public static final HumanoidModel.ArmPose SPELL_ARM_POSE = HumanoidModel.ArmPose.create( Registries.getLocationString( "spell" ), true, new IArmPoseTransformer() {
				@Override
				public void applyTransform( HumanoidModel< ? > model, LivingEntity entity, HumanoidArm arm ) {
					if( !entity.isUsingItem() ) {
						return;
					}

					float ticks = entity.getTicksUsingItem() + TimeHelper.getPartialTicks();
					model.leftArm.x = 5.0f;
					model.leftArm.z = 0.0f;
					model.leftArm.xRot = Mth.cos( ticks * 0.6662f ) * 0.25f;
					model.leftArm.yRot = 0.0f;
					model.leftArm.zRot = -2.3561945f;
					model.rightArm.x = -5.0f;
					model.rightArm.z = 0.0f;
					model.rightArm.xRot = Mth.cos( ticks * 0.6662f ) * 0.25f;
					model.rightArm.yRot = 0.0f;
					model.rightArm.zRot = 2.3561945f;
				}
			} );

			@Override
			public boolean applyForgeHandTransform( PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick,
				float equippedProgress, float swingProgress
			) {
				if( player.isUsingItem() && player.getUseItem().equals( stack ) ) {
					pose.translate( arm == HumanoidArm.RIGHT ? 0.56 : -0.56, -0.52 - 0.1 * Math.sin( TimeHelper.getClientTicks() + partialTick ), -0.72 );
					return true;
				}

				return false;
			}

			@Override
			public HumanoidModel.ArmPose getArmPose( LivingEntity entity, InteractionHand hand, ItemStack stack ) {
				return SPELL_ARM_POSE;
			}
		} );
	}

	private void summonEvokerFangs( Level level, LivingEntity entity, int extraDamage ) {
		CAST_SOUND.play( level, entity.position() );
		double rotation = Math.toRadians( entity.getYRot() ) + Math.PI / 2.0;
		this.getAttackPattern( entity, rotation ).forEach( spawnPoint->{
			EvokerFangs evokerFangs = new EvokerFangs( level, spawnPoint.pos.x, spawnPoint.pos.y, spawnPoint.pos.z, ( float )rotation, spawnPoint.cooldown, entity );
			SerializableHelper.modify( DamageInfo::new, evokerFangs.getPersistentData(), damageInfo->{
				damageInfo.extraDamage = extraDamage;
			} );

			level.addFreshEntity( evokerFangs );
		} );
		this.disableItemFor( entity, Utility.secondsToTicks( 8.0 ) );
	}

	private void disableItemFor( LivingEntity entity, int ticks ) {
		if( entity instanceof Player player ) {
			ItemHelper.addCooldown( player, ticks, Registries.EVOKER_FANG_SCROLL.get() );
		}
	}

	private List< SpawnPoint > getAttackPattern( LivingEntity entity, double rotation ) {
		List< SpawnPoint > spawnPoints = new ArrayList<>();
		for( int x = 0; x <= 16; ++x ) {
			for( int z = -1; z <= 1; ++z ) {
				int cooldown = Math.abs( x ) * 2 + 8;
				Vec3 position = AnyPos.from( entity.position() ).floor().add( AnyPos.from( x + 1, 0, z ).rot2d( rotation ).round() ).vec3();
				LevelHelper.findBlockPosOnGround( entity.level(), position.x, new Range<>( position.y - 3, position.y + 3 ), position.z )
					.ifPresent( blockPos->spawnPoints.add( new SpawnPoint( AnyPos.from( blockPos ).add( 0.5, 0.0, 0.5 ).vec3(), cooldown ) ) );
			}
		}

		return spawnPoints;
	}

	private record SpawnPoint( Vec3 pos, int cooldown ) {}

	@AutoInstance
	public static class Spell {
		public Spell() {
			OnPreDamaged.listen( this::increaseDamage )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->data.source.getDirectEntity() instanceof EvokerFangs ) )
				.addCondition( OnPreDamaged.dealtAnyDamage() );
		}

		private void increaseDamage( OnPreDamaged.Data data ) {
			DamageInfo damageInfo = SerializableHelper.read( DamageInfo::new, data.source.getDirectEntity().getPersistentData() );

			data.extraDamage += damageInfo.extraDamage;
		}
	}

	private static class DamageInfo extends SerializableStructure {
		int extraDamage = 0;

		public DamageInfo() {
			this.define( "MajruszsProgressiveDifficultyEvokerFangDamage", ()->this.extraDamage, x->this.extraDamage = x );
		}
	}
}
