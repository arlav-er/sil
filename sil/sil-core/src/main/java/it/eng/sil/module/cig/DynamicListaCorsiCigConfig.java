package it.eng.sil.module.cig;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.Values;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class DynamicListaCorsiCigConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicListaCorsiCigConfig.class.getName());

	private String className = this.getClass().getName();

	public DynamicListaCorsiCigConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		SessionContainer sessionContainer = this.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "CigLavCorsiPage");

		// boolean canDeleteCat = attributi.containsButton("CANCELLA_CAT");
		// boolean canDeleteOrienter = attributi.containsButton("CANCELLA_ORIENTER");

		DataConnection dc = null;
		java.sql.Connection conn = null;
		try {
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			dc = dcm.getConnection(Values.DB_SIL_DATI);
			conn = dc.getInternalConnection();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile ottenere una connessione dal pool", e);

		}

		try {
			// COLUMNS
			SourceBean colonneSB = new SourceBean("COLUMNS");

			SourceBean col_0 = new SourceBean("COLUMN");
			SourceBean col_1 = new SourceBean("COLUMN");
			SourceBean col_2 = new SourceBean("COLUMN");
			SourceBean col_3 = new SourceBean("COLUMN");
			SourceBean col_4 = new SourceBean("COLUMN");
			SourceBean col_5 = new SourceBean("COLUMN");
			SourceBean col_6 = new SourceBean("COLUMN");
			SourceBean col_7 = new SourceBean("COLUMN");
			SourceBean col_8 = new SourceBean("COLUMN");

			col_0.setAttribute("name", "idCorso");
			col_0.setAttribute("label", "ID Corso");
			colonneSB.setAttribute(col_0);

			col_1.setAttribute("name", "descrizione");
			col_1.setAttribute("label", "Descrizione");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "ente");
			col_2.setAttribute("label", "Ente");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "referente");
			col_3.setAttribute("label", "Referente");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "tipo");
			col_4.setAttribute("label", "Tipo");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "periodoPrevisto");
			col_5.setAttribute("label", "Periodo previsto");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "periodoEffettivo");
			col_6.setAttribute("label", "Periodo effettivo");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "CigCollegata");
			col_7.setAttribute("label", "Iscrizione Collegata");
			colonneSB.setAttribute(col_7);

			col_8.setAttribute("name", "dataCancellazione");
			col_8.setAttribute("label", "Data cancellazione");
			colonneSB.setAttribute(col_8);

			// CAPTIONS
			SourceBean captionsSB = new SourceBean("CAPTIONS");

			SourceBean selectCaptionSB = new SourceBean("SELECT_CAPTION");
			selectCaptionSB.setAttribute("image", "");
			selectCaptionSB.setAttribute("label", "Dettaglio");
			selectCaptionSB.setAttribute("confirm", "false");

			SourceBean parameterSB2 = new SourceBean("PARAMETER");
			parameterSB2.setAttribute("name", "PRGCORSO");
			parameterSB2.setAttribute("scope", "LOCAL");
			parameterSB2.setAttribute("type", "RELATIVE");
			parameterSB2.setAttribute("value", "PRGCORSO");

			SourceBean parameterSB3 = new SourceBean("PARAMETER");
			parameterSB3.setAttribute("name", "ORIENTER");
			parameterSB3.setAttribute("scope", "LOCAL");
			parameterSB3.setAttribute("type", "RELATIVE");
			parameterSB3.setAttribute("value", "ORIENTER");

			selectCaptionSB.setAttribute(parameterSB2);
			selectCaptionSB.setAttribute(parameterSB3);
			captionsSB.setAttribute(selectCaptionSB);

			// if(canDeleteCat) {
			SourceBean deleteCaptionSB = new SourceBean("DELETE_CAPTION");
			deleteCaptionSB.setAttribute("image", "../../img/del.gif");
			deleteCaptionSB.setAttribute("label", "Cancella");
			deleteCaptionSB.setAttribute("confirm", "false");
			deleteCaptionSB.setAttribute("hiddenColumn", "isCatalogo");

			SourceBean deleteParameterSB2 = new SourceBean("PARAMETER");
			deleteParameterSB2.setAttribute("name", "prgCorso");
			deleteParameterSB2.setAttribute("scope", "LOCAL");
			deleteParameterSB2.setAttribute("type", "RELATIVE");
			deleteParameterSB2.setAttribute("value", "prgCorso");

			deleteCaptionSB.setAttribute(deleteParameterSB2);
			captionsSB.setAttribute(deleteCaptionSB);
			// }

			/*
			 * if(canDeleteOrienter) { SourceBean deleteCaptionSB1 = new SourceBean("DELETE_CAPTION");
			 * deleteCaptionSB1.setAttribute("image", "../../img/del.gif"); deleteCaptionSB1.setAttribute("label",
			 * "Cancella"); deleteCaptionSB1.setAttribute("confirm", "false");
			 * deleteCaptionSB1.setAttribute("hiddenColumn", "isOrienter");
			 * 
			 * SourceBean deleteParameterSB4 = new SourceBean("PARAMETER"); deleteParameterSB4.setAttribute("name",
			 * "prgCorso"); deleteParameterSB4.setAttribute("scope", "LOCAL"); deleteParameterSB4.setAttribute("type",
			 * "RELATIVE"); deleteParameterSB4.setAttribute("value", "prgCorso");
			 * 
			 * deleteCaptionSB1.setAttribute(deleteParameterSB4); captionsSB.setAttribute(deleteCaptionSB1); }
			 */

			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "");
			configSB.setAttribute("rows", "15");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(captionsSB);

			_logger.debug(configSB.toXML());

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}

		finally {
			Utils.releaseResources(dc, null, null);
		}

		return configSB;
	}
}
