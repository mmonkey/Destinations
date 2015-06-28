package com.github.mmonkey.Destinations.Events;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerChatEvent;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Parse.CommandFilter;
import com.github.mmonkey.Destinations.Parse.HomeFilter;
import com.github.mmonkey.Destinations.Parse.TextParsingService;
import com.github.mmonkey.Destinations.Parse.WarpFilter;

public class ChatListener {
	
	private Destinations plugin;

	@Subscribe
	public void onMessage(PlayerChatEvent event) {
		
		Player player = null;
		if (event.getSource() instanceof Player) {
			player = (Player) event.getSource();
		}
		
		TextParsingService service = new TextParsingService(
			new WarpFilter(plugin),
			new HomeFilter(plugin, player),
			new CommandFilter());
		
		service.setText(event.getMessage());
		event.setNewMessage(service.parse());

	}
	
	public ChatListener(Destinations plugin) {
		this.plugin = plugin;
	}
	

}
