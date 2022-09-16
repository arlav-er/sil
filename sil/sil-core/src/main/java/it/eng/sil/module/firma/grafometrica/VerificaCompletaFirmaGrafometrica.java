package it.eng.sil.module.firma.grafometrica;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.consenso.Consenso;
import it.eng.sil.module.consenso.ConsensoFirmaBean;
import it.eng.sil.module.consenso.GConstants;
import it.eng.sil.module.pi3.ProtocolloPi3DBManager;

public class VerificaCompletaFirmaGrafometrica extends AbstractSimpleModule {

	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(VerificaCompletaFirmaGrafometrica.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);

		ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();

		String prgDoc = (String) serviceRequest.getAttribute("prgDocumento");

		Documento doc = new Documento();
		doc.setPrgDocumento(new BigDecimal(prgDoc));
		try {
			// VERIFICA SE IL DOCUMENTO E' UNA STAMPA PARAMETRICA
			setSectionQuerySelect("QUERY_IS_STAMPE_PARAM");
			SourceBean rowStampeParam = doSelect(serviceRequest, serviceResponse, false);
			if (rowStampeParam != null) {
				if (rowStampeParam.containsAttribute("ROW")) {
					doc.selectStampaParam();
				} else {
					doc.select();
				}
			} else {
				doc.select();
			}

			// VERIFICA SE IL TIPO DI DOCUMENTO E' PREDISPOSTO PER ESSERE FIRMATO DA SISTEMA
			BigDecimal prgTemplateStampa = dbManager.getPrgTemplateStampa(doc.getCodTipoDocumento());
			boolean isDocumentoFirmabileDaConfigurazioneDiSistema = dbManager
					.isDocumentTypeGraphSignature(prgTemplateStampa.toString());

			// VERIFICA SE IL DOCUMENTO E' DI UN UTENTE LAVORATORE ED HA IL CONSENSO ATTIVO PER EFFETTUARE LA FIRMA
			boolean isLavoratoreConsensoAttivo = false;
			String codiceConsenso = "";
			if (doc.getCdnLavoratore() != null) {
				if (!StringUtils.isEmpty(doc.getCdnLavoratore().toString())) {
					Consenso consenso = new Consenso(null);
					ConsensoFirmaBean cfb = consenso.getConsensoFirma(doc.getCdnLavoratore().toString());
					codiceConsenso = cfb.getCodiceStatoConsenso();
					if (!StringUtils.isEmpty(codiceConsenso)
							&& codiceConsenso.equals(GConstants.CONSENSO_ATTIVO_CODICE)) {
						isLavoratoreConsensoAttivo = true;
					}
				}
			}

			// VERIFICA SE IL DOCUMENTO E' STATO GIA' FIRMATO GRAFOMETRICAMENTE (da AM_DOCUMENTO_BLOB)
			boolean isDocumentoGiaFirmato = dbManager.isAllegatoDocumentoFirmato(prgDoc.toString());

			if (isDocumentoFirmabileDaConfigurazioneDiSistema && isLavoratoreConsensoAttivo) {
				if (!isDocumentoGiaFirmato) {
					serviceResponse.setAttribute("IS_OK_DOCUMENTO_FOR_FIRMA_GRAFOMETRICA", "S");
				} else {
					serviceResponse.setAttribute("IS_OK_DOCUMENTO_FOR_FIRMA_GRAFOMETRICA", "N");
				}
			} else {
				serviceResponse.setAttribute("IS_OK_DOCUMENTO_FOR_FIRMA_GRAFOMETRICA", "N");
			}

		} catch (Exception ex) {
			_logger.error(ex.getMessage());
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, ex, "service", "");
		}

	}

}
