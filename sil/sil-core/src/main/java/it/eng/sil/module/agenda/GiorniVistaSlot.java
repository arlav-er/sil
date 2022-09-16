package it.eng.sil.module.agenda;

import java.util.Calendar;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce i giorni da evidenziare nella gestione degli slot
 * relativi al mese e all'anno 
 * (presenti come parametri nella request, oppure mese/anno correnti)
 * 
 * @author: Stefania Orioli
 */

public class GiorniVistaSlot implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GiorniVistaSlot.class.getName());

	public GiorniVistaSlot() {
	}

	private static final String SELECT_SQL_BASE = "select * " + "from vw_ag_slot where vw_ag_slot.CODTIPOVISTA=";

	private static int mapping_mesiDB[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		int cdnUt = user.getCodut();
		int cdnTipoGruppo = user.getCdnTipoGruppo();
		String codCpi;
		if (cdnTipoGruppo == 1) {
			codCpi = user.getCodRif();
		} else {
			codCpi = req.getAttribute("agenda_codCpi").toString();
		}

		String cod_vista = (String) req.getAttribute("cod_vista");
		String mese = (String) req.getAttribute("mese");
		String anno = (String) req.getAttribute("anno");
		// Savino 16/08/05 - applicazione dei filtri attivi
		String fmiei = (String) sessionContainer.getAttribute("slot_FMiei");
		String foperatore = (String) sessionContainer.getAttribute("slot_FOperatore");
		String fservizio = (String) sessionContainer.getAttribute("slot_FServizio");
		String faula = (String) sessionContainer.getAttribute("slot_FAula");

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

		if ((codCpi != null) && (!codCpi.equals(""))) {
			buf.append(" and (codcpi='" + codCpi + "') ");
		}

		// applicazione dei filtri attivi
		if ((cod_vista != null && !cod_vista.equals(""))
				&& ((fmiei != null && fmiei.equals("1")) || (foperatore != null && !foperatore.equals(""))
						|| (fservizio != null && !fservizio.equals("")) || (faula != null && !faula.equals("")))) {
			// i filtri sulla ricerca sono attivi: bisogna aggiungere la exists
			buf.append(" and exists (");
			buf.append(" select 1 from ag_slot, ag_spi_slot, ts_utente where ag_slot.codcpi=vw_ag_slot.codcpi and ");
			buf.append(" to_date(to_char(ag_slot.dtmdataora, 'dd/mm/yyyy'), 'dd/mm/yyyy') = vw_ag_slot.datdata ");
			buf.append(" and ag_slot.prgSlot = ag_spi_slot.prgSlot (+)");
			buf.append(" and ag_spi_slot.PRGSPI=ts_utente.PRGSPI (+)");
			if (fmiei != null && fmiei.equals("1")) {
				buf.append("and (ts_utente.CDNUT = " + cdnUt + ") ");
			}
			if (foperatore != null && !foperatore.equals("")) {
				buf.append(" and (ag_spi_slot.PRGSPI= " + foperatore + ") ");
			}
			if (fservizio != null && !fservizio.equals("")) {
				buf.append(" and (ag_slot.CODSERVIZIO = '" + fservizio + "') ");
			}
			if (faula != null && !faula.equals("")) {
				buf.append(" and (ag_slot.PRGAMBIENTE = '" + faula + "') ");
			}
			buf.append(")");
		}

		// Debug - Log
		query_totale.append(buf.toString());
		_logger.debug("sil.module.agenda.GiorniVistaSlot" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}

}