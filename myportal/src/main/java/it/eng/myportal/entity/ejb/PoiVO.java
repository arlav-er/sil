package it.eng.myportal.entity.ejb;

class PoiVO {
	
	private double lat;

	private double lon;
	
	PoiVO() {
	}
	public double getLat() {
		return lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}
}