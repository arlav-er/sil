package it.eng.myportal.ws;

import it.eng.myportal.entity.YgAdesione;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.entity.ejb.youthGuarantee.YouthGuaranteeAdesioneEjb;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.entity.home.YgAdesioneStoriaHome;
import it.eng.myportal.entity.home.YgNotificaFailHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoAdesioneMinHome;
import it.eng.myportal.enums.ErroreNotificaCambioStatoAdesioneYGEnum;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.gov.lavoro.servizi.servizicoapAdesioneSet.types.holders.Risposta_setStatoAdesioneYG_TypeEsitoHolder;

import java.util.Date;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.ws.Holder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@org.apache.cxf.interceptor.InInterceptors(interceptors = { "it.eng.myportal.ws.interceptor.PDDBasicAuthYouthGuaranteeInterceptor" })
@WebService(serviceName = "NotificaCambioStatoAdesioneYG")
public class NotificaCambioStatoAdesioneYG implements it.eng.myportal.ws.statoadesione.notifica.ServizicoapWS {
	protected final Log log = LogFactory.getLog(this.getClass());
	private final Integer idPfPrincipalAdmin = 0;

	@EJB
	private YouthGuaranteeAdesioneEjb youthGuaranteeAdesioneEjb;

	@EJB
	private YgAdesioneHome ygAdesioneHome;

	@EJB
	private YgNotificaFailHome ygNotificaFailHome;

	@EJB
	private YgAdesioneStoriaHome ygAdesioneStoriaHome;

	@EJB
	private DeStatoAdesioneMinHome deStatoAdesioneMinHome;

	@EJB
	private DeRegioneHome deRegioneHome;

	@Override
	public void notificaCambioStatoAdesioneYG(String xml, Holder<String> esito, Holder<String> messaggioErrore) {
		log.info("notificaCambioStatoAdesioneYG inizio" );
		log.info("notificaCambioStatoAdesione datiStatoAdesione XML: " + xml);

		/* al ministero si risponde SEMPRE tutto OK! */
		esito.value = "OK";
		messaggioErrore.value = "";

		try {
			youthGuaranteeAdesioneEjb.parseNotificaCambioStatoAdesioneInput(xml);
		} catch (Exception e) {
			/* errore parsing */
			esito.value = "KO";
			messaggioErrore.value = "XML mal formattato";
			
			log.error("notificaCambioStatoAdesione: XML mal formattato\n" + xml + "\n" + e.getMessage());
			ygNotificaFailHome.logErroreParsing(xml, "C", idPfPrincipalAdmin);
			
			log.info("notificaCambioStatoAdesione risposta esito: " + esito.value + " - messaggioErrore: " + messaggioErrore.value);
			return;
		}

		it.eng.myportal.youthGuarantee.notificaStatoAdesione.input.DatiStatoAdesione datiStatoAdesione = youthGuaranteeAdesioneEjb
				.unmarshallDatiStatoAdesioneNotifica(xml);

		String codiceFiscale = datiStatoAdesione.getCodiceFiscale();
		Date dataAdesione = null;
		try {
			dataAdesione = Utils.gregorianDateToDate(datiStatoAdesione.getDataAdesione());
		} catch (Exception e) {			
						
			esito.value = "KO";
			messaggioErrore.value = "Errore nel parsing della data adesione";
			
			String msgErr = "Errore nel parsing della data adesione";

			log.error("notificaCambioStatoAdesione: " + msgErr + "\n" + xml + "\n" + e.getMessage());			
			ygNotificaFailHome.logErroreParsing(xml, "C", idPfPrincipalAdmin);
			e.printStackTrace();
			
			log.info("notificaCambioStatoAdesione risposta esito: " + esito.value + " - messaggioErrore: " + messaggioErrore.value);
			return;
		}
		String codRegioneMin = StringUtils.leftPad(datiStatoAdesione.getRegioneAdesione(), 2, "0");
		DeRegione deRegione = deRegioneHome.findByCodRegioneMin(codRegioneMin);
		String codRegione = deRegione.getCodRegione();
		Date dataStatoAdesione = null;
		try {
			dataStatoAdesione = Utils.gregorianDateToDate(datiStatoAdesione.getDataStatoAdesione());
		} catch (Exception e) {
			
			esito.value = "KO";
			messaggioErrore.value = "Errore nel parsing della data stato adesione";
			
			String msgErr = "Errore nel parsing della data stato adesione";
			log.error("notificaCambioStatoAdesione: " + msgErr + "\n" + xml + "\n" + e.getMessage());
			ygNotificaFailHome.logErroreParsing(xml, "C", idPfPrincipalAdmin);
			
			log.info("notificaCambioStatoAdesione risposta esito: " + esito.value + " - messaggioErrore: " + messaggioErrore.value);
			return;
		}

		YgAdesione adesione = ygAdesioneHome.findByCodiceFiscaleDataRegioneAdesione(codiceFiscale, dataAdesione, codRegione);
		
		if (adesione == null) {
			/* adesione non presente */
			//boolean insFail = true;
			if (("P").equalsIgnoreCase(datiStatoAdesione.getStatoAdesione())) {
				/* notifica di presa in carico... */
				
				// Recupero della regione di lavoro da utilizzare per verificare la provenienza.
				// L'alternativa è l'lpad della costante del codice regione ministeriale, ma non è molto pulito
				DeRegione deRegioneLavoro = deRegioneHome.findById(String.valueOf(ConstantsSingleton.COD_REGIONE));
				
				if (!deRegioneLavoro.getCodMin().equals(codRegioneMin)) {
					/* ...da un'altra regione */
					YgAdesione adesioneInRegione = ygAdesioneHome.findLatestByCodiceFiscaleInRegionePortale(codiceFiscale);
					if (adesioneInRegione != null) {
						/*
						 * annullamento d'ufficio dell'adesione in questa regione:
						 * il lavoratore e' gia' stato preso in carico da un'altra
						 * regione ed è in stato A
						 */
						String codStatoAdesioneMinInReg = adesioneInRegione.getDeStatoAdesioneMin().getCodStatoAdesioneMin();
						if (("A").equalsIgnoreCase(codStatoAdesioneMinInReg)) {
							Risposta_setStatoAdesioneYG_TypeEsitoHolder esitoHolder = new Risposta_setStatoAdesioneYG_TypeEsitoHolder();
							StringHolder messaggioErroreHolder = new StringHolder();
							try {
								youthGuaranteeAdesioneEjb.setStatoAdesioneYG(adesioneInRegione.getCodiceFiscale(),
										Utils.dateToGregorianDate(adesioneInRegione.getDtAdesione()),
										deRegioneLavoro.getCodMin(), "N", esitoHolder, messaggioErroreHolder,
										idPfPrincipalAdmin);
								
								/* storicizzo lo stato dell'adesione */
								ygAdesioneStoriaHome.storicizza(adesioneInRegione, idPfPrincipalAdmin);
						
								/* aggiorno i dati dell'adesione */
								adesioneInRegione.setDeStatoAdesioneMin(deStatoAdesioneMinHome.findById("N"));	
								adesioneInRegione.setDtStatoAdesioneMin(dataStatoAdesione);
								ygAdesioneHome.merge(adesioneInRegione);
								//insFail = false;
								
							} catch (Exception e) {
								log.error("notificaCambioStatoAdesione: errore nell'aggiornamento stato N e storicizzazione adesione idYgAdesione = " + adesioneInRegione.getIdYgAdesione());
								log.error("notificaCambioStatoAdesione: " +e.getMessage());
							}
						}
					}
				}
			}			
					
			esito.value = "OK";
			messaggioErrore.value = "";
			
			//if (insFail) {
				log.error("notificaCambioStatoAdesione: " + ErroreNotificaCambioStatoAdesioneYGEnum.NP.getMessaggio()
						+ "\n" + xml);
				ygNotificaFailHome.logErrore(adesione, datiStatoAdesione, dataStatoAdesione,
						ErroreNotificaCambioStatoAdesioneYGEnum.NP.getCodice(),
						ErroreNotificaCambioStatoAdesioneYGEnum.NP.getMessaggio(), "C", idPfPrincipalAdmin);
				
				log.info("notificaCambioStatoAdesione risposta esito: " + esito.value + " - messaggioErrore: " + messaggioErrore.value);
			//}
			return;
			
		}
		else {				
			/* storicizzo lo stato dell'adesione */
			ygAdesioneStoriaHome.storicizza(adesione, idPfPrincipalAdmin);
	
			/* aggiorno i dati dell'adesione */
			adesione.setDeStatoAdesioneMin(deStatoAdesioneMinHome.findById(datiStatoAdesione.getStatoAdesione()));
			adesione.setDtStatoAdesioneMin(dataStatoAdesione);
			
			ygAdesioneHome.merge(adesione);
		}

		log.info("notificaCambioStatoAdesione risposta esito: " + esito.value + " - messaggioErrore: " + messaggioErrore.value);
		
		log.info("notificaCambioStatoAdesioneYG fine");
	}
}
