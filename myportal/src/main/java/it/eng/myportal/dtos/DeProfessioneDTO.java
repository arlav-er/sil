package it.eng.myportal.dtos;


/**
 * Data transfer object della tabella di decodifica Professione
 * 
 * @author turrini
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */
public class DeProfessioneDTO extends GenericDecodeDTO implements ITreeable {

	private String codProfessione;
	private String descrizione;

	public DeProfessioneDTO() {
	}

	@Override
	public String getDescrizione() {
		return this.descrizione;
	}

	public String getCodProfessione() {
		return codProfessione;
	}

	public void setCodProfessione(String codProfessione) {
		this.codProfessione = codProfessione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
