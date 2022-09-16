package it.eng.sil.module.stampeparam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;

public class MElaboraStampaParametrica extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(MElaboraStampaParametrica.class.getName());

	public MElaboraStampaParametrica() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// commento da eliminare
		
		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int errorCode = MessageCodes.General.INSERT_FAIL;

		// String flgFirmaGrafo = null;
		String codTipoDoc = null;
		String nomeTemplate = null;
		boolean result = true;
		User user = null;

		String codStatoAtto = Properties.STATO_ATTO_PROTOC;
		String dataAcquisizione = DateUtils.getNow();
		String dataInizio = DateUtils.getNow();
		String flgCodMonoIO = "";
		BigDecimal progressivoDoc = null;

		boolean protocollazione = (request.containsAttribute("GENERASTAMPA")
				&& request.getAttribute("GENERASTAMPA").toString().equalsIgnoreCase("STAMPA"));

		TransactionQueryExecutor transExec = null;

		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);
			transExec.initTransaction();

			user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
			codTipoDoc = request.containsAttribute("TIPODOC") ? request.getAttribute("TIPODOC").toString() : "";

			// invoco lo statement DettaglioTemplate per estrarre i dati dalla ST_TEMPLATE_STAMPA
			// parameter: prgtemplatestampa (già presente nella request)
			setSectionQuerySelect("QUERY_SELECT_TEMPLATE");
			SourceBean rowTemplate = doSelect(request, response, false);
			nomeTemplate = rowTemplate.getAttribute("ROW.STRNOME")!=null?rowTemplate.getAttribute("ROW.STRNOME").toString():"";
			flgCodMonoIO = rowTemplate.getAttribute("ROW.STRIO")!=null?rowTemplate.getAttribute("ROW.STRIO").toString():"";

			// invoco lo statement GET_CDNCOMPONENTE per estrarre il CDNCOMPONENTE dalla TS_COMPONENTE
			// parameter: PAGE (già presente nella request)
			setSectionQuerySelect("QUERY_SELECT_COMPONENTE");
			SourceBean rowComponente = doSelect(request, response, false);
			Object cdnComponente = rowComponente.getAttribute("ROW.CDNCOMPONENTE");

			if (protocollazione) {

				// Insert in AM_DOCUMENTO
				// invoco lo statement INSERT_STAMPA_PARAM_LAV settando prima tutti i parametri necessari
				// PRGDOCUMENTO - CODCPI - CDNLAVORATORE - CODTIPODOCUMENTO - NOMETEMPLATEDESC - CODSTATOATTO
				// FLGCODMONOIO - DATAACQUISIZIONE - DATAINIZIO
				// PRGAZIENDA - PRGUNITA - DATAFINE - NOTE sono null
				progressivoDoc = getNextValAmDocumento(request, response); // setto PRGDOCUMENTO
				request.setAttribute("CODTIPODOCUMENTO", codTipoDoc);
				request.setAttribute("NOMETEMPLATEDESC", nomeTemplate);
				request.setAttribute("CODSTATOATTO", codStatoAtto);
				request.setAttribute("FLGCODMONOIO", flgCodMonoIO);
				request.setAttribute("DATAACQUISIZIONE", dataAcquisizione);
				request.setAttribute("DATAINIZIO", dataInizio);
				setSectionQueryInsert("QUERY_INSERT_DOC");
				result = doInsert(request, response);
				if (!result) {
					transExec.rollBackTransaction();
					reportOperation.reportFailure(errorCode);
					return;
				}

				// Insert in AM_DOCUMENTO_COLL
				// invoco lo statement INSERT_AM_DOCUMENTO_COLL settando prima tutti i parametri necessari
				// PRGDOCUMENTOCOLL - PRGDOCUMENTO - CDNCOMPONENTE - PRGTEMPLATESTAMPA
				getNextValAmDocumentoColl(request, response); // setto PRGDOCUMENTOCOLL
				request.setAttribute("CDNCOMPONENTE", cdnComponente);
				setSectionQueryInsert("QUERY_INSERT_DOC_COLL");
				result = doInsert(request, response);
				if (!result) {
					transExec.rollBackTransaction();
					reportOperation.reportFailure(errorCode);
					return;
				}

				// Insert in AM_DOCUMENTO_BLOB
				// invoco lo statement INSERT_AM_DOCUMENTO_BLOB settando prima tutti i parametri necessari
				// PRGDOCUMENTOBLOB - PRGDOCUMENTO
				getNextValAmDocumentoBlob(request, response); // setto PRGDOCUMENTOBLOB
				setSectionQueryInsert("QUERY_INSERT_DOC_BLOB");
				result = doInsert(request, response);
				if (!result) {
					transExec.rollBackTransaction();
					reportOperation.reportFailure(errorCode);
					return;
				}

			} // FINE inserimento

			String htmlDoc = replaceMarcatoriTemplateStampa();

			protocollaVisualizzaDocumento(transExec, request, protocollazione, htmlDoc, progressivoDoc);

			transExec.commitTransaction();
			response.setAttribute("ESITOELABORAZIONE", "OK");
			// setto attributi nella response del module solo se e' stato effettuata la protocollazione
			if (progressivoDoc != null) {
				response.setAttribute("PRGDOCUMENTO", progressivoDoc);
			}
		} catch (Throwable ex) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "MElaboraStampaParametrica.service()", ex);
			response.setAttribute("ESITOELABORAZIONE", "KO");
			reportOperation.reportFailure(errorCode);
		}

	}

	private void protocollaVisualizzaDocumento(TransactionQueryExecutor trans, SourceBean request,
			boolean protocollazione, String htmlDoc, BigDecimal prgDocumento) throws Exception {
		String token = "src=\"";
		String protocol = "";
		String host = "";
		String address = "";
		Documento doc = null;
		SessionContainer sessionContainer = (SessionContainer) getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		BigDecimal userid = new BigDecimal(user.getCodut());

		if (request.getAttribute("HOST") != null && !request.getAttribute("HOST").equals("")) {
			protocol = (String) request.getAttribute("PROTOCOL");
			host = (String) request.getAttribute("HOST");
			address = protocol + "//" + host;
			htmlDoc = htmlDoc.replaceAll(token, token + address);
		}

		boolean footerContentUMB = false;

		String tagFooterUMB = "@UMBFooter";
		int pos =htmlDoc.indexOf( tagFooterUMB);
		if(pos !=-1){
			htmlDoc = htmlDoc.substring(0,pos).concat(htmlDoc.substring(pos  + (tagFooterUMB.length())));
			footerContentUMB=true;
		}

		if (protocollazione) {
			
			doc = new Documento();
			doc.setPrgDocumento(prgDocumento);
			doc.selectStampaParam(trans.getDataConnection());

			SourceBean rowProt = (SourceBean) trans.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
			if (rowProt == null) {
				throw new Exception("impossibile protocollare il documento di identificazione");
			}
			rowProt = (rowProt.containsAttribute("ROW") ? (SourceBean) rowProt.getAttribute("ROW") : rowProt);

			BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
			BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
			String datProtocollazione = (String) rowProt.getAttribute("DATAORAPROT");
			String flgprotocolloaut = (String) rowProt.getAttribute("flgprotocolloaut");

			doc.setNumAnnoProt(numAnnoProt);
			doc.setNumProtocollo(numProtocollo);
			doc.setDatProtocollazione(datProtocollazione);
			doc.setTipoProt(flgprotocolloaut);
			doc.setStrNomeDoc("StampaParametrica.PDF");

			doc.leggiNewNumProt(trans); // legge il numero effettivo protocollo
			doc.setCodStatoAtto(Properties.STATO_ATTO_PROTOC);
			doc.setCdnUtMod(userid);
			BigDecimal newNumklo = doc.getNumKloDocumento();
			newNumklo = newNumklo.add(new BigDecimal("1"));
			doc.setNumKloDocumento(newNumklo);
			doc.setChiaveTabella(prgDocumento.toString());

			htmlDoc = htmlDoc.replaceAll("@AnnoProt", doc.getNumAnnoProt().toPlainString());
			htmlDoc = htmlDoc.replaceAll("@NumProt", doc.getNumProtocollo().toPlainString());
			htmlDoc = htmlDoc.replaceAll("@DataProt", doc.getDatProtocollazione());

			_logger.debug("HTML prima della checked box transformation: \n" + htmlDoc);
			StringBuffer strCheckedBoxes = findCheckedBoxes(new StringBuffer(htmlDoc));
			htmlDoc = strCheckedBoxes.toString();
			_logger.debug("HTML dopo la checked box transformation: \n" + htmlDoc);

			htmlDoc = htmlDoc.replaceAll("<input.+?type=\"checkbox\".+?/>", "[ ] ");

			// marcatori per la firma grafometrica - per il momento non usati
			String dd1 = "<span style=\"font-size:15%;opacity: 0.4;\" >@DD_1</span>";
			htmlDoc = htmlDoc.replaceAll("@DD_1", dd1);
			String dd2 = "<span style=\"font-size:15%;opacity: 0.4;\">@DD_2</span>";
			htmlDoc = htmlDoc.replaceAll("@DD_2", dd2);
			String di1 = "<span style=\"font-size:15%;opacity: 0.4;\">@DI_1</span>";
			htmlDoc = htmlDoc.replaceAll("@DI_1", di1);
			String di2 = "<span style=\"font-size:15%;opacity: 0.4;\">@DI_2</span>";
			htmlDoc = htmlDoc.replaceAll("@DI_2", di2);

			_logger.debug("inizio htmlDoc: " + htmlDoc + "FINE");
			_logger.debug("****END TESTO TO PDF****");

			if (htmlDoc.equals(""))
				htmlDoc = "<p><br /></p>";
			_logger.debug("inizio htmlDoc: " + htmlDoc + "FINE");

			_logger.debug("Inserisco il footer");			
			
			
			it.eng.sil.module.umbria.ConvertToPdf convertToPdf = new it.eng.sil.module.umbria.ConvertToPdf();
			
			File tempFile = File.createTempFile("~rpt", ".out");
			
			doc.setTempFile(tempFile);
			OutputStream osInf = new FileOutputStream(doc.getTempFile().getAbsoluteFile());
			
			OutputStream osOutf = convertToPdf.createPDF(htmlDoc, osInf, footerContentUMB);
			
			doc.inserisciBlob(trans);
			
			doc.update(trans);  // aggiorna oltre il blob anche il numero protocollo usato
			
		} else {
			_logger.debug("***TESTO TO PDF*****");

			_logger.debug("****ip:" + request.getAttribute("HOST") + "*****");
			_logger.debug("****port:" + request.getAttribute("PORT") + "*****");
			_logger.debug("****" + htmlDoc + "****");
			_logger.debug("****END TESTO TO PDF****");
			
			// solo visualizzazione togliere i marcatori relativi al protocollo
			if (htmlDoc.equals("")) {
				htmlDoc = "<p><br /></p>";
			} else {
				htmlDoc = htmlDoc.replaceAll("@AnnoProt", "");
				htmlDoc = htmlDoc.replaceAll("@NumProt", "");
				htmlDoc = htmlDoc.replaceAll("@DataProt", "");
			}
			
			
			it.eng.sil.module.umbria.ConvertToPdf convertToPdf = new it.eng.sil.module.umbria.ConvertToPdf();

			File tempFile = File.createTempFile("~rpt", ".out");
			OutputStream osIn = new FileOutputStream(tempFile.getAbsoluteFile());
			
			OutputStream osOut = convertToPdf.createPDF(htmlDoc, osIn, true);
			
			osOut.flush();
			osOut.close();
			
			sessionContainer.setAttribute("FILE_TO_VIEW", tempFile.getAbsolutePath());
		}

	}

	private String replaceMarcatoriTemplateStampa() {
		SourceBean serviceResponse = getResponseContainer().getServiceResponse();
		String fileTemplate = "";

		// CARICA I DATI DEL CPI
		String indirizzoStampa = "", codCpi = "", descCpi = "", indirizzo = "", localita = "", cap = "", codCom = "",
				codProvincia = "", comune = "", provincia = "", telefono = "", fax = "", CPIEmail = "", orario = "",
				responsabile = "", emailPec = "", emailAdl = "";
		SourceBean rowCpi = null;
		if (serviceResponse.containsAttribute("MDATICPI")) {
			rowCpi = (SourceBean) serviceResponse.getAttribute("MDATICPI.ROWS.ROW");
			if (rowCpi != null) {
				indirizzoStampa = rowCpi.containsAttribute("INDIRIZZO_STAMPA")
						? rowCpi.getAttribute("INDIRIZZO_STAMPA").toString()
						: "";
				codCpi = rowCpi.containsAttribute("COD_CPI") ? rowCpi.getAttribute("COD_CPI").toString() : "";
				descCpi = rowCpi.containsAttribute("DESC_CPI") ? rowCpi.getAttribute("DESC_CPI").toString() : "";
				indirizzo = rowCpi.containsAttribute("INDIRIZZO") ? rowCpi.getAttribute("INDIRIZZO").toString() : "";
				localita = rowCpi.containsAttribute("LOCALITA") ? rowCpi.getAttribute("LOCALITA").toString() : "";
				cap = rowCpi.containsAttribute("CAP") ? rowCpi.getAttribute("CAP").toString() : "";
				codCom = rowCpi.containsAttribute("CODCOM") ? rowCpi.getAttribute("CODCOM").toString() : "";
				codProvincia = rowCpi.containsAttribute("CODPROVINCIA") ? rowCpi.getAttribute("CODPROVINCIA").toString()
						: "";
				comune = rowCpi.containsAttribute("COMUNE") ? rowCpi.getAttribute("COMUNE").toString() : "";
				provincia = rowCpi.containsAttribute("PROVINCIA") ? rowCpi.getAttribute("PROVINCIA").toString() : "";
				telefono = rowCpi.containsAttribute("TELEFONO") ? rowCpi.getAttribute("TELEFONO").toString() : "";
				fax = rowCpi.containsAttribute("FAX") ? rowCpi.getAttribute("FAX").toString() : "";
				CPIEmail = rowCpi.containsAttribute("EMAIL") ? rowCpi.getAttribute("EMAIL").toString() : "";
				orario = rowCpi.containsAttribute("ORARIO") ? rowCpi.getAttribute("ORARIO").toString() : "";
				responsabile = rowCpi.containsAttribute("RESPONSABILE") ? rowCpi.getAttribute("RESPONSABILE").toString()
						: "";
				emailPec = rowCpi.containsAttribute("EMAILPEC") ? rowCpi.getAttribute("EMAILPEC").toString() : "";
				emailAdl = rowCpi.containsAttribute("EMAILADL") ? rowCpi.getAttribute("EMAILADL").toString() : "";

			}
		}

		// CARICA I DATI DEL LAVORATORE
		String codiceFiscale = "", cognomeLavoratore = "", nomeLavoratore = "", sesso = "", dataNascita = "",
				comuneNascita = "", provinciaNascita = "", cellulare = "", email = "", cittadinanza = "", nazione = "",
				secondaCittadinanza = "", secondaNazionalita = "", comuneResidenza = "", provinciaResidenza = "",
				indirizzoResidenza = "", localitaResidenza = "", capResidenza = "", comuneDomicilio = "",
				provinciaDomicilio = "", indirizzoDomicilio = "", localitaDomicilio = "", capDomicilio = "",
				telefonoResidenza = "", telefonoDomicilio = "";
		SourceBean rowLav = null;
		if (serviceResponse.containsAttribute("MDATILAVORATORE")) {
			rowLav = (SourceBean) serviceResponse.getAttribute("MDATILAVORATORE.ROWS.ROW");
			if (rowLav != null) {
				codiceFiscale = rowLav.containsAttribute("STRCODICEFISCALE")
						? rowLav.getAttribute("STRCODICEFISCALE").toString()
						: "";
				cognomeLavoratore = rowLav.containsAttribute("STRCOGNOME")
						? rowLav.getAttribute("STRCOGNOME").toString()
						: "";
				nomeLavoratore = rowLav.containsAttribute("STRNOME") ? rowLav.getAttribute("STRNOME").toString() : "";
				sesso = rowLav.containsAttribute("STRSESSO") ? rowLav.getAttribute("STRSESSO").toString() : "";
				dataNascita = rowLav.containsAttribute("DATNASC") ? rowLav.getAttribute("DATNASC").toString() : "";
				comuneNascita = rowLav.containsAttribute("STRCOMNAS") ? rowLav.getAttribute("STRCOMNAS").toString()
						: "";
				provinciaNascita = rowLav.containsAttribute("STRPROVNAS") ? rowLav.getAttribute("STRPROVNAS").toString()
						: "";
				cellulare = rowLav.containsAttribute("STRCELL") ? rowLav.getAttribute("STRCELL").toString() : "";
				email = rowLav.containsAttribute("STREMAIL") ? rowLav.getAttribute("STREMAIL").toString() : "";
				cittadinanza = rowLav.containsAttribute("STRCITTADINANZA")
						? rowLav.getAttribute("STRCITTADINANZA").toString()
						: "";
				nazione = rowLav.containsAttribute("STRNAZIONE") ? rowLav.getAttribute("STRNAZIONE").toString() : "";
				secondaCittadinanza = rowLav.containsAttribute("STRCITTADINANZA2")
						? rowLav.getAttribute("STRCITTADINANZA2").toString()
						: "";
				secondaNazionalita = rowLav.containsAttribute("STRNAZIONE2")
						? rowLav.getAttribute("STRNAZIONE2").toString()
						: "";
				comuneResidenza = rowLav.containsAttribute("STRCOMRES") ? rowLav.getAttribute("STRCOMRES").toString()
						: "";
				provinciaResidenza = rowLav.containsAttribute("PROVRES") ? rowLav.getAttribute("PROVRES").toString()
						: "";
				indirizzoResidenza = rowLav.containsAttribute("STRINDIRIZZORES")
						? rowLav.getAttribute("STRINDIRIZZORES").toString()
						: "";
				localitaResidenza = rowLav.containsAttribute("STRLOCALITARES")
						? rowLav.getAttribute("STRLOCALITARES").toString()
						: "";
				capResidenza = rowLav.containsAttribute("STRCAPRES") ? rowLav.getAttribute("STRCAPRES").toString() : "";
				comuneDomicilio = rowLav.containsAttribute("STRCOMDOM") ? rowLav.getAttribute("STRCOMDOM").toString()
						: "";
				provinciaDomicilio = rowLav.containsAttribute("PROVDOM") ? rowLav.getAttribute("PROVDOM").toString()
						: "";
				indirizzoDomicilio = rowLav.containsAttribute("STRINDIRIZZODOM")
						? rowLav.getAttribute("STRINDIRIZZODOM").toString()
						: "";
				localitaDomicilio = rowLav.containsAttribute("STRLOCALITADOM")
						? rowLav.getAttribute("STRLOCALITADOM").toString()
						: "";
				capDomicilio = rowLav.containsAttribute("STRCAPDOM") ? rowLav.getAttribute("STRCAPDOM").toString() : "";
				telefonoResidenza = rowLav.containsAttribute("STRTELRES") ? rowLav.getAttribute("STRTELRES").toString()
						: "";
				telefonoDomicilio = rowLav.containsAttribute("STRTELDOM") ? rowLav.getAttribute("STRTELDOM").toString()
						: "";
			}
		}
		
		
		// CARICA I DATI DELL'AZIENDA
		String ragSociale = "", partitaIva = "", codiceFiscaleAzienda = "", naturaGiuridica = "", tipoAzienda = "",
				sitoInternet = "", attivaAteco = "", numSoci = "", numDipendenti = "", numCollaboratori = "",
				numAltraPosizione = "", dataInizioAttivita = "", dataFineAttivita = "", patInail = "", flgObbl68 = "",
				numAlboInterinali = "", numAgSomministrazione = "", dataCambioRagsociale = "";
		SourceBean rowAz = null;
		if (serviceResponse.containsAttribute("MDATIAZIENDA")) {
			rowAz = (SourceBean) serviceResponse.getAttribute("MDATIAZIENDA.ROWS.ROW");
			if (rowAz != null) {
				ragSociale = rowAz.containsAttribute("RagSociale") ? rowAz.getAttribute("RagSociale").toString() : "";
				partitaIva = rowAz.containsAttribute("PartitaIva") ? rowAz.getAttribute("PartitaIva").toString() : "";
				codiceFiscaleAzienda = rowAz.containsAttribute("CodiceFiscale")
						? rowAz.getAttribute("CodiceFiscale").toString()
						: "";
				naturaGiuridica = rowAz.containsAttribute("NaturaGiuridica")
						? rowAz.getAttribute("NaturaGiuridica").toString()
						: "";
				tipoAzienda = rowAz.containsAttribute("TipoAzienda") ? rowAz.getAttribute("TipoAzienda").toString()
						: "";
				sitoInternet = rowAz.containsAttribute("SitoInternet") ? rowAz.getAttribute("SitoInternet").toString()
						: "";
				attivaAteco = rowAz.containsAttribute("AttivAteco") ? rowAz.getAttribute("AttivAteco").toString() : "";
				numSoci = rowAz.containsAttribute("NumSoci") ? rowAz.getAttribute("NumSoci").toString() : "";
				numDipendenti = rowAz.containsAttribute("NumDipendenti")
						? rowAz.getAttribute("NumDipendenti").toString()
						: "";
				numCollaboratori = rowAz.containsAttribute("NumCollaboratori")
						? rowAz.getAttribute("NumCollaboratori").toString()
						: "";
				numAltraPosizione = rowAz.containsAttribute("NumAltraPosizione")
						? rowAz.getAttribute("NumAltraPosizione").toString()
						: "";
				dataInizioAttivita = rowAz.containsAttribute("DataInizioAttivita")
						? rowAz.getAttribute("DataInizioAttivita").toString()
						: "";
				dataFineAttivita = rowAz.containsAttribute("DataFineAttivita")
						? rowAz.getAttribute("DataFineAttivita").toString()
						: "";
				patInail = rowAz.containsAttribute("PatInail") ? rowAz.getAttribute("PatInail").toString() : "";
				flgObbl68 = rowAz.containsAttribute("FlgObbl68") ? rowAz.getAttribute("FlgObbl68").toString() : "";
				numAlboInterinali = rowAz.containsAttribute("NumAlboInterinali")
						? rowAz.getAttribute("NumAlboInterinali").toString()
						: "";
				numAgSomministrazione = rowAz.containsAttribute("NumAgSomministrazione")
						? rowAz.getAttribute("NumAgSomministrazione").toString()
						: "";
				dataCambioRagsociale = rowAz.containsAttribute("DataCambioRagsociale")
						? rowAz.getAttribute("DataCambioRagsociale").toString()
						: "";
			}
		}
		
		
		// CARICA I DATI STATO OCCUPAZIONALE LAVORATORE
		SourceBean rowOcc = null;
		String descrizioneStatoOcc = "", mesiAnzianita = "", giorniAnzianita = "";
		if (serviceResponse.containsAttribute("M_GetInfoStatoOccDatiLavoratore")) {
			rowOcc = (SourceBean) serviceResponse.getAttribute("M_GetInfoStatoOccDatiLavoratore.ROWS.ROW");
			if (rowOcc != null) {
				descrizioneStatoOcc = rowOcc.containsAttribute("DescrizioneStato") ? rowOcc.getAttribute("DescrizioneStato").toString()
						: "";
				mesiAnzianita = rowOcc.containsAttribute("mesiAnzianita") ? rowOcc.getAttribute("mesiAnzianita").toString()
						: "";
				giorniAnzianita = rowOcc.containsAttribute("giorniAnzianita") ? rowOcc.getAttribute("giorniAnzianita").toString()
						: "";
			}
		}
		// CARICA I DATI DA an_lav_storia_inf E de_cpi
		SourceBean rowLavInf = null;
		String descrizioneCpiTrasferimento = "", dataInizioTrasferimentoCpi = "";
		if (serviceResponse.containsAttribute("M_GetLavStoriaInfoStampeParam")) {
			rowLavInf = (SourceBean) serviceResponse.getAttribute("M_GetLavStoriaInfoStampeParam.ROWS.ROW");
			if (rowLavInf != null) {
				descrizioneCpiTrasferimento = rowLavInf.containsAttribute("descrizioneCpiTrasferimento") ? rowLavInf.getAttribute("descrizioneCpiTrasferimento").toString()
						: "";
				dataInizioTrasferimentoCpi = rowLavInf.containsAttribute("dataInizioTrasferimentoCpi") ? rowLavInf.getAttribute("dataInizioTrasferimentoCpi").toString()
						: "";
			}
		}
		
		// CARICA I DATI InfoStatoIscrizioneCM
		SourceBean rowStatoInfoIscrCM = null;
		String DataInizioCM ="";
		String categoriaCM = "";
		String dataanzianital68 = "", numiscrizione = "";
		if (serviceResponse.containsAttribute("M_GetInfoStatoIscrizioneCM")) {
			rowStatoInfoIscrCM = (SourceBean) serviceResponse.getAttribute("M_GetInfoStatoIscrizioneCM.ROWS.ROW");
			if (rowStatoInfoIscrCM != null) {
				dataanzianital68 = rowStatoInfoIscrCM.containsAttribute("dataanzianital68") ? rowStatoInfoIscrCM.getAttribute("dataanzianital68").toString()
						: "";
				numiscrizione = rowStatoInfoIscrCM.containsAttribute("numiscrizione") ? rowStatoInfoIscrCM.getAttribute("numiscrizione").toString()
						: "";
				categoriaCM = rowStatoInfoIscrCM.containsAttribute("descrizione") ? rowStatoInfoIscrCM.getAttribute("descrizione").toString() : "";
				DataInizioCM = rowStatoInfoIscrCM.containsAttribute("DATULTIMAISCR") ? rowStatoInfoIscrCM.getAttribute("DATULTIMAISCR").toString() : "";
			}
		}
		
		
		//CARICA I DATI DID DEL LAVORATORE
		SourceBean rowDid = null;
		String DATADID = "";
		SourceBean moduleDid = (SourceBean) serviceResponse.getAttribute("MDatiDidLavoratore");
		if (serviceResponse.containsAttribute("MDatiDidLavoratore")) {
			rowDid = (SourceBean) serviceResponse.getAttribute("MDatiDidLavoratore.ROWS.ROW");
			if (rowDid != null) {
				DATADID = rowDid.containsAttribute("datdichiarazione") ? rowDid.getAttribute("datdichiarazione").toString() : "";
			}	
		}

		/*
		 * 
		 * //CARICA I DATI LEGALI DELL' AZIENDA //System.out.println("CARICA I DATI LEGALI AZIENDA"); SourceBean
		 * datiLegAz = (SourceBean) serviceResponse .getAttribute("MDATILEGALIAZIENDA"); SourceBean rowLegAz =
		 * (SourceBean) datiLegAz.getAttribute("ROWS.ROW"); if (rowLegAz != null) { DenominazioneSedeLegale =
		 * rowLegAz.containsAttribute("DenominazioneSedeLegale") ?
		 * rowLegAz.getAttribute("DenominazioneSedeLegale").toString() : ""; IndirizzoSedeLegale =
		 * rowLegAz.containsAttribute("IndirizzoSedeLegale") ? rowLegAz.getAttribute("IndirizzoSedeLegale").toString() :
		 * ""; ComuneSedeLegale = rowLegAz.containsAttribute("ComuneSedeLegale") ?
		 * rowLegAz.getAttribute("ComuneSedeLegale").toString() : ""; CapSedeLegale =
		 * rowLegAz.containsAttribute("CapSedeLegale") ? rowLegAz.getAttribute("CapSedeLegale").toString() : "";
		 * TelSedeLegale = rowLegAz.containsAttribute("TelSedeLegale") ?
		 * rowLegAz.getAttribute("TelSedeLegale").toString() : ""; FaxSedeLegale =
		 * rowLegAz.containsAttribute("FaxSedeLegale") ? rowLegAz.getAttribute("FaxSedeLegale").toString() : "";
		 * EmailSedeLegale = rowLegAz.containsAttribute("EmailSedeLegale") ?
		 * rowLegAz.getAttribute("EmailSedeLegale").toString() : "";
		 * //System.out.println("CARICA I DATI LEGALI AZIENDA ...OK"); }
		 */

		// faccio la replace
		SourceBean cont4 = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOEDITORLAV");
		SourceBean row4 = (SourceBean) cont4.getAttribute("ROWS.ROW");
		fileTemplate = row4.containsAttribute("FILETEMPLATE") ? row4.getAttribute("FILETEMPLATE").toString() : "";
		fileTemplate = fileTemplate.replace("\n", "").replace("\r", "");



		// sostituisco gli ALIAS con i dati del lavoratore
		fileTemplate = fileTemplate.replace("@NomeLavoratore", nomeLavoratore);
		fileTemplate = fileTemplate.replace("@CognomeLavoratore", cognomeLavoratore);
		fileTemplate = fileTemplate.replace("@CodiceFiscale", codiceFiscale);
		fileTemplate = fileTemplate.replace("@Sesso", sesso);
		fileTemplate = fileTemplate.replace("@DataNascita", dataNascita);
		fileTemplate = fileTemplate.replace("@Nazione", nazione);
		fileTemplate = fileTemplate.replace("@Cittadinanza", cittadinanza);
		fileTemplate = fileTemplate.replace("@SecondaCittadinanza", secondaCittadinanza);
		fileTemplate = fileTemplate.replace("@ComuneNascita", comuneNascita);
		fileTemplate = fileTemplate.replace("@ProvinciaNascita", provinciaNascita);
		fileTemplate = fileTemplate.replace("@Cellulare", cellulare);
		fileTemplate = fileTemplate.replace("@Email", email);
		fileTemplate = fileTemplate.replace("@IndirizzoDomicilio", indirizzoDomicilio);
		fileTemplate = fileTemplate.replace("@IndirizzoResidenza", indirizzoResidenza);
		fileTemplate = fileTemplate.replace("@ComuneDomicilio", comuneDomicilio);
		fileTemplate = fileTemplate.replace("@ComuneResidenza", comuneResidenza);
		fileTemplate = fileTemplate.replace("@SecondaNazionalita", secondaNazionalita);
		fileTemplate = fileTemplate.replace("@ProvinciaResidenza", provinciaResidenza);
		fileTemplate = fileTemplate.replace("@LocalitaResidenza", localitaResidenza);
		fileTemplate = fileTemplate.replace("@CapResidenza", capResidenza);
		fileTemplate = fileTemplate.replace("@ProvinciaDomicilio", provinciaDomicilio);
		fileTemplate = fileTemplate.replace("@LocalitaDomicilio", localitaDomicilio);
		fileTemplate = fileTemplate.replace("@CapDomicilio", capDomicilio);
		fileTemplate = fileTemplate.replace("@TelefonoResidenza", telefonoResidenza);
		fileTemplate = fileTemplate.replace("@TelefonoDomicilio", telefonoDomicilio);
		fileTemplate = fileTemplate.replace("@DataDid", DATADID);
		fileTemplate = fileTemplate.replace("@CategoriaCM", categoriaCM);
		
		// sostituisco gli ALIAS con i dati dello stato occupazionale del lavoratore
		fileTemplate = fileTemplate.replace("@StatoOccupazionaleCorrente", descrizioneStatoOcc);
		fileTemplate = fileTemplate.replace("@MesiAnzianitaCorrente", mesiAnzianita);
		fileTemplate = fileTemplate.replace("@GiorniAnzianitaCorrente", giorniAnzianita);

		// sostituisco gli ALIAS con i dati del lavoratore e cpi
		fileTemplate = fileTemplate.replace("@DataTrasferimentoCpi", dataInizioTrasferimentoCpi);
		fileTemplate = fileTemplate.replace("@DescrizioneTrasferimentoCpi", descrizioneCpiTrasferimento);
		
		// sostituisco gli ALIAS con i iscrizione CM per L68
		fileTemplate = fileTemplate.replace("@DataAnazianitaL68", dataanzianital68);
		fileTemplate = fileTemplate.replace("@NumeroIscrizione", numiscrizione);
		
		// sostituisco gli ALIAS con i dati dell'azienda
		fileTemplate = fileTemplate.replace("@RagSociale", ragSociale);
		fileTemplate = fileTemplate.replace("@PartitaIva", partitaIva);
		fileTemplate = fileTemplate.replace("@CodiceFiscaleAzienda", codiceFiscale);
		fileTemplate = fileTemplate.replace("@NaturaGiuridica", naturaGiuridica);
		fileTemplate = fileTemplate.replace("@TipoAzienda", tipoAzienda);
		fileTemplate = fileTemplate.replace("@SitoInternet", sitoInternet);
		fileTemplate = fileTemplate.replace("@AttivAteco", attivaAteco);
		fileTemplate = fileTemplate.replace("@NumSoci", numSoci);
		fileTemplate = fileTemplate.replace("@NumDipendenti", numDipendenti);
		fileTemplate = fileTemplate.replace("@NumCollaboratori", numCollaboratori);
		fileTemplate = fileTemplate.replace("@NumAltraPosizione", numAltraPosizione);
		fileTemplate = fileTemplate.replace("@DataInizioAttivita", dataInizioAttivita);
		fileTemplate = fileTemplate.replace("@DataFineAttivita", dataFineAttivita);
		fileTemplate = fileTemplate.replace("@PatInail", patInail);
		fileTemplate = fileTemplate.replace("@FlgObbl68", flgObbl68);
		fileTemplate = fileTemplate.replace("@NumAlboInterinali", numAlboInterinali);
		fileTemplate = fileTemplate.replace("@NumAgSomministrazione", numAgSomministrazione);
		fileTemplate = fileTemplate.replace("@DataCambioRagsociale", dataCambioRagsociale);
		
		// sostituisco gli ALIAS con la data iscrizione CM
		fileTemplate = fileTemplate.replace("@DataIscrCM", DataInizioCM);
				
		// sostituisco gli ALIAS con i dati del CPI
		fileTemplate = fileTemplate.replace("@CodiceCPI", codCpi);
		fileTemplate = fileTemplate.replace("@Nome", descCpi);
		fileTemplate = fileTemplate.replace("@Indirizzo", indirizzo);
		fileTemplate = fileTemplate.replace("@Localita", localita);
		fileTemplate = fileTemplate.replace("@Cap", cap);
		fileTemplate = fileTemplate.replace("@CodComune", codCom);
		fileTemplate = fileTemplate.replace("@CodProvincia", codProvincia);
		fileTemplate = fileTemplate.replace("@Comune", comune);
		fileTemplate = fileTemplate.replace("@Provincia", provincia);
		fileTemplate = fileTemplate.replace("@Telefono", telefono);
		fileTemplate = fileTemplate.replace("@Fax", fax);
		fileTemplate = fileTemplate.replace("@CPIEmail", CPIEmail);
		fileTemplate = fileTemplate.replace("@Orario", orario);
		fileTemplate = fileTemplate.replace("@Responsabile", responsabile);
		fileTemplate = fileTemplate.replace("@emailPEC", emailPec);
		fileTemplate = fileTemplate.replace("@emailADL", emailAdl);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String dataStr = sdf.format(new java.util.Date());
		fileTemplate = fileTemplate.replace("@DataSistema", dataStr);

		return fileTemplate;

	}

	private BigDecimal getNextValAmDocumento(SourceBean request, SourceBean response) throws SourceBeanException {
		request.delAttribute("PRGDOCUMENTO");
		setSectionQuerySelect("QUERY_NEXT_VAL");
		SourceBean rowDoc = doSelect(request, response, false);
		BigDecimal progressivoDoc = (BigDecimal) rowDoc.getAttribute("ROW.KEY");
		request.setAttribute("PRGDOCUMENTO", progressivoDoc);
		return progressivoDoc;
	}

	private void getNextValAmDocumentoColl(SourceBean request, SourceBean response) throws SourceBeanException {
		request.delAttribute("PRGDOCUMENTOCOLL");
		setSectionQuerySelect("QUERY_NEXT_VAL_COLL");
		SourceBean rowDocColl = doSelect(request, response, false);
		Object progressivoDocColl = rowDocColl.getAttribute("ROW.KEY");
		request.setAttribute("PRGDOCUMENTOCOLL", progressivoDocColl);
	}

	private void getNextValAmDocumentoBlob(SourceBean request, SourceBean response) throws SourceBeanException {
		request.delAttribute("PRGDOCUMENTOBLOB");
		setSectionQuerySelect("QUERY_NEXT_VAL_BLOB");
		SourceBean rowDocBlob = doSelect(request, response, false);
		Object progressivoDocBlob = rowDocBlob.getAttribute("ROW.KEY");
		request.setAttribute("PRGDOCUMENTOBLOB", progressivoDocBlob);
	}

	private StringBuffer findCheckedBoxes(StringBuffer strHtml) {
		Pattern pattern = Pattern.compile("<input.+?type=\"checkbox\".+?/>");
		Matcher matcher = pattern.matcher(strHtml);

		while (matcher.find()) {

			int start = matcher.start();
			int end = matcher.end();

			if (matcher.group().contains("checked")) {
				strHtml.replace(start, end, "[x]");

				StringBuffer strHtmlChecked = findCheckedBoxes(strHtml);
				break;
			}

		}

		return strHtml;
	}

}
