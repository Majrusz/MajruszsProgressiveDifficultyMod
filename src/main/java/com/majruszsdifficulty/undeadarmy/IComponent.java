package com.majruszsdifficulty.undeadarmy;

interface IComponent {
	default void tick() {}

	default void onStart() {}

	default void onStateChanged() {}

	default void onWaveFinished() {}

	default void onGameReload() {}
}
