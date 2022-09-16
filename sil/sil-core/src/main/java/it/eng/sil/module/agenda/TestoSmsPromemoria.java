package it.eng.sil.module.agenda;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.sms.ContattoSMS;

public class TestoSmsPromemoria extends AbstractSimpleModule {

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		String testoSMS = "";
		String msg1, msg2, msg3, msg4, msgRdc;
		String nome = "", cognome = "", cpi = "", cpiMin = "", data = "", ora = "", durata = "", telCpi = "",
				telRdcCpi = "", cellLav = "", indirizzoCpi = "";
		ContattoSMS contatto = new ContattoSMS();
		SourceBean testoRow = null;

		SourceBean appuntamentoRow = (SourceBean) getResponseContainer().getServiceResponse()
				.getAttribute("M_AGENDA_SMS_DETTAPPUNTAMENTI.ROWS.ROW");
		String codServizio = StringUtils.getAttributeStrNotNull(appuntamentoRow, "CODSERVIZIO");
		String maxLengthTestoSMS;

		/*
		 * if (codServizio!=null && "APPRDC".equals(codServizio)) { maxLengthTestoSMS="320"; testoRow =
		 * contatto.getTesto("PROMRDC"); if (testoRow != null) { msgRdc =
		 * StringUtils.getAttributeStrNotNull(((SourceBean) testoRow), "ROW.STRTEMPLATE"); SourceBean lavoratoreRow =
		 * (SourceBean)getResponseContainer().getServiceResponse().getAttribute("M_GetLavoratoreAnag.ROWS.ROW"); nome =
		 * StringUtils.getAttributeStrNotNull(lavoratoreRow, "STRNOME"); cognome =
		 * StringUtils.getAttributeStrNotNull(lavoratoreRow, "STRCOGNOME"); cpi =
		 * StringUtils.getAttributeStrNotNull(appuntamentoRow, "DESCRIZIONECPI").replaceAll(" {2,}", " "); cpiMin =
		 * StringUtils.getAttributeStrNotNull(appuntamentoRow, "STRDESCRIZIONEMIN"); if (cpiMin.startsWith("CPI"))
		 * cpiMin = cpiMin.substring(4); data = StringUtils.getAttributeStrNotNull(appuntamentoRow, "DATA"); ora =
		 * StringUtils.getAttributeStrNotNull(appuntamentoRow, "ORARIO"); BigDecimal durataBD = (BigDecimal)
		 * appuntamentoRow.getAttribute("NUMMINUTI"); if (durataBD!=null) durata = durataBD.toPlainString(); // durata =
		 * StringUtils.getAttributeStrNotNull(appuntamentoRow, "NUMMINUTI"); telCpi =
		 * StringUtils.getAttributeStrNotNull(appuntamentoRow, "TELCPI"); telRdcCpi =
		 * StringUtils.getAttributeStrNotNull(appuntamentoRow, "TELRDCCPI"); cellLav =
		 * StringUtils.getAttributeStrNotNull(lavoratoreRow, "STRCELL"); indirizzoCpi =
		 * StringUtils.getAttributeStrNotNull(appuntamentoRow, "INDIRIZZOCPI").replaceAll(" {2,}", " ");
		 * 
		 * testoSMS = msgRdc.replace("@nome@", nome) .replace("@cognome@", cognome) .replace("@cpi@", cpi)
		 * .replace("@cpi_min@", cpiMin) .replace("@data@", data) .replace("@ora@", ora) .replace("@durata@", durata)
		 * .replace("@tel_cpi@", telRdcCpi) .replace("@cellLav@", cellLav) .replace("@indirizzoCpi@", indirizzoCpi);
		 * 
		 * }
		 * 
		 * } else { maxLengthTestoSMS="160"; testoRow = contatto.getTesto("PROMAPPU"); if (testoRow != null) { msg1 =
		 * StringUtils.getAttributeStrNotNull(((SourceBean) testoRow), "ROW.STR30MSG1"); msg2 =
		 * StringUtils.getAttributeStrNotNull(((SourceBean) testoRow), "ROW.STR30MSG2"); msg3 =
		 * StringUtils.getAttributeStrNotNull(((SourceBean) testoRow), "ROW.STR30MSG3"); msg4 =
		 * StringUtils.getAttributeStrNotNull(((SourceBean) testoRow), "ROW.STR30MSG4");
		 * 
		 * if (appuntamentoRow != null) { testoSMS = msg1 + " il giorno " +
		 * StringUtils.getAttributeStrNotNull(appuntamentoRow, "DATA") + " alle ore " +
		 * StringUtils.getAttributeStrNotNull(appuntamentoRow, "ORARIO") + " presso CPI di " +
		 * StringUtils.getAttributeStrNotNull(appuntamentoRow, "DESCRIZIONECPI") + " " + msg2 + " " + msg3 + " " + msg4;
		 * } } }
		 */

		testoRow = contatto.getTesto(codServizio);

		msgRdc = StringUtils.getAttributeStrNotNull(((SourceBean) testoRow), "ROW.STRTEMPLATE");
		maxLengthTestoSMS = ((BigDecimal) testoRow.getAttribute("ROW.MAX_LEN")).toPlainString();
		SourceBean lavoratoreRow = (SourceBean) getResponseContainer().getServiceResponse()
				.getAttribute("M_GetLavoratoreAnag.ROWS.ROW");
		nome = StringUtils.getAttributeStrNotNull(lavoratoreRow, "STRNOME");
		cognome = StringUtils.getAttributeStrNotNull(lavoratoreRow, "STRCOGNOME");
		appuntamentoRow.setAttribute("STRNOME", nome);
		appuntamentoRow.setAttribute("STRCOGNOME", cognome);

		String nomeCampo = "";
		String valoreCampo = "";

		testoSMS = msgRdc;
		/*
		 * Nel campo STRTEMPLATE della tabella TS_SMS sono contenuti i marcatori compresi tra @ con gli stessi nomi dei
		 * campi del SourceBean
		 */

		for (int i = 0; i < msgRdc.length(); i++) {
			String lettera = msgRdc.charAt(i) + "";
			if (lettera != null && lettera.equalsIgnoreCase("@")) {
				if (msgRdc.indexOf("@", i + 1) >= 1) {
					nomeCampo = msgRdc.substring(i + 1, msgRdc.indexOf("@", i + 1));
				}
				valoreCampo = StringUtils.getAttributeStrNotNull(appuntamentoRow, nomeCampo);
				if (valoreCampo.startsWith("CPI"))
					valoreCampo = valoreCampo.substring(4);
				testoSMS = testoSMS.replace("@" + nomeCampo + "@", valoreCampo);
			}
		}

		testoSMS = testoSMS.replaceAll(" {2,}", " ");

		serviceResponse.setAttribute("testo", testoSMS);
		serviceResponse.setAttribute("maxLengthTestoSMS", maxLengthTestoSMS);

	}

}
