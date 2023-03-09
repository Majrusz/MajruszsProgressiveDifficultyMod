package com.majruszsdifficulty.undeadarmy.data;

import com.mlib.data.SerializableStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class WavesDef extends SerializableStructure {
	public final List< WaveDef > waveDefs = new ArrayList<>();

	public WavesDef() {
		this.define( null, ()->this.waveDefs, this.waveDefs::addAll, WaveDef::new );
	}

	public Stream< WaveDef > stream() {
		return this.waveDefs.stream();
	}
}
