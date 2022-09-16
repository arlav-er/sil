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
  
  String aggFunz = (String) serviceRequest.getAttribute("AGG_FUNZ");
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  String soggetto = (String) serviceRequest.getAttribute("MOV_SOGG");
  String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "prgAzienda");
  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "prgUnita");
  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>

<head>
<title>Ricerca Azienda</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>    
 <script language="Javascript">
 
 function checkCampiObbligatori()
  { 
  	if ((document.Form1.ragioneSociale.value.length > 0 ) || (document.Form1.cf.value.length > 0) || 
    	(document.Form1.piva.value.length > 0)) {
        return true;
    }
    alert("Inserire almeno uno dei seguenti campi nella ricerca:\n\tCodice Fiscale\n\tPartita IVA\n\tRagione Sociale\n");
    return false;
  }

  </script>
</head>
<body class="gestione" onload="">
<p class="titolo">Ricerca di un'Azienda</p>
<p align="center">

<af:form name="Form1" method="POST" action="AdapterHTTP" onSubmit="checkCampiObbligatori()">
<input type="hidden" name="PAGE" value="NullaOstaListAziendePage"/> 
<input type="hidden" name="AGG_FUNZ" value="<%=aggFunz%>"/>  
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>  
<input type="hidden" name="MOV_SOGG" value="<%=soggetto%>"/>
<input type="hidden" name="CONTESTOPROVENIENZA" value="MOVIMENTI"/>
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>"/>
<input type="hidden" name="prgUnita" value="<%=prgUnita%>"/>

<%out.print(htmlStreamTop);%>
<table class="main">
    <tr valign="top">
      <td class="etichetta">Tipo azienda</td>
      <td class="campo">
        <af:comboBox classNameBase="input" name="codTipoAzienda" title="Tipo Azienda" moduleName="M_GETTIPIAZIENDA" addBlank="true" />
      </td>
    </tr>
    <tr valign="top">
      <td class="etichetta">Natura azienda</td>
      <td class="campo">
        <af:comboBox classNameBase="input" name="codNatGiuridica" title="Natura Giuridica" moduleName="M_GETTIPINATGIURIDICA" addBlank="true" />
      </td>
    </tr>
    <tr>
      <td class="etichetta">Partita IVA</td>
      <td class="campo">
        <af:textBox type="text" name="piva" validateOnPost="true" value="" size="30" maxlength="11"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta">Codice Fiscale</td>
      <td class="campo">
        <af:textBox type="text" name="cf" validateOnPost="true" value="" size="30" maxlength="16"/>
      </td>
    </tr>            
    <tr>
      <td class="etichetta">Ragione Sociale</td>
      <td class="campo">
        <af:textBox type="text" name="ragioneSociale" validateOnPost="true" value="" size="30" maxlength="100"/>                
      </td>
    </tr> 
<!--Inserisco i pulsanti necessari-->
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
<%out.print(htmlStreamBottom);%>
</af:form>
</p>
</body>
</html>
