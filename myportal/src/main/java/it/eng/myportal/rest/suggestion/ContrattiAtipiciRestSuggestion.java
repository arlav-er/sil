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

import it.eng.myportal.dtos.DeAtpContrattoDTO;
import it.eng.myportal.entity.home.decodifiche.DeAtpContrattoHome;

/**
 * REST Prende in input un termine di ricerca e restituisce la lista dei
 * risultati in formato JSON.
 * 
 * Valutare un helper per la trasformazione in String del risultato
 * 
 * @author Enrico
 * 
 */
@Stateless
@Path("rest/contrattiatipici/")
public class ContrattiAtipiciRestSuggestion {

	protected Log log = LogFactory.getLog(this.getClass());

	@EJB
	transient DeAtpContrattoHome deAtpContrattoHome;

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
		List<DeAtpContrattoDTO> list;
		String trimPar = normalizeParameter(par);
		try {
			list = deAtpContrattoHome.findBySuggestion(trimPar);
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
				for (DeAtpContrattoDTO element : list) {
					JSONObject obj = new JSONObject();
					obj.put("id", element.getId());
					obj.put("value", element.getDescrizionePadre() + " - " + element.getDescrizione());
					obj.put("label", element.getDescrizionePadre() + " - " + element.getDescrizione());
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
		List<DeAtpContrattoDTO> list;
		try {
			if (par == null) {
				par = "0";
			}

			list = deAtpContrattoHome.findByCodPadre(par);
			boolean isResEmpty = (list == null) || list.isEmpty();

			// se non ho ottenuto alcun risultato restituisco un oggetto senza
			// 'value' e 'id'
			if (!isResEmpty) {
				// aggiunge tutti i DTO recuperati al risultato JSON
				for (DeAtpContrattoDTO element : list) {
					JSONObject obj = new JSONObject();
					obj.put("key", element.getId());
					obj.put("title", element.getDescrizione());
					if (element.getNumeroFigli() > 0) {
						obj.put("isLazy", true);
						obj.put("isFolder", false);
						obj.put("unselectable", true);
					} else {
						obj.put("isLazy", false);
						obj.put("isFolder", false);
					}
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

	protected String normalizeParameter(String par) {
		String trimPar = StringUtils.trimToEmpty(par);
		if (trimPar != null) {
			return trimPar;
		}
		return "";
	}
}
