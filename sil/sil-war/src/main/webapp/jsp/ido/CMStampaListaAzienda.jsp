<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,it.eng.afExt.utils.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*,
                 java.math.*"
%>

<%@ taglib uri="aftags" prefix="af" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String chiamata =StringUtils.getAttributeStrNotNull(serviceRequest,"chiamata");
String CodCPI =StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");

String queryString = null;

%>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script type="text/javascript">
  
  function stampa() {
	if (!controllaFunzTL()) return;
	
	var data = document.form1.data.value;
	var anno = document.form1.anno.value;
	
	apriGestioneDoc('RPT_LISTAAZI_NO_PROSPETTI','&data='+data +'&anno='+anno, 'L68_AZI');
	  undoSubmit();
	}
 
</script>
 
</head>

<body class="gestione" onload="rinfresca()">
<br>
<p class="titolo">Stampa lista aziende senza prospetti</p>

<%out.print(htmlStreamTop);%>  

<af:form action="AdapterHTTP" name="form1" method="GET">

<table class="main">
	<tr>
		<td class="etichetta">Data</td>
		<td class="campo">
			<af:textBox name="data" title="Data" type="date" size="10" maxlength="10" validateOnPost="true"/>
		</td>
	</tr>
	<tr>
		<td class="etichetta">Anno</td>
		<td class="campo">
			<af:textBox name="anno" title="Anno" type="text" size="5" maxlength="4" required="true" validateOnPost="true" value=""/>
		</td>
	</tr>
</table>
	
<br/>
	<center><input type="button" class="pulsanti" value="Stampa" onclick="stampa()" /></center>
<br/>
</af:form>

<%out.print(htmlStreamBottom);%>

</body>
</html>

