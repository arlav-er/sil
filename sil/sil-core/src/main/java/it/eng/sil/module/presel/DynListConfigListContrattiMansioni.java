package it.eng.sil.module.presel;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

/**
 * @author Rolfini
 */
public class DynListConfigListContrattiMansioni extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynListConfigListContrattiMansioni.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;

		SessionContainer sessionContainer = this.getSessionContainer();

		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "ContrattiMansioniPage");

		boolean canDelete = attributi.containsButton("rimuovi");

		String configXmlStr = "<CONFIG pool=\"SIL_DATI\" title=\"\">" + "<QUERY statement=\"LOAD_CONTRATTI_MANSIONI\">"
				+ "<PARAMETER scope=\"SERVICE_REQUEST\" type=\"RELATIVE\" value=\"CDNLAVORATORE\"/>" + "</QUERY>";

		String checkBoxXmlStr = "<CHECKBOXES>"
				+ "<CHECKBOX name=\"CK_CANCCONTRATTO\" label=\"\" refColumn=\"\" jsCheckBoxClick=\"\">"
				+ "<CHECKBOXVALUE name=\"PRGDISCONTRATTO\" scope=\"LOCAL\" type=\"RELATIVE\" value=\"PRGDISCONTRATTO\"/>"
				+ "</CHECKBOX>" + "</CHECKBOXES>";

		String columnCaptionXmlStr = "<COLUMNS>" + "<COLUMN name=\"DESC_MANSIONE\" label=\"Mansione\" />"
				+ "<COLUMN name=\"DESC_CONTRATTO\" label=\"Tipo di rapporto\"/>"
				+ "<COLUMN name=\"STRINSERIMENTO\" label=\"Inserimento\"/>" + "</COLUMNS>" + "<CAPTIONS>"
				+ "<DELETE_CAPTION image=\"../../img/del.gif\" label=\"\" confirm=\"false\">"
				+ "<PARAMETER name=\"PRGDISCONTRATTO\" type=\"RELATIVE\" value=\"PRGDISCONTRATTO\" scope=\"LOCAL\"/>"
				+ "<PARAMETER name=\"DESC_CONTRATTO\" type=\"RELATIVE\" value=\"DESC_CONTRATTO\" scope=\"LOCAL\"/>"
				+ "<PARAMETER name=\"DESC_MANSIONE\" type=\"RELATIVE\" value=\"DESC_MANSIONE\" scope=\"LOCAL\"/>"
				+ "</DELETE_CAPTION>" + "</CAPTIONS>";

		if (canDelete) {
			configXmlStr += checkBoxXmlStr + columnCaptionXmlStr;
		} else {
			configXmlStr += columnCaptionXmlStr;
		}

		configXmlStr += "</CONFIG>";

		try {
			configSB = SourceBean.fromXMLString(configXmlStr);
		} catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}
}