package it.eng.myportal.rest;

import it.eng.myportal.entity.home.MsgMessaggioHome;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * REST
 * Prende in input un termine di ricerca e restituisce
 * la lista dei risultati in formato JSON.
 * 
 * 
 * @author Rodi A.
 *
 */
@Stateless
@Path("rest/polling/")
public class Polling {
	
	protected static Log log = LogFactory.getLog(Polling.class);
	
	@EJB
	MsgMessaggioHome msgMessaggioHome;
	

	/**
	 * Servizio esposto dal REST
	 * @param par String
	 * @return String
	 */
	@GET
	@Path("notifications")
	@Produces("application/json; charset=UTF-8")
	public String notifications(@QueryParam("user") String userId) {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".notifications");		
		try {
			Map<String, Integer> nonletti = msgMessaggioHome.findNumMessaggiNonLetti(Integer.parseInt(userId));
			int totale = 0;
			for (String key : nonletti.keySet()) {
				totale += nonletti.get(key);
			}
			JSONObject obj = new JSONObject();
			if (totale > 0) {
				obj.put("status", "new");				
			}
			else {
				obj.put("status", "none");
			}
			obj.put("count", totale);
			
			return obj.toString();
		} catch (JSONException e) {
			log.error("Errore nel recupero delle notifiche.");
			return "{\"status\",\"error\"}";
		} catch (Exception e) {
			log.error("Errore nel recupero delle notifiche.");
			return "{\"status\",\"error\"}";
		} finally {
			jamonMonitor.stop();
		}
	}
	
}
