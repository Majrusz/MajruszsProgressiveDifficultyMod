package com.majruszsdifficulty;

import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;

public class ObfuscationGetter< Type, ReturnType > {
	private final Class< ? super Type > classToAccess;
	private final String fieldName;
	private final String mcpName;
	private boolean isMCPName;

	public ObfuscationGetter( Class< ? super Type > classToAccess, String fieldName, String mcpName ) {
		this.classToAccess = classToAccess;
		this.fieldName = fieldName;
		this.mcpName = mcpName;
		this.isMCPName = false;
	}

	@Nullable
	public ReturnType get( Type instance ) {
		if( this.isMCPName ) {
			try {
				return find( instance, this.mcpName );
			} catch( Exception exception ) {
				return null;
			}
		} else {
			try {
				return find( instance, this.fieldName );
			} catch( Exception exception ) {
				this.isMCPName = true;
				return get( instance );
			}
		}
	}

	@Nullable
	private ReturnType find( Type instance, String name ) throws IllegalAccessException {
		return ( ReturnType )( ObfuscationReflectionHelper.findField( this.classToAccess, name ).get( instance ) );
	}
}
