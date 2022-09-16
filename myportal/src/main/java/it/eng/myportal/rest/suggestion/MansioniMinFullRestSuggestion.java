package it.eng.myportal.rest.suggestion;

import java.util.ArrayList;
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

import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.local.ITreeableHome;

/**
 * REST Prende in input un termine di ricerca e restituisce la lista dei
 * risultati in formato JSON.
 * Ritorna tutti gli elementi della tabella
 * 
 * Valutare un helper per la trasformazione in String del risultato
 * 
 * @author Enrico D'Angelo
 * 
 */

@Stateless
@Path("rest/mansioneMinFull/")
public class MansioniMinFullRestSuggestion extends AbstractRestSuggestion<DeMansioneMinDTO> {

	@EJB
	transient DeMansioneMinHome deMansioneMinHome;

	@Override
	protected ITreeableHome<DeMansioneMinDTO> getHome() {
		return deMansioneMinHome;
	}

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
	public String albero(@QueryParam("key") String par) {
		// costruisci la lista dei risultati
		JSONArray array = new JSONArray();

		// recupera la lista delle suggestion.
		// operazione delegata alla classe concreta
		List<DeMansioneMinDTO> list = new ArrayList<DeMansioneMinDTO>();
		try {
			if (par.equals("0")) {
				par = "";
			}
			list = deMansioneMinHome.findByCodPadre(par);
			
			boolean isResEmpty = (list == null) || list.isEmpty();

			// se non ho ottenuto alcun risultato restituisco un oggetto senza
			// 'value' e 'id'
			if (!isResEmpty) {
				// aggiunge tutti i DTO recuperati al risultato JSON
				for (DeMansioneMinDTO element : list) {
					JSONObject obj = new JSONObject();
					obj.put("key", element.getId());
					obj.put("title", element.getDescrizione());
					
					if (element.getCodMansioneDot() == null) {
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

}
