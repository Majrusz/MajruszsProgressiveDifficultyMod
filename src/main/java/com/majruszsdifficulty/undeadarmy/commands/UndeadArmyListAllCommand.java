package com.majruszsdifficulty.undeadarmy.commands;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.undeadarmy.UndeadArmy;
import com.mlib.annotations.AutoInstance;
import com.mlib.commands.Command;
import com.mlib.commands.CommandData;
import com.mlib.text.TextHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

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
			MutableComponent component = new TranslatableComponent( "commands.undeadarmy.list" );
			undeadArmies.forEach( undeadArmy->{
				component.append( "\n- " );
				component.append( new TranslatableComponent( "majruszsdifficulty.undead_army.title" ) );
				component.append( " " );
				component.append( new TranslatableComponent( "majruszsdifficulty.undead_army.wave", TextHelper.toRoman( Math.max( undeadArmy.currentWave, 1 ) ) ) );
				component.append( String.format( " (%s)", undeadArmy.positionToAttack.toShortString() ) );
			} );
			data.source.sendSuccess( component, true );
			return 0;
		}

		data.source.sendSuccess( new TranslatableComponent( "commands.undeadarmy.list_empty" ), true );
		return -1;
	}
}
