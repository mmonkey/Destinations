package com.github.mmonkey.Destinations.Parse;

import java.util.regex.Pattern;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

import com.github.mmonkey.Destinations.Utilities.FormatUtil;

public class CommandFilter extends Filter {

	@Override
	public Pattern[] getPatterns() {
		return new Pattern[] {Pattern.compile("\\[command:(.+?)\\]"), Pattern.compile("\\[c:(.+?)\\]")};
	}

	@Override
	public Text filter(Match match) {
		
		String command = match.getMatch();
		
		if (command.startsWith("/")) {
			command = command.substring(1);
		}
		
		String[] words = command.split(" ");
		String preview = (words.length > 0) ? words[0] : "";
		
		if (command.length() > 0 && preview.length() > 0) {
			return getCommandAction(command, preview);
		}
		
		return Texts.of(match.getMatch());
	}
	
	private Text getCommandAction(String command, String preview) {
		return Texts.builder(preview)
			.onClick(TextActions.runCommand("/" + command))
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Run command ", FormatUtil.OBJECT, preview)))
			.color(FormatUtil.GENERIC_LINK)
			.style(TextStyles.UNDERLINE)
			.build();
	}
	
	public CommandFilter() {
	}

}
