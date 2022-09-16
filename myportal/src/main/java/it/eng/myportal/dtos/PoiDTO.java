package it.eng.myportal.dtos;


public class PoiDTO extends AbstractUpdatablePkDTO {


	/**
	 * 
	 */
	private static final long serialVersionUID = -97928649572467555L;
	private String descrizione;
	private Double lat;
	private Double lon;
	private String codTipoPoi;
	
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public String getCodTipoPoi() {
		return codTipoPoi;
	}
	public void setCodTipoPoi(String codTipoPoi) {
		this.codTipoPoi = codTipoPoi;
	}
}
