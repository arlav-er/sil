package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.message.MessageBundle;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.enumeration.CodTipoOrarioEnum;
import it.eng.sil.module.movimenti.extractor.ManualValidationFieldExtractor;
import it.eng.sil.util.amministrazione.impatti.Controlli;

public class ControllaAvv implements RecordProcessor {
	private String className;
	private String prc;
	private TransactionQueryExecutor trans;
	private String codRegione = "";
	private String lavoratoreExtraprovinciale = "";
	private String lavCompetenteProv = "";
	private boolean checkForzaValidazione = false;
	private SourceBean sbInfoGenerale = null;
	static final String REGIONE_EMILIA_ROMAGNA = "8";

	public ControllaAvv(SourceBean sb, TransactionQueryExecutor transexec) {
		className = this.getClass().getName();
		prc = "Controlla Avviamento";
		trans = transexec;
		sbInfoGenerale = sb;

		if (sbInfoGenerale != null) {

			if (sbInfoGenerale.containsAttribute("CODREGIONE")) {
				codRegione = sbInfoGenerale.getAttribute("CODREGIONE").toString();
			}

			if (sbInfoGenerale.containsAttribute("CHECKFORZAVALIDAZIONE")) {
				checkForzaValidazione = (sbInfoGenerale.getAttribute("CHECKFORZAVALIDAZIONE").toString() == "true"
						? true
						: false);
			}
		}
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		boolean ok = true;
		// Campi per il controllo della data
		String dataI = "";
		String dataF = "";
		int giorno, mese, anno;
		GregorianCalendar dataInizio, dataFine;
		Warning w = null;
		boolean flgWarCFL = false;
		ArrayList warnings = new ArrayList();
		SourceBean result = null;
		String context = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		// checkForzaValidazione = false se non sono in validazione massiva
		if (record.get("CONTEXT") == null || !record.get("CONTEXT").toString().equalsIgnoreCase("validazioneMassiva")) {
			checkForzaValidazione = false;
		}
		ManualValidationFieldExtractor extractor = new ManualValidationFieldExtractor();

		String codTipoAss = extractor.estraiCodTipoContratto(record);
		String flgAssPropria = extractor.estraiAssunzionePropria(record);
		String codTipoAzienda = extractor.estraiTipoAzienda(record);
		boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
		String codMonoTempo = extractor.estraiCodMonoTempo(record);
		String dataFineMov = extractor.estraiDataFineMovimento(record);
		String dataInizioMov = extractor.estraiDataInizio(record);

		if (!record.get("CODSTATOATTO").toString().equalsIgnoreCase("SS")) {
			// Matricola avviamento non valorizzata
			if (record.get("STRMATRICOLA") == null) {
				if (checkForzaValidazione) {
					// modifica della matricola
					record.put("STRMATRICOLA", "--");
				} else {
					if (codTipoAzienda.equalsIgnoreCase("INT") && notAssPropria) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_MATRICOLA_AVV), "", warnings, null);
					}
				}
			}

			// DECRETO GENNAIO 2013
			try {
				this.checkOreSettimanali(record, extractor);
			} catch (ControlliDecretoApplicationException e) {
				return ProcessorsUtils.createResponse(prc, className, e.getErrorCode(), "", warnings, null);
			}
			// FINE DECRETO GENNAIO 2013

			/*
			 * CONTROLLO SPOSTATO IN ControllaGen Nel caso di un avviamento in agricoltura è obbligatorio indicare il
			 * numero dei giorni presunti in agr. e la lavorazione.
			 * 
			 * if( codTipoAss.equals("H.01.00") ){ if ( (record.get("CODLAVORAZIONE").toString().equals("")) ||
			 * record.get("NUMGGPREVISTIAGR").toString().equals("")) { return ProcessorsUtils.createResponse(prc,
			 * className, new Integer(MessageCodes.ImportMov.ERR_LAVORAZIONE_NUMGGAGR_NULL), "", warnings, null); } }
			 */

			// Nella fase 2 il campo grado non viene più utilizzato
			// Grado avviamneto
			/*
			 * if ( record.get("CODGRADO") == null ) { if (checkForzaValidazione) { String strNote = null; //modifica
			 * del grado (14 --> Generico) record.put("CODGRADO", "14"); //nota da aggiungere String notaAdd =
			 * "<li>Non è stato specificato il grado. Il sistema ha impostato automaticamente 'Generico'.</li>" ;
			 * strNote = (String) record.get("STRNOTE"); if (strNote != null) { strNote = strNote + notaAdd; } else {
			 * strNote = notaAdd; } record.put("STRNOTE", strNote); } //else { // return
			 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_GRADO_AVV), "",
			 * warnings, null); //} }
			 */
			// Livello (Non è più obbligatorio 26/06/2007 Landi)
			if (record.get("NUMLIVELLO") == null && !record.containsKey("NUMGGPREVISTIAGR")) {
				if (checkForzaValidazione) {
					// modifica del livello
					record.put("NUMLIVELLO", "--");
				}
				// else {
				// return ProcessorsUtils.createResponse(prc, className, new
				// Integer(MessageCodes.ImportMov.ERR_LIVELLO_NULLO), "",
				// warnings, null);
				// }
			}
			// scadenza obbligatoria per contratti a T.D.
			if (codMonoTempo.equalsIgnoreCase("D")) {
				if (dataFineMov.equals("")) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_SCADENZA_AVV), "", warnings, null);
				}
			}

			// In caso di assunzione in somministrazione presso una ditta utilizzatrice, sono obbligatori i dati della
			// missione

			if (codTipoAzienda.equalsIgnoreCase("INT") && notAssPropria) {
				BigDecimal prgAzUtil = (BigDecimal) record.get("PRGAZIENDAUTIL");
				String dataInizioRapLav = extractor.estraiInizioMissione(record);
				String dataFineRapLav = extractor.estraiFineMissione(record);
				String dataInizioSommUtil = extractor.estraiInizioContrattoSomm(record);
				String dataFineSommUtil = extractor.estraiFineContrattoSomm(record);

				try {
					if ((prgAzUtil == null && !dataInizioRapLav.equals(""))
							|| (prgAzUtil != null && dataInizioRapLav.equals(""))) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_AZISOMM_AZIUT_DATMIS), "", warnings, null);
					}

					// La missione deve essere compresa nell'intervallo di durata del contratto di somministrazione
					if (!dataFineRapLav.equals("") && !dataInizioMov.equals("") && !dataFineMov.equals("")) {
						if (DateUtils.compare(dataFineRapLav, dataFineMov) > 0
								|| DateUtils.compare(dataFineRapLav, dataInizioMov) < 0) {
							return ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.ImportMov.ERR_PERIODO_MISSIONE_RAPPORTO), "", warnings,
									null);
						}
					}

					if (!dataInizioRapLav.equals("") && !dataInizioMov.equals("") && !dataFineMov.equals("")) {
						if (DateUtils.compare(dataInizioRapLav, dataFineMov) > 0
								|| DateUtils.compare(dataInizioRapLav, dataInizioMov) < 0) {
							return ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.ImportMov.ERR_PERIODO_MISSIONE_RAPPORTO), "", warnings,
									null);
						}
					}

					// La missione deve essere compresa nell'intervallo di durata del contratto di somministrazione
					// presso l'utilizzatrice
					if (!dataFineRapLav.equals("") && !dataInizioSommUtil.equals("") && !dataFineSommUtil.equals("")) {
						if (DateUtils.compare(dataFineRapLav, dataFineSommUtil) > 0
								|| DateUtils.compare(dataFineRapLav, dataInizioSommUtil) < 0) {
							return ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.ImportMov.ERR_PERIODO_MISSIONE_RAPPORTO_SOMM_UTILIZ), "",
									warnings, null);
						}
					}

					if (!dataInizioRapLav.equals("") && !dataInizioSommUtil.equals("")
							&& !dataFineSommUtil.equals("")) {
						if (DateUtils.compare(dataInizioRapLav, dataFineSommUtil) > 0
								|| DateUtils.compare(dataInizioRapLav, dataInizioSommUtil) < 0) {
							return ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.ImportMov.ERR_PERIODO_MISSIONE_RAPPORTO_SOMM_UTILIZ), "",
									warnings, null);
						}
					}
				} catch (Exception ex) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.General.OPERATION_FAIL), "", warnings, null);
				}
			}

			/*
			 * Il codice che segue va sostituito con il blocco precedente per consentire che la data di scadenza del
			 * contratto T.D. sia vuota nel caso delle normative 62 e 57 if ( (record.get("CODMONOTEMPO") != null) &&
			 * (ok) ){ if ( record.get("CODMONOTEMPO").equals("D") ) { if (record.get("CODNORMATIVA") != null) { String
			 * norma = record.get("CODNORMATIVA").toString(); if (!((norma.equalsIgnoreCase("57")) ||
			 * (norma.equalsIgnoreCase("62")))) { if ( record.get("DATFINEMOV") == null){ ok=false; return
			 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_SCADENZA_AVV), "",
			 * warnings, null); } } } else { if ( record.get("DATFINEMOV") == null){ ok=false; return
			 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_SCADENZA_AVV), "",
			 * warnings, null); } } } }
			 */

			// DONA 17/07/2007: eliminato controllo sulla presenza AZIENDA
			// UTILIZZATRICE
			// viene lasciato solamente sul file ContrallaGen
			/*
			 * //azienda utilizzatrice se interinale if ( record.containsKey("CODAZTIPOAZIENDA") &&
			 * record.get("CODAZTIPOAZIENDA").toString().equalsIgnoreCase("INT") && record.containsKey("FLGASSPROPRIA")
			 * && record.get("FLGASSPROPRIA").toString().equalsIgnoreCase("N") ) { if ( (prgAziendaUtil == null) ) {
			 * //DONA 17/07/2007: trasformato errore azienda utilizzatrice in un WARNING w = new
			 * Warning(MessageCodes.ImportMov.ERR_AZ_UTILIZ, ""); warnings.add(w); //return
			 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_AZ_UTILIZ), "",
			 * warnings, null); } } // Se non si è scelto personale interno, si ha bisogno dell'azienda utilizzaztrice
			 * else if ( (record.get("FLGASSPROPRIA") != null) &&
			 * record.get("FLGASSPROPRIA").toString().equalsIgnoreCase("N") ) { if ( (prgAziendaUtil == null) ) { //DONA
			 * 17/07/2007: trasformato errore azienda utilizzatrice in un WARNING // XXX mi sembra assurdo ma va messo
			 * anche qui
			 * 
			 * w = new Warning(MessageCodes.ImportMov.ERR_AZ_UTILIZ, ""); warnings.add(w);
			 * 
			 * //return ProcessorsUtils.createResponse(prc, className, new
			 * Integer(MessageCodes.ImportMov.ERR_AZ_UTILIZ), "", warnings, null); } }
			 */

		} // FINE
			// if(record.get("CODSTATOATTO").toString().equalsIgnoreCase("SS"))

		// La data di cessazione del contratto deve essere successiva a quella
		// di assunzione
		if (record.get("DATINIZIOMOV") != null) {
			dataI = record.get("DATINIZIOMOV").toString();
			if (record.get("DATFINEMOV") != null) {
				dataF = record.get("DATFINEMOV").toString();
			}
			if (!(dataI.equals("")) && !(dataF.equals("")) && (dataI.length() == 10) && (dataF.length() == 10)) {
				giorno = Integer.parseInt(dataI.substring(0, 2));
				mese = Integer.parseInt(dataI.substring(3, 5));
				anno = Integer.parseInt(dataI.substring(6, 10));
				dataInizio = new GregorianCalendar(anno, (mese - 1), giorno);

				giorno = Integer.parseInt(dataF.substring(0, 2));
				mese = Integer.parseInt(dataF.substring(3, 5));
				anno = Integer.parseInt(dataF.substring(6, 10));
				dataFine = new GregorianCalendar(anno, (mese - 1), giorno);

				if (dataFine.before(dataInizio)) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_DATACESSAZIONE_AVV), "", warnings, null);
				}
			}
		}

		// Controlli CFL che considerano il tipo di avviamento N04 (tipo
		// contratto A.03.04)
		if (codTipoAss.equals(DeTipoContrattoConstant.CONTRATTO_FORMAZIONE_LAVORO)) {
			// Una volta scelto il CFL, il movimento deve essere a tempo
			// determinato
			if ((record.get("STRTIPOCFL") != null) && (!record.get("STRTIPOCFL").equals(""))) {
				if (!codMonoTempo.equalsIgnoreCase("D")) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_TIPOCFL), "", warnings, null);
				}
			}

			// Se come tipo CFL = A2 o B le ore settimanali devono essere
			// inferiori a 20
			if ((record.get("STRTIPOCFL") != null)
					&& (record.get("STRTIPOCFL").equals("A2") || record.get("STRTIPOCFL").equals("B")) && ok) {
				if ((record.get("NUMORESETT") != null)
						&& (!codMonoTempo.equals("") && (Integer.parseInt((String) record.get("NUMORESETT")) > 20))) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_ORESETT), "", warnings, null);
				}
			}

			// Mancato inserimento data di autorizzazione
			/*
			 * if( (record.get("DATCFL") == null) && ok ) { ok = false; return ProcessorsUtils.createResponse(prc,
			 * className, new Integer(MessageCodes.ImportMov.ERR_NODATAAUT), "", warnings, null); }
			 */

			// mancato inserimento numero di autorizzazione
			/*
			 * if( (record.get("STRNUMCFL") == null) && ok ) { ok = false; return ProcessorsUtils.createResponse(prc,
			 * className, new Integer(MessageCodes.ImportMov.ERR_NONUMEROAUT), "", warnings, null); }
			 */
		}

		// Se non è indicata l'iscrizione all'albo non si deve poter selezionare
		// il lavoro interinale come tipo di assunzione
		// i codici codTipoAss AF1 o NB1 corrispondono al CODTIPOASS A.06.00 e
		// A.06.01
		if ((record.get("DESCRTIPOAZIENDAUTIL") != null)
				&& (record.get("DESCRTIPOAZIENDAUTIL").toString().equalsIgnoreCase("Interinale")) && (ok)) {
			if ((record.get("STRAZNUMALBOINTERINALI") != null)
					&& (record.get("STRAZNUMALBOINTERINALI").toString().equals(""))) {
				if (codTipoAss.equals(DeTipoContrattoConstant.CONTRATTO_INTERINALE_TI)
						|| codTipoAss.equals(DeTipoContrattoConstant.CONTRATTO_INTERINALE_TD)) {
					ok = false;
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_INTERINALE), "", warnings, null);
				}
			}
		}

		/*
		 * FASE 2: Eliminato il controllo di valorizzazione del registro committenti in caso di 'Lavoro a domicilio'
		 * //Impossibile selezionare come tipo di assunzione 'Lavoro a domicilio' //se non è valorizzato il registro
		 * committenti. if ( ( (record.get("STRNUMREGISTROCOMMITT") == null) || ((record.get("STRNUMREGISTROCOMMITT") !=
		 * null) && record.get("STRNUMREGISTROCOMMITT").toString().equals("")) ) && (ok) ){ if (
		 * (record.get("CODTIPOASS") != null) && record.get("CODTIPOASS").toString().equals("NOG") ){ ok=false; return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_DOMICILIO), "",
		 * warnings, null); } }
		 */

		// Warning per la valorizzazione della tipologia NO0 e tempo determinato
		// il codice codTipoAss NO0 corrisponde al CODTIPOASS ???
		/*
		 * if ( (record.get("CODMONOTEMPO") != null) && record.get("CODMONOTEMPO").equals("D") ){ if (
		 * (record.get("CODNORMATIVA") == null) ){ w = new Warning(MessageCodes.ImportMov.WAR_OBBL_NORMATIVA, "");
		 * warnings.add(w); } } else if ( record.get("CODTIPOASS").equals("NO0") ) { if ( record.get("CODNORMATIVA") ==
		 * null ){ w = new Warning(MessageCodes.ImportMov.WAR_OBBL_NORMATIVA, ""); warnings.add(w); } }
		 */

		// Gruppi di secondo livello
		if (record.get("CODMANSIONE") != null) {
			if (record.get("CODMANSIONE").toString().length() == 6) {
				if (record.get("CODMANSIONE").toString().substring(4, 6).equals("00")) {
					w = new Warning(MessageCodes.ImportMov.WAR_GRUPPI_SEC_LIV, "");
					warnings.add(w);
				}
			} else {
				if (record.get("CODMANSIONE").toString().length() != 7) {
					if (!checkForzaValidazione) {
						if (codTipoAzienda.equalsIgnoreCase("INT")) {
							w = new Warning(MessageCodes.ImportMov.ERR_QUAL_MOV, "");
							warnings.add(w);
						}
					}
				}
			}
		} else {
			if (!checkForzaValidazione) {
				// per aziende interinali la QUALIFICA non è obbligatoria
				if (codTipoAzienda.equalsIgnoreCase("INT")) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_QUAL_MOV), "", warnings, null);
				}
			}
		}

		// Warning per visita nella scelta di CFL
		/*
		 * if ( record.get("CODTIPOASS") != null ) { if ( record.get("CODTIPOASS").toString().equals("NO7") ||
		 * record.get("CODTIPOASS").toString().equals("NB7")) { String dataVisitaMedica = record.get("DATVISITAMEDICA")
		 * != null ? (String)record.get("DATVISITAMEDICA"):""; if (dataVisitaMedica.equals("")){ w = new
		 * Warning(MessageCodes.ImportMov.WAR_VISITA, ""); warnings.add(w); } } }
		 */

		// se sto registrando un movimento a TD part_time controllo che non ne
		// esistano altri
		// correnti /*a TD (data fine minore o uguale ad oggi)*/ sempre
		// part-time che mi facciano superare le 40 ore settimanali
		/*
		 * 17/06/2004 (record.get("CODMONOTEMPO").toString().equalsIgnoreCase("D") &&) non è più solo per i T.D. ma sia
		 * per i T.D. che per i T.I.
		 */
		if (record.get("CODORARIO") != null) {
			if ((record.get("CODORARIO").toString().equalsIgnoreCase("P")
					|| record.get("CODORARIO").toString().equalsIgnoreCase("V")
					|| record.get("CODORARIO").toString().equalsIgnoreCase("M")) && (record.get("NUMORESETT") != null)
					&& (record.get("CDNLAVORATORE") != null) && (record.get("PRGAZIENDA") != null)) {
				String queryStat = "SELECT nvl(SUM(amm.NUMORESETT),0) SOMMA FROM AM_MOVIMENTO amm "
						+ " WHERE /*amm.CODMONOTEMPO='D' AND*/ "// NON SOLO A TD
						+ "amm.CODORARIO IN ('P', 'V', 'M' ) "
						// +
						// "AND TO_DATE(to_char(amm.DATFINEMOV,'DD/MM/YYYY'),'DD/MM/YYYY') <=
						// TO_DATE(to_char(SYSDATE,'DD/MM/YYYY'),'DD/MM/YYYY') "
						+ "AND amm.CDNLAVORATORE=" + record.get("CDNLAVORATORE").toString() + " AND amm.PRGAZIENDA="
						+ record.get("PRGAZIENDA").toString() + " AND amm.codStatoAtto = 'PR' " + " AND NOT EXISTS ( "
						+ "SELECT amm2.PRGMOVIMENTO FROM AM_MOVIMENTO amm2 " + "WHERE amm2.CODTIPOMOV = 'CES' "
						+ "AND amm2.prgmovimentoprec = amm.PRGMOVIMENTO " + "AND amm2.DATINIZIOMOV >= amm.DATINIZIOMOV "
						+ "AND amm2.CDNLAVORATORE=" + record.get("CDNLAVORATORE").toString() + " AND amm2.PRGAZIENDA="
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
		 * ------- if ( record.get("CODTIPOASS").toString().equalsIgnoreCase("NB7") && (record.get("CDNLAVORATORE") !=
		 * null) ) { String queryStat = "SELECT "; } ------------
		 */

		/*
		 * Non è possibile inserire due movimenti per lo stesso lavoratore, con orario a tempo pieno
		 */
		/*
		 * 15/09/04 Momentaneamente disabilitato, perché da modificare if (
		 * record.get("CODORARIO").toString().equalsIgnoreCase("TP1") && (record.get("CDNLAVORATORE") != null) ){ String
		 * queryStat = "SELECT amm.PRGMOVIMENTO FROM AM_MOVIMENTO amm WHERE " + "amm.CODORARIO = 'TP1' " +
		 * "AND amm.CDNLAVORATORE=" + record.get("CDNLAVORATORE").toString() + " AND amm.codStatoAtto = 'PR' " +
		 * " AND amm.CODTIPOMOV <> 'CES'" + " AND NOT EXISTS ( " + "SELECT amm2.PRGMOVIMENTO FROM AM_MOVIMENTO amm2 " +
		 * "WHERE amm2.CODTIPOMOV = 'CES' " + "AND amm2.prgmovimentoprec = amm.PRGMOVIMENTO " +
		 * "AND amm2.DATINIZIOMOV >= amm.DATINIZIOMOV " + "AND amm2.CDNLAVORATORE=" +
		 * record.get("CDNLAVORATORE").toString() + " )";
		 * 
		 * try { rs = stmt.executeQuery(queryStat);
		 * 
		 * if (rs != null) { rs.next(); if (!rs.isAfterLast() && (rs.getRow()!=0)) { ok = false; return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_TEMPO_PIENO), "",
		 * warnings, null); } rs.close(); } } catch (Exception e) { ok=false; return ProcessorsUtils.createResponse(prc,
		 * className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null); } }
		 */

		/*
		 * Non è possibile inserire, per lo stesso lavoratore, un movimento a tempo pieno, se è già impegnato per un
		 * movimento a tempo parziale.
		 */
		/*
		 * 15/09/04 Momentaneamente disabilitato, perché da modificare if (
		 * record.get("CODORARIO").toString().equalsIgnoreCase("TP1") && (record.get("CDNLAVORATORE") != null) ){ String
		 * queryStat = "SELECT amm.PRGMOVIMENTO FROM AM_MOVIMENTO amm WHERE " + "amm.CODORARIO = 'PTO' " +
		 * "AND amm.CDNLAVORATORE=" + record.get("CDNLAVORATORE").toString() + " AND amm.codStatoAtto = 'PR' " +
		 * " AND amm.CODTIPOMOV <> 'CES'" + " AND NOT EXISTS ( " + "SELECT amm2.PRGMOVIMENTO FROM AM_MOVIMENTO amm2 " +
		 * "WHERE amm2.CODTIPOMOV = 'CES' " + "AND amm2.prgmovimentoprec = amm.PRGMOVIMENTO " +
		 * "AND amm2.DATINIZIOMOV >= amm.DATINIZIOMOV " + "AND amm2.CDNLAVORATORE=" +
		 * record.get("CDNLAVORATORE").toString() + " )";
		 * 
		 * try { rs = stmt.executeQuery(queryStat);
		 * 
		 * if (rs != null) { rs.next(); if (!rs.isAfterLast() && (rs.getRow()!=0)) { ok = false; return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_TEMPOPARZ_ESISTENTE),
		 * "", warnings, null); } rs.close(); } } catch (Exception e) { ok=false; return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "",
		 * warnings, null); } }
		 */

		/*
		 * Non è possibile inserire, per lo stesso lavoratore, un movimento a tempo parziale, se è già impegnato per un
		 * movimento a tempo pieno.
		 */
		/*
		 * 15/09/04 Momentaneamente disabilitato, perché da modificare if (
		 * record.get("CODORARIO").toString().equalsIgnoreCase("PTO") && (record.get("CDNLAVORATORE") != null) ){ String
		 * queryStat = "SELECT amm.PRGMOVIMENTO FROM AM_MOVIMENTO amm WHERE " + "amm.CODORARIO = 'TP1' " +
		 * "AND amm.CDNLAVORATORE=" + record.get("CDNLAVORATORE").toString() + " AND amm.codStatoAtto = 'PR' " +
		 * " AND amm.CODTIPOMOV <> 'CES'" + " AND NOT EXISTS ( " + "SELECT amm2.PRGMOVIMENTO FROM AM_MOVIMENTO amm2 " +
		 * "WHERE amm2.CODTIPOMOV = 'CES' " + "AND amm2.prgmovimentoprec = amm.PRGMOVIMENTO " +
		 * "AND amm2.DATINIZIOMOV >= amm.DATINIZIOMOV " + "AND amm2.CDNLAVORATORE=" +
		 * record.get("CDNLAVORATORE").toString() + " )";
		 * 
		 * try { rs = stmt.executeQuery(queryStat);
		 * 
		 * if (rs != null) { rs.next(); if (!rs.isAfterLast() && (rs.getRow()!=0)) { ok = false; return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_TEMPO_PIENO), "",
		 * warnings, null); } rs.close(); } } catch (Exception e) { ok=false; return ProcessorsUtils.createResponse(prc,
		 * className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null); } }
		 */
		// ***************************

		/*
		 * Non ha senso inserire due movimenti validi nello stesso periodo per lo stesso lavoratore riferiti alla stessa
		 * unita aziendale !!! Controllo spostato nel processor SelectMovimentoPrec e generalizzato a tutti i tipi di
		 * movimento !!! (Paolo Roccetti)
		 */
		/*
		 * if ( (record.get("PRGAZIENDA") != null) && (record.get("CDNLAVORATORE") != null) &&
		 * (record.get("DATINIZIOMOV") != null) && ok ){ String queryStat =
		 * "Select amm.prgmovimento, to_date(to_char(amm.datiniziomov,'DD/MM/YYYY'),'DD/MM/YYYY') inizio, " +
		 * "to_date(to_char(amm.datfinemov,'DD/MM/YYYY'),'DD/MM/YYYY') fine " + "from AM_MOVIMENTO amm " +
		 * "where to_date('" + record.get("DATINIZIOMOV").toString() +
		 * "','DD/MM/YYYY') = to_date(to_char(amm.datiniziomov,'DD/MM/YYYY'),'DD/MM/YYYY')" + " and amm.prgazienda = " +
		 * record.get("PRGAZIENDA").toString() + " and amm.cdnlavoratore = " + record.get("CDNLAVORATORE").toString() +
		 * " and amm.codtipomov <> 'CES'" + " and amm.codstatoatto not in ('AN','AR', 'AU')" +
		 * " order by amm.datiniziomov desc"; //Eseguo la query result = null; try { result =
		 * ProcessorsUtils.executeSelectQuery(queryStat, trans); } catch (Exception e) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "",
		 * warnings, null); }
		 * 
		 * //Controllo se ho almeno una riga if (result.containsAttribute("ROW")){ return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_DOPPIO_MOV), "",
		 * warnings, null); } }
		 */

		/*
		 * Il lavoratore ha due lavori aperti contemporaneamente.
		 */
		if ((record.get("CDNLAVORATORE") != null)) {
			String queryStat = "select count(prgmovimento) numLavori" + " from am_movimento amm"
					+ " where amm.cdnlavoratore = " + record.get("CDNLAVORATORE").toString()
					+ " and amm.datfinemoveffettiva > to_date(to_char(SYSDATE,'DD/MM/YYYY'),'DD/MM/YYYY')"
					+ " and amm.codstatoatto = 'PR'";
			// Eseguo la query
			result = null;
			try {
				result = ProcessorsUtils.executeSelectQuery(queryStat, trans);
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null);
			}

			// Controllo risultato
			BigDecimal i = (BigDecimal) result.getAttribute("ROW.numLavori");
			if (i.intValue() > 1) {
				w = new Warning(MessageCodes.ImportMov.WAR_PIU_LAV_APERTI, "");
				warnings.add(w);
			}
		}

		String cognome = record.get("STRCOGNOMETUTORE") != null ? (String) record.get("STRCOGNOMETUTORE") : "";
		String nome = record.get("STRNOMETUTORE") != null ? (String) record.get("STRNOMETUTORE") : "";
		String codQualificaSrq = record.get("CODQUALIFICASRQ") != null ? (String) record.get("CODQUALIFICASRQ") : "";

		// FASE 3 : la qualifica SRQ deve non essere più obbligatoria
		// Inizio controlli apprendistato
		/*
		 * if( codRegione.equals(REGIONE_EMILIA_ROMAGNA) && (codTipoAss.equals("A.03.00") ||
		 * codTipoAss.equals("A.03.01") || codTipoAss.equals("A.03.02") || codTipoAss.equals("A.03.03") ) ) { String
		 * codRegioneAzienda = ""; Object prgAzienda = record.get("PRGAZIENDA"); Object prgUnita =
		 * record.get("PRGUNITAPRODUTTIVA"); try { codRegioneAzienda = ProcessorsUtils.getRegioneAzienda(prgAzienda,
		 * prgUnita, trans); } catch (Exception e) { return ProcessorsUtils.createResponse(prc, className, new
		 * Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "Impossibile recuperare la regione relativa all'azienda",
		 * warnings, null); }
		 * 
		 * if( codRegioneAzienda.equals(REGIONE_EMILIA_ROMAGNA) ) { if( codQualificaSrq.equals("") ) { return
		 * ProcessorsUtils.createResponse(prc, className, new
		 * Integer(MessageCodes.ImportMov.ERR_CODQUALIFICASRQ_ASSENTE), "", warnings, null); } } }
		 */
		// Fine controlli apprendistato

		/*
		 * Se il tipo Avv. è AUT, il tipo movimento non può essere 'Da Comunicazione Obbligatoria' (questo controllo non
		 * va fatto per la validazione normale e neanche per quella massiva
		 */
		if ((record.get("CONTEXT") != null) && !record.get("CONTEXT").toString().equalsIgnoreCase("valida")
				&& !record.get("CONTEXT").toString().equalsIgnoreCase("validaArchivio")
				&& !record.get("CONTEXT").toString().equalsIgnoreCase("validazioneMassiva")) {
			if (codTipoAss.equalsIgnoreCase("AUT")) {
				if (record.get("CODMONOMOVDICH").toString().equalsIgnoreCase("O")) {
					ok = false;
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_MOV_DICH), "", warnings, null);
				}
			}
		}

		// WARNING SE LAVORATORE CON ETA' MINOORE DI 18
		if ((record.get("DATINIZIOMOV") != null) && (!record.get("DATINIZIOMOV").equals(""))) {
			if (record.get("DATNASC") != null) {
				String d1 = record.get("DATINIZIOMOV").toString().substring(6, 10)
						+ record.get("DATINIZIOMOV").toString().substring(3, 5)
						+ record.get("DATINIZIOMOV").toString().substring(0, 2);
				String d2 = record.get("DATNASC").toString().substring(6, 10)
						+ record.get("DATNASC").toString().substring(3, 5)
						+ record.get("DATNASC").toString().substring(0, 2);
				Integer num1 = new Integer(d1);
				Integer num2 = new Integer(d2);

				int valore = num1.intValue() - num2.intValue();
				String eta = String.valueOf(valore);
				if (eta.length() < 6) {
					eta = new String("0").concat(eta);
				}
				if (eta.length() > 6) {
					eta = eta.substring(0, 3);
				} else {
					eta = eta.substring(0, 2);
				}
				// un lavoratore per l'apprendistato NB7 con titoli di studio
				// o con codTitolo < '4%' per essere inserito deve avere più di
				// 18 anni.

				// il controllo sul titolo di studio è cambiato in questo modo:
				// Se il lavoratore è minorenne e se il tipo di apprendistato è
				// 'A.03.02' o 'A.03.03'
				// è necessario indicare come titolo di studio almeno una
				// qualifica professionale (bloccante).

				if (codTipoAss.equals(DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE)
						|| codTipoAss.equals(DeTipoContrattoConstant.APPRENDISTATO_ALTA_FORMAZIONE)) {
					if (Integer.parseInt(eta) < 18) {
						String codTitolo = record.get("CODTIPOTITOLO") != null ? (String) record.get("CODTIPOTITOLO")
								: "";
						if (codTitolo.equals("") || Integer.parseInt(codTitolo.substring(0, 1)) < 3) {
							return ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.ImportMov.ERR_LAV_NO_TITOLO), "", warnings, null);
						}

					}
				}

				/*
				 * if(Integer.parseInt(eta) < 18 ) { String statement =
				 * "SELECT MAX(CODTITOLO) CODTITOLO FROM PR_STUDIO " + "WHERE CODMONOSTATO='C'" + "AND CDNLAVORATORE=" +
				 * record.get("CDNLAVORATORE");
				 * 
				 * SourceBean resultTitolo = null; try { resultTitolo = ProcessorsUtils.executeSelectQuery(statement,
				 * trans); } catch (Exception e) { return ProcessorsUtils.createResponse(prc, className, new
				 * Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null); }
				 * 
				 * String codTitolo=resultTitolo.containsAttribute("ROW.CODTITOLO"
				 * )?resultTitolo.getAttribute("ROW.CODTITOLO").toString():null; if(codTitolo != null){ //se codtitolo è
				 * <'4%' controllo se in am_movimento ci sono valori utili if(Integer.parseInt(codTitolo.substring(0,1))
				 * < 4){ codTitolo= (String)record.get("CODTIPOTITOLO"); } } // Se non esistono valori utili controllo
				 * in am_movimento else { codTitolo=(String)record.get("CODTIPOTITOLO"); } if(codTitolo==null ||
				 * Integer.parseInt(codTitolo.substring(0,1)) < 4) { return ProcessorsUtils.createResponse(prc,
				 * className, new Integer(MessageCodes.ImportMov.ERR_LAV_NO_TITOLO), "", warnings, null); } }
				 */

				if (codTipoAss.equals(DeTipoContrattoConstant.APPRENDISTATO_QUALIFICA_DIPLOMA_PROFESSIONALE)
						|| codTipoAss.equals(
								DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE_O_CONTRATTO_DI_MESTIERE)
						|| codTipoAss.equals(DeTipoContrattoConstant.APPRENDISTATO_ALTA_FORMAZIONE_RICERCA)
						|| codTipoAss.equals(
								DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE_O_MESTIERE_STAGIONALI)) {
					// Controlli sull'età del lavoratore
					if (codTipoAss
							.equals(DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE_O_CONTRATTO_DI_MESTIERE)
							|| codTipoAss.equals(
									DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE_O_MESTIERE_STAGIONALI)) {
						if (Integer.parseInt(eta) < 17 || Integer.parseInt(eta) >= 30) {
							SourceBean puResult = ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.DatiLavoratore.ETA_LT_17_GT_29), "", warnings, null);
							if (!context.equalsIgnoreCase("validazioneMassiva")) {
								String flagEtaApprendistato = RequestContainer.getRequestContainer().getServiceRequest()
										.containsAttribute("FORZA_INSERIMENTO_ETA_APPRENDISTATO")
												? RequestContainer.getRequestContainer().getServiceRequest()
														.getAttribute("FORZA_INSERIMENTO_ETA_APPRENDISTATO").toString()
												: "";
								if (flagEtaApprendistato.equals("")) {
									return puResult;
								} else {
									if (flagEtaApprendistato.equalsIgnoreCase("false")) {
										ProcessorsUtils.addConfirm(puResult,
												"Il lavoratore deve avere un'età compresa tra i 17 e i 29 anni alla data di assunzione. Vuoi forzare l' inserimento?",
												"forzaEtaApprendistato", new String[] { "true" }, true);
										return puResult;
									}
								}
							} else {
								return puResult;
							}
						}
					} else {
						if (codTipoAss.equals(DeTipoContrattoConstant.APPRENDISTATO_QUALIFICA_DIPLOMA_PROFESSIONALE)) {
							if (Integer.parseInt(eta) < 15 || Integer.parseInt(eta) >= 26) {
								SourceBean puResult = ProcessorsUtils.createResponse(prc, className,
										new Integer(MessageCodes.DatiLavoratore.ETA_LT_15_GT_25), "", warnings, null);
								if (!context.equalsIgnoreCase("validazioneMassiva")) {
									String flagEtaApprendistato = RequestContainer.getRequestContainer()
											.getServiceRequest()
											.containsAttribute("FORZA_INSERIMENTO_ETA_APPRENDISTATO")
													? RequestContainer.getRequestContainer().getServiceRequest()
															.getAttribute("FORZA_INSERIMENTO_ETA_APPRENDISTATO")
															.toString()
													: "";
									if (flagEtaApprendistato.equals("")) {
										return puResult;
									} else {
										if (flagEtaApprendistato.equalsIgnoreCase("false")) {
											ProcessorsUtils.addConfirm(puResult,
													"Il lavoratore deve avere un'età compresa tra i 15 e i 25 anni alla data di assunzione. Vuoi forzare l' inserimento?",
													"forzaEtaApprendistato", new String[] { "true" }, true);
											return puResult;
										}
									}
								} else {
									return puResult;
								}
							}
						} else {
							if (Integer.parseInt(eta) < 18 || Integer.parseInt(eta) >= 30) {
								SourceBean puResult = ProcessorsUtils.createResponse(prc, className,
										new Integer(MessageCodes.DatiLavoratore.ETA_LT_18_GT_29), "", warnings, null);
								if (!context.equalsIgnoreCase("validazioneMassiva")) {
									String flagEtaApprendistato = RequestContainer.getRequestContainer()
											.getServiceRequest()
											.containsAttribute("FORZA_INSERIMENTO_ETA_APPRENDISTATO")
													? RequestContainer.getRequestContainer().getServiceRequest()
															.getAttribute("FORZA_INSERIMENTO_ETA_APPRENDISTATO")
															.toString()
													: "";
									if (flagEtaApprendistato.equals("")) {
										return puResult;
									} else {
										if (flagEtaApprendistato.equalsIgnoreCase("false")) {
											ProcessorsUtils.addConfirm(puResult,
													"Il lavoratore deve avere un'età compresa tra i 18 e i 29 anni alla data di assunzione. Vuoi forzare l' inserimento?",
													"forzaEtaApprendistato", new String[] { "true" }, true);
											return puResult;
										}
									}
								} else {
									return puResult;
								}
							}
						}
					}
				} else {
					if (codTipoAss.equals(DeTipoContrattoConstant.APPRENDISTATO_DIRITTO_DOVERE_ISTRUZIONE)
							|| codTipoAss.equals(DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE)
							|| codTipoAss.equals(DeTipoContrattoConstant.APPRENDISTATO_ALTA_FORMAZIONE)) {
						// Controlli sull'età del lavoratore
						if (codTipoAss.equals(DeTipoContrattoConstant.APPRENDISTATO_DIRITTO_DOVERE_ISTRUZIONE)) {
							if (Integer.parseInt(eta) < 15) {
								warnings.add(new Warning(MessageCodes.ImportMov.WAR_BENE_LAV_APP_ISTRUZ_FORMAZ, ""));
							}
						} else {
							if (Integer.parseInt(eta) < 16) {
								return ProcessorsUtils.createResponse(prc, className,
										new Integer(MessageCodes.ImportMov.ERR_LAVORATORE_APP), "", warnings, null);
							}
						}

						if (Integer.parseInt(eta) >= 30) {
							SourceBean puResult = ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.ImportMov.ERR_LAVORATORE_APP), "", warnings, null);
							if (!context.equalsIgnoreCase("validazioneMassiva")) {
								String flagEtaApprendistato = RequestContainer.getRequestContainer().getServiceRequest()
										.containsAttribute("FORZA_INSERIMENTO_ETA_APPRENDISTATO")
												? RequestContainer.getRequestContainer().getServiceRequest()
														.getAttribute("FORZA_INSERIMENTO_ETA_APPRENDISTATO").toString()
												: "";
								if (flagEtaApprendistato.equals("")) {
									return puResult;
								} else {
									if (flagEtaApprendistato.equalsIgnoreCase("false")) {
										ProcessorsUtils.addConfirm(puResult,
												"Il lavoratore deve avere un'età compresa tra i 16 e i 29 anni alla data di assunzione. Vuoi forzare l' inserimento?",
												"forzaEtaApprendistato", new String[] { "true" }, true);
										return puResult;
									}
								}
							} else {
								return puResult;
							}
						}
						if ((Integer.parseInt(eta) >= 24)) {
							warnings.add(new Warning(MessageCodes.ImportMov.WAR_ETA_LAV_APPRENDISTA,
									"Il lavoratore, al momento dell'assunzione, ha " + eta + " anni d'et&agrave;."));
						}
					} else {
						if (Integer.parseInt(eta) < 18) {
							w = new Warning(MessageCodes.ImportMov.WAR_MINORENNE, "");
							warnings.add(w);
						}
					}
				}
			} // if(record.get("DATNASC") != null)
		} // if( (record.get("DATINIZIOMOV") != null) &&
			// (!record.get("DATINIZIOMOV").equals("")) )

		// -- Nuovi controlli

		String codAgevolazioneAvv = record.containsKey("CODAGEVOLAZIONE") ? record.get("CODAGEVOLAZIONE").toString()
				: "";
		if (!codAgevolazioneAvv.equals("") && ok) {
			if (codAgevolazioneAvv.substring(0, 1).equals("[")) {
				codAgevolazioneAvv = codAgevolazioneAvv.substring(1, codAgevolazioneAvv.length() - 1);
			}
			String[] vettAgevolazioniMov = codAgevolazioneAvv.split(",");
			boolean warninngLavProvCigs = false;
			boolean warningLavExtraprovinciale = false;
			String confermato = it.eng.sil.util.Utils.notNull(record.get("CONFIRM_DISOC_LUNGADURATA"));
			SourceBean resultDisocLungaDurata = null;
			for (int nAgev = 0; nAgev < vettAgevolazioniMov.length; nAgev++) {
				String codAgevolazCurr = vettAgevolazioniMov[nAgev].trim();

				// WARNING SE BENEFICIO PER LAVORATORE PROV. DA AZ. IN CIGS > 6
				// MESI
				// Fase2 S4 e 86 corrispondono al vecchio codagevolazione = B
				if ((codAgevolazCurr.equalsIgnoreCase("S4") || codAgevolazCurr.equalsIgnoreCase("86"))
						&& (!warninngLavProvCigs)) {
					warninngLavProvCigs = true;
					w = new Warning(MessageCodes.ImportMov.WAR_LAV_PROV_CIGS, ""); // NUOVO
					// ERRORE
					warnings.add(w);
				}

				// SE BENEFICIO PER LAVORATORE ISCRITTO IN I CLASSE > 24 MESI
				// IL LAVORATORE DEVE ESSERE UN DISOCCUPATO DI LUNGA DURATA
				if (!confermato.equals("true")) {
					if (codAgevolazCurr.equalsIgnoreCase("58") || codAgevolazCurr.equalsIgnoreCase("S8")) {
						String queryDisocLungaDurata = "select flg40790" + " from VW_AM_LAV_SITUAZ_AMMIN"
								+ " where cdnlavoratore = " + record.get("CDNLAVORATORE").toString();
						// Eseguo la query
						try {
							resultDisocLungaDurata = ProcessorsUtils.executeSelectQuery(queryDisocLungaDurata, trans);
						} catch (Exception e) {
							return ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null);
						}

						// Controllo risultato
						String flg40790 = (String) resultDisocLungaDurata.getAttribute("ROW.flg40790");
						if (!((flg40790 != null) && (flg40790.equalsIgnoreCase("S")))) {
							// return ProcessorsUtils.createResponse(prc,
							// className, new
							// Integer(MessageCodes.ImportMov.ERR_BENE_ISCR_I_CLS),
							// "", warnings, null);
							if (lavoratoreExtraprovinciale.equals("")) {
								try {
									this.lavoratoreExtraprovinciale = lavoratoreExtraProvinciale(record) ? "true"
											: "false";
								} catch (Exception e) {
									return ProcessorsUtils.createResponse(prc, className,
											new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null);
								}
							}

							if (lavoratoreExtraprovinciale.equals("true")) {
								if (record.get("CONTEXT").toString().equalsIgnoreCase("validazioneMassiva")) {
									// Se sono in validazioneMassiva allora lo
									// inserisco segnalando un warning
									if (!warningLavExtraprovinciale) {
										w = new Warning(MessageCodes.ImportMov.ERR_BENE_ISCR_I_CLS,
												"E' stato inserito un lavoratore extra-provinciale che non risulta disoccupato di lunga durata.");
										warnings.add(w);
										warningLavExtraprovinciale = true;
									}
								} else {
									// Chiedo un confirm
									SourceBean puResult = ProcessorsUtils.createResponse(prc, className,
											new Integer(MessageCodes.ImportMov.ERR_BENE_ISCR_I_CLS), "", warnings,
											null);
									ProcessorsUtils.addConfirm(puResult,
											MessageBundle.getMessage(
													Integer.toString(MessageCodes.ImportMov.ERR_BENE_ISCR_I_CLS))
													+ "\\n(Lavoratore EXTRA-PROVINCIALE)\\nVuoi forzare l' operazione?",
											"confermaInserimento", new String[] { "CONFIRM_DISOC_LUNGADURATA", "true" },
											true);
									return puResult;
								}
							} else {
								return ProcessorsUtils.createResponse(prc, className,
										new Integer(MessageCodes.ImportMov.ERR_BENE_ISCR_I_CLS), "", warnings, null);
							}

						} // if (!((flg40790 != null) &&
							// (flg40790.equalsIgnoreCase("S"))))

					} // if (codAgevolazCurr.equalsIgnoreCase("58") ||
						// codAgevolazCurr.equalsIgnoreCase("S8"))

				} // if (!confermato.equals("true"))

				// SE BENEFICIO C.f.l.
				// l'AZIENDA DEVE ESSERE UNA PA OPPURE LA DATA DI APPROVAZIONE
				// DEL CFL
				// DEVE ESSERE ANTECEDENTE AL 24/10/2004
				// FASE2: 15 e 56 corrispondono al vecchio codagevolazione = J
				if ((codAgevolazCurr.equalsIgnoreCase("56") || codAgevolazCurr.equalsIgnoreCase("15"))
						&& (!flgWarCFL)) {
					if (!codTipoAzienda.equalsIgnoreCase("PA")) {
						flgWarCFL = true;
						w = new Warning(MessageCodes.ImportMov.WAR_BENE_CFL, "");
						warnings.add(w);
					}
				}

			} // for (int nAgev=0;nAgev<vettAgevolazioniMov.length;nAgev++)

		} // if (!codAgevolazioneAvv.equals("") && ok)

		// if (record.get("CODAGEVOLAZIONE") != null && ok) {

		// SE BENEFICIO PER LAVORATORE APPRENDISTA
		// ALLA DATA DI FINE ASSUNZIONE IL LAVORATORE DEVE AVER COMPIUTO I 16
		// ANNI DI ETA' (CONTROLLO BLOCCANTE)
		// E NON SUPERARE I 29 ANNI (IN VALIDAZIONE MANUALE SI CHIEDE CONFERMA
		// ALL'OPERATORE.
		// Se sopra ai 24 anni warning non bloccante)

		// Prima era CODAGEVOLAZIONE=I o L. D0 e A0 non vanno bene perché ne
		// includono anche altre
		// MA Questo controllo viene commentato in data 5/05/08 in quanto i
		// controlli sull'età sono già stati eseguiti
		// XXX Alessandro Pegoraro cancellerebbe le seguenti righe ma le lascia
		// perché "non si sa mai"
		/*
		 * if ((record.get("CODAGEVOLAZIONE").toString().equalsIgnoreCase("D0")) ||
		 * (record.get("CODAGEVOLAZIONE").toString().equalsIgnoreCase("A0"))) { if( (record.get("DATFINEMOV") != null)
		 * && (!record.get("DATFINEMOV").equals("")) ) { if(record.get("DATNASC") != null){ String d1 =
		 * record.get("DATFINEMOV").toString().substring(6,10) + record.get("DATFINEMOV").toString().substring(3,5) +
		 * record.get("DATFINEMOV").toString().substring(0,2); String d2 =
		 * record.get("DATNASC").toString().substring(6,10) + record.get("DATNASC").toString().substring(3,5) +
		 * record.get("DATNASC").toString().substring(0,2); Integer num1 = new Integer(d1); Integer num2 = new
		 * Integer(d2);
		 * 
		 * int valore = num1.intValue() - num2.intValue(); String eta = String.valueOf(valore); if (eta.length()<6) {
		 * eta = new String("0").concat(eta); } if (eta.length()>6) { eta = eta.substring(0,3); } else { eta =
		 * eta.substring(0,2); } //Controlli sull'età del lavoratore if (Integer.parseInt(eta) < 16) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_BENE_LAV_APP), "",
		 * warnings, null); } if (Integer.parseInt(eta) >= 30) { SourceBean puResult =
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_BENE_LAV_APP), "",
		 * warnings, null); if (!context.equalsIgnoreCase("validazioneMassiva")) { String flagEtaApprendistato =
		 * RequestContainer.getRequestContainer().getServiceRequest
		 * ().containsAttribute("FORZA_INSERIMENTO_ETA_APPRENDISTATO")? RequestContainer
		 * .getRequestContainer().getServiceRequest().getAttribute
		 * ("FORZA_INSERIMENTO_ETA_APPRENDISTATO").toString():""; if (flagEtaApprendistato.equals("")) { return
		 * puResult; } else { if (flagEtaApprendistato.equalsIgnoreCase("false")) { ProcessorsUtils.addConfirm(puResult,
		 * "Il lavoratore deve avere un'età compresa tra i 16 e i 29 anni alla data di assunzione. Vuoi forzare l' inserimento?"
		 * , "forzaEtaApprendistato", new String[] {"true"}, true); return puResult; } } } else { return puResult; } } }
		 * } }
		 */

		// FASE 2: è Agevolazioni mobilità. Non si controlla più che il
		// lavoratore sia iscritto alla mobilità
		// BENEFICIO LAVORATORE GIA' l.s.u.
		// il lavoratore deve già essere in mobilità
		// FIXME Alessandro: la codifica "P" di codagevolazione NON esiste.
		// Lascio così poiché è già commentato
		/*
		 * if (record.get("CODAGEVOLAZIONE").toString().equalsIgnoreCase("P")) { String queryMob =
		 * "select * from AM_MOBILITA_ISCR, DE_MB_TIPO where "
		 * +"AM_MOBILITA_ISCR.CODTIPOMOB = DE_MB_TIPO.CODMBTIPO AND " +"DE_MB_TIPO.CODMONOATTIVA = 'A' AND to_date('" +
		 * record.get("DATINIZIOMOV").toString() + "','DD/MM/YYYY') " +" > AM_MOBILITA_ISCR.DATINIZIO AND "
		 * +"( to_date('" + record.get("DATINIZIOMOV").toString() +
		 * "','DD/MM/YYYY') < AM_MOBILITA_ISCR.DATFINE OR AM_MOBILITA_ISCR.DATFINE IS NULL) "
		 * +"AND AM_MOBILITA_ISCR.CDNLAVORATORE = " + record.get("CDNLAVORATORE") ; //Eseguo la query result = null; try
		 * { result = ProcessorsUtils.executeSelectQuery(queryMob, trans); } catch (Exception e) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "",
		 * warnings, null); }
		 * 
		 * //Controllo risultato if (!result.containsAttribute("ROW")) { return ProcessorsUtils.createResponse(prc,
		 * className, new Integer(MessageCodes.ImportMov.ERR_BENE_LSU), "", warnings, null); } }
		 */

		// }

		// codtipoAss NU9 e NU8 corrisponde a CODAGEVOLAZIONE = A)
		/*
		 * if (( record.get("CODAGEVOLAZIONE") != null //FIXME Alessandro CODAGEVOLAZIONE = "A" non esiste più dalla
		 * fase2 && record.get("CODAGEVOLAZIONE").toString().equalsIgnoreCase("A") ) ) {
		 * 
		 * String queryMob = "select * from AM_MOBILITA_ISCR, DE_MB_TIPO where " +
		 * "AM_MOBILITA_ISCR.CODTIPOMOB = DE_MB_TIPO.CODMBTIPO AND " + "DE_MB_TIPO.CODMONOATTIVA = 'A' AND to_date('" +
		 * record.get("DATINIZIOMOV").toString() + "','DD/MM/YYYY') " + " > AM_MOBILITA_ISCR.DATINIZIO AND " +
		 * "( to_date('" + record.get("DATINIZIOMOV").toString() +
		 * "','DD/MM/YYYY') < AM_MOBILITA_ISCR.DATFINE OR AM_MOBILITA_ISCR.DATFINE IS NULL) " +
		 * "AND AM_MOBILITA_ISCR.CDNLAVORATORE = " + record.get("CDNLAVORATORE") ; //Eseguo la query result = null; try
		 * { result = ProcessorsUtils.executeSelectQuery(queryMob, trans); } catch (Exception e) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "",
		 * warnings, null); }
		 * 
		 * //Controllo risultato if (!result.containsAttribute("ROW")) { return ProcessorsUtils.createResponse(prc,
		 * className, new Integer(MessageCodes.ImportMov.ERR_BENE_MOB), "", warnings, null); //Warning wrg = new
		 * Warning(MessageCodes.ImportMov.ERR_BENE_MOB,null); //warnings.add(wrg); } }
		 */

		// BENEFICIO LAVORATORE ISCRITTO IN LISTA DI MOBILITA'
		// il lavoratore deve già essere in mobilità
		// NOD E NOC Non esistono più. Gli eventuali controlli vanno fatti
		// sull'agevolazione 'A'
		/*
		 * if (( record.get("CODAGEVOLAZIONE") != null && ok &&
		 * record.get("CODAGEVOLAZIONE").toString().equalsIgnoreCase("A") ) ) { String queryMob =
		 * "select * from AM_MOBILITA_ISCR, DE_MB_TIPO where " +
		 * "AM_MOBILITA_ISCR.CODTIPOMOB = DE_MB_TIPO.CODMBTIPO AND " + "DE_MB_TIPO.CODMONOATTIVA = 'A' AND to_date('" +
		 * record.get("DATINIZIOMOV").toString() + "','DD/MM/YYYY') " + " > AM_MOBILITA_ISCR.DATINIZIO AND " +
		 * "( to_date('" + record.get("DATINIZIOMOV").toString() +
		 * "','DD/MM/YYYY') < AM_MOBILITA_ISCR.DATFINE OR AM_MOBILITA_ISCR.DATFINE IS NULL) " +
		 * "AND AM_MOBILITA_ISCR.CDNLAVORATORE = " + record.get("CDNLAVORATORE") ; //Eseguo la query result = null; try
		 * { result = ProcessorsUtils.executeSelectQuery(queryMob, trans); } catch (Exception e) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "",
		 * warnings, null); }
		 * 
		 * //Controllo risultato if (!result.containsAttribute("ROW")) { //return ProcessorsUtils.createResponse(prc,
		 * className, new Integer(MessageCodes.ImportMov.ERR_BENE_MOB), "", warnings, null); Warning wrg = new
		 * Warning(MessageCodes.ImportMov.ERR_BENE_MOB,null); warnings.add(wrg); } }
		 */

		// Nuovi controlli sui tipi di avviamento

		// ISCRITTI 24 MESI
		// NO2 non esiste più. Gli eventuali controlli vanno fatti sulle
		// agevolazioni 'D' e '03'
		// Commentato in data 5/05/08 perché il controllo c'è già (vedi sopra,
		// riga 814)
		/*
		 * String confermato = it.eng.sil.util.Utils.notNull(record.get("CONFIRM_DISOC_LUNGADURATA" )); if
		 * (!confermato.equals("true")) { //FIXME: D è una vecchia codifica, 03 NON ESISTE if
		 * (record.get("CODAGEVOLAZIONE") != null && ( record.get("CODAGEVOLAZIONE").toString().equalsIgnoreCase("D") ||
		 * record.get("CODAGEVOLAZIONE").toString().equalsIgnoreCase("03") ) ) {
		 * 
		 * String queryDisocLungaDurata = "select flg40790" + " from VW_AM_LAV_SITUAZ_AMMIN" + " where cdnlavoratore = "
		 * + record.get("CDNLAVORATORE").toString(); //Eseguo la query result = null; try { result =
		 * ProcessorsUtils.executeSelectQuery(queryDisocLungaDurata, trans); } catch (Exception e) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "",
		 * warnings, null); }
		 * 
		 * //Controllo risultato String flg40790 = (String) result.getAttribute("ROW.flg40790"); if (!((flg40790 !=
		 * null) && (flg40790.equalsIgnoreCase("S")))){
		 * 
		 * if (lavoratoreExtraprovinciale.equals("")) { try { this.lavoratoreExtraprovinciale =
		 * lavoratoreExtraProvinciale(record) ? "true" : "false"; } catch (Exception e) { return
		 * ProcessorsUtils.createResponse(prc, className,new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "",
		 * warnings, null); } }
		 * 
		 * if (lavoratoreExtraprovinciale.equals("true")) { if(record.get("CONTEXT"
		 * ).toString().equalsIgnoreCase("validazioneMassiva")){ //Se sono in validazioneMassiva allora lo inserisco
		 * segnalando un warning w = new Warning(MessageCodes.ImportMov.ERR_BENE_ISCR_I_CLS,
		 * "E' stato inserito un lavoratore extra-provinciale che non risulta disoccupato di lunga durata." );
		 * warnings.add(w); } else { //Chiedo un confirm SourceBean puResult = ProcessorsUtils.createResponse(prc,
		 * className, new Integer(MessageCodes.ImportMov.ERR_BENE_ISCR_I_CLS), "", warnings, null);
		 * ProcessorsUtils.addConfirm(puResult, MessageBundle.getMessage(Integer
		 * .toString(MessageCodes.ImportMov.ERR_BENE_ISCR_I_CLS))+
		 * "\\n(Lavoratore EXTRA-PROVINCIALE)\\nVuoi forzare l' operazione?", "confermaInserimento", new String[] {
		 * "CONFIRM_DISOC_LUNGADURATA", "true" }, true); return puResult; } } else { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_BENE_ISCR_I_CLS), "",
		 * warnings, null); }
		 * 
		 * 
		 * } } }
		 */

		// C.F.L.
		if (codTipoAss.equals(DeTipoContrattoConstant.CONTRATTO_FORMAZIONE_LAVORO) && (!flgWarCFL)) {
			if (!codTipoAzienda.equalsIgnoreCase("PA")) {
				w = new Warning(MessageCodes.ImportMov.WAR_BENE_CFL, "");
				warnings.add(w);
			}
		}

		// Apprendistato
		if (codTipoAss.equals(DeTipoContrattoConstant.APPRENDISTATO_EX_ART16)) {
			if ((record.get("DATINIZIOMOV") != null) && (!record.get("DATINIZIOMOV").equals(""))) {
				if (record.get("DATNASC") != null) {
					String d1 = record.get("DATINIZIOMOV").toString().substring(6, 10)
							+ record.get("DATINIZIOMOV").toString().substring(3, 5)
							+ record.get("DATINIZIOMOV").toString().substring(0, 2);
					String d2 = record.get("DATNASC").toString().substring(6, 10)
							+ record.get("DATNASC").toString().substring(3, 5)
							+ record.get("DATNASC").toString().substring(0, 2);
					Integer num1 = new Integer(d1);
					Integer num2 = new Integer(d2);

					int valore = num1.intValue() - num2.intValue();
					String eta = String.valueOf(valore);
					if (eta.length() < 6) {
						eta = new String("0").concat(eta);
					}
					if (eta.length() > 6) {
						eta = eta.substring(0, 3);
					} else {
						eta = eta.substring(0, 2);
					}
					// Controlli sull'età del lavoratore
					if (Integer.parseInt(eta) < 16) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_BENE_LAV_APP), "", warnings, null);
					}
					if (Integer.parseInt(eta) >= 30) {
						SourceBean puResult = ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_BENE_LAV_APP), "", warnings, null);
						if (!context.equalsIgnoreCase("validazioneMassiva")) {
							String flagEtaApprendistato = RequestContainer.getRequestContainer().getServiceRequest()
									.containsAttribute("FORZA_INSERIMENTO_ETA_APPRENDISTATO")
											? RequestContainer.getRequestContainer().getServiceRequest()
													.getAttribute("FORZA_INSERIMENTO_ETA_APPRENDISTATO").toString()
											: "";
							if (flagEtaApprendistato.equals("")) {
								return puResult;
							} else {
								if (flagEtaApprendistato.equalsIgnoreCase("false")) {
									ProcessorsUtils.addConfirm(puResult,
											"Il lavoratore deve avere un'età compresa tra i 16 e i 29 anni alla data di assunzione. Vuoi forzare l' inserimento?",
											"forzaEtaApprendistato", new String[] { "true" }, true);
									return puResult;
								}
							}
						} else {
							return puResult;
						}
					}
					if ((Integer.parseInt(eta) >= 24)) {
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_ETA_LAV_APPRENDISTA,
								"Il lavoratore, al momento dell'assunzione, ha " + eta + " anni d'et&agrave;."));
					}
				}
			}
		}

		Vector vettCM = new Vector();
		// Collocamento obbligatorio
		/*
		 * Se il lavoratore è di competenza interna alla provincia, allora il lavoratore deve essere iscritto al
		 * collocamento mirato. Controllo bloccante solo per gli interni
		 */
		if ((record.get("FLGLEGGE68") != null) && record.get("FLGLEGGE68").equals("S")) {

			if (lavCompetenteProv.equals("")) {
				try {
					this.lavCompetenteProv = lavCompetenteProv(record) ? "true" : "false";
				} catch (Exception e) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null);
				}
			}

			String codCatAssObbl = record.get("CODCATASSOBBL") != null ? (String) record.get("CODCATASSOBBL") : "";

			if (codCatAssObbl.equals("")) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_NO_CAT_ASS_OBBL), null, warnings, null);
			}
		}

		// Controllo generico per il flag assunzione di obbliglo a prescindere dalla categoria L.68/99
		String flgAssObbl = record.get("FLGASSOBBL") != null ? (String) record.get("FLGASSOBBL") : "";
		if (flgAssObbl.equalsIgnoreCase(Values.FLAG_TRUE)) {
			String codCatAssObbl = record.get("CODCATASSOBBL") != null ? (String) record.get("CODCATASSOBBL") : "";
			if (codCatAssObbl.equals("")) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_NO_CAT_LAV_ASS_OBBL), null, warnings, null);
			}
		} else {
			record.remove("CODCATASSOBBL");
		}

		SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
		String encryptKey = (String) sessione.getAttribute("_ENCRYPTER_KEY_");

		String queryCollMir = "select AM_CM_ISCR.PRGCMISCR,AM_CM_ISCR.DATDATAFINE, DE_CM_TIPO_ISCR.CODMONOTIPORAGG "
				+ " from AM_CM_ISCR, DE_CM_TIPO_ISCR, AM_DOCUMENTO DOC, AM_DOCUMENTO_COLL COLL "
				+ " where DE_CM_TIPO_ISCR.CODCMTIPOISCR = AM_CM_ISCR.CODCMTIPOISCR "
				+ " and AM_CM_ISCR.PRGCMISCR = COLL.STRCHIAVETABELLA AND COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO "
				+ " and DOC.CODTIPODOCUMENTO = 'L68' AND DOC.CODSTATOATTO = 'PR' "
				+ " and DECRYPT(AM_CM_ISCR.CDNLAVORATORE, '" + encryptKey + "') = " + record.get("CDNLAVORATORE")
				+ " and ( TRUNC(NVL(AM_CM_ISCR.DATDATAFINE, sysdate))  >= TRUNC(sysdate)) "
				+ " and DOC.CDNLAVORATORE = " + record.get("CDNLAVORATORE");

		// Eseguo la query
		result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(queryCollMir, trans);
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
					"", warnings, null);
		}
		vettCM = result.getAttributeAsVector("ROW");

		if ((record.get("FLGLEGGE68") != null) && record.get("FLGLEGGE68").equals("S")) {
			if (vettCM.size() == 0) { // non ha iscrizioni aperte alla data
				// odierna (sysdate)

				if (lavCompetenteProv.equals("true")) { // il lavoratore è di
					// competenza interna
					// alla provincia
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_AVV_COLL), "", warnings, null);
				} else { // inserisco il lavoratore segnalando con un warning
					// che non è iscritto al collocamento mirato.
					w = new Warning(MessageCodes.ImportMov.ERR_AVV_COLL,
							"E' stato inserito un lavoratore extra-provinciale.");
					warnings.add(w);
				}
			}
		}

		/*
		 * if (lavoratoreExtraprovinciale.equals("")) { try { this.lavoratoreExtraprovinciale =
		 * lavoratoreExtraProvinciale(record) ? "true" : "false"; } catch (Exception e) { return
		 * ProcessorsUtils.createResponse(prc, className,new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "",
		 * warnings, null); } }
		 * 
		 * if (lavoratoreExtraprovinciale.equals("true")) { if(record.get("CONTEXT"
		 * ).toString().equalsIgnoreCase("validazioneMassiva")){ //Se sono in validazioneMassiva allora lo inserisco
		 * segnalando un warning w = new Warning(MessageCodes.ImportMov.ERR_AVV_COLL,
		 * "E' stato inserito un lavoratore extra-provinciale in mobilità."); warnings.add(w); } else { //Chiedo un
		 * confirm SourceBean puResult = ProcessorsUtils.createResponse(prc, className, new
		 * Integer(MessageCodes.ImportMov.ERR_AVV_COLL), "", warnings, null); ProcessorsUtils.addConfirm(puResult,
		 * MessageBundle.getMessage(Integer. toString(MessageCodes.ImportMov.ERR_AVV_COLL))+
		 * "\\n(Lavoratore EXTRA-PROVINCIALE)\\nVuoi forzare l' operazione?", "confermaInserimento", new String[] {
		 * "CONFIRM_NO_MOBILITA", "true" }, true); return puResult; } } else { //Controllo risultato
		 * if(lavCompetenteProv.equals("true")) { return ProcessorsUtils.createResponse(prc, className, new
		 * Integer(MessageCodes.ImportMov.ERR_AVV_COLL), "", warnings, null); } } } }//!confermato.equals("true") }
		 */

		// Se il CODTIPOASS è uguale a "A.03.07" CONTRATTO DI INSERIMENTO
		// LAVORATIVO

		if (codTipoAss.equals(DeTipoContrattoConstant.CONTRATTO_DI_INSERIMENTO_LAVORATIVO)) {
			boolean lavDisabile = false;
			for (int i = 0; i < vettCM.size(); i++) {
				SourceBean sbApp = (SourceBean) vettCM.get(i);
				String codMonoTipoRagg = StringUtils.getAttributeStrNotNull(sbApp, "CODMONOTIPORAGG");
				if (codMonoTipoRagg.equalsIgnoreCase("D")) {
					lavDisabile = true;
					break;
				}
			}
			boolean controlData = false;
			try {
				// Calcolo la durata in mesi del contratto
				int durataContratto = Controlli.numeroMesiDiLavoro(dataI, dataF);

				// Controllo se il lavoratore è invalido e di conseguenza che il
				// contratto duri tra i 9 e 36 mesi
				if (lavDisabile) {
					if (durataContratto < 9 || durataContratto > 36) {
						controlData = true;
					}
				} else {
					// Se il lavoratore non è invalido controllo che il
					// contratto duri tra i 9 e 18 mesi
					if (durataContratto < 9 || durataContratto > 18) {
						// FASE 2: questo controllo diventa bloccante
						// return ProcessorsUtils.createResponse(prc, className,
						// new
						// Integer(MessageCodes.ImportMov.ERR_CONTRATTO_INSERIMENTO),
						// "", warnings, null);
						/*
						 * CONTROLLO POST FASE 3 : Modificare blocco in warning se contratto d'inserimento abbia durata
						 * non compresa tra 9 e 18 mesi.
						 */
						warnings.add(
								new Warning(MessageCodes.InfoMovimento.DURATA_MESI_MOVIMENTO, "esterna all'intervallo "
										+ ("9 - 18 mesi") + " previsti per il contratto di inserimento"));
						// controlData=true;
					}
				}
				// Se la durata del contratto non è valida creo un warning
				if (controlData) {
					warnings.add(new Warning(MessageCodes.InfoMovimento.DURATA_MESI_MOVIMENTO,
							"esterna all'intervallo " + ("9 - 36 mesi") + " previsti per il contratto di inserimento"));
				}
			} catch (Exception ex) {
				return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_AVV_COLL),
						" Data fine movimento o data inizio movimento NULL. Oppure data fine minore di data inizio",
						warnings, null);
			}
		}

		// Controllo se il lavoratore e' iscritto in agricoltura
		if (record.containsKey("NUMGGPREVISTIAGR")) {
			BigDecimal numGGagricoltura = null;
			if (record.get("NUMGGPREVISTIAGR") instanceof BigDecimal) {
				numGGagricoltura = (BigDecimal) record.get("NUMGGPREVISTIAGR");
			} else if (record.get("NUMGGPREVISTIAGR") instanceof String) {
				numGGagricoltura = new BigDecimal((String) record.get("NUMGGPREVISTIAGR"));

			}
			if (numGGagricoltura.intValue() > 0) {
				// Se sono in agricoltura devo controllare che alcuni campi
				// siano presenti e corretti
				// Se sono in validazione manuale o massiva il controllo va
				// effettuato solo se
				// la versione del tracciato è >= alla 2.0.0
				if ((record.get("CONTEXT") != null) && (record.get("CONTEXT").toString().equalsIgnoreCase("valida")
						|| record.get("CONTEXT").toString().equalsIgnoreCase("validaArchivio")
						|| record.get("CONTEXT").toString().equalsIgnoreCase("validazionemassiva"))) { // Sono
					// in
					// validazione
					if (record.containsKey("STRVERSIONETRACCIATO")) {
						String versioneTrac = (String) record.get("STRVERSIONETRACCIATO");
						StringTokenizer sToken = new StringTokenizer(versioneTrac, ",");
						String tk = "-1";
						if (sToken.hasMoreTokens()) {
							tk = sToken.nextToken();
						}
						if (Integer.parseInt(tk) >= 2 && Integer.parseInt(tk) < 3) {
							// In caso di validazione massiva il numLivello per
							// lavoratori in agricoltura si reperisce in un
							// apposito campo
							// della tabella di appoggio denominato
							// CODLIVELLOAGR. Occorre quindi poi inserirlo nel
							// corretto che verrà
							// memeorizzato nella tabella di movimenti.
							if (record.get("CONTEXT").toString().equalsIgnoreCase("validazionemassiva")) {
								String livello = (String) record.get("CODLIVELLOAGR");
								if (livello == null)
									livello = "";
								record.put("NUMLIVELLO", livello);
							}
						} else {
							if (record.get("NUMLIVELLO") == null) {
								if (checkForzaValidazione) {
									// modifica del livello
									record.put("NUMLIVELLO", "--");
								}
							}
						}
						// In caso di movimento in agricoltura, se sono in
						// validazione
						// NON considero il reddito perché da SARE viene
						// inserito quello giornaliero
						// e non mensile e questo provoca calcoli erronei negli
						// impatti
						if (record.containsKey("DECRETRIBUZIONEMEN")) {
							// si è deciso di non rimuovere la retribuzione
							// (Anna Paola, 23/01/2014)
							// record.remove("DECRETRIBUZIONEMEN");
						}
					} else {
						if (record.get("NUMLIVELLO") == null) {
							if (checkForzaValidazione) {
								// modifica del livello
								record.put("NUMLIVELLO", "--");
							}
						}
					}
				}
				// I campi lavorazione, categoria, livello non sono più
				// obbligatori (26/06/2007 Landi)
				/*
				 * if (controllaCampiAgricoltura) { //Eseguo i controlli sui campi inerenti l'agricoltura. String
				 * campoAgr = (String) record.get("CODLAVORAZIONE"); if (campoAgr == null || campoAgr.equals("")) {
				 * return ProcessorsUtils.createResponse(prc, className, new
				 * Integer(MessageCodes.ImportMov.ERR_LAVORAZIONE_NULL), "", warnings, null); } campoAgr = (String)
				 * record.get("NUMLIVELLO"); if (campoAgr!=null && !campoAgr.equals("")) { String queryDisocLungaDurata
				 * = "select * from de_livello_agr t " + "where t.CODLIVELLO = \'"+campoAgr+"\'"; //Eseguo la query
				 * result = null; try { result = ProcessorsUtils.executeSelectQuery(queryDisocLungaDurata, trans); }
				 * catch (Exception e) { return ProcessorsUtils.createResponse(prc, className, new
				 * Integer(MessageCodes.ImportMov.ERR_CODLIVELLOAGR_INESITENTE), "", warnings, null); } } else { return
				 * ProcessorsUtils.createResponse(prc, className, new
				 * Integer(MessageCodes.ImportMov.ERR_CODLIVELLOAGR_NULL), "", warnings, null); } campoAgr = (String)
				 * record.get("CODCATEGORIA"); if (campoAgr!=null && !campoAgr.equals("")) { String
				 * queryDisocLungaDurata = "select * from de_categoria_agr t " +
				 * "where t.CODCATEGORIA = \'"+campoAgr+"\'"; //Eseguo la query result = null; try { result =
				 * ProcessorsUtils.executeSelectQuery(queryDisocLungaDurata, trans); } catch (Exception e) { return
				 * ProcessorsUtils.createResponse(prc, className, new
				 * Integer(MessageCodes.ImportMov.ERR_CATEGORIA_INESISTENTE), "", warnings, null); } } else { return
				 * ProcessorsUtils.createResponse(prc, className, new
				 * Integer(MessageCodes.ImportMov.ERR_CATEGORIA_NULL), "", warnings, null); } }
				 */
			} else {
				if (record.get("NUMLIVELLO") == null) {
					if (checkForzaValidazione) {
						// modifica del livello
						record.put("NUMLIVELLO", "--");
					}
				}
			}
		} // Fine controlli agricoltura

		// Inizio controlli tirocinio
		/*
		 * CONTROLLO POST FASE 3 : Togliere controllo obbligatorietà campi specifici per RER (Ente promotore, numero e
		 * data convenzione, cognome e nome tutore) per i TIRONINI.
		 */
		/*
		 * String codMonoTipoAvv = record.get("CODMONOTIPO")!=null?record.get("CODMONOTIPO" ).toString():""; if (
		 * (codMonoTipoAvv.equalsIgnoreCase("T")) && (codRegione.equals(REGIONE_EMILIA_ROMAGNA)) &&
		 * (!codTipoAss.equals("") && !codTipoAss.equals("NB5")) ) { String codRegioneAzienda = ""; Object prgAzienda =
		 * record.get("PRGAZIENDA"); Object prgUnita = record.get("PRGUNITAPRODUTTIVA"); try { codRegioneAzienda =
		 * ProcessorsUtils.getRegioneAzienda(prgAzienda, prgUnita, trans); } catch (Exception e) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
		 * "Impossibile recuperare la regione relativa all'azienda", warnings, null); } try { String dataTirocinio =
		 * sbInfoGenerale.getAttribute("DATTIROCINIO") !=
		 * null?sbInfoGenerale.getAttribute("DATTIROCINIO").toString():""; if(
		 * (codRegioneAzienda.equals(REGIONE_EMILIA_ROMAGNA)) && (!dataTirocinio.equals("")) &&
		 * (DateUtils.compare(DateUtils.getNow(), dataTirocinio)>=0) ) { if ( prgAziendaUtil == null ) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_AZ_UTILIZ_ENTE), "",
		 * warnings, null); }
		 * 
		 * if ( record.get("DATAZINTINIZIOCONTRATTO") == null || record.get("STRAZINTNUMCONTRATTO") == null) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_NUM_DAT_CONV_ENTE), "",
		 * warnings, null); }
		 * 
		 * if(cognome.equals("")) { return ProcessorsUtils.createResponse(prc, className, new
		 * Integer(MessageCodes.ImportMov.ERR_NO_COGNOME_APP), null, warnings, null); } if(nome.equals("")) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_NO_NOME_APP), null,
		 * warnings, null); } } } catch (Exception e) { return ProcessorsUtils.createResponse(prc, className, new
		 * Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "Impossibile completare i controlli sui tirocini", warnings,
		 * null); } }
		 */
		// Fine controlli tirocinio

		// End Control Session

		return ProcessorsUtils.createResponse(prc, className, null, null, warnings, null);
	}

	private void checkOreSettimanali(Map record, ManualValidationFieldExtractor extractor)
			throws ControlliDecretoApplicationException {
		String codTipoOrario = extractor.estraiCodTipoOrario(record);
		if (!codTipoOrario.equals("")) {
			BigDecimal oreSettimanali = extractor.estraiOreSettimanali(record);
			List<String> orariTempoParziale = Arrays
					.asList(new String[] { CodTipoOrarioEnum.TEMPO_PARZIALE_MISTO.getCodice(),
							CodTipoOrarioEnum.TEMPO_PARZIALE_VERTICALE.getCodice(),
							CodTipoOrarioEnum.TEMPO_PARZIALE_ORIZZONTALE.getCodice() });
			if (orariTempoParziale.contains(codTipoOrario.toUpperCase()) && oreSettimanali == null)
				throw new ControlliDecretoApplicationException(
						MessageCodes.ControlliMovimentiDecreto.ERR_NUMERO_ORE_SETTIMANALI);
		}
	}

	private boolean lavoratoreExtraProvinciale(Map record) throws Exception {
		boolean lavExtraProv = false;
		String queryGetProv = "";
		String codComCpIlav = "";

		// Creo la query per la verifica di appartenenza a questa provincia
		// a seconda del contesto in cui mi trovo
		if ((record.get("CONTEXT") != null) && record.get("CONTEXT").toString().equalsIgnoreCase("valida")
				|| record.get("CONTEXT").toString().equalsIgnoreCase("validaArchivio")
				|| record.get("CONTEXT").toString().equalsIgnoreCase("validazioneMassiva")) {

			codComCpIlav = (String) record.get("CODCOMDOM");
			queryGetProv = "SELECT P.CODPROVINCIA, P.STRDENOMINAZIONE "
					+ " FROM DE_COMUNE C, DE_PROVINCIA P, TS_GENERALE G " + "WHERE C.CODPROVINCIA = P.CODPROVINCIA "
					+ "  AND P.CODPROVINCIA = G.CODPROVINCIASIL" + "  AND C.CODCOM = '" + codComCpIlav.toUpperCase()
					+ "' ";
		} else {
			codComCpIlav = (String) record.get("CODCPILAV");
			queryGetProv = "SELECT P.CODPROVINCIA, P.STRDENOMINAZIONE "
					+ "FROM DE_CPI cp, DE_COMUNE c, DE_PROVINCIA P, TS_GENERALE G " + "WHERE CP.CODCOM = C.CODCOM "
					+ "  AND C.CODPROVINCIA = P.CODPROVINCIA" + "  AND P.CODPROVINCIA = G.CODPROVINCIASIL"
					+ "  AND CP.CODCPI = '" + codComCpIlav.toUpperCase() + "'";

		}
		// Eseguo la query
		SourceBean resultProv = null;
		try {
			resultProv = ProcessorsUtils.executeSelectQuery(queryGetProv, trans);
			if (!resultProv.containsAttribute("ROW")) {
				lavExtraProv = true;
			}
		} catch (Exception e) {
			throw e;
			// return ProcessorsUtils.createResponse(prc, className,new
			// Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings,
			// null);
		}

		return lavExtraProv;
	}

	// Creo la query per verificare che il lavoratore è di competenza interna
	// alla provincia
	private boolean lavCompetenteProv(Map record) throws Exception {
		boolean lavCompetenteProv = false;
		String queryGetProv = "";
		String cdnLav = record.get("CDNLAVORATORE").toString();
		if (cdnLav != null && !cdnLav.equals("")) {
			queryGetProv = " select distinct de_cpi.CODCPI" + " from an_lav_storia_inf, de_cpi"
					+ " where an_lav_storia_inf.codcpitit =  de_cpi.codcpi" + " AND CODMONOTIPOCPI = 'T'"
					+ " and an_lav_storia_inf.CDNLAVORATORE = '" + cdnLav.toUpperCase() + "'";

			// Eseguo la query
			SourceBean resultProv = null;
			try {
				resultProv = ProcessorsUtils.executeSelectQuery(queryGetProv, trans);
				if (!resultProv.containsAttribute("ROW")) { // il lavoratore è
					// di competenza
					// interna alla
					// provincia
					lavCompetenteProv = true;
				}
			} catch (Exception e) {
				throw e;
				// return ProcessorsUtils.createResponse(prc, className,new
				// Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "",
				// warnings, null);
			}
		}

		return lavCompetenteProv;
	}

}// end class