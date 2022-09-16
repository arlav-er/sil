package it.eng.sil.coop.webservices.bean;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.movimenti.InfoLavoratore;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

public class DidBean {
	private BigDecimal prgKey = null;
	private String dataDichiarazione = null;
	private BigDecimal prgElencoAnag = null;
	private String tipoDichiarazione = null;
	private String codStatoAttoDid = null;
	private String codUltimoContratto = null;
	private String flgRischioDisoccupazione = null;
	private String datLicenziamento = null;
	private BigDecimal userSP = null;

	public DidBean(BigDecimal prg, String dataDich, BigDecimal prgEAnag, String tipoDich, String codStatoAtto,
			BigDecimal userID) throws Exception {
		this.prgKey = prg;
		this.dataDichiarazione = dataDich;
		this.prgElencoAnag = prgEAnag;
		this.tipoDichiarazione = tipoDich;
		this.codStatoAttoDid = codStatoAtto;
		this.userSP = userID;
	}

	public DidBean(BigDecimal prg, String dataDich, BigDecimal prgEAnag, String tipoDich, String codStatoAtto)
			throws Exception {
		this.prgKey = prg;
		this.dataDichiarazione = dataDich;
		this.prgElencoAnag = prgEAnag;
		this.tipoDichiarazione = tipoDich;
		this.codStatoAttoDid = codStatoAtto;
	}

	public String getDataDichiarazione() {
		return this.dataDichiarazione;
	}

	public void setDataDichiarazione(String val) {
		this.dataDichiarazione = val;
	}

	public String getTipoDichiarazione() {
		return this.tipoDichiarazione;
	}

	public void setTipoDichiarazione(String val) {
		this.tipoDichiarazione = val;
	}

	public String getStatoAtto() {
		return this.codStatoAttoDid;
	}

	public void setStatoAtto(String val) {
		this.codStatoAttoDid = val;
	}

	public BigDecimal getProgressivo() {
		return this.prgKey;
	}

	public void setProgressivo(BigDecimal val) {
		this.prgKey = val;
	}

	public BigDecimal getPrgEA() {
		return this.prgElencoAnag;
	}

	public void setPrgEA(BigDecimal val) {
		this.prgElencoAnag = val;
	}

	public BigDecimal getUserSP() {
		return this.userSP;
	}

	public void setUserSP(BigDecimal val) {
		this.userSP = val;
	}

	public String getUltimoContratto() {
		return this.codUltimoContratto;
	}

	public void setUltimoContratto(String val) {
		this.codUltimoContratto = val;
	}

	public String getFlgRischioDisoccupazione() {
		return this.flgRischioDisoccupazione;
	}

	public void setFlgRischioDisoccupazione(String val) {
		this.flgRischioDisoccupazione = val;
	}

	public String getDatLicenziamento() {
		return this.datLicenziamento;
	}

	public void setDatLicenziamento(String val) {
		this.datLicenziamento = val;
	}

	public boolean insertDid(TransactionQueryExecutor txExec) throws Exception {
		SourceBean sbGenerale = DBLoad.getInfoGenerali();
		Integer numggStipulaPatto = sbGenerale.getAttribute("NUMGGSTIPULAPATTO") != null
				? new Integer(sbGenerale.getAttribute("NUMGGSTIPULAPATTO").toString())
				: 0;
		Integer numggCollOr = sbGenerale.getAttribute("NUMGGCOLLOQUIOOR") != null
				? new Integer(sbGenerale.getAttribute("NUMGGCOLLOQUIOOR").toString())
				: 0;

		String dataStipulaPatto = DateUtils.aggiungiNumeroGiorni(getDataDichiarazione(), numggStipulaPatto.intValue());
		String dataScadColl = DateUtils.aggiungiNumeroGiorni(getDataDichiarazione(), numggCollOr.intValue());

		Object[] params = new Object[] { getProgressivo(), getDataDichiarazione(), getPrgEA(), null,
				getTipoDichiarazione(), getUltimoContratto(), dataScadColl, dataStipulaPatto, null, getStatoAtto(),
				null, null, getUserSP(), getUserSP(), null, null, getFlgRischioDisoccupazione(), getDatLicenziamento(),
				null, null };
		Boolean res = (Boolean) txExec.executeQuery("INSERT_DISPO", params, "INSERT");
		return res.booleanValue();
	}

	public boolean insertDid(TransactionQueryExecutor txExec, BigDecimal cdnutins, BigDecimal cdnutmod)
			throws Exception {
		SourceBean sbGenerale = DBLoad.getInfoGenerali();
		Integer numggStipulaPatto = sbGenerale.getAttribute("NUMGGSTIPULAPATTO") != null
				? new Integer(sbGenerale.getAttribute("NUMGGSTIPULAPATTO").toString())
				: 0;
		Integer numggCollOr = sbGenerale.getAttribute("NUMGGCOLLOQUIOOR") != null
				? new Integer(sbGenerale.getAttribute("NUMGGCOLLOQUIOOR").toString())
				: 0;

		String dataStipulaPatto = DateUtils.aggiungiNumeroGiorni(getDataDichiarazione(), numggStipulaPatto.intValue());
		String dataScadColl = DateUtils.aggiungiNumeroGiorni(getDataDichiarazione(), numggCollOr.intValue());

		Object[] params = new Object[] { getProgressivo(), getDataDichiarazione(), getPrgEA(), null,
				getTipoDichiarazione(), getUltimoContratto(), dataScadColl, dataStipulaPatto, null, getStatoAtto(),
				null, null, cdnutins, cdnutmod, null, null, getFlgRischioDisoccupazione(), getDatLicenziamento(), null,
				null };
		Boolean res = (Boolean) txExec.executeQuery("INSERT_DISPO", params, "INSERT");
		return res.booleanValue();
	}

	public boolean insertDidVA18(TransactionQueryExecutor txExec, BigDecimal prgDidColl, BigDecimal prgStatoOccupaz,
			String strNote, BigDecimal cdnutins, BigDecimal cdnutmod) throws Exception {
		SourceBean sbGenerale = DBLoad.getInfoGenerali();
		Integer numggStipulaPatto = sbGenerale.getAttribute("NUMGGSTIPULAPATTO") != null
				? new Integer(sbGenerale.getAttribute("NUMGGSTIPULAPATTO").toString())
				: 0;
		Integer numggCollOr = sbGenerale.getAttribute("NUMGGCOLLOQUIOOR") != null
				? new Integer(sbGenerale.getAttribute("NUMGGCOLLOQUIOOR").toString())
				: 0;

		String dataStipulaPatto = DateUtils.aggiungiNumeroGiorni(getDataDichiarazione(), numggStipulaPatto.intValue());
		String dataScadColl = DateUtils.aggiungiNumeroGiorni(getDataDichiarazione(), numggCollOr.intValue());
		if (strNote.equals("")) {
			strNote = Properties.NOTE_DID_VA18;
		} else {
			strNote = strNote + " " + Properties.NOTE_DID_VA18;
			if (strNote.length() > Properties.LUNGHEZZA_NOTE_DID) {
				strNote = Properties.NOTE_DID_VA18;
			}
		}
		Object[] params = new Object[] { getProgressivo(), getDataDichiarazione(), getPrgEA(), prgStatoOccupaz,
				getTipoDichiarazione(), getUltimoContratto(), dataScadColl, dataStipulaPatto, null, getStatoAtto(),
				null, strNote, cdnutins, cdnutmod, null, null, prgDidColl };
		Boolean res = (Boolean) txExec.executeQuery("INSERT_DISPO_VA18", params, "INSERT");
		return res.booleanValue();
	}

	public boolean associaDocumentoIdentificazione(TransactionQueryExecutor txExec, BigDecimal prgDocColl)
			throws Exception {
		Object params[] = new Object[] { getProgressivo(), prgDocColl };
		Boolean resColl = (Boolean) txExec.executeQuery("UPDATE_COLL_IDENTITA_DA_PORTALE", params, "UPDATE");
		return resColl.booleanValue();
	}

	public DataHandler getStampaDid(TransactionQueryExecutor transExec, String codCpi, BigDecimal cdnLavoratore,
			String codiceFisc, String rptStampa, SourceBean request) {

		DataHandler dh = null;
		Documento doc = null;

		try {
			doc = new Documento();
			String currentDate = DateUtils.getNow();

			doc.setCrystalClearRelativeReportFile(rptStampa);
			doc.setCodTipoDocumento("IM");
			doc.setCodCpi(codCpi);
			doc.setChiaveTabella(getProgressivo().toString());
			doc.setCdnLavoratore(cdnLavoratore);
			doc.setPrgAzienda(null);
			doc.setPrgUnita(null);
			doc.setStrDescrizione("");
			doc.setFlgDocAmm("S");
			doc.setFlgDocIdentifP("N");
			doc.setDatInizio(getDataDichiarazione());
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
			doc.setCdnUtIns(getUserSP());
			doc.setCdnUtMod(getUserSP());
			doc.setServizioOnLine("DID");

			SourceBean rowProt = (SourceBean) transExec.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
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
			prompts.put("par_DichDisp", getProgressivo().toString());
			prompts.put("cdnLavoratore", cdnLavoratore.toString());
			prompts.put("codCpi", codCpi);
			prompts.put("numProt", numProtocollo.toString());
			prompts.put("numAnnoProt", numAnnoProt.toString());
			prompts.put("dataProt", datProtocollazione);
			prompts.put("docInOut", "Input");
			prompts.put("intestazioneStampa", intestazioneStampa);

			doc.setCrystalClearPromptFields(prompts);

			doc.insert(transExec);

			File temp = doc.getTempFile();

			dh = new DataHandler(new FileDataSource(temp));

			return dh;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean protocollaDIDVA18(TransactionQueryExecutor transExec, String codCpi, BigDecimal cdnLavoratore,
			String rptStampa, BigDecimal cdnutins, BigDecimal cdnutmod, SourceBean request) {

		Documento doc = null;

		try {
			doc = new Documento();
			String currentDate = DateUtils.getNow();

			doc.setCrystalClearRelativeReportFile(rptStampa);
			doc.setCodTipoDocumento("IM");
			doc.setCodCpi(codCpi);
			doc.setChiaveTabella(getProgressivo().toString());
			doc.setCdnLavoratore(cdnLavoratore);
			doc.setPrgAzienda(null);
			doc.setPrgUnita(null);
			doc.setStrDescrizione("");
			doc.setFlgDocAmm("S");
			doc.setFlgDocIdentifP("N");
			doc.setDatInizio(getDataDichiarazione());
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
			doc.setCdnUtIns(cdnutins);
			doc.setCdnUtMod(cdnutmod);

			SourceBean rowProt = (SourceBean) transExec.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
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

			String intestazioneStampa = (String) request.getAttribute("intestazioneStampa");
			// parametri per il report
			Map prompts = new HashMap();
			prompts.put("par_DichDisp", getProgressivo().toString());
			prompts.put("cdnLavoratore", cdnLavoratore.toString());
			prompts.put("codCpi", codCpi);
			prompts.put("numProt", numProtocollo.toString());
			prompts.put("numAnnoProt", numAnnoProt.toString());
			prompts.put("dataProt", datProtocollazione);
			prompts.put("docInOut", "Input");
			prompts.put("intestazioneStampa", intestazioneStampa);

			doc.setCrystalClearPromptFields(prompts);

			doc.insert(transExec);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public FileInputStream getStampaDidToFile(TransactionQueryExecutor transExec, String codCpi,
			BigDecimal cdnLavoratore, String codiceFisc, String rptStampa, SourceBean request) {

		Documento doc = null;

		try {
			doc = new Documento();
			String currentDate = DateUtils.getNow();

			doc.setCrystalClearRelativeReportFile(rptStampa);
			doc.setCodTipoDocumento("IM");
			doc.setCodCpi(codCpi);
			doc.setChiaveTabella(getProgressivo().toString());
			doc.setCdnLavoratore(cdnLavoratore);
			doc.setPrgAzienda(null);
			doc.setPrgUnita(null);
			doc.setStrDescrizione("");
			doc.setFlgDocAmm("S");
			doc.setFlgDocIdentifP("N");
			doc.setDatInizio(getDataDichiarazione());
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
			doc.setCdnUtIns(getUserSP());
			doc.setCdnUtMod(getUserSP());

			SourceBean rowProt = (SourceBean) transExec.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
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

			// parametri per il report
			Map prompts = new HashMap();
			prompts.put("par_DichDisp", getProgressivo().toString());
			prompts.put("codCpi", codCpi);
			prompts.put("cdnLavoratore", cdnLavoratore.toString());
			doc.setCrystalClearPromptFields(prompts);

			doc.insert(transExec);

			File temp = doc.getTempFile();

			FileInputStream dh = new FileInputStream(temp);

			return dh;
		} catch (Exception e) {
			return null;
		}
	}

	public void aggiornaInfoDid(TransactionQueryExecutor transExec, BigDecimal cdnLavoratore) throws Exception {
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
			BigDecimal tgiorniAnz = (BigDecimal) statoOcc.getAttribute("GIORNI_ANZ");
			if (tgiorniAnz == null) {
				tgiorniAnz = new BigDecimal(0);
			}
			t1 = (BigDecimal) statoOcc.getAttribute("ROW.mesi_rischio_disocc");
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

			String cat181 = Controlli.getCat181(annoNascita, getDataDichiarazione(), flgObbSco, flgLaurea);
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
			BigDecimal nMesiSosp = null;
			BigDecimal nMesiSospPrec = null;
			BigDecimal nMesiAnzPrec = null;
			BigDecimal giorniAnzResidui = null;
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
						numMesiSospFornero = new BigDecimal(0);
					}
				} else {
					numMesiSospFornero = new BigDecimal(0);
					numMesiSospFornero = new BigDecimal(0);
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
			params[14] = getProgressivo();

			Boolean res = (Boolean) transExec.executeQuery("QUERY_UPDATE_DICH_DISP_IFO_STATO_OCCUPAZ", params,
					"UPDATE");
			if (!res.booleanValue()) {
				throw new Exception("Impossibile aggiornare le info stato occupazionale");
			}
		}
	}

}
