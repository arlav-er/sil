package it.eng.myportal.utils.geocoder;

import it.eng.myportal.utils.ConstantsSingleton;

public class GeocoderFactory {

	public static IGeocoder getGeocoder() {
		if (!ConstantsSingleton.GoogleMap.getKey().isEmpty())
			return new GoogleGeocoder();
		return new OpenStreetGeocoder();
	}
}


