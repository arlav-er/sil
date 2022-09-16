/*
 * Creato il 20-mar-07
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
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DynRicercaLavIscrCM implements IDynamicStatementProvider {
	public DynRicercaLavIscrCM() {
	}

	private static final String SELECT_SQL_DETT = " SELECT LAV.CDNLAVORATORE CDNLAVORATORE, "
			+ " LAV.STRCODICEFISCALE STRCODICEFISCALE, " + " LAV.STRCOGNOME STRCOGNOME, " + " LAV.STRNOME STRNOME, "
			+ " DE_T_ISC.CODMONOTIPORAGG, "
			+ " DECODE(DE_T_ISC.CODMONOTIPORAGG,'D','Disabile','A','Altra categoria protetta') AS CATEGORIA "
			+ " FROM AN_LAVORATORE LAV, AM_CM_ISCR ISC, DE_CM_TIPO_ISCR DE_T_ISC, AM_DOCUMENTO_COLL COLL, AM_DOCUMENTO DOC ";

	private static final String SELECT_SQL_RIC = " SELECT DISTINCT LAV.CDNLAVORATORE CDNLAVORATORE,  "
			+ " LAV.STRCODICEFISCALE STRCODICEFISCALE, " + " LAV.STRCOGNOME STRCOGNOME, " + " LAV.STRNOME STRNOME, "
			+ " LAV.FLGCFOK FLGCFOK " + " FROM AN_LAVORATORE LAV, AN_LAV_STORIA_INF ST ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		String nome = (String) req.getAttribute("strNome_ric");
		String cognome = (String) req.getAttribute("strCognome_ric");
		String cf = (String) req.getAttribute("strCodiceFiscale_ric");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");

		String fromWhere = StringUtils.getAttributeStrNotNull(req, "fromWhere");

		StringBuffer query_totale = null;
		if (fromWhere.equals("dettaglio")) {
			query_totale = new StringBuffer(SELECT_SQL_DETT);
		}
		if (fromWhere.equals("ricerca")) {
			query_totale = new StringBuffer(SELECT_SQL_RIC);
		}
		StringBuffer buf = new StringBuffer();

		if (fromWhere.equals("ricerca")) {
			buf.append(" WHERE LAV.CDNLAVORATORE = ST.CDNLAVORATORE ");
			buf.append(" AND ST.DATFINE IS NULL ");
		}

		if (fromWhere.equals("dettaglio")) {
			buf.append(" WHERE LAV.CDNLAVORATORE = DECRYPT(ISC.CDNLAVORATORE, '" + encryptKey + "') ");
			buf.append(" AND ISC.PRGCMISCR = COLL.STRCHIAVETABELLA ");
			buf.append(" AND COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO ");
			buf.append(" AND DOC.CDNLAVORATORE = LAV.CDNLAVORATORE ");
			buf.append(" AND DOC.CODTIPODOCUMENTO = 'L68' ");
			buf.append(" AND DOC.CODSTATOATTO = 'PR' ");
			buf.append(" AND ISC.CODCMTIPOISCR = DE_T_ISC.CODCMTIPOISCR ");
			buf.append(" AND ISC.DATDATAFINE IS NULL ");
		}

		if (tipoRic.equalsIgnoreCase("esatta")) {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				buf.append(" AND");
				buf.append(" upper(strnome) = '" + nome.toUpperCase() + "'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				buf.append(" AND");
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(strcognome) = '" + cognome.toUpperCase() + "'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				buf.append(" AND");
				buf.append(" upper(strCodiceFiscale) = '" + cf.toUpperCase() + "'");
			}
		} else {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				buf.append(" AND");
				buf.append(" upper(strnome) like '" + nome.toUpperCase() + "%'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				buf.append(" AND");
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(strcognome) like '" + cognome.toUpperCase() + "%'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				buf.append(" AND");
				buf.append(" upper(strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
			}
		} // else

		buf.append("ORDER BY STRCOGNOME, STRNOME, STRCODICEFISCALE");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}