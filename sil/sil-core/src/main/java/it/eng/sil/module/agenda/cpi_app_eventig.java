package it.eng.sil.module.agenda;

import java.util.Calendar;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce il sommario giornaliero degli eventi
 * relativi ad un CpI (ristretto agli appuntamenti pubblici se si tratta
 * di un lavoratore oppure di una azienda).
 * 
 * @author: Stefania Orioli
 * 
 */

public class cpi_app_eventig implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_app_eventig.class.getName());

	public cpi_app_eventig() {
	}

	private static final String SELECT_SQL_BASE = "select prgEvento, INITCAP(ag_evento.STRDESCRIZIONEBREVE) as descr_evento "
			+ "from ag_evento where ";

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

		String giorno = (String) req.getAttribute("giornoDB");
		String mese = (String) req.getAttribute("meseDB");
		String anno = (String) req.getAttribute("annoDB");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		// Data Odierna
		Calendar oggi = Calendar.getInstance();
		int oggi_gg = oggi.get(5);
		int oggi_mm = oggi.get(2);
		int oggi_aa = oggi.get(1);

		if ((giorno != null) && (!giorno.equals(""))) {
			buf.append(" to_number(to_char(ag_evento.datevento, 'dd'))=" + giorno + " ");
		} else {
			buf.append(" to_number(to_char(ag_evento.datevento, 'dd'))=" + oggi_gg + " ");
		}

		buf.append(" and ");

		if ((mese != null) && (!mese.equals(""))) {
			buf.append(" to_number(to_char(ag_evento.datevento, 'mm'))=" + mese + " ");
		} else {
			buf.append(" to_number(to_char(ag_evento.datevento, 'mm'))=" + (oggi_mm + 1) + " ");
		}

		buf.append(" and ");

		if ((anno != null) && (!anno.equals(""))) {
			buf.append(" to_number(to_char(ag_evento.datevento, 'yyyy'))=" + anno + " ");
		} else {
			buf.append(" to_number(to_char(ag_evento.datevento, 'yyyy'))=" + oggi_aa + " ");
		}

		/* FILTRI */
		buf.append(" and ag_evento.codcpievento='" + codCpi + "'");
		if (cdnTipoGruppo == 4 || cdnTipoGruppo == 5) { // ==4 cittadino, ==5
														// azienda ....
			buf.append(" and ag_evento.flgpubblico='S' ");
		}

		// Costruisco la query
		query_totale.append(buf.toString());

		// Debug
		_logger.debug("sil.module.agenda.cpi_app_eventig" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}