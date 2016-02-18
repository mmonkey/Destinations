package com.github.mmonkey.Destinations.Pagination;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class PaginatedList {
	
	private List<Text> items = new ArrayList<Text>();
	private Text header;
	private Text footer;
	private String command;
	private String commandSuffix;
	private int itemsPerPage = PaginatedListUtil.BEST_FIT_WITH_HEADER;
	
	// Style options - pagination
	private char paginationType = PaginatedListUtil.PAGINATION_TYPE_DASH;
	private TextColor clickableLinkColor = TextColors.AQUA;
	private TextColor nonClickableLinkColor = TextColors.DARK_GRAY;
	private TextColor pageNumberColor = TextColors.GOLD;
	private TextColor paginationColor = TextColors.WHITE;
	
	// Style options - line numbers
	private boolean displayLineNumbers = true;
	private String lineNumberType = PaginatedListUtil.LINE_NUMBER_TYPE_PARENTHESIS;
	private TextColor lineNumberColor = TextColors.WHITE;
	
	public boolean add(Text text) {
		return this.items.add(text);
	}
	
	public boolean add(Text...texts) {
		List<Text> temp = new ArrayList<Text>();
		for (Text text: texts) {
			temp.add(text);
		}
		return this.addAll(temp);
	}
	
	public boolean addAll(List<Text> texts) {
		return this.items.addAll(texts);
	}
	
	public boolean remove(Text text) {
		return this.items.remove(text);
	}
	
	public boolean remove(Text...texts) {
		List<Text> temp = new ArrayList<Text>();
		for (Text text: texts) {
			temp.add(text);
		}
		return this.items.removeAll(temp);
	}
	
	public boolean removeAll(List<Text> texts) {
		return this.removeAll(texts);
	}
	
	public void clear() {
		this.items.clear();
	}
	
	public int size() {
		return this.items.size();
	}
	
	public boolean contains(Text text) {
		return this.items.contains(text);
	}
	
	public Text get(int index) {
		return this.items.get(index);
	}
	
	public List<Text> subList(int fromIndex, int toIndex) {
		int from = (this.size() > fromIndex) ? fromIndex : this.size();
		int to = (this.size() > toIndex) ? toIndex : this.size();
		return this.items.subList(from, to);
	}
	
	public int indexOf(Text text) {
		return this.items.indexOf(text);
	}
	
	public Text getHeader() {
		return this.header;
	}
	
	public void setHeader(Text header) {
		this.header = header;
	}
	
	public Text getFooter() {
		return this.footer;
	}
	
	public void setFooter(Text footer) {
		this.footer = footer;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public String getCommandSuffix() {
		return this.commandSuffix;
	}
	
	public void setCommandSuffix(String commandSuffix) {
		this.commandSuffix = commandSuffix;
	}
	
	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
	
	public int getItemsPerPage() {
		return this.itemsPerPage;
	}
	
	public int getTotalPages() {
		return (int) Math.ceil((double)this.size() / (double)this.getItemsPerPage());
	}

	public char getPaginationType() {
		return this.paginationType;
	}
	
	public void setPaginationType(char type) {
		this.paginationType = type;
	}

	public TextColor getClickableLinkColor() {
		return this.clickableLinkColor;
	}
	
	public void setClickableLinkColor(TextColor color) {
		this.clickableLinkColor = color;
	}
	
	public TextColor getNonClickableLinkColor() {
		return this.nonClickableLinkColor;
	}
	
	public void setNonClickableLinkColor(TextColor color) {
		this.nonClickableLinkColor = color;
	}
	
	public TextColor getPageNumberColor() {
		return this.pageNumberColor;
	}
	
	public void setPageNumberColor(TextColor color) {
		this.pageNumberColor = color;
	}
	
	public TextColor getPaginationColor() {
		return this.paginationColor;
	}
	
	public void setPaginationColor(TextColor color) {
		this.paginationColor = color;
	}
	
	public void displayLineNumbers(boolean display) {
		this.displayLineNumbers = display;
	}
	
	public boolean displayLineNumbers() {
		return this.displayLineNumbers;
	}
	
	public String getLineNumberType() {
		return this.lineNumberType;
	}
	
	public void setLineNumberType(String type) {
		this.lineNumberType = type;
	}
	
	public TextColor getLineNumberColor() {
		return this.lineNumberColor;
	}
	
	public void setLineNumberColor(TextColor color) {
		this.lineNumberColor = color;
	}
	
	/**
	 * Returns a Text object that contains a paginated list
	 * @param pageNumber int
	 * @return Text
	 */
	public Text getPage(int pageNumber) {
		
		int currentPage = (pageNumber <= this.getTotalPages()) ? pageNumber : this.getTotalPages();
		int startIndex = (currentPage - 1) * this.itemsPerPage;
		int itemIndex = startIndex + 1;
		
		Text.Builder list = Text.builder();
		List<Text> items = this.subList(startIndex, startIndex + this.itemsPerPage);
		
		if (this.header != null ) {
			list.append(this.header);
		}
		
		list.append(Text.NEW_LINE);
		
		for (Text item: items) {
			
			if (this.displayLineNumbers) {
				list.append(Text.of(this.lineNumberColor, (itemIndex < 10) ?
					"0" + itemIndex + this.lineNumberType :
					Integer.toString(itemIndex) + this.lineNumberType));
			}
			
			list.append(item);
			list.append(Text.NEW_LINE);
			
			itemIndex++;
		}
		
		list.append(Text.NEW_LINE);
		
		list.append(Text.of(this.paginationColor, fill(18, this.paginationType)));
		list.append(getPrevLinks(currentPage));
		list.append(Text.of(this.pageNumberColor, " " + currentPage + " "));
		list.append(getNextLinks(currentPage));
		list.append(Text.of(this.paginationColor, fill(18, this.paginationType)));
		
		if (this.footer != null) {
			list.append(Text.NEW_LINE);
			list.append(footer);
		}
		
		return list.build();
	}
	
	private Text getPrevLinks(int currentPage) {
		
		Text.Builder paginationPrev = Text.builder();
	
		paginationPrev.append(Text.of(" "));
		
		if (currentPage > 2) {
			paginationPrev.append(Text.of(" "), getLink(PaginatedListUtil.PAGINATION_FIRST, 1), Text.of(" "));
		} else {
			paginationPrev.append(Text.of(this.nonClickableLinkColor, " " + PaginatedListUtil.PAGINATION_FIRST + " "));
		}
		
		if (currentPage > 1) {
			paginationPrev.append(Text.of(" "), getLink(PaginatedListUtil.PAGINATION_BACK, (currentPage - 1)), Text.of(" "));
		} else {
			paginationPrev.append(Text.of(this.nonClickableLinkColor, " " + PaginatedListUtil.PAGINATION_BACK + " "));
		}
		
		return paginationPrev.build();
	
	}
	
	private Text getNextLinks(int currentPage) {
		
		Text.Builder paginationNext = Text.builder();
		
		if (currentPage < this.getTotalPages()) {
			paginationNext.append(Text.of(" "), getLink(PaginatedListUtil.PAGINATION_NEXT, (currentPage + 1)), Text.of(" "));
		} else {
			paginationNext.append(Text.of(this.nonClickableLinkColor, " " + PaginatedListUtil.PAGINATION_NEXT + " "));
		}
		
		if (currentPage < (this.getTotalPages() - 1)) {
			paginationNext.append(Text.of(" "), getLink(PaginatedListUtil.PAGINATION_LAST, this.getTotalPages()), Text.of(" "));
		} else {
			paginationNext.append(Text.of(this.nonClickableLinkColor, " " + PaginatedListUtil.PAGINATION_LAST + " "));
		}
		
		return paginationNext.build();
		
	}
	
	private Text getLink(String preview, int page) {
		
		String suffix = (this.commandSuffix != null) ? " " + this.commandSuffix : "";
		
		return Text.builder(preview)
			.onClick(TextActions.runCommand(this.command + " " + page + suffix))
			.onHover(TextActions.showText(Text.of(TextColors.WHITE, "Go to page ", TextColors.GOLD, page)))
			.color(this.clickableLinkColor)
			.build();
	
	}
	
	private String fill(int length, char character) {
		return new String(new char[length]).replace('\0', character);
	}
	
	/**
	 * @param command String
	 */
	public PaginatedList(String command) {
		this.items = new ArrayList<Text>();
		this.command = command;
	}
	
	/**
	 * @param command String
	 * @param itemsPerPage int
	 */
	public PaginatedList(String command, int itemsPerPage) {
		this.items = new ArrayList<Text>();
		this.command = command;
		this.itemsPerPage = itemsPerPage;
	}

}
