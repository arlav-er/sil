<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%

  	String _page = (String) serviceRequest.getAttribute("PAGE");
	String  fromRicerca =StringUtils.getAttributeStrNotNull(serviceRequest,"fromRicerca");  
	String _funzione = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNFUNZIONE");

	//Profilatura ------------------------------------------------
 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

  	PageAttribs attributi = new PageAttribs(user, _page); 

  	boolean canInsert = false; 

  	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	} else {
  		canInsert = attributi.containsButton("INSERISCI"); 
  	}  
	
	String  prgAzienda = (String)serviceRequest.getAttribute("prgAzienda");
	String  prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest,"prgUnita");

	String prgRichSospensione = StringUtils.getAttributeStrNotNull(serviceRequest,"prgRichSospensione");
	
	//Servono per gestire il layout grafico
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
	  
%>

<html>
<head>
    <title></title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
    <af:linkScript path="../../js/"/>


<script language="JavaScript">

	var urlpage="AdapterHTTP?";	

	function tornaAllaRicerca() {
	  if (isInSubmit()) return;
	  urlpage+="PAGE=CMRichSospRicercaPage";
	  urlpage = urlpage +"&CDNFUNZIONE=<%=_funzione%>";
	  <% if (prgAzienda != null) { %>
 	    	urlpage = urlpage + "&prgUnita=<%=prgUnita%>";
 	    	urlpage = urlpage + "&prgAzienda=<%=prgAzienda%>";
 	  <%}%>
	 
	  setWindowLocation(urlpage);
	}

 
    <% if (prgAzienda!=null) {
       //Genera il Javascript che si occuperà di inserire i links nel footer
       		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
       }%>
	
</script>
<script language="Javascript">
	<% if (prgAzienda!=null && prgUnita!=null) { %>
		window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
	<% } %>	
</script>

</head>

<body class="gestione" onload="rinfresca()">

	<af:showErrors />

<af:form method="POST" action="AdapterHTTP" name="Frm1">
	<input type="hidden" name="prgRichSospensione" value="<%=prgRichSospensione%>">
	<% if (prgUnita!=null && !prgUnita.equals("")) { %>
		<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
	<% } %>

	<af:list moduleName="M_List_Rich_sosp" canInsert="<%= canInsert ? \"1\" : \"0\" %>" />	 
	      
	<table align="center">
		<tr>
		   <td align="center">
	          <input type="button" onclick="tornaAllaRicerca()" value = "Torna alla pagina di ricerca" class="pulsanti">	         
		   </td>
		</tr>		          		    
	</table>

</af:form>	
</body>
</html>
