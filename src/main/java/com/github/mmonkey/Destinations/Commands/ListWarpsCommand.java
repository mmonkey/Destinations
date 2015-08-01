package com.github.mmonkey.Destinations.Commands;

import java.util.Collection;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Models.WarpModel;
import com.github.mmonkey.Destinations.Pagination.PaginatedList;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;

public class ListWarpsCommand implements CommandExecutor {

	private Destinations plugin;
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		Player player = (Player) src;
		int currentPage = (args.hasAny("page")) ? (Integer) args.getOne("page").get() : 1;
		
		Collection<WarpModel> warps = plugin.getWarpStorageService().getPlayerWarps(player);
		
		if (warps.size() == 0) {
			
			player.sendMessage(
				Texts.of(FormatUtil.ERROR, "No warps have been set.").builder().build()
			);
			
			return CommandResult.success();
			
		}
		
		PaginatedList paginatedList = new PaginatedList("/listwarps");
		
		for (WarpModel warp: warps) {
			
			TextBuilder row = Texts.builder();
			row.append(getWarpAction(warp));
			row.append(getDeleteWarpAction(warp, player));
			
			paginatedList.add(row.build());
			
		}
		
		TextBuilder header = Texts.builder();
		TextBuilder message = Texts.builder();
		
		header.append(Texts.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		header.append(Texts.of(FormatUtil.HEADLINE, " Showing warps page " + currentPage + " of " + paginatedList.getTotalPages() + " "));
		header.append(Texts.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		
		paginatedList.setHeader(header.build());
		
		message.append(FormatUtil.empty());
		message.append(paginatedList.getPage(currentPage));
		
		player.sendMessage(message.build());
		
		return CommandResult.success();
	
	}
	
	private Text getWarpAction(WarpModel warp) {
		
		if (warp.isPublic()) {
			
			return Texts.builder(warp.getName())
				.onClick(TextActions.runCommand("/warp " + warp.getName()))
				.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Teleport to ", FormatUtil.OBJECT, warp.getName())))
				.color(FormatUtil.GENERIC_LINK)
				.style(TextStyles.UNDERLINE)
				.build();
			
		} else {
			
			return Texts.builder(warp.getName() + " (private)")
				.onClick(TextActions.runCommand("/warp " + warp.getName()))
				.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Teleport to ", FormatUtil.OBJECT, warp.getName())))
				.color(FormatUtil.GENERIC_LINK)
				.style(TextStyles.UNDERLINE)
				.build();
			
		}
		
	}
	
	private Text getDeleteWarpAction(WarpModel warp, Player player) {
		
		if (warp.getWhitelist().containsKey(player.getUniqueId()) || warp.getOwnerUniqueId().equals(player.getUniqueId())) {
			
			TextBuilder deleteAction = Texts.builder();
			deleteAction.append(Texts.of(" - "));
			deleteAction.append(Texts.builder("delete")
				.onClick(TextActions.runCommand("/delwarp " + warp.getName()))
				.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Delete warp ", FormatUtil.OBJECT, warp.getName())))
				.color(FormatUtil.DELETE)
				.style(TextStyles.UNDERLINE)
				.build());
			
			return deleteAction.build();
			
		}
		
		return Texts.of("");
		
	}
	
	public ListWarpsCommand(Destinations plugin) {
		this.plugin = plugin;
	}

}
