package it.eng.sil.module.anag;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.rpc.holders.CalendarHolder;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.axis.holders.DateHolder;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.coop.webservices.coapadesioneGet.pojo.yg.statoAdesioneYg.DatiStatoAdesione;
import it.eng.sil.coop.webservices.coapadesioneGet.portale.ServizicoapWSProxy;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

public class GetStatoAdesioneYg extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetStatoAdesioneYg.class.getName());
	private String className = this.getClass().getName();
	private String END_POINT_NAME = "GetStatoAdesioneYg";

	public void service(SourceBean request, SourceBean response) {
		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		BigDecimal p_cdnUtente = new BigDecimal(user.getCodut());

		String cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		// String codMinSap = "";
		TransactionQueryExecutor tex = null;

		try {
			_logger.info("CHIAMATA GET STATO ADESIONE, CDNLAVORATORE = " + cdnLavoratore);

			SourceBean anLavoratoreSB = doSelect(request, response);
			String strCodiceFiscale = (String) anLavoratoreSB.getAttribute("ROW.STRCODICEFISCALE");

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			// recupera endpoint servizio
			String statoYgEndpoint = eps.getUrl(END_POINT_NAME);
			ServizicoapWSProxy servizicoapWSProxy = new ServizicoapWSProxy(statoYgEndpoint);

			StringHolder esito = new StringHolder("FUF");
			StringHolder messaggioErrore = new StringHolder();
			messaggioErrore.value = "TEST";

			DateHolder dataAdesione = new DateHolder();
			StringHolder statoAdesione = new StringHolder("PAP");
			CalendarHolder dataStatoAdesione = new CalendarHolder();

			DatiStatoAdesione dsa = new DatiStatoAdesione();
			dsa.setCodiceFiscale(strCodiceFiscale);

			servizicoapWSProxy.getStatoAdesioneYG(creaXMLoutAccount(dsa), esito, messaggioErrore, dataAdesione,
					statoAdesione, dataStatoAdesione);
			// codMinSap = "AA01007901C";
			Vector<String> params = new Vector<String>();
			// QUI VA LA LOGICA

			response.setAttribute("YG_STATO", esito.value + " - Stato attuale: " + statoAdesione.value);

		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.YG.ERR_WS_VERIFICASAP);
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + ": errore chiamata WS", ex);
			try {
				if (tex != null)
					tex.rollBackTransaction();
			} catch (EMFInternalError e1) {
				_logger.error("VerificaEsistenzaSAP: problema con la rollback", e1);
			}

		}
	}

	private String creaXMLoutAccount(DatiStatoAdesione account) {
		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";
			JAXBContext jc = JAXBContext.newInstance(DatiStatoAdesione.class);
			Marshaller marshaller = jc.createMarshaller();
			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + "statoAdesione" + File.separator + "Rev001GetStatoAdesione.xsd");
			StreamSource streamSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(streamSource);

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

			marshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			marshaller.marshal(account, writer);
			String xmlRichiesta = writer.getBuffer().toString();
			if (xmlRichiesta.contains("<?xml")) {
				xmlRichiesta = xmlRichiesta.substring(xmlRichiesta.indexOf("?>") + 2);
			}
			return xmlRichiesta;
		} catch (Exception e) {
			_logger.error("creaXMLoutAccount: " + e);
			return "";
		}
	}

}
