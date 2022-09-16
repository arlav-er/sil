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
String _cdnFunz = (String) serviceRequest.getAttribute("CDNFUNZIONE");
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<script language="Javascript">
function ControllaData() {
	var d = new Date();
	var annoDid = 0;
	var annoRiferimento = 0;
    var annoOdierno = parseInt(d.getFullYear().toString());
    if (document.Frm1.annoRif.value != "") {
    	annoRiferimento = parseInt(document.Frm1.annoRif.value);
    	if (annoRiferimento > annoOdierno) {
    		alert ("Attenzione, l'anno non può essere maggiore dell'anno corrente.");
            return false;		
    	}
    	if (document.Frm1.annoDid.value != "") {
    		annoDid = parseInt(document.Frm1.annoDid.value);
    		if (annoDid >= annoRiferimento)	{
    			alert ("Attenzione, l'anno della did deve essere minore dell'anno riferimento.");
                return false;	
    		}	
    	}
    	else {
    		alert ("Attenzione, l'anno della did è obbligatorio.");
            return false;	
    	}		
    }
    else {
    	alert ("Attenzione, l'anno riferimento è obbligatorio.");
        return false;	   
    }
    return true;
}
</script>
</head>

<body class="gestione">
<af:form name="Frm1" method="GET" action="AdapterHTTP" onSubmit="ControllaData()">
<p class="titolo">Ricerca Dich. Annuale</p>
<%out.print(htmlStreamTop);%>
<p align="center">
<table class="main"> 
<tr>
<td class="etichetta">Anno riferimento</td>
<td class="campo">
<af:textBox classNameBase="input" type="integer" name="annoRif" value="" 
	title="Anno riferimento" size="4" maxlength="4" validateOnPost="true" required="true" />
</td>
</tr>
<tr>
<td class="etichetta">Anno DID</td>
<td class="campo">
<af:textBox classNameBase="input" type="integer" name="annoDid" value="" 
	title="Anno riferimento" size="4" maxlength="4" validateOnPost="true" required="true" />
</td>
</tr>
<tr>
<td class="etichetta">Data fine mobilit&agrave;</td>
<td class="campo">
<af:textBox type="date" name="datFineMob" title="Data fine mobilit&agrave;" validateOnPost="true" value="" size="10" maxlength="10"/>
</td>
</tr>
<tr><td colspan=2><hr></hr></td></tr>
<tr valign="top">
<td class="etichetta">Centro per l'impiego competente</td>
<td class="campo">
<af:comboBox name="CODCPI" title="Centro per l'impiego" moduleName="M_ELENCOCPI" disabled="false"
focusOn="false" blankValue="" addBlank="true" required="true" />
</td>
</tr>
</table>
</p>
	
<br>
<center>
<table align="center">
<tr>
<td align="center">
<input class="pulsanti" type="submit" name="cerca" value="Cerca"/>&nbsp;&nbsp;
<input type="reset" class="pulsanti" value="Annulla"/>
</td>
</tr>
</table>
</center>
<%out.print(htmlStreamBottom);%>
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
<input type="hidden" name="PAGE" value="ListaLavDichAnnualePage"/>
</af:form>

</body>
</html>
