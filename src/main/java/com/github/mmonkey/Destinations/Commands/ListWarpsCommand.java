package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Dams.WarpDam;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Models.WarpModel;
import com.github.mmonkey.Destinations.Pagination.PaginatedList;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;

public class ListWarpsCommand implements CommandExecutor {

	private WarpDam warpDam;
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		Player player = (Player) src;
		int currentPage = (args.hasAny("page")) ? (Integer) args.getOne("page").get() : 1;
		
		ArrayList<WarpModel> warps = this.getWarps(player);
		
		if (warps.size() == 0) {
			
			player.sendMessage(
				Text.of(FormatUtil.ERROR, "No warps have been set.")
			);
			
			return CommandResult.success();
			
		}

		Text.Builder header = Text.builder();
		Text.Builder message = Text.builder();
		PaginatedList paginatedList = new PaginatedList("/listwarps");
		
		for (WarpModel warp: warps) {
			
			Text.Builder row = Text.builder();
			row.append(getWarpAction(warp));
			row.append(getDeleteWarpAction(warp, player));
			
			paginatedList.add(row.build());
			
		}

		currentPage = currentPage > paginatedList.getTotalPages() ? paginatedList.getTotalPages() : currentPage;

		header.append(Text.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		header.append(Text.of(FormatUtil.HEADLINE, " Showing warps page " + currentPage + " of " + paginatedList.getTotalPages() + " "));
		header.append(Text.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		
		paginatedList.setHeader(header.build());
		
		message.append(FormatUtil.empty());
		message.append(paginatedList.getPage(currentPage));
		
		player.sendMessage(message.build());
		
		return CommandResult.success();
	
	}

	private ArrayList<WarpModel> getWarps(Player player) {
		// this is broken in sponge
		// return (player.hasPermission("warp.admin")) ? warpDam.getAllWarps() : warpDam.getPlayerWarps(player);
		return warpDam.getPlayerWarps(player);
	}
	
	private Text getWarpAction(WarpModel warp) {
		
		if (warp.isPublic()) {
			
			return Text.builder(warp.getName())
				.onClick(TextActions.runCommand("/warp " + warp.getName()))
				.onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Teleport to ", FormatUtil.OBJECT, warp.getName())))
				.color(FormatUtil.GENERIC_LINK)
				.style(TextStyles.UNDERLINE)
				.build();
			
		} else {
			
			return Text.builder(warp.getName() + " (private)")
				.onClick(TextActions.runCommand("/warp " + warp.getName()))
				.onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Teleport to ", FormatUtil.OBJECT, warp.getName())))
				.color(FormatUtil.GENERIC_LINK)
				.style(TextStyles.UNDERLINE)
				.build();
			
		}
		
	}
	
	private Text getDeleteWarpAction(WarpModel warp, Player player) {
		
		if (warp.getWhitelist().containsKey(player.getUniqueId()) || warp.getOwnerUniqueId().equals(player.getUniqueId())) {
			
			Text.Builder deleteAction = Text.builder();
			deleteAction.append(Text.of(" - "));
			deleteAction.append(Text.builder("delete")
				.onClick(TextActions.runCommand("/delwarp " + warp.getName()))
				.onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Delete warp ", FormatUtil.OBJECT, warp.getName())))
				.color(FormatUtil.DELETE)
				.style(TextStyles.UNDERLINE)
				.build());
			
			return deleteAction.build();
			
		}
		
		return Text.of("");
		
	}
	
	public ListWarpsCommand(Destinations plugin) {
		this.warpDam = new WarpDam(plugin);
	}

}
