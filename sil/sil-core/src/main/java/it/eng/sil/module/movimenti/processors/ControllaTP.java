package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.enumeration.TipoTrasfEnum;
import it.eng.sil.module.movimenti.extractor.ManualValidationFieldExtractor;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;

public class ControllaTP implements RecordProcessor {
	private String className;
	private String prc;
	private TransactionQueryExecutor trans;
	private boolean checkForzaValidazione = false;
	private boolean checkForzaModifiche = false;
	private SourceBean sbInfoGenerale = null;

	public ControllaTP(SourceBean sb, TransactionQueryExecutor transexec) {
		className = this.getClass().getName();
		prc = "Controlla Trasformazione/Proroga";
		trans = transexec;
		sbInfoGenerale = sb;

		if (sbInfoGenerale != null) {
			if (sbInfoGenerale.containsAttribute("CHECKFORZAVALIDAZIONE")) {
				checkForzaValidazione = (sbInfoGenerale.getAttribute("CHECKFORZAVALIDAZIONE").toString() == "true"
						? true
						: false);
			}
			if (sbInfoGenerale.containsAttribute("CHECKFORZAMODIFICHE")) {
				checkForzaModifiche = (sbInfoGenerale.getAttribute("CHECKFORZAMODIFICHE").toString() == "true" ? true
						: false);
			}
		}
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		int giorno, mese, anno;
		GregorianCalendar dtInizioMovPrecedente = null;
		GregorianCalendar dtFineMovPrecedente = null;
		GregorianCalendar dtInizioMov = null;
		GregorianCalendar dtFineMov = null;
		GregorianCalendar dtAppoggio = null;
		Warning w = null;
		ArrayList warnings = new ArrayList();
		SourceBean result = null;
		ManualValidationFieldExtractor extractor = new ManualValidationFieldExtractor();
		// checkForzaValidazione = false se non sono in validazione massiva
		if (record.get("CONTEXT") == null || !record.get("CONTEXT").toString().equalsIgnoreCase("validazioneMassiva")) {
			checkForzaValidazione = false;
		}
		String context = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";

		// Recupero dati dal record
		String codTipoMov = (String) record.get("CODTIPOMOV");
		if (codTipoMov == null) {
			codTipoMov = "";
		}
		String codTipoMovPrec = (String) record.get("CODTIPOMOVPREC");
		if (codTipoMovPrec == null) {
			codTipoMovPrec = "";
		}
		String codMonoTempo = (String) record.get("CODMONOTEMPO");
		if (codMonoTempo == null) {
			codMonoTempo = "";
		}
		String codMonoTempoMovPrec = (String) record.get("CODMONOTEMPOMOVPREC");
		if (codMonoTempoMovPrec == null) {
			codMonoTempoMovPrec = "";
		}
		String datFineMov = (String) record.get("DATFINEMOV");
		if (datFineMov == null) {
			datFineMov = "";
		}
		String datFineMovPrec = (String) record.get("DATFINEMOVPREC");
		if (datFineMovPrec == null || datFineMovPrec.equals("null")) {
			datFineMovPrec = "";
		}
		String datInizioMov = (String) record.get("DATINIZIOMOV");
		if (datInizioMov == null) {
			datInizioMov = "";
		}
		String datInizioMovPrec = (String) record.get("DATINIZIOMOVPREC");
		if (datInizioMovPrec == null || datInizioMovPrec.equals("null")) {
			datInizioMovPrec = "";
		}
		String codOrario = (String) record.get("CODORARIO");
		if (codOrario == null) {
			codOrario = "";
		}
		String collegato = (String) record.get("COLLEGATO");
		if (collegato == null) {
			collegato = "";
		}
		BigDecimal numOreSett = (BigDecimal) record.get("NUMORESETT");
		String codTipoAssPrec = (String) record.get("CODTIPOASSPREC");
		String codTipoAss = (String) record.get("CODTIPOASS");
		if (codTipoAss == null) {
			codTipoAss = "";
		}
		String codMonoTipoMov = (String) record.get(MovimentoBean.DB_COD_MONO_TIPO_ASS);
		if (codMonoTipoMov == null) {
			codMonoTipoMov = "";
		}

		String codTipoTrasf = extractor.estraiTipoTrasformazione(record);

		// Creazione delle date come GregorianCalendar
		if (!datInizioMovPrec.equals("")) {
			giorno = Integer.parseInt(datInizioMovPrec.substring(0, 2));
			mese = Integer.parseInt(datInizioMovPrec.substring(3, 5));
			anno = Integer.parseInt(datInizioMovPrec.substring(6, 10));
			dtInizioMovPrecedente = new GregorianCalendar(anno, (mese - 1), giorno);
		}

		if (!datInizioMov.equals("")) {
			giorno = Integer.parseInt(datInizioMov.substring(0, 2));
			mese = Integer.parseInt(datInizioMov.substring(3, 5));
			anno = Integer.parseInt(datInizioMov.substring(6, 10));
			dtInizioMov = new GregorianCalendar(anno, (mese - 1), giorno);
		}

		if (!datFineMovPrec.equals("")) {
			giorno = Integer.parseInt(datFineMovPrec.substring(0, 2));
			mese = Integer.parseInt(datFineMovPrec.substring(3, 5));
			anno = Integer.parseInt(datFineMovPrec.substring(6, 10));
			dtFineMovPrecedente = new GregorianCalendar(anno, (mese - 1), giorno);
		}
		if (!datFineMov.equals("")) {
			giorno = Integer.parseInt(datFineMov.substring(0, 2));
			mese = Integer.parseInt(datFineMov.substring(3, 5));
			anno = Integer.parseInt(datFineMov.substring(6, 10));
			dtFineMov = new GregorianCalendar(anno, (mese - 1), giorno);
		}
		// INIZIO CONTROLLI!!!

		SourceBean row = null;
		String codAvvTMP = codTipoAssPrec;
		BigDecimal prgMovPrec = (BigDecimal) record.get("PRGMOVIMENTOPREC");

		String flgAssPropria = extractor.estraiAssunzionePropria(record);
		boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
		String tipoAzienda = extractor.estraiTipoAzienda(record);

		String selectquery = "";
		if (codAvvTMP == null) {
			codAvvTMP = "";
		}

		boolean distacco = false;
		String dataFineDistacco = "";
		String flgDistaAzEstera = (String) record.get("FLGDISTAZESTERA");
		boolean notDistaAzEstera = "N".equalsIgnoreCase(flgDistaAzEstera);
		boolean comUnisomm = false;
		String dataInizioMissione = "";
		String dataFineContrattoSomm = "";

		if (tipoAzienda.equalsIgnoreCase("INT") && notAssPropria) {
			comUnisomm = true;
			dataInizioMissione = extractor.estraiInizioMissione(record);
			dataFineContrattoSomm = extractor.estraiFineContrattoSomm(record);
		}

		if (codTipoMov.equalsIgnoreCase("TRA")) {
			if (codTipoTrasf.equalsIgnoreCase("DL")) {
				distacco = true;
				if (comUnisomm) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_DIST_PERSONALE_INT), "", warnings, null);
				}
				dataFineDistacco = (String) record.get("DATFINEDISTACCO");
				if (dataFineDistacco == null) {
					dataFineDistacco = "";
				}
				// In caso di distacco, i dati relativi all'azienda distaccataria vengono inseriti in fase di
				// validazione dal processor
				// insertAzienda o insertAziendaXValidazioneMassiva, mentre in caso di inserimento o rettifica dal
				// processor prevalorizzaCampi
				// In ogni caso PRGAZIENDADIST = PRGAZIENDAUTIL e PRGUNITADIST = PRGUNITAUTIL
				BigDecimal prgAzDist = (BigDecimal) record.get("PRGAZIENDADIST");
				BigDecimal prgUnitaDist = (BigDecimal) record.get("PRGUNITADIST");
				if (dataFineDistacco.equals("")) {
					// ritorno errore per i distacchi (è necessaria la data fine distacco)
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_AZ_UTILIZ),
							"In caso di distacco lavoratore bisogna specificare la data fine distacco", warnings, null);
				}
				// ritorno errore per i distacchi (l'azienda distaccataria è obbligatoria solo se non si tratta di
				// azienda estera.)
				// in quest'ultimo caso l'informazione viene gestita dal flag FLGDISTAZESTERA
				if (notDistaAzEstera && (prgAzDist == null || prgUnitaDist == null)) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_AZ_UTILIZ),
							"In caso di distacco lavoratore bisogna specificare l'azienda distaccataria", warnings,
							null);
				}
			} else {
				String dataFineAffittoRamo = (String) record.get("DATFINEAFFITTORAMO");
				if (codTipoTrasf.equalsIgnoreCase("02")) {
					// controlli non bloccanti sulla data fine affitto ramo
					if (dataFineAffittoRamo == null || dataFineAffittoRamo.equals("")) {
						w = new Warning(MessageCodes.AffittoRamoAzienda.WARNING_DT_FINE_AFFITTO_NULL, "");
						warnings.add(w);
					} else {
						if (!datInizioMov.equals("")) {
							try {
								if (DateUtils.compare(dataFineAffittoRamo, datInizioMov) < 0) {
									return ProcessorsUtils.createResponse(prc, className,
											new Integer(MessageCodes.AffittoRamoAzienda.ERR_DT_FINE_AFFITTO_PREC_TRA),
											"", warnings, null);
								}
							} catch (Exception ex) {
								dataFineAffittoRamo = null;
								record.remove("DATFINEAFFITTORAMO");
								w = new Warning(MessageCodes.AffittoRamoAzienda.WARNING_DT_FINE_AFFITTO_NULL, "");
								warnings.add(w);
							}
						}
					}
				} else {
					if (dataFineAffittoRamo != null && !dataFineAffittoRamo.equals("")) {
						w = new Warning(MessageCodes.AffittoRamoAzienda.WARNING_NO_DT_FINE_AFFITTO_, "");
						warnings.add(w);
					}

					// CONTROLLO DECRETO CO 30/09/16
					if (codTipoTrasf
							.equalsIgnoreCase(TipoTrasfEnum.TRASFORMAZIONE_APPRENDISTATO_APP_PROFESS.getCodice())) {
						if (!codTipoAss.equalsIgnoreCase(
								DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE_O_CONTRATTO_DI_MESTIERE)) {
							return ProcessorsUtils.createResponse(prc, className, new Integer(
									MessageCodes.ImportMov.ERR_TRASFORMAZIONE_APPRENDISTATO_PROFESSIONALIZZANTE), "",
									warnings, null);
						}
					}
				}
			}
		}

		// controllo bloccante (data fine distacco deve essere >= data inizio trasformazione)
		try {
			if (distacco && !datInizioMov.equals("") && !dataFineDistacco.equals("")
					&& DateUtils.compare(dataFineDistacco, datInizioMov) < 0) {
				return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.General.OPERATION_FAIL),
						"Data fine distacco deve essere maggiore o uguale alla data inizio trasformazione", warnings,
						null);
			}
			if (distacco && codMonoTempo.equals("D") && !datFineMov.equals("") && !dataFineDistacco.equals("")
					&& DateUtils.compare(dataFineDistacco, datFineMov) > 0) {
				return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.General.OPERATION_FAIL),
						"Data fine distacco deve essere minore o uguale alla data fine trasformazione", warnings, null);
			}

		} catch (Exception ex) {
			// ritorno errore nei controlli sulle date
			return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.General.OPERATION_FAIL),
					"Data fine distacco deve essere maggiore o uguale a data inizio trasformazione e minore o uguale a data fine trasformazione.",
					warnings, null);
		}

		if (codTipoAss.equals("")) {
			while ((codAvvTMP.equals("")) && (prgMovPrec != null)) {
				// Cerco il codice di assunzione dal movimento precedente
				selectquery = "select m.prgmovimentoprec, m.codtipomov, m.codtipocontratto codtipoass "
						+ "from am_movimento m " + "where m.prgmovimento = " + prgMovPrec;
				try {
					row = ProcessorsUtils.executeSelectQuery(selectquery, trans);
				} catch (Exception e) {
					// TracerSingleton.log(Values.APP_NAME, TracerSingleton.CRITICAL,"Errore nel reperimento del tipo di
					// assunzione.", e);
					return ProcessorsUtils.createResponse("ControllaTP", className,
							new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
							"Errore nel reperimento del tipo di assunzione.", warnings, null);
				}
				codAvvTMP = row.containsAttribute("ROW.codtipoass") ? row.getAttribute("ROW.codtipoass").toString()
						: "";
				prgMovPrec = (BigDecimal) row.getAttribute("ROW.PRGMOVIMENTOPREC");
			}

			if (!codAvvTMP.equals("")) {
				// Ho trovato il cod di assunzione antecedente la trasformazione
				codTipoAss = codAvvTMP;
				record.put("CODTIPOASS", codAvvTMP);
			}
		}

		if ("Tra".equalsIgnoreCase(codTipoMov)) {
			// 18/05/2011 Per i contratti in ASSOCIAZIONE IN PARTECIPAZIONE A TEMPO DETERMINATO/INDETERMINATO
			// è ammessa la trasformazione solo per tipo trasformazione = TL(Trasferimento lavoratore).
			if (DeTipoContrattoConstant.mapContrattiAut_Tra.containsKey(codTipoAss)) {
				if (!codTipoTrasf.equalsIgnoreCase("TL")) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_TRASFORMAZIONI_ASS_PARTECIPAZIONE), "", warnings,
							null);
				}
			} else {
				if (codMonoTipoMov.equalsIgnoreCase("N")) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_TRASFORMAZIONI_LAV_AUTONOMO), "", warnings, null);
				}
			}

			String statement = "SELECT coll.STRCHIAVETABELLAPARTENZA TEMPO " + "FROM de_mv_tipo_ass_coll coll "
					+ "WHERE coll.codlsttabpartenza = 'DE_CONT' " + "	  AND coll.codlsttab = 'TIPOCONT' "
					+ "	  AND STRCHIAVETABELLA = '" + codTipoAss + "'";

			SourceBean resultTempo = null;
			String tempo = null;

			try {
				resultTempo = ProcessorsUtils.executeSelectQuery(statement, trans);
				if (resultTempo != null) {
					Vector row_contratto = resultTempo.getAttributeAsVector("ROW");
					if (row_contratto != null) {
						if (row_contratto.size() == 1) {
							tempo = (String) ((SourceBean) row_contratto.get(0)).getAttribute("TEMPO");
						} else {
							if (row_contratto.size() > 1) {
								if (datFineMov.equals("")) {
									tempo = "LP";
								} else {
									tempo = "LT";
								}
							}
						}
					}
				}
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null);
			}

			if (codTipoTrasf.equals("DI") || codTipoTrasf.equals("FF") || codTipoTrasf.equals("AI")
					|| codTipoTrasf.equals("FI") || codTipoTrasf.equals("II")) {
				// tempo è LT e si considera un tempo determinato
				if (tempo != null && "LT".equalsIgnoreCase(tempo)) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_TRASF_DA_DET_A_IND), "", warnings, null);
				}
			}

			// Data fine trasformazione
			if (!context.equalsIgnoreCase("validazioneMassiva")) {
				if (codMonoTempo.equals("D") && datFineMov.equals(""))
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_DATAFINE_TRASFPRO), "", warnings, null);
			} else {
				if (!codTipoTrasf.equalsIgnoreCase("DL")) {
					if (tempo != null && "LP".equalsIgnoreCase(tempo)) {
						record.put("CODMONOTEMPO", "I");
						if (!datFineMov.equals("")) {
							record.remove("DATFINEMOV");
							record.remove("DATFINEMOVEFFETTIVA");
						}
					} else {
						// tempo è LT e si considera un tempo determinato
						record.put("CODMONOTEMPO", "D");
						if (prgMovPrec == null) {
							return ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.ImportMov.ERR_NOT_PREC_DATFINE), "", warnings, null);
						} else {
							if (!datFineMovPrec.equals("")) {
								/*
								 * se esiste il precedente ed ha la data fine allora valorizzo la data fine con quella
								 * del movimento precedente
								 */
								record.put("DATFINEMOV", datFineMovPrec);
							} else {
								return ProcessorsUtils.createResponse(prc, className,
										new Integer(MessageCodes.ImportMov.ERR_PREC_DATFINE), "", warnings, null);
							}
						}
					}
				}
			}
		}

		// Controllo orario e nel caso di forzatura validazione lo setto a TP1 se non è presente
		// Fase 2 "TP1" corrisponde a "F"
		if (record.get("CODORARIO") == null) {
			if (checkForzaValidazione) {
				// modifica dell'orario
				codOrario = "F";
				record.put("CODORARIO", codOrario);
			} else {
				if (tipoAzienda.equalsIgnoreCase("INT")) {
					w = new Warning(MessageCodes.ImportMov.WAR_CODORARIO_NULL, "");
					warnings.add(w);
				} else {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
							"Il campo Orario non può essere vuoto.", warnings, null);
				}
			}
		}

		// Orario part time in trasformazione
		if (codTipoMov.equalsIgnoreCase("Tra")) {
			if (!codOrario.equals("F") && !codOrario.equals("N")) {
				// Controlla se contiene l'orario
				if (numOreSett == null) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_ORE_SETT_AVV), "", warnings, null);
				}
			}
		}

		// Data di inizio proroga successiva alla data di inizio del movimento precedente
		if (codTipoMov.equalsIgnoreCase("Pro") && !collegato.equalsIgnoreCase("nessuno")) {
			if (datInizioMovPrec.equals("")) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_NO_DATA_INIZIOMOVPREC), "", warnings, null);
			} else {
				if (dtInizioMov.before(dtInizioMovPrecedente)) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_DATA_INIZIO_PRO), "", warnings, null);
				} else if (dtInizioMov.equals(dtInizioMovPrecedente)) {
					// Controllo se il movimento precedente è esso stesso una proroga,
					// in questo caso segnalo che il movimento è doppio, altrimenti segnalo
					// semplicemente che la data di inizio è errata
					if (codTipoMovPrec.equalsIgnoreCase("PRO")) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_DOPPIO_MOV), "", warnings, null);
					} else {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_DATA_INIZIO_PRO), "", warnings, null);
					}
				}
				if (datFineMovPrec.equals("")) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_NO_DATA_FINEMOVPREC), "", warnings, null);
				} else {
					// La data di inizio proroga deve essere il giorno successivo alla data di fine avviamento
					GregorianCalendar dtSuccFineMovPrecedente = null;
					giorno = Integer.parseInt(datFineMovPrec.substring(0, 2));
					mese = Integer.parseInt(datFineMovPrec.substring(3, 5));
					anno = Integer.parseInt(datFineMovPrec.substring(6, 10));
					dtSuccFineMovPrecedente = new GregorianCalendar(anno, mese - 1, giorno);
					dtSuccFineMovPrecedente.set(Calendar.DATE, (giorno + 1));
					if (!(dtInizioMov.equals(dtSuccFineMovPrecedente))) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_DATA_FINE_PRO), "", warnings, null);
					}
				}
			}
		}

		// Data di trasformazione successiva alla data di inizio del movimento precedente
		if (codTipoMov.equalsIgnoreCase("Tra") && !collegato.equalsIgnoreCase("nessuno")) {
			if (datInizioMovPrec.equals("")) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_NO_DATA_INIZIOMOVPREC), "", warnings, null);
			} else {
				if (dtInizioMov.before(dtInizioMovPrecedente)) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_DATA_INIZIO_TRA), "", warnings, null);
				}
				// controllo che la data inizio tr non sia uguale alla data inizio avv.
				// 07042010 - controllo disabilitato Rodi
				/*
				 * else if (dtInizioMov.equals(dtInizioMovPrecedente)) { //Controllo se il movimento precedente è esso
				 * stesso una trasformazione, //in questo caso segnalo che il movimento è doppio, altrimenti segnalo
				 * //semplicemente che la data di inizio è errata if (codTipoMovPrec.equalsIgnoreCase("TRA")) { String
				 * codTipoTrasfPrec = record.get("CODTIPOTRASFPREC")!=null?record.get("CODTIPOTRASFPREC").toString():"";
				 * if (codTipoTrasf.equalsIgnoreCase(codTipoTrasfPrec) && !codTipoTrasf.equalsIgnoreCase("DL")) { return
				 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_DOPPIO_MOV),
				 * "", warnings, null); } } else { return ProcessorsUtils.createResponse(prc, className, new
				 * Integer(MessageCodes.ImportMov.ERR_DATA_INIZIO_TRA), "", warnings, null); } }
				 */
			}
		}

		// Controllo che la data di inizio della trasformazione sia precedente o uguale
		// alla data di fine del movimento precedente per trasformazioni di movimenti precedentemente a TD
		if (codTipoMov.equalsIgnoreCase("Tra") && codMonoTempoMovPrec.equalsIgnoreCase("D")
				&& dtInizioMov.after(dtFineMovPrecedente)) {
			// Se non sto passando a TI devo dare errore
			if (!codMonoTempo.equalsIgnoreCase("I")) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_DATA_INIZIO_TRA_DET_AFTER), "", warnings, null);
			} else {
				// Se sto trasformando in TI la data di inizio può anche essere il gg dopo la fine del mov prec
				GregorianCalendar dtSuccFineMovPrecedente = null;
				giorno = Integer.parseInt(datFineMovPrec.substring(0, 2));
				mese = Integer.parseInt(datFineMovPrec.substring(3, 5));
				anno = Integer.parseInt(datFineMovPrec.substring(6, 10));
				dtSuccFineMovPrecedente = new GregorianCalendar(anno, mese - 1, giorno);
				dtSuccFineMovPrecedente.set(Calendar.DATE, (giorno + 1));

				// DONA 14/02/2007: 3.6 Trasformazione da TD a TI
				// è stato inserito un campo nella TS_GENERALE che contiene il numero di giorni di elasticità
				// se la differenza tra la data fine mov precedente e la data inizio trasformazione è
				// maggiore del numero di giorni viene segnalato l'errore
				// in caso contrario viene modificata la data fine del movimento precedente
				// ponendola a quella di inizio trasformazione -1
				// 11/07/2007 aggiunto flag FORZAMOD: se TRUE le modifiche vengono effettuate altrimenti NO
				if (checkForzaValidazione && checkForzaModifiche) {
					// nel caso FORZAVAL = TRUE
					int nuGgElasticitaTrasformazione = getNumGgElasticitaTra();

					int ggDiff = DateUtils.daysBetween(datFineMovPrec, datInizioMov);
					if (ggDiff > nuGgElasticitaTrasformazione) {
						// TODO da modificare errore
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_DATA_INIZIO_TRA_IND_AFTER_FORZAVAL),
								"Numero max giorni di elasticità è " + nuGgElasticitaTrasformazione, warnings, null);
					} else {
						// data inizio trasformazione meno 1 giorno
						String datPrecInizioMov;
						try {
							datPrecInizioMov = DateUtils.giornoPrecedente(datInizioMov);
						} catch (Exception e1) {
							return ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.StatoOccupazionale.ERRORE_AGG_DATA_FINE_MOV_PREC), "",
									warnings, null);
						}

						// devo aggiornare la datFineMov del movimento precedente
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
								BigDecimal user = (BigDecimal) RequestContainer.getRequestContainer()
										.getSessionContainer().getAttribute("_CDUT_");
								numkloMovPrec = DBStore.aggiornaDataFineMov(prgMovDaAggiornare, datPrecInizioMov, user,
										numkloMovPrec, trans);
								record.put("NUMKLOMOVPREC", numkloMovPrec);
								record.put("DATFINEMOVPREC", datPrecInizioMov);
							} catch (Exception e) {
								return ProcessorsUtils.createResponse(prc, className,
										new Integer(MessageCodes.StatoOccupazionale.ERRORE_AGG_DATA_FINE_MOV_PREC), "",
										warnings, null);
							}

							// modifica alle note dei movimenti
							String notaMov = (String) record.get("STRNOTE");
							String strNota = "<li>Il movimento precedente collegato potrebbe non essere quello corretto.</li>";
							if (notaMov != null) {
								notaMov = notaMov + strNota;
							} else {
								notaMov = strNota;
							}

							String notaMovPrec = (String) record.get("STRNOTEMOVPREC");
							String strNotaPrec = "<li>E' stata modificata la data fine del movimento da "
									+ datFineMovPrec + " a " + datPrecInizioMov
									+ " per renderlo compatibile con la trasformazione.</li>";
							if (notaMovPrec != null) {
								notaMovPrec = notaMovPrec + strNotaPrec;
							} else {
								notaMovPrec = strNotaPrec;
							}

							record.put("STRNOTE", notaMov);
							record.put("STRNOTEMOVPREC", notaMovPrec);
						}
					}
				} else {
					// versione standard
					// nel caso in cui il flag FORZAVAL non esiste
					if (!dtInizioMov.equals(dtSuccFineMovPrecedente)) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_DATA_INIZIO_TRA_IND_AFTER), "", warnings, null);
					}
				}

			}

		}
		// Non è possibile cambiare una trasformazione da Ind. a Det.
		if (codMonoTempo.equalsIgnoreCase("D") && codMonoTempoMovPrec.equalsIgnoreCase("I")) {
			return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_MANS_TRA), "",
					warnings, null);
		}

		// se si effettua una Trasformazione da tempo determinato a tempo indeterminato deve essere
		// aggiornata anche la combo del Tempo a indeterminato
		if (codTipoMov.equalsIgnoreCase("Tra")) {
			// se si effettua una Trasformazione da tempo parziale a tempo pieno deve essere aggiornata anche
			// la combo Orario a Tempo Pieno
			if (codTipoTrasf.equalsIgnoreCase("PP")) {
				if (!codOrario.equalsIgnoreCase("F")) {
					if (checkForzaValidazione) {
						record.put("CODORARIO", "F");
						record.put("NUMORESETT", "");
					} else {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_TRASF_DA_PARZ_A_PIENO), "", warnings, null);
					}
				}
			}
			// se si effettua una Trasformazione da tempo pieno a tempo parziale deve essere specificato
			// il tipo di orario parziale e il numero di ore
			if (codTipoTrasf.equalsIgnoreCase("TP")) {
				if (codOrario.equalsIgnoreCase("F")) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_TEMPO_PAR_NUM_ORE), "", warnings, null);
				}
			}
			if (codTipoTrasf.equalsIgnoreCase("NT")) {
				return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_TRASF_NT),
						"", warnings, null);
			}

			// Controlli nuovi decreto GENNAIO 2013
			if (!comUnisomm && TipoTrasfEnum.TRASFORMAZIONE_FORMAZIONE_TI.getCodice().equalsIgnoreCase(codTipoTrasf)) {
				if (!tipoAzienda.equals("") && !tipoAzienda.equalsIgnoreCase("PA")) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_TIPO_TRASFORMAZIONE_PA), "", warnings, null);
				}
			}

			if (comUnisomm) {
				try {
					if (!datInizioMov.equals("") && !dataInizioMissione.equals("")
							&& DateUtils.compare(datInizioMov, dataInizioMissione) <= 0) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_DATA_INIZIO_TRA_INIZIO_MIS), "", warnings, null);
					}
					if (!datInizioMov.equals("") && !dataFineContrattoSomm.equals("")
							&& DateUtils.compare(datInizioMov, dataFineContrattoSomm) > 0) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_DATA_INIZIO_TRA_FINE_CONTRATTO_SOMM), "",
								warnings, null);
					}
				} catch (Exception ex1) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
							"Data inizio trasformazione non corretta", warnings, null);
				}
			}
		}

		// Grado avviamneto
		// FASE 2: il campo grado non è più utilizzato
		/*
		 * if ( record.get("CODGRADO") == null) { if (checkForzaValidazione) { String strNote = null; //modifica del
		 * grado (14 --> Generico) record.put("CODGRADO", "14"); //nota da aggiungere String notaAdd =
		 * "<li>Non è stato specificato il grado. Il sistema ha impostato automaticamente 'Generico'.</li>"; strNote =
		 * (String) record.get("STRNOTE"); if (strNote != null) { strNote = strNote + notaAdd; } else { strNote =
		 * notaAdd; } record.put("STRNOTE", strNote); } else { // se è nullo lo metto a "" per evitare i NullPointer
		 * record.put("CODGRADO", ""); }
		 * 
		 * /*else { return ProcessorsUtils.createResponse(prc, className, new
		 * Integer(MessageCodes.ImportMov.ERR_GRADO_AVV), "", warnings, null); } }
		 */
		// Livello
		if (record.get("NUMLIVELLO") == null) {
			if (checkForzaValidazione) {
				// modifica del livello
				record.put("NUMLIVELLO", "--");
			}
			/*
			 * else { return ProcessorsUtils.createResponse(prc, className, new
			 * Integer(MessageCodes.ImportMov.ERR_LIVELLO_NULLO), "", warnings, null); }
			 */
		}

		/*
		 * //Se sto effettuando una TRASFORMAZIONE da tempo Determinato a tempo Indeterminato... if (
		 * codTipoMov.equalsIgnoreCase("Tra") ) { if ( codMonoTempoMovPrec.equalsIgnoreCase("D") &&
		 * codMonoTempo.equalsIgnoreCase("I") ) { //...e il tipo di avviamento corrisponde ad uno dei codici seguenti
		 * //allora ho una 'Trasformazione rapporto di lavoro' if ( (codTipoAssPrec != null) &&
		 * (codTipoAssPrec.equalsIgnoreCase("A.03.00") || codTipoAssPrec.equalsIgnoreCase("A.03.04") ||
		 * codTipoAssPrec.equalsIgnoreCase("A.03.07") || codTipoAssPrec.equalsIgnoreCase("A.03.01") ||
		 * codTipoAssPrec.equalsIgnoreCase("A.03.02") || codTipoAssPrec.equalsIgnoreCase("A.03.03")) ) {
		 * 
		 * String codGrado = (String) record.get("CODGRADO");//non è nullo l'ho già controllato prima..
		 * 
		 * //Messaggio da dare solo in inserimento e rettifica di un avvamento if
		 * ("inserisci".equalsIgnoreCase((String)record.get("CONTEXT")) && "AVV".equalsIgnoreCase(codTipoMovPrec)) {
		 * //...quando il grado della trasformazione coincide con quello dell'avviamento if
		 * (codGrado.equalsIgnoreCase((String)record.get("CODGRADOPREC"))) { warnings.add(new
		 * Warning(MessageCodes.ImportMov.WAR_STESSO_GRADO, null)); } }
		 * 
		 * //se poi il grado è diveso 'Apprendista' o 'In formazione/lavoro' //il tipo di contratto diviene 'Lavoro
		 * Dipendente TI' if (!codGrado.equals("15") && !codGrado.equals("16")) { record.put("CODCONTRATTO","LP"); } } }
		 * }
		 */

		// La data di fine non può essere modificata rispetto al movimento originario se
		// si fa una trasformazione collegata
		if (codTipoMov.equalsIgnoreCase("Tra") && !collegato.equalsIgnoreCase("nessuno")
				&& codMonoTempo.equalsIgnoreCase("D")) {
			if (!dtFineMov.equals(dtFineMovPrecedente)) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_NO_MODIFY_DATAFINE_TRA), "", warnings, null);
			}
		}

		// Controllo che la data di fine proroga sia successiva a quella di inizio
		if (codTipoMov.equalsIgnoreCase("Pro")) {
			// if (dtFineMov.before(dtInizioMov) || dtFineMov.equals(dtInizioMov)) {
			// 13/06/2005 Davide: Correzione PRO la data fine può coincidere con l'inizio
			if (dtFineMov.before(dtInizioMov)) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_FINE_PREC_INIZIO), "", warnings, null);
			}

			// Controlli nuovi decreto GENNAIO 2013
			if (comUnisomm) {
				try {
					if (!datFineMov.equals("") && !dataInizioMissione.equals("")
							&& DateUtils.compare(datFineMov, dataInizioMissione) <= 0) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_DATA_FINE_PRO_INIZIO_MIS), "", warnings, null);
					}
					if (!datFineMov.equals("") && !dataFineContrattoSomm.equals("")
							&& DateUtils.compare(datFineMov, dataFineContrattoSomm) > 0) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_DATA_FINE_PRO_FINE_CONTRATTO_SOMM), "", warnings,
								null);
					}
				} catch (Exception ex1) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "Data fine proroga non corretta",
							warnings, null);
				}
			}
		}

		// Controllo che la data di fine trasformazione sia successiva a quella di inizio per trasf a TI
		if (codTipoMov.equalsIgnoreCase("Tra") && codMonoTempo.equalsIgnoreCase("D")) {
			if (dtFineMov.before(dtInizioMov)) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_FINE_PREC_INIZIO), "", warnings, null);
			}
		}

		// Controllo che si può prorogare solo se si tratta di rapporto a T.D.
		if (codTipoMov.equalsIgnoreCase("Pro") && !codMonoTempo.equalsIgnoreCase("D")) {
			return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_EFFETTUA_PRO),
					"", warnings, null);
		}

		// Controllo del numero di proroghe già fatte, max 4 per az. interinali e max 1 negli altri casi
		// FASE 2: Non esistono limiti massimi sul numero di proroghe, nè per aziende di somministrazione,
		// nè per altre tipologie aziendali
		/*
		 * if (codTipoMov.equalsIgnoreCase("Pro")){ BigDecimal numProroghe = (BigDecimal) record.get("NUMPROROGHE");
		 * String codTipoAz = (String) record.get("CODAZTIPOAZIENDA"); if (codTipoAz != null &&
		 * codTipoAz.equalsIgnoreCase("INT")) { if (numProroghe.intValue() > 4) { warnings.add(new
		 * Warning(MessageCodes.ImportMov.ERR_PRO_INT, "")); } } else if (numProroghe.intValue() > 1) { warnings.add(new
		 * Warning(MessageCodes.ImportMov.ERR_PRO_DET, "")); } }
		 */

		// ************
		/*
		 * se sto registrando un movimento a TD part_time controllo che non ne esistano altri correnti sempre part-time
		 * che mi facciano superare le 48 ore settimanali
		 */
		// 26/06/2007 donato modifica codici orario tempo parttime
		// 05/05/2008 alessandro adeguamento fase2: aggiornati codici cablati
		if (record.get("CODORARIO") != null && (record.get("CODORARIO").toString().equalsIgnoreCase("P")
				|| record.get("CODORARIO").toString().equalsIgnoreCase("V")
				|| record.get("CODORARIO").toString().equalsIgnoreCase("M"))) {
			if ((record.get("NUMORESETT") != null) && (record.get("CDNLAVORATORE") != null)
					&& (record.get("PRGAZIENDA") != null) && (record.get("PRGMOVIMENTOPREC") != null)
					&& (record.get("CODTIPOMOV") != null)
					&& record.get("CODTIPOMOV").toString().equalsIgnoreCase("Tra")) {
				String queryStat = "SELECT nvl(SUM(amm.NUMORESETT),0) SOMMA FROM AM_MOVIMENTO amm WHERE "
						+ "amm.CODORARIO IN ('P', 'V', 'M' ) " + "AND amm.CDNLAVORATORE="
						+ record.get("CDNLAVORATORE").toString() + " AND amm.PRGAZIENDA="
						+ record.get("PRGAZIENDA").toString() + " AND amm.codStatoAtto = 'PR' "
						+ " AND amm.prgmovimento <>" + record.get("PRGMOVIMENTOPREC").toString()// Il mov che seleziono
																								// non deve essere
																								// l'avviamento che sto
																								// trasformando
						+ " AND NOT EXISTS ( " + "SELECT amm2.PRGMOVIMENTO FROM AM_MOVIMENTO amm2 "
						+ "WHERE amm2.CODTIPOMOV = 'CES' " + "AND amm2.prgmovimentoprec = amm.PRGMOVIMENTO "
						+ "AND amm2.DATINIZIOMOV >= amm.DATINIZIOMOV " + "AND amm2.CDNLAVORATORE="
						+ record.get("CDNLAVORATORE").toString() + " AND amm2.PRGAZIENDA="
						+ record.get("PRGAZIENDA").toString() + " )";

				// Eseguo la query
				result = null;
				try {
					result = ProcessorsUtils.executeSelectQuery(queryStat, trans);
				} catch (Exception e) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null);
				}

				// Estraggo il risultato
				BigDecimal somma = ((BigDecimal) result.getAttribute("ROW.SOMMA"))
						.add(new BigDecimal(record.get("NUMORESETT").toString()));
				if (somma.compareTo(new BigDecimal("48")) > 0) {
					w = new Warning(MessageCodes.ImportMov.WAR_ORESETT_TDPT_AVV, "");
					warnings.add(w);
				}
			}
		}

		/*
		 * Non è possibile inserire due movimenti per lo stesso lavoratore, con orario a tempo pieno
		 */
		/*
		 * 15/09/04 Momentaneamente disabilitato, perché da modificare
		 *
		 * //XXX anche la codifica di CODORARIO è cambiata if (
		 * record.get("CODORARIO").toString().equalsIgnoreCase("TP1") && (record.get("CDNLAVORATORE") != null) &&
		 * (record.get("PRGMOVIMENTOPREC") != null) && (record.get("CODTIPOMOV") != null) &&
		 * record.get("CODTIPOMOV").toString().equalsIgnoreCase("Tra") ){ String queryStat =
		 * "SELECT amm.PRGMOVIMENTO FROM AM_MOVIMENTO amm WHERE " + "amm.CODORARIO = 'TP1' " + "AND amm.CDNLAVORATORE="
		 * + record.get("CDNLAVORATORE").toString() + " AND amm.codStatoAtto = 'PR' " + " AND amm.CODTIPOMOV <> 'CES'" +
		 * " AND amm.prgmovimento <> " + record.get("PRGMOVIMENTOPREC").toString()//Il mov che seleziono non deve essere
		 * l'avviamento che sto trasformando + " AND NOT EXISTS ( " + "SELECT amm2.PRGMOVIMENTO FROM AM_MOVIMENTO amm2 "
		 * + "WHERE amm2.CODTIPOMOV = 'CES' " + "AND amm2.prgmovimentoprec = amm.PRGMOVIMENTO " +
		 * "AND amm2.DATINIZIOMOV >= amm.DATINIZIOMOV " + "AND amm2.CDNLAVORATORE=" +
		 * record.get("CDNLAVORATORE").toString() + " )";
		 * 
		 * try { rs = stmt.executeQuery(queryStat);
		 * 
		 * if (rs != null) { rs.next(); if (!rs.isAfterLast() && (rs.getRow()!=0)) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_TEMPO_PIENO), "",
		 * warnings, null); } rs.close(); } } catch (Exception e) { return ProcessorsUtils.createResponse(prc,
		 * className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null); } }
		 */

		/*
		 * Non è possibile inserire, per lo stesso lavoratore, un movimento a tempo pieno, se è già impegnato per un
		 * movimento a tempo parziale.
		 */
		// XXX 05/05/08 anche la codifica di CODORARIO è cambiata
		/*
		 * 15/09/04 Momentaneamente disabilitato, perché da modificare if (
		 * record.get("CODORARIO").toString().equalsIgnoreCase("TP1") && (record.get("CDNLAVORATORE") != null) &&
		 * (record.get("PRGMOVIMENTOPREC") != null) && (record.get("CODTIPOMOV") != null) &&
		 * record.get("CODTIPOMOV").toString().equalsIgnoreCase("Tra") ){ String queryStat =
		 * "SELECT amm.PRGMOVIMENTO FROM AM_MOVIMENTO amm WHERE " + "amm.CODORARIO = 'PTO' " + "AND amm.CDNLAVORATORE="
		 * + record.get("CDNLAVORATORE").toString() + " AND amm.codStatoAtto = 'PR' " + " AND amm.CODTIPOMOV <> 'CES'" +
		 * " AND amm.prgmovimento <> " + record.get("PRGMOVIMENTOPREC").toString()//Il mov che seleziono non deve essere
		 * l'avviamento che sto trasformando + " AND NOT EXISTS ( " + "SELECT amm2.PRGMOVIMENTO FROM AM_MOVIMENTO amm2 "
		 * + "WHERE amm2.CODTIPOMOV = 'CES' " + "AND amm2.prgmovimentoprec = amm.PRGMOVIMENTO " +
		 * "AND amm2.DATINIZIOMOV >= amm.DATINIZIOMOV " + "AND amm2.CDNLAVORATORE=" +
		 * record.get("CDNLAVORATORE").toString() + " )";
		 * 
		 * try { rs = stmt.executeQuery(queryStat);
		 * 
		 * if (rs != null) { rs.next(); if (!rs.isAfterLast() && (rs.getRow()!=0)) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_TEMPOPARZ_ESISTENTE),
		 * "", warnings, null); } rs.close(); } } catch (Exception e) { return ProcessorsUtils.createResponse(prc,
		 * className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null); } }
		 */

		/*
		 * Non è possibile inserire, per lo stesso lavoratore, un movimento a tempo parziale, se è già impegnato per un
		 * movimento a tempo pieno.
		 */
		// XXX 05/05/08 anche la codifica di CODORARIO è cambiata
		/*
		 * 15/09/04 Momentaneamente disabilitato, perché da modificare if (
		 * record.get("CODORARIO").toString().equalsIgnoreCase("PTO") && (record.get("CDNLAVORATORE") != null) &&
		 * (record.get("PRGMOVIMENTOPREC") != null) && (record.get("CODTIPOMOV") != null) &&
		 * record.get("CODTIPOMOV").toString().equalsIgnoreCase("Tra") ){ String queryStat =
		 * "SELECT amm.PRGMOVIMENTO FROM AM_MOVIMENTO amm WHERE " + "amm.CODORARIO = 'TP1' " + "AND amm.CDNLAVORATORE="
		 * + record.get("CDNLAVORATORE").toString() + " AND amm.codStatoAtto = 'PR' " + " AND amm.CODTIPOMOV <> 'CES'" +
		 * " AND amm.prgmovimento <> " + record.get("PRGMOVIMENTOPREC").toString()//Il mov che seleziono non deve essere
		 * l'avviamento che sto trasformando + " AND NOT EXISTS ( " + "SELECT amm2.PRGMOVIMENTO FROM AM_MOVIMENTO amm2 "
		 * + "WHERE amm2.CODTIPOMOV = 'CES' " + "AND amm2.prgmovimentoprec = amm.PRGMOVIMENTO " +
		 * "AND amm2.DATINIZIOMOV >= amm.DATINIZIOMOV " + "AND amm2.CDNLAVORATORE=" +
		 * record.get("CDNLAVORATORE").toString() + " )";
		 * 
		 * try { rs = stmt.executeQuery(queryStat);
		 * 
		 * if (rs != null) { rs.next(); if (!rs.isAfterLast() && (rs.getRow()!=0)) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_TEMPO_PIENO), "",
		 * warnings, null); } rs.close(); } } catch (Exception e) { return ProcessorsUtils.createResponse(prc,
		 * className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null); } }
		 */
		// *************

		// SE BENEFICIO PER LAVORATORE APPRENDISTA
		// ALLA DATA DI FINE ASSUNZIONE IL LAVORATORE DEVE AVER COMPIUTO I 16 ANNI DI ETA' (CONTROLLO BLOCCANTE)
		// E NON SUPERARE I 29 ANNI (IN VALIDAZIONE MANUALE SI CHIEDE CONFERMA ALL'OPERATORE)
		// Adeguamento Fase 2 Alessandro 05/05/2008 Commentato il seguente controllo poiché già eseguito in controllaAvv
		/*
		 * if (record.get("CODAGEVOLAZIONE") != null) { if
		 * ((record.get("CODAGEVOLAZIONE").toString().equalsIgnoreCase("D0")) || //FIXME: A0 corrisponde alla vecchia
		 * codifica "L", ma non è univoco !! (record.get("CODAGEVOLAZIONE").toString().equalsIgnoreCase("L"))) { if(
		 * (record.get("DATFINEMOV") != null) && (!record.get("DATFINEMOV").equals("")) ) { if(record.get("DATNASC") !=
		 * null) { String d1 = record.get("DATFINEMOV").toString().substring(6,10) +
		 * record.get("DATFINEMOV").toString().substring(3,5) + record.get("DATFINEMOV").toString().substring(0,2);
		 * String d2 = record.get("DATNASC").toString().substring(6,10) +
		 * record.get("DATNASC").toString().substring(3,5) + record.get("DATNASC").toString().substring(0,2); Integer
		 * num1 = new Integer(d1); Integer num2 = new Integer(d2);
		 * 
		 * int valore = num1.intValue() - num2.intValue(); String eta = String.valueOf(valore); if (eta.length()<6) {
		 * eta = new String("0").concat(eta); } if (eta.length()>6) { eta = eta.substring(0,3); } else { eta =
		 * eta.substring(0,2); } //Controlli sull'età del lavoratore if (Integer.parseInt(eta) < 16) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_BENE_LAV_APP), "",
		 * warnings, null); } if (Integer.parseInt(eta) >= 30) { SourceBean puResult =
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_BENE_LAV_APP), "",
		 * warnings, null); if (!context.equalsIgnoreCase("validazioneMassiva")) { String flagEtaApprendistato =
		 * RequestContainer.getRequestContainer().getServiceRequest().containsAttribute(
		 * "FORZA_INSERIMENTO_ETA_APPRENDISTATO")?
		 * RequestContainer.getRequestContainer().getServiceRequest().getAttribute("FORZA_INSERIMENTO_ETA_APPRENDISTATO"
		 * ).toString():""; if (flagEtaApprendistato.equals("")) { return puResult; } else { if
		 * (flagEtaApprendistato.equalsIgnoreCase("false")) { ProcessorsUtils.addConfirm(puResult,
		 * "Il lavoratore deve avere un'età compresa tra i 16 e i 29 anni alla data di assunzione. Vuoi forzare l' inserimento?"
		 * , "forzaEtaApprendistato", new String[] {"true"}, true); return puResult; } } } else { return puResult; } } }
		 * } } }
		 */

		return ProcessorsUtils.createResponse(prc, className, null, null, warnings, null);
	}

	/**
	 * recupera il numero dei giorni di elasticità per le trasformazioni dalla TS_GENERALE
	 * 
	 * @return
	 */
	private int getNumGgElasticitaTra() {

		String selectquery = "select NUMGGELASTICITATRASFORMAZIONI from ts_generale";

		SourceBean result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
		} catch (Exception e) {
			return 0;
		}

		// Esamino il risultato
		Vector rows = result.getAttributeAsVector("ROW");
		if (rows.size() == 0) {
			return 0;
		} else {
			if (result.getAttribute("ROW.NUMGGELASTICITATRASFORMAZIONI") == null) {
				return 0;
			} else {
				BigDecimal numGg = (BigDecimal) result.getAttribute("ROW.NUMGGELASTICITATRASFORMAZIONI");
				return numGg.intValue();
			}
		}
	}
}