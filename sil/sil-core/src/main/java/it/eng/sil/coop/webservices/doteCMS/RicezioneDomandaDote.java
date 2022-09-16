package it.eng.sil.coop.webservices.doteCMS;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.doteCMS.input.ObjectFactory;
import it.eng.sil.coop.webservices.doteCMS.input.RicezionePartecipanteDote;
import it.eng.sil.coop.webservices.doteCMS.input.RicezionePartecipanteDote.DomandaDote;
import it.eng.sil.coop.webservices.doteCMS.input.RicezionePartecipanteDote.DomandaDote.PoliticheAttive;
import it.eng.sil.coop.webservices.doteCMS.input.RicezionePartecipanteDote.DomandaDote.PoliticheAttive.PoliticaAttiva;
import it.eng.sil.coop.webservices.doteCMS.output.EsitoPartecipanteDote;

public class RicezioneDomandaDote implements RicezioneDomandaDoteInterface {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicezioneDomandaDote.class.getName());
	private static final String ESITO_OK = "00";
	private static final String TIPO_PATTO_DOTE = "DOTE";
	private static final String TIPO_PATTO_DOTE_IA = "DOTE_IA";
	private RicezionePartecipanteDote dettDomandaDote;
	private String tipoDote = "";
	private String esito = "00";
	private String descrizioneEsito = "Inserimento/Aggiornamento Patto Dote OK";
	private String strCodiceFiscale = "00000000000";
	private BigDecimal cdnLavoratore = null;
	private BigDecimal prgDichDisponibilita = null;
	private BigDecimal prgPattoDB = null;
	private String codTipoPattoDB = "";
	private String dataStipulaDB = "";
	private DomandaDote dote = null;
	private String codcpitit = "";
	private BigDecimal prgPattoLavNew = null;
	private String strenteCF = "";
	private String codSedeCF = "";
	private BigDecimal prgColloquio = null;
	private EsitoPartecipanteDote.PoliticheAttive polAttiveOutput = null;

	public static final String XSD_DOMANDA_DOTE = ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "doteCMS" + File.separator + "CMS-SIL.xsd";
	public static final String XSD_ESITO_DOMANDA_DOTE = ConfigSingleton.getRootPath() + File.separator + "WEB-INF"
			+ File.separator + "xsd" + File.separator + "doteCMS" + File.separator + "SIL-CMS.xsd";

	public RicezioneDomandaDote() {
	}

	public RicezioneDomandaDote(String strPattoL14) {

		try {
			System.out.println(processDomandaDote(strPattoL14));

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String processDomandaDote(String inputXML) throws Throwable {
		String outputXml = "";

		try {
			if (checkInputXml(inputXML)) {
				if (checkEsistenzaLavoratore()) {
					if (checkDidAttiva()) {
						if (checkPatto()) {
							gestisciDote();
						}
					}
				}
			}
		} catch (Exception e) {
			descrizioneEsito = "Errore generico";
			_logger.error(descrizioneEsito);
			esito = "99";
		}

		outputXml = createXMLesito();

		if (!isXmlValid(outputXml, XSD_ESITO_DOMANDA_DOTE)) {
			descrizioneEsito = "Errore nella validazione output xml";
			_logger.error(descrizioneEsito);
			esito = "11";
			outputXml = createXMLesito();
		}

		return outputXml;
	}

	private final String createXMLesito() throws Exception {
		_logger.debug("createXMLesito() - start generazione xml ");
		String returnString = "";

		EsitoPartecipanteDote esitoCMS = new EsitoPartecipanteDote();

		if (ESITO_OK.equals(esito)) {
			esitoCMS.setCodiceEsito(ESITO_OK);
			esitoCMS.setDescrizioneEsito(descrizioneEsito);
			esitoCMS.setCodiceFiscale(strCodiceFiscale);

			BigDecimal prgPattoToUse = prgPattoLavNew != null ? prgPattoLavNew : prgPattoDB;
			esitoCMS.setPrgPatto((prgPattoToUse).toBigInteger());

			esitoCMS.setPoliticheAttive(polAttiveOutput);
		} else {
			esitoCMS.setCodiceEsito(esito);
			esitoCMS.setDescrizioneEsito(descrizioneEsito);
			esitoCMS.setCodiceFiscale(strCodiceFiscale);
		}

		_logger.debug("createXMLesito() - end generazione xml ");

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(EsitoPartecipanteDote.class);
			StringWriter writer = new StringWriter();
			jaxbContext.createMarshaller().marshal(esitoCMS, writer);
			returnString = writer.toString();
		} catch (JAXBException e) {
			_logger.error("Errore nella creazione dell'xml da inviare", e);
		}

		return returnString;
	}

	private static BigDecimal getUtenteCMS() {
		SourceBean utenteBean = (SourceBean) QueryExecutor.executeQuery("GET_USERCODE_DOTE_CMS", new Object[] {},
				"SELECT", Values.DB_SIL_DATI);
		BigDecimal codUtenteCMS = (BigDecimal) utenteBean.getAttribute("ROW.CDNUT");

		return codUtenteCMS;
	}

	private static BigDecimal getPrgSpiSystem() {
		SourceBean prgSpiBean = (SourceBean) QueryExecutor.executeQuery("SELECT_PRGSPI_SYSTEM", new Object[] {},
				"SELECT", Values.DB_SIL_DATI);
		BigDecimal prgSpi = (BigDecimal) prgSpiBean.getAttribute("ROW.prgspi");

		return prgSpi;
	}

	private final void gestisciDote() {
		TransactionQueryExecutor transExec = null;

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			dote = dettDomandaDote.getDomandaDote();
			tipoDote = dote.getPattoInclusioneAttiva().equals("S") ? TIPO_PATTO_DOTE_IA : TIPO_PATTO_DOTE;
			strenteCF = dote.getCFEnteAccreditato();
			codSedeCF = dote.getSedeEnteAccreditato();

			if (prgPattoDB == null) {
				inserisciPattoDote(transExec);
			} else {
				if (!codTipoPattoDB.equals(TIPO_PATTO_DOTE) && !codTipoPattoDB.equals(TIPO_PATTO_DOTE_IA)) {
					chiudiPattoNonDote(transExec);
					inserisciPattoDote(transExec);
				}
			}

			checkEnte(transExec);
			gestisciColloquio(transExec);
			gestisciPoliticheAttive(transExec);

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

	private final void gestisciPercorso(TransactionQueryExecutor txExec, PoliticaAttiva polAttiva,
			BigDecimal prgPercorso) throws Exception {
		EsitoPartecipanteDote.PoliticheAttive.PoliticaAttiva polAttivaOutput = new EsitoPartecipanteDote.PoliticheAttive.PoliticaAttiva();
		String codAzioneFormCal = polAttiva.getCodAzioneFormCal();
		/*
		 * Connection conn = txExec.getDataConnection().getInternalConnection(); Savepoint savePointPercorso =
		 * conn.setSavepoint(); try {
		 */
		SourceBean prgAzioniBean = (SourceBean) txExec.executeQuery("SELECT_PRGAZIONI_DOTE",
				new Object[] { codAzioneFormCal }, "SELECT");
		BigDecimal prgAzioni = (BigDecimal) prgAzioniBean.getAttribute("ROW.prgAzioni");

		String dataStimata = DateUtils.formatXMLGregorian(polAttiva.getDataStimata());
		String codEsito = polAttiva.getEsito();

		Object resPercorso = null;
		BigDecimal newPrgPercorso = null;
		if (prgPercorso == null) {
			SourceBean resNewPrgPercorso = (SourceBean) txExec.executeQuery("OR_PERCORSO_CONCORDATO_NEXTVAL", null,
					"SELECT");
			newPrgPercorso = (BigDecimal) resNewPrgPercorso.getAttribute("ROW.do_nextval");

			resPercorso = txExec.executeQuery("INSERT_PERCORSO_DOTE", new Object[] { prgColloquio, newPrgPercorso,
					codEsito, dataStimata, prgAzioni, getPrgSpiSystem(), getUtenteCMS(), getUtenteCMS() }, "INSERT");
		} else {
			resPercorso = txExec.executeQuery("UPDATE_PERCORSO_DOTE", new Object[] { codEsito, dataStimata, prgAzioni,
					getPrgSpiSystem(), getUtenteCMS(), prgColloquio, prgPercorso }, "UPDATE");
		}

		if (prgPercorso == null) {
			if (resPercorso == null
					|| !(resPercorso instanceof Boolean && ((Boolean) resPercorso).booleanValue() == true)) {
				throw new Exception();
			}

			Object resPattoScelta = txExec.executeQuery("INS_PATTO_SCELTA_WS",
					new Object[] { prgPattoLavNew != null ? prgPattoLavNew : prgPattoDB, "OR_PER", newPrgPercorso },
					"INSERT");
			if (resPattoScelta == null
					|| !(resPattoScelta instanceof Boolean && ((Boolean) resPattoScelta).booleanValue() == true)) {
				throw new Exception();
			}

			SourceBean codImpegniBean = (SourceBean) txExec.executeQuery("SELECT_DE_IMPEGNI_WS",
					new Object[] { prgAzioni }, "SELECT");
			Vector<SourceBean> codImpegni = (Vector<SourceBean>) codImpegniBean.getAttributeAsVector("ROW");

			for (int j = 0; j < codImpegni.size(); j++) {
				String codImpegno = (String) ((SourceBean) codImpegni.get(j)).getAttribute("codImpegno");

				SourceBean codImpegnoDB = (SourceBean) txExec.executeQuery("SELECT_IMPEGNI_WS",
						new Object[] { prgPattoLavNew != null ? prgPattoLavNew : prgPattoDB, codImpegno }, "SELECT");
				Vector vectCodImpegnoDB = codImpegnoDB.getAttributeAsVector("ROW");

				boolean isImpegnoEsistente = (codImpegnoDB != null) && (vectCodImpegnoDB.size() > 0);

				if (!isImpegnoEsistente) {
					Object resImpegno = txExec.executeQuery("INS_PATTO_SCELTA_WS", new Object[] {
							prgPattoLavNew != null ? prgPattoLavNew : prgPattoDB, "DE_IMPE", codImpegno }, "INSERT");
					if (resImpegno == null
							|| !(resImpegno instanceof Boolean && ((Boolean) resImpegno).booleanValue() == true)) {
						throw new Exception();
					}
				}
			}
			polAttivaOutput.setPrgPercorso(newPrgPercorso.toBigInteger());
			polAttivaOutput.setPrgColloquio(prgColloquio.toBigInteger());
		} else {
			/*
			 * if (resPercorso == null || !(resPercorso instanceof Boolean && ((Boolean) resPercorso).booleanValue() ==
			 * true)) { polAttivaOutput.setCodiceErrorePolitica("12");
			 * polAttivaOutput.setDescrizioneErrorePolitica("Non esiste una politica attiva con prgcolloquio " +
			 * prgColloquio.toString() + " e prgPercorso " + prgPercorso.toString()); } else {
			 */
			polAttivaOutput.setPrgPercorso(prgPercorso.toBigInteger());
			polAttivaOutput.setPrgColloquio(prgColloquio.toBigInteger());
			// }
		}
		/*
		 * } catch (Exception e) { if (prgPercorso == null) { polAttivaOutput.setCodiceErrorePolitica("14");
		 * polAttivaOutput.
		 * setDescrizioneErrorePolitica("Errore in fase di inserimento della politica attiva Dote con codAzioneFormCal "
		 * + codAzioneFormCal); conn.rollback(savePointPercorso); } else {
		 * polAttivaOutput.setCodiceErrorePolitica("15"); polAttivaOutput.
		 * setDescrizioneErrorePolitica("Errore in fase di aggiornamento della politica attiva Dote con prgcolloquio " +
		 * prgColloquio.toString() + " e prgPercorso " + prgPercorso.toString()); } }
		 */
		polAttiveOutput.getPoliticaAttiva().add(polAttivaOutput);
	}

	private final void gestisciPoliticheAttive(TransactionQueryExecutor txExec) throws Exception {
		try {
			polAttiveOutput = new EsitoPartecipanteDote.PoliticheAttive();

			PoliticheAttive polAttiveBean = dote.getPoliticheAttive();
			List<PoliticaAttiva> polAttive = polAttiveBean.getPoliticaAttiva();
			for (PoliticaAttiva polAttiva : polAttive) {
				if (prgPattoLavNew != null) {
					gestisciPercorso(txExec, polAttiva, null);
				} else {
					if (polAttiva.getPrgColloquio() != null) {
						prgColloquio = new BigDecimal(polAttiva.getPrgColloquio());
					} else {
						SourceBean colloquioBean = (SourceBean) txExec.executeQuery("CHECK_OR_COLLOQUIO_DOTE",
								new Object[] { cdnLavoratore, tipoDote, dataStipulaDB }, "SELECT");
						if (colloquioBean.containsAttribute("ROW")) {
							prgColloquio = (BigDecimal) colloquioBean.getAttribute("ROW.prgColloquio");
						} else
							throw new Exception();
					}

					if (polAttiva.getPrgPercorso() != null) {
						BigDecimal prgPercorso = new BigDecimal(polAttiva.getPrgPercorso());
						SourceBean percorsoBean = (SourceBean) txExec.executeQuery("CHECK_OR_PERCORSO_CONCORDATO_DOTE",
								new Object[] { prgColloquio, prgPercorso }, "SELECT");
						if (percorsoBean.containsAttribute("ROW")) {
							// String codAzioneFormCalDB = (String) percorsoBean.getAttribute("ROW.codAzioneFormCal");
							String codAzioneFormCalDB = (String) percorsoBean.getAttribute("ROW.codAzioneSifer");
							if (codAzioneFormCalDB.equalsIgnoreCase(polAttiva.getCodAzioneFormCal())) {
								descrizioneEsito = "Errore in fase di aggiornamento della politica attiva con prgcolloquio "
										+ prgColloquio.toString() + " e prgPercorso " + prgPercorso.toString()
										+ ": il CodAzioneFormCal non deve essere diverso da quello presente a sistema";
								esito = "13";
								throw new Exception();
							} else
								gestisciPercorso(txExec, polAttiva, prgPercorso);
						} else
							throw new Exception();
					} else {
						gestisciPercorso(txExec, polAttiva, null);
					}
				}
			}
			/*
			 * boolean allErrorsPolitiche = true; for (EsitoPartecipanteDote.PoliticheAttive.PoliticaAttiva
			 * polAttivaCheckOutput : polAttiveOutput.getPoliticaAttiva()) { if (polAttivaCheckOutput.getPrgPercorso()
			 * != null) { allErrorsPolitiche = false; break; } } if (allErrorsPolitiche) throw new Exception();
			 */
		} catch (Exception e) {
			if (ESITO_OK.equals(esito)) {
				descrizioneEsito = "Errore in fase di inserimento/aggiornamento delle politiche attive Dote";
				esito = "10";
			}
			_logger.error(descrizioneEsito);
			throw new Exception();
		}
	}

	private final void gestisciColloquio(TransactionQueryExecutor txExec) throws Exception {
		try {
			if (prgPattoLavNew != null) {
				SourceBean prgColloquioBean = (SourceBean) txExec.executeQuery("OR_COLLOQUIO_NEXTVAL", new Object[] {},
						"SELECT");
				prgColloquio = (BigDecimal) prgColloquioBean.getAttribute("ROW.do_nextval");

				Object resColloquio = txExec.executeQuery("INSERT_COLLOQUIO_DOTE", new Object[] { prgColloquio,
						cdnLavoratore, codcpitit, getPrgSpiSystem(), tipoDote, getUtenteCMS(), getUtenteCMS() },
						"INSERT");

				if (resColloquio == null
						|| !(resColloquio instanceof Boolean && ((Boolean) resColloquio).booleanValue() == true)) {
					throw new Exception();
				}

				Object resSchedaColloquio = txExec.executeQuery("INSERT_SCHEDA_COLLOQUIO_WS",
						new Object[] { prgColloquio }, "INSERT");

				if (resSchedaColloquio == null || !(resSchedaColloquio instanceof Boolean
						&& ((Boolean) resSchedaColloquio).booleanValue() == true)) {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			descrizioneEsito = "Errore in fase di inserimento del colloquio Dote";
			_logger.error(descrizioneEsito);
			esito = "09";
			throw new Exception();
		}
	}

	private final void checkEnte(TransactionQueryExecutor txExec) throws Exception {
		try {
			if (prgPattoLavNew == null) {
				SourceBean enteBean = (SourceBean) txExec.executeQuery("CHECK_ENTE_PATTO_CMS",
						new Object[] { prgPattoDB }, "SELECT");

				String cfEnteDB = (String) enteBean.getAttribute("ROW.cf_ente");
				String sedeEnteDB = (String) enteBean.getAttribute("ROW.sede_ente");

				if (!cfEnteDB.equals(strenteCF) || !sedeEnteDB.equals(codSedeCF)) {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			descrizioneEsito = "Non è consentito modificare l'Ente Accreditato Dote";
			_logger.error(descrizioneEsito);
			esito = "08";
			throw new Exception();
		}
	}

	private final void gestisciEnte(TransactionQueryExecutor txExec) throws Exception {
		try {
			SourceBean enteBean = (SourceBean) txExec.executeQuery("CHECK_EXIST_ENTE",
					new Object[] { strenteCF, codSedeCF }, "SELECT");

			if (!enteBean.containsAttribute("ROW.exist_ente")) {
				String ragSocEnte = dettDomandaDote.getDomandaDote().getRagioneSocialeEnte();
				String indirizzoEnte = dettDomandaDote.getDomandaDote().getIndirizzoEnteAccreditato() != null
						? dettDomandaDote.getDomandaDote().getIndirizzoEnteAccreditato()
						: null;
				String telefonoEnte = dettDomandaDote.getDomandaDote().getTelefonoEnteAccreditato() != null
						? dettDomandaDote.getDomandaDote().getTelefonoEnteAccreditato()
						: null;

				Object resEnteDote = txExec.executeQuery("INSERT_ENTE_DOTE", new Object[] { strenteCF, codSedeCF,
						ragSocEnte, indirizzoEnte, codSedeCF, getUtenteCMS(), getUtenteCMS(), telefonoEnte }, "INSERT");

				if (resEnteDote == null
						|| !(resEnteDote instanceof Boolean && ((Boolean) resEnteDote).booleanValue() == true)) {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			descrizioneEsito = "Errore in fase di inserimento dell'Ente Accreditato Dote";
			_logger.error(descrizioneEsito);
			esito = "06";
			throw new Exception();
		}
	}

	private final void inserisciPattoDote(TransactionQueryExecutor txExec) throws Exception {
		try {
			String strIdDomandaDote = dote.getIdDomandaDote();

			gestisciEnte(txExec);

			SourceBean cpiTitBean = (SourceBean) txExec.executeQuery("GET_CPI_AN_LAVORATORE",
					new Object[] { cdnLavoratore }, "SELECT");
			codcpitit = (String) cpiTitBean.getAttribute("ROW.codcpitit");

			SourceBean prgPattoBean = (SourceBean) txExec.executeQuery("GET_NEW_PATTOLAV", new Object[] {}, "SELECT");
			prgPattoLavNew = (BigDecimal) prgPattoBean.getAttribute("ROW.prgPattoLavoratore");

			Object resPattoDote = txExec
					.executeQuery("INSERT_PATTOLAV_DOTE",
							new Object[] { prgPattoLavNew, cdnLavoratore, codcpitit, prgDichDisponibilita, tipoDote,
									tipoDote, strenteCF, codSedeCF, strIdDomandaDote, getUtenteCMS(), getUtenteCMS() },
							"INSERT");

			if (resPattoDote == null
					|| !(resPattoDote instanceof Boolean && ((Boolean) resPattoDote).booleanValue() == true)) {
				throw new Exception();
			}

			SourceBean prgDocumentoBean = (SourceBean) txExec.executeQuery("NEXT_S_AM_DOCUMENTO", new Object[] {},
					"SELECT");
			BigDecimal newPrgDocumento = (BigDecimal) prgDocumentoBean.getAttribute("ROW.KEY");

			SourceBean infoProtBean = (SourceBean) txExec.executeQuery("GET_INFO_PROTOCOLLO_DOC_WS", new Object[] {},
					"SELECT");
			BigDecimal numProt = (BigDecimal) infoProtBean.getAttribute("ROW.numprotocollo");
			BigDecimal numkloprot = (BigDecimal) infoProtBean.getAttribute("ROW.numkloprotocollo");

			Object resDocumento = txExec.executeQuery("INSERT_AM_DOCUMENTO_DOTE", new Object[] { newPrgDocumento,
					cdnLavoratore, codcpitit, codcpitit, numProt, getUtenteCMS(), getUtenteCMS() }, "INSERT");

			if (resDocumento == null
					|| !(resDocumento instanceof Boolean && ((Boolean) resDocumento).booleanValue() == true)) {
				throw new Exception();
			}

			SourceBean cdnComponenteBean = (SourceBean) txExec.executeQuery("GET_CDNCOMPONENTE",
					new Object[] { "pattolavdettagliopage" }, "SELECT");
			BigDecimal cdnComponente = (BigDecimal) cdnComponenteBean.getAttribute("ROW.cdnComponente");

			Object resDocumentoBlob = txExec.executeQuery("INSERT_AM_DOCUMENTO_BLOB_WS",
					new Object[] { newPrgDocumento }, "INSERT");

			if (resDocumentoBlob == null
					|| !(resDocumentoBlob instanceof Boolean && ((Boolean) resDocumentoBlob).booleanValue() == true)) {
				throw new Exception();
			}

			Object resDocumentoColl = txExec.executeQuery("INSERT_AM_DOCUMENTO_COLL_WS",
					new Object[] { newPrgDocumento, cdnComponente, prgPattoLavNew }, "INSERT");

			if (resDocumentoColl == null
					|| !(resDocumentoColl instanceof Boolean && ((Boolean) resDocumentoColl).booleanValue() == true)) {
				throw new Exception();
			}

			Object resUpdInfoProt = txExec.executeQuery("UPDATE_AM_PROTOCOLLO_WS",
					new Object[] { numProt, numkloprot.add(new BigDecimal(1D)) }, "UPDATE");
			if (resUpdInfoProt == null
					|| !(resUpdInfoProt instanceof Boolean && ((Boolean) resUpdInfoProt).booleanValue() == true)) {
				throw new Exception();
			}

		} catch (Exception e) {
			if (esito.equals(ESITO_OK)) {
				descrizioneEsito = "Errore in fase di inserimento del patto Dote";
				_logger.error(descrizioneEsito);
				esito = "07";
			}
			throw new Exception();
		}
	}

	private final void chiudiPattoNonDote(TransactionQueryExecutor txExec) throws Exception {
		Object queryPattoChiusuraNonDote = txExec.executeQuery("CHIUDI_PATTO_NON_DOTE",
				new Object[] { getUtenteCMS(), prgPattoDB }, "UPDATE");
		if (queryPattoChiusuraNonDote == null || !(queryPattoChiusuraNonDote instanceof Boolean
				&& ((Boolean) queryPattoChiusuraNonDote).booleanValue() == true)) {
			descrizioneEsito = "Errore chiusura patto precedente non Dote";
			_logger.error(descrizioneEsito);
			esito = "05";
			throw new Exception();
		}
	}

	private final boolean checkPatto() throws Exception {
		boolean checkPatto = true;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = cdnLavoratore;

		SourceBean pattoAperto = (SourceBean) QueryExecutor.executeQuery("GET_PATTO_APERTO_WS", inputParameters,
				"SELECT", Values.DB_SIL_DATI);

		if (pattoAperto.containsAttribute("ROW")) {
			prgPattoDB = (BigDecimal) pattoAperto.getAttribute("ROW.PRGPATTOLAVORATORE");
			codTipoPattoDB = (String) pattoAperto.getAttribute("ROW.CODTIPOPATTO");
			dataStipulaDB = (String) pattoAperto.getAttribute("ROW.DATASTIPULA");

			if (codTipoPattoDB.equals(TIPO_PATTO_DOTE) || codTipoPattoDB.equals(TIPO_PATTO_DOTE_IA)) {
				try {
					BigDecimal prgPattoFromCMS = new BigDecimal(dettDomandaDote.getDomandaDote().getPrgPatto());
					if (!prgPattoFromCMS.equals(prgPattoDB)) {
						throw new Exception();
					}
				} catch (Exception e) {
					descrizioneEsito = "Lavoratore possiede già un patto DOTE valido";
					_logger.error(descrizioneEsito);
					esito = "04";
					checkPatto = false;
				}
			}
		}

		return checkPatto;
	}

	private final boolean checkDidAttiva() throws Exception {
		boolean checkDidAttiva = true;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = cdnLavoratore;

		SourceBean didAttiva = (SourceBean) QueryExecutor.executeQuery("GET_DID_LAVORATORE_APERTA", inputParameters,
				"SELECT", Values.DB_SIL_DATI);
		if (!didAttiva.containsAttribute("ROW")) {
			descrizioneEsito = "Lavoratore non possiede una DID attiva protocollata";
			_logger.error(descrizioneEsito);
			esito = "03";
			checkDidAttiva = false;
		} else {
			prgDichDisponibilita = (BigDecimal) didAttiva.getAttribute("ROW.prgdichdisponibilita");
		}
		return checkDidAttiva;
	}

	private final boolean checkEsistenzaLavoratore() throws Exception {
		boolean checkokEL = true;
		strCodiceFiscale = dettDomandaDote.getPartecipante().getCodiceFiscale();
		Object[] inputParameters = new Object[1];
		inputParameters[0] = strCodiceFiscale;

		SourceBean lavoratore = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE", inputParameters,
				"SELECT", Values.DB_SIL_DATI);
		if (!lavoratore.containsAttribute("ROW.CDNLAVORATORE")) {
			descrizioneEsito = "Lavoratore non presente";
			_logger.error(descrizioneEsito);
			checkokEL = false;
			esito = "02";
		} else {
			cdnLavoratore = (BigDecimal) lavoratore.getAttribute("ROW.CDNLAVORATORE");
		}
		return checkokEL;
	}

	private final boolean checkInputXml(String inputXML) throws Exception {
		boolean checkokinputXML = true;

		if (!isXmlValid(inputXML, XSD_DOMANDA_DOTE)) {
			descrizioneEsito = "Errore nella validazione input xml";
			_logger.error(descrizioneEsito);
			checkokinputXML = false;
			esito = "01";
		} else {
			dettDomandaDote = convertToDomandaDote(inputXML);
		}
		return checkokinputXML;
	}

	private RicezionePartecipanteDote convertToDomandaDote(String strDomandaDote) {
		JAXBContext jaxbContext;
		RicezionePartecipanteDote domandaDoteType = null;
		try {

			jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Schema schema = getXsdSchema(XSD_DOMANDA_DOTE);
			jaxbUnmarshaller.setSchema(schema);
			domandaDoteType = (RicezionePartecipanteDote) jaxbUnmarshaller.unmarshal(new StringReader(strDomandaDote));

		} catch (Exception e) {
			_logger.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return domandaDoteType;
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
		} catch (Exception e) {
			String validityError = "Errore di validazione xml: " + e.getMessage();
			_logger.error(validityError);
			_logger.warn(stringaXML);
			checkokXML = false;
		}
		return checkokXML;
	}

}
