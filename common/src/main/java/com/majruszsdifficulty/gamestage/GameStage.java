package com.majruszsdifficulty.gamestage;

import com.mlib.data.Serializables;
import com.mlib.text.RegexString;
import com.mlib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class GameStage {
	public static final String NORMAL_ID = "normal";
	public static final String EXPERT_ID = "expert";
	public static final String MASTER_ID = "master";
	private String name = "";
	private List< ChatFormatting > format = new ArrayList<>();
	private Trigger trigger = new Trigger();
	private List< Message > messages = new ArrayList<>();
	private int ordinal = 0;

	static {
		Serializables.get( GameStage.class )
			.defineString( "name", s->s.name, ( s, v )->s.name = v )
			.defineEnumList( "format", s->s.format, ( s, v )->s.format = v, ChatFormatting::values )
			.defineCustom( "triggers", s->s.trigger, ( s, v )->s.trigger = v, Trigger::new )
			.defineCustomList( "messages", s->s.messages, ( s, v )->s.messages = v, Message::new );
	}

	public static Builder named( String name ) {
		return new Builder( name );
	}

	@Override
	public boolean equals( Object object ) {
		return object instanceof GameStage gameStage
			&& this.name.equals( gameStage.name );
	}

	public void setOrdinal( int ordinal ) {
		this.ordinal = ordinal;
	}

	public boolean checkDimension( String dimensionId ) {
		return this.trigger.dimensions.stream().anyMatch( string->string.matches( dimensionId ) );
	}

	public boolean checkEntity( String entityId ) {
		return this.trigger.entities.stream().anyMatch( string->string.matches( entityId ) );
	}

	public boolean is( String name ) {
		return this.name.equals( name );
	}

	public String getName() {
		return this.name;
	}

	public int getOrdinal() {
		return this.ordinal;
	}

	public MutableComponent getComponent() {
		return TextHelper.translatable( "majruszsdifficulty.stages.%s".formatted( this.name.toLowerCase() ) )
			.withStyle( this.format.toArray( ChatFormatting[]::new ) );
	}

	public List< MutableComponent > getMessages() {
		return this.messages.stream()
			.map( message->TextHelper.translatable( message.id ).withStyle( message.format.toArray( ChatFormatting[]::new ) ) )
			.toList();
	}

	public static class Builder {
		private final GameStage gameStage;

		public Builder( String name ) {
			this.gameStage = new GameStage();
			this.gameStage.name = name;
		}

		public Builder format( ChatFormatting... format ) {
			this.gameStage.format = List.of( format );

			return this;
		}

		public Builder triggersIn( String dimensionId ) {
			this.gameStage.trigger.dimensions.add( new RegexString( dimensionId ) );

			return this;
		}

		public Builder triggersByKilling( String entityId ) {
			this.gameStage.trigger.entities.add( new RegexString( entityId ) );

			return this;
		}

		public Builder message( String id, ChatFormatting... format ) {
			Message message = new Message();
			message.id = id;
			message.format = List.of( format );
			this.gameStage.messages.add( message );

			return this;
		}

		public GameStage create() {
			return this.gameStage;
		}
	}

	private static class Trigger {
		public List< RegexString > dimensions = new ArrayList<>();
		public List< RegexString > entities = new ArrayList<>();

		static {
			Serializables.get( Trigger.class )
				.defineStringList( "dimensions", s->RegexString.toString( s.dimensions ), ( s, v )->s.dimensions = RegexString.toRegex( v ) )
				.defineStringList( "entities", s->RegexString.toString( s.entities ), ( s, v )->s.entities = RegexString.toRegex( v ) );
		}
	}

	private static class Message {
		public String id;
		public List< ChatFormatting > format = new ArrayList<>();

		static {
			Serializables.get( Message.class )
				.defineString( "id", s->s.id, ( s, v )->s.id = v )
				.defineEnumList( "format", s->s.format, ( s, v )->s.format = v, ChatFormatting::values );
		}
	}
}
