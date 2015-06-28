package com.github.mmonkey.Destinations.Parse;

import java.util.regex.Pattern;

import org.spongepowered.api.text.Text;

public abstract class Filter {

	public abstract Pattern[] getPatterns();
	
	public abstract Text filter(Match match);
	
}
