package it.eng.sil.module.anag;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.coop.webservices.coapadesioneSet.pojo.yg.statoAdesioneYg.DatiStatoAdesione;
import it.eng.sil.coop.webservices.coapadesioneSet.portale.ServizicoapWSProxy;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

public class SetStatoAdesioneYg extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1533726811526662058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SetStatoAdesioneYg.class.getName());
	private String className = this.getClass().getName();
	private String END_POINT_NAME = "SetStatoAdesioneYg";

	public void service(SourceBean request, SourceBean response) {
		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		BigDecimal p_cdnUtente = new BigDecimal(user.getCodut());

		String cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));
		String ns = Utils.notNull(request.getAttribute("nuovoStato"));
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		// String codMinSap = "";
		TransactionQueryExecutor tex = null;

		try {
			_logger.info("CHIAMATA SET STATO ADESIONE, CDNLAVORATORE =" + cdnLavoratore);

			SourceBean anLavoratoreSB = doSelect(request, response);
			String strCodiceFiscale = (String) anLavoratoreSB.getAttribute("ROW.STRCODICEFISCALE");

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String statoYgEndpoint = eps.getUrl(END_POINT_NAME);
			ServizicoapWSProxy servizicoapWSProxy = new ServizicoapWSProxy(statoYgEndpoint);

			StringHolder esito = new StringHolder("OK");
			StringHolder messaggioErrore = new StringHolder();

			DatiStatoAdesione dsa = new DatiStatoAdesione();
			dsa.setStatoAdesione(ns);
			dsa.setDataAdesione(toXMLGregorianCalendarDate(new Date()));
			dsa.setCodiceFiscale(strCodiceFiscale);
			_logger.info("STO PER INVOCARE WS CON NUOVO STATO = " + ns);
			servizicoapWSProxy.setStatoAdesioneYG(creaXMLoutAccount(dsa), esito, messaggioErrore);

			response.setAttribute("YG_STATO_SET", esito.value + " - " + esito.value);
			// QUI VA LA LOGICA

		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.YG.ERR_WS_VERIFICASAP);
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + ": errore chiamata WS", ex);
			try {
				if (tex != null)
					tex.rollBackTransaction();
			} catch (EMFInternalError e1) {
				_logger.error("SetStatoAdesioneYg: problema con la rollback", e1);
			}
		}
	}

	private String creaXMLoutAccount(DatiStatoAdesione account) {
		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";
			JAXBContext jc = JAXBContext

					.newInstance(DatiStatoAdesione.class);
			Marshaller marshaller = jc.createMarshaller();
			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
			/*
			 * Schema schema = Utils.getXsdSchema("servizi_cittadino" + File.separator +
			 * "outputXML_getAccountCittadino.xsd");
			 */

			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + "statoAdesione" + File.separator + "Rev001SetStatoAdesione.xsd");
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

	private static XMLGregorianCalendar toXMLGregorianCalendarDate(Date date)
			throws DatatypeConfigurationException, ParseException {
		GregorianCalendar gc = new GregorianCalendar();
		// Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
		gc.setTime(date);

		XMLGregorianCalendar xc = null;

		xc = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(gc.get(Calendar.YEAR),
				gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);

		return xc;
	}
}