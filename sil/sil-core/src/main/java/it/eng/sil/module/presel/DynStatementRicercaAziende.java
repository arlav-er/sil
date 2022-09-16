package it.eng.sil.module.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la ricerca dinamica di una azienda dati: - o la partita iva - o il codice fiscale - o la ragione sociale - o
 * nessuna delle precedenti (restituisce TUTTO)
 * 
 * @author Alessio Rolfini
 * 
 */
public class DynStatementRicercaAziende implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select auz.codAteco, "
			+ "att.strdescrizione desateco, att2.strDescrizione as tipoAteco, "
			+ "az.strCodiceFiscale, az.strPartitaIva, az.strRagioneSociale, "
			+ "auz.strIndirizzo, auz.codcom, c.strdenominazione descom, p.strIstat provincia, " + "az.codnatgiuridica "
			+ "FROM AN_AZIENDA az " + "LEFT JOIN AN_UNITA_AZIENDA auz on az.prgAzienda=auz.prgAzienda "
			+ "LEFT JOIN DE_COMUNE c on auz.codcom=c.codcom "
			+ "LEFT JOIN DE_PROVINCIA p on c.CODPROVINCIA=p.codprovincia "
			+ "INNER JOIN de_Attivita att on auz.codateco=att.codateco "
			+ "LEFT JOIN de_attivita att2 on att2.codateco=att.codpadre";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cf = (String) req.getAttribute("cf");
		String pi = (String) req.getAttribute("piva");
		String ragsoc = (String) req.getAttribute("RagioneSociale");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((ragsoc != null) && (!ragsoc.equals(""))) {
			ragsoc = StringUtils.replace(ragsoc, "'", "''");
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" lower(az.strRagioneSociale)  like ('%" + ragsoc.toLowerCase() + "%')");
		}

		if ((pi != null) && (!pi.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" az.strPartitaIva like '" + pi + "%'");
		}

		if ((cf != null) && (!cf.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(az.strcodicefiscale) like ('" + cf.toUpperCase() + "%')");
		}

		buf.append("order by az.strRagioneSociale, auz.prgunita");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}