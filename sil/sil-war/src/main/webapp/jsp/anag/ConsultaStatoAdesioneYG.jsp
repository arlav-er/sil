<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page
	import="com.engiweb.framework.base.*,
                it.eng.sil.security.*,
                it.eng.afExt.utils.*,
                it.eng.sil.util.*,
                java.lang.*,
                java.text.*,
                java.util.*,
                it.eng.sil.util.*,
                java.math.BigDecimal"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%
	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user,
			_current_page);
	boolean notavailable = false;
	PageAttribs attributi = new PageAttribs(user, _current_page);

	String cdnLavoratore = Utils.notNull(serviceRequest
			.getAttribute("CDNLAVORATORE"));
	String StatoAttuale = "Stato adesione ottenuto: "
            + (String) serviceResponse
			.getAttribute("M_GetStatoAdesioneYg.YG_STATO");
	if (StatoAttuale == null)
		StatoAttuale = "Usare la funzione GET per ottenere lo stato attuale";
	if (serviceResponse
			.containsAttribute("M_SetStatoAdesioneYg.YG_STATO_SET")) {
		StatoAttuale = "Nuovo Stato Settato: "
				+ (String) serviceResponse
						.getAttribute("M_SetStatoAdesioneYg.YG_STATO_SET");
	}
%>

<%@page import="it.eng.sil.util.Utils"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/javascript">
function goGet(cdn) {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    
      urlpage = "AdapterHTTP?";
      urlpage+="PAGE=ConsultaStatoAdesioneYGPage&getYG=true&CDNLAVORATORE="+cdn;
    
      setWindowLocation(urlpage);
  }
function goSet(ns, cdn) {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    
      urlpage = "AdapterHTTP?";
      urlpage+="PAGE=ConsultaStatoAdesioneYGPage&setYG=true&nuovoStato="+ns+"&CDNLAVORATORE="+cdn;
     //alert(document.getElementByName('CsetStatoAdesione')[0].value);
     //getElementByName('CsetStatoAdesione')[0].value
      setWindowLocation(urlpage);
  }</script>



</head>

<body>
	<af:showErrors />
	<af:form method="POST" action="AdapterHTTP" name="Frm1">
		<table width="60%" align="center">
			<tr>
				<td class="campo"><input type="button" name="getYG"
					class="pulsanti" onclick="goGet(<%=cdnLavoratore%>)" value="GET" /></td>
				<td class="etichetta">Ottieni stato attuale</td>
			</tr>

			<tr>

				<td class="campo"><input type="submit" name="setYG"
					class="pulsanti" value="SET"
					onclick="goSet(document.getElementsByName('setStatoAdesione')[0].value,<%=cdnLavoratore%>)" /></td>

				<td class="campo"><af:comboBox name="setStatoAdesione"
						title="Capacita cogn" required="false"
						moduleName="M_ComboStatoAdesioneYg" classNameBase="input"
						addBlank="false" /></td>
			</tr>

			<tr>

				<td class="campo" colspan="2"><%=StatoAttuale%></td>

			</tr>

			

		</table>
	</af:form>

</body>
</html>
