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
public class DynListConfigListMobGeoMansioni extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynListConfigListMobGeoMansioni.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		SourceBean configSB = null;

		SessionContainer sessionContainer = this.getSessionContainer();

		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "MobilitaGeoPage");

		boolean canDelete = attributi.containsButton("rimuovi");

		String configXmlStr = "<CONFIG pool=\"SIL_DATI\" title=\"\">" + "<QUERY statement=\"LOAD_MOBGEO_MANSIONI\">"
				+ "<PARAMETER scope=\"SERVICE_REQUEST\" type=\"RELATIVE\" value=\"CDNLAVORATORE\"/>" + "</QUERY>";

		String checkBoxXmlStr = "<CHECKBOXES>"
				+ "<CHECKBOX name=\"CK_CANCMOB\" label=\"\" refColumn=\"\" jsCheckBoxClick=\"\">"
				+ "<CHECKBOXVALUE name=\"PRGMANSIONE\" scope=\"LOCAL\" type=\"RELATIVE\" value=\"PRGMANSIONE\"/>"
				+ "</CHECKBOX>" + "</CHECKBOXES>";

		String columnCaptionXmlStr = "<COLUMNS>" + "<COLUMN name=\"STRDESCRIZIONE\" label=\"Mansione\" />"
				+ "<COLUMN name=\"FLGDISPAUTO\" label=\"Uso Automobile\"/>"
				+ "<COLUMN name=\"FLGDISPMOTO\" label=\"Uso Motociclo\"/>"
				+ "<COLUMN name=\"FLGPENDOLARISMO\" label=\"Pendolarismo\"/>"
				+ "<COLUMN name=\"FLGMOBSETT\" label=\"Mob. Settimanale\"/>"
				+ "<COLUMN name=\"TRASFERTA\" label=\"Trasferta\"/>" + "</COLUMNS>" + "<CAPTIONS>"
				+ "<SELECT_CAPTION image=\"../../img/detail.gif\" label=\"\" confirm=\"false\">"
				+ "<PARAMETER name=\"PRGMANSIONE\" type=\"RELATIVE\" value=\"PRGMANSIONE\" scope=\"LOCAL\"/>"
				+ "</SELECT_CAPTION>" + "<DELETE_CAPTION image=\"../../img/del.gif\" label=\"\" confirm=\"false\">"
				+ "<PARAMETER name=\"PRGMANSIONE\" type=\"RELATIVE\" value=\"PRGMANSIONE\" scope=\"LOCAL\"/>"
				+ "<PARAMETER name=\"STRDESCRIZIONE\" type=\"RELATIVE\" value=\"STRDESCRIZIONE\" scope=\"LOCAL\"/>"
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