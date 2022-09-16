package it.eng.myportal.rest.suggestion;

import java.util.List;

import javax.ejb.EJBException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.dtos.ISuggestible;
import it.eng.myportal.dtos.ITreeable;
import it.eng.myportal.entity.home.local.ITreeableHome;

/**
 * Servizio astratto di albero REST.
 * 
 * @author Rodi
 * 
 */
public abstract class AbstractAlberoRestSuggestion<DTO extends ITreeable> extends AbstractRestSuggestion<DTO> {

	protected abstract ITreeableHome<DTO> getHome();

	/**
	 * Servizio esposto dal REST
	 * @param par String
	 * @return String
	 */
	@GET
	@Path("albero")
	@Produces("application/json; charset=UTF-8")
	public String albero(@QueryParam("key") String par) {
		// costruisci la lista dei risultati
		JSONArray array = new JSONArray(); 
		
		// recupera la lista delle suggestion. 
		// operazione delegata alla classe concreta
		List<DTO> list;
		try {
			if (par == null) {
				par = "0";
			}
			
			list = getHome().findByCodPadre(par);
			boolean isResEmpty = (list == null) || list.isEmpty();
			
			//se non ho ottenuto alcun risultato restituisco un oggetto senza 'value' e 'id'
			if (!isResEmpty) {			
				// aggiunge tutti i DTO recuperati al risultato JSON
				for (ISuggestible element : list) { 
					JSONObject obj = new JSONObject();		
					obj.put("key", element.getId());
					obj.put("title", element.getDescrizione());
					obj.put("isLazy", isLazy());					
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
	
	protected boolean isLazy() {
		return true;
	}
}
