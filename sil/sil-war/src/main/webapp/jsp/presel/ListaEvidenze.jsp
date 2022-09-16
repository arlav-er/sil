<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                java.text.*, java.util.*,it.eng.sil.util.*, 
                it.eng.afExt.utils.*,
                java.math.*, it.eng.sil.security.* "%>

    
<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
// @author: Stefania Orioli
String fScad = StringUtils.getAttributeStrNotNull(serviceRequest, "SCAD");

String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");
InfCorrentiLav infCorrentiLav= new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
String _page = (String) serviceRequest.getAttribute("PAGE"); 
int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));
ProfileDataFilter filter = new ProfileDataFilter(user, _page);  
filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
boolean canView=filter.canViewLavoratore();
boolean canInsert = false;
boolean canDelete = false;
boolean canModify = false;
PageAttribs attributi = new PageAttribs(user, _page);

if (! canView){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}else{
	canInsert = attributi.containsButton("INSERISCI");
	canDelete = attributi.containsButton("CANCELLA");
	canModify = attributi.containsButton("AGGIORNA");
}
if ( !canModify && !canInsert && !canDelete ) {
	// do nothing
} else {
	boolean canEdit = filter.canEditLavoratore();
	if ( !canEdit ) {
		canModify = false;
		canInsert = false;
		canDelete = false;
	}
}


String msg = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String lp = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");



%>

<html>
<head>
  <title>Evidenze</title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <script type="text/javascript">
  <!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
  	 if(fScad.equals("")) {
     	//Genera il Javascript che si occuperà di inserire i links nel footer
       	attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
     }
  %>
    
  </script>
</head>
<body class="gestione" onLoad="rinfresca();">
<%
if(fScad.equals("")) {
	// Visualizzazione linguette solo se non apro la Page come POPUP
	infCorrentiLav.show(out);
	linguette.show(out);
}
%>

<font color="red">
  <af:showErrors/>
</font>
<font color="green">
  <af:showMessages prefix="MSalvaEvidenza"/>
  <af:showMessages prefix="MCancEvidenza"/>
</font>

<%if(fScad.equals("")) {%>
	<af:list moduleName="MListaEvidenze" skipNavigationButton="0" canDelete="<%=canDelete ? \"1\" : \"0\"%>" />
<%} else {%> 
	<p class="titolo">Lista Evidenze</p>
	<af:list moduleName="MListaEvidenze" canDelete="<%=canDelete ? \"1\" : \"0\"%>" />
<%}%>
<center>
<af:form method="POST" name="frm" action="AdapterHTTP" dontValidate="true">
<input type="hidden" name="PAGE" value="NuovaEvidenzaPage"/>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="CDNFUNZIONE"	value="<%=_funzione%>"/>
<input type="hidden" name="MSG" value="<%=msg%>"/>
<input type="hidden" name="LP" value="<%=lp%>"/>
<%if(!fScad.equals("")) {%>
	<input type="hidden" name="SCAD" value="N"/>
<%}%>

<%if(canInsert) {%>
	<input class="pulsanti" type="submit" name="inserisci" value="Nuova evidenza"/>
<%}%>

<%if(!fScad.equals("")) {%>
	<br><br>
	<input type="button" name="chiudi" value="Chiudi" class="pulsanti" onClick="window.close()"/>
<%}%>
</af:form>
</center>
</body>
</html>