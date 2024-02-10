package com.majruszsdifficulty.bloodmoon;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.platform.Side;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.data.WorldData;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class BloodMoonHelper {
	private static BloodMoon BLOOD_MOON = new BloodMoon();

	static {
		Serializables.getStatic( WorldData.class )
			.define( "blood_moon", Reader.custom( BloodMoon::new ), ()->BLOOD_MOON, v->BLOOD_MOON = v );

		Serializables.getStatic( WorldData.Client.class )
			.define( "blood_moon", Reader.bool(), ()->BLOOD_MOON.isActive(), v->BLOOD_MOON.isActive = v );
	}

	public static boolean start() {
		if( BLOOD_MOON.start() ) {
			MajruszsDifficulty.WORLD_DATA.setDirty();
			return true;
		}

		return false;
	}

	public static boolean stop() {
		if( BLOOD_MOON.finish() ) {
			MajruszsDifficulty.WORLD_DATA.setDirty();
			return true;
		}

		return false;
	}

	@OnlyIn( Dist.CLIENT )
	public static float getColorRatio() {
		return BloodMoonClient.COLOR_RATIO;
	}

	public static long getRelativeDayTime() {
		return Optional.ofNullable( Side.getServer() ).map( server->server.overworld().getDayTime() % Level.TICKS_PER_DAY ).orElse( 0L );
	}

	public static boolean isActive() {
		return BLOOD_MOON.isActive();
	}

	public static boolean isValidDayTime() {
		return BloodMoonConfig.TIME.within( BloodMoonHelper.getRelativeDayTime() );
	}
}
