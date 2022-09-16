package it.eng.myportal.ws;

import java.util.Date;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.xml.ws.Holder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.DeRegioneDTO;
import it.eng.myportal.dtos.DeStatoAdesioneMinDTO;
import it.eng.myportal.dtos.YgAdesioneDTO;
import it.eng.myportal.entity.ejb.youthGuarantee.YouthGuaranteeAdesioneEjb;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoAdesioneMinHome;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.ws.youthGuarantee.ServizicoapWS;
import it.eng.myportal.ws.youthGuarantee.types.RispostaInvioUtenteYGType;
import it.eng.myportal.youthGuarantee.utenteYG.Utente;
import it.eng.myportal.youthGuarantee.utenteYG.UtenteygType;

@org.apache.cxf.interceptor.InInterceptors(interceptors = { "it.eng.myportal.ws.interceptor.PDDBasicAuthYouthGuaranteeInterceptor" })
@WebService(serviceName = "ServiziUtentiYG")
public class ServiziUtentiYG implements ServizicoapWS {
	protected final Log logger = LogFactory.getLog(this.getClass());

	@EJB
	YouthGuaranteeAdesioneEjb youthGuaranteeAdesioneEjb;

	@EJB
	YgAdesioneHome ygAdesioneHome;

	@EJB
	DeRegioneHome deRegioneHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeStatoAdesioneMinHome deStatoAdesioneMinHome;

	@Override
	public void checkUtenteYG(String utenteYG, javax.xml.ws.Holder<java.lang.String> esito,
			Holder<String> messaggioErrore) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void invioUtenteYG(String utenteYG, javax.xml.ws.Holder<java.lang.String> esito,
			Holder<String> messaggioErrore) {

		logger.info("Chiamata al servizio utenti YG - avvio procedura");
		try {

			// gestione xml ricevuto
			RispostaInvioUtenteYGType rispostaPDD = riceviAdesioneUtente(utenteYG);

			// risposta esito OK
			esito.value = rispostaPDD.getEsito();
			messaggioErrore.value = rispostaPDD.getMessaggioErrore();

		} catch (Exception e) {

			logger.error("Errore generico conversione dell'adesione YG dalla pdd", e);

			RispostaInvioUtenteYGType rispostaPDD = new RispostaInvioUtenteYGType();
			rispostaPDD.setEsito("KO");
			rispostaPDD.setMessaggioErrore("Errore generico");

			esito.value = rispostaPDD.getEsito();
			messaggioErrore.value = rispostaPDD.getMessaggioErrore();

		}

	}

	private RispostaInvioUtenteYGType riceviAdesioneUtente(String datiXml) {

		RispostaInvioUtenteYGType risp = new RispostaInvioUtenteYGType();
		UtenteygType utenteYG = null;

		try {

			utenteYG = youthGuaranteeAdesioneEjb.convertToUtente(datiXml);

			if (utenteYG != null) {

				salvaAdesione(utenteYG.getUtente(), datiXml);
				risp.setEsito("OK");

			} else {

				// possibili errori di validazione
				risp.setEsito("KO");
				risp.setMessaggioErrore("Errore generico");

			}

		} catch (Exception e) {

			logger.error("Errore generico conversione dell'adesione YG dalla pdd", e);
			risp.setEsito("KO");
			risp.setMessaggioErrore("Errore generico");

		}

		return risp;
	}

	private void salvaAdesione(Utente utente, String datiXml) {

		boolean flagSap = false;
		String identificativoSap = utente.getIdentificativoSap();
		String codiceFiscale = utente.getCodiceFiscale();

		if (identificativoSap != null && !"0".equalsIgnoreCase(identificativoSap)) {
			flagSap = true;
		}

		Date currentDate = new Date();
		Date dataAdesione;

		try {
			dataAdesione = Utils.gregorianDateToDate(utente.getDataadesione());
		} catch (Exception e) {
			logger.error(e);
			dataAdesione = null;
		}

		// recupero la regione di provenienza della notifica
		DeRegioneDTO deRegioneDTO = null;
		DeProvinciaDTO deProvinciaDTO = null;
		String codRegioneMin = utente.getRegione();
		if (codRegioneMin != null) {
			codRegioneMin = StringUtils.leftPad(codRegioneMin, 2, '0');
			;
			deRegioneDTO = deRegioneHome.findDTOByCodRegioneMin(codRegioneMin);
		}

		// recupero la provincia di riferimento dal CF chiamando un WS del IX
		// della notifica
		// SOLO PER RER
		/*
		 * if (ConstantsSingleton.COD_REGIONE_RER ==
		 * ConstantsSingleton.COD_REGIONE) { String xmlResponse = ""; String
		 * codProvinciaMaster = ""; try { // interroga l'indice regionale
		 * codProvinciaMaster =
		 * utenteInfoHome.getProvinciaDomicilioIR(codiceFiscale);
		 * logger.info("Risposta IR= provmaster:" + codProvinciaMaster); } catch
		 * (ServiziLavoratoreException e) { logger.error(
		 * "Errore chiamata ws indice regionale per la notifica adesione: identificativo SAP ="
		 * +identificativoSap); }
		 * 
		 * if (codProvinciaMaster != null && !("").equals(codProvinciaMaster)) {
		 * DeProvincia provinciaRif =
		 * deProvinciaHome.findById(codProvinciaMaster); deProvinciaDTO =
		 * deProvinciaHome.toDTO(provinciaRif); } }
		 */

		YgAdesioneDTO newYgAdesioneDTO = new YgAdesioneDTO();
		newYgAdesioneDTO.setCodiceFiscale(codiceFiscale);
		newYgAdesioneDTO.setDtAdesione(dataAdesione);
		newYgAdesioneDTO.setIdentificativoSap(identificativoSap);
		newYgAdesioneDTO.setPfPrincipal(null);
		newYgAdesioneDTO.setCodMonoProv("N");
		newYgAdesioneDTO.setFlgAdesione(true);
		newYgAdesioneDTO.setStrMessWsAdesione(null);
		newYgAdesioneDTO.setFlgSap(flagSap);
		newYgAdesioneDTO.setStrMessWsInvioSap(null);
		newYgAdesioneDTO.setDtmIns(currentDate);
		newYgAdesioneDTO.setDtmMod(currentDate);
		newYgAdesioneDTO.setIdPrincipalIns(0);
		newYgAdesioneDTO.setIdPrincipalMod(0);
		newYgAdesioneDTO.setStrMessWsNotifica(datiXml);
		newYgAdesioneDTO.setDeProvinciaNotifica(deProvinciaDTO);
		newYgAdesioneDTO.setDeRegioneRifNotifica(deRegioneDTO);

		DeStatoAdesioneMinDTO deStatoAdesioneMin = deStatoAdesioneMinHome.findDTOById("A");
		if (deStatoAdesioneMin != null && deStatoAdesioneMin.getId() != null
				&& !"".equalsIgnoreCase(deStatoAdesioneMin.getId())) {
			newYgAdesioneDTO.setDeStatoAdesioneMin(deStatoAdesioneMin);
		}
		newYgAdesioneDTO.setDtStatoAdesioneMin(dataAdesione);

		ygAdesioneHome.persistDTO(newYgAdesioneDTO, 0);
	}

}
