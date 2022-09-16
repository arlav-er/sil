package com.engiweb.framework.paginator.smart.impl;

import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.paginator.smart.AbstractListProvider;
import com.engiweb.framework.paginator.smart.AbstractPageProvider;
import com.engiweb.framework.paginator.smart.IFaceListProvider;
import com.engiweb.framework.paginator.smart.IFacePageProvider;

/**
 * La classe <code>DefaultListProvider</code> &egrave; l'implementazione di default di
 * <code>AbstractListProvider</code>. Questa implementazione consente di riutilizzare i fogli di stile scritti per il
 * precedente paginatore perch&eacute; mantiene la medesima rappresentazione XML di ciascuna pagina.
 * 
 * @version 1.0, 15/03/2003
 * @see AbstractListProvider
 */
public class DefaultListProvider extends AbstractListProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DefaultListProvider.class.getName());
	private static final String ROWS = "ROWS";
	private static final String STATIC_DATA = "STATIC_DATA";
	private static final String DYNAMIC_DATA = "DYNAMIC_DATA";
	private static final String PAGED_LIST = "PAGED_LIST";
	private static final String PAGES_NUMBER = "PAGES_NUMBER";
	private static final String PAGE_NUMBER = "PAGE_NUMBER";

	/**
	 * Costruisce un <code>DefaultListProvider</code>. Questo costruttore &egrave; vuoto ed ogni azione di
	 * inizializzazione &egrave; demandata al metodo <code>init(SourceBean config)</code> in
	 * <code>AbstractListProvider</code>.
	 * <p>
	 * 
	 * @see AbstractListProvider#init(SourceBean)
	 */
	public DefaultListProvider() {
		super();
		_logger.debug("DefaultListProvider::DefaultListProvider: invocato");

	} // public DefaultListProvider()

	public void init(SourceBean config) {
		super.init(config);
		_logger.debug("DefaultListProvider::init: invocato");

	} // public void init(SourceBean config)

	/**
	 * Ritorna un <code>SourceBean</code> contenente tutti gli attributi utili a rappresentare la pagina richiesta. La
	 * rappresentazione XML del <code>SourceBean</code> &egrave; quella gi&agrave; usata dal precedente paginatore.
	 * Questo consente di mantenere tutti i fogli di stile gi&agrave; definiti.
	 * <p>
	 * 
	 * @param page
	 *            <code>int</code> numero della pagina richiesta. Se <em>page</em> &egrave; non positivo viene tornata
	 *            la prima pagina; se <em>page</em> eccede il numero di pagine viene tornata l'ultima pagina
	 *            disponibile.
	 * @see AbstractPageProvider#getPage(int)
	 */
	public Object getListPage(int page) {
		_logger.debug("DefaultListProvider::getListPage: pagina richiesta [" + page + "]");

		SourceBean listData = null;
		try {
			listData = new SourceBean(PAGED_LIST);
			SourceBean staticData = new SourceBean(STATIC_DATA);
			ArrayList staticDataArrayList = new ArrayList(getStaticData());
			for (int i = 0; i < staticDataArrayList.size(); i++)
				staticData.setAttribute((SourceBean) staticDataArrayList.get(i));
			listData.setAttribute(staticData);
			SourceBean dynamicData = new SourceBean(DYNAMIC_DATA);
			ArrayList dynamicDataArrayList = new ArrayList(getDynamicData());
			for (int i = 0; i < dynamicDataArrayList.size(); i++)
				dynamicData.setAttribute((SourceBean) dynamicDataArrayList.get(i));
			listData.setAttribute(dynamicData);
			SourceBean pageData = new SourceBean(ROWS);
			listData.setAttribute(pageData);
			int currentPage = 0;
			int pages = 0;
			IFacePageProvider pageProvider = getPageProvider();
			if (pageProvider != null) {
				if (page == IFaceListProvider.LAST)
					page = IFacePageProvider.LAST;
				ArrayList rows = new ArrayList(pageProvider.getPage(page));
				for (int i = 0; i < rows.size(); i++)
					if (rows.get(i) instanceof SourceBean)
						pageData.setAttribute((SourceBean) rows.get(i));
					else
						pageData.setAttribute("row", rows.get(i));
				currentPage = pageProvider.getCurrentPage();
				pages = pageProvider.pages();
			} // if (pageProvider != null)
			setCurrentPage(currentPage);
			listData.setAttribute(PAGE_NUMBER, String.valueOf(currentPage));
			_logger.debug("DefaultListProvider::getListPage: pagina ritornata [" + currentPage + "]");

			listData.setAttribute(PAGES_NUMBER, String.valueOf(pages));
			_logger.debug("DefaultListProvider::getListPage: numero pagine [" + pages + "]");

		} // try
		catch (SourceBeanException sbe) {
			it.eng.sil.util.TraceWrapper.warn(_logger, "DefaultListProvider::getListPage:", sbe);

		} // catch (SourceBeanException sbe)
		return listData;
	} // public Object getListPage(int page)
} // public abstract class DefaultListProvider extends AbstractListProvider
