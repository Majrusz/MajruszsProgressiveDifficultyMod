package com.majruszsdifficulty.items;

import com.majruszsdifficulty.Registries;
import com.mlib.modhelper.AutoInstance;
import com.mlib.data.SerializableHelper;
import com.mlib.data.SerializableStructure;
import com.mlib.entities.EntityHelper;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.OnItemAttributeTooltip;
import com.mlib.contexts.OnPreDamaged;
import com.mlib.levels.LevelHelper;
import com.mlib.math.AnyPos;
import com.mlib.math.AnyRot;
import com.mlib.math.Range;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class EvokerFangScrollItem extends ScrollItem {
	public static int ATTACK_DAMAGE = 12;
	public static Range< Integer > ATTACK_RANGE = new Range<>( 8, 20 );

	@Override
	protected void useScroll( ItemStack itemStack, Level level, LivingEntity entity, float useRatio ) {
		super.useScroll( itemStack, level, entity, useRatio );

		double rotation = Math.toRadians( entity.getYRot() ) - Math.PI / 2.0;
		this.getAttackPattern( entity, ( int )Mth.lerp( useRatio, ATTACK_RANGE.from, ATTACK_RANGE.to ) )
			.forEach( spawnPoint->{
				EvokerFangs evokerFangs = new EvokerFangs( level, spawnPoint.pos.x, spawnPoint.pos.y, spawnPoint.pos.z, ( float )rotation, spawnPoint.cooldown, entity );
				SerializableHelper.modify( DamageInfo::new, evokerFangs.getPersistentData(), damageInfo->damageInfo.extraDamage = ( int )( ATTACK_DAMAGE - 6.0f ) );

				level.addFreshEntity( evokerFangs );
			} );
	}

	@Override
	protected SoundEvent getPrepareSound() {
		return SoundEvents.EVOKER_PREPARE_SUMMON;
	}

	@Override
	protected SoundEvent getCastSound() {
		return SoundEvents.EVOKER_CAST_SPELL;
	}

	private List< SpawnPoint > getAttackPattern( LivingEntity entity, int attackLength ) {
		List< SpawnPoint > spawnPoints = new ArrayList<>();
		AnyRot lookRotation = EntityHelper.getLookRotation( entity );
		for( int x = 0; x <= attackLength; ++x ) {
			for( int z = -1; z <= 1; ++z ) {
				int cooldown = Math.abs( x ) + 4;
				Vec3 position = AnyPos.from( entity.position() ).floor().add( AnyPos.from( x, 0, z ).rot( lookRotation ).round() ).vec3();
				LevelHelper.findBlockPosOnGround( entity.level, position.x, new Range<>( position.y - 3, position.y + 3 ), position.z )
					.ifPresent( blockPos->spawnPoints.add( new SpawnPoint( AnyPos.from( blockPos ).add( 0.5, 0.0, 0.5 ).vec3(), cooldown ) ) );
			}
		}

		return spawnPoints;
	}

	private record SpawnPoint( Vec3 pos, int cooldown ) {}

	@AutoInstance
	public static class Spell {
		public Spell() {
			OnPreDamaged.listen( this::increaseDamage )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->data.source.getDirectEntity() instanceof EvokerFangs ) )
				.addCondition( OnPreDamaged.dealtAnyDamage() );
		}

		private void increaseDamage( OnPreDamaged.Data data ) {
			DamageInfo damageInfo = SerializableHelper.read( DamageInfo::new, data.source.getDirectEntity().getPersistentData() );

			data.extraDamage += damageInfo.extraDamage;
		}
	}

	@AutoInstance
	public static class Tooltip {
		public Tooltip() {
			OnItemAttributeTooltip.listen( this::addSpellInfo )
				.addCondition( Condition.predicate( data->Registries.EVOKER_FANG_SCROLL.isPresent() ) )
				.addCondition( Condition.predicate( data->data.itemStack.is( Registries.EVOKER_FANG_SCROLL.get() ) ) );
		}

		private void addSpellInfo( OnItemAttributeTooltip.Data data ) {
			List.of(
				new TranslatableComponent( "majruszsdifficulty.scrolls.attack_damage", ATTACK_DAMAGE ).withStyle( ChatFormatting.DARK_GREEN ),
				new TranslatableComponent( "majruszsdifficulty.scrolls.attack_range", "%d-%d".formatted( ATTACK_RANGE.from, ATTACK_RANGE.to ) )
					.withStyle( ChatFormatting.DARK_GREEN )
			).forEach( component->data.add( EquipmentSlot.MAINHAND, component ) );
		}
	}

	private static class DamageInfo extends SerializableStructure {
		int extraDamage = 0;

		public DamageInfo() {
			this.defineInteger( "MajruszsProgressiveDifficultyEvokerFangDamage", ()->this.extraDamage, x->this.extraDamage = x );
		}
	}
}
