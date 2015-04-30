package com.github.mmonkey.Destinations.Utilities;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class PaginationUtil {
	
	private static final String PREVIEW_FIRST = "<<";
	private static final String PREVIEW_BACK = "<";
	private static final String PREVIEW_NEXT = ">";
	private static final String PREVIEW_LAST = ">>";
	
	private TextBuilder paginationBefore = Texts.builder();
	private TextBuilder paginationAfter = Texts.builder();
	private int currentPage;
	private int totalPages;
	private String commandPrefix;
	
	public void setCommandPrefix(String commandPrefix) {
		this.commandPrefix = commandPrefix;
	}
	
	public PaginationUtil(int currentPage, int totalPages, String commandPrefix) {
		this.currentPage = currentPage;
		this.totalPages = totalPages;
		this.commandPrefix = commandPrefix;
	}
	
	/**
	 * Get pagination links going to previous pages
	 * 
	 * @return Text
	 */
	public Text getPrevPagination() {
	
		if (currentPage > 2) {
			paginationBefore.append(Texts.of("  "), getPaginationLink(PaginationUtil.PREVIEW_FIRST, 1));
		}
		
		if (currentPage > 1) {
			paginationBefore.append(Texts.of("  "), getPaginationLink(PaginationUtil.PREVIEW_BACK, (currentPage - 1)));
		}
		
		return paginationBefore.build();
	
	}
	
	/**
	 * Get pagination links going to next pages
	 * 
	 * @return Text
	 */
	public Text getNextPagination() {
		
		if (currentPage < totalPages) {
			paginationAfter.append(Texts.of("  "), getPaginationLink(PaginationUtil.PREVIEW_NEXT, (currentPage + 1)));
		}
		
		if (currentPage < (totalPages - 1)) {
			paginationAfter.append(Texts.of("  "), getPaginationLink(PaginationUtil.PREVIEW_LAST, totalPages));
		}
		
		return paginationAfter.build();
		
	}
	
	/**
	 * Generate a pagination link
	 * 
	 * @param preview
	 * @param page
	 * @return
	 */
	private Text getPaginationLink(String preview, int page) {
		
		return Texts.builder(preview)
			.onClick(TextActions.runCommand(commandPrefix + " " + page))
			.onHover(TextActions.showText(Texts.of(TextColors.WHITE, "Go to page ", TextColors.GOLD, page)))
			.color(TextColors.AQUA)
			.build();
	
	}
	
}
