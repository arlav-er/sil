<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  java.text.SimpleDateFormat,
                  java.text.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  it.eng.sil.util.GiorniNL,
                  it.eng.sil.module.agenda.*,
                  java.util.*,
                  java.math.*,
                  it.eng.sil.security.User,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
 
  int _funzione=0;
  InfCorrentiLav infCorrentiLav= null;

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page); 
  
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);

  PageAttribs attributi = new PageAttribs(user, _page); 
	
  String numAnnoRedditoCM = "";	  
  String punteggio = ""; 
  String punteggioIniziale = "";
  String punteggioAnzianita = "";
  String punteggioInvalidita = "";
  String caricoFamiliare = "";
  String reddito = "";
  String redditoAvideo = ""; 
  String punteggioAvideo = "";
  
  String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
  String CODTIPOLISTA = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPOLISTA");
  String prgIscrArt1 = StringUtils.getAttributeStrNotNull(serviceRequest, "prgIscrArt1");
    
  
  SourceBean rowPunteggio = (SourceBean)serviceResponse.getAttribute("CMCALCOLAPUNTEGGIOISCR.ROW");
  if (rowPunteggio != null) {
  		punteggio = rowPunteggio.getAttribute("punteggio").toString();
  		punteggioIniziale = rowPunteggio.getAttribute("punteggioIniziale").toString();
  		punteggioAnzianita = rowPunteggio.getAttribute("punteggioAnzianita").toString();
  		punteggioInvalidita = rowPunteggio.getAttribute("punteggioInvalidita").toString();
  		caricoFamiliare = rowPunteggio.getAttribute("punteggioPersoneCarico").toString();
  		reddito = rowPunteggio.getAttribute("punteggioReddito").toString();
  		
  		if( reddito.equals("-1") ){
  			redditoAvideo = "non presente"; 
  			punteggioAvideo = "non è possibile calcolarlo  senza reddito";
  		}else{ 
  			redditoAvideo = reddito;
  			punteggioAvideo = punteggio;
  		}
  	}	


%>

<html>

<head>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    
    <af:linkScript path="../../js/"/>
    
	<script language="JavaScript">
	
		function selPunteggio(){
			var punteggio = '<%=punteggio%>';
			window.opener.document.Frm1.NUMPUNTEGGIO.value=punteggio;
			window.close();
		}
	
	
		function calcolaPunteggio() {
			if (validateInteger('annoRedditoCM')) {
				if(document.Frm2.annoRedditoCM.value != "" && document.Frm2.annoRedditoCM.value != null) { 
					document.Frm2.numAnnoRedditoCM.value = document.Frm2.annoRedditoCM.value;
				}
				return true;
	 		} else {
	 			return false;
	 		}				  
		  }
	</script>

</head>

<%
  //Oggetti per l'applicazione dello stile
  htmlStreamTop = StyleUtils.roundTopTable(true);
  htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>

<body class="gestione" onload="rinfresca();">

<br><br>

<p class="titolo">Dettaglio punteggio</p>

<af:form method="POST" action="AdapterHTTP" name="Frm2" onSubmit="calcolaPunteggio()">
<%=htmlStreamTop%>
<table class="main" border="0">
<%
if (rowPunteggio != null) {
%>
	<tr>
		<td class="etichetta" >Punteggio&nbsp;Iniziale</td>	
		<td class="campo"><b><%=punteggioIniziale%></b></td>	
	</tr>        
	<tr>
		<td class="etichetta">+&nbsp;&nbsp;Punteggio&nbsp;Anzianit&agrave;</td>	
		<td class="campo"><b><%=punteggioAnzianita%></b></td>	
	</tr>        
	<tr>
	 	<td class="etichetta">-&nbsp;&nbsp;Perc.&nbsp;invalidità</td>	
	 	<td class="campo"><b><%=punteggioInvalidita%></b></td>	
	</tr>  
	<tr>
	 	<td class="etichetta">-&nbsp;&nbsp;Carico&nbsp;familiare</td>	
	 	<td class="campo"><b><%=caricoFamiliare%></b></td>	
	</tr>        
	<tr>
		<td class="etichetta">+&nbsp;&nbsp;Reddito</td>	
		<td class="campo"><b><%=redditoAvideo%></b></td>	
	</tr>        
	<tr>
		<td colspan="2">&nbsp;</td>	
	</tr>  
	<tr>
		<td class="etichetta">Punteggio</td>	
		<td class="campo"><b><%=punteggioAvideo%></b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%if( !reddito.equals("-1") ){ %>
			<a href="#" onClick="selPunteggio();return false">
			<img src="../../img/add.gif" alt="Riporta"></a>
		<%}%>
		</td>
		
	</tr> 

<%
}
else {
%>	
	<input type="hidden" name="PAGE" value="CMIscrLSPunteggioPage"/>
	<input type="hidden" name="MODULE" value="CMCalcolaPunteggioIscr"/>
	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
	<input type="hidden" name="CODTIPOLISTA" value="<%=CODTIPOLISTA%>"/>
	<input type="hidden" name="prgIscrArt1" value="<%=prgIscrArt1%>"/>
	<input type="hidden" name="numAnnoRedditoCM" value="0"/>
	
	<tr>
		<td class="etichetta2" nowrap="nowrap">Anno di riferimento <br>per il reddito</td>			
		<td class="campo">
			<af:textBox classNameBase="input" name="annoRedditoCM" size="5" title="Anno di riferimento per il reddito" value="" maxlength="4"/>			
	    </td>	
	</tr> 
<%
}
%>	
</table>
		
<%=htmlStreamBottom%>

<table align="center">
	<tr>
		<td colspan="2">&nbsp;</td>		
		<td>	
			<%
			if (rowPunteggio != null) {
			%>
				<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="window.close()"/>
			<%
			} 
			else {
			%>
				<input type="submit" class="pulsante" name="calcola" value="Calcola"/>
				&nbsp;&nbsp;&nbsp;
				<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="window.close()"/>
			<%
			}
			%>
		</td>
	</tr>     
</table>
</af:form>
	
</body>
</html>
