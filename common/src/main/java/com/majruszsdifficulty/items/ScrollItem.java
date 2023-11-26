package com.majruszsdifficulty.items;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.events.OnEntityModelSetup;
import com.majruszlibrary.events.OnItemRendered;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.platform.Side;
import com.majruszlibrary.time.TimeHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public abstract class ScrollItem extends Item {
	protected static final int MIN_DURATION = TimeHelper.toTicks( 1.0 );

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
		if( ( this.getUseDuration( itemStack ) - ticksLeft ) < MIN_DURATION ) {
			this.disableItem( itemStack, entity, TimeHelper.toTicks( 1.0 ) );
			return;
		}

		float useRatio = Mth.clamp( 1.0f - ( float )ticksLeft / ( this.getUseDuration( itemStack ) - MIN_DURATION ), 0.0f, 1.0f );
		this.useScroll( itemStack, level, entity, useRatio );
	}

	@Override
	public UseAnim getUseAnimation( ItemStack itemStack ) {
		return UseAnim.NONE;
	}

	@Override
	public int getUseDuration( ItemStack itemStack ) {
		return TimeHelper.toTicks( 3.0 );
	}

	protected void useScroll( ItemStack itemStack, Level level, LivingEntity entity, float useRatio ) {
		this.disableItem( itemStack, entity, TimeHelper.toTicks( 16.0 ) );
		this.playSound( this::getCastSound, level, entity, 2.0f );
	}

	protected void disableItem( ItemStack itemStack, LivingEntity entity, int ticks ) {
		if( entity instanceof Player player ) {
			ItemHelper.addCooldown( player, ticks, itemStack.getItem() );
		}
	}

	protected void playSound( Supplier< SoundEvent > sound, Level level, LivingEntity entity, float volumeScale ) {
		SoundEmitter.of( sound )
			.source( SoundSource.PLAYERS )
			.volume( SoundEmitter.randomized( volumeScale ) )
			.position( entity.position() )
			.emit( level );
	}

	protected abstract SoundEvent getPrepareSound();

	protected abstract SoundEvent getCastSound();

	@OnlyIn( Dist.CLIENT )
	private static class Client {
		static {
			OnEntityModelSetup.listen( Client::modify )
				.addCondition( data->data.entity.isUsingItem() )
				.addCondition( data->data.entity.getUseItem().getItem() instanceof ScrollItem );

			OnItemRendered.listen( Client::modify )
				.addCondition( data->data.player.isUsingItem() )
				.addCondition( data->data.player.getUseItem().equals( data.itemStack ) )
				.addCondition( data->data.itemStack.getItem() instanceof ScrollItem );
		}

		private static void modify( OnEntityModelSetup data ) {
			float ticks = data.entity.getTicksUsingItem() + TimeHelper.getPartialTicks();
			data.model.leftArm.x = 5.0f;
			data.model.leftArm.z = 0.0f;
			data.model.leftArm.xRot = Mth.cos( ticks * 0.6662f ) * 0.25f;
			data.model.leftArm.yRot = 0.0f;
			data.model.leftArm.zRot = -2.3561945f;
			data.model.rightArm.x = -5.0f;
			data.model.rightArm.z = 0.0f;
			data.model.rightArm.xRot = Mth.cos( ticks * 0.6662f ) * 0.25f;
			data.model.rightArm.yRot = 0.0f;
			data.model.rightArm.zRot = 2.3561945f;
		}

		private static void modify( OnItemRendered data ) {
			data.poseStack.translate( 0.0, - 0.1 * Math.sin( Side.getLocalPlayer().getTicksUsingItem() + TimeHelper.getPartialTicks() ), 0.0 );
		}
	}
}