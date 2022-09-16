<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title>Ricerca Lavoratore</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>    
<script language="Javascript">
	<%@ include file="./RicercaCheck.inc" %>
</script>

</head>
<body class="gestione" onload="">
<p class="titolo">Ricerca di un Lavoratore</p>
<p align="center">

<af:form method="POST" action="AdapterHTTP" onSubmit="checkCampiObbligatori(strCodiceFiscale_ric,strCognome_ric)">
<input type="hidden" name="PAGE" value="DocumentiListaLavoratoriPage"/>
<!--Passo l'informazione su che tipo di ricerca dovrò eseguire e sulla funzione da chiamare al ritorno-->
<input type="hidden" name="AGG_FUNZ" value="<%= (String) serviceRequest.getAttribute("AGG_FUNZ") %>"/>

  <%= htmlStreamTop %> 
  <table class="main"> 
    <tr><td colspan="2">Per effettuare una ricerca inserire almeno i primi sei caratteri del codice fiscale o almeno i primi due caratteri del cognome</td></tr>
    <tr><td colspan="2">&nbsp;</td></tr>
    <tr>
      <td class="etichetta">Codice Fiscale</td>
      <td class="campo">
        <af:textBox type="text" name="strCodiceFiscale_ric" validateOnPost="true" value="" size="30" maxlength="16"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta">Cognome</td>
      <td class="campo">
        <af:textBox type="text" name="strCognome_ric" validateOnPost="true" value="" size="30" maxlength="40"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta">Nome</td>
      <td class="campo">
        <af:textBox type="text" name="strNome_ric" validateOnPost="true" value="" size="30" maxlength="40"/>
      </td>
    </tr>       
    <tr>
      <td class="etichetta">tipo ricerca</td>
      <td class="campo">
      <table colspacing="0" colpadding="0" border="0">
      <tr>
       <td><input type="radio" name="tipoRicerca" value="esatta" /> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
       <td><input type="radio" name="tipoRicerca" value="iniziaPer" checked="true" /> inizia per</td>
      </tr>
      </table>
      </td>
    </tr>
    
    <tr><td colspan="2">&nbsp;</td></tr>
    <tr>
      <td colspan="2">
        <span class="bottoni">
	      <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
	      &nbsp;&nbsp;
	      <input class="pulsanti" type="reset" name="reset" value="Annulla"/>
        </span>
      </td>
    </tr>
    <tr><td colspan="2">&nbsp;</td></tr>
    <tr>
      <td colspan="2">
      	  <span class="bottoni">
      		<input type="button" class="pulsanti" value="Chiudi" onClick="window.close();" />
      	  </span>
      </td>
    </tr>
  </table>
  <%= htmlStreamBottom %> 
</af:form>
</p>

</body>
</html>
