package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la ricerca dinamica di una dichiarazione di sospensione dato: - o il codice fiscale - o il nome - o il
 * cognome - o il CPI competente
 * 
 * @author Alessio Rolfini
 * 
 */
public class DynStatementRicercadDichSospensione implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " SELECT sosp.prgDichSospensione, "
			+ " lav.cdnLavoratore, lav.strCognome, lav.strnome, lav.strCodiceFiscale, "
			+ " az.STRRAGIONESOCIALE, az.strPartitaIva, "
			+ " decode(uaz.strIndirizzo, '', '', uaz.strIndirizzo ||',  ' || VW_INDIRIZZI_COM_PROV.DENOMINAZIONECOMUNE || nvl(' (' || VW_INDIRIZZI_COM_PROV.DENOMINAZIONEPROV ||') ', VW_INDIRIZZI_COM_PROV.CODPROVINCIA))  as strIndirizzo, "
			+ " TO_CHAR(sosp.DATDICHIARAZIONE, 'DD/MM/YYYY') as DatDichiarazione, TO_CHAR(sosp.DATFINE, 'DD/MM/YYYY') as DatFine, "
			+ " decode(tab.prgDocumento, null,'1','0') as protocollato " + " FROM AM_DICH_SOSPENSIONE sosp "
			+ " INNER JOIN AN_LAVORATORE lav on sosp.cdnLavoratore=lav.cdnLavoratore "
			+ " INNER JOIN AN_LAV_STORIA_INF lavs on (lav.cdnLavoratore=lavs.cdnLavoratore and lavs.datFine is null) "
			+ " LEFT JOIN AN_AZIENDA az on (sosp.prgAzienda=az.prgAzienda) "
			+ " LEFT JOIN AN_UNITA_AZIENDA uaz on (sosp.prgAzienda=uaz.prgAzienda and sosp.prgUnita=uaz.prgUnita) "
			+ " LEFT JOIN VW_INDIRIZZI_COM_PROV on VW_INDIRIZZI_COM_PROV.codcom = uaz.codcom   " + " left join "
			+ "      (select doc.cdnlavoratore , coll.strchiavetabella, doc.prgdocumento, coll.cdncomponente "
			+ " 		 from am_documento doc left join am_documento_coll coll "
			+ "				on(coll.prgdocumento = doc.prgdocumento "
			+ "				   and coll.cdncomponente =  (select cdncomponente from ts_componente where upper(ts_componente.STRPAGE) = upper('AmmDettDichSospPage'))) "
			+ "		 where doc.CODTIPODOCUMENTO='SOSP' ) tab "
			+ "   on (tab.cdnlavoratore = lav.cdnlavoratore and tab.strchiavetabella = to_char(sosp.prgDichSospensione)) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cdnLavoratore = (String) req.getAttribute("cdnLavoratore");
		String nome = (String) req.getAttribute("strNome");
		String cognome = (String) req.getAttribute("strCognome");
		String cf = (String) req.getAttribute("strCodiceFiscale");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");
		String codCPI = StringUtils.getAttributeStrNotNull(req, "CodCPI");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (tipoRic.equalsIgnoreCase("esatta")) {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(lav.strnome) = '" + nome.toUpperCase() + "'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND");
				}
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(lav.strcognome) = '" + cognome.toUpperCase() + "'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(lav.strCodiceFiscale) = '" + cf.toUpperCase() + "'");
			}
		} else {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(lav.strnome) like '" + nome.toUpperCase() + "%'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND");
				}
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(lav.strcognome) like '" + cognome.toUpperCase() + "%'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND");
				}
				buf.append(" upper(lav.strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
			}
		}

		if (buf.length() == 0) {
			buf.append(" WHERE ");
		} else {
			buf.append(" AND");
		}

		// ricerca tramite il cdnlavoratore (dovrebbe essere utilizzata solo dal
		// menu contestuale)
		if (cdnLavoratore != null) {
			buf.append(" lav.cdnLavoratore= " + cdnLavoratore + " ");
		} else {
			buf.append(" LAVS.CODMONOTIPOCPI='C' ");
			buf.append(" AND LAVS.CODCPITIT= '" + codCPI + "' ");
		}

		buf.append(" AND (SOSP.DATFINE>=SYSDATE OR SOSP.DATFINE IS NULL) ");
		buf.append("ORDER BY upper(lav.STRCOGNOME), upper(lav.STRNOME), upper(lav.STRCODICEFISCALE)");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}