package com.majruszsdifficulty.config;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.config.*;
import com.mlib.math.Range;

import java.util.List;
import java.util.function.Supplier;

public class GameStageConfig< Type > extends ConfigGroup implements Supplier< Type > {
	final ValueConfig< Type > normal, expert, master;

	public static GameStageConfig< Double > create( double normal, double expert, double master, Range< Double > range ) {
		return new GameStageConfig<>( new DoubleConfig( normal, range ), new DoubleConfig( expert, range ), new DoubleConfig( master, range ) );
	}

	public static GameStageConfig< Integer > create( int normal, int expert, int master, Range< Integer > range ) {
		return new GameStageConfig<>( new IntegerConfig( normal, range ), new IntegerConfig( expert, range ), new IntegerConfig( master, range ) );
	}

	public static GameStageConfig< List< ? extends String > > create( List< String > normal, List< String > expert, List< String > master ) {
		return new GameStageConfig<>( new StringListConfig( normal ), new StringListConfig( expert ), new StringListConfig( master ) );
	}

	public GameStageConfig( ValueConfig< Type > normal, ValueConfig< Type > expert, ValueConfig< Type > master ) {
		this.normal = normal;
		this.expert = expert;
		this.master = master;

		this.addConfig( this.normal.name( "normal" ).comment( "Normal Mode" ) );
		this.addConfig( this.expert.name( "expert" ).comment( "Expert Mode" ) );
		this.addConfig( this.master.name( "master" ).comment( "Master Mode" ) );
	}

	public Type getCurrentGameStageValue() {
		return GameStage.getCurrentGameStageDependentValue( this.normal.get(), this.expert.get(), this.master.get() );
	}

	@Override
	public Type get() {
		return this.getCurrentGameStageValue();
	}
}
