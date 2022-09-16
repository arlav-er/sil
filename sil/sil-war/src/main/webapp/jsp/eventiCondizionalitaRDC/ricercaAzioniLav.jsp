<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 String _funzione=(String) serviceRequest.getAttribute("cdnFunzione");

String bottoneCerca =(String) serviceRequest.getAttribute("cerca");

String _page = "RicercaAzCondPage";
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
PageAttribs attributi = new PageAttribs(user, _page);
String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");


if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
} 

 String dataInizioProg	= StringUtils.getAttributeStrNotNull(serviceRequest, "dataInizioProg");
 String flagCondizionalita= StringUtils.getAttributeStrNotNull(serviceRequest, "CONDIZIONALITA");
 String flagNegativi= StringUtils.getAttributeStrNotNull(serviceRequest, "NEGATIVI");
 String flagAperti= StringUtils.getAttributeStrNotNull(serviceRequest, "APERTI");
 
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
   	 <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    	 
   	 <af:linkScript path="../../js/" />
   	 
   	 <style type="text/css">
td.etichetta{
vertical-align: top;
}
input.inputView{
 vertical-align: top;
}
td.campo {
 vertical-align: top;
 
}
</style>
   	 
</head>
<body class="gestione" onload="rinfresca()">
<br>
<p class="titolo">Ricerca programmi per condizionailtà</p>
<p align="center">
<af:form action="AdapterHTTP" method="POST"	name="Frm1">
	<%out.print(htmlStreamTop);%>

	
	<table class="main" border="0">
						<tr>
							<td class="etichetta" nowrap>Data inizio programma</td>
							<td class="campo" colspan="2"><af:textBox validateOnPost="true"
								type="date" name="dataInizioProg" title="Data inizio programma"
								value="<%=dataInizioProg%>" size="10" maxlength="10" /> 
							</td>
						</tr>
 
	 
			      		<tr>
			       				<td class="etichetta" nowrap>
			       					<input type="checkbox" name="APERTI"
									 <%=(flagAperti != null && flagAperti.equals("on")) ? "checked='checked'" : ""%> /> Solo aperti&nbsp;&nbsp;
			       				</td>
			       				<td class="etichetta" nowrap>
			       					<input type="checkbox" name="NEGATIVI"
							 		<%=(flagNegativi != null && flagNegativi.equals("on")) ? "checked='checked'" : ""%> /> Solo con esiti negativi&nbsp;&nbsp;
			       				</td>
			       				<td class="etichetta" nowrap>
			       					<input type="checkbox" name="CONDIZIONALITA"
										 <%=(flagCondizionalita != null && flagCondizionalita.equals("on")) ? "checked='checked'" : ""%> /> Solo con eventi di condizionalità
			       				</td>
			        	</tr>
			         	 <tr><td  colspan="2" align="center">&nbsp;</td></tr>
			         
	 
	    <tr>
	    	<td  colspan="3" align="center">
		    	<input class="pulsanti" type="submit" name="cerca" value="Applica Filtri" />
	        </td>
	    </tr>
	 </table>
	    <input type="hidden" name="PAGE" value="RicercaAzCondPage"/>
	    <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
	    <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
	<%out.print(htmlStreamBottom);%>
	</af:form>
    <af:list moduleName="M_ListaProgrammiCondizionalita"/>
      <center>
      		        <input type="button" class="pulsanti" value="Chiudi" onclick="window.close()"/>			
      </center>

	
	</body>
</html>

