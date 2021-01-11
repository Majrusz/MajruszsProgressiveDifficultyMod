package com.majruszs_difficulty.items;

import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.RegistryHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.List;

/** Class representing treasure bag. */
public class TreasureBagItem extends Item {
	private final ResourceLocation lootTableLocation;

	public TreasureBagItem( String id ) {
		super( ( new Item.Properties() ).maxStackSize( 16 )
			.group( RegistryHandler.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );

		this.lootTableLocation = new ResourceLocation( MajruszsDifficulty.MOD_ID, "gameplay/" + id );
	}

	/** Opening treasure bag on right click. */
	@Override
	public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity player, Hand hand ) {
		ItemStack itemStack = player.getHeldItem( hand );

		if( !world.isRemote ) {
			if( !player.abilities.isCreativeMode )
				itemStack.shrink( 1 );
			player.addStat( Stats.ITEM_USED.get( this ) );
			world.playSound( null, player.getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.AMBIENT, 1.0f, 0.9f );

			List< ItemStack > loot = generateLoot( player );
			for( ItemStack reward : loot ) {
				if( player.canPickUpItem( reward ) )
					player.inventory.addItemStackToInventory( reward );
				else
					world.addEntity( new ItemEntity( world, player.getPosX(), player.getPosY() + 1, player.getPosZ(), reward ) );
			}
		}

		return ActionResult.func_233538_a_( itemStack, world.isRemote() );
	}

	/** Adding simple tooltip to treasure bag. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		toolTip.add( new TranslationTextComponent( "majruszs_difficulty.treasure_bag.item_tooltip" ).mergeStyle( TextFormatting.GRAY ) );
	}

	/**
	 Returning full object registry of treasure bag.

	 @param name Name of the treasure bag.
	 */
	public static RegistryObject< TreasureBagItem > getRegistry( String name ) {
		return RegistryHandler.ITEMS.register( name + "_treasure_bag", ()->new TreasureBagItem( name + "_treasure_loot" ) );
	}

	/** Generating loot context of current treasure bag. (who opened the bag, where, etc.) */
	protected static LootContext generateLootContext( PlayerEntity player ) {
		LootContext.Builder lootContextBuilder = new LootContext.Builder( ( ServerWorld )player.getEntityWorld() );
		lootContextBuilder.withParameter( LootParameters.field_237457_g_, player.getPositionVec() );
		lootContextBuilder.withParameter( LootParameters.THIS_ENTITY, player );

		return lootContextBuilder.build( LootParameterSets.GIFT );
	}

	/**
	 Generating random loot from loot table.

	 @param player Player required to generate loot context.
	 */
	protected List< ItemStack > generateLoot( PlayerEntity player ) {
		LootTable lootTable = getLootTable();

		return lootTable.generate( generateLootContext( player ) );
	}

	/** Returning loot table for current treasure bag. (possible loot) */
	protected LootTable getLootTable() {
		return ServerLifecycleHooks.getCurrentServer()
			.getLootTableManager()
			.getLootTableFromLocation( this.lootTableLocation );
	}
}
