package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import com.mlib.levels.LevelHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;

public class EndermanTeleportAttack extends GameModifier {
	public EndermanTeleportAttack() {
		super( GameModifier.DEFAULT, "EndermanTeleport", "Enderman attack may teleport the player somewhere nearby." );

		OnDamagedContext onDamaged = new OnDamagedContext( this::teleportPlayerRandomly );
		onDamaged.addCondition( new CustomConditions.GameStage( GameStage.Stage.MASTER ) )
			.addCondition( new CustomConditions.CRDChance( 0.5 ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( new Condition.ContextOnDamaged( data->data.attacker instanceof EnderMan && data.level != null ) )
			.addCondition( new OnDamagedContext.DirectDamage() );

		this.addContext( onDamaged );
	}

	private void teleportPlayerRandomly( OnDamagedData data ) {
		assert data.level != null;
		LivingEntity target = data.target;
		if( LevelHelper.teleportNearby( target, data.level, 10.0 ) && target instanceof ServerPlayer player ) {
			Registries.BASIC_TRIGGER.trigger( player, "enderman_teleport_attack" );
		}
	}
}
