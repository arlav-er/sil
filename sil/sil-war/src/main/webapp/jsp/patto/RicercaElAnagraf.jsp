
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  java.util.*,
                  it.eng.sil.util.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
	<title>Ricerca Elenco Anagrafico</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<script language="Javascript">
<%@ include file="../documenti/RicercaCheck.inc" %>
</script>
</head>

<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca Elenco Anagrafico Lavoratori</p>
<p align="center">
  <af:form action="AdapterHTTP?PAGE=ListaElAnagPage" method="POST" onSubmit="checkCampiObbligatori(CF,COGNOME)">        	
  <%out.print(htmlStreamTop);%> 
  <table class="main">
  
  <tr><td colspan="2"/><br/>Per effettuare una ricerca inserire almeno i primi sei caratteri del codice fiscale o almeno i primi due caratteri del cognome</td></tr>
  <tr><td colspan="2"/>&nbsp;</td></tr>
  <tr>
    <td class="etichetta">Codice Fiscale</td>
    <td class="campo">
      <af:textBox type="text" name="CF" value="" size="20" maxlength="16"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Cognome</td>
    <td class="campo"><af:textBox type="text" name="COGNOME" value="" size="20" maxlength="50"/></td>
  </tr>
  <tr>
    <td class="etichetta">Nome</td>
    <td class="campo"><af:textBox type="text" name="NOME" value="" size="20" maxlength="50"/></td>
  </tr>
  <tr>
    <td class="etichetta">tipo ricerca</td>
    <td class="campo">
    <table colspacing="0" colpadding="0" border="0">
    <tr>
     <td><input type="radio" name="tipoRicerca" value="esatta" CHECKED/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
     <td><input type="radio" name="tipoRicerca" value="iniziaPer"/> inizia per</td>
    </tr>
    </table>
    </td>
  </tr>

  <tr><td colspan="2"><hr width="90%"/></td></tr>
  <tr>
    <td class="etichetta">Data Inserimento nell'Elenco Anag.</td>
    <td class="campo"><af:textBox type="date" name="datInizio" value="" size="12" maxlength="10" validateOnPost="true" /></td>
  </tr>

  <tr><td colspan="2"><hr width="90%"/></td></tr>
  <tr>
    <td class="etichetta">Centro per l'Impiego competente</td>
    <td class="campo">
      <af:comboBox name="CodCPI" moduleName="M_ELENCOCPI" addBlank="true" selectedValue="<%=\"\" /*user.getCodRif()*/%>"/>
    </td>
  <tr>
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr> 
    <td colspan="2" align="center">
      <input name="Invia" type="submit" class="pulsanti" value="Cerca">
      <%
			String CDNFUNZIONE = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	  %>
      <input name="CDNFUNZIONE" type="hidden" value="<%=CDNFUNZIONE%>"/>&nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla">
    </td>
  </tr>

  </table>
  <%out.print(htmlStreamBottom);%> 
  </af:form>
</p>

</body>
</html>
