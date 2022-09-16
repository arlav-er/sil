package it.eng.sil.coop.webservices.agricoltura;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.agricoltura.output.AGRICOLTURA;
import it.eng.sil.coop.webservices.agricoltura.output.AGRICOLTURA.ListaMovimenti;
import it.eng.sil.coop.webservices.agricoltura.output.AGRICOLTURA.ListaMovimenti.Movimento;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

public class GetMovimentiAgricoltura {
	private static final Logger logger = Logger.getLogger(GetMovimentiAgricoltura.class);

	private static final File OUTPUT_MOVIMENTI_AGRICOLTURA_XSD = new File(
			ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd" + File.separator
					+ "agricoltura" + File.separator + "output_agricoltura.xsd");

	private static final Pattern codiceFiscaleCheck = Pattern
			.compile("([A-Z]{6}[0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z])|([0-9]{11})");

	String codiceStr = null;
	String descrizioneStr = null;

	public String getMovimentiAgricoltura(String username, String password, String codiceFiscale) {
		TransactionQueryExecutor tex = null;
		DataConnection conn = null;
		AGRICOLTURA outAgr = new AGRICOLTURA();
		String xmlOutput = null;
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			tex = new TransactionQueryExecutor(conn, null, null);
			tex.initTransaction();

			// verifica dati accesso
			if (!isAutenticazioneValida(username, password, tex)) {
				codiceStr = "01";
				descrizioneStr = "Autenticazione fallita";
				throw new GetMovimentiAgricolturaException("", codiceStr, descrizioneStr);
			}

			BigDecimal cdnLavoratore = getLavoratore(codiceFiscale, tex);

			if (cdnLavoratore != null) {
				recuperaMovimenti(cdnLavoratore, outAgr, tex);
				logger.info("Estratti movimenti del lavoratore con codice fiscale: " + codiceFiscale);
			}

			xmlOutput = convertMovimentiAgricolturaOutputToString(outAgr);
			checkXmlValid(xmlOutput, OUTPUT_MOVIMENTI_AGRICOLTURA_XSD);
			logger.info("Validazione file XML di output correttamente riuscita");

			tex.commitTransaction();

		} catch (Exception e) {
			outAgr.setListaMovimenti(new ListaMovimenti());
			if (e instanceof GetMovimentiAgricolturaException) {
				outAgr.setCodice(((GetMovimentiAgricolturaException) e).getRespCode());
				outAgr.setDescrizione(((GetMovimentiAgricolturaException) e).getRespDesc());
			} else {
				outAgr.setCodice("99");
				outAgr.setDescrizione("Errore generico");
			}
			try {
				xmlOutput = convertMovimentiAgricolturaOutputToString(outAgr);
				if (conn != null) {
					conn.rollBackTransaction();
				}
			} catch (Exception e1) {
				logger.error("Fallita conversione xml di output o problema con la rollback", e1);
			}
		} finally {
			Utils.releaseResources(conn, null, null);
		}

		return xmlOutput;
	}

	private boolean isAutenticazioneValida(String userName, String password, TransactionQueryExecutor tex)
			throws Exception {
		// Controllare autenticazione - by db!
		String usernameTsWs = null;
		String passwordTsWs = null;
		try {
			SourceBean logon = (SourceBean) tex.executeQuery("GET_USER_PWD_WS",
					new Object[] { "GetMovimentiAgricoltura" }, "SELECT");
			usernameTsWs = (String) logon.getAttribute("ROW.struserid");
			passwordTsWs = (String) logon.getAttribute("ROW.strpassword");

			if (usernameTsWs == null || passwordTsWs == null) {
				return false;
			}

			if (usernameTsWs.equalsIgnoreCase(userName) && passwordTsWs.equalsIgnoreCase(password)) {
				logger.info("Autenticato con userName:" + userName + ",  password:" + password);
				return true;
			} else {
				logger.error("Impossibile autenticare userName:" + userName + ",  password:" + password);
				return false;
			}
		} catch (Exception e) {
			logger.error("Impossibile autenticare userName:" + userName + ",  password:" + password, e);
			throw e;
		}
	}

	private String convertMovimentiAgricolturaOutputToString(AGRICOLTURA outAgr) throws JAXBException {
		try {
			JAXBContext jc = JAXBContext.newInstance(AGRICOLTURA.class);
			Marshaller marshaller = jc.createMarshaller();
			StringWriter writer = new StringWriter();
			marshaller.marshal(outAgr, writer);
			String xmlOutAgr = writer.getBuffer().toString();
			return xmlOutAgr;
		} catch (JAXBException ej) {
			logger.error("Fallita conversione xml di output", ej);
			throw ej;
		}
	}

	private BigDecimal getLavoratore(String codiceFiscale, TransactionQueryExecutor tex)
			throws GetMovimentiAgricolturaException, EMFInternalError {
		try {
			BigDecimal cdnlavoratore = null;

			try {
				XMLValidator.checkObjectFieldExists(codiceFiscale, "codicefiscale", true, codiceFiscaleCheck,
						"\"Codice fiscale del lavoratore\"");
			} catch (MandatoryFieldException e1) {
				codiceStr = "02";
				descrizioneStr = "Codice fiscale " + codiceFiscale + " non valido";

				throw new GetMovimentiAgricolturaException(codiceFiscale, codiceStr, descrizioneStr);
			} catch (FieldFormatException e2) {
				codiceStr = "02";
				descrizioneStr = "Codice fiscale " + codiceFiscale + " non valido";

				throw new GetMovimentiAgricolturaException(codiceFiscale, codiceStr, descrizioneStr);
			}

			SourceBean sb = (SourceBean) tex.executeQuery("GET_CDNLAVORATORE", new Object[] { codiceFiscale },
					"SELECT");

			SourceBean lav = null;
			Vector<SourceBean> vectLav = sb.getAttributeAsVector("ROW");
			if (vectLav.size() > 1) {
				// errore più righe ritornate
				codiceStr = "03";
				descrizioneStr = "Codice fiscale " + codiceFiscale + " multiplo in banca dati";

				throw new GetMovimentiAgricolturaException(codiceFiscale, codiceStr, descrizioneStr);
			}
			if (vectLav.size() > 0) {
				lav = vectLav.firstElement();
			} else {
				codiceStr = "03";
				descrizioneStr = "Codice fiscale " + codiceFiscale + " non presente in banca dati";

				throw new GetMovimentiAgricolturaException(codiceFiscale, codiceStr, descrizioneStr);
			}
			cdnlavoratore = (BigDecimal) lav.getAttribute("CDNLAVORATORE");
			return cdnlavoratore;
		} catch (EMFInternalError emf) {
			logger.error("Fallito recupero cdnlavoratore del lavoratore con codice fiscale: " + codiceFiscale, emf);
			throw emf;
		}
	}

	protected void recuperaMovimenti(BigDecimal cdnLavoratore, AGRICOLTURA outAgr, TransactionQueryExecutor tex)
			throws EMFInternalError, ParseException, DatatypeConfigurationException {
		try {
			SourceBean sbAgr = (SourceBean) tex.executeQuery("GET_MOVIMENTI_AGRICOLTURA",
					new Object[] { cdnLavoratore }, "SELECT");

			Vector<SourceBean> vectMovAgr = sbAgr.getAttributeAsVector("ROW");
			ListaMovimenti listaMovimenti = new ListaMovimenti();

			for (SourceBean appMovAgr : vectMovAgr) {
				Movimento mov = new Movimento();
				mov.setCfazienda(StringUtils.getAttributeStrNotNull(appMovAgr, "cfAzienda"));
				mov.setDtinizio(datestringToXml(StringUtils.getAttributeStrNotNull(appMovAgr, "datiniziomov")));
				mov.setDtfine(datestringToXml(StringUtils.getAttributeStrNotNull(appMovAgr, "datFineMovEffettiva")));
				listaMovimenti.getMovimento().add(mov);
			}

			outAgr.setListaMovimenti(listaMovimenti);
			outAgr.setCodice("00");
			outAgr.setDescrizione("OK");
		} catch (EMFInternalError emf) {
			logger.error("Fallito recupero movimenti agricoltura del lavoratore con cdnlavoratore: " + cdnLavoratore,
					emf);
			throw emf;
		}
	}

	protected void checkXmlValid(String issuedXML, File schemaFile) throws GetMovimentiAgricolturaException {

		String validityErrors = XMLValidator.getValidityErrors(issuedXML, schemaFile);

		if (validityErrors != null) {
			logger.error("Errori di validazione file XML:\n" + validityErrors);

			codiceStr = "04";
			descrizioneStr = "Errori di validazione file XML: " + validityErrors;

			throw new GetMovimentiAgricolturaException("", codiceStr, descrizioneStr);
		}
	}

	public static XMLGregorianCalendar datestringToXml(String dateFromDB)
			throws ParseException, DatatypeConfigurationException {
		try {
			XMLGregorianCalendar xmlDateDB = null;
			if (dateFromDB != null && !"".equals(dateFromDB)) {
				DateFormat dfIn = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd");
				xmlDateDB = DatatypeFactory.newInstance().newXMLGregorianCalendar(dfOut.format(dfIn.parse(dateFromDB)));
			}
			return xmlDateDB;
		} catch (ParseException epe) {
			logger.error("Fallito parsing della data: " + dateFromDB, epe);
			throw epe;
		} catch (DatatypeConfigurationException edte) {
			logger.error("Errore durante instaziazione DatatypeFactory della data:" + dateFromDB, edte);
			throw edte;
		}
	}

}