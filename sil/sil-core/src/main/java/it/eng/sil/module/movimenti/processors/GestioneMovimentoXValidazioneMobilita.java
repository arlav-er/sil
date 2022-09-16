package it.eng.sil.module.movimenti.processors;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.amministrazione.UtilsMobilita;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.ResultLogger;
import it.eng.sil.module.movimenti.ValidatorGeneral;

/**
 * @author landi
 * 
 *         Questo processor gestisce il movimento dalla validazione mobilità
 */
public class GestioneMovimentoXValidazioneMobilita implements RecordProcessor {

	/** Dati per la risposta */
	private String name;
	private String classname = this.getClass().getName();
	private MultipleTransactionQueryExecutor trans;
	private BigDecimal user = null;
	private boolean mobCollegataMov = false;
	private SourceBean request = null;
	private SourceBean sbGenerale = null;
	private ResultLogger logger = null;
	private BigDecimal numTsConfigLoc = null;
	private String dettaglioErrore = "";

	/**
	 * Costruttore
	 * <p>
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            TransactionQueryExecutor da utilizzare per le query
	 */
	public GestioneMovimentoXValidazioneMobilita(String name, SourceBean sbTsConfigLoc,
			MultipleTransactionQueryExecutor transexec, SourceBean req, BigDecimal user, SourceBean sbGenerale,
			ResultLogger logger) {
		this.name = name;
		this.trans = transexec;
		this.user = user;
		if (sbTsConfigLoc != null) {
			this.mobCollegataMov = true;
			this.numTsConfigLoc = sbTsConfigLoc.containsAttribute("NUMCONFIGLOC")
					? (BigDecimal) sbTsConfigLoc.getAttribute("NUMCONFIGLOC")
					: null;
		}
		this.request = req;
		this.sbGenerale = sbGenerale;
		this.logger = logger;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.LogOperazioniValidazioneMobilita.RECORD_VALIDAZIONE_NULLO),
					"Record da elaborare nullo.", warnings, nested);
		}
		// Se la mobilità è gia presente nel database allora la fase di ricerca
		// del
		// movimento non viene fatta.
		if (record.containsKey("MOBILITAPRESENTE")) {
			return null;
		}
		String context = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		String dataInizioMov = "";
		String dataCessazioneMov = "";
		if (context.equalsIgnoreCase("valida")) {
			// validazione manuale
			dataInizioMov = record.get("DATINIZIOMOVHID") != null ? record.get("DATINIZIOMOVHID").toString() : "";
			dataCessazioneMov = record.get("DATFINEMOVHID") != null ? record.get("DATFINEMOVHID").toString() : "";
		} else {
			// validazione massiva
			dataInizioMov = record.get("DATINIZIOMOV") != null ? record.get("DATINIZIOMOV").toString() : "";
			dataCessazioneMov = record.get("DATFINEMOV") != null ? record.get("DATFINEMOV").toString() : "";
		}
		if (!dataInizioMov.equals(""))
			record.put("DATAINIZIOMOVORIG", dataInizioMov);
		if (!dataCessazioneMov.equals(""))
			record.put("DATAFINEMOVORIG", dataCessazioneMov);
		// nel caso della validazione manuale, l'utente ha potuto
		// effettuare la scelta del movimento da collegare nella linguetta
		// Mobilità della pagina di validazione
		Object prgMovimentoMob = record.get("PRGMOVIMENTO");
		if (prgMovimentoMob != null && !prgMovimentoMob.toString().equals("")) {
			record.put("PRGMOVIMENTOMOB", prgMovimentoMob);
			return null;
		}
		String cdnlavoratoreMob = record.get("CDNLAVORATORE") != null ? record.get("CDNLAVORATORE").toString() : "";
		BigDecimal prgAziendaMob = record.get("PRGAZIENDA") != null
				? new BigDecimal(record.get("PRGAZIENDA").toString())
				: null;
		BigDecimal prgUnitaMob = record.get("PRGUNITAPRODUTTIVA") != null
				? new BigDecimal(record.get("PRGUNITAPRODUTTIVA").toString())
				: null;
		String dataInizioMob = "";
		if (context.equalsIgnoreCase("valida")) {
			// validazione manuale
			dataInizioMob = record.get("DATINIZIOHID") != null ? record.get("DATINIZIOHID").toString() : "";
		} else {
			// validazione massiva
			dataInizioMob = record.get("DATINIZIO") != null ? record.get("DATINIZIO").toString() : "";
		}
		String dataFineMob = record.get("DATFINE") != null ? record.get("DATFINE").toString() : "";
		String codTipoMob = record.get("CODTIPOMOB") != null ? record.get("CODTIPOMOB").toString() : "";
		Object prgMovimento = null;
		SourceBean sb = null;
		SourceBean sbMovimentoMobilita = null;
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator + "processors" + File.separator;
		try {
			// carico i movimenti protocollati del lavoratore e per la relativa
			// azienda
			Vector movimentiAperti = UtilsMobilita.getMovimentiLavoratore(cdnlavoratoreMob, prgAziendaMob, trans);
			// movimenti con cessazione compatibili con la sede aziendale
			Vector movCompatibiliSedeAzienda = new Vector();
			// movimenti con cessazione compatibili solo con l'azienda
			Vector movCompatibiliAzienda = new Vector();
			// movimenti senza cessazione compatibili con la sede aziendale
			Vector movSenzaCessCompatibiliSedeAzienda = new Vector();
			// movimenti senza cessazione compatibili solo con l'azienda
			Vector movSenzaCessCompatibiliAzienda = new Vector();
			// ricerca di un movimento compatibile con la mobilità
			for (int i = 0; i < movimentiAperti.size(); i++) {
				SourceBean sbMov = (SourceBean) movimentiAperti.get(i);
				String codTipoMov = StringUtils.getAttributeStrNotNull(sbMov, "CODTIPOMOV");
				String codMonoTempo = StringUtils.getAttributeStrNotNull(sbMov, "CODMONOTEMPO");
				String datFineMovEffettiva = StringUtils.getAttributeStrNotNull(sbMov, "DATFINEMOVEFFETTIVA");
				BigDecimal prgMovSucc = (BigDecimal) sbMov.getAttribute("PRGMOVIMENTOSUCC");
				String codMonoTipoFine = StringUtils.getAttributeStrNotNull(sbMov, "CODMONOTIPOFINE");
				BigDecimal prgUnita = (BigDecimal) sbMov.getAttribute("PRGUNITA");
				String codMvCessazione = StringUtils.getAttributeStrNotNull(sbMov, "CODMVCESSAZIONE");
				if (!codTipoMov.equalsIgnoreCase("CES") && codMonoTempo.equalsIgnoreCase("I") && prgMovSucc != null
						&& !dataCessazioneMov.equals("") && datFineMovEffettiva.equals(dataCessazioneMov)
						&& codMonoTipoFine.equalsIgnoreCase("C") && !codMvCessazione.equalsIgnoreCase("SC")) {
					movCompatibiliAzienda.add(sbMov);
					if (prgUnita != null && prgUnitaMob != null && prgUnita.equals(prgUnitaMob)) {
						movCompatibiliSedeAzienda.add(sbMov);
					}
				}
			}

			if (movCompatibiliSedeAzienda.size() == 1) {
				// ho trovato un solo movimento a tempo indeterminato cessato
				// compatibile con la mobilità (rispetto alla sede aziendale)
				sb = (SourceBean) movCompatibiliSedeAzienda.get(0);
				sbMovimentoMobilita = sb;
				prgMovimento = sb.getAttribute("PRGMOVIMENTOSUCC"); // prgmovimento
																	// della
																	// cessazione
			} else {
				if (movCompatibiliSedeAzienda.size() > 1) {
					// ci sono più movimenti a tempo indeterminato cessati
					// che sono compatibili con la mobilità (rispetto alla sede
					// aziendale)
					if (mobCollegataMov) { // caso 1 e caso 2
						// in questo caso devo concludere con errore perché il
						// collegamento della
						// mobilità al movimento è obbligatorio, e dalla ricerca
						// non lo riesco
						// a determinare univocamente
						return ProcessorsUtils.createResponse(name, classname, new Integer(
								MessageCodes.LogOperazioniValidazioneMobilita.FALLITA_DETERMINAZIONE_MOVIMENTO_DA_COLLEGARE_MOBILITA),
								"Il collegamento della mobilità al movimento è obbligatorio.", warnings, nested);
					}
				} else {
					// non esiste un movimento a tempo indeterminato cessato
					// compatibile con la mobilità (rispetto alla sede
					// aziendale)
					if (movCompatibiliSedeAzienda.size() == 0) {
						if (movCompatibiliAzienda.size() == 1) {
							// ho trovato un solo movimento a tempo
							// indeterminato cessato
							// compatibile con la mobilità (rispetto alla sola
							// azienda)
							sb = (SourceBean) movCompatibiliAzienda.get(0);
							sbMovimentoMobilita = sb;
							prgMovimento = sb.getAttribute("PRGMOVIMENTOSUCC"); // prgmovimento
																				// della
																				// cessazione
						} else {
							if (movCompatibiliAzienda.size() > 1) {
								// ci sono più movimenti a tempo indeterminato
								// cessati
								// che sono compatibili con la mobilità
								// (rispetto alla sola azienda)
								if (mobCollegataMov) { // caso 1 e caso 2
									// in questo caso devo concludere con errore
									// perché il collegamento della
									// mobilità al movimento è obbligatorio, e
									// dalla ricerca non lo riesco
									// a determinare univocamente
									return ProcessorsUtils.createResponse(name, classname, new Integer(
											MessageCodes.LogOperazioniValidazioneMobilita.FALLITA_DETERMINAZIONE_MOVIMENTO_DA_COLLEGARE_MOBILITA),
											"Il collegamento della mobilità al movimento è obbligatorio.", warnings,
											nested);
								}
							} else {
								// Siamo nel caso in cui
								// movCompatibiliSedeAzienda.size() == 0 e
								// movCompatibiliAzienda.size == 0 (non ho
								// trovato nessun movimento a
								// tempo indeterminato cessato compatibile con
								// la mobilità).
								if (mobCollegataMov) {
									// collegamento della mobilità al movimento
									// è obbligatorio
									// Cerco un movimento a tempo indeterminato
									// non cessato compatibile con la mobilità
									for (int i = 0; i < movimentiAperti.size(); i++) {
										SourceBean sbMov = (SourceBean) movimentiAperti.get(i);
										String codTipoMov = StringUtils.getAttributeStrNotNull(sbMov, "CODTIPOMOV");
										String codMonoTempo = StringUtils.getAttributeStrNotNull(sbMov, "CODMONOTEMPO");
										String datInizioMov = StringUtils.getAttributeStrNotNull(sbMov, "DATINIZIOMOV");
										BigDecimal prgMovSucc = (BigDecimal) sbMov.getAttribute("PRGMOVIMENTOSUCC");
										BigDecimal prgUnita = (BigDecimal) sbMov.getAttribute("PRGUNITA");
										String codTipoAss = StringUtils.getAttributeStrNotNull(sbMov, "CODTIPOASS");
										if (!codTipoMov.equalsIgnoreCase("CES") && codMonoTempo.equalsIgnoreCase("I")
												&& prgMovSucc == null && !dataCessazioneMov.equals("")
												&& DateUtils.compare(datInizioMov, dataCessazioneMov) <= 0
												&& !codTipoAss.equalsIgnoreCase("Z.09.02")) {
											movSenzaCessCompatibiliAzienda.add(sbMov);
											if (prgUnita != null && prgUnitaMob != null
													&& prgUnita.equals(prgUnitaMob)) {
												movSenzaCessCompatibiliSedeAzienda.add(sbMov);
											}
										}
									}

									if (movSenzaCessCompatibiliSedeAzienda.size() == 1) {
										// ho trovato un solo movimento a tempo
										// indeterminato non cessato
										// compatibile con la mobilità (rispetto
										// alla sede aziendale)
										if (numTsConfigLoc != null && numTsConfigLoc.equals(new BigDecimal("1"))) {
											// inserimento automatico abilitato
											// e in questo
											// caso devo inserire solo la
											// cessazione
											try {
												sb = (SourceBean) movSenzaCessCompatibiliSedeAzienda.get(0);
												ValidatorGeneral validatorMov = inserisciMovimento(sb, warnings, nested,
														record);
												if (validatorMov.getErroreValidatorGeneral()) {
													return ProcessorsUtils.createResponse(name, classname,
															new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
															"errore nel recupero del movimento da associare alla mobilità:"
																	+ dettaglioErrore,
															warnings, nested);
												}
												prgMovimento = record.get("PRGMOVIMENTO");
												warnings.add(new Warning(
														MessageCodes.LogOperazioniValidazioneMobilita.INSERT_MOVIMENTO_CESS_VALIDAZIONE_MASSIVA_MOBILITA,
														"Tipo Mobilità '" + codTipoMob + " data inizio " + dataInizioMob
																+ " data fine " + dataFineMob));
											} catch (Exception e) {
												return ProcessorsUtils.createResponse(name, classname, new Integer(
														MessageCodes.LogOperazioniValidazioneMobilita.INSERT_MOV_ASSOCIATO_MOB_FAIL),
														"non è stato possibile associare alla mobilità il movimento.",
														warnings, nested);
											}
										} else { // caso 2
											// la mobilità deve essere collegata
											// ad un movimento e num nella
											// ts_config_loc
											// non prevede l'inserimento
											// automatico del movimento ----->
											// questo implica segnalazione di
											// errore
											return ProcessorsUtils.createResponse(name, classname, new Integer(
													MessageCodes.LogOperazioniValidazioneMobilita.INS_MOVIMENTO_NON_PREVISTO_IN_VALIDAZIONE_MOBILITA),
													"Il collegamento della mobilità al movimento è obbligatorio.",
													warnings, nested);
										}
									} else {
										if (movSenzaCessCompatibiliSedeAzienda.size() > 1) { // caso
																								// 1 e
																								// caso
																								// 2
											// ci sono più movimenti a tempo
											// indeterminato non cessati
											// che sono compatibili con la
											// mobilità (rispetto alla sede
											// aziendale)
											// in questo caso devo concludere
											// con errore perché il collegamento
											// della
											// mobilità al movimento è
											// obbligatorio
											return ProcessorsUtils.createResponse(name, classname, new Integer(
													MessageCodes.LogOperazioniValidazioneMobilita.FALLITA_DETERMINAZIONE_MOVIMENTO_DA_COLLEGARE_MOBILITA),
													"Il collegamento della mobilità al movimento è obbligatorio.",
													warnings, nested);
										} else {
											// non esiste un movimento a tempo
											// indeterminato non cessato
											// compatibile con la mobilità
											// (rispetto alla sede aziendale)
											if (movSenzaCessCompatibiliSedeAzienda.size() == 0) {
												if (movSenzaCessCompatibiliAzienda.size() == 1) {
													// ho trovato un solo
													// movimento a tempo
													// indeterminato non cessato
													// compatibile con la
													// mobilità (rispetto alla
													// sola azienda)
													if (numTsConfigLoc != null
															&& numTsConfigLoc.equals(new BigDecimal("1"))) {
														// inserimento
														// automatico abilitato
														// e in questo
														// caso devo inserire
														// solo la cessazione
														try {
															sb = (SourceBean) movSenzaCessCompatibiliAzienda.get(0);
															ValidatorGeneral validatorMov = inserisciMovimento(sb,
																	warnings, nested, record);
															if (validatorMov.getErroreValidatorGeneral()) {
																return ProcessorsUtils.createResponse(name, classname,
																		new Integer(
																				MessageCodes.ImportMov.ERR_INSERT_DATA),
																		"errore nel recupero del movimento da associare alla mobilità:"
																				+ dettaglioErrore,
																		warnings, nested);
															}
															prgMovimento = record.get("PRGMOVIMENTO");
															warnings.add(new Warning(
																	MessageCodes.LogOperazioniValidazioneMobilita.INSERT_MOVIMENTO_CESS_VALIDAZIONE_MASSIVA_MOBILITA,
																	"Tipo Mobilità '" + codTipoMob + " data inizio "
																			+ dataInizioMob + " data fine "
																			+ dataFineMob));
														} catch (Exception e) {
															return ProcessorsUtils.createResponse(name, classname,
																	new Integer(
																			MessageCodes.LogOperazioniValidazioneMobilita.INSERT_MOV_ASSOCIATO_MOB_FAIL),
																	"non è stato possibile associare alla mobilità il movimento.",
																	warnings, nested);
														}
													} else { // caso 2
														// la mobilità deve
														// essere collegata ad
														// un movimento e num
														// nella ts_config_loc
														// non prevede
														// l'inserimento
														// automatico del
														// movimento ----->
														// questo implica
														// segnalazione di
														// errore
														return ProcessorsUtils.createResponse(name, classname,
																new Integer(
																		MessageCodes.LogOperazioniValidazioneMobilita.INS_MOVIMENTO_NON_PREVISTO_IN_VALIDAZIONE_MOBILITA),
																"Il collegamento della mobilità al movimento è obbligatorio.",
																warnings, nested);
													}
												} else {
													if (movSenzaCessCompatibiliAzienda.size() > 1) { // caso
																										// 1 e
																										// caso
																										// 2
														// ci sono più movimenti
														// a tempo indeterminato
														// non cessati
														// che sono compatibili
														// con la mobilità
														// (rispetto alla sola
														// azienda)
														// in questo caso devo
														// concludere con errore
														// perché il
														// collegamento della
														// mobilità al movimento
														// è obbligatorio
														return ProcessorsUtils.createResponse(name, classname,
																new Integer(
																		MessageCodes.LogOperazioniValidazioneMobilita.FALLITA_DETERMINAZIONE_MOVIMENTO_DA_COLLEGARE_MOBILITA),
																"Il collegamento della mobilità al movimento è obbligatorio.",
																warnings, nested);
													} else {
														// Cerco di inserire sia
														// l'avviamento che la
														// cessazione
														if (numTsConfigLoc != null
																&& numTsConfigLoc.equals(new BigDecimal("1"))) {
															// inserimento
															// automatico
															// abilitato e in
															// questo caso devo
															// cercare di
															// inserire sia
															// l'avviamento che
															// la cessazione
															try {
																SourceBean sbApp = null;
																ValidatorGeneral validatorMov = inserisciMovimento(
																		sbApp, warnings, nested, record);
																if (validatorMov.getErroreValidatorGeneral()) {
																	return ProcessorsUtils.createResponse(name,
																			classname,
																			new Integer(
																					MessageCodes.ImportMov.ERR_INSERT_DATA),
																			"errore nel recupero del movimento da associare alla mobilità:"
																					+ dettaglioErrore,
																			warnings, nested);
																}
																prgMovimento = record.get("PRGMOVIMENTO");
																warnings.add(new Warning(
																		MessageCodes.LogOperazioniValidazioneMobilita.INSERT_MOVIMENTO_CESS_VELOCE_VALIDAZIONE_MASSIVA_MOBILITA,
																		"Tipo Mobilità '" + codTipoMob + " data inizio "
																				+ dataInizioMob + " data fine "
																				+ dataFineMob));
															} catch (Exception e) {
																return ProcessorsUtils.createResponse(name, classname,
																		new Integer(
																				MessageCodes.LogOperazioniValidazioneMobilita.INSERT_MOV_ASSOCIATO_MOB_FAIL),
																		"non è stato possibile associare alla mobilità il movimento.",
																		warnings, nested);
															}
														} else { // caso 2
															// la mobilità deve
															// essere collegata
															// ad un movimento e
															// num nella
															// ts_config_loc
															// non prevede
															// l'inserimento
															// automatico del
															// movimento ----->
															// questo implica
															// segnalazione di
															// errore
															return ProcessorsUtils.createResponse(name, classname,
																	new Integer(
																			MessageCodes.LogOperazioniValidazioneMobilita.INS_MOVIMENTO_NON_PREVISTO_IN_VALIDAZIONE_MOBILITA),
																	"Il collegamento della mobilità al movimento è obbligatorio.",
																	warnings, nested);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			// se è stato trovato o creato il movimento da collegare alla
			// mobilità
			// lo inserisco nella Map per il processor che inserisce o aggiorna
			// la mobilità validata nella tabella am_mobilita_iscr
			if (prgMovimento != null) {
				/*
				 * if (sbMovimentoMobilita != null) { //ho creato il collegamento con il movimento senza l'inserimento
				 * di alcun movimento String codGrado =
				 * sbMovimentoMobilita.containsAttribute("CODGRADOCES")?sbMovimentoMobilita.getAttribute("CODGRADOCES").
				 * toString():""; String numLivello =
				 * sbMovimentoMobilita.containsAttribute("NUMLIVELLOCES")?sbMovimentoMobilita.getAttribute(
				 * "NUMLIVELLOCES").toString():""; String codMansione =
				 * sbMovimentoMobilita.containsAttribute("CODMANSIONECES")?sbMovimentoMobilita.getAttribute(
				 * "CODMANSIONECES").toString():""; String codCCNL =
				 * sbMovimentoMobilita.containsAttribute("CODCCNL")?sbMovimentoMobilita.getAttribute("CODCCNL").toString
				 * ():""; String descCCNL =
				 * sbMovimentoMobilita.containsAttribute("strCCNL")?sbMovimentoMobilita.getAttribute("strCCNL").toString
				 * ():""; if (context.equalsIgnoreCase("valida")) { //validazione manuale record.put("CODGRADOHID",
				 * codGrado); } else { //validazione massiva record.put("CODGRADO", codGrado); }
				 * record.put("STRLIVELLO", numLivello); record.put("CODMANSIONE", codMansione); record.put("CODCCNL",
				 * codCCNL); }
				 */
				record.put("PRGMOVIMENTOMOB", prgMovimento);
			}
		}

		catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.LogOperazioniValidazioneMobilita.FALLITA_GESTIONE_MOVIMENTO),
					"errore nel recupero del movimento da associare alla mobilità.", warnings, nested);
		}

		if ((warnings.size() > 0) || (nested.size() > 0)) {
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		} else {
			return null;
		}
	}

	/**
	 * Metodo che gestisce l'inserimento dei movimenti che hanno messo il lavoratore in mobilità con il ricalcolo dello
	 * stato occupazionale del lavoratore (l'inserimento o meno è possibile configurando opportunamente la tabella
	 * TS_CONFIG_LOC; per adesso in fase di validazione mobilità, l'inserimento del movimento o dei movimenti che hanno
	 * messo il lavoratore in mobilità NON E' PREVISTO).
	 * 
	 * @param sb
	 *            sourceBean che non è null quando ho trovato solo il movimento a tempo indeterminato senza la
	 *            cessazione; contiene i dati del movimento a cui si cercherà di collegare la cessazione
	 * @param warnings
	 * @param nested
	 * @param record
	 * @return
	 * @throws Exception
	 */
	public ValidatorGeneral inserisciMovimento(SourceBean sb, ArrayList warnings, ArrayList nested, Map record)
			throws Exception {
		String dataCessazioneMov = "";
		String dataInizioMov = "";
		String context = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		if (context.equalsIgnoreCase("valida")) {
			// validazione manuale
			dataCessazioneMov = record.get("DATFINEMOVHID") != null ? record.get("DATFINEMOVHID").toString() : "";
			dataInizioMov = record.get("DATINIZIOMOVHID") != null ? record.get("DATINIZIOMOVHID").toString() : "";
		} else {
			// validazione massiva
			dataCessazioneMov = record.get("DATFINEMOV") != null ? record.get("DATFINEMOV").toString() : "";
			dataInizioMov = record.get("DATINIZIOMOV") != null ? record.get("DATINIZIOMOV").toString() : "";
		}
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator + "processors" + File.separator;
		record.put("CODSTATOATTO", "PR");
		record.put("CODTIPOMOV", "CES");
		record.put("CONTEXT", "inserisci");
		record.put("DATINIZIOMOV", dataCessazioneMov);
		if (record.containsKey("DATFINEMOV"))
			record.remove("DATFINEMOV");
		if (record.containsKey("COLLEGATO"))
			record.remove("COLLEGATO");
		if (sb == null)
			record.put("COLLEGATO", "nessuno");
		if (record.containsKey("DATAINIZIOAVVCEV"))
			record.remove("DATAINIZIOAVVCEV");
		if (sb == null)
			record.put("DATAINIZIOAVVCEV", dataInizioMov);
		if (sb == null) {
			record.put("CODORARIO", "F");
		} else {
			if (sb.getAttribute("CODORARIO") != null)
				record.put("CODORARIO", sb.getAttribute("CODORARIO").toString());
			else
				record.put("CODORARIO", "F");
		}
		String numLivelloMov = "";
		if (record.containsKey("STRLIVELLO")) {
			numLivelloMov = record.get("STRLIVELLO").toString();
			record.put("NUMLIVELLO", numLivelloMov);
		}
		// verificare matricola, livello, codMansione e codGrado quando non sono
		// valorizzati
		// per assegnare eventualmente valori di default, come è stato fatto per
		// la validazione massiva
		record.put("CODMONOMOVDICH", "O");
		record.put("CODMONOPROV", "F");
		record.put("CODMVCESSAZIONE", "LC");
		record.put("CODMOTANNULLAMENTO", "MOB");
		// indica che sto inserendo il movimento dalla validazione mobilità
		record.put("VALIDAZIONE_MOBILITA", "true");
		ValidatorGeneral validatorMov = new ValidatorGeneral(record);
		// Processor che gestisce i movimenti di AVV da CVE
		RecordProcessor gestoreCVE = new GestisciCVEInserimento("gestione CVE inserimento", sbGenerale, trans, user);
		// Processore che seleziona il movimento precedente
		validatorMov.addProcessor(new SelectMovimentoPrecManuale("Seleziona Precedente", trans, gestoreCVE));
		// Controlli sulle autorizzazioni
		validatorMov.addProcessor(new ControlloPermessi("Autorizzazione per impatti", trans));
		// controllo dei dati sensibili del lavoratore
		validatorMov.addProcessor(new ProcControlloMbCmEtaLav("Controllo dati lavoratore", trans));
		// Processore per il controllo dell'esistenza di movimenti simili a
		// quello in inserimento
		validatorMov.addProcessor(new ControlloMovimentoSimile("Controllo movimenti simili", trans, sbGenerale));

		// controllo sul tipo di assunzione
		validatorMov.addProcessor(new ControlloTipoAssunzione("Controllo tipo assunzione", trans));

		// Processore che controlla la durata dei movimenti a TD
		validatorMov.addProcessor(new ControlloDurataTD("Controlla durata movimenti a TD"));
		// Processore che controlla i dati del movimento.
		validatorMov.addProcessor(new ControllaMovimenti(sbGenerale, trans, user));
		// Processore per ulteriori controlli che di solito sono svolti nella
		// jsp
		validatorMov.addProcessor(new CrossController("Controllore Incrociato"));
		// Processore per l'esecuzione degli impatti
		RecordProcessor eseguiImpatti = new EseguiImpatti("Esecuzione impatti", sbGenerale, trans, user);
		validatorMov.addProcessor(eseguiImpatti);
		// Inserimento Movimento
		validatorMov.addProcessor(new InsertData("Inserimento Movimento", trans, configbase + "insertMovimento.xml",
				"INSERT_MOVIMENTO", user));
		// Processore che aggiorna il movimento precedente
		validatorMov.addProcessor(new UpdateMovimentoPrec("Aggiorna Precedente", trans, user));
		// Processore per l'inserimento in am_movimento_apprendist
		validatorMov.addProcessor(new InsertApprendistato(user, trans));
		// Processore per l'inserimento in am_movimento_apprendist delle info
		// relative al tirocinio
		validatorMov.addProcessor(new InsertTirocinio(user, trans, sbGenerale));
		validatorMov.addProcessor(new SelectMovimentoSucc("CercaMovimentoSuccessivo", trans));
		// Processors per l'inserimento del documento(quello da validare e
		// eventualmente l'avviamento
		// creato dalla cessazione)
		validatorMov.addProcessor(new GestisciDocumentoCVE(user, trans));
		validatorMov.addProcessor(new InsertDocumento(user, trans));
		// eseguo i processors
		SourceBean result = validatorMov.importRecords(trans);
		Vector processorResult = result.getAttributeAsVector("PROCESSOR");
		for (int i = 0; i < processorResult.size(); i++) {
			SourceBean sbProcessor = (SourceBean) processorResult.get(i);
			String tipoErrore = sbProcessor.containsAttribute("RESULT") ? sbProcessor.getAttribute("RESULT").toString()
					: "";
			if (tipoErrore.equalsIgnoreCase("WARNING")) {
				String codice = sbProcessor.containsAttribute("WARNING.CODE")
						? sbProcessor.getAttribute("WARNING.CODE").toString()
						: "";
				String descrizione = sbProcessor.containsAttribute("WARNING.MESSAGECODE")
						? sbProcessor.getAttribute("WARNING.MESSAGECODE").toString()
						: "";
				if (!codice.equals("")) {
					int nCod = new Integer(codice).intValue();
					warnings.add(new Warning(nCod, descrizione));
				}
			} else {
				if (tipoErrore.equalsIgnoreCase("ERROR")) {
					this.dettaglioErrore = sbProcessor.containsAttribute("ERROR.DETTAGLIO")
							? sbProcessor.getAttribute("ERROR.DETTAGLIO").toString()
							: "";
				}
			}
		}
		record.put("CONTEXT", context);
		return validatorMov;
	}

}
