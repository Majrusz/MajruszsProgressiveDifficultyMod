package com.majruszsdifficulty.items;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.level.BlockHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EnderiumShardLocator extends Item {
	private static final int OFFSET = 26;

	static {
		Serializables.get( ItemInfo.class )
			.define( "EnderiumShardLocatorPos", Reader.optional( Reader.blockPos() ), s->s.position, ( s, v )->s.position = v )
			.define( "EnderiumShardLocatorCounter", Reader.integer(), s->s.counter, ( s, v )->s.counter = v );
	}

	@OnlyIn( Dist.CLIENT )
	public static float getShardDistance( ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed ) {
		if( !( entity instanceof Player player ) || level == null ) {
			return 1.0f;
		}

		if( player.getInventory().findSlotMatchingItem( itemStack ) == -1 && !player.getOffhandItem().equals( itemStack ) ) {
			return 1.0f;
		}

		ItemInfo itemInfo = new ItemInfo();
		Serializables.modify( itemInfo, itemStack.getOrCreateTag(), info->{
			if( itemInfo.position != null && !EnderiumShardLocator.isOre( level, itemInfo.position ) ) {
				info.position = null;
			}
			info.position = EnderiumShardLocator.findNearestOre( level, player, info );
			info.counter = ( info.counter + 1 ) % ( 2 * OFFSET );
		} );

		if( itemInfo.position == null ) {
			return 1.0f;
		}

		return ( float )Mth.clamp( AnyPos.from( player.position() ).dist( itemInfo.position ).doubleValue() / OFFSET, 0.0f, 1.0f );
	}

	public EnderiumShardLocator() {
		super( new Properties().rarity( Rarity.UNCOMMON ) );
	}

	@OnlyIn( Dist.CLIENT )
	private static BlockPos findNearestOre( ClientLevel level, Player player, ItemInfo itemInfo ) {
		BlockPos nearestPosition = itemInfo.position;
		float nearestDistance = nearestPosition != null ? AnyPos.from( player.position() ).dist( nearestPosition.getCenter() ).floatValue() : OFFSET;

		for( int x = -OFFSET; x < OFFSET; ++x ) {
			for( int z = -OFFSET; z < OFFSET; ++z ) {
				BlockPos position = AnyPos.from( player.blockPosition() ).add( x, itemInfo.counter - OFFSET, z ).block();
				if( EnderiumShardLocator.isOre( level, position ) ) {
					float distance = AnyPos.from( player.position() ).dist( position.getCenter() ).floatValue();
					if( nearestDistance > distance ) {
						nearestPosition = position;
						nearestDistance = distance;
					}
				}
			}
		}

		return nearestPosition;
	}

	private static boolean isOre( Level level, BlockPos position ) {
		return BlockHelper.getState( level, position ).getBlock() == MajruszsDifficulty.Blocks.ENDERIUM_SHARD_ORE.get();
	}

	private static class ItemInfo {
		public BlockPos position = null;
		public int counter = 0;
	}
}
