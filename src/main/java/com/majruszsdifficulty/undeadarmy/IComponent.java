package com.majruszsdifficulty.undeadarmy;

interface IComponent {
	default void tick() {}

	default void onPhaseChanged() {}
}
