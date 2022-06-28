package com.majruszsdifficulty;

import com.mlib.config.ConfigGroup;
import com.mlib.config.StringListConfig;
import com.mlib.levels.LevelHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.majruszsdifficulty.MajruszsDifficulty.CONFIG_HANDLER;

/** Disabling natural spawns for entities that have not met certain conditions. */
@Mod.EventBusSubscriber
public class SpawnDisabler {
	@SubscribeEvent
	public static void disableSpawns( LivingSpawnEvent.CheckSpawn event ) {
		if( shouldEntitySpawnBeDisabled( event.getEntityLiving() ) )
			event.setResult( Event.Result.DENY );
	}

	/** Checks whether given entity should not spawn. */
	protected static boolean shouldEntitySpawnBeDisabled( Entity entity ) {
		ResourceLocation entityKey = Registry.ENTITY_TYPE.getKey( entity.getType() );
		StringListConfig forbiddenMobsConfig = Registries.SPAWN_DISABLER_CONFIG.getCurrentForbiddenList();

		return forbiddenMobsConfig.contains( entityKey.toString() ) || entity instanceof Illusioner && isVillageNearby( entity );
	}

	/**
	 Checks whether there is the village nearby.
	 It is required to disable spawns near village for Illusioner because
	 otherwise Illusioner may spawn underground while there is raid active and
	 players will have a problem to finish it.
	 */
	private static boolean isVillageNearby( Entity entity ) {
		if( !( entity.level instanceof ServerLevel ) || !LevelHelper.isEntityIn( entity, Level.OVERWORLD ) )
			return false;

		ServerLevel world = ( ServerLevel )entity.level;
		return world.findNearestMapStructure( StructureTags.VILLAGE, entity.blockPosition(), 10000, false ) != null;
	}

	public static class Config {
		public final ConfigGroup configGroup;
		public final StringListConfig forbiddenInNormal;
		public final StringListConfig forbiddenInExpert;
		public final StringListConfig forbiddenInMaster;

		public Config() {
			String normalComment = "List of entities that cannot spawn in Normal Mode.";
			this.forbiddenInNormal = new StringListConfig( "forbidden_normal", normalComment, false, "majruszsdifficulty:giant", "minecraft:illusioner", "majruszsdifficulty:elite_skeleton", "majruszsdifficulty:parasite", "majruszsdifficulty:tank" );

			String expertComment = "List of entities that cannot spawn in Expert Mode.";
			this.forbiddenInExpert = new StringListConfig( "forbidden_expert", expertComment, false, "majruszsdifficulty:parasite" );

			String masterComment = "List of entities that cannot spawn in Master Mode.";
			this.forbiddenInMaster = new StringListConfig( "forbidden_master", masterComment, false );

			String groupComment = "Disables natural spawning of given entities depending on current game progress. (entities can still spawn in structures though!)";
			this.configGroup = CONFIG_HANDLER.addNewGroup( "SpawnDisabler", groupComment );
			this.configGroup.addConfigs( this.forbiddenInNormal, this.forbiddenInExpert, this.forbiddenInMaster );
		}

		/** Returns forbidden list depending on current game stage. */
		public StringListConfig getCurrentForbiddenList() {
			return GameStage.getCurrentGameStageDependentValue( this.forbiddenInNormal, this.forbiddenInExpert, this.forbiddenInMaster );
		}
	}
}
