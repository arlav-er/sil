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
  String _funzione = (String) serviceRequest.getAttribute("cdnfunzione");
  String aggFunz = (String) serviceRequest.getAttribute("AGG_FUNZ");
  String fromWhere = (String) serviceRequest.getAttribute("fromWhere");
%>
<html>

<head>
<title>Ricerca Lavoratore</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>    
<script language="Javascript">

  function checkCampiObbligatori()
  { 
   if( ((document.Form1.strCodiceFiscale_ric.value != null) && (document.Form1.strCodiceFiscale_ric.value.length >= 6)) 
          || (document.Form1.strCognome_ric.value.length >= 2) ) return true;
        alert("Inserire almeno sei caratteri del codice fiscale\no almeno due caratteri del cognome");
        return false;
   }
 
</script>
</head>
<body class="gestione" onload="">
<p class="titolo">Ricerca Lavoratore</p>
<p align="center">

<af:form method="POST" name="Form1"  action="AdapterHTTP" onSubmit="checkCampiObbligatori()">
<input type="hidden" name="PAGE" value="NullaOstaListLavoratorePage"/> 
<input type="hidden" name="AGG_FUNZ" value="<%=aggFunz%>"/> 
<input type="hidden" name="fromWhere" value="<%=fromWhere%>"/> 
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>  

  <%= htmlStreamTop %>
  <table class="main">
      	<tr>
           	<tr><td colspan="2"/><br/>Per effettuare una ricerca inserire almeno i primi sei caratteri del codice fiscale o almeno i primi due caratteri del cognome</td></tr>
            <tr><td colspan="2"/>&nbsp;</td></tr>
            <tr>
              <td class="etichetta">Codice Fiscale</td>
              <td class="campo">
                <af:textBox type="text" name="strCodiceFiscale_ric" validateOnPost="true" value="" size="30" maxlength="16"/> 
              </td>
            </tr>
            <tr>
              <td class="etichetta">Cognome</td>
              <td class="campo">
                <af:textBox type="text" name="strCognome_ric" validateOnPost="true" value="" size="30" maxlength="50"/>
              </td>
            </tr>
            <tr>
              <td class="etichetta">Nome</td>
              <td class="campo">
                <af:textBox type="text" name="strNome_ric" validateOnPost="true" value="" size="30" maxlength="50"/>
              </td>
            </tr>
            <tr>
              <td class="etichetta">Tipo ricerca</td>
              <td class="campo">
              <table colspacing="0" colpadding="0" border="0">
              <tr>
               <td><input type="radio" name="tipoRicerca" value="esatta" CHECKED/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
               <td><input type="radio" name="tipoRicerca" value="iniziaPer"/> inizia per</td>
              </tr>
              </table>
              </td>
            </tr>
            <tr><td colspan="2">&nbsp;</td></tr>
    <tr>
      <td colspan="2" align="center">
      <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
      &nbsp;&nbsp;
      <input class="pulsanti" type="reset" name="reset" value="Annulla"/>
      </td>
    </tr>         
    <tr><td colspan="2">&nbsp;</td></tr>
    <tr>
      <td colspan="2" align="center">
      <input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
      </td>
    </tr>
    </table>
    <%=htmlStreamBottom %>
    </af:form>
		
	</body>
</html>

