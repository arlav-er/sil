package it.eng.myportal.rest.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.utils.geocoder.Coordinate;
import it.eng.myportal.utils.geocoder.GeocoderFactory;
import it.eng.myportal.utils.geocoder.IGeocoder;
import it.eng.myportal.utils.geocoder.Indirizzo;

/**
 * Servlet per recuperare tramite ricerca con APACHE SOLR la lista delel vacancy
 * 
 * vengono recuperati anche i vari raggruppamenti per i filtri di II livello
 * 
 * @author
 *
 */
@Deprecated
@Stateless
//@Path("/nocas/app/vacancies")
public class Vacancies {

	private static final String SOLR = "/core0/select/?";
	private static final String FILTRO_TUTTO = "*%3A*";

	protected static Log log = LogFactory.getLog(Vacancies.class);

	@EJB
	DeComuneHome deComuneHome;

	@GET
	@Path("/list/{tokenSecurity}")
	@Produces("application/json")
	public String index(@PathParam("tokenSecurity") String tokenSecurity, @QueryParam("cosa") String cosa,
			@QueryParam("dove") String dove, @QueryParam("token") String token, @QueryParam("start") String start,
			@QueryParam("rows") String rows, @QueryParam("dist") String dist, @QueryParam("lat") String lat,
			@QueryParam("lon") String lon) {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".index");
		try {
			String completeUrl = "/" + ConstantsSingleton.CONTESTO_APP + "/secure/nocas/app/vacancies/list/";
			if (tokenSecurity != null || !("").equalsIgnoreCase(tokenSecurity)) {
				if (Utils.isTokenSecurityCorrectApp(tokenSecurity, completeUrl)) {
					String filtro = "q=" + FILTRO_TUTTO;
					String filtroGeo = "";
					// Creo il filtro a partire dal Cosa e/o Dove
					if (cosa != null && cosa.trim().length() > 0) {

						// la query su solr deve essere del tipo
						// q=cosa:(*azienda* +
						// *progetto*)
						String[] parole = cosa.trim().toLowerCase().split(" ");
						String cosaCercare = "";
						for (int i = 0; i < parole.length; i++) {
							if (i > 0) {
								cosaCercare = cosaCercare + " + ";
							}
							cosaCercare = cosaCercare + "*" + parole[i] + "*";
						}
						filtro = "q=cosa%3A(" + cosaCercare + ")*";
					}
					if (dove != null && !("").equalsIgnoreCase(dove)) {
						List<Coordinate> coordinates = new ArrayList<Coordinate>();
						// aggiungo il filtro per POI solo se trovo il comune
						// selezionato
						DeComune comune = deComuneHome.findByDenominazione(dove);
						if (comune != null) {
							IGeocoder googleGeocoder = GeocoderFactory.getGeocoder();
							try {
								// TODO una merda!
								Indirizzo indirizzo = new Indirizzo("", deComuneHome.toDTO(comune), comune.getCap(),
										comune.getDeProvincia().getTarga());
								coordinates = googleGeocoder.getCoordinates(indirizzo);
								if (coordinates.size() > 0) {
									Coordinate coordinate = coordinates.get(0);
									Double latitudine = coordinate.getLatitudine();
									Double longitudine = coordinate.getLongitudine();

									filtroGeo = filtroGeo + "&fq={!geofilt pt=" + latitudine + "," + longitudine
											+ " sfield=punto d=" + dist + "}";
								}
							} catch (Exception mpe) {
								log.warn("Errore GEOCODER " + mpe.getMessage());
							}
						}

						if (coordinates.size() == 0) {

							String[] paroleDove = dove.trim().toLowerCase().split(" ");
							String doveCercare = "";
							for (int i = 0; i < paroleDove.length; i++) {
								if (i > 0) {
									doveCercare = doveCercare + " + ";
								}
								doveCercare = doveCercare + "*" + paroleDove[i] + "*";
							}

							filtro = dove.length() > 0 ? (filtro + "+AND+dove%3A(" + doveCercare + ")") : filtro;
						}
					} else if (lat != null && !("").equalsIgnoreCase(lat) && lon != null && !("").equalsIgnoreCase(lon)) {
						filtroGeo = filtroGeo + "&fq={!geofilt pt=" + lat + "," + lon + " sfield=punto d=" + dist + "}";
					}

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Calendar c = Calendar.getInstance();
					//c.add(Calendar.DATE, +1);
					String dataInizio = dateFormat.format(c.getTime());
					c.add(Calendar.YEAR, 100);
					String dataFine = dateFormat.format(c.getTime());
					filtro = filtro + "+AND+data_scadenza_pubblicazione:[" + dataInizio + "T00:00:00Z TO " + dataFine
							+ "T00:00:00Z]";

					String baseDominio = ConstantsSingleton.getSolrUrl();
					String url = baseDominio
							+ SOLR
							+ it.eng.myportal.utils.URL.escape(filtro)
							+ "&start="
							+ start
							+ "&rows="
							+ rows
							+ "&indent=on&wt=json&sort=data_modifica%20desc"
							+ "&fl=id_va_dati_vacancy,codmansione,ragione_sociale,attivita_principale,attivita_descrizione_estesa,mansione,orario,settore,data_pubblicazione,data_scadenza_pubblicazione,numero,anno,provenienza,punto_0_coordinate,punto_1_coordinate,descrizione"
							+ it.eng.myportal.utils.URL.escape(filtroGeo);
					log.debug("SOLR URL:" + url);

					String ret = Utils.stringSOLR(url);
					return ret;
				} else {
					// token errato
					log.error("Errore nell'autenticazione token security errato.");
					JSONObject obj = new JSONObject();
					try {
						obj.put("status", "ko");
						obj.put("error", "Token security errato");
					} catch (JSONException e1) {
						log.error(e1);
					}
					return obj.toString();
				}
			} else {
				// token nullo
				log.error("Errore nell'autenticazione token security nullo.");
				JSONObject obj = new JSONObject();
				try {
					obj.put("status", "ko");
					obj.put("error", "Token security nullo");
				} catch (JSONException e1) {
					log.error(e1);
				}
				return obj.toString();
			}
		} finally {
			jamonMonitor.stop();
		}
	}

}
