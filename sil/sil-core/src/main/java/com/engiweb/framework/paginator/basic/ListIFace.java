package com.engiweb.framework.paginator.basic;

import com.engiweb.framework.base.SourceBean;

public interface ListIFace {
	PaginatorIFace getPaginator();

	void setPaginator(PaginatorIFace paginator);

	void addStaticData(SourceBean data);

	void clearDynamicData();

	void addDynamicData(SourceBean data);

	SourceBean getPagedList(int i);

	int getCurrentPage();

	void setCurrentPage(int page);

	int getPrevPage();

	int getNextPage();

	int pages();
} // public interface ListIFace
