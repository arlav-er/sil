package it.eng.sil.coop.webservices.myportal;

import java.io.StringReader;
import java.sql.Connection;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.myportal.vacancy.risposta.InserisciVacancy;
import it.eng.sil.module.AbstractSimpleModule;

public class InviaVacancyPub extends AbstractSimpleModule {

	private static final long serialVersionUID = 13476689L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InviaVacancyPub.class.getName());

	private static final String ESITO_OK = "0";

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		DataConnectionManager dcm = null;
		DataConnection dc = null;
		Connection con = null;
		String codiceEsito = "";
		String descrizioneEsito = "";
		String esito = null;

		String prgRichiesta = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();

		try {
			dcm = DataConnectionManager.getInstance();
			dc = dcm.getConnection(Values.DB_SIL_DATI);
			con = dc.getInternalConnection();
			InviaVacancy iv = new InviaVacancy();
			esito = iv.inviaVacancySil(prgRichiesta, con);
			if (esito == null) {
				Vector<String> paramV = new Vector<String>();
				paramV.add("Errore generico");
				reportOperation.reportFailure(MessageCodes.MyPortal.ERROR_VACANCY, "WS RicezioneVacancy", "", paramV);
			} else {
				InserisciVacancy risposta = iv.convertRispostaXMLToObjectNew(esito);
				codiceEsito = risposta.getEsito();
				descrizioneEsito = risposta.getDettaglio();

				if (codiceEsito.equalsIgnoreCase(ESITO_OK)) {
					reportOperation.reportSuccess(MessageCodes.MyPortal.SUCCESS_VACANCY);
					doUpdate(serviceRequest, serviceResponse);
				} else {
					Vector<String> paramV = new Vector<String>();
					paramV.add(descrizioneEsito);
					reportOperation.reportFailure(MessageCodes.MyPortal.ERROR_VACANCY, "WS RicezioneVacancy", "",
							paramV);
				}
			}
		} catch (Exception e) {
			_logger.error("Errore invio vacancy al servizio portale. Progressivo:" + e.getMessage() + " richiesta = "
					+ prgRichiesta);
			Vector<String> paramV = new Vector<String>();
			paramV.add("Errore generico");
			reportOperation.reportFailure(MessageCodes.MyPortal.ERROR_VACANCY, "WS RicezioneVacancy", "", paramV);
		} finally {
			Utils.releaseResources(dc, null, null);
		}
	}

	private InserisciVacancy convertRispostaXMLToObject(String xml) throws JAXBException {
		JAXBContext jaxbContext;
		InserisciVacancy resVacancy = null;

		jaxbContext = JAXBContext
				.newInstance(it.eng.sil.coop.webservices.myportal.vacancy.risposta.ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<InserisciVacancy> root = (JAXBElement<InserisciVacancy>) jaxbUnmarshaller
				.unmarshal(new StringReader(xml));
		resVacancy = root.getValue();

		return resVacancy;
	}

}