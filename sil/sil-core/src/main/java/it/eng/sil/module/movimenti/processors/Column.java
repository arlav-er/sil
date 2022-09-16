package it.eng.sil.module.movimenti.processors;

/**
 * Classe di utilita per la configurazione dei processori che inseriscono dati nelle tabelle del DB
 * <p>
 * 
 * @author Paolo Roccetti
 * 
 */
public class Column {
	private String name;
	private String format;
	private boolean nullable;
	private boolean absolute;
	private String data;
	private String descr;

	/**
	 * Costruttore, i dati da inserire sono gli attributi degli elementi COLUMN nella definizione del file
	 * WEB-INF/conf/import/processors/insertData.dtd
	 * <p>
	 * 
	 * @param name
	 *            nome della colonna
	 * @param format
	 *            formato del dato nella colonna
	 * @param nullable
	 *            possibilità (solo con "true") di inserire il valore null se non trovato, default: false
	 * @param type
	 *            se il valore deve essere ricercato nel record corrente (con record), o è indicato in modo assoluto
	 *            nell'attributo data (solo "absolute"), default record
	 * @param data
	 *            indica il nome dell'attributo in cui trovare il valore della colonna da inserire, oppure il suo valore
	 *            assoluto se l'attributo type ha valore "absolute"
	 * @param description
	 *            indica la descrizione della colonna da utilizzare per le informazioni all'utente. Se non presente
	 *            verrà usato il nome della colonna.
	 */
	public Column(String name, String format, String nullable, String type, String data, String description) {
		this.name = name;
		this.format = format;
		this.nullable = (nullable.equals("true") ? true : false);
		this.absolute = (type.equals("absolute") ? true : false);
		this.data = data;
		this.descr = (description != null ? description : name);
	}

	public String getName() {
		return name;
	}

	public String getFormat() {
		return format;
	}

	public boolean isNullable() {
		return nullable;
	}

	public boolean isAbsolute() {
		return absolute;
	}

	public String getData() {
		return data;
	}

	public String getDescription() {
		return descr;
	}
}