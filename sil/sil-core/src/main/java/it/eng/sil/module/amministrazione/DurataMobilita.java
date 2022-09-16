/*
 * Creato il 30-mag-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author landi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DurataMobilita extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DurataMobilita.class.getName());

	public static final int ANNO_MAX_REGOLA_VECCHIA = 2014;

	public void service(SourceBean request, SourceBean response) {
		SourceBean sbDurataMob = null;
		SourceBean sbLav = null;
		String dataNascitaLavoratore = "";
		String numMesi = "0";
		BigDecimal anniLav = null;
		BigDecimal strEtaMin = null;
		BigDecimal strEtaMax = null;
		BigDecimal numAnnoTabella = null;
		int numAnnoRegola = 0;
		try {
			setSectionQuerySelect("GET_DURATA_MOBILITA");
			sbDurataMob = doSelect(request, response);
			Vector rows = sbDurataMob.getAttributeAsVector("ROW");
			setSectionQuerySelect("GET_DATA_NASCITA_LAV");
			sbLav = doSelect(request, response);
			dataNascitaLavoratore = (sbLav != null) ? sbLav.getAttribute("ROW.DATNASC").toString() : "";
			String dataFineMov = StringUtils.getAttributeStrNotNull(request, "DATFINEMOV");
			if (!dataFineMov.equals("")) {
				numAnnoRegola = DateUtils.getAnno(DateUtils.giornoSuccessivo(dataFineMov));
				if (numAnnoRegola < ANNO_MAX_REGOLA_VECCHIA) {
					numAnnoRegola = ANNO_MAX_REGOLA_VECCHIA;
				}
			} else {
				numAnnoRegola = DateUtils.getAnno(DateUtils.getNow());
			}
			anniLav = new BigDecimal(DateUtils.getEta(dataNascitaLavoratore, dataFineMov));
			for (int i = 0; i < rows.size(); i++) {
				SourceBean sbCurr = (SourceBean) rows.get(i);
				strEtaMin = sbCurr.containsAttribute("NUMETAMINIMA")
						? new BigDecimal(sbCurr.getAttribute("NUMETAMINIMA").toString())
						: null;
				strEtaMax = sbCurr.containsAttribute("NUMETAMASSIMA")
						? new BigDecimal(sbCurr.getAttribute("NUMETAMASSIMA").toString())
						: null;
				numAnnoTabella = sbCurr.containsAttribute("NUMANNO")
						? new BigDecimal(sbCurr.getAttribute("NUMANNO").toString())
						: null;
				if ((strEtaMax == null || strEtaMin == null) && (numAnnoTabella.intValue() == numAnnoRegola)) {
					numMesi = sbCurr.containsAttribute("NUMDURATAMOB") ? sbCurr.getAttribute("NUMDURATAMOB").toString()
							: "";
					break;
				}
				if ((strEtaMax != null && strEtaMin != null && strEtaMin.compareTo(anniLav) <= 0
						&& strEtaMax.compareTo(anniLav) >= 0) && (numAnnoTabella.intValue() == numAnnoRegola)) {
					numMesi = sbCurr.containsAttribute("NUMDURATAMOB") ? sbCurr.getAttribute("NUMDURATAMOB").toString()
							: "";
					break;
				}
			}

			if (numMesi.equals("")) {
				numMesi = "0";
			}
			SourceBean row = new SourceBean("DURATA_MOB");
			row.setAttribute("MESI", numMesi);
			response.setAttribute((SourceBean) row);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore recupero durata mobilità", e);

		}

	}

}