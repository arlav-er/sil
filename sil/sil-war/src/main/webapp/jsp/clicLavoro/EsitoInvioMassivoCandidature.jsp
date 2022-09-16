<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.afExt.utils.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String titolo = "Esito Invio Massivo";
	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PAGE");

	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	Integer esitoOK  = (Integer)serviceResponse.getAttribute("M_Invio_Massivo.OK");
	Integer esitoNOK = (Integer)serviceResponse.getAttribute("M_Invio_Massivo.NOK");
	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>

<script language="Javascript">

	//...

</script>
</head>

<body class="gestione">

<p class="titolo"><%= titolo %></p>
<af:showMessages prefix="M_Invio_Massivo"/>
<af:showErrors />

<%= htmlStreamTop %>
<table class="main">
  <tr>
  <td class="etichetta" >
     CV inviati con successo 
  </td>
  <td class="campo" >
   	<af:textBox name="numCvOK" type="text"
						value="<%= esitoOK != null ? esitoOK.toString() : "" %>"
						size="10"  classNameBase="input" readonly="true" />
  </td>


  </tr>
  <tr>
  <td class="etichetta" >
     CV scartati 
  </td>
  <td class="campo" >
   	<af:textBox name="numCvNOK" type="text"
						value="<%= esitoNOK != null ? esitoNOK.toString() : "" %>"
						size="10"  classNameBase="input" readonly="true" />
  </td>


  </tr>

</table>
<%= htmlStreamBottom %>
<%--
<af:showMessages prefix="M_Load_Tmp_Massivo"/>--%>



</body>
</html>
