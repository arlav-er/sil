<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
	<title>Lista mobilit&agrave; per utenti esterni</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<af:linkScript path="../../js/"/>

<script language="JavaScript">

</script>

</head>
<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Lista mobilit&agrave; per utenti esterni</p>
<p align="center">
<af:form name="frm1" action="AdapterHTTP" method="GET">
<input  type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
<input  type="hidden" name="PAGE" value="ListaMobilitaUtentiEsterniPage">
<% out.print(htmlStreamTop); %> 
  <table>  
  <tr><td colspan="2"/>&nbsp;</td></tr>
  <tr>
    <td class="etichetta" nowrap>Data CPM/Delibera Provinciale</td>
    <td><af:textBox type="date" name="dataIn" title="Data CPM" value="" size="12" maxlength="10" validateOnPost="true" required="true"/>
    </td>
  </tr>
  
  <tr> 
     <td class="etichetta">Tipo lista</td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="CodTipoLista" title="Tipo lista" moduleName="M_MO_TIPO_LISTA" 
		addBlank="true" selectedValue=""/>     
     </td>
  </tr>
  
  <tr> 
     <td class="etichetta">Stato della domanda</td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="codEsito" title="Stato della domanda" moduleName="M_GetStatoMob" 
		addBlank="true" selectedValue=""/>     
     </td>
  </tr>
  
  <tr>
    <td class="etichetta" nowrap>Centro per l'Impiego</td>
    <td class="campo"><af:comboBox title="Centro per l'Impiego" required="true" name="CodCPI" selectedValue="<%=user.getCodRif()%>" moduleName="M_ELENCOCPI" 
    	addBlank="true"/></td>
  </tr>

  <tr>
  <td colspan="2">&nbsp;</td>
  </tr>
  
  <tr>
    <td colspan="2" align="center">
    <input type="submit" class="pulsanti"  name="Cerca" value="Cerca">
    &nbsp;&nbsp;
    <input name="reset" type="reset" class="pulsanti" value="Annulla">
    &nbsp;&nbsp;      
    </td>
  </tr>
  </table>
  <%out.print(htmlStreamBottom);%>
  </af:form>

</body>
</html>
