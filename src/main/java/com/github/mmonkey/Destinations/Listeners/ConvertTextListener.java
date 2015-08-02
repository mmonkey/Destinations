package com.github.mmonkey.Destinations.Listeners;

import com.google.common.base.Optional;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Filters.WarpFilter;
import com.gmail.mmonkey.Commando.Events.CommandoConvertTextEvent;
import com.gmail.mmonkey.Commando.Events.TextType;


public class ConvertTextListener {
	
	private Destinations plugin;
	
	@Subscribe
	public void onConvert(CommandoConvertTextEvent event) {

        Optional<Player> player = event.getPlayer();
        if (player.isPresent()) {

            if (event.getTextType() == TextType.PLAYER_MESSAGE || event.getTextType() == TextType.SIGN) {
                event.addFilter(new WarpFilter(plugin, player.get()));
            }

        }
		
	}
	
	public ConvertTextListener(Destinations plugin) {
		this.plugin = plugin;
	}

}
