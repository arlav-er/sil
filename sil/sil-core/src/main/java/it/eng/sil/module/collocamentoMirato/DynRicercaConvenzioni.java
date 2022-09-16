/*
 * Creato il 6 dicembre 2006
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * @author riccardi
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DynRicercaConvenzioni implements IDynamicStatementProvider {
	public DynRicercaConvenzioni() {
	}

	private static final String SELECT_SQL_BASE = " SELECT " + "	CM_CONVENZIONE.PRGCONV, "
			+ "	CM_CONVENZIONE.NUMANNOCONV AS anno, " + "	CM_CONVENZIONE.NUMCONVENZIONE AS numero, "
			+ " ( CM_CONVENZIONE.NUMCONVENZIONE ||'/' || CM_CONVENZIONE.NUMANNOCONV) AS CONV, "
			+ " CM_CONVENZIONE.PRGAZIENDA, " + " AN_AZIENDA.STRRAGIONESOCIALE AS RAGIONESOCIALE, "
			+ " CM_CONVENZIONE.CODMONOINTERA, " + " CM_CONVENZIONE.NUMCOINVOLTI, "
			+ " to_char(CM_CONVENZIONE.DATCONVENZIONE,'dd/mm/yyyy') AS DATCONVENZIONE,  "
			+ " CM_CONVENZIONE.CODSTATORICHIESTA, " + " DE_STATO_ATTO.STRDESCRIZIONE AS DESCSTATO, "
			+ " to_char(CM_CONVENZIONE.DATSCADENZA,'dd/mm/yyyy')AS DATSCADENZA, "
			+ " PR.STRDENOMINAZIONE as PROVINCIA_ISCR, " + " CM_CONVENZIONE.NUMDURATA, "
			+ " CM_CONVENZIONE.FLGPROROGA, " + " CM_CONVENZIONE.FLGMODIFICA, " + " CM_CONVENZIONE.NUMRINUNCIADIS, "
			+ " CM_CONVENZIONE.NUMRINUNCIAART18, " + " CM_CONVENZIONE.STRNOTE, " + " CM_CONVENZIONE.CDNUTINS, "
			+ " to_char(CM_CONVENZIONE.DTMINS,'dd/mm/yyyy') AS DTMINS, " + " CM_CONVENZIONE.CDNUTMOD, "
			+ " to_char(CM_CONVENZIONE.DTMMOD,'dd/mm/yyyy') AS DTMMOD " + "FROM CM_CONVENZIONE "
			+ "INNER JOIN AN_AZIENDA ON (CM_CONVENZIONE.PRGAZIENDA = AN_AZIENDA.PRGAZIENDA) "
			+ " INNER JOIN DE_PROVINCIA PR ON (CM_CONVENZIONE.CODPROVINCIA = PR.CODPROVINCIA) "
			+ "INNER JOIN DE_STATO_ATTO ON (CM_CONVENZIONE.CODSTATORICHIESTA = DE_STATO_ATTO.CODSTATOATTO) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		// recupero lo stato della convenzione da una lista multiselezione
		String statoConv = "";
		Vector list = req.getAttributeAsVector("statoConv");
		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				if (statoConv.length() > 0)
					statoConv += ",";
				if (!list.elementAt(i).equals("")) {
					statoConv += "'" + list.elementAt(i) + "'";
				}
			}
		}
		String prgAzienda = StringUtils.getAttributeStrNotNull(req, "prgAzienda");
		String prgAziendaApp = StringUtils.getAttributeStrNotNull(req, "prgAziendaApp");
		if (prgAzienda.equals("") && !prgAziendaApp.equals("")) {
			prgAzienda = prgAziendaApp;
		}
		String datStipDa = StringUtils.getAttributeStrNotNull(req, "datStipula_Da");
		String datStipA = StringUtils.getAttributeStrNotNull(req, "datStipula_A");
		String datScadDa = StringUtils.getAttributeStrNotNull(req, "datScad_Da");
		String datScadA = StringUtils.getAttributeStrNotNull(req, "datScad_A");
		String numConv = StringUtils.getAttributeStrNotNull(req, "numero");
		String annoConv = StringUtils.getAttributeStrNotNull(req, "anno");
		String PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(req, "PROVINCIA_ISCR");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (req.containsAttribute("conAssPrev")) {
			if (buf.length() == 0) {
				buf.append(" WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (CM_CONVENZIONE.PRGCONV IN (SELECT PRGCONV FROM CM_CONV_DETTAGLIO "
					+ "WHERE PRGCONV = CM_CONVENZIONE.PRGCONV AND CODSTATO = 'PRE'))");
		}

		if ((prgAzienda != null) && (!prgAzienda.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (CM_CONVENZIONE.PRGAZIENDA) = '" + prgAzienda.toUpperCase() + "'");
		}

		if ((statoConv != null) && (!statoConv.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (CM_CONVENZIONE.CODSTATORICHIESTA in (" + statoConv.toUpperCase() + "))");
		}

		if ((datStipDa != null) && (!datStipDa.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (CM_CONVENZIONE.DATCONVENZIONE) >= TO_DATE('" + datStipDa.toUpperCase() + "','DD/MM/YYYY') ");

		}

		if ((datStipA != null) && (!datStipA.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (CM_CONVENZIONE.DATCONVENZIONE) <= TO_DATE('" + datStipA.toUpperCase() + "','DD/MM/YYYY') ");

		}

		if ((datScadDa != null) && (!datScadDa.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (CM_CONVENZIONE.DATSCADENZA) >= TO_DATE('" + datScadDa.toUpperCase() + "','DD/MM/YYYY') ");

		}

		if ((datScadA != null) && (!datScadA.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (CM_CONVENZIONE.DATSCADENZA) <= TO_DATE('" + datScadA.toUpperCase() + "','DD/MM/YYYY') ");

		}

		if ((numConv != null) && (!numConv.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (CM_CONVENZIONE.NUMCONVENZIONE) = '" + numConv.toUpperCase() + "'");
		}

		if ((annoConv != null) && (!annoConv.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (CM_CONVENZIONE.NUMANNOCONV) = '" + annoConv.toUpperCase() + "'");
		}
		if ((PROVINCIA_ISCR != null) && (!PROVINCIA_ISCR.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (CM_CONVENZIONE.CODPROVINCIA) = '" + PROVINCIA_ISCR.toUpperCase() + "'");
		}

		buf.append(" ORDER BY CM_CONVENZIONE.DATCONVENZIONE DESC ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}