package it.eng.sil.coop.webservices.sanatoria;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.bean.Documento;
import it.eng.sil.coop.webservices.bean.LavoratoreBean;
import it.eng.sil.coop.webservices.bean.RichiestaBeanSana;
import it.eng.sil.coop.webservices.bean.UserBean;
import it.eng.sil.coop.webservices.utils.Utils;
import it.eng.sil.module.amministrazione.CalcoloRetribuzione;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;
import it.eng.sil.util.xml.XMLValidator;

public class SanaMovimento implements SanaMovimentoInterface {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SanaMovimento.class.getName());

	private static final BigDecimal userSP = new BigDecimal("150");
	private static final int ERROR_REDDITO_SUPERIORE = 10;
	private final String SCHEMA_XSD_INPUT = "sanamov_in.xsd";

	private int codiceErrore = 0;

	public String putSanatoriaReddito(String inputXML) throws java.rmi.RemoteException, Exception {
		String outputXML = "";
		String codiceFisc = "";
		String statoOccRagg = "";
		Document doc = null;
		BigDecimal cdnlavoratore = null;
		RichiestaBeanSana checkRichiesta = null;
		LavoratoreBean lavService = null;
		TransactionQueryExecutor transExec = null;
		RequestContainer requestContainer = null;
		SessionContainer sessionContainer = null;
		SourceBean request = null;
		SourceBean response = null;
		UserBean usrSP = null;
		boolean ris = false;
		ArrayList<String> listMovimenti = null;
		ArrayList<String> redditiSanMovimenti = null;
		ArrayList<String> numKloMovimenti = null;
		HashMap<BigDecimal, BigDecimal> redditiMovimenti = null;
		BigDecimal prgMovIniziale = null;
		String dataInizioMovIniziale = "";
		String dataOdierna = DateUtils.getNow();

		_logger.info("Il servizio di sanatoria reddito e' stato chiamato");

		try {
			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + "sanatoria" + File.separator + SCHEMA_XSD_INPUT);

			String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);
			if (validityErrors != null && validityErrors.length() > 0) {
				String validityError = "Errore di validazione xml: " + validityErrors;
				_logger.error(validityError);
				_logger.warn(inputXML);
				outputXML = Utils.createXMLRisposta("99", "Errore generico");
				return outputXML;
			}

			InputStream is = new ByteArrayInputStream(inputXML.getBytes());
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			doc = documentBuilder.parse(is);
			doc.getDocumentElement().normalize();

			checkRichiesta = new RichiestaBeanSana(doc);
			outputXML = checkRichiesta.getOutputXML();
			if (outputXML != null) {
				return outputXML;
			}
			codiceFisc = checkRichiesta.getCodiceFiscale();
		}

		catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "SanaMovimento:putSanatoriaReddito", e);
			outputXML = Utils.createXMLRisposta("99", "Errore generico");
			return outputXML;
		}

		try {
			lavService = new LavoratoreBean(codiceFisc);
			outputXML = lavService.getOutputXml();
			if (outputXML != null) {
				return outputXML;
			}
			cdnlavoratore = lavService.getCdnLavoratore();
			boolean flgCompetenza = false;
			if (lavService.getCodMonoTipoCpi() != null && lavService.getCodMonoTipoCpi().equalsIgnoreCase("C")) {
				flgCompetenza = true;
			}
			if (!flgCompetenza) {
				outputXML = Utils.createXMLRisposta("10", "Lavoratore non competente");
				return outputXML;
			}

			lavService.caricaStatoOccupazionale();
			outputXML = lavService.getOutputXml();
			if (outputXML != null) {
				return outputXML;
			}
			lavService.caricaDid();
			outputXML = lavService.getOutputXml();
			if (outputXML != null) {
				return outputXML;
			}
			statoOccRagg = lavService.getStatoOccRagg();
			if (statoOccRagg == null || statoOccRagg.equalsIgnoreCase("D") || statoOccRagg.equalsIgnoreCase("I")) {
				outputXML = Utils.createXMLRisposta("06", "Lavoratore disoccupato/inoccupato");
				return outputXML;
			}
			if (lavService.getDataDid() != null) {
				outputXML = Utils.createXMLRisposta("05", "Esiste DID");
				return outputXML;
			}

			usrSP = new UserBean(userSP, cdnlavoratore);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "SanaMovimento:putSanatoriaReddito", e);
			outputXML = Utils.createXMLRisposta("99", "Errore generico");
			return outputXML;
		}

		try {
			requestContainer = new RequestContainer();
			sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", userSP);
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			sessionContainer.setAttribute(User.USERID, usrSP.getUser());
			requestContainer.setSessionContainer(sessionContainer);
			request = new SourceBean("SERVICE_REQUEST");
			response = new SourceBean("SERVICE_RESPONSE");
			request.setAttribute("FORZA_INSERIMENTO", "true");
			request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
			request.setAttribute("cdnLavoratore", cdnlavoratore.toString());
			request.setAttribute("datDichiarazione", dataOdierna);
			requestContainer.setServiceRequest(request);
			RequestContainer.setRequestContainer(requestContainer);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "SanaMovimento:putSanatoriaReddito", e);
			outputXML = Utils.createXMLRisposta("99", "Errore generico");
			return outputXML;
		}

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			// Verifica apertura del rapporto di lavoro che si vuole sanare
			// Verifica esistenza altri rapporti aperti
			listMovimenti = checkRichiesta.getMovimenti();
			redditiSanMovimenti = checkRichiesta.getRedditiMov();
			numKloMovimenti = checkRichiesta.getNumKloMov();

			String listaMov = getListaMov(listMovimenti);
			if (!listaMov.equals("")) {
				boolean isSanato = esisteSanatoria(listaMov, transExec);
				if (isSanato) {
					transExec.rollBackTransaction();
					outputXML = Utils.createXMLRisposta("11", "Sanatoria reddito già presente");
					return outputXML;
				}
				redditiMovimenti = new HashMap<BigDecimal, BigDecimal>();
				boolean movSanatoAperto = false;
				Vector movSanato = catenaAperta(listaMov, transExec);
				if (movSanato == null) {
					transExec.rollBackTransaction();
					outputXML = Utils.createXMLRisposta("99", "Errore generico");
					return outputXML;
				}
				if (movSanato.size() > 0) {
					prgMovIniziale = (BigDecimal) ((SourceBean) movSanato.get(0)).getAttribute("prgmovimento");
					dataInizioMovIniziale = (String) ((SourceBean) movSanato.get(0)).getAttribute("datInizio");
				}
				for (int i = 0; i < movSanato.size(); i++) {
					SourceBean movCurr = (SourceBean) movSanato.get(i);
					BigDecimal retribuzioneMen = (BigDecimal) movCurr.getAttribute("decretribuzionemen");
					BigDecimal prgMovCurr = (BigDecimal) movCurr.getAttribute("prgmovimento");
					if (retribuzioneMen != null) {
						redditiMovimenti.put(prgMovCurr, retribuzioneMen);
					}
					String dataFine = movCurr.getAttribute("datFine") != null
							? movCurr.getAttribute("datFine").toString()
							: "";
					if (dataFine.equals("") || DateUtils.compare(dataFine, dataOdierna) >= 0) {
						movSanatoAperto = true;
					}
				}
				if (!movSanatoAperto) {
					transExec.rollBackTransaction();
					outputXML = Utils.createXMLRisposta("07", "Rapporto lavorativo non in essere");
					return outputXML;
				}
				int nAperto = altraCatenaAperta(cdnlavoratore, listaMov, transExec);
				if (nAperto > 0) {
					transExec.rollBackTransaction();
					outputXML = Utils.createXMLRisposta("08", "Più posizioni lavorative aperte");
					return outputXML;
				}
			} else {
				transExec.rollBackTransaction();
				outputXML = Utils.createXMLRisposta("99", "Errore generico");
				return outputXML;
			}

			BigDecimal prgDichiarazione = creaDichiarazioneLavoratore(cdnlavoratore, prgMovIniziale, userSP, transExec);
			// Per ogni movimento della catena da sanare
			BigDecimal prgMovCurr = null;
			BigDecimal retribuzione = null;
			BigDecimal numKlo = null;
			for (int i = 0; i < listMovimenti.size(); i++) {

				BigDecimal retribSanata = null;
				String currPrg = listMovimenti.get(i);
				String currNumKloMov = numKloMovimenti.get(i);
				String retribuzioneSanCurr = redditiSanMovimenti.get(i);
				prgMovCurr = new BigDecimal(currPrg);
				numKlo = new BigDecimal(currNumKloMov);
				if (retribuzioneSanCurr != null) {
					retribSanata = new BigDecimal(retribuzioneSanCurr);
				}

				// Decreto 05/11/2019
				// TODO 18/11/2019 AGGIUNGERE CALCOLO RETRIBUZIONE DECRETO 5 NOVEMBRE
				/*
				 * checkVerificaCompensoSanato(prgMovCurr, retribSanata); if(getCodiceErrore() != 0) {
				 * transExec.rollBackTransaction(); outputXML = Utils.createXMLRisposta("99", "Errore generico");
				 * it.eng.sil.util.TraceWrapper.error( _logger,
				 * "SanaMovimento:putSanatoriaReddito: Errore nel calcolo retribuzione " + getCodiceErrore(), new
				 * Exception()); return outputXML; }
				 */
				////////////////////

				ris = aggiornaMovInfoSanare(prgDichiarazione, prgMovCurr, retribSanata, userSP, numKlo, transExec);
				if (!ris) {
					transExec.rollBackTransaction();
					outputXML = Utils.createXMLRisposta("99", "Errore generico");
					return outputXML;
				}
				retribuzione = redditiMovimenti.get(prgMovCurr);
				ris = creaDichiarazioneLavDettaglio(prgDichiarazione, prgMovCurr, retribuzione, retribSanata, userSP,
						transExec);
				if (!ris) {
					transExec.rollBackTransaction();
					outputXML = Utils.createXMLRisposta("99", "Errore generico");
					return outputXML;
				}
			}

			// Verifica se c'è comunque superamento reddito
			boolean checkControllo = lavService.isDidStipulabile(dataOdierna, userSP, requestContainer, request,
					response, transExec);
			if (!checkControllo) {
				outputXML = lavService.getOutputXml();
				if (outputXML != null) {
					if (lavService.getCodiceErrore() == ERROR_REDDITO_SUPERIORE) {
						transExec.rollBackTransaction();
						outputXML = Utils.createXMLRisposta("12", "Superamento reddito");
						return outputXML;
					}
				}
			}

			// Ricalcolo Stato Occupazionale
			lavService.calcolaStatoOccupazionale(transExec, dataInizioMovIniziale);

			// Protocolla dichiarazione
			boolean protocolla = protocollaDichiarazione(transExec, lavService, prgDichiarazione, request);
			if (!protocolla) {
				transExec.rollBackTransaction();
				outputXML = Utils.createXMLRisposta("09", "Errore protocollazione");
				return outputXML;

			}
			transExec.commitTransaction();
			outputXML = Utils.createXMLRisposta("00", "OK");
			return outputXML;
		}

		catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "SanaMovimento:putSanatoriaReddito", e);
			outputXML = Utils.createXMLRisposta("99", "Errore generico");
			return outputXML;
		}
	}

	private boolean aggiornaMovInfoSanare(BigDecimal prgDich, BigDecimal prgMov, BigDecimal retribuzioneSanata,
			BigDecimal cdnUtente, BigDecimal numKlo, TransactionQueryExecutor transExec) throws Exception {
		String dataSitSanata = DateUtils.getNow();
		String codTipoDich = "DDRN";
		numKlo = numKlo.add(new BigDecimal(1));
		Object params[] = new Object[8];
		params[0] = prgDich;
		params[1] = retribuzioneSanata;
		params[2] = dataSitSanata;
		params[3] = codTipoDich;
		params[4] = numKlo;
		params[5] = cdnUtente;
		params[6] = null;
		params[7] = prgMov;

		Boolean ret = (Boolean) transExec.executeQuery("UPDATE_MOVIMENTO_PER_SANARE_PERIODO_PREC", params, "UPDATE");
		return ret.booleanValue();
	}

	private BigDecimal creaDichiarazioneLavoratore(BigDecimal cdnLavoratore, BigDecimal prgMovIniziale,
			BigDecimal cdnUtente, TransactionQueryExecutor transExec) throws Exception {
		BigDecimal prg = null;
		String dataSitSanata = DateUtils.getNow();
		String codTipoDich = "DDRN";
		SourceBean row = null;
		row = (SourceBean) transExec.executeQuery("AM_DICH_LAV_NEXTVAL", new Object[0], "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere il progressivo per la tabella am_dich_lav");
		prg = (BigDecimal) row.getAttribute("ROW.PRGDICHLAV");
		Object[] params = new Object[8];
		params[0] = prg;
		params[1] = cdnLavoratore;
		params[2] = dataSitSanata;
		params[3] = prgMovIniziale;
		params[4] = null;
		params[5] = cdnUtente;
		params[6] = cdnUtente;
		params[7] = codTipoDich;
		Boolean res = (Boolean) transExec.executeQuery("INS_DICH_LAV", params, "INSERT");
		if (!res.booleanValue()) {
			return null;
		}
		return prg;
	}

	private boolean creaDichiarazioneLavDettaglio(BigDecimal prgDich, BigDecimal prgMov, BigDecimal retribuzione,
			BigDecimal retribSanata, BigDecimal cdnUtente, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[6];
		params[0] = prgDich;
		params[1] = prgMov;
		params[2] = retribuzione;
		params[3] = cdnUtente;
		params[4] = cdnUtente;
		params[5] = retribSanata;
		Boolean res = (Boolean) transExec.executeQuery("INS_DICH_LAV_DETTAGLIO", params, "INSERT");
		return res.booleanValue();
	}

	private Vector catenaAperta(String lista, TransactionQueryExecutor transExec) throws Exception {
		String selectquery = "select mov.prgmovimento, to_char(mov.datiniziomov, 'dd/mm/yyyy') datInizio, "
				+ "to_char(mov.datfinemoveffettiva, 'dd/mm/yyyy') datFine, mov.decretribuzionemen "
				+ "from am_movimento mov where mov.prgmovimento in (" + lista + ") and mov.codstatoatto = 'PR' "
				+ "and mov.codTipoMov <> 'CES' order by mov.datiniziomov asc, mov.dtmins asc";
		SourceBean row = ProcessorsUtils.executeSelectQuery(selectquery, transExec);
		if (row != null)
			return row.getAttributeAsVector("ROW");
		else
			return null;
	}

	private boolean esisteSanatoria(String lista, TransactionQueryExecutor transExec) throws Exception {
		String selectQueryDich = "select dich.prgdichlav from am_dich_lav dich, am_dich_lav_dettaglio dett "
				+ " where dich.prgdichlav = dett.prgdichlav and dich.codstatoatto = 'PR' and dett.prgmovimento in ("
				+ lista + ")";
		SourceBean rowDich = ProcessorsUtils.executeSelectQuery(selectQueryDich, transExec);
		if (rowDich != null && rowDich.getAttributeAsVector("ROW").size() > 0) {
			return true;
		}
		return false;
	}

	private int altraCatenaAperta(BigDecimal cdnlav, String lista, TransactionQueryExecutor transExec)
			throws Exception {
		int nRit = 0;
		String selectquery = "select count(*) num " + "from am_movimento mov " + "where mov.cdnlavoratore = " + cdnlav
				+ " and mov.prgmovimento not in (" + lista + ") and mov.codstatoatto = 'PR' "
				+ "and mov.codTipoMov <> 'CES' and trunc(nvl(mov.datfinemoveffettiva, sysdate)) >= trunc(sysdate)";
		SourceBean row = ProcessorsUtils.executeSelectQuery(selectquery, transExec);
		if (row != null) {
			row = (row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row);
			nRit = row.getAttribute("num") != null ? new Integer(row.getAttribute("num").toString()).intValue() : 0;
		}
		return nRit;
	}

	private String getListaMov(ArrayList<String> listMovimenti) {
		String listaMov = "";
		int nSize = listMovimenti.size();
		for (int i = 0; i < nSize; i++) {
			String currPrg = listMovimenti.get(i);
			if (listaMov.equals("")) {
				listaMov = currPrg;
			} else {
				listaMov = listaMov + "," + currPrg;
			}
		}
		return listaMov;
	}

	private boolean protocollaDichiarazione(TransactionQueryExecutor transExec, LavoratoreBean lavService,
			BigDecimal prgDichiarazione, SourceBean request) throws Exception {

		Documento doc = new Documento();

		SourceBean rowProt = (SourceBean) transExec.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
		if (rowProt == null) {
			throw new Exception("impossibile protocollare il documento di identificazione");
		}
		rowProt = (rowProt.containsAttribute("ROW") ? (SourceBean) rowProt.getAttribute("ROW") : rowProt);
		BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
		BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
		String datProtocollazione = (String) rowProt.getAttribute("DATAORAPROT");

		doc.setCdnLavoratore(lavService.getCdnLavoratore());
		doc.setNumAnnoProt(numAnnoProt);
		doc.setNumProtocollo(numProtocollo);
		doc.setDatProtocollazione(datProtocollazione);
		doc.setTipoProt("S");
		doc.setFlgDocAmm("S");
		doc.setCodMonoIO("I");
		doc.setCodCpi(lavService.getCodCpi());
		doc.setCodTipoDocumento("DR02");
		doc.setChiaveTabella(prgDichiarazione.toString());
		doc.setCdnUtIns(userSP);
		doc.setCdnUtMod(userSP);
		doc.setDatAcqril(DateUtils.getNow());
		doc.setCdnComponente(new BigDecimal("373"));
		doc.setCodStatoAtto("PR");
		doc.setPagina("DichRedDettaglioPage");

		request.setAttribute("numProt", numProtocollo.toString());
		request.setAttribute("annoProt", numAnnoProt.toString());
		request.setAttribute("dataProt", datProtocollazione);

		try {
			doc.insert(transExec);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void checkVerificaCompensoSanato(BigDecimal prg, BigDecimal retribuzioneSanata) throws Exception {

		SourceBean recuperaMovimentoBean = null;
		CalcoloRetribuzione calcRet = null;
		String codorario = null;
		String codccnl = null;
		String numOreSett = null;
		String numLivello = null;

		// Decreto 05/11/2019
		if (getCodiceErrore() == 0) {

			recuperaMovimentoBean = MovimentoBean.recuperaMovimento(prg);
			SourceBean response = new SourceBean("SERVICE_RESPONSE");
			if (recuperaMovimentoBean != null) { // se il prgmovimento è presente

				codorario = recuperaMovimentoBean.containsAttribute("ROW.CODORARIO")
						? recuperaMovimentoBean.getAttribute("ROW.CODORARIO").toString()
						: null;
				codccnl = recuperaMovimentoBean.containsAttribute("ROW.CODCCNL")
						? (recuperaMovimentoBean.getAttribute("ROW.CODCCNL").toString())
						: null;

				numOreSett = recuperaMovimentoBean.containsAttribute("ROW.NUMORESETT")
						? (recuperaMovimentoBean.getAttribute("ROW.NUMORESETT").toString())
						: null;
				numLivello = recuperaMovimentoBean.containsAttribute("ROW.NUMLIVELLO")
						? (recuperaMovimentoBean.getAttribute("ROW.NUMLIVELLO").toString())
						: null;

				calcRet = new CalcoloRetribuzione(codorario, numOreSett, codccnl, numLivello);

				if (calcRet.checkCalcoloRetribuzione()) { // se true trattasi di nuovo codiceccnl - fare il calcolo e
															// verificare
					calcRet.calcoloCompensoRetribuzione(response);

					String esito = null;
					String retribuzioneCalcolata = null;
					BigDecimal retribuzioneNumber = null;

					if (response.getAttribute("ESITO") != null) {
						esito = response.getAttribute("ESITO").toString();

						if (esito.equalsIgnoreCase("KO")) {

							this.codiceErrore = MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSO_RETRIBUZIONE_SANATORIA;
							// this.descErrore = dataInizioMov;

						} else { // se OK
							if (response.getAttribute("RETRIBUZIONE") != null
									&& !(response.getAttribute("RETRIBUZIONE").toString()).isEmpty()) {
								retribuzioneCalcolata = response.getAttribute("RETRIBUZIONE").toString();
								retribuzioneNumber = BigDecimal.valueOf(new Double(retribuzioneCalcolata));

								if (retribuzioneSanata == null || (retribuzioneSanata != null
										&& (retribuzioneSanata.multiply(new BigDecimal("12")))
												.compareTo(retribuzioneNumber) < 0)) {

									this.codiceErrore = MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSO_RETRIBUZIONE_SANATORIA_CALCOLO;
									// this.descErrore = dataInizioMov;
								}
							}
						}
					} else {

						this.codiceErrore = MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSO_RETRIBUZIONE_SANATORIA;
						// this.descErrore = dataInizioMov;
					}
				}

			}
		}
	}

	public int getCodiceErrore() {
		return codiceErrore;
	}

	public void setCodiceErrore(int codiceErrore) {
		this.codiceErrore = codiceErrore;
	}

}
