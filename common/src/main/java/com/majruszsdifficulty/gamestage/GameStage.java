package com.majruszsdifficulty.gamestage;

import com.mlib.data.Serializables;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class GameStage {
	public static final GameStage NORMAL = GameStage.named( "normal" ).format( ChatFormatting.WHITE );
	public static final GameStage EXPERT = GameStage.named( "expert" ).format( ChatFormatting.RED, ChatFormatting.BOLD );
	public static final GameStage MASTER = GameStage.named( "master" ).format( ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD );
	private String name;
	private List< ChatFormatting > format;

	static {
		Serializables.get( GameStage.class )
			.defineString( "name", s->s.name, ( s, v )->s.name = v )
			.defineEnumList( "format", s->s.format, ( s, v )->s.format = v, ChatFormatting::values );
	}

	public static GameStage named( String name ) {
		GameStage gameStage = new GameStage();
		gameStage.name = name;

		return gameStage;
	}

	@Override
	public boolean equals( Object object ) {
		return object instanceof GameStage gameStage
			&& this.name.equals( gameStage.name );
	}

	public GameStage format( ChatFormatting... format ) {
		this.format = List.of( format );

		return this;
	}

	public boolean is( String name ) {
		return this.name.equals( name );
	}

	public String getName() {
		return this.name;
	}

	public MutableComponent getComponent() {
		return TextHelper.translatable( "majruszsdifficulty.stages.%s".formatted( this.name.toLowerCase() ) )
			.withStyle( this.format.toArray( ChatFormatting[]::new ) );
	}
}
