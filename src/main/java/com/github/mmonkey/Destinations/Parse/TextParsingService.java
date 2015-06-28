package com.github.mmonkey.Destinations.Parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;

public class TextParsingService {
	
	private Text text = null;
	private Filter[] filters;
	private String raw = null;
	
	public void setText(Text text) {
		this.text = text;
		this.raw = Texts.toPlain(text);
	}
	
	public Text parse() {
		
		if (this.text == null) {
			return Texts.of("");
		}
		
		List<Match> matches = new ArrayList<Match>();
		
		for(Filter filter:filters) {
			for(Pattern pattern:filter.getPatterns()) {
				Matcher matcher = pattern.matcher(this.raw);
				while (matcher.find()) {
					Match match = new Match(matcher.group(1), filter, matcher.start(), matcher.end());
					matches.add(match);
				}
			}
		}
		
		return processMatches(matches);
	}
	
	private Text processMatches(List<Match> matches) {
		
		String original = this.raw;
		
		if (!matches.isEmpty()) {
			
			TextBuilder builder = Texts.builder();
			StringBuilder piece = new StringBuilder();
			
			for (int i = 0; i < original.length(); i++) {
				
				int index = i;
				for (Match match:matches) {
					if (i == match.getStart()) {
						builder.append(Texts.of(piece.toString()));
						piece = new StringBuilder();
						builder.append(match.getFilter().filter(match));
						i = match.getEnd() - 1;
					}
				}
				
				if (i == index) {
					piece.append(original.charAt(index));
				}
				
				if (index == (original.length() - 1)) {
					builder.append(Texts.of(piece.toString()));
				}
				
			}
			
			return builder.build();
		
		}
		
		return Texts.of(original);
		
	}
	
	public TextParsingService(Filter... filters) {
		this.filters = filters;
	}
}
