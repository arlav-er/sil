/*
 * Creato il 19-apr-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * @author riccardi
 * 
 */
public class DynLoadIscrLAvL68 implements IDynamicStatementProvider {
	public DynLoadIscrLAvL68() {
	}

	private static final String SELECT_SQL_BASE = "SELECT" + " I.PRGCMISCR," + " I.CDNLAVORATORE," + " I.NUMISCRIZIONE,"
			+ " I.PRGDICHDISPONIBILITA," + " I.CODCMTIPOISCR," + " TIS.STRDESCRIZIONE AS DESCRIZIONEISCR,"
			+ " to_char(I.DATDATAINIZIO,'DD/MM/YYYY') DATINIZIO," + " to_char(I.DATDATAFINE,'DD/MM/YYYY') DATFINE,"
			+ " I.CODTIPOINVALIDITA," + " TIN.STRDESCRIZIONE AS DESCRIZIONEINV," + " I.NUMPERCINVALIDITA,"
			+ " I.CODACCERTSANITARIO," + " to_char(I.DATACCERTSANITARIO,'DD/MM/YYYY') DATACCERTSANITARIO,"
			+ " I.STRNOTEACCERTSANITARIO," + " I.STRNOTE," + " I.PRGSPIMOD as PRGSPIMOD," + " I.CDNUTINS,"
			+ " I.CDNUTMOD," + " to_char(I.DTMINS,'DD/MM/YYYY') DTMINS," + " to_char(I.DTMMOD,'DD/MM/YYYY') DTMMOD,"
			+ " I.NUMKLOCMISCR," + " to_char(I.DATANZIANITA68, 'DD/MM/YYYY') DATANZIANITA68,"
			+ " to_char(I.DATULTIMAISCR, 'DD/MM/YYYY') DATULTIMAISCR," + " I.CODMOTIVOFINEATTO,"
			+ " MOT.STRDESCRIZIONE AS DESCRMOTIVOFINEATTO," + " I.PRGVERBALEACC,"
			+ " PR.STRDENOMINAZIONE as PROVINCIA_ISCR," + " TIS.codMonoTipoRagg,"
			+ " decode (TIS.codMonoTipoRagg,'D','Disabili','A','Altre categorie protette') TIPO,"
			+ " DOC.CODSTATOATTO, DOC.CDNLAVORATORE CDNLAVINCHIARO, " + " ATTO.STRDESCRIZIONE STATOATTO"
			+ " FROM AM_CM_ISCR I" + " INNER JOIN DE_CM_TIPO_ISCR TIS ON (I.CODCMTIPOISCR = TIS.CODCMTIPOISCR)"
			+ " LEFT JOIN DE_CM_TIPO_INVALIDITA TIN ON (I.CODTIPOINVALIDITA = TIN.CODTIPOINVALIDITA)"
			+ " LEFT JOIN DE_MOTIVO_FINE_ATTO MOT ON (I.CODMOTIVOFINEATTO = MOT.CODMOTIVOFINEATTO)"
			+ " INNER JOIN AM_DOCUMENTO_COLL COLL ON (I.PRGCMISCR = COLL.STRCHIAVETABELLA)"
			+ " INNER JOIN AM_DOCUMENTO DOC ON (COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO AND DOC.CODTIPODOCUMENTO = 'L68')"
			+ " INNER JOIN DE_STATO_ATTO ATTO ON (DOC.CODSTATOATTO = ATTO.CODSTATOATTO)"
			+ " INNER JOIN DE_PROVINCIA PR ON (I.CODPROVINCIA = PR.CODPROVINCIA)";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cdnLavoratore = StringUtils.getAttributeStrNotNull(req, "cdnLavoratore");
		String selTutte = StringUtils.getAttributeStrNotNull(req, "selTutte");

		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		StringBuilder query_totale = new StringBuilder(SELECT_SQL_BASE);
		StringBuilder buf = new StringBuilder();

		buf.append(" WHERE I.CDNLAVORATORE = ENCRYPT('" + cdnLavoratore.toUpperCase() + "', '" + encryptKey + "')");
		buf.append(" AND DOC.CDNLAVORATORE = TO_NUMBER('" + cdnLavoratore + "') ");

		if (!"S".equalsIgnoreCase(selTutte)) {
			buf.append(" AND DOC.CODSTATOATTO <> 'AN'");
		}

		buf.append(" ORDER BY I.DATDATAINIZIO DESC, I.DATDATAFINE DESC ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}