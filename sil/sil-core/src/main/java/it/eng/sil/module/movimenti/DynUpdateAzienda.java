/*
 * Creato il 29-lug-04
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

/**
 * Aggiornamento della testata aziendale sulla base delle informazioni passate
 * 
 * @author Paolo Roccetti
 * 
 */
public class DynUpdateAzienda implements IDynamicStatementProvider {

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		// Recupreo informazioni
		SourceBean req = requestContainer.getServiceRequest();
		String prgAzienda = (String) req.getAttribute("prgAzienda");
		String pIvaMov = (String) req.getAttribute("pIvaMov");
		String ragSocMov = (String) req.getAttribute("ragSocMov");
		// String codCcnlMov = (String) req.getAttribute("codCcnlMov");
		String numIscrAlboIntMov = (String) req.getAttribute("numIscrAlboIntMov");
		String codTipoAzMov = (String) req.getAttribute("codTipoAzMov");
		String numkloAzienda = (String) req.getAttribute("NUMKLOAZIENDA");

		// Recupero codice utente
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		// Creazione query
		return "UPDATE AN_AZIENDA SET cdnUtMod = " + user.getCodut() + ", dtmMod = SYSDATE "
				+ (pIvaMov != null ? ", STRPARTITAIVA = '" + pIvaMov + "'" : "")
				+ (ragSocMov != null ? ", STRRAGIONESOCIALE = '" + StringUtils.replace(ragSocMov, "'", "''") + "'" : "")
				+ (codTipoAzMov != null ? ", CODTIPOAZIENDA = '" + codTipoAzMov + "'" : "")
				+ (numIscrAlboIntMov != null ? ", STRNUMALBOINTERINALI = '" + numIscrAlboIntMov + "'" : "")
				+ ", NUMKLOAZIENDA = " + numkloAzienda + " WHERE PRGAZIENDA = " + prgAzienda;
	}
}