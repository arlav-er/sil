package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la ricerca dinamica di una azienda dati: - o la partita iva - o il codice fiscale - o la ragione sociale - o
 * l'indirizzo - o il codice del comune
 * 
 * @author Cristian Mudadu
 * 
 */
public class DynStatementRicercaAziende implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select " + "az.prgazienda as prgazienda, "
			+ "auz.prgunita as prgunita, " + "az.strcodicefiscale as codicefiscale, " + "az.strpartitaiva as piva, "
			+ "az.strragionesociale as ragionesociale, " + "auz.strindirizzo as indirizzo, "
			+ "auz.codcom as codicecomune, " + "c.strdenominazione as dencomune, " + "az.codtipoazienda as codtipoaz "
			+ "from " + "an_azienda az " + "INNER JOIN AN_UNITA_AZIENDA auz on az.prgAzienda=auz.prgAzienda "
			+ "LEFT JOIN DE_COMUNE c on auz.codcom=c.codcom ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String codTipoAzienda = (String) req.getAttribute("CodTipoAzienda");
		String cf = (String) req.getAttribute("CodiceFiscale");
		String pi = (String) req.getAttribute("PIva");
		String ragsoc = (String) req.getAttribute("RagioneSociale");
		String indirizzo = (String) req.getAttribute("Indirizzo");
		String codComune = (String) req.getAttribute("CodComune");
		// String desComune =(String) req.getAttribute("DesComune");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((codTipoAzienda != null) && (!codTipoAzienda.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}

			buf.append(" az.codTipoAzienda = '" + codTipoAzienda + "' ");
		}

		if ((ragsoc != null) && (!ragsoc.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}

			ragsoc = StringUtils.replace(ragsoc, "'", "''");
			buf.append(" lower(az.strRagioneSociale)  like ('%" + ragsoc.toLowerCase() + "%')");
		}

		if ((pi != null) && (!pi.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}

			buf.append(" az.strPartitaIva like '" + pi + "%'");
		}

		if ((cf != null) && (!cf.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}

			buf.append(" upper(az.strcodicefiscale) like ('" + cf.toUpperCase() + "%')");
		}

		if ((indirizzo != null) && (!indirizzo.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}

			buf.append(" lower(auz.strindirizzo) like ('%" + indirizzo.toLowerCase() + "%')");
		}

		if ((codComune != null) && (!codComune.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}

			buf.append(" auz.codCom = '" + codComune + "'");
		}

		buf.append(" order by az.strRagioneSociale, auz.prgunita");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}