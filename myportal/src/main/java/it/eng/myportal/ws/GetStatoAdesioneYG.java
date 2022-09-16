package it.eng.myportal.ws;

import java.util.GregorianCalendar;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.rpc.holders.CalendarHolder;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.ws.Holder;

import org.apache.axis.holders.DateHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.ejb.youthGuarantee.YouthGuaranteeAdesioneEjb;
import it.eng.myportal.utils.Utils;
import it.gov.lavoro.servizi.servizicoapAdesioneGet.types.holders.Risposta_getStatoAdesioneYG_TypeEsitoHolder;

@WebService(name = "GetStatoAdesioneYG",
	portName = "GetStatoAdesioneYG",
	serviceName = "GetStatoAdesioneYG",  
	targetNamespace = "http://ws.myportal.eng.it/")  
public class GetStatoAdesioneYG implements it.eng.myportal.ws.statoadesione.get.ServizicoapWS {

	protected final Log log = LogFactory.getLog(this.getClass());

	private final Integer idPfPrincipalAdmin = 0;

	@EJB
	private YouthGuaranteeAdesioneEjb youthGuaranteeAdesioneEjb;

	@Override
	public void getStatoAdesioneYG(String datiStatoAdesione, Holder<String> esito, Holder<String> messaggioErrore,
			Holder<XMLGregorianCalendar> dataAdesione, Holder<String> statoAdesione,
			Holder<XMLGregorianCalendar> dataStatoAdesione) {
		log.info("inizio getStatoAdesioneYG MyPortal endpoint: datiStatoAdesione =\n" + datiStatoAdesione);

		it.gov.lavoro.servizi.servizicoapAdesioneGet.types.holders.Risposta_getStatoAdesioneYG_TypeEsitoHolder esitoHolder = new Risposta_getStatoAdesioneYG_TypeEsitoHolder();
		StringHolder messaggioErroreHolder = new StringHolder();
		StringHolder statoAdesioneHolder = new StringHolder();
		DateHolder dataAdesioneHolder = new DateHolder();
		CalendarHolder dataStatoAdesioneHolder = new CalendarHolder();
		youthGuaranteeAdesioneEjb.getStatoAdesioneYG(datiStatoAdesione, esitoHolder, messaggioErroreHolder,
				statoAdesioneHolder, dataAdesioneHolder, dataStatoAdesioneHolder, idPfPrincipalAdmin);

		/* riporto l'esito ricevuto */
		try {
			esito.value = esitoHolder.value.getValue();
			messaggioErrore.value = messaggioErroreHolder.value;
			if (dataAdesioneHolder.value != null) {
				dataAdesione.value = Utils.dateToGregorianDate(dataAdesioneHolder.value);
			}
			statoAdesione.value = statoAdesioneHolder.value;
			if (dataStatoAdesioneHolder.value != null) {
				GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
				gc.setTime(dataStatoAdesioneHolder.value.getTime());
				dataStatoAdesione.value = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
			}
		} catch (Exception e) {
			log.error("Non dovrebbe mai accadere, tutti gli errori devono essere trattati nella chiamata a youthGuaranteeAdesioneEjb.getStatoAdesioneYG(): " + e.getMessage());
		}

		log.info("fine getStatoAdesioneYG MyPortal endpoint: datiStatoAdesione =\n" + datiStatoAdesione);
	}
}
