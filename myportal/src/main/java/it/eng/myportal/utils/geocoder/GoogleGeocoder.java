package it.eng.myportal.utils.geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * Invoca i servizi di google per avere le coordinate dll'indirizzo passato. Se
 * si tratta di un indirizzo all'estero usa solo la denominazione del comune
 * come indirizzo, nel caso di indirizzi all'estero infatti il comune
 * corrisponde al nome dello stato estero.
 * 
 * @author enrico
 * 
 */
public class GoogleGeocoder implements IGeocoder {

	protected Log log = LogFactory.getLog(this.getClass());
	private HttpClient httpClient = null;

	// old url deprecated API GOOGLE http://maps.google.com/maps/geo
	// private final static String URL_GOOGLE =
	// "https://maps.googleapis.com/maps/api/geocode/json";

	public GoogleGeocoder() {
		httpClient = new HttpClient();
		HttpConnectionManagerParams connectionManagerParams = httpClient.getHttpConnectionManager().getParams();
		connectionManagerParams.setConnectionTimeout(5000);
		
		if (ConstantsSingleton.Proxy.ACTIVE) {
			if (ConstantsSingleton.Proxy.USE_AUTHENTICATION) {
				String geocoderUsername = ConstantsSingleton.Proxy.USERNAME;
				String geocoderPassword = new String(ConstantsSingleton.Proxy.PASSWORD);
				Credentials defaultcreds = new UsernamePasswordCredentials(geocoderUsername, geocoderPassword);
				httpClient.getState().setProxyCredentials(AuthScope.ANY, defaultcreds);
			}
			httpClient.getHostConfiguration().setProxy(ConstantsSingleton.Proxy.ADDRESS, ConstantsSingleton.Proxy.PORT);
		}
	}

	public List<Coordinate> getCoordinates(Indirizzo indirizzo) throws MyPortalException {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();

		JSONArray list;
		JSONObject json;
		try {
			json = getGeocoder(indirizzo.getIndirizzoFormattato(), indirizzo.getDeComuneDTO().isStato());
			String codice = json.getString("status");
			if ("OK".equalsIgnoreCase(codice)) {
				list = json.getJSONArray("results");
				coordinates = getPointCoordinates(list, indirizzo);
				// se non ho trovato nulla effettuo la ricerca solamente per
				// COMUNE
				if (coordinates.size() < 1) {
					json = getGeocoder(indirizzo.getIndirizzoShort(), indirizzo.getDeComuneDTO().isStato());
					codice = json.getString("status");
					if ("OK".equalsIgnoreCase(codice)) {
						list = json.getJSONArray("results");
						coordinates = getPointCoordinates(list, indirizzo);
					}
				}
			} else {
				log.warn("Errore nella geolocalizzazione. Status code: " + codice);
				json = getGeocoder(indirizzo.getIndirizzoShort(), indirizzo.getDeComuneDTO().isStato());
				codice = json.getString("status");
				if ("OK".equalsIgnoreCase(codice)) {
					list = json.getJSONArray("results");
					coordinates = getPointCoordinates(list, indirizzo);
				}
			}
		} catch (JSONException e2) {
			throw new MyPortalException("geocoder.error", e2);
		}

		return coordinates;
	}

	public List<Coordinate> getPointCoordinates(JSONArray listIndirizzi, Indirizzo indirizzo) throws JSONException {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();

		// TODO implementare un controllo migliore passando al geocoder anche il
		// codice internazionale dello stato
		/*
		 * se l'indirizzo e' in uno stato estero restituisco le prime coordinate
		 * ottenute
		 */
		if (indirizzo.getDeComuneDTO().isStato()) {
			if (listIndirizzi.length() > 0) {
				JSONObject obj = listIndirizzi.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
				coordinates = getLatLong(obj);
				return coordinates;
			}
		}

		// verifico prima se esiste un record per CAP
		for (int j = 0; j < listIndirizzi.length(); j++) {
			JSONArray arrAddress = listIndirizzi.getJSONObject(j).getJSONArray("address_components");

			/*
			 * dai risultati ottenuti si controlla se il CAP esiste ed è quello che si sta
			 * cercando, nel caso non sia presente viene controllato il nome della città
			 * trovata
			 */
			String strCapAddress = null;
			String strLocalityAddress = null;
			for (int k = 0; k < arrAddress.length(); k++) {
				String strObjAddress = (String) arrAddress.getJSONObject(k).getJSONArray("types").get(0);
				if (("postal_code").equalsIgnoreCase(strObjAddress)) {
					strCapAddress = (String) arrAddress.getJSONObject(k).getString("short_name");
				}
				if (("administrative_area_level_3").equalsIgnoreCase(strObjAddress)) {
					strLocalityAddress = (String) arrAddress.getJSONObject(k).getString("short_name");
				}
			}

			/* prima controllo il CAP */
			if (strCapAddress != null) {
				String strCap = strCapAddress.substring(0, 3);
				/*
				 * se le prime 3 cifre del cap sono uguali allora va bene la localizzazione
				 */
				if (strCap.equalsIgnoreCase(indirizzo.getCap().substring(0, 3))) {
					JSONObject obj = listIndirizzi.getJSONObject(j).getJSONObject("geometry").getJSONObject("location");
					coordinates = getLatLong(obj);
					break;
				}
			}

			/* poi il nome del comune */
			if (strLocalityAddress != null) {
				// NOME CITTA
				if (strLocalityAddress.equalsIgnoreCase(indirizzo.getDeComuneDTO().getDescrizione())) {
					JSONObject obj = listIndirizzi.getJSONObject(j).getJSONObject("geometry").getJSONObject("location");
					coordinates = getLatLong(obj);
					break;
				}
			}
		}

		if (coordinates.isEmpty()) {
			/*
			 * ulteriore controllo: viene verificata se esiste la sezione "location_type":
			 * "APPROXIMATE"
			 */
			for (int k = 0; k < listIndirizzi.length(); k++) {
				JSONObject arrGeometry = listIndirizzi.getJSONObject(k).getJSONObject("geometry");
				if (arrGeometry != null) {
					JSONObject objCoord = arrGeometry.getJSONObject("location");
					coordinates = getLatLong(objCoord);
					break;
				}
			}
		}

		return coordinates;
	}

	public List<Coordinate> getLatLong(JSONObject obj) throws JSONException {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();

		String longitudineString = obj.getString("lng");
		String latitudineString = obj.getString("lat");
		coordinates.add(new Coordinate(new Double(latitudineString), new Double(longitudineString)));

		return coordinates;
	}

	public JSONObject getGeocoder(String parameterIndirizzo, boolean statoEstero)
			throws JSONException, MyPortalException {
		int i = 0;
		NameValuePair[] query = null;

		query = new NameValuePair[4];
		/* indirizzo da cercare */
		query[i++] = new NameValuePair("address", parameterIndirizzo);
		/* regione da cui parte la query */
		query[i++] = new NameValuePair("region", "it");
		/* true se la richiesta viene da un device con sensori di posizione */
		query[i++] = new NameValuePair("sensor", "true");

		/* key di google maps */
		query[i++] = new NameValuePair("key", ConstantsSingleton.GoogleMap.getKey());
		GetMethod urlGoogle = new GetMethod(ConstantsSingleton.GoogleMap.GEOCODE_URL);

		urlGoogle.setQueryString(query);
		String jsonString = null;

		// Invocazione servizio
		try {
			httpClient.executeMethod(urlGoogle);
			jsonString = urlGoogle.getResponseBodyAsString();
		} catch (HttpException e) {
			throw new MyPortalException("Geocoder: errore HTTP:"+e.getMessage());
		} catch (IOException e) {
			throw new MyPortalException("Geocoder: errore di IO: "+e.getMessage());
		}
		urlGoogle.releaseConnection();

		JSONObject json = new JSONObject(jsonString);

		return json;
	}
}
