package com.github.mmonkey.Destinations.Parse;

import java.util.List;
import java.util.regex.Pattern;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Home;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;

public class HomeFilter extends Filter {

	private Destinations plugin;
	private Player player;
	
	@Override
	public Pattern[] getPatterns() {
		return new Pattern[] {Pattern.compile("\\[home:(.+?)\\]"), Pattern.compile("\\[h:(.+?)\\]")};
	}

	@Override
	public Text filter(Match match) {
		
		List<String> homeList = plugin.getHomeStorageService().getHomeList(player);
		if (homeList.contains(match.getMatch())) {
			
			List<Home> homes = plugin.getHomeStorageService().getHomes(player);
			for(Home home:homes) {
				
				if (home.getName().equalsIgnoreCase(match.getMatch())) {
					return getHomeAction(home.getName());
				}
			
			}
			
		}
		
		return Texts.of(match.getMatch());
	
	}
	
	private Text getHomeAction(String name) {
		
		return Texts.builder(name)
			.onClick(TextActions.runCommand("/home " + name))
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Teleport to ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.GENERIC_LINK)
			.style(TextStyles.UNDERLINE)
			.build();
	}
	
	public HomeFilter(Destinations plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}

}
