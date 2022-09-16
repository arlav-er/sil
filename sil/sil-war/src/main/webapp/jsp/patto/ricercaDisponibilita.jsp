<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<html>
<head>
	<title>Ricerca Lavoratore</title>
	<af:linkScript path="../../js/" />
</head>

<body>
     <af:form name="ricercalav" action="AdapterHTTP?PAGE=ListaDisponibilitaPage" method="post" dontValidate="true">
        	
  <table>
    <tr> 
      <td>Nome</td>
      <td><input name="NOME" size="18" maxlength="30" value="" ></td>
    </tr>
    <tr> 
      <td>Cognome</td>
      <td><input name="COGNOME" size="18" maxlength="30" value="" ></td>
    </tr>
    <tr> 
      <td>Cod. Fiscale</td>
      <td><input name="CF" size="18" maxlength="16" ></td>
    </tr>
    <tr> 
      <td>Attività precedente</td>
      <td><input name="ATT_PREC" size="18" maxlength="30" ></td>
    </tr>
    <tr> 
      <td></td>
      <td></td>
    </tr>
    <tr> 
      <td></td>
      <td><input name="Invia" type="submit" class="pulsanti" value="CERCA"></td>
    </tr>
  </table>
     	
     	</af:form>
</body>
</html>
