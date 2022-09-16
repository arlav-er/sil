/*
 * Creato il 4-set-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * @author gritti
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

public class DynStatementListaMotivoRiapertura implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " SELECT MOT.PRGMOTIVOAPERTURARICH, "
			+ " 		 TO_CHAR(MOT.DTMINS,'DD/MM/YYYY') AS DTMINS, " + " 		 MOT.CODMOTIVOAPERTURARICH, "
			+ "     	 MOT.STRALTROMOTIVOAPERTURARICH, " + "		 MOT.PRGRICHIESTAAZ, " + "		 MOT.CDNUTINS, "
			+ "		 STRMOT.STRDESCRIZIONE, "
			+ "		 nvl(UT.STRCOGNOME, '') ||'&nbsp;'|| nvl(UT.STRNOME,'') AS UTENTE "
			+ " 	FROM AS_MOTIVO_APERTURARICH MOT,DE_MOTIVO_APERTURARICH STRMOT,TS_UTENTE UT ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);

		SourceBean req = requestContainer.getServiceRequest();

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		String prgRichAz = StringUtils.getAttributeStrNotNull(req, "PRGRICHIESTAAZ");

		buf.append(
				" WHERE MOT.CODMOTIVOAPERTURARICH = STRMOT.CODMOTIVOAPERTURARICH AND MOT.CDNUTINS = UT.CDNUT AND PRGRICHIESTAAZ = "
						+ prgRichAz + " ORDER BY DTMINS DESC");
		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}