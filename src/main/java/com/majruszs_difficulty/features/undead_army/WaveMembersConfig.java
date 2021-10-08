package com.majruszs_difficulty.features.undead_army;

import com.mlib.MajruszLibrary;
import com.mlib.config.ConfigGroup;
import com.mlib.config.StringListConfig;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Stores information about enemies in Undead Army waves. */
public class WaveMembersConfig extends ConfigGroup {
	private final List< StringListConfig > listConfigs;
	private int amountOfWaves = 0;

	public WaveMembersConfig( String name, String comment ) {
		super( name, comment );

		this.listConfigs = new ArrayList<>();
	}

	/** Creates new list config for single wave. */
	public StringListConfig createWaveConfig( String... defaultValues ) {
		this.amountOfWaves = this.amountOfWaves + 1;
		String waveID = "wave_" + amountOfWaves;
		String waveComment = "Mobs in wave " + amountOfWaves + ".";
		return new StringListConfig( waveID, waveComment, false, defaultValues );
	}

	public void addWaveConfigs( StringListConfig... waveConfigs ) {
		this.configTypeList = new ArrayList<>( Arrays.asList( waveConfigs ) );
		this.listConfigs.addAll( Arrays.asList( waveConfigs ) );
	}

	/** Returns list of wave members converted from config strings. */
	public List< WaveMember > getWaveMembers( int waveNumber ) {
		StringListConfig waveStringConfig = this.listConfigs.get( waveNumber - 1 );
		List< WaveMember > waveMembers = new ArrayList<>();

		for( String config : waveStringConfig.get() ) {
			Pattern pattern = Pattern.compile( "(.*)-(.*) (.*)" );
			Matcher matcher = pattern.matcher( config );

			if( matcher.find() ) {
				Optional< EntityType< ? > > optionalEntityType = EntityType.byString( matcher.group( 3 ) );
				if( optionalEntityType.isPresent() ) {
					int minAmount = Integer.parseInt( matcher.group( 1 ) );
					int maxAmount = Integer.parseInt( matcher.group( 2 ) );

					WaveMember waveMember = new WaveMember();
					waveMember.amount = minAmount + MajruszLibrary.RANDOM.nextInt( maxAmount - minAmount + 1 );
					waveMember.entityType = optionalEntityType.get();
					if( waveMember.amount > 0 )
						waveMembers.add( waveMember );
				}
			}
		}

		return waveMembers;
	}

	public static class WaveMember {
		int amount;
		EntityType< ? > entityType;
	}
}
