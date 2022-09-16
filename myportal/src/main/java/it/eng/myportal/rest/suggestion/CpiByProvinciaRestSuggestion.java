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

import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;

@Stateless
@Path("rest/cpibyprovincia/")
public class CpiByProvinciaRestSuggestion {

	private Log log = LogFactory.getLog(this.getClass());
	
	@EJB
	transient DeCpiHome deCpiHome;
	
	@GET
	@Path("suggestion")
	@Produces("application/json; charset=UTF-8")
	public String suggest (
			@QueryParam("term") String par, 
			@QueryParam("provincia") String provincia) {
		
		// costruisci la lista dei risultati
		
		JSONArray array = new JSONArray();
		
		// recupera la lista delle suggestion
		// operazione delegata alla classe concreta
		
		List<DeCpiDTO> list;
		String trimmedPar = normalizeParameter(par);
		String trimmedProvincia = normalizeParameter(provincia);
				
		try {
			
			list = deCpiHome.findBySuggestionAndProvincia(trimmedPar, trimmedProvincia);
			
			boolean isResEmpty = (list == null) || list.isEmpty();

			// se non ho ottenuto alcun risultato restituisce un oggetto senza 'value' e 'id'
			if (isResEmpty) {
				JSONObject obj = new JSONObject();
				obj.put("id", "");
				obj.put("label", "Nessun dato trovato");
				obj.put("value", "");
				array.put(obj);
			} else {
				// aggiunge tutti i DTO recuperati al risultato JSON
				for (DeCpiDTO element : list) {
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
	
	protected String normalizeParameter(String par) {
		String trimPar = StringUtils.trimToEmpty(par);
		if (trimPar != null) {
			return trimPar;
		}
		return "";
	}
	
	protected JSONObject createJSON(DeCpiDTO element) throws JSONException {		
		JSONObject obj = new JSONObject();
		obj.put("id", element.getId());
		obj.put("value", element.getDescrizione());
		obj.put("label", element.getDescrizione());
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
