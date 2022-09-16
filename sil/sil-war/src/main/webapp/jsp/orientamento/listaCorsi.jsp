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
	
	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"PAGE");

	// Lettura parametri dalla REQUEST
	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest,
			"cdnfunzione");
	String cdnfunzioneStr = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "cdnfunzione");
	String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "cdnLavoratore");

	String prgprogrammaq = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"prgprogrammaq");
	
	String strtitolo = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"strtitolo");
	
	String titoloprogb = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"titoloprog");
	
	String titoloprog = "";
	
	boolean canInsert = true;
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	PageAttribs attributi = new PageAttribs(user, _page);
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");

	} else {
		canInsert = attributi.containsButton("INSERISCI");
		
	}
	
	if("".equals(titoloprogb)){
		titoloprog = strtitolo;
	} else{
		titoloprog = titoloprogb;
	}
	
	serviceRequest.setAttribute("TitoloProgramma",titoloprog);
	
	
%>

<html>
<head>
<title><%=titoloprog%></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">

	function nuovo() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
		var url = "AdapterHTTP?PAGE=DettaglioXxxPage" +
							"&NUOVO=true" +
							"&cdnfunzione=<%=cdnfunzioneStr%>";
		setWindowLocation(url);
	}

	function indietroP() {
		    urlpage+="AdapterHTTP?PAGE=ProgrammiPage&";
		    urlpage+="prgprogrammaq=<%=Utils.notNull(prgprogrammaq)%>&";		   
		    setWindowLocation(urlpage);
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


</script>
</head>

<body class="gestione" onload="rinfresca()">
	<input type="hidden" name="PAGE" value="CorsiPage">
	<input type="hidden" name="MODULE" value="" />
	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnfunzioneStr%>" />	
	<input type="hidden" name="titoloprog" value="<%=titoloprog%>" />			
	<af:showErrors />
	<af:showMessages prefix="CorsiListModule"/>
	<af:showMessages prefix="M_DelCorso"/>
	  
	<p class="titolo">Gestione Corsi del Programma <%=titoloprog%></p>
    
    <af:list moduleName="CorsiListModule"/>
	 <center>      
    <%	if (canInsert) { %>
          <input type="button" class="pulsanti"  name = "inserisciNuovo" value="Nuovo Corso" onclick= "go('PAGE=CorsiPage&cdnFunzione=<%=cdnfunzioneStr%>&inserisciNuovo=1&prgprogrammaq=<%=prgprogrammaq%>&titoloprog=<%=titoloprog%>', 'FALSE')">    
    <%}%>
      <td align="center"><input type="button" onclick="go('PAGE=ProgrammiPage&cdnFunzione=<%=cdnfunzioneStr%>&prgprogrammaq=<%=prgprogrammaq%>', 'FALSE')" value = "Torna al Programma" class="pulsanti"></td>
     </center>  
    
<br/>
</body>
</html>
