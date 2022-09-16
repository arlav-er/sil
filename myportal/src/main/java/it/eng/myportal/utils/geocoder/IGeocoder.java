package it.eng.myportal.utils.geocoder;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.exception.MyPortalException;

public interface IGeocoder {

	List<Coordinate> getCoordinates(Indirizzo indirizzo) throws MyPortalException;

	//Estraggo i valori di latitudine e longitudine dall'oggetto in input
	List<Coordinate> getLatLong(JSONObject obj) throws JSONException;

	//Geolozalizzazione mediante il servizio openStreetMap
	// - indirizzo: stringa usata contenente le informazioni da passare al servizio
	// - statoEstero: non viene utilizzato
	//Viene restituito un JSONObject contente le informazioni (latitudine, longitudine, ecc)
	//Attenzione: se il servizio non trova alcun valore viene lanciata un'eccezione
	JSONObject getGeocoder(String parameterIndirizzo, boolean statoEstero) throws JSONException, MyPortalException;

}