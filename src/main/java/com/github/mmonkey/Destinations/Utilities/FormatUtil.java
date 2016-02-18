package com.github.mmonkey.Destinations.Utilities;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

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
	
	public static Text empty() {
		
		Text.Builder text = Text.builder();
		for (int i = 0; i < 20; i++) {
			text.append(Text.NEW_LINE);
		}
		
		return text.build();
		
	}
	
	public static String getFill(int length, char fill) {
		return new String(new char[length]).replace('\0', fill);
	}
	
	public static String getFill(int length) {
		return new String(new char[length]).replace('\0', ' ');
	}
	
	public FormatUtil(){
	}

}
