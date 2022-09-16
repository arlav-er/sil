package it.eng.myportal.utils;

/**
 * PaginationHandler
 * a simple class for pagination mini calculation
 * 
 * @author Hatem Alimam
 * Last Modified 14-06-2013
 * 
 */
public class PaginationHandler {

	private int start;
	private int chuckSize;
	private Long itemsCount;
	private int currentPage;
	
	public PaginationHandler(int start, int chuckSize, Long itemsCount) {
		this.start = start;
		this.chuckSize = chuckSize;
		this.itemsCount = itemsCount;
	}
	
	/*
	 * how many pages should the pagination render 
	 */
	public int pages() {
		return (int)Math.ceil((double)getItemsCount() / (double)getChuckSize());
	}

	/*
	 * based on the current page number, calculates DB startResultsFrom 
	 */
	public int calculateStart(int pageNumber) {
		return (pageNumber -1) * getChuckSize();
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getChuckSize() {
		return chuckSize;
	}

	public void setChuckSize(int chuckSize) {
		this.chuckSize = chuckSize;
	}

	public Long getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(Long itemsCount) {
		this.itemsCount = itemsCount;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	public String toString() {
		return "PaginationHandler [start=" + start + ", chuckSize=" + chuckSize
				+ ", itemsCount=" + itemsCount + ", currentPage=" + currentPage
				+ "]";
	}
}
