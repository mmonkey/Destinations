package com.github.mmonkey.Destinations.Filters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.mmonkey.Destinations.Dams.WarpDam;
import com.github.mmonkey.Destinations.Models.WarpModel;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import com.gmail.mmonkey.Commando.Match;
import com.gmail.mmonkey.Commando.Filters.Filter;

public class WarpFilter extends Filter {

    private WarpDam warpDam;
    private Player player;

	@Override
	public Pattern[] getPatterns() {
		return new Pattern[] {Pattern.compile("\\[warp:([^]]+)\\]"), Pattern.compile("\\[w:([^]]+)\\]")};
	}
	
	@Override
	public Text filter(Match match) {
		
		ArrayList<WarpModel> warps = this.getWarps(player);
        WarpModel warp = this.searchWarps(warps, match.getContent());

		if (warp != null) {

			return getWarpAction(match.getTitle(), warp.getName());
		
		} else {
			
			return Texts.of(match.getContent());
		}
		
	}

    private ArrayList<WarpModel> getWarps(Player player) {
        // this is broken in sponge
        // return (player.hasPermission("warp.admin")) ? warpDam.getAllWarps() : warpDam.getPlayerWarps(player);
        return warpDam.getPlayerWarps(player);
    }

    public WarpModel searchWarps(ArrayList<WarpModel> warps, String name) {

        for (WarpModel warp : warps) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }

        return null;
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
	
	public WarpFilter(Destinations plugin, Player player) {
		this.player = player;
        this.warpDam = new WarpDam(plugin);
	}

}
