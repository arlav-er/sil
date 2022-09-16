package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.tags.AbstractConfigProvider;

/**
 * @author Mauro Riccardi
 */
public class DynamicEstrazioneColloquiConfig extends AbstractConfigProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicEstrazioneColloquiConfig.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
	}

	public SourceBean getConfigSourceBean(SourceBean request, SourceBean response) {
		String labelDescrizione = "Programma";
		String configXmlStrBase = "<CONFIG title=\"\" rows=\"20\">" + "	<COLUMNS>"
				+ "		<COLUMN name=\"DESCRCPI\" label=\"CPI\" />"
				+ "		<COLUMN name=\"DATCOLLOQUIO\" label=\"Data&lt;BR&gt;programma\" />"
				+ "		<COLUMN name=\"STRCOGNOME\" label=\"Cognome\" />"
				+ "		<COLUMN name=\"STRNOME\" label=\"Nome\" />"
				+ "		<COLUMN name=\"STRCODICEFISCALE\" label=\"Codice Fiscale\" />"
				+ "		<COLUMN name=\"STRDESCRIZIONE\" label=\"" + labelDescrizione + "\" />"
				+ "		<COLUMN name=\"DATAFINEPROGRAMMA\" label=\"Data fine&lt;BR&gt;programma\" />";
		String canCIG = (String) response.getAttribute("M_Configurazione_CIG.ROWS.ROW.canCIG");
		if (canCIG.equals("true")) {
			configXmlStrBase = configXmlStrBase.concat("		<COLUMN name=\"DESCRISCRCIG\" label=\"Iscrizione\" />");
		}

		String configXmlStrFinale = "	</COLUMNS>" + "	<CAPTIONS> "
				+ " 	<SELECT_CAPTION image=\"../../img/detail.gif\" confirm=\"FALSE\" label=\"Dettaglio\"> "
				+ "       	<PARAMETER name=\"PAGE\" type=\"ABSOLUTE\" value=\"COLLOQUIOPAGE\"/> "
				+ "       	<PARAMETER name=\"PRGCOLLOQUIO\" type=\"RELATIVE\" value=\"PRGCOLLOQUIO\" scope=\"LOCAL\"/> "
				+ "       	<PARAMETER name=\"CDNLAVORATORE\" type=\"RELATIVE\" value=\"CDNLAVORATORE\" scope=\"LOCAL\"/> "
				+ "       	<PARAMETER name=\"CDNFUNZIONE\" type=\"RELATIVE\" value=\"CDNFUNZIONE\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<!-- parametri necessari per permettere di tornare indietro dalle pagine lincate --> "
				+ "       	<PARAMETER name=\"CF\" type=\"RELATIVE\" value=\"CF\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"CODCPI\" type=\"RELATIVE\" value=\"CODCPI\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"DESCRCPI\" type=\"RELATIVE\" value=\"DESCRCPI\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"COGNOME\" type=\"RELATIVE\" value=\"COGNOME\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"NOME\" type=\"RELATIVE\" value=\"NOME\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"CODSERVIZIO\" type=\"RELATIVE\" value=\"CODSERVIZIO\" scope=\"LOCAL\"/> "
				+ "       	<PARAMETER name=\"DATAINIZIO\" type=\"RELATIVE\" value=\"DATAINIZIO\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"DATAFINE\" type=\"RELATIVE\" value=\"DATAFINE\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<!-- parametro che serve per determinare se la ricerca e' avvenuta dal menu generale o da quello del lavoratore --> "
				+ "       	<PARAMETER name=\"ricerca_generale\" type=\"RELATIVE\" value=\"ricerca_generale\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<!-- POPUP EVIDENZE --> "
				+ "       	<PARAMETER name=\"APRI_EV\" type=\"ABSOLUTE\" value=\"1\"/> "
				+ "       	<PARAMETER name=\"PRGSPI\" type=\"RELATIVE\" value=\"PRGSPI\" scope=\"LOCAL\"/> "
				+ " 	</SELECT_CAPTION> " + " 	<DELETE_CAPTION confirm=\"TRUE\" label=\"Cancella\"> "
				+ "       	<PARAMETER name=\"PAGE\" type=\"ABSOLUTE\" value=\"LISTACOLLOQUIPAGE\"/> "
				+ "       	<PARAMETER name=\"MODULE\" type=\"ABSOLUTE\" value=\"M_DELETE_COLLOQUIO\"/> "
				+ "       	<PARAMETER name=\"PRGCOLLOQUIO\" type=\"RELATIVE\" value=\"PRGCOLLOQUIO\" scope=\"LOCAL\"/> "
				+ "       	<PARAMETER name=\"CDNFUNZIONE\" type=\"RELATIVE\" value=\"CDNFUNZIONE\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"CDNLAVORATORE\" type=\"RELATIVE\" value=\"CDNLAVORATORE\" scope=\"LOCAL\"/> "
				+ "       	<!-- parametri necessari per permettere di tornare indietro dalle pagine lincate --> "
				+ "       	<PARAMETER name=\"CF\" type=\"RELATIVE\" value=\"CF\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"CODCPI\" type=\"RELATIVE\" value=\"CODCPI\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"DESCRCPI\" type=\"RELATIVE\" value=\"DESCRCPI\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"COGNOME\" type=\"RELATIVE\" value=\"COGNOME\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"NOME\" type=\"RELATIVE\" value=\"NOME\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"CODSERVIZIO\" type=\"RELATIVE\" value=\"CODSERVIZIO\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"DATAINIZIO\" type=\"RELATIVE\" value=\"DATAINIZIO\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<PARAMETER name=\"DATAFINE\" type=\"RELATIVE\" value=\"DATAFINE\" scope=\"SERVICE_REQUEST\"/> "
				+ "       	<!-- parametro che serve per determinare se la ricerca e' avvenuta dal menu generale o da quello del lavoratore --> "
				+ "       	<PARAMETER name=\"ricerca_generale\" type=\"RELATIVE\" value=\"ricerca_generale\" scope=\"SERVICE_REQUEST\"/> "
				+ " 	</DELETE_CAPTION> " + "	</CAPTIONS>" + "</CONFIG> ";

		if (!request.containsAttribute("ricerca_generale") && request.getAttribute("ricerca_generale") == null) {
			configXmlStrBase = configXmlStrBase
					.concat("		<COLUMN name=\"FLGCONDIZIONALITA\" label=\"Sott. a condizionalit&agrave;\" />");
			configXmlStrBase = configXmlStrBase
					.concat("		<COLUMN name=\"flgCountAzioniNegativo\" label=\"Con esiti negativi\" />");
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