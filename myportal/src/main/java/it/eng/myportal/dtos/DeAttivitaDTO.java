package it.eng.myportal.dtos;

/**
 * Data transfer object della tabella di decodifica Attività
 * 
 * @author Rodi A.
 * 
 * @see GenericDecodeDTO
 * @see ITreeable
 */
public class DeAttivitaDTO extends GenericDecodeDTO implements ITreeable {
	private static final long serialVersionUID = -180372330752142170L;

	private String codPadre;
	private Integer numeroFigli;

	public String getCodPadre() {
		return this.codPadre;
	}

	public void setCodPadre(String codPadre) {
		this.codPadre = codPadre;
	}

	public Integer getNumeroFigli() {
		return numeroFigli;
	}

	public void setNumeroFigli(Integer numeroFigli) {
		this.numeroFigli = numeroFigli;
	}

}
