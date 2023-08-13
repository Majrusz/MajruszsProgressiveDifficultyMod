package com.majruszsdifficulty.undeadarmy.commands;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.mlib.modhelper.AutoInstance;
import com.mlib.commands.Command;
import com.mlib.commands.CommandData;
import com.mlib.text.TextHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

@AutoInstance
public class UndeadArmyListAllCommand extends Command {
	public UndeadArmyListAllCommand() {
		this.newBuilder()
			.literal( "undeadarmy" )
			.literal( "list" )
			.hasPermission( 4 )
			.execute( this::handle );
	}

	private int handle( CommandData data ) throws CommandSyntaxException {
		List< UndeadArmy > undeadArmies = Registries.getUndeadArmyManager().getUndeadArmies();
		if( !undeadArmies.isEmpty() ) {
			MutableComponent component = Component.translatable( "commands.undeadarmy.list" );
			undeadArmies.forEach( undeadArmy->{
				component.append( "\n- " );
				component.append( Component.translatable( "majruszsdifficulty.undead_army.title" ) );
				component.append( " " );
				component.append( Component.translatable( "majruszsdifficulty.undead_army.wave", TextHelper.toRoman( Math.max( undeadArmy.currentWave, 1 ) ) ) );
				component.append( String.format( " (%s)", undeadArmy.positionToAttack.toShortString() ) );
			} );
			data.source.sendSuccess( ()->component, true );
			return 0;
		}

		data.source.sendSuccess( ()->Component.translatable( "commands.undeadarmy.list_empty" ), true );
		return -1;
	}
}
