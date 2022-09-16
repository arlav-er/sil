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
 * Aggiornamento della unita aziendale sulla base delle informazioni passate
 * 
 * @author Paolo Roccetti
 * 
 */
public class DynUpdateUnitaAzienda implements IDynamicStatementProvider {

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		// Recupreo informazioni
		SourceBean req = requestContainer.getServiceRequest();
		String prgAzienda = (String) req.getAttribute("prgAzienda");
		String prgUnita = (String) req.getAttribute("prgUnita");
		String indirMov = (String) req.getAttribute("indirMov");
		String capMov = (String) req.getAttribute("capMov");
		String codAtecoMov = (String) req.getAttribute("codAtecoMov");
		String telMov = (String) req.getAttribute("telMov");
		String FaxMov = (String) req.getAttribute("FaxMov");
		String emailMov = (String) req.getAttribute("emailMov");
		String numkloUnita = (String) req.getAttribute("NUMKLOUNITAAZIENDA");
		String codCcnlMov = (String) req.getAttribute("codCcnlMov");
		String cfAzEsteraMov = "";
		String ragSocEsteraMov = "";
		cfAzEsteraMov = req.getAttribute("CODFISCAZESTERA") != null ? (String) req.getAttribute("CODFISCAZESTERA") : "";
		ragSocEsteraMov = req.getAttribute("RAGSOCAZESTERA") != null ? (String) req.getAttribute("RAGSOCAZESTERA") : "";

		// Recupero codice utente
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		// Creazione query
		return "UPDATE AN_UNITA_AZIENDA SET cdnUtMod = " + user.getCodut() + ", dtmMod = SYSDATE "
				+ (indirMov != null ? ", STRINDIRIZZO = '" + StringUtils.replace(indirMov, "'", "''") + "'" : "")
				+ (capMov != null ? ", STRCAP = '" + capMov + "'" : "")
				+ (codAtecoMov != null ? ", CODATECO = '" + codAtecoMov + "'" : "")
				+ (telMov != null ? ", STRTEL = '" + telMov + "'" : "")
				+ (FaxMov != null ? ", STRFAX = '" + FaxMov + "'" : "")
				+ (emailMov != null ? ", STREMAIL = '" + emailMov + "'" : "")
				+ (!cfAzEsteraMov.equals("") ? ", STRCFAZESTERA = '" + cfAzEsteraMov + "'" : "")
				+ (!ragSocEsteraMov.equals("") ? ", STRRAGSOCAZESTERA = '" + ragSocEsteraMov + "'" : "")
				+ (codCcnlMov != null ? ", CODCCNL = '" + codCcnlMov + "'" : "") + ", NUMKLOUNITAAZIENDA = "
				+ numkloUnita + " WHERE PRGAZIENDA = " + prgAzienda + " AND PRGUNITA = " + prgUnita;
	}
}