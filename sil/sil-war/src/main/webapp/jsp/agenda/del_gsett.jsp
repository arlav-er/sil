<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>


<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String giorno = StringUtils.getAttributeStrNotNull(serviceRequest,"giorno");
String mese = StringUtils.getAttributeStrNotNull(serviceRequest,"mese");
String anno = StringUtils.getAttributeStrNotNull(serviceRequest,"anno");
String giornoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"giornoDB");
String meseDB = StringUtils.getAttributeStrNotNull(serviceRequest,"meseDB");
String annoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"annoDB");

String mod = StringUtils.getAttributeStrNotNull(serviceRequest,"MOD");
if(mod.equals("")) { mod = "0"; }
String data=giornoDB + "/" + meseDB + "/" + annoDB;
%>

<%
String codCpi = "";
BigDecimal numKloGiornoNl = null;
String prgGiornoNl = "";
String datInizioVal = "";
String datFineVal = "";
BigDecimal numGSett = null;
String tipo = "";

boolean errIns = false;
%>

<%
String MODULE_NAME="MDETTGSETT";
SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");

if(row != null) {
    BigDecimal prg = (BigDecimal) row.getAttribute("PRGGIORNONL");
    if(prg!=null) { prgGiornoNl = prg.toString(); }
    codCpi = StringUtils.getAttributeStrNotNull(row, "CODCPI");
    numKloGiornoNl = (BigDecimal) row.getAttribute("NUMKLOGIORNONL");
    // numKloGiornoNl = numKloGiornoNl.add(new BigDecimal(1)); // il +1 è inglobato nello statement
    numGSett = (BigDecimal) row.getAttribute("NUMGSETT");
    datInizioVal = StringUtils.getAttributeStrNotNull(row, "DATINIZIOVAL");
    datFineVal = StringUtils.getAttributeStrNotNull(row, "DATFINEVAL");
    tipo = StringUtils.getAttributeStrNotNull(serviceRequest, "tipo");

    errIns = false;
} 
Testata testata = new Testata(null,null,null,null);
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Cancella Regola Giorno Festivo</title>
  <script language="Javascript">
  function cancellaFestivo(n)
  {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var oPage = document.getElementById("PAGE");
    var oDel = document.getElementById("DEL");
    var oUpdf = document.getElementById("UPDF");
    var oTipoUpdf = document.getElementById("TIPOUPDF");
    var oForm = document.getElementById("frmDelFestivo");

    switch(n) {
      case 1:
              oPage.value = "FestiviPage";
              oDel.value = "true";
              oUpdf.value = "";
              oTipoUpdf.value = "";
              break;
      case 2:
      case 3:
              oPage.value = "FestiviPage";
              oDel.value = "";
              oUpdf.value = "GSETT";
              oTipoUpdf.value = n;
              break;
      default:
              oPage.value = "FestiviPage";
              oDel.value = "";
              oUpdf.value = "";
              oTipoUpdf.value = "";
              break;
    }
    
    doFormSubmit(oForm);
  }

  function chiudiDelFestivo()
  {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var oPage = document.getElementById("PAGE");
    var oDel = document.getElementById("DEL");
    var oUpdf = document.getElementById("UPDF");
    var oTipoUpdf = document.getElementById("TIPOUPDF");
    var oForm = document.getElementById("frmDelFestivo");

    oPage.value = "FestiviPage";
    oDel.value = "";
    oUpdf.value = "";
    oTipoUpdf.value = "";
    doFormSubmit(oForm);
  }
  </script>
</head>
<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<body class="gestione">
<br>
<p class="titolo">Cancella Regola Riposo Settimanale</p>

<af:form name="frmDelFestivo" id="frmDelFestivo" action="AdapterHTTP" method="POST" dontValidate="true">

<input type="hidden" id="PAGE" name="PAGE" value=""/>
<input type="hidden" id="DEL" name="DEL" value=""/>
<input type="hidden" id="UPDF" name="UPDF" value=""/>
<input type="hidden" id="TIPOUPDF" name="TIPOUPDF" value=""/>

<input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
<input name="meseDB" type="hidden" value="<%=meseDB%>"/>
<input name="annoDB" type="hidden" value="<%=annoDB%>"/>
<input name="giorno" type="hidden" value="<%=giorno%>"/>
<input name="mese" type="hidden" value="<%=mese%>"/>
<input name="anno" type="hidden" value="<%=anno%>"/>
<input name="MOD" type="hidden" value="<%=mod%>"/>

<input type="hidden" name="NUMGSETT" value="<%=numGSett%>"/>
<input type="hidden" name="CODCPI" value="<%=codCpi%>"/>
<input type="hidden" name="PRGGIORNONL" value="<%=prgGiornoNl%>"/>
<input type="hidden" name="NUMKLOGIORNONL" value="<%=numKloGiornoNl%>"/>
<input type="hidden" name="DATINIZIOVAL" value="<%=datInizioVal%>"/>
<input type="hidden" name="DATFINEVAL" value="<%=datFineVal%>"/>
<input type="hidden" name="data" value="<%=data%>"/>

<%out.print(htmlStreamTop);%>
<table class="main">
<%
String giorniEst[] = {"&nbsp;", "Luned&igrave;", "Marted&igrave;", "Mercoled&igrave;", "Gioved&igrave;", "Venerd&igrave;", "Sabati", "Domeniche"};
%>
<tr>
  <td class="etichetta">Giorno di Riposo</td>
  <td class="campo">
  <% if(numGSett.equals(new BigDecimal(7))) {%>
          Tutte le
  <% } else { %>
          Tutti i
  <% } %>
  <b><%=giorniEst[Integer.parseInt(numGSett.toString())]%></b>
  </td>
</tr>
<tr>
  <td class="etichetta">Giorno Selezionato</td>
  <td class="campo"><%=data%></td>
</tr>
<tr>
  <td class="etichetta">Data Inizio Validit&agrave;</td>
  <td class="campo"><%=datInizioVal%></td>
</tr>
<tr>
  <td class="etichetta">Data Fine Validit&agrave;</td>
  <td class="campo"><%=datFineVal%></td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="left"><div class="sezione2">1. Cancella</div></td>
</tr>
<tr>
  <td colspan="2" align="justify">
  Cancella questa Regola
  </td>
</tr>
<tr>
  <td colspan="2" align="center">
  <input type="button" class="pulsanti" onClick="cancellaFestivo(1)" value="Procedi">
  </td>
</tr>
<tr>
  <td colspan="2" align="left"><div class="sezione2">2. Cancella solo questo giorno</div></td>
</tr>
<tr>
  <td colspan="2" align="justify">
  Escludi il giorno selezionato (<%=data%>) dalla regola generale
  </td>
</tr>
<tr>
  <td colspan="2" align="center">
  <input type="button" class="pulsanti" onClick="cancellaFestivo(2)" value="Procedi">
  </td>
</tr>
<tr>
  <td colspan="2" align="left"><div class="sezione2">3. Fine della Regola</div></td>
</tr>
<tr>
  <td colspan="2" align="justify">
  Imposta il giorno selezionato (<%=data%>) come ultimo giorno di validit&agrave;
  della regola.
  </td>
</tr>
<tr>
  <td colspan="2" align="center">
  <input type="button" class="pulsanti" onClick="cancellaFestivo(3)" value="Procedi">
  </td>
</tr>

<tr>
  <td colspan="2" align="left"><div class="sezione2">&nbsp;</div></td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  <input type="button" class="pulsanti" onClick="chiudiDelFestivo()" value="Chiudi">
  </td>
</tr>
</table>
<%out.print(htmlStreamBottom);%>
</af:form>
</body>
</html>

