package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.tags.AbstractConfigProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.movimenti.constant.Properties;

/**
 * @author Mauro Riccardi
 */
public class DynamicEstrazionePercorsiConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicEstrazionePercorsiConfig.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {

		String config = "";
		String labelEsito = "Esito";
		if (response.containsAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM")) {
			config = response.getAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM").toString();
		}
		String labelMisura = "Obiettivo";
		String labelAzione = "Azione";
		String regioneUmbriaGestioneAzioni = (String) response.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM");
		if (StringUtils.isFilledNoBlank(regioneUmbriaGestioneAzioni)
				&& regioneUmbriaGestioneAzioni.equalsIgnoreCase("1")) {
			labelMisura = "Servizio";
			labelAzione = "Misura";
		}

		else {
			UtilsConfig objConfig = new UtilsConfig("VOUCHER");
			config = objConfig.getConfigurazioneDefault_Custom();
		}

		if (config.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {
			labelEsito = "Esito/&lt;BR&gt;stato TDA";
		}

		SourceBean rowRegione = (SourceBean) response.getAttribute("M_GetCodRegione.ROWS.ROW");
		String regione = StringUtils.getAttributeStrNotNull(rowRegione, "codregione");

		String configXmlStrBase = "<CONFIG title=\"Percorsi Inseriti\" rows=\"20\">" + "	<COLUMNS>"
				+ "		<COLUMN name=\"datColloquio\" label=\"Data Programma\" />"
				+ "		<COLUMN name=\"datStimata\" label=\"Data Stimata\" />"
				+ "		<COLUMN name=\"datadesionegg\" label=\"Data Adesione\" />";
		// TODO aaa
		if ("8".equalsIgnoreCase(regione)) {
			configXmlStrBase += "		<COLUMN name=\"azione\" label=\"Attivit&agrave;\" />"
					+ "		<COLUMN name=\"misura\" label=\"Prestazione\" />";
		} else {
			configXmlStrBase += "		<COLUMN name=\"azione\" label=\"" + labelAzione + "\" />"
					+ "		<COLUMN name=\"misura\" label=\"" + labelMisura + "\" />";
		}

		configXmlStrBase += "		<COLUMN name=\"esito\" label=\"" + labelEsito + "\" />"
				+ "		<COLUMN name=\"dateffettiva\" label=\"Data conclusione&lt;BR&gt;effettiva/prevista\" />"
				+ "		<COLUMN name=\"DATSTIPULA\" label=\"Data stipula&lt;BR&gt;patto\" />";

		String configXmlStrFinale = "	</COLUMNS>" + "	<CAPTIONS> "
				+ " 	<SELECT_CAPTION image=\"../../img/detail.gif\" confirm=\"FALSE\" label=\"Dettaglio\"> "
				+ "       	<PARAMETER name=\"\" type=\"RELATIVE\" value=\"prgpercorso\" scope=\"LOCAL\"/> "
				+ "       	<PARAMETER name=\"\" type=\"RELATIVE\" value=\"prgAzioneRagg\" scope=\"LOCAL\"/> "
				+ "       	<PARAMETER name=\"\" type=\"RELATIVE\" value=\"prgcolloquio\" scope=\"LOCAL\"/> "
				+ "       	<PARAMETER name=\"\" type=\"RELATIVE\" value=\"prgprogrammaq\" scope=\"LOCAL\"/> "
				+ " 	</SELECT_CAPTION> " + " 	<DELETE_CAPTION confirm=\"TRUE\" label=\"Cancella\"> "
				+ "       	<PARAMETER name=\"\" type=\"RELATIVE\" value=\"prgpercorso\" scope=\"LOCAL\"/> "
				+ "       	<PARAMETER name=\"\" type=\"RELATIVE\" value=\"azione\" scope=\"LOCAL\"/> "
				+ "       	<PARAMETER name=\"\" type=\"RELATIVE\" value=\"flgNonModificare\" scope=\"LOCAL\"/> "
				+ "       	<PARAMETER name=\"PRGLAVPATTOSCELTA\" type=\"RELATIVE\" value=\"PRGLAVPATTOSCELTA\" scope=\"LOCAL\"/> "
				+ " 	</DELETE_CAPTION> "
				+ " 	<CAPTION image=\"../../img/copiarich.gif\" onClick=\"apriDettaglioCondizionalita\" label=\"Eventi Condizionalità\" hiddenColumn=\"viewCondizionalita\"> "
				+ " 		<PARAMETER name=\"CDNFUNZIONE\" type=\"RELATIVE\" value=\"CDNFUNZIONE\" scope=\"SERVICE_REQUEST\"/> "
				+ "     	<PARAMETER name=\"CDNLAVORATORE\" type=\"RELATIVE\" value=\"CDNLAVORATORE\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"prgpercorso\" type=\"RELATIVE\" value=\"prgpercorso\" scope=\"LOCAL\"/> "
				+ "       	<PARAMETER name=\"prgcolloquio\" type=\"RELATIVE\" value=\"prgcolloquio\" scope=\"LOCAL\"/> "
				+ " 	</CAPTION> ";

		if (config.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {
			configXmlStrFinale = configXmlStrFinale
					+ "	<CAPTION image=\"../../img/scadenze.gif\" label=\"Dettaglio Voucher\" onClick=\"apriDettaglioVoucher\" hiddenColumn=\"prgvoucherhiddencolumn\"> "
					+ "		<PARAMETER name=\"CDNFUNZIONE\" type=\"RELATIVE\" value=\"CDNFUNZIONE\" scope=\"SERVICE_REQUEST\"/> "
					+ " 	<PARAMETER name=\"CDNLAVORATORE\" type=\"RELATIVE\" value=\"CDNLAVORATORE\" scope=\"SERVICE_REQUEST\"/> "
					+ " 	<PARAMETER name=\"prgvoucher\" type=\"RELATIVE\" value=\"prgvoucher\" scope=\"LOCAL\"/> "
					+ " 	<PARAMETER name=\"prgvoucherhiddencolumn\" type=\"RELATIVE\" value=\"prgvoucherhiddencolumn\" scope=\"LOCAL\"/> "
					+ " 	<PARAMETER name=\"prgpercorso\" type=\"RELATIVE\" value=\"prgpercorso\" scope=\"LOCAL\"/> "
					+ "     <PARAMETER name=\"prgcolloquio\" type=\"RELATIVE\" value=\"prgcolloquio\" scope=\"LOCAL\"/> "
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