package com.majruszsdifficulty.effects.bleeding;

import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EffectDef;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageValue;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class BleedingConfig {
	public static boolean IS_ENABLED = true;
	public static boolean CAN_BE_CURED_WITH_GOLDEN_APPLES = true;
	public static boolean IS_APPLICABLE_TO_ANIMALS = true;
	public static boolean IS_APPLICABLE_TO_ILLAGERS = true;
	public static List< EntityType< ? > > OTHER_APPLICABLE_MOBS = List.of( EntityType.PLAYER, EntityType.VILLAGER );
	public static List< EntityType< ? > > IMMUNE_MOBS = List.of( EntityType.ZOMBIE_HORSE, EntityType.SKELETON_HORSE );
	public static GameStageValue< EffectDef > EFFECTS = GameStageValue.of(
		DefaultMap.defaultEntry( new EffectDef( ()->null, 0, 24.0f ) ),
		DefaultMap.entry( GameStage.EXPERT_ID, new EffectDef( ()->null, 1, 24.0f ) ),
		DefaultMap.entry( GameStage.MASTER_ID, new EffectDef( ()->null, 2, 24.0f ) )
	);

	static {
		Serializables.getStatic( com.majruszsdifficulty.data.Config.class )
			.define( "bleeding", BleedingConfig.class );

		Serializables.getStatic( BleedingConfig.class )
			.define( "is_enabled", Reader.bool(), ()->IS_ENABLED, v->IS_ENABLED = v )
			.define( "can_be_cured_with_golden_apples", Reader.bool(), ()->CAN_BE_CURED_WITH_GOLDEN_APPLES, v->CAN_BE_CURED_WITH_GOLDEN_APPLES = v )
			.define( "is_applicable_to_animals", Reader.bool(), ()->IS_APPLICABLE_TO_ANIMALS, v->IS_APPLICABLE_TO_ANIMALS = v )
			.define( "is_applicable_to_pillagers", Reader.bool(), ()->IS_APPLICABLE_TO_ILLAGERS, v->IS_APPLICABLE_TO_ILLAGERS = v )
			.define( "other_applicable_mobs", Reader.list( Reader.entityType() ), ()->OTHER_APPLICABLE_MOBS, v->OTHER_APPLICABLE_MOBS = v )
			.define( "immune_mobs", Reader.list( Reader.entityType() ), ()->IMMUNE_MOBS, v->IMMUNE_MOBS = v )
			.define( "effect", Reader.map( Reader.custom( EffectDef::new ) ), ()->EFFECTS.get(), v->EFFECTS.set( v ) )
			.define( "sources", Sources.class );
	}

	public static class Sources {}
}