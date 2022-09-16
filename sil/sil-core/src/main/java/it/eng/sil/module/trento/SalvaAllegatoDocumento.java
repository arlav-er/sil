package it.eng.sil.module.trento;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractHttpModule;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;

import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;

public class SalvaAllegatoDocumento extends AbstractHttpModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SalvaAllegatoDocumento.class.getName());

	private final String className = StringUtils.getClassName(this);

	private HttpServletRequest httpRequest = null;
	private HttpServletResponse httpResponse = null;
	private ServletConfig servletConfig = null;
	private Request jspRequest = null;
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

	public void service(SourceBean request, SourceBean response) throws Exception {

		_logger.info(className + ".service() INIZIO");

		Documento doc = null;
		TransactionQueryExecutor transExecutor = null;
		BigDecimal prgDocDaJsp = null;
		boolean doInsert = true;
		Object idAllegato = null;
		String codTipoDocumento = null;
		String descrDocumento = null;
		BigDecimal cdnComponente = null;
		String indiceAllegatoTemplate = "0";

		try {

			initialize();

			prgDocDaJsp = getAttributeAsBigDecimal("prgDocumentoAllegato");
			idAllegato = getAttributeAsString("idAllegato");
			if (idAllegato != null) {
				indiceAllegatoTemplate = idAllegato.toString();
			}
			doInsert = (prgDocDaJsp == null);

			if (doInsert) {
				doc = new Documento();
			} else {
				// questo costruttore legge il documento da db
				doc = new Documento(new BigDecimal(request.getAttribute("prgDocumentoFiglio").toString()));
			}

			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);
			BigDecimal userid = new BigDecimal(user.getCodut());
			String codCpi = user.getCodRif();
			Object prgDocPadre = request.getAttribute("prgDocumento");
			_logger.debug(className + "prgDocPadre: " + prgDocPadre);
			transExecutor = new TransactionQueryExecutor(Values.DB_SIL_DATI, this);
			transExecutor.initTransaction();
			_logger.debug(className + "indiceAllegatoTemplate: " + indiceAllegatoTemplate);
			if (!indiceAllegatoTemplate.equalsIgnoreCase("0")) {
				Object[] obj = new Object[1];
				obj[0] = idAllegato;
				SourceBean sbAllegato = (SourceBean) transExecutor.executeQuery("SELECT_TIPO_E_DESC_DOC_ALLEGATO", obj,
						"SELECT");
				sbAllegato = sbAllegato.containsAttribute("ROW") ? (SourceBean) sbAllegato.getAttribute("ROW")
						: sbAllegato;
				codTipoDocumento = (String) sbAllegato.getAttribute("codtipodocumento");
				descrDocumento = (String) sbAllegato.getAttribute("descrdocumento");
			} else {
				codTipoDocumento = getAttributeAsString("ambito");
				descrDocumento = getAttributeAsString("strDescAltroAllegato");
			}
			_logger.debug(className + "doInsert: " + doInsert);

			if (!doInsert) {
				doc.setPrgDocumento(new BigDecimal(request.getAttribute("prgDocumentoFiglio").toString()));
			} else {
				Object[] objComp = new Object[1];
				objComp[0] = "InsStampaParamPage";
				SourceBean sbComp = (SourceBean) transExecutor.executeQuery("GET_CDNCOMPONENTE", objComp, "SELECT");
				sbComp = sbComp.containsAttribute("ROW") ? (SourceBean) sbComp.getAttribute("ROW") : sbComp;
				cdnComponente = (BigDecimal) sbComp.getAttribute("CDNCOMPONENTE");
			}
			_logger.debug(className + " getProtocollazione....");
			SourceBean rowProt = (SourceBean) transExecutor.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
			if (rowProt == null) {
				throw new Exception("impossibile protocollare il documento di identificazione");
			}
			rowProt = (rowProt.containsAttribute("ROW") ? (SourceBean) rowProt.getAttribute("ROW") : rowProt);
			BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
			BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
			String datProtocollazione = (String) rowProt.getAttribute("DATAORAPROT");
			_logger.debug(className + " protocollo: " + numAnnoProt + " " + numProtocollo + " " + datProtocollazione);
			doc.setCodCpi(codCpi);
			Object cdnLavoratore = request.getAttribute("CDNLAVORATORE");
			_logger.debug(className + " cdnLavoratore: " + cdnLavoratore);
			if (cdnLavoratore != null && !cdnLavoratore.equals(""))
				doc.setCdnLavoratore(new BigDecimal(cdnLavoratore.toString()));
			else
				doc.setCdnLavoratore(null);
			_logger.debug(className + " cdnLavoratore: " + cdnLavoratore);

			doc.setCodTipoDocumento(codTipoDocumento);
			doc.setStrDescrizione(descrDocumento);
			doc.setDatAcqril(getAttributeAsString("dataAcquisizione"));
			doc.setDatInizio(getAttributeAsString("dataInizio"));
			doc.setDatFine(getAttributeAsString("dataFine"));
			doc.setPagina("InsStampaParamPage");
			doc.setNumAnnoProt(numAnnoProt);
			doc.setNumProtocollo(numProtocollo);
			doc.setDatProtocollazione(datProtocollazione);
			doc.setTipoProt("S");

			doc.setCdnComponente(cdnComponente);
			_logger.debug(className + " prgDocumento: " + request.getAttribute("prgDocumento"));
			doc.setStrChiaveTabella(request.getAttribute("prgDocumento").toString());

			doc.setCodMonoIO("I");
			doc.setCodStatoAtto(Properties.STATO_ATTO_PROTOC);
			doc.setCdnUtMod(userid);
			_logger.debug(className + " getUploadedFile....");
			getUploadedFile();
			_logger.debug(className + " file caricato");
			if (this.file != null) {
				doc.setTempFile(this.file);
				doc.setStrNomeDoc(this.filename);

			}

			if (doInsert) {
				doc.setCdnUtIns(userid);
				doc.insert(transExecutor);
				nuovoPrgDocumento = doc.getPrgDocumento();

				if (nuovoPrgDocumento != null) {
					doc.setPrgDocumento(nuovoPrgDocumento);
				}
				_logger.debug(className + " parametri:");
				// devo creare il collegamento in am_documento_allegato
				Object params[] = new Object[6];
				params[0] = prgDocPadre;
				params[1] = nuovoPrgDocumento;
				params[2] = getAttributeAsString("presaVisione");
				params[3] = getAttributeAsString("caricatoSucc");
				params[4] = userid;
				params[5] = userid;
				_logger.debug(className + "params[0]: " + params[0]);
				_logger.debug(className + "params[1]: " + params[1]);
				_logger.debug(className + "params[2]: " + params[2]);
				_logger.debug(className + "params[3]: " + params[3]);
				_logger.debug(className + "params[4]: " + params[4]);
				_logger.debug(className + "params[5]: " + params[5]);

				Boolean res = (Boolean) transExecutor.executeQuery("INSERT_DOC_ALLEGATO_STAMPA_PARAM", params,
						"INSERT");
				_logger.debug(className + "res: " + res);
				if (!res.booleanValue())
					throw new Exception("Impossibile inserire il collegamento tra documento padre e allegato");

				transExecutor.commitTransaction();

			} else {
				if (this.file != null) {
					// docFiglio.setPrgDocumento(new BigDecimal(request.getAttribute("prgDocumentoFiglio").toString()));
					doc.setNumKloDocumento(getAttributeAsBigDecimal("numKloDocumento").add(new BigDecimal(1)));
					// doc.update(transExecutor);
					doc.inserisciBlob(transExecutor);
					// doc.update(transExecutor);
					Documento docFiglio = new Documento(
							new BigDecimal(request.getAttribute("prgDocumentoFiglio").toString()));

					docFiglio.setStrNomeDoc(this.filename);
					docFiglio.aggiornaSoloNomeDoc(transExecutor);

					_logger.debug(className + " parametri:");
					// devo creare il collegamento in am_documento_allegato
					Object params[] = new Object[2];

					params[0] = getAttributeAsString("caricatoSucc");
					params[1] = prgDocDaJsp;
					_logger.debug(className + "params[0]: " + params[0]);
					_logger.debug(className + "params[1]: " + params[1]);

					Boolean res = (Boolean) transExecutor.executeQuery("UPDATE_DOC_ALLEGATO_STAMPA_PARAM", params,
							"INSERT");
					_logger.debug(className + "res: " + res);
					if (!res.booleanValue())
						throw new Exception("Impossibile inserire il collegamento tra documento padre e allegato");

					transExecutor.commitTransaction();
				}
			}

			// transExecutor.commitTransaction();

			// DOCAREA: cancello il file temporaneo inviato e protocollato
			ProtocolloDocumentoUtil.cancellaFileDocarea(doc);
			response.setAttribute("OPERAZIONI_OK", "true");
			MessageAppender.appendMessage(response, MessageCodes.General.OPERATION_SUCCESS);
		} catch (Exception e) {
			if (transExecutor != null) {
				transExecutor.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);
		}
		_logger.info(className + ".service() FINE");
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

}