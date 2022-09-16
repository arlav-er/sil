package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.enumeration.CodMonoTempoEnum;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;

/**
 * @author roccetti Questa classe efettua la gestione del movimento precedente in inserimento e validazione manuale e
 *         rettifica
 */
public class SelectMovimentoPrecManuale extends AbstractSelectMovimentoPrecedente {

	public SelectMovimentoPrecManuale(String name, TransactionQueryExecutor transexec, RecordProcessor gestoreCVE) {
		super(name, transexec, gestoreCVE);
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
	 * l'inserimento senza movimento precedente. (sempre che non si tratti di cessazioni veloci)
	 * <p>
	 * 
	 * @exception SourceBeanException
	 *                Se avviene un errore nella creazione del SourceBean di risposta
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SourceBean customProcessRecord(Map record, ArrayList warnings, ArrayList nested) throws SourceBeanException {

		String name = this.getName();
		String classname = this.getClassName();
		TransactionQueryExecutor trans = this.getTrans();
		RecordProcessor gestoreCVE = this.getGestoreCVE();

		// Controllo che i parametri necessari per la query di select ci siano
		String codTipoMov = (String) record.get("CODTIPOMOV");
		String datInizioMov = (String) record.get("DATINIZIOMOV");
		String datFineMov = (String) record.get("DATFINEMOV");
		BigDecimal prgAzienda = (BigDecimal) record.get("PRGAZIENDA");
		BigDecimal prgUnita = (BigDecimal) record.get("PRGUNITAPRODUTTIVA");
		BigDecimal cdnLav = (BigDecimal) record.get("CDNLAVORATORE");
		BigDecimal prgAzUtil = (BigDecimal) record.get("PRGAZIENDAUTIL");
		BigDecimal prgUnitaUtil = (BigDecimal) record.get("PRGUNITAUTIL");
		String collegato = (String) record.get("COLLEGATO");
		String codMonoTempo = (String) record.get("CODMONOTEMPO");
		String contesto = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		String codTipoContratto = record.get("CODTIPOASS").toString();
		BigDecimal prgAziendaPrec = null;
		BigDecimal prgUnitaPrec = null;
		boolean trasferimentoAzienda = false;
		// eseguiQueryMovPrec = false quando si tratta di un trasferimento
		// d'azienda e
		// nel db non è presente l'azienda relativa al movimento precedente (in
		// base al campo STRCODICEFISCALEAZPREC)
		boolean eseguiQueryMovPrec = true;
		String codTipoTrasf = (String) record.get("CODTIPOTRASF");
		boolean distacco = false;
		if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE) && codTipoTrasf != null
				&& codTipoTrasf.equalsIgnoreCase("DL")) {
			distacco = true;
		}

		if (prgAzienda == null || prgUnita == null || cdnLav == null || codTipoMov == null
				|| (datInizioMov == null && !codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA))) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Impossibile elaborare il movimento, dati mancanti.", warnings, nested);
		}

		// Se sto inserendo un'assunzione non devo cercare nulla
		if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_ASSUNZIONE)) {
			// Imposto la data di inizio dell'avviamento originario e quella di
			// inizio del movimento precedente
			record.put("DATAINIZIOAVV", datInizioMov);
			record.put("DATINIZIOMOVPREC", datInizioMov);
			// Aggiungo al record il nuovo valore di collegato
			record.put("COLLEGATO", "nessuno");
			return null;
		}

		// Se ho inserito il lavoratore allora in caso di proroga o trasformazione o cessazione se presente cerco di
		// validare avviamento veloce
		// Nel caso in cui viene effettuata la validazione dell'avviamento veloce, allora il valore associato a
		// COLLEGATO viene aggiornato a precedente
		// e alla Map viene aggiunto anche il prgmovimentoprec
		if (record.containsKey("INSNUOVOLAV") && record.get("INSNUOVOLAV").toString().equalsIgnoreCase("TRUE")) {
			record.put("COLLEGATO", "nessuno");
		}

		// Controllo se si tratta di un movimento di cessazione (proroga o
		// trasformazione) veloce con i dati per l'avviamento
		// In tal caso prima di cercare il movimento precedente inserisco
		// l'avviamento
		if (checkCEV(record, codTipoMov, contesto)) {
			if (gestoreCVE != null) {
				SourceBean insertAvvResult = gestoreCVE.processRecord(record);
				if (insertAvvResult != null) {
					nested.add(insertAvvResult);
					// Se ho errori nell'inserimento dell'avvimento ritorno con
					// errore
					if (ProcessorsUtils.isError(insertAvvResult)) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA), null, warnings, nested);
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

		// Ora posso rileggere i valori del movimento per gestire il precedente
		BigDecimal prgmovprec = (BigDecimal) record.get("PRGMOVIMENTOPREC");
		collegato = (String) record.get("COLLEGATO");

		// Se sto processando un movimento di cessazione o trasformazione non
		// collegato controllo se posso inserirlo
		// ugualmente anche senza precedente
		String oggi = DateUtils.getNow();
		// e che la data inizio movimento non sia futura
		if (collegato != null && collegato.equalsIgnoreCase("nessuno")) {
			try {
				if (DateUtils.compare(oggi, datInizioMov) == -1) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
							"Impossibile inserire trasformazione o cessazione senza precedente con data inizio maggiore della data attuale.",
							warnings, nested);
				}
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "formato data non corretto", warnings,
						nested);
			}
			Integer insScollegatoResult = ProcessorsUtils.checkInserimentoScollegato(codTipoMov, codMonoTempo, cdnLav,
					datInizioMov, trans);
			if (insScollegatoResult == null)
				return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
			else
				return ProcessorsUtils.createResponse(name, classname, insScollegatoResult, null, warnings, nested);
		} else {
			String selectquery = "SELECT PrgMovimento," + "NumProroghe, " + "CodMonoTempo, " + "NumKloMov, "
					+ "TO_CHAR(DatInizioMov, 'DD/MM/YYYY') DatInizioMov, " + "DecRetribuzioneMen, "
					+ "TO_CHAR(DatFineMovEffettiva, 'DD/MM/YYYY') DatFineMov, "
					+ "TO_CHAR(DatInizioAvv, 'DD/MM/YYYY') DatInizioAvv, " + "codTipoMov, " + "codOrario, "
					+ "numOreSett, " + "DE_TIPO_CONTRATTO.CODMONOTIPO CODMONOTIPOASS, "
					+ "AM_MOVIMENTO.CODTIPOCONTRATTO CODTIPOASS, " + "AM_MOVIMENTO.CODMVCESSAZIONE CODMVCESSAZIONE, "
					+ "AM_MOVIMENTO.CODGRADO, " + "AM_MOVIMENTO.CODMANSIONE, " + "AM_MOVIMENTO.CODTIPOTRASF "
					+ "FROM AM_MOVIMENTO, DE_TIPO_CONTRATTO ";

			if (prgmovprec == null) {
				// Gestione trasferimenti d'azienda nella validazione manuale
				// dei movimenti.
				if ((contesto.equalsIgnoreCase("valida") || contesto.equalsIgnoreCase("validaArchivio"))
						&& codTipoTrasf != null && codTipoMov.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE)) {
					trasferimentoAzienda = (record.get("FLGTRASFER") != null
							&& record.get("FLGTRASFER").toString().equalsIgnoreCase("S")) ? true : false;
					if (trasferimentoAzienda) {
						String codFiscAziendaPrec = record.get("STRCODICEFISCALEAZPREC") != null
								? record.get("STRCODICEFISCALEAZPREC").toString()
								: "";
						String codComAzPrec = record.get("CODCOMAZPREC") != null
								? record.get("CODCOMAZPREC").toString().toUpperCase()
								: "";
						String indirizzoAzPrec = record.get("STRINDIRIZZOAZPREC") != null
								? record.get("STRINDIRIZZOAZPREC").toString().toUpperCase()
								: "";
						if (!codFiscAziendaPrec.equals("")) {
							Vector prgAzList = ProcessorsUtils.cercaTrasfAziendaPrec(codFiscAziendaPrec, trans);
							if (prgAzList.size() == 1) {
								SourceBean azPrec = (SourceBean) prgAzList.get(0);
								prgAziendaPrec = new BigDecimal(azPrec.getAttribute("PRGAZIENDA").toString());
								// Cerco anche l'eventuale prgUnita per comune e
								// indirizzo
								Vector unitaAzienda = ProcessorsUtils.getListaUnitaAzienda(prgAziendaPrec, trans);
								for (int i = 0; i < unitaAzienda.size(); i++) {
									SourceBean unita = (SourceBean) unitaAzienda.get(i);
									String strIndirizzoUAZ = unita.containsAttribute("STRINDIRIZZO")
											? unita.getAttribute("STRINDIRIZZO").toString().toUpperCase()
											: "";
									String codComUAZ = unita.containsAttribute("CODCOM")
											? unita.getAttribute("CODCOM").toString().toUpperCase()
											: "";
									if (codComUAZ.equals(codComAzPrec) && strIndirizzoUAZ.equals(indirizzoAzPrec)) {
										prgUnitaPrec = new BigDecimal(unita.getAttribute("PRGUNITA").toString());
										break;
									}
								}
							}
						}
					}
				}
				// Creo la query se non ho il precedente

				if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) {
					selectquery += " WHERE AM_MOVIMENTO.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO(+)  "
							+ " AND CODSTATOATTO = 'PR' AND PRGMOVIMENTOSUCC IS NULL "
							+ " AND ( (AM_MOVIMENTO.CODTIPOMOV = 'CES' AND NVL(AM_MOVIMENTO.CODMVCESSAZIONE, ' ') = 'SC') OR "
							+ " (CODTIPOMOV != 'CES') ) ";
				} else {
					selectquery += " WHERE CODTIPOMOV != 'CES' AND CODSTATOATTO = 'PR' "
							+ "AND PRGMOVIMENTOSUCC IS NULL "
							+ "AND AM_MOVIMENTO.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO(+) "
							+ (codTipoMov.equals(MovimentoBean.COD_TRASFORMAZIONE)
									? "AND (NVL(DE_TIPO_CONTRATTO.CODMONOTIPO,'0') != 'N' OR "
											+ "AM_MOVIMENTO.CODTIPOCONTRATTO IN ("
											+ DeTipoContrattoConstant.listaContrattiAut_Tra + ")) "
									: "");
				}

				StringBuffer buf = new StringBuffer();
				if (trasferimentoAzienda) {
					if (prgAziendaPrec != null) {
						buf.append(" AND PRGAZIENDA = " + prgAziendaPrec + " ");
						if (prgUnitaPrec != null) {
							buf.append(" AND PRGUNITA = " + prgUnitaPrec + " ");
						}
					} else {
						eseguiQueryMovPrec = false;
					}
				} else {
					if (!prgAzienda.equals("")) {
						buf.append(" AND PRGAZIENDA = " + prgAzienda + " ");
					}
					if (!prgUnita.equals("")) {
						buf.append(" AND PRGUNITA = " + prgUnita + " ");
					}
				}
				if (!cdnLav.equals("")) {
					buf.append(" AND CDNLAVORATORE = " + cdnLav + " ");
				}
				// nel caso di distacco, nuovi criteri per la ricerca
				if (distacco) {
					buf.append(" AND ((CODTIPOMOV in ('AVV','PRO')) OR (CODTIPOMOV = 'TRA' AND CODTIPOTRASF = 'DL' ");
					if (prgAzUtil != null) {
						buf.append("AND PRGAZIENDADIST = " + prgAzUtil);
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
					if (prgAzUtil != null && !prgAzUtil.equals("")) {
						buf.append(" AND PRGAZIENDAUTILIZ = " + prgAzUtil + " ");
					}
					if (prgUnitaUtil != null && !prgUnitaUtil.equals("")) {
						buf.append(" AND PRGUNITAUTILIZ = " + prgUnitaUtil + " ");
					}
					// La data di fine del movimento cercato deve corrispondere con quella di inizio -1 del movimento
					// corrente nel caso di Proroghe per tracciati che
					// prevedevano la presenza della data inizio proroga.
					// Siccome la data inizio proroga non viene più comunicata, allora la data di fine del movimento
					// cercato deve essere minore della data fine proroga
					// Nel caso di trasf/cessazioni la data di inizio del
					// movimento precedente deve essere
					// anteriore alla data di inizio della
					// trasformazione/cessazione
					if (datInizioMov != null && !datInizioMov.equals("")) {
						if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
							// contesto = inserisci o validazione
							if (!record.containsKey("INIZIOPROROGAASSENTE")) {
								buf.append(" AND CODMONOTEMPO = 'D' ");
								buf.append(" AND TRUNC(DATFINEMOVEFFETTIVA) = TO_DATE('" + datInizioMov
										+ "', 'DD/MM/YYYY') -1 ");
							} else {
								// sezione date contratto, inizio e fine, non compatibili in caso di validazione,
								buf.append(" AND CODMONOTEMPO = 'D' ");
								buf.append(" AND TRUNC(DATFINEMOVEFFETTIVA) < TO_DATE('" + datFineMov
										+ "', 'DD/MM/YYYY') ");
							}
						} else {
							if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_TRASFORMAZIONE)) {
								buf.append(
										" AND TRUNC(DATINIZIOMOV) <= TO_DATE('" + datInizioMov + "', 'DD/MM/YYYY') ");
								if (codMonoTempo != null
										&& codMonoTempo.equalsIgnoreCase(CodMonoTempoEnum.INDETERMINATO.getCodice())) {
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
									if (codMonoTempo != null && codMonoTempo
											.equalsIgnoreCase(CodMonoTempoEnum.DETERMINATO.getCodice())) {
										// movimento precedente a TD e data fine movimento precedente >= data
										// trasformazione
										buf.append(" AND CODMONOTEMPO = 'D' ");
										buf.append(" AND TRUNC(NVL(DATFINEMOVEFFETTIVA, TO_DATE('" + datInizioMov
												+ "', 'DD/MM/YYYY'))) >= TO_DATE('" + datInizioMov
												+ "', 'DD/MM/YYYY') ");
									}
								}
							} else {
								if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) {
									buf.append(" AND TRUNC(DATINIZIOMOV) <= TO_DATE('" + datInizioMov
											+ "', 'DD/MM/YYYY') ");
								}
							}
						}
					} else {
						// data inizio movimento può essere vuota per le
						// proroghe importate con il nuovo tracciato
						if ((contesto.equalsIgnoreCase("valida") || contesto.equalsIgnoreCase("validaArchivio"))
								&& codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
							buf.append(" AND CODMONOTEMPO = 'D' ");
							buf.append(
									" AND TRUNC(DATFINEMOVEFFETTIVA) < TO_DATE('" + datFineMov + "', 'DD/MM/YYYY') ");
						}
					}
				}
				selectquery = selectquery + buf;
				if (distacco) {
					selectquery += " ORDER BY DECODE(AM_MOVIMENTO.CODTIPOMOV, 'TRA', 2, 'AVV', 1, 0), AM_MOVIMENTO.DATINIZIOMOV DESC ";
				}
			} else {
				// creo la query se ho il precedente
				if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) {
					selectquery += " WHERE AM_MOVIMENTO.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO(+)  "
							+ " AND CODSTATOATTO = 'PR' AND PRGMOVIMENTO = " + prgmovprec
							+ " AND ( (AM_MOVIMENTO.CODTIPOMOV = 'CES' AND NVL(AM_MOVIMENTO.CODMVCESSAZIONE, ' ') = 'SC') OR "
							+ " (CODTIPOMOV != 'CES') ) ";
				} else {
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

			// Esecuzione query per determinare il movimento precedente
			SourceBean result = null;
			Vector v = new Vector();
			try {
				if (eseguiQueryMovPrec) {
					result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
					v = result.getAttributeAsVector("ROW");
				}
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
						"Impossibile cercare il movimento precedente", warnings, nested);
			}

			// Esamino il risultato
			SourceBean rowPrec = null;
			if (v.size() == 0) {
				// Nel caso di rettifica di una comunicazione precedente si
				// cerca di inserire un movimento orfano
				String codTipoComunic = (String) record.get("CODTIPOCOMUNIC");
				if (codTipoComunic != null && codTipoComunic.equals(MessageCodes.General.RETTIFICA_COMUNICAZIONE_PREC)
						&& !codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA)) {
					// se sto processando un movimento di cessazione o
					// trasformazione non collegato controllo se posso
					// inserirlo ugualmente anche senza precedente e che la data
					// inizio movimento non sia futura
					try {
						if (DateUtils.compare(oggi, datInizioMov) == -1) {
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
					Integer insScollegatoResult = ProcessorsUtils.checkInserimentoScollegato(codTipoMov, codMonoTempo,
							cdnLav, datInizioMov, trans);
					if (insScollegatoResult == null) {
						// Imposto la data di inizio del movimento originario e
						// quella di inizio del movimento precedente
						record.put("DATAINIZIOAVV", datInizioMov);
						record.put("DATINIZIOMOVPREC", datInizioMov);
						record.put("COLLEGATO", "nessuno");
						return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
					} else {
						return ProcessorsUtils.createResponse(name, classname, insScollegatoResult, null, warnings,
								nested);
					}
				} else {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_FIND_MOV_PREC),
							"Non è stato individuato nessun movimento da utilizzare come precedente per quello da inserire.",
							warnings, nested);
				}
			} else if (v.size() == 1) {
				rowPrec = (SourceBean) v.get(0);
				/*
				 * // CONTROLLO SUL TIPO DI AVVIAMENTO: NON E' POSSIBILE PROROGARE O TRASFORMARE UNA ASSUNZIONE AVVENUTA
				 * // CON UN TIROCINIO (DE_MV_TIPO_ASS.CODMONOTIPO = 'T') if
				 * ((codTipoMov.equals(MovimentoBean.COD_TRASFORMAZIONE)||codTipoMov.equals(MovimentoBean.COD_PROROGA))
				 * && rowPrec.getAttribute("CODMONOTIPOASS")!=null) { String codMonoTipoAss =
				 * (String)rowPrec.getAttribute("CODMONOTIPOASS"); if (codMonoTipoAss.equals("T") ) return
				 * ProcessorsUtils.createResponse(name, classname, new
				 * Integer(MessageCodes.ImportMov.ERR_FIND_MOV_PREC),
				 * "Non è stato individuato nessun movimento da utilizzare come precedente per quello da inserire." ,
				 * warnings, nested); }
				 */
			} else {
				boolean findPrec = false;
				SourceBean row = null;
				String codTipoComunic = (String) record.get("CODTIPOCOMUNIC");
				// Se si tratta di una rettifica di una comunicazione
				// precedente, allora cerco
				// di selezionare come movimento precedente quello annullato in
				// precedenza,
				// il cui prgMovimento si trova in
				// PRGMOVIMENTOPREC_RETTIFICACOMUNICAZIONE
				if (codTipoComunic != null && codTipoComunic.equals(MessageCodes.General.RETTIFICA_COMUNICAZIONE_PREC)
						&& (contesto.equalsIgnoreCase("valida") || contesto.equalsIgnoreCase("validaArchivio"))) {
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
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_FIND_MOV_PREC),
							"Impossibile individuare univocamente il movimento da utilizzare come precedente per quello da inserire, "
									+ "ne esiste più di uno compatibile!",
							warnings, nested);
				}
			}

			// Se ho un result set valido e unico cerco di estrarne i dati
			BigDecimal prgMovimentoPrec = (BigDecimal) rowPrec.getAttribute("PrgMovimento");
			BigDecimal numProroghe = (BigDecimal) rowPrec.getAttribute("NumProroghe");
			String codMonoTempoMovPrec = (String) rowPrec.getAttribute("CodMonoTempo");
			String datInizioMovPrec = (String) rowPrec.getAttribute("DatInizioMov");
			BigDecimal numKloMovPrec = (BigDecimal) rowPrec.getAttribute("NumKloMov");
			String datInizioAvv = (String) rowPrec.getAttribute("DatInizioAvv");
			BigDecimal decRetribuzionePrec = (BigDecimal) rowPrec.getAttribute("DecRetribuzioneMen");
			String datFineMovPrec = (String) rowPrec.getAttribute("DatFineMov");
			String codTipoMovPrec = (String) rowPrec.getAttribute("codTipoMov");
			String codOrarioPrec = (String) rowPrec.getAttribute("codOrario");
			BigDecimal numOreSettPrec = (BigDecimal) rowPrec.getAttribute("numOreSett");
			String codTipoAssPrec = (String) rowPrec.getAttribute("CODTIPOASS");
			String codGradoPrec = (String) rowPrec.getAttribute("CODGRADO");
			String codMvCessazionePrec = (String) rowPrec.getAttribute("CODMVCESSAZIONE");
			String codTipoTrasfPrec = rowPrec.getAttribute("CODTIPOTRASF") != null
					? rowPrec.getAttribute("CODTIPOTRASF").toString()
					: "";
			String codMansionePrec = rowPrec.getAttribute("CODMANSIONE") != null
					? rowPrec.getAttribute("CODMANSIONE").toString()
					: "";

			record.put("PRGMOVIMENTOPREC", prgMovimentoPrec);
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
			record.put("DATINIZIOMOVPREC", datInizioMovPrec);
			// Calcolo la data inizio della proroga (assente nel nuovo tracciato)
			if ((record.containsKey("INIZIOPROROGAASSENTE")) || ((datInizioMov == null || datInizioMov.equals(""))
					&& codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA))) {
				try {
					if (datFineMovPrec != null && !datFineMovPrec.equals("")) {
						record.put("DATINIZIOMOV", DateUtils.giornoSuccessivo(datFineMovPrec));
					}
				} catch (Exception e) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
							"Impossibile cercare il movimento precedente", warnings, nested);
				}
			}
			if (codMonoTempoMovPrec != null) {
				record.put("CODMONOTEMPOMOVPREC", codMonoTempoMovPrec);
				// Se ho una cessazione imposto anche il codMonoTempo a quello
				// del movimento precedente

				// if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) {
				// record.put("CODMONOTEMPO", codMonoTempoMovPrec);
				// }
			}
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
							|| ((String) record.get("CONTEXT")).equalsIgnoreCase("validaArchivio")
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

			if (codGradoPrec != null) {
				record.put("CODGRADOPREC", codGradoPrec);
			}

			String codMansioneMovimento = record.get("CODMANSIONE") != null ? record.get("CODMANSIONE").toString() : "";
			if (codMansioneMovimento.equals("") && !codMansionePrec.equals("")) {
				record.put("CODMANSIONE", codMansionePrec);
			}

			// Aggiungo al record il nuovo valore di collegato
			record.put("COLLEGATO", "precedente");

			// CONTROLLI DECRETO 2013
			SourceBean controlliDecretoResult = this.controlliDecreto(codTipoMovPrec, codTipoTrasfPrec,
					codTipoContratto, codMonoTempoMovPrec, warnings, nested);
			if (controlliDecretoResult != null)
				return controlliDecretoResult;

			return null;
		}
	}

	/**
	 * Questo metodo controlla se si tratta di una cessazione veloce con Avviamento in inserimento o validazione manuale
	 */
	private boolean checkCEV(Map record, String codTipoMov, String contesto) {

		boolean gestisciPVETVE = false;
		if (("valida".equalsIgnoreCase(contesto) || "validaArchivio".equalsIgnoreCase(contesto))
				&& (MovimentoBean.COD_PROROGA.equalsIgnoreCase(codTipoMov)
						|| MovimentoBean.COD_TRASFORMAZIONE.equalsIgnoreCase(codTipoMov))) {
			gestisciPVETVE = true;
		}

		// Se non è una cessazione e non si tratta di una proroga o
		// trasformazione in contesto validazione, ritorno false
		if (!gestisciPVETVE && !codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE))
			return false;

		BigDecimal prgMovAppCVE = (BigDecimal) record.get("PRGMOVIMENTOAPPCVE");
		String datInizioAvvCev = (String) record.get("DATAINIZIOAVVCEV");

		String codTipoComunic = (String) record.get("CODTIPOCOMUNIC");
		if ((codTipoComunic != null) && (codTipoComunic.equals(MessageCodes.General.RETTIFICA_COMUNICAZIONE_PREC))
				&& ("valida".equalsIgnoreCase(contesto) || "validaArchivio".equalsIgnoreCase(contesto))) {
			String rettificatoAvvVce = (String) record.get("RETTIFICA_AVV_VCE");
			if (rettificatoAvvVce != null && rettificatoAvvVce.equalsIgnoreCase("TRUE") && datInizioAvvCev != null
					&& !datInizioAvvCev.equalsIgnoreCase("") && prgMovAppCVE != null) {
				return true;
			} else {
				return false;
			}
		} else {
			// Se è un movimento con un precedente ritorno false
			if (!"nessuno".equalsIgnoreCase((String) record.get("COLLEGATO")))
				return false;

			// A seconda del contesto ho ulteriori controlli diversi
			if ("inserisci".equalsIgnoreCase(contesto)) {
				// Controllo se ho il campo DATINIZIOAVVCEV
				if (datInizioAvvCev != null && !datInizioAvvCev.equalsIgnoreCase(""))
					return true;
				else
					return false;
			} else if ("valida".equalsIgnoreCase(contesto) || "validaArchivio".equalsIgnoreCase(contesto)) {
				// Contesto validazione manuale
				// Controllo se ho il campo PRGMOVIMENTOAPPCVE e la data di
				// inizio dell'AVV veloce
				if (datInizioAvvCev != null && !datInizioAvvCev.equalsIgnoreCase("") && prgMovAppCVE != null)
					return true;
				else
					return false;
			} else
				return false;
		}
	}

}