package it.eng.sil.module.anag;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.pojo.yg.verificaSAP.VerificaSAP;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSProxy;

public class ImportaVerificaEsistenzaSAP extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811528932058L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ImportaVerificaEsistenzaSAP.class.getName());
	private String className = this.getClass().getName();
	private String END_POINT_NAME = "VerificaEsistenzaSAP";

	public void service(SourceBean request, SourceBean response) {
		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		BigDecimal p_cdnUtente = new BigDecimal(user.getCodut());

		String strCodiceFiscale = Utils.notNull(request.getAttribute("CFLAVORATORE"));
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		disableMessageIdFail();
		disableMessageIdSuccess();

		String codMinSap = "";

		try {
			_logger.info("CHIAMATA VERIFICA ESISTENZA SAP, CodiceFiscale =" + strCodiceFiscale);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String verificaEsistenzaSAPEndPoint = eps.getUrl(END_POINT_NAME);
			ServizicoapWSProxy servizicoapWSProxy = new ServizicoapWSProxy(verificaEsistenzaSAPEndPoint);

			VerificaSAP xmlVerifica = new VerificaSAP();
			xmlVerifica.setCodiceFiscale(strCodiceFiscale);

			codMinSap = servizicoapWSProxy.verificaEsistenzaSAP(convertVerificaSapToString(xmlVerifica));

			if (codMinSap != null && !codMinSap.equals("") && !codMinSap.equals("0")
					&& !codMinSap.trim().startsWith("X001")) {
				Vector<String> params = new Vector<String>();
				params.add(codMinSap);
				response.setAttribute("CODMINSAP", codMinSap);
				reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_TROVATO_NO_MODIFICA, "service",
						"SAP trovata", params);
			} else {
				if (codMinSap != null && codMinSap.trim().startsWith("X001")) {
					Vector<String> params = new Vector<String>();
					params.add(codMinSap);
					reportOperation.reportFailure(MessageCodes.YG.WS_VERIFICASAP_RISPOSTA_MINISTERO, "service",
							"Errore ministero SAP", params);
				} else {
					if (codMinSap != null && ("0").equalsIgnoreCase(codMinSap)) {
						Object[] params = new Object[1];
						params[0] = strCodiceFiscale.toUpperCase();
						SourceBean result = (SourceBean) QueryExecutor.executeQuery("GET_CDNLAVORATORE", params,
								"SELECT", Values.DB_SIL_DATI);
						if (result != null) {
							result = result.containsAttribute("ROW") ? (SourceBean) result.getAttribute("ROW") : result;
							BigDecimal cdnLavoratore = (BigDecimal) result.getAttribute("cdnlavoratore");
							boolean risChiudiZero = VerificaEsistenzaSAP.chiudiRecordSapZero(cdnLavoratore, p_cdnUtente,
									null);
						}
					}
					_logger.debug("Lavoratore: " + strCodiceFiscale + " - SAP Nazionale inesistente");
					reportOperation.reportSuccess(MessageCodes.YG.WS_VERIFICASAP_NON_TROVATO);
				}
			}
		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.YG.ERR_WS_VERIFICASAP);
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + ": errore chiamata SAP", ex);
		}
	}

	private String convertVerificaSapToString(VerificaSAP verifica) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(VerificaSAP.class);
		Marshaller marshaller = jc.createMarshaller();
		StringWriter writer = new StringWriter();
		marshaller.marshal(verifica, writer);
		String xmlVerificaSAP = writer.getBuffer().toString();
		return xmlVerificaSAP;
	}
}