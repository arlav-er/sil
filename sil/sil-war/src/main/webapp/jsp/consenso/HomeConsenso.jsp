<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*, java.math.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
	
	boolean canVerificaConsenso = false;
	
	//ProfileDataFilter filter = new ProfileDataFilter(user, _page); //per ogni pagina da profilare
	/** Per qualsiasi pagina da profilare appartenente alla 'sezione' Gestione Consenso */ 
	ProfileDataFilter filter = new ProfileDataFilter(user, "HomeConsensoPage");
	
	//PageAttribs attributi = new PageAttribs(user, _page); //per ogni pagina da profilare
	/** Per ogni attributo della pagina da profilare appartenente alla 'sezione' Gestione Consenso */
	PageAttribs attributi = new PageAttribs(user, "HomeConsensoPage");
	
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
		// TODO: da mettere il pulsante 'VERIFICA'...
		//canVerificaConsenso = attributi.containsButton("VERIFICA");
		canVerificaConsenso = true;
	}
	
	InfCorrentiLav infCorrentiLav= null;

	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	
	SourceBean lavoratore = (SourceBean) serviceResponse.getAttribute("M_GetInfoLavAdesioneGG.ROWS.ROW");
	String codfisclav = lavoratore.getAttribute("strcodicefiscale").toString();
    
%>
<html>
<head>
<title>Home Consenso</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
<af:linkScript path="../../js/"/>  
<script language="JavaScript">

<%//Genera il Javascript che si occuperà di inserire i links nel footer
if (!cdnLavoratore.equals(""))
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);
%>

</script>

</head>
<body class="gestione" onload="rinfresca();">

	<%
		infCorrentiLav= new InfCorrentiLav(sessionContainer, cdnLavoratore, user);
		infCorrentiLav.setSkipLista(true);
		infCorrentiLav.show(out);
	%>
	
	<!--
	<br>
	<table maxwidth="96%" width="96%" align="center" margin="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="info" valign="top" align="left" width="6" height="6px">
				<img src="../../img/angoli/bia1.gif" width="6" height="6">
			</td>
			<td class="info" height="6px">&nbsp;</td>
			<td class="info" valign="top" align="right" width="6" height="6px">
				<img src="../../img/angoli/bia2.gif" width="6" height="6">
			</td>
		</tr>
		<tr>
			<td class="info" height="6px">&nbsp;</td>
			<td class="info" height="6px">&nbsp; Il Servizio consente di verificare la presenza del consenso nel Sistema di Gestione dei Consensi</td>
			<td class="info" height="6px">&nbsp;</td>
		</tr>
		<tr>
			<td class="info" height="6px">&nbsp;</td>
			<td class="info" height="6px">&nbsp;</td>
			<td class="info" height="6px">&nbsp;</td>
		</tr>
		<tr>
			<td class="info" height="6px">&nbsp;</td>
			<td class="info" height="6px">&nbsp;Codice Fiscale: <font color="Navy"><b></b></font></td>
			<td class="info" height="6px">&nbsp;</td>
		</tr>
		<tr>
			<td class="info" height="6px">&nbsp;</td>
			<td class="info" height="6px">&nbsp;</td>
			<td class="info" height="6px">&nbsp;</td>
		</tr>
		<tr valign="bottom">
			<td class="info" valign="bottom" align="left" width="6" height="6px">
				<img src="../../img/angoli/bia4.gif" width="6" height="6">
			</td>
			<td class="info" height="6px">&nbsp;</td>
			<td class="info" valign="bottom" align="right" width="6" height="6px">
				<img src="../../img/angoli/bia3.gif" width="6" height="6">
			</td>
		</tr>
	</table>
	-->
	
	<p>
	 	<font color="green" />
	  	<font color="red"><af:showErrors /></font>
	</p>

<p class="titolo">Consenso</p>
  	<af:form name="form1" action="AdapterHTTP" method="POST">
	  <%out.print(htmlStreamTop);%> 
	  <table class="main" border="0">
	  	<tr>
		 <td class="etichetta" nowrap>Il Servizio consente di verificare la presenza del consenso nel Sistema di Gestione dei Consensi</td>          
	   </tr>
	   <tr>
		 <td class="etichetta" nowrap>Codice Fiscale</td>
		 <td class="campo">
		    <font color="Navy"><b><%=codfisclav %></b></font>
		 </td>            
	   </tr>
	  </table>
	  
	  <%if (canVerificaConsenso) {%>
         	<br>
         	<center>
	     		<table class="main">
		       		<tr>
		         		<td>
		             		<input type="submit" class="pulsanti" name="BTNVERIFICA" value="Verifica Consenso">
		           		</td>
		         	</tr>
	         	</table>
	         	</center>
       <%}	
	  
	  out.print(htmlStreamBottom);%>
	  <input type="hidden" name="PAGE" value="VerificaConsensoPage">
	  <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>">
	  <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
	 </af:form>
	 
</body>
</html>
