<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
  	String _page = (String) serviceRequest.getAttribute("PAGE"); 
  	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
      
	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	else{


	    PageAttribs attributi = new PageAttribs(user, "ASListaAdesioniPage");
	
		String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	    String cdnFunzione=(String)serviceRequest.getAttribute("CDNFUNZIONE");
		
		boolean canInsert = false;
		boolean canDelete = false;
		boolean canSearch = false;
					    	    
	    canInsert=attributi.containsButton("inserisci");
	    canDelete=attributi.containsButton("cancella");
	    canSearch=attributi.containsButton("cerca");
	    
	    String strErrorCode = "";
	    String msgConferma = "";
	    boolean confirm = false;   
    
%>


<script>

function go(url, alertFlag) {      
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
  
  var _url = "AdapterHTTP?" + url;
  if (alertFlag == 'TRUE' ) {
    if (confirm('Confermi operazione')) {
      setWindowLocation(_url);
    }
  }
  else {
    setWindowLocation(_url);
  }
}

</script>

<html>
<head>
<title></title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
</head>

<body onload="rinfresca()">
<af:form name="Frm1" method="POST" action="AdapterHTTP">
<input type="hidden" name="MODULE" value=""/>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>
<input type="hidden" name="PAGE" value="ASListaAdesioniPage"/>

<%
    if (cdnLavoratore != null) {         
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
	}
%>
	<center>
    	<font color="green">         
        	<af:showMessages prefix="M_ASAssociaRichiesteAdesione"/>                	
        	<af:showMessages prefix="M_ASDeleteRichiestaAdesione"/>    
        	<af:showMessages prefix="M_ASUpdatePunteggiRichiestaAdesione"/>    
        </font>
        <font color="red">
          <af:showErrors />
        </font>
	</center>
				
	<af:list moduleName="M_GetASListaAdesioniLavoratore" configProviderClass="it.eng.sil.module.ido.ASListaAdesioniLavoratoreConfig" />
		    
    <center>
	<%if (canSearch && cdnLavoratore !=null){%>
         <input type="button" class="pulsanti"  name = "inserisciNuovo" value="Nuova adesione" onclick="go('PAGE=ASCercaRichiesteAdesionePage&cdnFunzione=<%=cdnFunzione%>&cdnLavoratore=<%=cdnLavoratore%>', 'FALSE')">
	<%}%>
	
    </center>
<br>       

</af:form>
</body>
</html>
<% } %>