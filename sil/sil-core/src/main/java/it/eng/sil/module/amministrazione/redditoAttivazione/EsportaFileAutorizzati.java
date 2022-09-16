package it.eng.sil.module.amministrazione.redditoAttivazione;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dispatching.action.AbstractAction;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.util.ZipEntryFile;
import it.eng.sil.util.ZipPackager;
import it.eng.sil.util.xml.XMLValidator;

public class EsportaFileAutorizzati extends AbstractAction {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private File outFileAutorizzati;
	private final String SCHEMA_XSD_OUTPUT = "redditoAttivazione.xsd";

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EsportaFileAutorizzati.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		TransactionQueryExecutor txExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {
			BigDecimal prgRAFile = request.containsAttribute("nomeFile")
					? new BigDecimal(request.getAttribute("nomeFile").toString())
					: null;
			String numeroDelibera = request.containsAttribute("numeroDelibera")
					? request.getAttribute("numeroDelibera").toString()
					: null;
			String dataDelibera = request.containsAttribute("datDelibera")
					? request.getAttribute("datDelibera").toString()
					: null;
			BigDecimal annoDelibera = null;
			BigDecimal progressivo = null;
			BigDecimal numkloRA = null;

			// domanda del file da autorizzare attraverso la procedura
			ArrayList<SourceBean> domandeAutorizzate = new ArrayList<SourceBean>();
			// domanda del file da autorizzare perché provenienza manuale e flgEstrai = S
			ArrayList<SourceBean> domandeFlgEstrai = new ArrayList<SourceBean>();

			BigDecimal cdnUtente = (BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
			if (dataDelibera != null && !dataDelibera.equals("")) {
				annoDelibera = new BigDecimal(DateUtils.getAnno(dataDelibera));
			}
			if (prgRAFile != null) {
				Vector rows = getLavoratoriFile(prgRAFile);
				int numLavoratori = rows.size();
				for (int i = 0; i < numLavoratori; i++) {
					try {
						SourceBean domanda = (SourceBean) rows.get(i);
						String codProvenienza = domanda.containsAttribute("CODPROVENIENZA")
								? domanda.getAttribute("CODPROVENIENZA").toString()
								: "";
						String codStaoRA = domanda.containsAttribute("CODSTATORA")
								? domanda.getAttribute("CODSTATORA").toString()
								: "";
						String flgEstrai = domanda.containsAttribute("FLGESTRAIDATI")
								? domanda.getAttribute("FLGESTRAIDATI").toString()
								: "";

						if (codProvenienza.equalsIgnoreCase(Decodifica.Provenienza.AGG_MANUALE)
								&& codStaoRA.equalsIgnoreCase(Decodifica.Stato.AUTORIZZATO)) {
							if (flgEstrai.equalsIgnoreCase(Decodifica.Costanti.SI)) {
								domandeFlgEstrai.add(domanda);
							}
						} else {
							txExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
							txExec.initTransaction();

							progressivo = (BigDecimal) domanda.getAttribute("PRGREDDITOATTIVAZIONE");
							numkloRA = (BigDecimal) domanda.getAttribute("NUMKLORA");
							DomandaBean dm = new DomandaBean(domanda, txExec);
							String codStatoDomanda = dm.concediAutorizzazione();
							String motivoVerifica = null;
							String codiceProvenienza = null;
							Boolean res = null;

							if (!codStatoDomanda.equalsIgnoreCase(Decodifica.Stato.AUTORIZZATO)) {
								motivoVerifica = dm.getMotivoVerifica();
								if (codStatoDomanda.equalsIgnoreCase(Decodifica.Stato.DA_VERIFICARE)) {
									codiceProvenienza = Decodifica.Provenienza.AGG_MANUALE;
								}
								res = (Boolean) txExec.executeQuery("UPDATE_STATO_RA_AUTORIZZATI",
										new Object[] { codStatoDomanda, motivoVerifica, codiceProvenienza,
												codiceProvenienza, null, null, null, numkloRA, cdnUtente, progressivo },
										"UPDATE");
							} else {
								res = (Boolean) txExec.executeQuery("UPDATE_STATO_RA_AUTORIZZATI",
										new Object[] { codStatoDomanda, motivoVerifica, codiceProvenienza,
												codiceProvenienza, numeroDelibera, dataDelibera, annoDelibera, numkloRA,
												cdnUtente, progressivo },
										"UPDATE");
							}

							if (!res.booleanValue())
								throw new Exception("Impossibile procedere con l'operazione");

							txExec.commitTransaction();

							if (codStatoDomanda.equalsIgnoreCase(Decodifica.Stato.AUTORIZZATO)) {
								if (numeroDelibera != null && !numeroDelibera.equals("")) {
									domanda.updAttribute("STRNUMDELIBERA", numeroDelibera);
								}
								if (dataDelibera != null && !dataDelibera.equals("")) {
									domanda.updAttribute("DATESITODELIBERA", dataDelibera);
									domanda.updAttribute("NUMANNODELIBERA", annoDelibera);
								}
								domandeAutorizzate.add(domanda);
							}
						}
					} catch (Exception ex) {
						if (txExec != null) {
							txExec.rollBackTransaction();
						}
					}
				}

				// domande autorizzate dal file
				Domande domandeXML = new Domande();
				int numDomandeAutorizzate = domandeAutorizzate.size();
				for (int i = 0; i < numDomandeAutorizzate; i++) {
					SourceBean domanda = (SourceBean) domandeAutorizzate.get(i);
					it.eng.sil.module.amministrazione.redditoAttivazione.Domande.Domanda domandaXML = creaDomandaXML(
							domanda);
					domandeXML.getDomanda().add(domandaXML);
				}

				// domande manuali autorizzate dal file
				int numDomandeManuali = domandeFlgEstrai.size();
				for (int i = 0; i < numDomandeManuali; i++) {
					SourceBean domanda = (SourceBean) domandeFlgEstrai.get(i);
					BigDecimal prgRedditoAttivazione = (BigDecimal) domanda.getAttribute("PRGREDDITOATTIVAZIONE");
					BigDecimal numklo = (BigDecimal) domanda.getAttribute("NUMKLORA");

					Boolean res = (Boolean) QueryExecutor.executeQuery(
							"UPDATE_AUTORIZZATI_MANUALI_FILE_RA", new Object[] { Decodifica.Costanti.NO, numeroDelibera,
									dataDelibera, annoDelibera, numklo, cdnUtente, prgRedditoAttivazione },
							"UPDATE", Values.DB_SIL_DATI);

					if (res.booleanValue()) {
						it.eng.sil.module.amministrazione.redditoAttivazione.Domande.Domanda domandaXML = creaDomandaXML(
								domanda);
						domandeXML.getDomanda().add(domandaXML);
					}
				}

				// domande autorizzate da altri file
				rows = getLavoratoriAutorizzatiAltriFile(prgRAFile);
				numLavoratori = rows.size();
				for (int i = 0; i < numLavoratori; i++) {
					SourceBean domanda = (SourceBean) rows.get(i);
					BigDecimal prgRedditoAttivazione = (BigDecimal) domanda.getAttribute("PRGREDDITOATTIVAZIONE");
					BigDecimal numklo = (BigDecimal) domanda.getAttribute("NUMKLORA");

					Boolean res = (Boolean) QueryExecutor.executeQuery("UPDATE_FLGESTRAI_AUTORIZZATI_MANUALI",
							new Object[] { Decodifica.Costanti.NO, numklo, cdnUtente, prgRedditoAttivazione }, "UPDATE",
							Values.DB_SIL_DATI);

					if (res.booleanValue()) {
						it.eng.sil.module.amministrazione.redditoAttivazione.Domande.Domanda domandaXML = creaDomandaXML(
								domanda);
						domandeXML.getDomanda().add(domandaXML);
					}
				}

				JAXBContext context = JAXBContext.newInstance(Domande.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				outFileAutorizzati = File.createTempFile("AUTORIZZATIRA_" + prgRAFile, ".xml");
				FileOutputStream fileOut = new FileOutputStream(outFileAutorizzati);
				m.marshal(domandeXML, fileOut);
				fileOut.close();

				String fileAutorizzati = read(outFileAutorizzati);
				File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
						+ "xsd" + File.separator + "redditoAttivazione" + File.separator + SCHEMA_XSD_OUTPUT);
				String validityErrors = XMLValidator.getValidityErrors(fileAutorizzati, schemaFile);
				if (validityErrors != null && validityErrors.length() > 0) {
					String validityError = "Errore di validazione xml: " + validityErrors;
					_logger.error(validityError);
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, true,
							"Errore validazione xml del file autorizzati");
				} else {
					// lo setto nella response per scaricarlo
					ZipPackager zipPackager = new ZipPackager();
					ZipEntryFile[] filesToZip = new ZipEntryFile[1];
					filesToZip[0] = new ZipEntryFile(outFileAutorizzati.getName(),
							outFileAutorizzati.getAbsolutePath());
					File outputfile = File.createTempFile("EXPORT_AUTORIZZATI_", ".zip");
					zipPackager.zip(outputfile, filesToZip);
					response.setAttribute("fileListaAutorizzati", outputfile.getAbsolutePath());
				}
			}
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, "service(): Fallito esporta autorizzati.", ex);
		}
	}

	public Vector getLavoratoriFile(BigDecimal prgRAFile) throws Exception {
		Object params[] = new Object[1];
		params[0] = prgRAFile;
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_AUTORIZZAZIONE_LAV_FILE_RA", params, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i lavoratori da autorizzare nel file");
	}

	public Vector getLavoratoriAutorizzatiAltriFile(BigDecimal prgRAFile) throws Exception {
		Object params[] = new Object[1];
		params[0] = prgRAFile;
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_AUTORIZZAZIONE_LAV_ALTRI_FILE_RA", params,
				"SELECT", Values.DB_SIL_DATI);
		if (res != null)
			return res.getAttributeAsVector("ROW");
		else
			throw new Exception("impossibile leggere i lavoratori da autorizzare nel file");
	}

	public it.eng.sil.module.amministrazione.redditoAttivazione.Domande.Domanda creaDomandaXML(SourceBean domanda)
			throws Exception {
		it.eng.sil.module.amministrazione.redditoAttivazione.Domande.Domanda domandaXML = new it.eng.sil.module.amministrazione.redditoAttivazione.Domande.Domanda();
		String dataDomanda = domanda.containsAttribute("DATDOMANDA") ? domanda.getAttribute("DATDOMANDA").toString()
				: "";
		String codiceEnteAutonomo = domanda.containsAttribute("STRCODENTEAUTONOMO")
				? domanda.getAttribute("STRCODENTEAUTONOMO").toString()
				: "";
		String descEnteAutonomo = domanda.containsAttribute("STRENTEAUTONOMO")
				? domanda.getAttribute("STRENTEAUTONOMO").toString()
				: "";
		String matricola = domanda.containsAttribute("STRMATRICOLAINPS")
				? domanda.getAttribute("STRMATRICOLAINPS").toString()
				: "";
		String cfBeneficiario = domanda.containsAttribute("STRCFAZIENDA")
				? domanda.getAttribute("STRCFAZIENDA").toString()
				: "";
		String ragSocBeneficiario = domanda.containsAttribute("STRRAGSOCAZIENDA")
				? domanda.getAttribute("STRRAGSOCAZIENDA").toString()
				: "";
		String numDelibera = domanda.containsAttribute("STRNUMDELIBERA")
				? domanda.getAttribute("STRNUMDELIBERA").toString()
				: "";
		BigDecimal annoDelibera = domanda.containsAttribute("NUMANNODELIBERA")
				? (BigDecimal) domanda.getAttribute("NUMANNODELIBERA")
				: null;
		String strAnnoDelibera = "";
		if (annoDelibera != null) {
			strAnnoDelibera = annoDelibera.toString();
		}
		String dataEsitoDelibera = domanda.containsAttribute("DATESITODELIBERA")
				? domanda.getAttribute("DATESITODELIBERA").toString()
				: "";
		String dataInizioPrestazione = domanda.containsAttribute("DATINIZIOPRESTAZIONE")
				? domanda.getAttribute("DATINIZIOPRESTAZIONE").toString()
				: "";
		String dataFinePrestazione = domanda.containsAttribute("DATFINEPRESTAZIONE")
				? domanda.getAttribute("DATFINEPRESTAZIONE").toString()
				: "";
		BigDecimal durataPrestazione = domanda.containsAttribute("NUMDURATAPRESTAZIONE")
				? (BigDecimal) domanda.getAttribute("NUMDURATAPRESTAZIONE")
				: null;
		String strDurataPrestazione = "";
		if (durataPrestazione != null) {
			strDurataPrestazione = durataPrestazione.toString();
		}
		String tipoPrestazione = domanda.containsAttribute("STRTIPOPRESTAZIONE")
				? domanda.getAttribute("STRTIPOPRESTAZIONE").toString()
				: "";
		String cognome = domanda.containsAttribute("STRCOGNOME") ? domanda.getAttribute("STRCOGNOME").toString() : "";
		String nome = domanda.containsAttribute("STRNOME") ? domanda.getAttribute("STRNOME").toString() : "";
		String dataNascita = domanda.containsAttribute("DATNASCITA") ? domanda.getAttribute("DATNASCITA").toString()
				: "";
		String cfLav = domanda.containsAttribute("STRCODICEFISCALE")
				? domanda.getAttribute("STRCODICEFISCALE").toString()
				: "";
		String comuneNasc = domanda.containsAttribute("STRCOMUNENASC")
				? domanda.getAttribute("STRCOMUNENASC").toString()
				: "";
		String provNasc = domanda.containsAttribute("STRPROVINCIANASC")
				? domanda.getAttribute("STRPROVINCIANASC").toString()
				: "";
		String statoNasc = domanda.containsAttribute("CODSTATONASCITA")
				? domanda.getAttribute("CODSTATONASCITA").toString()
				: "";
		String indirizzo = domanda.containsAttribute("STRINDIRIZZO") ? domanda.getAttribute("STRINDIRIZZO").toString()
				: "";
		String cap = domanda.containsAttribute("STRCAP") ? domanda.getAttribute("STRCAP").toString() : "";
		String comune = domanda.containsAttribute("STRCOMUNE") ? domanda.getAttribute("STRCOMUNE").toString() : "";
		String provincia = domanda.containsAttribute("STRPROVINCIA") ? domanda.getAttribute("STRPROVINCIA").toString()
				: "";
		String telefono = domanda.containsAttribute("STRTELBENEFICIARIO")
				? domanda.getAttribute("STRTELBENEFICIARIO").toString()
				: "";
		String email = domanda.containsAttribute("STREMAILBENEFICIARIO")
				? domanda.getAttribute("STREMAILBENEFICIARIO").toString()
				: "";
		String ibanNazione = domanda.containsAttribute("STRIBANNAZIONE")
				? domanda.getAttribute("STRIBANNAZIONE").toString()
				: "";
		String ibanControllo = domanda.containsAttribute("STRIBANCONTROLLO")
				? domanda.getAttribute("STRIBANCONTROLLO").toString()
				: "";
		String cin = domanda.containsAttribute("STRCINLAVORATORE") ? domanda.getAttribute("STRCINLAVORATORE").toString()
				: "";
		String abi = domanda.containsAttribute("STRABILAVORATORE") ? domanda.getAttribute("STRABILAVORATORE").toString()
				: "";
		String cab = domanda.containsAttribute("STRCABLAVORATORE") ? domanda.getAttribute("STRCABLAVORATORE").toString()
				: "";
		String cc = domanda.containsAttribute("STRCCLAVORATORE") ? domanda.getAttribute("STRCCLAVORATORE").toString()
				: "";
		BigDecimal lordoGG = domanda.containsAttribute("DECIMPORTOLORDOGIORNALIERO")
				? (BigDecimal) domanda.getAttribute("DECIMPORTOLORDOGIORNALIERO")
				: null;
		BigDecimal lordoComplessivo = domanda.containsAttribute("DECIMPORTOLORDOCOMPLESSIVO")
				? (BigDecimal) domanda.getAttribute("DECIMPORTOLORDOCOMPLESSIVO")
				: null;
		String strLordoGG = "";
		String strLordoComplessivo = "";
		if (lordoGG != null) {
			strLordoGG = lordoGG.toString();
		}
		if (lordoComplessivo != null) {
			strLordoComplessivo = lordoComplessivo.toString();
		}

		//
		if (!dataDomanda.equals("")) {
			String strDataApp = dataDomanda.substring(6, 10) + "-" + dataDomanda.substring(3, 5) + "-"
					+ dataDomanda.substring(0, 2);
			domandaXML.setDataDomandaPrestazione(strDataApp);
		} else {
			domandaXML.setDataDomandaPrestazione(dataDomanda);
		}
		domandaXML.setCodiceEnteAutonomo(codiceEnteAutonomo);
		domandaXML.setDescrizioneEnteAutonomo(descEnteAutonomo);
		domandaXML.setMatricolaInpsAziendaBenficiario(matricola);
		domandaXML.setCodiceFiscaleAziendaBeneficiario(cfBeneficiario);
		domandaXML.setRagioneSocialeAziendaDelBeneficiario(ragSocBeneficiario);
		domandaXML.setNumeroDeliberaEnteAutonomo(numDelibera);
		domandaXML.setAnnoDeliberaEnteAutonomo(strAnnoDelibera);
		if (!dataEsitoDelibera.equals("")) {
			String strDataApp = dataEsitoDelibera.substring(6, 10) + "-" + dataEsitoDelibera.substring(3, 5) + "-"
					+ dataEsitoDelibera.substring(0, 2);
			domandaXML.setDataEsitoDeliberaEnteAutonomo(strDataApp);
		} else {
			domandaXML.setDataEsitoDeliberaEnteAutonomo(dataEsitoDelibera);
		}
		if (!dataInizioPrestazione.equals("")) {
			String strDataApp = dataInizioPrestazione.substring(6, 10) + "-" + dataInizioPrestazione.substring(3, 5)
					+ "-" + dataInizioPrestazione.substring(0, 2);
			domandaXML.setDataInizioPrestazione(strDataApp);
		} else {
			domandaXML.setDataInizioPrestazione(dataInizioPrestazione);
		}
		if (!dataFinePrestazione.equals("")) {
			String strDataApp = dataFinePrestazione.substring(6, 10) + "-" + dataFinePrestazione.substring(3, 5) + "-"
					+ dataFinePrestazione.substring(0, 2);
			domandaXML.setDataFinePrestazione(strDataApp);
		} else {
			domandaXML.setDataFinePrestazione(dataFinePrestazione);
		}
		domandaXML.setDurataPrestazione(strDurataPrestazione);
		domandaXML.setTipoPrestazione(tipoPrestazione);
		domandaXML.setCognome(cognome);
		domandaXML.setNome(nome);
		if (!dataNascita.equals("")) {
			String strDataApp = dataNascita.substring(6, 10) + "-" + dataNascita.substring(3, 5) + "-"
					+ dataNascita.substring(0, 2);
			domandaXML.setDataDiNascita(strDataApp);
		} else {
			domandaXML.setDataDiNascita(dataNascita);
		}
		domandaXML.setCodiceFiscale(cfLav);
		domandaXML.setComuneNascita(comuneNasc);
		domandaXML.setProvinciaNascita(provNasc);
		domandaXML.setStatoNascita(statoNasc);
		domandaXML.setIndirizzo(indirizzo);
		domandaXML.setCap(cap);
		domandaXML.setComune(comune);
		domandaXML.setProvincia(provincia);
		domandaXML.setNumeroDiTelefonoDelBeneficiario(telefono);
		domandaXML.setIndirizzoEmailDelBeneficiario(email);
		domandaXML.setIbanNazioneLavoratore(ibanNazione);
		domandaXML.setIbanControlloLavoratore(ibanControllo);
		domandaXML.setCinLavoratore(cin);
		domandaXML.setAbiLavoratore(abi);
		domandaXML.setCabLavoratore(cab);
		domandaXML.setContoCorrenteLavoratore(cc);
		domandaXML.setImportoLordoGiornalieroPrestazione(strLordoGG);
		domandaXML.setImportoLordoComplessivoDaErogareAlBeneficiario(strLordoComplessivo);

		return domandaXML;
	}

	private XMLGregorianCalendar toXMLGregorianCalendarDateOnly(String dateString)
			throws DatatypeConfigurationException, ParseException {
		GregorianCalendar gc = new GregorianCalendar();
		Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
		gc.setTime(date);

		XMLGregorianCalendar xc = null;

		xc = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(gc.get(Calendar.YEAR),
				gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);

		return xc;
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

}
