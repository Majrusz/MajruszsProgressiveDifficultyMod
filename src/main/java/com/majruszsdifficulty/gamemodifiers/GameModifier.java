package com.majruszsdifficulty.gamemodifiers;

import com.mlib.config.ConfigGroup;

import static com.majruszsdifficulty.MajruszsDifficulty.GAME_MODIFIERS_GROUP;

public abstract class GameModifier {
	protected final Context[] contexts;
	protected final ConfigGroup configGroup;

	public GameModifier( String configName, String configComment, Context... contexts ) {
		this.contexts = contexts;
		this.configGroup = GAME_MODIFIERS_GROUP.addGroup( new ConfigGroup( configName, configComment ) );

		assert this.contexts.length > 0;
		for( Context context : this.contexts ) {
			context.setup( this );

			if( this.contexts.length == 1 ) {
				context.setupConfig( this.configGroup ); // if there is only one context then we do not want to create redundant subgroup
			} else {
				ConfigGroup contextConfig = context.createConfigGroup();
				context.setupConfig( contextConfig );
				this.configGroup.addGroup( contextConfig );
			}
		}
	}

	public abstract void execute( Object data );
}
