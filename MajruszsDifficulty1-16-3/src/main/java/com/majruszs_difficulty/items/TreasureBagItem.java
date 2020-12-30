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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.List;

public class TreasureBagItem extends Item {
	private final ResourceLocation lootTableLocation;

	public TreasureBagItem( String id ) {
		super( ( new Item.Properties() ).maxStackSize( 16 )
			.group( RegistryHandler.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );

		this.lootTableLocation = new ResourceLocation( MajruszsDifficulty.MOD_ID, "gameplay/" + id );
	}

	@Override
	public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity player, Hand hand ) {
		ItemStack itemStack = player.getHeldItem( hand );

		if( !world.isRemote ) {
			if( !player.abilities.isCreativeMode )
				itemStack.shrink( 1 );
			player.addStat( Stats.ITEM_USED.get( this ) );

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

	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		toolTip.add( new TranslationTextComponent( "majruszs_difficulty.treasure_bag.item_tooltip" ).func_240699_a_( TextFormatting.GRAY ) );
	}

	protected List< ItemStack > generateLoot( PlayerEntity player ) {
		LootTable lootTable = getLootTable();

		return lootTable.generate( generateLootContext( player ) );
	}

	protected static LootContext generateLootContext( PlayerEntity player ) {
		LootContext.Builder lootContextBuilder = new LootContext.Builder( ( ServerWorld )player.getEntityWorld() );
		lootContextBuilder.withParameter( LootParameters.field_237457_g_, player.getPositionVec() );
		lootContextBuilder.withParameter( LootParameters.THIS_ENTITY, player );

		return lootContextBuilder.build( LootParameterSets.GIFT );
	}

	protected LootTable getLootTable() {
		return ServerLifecycleHooks.getCurrentServer()
			.getLootTableManager()
			.getLootTableFromLocation( this.lootTableLocation );
	}
}
