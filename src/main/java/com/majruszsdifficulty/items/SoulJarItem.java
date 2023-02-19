package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.contexts.OnSoulJarMultiplier;
import com.mlib.annotations.AutoInstance;
import com.mlib.attributes.AttributeHandler;
import com.mlib.data.SerializableStructure;
import com.mlib.effects.SoundHandler;
import com.mlib.gamemodifiers.contexts.*;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SoulJarItem extends Item {
	static final float DAMAGE_BONUS = 2.5f;
	static final float MOVE_BONUS = 0.15f;
	static final float RANGE_BONUS = 0.5f;
	static final int ARMOR_BONUS = 2;
	static final float MINE_BONUS = 0.15f;
	static final int LUCK_BONUS = 1;
	static final float SWIM_BONUS = 0.30f;
	static final AttributeHandler MOVE_ATTRIBUTE = new AttributeHandler( "51e7e4fb-e8b4-4c90-ab8a-e8c334e206be", "SoulJarMovementBonus", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );
	static final AttributeHandler ARMOR_ATTRIBUTE = new AttributeHandler( "7d2d7767-51da-46cc-8081-80fda32d4126", "SoulJarArmorBonus", Attributes.ARMOR, AttributeModifier.Operation.ADDITION );
	static final AttributeHandler REACH_ATTRIBUTE = new AttributeHandler( "23868877-961b-44c9-89c3-376e5c06dbd1", "SoulJarReachBonus", ForgeMod.REACH_DISTANCE.get(), AttributeModifier.Operation.ADDITION );
	static final AttributeHandler RANGE_ATTRIBUTE = new AttributeHandler( "a45d6f34-5b78-4d7c-b60a-03fe6400f8cd", "SoulJarRangeBonus", ForgeMod.ATTACK_RANGE.get(), AttributeModifier.Operation.ADDITION );
	static final AttributeHandler LUCK_ATTRIBUTE = new AttributeHandler( "a2a496f4-3799-46eb-856c-1ba992f67912", "SoulJarLuckBonus", Attributes.LUCK, AttributeModifier.Operation.ADDITION );
	static final AttributeHandler SWIM_ATTRIBUTE = new AttributeHandler( "f404c216-a758-404f-ba95-5a53d3974b44", "SoulJarSwimmingBonus", ForgeMod.SWIM_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL );

	public static ItemStack randomItemStack( int bonusCount ) {
		ItemStack itemStack = new ItemStack( Registries.SOUL_JAR.get() );
		BonusInfo bonusInfo = new BonusInfo( itemStack.getOrCreateTag() );
		bonusInfo.bonusCount = bonusCount;
		bonusInfo.write( itemStack.getOrCreateTag() );

		return itemStack;
	}

	public SoulJarItem() {
		super( new Properties().stacksTo( 1 ).rarity( Rarity.UNCOMMON ) );
	}

	@Override
	public boolean isFoil( ItemStack itemStack ) {
		return new BonusInfo( itemStack.getOrCreateTag() ).bonusMask != 0b0;
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level level, Player player, InteractionHand hand ) {
		ItemStack itemStack = player.getItemInHand( hand );
		BonusInfo bonusInfo = new BonusInfo( itemStack.getOrCreateTag() );
		if( bonusInfo.bonusMask == 0b0 ) {
			bonusInfo.randomize();
			bonusInfo.write( itemStack.getOrCreateTag() );
			SoundHandler.ENCHANT.play( level, player.position() );

			return InteractionResultHolder.sidedSuccess( itemStack, level.isClientSide() );
		}

		return InteractionResultHolder.pass( itemStack );
	}

	@AutoInstance
	public static class Handler {
		public Handler() {
			new OnItemEquipped.Context( this::updateAttributes )
				.addCondition( data->data.entity instanceof LivingEntity );

			new OnPreDamaged.Context( this::increaseDamage )
				.addCondition( data->data.target instanceof Mob mob && mob.getMobType() == MobType.UNDEAD )
				.addCondition( data->data.attacker != null )
				.addCondition( data->hasBonus( data.attacker, BonusType.DAMAGE ) );

			new OnBreakSpeed.Context( this::increaseSpeed )
				.addCondition( data->hasBonus( data.player, BonusType.MINING ) );

			new OnItemAttributeTooltip.Context( this::addTooltip )
				.addCondition( data->data.itemStack.getItem() instanceof SoulJarItem );

			new OnItemTooltip.Context( this::addTooltip )
				.addCondition( data->data.itemStack.getItem() instanceof SoulJarItem );
		}

		private static boolean hasBonus( @Nullable Entity entity, BonusType bonusType ) {
			ItemStack itemStack = entity instanceof LivingEntity livingEntity ? livingEntity.getItemBySlot( EquipmentSlot.OFFHAND ) : ItemStack.EMPTY;
			if( !( itemStack.getItem() instanceof SoulJarItem ) )
				return false;

			return new BonusInfo( itemStack.getOrCreateTag() )
				.getBonusTypes()
				.contains( bonusType );
		}

		private static float getMultiplier( @Nullable Entity entity, ItemStack itemStack ) {
			if( !( itemStack.getItem() instanceof SoulJarItem ) )
				return 0.0f;

			return OnSoulJarMultiplier.broadcast( new OnSoulJarMultiplier.Data( entity, itemStack ) ).getMultiplier();
		}

		private static float getMultiplier( @Nullable Entity entity ) {
			return entity instanceof LivingEntity livingEntity ? getMultiplier( entity, livingEntity.getItemBySlot( EquipmentSlot.OFFHAND ) ) : 0.0f;
		}

		private void updateAttributes( OnItemEquipped.Data data ) {
			LivingEntity entity = ( LivingEntity )data.entity;
			float multiplier = getMultiplier( entity );
			float moveBonus = hasBonus( data.entity, BonusType.MOVE ) ? MOVE_BONUS : 0.0f;
			float armorBonus = hasBonus( data.entity, BonusType.ARMOR ) ? ARMOR_BONUS : 0.0f;
			float rangeBonus = hasBonus( data.entity, BonusType.RANGE ) ? RANGE_BONUS : 0.0f;
			float luckBonus = hasBonus( data.entity, BonusType.LUCK ) ? LUCK_BONUS : 0.0f;
			float swimBonus = hasBonus( data.entity, BonusType.SWIM ) ? SWIM_BONUS : 0.0f;

			MOVE_ATTRIBUTE.setValueAndApply( entity, multiplier * moveBonus );
			ARMOR_ATTRIBUTE.setValueAndApply( entity, multiplier * armorBonus );
			REACH_ATTRIBUTE.setValueAndApply( entity, multiplier * rangeBonus );
			RANGE_ATTRIBUTE.setValueAndApply( entity, multiplier * rangeBonus );
			LUCK_ATTRIBUTE.setValueAndApply( entity, multiplier * luckBonus );
			SWIM_ATTRIBUTE.setValueAndApply( entity, multiplier * swimBonus );
		}

		private void increaseDamage( OnPreDamaged.Data data ) {
			data.extraDamage += DAMAGE_BONUS * 2.5f * getMultiplier( data.attacker );
			data.spawnMagicParticles = true;
		}

		private void increaseSpeed( OnBreakSpeed.Data data ) {
			data.event.setNewSpeed( data.event.getNewSpeed() + data.event.getOriginalSpeed() * MINE_BONUS * getMultiplier( data.player ) );
		}

		private void addTooltip( OnItemAttributeTooltip.Data data ) {
			BonusInfo bonusInfo = new BonusInfo( data.itemStack.getOrCreateTag() );
			float multiplier = getMultiplier( Minecraft.getInstance().player, data.itemStack );
			for( BonusType bonusType : bonusInfo.getBonusTypes() ) {
				data.add( EquipmentSlot.OFFHAND, bonusType.getBonusComponent( multiplier ) );
			}
		}

		private void addTooltip( OnItemTooltip.Data data ) {
			BonusInfo bonusInfo = new BonusInfo( data.itemStack.getOrCreateTag() );
			if( bonusInfo.bonusMask != 0b0 ) {
				MutableComponent souls = Component.literal( "" );
				for( BonusType bonusType : bonusInfo.getBonusTypes() ) {
					souls.append( souls.getString().equals( "" ) ? "" : " " ).append( bonusType.getSoulComponent() );
				}
				data.tooltip.add( 1, souls );
			}

			data.tooltip.addAll( 1, bonusInfo.getHintComponents() );
		}
	}

	public static class BonusInfo extends SerializableStructure {
		public int bonusMask = 0b0;
		public int bonusCount = 2;

		public BonusInfo( CompoundTag tag ) {
			super( "SoulJar" );

			this.define( "BonusMask", ()->this.bonusMask, x->this.bonusMask = x );
			this.define( "BonusCount", ()->this.bonusCount, x->this.bonusCount = x );

			this.read( tag );
		}

		public void randomize() {
			this.bonusMask = 0b0;
			List< BonusType > bonusTypes = new ArrayList<>( Arrays.stream( BonusType.values() ).toList() );
			Collections.shuffle( bonusTypes );
			bonusTypes.stream()
				.limit( this.bonusCount )
				.forEach( bonusType->this.bonusMask |= bonusType.bit );
		}

		public List< BonusType > getBonusTypes() {
			return Arrays.stream( BonusType.values() )
				.filter( bonusType->( bonusType.bit & this.bonusMask ) != 0 )
				.toList();
		}

		public List< Component > getHintComponents() {
			List< Component > components = new ArrayList<>();
			if( this.bonusMask == 0b0 ) {
				Component bonusCount = Component.literal( "" + this.bonusCount ).withStyle( ChatFormatting.GREEN );
				components.add( Component.translatable( "item.majruszsdifficulty.soul_jar.item_tooltip1", bonusCount ).withStyle( ChatFormatting.GRAY ) );
				components.add( Component.translatable( "item.majruszsdifficulty.soul_jar.item_tooltip2" ).withStyle( ChatFormatting.GRAY ) );
			} else {
				components.add( Component.translatable( "item.majruszsdifficulty.soul_jar.item_tooltip3" ).withStyle( ChatFormatting.GRAY ) );
			}

			return components;
		}
	}

	public enum BonusType {
		DAMAGE( 1 << 0, "item.majruszsdifficulty.soul_jar.smite", "entity.minecraft.wolf", ChatFormatting.RED, multiplier->TextHelper.signed( DAMAGE_BONUS * multiplier ) ),
		MOVE( 1 << 1, "item.majruszsdifficulty.soul_jar.move", "entity.minecraft.horse", ChatFormatting.WHITE, multiplier->TextHelper.signedPercent( MOVE_BONUS * multiplier ) ),
		RANGE( 1 << 2, "item.majruszsdifficulty.soul_jar.range", "entity.minecraft.enderman", ChatFormatting.DARK_PURPLE, multiplier->TextHelper.signed( RANGE_BONUS * multiplier ) ),
		ARMOR( 1 << 3, "item.majruszsdifficulty.soul_jar.armor", "entity.majruszsdifficulty.tank", ChatFormatting.BLUE, multiplier->TextHelper.signed( ( int )( ARMOR_BONUS * multiplier ) ) ),
		MINING( 1 << 4, "item.majruszsdifficulty.soul_jar.mine", "entity.minecraft.sniffer", ChatFormatting.YELLOW, multiplier->TextHelper.signedPercent( MINE_BONUS * multiplier ) ),
		LUCK( 1 << 5, "item.majruszsdifficulty.soul_jar.luck", "entity.minecraft.rabbit", ChatFormatting.GREEN, multiplier->TextHelper.signed( ( int )( LUCK_BONUS * multiplier ) ) ),
		SWIM( 1 << 6, "item.majruszsdifficulty.soul_jar.swim", "entity.minecraft.dolphin", ChatFormatting.AQUA, multiplier->TextHelper.signedPercent( SWIM_BONUS * multiplier ) );

		final int bit;
		final String bonusId;
		final String mobId;
		final ChatFormatting soulFormatting;
		final Function< Float, String > valueProvider;

		BonusType( int bit, String bonusId, String mobId, ChatFormatting soulFormatting, Function< Float, String > valueProvider ) {
			this.bit = bit;
			this.bonusId = bonusId;
			this.mobId = mobId;
			this.soulFormatting = soulFormatting;
			this.valueProvider = valueProvider;
		}

		public Component getBonusComponent( float multiplier ) {
			return Component.translatable( this.bonusId, this.valueProvider.apply( multiplier ) )
				.withStyle( ChatFormatting.BLUE );
		}

		public Component getSoulComponent() {
			return Component.translatable( this.mobId )
				.withStyle( this.soulFormatting );
		}
	}
}
