package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.contexts.base.CustomConditions;
import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.EffectConfig;
import com.mlib.data.SerializableHelper;
import com.mlib.data.SerializableStructure;
import com.mlib.effects.ParticleHandler;
import com.mlib.entities.EntityHelper;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnDamaged;
import com.mlib.contexts.OnDeath;
import com.mlib.contexts.OnItemAttributeTooltip;
import com.mlib.items.ItemHelper;
import com.mlib.mobeffects.MobEffectHelper;
import com.mlib.text.TextHelper;
import com.mlib.time.Time;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.Vec3;

public class WitherSwordItem extends SwordItem {
	static final String GROUP_ID = Registries.getLocationString( "wither_sword" );

	static {
		ModConfigs.init( Registries.Groups.DEFAULT, GROUP_ID ).name( "WitherSword" );
	}

	public WitherSwordItem() {
		super( CustomItemTier.WITHER, 3, -2.4f, new Properties().tab( Registries.ITEM_GROUP ).rarity( Rarity.UNCOMMON ) );
	}

	@AutoInstance
	public static class Effect {
		static final String ATTRIBUTE_ID = "item.majruszsdifficulty.wither_sword.effect";
		final EffectConfig wither = new EffectConfig( MobEffects.WITHER, 1, 6.0 );

		public Effect() {
			ConfigGroup group = ModConfigs.registerSubgroup( GROUP_ID )
				.name( "Effect" )
				.comment( "Wither Sword inflicts wither effect." );

			OnDamaged.listen( this::applyWither )
				.addCondition( Condition.predicate( data->ItemHelper.hasInMainHand( data.attacker, WitherSwordItem.class ) ) )
				.addCondition( Condition.predicate( data->data.source.getDirectEntity() == data.attacker ) )
				.addConfig( this.wither )
				.insertTo( group );

			OnItemAttributeTooltip.listen( this::addTooltip )
				.addCondition( Condition.predicate( data->data.item instanceof WitherSwordItem ) )
				.insertTo( group );
		}

		private void applyWither( OnDamaged.Data data ) {
			MobEffectHelper.tryToApply( data.target, MobEffects.WITHER, this.wither.getDuration(), this.wither.getAmplifier() );
		}

		private void addTooltip( OnItemAttributeTooltip.Data data ) {
			String chance = TextHelper.percent( 1.0f );
			String amplifier = TextHelper.toRoman( this.wither.getAmplifier() + 1 );
			data.add( EquipmentSlot.MAINHAND, new TranslatableComponent( ATTRIBUTE_ID, chance, amplifier )
				.withStyle( ChatFormatting.DARK_GREEN ) );
		}
	}

	@AutoInstance
	public static class TurnSkeletonIntoWitherSkeleton {
		public TurnSkeletonIntoWitherSkeleton() {
			ConfigGroup group = ModConfigs.registerSubgroup( GROUP_ID )
				.name( "TransformSkeletons" )
				.comment( "If the Skeleton dies from Wither Sword it will respawn as Wither Skeleton in a few seconds." );

			OnDamaged.listen( this::applyWitherTag )
				.addCondition( Condition.predicate( data->data.attacker != null ) )
				.addCondition( Condition.predicate( data->data.attacker.getMainHandItem().getItem() instanceof WitherSwordItem ) )
				.addCondition( Condition.predicate( data->data.target instanceof Skeleton ) )
				.insertTo( group );

			OnDeath.listen( this::spawnWitherSkeleton )
				.addCondition( Condition.isServer() )
				.addCondition( CustomConditions.gameStageAtLeast( GameStage.MASTER ) )
				.addCondition( Condition.chanceCRD( 0.5, true ) )
				.addCondition( Condition.excludable() )
				.addCondition( Condition.predicate( data->SerializableHelper.read( Data::new, data.target.getPersistentData() ).hasWitherTag ) )
				.insertTo( group );
		}

		private void applyWitherTag( OnDamaged.Data data ) {
			SerializableHelper.modify( Data::new, data.target.getPersistentData(), subdata->subdata.hasWitherTag = true );
		}

		private void spawnWitherSkeleton( OnDeath.Data data ) {
			ServerLevel level = data.getServerLevel();
			Time.slider( 7.0, slider->{
				Vec3 position = data.target.position().add( 0.0, 1.0, 0.0 );
				if( slider.getTicksLeft() % 5 == 0 ) {
					ParticleHandler.SOUL.spawn( level, position, ( int )( slider.getRatio() * 10 ), ParticleHandler.offset( slider.getRatio() ) );
				}
				if( slider.getTicksLeft() == 2 ) {
					ParticleHandler.SOUL.spawn( level, position, 100, ParticleHandler.offset( 0.5f ) );
					ParticleHandler.SOUL.spawn( level, position, 100, ParticleHandler.offset( 1.0f ) );
				}
				if( slider.isFinished() ) {
					EntityHelper.createSpawner( EntityType.WITHER_SKELETON, level )
						.mobSpawnType( MobSpawnType.EVENT )
						.position( data.target.position() )
						.spawn();
				}
			} );
		}

		private static class Data extends SerializableStructure {
			boolean hasWitherTag = false;

			public Data() {
				this.defineBoolean( "MajruszsDifficultyWitherTag", ()->this.hasWitherTag, x->this.hasWitherTag = x );
			}
		}
	}
}
