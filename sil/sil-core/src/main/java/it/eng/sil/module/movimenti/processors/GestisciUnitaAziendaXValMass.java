/*
 * Creato il 5-apr-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti.processors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * @author giuliani
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class GestisciUnitaAziendaXValMass implements RecordProcessor {

	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di insert */
	private TransactionQueryExecutor trans;

	/** Processori per l'aggiornamento dei dati */
	private RecordProcessor updateAzienda;
	private RecordProcessor updateUnitaProd;
	private RecordProcessor updateSedeLegale;
	private RecordProcessor updateAziendaUtil;
	private RecordProcessor updateUnitaUtil;
	private boolean checkForzaValidazione = false;

	public GestisciUnitaAziendaXValMass(String name, TransactionQueryExecutor transexec, BigDecimal user,
			String configFileName, boolean flagForzaValidazione) throws SAXException, FileNotFoundException,
			IOException, ParserConfigurationException, NullPointerException, SQLException {
		this.name = name;
		this.trans = transexec;
		if (user == null) {
			throw new NullPointerException("Identificatore utente nullo");
		}
		updateUnitaProd = new UpdateUnitaAzienda("Aggiornamento Unità Produttiva", transexec, configFileName,
				"UNITA_PRODUTTIVA", user);
		updateUnitaUtil = new UpdateUnitaAzienda("Aggiornamento Unità Utilizzatrice", transexec, configFileName,
				"UNITA_UTIL", user);
		this.checkForzaValidazione = flagForzaValidazione;
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

		/*
		 * ============================================================================================================
		 * Sezione inerente l'unità produttiva
		 * ============================================================================================================
		 */
		// In validazione Massiva se sono in AVVIAMENTO allora DEVO anche
		// individuare l'unità produttiva,
		// per gli altri tipi di movimento non è necessario l'importante è avere
		// indivuduato la testata.
		// L'unità verrà poi repertita dal movimento precedente. Questo non è
		// vero se sto validando un
		// movimento che non ha movimento precedente
		// Controllo che i parametri necessari per le query di ricerca ci siano
		String codfisc = (String) record.get("STRAZCODICEFISCALE");
		String indirUnitaAz = (String) record.get("STRUAINDIRIZZO");
		String codComUnitaAz = (String) record.get("CODUACOM");
		String ragSocAz = (String) record.get("STRAZRAGIONESOCIALE");
		String pIvaAz = (String) record.get("STRAZPARTITAIVA");
		BigDecimal prgAz = (BigDecimal) record.get("PRGAZIENDA");
		BigDecimal prgUnita = (BigDecimal) record.get("PRGUNITAPRODUTTIVA");
		ArrayList primaUnitaComune = null;
		if (prgUnita == null) {
			// Cerco l'unita produttiva
			Vector prgUnitaProdList = null;
			try {
				prgUnitaProdList = ProcessorsUtils.findUnita((BigDecimal) record.get("PRGAZIENDA"), codComUnitaAz,
						trans);
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA), "Impossibile cercare l'unita produttiva",
						warnings, nested);
			}
			// Devo agire in base al numero di unita produttive ripescate
			if (prgUnitaProdList.size() >= 1) {
				// Ho trovato una o più unita aziendali, controllo quante
				// matchano l'indirizzo passato
				ArrayList prgUnitaProdCheckedList = null;
				try {
					prgUnitaProdCheckedList = ProcessorsUtils.checkIndirizzo(prgUnitaProdList, indirUnitaAz);
				} catch (Exception e) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
							"Impossibile filtrare l'unita produttiva in base all'indirizzo", warnings, nested);
				}
				// Devo agire in base al numero di unita produttive ripescate
				if (prgUnitaProdCheckedList.size() == 1) {
					// ho individuato univocamente l'unita aziendale, la riporto
					// nel record
					record.put("PRGUNITAPRODUTTIVA", prgUnitaProdCheckedList.get(0));
					// Aggiorno i dati dell'unita produttiva
					SourceBean updateUnitaResult = updateUnitaProd.processRecord(record);
					if (updateUnitaResult != null) {
						nested.add(updateUnitaResult);
						if (ProcessorsUtils.isError(updateUnitaResult)) {
							return ProcessorsUtils
									.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
											"Impossibile aggiornare i dati dell'unita produttiva con indirizzo '"
													+ indirUnitaAz + "' dell'azienda '" + ragSocAz + "' nel DB.",
											warnings, nested);
						}
					}
				} else {
					if (prgUnitaProdCheckedList.size() == 0) {
						// ho individuato l'unita aziendale per comune,
						// si prende per default la prima unità produttiva dello
						// stesso comune
						primaUnitaComune = ProcessorsUtils.findPrimaUnita(prgUnitaProdList);
						record.put("PRGUNITAPRODUTTIVA", primaUnitaComune.get(0));
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA, ""));
					} else {
						// Ho più di una unita produttiva con stesso indirizzo,
						// nel caso di forzatura prendo
						// la prima unità trovata
						if (checkForzaValidazione) {
							record.put("PRGUNITAPRODUTTIVA", prgUnitaProdCheckedList.get(0));
							warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA_INDIRIZZO, ""));
							// aggiorno le note del movimento
							String notaModifica = "<li>E' stata trovata più di una unità aziendale compatibile "
									+ "con i dati del movimento. Il sistema ha automaticamente inserito la prima. </li>";
							String strNote = (String) record.get("STRNOTE");
							if (strNote != null) {
								strNote = strNote + notaModifica;
							} else {
								strNote = notaModifica;
							}
							record.put("STRNOTE", strNote);
						}
					}
				}
			}
		}
		/*
		 * ============================================================================================================
		 * FINE (Sezione inerente l'unità produttiva)
		 * ============================================================================================================
		 */

		/*
		 * ============================================================================================================
		 * Sezione inerente l'unità utilizzatrice
		 * ============================================================================================================
		 */

		// Se non si tratta di un'assunzione propria controllo che l'azienda
		// utilizzatrice sia valorizzata
		String flgAssPropria = (String) record.get("FLGASSPROPRIA");
		boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
		String codTipoAzienda = record.containsKey("CODAZTIPOAZIENDA") ? record.get("CODAZTIPOAZIENDA").toString() : "";
		if (codTipoAzienda.equalsIgnoreCase("INT")) {
			if (notAssPropria) {
				BigDecimal prgAzUtil = (BigDecimal) record.get("PRGAZIENDAUTIL");
				BigDecimal prgUnitaUtil = (BigDecimal) record.get("PRGUNITAUTIL");
				if (prgUnitaUtil == null) {
					String codComUnitaAzUtil = (String) record.get("CODUAINTCOM");
					String indirUnitaAzUtil = (String) record.get("STRUAINTINDIRIZZO");
					String ragSocAzUtil = (String) record.get("STRAZINTRAGIONESOCIALE");
					// Cerco l'unita produttiva dell'azienda utilizzatrice
					Vector prgUnitaUtilProdList = null;
					try {
						prgUnitaUtilProdList = ProcessorsUtils.findUnita((BigDecimal) record.get("PRGAZIENDAUTIL"),
								codComUnitaAzUtil, trans);
					} catch (Exception e) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
								"Impossibile cercare l'unita produttiva dell'azienda utilizzatrice", warnings, nested);
					}
					// Devo agire in base al numero di unita produttive
					// ripescate
					if (prgUnitaUtilProdList.size() >= 1) {
						// Ho trovato una o più unita aziendali, controllo
						// quante matchano l'indirizzo passato
						ArrayList prgUnitaProdUtilCheckedList = null;
						try {
							prgUnitaProdUtilCheckedList = ProcessorsUtils.checkIndirizzo(prgUnitaUtilProdList,
									indirUnitaAzUtil);
						} catch (Exception e) {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
									"Impossibile filtrare l'unita produttiva dell'azienda utilizzatrice in base all'indirizzo",
									warnings, nested);
						}
						// Devo agire in base al numero di unita produttive
						// ripescate
						if (prgUnitaProdUtilCheckedList.size() == 1) {
							// ho individuato univocamente l'unita aziendale
							// dell'azienda utilizzatrice e la riporto nel
							// record
							record.put("PRGUNITAUTIL", prgUnitaProdUtilCheckedList.get(0));
							// Aggiorno i dati dell'unita produttiva
							SourceBean updateUnitaUtilResult = updateUnitaUtil.processRecord(record);
							if (updateUnitaUtilResult != null) {
								nested.add(updateUnitaUtilResult);
								if (ProcessorsUtils.isError(updateUnitaUtilResult)) {
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
											"Impossibile aggiornare i dati dell'unita produttiva con indirizzo '"
													+ indirUnitaAzUtil + "' dell'azienda '" + ragSocAzUtil
													+ "' nel DB.",
											warnings, nested);
								}
							}
						} else {
							if (prgUnitaProdUtilCheckedList.size() == 0) {
								// ho individuato l'unita aziendale per comune,
								// si prende per default la prima unità
								// produttiva dello stesso comune
								primaUnitaComune = ProcessorsUtils.findPrimaUnita(prgUnitaUtilProdList);
								record.put("PRGUNITAUTIL", primaUnitaComune.get(0));
								warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA_UTIL, ""));
							} else {
								// Ho più di una unita produttiva con stesso
								// indirizzo, nel caso di forzatura prendo
								// la prima unità trovata
								if (checkForzaValidazione) {
									record.put("PRGUNITAUTIL", prgUnitaProdUtilCheckedList.get(0));
									warnings.add(
											new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA_UTIL_INDIRIZZO, ""));
									// aggiorno le note del movimento
									String notaModifica = "<li>E' stata trovata più di una unità aziendale relativa all' azienda "
											+ "utilizzatrice compatibile con i dati del movimento. "
											+ "Il sistema ha automaticamente inserito la prima. </li>";
									String strNote = (String) record.get("STRNOTE");
									if (strNote != null) {
										strNote = strNote + notaModifica;
									} else {
										strNote = notaModifica;
									}
									record.put("STRNOTE", strNote);
								}
							}
						}
					}
				}
			}
		}

		/*
		 * ============================================================================================================
		 * FINE (Sezione inerente l'unità utilizzatrice)
		 * ============================================================================================================
		 */

		// Se ho warning o risultati annidati li inserisco nella risposta,
		// altrimenti non ritorno nulla.
		if ((warnings.size() > 0) || (nested.size() > 0)) {
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		} else {
			return null;
		}
	}

	public SourceBean processRecord(Map record, BigDecimal prgAzienda, String ragSocAz, String codComuneAz,
			String indirizzoAz, Vector vettPrgUnita) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		ArrayList primaUnitaComune = null;

		/*
		 * ============================================================================================================
		 * Sezione inerente l'unità produttiva
		 * ============================================================================================================
		 */
		// Cerco l'unita produttiva
		Vector prgUnitaProdList = null;
		try {
			prgUnitaProdList = ProcessorsUtils.findUnita(prgAzienda, codComuneAz, trans);
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Impossibile cercare l'unita produttiva", warnings, nested);
		}
		// Devo agire in base al numero di unita produttive ripescate
		if (prgUnitaProdList.size() >= 1) {
			// Ho trovato una o più unita aziendali, controllo quante matchano
			// l'indirizzo passato
			ArrayList prgUnitaProdCheckedList = null;
			try {
				prgUnitaProdCheckedList = ProcessorsUtils.checkIndirizzo(prgUnitaProdList, indirizzoAz);
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
						"Impossibile filtrare l'unita produttiva in base all'indirizzo", warnings, nested);
			}
			// Devo agire in base al numero di unita produttive ripescate
			if (prgUnitaProdCheckedList.size() == 1) {
				// ho individuato univocamente l'unita aziendale, la riporto nel
				// record
				vettPrgUnita.add(0, (BigDecimal) prgUnitaProdCheckedList.get(0));
			} else {
				if (prgUnitaProdCheckedList.size() == 0) {
					// ho individuato l'unita aziendale per comune,
					// si prende per default la prima unità produttiva dello
					// stesso comune
					primaUnitaComune = ProcessorsUtils.findPrimaUnita(prgUnitaProdList);
					vettPrgUnita.add(0, (BigDecimal) primaUnitaComune.get(0));
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA, ""));
				} else {
					// Ho più di una unita produttiva con stesso indirizzo, nel
					// caso di forzatura prendo
					// la prima unità trovata
					if (checkForzaValidazione) {
						vettPrgUnita.add(0, (BigDecimal) prgUnitaProdCheckedList.get(0));
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA_INDIRIZZO, ""));
						// aggiorno le note del movimento
						String notaModifica = "<li>E' stata trovata più di una unità aziendale compatibile "
								+ "con i dati del movimento. Il sistema ha automaticamente inserito la prima. </li>";
						String strNote = (String) record.get("STRNOTE");
						if (strNote != null) {
							strNote = strNote + notaModifica;
						} else {
							strNote = notaModifica;
						}
						record.put("STRNOTE", strNote);
					}
				}
			}
		}
		/*
		 * ============================================================================================================
		 * FINE (Sezione inerente l'unità produttiva)
		 * ============================================================================================================
		 */

		/*
		 * ============================================================================================================
		 * Sezione inerente l'unità utilizzatrice
		 * ============================================================================================================
		 */

		// Se non si tratta di un'assunzione propria controllo che l'azienda
		// utilizzatrice sia valorizzata
		String flgAssPropria = (String) record.get("FLGASSPROPRIA");
		boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
		String codTipoAzienda = record.containsKey("CODAZTIPOAZIENDA") ? record.get("CODAZTIPOAZIENDA").toString() : "";
		if (codTipoAzienda.equalsIgnoreCase("INT")) {
			if (notAssPropria) {
				BigDecimal prgAzUtil = (BigDecimal) record.get("PRGAZIENDAUTIL");
				BigDecimal prgUnitaUtil = (BigDecimal) record.get("PRGUNITAUTIL");
				if (prgUnitaUtil == null) {
					String codComUnitaAzUtil = (String) record.get("CODUAINTCOM");
					String indirUnitaAzUtil = (String) record.get("STRUAINTINDIRIZZO");
					String ragSocAzUtil = (String) record.get("STRAZINTRAGIONESOCIALE");
					// Cerco l'unita produttiva dell'azienda utilizzatrice
					Vector prgUnitaUtilProdList = null;
					try {
						prgUnitaUtilProdList = ProcessorsUtils.findUnita((BigDecimal) record.get("PRGAZIENDAUTIL"),
								codComUnitaAzUtil, trans);
					} catch (Exception e) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
								"Impossibile cercare l'unita produttiva dell'azienda utilizzatrice", warnings, nested);
					}
					// Devo agire in base al numero di unita produttive
					// ripescate
					if (prgUnitaUtilProdList.size() >= 1) {
						// Ho trovato una o più unita aziendali, controllo
						// quante matchano l'indirizzo passato
						ArrayList prgUnitaProdUtilCheckedList = null;
						try {
							prgUnitaProdUtilCheckedList = ProcessorsUtils.checkIndirizzo(prgUnitaUtilProdList,
									indirUnitaAzUtil);
						} catch (Exception e) {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
									"Impossibile filtrare l'unita produttiva dell'azienda utilizzatrice in base all'indirizzo",
									warnings, nested);
						}
						// Devo agire in base al numero di unita produttive
						// ripescate
						if (prgUnitaProdUtilCheckedList.size() == 1) {
							// ho individuato univocamente l'unita aziendale
							// dell'azienda utilizzatrice e la riporto nel
							// record
							record.put("PRGUNITAUTIL", prgUnitaProdUtilCheckedList.get(0));
							// Aggiorno i dati dell'unita produttiva
							SourceBean updateUnitaUtilResult = updateUnitaUtil.processRecord(record);
							if (updateUnitaUtilResult != null) {
								nested.add(updateUnitaUtilResult);
								if (ProcessorsUtils.isError(updateUnitaUtilResult)) {
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
											"Impossibile aggiornare i dati dell'unita produttiva con indirizzo '"
													+ indirUnitaAzUtil + "' dell'azienda '" + ragSocAzUtil
													+ "' nel DB.",
											warnings, nested);
								}
							}
						} else {
							if (prgUnitaProdUtilCheckedList.size() == 0) {
								// ho individuato l'unita aziendale per comune,
								// si prende per default la prima unità
								// produttiva dello stesso comune
								primaUnitaComune = ProcessorsUtils.findPrimaUnita(prgUnitaUtilProdList);
								record.put("PRGUNITAUTIL", primaUnitaComune.get(0));
								warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA_UTIL, ""));
							} else {
								// Ho più di una unita produttiva con stesso
								// indirizzo, nel caso di forzatura prendo
								// la prima unità trovata
								if (checkForzaValidazione) {
									record.put("PRGUNITAUTIL", prgUnitaProdUtilCheckedList.get(0));
									warnings.add(
											new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA_UTIL_INDIRIZZO, ""));
									// aggiorno le note del movimento
									String notaModifica = "<li>E' stata trovata più di una unità aziendale relativa all' azienda "
											+ "utilizzatrice compatibile con i dati del movimento. "
											+ "Il sistema ha automaticamente inserito la prima. </li>";
									String strNote = (String) record.get("STRNOTE");
									if (strNote != null) {
										strNote = strNote + notaModifica;
									} else {
										strNote = notaModifica;
									}
									record.put("STRNOTE", strNote);
								}
							}
						}
					}
				}
			}
		}

		/*
		 * ============================================================================================================
		 * FINE (Sezione inerente l'unità utilizzatrice)
		 * ============================================================================================================
		 */

		// Se ho warning o risultati annidati li inserisco nella risposta,
		// altrimenti non ritorno nulla.
		if ((warnings.size() > 0) || (nested.size() > 0)) {
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		} else {
			return null;
		}
	}

}// End Class
