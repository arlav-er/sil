/*
 * Creato il 2-ago-04
 *
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;

//Configurazione per la lista di aggregazione/inserimento delle unita aziendali in validazione movimenti
public class DynamicListaUnitaAziendeConfig extends AbstractConfigProvider {
	public DynamicListaUnitaAziendeConfig() {
	}

	private String className = this.getClass().getName();
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicListaUnitaAziendeConfig.class.getName());

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {
		SourceBean configSB = null;
		SessionContainer sessionContainer = this.getSessionContainer();
		try {
			configSB = new SourceBean("CONFIG");
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");
			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_1b = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");
			SourceBean col_5 = new SourceBean("COLUMN");
			SourceBean col_6 = new SourceBean("COLUMN");
			col_1.setAttribute("name", "STRINDIRIZZO");
			col_1.setAttribute("label", "Indirizzo");
			colonneSB.setAttribute(col_1);
			col_1b.setAttribute("name", "comune_az");
			col_1b.setAttribute("label", "Comune");
			colonneSB.setAttribute(col_1b);
			col_2.setAttribute("name", "codAteco");
			col_2.setAttribute("label", "Codice Ateco");
			colonneSB.setAttribute(col_2);
			col_3.setAttribute("name", "strDesAtecoUAz");
			col_3.setAttribute("label", "Descrizione Ateco");
			colonneSB.setAttribute(col_3);
			col_4.setAttribute("name", "STRTEL");
			col_4.setAttribute("label", "Telefono");
			colonneSB.setAttribute(col_4);
			col_5.setAttribute("name", "strNumeroInps");
			col_5.setAttribute("label", "Numero INPS");
			colonneSB.setAttribute(col_5);
			col_6.setAttribute("name", "STRNUMREGISTROCOMMITT");
			col_6.setAttribute("label", "Num. Reg. Committenti");
			colonneSB.setAttribute(col_6);
			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");
			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/add.gif");
			selectCaptionSB.setAttribute("label", "Seleziona Unita");
			selectCaptionSB.setAttribute("confirm", "false");
			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "PAGE");
			parameterSB1.setAttribute("type", "ABSOLUTE");
			parameterSB1.setAttribute("value", "MovimentiSelezionaUnitaRefreshPage");
			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "PRGMOVIMENTOAPP");
			parameterSB2.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "PRGMOVIMENTOAPP");
			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "CDNFUNZIONE");
			parameterSB3.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB3.setAttribute("type", "RELATIVE");
			parameterSB3.setAttribute("value", "CDNFUNZIONE");
			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "CONTESTO");
			parameterSB4.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB4.setAttribute("type", "RELATIVE");
			parameterSB4.setAttribute("value", "CONTESTO");
			SourceBean parameterSB5 = new SourceBean("PARAMETER");
			parameterSB5.setAttribute("name", "PRGUNITA");
			parameterSB5.setAttribute("scope", "LOCAL");
			parameterSB5.setAttribute("type", "RELATIVE");
			parameterSB5.setAttribute("value", "PRGUNITA");
			SourceBean parameterSB6 = new SourceBean("PARAMETER");
			parameterSB6.setAttribute("name", "PRGAZIENDA");
			parameterSB6.setAttribute("scope", "LOCAL");
			parameterSB6.setAttribute("type", "RELATIVE");
			parameterSB6.setAttribute("value", "PRGAZIENDA");
			SourceBean parameterSB7 = new SourceBean("PARAMETER");
			parameterSB7.setAttribute("name", "FUNZ_AGGIORNAMENTO");
			parameterSB7.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB7.setAttribute("type", "RELATIVE");
			parameterSB7.setAttribute("value", "FUNZ_AGGIORNAMENTO");
			SourceBean parameterSB8 = new SourceBean("PARAMETER");
			parameterSB8.setAttribute("name", "PRGMOBILITAISCRAPP");
			parameterSB8.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB8.setAttribute("type", "RELATIVE");
			parameterSB8.setAttribute("value", "PRGMOBILITAISCRAPP");
			SourceBean parameterSB9 = new SourceBean("PARAMETER");
			parameterSB9.setAttribute("name", "daMovimentiNew");
			parameterSB9.setAttribute("scope", "SERVICE_REQUEST");
			parameterSB9.setAttribute("type", "RELATIVE");
			parameterSB9.setAttribute("value", "daMovimentiNew");

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			selectCaptionSB.setAttribute(parameterSB5);
			selectCaptionSB.setAttribute(parameterSB6);
			selectCaptionSB.setAttribute(parameterSB7);
			selectCaptionSB.setAttribute(parameterSB8);
			selectCaptionSB.setAttribute(parameterSB9);
			captionsSB.setAttribute(selectCaptionSB);
			configSB.setAttribute("title", "Lista Aziende");
			configSB.setAttribute("rows", "20");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);
			// bottone di inserimento Nuova Unita
			SourceBean buttonsSB = new SourceBean("BUTTONS");
			SourceBean insertButtonSB = new SourceBean("INSERT_BUTTON");
			insertButtonSB.setAttribute("label", "Nuova Unita");
			insertButtonSB.setAttribute("confirm", "FALSE");
			SourceBean buttonParam1 = new SourceBean("PARAMETER");
			buttonParam1.setAttribute("name", "PAGE");
			buttonParam1.setAttribute("scope", "");
			buttonParam1.setAttribute("type", "ABSOLUTE");
			buttonParam1.setAttribute("value", "MovimentiUnitaAziendaPage");
			SourceBean buttonParam2 = new SourceBean("PARAMETER");
			buttonParam2.setAttribute("name", "CDNFUNZIONE");
			buttonParam2.setAttribute("scope", "SERVICE_REQUEST");
			buttonParam2.setAttribute("type", "RELATIVE");
			buttonParam2.setAttribute("value", "CDNFUNZIONE");
			SourceBean buttonParam3 = new SourceBean("PARAMETER");
			buttonParam3.setAttribute("name", "PRGMOVIMENTOAPP");
			buttonParam3.setAttribute("scope", "SERVICE_REQUEST");
			buttonParam3.setAttribute("type", "RELATIVE");
			buttonParam3.setAttribute("value", "PRGMOVIMENTOAPP");
			SourceBean buttonParam4 = new SourceBean("PARAMETER");
			buttonParam4.setAttribute("name", "PRGAZIENDA");
			buttonParam4.setAttribute("scope", "SERVICE_REQUEST");
			buttonParam4.setAttribute("type", "RELATIVE");
			buttonParam4.setAttribute("value", "PRGAZ");
			SourceBean buttonParam5 = new SourceBean("PARAMETER");
			buttonParam5.setAttribute("name", "CONTESTO");
			buttonParam5.setAttribute("scope", "SERVICE_REQUEST");
			buttonParam5.setAttribute("type", "RELATIVE");
			buttonParam5.setAttribute("value", "CONTESTO");
			SourceBean buttonParam6 = new SourceBean("PARAMETER");
			buttonParam6.setAttribute("name", "FUNZ_AGGIORNAMENTO");
			buttonParam6.setAttribute("scope", "SERVICE_REQUEST");
			buttonParam6.setAttribute("type", "RELATIVE");
			buttonParam6.setAttribute("value", "FUNZ_AGGIORNAMENTO");
			insertButtonSB.setAttribute(buttonParam1);
			insertButtonSB.setAttribute(buttonParam2);
			insertButtonSB.setAttribute(buttonParam3);
			insertButtonSB.setAttribute(buttonParam4);
			insertButtonSB.setAttribute(buttonParam5);
			insertButtonSB.setAttribute(buttonParam6);
			buttonsSB.setAttribute(insertButtonSB);
			configSB.setAttribute(buttonsSB);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ex);
		}
		return configSB;
	}
}