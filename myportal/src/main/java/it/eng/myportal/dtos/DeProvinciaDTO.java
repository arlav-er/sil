package it.eng.myportal.dtos;

/**
 * Data transfer object della tabella Provincia
 * 
 * @author Rodi A.
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */

public class DeProvinciaDTO extends GenericDecodeDTO implements ISuggestible {
	private static final long serialVersionUID = 2874663271762999280L;

	private String idRegione;
	private String targa;
	private String destinatarioSare;
	private String faxRichiestaSare;

	public String getIdRegione() {
		return idRegione;
	}

	public void setIdRegione(String id) {
		this.idRegione = id;
	}

	public String getTarga() {
		return targa;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}

	public String getDestinatarioSare() {
		return destinatarioSare;
	}

	public void setDestinatarioSare(String strDestinatarioSare) {
		this.destinatarioSare = strDestinatarioSare;
	}

	public String getFaxRichiestaSare() {
		return faxRichiestaSare;
	}

	public void setFaxRichiestaSare(String faxRixhiestaSare) {
		this.faxRichiestaSare = faxRixhiestaSare;
	}

	@Override
	public String toString() {
		return String.format(
				"DeProvinciaDTO [idRegione=%s, targa=%s, destinatarioSare=%s, faxRichiestaSare=%s]",
				idRegione, targa, destinatarioSare, faxRichiestaSare);
	}

}
