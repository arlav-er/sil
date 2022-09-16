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
 * @author enrico/sebastiano/franco
 * 
 */
public class OpenStreetGeocoder implements IGeocoder {

	protected Log log = LogFactory.getLog(OpenStreetGeocoder.class);
	private HttpClient httpClient = null;


	public OpenStreetGeocoder() {
		httpClient = new HttpClient();
		HttpConnectionManagerParams connectionManagerParams = httpClient.getHttpConnectionManager().getParams();
		connectionManagerParams.setConnectionTimeout(5000);
		
		if (ConstantsSingleton.Proxy.ACTIVE) {
			if (ConstantsSingleton.Proxy.USE_AUTHENTICATION){
				String geocoderUsername = ConstantsSingleton.Proxy.USERNAME;
				String geocoderPassword = new String(ConstantsSingleton.Proxy.PASSWORD);
				Credentials defaultcreds = new UsernamePasswordCredentials(geocoderUsername, geocoderPassword);
				httpClient.getState().setProxyCredentials(AuthScope.ANY, defaultcreds);
			}
			httpClient.getHostConfiguration().setProxy(ConstantsSingleton.Proxy.ADDRESS, ConstantsSingleton.Proxy.PORT);
		}
	}

	/* (non-Javadoc)
	 * @see it.eng.myportal.utils.geocoder.IGeocoder#getCoordinates(it.eng.myportal.utils.geocoder.Indirizzo)
	 */
	@Override
	public List<Coordinate> getCoordinates(Indirizzo indirizzo) throws MyPortalException {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		try {
			JSONObject jsonObj = getGeocoder(indirizzo.getIndirizzoFormattato(), indirizzo.getDeComuneDTO().isStato());
			coordinates = getLatLong(jsonObj);
			// se non ho trovato nulla effettuo la ricerca solamente per COMUNE
			if (coordinates.size() < 1) {
				JSONObject jsonObjComune = getGeocoder(indirizzo.getIndirizzoShort(), indirizzo.getDeComuneDTO().isStato());
				coordinates = getLatLong(jsonObjComune);
			}
		} catch (JSONException e2) {
			throw new MyPortalException("geocoder.error", e2);
		} catch (MyPortalException e3){
			throw new MyPortalException("geocoder.error", e3);
		}

		return coordinates;
	}

	//Estraggo i valori di latitudine e longitudine dall'oggetto in input
	/* (non-Javadoc)
	 * @see it.eng.myportal.utils.geocoder.IGeocoder#getLatLong(org.json.JSONObject)
	 */
	@Override
	public List<Coordinate> getLatLong(JSONObject obj) throws JSONException {
		List<Coordinate> coordinates = new ArrayList<Coordinate>();

		String longitudineString = obj.getString("lon");
		String latitudineString = obj.getString("lat");
		coordinates.add(new Coordinate(new Double(latitudineString), new Double(longitudineString)));

		return coordinates;
	}

	//Geolozalizzazione mediante il servizio openStreetMap
	// - indirizzo: stringa usata contenente le informazioni da passare al servizio
	// - statoEstero: non viene utilizzato
	//Viene restituito un JSONObject contente le informazioni (latitudine, longitudine, ecc)
	//Attenzione: se il servizio non trova alcun valore viene lanciata un'eccezione
	/* (non-Javadoc)
	 * @see it.eng.myportal.utils.geocoder.IGeocoder#getGeocoder(java.lang.String, boolean)
	 */
	@Override
	public JSONObject getGeocoder(String parameterIndirizzo, boolean statoEstero) throws JSONException, MyPortalException {
		int i = 0;
		//Array di coppie chiave valore usate per costruire l'url del servizio
		NameValuePair[] query = null;
		query = new NameValuePair[3];
		query[i++] = new NameValuePair("q", parameterIndirizzo);
		query[i++] = new NameValuePair("format", "json");

		//max numero di risultati restituiti
		query[i++] = new NameValuePair("limit", "1");
		
		GetMethod urlGoogle = new GetMethod(ConstantsSingleton.OpenStreetMap.GEOCODE_URL);
		
		urlGoogle.setQueryString(query);
		log.info("Indirizzo da georeferenziare: " + parameterIndirizzo);
		String jsonString=null;
		//Invocazione servizio
		try {
			httpClient.executeMethod(urlGoogle);
			jsonString = urlGoogle.getResponseBodyAsString();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			throw new MyPortalException("Geocoder: errore HTTP", e);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			throw new MyPortalException("Geocoder: errore di IO", e);
		}
		urlGoogle.releaseConnection();
		
		
		//restituisce solo il primo risultato
		JSONArray jsonArray = new JSONArray(jsonString);
		if(jsonArray.length() == 0){
			throw new MyPortalException("Geocoder: Il servizio di geolocalizzazione non ha trovato alcun risultato per l'indirizzo: " + parameterIndirizzo);
		}
		return new JSONArray(jsonString).getJSONObject(0);
	}
}
