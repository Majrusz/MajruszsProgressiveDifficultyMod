package com.majruszs_difficulty;

import com.mlib.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class MajruszsHelper {
	/**
	 Returns player from given damage source.
	 Returns null if casting was impossible.
	 */
	@Nullable
	public static Player getPlayerFromDamageSource( DamageSource damageSource ) {
		return damageSource.getEntity() instanceof Player ? ( Player )damageSource.getEntity() : null;
	}

	/**
	 Checking if given entity is human.

	 @param entity Entity to test.
	 */
	public static boolean isHuman( @Nullable Entity entity ) {
		return entity instanceof Player || entity instanceof Villager || entity instanceof Pillager || entity instanceof Witch;
	}

	/**
	 Checking if given entity is animal.

	 @param entity Entity to test.
	 */
	public static boolean isAnimal( @Nullable Entity entity ) {
		return entity instanceof Animal;
	}

	/** Adds tooltip to list if advanced tooltips are enabled. */
	public static void addAdvancedTooltip( List< Component > tooltip, TooltipFlag flag, String translationKey ) {
		if( flag.isAdvanced() )
			tooltip.add( new TranslatableComponent( translationKey ).withStyle( ChatFormatting.GRAY ) );
	}

	/** Adds multiple tooltips to list if advanced tooltips are enabled. */
	public static void addAdvancedTooltips( List< Component > tooltip, TooltipFlag flag, String... translationKeys ) {
		if( flag.isAdvanced() )
			for( String translationKey : translationKeys )
				addAdvancedTooltip( tooltip, flag, translationKey );
	}

	/** Returns formatted text with information that item is disabled. */
	public static MutableComponent getDisabledItemTooltip() {
		return new TranslatableComponent( "majruszs_difficulty.items.disabled_tooltip" ).withStyle( ChatFormatting.RED, ChatFormatting.BOLD );
	}

	/** Adds information that item is disabled if certain conditions are met. */
	public static void addExtraTooltipIfDisabled( List< Component > toolTip, boolean isEnabled ) {
		if( !isEnabled )
			toolTip.add( getDisabledItemTooltip() );
	}

	/** Teleports the target somewhere nearby. */
	public static boolean teleportNearby( LivingEntity target, ServerLevel world, double insideOffset, double outsideOffset ) {
		double distanceFactor = insideOffset;
		if( target.yOld + 8 > world.getHeight( Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ( int )target.xOld, ( int )target.zOld ) )
			distanceFactor = outsideOffset;

		Vec3 newPosition = Random.getRandomVector3d( -distanceFactor, distanceFactor, -1.0, 1.0, -distanceFactor, distanceFactor )
			.add( target.position() );
		double y = world.getHeight( Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ( int )newPosition.x, ( int )newPosition.z ) + 1;

		return !( y < 5 ) && target.randomTeleport( newPosition.x, target.yOld + 8 > y ? y : newPosition.y, newPosition.z, true );
	}
}
