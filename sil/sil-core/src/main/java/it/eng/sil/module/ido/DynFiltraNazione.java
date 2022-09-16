package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.tags.Util;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

public class DynFiltraNazione implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynFiltraNazione.class.getName());

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		String query = "";

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		SourceBean req = requestContainer.getServiceRequest();
		String prgRosa = (String) req.getAttribute("prgRosa");
		String strValidaCanc = (String) req.getAttribute("strValidaCanc");
		strValidaCanc = Util.replace(strValidaCanc, "'", "''");
		// Creo la subquery che filtra per nazionalità. Possono esistere più
		// nazionalità che si vogliono filtrare: occorre fare un ciclo.
		// Se sto costruendo la query significa che "codCittadinanza_1" è
		// valorizzato (conidtion nella page)
		String codCitt = StringUtils.getAttributeStrNotNull(req, "codCittadinanza_1");
		// 14/07/2005 - Aggiunto il join con la rosa per limitare il
		// sottoinsieme nella clausola IN (Stefy)
		String subQuery = "      (select an_lavoratore.cdnlavoratore" + "       from an_lavoratore, do_nominativo "
				+ "       where (an_lavoratore.CDNLAVORATORE=do_nominativo.CDNLAVORATORE) " + "and prgrosa=" + prgRosa
				+ " and " + "(an_lavoratore.CODCITTADINANZA = \'" + codCitt + "\'";
		String paramCitt = "";
		codCitt = StringUtils.getAttributeStrNotNull(req, "codCittadinanza_2");
		for (int i = 3; !codCitt.equals(""); i++) {
			paramCitt = "codCittadinanza_" + i;
			subQuery += " or an_lavoratore.CODCITTADINANZA = \'" + codCitt + "\'";
			codCitt = StringUtils.getAttributeStrNotNull(req, paramCitt);
		}
		subQuery += ") )";

		// Costruisco la query inserendo la subquery
		// 14/07/2005 NON SI DISCRIMINAVA IL CASO IN CUI CODTIPOCANC FOSSE GIA'
		// VALORIZZATO ED E' UN ERRORE
		query = "update do_nominativo " + "set codTipoCanc = 'D'," + "    strMotivoCanc = \'" + strValidaCanc + "\',"
				+ "	   dtmCanc = SYSDATE," + "	  NUMKLONOMINATIVO = NUMKLONOMINATIVO+1," + "	   cdnUtCanc = "
				+ user.getCodut() + " where do_nominativo.cdnlavoratore NOT in " + subQuery
				+ "   AND do_nominativo.PRGROSA = " + prgRosa + " and codTipoCanc is null ";

		_logger.debug(className + "::Filtra per nazione: " + query);

		return query;

	}// getStatement

}// class DynFiltraNazione
