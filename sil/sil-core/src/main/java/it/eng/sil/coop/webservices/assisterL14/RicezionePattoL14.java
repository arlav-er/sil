package it.eng.sil.coop.webservices.assisterL14;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.coop.webservices.assisterL14.input.FlussoASSISTERSIL;
import it.eng.sil.coop.webservices.assisterL14.input.FlussoASSISTERSIL.Partecipante;
import it.eng.sil.coop.webservices.assisterL14.input.FlussoASSISTERSIL.ProfilingPatto;
import it.eng.sil.coop.webservices.assisterL14.input.FlussoASSISTERSIL.ProfilingPatto.PoliticheAttive;
import it.eng.sil.coop.webservices.assisterL14.input.FlussoASSISTERSIL.ProfilingPatto.PoliticheAttive.PoliticaAttiva;
import it.eng.sil.coop.webservices.assisterL14.input.FlussoASSISTERSIL.ProfilingPatto.Svantaggi;
import it.eng.sil.coop.webservices.assisterL14.input.ObjectFactory;
import it.eng.sil.coop.webservices.assisterL14.output.EsitoFlussoASSISTERSIL;
import it.eng.sil.coop.webservices.bean.UserBean;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

public class RicezionePattoL14 extends AbstractSimpleReport implements RicezionePattoL14Interface {
	
	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicezionePattoL14.class.getName());
	private static final BigDecimal CDN_UT_SIL_ASSISTER =  new BigDecimal("400");
	private static final String ESITO_OK = "00";
	private static final String TIPO_PATTO_L14 = "PL14";
	private static final String TIPO_PATTO_L14_2018 = "L14_2018";
	private static final String TIPO_PATTO_L14_2019 = "L14_2019";

	private FlussoASSISTERSIL dettPattoL14;
	
	public static final String XSD_PATTOL14 = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + 
			"xsd" + File.separator + "assisterL14" + File.separator + "ASSISTER-SIL.xsd";
	public static final String XSD_ESITO_PATTOL14 = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + 
			"xsd" + File.separator + "assisterL14" + File.separator + "SIL-ASSISTER.xsd";
	
	private String esito = "00";
	//private String strCodiceFiscale = null;
	//valore effimero per consentire un esito in caso di xml in ingresso non valido
	//nel wsdl ci dovrebbe essere il codice fiscale come parametro 
	//e non solo all'interno della stringa xml...direi TODO
	//oppure nell'xsd di risposta mettere minoccurs a 0 per il codice fiscale
	private String strCodiceFiscale = "00000000000";
	private BigDecimal cdnLavoratore = null;
	private BigDecimal prgDichDisponibilita = null;
	private String dataDid = null;
	private BigDecimal prgPattoDB = null;
	private String codTipoPattoDB = "";
	private String dataStipulaDB = "";
	private BigDecimal prgPattoLavNew = null;
	private String codcpitit = "";
	private ProfilingPatto patto = null;
	private String dataStipula = "";
	private BigDecimal prgColloquio = null;
	private BigDecimal prgSpi = null;
	private String codCodificaPatto = "";
	private String flgPatto297 = "";
	
	public RicezionePattoL14() {
	}
	
	public RicezionePattoL14(String strPattoL14) {
		
		try {
			System.out.println(processPattoL14(strPattoL14));
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String processPattoL14(String inputXML) throws Throwable {
		String outputXml = "";
		
		try {
			if (checkInputXml(inputXML)) {
				if (checkEsistenzaLavoratore()) {
					boolean checkDidSuperato = true;
					String dataPatto = "";
				   	ProfilingPatto pattoCurr = dettPattoL14.getProfilingPatto();
				   	if (pattoCurr != null) {
				   		dataPatto = DateUtils.formatXMLGregorian(pattoCurr.getDataPatto());	
				   	}
					if (checkDidAttiva(dataPatto)) {
						codCodificaPatto = "PT297";
						flgPatto297 = "S";
					} else {
						if (!dataPatto.equals("") && DateUtils.compare(dataPatto, MessageCodes.Patto.DATA_CHECK_COD_SERVIZIO) <= 0) {
							checkDidSuperato = false;
						}
						else {
							// CONTROLLARE LO STATO OCCUPAZIONALE DEL LAVORATORE ALLA DATA PATTO
							if (checkStatoDisoccupatoPatto150(dataPatto)) {
								codCodificaPatto = "PT297";
								flgPatto297 = "S";
							} else {
								codCodificaPatto = "PTGEN";
								flgPatto297 = "N";
							}
							
							
						}
					}	
					//if (checkDidAttivaCPICompetente()) {
					BigDecimal prgPattoFromAss = null;
					if (pattoCurr != null && pattoCurr.getPrgPatto() != null) {
						prgPattoFromAss = new BigDecimal(pattoCurr.getPrgPatto());
					}
					if (checkDidSuperato && checkPatto(prgPattoFromAss)) {
						gestisciL14();
					}		
					//}
				}
			} 		
		} catch (Exception e) {
			_logger.error("Errore generico");
			esito = "99";
		}
		
		outputXml = createXMLesito();
		
		if (!isXmlValid(outputXml, XSD_ESITO_PATTOL14)) {
			_logger.error("Errore nella validazione output xml");
			esito = "12";
			outputXml = createXMLesito();
		}
		
		return outputXml;
	}
	
	private final String createXMLesito() throws Exception {
		_logger.debug("createXMLesito() - start generazione xml ");
		String returnString = "";
				
		EsitoFlussoASSISTERSIL esitoAssister = new EsitoFlussoASSISTERSIL();
		
		if (ESITO_OK.equals(esito)){
			esitoAssister.setResponseCodiceEsito(ESITO_OK);
			esitoAssister.setCodiceFiscale(strCodiceFiscale);
			
			BigDecimal prgPattoToUse = prgPattoLavNew!=null?prgPattoLavNew:prgPattoDB;
			
			esitoAssister.setPrgPatto((prgPattoToUse).toBigInteger());
			esitoAssister.setPattoData(patto.getDataPatto());
			
			SourceBean strProtBean = (SourceBean)QueryExecutor.executeQuery("GET_INFO_PROTOCOLLAZIONE_DOC_L14", new Object[] { prgPattoToUse, codCodificaPatto }, "SELECT", Values.DB_SIL_DATI);
			String strProtPerAssister = (String) strProtBean.getAttribute("ROW.strProtPerAssister");
			esitoAssister.setPattoNumeroProtocollo(strProtPerAssister);
			
			SourceBean polAttiveBean = (SourceBean)QueryExecutor.executeQuery("LIST_POL_ATTIVE_L14_AGGIORNATE", new Object[] { prgColloquio }, "SELECT", Values.DB_SIL_DATI);
			Vector<SourceBean> polAttiveDB = polAttiveBean.getAttributeAsVector("ROW");
			EsitoFlussoASSISTERSIL.PoliticheAttive polAttive = new EsitoFlussoASSISTERSIL.PoliticheAttive();
			
			for (SourceBean polAttivaDB: polAttiveDB) {
				EsitoFlussoASSISTERSIL.PoliticheAttive.PoliticaAttiva polAttiva = new EsitoFlussoASSISTERSIL.PoliticheAttive.PoliticaAttiva();
				polAttiva.setTipologiaAzioneSifer(SourceBeanUtils.getAttrStr(polAttivaDB,"codAzioneSifer"));
				polAttiva.setMisura(SourceBeanUtils.getAttrStr(polAttivaDB,"codMisura"));
				polAttiva.setIdIntervento(SourceBeanUtils.getAttrBigDecimal(polAttivaDB, "idinterventoL14").toBigInteger());
				polAttiva.setPrgPercorso(SourceBeanUtils.getAttrBigDecimal(polAttivaDB, "prgpercorso").toBigInteger());
				polAttiva.setPrgColloquio(SourceBeanUtils.getAttrBigDecimal(polAttivaDB, "prgcolloquio").toBigInteger());
				polAttive.getPoliticaAttiva().add(polAttiva);
			}
			
			esitoAssister.setPoliticheAttive(polAttive);   		     		
		} else {
			esitoAssister.setResponseCodiceEsito(esito);
			esitoAssister.setCodiceFiscale(strCodiceFiscale);	
		}
		
		_logger.debug("createXMLesito() - end generazione xml ");
		
		JAXBContext jaxbContext;
		try {		
			jaxbContext = JAXBContext.newInstance(EsitoFlussoASSISTERSIL.class);
			StringWriter writer = new StringWriter();
			jaxbContext.createMarshaller().marshal(esitoAssister, writer);
			returnString = writer.toString();			
		} catch (JAXBException e) {
			_logger.error("Errore nella creazione dell'xml da inviare", e);
		}
		
		return returnString;			
	}
	
	private final void gestisciL14() {
		TransactionQueryExecutor transExec = null;
		
		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			patto = dettPattoL14.getProfilingPatto();
			dataStipula = DateUtils.formatXMLGregorian(patto.getDataPatto());
			
			BigDecimal prgPattoFromAss = null;
			if (patto != null && patto.getPrgPatto() != null) {
				prgPattoFromAss = new BigDecimal(patto.getPrgPatto());
			}
			
			// modalità inserimento/aggiornamento in base a prgPattoFromAss
			if (prgPattoFromAss == null) {
				// modalità inserimento patto/programma PL14/L14_2018
				// solo se non esiste un patto aperto, altrimenti devo inserire solo il programma
				if (prgPattoDB == null) {
					inserisciPattoL14(transExec);
					// prgPattoLavNew valorizzato in inserisciPattoL14
					gestisciSchedaPartecipante(transExec);
					gestisciColloquio(transExec);
					gestisciPoliticheAttive(transExec);
					gestisciProgramma(transExec);
					aggiornaDatiLavoratore(transExec);
					
					stampaPattoL14(transExec);
				}
				else {
					prgPattoLavNew = prgPattoDB;
					//esiste un patto aperto, quindi attenzione alla gestione scheda partecipante in gestisciSchedaPartecipante
					gestisciSchedaPartecipante(transExec);
					gestisciColloquio(transExec);
					gestisciPoliticheAttive(transExec);
					gestisciProgramma(transExec);
					aggiornaDatiLavoratore(transExec);
				}
			}
			else {
				// prgPattoLavNew = null
				gestisciSchedaPartecipante(transExec);
				gestisciColloquio(transExec);
				gestisciPoliticheAttive(transExec);
				gestisciProgramma(transExec);
				aggiornaDatiLavoratore(transExec);
			}
			
			transExec.commitTransaction();
		} catch (Exception e) {
			if (transExec != null) {
				try {
					transExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
					e1.printStackTrace();
				}
			}	
		}		
	}	
	
	private final void aggiornaDatiLavoratore(TransactionQueryExecutor txExec) throws Exception {
		try {	
			Partecipante partecipante = dettPattoL14.getPartecipante();
			
			String comcomres = partecipante.getResidenzaCodiceCatastale()!= null?partecipante.getResidenzaCodiceCatastale():null;
			String strindirizzores = partecipante.getResidenzaIndirizzo()!= null?partecipante.getResidenzaIndirizzo():null;
			String strcapres = partecipante.getResidenzaCap()!= null?partecipante.getResidenzaCap():null;
			
			if (comcomres != null && strindirizzores != null && strcapres != null) {
				Object resDatiLavRes = txExec.executeQuery("AGGIORNA_AN_LAVORATORE_L14_RES", 
						new Object[] { comcomres, strindirizzores, strcapres, CDN_UT_SIL_ASSISTER, cdnLavoratore }, "UPDATE");				

				if (resDatiLavRes == null || !(resDatiLavRes instanceof Boolean && ((Boolean) resDatiLavRes).booleanValue() == true)) {
					throw new Exception();
				}				
			}
			
			String comcomdom = partecipante.getDomicilioCodiceCatastale()!= null?partecipante.getDomicilioCodiceCatastale():null;
			String strindirizzodom = partecipante.getDomicilioIndirizzo()!= null?partecipante.getDomicilioIndirizzo():null;
			String strcapdom = partecipante.getDomicilioCap()!= null?partecipante.getDomicilioCap():null;
			
			if (comcomdom != null && strindirizzodom != null && strcapdom != null) {
				Object resDatiLavDom = txExec.executeQuery("AGGIORNA_AN_LAVORATORE_L14_DOM", 
						new Object[] { comcomdom, strindirizzodom, strcapdom, CDN_UT_SIL_ASSISTER, cdnLavoratore }, "UPDATE");				

				if (resDatiLavDom == null || !(resDatiLavDom instanceof Boolean && ((Boolean) resDatiLavDom).booleanValue() == true)) {
					throw new Exception();
				}				
			}
		} catch (Exception e) {		
			_logger.error("Errore in fase di aggiornamento dei dati del lavoratore");
			esito = "11";
			throw new Exception();
		}		
	}
	
	private final void gestisciProgramma(TransactionQueryExecutor txExec) throws Exception {
		try {
			String rif_pa = patto.getRifPa()!= null?patto.getRifPa():null;
			String codorganismo = null;
			String nome_responsabile = patto.getNomeResponsabile()!= null?patto.getNomeResponsabile():null;
			String cognome_responsabile = patto.getCognomeResponsabile()!= null?patto.getCognomeResponsabile():null;
			String email_responsabile = patto.getEmailResponsabile()!= null?patto.getEmailResponsabile():null;
			BigDecimal idprogramma = patto.getIdProgramma()!=null?new BigDecimal(patto.getIdProgramma()):null;
			String stato_programma = patto.getStatoProgramma()!= null?patto.getStatoProgramma():null;
					
			Object resProgramma = null;
			if (prgPattoLavNew != null) {
				resProgramma = txExec.executeQuery("INS_AM_DATI_PROGRAMMA_L14", 
						new Object[] { prgPattoLavNew, rif_pa, codorganismo, nome_responsabile, cognome_responsabile, email_responsabile, 
										idprogramma, stato_programma, CDN_UT_SIL_ASSISTER, CDN_UT_SIL_ASSISTER }, "INSERT");				
			} else {
				resProgramma = txExec.executeQuery("UPDATE_AM_DATI_PROGRAMMA_L14", 
						new Object[] { rif_pa, codorganismo, nome_responsabile, cognome_responsabile, email_responsabile, 
										idprogramma, stato_programma, CDN_UT_SIL_ASSISTER, prgPattoDB }, "UPDATE");				
			}
			
			if (resProgramma == null || !(resProgramma instanceof Boolean && ((Boolean) resProgramma).booleanValue() == true)) {
				throw new Exception();
			}			
		} catch (Exception e) {		
			_logger.error("Errore in fase di inserimento/aggiornamento del programma");
			esito = "10";
			throw new Exception();
		}		
	}
	
	private final void gestisciPoliticheAttive(TransactionQueryExecutor txExec) throws Exception {
		try {
			PoliticheAttive polAttiveBean = patto.getPoliticheAttive();
			
			String codServizio = TIPO_PATTO_L14;
			if (DateUtils.compare(dataStipula, MessageCodes.Patto.DATA_CHECK_COD_SERVIZIO_2020) >= 0) {
				codServizio = TIPO_PATTO_L14_2019;
			}else {
				if (DateUtils.compare(dataStipula, MessageCodes.Patto.DATA_CHECK_COD_SERVIZIO) > 0) { 
					codServizio = TIPO_PATTO_L14_2018;
				}
			}	
				
			
			if (polAttiveBean != null) {
				List<PoliticaAttiva> polAttive = polAttiveBean.getPoliticaAttiva();
				for( PoliticaAttiva polAttiva : polAttive ) {
					if (prgPattoLavNew != null) {
						gestisciPercorso(txExec, polAttiva, null,codServizio);
					} else {
						if (polAttiva.getPrgcolloquio() != null) {
							prgColloquio = new BigDecimal(polAttiva.getPrgcolloquio());
						} else {
													
							SourceBean colloquioBean = (SourceBean) txExec.executeQuery("CHECK_OR_COLLOQUIO_L14", new Object[] { cdnLavoratore, codServizio, dataStipula }, "SELECT");
							if (colloquioBean.containsAttribute("ROW")) {	
								prgColloquio = (BigDecimal) colloquioBean.getAttribute("ROW.prgColloquio");
							} else throw new Exception();
						}
												
						if (polAttiva.getPrgpercorso() != null) {
							BigDecimal prgPercorso = new BigDecimal(polAttiva.getPrgpercorso());
							SourceBean percorsoBean = (SourceBean) txExec.executeQuery("CHECK_OR_PERCORSO_CONCORDATO", new Object[] { prgColloquio, prgPercorso }, "SELECT");
							if (percorsoBean.containsAttribute("ROW")) {	
								gestisciPercorso(txExec, polAttiva, prgPercorso,codServizio);
							} else throw new Exception();
						} else {
							gestisciPercorso(txExec, polAttiva, null,codServizio);
						}
					}
				}
			}
		} catch (Exception e) {		
			_logger.error("Errore in fase di inserimento/aggiornamento delle politiche attive");
			esito = "09";
			throw new Exception();
		}
	}
	
	private final void gestisciPercorso(TransactionQueryExecutor txExec, PoliticaAttiva polAttiva, BigDecimal prgPercorso, String codServizio) throws Exception {
		String tipoAzioneSifer = polAttiva.getTipologiaAzioneSifer();
		String misura = polAttiva.getMisura();
		
		SourceBean prgAzioniBean = (SourceBean) txExec.executeQuery("SELECT_PRGAZIONI_L14", new Object[] { tipoAzioneSifer, misura, codServizio }, "SELECT");
		BigDecimal prgAzioni = (BigDecimal) prgAzioniBean.getAttribute("ROW.prgAzioni");
		
		String tipoDurata = polAttiva.getTipologiaDurata()!= null?polAttiva.getTipologiaDurata():"";
		String codTipologiaDurata = null;
		
		if (tipoDurata.equals("GG")) {
			codTipologiaDurata = "G";
		} else if (tipoDurata.equals("HH")) {
			codTipologiaDurata = "O";
		} else if (tipoDurata.equals("MM")) {
			codTipologiaDurata = "M";
		}
		
		BigDecimal numygdurataeff = polAttiva.getDurataEffettiva()!= null?new BigDecimal(polAttiva.getDurataEffettiva()):null;
		BigDecimal idinterventoL14 = new BigDecimal(polAttiva.getIdIntervento());
		BigDecimal codOrganismo = new BigDecimal(polAttiva.getCodiceOrganismo());
		BigDecimal codDistretto = new BigDecimal(polAttiva.getCodiceDistretto());
		
		String codEsito = polAttiva.getEsito()!= null?polAttiva.getEsito():"";
		String codesitorendicont = null;
		BigDecimal prgSpiProposta = null;
		BigDecimal prgSpiConclusione = null;
		
		if (codEsito.equals("PRO")) {
			codesitorendicont = "P";
			prgSpiProposta = prgSpi;
		} else if (codEsito.equals("NA")) {
			codesitorendicont = "A";
		} else if (codEsito.equals("RIF")) {
			codesitorendicont = "A";
			prgSpiProposta = prgSpi;
			prgSpiConclusione = prgSpi;
		}		

		Object resPercorso = null;
		BigDecimal newPrgPercorso = null;
		if (prgPercorso == null) {
			SourceBean resNewPrgPercorso = (SourceBean) txExec.executeQuery("OR_PERCORSO_CONCORDATO_NEXTVAL", null, "SELECT");
			newPrgPercorso = (BigDecimal) resNewPrgPercorso.getAttribute("ROW.do_nextval");
			
			resPercorso = txExec.executeQuery("INSERT_PERCORSO_L14", 
					new Object[] { prgColloquio, newPrgPercorso, codEsito, dataStipula, prgAzioni, numygdurataeff, codTipologiaDurata, idinterventoL14, 
									codesitorendicont, prgSpiProposta, prgSpiConclusione, codOrganismo, codDistretto, CDN_UT_SIL_ASSISTER, CDN_UT_SIL_ASSISTER }, "INSERT");
		} else {
			resPercorso = txExec.executeQuery("UPDATE_PERCORSO_L14", 
					new Object[] { codEsito, dataStipula, prgAzioni, numygdurataeff, codTipologiaDurata, idinterventoL14, 
									codesitorendicont, prgSpiProposta, prgSpiConclusione, codOrganismo, codDistretto, CDN_UT_SIL_ASSISTER, prgColloquio, prgPercorso }, "UPDATE");			
		}
		if (resPercorso == null || !(resPercorso instanceof Boolean && ((Boolean) resPercorso).booleanValue() == true)) {
			throw new Exception();
		}
		
		if (prgPercorso == null) {
			Object resPattoScelta = txExec.executeQuery("INS_PATTO_SCELTA_WS", new Object[] { prgPattoLavNew!=null?prgPattoLavNew:prgPattoDB, "OR_PER", newPrgPercorso }, "INSERT");			
			if (resPattoScelta == null || !(resPattoScelta instanceof Boolean && ((Boolean) resPattoScelta).booleanValue() == true)) {
				throw new Exception();
			}		
			
			SourceBean codImpegniBean = (SourceBean) txExec.executeQuery("SELECT_DE_IMPEGNI_WS", new Object[] { prgAzioni }, "SELECT");
			Vector<SourceBean> codImpegni = (Vector<SourceBean>) codImpegniBean.getAttributeAsVector("ROW");
			
			for (int j = 0; j < codImpegni.size(); j++) {
				String codImpegno = (String)((SourceBean)codImpegni.get(j)).getAttribute("codImpegno");

				SourceBean codImpegnoDB = (SourceBean) txExec.executeQuery("SELECT_IMPEGNI_WS", new Object[] { prgPattoLavNew!=null?prgPattoLavNew:prgPattoDB, codImpegno }, "SELECT");
				Vector vectCodImpegnoDB = codImpegnoDB.getAttributeAsVector("ROW");

				boolean isImpegnoEsistente = (codImpegnoDB != null) && (vectCodImpegnoDB.size() > 0);

				if (!isImpegnoEsistente) {
					Object resImpegno = txExec.executeQuery("INS_PATTO_SCELTA_WS", new Object[] { prgPattoLavNew!=null?prgPattoLavNew:prgPattoDB, "DE_IMPE", codImpegno }, "INSERT");			
					if (resImpegno == null || !(resImpegno instanceof Boolean && ((Boolean) resImpegno).booleanValue() == true)) {
						throw new Exception();
					}					
				}
			}
		}
	}
	
	private final void gestisciColloquio(TransactionQueryExecutor txExec) throws Exception {
		boolean failCheck = false;
		try {	
			SourceBean prgSpiBean = (SourceBean) txExec.executeQuery("SELECT_PRGSPI_SYSTEM", new Object[] {}, "SELECT");
			prgSpi = (BigDecimal) prgSpiBean.getAttribute("ROW.prgspi");
			
			if (prgPattoLavNew != null) {
				SourceBean prgColloquioBean = (SourceBean) txExec.executeQuery("OR_COLLOQUIO_NEXTVAL", new Object[] {}, "SELECT");
				prgColloquio = (BigDecimal) prgColloquioBean.getAttribute("ROW.do_nextval");

				String codServizio = TIPO_PATTO_L14;
				if (DateUtils.compare(dataStipula, MessageCodes.Patto.DATA_CHECK_COD_SERVIZIO_2020) >= 0) {
					codServizio =  TIPO_PATTO_L14_2019;
				}else {
					if (DateUtils.compare(dataStipula, MessageCodes.Patto.DATA_CHECK_COD_SERVIZIO) > 0) { 
						codServizio =  TIPO_PATTO_L14_2018;
					}
				}
				
				SourceBean rowsIni = (SourceBean) txExec.executeQuery("GET_SERVIZIO_INI", new Object[] {codServizio}, "SELECT");
				if (rowsIni.containsAttribute("ROW.codtsprogramma")) {
					String codTsProgramma = Utils.notNull(rowsIni.getAttribute("ROW.codtsprogramma"));
					failCheck = checkCondizioniInserimentoProgrammaINI(txExec, codTsProgramma);
					if (failCheck) throw new Exception();
				}
				
				if (!failCheck) {
					String dataFineProgramma = (patto != null && patto.getDataFinePatto()!= null)?DateUtils.formatXMLGregorian(patto.getDataFinePatto()):null;
					
					Object resColloquio = txExec.executeQuery("INSERT_COLLOQUIO_L14", 
											new Object[] { prgColloquio, cdnLavoratore, codcpitit, dataStipula, prgSpi, codServizio, CDN_UT_SIL_ASSISTER, CDN_UT_SIL_ASSISTER, dataFineProgramma }, "INSERT");
					
					if (resColloquio == null || !(resColloquio instanceof Boolean && ((Boolean) resColloquio).booleanValue() == true)) {
						throw new Exception();
					}
					
					Object resSchedaColloquio = txExec.executeQuery("INSERT_SCHEDA_COLLOQUIO_WS", new Object[] { prgColloquio }, "INSERT");
	
					if (resSchedaColloquio == null || !(resSchedaColloquio instanceof Boolean && ((Boolean) resSchedaColloquio).booleanValue() == true)) {
						throw new Exception();
					}			
				}
			}
			else {
				// in fase di aggiornamento programma prgPattoLavNew = null
				String dataFineProgramma = (patto != null && patto.getDataFinePatto()!= null)?DateUtils.formatXMLGregorian(patto.getDataFinePatto()):null;
				if (dataFineProgramma != null) {
					BigDecimal prgColloquioL14 = null;
					PoliticheAttive polAttiveBean = patto.getPoliticheAttive();
					if (polAttiveBean != null) {
						List<PoliticaAttiva> polAttive = polAttiveBean.getPoliticaAttiva();
		  				if (polAttive.size() > 0) {
		  					PoliticaAttiva polAttiva = polAttive.get(0);
		 					prgColloquioL14 = new BigDecimal(polAttiva.getPrgcolloquio());
		  				}
					}
					if (prgColloquioL14 != null) {
			  			chiudiProgramma(prgColloquioL14, txExec);
			  		}
				}
			}
		} catch (Exception e) {		
			if (!failCheck){
				_logger.error("Errore in fase di inserimento del colloquio");
				esito = "08";
			}
			throw new Exception();
		}
	}
	
	private final void gestisciSchedaPartecipante(TransactionQueryExecutor txExec) throws Exception {
		try {
			String codContratto = patto.getContratto()!= null?patto.getContratto():null;
			String codstudio = patto.getTitoloStudioPatto();
			String coddurata = patto.getDurataRicercaOccupazione()!= null?patto.getDurataRicercaOccupazione():null;
			String codoccupazione = patto.getCondizioneOccupazionale();
			Object resScheda;
			//BigDecimal prgPattoToUse = null;
			if (prgPattoLavNew != null) {
				// prgPattoLavNew != null quando abbiamo inserito il patto oppure quando esiste un patto aperto sul sil
				SourceBean schedaBean = (SourceBean) txExec.executeQuery("GET_SCHEDA_PARTECIPANTE_PATTO", new Object[] {prgPattoLavNew}, "SELECT");
				boolean inserisciScheda = true;
				if (schedaBean != null && schedaBean.getAttribute("ROW.PRGPATTOLAVORATORE") != null) {
					inserisciScheda = false;
				}
				if (inserisciScheda) {
					// caso in cui abbiamo inserito il patto 
					resScheda = txExec.executeQuery("INSERT_SCHEDA_PARTECIPANTE_L14", 
							new Object[] { prgPattoLavNew, codContratto, codstudio, coddurata, codoccupazione, CDN_UT_SIL_ASSISTER, CDN_UT_SIL_ASSISTER }, "INSERT");
					if (resScheda == null || !(resScheda instanceof Boolean && ((Boolean) resScheda).booleanValue() == true)) {
						throw new Exception();
					}
					Svantaggi svantaggiBean = patto.getSvantaggi();
					if (svantaggiBean != null) {
						List<String> svantaggi = svantaggiBean.getTipoSvantaggio();
						for( String svantaggio : svantaggi ) {
							Object resSvantaggio = txExec.executeQuery("INSERT_SVANTAGGIO_SCHEDA", 
									new Object[] { prgPattoLavNew, svantaggio, CDN_UT_SIL_ASSISTER, CDN_UT_SIL_ASSISTER }, "INSERT");
							if (resSvantaggio == null || !(resSvantaggio instanceof Boolean && ((Boolean) resSvantaggio).booleanValue() == true)) {
								throw new Exception();
							}
						}			
					}
				}
			} 
			//else {
			//	resScheda = txExec.executeQuery("UPDATE_SCHEDA_PARTECIPANTE_L14",  
			//			new Object[] { codContratto, codstudio, coddurata, codoccupazione, CDN_UT_SIL_ASSISTER, prgPattoDB }, "UPDATE");
			//}	
			
//			if (prgPattoLavNew == null) {
//				Object resDelSvantaggi = txExec.executeQuery("DELETE_SVANTAGGI_SCHEDA", new Object[] { prgPattoDB }, "DELETE");
//				if (resDelSvantaggi == null || !(resDelSvantaggi instanceof Boolean && ((Boolean) resDelSvantaggi).booleanValue() == true)) {
//					throw new Exception();
//				}
//				prgPattoToUse = prgPattoDB;
//			} else {
//				prgPattoToUse = prgPattoLavNew;
//			}
			
//			Svantaggi svantaggiBean = patto.getSvantaggi();
//			if (svantaggiBean != null) {
//				List<String> svantaggi = svantaggiBean.getTipoSvantaggio();
//				for( String svantaggio : svantaggi ) {
//					Object resSvantaggio = txExec.executeQuery("INSERT_SVANTAGGIO_SCHEDA", 
//							new Object[] { prgPattoToUse, svantaggio, CDN_UT_SIL_ASSISTER, CDN_UT_SIL_ASSISTER }, "INSERT");
//					if (resSvantaggio == null || !(resSvantaggio instanceof Boolean && ((Boolean) resSvantaggio).booleanValue() == true)) {
//						throw new Exception();
//					}
//				}			
//			}
		} catch (Exception e) {		
			_logger.error("Errore in fase di inserimento/aggiornamento della scheda partecipante");
			esito = "07";
			throw new Exception();
		}
	}
	
	private final void chiudiPattoNonL14(TransactionQueryExecutor txExec) throws Exception {
		if (DateUtils.compare(dataStipulaDB, dataStipula) < 0 ) {
			Object queryPattoChiusuraNonL14 = txExec.executeQuery("CHIUDI_PATTO_NO_L14", new Object[] { dataStipula, CDN_UT_SIL_ASSISTER, prgPattoDB }, "UPDATE");
			if (queryPattoChiusuraNonL14 == null || !(queryPattoChiusuraNonL14 instanceof Boolean && ((Boolean) queryPattoChiusuraNonL14).booleanValue() == true)) {
				_logger.error("Errore chiusura patto precedente non L14");
				esito = "06";
				throw new Exception();
			}
		} else {
			_logger.error("Lavoratore possiede già un patto con data stipula uguale o successiva al patto da inserire");
			esito = "15";
			throw new Exception();			
		}
	}
	
	private final void chiudiPattoL14(TransactionQueryExecutor txExec) throws Exception {
		String dataFinePatto = DateUtils.formatXMLGregorian(patto.getDataFinePatto());
		Object queryPattoChiusuraL14 = txExec.executeQuery("CHIUDI_PATTO_L14", new Object[] { dataFinePatto, CDN_UT_SIL_ASSISTER, prgPattoDB }, "UPDATE");
		if (queryPattoChiusuraL14 == null || !(queryPattoChiusuraL14 instanceof Boolean && ((Boolean) queryPattoChiusuraL14).booleanValue() == true)) {
			_logger.error("Errore chiusura patto L14");
			esito = "14";
			throw new Exception();
		}
	}
	
	private final void chiudiProgramma(BigDecimal prgColloquioL14, TransactionQueryExecutor txExec) throws Exception {
		String dataFinePatto = DateUtils.formatXMLGregorian(patto.getDataFinePatto());
	  	SourceBean colloquioBean = (SourceBean) txExec.executeQuery("GET_INFO_COLLOQUIO", new Object[] { prgColloquioL14 }, "SELECT");
	  	BigDecimal numklocoll = (BigDecimal) colloquioBean.getAttribute("ROW.NUMKLOCOLLOQUIO");
	  	numklocoll = numklocoll.add(new BigDecimal(1D));
	 
  		Object queryChiusuraL14 = txExec.executeQuery("UPDATE_RIAPRI_CHIUDI_PROGRAMMA", new Object[] { dataFinePatto, numklocoll, CDN_UT_SIL_ASSISTER, prgColloquioL14 }, "UPDATE");
  		
  		if (queryChiusuraL14 == null || !(queryChiusuraL14 instanceof Boolean && ((Boolean) queryChiusuraL14).booleanValue() == true)) {
  			_logger.error("Errore chiusura programma L14");
  			esito = "14";
  			throw new Exception();
  		}
	}
	
	private final void inserisciPattoL14(TransactionQueryExecutor txExec) throws Exception {
		try {
			SourceBean profilingBean = (SourceBean) txExec.executeQuery("GET_DATI_PROFILING_DID", new Object[] { cdnLavoratore }, "SELECT");
			BigDecimal indiceSvantaggio150 = profilingBean.containsAttribute("ROW.decprofiling")?(BigDecimal) profilingBean.getAttribute("ROW.decprofiling"):null;
		    String dataRiferimentoIndice150 = profilingBean.containsAttribute("ROW.datprofiling")?(String) profilingBean.getAttribute("ROW.datprofiling"):null;
		    BigDecimal numProfiling150 = profilingBean.containsAttribute("ROW.numprofiling")?(BigDecimal) profilingBean.getAttribute("ROW.numprofiling"):null;
			
			SourceBean prgPattoBean = (SourceBean) txExec.executeQuery("GET_NEW_PATTOLAV", new Object[] {}, "SELECT");
			prgPattoLavNew = (BigDecimal) prgPattoBean.getAttribute("ROW.prgPattoLavoratore");
		
			String dataFinePatto = null;
			String codMotivoFineAtto = null;
			
			Object resPatto = txExec.executeQuery("INSERT_PATTOLAV_L14", 
									new Object[] { prgPattoLavNew, cdnLavoratore, codcpitit, prgDichDisponibilita, flgPatto297, dataStipula, dataFinePatto, codMotivoFineAtto, codCodificaPatto,
												   indiceSvantaggio150, dataRiferimentoIndice150, numProfiling150, CDN_UT_SIL_ASSISTER, CDN_UT_SIL_ASSISTER }, "INSERT");
			
			if (resPatto == null || !(resPatto instanceof Boolean && ((Boolean) resPatto).booleanValue() == true)) {
				throw new Exception();
			}	
			
		} catch (Exception e) {		
			_logger.error("Errore in fase di inserimento del patto");
			esito = "05";
			throw new Exception();
		}		
	}
	
	private final void stampaPattoL14(TransactionQueryExecutor txExec) throws Exception {
		try {
			UserBean usr = new UserBean(CDN_UT_SIL_ASSISTER, cdnLavoratore);
			RequestContainer requestContainer = new RequestContainer();
			SessionContainer sessionContainer = new SessionContainer(true);
	   		sessionContainer.setAttribute("_CDUT_", CDN_UT_SIL_ASSISTER);
	   		sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
	   		sessionContainer.setAttribute(User.USERID, usr.getUser());
	   		requestContainer.setSessionContainer(sessionContainer);
	   		SourceBean request = new SourceBean("SERVICE_REQUEST");
	   		SourceBean response = new SourceBean("SERVICE_RESPONSE");
	   		request.setAttribute("datStipula", dataStipula);
	   		request.setAttribute("cdnLavoratore", cdnLavoratore.toString());
	   		requestContainer.setServiceRequest(request);
	   		RequestContainer.setRequestContainer(requestContainer);
	   		
	   		if (codCodificaPatto.equalsIgnoreCase("PTGEN")) {
	   			setReportPath("patto/accordo_CC.rpt");
	   		}
	   		else {
	   			setReportPath("patto/patto_CC.rpt");
	   		}
	   		
	   		SourceBean rowProt = (SourceBean)txExec.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
			if (rowProt == null) {
				throw new Exception("impossibile recuperare il numero di protocollo");
			}
			rowProt = (rowProt.containsAttribute("ROW")?(SourceBean)rowProt.getAttribute("ROW"):rowProt);
			BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
			BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
			String datProtocollazione = (String ) rowProt.getAttribute("DATAORAPROT");
			String tipoDocPatto = codCodificaPatto;
			if (codCodificaPatto.equalsIgnoreCase("PTGEN")) {
				tipoDocPatto = "ACLA";
			}
			
			com.inet.report.Engine eng = PattoBean.eseguiStampaPattoConApi(txExec, codcpitit, cdnLavoratore, dataStipula, prgPattoLavNew, numAnnoProt, numProtocollo,
					datProtocollazione, requestContainer, request, response, getDocumento(), tipoDocPatto, CDN_UT_SIL_ASSISTER, dataDid);
			
			Map prompts = new HashMap();
			
			String descrizioneInOut = "Output";
			prompts.put("numProt", numProtocollo);
			prompts.put("numAnnoProt", numAnnoProt);
			prompts.put("dataProt", datProtocollazione);
			prompts.put("docInOut", descrizioneInOut);
			
			setPromptFields(prompts);
			
			if (insertDocument(request, response, txExec, eng)) {
				PattoBean.updateInfoProtocolloPatto(request, response, txExec);
			}
			else {
				throw new Exception("Errore protocollazione patto");
			}
				
			/*
			SourceBean prgDocumentoBean = (SourceBean) txExec.executeQuery("NEXT_S_AM_DOCUMENTO", new Object[] {}, "SELECT");
			BigDecimal newPrgDocumento = (BigDecimal) prgDocumentoBean.getAttribute("ROW.KEY");
			
			SourceBean infoProtBean = (SourceBean) txExec.executeQuery("GET_INFO_PROTOCOLLO_DOC_WS", new Object[] {}, "SELECT");
			BigDecimal numProt = (BigDecimal) infoProtBean.getAttribute("ROW.numprotocollo");	
			BigDecimal numkloprot = (BigDecimal) infoProtBean.getAttribute("ROW.numkloprotocollo");
			
			Object resDocumento = txExec.executeQuery("INSERT_AM_DOCUMENTO_L14", 
					new Object[] { newPrgDocumento, cdnLavoratore, codcpitit, codCodificaPatto, dataStipula, codcpitit, 
									dataStipula, numProt, CDN_UT_SIL_ASSISTER, CDN_UT_SIL_ASSISTER }, "INSERT");			

			if (resDocumento == null || !(resDocumento instanceof Boolean && ((Boolean) resDocumento).booleanValue() == true)) {
				throw new Exception();
			}
			
			SourceBean cdnComponenteBean = (SourceBean) txExec.executeQuery("GET_CDNCOMPONENTE", new Object[] { "pattolavdettagliopage" }, "SELECT");
			BigDecimal cdnComponente = (BigDecimal) cdnComponenteBean.getAttribute("ROW.cdnComponente");
			
			Object resDocumentoBlob = txExec.executeQuery("INSERT_AM_DOCUMENTO_BLOB_WS", new Object[] { newPrgDocumento }, "INSERT");
			
			if (resDocumentoBlob == null || !(resDocumentoBlob instanceof Boolean && ((Boolean) resDocumentoBlob).booleanValue() == true)) {
				throw new Exception();
			}
			
			Object resDocumentoColl = txExec.executeQuery("INSERT_AM_DOCUMENTO_COLL_WS", new Object[] { newPrgDocumento, cdnComponente, prgPattoLavNew }, "INSERT");
			
			if (resDocumentoColl == null || !(resDocumentoColl instanceof Boolean && ((Boolean) resDocumentoColl).booleanValue() == true)) {
				throw new Exception();
			}	
			
			Object resUpdInfoProt = txExec.executeQuery("UPDATE_AM_PROTOCOLLO_WS", new Object[] { numProt, numkloprot.add(new BigDecimal(1D)) }, "UPDATE");
			if (resUpdInfoProt == null || !(resUpdInfoProt instanceof Boolean && ((Boolean) resUpdInfoProt).booleanValue() == true)) {
				throw new Exception();
			}
			*/	
			
		} catch (Exception e) {		
			_logger.error("Errore in fase di protocollazione del patto");
			esito = "05";
			throw new Exception();
		}	
	}
	
  	private boolean checkCondizioniInserimentoProgrammaINI(TransactionQueryExecutor txExec, String codTsProgramma) throws Exception {
		boolean failCheck = false;
		
		SourceBean sb = (SourceBean) txExec.executeQuery("GET_CHECK_INS_QUERY_TSPROGRAMMA", new Object[] { "B", codTsProgramma }, "SELECT");
		String flgAttivo = SourceBeanUtils.getAttrStrNotNull(sb, "ROW.flgAttivo"); 
		
		if ("S".equals(flgAttivo)) {
			String strSqlQuery = SourceBeanUtils.getAttrStrNotNull(sb, "ROW.strSqlQuery");
			String strMessErrore = SourceBeanUtils.getAttrStrNotNull(sb, "ROW.strMessErrore");

			sb = (SourceBean) txExec.executeQueryByStringStatement(strSqlQuery, new Object[] { cdnLavoratore }, "SELECT");
			BigDecimal numProgrammi = SourceBeanUtils.getAttrBigDecimal(sb, "ROW.numProgrammi");			
			
			if (numProgrammi.intValue() > 0) {		
				_logger.error("Errore: " + strMessErrore);
				esito = "15";
				failCheck = true;
			}
		}	
		
		return failCheck;
	}	
	
	
	/**
	 * 
	 * @return setta i campi prgPattoDB, codTipoPattoDB, dataStipulaDB del patto aperto
	 * @throws Exception
	 */
	private final boolean checkPatto(BigDecimal prgPattoAss) throws Exception {
		boolean checkPatto = true;
		Object[] inputParameters = new Object[1];
		
		SourceBean pattoResult = null;
		
		if(prgPattoAss == null){
			// cerco patto aperto di quel lavoratore
			inputParameters[0] = cdnLavoratore;
			pattoResult = (SourceBean)QueryExecutor.executeQuery("GET_PATTO_APERTO_WS", inputParameters, "SELECT", Values.DB_SIL_DATI);
		}else{
			//ricerca puntuale sul patto avente prg prgPattoAss
			inputParameters[0] = prgPattoAss;
			pattoResult = (SourceBean)QueryExecutor.executeQuery("GET_PATTO_FROM_PRGASS", inputParameters, "SELECT", Values.DB_SIL_DATI);
		}
		
		if (pattoResult != null && pattoResult.containsAttribute("ROW")) {
	   		prgPattoDB = (BigDecimal) pattoResult.getAttribute("ROW.PRGPATTOLAVORATORE");
		   	codTipoPattoDB = (String) pattoResult.getAttribute("ROW.CODTIPOPATTO");
		   	dataStipulaDB = (String) pattoResult.getAttribute("ROW.DATASTIPULA");
		   	/*
		   	if (codTipoPattoDB.equals(TIPO_PATTO_L14)) {
		   		try {
		   			BigDecimal prgPattoFromAss = new BigDecimal(dettPattoL14.getProfilingPatto().getPrgPatto());
		   			if (!prgPattoFromAss.equals(prgPattoDB)) {
		   				throw new Exception();
		   			}
				} catch (Exception e) {		
					_logger.error("Lavoratore possiede già un patto valido L14");
					esito = "04";
					checkPatto = false;	
				}
		   	}
		   	*/
	   	}
	   
	   	//GET_PATTO_APERTO_FROM_PRGASS
		return checkPatto;	   	
	}	
	
	private final boolean checkDidAttiva(String dataPatto) throws Exception {
		boolean checkDidAttiva = true;
		Object[] inputParameters = new Object[1];
	   	inputParameters[0] = cdnLavoratore;
		   
	   	SourceBean didAttiva = (SourceBean)QueryExecutor.executeQuery("GET_DID_LAVORATORE_APERTA", inputParameters, "SELECT", Values.DB_SIL_DATI);
	   	BigDecimal prgDidAperta = null;
	   	if (didAttiva != null && didAttiva.containsAttribute("ROW")) {
	   		prgDidAperta = (BigDecimal) didAttiva.getAttribute("ROW.prgdichdisponibilita");
	   	}
	   	if (!dataPatto.equals("") && DateUtils.compare(dataPatto, MessageCodes.Patto.DATA_CHECK_COD_SERVIZIO) <= 0) {
	   		if (prgDidAperta == null) {
	   			_logger.error("Lavoratore non possiede una DID attiva protocollata");
				esito = "03";
				checkDidAttiva = false;
	   		}
	   	}
	   	else {
	   		if (prgDidAperta == null) {
	   			checkDidAttiva = false;
	   		}
		   	else {
		   		prgDichDisponibilita = (BigDecimal) didAttiva.getAttribute("ROW.prgdichdisponibilita");
		   		dataDid = (String) (String) didAttiva.getAttribute("ROW.dataDid");
		   	}
	   	}
		return checkDidAttiva;	   	
	}	
	
	private final boolean checkDidAttivaCPICompetente() throws Exception {
		boolean checkDidAttivaCPIComp = true;
		Partecipante partecipante = dettPattoL14.getPartecipante();
		String comcomres = partecipante.getResidenzaCodiceCatastale()!= null?partecipante.getResidenzaCodiceCatastale():null;
		Object[] inputParameters = new Object[2];
	   	inputParameters[0] = prgDichDisponibilita;
	   	inputParameters[1] = comcomres;
	   	
	   	SourceBean didAttivaCPIComp = (SourceBean)QueryExecutor.executeQuery("GET_DID_LAV_APERTA_CPI_COMP_L14", inputParameters, "SELECT", Values.DB_SIL_DATI);
	   	int numDidComp = SourceBeanUtils.getAttrInt(didAttivaCPIComp, "ROW.numDidComp", 0);
	   	
	   	if (numDidComp != 1) {
			_logger.error("Lavoratore possiede una DID attiva protocollata ma non stipulata al CPI di competenza del comune di residenza del lavoratore");
			esito = "13";
			checkDidAttivaCPIComp = false;
	   	} 
	   	
		return checkDidAttivaCPIComp;	   	
	}	
	
	private final boolean checkEsistenzaLavoratore() throws Exception {
		boolean checkokEL = true;
		strCodiceFiscale = dettPattoL14.getPartecipante().getCodiceFiscale();
		Object[] inputParameters = new Object[1];
	   	inputParameters[0] = strCodiceFiscale;
		   
	   	SourceBean lavoratore = (SourceBean)QueryExecutor.executeQuery("SELECT_AN_LAVORATORE", inputParameters, "SELECT", Values.DB_SIL_DATI);
	   	if (!lavoratore.containsAttribute("ROW.CDNLAVORATORE")) {
			_logger.error("Lavoratore non presente");
			checkokEL = false;
			esito = "02";
	   	} else {
			cdnLavoratore = (BigDecimal) lavoratore.getAttribute ("ROW.CDNLAVORATORE");
			SourceBean cpiTitBean = (SourceBean) QueryExecutor.executeQuery("GET_CPI_AN_LAVORATORE", new Object[] { cdnLavoratore }, "SELECT", Values.DB_SIL_DATI);
			codcpitit = (String) cpiTitBean.getAttribute("ROW.codcpitit");
	   	} 
	   	return checkokEL;
	}	
	
	private final boolean checkInputXml(String inputXML) throws Exception {
		boolean checkokinputXML = true;

		if (!isXmlValid(inputXML, XSD_PATTOL14)) {
			_logger.error("Errore nella validazione input xml");
			checkokinputXML = false;
			esito = "01";
		} else {
			dettPattoL14 = convertToPattoL14(inputXML); 
		}
		return checkokinputXML;
	}	
	
	private FlussoASSISTERSIL convertToPattoL14(String strpattoL14) {
		JAXBContext jaxbContext;
		FlussoASSISTERSIL pattoL14Type = null;
		try {

			jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Schema schema = getXsdSchema(XSD_PATTOL14);
			jaxbUnmarshaller.setSchema(schema);			
			pattoL14Type = (FlussoASSISTERSIL) jaxbUnmarshaller.unmarshal(new StringReader(strpattoL14));

		} catch (Exception e) {		
			_logger.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return pattoL14Type;
	}	
	
	public static Schema getXsdSchema(String xsdPath) throws SAXException {
		String schemaLang = "http://www.w3.org/2001/XMLSchema";
        SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
        File schemaFile = new File(xsdPath);
        StreamSource streamSource = new StreamSource(schemaFile);
        Schema schema = factory.newSchema(streamSource);
        return schema;
    }	
	
	protected boolean isXmlValid(String stringaXML, String schemaFile) {
		boolean checkokXML = true;
		try {
			Schema schema = getXsdSchema(schemaFile);
			Validator validator = schema.newValidator();
			StreamSource datiXmlStreamSource = new StreamSource(new StringReader(stringaXML.trim()));
			validator.validate(datiXmlStreamSource);
		}
		catch (Exception e) {
			String validityError = "Errore di validazione xml: "+ e.getMessage();
			_logger.error(validityError);
			_logger.warn(stringaXML);
			checkokXML = false;
		}		
		return checkokXML;
	}
	
	private final boolean checkStatoDisoccupatoPatto150(String dataPatto) {
		boolean checkStatoDisoccupato = false;
		Object[] inputParameters = new Object[3];
	   	inputParameters[0] = cdnLavoratore;
	   	inputParameters[1] = dataPatto;
	   	inputParameters[2] = dataPatto;
	   	SourceBean statoDisoccupatoBean = (SourceBean)QueryExecutor.executeQuery("GET_STATO_DISOCCUPATO_PATTO_150", inputParameters, "SELECT", Values.DB_SIL_DATI);
	   	int numStatoDisocc = SourceBeanUtils.getAttrInt(statoDisoccupatoBean, "ROW.numStatoDisocc", 0);

	   	if (numStatoDisocc > 0) {
			checkStatoDisoccupato = true;
	   	} 

		return checkStatoDisoccupato;	   	
	}	

}
