package it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;

public class CLRicercaPersonale extends AbstractSimpleModule {
	private static final long serialVersionUID = 1L;

	static Logger _logger = Logger.getLogger(CLRicercaPersonale.class.getName());

	public void service(SourceBean request, SourceBean response) {
		int idSuccess = this.disableMessageIdSuccess();

		User objUser = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		String codCPI = objUser.getCodRif();

		String prgRichiesta = request.getAttribute("PRGRICHIESTA").toString();
		String prgAlternativa = request.getAttribute("PRGALTERNATIVA").toString();
		Object codReq = request.getAttribute("CODRICHIESTA");
		String codTipoComunicazioneCl = request.getAttribute("codTipoComunicazioneCl").toString();

		String codiceRichiesta;
		if (codReq != null)
			codiceRichiesta = codReq.toString();
		else
			codiceRichiesta = "codice";

		String xmlGenerato;

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			xmlGenerato = buildRichiestaDiPersonale(prgRichiesta, prgAlternativa, codCPI, codiceRichiesta,
					codTipoComunicazioneCl);
			if (StringUtils.isFilledNoBlank(xmlGenerato)) {
				response.setAttribute("xmlGenerato", xmlGenerato);
				reportOperation.reportSuccess(idSuccess);
			}
		} catch (EMFUserError e) {
			reportOperation.reportFailure(e, "buildRichiestaDiPersonale", e.getDescription());
			_logger.error(e);
		} catch (MandatoryFieldException e) {
			reportOperation.reportFailure(e, "buildRichiestaDiPersonale", e.getExceptionMessage());
			_logger.error(e);
		} catch (FieldFormatException e) {
			reportOperation.reportFailure(e, "buildRichiestaDiPersonale", e.getExceptionMessage());
			_logger.error(e);
		} catch (SourceBeanException e) {
			reportOperation.reportFailure(e, "buildRichiestaDiPersonale", "Errore interno");
			_logger.error(e);
		}

	}

	private String buildRichiestaDiPersonale(String prgRichiesta, String prgAlternativa, String codCPI,
			String codiceOfferta, String codTipoComunicazioneCl)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {

		BigDecimal bdRichiesta;
		BigDecimal bdAlternativa;
		try {
			bdRichiesta = new BigDecimal(prgRichiesta);
			bdAlternativa = new BigDecimal(prgAlternativa);
		} catch (NumberFormatException ex) {
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_INPUT_ERRATO,
					"I valori passati prgRichiesta e prgAlternativa non sono numerici: prgRichiesta:" + prgRichiesta
							+ ", prgAlternativa:" + prgAlternativa);
		}
		CLRicercaPersonaleData risposta = new CLRicercaPersonaleData(bdRichiesta, bdAlternativa, codCPI,
				codTipoComunicazioneCl);
		risposta.costruisci(false);

		return risposta.generaXML();
	}

	private static Document parseXmlFile(String in) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(in));
		return db.parse(is);
	}

}
