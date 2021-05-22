package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.MajruszsDifficulty;
import com.majruszs_difficulty.MajruszsHelper;
import com.majruszs_difficulty.RegistryHandler;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
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

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Class representing treasure bag. */
public class TreasureBagItem extends Item {
	protected final static ConfigGroup CONFIG_GROUP;
	private final static String TOOLTIP_TRANSLATION_KEY = "majruszs_difficulty.treasure_bag.item_tooltip";
	static {
		CONFIG_GROUP = new ConfigGroup( "TreasureBag", "Configuration for treasure bags." );
		FEATURES_GROUP.addGroup( CONFIG_GROUP );
	}

	private final ResourceLocation lootTableLocation;
	private final String id;
	private final AvailabilityConfig availability;

	public TreasureBagItem( String id, String entityNameForConfiguration ) {
		super( ( new Item.Properties() ).maxStackSize( 16 )
			.group( Instances.ITEM_GROUP )
			.rarity( Rarity.UNCOMMON ) );

		this.lootTableLocation = new ResourceLocation( MajruszsDifficulty.MOD_ID, "gameplay/" + id + "_treasure_loot" );
		this.id = id;
		this.availability = new AvailabilityConfig( id, getComment( entityNameForConfiguration ), false, true );
		CONFIG_GROUP.addConfig( this.availability );
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

			if( this.availability.isEnabled() ) {
				List< ItemStack > loot = generateLoot( player );
				for( ItemStack reward : loot ) {
					if( player.canPickUpItem( reward ) )
						player.inventory.addItemStackToInventory( reward );
					else
						world.addEntity( new ItemEntity( world, player.getPosX(), player.getPosY() + 1, player.getPosZ(), reward ) );
				}
			}
		}

		return ActionResult.func_233538_a_( itemStack, world.isRemote() );
	}

	/** Adding simple tooltip to treasure bag. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > tooltip, ITooltipFlag flag ) {
		MajruszsHelper.addExtraTooltipIfDisabled( tooltip, this.availability.isEnabled() );
		MajruszsHelper.addAdvancedTooltip( tooltip, flag, TOOLTIP_TRANSLATION_KEY );
	}

	/** Registers given treasure bag. */
	public RegistryObject< TreasureBagItem > register() {
		return RegistryHandler.ITEMS.register( this.id + "_treasure_bag", ()->this );
	}

	/** Checks whether treasure bag is not disabled in configuration file? */
	public boolean isAvailable() {
		return this.availability.isEnabled();
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

	/** Creates comment for configuration. */
	private static String getComment( String treasureBagSourceName ) {
		return "Is treasure bag from " + treasureBagSourceName + " available in survival mode?";
	}
}
