package com.majruszsdifficulty.undeadarmy.data;

import com.majruszsdifficulty.gamestage.GameStage;
import com.mlib.data.SerializableStructure;

import java.util.ArrayList;
import java.util.List;

public class WaveDef extends SerializableStructure {
	public List< MobDef > mobDefs = new ArrayList<>();
	public MobDef boss;
	public int experience = 0;
	public GameStage gameStage = GameStage.NORMAL;

	public WaveDef() {
		this.defineCustom( "mobs", ()->this.mobDefs, x->this.mobDefs = x, MobDef::new );
		this.defineCustom( "boss", ()->this.boss, x->this.boss = x, MobDef::new );
		this.defineInteger( "exp", ()->this.experience, x->this.experience = x );
		this.defineEnum( "game_stage", ()->this.gameStage, x->this.gameStage = x, GameStage::values );
	}
}
