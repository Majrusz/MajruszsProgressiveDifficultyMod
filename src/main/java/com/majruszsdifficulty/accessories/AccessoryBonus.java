package com.majruszsdifficulty.accessories;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class AccessoryBonus {
	ITooltipConsumer tooltipConsumer;

	public void addTooltip( ItemStack itemStack, List< Component > tooltip ) {
		this.tooltipConsumer.addTooltip( itemStack, tooltip );
	}

	protected void setTooltipConsumer( ITooltipConsumer consumer ) {
		this.tooltipConsumer = consumer;
	}

	public interface ITooltipConsumer {
		void addTooltip( ItemStack itemStack, List< Component > tooltip );
	}
}
