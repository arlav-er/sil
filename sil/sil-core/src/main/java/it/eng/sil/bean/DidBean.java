package it.eng.sil.bean;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.movimenti.InfoLavoratore;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.DIDManager;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativa;

public class DidBean {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DidBean.class.getName());

	public static final String TipoDichImmediataDisp = "ID";
	private static final String rptStampa = "patto/DichImmDisp_CC.rpt";

	private BigDecimal prgDichDisponibilita = null;
	private String datDichiarazione = null;
	private String codUltimoContratto = null;
	private BigDecimal prgElencoAnagrafico = null;
	private String codStatoAtto = null;
	private String tipoDichiarazione = null;
	private String flgLavoroAutonomo = null;
	private int codiceErroreDID;

	public DidBean() {
	}

	public void loadDid(BigDecimal cdnLav, MultipleTransactionQueryExecutor txExec) throws Exception {
		BigDecimal prgDid = null;
		BigDecimal prgElencoAnagrafico = null;
		String dataDid = null;
		String codStatoAtto = null;
		String codTipoDich = null;
		Object[] params = new Object[] { cdnLav };
		SourceBean row = (SourceBean) txExec.executeQuery("COOP_GET_DID_APERTA", params, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgDid = (BigDecimal) row.getAttribute("prgdichdisponibilita");
			dataDid = (String) row.getAttribute("datdichiarazione");
			codStatoAtto = (String) row.getAttribute("codstatoatto");
			prgElencoAnagrafico = (BigDecimal) row.getAttribute("prgelencoanagrafico");
			codTipoDich = (String) row.getAttribute("codtipodichdisp");
			setPrgDichDisponibilita(prgDid);
			setDatDichiarazione(dataDid);
			setStatoAtto(codStatoAtto);
			setTipoDichiarazione(codTipoDich);
			setPrgElencoAnagrafico(prgElencoAnagrafico);
		}
	}

	public void esisteDid(BigDecimal cdnLav, MultipleTransactionQueryExecutor txExec) throws Exception {
		BigDecimal prgDid = null;
		BigDecimal prgElencoAnagrafico = null;
		String dataDid = null;
		Object[] params = new Object[] { cdnLav };
		SourceBean row = (SourceBean) txExec.executeQuery("COOP_GET_DID_APERTA", params, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgDid = (BigDecimal) row.getAttribute("prgdichdisponibilita");
			dataDid = (String) row.getAttribute("datdichiarazione");
			prgElencoAnagrafico = (BigDecimal) row.getAttribute("prgelencoanagrafico");
			setPrgDichDisponibilita(prgDid);
			setDatDichiarazione(dataDid);
			setPrgElencoAnagrafico(prgElencoAnagrafico);
		}
	}

	public boolean loadPrivacy(BigDecimal cdnLav, MultipleTransactionQueryExecutor txExec) throws Exception {
		BigDecimal prgPrivacy = null;
		try {
			Object[] params = new Object[] { cdnLav, getDatDichiarazione() };
			SourceBean row = (SourceBean) txExec.executeQuery("AUTORIZZAZIONE_PRIVACY_VALIDA", params, "SELECT");
			if (row != null) {
				row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
				prgPrivacy = (BigDecimal) row.getAttribute("prgprivacy");
			}
			if (prgPrivacy != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Verifica esistenza privacy in importazione SAP.",
					(Exception) ex);
			return false;
		}
	}

	public boolean insertPrivacy(BigDecimal cdnLav, BigDecimal userID, MultipleTransactionQueryExecutor txExec)
			throws Exception {
		try {
			Object[] params = new Object[] { cdnLav, getDatDichiarazione(), null, "S", userID, userID };
			Boolean resPrivacy = (Boolean) txExec.executeQuery("INSERT_PRIVACY", params, "INSERT");
			return resPrivacy.booleanValue();
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Inserimento privacy in importazione SAP.", (Exception) ex);
			return false;
		}
	}

	public boolean isDidStipulabile(BigDecimal cdnLav, BigDecimal userID, RequestContainer reqCont, SourceBean request,
			SourceBean response, MultipleTransactionQueryExecutor txExec) {
		boolean checkControllo = false;
		try {
			Vector statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLav, txExec);
			Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLav, getDatDichiarazione(), txExec);
			Vector patti = DBLoad.getPattiStoricizzati(cdnLav, getDatDichiarazione(), txExec);
			Vector movimentiAperti = DBLoad.getMovimentiLavoratore(cdnLav, txExec);
			movimentiAperti = Controlli.togliMovNonProtocollati(movimentiAperti);
			movimentiAperti = Controlli.togliMovimentoInDataFutura(movimentiAperti);

			SituazioneAmministrativa sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti,
					dids, getDatDichiarazione(), txExec, reqCont);

			checkControllo = DIDManager.inserimentoDID(request, reqCont, response, sitAmm, txExec);
			return checkControllo;
		} catch (ControlliException ex) {
			int codiceErrore = ex.getCode();
			it.eng.sil.util.TraceWrapper.error(_logger,
					"Verifica did stipulabile in importazione SAP, errore codice = " + codiceErrore, (Exception) ex);
			return false;
		} catch (Exception ex1) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Verifica did stipulabile in importazione SAP.",
					(Exception) ex1);
			return false;
		}
	}

	public boolean insertDid(BigDecimal cdnlavoratore, BigDecimal userID, MultipleTransactionQueryExecutor txExec) {
		Boolean res = null;
		BigDecimal prgDichDispoNew = null;
		try {
			SourceBean row = (SourceBean) txExec.executeQuery("QUERY_NEXTVAL_STATEMENT", null, "SELECT");
			if (row == null) {
				throw new Exception("Errore generazione sequence");
			}
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			if (row.containsAttribute("DO_NEXTVAL")) {
				String codUltimoContratto = "";
				prgDichDispoNew = (BigDecimal) row.getAttribute("DO_NEXTVAL");
				setPrgDichDisponibilita(prgDichDispoNew);
				Object[] params = new Object[] { cdnlavoratore };
				SourceBean rowUltimoContratto = (SourceBean) txExec.executeQuery("GET_ULTIMO_MOV", params, "SELECT");
				if (rowUltimoContratto != null) {
					rowUltimoContratto = (rowUltimoContratto.containsAttribute("ROW")
							? (SourceBean) rowUltimoContratto.getAttribute("ROW")
							: rowUltimoContratto);
					codUltimoContratto = rowUltimoContratto.getAttribute("CODCONTRATTO") != null
							? rowUltimoContratto.getAttribute("CODCONTRATTO").toString()
							: "";
					if (!codUltimoContratto.equals("")) {
						setUltimoContratto(codUltimoContratto);
					}
				}
				SourceBean sbGenerale = DBLoad.getInfoGenerali();
				Integer numggStipulaPatto = sbGenerale.getAttribute("NUMGGSTIPULAPATTO") != null
						? new Integer(sbGenerale.getAttribute("NUMGGSTIPULAPATTO").toString())
						: 0;
				Integer numggCollOr = sbGenerale.getAttribute("NUMGGCOLLOQUIOOR") != null
						? new Integer(sbGenerale.getAttribute("NUMGGCOLLOQUIOOR").toString())
						: 0;

				String dataStipulaPatto = DateUtils.aggiungiNumeroGiorni(getDatDichiarazione(),
						numggStipulaPatto.intValue());
				String dataScadColl = DateUtils.aggiungiNumeroGiorni(getDatDichiarazione(), numggCollOr.intValue());

				Object[] paramsDid = new Object[] { getPrgDichDisponibilita(), getDatDichiarazione(),
						getPrgElencoAnagrafico(), null, getTipoDichiarazione(), getUltimoContratto(), dataScadColl,
						dataStipulaPatto, null, getStatoAtto(), null, null, userID, userID, null, null, null, null,
						null, getFlgLavoroAutonomo() };
				res = (Boolean) txExec.executeQuery("INSERT_DISPO", paramsDid, "INSERT");
			}
			if (res != null) {
				return res.booleanValue();
			} else {
				return false;
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore inserimento DID in importazione SAP.", (Exception) e);
			return false;
		}
	}

	public boolean protocolla(MultipleTransactionQueryExecutor txExec, String codCpi, BigDecimal cdnLavoratore,
			BigDecimal userID, SourceBean request) {

		Documento doc = null;

		try {
			doc = new Documento();
			String currentDate = DateUtils.getNow();

			doc.setCrystalClearRelativeReportFile(rptStampa);
			doc.setCodTipoDocumento("IM");
			doc.setCodCpi(codCpi);
			doc.setChiaveTabella(getPrgDichDisponibilita().toString());
			doc.setCdnLavoratore(cdnLavoratore);
			doc.setPrgAzienda(null);
			doc.setPrgUnita(null);
			doc.setStrDescrizione("");
			doc.setFlgDocAmm("S");
			doc.setFlgDocIdentifP("N");
			doc.setDatInizio(getDatDichiarazione());
			doc.setStrNumDoc(null);
			doc.setStrEnteRilascio(codCpi);
			doc.setCodMonoIO("I");
			doc.setDatAcqril(currentDate);
			doc.setCodModalitaAcqril(null);
			doc.setCodTipoFile(null);
			doc.setStrNomeDoc("DichiarazioneImmediataDisponibilita.pdf");
			doc.setDatFine(null);
			doc.setStrNote("");
			doc.setTipoProt("S");
			doc.setCodStatoAtto("PR");
			doc.setStrDescrizione("Dichiarazione immediata disponibilità");
			doc.setFlgAutocertificazione("N");
			doc.setPagina("DispoDettaglioPage");
			doc.setCdnUtIns(userID);
			doc.setCdnUtMod(userID);
			doc.setServizioOnLine("DID");

			SourceBean rowProt = (SourceBean) txExec.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
			if (rowProt == null) {
				throw new Exception("impossibile protocollare il documento di identificazione");
			}
			rowProt = (rowProt.containsAttribute("ROW") ? (SourceBean) rowProt.getAttribute("ROW") : rowProt);
			BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
			BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
			String datProtocollazione = (String) rowProt.getAttribute("DATAORAPROT");

			doc.setNumAnnoProt(numAnnoProt);
			doc.setNumProtocollo(numProtocollo);
			doc.setDatProtocollazione(datProtocollazione);

			request.setAttribute("numProt", numProtocollo.toString());
			request.setAttribute("annoProt", numAnnoProt.toString());
			request.setAttribute("dataProt", datProtocollazione);

			String intestazioneStampa = (String) request.getAttribute("intestazioneStampa");

			// parametri per il report
			Map prompts = new HashMap();
			prompts.put("par_DichDisp", getPrgDichDisponibilita().toString());
			prompts.put("cdnLavoratore", cdnLavoratore.toString());
			prompts.put("codCpi", codCpi);
			prompts.put("numProt", numProtocollo.toString());
			prompts.put("numAnnoProt", numAnnoProt.toString());
			prompts.put("dataProt", datProtocollazione);
			prompts.put("docInOut", "Input");
			prompts.put("intestazioneStampa", intestazioneStampa);

			doc.setCrystalClearPromptFields(prompts);

			doc.insert(txExec);

			return true;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore protocollazione DID in importazione SAP.",
					(Exception) e);
			return false;
		}
	}

	public void aggiornaInfoDid(MultipleTransactionQueryExecutor transExec, BigDecimal cdnLavoratore) throws Exception {
		SourceBean row = null;
		String flgObbSco = "";
		String annoNascita = "";
		String flgLaurea = "";
		Vector cat181Rows = null;
		Vector laureaRows = null;
		Vector statoOccRows = null;
		Vector movimenti = null;
		String codStatoOccupaz = null;
		String flgIndennizzato = null;
		String numMesiSosp = null;
		String numMesiAnzianita = null;
		String dataAnzianita = null;
		String numMesiAnzianitaPrec = null;
		String codMonoCalcolaAnz = null;
		String dataCalcolaAnzianita = null;
		String dataCalcolaSosp = null;
		String numMesiSospPrec = null;

		Object[] params = new Object[6];
		params[0] = cdnLavoratore;
		params[1] = cdnLavoratore;
		params[2] = cdnLavoratore;
		params[3] = cdnLavoratore;
		params[4] = cdnLavoratore;
		params[5] = cdnLavoratore;

		SourceBean statoOcc = (SourceBean) transExec.executeQuery("GET_CAMPI_STATO_OCCUPAZ_PROT_DID", params, "SELECT");
		if (statoOcc != null) {
			BigDecimal t = null;
			BigDecimal t1 = null;
			BigDecimal tSosp = null;
			BigDecimal t1Sosp = null;
			Object mesiRischioDisoccCompleto = null;
			BigDecimal numGGRestantiRischioDisocc = null;
			BigDecimal numGGRestantiSospFornero = null;
			BigDecimal ggSospForneroRischioDisocc = null;
			Object nMesiSospForneroCompleto = null;
			codStatoOccupaz = StringUtils.getAttributeStrNotNull(statoOcc, "ROW.codstatooccupaz");
			flgIndennizzato = StringUtils.getAttributeStrNotNull(statoOcc, "ROW.flgindennizzato");
			tSosp = (BigDecimal) statoOcc.getAttribute("ROW.NUMMESISOSP");
			t1Sosp = (BigDecimal) statoOcc.getAttribute("ROW.mesiSospFornero2014");
			if (tSosp != null) {
				if (t1Sosp != null) {
					tSosp = tSosp.add(t1Sosp);
				}
				numMesiSosp = tSosp.toString();
			} else {
				if (t1Sosp != null) {
					tSosp = t1Sosp;
					numMesiSosp = t1Sosp.toString();
				}
			}
			nMesiSospForneroCompleto = statoOcc.getAttribute("mesiSospFornero2014_completo");
			if (nMesiSospForneroCompleto != null && !nMesiSospForneroCompleto.equals("")) {
				String[] sospFornero = nMesiSospForneroCompleto.toString().split("-");
				if (sospFornero.length == 4) {
					numGGRestantiSospFornero = new BigDecimal(sospFornero[3]);
				} else {
					numGGRestantiSospFornero = new BigDecimal(0);
				}
			} else {
				numGGRestantiSospFornero = new BigDecimal(0);
			}
			t = (BigDecimal) statoOcc.getAttribute("ROW.mesi_anz_calc");
			t1 = (BigDecimal) statoOcc.getAttribute("ROW.mesi_rischio_disocc");
			BigDecimal tgiorniAnz = (BigDecimal) statoOcc.getAttribute("ROW.giorni_anz");
			if (tgiorniAnz == null) {
				tgiorniAnz = new BigDecimal(0);
			}
			mesiRischioDisoccCompleto = statoOcc.getAttribute("mesi_rischio_disocc_completo");
			if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
				String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
				if (rischioDisocc.length == 2) {
					numGGRestantiRischioDisocc = new BigDecimal(rischioDisocc[1]);
				} else {
					numGGRestantiRischioDisocc = new BigDecimal(0);
				}
			} else {
				numGGRestantiRischioDisocc = new BigDecimal(0);
			}
			ggSospForneroRischioDisocc = numGGRestantiSospFornero.add(numGGRestantiRischioDisocc);
			int mesiAggiuntivi = (ggSospForneroRischioDisocc.intValue()) / 30;

			if (t != null && tSosp != null) {
				if (t1 != null) {
					tSosp = tSosp.add(t1);
				}
				if (mesiAggiuntivi > 0) {
					tSosp = tSosp.add(new BigDecimal(mesiAggiuntivi));
				}
				int meseDiffAnzianitaGiorni = 0;
				int numGGAnzResidui = tgiorniAnz.intValue();
				if (numGGAnzResidui >= (ggSospForneroRischioDisocc.intValue() % 30)) {
					numGGAnzResidui = numGGAnzResidui - (ggSospForneroRischioDisocc.intValue() % 30);
				} else {
					if ((ggSospForneroRischioDisocc.intValue() % 30) > 0) {
						numGGAnzResidui = numGGAnzResidui + (30 - (ggSospForneroRischioDisocc.intValue() % 30));
						meseDiffAnzianitaGiorni = 1;
						if (t != null && t.intValue() > 0) {
							t = t.subtract(new BigDecimal(meseDiffAnzianitaGiorni));
						}
					}
				}
				if (tSosp.intValue() <= t.intValue()) {
					t = t.subtract(tSosp);
				} else {
					t = new BigDecimal(0);
				}
				numMesiAnzianita = t.toString();
			} else {
				t = new BigDecimal(0);
				numMesiAnzianita = t.toString();
			}
			dataAnzianita = StringUtils.getAttributeStrNotNull(statoOcc, "ROW.DATANZIANITADISOC");
			t = (BigDecimal) statoOcc.getAttribute("ROW.mesi_anz_prec");
			if (t != null) {
				numMesiAnzianitaPrec = t.toString();
			}
			codMonoCalcolaAnz = StringUtils.getAttributeStrNotNull(statoOcc, "ROW.CODMONOCALCOLOANZIANITAPREC297");
			dataCalcolaSosp = StringUtils.getAttributeStrNotNull(statoOcc, "ROW.DATCALCOLOMESISOSP");
			dataCalcolaAnzianita = StringUtils.getAttributeStrNotNull(statoOcc, "ROW.DATCALCOLOANZIANITA");
			t = (BigDecimal) statoOcc.getAttribute("ROW.NUMMESISOSPPREC");
			if (t != null) {
				numMesiSospPrec = t.toString();
			}

			// Recupero delle info necessarie calcolate
			SourceBean temp = null;
			params = new Object[] { cdnLavoratore };
			row = (SourceBean) transExec.executeQuery("GET_181_CAT", params, "SELECT");
			if (row != null) {
				cat181Rows = row.getAttributeAsVector("ROW");
				if (cat181Rows != null && !cat181Rows.isEmpty()) {
					SourceBean rowTmp = (SourceBean) cat181Rows.elementAt(0);
					flgObbSco = (String) rowTmp.getAttribute("FLGOBBLIGOSCOLASTICO");
					annoNascita = (String) rowTmp.getAttribute("datNasc");
				}
			}
			row = (SourceBean) transExec.executeQuery("GET_LAUREA_X_CAT181", params, "SELECT");
			if (row != null) {
				laureaRows = row.getAttributeAsVector("ROW");
				flgLaurea = laureaRows != null && !laureaRows.isEmpty() ? "S" : "N";
			}

			String cat181 = Controlli.getCat181(annoNascita, getDatDichiarazione(), flgObbSco, flgLaurea);
			String codCat181 = null;
			if (cat181 != null && cat181.equalsIgnoreCase("GIOVANE")) {
				codCat181 = "G";
			} else {
				if (cat181 != null && cat181.equalsIgnoreCase("ADOLESCENTE")) {
					codCat181 = "A";
				}
			}

			params = new Object[] { cdnLavoratore, cdnLavoratore, cdnLavoratore, cdnLavoratore, cdnLavoratore };
			temp = (SourceBean) transExec.executeQuery("GET_STATO_OCCUPAZ", params, "SELECT");

			if (temp != null) {
				statoOccRows = temp.getAttributeAsVector("ROW");
			}

			params = new Object[] { cdnLavoratore };
			temp = (SourceBean) transExec.executeQuery("GET_MOVIMENTI_LAVORATORE", params, "SELECT");
			if (temp != null) {
				movimenti = temp.getAttributeAsVector("ROW");
			}

			// Info aggiuntive per lo stato occupaz
			SourceBean rowTmp = null;
			BigDecimal mesiInattivita = null;
			String disoccInoccText = "";
			String codStatoOccRagg = null;
			String sesso = null;
			String donnaInReinserimento = null;
			String flg40790 = null;
			;
			BigDecimal nMesiAnz = null;
			BigDecimal giorniAnzResidui = null;
			BigDecimal nMesiSosp = null;
			BigDecimal nMesiSospPrec = null;
			BigDecimal nMesiAnzPrec = null;
			Object nMesiSospFornero = null;
			BigDecimal numMesiSospFornero = null;
			Object nMesiRischioDisocc = null;
			BigDecimal numMesiRischioDisocc = null;
			if (statoOccRows != null && !statoOccRows.isEmpty()) {
				rowTmp = (SourceBean) statoOccRows.elementAt(0);
				nMesiAnz = (BigDecimal) rowTmp.getAttribute("MESI_ANZ");
				giorniAnzResidui = (BigDecimal) rowTmp.getAttribute("GIORNI_ANZ");
				if (nMesiAnz == null) {
					nMesiAnz = new BigDecimal(0);
				}
				if (giorniAnzResidui == null) {
					giorniAnzResidui = new BigDecimal(0);
				}
				nMesiAnzPrec = (BigDecimal) rowTmp.getAttribute("MESI_ANZ_PREC");
				if (nMesiAnzPrec == null) {
					nMesiAnzPrec = new BigDecimal(0);
				}
				nMesiSosp = (BigDecimal) rowTmp.getAttribute("NUMMESISOSP");
				if (nMesiSosp == null) {
					nMesiSosp = new BigDecimal(0);
				}
				nMesiSospPrec = (BigDecimal) rowTmp.getAttribute("NUMMESISOSPPREC");
				if (nMesiSospPrec == null) {
					nMesiSospPrec = new BigDecimal(0);
				}
				nMesiSospFornero = rowTmp.getAttribute("mesiSospFornero2014");

				if (nMesiSospFornero != null && !nMesiSospFornero.equals("")) {
					String[] sospFornero = nMesiSospFornero.toString().split("-");
					if (sospFornero.length == 4) {
						numMesiSospFornero = new BigDecimal(sospFornero[0]);
						numGGRestantiSospFornero = new BigDecimal(sospFornero[3]);
					} else {
						numMesiSospFornero = new BigDecimal(0);
						numGGRestantiSospFornero = new BigDecimal(0);
					}
				} else {
					numMesiSospFornero = new BigDecimal(0);
					numGGRestantiSospFornero = new BigDecimal(0);
				}
				nMesiRischioDisocc = rowTmp.getAttribute("mesi_rischio_disocc");
				if (nMesiRischioDisocc == null) {
					numMesiRischioDisocc = new BigDecimal(0);
				} else {
					numMesiRischioDisocc = new BigDecimal(nMesiRischioDisocc.toString());
				}
				mesiRischioDisoccCompleto = rowTmp.getAttribute("mesi_rischio_disocc_completo");
				if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
					String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
					if (rischioDisocc.length == 2) {
						numGGRestantiRischioDisocc = new BigDecimal(rischioDisocc[1]);
					} else {
						numGGRestantiRischioDisocc = new BigDecimal(0);
					}
				} else {
					numGGRestantiRischioDisocc = new BigDecimal(0);
				}
				ggSospForneroRischioDisocc = numGGRestantiSospFornero.add(numGGRestantiRischioDisocc);
				mesiAggiuntivi = (ggSospForneroRischioDisocc.intValue()) / 30;
				int meseDiffAnzianitaGiorni = 0;
				if (giorniAnzResidui.intValue() >= (ggSospForneroRischioDisocc.intValue() % 30)) {
					giorniAnzResidui = giorniAnzResidui.subtract(ggSospForneroRischioDisocc);
				} else {
					if ((ggSospForneroRischioDisocc.intValue()) % 30 > 0) {
						BigDecimal appAnz = new BigDecimal(30 - (ggSospForneroRischioDisocc.intValue() % 30));
						giorniAnzResidui = giorniAnzResidui.add(appAnz);
						meseDiffAnzianitaGiorni = 1;
					}
				}

				nMesiAnz = nMesiAnz.add(nMesiAnzPrec).subtract(nMesiSosp.add(nMesiSospPrec).add(numMesiSospFornero)
						.add(numMesiRischioDisocc).add(new BigDecimal(mesiAggiuntivi)));
				if (nMesiAnz.intValue() > 0 && meseDiffAnzianitaGiorni > 0) {
					nMesiAnz = nMesiAnz.subtract(new BigDecimal(meseDiffAnzianitaGiorni));
				}

				codStatoOccRagg = (String) rowTmp.getAttribute("codstatooccupazragg");

				InfoLavoratore infoLav = new InfoLavoratore((BigDecimal) rowTmp.getAttribute("cdnLavoratore"));
				sesso = infoLav.getSesso();// (String)rowTmp.getAttribute("STRSESSO");
			}

			if (movimenti != null && movimenti.size() > 0) {
				rowTmp = (SourceBean) movimenti.get(0);
				mesiInattivita = (BigDecimal) rowTmp.getAttribute("mesiInattivita");
			}

			if (nMesiAnz == null) {
				nMesiAnz = new BigDecimal(0);
			}
			BigDecimal totMesiAnz = nMesiAnz;
			disoccInoccText = Controlli.disoccInoccLungaDurata(codStatoOccRagg, totMesiAnz, codCat181);
			String codMonoDisoccInocc = "";
			if ((disoccInoccText != null) && disoccInoccText.equals("Disoccupato di lunga durata")) {
				codMonoDisoccInocc = "D";
			} else if ((disoccInoccText != null) && disoccInoccText.equals("Inoccupato di lunga durata")) {
				codMonoDisoccInocc = "I";
			}
			boolean donnaInReinserimentoB = Controlli.donnaInInserimentoLavorativo(codStatoOccRagg, mesiInattivita,
					sesso);
			if (donnaInReinserimentoB) {
				donnaInReinserimento = "S";
			} else {
				donnaInReinserimento = "N";
			}
			if (totMesiAnz != null && totMesiAnz.compareTo(new BigDecimal(24)) >= 0) {
				flg40790 = "S";
			}

			params = new Object[15];
			params[0] = codStatoOccupaz;
			params[1] = codMonoDisoccInocc;
			params[2] = flgIndennizzato;
			params[3] = flg40790;
			params[4] = donnaInReinserimento;
			params[5] = numMesiSosp;
			params[6] = numMesiAnzianita;
			params[7] = dataAnzianita;
			params[8] = codCat181;
			params[9] = numMesiAnzianitaPrec;
			params[10] = codMonoCalcolaAnz;
			params[11] = dataCalcolaSosp;
			params[12] = dataCalcolaAnzianita;
			params[13] = numMesiSospPrec;
			params[14] = getPrgDichDisponibilita();

			Boolean res = (Boolean) transExec.executeQuery("QUERY_UPDATE_DICH_DISP_IFO_STATO_OCCUPAZ", params,
					"UPDATE");
			if (!res.booleanValue()) {
				throw new Exception("Impossibile aggiornare le info stato occupazionale");
			}
		}
	}

	public boolean associaDocumentoIdentificazione(MultipleTransactionQueryExecutor txExec,
			BigDecimal prgDocumentoColl) {
		try {
			Object params[] = new Object[] { getPrgDichDisponibilita(), prgDocumentoColl };
			Boolean resColl = (Boolean) txExec.executeQuery("UPDATE_COLL_IDENTITA_DA_PORTALE", params, "UPDATE");
			return resColl.booleanValue();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger,
					"Errore associazione documento identificazione alla DID in importazione SAP.", (Exception) e);
			return false;
		}
	}

	public boolean inserisciAssociazioneDocIdentita(MultipleTransactionQueryExecutor txExec, BigDecimal prgDocumento,
			BigDecimal prgDocumentoColl) {
		try {
			Object params[] = new Object[] { prgDocumentoColl, prgDocumento, new BigDecimal("0") };
			Boolean resColl = (Boolean) txExec.executeQuery("INSERT_COLL_IDENTITA_DA_CONFERIMENTO", params, "INSERT");
			return resColl.booleanValue();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger,
					"Errore associazione documento identificazione alla DID in importazione SAP.", (Exception) e);
			return false;
		}
	}

	public BigDecimal getPrgDichDisponibilita() {
		return this.prgDichDisponibilita;
	}

	public void setPrgDichDisponibilita(BigDecimal val) {
		this.prgDichDisponibilita = val;
	}

	public BigDecimal getPrgElencoAnagrafico() {
		return this.prgElencoAnagrafico;
	}

	public void setPrgElencoAnagrafico(BigDecimal val) {
		this.prgElencoAnagrafico = val;
	}

	public String getDatDichiarazione() {
		return this.datDichiarazione;
	}

	public void setDatDichiarazione(String val) {
		this.datDichiarazione = val;
	}

	public String getUltimoContratto() {
		return this.codUltimoContratto;
	}

	public void setUltimoContratto(String val) {
		this.codUltimoContratto = val;
	}

	public String getTipoDichiarazione() {
		return this.tipoDichiarazione;
	}

	public void setTipoDichiarazione(String val) {
		this.tipoDichiarazione = val;
	}

	public String getFlgLavoroAutonomo() {
		return this.flgLavoroAutonomo;
	}

	public void setFlgLavoroAutonomo(String val) {
		this.flgLavoroAutonomo = val;
	}

	public String getStatoAtto() {
		return this.codStatoAtto;
	}

	public void setStatoAtto(String val) {
		this.codStatoAtto = val;
	}

	public int getCodiceErroreDID() {
		return this.codiceErroreDID;
	}

	public int gestisciAutomatismiDID(BigDecimal cdnLavoratore, String dataDichiarazione, BigDecimal userid,
			MultipleTransactionQueryExecutor transExec) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {
			SourceBean row = null;
			BigDecimal prgDidPA = null;
			Object[] params = new Object[1];
			params[0] = prgElencoAnagrafico;
			row = (SourceBean) transExec.executeQuery("GET_DID_PA_APERTA", params, "SELECT");
			if (row != null) {
				row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
				prgDidPA = (BigDecimal) row.getAttribute("PRGDICHDISPONIBILITA");
				if (prgDidPA != null) {
					// la devo cancellare
					command = (StoredProcedureCommand) transExec.getDataConnection()
							.createStoredProcedureCommand("{ call ? := PG_UTILS_DID_PATTO.DELETEDIDPA(?,?) }");

					int paramIndex = 0;
					ArrayList parameters = new ArrayList(3);

					// Parametro di Ritorno
					parameters.add(transExec.getDataConnection().createDataField("esito", Types.INTEGER, null));
					command.setAsOutputParameters(paramIndex++);

					// preparazione dei Parametri di Input
					parameters.add(transExec.getDataConnection().createDataField("prgDid", Types.BIGINT, prgDidPA));
					command.setAsInputParameters(paramIndex++);

					parameters.add(transExec.getDataConnection().createDataField("cdnUt", Types.BIGINT, userid));
					command.setAsInputParameters(paramIndex++);

					// Chiamata alla Stored Procedure
					dr = command.execute(parameters);
					PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
					DataField df = pdr.getPunctualDatafield();
					Integer esito = (Integer) df.getObjectValue();

					if (esito.intValue() < 0) {
						throw new Exception(
								"Errore in gestione automatismi did a seguito del Conferimento DID:PG_UTILS_DID_PATTO.DELETEDIDPA");
					}
				}
			}

			// se a sistema SIL esiste una DID annullata con stessa data dichiarazione di quella del conferimento DID,
			// provare ad effettuarne in automatico la riapertura e la conseguente riapertura del patto
			BigDecimal prgDidAnnullata = null;
			params = new Object[2];
			params[0] = cdnLavoratore;
			params[1] = dataDichiarazione;
			row = (SourceBean) transExec.executeQuery("GET_DID_ANNULLATA", params, "SELECT");
			if (row != null) {
				row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
				prgDidAnnullata = (BigDecimal) row.getAttribute("PRGDICHDISPONIBILITA");
				if (prgDidAnnullata != null) {
					// provare ad effettuarne in automatico la riapertura e la conseguente riapertura del patto
					// la devo cancellare
					command = (StoredProcedureCommand) transExec.getDataConnection()
							.createStoredProcedureCommand("{ call ? := PG_UTILS_DID_PATTO.RIAPRIDIDAN(?,?,?) }");

					int paramIndex = 0;
					ArrayList parameters = new ArrayList(4);

					// Parametro di Ritorno
					parameters.add(transExec.getDataConnection().createDataField("esito", Types.INTEGER, null));
					command.setAsOutputParameters(paramIndex++);

					// preparazione dei Parametri di Input
					parameters.add(
							transExec.getDataConnection().createDataField("prgDid", Types.BIGINT, prgDidAnnullata));
					command.setAsInputParameters(paramIndex++);

					parameters
							.add(transExec.getDataConnection().createDataField("cdnLav", Types.BIGINT, cdnLavoratore));
					command.setAsInputParameters(paramIndex++);

					parameters.add(transExec.getDataConnection().createDataField("cdnUt", Types.BIGINT, userid));
					command.setAsInputParameters(paramIndex++);

					// Chiamata alla Stored Procedure
					dr = command.execute(parameters);
					PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
					DataField df = pdr.getPunctualDatafield();
					Integer esito = (Integer) df.getObjectValue();

					if (esito.intValue() < 0) {
						throw new Exception(
								"Errore in gestione automatismi did a seguito del Conferimento DID:PG_UTILS_DID_PATTO.RIAPRIDIDAN");
					}
					setPrgDichDisponibilita(prgDidAnnullata);
					return 1;
				}
			}
			return 0;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger,
					"Errore in gestione automatismi did a seguito del Conferimento DID.", (Exception) e);
			return -1;
		} finally {
			Utils.releaseResources(null, command, dr);
		}
	}

	public BigDecimal[] loadDocumentoIdentificazione(BigDecimal cdnLav, MultipleTransactionQueryExecutor txExec) {
		BigDecimal[] vettDocIdentifazione = new BigDecimal[2];
		BigDecimal prgDocumento = null;
		BigDecimal prgDocColl = null;
		try {
			String dataRif = getDatDichiarazione();
			Object[] params = new Object[] { cdnLav, dataRif, dataRif, dataRif };
			SourceBean row = (SourceBean) txExec.executeQuery("LOAD_DOCUMENTO_IDENTITA_VALIDO", params, "SELECT");
			if (row != null) {
				Vector documenti = row.getAttributeAsVector("ROW");
				if (documenti.size() > 0) {
					SourceBean rowDoc = (SourceBean) documenti.get(0);
					if (rowDoc != null && rowDoc.containsAttribute("PRGDOCUMENTOCOLL")) {
						prgDocColl = (BigDecimal) rowDoc.getAttribute("PRGDOCUMENTOCOLL");
					}
					if (rowDoc != null && rowDoc.containsAttribute("PRGDOCUMENTO")) {
						prgDocumento = (BigDecimal) rowDoc.getAttribute("PRGDOCUMENTO");
					}
				}
			}
			vettDocIdentifazione[0] = prgDocumento;
			vettDocIdentifazione[1] = prgDocColl;

			return vettDocIdentifazione;
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.error(_logger,
					"Verifica esistenza documento identificazione in importazione SAP.", (Exception) ex);
			return null;
		}
	}

	public BigDecimal loadDocumentoIdentificazioneNE(BigDecimal cdnLav, MultipleTransactionQueryExecutor txExec) {
		BigDecimal prgDocColl = null;
		try {
			String dataRif = getDatDichiarazione();
			Object[] params = new Object[] { cdnLav, dataRif, dataRif, dataRif };
			SourceBean row = (SourceBean) txExec.executeQuery("LOAD_DOCUMENTO_IDENTITA_VALIDO_NE", params, "SELECT");
			if (row != null) {
				Vector documenti = row.getAttributeAsVector("ROW");
				if (documenti.size() > 0) {
					SourceBean rowDoc = (SourceBean) documenti.get(0);
					if (rowDoc != null && rowDoc.containsAttribute("PRGDOCUMENTOCOLL")) {
						prgDocColl = (BigDecimal) rowDoc.getAttribute("PRGDOCUMENTOCOLL");
					}
				}
			}
			return prgDocColl;
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.error(_logger,
					"Verifica esistenza documento identificazione in importazione SAP.", (Exception) ex);
			return null;
		}
	}

	public boolean insertDocumentoIdentificazione(BigDecimal cdnLav, String codCpi, BigDecimal userID,
			MultipleTransactionQueryExecutor txExec) {
		try {
			String dataRichiesta = DateUtils.getNow();
			Documento docIdentita = new Documento();
			docIdentita.setPrgDocumento(null);
			docIdentita.setCodCpi(codCpi);
			docIdentita.setCdnLavoratore(cdnLav);
			docIdentita.setCodTipoDocumento("NE");
			docIdentita.setFlgDocIdentifP("S");
			docIdentita.setDatInizio(getDatDichiarazione());
			docIdentita.setCodMonoIO("I");
			docIdentita.setDatAcqril(dataRichiesta);
			docIdentita.setPagina("DispoDettaglioPage");

			SourceBean rowProt = (SourceBean) txExec.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
			if (rowProt == null) {
				throw new Exception("impossibile protocollare il documento di identificazione");
			}
			rowProt = (rowProt.containsAttribute("ROW") ? (SourceBean) rowProt.getAttribute("ROW") : rowProt);
			BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
			BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
			String datProtocollazione = (String) rowProt.getAttribute("DATAORAPROT");

			docIdentita.setNumAnnoProt(numAnnoProt);
			docIdentita.setNumProtocollo(numProtocollo);
			docIdentita.setDatProtocollazione(datProtocollazione);
			docIdentita.setTipoProt("S");
			docIdentita.setCodStatoAtto("PR");
			docIdentita.setCdnUtMod(userID);
			docIdentita.setCdnUtIns(userID);

			docIdentita.insert(txExec);

			return true;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore inserimento documento di identificazione.",
					(Exception) e);
			return false;
		}
	}

	public boolean inserisciDid(SourceBean request, SourceBean response, String cdnLavoratore, String dataDid,
			BigDecimal prgElencoAnagrafico, String codCpi, BigDecimal userid, RequestContainer requestContainer,
			MultipleTransactionQueryExecutor transExec) throws Exception {
		boolean existsPrivacy = true;
		boolean existsDocIdentificazione = true;
		boolean associaDocIdentificazione = true;
		boolean successDid = true;
		setDatDichiarazione(dataDid);
		setStatoAtto(Properties.STATO_ATTO_PROTOC);
		setTipoDichiarazione(Properties.TIPO_DICHIARAZIONE_DID);
		setPrgElencoAnagrafico(prgElencoAnagrafico);
		if (request.getAttribute("flgLavoroAutonomo") != null) {
			setFlgLavoroAutonomo(request.getAttribute("flgLavoroAutonomo").toString());
		}
		if (request.containsAttribute("cdnLavoratore")) {
			request.updAttribute("cdnLavoratore", cdnLavoratore);
		} else {
			request.setAttribute("cdnLavoratore", cdnLavoratore);
		}
		if (request.containsAttribute("datDichiarazione")) {
			request.updAttribute("datDichiarazione", dataDid);
		} else {
			request.setAttribute("datDichiarazione", dataDid);
		}
		if (request.containsAttribute("FORZA_INSERIMENTO")) {
			request.updAttribute("FORZA_INSERIMENTO", "true");
		} else {
			request.setAttribute("FORZA_INSERIMENTO", "true");
		}
		if (request.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
			request.updAttribute("CONTINUA_CALCOLO_SOCC", "true");
		} else {
			request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
		}
		if (request.containsAttribute("FORZA_CHIUSURA_MOBILITA")) {
			request.updAttribute("FORZA_CHIUSURA_MOBILITA", "true");
		} else {
			request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
		}
		if (request.containsAttribute("intestazioneStampa")) {
			request.updAttribute("intestazioneStampa", "OFF");
		} else {
			request.setAttribute("intestazioneStampa", "OFF");
		}
		requestContainer.setServiceRequest(request);

		BigDecimal cdnLav = new BigDecimal(cdnLavoratore);

		if (isDidStipulabile(cdnLav, userid, requestContainer, request, response, transExec)) {
			if (!loadPrivacy(cdnLav, transExec)) {
				existsPrivacy = insertPrivacy(cdnLav, userid, transExec);
			}
			BigDecimal prgDocumentoIdentificazione = null;
			BigDecimal prgDocCollIdentificazione = null;
			BigDecimal[] vettDocIdentifazione = loadDocumentoIdentificazione(cdnLav, transExec);
			if (vettDocIdentifazione == null) {
				codiceErroreDID = MessageCodes.CONFERIMENTO_DID.ERR_GENERICO_DOCUMENTO_IDENTIFICAZIONE;
				return false;
			} else {
				prgDocumentoIdentificazione = vettDocIdentifazione[0];
				prgDocCollIdentificazione = vettDocIdentifazione[1];
				if (prgDocumentoIdentificazione == null) {
					existsDocIdentificazione = insertDocumentoIdentificazione(cdnLav, codCpi, userid, transExec);
					prgDocCollIdentificazione = loadDocumentoIdentificazioneNE(cdnLav, transExec);
				} else {
					if (prgDocCollIdentificazione == null) {
						SourceBean rowsD = (SourceBean) transExec.executeQuery("NEXT_S_AM_DOCUMENTO_COLL", null,
								"SELECT");
						rowsD = rowsD.containsAttribute("ROW") ? (SourceBean) rowsD.getAttribute("ROW") : rowsD;
						prgDocCollIdentificazione = (BigDecimal) rowsD.getAttribute("KEY");
						existsDocIdentificazione = inserisciAssociazioneDocIdentita(transExec,
								prgDocumentoIdentificazione, prgDocCollIdentificazione);
					}
				}
			}

			if (existsPrivacy && existsDocIdentificazione) {
				successDid = insertDid(cdnLav, userid, transExec);
				if (prgDocCollIdentificazione != null) {
					associaDocIdentificazione = associaDocumentoIdentificazione(transExec, prgDocCollIdentificazione);
				}
				if (successDid && associaDocIdentificazione) {
					successDid = protocolla(transExec, codCpi, cdnLav, userid, request);
					if (!successDid) {
						codiceErroreDID = MessageCodes.CONFERIMENTO_DID.ERR_GENERICO_PROTOCOLLA_DID;
					}
					return successDid;
				} else {
					codiceErroreDID = MessageCodes.CONFERIMENTO_DID.ERR_GENERICO_INSERT_DID;
					return false;
				}
			} else {
				codiceErroreDID = MessageCodes.CONFERIMENTO_DID.ERR_GENERICO_DOCUMENTO_IDENTIFICAZIONE;
				return false;
			}
		} else {
			codiceErroreDID = MessageCodes.CONFERIMENTO_DID.ERR_CONTROLLI_STIPULA_DID;
			return false;
		}
	}

}
