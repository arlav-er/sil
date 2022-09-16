<!-- @author: Giovanni Landi - Ottobre 2014 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
				  com.engiweb.framework.tags.DefaultErrorTag,
                  it.eng.sil.security.*,
                  it.eng.sil.module.movimenti.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  com.engiweb.framework.util.JavaScript,
                  com.engiweb.framework.dbaccess.sql.TimestampDecorator,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.text.*,
                  java.sql.*,
                  oracle.sql.*,
                  java.util.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	//Attributi della pagina
  	PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
	boolean canGetStato = attributi.containsButton("GET_STATO");

	String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
	String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
	
	String strCodiceFiscale = "";
	String strRegioneMin = "";

  	String htmlStreamTop = StyleUtils.roundTopTable(false);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  	
  	SourceBean rowLavAdesione = (SourceBean)serviceResponse.getAttribute("M_GetInfoLavAdesioneGG.ROWS.ROW");
  	if (rowLavAdesione != null) {
  		strCodiceFiscale = rowLavAdesione.containsAttribute("strcodicefiscale")?rowLavAdesione.getAttribute("strcodicefiscale").toString():"";
  	}
  	SourceBean rowRegione = (SourceBean)serviceResponse.getAttribute("M_GetInfoRegioneGG.ROWS.ROW");
  	if (rowRegione != null) {
  		strRegioneMin = rowRegione.containsAttribute("codmin")?rowRegione.getAttribute("codmin").toString():"";
  	}
%>

<html>
<head>
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css">
  	<af:linkScript path="../../js/"/>
  	<title>Cruscotto Garanzia Giovani</title>
  
  	<script language="Javascript">
  	
  	function settaOperazione(operazione) {
  		document.adesioneGG.OPERAZIONE.value = operazione;	
  	}	
  	
  	</script>
  	
  	<script language="Javascript">

   		window.top.menu.caricaMenuLav(<%=_funzione%>,<%=cdnLavoratore%>);

	</script>
	<script language="Javascript">
  	<%
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
  	%>
	</script>
  
</head>

<body class="gestione" onload="rinfresca();">
<af:error/>
<br>
<p class="titolo">Cruscotto Garanzia Giovani</p>
<br/>
<center>
	<%out.print(htmlStreamTop);%>
	<af:form name="adesioneGG" method="POST" action="AdapterHTTP">
    	<input type="hidden" name="PAGE" value="CruscottoDettaglioPage">
		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>">
		<input type="hidden" name="CDNLAVORATORE" value="<%=Utils.notNull(cdnLavoratore)%>">
		<input type="hidden" name="OPERAZIONE" value="">
		<input type="hidden" name="REGIONE" value="<%=strRegioneMin%>">
		
		<table class="main">
		<tr><td colspan="2"/><br/>Il servizio consente di ottenere dal ministero lo stato attuale dell'adesione</td></tr>
		<tr><td colspan="2"/>&nbsp;</td></tr>
		<tr>
			<td class="etichetta">Codice Fiscale</td>
		    <td class="campo">
		      <af:textBox type="text" name="CF" classNameBase="input" value="<%=strCodiceFiscale%>" size="20" readonly="true"/>
		   	</td>
		</tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          	<td colspan="2" align="center">
          	<%if (canGetStato) {%>
          		<input class="pulsanti" type="submit" name="btnGetStato" onclick="settaOperazione('GET_STATO');" value="Richiedi Stato Adesione"/>
          	<%}%>
         	</td>
        </tr>
        </table>
      </af:form>
    <%out.print(htmlStreamBottom);%>
    <br>
</center>  
</body>
</html>
