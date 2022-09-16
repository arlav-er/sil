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

import it.eng.myportal.dtos.DeTitoloDTO;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.entity.home.local.ITreeableHome;

/**
 * REST per l'autocomplete dei titoli. Prende in input un termine di ricerca e
 * restituisce la lista dei risultati in formato JSON.
 * 
 * @author Pegoraro A., Rodi A.
 * 
 */
@Stateless
@Path("rest/titoliStudio/")
public class TitoliRestSuggestion extends AbstractAlberoRestSuggestion<DeTitoloDTO> {

	@EJB
	transient DeTitoloHome deTitoloHome;

	@Override
	protected ITreeableHome<DeTitoloDTO> getHome() {
		return deTitoloHome;
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
		List<DeTitoloDTO> list;
		try {
			if (par == null) {
				par = "0";
			}

			list = deTitoloHome.findByCodPadre(par);
			boolean isResEmpty = (list == null) || list.isEmpty();

			// se non ho ottenuto alcun risultato restituisco un oggetto senza
			// 'value' e 'id'
			if (!isResEmpty) {
				// aggiunge tutti i DTO recuperati al risultato JSON
				for (DeTitoloDTO element : list) {
					JSONObject obj = new JSONObject();
					obj.put("key", element.getId());
					obj.put("title", element.getDescrizione());
					obj.put("descrizioneTipoTitolo", element.getDescrizioneTipoTitolo());
					if (element.getNumeroFigli() > 0) {
						obj.put("isLazy", true);
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
		return returnString(array);
	}

	protected JSONObject createJSON(DeTitoloDTO element) throws JSONException {
		JSONObject ret = super.createJSON(element);
		ret.put("descrizioneTipoTitolo", element.getDescrizioneTipoTitolo());
		ret.put("label", element.getDescrizione() + " - " + element.getDescrizioneTipoTitolo());
		return ret;
	}

}
