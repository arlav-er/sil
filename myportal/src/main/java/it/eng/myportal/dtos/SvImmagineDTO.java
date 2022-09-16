/**
 * 
 */
package it.eng.myportal.dtos;

/**
 * @author iescone
 * 
 */
public class SvImmagineDTO extends AbstractUpdatablePkDTO implements IUpdatable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1690793978906520596L;

	/**
	 * id dell'azienda alla cui vetrina è attaccata l'immagine
	 */
	private Integer idPfPrincipal;

	private byte[] immagine;
	private String codSezione;
	private String descrizSezione;

	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idSvAzienda) {
		this.idPfPrincipal = idSvAzienda;
	}

	public byte[] getImmagine() {
		return immagine;
	}

	public void setImmagine(byte[] immagine) {
		this.immagine = immagine;
	}

	public String getCodSezione() {
		return codSezione;
	}

	public void setCodSezione(String codSezione) {
		this.codSezione = codSezione;
	}

	public String getDescrizSezione() {
		return descrizSezione;
	}

	public void setDescrizSezione(String descrizSezione) {
		this.descrizSezione = descrizSezione;
	}

}
