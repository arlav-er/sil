package it.eng.myportal.rest.geo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.URL;
import it.eng.myportal.utils.Utils;

/**
 * @author Maresta A.
 * 
 *         This code is only a test version. Complete the code changing the mask: 1 byte: 8 pixel
 * 
 */

@Stateless
@Path("rest/geo")
public class GetPoiMask {

	protected static Log log = LogFactory.getLog(GetPoiMask.class);

	@GET
	@Path("getPoiMask")
	@Produces("text/plain; charset=UTF-8")
	public String getPoiMask(@QueryParam("solr_param") String solr_param) {
			
			String baseDominio = ConstantsSingleton.getSolrUrl();
			
			String url = baseDominio + URL.escapeSolr("/core0/select?" + solr_param + "&fq=flg_pacchetto_cresco:N");
			log.debug(url);
			String jsonOfferte = Utils.RawSOLR(url);
			// log.debug(jsonOfferte);
					
			return jsonOfferte;
			
		
	}

	@GET
	@Path("getPoiVacanciesValide")
	@Produces("text/plain; charset=UTF-8")
	public String getPoiVacanciesValide(@QueryParam("solr_param") String solr_param) {

		String baseDominio = ConstantsSingleton.getSolrUrl();

		String url = baseDominio + URL.escapeSolr("/core0/select?" + solr_param + "&fq=flg_pacchetto_cresco:N"
				+ getDataScadenzaPubblicazioneFilter(solr_param));
		log.debug(url);
		String jsonOfferte = Utils.RawSOLR(url);
		// log.debug(jsonOfferte);

		return jsonOfferte;

	}

	private String getDataScadenzaPubblicazioneFilter(String solr_param) {
		final String dataScadenzaPubblicazioneLabel = "data_scadenza_pubblicazione";

		String ret = "";

		/*
		 * Da alcuni chiamanti (es. mappa ricerca vacancy), viene già passato il filtro data_scadenza_pubblicazione, si
		 * verifica quindi se è il caso di aggiungere questo filtro
		 */
		if (solr_param != null && !solr_param.contains(dataScadenzaPubblicazioneLabel)) {
			// Filtro data scadenza: da data attuale a data attuale + 100 anni
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();

			// Data presente: se non è Umbria è domani (logica ripresa da
			// RvRicercaVacancyHome.calcolaFiltroByPrimefaces)
			
			/* UNA VACANCY RISULTA VALIDA E QUINDI NON SCADUTA FINO ALLE ORE 23:59:59 DELLA DATA SCADENZA STESSA
			if (!ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA))
				c.add(Calendar.DATE, +1);
			*/	
			String dataPresente = dateFormat.format(c.getTime());
			// Data futura: +100 anni
			c.add(Calendar.YEAR, 100);
			String dataFutura = dateFormat.format(c.getTime());

			ret = URL.escape(
					"&fq=data_scadenza_pubblicazione:[" + dataPresente + "T00:00:00Z TO " + dataFutura + "T00:00:00Z]");
		}

		return ret;
	}
}
