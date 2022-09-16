<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*,
                java.math.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  //NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  
  if (!filter.canView()) {
  	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	return;
  }
  
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  String prgVoucher = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGVOUCHER");
  String titolo = "Il titolo d'acquisto prevede la compilazione di ";
  SourceBean rowVoucher = (SourceBean)serviceResponse.getAttribute("M_InfoGeneraliTDA.ROWS.ROW");
  if (rowVoucher != null) {
	  titolo = titolo + StringUtils.getAttributeStrNotNull(rowVoucher,"descmodalitasel");
  }
  titolo = titolo + " delle modalità previste";
%>

<html>
<head>
  <title>Modalità e durata</title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  
  <af:linkScript path="../../js/"/>

  <script type="text/Javascript">

  	 function tornaLista() {
		if (isInSubmit()) return;
		<%
		// Recupero l'eventuale URL generato dalla LISTA precedente
		String token = "_TOKEN_" + "ListaTDAPage";
		String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());	
		%>
		setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
	  }
  
  </script>
 
</head>
<body  class="gestione" onload="rinfresca();">
<p>
	<font color="green">
		<af:showMessages prefix="M_AggiornaModalitaTDA"/>
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<%
Linguette _linguetta = new Linguette(user, new Integer(_funzione).intValue() , _page, new BigDecimal(prgVoucher));
_linguetta.setCodiceItem("prgVoucher");
_linguetta.show(out);
%>

<p class="titolo"><%= titolo %></p>

<p>
<af:list moduleName="M_ListaModalitaTDA" />
</p>

<%-- <center>
<af:form name="frm1">
<table>
<tr>
<td align="center">
	<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onClick="tornaLista();"/>
</td>
</tr>
</table>
</af:form>
</center> --%>

</body>
</html>