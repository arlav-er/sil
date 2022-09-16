<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*
                  "%>
      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 // NOTE: Attributi della pagina (pulsanti e link) 
 String _page = (String) serviceRequest.getAttribute("PAGE");
 PageAttribs attributi = new PageAttribs(user,_page);
 boolean canPrintRosa        = attributi.containsButton("STAMPA_ROSA");
 boolean canPrintCVRosa      = attributi.containsButton("STAMPA_CV_SINGOLA_ROSA");
 boolean canPrintCVRosaCM    = attributi.containsButton("STAMPA_CV_SINGOLA_ROSA_CM"); 
 boolean canPrintSDRosa      = attributi.containsButton("STAMPA_SCHEDADISP_SINGOLA_ROSA");
 boolean canPrintSDRosaCM    = attributi.containsButton("STAMPA_SC_DISP_SINGOLA_ROSA_CM"); 
 boolean canPrintTutteRose   = attributi.containsButton("STAMPA_TUTTE_ROSE");
 boolean canPrintCVTutteRose = attributi.containsButton("STAMPA_CV_TUTTE_ROSE");
 boolean canPrintCVTutteRoseCM = attributi.containsButton("STAMPA_CV_TUTTE_ROSE_CM");
 boolean canPrintSDTutteRose = attributi.containsButton("STAMPA_SCHEDADISP_TUTTE_ROSE");
 boolean canPrintSDTutteRoseCM = attributi.containsButton("STAMPA_SC_DISP_TUTTE_ROSE_CM");
 
 //Servono per gestire il layout grafico
 String htmlStreamTop = StyleUtils.roundTopTable(true);
 String htmlStreamBottom = StyleUtils.roundBottomTable(true);

 //Recupero Cognome e nome dall sessione per verificare l'Operatore SPI
 String cognome = user.getCognome();
 String nome = user.getNome();	
	
			
 String prgRichiestaAz = (String) serviceRequest.getAttribute("prgRichiestaAz");
 String prgOrig = (String) serviceRequest.getAttribute("prgOrig");
 String prgAzienda     = (String) serviceRequest.getAttribute("prgAzienda");
 String prgUnita       = (String) serviceRequest.getAttribute("prgUnita");
 String prgRosa        = (String) serviceRequest.getAttribute("prgRosa");
 String queryString = null;
 SourceBean row= (SourceBean) serviceResponse.getAttribute("M_IDOGETANNONUMRICHIESTA.ROWS.ROW");
 BigDecimal numAnno      = (BigDecimal) row.getAttribute("NUMANNO");
 BigDecimal numRichiesta = (BigDecimal) row.getAttribute("NUMRICHIESTA");

 int gruppo = user.getCdnGruppo();
 int cdnut  = user.getCodut();
 
 String urlDiLista = "";
  if (sessionContainer!=null){
        String token = "_TOKEN_" + "MatchDettRosaPage";
        urlDiLista = (String)sessionContainer.getAttribute(token.toUpperCase());
        if (urlDiLista!=null){
          int pos1 = urlDiLista.indexOf("MODULE");
          int pos2 = urlDiLista.indexOf("&",pos1);
          String str1 = urlDiLista.substring (0,pos1);
          String str2 = urlDiLista.substring (pos2+1);
          urlDiLista = str1.concat(str2);
        }
  }
%>




<%!
String getQueryString(String qs) {    
	try {
	    StringTokenizer st = new StringTokenizer(qs, "&");
	    StringBuffer sb = new StringBuffer();
	    while (st.hasMoreTokens()) {
	        String par = (String)st.nextToken();
	        int i = par.indexOf("=");
	        if (i<0)continue;
	        sb.append(par.substring(0,i));
	        sb.append("%3D");
	        if (par.length()>i+1)
	            sb.append(par.substring(i+1));
	        sb.append("%26");
	    }
	    return sb.toString();
    }catch(Throwable t) {return null;}
}
%>




<html>
<head>
    <title>StampeRosaCandidatiIDO.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>


<script language="JavaScript">

<% queryString = (String)requestContainer.getAttribute("HTTP_REQUEST_QUERY_STRING");
   /*if (queryString==null) 
   { Enumeration params = request.getParameterNames();
     while(params.hasMoreElements())
     { String parName = (String) params.nextElement();
       String parValue = (String) request.getAttribute(parName);
       queryString += "&"+parName+"="+parValue;
     }
   }*/
%>

var HTTPrequest = "<%=getQueryString(queryString)%>";

function Indietro(){
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s= "AdapterHTTP?";
    s += "<%=urlDiLista%>";
    setWindowLocation(s);
}

function apriGestioneRosaIDO(rptAction,parametri,tipoDoc)
{
  var urlo = "AdapterHTTP?PAGE=GestioneRosaIDOPage";
  urlo += "&rptAction="+rptAction;
  urlo += parametri;
  urlo +="&tipoDoc="+tipoDoc; 
  urlo += "&QUERY_STRING="+HTTPrequest;
  //alert("&QUERY_STRING="+HTTPrequest);
  var titolo = "gestioneDoc";
  var w=750; var l=((screen.availWidth)-w)/2;
  var h=350; var t=((screen.availHeight)-h)/2;
  //alert("URL:: "+urlo);
  //var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  var feat = "status=NO,location=NO,toolbar=NO,scrollbars=NO,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;
  window.open(urlo, titolo, feat);
}
</script>

    
    <script language="JavaScript">
    
    function impostaNomeReport(nome){
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

        document.form1.REPORT.value = nome;
        doFormSubmit(document.form1);
    }
    
    </script>
</head>

<body class="gestione" onload="rinfresca()">

<af:form name="form1" method="POST" action="AdapterHTTP" >
<br/><br/><br/>

<%=htmlStreamTop%>
<table class="main">
<tr><td width="33%">&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
<tr><td colspan="3"><p class="titolo">Gestione stampe della rosa dei candidati</p></td></tr>
<tr><td colspan="3"><br/></td></tr>
<tr><td colspan="3"><br/></td></tr>

<!--tr><td rowspan="3" align="right"><img align="absmiddle" name="dettImg"
		alt="Visualizza la scheda relativa all'anagrafica professionale"
		src="../../img/Rosa.gif"></td></tr-->

<%
	String urlComune =   "&cdnGruppo=" + gruppo +
						 "&prgRichAzienda=" + prgRichiestaAz +
						 "&numAnno=" + numAnno.intValue() +
						 "&numRichiesta=" + numRichiesta.intValue() +
						 "&prgAzienda=" + prgAzienda +
						 "&prgUnita=" + prgUnita;
	String myClick;
%>
<%if (canPrintRosa) {%>
<tr>
  <td></td>
  <td align="left">&nbsp;
  		 <% myClick = "apriGestioneRosaIDO('RPT_IDO_ROSA','" + urlComune + "&prgRosa=" + prgRosa+ "&pagina=MatchListaRosePage&strChiaveTabella="+prgOrig+"&CDNUT="+ cdnut +"','ROS')"; %>
		 <%--
		 NOTA BENE: per le stampe delle Rose uso il riferimento alla "pagina"
		 chiamata "MatchListaRosePage" che ha il codice componente pari a "284".
		 --%>
  		 <a href="#" onClick="<%= myClick %>">
	     <img name="dettImg" alt="Stampa rosa candidati" src="../../img/text.gif"></a>&nbsp;
		 <a href="#" onClick="<%= myClick %>">
	     Rosa dei candidati</a>
  </td>
  <td></td>
</tr>
<%}%>
<%if (canPrintCVRosa) {%>
<tr>
  <td></td>
  <td align="left">&nbsp;
  		 <% myClick = "apriGestioneRosaIDO('RPT_CV_IDO_ROSA','" + urlComune + "&prgRosa=" + prgRosa+ "&mostraPerLavoratore=false&isCM=false','ROS')"; %>
  		 <a href="#" onClick="<%= myClick %>">
    	 <img name="dettImg" alt="Stampa rosa candidati" src="../../img/text.gif"></a>&nbsp;
  		 <a href="#" onClick="<%= myClick %>">
	     Curricula della rosa n. <%=prgRosa%></a>
  </td>
  <td></td>
</tr>
<%}%>
<%if (canPrintCVRosaCM) {%>
<tr>
  <td></td>
  <td align="left">&nbsp;
  		 <% myClick = "apriGestioneRosaIDO('RPT_CV_IDO_ROSA','" + urlComune + "&prgRosa=" + prgRosa+ "&mostraPerLavoratore=false&isCM=true','ROS')"; %>
  		 <a href="#" onClick="<%= myClick %>">
    	 <img name="dettImg" alt="Stampa rosa candidati CM" src="../../img/text.gif"></a>&nbsp;
  		 <a href="#" onClick="<%= myClick %>">
	     CM - Curricula della rosa n. <%=prgRosa%></a>
  </td>
  <td></td>
</tr>
<%}%>
<%if (canPrintSDRosa) {%>
<tr>
  <td></td>
  <td align="left">&nbsp;
  		 <% myClick = "apriGestioneRosaIDO('RPT_CV_IDO_ROSA','" + urlComune + "&prgRosa=" + prgRosa+ "&mostraPerLavoratore=true&isCM=false','ROS')"; %>
  		 <a href="#" onClick="<%= myClick %>">
    	 <img name="dettImg" alt="Stampa rosa candidati" src="../../img/text.gif"></a>&nbsp;
  		 <a href="#" onClick="<%= myClick %>">
	     Schede disponibilità della rosa n. <%=prgRosa%></a>
  </td>
  <td></td>
</tr>
<%}%>
<%if (canPrintSDRosaCM) {%>
<tr>
  <td></td>
  <td align="left">&nbsp;
  		 <% myClick = "apriGestioneRosaIDO('RPT_CV_IDO_ROSA','" + urlComune + "&prgRosa=" + prgRosa+ "&mostraPerLavoratore=true&isCM=true','ROS')"; %>
  		 <a href="#" onClick="<%= myClick %>">
    	 <img name="dettImg" alt="Stampa rosa candidati CM" src="../../img/text.gif"></a>&nbsp;
  		 <a href="#" onClick="<%= myClick %>">
	     CM - Schede disponibilità della rosa n. <%=prgRosa%></a>
  </td>
  <td></td>
</tr>
<%}%>
<tr><td>&nbsp;</td><tr>
<%if (canPrintTutteRose) {%>
<tr>
  <td></td>
  <td align="left">&nbsp;
  		 <% myClick = "apriGestioneRosaIDO('RPT_IDO_ROSA','" + urlComune + "&prgRosa=0&pagina=MatchListaRosePage&strChiaveTabella="+prgOrig+ "&CDNUT="+ cdnut +"','TROS')"; %>
  		 <a href="#" onClick="<%= myClick %>">
     	 <img name="dettImg" alt="Stampa rosa candidati" src="../../img/text.gif"></a>&nbsp;
  		 <a href="#" onClick="<%= myClick %>">
	     TUTTE le rose relative alla richiesta</a>
  </td>
  <td></td>
</tr>
<%}%>
<%if (canPrintCVTutteRose) {%>
<tr>
  <td></td>
  <td align="left">&nbsp;
  		 <% myClick = "apriGestioneRosaIDO('RPT_CV_IDO_ROSA','" + urlComune + "&prgRosa=0&mostraPerLavoratore=false&isCM=false','TROS')"; %>
  		 <a href="#" onClick="<%= myClick %>">
	     <img name="dettImg" alt="Stampa rosa candidati" src="../../img/text.gif"></a>&nbsp;
  		 <a href="#" onClick="<%= myClick %>">
	     Curricula di TUTTE  le rose associate alla richiesta</a>
  </td>
  <td></td>
</tr>
<%}%>
<%if (canPrintCVTutteRoseCM) {%>
<tr>
  <td></td>
  <td align="left">&nbsp;
  		 <% myClick = "apriGestioneRosaIDO('RPT_CV_IDO_ROSA','" + urlComune + "&prgRosa=0&mostraPerLavoratore=false&isCM=true','TROS')"; %>
  		 <a href="#" onClick="<%= myClick %>">
	     <img name="dettImg" alt="Stampa rosa candidati CM" src="../../img/text.gif"></a>&nbsp;
  		 <a href="#" onClick="<%= myClick %>">
	     CM - Curricula di TUTTE  le rose associate alla richiesta</a>
  </td>
  <td></td>
</tr>
<%}%>
<%if (canPrintSDTutteRose) {%>
<tr>
  <td></td>
  <td align="left">&nbsp;
  		 <% myClick = "apriGestioneRosaIDO('RPT_CV_IDO_ROSA','" + urlComune + "&prgRosa=0&mostraPerLavoratore=true&isCM=false','TROS')"; %>
  		 <a href="#" onClick="<%= myClick %>">
	     <img name="dettImg" alt="Stampa rosa candidati" src="../../img/text.gif"></a>&nbsp;
  		 <a href="#" onClick="<%= myClick %>">
	     Schede disponibilità di TUTTE le rose associate alla richiesta</a>
  </td>
  <td></td>
</tr>
<%}%>
<%if (canPrintSDTutteRoseCM) {%>
<tr>
  <td></td>
  <td align="left">&nbsp;
  		 <% myClick = "apriGestioneRosaIDO('RPT_CV_IDO_ROSA','" + urlComune + "&prgRosa=0&mostraPerLavoratore=true&isCM=true','TROS')"; %>
  		 <a href="#" onClick="<%= myClick %>">
	     <img name="dettImg" alt="Stampa rosa candidati CM" src="../../img/text.gif"></a>&nbsp;
  		 <a href="#" onClick="<%= myClick %>">
	     CM - Schede disponibilità di TUTTE le rose associate alla richiesta</a>
  </td>
  <td></td>
</tr>
<%}%>
<tr><td colspan="3"><br/></td></tr>
<tr><td colspan="3"><br/></td></tr>
</table>
<input type="button" class="pulsanti"  name="torna" value="Torna alla Rosa" onclick="Indietro()">
<%=htmlStreamBottom%>

</af:form>
</body>
</html>



