package it.eng.sil.module.patto;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.rpc.holders.StringHolder;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.module.AbstractSimpleModule;
import it.gov.lavoro.servizi.servizicoapYg.ServizicoapWSProxy;
import it.gov.lavoro.servizi.servizicoapYg.types.Risposta_checkUtenteYG_TypeEsito;
import it.gov.lavoro.servizi.servizicoapYg.types.holders.Risposta_checkUtenteYG_TypeEsitoHolder;

public class CheckUtenteYG extends AbstractSimpleModule {

	private static final long serialVersionUID = 1L;
	public static final String END_POINT_NAME = "CheckUtenteYG";
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CheckUtenteYG.class.getName());

	public void service(SourceBean request, SourceBean response) {

		String codiceFiscale = (String) request.getAttribute("strCodiceFiscale");
		String address = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			ServizicoapWSProxy proxy = new ServizicoapWSProxy();
			EndPoint endPoint = new EndPoint();
			endPoint.init(dataSourceJndiName, END_POINT_NAME);
			address = endPoint.getUrl();
			_logger.debug("CheckUtenteYG: codice fiscale= " + codiceFiscale + ", URL Web Service= " + address);
			proxy.setEndpoint(address);
			Risposta_checkUtenteYG_TypeEsitoHolder esito = new Risposta_checkUtenteYG_TypeEsitoHolder();
			StringHolder messaggioErrore = new StringHolder();

			it.eng.sil.pojo.yg.checkUtenteYG.CheckUtenteYG checkUtente = new it.eng.sil.pojo.yg.checkUtenteYG.CheckUtenteYG();
			checkUtente.setCodiceFiscale(codiceFiscale);
			String inputXml = convertCheckUtenteYGToString(checkUtente);

			proxy.checkUtenteYG(inputXml, esito, messaggioErrore);
			_logger.debug("CheckUtenteYG: codice fiscale= " + codiceFiscale + ", URL Web Service= " + address
					+ ", Esito restituito dal WS= " + esito);

			if ((Risposta_checkUtenteYG_TypeEsito.OK).equals(esito.value)) {
				_logger.debug("CheckUtenteYG: Adesione presente. Codice fiscale= " + codiceFiscale
						+ ", URL Web Service= " + address);

				reportOperation.reportSuccess(MessageCodes.YG.ADESIONE_PRESENTE_YG);
			} else if ((Risposta_checkUtenteYG_TypeEsito.KO).equals(esito.value)) {
				_logger.debug("CheckUtenteYG: Adesione assente. Codice fiscale= " + codiceFiscale
						+ ", URL Web Service= " + address);

				reportOperation.reportWarning(MessageCodes.YG.ADESIONE_ASSENTE_YG);
			}

		} catch (javax.xml.rpc.ServiceException se) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CheckUtenteYG: impossibile chiamare il Web Service. Codice fiscale= " + codiceFiscale
							+ ", URL Web Service= " + address,
					se);

			reportOperation.reportFailure(MessageCodes.YG.ERR_CONNESSIONE_YG);
		} catch (java.rmi.RemoteException re) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CheckUtenteYG: errore nell'esecuzione remota del Web Service. Codice fiscale= " + codiceFiscale
							+ ", URL Web Service= " + address,
					re);

			reportOperation.reportFailure(MessageCodes.YG.ERR_EXEC_WS_YG);
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, "CheckUtenteYG: errore generico. Codice fiscale= "
					+ codiceFiscale + ", URL Web Service= " + address, e);

		}

	}

	public String convertCheckUtenteYGToString(it.eng.sil.pojo.yg.checkUtenteYG.CheckUtenteYG checkUtenteYG)
			throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance(it.eng.sil.pojo.yg.checkUtenteYG.CheckUtenteYG.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(checkUtenteYG, writer);
		String xmlCheckUtenteYG = writer.getBuffer().toString();
		return xmlCheckUtenteYG;
	}
}