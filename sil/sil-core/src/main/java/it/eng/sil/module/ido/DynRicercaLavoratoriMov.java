/*
 * Creato il 15-feb-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * @author melandri
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynRicercaLavoratoriMov implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE =

			"	SELECT aci.cdnLavoratore, " + "		   lav.strnome,  " + "		   lav.strcognome,  "
					+ "		   lav.strcodicefiscale, " + "		   TO_CHAR (lav.datnasc, 'DD/MM/YYYY') AS datnasc, "
					+ "		   TO_CHAR (aci.datdatainizio, 'DD/MM/YYYY') AS datinizio, "
					+ "		   tipo.codmonotiporagg,  " + "		   TO_CHAR (SYSDATE, 'DD/MM/YYYY') AS datoggi, "
					+ "		   TO_CHAR (mov.DATINIZIOMOV, 'DD/MM/YYYY') AS DATINIZIOMOV "
					+ "	  FROM am_cm_iscr aci,  " + "		   an_lavoratore lav,  "
					+ "		   de_cm_tipo_iscr tipo, " + "		   am_movimento mov "
					+ "	 WHERE aci.datdatafine IS NULL " + "	   AND tipo.codcmtipoiscr = aci.codcmtipoiscr ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		buf.append(" AND DECRYPT(aci.CDNLAVORATORE, '" + encryptKey + "') = lav.CDNLAVORATORE ");
		buf.append(" AND DECRYPT(aci.CDNLAVORATORE, '" + encryptKey + "') = mov.CDNLAVORATORE ");

		String codMonoTempo = StringUtils.getAttributeStrNotNull(req, "codMonoTempo");
		String codCMTipoIscr = StringUtils.getAttributeStrNotNull(req, "codCMTipoIscr");

		String dataMovDa = StringUtils.getAttributeStrNotNull(req, "dataMovDa");
		String dataMovA = StringUtils.getAttributeStrNotNull(req, "dataMovA");

		if ((codMonoTempo != null) && (!codMonoTempo.equals(""))) {
			buf.append(" AND mov.CODMONOTEMPO = '" + codMonoTempo + "'");
		}
		if ((codCMTipoIscr != null) && (!codCMTipoIscr.equals(""))) {
			buf.append(" AND tipo.CODMONOTIPORAGG = '" + codCMTipoIscr + "'");
		}

		if ((dataMovDa != null) && (!dataMovDa.equals(""))) {
			buf.append(" AND mov.DATINIZIOMOV >= to_date('" + dataMovDa + "', 'DD/MM/YYYY') ");
		}
		if ((dataMovA != null) && (!dataMovA.equals(""))) {
			buf.append(" AND mov.DATINIZIOMOV <= to_date('" + dataMovA + "', 'DD/MM/YYYY') ");
		}

		buf.append(" order by strcognome,cdnLavoratore ");

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}