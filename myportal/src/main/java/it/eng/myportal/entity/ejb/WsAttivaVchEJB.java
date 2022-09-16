package it.eng.myportal.entity.ejb;

import java.rmi.RemoteException;
import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.VchEntiAccreditatiDTO;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.exception.MyPortalException;
import it.eng.sil.coop.webservices.voucher.AttivaVoucherProxy;

@Stateless
public class WsAttivaVchEJB {
	
	@EJB
	WsEndpointHome wsEndpointHome;
	
	protected static Log log = LogFactory.getLog(WsAttivaVchEJB.class.getName());
	
	private String wsAddress;
	
	public void checkParameterVch(Integer codAttivazione) throws MyPortalException, RemoteException{
		//Controllo struttura del codice di attivazione
		if(codAttivazione.toString().length() < 7){
			log.warn("Errore codice di attivazione: troppo corto, non rispetta le regole di formattazione");
			throw new MyPortalException("Il codice di attivazione è formalmente non corretto");
		}
		String codProvincia = codAttivazione.toString().substring(0, 2);
		int anno = Integer.parseInt(codAttivazione.toString().substring(2, 6));
		if(anno<2016 || anno > Calendar.getInstance().get(Calendar.YEAR)){
			log.warn("Errore codice di attivazione: L'anno non è all'interno del range [2016 - anno corrente]");
			throw new MyPortalException("Il codice di attivazione è formalmente non corretto");
		}
		//Setta indirizzo: nel caso di errore viene lanciata un'eccezione
		wsAddress = wsEndpointHome.getWebServiceAddress(TipoServizio.ATTIVA_VOUCHER, codProvincia);
	}
		
	public String callServiceAttivaVch(UtenteCompletoDTO utenteDTO, VchEntiAccreditatiDTO vchEntiAccreditatiDTO, Integer codAttivazione) throws MyPortalException, RemoteException{
		
		//Parametri di input per la richiesta del servizio
		String cfEnte = vchEntiAccreditatiDTO.getCodiceFiscaleEnte();
		String sedeEnte = vchEntiAccreditatiDTO.getCodSede();
		String cfLavoratore = utenteDTO.getCodiceFiscale();
		String codiceAttivazione = codAttivazione.toString();
		String codProvincia = codAttivazione.toString().substring(0, 2);
		log.info("Parametri di input settati");
		
		//Configurazione endpoint
		log.info("Configurazione dell'endpoint in corso...");
		log.info("Variabili...\n"
				+ "- cfEnte: " + cfEnte + ";\n"
				+ "- sedeEnte: " + sedeEnte + ";\n"
				+ "- cfLavoratore: " + cfLavoratore + ";\n"
				+ "- codiceAttivazione: " + codiceAttivazione + ";\n"
				+ "- codProvincia: " + codProvincia);
//		wsAddress = wsEndpointHome.getWebServiceAddress(TipoServizio.ATTIVA_VOUCHER, codProvincia);
		AttivaVoucherProxy proxy = new AttivaVoucherProxy();
		log.info("E' in corso il settaggio endpoint con il seguente wsAddress: " + wsAddress);
		proxy.setEndpoint(wsAddress);
		
		//Parametri per settare l'endpoint
		try{
			log.info("Invoking attivaVoucher...");
			String attivaVoucher = proxy.attivaVoucher(cfEnte, sedeEnte, cfLavoratore, codiceAttivazione);
			log.info("Risposta del servizio AttivaVoucher = " + attivaVoucher);
			return attivaVoucher;
		}catch(RemoteException e){
			log.info("RemoteException in WsAttivaVchEJB -> checkAttivaVch(): " + e.getMessage());
			throw e;
		}
	}
}
