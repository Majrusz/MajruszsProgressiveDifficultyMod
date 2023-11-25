package com.majruszsdifficulty.features;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EffectDef;
import com.majruszlibrary.events.OnEntityDamaged;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.level.LevelHelper;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class MobsApplyEffects {
	private static List< ExtraEffectDef > EXTRA_EFFECTS = List.of(
		new ExtraEffectDef(
			GameStage.EXPERT_ID,
			0.25f,
			true,
			EntityType.SPIDER,
			new EffectDef( ()->MobEffects.POISON, 0, 7.0f )
		),
		new ExtraEffectDef(
			GameStage.EXPERT_ID,
			0.5f,
			true,
			EntityType.SLIME,
			new EffectDef( ()->MobEffects.MOVEMENT_SLOWDOWN, 0, 7.0f )
		),
		new ExtraEffectDef(
			GameStage.MASTER_ID,
			0.5f,
			true,
			EntityType.SHULKER,
			new EffectDef( ()->MobEffects.BLINDNESS, 0, 7.0f )
		),
		new ExtraEffectDef(
			GameStage.MASTER_ID,
			0.75f,
			true,
			EntityType.PHANTOM,
			new EffectDef( ()->MobEffects.LEVITATION, 0, 7.0f )
		)
	);

	static {
		OnEntityDamaged.listen( MobsApplyEffects::tryToApply )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.attacker != null );

		Serializables.getStatic( Config.Features.class )
			.define( "mobs_apply_effects", Reader.list( Reader.custom( ExtraEffectDef::new ) ), ()->EXTRA_EFFECTS, v->EXTRA_EFFECTS = v );

		Serializables.get( ExtraEffectDef.class )
			.define( "required_game_stage", Reader.string(), s->s.requiredGameStage.getId(), ( s, v )->s.requiredGameStage = GameStageHelper.find( v ) )
			.define( "chance", Reader.number(), s->s.chance, ( s, v )->s.chance = Range.CHANCE.clamp( v ) )
			.define( "is_scaled_by_crd", Reader.bool(), s->s.isScaledByCrd, ( s, v )->s.isScaledByCrd = v )
			.define( "entity_type", Reader.entityType(), s->s.entityType, ( s, v )->s.entityType = v )
			.define( "effect", Reader.custom( EffectDef::new ), s->s.effect, ( s, v )->s.effect = v );
	}

	private static void tryToApply( OnEntityDamaged data ) {
		GameStage gameStage = GameStageHelper.determineGameStage( data );
		float crd = ( float )LevelHelper.getClampedRegionalDifficultyAt( data.getLevel(), data.target.blockPosition() );
		for( ExtraEffectDef extraEffect : EXTRA_EFFECTS ) {
			if( data.attacker.getType() != extraEffect.entityType ) {
				continue;
			}

			if( extraEffect.requiredGameStage.getOrdinal() > gameStage.getOrdinal() ) {
				continue;
			}

			if( !Random.check( extraEffect.isScaledByCrd ? extraEffect.chance * crd : extraEffect.chance ) ) {
				continue;
			}

			data.target.addEffect( extraEffect.effect.toEffectInstance() );
		}
	}

	public static class ExtraEffectDef {
		public GameStage requiredGameStage = GameStageHelper.find( GameStage.NORMAL_ID );
		public float chance;
		public boolean isScaledByCrd;
		public EntityType< ? > entityType;
		public EffectDef effect;

		public ExtraEffectDef( String gameStageId, float chance, boolean isScaledByCRD, EntityType< ? > entityType, EffectDef effect ) {
			this.requiredGameStage = GameStageHelper.find( gameStageId );
			this.chance = chance;
			this.isScaledByCrd = isScaledByCRD;
			this.entityType = entityType;
			this.effect = effect;
		}

		public ExtraEffectDef() {}
	}
}
