/*
 * Creato il 22-nov-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

/*
 * Classe utilizzata per configurare la lista dei 
 * vengono settati:
 * 1) COLONNE
 * 2) PULSANTI
 * 3) CHECKBOX    
 * 
 * @author riccardi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CMProspMovimentiDispListConfigNew extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMProspMovimentiDispListConfigNew.class.getName());

	private String className = this.getClass().getName();

	public CMProspMovimentiDispListConfigNew() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		String pageQuestaLista = (String) request.getAttribute("PAGE");
		SessionContainer sessionContainer = this.getSessionContainer();
		SourceBean dett = (SourceBean) response.getAttribute("CMProspMovimentiDispListModuleNew");
		SourceBean row = (SourceBean) dett.getAttribute("ROWS");

		// attributi visibilita
		User user = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		PageAttribs attributi = new PageAttribs(user, "CMProspLavoratoriPage");

		try {
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");
			SourceBean col_5 = new SourceBean("COLUMN");
			SourceBean col_6 = new SourceBean("COLUMN");
			SourceBean col_7 = new SourceBean("COLUMN");
			SourceBean col_8 = new SourceBean("COLUMN");
			SourceBean col_9 = new SourceBean("COLUMN");
			SourceBean col_10 = new SourceBean("COLUMN");

			col_1.setAttribute("name", "checkLavRiserva");
			col_1.setAttribute("label", "Azione");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "cognomenomelav");
			col_2.setAttribute("label", "Lavoratore");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "codmonocategoria");
			col_3.setAttribute("label", "D/A");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "descrmansione");
			col_4.setAttribute("label", "Mansione");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "descrcontratto");
			col_5.setAttribute("label", "Contratto");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "datiniziomov");
			col_6.setAttribute("label", "Data Inizio");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "datfinemov");
			col_7.setAttribute("label", "Data Fine");
			colonneSB.setAttribute(col_7);

			col_8.setAttribute("name", "oreLavorate");
			col_8.setAttribute("label", "Ore Lavoratore");
			colonneSB.setAttribute(col_8);

			col_9.setAttribute("name", "oreTotali");
			col_9.setAttribute("label", "Ore Totali");
			colonneSB.setAttribute(col_9);
			
			col_10.setAttribute("name", "indirizzocompletoaz");
			col_10.setAttribute("label", "Indirizzo");			
			colonneSB.setAttribute(col_10);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			// CHECKBOXES INSERISCI MOVIMENTI DISP
			SourceBean checkSB = new SourceBean("CHECKBOXES");
			SourceBean checkEscl = new SourceBean("CHECKBOX");
			checkEscl.setAttribute("name", "CK_MOV");
			checkEscl.setAttribute("label", "");
			checkEscl.setAttribute("refColumn", "");
			checkEscl.setAttribute("jsCheckBoxClick", "");
			SourceBean checkEsclValue = new SourceBean("CHECKBOXVALUE");
			checkEsclValue.setAttribute("name", "PRG_CHECK");
			checkEsclValue.setAttribute("scope", "LOCAL");
			checkEsclValue.setAttribute("type", "RELATIVE");
			checkEsclValue.setAttribute("value", "PRG_CHECK");
			checkEscl.setAttribute(checkEsclValue);
			checkSB.setAttribute(checkEscl);

			// Preparo il <CONFIG>
			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "Movimenti disponibili");
			configSB.setAttribute(colonneSB);

			configSB.setAttribute(checkSB);
			configSB.setAttribute(captionsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}