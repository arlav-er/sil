package it.eng.myportal.dtos;



/**
 * Classe per visualizzare le Notizie in HomePage
 * @author Rodi A.
 *
 */
public class PfTestiDTO extends AbstractUpdatablePkDTO  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -359826010329220764L;
	private String codRegione;
	private String codice;
	private String testo;
	private String contesto;
	private String tipo;
	
	
	public String getCodRegione() {
		return codRegione;
	}
	public void setCodRegione(String codRegione) {
		this.codRegione = codRegione;
	}
	public String getCodice() {
		return codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
	public String getTesto() {
		return testo;
	}
	public void setTesto(String testo) {
		this.testo = testo;
	}
	public String getContesto() {
		return contesto;
	}
	public void setContesto(String contesto) {
		this.contesto = contesto;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	

}
