package com.majruszsdifficulty.features;

import com.majruszsdifficulty.data.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.majruszsdifficulty.gamestage.GameStageValue;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityDamaged;
import com.mlib.data.Serializables;
import net.minecraft.world.entity.monster.Creeper;

@AutoInstance
public class CreeperChainReaction {
	private GameStageValue< Boolean > isEnabled = GameStageValue.disabledOn( GameStage.NORMAL_ID );

	public CreeperChainReaction() {
		OnEntityDamaged.listen( this::igniteCreeper )
			.addCondition( data->this.isEnabled.get( GameStageHelper.determineGameStage( data ) ) )
			.addCondition( data->data.target instanceof Creeper )
			.addCondition( data->data.attacker instanceof Creeper );

		Serializables.get( Config.Features.class )
			.define( "creeper_chain_reaction", subconfig->{
				subconfig.defineBooleanMap( "is_enabled", s->this.isEnabled.get(), ( s, v )->this.isEnabled.set( v ) );
			} );
	}

	private void igniteCreeper( OnEntityDamaged data ) {
		( ( Creeper )data.target ).ignite();
	}
}
