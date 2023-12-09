package com.majruszsdifficulty.gamestage;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.text.RegexString;
import com.majruszlibrary.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class GameStage {
	public static final String NORMAL_ID = "normal";
	public static final String EXPERT_ID = "expert";
	public static final String MASTER_ID = "master";
	private String id = "";
	private List< ChatFormatting > format = new ArrayList<>();
	private Trigger trigger = new Trigger();
	private List< Message > messages = new ArrayList<>();
	int ordinal = 0;

	static {
		Serializables.get( GameStage.class )
			.define( "id", Reader.string(), s->s.id, ( s, v )->s.id = v )
			.define( "format", Reader.list( Reader.enumeration( ChatFormatting::values ) ), s->s.format, ( s, v )->s.format = v )
			.define( "triggers", Reader.custom( Trigger::new ), s->s.trigger, ( s, v )->s.trigger = v )
			.define( "messages", Reader.list( Reader.custom( Message::new ) ), s->s.messages, ( s, v )->s.messages = v );

		Serializables.get( Trigger.class )
			.define( "dimensions", Reader.list( Reader.string() ), s->RegexString.toString( s.dimensions ), ( s, v )->s.dimensions = RegexString.toRegex( v ) )
			.define( "entities", Reader.list( Reader.string() ), s->RegexString.toString( s.entities ), ( s, v )->s.entities = RegexString.toRegex( v ) );

		Serializables.get( Message.class )
			.define( "id", Reader.string(), s->s.id, ( s, v )->s.id = v )
			.define( "format", Reader.list( Reader.enumeration( ChatFormatting::values ) ), s->s.format, ( s, v )->s.format = v );
	}

	public static Builder named( String name ) {
		return new Builder( name );
	}

	@Override
	public boolean equals( Object object ) {
		return object instanceof GameStage gameStage
			&& this.id.equals( gameStage.id );
	}

	public boolean checkDimension( String dimensionId ) {
		return this.trigger.dimensions.stream().anyMatch( string->string.matches( dimensionId ) );
	}

	public boolean checkEntity( String entityId ) {
		return this.trigger.entities.stream().anyMatch( string->string.matches( entityId ) );
	}

	public boolean is( String name ) {
		return this.id.equals( name );
	}

	public String getId() {
		return this.id;
	}

	public int getOrdinal() {
		return this.ordinal;
	}

	public MutableComponent getComponent() {
		return TextHelper.translatable( "majruszsdifficulty.stages.%s".formatted( this.id.toLowerCase() ) )
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
			this.gameStage.id = name;
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
	}

	private static class Message {
		public String id;
		public List< ChatFormatting > format = new ArrayList<>();
	}
}
