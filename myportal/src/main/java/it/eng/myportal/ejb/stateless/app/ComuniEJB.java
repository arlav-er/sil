package it.eng.myportal.ejb.stateless.app;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.exception.GenericException;
import it.eng.myportal.rest.app.exception.UserNotFoundException;

@Stateless
public class ComuniEJB {

	public static final String CODICE_COMUNE = "codice_comune";
	public static final String DENOMINAZIONE = "denominazione";
	public static final String TARGA = "targa";

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private DeComuneHome deComuneHome;

	public String getComuni(String username, String denominazione, String soloValidi) throws AppEjbException {
		String ret = null;

		try {
			if (pfPrincipalHome.exists(username)) {
				List<DeComune> listComuni = null;
				if ("N".equals(soloValidi)) {
					listComuni = deComuneHome.findComuneAutocomplete(denominazione);
				} else {
					listComuni = deComuneHome.findComuneValidiAutocomplete(denominazione);
				}

				if (listComuni != null && !listComuni.isEmpty()) {
					JSONArray jsonArray = new JSONArray();
					for (DeComune deComune : listComuni) {
						jsonArray.put(toJsonObject(deComune));
					}
					ret = jsonArray.toString();
				}
			} else {
				throw new UserNotFoundException(username);
			}
		} catch (JSONException e) {
			throw new GenericException("Errore durante la costruzione dell'oggetto");
		} catch (Exception e) {
			throw new GenericException("Errore durante il recuper dei comuni");
		}
		return ret;
	}

	public JSONObject toJsonObject(DeComune comune) throws JSONException {
		JSONObject obj = new JSONObject();

		obj.put(CODICE_COMUNE, comune.getCodCom());
		obj.put(DENOMINAZIONE, comune.getDenominazione());

		String targa = null;
		if (comune.getDeProvincia() != null)
			targa = comune.getDeProvincia().getTarga();
		obj.put(TARGA, targa);

		return obj;
	}
}
