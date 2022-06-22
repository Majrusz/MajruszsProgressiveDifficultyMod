package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.majruszsdifficulty.gamemodifiers.contexts.DamagedContext;
import com.mlib.levels.LevelHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;

public class EndermanTeleportAttack extends GameModifier {
	static final DamagedContext ON_DAMAGED = new DamagedContext();

	static {
		ON_DAMAGED.addCondition( new ICondition.Excludable() );
		ON_DAMAGED.addCondition( new ICondition.GameStage( GameStage.Stage.MASTER ) );
		ON_DAMAGED.addCondition( new ICondition.Chance( 0.5, true ) );
		ON_DAMAGED.addCondition( new ICondition.Context<>( DamagedContext.Data.class, data->data.attacker instanceof EnderMan ) );
	}

	public EndermanTeleportAttack() {
		super( "EndermanTeleport", "Enderman attack may teleport the player somewhere nearby.", ON_DAMAGED );
	}

	@Override
	public void execute( Object data ) {
		if( data instanceof DamagedContext.Data damagedData ) {
			LivingEntity target = damagedData.target;
			if( LevelHelper.teleportNearby( target, damagedData.level, 10.0 ) && target instanceof ServerPlayer player ) {
				Registries.BASIC_TRIGGER.trigger( player, "enderman_teleport_attack" );
			}
		}
	}
}
