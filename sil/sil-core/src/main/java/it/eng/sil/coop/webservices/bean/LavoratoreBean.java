package it.eng.sil.coop.webservices.bean;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.bean.Documento;
import it.eng.sil.coop.webservices.agenda.appuntamento.Constants;
import it.eng.sil.coop.webservices.agenda.appuntamento.EsitoInserimentoLavoratore;
import it.eng.sil.coop.webservices.agenda.appuntamento.LavoratoreUtils;
import it.eng.sil.coop.webservices.did.xml.DatiDid.Lavoratore;
import it.eng.sil.coop.webservices.utils.Utils;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.DIDManager;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativa;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;

public class LavoratoreBean {
	private String codCpi = null;
	private String codMonoTipoCpi = null;
	private BigDecimal cdnlavoratore = null;
	private String codiceFiscale = null;
	private String dataDid = null;
	private String codStatoOccRagg = null;
	private String outputXML = null;
	private TransactionQueryExecutor transExec = null;
	private int codiceErrore = 0;

	public LavoratoreBean(String codFisc, String dataRif) throws Exception {
		this.codiceFiscale = codFisc.toUpperCase();
		this.dataDid = dataRif;
		caricaLavoratore();
	}

	public LavoratoreBean(String codFisc, String dataRif, TransactionQueryExecutor txExec) throws Exception {
		this.codiceFiscale = codFisc.toUpperCase();
		this.dataDid = dataRif;
		this.transExec = txExec;
		caricaLavoratoreTransazione();
	}

	public LavoratoreBean(String codFisc) throws Exception {
		this.codiceFiscale = codFisc.toUpperCase();
		caricaLavoratore();
	}

	public LavoratoreBean(BigDecimal cdnLav, String dataRif) throws Exception {
		this.cdnlavoratore = cdnLav;
		this.dataDid = dataRif;
		caricaLavoratoreByCdn();
	}

	public LavoratoreBean(String codFisc, String dataRif, Lavoratore lav, TransactionQueryExecutor txExec)
			throws Exception {
		this.codiceFiscale = codFisc.toUpperCase();
		this.dataDid = dataRif;
		this.transExec = txExec;
		caricaLavoratore(lav);
	}

	private void caricaLavoratoreTransazione() throws Exception {
		Object[] params = new Object[] { getCodiceFiscale() };
		SourceBean row = (SourceBean) transExec.executeQuery("SELECT_AN_LAVORATORE", params, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			cdnlavoratore = (BigDecimal) row.getAttribute("cdnLavoratore");
			if (cdnlavoratore == null) {
				setCodiceErrore(3);
				setOutputXml(Utils.createXMLRisposta("03", "Codice fiscale non trovato"));
			} else {
				setCdnLavoratore(cdnlavoratore);
				String dataCalcolo = getDataDid();
				if (dataCalcolo == null) {
					dataCalcolo = DateUtils.getNow();
				}
				Object[] inputParametersCpi = new Object[3];
				inputParametersCpi[0] = getCdnLavoratore();
				inputParametersCpi[1] = dataCalcolo;
				inputParametersCpi[2] = dataCalcolo;

				SourceBean cpiLav = (SourceBean) transExec.executeQuery("GET_CPI_AN_LAVORATORE_COMPETENTE_DATA",
						inputParametersCpi, "SELECT");
				String codCpiLav = (String) cpiLav.getAttribute("ROW.CPICOMP");
				String codTipoCpi = (String) cpiLav.getAttribute("ROW.codmonotipocpi");
				if (codCpiLav == null) {
					setCodiceErrore(99);
					setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
				} else {
					setCodCpi(codCpiLav);
					if (codTipoCpi != null) {
						setCodMonoTipoCpi(codTipoCpi);
					}
				}
			}
		} else {
			setCodiceErrore(99);
			setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
		}
	}

	private void caricaLavoratore() throws Exception {
		Object[] params = new Object[] { getCodiceFiscale() };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			cdnlavoratore = (BigDecimal) row.getAttribute("cdnLavoratore");
			if (cdnlavoratore == null) {
				setCodiceErrore(3);
				setOutputXml(Utils.createXMLRisposta("03", "Codice fiscale non trovato"));
			} else {
				setCdnLavoratore(cdnlavoratore);
				String dataCalcolo = getDataDid();
				if (dataCalcolo == null) {
					dataCalcolo = DateUtils.getNow();
				}
				Object[] inputParametersCpi = new Object[3];
				inputParametersCpi[0] = getCdnLavoratore();
				inputParametersCpi[1] = dataCalcolo;
				inputParametersCpi[2] = dataCalcolo;

				SourceBean cpiLav = (SourceBean) QueryExecutor.executeQuery("GET_CPI_AN_LAVORATORE_COMPETENTE_DATA",
						inputParametersCpi, "SELECT", Values.DB_SIL_DATI);
				String codCpiLav = (String) cpiLav.getAttribute("ROW.CPICOMP");
				String codTipoCpi = (String) cpiLav.getAttribute("ROW.codmonotipocpi");
				if (codCpiLav == null) {
					setCodiceErrore(99);
					setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
				} else {
					setCodCpi(codCpiLav);
					if (codTipoCpi != null) {
						setCodMonoTipoCpi(codTipoCpi);
					}
				}
			}
		} else {
			setCodiceErrore(99);
			setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
		}
	}

	private void caricaLavoratoreByCdn() throws Exception {
		setCdnLavoratore(cdnlavoratore);
		String dataCalcolo = getDataDid();
		if (dataCalcolo == null) {
			dataCalcolo = DateUtils.getNow();
		}
		Object[] inputParametersCpi = new Object[1];
		inputParametersCpi[0] = getCdnLavoratore();

		SourceBean cpiLav = (SourceBean) QueryExecutor.executeQuery("GET_CPI_AN_LAVORATORE_COMPETENTE_VA18",
				inputParametersCpi, "SELECT", Values.DB_SIL_DATI);
		String codCpiLav = (String) cpiLav.getAttribute("ROW.CPICOMP");
		String codTipoCpi = (String) cpiLav.getAttribute("ROW.codmonotipocpi");

		if (codCpiLav != null) {
			setCodCpi(codCpiLav);
			if (codTipoCpi != null) {
				setCodMonoTipoCpi(codTipoCpi);
			}
		}
	}

	private void caricaLavoratore(Lavoratore lav) throws Exception {
		Object[] params = new Object[] { getCodiceFiscale() };
		SourceBean row = (SourceBean) transExec.executeQuery("SELECT_AN_LAVORATORE", params, "SELECT");
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			cdnlavoratore = (BigDecimal) row.getAttribute("cdnLavoratore");
			if (cdnlavoratore == null) {
				String codCpiDomicilio = "";
				String codCpiCapoluogo = "";
				String codTipoCpi = "";
				String codProvinciaDomicilio = "";
				String codProvinciaSil = "";
				String codCpiTit = "";
				String codCpiOrig = null;

				SourceBean rowCpi = LavoratoreUtils.getCpiProvinciaComune(transExec, lav.getDomicilio().getComune());

				if (rowCpi != null) {
					rowCpi = rowCpi.containsAttribute("ROW") ? (SourceBean) rowCpi.getAttribute("ROW") : rowCpi;
					codCpiDomicilio = SourceBeanUtils.getAttrStr(rowCpi, "CODCPI");
					codProvinciaDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODPROVINCIA");
					codProvinciaSil = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODPROVINCIASIL");
					codCpiCapoluogo = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGO");
					if (codProvinciaDomicilio.equalsIgnoreCase(codProvinciaSil)) {
						codTipoCpi = Properties.CODMONOTIPOCPI_COMP;
						codCpiTit = codCpiDomicilio;
					} else {
						codTipoCpi = Properties.CODMONOTIPOCPI_TIT;
						codCpiTit = codCpiCapoluogo;
						codCpiOrig = codCpiDomicilio;
					}

					// inserimento nuovo lavoratore
					EsitoInserimentoLavoratore esitoInserimentoLavoratore = LavoratoreUtils
							.inserisciLavoratoreStipulaDid(transExec, Constants.UTENTE_PORTALE, codCpiTit, codCpiOrig,
									codTipoCpi, getCodiceFiscale(), lav.getNome(), lav.getCognome(),
									DateUtils.formatXMLGregorian(lav.getDataNascita()), lav.getComuneNascita(),
									lav.getSesso().value(), lav.getCittadinanza(), lav.getDomicilio().getIndirizzo(),
									lav.getDomicilio().getComune(), lav.getResidenza().getIndirizzo(),
									lav.getResidenza().getComune(), lav.getCellulare(), lav.getEmail(), null);

					if (esitoInserimentoLavoratore == null || !esitoInserimentoLavoratore.isSuccess()) {
						setCodiceErrore(99);
						setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
					} else {
						setCdnLavoratore(esitoInserimentoLavoratore.getCdnLavoratore());
						setCodCpi(codCpiTit);
						setCodMonoTipoCpi(codTipoCpi);
					}
				} else {
					setCodiceErrore(99);
					setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
				}
			} else {
				BigDecimal numklolav = (BigDecimal) row.getAttribute("NUMKLOLAVORATORE");
				String strCellulare = row.containsAttribute("STRCELL") ? row.getAttribute("STRCELL").toString() : "";
				String strEmail = row.containsAttribute("STREMAIL") ? row.getAttribute("STREMAIL").toString() : "";
				if (!strCellulare.equals("")) {
					strCellulare = strCellulare.trim();
				}
				if (!strEmail.equals("")) {
					strEmail = strEmail.trim();
				}
				boolean aggiornaCell = false;
				boolean aggiornaEmail = false;
				String newStrCellulare = !strCellulare.equals("") ? strCellulare : null;
				String newStrEmail = !strEmail.equals("") ? strEmail : null;
				boolean successContatti = true;

				if (strCellulare.equals("") && lav.getCellulare() != null && !lav.getCellulare().equals("")) {
					aggiornaCell = true;
					newStrCellulare = lav.getCellulare();
				}
				if (strEmail.equals("") && lav.getEmail() != null && !lav.getEmail().equals("")) {
					aggiornaEmail = true;
					newStrEmail = lav.getEmail();
				}

				if (aggiornaCell || aggiornaEmail) {
					numklolav = numklolav.add(new BigDecimal("1"));
					successContatti = LavoratoreUtils.aggiornaContattiLavoratore(transExec, cdnlavoratore,
							newStrCellulare, newStrEmail, numklolav, Constants.UTENTE_PORTALE);
				}

				if (successContatti) {
					setCdnLavoratore(cdnlavoratore);
					String dataCalcolo = getDataDid();
					if (dataCalcolo == null) {
						dataCalcolo = DateUtils.getNow();
					}
					Object[] inputParametersCpi = new Object[3];
					inputParametersCpi[0] = getCdnLavoratore();
					inputParametersCpi[1] = dataCalcolo;
					inputParametersCpi[2] = dataCalcolo;

					SourceBean cpiLav = (SourceBean) transExec.executeQuery("GET_CPI_AN_LAVORATORE_COMPETENTE_DATA",
							inputParametersCpi, "SELECT");
					String codCpiLav = (String) cpiLav.getAttribute("ROW.CPICOMP");
					String codTipoCpi = (String) cpiLav.getAttribute("ROW.codmonotipocpi");
					if (codCpiLav == null) {
						setCodiceErrore(99);
						setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
					} else {
						setCodCpi(codCpiLav);
						if (codTipoCpi != null) {
							setCodMonoTipoCpi(codTipoCpi);
						}
					}
				} else {
					setCodiceErrore(99);
					setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
				}
			}
		} else {
			setCodiceErrore(99);
			setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
		}
	}

	public void caricaStatoOccupazionale() throws Exception {
		String codStatoOccRagg = null;
		Object[] params = new Object[] { getCdnLavoratore() };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("COOP_GET_STATO_OCC_APERTO", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row == null) {
			setCodiceErrore(99);
			setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
		} else {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			codStatoOccRagg = (String) row.getAttribute("codstatooccupazragg");
			if (codStatoOccRagg != null) {
				setStatoOccRagg(codStatoOccRagg);
			}
		}
	}

	public void caricaDid() throws Exception {
		Object[] params = new Object[] { getCdnLavoratore() };
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("COOP_GET_DID_APERTA", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			setDataDid((String) row.getAttribute("datdichiarazione"));
		} else {
			setCodiceErrore(99);
			setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
		}
	}

	public boolean isDidStipulabile(String dataDichiarazione, BigDecimal userSP, RequestContainer reqCont,
			SourceBean request, SourceBean response, TransactionQueryExecutor transExec) throws Exception {
		boolean checkControllo = false;
		SituazioneAmministrativa sitAmm = null;
		try {
			Vector statiOccupazionali = DBLoad.getStatiOccupazionali(getCdnLavoratore(), transExec);
			Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(getCdnLavoratore(), dataDichiarazione,
					transExec);
			Vector patti = DBLoad.getPattiStoricizzati(getCdnLavoratore(), dataDichiarazione, transExec);
			Vector movimentiAperti = DBLoad.getMovimentiLavoratore(getCdnLavoratore(), transExec);
			movimentiAperti = Controlli.togliMovNonProtocollati(movimentiAperti);
			movimentiAperti = Controlli.togliMovimentoInDataFutura(movimentiAperti);

			sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti, dids, dataDichiarazione,
					transExec, reqCont);

			checkControllo = DIDManager.inserimentoDID(request, reqCont, response, sitAmm, transExec);
			return checkControllo;
		}

		catch (ControlliException ex) {
			int codiceErrore = ex.getCode();
			switch (codiceErrore) {
			case MessageCodes.DID.LAVORATORE_GIA_DISOCCUPATO:
				setCodiceErrore(9);
				setOutputXml(Utils.createXMLRisposta("09", "Stato occupazionale non compatibile"));
				break;
			case MessageCodes.DID.DATA_DID_PRECEDENTE_AL_30012003:
				setCodiceErrore(7);
				setOutputXml(
						Utils.createXMLRisposta("07", "Data dichiarazione precedente al " + sitAmm.getDataPrec297()));
				break;
			case MessageCodes.DID.ESISTE_PERIODO_MOBILITA:
				setCodiceErrore(9);
				setOutputXml(Utils.createXMLRisposta("09", "Stato occupazionale non compatibile"));
				break;
			case MessageCodes.DID.REDDITO_SUPERIORE_LIMITE:
				setCodiceErrore(10);
				setOutputXml(Utils.createXMLRisposta("10", "Superamento reddito"));
				break;
			case MessageCodes.DID.TERMINI_DID_NON_SCADUTI:
				setCodiceErrore(11);
				setOutputXml(Utils.createXMLRisposta("11", "Decorrenza termini"));
				break;
			case MessageCodes.DID.ERR_MOV_RISCHIO_DISOCCUPAZIONE:
				// situazione gestita solo nella stipula did online(ws StipulaDid)
				setCodiceErrore(20);
				setOutputXml(Utils.createXMLRisposta("20",
						"DID non valida. E' in corso un altro rapporto di lavoro, rivolgersi al centro per l'impiego"));
				break;
			default:
				setCodiceErrore(99);
				setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
				break;
			}
			return false;
		}
	}

	public BigDecimal getDocumentoVP(TransactionQueryExecutor txExec) throws Exception {
		BigDecimal prgDoc = null;
		Object[] params = new Object[] { getCdnLavoratore() };
		SourceBean row = (SourceBean) txExec.executeQuery("GET_DOCUMENTO_IDENTITA_DA_PORTALE", params, "SELECT");
		if (row == null) {
			setCodiceErrore(99);
			setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
		} else {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgDoc = (BigDecimal) row.getAttribute("prgdocumento");
		}
		return prgDoc;
	}

	public BigDecimal getAutorizzazionePrivacy(TransactionQueryExecutor txExec) throws Exception {
		BigDecimal prgPrivacy = null;
		Object[] params = new Object[] { getCdnLavoratore() };
		SourceBean row = (SourceBean) txExec.executeQuery("AUTORIZZAZIONE_PRIVACY", params, "SELECT");
		if (row == null) {
			setCodiceErrore(99);
			setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
		} else {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgPrivacy = (BigDecimal) row.getAttribute("prgprivacy");
		}
		return prgPrivacy;
	}

	public BigDecimal getElencoAnagrafico(TransactionQueryExecutor txExec) throws Exception {
		BigDecimal prgElencoAnag = null;
		Object[] params = new Object[] { getCdnLavoratore() };
		SourceBean row = (SourceBean) txExec.executeQuery("GET_ULTIMOELANAG_DA_CDNLAV", params, "SELECT");
		if (row == null) {
			setCodiceErrore(99);
			setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
		} else {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgElencoAnag = (BigDecimal) row.getAttribute("prgelencoanagrafico");
		}
		return prgElencoAnag;
	}

	public BigDecimal getDidApertaPA(TransactionQueryExecutor txExec, BigDecimal prgEA) throws Exception {
		BigDecimal prgDid = null;
		Object[] params = new Object[] { prgEA };
		SourceBean row = (SourceBean) txExec.executeQuery("GET_DID_APERTA_DA_PORTALE", params, "SELECT");
		if (row == null) {
			setCodiceErrore(99);
			setOutputXml(Utils.createXMLRisposta("99", "Errore generico"));
		} else {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			prgDid = (BigDecimal) row.getAttribute("prgdichdisponibilita");
		}
		return prgDid;
	}

	public boolean insertElencoAnagrafico(TransactionQueryExecutor txExec, BigDecimal userSP) throws Exception {
		String dataRichiesta = DateUtils.getNow();
		Object[] params = new Object[] { getCdnLavoratore(), dataRichiesta, null, null, null, userSP, userSP };
		Boolean resEA = (Boolean) txExec.executeQuery("INSERT_ELANAG", params, "INSERT");
		return resEA.booleanValue();
	}

	public boolean insertPrivacy(TransactionQueryExecutor txExec, BigDecimal userSP) throws Exception {
		String dataRichiesta = DateUtils.getNow();
		Object[] params = new Object[] { getCdnLavoratore(), dataRichiesta, null, "S", userSP, userSP };
		Boolean resPrivacy = (Boolean) txExec.executeQuery("INSERT_PRIVACY", params, "INSERT");
		return resPrivacy.booleanValue();
	}

	public boolean insertDocumentoIdentificazione(TransactionQueryExecutor txExec, BigDecimal userSP) {
		try {
			String dataRichiesta = DateUtils.getNow();
			Documento docIdentita = new Documento();
			docIdentita.setPrgDocumento(null);
			docIdentita.setCodCpi(getCodCpi());
			docIdentita.setCdnLavoratore(getCdnLavoratore());
			docIdentita.setCodTipoDocumento("VP");
			docIdentita.setFlgAutocertificazione("N");
			docIdentita.setFlgDocIdentifP("N");
			docIdentita.setDatInizio(dataRichiesta);
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
			docIdentita.setCdnUtMod(userSP);
			docIdentita.setCdnUtIns(userSP);

			docIdentita.insert(txExec);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public BigDecimal getDocumentCollDocIdentificazione(TransactionQueryExecutor txExec) throws Exception {
		BigDecimal prgDocColl = null;
		Object[] params = new Object[] { getCdnLavoratore() };
		SourceBean row = (SourceBean) txExec.executeQuery("SELECT_COLL_IDENTITA_DA_PORTALE", params, "SELECT");
		row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
		if (row != null && row.containsAttribute("PRGDOCUMENTOCOLL")) {
			prgDocColl = (BigDecimal) row.getAttribute("PRGDOCUMENTOCOLL");
		}
		return prgDocColl;
	}

	public void calcolaStatoOccupazionale(TransactionQueryExecutor txExec, String dataCalcolo) throws Exception {
		SituazioneAmministrativaFactory.newInstance(getCdnLavoratore().toString(), dataCalcolo, txExec)
				.calcolaImpatti();
	}

	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	public void setCodiceFiscale(String val) {
		this.codiceFiscale = val;
	}

	public String getCodCpi() {
		return this.codCpi;
	}

	public void setCodCpi(String val) {
		this.codCpi = val;
	}

	public String getOutputXml() {
		return this.outputXML;
	}

	public void setOutputXml(String outputRis) {
		this.outputXML = outputRis;
	}

	public String getStatoOccRagg() {
		return this.codStatoOccRagg;
	}

	public void setStatoOccRagg(String sOccRagg) {
		this.codStatoOccRagg = sOccRagg;
	}

	public BigDecimal getCdnLavoratore() {
		return this.cdnlavoratore;
	}

	public void setCdnLavoratore(BigDecimal val) {
		this.cdnlavoratore = val;
	}

	public String getCodMonoTipoCpi() {
		return this.codMonoTipoCpi;
	}

	public void setCodMonoTipoCpi(String val) {
		this.codMonoTipoCpi = val;
	}

	public String getDataDid() {
		return this.dataDid;
	}

	public void setDataDid(String val) {
		this.dataDid = val;
	}

	public int getCodiceErrore() {
		return codiceErrore;
	}

	public void setCodiceErrore(int val) {
		this.codiceErrore = val;
	}

}
