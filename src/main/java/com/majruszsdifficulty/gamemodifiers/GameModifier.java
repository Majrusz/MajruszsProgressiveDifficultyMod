package com.majruszsdifficulty.gamemodifiers;

import com.mlib.config.ConfigGroup;

import static com.majruszsdifficulty.MajruszsDifficulty.GAME_MODIFIERS_GROUP;

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
