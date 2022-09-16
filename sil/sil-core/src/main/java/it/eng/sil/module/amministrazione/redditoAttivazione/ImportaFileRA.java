package it.eng.sil.module.amministrazione.redditoAttivazione;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractHttpModule;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.amministrazione.redditoAttivazione.Domande.Domanda;

public class ImportaFileRA extends AbstractHttpModule {

	private static final long serialVersionUID = 1706689475208344449L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ImportaFileRA.class.getName());
	private String className = this.getClass().getName();
	private HttpServletRequest httpRequest = null;
	private HttpServletResponse httpResponse = null;
	private ServletConfig servletConfig = null;
	private Request jspRequest = null;
	private SmartUpload mySmartUpload = null;
	private java.io.File file = null;

	private final String SCHEMA_XSD_INPUT = "redditoAttivazione.xsd";
	private final String STRCODENTEAUTONOMO = "REG042";
	private final String STRENTEAUTONOMO = "PROVINCIA AUTONOMA DI TRENTO";

	private void initialize() throws ServletException, IOException, SmartUploadException {
		httpRequest = this.getHttpRequest();
		httpResponse = this.getHttpResponse();
		servletConfig = this.getServletConfig();

		mySmartUpload = new SmartUpload();
		mySmartUpload.initialize(servletConfig, httpRequest, httpResponse);
		mySmartUpload.upload();
		jspRequest = mySmartUpload.getRequest();
	}

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		Domande rootDomande = null;
		List<Domanda> listaDomande = null;
		TransactionQueryExecutor txExec = null;
		BigDecimal cdnUtente = null;
		String dettaglioErrore = "";

		try {
			initialize();
			getUploadedFile();
			if (this.file == null) {
				throw new FileNotFoundException("Impossibile effettuare l'importazione");
			}
			cdnUtente = (BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		} catch (Exception e) {
			this.getErrorHandler()
					.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ImportMov.ERR_NOME_FILE));
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);
			return;
		}

		try {
			String fileRedditoAttivazione = read(file);
			// validazione xsd
			/*
			 * File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator +
			 * "xsd" + File.separator + "redditoAttivazione" + File.separator + SCHEMA_XSD_INPUT); String validityErrors
			 * = XMLValidator.getValidityErrors(fileRedditoAttivazione, schemaFile); if (validityErrors != null &&
			 * validityErrors.length() > 0) { String validityError = "Errore di validazione xml: " + validityErrors;
			 * _logger.error(validityError); return; }
			 */

			// Estrazione cdnFunzione e settaggio in response
			response.setAttribute("CDNFUNZIONE", getAttributeAsString("cdnFunzione"));
			BigDecimal cdnLav = null;
			BigDecimal prgRedditoAttivazione = null;
			BigDecimal prgRAFile = null;
			SourceBean rowKey = null;
			boolean insTestataRA = false;

			rootDomande = convertToListaDomanda(fileRedditoAttivazione);

			if (rootDomande == null) {
				throw new Exception("Errore in fase di lettura del file xml");
			}

			listaDomande = rootDomande.getDomanda();

			SourceBean rows = new SourceBean("ROWS");
			SourceBean row = null;
			String nomeFile = "";
			String dataOggi = DateUtils.getNow();

			try {

				int contatoreRighe = 0;
				for (int iDomanda = 0; iDomanda < listaDomande.size(); iDomanda++) {
					try {
						Domanda dm = listaDomande.get(iDomanda);
						cdnLav = null;
						dettaglioErrore = "";
						row = new SourceBean("ROW");
						if (dm.getCognome() != null) {
							row.setAttribute("COGNOME", dm.getCognome());
						}
						if (dm.getNome() != null) {
							row.setAttribute("NOME", dm.getNome());
						}
						row.setAttribute("CODFISC", dm.getCodiceFiscale());

						txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
						txExec.initTransaction();

						if (!insTestataRA) {
							rowKey = (SourceBean) txExec.executeQuery("QUERY_NEXTVAL_RA_FILE", null, "SELECT");
							if (rowKey == null) {
								throw new Exception("Impossibile leggere la sequence");
							}
							rowKey = (rowKey.containsAttribute("ROW") ? (SourceBean) rowKey.getAttribute("ROW")
									: rowKey);
							if (rowKey.containsAttribute("DO_NEXTVAL")) {
								prgRAFile = (BigDecimal) rowKey.getAttribute("DO_NEXTVAL");
								nomeFile = DateUtils.getNow();
								nomeFile = nomeFile.substring(6, 10) + nomeFile.substring(3, 5)
										+ nomeFile.substring(0, 2) + "_RA.xml";

								txExec.executeQuery("INS_RA_FILE", new Object[] { prgRAFile, nomeFile, cdnUtente },
										"INSERT");
								insTestataRA = true;
							}
						}

						SourceBean lavoratoreSB = (SourceBean) txExec.executeQuery("SELECT_AN_LAVORATORE",
								new Object[] { dm.getCodiceFiscale() }, "SELECT");

						if (lavoratoreSB != null) {
							SourceBean rowlav = lavoratoreSB.containsAttribute("ROW")
									? (SourceBean) lavoratoreSB.getAttribute("ROW")
									: lavoratoreSB;
							cdnLav = (BigDecimal) rowlav.getAttribute("CDNLAVORATORE");
							if (cdnLav == null) {
								dettaglioErrore = "Lavoratore non presente in anagrafica";
								throw new Exception("Errore in fase di importazione:" + dettaglioErrore);
							}
						} else {
							dettaglioErrore = "Errore recupero informazioni lavoratore";
							throw new Exception("Errore in fase di importazione:" + dettaglioErrore);
						}

						lavoratoreSB = (SourceBean) txExec.executeQuery("SELECT_AN_LAVORATORE_REDDITO_ATTIVAZIONE",
								new Object[] { dm.getCodiceFiscale(), dm.getDataInizioPrestazione() }, "SELECT");

						if (lavoratoreSB != null) {
							SourceBean rowlav = lavoratoreSB.containsAttribute("ROW")
									? (SourceBean) lavoratoreSB.getAttribute("ROW")
									: lavoratoreSB;
							prgRedditoAttivazione = (BigDecimal) rowlav.getAttribute("PRGREDDITOATTIVAZIONE");
							if (prgRedditoAttivazione != null) {
								dettaglioErrore = "Lavoratore già comunicato nello stesso file o in altro file con la stessa data inizio prestazione";
								throw new Exception("Errore in fase di importazione:" + dettaglioErrore);
							}
						} else {
							dettaglioErrore = "Errore recupero informazioni lavoratore";
							throw new Exception("Errore in fase di importazione:" + dettaglioErrore);
						}

						// gestione inserimento reddito attivazione
						dm.setCodiceEnteAutonomo(STRCODENTEAUTONOMO);
						dm.setDescrizioneEnteAutonomo(STRENTEAUTONOMO);
						if (dm.getDataDomandaPrestazione() == null) {
							dm.setDataDomandaPrestazione(dataOggi.substring(6, 10) + "-" + dataOggi.substring(3, 5)
									+ "-" + dataOggi.substring(0, 2));
						}
						BigDecimal numAnnoDelibera = null;
						BigDecimal numDurataPrestazione = null;
						BigDecimal numImportoGG = null;
						BigDecimal numImportoTotale = null;
						if (dm.getAnnoDeliberaEnteAutonomo() != null && !dm.getAnnoDeliberaEnteAutonomo().equals("")) {
							numAnnoDelibera = new BigDecimal(dm.getAnnoDeliberaEnteAutonomo());
						}
						if (dm.getDurataPrestazione() != null && !dm.getDurataPrestazione().equals("")) {
							numDurataPrestazione = new BigDecimal(dm.getDurataPrestazione());
						}
						if (dm.getImportoLordoGiornalieroPrestazione() != null
								&& !dm.getImportoLordoGiornalieroPrestazione().equals("")) {
							numImportoGG = new BigDecimal(dm.getImportoLordoGiornalieroPrestazione());
						}
						if (dm.getImportoLordoComplessivoDaErogareAlBeneficiario() != null
								&& !dm.getImportoLordoComplessivoDaErogareAlBeneficiario().equals("")) {
							numImportoTotale = new BigDecimal(dm.getImportoLordoComplessivoDaErogareAlBeneficiario());
						}

						txExec.executeQuery("INS_REDDITO_ATTIVAZIONE", new Object[] { dm.getDataDomandaPrestazione(),
								dm.getCodiceEnteAutonomo(), dm.getDescrizioneEnteAutonomo(),
								dm.getMatricolaInpsAziendaBenficiario(), dm.getCodiceFiscaleAziendaBeneficiario(),
								dm.getRagioneSocialeAziendaDelBeneficiario(), dm.getNumeroDeliberaEnteAutonomo(),
								numAnnoDelibera, dm.getDataEsitoDeliberaEnteAutonomo(), dm.getDataInizioPrestazione(),
								dm.getDataFinePrestazione(), numDurataPrestazione, dm.getTipoPrestazione(),
								dm.getCognome(), dm.getNome(), dm.getDataDiNascita(), dm.getCodiceFiscale(),
								dm.getComuneNascita(), dm.getProvinciaNascita(), dm.getStatoNascita(),
								dm.getIndirizzo(), dm.getCap(), dm.getComune(), dm.getProvincia(),
								dm.getNumeroDiTelefonoDelBeneficiario(), dm.getIndirizzoEmailDelBeneficiario(),
								dm.getIbanNazioneLavoratore(), dm.getIbanControlloLavoratore(), dm.getCinLavoratore(),
								dm.getAbiLavoratore(), dm.getCabLavoratore(), dm.getContoCorrenteLavoratore(),
								numImportoGG, numImportoTotale, Decodifica.Stato.DA_ELABORARE, cdnLav, prgRAFile,
								Decodifica.Provenienza.DA_FILE, Decodifica.Costanti.NO, cdnUtente, cdnUtente },
								"INSERT");

						contatoreRighe = contatoreRighe + 1;

						txExec.commitTransaction();

						row.setAttribute("RESULT", "OK");
						rows.setAttribute(row);
					} catch (Exception ex) {
						if (txExec != null) {
							txExec.rollBackTransaction();
						}
						if (contatoreRighe == 0) {
							insTestataRA = false;
						}
						row.setAttribute("RESULT", "ERROR");
						if (dettaglioErrore.equals("")) {
							dettaglioErrore = "Errore inserimento dati";
						}
						row.setAttribute("DETTAGLIOERRORE", dettaglioErrore);
						rows.setAttribute(row);
						it.eng.sil.util.TraceWrapper.error(_logger, "ImportaFileRA:service", ex);
					}
				}
			} catch (Exception ex) {
				if (txExec != null) {
					txExec.rollBackTransaction();
				}
				it.eng.sil.util.TraceWrapper.error(_logger, "ImportaFileRA:service", ex);
			}

			if (insTestataRA) {
				rows.setAttribute("RITORNA_PRGRISULTATO", prgRAFile);
				response.setAttribute("NOME_FILE", nomeFile);
			}
			// nella response metto il risultato dell'importazione
			response.setAttribute(rows);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, "ImportaFileRA:service", e);
			return;
		}

	}

	private String getAttributeAsString(String param) {
		return (String) jspRequest.getParameter(param);
	}

	private void getUploadedFile() throws IOException, SmartUploadException {
		com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(0);
		if (!myFile.isMissing()) {
			// verifico che l'estensione sia xml
			if (!"xml".equalsIgnoreCase(myFile.getFileExt())) {
				throw new IOException("Formato file non valido");
			}
			myFile.getFileName();
			file = File.createTempFile("MOBTEMP", null, null);
			myFile.saveAs(file.getAbsolutePath(), SmartUpload.SAVE_PHYSICAL);
		}
	}

	private String read(File file) throws Exception {
		StringBuffer s = new StringBuffer();
		char[] buff = new char[1024];
		FileReader fr = new FileReader(file.getAbsolutePath());
		int n = 0;
		while ((n = fr.read(buff)) > 0) {
			s.append(buff, 0, n);
		}
		fr.close();
		return s.toString();
	}

	private Domande convertToListaDomanda(String xmlFile) throws JAXBException {
		try {
			JAXBContext context = JAXBContext.newInstance(Domande.class);
			Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
			Domande lstDomande = (Domande) jaxbUnmarshaller.unmarshal(new StringReader(xmlFile));
			return lstDomande;
		} catch (JAXBException e) {
			return null;
		}
	}

	private static XMLGregorianCalendar toXMLGregorianCalendarDateOnly(String dateString)
			throws DatatypeConfigurationException, ParseException {
		GregorianCalendar gc = new GregorianCalendar();
		Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
		gc.setTime(date);

		XMLGregorianCalendar xc = null;

		xc = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(gc.get(Calendar.YEAR),
				gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);

		return xc;
	}

}
