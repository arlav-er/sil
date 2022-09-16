package it.eng.sil.module.anag;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

public class DynamicListaRA implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " SELECT ra.prgRedditoAttivazione, " + "ra.strCodiceFiscale, "
			+ "ra.strCognome, " + "ra.strNome, "
			+ " to_char(ra.datInizioPrestazione,'DD/MM/YYYY') datInizioPrestazione, "
			+ " to_char(ra.datFinePrestazione,'DD/MM/YYYY') datFinePrestazione, " + "stato.strDescrizione descrStato, "
			+ "to_char(filera.dtmIns,'DD/MM/YYYY') datCaricamentoFile, ra.decimportolordocomplessivo, "
			+ "filera.strNomefileXml nomeFileRa " + "from AM_REDDITO_ATTIVAZIONE ra "
			+ "inner join AM_RA_FILE filera on filera.prgRaFile = ra.prgRaFile "
			+ "left join DE_STATO_RA stato on stato.codStatoRa = ra.codStatoRa ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		// leggo il cdnLavoratore, se presente siamo in un contesto
		String cdnLavoratore = StringUtils.getAttributeStrNotNull(req, "cdnLavoratore");

		String CF = (String) SourceBeanUtils.getAttrStrNotNull(req, "CF");
		String COGNOME = (String) SourceBeanUtils.getAttrStrNotNull(req, "COGNOME");
		String NOME = (String) SourceBeanUtils.getAttrStrNotNull(req, "NOME");
		String datprestazioneda = (String) SourceBeanUtils.getAttrStrNotNull(req, "datprestazioneda");
		String datprestazionea = (String) SourceBeanUtils.getAttrStrNotNull(req, "datprestazionea");
		String stato = (String) SourceBeanUtils.getAttrStrNotNull(req, "stato");
		String datcaricamentoda = (String) SourceBeanUtils.getAttrStrNotNull(req, "datcaricamentoda");
		String datcaricamentoa = (String) SourceBeanUtils.getAttrStrNotNull(req, "datcaricamentoa");
		String tipoRic = (String) SourceBeanUtils.getAttrStrNotNull(req, "tipoRicerca");
		String codProvenienza = (String) SourceBeanUtils.getAttrStrNotNull(req, "codProvenienza");
		String nomeFile = (String) SourceBeanUtils.getAttrStrNotNull(req, "nomeFile");

		String opTipoRic = "like";
		String opTipoRic2 = "%";

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (tipoRic.equalsIgnoreCase("esatta")) {
			opTipoRic = "=";
			opTipoRic2 = "";
		}

		if (!cdnLavoratore.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" ra.cdnLavoratore = " + cdnLavoratore);
		}

		if (!CF.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(ra.strCodiceFiscale) " + opTipoRic + " '" + CF.toUpperCase() + opTipoRic2 + "'");
		}

		if (!COGNOME.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(ra.strCognome) " + opTipoRic + " '" + COGNOME.toUpperCase() + opTipoRic2 + "'");
		}

		if (!NOME.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(ra.strNome) " + opTipoRic + " '" + NOME.toUpperCase() + opTipoRic2 + "'");
		}

		if (!codProvenienza.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" ra.codProvenienza = '" + codProvenienza + "'");
		}

		if (!datprestazioneda.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" ra.datInizioPrestazione >= TO_DATE('" + datprestazioneda + "','DD/MM/YYYY')");
		}

		if (!datprestazionea.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" ra.datFinePrestazione <= TO_DATE('" + datprestazionea + "','DD/MM/YYYY')");
		}

		if (!stato.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" ra.codStatoRa = '" + stato + "'");
		}

		if (!datcaricamentoda.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" filera.dtmIns >= TO_DATE('" + datcaricamentoda + "','DD/MM/YYYY')");
		}

		if (!datcaricamentoa.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" filera.dtmIns <= TO_DATE('" + datcaricamentoa + "','DD/MM/YYYY')");
		}

		if (!nomeFile.equals("")) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" ra.prgRaFile = " + nomeFile);
		}

		buf.append(" ORDER BY filera.strNomefileXml, ra.strCodiceFiscale desc ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}