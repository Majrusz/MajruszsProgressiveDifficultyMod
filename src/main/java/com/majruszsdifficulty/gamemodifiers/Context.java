package com.majruszsdifficulty.gamemodifiers;

import com.mlib.config.ConfigGroup;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class Context {
	final List< ICondition > conditions = new ArrayList<>();
	final List< Config > configs = new ArrayList<>();
	final String configName;
	final String configComment;
	protected GameModifier gameModifier = null;

	public Context( String configName, String configComment ) {
		this.configName = configName;
		this.configComment = configComment;
	}

	public void setup( GameModifier gameModifier ) {
		this.gameModifier = gameModifier;

		ConfigGroup parentGroup = getParentConfigGroup();
		for( ICondition condition : this.conditions ) {
			condition.setup( parentGroup );
		}
		for( Config config : this.configs ) {
			config.setup( this.configs.size() > 1 ? config.addNewGroup( parentGroup ) : parentGroup );
		}
	}

	public void addCondition( ICondition condition ) {
		assert this.gameModifier == null : "context was already set up";
		this.conditions.add( condition );
	}

	public void addConfig( Config config ) {
		assert this.gameModifier == null : "context was already set up";
		this.configs.add( config );
	}

	public boolean check( Context.Data data ) {
		for( ICondition condition : this.conditions ) {
			if( !condition.check( this.gameModifier, data ) ) {
				return false;
			}
		}

		return true;
	}

	private ConfigGroup getParentConfigGroup() {
		ConfigGroup parentGroup = this.gameModifier.getConfigGroup();
		if( this.gameModifier.getContextsLength() == 1 ) {
			return parentGroup;
		} else {
			return parentGroup.addNewGroup( this.configName, this.configComment );
		}
	}

	public static abstract class Data {
		@Nullable
		final LivingEntity entity;

		public Data( @Nullable LivingEntity entity ) {
			this.entity = entity;
		}
	}
}
