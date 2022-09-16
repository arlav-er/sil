<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,it.eng.sil.security.*,it.eng.afExt.utils.*,it.eng.sil.util.*,java.math.*,java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	

	
	

	// Gestione
	String titolo = "Gestione Programmi";
	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"PAGE");

	String cdnfunzione = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "cdnfunzione");
	String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "cdnLavoratore");
	String prgAzienda = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "prgAzienda");
	String prgUnita = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"prgUnita");
	
	
	//Profilatura
		boolean canInsert = true;
		boolean readOnlyStr = true;
		
	    ProfileDataFilter filter = new ProfileDataFilter(user, _page);
		PageAttribs attributi = new PageAttribs(user, _page);
		if (!filter.canView()) {
		            response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");

		} else {
			canInsert = attributi.containsButton("INSERISCI");
		}


%>

<html>
<head>
<title><%=titolo%></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">

	function nuovo() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
		var url = "AdapterHTTP?PAGE=DettaglioXxxPage" +
							"&NUOVO=true" +
							"&cdnfunzione=<%=cdnfunzione%>";
		setWindowLocation(url);
	}

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


	 function indietroP() {
		  // Se la pagina è già in submit, ignoro questo nuovo invio!
		    var urlpage;
		    urlpage = "AdapterHTTP?";
		    urlpage+="PAGE=RicercaListaProgrammiPage&";		
		    urlpage+="cdnfunzione=<%=cdnfunzione%>";   
		    setWindowLocation(urlpage);
		}

	 
	 function RicercaProgramma() {
	        var urlpage="";
	        urlpage+="AdapterHTTP?";
	        urlpage+="cdnLavoratore="+cdnLav+"&";
	        urlpage+="PAGE=RicercaProgrammaPage";
	        opened = window.open(urlpage,"RicercaProgrammi", 'toolbar=0, scrollbars=1,width=800,height=400'); 
	    }


</script>
</head>

<body class="gestione" onload="rinfresca()">
	<input type="hidden" name="PAGE" value="ProgrammiPage">
	<input type="hidden" name="MODULE" value="" />
	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnfunzione%>" />		
	<af:showErrors />
	<af:showMessages prefix="ProgrammiListModule"/>
	<af:showMessages prefix="M_del_Programmi"/>      

    <af:list moduleName="ProgrammiListModule"/>
	       
    
     <center>     
     <%	if (canInsert) { %>    
          <input type="button" class="pulsanti"  name = "inserisciNuovo" value="Nuovo Programa" onclick= "go('PAGE=ProgrammiPage&cdnFunzione=<%=cdnfunzione%>&inserisciNuovo=1', 'FALSE')">
      <%}%>
          <input type="button" onclick="indietroP()" value = "Torna alla Ricerca" class="pulsanti">        
     </center>     
   
     
<br/>
</body>
</html>
