<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import="
  com.engiweb.framework.base.*,
  java.lang.*,
  java.text.*,
  java.util.*, 
  it.eng.sil.util.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.*" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%	
	String _page = (String) serviceRequest.getAttribute("PAGE"); 

	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canAltraIscr = attributi.containsButton("ALTRA_ISCR"); 

	// Lettura parametri dalla REQUEST
	String cdnFunzione =(String) serviceRequest.getAttribute("CDNFUNZIONE");
	String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNLAVORATORE");
	
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>

<html>
<head>
<title>Esito accorpamento Iscrizioni</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>

<script language="Javascript">

	function nuovoAccorpamento() {
		
		if (isInSubmit()) return;
		
		goTo("Page=AccorpaAltreIscrPage&cdnfunzione=<%=cdnFunzione%>&cdnLavoratore=<%=cdnLavoratore%>");
	}

	function altreIscr() {
		if (isInSubmit()) return;

		var s= "AdapterHTTP?";
			s += "PAGE=CigLavListaPage&";
			s += "cdnfunzione=<%= cdnFunzione %>&";
			s += "cdnLavoratore=<%=cdnLavoratore%>";
		    setWindowLocation(s);
	}

</script>
</head>

<body class="gestione" onload="rinfresca()">

<%  
   		InfCorrentiLav _testata= new InfCorrentiLav(sessionContainer, cdnLavoratore, user);
		_testata.setSkipLista(true);
		_testata.show(out);
        
    
  %>

<p class="titolo">Esito accorpamento Iscrizioni</p>

<af:form name="form" action="AdapterHTTP" method="POST">

<%= htmlStreamTop %>
	<table class="main">
		<tr>
			<td colspan="2" align="center">
				<table>
					<tr><td><af:showErrors />
							<af:showMessages   prefix="ACCORPA_ISCRIZIONE" />
					</td><tr>
				</table>
			</td>
		<tr>
			<td colspan="2">
				<input type="button" class="pulsante" name="back" value="Chiudi" onclick="nuovoAccorpamento()" />&nbsp;&nbsp;
				<%if(canAltraIscr) {%>
					<input type="button" class="pulsante" name="altreIscrizioni" value="Vai Altre Iscrizioni" onclick="altreIscr()" />
				<%}%>
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
	</table>
<%= htmlStreamBottom %>
</af:form>

</body>
</html>
