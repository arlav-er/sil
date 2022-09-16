package it.eng.sil.module.anag;

import java.util.GregorianCalendar;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;

public class GetLavoratoreAccountDettaglioSil extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(GetLavoratoreAccountDettaglioSil.class.getName());

	public void service(SourceBean request, SourceBean response) {
		SourceBean dettaglioSourceBean;
		try {
			String cdnlavoratore = (String) request.getAttribute("cdnlavoratore");
			dettaglioSourceBean = new SourceBean("ROW");

			SourceBean provincia = getProvincia(cdnlavoratore);
			String descProvincia = provincia.getAttribute("ROW.STRDENOMINAZIONE") != null
					? provincia.getAttribute("ROW.STRDENOMINAZIONE").toString()
					: null;
			String codProvincia = provincia.getAttribute("ROW.CODPROVINCIA") != null
					? provincia.getAttribute("ROW.CODPROVINCIA").toString()
					: null;

			dettaglioSourceBean.setAttribute("strDocumento", "SIL " + descProvincia);
			dettaglioSourceBean.setAttribute("strNumDoc", "1");
			GregorianCalendar datag = new GregorianCalendar();
			datag.get(datag.YEAR);
			dettaglioSourceBean.setAttribute("datScad", "31/12/" + datag.get(datag.YEAR));

			String mail = getMail(cdnlavoratore);
			if (mail != null) {
				dettaglioSourceBean.setAttribute("strmail", mail);
			}

			dettaglioSourceBean.setAttribute("codiceProvinciaSil", codProvincia);

			dettaglioSourceBean.setAttribute("abilitatoServiziAmministrativi", "SI");
			dettaglioSourceBean.setAttribute("abilitato", "Da Attivare");

			response.setAttribute(dettaglioSourceBean);
		} catch (Exception e) {
			_logger.error(e.getMessage(), e);
		} // catch (Exception ex)

	}

	private SourceBean getProvincia(String cdnlavoratore) throws Exception {
		String[] parameter = new String[1];
		parameter[0] = cdnlavoratore;
		SourceBean infoPolo = (SourceBean) com.engiweb.framework.util.QueryExecutor
				.executeQuery("GET_INFO_PROVINCIA_SIL", null, "SELECT", Values.DB_SIL_DATI);
		SourceBean infoLav = (SourceBean) com.engiweb.framework.util.QueryExecutor
				.executeQuery("GET_INFO_PROVINCIA_LAV_SIL", parameter, "SELECT", Values.DB_SIL_DATI);

		String regionePolo = infoPolo.getAttribute("ROW.codRegione") != null
				? infoPolo.getAttribute("ROW.codRegione").toString()
				: "";
		String regioneLav = infoLav.getAttribute("ROW.codRegione") != null
				? infoLav.getAttribute("ROW.codRegione").toString()
				: "";
		if (regioneLav.equals(regionePolo)) {
			return infoLav;
		} else {
			return infoPolo;
		}
	}

	private String getMail(String cdnlavoratore) throws Exception {
		String mail = null;

		String[] parameter = new String[1];
		parameter[0] = cdnlavoratore;
		SourceBean row = (SourceBean) com.engiweb.framework.util.QueryExecutor.executeQuery("GET_MAIL_LAVORATORE_SIL",
				parameter, "SELECT", Values.DB_SIL_DATI);

		if (row != null) {
			mail = row.getAttribute("ROW.stremail") != null ? row.getAttribute("ROW.stremail").toString() : null;
		}

		return mail;
	}

}