package it.eng.sil.action.report.patto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.module.AccessoSemplificato;

public class StampaBuonoReimpiego extends AbstractSimpleReport {

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile != null) {
				setStrNomeDoc("StampaBuonoReimpiego." + tipoFile);
			} else {
				setStrNomeDoc("StampaBuonoReimpiego.pdf");
			}
			setStrDescrizione("Stampa Buono Reimpiego");

			setReportPath("patto/Buono_Reimpiego_UMB_CC.rpt");

			Map prompts = new HashMap();

			AccessoSemplificato _db = new AccessoSemplificato(this);
			// per recuperare le informazioni utilizzate per la generazione
			// del report non e' necessario
			// l'utilizzo della transazione. Se necessaria nei passi
			// successivi verra' abilitata.
			_db.enableSimpleQuery();
			_db.disableMessageIdFail();
			_db.disableMessageIdSuccess();
			_db.setSectionQuerySelect("QUERY_CONTROLLO_DISABILITA");
			SourceBean numIscrL68B = _db.doSelect(request, response);
			int numIscrL68 = SourceBeanUtils.getAttrInt(numIscrL68B, "ROW.NUMERO", 0);
			String flgL68 = "";
			if (numIscrL68 > 0)
				flgL68 = "S";
			prompts.put("flgL68", flgL68);
			prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));

			Object dataProtObj = request.getAttribute("dataProt");
			String dataProt = "";
			if (dataProtObj != null) {
				dataProt = dataProtObj.toString();
			}
			prompts.put("dataProt", dataProt);

			Object numProt = request.getAttribute("numProt");
			prompts.put("numProt", numProt);

			setPromptFields(prompts);

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");
			if (salva != null && salva.equalsIgnoreCase("true")) {
				setStrChiavetabella((String) request.getAttribute("strchiavetabella"));
				insertDocument(request, response);
			} else if (apri != null && apri.equalsIgnoreCase("true"))
				showDocument(request, response);

		}

	}
}