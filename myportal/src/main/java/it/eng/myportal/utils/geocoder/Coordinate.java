package it.eng.myportal.utils.geocoder;

public class Coordinate {
	private double latitudine = 0.0;
	private double longitudine = 0.0;

	public Coordinate(Double latitudine, Double longitudine) {
		this.latitudine = latitudine;
		this.longitudine = longitudine;
	}

	public double getLatitudine() {
		return latitudine;
	}

	public void setLatitudine(double latitudine) {
		this.latitudine = latitudine;
	}

	public double getLongitudine() {
		return longitudine;
	}

	public void setLongitudine(double longitudine) {
		this.longitudine = longitudine;
	}
}
