package it.eng.sil.module.agenda;

import java.util.Calendar;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce i giorni non lavorativi 
 * relativi al mese e all'anno 
 * (presenti come parametri nella request, oppure mese/anno correnti)
 * 
 * @author: Stefania Orioli
 */

public class GiorniVista implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GiorniVista.class.getName());

	public GiorniVista() {
	}

	private static final String SELECT_SQL_BASE = "select * "
			+ "from vw_ag_calendario where vw_ag_calendario.CODTIPOVISTA=";

	private static int mapping_mesiDB[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		int cdnUt = user.getCodut();
		int cdnTipoGruppo = user.getCdnTipoGruppo();
		String codCpi = StringUtils.getAttributeStrNotNull(req, "CODCPI");
		if (codCpi.equalsIgnoreCase("")) {
			if (cdnTipoGruppo == 1) {
				codCpi = user.getCodRif();
			}
		}

		String cod_vista = (String) req.getAttribute("cod_vista");
		String mese = (String) req.getAttribute("mese");
		String anno = (String) req.getAttribute("anno");
		// Savino 16/08/05 - applicazione dei filtri attivi
		String fmiei = (String) sessionContainer.getAttribute("agenda_FMiei");
		String foperatore = (String) sessionContainer.getAttribute("agenda_FOperatore");
		String fservizio = (String) sessionContainer.getAttribute("agenda_FServizio");
		String faula = (String) sessionContainer.getAttribute("agenda_FAula");
		String fattivi = (String) sessionContainer.getAttribute("agenda_FAttivi");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		// Data Odierna
		Calendar oggi = Calendar.getInstance();
		int oggi_mm = oggi.get(2) + 1;
		int oggi_aa = oggi.get(1);

		// Codice della vista selezionata
		buf.append("'" + cod_vista + "' ");
		buf.append(" and ");
		if ((mese != null) && (!mese.equals(""))) {
			buf.append(" (NUMMESE = " + mapping_mesiDB[Integer.parseInt(mese)] + ") ");
		} else {
			buf.append(" (NUMMESE = " + oggi_mm + ") ");
		}

		buf.append(" and ");

		if ((anno != null) && (!anno.equals(""))) {
			buf.append(" (NUMANNO = " + anno + ") ");
		} else {
			buf.append(" (NUMANNO = " + oggi_aa + ") ");
		}

		/*
		 * In questa sezione dovrei inserire il codcpi che è in sessione oppure il codice scelto dal Lavoratore/Azienda,
		 * quindi codcpi non deve essere mai nullo???
		 */
		if ((codCpi != null) && (!codCpi.equals(""))) {
			buf.append(" and (codcpi='" + codCpi + "') ");
		}

		// applicazione dei filtri attivi: da applicare solo se il codice vista
		// e' diverso da "giorni non lavorativi": 0
		int codVista = -1;
		try {
			if (cod_vista != null && !cod_vista.equals(""))
				codVista = Integer.parseInt(cod_vista);
		} catch (NumberFormatException e) {
			_logger.debug("GiorniVista: codice vista non numerico:" + cod_vista);

		}
		if (((fmiei != null && fmiei.equals("1")) || (foperatore != null && !foperatore.equals(""))
				|| (fservizio != null && !fservizio.equals("")) || (faula != null && !faula.equals(""))
				|| (fattivi != null && !fattivi.equals("") && codVista == 1)) && (codVista > 0)) {
			buf.append(" and exists ( select 1 from ");
			if (codVista == 1 || codVista == 2) {
				// 1) giorni con prenotazioni
				// 2) Incongruenze per Stesso Lavoratore - Incongruenze per
				// stesso operatore
				buf.append(" ag_agenda tab, ts_utente, de_stato_appuntamento ");
				buf.append(
						" where to_date(to_char(tab.dtmDataOra, 'dd/mm/YYYY'), 'dd/mm/yyyy')= vw_ag_calendario.datData");
				buf.append(" and tab.codCpi = vw_ag_calendario.codCpi ");
				buf.append(" and tab.codStatoAppuntamento = de_stato_appuntamento.codStatoAppuntamento (+)");
				buf.append(" and tab.prgSpi = ts_utente.prgSpi (+) ");
			}
			if (codVista == 3) { // 3) GIORNI CON SLOT PRENOTABILI
				buf.append(" ag_slot tab, ts_utente, ag_spi_slot, de_stato_slot st ");
				buf.append(
						" where to_date(to_char(tab.dtmDataOra, 'dd/mm/YYYY'), 'dd/mm/yyyy')= vw_ag_calendario.datData");
				buf.append(" and tab.codCpi = vw_ag_calendario.codCpi ");
				buf.append(" and tab.prgSlot = ag_spi_slot.prgSlot(+)");
				buf.append(" and ag_spi_slot.prgSpi = ts_utente.prgSpi(+)");
				buf.append(" and (tab.CODSTATOSLOT=st.codStatoSlot)");
				buf.append(" and (st.flgprenotabile='S') ");
			}
			if (fmiei != null && fmiei.equals("1")) {
				buf.append("and (ts_utente.CDNUT = " + cdnUt + ") ");
			}
			if (foperatore != null && !foperatore.equals("")) {
				if (codVista < 3) // per 1) e 2) il prgSpi e' in ag_agenda
					buf.append(" and (tab.PRGSPI= " + foperatore + ") ");
				else
					// in 3) il prgSpi e' in ag_spi_slot
					buf.append(" and (ag_spi_slot.PRGSPI= " + foperatore + ") ");
			}
			if (fservizio != null && !fservizio.equals("")) {
				buf.append(" and (tab.CODSERVIZIO = '" + fservizio + "') ");
			}
			if (faula != null && !faula.equals("")) {
				buf.append(" and (tab.PRGAMBIENTE = '" + faula + "') ");
			}
			if (fattivi != null && !fattivi.equals("") && codVista == 1) { // per
																			// 2)
																			// la
																			// condizione
																			// e'
																			// implicita
																			// nella
																			// view
				buf.append(" and (de_stato_appuntamento.FLGATTIVO = 'S') ");
			}
			buf.append(")");
		}
		// Debug - Log
		query_totale.append(buf.toString());
		_logger.debug("sil.module.agenda.GiorniVista" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}

}