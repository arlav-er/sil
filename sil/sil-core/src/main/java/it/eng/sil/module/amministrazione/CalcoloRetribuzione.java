package it.eng.sil.module.amministrazione;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.message.MessageBundle;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;

public class CalcoloRetribuzione extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CalcoloRetribuzione.class.getName());

	public static final Double NUM_SETTIMANE_LAVORATIVE = new Double(52);
	public static final Double NUM_MESI_LAVORATIVI = new Double(12);

	public static final String TEMPO_TOTALE = "T";
	public static final String TEMPO_PARZIALE = "P";
	public static final String TEMPO_INDEFINITO = "N";
	// public static final String PARAMETRO_DB_MANCANTE = "PARAMETRI DB MANCANTI PER CALCOLO RETRIBUZIONE";
	// public static final String PARAMETRO_INPUT_MANCANTI = "PARAMETRI INPUT CCNL, LIVELLO, TIPO ASSUNZIONE, ORARIO
	// SETTIMANALE MANCANTI PER CALCOLO RETRIBUZIONE";
	// public static final String PARAMETRO_INPUT_NON_CONGRUENTI = "IMPOSSIBILE EFFETTUARE IL CALCOLO DELLA RETRIBUZIONE
	// PARAMETRI INPUT NON CONGRUENTI";
	// public static final String ERR_ESITO_MANCANTE = "ESITO MANCANTE PER CALCOLO RETRIBUZIONE";
	private String codOrario = null;
	private String orarioSettimanale = null;
	private String codCcnl = null;
	private String numLivello = null;

	public CalcoloRetribuzione() {
	}

	public CalcoloRetribuzione(String codorario, String oresett, String codccnl, String numLivello) {
		this.codCcnl = codccnl;
		this.orarioSettimanale = oresett;
		this.codOrario = codorario;
		this.numLivello = numLivello;
	}

	public void service(SourceBean request, SourceBean response) {

		this.codOrario = (String) request.getAttribute("codorario"); // se parziale o totale
		this.orarioSettimanale = (String) request.getAttribute("oresett");
		this.codCcnl = (String) request.getAttribute("codccnl");
		this.numLivello = (String) request.getAttribute("numLivello");
		try {
			// validazione manuale o inserimento/rettifica dalla pagina il codccnl dovrebbe sempre arrivare
			if (checkCalcoloRetribuzione()) {
				calcoloCompensoRetribuzione(response);
			} else {
				response.setAttribute("ESITO", "KO");
				response.setAttribute("DESCRIZIONEESITO", "NON E' NECESSARIO CALCOLARE IL COMPENSO LORDO MINIMO");
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile effettuare il calcolo della retribuzione", e);
		}
	}

	// public void calcoloCompensoRetribuzione(SourceBean request, SourceBean response) {
	public void calcoloCompensoRetribuzione(SourceBean response) {
		String numLivelloDB = null;
		String tipoLivelloDB = null;
		String descrizioneLivelloDB = null;

		String codCcnlDB = null;

		Double lordoMensilitaDB = null;
		Double numMensilitaDB = null;
		Double retribuzioneCalc = null;
		Integer retribuzioneCalcInteger = null;
		Double divisorioOrarioDB = null;

		Double retribuzioneCalcParteA = null;
		Double retribuzioneCalcParteB = null;
		Double orarioSettimanaleDec = null;

		String codmonoorario = null;
		boolean checkInput = true;
		int msgErrore = 0;
		try {

			/******************* CONTROLLO PARAMETRI INPUT DALLA SERVICE REQUEST ****************/
			if (this.codCcnl == null || this.codCcnl.isEmpty() || this.numLivello == null || this.numLivello.isEmpty()
					|| this.codOrario == null || this.codOrario.isEmpty()) {
				checkInput = false;
				msgErrore = MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_INPUT_MANCANTI;
			} else {
				if (!codOrario.equalsIgnoreCase(this.TEMPO_INDEFINITO)) { // se N resta a true il check // N = Contratto
																			// Indefinito

					SourceBean sbCodOrario = null;
					Object params[] = new Object[1];
					params[0] = this.codOrario;
					sbCodOrario = (SourceBean) QueryExecutor.executeQuery("GET_CODORARIO", params, "SELECT",
							Values.DB_SIL_DATI);

					Vector rows = sbCodOrario.getAttributeAsVector("ROW");
					if (rows.size() != 0) {

						for (int i = 0; i < rows.size(); i++) {
							SourceBean sbCorrorrente = (SourceBean) rows.get(i);
							codmonoorario = sbCorrorrente.containsAttribute("codmonoorario")
									? sbCorrorrente.getAttribute("codmonoorario").toString()
									: null;
						}
						if (codmonoorario != null) {
							if (codmonoorario.equalsIgnoreCase(this.TEMPO_PARZIALE) && this.orarioSettimanale == null) {
								checkInput = false;
								msgErrore = MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_INPUT_MANCANTI;
							}

						} else {
							checkInput = false;
							msgErrore = MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_INPUT_DB_MANCANTI;
						}

						// codmonoorario
					} else {
						checkInput = false;
						msgErrore = MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_INPUT_DB_MANCANTI;
					}
				} else {
					codmonoorario = this.TEMPO_INDEFINITO;
				}
			}
			/****************************************************************************************/

			if (checkInput) {
				// verificare se per codccnl e numlivello c'è la corretta associazione
				SourceBean sbLivelloFromCcnl = null;
				Object params[] = new Object[2];
				params[0] = this.codCcnl;
				params[1] = this.numLivello;
				sbLivelloFromCcnl = (SourceBean) QueryExecutor.executeQuery("GET_LIVELLO_FROM_CCNL", params, "SELECT",
						Values.DB_SIL_DATI);

				Vector rows = sbLivelloFromCcnl.getAttributeAsVector("ROW");

				if (rows.size() != 0) {

					boolean checkInputDB = true;

					for (int i = 0; i < rows.size(); i++) {
						SourceBean sbCurr = (SourceBean) rows.get(i);

						/* ESTRAZIONE CAMPI DB DE_LIVELLO_RETRIBUZIONE */
						numLivelloDB = sbCurr.containsAttribute("NUMLIVELLO")
								? sbCurr.getAttribute("NUMLIVELLO").toString()
								: null;
						tipoLivelloDB = sbCurr.containsAttribute("TIPOLIVELLO")
								? sbCurr.getAttribute("TIPOLIVELLO").toString()
								: null;
						descrizioneLivelloDB = sbCurr.containsAttribute("STRDESCRIZIONE")
								? sbCurr.getAttribute("STRDESCRIZIONE").toString()
								: null;
						codCcnlDB = sbCurr.containsAttribute("CODCCNL") ? (sbCurr.getAttribute("CODCCNL").toString())
								: null;

						lordoMensilitaDB = sbCurr.containsAttribute("LORDOMENSILE")
								? new Double(sbCurr.getAttribute("LORDOMENSILE").toString())
								: null;
						numMensilitaDB = sbCurr.containsAttribute("MENSILITA")
								? new Double(sbCurr.getAttribute("MENSILITA").toString())
								: null;
						divisorioOrarioDB = sbCurr.containsAttribute("DIVISOREORARIO")
								? new Double(sbCurr.getAttribute("DIVISOREORARIO").toString())
								: null;

					}

					if (lordoMensilitaDB != null && numMensilitaDB != null) {

						if (codmonoorario.equalsIgnoreCase(this.TEMPO_TOTALE)
								|| codmonoorario.equalsIgnoreCase(this.TEMPO_INDEFINITO)) { // leggere il codmonoorario
																							// se a tempo totale
																							// altrimenti N se a tempo
																							// indefinito
							// EFFETTUO IL CALCOLO
							retribuzioneCalc = lordoMensilitaDB * numMensilitaDB;
							retribuzioneCalcInteger = new Integer(retribuzioneCalc.intValue());

							response.setAttribute("ESITO", "OK");
							response.setAttribute("DESCRIZIONEESITO", "");
							response.setAttribute("RETRIBUZIONE", retribuzioneCalcInteger.toString());
							response.setAttribute("LORDOMENSILE", lordoMensilitaDB.toString());
							response.setAttribute("MENSILITA", new Integer(numMensilitaDB.intValue()).toString());
							response.setAttribute("TIPOLIVELLO", tipoLivelloDB);
							response.setAttribute("DESCRLIVELLO", descrizioneLivelloDB);

						} else if (codmonoorario.equalsIgnoreCase(this.TEMPO_PARZIALE)) { // // leggere il codmonoorario
																							// DA FARE

							if (divisorioOrarioDB != null) {

								retribuzioneCalcParteA = lordoMensilitaDB / divisorioOrarioDB;
								orarioSettimanaleDec = new Double(orarioSettimanale);
								orarioSettimanaleDec = Math.ceil(orarioSettimanaleDec); // arrotondo gli eventuali
																						// decimali sempre per eccesso
								retribuzioneCalcParteB = ((orarioSettimanaleDec * this.NUM_SETTIMANE_LAVORATIVE)
										/ this.NUM_MESI_LAVORATIVI) * numMensilitaDB;
								retribuzioneCalc = retribuzioneCalcParteA * retribuzioneCalcParteB;
								retribuzioneCalcInteger = new Integer(retribuzioneCalc.intValue());

								response.setAttribute("ESITO", "OK");
								response.setAttribute("DESCRIZIONEESITO", "");
								response.setAttribute("RETRIBUZIONE", retribuzioneCalcInteger.toString());

								response.setAttribute("LORDOMENSILE", lordoMensilitaDB.toString());
								response.setAttribute("MENSILITA", new Integer(numMensilitaDB.intValue()).toString());
								response.setAttribute("DIVISOREORARIO", divisorioOrarioDB.toString());
								response.setAttribute("TIPOLIVELLO", tipoLivelloDB);
								response.setAttribute("DESCRLIVELLO", descrizioneLivelloDB);

							} else {
								response.setAttribute("ESITO", "KO");
								response.setAttribute("CODICEERRORE",
										MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_INPUT_DB_MANCANTI);
								response.setAttribute("DESCRIZIONEESITO", MessageBundle.getMessage(
										MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_INPUT_DB_MANCANTI));
							}

						} else {
							response.setAttribute("ESITO", "KO");
							response.setAttribute("CODICEERRORE",
									MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_RETRIBUZIONE_NON_CONGRUENTI);
							response.setAttribute("DESCRIZIONEESITO", MessageBundle.getMessage(
									MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_RETRIBUZIONE_NON_CONGRUENTI));
						}
					} else {
						response.setAttribute("ESITO", "KO");
						response.setAttribute("CODICEERRORE",
								MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_INPUT_DB_MANCANTI);
						response.setAttribute("DESCRIZIONEESITO", MessageBundle
								.getMessage(MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_INPUT_DB_MANCANTI));
					}

				} else {
					response.setAttribute("ESITO", "KO");
					response.setAttribute("CODICEERRORE",
							MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_RETRIBUZIONE_NON_CONGRUENTI);
					response.setAttribute("DESCRIZIONEESITO", MessageBundle.getMessage(
							MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_RETRIBUZIONE_NON_CONGRUENTI));
				}

			} else {
				response.setAttribute("ESITO", "KO");
				response.setAttribute("CODICEERRORE", msgErrore);
				response.setAttribute("DESCRIZIONEESITO", MessageBundle.getMessage(msgErrore));
			}

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile effettuare il calcolo della retribuzione", e);

		}
	}

	public String getCodOrario() {
		return codOrario;
	}

	public String getOrarioSettimanale() {
		return orarioSettimanale;
	}

	public String getCodCcnl() {
		return codCcnl;
	}

	public String getNumLivello() {
		return numLivello;
	}

	public boolean checkCalcoloRetribuzione() {
		boolean checkInput = false;

		// primo passo query getlivello solo per ccnl
		// a) la query restituisce 0---------------------> livello può esserci qualsiasi valore
		// b) la query restituisce qualcosa, verificare che il livello è corretto. se non è corretto errore
		if (this.codCcnl != null && !this.codCcnl.isEmpty()) {
			SourceBean sbCcnl = null;
			Object params[] = new Object[1];
			params[0] = this.codCcnl;
			sbCcnl = (SourceBean) QueryExecutor.executeQuery("GET_CCNL_DE_LIVELLO_RETRIBUZIONE", params, "SELECT",
					Values.DB_SIL_DATI);

			Vector rows = sbCcnl.getAttributeAsVector("ROW");

			if (rows.size() != 0) { // contratto è stato trovato
				checkInput = true;
				/*
				 * if(this.numLivello != null && !this.numLivello.isEmpty()) { SourceBean sbLivelloFromCcnl = null;
				 * Object paramsApp[] = new Object [2]; paramsApp[0] = this.codCcnl; paramsApp[1] = this.numLivello;
				 * sbLivelloFromCcnl = (SourceBean)QueryExecutor.executeQuery("GET_LIVELLO_FROM_CCNL", paramsApp,
				 * "SELECT", Values.DB_SIL_DATI);
				 * 
				 * Vector rowsApp = sbLivelloFromCcnl.getAttributeAsVector("ROW"); if(rowsApp.size() != 0) { // non c'è
				 * congruenza checkInput = true; } }
				 */
			}
		}

		return checkInput;
	}

}