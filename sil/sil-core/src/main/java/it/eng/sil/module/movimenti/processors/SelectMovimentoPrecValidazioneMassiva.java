/*
 * Creato il 29-dic-04
 */
package it.eng.sil.module.movimenti.processors;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.constant.TipiTrasfConstant;
import it.eng.sil.module.movimenti.enumeration.CodMonoTempoEnum;
import it.eng.sil.module.movimenti.enumeration.CodiceVariazioneEnum;
import it.eng.sil.module.movimenti.enumeration.TipoTrasfEnum;
import it.eng.sil.module.movimenti.extractor.ManualValidationFieldExtractor;
import it.eng.sil.util.UtilityNumGGTraDate;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;

/**
 * @author roccetti Questa classe effettua la gestione del movimento precedente in validazione massiva
 */
public class SelectMovimentoPrecValidazioneMassiva extends AbstractSelectMovimentoPrecedente {

	/** Dati per la risposta */

	private BigDecimal user;
	private String configFileName;
	private boolean checkForzaValidazione = false;
	private boolean checkForzaModifiche = false;

	/**
	 * Costruttore
	 * <p>
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            TransactionQueryExecutor da utilizzare per le query
	 * 
	 **/
	public SelectMovimentoPrecValidazioneMassiva(String name, TransactionQueryExecutor transexec,
			RecordProcessor gestoreCVE, BigDecimal user, String configFileName) {
		super(name, transexec, gestoreCVE);
		this.user = user;
		this.configFileName = configFileName;
		checkForzaValidazione = ProcessorsUtils.checkForzaValidazione(transexec);
		checkForzaModifiche = ProcessorsUtils.checkForzaModifiche(transexec);
	}

	@Override
	public String getClassName() {
		return this.getClass().getName();
	}

	/**
	 * Processa il record. Cerca la proprieta PRGAZIENDA, PRGUNITA, CDNLAVORATORE, DATINIZIOMOV e PRGAZIENDAUTIL,
	 * PRGUNITAUTIL se esistono e sceglie (se riesce) il movimento precedente dalla tabella AM_MOVIMENTO. Inserisce nel
	 * record i dati necessari al record corrente (già corretti nel caso debbano essere elaborati).
	 * <p>
	 * Modifica del 14/09/2004: Possibilita di inserire movimenti di trasformazione e cessazione senza il movimento
	 * precedente. Per i suddetti movimenti che contengono la proprietà COLLEGATO="nessuno" si deve consentire
	 * l'inserimento senza movimento precedente.
	 * <p>
	 * 
	 * @exception SourceBeanException
	 *                Se avviene un errore nella creazione del SourceBean di risposta
	 */
	public SourceBean customProcessRecord(Map record, ArrayList warnings, ArrayList nested) throws SourceBeanException {

		String name = this.getName();
		String classname = this.getClassName();
		TransactionQueryExecutor trans = this.getTrans();
		RecordProcessor gestoreCVE = this.getGestoreCVE();

		// segnala l'esistenza di un movimento doppio o simile
		boolean doppio = false;
		SourceBean res = null;

		// Controllo che i parametri necessari per la query di select ci siano
		String codTipoMov = (String) record.get("CODTIPOMOV");
		String datInizioMov = (String) record.get("DATINIZIOMOV");
		String datFineMov = (String) record.get("DATFINEMOV");
		BigDecimal prgAzienda = (BigDecimal) record.get("PRGAZIENDA");
		BigDecimal prgUnita = (BigDecimal) record.get("PRGUNITAPRODUTTIVA");
		BigDecimal cdnLav = (BigDecimal) record.get("CDNLAVORATORE");
		BigDecimal prgAzUtilMovCurr = (BigDecimal) record.get("PRGAZIENDAUTIL");
		String codMonoTempo = (String) record.get("CODMONOTEMPO");
		String codTipoTrasf = (String) record.get("CODTIPOTRASF");
		String codFiscAziendaPrec = record.get("STRCODICEFISCALEAZPREC") != null
				? record.get("STRCODICEFISCALEAZPREC").toString()
				: "";
		String codComAzPrec = record.get("CODCOMAZPREC") != null ? record.get("CODCOMAZPREC").toString() : "";
		String indirizzoAzPrec = record.get("STRINDIRIZZOAZPREC") != null ? record.get("STRINDIRIZZOAZPREC").toString()
				: "";
		String ragSocAzPrec = record.get("STRRAGIONESOCIALEAZPREC") != null
				? record.get("STRRAGIONESOCIALEAZPREC").toString()
				: "";
		String codTipoContratto = record.get("CODTIPOASS").toString();
		BigDecimal prgAziendaPrec = null;
		boolean trasferimentoAzienda = false;
		boolean distacco = false;
		if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE) && codTipoTrasf != null
				&& codTipoTrasf.equalsIgnoreCase(TipoTrasfEnum.DISTACCO_COMANDO.getCodice())) {
			distacco = true;
		}
		// eseguiQueryMovPrec = false quando si tratta di un trasferimento
		// d'azienda e
		// nel db non è presente l'azienda relativa al movimento precedente (in
		// base al campo STRCODICEFISCALEAZPREC)
		boolean eseguiQueryMovPrec = true;

		/*
		 * 15/03/2005 DAVIDE: modifica alla ricerca di un movimento precedente che permetta di collegare mov precedenti
		 * che abbiano la stessa Testata Azienda anche se unità diverse.
		 * 
		 * DONA 22/02/2007: visualizza nel messaggio di errore anche il campo che manca
		 */
		// (21/06/2007 Landi)Per le proroghe la data inizio movimento proroga
		// non è più presente nel tracciato e va
		// valorizzata a seconda del movimento precedente (data fine mov
		// precedente + 1)

		// (05/07/2012 Landi) Per le trasformazioni ZZ(in fase di acquisizione
		// le proroghe dei nuovi contratti di apprendistato a T.I
		// vengono trasformate in TRA con codTipoTrasf = ZZ), la data inizio
		// trasformazione = data fine periodo formativo del movimento
		// precedente + 1

		boolean checkDatiMancanti = false;
		String msgError = "Impossibile elaborare il movimento, dati mancanti.";
		String addMsg = "";
		if (prgAzienda == null || cdnLav == null || codTipoMov == null) {
			checkDatiMancanti = true;
			if (prgAzienda == null) {
				addMsg = addMsg + " Non è stata specificata l'azienda.";
			}
			if (cdnLav == null) {
				addMsg = addMsg + " Non è stato specificato il lavoratore.";
			}
			if (codTipoMov == null) {
				addMsg = addMsg + " Non è stato specificats il tipo di movimento.";
			}
		}

		if (datInizioMov == null) {
			if (!(codTipoMov.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE) && codTipoTrasf != null
					&& codTipoTrasf.equalsIgnoreCase(TipoTrasfEnum.PROSECUZIONE_PERIODO_FORMATIVO.getCodice()))) {
				if (!codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
					checkDatiMancanti = true;
					addMsg = addMsg + " Non è stato specificata la data inizio del movimento.";
				}
			}
		}

		if (checkDatiMancanti) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					msgError + addMsg, warnings, nested);
		}

		// Se sto inserendo un'assunzione non devo cercare nulla
		// ===============================
		if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_ASSUNZIONE)) {
			// Imposto la data di inizio dell'avviamento originario e quella di
			// inizio del movimento precedente
			record.put("DATAINIZIOAVV", datInizioMov);
			record.put("DATINIZIOMOVPREC", datInizioMov);
			// Aggiungo al record il nuovo valore di collegato
			record.put("COLLEGATO", "nessuno");
			return null;
		}

		// STOP !!
		// =====================================================================================

		ManualValidationFieldExtractor extractor = new ManualValidationFieldExtractor();
		String codVariazione = extractor.estraiCodVariazione(record);
		String _codTipoAzienda = extractor.estraiTipoAzienda(record);
		String datinizioMis = extractor.estraiInizioMissione(record);
		String codMonoTmp = extractor.estraiCodMonoTempo(record);

		// GESTIONE DI PROROGHE DELLA MISSIONE PER CONTRATTI A TEMPO INDETERMINATO CON CODICE VARIAZIONE 2.03
		// IN QUESTO CASO NON DEVO CERCARE IL MOVIMENTO PRECEDENTE IN QUANTO LA VALIDAZIONE COMPORTA L'AGGANCIO
		// DELLA MISSIONE A UN MOVIMENTO A TEMPO INDETERMINATO GIA' PRESENTE SULLA TABELLA AM_MOVIMENTO
		if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA) && _codTipoAzienda.equalsIgnoreCase("INT")
				&& !datinizioMis.equals("") && codMonoTmp.equalsIgnoreCase(CodMonoTempoEnum.INDETERMINATO.getCodice())
				&& codVariazione.equalsIgnoreCase(
						CodiceVariazioneEnum.PROROGA_MISSIONE_RAPPORTO_TEMPO_INDETERMINATO.getCodice())) {
			try {
				return agganciaMissioneProATI(extractor, record, warnings, nested);
			} catch (Exception excPro) {
				return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.General.OPERATION_FAIL),
						"Errore nella validazione della proroga della missione", warnings, nested);
			}
		}

		doppio = esisteDoppio(record);

		// =====================================================================================
		// CESSAZIONE VELOCE
		//
		// Se non ho movimenti precedenti sul DB e si tratta di una CEV provo ad
		// inserire l'avviamento
		//
		if (!doppio && checkCEV(record, codTipoMov) && !esistePrecedenteSuDB(record)) {
			if (gestoreCVE != null) {
				SourceBean insertAvvResult = gestoreCVE.processRecord(record);
				if (insertAvvResult != null) {
					nested.add(insertAvvResult);
					// Stefy - 07/03/2006
					// se ci sono warning sull'utilizzo della prima unità
					// aziendale del comune
					// devo propagarli al movimento di cessazione
					res = ProcessorsUtils.createResponse(name, classname, null, "", warnings, nested);
					// Se ho errori nell'inserimento dell'avvimento ritorno con
					// errore
					if (ProcessorsUtils.isError(insertAvvResult)) {
						if (!checkForzaValidazione) {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA), null, warnings, nested);
						}
					} else {
						if (ProcessorsUtils.isWarningAndSTOP(insertAvvResult)) {
							try {
								String oggi = DateUtils.getNow();
								if (DateUtils.compare(oggi, datInizioMov) == -1) {
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
											"Impossibile inserire movimento orfano con data inizio maggiore della data attuale.",
											warnings, nested);
								}
							} catch (Exception e) {
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
										"formato data non corretto", warnings, nested);
							}
							Integer insScollegatoResult = ProcessorsUtils.checkInserimentoScollegato(codTipoMov,
									codMonoTempo, cdnLav, datInizioMov, trans);
							if (insScollegatoResult == null) {
								record.put("COLLEGATO", "nessuno");
								return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
							} else {
								return ProcessorsUtils.createResponse(name, classname, insScollegatoResult, null,
										warnings, nested);
							}
						}
					}
				}
			}
		}
		// =====================================================================================
		// Cerco il movimento precedente per impostarne i campi nel movimento
		// corrente
		BigDecimal prgmovprec = (BigDecimal) record.get("PRGMOVIMENTOPREC");
		// Gestione trasferimenti d'azienda nella validazione massiva dei
		// movimenti.
		if (codTipoTrasf != null && codTipoMov.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE)) {
			trasferimentoAzienda = (record.get("FLGTRASFER") != null
					&& record.get("FLGTRASFER").toString().equalsIgnoreCase("S")) ? true : false;
			if (trasferimentoAzienda) {
				if (!codFiscAziendaPrec.equals("")) {
					Vector prgAzList = ProcessorsUtils.cercaTrasfAziendaPrec(codFiscAziendaPrec, trans);
					if (prgAzList.size() == 1) {
						SourceBean azPrec = (SourceBean) prgAzList.get(0);
						prgAziendaPrec = new BigDecimal(azPrec.getAttribute("PRGAZIENDA").toString());
					}
				}
			}
		}
		// query per determinare il movimento precedente
		String selectquery = "SELECT PrgMovimento," + "PRGAZIENDA," + "PRGUNITA," + "PRGAZIENDAUTILIZ,"
				+ "PRGUNITAUTILIZ," + "NumProroghe, " + "CodMonoTempo, " + "NumKloMov, "
				+ "TO_CHAR(DatInizioMov, 'DD/MM/YYYY') DatInizioMov, " + "DecRetribuzioneMen, "
				+ "TO_CHAR(DatFineMovEffettiva, 'DD/MM/YYYY') DatFineMov, "
				+ "TO_CHAR(AM_MOVIMENTO.datFinePF, 'DD/MM/YYYY') datFinePF, "
				+ "TO_CHAR(DatInizioAvv, 'DD/MM/YYYY') DatInizioAvv, " + "codTipoMov, " + "codOrario, " + "numOreSett, "
				+ "strNote, " + "DE_TIPO_CONTRATTO.CODMONOTIPO CODMONOTIPOASS, "
				+ "AM_MOVIMENTO.CODMVCESSAZIONE CODMVCESSAZIONE, " + "AM_MOVIMENTO.CODTIPOTRASF, "
				+ "AM_MOVIMENTO.CODMANSIONE, " + "AM_MOVIMENTO.CODTIPOCONTRATTO CODTIPOASS "
				+ "FROM AM_MOVIMENTO, DE_TIPO_CONTRATTO ";

		if (prgmovprec == null) {
			// Creo la query se non ho il precedente

			if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) {
				selectquery += " WHERE CODSTATOATTO = 'PR' "
						+ " AND ( (AM_MOVIMENTO.CODTIPOMOV = 'CES' AND NVL(AM_MOVIMENTO.CODMVCESSAZIONE, ' ') = 'SC') OR "
						+ " (CODTIPOMOV != 'CES') ) ";
			} else {
				selectquery += " WHERE CODTIPOMOV != 'CES' AND CODSTATOATTO = 'PR' ";
			}
			// 04/01/2006 - Stefy
			// modificata la ricerca del precedente se ci sono movimenti simili
			// a quello che si sta validando
			if (doppio) {
				selectquery += "AND (prgmovimentosucc IS NULL or exists "
						+ "(select 1 from am_movimento am1 where am1.prgmovimento = am_movimento.prgmovimentosucc "
						+ "and am1.codtipomov = '" + codTipoMov + "' and am1.codmonomovdich in ('D','C')) )";
			} else {
				selectquery += "AND PRGMOVIMENTOSUCC IS NULL ";
			}
			selectquery += "AND AM_MOVIMENTO.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO(+)  "
					+ (codTipoMov.equals(MovimentoBean.COD_TRASFORMAZIONE)
							? "AND (NVL(DE_TIPO_CONTRATTO.CODMONOTIPO,'0') != 'N' OR "
									+ "AM_MOVIMENTO.CODTIPOCONTRATTO IN ("
									+ DeTipoContrattoConstant.listaContrattiAut_Tra + ")) "
							: "");

			StringBuffer buf = new StringBuffer();
			if (!cdnLav.equals("")) {
				buf.append(" AND CDNLAVORATORE = " + cdnLav + " ");
			}
			if (trasferimentoAzienda) {
				if (prgAziendaPrec != null) {
					// in questo caso cerco il movimento precedente dai dati
					// dell'azienda precedente
					// contenuti nel movimento che si sta validando (caso di
					// trasferimento d'azienda)
					buf.append(" AND PRGAZIENDA = " + prgAziendaPrec + " ");
				} else {
					eseguiQueryMovPrec = false;
				}
			} else {
				if (!prgAzienda.equals("")) {
					buf.append(" AND PRGAZIENDA = " + prgAzienda + " ");
				}
			}
			// nel caso di distacco, nuovi criteri per la ricerca
			if (distacco) {
				buf.append(" AND ((CODTIPOMOV in ('AVV','PRO')) OR (CODTIPOMOV = 'TRA' AND CODTIPOTRASF = 'DL' ");
				if (prgAzUtilMovCurr != null) {
					buf.append("AND PRGAZIENDADIST = " + prgAzUtilMovCurr);
				}
				buf.append("))");
				String dataFineDistacco = record.get("DATFINEDISTACCO") != null
						? record.get("DATFINEDISTACCO").toString()
						: "";
				if (dataFineDistacco.equals("")) {
					dataFineDistacco = DateUtils.getNow();
				}
				buf.append(" AND TRUNC(NVL(DATFINEMOVEFFETTIVA, TO_DATE('" + dataFineDistacco
						+ "', 'DD/MM/YYYY'))) >= TO_DATE('" + dataFineDistacco + "', 'DD/MM/YYYY') ");
			} else {
				// La data di fine del movimento cercato deve corrispondere con quella di inizio -1 del movimento
				// corrente nel caso di Proroghe per tracciati che
				// prevedevano la presenza della data inizio proroga.
				// Siccome la data inizio proroga non viene più comunicata, allora la data di fine del movimento cercato
				// deve essere minore della data fine proroga
				// Nel caso di trasf/cessazioni la data di inizio del movimento
				// precedente deve essere
				// anteriore alla data di inizio della trasformazione/cessazione
				if (datInizioMov != null && !datInizioMov.equals("")) {
					// proroghe
					if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
						buf.append(" AND CODMONOTEMPO = 'D' ");
						buf.append(
								" AND TRUNC(DATFINEMOVEFFETTIVA) = TO_DATE('" + datInizioMov + "', 'DD/MM/YYYY') - 1 ");
					} else {
						// trasformazioni
						if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE)) {
							buf.append(" AND TRUNC(DATINIZIOMOV) <= TO_DATE('" + datInizioMov + "', 'DD/MM/YYYY') ");
							if (codMonoTmp.equalsIgnoreCase(CodMonoTempoEnum.INDETERMINATO.getCodice())) {
								// data fine movimento precedente >= giorno precedente data trasformazione
								boolean erroreControlloDataFine = false;
								String dataMaxFineMovPrecedente = "";
								try {
									dataMaxFineMovPrecedente = DateUtils.giornoPrecedente(datInizioMov);
								} catch (Exception eData) {
									erroreControlloDataFine = true;
								}
								if (!erroreControlloDataFine) {
									buf.append(" AND TRUNC(NVL(DATFINEMOVEFFETTIVA, TO_DATE('"
											+ dataMaxFineMovPrecedente + "', 'DD/MM/YYYY'))) >= TO_DATE('"
											+ dataMaxFineMovPrecedente + "', 'DD/MM/YYYY') ");
								}
							} else {
								if (codMonoTmp.equalsIgnoreCase(CodMonoTempoEnum.DETERMINATO.getCodice())) {
									// movimento precedente a TD e data fine movimento precedente >= data trasformazione
									buf.append(" AND CODMONOTEMPO = 'D' ");
									buf.append(" AND TRUNC(NVL(DATFINEMOVEFFETTIVA, TO_DATE('" + datInizioMov
											+ "', 'DD/MM/YYYY'))) >= TO_DATE('" + datInizioMov + "', 'DD/MM/YYYY') ");
								}
							}
						}
						// cessazioni
						else {
							if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) {
								buf.append(
										" AND TRUNC(DATINIZIOMOV) <= TO_DATE('" + datInizioMov + "', 'DD/MM/YYYY') ");
							}
						}
					}
				} else {
					// data inizio movimento può essere vuota per le proroghe
					// importate con il nuovo tracciato
					if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
						buf.append(" AND CODMONOTEMPO = 'D' ");
						buf.append(" AND TRUNC(DATFINEMOVEFFETTIVA) < TO_DATE('" + datFineMov + "', 'DD/MM/YYYY') ");
					}
				}
			}
			selectquery = selectquery + buf;
		} else {
			if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) {
				selectquery += " WHERE AM_MOVIMENTO.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO(+)  "
						+ " AND CODSTATOATTO = 'PR' AND PRGMOVIMENTO = " + prgmovprec
						+ " AND ( (AM_MOVIMENTO.CODTIPOMOV = 'CES' AND NVL(AM_MOVIMENTO.CODMVCESSAZIONE, ' ') = 'SC') OR "
						+ " (CODTIPOMOV != 'CES') ) ";
			} else {
				// creo la query se ho il precedente
				selectquery += " WHERE CODTIPOMOV != 'CES' "
						+ "AND AM_MOVIMENTO.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO(+)  "
						+ "AND CODSTATOATTO = 'PR' AND PRGMOVIMENTO = " + prgmovprec + " "
						+ (codTipoMov.equals(MovimentoBean.COD_TRASFORMAZIONE)
								? "AND (NVL(DE_TIPO_CONTRATTO.CODMONOTIPO,'0') != 'N' OR "
										+ "AM_MOVIMENTO.CODTIPOCONTRATTO IN ("
										+ DeTipoContrattoConstant.listaContrattiAut_Tra + ")) "
								: "");
			}
		}

		if (distacco) {
			selectquery += " ORDER BY DECODE(AM_MOVIMENTO.CODTIPOMOV, 'TRA', 2, 'AVV', 1, 0), AM_MOVIMENTO.DATINIZIOMOV DESC ";
		} else {
			// DONA 12/02/2007: ordinamento query per datInizioMov
			selectquery += " ORDER BY AM_MOVIMENTO.DATINIZIOMOV DESC ";
		}

		SourceBean result = null;
		Vector v = new Vector();
		// Esecuzione query per determinare il movimento precedente
		try {
			if (eseguiQueryMovPrec) {
				result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
				v = result.getAttributeAsVector("ROW");
			}
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
					"Impossibile cercare il movimento precedente", warnings, nested);
		}

		// Esamino il risultato
		SourceBean rowPrec = null;

		if (v.size() == 0) {
			if (distacco) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_FIND_MOV_PREC),
						"Non è stato individuato nessun movimento da utilizzare come precedente per quello da inserire.",
						warnings, nested);
			}
			boolean errorFlag = true;
			// DONA 13/02/2007:
			// in caso di validazione forzata se non viene individuato il
			// precedente
			if (checkForzaValidazione && checkForzaModifiche) {
				// DONA 12/02/2007: par. 3.3.2 Proroghe senza precedente
				// in caso di validazione forzata se per una proroga non viene
				// individuato il precedente
				// si ripete la ricerca il movimento modificando il controllo
				// sulla data
				// 11/07/2007 aggiunto flag FORZAMOD: se TRUE le modifiche
				// vengono effettuate altrimenti NO
				if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
					Vector vectPro = new Vector();
					SourceBean resultMovPrec = null;
					// 21/06/2007 Landi (quando la data inizio della proroga non
					// è presente, nel caso di tracciato
					// nuovo, allora è inutile ripetere la ricerca perché è
					// stata fatta già in precedenza cercando
					// tra i movimenti con data fine movimento < data fine
					// proroga)
					if (datInizioMov != null && !datInizioMov.equals("")) {
						String sqlQuery = getQueryRicercaForzataMovPrec(record, doppio);
						try {
							resultMovPrec = ProcessorsUtils.executeSelectQuery(sqlQuery, trans);
						} catch (Exception e) {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
									"Impossibile cercare il movimento precedente", warnings, nested);
						}
						if (resultMovPrec != null) {
							vectPro = resultMovPrec.getAttributeAsVector("ROW");
						}
					}
					if (vectPro.size() == 0) {
						// non ci sono risultati allora setto l'errore
						errorFlag = true;
					} else if (vectPro.size() > 0) {
						// Quando la ricerca viene eseguita, allora datInizioMov
						// (data inizio proroga) è presente nel
						// tracciato, altrimenti la chiamata a
						// getQueryRicercaForzataMovPrec non viene fatta
						SourceBean rowMov = (SourceBean) vectPro.get(0);
						String dataInizioMovPrec = (String) rowMov.getAttribute("DatInizioMov");
						if (dataInizioMovPrec != null || ("").equals(dataInizioMovPrec)) {

							String dataFineMovPrec = (String) rowMov.getAttribute("DATFINEMOV");
							String newDataInizio = UtilityNumGGTraDate.sottraiGiorni(datInizioMov, 1);
							String strNoteMovPrec = StringUtils.getAttributeStrNotNull(rowMov, "STRNOTE");
							// aggiorno la data fine del movimento precedente
							rowMov.updAttribute("DatFineMov", newDataInizio);
							// aggiorno la note del movimento precedente
							String notaModifica = "<li>E' stata modificata la data fine del movimento " + "<br>da "
									+ dataFineMovPrec + " a " + newDataInizio + " per renderlo compatibile "
									+ "con la PRO successiva collegata </li>";
							strNoteMovPrec = strNoteMovPrec + notaModifica;
							rowMov.updAttribute("STRNOTE", strNoteMovPrec);

							rowPrec = rowMov;
							// non deve comparire il messaggio di errore
							errorFlag = false;

							// inserisco la nota
							String strNote = (String) record.get("STRNOTE");
							if (strNote != null) {
								strNote = strNote
										+ "<li>Il movimento precedente collegato potrebbe non essere quello corretto.</li>";
							} else {
								strNote = "<li>Il movimento precedente collegato potrebbe non essere quello corretto.</li>";
							}
							record.put("STRNOTE", strNote);
						}
					}
				} else {
					if (codMonoTempo == null || codMonoTempo.equals("")) {
						// setto codMonoTempo = "I" nel caso non ci sia e sto
						// cercando di inserire un movimento senza precedente
						codMonoTempo = "I";
						record.put("CODMONOTEMPO", codMonoTempo);
					}
					Integer insScollegatoResult = ProcessorsUtils.checkInserimentoScollegato(codTipoMov, codMonoTempo,
							cdnLav, datInizioMov, trans);
					if (insScollegatoResult == null) {
						// Imposto la data di inizio del movimento originario e
						// quella di inizio del movimento precedente
						record.put("DATAINIZIOAVV", datInizioMov);
						record.put("DATINIZIOMOVPREC", datInizioMov);
						// DONA: 3.3.3 nessun movimento precedente
						// Aggiungo al record il nuovo valore di collegato
						record.put("COLLEGATO", "nessuno");
						try {
							if (datInizioMov != null && !datInizioMov.equals("")
									&& DateUtils.compare(DateUtils.getNow(), datInizioMov) < 0) {
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
										"Impossibile inserire trasformazione o cessazione senza precedente con data inizio maggiore della data attuale.",
										warnings, nested);
							}
						} catch (Exception e) {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "formato data non corretto",
									warnings, nested);
						}
						try {
							// Il seguente processor cerca l'unità azienda nel
							// DB e se la trova aggiorna la Map record
							GestisciUnitaAziendaXValMass cercaUnita = new GestisciUnitaAziendaXValMass(
									"Cerca Unita Azienda nel DB", trans, user, configFileName + "insertAzienda.xml",
									checkForzaValidazione);
							SourceBean resultUnita = cercaUnita.processRecord(record);
							if (resultUnita != null) {
								nested.add(resultUnita);
							}
						} catch (Exception e) {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_RICERCA_UNITA),
									"Problemi con il reperimento dell'unita azienda sul DB.", warnings, nested);
						}
						return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
					} else {
						return ProcessorsUtils.createResponse(name, classname, insScollegatoResult, null, warnings,
								nested);
					}
					// non bisogna aggiornare la nota
				}
			}

			if (errorFlag) {
				String errorMSG = "Non è stato individuato nessun movimento da utilizzare come precedente per quello da inserire.";
				if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
					errorMSG += "<br/>&nbsp;&nbsp;&nbsp;Il movimento è una proroga, è possibile che non si sia trovato un precedente per incongruenza di date ";
					errorMSG += "<br/>&nbsp;&nbsp;&nbsp;oppure il movimento è una SOMMINISTRAZIONE di AVVIAMENTO erronea che il sistema considera come PROROGA.";
				}
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_FIND_MOV_PREC), errorMSG, warnings, nested);
			}
		} else if (v.size() == 1) {
			rowPrec = (SourceBean) v.get(0);
			/*
			 * // CONTROLLO SUL TIPO DI AVVIAMENTO: NON E' POSSIBILE PROROGARE O TRASFORMARE UNA ASSUNZIONE AVVENUTA //
			 * CON UN TIROCINIO (DE_MV_TIPO_ASS.CODMONOTIPO = 'T') if
			 * ((codTipoMov.equals(MovimentoBean.COD_TRASFORMAZIONE)||codTipoMov.equals(MovimentoBean.COD_PROROGA)) &&
			 * rowPrec.getAttribute("CODMONOTIPOASS")!=null) { String codMonoTipoAss =
			 * (String)rowPrec.getAttribute("CODMONOTIPOASS"); if (codMonoTipoAss.equals("T") ) return
			 * ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_FIND_MOV_PREC),
			 * "Non è stato individuato nessun movimento da utilizzare come precedente per quello da inserire." ,
			 * warnings, nested); }
			 */
		} else {
			if (distacco) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_FIND_MOV_PREC),
						"Impossibile individuare univocamente il movimento da utilizzare come precedente per quello da inserire, "
								+ "ne esiste più di uno compatibile!",
						warnings, nested);
			}
			/*
			 * 16/03/2005 DAVIDE Se ho più di un risultato vuol dire che per quella testata azienda ci sono più
			 * movimenti aperti. Controllo allora l'unità azienda di ogni movimento aperto e vedo se c'è ne una che
			 * corrisponde al movimento che sto importando. In tal caso posso associare il movimento precedente
			 * altrimenti segnalo l'impossibilità di procedere.
			 */
			if (distacco) {
				// nel caso di trasformazione di DISTACCO, come movimento
				// precedente prendo il primo
				// ritornato dalla query (ordinamento su (codtipomov,
				// datiniziomov) decrescente)
				rowPrec = (SourceBean) v.get(0);
			} else {
				boolean findPrec = false;
				boolean findPrecMaxDatInizio = false;
				SourceBean row = null;
				String codTipoComunic = (String) record.get("CODTIPOCOMUNIC");
				// Se si tratta di una rettifica di una comunicazione
				// precedente, allora cerco
				// di selezionare come movimento precedente quello annullato in
				// precedenza,
				// il cui prgMovimento si trova in
				// PRGMOVIMENTOPREC_RETTIFICACOMUNICAZIONE
				if (codTipoComunic != null
						&& codTipoComunic.equals(MessageCodes.General.RETTIFICA_COMUNICAZIONE_PREC)) {
					Object prgMovimentoPrecRettificaCom = record.get("PRGMOVIMENTOPREC_RETTIFICACOMUNICAZIONE");
					if (prgMovimentoPrecRettificaCom != null) {
						for (int i = 0; (i < v.size() && !findPrec); i++) {
							row = (SourceBean) v.get(i);
							Object prgMovimento = row.getAttribute("PRGMOVIMENTO");
							if (prgMovimento.equals(prgMovimentoPrecRettificaCom)) {
								rowPrec = row;
								findPrec = true;
							}
						}
					}
				}
				if (!findPrec) {
					// Cerco in base al prgUnita
					try {
						// Il seguente processor cerca l'unità azienda nel DB e
						// se la trova aggiorna la Map record
						// nel caso in cui non sia un trasferimento d'azienda
						SourceBean resultUnita = null;
						GestisciUnitaAziendaXValMass cercaUnita = new GestisciUnitaAziendaXValMass(
								"Cerca Unita Azienda nel DB", trans, user, configFileName + "insertAzienda.xml",
								checkForzaValidazione);
						if (trasferimentoAzienda && prgAziendaPrec != null) {
							prgUnita = null;
							Vector vettPrgUnita = new Vector();
							resultUnita = cercaUnita.processRecord(record, prgAziendaPrec, ragSocAzPrec, codComAzPrec,
									indirizzoAzPrec, vettPrgUnita);
							if (resultUnita != null) {
								nested.add(resultUnita);
							}
							if (vettPrgUnita.size() > 0) {
								prgUnita = (BigDecimal) vettPrgUnita.get(0);
							}
						} else {
							resultUnita = cercaUnita.processRecord(record);
							if (resultUnita != null) {
								nested.add(resultUnita);
							}
							// Riassegno il prgUnita
							prgUnita = (BigDecimal) record.get("PRGUNITAPRODUTTIVA");
						}
					} catch (Exception e) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_FIND_MOV_PREC),
								"Problemi con il reperimento dell'unita azienda sul DB. Impossibile risalire al movimento precedente",
								warnings, nested);
					}
					if (prgUnita != null) {
						for (int i = 0; (i < v.size() && !findPrec); i++) {
							row = (SourceBean) v.get(i);
							BigDecimal prgUnitaMovPrec = (BigDecimal) row.getAttribute("PRGUNITA");
							if (prgUnita.equals(prgUnitaMovPrec)) {
								rowPrec = row;
								findPrec = true;
							}
						}
					}
				}

				if (!findPrec) {
					// DONA 12/02/2007: par. 3.3.1 Movimento precedente univoco
					// se esiste flag forzatura allora recupero come movimento
					// prec quello con datInizioMov più recente
					// il movimento con datInizioMov maggiore deve essere il
					// primo (query ordinata con datInizioMov desc)
					if (checkForzaValidazione) {
						SourceBean rowMov = (SourceBean) v.get(0);
						String dataInizioMovPrec = (String) rowMov.getAttribute("DatInizioMov");
						if (dataInizioMovPrec != null || ("").equals(dataInizioMovPrec)) {
							findPrecMaxDatInizio = true;

							String strNoteMovPrec = (String) rowMov.getAttribute("STRNOTE");
							// aggiorno la note del movimento precedente
							String notaModifica = "<li>Il movimento successivo collegato potrebbe non essere quello corretto.</li>";
							if (strNoteMovPrec != null) {
								strNoteMovPrec = strNoteMovPrec + notaModifica;
							} else {
								strNoteMovPrec = notaModifica;
							}
							rowMov.updAttribute("STRNOTE", strNoteMovPrec);

							rowPrec = rowMov;

							// inserisco la nota
							String strNote = (String) record.get("STRNOTE");
							if (strNote != null) {
								strNote = strNote
										+ "<li>Il movimento precedente collegato potrebbe non essere quello corretto.</li>";
							} else {
								strNote = "<li>Il movimento precedente collegato potrebbe non essere quello corretto.</li>";
							}

							record.put("STRNOTE", strNote);
						}
					}

					if (!findPrecMaxDatInizio) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_FIND_MOV_PREC),
								"Impossibile individuare univocamente il movimento da utilizzare come precedente per quello da inserire, "
										+ "ne esiste più di uno compatibile!",
								warnings, nested);
					}
				}
			}
		}
		// recupero il codMonoTempo del movimento precedente
		String codMonoTempoMovPrec = (String) rowPrec.getAttribute("CodMonoTempo");
		if (codMonoTempoMovPrec != null) {
			record.put("CODMONOTEMPOMOVPREC", codMonoTempoMovPrec);
			// Se ho una cessazione imposto anche il codMonoTempo a quello del movimento precedente
			if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) {
				record.put("CODMONOTEMPO", codMonoTempoMovPrec);
				codMonoTempo = codMonoTempoMovPrec;
			} else {
				// Se ho una trasformazione che non è a tempo indeterminato e non è un Distacco/Comando,
				// imposto il codMonoTempo della trasformazione con quello del movimento precedente
				if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE)) {
					if (codTipoTrasf != null
							&& !codTipoTrasf.equalsIgnoreCase(TipoTrasfEnum.DISTACCO_COMANDO.getCodice())
							&& !TipiTrasfConstant.Tra_TI.containsKey(codTipoTrasf)
							&& DeTipoContrattoConstant.mapContratti_TD_TI.containsKey(codTipoContratto)) {
						record.put("CODMONOTEMPO", codMonoTempoMovPrec);
						codMonoTempo = codMonoTempoMovPrec;
					}
				}
			}
		}

		if (codMonoTempo == null || codMonoTempo.equals("")) {
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.ImportMov.ERR_TIPO_TEMPO_MOVIMENTO), "", warnings, nested);
		}
		// Se si sta validando una CES collegata a un movimento a TD, e il
		// movimento precedente
		// non ha la data fine, si imposta la data fine del movimento precedete
		// uguale
		// alla data inizio della CES.
		// 11/07/2007 aggiunto flag FORZAMOD: se TRUE le modifiche vengono
		// effettuate altrimenti NO
		if ((codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) && (codMonoTempo.equalsIgnoreCase("D"))) {
			String dataFineMovPrec = (String) rowPrec.getAttribute("DatFineMov");
			if ((checkForzaValidazione && checkForzaModifiche)
					&& (dataFineMovPrec == null || dataFineMovPrec.equals(""))) {
				// aggiorno la data fine del movimento precedente
				rowPrec.updAttribute("DatFineMov", datInizioMov);
				// aggiorno la note del movimento precedente
				String strNoteMovPrec = StringUtils.getAttributeStrNotNull(rowPrec, "STRNOTE");
				String notaModifica = "<li>Non era stata dichiarata la data fine del movimento. "
						+ "Il sistema ha automaticamente impostato quella della CES collegata.</li>";
				strNoteMovPrec = strNoteMovPrec + notaModifica;
				rowPrec.updAttribute("STRNOTE", strNoteMovPrec);
				// Aggiornamento dataFineMov del movimento precedente sul db
				BigDecimal prgMovDaAggiornare = null;
				BigDecimal numkloMovPrec = null;
				if (rowPrec.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO) != null
						&& rowPrec.getAttribute(MovimentoBean.DB_NUM_K_LOCK) != null) {
					prgMovDaAggiornare = new BigDecimal(
							rowPrec.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO).toString());
					numkloMovPrec = new BigDecimal(rowPrec.getAttribute(MovimentoBean.DB_NUM_K_LOCK).toString());
					if (prgMovDaAggiornare != null && numkloMovPrec != null) {
						try {
							numkloMovPrec = numkloMovPrec.add(new BigDecimal(1));
							rowPrec.updAttribute(MovimentoBean.DB_NUM_K_LOCK, numkloMovPrec);
							numkloMovPrec = DBStore.aggiornaDataFineMov(prgMovDaAggiornare, datInizioMov, user,
									numkloMovPrec, trans);
						} catch (Exception e) {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.StatoOccupazionale.ERRORE_AGG_DATA_FINE_MOV_PREC), "",
									warnings, null);
						}
					}

				}
			}
		}

		// Se ho un result set valido e unico estraggo i dati
		BigDecimal prgMovimentoPrec = (BigDecimal) rowPrec.getAttribute("PrgMovimento");
		BigDecimal numProroghe = (BigDecimal) rowPrec.getAttribute("NumProroghe");
		String datInizioMovPrec = (String) rowPrec.getAttribute("DatInizioMov");
		BigDecimal numKloMovPrec = (BigDecimal) rowPrec.getAttribute("NumKloMov");
		String datInizioAvv = (String) rowPrec.getAttribute("DatInizioAvv");
		BigDecimal decRetribuzionePrec = (BigDecimal) rowPrec.getAttribute("DecRetribuzioneMen");
		String datFineMovPrec = (String) rowPrec.getAttribute("DatFineMov");
		String codTipoMovPrec = (String) rowPrec.getAttribute("codTipoMov");
		String codOrarioPrec = (String) rowPrec.getAttribute("codOrario");
		BigDecimal numOreSettPrec = (BigDecimal) rowPrec.getAttribute("numOreSett");
		String codTipoAssPrec = (String) rowPrec.getAttribute("CODTIPOASS");
		// 16/03/2005 DAVIDE
		prgAzienda = (BigDecimal) rowPrec.getAttribute("PRGAZIENDA");
		prgUnita = (BigDecimal) rowPrec.getAttribute("PRGUNITA");
		BigDecimal prgAzUtil = (BigDecimal) rowPrec.getAttribute("PRGAZIENDAUTILIZ");
		BigDecimal prgUnitaUtil = (BigDecimal) rowPrec.getAttribute("PRGUNITAUTILIZ");
		String codMvCessazionePrec = (String) rowPrec.getAttribute("CODMVCESSAZIONE");
		String codTipoTrasfPrec = rowPrec.getAttribute("CODTIPOTRASF") != null
				? rowPrec.getAttribute("CODTIPOTRASF").toString()
				: "";
		String codMansionePrec = rowPrec.getAttribute("CODMANSIONE") != null
				? rowPrec.getAttribute("CODMANSIONE").toString()
				: "";

		// DONA 13/02/2007: per il movimento precedente bisogna inserire anche
		// il campo STRNOTE
		String strNoteMovPrec = (String) rowPrec.getAttribute("STRNOTE");
		if (strNoteMovPrec != null) {
			record.put("STRNOTEMOVPREC", strNoteMovPrec);
		}

		record.put("PRGMOVIMENTOPREC", prgMovimentoPrec);

		/*
		 * 16/03/2005 DAVIDE Inserisco i progressivi della nuova (se nuova) azienda ed unità trovata nel movimento da
		 * collegare, tranne nel caso si tratti di un trasferimento d'azienda
		 */
		if (!trasferimentoAzienda) {
			record.put("PRGAZIENDA", prgAzienda);
			record.put("PRGUNITAPRODUTTIVA", prgUnita);
		}
		// Nel caso di distacco(trasformazione con codTipoTrasf = 'DL') non
		// sostituisco l'azienda utilizzatrice
		if (!distacco) {
			if (prgAzUtil != null) {
				record.put("PRGAZIENDAUTIL", prgAzUtil);
			}
			if (prgUnitaUtil != null) {
				record.put("PRGUNITAUTIL", prgUnitaUtil);
			}
			if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE) && codMonoTempo.equalsIgnoreCase("D")) {
				if (datFineMovPrec != null && !datFineMovPrec.equals("")) {
					record.put("DATFINEMOV", datFineMovPrec);
				}
			}
		} else {
			if (datFineMovPrec != null && !datFineMovPrec.equals("")) {
				record.put("DATFINEMOV", datFineMovPrec);
			}
		}

		// -------------------------------------------------------------------------------------------------------

		record.put("NUMKLOMOVPREC", numKloMovPrec.add(new BigDecimal(1)));
		if (datInizioAvv != null) {
			record.put("DATAINIZIOAVV", datInizioAvv);
		}
		if (codTipoMovPrec != null) {
			record.put("CODTIPOMOVPREC", codTipoMovPrec);
			if (codTipoMovPrec.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE)) {
				if (!codTipoTrasfPrec.equals("")) {
					record.put("CODTIPOTRASFPREC", codTipoTrasfPrec);
				}
			}
		}
		// Calcolo la data inizio della proroga (assente nel nuovo tracciato) o
		// trasformazione ZZ
		if (datInizioMov == null || datInizioMov.equals("")) {
			if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
				try {
					if (datFineMovPrec != null && !datFineMovPrec.equals("")) {
						record.put("DATINIZIOMOV", DateUtils.giornoSuccessivo(datFineMovPrec));
					}
				} catch (Exception e) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
							"Impossibile cercare il movimento precedente", warnings, nested);
				}
			} else {
				if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE) && codTipoTrasf != null
						&& codTipoTrasf.equalsIgnoreCase(TipoTrasfEnum.PROSECUZIONE_PERIODO_FORMATIVO.getCodice())) {
					try {
						String datFinePFPrec = (String) rowPrec.getAttribute("datFinePF");
						if (datFinePFPrec != null && !datFinePFPrec.equals("")) {
							record.put("DATINIZIOMOV", DateUtils.giornoSuccessivo(datFinePFPrec));
						} else {
							String dettaglioMsg = "Non è stato possibile determinare la data inizio del movimento di sospensione del periodo formativo.";
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA), msgError + dettaglioMsg,
									warnings, nested);
						}
					} catch (Exception e) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
								"Impossibile cercare il movimento precedente", warnings, nested);
					}
				}
			}
		}

		String codMonoTipo = record.get("CODMONOTIPO") != null ? (String) record.get("CODMONOTIPO") : "";
		String flgContrattoTI = record.get("FLGCONTRATTOTI") != null ? (String) record.get("FLGCONTRATTOTI") : "";
		if (codMonoTipo.equalsIgnoreCase("A") && flgContrattoTI.equalsIgnoreCase("S")) {
			if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE) && codTipoTrasf != null
					&& !codTipoTrasf.equalsIgnoreCase(TipoTrasfEnum.PROSECUZIONE_PERIODO_FORMATIVO.getCodice())) {
				String dataFinePFTRA = (String) record.get("DATFINEPF");
				if (dataFinePFTRA == null || dataFinePFTRA.equals("")) {
					String datFinePFPrec = (String) rowPrec.getAttribute("datFinePF");
					if (datFinePFPrec != null && !datFinePFPrec.equals("")) {
						record.put("DATFINEPF", datFinePFPrec);
					}
				}
			} else {
				if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) {
					String dataFinePFCES = (String) record.get("DATFINEPF");
					if (dataFinePFCES == null || dataFinePFCES.equals("")) {
						String datFinePFPrec = (String) rowPrec.getAttribute("datFinePF");
						if (datFinePFPrec != null && !datFinePFPrec.equals("")) {
							record.put("DATFINEPF", datFinePFPrec);
						}
					}
				}
			}
		}

		record.put("DATINIZIOMOVPREC", datInizioMovPrec);
		if (decRetribuzionePrec != null) {
			record.put("DECRETRIBUZIONEPREC", decRetribuzionePrec);
		}
		if (datFineMovPrec != null) {
			record.put("DATFINEMOVPREC", datFineMovPrec);
		}

		// Se quella da inserire è una proroga il numero di proroghe va
		// aumentato di uno.
		if (numProroghe != null) {
			if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
				numProroghe = numProroghe.add(new BigDecimal(1));
			}
		} else {
			if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
				numProroghe = new BigDecimal(1);
			} else
				numProroghe = new BigDecimal(0);
		}
		record.put("NUMPROROGHE", numProroghe);

		if (codMvCessazionePrec != null) {
			record.put("CODMVCESSAZIONEPREC", codMvCessazionePrec);
		}

		// se sto validando una proroga devo settare questi valori uguali al
		// movimento precedente
		boolean validazione = record.containsKey("CONTEXT")
				&& (((String) record.get("CONTEXT")).equalsIgnoreCase("valida")
						|| ((String) record.get("CONTEXT")).equalsIgnoreCase("validazioneMassiva"));
		if (validazione && codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
			if (codOrarioPrec != null && !record.containsKey("CODORARIO")) {
				record.put("CODORARIO", codOrarioPrec);
			}
			if (numOreSettPrec != null && !record.containsKey("NUMORESETT")) {
				record.put("NUMORESETT", numOreSettPrec);
			}
		}

		if (codTipoAssPrec != null) {
			record.put("CODTIPOASSPREC", codTipoAssPrec);
		}

		String codMansioneMovimento = record.get("CODMANSIONE") != null ? record.get("CODMANSIONE").toString() : "";
		if (codMansioneMovimento.equals("") && !codMansionePrec.equals("")) {
			record.put("CODMANSIONE", codMansionePrec);
		}

		// Aggiungo al record il nuovo valore di collegato
		record.put("COLLEGATO", "precedente");

		// CONTROLLI DECRETO 2013
		SourceBean controlliDecretoResult = this.controlliDecreto(codTipoMov, codTipoTrasf, codTipoContratto,
				codMonoTempoMovPrec, warnings, nested);
		if (controlliDecretoResult != null)
			return controlliDecretoResult;

		return res; // return null;
	}

	/**
	 * Questo metodo controlla se si tratta di una cessazione veloce con Avviamento
	 */
	private boolean checkCEV(Map record, String codTipoMov) {
		if (!(MovimentoBean.COD_CESSAZIONE.equalsIgnoreCase(codTipoMov)
				|| MovimentoBean.COD_PROROGA.equalsIgnoreCase(codTipoMov)
				|| MovimentoBean.COD_TRASFORMAZIONE.equalsIgnoreCase(codTipoMov)))
			return false;
		// Contesto validazione o validazione massiva
		// Controllo se ho il campo PRGMOVIMENTOAPPCVE
		BigDecimal prgMovAppCVE = (BigDecimal) record.get("PRGMOVIMENTOAPPCVE");
		if (prgMovAppCVE != null)
			return true;
		else
			return false;
	}

	/**
	 * Questo metodo effettua la ricerca del movimento precedente sulla base dei dati del record e restituisce true se
	 * ne trova almeno uno (invocato solo per le cessazioni veloci, ma è stato mantenuto allineato con le modifiche
	 * riguardo la mancanza della data inizio proroga nei nuovi tracciati, perché era presente il codice che riguardava
	 * le proroghe.)
	 */
	private boolean esistePrecedenteSuDB(Map record) {

		String codTipoMov = (String) record.get("CODTIPOMOV");
		String datInizioMov = (String) record.get("DATINIZIOMOV");
		String datFineMov = (String) record.get("DATFINEMOV");
		BigDecimal prgAzienda = (BigDecimal) record.get("PRGAZIENDA");
		BigDecimal cdnLav = (BigDecimal) record.get("CDNLAVORATORE");

		// Cerco il movimento precedente
		String selectquery = "SELECT PrgMovimento," + "PRGAZIENDA," + "PRGUNITA," + "NumProroghe, " + "CodMonoTempo, "
				+ "NumKloMov, " + "TO_CHAR(DatInizioMov, 'DD/MM/YYYY') DatInizioMov, " + "DecRetribuzioneMen, "
				+ "TO_CHAR(DatFineMovEffettiva, 'DD/MM/YYYY') DatFineMov, "
				+ "TO_CHAR(DatInizioAvv, 'DD/MM/YYYY') DatInizioAvv, " + "codTipoMov, " + "codOrario, " + "numOreSett, "
				+ "DE_TIPO_CONTRATTO.CODMONOTIPO CODMONOTIPOASS, " + "AM_MOVIMENTO.CODTIPOCONTRATTO CODTIPOASS "
				+ "FROM AM_MOVIMENTO, DE_TIPO_CONTRATTO ";

		// Creo la query se non ho il precedente
		if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) {
			selectquery += " WHERE AM_MOVIMENTO.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO(+)  "
					+ " AND CODSTATOATTO = 'PR' AND PRGMOVIMENTOSUCC IS NULL "
					+ " AND ( (AM_MOVIMENTO.CODTIPOMOV = 'CES' AND NVL(AM_MOVIMENTO.CODMVCESSAZIONE, ' ') = 'SC') OR "
					+ " (CODTIPOMOV != 'CES') ) ";
		} else {
			selectquery += " WHERE CODTIPOMOV != 'CES' AND CODSTATOATTO = 'PR' " + "AND PRGMOVIMENTOSUCC IS NULL "
					+ "AND AM_MOVIMENTO.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO(+) "
					+ (codTipoMov.equals(MovimentoBean.COD_TRASFORMAZIONE)
							? "AND (NVL(DE_TIPO_CONTRATTO.CODMONOTIPO,'0') != 'N' OR "
									+ "AM_MOVIMENTO.CODTIPOCONTRATTO IN ("
									+ DeTipoContrattoConstant.listaContrattiAut_Tra + ")) "
							: "");
		}

		StringBuffer buf = new StringBuffer();
		if (!cdnLav.equals("")) {
			buf.append(" AND CDNLAVORATORE = " + cdnLav + " ");
		}
		if (!prgAzienda.equals("")) {
			buf.append(" AND PRGAZIENDA = " + prgAzienda + " ");
		}
		// La data di fine del movimento cercato deve corrispondere con quella
		// di inizio -1
		// del movimento corrente nel caso di Proroghe,
		// nel caso di trasf/cessazioni la data di inizio del movimento
		// precedente deve essere
		// anteriore alla data di inizio della trasformazione/cessazione
		if (datInizioMov != null && !datInizioMov.equals("")) {
			if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
				buf.append(" AND CODMONOTEMPO = 'D' ");
				buf.append(" AND TRUNC(DATFINEMOVEFFETTIVA) = TO_DATE('" + datInizioMov + "', 'DD/MM/YYYY') -1 ");
			} else if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE)
					|| codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) {
				buf.append(" AND TRUNC(DATINIZIOMOV) <= TO_DATE('" + datInizioMov + "', 'DD/MM/YYYY') ");
			}
		} else {
			// data inizio movimento può essere vuota per le proroghe importate
			// con il nuovo tracciato
			if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
				buf.append(" AND CODMONOTEMPO = 'D' ");
				buf.append(" AND TRUNC(DATFINEMOVEFFETTIVA) < TO_DATE('" + datFineMov + "', 'DD/MM/YYYY') ");
			}
		}
		selectquery = selectquery + buf;

		SourceBean result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(selectquery, this.getTrans());
		} catch (Exception e) {
			return false;
		}

		// Esamino il risultato
		SourceBean rowPrec = null;
		Vector v = result.getAttributeAsVector("ROW");
		if (v.size() == 0) {
			return false;
		} else
			return true;
	}

	// controlla se esiste già un movimento doppio o comunque simile al
	// movimento di
	// cessazione che sta validando in modo da evitare il controllo sulla
	// ricerca di
	// un movimento precedente non collegato che porta in errore qualora si stia
	// validando
	// una comunicazione doppia
	private boolean esisteDoppio(Map record) {
		String codTipoMov = (String) record.get("CODTIPOMOV");
		String datInizioMov = (String) record.get("DATINIZIOMOV");
		BigDecimal cdnLav = (BigDecimal) record.get("CDNLAVORATORE");

		SourceBean row = null;
		Object params[] = new Object[5];
		params[0] = cdnLav;
		params[1] = "MovDettaglioGeneraleConsultaPage";
		params[2] = codTipoMov.toUpperCase();
		params[3] = datInizioMov;
		params[4] = cdnLav;

		try {
			row = (SourceBean) this.getTrans().executeQuery("GET_MOVIMENTI_SIMILI", params, "SELECT");
		} catch (Exception e) {
			return false;
		}

		// Esamino il risultato
		Vector rows = row.getAttributeAsVector("ROW");
		if (rows.size() == 0) {
			return false;
		} else {
			// aggiungere il controllo sul doppione?
			return true;
		}
	}

	/**
	 * Invocata per le proroghe. Creo lo statement da eseguire per la ricerca del movimento precendente nel caso di
	 * forzatura della validazione. Si modifica il controllo sulla data inizio e fine movimento: vengono recurati i
	 * movimenti prec con datainizioMov minore della datInizioMov del movimento e con dataFineMov minore della
	 * datFineMov del movimento di origine
	 * 
	 * @param record
	 *            dati del movimento di origine
	 * @param doppio
	 *            parametro indicante l'esistenza di un movimento doppio
	 * @return la query da eseguire per verificare l'esistenza del precendete
	 */
	private String getQueryRicercaForzataMovPrec(Map record, boolean doppio) {

		String datInizioMov = (String) record.get("DATINIZIOMOV");
		String datFineMov = (String) record.get("DATFINEMOV");
		BigDecimal prgAzienda = (BigDecimal) record.get("PRGAZIENDA");
		BigDecimal cdnLav = (BigDecimal) record.get("CDNLAVORATORE");
		String codTipoMov = (String) record.get("CODTIPOMOV");

		String selectquery = "SELECT PrgMovimento," + "PRGAZIENDA," + "PRGUNITA," + "PRGAZIENDAUTILIZ,"
				+ "PRGUNITAUTILIZ," + "NumProroghe, " + "CodMonoTempo, " + "NumKloMov, " + "StrNote, "
				+ "TO_CHAR(DatInizioMov, 'DD/MM/YYYY') DatInizioMov, " + "DecRetribuzioneMen, "
				+ "TO_CHAR(DatFineMovEffettiva, 'DD/MM/YYYY') DatFineMov, "
				+ "TO_CHAR(DatInizioAvv, 'DD/MM/YYYY') DatInizioAvv, " + "codTipoMov, " + "codOrario, " + "numOreSett, "
				+ "DE_TIPO_CONTRATTO.CODMONOTIPO CODMONOTIPOASS, " + "AM_MOVIMENTO.CODTIPOCONTRATTO CODTIPOASS "
				+ "FROM AM_MOVIMENTO, DE_TIPO_CONTRATTO ";

		selectquery += " WHERE CODTIPOMOV != 'CES' AND CODSTATOATTO = 'PR' ";

		if (doppio) {
			selectquery += "AND (prgmovimentosucc IS NULL or exists "
					+ "(select 1 from am_movimento am1 where am1.prgmovimento = am_movimento.prgmovimentosucc "
					+ "and am1.codtipomov = 'PRO' and am1.codmonomovdich in ('D','C')) )";
		} else {
			selectquery += "AND PRGMOVIMENTOSUCC IS NULL ";
		}

		selectquery += " AND AM_MOVIMENTO.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO(+) "
				+ (codTipoMov.equals(MovimentoBean.COD_TRASFORMAZIONE)
						? "AND (NVL(DE_TIPO_CONTRATTO.CODMONOTIPO,'0') != 'N' OR "
								+ "AM_MOVIMENTO.CODTIPOCONTRATTO IN (" + DeTipoContrattoConstant.listaContrattiAut_Tra
								+ ")) "
						: "");

		StringBuffer buf = new StringBuffer();
		buf.append(" AND CODMONOTEMPO = 'D' ");
		if (!cdnLav.equals("")) {
			buf.append(" AND CDNLAVORATORE = " + cdnLav + " ");
		}
		if (!prgAzienda.equals("")) {
			buf.append(" AND PRGAZIENDA = " + prgAzienda + " ");
		}
		if (datInizioMov != null && !datInizioMov.equals("")) {
			buf.append(" AND TRUNC(DATINIZIOMOV) < TO_DATE('" + datInizioMov + "', 'DD/MM/YYYY') ");
			buf.append(" AND TRUNC(DATFINEMOVEFFETTIVA) < TO_DATE('" + datFineMov + "', 'DD/MM/YYYY') ");
		}
		selectquery = selectquery + buf;
		selectquery += " ORDER BY AM_MOVIMENTO.DATINIZIOMOV DESC ";

		return selectquery;
	}

	private SourceBean agganciaMissioneProATI(ManualValidationFieldExtractor extractor, Map record, ArrayList warnings,
			ArrayList nested) throws Exception {
		RecordProcessor insertMissione;
		RecordProcessor deleteMovValida;
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator;
		String configproc = configbase + "processors" + File.separator;
		// dataFineProroga in questi casi equivale alla data fine missione
		String dataFineProroga = extractor.estraiDataFineMovimento(record);
		BigDecimal _prgAzienda = extractor.estraiPrgAzienda(record);
		BigDecimal _prgAzUtil = extractor.estraiPrgAziendaUtiliz(record);
		BigDecimal cdnLav = extractor.estraiCdnLavoratore(record);

		SourceBean rowMovMiss = null;
		Object paramsMovMiss[] = new Object[4];
		paramsMovMiss[0] = cdnLav;
		paramsMovMiss[1] = _prgAzienda;
		paramsMovMiss[2] = _prgAzUtil;
		paramsMovMiss[3] = dataFineProroga;

		try {
			rowMovMiss = (SourceBean) getTrans().executeQuery("GET_MOVIMENTI_TI_SOMMINISTRAZIONE", paramsMovMiss,
					"SELECT");
		} catch (EMFInternalError e) {
			return ProcessorsUtils.createResponse(getName(), getClassName(),
					new Integer(MessageCodes.General.GET_ROW_FAIL),
					"Impossibile estrarre i movimenti a cui agganciare la proroga della missione", warnings, nested);
		}
		Vector rowsMovMiss = rowMovMiss.getAttributeAsVector("ROW");
		// ESISTONO MOVIMENTI DI SOMMINISTRAZIONE A TEMPO INDETERMINATO
		if (rowsMovMiss.size() == 1) {
			SourceBean movSomm = (SourceBean) rowsMovMiss.get(0);
			Object prgMovimentoSomm = movSomm.getAttribute("PRGMOVIMENTO");
			record.put("PRGMOVIMENTO", prgMovimentoSomm);
			if (dataFineProroga != null && !dataFineProroga.equals("")) {
				if (record.containsKey("DATFINERAPLAV")) {
					record.remove("DATFINERAPLAV");
					record.put("DATFINERAPLAV", dataFineProroga);
				} else {
					record.put("DATFINERAPLAV", dataFineProroga);
				}
			}
			// inserimento missione
			try {
				insertMissione = new InsertData("Inserimento Missione", getTrans(),
						configproc + "InsertMovimentoMissione.xml", "INSERT_MOVIMENTO_MISSIONE", user);
				insertMissione.processRecord(record);
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(getName(), getClassName(),
						new Integer(MessageCodes.General.OPERATION_FAIL), "Impossibile inserire i dati della missione",
						warnings, nested);
			}

			// Il record (la proroga) non viene inserito in am_movimento, ma deve essere
			// cancellato dalla am_movimento_appoggio per evitare di inserire missioni per lo stesso movimento
			deleteMovValida = new RemoveMovimentoAppoggio("Rimozione del record dalla tabella AM_MOVIMENTO_APPOGGIO",
					getTrans());
			deleteMovValida.processRecord(record);
			warnings.add(new Warning(MessageCodes.ImportMov.WAR_NEW_MISSIONE, ""));
			return ProcessorsUtils.createResponse(getName(), getClassName(), null, null, warnings, nested, true);
		} else {
			if (rowsMovMiss.size() > 1) {
				return ProcessorsUtils.createResponse(getName(), getClassName(),
						new Integer(MessageCodes.General.GET_ROW_FAIL),
						"Esistono movimenti doppi a cui è possibile agganciare la proroga della missione", warnings,
						nested);
			} else {
				try {
					rowMovMiss = (SourceBean) getTrans().executeQuery(
							"GET_MOVIMENTI_TI_SOMMINISTRAZIONE_UTILIZZ_IN_MISSIONE", paramsMovMiss, "SELECT");
				} catch (EMFInternalError e) {
					return ProcessorsUtils.createResponse(getName(), getClassName(),
							new Integer(MessageCodes.General.GET_ROW_FAIL),
							"Impossibile estrarre i movimenti a cui agganciare la proroga della missione", warnings,
							nested);
				}
				rowsMovMiss = rowMovMiss.getAttributeAsVector("ROW");
				if (rowsMovMiss.size() >= 1) {
					SourceBean movSomm = (SourceBean) rowsMovMiss.get(0);
					Object prgMovimentoSomm = movSomm.getAttribute("PRGMOVIMENTO");
					record.put("PRGMOVIMENTO", prgMovimentoSomm);
					if (dataFineProroga != null && !dataFineProroga.equals("")) {
						if (record.containsKey("DATFINERAPLAV")) {
							record.remove("DATFINERAPLAV");
							record.put("DATFINERAPLAV", dataFineProroga);
						} else {
							record.put("DATFINERAPLAV", dataFineProroga);
						}
					}
					// inserimento missione
					try {
						insertMissione = new InsertData("Inserimento Missione", getTrans(),
								configproc + "InsertMovimentoMissione.xml", "INSERT_MOVIMENTO_MISSIONE", user);
						insertMissione.processRecord(record);
					} catch (Exception e) {
						return ProcessorsUtils.createResponse(getName(), getClassName(),
								new Integer(MessageCodes.General.OPERATION_FAIL),
								"Impossibile inserire i dati della missione", warnings, nested);
					}

					// Il record (la proroga) non viene inserito in am_movimento, ma deve essere
					// cancellato dalla am_movimento_appoggio per evitare di inserire missioni per lo stesso movimento
					deleteMovValida = new RemoveMovimentoAppoggio(
							"Rimozione del record dalla tabella AM_MOVIMENTO_APPOGGIO", getTrans());
					deleteMovValida.processRecord(record);
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_NEW_MISSIONE, ""));
					return ProcessorsUtils.createResponse(getName(), getClassName(), null, null, warnings, nested,
							true);
				} else {
					return ProcessorsUtils.createResponse(getName(), getClassName(),
							new Integer(MessageCodes.General.GET_ROW_FAIL),
							"Non esiste il movimento a cui è possibile agganciare la proroga della missione", warnings,
							nested);
				}
			}
		}
	}

}// END class
