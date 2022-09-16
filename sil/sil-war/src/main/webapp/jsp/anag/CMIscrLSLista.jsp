<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*,
                 it.eng.sil.util.*,
                 java.util.HashSet,
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*,
                 it.eng.sil.util.Sottosistema"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  PageAttribs attributi = new PageAttribs(user, _page);
  String _funzione =(String) serviceRequest.getAttribute("CDNFUNZIONE");
  
  boolean canNuovo=false;
  
  if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		
		canNuovo = attributi.containsButton("INSERISCI");
	}

  	String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
  	if(!cdnLavoratore.equals("")){
  		InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  		testata.setSkipLista(true);
  		testata.show(out);
  	}
  
  	
  	String nome = StringUtils.getAttributeStrNotNull(serviceRequest, "strNome");
  	String cognome = StringUtils.getAttributeStrNotNull(serviceRequest, "strCognome");
  	String cf = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
  	String datIscrAlboDa = StringUtils.getAttributeStrNotNull(serviceRequest, "datIscrAlboDa");
  	String datIscrAlboA = StringUtils.getAttributeStrNotNull(serviceRequest, "datIscrAlboA");
  	String datIscrListProvDa = StringUtils.getAttributeStrNotNull(serviceRequest, "datIscrListProvDa");
  	String datIscrListProvA = StringUtils.getAttributeStrNotNull(serviceRequest, "datIscrListProvA");
  	String tipoListaSpec = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoListaSpec");
  	String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "codCpi");
  	String tipoRicerca      = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
  	String PROVINCIA_ISCR      = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVINCIA_ISCR");
  	
  	
 
%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>


  
<SCRIPT language="Javascript" type="text/javascript">

function tornaRicerca(){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
     
     url="AdapterHTTP?PAGE=CMIscrLSRicercaPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&strNome="+"<%=nome%>";
     url += "&strCognome="+"<%=cognome%>";
     url += "&strCodiceFiscale="+"<%=cf%>";
     url += "&datIscrAlboDa="+"<%=datIscrAlboDa%>";
     url += "&datIscrAlboA="+"<%=datIscrAlboA%>";
     url += "&datIscrListProvA="+"<%=datIscrListProvA%>";
     url += "&datIscrListProvDa="+"<%=datIscrListProvDa%>";
     url += "&tipoListaSpec="+"<%=tipoListaSpec%>"; 
     url += "&cdnLavoratore="+"<%=cdnLavoratore%>";
     url += "&tipoRicerca="+"<%=tipoRicerca%>";
     url += "&codCpi="+"<%=codCpi%>"; 
     url += "&PROVINCIA_ISCR="+"<%=PROVINCIA_ISCR%>"; 
     setWindowLocation(url);
  }
    
// -->
</SCRIPT>
<script language="Javascript">
<% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
       
 %>
     
</script>
<script language="Javascript">
<% if (cdnLavoratore.equals("")) { %>
	if (window.top.menu != undefined){
  		window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
	}
<% } else {%>
	if (window.top.menu != undefined){
  		window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage&CDNLAVORATORE=<%=cdnLavoratore%>";
	}
<% } %>


</script>

</head>
<body class="gestione" onload="rinfresca();">

<br/><p class="titolo">Elenco Iscrizioni Liste Speciali</p>
<%	
if(cdnLavoratore.equals("")) {
	String attr   = null;
  	String valore = null;
  	String txtOut = "";
	attr= "strCognome";
    valore = (String) serviceRequest.getAttribute(attr);
   	if(valore != null && !valore.equals(""))
   	{
   		txtOut += "cognome <strong>"+ valore +"</strong>; ";
   	}
    attr= "strNome";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += "nome <strong>"+ valore +"</strong>; ";
    }
	attr= "strCodiceFiscale";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += "codice fiscale <strong>"+ valore +"</strong>; ";
    }
  	if(!serviceRequest.getAttribute("datIscrAlboDa").equals("") || !serviceRequest.getAttribute("datIscrAlboA").equals("")){
   		txtOut += "data iscr. albo naz.";
    } 
    attr= "datIscrAlboDa";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += " da <strong>"+ valore +"</strong> ";
    }
    attr= "datIscrAlboA";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += " fino a <strong>"+ valore +"</strong>; ";
    }
    if(!serviceRequest.getAttribute("datIscrListProvDa").equals("") || !serviceRequest.getAttribute("datIscrListProvA").equals("")){
   		txtOut += "data iscr.lista provinciale";
    } 
    attr= "datIscrListProvDa";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += " da <strong>"+ valore +"</strong> ";
    }
    attr= "datIscrListProvA";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += " fino a <strong>"+ valore +"</strong>; ";
    }
    
    attr= "descrTipoLShid";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += "Tipo lista speciale <strong>"+ valore +"</strong>; ";
    }
    attr= "descrCPIhid";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {	   
	   	txtOut += "Centro per l'impiego competente <strong>"+ valore +"</strong>; ";
    }
    
    
    attr= "provinciaTerrit";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {	   
	   	txtOut += "Ambito Territoriale <strong>"+ valore +"</strong>; ";
    }
 %>
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%}%>

<%}%>
<af:form name="Frm1" method="POST" action="AdapterHTTP">

<input type="hidden" name="PAGE" value="CMIscrLSDettPage"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/> 
<input type="hidden" name="nuovo" value="true" />


<af:list moduleName="M_ListSpecialiRicerca"/>
<table align="center">
	<% if(canNuovo && !cdnLavoratore.equals("")) {%>
	<tr>
		<td>	
			<center><input class="pulsante" type = "submit" name="inserisci" value="Nuova Iscrizione" onClick=""/></center>
		</td>
	</tr>
		<%} else if(cdnLavoratore.equals("")){ %>
	<tr>
		<td>	
			<input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onClick="tornaRicerca();"/>
		</td>
	</tr>
	<%}%>
</table>	
</af:form>
<br/>
</body>
</html>