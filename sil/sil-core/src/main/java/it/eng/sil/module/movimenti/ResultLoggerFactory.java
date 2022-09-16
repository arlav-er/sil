package it.eng.sil.module.movimenti;

/**
 * Classe di utilità per la costruzione delle catene di logger usate.
 * 
 * @author Luigi Antenucci
 */
public abstract class ResultLoggerFactory {

	/**
	 * Crea la catena per il log di tipo "Single" (elaborazione del singolo record):
	 */
	public static final ResultLogger getSingleChain() {

		ResultLogger txt = new HtmlResultLogger();
		ResultLogger db = new SingleResultLogger();
		txt.addChildResultLogger(db);

		return txt;
	}

	/**
	 * Crea la catena per il log di tipo "Multiple" (elaborazione massiva di record). NB: i parametri servono per
	 * costruire il MultipleResultLogger.
	 */
	/*
	 * NON USATA:7 public static final ResultLogger getMultipleChain(int numRecords, SessionContainer sessione) throws
	 * LogException {
	 * 
	 * ResultLogger txt = new TextResultLogger(); ResultLogger db = new MultipleResultLogger(numRecords, sessione); //
	 * può generare LogException txt.addChildResultLogger(db);
	 * 
	 * return txt; }
	 */
}
