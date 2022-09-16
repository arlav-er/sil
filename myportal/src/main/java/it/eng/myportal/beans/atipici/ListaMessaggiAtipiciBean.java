package it.eng.myportal.beans.atipici;

import it.eng.myportal.beans.AbstractBaseBean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ManagedBean
@ViewScoped
public class ListaMessaggiAtipiciBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(ListaMessaggiAtipiciBean.class);

	/**
	 * Restituisce true se la pratica visualizzata e' stata spedita dal o al
	 * consulente loggato
	 * 
	 * @return
	 */

	/**
	 * Metodo per limitare la visibilita' di alcuni camp della pratica. In
	 * particolare i consulento possono vedere anche le pratiche chiuse da altri
	 * tipi di consulenti, ma non tutti i dati relativi a tali pratiche.
	 * 
	 * @param pfPrincipalIdFrom
	 *            idPfPrincipal del mittente della pratica
	 * @param pfPrincipalIdTo
	 *            idPfPrincipal del destinatario della pratica
	 * @return
	 */
	public boolean consulenteCanView(Integer pfPrincipalIdFrom, Integer pfPrincipalIdTo) {
		if (session.getConnectedConsulente() != null) {
			if (pfPrincipalIdFrom.equals(session.getPrincipalId()) || pfPrincipalIdTo.equals(session.getPrincipalId())) {
				return true;
			} else {
				return false;
			}
		} else {
			/*
			 * se non sono un consulente le regole di visualizzazione sono
			 * invariate
			 */
			return true;
		}
	}
}
