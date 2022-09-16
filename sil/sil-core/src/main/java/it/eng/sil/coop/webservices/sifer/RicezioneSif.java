package it.eng.sil.coop.webservices.sifer;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.coop.utils.MessageBundle;
import it.eng.sil.coop.webservices.sifer.input.SIFERInput;
import it.eng.sil.coop.webservices.sifer.input.SIFERInput.PoliticheAttive.PoliticaAttiva;
import it.eng.sil.coop.webservices.sifer.output.SIFEROutput;
import it.eng.sil.coop.webservices.sifer.output.SIFEROutput.DatiGiovane;
import it.eng.sil.coop.webservices.sifer.output.SIFEROutput.PoliticheAttive;
import it.eng.sil.module.movimenti.constant.Properties;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;

public class RicezioneSif {
	private static final Logger logger = Logger.getLogger(RicezioneSif.class);

	private static final String INPUT_SIF_XSD = ConfigSingleton.getRootPath()
			+ File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "sifer" + File.separator + "Request_SIL_SIF.xsd";
	private static final String OUTPUT_SIF_XSD = ConfigSingleton.getRootPath()
			+ File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "sifer" + File.separator + "Response_SIL_SIF.xsd";

	String codiceStr = null;
	String descrizioneStr = null;

	public String getDatiSif(String userName, String password, String datiXml) {
		TransactionQueryExecutor tex = null;
		DataConnection conn = null;
		SIFEROutput output = new SIFEROutput();
		try {		
			String xml = datiXml.trim();
			// Inizializzo il TransactionQueryExecutor
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			tex = new TransactionQueryExecutor(conn, null, null);
			tex.initTransaction();

			// verifica dati accesso
			if (isAutenticazioneValida(userName, password, tex)) {
				logger.debug("Autenticato con userName:" + userName + ",  password:" + password);
			} else {			
				codiceStr = "99";
				descrizioneStr = "Autenticazione fallita";				
				throw new InvioSiferException("", codiceStr, descrizioneStr);									
			}

			// verifica xml
			try {
				validazioneXml(xml, INPUT_SIF_XSD);			
			}
			catch(Exception e) {
				String returnString = e.getLocalizedMessage();
				codiceStr = "34";
				descrizioneStr = "Validazione fallita: "+ returnString;	

				throw new InvioSiferException("", codiceStr, descrizioneStr);
			}

			SIFERInput inputObj = convertToSiferInput(xml);

			String codiceFiscale = inputObj.getDatiGiovane().getCodiceFiscale();
			//verifica lavoratore
			BigDecimal cdnLavoratore = getLavoratore(codiceFiscale, tex);

			DatiGiovane outDatiGiovane = new DatiGiovane();
			outDatiGiovane.setCodiceFiscale(codiceFiscale);			
			output.setDatiGiovane(outDatiGiovane);

			if (cdnLavoratore != null) {
				processImportSIFER(codiceFiscale, cdnLavoratore, inputObj, output, tex);	
			}


			tex.commitTransaction();

		} catch (InvioSiferException e) {
			DatiGiovane dati = new DatiGiovane();
			dati.setCodice(new BigInteger(e.getRespCode()));
			dati.setDescrizione(e.getRespDesc());
			output.setDatiGiovane(dati);
			try {
				tex.commitTransaction();
			} catch (EMFInternalError e1) {
				logger.error("problema con la rollback", e1);

			}
		} catch (Exception e) {
			DatiGiovane dati = new DatiGiovane();
			dati.setCodice(new BigInteger("99"));
			dati.setDescrizione("Errore generico");
			output.setDatiGiovane(dati);
			try {
				if (conn != null) {
					conn.rollBackTransaction();
				}
			} catch (EMFInternalError e1) {
				logger.error("problema con la rollback", e1);

			}
		}
		finally {
			Utils.releaseResources(conn, null, null);
		}

		String xmlOutput = null;
		try {
			xmlOutput = convertSiferOutputToString(output);
		} catch (JAXBException e) {
			logger.error("Errore nella generazione dell'xml di risposta");
		}

		return xmlOutput;	
	}

	protected void processImportSIFER(String codiceFiscale, BigDecimal cdnLavoratore, SIFERInput sifIn, SIFEROutput sifOut, TransactionQueryExecutor tex) throws InvioSiferException, EMFInternalError, SQLException {
		PoliticheAttive politicheAttiveOut = new PoliticheAttive();
		sifOut.setPoliticheAttive(politicheAttiveOut);
		// recupero configurazione gestione esito politica di tirocinio C06
		UtilsConfig utility = new UtilsConfig("SIF_C06");
		String numConfigC06 = utility.getConfigurazioneDefault_Custom();

		for (SIFERInput.PoliticheAttive.PoliticaAttiva politicaAttiva : sifIn.getPoliticheAttive().getPoliticaAttiva()) {
			SIFEROutput.PoliticheAttive.PoliticaAttiva politicaOut = new SIFEROutput.PoliticheAttive.PoliticaAttiva();
			try {
				politicaOut.setPrgColloquio(politicaAttiva.getPrgColloquio());
				politicaOut.setPrgPercorso(politicaAttiva.getPrgPercorso());

				checkPrgColloquio(codiceFiscale, cdnLavoratore, politicaAttiva, tex);			
				SourceBean percorsoConcordato = checkPercorsoConcordato(codiceFiscale, politicaAttiva, tex);

				int numAzioni = getAzioni(politicaAttiva, tex);
				if (numAzioni == 0) {  
					String denomProv = getProvinciaSIL(tex);
					codiceStr = "24";
					descrizioneStr = "Per il CF "+codiceFiscale+" non è stata trovata alcuna politica attiva nel SIL Provinciale "+denomProv;
					throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
				}

				if (politicaAttiva.getTipologiaDurata() != null) {
					checkTipoDurata(codiceFiscale, politicaAttiva, tex);
				}

				if (politicaAttiva.getDataAvvioAttivita() != null && politicaAttiva.getDataFineAttivita() != null) {
					if (politicaAttiva.getDataAvvioAttivita().compare(politicaAttiva.getDataFineAttivita()) > 0) {
						codiceStr = "9";
						descrizioneStr = "La data fine attività è minore della data avvio attività";
						throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
					}
				}

				if (politicaAttiva.getEsito() != null) {
					String descEsitoFormazione = getDescrizioneEsito(politicaAttiva.getEsito(), tex);
					if (descEsitoFormazione == null) { 
						codiceStr = "10";
						descrizioneStr = "Codice esito "+politicaAttiva.getEsito()+" non riconosciuto";
						throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
					}
				}

				if (politicaAttiva.getEsito() != null) {
					String codEsitoValido = getValiditaEsitoSifer(politicaAttiva.getEsito(), tex);
					if (codEsitoValido == null) { 
						codiceStr = "38";					
						String descEsito = getDescrizioneEsito(politicaAttiva.getEsito(), tex);					
						descrizioneStr = "Politica attiva con esito "+descEsito+ " non condiviso con la Formazione";
						throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
					}
					else if (("FC").equalsIgnoreCase(politicaAttiva.getEsito())) {
						if(politicaAttiva.getCorsoLavoratore()==null || StringUtils.isEmptyNoBlank(politicaAttiva.getCorsoLavoratore().getCodiceCorso())) {
							codiceStr = "15";					
							descrizioneStr = "La politica attiva ha esito Concluso ma non ha un corso associato.";
							throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
						}
					}
				}


				String codAzioneSifer = getCodAzioneSifer(politicaAttiva, tex);
				if (codAzioneSifer != null) {					
					if (politicaAttiva.getCfDatoreLavoro() == null) {
						if (("C06").equalsIgnoreCase(codAzioneSifer) || ("E01").equalsIgnoreCase(codAzioneSifer) || 
								("E02").equalsIgnoreCase(codAzioneSifer) || ("E03").equalsIgnoreCase(codAzioneSifer) ||
								("D01").equalsIgnoreCase(codAzioneSifer)) {
							if (("AVV").equalsIgnoreCase(politicaAttiva.getEsito()) || ("INT").equalsIgnoreCase(politicaAttiva.getEsito()) || 
									("FC").equalsIgnoreCase(politicaAttiva.getEsito()) || ("INF").equalsIgnoreCase(politicaAttiva.getEsito())) {
								codiceStr = "23";
								descrizioneStr = "CF Datore Lavoro obbligatorio per la politica attiva con tipo attività C06/E01/E02/E03/D01 e con esito Avviato, Interrotto, Concluso, " +
										"Interrotto da Formazione";
								throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
							}
						}
					}
				}

				if ("AVV".equalsIgnoreCase(politicaAttiva.getEsito()) || "FC".equalsIgnoreCase(politicaAttiva.getEsito()) || 
						"INT".equalsIgnoreCase(politicaAttiva.getEsito()) || "INF".equalsIgnoreCase(politicaAttiva.getEsito())) {
					if (politicaAttiva.getDataAvvioAttivita() == null) {
						codiceStr = "30";
						descrizioneStr = "Data avvio attività obbligatoria per esito Avviato, Interrotto, Interrotto da Formazione, Concluso";
						throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
					}
				}

				if ("FC".equalsIgnoreCase(politicaAttiva.getEsito()) || "INT".equalsIgnoreCase(politicaAttiva.getEsito()) || 
						"INF".equalsIgnoreCase(politicaAttiva.getEsito())) {
					if (politicaAttiva.getDataFineAttivita() == null) {
						codiceStr = "31";
						descrizioneStr = "Data fine attività obbligatoria per esito Interrotto, Concluso, Interrotto da formazione";
						throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
					}
				}			


				if ("FC".equalsIgnoreCase(politicaAttiva.getEsito()) || "INT".equalsIgnoreCase(politicaAttiva.getEsito()) || 
						"INF".equalsIgnoreCase(politicaAttiva.getEsito())) {
					if (politicaAttiva.getDurataEffettiva() == null) {
						codiceStr = "32";
						descrizioneStr = "Durata effettiva obbligatoria per esito Interrotto, Concluso, Interrotto da Formazione";
						throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
					}
				}

				if (politicaAttiva.getDurataEffettiva() != null || politicaAttiva.getDurataMassima() != null || politicaAttiva.getDurataMinima() != null) {
					if (politicaAttiva.getTipologiaDurata() == null) {
						codiceStr = "33";
						descrizioneStr = "Tipologia durata obbligatoria se presente Durata effettiva o Durata massima o Durata minima";
						throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
					}
				}


				// controllo esito politica attiva
				// workflow cambio esito + aggiornamento
				String esitoFormazione = politicaAttiva.getEsito();				
				String codEsitoSil = (String)percorsoConcordato.getAttribute("CODESITO");
				String flgNonModificare = (String)percorsoConcordato.getAttribute("FLG_NON_MODIFICARE");

				if ("NA".equalsIgnoreCase(esitoFormazione) || "RIF".equalsIgnoreCase(esitoFormazione) || "ANT".equalsIgnoreCase(esitoFormazione)) {
					codiceStr = "37";					
					String descEsito = getDescrizioneEsito(esitoFormazione, tex);					
					descrizioneStr = "Politica attiva con esito "+descEsito+ " non di competenza della Formazione";
					throw new InvioSiferException(codiceFiscale, codiceStr, descrizioneStr);
				}
				else if ("PRO".equalsIgnoreCase(esitoFormazione)) {			
					if ("S".equalsIgnoreCase(flgNonModificare)) {

						codiceStr = "28";
						String descEsito = getDescrizioneEsito(codEsitoSil, tex);
						String descEsitoFormazione = getDescrizioneEsito(esitoFormazione, tex);	
						descrizioneStr = "Cambio esito della politica attiva non consentita da esito "+descEsito+" a "+descEsitoFormazione;
						//updateSezioneFormazione
						updateSezioneFormazione(codiceFiscale, politicaAttiva, descrizioneStr, tex);

						throw new InvioSiferException(codiceFiscale, codiceStr, descrizioneStr);
					}
				}
				else if ("AVV".equalsIgnoreCase(esitoFormazione)) {
					if ("PRO".equalsIgnoreCase(codEsitoSil) || "AVV".equalsIgnoreCase(codEsitoSil)) {
						//updatePoliticaAttiva
						updatePoliticaAttiva(codiceFiscale, codEsitoSil, politicaAttiva, tex);
						//updateSezioneFormazione
						updateSezioneFormazione(codiceFiscale, politicaAttiva, null, tex);
					}
					else {						
						codiceStr = "28";
						String descEsito = getDescrizioneEsito(codEsitoSil, tex);
						String descEsitoFormazione = getDescrizioneEsito(esitoFormazione, tex);	
						descrizioneStr = "Cambio esito della politica attiva non consentita da esito "+descEsito+" a "+descEsitoFormazione;
						//updateSezioneFormazione
						updateSezioneFormazione(codiceFiscale, politicaAttiva, descrizioneStr, tex);

						throw new InvioSiferException(codiceFiscale, codiceStr, descrizioneStr);				
					}
				}
				else if ("INF".equalsIgnoreCase(esitoFormazione)) {
					if ("AVV".equalsIgnoreCase(codEsitoSil) || "INF".equalsIgnoreCase(codEsitoSil)) {
						//updatePoliticaAttiva
						updatePoliticaAttiva(codiceFiscale, codEsitoSil, politicaAttiva, tex);
						//updateSezioneFormazione
						updateSezioneFormazione(codiceFiscale, politicaAttiva, null, tex);
						//Gestione Esito Politica di Tirocinio (personalizzazione Umbria)
						if (codAzioneSifer.equalsIgnoreCase("C06") && numConfigC06.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {
							//updatePoliticaAttivaTirocinio
							updatePoliticaAttivaTirocinio(politicaAttiva, tex);
							//insertPoliticaAttivaTirocinio
							SourceBean resNewPrgPercorso = (SourceBean) tex.executeQuery("OR_PERCORSO_CONCORDATO_NEXTVAL", null, "SELECT");
							BigDecimal prgPercorsoNew = resNewPrgPercorso.containsAttribute("ROW")?
									(BigDecimal)resNewPrgPercorso.getAttribute("ROW.do_nextval"):(BigDecimal)resNewPrgPercorso.getAttribute("do_nextval");

									insertPoliticaAttivaTirocinio(politicaAttiva, prgPercorsoNew, tex);
									//ricreare legame con il patto
									BigDecimal prgPattoLavCollegato = getCollegamentoAzionePatto(politicaAttiva, tex, codiceFiscale);
									if (prgPattoLavCollegato != null) {
										insertLegamePattoTirocinio(prgPattoLavCollegato, prgPercorsoNew, tex);
									}
						}
					}
					else {
						codiceStr = "28";
						String descEsito = getDescrizioneEsito(codEsitoSil, tex);
						String descEsitoFormazione = getDescrizioneEsito(esitoFormazione, tex);	
						descrizioneStr = "Cambio esito della politica attiva non consentita da esito "+descEsito+" a "+descEsitoFormazione;
						//updateSezioneFormazione
						updateSezioneFormazione(codiceFiscale, politicaAttiva, descrizioneStr, tex);
						throw new InvioSiferException(codiceFiscale, codiceStr, descrizioneStr);
					}
				}
				else if ("INT".equalsIgnoreCase(esitoFormazione)) {
					if (flgNonModificare == null  || "N".equalsIgnoreCase(flgNonModificare)) {						
						codiceStr = "28";
						String descEsito = getDescrizioneEsito(codEsitoSil, tex);
						String descEsitoFormazione = getDescrizioneEsito(esitoFormazione, tex);	
						descrizioneStr = "Cambio esito della politica attiva non consentita da esito "+descEsito+" a "+descEsitoFormazione;
						//updateSezioneFormazione
						updateSezioneFormazione(codiceFiscale, politicaAttiva, descrizioneStr, tex);

						throw new InvioSiferException(codiceFiscale, codiceStr, descrizioneStr);
					}
					else if ("S".equalsIgnoreCase(flgNonModificare)) {
						//updatePoliticaAttiva
						updatePoliticaAttiva(codiceFiscale, codEsitoSil, politicaAttiva, tex);
						//updateSezioneFormazione
						updateSezioneFormazione(codiceFiscale, politicaAttiva, null, tex);	
					}
				}
				else if ("FC".equalsIgnoreCase(esitoFormazione)) {
					if (flgNonModificare == null  || "N".equalsIgnoreCase(flgNonModificare)) {
						//updateSezioneFormazione
						updateSezioneFormazione(codiceFiscale, politicaAttiva, null, tex);							
					}
					else if ("S".equalsIgnoreCase(flgNonModificare)) {
						//updatePoliticaAttiva
						updatePoliticaAttiva(codiceFiscale, codEsitoSil, politicaAttiva, tex);
						//updateSezioneFormazione
						updateSezioneFormazione(codiceFiscale, politicaAttiva, null, tex);	
					}
				}
				else {
					//esito non condiviso 
					codiceStr = "38";					
					String descEsito = getDescrizioneEsito(codEsitoSil, tex);					
					descrizioneStr = "Politica attiva con esito "+descEsito+ " non condiviso con la Formazione";
					throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
				}
				
				//inserimento corso 
				if(politicaAttiva.getCorsoLavoratore()!=null && !StringUtils.isEmptyNoBlank(politicaAttiva.getCorsoLavoratore().getCodiceCorso())) {
					inserisciCorso(politicaAttiva, tex, codiceFiscale);
					inserisciCorsoLavoratore(cdnLavoratore, politicaAttiva, tex, codiceFiscale);
				}

				politicaOut.setCodice(new BigInteger("0"));
				politicaOut.setDescrizione("OK");
				politicheAttiveOut.getPoliticaAttiva().add(politicaOut);

			} catch (InvioSiferException e) {
				politicaOut.setCodice(new BigInteger(e.getRespCode()));
				politicaOut.setDescrizione(e.getRespDesc());
				politicheAttiveOut.getPoliticaAttiva().add(politicaOut);
			}

			
			

		}

		sifOut.setPoliticheAttive(politicheAttiveOut);
	}	

	private static void validazioneXml(String xml, String xsdPath) throws Exception {		
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		File schemaFile = new File(xsdPath);
		StreamSource streamSource = new StreamSource(schemaFile);
		Schema schema = factory.newSchema(streamSource);
		Validator validator = schema.newValidator();
		StreamSource datiXmlStreamSource = new StreamSource(new StringReader(xml.trim()));
		validator.validate(datiXmlStreamSource);	
	}	

	private boolean isAutenticazioneValida(String userName, String password, TransactionQueryExecutor tex) throws Exception {
		// Controllare autenticazione - by db!
		String usernameTsWs = null;
		String passwordTsWs = null;
		try {
			SourceBean logon = (SourceBean) tex.executeQuery("GET_USER_PWD_WS", new Object[] {"SIFER_TO_SIL"}, "SELECT");
			usernameTsWs = (String) logon.getAttribute("ROW.struserid");
			passwordTsWs = (String) logon.getAttribute("ROW.strpassword");				

			if (usernameTsWs == null || passwordTsWs == null) {
				return false;
			}

			if (usernameTsWs.equalsIgnoreCase(userName) && passwordTsWs.equalsIgnoreCase(password)) {
				logger.info("Autenticato con userName:" + userName + ",  password:" + password);
				return true;
			}
			else {
				logger.error("Impossibile autenticare userName:" + userName + ",  password:" + password);
				return false;
			}
		} catch (Exception e) {
			logger.error("Impossibile autenticare userName:" + userName + ",  password:" + password, e);
			// return false;
			throw e;
		} 
	}

	private String getProvinciaSIL(TransactionQueryExecutor tex) throws InvioSiferException, EMFInternalError{
		// Controllare autenticazione - by db!
		String provincia = null;		
		SourceBean generale = (SourceBean) tex.executeQuery("GET_PROVINCIA_SIL", new Object[] {}, "SELECT");
		provincia = (String) generale.getAttribute("ROW.strdenominazione");

		return provincia;		
	}

	private String convertSiferOutputToString(SIFEROutput output) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(SIFEROutput.class);
		Marshaller marshaller = jc.createMarshaller();		
		StringWriter writer = new StringWriter();
		marshaller.marshal(output, writer);
		String xmlOutput = writer.getBuffer().toString();
		return xmlOutput;
	}

	public SIFERInput convertToSiferInput(String xmlInput) throws JAXBException {
		JAXBContext jaxbContext;
		SIFERInput inputObj = null;
		try {
			jaxbContext = JAXBContext.newInstance(SIFERInput.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			inputObj = (SIFERInput) jaxbUnmarshaller.unmarshal(new StringReader(xmlInput));
		} catch (JAXBException e) {
			logger.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return inputObj;
	}

	private BigDecimal getLavoratore(String codiceFiscale, TransactionQueryExecutor tex) throws InvioSiferException, EMFInternalError  {
		BigDecimal cdnlavoratore = null;

		SourceBean sb = (SourceBean) tex.executeQuery("GET_CDNLAVORATORE", new Object[] {codiceFiscale}, "SELECT");

		SourceBean lav = null;
		Vector<SourceBean> vectLav = sb.getAttributeAsVector("ROW");
		if (vectLav.size() > 1) {
			// errore più righe ritornate
			codiceStr = "1";
			descrizioneStr = "Codice fiscale "+codiceFiscale+" multiplo in banca dati";

			throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
		}
		if (vectLav.size() > 0) {
			lav = vectLav.firstElement();
		} else {
			codiceStr = "1";
			descrizioneStr = "Codice fiscale "+codiceFiscale+" non presente in banca dati";

			throw new InvioSiferException(codiceFiscale, codiceStr,	descrizioneStr);
		}
		cdnlavoratore = (BigDecimal)lav.getAttribute("CDNLAVORATORE");
		return cdnlavoratore;
	}

	private void checkPrgColloquio(String cf, BigDecimal cdn, PoliticaAttiva politicaAttiva, TransactionQueryExecutor tex) throws InvioSiferException, EMFInternalError {
		SourceBean sb = (SourceBean) tex.executeQuery("CHECK_OR_LAV_COLLOQUIO", new Object[] {politicaAttiva.getPrgColloquio(), cdn}, "SELECT");		
		if (sb == null
				|| ((SourceBean) sb).getAttributeAsVector("ROW") == null
				|| ((SourceBean) sb).getAttributeAsVector("ROW").size() < 1) {
			codiceStr = "3";
			descrizioneStr = "Identificativo del colloquio non valido relativo al prgcolloquio "+politicaAttiva.getPrgColloquio()+" ed al CF "+cf;
			throw new InvioSiferException(cf, codiceStr, descrizioneStr);
		}
	}

	private SourceBean checkPercorsoConcordato(String cf, PoliticaAttiva politicaAttiva, TransactionQueryExecutor tex)
			throws InvioSiferException, EMFInternalError {
		SourceBean sb = (SourceBean) tex.executeQuery("CHECK_OR_PERCORSO_CONCORDATO", new Object[] {politicaAttiva.getPrgColloquio(), politicaAttiva.getPrgPercorso()}, "SELECT");			
		if (sb == null
				|| ((SourceBean) sb).getAttributeAsVector("ROW") == null
				|| ((SourceBean) sb).getAttributeAsVector("ROW").size() < 1) {
			codiceStr = "6";
			descrizioneStr = "Identificativo dell'azione non valido relativo al prgpercorso "+politicaAttiva.getPrgPercorso()+" ed al CF "+cf;
			throw new InvioSiferException(cf, codiceStr, descrizioneStr);
		}
		else if (((SourceBean) sb).getAttributeAsVector("ROW").size() > 1) {
			codiceStr = "36";
			descrizioneStr = "Politica attiva non univoca per il prgcolloquio "+politicaAttiva.getPrgColloquio()+" e prgpercorso "+politicaAttiva.getPrgPercorso()+" relativo al lavoratore con CF "+cf;
			throw new InvioSiferException(cf, codiceStr, descrizioneStr);
		}
		else{
			// da codazionesifer -> prgazioni
			Vector<SourceBean> bd = ((SourceBean) sb).getAttributeAsVector("ROW");
			return bd.get(0);
		}
	}

	private void checkTipoDurata(String cf, PoliticaAttiva politicaAttiva,
			TransactionQueryExecutor tex) throws InvioSiferException, EMFInternalError {
		SourceBean sb = (SourceBean) tex.executeQuery("CHECK_MN_YG_TIPOLOGIA_DURATA", new Object[] {politicaAttiva.getTipologiaDurata()}, "SELECT");					
		if (sb == null
				|| ((SourceBean) sb).getAttributeAsVector("ROW") == null
				|| ((SourceBean) sb).getAttributeAsVector("ROW").size() < 1) {
			codiceStr = "8";
			descrizioneStr = "Tipologia durata non valida";
			throw new InvioSiferException(cf, codiceStr, descrizioneStr);
		}
	}

	private void updatePoliticaAttiva(String cf, String codEsitoSil, PoliticaAttiva politicaAttiva, TransactionQueryExecutor transExec)	throws InvioSiferException {

		try {
			String flgGruppo = "N";
			Date dataAvvio = null;
			Date dataFine = null;
			if (politicaAttiva.getNumPartecipanti() != null && politicaAttiva.getNumPartecipanti().compareTo(new BigInteger("1")) > 0) {
				flgGruppo = "S";
			}			
			if (politicaAttiva.getDataAvvioAttivita() != null) {
				dataAvvio = politicaAttiva.getDataAvvioAttivita().toGregorianCalendar().getTime();
			}			
			if (politicaAttiva.getDataFineAttivita() != null) {
				dataFine = politicaAttiva.getDataFineAttivita().toGregorianCalendar().getTime();
			}					

			String esitoFormazione = politicaAttiva.getEsito();
			String flgNonModificare = "N";
			if ("AVV".equalsIgnoreCase(esitoFormazione) || "INT".equalsIgnoreCase(esitoFormazione) || "INF".equalsIgnoreCase(esitoFormazione)) {
				flgNonModificare = "S";
			}
			if ("FC".equalsIgnoreCase(esitoFormazione) && "AVV".equalsIgnoreCase(codEsitoSil)) {
				flgNonModificare = "S";
			}

			Object params[] = new Object[17];
			params[0] = new BigDecimal("190");
			params[1] = flgGruppo;
			params[2] = politicaAttiva.getNumPartecipanti();
			params[3] = dataAvvio == null ? null : DateUtils.format(dataAvvio);
			params[4] = dataAvvio == null ? null : DateUtils.format(dataAvvio);
			params[5] = dataFine == null ? null : DateUtils.format(dataFine);
			params[6] = politicaAttiva.getDurataEffettiva();
			params[7] = politicaAttiva.getDurataMassima();
			params[8] = politicaAttiva.getDurataMinima();
			params[9] = politicaAttiva.getTipologiaDurata();
			params[10] = esitoFormazione;
			params[11] = esitoFormazione;
			params[12] = flgNonModificare;
			params[13] = politicaAttiva.getCfDatoreLavoro();
			params[14] = politicaAttiva.getFlgPoliticaFse();
			params[15] = politicaAttiva.getPrgColloquio();
			params[16] = politicaAttiva.getPrgPercorso();

			Boolean res = (Boolean) transExec.executeQuery("UPD_PERCORSO_POLITICA_ATTIVA_SIFER", params, "UPDATE");			
		} catch (Exception e) {			
			codiceStr = "99";
			descrizioneStr = "Errore generico in aggiornamento percorso";
			logger.warn(codiceStr);
			logger.warn(descrizioneStr);

			throw new InvioSiferException("", codiceStr, descrizioneStr);

		} 

	}

	private void updatePoliticaAttivaTirocinio(PoliticaAttiva politicaAttiva, TransactionQueryExecutor transExec) throws InvioSiferException {
		String strNote = "Esito Azione modificato in Concluso in seguito al ricevimento dell'esito \"Interrotto da Formazione\"";
		Date dataFine = null;
		try {
			if (politicaAttiva.getDataFineAttivita() != null) {
				dataFine = politicaAttiva.getDataFineAttivita().toGregorianCalendar().getTime();
			}
			Object params[] = new Object[7];
			params[0] = new BigDecimal("190");
			params[1] = "FC";
			params[2] = "E";
			params[3] = dataFine == null ? null : DateUtils.format(dataFine);
			params[4] = strNote;
			params[5] = politicaAttiva.getPrgColloquio();
			params[6] = politicaAttiva.getPrgPercorso();

			Boolean res = (Boolean) transExec.executeQuery("UPD_PERCORSO_POLITICA_ATTIVA_TIROCINIO_SIFER", params, "UPDATE");

			if (!res.booleanValue()) {
				throw new Exception("Errore in aggiornamento percorso");
			}
		} 
		catch (Exception e) {			
			codiceStr = "99";
			descrizioneStr = "Errore generico in aggiornamento percorso tirocinio";
			logger.warn(codiceStr);
			logger.warn(descrizioneStr);
			throw new InvioSiferException("", codiceStr, descrizioneStr);
		}
	}

	private void insertPoliticaAttivaTirocinio(PoliticaAttiva politicaAttiva, BigDecimal prgPercorsoNew, TransactionQueryExecutor transExec)
			throws InvioSiferException {
		String strNote = "Azione creata automaticamente per ricezione azione C06 con esito Interrotto da Formazione";
		String flgNonModificare = "N";
		try {
			Object params[] = new Object[10];
			params[0] = prgPercorsoNew;
			params[1] = "PRO";
			params[2] = new BigDecimal("190");
			params[3] = new BigDecimal("190");
			params[4] = strNote;
			params[5] = "P";
			params[6] = flgNonModificare;
			params[7] = politicaAttiva.getFlgPoliticaFse();
			params[8] = politicaAttiva.getPrgColloquio();
			params[9] = politicaAttiva.getPrgPercorso();

			Boolean res = (Boolean) transExec.executeQuery("INS_PERCORSO_POLITICA_ATTIVA_TIROCINIO_SIFER", params, "INSERT");

			if (!res.booleanValue()) {
				throw new Exception("Errore in inserimento percorso tirocinio");
			}
		} 
		catch (Exception e) {			
			codiceStr = "99";
			descrizioneStr = "Errore generico in inserimento percorso tirocinio";
			logger.warn(codiceStr);
			logger.warn(descrizioneStr);
			throw new InvioSiferException("", codiceStr, descrizioneStr);
		}
	}

	private void insertLegamePattoTirocinio(BigDecimal prgPattoLavoratore, BigDecimal prgPercorso, TransactionQueryExecutor transExec) throws InvioSiferException {
		try {
			Object params[] = new Object[3];
			params[0] = prgPattoLavoratore;
			params[1] = "OR_PER";
			params[2] = prgPercorso;

			Boolean res = (Boolean) transExec.executeQuery("INS_LEGAME_PATTO_TIROCINIO_SIFER", params, "INSERT");

			if (!res.booleanValue()) {
				throw new Exception("Errore in inserimento legame tirocinio con il patto");
			}
		} 
		catch (Exception e) {			
			codiceStr = "99";
			descrizioneStr = "Errore generico in inserimento legame tirocinio con il patto";
			logger.warn(codiceStr);
			logger.warn(descrizioneStr);
			throw new InvioSiferException("", codiceStr, descrizioneStr);
		}
	}

	private void updateSezioneFormazione(String cf, PoliticaAttiva politicaAttiva, String notaFormazione, TransactionQueryExecutor transExec) throws InvioSiferException {

		try {
			Date dataAvvio = null;
			Date dataFine = null;
			if (politicaAttiva.getDataAvvioAttivita() != null) {
				dataAvvio = politicaAttiva.getDataAvvioAttivita().toGregorianCalendar().getTime();
			}			
			if (politicaAttiva.getDataFineAttivita() != null) {
				dataFine = politicaAttiva.getDataFineAttivita().toGregorianCalendar().getTime();
			}

			Object params[] = new Object[8];
			params[0] = new BigDecimal("190");
			params[1] = politicaAttiva.getEsito();
			params[2] = dataAvvio == null ? null : DateUtils.format(dataAvvio);
			params[3] = dataFine == null ? null : DateUtils.format(dataFine);
			params[4] = notaFormazione;
			params[5] = notaFormazione;
			params[6] = politicaAttiva.getPrgColloquio();
			params[7] = politicaAttiva.getPrgPercorso();

			Boolean res = (Boolean) transExec.executeQuery("UPD_PERCORSO_SEZ_FORMAZIONE_SIFER", params, "UPDATE");

		} catch (Exception e) {			
			codiceStr = "99";
			descrizioneStr = "Errore generico in aggiornamento percorso";
			logger.warn(codiceStr);
			logger.warn(descrizioneStr);

			throw new InvioSiferException("", codiceStr, descrizioneStr);

		} 
	}

	private String getDescrizioneEsito(String codEsito, TransactionQueryExecutor tex) throws InvioSiferException, EMFInternalError {
		SourceBean sb = (SourceBean) tex.executeQuery("CHECK_ESITO_WS_SIFER", new Object[] {codEsito}, "SELECT");					
		SourceBean esito = null;
		Vector<SourceBean> vectLav = sb.getAttributeAsVector("ROW");		
		if (vectLav.size() > 0) {
			esito = vectLav.firstElement();
			String descEsito = (String)esito.getAttribute("DESCRIZIONE");			
			return descEsito;
		} 
		return null;	
	}

	private int getAzioni(PoliticaAttiva politicaAttiva, TransactionQueryExecutor tex) throws InvioSiferException, EMFInternalError {
		SourceBean sb = (SourceBean) tex.executeQuery("CHECK_AZIONI_WS_SIFER", new Object[] {politicaAttiva.getPrgColloquio(), politicaAttiva.getPrgPercorso()}, "SELECT");		
		Vector<SourceBean> vectAzioni = sb.getAttributeAsVector("ROW");				
		return vectAzioni.size();

	}

	private String getCodAzioneSifer(PoliticaAttiva politicaAttiva, TransactionQueryExecutor tex) throws InvioSiferException, EMFInternalError {
		SourceBean sb = (SourceBean) tex.executeQuery("CHECK_AZIONI_WS_SIFER", new Object[] {politicaAttiva.getPrgColloquio(), politicaAttiva.getPrgPercorso()}, "SELECT");
		SourceBean esito = null;
		Vector<SourceBean> vectAzioni = sb.getAttributeAsVector("ROW");		
		if (vectAzioni.size() > 0) {
			esito = vectAzioni.firstElement();
			String codAzioneSifer = (String)esito.getAttribute("CODAZIONESIFER");			
			return codAzioneSifer;
		} 
		return null;
	}

	private String getValiditaEsitoSifer(String codEsito, TransactionQueryExecutor tex) throws InvioSiferException, EMFInternalError {
		SourceBean sb = (SourceBean) tex.executeQuery("CHECK_VALIDITA_ESITO_WS_SIFER", new Object[] {codEsito}, "SELECT");
		SourceBean esito = null;
		Vector<SourceBean> vectAzioni = sb.getAttributeAsVector("ROW");		
		if (vectAzioni.size() > 0) {
			esito = vectAzioni.firstElement();
			String codice = (String)esito.getAttribute("CODICE");			
			return codice;
		} 
		return null;
	}

	private BigDecimal getCollegamentoAzionePatto(PoliticaAttiva politicaAttiva, TransactionQueryExecutor tex, String codiceFiscale) throws InvioSiferException, EMFInternalError {
		try {
			SourceBean sb = (SourceBean) tex.executeQuery("CHECK_AZIONE_COLLEGATAA_PATTO_SIFER", new Object[] {politicaAttiva.getPrgPercorso()}, "SELECT");
			SourceBean patto = null;
			BigDecimal prgPattoLav = null;
			if (sb != null) {
				Vector<SourceBean> vectPatto = sb.getAttributeAsVector("ROW");		
				if (vectPatto.size() == 1) {
					patto = vectPatto.firstElement();
					prgPattoLav = (BigDecimal)patto.getAttribute("PRGPATTOLAVORATORE");
				}
			}
			return prgPattoLav;
		}
		catch (Exception e) {			
			codiceStr = "20";
			descrizioneStr = "Errore generico in recupero legame tirocinio con il patto";
			Vector v = new Vector(1);
			String messaggio = MessageBundle.getMessage(MessageCodes.SIFERFORMAZIONE.ERR_INSERIMENTOCORSO, v);
			logger.warn(codiceStr);
			logger.warn(messaggio);
			throw new InvioSiferException(codiceFiscale, codiceStr, messaggio);
		}

	}


	private void inserisciCorso( PoliticaAttiva politicaAttiva, TransactionQueryExecutor transExec, String codiceFiscale) throws InvioSiferException {

		try {

			String strDataFine="01/01/2100";
			Date dataFine=new SimpleDateFormat("dd/MM/yyyy").parse(strDataFine);
			Object params[] = new Object[6];
			params[0] = politicaAttiva.getCorsoLavoratore().getCodiceCorso();
			params[1] = politicaAttiva.getCorsoLavoratore().getDescrizioneCorso();
			params[2] = politicaAttiva.getCorsoLavoratore().getAnnoCorso();
			params[3] = DateUtils.formatXMLGregorian(politicaAttiva.getCorsoLavoratore().getDataInizioCorso());
			params[4] = politicaAttiva.getCorsoLavoratore().getDataFineCorso() == null ? DateUtils.format(dataFine) : DateUtils.formatXMLGregorian(politicaAttiva.getCorsoLavoratore().getDataFineCorso());
			params[5] = politicaAttiva.getCorsoLavoratore().getCodiceCorso();


			Boolean res = (Boolean) transExec.executeQuery("INSERT_DE_CORSO", params, "INSERT");			

		} catch (Exception e) {			
			codiceStr = "21";
			Vector v = new Vector(2);
			v.add(politicaAttiva.getCorsoLavoratore().getCodiceCorso());
			v.add(politicaAttiva.getCorsoLavoratore().getCodiceEdizione());
			String messaggio = MessageBundle.getMessage(MessageCodes.SIFERFORMAZIONE.ERR_INSERIMENTOCORSO, v);
			logger.warn(codiceStr);
			logger.warn(messaggio);
			
			throw new InvioSiferException(codiceFiscale, codiceStr, messaggio);
		} 

	}

	private void inserisciCorsoLavoratore(BigDecimal cdnLavoratore, PoliticaAttiva politicaAttiva, TransactionQueryExecutor transExec, String codiceFiscale) throws InvioSiferException {

		try {

			String strDataFine="01/01/2100";
			Date dataFine=new SimpleDateFormat("dd/MM/yyyy").parse(strDataFine);

			BigDecimal  prgCorso = DBKeyGenerator.getNextSequence(transExec.getDataConnection(), "S_PR_CORSO");
			
			Object params[] = new Object[31];
		    params[0] = prgCorso; 	   
		    params[1] = cdnLavoratore;	 
			params[2] = politicaAttiva.getCorsoLavoratore().getCodiceCorso();
			params[3] = politicaAttiva.getCorsoLavoratore().getDescrizioneCorso();
			params[4] = null;
			params[5] = politicaAttiva.getCorsoLavoratore().getDenominazioneEnte();
			params[6] = politicaAttiva.getCorsoLavoratore().getComuneEnte();
			params[7] = politicaAttiva.getCorsoLavoratore().getLocalitaEnte();
			params[8] = politicaAttiva.getCorsoLavoratore().getIndirizzoEnte();
			params[9] = politicaAttiva.getCorsoLavoratore().getAnnoCorso();
			params[10] = politicaAttiva.getCorsoLavoratore().getNumMesi();
			params[11] = politicaAttiva.getCorsoLavoratore().getNumOre();
			params[12] = null;
			params[13] = politicaAttiva.getCorsoLavoratore().getCompletato();
			params[14] = null;
			params[15] = politicaAttiva.getCorsoLavoratore().getTipoCertificazione();
			params[16] = null;
			params[17] = null;
			params[18] = null;
			params[19] = null;
			params[20] = null;
			params[21] = null;
			params[22] = null;
			params[23] = null;
			params[24] = new BigDecimal("190");
			params[25] = new BigDecimal("190");
			params[26] = politicaAttiva.getCorsoLavoratore().getCodiceEdizione();		
			params[27] = politicaAttiva.getCorsoLavoratore().getDescrizioneEdizione();			
			params[28] = DateUtils.formatXMLGregorian(politicaAttiva.getCorsoLavoratore().getDataInizioFrequenzaCorso());
			params[29] = DateUtils.formatXMLGregorian(politicaAttiva.getCorsoLavoratore().getDataFineFrequenzaCorso());
			params[30] = "S";
			
			Boolean res = (Boolean) transExec.executeQuery("INSERT_FOR_PRO", params, "INSERT");			

		} catch (Exception e) {			
			codiceStr = "22";
			Vector v = new Vector(2);
			v.add(politicaAttiva.getPrgColloquio().toString());
			v.add(politicaAttiva.getPrgPercorso().toString());
			String messaggio = MessageBundle.getMessage(MessageCodes.SIFERFORMAZIONE.ERR_INSERIMENTOCORSOPOLITICA, v);
			logger.warn(codiceStr);
			logger.warn(messaggio);
			
			throw new InvioSiferException(codiceFiscale, codiceStr, messaggio);

		} 

	}

}

