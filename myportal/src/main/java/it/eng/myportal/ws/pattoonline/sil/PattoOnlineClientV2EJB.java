package it.eng.myportal.ws.pattoonline.sil;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.PattoSil;
import it.eng.myportal.entity.WsEndpoint;
import it.eng.myportal.entity.home.PattoSilHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.sil.coop.webservices.pattoonlinenew.AccettazionePattoType;
import it.eng.sil.coop.webservices.pattoonlinenew.AccettazionePattoTypeTipoAccettazione;
import it.eng.sil.coop.webservices.pattoonlinenew.EsitoType;
import it.eng.sil.coop.webservices.pattoonlinenew.PattoAccettatoType;
import it.eng.sil.coop.webservices.pattoonlinenew.PattoType;
import it.eng.sil.coop.webservices.pattoonlinenew.TrasmettiPatto;
import it.eng.sil.coop.webservices.pattoonlinenew.TrasmettiPattoServiceLocator;

/**
 *
 */
@Stateless
public class PattoOnlineClientV2EJB {

	@EJB
	private PattoSilHome pattoSilHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	protected final Log log = LogFactory.getLog(this.getClass());

	public EsitoType inviaPattoFirmato(Integer pattoSilId, String codServizi,
			AccettazionePattoTypeTipoAccettazione tipoAccettazione) {

		PattoSil toSend = pattoSilHome.findById(pattoSilId);
		WsEndpoint wsRow = null;
		try {
			wsRow = wsEndpointHome.findByTipoServizioAndProvincia(TipoServizio.SIL_PATTO_FIRMATO_CLI,
					toSend.getDeProvincia().getCodProvincia());
		} catch (Exception e) {
			log.error("GRAVE endpoint non trovato ptonsil per provincia: " + toSend.getDeProvincia().getCodProvincia());
		}

		try {
			
			PattoAccettatoType patto = new PattoAccettatoType();
			//
			PattoType info = new PattoType();
			info.setCodiceFiscale(toSend.getCodFis());
			Calendar dtPatto = Calendar.getInstance();
			dtPatto.setTime(toSend.getDtPatto());
			info.setDataPatto(dtPatto);
			info.setCodServiziAmministrativi(codServizi);
			info.setNumProtocollo(toSend.getNumProtocollo());
			
			BigInteger numAnno = BigInteger.valueOf(toSend.getNumAnnoProtocollo());
			info.setAnnoProtocollo(numAnno);
			info.setCodProvinciaProv(toSend.getDeProvincia().getCodProvincia());
			//
			AccettazionePattoType accettato = new AccettazionePattoType();
			Calendar lCalendar = Calendar.getInstance();
			Date lDate =  toSend.getTsAccettazione();
			lCalendar.setTime(lDate);
			accettato.setDtmAccettazione(lCalendar);
			accettato.setTipoAccettazione(tipoAccettazione);

			patto.setPatto(info);
			patto.setAccettazionePatto(accettato);
			//
			TrasmettiPattoServiceLocator locatorP = new TrasmettiPattoServiceLocator();
			locatorP.setTrasmettiPattoEndpointAddress(wsRow.getAddress());
			TrasmettiPatto serviceP = locatorP.getTrasmettiPatto();
			EsitoType esitoP = serviceP.aggiornaPatto(patto);
			 
 
			return esitoP;
		} catch (java.lang.Exception ex) {
			log.error("ERRORE inviaPattoFirmato(): " + ex.getMessage());
			throw new RuntimeException(ex);
		}

	}

	 
	/*
	 * @Override public Class<TrasmettiPatto> getServiceClass() { return TrasmettiPatto.class; }
	 */
}
