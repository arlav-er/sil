package it.eng.sil.coop.webservices.wsproxy.servsocwsproxy;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;

public class Client extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Client.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {

		String path = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "classes"
				+ File.separator + "InfoSilPerServSoc.properties";
		File f = new File(path);
		Properties listaProperties = new Properties();
		listaProperties.load(new FileInputStream(f));

		String usernameProp = listaProperties.getProperty("CLIENTUSERNAME");
		String passwordProp = listaProperties.getProperty("CLIENTPASSWORD");

		String codCom = (String) request.getAttribute("codcomune");
		String cf = (String) request.getAttribute("strCodiceFiscale");
		String servizio = (String) request.getAttribute("servizio");
		BigDecimal operatore = (BigDecimal) request.getAttribute("operatore");

		SourceBean urlInfo = (SourceBean) QueryExecutor.executeQuery("GET_INFO_INVOCAZIONE_SERVIZI",
				new Object[] { codCom, "WS_SERVIZI_SOCIALI_" + servizio + "_" + codCom, operatore }, "SELECT",
				Values.DB_SIL_DATI);

		if (!urlInfo.containsAttribute("ROW.STRURL")) {
			response.setAttribute("ERRNUM", 23);
			response.setAttribute("ERRDESCRIPTION", "Servizio non presente per il comune di residenza");

			return;
		}

		String _url = (String) urlInfo.getAttribute("ROW.STRURL");
		Boolean _flagPoloAttivo = ((String) urlInfo.getAttribute("ROW.FLGPOLOATTIVO")).equals("S") ? true : false;
		String _operatore = (String) urlInfo.getAttribute("ROW.STRNOME") + " "
				+ (String) urlInfo.getAttribute("ROW.STRCOGNOME");

		if (!_flagPoloAttivo) {
			response.setAttribute("ERRNUM", 21);
			response.setAttribute("ERRDESCRIPTION", "Servizio non attivo per il comune di residenza");

			return;
		}

		WSLegacyProxy wsP = new WSLegacyProxy(_url);

		TipoSistema[] sys = new TipoSistema[1];
		sys[0] = new TipoSistema(servizio);

		Richiesta ric = new Richiesta();
		ric.setSistemi(sys);

		ric.setUser(usernameProp);
		ric.setPassword(passwordProp);

		ric.setOperatore(_operatore);
		ric.setSoggettoCF(cf);

		Risposta risp = new Risposta();

		try {
			risp = wsP.getInfo(ric);
		} catch (RemoteException re) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Client:service:" + servizio, re);
			response.setAttribute("ERRNUM", 22);
			response.setAttribute("ERRDESCRIPTION", "Errore nell'invocazione del servizio");
			return;
		}

		Esito e = new Esito();

		e = risp.getEsito();

		if (e.getErrNum() != 0) {
			response.setAttribute("ERRNUM", e.getErrNum());
			response.setAttribute("ERRDESCRIPTION", e.getErrDescription());
			return;
		}

		Servizio[] s;
		if (risp.getServizi() != null) {
			s = new Servizio[risp.getServizi().length];
			s = risp.getServizi();
		} else
			return;

		SourceBean response_rows = new SourceBean("ROWS");

		if (servizio.equals("OSSERVATORIO")) {
			for (int i = 0; i < risp.getServizi().length; i++) {
				DatiOsservatorio o = new DatiOsservatorio();
				o = s[i].getOSSERVATORIO();

				SourceBean response_row = new SourceBean("ROW");

				if (o.getDataContatto() != null)
					response_row.setAttribute("O_DATACONTATTO", calendarToString(o.getDataContatto()));
				if (o.getLuogo() != null)
					response_row.setAttribute("O_LUOGO", o.getLuogo());
				if (o.getAreaBisogno() != null)
					response_row.setAttribute("O_AREABISOGNO", o.getAreaBisogno());
				if (o.getTipoRichiesta() != null)
					response_row.setAttribute("O_TIPORICHIESTA", o.getTipoRichiesta());
				if (o.getInviatoA() != null)
					response_row.setAttribute("O_INVIATOA", o.getInviatoA());
				if (o.getRelazione() != null)
					response_row.setAttribute("O_RELAZIONE", o.getRelazione());
				if (o.getIdCriticita() != null)
					response_row.setAttribute("O_IDCRITICITA", o.getIdCriticita());
				if (o.getCriticita() != null)
					response_row.setAttribute("O_CRITICITA", o.getCriticita());

				response_rows.setAttribute(response_row);
			}
			response.setAttribute(response_rows);
			return;
		}
		if (servizio.equals("GARSIA")) {
			for (int i = 0; i < risp.getServizi().length; i++) {
				DatiGarsia ga = new DatiGarsia();
				ga = s[i].getGARSIA();

				SourceBean response_row = new SourceBean("ROW");

				if (ga.getServizio() != null)
					response_row.setAttribute("GA_SERVIZIO", ga.getServizio());
				if (ga.getEsito() != null)
					response_row.setAttribute("GA_ESITO", ga.getEsito());
				if (ga.getOperatoreSST() != null)
					response_row.setAttribute("GA_OPERATORESST", ga.getOperatoreSST());
				if (ga.getOperatoreUVG() != null)
					response_row.setAttribute("GA_OPERATOREUVG", ga.getOperatoreUVG());
				if (ga.getDataEsito() != null)
					response_row.setAttribute("GA_DATAESITO", calendarToString(ga.getDataEsito()));
				if (ga.getDataValutazioneRC() != null)
					response_row.setAttribute("GA_DATAVALUTAZIONERC", calendarToString(ga.getDataValutazioneRC()));
				if (ga.getDataValutazioneUVG() != null)
					response_row.setAttribute("GA_DATAVALUTAZIONEUVG", calendarToString(ga.getDataValutazioneUVG()));
				if (ga.getRelazione() != null)
					response_row.setAttribute("GA_RELAZIONE", ga.getRelazione());
				if (ga.getRelazione() != null)
					response_row.setAttribute("GA_IDCRITICITA", ga.getIdCriticita());
				if (ga.getCriticita() != null)
					response_row.setAttribute("GA_CRITICITA", ga.getCriticita());

				response_rows.setAttribute(response_row);
			}
			response.setAttribute(response_rows);
			return;
		}
		if (servizio.equals("SOSIA")) {
			for (int i = 0; i < risp.getServizi().length; i++) {
				DatiSosia so = new DatiSosia();
				so = s[i].getSOSIA();

				SourceBean response_row = new SourceBean("ROW");

				response_row.setAttribute("S_CODICESERVIZIO", so.getCodiceServizio());
				if (so.getServizio() != null)
					response_row.setAttribute("S_SERVIZIO", so.getServizio());
				response_row.setAttribute("S_CODICESOTTOSERVIZIO", so.getCodiceSottoServizio());
				if (so.getSottoServizio() != null)
					response_row.setAttribute("S_SOTTOSERVIZIO", so.getSottoServizio());
				response_row.setAttribute("S_CODICEISTITUTO", so.getCodiceIstituto());
				if (so.getIstituto() != null)
					response_row.setAttribute("S_ISTITUTO", so.getIstituto());
				if (so.getDecorrenza() != null)
					response_row.setAttribute("S_DECORRENZA", calendarToString(so.getDecorrenza()));
				if (so.getScadenza() != null)
					response_row.setAttribute("S_SCADENZA", calendarToString(so.getScadenza()));
				if (so.getRelazione() != null)
					response_row.setAttribute("S_RELAZIONE", so.getRelazione());
				if (so.getIdCriticita() != null)
					response_row.setAttribute("S_IDCRITICITA", so.getIdCriticita());
				if (so.getCriticita() != null)
					response_row.setAttribute("S_CRITICITA", so.getCriticita());

				response_rows.setAttribute(response_row);
			}
			response.setAttribute(response_rows);
			return;
		}
		if (servizio.equals("GRADUS")) {
			for (int i = 0; i < risp.getServizi().length; i++) {
				DatiGradus gr = new DatiGradus();
				gr = s[i].getGRADUS();

				SourceBean response_row = new SourceBean("ROW");

				response_row.setAttribute("GR_CODICEDOMANDA", gr.getCodiceDomanda());
				response_row.setAttribute("GR_ANNODOMANDA", gr.getAnnoDomanda());
				response_row.setAttribute("GR_PROGRESSIVOREVISIONE", gr.getProgressivoRevisione());
				response_row.setAttribute("GR_TIPOREVISIONE", gr.getTipoRevisione());
				response_row.setAttribute("GR_CODICENODOPRESENTAZIONE", gr.getCodiceNodoPresentazione());
				response_row.setAttribute("GR_CODICEGRADUATORIA", gr.getCodiceGraduatoria());
				if (gr.getGraduatoria() != null)
					response_row.setAttribute("GR_GRADUATORIA", gr.getGraduatoria());
				response_row.setAttribute("GR_CODICESERVIZIO", gr.getCodiceServizio());
				if (gr.getServizio() != null)
					response_row.setAttribute("GR_SERVIZIO", gr.getServizio());
				if (gr.getStato() != null)
					response_row.setAttribute("GR_STATO", gr.getStato());
				if (gr.getDataPresentazione() != null)
					response_row.setAttribute("GR_DATAPRESENTAZIONE", calendarToString(gr.getDataPresentazione()));
				if (gr.getRelazione() != null)
					response_row.setAttribute("GR_RELAZIONE", gr.getRelazione());
				if (gr.getIdCriticita() != null)
					response_row.setAttribute("GR_IDCRITICITA", gr.getIdCriticita());
				if (gr.getCriticita() != null)
					response_row.setAttribute("GR_CRITICITA", gr.getCriticita());

				response_rows.setAttribute(response_row);
			}
			response.setAttribute(response_rows);
			return;
		}
	}

	public String calendarToString(Calendar cal) {
		String strdate = null;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (cal != null) {
			strdate = sdf.format(cal.getTime());
		} else
			return "";

		return strdate;
	}
}