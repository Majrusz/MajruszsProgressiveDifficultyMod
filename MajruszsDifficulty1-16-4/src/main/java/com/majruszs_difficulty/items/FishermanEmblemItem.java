package com.majruszs_difficulty.items;

import com.majruszs_difficulty.Instances;
import com.majruszs_difficulty.config.GameStateIntegerConfig;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.ConfigGroup;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

import static com.majruszs_difficulty.MajruszsDifficulty.FEATURES_GROUP;

/** Emblem that increases luck for player that has it anywhere in the inventory. */
@Mod.EventBusSubscriber
public class FishermanEmblemItem extends Item {
	protected static final AttributeHandler LUCK_ATTRIBUTE = new AttributeHandler( "4010270c-9d57-4273-8a41-00985f1e4781",
		"FishermanEmblemLuckBonus", Attributes.LUCK, AttributeModifier.Operation.ADDITION
	);
	protected final ConfigGroup group;
	protected final GameStateIntegerConfig luck;

	public FishermanEmblemItem() {
		super( ( new Item.Properties() ).maxStackSize( 1 )
			.rarity( Rarity.UNCOMMON )
			.group( Instances.ITEM_GROUP ) );

		String luck_comment = "Luck bonus when fishing.";
		String group_comment = "Functionality of Fisherman Emblem.";
		this.luck = new GameStateIntegerConfig( "Luck", luck_comment, 2, 3, 4, 1, 100 );

		this.group = FEATURES_GROUP.addGroup( new ConfigGroup( "FishermanEmblem", group_comment ) );
		this.group.addConfig( this.luck );
	}

	/** Adding tooltip with information for what this embled does. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		toolTip.add( new TranslationTextComponent( "item.majruszs_difficulty.fisherman_emblem.item_tooltip1" ).mergeStyle( TextFormatting.GOLD ) );
		toolTip.add( new TranslationTextComponent( "item.majruszs_difficulty.fisherman_emblem.item_tooltip2" ).mergeStyle( TextFormatting.GRAY ) );
	}

	/** Returns current luck bonus. (whether player has emblem or not) */
	public int getEmblemLuckBonus( PlayerEntity player ) {
		return player.fishingBobber != null ? this.luck.getCurrentGameStateValue() : 0;
	}

	@SubscribeEvent
	public static void increaseLuck( TickEvent.PlayerTickEvent event ) {
		PlayerEntity player = event.player;

		LUCK_ATTRIBUTE.setValue( Instances.FISHERMAN_EMBLEM_ITEM.getEmblemLuckBonus( player ) ).apply( player );
	}
}
