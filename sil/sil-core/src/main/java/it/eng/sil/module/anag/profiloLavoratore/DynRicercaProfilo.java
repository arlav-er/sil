package it.eng.sil.module.anag.profiloLavoratore;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

public class DynRicercaProfilo implements IDynamicStatementProvider {

	public DynRicercaProfilo() {
	}

	private static final String SELECT_SQL_BASE = "SELECT PRGLAVORATOREPROFILO, LAV.Strcognome, LAV.Strnome, "
			+ "LAV.Strcodicefiscale, TO_CHAR(DATCREAZIONEPROFILO, 'DD/MM/YYYY') STRDATCREAZIONEPROFILO, "
			+ "case  when CODMONOSTATOPROF = 'I' then 'In compilazione' "
			+ "when CODMONOSTATOPROF = 'C'  then  'Calcolato' "
			+ "when CODMONOSTATOPROF not in ('I','C') then  ''  end STRSTATOPROF, "
			+ "NUMVALOREPROFILO, cpiComp.Strdescrizione STRCPICOMP, " + "cpiProf.Strdescrizione STRCPIPROF, "
			+ "LAV.CDNLAVORATORE, " + "De_Vch_Profiling.Strdescrizione indiceProfilo "
			+ "FROM Am_Lavoratore_Profilo prof "
			+ "INNER JOIN AN_LAVORATORE LAV ON (prof.CDNLAVORATORE = LAV.CDNLAVORATORE) "
			+ "INNER JOIN de_cpi cpiProf on (prof.CODCPI = cpiProf.codcpi) "
			+ "LEFT JOIN An_Lav_Storia_Inf ON (An_Lav_Storia_Inf.Cdnlavoratore = prof.CDNLAVORATORE "
			+ "AND an_lav_storia_inf.datFine is null) "
			+ "LEFT JOIN de_cpi cpiComp on (an_lav_storia_inf.codcpitit = cpiComp.codcpi) "
			+ "LEFT JOIN De_Vch_Profiling ON (prof.CODVCHPROFILING = De_Vch_Profiling.Codvchprofiling) ";

	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String nome = SourceBeanUtils.getAttrStrNotNull(req, "strNome");
		String cognome = SourceBeanUtils.getAttrStrNotNull(req, "strCognome");
		String cf = SourceBeanUtils.getAttrStrNotNull(req, "strCodiceFiscale");
		String tipoRic = SourceBeanUtils.getAttrStrNotNull(req, "tipoRicerca");
		String statoProfilo = SourceBeanUtils.getAttrStrNotNull(req, "statoProfilo");
		String dataProfiloDa = SourceBeanUtils.getAttrStrNotNull(req, "dataProfiloDa");
		String dataProfiloA = SourceBeanUtils.getAttrStrNotNull(req, "dataProfiloA");
		String indiceProfilo = SourceBeanUtils.getAttrStrNotNull(req, "indiceProfilo");
		String codCpiComp = SourceBeanUtils.getAttrStrNotNull(req, "codCpiComp");
		String codCpiProf = SourceBeanUtils.getAttrStrNotNull(req, "codCpiProf");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (tipoRic.equalsIgnoreCase("esatta")) {

			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strnome) = '" + nome.toUpperCase() + "'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				cognome = StringUtils.replace(cognome, "'", "''");
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strcognome) = '" + cognome.toUpperCase() + "'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strCodiceFiscale) = '" + cf.toUpperCase() + "'");
			}
		} else {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strnome) like '" + nome.toUpperCase() + "%'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				cognome = StringUtils.replace(cognome, "'", "''");
				buf.append(" upper(lav.strcognome) like '" + cognome.toUpperCase() + "%'");
			}

			if ((cf != null) && (!cf.equals(""))) {
				if (buf.length() == 0) {
					buf.append(" WHERE ");
				} else {
					buf.append(" AND ");
				}
				buf.append(" upper(lav.strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
			}
		}
		if ((dataProfiloDa != null) && (!dataProfiloDa.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" TRUNC(DATCREAZIONEPROFILO) >= to_date('" + dataProfiloDa + "', 'dd/mm/yyyy') ");
		}
		if ((dataProfiloA != null) && (!dataProfiloA.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" TRUNC(DATCREAZIONEPROFILO) <= to_date('" + dataProfiloA + "', 'dd/mm/yyyy') ");
		}

		if ((indiceProfilo != null) && (!indiceProfilo.equals(""))) {
			indiceProfilo = StringUtils.replace(indiceProfilo, "'", "''");
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" upper(CODVCHPROFILING) = '" + indiceProfilo.toUpperCase() + "'");
		}

		if ((statoProfilo != null) && (!statoProfilo.equals(""))) {

			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" upper(CODMONOSTATOPROF) = '" + statoProfilo.toUpperCase() + "'");
		}

		if ((codCpiComp != null) && (!codCpiComp.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" An_Lav_Storia_Inf.Prglavstoriainf is not null and cpiComp.codcpi = '" + codCpiComp + "'");
			buf.append(" and An_Lav_Storia_Inf.codmonotipocpi = 'C' ");
		}

		if ((codCpiProf != null) && (!codCpiProf.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND ");
			}
			buf.append(" PROF.CODCPI = '" + codCpiProf + "'");
		}

		buf.append(" ORDER BY LAV.strCodiceFiscale ASC, TRUNC(DATCREAZIONEPROFILO) DESC");
		query_totale.append(buf.toString());
		return query_totale.toString();
	}

}
