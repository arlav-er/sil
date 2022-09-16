package it.eng.sil.module.trento;

public class Consenso {

	private String codice = null;
	private String descrizione = null;
	private String dataRegistrazione = null;
	private String dataRevoca = null;

	public static final String ATTIVO = "AT";
	public static final String REVOCATO = "RE";
	public static final String ASSENTE = "AS";
	public static final String NON_DISPONIBILE = "ND";

	public Consenso() {
		// TODO Auto-generated constructor stub
	}

	public String getCodice() {
		return this.codice;
	}

	public String getDescrizione() {
		return this.descrizione;
	}

	public String getDataRegistrazione() {
		return this.dataRegistrazione;
	}

	public String getDataRevoca() {
		return this.dataRevoca;
	}

	public void setCodice(String val) {
		this.codice = val;
	}

	public void setDescrizione(String val) {
		this.descrizione = val;
	}

	public void setDataRegistrazione(String val) {
		this.dataRegistrazione = val;
	}

	public void setDataRevoca(String val) {
		this.dataRevoca = val;
	}

	public String toString() {
		return "Consenso con stato = " + getCodice();
	}

}
