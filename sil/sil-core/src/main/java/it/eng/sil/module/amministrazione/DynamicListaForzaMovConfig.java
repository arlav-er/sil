package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.Values;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class DynamicListaForzaMovConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicListaForzaMovConfig.class.getName());

	private String className = this.getClass().getName();

	/**
	 * Classe in disuso da aprile 2020: nuova funzionalita forzatura movimenti
	 */
	public DynamicListaForzaMovConfig() {
	}

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;
		SessionContainer sessionContainer = this.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "ListaForzaModMovPage");

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

			col_0.setAttribute("name", "codtipomov");
			col_0.setAttribute("label", "Tipo mov.");
			colonneSB.setAttribute(col_0);

			col_1.setAttribute("name", "codstatoatto");
			col_1.setAttribute("label", "Stato");
			colonneSB.setAttribute(col_1);

			col_2.setAttribute("name", "datamov");
			col_2.setAttribute("label", "Data inizio");
			colonneSB.setAttribute(col_2);

			col_3.setAttribute("name", "datfinemov");
			col_3.setAttribute("label", "Data fine");
			colonneSB.setAttribute(col_3);

			col_4.setAttribute("name", "datfinemoveffettiva");
			col_4.setAttribute("label", "Data fine effettiva");
			colonneSB.setAttribute(col_4);

			col_5.setAttribute("name", "codtipoass");
			col_5.setAttribute("label", "Tipo contratto");
			colonneSB.setAttribute(col_5);

			col_6.setAttribute("name", "codmonotempo");
			col_6.setAttribute("label", "Tempo");
			colonneSB.setAttribute(col_6);

			col_7.setAttribute("name", "ragsocaz");
			col_7.setAttribute("label", "Azienda");
			colonneSB.setAttribute(col_7);

			col_8.setAttribute("name", "codmonotipofine");
			col_8.setAttribute("label", "Cod. Tipo fine");
			colonneSB.setAttribute(col_8);

			SourceBean checkBoxCaption = new SourceBean("CHECKBOX");
			checkBoxCaption.setAttribute("name", "ckeckboxMovimenti");
			checkBoxCaption.setAttribute("label", "");
			checkBoxCaption.setAttribute("refColumn", "");

			SourceBean checkValue = new SourceBean("CHECKBOXVALUE");
			checkValue.setAttribute("name", "PRGMOVIMENTO");
			checkValue.setAttribute("scope", "LOCAL");
			checkValue.setAttribute("type", "relative");
			checkValue.setAttribute("value", "PRGMOVIMENTO");
			checkBoxCaption.setAttribute(checkValue);

			SourceBean objParam = new SourceBean("PARAMETER");
			objParam.setAttribute("name", "this");
			objParam.setAttribute("type", "ABSOLUTE");
			objParam.setAttribute("value", "this");
			objParam.setAttribute("scope", "");

			checkBoxCaption.setAttribute(objParam);

			SourceBean checkBoxes = new SourceBean("CHECKBOXES");
			checkBoxes.setAttribute(checkBoxCaption);

			configSB = new SourceBean("CONFIG");
			configSB.setAttribute("title", "");
			configSB.setAttribute("rows", "15");
			configSB.setAttribute(colonneSB);
			configSB.setAttribute(checkBoxes);

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
