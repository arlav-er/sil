package it.eng.myportal.rest.suggestion;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.entity.home.PfIdentityDeviceHome;

@Stateless
@Path("rest/usersApp/")
public class UsersAppRestSuggestion {

	@EJB
	transient PfIdentityDeviceHome pfIdentityDeviceHome;

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * Servizio esposto dal REST
	 * 
	 * @param par
	 *            String stringa di ricerca. Sarà poi la classe concreta a deciderne l'utilizzo all'interno della query.
	 * @return String
	 */
	@GET
	@Path("suggestion")
	@Produces("application/json; charset=UTF-8")
	public String suggest(@QueryParam("term") String par, @QueryParam("filter") String filterCodProvincia) {

		JSONArray array = new JSONArray();
		par = normalizeParameter(par);

		try {

			List<Object[]> listUsers = pfIdentityDeviceHome.suggestEmailUsersApp(par, filterCodProvincia);

			if (listUsers == null || listUsers.isEmpty()) {
				// se non ho ottenuto alcun risultato restituisco un oggetto senza 'value' e 'id'
				JSONObject obj = new JSONObject();
				obj.put("id", "");
				obj.put("label", "Nessun dato trovato");
				obj.put("value", "");
				array.put(obj);
			} else {
				// aggiunge gli elementi recuperati al risultato JSON
				for (Object[] element : listUsers) {
					JSONObject obj = createJSON(element);
					array.put(obj);
				}
			}
		} catch (EJBException e1) {
			log.error("Errore durante il recupero dati: " + e1.getMessage());
			array = new JSONArray();
			array.put(buildErrorResponse());
		} catch (JSONException e) {
			log.error("Errore durante la costruzione della risposta: " + e.getMessage());
			array = new JSONArray();
			array.put(buildErrorResponse());
		}
		return array.toString();
	}

	private String normalizeParameter(String par) {
		String trimPar = StringUtils.trim(par);
		if (trimPar != null) {
			return trimPar;
		}
		return "";
	}

	protected JSONObject createJSON(Object[] element) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("id", element[0]);
		obj.put("value", element[1]);
		obj.put("label", element[1]);
		return obj;
	}

	private JSONObject buildErrorResponse() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("id", "");
			obj.put("label", "Errore nel recupero dati");
			obj.put("value", "");
			return obj;
		} catch (JSONException e) {
			log.error("Errore durante la costruzione dell'errore: " + e.getMessage());
			return null;
		}
	}

}
