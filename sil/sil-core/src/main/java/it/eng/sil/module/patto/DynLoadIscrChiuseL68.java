/*
 * Creato il 23-gen-08
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * @author riccardi
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DynLoadIscrChiuseL68 implements IDynamicStatementProvider {

	public DynLoadIscrChiuseL68() {
	}

	private static final String SELECT_SQL_BASE = "SELECT" + " I.PRGCMISCR," + " I.CDNLAVORATORE,"
			+ " I.CDNLAVORATORE AS CDNLAVORATORENCRYPT," + " I.NUMISCRIZIONE," + " I.PRGDICHDISPONIBILITA,"
			+ " I.CODCMTIPOISCR," + " TIS.STRDESCRIZIONE AS DESCRIZIONEISCR,"
			+ " to_char(I.DATDATAINIZIO,'DD/MM/YYYY') DATINIZIO," + " to_char(I.DATDATAFINE,'DD/MM/YYYY') DATFINE,"
			+ " I.CODTIPOINVALIDITA," + " TIN.STRDESCRIZIONE AS DESCRIZIONEINV," + " I.NUMPERCINVALIDITA,"
			+ " I.CODACCERTSANITARIO," + " to_char(I.DATACCERTSANITARIO,'DD/MM/YYYY') DATACCERTSANITARIO,"
			+ " I.STRNOTEACCERTSANITARIO," + " I.STRNOTE," + " I.PRGSPIMOD," + " I.CDNUTINS," + " I.CDNUTMOD,"
			+ " to_char(I.DTMINS,'DD/MM/YYYY') DTMINS," + " to_char(I.DTMMOD,'DD/MM/YYYY') DTMMOD," + " I.NUMKLOCMISCR,"
			+ " to_char(I.DATANZIANITA68, 'DD/MM/YYYY') DATANZIANITA68,"
			+ " to_char(I.DATULTIMAISCR, 'DD/MM/YYYY') DATULTIMAISCR," + " I.CODMOTIVOFINEATTO,"
			+ " MOT.STRDESCRIZIONE AS DESCRMOTIVOFINEATTO," + " I.PRGVERBALEACC," + " TIS.codMonoTipoRagg,"
			+ " decode (TIS.codMonoTipoRagg,'D','Disabili','A','Altre categorie protette') TIPO" + " FROM AM_CM_ISCR I"
			+ " INNER JOIN DE_CM_TIPO_ISCR TIS ON (I.CODCMTIPOISCR = TIS.CODCMTIPOISCR)"
			+ " LEFT JOIN DE_CM_TIPO_INVALIDITA TIN ON (I.CODTIPOINVALIDITA = TIN.CODTIPOINVALIDITA)"
			+ " LEFT JOIN DE_MOTIVO_FINE_ATTO MOT ON (I.CODMOTIVOFINEATTO = MOT.CODMOTIVOFINEATTO)"
			+ " INNER JOIN AM_DOCUMENTO_COLL COLL ON (I.PRGCMISCR = COLL.STRCHIAVETABELLA)"
			+ " INNER JOIN AM_DOCUMENTO DOC ON (COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO AND DOC.CODTIPODOCUMENTO = 'L68')";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cdnLavoratore = StringUtils.getAttributeStrNotNull(req, "cdnLavoratore");
		String dataDichDid = StringUtils.getAttributeStrNotNull(req, "dataDichDid");

		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		buf.append(" WHERE I.CDNLAVORATORE = ENCRYPT('" + cdnLavoratore.toUpperCase() + "', '" + encryptKey + "')");
		buf.append(" AND DOC.CDNLAVORATORE = " + cdnLavoratore);
		buf.append(" AND DOC.CODSTATOATTO = 'PR'");
		buf.append(" AND I.DATDATAFINE IS NOT NULL");
		buf.append(" AND I.DATDATAFINE <= to_date('" + dataDichDid + "', 'DD/MM/YYYY')");
		buf.append(" AND NOT EXISTS ");
		buf.append(" (SELECT 1 FROM AM_CM_ISCR I1");
		buf.append(" INNER JOIN DE_CM_TIPO_ISCR TIS1 ON (I1.CODCMTIPOISCR = TIS1.CODCMTIPOISCR)");
		buf.append(" INNER JOIN AM_DOCUMENTO_COLL COLL1 ON (I1.PRGCMISCR = COLL1.STRCHIAVETABELLA)");
		buf.append(" INNER JOIN AM_DOCUMENTO DOC1 ON (COLL1.PRGDOCUMENTO = DOC1.PRGDOCUMENTO)");
		buf.append(" WHERE I1.CDNLAVORATORE = ENCRYPT('" + cdnLavoratore.toUpperCase() + "', '" + encryptKey + "')");
		buf.append(" AND DOC1.CDNLAVORATORE = " + cdnLavoratore);
		buf.append(" AND DOC1.CODTIPODOCUMENTO = 'L68' AND DOC1.CODSTATOATTO = 'PR'");
		buf.append(" AND I1.DATDATAINIZIO >= I.DATDATAINIZIO");
		buf.append(" AND I1.PRGCMISCR > I.PRGCMISCR");
		buf.append(" AND TIS1.codMonoTipoRagg = TIS.codMonoTipoRagg)");

		buf.append(" ORDER BY I.DATDATAINIZIO DESC, I.DATDATAFINE DESC ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}
