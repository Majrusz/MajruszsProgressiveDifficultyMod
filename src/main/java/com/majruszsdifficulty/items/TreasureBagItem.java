package com.majruszsdifficulty.items;

import com.majruszsdifficulty.MajruszsHelper;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.config.GameStageIntegerConfig;
import com.majruszsdifficulty.gamemodifiers.configs.TreasureBagConfig;
import com.majruszsdifficulty.gamemodifiers.contexts.OnTreasureBagOpened;
import com.majruszsdifficulty.treasurebags.TreasureBagProgressClient;
import com.mlib.effects.SoundHandler;
import com.mlib.items.ItemHelper;
import com.mlib.math.Range;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
			UndeadArmy.CONFIG, ElderGuardian.CONFIG, Wither.CONFIG, EnderDragon.CONFIG, Fishing.CONFIG, Pillager.CONFIG, Warden.CONFIG
		};
	}

	/** Generates loot context of current treasure bag. (who opened the bag, where, etc.) */
	public static LootContext generateLootContext( Player player ) {
		LootContext.Builder lootContextBuilder = new LootContext.Builder( ( ServerLevel )player.level );
		lootContextBuilder.withParameter( LootContextParams.ORIGIN, player.position() );
		lootContextBuilder.withParameter( LootContextParams.THIS_ENTITY, player );

		return lootContextBuilder.create( LootContextParamSets.GIFT );
	}

	public TreasureBagItem( ResourceLocation location, TreasureBagConfig config ) {
		super( new Properties().tab( Registries.ITEM_GROUP ).stacksTo( 16 ).rarity( Rarity.UNCOMMON ) );
		this.lootTableLocation = location;
		this.config = config;
		TREASURE_BAGS.add( this );
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level level, Player player, InteractionHand hand ) {
		ItemStack itemStack = player.getItemInHand( hand );

		if( !level.isClientSide ) {
			if( player instanceof ServerPlayer serverPlayer ) {
				this.triggerTreasureBagAdvancement( serverPlayer );
			}

			SoundHandler.ITEM_PICKUP.play( level, player.position() );
			List< ItemStack > loot = generateLoot( player );
			OnTreasureBagOpened.dispatch( player, this, loot );
			if( level instanceof ServerLevel serverLevel ) {
				loot.forEach( reward->ItemHelper.giveItemStackToPlayer( reward, player, serverLevel ) );
			}
			ItemHelper.consumeItemOnUse( itemStack, player );
		}

		return InteractionResultHolder.sidedSuccess( itemStack, level.isClientSide() );
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > tooltip, TooltipFlag flag ) {
		MajruszsHelper.addAdvancedTranslatableTexts( tooltip, flag, ITEM_TOOLTIP_TRANSLATION_KEY, " " );

		tooltip.addAll( TreasureBagProgressClient.getTextComponents( this ) );
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
		public static final ResourceLocation LOCATION = Registries.getLocation( "gameplay/undead_army_treasure_loot" );
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "UndeadArmy" );

		public UndeadArmy() {
			super( LOCATION, CONFIG );
		}
	}

	public static class ElderGuardian extends TreasureBagItem {
		public static final ResourceLocation LOCATION = Registries.getLocation( "gameplay/elder_guardian_treasure_loot" );
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "ElderGuardian" );

		public ElderGuardian() {
			super( LOCATION, CONFIG );
		}
	}

	public static class Wither extends TreasureBagItem {
		public static final ResourceLocation LOCATION = Registries.getLocation( "gameplay/wither_treasure_loot" );
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "Wither" );

		public Wither() {
			super( LOCATION, CONFIG );
		}
	}

	public static class EnderDragon extends TreasureBagItem {
		public static final ResourceLocation LOCATION = Registries.getLocation( "gameplay/ender_dragon_treasure_loot" );
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "EnderDragon" );

		public EnderDragon() {
			super( LOCATION, CONFIG );
		}
	}

	public static class Fishing extends TreasureBagItem {
		public static final ResourceLocation LOCATION = Registries.getLocation( "gameplay/fishing_treasure_loot" );
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "Fishing" );
		public static final GameStageIntegerConfig REQUIRED_FISH_COUNT = new GameStageIntegerConfig( 20, 15, 10, new Range<>( 3, 100 ) );

		static {
			CONFIG.addConfig( REQUIRED_FISH_COUNT.name( "RequiredFishCount" ).comment( "Required amount of items fished to get this Treasure Bag." ) );
		}

		public Fishing() {
			super( LOCATION, CONFIG );
		}
	}

	public static class Pillager extends TreasureBagItem {
		public static final ResourceLocation LOCATION = Registries.getLocation( "gameplay/pillager_treasure_loot" );
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "PillagerRaid" );

		public Pillager() {
			super( LOCATION, CONFIG );
		}
	}

	public static class Warden extends TreasureBagItem {
		public static final ResourceLocation LOCATION = Registries.getLocation( "gameplay/warden_treasure_loot" );
		public static final TreasureBagConfig CONFIG = new TreasureBagConfig( "Warden" );

		public Warden() {
			super( LOCATION, CONFIG );
		}
	}
}
