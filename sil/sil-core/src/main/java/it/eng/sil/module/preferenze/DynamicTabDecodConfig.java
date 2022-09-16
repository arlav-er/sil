package it.eng.sil.module.preferenze;

import java.util.Iterator;
import java.util.List;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.tags.IDynamicConfigProvider;
import it.eng.sil.util.ga.db.Colonna;
import it.eng.sil.util.ga.db.Tabella;

/**
 * @author vuoto
 * 
 */

public class DynamicTabDecodConfig implements IDynamicConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynamicTabDecodConfig.class.getName());
	private String className = this.getClass().getName();

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {
		String tableName = ((String) request.getAttribute("TABLE_NAME")).toUpperCase();

		SourceBean configSB = null;

		/*
		 * SessionContainer sessionContainer = RequestContainer.getRequestContainer().getSessionContainer(); User user =
		 * (User) sessionContainer.getAttribute(User.USERID); PageAttribs attributi = new PageAttribs(user,
		 * "ListaTabDecodPage"); boolean canDelete = true;//attributi.containsButton("CANCELLA");
		 */
		Tabella tab = (Tabella) response.getAttribute("LISTATABDECOD.TABELLA");

		List pkColonne = new GenQuery(tab).retrievePKs();

		try {
			SourceBean colonneSB = new SourceBean("COLUMNS");
			List colonne = tab.getArrColonne();

			Colonna pkCol = null;
			for (Iterator iter = colonne.iterator(); iter.hasNext();) {
				Colonna colonna = (Colonna) iter.next();
				if (colonna.isPK() && (pkColonne.size() == 1)) {
					if (colonna.getNometipo().equals("NUMBER")) {
						continue;
					}
				}

				String commento = colonna.getCommento();
				String nome = colonna.getNome();

				if (nome.toUpperCase().startsWith("NUMKLO")) {
					continue;
				}

				if (commento.equals("")) {
					commento = nome;
				}
				SourceBean colSB = new SourceBean("COLUMN");
				colSB.setAttribute("name", nome);
				colSB.setAttribute("label", commento);
				colonneSB.setAttribute(colSB);
			}

			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "../../img/detail.gif");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB1 = new SourceBean("PARAMETER");
			parameterSB1.setAttribute("name", "PAGE");
			parameterSB1.setAttribute("type", "ABSOLUTE");
			parameterSB1.setAttribute("value", "DettaglioTabDecodPage");

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "CDNFUNZIONE");
			parameterSB3.setAttribute("type", "RELATIVE");
			parameterSB3.setAttribute("value", "CDNFUNZIONE");
			parameterSB3.setAttribute("scope", "SERVICE_REQUEST");

			SourceBean parameterSB5 = new SourceBean("PARAMETER");
			parameterSB5.setAttribute("name", "SHOW_KEYS");
			parameterSB5.setAttribute("type", "RELATIVE");
			parameterSB5.setAttribute("value", "SHOW_KEYS");
			parameterSB5.setAttribute("scope", "SERVICE_REQUEST");

			SourceBean parameterSB6 = new SourceBean("PARAMETER");
			parameterSB6.setAttribute("name", "SKIP_COMMENTS");
			parameterSB6.setAttribute("type", "RELATIVE");
			parameterSB6.setAttribute("value", "SKIP_COMMENTS");
			parameterSB6.setAttribute("scope", "SERVICE_REQUEST");

			SourceBean parameterSB4 = new SourceBean("PARAMETER");
			parameterSB4.setAttribute("name", "TABLE_NAME");
			parameterSB4.setAttribute("type", "ABSOLUTE");
			parameterSB4.setAttribute("value", tableName);

			selectCaptionSB.setAttribute(parameterSB1);
			selectCaptionSB.setAttribute(parameterSB3);
			selectCaptionSB.setAttribute(parameterSB4);
			selectCaptionSB.setAttribute(parameterSB5);
			selectCaptionSB.setAttribute(parameterSB6);

			for (Iterator iter = pkColonne.iterator(); iter.hasNext();) {
				pkCol = (Colonna) iter.next();
				SourceBean parameterSB2 = new SourceBean("PARAMETER");
				parameterSB2.setAttribute("name", pkCol.getNome());
				parameterSB2.setAttribute("type", "RELATIVE");
				parameterSB2.setAttribute("value", pkCol.getNome());
				parameterSB2.setAttribute("scope", "LOCAL");

				selectCaptionSB.setAttribute(parameterSB2);
			}

			// if(canDelete)
			{ // ---- Creo il pulsante per cancellare una righa della tabella
				// -----
				SourceBean deleteCaptionSB = new SourceBean("DELETE_CAPTION");
				deleteCaptionSB.setAttribute("image", "../../img/del.gif");
				deleteCaptionSB.setAttribute("label", "Cancella");
				deleteCaptionSB.setAttribute("confirm", "TRUE");

				parameterSB1 = new SourceBean("PARAMETER");
				parameterSB1.setAttribute("name", "PAGE");
				parameterSB1.setAttribute("type", "ABSOLUTE");
				parameterSB1.setAttribute("value", "ListaTabDecodPage");

				parameterSB3 = new SourceBean("PARAMETER");
				parameterSB3.setAttribute("name", "CDNFUNZIONE");
				parameterSB3.setAttribute("type", "RELATIVE");
				parameterSB3.setAttribute("value", "CDNFUNZIONE");
				parameterSB3.setAttribute("scope", "SERVICE_REQUEST");

				parameterSB4 = new SourceBean("PARAMETER");
				parameterSB4.setAttribute("name", "TABLE_NAME");
				parameterSB4.setAttribute("type", "ABSOLUTE");
				parameterSB4.setAttribute("value", tableName);

				parameterSB5 = new SourceBean("PARAMETER");
				parameterSB5.setAttribute("name", "SHOW_KEYS");
				parameterSB5.setAttribute("type", "RELATIVE");
				parameterSB5.setAttribute("value", "SHOW_KEYS");
				parameterSB5.setAttribute("scope", "SERVICE_REQUEST");

				parameterSB6 = new SourceBean("PARAMETER");
				parameterSB6.setAttribute("name", "SKIP_COMMENTS");
				parameterSB6.setAttribute("type", "RELATIVE");
				parameterSB6.setAttribute("value", "SKIP_COMMENTS");
				parameterSB6.setAttribute("scope", "SERVICE_REQUEST");

				SourceBean parameterSB7 = new SourceBean("PARAMETER");
				parameterSB7.setAttribute("name", "cancellaRiga");
				parameterSB7.setAttribute("type", "ABSOLUTE");
				parameterSB7.setAttribute("value", "true");
				parameterSB7.setAttribute("scope", "SERVICE_REQUEST");

				deleteCaptionSB.setAttribute(parameterSB1);
				deleteCaptionSB.setAttribute(parameterSB3);
				deleteCaptionSB.setAttribute(parameterSB4);
				deleteCaptionSB.setAttribute(parameterSB5);
				deleteCaptionSB.setAttribute(parameterSB6);
				deleteCaptionSB.setAttribute(parameterSB7);

				int i = 1;
				for (Iterator iter = pkColonne.iterator(); iter.hasNext(); i++) {
					pkCol = (Colonna) iter.next();
					SourceBean parameterSB2 = new SourceBean("PARAMETER");
					parameterSB2.setAttribute("name", pkCol.getNome());
					parameterSB2.setAttribute("type", "RELATIVE");
					parameterSB2.setAttribute("value", pkCol.getNome());
					parameterSB2.setAttribute("scope", "LOCAL");

					deleteCaptionSB.setAttribute(parameterSB2);
				}

				captionsSB.setAttribute(deleteCaptionSB);
			}

			captionsSB.setAttribute(selectCaptionSB);

			configSB = new SourceBean("CONFIG");
			String commento = tab.getCommento();
			if (commento.equals("")) {
				commento = tab.getNome();
			}
			configSB.setAttribute("title", commento);
			configSB.setAttribute("rows", "20");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}

}