<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<title>Ricerca Gruppi</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<script language="Javascript">
<%@ include file="../documenti/RicercaCheck.inc" %>
</script>
</head>

<% boolean readOnlyStr = false;
  String cdnFunzione = (String) serviceRequest.getAttribute("cdnfunzione");
   String htmlStreamTop = StyleUtils.roundTopTable(false);
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);

 String _page = (String) serviceRequest.getAttribute("PAGE"); 
  PageAttribs attributi = new PageAttribs(user, _page);
 


%>

<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca gruppi</p>
<p align="center">
  <af:form  action="AdapterHTTP?PAGE=ProfListaGruppiPage" method="POST" name="Frm1">
  <%out.print(htmlStreamTop);%> 
  <table class="main">
   <tr>
     <td class="etichetta">Gruppo</td>
     <td class="campo"><af:textBox type="text" name="STRDENOMINAZIONERIC" value="" size="30" maxlength="100" /></td>
   </tr>   
   <tr>
    <td class="etichetta">Tipo gruppo </td>
    <td  class="campo"><af:comboBox name="TIPOGRUPPORIC" moduleName="M_ComboTipoGruppo" classNameBase="input" addBlank="true"
                     disabled="<%=String.valueOf(readOnlyStr)%>"/></td>
   </tr>
   <tr>
    <td class="etichetta">Standard</td>
    <td class="campo"><SELECT name="FLGSTANDARDRIC"><OPTION value=""></OPTION><OPTION value="S">S&igrave;</OPTION><OPTION value="N">No</OPTION></SELECT></td>
   </tr>

   <tr><td colspan="2">&nbsp;</td></tr>
   <tr><td colspan="2">&nbsp;</td></tr>
   <tr>
    <td colspan="2" align="center">
      <input class="pulsante" type="submit" name="cerca" value="Cerca"/>
      &nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla">
    </td>
   </tr>
   
   <% if (attributi.containsButton("nuovo")){%>
   <tr>
    <td colspan="2" align="center">
      <input class="pulsante" type="button" name="btnInserisci" value="Nuovo gruppo" onClick="javascript:document.Frm1.action='AdapterHTTP?PAGE=ProfNuovoGruppoPage&MODE=VIEW';doFormSubmit(document.Frm1);"/>
    </td>
   </tr>
   
   <% }%>
   
  </table>
<%out.print(htmlStreamBottom);%> 
  <input type="hidden" name="CDNFUNZIONE" value="<%=((String) serviceRequest.getAttribute("CDNFUNZIONE"))%>"/>
  </af:form>
</p>

</body>
</html>
