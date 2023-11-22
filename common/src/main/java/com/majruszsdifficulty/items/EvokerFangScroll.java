package com.majruszsdifficulty.items;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntityPreDamaged;
import com.majruszlibrary.events.OnItemAttributeTooltip;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.level.LevelHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.AnyRot;
import com.majruszlibrary.math.Range;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

public class EvokerFangScroll extends ScrollItem {
	public static int ATTACK_DAMAGE = 12;
	public static Range< Integer > ATTACK_RANGE = Range.of( 8, 20 );

	static {
		OnEntityPreDamaged.listen( EvokerFangScroll::increaseDamage )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.source.getDirectEntity() instanceof EvokerFangs )
			.addCondition( data->EntityHelper.getExtraTag( data.source.getDirectEntity() ) != null );

		OnItemAttributeTooltip.listen( EvokerFangScroll::addSpellInfo )
			.addCondition( data->data.itemStack.getItem() instanceof EvokerFangScroll );

		Serializables.get( DamageInfo.class )
			.define( "MajruszsProgressiveDifficultyEvokerFangDamage", Reader.integer(), s->s.damage, ( s, v )->s.damage = v );
	}

	@Override
	protected void useScroll( ItemStack itemStack, Level level, LivingEntity entity, float useRatio ) {
		super.useScroll( itemStack, level, entity, useRatio );

		double rotation = Math.toRadians( entity.getYRot() ) - Math.PI / 2.0;
		this.getAttackPattern( entity, ( int )Mth.lerp( useRatio, ATTACK_RANGE.from, ATTACK_RANGE.to ) )
			.forEach( spawnPoint->{
				EvokerFangs evokerFangs = new EvokerFangs( level, spawnPoint.pos.x, spawnPoint.pos.y, spawnPoint.pos.z, ( float )rotation, spawnPoint.cooldown, entity );
				Serializables.write( new DamageInfo( ATTACK_DAMAGE ), EntityHelper.getOrCreateExtraTag( evokerFangs ) );

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
				LevelHelper.findBlockPosOnGround( entity.level(), position.x, Range.of( position.y - 3, position.y + 3 ), position.z )
					.ifPresent( blockPos->spawnPoints.add( new SpawnPoint( AnyPos.from( blockPos ).add( 0.5, 0.0, 0.5 ).vec3(), cooldown ) ) );
			}
		}

		return spawnPoints;
	}

	private static void increaseDamage( OnEntityPreDamaged data ) {
		data.damage += Serializables.read( new DamageInfo(), EntityHelper.getExtraTag( data.source.getDirectEntity() ) ).damage - data.original;
	}

	private static void addSpellInfo( OnItemAttributeTooltip data ) {
		List.of(
			Component.translatable( "majruszsdifficulty.scrolls.attack_damage", ATTACK_DAMAGE ).withStyle( ChatFormatting.DARK_GREEN ),
			Component.translatable( "majruszsdifficulty.scrolls.attack_range", "%d-%d".formatted( ATTACK_RANGE.from, ATTACK_RANGE.to ) )
				.withStyle( ChatFormatting.DARK_GREEN )
		).forEach( component->data.add( EquipmentSlot.MAINHAND, component ) );
	}

	private record SpawnPoint( Vec3 pos, int cooldown ) {}

	private static class DamageInfo {
		int damage = 0;

		public DamageInfo( int damage ) {
			this.damage = damage;
		}

		public DamageInfo() {}
	}
}
