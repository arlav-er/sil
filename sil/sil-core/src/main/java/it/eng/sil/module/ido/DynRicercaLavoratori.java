/*
 * Creato il 13-feb-07
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

public class DynRicercaLavoratori implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE =

			"	select lav.STRNOME, " + "		   lav.STRCOGNOME, " + "		   lav.STRCODICEFISCALE, "
					+ "		   to_char(lav.DATNASC,'DD/MM/YYYY') as DATNASC, "
					+ "		   to_char(aci.DATDATAINIZIO,'DD/MM/YYYY') as DATINIZIO, "
					+ "		   tipo.CODMONOTIPORAGG, " + "          to_char(sysdate,'DD/MM/YYYY') as DATOGGI "
					+ "	from   AM_CM_ISCR aci, " + "		   AN_LAVORATORE lav, "
					+ " 		   AM_DOCUMENTO_COLL DOC_COLL," + "  		   AM_DOCUMENTO DOC, "
					+ "		   DE_CM_TIPO_ISCR tipo " + "	where  aci.DATDATAFINE is null "
					+ "	  	   and  tipo.CODCMTIPOISCR = aci.CODCMTIPOISCR "
					+ "          and aci.PRGCMISCR = DOC_COLL.STRCHIAVETABELLA "
					+ "          and DOC_COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO "
					+ "          and DOC.CODTIPODOCUMENTO = 'L68' " + " 		   and DOC.CODSTATOATTO = 'PR' ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		buf.append(" AND DECRYPT(aci.CDNLAVORATORE, '" + encryptKey + "') = lav.CDNLAVORATORE ");
		buf.append(" AND DOC.CDNLAVORATORE = lav.CDNLAVORATORE ");

		String codCMTipoIscr = StringUtils.getAttributeStrNotNull(req, "codCMTipoIscr");

		if ((codCMTipoIscr != null) && (!codCMTipoIscr.equals(""))) {
			buf.append(" and  tipo.CODMONOTIPORAGG = '" + codCMTipoIscr + "'");
		}
		buf.append(" order by STRCOGNOME ");

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}