package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.effects.SoundHandler;
import com.mlib.items.ItemHelper;
import com.mlib.time.TimeHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IArmPoseTransformer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ScrollItem extends Item {
	public ScrollItem() {
		super( new Properties().rarity( Rarity.UNCOMMON ).stacksTo( 1 ) );
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level level, Player player, InteractionHand hand ) {
		this.playSound( this::getPrepareSound, level, player, 1.0f );

		return ItemUtils.startUsingInstantly( level, player, hand );
	}

	@Override
	public ItemStack finishUsingItem( ItemStack itemStack, Level level, LivingEntity entity ) {
		this.useScroll( itemStack, level, entity, 1.0f );

		return itemStack;
	}

	@Override
	public void releaseUsing( ItemStack itemStack, Level level, LivingEntity entity, int ticksLeft ) {
		if( ( this.getUseDuration( itemStack ) - ticksLeft ) < 20 ) {
			this.disableItem( itemStack, entity, Utility.secondsToTicks( 1.0 ) );
			return;
		}

		float useRatio = Mth.clamp( 1.0f - ( float )ticksLeft / this.getUseDuration( itemStack ), 0.0f, 1.0f );
		this.useScroll( itemStack, level, entity, useRatio );
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
		components.add( this.getEffectTooltip() );
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

	protected void useScroll( ItemStack itemStack, Level level, LivingEntity entity, float useRatio ) {
		this.disableItem( itemStack, entity, Utility.secondsToTicks( 12.0 ) );
		this.playSound( this::getCastSound, level, entity, 2.0f );
	}

	protected void playSound( Supplier< SoundEvent > sound, Level level, LivingEntity entity, float volumeScale ) {
		new SoundHandler( sound.get(), SoundSource.PLAYERS, SoundHandler.randomized( volumeScale ) ).play( level, entity.position() );
	}

	protected void disableItem( ItemStack itemStack, LivingEntity entity, int ticks ) {
		if( entity instanceof Player player ) {
			ItemHelper.addCooldown( player, ticks, itemStack.getItem() );
		}
	}

	protected abstract Component getEffectTooltip();

	protected abstract SoundEvent getPrepareSound();

	protected abstract SoundEvent getCastSound();
}
