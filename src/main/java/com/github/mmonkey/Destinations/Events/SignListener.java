package com.github.mmonkey.Destinations.Events;

import java.util.List;

import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityTypes;
import org.spongepowered.api.data.manipulator.tileentity.SignData;
import org.spongepowered.api.entity.EntityInteractionTypes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.block.tileentity.SignChangeEvent;
import org.spongepowered.api.event.entity.player.PlayerInteractBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.world.Location;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Parse.CommandFilter;
import com.github.mmonkey.Destinations.Parse.HomeFilter;
import com.github.mmonkey.Destinations.Parse.TextParsingService;
import com.github.mmonkey.Destinations.Parse.WarpFilter;
import com.google.common.base.Optional;

public class SignListener {
	
	private Destinations plugin;

	@Subscribe
	public void onSignChange(SignChangeEvent event) {
		
		Object cause = event.getCause().get().getCause();
		Player player = null;
		if (cause instanceof Player) {
			player = (Player) cause;
		}
		
		SignData data = event.getNewData();
		List<Text> lines = data.getLines();
		
		TextParsingService service = new TextParsingService(
			new WarpFilter(plugin),
			new HomeFilter(plugin, player),
			new CommandFilter());
		
		for(int i = 0; i < lines.size(); i++) {
			service.setText(lines.get(i));
			data.setLine(i, service.parse());
		}
		
		event.setNewData(data);
		
	}
	
	@Subscribe
	public void playerInteractSign(PlayerInteractBlockEvent event) {
		
		Location block = event.getBlock();
		Player player = event.getUser();
		
		if (!event.getInteractionType().equals(EntityInteractionTypes.USE)) {
			return;
		}
		
		if (block.getTileEntity().isPresent()) {
			TileEntity sign = block.getTileEntity().get();
			if (sign.getType() == TileEntityTypes.SIGN) {

				Optional<SignData> data = sign.getOrCreate(SignData.class);
				if (data.isPresent()) {
					List<Text> lines = data.get().getLines();
					for(Text line: lines) {
						List<Text> children = line.getChildren();
						for(int i = 0; i < children.size(); i++) {
							if (children.get(i).getClickAction().isPresent()) {
								ClickAction<?> clickAction = children.get(i).getClickAction().get();
								if (clickAction.getResult() instanceof String) {
									String command = (String) clickAction.getResult();
									if (command.startsWith("/")) {
										command = command.substring(1);
									}
									plugin.getGame().getCommandDispatcher().process(player, command);
								}
							}
						}
					}
				}
				
			}
		}
		
	}
	
	public SignListener(Destinations plugin) {
		this.plugin = plugin;
	}
}
