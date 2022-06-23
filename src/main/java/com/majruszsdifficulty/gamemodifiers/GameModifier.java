package com.majruszsdifficulty.gamemodifiers;

import com.mlib.config.ConfigGroup;

import static com.majruszsdifficulty.MajruszsDifficulty.GAME_MODIFIERS_GROUP;

/**
 If you would ever wonder what the hell is going over here then let me explain...

 Most of this mod changes are "game modifiers", which change or extend certain game
 events with new functionalities via contexts. The context handles specific moment
 in the gameplay (for instance it can be a moment when player is about to take the damage)
 and it is made of conditions, which determines whether we should consider that context.
 It allows for more generic and reusable
 */
public abstract class GameModifier {
	protected final Context[] contexts;
	protected final ConfigGroup configGroup;

	public GameModifier( String configName, String configComment, Context... contexts ) {
		this.contexts = contexts;
		this.configGroup = GAME_MODIFIERS_GROUP.addNewGroup( configName, configComment );

		assert this.contexts.length > 0;
		for( Context context : this.contexts ) {
			context.setup( this );
		}
	}

	public abstract void execute( Object data );

	public int getContextsLength() {
		return this.contexts.length;
	}

	public ConfigGroup getConfigGroup() {
		return this.configGroup;
	}
}
