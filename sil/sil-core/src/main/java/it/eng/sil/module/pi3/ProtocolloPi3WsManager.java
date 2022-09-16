package it.eng.sil.module.pi3;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.activation.MimetypesFileTypeMap;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Stub;
import org.datacontract.schemas._2004._07.VtDocsWS_Services.Request;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_AddressBook_AddCorrespondent.AddCorrespondentRequest;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_AddressBook_AddCorrespondent.AddCorrespondentResponse;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_AddressBook_GetCorrespondent.GetCorrespondentRequest;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_AddressBook_GetCorrespondent.GetCorrespondentResponse;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_ClassificationScheme_GetActiveClassificationScheme.GetActiveClassificationSchemeRequest;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_ClassificationScheme_GetActiveClassificationScheme.GetActiveClassificationSchemeResponse;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Documents_CreateDocumentAndAddInProject.CreateDocumentAndAddInProjectRequest;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Documents_CreateDocumentAndAddInProject.CreateDocumentAndAddInProjectResponse;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Documents_GetDocument.GetDocumentRequest;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Documents_GetDocument.GetDocumentResponse;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Documents_GetTemplateDoc.GetTemplateDocRequest;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Documents_GetTemplateDoc.GetTemplateDocResponse;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Documents_UploadFileToDocument.UploadFileToDocumentRequest;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Documents_UploadFileToDocument.UploadFileToDocumentResponse;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Projects_GetProject.GetProjectRequest;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Projects_GetProject.GetProjectResponse;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Token_GetAuthenticationToken.GetAuthenticationTokenRequest;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Token_GetAuthenticationToken.GetAuthenticationTokenResponse;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Transmissions_ExecuteTransmissionDocument.ExecuteTransmissionDocumentRequest;
import org.datacontract.schemas._2004._07.VtDocsWS_Services_Transmissions_ExecuteTransmissionDocument.ExecuteTransmissionDocumentResponse;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;
import com.nttdata._2012.Pi3.BasicHttpBinding_IAddressBookStub;
import com.nttdata._2012.Pi3.BasicHttpBinding_IClassificationSchemesStub;
import com.nttdata._2012.Pi3.BasicHttpBinding_IDocumentsStub;
import com.nttdata._2012.Pi3.BasicHttpBinding_IProjectsStub;
import com.nttdata._2012.Pi3.BasicHttpBinding_ITokenStub;
import com.nttdata._2012.Pi3.BasicHttpBinding_ITransmissionsStub;
import com.nttdata._2012.Pi3.ClassificationScheme;
import com.nttdata._2012.Pi3.Correspondent;
import com.nttdata._2012.Pi3.Document;
import com.nttdata._2012.Pi3.Project;
import com.nttdata._2012.Pi3.Template;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.bean.Documento;
import it.eng.sil.coop.webservices.myportal.servizicittadino.utils.Credentials;

public class ProtocolloPi3WsManager {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProtocolloPi3WsManager.class.getName());
	private final String className = StringUtils.getClassName(this);

	private String pi3WsEndPoint = "";
	private String codeAmn = "";
	private String userName = "";
	private String codeApplication = "";

	private String codeRoleLogin = "";
	private String codeRF = "";
	private String codeRegisterOrRf = "";

	private String codeRegister = "";

	private String descptionTemplate = "";

	private String authenticationToken;

	// private IAddressBookProxy proxyAddressBook = new IAddressBookProxy();
	// private IDocumentsProxy proxyDocuments = new IDocumentsProxy();
	// private IClassificationSchemesProxy proxyClassificationSchemes = new IClassificationSchemesProxy();
	// private IProjectsProxy proxyProjects = new IProjectsProxy();

	private BasicHttpBinding_IAddressBookStub proxyAddressBook = new BasicHttpBinding_IAddressBookStub();
	private BasicHttpBinding_IDocumentsStub proxyDocuments = new BasicHttpBinding_IDocumentsStub();
	private BasicHttpBinding_IClassificationSchemesStub proxyClassificationSchemes = new BasicHttpBinding_IClassificationSchemesStub();
	private BasicHttpBinding_IProjectsStub proxyProjects = new BasicHttpBinding_IProjectsStub();
	private BasicHttpBinding_ITokenStub proxyToken = new BasicHttpBinding_ITokenStub();
	private BasicHttpBinding_ITransmissionsStub proxyTransmission = new BasicHttpBinding_ITransmissionsStub();

	public ProtocolloPi3WsManager() throws Exception {
		super();

		// Get Endpoint URL from DB
		pi3WsEndPoint = getWSUrl(Pi3Constants.PI3_DOCUMENT_WS_URL_SERVICE_NAME);

		// Get Input Attributes for Pi3 WS from DB
		codeAmn = getPi3InputAttribute(Pi3Constants.PI3_DOCUMENT_WS_INPUT_CODE_AMN);
		userName = getPi3InputAttribute(Pi3Constants.PI3_DOCUMENT_WS_INPUT_USERNAME);
		codeApplication = getPi3InputAttribute(Pi3Constants.PI3_DOCUMENT_WS_INPUT_CODE_APPLICATION);
		codeRoleLogin = getPi3InputAttribute(Pi3Constants.PI3_DOCUMENT_WS_INPUT_CODE_ROLE_LOGIN);
		codeRF = getPi3InputAttribute(Pi3Constants.PI3_DOCUMENT_WS_INPUT_CODE_RF);
		codeRegister = getPi3InputAttribute(Pi3Constants.PI3_DOCUMENT_WS_INPUT_CODE_REGISTER);
		codeRegisterOrRf = getPi3InputAttribute(Pi3Constants.PI3_DOCUMENT_WS_INPUT_CODE_RF);

		setDescptionTemplate(getPi3InputAttribute(Pi3Constants.PI3_DOCUMENT_WS_INPUT_REPERTORIO));
		// Setting endpoint for WS calls
		// proxyAddressBook.setEndpoint(pi3WsEndPoint);
		// proxyDocuments.setEndpoint(pi3WsEndPoint);
		proxyAddressBook._setProperty(proxyAddressBook.ENDPOINT_ADDRESS_PROPERTY, pi3WsEndPoint);
		proxyDocuments._setProperty(proxyDocuments.ENDPOINT_ADDRESS_PROPERTY, pi3WsEndPoint);
		proxyClassificationSchemes._setProperty(proxyClassificationSchemes.ENDPOINT_ADDRESS_PROPERTY, pi3WsEndPoint);
		proxyProjects._setProperty(proxyProjects.ENDPOINT_ADDRESS_PROPERTY, pi3WsEndPoint);
		proxyToken._setProperty(proxyToken.ENDPOINT_ADDRESS_PROPERTY, pi3WsEndPoint);
		proxyTransmission._setProperty(proxyToken.ENDPOINT_ADDRESS_PROPERTY, pi3WsEndPoint);

		// Get AuthentsicationToken from Pi3 WS
		setAuthenticationToken(getServiceAuthenticationToken());

		// Print Debug Input Attributes
		printDebugMainAttributes();
	}

	private String getServiceAuthenticationToken() throws Exception {

		GetAuthenticationTokenRequest request = new GetAuthenticationTokenRequest();
		request.setCodeAdm(codeAmn);
		request.setUserName(userName);

		GetAuthenticationTokenResponse response = proxyToken.getAuthenticationToken(request);

		printSOAPEnvelopeXmlRequestResponse(proxyToken, "getServiceAuthenticationToken");
		_logger.debug("[ProtocolloPi3WsManager] -> getServiceAuthenticationToken -> [OUTPUT: Authentication Token]: "
				+ response.getAuthenticationToken());

		return response.getAuthenticationToken();
	}

	public Correspondent getCorrespondent(String senderId) throws Exception {

		GetCorrespondentRequest request = new GetCorrespondentRequest();
		request = (GetCorrespondentRequest) setRequestData(request);
		request.setIdCorrespondent(senderId);
		_logger.debug("[ProtocolloPi3WsManager] -> getCorrespondent -> [INPUT: senderId]: " + senderId);

		GetCorrespondentResponse response = proxyAddressBook.getCorrespondent(request);

		printSOAPEnvelopeXmlRequestResponse(proxyAddressBook, "getCorrespondent");
		_logger.debug("[ProtocolloPi3WsManager] -> getCorrespondent -> [OUTPUT: name]: "
				+ response.getCorrespondent().getName());
		_logger.debug("[ProtocolloPi3WsManager] -> getCorrespondent -> [OUTPUT: surname]: "
				+ response.getCorrespondent().getSurname());
		_logger.debug("[ProtocolloPi3WsManager] -> getCorrespondent -> [OUTPUT: code]: "
				+ response.getCorrespondent().getCode());
		_logger.debug("[ProtocolloPi3WsManager] -> getCorrespondent -> [OUTPUT: description]: "
				+ response.getCorrespondent().getDescription());

		return response.getCorrespondent();
	}

	public Correspondent addCorrespondent(UtentePi3Bean bean) throws Exception {

		AddCorrespondentRequest request = new AddCorrespondentRequest();
		request = (AddCorrespondentRequest) setRequestData(request);

		// Print INPUT BEAN ATTRIBUTES
		try {
			if (bean != null) {
				_logger.debug("[ProtocolloPi3WsManager] -> addCorrespondent -> [INPUT: UtentePi3Bean nome]: "
						+ bean.getNome());
				_logger.debug("[ProtocolloPi3WsManager] -> addCorrespondent -> [INPUT: UtentePi3Bean cognome]: "
						+ bean.getCognome());
				_logger.debug("[ProtocolloPi3WsManager] -> addCorrespondent -> [INPUT: UtentePi3Bean code]: "
						+ bean.getCodeCorrespondentINFTRENT());
				_logger.debug("[ProtocolloPi3WsManager] -> addCorrespondent -> [INPUT: UtentePi3Bean description]: "
						+ bean.getDescriptionCorrespondentINFTRENT());
				_logger.debug(
						"[ProtocolloPi3WsManager] -> addCorrespondent -> [INPUT: UtentePi3Bean correspondent type]: "
								+ bean.getCorrespondentTypeINFTRENT());
			} else {
				throw new Exception(
						"{Exception/Error} -> [ProtocolloPi3WsManager] -> addCorrespondent -> UtentePi3Bean is null");
			}
		} catch (Exception ex) {
			throw new Exception("{Exception/Error} -> [ProtocolloPi3WsManager] -> addCorrespondent -> [message]: "
					+ ex.getMessage());
		}

		Correspondent correspondent = new Correspondent();
		correspondent.setName(bean.getNome());
		correspondent.setSurname(bean.getCognome());
		correspondent.setCode("SPIL_" + bean.getCodeCorrespondentINFTRENT()); //
		correspondent.setDescription(bean.getDescriptionCorrespondentINFTRENT());
		correspondent.setCorrespondentType(bean.getCorrespondentTypeINFTRENT()); // Nome + Cognome
		correspondent.setNationalIdentificationNumber(bean.getCodiceFiscale());
		correspondent.setCodeRegisterOrRF(codeRF);

		correspondent.setNote("inserito da SPIL");

		request.setCorrespondent(correspondent);

		AddCorrespondentResponse response = proxyAddressBook.addCorrespondent(request);

		printSOAPEnvelopeXmlRequestResponse(proxyAddressBook, "addCorrespondent");
		_logger.debug(
				"[ProtocolloPi3WsManager] -> addCorrespondent -> [OUTPUT: id]: " + response.getCorrespondent().getId());
		_logger.debug("[ProtocolloPi3WsManager] -> addCorrespondent -> [OUTPUT: name]: "
				+ response.getCorrespondent().getName());
		_logger.debug("[ProtocolloPi3WsManager] -> addCorrespondent -> [OUTPUT: surname]: "
				+ response.getCorrespondent().getSurname());
		_logger.debug("[ProtocolloPi3WsManager] -> addCorrespondent -> [OUTPUT: code]: "
				+ response.getCorrespondent().getCode());
		_logger.debug("[ProtocolloPi3WsManager] -> addCorrespondent -> [OUTPUT: description]: "
				+ response.getCorrespondent().getDescription());

		if (response != null) {
			_logger.debug("[ProtocolloPi3WsManager] -> addCorrespondent -> [OUTPUT: result]: true");

			return response.getCorrespondent();
		} else {
			return null;
		}

	}

	public Document createDocumentAndAddInProject(CreaDocumentPi3Bean creaDocumentBean) throws Exception {

		Document creaDocumentInProj = null;

		CreateDocumentAndAddInProjectRequest request = new CreateDocumentAndAddInProjectRequest();
		request = (CreateDocumentAndAddInProjectRequest) setRequestData(request);
		request.setCodeRF(codeRF);

		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: nrPraticaSPIL]: "
				+ creaDocumentBean.getNrPraticaSPIL());
		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: prgMainDocument]: "
				+ creaDocumentBean.getDocumentSil().getPrgDocumento());

		_logger.debug(
				"[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: listaDocumentiAllegatiSIL nr. size]: "
						+ creaDocumentBean.getLstDocumentiAllegati().size());
		int i = 0;
		for (Documento allegato : creaDocumentBean.getLstDocumentiAllegati()) {
			_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: prgDocument allegato("
					+ i + ")]: " + allegato.getPrgDocumento());
			i++;
		}

		if (creaDocumentBean.getUtenteMittente() != null) {
			_logger.debug(
					"[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: utenteMittente typeUtente]: "
							+ creaDocumentBean.getUtenteMittente().getTypeUtente());
			_logger.debug(
					"[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: utenteMittente idUtente]: "
							+ creaDocumentBean.getUtenteMittente().getIdUtenteSPIL());
			_logger.debug(
					"[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: utenteMittente name surname]: "
							+ creaDocumentBean.getUtenteMittente().getNome() + " "
							+ creaDocumentBean.getUtenteMittente().getCognome());

		}

		if (creaDocumentBean.getDocumentPi3().getSender() != null) {
			_logger.debug(
					"[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: sender Correspondent Id]: "
							+ creaDocumentBean.getDocumentPi3().getSender().getId());
			_logger.debug(
					"[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: sender Correspondent name surname]: "
							+ creaDocumentBean.getDocumentPi3().getSender().getName() + " "
							+ creaDocumentBean.getDocumentPi3().getSender().getSurname());
		}

		int j = 0;
		for (Correspondent destinatario : creaDocumentBean.getDocumentPi3().getRecipients()) {
			_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: utenteDestinatario ("
					+ j + ") idUtente]: " + destinatario.getId());
			_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: utenteDestinatario ("
					+ j + ") name surname]: " + destinatario.getName() + " " + destinatario.getSurname());
			j++;
		}

		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: documentPi3Type]: "
				+ creaDocumentBean.getDocumentPi3().getDocumentType());
		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: documentPi3 Oggetto]: "
				+ creaDocumentBean.getDocumentPi3().getObject());
		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: documentSIL file path]: "
				+ creaDocumentBean.getDocumentSil().getTempFile().getAbsolutePath());
		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: code register]: "
				+ creaDocumentBean.getCodeRegister());
		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: id project]: "
				+ creaDocumentBean.getProject().getId());
		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: code project]: "
				+ creaDocumentBean.getProject().getCode());
		_logger.debug(
				"[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: id ClassificationScheme - Titolario]: "
						+ creaDocumentBean.getClassificationScheme().getId());
		_logger.debug(
				"[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [INPUT: description ClassificationScheme - Titolario]: "
						+ creaDocumentBean.getClassificationScheme().getDescription());

		// Create Document
		// Document document = creaDocumentBean.getDocumentPi3();
		Document document = new Document();
		document.setSender(creaDocumentBean.getDocumentPi3().getSender());
		document.setRecipients(creaDocumentBean.getDocumentPi3().getRecipients());

		// Imposta il Main Document o l'Attributo PREDISPOSED a seconda del Tipo di Documento ('In Entrata, 'In Uscita',
		// 'Repertoriato')
		if (creaDocumentBean.isDocInEntrata()) {
			if (creaDocumentBean.isDocumentoFirmabile() && creaDocumentBean.isDocumentoFirmato()
					&& creaDocumentBean.isConsensoAttivo()) {
				if (creaDocumentBean.getDocumentSil().getTempFile().length() != 0) {
					document.setMainDocument(getPi3Document(creaDocumentBean.getDocumentSil().getTempFile()));
				} else {
					document.setPredisposed(true);
				}
			} else {
				document.setPredisposed(true);
			}
		} else if (creaDocumentBean.isDocInUscita()) {
			if (creaDocumentBean.getDocumentSil().getTempFile().length() != 0) {
				document.setMainDocument(getPi3Document(creaDocumentBean.getDocumentSil().getTempFile()));
			} else {
				document.setPredisposed(true);
			}
		} else if (creaDocumentBean.isDocRepertoriato()) {
			if (creaDocumentBean.isDocumentoFirmabile() && creaDocumentBean.isDocumentoFirmato()
					&& creaDocumentBean.isConsensoAttivo()) {
				if (creaDocumentBean.getDocumentSil().getTempFile().length() != 0) {
					document.setMainDocument(getPi3Document(creaDocumentBean.getDocumentSil().getTempFile()));
				}
			}
		} else {
			// TODO: ALTRI DOCUMENTI?
			if (creaDocumentBean.getDocumentSil().getTempFile().length() != 0) {
				document.setMainDocument(getPi3Document(creaDocumentBean.getDocumentSil().getTempFile()));
			} else {
				document.setPredisposed(true);
			}
		}

		/*
		 * //se il documento da configurazione prevede la firma grafometrica (su AM_CONFIG_PROTOCOLLO) , //consenso è
		 * ATTIVO ( su AM_CONSENSO_FIRMA) e FLGFIRMATO=S su AM_DOCUMENTO_BLOB //Si deve inviare anche il Main Document (
		 * come attributo del CreateDocumentsAndAddInProject)
		 * if(creaDocumentBean.isDocumentoFirmabile()&&creaDocumentBean.isDocumentoFirmato()&&creaDocumentBean.
		 * isConsensoAttivo()){
		 * document.setMainDocument(getPi3Document(creaDocumentBean.getDocumentSil().getTempFile())); } else{
		 * //if(!creaDocumentBean.getDocumentPi3().getDocumentType().equals(Pi3Constants.PI3_DOCUMENT_TYPE_REPERTORIO))
		 * if(creaDocumentBean.getDocumentPi3().getDocumentType().equals(Pi3Constants.PI3_DOCUMENT_TYPE_ENTRATA))
		 * document.setPredisposed(true);
		 * 
		 * }
		 */

		// Create Document sNote
		/*
		 * Note[] note = new Note[1]; note[0] = new Note(); note[0].setDescription(creaDocumentBean.getNrPraticaSPIL());
		 * document.setNote(note);
		 */
		document.setNote(creaDocumentBean.getDocumentPi3().getNote());

		document.setObject(creaDocumentBean.getDocumentPi3().getObject());

		document.setCreationDate(creaDocumentBean.getDocumentSil().getDatAcqril());
		document.setDocumentType(creaDocumentBean.getDocumentPi3().getDocumentType());

		if (creaDocumentBean.getDocumentPi3().getTemplate() != null) {
			document.setTemplate(creaDocumentBean.getDocumentPi3().getTemplate());
		}
		_logger.debug("document: " + document);
		request.setDocument(document);

		request.setCodeRegister(codeRegister); // obbligatorio per documenti protocollati
		request.setCodeProject(creaDocumentBean.getProject().getCode());
		// request.setIdProject(creaDocumentBean.getProject().getId());
		request.setClassificationSchemeId(creaDocumentBean.getClassificationScheme().getId());

		CreateDocumentAndAddInProjectResponse response = proxyDocuments.createDocumentAndAddInProject(request);

		printSOAPEnvelopeXmlRequestResponse(proxyDocuments, "createDocumentAndAddInProject");
		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [OUTPUT: id]: "
				+ response.getDocument().getId());
		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [OUTPUT: oggetto]: "
				+ response.getDocument().getObject());
		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [OUTPUT: docNumber]: "
				+ response.getDocument().getDocNumber());
		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [OUTPUT: oggetto]: "
				+ response.getDocument().getObject());
		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [OUTPUT: signature]: "
				+ response.getDocument().getSignature());
		_logger.debug("[ProtocolloPi3WsManager] -> createDocumentAndAddInProject -> [OUTPUT: document name]: "
				+ response.getDocument().getMainDocument().getName());

		creaDocumentInProj = response.getDocument();

		return creaDocumentInProj;

	}

	public boolean addAttachesFileInProject(String idMainDocument, Documento documentoSil, String description)
			throws Exception {

		boolean isAttachedFile = false;

		UploadFileToDocumentRequest request = new UploadFileToDocumentRequest();
		request = (UploadFileToDocumentRequest) setRequestData(request);

		_logger.debug(
				"[ProtocolloPi3WsManager] -> addAttachesFileInProject -> [INPUT: idMainDocument]: " + idMainDocument);
		_logger.debug("[ProtocolloPi3WsManager] -> addAttachesFileInProject -> [INPUT: fileAllegato]: "
				+ documentoSil.getTempFile().getAbsolutePath());

		request.setIdDocument(idMainDocument);
		request.setDescription(description);
		request.setCreateAttachment(true);
		// request.setCovertToPDFA(false);

		// E = ESTERNO; U = UTENTE (DA USARE SE CREATE_ATTACHMENT = TRUE)
		if (!StringUtils.isEmpty(documentoSil.getStrNote())) {

			if (documentoSil.getStrNote()
					.equalsIgnoreCase(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_RICEVUTA_FIRMA_GRAFOMETRICA)) {
				request.setAttachmentType("U");
			} else if (documentoSil.getStrNote().equalsIgnoreCase(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_XML)) {
				request.setAttachmentType("E");
			}

		}

		if (!StringUtils.isEmpty(documentoSil.getCodTipoDocumento())) {
			if (documentoSil.getCodTipoDocumento().equalsIgnoreCase(
					Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_RICEVUTA_FIRMA_GRAFOMETRICA_CODICE_TIPO)) {
				request.setAttachmentType("U");
			}
		}

		// Se l'Allegato e' di TIPO FILE XML o RICEVUTA PER FIRMA GRAFOMETRICA...
		// se il file allegato è di tipo ricevuta grafometrica setAttachmentType = E
		/*
		 * if(documentoSil.getCodTipoDocumento()!=null&&!StringUtils.isEmpty(documentoSil.getCodTipoDocumento())&&
		 * documentoSil.getCodTipoDocumento().equalsIgnoreCase(Pi3Constants.
		 * PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_RICEVUTA_FIRMA_GRAFOMETRICA_CODICE_TIPO)){ request.setAttachmentType("E");
		 * } //se il file allegato è di tipo XML setAttachmentType = U
		 * if(!StringUtils.isEmpty(documentoSil.getStrNote())){ if(
		 * documentoSil.getStrNote().equalsIgnoreCase(Pi3Constants.PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_XML) ||
		 * documentoSil.getStrNote().equalsIgnoreCase(Pi3Constants.
		 * PI3_PROTOCOLLO_DOCUMENTO_ALLEGATO_RICEVUTA_FIRMA_GRAFOMETRICA) ){
		 * 
		 * request.setAttachmentType("E"); // E = ESTERNO; U = UTENTE (DA USARE SE CREATE_ATTACHMENT = TRUE) } }
		 */

		request.setFile(getPi3Document(documentoSil.getTempFile()));

		UploadFileToDocumentResponse response = proxyDocuments.uploadFileToDocument(request);

		isAttachedFile = true;

		printSOAPEnvelopeXmlRequestResponse(proxyDocuments, "addAttachesFileInProject");
		_logger.debug("[ProtocolloPi3WsManager] -> addAttachesFileInProject -> [OUTPUT]: " + isAttachedFile);

		return isAttachedFile;

	}

	public Document getDocumentPi3(String idDocument, boolean getFile) throws Exception {

		Document docPi3 = null;

		GetDocumentRequest request = new GetDocumentRequest();
		request = (GetDocumentRequest) setRequestData(request);
		request.setIdDocument(idDocument);
		request.setGetFile(getFile);

		_logger.debug("[ProtocolloPi3WsManager] -> getDocumentPi3 -> [INPUT: idDocument]: " + idDocument);
		_logger.debug("[ProtocolloPi3WsManager] -> getDocumentPi3 -> [INPUT: getFile]: " + getFile);

		GetDocumentResponse response = proxyDocuments.getDocument(request);

		printSOAPEnvelopeXmlRequestResponse(proxyDocuments, "getDocumentPi3");
		_logger.debug("[ProtocolloPi3WsManager] -> getDocumentPi3 -> [OUTPUT: id]: " + response.getDocument().getId());
		_logger.debug("[ProtocolloPi3WsManager] -> getDocumentPi3 -> [OUTPUT: oggetto]: "
				+ response.getDocument().getObject());
		_logger.debug("[ProtocolloPi3WsManager] -> getDocumentPi3 -> [OUTPUT: docNumber]: "
				+ response.getDocument().getDocNumber());
		_logger.debug("[ProtocolloPi3WsManager] -> getDocumentPi3 -> [OUTPUT: oggetto]: "
				+ response.getDocument().getObject());
		_logger.debug("[ProtocolloPi3WsManager] -> getDocumentPi3 -> [OUTPUT: signature]: "
				+ response.getDocument().getSignature());
		_logger.debug("[ProtocolloPi3WsManager] -> getDocumentPi3 -> [OUTPUT: main document file name]: "
				+ response.getDocument().getMainDocument().getName());
		_logger.debug("[ProtocolloPi3WsManager] -> getDocumentPi3 -> [OUTPUT: main document file length]: "
				+ response.getDocument().getMainDocument().getContent().length);
		_logger.debug("[ProtocolloPi3WsManager] -> getDocumentPi3 -> [OUTPUT: document attachment files]: "
				+ response.getDocument().getAttachments().length);

		return docPi3;
	}

	public Project getProject(String codeProject, String ClassificationSchemeId) throws Exception {

		GetProjectRequest request = new GetProjectRequest();
		request = (GetProjectRequest) setRequestData(request);

		// questo...
		// request.setIdProject(idProject);

		// ... oppure questo
		request.setClassificationSchemeId(ClassificationSchemeId);
		request.setCodeProject(codeProject);

		GetProjectResponse response = proxyProjects.getProject(request);

		printSOAPEnvelopeXmlRequestResponse(proxyProjects, "getProject");
		_logger.debug("[ProtocolloPi3WsManager] -> getProject -> [OUTPUT: id]: " + response.getProject().getId());
		_logger.debug("[ProtocolloPi3WsManager] -> getProject -> [OUTPUT: code]: " + response.getProject().getCode());
		_logger.debug("[ProtocolloPi3WsManager] -> getProject -> [OUTPUT: ClassificationSchemeId]: "
				+ response.getProject().getClassificationScheme().getId());

		return response.getProject();

	}

	public ClassificationScheme getActiveClassificationScheme() throws Exception {

		GetActiveClassificationSchemeRequest request = new GetActiveClassificationSchemeRequest();
		request = (GetActiveClassificationSchemeRequest) setRequestData(request);
		request.setCodeAdm(codeAmn);
		request.setUserName(userName);
		// request.setCodeApplication(codeApplication);

		GetActiveClassificationSchemeResponse response = proxyClassificationSchemes
				.getActiveClassificationScheme(request);

		printSOAPEnvelopeXmlRequestResponse(proxyClassificationSchemes, "getClassificationScheme");
		_logger.debug("[ProtocolloPi3WsManager] -> getClassificationScheme -> [OUTPUT: id]: "
				+ response.getClassificationScheme().getId());
		_logger.debug("[ProtocolloPi3WsManager] -> getClassificationScheme -> [OUTPUT: description]: "
				+ response.getClassificationScheme().getDescription());
		_logger.debug("[ProtocolloPi3WsManager] -> getClassificationScheme -> [OUTPUT: active]: "
				+ response.getClassificationScheme().getActive());

		return response.getClassificationScheme();

	}

	public Template getTemplateDoc() throws Exception {

		GetTemplateDocRequest request = new GetTemplateDocRequest();
		request = (GetTemplateDocRequest) setRequestData(request);

		_logger.debug("[ProtocolloPi3WsManager] -> getTemplateDoc -> [INPUT: repertorio]: " + descptionTemplate);

		request.setDescriptionTemplate(descptionTemplate);

		GetTemplateDocResponse response = proxyDocuments.getTemplateDoc(request);

		printSOAPEnvelopeXmlRequestResponse(proxyDocuments, "getTemplateDoc");
		_logger.debug("[ProtocolloPi3WsManager] -> getTemplateDoc -> [OUTPUT: id]: " + response.getTemplate().getId());
		_logger.debug(
				"[ProtocolloPi3WsManager] -> getTemplateDoc -> [OUTPUT: name]: " + response.getTemplate().getName());
		_logger.debug(
				"[ProtocolloPi3WsManager] -> getTemplateDoc -> [OUTPUT: type]: " + response.getTemplate().getType());

		return response.getTemplate();
	}

	public boolean executeTransmissionDocument(String idPredisposedDocument) throws Exception {

		boolean isExecutedTransmissionDocument = false;

		_logger.debug("[ProtocolloPi3WsManager] -> executeTransmissionDocument -> [INPUT: idPredisposedDocument]: "
				+ idPredisposedDocument);

		ExecuteTransmissionDocumentRequest request = new ExecuteTransmissionDocumentRequest();
		request = (ExecuteTransmissionDocumentRequest) setRequestData(request);

		request.setIdDocument(idPredisposedDocument);
		request.setTransmissionType(Pi3Constants.PI3_DOCUMENT_WS_INPUT_TYPE_S_TRASMISSIONE);
		request.setNotify(true);
		request.setTransmissionReason(Pi3Constants.PI3_DOCUMENT_WS_INPUT_CODE_RAGIONE_TRASMISSIONE); // Codice della
																										// Ragione di
																										// Trasmissione
		Correspondent receiver = new Correspondent();
		receiver.setCode(codeRoleLogin);
		request.setReceiver(receiver);

		ExecuteTransmissionDocumentResponse response = proxyTransmission.executeTransmissionDocument(request);

		isExecutedTransmissionDocument = true;

		printSOAPEnvelopeXmlRequestResponse(proxyTransmission, "executeTransmissionDocument");
		_logger.debug("[ProtocolloPi3WsManager] -> executeTransmissionDocument -> [OUTPUT]: "
				+ isExecutedTransmissionDocument);

		return isExecutedTransmissionDocument;

	}

	private Request setRequestData(Request request) throws Exception {

		request.setAuthenticationToken(getAuthenticationToken());
		request.setCodeAdm(codeAmn);
		request.setUserName(userName);
		request.setCodeApplication(codeApplication);

		request.setCodeRoleLogin(codeRoleLogin); // opzionale

		return request;

	}

	private com.nttdata._2012.Pi3.File getPi3Document(File silFile) throws IOException {

		byte fileContent[] = new byte[(int) silFile.length()];
		FileInputStream fin = new FileInputStream(silFile);
		int read = 0;
		ByteArrayOutputStream ous = new ByteArrayOutputStream();
		while ((read = fin.read(fileContent)) != -1) {
			ous.write(fileContent, 0, read);
		}
		com.nttdata._2012.Pi3.File mainDocument = new com.nttdata._2012.Pi3.File();
		mainDocument.setContent(fileContent);
		mainDocument.setName(silFile.getName());
		mainDocument.setMimeType(new MimetypesFileTypeMap().getContentType(silFile));

		_logger.debug("[ProtocolloPi3WsManager] -> getPi3Document -> [INPUT: fileName]: " + silFile.getName());

		return mainDocument;
	}

	private String getWSUrl(String urlServiceName) throws Exception {

		String url = "";

		Object[] inputParameters = new Object[1]; // END_POINT_NAME
		inputParameters[0] = urlServiceName;

		_logger.debug("[ProtocolloPi3WsManager] -> getWSUrl -> [INPUT: urlServiceName]: " + urlServiceName);

		SourceBean ret = (SourceBean) QueryExecutor.executeQuery("SELECT_TS_ENDPOINT", inputParameters, "SELECT",
				Values.DB_SIL_DATI);

		url = (String) ret.getAttribute("ROW.STRURL");

		_logger.debug("[ProtocolloPi3WsManager] -> getWSUrl -> [OUTPUT: urlWs]: " + url);

		return url;

	}

	private String getPi3InputAttribute(String inputAttributeName) throws Exception {

		String inputAttributeValue = "";

		UtilsConfig utilsConfig = new UtilsConfig(inputAttributeName);
		inputAttributeValue = utilsConfig.getValoreConfigurazione();

		return inputAttributeValue;
	}

	private Credentials getwsAuthUtils(String wsName) {

		Credentials creden = null;

		Object[] inputParameters = new Object[1]; // END_POINT_NAME
		inputParameters[0] = wsName;
		SourceBean ret = (SourceBean) QueryExecutor.executeQuery("GET_WS_CREDENTIALS", inputParameters, "SELECT",
				Values.DB_SIL_DATI);

		String username = (String) ret.getAttribute("ROW.struserid");
		String password = (String) ret.getAttribute("ROW.CLN_PWD");

		creden = new Credentials(username, password);

		return creden;

	}

	private void printDebugMainAttributes() {

		if (_logger.isDebugEnabled()) {
			_logger.debug(
					"[ProtocolloPi3WsManager] -> ProtocolloPi3WsManager() -> printDebugMainAttributes -> [OUTPUT: pi3WsEndPoint]: "
							+ pi3WsEndPoint);
			_logger.debug(
					"[ProtocolloPi3WsManager] -> ProtocolloPi3WsManager() -> printDebugMainAttributes -> [OUTPUT: codeAmn]: "
							+ codeAmn);
			_logger.debug(
					"[ProtocolloPi3WsManager] -> ProtocolloPi3WsManager() -> printDebugMainAttributes -> [OUTPUT: userName]: "
							+ userName);
			_logger.debug(
					"[ProtocolloPi3WsManager] -> ProtocolloPi3WsManager() -> printDebugMainAttributes -> [OUTPUT: codeApplication]: "
							+ codeApplication);
			_logger.debug(
					"[ProtocolloPi3WsManager] -> ProtocolloPi3WsManager() -> printDebugMainAttributes -> [OUTPUT: codeRoleLogin]: "
							+ codeRoleLogin);
			_logger.debug(
					"[ProtocolloPi3WsManager] -> ProtocolloPi3WsManager() -> printDebugMainAttributes -> [OUTPUT: codeRF]: "
							+ codeRF);
			_logger.debug(
					"[ProtocolloPi3WsManager] -> ProtocolloPi3WsManager() -> printDebugMainAttributes -> [OUTPUT: codeRegister]: "
							+ codeRegister);
		}
	}

	private void printSOAPEnvelopeXmlRequestResponse(Stub stub, String wServiceDescription) throws AxisFault {

		if (_logger.isDebugEnabled()) {
			String requestXML = stub._getCall().getMessageContext().getRequestMessage().getSOAPPartAsString();
			String responseXML = stub._getCall().getMessageContext().getResponseMessage().getSOAPPartAsString();

			_logger.debug("[ProtocolloPi3WsManager] -> " + wServiceDescription + " -> [SOAP ENVELOPE: requestXML]: "
					+ requestXML);
			_logger.debug("[ProtocolloPi3WsManager] -> " + wServiceDescription + " -> [SOAP ENVELOPE: responseXML]: "
					+ responseXML);
		}

	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	private void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	public String getDescptionTemplate() {
		return descptionTemplate;
	}

	public void setDescptionTemplate(String descptionTemplate) {
		this.descptionTemplate = descptionTemplate;
	}

}
