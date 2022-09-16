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
  String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
  String prgAzienda = (String) serviceRequest.getAttribute("PRGAZIENDA");
  String prgUnita = (String) serviceRequest.getAttribute("PRGUNITA");  
  String PRGRICHCOMPUTO = (String) serviceRequest.getAttribute("PRGRICHCOMPUTO");
  
  String tipoMenu = (String) serviceRequest.getAttribute("tipoMenu");
  
  //Profilatura ------------------------------------------------
  PageAttribs attributi = new PageAttribs(user, _page);
	  
  boolean canInsert= attributi.containsButton("INSERISCI");
  boolean canDelete= attributi.containsButton("RIMUOVI");
			
  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);
  
  //Config
  SourceBean conf_b = Utils.getConfigValue("CMCOMP");
  String str_conf_CMCOMP = (String) conf_b.getAttribute("row.num");
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
	  urlpage+="PAGE=CMComputoRicPage";
	  urlpage = urlpage + "&CDNFUNZIONE=<%=cdnFunzione%>";
	  urlpage = urlpage + "&prgAzienda=<%=prgAzienda%>";
	  urlpage = urlpage + "&PRGUNITA=<%=prgUnita%>";
	  urlpage = urlpage + "&CDNLAVORATORE=<%=cdnLavoratore%>";
	  urlpage = urlpage + "&tipoMenu=<%=tipoMenu%>";
	  
	  setWindowLocation(urlpage);
	}

    function tornaLista(){
	  var f;
      f = " AdapterHTTP?PAGE=CMComputiListaPage ";
      f = f + "&CDNFUNZIONE=<%=cdnFunzione%>";
	  f = f + "&prgAzienda=<%=prgAzienda%>";
	  f = f + "&PRGUNITA=<%=prgUnita%>";
	  f = f + "&PRGRICHCOMPUTO=<%=PRGRICHCOMPUTO%>";
	  f = f + "&CDNLAVORATORE=<%=cdnLavoratore%>";
	  f = f + "&tipoMenu=<%=tipoMenu%>";
	  		  
	  document.location = f;
    }	
	
</script>

		
</head>

<body class="gestione" onload="rinfresca()">

	<af:showErrors />

	<%if("1".equals(str_conf_CMCOMP)){ %>
		
	<af:list moduleName="M_List_Rich_Computi_config_Provv" 
	       canDelete="<%= canDelete ? \"1\" : \"0\" %>" 
	       canInsert="<%= canInsert ? \"1\" : \"0\" %>"   />
	       
	<%} else { %>
	
	<af:list moduleName="M_List_Rich_Computi" 
	       canDelete="<%= canDelete ? \"1\" : \"0\" %>" 
	       canInsert="<%= canInsert ? \"1\" : \"0\" %>"   />
	       
	<%} %>
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
