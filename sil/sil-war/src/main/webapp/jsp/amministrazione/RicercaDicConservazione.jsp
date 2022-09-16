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
	<title>Ricerca Elenco Sanatoria Redditi</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
</head>
<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca Elenco Sanatoria Redditi</p>
<p align="center">
<af:form name="frm1" action="AdapterHTTP" method="GET">
<% out.print(htmlStreamTop); %> 
  <table>  
  <tr><td colspan="2"/>&nbsp;</td></tr>
  <tr>
    <td class="etichetta" nowrap>Data inizio ricerca da</td>
    <td class="campo">
    <af:textBox type="date" name="dataInizioRicerca" title="Data inizio ricerca da" value="" size="12" maxlength="10"  validateOnPost="true" required="true"/>
    a&nbsp;&nbsp;<af:textBox type="date" name="dataInizioRicercaA" title="Data inizio ricerca a" value="" size="12" maxlength="10"  validateOnPost="true"/>
    </td>
  </tr>
  
  <tr>
  <td class="etichetta" nowrap>Tipologia</td>
  <td class="campo">
  <af:comboBox name="tipoRicerca"
               title="Tipologia ricerca"
               moduleName=""
               classNameBase="input"
               multiple="true">
    <option value="A">Mancata dichiarazione</option>
    <option value="B">Data fine non valida</option>
  </af:comboBox>
  </td>
  </tr>
  
  <tr><td colspan="2"><hr width="90%"/></td></tr>
  
  <tr>
    <td class="etichetta" nowrap>Centro per l'Impiego</td>
    <td class="campo"><af:comboBox title="Centro per l'Impiego" name="CodCPI" moduleName="M_ELENCOCPI" 
    	addBlank="true" selectedValue="" required="true"/></td>
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
   <input  type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
    <input  type="hidden" name="PAGE" value="ListaDichConservazionePage"> 
  </af:form>

</body>
</html>
