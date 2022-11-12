package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.GameStage;
import com.mlib.annotations.AutoInstance;
import com.mlib.commands.CommandData;
import com.mlib.levels.LevelHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

@AutoInstance
public class ClampedRegionalDifficultyGetCommand extends DifficultyCommand {
	public ClampedRegionalDifficultyGetCommand() {
		this.newBuilder()
			.literal( "crd", "clampedregionaldifficulty" )
			.execute( this::handle )
			.entity()
			.hasPermission( 4 )
			.execute( this::handle );
	}

	private int handle( CommandData data ) throws CommandSyntaxException {
		data.source.sendSuccess( this.buildMessage( data ), true );

		return 0;
	}

	private MutableComponent buildMessage( CommandData data ) throws CommandSyntaxException {
		ServerLevel level = data.source.getLevel();
		Vec3 position = this.getOptionalEntityOrPlayer( data ).position();
		String total = String.format( "%.2f", GameStage.getRegionalDifficulty( level, position ) );

		return Component.translatable( "commands.clampedregionaldifficulty", this.asVec3i( position ), this.withStageStyle( total ), this.buildFormula( level, position ) );
	}

	private MutableComponent buildFormula( ServerLevel level, Vec3 position ) {
		if( GameStage.getCurrentStage() == GameStage.Stage.NORMAL )
			return Component.literal( "" );

		String crd = String.format( "%.2f", LevelHelper.getClampedRegionalDifficulty( level, position ) );
		String stageModifier = String.format( "%.2f", GameStage.getStageModifier() );
		return Component.translatable( "commands.clampedregionaldifficulty.formula", crd, this.withStageStyle( stageModifier ) );
	}

	private MutableComponent withStageStyle( String text ) {
		return GameStage.withStyle( Component.literal( text ) );
	}
}
