package com.majruszsdifficulty.items;

import com.mlib.annotations.AutoInstance;
import com.mlib.data.SerializableHelper;
import com.mlib.data.SerializableStructure;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnPreDamaged;
import com.mlib.levels.LevelHelper;
import com.mlib.math.AnyPos;
import com.mlib.math.AnyRot;
import com.mlib.math.Range;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class EvokerFangScrollItem extends ScrollItem {
	@Override
	protected void useScroll( ItemStack itemStack, Level level, LivingEntity entity, float useRatio ) {
		super.useScroll( itemStack, level, entity, useRatio );

		double rotation = Math.toRadians( entity.getYRot() ) - Math.PI / 2.0;
		this.getAttackPattern( entity )
			.forEach( spawnPoint->{
				EvokerFangs evokerFangs = new EvokerFangs( level, spawnPoint.pos.x, spawnPoint.pos.y, spawnPoint.pos.z, ( float )rotation, spawnPoint.cooldown, entity );
				SerializableHelper.modify( DamageInfo::new, evokerFangs.getPersistentData(), damageInfo->{
					damageInfo.extraDamage = ( int )( useRatio * 6.0f );
				} );

				level.addFreshEntity( evokerFangs );
			} );
	}

	@Override
	protected Component getEffectTooltip() {
		return Component.translatable( "item.majruszsdifficulty.evoker_fang_scroll.effect" ).withStyle( ChatFormatting.BLUE );
	}

	@Override
	protected SoundEvent getPrepareSound() {
		return SoundEvents.EVOKER_PREPARE_SUMMON;
	}

	@Override
	protected SoundEvent getCastSound() {
		return SoundEvents.EVOKER_CAST_SPELL;
	}

	private List< SpawnPoint > getAttackPattern( LivingEntity entity ) {
		List< SpawnPoint > spawnPoints = new ArrayList<>();
		AnyRot lookRotation = EntityHelper.getLookRotation( entity );
		for( int x = 0; x <= 16; ++x ) {
			for( int z = -1; z <= 1; ++z ) {
				int cooldown = Math.abs( x ) * 2 + 8;
				Vec3 position = AnyPos.from( entity.position() ).floor().add( AnyPos.from( x + 1, 0, z ).rot( lookRotation ).round() ).vec3();
				LevelHelper.findBlockPosOnGround( entity.level(), position.x, new Range<>( position.y - 3, position.y + 3 ), position.z )
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

	private static class DamageInfo extends SerializableStructure {
		int extraDamage = 0;

		public DamageInfo() {
			this.define( "MajruszsProgressiveDifficultyEvokerFangDamage", ()->this.extraDamage, x->this.extraDamage = x );
		}
	}
}
