package it.eng.myportal.dtos;


/**
 * Data transfer object della tabella di decodifica Attivita Ministeriale
 * 
 * @author Turrini
 * 
 * @see GenericDecodeDTO
 * @see ITreeable
 */
public class DeAttivitaMinDTO extends GenericDecodeDTO implements ITreeable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5210248379251689193L;
	private String codAttivitaPadre;
	private String codAttivitaDot;
	private Long numeroFigli;

	public String getCodAttivitaPadre() {
		return this.codAttivitaPadre;
	}

	public void setCodAttivitaPadre(String codAttivitaPadre) {
		this.codAttivitaPadre = codAttivitaPadre;
	}

	public String getCodAttivitaDot() {
		return codAttivitaDot;
	}

	public void setCodAttivitaDot(String codAttivitaDot) {
		this.codAttivitaDot = codAttivitaDot;
	}

	public Long getNumeroFigli() {
		return numeroFigli;
	}

	public void setNumeroFigli(Long numeroFigli) {
		this.numeroFigli = numeroFigli;
	}
}
