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
		if (warpList.contains(match.getMatch())) {
		
			Warp warp = plugin.getWarpStorageService().getWarp(match.getMatch());
			return getWarpAction(warp.getName());
		
		} else {
			
			return Texts.of(match.getMatch());
		}
		
	}
	
	private Text getWarpAction(String name) {
		return Texts.builder(name)
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
