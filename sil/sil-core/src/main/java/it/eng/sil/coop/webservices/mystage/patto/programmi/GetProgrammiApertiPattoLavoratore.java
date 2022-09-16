package it.eng.sil.coop.webservices.mystage.patto.programmi;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.mystage.patto.ErroreMyStage;
import it.eng.sil.coop.webservices.mystage.patto.MyStageException;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto.ProgrammiAperti;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto.ProgrammiAperti.ProgrammaAperto;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto.ProgrammiAperti.ProgrammaAperto.PoliticheAttive;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto.ProgrammiAperti.ProgrammaAperto.PoliticheAttive.PoliticaAttiva;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto.SchedaPartecipantePatto;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.ProfilingPatto.SchedaPartecipantePatto.SvantaggiFse;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.SchedaPartecipante;
import it.eng.sil.coop.webservices.mystage.patto.programmi.ProgrammiApertiPattoLavoratore.SchedaPartecipante.Svantaggi;
import it.eng.sil.util.amministrazione.impatti.CmBean;
import it.eng.sil.util.xml.XMLValidator;

public class GetProgrammiApertiPattoLavoratore {

	static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(GetProgrammiApertiPattoLavoratore.class.getName());
	private static ObjectFactory factory = new ObjectFactory();

	private static final String ESITO_OK = "00";
	private static final String DESC_OK = "OK";

	private static String codiceStr = null;

	private static String QUERY_STRING_ADESIONE_GG = "SELECT to_char(perAdesione.datadesionegg, 'dd/mm/yyyy') datadesionegg "
			+ " FROM or_percorso_concordato pc " + "INNER JOIN or_colloquio co ON (pc.prgcolloquio = co.prgcolloquio) "
			+ "INNER JOIN de_azione az ON (az.prgazioni = pc.prgazioni) "
			+ "LEFT JOIN or_percorso_concordato perAdesione ON (pc.prgpercorsoadesione = perAdesione.prgpercorso and pc.prgcolloquioadesione = perAdesione.prgcolloquio) "
			+ "WHERE co.prgColloquio = ? and co.codServizio in ('MGG','NGG') "
			+ "ORDER by perAdesione.datadesionegg desc";

	protected static final File outputXsd = new File(
			ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd" + File.separator
					+ "mystage" + File.separator + "ProgrammiApertiPattoLavoratore.xsd");

	public String getDati(String userName, String password, String codiceFiscale, String dataInizioTirocinio) {
		TransactionQueryExecutor tex = null;
		DataConnection conn = null;
		String descrizioneStr = null;

		try {
			// Inizializzo il TransactionQueryExecutor
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			conn = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			tex = new TransactionQueryExecutor(conn, null, null);
			tex.initTransaction();

			// verifica dati accesso
			if (isAutenticazioneValida(userName, password, tex)) {
				logger.debug("Autenticato con userName:" + userName + ",  password:" + password);
			} else {
				codiceStr = "01";
				descrizioneStr = "Errore in fase di autenticazione";
				throw new MyStageException("", codiceStr, descrizioneStr);
			}

			BigDecimal cdnLavoratore = null;
			SourceBean anLavBeanRows = (SourceBean) tex.executeQuery("WS_GET_CDN_LAVORATORE",
					new Object[] { codiceFiscale }, "SELECT");

			if (anLavBeanRows != null && !anLavBeanRows.getAttributeAsVector("ROW").isEmpty()) {
				cdnLavoratore = (BigDecimal) anLavBeanRows.getAttribute("ROW.CDNLAVORATORE");
			} else {
				codiceStr = "02";
				descrizioneStr = "Nessun lavoratore trovato";
				throw new MyStageException("", codiceStr, descrizioneStr);
			}

			String codProvinciaMinisteriale = null;
			String descrProvinciaMinisteriale = null;
			SourceBean rowsGenerale = (SourceBean) tex.executeQuery("GET_INFO_TS_GENERALE", null, "SELECT");
			if (rowsGenerale != null && !rowsGenerale.getAttributeAsVector("ROW").isEmpty()) {
				codProvinciaMinisteriale = (String) rowsGenerale.getAttribute("ROW.CODMIN");
				descrProvinciaMinisteriale = (String) rowsGenerale.getAttribute("ROW.STRDENOMINAZIONE");
			}

			ErroreMyStage errore = null;
			ProgrammiApertiPattoLavoratore programmiAperti = factory.createProgrammiApertiPattoLavoratore();

			Object[] params = new Object[6];
			params[0] = cdnLavoratore;
			params[1] = dataInizioTirocinio;
			params[2] = cdnLavoratore;
			params[3] = dataInizioTirocinio;
			params[4] = cdnLavoratore;
			params[5] = dataInizioTirocinio;
			SourceBean pattoBeanRows = (SourceBean) tex.executeQuery("GET_Patti_con_programmi_aperti_WS", params,
					"SELECT");
			if ((pattoBeanRows == null) || (pattoBeanRows.getAttributeAsVector("ROW").size() == 0)) {
				errore = getSchedaPartecipanteCalcolata(tex, programmiAperti, cdnLavoratore, dataInizioTirocinio);
			} else {
				errore = getPattoConProgrammiAperti(tex, programmiAperti, pattoBeanRows, cdnLavoratore,
						codProvinciaMinisteriale, descrProvinciaMinisteriale, dataInizioTirocinio);
			}

			if (errore.errCod != 0) {
				logger.error("GetProgrammiApertiPattoLavoratore: Invio a mystage non effettuato: cdnlavoratore = "
						+ cdnLavoratore);
				descrizioneStr = errore.erroreEsteso;
				throw new MyStageException("", codiceStr, descrizioneStr);
			}

			// Restituisco l'xml risultato
			programmiAperti.setCodiceEsito(ESITO_OK);
			programmiAperti.setDescEsito(DESC_OK);
			String xmlProgrammiAperti = convertInputToString(programmiAperti);

			if (!isXmlValid(xmlProgrammiAperti, outputXsd)) {
				codiceStr = "06";
				descrizioneStr = "Errore validazione output xml";
				throw new MyStageException("", codiceStr, descrizioneStr);
			} else
				logger.debug("XML Risposta " + xmlProgrammiAperti);

			tex.commitTransaction();

			return xmlProgrammiAperti;

		} catch (MyStageException e) {
			try {
				if (tex != null) {
					tex.rollBackTransaction();
				}
			} catch (EMFInternalError e1) {
				logger.error("problema con la rollback", e1);
			}
			return ritornoErrore(e.getRespCode(), e.getRespDesc());
		} catch (Throwable e) {
			try {
				if (tex != null) {
					tex.rollBackTransaction();
				}
			} catch (EMFInternalError e1) {
				logger.error("problema con la rollback", e1);
			}
			return ritornoErrore("99", "Errore generico");
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (EMFInternalError emf) {
				logger.error(emf);
			}
		}
	}

	protected boolean isXmlValid(String inputXML, File schemaFile) {

		String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);

		if (validityErrors != null) {
			logger.error("Errori di validazione file XML:\n" + validityErrors);
			return false;
		}
		return true;
	}

	private static ErroreMyStage getSchedaPartecipanteCalcolata(TransactionQueryExecutor texec,
			ProgrammiApertiPattoLavoratore progAperti, BigDecimal cdnLavoratore, String dataInizioTirocinio)
			throws Exception {

		try {
			String codContratto = null;
			String codStudio = null;
			String codDurata = null;
			String codOccupazione = null;

			SchedaPartecipante schedaPartecipante = factory.createProgrammiApertiPattoLavoratoreSchedaPartecipante();

			Object[] params = new Object[1];
			params[0] = cdnLavoratore;
			SourceBean contrattoBeanRows = (SourceBean) texec.executeQuery("GET_CONTRATTO_WS", params, "SELECT");
			if ((contrattoBeanRows != null) && !(contrattoBeanRows.getAttributeAsVector("ROW").isEmpty())) {
				codContratto = (String) contrattoBeanRows.getAttribute("ROW.CODCONTRATTO");
				schedaPartecipante.setContratto(codContratto);
			} else
				schedaPartecipante.setContratto("0");

			SourceBean titoloStudioBeanRows = (SourceBean) texec.executeQuery("GET_TITOLO_STUDIO_WS", params, "SELECT");
			codStudio = (String) titoloStudioBeanRows.getAttribute("ROW.CODSTUDIO");
			schedaPartecipante.setTitoloStudio(codStudio.trim());

			params = new Object[2];
			params[0] = cdnLavoratore;
			params[1] = dataInizioTirocinio;
			SourceBean condizioneOccupazBeanRows = (SourceBean) texec.executeQuery("GET_CONDIZIONE_OCCUPAZIONALE_WS",
					params, "SELECT");
			codOccupazione = (String) condizioneOccupazBeanRows.getAttribute("ROW.CODOCCUPAZIONE");
			schedaPartecipante.setCondizioneOccupazionale(codOccupazione.trim());

			params = new Object[4];
			params[0] = cdnLavoratore;
			params[1] = dataInizioTirocinio;
			params[2] = cdnLavoratore;
			params[3] = dataInizioTirocinio;
			SourceBean durataRicercaBeanRows = (SourceBean) texec.executeQuery("GET_DURATA_RICERCA_OCCUPAZIONE_WS",
					params, "SELECT");
			codDurata = (String) durataRicercaBeanRows.getAttribute("ROW.CODDURATA");
			if (codDurata != null) {
				schedaPartecipante.setDurataRicercaOccupazione(codDurata.trim());
			}

			Svantaggi svantaggi = factory.createProgrammiApertiPattoLavoratoreSchedaPartecipanteSvantaggi();
			CmBean cmLavBean = new CmBean(cdnLavoratore, texec);
			if (cmLavBean.isDisabile(dataInizioTirocinio)) {
				svantaggi.getTipoSvantaggio().add("6");
			} else
				svantaggi.getTipoSvantaggio().add("18");
			schedaPartecipante.setSvantaggi(svantaggi);

			progAperti.setSchedaPartecipante(schedaPartecipante);

		} catch (Throwable e) {
			logger.error("Impossibile recuperare i dati relativi alla scheda partecipante calcolata: " + cdnLavoratore);
			codiceStr = "05";
			return new ErroreMyStage(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_GENERICO);
		}

		return new ErroreMyStage(0);
	}

	private static ErroreMyStage getPattoConProgrammiAperti(TransactionQueryExecutor texec,
			ProgrammiApertiPattoLavoratore progAperti, SourceBean pattiBeanRows, BigDecimal cdnLavoratore,
			String codProvincia, String descProvincia, String dataInizioTirocinio) throws Exception {

		if (pattiBeanRows != null) {
			Vector pattiBeanVector = pattiBeanRows.getAttributeAsVector("ROW");

			BigDecimal prgPattoLavoratore = null;
			BigDecimal numIndiceSvantaggio2 = null;
			String datRiferimento = null;
			XMLGregorianCalendar datRiferimentoGregorian = null;
			String datStipula = null;
			String codCpi = null;
			String dataChiusuraPatto = null;
			String motivoChiusuraPatto = null;
			BigDecimal numProtocollo = null;
			XMLGregorianCalendar datStipulaGregorian = null;
			XMLGregorianCalendar dataChiusuraPattoGregorian = null;

			SourceBean pattiBeanRow = (SourceBean) pattiBeanVector.elementAt(0);

			prgPattoLavoratore = (BigDecimal) pattiBeanRow.getAttribute("PRGPATTOLAVORATORE");
			numIndiceSvantaggio2 = (BigDecimal) pattiBeanRow.getAttribute("NUMINDICESVANTAGGIO2");
			datRiferimento = (String) pattiBeanRow.getAttribute("DATRIFERIMENTO");
			if (datRiferimento != null) {
				datRiferimentoGregorian = toXMLGregorianCalendarDateOnly(datRiferimento);
			}
			datStipula = (String) pattiBeanRow.getAttribute("DATSTIPULA");
			if (datStipula != null) {
				datStipulaGregorian = toXMLGregorianCalendarDateOnly(datStipula);
			}
			codCpi = (String) pattiBeanRow.getAttribute("CODCPI");
			dataChiusuraPatto = (String) pattiBeanRow.getAttribute("DATFINE");
			if (dataChiusuraPatto != null) {
				dataChiusuraPattoGregorian = toXMLGregorianCalendarDateOnly(dataChiusuraPatto);
			}
			motivoChiusuraPatto = (String) pattiBeanRow.getAttribute("CODMOTIVOFINEATTO");
			numProtocollo = (BigDecimal) pattiBeanRow.getAttribute("NUMPROTOCOLLO");

			ProfilingPatto profilingPatto = factory.createProgrammiApertiPattoLavoratoreProfilingPatto();

			profilingPatto.setIndiceDataRiferimento(datRiferimentoGregorian);
			if (numIndiceSvantaggio2 != null) {
				profilingPatto.setIndiceSvantaggio(numIndiceSvantaggio2.toBigInteger());
			}
			profilingPatto.setDataPatto(datStipulaGregorian);
			if (codCpi != null) {
				profilingPatto.setPattoCpi(codCpi.trim());
			}
			if (codProvincia != null) {
				profilingPatto.setProvinciaProvenienza(codProvincia);
			}
			if (descProvincia != null) {
				profilingPatto.setDescrProvinciaProvenienza(descProvincia);
			}

			profilingPatto.setDataChiusuraPatto(dataChiusuraPattoGregorian);
			if (motivoChiusuraPatto != null) {
				profilingPatto.setMotivoChiusuraPatto(motivoChiusuraPatto.trim());
			}
			if (numProtocollo != null) {
				profilingPatto.setPattoNumeroProtocollo(numProtocollo.toBigInteger());
			}

			try {
				Object[] params = new Object[1];
				params[0] = prgPattoLavoratore;
				SourceBean schedaPartecipanteRows = (SourceBean) texec
						.executeQuery("GET_SCHEDA_PARTECIPANTE_E_SVANTAGGI_PATTO_WS", params, "SELECT");

				if ((schedaPartecipanteRows != null)
						&& !(schedaPartecipanteRows.getAttributeAsVector("ROW").isEmpty())) {
					SchedaPartecipantePatto schedaPartecipantePatto = factory
							.createProgrammiApertiPattoLavoratoreProfilingPattoSchedaPartecipantePatto();
					getSchedaPartecipanteFse(schedaPartecipantePatto, schedaPartecipanteRows);
					profilingPatto.setSchedaPartecipantePatto(schedaPartecipantePatto);
				}
			} catch (Throwable e) {
				logger.error("Impossibile recuperare i dati relativi alla scheda partecipante patto: " + cdnLavoratore);
				codiceStr = "03";
				return new ErroreMyStage(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_GENERICO);
			}

			ProgrammiAperti programmiAperti = factory
					.createProgrammiApertiPattoLavoratoreProfilingPattoProgrammiAperti();

			try {
				Object[] params = new Object[1];
				params[0] = prgPattoLavoratore;
				SourceBean programmiApertiRows = (SourceBean) texec.executeQuery("GET_programmi_aperti_tirocinio_WS",
						params, "SELECT");

				ErroreMyStage erroreMyStage = getProgrammi(texec, programmiAperti, programmiApertiRows);
				if (erroreMyStage.errCod != 0) {
					return erroreMyStage;
				} else {
					if (programmiAperti.getProgrammaAperto().size() > 0) {
						profilingPatto.setProgrammiAperti(programmiAperti);
					}
				}

			} catch (Throwable e) {
				logger.error("Impossibile recuperare i dati relativi ai programmi aperti: " + cdnLavoratore);
				codiceStr = "04";
				return new ErroreMyStage(MessageCodes.YG.ERR_WS_PARTECIPANTE_GG_DATA_ADESIONE_GENERICO);
			}

			progAperti.setProfilingPatto(profilingPatto);
		}

		return new ErroreMyStage(0);
	}

	private static ErroreMyStage getProgrammi(TransactionQueryExecutor texec, ProgrammiAperti programmiAperti,
			SourceBean programmiApertiBeanRows) throws Exception {

		if (programmiApertiBeanRows != null) {
			Vector programmiApertiBeanVector = programmiApertiBeanRows.getAttributeAsVector("ROW");

			BigDecimal prgColloquio = null;
			String datInizioProgramma = null;
			String datFineProgramma = null;
			String tipoProgramma = null;

			for (int i = 0; i < programmiApertiBeanVector.size(); i++) {
				XMLGregorianCalendar datInizioProgrammaGregorian = null;
				XMLGregorianCalendar datFineProgrammaGregorian = null;
				SourceBean programmiApertiBeanRow = (SourceBean) programmiApertiBeanVector.elementAt(i);

				prgColloquio = (BigDecimal) programmiApertiBeanRow.getAttribute("prgColloquio");
				datInizioProgramma = (String) programmiApertiBeanRow.getAttribute("datInizioProgramma");
				if (datInizioProgramma != null) {
					datInizioProgrammaGregorian = toXMLGregorianCalendarDateOnly(datInizioProgramma);
				}
				datFineProgramma = (String) programmiApertiBeanRow.getAttribute("datFineProgramma");
				if (datFineProgramma != null) {
					datFineProgrammaGregorian = toXMLGregorianCalendarDateOnly(datFineProgramma);
				}
				tipoProgramma = (String) programmiApertiBeanRow.getAttribute("tipoProgramma");

				ProgrammaAperto programmaAperto = factory
						.createProgrammiApertiPattoLavoratoreProfilingPattoProgrammiApertiProgrammaAperto();

				programmaAperto.setDataInizioProgramma(datInizioProgrammaGregorian);
				programmaAperto.setDataFineProgramma(datFineProgrammaGregorian);
				programmaAperto.setTipoProgramma(tipoProgramma);

				XMLGregorianCalendar datAdesioneGgGregorian = null;
				Object[] params = new Object[1];
				params[0] = prgColloquio;
				SourceBean adesioneAttivaBeanRows = (SourceBean) texec
						.executeQueryByStringStatement(QUERY_STRING_ADESIONE_GG, params, "SELECT");
				if (adesioneAttivaBeanRows != null && !adesioneAttivaBeanRows.getAttributeAsVector("ROW").isEmpty()) {
					SourceBean pattoAttivazione = (SourceBean) adesioneAttivaBeanRows.getAttributeAsVector("ROW")
							.get(0);
					String datAdesioneGg = pattoAttivazione.containsAttribute("DATADESIONEGG")
							? pattoAttivazione.getAttribute("DATADESIONEGG").toString()
							: "";
					if (!datAdesioneGg.equals("")) {
						datAdesioneGgGregorian = toXMLGregorianCalendarDateOnly(datAdesioneGg);
					}
				}
				if (datAdesioneGgGregorian != null) {
					programmaAperto.setAdesioneGgData(datAdesioneGgGregorian);
				}

				PoliticheAttive politicheAttive = factory
						.createProgrammiApertiPattoLavoratoreProfilingPattoProgrammiApertiProgrammaApertoPoliticheAttive();

				params = new Object[1];
				params[0] = prgColloquio;
				SourceBean politicheAttiveBeanRows = (SourceBean) texec
						.executeQuery("GET_politiche_attive_da_programma_aperto_tirocinio_WS", params, "SELECT");

				ErroreMyStage erroreMyStage = getPoliticheAttive(politicheAttive, politicheAttiveBeanRows);
				if (erroreMyStage.errCod != 0) {
					return erroreMyStage;
				} else {
					if (politicheAttive.getPoliticaAttiva().size() > 0) {
						programmaAperto.setPoliticheAttive(politicheAttive);
					}
				}

				programmiAperti.getProgrammaAperto().add(programmaAperto);
			}
		}

		return new ErroreMyStage(0);
	}

	private static ErroreMyStage getPoliticheAttive(PoliticheAttive politicheAttive, SourceBean politicheAttiveBeanRows)
			throws Exception {

		if (politicheAttiveBeanRows != null) {
			Vector politicheAttiveBeanVector = politicheAttiveBeanRows.getAttributeAsVector("ROW");

			BigDecimal prgPercorso = null;
			BigDecimal prgColloquio = null;
			String codAzioneSifer = null;
			BigDecimal prgAzioneRagg = null;
			String flgGruppo = null;
			BigDecimal numPartecipanti = null;
			String datEffettiva = null;
			String datStimata = null;
			String codEsito = null;
			String datAvvioAzione = null;
			XMLGregorianCalendar datAvvioAzioneGregorian = null;

			for (int i = 0; i < politicheAttiveBeanVector.size(); i++) {
				XMLGregorianCalendar datEffettivaGregorian = null;
				XMLGregorianCalendar datStimataGregorian = null;
				SourceBean politicheAttiveBeanRow = (SourceBean) politicheAttiveBeanVector.elementAt(i);

				prgPercorso = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGPERCORSO");
				prgColloquio = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGCOLLOQUIO");
				codAzioneSifer = (String) politicheAttiveBeanRow.getAttribute("CODAZIONESIFER");
				prgAzioneRagg = (BigDecimal) politicheAttiveBeanRow.getAttribute("PRGAZIONERAGG");
				flgGruppo = (String) politicheAttiveBeanRow.getAttribute("FLGGRUPPO");
				numPartecipanti = (BigDecimal) politicheAttiveBeanRow.getAttribute("NUMPARTECIPANTI");
				datEffettiva = (String) politicheAttiveBeanRow.getAttribute("DATEFFETTIVA");
				if (datEffettiva != null) {
					datEffettivaGregorian = toXMLGregorianCalendarDateOnly(datEffettiva);
				}
				datStimata = (String) politicheAttiveBeanRow.getAttribute("DATSTIMATA");
				if (datStimata != null) {
					datStimataGregorian = toXMLGregorianCalendarDateOnly(datStimata);
				}
				codEsito = (String) politicheAttiveBeanRow.getAttribute("CODESITO");
				datAvvioAzione = (String) politicheAttiveBeanRow.getAttribute("DATAVVIOAZIONE");
				if (datAvvioAzione != null) {
					datAvvioAzioneGregorian = toXMLGregorianCalendarDateOnly(datAvvioAzione);
				}

				PoliticaAttiva politicaAttiva = factory
						.createProgrammiApertiPattoLavoratoreProfilingPattoProgrammiApertiProgrammaApertoPoliticheAttivePoliticaAttiva();

				politicaAttiva.setDataFineAttivita(datEffettivaGregorian);
				politicaAttiva.setDataStimataAttivita(datStimataGregorian);
				if (codEsito != null) {
					politicaAttiva.setEsito(codEsito.trim());
				}
				if (flgGruppo != null) {
					politicaAttiva.setFlgGruppo(flgGruppo.trim());
				}
				if (prgAzioneRagg != null) {
					politicaAttiva.setMisura(prgAzioneRagg.toBigInteger());
				}
				if (numPartecipanti != null) {
					politicaAttiva.setNumPartecipanti(numPartecipanti.toBigInteger());
				}
				if (prgColloquio != null) {
					politicaAttiva.setPrgColloquio(prgColloquio.toBigInteger());
				}
				if (prgPercorso != null) {
					politicaAttiva.setPrgPercorso(prgPercorso.toBigInteger());
				}
				if (codAzioneSifer != null) {
					politicaAttiva.setTipoAttivita(codAzioneSifer.trim());
				}
				politicaAttiva.setDataAvvioAttivita(datAvvioAzioneGregorian);

				politicheAttive.getPoliticaAttiva().add(politicaAttiva);
			}
		}
		return new ErroreMyStage(0);
	}

	private static void getSchedaPartecipanteFse(SchedaPartecipantePatto schedaPartecipantePatto,
			SourceBean schedaPartecipanteRows) throws Exception {

		String codContratto = null;
		String codStudio = null;
		String codDurata = null;
		String codOccupazione = null;
		String codiciSvantaggio = null;

		codContratto = (String) schedaPartecipanteRows.getAttribute("ROW.CODCONTRATTO");
		codStudio = (String) schedaPartecipanteRows.getAttribute("ROW.CODSTUDIO");
		codDurata = (String) schedaPartecipanteRows.getAttribute("ROW.CODDURATA");
		codOccupazione = (String) schedaPartecipanteRows.getAttribute("ROW.CODOCCUPAZIONE");
		codiciSvantaggio = (String) schedaPartecipanteRows.getAttribute("ROW.CODICISVANTAGGIO");

		schedaPartecipantePatto.setTitoloStudioFse(codStudio.trim());
		schedaPartecipantePatto.setCondizioneOccupazionaleFse(codOccupazione.trim());
		if (codDurata != null) {
			schedaPartecipantePatto.setDurataRicercaOccupazioneFse(codDurata.trim());
		}
		if (codContratto != null) {
			schedaPartecipantePatto.setContrattoFse(codContratto.trim());
		}

		SvantaggiFse svantaggiFse = factory
				.createProgrammiApertiPattoLavoratoreProfilingPattoSchedaPartecipantePattoSvantaggiFse();

		if (codiciSvantaggio != null) {
			String[] svantaggi = codiciSvantaggio.split(",");
			List<String> tipoSvantaggi = new ArrayList<String>(Arrays.asList(svantaggi));
			if (!tipoSvantaggi.isEmpty()) {
				svantaggiFse.getTipoSvantaggioFse().addAll(tipoSvantaggi);
				schedaPartecipantePatto.setSvantaggiFse(svantaggiFse);
			}
		}
	}

	private boolean isAutenticazioneValida(String userName, String password, TransactionQueryExecutor tex)
			throws Throwable {
		// Controllare autenticazione - by db!
		String usernameTsWs = null;
		String passwordTsWs = null;
		try {
			SourceBean logon = (SourceBean) tex.executeQuery("GET_USER_PWD_WS_BY_SERVICE",
					new Object[] { "ProgrammiApertiLav" }, "SELECT");
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
		} catch (Throwable e) {
			logger.error("Impossibile autenticare userName:" + userName + ",  password:" + password, e);
			// return false;
			throw e;
		}
	}

	private static String ritornoErrore(String codice, String descErrore) {
		String xmlProgrammiAperti = "";
		ProgrammiApertiPattoLavoratore programmiAperti = null;

		try {
			programmiAperti = factory.createProgrammiApertiPattoLavoratore();
			programmiAperti.setCodiceEsito(codice);
			programmiAperti.setDescEsito(descErrore);
			xmlProgrammiAperti = convertInputToString(programmiAperti);
		} catch (Throwable e) {
			logger.error("Errore creazione input XML", e);
		}
		return xmlProgrammiAperti;
	}

	private static String convertInputToString(ProgrammiApertiPattoLavoratore msg) {
		String xml = "";
		try {
			JAXBContext jc = JAXBContext.newInstance(ProgrammiApertiPattoLavoratore.class);
			Marshaller marshaller = jc.createMarshaller();
			StringWriter writer = new StringWriter();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(msg, writer);
			xml = writer.getBuffer().toString();
		} catch (JAXBException e) {
			logger.error("Errore nella conversione a stringa dell' xml da inviare", e);
		}
		return xml;
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
