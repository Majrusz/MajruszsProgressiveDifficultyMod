package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.levels.LevelHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;

public class EndermanTeleportAttack extends GameModifier {
	static final OnDamagedContext ON_DAMAGED = new OnDamagedContext( EndermanTeleportAttack::teleportPlayerRandomly );

	static {
		ON_DAMAGED.addCondition( new CustomConditions.GameStage( GameStage.Stage.MASTER ) );
		ON_DAMAGED.addCondition( new CustomConditions.CRDChance( 0.5 ) );
		ON_DAMAGED.addCondition( new Condition.Excludable() );
		ON_DAMAGED.addCondition( new Condition.ContextOnDamaged( data->data.attacker instanceof EnderMan && data.level != null ) );
		ON_DAMAGED.addCondition( new OnDamagedContext.DirectDamage() );
	}

	public EndermanTeleportAttack() {
		super( GameModifier.DEFAULT, "EndermanTeleport", "Enderman attack may teleport the player somewhere nearby.", ON_DAMAGED );
	}

	private static void teleportPlayerRandomly( com.mlib.gamemodifiers.GameModifier gameModifier, OnDamagedContext.Data data ) {
		assert data.level != null;
		LivingEntity target = data.target;
		if( LevelHelper.teleportNearby( target, data.level, 10.0 ) && target instanceof ServerPlayer player ) {
			Registries.BASIC_TRIGGER.trigger( player, "enderman_teleport_attack" );
		}
	}
}
