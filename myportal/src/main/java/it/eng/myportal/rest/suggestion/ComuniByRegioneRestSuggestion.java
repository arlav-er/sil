package it.eng.myportal.rest.suggestion;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.local.ISuggestibleHome;

/**
 * Servlet per l'autocomplete dei titoli.
 * Prende in input un termine di ricerca e restituisce la lista dei risultati in formato JSON.
 * @author Menzi, Maresta
 *
 */
@Stateless
@Path("rest/comunibyregione/")
public class ComuniByRegioneRestSuggestion extends AbstractRestSuggestion<DeComuneDTO>  {
	
	@EJB
	transient DeComuneHome deComuneHome;

	@Override
	protected ISuggestibleHome<DeComuneDTO> getHome() {
		return deComuneHome;
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
	public String suggest(@QueryParam("term") String par) {
		// costruisci la lista dei risultati
		JSONArray array = new JSONArray();

		// recupera la lista delle suggestion.
		// operazione delegata alla classe concreta
		List<DeComuneDTO> list;
		String trimPar = normalizeParameter(par);
		try {
			list = deComuneHome.findBySuggestionRegione(trimPar);
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
				for (DeComuneDTO element : list) {
					JSONObject obj = createJSON(element);					
					array.put(obj);
				}
			}
		} catch (EJBException e1) {
			log.error("Errore durante il recupero dati: "+ e1.getMessage());
			array = new JSONArray();
			array.put(buildErrorResponse());
		} catch (JSONException e) {
			log.error("Errore durante la costruzione della risposta: " + e.getMessage());
			array = new JSONArray();
			array.put(buildErrorResponse());
		}
		return array.toString();
	}
}
