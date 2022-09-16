<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,it.eng.sil.security.*,it.eng.afExt.utils.*,it.eng.sil.util.*,java.math.*,java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%

    String titoloprog = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"titoloprog"); 
    String strtitolocorso = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"strtitolocorso");
    
    SourceBean rowcount = (SourceBean) serviceResponse.getAttribute("M_COUNT_ISCRICORSO.ROWS.ROW");	
	int     countIscr = SourceBeanUtils.getAttrInt(rowcount, "NUMISCR", 0);
	String  strCountIscr = SourceBeanUtils.getAttrStrNotNull(rowcount, "NUMISCR");
    
	//Profilatura
	boolean canInsert = true;
	boolean canDelete = true;
	boolean readOnlyStr = true;
	boolean canView = true;
	

	// Gestione
	
	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"PAGE");
    
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	PageAttribs attributi = new PageAttribs(user, _page);
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");

	} else {
		canInsert = attributi.containsButton("INSERISCI");		
	}
	
	// Lettura parametri dalla REQUEST
	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest,
			"cdnfunzione");
	String cdnfunzioneStr = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "cdnfunzione");
	String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(
			serviceRequest, "cdnLavoratore");

	String prgprogrammaq = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"prgprogrammaq");
	
	String prgcorso = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"prgcorso");
	
	String strtitolo = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"strtitolo");
	
	String titolo = "Gestione Iscritti Corsi del Programma"+strtitolo;
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
							"&cdnfunzione=<%=cdnfunzioneStr%>";
		setWindowLocation(url);
	}

	function indietroI() {
		    urlpage+="AdapterHTTP?PAGE=CorsiPage&";
		    urlpage+="prgprogrammaq=<%=Utils.notNull(prgprogrammaq)%>&";
		    urlpage+="prgcorso=<%=Utils.notNull(prgcorso)%>&";
		    urlpage+="titoloprog=<%=Utils.notNull(titoloprog)%>&";		
		    urlpage+="strtitolocorso=<%=Utils.notNull(strtitolocorso)%>&";				    		    
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

	 function verificaEsisteIscr() {
		  // Se la pagina è già in submit, ignoro questo nuovo invio!
		  if (<%=countIscr%> > 0 ){			  
			  go('PAGE=IscrittiCorsiPage&cdnFunzione=<%=cdnfunzioneStr%>&inserisciNuovo=1&prgprogrammaq=<%=prgprogrammaq%>&prgcorso=<%=prgcorso%>&titoloprog=<%=titoloprog%>&strtitolocorso=<%=strtitolocorso%>', 'FALSE');
		  }else{
			  alert('Non esistono lavoratori con azioni concordate che possono esser iscritti.');
			  return false; 
		  }
     }


</script>
</head>


<p class="titolo"> Programma <%=titoloprog%> -  Corso <%=strtitolocorso%> </p>
<p class="titolo"> Elenco Iscritti </p>
<body class="gestione" onload="rinfresca()">
	<input type="hidden" name="PAGE" value="CorsiPage">
	<input type="hidden" name="MODULE" value="" />
	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnfunzioneStr%>" />
	<input type="hidden" name="prgprogrammaq" value="<%=prgprogrammaq%>" />			
	<af:showErrors />
	<af:showMessages prefix="IscrittiCorsiListModule"/>   

    <af:list moduleName="IscrittiCorsiListModule"/>
	 <center>
	  <%	if (canInsert) { %>
          <input type="button" class="pulsanti"  name = "inserisciNuovo" value="Nuova Iscrizione" onclick= "verificaEsisteIscr()">    
    <%}%>
      <td align="center"><input type="button" onclick="go('PAGE=CorsiPage&cdnFunzione=<%=cdnfunzioneStr%>&prgcorso=<%=prgcorso%>', 'FALSE')" value = "Torna al Corso" class="pulsanti"></td>
     </center>  
<br/>
</body>
</html>
