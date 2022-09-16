package it.eng.sil.coop.webservices.myportal.servizicittadino;

public class ServiziCittadinoException extends Exception {

	private static final long serialVersionUID = 1L;
	public static final String SERVIZIO_NON_DISPONIBILE = "Servizio non disponibile";
	public static final String ERRORE_GENERICO = "Errore generico";

	public ServiziCittadinoException(String description) {
		super(description);
	}

}
