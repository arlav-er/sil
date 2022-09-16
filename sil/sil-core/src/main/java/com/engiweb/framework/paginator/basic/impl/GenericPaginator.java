package com.engiweb.framework.paginator.basic.impl;

import java.io.Serializable;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.paginator.basic.PaginatorIFace;

public class GenericPaginator implements PaginatorIFace, Serializable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GenericPaginator.class.getName());

	public GenericPaginator() {
		_rows = new Vector();
		_size = 10;
	} // public GenericPaginator()

	public void addRow(Object row) {
		if (row == null) {
			_logger.debug("GenericPaginator::addRow: row nullo");

			return;
		} // if (row == null)
		_rows.addElement(row);
	} // public void addRow(Object row)

	public int getPageSize() {
		return _size;
	} // public int getPageSize()

	public void setPageSize(int size) {
		if (size < 1) {
			_logger.debug("GenericPaginator::setPageSize: size [" + size + "] non valida");

			return;
		} // if (size < 1)
		_size = size;
	} // public void setPageSize(int size)

	public int rows() {
		return _rows.size();
	} // public int rows()

	public int pages() {
		if (_rows.size() == 0)
			return 0;
		return (_rows.size() - 1) / _size + 1;
	} // public int pages()

	public SourceBean getPage(int page) {
		SourceBean rows = null;
		try {
			rows = new SourceBean(ROWS);
			if ((page < 1) || (page > pages())) {
				_logger.debug("GenericPaginator::getPage: page [" + page + "] non valida");

				return rows;
			} // if ((page < 1) || (page > pages()))
			int startRow = (page - 1) * _size;
			int endRow = page * _size;
			if (endRow > _rows.size())
				endRow = _rows.size();
			for (int i = startRow; i < endRow; i++) {
				Object objectRow = _rows.elementAt(i);
				if (!(objectRow instanceof SourceBean)) {
					SourceBean row = new SourceBean("ROW");
					row.setAttribute("VALUE", objectRow);
					objectRow = row;
				} // if (!(objectRow instanceof SourceBean))
				rows.setAttribute((SourceBean) objectRow);
			} // for (int i = startRow; i < endRow; i++)
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "GenericPaginator::getPage:", ex);

		} // catch (SourceBeanException ex) try
		return rows;
	} // public SourceBean getPage(int page)

	public SourceBean getAll() {
		SourceBean rows = null;
		try {
			rows = new SourceBean(ROWS);
			for (int i = 0; i < _rows.size(); i++) {
				Object objectRow = _rows.elementAt(i);
				if (!(objectRow instanceof SourceBean)) {
					SourceBean row = new SourceBean("ROW");
					row.setAttribute("VALUE", objectRow);
					objectRow = row;
				} // if (!(objectRow instanceof SourceBean))
				rows.setAttribute((SourceBean) objectRow);
			} // for (int i = startRow; i < endRow; i++)
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "GenericPaginator::getAll:", ex);

		} // catch (SourceBeanException ex) try
		return rows;
	} // public SourceBean getAll()

	private Vector _rows = null;
	private int _size = 0;
	private static final String ROWS = "ROWS";
} // public class GenericPaginator implements PaginatorIFace, Serializable
