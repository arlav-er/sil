package it.eng.myportal.ws;

import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WebService(serviceName = "RicezioneVacancy")
public class RicezioneVacancy {

	protected final Log log = LogFactory.getLog(this.getClass());

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	WsEndpointHome wsEndpointHome;

	@WebMethod(operationName = "inserisciVacancySil")
	public String inserisciVacancySil(String username, String password, String xmlVacancy) {
		int retCode = 0;
		try {
			checkCredenziali(username, password);
		} catch (Exception e) {
			log.error("checkUtente " + e);
			return creaMessaggioNuovo("inserisciVacancySil", "500", "Autenticazione fallita");
		}

		if (xmlVacancy == null || "".equals(xmlVacancy)) {
			return creaMessaggioNuovo("inserisciVacancySil", "501", "Dati vacancy nulli");
		}

		InitialContext ic;
		//List<AziendaInfo> aziendeInfoList = null;
		List<PfPrincipal> aziendeInfoPfPrincipalList = null;
		String mailConcat = null;
		StringBuilder listConcatEmail = null;
		try {
			ic = new InitialContext();
			//VaDatiVacancyHome home = (VaDatiVacancyHome) ic.lookup(ConstantsSingleton.JNDI_BASE + "VaDatiVacancyHome");
			//home.insertVacancyFromSIL(xmlVacancy);
			retCode = vaDatiVacancyHome.insertVacancyFromSIL(xmlVacancy);
			if(retCode == -1) { // caso in cui esistono più occorrenze/aziende con lo stesso CF -- ciò avviene solo se sto in RER
				aziendeInfoPfPrincipalList =vaDatiVacancyHome.ottieniEmailListAzInfo(xmlVacancy);
				if(aziendeInfoPfPrincipalList != null && !aziendeInfoPfPrincipalList.isEmpty()) {
					listConcatEmail = new StringBuilder();
					
					for(int i = 0 ; i<= aziendeInfoPfPrincipalList.size()-1; i ++) {
						String email = aziendeInfoPfPrincipalList.get(i).getEmail();
						if(email == null || (email != null && email.isEmpty())) {
							continue;
						}
						listConcatEmail.append(email);
						if(i != aziendeInfoPfPrincipalList.size()-1 ) listConcatEmail.append(", ");
					}
				}
				if(listConcatEmail != null) mailConcat = listConcatEmail.toString();
				return creaMessaggioNuovo("inserisciVacancySil", "111", "Non e' stato possibile pubblicare l'annuncio sul Portale perche' esistono gia' delle aziende con quel codice fiscale ma con indirizzi email diversi: " + mailConcat);
			}
		} catch (Exception e) {
			log.error("inserisciVacancy " + e);
			return creaMessaggioNuovo("inserisciVacancySil", "999", "Errore generico");
		}

		log.debug("XML da SIL -- " + xmlVacancy + " -- ");
		return creaMessaggioNuovo("inserisciVacancySil", "0", "OK");
	}

	@WebMethod(operationName = "inserisciVacancy")
	public String inserisciVacancy(String username, String password, String xmlVacancy) {
		try {
			checkCredenziali(username, password);
		} catch (Exception e) {
			log.error("checkUtente " + e);
			return creaMessaggio("inserisciVacancy", "500", e.getMessage());
		}

		if (xmlVacancy == null || "".equals(xmlVacancy)) {
			return creaMessaggio("inserisciVacancy", "501", "Dati vacancy nulli");
		}

		InitialContext ic;
		try {
			ic = new InitialContext();
			VaDatiVacancyHome home = (VaDatiVacancyHome) ic.lookup(ConstantsSingleton.JNDI_BASE + "VaDatiVacancyHome");
			home.insertVacancyFromSIL(xmlVacancy);

		} catch (Exception e) {
			log.error("inserisciVacancy " + e);
			return creaMessaggio("inserisciVacancy", "999", e.getMessage());
		}

		log.debug("XML da SIL -- " + xmlVacancy + " -- ");
		return creaMessaggio("inserisciVacancy", "0", "OK");
	}

	/**
	 * verifica le credenziali di accesso per il WS di autenticazione
	 * 
	 * da gestire il recupero dei dati TABELLA o file di PROPERTIES
	 * 
	 * @param _login
	 * @param _pwd
	 * @throws Exception
	 */
	private void checkCredenziali(String login, String pwd) throws Exception {
		String user[] = null;
		user = wsEndpointHome.getWebServiceUser(TipoServizio.SIL_RICHIESTAPERSONALE);

		String userLocal = user[0];
		String pwdLocal = user[1];

		if (!login.equals(userLocal)) {
			throw new Exception("Username o Password errati");
		}

		if (!pwd.equals(pwdLocal)) {
			throw new Exception("Username o Password errati");
		}

		/*
		 * if (oggi.compareTo(dataInizioVal) < 0) { throw new Exception("Account non ancora valido"); }
		 * 
		 * if (oggi.compareTo(dataFineVal) > 0) { throw new Exception("Account scaduto"); }
		 */
	}

	private String creaMessaggioNuovo(String operazione, String esito, String msg) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<inserisciVacancy>" + "<esito>" + esito
				+ "</esito><dettaglio>" + msg + "</dettaglio></inserisciVacancy>";
	}

	private String creaMessaggio(String operazione, String esito, String msg) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<Vacancy>" + "<" + operazione + "> " + "<esito ok=\""
				+ esito + "\" dettaglio=\"" + msg + "\"/>" + "</" + operazione + "> " + "</Vacancy>";
	}
}
