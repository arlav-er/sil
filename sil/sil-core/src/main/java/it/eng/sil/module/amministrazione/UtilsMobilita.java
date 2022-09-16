/*
 * Creato il 13-dic-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.security.TransactionProfileDataFilter;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.amministrazione.impatti.ChiusuraMobilitaBean;
import it.eng.sil.util.amministrazione.impatti.Contratto;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.ControlliExt;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.EventoAmministrativo;
import it.eng.sil.util.amministrazione.impatti.LimiteReddito;
import it.eng.sil.util.amministrazione.impatti.LimiteRedditoExt;
import it.eng.sil.util.amministrazione.impatti.ListaMobilita;
import it.eng.sil.util.amministrazione.impatti.MobilitaBean;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;
import it.eng.sil.util.amministrazione.impatti.Reddito;
import it.eng.sil.util.amministrazione.impatti.Retribuzione;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;

/**
 * @author Landi Giovanni
 *
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class UtilsMobilita {

	public static void setMovimentoInMobilita(SourceBean request, SourceBean responseWarning,
			TransactionQueryExecutor transExec) throws Exception {
		SourceBean sb = null;
		String prgMovimento = "";
		// String codGrado = "";
		// String numLivello = "";
		// String codMansione = "";
		// String codCCNL = "";
		// String descCCNL = "";
		Object cdnlavoratore = request.getAttribute("cdnlavoratore");
		BigDecimal prgAziendaMob = request.containsAttribute("PRGAZIENDA")
				? new BigDecimal(request.getAttribute("PRGAZIENDA").toString())
				: null;
		BigDecimal prgUnitaMob = request.containsAttribute("PRGUNITA")
				? new BigDecimal(request.getAttribute("PRGUNITA").toString())
				: null;
		String datFineMov = request.containsAttribute("datFineMov") ? request.getAttribute("datFineMov").toString()
				: "";
		// carico i movimenti protocollati del lavoratore e per la relativa azienda
		Vector movimentiAperti = getMovimentiLavoratore(cdnlavoratore, prgAziendaMob, transExec);
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
			String codMvCessazione = StringUtils.getAttributeStrNotNull(sbMov, "CODMVCESSAZIONE");
			BigDecimal prgUnita = (BigDecimal) sbMov.getAttribute("PRGUNITA");
			if (!codTipoMov.equalsIgnoreCase("CES") && codMonoTempo.equalsIgnoreCase("I") && prgMovSucc != null
					&& !datFineMov.equals("") && datFineMovEffettiva.equals(datFineMov)
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
		} else {
			if (movCompatibiliSedeAzienda.size() == 0 && movCompatibiliAzienda.size() == 1) {
				// ho trovato un solo movimento a tempo indeterminato cessato
				// compatibile con la mobilità (rispetto alla sede aziendale)
				sb = (SourceBean) movCompatibiliAzienda.get(0);
			}
		}
		if (sb != null) {
			prgMovimento = sb.getAttribute("PRGMOVIMENTOSUCC").toString(); // prgmovimento della cessazione
			// codGrado = sb.containsAttribute("CODGRADOCES")?sb.getAttribute("CODGRADOCES").toString():"";
			// numLivello = sb.containsAttribute("NUMLIVELLOCES")?sb.getAttribute("NUMLIVELLOCES").toString():"";
			// codMansione = sb.containsAttribute("CODMANSIONECES")?sb.getAttribute("CODMANSIONECES").toString():"";
			// codCCNL = sb.containsAttribute("CODCCNL")?sb.getAttribute("CODCCNL").toString():"";
			// descCCNL = sb.containsAttribute("strCCNL")?sb.getAttribute("strCCNL").toString():"";
			if (request.containsAttribute("prgMovimento"))
				request.updAttribute("prgMovimento", prgMovimento);
			else
				request.setAttribute("prgMovimento", prgMovimento);
			// if (request.containsAttribute("codGrado")) request.updAttribute("codGrado", codGrado);
			// else request.setAttribute("codGrado", codGrado);
			// if (request.containsAttribute("CODMANSIONE")) request.updAttribute("CODMANSIONE", codMansione);
			// else request.setAttribute("CODMANSIONE", codMansione);
			// if (request.containsAttribute("strLivello")) request.updAttribute("strLivello", numLivello);
			// else request.setAttribute("strLivello", numLivello);
			// if (request.containsAttribute("codCCNL")) request.updAttribute("codCCNL", codCCNL);
			// else request.setAttribute("codCCNL", codCCNL);
			// if (request.containsAttribute("strCCNL")) request.updAttribute("strCCNL", descCCNL);
			// else request.setAttribute("strCCNL", descCCNL);
			SourceBean rowAssociazione = new SourceBean("ROW");
			rowAssociazione.setAttribute("DETTAGLIO", "La mobilità è stata associata al movimento");
			responseWarning.setAttribute((SourceBean) rowAssociazione);

			// SourceBean rowWarning = new SourceBean("ROW");
			// rowWarning.setAttribute("DETTAGLIO", "Una o più delle seguenti informazioni è stata sovrascritta con
			// quelle del movimento: qualifica, grado, livello, ccnl");
			// responseWarning.setAttribute((SourceBean) rowWarning);
		}
	}

	/**
	 * restituisce tutti i movimenti protocollati del lavoratore in una data azienda
	 * 
	 * @param cdnLavoratore
	 * @param prgAzienda
	 * @param txExec
	 * @return
	 * @throws Exception
	 */
	public static Vector getMovimentiLavoratore(Object cdnLavoratore, Object prgAzienda,
			TransactionQueryExecutor txExec) throws Exception {
		Object params[] = new Object[2];
		params[0] = cdnLavoratore;
		params[1] = prgAzienda;
		Vector movimenti = null;
		SourceBean res = (SourceBean) txExec.executeQuery("GET_TUTTI_MOVIMENTI_LAVORATORE_VALIDAZIONE_MOBILITA", params,
				"SELECT");
		if (res != null)
			movimenti = res.getAttributeAsVector("ROW");
		if (movimenti == null)
			throw new Exception("impossibile leggere i movimenti del lavoratore " + cdnLavoratore.toString());
		return movimenti;
	}

	public static SourceBean getInfoSedeAziendale(Object prgAzienda, Object prgUnita, TransactionQueryExecutor txExec)
			throws Exception {
		SourceBean ret = null;
		Object params[] = new Object[2];
		params[0] = prgAzienda;
		params[1] = prgUnita;
		ret = (SourceBean) txExec.executeQuery("GET_MOB_INFO_SEDE_AZIENDALE", params, "SELECT");
		return DBLoad.getRowAttribute(ret);
	}

	public static boolean controllaPermessi(BigDecimal cdnlavoratore, TransactionQueryExecutor transExec) {
		String _page = "AMMINISTRLISTESPECPAGE";
		SessionContainer session = RequestContainer.getRequestContainer().getSessionContainer();
		User usr = (User) session.getAttribute(User.USERID);
		boolean permettiImpatti = true;
		TransactionProfileDataFilter tProfile = new TransactionProfileDataFilter(usr, _page,
				transExec.getDataConnection());
		tProfile.setCdnLavoratore((BigDecimal) cdnlavoratore);
		permettiImpatti = tProfile.canEditLavoratore();
		return permettiImpatti;
	}

	/**
	 * 
	 * @param prgMobilita
	 * @param dataInizioMobilita
	 * @param datFineMobOrig
	 * @param datMaxDiff
	 * @param decadenzaMotivoFine
	 *            (se = false allora anche i tempi indeterminati full time provocano scorrimento)
	 * @param indice
	 * @param movimenti
	 * @param vettIndispTemp
	 *            (maternità può provocare scorrimento della mobilità)
	 * @return
	 * @throws Exception
	 */
	public static Vector ScorrimentoMobilita(BigDecimal prgMobilita, String dataInizioMobilita, String datFineMobOrig,
			String datMaxDiff, boolean decadenzaMotivoFine, int indice, List movimenti, Vector vettIndispTemp)
			throws Exception {
		Vector ris = null;
		int giorni = 0;
		EventoAmministrativo evento = null;
		Vector eventiScorrimento = new Vector();
		int iCont = 0;
		int jCont = 0;
		String dataInizioEvento = "";
		String dataInizioIndisp = "";
		String dataFineIndisp = null;
		SourceBean sb = null;
		MovimentoBean m = null;
		String codMonoTipoAss = "";
		String codContratto = "";
		Boolean scorrimentoEseguito = new Boolean(false);
		// se la mobilità ha data fine origine = "", allora non ha senso fare lo scorrimento
		if (datFineMobOrig.equals(""))
			return ris;
		int movimentiSize = movimenti.size();
		// ricavo la configurazione per lo scorrimento dei movimenti a td in agricoltura
		String conf_mov_agricoltura = "0";
		Object params[] = new Object[] { "M_AGR_MB" };
		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery("ST_GetConfig", params, "SELECT",
				"SIL_DATI");
		if (rowsSourceBean != null) {
			rowsSourceBean = rowsSourceBean.containsAttribute("ROW") ? (SourceBean) rowsSourceBean.getAttribute("ROW")
					: rowsSourceBean;
			conf_mov_agricoltura = (String) rowsSourceBean.getAttribute("NUM");
		}
		// costruisco il vettore degli eventi che possono determinare uno scorrimento
		if (vettIndispTemp == null || vettIndispTemp.size() == 0) {
			for (int j = 0; j < movimentiSize; j++) {
				evento = (EventoAmministrativo) movimenti.get(j);
				int tipoEvento = evento.getTipoEventoAmministrativo();
				if (tipoEvento == EventoAmministrativo.AVVIAMENTO || tipoEvento == EventoAmministrativo.PROROGA
						|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {
					m = (MovimentoBean) evento;
					if (DateUtils.compare(m.getDataInizio(), DateUtils.getNow()) > 0)
						break;
					String dataFineMovimento = m.getDataFineEffettiva();
					if ((j >= indice) || (dataFineMovimento == null || dataFineMovimento.equals("")
							|| DateUtils.compare(dataFineMovimento, dataInizioMobilita) >= 0)) {
						codMonoTipoAss = m.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
								? m.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
								: "";
						codContratto = m.containsAttribute(MovimentoBean.DB_COD_CONTRATTO)
								? m.getAttribute(MovimentoBean.DB_COD_CONTRATTO).toString()
								: "";
						// lavoro autonomo, tirocini, occasionale, progetto, cococo e attività socialmente utile non
						// comportano scorrimento
						if (!codMonoTipoAss.equalsIgnoreCase("N") && !codMonoTipoAss.equals("T")
								&& !codContratto.equalsIgnoreCase("LO") && !codContratto.equalsIgnoreCase("PG")
								&& !codContratto.equalsIgnoreCase("CO") && !codContratto.equalsIgnoreCase("RP1")) {
							if (!m.containsAttribute("MOV_NON_IMPATTANTE_SCORRIMENTO")) {
								// Rapporti di lavoro in agricoltura
								if (conf_mov_agricoltura.equals("0")) {
									ScorrimentoRapportiAgr(evento, j, movimenti);
									if (!m.containsAttribute("MOV_NON_IMPATTANTE_SCORRIMENTO")) {
										eventiScorrimento.add(evento);
									}
								} else {
									eventiScorrimento.add(evento);
								}
							}
						}
					}
				}
			}
		} else {
			while (iCont < movimentiSize && jCont < vettIndispTemp.size()) {
				sb = (SourceBean) vettIndispTemp.get(jCont);
				dataInizioIndisp = (String) sb.getAttribute("datInizio");
				evento = (EventoAmministrativo) movimenti.get(iCont);
				int tipoEvento = evento.getTipoEventoAmministrativo();
				if (tipoEvento == EventoAmministrativo.AVVIAMENTO || tipoEvento == EventoAmministrativo.PROROGA
						|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {
					m = (MovimentoBean) evento;
					dataInizioEvento = m.getDataInizio();
					if (DateUtils.compare(dataInizioEvento, DateUtils.getNow()) > 0) {
						iCont = movimentiSize;
					} else {
						// controllo se devo gestire il movimento o la condizione di maternità
						if (DateUtils.compare(dataInizioIndisp, dataInizioEvento) <= 0) {
							if (DateUtils.compare(dataInizioIndisp, DateUtils.getNow()) <= 0) {
								eventiScorrimento.add(sb);
							}
							jCont++;
						} else {
							String dataFineMovimento = m.getDataFineEffettiva();
							if ((iCont >= indice) || (dataFineMovimento == null || dataFineMovimento.equals("")
									|| DateUtils.compare(dataFineMovimento, dataInizioMobilita) >= 0)) {
								codMonoTipoAss = m.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
										? m.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
										: "";
								codContratto = m.containsAttribute(MovimentoBean.DB_COD_CONTRATTO)
										? m.getAttribute(MovimentoBean.DB_COD_CONTRATTO).toString()
										: "";
								// lavoro autonomo, tirocini, occasionale, progetto, cococo e attività socialmente utile
								// non comportano scorrimento
								if (codMonoTipoAss.equalsIgnoreCase("N") || codMonoTipoAss.equals("T")
										|| codContratto.equalsIgnoreCase("LO") || codContratto.equalsIgnoreCase("PG")
										|| codContratto.equalsIgnoreCase("CO")
										|| codContratto.equalsIgnoreCase("RP1")) {
									;
								} else {
									if (!m.containsAttribute("MOV_NON_IMPATTANTE_SCORRIMENTO")) {
										// Rapporti di lavoro in agricoltura
										if (conf_mov_agricoltura.equals("0")) {
											ScorrimentoRapportiAgr(evento, iCont, movimenti);
											if (!m.containsAttribute("MOV_NON_IMPATTANTE_SCORRIMENTO")) {
												eventiScorrimento.add(evento);
											}
										} else {
											eventiScorrimento.add(evento);
										}
									}
								}
							}
							iCont++;
						}
					}
				} else {
					iCont++;
				}
			}
			if (iCont < movimentiSize) {
				for (int j = iCont; j < movimentiSize; j++) {
					evento = (EventoAmministrativo) movimenti.get(j);
					int tipoEvento = evento.getTipoEventoAmministrativo();
					if (tipoEvento == EventoAmministrativo.AVVIAMENTO || tipoEvento == EventoAmministrativo.PROROGA
							|| tipoEvento == EventoAmministrativo.TRASFORMAZIONE) {
						m = (MovimentoBean) evento;
						if (DateUtils.compare(m.getDataInizio(), DateUtils.getNow()) > 0)
							break;
						String dataFineMovimento = m.getDataFineEffettiva();
						if ((j >= indice) || (dataFineMovimento == null || dataFineMovimento.equals("")
								|| DateUtils.compare(dataFineMovimento, dataInizioMobilita) >= 0)) {
							codMonoTipoAss = m.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
									? m.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString()
									: "";
							codContratto = m.containsAttribute(MovimentoBean.DB_COD_CONTRATTO)
									? m.getAttribute(MovimentoBean.DB_COD_CONTRATTO).toString()
									: "";
							// lavoro autonomo, tirocini, occasionale, progetto, cococo e attività socialmente utile non
							// comportano scorrimento
							if (!codMonoTipoAss.equalsIgnoreCase("N") && !codMonoTipoAss.equals("T")
									&& !codContratto.equalsIgnoreCase("LO") && !codContratto.equalsIgnoreCase("PG")
									&& !codContratto.equalsIgnoreCase("CO") && !codContratto.equalsIgnoreCase("RP1")) {
								if (!m.containsAttribute("MOV_NON_IMPATTANTE_SCORRIMENTO")) {
									// Rapporti di lavoro in agricoltura
									if (conf_mov_agricoltura.equals("0")) {
										ScorrimentoRapportiAgr(evento, j, movimenti);
										if (!m.containsAttribute("MOV_NON_IMPATTANTE_SCORRIMENTO")) {
											eventiScorrimento.add(evento);
										}
									} else {
										eventiScorrimento.add(evento);
									}
								}
							}
						}
					}
				}
			} else {
				for (int j = jCont; j < vettIndispTemp.size(); j++) {
					sb = (SourceBean) vettIndispTemp.get(j);
					dataInizioIndisp = (String) sb.getAttribute("datInizio");
					if (DateUtils.compare(dataInizioIndisp, DateUtils.getNow()) > 0)
						break;
					eventiScorrimento.add(sb);
				}
			}
		}

		String dataUltimoScorrimento = DateUtils.giornoPrecedente(dataInizioMobilita);
		String dataFineFinal = datFineMobOrig;
		boolean scorrimentoIndisp = false;
		boolean scorrimentoMov = false;
		for (int j = 0; j < eventiScorrimento.size(); j++) {
			sb = (SourceBean) eventiScorrimento.get(j);
			if (sb.containsAttribute("PRGINDISPTEMP")) {
				scorrimentoIndisp = false;
				// scorrimento dovuto a indisponibilità
				dataInizioIndisp = (String) sb.getAttribute("datInizio");
				dataFineIndisp = (String) sb.getAttribute("datFine");
				// se l'indisponibilità è successiva al periodo di mobilità allora esco dal ciclo perché non
				// ci sono più eventi che possono determinare uno scorrimento della mobilità
				if (DateUtils.compare(dataInizioIndisp, dataFineFinal) > 0)
					break;
				if (dataFineIndisp != null) {
					if (dataInizioIndisp != null && DateUtils.compare(dataInizioIndisp, dataFineFinal) <= 0) {
						if (DateUtils.compare(dataInizioIndisp, dataUltimoScorrimento) > 0) {
							giorni = (DateUtils.daysBetween(dataInizioIndisp, dataFineIndisp)) + 1;
							dataFineFinal = DateUtils.aggiungiNumeroGiorni(dataFineFinal, giorni);
							scorrimentoIndisp = true;
						} else {
							if (DateUtils.compare(dataFineIndisp, dataUltimoScorrimento) > 0) {
								giorni = (DateUtils.daysBetween(DateUtils.giornoSuccessivo(dataUltimoScorrimento),
										dataFineIndisp)) + 1;
								dataFineFinal = DateUtils.aggiungiNumeroGiorni(dataFineFinal, giorni);
								scorrimentoIndisp = true;
							}
						}
					}
				}
				if (scorrimentoIndisp) {
					scorrimentoEseguito = new Boolean(true);
					dataUltimoScorrimento = dataFineIndisp;
					if (!datMaxDiff.equals("")) {
						// La maternità comporta sempre lo scorrimento della data fine mobilità
						// e della data massimo differimento (30/05/2008)
						datMaxDiff = DateUtils.aggiungiNumeroGiorni(datMaxDiff, giorni);
					}
				}
			} else {
				String numGGEffettivi = "";
				scorrimentoMov = false;
				// scorrimento dovuto a movimenti
				evento = (EventoAmministrativo) sb;
				m = (MovimentoBean) evento;
				String codMonoTempo = m.getCodMonoTempo();
				String codTipoAss = m.containsAttribute(MovimentoBean.DB_COD_ASSUNZIONE)
						? m.getAttribute(MovimentoBean.DB_COD_ASSUNZIONE).toString()
						: "";
				String codContrattoCurr = m.containsAttribute(MovimentoBean.DB_COD_CONTRATTO)
						? m.getAttribute(MovimentoBean.DB_COD_CONTRATTO).toString()
						: "";
				String codOrario = "";
				if (m.containsAttribute("codOrario")) {
					codOrario = m.getAttribute("codOrario").toString();
				}
				String dataInizioMov = m.getDataInizio();
				String dataFineMov = m.getDataFineEffettiva();
				// se il movimento è successivo al periodo di mobilità allora esco dal ciclo perché non
				// ci sono più eventi che possono determinare uno scorrimento della mobilità
				if (DateUtils.compare(dataInizioMov, dataFineFinal) > 0)
					break;

				if ((DateUtils.compare(dataInizioMov, dataInizioMobilita) >= 0) || (dataFineMov == null
						|| dataFineMov.equals("") || DateUtils.compare(dataFineMov, dataInizioMobilita) >= 0)) {
					String flgMobilitaAperta = "";
					BigDecimal mesiMobilitaAperta = null;
					flgMobilitaAperta = m.containsAttribute("FLGMOBILITARIMANEAPERTA")
							? m.getAttribute("FLGMOBILITARIMANEAPERTA").toString()
							: "";
					mesiMobilitaAperta = (BigDecimal) m.getAttribute("MESIMOBILITAAPERTA");
					if (codMonoTempo.equalsIgnoreCase("I") && flgMobilitaAperta.equalsIgnoreCase("S")
							&& mesiMobilitaAperta != null) {
						flgMobilitaAperta = Controlli.controlloMobilitaApertaDurata(flgMobilitaAperta, m);
					}
					// flgMobilitaAperta = S viene settato solo per i TI cessati con motivo PP (MANCATO SUPERAMENTO DEL
					// PERIODO DI PROVA)
					// (data fine movimento valorizzata così anche per i tempi DETERMINATI e per gli altri casi previsti
					// dalle condizioni)
					if (((flgMobilitaAperta.equalsIgnoreCase("S"))
							|| (codMonoTempo != null && codMonoTempo.equalsIgnoreCase("D"))
							|| (codMonoTempo != null && codMonoTempo.equalsIgnoreCase("I")
									&& (codOrario.equalsIgnoreCase("M") || codOrario.equalsIgnoreCase("V")
											|| codOrario.equalsIgnoreCase("P"))
									&& dataFineMov != null)
							|| (codMonoTempo != null && codMonoTempo.equalsIgnoreCase("I") && !decadenzaMotivoFine
									&& dataFineMov != null)
							|| (codMonoTempo != null && codMonoTempo.equalsIgnoreCase("I")
									&& codOrario.equalsIgnoreCase("N") && m.containsAttribute("NUMORESETT")
									&& !m.getAttribute("NUMORESETT").toString().equals("") && dataFineMov != null)
							|| (codMonoTempo != null && codMonoTempo.equalsIgnoreCase("I")
									&& codContrattoCurr.equalsIgnoreCase("LI") && dataFineMov != null))
							&& !codTipoAss.equalsIgnoreCase("Z.09.02")) {
						// movimenti a partire dalla data inizio mobilità e per questi lo scorrimento è su tutta la
						// durata
						if (DateUtils.compare(dataInizioMov, dataInizioMobilita) >= 0) {
							if (DateUtils.compare(dataInizioMov, dataUltimoScorrimento) > 0) {
								if (m.containsAttribute("GIORNI_EFFETTIVI_AGRICOLTURA")) {
									numGGEffettivi = m.getAttribute("GIORNI_EFFETTIVI_AGRICOLTURA").toString();
									if (!numGGEffettivi.equals("")) {
										giorni = new Integer(numGGEffettivi).intValue();
										dataUltimoScorrimento = DateUtils.aggiungiNumeroGiorni(dataInizioMov, giorni);
										dataFineFinal = DateUtils.aggiungiNumeroGiorni(dataFineFinal, giorni);
										scorrimentoMov = true;
									}
								} else {
									if (dataFineMov != null) {
										giorni = (DateUtils.daysBetween(dataInizioMov, dataFineMov)) + 1;
										dataUltimoScorrimento = dataFineMov;
										dataFineFinal = DateUtils.aggiungiNumeroGiorni(dataFineFinal, giorni);
										scorrimentoMov = true;
									}
								}
							} else {
								if (m.containsAttribute("GIORNI_EFFETTIVI_AGRICOLTURA")) {
									numGGEffettivi = m.getAttribute("GIORNI_EFFETTIVI_AGRICOLTURA").toString();
									if (!numGGEffettivi.equals("")) {
										int giorniEff = new Integer(numGGEffettivi).intValue();
										String dataFineMovGGEff = DateUtils.aggiungiNumeroGiorni(dataInizioMov,
												giorniEff);
										if (dataFineMovGGEff != null
												&& DateUtils.compare(dataFineMovGGEff, dataUltimoScorrimento) > 0) {
											giorni = (DateUtils.daysBetween(
													DateUtils.giornoSuccessivo(dataUltimoScorrimento),
													dataFineMovGGEff)) + 1;
											dataFineFinal = DateUtils.aggiungiNumeroGiorni(dataFineFinal, giorni);
											dataUltimoScorrimento = dataFineMovGGEff;
											scorrimentoMov = true;
										}
									}
								} else {
									if (dataFineMov != null
											&& DateUtils.compare(dataFineMov, dataUltimoScorrimento) > 0) {
										giorni = (DateUtils.daysBetween(
												DateUtils.giornoSuccessivo(dataUltimoScorrimento), dataFineMov)) + 1;
										dataFineFinal = DateUtils.aggiungiNumeroGiorni(dataFineFinal, giorni);
										dataUltimoScorrimento = dataFineMov;
										scorrimentoMov = true;
									}
								}
							}
						} else {
							// movimenti che sono a cavallo della data inizio mobilità
							if (m.containsAttribute("GIORNI_EFFETTIVI_AGRICOLTURA")) {
								numGGEffettivi = m.getAttribute("GIORNI_EFFETTIVI_AGRICOLTURA").toString();
								if (!numGGEffettivi.equals("")) {
									int giorniEff = new Integer(numGGEffettivi).intValue();
									String dataFineMovGGEff = DateUtils.aggiungiNumeroGiorni(dataInizioMov, giorniEff);
									if (dataFineMovGGEff != null
											&& DateUtils.compare(dataFineMovGGEff, dataUltimoScorrimento) > 0) {
										giorni = (DateUtils.daysBetween(
												DateUtils.giornoSuccessivo(dataUltimoScorrimento), dataFineMovGGEff))
												+ 1;
										dataFineFinal = DateUtils.aggiungiNumeroGiorni(dataFineFinal, giorni);
										dataUltimoScorrimento = dataFineMovGGEff;
										scorrimentoMov = true;
									}
								}
							} else {
								if (dataFineMov != null && DateUtils.compare(dataFineMov, dataUltimoScorrimento) > 0) {
									giorni = (DateUtils.daysBetween(DateUtils.giornoSuccessivo(dataUltimoScorrimento),
											dataFineMov)) + 1;
									dataFineFinal = DateUtils.aggiungiNumeroGiorni(dataFineFinal, giorni);
									dataUltimoScorrimento = dataFineMov;
									scorrimentoMov = true;
								}
							}
						}
					} else {
						if (!codTipoAss.equalsIgnoreCase("Z.09.02") && ((codMonoTempo != null
								&& codMonoTempo.equalsIgnoreCase("I")
								&& (codOrario.equalsIgnoreCase("M") || codOrario.equalsIgnoreCase("V")
										|| codOrario.equalsIgnoreCase("P"))
								&& dataFineMov == null)
								|| (codMonoTempo != null && codMonoTempo.equalsIgnoreCase("I") && !decadenzaMotivoFine
										&& dataFineMov == null)
								|| (codMonoTempo != null && codMonoTempo.equalsIgnoreCase("I")
										&& codOrario.equalsIgnoreCase("N") && m.containsAttribute("NUMORESETT")
										&& !m.getAttribute("NUMORESETT").toString().equals("") && dataFineMov == null)
								|| (codMonoTempo != null && codMonoTempo.equalsIgnoreCase("I")
										&& codContrattoCurr.equalsIgnoreCase("LI") && dataFineMov == null))) {
							if (DateUtils.compare(dataInizioMov, dataFineFinal) <= 0) {
								// tempo indeterminato part-time, lo scorrimento è fino alla datamaxdiff
								// se il movimento inizia prima della fine della mobilità
								dataFineFinal = datMaxDiff;
								dataUltimoScorrimento = dataFineFinal;
								scorrimentoMov = true;
							}
						}
					}
					if (scorrimentoMov) {
						scorrimentoEseguito = new Boolean(true);
						if (!datMaxDiff.equals("") && DateUtils.compare(dataFineFinal, datMaxDiff) > 0) {
							dataFineFinal = datMaxDiff;
						}
					}
				}
			}
		}
		ris = new Vector(3);
		ris.add(0, dataFineFinal);
		ris.add(1, datMaxDiff);
		ris.add(2, scorrimentoEseguito);
		return ris;
	}

	private static void ScorrimentoRapportiAgr(EventoAmministrativo evento, int j, List movimenti) throws Exception {
		Object prgMovimento = null;
		Object prgMovimentoPrec = null;
		boolean trovataCessazione = false;
		boolean trovatoMovPrec = false;
		MovimentoBean mApp = null;
		SourceBean sbApp = null;
		EventoAmministrativo eventoApp = null;
		MovimentoBean m = (MovimentoBean) evento; // AVVIAMENTO
		String codTipoContratto = m.containsAttribute("CODTIPOASS") ? m.getAttribute("CODTIPOASS").toString() : "";
		String flagLavoroAgr = m.containsAttribute("FLGLAVOROAGR") ? m.getAttribute("FLGLAVOROAGR").toString() : "";
		String codMonoTempo = m.containsAttribute("CODMONOTEMPO") ? m.getAttribute("CODMONOTEMPO").toString() : "";
		if (evento.getTipoEventoAmministrativo() == EventoAmministrativo.AVVIAMENTO
				&& (codTipoContratto.equalsIgnoreCase("H.01.00")
						|| (flagLavoroAgr.equalsIgnoreCase("S") && codMonoTempo.equalsIgnoreCase("D")))) {
			String sGiorniPrevistiAgr = m.containsAttribute("NUMGGPREVISTIAGR")
					? m.getAttribute("NUMGGPREVISTIAGR").toString()
					: "";
			if (sGiorniPrevistiAgr.equals("")) {
				sGiorniPrevistiAgr = "0";
			}
			int movimentiSize = movimenti.size();
			trovataCessazione = false;
			if (!m.inInserimento()) {
				prgMovimento = m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
				// i movimenti successivi all'avviamento (proroghe e trasformazioni non devono essere considerate
				// nella determinazione dello scorrimento)
				if (m.containsAttribute("MOVIMENTI_PROROGATI")) {
					Vector prec = (Vector) m.getAttribute("MOVIMENTI_PROROGATI");
					SourceBean movimento = null;
					SourceBean movimentoApp = null;
					BigDecimal prgCurr = null;
					BigDecimal prgCurrApp = null;
					for (int k = 0; k < prec.size(); k++) {
						movimento = (SourceBean) prec.get(k);
						prgCurr = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
						if (prgCurr == null || prgCurr.equals(prgMovimento))
							continue;
						for (int i = 0; i < movimentiSize; i++) {
							movimentoApp = (SourceBean) movimenti.get(i);
							prgCurrApp = (BigDecimal) movimentoApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
							if (prgCurrApp != null && prgCurrApp.equals(prgCurr)) {
								if (!movimentoApp.containsAttribute("MOV_NON_IMPATTANTE_SCORRIMENTO")) {
									movimentoApp.setAttribute("MOV_NON_IMPATTANTE_SCORRIMENTO", "true");
								}
								break;
							}
						}
					}
				}
				// devo risalire alla cessazione per determinare il numero gi giorni
				// effettivi lavorati in agricoltura (se presenti)
				for (int c = j + 1; c < movimentiSize; c++) {
					eventoApp = (EventoAmministrativo) movimenti.get(c);
					if (eventoApp.getTipoEventoAmministrativo() == EventoAmministrativo.CESSAZIONE) {
						mApp = (MovimentoBean) eventoApp; // CESSAZIONE
						if (mApp.virtuale())
							continue;
						prgMovimentoPrec = mApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
						while (prgMovimentoPrec != null) {
							if (prgMovimentoPrec.equals(prgMovimento)) {
								trovataCessazione = true;
								prgMovimentoPrec = null;
							} else {
								Object prgMovimentoApp = null;
								trovatoMovPrec = false;
								for (int k = 0; k < movimentiSize; k++) {
									sbApp = (SourceBean) movimenti.get(k);
									prgMovimentoApp = sbApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
									if (prgMovimentoApp != null && prgMovimentoApp.equals(prgMovimentoPrec)) {
										trovatoMovPrec = true;
										break;
									}
								}
								if (trovatoMovPrec) {
									prgMovimentoPrec = sbApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_PREC);
								} else {
									prgMovimentoPrec = null;
								}
							}
						}
					}
					if (trovataCessazione)
						break;
				}

				if (trovataCessazione) {
					// scorrimento = giorni effettivi se specificati, altrimenti giorni previsti
					if (mApp != null) {
						String numggEffettuatiAgr = mApp.containsAttribute("NUMGGEFFETTUATIAGR")
								? mApp.getAttribute("NUMGGEFFETTUATIAGR").toString()
								: "";
						if (!numggEffettuatiAgr.equals("")) {
							if (!m.containsAttribute("GIORNI_EFFETTIVI_AGRICOLTURA")) {
								m.setAttribute("GIORNI_EFFETTIVI_AGRICOLTURA", numggEffettuatiAgr);
							}
						} else {
							if (!m.containsAttribute("GIORNI_EFFETTIVI_AGRICOLTURA")) {
								m.setAttribute("GIORNI_EFFETTIVI_AGRICOLTURA", sGiorniPrevistiAgr);
							}
						}
					}
				} else {
					// non ho trovato la cessazione, allora scorrimento = giorni previsti
					if (!m.containsAttribute("GIORNI_EFFETTIVI_AGRICOLTURA")) {
						m.setAttribute("GIORNI_EFFETTIVI_AGRICOLTURA", sGiorniPrevistiAgr);
					}
				}
			} else { // movimento in inserimento, allora scorrimento = giorni previsti in agricoltura
				if (!m.containsAttribute("GIORNI_EFFETTIVI_AGRICOLTURA")) {
					m.setAttribute("GIORNI_EFFETTIVI_AGRICOLTURA", sGiorniPrevistiAgr);
				}
			}
		}
	}

	public static BigDecimal settaDecadenzaMobilitaPerReddito(SourceBean sbMobilita,
			ChiusuraMobilitaBean chiusuraMobilita, String dataInizioDecadenza, BigDecimal userID, List listaMobilita,
			String flagDecadenza, TransactionQueryExecutor txExecutor) throws Exception {
		// sposto la fine della mobilità al giorno inizio del mov che
		// ha fatto decadere la mobilità per superamento del reddito
		chiusuraMobilita.updAttribute(MobilitaBean.DB_DAT_INIZIO, dataInizioDecadenza);
		if (chiusuraMobilita.containsAttribute(MobilitaBean.DB_DAT_FINE)) {
			chiusuraMobilita.updAttribute(MobilitaBean.DB_DAT_FINE, dataInizioDecadenza);
		} else {
			chiusuraMobilita.setAttribute(MobilitaBean.DB_DAT_FINE, dataInizioDecadenza);
		}
		BigDecimal prgMobilitaIscr = (BigDecimal) sbMobilita.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR);
		// aggiornamento della decadenza sul db
		BigDecimal numkloMob = new BigDecimal(sbMobilita.getAttribute("NUMKLOMOBISCR").toString());
		String dataDecadenza = DateUtils.giornoPrecedente(dataInizioDecadenza);
		numkloMob = DBStore.aggiornaDataFineMobilita(prgMobilitaIscr, dataDecadenza, flagDecadenza, userID, numkloMob,
				txExecutor);
		sbMobilita.updAttribute("NUMKLOMOBISCR", numkloMob);
		aggiornaMobilitaInListaMobilita(listaMobilita, chiusuraMobilita, numkloMob);
		return numkloMob;
	}

	public static BigDecimal settaDecadenzaMobilitaPerReddito(SourceBean sbMobilita, String dataInizioDecadenza,
			BigDecimal userID, List listaMobilita, String flagDecadenza, TransactionQueryExecutor txExecutor)
			throws Exception {
		BigDecimal prgMobilitaIscr = (BigDecimal) sbMobilita.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR);
		// aggiornamento della decadenza sul db
		BigDecimal numkloMob = new BigDecimal(sbMobilita.getAttribute("NUMKLOMOBISCR").toString());
		String dataDecadenza = DateUtils.giornoPrecedente(dataInizioDecadenza);
		numkloMob = DBStore.aggiornaDataFineMobilita(prgMobilitaIscr, dataDecadenza, flagDecadenza, userID, numkloMob,
				txExecutor);
		sbMobilita.updAttribute("NUMKLOMOBISCR", numkloMob);
		aggiornaMobilitaInListaMobilita(listaMobilita, new ChiusuraMobilitaBean(sbMobilita), numkloMob);
		return numkloMob;
	}

	public static BigDecimal settaDecadenzaMobilita(SourceBean sbMobilita, ChiusuraMobilitaBean chiusuraMobilita,
			List movimenti, MovimentoBean movInMobilita, String dataInizioMov, BigDecimal userID, List listaMobilita,
			String flagDecadenza, TransactionQueryExecutor txExecutor) throws Exception {
		// sposto la fine della mobilità al giorno inizio del mov che
		// ha fatto decadere la mobilità
		chiusuraMobilita.updAttribute(MobilitaBean.DB_DAT_INIZIO, dataInizioMov);
		if (chiusuraMobilita.containsAttribute(MobilitaBean.DB_DAT_FINE)) {
			chiusuraMobilita.updAttribute(MobilitaBean.DB_DAT_FINE, dataInizioMov);
		} else {
			chiusuraMobilita.setAttribute(MobilitaBean.DB_DAT_FINE, dataInizioMov);
		}
		BigDecimal prgMobilitaIscr = (BigDecimal) sbMobilita.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR);
		// aggiornamento della decadenza sul db
		BigDecimal numkloMob = new BigDecimal(sbMobilita.getAttribute("NUMKLOMOBISCR").toString());
		String dataDecadenza = DateUtils.giornoPrecedente(dataInizioMov);
		numkloMob = DBStore.aggiornaDataFineMobilita(prgMobilitaIscr, dataDecadenza, flagDecadenza, userID, numkloMob,
				txExecutor);
		sbMobilita.updAttribute("NUMKLOMOBISCR", numkloMob);
		aggiornaMobilitaInListaMobilita(listaMobilita, chiusuraMobilita, numkloMob);
		return numkloMob;
	}

	public static void settaMovimentoFuoriMobilita(List movimenti, MovimentoBean movInMobilita, String dataInizioMov)
			throws Exception {
		if (!movInMobilita.containsAttribute("FLG_MOVIMENTO_NON_MOBILITA")) {
			movInMobilita.setAttribute("FLG_MOVIMENTO_NON_MOBILITA", "1");
		}
		// Per tutti i movimenti che iniziano lo stesso giorno del movimento che ha fatto superare
		// il reddito bisogna settare il flag "FLG_MOVIMENTO_NON_MOBILITA"
		int movimentiSize = movimenti.size();
		for (int cont = 0; cont < movimentiSize; cont++) {
			Object objAppMob = movimenti.get(cont);
			if (objAppMob instanceof MovimentoBean) {
				MovimentoBean movInMobilitaApp = (MovimentoBean) objAppMob;
				String dataInizioMovPerMob = movInMobilitaApp.getDataInizio();
				if (DateUtils.compare(dataInizioMovPerMob, dataInizioMov) == 0 && !movInMobilitaApp
						.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString().equalsIgnoreCase("CES")) {
					if (!movInMobilitaApp.containsAttribute("FLG_MOVIMENTO_NON_MOBILITA")) {
						movInMobilitaApp.setAttribute("FLG_MOVIMENTO_NON_MOBILITA", "1");
					}
				}
			}
		}
	}

	public static void aggiornaMobilitaInListaMobilita(List listaMobilita, ChiusuraMobilitaBean chMobilita,
			BigDecimal numklo) throws Exception {
		BigDecimal prgChiusuraMob = chMobilita.getPrgMobilitaIscr();
		BigDecimal prgMobilita = null;
		SourceBean sbMobilita = null;
		String dataFineMob = DateUtils.giornoPrecedente(chMobilita.getDataInizio());
		int listaMobilitaSize = listaMobilita.size();
		for (int i = 0; i < listaMobilitaSize; i++) {
			sbMobilita = (SourceBean) listaMobilita.get(i);
			prgMobilita = new BigDecimal(sbMobilita.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR).toString());
			if (prgMobilita.equals(prgChiusuraMob)) {
				sbMobilita.updAttribute(MobilitaBean.DB_DAT_FINE, dataFineMob);
				sbMobilita.updAttribute("NUMKLOMOBISCR", numklo);
				break;
			}
		}
	}

	public static void aggiornaMobilitaInListaMobilita(List listaMobilita, ChiusuraMobilitaBean chMobilita)
			throws Exception {
		BigDecimal prgChiusuraMob = chMobilita.getPrgMobilitaIscr();
		BigDecimal prgMobilita = null;
		SourceBean sbMobilita = null;
		String dataFineMob = DateUtils.giornoPrecedente(chMobilita.getDataInizio());
		int listaMobilitaSize = listaMobilita.size();
		for (int i = 0; i < listaMobilitaSize; i++) {
			sbMobilita = (SourceBean) listaMobilita.get(i);
			prgMobilita = new BigDecimal(sbMobilita.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR).toString());
			if (prgMobilita.equals(prgChiusuraMob)) {
				sbMobilita.updAttribute(MobilitaBean.DB_DAT_FINE, dataFineMob);
				break;
			}
		}
	}

	public static void rimuoviMobDallaListaMobilita(BigDecimal prg, List listaMobilita) {
		SourceBean rowMobilita = null;
		BigDecimal prgMobilita = null;
		int listaMobilitaSize = listaMobilita.size();
		for (int i = 0; i < listaMobilitaSize; i++) {
			rowMobilita = (SourceBean) listaMobilita.get(i);
			prgMobilita = (BigDecimal) rowMobilita.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR);
			if (prgMobilita.equals(prg)) {
				listaMobilita.remove(i);
				break;
			}
		}
	}

	public static void rimuoviMobilitaDaMovimenti(BigDecimal prg, List movimenti) throws Exception {
		Object objMobilita = null;
		SourceBean rowMobilita = null;
		BigDecimal prgMobilita = null;
		int movimentiSize = movimenti.size();
		for (int i = 0; i < movimentiSize; i++) {
			objMobilita = movimenti.get(i);
			if (objMobilita instanceof MobilitaBean || objMobilita instanceof ChiusuraMobilitaBean) {
				rowMobilita = (SourceBean) movimenti.get(i);
				prgMobilita = (BigDecimal) rowMobilita.getAttribute(MobilitaBean.DB_PRG_MOBILITA_ISCR);
				if (prgMobilita.equals(prg)) {
					rowMobilita.setAttribute("MOBILITA_NON_IMPATTANTE", "1");
				}
			}
		}
	}

	public static double getLimiteAttuale(MovimentoBean movimento, SourceBean cm, String dataInizioMov,
			Vector movimentiAmm, LimiteRedditoExt limiteReddito, double limiteAttuale) throws Exception {
		if (Controlli.inCollocamentoMirato(cm, dataInizioMov)) {
			if (limiteAttuale < limiteReddito.get(LimiteReddito.CM)) {
				limiteAttuale = limiteReddito.get(LimiteReddito.CM);
			}
		} else {
			switch (Contratto.getTipoContratto(movimento)) {
			case Contratto.AUTONOMO:
				// POST FORNERO AUTONOMO E SUBORDINATO ALLORA IL LIMITE REDDITO AUTONOMO
				if (DateUtils.compare(dataInizioMov, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0) {
					boolean esisteMovimentoParaSubordinato = false;
					if (movimentiAmm.size() > 0) {
						esisteMovimentoParaSubordinato = ControlliExt.getMovimentiLavoroParaSubordinato(dataInizioMov,
								movimentiAmm);
					}
					if (esisteMovimentoParaSubordinato) {
						if (limiteAttuale < limiteReddito.get(LimiteReddito.DIPENDENTE)) {
							limiteAttuale = limiteReddito.get(LimiteReddito.DIPENDENTE);
						}
					} else {
						if (limiteAttuale < limiteReddito.get(LimiteReddito.AUTONOMO)) {
							limiteAttuale = limiteReddito.get(LimiteReddito.AUTONOMO);
						}
					}
				} else {
					// controllare se nella data inizio movimento ci sono
					// rapporti di lavoro dipendente aperti. In tal caso bisogna considerare
					// limite reddito di lavoro dipendente
					boolean esisteMovimentoDip = false;
					if (movimentiAmm.size() > 0) {
						esisteMovimentoDip = ControlliExt.getMovimentiLavoroDipendente(dataInizioMov, movimentiAmm);
					}
					if (esisteMovimentoDip) {
						if (limiteAttuale < limiteReddito.get(LimiteReddito.DIPENDENTE)) {
							limiteAttuale = limiteReddito.get(LimiteReddito.DIPENDENTE);
						}
					} else {
						if (limiteAttuale < limiteReddito.get(LimiteReddito.AUTONOMO)) {
							limiteAttuale = limiteReddito.get(LimiteReddito.AUTONOMO);
						}
					}
				}
				break;
			case Contratto.COCOCO:
			case Contratto.DIP_TD:
			case Contratto.DIP_TI:
				if (DateUtils.compare(dataInizioMov, MessageCodes.General.DATA_DECRETO_FORNERO_2014) >= 0) {
					boolean esisteMovimentoParaSubordinato = false;
					if (movimentiAmm.size() > 0) {
						esisteMovimentoParaSubordinato = ControlliExt.getMovimentiLavoroParaSubordinato(dataInizioMov,
								movimentiAmm);
					}
					if (esisteMovimentoParaSubordinato) {
						if (limiteAttuale < limiteReddito.get(LimiteReddito.DIPENDENTE)) {
							limiteAttuale = limiteReddito.get(LimiteReddito.DIPENDENTE);
						}
					} else {
						boolean esisteMovimentoAutonomo = false;
						if (movimentiAmm.size() > 0) {
							esisteMovimentoAutonomo = ControlliExt.getMovimentiLavoroAutonomo(dataInizioMov,
									movimentiAmm);
						}
						if (esisteMovimentoAutonomo) {
							if (limiteAttuale < limiteReddito.get(LimiteReddito.AUTONOMO)) {
								limiteAttuale = limiteReddito.get(LimiteReddito.AUTONOMO);
							}
						} else {
							if (limiteAttuale < limiteReddito.get(LimiteReddito.DIPENDENTE)) {
								limiteAttuale = limiteReddito.get(LimiteReddito.DIPENDENTE);
							}
						}
					}
				} else {
					if (limiteAttuale < limiteReddito.get(LimiteReddito.DIPENDENTE)) {
						limiteAttuale = limiteReddito.get(LimiteReddito.DIPENDENTE);
					}
				}
				break;
			}
		}
		return limiteAttuale;

	}

	public static boolean isDecadenzaPerReddito(MovimentoBean movInMobilita, Reddito reddito,
			String dataInizioMovReddito, String dataFineMovReddito, String dataInizioMobReddito, int annoInizioMov,
			int annoInizialeMob, double limiteAttuale) throws Exception {
		boolean decadenzaPerReddito = false;
		int ggDiLavoro = 0;
		int ggDiLavoroAnnoSuccessivo = 0;
		if (annoInizioMov > annoInizialeMob) {
			if (annoInizioMov == annoInizialeMob + 1) {
				double redditoAnnoSucc = reddito.getRedditoAnnoSuccessivo();
				reddito = null;
				reddito = new Reddito(redditoAnnoSucc, 0);
			} else {
				reddito = null;
				reddito = new Reddito(0, 0);
			}
			ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioMovReddito, dataFineMovReddito,
					dataInizioMobReddito);
			ggDiLavoroAnnoSuccessivo = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFineMovReddito,
					dataInizioMobReddito);
		} else {
			ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioMovReddito, dataFineMovReddito,
					dataInizioMobReddito);
			ggDiLavoroAnnoSuccessivo = ControlliExt.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFineMovReddito,
					dataInizioMobReddito);
		}
		BigDecimal retribuzione = Retribuzione.getRetribuzioneMen(movInMobilita);
		if (retribuzione != null) {
			reddito.aggiorna(ggDiLavoro, ggDiLavoroAnnoSuccessivo, retribuzione.doubleValue());
			if (reddito.getRedditoAnno() > limiteAttuale)
				decadenzaPerReddito = true;
		} else {
			decadenzaPerReddito = true;
		}
		return decadenzaPerReddito;
	}

	public static boolean scorrimentoPerIndisp(SourceBean request, String codIndisp, String cdnLav, String dataInizio,
			TransactionQueryExecutor txExecutor) throws Exception {
		if (Sottosistema.MO.isOff())
			return false;
		if (!codIndisp.equalsIgnoreCase("I4"))
			return false;
		if (controllaPermessi(new BigDecimal(cdnLav), txExecutor)) {
			ListaMobilita mobilita = new ListaMobilita(cdnLav, txExecutor);
			List listaMobilita = mobilita.getMobilita();
			if (listaMobilita.size() == 0)
				return false;
			// forzature in ricalcolo impatti
			if (!request.containsAttribute("FORZA_INSERIMENTO"))
				request.setAttribute("FORZA_INSERIMENTO", "true");
			else
				request.updAttribute("FORZA_INSERIMENTO", "true");
			if (!request.containsAttribute("CONTINUA_CALCOLO_SOCC"))
				request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			else
				request.updAttribute("CONTINUA_CALCOLO_SOCC", "true");
			// ricalcolo impatti
			SituazioneAmministrativaFactory.newInstance(cdnLav, dataInizio, txExecutor).calcolaImpatti();
			return true;
		} else
			return false;
	}

	public static boolean scorrimentoDaEventi(SourceBean request, String cdnLav, String dataInizio,
			TransactionQueryExecutor txExecutor) throws Exception {
		if (Sottosistema.MO.isOff())
			return false;
		// ricalcolo impatti
		SituazioneAmministrativaFactory.newInstance(cdnLav, dataInizio, txExecutor).calcolaImpatti();
		return true;
	}

	public static void addConfirm(SourceBean request, SourceBean response, int code) throws Exception {
		String forzaRicostruzione = "";
		String continuaRicalcoloSOccManuale = "";
		ArrayList warnings = new ArrayList();
		ArrayList nested = new ArrayList();
		switch (code) {
		case MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE:
		case MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE:
		case MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI:
		case MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA:
			continuaRicalcoloSOccManuale = "true";
			if (request.containsAttribute("FORZA_INSERIMENTO")) {
				forzaRicostruzione = request.getAttribute("FORZA_INSERIMENTO").toString();
			}
			break;
		default:
			forzaRicostruzione = "true";
			if (request.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
				continuaRicalcoloSOccManuale = request.getAttribute("CONTINUA_CALCOLO_SOCC").toString();
			}

		}
		SourceBean puResult = ProcessorsUtils.createResponse("", "CalcolaStatoOccupazionale", new Integer(code),
				"Situazione di incongruenza", warnings, nested);
		ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiOperazione",
				new String[] { forzaRicostruzione, continuaRicalcoloSOccManuale }, true);
		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERROR");
		sb.setAttribute(puResult);
		response.setAttribute(sb);
	}

	/**
	 * Viene chiamata se il movimento che si trova in un periodo di mobilità è successivo all'entrata in vigore della
	 * legge Fornero
	 * 
	 * @param movimentiAnno
	 *            contiene gli avviamenti o eventuali proroghe e trasformazioni orfane che sono a cavallo della mobilità
	 * @param annoReddito
	 * @param dataInizioMobilita
	 * @param dataFineMobEffettiva
	 * @param dataReddito
	 * @param reddito
	 * @param limiteAttuale
	 * @return
	 * @throws Exception
	 */
	public static boolean controllaDecadenzaReddito(Vector movimentiAnno, int annoReddito, String dataInizioMobilita,
			String dataFineMobEffettiva, String dataReddito, Reddito reddito, double limiteAttuale) throws Exception {
		int ggDiLavoro = 0;
		int ggDiLavoroAnnoSuccessivo = 0;
		BigDecimal retribuzione = null;
		int annoInizioMov = 0;
		boolean decadenzaPerReddito = false;
		for (int iAnno = 0; (!decadenzaPerReddito && iAnno < movimentiAnno.size()); iAnno++) {
			SourceBean movAnno = (SourceBean) movimentiAnno.get(iAnno);
			String dataInizioMovAnno = movAnno.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			String dataFineMovAnno = movAnno.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
					? movAnno.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
					: "";
			if (movAnno.getAttribute("MOVIMENTI_PROROGATI") != null) {
				Vector prec = (Vector) movAnno.getAttribute("MOVIMENTI_PROROGATI");
				SourceBean movimentoAvv = null;
				SourceBean movimentoSucc = null;
				String dataInizioPrec = "";
				String dataInizioSucc = "";
				String dataFinePrec = "";
				BigDecimal retribuzionePrec = null;
				int precSize = prec.size();

				if (precSize > 0) {
					int movUltimoCatena = precSize - 1;
					SourceBean movimentoUltimo = (SourceBean) prec.get(movUltimoCatena);
					String dataFineCatena = movimentoUltimo.containsAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
							? movimentoUltimo.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA).toString()
							: "";
					if (dataFineCatena.equals("") || DateUtils.compare(dataFineCatena, dataInizioMobilita) >= 0) {
						for (int k = 0; (!decadenzaPerReddito && k < precSize); k++) {
							movimentoAvv = (SourceBean) prec.get(k);
							int kSucc = k + 1;
							dataInizioPrec = movimentoAvv.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
							if (movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA) != null) {
								dataFinePrec = movimentoAvv.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA)
										.toString();
							} else {
								dataFinePrec = null;
							}
							// Se il movimento successivo nel vettore dei movimenti prorogati ha la stessa data inizio
							// del movimento corrente nel vettore dei prorogati, allora al fine del reddito non lo
							// considero
							if (kSucc < precSize) {
								movimentoSucc = (SourceBean) prec.get(kSucc);
								dataInizioSucc = movimentoSucc.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
								if (DateUtils.compare(dataInizioPrec, dataInizioSucc) == 0) {
									continue;
								}
							}
							// prendo la retribuzione
							if (DateUtils.getAnno(dataInizioPrec) < annoReddito) {
								dataInizioPrec = "01/01/" + annoReddito;
							}
							if (dataFinePrec == null || dataFinePrec.equals("")
									|| (DateUtils.getAnno(dataFinePrec) > annoReddito)) {
								dataFinePrec = "31/12/" + annoReddito;
							}
							retribuzionePrec = Retribuzione.getRetribuzioneMen(movimentoAvv);
							annoInizioMov = DateUtils.getAnno(dataInizioPrec);
							if (annoInizioMov <= annoReddito && (dataFinePrec == null || dataFinePrec.equals("")
									|| DateUtils.getAnno(dataFinePrec) >= annoReddito)) {
								if (retribuzionePrec != null) {
									ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioPrec, dataFinePrec,
											dataReddito);
									ggDiLavoroAnnoSuccessivo = ControlliExt
											.getNumeroGiorniDiLavoroAnnoSuccessivo(dataFinePrec, dataReddito);
									reddito.aggiorna(ggDiLavoro, ggDiLavoroAnnoSuccessivo,
											retribuzionePrec.doubleValue());
									if (reddito.getRedditoAnno() > limiteAttuale) {
										decadenzaPerReddito = true;
									}
								} else {
									decadenzaPerReddito = true;
								}
							}
						}
					}
				}
			} else {
				if (dataFineMovAnno.equals("") || DateUtils.compare(dataFineMovAnno, dataInizioMobilita) >= 0) {
					if (DateUtils.getAnno(dataInizioMovAnno) < annoReddito) {
						dataInizioMovAnno = "01/01/" + annoReddito;
					}
					if (dataFineMovAnno.equals("") || (DateUtils.getAnno(dataFineMovAnno) > annoReddito)) {
						dataFineMovAnno = "31/12/" + annoReddito;
					}

					ggDiLavoro = ControlliExt.getNumeroGiorniDiLavoro(dataInizioMovAnno, dataFineMovAnno, dataReddito);
					retribuzione = Retribuzione.getRetribuzioneMen(movAnno);
					if (retribuzione != null) {
						reddito.aggiorna(ggDiLavoro, ggDiLavoroAnnoSuccessivo, retribuzione.doubleValue());
						if (reddito.getRedditoAnno() > limiteAttuale) {
							decadenzaPerReddito = true;
						}
					} else {
						decadenzaPerReddito = true;
					}
				}
			}
		}
		return decadenzaPerReddito;
	}

}
