package com.majruszsdifficulty.undeadarmy;

interface IComponent {
	default void tick() {}

	default void onStart() {}

	default void onPhaseChanged() {}

	default void onGameReload() {}
}
