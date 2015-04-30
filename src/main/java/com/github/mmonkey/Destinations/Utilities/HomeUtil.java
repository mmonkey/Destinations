package com.github.mmonkey.Destinations.Utilities;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class HomeUtil {
	
	/**
	 * Generate link to home of the given name
	 * 
	 * @param name String
	 * @return Text
	 */
	public Text getHomeLink(String name) {
		
		return Texts.builder(name)
			.onClick(TextActions.runCommand("/home " + name))
			.onHover(TextActions.showText(Texts.of(TextColors.WHITE, "Teleport to ", TextColors.GOLD, name)))
			.color(TextColors.AQUA)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	public HomeUtil() {
	}

}
