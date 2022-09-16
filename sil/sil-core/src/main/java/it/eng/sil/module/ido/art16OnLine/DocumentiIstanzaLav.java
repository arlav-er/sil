package it.eng.sil.module.ido.art16OnLine;

import com.adobe.idp.services.ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloProxy;
import com.adobe.idp.services.holders.XMLHolder;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;

public class DocumentiIstanzaLav extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -196057433677559787L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DocumentiIstanzaLav.class.getName());

	public static final String END_POINT_NAME = "GetAttachmentsListByProtocollo";

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		disableMessageIdFail();
		disableMessageIdSuccess();

		String idIstanza = null;
		String annoProtIstanza = null;
		String protIstanza = null;
		String candidato = null;
		try {

			SourceBean row = (SourceBean) serviceRequest.getAttribute("ROW");
			idIstanza = row.getAttribute("STRIDISTANZA").toString();
			annoProtIstanza = row.getAttribute("NUMANNOPROTISTANZA").toString();
			protIstanza = row.getAttribute("STRPROTISTANZA").toString();
			candidato = row.getAttribute("cognomeNome").toString();

			serviceResponse.setAttribute("candidato", candidato);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			// recupera endpoint servizio
			String endpointServizio = eps.getUrl(END_POINT_NAME);

			ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloProxy proxy = new ModulisticaOnline_GestioneAllegati_GetAttachmentsListByProtocolloProxy();
			proxy.setEndpoint(END_POINT_NAME);

			XMLHolder dataXml = new XMLHolder();
			XMLHolder outputXml = new XMLHolder();

			proxy.invoke(annoProtIstanza, idIstanza, protIstanza, dataXml, outputXml);

		} catch (Exception e) {
			_logger.error("ScaricoIstanze", e);
		}
	}

}
