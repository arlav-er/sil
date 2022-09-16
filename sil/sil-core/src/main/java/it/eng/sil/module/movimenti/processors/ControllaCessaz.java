package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.movimenti.enumeration.CodiceVariazioneEnum;
import it.eng.sil.module.movimenti.extractor.ManualValidationFieldExtractor;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;

public class ControllaCessaz implements RecordProcessor {
	private String className;
	private String prc;
	private TransactionQueryExecutor trans;
	private SourceBean sbInfoGenerale = null;
	private int numGiorniElasticitaCess = 0;
	private boolean checkForzaValidazione = false;
	private boolean checkForzaModifiche = false;
	/** Identificatore utente */
	private BigDecimal userId;

	public ControllaCessaz(SourceBean sb, TransactionQueryExecutor transexec, BigDecimal user) {
		this.className = this.getClass().getName();
		this.prc = "Controlla Cessazione";
		this.trans = transexec;
		this.sbInfoGenerale = sb;
		this.userId = user;
		String strNumGgElasticitaCessazioni = "";

		if (this.sbInfoGenerale != null) {

			if (this.sbInfoGenerale.containsAttribute("NUMGGELASTICITACESSAZIONI")) {
				strNumGgElasticitaCessazioni = this.sbInfoGenerale.getAttribute("NUMGGELASTICITACESSAZIONI").toString();
			}

			if (this.sbInfoGenerale.containsAttribute("CHECKFORZAVALIDAZIONE")) {
				checkForzaValidazione = (this.sbInfoGenerale.getAttribute("CHECKFORZAVALIDAZIONE").toString() == "true"
						? true
						: false);
			}

			if (this.sbInfoGenerale.containsAttribute("CHECKFORZAMODIFICHE")) {
				checkForzaModifiche = (this.sbInfoGenerale.getAttribute("CHECKFORZAMODIFICHE").toString() == "true"
						? true
						: false);
			}
		}

		if (!strNumGgElasticitaCessazioni.equals("")) {
			numGiorniElasticitaCess = Integer.parseInt(strNumGgElasticitaCessazioni);
		}

	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		int giorno, mese, anno;
		Warning w = null;
		String dataInizioMissione = "";
		String dataFineContrattoSomm = "";
		ArrayList warnings = new ArrayList();
		ManualValidationFieldExtractor extractor = new ManualValidationFieldExtractor();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_GLOBALE),
					"Nessun dato inserito.", warnings, null);
		}

		String codTipoContratto = record.get("CODTIPOASS") != null ? (String) record.get("CODTIPOASS") : "";
		String context = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		// checkForzaValidazione = false se non sono in validazione massiva
		if (!context.equalsIgnoreCase("validazioneMassiva")) {
			checkForzaValidazione = false;
		}

		boolean comUnisomm = false;
		String flgAssPropria = extractor.estraiAssunzionePropria(record);
		boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
		String tipoAzienda = extractor.estraiTipoAzienda(record);
		if (tipoAzienda.equalsIgnoreCase("INT") && notAssPropria) {
			comUnisomm = true;
			dataInizioMissione = extractor.estraiInizioMissione(record);
			dataFineContrattoSomm = extractor.estraiFineContrattoSomm(record);
		}
		String codMvCessazione = (String) record.get("CODMVCESSAZIONE");

		if (codMvCessazione == null) {
			if (checkForzaValidazione) {
				String strNote = null;
				// modifica del motivo cessazione
				record.put("CODMVCESSAZIONE", "NT");
				// nota da aggiungere
				String notaAdd = "<li>Non è stato specificato il motivo della cessazione. Il sistema ha impostato automaticamente 'NT'.</li>";
				strNote = (String) record.get("STRNOTE");
				if (strNote != null) {
					strNote = strNote + notaAdd;
				} else {
					strNote = notaAdd;
				}
				record.put("STRNOTE", strNote);
			} else {
				if (tipoAzienda.equalsIgnoreCase("INT")) {
					w = new Warning(MessageCodes.ImportMov.ERR_MOTIVO_CESSAZIONE, "");
					warnings.add(w);
				} else {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_MOTIVO_CESSAZIONE),
							"Il campo 'Motivo Cessazione' non è stato valorizzato.", warnings, null);
				}
			}
		} else {
			if (codMvCessazione.equalsIgnoreCase("AC") || codMvCessazione.equalsIgnoreCase("AM")
					|| codMvCessazione.equalsIgnoreCase("AD") || codMvCessazione.equalsIgnoreCase("AR")) {
				if (!codTipoContratto.equals("")
						&& !codTipoContratto
								.equalsIgnoreCase(DeTipoContrattoConstant.APPRENDISTATO_QUALIFICA_DIPLOMA_PROFESSIONALE)
						&& !codTipoContratto.equalsIgnoreCase(
								DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE_O_CONTRATTO_DI_MESTIERE)
						&& !codTipoContratto
								.equalsIgnoreCase(DeTipoContrattoConstant.APPRENDISTATO_ALTA_FORMAZIONE_RICERCA)
						&& !codTipoContratto.equalsIgnoreCase(
								DeTipoContrattoConstant.APPRENDISTATO_QUALIFICA_DIPLOMA_PROFESSIONALE_IN_MOBILITA)
						&& !codTipoContratto.equalsIgnoreCase(
								DeTipoContrattoConstant.APPRENDISTATO_CONTRATTO_DI_MESTIERE_IN_MOBILITA)
						&& !codTipoContratto.equalsIgnoreCase(
								DeTipoContrattoConstant.APPRENDISTATO_ALTA_FORMAZIONE_RICERCA_IN_MOBILITA)
						&& !codTipoContratto.equalsIgnoreCase(
								DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE_O_MESTIERE_STAGIONALI)) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_MOTIVO_CES_APPRENDISTATO), "", warnings, null);
				}
			}
		}

		// Controllo data e tipo della cessazione
		String dataInizioCess = (String) record.get("DATINIZIOMOV");
		String codMonoTempo = (String) record.get("CODMONOTEMPO");
		String codMonoTempoMovPrec = record.get("CODMONOTEMPOMOVPREC") != null
				? (String) record.get("CODMONOTEMPOMOVPREC")
				: "";

		// controllo se ho CodMonoTempo
		if (codMonoTempo == null || codMonoTempo.equals("")) {
			return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.WAR_COD_TEMPO_CES),
					"", warnings, null);
		}

		// Controllo se ho la data di cessazione
		GregorianCalendar dataCessazione = null;
		if (dataInizioCess == null || dataInizioCess.equals("")) {
			return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.WAR_DATA_CES), "",
					warnings, null);
		} else {
			try {
				giorno = Integer.parseInt(dataInizioCess.substring(0, 2));
				mese = Integer.parseInt(dataInizioCess.substring(3, 5));
				anno = Integer.parseInt(dataInizioCess.substring(6, 10));
				dataCessazione = new GregorianCalendar(anno, (mese - 1), giorno);
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.WAR_DATA_CES),
						"", warnings, null);
			}
		}

		// La data di cessazione uguale o precedente alla data di fine movimento del mov. prec.
		// (se non è un inserimento scollegato)
		String collegato = (String) record.get("COLLEGATO");
		if (collegato != null && !collegato.equalsIgnoreCase("nessuno")) {
			String codContratto = (String) record.get("CODCONTRATTO");
			if (codContratto.equalsIgnoreCase(Properties.CONTRATTO_LAVORO_INTERMITTENTE)) {
				BigDecimal prgMovPrecedente = null;
				if (record.get("PRGMOVIMENTOPREC") != null) {
					prgMovPrecedente = new BigDecimal(record.get("PRGMOVIMENTOPREC").toString());
					Vector periodi = MovimentoBean.caricaPeriodiLavorativi(prgMovPrecedente, trans);
					if (periodi != null && periodi.size() > 0) {
						boolean exit = false;
						boolean riallineamentoPer = false;
						for (int i = 0; i < periodi.size() && !exit; i++) {
							SourceBean periodo = (SourceBean) periodi.get(i);
							String dataInizioPer = StringUtils.getAttributeStrNotNull(periodo, "DATAINIZIO");
							String dataFinePer = StringUtils.getAttributeStrNotNull(periodo, "DATAFINE");
							try {
								if (DateUtils.compare(dataFinePer, dataInizioCess) <= 0) {
									exit = true;
								} else {
									BigDecimal prgPeriodo = (BigDecimal) periodo.getAttribute("PRGPERIODOLAV");
									BigDecimal numkloPeriodo = (BigDecimal) periodo.getAttribute("NUMKLOPERIODOLAV");
									numkloPeriodo = numkloPeriodo.add(new BigDecimal(1));
									Boolean resInterm = null;
									if (DateUtils.compare(dataInizioPer, dataInizioCess) <= 0) {
										resInterm = MovimentoBean.aggiornaPeriodoLavorativo(prgPeriodo, numkloPeriodo,
												dataInizioPer, dataInizioCess, userId, trans);
										exit = true;
										riallineamentoPer = true;
									} else {
										// cancellazione periodo
										resInterm = MovimentoBean.cancellaPeriodoLavorativo(prgPeriodo, trans);
										riallineamentoPer = true;
									}
									if (resInterm == null || !resInterm.booleanValue()) {
										return ProcessorsUtils.createResponse(prc, className,
												new Integer(MessageCodes.ImportMov.ERR_GESTIONE_PERIODI_INTERMITTENTI),
												"", warnings, null);
									}
								}
							} catch (Exception e) {
								return ProcessorsUtils.createResponse(prc, className,
										new Integer(MessageCodes.ImportMov.ERR_GESTIONE_PERIODI_INTERMITTENTI), "",
										warnings, null);
							}
						}
						if (riallineamentoPer) {
							warnings.add(
									new Warning(MessageCodes.ImportMov.WAR_RIALLINEAMENTO_PERIODI_INTERMITTENTI, ""));
						}
					}
				}
			}

			String dataInizioMovPrec = (String) record.get("DATINIZIOMOVPREC");
			String dataFineMovPrec = (String) record.get("DATFINEMOVPREC");

			String codMvCessazionePrec = record.get("CODMVCESSAZIONEPREC") != null
					? (String) record.get("CODMVCESSAZIONEPREC")
					: "";
			if (codMvCessazionePrec.equalsIgnoreCase("SC")) {
				if (dataFineMovPrec == null || dataFineMovPrec.equals("")) {
					dataFineMovPrec = dataInizioCess;
					record.put("DATFINEMOVPREC", dataFineMovPrec);
				}

			}
			// controllo la data inizio del movimento precedente
			GregorianCalendar dataInizioMovPrecedente = null;
			if (dataInizioMovPrec == null || dataInizioMovPrec.equals("")) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_NO_DATA_INIZIOMOVPREC), "", warnings, null);
			} else {
				try {
					giorno = Integer.parseInt(dataInizioMovPrec.substring(0, 2));
					mese = Integer.parseInt(dataInizioMovPrec.substring(3, 5));
					anno = Integer.parseInt(dataInizioMovPrec.substring(6, 10));
					dataInizioMovPrecedente = new GregorianCalendar(anno, (mese - 1), giorno);
				} catch (Exception e) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_NO_DATA_INIZIOMOVPREC), "", warnings, null);
				}
			}

			if (!codMonoTempo.equalsIgnoreCase(codMonoTempoMovPrec)) {
				// record.put("codMonoTempo", codMonoTempoMovPrec);
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_TEMPO_MOV_PREC), "", warnings, null);
			}

			// controllo la data fine del movimento precedente se determinato
			GregorianCalendar dataFineMovPrecedente = null;
			if (codMonoTempo.equalsIgnoreCase("D") && (dataFineMovPrec == null || dataFineMovPrec.equals(""))) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_NO_DATA_FINEMOVPREC), "", warnings, null);
			} else if (codMonoTempo.equalsIgnoreCase("D")) {
				try {
					giorno = Integer.parseInt(dataFineMovPrec.substring(0, 2));
					mese = Integer.parseInt(dataFineMovPrec.substring(3, 5));
					anno = Integer.parseInt(dataFineMovPrec.substring(6, 10));
					dataFineMovPrecedente = new GregorianCalendar(anno, (mese - 1), giorno);
				} catch (Exception e) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_NO_DATA_FINEMOVPREC), "", warnings, null);
				}
			}
			// Controllo date per il tipo di tempo
			if (codMonoTempo.equalsIgnoreCase("D") && dataCessazione.after(dataFineMovPrecedente)) {
				String strDataFineMovPrec = dataFineMovPrec.substring(0, 2) + "/" + dataFineMovPrec.substring(3, 5)
						+ "/" + dataFineMovPrec.substring(6, 10);
				String strDataCessazione = dataInizioCess.substring(0, 2) + "/" + dataInizioCess.substring(3, 5) + "/"
						+ dataInizioCess.substring(6, 10);

				/*
				 * Modifica: se arriva una cessazione relativa al movimento avente il codtipocontratto = 'A.02.01',
				 * viene salvata anche se fuori tempo massimo e il mov precedente viene modificato.
				 */

				String codTipoMovPrec = record.get("CODTIPOMOVPREC") != null ? (String) record.get("CODTIPOMOVPREC")
						: "";
				int ggDiff = DateUtils.daysBetween(strDataFineMovPrec, strDataCessazione);
				boolean controlloElasticitaCes = true;
				if (codTipoMovPrec.equalsIgnoreCase("CES") && codMvCessazionePrec.equalsIgnoreCase("SC")) {
					controlloElasticitaCes = false;
				}
				if (controlloElasticitaCes && ggDiff > numGiorniElasticitaCess && !codTipoContratto.equals("A.02.01")
						&& !codTipoMovPrec.equals("AVV")) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_DATA_CESSAZIONE_DET), " GIORNI DI DIFF: " + ggDiff,
							warnings, null);
				} else {
					// devo aggiornare la datFineMov e datFineMovEffettiva del movimento precedente
					BigDecimal prgMovDaAggiornare = null;
					BigDecimal numkloMovPrec = null;
					if (record.get("PRGMOVIMENTOPREC") != null) {
						prgMovDaAggiornare = new BigDecimal(record.get("PRGMOVIMENTOPREC").toString());
					}
					if (record.get("NUMKLOMOVPREC") != null) {
						numkloMovPrec = new BigDecimal(record.get("NUMKLOMOVPREC").toString());
					}
					if (prgMovDaAggiornare != null && numkloMovPrec != null) {
						try {
							BigDecimal user = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
									.getAttribute("_CDUT_");
							numkloMovPrec = DBStore.aggiornaDataFineMov(prgMovDaAggiornare, strDataCessazione, user,
									numkloMovPrec, trans);
							record.put("NUMKLOMOVPREC", numkloMovPrec);
							record.put("DATFINEMOVPREC", strDataCessazione);
						} catch (Exception e) {
							return ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.StatoOccupazionale.ERRORE_AGG_DATA_FINE_MOV_PREC), "",
									warnings, null);
						}

						// DONA 14/02/2007: 3.4 Incongruenza date per cessazioni a TD
						// se esiste flag forzatura allora per il movimento bisogna inserire il campo STRNOTE
						// 11/07/2007 aggiunto flag FORZAMOD: se TRUE le modifiche vengono effettuate altrimenti NO
						if (checkForzaValidazione && checkForzaModifiche) {
							String notaMov = (String) record.get("STRNOTE");
							String strNota = "<li>Il movimento precedente collegato potrebbe non essere quello corretto.</li>";
							if (notaMov != null) {
								notaMov = notaMov + strNota;
							} else {
								notaMov = strNota;
							}

							String notaMovPrec = (String) record.get("STRNOTEMOVPREC");
							String strNotaPrec = "<li>E' stata modificata la data fine del movimento da "
									+ strDataCessazione + " a " + strDataFineMovPrec
									+ " per renderlo compatibile con la CES successiva collegata.</li>";
							if (notaMovPrec != null) {
								notaMovPrec = notaMovPrec + strNotaPrec;
							} else {
								notaMovPrec = strNotaPrec;
							}

							record.put("STRNOTE", notaMov);
							record.put("STRNOTEMOVPREC", notaMovPrec);
						}
					}
				}
			}

			if (codMonoTempo.equalsIgnoreCase("D") && dataCessazione.before(dataInizioMovPrecedente)) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_FINE_CESS_PREC_INIZIO), "", warnings, null);
			} else if (codMonoTempo.equalsIgnoreCase("I") && (dataCessazione.before(dataInizioMovPrecedente))) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_DATA_CESSAZIONE_IND), "", warnings, null);
			}
		}

		if (collegato != null && collegato.equalsIgnoreCase("nessuno") && codMonoTempo.equalsIgnoreCase("D")) {
			return ProcessorsUtils.createResponse(prc, className,
					new Integer(MessageCodes.ImportMov.ERR_CESS_ORFANA_O_VELOCE), "", warnings, null);
		}

		// Controlli nuovi decreto GENNAIO 2013
		if (comUnisomm) {
			try {
				if (!dataInizioCess.equals("") && !dataInizioMissione.equals("")
						&& DateUtils.compare(dataInizioCess, dataInizioMissione) < 0) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_DATA_CESSAZIONE_INIZIO_MIS), "", warnings, null);
				}
				if (!dataInizioCess.equals("") && !dataFineContrattoSomm.equals("")
						&& DateUtils.compare(dataInizioCess, dataFineContrattoSomm) > 0) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_DATA_CESSAZIONE_FINE_CONTRATTO_SOMM), "", warnings,
							null);
				}
				String codiceVariazione = record.get("CODVARIAZIONE") == null ? ""
						: record.get("CODVARIAZIONE").toString();
				if (CodiceVariazioneEnum.CESSAZIONE_MISSIONE.getCodice().equals(codiceVariazione)) {
					// devo solo inserire il record della missione in am_movimento_missione (data fine missione deve
					// essere data cessazione)
					Object prgMovimentoMissione = record.get("PRGMOVIMENTOPREC");
					if (prgMovimentoMissione != null) {
						InsertDatiMissione missione = new InsertDatiMissione(userId, trans, sbInfoGenerale);
						record.put("PRGMOVIMENTO", prgMovimentoMissione);
						// aggiornamento data fine missione
						record.remove("DATFINERAPLAV");
						record.put("DATFINERAPLAV", dataInizioCess);
						missione.processRecord(record);
						// Il record (la cessazione) non viene inserito in am_movimento, ma deve essere
						// cancellato dalla am_movimento_appoggio
						RemoveMovimentoAppoggio deleteMovValida = new RemoveMovimentoAppoggio(
								"Rimozione del record dalla tabella AM_MOVIMENTO_APPOGGIO", trans);
						deleteMovValida.processRecord(record);

						warnings.add(new Warning(MessageCodes.ImportMov.WAR_NEW_MISSIONE, ""));
						return ProcessorsUtils.createResponse(prc, className, null, null, warnings, null, true);
					} else {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_FIND_MOV_PREC), "Data cessazione non corretta",
								warnings, null);
					}
				}

			} catch (Exception ex1) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "Data cessazione non corretta", warnings,
						null);
			}
		}

		/*
		 * CONTROLLO SPOSTATO IN ControllaGen Nel caso di una Cessazione in agricoltura è obbligatorio indicare il
		 * numero dei giorni effettuati in agr. e la lavorazione. if( codTipoAss.equals("H.01.00") ){ if (
		 * (record.get("CODLAVORAZIONE").toString().equals("")) ||
		 * record.get("NUMGGEFFETTUATIAGR").toString().equals("")) { return ProcessorsUtils.createResponse(prc,
		 * className, new Integer(MessageCodes.ImportMov.ERR_LAV_NUMGGAGREFF_NULL), "", warnings, null); } }
		 */

		// Grado avviamneto
		// if ( record.get("CODGRADO") == null) {
		// if (checkForzaValidazione) {
		// String strNote = null;
		// modifica del grado (14 --> Generico)
		// record.put("CODGRADO", "14");
		// nota da aggiungere
		// String notaAdd = "<li>Non è stato specificato il grado. Il sistema ha impostato automaticamente
		// 'Generico'.</li>";
		// strNote = (String) record.get("STRNOTE");
		// if (strNote != null) {
		// strNote = strNote + notaAdd;
		// }
		// else {
		// strNote = notaAdd;
		// }
		// record.put("STRNOTE", strNote);
		// }
		// else {
		// return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_GRADO_AVV), "",
		// warnings, null);
		// }
		// }
		// Livello
		// DAVIDE 10/01/2007: il controllo del livello è stato modificato per poter gestire
		// anche quei movimenti di CES in agricoltura che hanno valorizzato non il campo
		// NUMLIVELLO ma il campo CODLIVELLOAGR
		if (record.get("NUMLIVELLO") == null) {
			if (record.get("CODLIVELLOAGR") == null) {
				if (checkForzaValidazione) {
					// modifica del livello
					record.put("NUMLIVELLO", "--");
				}
				/*
				 * else { return ProcessorsUtils.createResponse(prc, className, new
				 * Integer(MessageCodes.ImportMov.ERR_LIVELLO_NULLO), "", warnings, null); }
				 */
			} else {
				String livello = (String) record.get("CODLIVELLOAGR");
				record.put("NUMLIVELLO", livello);
			}
		}

		// DAVIDE 06/06/2007: Il tracciato unoco ha modificato la gestione dei girni EFFETTIVI in agricoltura, questi
		// sono sempre riportati nel campo giorni PREVISTI
		// ma se è una CES allora sono EFFETTIVI -> lo spostiamo da previsti a effettivi :-)
		if (context.equalsIgnoreCase("valida") || context.equalsIgnoreCase("validaArchivio")
				|| context.equalsIgnoreCase("validazionemassiva")) {
			// Sono in validazione
			if (record.get("NUMGGPREVISTIAGR") != null) {
				if (record.get("NUMGGEFFETTUATIAGR") == null || record.get("NUMGGEFFETTUATIAGR") == "") {
					record.put("NUMGGEFFETTUATIAGR", record.get("NUMGGPREVISTIAGR"));
				}
				record.remove("NUMGGPREVISTIAGR");
			}

			// Controllo esistenza nel sistema di una comunicazione di cessazione dichiarata o documentata dal
			// lavoratore annullata
			// per lo stesso rapporto di lavoro con data precedente al movimento da validare (Requisito VA18 Trento)
			String codTipoComunic = record.get("CODTIPOCOMUNIC") != null ? record.get("CODTIPOCOMUNIC").toString() : "";
			if (!codTipoComunic.equals(MessageCodes.General.RETTIFICA_COMUNICAZIONE_PREC)) {
				UtilsConfig utility = new UtilsConfig("VA18");
				String tipoConfig = utility.getConfigurazioneDefault_Custom();
				if (tipoConfig.equals(Properties.CUSTOM_CONFIG)) {// configurazione CUSTOM
					SourceBean cessazioneVA18 = esisteCesAnnullataDocDich(record);
					if (cessazioneVA18 != null) {
						record.put("CESANNULLATAVA18", cessazioneVA18);
					}
				}
			}
		}

		// Ritorno
		if (warnings.size() > 0) {
			return ProcessorsUtils.createResponse(prc, className, null, null, warnings, null);
		} else
			return null;
	}

	private SourceBean esisteCesAnnullataDocDich(Map record) {
		SourceBean cessVA18 = null;
		try {
			BigDecimal cdnLavoratore = (BigDecimal) record.get("CDNLAVORATORE");
			BigDecimal prgAz = (BigDecimal) record.get("PRGAZIENDA");
			BigDecimal prgUnita = (BigDecimal) record.get("PRGUNITAPRODUTTIVA");
			String dataInizioCess = (String) record.get("DATINIZIOMOV");

			Object params[] = new Object[4];
			params[0] = cdnLavoratore;
			params[1] = prgAz;
			params[2] = prgUnita;
			params[3] = dataInizioCess;
			SourceBean row = (SourceBean) trans.executeQuery("GET_CESSAZIONE_ANNULLATA_DOC_DICH", params, "SELECT");
			Vector rows = row.getAttributeAsVector("ROW");
			if (rows.size() > 0) {
				cessVA18 = (SourceBean) rows.get(0);
			}
			return cessVA18;
		} catch (Exception e) {
			return null;
		}
	}

}