package com.majruszsdifficulty.mixin;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin( Creeper.class )
public interface IMixinCreeper {
	@Accessor( "explosionRadius" )
	void setExplosionRadius( int radius );
}
