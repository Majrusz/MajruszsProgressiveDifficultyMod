package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.DifficultyModifier;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import com.mlib.levels.LevelHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;

public class EndermanTeleportAttack extends DifficultyModifier {
	public EndermanTeleportAttack() {
		super( DifficultyModifier.DEFAULT, "EndermanTeleport", "Enderman attack may teleport the player somewhere nearby." );

		OnDamagedContext onDamaged = new OnDamagedContext( this::teleportPlayerRandomly );
		onDamaged.addCondition( new CustomConditions.GameStage( GameStage.Stage.MASTER ) )
			.addCondition( new CustomConditions.CRDChance( 0.5 ) )
			.addCondition( new Condition.Excludable() )
			.addCondition( data->data.level != null )
			.addCondition( data->data.attacker instanceof EnderMan )
			.addCondition( data->data.source.getDirectEntity() == data.attacker );

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
