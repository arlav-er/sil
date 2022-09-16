package it.eng.myportal.dtos;


/**
 * Data transfer object della tabella di decodifica Mansione
 * 
 * @author iescone
 * 
 * @see GenericDecodeDTO
 * @see ITreeable
 */
public class DeMansioneDTO extends GenericDecodeDTO implements ITreeable {

	/**
     * 
     */
    private static final long serialVersionUID = 2054062840300004764L;
	private String descrizione;
	private String codPadre;
	private Long numeroFigli;

	@Override
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getCodPadre() {
		return this.codPadre;
	}

	public void setCodPadre(String codPadre) {
		this.codPadre = codPadre;
	}

	public Long getNumeroFigli() {
		return numeroFigli;
	}

	public void setNumeroFigli(Long numeroFigli) {
		this.numeroFigli = numeroFigli;
	}

}
