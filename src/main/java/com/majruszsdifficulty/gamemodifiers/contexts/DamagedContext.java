package com.majruszsdifficulty.gamemodifiers.contexts;

import com.majruszsdifficulty.gamemodifiers.Context;
import com.majruszsdifficulty.gamemodifiers.ICondition;
import com.mlib.config.ConfigGroup;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class DamagedContext extends Context {
	static final List< DamagedContext > CONTEXTS = new ArrayList<>();

	public DamagedContext( ICondition... conditions ) {
		super( conditions );
		CONTEXTS.add( this );
	}

	@SubscribeEvent
	public static void onDamaged( LivingHurtEvent event ) {
		DamageSource source = event.getSource();
		LivingEntity attacker = source.getEntity() instanceof LivingEntity ? ( LivingEntity )source.getEntity() : null;
		LivingEntity target = event.getEntityLiving();
		Data data = new Data( event, attacker, target, source );

		for( DamagedContext context : CONTEXTS ) {
			if( context.check( data ) ) {
				context.gameModifier.execute( data );
			}
		}
	}

	@Override
	public ConfigGroup createConfigGroup() {
		return new ConfigGroup( "OnDamaged", "" );
	}

	public static class Data extends Context.Data {
		public final LivingHurtEvent event;
		@Nullable
		public LivingEntity attacker;
		public LivingEntity target;
		public DamageSource source;

		public Data( LivingHurtEvent event, @Nullable LivingEntity attacker, LivingEntity target, DamageSource source ) {
			super( attacker );
			this.event = event;
			this.attacker = attacker;
			this.target = target;
			this.source = source;
		}
	}
}
