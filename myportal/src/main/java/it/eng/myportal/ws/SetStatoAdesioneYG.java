package it.eng.myportal.ws;

import it.eng.myportal.entity.ejb.youthGuarantee.YouthGuaranteeAdesioneEjb;
import it.gov.lavoro.servizi.servizicoapAdesioneSet.types.holders.Risposta_setStatoAdesioneYG_TypeEsitoHolder;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.ws.Holder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WebService(serviceName = "SetStatoAdesioneYG")
public class SetStatoAdesioneYG implements it.eng.myportal.ws.statoadesione.set.ServizicoapWS {

	protected final Log log = LogFactory.getLog(this.getClass());

	private final Integer idPfPrincipalAdmin = 0;

	@EJB
	private YouthGuaranteeAdesioneEjb youthGuaranteeAdesioneEjb;

	@Override
	public void setStatoAdesioneYG(String datiStatoAdesione, Holder<java.lang.String> esito,
			Holder<java.lang.String> messaggioErrore) {
		log.info("inizio setStatoAdesioneYG MyPortal endpoint: datiStatoAdesione =\n" + datiStatoAdesione);

		it.gov.lavoro.servizi.servizicoapAdesioneSet.types.holders.Risposta_setStatoAdesioneYG_TypeEsitoHolder esitoHolder = new Risposta_setStatoAdesioneYG_TypeEsitoHolder();
		StringHolder messaggioErroreHolder = new StringHolder();
		youthGuaranteeAdesioneEjb.setStatoAdesioneYG(datiStatoAdesione, esitoHolder, messaggioErroreHolder, idPfPrincipalAdmin);

		/* riporto l'esito ricevuto */
		messaggioErrore.value = messaggioErroreHolder.value;
		if (esitoHolder.value != null) {
			esito.value = esitoHolder.value.getValue();    
		}
		else {
			esito.value = "KO";
		}
		
		log.info("fine setStatoAdesioneYG MyPortal endpoint: datiStatoAdesione =\n" + datiStatoAdesione);
	}
}
