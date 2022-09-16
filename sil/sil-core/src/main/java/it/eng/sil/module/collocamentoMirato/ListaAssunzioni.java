/*
 * Creato il 11 dicembre 2006
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * @author gritti
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ListaAssunzioni implements IDynamicStatementProvider {
	public ListaAssunzioni() {
	}

	private static final String SELECT_SQL_BASE = " SELECT " + "	CM_CONV_DETTAGLIO.PRGCONVDETTAGLIO, "
			+ "	CM_CONV_DETTAGLIO.PRGCONV, " + "	DE_CONV_STATO_ASS.STRDESCRIZIONE AS STATOASS, "
			+ " CM_CONV_DETTAGLIO.NUMLAVORATORI, "
			+ " DECODE(CM_CONV_DETTAGLIO.CODMONOTIPO,'M','Nominativa','R','Numerica') AS TIPO, "
			+ " DECODE(CM_CONV_DETTAGLIO.CODMONOCATEGORIA,'D','Disabile','A','Altra categoria protetta') AS CATEGORIA, "
			+ " AN_LAVORATORE.STRCODICEFISCALE || '&nbsp;' || AN_LAVORATORE.STRCOGNOME || '&nbsp;' || AN_LAVORATORE.STRNOME AS LAV, "
			+ " TO_CHAR(CM_CONV_DETTAGLIO.DATSCADENZA,'DD/MM/YYYY') DATASCAD, "
			+ " DE_MANSIONE.STRDESCRIZIONE AS MANSIONE,  " + " DE_CONTRATTO.STRDESCRIZIONE AS CONTRATTO, "
			+ " CM_CONV_DETTAGLIO.NUMGGPROLUNGPROVA, " + " CM_CONV_DETTAGLIO.FLGINNALZAMENTOLIMITI, "
			+ " CM_CONV_DETTAGLIO.STRNOTE " + "FROM   CM_CONV_DETTAGLIO "
			+ "INNER JOIN CM_CONVENZIONE ON( CM_CONV_DETTAGLIO.PRGCONV = CM_CONVENZIONE.PRGCONV) "
			+ "LEFT OUTER JOIN AN_LAVORATORE ON( CM_CONV_DETTAGLIO.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE)"
			+ "INNER JOIN DE_CONV_STATO_ASS ON( CM_CONV_DETTAGLIO.CODSTATO = DE_CONV_STATO_ASS.CODSTATO) "
			+ "LEFT OUTER JOIN DE_CONTRATTO ON( CM_CONV_DETTAGLIO.CODCONTRATTO = DE_CONTRATTO.CODCONTRATTO) "
			+ "LEFT OUTER JOIN DE_MANSIONE  ON( CM_CONV_DETTAGLIO.CODMANSIONE = DE_MANSIONE.CODMANSIONE) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String prgconv = (String) req.getAttribute("PRGCONV");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		buf.append(" WHERE CM_CONVENZIONE.PRGCONV = " + prgconv);

		buf.append(" ORDER BY CM_CONV_DETTAGLIO.DATSCADENZA ASC ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}