package com.github.mmonkey.Destinations.Utilities;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
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
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Teleport to ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.GENERIC_LINK)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	public Text getDeleteHomeConfirmationLink(String name, String linkText) {
		
		return Texts.builder(linkText)
			.onClick(TextActions.runCommand("/delhome -f " + name))
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Delete home ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.CONFIRM)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	public Text getDeleteHomeCancelLink(String name, String linkText) {
		
		return Texts.builder(linkText)
			.onClick(TextActions.runCommand("/delhome -c " + name))
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Do not delete home ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.CANCEL)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	public Text getDeleteHomeLink(String name, String linkText) {
		
		return Texts.builder(linkText)
			.onClick(TextActions.runCommand("/delhome " + name))
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Delete home ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.DELETE)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	public HomeUtil() {
	}

}
