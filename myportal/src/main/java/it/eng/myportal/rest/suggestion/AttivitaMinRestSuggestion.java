package it.eng.myportal.rest.suggestion;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

import it.eng.myportal.dtos.DeAttivitaMinDTO;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaMinHome;

/**
 * REST Prende in input un termine di ricerca e restituisce la lista dei
 * risultati in formato JSON.
 * 
 * Valutare un helper per la trasformazione in String del risultato
 * 
 * @author Enrico D'Angelo
 * 
 */

@Stateless
@Path("rest/attivitaMin/")
public class AttivitaMinRestSuggestion {

	private Log log = LogFactory.getLog(this.getClass());
	
	@EJB
	transient DeAttivitaMinHome deAttivitaMinHome;

	/**
	 * Servizio esposto dal REST
	 * 
	 * @param par
	 *            String
	 * @return String
	 */
	@GET
	@Path("albero")
	@Produces("application/json; charset=UTF-8")
	public String albero(@QueryParam("key") String par,
			@QueryParam("filter") String filter) {
		// costruisci la lista dei risultati
		JSONArray array = new JSONArray();

		// recupera la lista delle suggestion.
		// operazione delegata alla classe concreta
		List<DeAttivitaMinDTO> list = new ArrayList<DeAttivitaMinDTO>();
		try {
			if (!"0".equals(par)) {
				list = deAttivitaMinHome.findByCodPadre(par);
			} else if (filter != null && !"".equals(filter)) {
				DeAttivitaMinDTO deAttivitaMinDTO = deAttivitaMinHome.findDTOById(filter);
				list.add(deAttivitaMinDTO);
			}
			
			boolean isResEmpty = (list == null) || list.isEmpty();

			// se non ho ottenuto alcun risultato restituisco un oggetto senza
			// 'value' e 'id'
			if (!isResEmpty) {
				// aggiunge tutti i DTO recuperati al risultato JSON
				for (DeAttivitaMinDTO element : list) {
					JSONObject obj = new JSONObject();
					obj.put("key", element.getId());
					obj.put("title", element.getDescrizione().toUpperCase());
					
					if (element.getCodAttivitaDot() == null) {
						obj.put("unselectable", true);
					}
					if (element.getNumeroFigli() > 0) {
						obj.put("isLazy", true);
						obj.put("isFolder", true);
					} else {
						obj.put("isLazy", false);
						obj.put("isFolder", false);
					}
					array.put(obj);
				}
			} else {
				JSONObject obj = new JSONObject();
				obj.put("key", "");
				obj.put("title", "Nessun elemento");
				obj.put("unselectable", true);
				obj.put("isLazy", false);
				obj.put("isFolder", false);
				array.put(obj);
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
		
		return returnString(array);
	}
	
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
	public String suggest(@QueryParam("term") String par, @QueryParam("filter") String filter) {
		// costruisci la lista dei risultati
		JSONArray array = new JSONArray();

		// recupera la lista delle suggestion.
		// operazione delegata alla classe concreta
		List<DeAttivitaMinDTO> list = new ArrayList<DeAttivitaMinDTO>();
		par = normalizeParameter(par);
		try {
			if (filter != null && !filter.isEmpty()) {
				List<DeAttivitaMinDTO> listPadriDeAttivitaMinDTO = deAttivitaMinHome
						.findByCodPadre(filter);

				List<String> listCodPadre = new ArrayList<String>();
				for (DeAttivitaMinDTO deAttivitaMinDTO : listPadriDeAttivitaMinDTO) {
					listCodPadre.add(deAttivitaMinDTO.getId());
				}

				list = deAttivitaMinHome.findByCodPadre(par, listCodPadre);
			}
			
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
				for (DeAttivitaMinDTO element : list) {
					if (element.getCodAttivitaDot() != null) {
						JSONObject obj = createJSON(element);					
						array.put(obj);	
					}
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
	
	protected JSONObject createJSON(DeAttivitaMinDTO element) throws JSONException {		
		JSONObject obj = new JSONObject();
		obj.put("id", element.getId());
		obj.put("value", element.getDescrizione());
		obj.put("label", element.getDescrizione());
		return obj;
	}
	
	private String returnString(JSONArray array) {
		try {
			String tmp = array.toString();
			String ret = new String(tmp.getBytes("UTF-8"),"UTF-8");
			//ret = ret.replaceAll("\u00C0", "A'");
			return ret;
		} catch (UnsupportedEncodingException e) {
			JSONArray tmp = new JSONArray();
			tmp.put(buildErrorResponse());
			return array.toString();
		}
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
