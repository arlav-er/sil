package it.eng.sil.util.amministrazione.impatti;

/**
 * @author roccetti Classe che da alcune informazioni del lavoratore è in grado di calcolare il CodCpiTit,
 *         codMonoTipoCpi e CodCpiOrig da riportare nella tabella AN_LAV_STORIA_INF durante l'inserimento del lavoratore
 *         nell'anagrafica.
 */
public class CalcolaCpiLav {

	private String codCpiTit = "";
	private String codMonoTipoCpi = "";
	private String codCpiOrig = "";

	/**
	 * Costruttore, occorre fornire i dati su regione e provincia del lavoratore, oltre che i codCpi del lavoratore e
	 * dell'utente che effettua l'inserimento
	 */
	public CalcolaCpiLav(String regioneLav, String regioneRif, String provinciaLav, String provinciaRif,
			String codCpiLav, String codCpiRif) throws IllegalArgumentException {

		if (regioneLav == null || regioneRif == null || provinciaLav == null || provinciaRif == null) {
			throw new IllegalArgumentException("Impossibile calcolare i dati del CPI per il soggetto: "
					+ (regioneLav == null ? "<br/>&nbsp;&nbsp;regione del soggetto non indicata " : "")
					+ (regioneRif == null ? "<br/>&nbsp;&nbsp;regione dell'operatore non indicata " : "")
					+ (provinciaLav == null ? "<br/>&nbsp;&nbsp;provincia del soggetto non indicata " : "")
					+ (provinciaRif == null ? "<br/>&nbsp;&nbsp;provincia dell'operatore non indicata " : ""));
		}

		/*
		 * Se il CPI corrispondente al domicilio è uguale a quello con cui si è entrati, si aggiorna an_lav_storia_inf
		 * con codMonoTipoCpi = 'C', CodCpiTit = Cpi del domicilio, CodCpiOrig = valorizzato solo se trasferimentoda
		 * altro CPI [STESSA REGIONE, STESSA PROVINCIA]
		 */
		if (regioneLav.equals(regioneRif) && provinciaLav.equals(provinciaRif)) {
			codCpiTit = codCpiLav;
			codMonoTipoCpi = "C";
			codCpiOrig = "";
		}

		/*
		 * Se il CPI è fuori regione: codMonoTipoCpi = 'T', CodCpiTit = il cod CPI che ha la titolarità, ossia il codCpi
		 * di riferimento (quello di competenza con cui è entrato l'operatore), CodCpiOrig = CPI competente(Fuori
		 * regione) [REGIONI DIVERSE]
		 */
		else if (!regioneLav.equals(regioneRif)) {
			codCpiTit = codCpiRif;
			codMonoTipoCpi = "T";
			codCpiOrig = codCpiLav;// CodCpiOrig
		}

		/*
		 * Se il CPI è fuori provincia: (04/08/2004 al momento viene trattato come il caso di fuori regione, ma deve
		 * essere rivisto e trattato nel modo giusto)
		 */
		else if (!provinciaLav.equals(provinciaRif)) {
			codCpiTit = codCpiRif;
			codMonoTipoCpi = "T";
			codCpiOrig = codCpiLav;
		}
	}

	// Metodi Get per recupero dei dati
	public String getCodCpiTit() {
		return codCpiTit;
	}

	public String getCodMonoTipoCpi() {
		return codMonoTipoCpi;
	}

	public String getCodCpiOrig() {
		return codCpiOrig;
	}
}
