package com.majruszsdifficulty.gamemodifiers;

import com.mlib.config.ConfigGroup;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public abstract class Context {
	final ICondition[] conditions;
	protected GameModifier gameModifier = null;

	public Context( ICondition... conditions ) {
		this.conditions = conditions;
	}

	public void setup( GameModifier gameModifier ) {
		this.gameModifier = gameModifier;
	}

	public void setupConfig( ConfigGroup config ) {
		for( ICondition condition : this.conditions ) {
			condition.setupConfig( config );
		}
	}

	abstract public ConfigGroup createConfigGroup();

	public boolean check( Object data ) {
		for( ICondition condition : this.conditions ) {
			if( !condition.check( this.gameModifier, data ) ) {
				return false;
			}
		}

		return true;
	}

	public static abstract class Data {
		@Nullable
		final Entity entity;

		public Data( @Nullable Entity entity ) {
			this.entity = entity;
		}
	}
}
