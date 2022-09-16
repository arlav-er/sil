package com.engiweb.framework.paginator.basic;

import com.engiweb.framework.base.SourceBean;

public interface PaginatorIFace {
	void addRow(Object row);

	int getPageSize();

	void setPageSize(int size);

	int rows();

	int pages();

	SourceBean getPage(int page);

	SourceBean getAll();
} // public interface PaginatorIFace
