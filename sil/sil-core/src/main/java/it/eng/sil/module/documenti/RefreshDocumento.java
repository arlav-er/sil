package it.eng.sil.module.documenti;

import java.io.IOException;
import java.math.BigDecimal;

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
import com.engiweb.framework.error.EMFUserError;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.bean.Documento;
import it.eng.sil.security.User;

/**
 * @author De Simone Viene utilizzata per settare il documento con i dati inseriti dall'utente. Questo si verifica
 *         quando l'utente inserisce i dati nel form e successivamente decide di protocollare, la cui operazione
 *         comporta il refresh della pagina per recuperare le giuste informazioni sulla protocollazione.
 */

public class RefreshDocumento extends AbstractHttpModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RefreshDocumento.class.getName());

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

		Documento doc = new Documento();

		try {

			initialize();

			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);
			BigDecimal userid = new BigDecimal(user.getCodut());
			// String sostituisciIdentificazione =
			// getAttributeAsString("sostituisciIdentificazione");

			doc.setPrgDocumento(getAttributeAsBigDecimal("prgDocumento"));
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
			// Se sono in aggiornamento recupero il numKlo per effettuare le
			// modifiche
			doc.setNumKloDocumento(getAttributeAsBigDecimal("numKloDocumento"));

			// Vengono risettate nella JSP
			// ******************************************************************
			// doc.setNumAnnoProt(getAttributeAsBigDecimal("numAnnoProt"));
			// doc.setNumProtocollo(getAttributeAsBigDecimal("numProtocollo"));
			// doc.setDatProtocollazione(getAttributeAsString("dataOraProt"));
			// ******************************************************************

			doc.setStrNote(getAttributeAsString("strNote"));
			doc.setNumKeyLock(getAttributeAsBigDecimal("KLOCKPROT"));
			doc.setChiaveTabella(getAttributeAsString("strChiaveTabella"));
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

		} catch (SecurityException e1) {
			if (e1.getMessage().indexOf("(1105)") > 0) {
				this.getErrorHandler()
						.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.MAX_SIZE_UPLOAD));
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e1);
			} else {
				this.getErrorHandler()
						.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL));
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e1);
			}

		} catch (Exception e) {

			this.getErrorHandler()
					.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL));
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

		} // Setta i parametri noti da recuperare nella JSP
		finally {
			try {
				response.setAttribute("documento", doc);
				setParametriResponse(request, response);
			} catch (Exception e) {
				this.getErrorHandler()
						.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL));
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

			}
		}
		_logger.debug(className + ".service() FINE");

	}

	private BigDecimal getAttributeAsBigDecimal(String param) {
		String tmp = (String) jspRequest.getParameter(param);
		if ((tmp != null) && (!tmp.equals(""))) {

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
		SourceBeanUtils.setAttrIfFilled(response, "prgDocIdentPrincipale",
				getAttributeAsString("prgDocIdentPrincipale"));

		SourceBeanUtils.setAttrIfFilled(response, "FRAME_NAME", getAttributeAsString("FRAME_NAME"));
		SourceBeanUtils.setAttrIfFilled(response, "QUERY_STRING", getAttributeAsString("QUERY_STRING"));

		SourceBeanUtils.setAttrIfFilled(response, "REFRESH_PR", getAttributeAsString("REFRESH_PR"));

		SourceBeanUtils.setAttrIfFilled(response, "codAmbitoDaCombo", getAttributeAsString("codAmbito"));
	}

}