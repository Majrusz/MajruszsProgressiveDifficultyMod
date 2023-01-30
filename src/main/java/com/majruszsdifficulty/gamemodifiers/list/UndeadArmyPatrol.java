package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.Registries;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.majruszsdifficulty.gamemodifiers.configs.MobGroupConfig;
import com.majruszsdifficulty.goals.UndeadArmyForgiveTeammateGoal;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import com.mlib.math.Range;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Supplier;

@AutoInstance
public class UndeadArmyPatrol extends GameModifier {
	static final List< EntityType< ? extends PathfinderMob > > ENTITIES = List.of( EntityType.ZOMBIE, EntityType.HUSK, EntityType.SKELETON, EntityType.STRAY );
	final MobGroupConfig mobGroups = new MobGroupConfig( getMobTypes(), new Range<>( 2, 4 ), null, null );

	private static Supplier< EntityType< ? extends PathfinderMob > > getMobTypes() {
		return ()->Random.nextRandom( ENTITIES );
	}

	public UndeadArmyPatrol() {
		super( Registries.Modifiers.DEFAULT );

		this.mobGroups.onSpawn( mob->{
			// UndeadArmy.markAsUndeadArmyPatrol( mob );
			// UndeadArmy.equipWithUndeadArmyArmor( mob );
			if( mob instanceof AbstractSkeleton ) {
				mob.setItemSlot( EquipmentSlot.MAINHAND, new ItemStack( Items.BOW ) );
			}
			mob.targetSelector.getAvailableGoals().removeIf( wrappedGoal->wrappedGoal.getGoal() instanceof HurtByTargetGoal );
			mob.targetSelector.addGoal( 1, new UndeadArmyForgiveTeammateGoal( mob ) );
		} );

		new OnSpawned.ContextSafe( this::spawnGroup )
			.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.NORMAL ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.0625, true ) )
			.addCondition( new CustomConditions.IsNotPartOfGroup<>() )
			.addCondition( new CustomConditions.IsNotUndeadArmy<>() )
			.addCondition( new Condition.IsServer<>() )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( new OnSpawned.IsNotLoadedFromDisk<>() )
			.addCondition( data->ENTITIES.stream().anyMatch( type->type.equals( data.target.getType() ) ) )
			.addConfigs( this.mobGroups.name( "Undead" ) )
			.insertTo( this );

		this.name( "UndeadArmyPatrol" ).comment( "Undead may spawn in groups as the Undead Army Patrol." );
	}

	private void spawnGroup( OnSpawned.Data data ) {
		this.mobGroups.spawn( ( PathfinderMob )data.target );
	}
}
