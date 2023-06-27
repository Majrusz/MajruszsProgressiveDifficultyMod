package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig;
import com.majruszsdifficulty.gamemodifiers.list.groups.PiglinsInGroup;
import com.majruszsdifficulty.gamemodifiers.list.groups.SkeletonsInGroup;
import com.majruszsdifficulty.gamemodifiers.list.groups.ZombiesInGroup;
import com.majruszsdifficulty.undeadarmy.UndeadArmyPatrol;
import com.mlib.annotations.AutoInstance;
import com.mlib.commands.Command;
import com.mlib.commands.CommandData;
import com.mlib.entities.EntityHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;

import java.util.function.Supplier;

@AutoInstance
public class SummonMobGroupCommand extends Command {
	public SummonMobGroupCommand() {
		this.newBuilder()
			.literal( "summongroup" )
			.enumeration( GroupType.class )
			.hasPermission( 4 )
			.execute( this::handle );
	}

	private int handle( CommandData data ) throws CommandSyntaxException {
		Entity entity = this.getOptionalEntityOrPlayer( data );
		if( entity.level() instanceof ServerLevel level ) {
			GroupType groupType = this.getEnumeration( data, GroupType.class );
			PathfinderMob mob = EntityHelper.createSpawner( groupType.entityType, level )
				.position( entity.position() )
				.mobSpawnType( MobSpawnType.COMMAND )
				.spawn();
			if( mob != null ) {
				Registries.HELPER.findInstance( groupType.clazz ).ifPresent( instance->instance.get().spawn( mob ) );
			}
		}

		return 0;
	}

	enum GroupType {
		ZOMBIE( EntityType.ZOMBIE, ZombiesInGroup.class ),
		SKELETONS( EntityType.SKELETON, SkeletonsInGroup.class ),
		PIGLINS( EntityType.PIGLIN, PiglinsInGroup.class ),
		UNDEAD_ARMY( EntityType.SKELETON, UndeadArmyPatrol.class );

		final EntityType< ? extends PathfinderMob > entityType;
		final Class< ? extends Supplier< MobGroupConfig > > clazz;

		GroupType( EntityType< ? extends PathfinderMob > entityType, Class< ? extends Supplier< MobGroupConfig > > clazz ) {
			this.entityType = entityType;
			this.clazz = clazz;
		}
	}
}
