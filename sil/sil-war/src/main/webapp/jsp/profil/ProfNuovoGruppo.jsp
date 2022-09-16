<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.StringUtils,                  
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<% boolean canModify = true;
   boolean readOnlyStr = false;
   //Oggetti per stile grafico
   String htmlStreamTop = StyleUtils.roundTopTable(false);
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Nuovo Gruppo</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<script language="Javascript">
	
	<%@ include file="scriptGruppo.inc" %>
	<%@ include file="../documenti/RicercaCheck.inc" %>
	
</script>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
</head>

<%
 //recupero dati request
 String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
 String mode=StringUtils.getAttributeStrNotNull(serviceRequest,"MODE");
 String denominazioneRic = (String) serviceRequest.getAttribute("STRDENOMINAZIONERIC");
 String tipoGruppoRic = (String) serviceRequest.getAttribute("TIPOGRUPPORIC");
 String flagStandardRic   = (String) serviceRequest.getAttribute("FLGSTANDARDRIC");  
String flgStandard="";
 String strDenominazione="";
 String strTipoGruppo="";
 String strCodRif="";
 String strLuogoRif="";
 String codComRif="";
 String strComRif="";
 String strNota="";
 String email="";
 BigDecimal cdnTipoGruppo=new BigDecimal(1);
 String cdnTipoGruppoDisabled = String.valueOf(readOnlyStr);
 
	//Se precedentemente stavo inserendo ed è andata mnale ricarico i dati dalla request
	if (mode.equalsIgnoreCase("NEW")) {
		strDenominazione = StringUtils.getAttributeStrNotNull(serviceRequest, "STRDENOMINAZIONE");
		//cdnTipoGruppo=(BigDecimal) serviceRequest.getAttribute("CDNTIPOGRUPPO");
		//TODO
		// temporanemante si puo' sceglere solo
		// un centro per l'impiego
		
		
		
		strCodRif = StringUtils.getAttributeStrNotNull(serviceRequest, "STRCODRIF");
		strLuogoRif = StringUtils.getAttributeStrNotNull(serviceRequest, "STRLUOGORIF");
		codComRif = StringUtils.getAttributeStrNotNull(serviceRequest, "codComNas");
		strComRif = StringUtils.getAttributeStrNotNull(serviceRequest, "strComNas");
		strNota = StringUtils.getAttributeStrNotNull(serviceRequest, "STRNOTA");
	}
%>

<body class="gestione" onload="rinfresca();caricaDati();abilitaEmail();abilitaCodRif();">
<br>
<p class="titolo">Inserimento Gruppo</p>
<p align="center">
  <af:form  action="AdapterHTTP" method="POST" name="Frm1" onSubmit="controllaCampi()">
  <input type="hidden" name="MODE" value="NEW">
  <input type="hidden" name="STRDENOMINAZIONERIC" value="<%=denominazioneRic%>"/>
  <input type="hidden" name="TIPOGRUPPORIC" value="<%=tipoGruppoRic%>"/>
  <input type="hidden" name="FLGSTANDARDRIC" value="<%=flagStandardRic%>"/>
  <input type="hidden" name="cdnfunzione" value="<%=cdnFunzione%>"/>
   <input type="hidden" name="flgStandard" value="S"/>
  
  <input type="hidden" name="PAGE" value="ProfSalvaGruppoPage"/>

  <%out.print(htmlStreamTop);%> 
  <table class="main">
	<%boolean disabilitaCmbCdnTipoGruppo = false; %>

  	<%@ include file="dettaglioGruppo.inc"%>
	
   <tr>
    <td colspan="2" align="center">
        <input class="pulsante" type="submit" name="BTNSALVA" value="Inserisci"/>
      &nbsp;&nbsp;
      <!--<input name="reset" type="reset" class="pulsanti" value="Annulla">-->
    </td>
   </tr>
  </table>
<%out.print(htmlStreamBottom);%> 
  </af:form>
</p>

</body>
</html>
