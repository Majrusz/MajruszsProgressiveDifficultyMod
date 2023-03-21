package com.majruszsdifficulty.commands;

import com.majruszsdifficulty.GameStage;
import com.mlib.annotations.AutoInstance;
import com.mlib.commands.CommandData;
import com.mlib.levels.LevelHelper;
import com.mlib.math.AnyPos;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
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
		BlockPos position = this.getOptionalEntityOrPlayer( data ).blockPosition();
		String total = String.format( "%.2f", GameStage.getRegionalDifficulty( level, AnyPos.from( position ).vec3() ) );

		return Component.translatable( "commands.clampedregionaldifficulty", asVec3i( position ), this.withStageStyle( total ), this.buildFormula( level, position ) );
	}

	private MutableComponent buildFormula( ServerLevel level, BlockPos position ) {
		if( GameStage.getCurrentStage() == GameStage.NORMAL )
			return Component.literal( "" );

		String crd = String.format( "%.2f", LevelHelper.getClampedRegionalDifficultyAt( level, position ) );
		String stageModifier = String.format( "%.2f", GameStage.getStageModifier() );
		return Component.translatable( "commands.clampedregionaldifficulty.formula", crd, this.withStageStyle( stageModifier ) );
	}

	private MutableComponent withStageStyle( String text ) {
		return Component.literal( text ).withStyle( GameStage.getCurrentStage().getChatFormatting() );
	}
}
