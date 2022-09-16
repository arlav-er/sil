package it.eng.sil.module.trento;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;

public class CheckAllegatiDocPadre extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean checkAllegati = true;
		this.setSectionQuerySelect("QUERY_ALLEGATI_TEMPLATE");
		SourceBean rowAllegatiTemplate = doSelect(request, response);
		if (rowAllegatiTemplate != null) {
			Vector allegatiTemplate = rowAllegatiTemplate.getAttributeAsVector("ROW");
			int numAllegatiPrevisti = allegatiTemplate.size();
			if (numAllegatiPrevisti > 0) {
				this.setSectionQuerySelect("QUERY_DOC");
				SourceBean rowAllegatiDoc = doSelect(request, response);
				Vector allegatiDoc = null;
				int numAllegatiDoc = 0;
				if (rowAllegatiDoc != null) {
					allegatiDoc = rowAllegatiDoc.getAttributeAsVector("ROW");
					numAllegatiDoc = allegatiDoc.size();
				}
				for (int i = 0; (i < numAllegatiPrevisti && checkAllegati); i++) {
					SourceBean allegato = (SourceBean) allegatiTemplate.get(i);
					String flgObb = allegato.containsAttribute("flgobbl") ? allegato.getAttribute("flgobbl").toString()
							: "";
					String codTipoAllegatoDoc = allegato.containsAttribute("codTipoDocAllegato")
							? allegato.getAttribute("codTipoDocAllegato").toString()
							: "";
					if (flgObb.equals(Properties.FLAG_1)) {
						boolean allegatoPresente = false;
						if (numAllegatiDoc > 0) {
							for (int k = 0; (k < numAllegatiDoc && !allegatoPresente); k++) {
								SourceBean allegatoDoc = (SourceBean) allegatiDoc.get(k);
								String flgPresaVisione = allegatoDoc.containsAttribute("flgpresavisione")
										? allegatoDoc.getAttribute("flgpresavisione").toString()
										: "";
								String flgCaricaSucc = allegatoDoc.containsAttribute("flgcaricsuccessivo")
										? allegatoDoc.getAttribute("flgcaricsuccessivo").toString()
										: "";
								String codTipoDoc = allegatoDoc.containsAttribute("codtipodocumento")
										? allegatoDoc.getAttribute("codtipodocumento").toString()
										: "";
								String codStatoAtto = allegatoDoc.containsAttribute("codstatoatto")
										? allegatoDoc.getAttribute("codstatoatto").toString()
										: "";
								String nomeFile = allegatoDoc.containsAttribute("strnomedoc")
										? allegatoDoc.getAttribute("strnomedoc").toString()
										: "";
								BigDecimal prgDocumentoBlob = (BigDecimal) allegatoDoc.getAttribute("prgdocumentoblob");
								if (codStatoAtto.equalsIgnoreCase(Properties.STATO_ATTO_PROTOC)
										&& codTipoDoc.equalsIgnoreCase(codTipoAllegatoDoc)) {

									allegatoPresente = true;

									// Verifica Presa Visione/Caricamento Successivo, eccetera commentato in vista delle
									// nuove disposizioni su Pi3 - 18/01/2k19
									/*
									 * if ( (flgPresaVisione.equalsIgnoreCase(Properties.FLAG_S)) ||
									 * (flgCaricaSucc.equalsIgnoreCase(Properties.FLAG_S)) || (!nomeFile.equals("") &&
									 * prgDocumentoBlob != null) ) { allegatoPresente = true; }
									 */
								}
							}
						}
						if (!allegatoPresente) {
							checkAllegati = false;
						}
					}
				}
			}
		} else {
			checkAllegati = false;
		}

		if (checkAllegati) {
			response.setAttribute("ALLEGATIOBBL", "true");
		}

		response.setAttribute("rowsAllegatiTemplateObbl", rowAllegatiTemplate);
	}

}
