package com.majruszsdifficulty.undeadarmy.data;

import com.mlib.data.SerializableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class WavesDef extends SerializableList {
	public List< WaveDef > waveDefs = new ArrayList<>();

	public WavesDef() {
		this.defineCustom( ()->this.waveDefs, x->this.waveDefs = x, WaveDef::new );
	}

	public Stream< WaveDef > stream() {
		return this.waveDefs.stream();
	}
}
