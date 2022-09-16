package com.engiweb.framework.dispatching.service.detail;

import com.engiweb.framework.base.SourceBean;

/**
 * @author Bellio
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to Window>Preferences>Java>Code Generation.
 */
public interface IFaceDetailService {
	/**
	 * In questo metodo dev'essere implementata la logica di business inerente all'azione di salvataggio dei dati.
	 * 
	 * @param request
	 *            nome del <code>SourceBean</code>
	 * @param response
	 *            nome del <code>SourceBean</code>
	 */
	boolean insert(SourceBean request, SourceBean response);

	/**
	 * In questo metodo dev'essere implementata la logica di business inerente all'azione di recupero dei dati.
	 * 
	 * @param request
	 *            nome del <code>SourceBean</code>
	 * @param response
	 *            nome del <code>SourceBean</code>
	 */
	boolean select(SourceBean request, SourceBean response);

	/**
	 * In questo metodo dev'essere implementata la logica di business inerente all'azione di modifica dei dati.
	 * 
	 * @param request
	 *            nome del <code>SourceBean</code>
	 * @param response
	 *            nome del <code>SourceBean</code>
	 */
	boolean update(SourceBean request, SourceBean response);
} // public interface IFaceDetailService
