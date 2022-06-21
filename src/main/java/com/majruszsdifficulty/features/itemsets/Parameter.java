package com.majruszsdifficulty.features.itemsets;

public class Parameter {
	final IValueProvider value;
	final IFormat format;

	public Parameter( IValueProvider value, IFormat format ) {
		this.value = value;
		this.format = format;
	}

	public static Parameter asFloat( IValueProvider valueProvider ) {
		return new Parameter( valueProvider, param->{
			float value = param.asFloat();
			if( Math.round( value ) == value ) {
				return String.format( "%.0f", value );
			} else if( value > 1.0f ) {
				return String.format( "%.1f", value );
			} else {
				return String.format( "%.2f", value );
			}
		} );
	}

	public static Parameter asFloat( float value ) {
		return asFloat( ()->value );
	}

	public static Parameter asSeconds( float value ) {
		return asFloat( ()->value );
	}

	public static Parameter asPercent( IValueProvider valueProvider ) {
		return new Parameter( valueProvider, param->{
			float value = param.asFloat() * 100.0f;
			if( Math.round( value ) == value ) {
				return String.format( "%.0f%%", value );
			} else if( value > 1.0f ) {
				return String.format( "%.1f%%", value );
			} else {
				return String.format( "%.2f%%", value );
			}
		} );
	}

	public static Parameter asPercent( float value ) {
		return asPercent( ()->value );
	}

	public static Parameter asInteger( IValueProvider valueProvider ) {
		return new Parameter( valueProvider, param->String.format( "%d", param.asInt() ) );
	}

	public static Parameter asInteger( int value ) {
		return asInteger( ()->value );
	}

	public Number getValue() {
		return this.value.get();
	}

	public int asInt() {
		return getValue().intValue();
	}

	public float asFloat() {
		return getValue().floatValue();
	}

	public String getFormat() {
		return this.format.getFormat( this );
	}

	public interface IValueProvider {
		Number get();
	}

	public interface IFormat {
		String getFormat( Parameter parameter );
	}
}
