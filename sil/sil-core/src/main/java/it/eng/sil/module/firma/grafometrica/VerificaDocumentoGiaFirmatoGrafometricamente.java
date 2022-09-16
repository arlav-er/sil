package it.eng.sil.module.firma.grafometrica;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.pi3.ProtocolloPi3DBManager;

public class VerificaDocumentoGiaFirmatoGrafometricamente extends AbstractSimpleModule {

	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(VerificaDocumentoGiaFirmatoGrafometricamente.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();

		String prgDoc = (String) serviceRequest.getAttribute("prgDocumento");

		// VERIFICA SE IL DOCUMENTO E' STATO GIA' FIRMATO GRAFOMETRICAMENTE (da AM_DOCUMENTO_BLOB)
		boolean isDocumentoGiaFirmato = dbManager.isAllegatoDocumentoFirmato(prgDoc.toString());

		if (isDocumentoGiaFirmato) {
			serviceResponse.setAttribute("IS_DOCUMENTO_GIA_FIRMATO_GRAFOMETRICAMENTE", "S");
		} else {
			serviceResponse.setAttribute("IS_DOCUMENTO_GIA_FIRMATO_GRAFOMETRICAMENTE", "N");
		}

	}

}
