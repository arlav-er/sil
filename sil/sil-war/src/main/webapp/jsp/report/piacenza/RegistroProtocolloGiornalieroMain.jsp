<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="/jsp/global/noCaching.inc"%>

<%@ include file="/jsp/global/getCommonObjects.inc"%>
<%
	String queryString = null;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	String dataCalcolo = sdf.format(new Date());
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<%--
<af:linkScript path="../../js/" />
--%>
<%@ include file="/jsp/documenti/_apriGestioneDoc.inc"%>
<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.configuration.ConfigSingleton,it.eng.sil.util.*,it.eng.afExt.utils.StringUtils,it.eng.sil.security.ProfileDataFilter,java.text.*,java.util.*,java.math.*,it.eng.sil.security.*"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<html>
<head>
<title>Registro Protocollo Giornaliero</title>
<link rel="stylesheet"
	href="<%=request.getContextPath() %>/css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath() %>/css/listdetail.css" />
<%@ include file="/jsp/global/checkFormatData.inc"%>
<af:linkScript path='<%=request.getContextPath() + "/js/" %>' />
<script language="JavaScript"
	src="<%=request.getContextPath() %>/js/layers.js"></script>
<script language="JavaScript" type="text/javascript"
	src="<%=request.getContextPath() %>/js/script_comuni.js"></script>
<script type="text/javascript">
  	function controllaData(form2chk) {
  	alert("form2chk.dataCalcolo:"+form2chk.dataCalcolo+", form2chk.dataCalcolo.value:"+form2chk.dataCalcolo.value);
		return checkFormatDate(form2chk.dataCalcolo);
   	}
   	function Stampa(){
		apriGestioneDoc('RPT_STAMPA_PROT_GIORNALIERO','&dataCalcolo='+document.dettCondizione.dataCalcolo.value,'AL');
	}
</script>
</head>
<body class="gestione" onload="rinfresca();">

<p class="titolo">Registro Protocollo Giornaliero</p>

<%
	out.print(htmlStreamTop);
%>
<af:form name="dettCondizione" method="POST" action="AdapterHTTP">
	<input type="hidden" name="PAGE"
		value="RegistroProtocolloGiornalieroReportPage">
	<table align="center" width="30%" border="0">
		<tr>
			<td class="etichetta">Data Calcolo</td>
			<td class="campo"><af:textBox type="date" validateOnPost="true"
				title="Data Inizio" name="dataCalcolo" value="<%=dataCalcolo%>"
				size="10" maxlength="10" required="true" /></td>
		</tr>
		<tr>
			&nbsp;
		</tr>
		<tr>
			<td class="etichetta" align="center"><input type="submit" class="pulsanti"
				value="Visualizza Report" onclick="controllaData(this)" /></td>
			<td class="etichetta" align="center"><input type="button" class="pulsanti"
				value="Stampa" onclick="Stampa()" /></td>
		</tr>
	</table>
</af:form>
<%--
	<br />
	<center><input type="submit" class="pulsanti"
		value="Genera Report" onclick="controllaData(this)" /></center>
	<br />
--%>
<%
	out.print(htmlStreamBottom);
%>
</body>
</html>