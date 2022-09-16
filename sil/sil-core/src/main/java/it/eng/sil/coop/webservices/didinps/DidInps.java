package it.eng.sil.coop.webservices.didinps;

import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.utils.Utils;
import it.eng.sil.coordinamento.servizi.ServizioSoap;
import it.eng.sil.coordinamento.wsClient.np.Execute;
import it.eng.sil.coordinamento.wsClient.np.RispostaXML;
import it.eng.sil.util.xml.XMLValidator;

public class DidInps implements ServizioSoap {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DidInps.class.getName());
	private static final BigDecimal userSP = new BigDecimal("190");
	private final String SCHEMA_XSD_INPUT = "did_inps.xsd";
	private String xmlNotifica;
	private ListaDID listaDIDToInsert;

	public String elabora(Execute parametri) {
		_logger.debug("Notifica SAP... ");

		try {
			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + "did" + File.separator + SCHEMA_XSD_INPUT);

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

		TransactionQueryExecutor tex = null;
		RispostaXML rispostaXML = new RispostaXML("", "", "");
		String risultato = "";
		BigDecimal cdnLav = new BigDecimal(0);

		try {
			xmlNotifica = parametri.getDati();
			listaDIDToInsert = convertToListaDID(xmlNotifica);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "DidInps:convertToListaDID", e);
			String errMsg = "DID Inps: fallita conversione: " + e.getMessage();
			_logger.error(errMsg);
			rispostaXML = new RispostaXML("999", errMsg, "E");
			return rispostaXML.toXMLString();
		}

		try {
			SourceBean spLavoratoreSB = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE",
					new Object[] { listaDIDToInsert.did.getCodiceFiscale() }, "SELECT", Values.DB_SIL_DATI);
			if (spLavoratoreSB != null) {
				SourceBean cpiMasterRow = spLavoratoreSB.containsAttribute("ROW")
						? (SourceBean) spLavoratoreSB.getAttribute("ROW")
						: spLavoratoreSB;
				cdnLav = (BigDecimal) cpiMasterRow.getAttribute("CDNLAVORATORE");
			} else {
				cdnLav = null; // LAVORATORE non trovato
			}
		} catch (Exception e) {
			_logger.error(e);
		}

		try {
			// Inizializzo il TransactionQueryExecutor
			tex = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			tex.initTransaction();
			try {
				String telefono = "", cellulare = "";
				List<JAXBElement<String>> telCell = listaDIDToInsert.did.getTelefonoOrCellulare();
				for (Iterator iterator = telCell.iterator(); iterator.hasNext();) {
					JAXBElement<String> jaxbElement = (JAXBElement<String>) iterator.next();
					if (jaxbElement.getName().toString().toLowerCase().contains("telefono")) {
						telefono = jaxbElement.getValue();
					} else if (jaxbElement.getName().toString().toLowerCase().contains("cellulare")) {
						cellulare = jaxbElement.getValue();
					}
				}
				tex.executeQuery("INSERT_AMDIDINPS", new Object[] {
						// FIXME CDNLAV
						cdnLav, userSP, userSP, listaDIDToInsert.did.getTipoOperazione(),
						listaDIDToInsert.did.getCodiceUnivocoCPI(), listaDIDToInsert.did.getDataDichiarazione(),
						listaDIDToInsert.did.getDataInizioAttivitaParaSub(),
						listaDIDToInsert.did.getDataInizioDisoccupazione(), listaDIDToInsert.dataInvio,
						listaDIDToInsert.did.getRedditoLavoroSubordinato(),
						listaDIDToInsert.did.getRedditoLavoroParaSubord(),
						listaDIDToInsert.did.getCodiceFiscalePIVAUltimaAzienda(), cellulare,
						listaDIDToInsert.did.getCodiceFiscale(), listaDIDToInsert.idComunicazione,
						listaDIDToInsert.did.getDenominazioneAzienda(), listaDIDToInsert.did.getEmail(),
						listaDIDToInsert.did.getProtocollo(), telefono, listaDIDToInsert.did.getUltimaQualifica(),
						listaDIDToInsert.did.getNome(), listaDIDToInsert.did.getCognome(),
						listaDIDToInsert.did.getCittadinanza(), listaDIDToInsert.did.getComuneDomicilio(),
						listaDIDToInsert.did.getCapDomicilio(), listaDIDToInsert.did.getCodiceComuneDomicilio(),
						listaDIDToInsert.did.getProvinciaDomicilio(), listaDIDToInsert.did.getIndirizzoDomicilio(),
						listaDIDToInsert.did.getEmailPatronato(), listaDIDToInsert.did.getRedditoLavoroAccess(),
						listaDIDToInsert.did.getRedditoLavoroAutonoma(),
						listaDIDToInsert.did.getDataInizioAttivitaSubord(),
						listaDIDToInsert.did.getDataInizioAttivitaAutonoma(),
						listaDIDToInsert.did.getDataInizioAttivitaAccess() }, "INSERT");
				// -- COMMIT TRANSAZIONE
				String infoMsg = "Notifica DID INPS registrata.";
				_logger.info(infoMsg);
				rispostaXML = new RispostaXML("101", infoMsg, "I");
				tex.commitTransaction();
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.error(_logger, "DidInps:execute", e);
				String errMsg = "DID Inps: fallito.";
				_logger.error(errMsg);
				rispostaXML = new RispostaXML("999", errMsg, "E");
				tex.rollBackTransaction();
			}
		} catch (Exception e) {
			try {
				if (tex != null) {
					String errMsg = "RegistraNotificaSAP: fallito. \n" + xmlNotifica;
					_logger.error(errMsg, e);
					rispostaXML = new RispostaXML("999", errMsg, "E");
					tex.rollBackTransaction();
				}
			} catch (EMFInternalError e1) {
				_logger.error("RegistraNotificaSAP: problema con la rollback", e1);
				rispostaXML = new RispostaXML("999", "RegistraNotificaSAP: problema con la rollback", "E");
			}
		}
		risultato = rispostaXML.toXMLString();
		return risultato;
	}

	ListaDID convertToListaDID(String xmlNotificaSAP) throws JAXBException {
		JAXBContext jaxbContext;
		ListaDID notificaSap = null;
		try {
			jaxbContext = JAXBContext.newInstance(ListaDID.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			notificaSap = (ListaDID) jaxbUnmarshaller.unmarshal(new StringReader(xmlNotificaSAP));
		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto ListaDID dall'xml");
		}
		return notificaSap;
	}
}
