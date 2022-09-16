package it.eng.myportal.dtos;

/**
 * Data transfer object della tabella di decodifica Titolo
 * 
 * @author iescone
 *
 * @see GenericDecodeDTO
 * @see ITreeable
 */

public class DeTitoloDTO extends GenericDecodeDTO implements ITreeable {
	private static final long serialVersionUID = -8405778501812243222L;

	private String codPadre;
	private Boolean flgLaurea;
	private String descrizioneParlante;
	private String descrizioneTipoTitolo;
	private Integer numeroFigli;
	private boolean flgConferimentoDid;

	public DeTitoloDTO() {
	}

	public String getCodPadre() {
		return this.codPadre;
	}

	public void setCodPadre(String codPadre) {
		this.codPadre = codPadre;
	}

	public Boolean getFlgLaurea() {
		return this.flgLaurea;
	}

	public void setFlgLaurea(Boolean flgLaurea) {
		this.flgLaurea = flgLaurea;
	}

	public String getDescrizioneParlante() {
		return this.descrizioneParlante;
	}

	public void setDescrizioneParlante(String descrizioneParlante) {
		this.descrizioneParlante = descrizioneParlante;
	}

	public String getDescrizioneTipoTitolo() {
		return descrizioneTipoTitolo;
	}

	public void setDescrizioneTipoTitolo(String descrizioneTipoTitolo) {
		this.descrizioneTipoTitolo = descrizioneTipoTitolo;
	}

	public Integer getNumeroFigli() {
		return numeroFigli;
	}

	public void setNumeroFigli(Integer numeroFigli) {
		this.numeroFigli = numeroFigli;
	}

	public boolean getFlgConferimentoDid() {
		return flgConferimentoDid;
	}

	public void setFlgConferimentoDid(boolean flgConferimentoDid) {
		this.flgConferimentoDid = flgConferimentoDid;
	}
}
