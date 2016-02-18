package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Dams.WarpDam;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Models.WarpModel;
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

public class DelWarpCommand implements CommandExecutor {

    private WarpDam warpDam;
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		boolean force = (args.hasAny("f")) ? (Boolean) args.getOne("f").get() : false;
		boolean cancel = (args.hasAny("c")) ? (Boolean) args.getOne("c").get() : false;
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		Player player = (Player) src;

        ArrayList<WarpModel> warps = this.getWarps(player);
		WarpModel warp = this.searchWarps(warps, name);

		if (cancel) {
			
			player.sendMessage(
				Text.of(FormatUtil.SUCCESS, "Warp ", FormatUtil.OBJECT, name, FormatUtil.SUCCESS, " was not deleted.")
			);
			
			return CommandResult.success();
			
		}
		
		if (force && warp != null) {
			
			deleteWarp(player, warp);
			return CommandResult.success();
				
		}
		
		if (warp != null) {
			
			player.sendMessage(
				Text.of(Text.NEW_LINE).toBuilder()
				.append(Text.of(FormatUtil.DIALOG, "Are you sure you want to delete warp ", FormatUtil.OBJECT, warp.getName(), FormatUtil.DIALOG, "?  "))
				.append(getDeleteWarpConfirmationAction(name, "Yes"))
				.append(Text.of("  "))
				.append(getDeleteWarpCancelAction(name, "No"))
				.append(Text.NEW_LINE)
				.build()
			);
			
			return CommandResult.success();
		
		} else {
			
			player.sendMessage(
				Text.of(FormatUtil.ERROR, "Warp ", FormatUtil.DELETED_OBJECT, name, FormatUtil.ERROR, " doesn't exist, or you don't have permissions to delete it.")
			);
			
			return CommandResult.success();
			
		}
		
	}

    private ArrayList<WarpModel> getWarps(Player player) {
        // this is broken in sponge
        // return (player.hasPermission("warp.admin")) ? warpDam.getAllWarps() : warpDam.getPlayerWarps(player);
        return warpDam.getPlayerWarps(player);
    }

    private WarpModel searchWarps(ArrayList<WarpModel> warps, String name) {

        for (WarpModel warp : warps) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }

        return null;
    }
	
	private void deleteWarp(Player player, WarpModel warp) {
		
		if (warpDam.deleteWarp(warp)) {
			
			player.sendMessage(
            	Text.of(FormatUtil.empty(), FormatUtil.SUCCESS, "Warp ", FormatUtil.DELETED_OBJECT, warp.getName(), FormatUtil.SUCCESS, " was successfully deleted!")
            );
			
		} else {
			
			player.sendMessage(
				Text.of(FormatUtil.ERROR, "Warp ", FormatUtil.DELETED_OBJECT, warp.getName(), FormatUtil.ERROR, " doesn't exist, or you don't have permissions to delete it.")
			);
			
		}

	}
	
	private Text getDeleteWarpConfirmationAction(String name, String linkText) {
		
		return Text.builder(linkText)
			.onClick(TextActions.runCommand("/delwarp -f " + name))
			.onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Delete warp ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.CONFIRM)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	private Text getDeleteWarpCancelAction(String name, String linkText) {
		
		return Text.builder(linkText)
			.onClick(TextActions.runCommand("/delwarp -c " + name))
			.onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Do not delete warp ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.CANCEL)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	public DelWarpCommand(Destinations plugin) {
        this.warpDam = new WarpDam(plugin);
	}
}
