package com.majruszsdifficulty.undeadarmy.components;

public interface IComponent {
	default void tick() {}

	default void onStart() {}

	default void onStateChanged() {}

	default void onWaveFinished() {}

	default void onGameReload() {}
}
