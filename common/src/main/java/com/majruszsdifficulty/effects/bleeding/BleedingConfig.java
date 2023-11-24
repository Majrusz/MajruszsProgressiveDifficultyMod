package com.majruszsdifficulty.effects.bleeding;

import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EffectDef;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class BleedingConfig {
	public static boolean IS_ENABLED = true;
	public static boolean IS_APPLICABLE_TO_ANIMALS = true;
	public static boolean IS_APPLICABLE_TO_ILLAGERS = true;
	public static List< EntityType< ? > > APPLICABLE_MOBS = List.of( EntityType.PLAYER, EntityType.VILLAGER );
	public static GameStageValue< EffectDef > EFFECTS = GameStageValue.of(
		DefaultMap.defaultEntry( new EffectDef( MajruszsDifficulty.Effects.BLEEDING, 0, 24.0f ) ),
		DefaultMap.entry( GameStage.EXPERT_ID, new EffectDef( MajruszsDifficulty.Effects.BLEEDING, 1, 24.0f ) ),
		DefaultMap.entry( GameStage.MASTER_ID, new EffectDef( MajruszsDifficulty.Effects.BLEEDING, 2, 24.0f ) )
	);

	static {
		Serializables.getStatic( com.majruszsdifficulty.data.Config.class )
			.define( "bleeding", BleedingConfig.class );

		Serializables.getStatic( BleedingConfig.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "is_applicable_to_animals", Reader.bool(), ()->IS_APPLICABLE_TO_ANIMALS, v->IS_APPLICABLE_TO_ANIMALS = v )
			.define( "is_applicable_to_pillagers", Reader.bool(), ()->IS_APPLICABLE_TO_ILLAGERS, v->IS_APPLICABLE_TO_ILLAGERS = v )
			.define( "applicable_mobs", Reader.list( Reader.entityType() ), ()->APPLICABLE_MOBS, v->APPLICABLE_MOBS = v )
			.define( "effect", Reader.map( Reader.custom( EffectDef::new ) ), ()->EFFECTS.get(), v->EFFECTS.set( v ) )
			.define( "sources", Sources.class );
	}

	public static class Sources {}
}