package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.contexts.OnSoulJarMultiplier;
import com.mlib.modhelper.AutoInstance;
import com.mlib.attributes.AttributeHandler;
import com.mlib.data.SerializableHelper;
import com.mlib.data.SerializableStructure;
import com.mlib.effects.SoundHandler;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.*;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class SoulJarItem extends Item {
	static final float DAMAGE_BONUS = 2.5f;
	static final float MOVE_BONUS = 0.15f;
	static final float RANGE_BONUS = 0.5f;
	static final int ARMOR_BONUS = 3;
	static final float MINE_BONUS = 0.15f;
	static final int LUCK_BONUS = 1;
	static final float SWIM_BONUS = 0.30f;

	public static ItemStack randomItemStack( int bonusCount ) {
		ItemStack itemStack = new ItemStack( Registries.SOUL_JAR.get() );
		SerializableHelper.modify( BonusInfo::new, itemStack.getOrCreateTag(), info->info.bonusCount = bonusCount );

		return itemStack;
	}

	public SoulJarItem() {
		super( new Properties().stacksTo( 1 ).rarity( Rarity.UNCOMMON ).tab( Registries.ITEM_GROUP ) );
	}

	@Override
	public InteractionResultHolder< ItemStack > use( Level level, Player player, InteractionHand hand ) {
		ItemStack itemStack = player.getItemInHand( hand );
		BonusInfo bonusInfo = SerializableHelper.read( BonusInfo::new, itemStack.getOrCreateTag() );
		if( bonusInfo.bonusMask == 0b0 ) {
			if( level instanceof ServerLevel ) {
				bonusInfo.randomize();
				bonusInfo.write( itemStack.getOrCreateTag() );
				SoundHandler.ENCHANT.play( level, player.position() );
			}

			return InteractionResultHolder.sidedSuccess( itemStack, level.isClientSide() );
		}

		return InteractionResultHolder.pass( itemStack );
	}

	@AutoInstance
	public static class Handler {
		public Handler() {
			OnItemEquipped.listen( this::updateAttributes )
				.addCondition( Condition.predicate( data->data.entity instanceof LivingEntity ) );

			OnPreDamaged.listen( this::increaseDamage )
				.addCondition( Condition.predicate( data->data.target instanceof Mob mob && mob.getMobType() == MobType.UNDEAD ) )
				.addCondition( Condition.predicate( data->data.attacker != null ) )
				.addCondition( Condition.predicate( data->hasBonus( data.attacker, BonusType.DAMAGE ) ) );

			OnBreakSpeed.listen( this::increaseSpeed )
				.addCondition( Condition.predicate( data->hasBonus( data.player, BonusType.MINE ) ) );

			OnLoot.listen( this::applyRandomSouls )
				.addCondition( Condition.isServer() );

			OnItemAttributeTooltip.listen( this::addTooltip )
				.addCondition( Condition.predicate( data->data.itemStack.getItem() instanceof SoulJarItem ) );

			OnItemTooltip.listen( this::addTooltip )
				.addCondition( Condition.predicate( data->data.itemStack.getItem() instanceof SoulJarItem ) );
		}

		private static boolean hasBonus( @Nullable Entity entity, BonusType bonusType ) {
			ItemStack itemStack = entity instanceof LivingEntity livingEntity ? livingEntity.getItemBySlot( EquipmentSlot.OFFHAND ) : ItemStack.EMPTY;
			if( !( itemStack.getItem() instanceof SoulJarItem ) )
				return false;

			return SerializableHelper.read( BonusInfo::new, itemStack.getOrCreateTag() )
				.getBonusTypes()
				.contains( bonusType );
		}

		private static float getMultiplier( @Nullable Entity entity, ItemStack itemStack ) {
			if( !( itemStack.getItem() instanceof SoulJarItem ) )
				return 0.0f;

			return OnSoulJarMultiplier.dispatch( entity, itemStack ).getMultiplier();
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

			final AttributeHandler MOVE_ATTRIBUTE = new AttributeHandler( "51e7e4fb-e8b4-4c90-ab8a-e8c334e206be", "SoulJarMovementBonus", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );
			final AttributeHandler ARMOR_ATTRIBUTE = new AttributeHandler( "7d2d7767-51da-46cc-8081-80fda32d4126", "SoulJarArmorBonus", Attributes.ARMOR, AttributeModifier.Operation.ADDITION );
			final AttributeHandler REACH_ATTRIBUTE = new AttributeHandler( "23868877-961b-44c9-89c3-376e5c06dbd1", "SoulJarReachBonus", ForgeMod.REACH_DISTANCE.get(), AttributeModifier.Operation.ADDITION );
			final AttributeHandler RANGE_ATTRIBUTE = new AttributeHandler( "a45d6f34-5b78-4d7c-b60a-03fe6400f8cd", "SoulJarRangeBonus", ForgeMod.ATTACK_RANGE.get(), AttributeModifier.Operation.ADDITION );
			final AttributeHandler LUCK_ATTRIBUTE = new AttributeHandler( "a2a496f4-3799-46eb-856c-1ba992f67912", "SoulJarLuckBonus", Attributes.LUCK, AttributeModifier.Operation.ADDITION );
			final AttributeHandler SWIM_ATTRIBUTE = new AttributeHandler( "f404c216-a758-404f-ba95-5a53d3974b44", "SoulJarSwimmingBonus", ForgeMod.SWIM_SPEED.get(), AttributeModifier.Operation.MULTIPLY_TOTAL );

			MOVE_ATTRIBUTE.setValue( multiplier * moveBonus ).apply( entity );
			ARMOR_ATTRIBUTE.setValue( multiplier * armorBonus ).apply( entity );
			REACH_ATTRIBUTE.setValue( multiplier * rangeBonus ).apply( entity );
			RANGE_ATTRIBUTE.setValue( multiplier * rangeBonus ).apply( entity );
			LUCK_ATTRIBUTE.setValue( multiplier * luckBonus ).apply( entity );
			SWIM_ATTRIBUTE.setValue( multiplier * swimBonus ).apply( entity );
		}

		private void increaseDamage( OnPreDamaged.Data data ) {
			data.extraDamage += DAMAGE_BONUS * 2.5f * getMultiplier( data.attacker );
			data.spawnMagicParticles = true;
		}

		private void increaseSpeed( OnBreakSpeed.Data data ) {
			data.newSpeed *= 1.0f + MINE_BONUS * getMultiplier( data.player );
		}

		private void applyRandomSouls( OnLoot.Data data ) {
			for( ItemStack itemStack : data.generatedLoot ) {
				if( !( itemStack.getItem() instanceof SoulJarItem ) ) {
					continue;
				}

				SerializableHelper.modify( BonusInfo::new, itemStack.getOrCreateTag(), bonusInfo->{
					if( bonusInfo.bonusMask == 0b0 ) {
						bonusInfo.randomize();
					}
				} );
			}
		}

		private void addTooltip( OnItemAttributeTooltip.Data data ) {
			Player player = DistExecutor.unsafeCallWhenOn( Dist.CLIENT, ()->()->Minecraft.getInstance().player );
			BonusInfo bonusInfo = SerializableHelper.read( BonusInfo::new, data.itemStack.getOrCreateTag() );
			float multiplier = getMultiplier( player, data.itemStack );
			for( BonusType bonusType : bonusInfo.getBonusTypes() ) {
				data.add( EquipmentSlot.OFFHAND, bonusType.getBonusComponent( multiplier ) );
			}
		}

		private void addTooltip( OnItemTooltip.Data data ) {
			BonusInfo bonusInfo = SerializableHelper.read( BonusInfo::new, data.itemStack.getOrCreateTag() );
			if( bonusInfo.hasBonuses() ) {
				MutableComponent souls = new TextComponent( "" );
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
		public int bonusCount = 3;

		public BonusInfo() {
			super( "SoulJar" );

			this.defineInteger( "BonusMask", ()->this.bonusMask, x->this.bonusMask = x );
			this.defineInteger( "BonusCount", ()->this.bonusCount, x->this.bonusCount = x );
		}

		public void randomize() {
			this.bonusMask = 0b0;
			List< BonusType > bonusTypes = new ArrayList<>( Arrays.stream( BonusType.values() ).toList() );
			Collections.shuffle( bonusTypes );
			bonusTypes.stream()
				.limit( this.bonusCount )
				.forEach( bonusType->this.bonusMask |= bonusType.bit );
		}

		public boolean hasBonuses() {
			return this.bonusMask != 0b0;
		}

		public List< BonusType > getBonusTypes() {
			return Arrays.stream( BonusType.values() )
				.filter( bonusType->( bonusType.bit & this.bonusMask ) != 0 )
				.toList();
		}

		public List< Component > getHintComponents() {
			List< Component > components = new ArrayList<>();
			if( this.bonusMask == 0b0 ) {
				Component bonusCount = new TextComponent( "" + this.bonusCount ).withStyle( ChatFormatting.GREEN );
				components.add( new TranslatableComponent( "item.majruszsdifficulty.soul_jar.item_tooltip1", bonusCount ).withStyle( ChatFormatting.GRAY ) );
				components.add( new TranslatableComponent( "item.majruszsdifficulty.soul_jar.item_tooltip2" ).withStyle( ChatFormatting.GRAY ) );
			} else {
				components.add( new TranslatableComponent( "item.majruszsdifficulty.soul_jar.item_tooltip3" ).withStyle( ChatFormatting.GRAY ) );
			}

			return components;
		}
	}

	public enum BonusType {
		DAMAGE( 1 << 0, "item.majruszsdifficulty.soul_jar.smite", "entity.minecraft.wolf", ChatFormatting.RED, multiplier->TextHelper.signed( DAMAGE_BONUS * multiplier ) ),
		MOVE( 1 << 1, "item.majruszsdifficulty.soul_jar.move", "entity.minecraft.horse", ChatFormatting.WHITE, multiplier->TextHelper.signedPercent( MOVE_BONUS * multiplier ) ),
		RANGE( 1 << 2, "item.majruszsdifficulty.soul_jar.range", "entity.minecraft.enderman", ChatFormatting.DARK_PURPLE, multiplier->TextHelper.signed( RANGE_BONUS * multiplier ) ),
		ARMOR( 1 << 3, "item.majruszsdifficulty.soul_jar.armor", "entity.majruszsdifficulty.tank", ChatFormatting.BLUE, multiplier->TextHelper.signed( ( int )( ARMOR_BONUS * multiplier ) ) ),
		MINE( 1 << 4, "item.majruszsdifficulty.soul_jar.mine", "entity.minecraft.sniffer", ChatFormatting.YELLOW, multiplier->TextHelper.signedPercent( MINE_BONUS * multiplier ) ),
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
			return new TranslatableComponent( this.bonusId, this.valueProvider.apply( multiplier ) )
				.withStyle( ChatFormatting.BLUE );
		}

		public Component getSoulComponent() {
			return new TranslatableComponent( this.mobId )
				.withStyle( this.soulFormatting );
		}
	}

	@OnlyIn( Dist.CLIENT )
	public static class ItemColor implements net.minecraft.client.color.item.ItemColor {
		static final Map< BonusType, Integer > COLOR_MAPPING = Map.of(
			BonusType.DAMAGE, 0xcc5555,
			BonusType.MOVE, 0xdddddd,
			BonusType.RANGE, 0xcc55cc,
			BonusType.ARMOR, 0x5555cc,
			BonusType.MINE, 0xcccc55,
			BonusType.LUCK, 0x55cc55,
			BonusType.SWIM, 0x55cccc
		);

		@Override
		public int getColor( ItemStack itemStack, int index ) {
			if( index == 0 ) {
				return 0xffffff;
			}

			BonusInfo bonusInfo = SerializableHelper.read( BonusInfo::new, itemStack.getOrCreateTag() );
			return bonusInfo.hasBonuses() ? COLOR_MAPPING.get( bonusInfo.getBonusTypes().get( index - 1 ) ) : 0xeeeeee - index * 0x111111;
		}
	}
}
