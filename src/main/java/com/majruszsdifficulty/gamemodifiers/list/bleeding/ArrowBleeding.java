package com.majruszsdifficulty.gamemodifiers.list.bleeding;

import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingCheck;
import com.majruszsdifficulty.gamemodifiers.contexts.OnBleedingTooltip;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ProjectileWeaponItem;

@AutoInstance
public class ArrowBleeding {
	final BooleanConfig availability;
	final DoubleConfig chance;

	public ArrowBleeding() {
		this.availability = Condition.DefaultConfigs.excludable( true );
		this.chance = Condition.DefaultConfigs.chance( 0.333 );

		ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.BLEEDING )
			.name( "Arrow" )
			.comment( "Arrows may inflict bleeding." );

		OnBleedingCheck.listen( OnBleedingCheck.Data::trigger )
			.addCondition( Condition.chanceCRD( this.chance, false ) )
			.addCondition( Condition.excludable( this.availability ) )
			.addCondition( Condition.isLivingBeing( data->data.target ) )
			.addCondition( Condition.predicate( data->data.source.getDirectEntity() instanceof Arrow ) )
			.insertTo( group );

		OnBleedingTooltip.listen( this::addTooltip )
			.addCondition( Condition.predicate( data->this.availability.isEnabled() ) )
			.addCondition( Condition.predicate( data->data.itemStack.getItem() instanceof ProjectileWeaponItem ) )
			.insertTo( group );
	}

	private void addTooltip( OnBleedingTooltip.Data data ) {
		data.addItem( this.chance.getOrDefault() );
	}
}
