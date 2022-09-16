package it.eng.myportal.dtos;

/**
 * Data transfer object della tabella Regione
 * 
 * @author Rodi A.
 * 
 * @see GenericDecodeDTO
 * @see ISuggestible
 */
public class DeRegioneDTO extends GenericDecodeDTO implements ISuggestible {
	private static final long serialVersionUID = -1790470081693673418L;

	private String denominazione;
	private String codMin;

	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	@Override
	public String getDescrizione() {
		return denominazione;
	}

	@Override
	public void setDescrizione(String denominazione) {
		this.denominazione = denominazione;
	}

	public String getCodMin() {
		return codMin;
	}

	public void setCodMin(String codMin) {
		this.codMin = codMin;
	}

}
