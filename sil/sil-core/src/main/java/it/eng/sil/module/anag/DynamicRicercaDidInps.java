package it.eng.sil.module.anag;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la ricerca dinamica di un iscritto dato: - o il codice fiscale - o il nome - o il cognome - o nessuna delle
 * precedenti (restituisce TUTTO)
 * 
 * @author Alessio Rolfini, Modificata 17/06/16 - 20/07/16 Giacomo Pandini
 * 
 */
public class DynamicRicercaDidInps implements IDynamicStatementProvider {

	// Query Base
	private static final String SELECT_SQL_BASE = " SELECT l.CDNLAVORATORE,  " + " l.STRCODICEFISCALELAV, "
			+ " l.PRGDIDINPS, " + " l.codunivoco, " + " TO_CHAR(l.DATDICHIARAZIONE,'DD/MM/YYYY') DATDICHIARAZIONE, "
			// + " l.CODMONOTIPOOPERAZIONE "
			+ " decode (l.CODMONOTIPOOPERAZIONE, 'D', 'Cancellazione','I', 'Inserimento','U','Modifica', l.CODMONOTIPOOPERAZIONE) AS CODMONOTIPOOPERAZIONE"
			// + " decode (ai.codMonoTipoCpi, 'C', '', 'T', cpi.strDescrizione, 'E', 'Fuori Provincia') as CpiTit "
			+ " FROM ( AM_DID_INPS l ";
	// + " inner join an_lav_storia_inf ai on (l.cdnLavoratore=ai.cdnLavoratore and ai.datFine is null) "
	// + " inner join de_comune c on (c.codCom=l.codComNas) "
	// + " inner join de_provincia pc on (c.CODPROVINCIA=pc.codProvincia) "
	// + " left join de_cpi cpi on (l.codunivoco=cpi.codCpimin) ";
	// + " left join de_cpi cpiO on (ai.codCpiOrig=cpiO.codCpi) ";

	// Componente della query nel caso si cerchino lavoratori senza DID valida
	private static final String QUERY_DID_NON_VALIDA = "" + " NOT EXISTS " + " ( SELECT 1 "
			+ " FROM (AM_DID_INPS l1 INNER JOIN " + " (SELECT * " + " FROM AM_ELENCO_ANAGRAFICO k1 "
			+ " WHERE NOT EXISTS " + " ( SELECT 1 " + " FROM AM_ELENCO_ANAGRAFICO k2 " + " WHERE EXISTS "
			+ " ( SELECT 1 " + " FROM AM_ELENCO_ANAGRAFICO k3 " + " WHERE k2.CDNLAVORATORE = k3.CDNLAVORATORE "
			+ " GROUP BY CDNLAVORATORE " + "  HAVING count(*) > 1 ) " + " AND DATCAN IS NOT NULL "
			+ " AND k1.PRGELENCOANAGRAFICO = k2.PRGELENCOANAGRAFICO  ) )m "
			+ " ON l1.CDNLAVORATORE = m.CDNLAVORATORE ) " + " INNER JOIN AM_DICH_DISPONIBILITA ON "
			+ " m.PRGELENCOANAGRAFICO = " + " AM_DICH_DISPONIBILITA.PRGELENCOANAGRAFICO "
			+ " WHERE CODSTATOATTO = 'PR' AND " + " DATFINE IS NULL AND l1.CDNLAVORATORE = " + " l.CDNLAVORATORE ) "
			+ " AND l.CDNLAVORATORE IS NOT NULL ";

	// Componente della query nel caso si cerchino lavoratori con DID valida
	private static final String QUERY_DID_VALIDA = "" + " INNER JOIN " + " ( SELECT * "
			+ "	FROM AM_ELENCO_ANAGRAFICO k1 " + "	WHERE NOT EXISTS " + "	( SELECT 1 "
			+ "	FROM AM_ELENCO_ANAGRAFICO k2 " + " WHERE EXISTS " + " ( SELECT 1 " + " FROM AM_ELENCO_ANAGRAFICO k3 "
			+ " WHERE k2.CDNLAVORATORE = k3.CDNLAVORATORE " + "	GROUP BY CDNLAVORATORE " + "	HAVING count(*) > 1 ) "
			+ "	AND DATCAN IS NOT NULL " + " AND k1.PRGELENCOANAGRAFICO = k2.PRGELENCOANAGRAFICO ) ) m "
			+ "	ON l.CDNLAVORATORE = m.CDNLAVORATORE ) "
			+ " INNER JOIN AM_DICH_DISPONIBILITA ON M.PRGELENCOANAGRAFICO = AM_DICH_DISPONIBILITA.PRGELENCOANAGRAFICO ";

	private static final String QUERY_NON_SISTEMA = "" + " NOT EXISTS (" + " SELECT 1 " + " FROM AN_LAVORATORE m "
			+ " WHERE m.STRCODICEFISCALE = l.STRCODICEFISCALELAV ) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		// Prende parametri dal SourceBean
		String cf = (String) SourceBeanUtils.getAttrStrNotNull(req, "strCodiceFiscale");
		String datin = (String) SourceBeanUtils.getAttrStrNotNull(req, "datstipulada");
		String datTo = (String) SourceBeanUtils.getAttrStrNotNull(req, "datstipulaa");
		String codCpi = (String) SourceBeanUtils.getAttrStrNotNull(req, "CodCPI");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");
		String codDID = (String) SourceBeanUtils.getAttrStrNotNull(req, "CodDID");

		// String Buffer
		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer query_did_non_valida = new StringBuffer(QUERY_DID_NON_VALIDA);
		StringBuffer query_did_valida = new StringBuffer(QUERY_DID_VALIDA);
		StringBuffer query_non_sistema = new StringBuffer(QUERY_NON_SISTEMA);
		StringBuffer buf = new StringBuffer();

		// Tipo Ricerca
		if (tipoRic.equalsIgnoreCase("esatta")) {

			if (!cf.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(strCodiceFiscalelav) = '" + cf.toUpperCase() + "'");
			}
		} else {

			if (!cf.equals("")) {
				if (buf.length() == 0) {
					buf.append("WHERE");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(strCodiceFiscalelav) like '" + cf.toUpperCase() + "%'");
			}
		}

		// Data stipula da
		if (!datin.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" l.DATDICHIARAZIONE >= TO_DATE('" + datin + "','DD/MM/YYYY') ");
		}

		// Data stipula a
		if (!datTo.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" l.DATDICHIARAZIONE <= TO_DATE('" + datTo + "','DD/MM/YYYY') ");
		}

		// Centro per l'impiego
		if (!codCpi.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}

			buf.append(" l.CODUNIVOCO in ( select codcpimin from de_cpi where codcpi ='" + codCpi + "')");
		}

		// DID = TUTTI
		if (codDID.equals("T")) {
			query_totale.append(" ) ");
		}

		// Did = DID valida
		if (codDID.equals("D")) {
			query_totale.append(query_did_valida.toString());
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" CODSTATOATTO = 'PR' AND DATFINE IS NULL");
		}

		// Did = DID non valida
		if (codDID.equals("S")) {
			query_totale.append(" ) ");
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(query_did_non_valida.toString());
		}

		// DID = non a sistema
		if (codDID.equals("N")) {
			query_totale.append(" ) ");
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(query_non_sistema.toString());
		}

		buf.append(" ORDER BY upper(strCodiceFiscalelav), DATDICHIARAZIONE DESC, PRGDIDINPS DESC ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}