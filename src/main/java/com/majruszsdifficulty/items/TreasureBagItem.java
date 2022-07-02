package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.majruszsdifficulty.events.TreasureBagOpenedEvent;
import com.majruszsdifficulty.gamemodifiers.configs.TreasureBagConfig;
import com.majruszsdifficulty.treasurebags.LootProgressClient;
import com.majruszsdifficulty.treasurebags.LootProgressManager;
import com.mlib.items.ItemHelper;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TreasureBagItem extends Item {
	public final static List< TreasureBagItem > TREASURE_BAGS = new ArrayList<>();
	final static String ITEM_TOOLTIP_TRANSLATION_KEY = "majruszsdifficulty.treasure_bag.item_tooltip";
	final ResourceLocation lootTableLocation;
	final TreasureBagConfig config;

	public static TreasureBagConfig[] getConfigs() {
		return new TreasureBagConfig[]{
			UndeadArmy.CONFIG, ElderGuardian.CONFIG, Wither.CONFIG, EnderDragon.CONFIG, Fishing.CONFIG, Pillager.CONFIG
		};
	}

	/** Generates loot context of current treasure bag. (who opened the bag, where, etc.) */
	public static LootContext generateLootContext( Player player ) {
		LootContext.Builder lootContextBuilder = new LootContext.Builder( ( ServerLevel )player.level );
		lootContextBuilder.withParameter( LootContextParams.ORIGIN, player.position() );
		lootContextBuilder.withParameter( LootContextParams.THIS_ENTITY, player );

		return lootContextBuilder.create( LootContextParamSets.GIFT );
	}

	public TreasureBagItem( String id, TreasureBagConfig config ) {
		super( new Properties().stacksTo( 16 ).tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );
		this.lootTableLocation = Registries.getLocation( "gameplay/" + id + "_treasure_loot" );
		this.config = config;
		TREASURE_BAGS.add( this );
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level level, Player player, InteractionHand hand ) {
		ItemStack itemStack = player.getItemInHand( hand );

		if( !level.isClientSide ) {
			ItemHelper.consumeItemOnUse( itemStack, player );
			if( player instanceof ServerPlayer serverPlayer )
				triggerTreasureBagAdvancement( serverPlayer );

			level.playSound( null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.AMBIENT, 1.0f, 0.9f );
			List< ItemStack > loot = generateLoot( player );
			MinecraftForge.EVENT_BUS.post( new TreasureBagOpenedEvent( player, this, loot ) );
			LootProgressManager.updateProgress( this, player, loot );
			if( level instanceof ServerLevel serverLevel )
				loot.forEach( reward->ItemHelper.giveItemStackToPlayer( reward, player, serverLevel ) );
		}

		return InteractionResultHolder.sidedSuccess( itemStack, level.isClientSide() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		MajruszsHelper.addAdvancedTranslatableTexts( tooltip, flag, ITEM_TOOLTIP_TRANSLATION_KEY, " " );

		LootProgressClient.addDropList( this, tooltip );
	}

	public boolean isEnabled() {
		return this.config.isEnabled();
	}

	public LootTable getLootTable() {
		return ServerLifecycleHooks.getCurrentServer().getLootTables().get( this.lootTableLocation );
	}

	private List< ItemStack > generateLoot( Player player ) {
		LootTable lootTable = getLootTable();

		return lootTable.getRandomItems( generateLootContext( player ) );
	}

	private void triggerTreasureBagAdvancement( ServerPlayer player ) {
		Registries.TREASURE_BAG_TRIGGER.trigger( player, this, player.getStats().getValue( Stats.ITEM_USED.get( this ) ) );
	}

	public static class UndeadArmy extends TreasureBagItem {
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "UndeadArmy", "" );

		public UndeadArmy() {
			super( "undead_army", CONFIG );
		}
	}

	public static class ElderGuardian extends TreasureBagItem {
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "ElderGuardian", "" );

		public ElderGuardian() {
			super( "elder_guardian", CONFIG );
		}
	}

	public static class Wither extends TreasureBagItem {
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "Wither", "" );

		public Wither() {
			super( "wither", CONFIG );
		}
	}

	public static class EnderDragon extends TreasureBagItem {
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "EnderDragon", "" );

		public EnderDragon() {
			super( "ender_dragon", CONFIG );
		}
	}

	public static class Fishing extends TreasureBagItem {
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "Fishing", "" );
		public static final GameStageIntegerConfig REQUIRED_FISH_COUNT = new GameStageIntegerConfig( "RequiredFishCount", "Required amount of items fished to get this Treasure Bag.", 20, 15, 10, 3, 100 );

		static {
			CONFIG.addConfig( REQUIRED_FISH_COUNT );
		}

		public Fishing() {
			super( "fishing", CONFIG );
		}
	}

	public static class Pillager extends TreasureBagItem {
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "PillagerRaid", "" );

		public Pillager() {
			super( "pillager", CONFIG );
		}
	}
}
