package it.eng.myportal.rest.suggestion;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ejb.EJBException;
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

import it.eng.myportal.dtos.ISuggestible;
import it.eng.myportal.entity.home.local.ISuggestibleHome;

/**
 * Servizio astratto di suggerimento REST. Prende in input un termine di ricerca
 * e restituisce la lista dei risultati in formato JSON.
 * 
 * @author Rodi
 * 
 */
public abstract class AbstractRestSuggestion<DTO extends ISuggestible> {

	protected Log log = LogFactory.getLog(this.getClass());

	protected abstract ISuggestibleHome<DTO> getHome();

	/**
	 * Servizio esposto dal REST
	 * 
	 * @param par
	 *            String stringa di ricerca. Sarà poi la classe concreta a
	 *            deciderne l'utilizzo all'interno della query.
	 * @return String
	 */
	@GET
	@Path("suggestion")
	@Produces("application/json; charset=UTF-8")
	public String suggest(@QueryParam("term") String par) {
		// costruisci la lista dei risultati
		JSONArray array = new JSONArray();

		// recupera la lista delle suggestion.
		// operazione delegata alla classe concreta
		List<DTO> list;
		String trimPar = normalizeParameter(par);
		try {
			list = getHome().findBySuggestion(trimPar);
			boolean isResEmpty = (list == null) || list.isEmpty();

			// se non ho ottenuto alcun risultato restituisco un oggetto senza
			// 'value' e 'id'
			if (isResEmpty) {
				JSONObject obj = new JSONObject();
				obj.put("id", "");
				obj.put("label", "Nessun dato trovato");
				obj.put("value", "");
				array.put(obj);
			} else {
				// aggiunge tutti i DTO recuperati al risultato JSON
				for (DTO element : list) {
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

	/**
	 * Crea l'oggetto JSON che rappresenta il DTO
	 * 
	 * @param element
	 * @return
	 * @throws JSONException
	 */
	protected JSONObject createJSON(DTO element) throws JSONException {		
		JSONObject obj = new JSONObject();
		obj.put("id", element.getId());
		obj.put("value", element.getDescrizione());
		obj.put("label", element.getDescrizione());
		return obj;
	}

	/**
	 * @param par
	 * @return stringa normalizzata per la ricerca - per default è un trim, la
	 *         classe concreta può estendere/modificare questo comportamento con
	 *         un override del metodo
	 */
	protected String normalizeParameter(String par) {
		String trimPar = StringUtils.trimToEmpty(par);
		if (trimPar != null) {
			return trimPar;
		}
		return "";
	}

	protected JSONObject buildErrorResponse() {
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
	
	/**
	 * Corregge un problema di merda!
	 * @param array
	 * @return
	 */
	protected String returnString(JSONArray array) {
		try {
			String tmp = array.toString();
			String ret = new String(tmp.getBytes(),"UTF8");
			return ret;
		} catch (UnsupportedEncodingException e) {
			JSONArray tmp = new JSONArray();
			tmp.put(buildErrorResponse());
			return array.toString();
		}
	}
}
