<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page
	import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.module.anag.profiloLavoratore.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.lang.*,
                  java.text.*"%>
 

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
boolean canModify = false;
String readonly = String.valueOf( ! canModify );
String _page = (String) serviceRequest.getAttribute("PAGE");

String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
 PageAttribs attributi = new PageAttribs(user,_page);

ProfileDataFilter filter = new ProfileDataFilter(user, _page);

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
} 

String titlePagina = "Elenco istanze as online";
String titleModule ="";

if(serviceResponse.containsAttribute("M_ASSCARICOISTANZE.ESITO") || (
			serviceRequest.containsAttribute("TIPO") && serviceRequest.getAttribute("TIPO").toString().equalsIgnoreCase("VISUALIZZA")
		)){
	titleModule ="Elenco istanze";
}

boolean isInCorso = false;
String prgIstanza = null;
String moduleName = "M_GetRisultatiIstOnline";
if(serviceRequest.containsAttribute("PRGISTANZA") ||  serviceResponse.containsAttribute("M_ASSCARICOISTANZE.PRGISTANZA")){
	moduleName = "M_GetRisultatiIstOnline_PrgIst";
	titleModule="Istanza in elaborazione...";
	SourceBean sb = (SourceBean) serviceResponse.getAttribute("M_GETRISULTATIISTONLINE_PRGIST");
	String statoCurr = (String) sb.getAttribute("ROWS.ROW.CODMONOSTATO");
	if(statoCurr.equalsIgnoreCase("T")){
		titleModule="Elenco istanze";
		isInCorso = false;
	}else{
		isInCorso = true;
	}
	
	if(serviceResponse.containsAttribute("M_ASSCARICOISTANZE.PRGISTANZA")){
		prgIstanza = serviceResponse.getAttribute("M_ASSCARICOISTANZE.PRGISTANZA").toString();
	}else{
		prgIstanza = serviceRequest.getAttribute("PRGISTANZA").toString();
	}
	
}
if(serviceRequest.containsAttribute("PAGE_STORICO")){
	moduleName="M_StoricoGetRisultatiIstOnline";
}

 String prgRichiestaAz = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
 String prgAzienda = serviceRequest.getAttribute("prgAzienda").toString();
 String prgUnita =serviceRequest.getAttribute("prgUnita").toString();
 String codCpi =StringUtils.getAttributeStrNotNull(serviceRequest,"codCpi");
 String concatenaCpi =StringUtils.getAttributeStrNotNull(serviceRequest,"ConcatenaCpi");
 String prgTipoIncrocio =serviceRequest.getAttribute("PRGTIPOINCROCIO").toString();
  	
String numRichiesta="", numAnno="",prgTipoRosa="", tipoIncrocio="",
	prgIncrocio="", tipoRosa="",prgAlternativa="", 
	 strAlternativa="",prgOrig="",   numRichiestaOrig="" ;

String prgRosa = "";

SourceBean infoRosa = (SourceBean) serviceResponse.getAttribute("ASDettaglioGraduatoria.row");
	prgRosa = infoRosa.getAttribute("PRGROSA").toString();
	numRichiestaOrig = StringUtils.getAttributeStrNotNull(infoRosa,"NUMRICHIESTAORIG");
	numAnno = StringUtils.getAttributeStrNotNull(infoRosa, "NUMANNO");
	tipoIncrocio = StringUtils.getAttributeStrNotNull(infoRosa,"TIPOINCROCIO");
	tipoRosa = StringUtils.getAttributeStrNotNull(infoRosa, "TIPOROSA");
	prgAlternativa = StringUtils.getAttributeStrNotNull(infoRosa,"PRGALTERNATIVA");
	strAlternativa = "";
	if (!prgAlternativa.equals("")) {
		strAlternativa = "Profilo n. " + prgAlternativa;
	}
	prgOrig = StringUtils.getAttributeStrNotNull(infoRosa,"PRGRICHIESTAORIG");

	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
 	String pageIndietro = "ASMatchDettGraduatoriaPage";
 	String moduleIndietro = "ASCandidatiGraduatoria";
 	String codMonoTipoGradCm = null;
 	if(serviceRequest.containsAttribute("PAGE_STORICO")){
 		pageIndietro = serviceRequest.getAttribute("PAGE_STORICO").toString();
 		moduleIndietro = serviceRequest.getAttribute("MODULE_STORICO").toString();
 		codMonoTipoGradCm =StringUtils.getAttributeStrNotNull(serviceRequest,"codmonotipogradcm");
 	}
%>
 
	<html>
	<head>
	<title><%=titlePagina %></title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 	
	<af:linkScript path="../../js/" />
	 <script language="Javascript">
	 
	  function openRich_PopUP(prgRich) {
		     window.open ("AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=" +prgRich +"&CDNFUNZIONE=<%=_funzione%>&POPUP=1", "DettaglioSintetico", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
		  }
	  
	  function reloadRisultati(){
		  var s = "AdapterHTTP?PAGE=ASScaricoIstanzePage";
		  	s += "&PRGROSA=<%=prgRosa%>";   
		  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>";	
		  	s += "&CALC_POSIZIONE=ASCalcolaPosizioneModule";
		  	s += "&MODULE=ASCandidatiGraduatoria";
		  	s += "&CDNFUNZIONE=<%=_funzione%>";  
		  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
		  	s += "&PRGAZIENDA=<%=prgAzienda%>";
	 	  	s += "&PRGUNITA=<%=prgUnita%>";
	        s += "&PRGINCROCIO=<%=prgIncrocio%>";  
		  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>";
		  	s += "&codCpi=<%=codCpi%>";
		  	s += "&ConcatenaCpi=<%=concatenaCpi%>"; 
		  	s += "&numrichiesta=<%=numRichiestaOrig%>";
		  	s += "&numanno=<%=numAnno%>"; 
		  	s += "&PRGISTANZA=<%=Utils.notNull(prgIstanza)%>";

		  	setWindowLocation(s);
	  }

	  function tornaIndietro(){
			var s = "AdapterHTTP?PAGE=<%=pageIndietro%>";
		  	s += "&PRGROSA=<%=prgRosa%>";   
		  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>";	
		  	s += "&CALC_POSIZIONE=ASCalcolaPosizioneModule";
		  	s += "&MODULE=<%=moduleIndietro%>";
		  	s += "&CDNFUNZIONE=<%=_funzione%>";  
		  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
		  	s += "&PRGAZIENDA=<%=prgAzienda%>";
	 	  	s += "&PRGUNITA=<%=prgUnita%>";
	        s += "&PRGINCROCIO=<%=prgIncrocio%>";  
		  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>";
		  	s += "&codCpi=<%=codCpi%>";
		  	s += "&ConcatenaCpi=<%=concatenaCpi%>"; 
		  	s += "&numrichiesta=<%=numRichiestaOrig%>";
		  	s += "&numanno=<%=numAnno%>"; 
		  	s += "&PRGISTANZA=<%=Utils.notNull(prgIstanza)%>";
		  	
		  	<%if(serviceRequest.containsAttribute("PAGE_STORICO")){%>
			  	s += "&codmonotipogradcm=<%=Utils.notNull(codMonoTipoGradCm)%>";
		  	<%}%>
 
		  	setWindowLocation(s);
	  }
	 </script>
	
	</head>
	<body class="gestione">
<br>
  <%out.print(htmlStreamTop);%>
  <p class="info_lav">
  
  Tipo di Graduatoria <b><%=tipoIncrocio%></b> - Stato della Graduatoria <b><%=tipoRosa%></b> <br>  
  <%
  if(!prgAlternativa.equals("")) {
  %>
  Alternativa utilizzata <b><%=strAlternativa%></b> <br>
  <%
  }
  %> 
  <br> 
  <a class="info_lav" href="#" onClick="openRich_PopUP('<%=prgOrig%>')"><img src="../../img/info_soggetto.gif" alt="Dettaglio"/></a>&nbsp;
  Richiesta num. <b><%=numRichiestaOrig%>/<%=numAnno%></b>
 
  </p>
  <%out.print(htmlStreamBottom);%>
	<p>
	 	<font color="green">
	 		<af:showMessages prefix="M_GetRisultatiIstOnline"/>
	 		<af:showMessages prefix="M_GetRisultatiIstOnline_PrgIst"/>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p><H2><%=titleModule %></H2>
	
	 	 	<af:list moduleName="<%=moduleName %>" getBack="true"/>
              <br>
          		<table align="center">
            		<tr align="center">
              		<td align="center">
                  	<input type="button" onClick="tornaIndietro();" class="pulsanti" value="Indietro" >
                	</td>
              		</tr>
              	</table>
              	<%if(isInCorso){ %>
              		<table align="center">
            		<tr align="center">
              		<td align="center">
                  	<input type="button" onClick="reloadRisultati();" class="pulsanti" value="Ricarica" >
                	</td>
              		</tr>
              	</table>
              	<%} %>
	 
	</body>
	</html>