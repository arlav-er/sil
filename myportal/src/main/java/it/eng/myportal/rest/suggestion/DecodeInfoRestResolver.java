package it.eng.myportal.rest.suggestion;

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
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.dtos.IDecodeInfo;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.entity.home.decodifiche.DeAtpContrattoInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneInfoHome;

/**
 * REST Prende in input un termine di ricerca e restituisce la lista dei risultati in formato JSON.
 *
 * Valutare un helper per la trasformazione in String del risultato
 *
 * @author Pegoraro A., Rodi A.
 *
 */
@Stateless
@Path("/rest/resolve")
public class DecodeInfoRestResolver {

	protected Log log = LogFactory.getLog(this.getClass());

	@EJB
	transient DeContrattoInfoHome deContrattoInfoHome;
	
	@EJB
	transient DeAtpContrattoInfoHome deAtpContrattoInfoHome;
	
	@EJB
	transient DeMansioneInfoHome deMansioneInfoHome;

	/**
	 * Servizio esposto dal REST
	 *
	 * @param cod
	 *            codice di cui si richiedono le info.
	 * @return String - JSONObject con id titolo e descrizione
	 */
	@GET
	@Path("/contrattiInfo")
	@Produces("application/json; charset=UTF-8")
	public String resolveContrattiInfo(@QueryParam("term") String cod) {
		return resolveGeneric(cod, deContrattoInfoHome);
	}

	@GET
	@Path("/mansioneInfo")
	@Produces("application/json; charset=UTF-8")
	public String resolveMansioneInfo(@QueryParam("term") String cod) {
		return resolveGeneric(cod, deMansioneInfoHome);
	}
	
	@GET
	@Path("/atpContrattoInfo")
	@Produces("application/json; charset=UTF-8")
	public String resolveAtpContrattiInfo(@QueryParam("term") String cod) {
		return resolveGeneric(cod, deAtpContrattoInfoHome);
	}

	private String resolveGeneric(String cod, AbstractHome<?, ? extends IDecodeInfo, String> genDeInfoHome) {
		// costruisci la lista dei risultati

		// recupera la lista delle suggestion.
		// operazione delegata alla classe concreta
		String normalizedPar = normalizeParameter(cod);
		JSONObject response;
		try {
			IDecodeInfo element = genDeInfoHome.findDTOById(normalizedPar);
			response = buildResponse(element);
		} catch (EJBException e1) {
			log.error("Errore durante il recupero dati: " + e1.getMessage());
			response = buildErrorResponse();
		} catch (JSONException e) {
			log.error("Errore durante la costruzione della risposta: " + e.getMessage());
			response = buildErrorResponse();
		}
		return response.toString();
	}

	private JSONObject buildResponse(IDecodeInfo element) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		boolean isResEmpty = (element == null);

		// se non ho ottenuto alcun risultato restituisco un oggetto senza
		// 'value' e 'id'
		if (isResEmpty) {
			jsonObject.put("id", "");
			jsonObject.put("titolo", "Nessun dato trovato");
			jsonObject.put("descrizione", "Nessun dato trovato");
		} else {
			// aggiunge tutti i DTO recuperati al risultato JSON
			// JSONObject obj = new JSONObject();
			jsonObject.put("id", element.getId());
			jsonObject.put("titolo", element.getTitoloInfo());
			jsonObject.put("descrizione", element.getDescrizione());
		}
		return jsonObject;
	}

	/**
	 * @param par
	 * @return stringa normalizzata per la ricerca - per default è un trim, la classe concreta può estendere/modificare
	 *         questo comportamento con un override del metodo
	 */
	protected String normalizeParameter(String par) {
		return StringUtils.trimToEmpty(par);
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
