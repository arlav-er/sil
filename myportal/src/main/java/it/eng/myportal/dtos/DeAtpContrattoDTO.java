package it.eng.myportal.dtos;


/**
 * 
 * @author enrico
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */

public class DeAtpContrattoDTO extends GenericDecodeDTO implements ITreeable {

	/**
     * 
     */
	private static final long serialVersionUID = 6501945446770981388L;
	private String codPadre;
	private String descrizionePadre;
	private Integer numeroFigli;

	public DeAtpContrattoDTO() {
	}

	public DeAtpContrattoDTO(String id, String descrizione, String codPadre, String descrizionePadre) {
	    super();
	    this.id = id;
	    this.descrizione = descrizione;
	    this.codPadre = codPadre;
	    this.descrizionePadre = descrizionePadre;
    }
	
	public String getDescrizionePadre() {
    	return descrizionePadre;
    }

	public void setDescrizionePadre(String descrizionePadre) {
    	this.descrizionePadre = descrizionePadre;
    }

	public String getCodPadre() {
		return codPadre;
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
