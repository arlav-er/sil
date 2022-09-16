<%@ page contentType="text/html;charset=utf-8"%>

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

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>


<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
boolean canModify = false;
String readonly = String.valueOf( ! canModify );
String _page = (String) serviceRequest.getAttribute("PAGE");

String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
String codFiscaleLavoratore = (String) serviceRequest.getAttribute("CFLAVORATORE");

PageAttribs attributi = new PageAttribs(user,_page);

ProfileDataFilter filter = new ProfileDataFilter(user, _page);

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
} 

boolean canStorico = attributi.containsButton("REQ_WS_STORICO_CO");
boolean canRapporti = attributi.containsButton("REQ_WS_LAVORI_ATTIVI_CO");


String titlePagina = "Consultazione CO in cooperazione applicativa";


	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);

%>
 
	<html>
	<head>
	<title><%=titlePagina %></title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 	
	<af:linkScript path="../../js/" />
	<script type="text/javascript"  src="../../js/script_comuni.js"></script>
	<script type="text/Javascript">
	 
		function setNomePage(btn){
			var nomePage = btn.name;
			if(new String(nomePage).valueOf() == new String("richiestaStoricoCO").valueOf()){
				document.Frm1.PAGE.value = "StoricoCOPage";
			}else if(new String(nomePage).valueOf() == new String("richiestaRapportiLavoriAttivi").valueOf()){
				document.Frm1.PAGE.value = "RapportiAttiviCOPage";
			}
		}
		function checkCampiAggiuntivaCO(){
			var datInizio=document.Frm1.DATINIZIO.value;
			var datFine=document.Frm1.DATFINE.value;
			var checkDate =  confrontaDate(datInizio, datFine);
			if(checkDate>=0){
				return true;
			}else{
				alert("La Data Fine deve essere maggiore o uguale alla Data Inizio"); 
			    return false;
			}
			return false;
		}
	
	</script>
	</head>
	<body class="gestione">

	<p>
	 	<font color="green">
	 		<af:showMessages prefix="M_ConsultaCO"/>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>
 
	 <af:form name="Frm1" action="AdapterHTTP" method="POST" onSubmit="checkCampiAggiuntivaCO()">
	
	<input type="hidden" name="PAGE" value=""/>
	<af:textBox type="hidden" name="cdnfunzione" value="<%= _funzione %>" />
	<af:textBox type="hidden" name="cdnlavoratore" value="<%= cdnLavoratore %>" />
 	<af:textBox type="hidden" name="cfLavoratore" value="<%= codFiscaleLavoratore %>" /> 
	<%= htmlStreamTop %>
	<table class="main">
			<tr>
				<td class="etichetta">
	       			Data inizio
	        	</td>
				<td class="campo" width="20%">
				     <af:textBox type="date" title="Data inizio" name="DATINIZIO" required="true"
				      value="" size="12" maxlength="10" onBlur="checkFormatDate(this);" />
				</td>
			</tr>
			<tr>
				<td class="etichetta">
	       			Data fine
	        	</td>
				<td class="campo" width="20%">
				     <af:textBox type="date" title="Data fine" name="DATFINE" required="true"
				      value="" size="12" maxlength="10" onBlur="checkFormatDate(this);" />
				</td>
			</tr>
			<tr><td colspan="2">&nbsp;</td></tr>	
			<tr>
		<% if (canStorico) { %>
			
				<td >
					<input type="submit" onclick="setNomePage(this)"  name="richiestaStoricoCO"  class="pulsanti" value="Richiesta Storico CO" />
				</td>
		<% } %>
		<% if (canRapporti) { %>
				<td>
					<input type="submit" onclick="setNomePage(this)" name="richiestaRapportiLavoriAttivi"  class="pulsanti" value="Richiesta Rapporti Lavoro Attivi" />
				</td>
		<% } %>
				</tr>
	<tr><td colspan="2">&nbsp;</td></tr>	
	<tr>
				<td colspan="2">	
					<input type="button" onClick="window.close();" class="pulsanti" value="Chiudi" >
				</td>
			</tr>
			<tr>
				<td  colspan="2">&nbsp;</td>
			</tr>
	</table>
	<%= htmlStreamBottom %>
	
	
	</af:form>
	 
</body>
</html>