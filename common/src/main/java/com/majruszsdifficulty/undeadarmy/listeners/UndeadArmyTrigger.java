package com.majruszsdifficulty.undeadarmy.listeners;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntityDied;
import com.majruszlibrary.text.TextHelper;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import com.majruszsdifficulty.undeadarmy.UndeadArmyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class UndeadArmyTrigger {
	static {
		OnEntityDied.listen( UndeadArmyTrigger::update )
			.addCondition( data->UndeadArmyConfig.KILL_REQUIREMENT > 0 )
			.addCondition( data->data.target.getMobType() == MobType.UNDEAD )
			.addCondition( data->!UndeadArmyHelper.isPartOfUndeadArmy( data.target ) )
			.addCondition( data->data.attacker instanceof ServerPlayer )
			.addCondition( data->EntityHelper.isIn( data.attacker, Level.OVERWORLD ) );

		Serializables.get( Progress.class )
			.define( "UndeadArmyUndeadLeft", Reader.integer(), s->s.undeadLeft, ( s, v )->s.undeadLeft = v );
	}

	private static void update( OnEntityDied data ) {
		Player player = ( Player )data.attacker;

		Serializables.modify( new Progress(), EntityHelper.getOrCreateExtraTag( player ), progress->{
			--progress.undeadLeft;
			if( progress.undeadLeft <= 0 && UndeadArmyHelper.tryToSpawn( player ) ) {
				progress.undeadLeft = UndeadArmyConfig.KILL_REQUIREMENT;
			} else if( progress.undeadLeft == UndeadArmyConfig.KILL_REQUIREMENT_WARNING ) {
				player.sendSystemMessage( TextHelper.translatable( "majruszsdifficulty.undead_army.warning" ).withStyle( ChatFormatting.DARK_PURPLE ) );
			}
		} );
	}

	public static class Progress {
		public int undeadLeft = UndeadArmyConfig.KILL_REQUIREMENT_FIRST;
	}
}
