package com.majruszsdifficulty.undeadarmy;

import com.mlib.Random;
import com.mlib.config.ConfigGroup;
import com.mlib.config.IConfigurable;
import com.mlib.config.StringListConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WaveMembersConfig extends ConfigGroup {
	final List< StringListConfig > configs = new ArrayList<>();

	public WaveMembersConfig addWaveConfig( String... defaultValues ) {
		this.configs.add( new StringListConfig( defaultValues ) );
		this.addConfig( this.configs.get( this.configs.size() - 1 ).name( "wave_" + ( this.configs.size() + 1 ) ) );

		return this;
	}

	/** Returns list of wave members converted from config strings. */
	public List< WaveMember > getWaveMembers( int waveNumber ) {
		StringListConfig waveStringConfig = this.configs.get( waveNumber - 1 );
		List< WaveMember > waveMembers = new ArrayList<>();

		for( String config : waveStringConfig.get() ) {
			Pattern pattern = Pattern.compile( "(.*)-(.*) (.*)" );
			Matcher matcher = pattern.matcher( config );
			if( matcher.find() ) {
				Optional< EntityType< ? > > optionalEntityType = EntityType.byString( matcher.group( 3 ) );
				if( optionalEntityType.isPresent() ) {
					int minAmount = Integer.parseInt( matcher.group( 1 ) );
					int maxAmount = Integer.parseInt( matcher.group( 2 ) );

					WaveMember waveMember = new WaveMember( Random.nextInt( minAmount, maxAmount + 1 ), optionalEntityType.get() );
					if( waveMember.amount > 0 ) {
						waveMembers.add( waveMember );
					}
				}
			}
		}

		return waveMembers;
	}

	public record WaveMember( int amount, EntityType< ? > entityType ) {}
}
