package com.github.mmonkey.Destinations.Utilities;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandMessageFormatting;

public class FormatUtil {
	
	public static final int COMMAND_WINDOW_LENGTH = 53;
	
	// Message Levels
	public static final TextColor SUCCESS = TextColors.GREEN;
	public static final TextColor WARN = TextColors.GOLD;
	public static final TextColor ERROR = TextColors.RED;
	public static final TextColor INFO = TextColors.WHITE;
	public static final TextColor DIALOG = TextColors.GRAY;
	
	// Object Levels
	public static final TextColor OBJECT = TextColors.GOLD;
	public static final TextColor DELETED_OBJECT = TextColors.RED;
	
	// Links
	public static final TextColor CANCEL = TextColors.RED;
	public static final TextColor DELETE = TextColors.RED;
	public static final TextColor CONFIRM = TextColors.GREEN;
	public static final TextColor GENERIC_LINK = TextColors.DARK_AQUA;
	
	//Other
	public static final TextColor HEADLINE = TextColors.DARK_GREEN;
	
	public Text empty() {
		
		TextBuilder text = Texts.builder();
		for (int i = 0; i < 20; i++) {
			text.append(CommandMessageFormatting.NEWLINE_TEXT);
		}
		
		return text.build();
		
	}
	
	public String getFill(int length, char fill) {
		return new String(new char[length]).replace('\0', fill);
	}
	
	public String getFill(int length) {
		return new String(new char[length]).replace('\0', ' ');
	}
	
	public FormatUtil(){
	}

}
