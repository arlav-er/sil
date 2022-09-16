<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>


<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String prgSettTipo = (String) serviceRequest.getAttribute("PRGSETTIPO");
String codCpi = (String) serviceRequest.getAttribute("CODCPI");

String MODULE_NAME="MDESCRSETTTIPO";
SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
SourceBean row = (SourceBean) cont.getAttribute("ROW");
String strDescrizioneSettimana = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONESETTIMANA");
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Definizione Settimana Tipo - Copia Definizione Giorno</title>
  <script language="Javascript">
  	function checkGiorno()
  	{
  		
  		var gc = document.frmCopia.GCOPIA.value;
  		var gd = document.frmCopia.GDEST.value;
  		
  		if(gc==gd) { 
  			alert("Il giorno da copiare e il giorno di destinazione devono essere differenti.");
  			return(false);
  		} else { return(true); }
  	}
  </script>
</head>

<body class="gestione"  onload="rinfresca()">
<br>
<p class="titolo">
Definizione Settimana Tipo: &quot;<%=strDescrizioneSettimana%>&quot;
<br>
Copia Definizione Giorno
</p>

<af:form action="AdapterHTTP" name="frmCopia" method="POST" dontValidate="false" onSubmit="checkGiorno()">
<input type="hidden" name="PAGE" value="VSettTipoPage">
<input type="hidden" name="MODULE" value="COPIAGIORNOTIPO">
<input type="hidden" name="PRGSETTIPO" value="<%=prgSettTipo%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<table cellspacing="0" margin="0" cellpadding="0" border="0px" width="94%" noshade align="center">
<tr>
	<td align="left" valign="top" width="6" height="19" class="cal_header"><img src="../../img/angoli/bia1.gif" width="6" height="6"></td>
    <td class="cal_header" align="center" valign="middle" cellpadding="2px"><b>Giorno da copiare</b></td>
    <td class="cal_header" align="center" valign="middle" cellpadding="2px"><b>Giorno di destinazione</b></td>
    <td class="cal_header" align="right" valign="top" width="6" height="19"><img src="../../img/angoli/bia2.gif" width="6" height="6"></td>
</tr>
<tr class="cal">
	<td class="cal" width="6"></td>
	<td class="cal_bordato">
		<br>
		<af:comboBox 
			name="GCOPIA" size="1" 
			title="Giorno da copiare"
            multiple="false" 
            required="TRUE"
            focusOn="false" 
            moduleName=""
            selectedValue="" addBlank="true" blankValue=""
            classNameBase="input">    
            	<option value="1">Luned&igrave;</option>
            	<option value="2">Marted&igrave;</option>
            	<option value="3">Mercoled&igrave;</option>
            	<option value="4">Gioved&igrave;</option>
            	<option value="5">Venerd&igrave;</option>
            	<option value="6">Sabato</option>
        </af:comboBox>
        <br>&nbsp;
	</td>
  	<td class="cal_bordato">
  		<br>
  		<af:comboBox 
			name="GDEST" size="1" 
			title="Giorno di destinazione"
            multiple="false" 
            required="true"
            focusOn="false" 
            moduleName=""
            selectedValue="" addBlank="true" blankValue=""
            classNameBase="input">    
            	<option value="1">Luned&igrave;</option>
            	<option value="2">Marted&igrave;</option>
            	<option value="3">Mercoled&igrave;</option>
            	<option value="4">Gioved&igrave;</option>
            	<option value="5">Venerd&igrave;</option>
            	<option value="6">Sabato</option>
        </af:comboBox>
        <br>&nbsp;
  	</td>
	<td class="cal" width="6"></td>
</tr>
<tr class="cal">
	<td class="cal_header" align="left" valign="bottom" width="6" height="6"><img src="../../img/angoli/bia4.gif"></td>
	<td class="cal_header" height="6" align="center" valign="middle">&nbsp;</td>
    <td class="cal_header" height="6" align="center" valign="middle">&nbsp;</td>
    <td class="cal_header" align="right" valign="bottom" width="6" height="6"><img src="../../img/angoli/bia3.gif"></td>
</tr>
</table>
<p align="center"><input type="submit" class="pulsanti" value="Copia"></p>
</af:form>

<div align="center">
<af:form action="AdapterHTTP" name="form" method="POST" dontValidate="true">
<input type="hidden" name="PAGE" value="VSettTipoPage">
<input type="hidden" name="PRGSETTIPO" value="<%=prgSettTipo%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="submit" class="pulsanti" value="Torna alla definizione della Settimana Tipo">
</af:form>
</div>

</body>
</html>

