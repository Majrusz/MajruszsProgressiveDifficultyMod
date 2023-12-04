package com.majruszsdifficulty.undeadarmy;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UndeadArmyConfig {
	public static boolean IS_ENABLED = true;
	public static boolean RESET_ALL_PARTICIPANTS_KILLS = true;
	public static float WAVE_DURATION = 1200.0f;
	public static float PREPARING_DURATION = 10.0f;
	public static float HIGHLIGHT_DELAY = 300.0f;
	public static float EXTRA_PLAYER_RATIO = 0.5f;
	public static int AREA_RADIUS = 70;
	public static int KILL_REQUIREMENT = 100;
	public static int KILL_REQUIREMENT_FIRST = 25;
	public static int KILL_REQUIREMENT_WARNING = 3;
	public static List< WaveDef > WAVE_DEFS = List.of(
		new WaveDef(
			List.of(
				new MobDef( EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_1_mob", 2 ),
				new MobDef( EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_1_mob", 2 ),
				new MobDef( EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_1_mob", 2 ),
				new MobDef( EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_1_mob", 2 )
			),
			null,
			GameStage.NORMAL_ID,
			8
		),
		new WaveDef(
			List.of(
				new MobDef( EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_2_mob", 2 ),
				new MobDef( EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_2_mob", 3 ),
				new MobDef( EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_2_mob", 3 ),
				new MobDef( EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_2_mob", 2 )
			),
			null,
			GameStage.NORMAL_ID,
			16
		),
		new WaveDef(
			List.of(
				new MobDef( EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_3_mob", 3 ),
				new MobDef( EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_3_skeleton", 3 ),
				new MobDef( EntityType.HUSK, "majruszsdifficulty:undead_army/wave_3_mob", 3 ),
				new MobDef( EntityType.STRAY, "majruszsdifficulty:undead_army/wave_3_skeleton", 3 )
			),
			new MobDef( MajruszsDifficulty.TANK_ENTITY ),
			GameStage.NORMAL_ID,
			24
		),
		new WaveDef(
			List.of(
				new MobDef( EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_4_mob", 2 ),
				new MobDef( EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_4_mob", 1 ),
				new MobDef( EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_4_skeleton", 2 ),
				new MobDef( EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_4_mob", 1 ),
				new MobDef( MajruszsDifficulty.TANK_ENTITY ),
				new MobDef( EntityType.HUSK, "majruszsdifficulty:undead_army/wave_4_mob", 3 ),
				new MobDef( EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_4_mob", 1 ),
				new MobDef( EntityType.STRAY, "majruszsdifficulty:undead_army/wave_4_skeleton", 3 ),
				new MobDef( EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_4_mob", 1 )
			),
			null,
			GameStage.EXPERT_ID,
			32
		),
		new WaveDef(
			List.of(
				new MobDef( EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_5_mob", 1 ),
				new MobDef( EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_5_skeleton", 1 ),
				new MobDef( EntityType.STRAY, "majruszsdifficulty:undead_army/wave_5_skeleton", 3 ),
				new MobDef( EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_5_mob", 2 ),
				new MobDef( MajruszsDifficulty.TANK_ENTITY ),
				new MobDef( EntityType.HUSK, "majruszsdifficulty:undead_army/wave_5_mob", 3 ),
				new MobDef( EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_5_mob", 2 )
			),
			new MobDef( MajruszsDifficulty.CERBERUS_ENTITY ),
			GameStage.EXPERT_ID,
			40
		),
		new WaveDef(
			List.of(
				new MobDef( EntityType.ZOMBIE, "majruszsdifficulty:undead_army/wave_6_mob", 1 ),
				new MobDef( EntityType.SKELETON, "majruszsdifficulty:undead_army/wave_6_skeleton", 1 ),
				new MobDef( MajruszsDifficulty.TANK_ENTITY ),
				new MobDef( EntityType.STRAY, "majruszsdifficulty:undead_army/wave_6_skeleton", 1 ),
				new MobDef( EntityType.HUSK, "majruszsdifficulty:undead_army/wave_6_mob", 1 ),
				new MobDef( EntityType.WITHER_SKELETON, "majruszsdifficulty:undead_army/wave_6_wither_skeleton", 1 ),
				new MobDef( MajruszsDifficulty.CERBERUS_ENTITY )
			),
			new MobDef( MajruszsDifficulty.GIANT_ENTITY, "majruszsdifficulty:undead_army/wave_6_mob", 1 ),
			GameStage.MASTER_ID,
			48
		)
	);

	static {
		Serializables.getStatic( UndeadArmyConfig.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "reset_all_participants_kills", Reader.bool(), ()->RESET_ALL_PARTICIPANTS_KILLS, v->RESET_ALL_PARTICIPANTS_KILLS = v )
			.define( "wave_duration", Reader.number(), ()->WAVE_DURATION, v->WAVE_DURATION = Range.of( 300.0f, 3600.0f ).clamp( v ) )
			.define( "preparing_duration", Reader.number(), ()->PREPARING_DURATION, v->PREPARING_DURATION = Range.of( 1.0f, 60.0f ).clamp( v ) )
			.define( "highlight_delay", Reader.number(), ()->HIGHLIGHT_DELAY, v->HIGHLIGHT_DELAY = Range.of( 30.0f, 3600.0f ).clamp( v ) )
			.define( "extra_size_ratio_per_player", Reader.number(), ()->EXTRA_PLAYER_RATIO, v->EXTRA_PLAYER_RATIO = Range.of( 0.0f, 1.0f ).clamp( v ) )
			.define( "area_radius", Reader.integer(), ()->AREA_RADIUS, v->AREA_RADIUS = Range.of( 35, 140 ).clamp( v ) )
			.define( "kill_requirement", Reader.integer(), ()->KILL_REQUIREMENT, v->KILL_REQUIREMENT = Range.of( 0, 1000 ).clamp( v ) )
			.define( "kill_requirement_first", Reader.integer(), ()->KILL_REQUIREMENT_FIRST, v->KILL_REQUIREMENT_FIRST = Range.of( 1, 1000 ).clamp( v ) )
			.define( "kill_requirement_warning", Reader.integer(), ()->KILL_REQUIREMENT_WARNING, v->KILL_REQUIREMENT_WARNING = Range.of( 1, 1000 ).clamp( v ) )
			.define( "waves", Reader.list( Reader.custom( WaveDef::new ) ), ()->WAVE_DEFS, v->WAVE_DEFS = v );

		Serializables.get( WaveDef.class )
			.define( "mobs", Reader.list( Reader.custom( MobDef::new ) ), s->s.mobDefs, ( s, v )->s.mobDefs = v )
			.define( "boss", Reader.optional( Reader.custom( MobDef::new ) ), s->s.bossDef, ( s, v )->s.bossDef = v )
			.define( "game_stage", Reader.string(), s->s.gameStage.getId(), ( s, v )->s.gameStage = GameStageHelper.find( v ) )
			.define( "exp", Reader.integer(), s->s.experience, ( s, v )->s.experience = Range.of( 0, 1000 ).clamp( v ) );

		Serializables.get( MobDef.class )
			.define( "type", Reader.entityType(), s->s.type.get(), ( s, v )->s.type = ()->v )
			.define( "equipment", Reader.optional( Reader.location() ), s->s.equipment, ( s, v )->s.equipment = v )
			.define( "count", Reader.integer(), s->s.count, ( s, v )->s.count = Range.of( 1, 20 ).clamp( v ) );
	}

	public static class WaveDef {
		public List< MobDef > mobDefs = new ArrayList<>();
		public MobDef bossDef;
		public GameStage gameStage;
		public int experience = 0;

		public WaveDef( List< MobDef > mobDefs, MobDef bossDef, String gameStageId, int experience ) {
			this.mobDefs = mobDefs;
			this.bossDef = bossDef;
			this.gameStage = GameStageHelper.find( gameStageId );
			this.experience = experience;
		}

		public WaveDef() {}
	}

	public static class MobDef {
		public Supplier< ? extends EntityType< ? > > type;
		public ResourceLocation equipment;
		public int count = 1;

		public MobDef( Supplier< ? extends EntityType< ? > > type, String equipment, int count ) {
			this.type = type;
			this.equipment = equipment != null ? new ResourceLocation( equipment ) : null;
			this.count = count;
		}

		public MobDef( EntityType< ? > type, String equipment, int count ) {
			this( ()->type, equipment, count );
		}

		public MobDef( Supplier< ? extends EntityType< ? > > type ) {
			this( type, null, 1 );
		}

		public MobDef() {}
	}
}
