package it.eng.sil.module.firma.grafometrica;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.bean.Documento;
import it.eng.sil.coop.webservices.firmagrafometrica.custom.BlobFG;
import it.eng.sil.coop.webservices.firmagrafometrica.custom.FirmaGrafometrica;
import it.eng.sil.coop.webservices.firmagrafometrica.custom.FirmaGrafometricaXmlOutputBean;
import it.eng.sil.module.consenso.GConstants;
import it.eng.sil.security.User;
import it.eng.sil.util.blen.StringUtils;

public class FirmaDocumenti {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(FirmaDocumenti.class.getName());

	public boolean firmaDocumento(SourceBean request, SourceBean response, User user,
			TransactionQueryExecutor txExecutor, Documento doc, String ipOperatore) throws Exception {

		_logger.debug("[FirmaDocumenti] --> firmaDocumento");

		BigDecimal cdnLav = new BigDecimal((String) request.getAttribute("cdnLavoratore"));

		SourceBean lav = null;
		if (txExecutor != null) {
			lav = leggiInfoLavoratore(cdnLav, txExecutor);
		} else {
			lav = leggiInfoLavoratore(cdnLav);
		}

		String codiceFiscale = (String) lav.getAttribute("STRCODICEFISCALE");
		String cognome = (String) lav.getAttribute("STRCOGNOME");
		String nome = (String) lav.getAttribute("STRNOME");
		HashMap<String, String> beanDatiInput = new HashMap<String, String>();
		beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_SERVICE_PROVIDER,
				GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_SERVICE_PROVIDER_VALUE);
		// beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_TIPO_DOCUMENTO,
		// GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_TIPO_DOCUMENTO_VALUE);
		String tipoDocumento = getTipoDocumento(doc.getPrgDocumento().toString(), cdnLav.toString());
		if (!StringUtils.isEmpty(tipoDocumento)) {
			beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_TIPO_DOCUMENTO, tipoDocumento);
		}
		beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_ID_DOCUMENTO, doc.getPrgDocumento().toString());
		beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_NOME_LAVORATORE, nome);
		beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_COGNOME_LAVORATORE, cognome);
		beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_CODICE_FISCALE_LAVORATORE, codiceFiscale);
		beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_CODICE_OPERATORE, user.getUsername());
		beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_IP_OPERATORE, ipOperatore);

		_logger.debug("[FirmaDocumenti] --> firmaDocumento --> getNumKloDocumento");
		beanDatiInput.put(GConstants.WS_FIRMA_GRAFOMETRICA_XML_PARAM_NUM_KLO_DOCUMENTO,
				doc.getNumKloDocumento().toString());
		_logger.debug(
				"[FirmaDocumenti] --> firmaDocumento --> beanDatiInput --> WS_FIRMA_GRAFOMETRICA_XML_PARAM_NUM_KLO_DOCUMENTO: "
						+ doc.getNumKloDocumento().toString());

		FirmaGrafometrica firmaGrafometrica = new FirmaGrafometrica();
		// Documento docc = getDocumento();
		BlobFG blobOutput = firmaGrafometrica.doSignedPDF(doc.getTempFile(), beanDatiInput);
		FirmaGrafometricaXmlOutputBean outBean = firmaGrafometrica.getXmlBeanOutput(blobOutput.getXmlOutput());
		if (outBean.getCodice() == 1) {
			File docFirmabile = blobOutput.getFile();
			doc.setTempFile(docFirmabile);
			_logger.info("[FirmaDocumenti] --> Transformation PDF to PDF/A and Sign To Doc result: TRUE");
			return true;
		}

		_logger.warn("[FirmaDocumenti] --> Transformation PDF to PDF/A and Sign To Doc result: FALSE");
		return false;
	}

	/**
	 * 
	 * @param cdnLav
	 * @param tex
	 * @return SourceBean contenente le info sul lavoratore
	 * @throws Exception
	 */
	private SourceBean leggiInfoLavoratore(BigDecimal cdnLav, TransactionQueryExecutor tex) throws Exception {
		SourceBean row = (SourceBean) tex.executeQuery("GET_DATI_LAV", new Object[] { cdnLav }, "SELECT");
		if (row == null) {
			throw new Exception("impossibile leggere le info del lavoratore");
		}
		return getRowAttribute(row);
	}

	private SourceBean leggiInfoLavoratore(BigDecimal cdnLav) throws Exception {
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_DATI_LAV", new Object[] { cdnLav }, "SELECT",
				Values.DB_SIL_DATI);
		if (row == null) {
			throw new Exception("impossibile leggere le info del lavoratore");
		}
		return getRowAttribute(row);
	}

	private SourceBean getRowAttribute(SourceBean row) {
		return row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
	}

	private String getTipoDocumento(String prgDocumento, String cdnLavoratore) throws Exception {

		String tipoDocumento = "";

		Object[] inputParameters = new Object[2]; // END_POINT_NAME
		inputParameters[0] = prgDocumento;
		inputParameters[1] = cdnLavoratore;
		SourceBean ret = (SourceBean) QueryExecutor.executeQuery("FIRMA_GRAFOMETRICA_GET_TIPO_DOCUMENTO",
				inputParameters, "SELECT", Values.DB_SIL_DATI);

		tipoDocumento = (String) ret.getAttribute("ROW.tipodocumento");

		_logger.debug("Firma Documenti per Firma Grafometrica - getTipoDocumento: " + tipoDocumento);

		return tipoDocumento;

	}

}
