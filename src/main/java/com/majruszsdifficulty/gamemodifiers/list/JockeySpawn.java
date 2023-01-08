package com.majruszsdifficulty.gamemodifiers.list;

import com.majruszsdifficulty.GameStage;
import com.majruszsdifficulty.gamemodifiers.CustomConditions;
import com.mlib.annotations.AutoInstance;
import com.mlib.gamemodifiers.GameModifier;import com.majruszsdifficulty.Registries;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnSpawned;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;

@AutoInstance
public class JockeySpawn extends GameModifier {
	public JockeySpawn() {
		super( Registries.Modifiers.DEFAULT );

		new OnSpawned.Context( this::spawnSkeletonOnSpider )
			.addCondition( new CustomConditions.GameStage<>( GameStage.Stage.EXPERT ) )
			.addCondition( new CustomConditions.CRDChance<>( 0.125, false ) )
			.addCondition( new Condition.Excludable<>() )
			.addCondition( OnSpawned.IS_NOT_LOADED_FROM_DISK )
			.addCondition( data->data.level != null )
			.addCondition( data->Spider.class.equals( data.target.getClass() ) )
			.insertTo( this );

		this.name( "JockeySpawn" ).comment( "Jockey is more likely to spawn." );
	}

	private void spawnSkeletonOnSpider( OnSpawned.Data data ) {
		assert data.level != null;
		Skeleton skeleton = EntityType.SKELETON.create( data.level );
		if( skeleton == null )
			return;

		skeleton.moveTo( data.target.getX(), data.target.getY(), data.target.getZ(), data.target.yBodyRot, 0.0f );
		skeleton.finalizeSpawn( data.level, data.level.getCurrentDifficultyAt( data.target.blockPosition() ), MobSpawnType.JOCKEY, null, null );
		skeleton.startRiding( data.target );
		data.level.addFreshEntity( skeleton );
	}
}
