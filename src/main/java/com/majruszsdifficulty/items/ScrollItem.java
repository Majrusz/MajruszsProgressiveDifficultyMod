package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.Utility;
import com.mlib.effects.SoundHandler;
import com.mlib.items.ItemHelper;
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
	public static final int MIN_DURATION = Utility.secondsToTicks( 1.0 );

	public ScrollItem() {
		super( new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ).stacksTo( 1 ) );
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
			this.disableItem( itemStack, entity, Utility.secondsToTicks( 1.0 ) );
			return;
		}

		float useRatio = Mth.clamp( 1.0f - ( float )ticksLeft / ( this.getUseDuration( itemStack ) - MIN_DURATION ), 0.0f, 1.0f );
		this.useScroll( itemStack, level, entity, useRatio );
	}

	@Override
	public UseAnim getUseAnimation( ItemStack itemStack ) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration( ItemStack itemStack ) {
		return Utility.secondsToTicks( 3.0 );
	}

	protected void useScroll( ItemStack itemStack, Level level, LivingEntity entity, float useRatio ) {
		this.disableItem( itemStack, entity, Utility.secondsToTicks( 16.0 ) );
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

	protected abstract SoundEvent getPrepareSound();

	protected abstract SoundEvent getCastSound();
}
