<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String _page = (String) serviceRequest.getAttribute("PAGE");
String _funzione = (String) serviceRequest.getAttribute("cdnFunzione");
// NOTE: Attributi della pagina (pulsanti e link) 
PageAttribs attributi = new PageAttribs(user, _page);

ProfileDataFilter filter = new ProfileDataFilter(user, _page);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}

String obiettivoMisura = StringUtils.getAttributeStrNotNull(serviceRequest,"obiettivoMisura");
String modelloAttivo       = StringUtils.getAttributeStrNotNull(serviceRequest,"modelloAttivo");
String modelloCM   = StringUtils.getAttributeStrNotNull(serviceRequest,"modelloCM");

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
	<title>Ricerca Modelli Titoli di Acquisto</title>
   	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
   	<af:linkScript path="../../js/" />

  	<script language="Javascript">
   	<% 
    	//Genera il Javascript che si occuperà di inserire i links nel footer
   		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  	%>
  	
   </script>
</head>
    
<body class="gestione" onload="rinfresca();">
<p class="titolo">Ricerca Modelli Titoli  di Acquisto</p>
<%out.print(htmlStreamTop);%>
<af:form method="POST" action="AdapterHTTP" name="Frm1" >
	<table class="main">
    <tr>
    	<td class="etichetta">Obiettivo/Misura</td>
      	<td class="campo">
    		<af:comboBox classNameBase="input" name="obiettivoMisura" moduleName="M_AzioniModelloVoucherNoFlag" 
            		 required="false" addBlank="true" selectedValue="<%=obiettivoMisura%>" title="Obiettivo/Misura"/>
       	</td>
   	</tr>
   	 <tr>
    	<td class="etichetta">Attivo</td>
      	<td class="campo">
    		<af:comboBox name="modelloAttivo"
               size="1"
               title="Attivo"
               required="false"
               moduleName="M_ComboSiNo"
               selectedValue="<%=modelloAttivo%>"
               addBlank="true"
               classNameBase="input"
               blankValue=""/>
 
       	</td>
   	</tr>
     	<tr>
    	<td class="etichetta">TDA soggetti in CM o svantaggiati&nbsp;</td>
      	<td class="campo">
      		<af:comboBox 
	        				name="modelloCM"
                         	moduleName="M_ComboSiNo" 
                        	selectedValue="<%= modelloCM %>"
                        	addBlank="true" 
                        	blankValue=""
                        	required="false" classNameBase="input"
                        	onChange="fieldChanged()" 
                        	title="TDA soggetti in CM o svantaggiati"/>
       	</td>
   	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
	  	<td colspan="2" align="center">
	  	<input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
	  	&nbsp;&nbsp;
	  	<input type="reset" class="pulsanti" value="Annulla"/>
	  	</td>
	</tr>
	<input type="hidden" name="PAGE" value="ListaModelliTdaPage"/>
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
</table>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>