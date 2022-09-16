<!-- @author: Giordano Gritti -->
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

%>

<html>
<head>
  <title>Ricerca Adesioni</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script type="text/javascript">
  
  function valorizzaHidden() {
   	document.form1.descCPI_H.value = document.form1.CodCPI.options[document.form1.CodCPI.selectedIndex].text;
  	return true;
  }
   
  </script>
 
</head>

<body class="gestione" onload="rinfresca()">
<br>
<p class="titolo">Ricerca Adesioni</p>

<%out.print(htmlStreamTop);%>  

<af:form action="AdapterHTTP" name="form1" method="GET" onSubmit="valorizzaHidden()">

<div class="sezione">Periodo</div>
	<table class="main">
		<tr>
		  <td class="etichetta">Data Chiamata</td>
		  <td class="campo">
		  <af:textBox name="chiamata" title="chiamata"
		              type="date"
		              size="10"
		              maxlength="10"
		              required="true"
		              validateOnPost="true"
		              disabled="false"
		              value="<%=chiamata%>"/>
		  </td>
		</tr>
	</table>
	
<div class="sezione">Centro per l'Impiego</div>
	<table class="main">
	<tr>
	  <td class="etichetta">Centro per l'Impiego</td>
	  <td class="campo">
	    <af:comboBox name="CodCPI"
	                 size="1"
	                 title="Scelta CPI"
	                 multiple="false"
	                 required="true"
	                 focusOn="false"
	                 moduleName="M_SMS_COMBO_CPI"
	                 addBlank="true"
	                 blankValue=""
	                 selectedValue="<%=CodCPI%>"/>
	  </td>
	</tr>
	</table>
	<table>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
		  <td colspan="2" align="center">
		  	<input type="submit" class="pulsanti" value="Cerca">
		  	&nbsp;&nbsp;
		  	<input type="reset" class="pulsanti" value="Annulla">
		  </td>
		</tr>
	</table>
	<!-- parametri passati alla request -->
	<input name="PAGE" type="hidden" value="ASListaAdesioniChiamPage"/>
	<input type="hidden" name="descCPI_H" value=""/>
</af:form>

<%out.print(htmlStreamBottom);%>

</body>
</html>

