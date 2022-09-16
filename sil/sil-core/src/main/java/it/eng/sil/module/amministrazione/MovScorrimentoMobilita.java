package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

public class MovScorrimentoMobilita implements IDynamicStatementProvider {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MovScorrimentoMobilita.class.getName());
	private String className = this.getClass().getName();

	private String SELECT_SQL_BASE_SENZA_APPRENDISTATO = "SELECT am_movimento.codMonoTempo, "
			+ " (CASE WHEN (am_movimento.codTipoMov = 'CES' and am_movimento.codMonoTempo = 'I' and nvl(de_mv_cessazione.flgmobilitarimaneaperta, 'N') = 'S') "
			+ " then null " + " ELSE to_char(am_movimento.datInizioMov,'dd/mm/yyyy') END) datInizioMov, "
			+ " (CASE WHEN (am_movimento.codTipoMov = 'CES' and am_movimento.codMonoTempo = 'I' and nvl(de_mv_cessazione.flgmobilitarimaneaperta, 'N') = 'S') "
			+ " then to_char(am_movimento.datInizioMov,'dd/mm/yyyy') "
			+ " ELSE to_char(am_movimento.datFineMovEffettiva,'dd/mm/yyyy') END) datFineMov, "
			+ " an_azienda.strragionesociale || '-' || an_azienda.strcodicefiscale ragionesociale, "
			+ " am_movimento.codTipoMov, am_movimento.codMonoTipoFine, am_movimento.decretribuzionemen, am_movimento.decretribuzionemensanata, "
			+ " am_movimento.numggeffettuatiagr, am_movimento.numggprevistiagr, "
			+ " to_char(am_movimento.numoresett) oreSettimanali, to_char(de_mv_cessazione.nummesimobaperta) nummesimobaperta "
			+ " FROM am_movimento, de_tipo_contratto, de_orario, an_azienda, de_mv_cessazione "
			+ " WHERE am_movimento.codtipocontratto = de_tipo_contratto.codtipocontratto(+) and am_movimento.prgazienda = an_azienda.prgazienda "
			+ " and am_movimento.codmvcessazione = de_mv_cessazione.codmvcessazione(+) "
			+ " and am_movimento.codorario = de_orario.codorario(+) and nvl(de_tipo_contratto.codMonoTipo,' ') not in ('N','T') "
			+ " and nvl(de_tipo_contratto.codContratto, ' ') not in ('LO', 'PG', 'CO', 'RP1') "
			+ " and am_movimento.codStatoAtto = 'PR' and "
			+ " ( (am_movimento.codTipoMov = 'CES' and (am_movimento.numggeffettuatiagr is not null or nvl(de_mv_cessazione.flgmobilitarimaneaperta, 'N') = 'S')) "
			+ " or (am_movimento.codTipoMov <> 'CES')) and " + " am_movimento.codTipoContratto <> 'Z.09.02' and "
			+ " ( (am_movimento.codMonoTempo = 'D') or (am_movimento.codMonoTempo = 'I' and nvl(de_orario.codMonoOrario,' ') = 'P') or "
			+ " (am_movimento.codMonoTempo = 'I' and nvl(de_tipo_contratto.codContratto,' ') = 'LI') or "
			+ " (am_movimento.codTipoMov = 'CES' and am_movimento.codMonoTempo = 'I' and nvl(de_mv_cessazione.flgmobilitarimaneaperta, 'N') = 'S' "
			+ " and am_movimento.prgmovimentoprec is not null and (de_mv_cessazione.nummesimobaperta is null or "
			+ " de_mv_cessazione.nummesimobaperta >= (trunc(MONTHS_BETWEEN(trunc(am_movimento.datInizioMov), trunc(nvl(am_movimento.datInizioAvv, am_movimento.datInizioMov))) + 1, 0) "
			+ " - (case when (trunc(MONTHS_BETWEEN(trunc(am_movimento.datInizioMov), trunc(nvl(am_movimento.datInizioAvv, am_movimento.datInizioMov))) + 1, 0)) = 1 "
			+ " and (to_number(to_char(am_movimento.datInizioMov, 'dd')) - to_number(to_char(nvl(am_movimento.datInizioAvv, am_movimento.datInizioMov), 'dd')) + 1 < 16) "
			+ " then 1 else 0 " + " end) "
			+ " - (case when (trunc(MONTHS_BETWEEN(trunc(am_movimento.datInizioMov), trunc(nvl(am_movimento.datInizioAvv, am_movimento.datInizioMov))) + 1, 0))  > 1 "
			+ " and (30 - to_number(to_char(nvl(am_movimento.datInizioAvv, am_movimento.datInizioMov), 'dd')) + 1 < 16) "
			+ " then 1 else 0 " + " end) "
			+ " - (case when (trunc(MONTHS_BETWEEN(trunc(am_movimento.datInizioMov), trunc(nvl(am_movimento.datInizioAvv, am_movimento.datInizioMov))) + 1, 0)) > 1 "
			+ " and (to_number(to_char(am_movimento.datInizioMov, 'dd')) < 16) " + " then 1 else 0 " + " end) ) "
			+ " ) ) or "
			+ " (am_movimento.codMonoTempo = 'I' and nvl(de_orario.codMonoOrario,' ') = 'N' and am_movimento.numoresett is not null) ) ";

	private String SELECT_SQL_BASE_CON_APPRENDISTATO = "SELECT am_movimento.codMonoTempo, "
			+ " (CASE WHEN (am_movimento.codTipoMov = 'CES' and am_movimento.codMonoTempo = 'I' and nvl(de_mv_cessazione.flgmobilitarimaneaperta, 'N') = 'S') "
			+ " then null " + " ELSE to_char(am_movimento.datInizioMov,'dd/mm/yyyy') END) datInizioMov, "
			+ " (CASE WHEN (am_movimento.codTipoMov = 'CES' and am_movimento.codMonoTempo = 'I' and nvl(de_mv_cessazione.flgmobilitarimaneaperta, 'N') = 'S') "
			+ " then to_char(am_movimento.datInizioMov,'dd/mm/yyyy') "
			+ " ELSE to_char(am_movimento.datFineMovEffettiva,'dd/mm/yyyy') END) datFineMov, "
			+ " an_azienda.strragionesociale || '-' || an_azienda.strcodicefiscale ragionesociale, "
			+ " am_movimento.codTipoMov, am_movimento.codMonoTipoFine, am_movimento.decretribuzionemen, am_movimento.decretribuzionemensanata, "
			+ " am_movimento.numggeffettuatiagr, am_movimento.numggprevistiagr, "
			+ " to_char(am_movimento.numoresett) oreSettimanali, to_char(de_mv_cessazione.nummesimobaperta) nummesimobaperta "
			+ " FROM am_movimento, de_tipo_contratto, de_orario, an_azienda, de_mv_cessazione "
			+ " WHERE am_movimento.codtipocontratto = de_tipo_contratto.codtipocontratto(+) and am_movimento.prgazienda = an_azienda.prgazienda "
			+ " and am_movimento.codmvcessazione = de_mv_cessazione.codmvcessazione(+) "
			+ " and am_movimento.codorario = de_orario.codorario(+) and nvl(de_tipo_contratto.codMonoTipo,' ') not in ('N','T') "
			+ " and nvl(de_tipo_contratto.codContratto, ' ') not in ('LO', 'PG', 'CO', 'RP1') "
			+ " and am_movimento.codStatoAtto = 'PR' and "
			+ " ( (am_movimento.codTipoMov = 'CES' and (am_movimento.numggeffettuatiagr is not null or nvl(de_mv_cessazione.flgmobilitarimaneaperta, 'N') = 'S')) "
			+ " or (am_movimento.codTipoMov <> 'CES')) and " + " am_movimento.codTipoContratto <> 'Z.09.02' and"
			+ " ( (am_movimento.codMonoTempo = 'D' and (am_movimento.codtipocontratto = 'A.03.00' or am_movimento.codtipocontratto = 'A.03.01'"
			+ " or am_movimento.codtipocontratto = 'A.03.02' or am_movimento.codtipocontratto = 'A.03.03') and "
			+ " (nvl(de_orario.codMonoOrario,' ') = 'P' or (nvl(de_orario.codMonoOrario,' ') = 'N' and am_movimento.numoresett is not null)) ) or "
			+ " (am_movimento.codMonoTempo = 'D' and am_movimento.codtipocontratto <> 'A.03.00' and am_movimento.codtipocontratto <> 'A.03.01' and "
			+ " am_movimento.codtipocontratto <> 'A.03.02' and am_movimento.codtipocontratto <> 'A.03.03') "
			+ " or (am_movimento.codMonoTempo = 'I' and nvl(de_orario.codMonoOrario,' ') = 'P') or "
			+ " (am_movimento.codMonoTempo = 'I' and nvl(de_tipo_contratto.codContratto,' ') = 'LI') or "
			+ " (am_movimento.codTipoMov = 'CES' and am_movimento.codMonoTempo = 'I' and nvl(de_mv_cessazione.flgmobilitarimaneaperta, 'N') = 'S' "
			+ " and am_movimento.prgmovimentoprec is not null and (de_mv_cessazione.nummesimobaperta is null or "
			+ " de_mv_cessazione.nummesimobaperta >= (trunc(MONTHS_BETWEEN(trunc(am_movimento.datInizioMov), trunc(nvl(am_movimento.datInizioAvv, am_movimento.datInizioMov))) + 1, 0) "
			+ " - (case when (trunc(MONTHS_BETWEEN(trunc(am_movimento.datInizioMov), trunc(nvl(am_movimento.datInizioAvv, am_movimento.datInizioMov))) + 1, 0)) = 1 "
			+ " and (to_number(to_char(am_movimento.datInizioMov, 'dd')) - to_number(to_char(nvl(am_movimento.datInizioAvv, am_movimento.datInizioMov), 'dd')) + 1 < 16) "
			+ " then 1 else 0 " + " end) "
			+ " - (case when (trunc(MONTHS_BETWEEN(trunc(am_movimento.datInizioMov), trunc(nvl(am_movimento.datInizioAvv, am_movimento.datInizioMov))) + 1, 0))  > 1 "
			+ " and (30 - to_number(to_char(nvl(am_movimento.datInizioAvv, am_movimento.datInizioMov), 'dd')) + 1 < 16) "
			+ " then 1 else 0 " + " end) "
			+ " - (case when (trunc(MONTHS_BETWEEN(trunc(am_movimento.datInizioMov), trunc(nvl(am_movimento.datInizioAvv, am_movimento.datInizioMov))) + 1, 0)) > 1 "
			+ " and (to_number(to_char(am_movimento.datInizioMov, 'dd')) < 16) " + " then 1 else 0 " + " end) ) "
			+ " ) ) or "
			+ " (am_movimento.codMonoTempo = 'I' and nvl(de_orario.codMonoOrario,' ') = 'N' and am_movimento.numoresett is not null) ) ";

	private String SELECT_SQL_BASE = "";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		_logger.debug(className + ".getStatement() INIZIO");
		String flgMovApprendistatoTIMob = "";
		try {
			SourceBean sbGenerale = DBLoad.getInfoGenerali();
			flgMovApprendistatoTIMob = sbGenerale.getAttribute("FLGAPPRENDISTATOTIMB") != null
					? sbGenerale.getAttribute("FLGAPPRENDISTATOTIMB").toString()
					: "";
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore lettura ts_generale", e);
		}
		if (flgMovApprendistatoTIMob.equalsIgnoreCase("S")) {
			SELECT_SQL_BASE = SELECT_SQL_BASE_CON_APPRENDISTATO;
		} else {
			SELECT_SQL_BASE = SELECT_SQL_BASE_SENZA_APPRENDISTATO;
		}
		SourceBean req = requestContainer.getServiceRequest();
		String cdnLav = req.containsAttribute("CDNLAVORATORE") ? req.getAttribute("CDNLAVORATORE").toString() : "";
		String datInizioMob = req.containsAttribute("DATINIZIOMOB") ? req.getAttribute("DATINIZIOMOB").toString() : "";
		String datFine = req.containsAttribute("DATMAXDIFF") ? req.getAttribute("DATMAXDIFF").toString() : "";
		if (!cdnLav.equals("")) {
			SELECT_SQL_BASE = SELECT_SQL_BASE + " AND am_movimento.cdnlavoratore = " + cdnLav;
		}
		if (!datInizioMob.equals("")) {
			SELECT_SQL_BASE = SELECT_SQL_BASE + " AND (trunc(am_movimento.datInizioMov) >= to_date('" + datInizioMob
					+ "','dd/mm/yyyy') OR "
					+ "am_movimento.datfinemoveffettiva is null OR trunc(am_movimento.datfinemoveffettiva) >= to_date('"
					+ datInizioMob + "','dd/mm/yyyy')) ";
		}
		if (!datFine.equals("")) {
			SELECT_SQL_BASE = SELECT_SQL_BASE + " AND trunc(am_movimento.datInizioMov) <= to_date('" + datFine
					+ "','dd/mm/yyyy') ";
		}

		SELECT_SQL_BASE = SELECT_SQL_BASE
				+ " order by am_movimento.datInizioMov asc, Decode (am_movimento.codtipomov, 'AVV', 1, 'PRO', 2, 'TRA', 3, 'CES', 4) asc ";

		return SELECT_SQL_BASE;
	}
}