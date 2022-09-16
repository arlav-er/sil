package com.engiweb.framework.paginator.basic.impl;

import java.io.Serializable;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.paginator.basic.ListIFace;
import com.engiweb.framework.paginator.basic.PaginatorIFace;

public class GenericList implements ListIFace, Serializable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GenericList.class.getName());
	private static final String PAGED_LIST = "PAGED_LIST";
	private static final String COLUMN_NAMES = "COLUMN_NAMES";
	private static final String COLUMN_NAME = "COLUMN_NAME";
	private static final String COLUMN_NAME_ATTRIBUTE = "NAME";
	private static final String STATIC_DATA = "STATIC_DATA";
	private static final String DYNAMIC_DATA = "DYNAMIC_DATA";
	private static final String PAGE_NUMBER = "PAGE_NUMBER";
	private static final String PAGES_NUMBER = "PAGES_NUMBER";
	private PaginatorIFace _paginator = null;
	private SourceBean _staticData = null;
	private SourceBean _dynamicData = null;
	private int _currentPage = 1;

	public GenericList() {
		try {
			_paginator = null;
			_staticData = new SourceBean(STATIC_DATA);
			_dynamicData = new SourceBean(DYNAMIC_DATA);
			_currentPage = 1;
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "GenericList::GenericList:", ex);

		} // catch (SourceBeanException ex) try
	} // public GenericList()

	public PaginatorIFace getPaginator() {
		return _paginator;
	} // public PaginatorIFace getPaginator()

	public void setPaginator(PaginatorIFace paginator) {
		_paginator = paginator;
	} // public void setPaginator(PaginatorIFace paginator)

	public void addStaticData(SourceBean data) {
		try {
			_staticData.setAttribute(data);
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "GenericList::addStaticData:", ex);

		} // catch (SourceBeanException ex) try
	} // public void addStaticData(SourceBean data)

	public void clearDynamicData() {
		_dynamicData.clearBean();
	} // public void clearDynamicData()

	public void addDynamicData(SourceBean data) {
		try {
			_dynamicData.setAttribute(data);
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "GenericList::addDynamicData:", ex);

		} // catch (SourceBeanException ex) try
	} // public void addDynamicData(SourceBean data)

	public SourceBean getPagedList(int page) {
		_logger.debug("GenericList::getPagedList: parametro page [" + page + "]");

		SourceBean pagedList = null;
		try {
			pagedList = new SourceBean(PAGED_LIST);
			pagedList.setAttribute(_staticData);
			pagedList.setAttribute(_dynamicData);
			if ((_paginator == null) || (_paginator.pages() == 0)) {
				_logger.debug("GenericList::getPagedList: _paginator nullo");

				return pagedList;
			} // if ((_paginator == null) || (_paginator.pages() == 0))
			setCurrentPage(page);
			_logger.debug("GenericList::getPagedList: page calcolato [" + page + "]");

			SourceBean pageBean = _paginator.getPage(page);
			if (pageBean == null) {
				_logger.debug("GenericList::getPagedList: pageBean nullo");

				return pagedList;
			} // if (pageBean == null)
			pagedList.setAttribute(PAGE_NUMBER, String.valueOf(page));
			pagedList.setAttribute(PAGES_NUMBER, String.valueOf(_paginator.pages()));
			pagedList.setAttribute(pageBean);
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "GenericList::getPagedList:", ex);

		} // catch (SourceBeanException ex) try
		return pagedList;
	} // public SourceBean getPagedList(int page)

	public int getCurrentPage() {
		if (_paginator == null) {
			_logger.debug("GenericList::getCurrentPage: _paginator nullo");

			return 0;
		} // if (_paginator == null)
		_logger.debug("GenericList::getCurrentPage: _currentPage [" + _currentPage + "]");

		return _currentPage;
	} // public int getCurrentPage()

	public void setCurrentPage(int page) {
		if (_paginator == null) {
			_logger.debug("GenericList::setCurrentPage: _paginator nullo");

			return;
		} // if (_paginator == null)
		_logger.debug("GenericList::setCurrentPage: page [" + page + "]");

		if (page < 1)
			page = 1;
		if (page > _paginator.pages())
			page = _paginator.pages();
		_currentPage = page;
	} // public void setCurrentPage(int page)

	public int getPrevPage() {
		if (_paginator == null) {
			_logger.debug("GenericList::getPrevPage: _paginator nullo");

			return 0;
		} // if (_paginator == null)
		if (_currentPage <= 1)
			return 1;
		_currentPage--;
		_logger.debug("GenericList::getPrevPage: _currentPage [" + _currentPage + "]");

		return _currentPage;
	} // public int getPrevPage()

	public int getNextPage() {
		if (_paginator == null) {
			_logger.debug("GenericList::getNextPage: _paginator nullo");

			return 0;
		} // if (_paginator == null)
		if (_currentPage >= _paginator.pages())
			return _paginator.pages();
		_currentPage++;
		_logger.debug("GenericList::getPrevPage: _currentPage [" + _currentPage + "]");

		return _currentPage;
	} // public int getNextPage()

	public int pages() {
		if (_paginator == null) {
			_logger.debug("GenericList::pages: _paginator nullo");

			return 0;
		} // if ((_paginator == null) || (_paginator.pages() == 0))
		return _paginator.pages();
	} // public int pages()
} // public class GenericList implements ListIFace, Serializable
