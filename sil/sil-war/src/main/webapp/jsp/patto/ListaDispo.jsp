<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                 it.eng.afExt.utils.StringUtils,
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
	}else{
	
	// NOTE: Attributi della pagina (pulsanti e link)
	PageAttribs attributi = new PageAttribs(user, "DispoDettaglioPage");
	boolean canDelete = attributi.containsButton("aggiorna");
	
	String filtriRicerca = "";
	
	//Filtri di ricerca
	
	String cf=StringUtils.getAttributeStrNotNull(serviceRequest,"cf");
  	String cognome=StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
  	String nome=StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
  	String codcpi=StringUtils.getAttributeStrNotNull(serviceRequest,"codcpi");
  	String AttiAperti=StringUtils.getAttributeStrNotNull(serviceRequest,"AttiAperti");      
  	String dataStipulaDa=StringUtils.getAttributeStrNotNull(serviceRequest,"dataStipulaDa");
  	String dataStipulaA=StringUtils.getAttributeStrNotNull(serviceRequest,"dataStipulaA");      
  	String dataColloquioDa=StringUtils.getAttributeStrNotNull(serviceRequest,"dataColloquioDa");
  	String dataColloquioA=StringUtils.getAttributeStrNotNull(serviceRequest,"dataColloquioA");      
  	String dataPattoDa=StringUtils.getAttributeStrNotNull(serviceRequest,"dataPattoDa");
  	String dataPattoA=StringUtils.getAttributeStrNotNull(serviceRequest,"dataPattoA");  
  	String dataFineAttoDa=StringUtils.getAttributeStrNotNull(serviceRequest,"dataFineAttoDa");
  	String dataFineAttoA=StringUtils.getAttributeStrNotNull(serviceRequest,"dataFineAttoA");
  	String MotivoFine=StringUtils.getAttributeStrNotNull(serviceRequest,"MotivoFine");    
  	String tipoRicerca=StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
  	String numDelibera=StringUtils.getAttributeStrNotNull(serviceRequest,"numDelibera");
  	String _funzione=serviceRequest.getAttribute("CDNFUNZIONE").toString();

%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
<script type="text/Javascript">
 
 // questa funzione non viene chiamata perchè il pulsante Torna alla ricerca è commentato 
  function tornaRicerca()
   {  
     if (isInSubmit()) return;
     
     url ="AdapterHTTP?PAGE=PattoRicercaDispoPage";
   	 url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&cf="+"<%=cf%>";
     url += "&cognome="+"<%=cognome%>";
     url += "&nome="+"<%=nome%>";
     url += "&tipoRicerca="+"<%=tipoRicerca%>"; 
     url += "&AttiAperti="+"<%=AttiAperti%>";
     url += "&dataStipulaDa="+"<%=dataStipulaDa%>";
     url += "&dataStipulaA="+"<%=dataStipulaA%>"; 
     url += "&dataColloquioDa="+"<%=dataColloquioDa%>";
     url += "&dataColloquioA="+"<%=dataColloquioA%>";
     url += "&dataPattoDa="+"<%=dataPattoDa%>";
     url += "&dataPattoA="+"<%=dataPattoA%>";
     url += "&codcpi="+"<%=codcpi%>";
     url += "&dataFineAttoDa="+"<%=dataFineAttoDa%>";
     url += "&dataFineAttoA="+"<%=dataFineAttoA%>";
   	 url += "&MotivoFine="+"<%=MotivoFine%>";
   	 url += "&numDelibera="+"<%=numDelibera%>";
     setWindowLocation(url);
  }
 
</script>
</head> 

<body onload="checkError();rinfresca()">
<af:error/>
<br/><p class="titolo">ESITO RICERCA LAVORATORI IMMEDIATAMENTE DISPONIBILI</p>
<%String attr   = null;
  String valore = null;
 
%>
     

<%	//cpiSelected = cpiSelected.substring(0, cpiSelected.indexOf("-")-1);
	
	if( cf!=null && !cf.equals("") ){
		filtriRicerca += "codice fiscale <strong>" +cf+ "</strong>; ";
	}
	if( cognome!=null && !cognome.equals("") ){
		filtriRicerca += "cognome <strong>" +cognome+ "</strong>; ";
	}
	if( nome!=null && !nome.equals("") ){
		filtriRicerca += "nome <strong>" +nome+ "</strong>; ";
	}
	if( AttiAperti!=null && !AttiAperti.equals("") ){
		filtriRicerca += "Atti aperti <strong>" +AttiAperti+ "</strong>; ";
	}
	if( ( cf!=null && !cf.equals("") ) || 
		( cognome!=null && !cognome.equals("") ) ||
		( nome!=null && !nome.equals("") ) ){
		filtriRicerca += "tipo di ricerca <strong>" +tipoRicerca+ "</strong>; ";
	}
	
	if( (dataStipulaDa!=null && !dataStipulaDa.equals("")) 
		|| (dataStipulaA!=null && !dataStipulaA.equals(""))){
		
		filtriRicerca += "data stipula ";
		if( dataStipulaDa!=null && !dataStipulaDa.equals("") ){
			filtriRicerca += "da <strong>" +dataStipulaDa+ "</strong>";
		}
		if( dataStipulaA!=null && !dataStipulaA.equals("") ){
			filtriRicerca += " fino a <strong>" +dataStipulaA+ "</strong>";
		}
		filtriRicerca +="; ";
	}
	if( (dataColloquioDa!=null && !dataColloquioDa.equals("")) 
		|| (dataColloquioA!=null && !dataColloquioA.equals(""))){
		
		filtriRicerca += "data scadenza colloquio di orientamento ";
		if( dataColloquioDa!=null && !dataColloquioDa.equals("") ){
			filtriRicerca += "da <strong>" +dataColloquioDa+ "</strong>";
		}
		if( dataColloquioA!=null && !dataColloquioA.equals("") ){
			filtriRicerca += " fino a <strong>" +dataColloquioA+ "</strong>";
		}
		filtriRicerca +="; ";
		
	}
	
	if( (dataPattoDa!=null && !dataPattoDa.equals("")) 
		|| (dataPattoA!=null && !dataPattoA.equals(""))){
		
		filtriRicerca += "data scadenza stipula patto ";
		if( dataPattoDa!=null && !dataPattoDa.equals("") ){
			filtriRicerca += "da <strong>" +dataPattoDa+ "</strong>";
		}
		if( dataPattoA!=null && !dataPattoA.equals("") ){
			filtriRicerca += " fino a <strong>" +dataPattoA+ "</strong>";
		}
		filtriRicerca +="; ";
		
	}
	if( (dataFineAttoDa!=null && !dataFineAttoDa.equals("")) 
		|| (dataFineAttoA!=null && !dataFineAttoA.equals(""))){
		
		filtriRicerca += "data fine atto  ";
		if( dataFineAttoDa!=null && !dataFineAttoDa.equals("") ){
			filtriRicerca += "da <strong>" +dataFineAttoDa+ "</strong>";
		}
		if( dataFineAttoA!=null && !dataFineAttoA.equals("") ){
			filtriRicerca += " fino a <strong>" +dataFineAttoA+ "</strong>";
		}
		filtriRicerca +="; ";
		
	}
	attr="cpiSelected";
    valore = (String) serviceRequest.getAttribute(attr);
	if( valore!=null && !valore.equals("") ){
		filtriRicerca += "centro per l'impiego competente <strong>" +valore+ "</strong>; ";
	}
	attr="didTelematica";
	valore = (String) serviceRequest.getAttribute(attr);
	if ( valore != null && valore.equals("S") ) {
		filtriRicerca += "did telematica <strong>Si</strong>; ";	
	}
	attr="didRiapertaTelematica";
	valore = (String) serviceRequest.getAttribute(attr);
	if ( valore != null && valore.equals("S") ) {
		filtriRicerca += "did riaperta telematicamente <strong>Si</strong>; ";	
	}
	attr="precarioDataDid";
	valore = (String) serviceRequest.getAttribute(attr);
	if ( valore != null && valore.equals("S") ) {
		filtriRicerca += "precario alla data did <strong>Si</strong>; ";	
	}
	attr="flgDidL68";
	valore = (String) serviceRequest.getAttribute(attr);
	if ( valore != null && valore.equals("S") ) {
		filtriRicerca += "DID fittizia finalizzata all'iscrizione L.68/99 <strong>Si</strong>; ";	
	}
	attr= "motSelected";
    valore = (String) serviceRequest.getAttribute(attr);
	if( valore!=null && !valore.equals("") ){
		filtriRicerca += "Motivo Fine atto <strong>" +valore+ "</strong>; ";
	}
	if( numDelibera!=null && !numDelibera.equals("") ){
		filtriRicerca += "Numero determina <strong>" + numDelibera + "</strong>; ";
	}
  

%>
<%-- <head>
  
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 <af:linkScript path="../../js/"/>

    
<body onload="checkError();rinfresca()">
<af:error/>
<br/>
<p class="titolo">ESITO RICERCA LAVORATORI IMMEDIATAMENTE DISPONIBILI</p> --%>
	<%
		if(filtriRicerca.length() > 0) {
			//Visualizza i filtri di ricerca utilizzati  			
  	%>
   	<table cellpadding="2" cellspacing="10" border="0" width="100%">
    	<tr>
    		<td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
    		<%
    			out.print( "Filtri di ricerca:<br/> ");
    			out.print( filtriRicerca );
    		%>
    		</td>
    	</tr>
    </table>
	<% } %>
	
	<af:list moduleName="ListaDispo" canDelete="<%= canDelete ? \"1\" : \"0\" %>" />
   <!-- <center><input class="pulsante" type = "button" name="torna" value="Torna" onclick="tornaRicerca()"/></center> -->

<table width="100%">

	   <tr><td class="campo2" colspan=3>N.B.:  Il pulsante di dettaglio permette all'operatore di entrare nel menù contestuale del lavoratore e di arrivare 
	   										   nella videata corrispondente alla visualizzazione/gestione degli atti amministrativi oggetto della ricerca. 
											   La videata mostrata é però sempre quella corrente anche se si parte da un'informazione storicizzata.
		</td></tr>	    
</table>

</body>
</html>
<%}%>

