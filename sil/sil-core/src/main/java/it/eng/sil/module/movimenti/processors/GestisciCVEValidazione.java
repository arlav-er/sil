/*
 * Creato il 29-dic-04
 */
package it.eng.sil.module.movimenti.processors;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.HtmlResultLogger;
import it.eng.sil.module.movimenti.ManualExtractor;
import it.eng.sil.module.movimenti.RecordExtractor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.ResultLogger;
import it.eng.sil.module.movimenti.SingleResultLogger;
import it.eng.sil.module.movimenti.ValidatorAssunzioneCVE;
import it.eng.sil.module.movimenti.enumeration.CodMonoTempoEnum;
import it.eng.sil.module.movimenti.enumeration.TipoTrasfEnum;

/**
 * @author giuliani
 *
 *         Questo processor gestisce l'inserimento dell'avviamento da cessazione, proroga o trasformazione nel caso di
 *         validazione manuale o massiva dei movimenti
 */
public class GestisciCVEValidazione implements RecordProcessor {

	/** Dati per la risposta */
	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di select */
	private TransactionQueryExecutor trans;
	private BigDecimal user = null;
	private BigDecimal numOreSett = null;
	private String codOrario = null;
	private String flgAssPropria = null;
	private Boolean notAssPropria = null;
	private SourceBean sbInfoGenerale = null;
	private SourceBean req = null;
	private Map dataPTVE = null;
	private boolean checkForzaValidazione = false;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GestisciCVEValidazione.class.getName());

	/**
	 * Costruttore
	 * <p>
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            TransactionQueryExecutor da utilizzare per le query
	 * @param user
	 *            codice dell'utente che esegue la processazione
	 */
	public GestisciCVEValidazione(String name, SourceBean sb, TransactionQueryExecutor transexec, BigDecimal user,
			SourceBean req) {
		this.name = name;
		this.trans = transexec;
		this.user = user;
		this.sbInfoGenerale = sb;
		setRequest(req);
		checkForzaValidazione = ProcessorsUtils.checkForzaValidazione(trans);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see it.eng.sil.module.movimenti.RecordProcessor#processRecord(java.util.Map)
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", warnings, nested);
		}
		// checkForzaValidazione = false se non sono in validazione massiva
		if (record.get("CONTEXT") == null || !record.get("CONTEXT").toString().equalsIgnoreCase("validazioneMassiva")) {
			checkForzaValidazione = false;
		}
		// Creo la Map per il movimento di avviamento da validare
		Map recordAvv = getMapAvv(record);
		if (recordAvv == null)
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.ImportMov.ERR_INSERT_AVV_CEV), null, null, null);
		// ***Istanzio il meccanismo di inserimento dell'assunzione veloce e lo configuro per l'inserimento***//
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator;

		// creazione del validatore per l'assunzione e configurazione dei processori
		ValidatorAssunzioneCVE validator = new ValidatorAssunzioneCVE();

		// Path dei file di configurazione
		String processorbase = configbase + "processors" + File.separator;

		// Aggiungo i processori al validator (quelli che non servono non li inserisco)
		try {
			// Selezione codice contratto
			RecordProcessor selectCodContr = new SelectCodContratto("Seleziona_Codice_Contratto", trans);
			validator.addProcessor(selectCodContr);
			// Prevalorizzazione campi mancanti
			validator.addProcessor(new PrevalorizzaCampi("Default campi mancanti"));
			// Inserimento Azienda*/
			RecordProcessor insAz = null;
			boolean validazioneMassiva = record.get("CONTEXT").toString().equalsIgnoreCase("validazioneMassiva");
			if (validazioneMassiva) {
				insAz = new InsertAziendaXValidazioneMass("Inserimento Azienda nel DB", trans, user,
						processorbase + "insertAzienda.xml", sbInfoGenerale);
			} else {
				insAz = new InsertAzienda("Inserimento Azienda nel DB", trans, user,
						processorbase + "insertAzienda.xml", sbInfoGenerale);
			}
			validator.addProcessor(insAz);

			// Controlli sulle autorizzazioni
			validator.addProcessor(new ControlloPermessi("Autorizzazione per impatti", trans));
			// Processore per il controllo dell'esistenza di movimenti simili a quello in inserimento
			// controllo dei dati sensibili del lavoratore
			validator.addProcessor(new ProcControlloMbCmEtaLav("Controllo dati lavoratore", trans));
			validator.addProcessor(new ControlloMovimentoSimile("Controllo movimenti simili", trans, sbInfoGenerale));
			// controllo sul tipo di assunzione
			validator.addProcessor(new ControlloTipoAssunzione("Controllo tipo assunzione", trans));
			// Processore che controlla i dati del movimento.
			validator.addProcessor(new ControllaMovimenti(sbInfoGenerale, trans, user));
			// Processore per ulteriori controlli che di solito sono svolti nella jsp
			validator.addProcessor(new CrossController("Controllore Incrociato"));
			// Processore per l'esecuzione degli impatti
			RecordProcessor eseguiImpatti = new EseguiImpatti("Esecuzione impatti", sbInfoGenerale, trans, user);
			validator.addProcessor(eseguiImpatti);
			// Inserimento Movimento
			validator.addProcessor(new InsertData("Inserimento Movimento", trans, processorbase + "insertMovimento.xml",
					"INSERT_MOVIMENTO", user));
			// Processore per l'inserimento in am_movimento_missione delle info relative alla missione
			validator.addProcessor(new InsertDatiMissione(user, trans, sbInfoGenerale));
			// Processore per l'inserimento in am_movimento_apprendist
			validator.addProcessor(new InsertApprendistato(user, trans));
			// Processore per l'inserimento in am_movimento_apprendist delle info relative al tirocinio
			validator.addProcessor(new InsertTirocinio(user, trans, sbInfoGenerale));

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile configurare i processori per l'inserimento dell'avviamento veloce, "
							+ "controllare i file XML di configurazione. ",
					e);
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.ImportMov.ERR_INSERT_AVV_CEV), null, null, null);
		}

		_logger.debug(
				"DefaultScrollableDataResult::SmartScrollableDataResult: INIZIO DELLA PROCESSAZIONE DELL'AVVIAMENTO VELOCE");
		// Esegue la processazione dell'avviamento e ritorna il risultato
		// (faccio il log sia su file sia su DB)
		SourceBean result = null;
		ResultLogger resultLogger = new HtmlResultLogger();
		resultLogger.addChildResultLogger(new SingleResultLogger()); // (x)->Html->DB

		SourceBean resultAvv = validator.validaAvvDaCVE(recordAvv, resultLogger, trans);
		_logger.debug(
				"DefaultScrollableDataResult::SmartScrollableDataResult: ...FINE DELLA PROCESSAZIONE DELL'AVVIAMENTO VELOCE");
		// Esamino il risultato e lo notifico nei risultati della validazione della cessazione o proroga o
		// trasformazione
		if (validator.getStopAvvVeloce()) {
			// Warning e Stop durante la validazione dell'avviamento veloce. Devo andare avanti cercando di inserire il
			// movimento orfano.
			// Tale situazione si può verificare per gli avviamenti veloci per cui non c'è la competenza (Trasferimento
			// lavoratore)
			Warning w = new Warning(MessageCodes.ImportMov.ERR_INSERT_AVV_CEV,
					"Si cerca di inserire il movimento orfano.");
			warnings.add(w);
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested, true);
		} else {
			String resultCode = (String) resultAvv.getAttribute("RESULT");
			if ("ERROR".equalsIgnoreCase(resultCode)) {
				// Ho avuto errori, inserisco la risposta del processor che ha dato errore in quella del processor
				SourceBean errorProcessor = (SourceBean) resultAvv.getFilteredSourceBeanAttribute("PROCESSOR", "RESULT",
						"ERROR");
				Integer code = (Integer) errorProcessor.getAttribute("ERROR.code");
				String dettaglio = "";
				if (code.equals(new Integer(50133))) {
					dettaglio = "Impossibile inserire un nuovo movimento in quanto per il lavoratore e "
							+ "l'azienda risulta essere presente già un movimento valido nello stesso periodo "
							+ "che ha un movimento successivo collegato.";
				} else {
					dettaglio = (String) errorProcessor.getAttribute("ERROR.messagecode");
					if (errorProcessor.containsAttribute("ERROR.dettaglio")) {
						dettaglio += " " + (String) errorProcessor.getAttribute("ERROR.dettaglio");
					}
				}
				result = ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_INSERT_AVV_CEV), dettaglio, null, null);
				// Se ho confirm o alert le riporto nella risposta all'utente (ma solo in validazione manuale)
				if ("valida".equalsIgnoreCase((String) record.get("CONTEXT"))
						|| "validaArchivio".equalsIgnoreCase((String) record.get("CONTEXT"))) {
					if (errorProcessor.containsAttribute("ALERT")) {
						result.setAttribute((SourceBean) errorProcessor.getAttribute("ALERT"));
					}
					if (errorProcessor.containsAttribute("CONFIRM")) {
						result.setAttribute((SourceBean) errorProcessor.getAttribute("CONFIRM"));
					}
				}
				return result;
			} else {
				// Map per l'inserimento del documento di avviamento veloce(appena inserito)
				record.put("MAPCVE", recordAvv);
				// Imposto nella Map della cessazione(proroga o trasformazione) che ho un precedente
				// e che si tratta del movimento appena inserito
				record.put("COLLEGATO", "precedente");
				record.put("PRGMOVIMENTOPREC", recordAvv.get("PRGMOVIMENTO"));

				// SourceBean sb = (SourceBean) resultAvv.getAttribute("");
				Vector warnProc = resultAvv.getAttributeAsVector("PROCESSOR");
				String dettaglio = "";

				// se ci sono warning sull'utilizzo della prima unità aziendale del comune
				// devo propagarli al movimento di cessazione(proroga o trasformazione)
				for (int i = 0; (i < warnProc.size()); i++) {
					SourceBean pr = (SourceBean) warnProc.elementAt(i);
					SourceBean warn = null;
					try {
						warn = (SourceBean) pr.getAttribute("WARNING");
					} catch (Exception e) {
						warn = null;
					}
					if (warn != null) {
						String code = StringUtils.getAttributeStrNotNull(warn, "code");
						if (code.equals("50368")) {
							warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA, ""));
							// result = ProcessorsUtils.createResponse(name, classname,null, "", warnings, null);
						}
						if (code.equals("50369")) {
							warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA_UTIL, ""));
							// result = ProcessorsUtils.createResponse(name, classname, null, "", warnings, null);
						}
					}
				}
				if (warnings != null) {
					result = ProcessorsUtils.createResponse(name, classname, null, "", warnings, null);
				}
				return result;
			}
		}
	}

	/**
	 * Recupera il movimento di avviamento da validare, ritorna la Map recupertata o null se ci sono problemi
	 */
	private Map getMapAvv(Map record) {
		BigDecimal prgMovimentoAppCVE = (BigDecimal) record.get("PRGMOVIMENTOAPPCVE");
		if (prgMovimentoAppCVE == null)
			return null;

		// Esecuzione Query di recupero
		Object[] args = new Object[1];
		args[0] = prgMovimentoAppCVE;

		// dati del movimento (cessazione, proroga o trasformazione)
		numOreSett = (BigDecimal) record.get("NUMORESETT");
		codOrario = (String) record.get("CODORARIO");
		flgAssPropria = (String) record.get("FLGASSPROPRIA");
		notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
		String codTipoMovDaVal = (record.get("CODTIPOMOV") != null) ? (String) record.get("CODTIPOMOV") : "";

		Object result = null;
		try {
			if ("validaArchivio".equalsIgnoreCase((String) record.get("CONTEXT"))) {
				result = trans.executeQuery("GET_MOVIMENTO_DA_ARCHIVIO", args, "SELECT");
			} else {
				result = trans.executeQuery("GET_MOVIMENTO_DA_APPOGGIO", args, "SELECT");
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile recuperare un'eventuale avviamento veloce con progressivo " + prgMovimentoAppCVE, e);
			return null;
		}

		// Controllo il risultato
		if (result instanceof SourceBean) {
			SourceBean row = (SourceBean) ((SourceBean) result).getAttribute("ROW");
			if (row != null) {
				// Ho un'avviamento, lo elaboro e aggiungo i campi manacanti
				try {
					Map avviamento = elabora(row, codTipoMovDaVal);
					// aggiunta dei campi standard non valorizzati
					prevalorizzaCampiMancanti(avviamento, record);
					return avviamento;
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Impossibile elaborare l'avviamento veloce con progressivo " + prgMovimentoAppCVE, e);
					return null;
				}
			} else {
				// Non ce l'ho, ritono null...
				return null;
			}
		} else if (result instanceof Exception) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile recuperare un'eventuale avviamento veloce con progressivo " + prgMovimentoAppCVE,
					(Exception) result);
			return null;
		} else {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile recuperare un'eventuale avviamento veloce con progressivo " + prgMovimentoAppCVE,
					(Exception) result);
			return null;
		}
	}

	/**
	 * Questo metodo prevalorizza i campi mancanti nell'avviamento ai valori di default
	 */
	private void prevalorizzaCampiMancanti(Map avviamento, Map movimentoNew) throws Exception {
		String codTipoMovNew = movimentoNew.containsKey("CODTIPOMOV") ? movimentoNew.get("CODTIPOMOV").toString() : "";
		String codTipoTrasf = movimentoNew.containsKey("CODTIPOTRASF") ? movimentoNew.get("CODTIPOTRASF").toString()
				: "";

		// Se questi campi non sono presenti nell'avviamento debbono essere prevalorizzati a valori fissi
		if ((!avviamento.containsKey("STRMATRICOLA") || "".equals(avviamento.get("STRMATRICOLA")))
				&& (movimentoNew.containsKey("CODAZTIPOAZIENDA")
						&& movimentoNew.get("CODAZTIPOAZIENDA").toString().equalsIgnoreCase("INT") && notAssPropria)) {
			if (movimentoNew.get("STRMATRICOLA") != null) {
				avviamento.put("STRMATRICOLA", movimentoNew.get("STRMATRICOLA"));
			}
		}

		if (codTipoMovNew.equalsIgnoreCase("TRA") || codTipoMovNew.equalsIgnoreCase("PRO")) {
			if (!avviamento.containsKey("CODORARIO") || "".equals(avviamento.get("CODORARIO"))) {
				if (codOrario != null) {
					avviamento.put("CODORARIO", codOrario);
				}
			}
		} else {
			if (codOrario == null) {
				if (!avviamento.containsKey("CODORARIO") || "".equals(avviamento.get("CODORARIO"))) {
					avviamento.put("CODORARIO", "F"); // Tempo pieno
				}
			} else {
				avviamento.put("CODORARIO", codOrario);
				if (codOrario.equalsIgnoreCase("P") || codOrario.equalsIgnoreCase("V")
						|| codOrario.equalsIgnoreCase("M")) {
					avviamento.put("NUMORESETT", numOreSett);
				}

			}
		}

		// Se questi campi non sono presenti nell'avviamento debbono essere ripresi dal movimento di partenza (se
		// presenti)
		if (codTipoMovNew.equalsIgnoreCase("TRA") || codTipoMovNew.equalsIgnoreCase("PRO")) {
			if (!avviamento.containsKey("CODTIPOASS") || "".equals(avviamento.get("CODTIPOASS"))) {
				if (movimentoNew.containsKey("CODTIPOASS") && !"".equals(movimentoNew.get("CODTIPOASS"))) {
					avviamento.put("CODTIPOASS", movimentoNew.get("CODTIPOASS"));
				}
			}
		} else {
			if (movimentoNew.containsKey("CODTIPOASS") && !"".equals(movimentoNew.get("CODTIPOASS"))) {
				avviamento.put("CODTIPOASS", movimentoNew.get("CODTIPOASS"));
			}
		}

		if (codTipoMovNew.equalsIgnoreCase("TRA") || codTipoMovNew.equalsIgnoreCase("PRO")) {
			if (!avviamento.containsKey("CODMONOTEMPO") || "".equals(avviamento.get("CODMONOTEMPO"))) {
				if (movimentoNew.containsKey("CODMONOTEMPO") && !"".equals(movimentoNew.get("CODMONOTEMPO"))) {
					avviamento.put("CODMONOTEMPO", movimentoNew.get("CODMONOTEMPO"));
				}
			}
			if (avviamento.get("CODMONOTEMPO") != null
					&& avviamento.get("CODMONOTEMPO").toString().equalsIgnoreCase("D")) {
				if (codTipoMovNew.equalsIgnoreCase("TRA")) {
					if (movimentoNew.containsKey("DATFINEMOV")
							&& !movimentoNew.get("DATFINEMOV").toString().equals("")) {
						if (!avviamento.containsKey("DATFINEMOV")
								|| avviamento.get("DATFINEMOV").toString().equals("")) {
							avviamento.put("DATFINEMOV", movimentoNew.get("DATFINEMOV"));
						}
					}
				} else {
					if (movimentoNew.containsKey("DATINIZIOMOV")
							&& !movimentoNew.get("DATINIZIOMOV").toString().equals("")) {
						if (!avviamento.containsKey("DATFINEMOV")
								|| avviamento.get("DATFINEMOV").toString().equals("")) {
							String dataFineAvvVeloce = DateUtils
									.giornoPrecedente(movimentoNew.get("DATINIZIOMOV").toString());
							avviamento.put("DATFINEMOV", dataFineAvvVeloce);
						}
					}
				}
			}
			if (codTipoMovNew.equalsIgnoreCase("TRA")
					&& codTipoTrasf.equalsIgnoreCase(TipoTrasfEnum.PROSECUZIONE_PERIODO_FORMATIVO.getCodice())) {
				if (movimentoNew.containsKey("DATINIZIOMOV")
						&& !movimentoNew.get("DATINIZIOMOV").toString().equals("")) {
					if (!avviamento.containsKey("DATFINEPF") || avviamento.get("DATFINEPF").toString().equals("")) {
						String dataFinePFormativoAvv = DateUtils
								.giornoPrecedente(movimentoNew.get("DATINIZIOMOV").toString());
						avviamento.put("DATFINEPF", dataFinePFormativoAvv);
					}
				}
			}
		} else {
			if (movimentoNew.containsKey("CODMONOTEMPO")) {
				if (!"".equals(movimentoNew.get("CODMONOTEMPO"))) {
					avviamento.put("CODMONOTEMPO", movimentoNew.get("CODMONOTEMPO"));
				}
				// Tempo indeterminato
				if (CodMonoTempoEnum.INDETERMINATO.getCodice()
						.equalsIgnoreCase(movimentoNew.get("CODMONOTEMPO").toString())) {
					if (avviamento.containsKey("DATFINEMOV")) {
						avviamento.remove("DATFINEMOV");
					}
				}
			}
		}

		// Se questi campi non sono presenti nell'avviamento debbono essere ripresi dal movimento di partenza (se
		// presenti)
		if ((!avviamento.containsKey("CODMANSIONE") || "".equals(avviamento.get("CODMANSIONE")))
				&& (movimentoNew.containsKey("CODMANSIONE") && !"".equals(movimentoNew.get("CODMANSIONE")))) {
			avviamento.put("CODMANSIONE", movimentoNew.get("CODMANSIONE"));
		}

		// il controllo del livello è stato modificato per poter gestire
		// anche quei movimenti di CES in agricoltura che hanno valorizzato non il campo
		// NUMLIVELLO ma il campo CODLIVELLOAGR
		if ((!avviamento.containsKey("NUMLIVELLO") || "".equals(avviamento.get("NUMLIVELLO")))
				&& (movimentoNew.containsKey("NUMLIVELLO") && !"".equals(movimentoNew.get("NUMLIVELLO")))) {
			avviamento.put("NUMLIVELLO", movimentoNew.get("NUMLIVELLO"));
		} else if (avviamento.containsKey("CODLIVELLOAGR") || "".equals(avviamento.get("CODLIVELLOAGR"))) {
			avviamento.put("NUMLIVELLO", avviamento.get("CODLIVELLOAGR"));
		} else if (movimentoNew.containsKey("CODLIVELLOAGR") || "".equals(movimentoNew.get("CODLIVELLOAGR"))) {
			avviamento.put("NUMLIVELLO", movimentoNew.get("CODLIVELLOAGR"));
		}

		// I seguenti valori sono fissi, stato atto
		avviamento.put("CODSTATOATTO", "PR");// Protocollatao (non sarebbe melio PA?!?!? mha...)
		// codMotivoAnnullamento
		String motivoAvviamentoVeloce = "CVE";
		if (codTipoMovNew.equalsIgnoreCase("PRO")) {
			motivoAvviamentoVeloce = "PVE";
		} else {
			if (codTipoMovNew.equalsIgnoreCase("TRA")) {
				motivoAvviamentoVeloce = "TVE";
			}
		}
		avviamento.put("CODMOTANNULLAMENTO", motivoAvviamentoVeloce);// avviamento veloce da Cessazione o Proroga o
																		// Trasformazionbe -> (CVE, PVE, TVE)
		avviamento.put("COLLEGATO", "nessuno");// L'avviamento non è collegato a nulla

		if (codTipoMovNew.equalsIgnoreCase("TRA") || codTipoMovNew.equalsIgnoreCase("PRO")) {
			if (!avviamento.containsKey("CODCCNL") || "".equals(avviamento.get("CODCCNL"))) {
				if (movimentoNew.containsKey("CODCCNL") && !"".equals(movimentoNew.get("CODCCNL"))) {
					avviamento.put("CODCCNL", movimentoNew.get("CODCCNL"));
				} else {
					if (avviamento.get("CODAZCCNL") != null) {
						avviamento.put("CODCCNL", avviamento.get("CODAZCCNL")); // prevalorizzo sulla base di quello
																				// dell'azienda
					}
				}
			}
		} else {
			if ((movimentoNew.containsKey("CODCCNL") && !"".equals(movimentoNew.get("CODCCNL")))) {
				avviamento.put("CODCCNL", movimentoNew.get("CODCCNL"));
			} else {
				if (avviamento.get("CODAZCCNL") != null) {
					avviamento.put("CODCCNL", avviamento.get("CODAZCCNL")); // prevalorizzo sulla base di quello
																			// dell'azienda
				}
			}
		}

		avviamento.put("DATAINIZIOAVV", avviamento.get("DATINIZIOMOV")); // Imposto la data di inizio rapporto
		avviamento.put("DATINIZIOMOVPREC", avviamento.get("DATINIZIOMOV"));

		// I seguenti valori debbono essere ripresi dal record del movimento di partenza (se presenti)
		if (movimentoNew.containsKey("CONTEXT"))
			avviamento.put("CONTEXT", movimentoNew.get("CONTEXT"));
		if ((codTipoMovNew.equalsIgnoreCase("CES")) || (codTipoMovNew.equalsIgnoreCase("PRO"))
				|| (codTipoMovNew.equalsIgnoreCase("TRA") && !codTipoTrasf.equalsIgnoreCase("TL"))) {
			if (movimentoNew.containsKey("CONTEXT"))
				avviamento.put("CONTEXT", movimentoNew.get("CONTEXT"));
			if (movimentoNew.containsKey("PRGAZIENDA"))
				avviamento.put("PRGAZIENDA", movimentoNew.get("PRGAZIENDA"));
			if (movimentoNew.containsKey("PRGUNITAPRODUTTIVA")) {
				avviamento.put("PRGUNITA", movimentoNew.get("PRGUNITAPRODUTTIVA"));
				// bisogna vedere se il field nella map PRGUNITAPRODUTTIVA è necessario o meno
				avviamento.put("PRGUNITAPRODUTTIVA", movimentoNew.get("PRGUNITAPRODUTTIVA"));
			}
			if (movimentoNew.containsKey("PRGSEDELEGALE")) {
				avviamento.put("PRGSEDELEGALE", movimentoNew.get("PRGSEDELEGALE"));
			}
		}
		if (movimentoNew.containsKey("PRGAZIENDAUTIL"))
			avviamento.put("PRGAZIENDAUTIL", movimentoNew.get("PRGAZIENDAUTIL"));
		if (movimentoNew.containsKey("PRGUNITAUTIL"))
			avviamento.put("PRGUNITAUTIL", movimentoNew.get("PRGUNITAUTIL"));
		if (movimentoNew.containsKey("CDNLAVORATORE"))
			avviamento.put("CDNLAVORATORE", movimentoNew.get("CDNLAVORATORE"));
		if (movimentoNew.containsKey("CODCPI"))
			avviamento.put("CODCPI", movimentoNew.get("CODCPI"));
		if (movimentoNew.containsKey("CODCPILAV"))
			avviamento.put("CODCPILAV", movimentoNew.get("CODCPILAV"));
		if (movimentoNew.containsKey("NUMPROTOCOLLO"))
			avviamento.put("NUMPROTOCOLLO", movimentoNew.get("NUMPROTOCOLLO"));
		if (movimentoNew.containsKey("KLOCKPROT"))
			avviamento.put("KLOCKPROT", movimentoNew.get("KLOCKPROT"));
		if (movimentoNew.containsKey("STRENTERILASCIO"))
			avviamento.put("STRENTERILASCIO", movimentoNew.get("STRENTERILASCIO"));
		if (movimentoNew.containsKey("NUMANNOPROT"))
			avviamento.put("NUMANNOPROT", movimentoNew.get("NUMANNOPROT"));
		if (movimentoNew.containsKey("DATAPROT"))
			avviamento.put("DATAPROT", movimentoNew.get("DATAPROT"));
		if (movimentoNew.containsKey("ORAPROT"))
			avviamento.put("ORAPROT", movimentoNew.get("ORAPROT"));
		if (movimentoNew.containsKey("TIPOPROT"))
			avviamento.put("TIPOPROT", movimentoNew.get("TIPOPROT"));
		if (movimentoNew.containsKey("numProtIniziale"))
			avviamento.put("numProtIniziale", movimentoNew.get("numProtIniziale"));
		if (movimentoNew.containsKey("CONFERMA_CONTROLLO_MOV_SIMILI"))
			avviamento.put("CONFERMA_CONTROLLO_MOV_SIMILI", movimentoNew.get("CONFERMA_CONTROLLO_MOV_SIMILI"));

		if (checkForzaValidazione) {
			if (!avviamento.containsKey("NUMLIVELLO") || avviamento.get("NUMLIVELLO").equals("")) {
				avviamento.put("NUMLIVELLO", "--");
			}
			if (!avviamento.containsKey("CODMANSIONE") || avviamento.get("CODMANSIONE").equals("")) {
				avviamento.put("CODMANSIONE", "NT");
			}
		}
		// PRGMOVIMENTORETTASSCVE è valorizzato nel caso di rettifica di una comunicazione precedente
		// e quando la comunicazione da rettificare è una cessazione (oppure proroga o trasformazione)
		if (movimentoNew.containsKey("PRGMOVIMENTORETTASSCVE"))
			avviamento.put("PRGMOVIMENTORETT", movimentoNew.get("PRGMOVIMENTORETTASSCVE"));
	}

	/**
	 * Elabora il movimento ripescato dalla tabella di appoggio e lo trasforma da SourceBean a Map.
	 */
	private Map elabora(SourceBean recordAppoggio, String codTipoMovDaVal) throws Exception {
		// recordDataPVE sarà valorizzato solo nel caso di validazione manuale (getRequest() != null)
		// e nel caso di avviamento veloce da proroga o trasformazione. In tutti gli altri casi,
		// validazione massiva (getRequest() = null) e avviamento veloce da cessazione, recordDataPVE sarà null.
		// Il numero di record di recordDataPVE sarà quindi = 1 (riempito solo nel caso di validazione manuale)
		Map recordDataPVE = null;
		if ((getRequest() != null)
				&& (codTipoMovDaVal.equalsIgnoreCase("PRO") || codTipoMovDaVal.equalsIgnoreCase("TRA"))) {
			String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
					+ File.separator + "import" + File.separator;
			String configFileName = configbase + "ManualValidationReadFieldsPTVE.xml";
			RecordExtractor extr = new ManualExtractor(this.req, null, null, configFileName);
			if (extr.hasNext()) {
				recordDataPVE = (Map) extr.next();
			}
		}
		/** Formattatore di date */
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Hashtable recordTable = new Hashtable();

		// estraggo gli attributi dal SourceBean e popolo la map
		Vector v = recordAppoggio.getContainedAttributes();
		for (int i = 0; i < v.size(); i++) {
			SourceBeanAttribute attribute = (SourceBeanAttribute) v.get(i);
			// Ritrovo il nome della colonna
			String colName = attribute.getKey();
			// Ritrovo il dato associato
			Object colValue = null;
			// Se non è nullo lo inserisco in tabella
			if (recordDataPVE != null) {
				if (recordDataPVE.containsKey(colName)) {
					colValue = recordDataPVE.get(colName);
				}
			}
			if (colValue == null) {
				colValue = attribute.getValue();
			}
			if ((colName != null) && (colValue != null)) {
				// Se è una data la riformatto a stringa DD/MM/YYYY, è necessario per come sono stati
				// implementati i controlli
				if (colValue instanceof java.util.Date) {
					colValue = formatter.format(colValue);
				}
				recordTable.put(colName, colValue);
			}
		}

		return recordTable;
	}

	public void setRequest(SourceBean reqCurr) {
		this.req = reqCurr;
	}

	public SourceBean getRequest() {
		return (this.req);
	}

}