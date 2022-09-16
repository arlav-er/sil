package it.eng.sil.module.documenti;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractHttpModule;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;

import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;

/**
 * @author vuoto / Antenucci
 */

public class SalvaDocumento extends AbstractHttpModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SalvaDocumento.class.getName());

	private final String className = StringUtils.getClassName(this);

	private SourceBean request = null;

	private HttpServletRequest httpRequest = null;
	private HttpServletResponse httpResponse = null;
	private ServletConfig servletConfig = null;
	private Request jspRequest = null;
	private com.jspsmart.upload.File myFile = null;
	private String filename = null;
	private java.io.File file = null;

	private SmartUpload mySmartUpload = null;

	private BigDecimal nuovoPrgDocumento = null;

	private void initialize() throws ServletException, IOException, SmartUploadException {
		httpRequest = this.getHttpRequest();
		httpResponse = this.getHttpResponse();
		servletConfig = this.getServletConfig();

		mySmartUpload = new SmartUpload();
		mySmartUpload.initialize(servletConfig, httpRequest, httpResponse);
		jspRequest = mySmartUpload.getRequest();
		mySmartUpload.setMaxFileSize(5 * 1024 * 1024);
		mySmartUpload.upload();

	}

	public void service(SourceBean request, SourceBean response) {

		_logger.debug(className + ".service() INIZIO");

		Documento doc = null;
		TransactionQueryExecutor transExecutor = null;
		BigDecimal prgDocDaJsp = null;
		boolean doInsert = true;

		try {

			initialize();
			prgDocDaJsp = getAttributeAsBigDecimal("prgDocumento");
			doInsert = (prgDocDaJsp == null);
			// Savino 15/09/05: per consentire un corretto aggiornamento anche
			// per documenti protocollati
			// bisogna prima leggere il documento stesso
			if (doInsert) {
				doc = new Documento();
			} else {
				doc = new Documento(prgDocDaJsp); // questo costruttore legge il documento da db
			}

			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);
			BigDecimal userid = new BigDecimal(user.getCodut());

			transExecutor = new TransactionQueryExecutor(Values.DB_SIL_DATI, this);
			transExecutor.initTransaction();

			// controlli documenti doppi per did e patto
			if (doInsert) {
				String codTipoDoc = getAttributeAsString("codTipoDocumento");
				String strChiaveTabella = getAttributeAsString("strChiaveTabella");
				if (!strChiaveTabella.equalsIgnoreCase("null") && !strChiaveTabella.equals("")) {
					if (codTipoDoc.equalsIgnoreCase(Properties.DEFAULT_DOCUMENTO_PATTO)
							|| codTipoDoc.equalsIgnoreCase(Properties.DOCUMENTO_PATTO_GENERICO)) {
						// CONTROLLO DOCUMENTI DOPPI PER IL PATTO
						Object[] objDoppi = new Object[3];
						objDoppi[0] = codTipoDoc;
						objDoppi[1] = new BigDecimal("14");
						objDoppi[2] = strChiaveTabella;
						SourceBean sbDocDoppi = (SourceBean) transExecutor.executeQuery("GET_DOCUMENTO_DOPPIO_PATTO",
								objDoppi, "SELECT");
						if (sbDocDoppi != null && sbDocDoppi.getAttribute("ROW") != null) {
							throw new EMFUserError(EMFErrorSeverity.BLOCKING,
									MessageCodes.Protocollazione.ERR_DOC_PATTO_ESISTENTE);
						}
					} else {
						if (codTipoDoc.equalsIgnoreCase("IM")) {
							Object[] objDoppi = new Object[3];
							objDoppi[0] = "IM";
							objDoppi[1] = new BigDecimal("25");
							objDoppi[2] = strChiaveTabella;
							SourceBean sbDocDoppi = (SourceBean) transExecutor.executeQuery("GET_DOCUMENTO_DOPPIO_DID",
									objDoppi, "SELECT");
							if (sbDocDoppi != null && sbDocDoppi.getAttribute("ROW") != null) {
								throw new EMFUserError(EMFErrorSeverity.BLOCKING,
										MessageCodes.Protocollazione.ERR_DOC_DID_ESISTENTE);
							}
						}
					}
				}
			}

			String sostituisciIdentificazione = getAttributeAsString("sostituisciIdentificazione");

			doc.setPrgDocumento(prgDocDaJsp);
			doc.setCodCpi(getAttributeAsString("codCpi"));
			doc.setCdnLavoratore(getAttributeAsBigDecimal("cdnLavoratore"));
			doc.setPrgAzienda(getAttributeAsBigDecimal("prgAzienda"));
			doc.setPrgUnita(getAttributeAsBigDecimal("prgUnita"));

			doc.setCodTipoDocumento(getAttributeAsString("codTipoDocumento"));
			doc.setFlgAutocertificazione(getAttributeAsString("flgAutocertificazione"));
			doc.setStrDescrizione(getAttributeAsString("strDescrizione"));
			doc.setFlgDocAmm(getAttributeAsString("flgDocAmm"));
			doc.setFlgDocIdentifP(getAttributeAsString("flgDocIdentifP"));
			doc.setDatInizio(getAttributeAsString("DatInizio"));
			doc.setStrNumDoc(getAttributeAsString("StrNumDoc"));
			doc.setStrEnteRilascio(getAttributeAsString("StrEnteRilascio"));
			doc.setCodMonoIO(getAttributeAsString("FlgCodMonoIO"));
			doc.setDatAcqril(getAttributeAsString("DatAcqril"));
			doc.setCodModalitaAcqril(getAttributeAsString("codModalitaAcqril"));
			doc.setCodTipoFile(getAttributeAsString("codTipoFile"));
			doc.setStrNomeDoc(getAttributeAsString("strNomeDoc"));
			doc.setDatFine(getAttributeAsString("datFine"));
			doc.setNumAnnoProt(getAttributeAsBigDecimal("numAnnoProt"));
			doc.setNumProtocollo(getAttributeAsBigDecimal("numProtocollo"));
			doc.setDatProtocollazione(getAttributeAsString("dataOraProt"));
			doc.setTipoProt(getAttributeAsString("tipoProt"));
			doc.setStrNote(getAttributeAsString("strNote"));
			doc.setNumKeyLock(getAttributeAsBigDecimal("KLOCKPROT"));
			doc.setChiaveTabella(getAttributeAsString("strChiaveTabella"));
			// Savino 15/09/05: aggiunto nuovo campo alla tabella
			doc.setCodMotAnnullamentoAtto(getAttributeAsString("codMotAnnullamentoAtto"));
			// Savino 15/09/05: aggiunti nuovi stati atto. Quindi bisogna
			// impostarli in modo esplicito
			// vedi metodo "Documento.lockNewNumProt()" che imposta il
			// codStatoAtto in base alla presenza
			// o meno dei campi di protocollazione
			doc.setCodStatoAtto(getAttributeAsString("CODSTATOATTO"));
			String pagina = getAttributeAsString("pagina");
			if (!pagina.equalsIgnoreCase("null"))
				doc.setPagina(pagina);

			doc.setCdnUtMod(userid);

			getUploadedFile();

			if (this.file != null) {
				// il file esiste
				doc.setTempFile(this.file);

				// Sovrascirvo il nome del file
				doc.setStrNomeDoc(this.filename);

			}

			// discrima il comportamento: se passo anche il progressivo
			// significa che sto inserendo
			// un nuovo documento, altrimenti voglio aggiornare uno già
			// esistente ?????????
			if (doInsert) {
				doc.setCdnUtIns(userid);
				doc.insert(transExecutor);
				nuovoPrgDocumento = doc.getPrgDocumento();
				// doc.select();
			} else {

				doc.setNumKloDocumento(getAttributeAsBigDecimal("numKloDocumento").add(new BigDecimal(1)));
				doc.update(transExecutor);
				// doc.select();
			}

			// Si esegue l'update del vecchio documento di identificazione
			// principale, se esiste.
			if ((sostituisciIdentificazione != null) && sostituisciIdentificazione.equals("true")) {
				doc.setPrgDocumento(getAttributeAsBigDecimal("prgDocIdentPrincipale"));
				doc.select();
				doc.setFlgDocIdentifP("N");
				doc.setNumKloDocumento(doc.getNumKloDocumento().add(new BigDecimal("1")));
				doc.update(transExecutor);
			}

			if (nuovoPrgDocumento != null) {
				doc.setPrgDocumento(nuovoPrgDocumento);
			}

			transExecutor.commitTransaction();
			// DOCAREA: cancello il file temporaneo inviato e protocollato
			ProtocolloDocumentoUtil.cancellaFileDocarea(doc);

			doc.select();

			MessageAppender.appendMessage(response, MessageCodes.General.OPERATION_SUCCESS);
			response.setAttribute("OPERAZIONI_OK", "true");
		} catch (SecurityException e1) {
			doc = gestisciErrore(doc, transExecutor, prgDocDaJsp, doInsert);

			if (e1.getMessage().indexOf("(1105)") > 0) {
				this.getErrorHandler()
						.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.MAX_SIZE_UPLOAD));
				it.eng.sil.util.TraceWrapper.debug(_logger, "SalvaDocumento: ", e1);
			} else {
				this.getErrorHandler()
						.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL));
				it.eng.sil.util.TraceWrapper.debug(_logger, "SalvaDocumento: ", e1);
			}

		} catch (Exception e) {

			doc = gestisciErrore(doc, transExecutor, prgDocDaJsp, doInsert);

			// DOCAREA: si recupera la eccezione generata dallo strategy
			if (e instanceof EMFUserError) {
				this.getErrorHandler().addError((EMFUserError) e);
			} else {
				// GG 24-01-05 :( Recupero codice SQL errore e lo mappo in
				// errCode per mostrarlo a video
				int errCode = MessageCodes.General.OPERATION_FAIL;
				if (e instanceof EMFInternalError) {
					Exception exNative = ((EMFInternalError) e).getNativeException();
					if ((exNative instanceof SQLException) && (exNative != null)) {
						int myErrCode = ((SQLException) exNative).getErrorCode();
						if (myErrCode >= 20000) {
							errCode = myErrCode;
						}
					}
				}
				this.getErrorHandler().addError(new EMFUserError(EMFErrorSeverity.ERROR, errCode));
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

		} // Setta i parametri noti da recuperare nella JSP
		finally {
			try {
				response.setAttribute("documento", doc);
				setParametriResponse(request, response);
				BigDecimal prgDoc = doc.getPrgDocumento();
				if (prgDoc != null) {
					response.setAttribute("PRGDOCUMENTO", prgDoc);
				}
			} catch (Exception e) {
				this.getErrorHandler()
						.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL));
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

			}
		}
		_logger.debug(className + ".service() FINE");

	}

	private Documento gestisciErrore(Documento doc, TransactionQueryExecutor transExecutor, BigDecimal prgDocDaJsp,
			boolean doInsert) {
		try {
			if (transExecutor != null)
				transExecutor.rollBackTransaction();
		} catch (EMFInternalError e1) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "SalvaDocumento: ", (Exception) e1);

		}

		// GG 27-01-05: se si stava inserendo, annullo la chiave del
		// documento:
		if (doInsert && doc != null) {
			doc.setPrgDocumento(null);
		}
		// Savino 15/09/05: casi particolari di fallimento dell'operazione;
		// necessario per un caricamento della
		// pagina almeno senza errori a runtime
		if (doInsert && doc == null) {
			doc = new Documento();
		}
		if (!doInsert) {
			if (prgDocDaJsp == null)
				doc = new Documento();
			else {// cerco di rileggere il documento
				try {
					doc = new Documento(prgDocDaJsp);
					doc.select();
				} catch (Exception e1) {
					// posso fare poco: ritorno un documento vuoto
					doc = new Documento();
				}
			}
		}
		return doc;
	}

	private BigDecimal getAttributeAsBigDecimal(String param) {
		String tmp = (String) jspRequest.getParameter(param);
		if (StringUtils.isFilled(tmp)) {
			return new BigDecimal(tmp);
		}
		return null;
	}

	private String getAttributeAsString(String param) {
		return (String) jspRequest.getParameter(param);
	}

	private void getUploadedFile() throws IOException, SmartUploadException {

		com.jspsmart.upload.File myFile = mySmartUpload.getFiles().getFile(0);

		if (!myFile.isMissing()) {

			filename = myFile.getFileName();

			file = java.io.File.createTempFile("UPLOAD", null, null);

			myFile.saveAs(file.getAbsolutePath(), SmartUpload.SAVE_PHYSICAL);
		}
	}

	private void setParametriResponse(SourceBean request, SourceBean response) throws SourceBeanException {

		// Parametri non recuperabili in altro modo nella jsp
		// per via del multipart...
		SourceBeanUtils.setAttrIfFilled(response, "cdnLavoratore", getAttributeAsString("cdnLavoratore"));
		SourceBeanUtils.setAttrIfFilled(response, "prgAzienda", getAttributeAsString("prgAzienda"));
		SourceBeanUtils.setAttrIfFilled(response, "prgUnita", getAttributeAsString("prgUnita"));
		SourceBeanUtils.setAttrIfFilled(response, "lookLavoratore", getAttributeAsString("lookLavoratore"));
		SourceBeanUtils.setAttrIfFilled(response, "lookAzienda", getAttributeAsString("lookAzienda"));
		SourceBeanUtils.setAttrIfFilled(response, "contesto", getAttributeAsString("contesto"));
		SourceBeanUtils.setAttrIfFilled(response, "pagina", getAttributeAsString("pagina"));
		SourceBeanUtils.setAttrIfFilled(response, "popUp", getAttributeAsString("popUp"));
		SourceBeanUtils.setAttrIfFilled(response, "cdnFunzione", getAttributeAsString("cdnFunzione"));
		SourceBeanUtils.setAttrIfFilled(response, "strChiaveTabella", getAttributeAsString("strChiaveTabella"));
		SourceBeanUtils.setAttrIfFilled(response, "goBackListPage", getAttributeAsString("goBackListPage"));

		SourceBeanUtils.setAttrIfFilled(response, "CARTAIDENTITA", getAttributeAsString("CARTAIDENTITA"));
		SourceBeanUtils.setAttrIfFilled(response, "aggiornaDatFineVal", getAttributeAsString("aggiornaDatFineVal"));
		SourceBeanUtils.setAttrIfFilled(response, "prgDocIdentPrincipale",
				getAttributeAsString("prgDocIdentPrincipale"));

		// Mi serve anche questo per quando salvo la sola data di fine validità
		// di un documento "storico"
		SourceBeanUtils.setAttrIfFilled(response, "infStoriche", getAttributeAsString("infStoriche"));

		SourceBeanUtils.setAttrIfFilled(response, "FRAME_NAME", getAttributeAsString("FRAME_NAME"));
		SourceBeanUtils.setAttrIfFilled(response, "QUERY_STRING", getAttributeAsString("QUERY_STRING"));
		SourceBeanUtils.setAttrIfFilled(response, "fromPattoDettaglio", getAttributeAsString("fromPattoDettaglio"));
	}

}