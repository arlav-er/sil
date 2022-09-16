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

String titlePagina = "Elenco candidature istanze as online";

String prgIstanza = serviceRequest.getAttribute("PRGISTANZA").toString();

String codStatoElab = serviceRequest.getAttribute("CODMONOSTATO").toString();

String descrElab = serviceRequest.getAttribute("strDescElaborazione").toString();

String numCandidati = serviceRequest.getAttribute("NUMCANDIDATI").toString(); 
 
 String prgRichiestaAz = "";
 String prgAzienda = serviceRequest.getAttribute("prgAzienda").toString();
 String prgUnita =serviceRequest.getAttribute("prgUnita").toString();
 String codCpi =serviceRequest.getAttribute("codCpi").toString();
 String concatenaCpi =serviceRequest.getAttribute("ConcatenaCpi").toString();
 String prgTipoIncrocio ="";
  	
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
	prgRichiestaAz = infoRosa.getAttribute("PRGRICHIESTAAZ").toString();
	prgTipoIncrocio = infoRosa.getAttribute("PRGTIPOINCROCIO").toString();
	
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
 
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

	 
	  function tornaIndietro(){
			var s = "AdapterHTTP?PAGE=ASScaricoIstanzePage";
		  	s += "&PRGROSA=<%=prgRosa%>";   
		  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>";	
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
		  	<%if(codStatoElab.equalsIgnoreCase("T")){%>
		  		s += "&TIPO=VISUALIZZA"; 
		  	<%}else{%>
				s += "&PRGISTANZA=<%=Utils.notNull(prgIstanza)%>";
		  	<%}%>
	 
		  	setWindowLocation(s);
	  }
	  
	  function openDetail(PRGRISULTATO) {
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;

		    var s= "AdapterHTTP?PAGE=DettaglioCandidaturaPage";
		    s += "&PRGRISULTATO=" + PRGRISULTATO;
		    s += "&CDNFUNZIONE=<%= _funzione %>";

		     window.open (s, "Dettaglio Candidatura", 'toolbar=NO,statusbar=YES,height=450,width=1000,scrollbars=YES,resizable=YES');

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
   <br> <br> 
	 Stato istanza:  <b><%=descrElab%></b>	 - Numero totale candidati: <b><%=numCandidati %></b>	
  </p>
  <%out.print(htmlStreamBottom);%>
	<p>
	 	<font color="green">
	 		<af:showMessages prefix="M_GetDettaglioIstanze"/>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>
	 	 	<af:list moduleName="M_GetDettaglioIstanze" getBack="true" jsSelect="openDetail"/>
              <br>
          		<table align="center">
            		<tr align="center">
              		<td align="center">
                  	<input type="button" onClick="tornaIndietro();" class="pulsanti" value="Indietro" >
                	</td>
              		</tr>
              	</table>
	 
	</body>
	</html>