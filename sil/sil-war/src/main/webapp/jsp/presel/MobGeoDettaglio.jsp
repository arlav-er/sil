<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  String _page = "MobilitaGeoPage";
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));

  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("salva");
%>

<%
  SourceBean content = (SourceBean) serviceResponse.getAttribute("MDETTMOBGEOMANSIONE");
  SourceBean row = (SourceBean) content.getAttribute("ROWS.ROW");
  BigDecimal prgMansioneD = (BigDecimal) row.getAttribute("PRGMANSIONE");
  String descMansione = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONE");
  String flgAutoD = StringUtils.getAttributeStrNotNull(row, "FLGDISPAUTO");
  String flgMotoD = StringUtils.getAttributeStrNotNull(row, "FLGDISPMOTO");
  String flgMezziPub=StringUtils.getAttributeStrNotNull(row, "flgMezziPub");
  String flgPendoD = StringUtils.getAttributeStrNotNull(row, "FLGPENDOLARISMO");
  String flgMobSettD = StringUtils.getAttributeStrNotNull(row, "FLGMOBSETT");
  BigDecimal maxOreRow = (BigDecimal) row.getAttribute("NUMOREPERC");
  String maxOreD = "";
  if(maxOreRow != null) { maxOreD = maxOreRow.toString(); }
  String codTrasfD = StringUtils.getAttributeStrNotNull(row, "CODTRASFERTA");
  String strNoteD = StringUtils.getAttributeStrNotNull(row, "STRNOTE");

  BigDecimal cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
  String dtmIns = (String) row.getAttribute("DTMINS");
  BigDecimal cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
  String dtmMod = (String) row.getAttribute("DTMMOD");

  Testata testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
%>


<html>

<head>
  <title>Disponibilit&agrave; sulla Mobilit&agrave; Geografica</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript" language="Javascript">
  
  function SalvaMobGeo()
  {
    var datiOk = controllaFunzTL();
    if (datiOk) {
      document.MainForm.MODULE.value = "MUpdMobGeoMansione";
      doFormSubmit(document.MainForm);
    }
  }

  function Chiudi()
  {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    document.MainForm.MODULE.value = "";
    document.MainForm.MODULE.disabled = true;
    doFormSubmit(document.MainForm);
  }

  // NOTE: Rilevazione Modifiche da parte dell'utente
  var flagChanged = false;
  
  function fieldChanged() {

    // DEBUG: Scommentare per vedere "field changed !" ad ogni cambiamento
    //alert("field changed !")  
    
    // NOTE: field-check solo se canModify 
    <% if ( canModify ) { %> 
      flagChanged = true;
    <% } %> 
  }

  </SCRIPT>

</head>

<body class="gestione">

  <%
    //infCorrentiLav.generaHTML(out); 
    infCorrentiLav.show(out);
    linguette.show(out);
  %>
  
  <af:form method="POST" action="AdapterHTTP" name="MainForm">

    <input type="hidden" name="PAGE" value="MobilitaGeoPage">
    <input type="hidden" name="MODULE" value=""/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
    <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore %>"/>

    <p class="titolo">Disponibilit&agrave; sulla Mobilit&agrave; Geografica</p>

    <p align="center">
    <table class="main">
    <input type="hidden" name="prgMansione" value="<%=prgMansioneD%>">
    <tr>
      <td class="etichetta">Mansione</td>
      <td class="campo"><%=descMansione%></td>
    </tr>
    <%@ include file="MobGeoDettaglio.inc" %>

    <tr>
      <td colspan="2" align="center">
      <% if ( canModify ) { %>
        <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="SalvaMobGeo()">
      <% } %>
        <input type="button" 
          class="pulsanti" 
          name="annulla" 
          value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
          onClick="Chiudi()">
        <!-- NOTE: "reset" commentato perchè non è implementata nelle altre pagine equivalenti a questa
                   sebbene sia una funzione utile,
             
        &nbsp;&nbsp;
        <input type="reset" class="pulsanti" value="Annulla">-->
      </td>
    </tr>
  </table>
  <BR/>
  <center>
    <% testata.showHTML(out); %>
  </center>
</af:form>
</body>

</html>
