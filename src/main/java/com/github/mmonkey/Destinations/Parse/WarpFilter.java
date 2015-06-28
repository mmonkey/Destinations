package com.github.mmonkey.Destinations.Parse;

import java.util.List;
import java.util.regex.Pattern;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Warp;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;

public class WarpFilter extends Filter {

	private Destinations plugin;
	
	@Override
	public Pattern[] getPatterns() {
		return new Pattern[] {Pattern.compile("\\[warp:(.+?)\\]"), Pattern.compile("\\[w:(.+?)\\]")};
	}
	
	@Override
	public Text filter(Match match) {
		
		List<String> warpList = plugin.getWarpStorageService().getWarpList();
		if (warpList.contains(match.getContent())) {
		
			Warp warp = plugin.getWarpStorageService().getWarp(match.getContent());
			return getWarpAction(match.getTitle(), warp.getName());
		
		} else {
			
			return Texts.of(match.getContent());
		}
		
	}
	
	private Text getWarpAction(String title, String name) {
		
		String showText = (title.length() > 0) ? title : name;
		
		return Texts.builder(showText)
			.onClick(TextActions.runCommand("/warp " + name))
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Teleport to ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.GENERIC_LINK)
			.style(TextStyles.UNDERLINE)
			.build();
	}
	
	public WarpFilter(Destinations plugin) {
		this.plugin = plugin;
	}

}
