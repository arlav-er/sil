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

  String  fromRicerca = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"fromRicerca");  

  String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
  String prgAzienda = (String) serviceRequest.getAttribute("PRGAZIENDA");
  String prgUnita = (String) serviceRequest.getAttribute("PRGUNITA");
  String PRGRICHESONERO = (String) serviceRequest.getAttribute("PRGRICHESONERO");
  
  //Profilatura ------------------------------------------------
 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

  PageAttribs attributi = new PageAttribs(user, _page); 

  boolean canInsert = false; 

  if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  } else {
  	canInsert = attributi.containsButton("INSERISCI"); 
  }
			
  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);
	  
%>



<html>
<head>
    <title>Lista richieste di esonero</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
    <af:linkScript path="../../js/"/>


<script language="JavaScript">

	var urlpage="AdapterHTTP?";	

	function tornaAllaRicerca() {
	  if (isInSubmit()) return;
	  urlpage+="PAGE=CMRichCompTerRicercaPage";
	  urlpage = urlpage + "&CDNFUNZIONE=<%=cdnFunzione%>";
	  urlpage = urlpage + "&prgAzienda=<%=prgAzienda%>";
	  setWindowLocation(urlpage);
	}

    function tornaLista(){
	  var f;
      f = "AdapterHTTP?PAGE=CMRichComTerListaPage";
      f = f + "&CDNFUNZIONE=<%=cdnFunzione%>";
	  f = f + "&prgAzienda=<%=prgAzienda%>";
	  f = f + "&PRGRICHESONERO=<%=PRGRICHESONERO%>";
			  
	  document.location = f;
    }	
	
</script>

<script language="Javascript">
	<% if (prgAzienda!=null && prgUnita!=null) { %>
		window.top.menu.caricaMenuAzienda(<%=cdnFunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
	<% } %>
</script>

</head>

<body class="gestione" onload="rinfresca()">
<% if (prgUnita!=null && !prgUnita.equals("")) { %>
		<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
<% } %>

	<af:showErrors />


		<%//testata.show(out);%>

	<af:list moduleName="M_List_Rich_ComTer" canInsert="<%= canInsert ? \"1\" : \"0\" %>" />
<%
	//if(!fromRicerca.equals("1")){
%>
	<table align="center">
		<tr>
		   <td align="center">
	          <input type="button" onclick="tornaAllaRicerca()" value = "Torna alla pagina di ricerca" class="pulsanti">	         
		   </td>
		</tr>		          		    
	</table>
<%  //}%>	
</body>
</html>
