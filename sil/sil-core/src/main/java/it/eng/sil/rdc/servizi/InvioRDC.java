package it.eng.sil.rdc.servizi;

import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.rdc.RichiestaRDCInviaNotifica;
import it.eng.sil.coop.webservices.utils.Utils;
import it.eng.sil.coordinamento.servizi.ServizioSoap;
import it.eng.sil.coordinamento.wsClient.np.Execute;
import it.eng.sil.coordinamento.wsClient.np.RispostaXML;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.util.xml.XMLValidator;

public class InvioRDC implements ServizioSoap {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InvioRDC.class.getName());
	private final String SCHEMA_XSD_INPUT = "invianotificaRDC_1.3.xsd";
	private String xmlNotificaInvioRDC;

	@Override
	public String elabora(Execute parametri) {
		_logger.debug("Ricezione invio Reddito di cittadinanza");

		MultipleTransactionQueryExecutor tex = null;
		RispostaXML rispostaXML = new RispostaXML("", "", "");
		String risultato = "";
		RichiestaRDCInviaNotifica richiestaInvioRDC = null;

		try {
			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + "redditoCittadinanza" + File.separator + SCHEMA_XSD_INPUT);

			String validityErrors = XMLValidator.getValidityErrors(parametri.getDati(), schemaFile);
			if (validityErrors != null && validityErrors.length() > 0) {
				String validityError = "Errore di validazione xml: " + validityErrors;
				_logger.error(validityError);
				// _logger.warn(inputXML);
				return Utils.createXMLRisposta("99", "Errore generico");
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "StipulaDid:putCreaDID", e);
			try {
				return Utils.createXMLRisposta("99", "Errore generico");
			} catch (Exception e1) {
				return "ERRORE GRAVE";
			}
		}

		try {
			xmlNotificaInvioRDC = parametri.getDati();
			richiestaInvioRDC = convertToRichiestaRDCInviaNotifica(xmlNotificaInvioRDC);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "InvioRDC:convertToRichiestaRDCInviaNotifica", e);
			String errMsg = "InvioRDC: fallita conversione: " + e.getMessage();
			_logger.error(errMsg);
			rispostaXML = new RispostaXML("999", errMsg, "E");
			return rispostaXML.toXMLString();
		}
		BigDecimal prgAmRdc = null;
		BigDecimal prgAmRdcStorico = null;
		RDCBean supportRdc = new RDCBean();
		boolean checkLav = true;
		try {
			tex = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);

			SourceBean notificaDB = supportRdc.getBeanNotificaRdcPerStorico(
					richiestaInvioRDC.getInvioRDC().getCodFiscale(),
					richiestaInvioRDC.getInvioRDC().getCodProtocolloInps(),
					richiestaInvioRDC.getInvioRDC().getCodRuoloBeneficiario());
			boolean insertAmRdc = true;

			if ((notificaDB != null) && notificaDB.containsAttribute("ROW")) {
				Date dataInvioNotifica = new SimpleDateFormat("dd/MM/yyyy")
						.parse(supportRdc.formatDate(richiestaInvioRDC.getDatiInvio().getDataInvio()));
				prgAmRdc = (BigDecimal) notificaDB.getAttribute("ROW.PRGRDC");
				BigDecimal numKlo = (BigDecimal) notificaDB.getAttribute("ROW.NUMKLORDC");
				BigDecimal cdnLav = (BigDecimal) notificaDB.getAttribute("ROW.CDNLAVORATORE");
				Date dataInvioRecordDB = (Date) notificaDB.getAttribute("ROW.DATINVIO");
				if (dataInvioNotifica.compareTo(dataInvioRecordDB) > 0) {
					insertAmRdc = false;
					prgAmRdcStorico = DBKeyGenerator.getNextSequence(tex.getDataConnection(), "S_AM_RDC_STORICO");
					rispostaXML = supportRdc.insericiAmRdcStoricoAggiornaAmdRdc(prgAmRdcStorico, prgAmRdc, cdnLav,
							numKlo, richiestaInvioRDC, tex);
				} else {
					BigDecimal prgTsWsErr = DBKeyGenerator.getNextSequence(tex.getDataConnection(), "S_TS_WS_ERR");
					rispostaXML = supportRdc.insericiTsWsErr(prgTsWsErr, xmlNotificaInvioRDC,
							richiestaInvioRDC.getDatiInvio().getIdComunicazione(), tex);
					insertAmRdc = false;
					checkLav = false;
				}
			}

			if (insertAmRdc) {
				prgAmRdc = DBKeyGenerator.getNextSequence(tex.getDataConnection(), "S_AM_RDC");
				rispostaXML = supportRdc.insericiAmRdc(prgAmRdc, richiestaInvioRDC, tex);
			}

		} catch (Exception e) {
			if (tex != null) {
				String errMsg = "Invio RDC Fallito: " + xmlNotificaInvioRDC;
				_logger.error(errMsg, e);
				rispostaXML = new RispostaXML("999", errMsg, "E");

			}

		}

		try {
			if (checkLav) {
				// vedo se il lavoratore e' nella tabella am_rdc
				SourceBean sbNumKlo = (SourceBean) QueryExecutor.executeQuery("NotificaRDC_NumKlo",
						new Object[] { prgAmRdc }, "SELECT", Values.DB_SIL_DATI);
				SourceBean sbNumKloRdc = sbNumKlo.containsAttribute("ROW") ? (SourceBean) sbNumKlo.getAttribute("ROW")
						: sbNumKlo;
				BigDecimal numKlo = (BigDecimal) sbNumKloRdc.getAttribute("NUMKLORDC");
				if (sbNumKloRdc.getAttribute("CDNLAVORATORE") == null) {
					// cerco il lavoratore
					BigDecimal cdnLav = null;
					boolean canUpdateCdnLav = false;
					SourceBean spLavoratoreSB = supportRdc
							.getBeanLavoratore(richiestaInvioRDC.getInvioRDC().getCodFiscale());
					if (spLavoratoreSB != null && spLavoratoreSB.containsAttribute("ROW")) {
						// lavoratore trovato
						SourceBean sbCdnLav = spLavoratoreSB.containsAttribute("ROW")
								? (SourceBean) spLavoratoreSB.getAttribute("ROW")
								: spLavoratoreSB;
						cdnLav = (BigDecimal) sbCdnLav.getAttribute("CDNLAVORATORE");
					}
					if (cdnLav == null) {
						// lavoratore non trovato, lo censisco
						cdnLav = DBKeyGenerator.getNextSequence(tex.getDataConnection(), "S_AN_LAVORATORE");
						RispostaXML rispotaInserimentoLav = new RispostaXML("", "", "");
						rispotaInserimentoLav = supportRdc.inserisciLavoratore(richiestaInvioRDC, cdnLav, tex);
						if (rispotaInserimentoLav.getCodiceEsito().equalsIgnoreCase("101")) {
							canUpdateCdnLav = true;
						}
					} else {
						canUpdateCdnLav = true;
					}
					if (canUpdateCdnLav) {
						// aggiorno la riga con cdnlavoratore
						supportRdc.updateAmRdc(cdnLav, numKlo, prgAmRdc, tex);
					}
				}

			}

		} catch (Exception e) {
			_logger.error("errore ", e);

		} finally {
			if (tex != null) {
				tex.closeConnection();
			}
		}

		risultato = rispostaXML.toXMLString();
		return risultato;
	}

	RichiestaRDCInviaNotifica convertToRichiestaRDCInviaNotifica(String xmlInvioRdc) throws JAXBException {
		JAXBContext jaxbContext;
		RichiestaRDCInviaNotifica richiestaRDC = null;
		try {
			jaxbContext = JAXBContext.newInstance(RichiestaRDCInviaNotifica.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			richiestaRDC = (RichiestaRDCInviaNotifica) jaxbUnmarshaller.unmarshal(new StringReader(xmlInvioRdc));
		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto RichiestaRDCInviaNotifica dall'xml");
		}
		return richiestaRDC;
	}

}
