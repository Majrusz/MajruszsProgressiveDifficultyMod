package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

/** Potion that will teleport the player to spawn/bed position after drinking it. */
public class RecallPotionItem extends Item {
	public RecallPotionItem() {
		super( new Item.Properties().group( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON )
			.maxStackSize( 16 ) );
	}

	@Override
	public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity player, Hand hand ) {
		return DrinkHelper.startDrinking( world, player, hand );
	}

	@Override
	public ItemStack onItemUseFinish( ItemStack itemStack, World world, LivingEntity livingEntity ) {
		PlayerEntity player = livingEntity instanceof PlayerEntity ? ( PlayerEntity )livingEntity : null;
		if( player instanceof ServerPlayerEntity )
			CriteriaTriggers.CONSUME_ITEM.trigger( ( ServerPlayerEntity )player, itemStack );

		if( player != null ) {
			player.addStat( Stats.ITEM_USED.get( this ) );
			if( !player.abilities.isCreativeMode )
				itemStack.shrink( 1 );

			if( world instanceof ServerWorld && player instanceof ServerPlayerEntity ) {
				ServerWorld serverWorld = ( ServerWorld )world;
				ServerPlayerEntity serverPlayer = ( ServerPlayerEntity )player;

				BlockPos spawnPosition = serverPlayer.func_241140_K_();
				Optional< BlockPos > position = Optional.empty();
				if( spawnPosition != null ) {
					Optional< Vector3d > newPosition = ServerPlayerEntity.func_242374_a( serverWorld, spawnPosition, serverPlayer.func_242109_L(),
						serverPlayer.func_241142_M_(), true
					);
					if( newPosition.isPresent() )
						position = Optional.of( new BlockPos( newPosition.get() ) );
				}

				BlockPos finalSpawnPosition = position.orElseGet( serverWorld::getSpawnPoint );
				player.attemptTeleport( finalSpawnPosition.getX() + 0.5, finalSpawnPosition.getY() + 1.0, finalSpawnPosition.getZ() + 0.5, true );
			}
		}

		return itemStack;
	}

	@Override
	public UseAction getUseAction( ItemStack itemStack ) {
		return UseAction.DRINK;
	}

	@Override
	public int getUseDuration( ItemStack itemStack ) {
		return 32;
	}

	public boolean hasEffect( ItemStack itemStack ) {
		return true;
	}
}
