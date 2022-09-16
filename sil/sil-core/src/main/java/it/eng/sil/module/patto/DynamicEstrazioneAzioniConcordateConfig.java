package it.eng.sil.module.patto;

import java.util.List;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

/**
 * @author Alessio Rolfini
 */
public class DynamicEstrazioneAzioniConcordateConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicEstrazioneAzioniConcordateConfig.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		// controlla possibilità chiusura multipla DID
		boolean canChiusuraDidMultipla = false;
		String labelEsito = "Esito";
		String numConfigurazioneChiusuraDidMultipla = (String) SourceBeanUtils.getAttrStrNotNull(response,
				"M_GETNUMCONFIGURAZIONECHIUSURADIDMULTIPLA.ROWS.ROW.NUM");
		if ("1".equals(numConfigurazioneChiusuraDidMultipla)) {
			canChiusuraDidMultipla = true;
		}

		boolean isContestuale = request.containsAttribute("CDNLAVORATORE");
		String prgprogrammaq = StringUtils.getAttributeStrNotNull(request, "prgprogrammaq");

		SessionContainer sessionContainer = this.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "AzioniConcordateListaPage");
		List sezioni = attributi.getSectionList();
		boolean canVisualizzaChiusuraDidDaProfilatura = sezioni.contains("CLOSEDID");

		canChiusuraDidMultipla = canChiusuraDidMultipla && "".equals(prgprogrammaq) && !isContestuale
				&& canVisualizzaChiusuraDidDaProfilatura;

		String configVoucher = "";
		if (response.containsAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM")) {
			configVoucher = response.getAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM").toString();
		} else {
			UtilsConfig objConfig = new UtilsConfig("VOUCHER");
			configVoucher = objConfig.getConfigurazioneDefault_Custom();
		}

		if (configVoucher.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {
			labelEsito = "Esito/&lt;BR&gt;stato TDA";
		}
		String labelAzione = "Azione";
		String regioneUmbriaGestioneAzioni = (String) response.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM");
		if (StringUtils.isFilledNoBlank(regioneUmbriaGestioneAzioni)
				&& regioneUmbriaGestioneAzioni.equalsIgnoreCase("1")) {
			labelAzione = "Misura";
		}

		SourceBean rowRegione = (SourceBean) response.getAttribute("M_GetCodRegione.ROWS.ROW");
		String regione = StringUtils.getAttributeStrNotNull(rowRegione, "codregione");

		String configXmlStrBase = "<CONFIG title=\"\" rows=\"15\">";

		configXmlStrBase += "<CHECKBOXES>"
				+ "	<CHECKBOX name=\"checkbox_azione_concordata\" label=\"\" refColumn=\"\" jsCheckBoxClick=\"\">"
				+ "	<CHECKBOXVALUE name=\"SELEZIONE\" scope=\"LOCAL\" type=\"RELATIVE\" value=\"pkAzioneConcordata\" />"
				+ "</CHECKBOX>" + "</CHECKBOXES>";

		configXmlStrBase += "	<COLUMNS>" + "		<COLUMN name=\"datColloquio\" label=\"Data Colloquio\" />"
				+ "		<COLUMN name=\"strCFCognomeNome\" label=\"CF Cognome Nome\" />";

		if ("8".equalsIgnoreCase(regione)) {
			configXmlStrBase += " <COLUMN name=\"azione\" label=\"Attivit&agrave;\" />";
		} else {
			configXmlStrBase += " <COLUMN name=\"azione\" label=\"" + labelAzione + "\" />";
		}
		configXmlStrBase += "		<COLUMN name=\"datStimata\" label=\"Data Stimata\" />"
				+ "		<COLUMN name=\"datadesionegg\" label=\"Data Adesione\" />"
				+ "		<COLUMN name=\"esitoDataSvolgimento\" label=\"" + labelEsito + "\" />"
				+ "		<COLUMN name=\"esitoFormazione\" label=\"Esito Formazione\" />"
				+ "		<COLUMN name=\"indirizzoDom\" label=\"Indirizzo dom.\" />"
				+ "		<COLUMN name=\"telefono\" label=\"Telefono\" />"
				+ "		<COLUMN name=\"cpiTitComp\" label=\"CPI comp/tit\" />";

		String configXmlStrFinale = "	</COLUMNS>" + "	<CAPTIONS> "
				+ " 		<SELECT_CAPTION image=\"\" confirm=\"FALSE\" label=\"Dettaglio\"> "
				+ "       <PARAMETER name=\"PAGE\"          type=\"ABSOLUTE\" value=\"PERCORSIPAGE\"/> "
				+ "       <PARAMETER name=\"PRGCOLLOQUIO\"      type=\"RELATIVE\" value=\"PRGCOLLOQUIO\" scope=\"LOCAL\"/> "
				+ "       <PARAMETER name=\"PRGPERCORSO\"      type=\"RELATIVE\" value=\"PRGPERCORSO\" scope=\"LOCAL\"/> "
				+ "       <PARAMETER name=\"PRGAZIONERAGG\"      type=\"RELATIVE\" value=\"PRGAZIONERAGG\" scope=\"LOCAL\"/> "
				+ " 		<PARAMETER name=\"CDNLAVORATORE\"     type=\"RELATIVE\" value=\"CDNLAVORATORE\" scope=\"LOCAL\"/> "
				+ "	    <PARAMETER name=\"CDNFUNZIONE\"     type=\"ABSOLUTE\" value=\"1\" scope=\"SERVICE_REQUEST\"/> "
				+ "       <PARAMETER name=\"DETTAGLIO\" type=\"ABSOLUTE\" value=\"TRUE\" scope=\"\"/> "
				+ "       <PARAMETER name=\"APRIDIV\" type=\"ABSOLUTE\" value=\"1\" scope=\"\"/> "
				+ "       <PARAMETER name=\"LISTAAZIONICONCORDATE\" type=\"ABSOLUTE\" value=\"TRUE\" scope=\"\"/> "
				+ " 		</SELECT_CAPTION> ";

		if (configVoucher.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {
			configXmlStrFinale = configXmlStrFinale
					+ "	<CAPTION image=\"../../img/scadenze.gif\" label=\"Dettaglio Voucher\" onClick=\"apriDettaglioVoucher\" hiddenColumn=\"prgvoucherhiddencolumn\"> "
					+ "	    <PARAMETER name=\"CDNFUNZIONE\"     type=\"ABSOLUTE\" value=\"1\" scope=\"SERVICE_REQUEST\"/> "
					+ " 	<PARAMETER name=\"CDNLAVORATORE\" type=\"RELATIVE\" value=\"CDNLAVORATORE\" scope=\"SERVICE_REQUEST\"/> "
					+ " 	<PARAMETER name=\"prgvoucher\" type=\"RELATIVE\" value=\"prgvoucher\" scope=\"LOCAL\"/> "
					+ " 	<PARAMETER name=\"prgvoucherhiddencolumn\" type=\"RELATIVE\" value=\"prgvoucherhiddencolumn\" scope=\"LOCAL\"/> "
					+ "       <PARAMETER name=\"PRGCOLLOQUIO\"      type=\"RELATIVE\" value=\"PRGCOLLOQUIO\" scope=\"LOCAL\"/> "
					+ "       <PARAMETER name=\"PRGPERCORSO\"      type=\"RELATIVE\" value=\"PRGPERCORSO\" scope=\"LOCAL\"/> "
					+ "	</CAPTION>" + "	</CAPTIONS>" + "</CONFIG> ";
		} else {
			configXmlStrFinale = configXmlStrFinale + "	</CAPTIONS>" + "</CONFIG> ";
		}

		String configXmlStr = configXmlStrBase.concat(configXmlStrFinale);
		SourceBean configSB = null;
		try {
			configSB = SourceBean.fromXMLString(configXmlStr);
		} catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getConfigSourceBean()", ex);

		}
		return configSB;
	}
}