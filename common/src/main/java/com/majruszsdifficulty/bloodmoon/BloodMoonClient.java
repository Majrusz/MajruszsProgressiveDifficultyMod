package com.majruszsdifficulty.bloodmoon;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.events.OnClientTicked;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

@OnlyIn( Dist.CLIENT )
public class BloodMoonClient {
	static float COLOR_RATIO = 0.0f;

	static {
		OnClientTicked.listen( BloodMoonClient::tick );
	}

	private static void tick( OnClientTicked data ) {
		ClientLevel level = Minecraft.getInstance().level;
		if( level == null ) {
			return;
		}

		if( BloodMoonHelper.isActive() && BloodMoonConfig.TIME.within( level.getDayTime() % Level.TICKS_PER_DAY ) ) {
			COLOR_RATIO = ( float )( 2.0 * ( level.getDayTime() - BloodMoonConfig.TIME.from ) / ( BloodMoonConfig.TIME.to - BloodMoonConfig.TIME.from ) - 1.0 );
			COLOR_RATIO = 1.0f - Math.abs( COLOR_RATIO * COLOR_RATIO * COLOR_RATIO );
		} else {
			COLOR_RATIO = 0.0f;
		}
	}
}
