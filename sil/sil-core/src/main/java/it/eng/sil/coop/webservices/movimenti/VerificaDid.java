package it.eng.sil.coop.webservices.movimenti;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.bean.LavoratoreBean;
import it.eng.sil.coop.webservices.bean.UserBean;
import it.eng.sil.security.User;

public class VerificaDid {
	private String codCpi = null;
	private String codMonoTipoCpi = null;
	private BigDecimal cdnlavoratore = null;
	// private String codiceFiscale = null;
	private String dataDid = null;
	private String outputXML = null;
	TransactionQueryExecutor transExec = null;
	RequestContainer requestContainer = null;
	SessionContainer sessionContainer = null;
	BigDecimal userSP = new BigDecimal("150");
	SourceBean request = null;
	SourceBean response = null;
	UserBean usrSP = null;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(VerificaDid.class.getName());

	public String ritornaDID(String codiceFiscale) throws Exception {
		String xmlOut = "";
		try {
			usrSP = new UserBean(userSP, cdnlavoratore);
			Date today = null;
			String dataOdierna = it.eng.afExt.utils.DateUtils.getNow();
			LavoratoreBean lavService = new LavoratoreBean(codiceFiscale, dataOdierna);
			BigDecimal cdnlavoratore = lavService.getCdnLavoratore();
			requestContainer = new RequestContainer();
			sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", userSP);
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			sessionContainer.setAttribute(User.USERID, usrSP.getUser());
			requestContainer.setSessionContainer(sessionContainer);
			request = new SourceBean("SERVICE_REQUEST");
			response = new SourceBean("SERVICE_RESPONSE");
			request.setAttribute("FORZA_INSERIMENTO", "true");
			request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
			request.setAttribute("datDichiarazione", dataOdierna);
			request.setAttribute("cdnLavoratore", cdnlavoratore.toString());
			requestContainer.setServiceRequest(request);
			RequestContainer.setRequestContainer(requestContainer);

			outputXML = lavService.getOutputXml();
			if (outputXML != null) {
				outputXML = xmlToString(outputXML);
				return outputXML;
			}
			boolean flgCompetenza = false;

			if (lavService.getCodMonoTipoCpi() != null && lavService.getCodMonoTipoCpi().equalsIgnoreCase("C")) {
				flgCompetenza = true;
			}
			if (!flgCompetenza) {
				outputXML = "08|Lavoratore non competente";
				return outputXML;
			}
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();
			BigDecimal prgElencoAnag = lavService.getElencoAnagrafico(transExec);

			if (prgElencoAnag != null) {

				BigDecimal prgDidApertaPA = lavService.getDidApertaPA(transExec, prgElencoAnag);
				boolean checkControllo = lavService.isDidStipulabile(dataOdierna, userSP, requestContainer, request,
						response, transExec);
				outputXML = lavService.getOutputXml();

				if (outputXML != null) {
					transExec.rollBackTransaction();
					outputXML = xmlToString(outputXML);
					return outputXML;
				}

				if (prgDidApertaPA != null) {
					transExec.rollBackTransaction();
					outputXML = "15|DID già presente";
					return outputXML;

				}

				if (!checkControllo) {
					transExec.rollBackTransaction();
					outputXML = "19|DID non stipulabile";
					return outputXML;
				} else {
					transExec.commitTransaction();
					outputXML = "OK|Did Stipulabile";
					return outputXML;
				}
			}
		} catch (Exception ex) {

			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "StipulaDid:putCreaDID", ex);

			outputXML = "99|Errore generico";

		}

		return outputXML;
	}

	private final String xmlToString(String inputXML) throws Exception {
		String returnString = "";
		String codice = "";
		String descrizione = "";
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(new ByteArrayInputStream(inputXML.getBytes()));
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("codice");
			NodeList nListReg = doc.getElementsByTagName("descrizione");
			Element ecodice = (Element) nList.item(0);
			Element edesc = (Element) nListReg.item(0);
			codice = ecodice.getFirstChild().getNodeValue();
			descrizione = edesc.getFirstChild().getNodeValue();
			returnString = codice + "|" + descrizione;

		} catch (Exception e) {
			// errore generico
			returnString = "99|Errore generico";
			return returnString;
		}
		return returnString;
	}

}
