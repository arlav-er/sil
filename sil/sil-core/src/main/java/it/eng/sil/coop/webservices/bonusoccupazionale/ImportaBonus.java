package it.eng.sil.coop.webservices.bonusoccupazionale;

import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.bonusoccupazionale.input.DatiBonusOccupazionale;
import it.eng.sil.coop.webservices.bonusoccupazionale.input.PoliticheAttive;
import it.eng.sil.coop.webservices.utils.Utils;
import it.eng.sil.coordinamento.servizi.ServizioSoap;
import it.eng.sil.coordinamento.wsClient.np.Execute;
import it.eng.sil.coordinamento.wsClient.np.RispostaXML;
import it.eng.sil.util.xml.XMLValidator;

public class ImportaBonus implements ServizioSoap {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ImportaBonus.class.getName());

	private static final BigDecimal userSP = new BigDecimal("190");
	private final String SCHEMA_XSD_INPUT = "bonus_occupazionale.xsd";
	private String xmlNotificaBonus;
	private DatiBonusOccupazionale bonusToInsert;

	public String elabora(Execute parametri) {
		_logger.debug("Importazione Bonus Occupazionale");
		RispostaXML rispostaXML = new RispostaXML("", "", "");
		String risultato = "";
		try {
			// validazione xsd
			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + "bonusoccupazionale" + File.separator + SCHEMA_XSD_INPUT);

			String validityErrors = XMLValidator.getValidityErrors(parametri.getDati(), schemaFile);
			if (validityErrors != null && validityErrors.length() > 0) {
				String validityError = "Errore di validazione xml: " + validityErrors;
				_logger.error(validityError);
				return Utils.createXMLRisposta("99", "Errore generico");
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "ImportaBonus:elabora", e);
			try {
				return Utils.createXMLRisposta("99", "Errore generico");
			} catch (Exception e1) {
				return "ERRORE GRAVE";
			}
		}

		try {
			xmlNotificaBonus = parametri.getDati();
			bonusToInsert = convertToBonus(xmlNotificaBonus);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "ImportaBonus:convertToBonus", e);
			String errMsg = "Bonus: fallita conversione: " + e.getMessage();
			_logger.error(errMsg);
			rispostaXML = new RispostaXML("999", errMsg, "E");
			return rispostaXML.toXMLString();
		}

		TransactionQueryExecutor tex = null;
		BigDecimal prgNotificaPN = null;

		try {
			tex = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			tex.initTransaction();

			SourceBean rowKey = (SourceBean) tex.executeQuery("GET_SP_NOTIFICA_PN_NEXTVAL", null, "SELECT");
			if (rowKey != null) {
				rowKey = rowKey.containsAttribute("ROW") ? (SourceBean) rowKey.getAttribute("ROW") : rowKey;
				prgNotificaPN = (BigDecimal) rowKey.getAttribute("do_nextval");
			}

			if (prgNotificaPN != null) {
				String datAdesione = DateUtils.formatXMLGregorian(bonusToInsert.getDataAdesione());
				String cfLav = bonusToInsert.getCodiceFiscale();
				String codProv = bonusToInsert.getCodprovincia();
				String idSAP = bonusToInsert.getIdentificativosap();
				Object params[] = { prgNotificaPN, datAdesione, cfLav, codProv, idSAP, userSP, userSP };
				Boolean res = (Boolean) tex.executeQuery("INSERT_NOTIFICA_BONUS_OCCUPAZIONALE", params, "INSERT");
				if (res == null || !res.booleanValue()) {
					_logger.error("Errore inserimento notifica bonus occupazionale");
					throw new Exception("Errore inserimento notifica bonus occupazionale");
				}

				List<PoliticheAttive> politicheAttive = bonusToInsert.getPoliticheAttive();
				for (PoliticheAttive politica : politicheAttive) {
					Boolean resPolitica = (Boolean) tex.executeQuery("INSERT_POLITICA_BONUS_OCCUPAZIONALE",
							new Object[] { prgNotificaPN, politica.getTipoAttivita(),
									DateUtils.formatXMLGregorian(politica.getDataProposta()),
									DateUtils.formatXMLGregorian(politica.getData()),
									DateUtils.formatXMLGregorian(politica.getDataFine()), politica.getTipologiaDurata(),
									politica.getDurata(), politica.getDescrizione(), politica.getTitoloProgetto(),
									politica.getCodiceEntePromotore(), userSP, userSP,
									politica.getTitoloDenominazione() },
							"INSERT");
					if (resPolitica == null || !resPolitica.booleanValue()) {
						_logger.error("Errore inserimento politica attiva bonus occupazionale");
						throw new Exception("Errore inserimento politica attiva bonus occupazionale");
					}
				}

				String infoMsg = "Notifica Bonus registrata.";
				_logger.info(infoMsg);
				rispostaXML = new RispostaXML("101", infoMsg, "I");
			} else {
				String errMsg = "ImportaBonus: errore generazione chiave SP_NOTIFICA_PN. \n" + xmlNotificaBonus;
				_logger.error(errMsg);
				rispostaXML = new RispostaXML("999", errMsg, "E");
			}

			tex.commitTransaction();
		}

		catch (Exception e) {
			try {
				if (tex != null) {
					tex.rollBackTransaction();
				}
				String errMsg = "ImportaBonus: fallito. \n" + xmlNotificaBonus;
				_logger.error(errMsg, e);
				rispostaXML = new RispostaXML("999", errMsg, "E");
			} catch (EMFInternalError e1) {
				_logger.error("ImportaBonus: problema con la rollback", e1);
				rispostaXML = new RispostaXML("999", "ImportaBonus: problema con la rollback", "E");
			}
		}

		risultato = rispostaXML.toXMLString();
		return risultato;
	}

	DatiBonusOccupazionale convertToBonus(String xmlBonus) throws JAXBException {
		JAXBContext jaxbContext;
		DatiBonusOccupazionale notificaBonus = null;
		try {
			jaxbContext = JAXBContext.newInstance(DatiBonusOccupazionale.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			notificaBonus = (DatiBonusOccupazionale) jaxbUnmarshaller.unmarshal(new StringReader(xmlBonus));
		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto DatiBonusOccupazionale dall'xml");
		}
		return notificaBonus;
	}

}
