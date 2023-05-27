package com.majruszsdifficulty.undeadarmy.data;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.data.SerializableStructure;

import java.util.ArrayList;
import java.util.List;

public class WaveDef extends SerializableStructure {
	public final List< MobDef > mobDefs = new ArrayList<>();
	public MobDef boss;
	public int experience = 0;
	public GameStage gameStage = GameStage.NORMAL;

	public WaveDef() {
		this.define( "mobs", ()->this.mobDefs, this.mobDefs::addAll, MobDef::new );
		this.define( "boss", ()->this.boss, x->this.boss = x, MobDef::new );
		this.define( "exp", ()->this.experience, x->this.experience = x );
		this.define( "game_stage", ()->this.gameStage, x->this.gameStage = x, GameStage::values );
	}
}
