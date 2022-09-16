<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>



<html>
<head>
  <title>Ricerca sulle Aziende</title>

  <link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/" />
</head>
<body class="gestione">
  <p align="center">
  <af:form method="POST" action="AdapterHTTP" dontValidate="true">
  <input type="hidden" name="PAGE" value="AmstrListaAziendePage"/>


<table class="main"> 
  <tr><td><br/></td></tr>
  <tr><td><br/></td></tr>
  <tr><td colspan="2"><p class="titolo">Ricerca sulle Aziende</p><br/></td></tr>
  <tr>
    <td class="etichetta">Ragione Sociale</td>
    <td class="campo">
      <input type="text" name="RagioneSociale" value="" size="20" maxlength="100"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Partita IVA</td>
    <td class="campo">
      <input type="text" name="piva" value="" size="20" maxlength="11"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Codice Fiscale</td>
    <td class="campo">
      <input type="text" name="cf" value="" size="20" maxlength="16"/>
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
  </af:form>

</body>
</html>

