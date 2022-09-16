package it.eng.sil.rdc.servizi;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.agenda.appuntamento.LavoratoreUtils;
import it.eng.sil.coop.webservices.rdc.RichiestaRDCInviaNotifica;
import it.eng.sil.coordinamento.wsClient.np.RispostaXML;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.blen.StringUtils;

public class RDCBean {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RDCBean.class.getName());

	public static final String COD_NULL_DEFAULT = new String("NT");

	private static final BigDecimal userSP = new BigDecimal("190");

	public RispostaXML insericiAmRdc(BigDecimal prgAmRdc, RichiestaRDCInviaNotifica richiestaInvioRDC,
			MultipleTransactionQueryExecutor trans) {

		RispostaXML rispostaXML = new RispostaXML("", "", "");

		try {

			String codCittadinanza = StringUtils.isFilledNoBlank(richiestaInvioRDC.getInvioRDC().getCodCittadinanza())
					? richiestaInvioRDC.getInvioRDC().getCodCittadinanza()
					: COD_NULL_DEFAULT;
			String codComuneNascita = getCodiceComune(richiestaInvioRDC.getInvioRDC().getCodComuneNascita());

			trans.initTransaction();

			trans.executeQuery("INSERT_AMRDC", new Object[] { prgAmRdc, richiestaInvioRDC.getInvioRDC().getCodSesso(),
					richiestaInvioRDC.getInvioRDC().getCodCapResidenza(),
					richiestaInvioRDC.getInvioRDC().getDesCognome(),
					richiestaInvioRDC.getInvioRDC().getCodComuneResidenza(),
					richiestaInvioRDC.getInvioRDC().getDesNome(), richiestaInvioRDC.getInvioRDC().getCodFiscale(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttRendicontazione()),
					richiestaInvioRDC.getInvioRDC().getCodRuoloBeneficiario(),
					richiestaInvioRDC.getInvioRDC().getCodFiscaleRichiedente(), codComuneNascita,
					richiestaInvioRDC.getInvioRDC().getDesIndirizzoDomicilio(),
					richiestaInvioRDC.getInvioRDC().getCodCapDomicilio(), richiestaInvioRDC.getInvioRDC().getDesEmail(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttDomanda()),
					richiestaInvioRDC.getInvioRDC().getDesTelefono(), richiestaInvioRDC.getInvioRDC().getCodCpi(),
					richiestaInvioRDC.getInvioRDC().getCodProtocolloInps(), codCittadinanza,
					richiestaInvioRDC.getInvioRDC().getCodSap(),
					formatDate(richiestaInvioRDC.getDatiInvio().getDataInvio()),
					richiestaInvioRDC.getDatiInvio().getIdComunicazione(),
					richiestaInvioRDC.getInvioRDC().getCodStato(),
					richiestaInvioRDC.getInvioRDC().getCodComuneDomicilio(), userSP, null, // inseriamo con cdnLav nullo
					formatDate(richiestaInvioRDC.getInvioRDC().getDttNascita()),
					richiestaInvioRDC.getInvioRDC().getDesIndirizzoResidenza(), userSP,
					richiestaInvioRDC.getInvioRDC().getFonte(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttTrasformazione()),
					richiestaInvioRDC.getInvioRDC().getTipoDomanda(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttVariazioneStato()) }, "INSERT");
			// -- COMMIT TRANSAZIONE
			String infoMsg = "Notifica RDC registrata.";
			_logger.info(infoMsg);
			rispostaXML = new RispostaXML("101", infoMsg, "I");
			trans.commitTransaction();
		} catch (Exception e) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();

					String errMsg = "Registrazione Notifica RDC Fallita";
					_logger.error(errMsg, e);
					rispostaXML = new RispostaXML("999", errMsg, "E");
				}
			} catch (EMFInternalError e1) {
				_logger.error("InvioRDC: problema con la rollback", e1);
				rispostaXML = new RispostaXML("999", "InvioRDC: problema con la rollback", "E");
			}
		}

		return rispostaXML;
	}

	public RispostaXML insericiAmRdcStoricoAggiornaAmdRdc(BigDecimal prgAmRdcStorico, BigDecimal prgAmRdc,
			BigDecimal cdnLavoratore, BigDecimal numKlo, RichiestaRDCInviaNotifica richiestaInvioRDC,
			MultipleTransactionQueryExecutor trans) {

		RispostaXML rispostaXML = new RispostaXML("", "", "");

		try {

			String codCittadinanza = StringUtils.isFilledNoBlank(richiestaInvioRDC.getInvioRDC().getCodCittadinanza())
					? richiestaInvioRDC.getInvioRDC().getCodCittadinanza()
					: COD_NULL_DEFAULT;

			String codComuneNascita = getCodiceComune(richiestaInvioRDC.getInvioRDC().getCodComuneNascita());

			trans.initTransaction();

			trans.executeQuery("INSERT_AMRDC_STORICO", new Object[] { prgAmRdcStorico, prgAmRdc,
					richiestaInvioRDC.getInvioRDC().getCodSesso(), richiestaInvioRDC.getInvioRDC().getCodCapResidenza(),
					richiestaInvioRDC.getInvioRDC().getDesCognome(),
					richiestaInvioRDC.getInvioRDC().getCodComuneResidenza(),
					richiestaInvioRDC.getInvioRDC().getDesNome(), richiestaInvioRDC.getInvioRDC().getCodFiscale(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttRendicontazione()),
					richiestaInvioRDC.getInvioRDC().getCodRuoloBeneficiario(),
					richiestaInvioRDC.getInvioRDC().getCodFiscaleRichiedente(), codComuneNascita,
					richiestaInvioRDC.getInvioRDC().getDesIndirizzoDomicilio(),
					richiestaInvioRDC.getInvioRDC().getCodCapDomicilio(), richiestaInvioRDC.getInvioRDC().getDesEmail(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttDomanda()),
					richiestaInvioRDC.getInvioRDC().getDesTelefono(), richiestaInvioRDC.getInvioRDC().getCodCpi(),
					richiestaInvioRDC.getInvioRDC().getCodProtocolloInps(), codCittadinanza,
					richiestaInvioRDC.getInvioRDC().getCodSap(),
					formatDate(richiestaInvioRDC.getDatiInvio().getDataInvio()),
					richiestaInvioRDC.getDatiInvio().getIdComunicazione(),
					richiestaInvioRDC.getInvioRDC().getCodStato(),
					richiestaInvioRDC.getInvioRDC().getCodComuneDomicilio(), cdnLavoratore,
					formatDate(richiestaInvioRDC.getInvioRDC().getDttNascita()),
					richiestaInvioRDC.getInvioRDC().getDesIndirizzoResidenza(),
					richiestaInvioRDC.getInvioRDC().getFonte(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttTrasformazione()),
					richiestaInvioRDC.getInvioRDC().getTipoDomanda(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttVariazioneStato()) }, "INSERT");

			trans.executeQuery("UPDATE_AMRDC", new Object[] { richiestaInvioRDC.getInvioRDC().getCodSesso(),
					richiestaInvioRDC.getInvioRDC().getCodCapResidenza(),
					richiestaInvioRDC.getInvioRDC().getDesCognome(), numKlo,
					richiestaInvioRDC.getInvioRDC().getCodComuneResidenza(),
					richiestaInvioRDC.getInvioRDC().getDesNome(), richiestaInvioRDC.getInvioRDC().getCodFiscale(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttRendicontazione()),
					richiestaInvioRDC.getInvioRDC().getCodRuoloBeneficiario(),
					richiestaInvioRDC.getInvioRDC().getCodFiscaleRichiedente(), codComuneNascita,
					richiestaInvioRDC.getInvioRDC().getDesIndirizzoDomicilio(),
					richiestaInvioRDC.getInvioRDC().getCodCapDomicilio(), richiestaInvioRDC.getInvioRDC().getDesEmail(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttDomanda()),
					richiestaInvioRDC.getInvioRDC().getDesTelefono(), richiestaInvioRDC.getInvioRDC().getCodCpi(),
					richiestaInvioRDC.getInvioRDC().getCodProtocolloInps(), codCittadinanza,
					richiestaInvioRDC.getInvioRDC().getCodSap(),
					formatDate(richiestaInvioRDC.getDatiInvio().getDataInvio()),
					richiestaInvioRDC.getDatiInvio().getIdComunicazione(),
					richiestaInvioRDC.getInvioRDC().getCodStato(),
					richiestaInvioRDC.getInvioRDC().getCodComuneDomicilio(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttNascita()),
					richiestaInvioRDC.getInvioRDC().getDesIndirizzoResidenza(), userSP,
					richiestaInvioRDC.getInvioRDC().getFonte(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttTrasformazione()),
					richiestaInvioRDC.getInvioRDC().getTipoDomanda(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttVariazioneStato()), prgAmRdc }, "UPDATE");
			// -- COMMIT TRANSAZIONE
			String infoMsg = "Notifica RDC registrata nello storico";
			_logger.info(infoMsg);
			rispostaXML = new RispostaXML("101", infoMsg, "I");
			trans.commitTransaction();
		} catch (Exception e) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();

					String errMsg = "Registrazione Notifica RDC Storico Fallita";
					_logger.error(errMsg, e);
					rispostaXML = new RispostaXML("999", errMsg, "E");
				}
			} catch (EMFInternalError e1) {
				_logger.error("InvioRDC: problema con la rollback", e1);
				rispostaXML = new RispostaXML("999", "InvioRDC: problema con la rollback", "E");
			}
		}

		return rispostaXML;
	}

	public RispostaXML insericiTsWsErr(BigDecimal prgTsWsErr, String tracciatoXml, String idComunicazione,
			MultipleTransactionQueryExecutor trans) {

		RispostaXML rispostaXML = new RispostaXML("", "", "");

		try {
			trans.initTransaction();

			trans.executeQuery("INSERT_LOG_COMUNICAZIONE_AM_RDC",
					new Object[] { prgTsWsErr, "RC", idComunicazione, tracciatoXml, "0", "Non inserito" }, "INSERT");

			// -- COMMIT TRANSAZIONE
			String infoMsg = "Inserimento riga in TS_WS_ERR";
			_logger.info(infoMsg);
			rispostaXML = new RispostaXML("101", infoMsg, "I");
			trans.commitTransaction();
		} catch (Exception e) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();

					String errMsg = "Inserimento riga in TS_WS_ERR Fallito";
					_logger.error(errMsg, e);
					rispostaXML = new RispostaXML("999", errMsg, "E");
				}
			} catch (EMFInternalError e1) {
				_logger.error("InvioRDC: problema con la rollback", e1);
				rispostaXML = new RispostaXML("999", "InvioRDC: problema con la rollback", "E");
			}
		}

		return rispostaXML;
	}

	public RispostaXML inserisciLavoratore(RichiestaRDCInviaNotifica richiestaInvioRDC, BigDecimal cdnLav,
			MultipleTransactionQueryExecutor trans) {

		RispostaXML rispostaXML = new RispostaXML("", "", "");
		String infoMsg = "";
		try {
			trans.initTransaction();
			String codComuneDomicilio = StringUtils
					.isFilledNoBlank(richiestaInvioRDC.getInvioRDC().getCodComuneDomicilio())
							? richiestaInvioRDC.getInvioRDC().getCodComuneDomicilio()
							: richiestaInvioRDC.getInvioRDC().getCodComuneResidenza();
			String indDomicilio = StringUtils
					.isFilledNoBlank(richiestaInvioRDC.getInvioRDC().getDesIndirizzoDomicilio())
							? richiestaInvioRDC.getInvioRDC().getDesIndirizzoDomicilio()
							: richiestaInvioRDC.getInvioRDC().getDesIndirizzoResidenza();
			String capDomicilio = StringUtils.isFilledNoBlank(richiestaInvioRDC.getInvioRDC().getCodCapDomicilio())
					? richiestaInvioRDC.getInvioRDC().getCodCapDomicilio()
					: richiestaInvioRDC.getInvioRDC().getCodCapResidenza();

			String codCittadinanza = StringUtils.isFilledNoBlank(richiestaInvioRDC.getInvioRDC().getCodCittadinanza())
					? richiestaInvioRDC.getInvioRDC().getCodCittadinanza()
					: COD_NULL_DEFAULT;

			String codComuneNascita = getCodiceComune(richiestaInvioRDC.getInvioRDC().getCodComuneNascita());

			trans.executeQuery("INSERT_AMRDC_ANLAVORATORE", new Object[] { cdnLav,
					richiestaInvioRDC.getInvioRDC().getCodFiscale(), richiestaInvioRDC.getInvioRDC().getDesCognome(),
					richiestaInvioRDC.getInvioRDC().getDesNome(), richiestaInvioRDC.getInvioRDC().getCodSesso(),
					formatDate(richiestaInvioRDC.getInvioRDC().getDttNascita()), codComuneNascita, codCittadinanza,
					richiestaInvioRDC.getInvioRDC().getCodComuneResidenza(), codComuneDomicilio, userSP, userSP,
					richiestaInvioRDC.getInvioRDC().getDesIndirizzoResidenza(),
					richiestaInvioRDC.getInvioRDC().getCodCapResidenza(), indDomicilio, capDomicilio,
					richiestaInvioRDC.getInvioRDC().getDesEmail(), richiestaInvioRDC.getInvioRDC().getDesTelefono(),
					"S", null }, "INSERT");
			_logger.info("inserimento in AN_LAVORATORE");
			infoMsg = "inserimento in AN_LAVORATORE";

			// tolgo inserimenti in AM_ELENCO_ANAGRAFICO e in AN_LAV_STORIA_INF fatti in automatico dai trigger
			// faccio solo update sy AN_LAV_STORIA_INF per il CODCPITIT CODCPIORIG ecc

			// regola competenza (codmonotipocpi)
			SourceBean sbCpi = (SourceBean) trans.executeQuery("GET_COD_CPI",
					new Object[] { richiestaInvioRDC.getInvioRDC().getCodComuneResidenza() }, "SELECT");
			String codRif = "";
			if (sbCpi != null) {
				codRif = (String) sbCpi.getAttribute("ROW.codcpi");
			}

			String codCpiTit = null;
			String codCpiOrig = null;
			String codTipoCpi = null;

			SourceBean rowCpi = LavoratoreUtils.getInfoCpiProvinciaComune(trans, codComuneDomicilio);

			if (rowCpi != null) {
				rowCpi = rowCpi.containsAttribute("ROW") ? (SourceBean) rowCpi.getAttribute("ROW") : rowCpi;
				String codCpiDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPI");
				codCpiDomicilio = codCpiDomicilio.trim();
				String codProvinciaDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "PROVINCIACPI");
				String codProvinciaSil = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODPROVINCIASIL");
				if (codCpiDomicilio.equalsIgnoreCase(COD_NULL_DEFAULT)) {
					codCpiDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGO");
				}
				if (codProvinciaDomicilio.equalsIgnoreCase(codProvinciaSil)) {
					codTipoCpi = Properties.CODMONOTIPOCPI_COMP;
					codCpiTit = codCpiDomicilio;
				} else {
					codTipoCpi = Properties.CODMONOTIPOCPI_TIT;
					codCpiTit = codRif;
					if (codCpiTit == null || codCpiTit.length() < 9) {
						codCpiTit = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGOGEN");
					}
					codCpiOrig = codCpiDomicilio;
				}
				// aggiornamento an_lav_storia_inf

				Object[] inputParameters = new Object[1];
				inputParameters[0] = cdnLav;
				SourceBean anLavStoriaInfSourceBean = ((SourceBean) trans
						.executeQuery("SELECT_DATI_AN_LAV_STORIA_INF_APPUNTAMENTO_ONLINE", inputParameters, "SELECT"));

				anLavStoriaInfSourceBean = anLavStoriaInfSourceBean.containsAttribute("ROW")
						? (SourceBean) anLavStoriaInfSourceBean.getAttribute("ROW")
						: anLavStoriaInfSourceBean;

				BigDecimal prgLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean.getAttribute("PRGLAVSTORIAINF");
				BigDecimal numkloLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean
						.getAttribute("NUMKLOLAVSTORIAINF");

				inputParameters = new Object[9];
				inputParameters[0] = codCpiTit;
				inputParameters[1] = userSP;
				inputParameters[2] = userSP;
				inputParameters[3] = codTipoCpi;
				inputParameters[4] = codCpiOrig;
				inputParameters[5] = numkloLavStoriaInf.add(new BigDecimal("1"));
				inputParameters[6] = codComuneDomicilio;
				inputParameters[7] = null;
				inputParameters[8] = prgLavStoriaInf;

				trans.executeQuery("UPDATE_AN_LAV_STORIA_INF_INS_LAV_IMPORTA_SAP", inputParameters, "UPDATE");
				_logger.info("aggiornamento AN_LAV_STORIA_INF");
				infoMsg = "aggiornamento AN_LAV_STORIA_INF";
			}

			/*
			 * se nella notifica e' valorizzato il codice SAP inserisco il record in SP_LAVORATORE (come se avessi
			 * effettuato la chiamata a 'verifica esistenza sap')
			 */
			if (StringUtils.isFilledNoBlank(richiestaInvioRDC.getInvioRDC().getCodSap())) {
				String codCpiSpLav = richiestaInvioRDC.getInvioRDC().getCodCpi();
				if (StringUtils.isEmptyNoBlank(richiestaInvioRDC.getInvioRDC().getCodCpi())) {
					if (StringUtils.isFilledNoBlank(codRif)) {
						codCpiSpLav = codRif;
					}
				}

				trans.executeQuery("INSERT_SP_LAVORATORE_VERIFICA_SAP",
						new Object[] { richiestaInvioRDC.getInvioRDC().getCodSap(), codCpiSpLav,
								formatDate(richiestaInvioRDC.getInvioRDC().getDttNascita()), cdnLav, userSP, userSP,
								richiestaInvioRDC.getInvioRDC().getCodFiscale(), "01" },
						"INSERT");
				_logger.info("inserimento in SP_LAVORATORE");
				infoMsg += " e inserimento in SP_LAVORATORE";
			}

			_logger.info(infoMsg);
			rispostaXML = new RispostaXML("101", infoMsg, "I");

			trans.commitTransaction();
		} catch (Exception e) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();

					String errMsg = "RDC: inserimento lavoratore fallito";
					_logger.error(errMsg, e);
					rispostaXML = new RispostaXML("999", errMsg, "E");
				}
			} catch (EMFInternalError e1) {
				_logger.error("RDC inserimento lavoratore: problema con la rollback", e1);
				rispostaXML = new RispostaXML("999", "RDC inserimento lavoratore: problema con la rollback", "E");
			}
		}

		return rispostaXML;
	}

	public RispostaXML updateAmRdc(BigDecimal cdnlav, BigDecimal numKlo, BigDecimal prgAmRdc,
			MultipleTransactionQueryExecutor trans) {

		RispostaXML rispostaXML = new RispostaXML("", "", "");
		try {
			trans.initTransaction();

			trans.executeQuery("UPDATE_AMRDC_CDNLAV", new Object[] { cdnlav, numKlo, userSP, prgAmRdc }, "UPDATE");
			// -- COMMIT TRANSAZIONE
			String infoMsg = "Aggiornato CDLAV per Notifica RDC: " + prgAmRdc.toString();
			_logger.info(infoMsg);
			rispostaXML = new RispostaXML("101", infoMsg, "U");
			trans.commitTransaction();
		} catch (Exception e) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();

					String errMsg = "Aggiornamento Notifica RDC Fallito";
					_logger.error(errMsg, e);
					rispostaXML = new RispostaXML("999", errMsg, "E");
				}
			} catch (EMFInternalError e1) {
				_logger.error("Aggiornamento Notifica RDC: problema con la rollback", e1);
				rispostaXML = new RispostaXML("999", "Aggiornamento Notifica RDC: problema con la rollback", "E");
			}
		}

		return rispostaXML;
	}

	public SourceBean getBeanLavoratore(String codiceFiscale) {
		SourceBean spLavoratoreSB = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE",
				new Object[] { codiceFiscale }, "SELECT", Values.DB_SIL_DATI);
		return spLavoratoreSB;
	}

	public SourceBean getBeanNotificaRdcPerStorico(String codiceFiscale, String protInps, String ruoloBeneficiario) {
		SourceBean notificaDB = (SourceBean) QueryExecutor.executeQuery("NotificheRDC_PerStorico",
				new Object[] { codiceFiscale, protInps, ruoloBeneficiario }, "SELECT", Values.DB_SIL_DATI);
		return notificaDB;
	}

	public boolean inserisciLavoratore(SourceBean serviceRequest, String codiceCPI, int index, BigDecimal cdnLav,
			MultipleTransactionQueryExecutor trans) {
		boolean isOk = false;
		try {
			trans.initTransaction();
			String codComuneDomicilio = StringUtils.isFilledNoBlank(getAttribute(serviceRequest, "codComDom", index))
					? getAttribute(serviceRequest, "codComDom", index)
					: getAttribute(serviceRequest, "codComRes", index);
			String indDomicilio = StringUtils.isFilledNoBlank(getAttribute(serviceRequest, "indirizzoDom", index))
					? getAttribute(serviceRequest, "indirizzoDom", index)
					: getAttribute(serviceRequest, "indirizzoRes", index);
			String capDomicilio = StringUtils.isFilledNoBlank(getAttribute(serviceRequest, "capDom", index))
					? getAttribute(serviceRequest, "capDom", index)
					: getAttribute(serviceRequest, "capComRes", index);

			String codCittadinanza = StringUtils.isFilledNoBlank(getAttribute(serviceRequest, "codCittad", index))
					? getAttribute(serviceRequest, "codCittad", index)
					: COD_NULL_DEFAULT;
			String codComuneNascita = getCodiceComune(getAttribute(serviceRequest, "codcomNas", index));

			trans.executeQuery("INSERT_AMRDC_ANLAVORATORE",
					new Object[] { cdnLav, getAttribute(serviceRequest, "codiceFiscale", index),
							getAttribute(serviceRequest, "cognome", index), getAttribute(serviceRequest, "nome", index),
							getAttribute(serviceRequest, "sesso", index),
							getAttribute(serviceRequest, "datanas", index), codComuneNascita, codCittadinanza,
							getAttribute(serviceRequest, "codComRes", index), codComuneDomicilio, userSP, userSP,
							getAttribute(serviceRequest, "indirizzoRes", index),
							getAttribute(serviceRequest, "capComRes", index), indDomicilio, capDomicilio,
							getAttribute(serviceRequest, "email", index), getAttribute(serviceRequest, "tel", index),
							"S", null },
					"INSERT");
			_logger.info("inserimento in AN_LAVORATORE");

			// tolgo inserimenti in AM_ELENCO_ANAGRAFICO e in AN_LAV_STORIA_INF fatti in automatico dai trigger
			// faccio solo update sy AN_LAV_STORIA_INF per il CODCPITIT CODCPIORIG ecc

			// regola competenza (codmonotipocpi)
			SourceBean sbCpi = (SourceBean) trans.executeQuery("GET_COD_CPI",
					new Object[] { getAttribute(serviceRequest, "codComRes", index) }, "SELECT");
			String codRif = "";
			if (sbCpi != null) {
				codRif = (String) sbCpi.getAttribute("ROW.codcpi");
			}

			String codCpiTit = null;
			String codCpiOrig = null;
			String codTipoCpi = null;

			SourceBean rowCpi = LavoratoreUtils.getInfoCpiProvinciaComune(trans, codComuneDomicilio);

			if (rowCpi != null) {
				rowCpi = rowCpi.containsAttribute("ROW") ? (SourceBean) rowCpi.getAttribute("ROW") : rowCpi;
				String codCpiDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPI");
				codCpiDomicilio = codCpiDomicilio.trim();
				String codProvinciaDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "PROVINCIACPI");
				String codProvinciaSil = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODPROVINCIASIL");
				if (codCpiDomicilio.equalsIgnoreCase(COD_NULL_DEFAULT)) {
					codCpiDomicilio = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGO");
				}
				if (codProvinciaDomicilio.equalsIgnoreCase(codProvinciaSil)) {
					codTipoCpi = Properties.CODMONOTIPOCPI_COMP;
					codCpiTit = codCpiDomicilio;
				} else {
					codTipoCpi = Properties.CODMONOTIPOCPI_TIT;
					codCpiTit = codRif;
					if (codCpiTit == null || codCpiTit.length() < 9) {
						codCpiTit = SourceBeanUtils.getAttrStrNotNull(rowCpi, "CODCPICAPOLUOGOGEN");
					}
					codCpiOrig = codCpiDomicilio;
				}
				// aggiornamento an_lav_storia_inf

				Object[] inputParameters = new Object[1];
				inputParameters[0] = cdnLav;
				SourceBean anLavStoriaInfSourceBean = ((SourceBean) trans
						.executeQuery("SELECT_DATI_AN_LAV_STORIA_INF_APPUNTAMENTO_ONLINE", inputParameters, "SELECT"));

				anLavStoriaInfSourceBean = anLavStoriaInfSourceBean.containsAttribute("ROW")
						? (SourceBean) anLavStoriaInfSourceBean.getAttribute("ROW")
						: anLavStoriaInfSourceBean;

				BigDecimal prgLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean.getAttribute("PRGLAVSTORIAINF");
				BigDecimal numkloLavStoriaInf = (BigDecimal) anLavStoriaInfSourceBean
						.getAttribute("NUMKLOLAVSTORIAINF");

				inputParameters = new Object[9];
				inputParameters[0] = codCpiTit;
				inputParameters[1] = userSP;
				inputParameters[2] = userSP;
				inputParameters[3] = codTipoCpi;
				inputParameters[4] = codCpiOrig;
				inputParameters[5] = numkloLavStoriaInf.add(new BigDecimal("1"));
				inputParameters[6] = codComuneDomicilio;
				inputParameters[7] = null;
				inputParameters[8] = prgLavStoriaInf;

				trans.executeQuery("UPDATE_AN_LAV_STORIA_INF_INS_LAV_IMPORTA_SAP", inputParameters, "UPDATE");
				_logger.info("aggiornamento AN_LAV_STORIA_INF");
			}

			/*
			 * se nella notifica e' valorizzato il codice SAP inserisco il record in SP_LAVORATORE (come se avessi
			 * effettuato la chiamata a 'verifica esistenza sap')
			 */
			if (StringUtils.isFilledNoBlank(getAttribute(serviceRequest, "codSap", index))) {

				trans.executeQuery("INSERT_SP_LAVORATORE_VERIFICA_SAP",
						new Object[] { getAttribute(serviceRequest, "codSap", index), codiceCPI,
								getAttribute(serviceRequest, "datanas", index), cdnLav, userSP, userSP,
								getAttribute(serviceRequest, "cfRich", index), "01" },
						"INSERT");
				_logger.info("inserimento in SP_LAVORATORE");
			}

			trans.commitTransaction();
			isOk = true;
		} catch (Exception e) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();

					String errMsg = "RDC: inserimento lavoratore fallito";
					_logger.error(errMsg, e);
				}
			} catch (EMFInternalError e1) {
				_logger.error("RDC inserimento lavoratore: problema con la rollback", e1);
			}
		}
		return isOk;
	}

	public String getCpiMembroNucleo(SourceBean serviceRequest, int index, MultipleTransactionQueryExecutor trans)
			throws EMFInternalError {
		String codCpiSpLav = null;
		SourceBean sbCpi = (SourceBean) trans.executeQuery("GET_COD_CPI",
				new Object[] { getAttribute(serviceRequest, "codComRes", index) }, "SELECT");
		if (sbCpi != null) {
			codCpiSpLav = (String) sbCpi.getAttribute("ROW.codcpi");
		}
		return codCpiSpLav;
	}

	public String getCpiMinisterialeMembroNucleo(String codiceCpi, MultipleTransactionQueryExecutor trans)
			throws EMFInternalError {
		String cocCpiMinLav = null;
		SourceBean sbCpiMin = (SourceBean) trans.executeQuery("GET_CPI_MIN_FROM_CODCPI", new Object[] { codiceCpi },
				"SELECT");
		if (sbCpiMin != null) {
			cocCpiMinLav = (String) sbCpiMin.getAttribute("ROW.CODCPIMIN");
		}
		return cocCpiMinLav;
	}

	public boolean insericiAmRdcStoricoAggiornaAmdRdc(BigDecimal prgAmRdcStorico, BigDecimal prgAmRdc,
			BigDecimal cdnLav, BigDecimal numKlo, String codiceCPI, int index, SourceBean serviceRequest,
			MultipleTransactionQueryExecutor trans) {
		boolean isOk = false;

		try {
			String codCittadinanza = StringUtils.isFilledNoBlank(getAttribute(serviceRequest, "codCittad", index))
					? getAttribute(serviceRequest, "codCittad", index)
					: COD_NULL_DEFAULT;
			String codComuneNascita = getCodiceComune(getAttribute(serviceRequest, "codcomNas", index));
			trans.initTransaction();

			trans.executeQuery("INSERT_AMRDC_STORICO_MEMBRI", new Object[] { prgAmRdcStorico, prgAmRdc,
					getAttribute(serviceRequest, "sesso", index), getAttribute(serviceRequest, "capComRes", index),
					getAttribute(serviceRequest, "cognome", index), getAttribute(serviceRequest, "codComRes", index),
					getAttribute(serviceRequest, "nome", index), getAttribute(serviceRequest, "codiceFiscale", index),
					getAttribute(serviceRequest, "dataRned", index), getAttribute(serviceRequest, "codRuolo", index),
					getAttribute(serviceRequest, "cfRich", index), codComuneNascita,
					getAttribute(serviceRequest, "indirizzoDom", index), getAttribute(serviceRequest, "capDom", index),
					getAttribute(serviceRequest, "email", index), getAttribute(serviceRequest, "dataDomanda", index),
					getAttribute(serviceRequest, "tel", index), codiceCPI,
					getAttribute(serviceRequest, "protInps", index), codCittadinanza,
					getAttribute(serviceRequest, "codSap", index), getAttribute(serviceRequest, "protInps", index),
					getAttribute(serviceRequest, "domandaInps", index),
					getAttribute(serviceRequest, "codComDom", index), cdnLav,
					getAttribute(serviceRequest, "datanas", index), getAttribute(serviceRequest, "indirizzoRes", index),
					getAttribute(serviceRequest, "fonte", index), getAttribute(serviceRequest, "dataPassaggio", index),
					getAttribute(serviceRequest, "codTipoDomanda", index),
					getAttribute(serviceRequest, "dataVariazioneStato", index) }, "INSERT");

			trans.executeQuery("UPDATE_AMRDC_MEMBRI", new Object[] { getAttribute(serviceRequest, "sesso", index),
					getAttribute(serviceRequest, "capComRes", index), getAttribute(serviceRequest, "cognome", index),
					numKlo, getAttribute(serviceRequest, "codComRes", index),
					getAttribute(serviceRequest, "nome", index), getAttribute(serviceRequest, "codiceFiscale", index),
					getAttribute(serviceRequest, "dataRned", index), getAttribute(serviceRequest, "codRuolo", index),
					getAttribute(serviceRequest, "cfRich", index), codComuneNascita,
					getAttribute(serviceRequest, "indirizzoDom", index), getAttribute(serviceRequest, "capDom", index),
					getAttribute(serviceRequest, "email", index), getAttribute(serviceRequest, "dataDomanda", index),
					getAttribute(serviceRequest, "tel", index), codiceCPI,
					getAttribute(serviceRequest, "protInps", index), codCittadinanza,
					getAttribute(serviceRequest, "codSap", index), getAttribute(serviceRequest, "domandaInps", index),
					getAttribute(serviceRequest, "codComDom", index), getAttribute(serviceRequest, "datanas", index),
					getAttribute(serviceRequest, "indirizzoRes", index), userSP,
					getAttribute(serviceRequest, "fonte", index), getAttribute(serviceRequest, "dataPassaggio", index),
					getAttribute(serviceRequest, "codTipoDomanda", index),
					getAttribute(serviceRequest, "dataVariazioneStato", index), prgAmRdc }, "UPDATE");
			// -- COMMIT TRANSAZIONE
			String infoMsg = "Notifica RDC registrata nello storico";
			_logger.info(infoMsg);
			trans.commitTransaction();
			isOk = true;
		} catch (Exception e) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();

					String errMsg = "Registrazione Notifica RDC Storico Fallita";
					_logger.error(errMsg, e);
				}
			} catch (EMFInternalError e1) {
				_logger.error("InvioRDC: problema con la rollback", e1);
			}
		}
		return isOk;
	}

	public boolean insericiAmRdc(BigDecimal prgAmRdc, String codiceCPI, int index, SourceBean serviceRequest,
			MultipleTransactionQueryExecutor trans) {

		boolean isOk = false;

		try {
			String codCittadinanza = StringUtils.isFilledNoBlank(getAttribute(serviceRequest, "codCittad", index))
					? getAttribute(serviceRequest, "codCittad", index)
					: COD_NULL_DEFAULT;
			String codComuneNascita = getCodiceComune(getAttribute(serviceRequest, "codcomNas", index));

			trans.initTransaction();

			trans.executeQuery("INSERT_AMRDC_MEMBRI", new Object[] { prgAmRdc,
					getAttribute(serviceRequest, "sesso", index), getAttribute(serviceRequest, "capComRes", index),
					getAttribute(serviceRequest, "cognome", index), getAttribute(serviceRequest, "codComRes", index),
					getAttribute(serviceRequest, "nome", index), getAttribute(serviceRequest, "codiceFiscale", index),
					getAttribute(serviceRequest, "dataRned", index), getAttribute(serviceRequest, "codRuolo", index),
					getAttribute(serviceRequest, "cfRich", index), codComuneNascita,
					getAttribute(serviceRequest, "indirizzoDom", index), getAttribute(serviceRequest, "capDom", index),
					getAttribute(serviceRequest, "email", index), getAttribute(serviceRequest, "dataDomanda", index),
					getAttribute(serviceRequest, "tel", index), codiceCPI,
					getAttribute(serviceRequest, "protInps", index), codCittadinanza,
					getAttribute(serviceRequest, "codSap", index), getAttribute(serviceRequest, "protInps", index),
					getAttribute(serviceRequest, "domandaInps", index),
					getAttribute(serviceRequest, "codComDom", index), userSP, null, // inseriamo con cdnLav nullo
					getAttribute(serviceRequest, "datanas", index), getAttribute(serviceRequest, "indirizzoRes", index),
					userSP, getAttribute(serviceRequest, "fonte", index),
					getAttribute(serviceRequest, "dataPassaggio", index),
					getAttribute(serviceRequest, "codTipoDomanda", index),
					getAttribute(serviceRequest, "dataVariazioneStato", index) }, "INSERT");
			// -- COMMIT TRANSAZIONE
			String infoMsg = "Notifica RDC registrata.";
			_logger.info(infoMsg);
			trans.commitTransaction();
			isOk = true;
		} catch (Exception e) {
			try {
				if (trans != null) {
					trans.rollBackTransaction();

					String errMsg = "Registrazione Notifica RDC Fallita";
					_logger.error(errMsg, e);
				}
			} catch (EMFInternalError e1) {
				_logger.error("InvioRDC: problema con la rollback", e1);
			}
		}

		return isOk;
	}

	public String getCodiceComune(String codiceComune) {

		if (StringUtils.isEmptyNoBlank(codiceComune)) {
			return COD_NULL_DEFAULT;
		}
		try {
			Object[] param = new Object[1];
			param[0] = codiceComune;
			SourceBean beanRows = (SourceBean) QueryExecutor.executeQuery("RDC_ESISTENZA_CODICE_COMUNE", param,
					"SELECT", Values.DB_SIL_DATI);
			if (beanRows == null) {
				return COD_NULL_DEFAULT;
			}
			String comune = null;

			beanRows = beanRows.containsAttribute("ROW") ? (SourceBean) beanRows.getAttribute("ROW") : beanRows;

			if (beanRows != null && beanRows.containsAttribute("CODCOM")) {
				comune = (String) beanRows.getAttribute("CODCOM");
			}
			if (StringUtils.isFilledNoBlank(comune)) {
				return codiceComune;
			} else {
				return COD_NULL_DEFAULT;
			}

		} catch (Exception e) {
			_logger.error(e);
		}

		return COD_NULL_DEFAULT;
	}

	private static SimpleDateFormat outSDF = new SimpleDateFormat("dd/mm/yyyy");
	private static SimpleDateFormat inSDF = new SimpleDateFormat("yyyy-mm-dd");

	public String formatDate(String inDate) {
		String outDate = "";
		if (StringUtils.isFilledNoBlank(inDate)) {
			try {
				Date date = inSDF.parse(inDate);
				outDate = outSDF.format(date);
			} catch (ParseException ex) {
			}
		}
		return outDate;
	}

	public String getAttribute(SourceBean bean, String key, int index) {

		Object attributeItem = bean.getAttributeItem(key);
		Object attrValue = null;
		if (attributeItem == null)
			return null;
		if (attributeItem instanceof SourceBeanAttribute) {
			SourceBeanAttribute sourceBeanAttribute = (SourceBeanAttribute) attributeItem;
			attrValue = sourceBeanAttribute.getValue();
		}
		if (attributeItem instanceof Vector) {
			@SuppressWarnings("rawtypes")
			Vector sourceBeanAttributes = (Vector) attributeItem;
			SourceBeanAttribute sourceBeanAttribute = (SourceBeanAttribute) (sourceBeanAttributes.elementAt(index));
			attrValue = sourceBeanAttribute.getValue();
		}
		if (attrValue == null)
			return "";
		else if (attrValue instanceof String)
			return (String) attrValue;
		else {
			String strVal = attrValue.toString();
			if (strVal == null)
				strVal = "";
			return strVal;
		}
	}
}