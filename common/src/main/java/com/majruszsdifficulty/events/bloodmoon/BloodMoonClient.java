package com.majruszsdifficulty.events.bloodmoon;

import com.mlib.annotation.Dist;
import com.mlib.annotation.OnlyIn;
import com.mlib.contexts.OnClientTicked;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

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

		if( BloodMoonHelper.isActive() && level.getDayTime() % 24000L > 12000L ) {
			COLOR_RATIO = ( float )( 2.0 * ( level.getDayTime() % 12000L ) / 12000.0 - 1.0 );
			COLOR_RATIO = 1.0f - Math.abs( COLOR_RATIO * COLOR_RATIO * COLOR_RATIO );
		} else {
			COLOR_RATIO = 0.0f;
		}
	}
}
