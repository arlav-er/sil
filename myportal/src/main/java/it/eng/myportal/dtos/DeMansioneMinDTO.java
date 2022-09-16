package it.eng.myportal.dtos;


/**
 * Data transfer object della tabella di decodifica Mansione Ministeriale
 * 
 * @author Turrini
 * 
 * @see GenericDecodeDTO
 * @see ITreeable
 */
public class DeMansioneMinDTO extends GenericDecodeDTO implements ITreeable, Comparable<DeMansioneMinDTO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5689623390984499096L;
	private String codMansioneDot;
	private String codMansionePadre;
	private Long numeroFigli;

	public String getCodMansioneDot() {
		return codMansioneDot;
	}

	public void setCodMansioneDot(String codMansioneDot) {
		this.codMansioneDot = codMansioneDot;
	}

	public String getCodMansionePadre() {
		return this.codMansionePadre;
	}

	public void setCodMansionePadre(String codMansionePadre) {
		this.codMansionePadre = codMansionePadre;
	}

	public Long getNumeroFigli() {
		return numeroFigli;
	}

	public void setNumeroFigli(Long numeroFigli) {
		this.numeroFigli = numeroFigli;
	}

	@Override
    public int compareTo(DeMansioneMinDTO o) {
	    return descrizione.compareTo(o.getDescrizione());
    }
}
