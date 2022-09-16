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


	    PageAttribs attributi = new PageAttribs(user, "CMListaAdesioniPage");
	
		String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	    String cdnFunzione=(String)serviceRequest.getAttribute("CDNFUNZIONE");
	    
	    String stampaPar = serviceRequest.getAttribute("STAMPA_PAR") == null ? "" : (String)serviceRequest.getAttribute("STAMPA_PAR");
	    String prioritaPar = serviceRequest.getAttribute("PRIORITA_PAR") == null ? "" : (String)serviceRequest.getAttribute("PRIORITA_PAR");
	    String prgRichiestaStampa = serviceRequest.getAttribute("PRGRICHIESTAAZ") == null ? "" : (String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
		String prgNominativoPriorita = serviceRequest.getAttribute("PRGNOMINATIVO") == null ? "" : (String)serviceRequest.getAttribute("PRGNOMINATIVO");
		
		boolean canInsert = false;
		boolean canDelete = false;
		boolean canSearch = false;
					    	    
	    canInsert=attributi.containsButton("inserisci");
	    canDelete=attributi.containsButton("cancella");
	    canSearch=attributi.containsButton("cerca");
	    
	    String strErrorCode = "";
	    String msgConferma = "";
	    boolean confirm = false;   
	    
	    String queryString = "cdnFunzione="+cdnFunzione+"&PAGE=CMListaAdesioniPage&cdnLavoratore="+cdnLavoratore;  
    
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
<title>Lista Adesioni</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" /> 
 <%@ include file="../documenti/_apriGestioneDoc.inc"%>
    
<script language="JavaScript">

function refresh() {

	rinfresca();
<%
	if (("STAMPA_PAR").equalsIgnoreCase(stampaPar)) {
%>
		stampa();		
<%	
	}
	else if (("PRIORITA_PAR").equalsIgnoreCase(prioritaPar)) {
%>
		popUpPriorita('&CDNLAVORATORE=<%=cdnLavoratore%>&PRGRICHIESTAAZ=<%=prgRichiestaStampa%>&PRGNOMINATIVO=<%=prgNominativoPriorita%>');		
<%	
	}
%>
	
}

function stampa(){
	apriGestioneGenericDoc('RPT_STAMPA_ADESIONE', '&CDNLAVORATORE=<%=cdnLavoratore%>&PRGRICHIESTAAZ=<%=prgRichiestaStampa%>&PRGNOMINATIVO=<%=prgNominativoPriorita%>&STRCHIAVETABELLA=<%=prgRichiestaStampa%>&STRCHIAVETABELLA2=null&CHECKDB=<%=true%>','CMADENUM');	
}
  
function popUpPriorita(parametri){
  var urlo = "AdapterHTTP?PAGE=CMPrioritaPage";
  urlo += parametri;
  var titolo = "PrioritaAdesione";  
  var w=800; var l=((screen.availWidth)-w)/2;
  var h=350; var t=((screen.availHeight)-h)/2;
  var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;
  var opened = window.open(urlo, titolo, feat);
  opened.focus();
}
</script>

</head>

<body onload="refresh()">
<af:form name="Frm1" method="POST" action="AdapterHTTP">
<input type="hidden" name="MODULE" value=""/>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>
<input type="hidden" name="PAGE" value="CMListaAdesioniPage"/>

<%
    if (cdnLavoratore != null) {         
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
	}
%>
	<center>
    	<font color="green">         
        	<af:showMessages prefix="M_CMAssociaRichiesteAdesione"/>                	
        	<af:showMessages prefix="M_CMDeleteRichiestaAdesione"/>    
        </font>
        <font color="red">
          <af:showErrors />
        </font>
	</center>
				
	<af:list moduleName="M_GetCMListaAdesioniLavoratore"  configProviderClass="it.eng.sil.module.ido.CMListaAdesioniLavoratoreConfig"/>
		    
    <center>
	<%if (canSearch && cdnLavoratore !=null){%>
         <input type="button" class="pulsanti"  name = "inserisciNuovo" value="Nuova adesione" onclick="go('PAGE=CMCercaRichiesteAdesionePage&cdnFunzione=<%=cdnFunzione%>&cdnLavoratore=<%=cdnLavoratore%>', 'FALSE')">
	<%}%>
	
    </center>
<br>       

</af:form>
</body>
</html>
<% } %>