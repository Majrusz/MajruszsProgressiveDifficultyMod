package com.majruszsdifficulty;

import com.mlib.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

	public static void addTranslatableText( List< Component > tooltip, String translationKey ) {
		tooltip.add( Component.translatable( translationKey ).withStyle( ChatFormatting.GRAY ) );
	}

	public static void addTranslatableTexts( List< Component > tooltip, String... translationKeys ) {
		for( String translationKey : translationKeys )
			addTranslatableText( tooltip, translationKey );
	}

	/** Adds tooltip to list if advanced tooltips are enabled. */
	public static void addAdvancedTranslatableText( List< Component > tooltip, TooltipFlag flag, String translationKey ) {
		if( flag.isAdvanced() )
			addTranslatableText( tooltip, translationKey );
	}

	/** Adds multiple tooltips to list if advanced tooltips are enabled. */
	public static void addAdvancedTranslatableTexts( List< Component > tooltip, TooltipFlag flag, String... translationKeys ) {
		if( flag.isAdvanced() )
			for( String translationKey : translationKeys )
				addAdvancedTranslatableText( tooltip, flag, translationKey );
	}

	/** Returns formatted text with information that item is disabled. */
	public static MutableComponent getDisabledItemComponent() {
		return Component.translatable( "majruszsdifficulty.items.disabled_tooltip" ).withStyle( ChatFormatting.RED, ChatFormatting.BOLD );
	}

	public static MutableComponent getMoreDetailsComponent() {
		return Component.translatable( "majruszsdifficulty.items.advanced_tooltip" ).withStyle( ChatFormatting.GRAY );
	}

	public static void addMoreDetailsText( List< Component > tooltip ) {
		tooltip.add( getMoreDetailsComponent() );
	}

	public static MutableComponent getEmptyLine() {
		return Component.literal( " " );
	}

	public static void addEmptyLine( List< Component > tooltip ) {
		tooltip.add( getEmptyLine() );
	}

	/** Teleports the target somewhere nearby. */
	public static boolean teleportNearby( LivingEntity target, ServerLevel world, double insideOffset, double outsideOffset ) {
		double distanceFactor = insideOffset;
		if( target.yOld + 8 > world.getHeight( Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ( int )target.xOld, ( int )target.zOld ) )
			distanceFactor = outsideOffset;

		Vec3 newPosition = Random.getRandomVector3d( -distanceFactor, distanceFactor, -1.0, 1.0, -distanceFactor, distanceFactor ).add( target.position() );
		double y = world.getHeight( Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ( int )newPosition.x, ( int )newPosition.z ) + 1;

		return !( y < 5 ) && target.randomTeleport( newPosition.x, target.yOld + 8 > y ? y : newPosition.y, newPosition.z, true );
	}
}
