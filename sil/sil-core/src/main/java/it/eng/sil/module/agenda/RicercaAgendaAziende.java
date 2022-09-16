package it.eng.sil.module.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/*
 * Questa classe restituisce la query per la ricerca degli appuntamenti 
 * relativi ad un CpI (ristretto agli appuntamenti personali se si tratta
 * di un lavoratore oppure di una azienda).
 * 
 * @author: Stefania Orioli
 * 
 */

public class RicercaAgendaAziende implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicercaAgendaAziende.class.getName());

	public RicercaAgendaAziende() {
	}

	private static final String SELECT_SQL_BASE = "select " + "an_unita_azienda.PRGAZIENDA, "
			+ "an_unita_azienda.PRGUNITA, " + "InitCap(AN_AZIENDA.STRRAGIONESOCIALE) as strRagioneSociale, "
			+ "AN_AZIENDA.STRPARTITAIVA, " + "AN_AZIENDA.STRCODICEFISCALE, " + "AN_UNITA_AZIENDA.STRTEL, "
			+ "AN_UNITA_AZIENDA.STRINDIRIZZO, " + "InitCap(DE_COMUNE.STRDENOMINAZIONE) as comune_az "
			+ "FROM AN_UNITA_AZIENDA " + "INNER JOIN AN_AZIENDA on (an_unita_azienda.PRGAZIENDA=an_azienda.PRGAZIENDA) "
			+ "LEFT JOIN VW_INDIRIZZI_COM_PROV on VW_INDIRIZZI_COM_PROV.codcom = AN_UNITA_AZIENDA.codcom "
			+ "INNER JOIN de_comune on (an_unita_azienda.CODCOM=de_comune.CODCOM)";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String ragioneSociale = (String) req.getAttribute("RagioneSociale");
		String piva = (String) req.getAttribute("piva");
		String cf = (String) req.getAttribute("cf");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		String buf = "";
		String codProvincia = (String) req.getAttribute("codProvincia");
		if (ragioneSociale != null && !ragioneSociale.equals("")) {
			buf += " upper(an_azienda.STRRAGIONESOCIALE) like ('%" + ragioneSociale.toUpperCase() + "%')";
		}

		if (piva != null && !piva.equals("")) {
			if (!buf.equals("")) {
				buf += " AND ";
			}
			buf += " upper(an_azienda.STRPARTITAIVA) like '" + piva.toUpperCase() + "%'";
		}

		if (cf != null && !cf.equals("")) {
			if (!buf.equals("")) {
				buf += " AND ";
			}
			buf += " upper(an_azienda.STRCODICEFISCALE) like ('" + cf.toUpperCase() + "%')";
		}

		if ((codProvincia != null) && (!codProvincia.equals(""))) {
			if (!buf.equals("")) {
				buf += " AND ";
			}
			buf += " VW_INDIRIZZI_COM_PROV.codProvincia = '" + codProvincia + "'";
		}

		if (!buf.equals("")) {
			query_totale.append(" WHERE ");
			query_totale.append(buf);
		}

		// Debug
		_logger.debug("sil.module.agenda.RicercaAgendaAziende" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}