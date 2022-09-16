package it.eng.sil.module.presel;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.LogUtils;

public class InsertTerritorioZona extends AbstractInsertDisponibilita {
	protected String getCodeFieldName() {

		return "CODCOM";
	}

	/**
	 * Gestisce l'inserimento di tutti i comuni della zone nella mansione o un tutte le mansioni del lavoratore.
	 */
	public void service(SourceBean request, SourceBean response) {

		// Fa in modo di non visualizzare alcun messaggio
		// durante l'esecuzione delle varie operazioni
		// per l'inserimento dei comuni delle zone.
		int prevSuccess = this.setMessageIdSuccess(0);
		int prevFail = this.setMessageIdFail(0);

		try {

			Vector vectCodComuni = new Vector();
			for (Iterator iter = loadComuniZona(request, response).iterator(); iter.hasNext();) {

				SourceBean beanComune = (SourceBean) iter.next();
				String codComune = (String) beanComune.getAttribute("CODCOM");
				vectCodComuni.add(codComune);
			}

			request.delAttribute("CODCOM");
			request.setAttribute("CODCOM", vectCodComuni);
		} catch (SourceBeanException ex) {

			LogUtils.logError("service", "", ex, this);
		}

		// Ripristina i messaggi
		this.setMessageIdSuccess(prevSuccess);
		this.setMessageIdFail(prevFail);

		super.service(request, response);
	}

	/**
	 * 
	 */
	private Collection loadComuniZona(SourceBean request, SourceBean response) {

		String prevSelect = this.setSectionQuerySelect("QUERY_SELECT_COMUNI_ZONA");

		SourceBean beanComuni = doSelect(request, response, false);

		this.setSectionQuerySelect(prevSelect);

		return (beanComuni != null) ? beanComuni.getAttributeAsVector("ROW") : new Vector();
	}
}