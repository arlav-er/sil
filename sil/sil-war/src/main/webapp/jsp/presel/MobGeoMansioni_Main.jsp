<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
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
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));

  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("inserisci");
  boolean canDelete= attributi.containsButton("rimuovi");
%>

<%
  SourceBean content = (SourceBean) serviceResponse.getAttribute("MLISTMOBGEOMANSIONI");
  Vector rows = content.getAttributeAsVector("ROWS.ROW");
  String prgMansioneD = "";
  String flgAutoD = "";
  String flgMotoD = "";
  String flgPendoD = "";
  String flgMobSettD = "";
  String maxOreD = "";
  String codTrasfD = "";
  String strNoteD = "";
 
%>


<html>

<head>
  <title>Disponibilit&agrave; sulla Mobilit&agrave; Geografica per Mansioni</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript" language="Javascript">
  function InsMobGeo()
  {
    var datiOk = controllaFunzTL() && riportaControlloUtente( IsElementiSelezionati('PRGMANSIONE') );
    if (datiOk) {
      document.MainForm.MODULE.value = "MInsMobGeoMansione";
      doFormSubmit(document.MainForm);
    }
  }

  function DelMobGeo(prgMan, descMan) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Cancellare la disponibilità sulla mobilità geografica\n" 
      + "relativa alla mansione \""
      + descMan.replace(/\^/g, '\'') 
      + "\" ?" ;
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=MobilitaGeoPage";
      s += "&MODULE=MDelMobGeoMansione";
      s += "&PRGMANSIONE=" + prgMan;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

  function ModMobGeo(prgMan)
  {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
  
    var w = "AdapterHTTP?PAGE=MobGeoDettaglioPage";
    w += "&PRGMANSIONE=" + prgMan;
    w += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    w += "&CDNFUNZIONE=<%=_funzione%>";

    setWindowLocation(w);
  }

  // Verifica che ci sia almeno 
  // un elemento selezionato
  // nella lista a multiselezione.
  //
  // @param listName nome del controllo lista multiselezione 
  // @return true se c'è almeno una selezione.
  function IsElementiSelezionati(listName) {

    var isElemScelto= false;
    var elementi= document.forms[0].elements[listName];
    
    for (var i= 0; i < elementi.length; i++) {
      
      if ( elementi[i].selected ) {
        isElemScelto= true;
        break;
      }
    }
    
    if ( isElemScelto == false ) {
      alert("E' necessario scegliere\nalmeno una mansione");
      return false;
    }
    return true;
  }

  // NOTE: fieldChanged è presente senza fare nulla.
  // Non fa nulla perchè in questa pagina
  // non è supportata la notifica delle modifiche,
  // presente perchè usata in MobGeoDettaglio.inc
  function fieldChanged() {
  }
   window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
  </SCRIPT>
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>
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

    <center>
      <font color="green">
        <af:showMessages prefix="MInsMobGeoMansione"/>
      </font>
      <font color="red">
        <af:showErrors />
      </font>
    </center>

    <p align="center">
    <table class="main">
    <tr class="note">
      <!-- DISPONIBILITA' SULLA MOBILITA' GEOGRAFICA GIA' INSERITE -->
      <td align="center">
      <table width="100%" align="center">
      <%
        SourceBean row = null;
        String descMansione = "";
        String flgDispAuto = "";
        String flgDispMoto = "";
        String flgMezziPub ="";
        String flgPendolarismo = "";
        String flgMobSett = "";
        String descTrasferta = "";
        BigDecimal prgMan = new BigDecimal(0);
      %>
      <%for(int i =0; i < rows.size(); i++) {%>
          <%
            row = (SourceBean) rows.elementAt(i);
            descMansione = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONE");
            flgDispAuto = StringUtils.getAttributeStrNotNull(row, "FLGDISPAUTO");
            flgDispMoto = StringUtils.getAttributeStrNotNull(row, "FLGDISPMOTO");
            flgMezziPub = StringUtils.getAttributeStrNotNull(row, "FLGMEZZIPUB");
            flgPendolarismo = StringUtils.getAttributeStrNotNull(row, "FLGPENDOLARISMO");
            flgMobSett = StringUtils.getAttributeStrNotNull(row, "FLGMOBSETT");
            descTrasferta = StringUtils.getAttributeStrNotNull(row, "TRASFERTA");
            prgMan = (BigDecimal) row.getAttribute("PRGMANSIONE");
          %>
          <tr>
            <td class="lista" width="100%">
            <table width="100%">
            <tr class="note">
              <td>
                <a href="#" onClick="ModMobGeo('<%=prgMan%>')"><img src="../../img/detail.gif"></a>&nbsp;
              <% if ( canDelete ) { %>
                <a href="#" onClick="DelMobGeo('<%=prgMan%>', '<%= descMansione.replace('\'', '^') %>')"><img src="../../img/del.gif"></a>&nbsp;
              <% } %>
              </td>
            <tr class="note">
              <td>Mansione <b><%=descMansione%></b>
              <% if(flgDispAuto.equals("S")) { out.print("<br>Uso Automobile <b>Sì</b>"); } %>
              <% if(flgDispAuto.equals("N")) { out.print("<br>Uso Automobile <b>No</b>"); } %>
              <% if(flgDispMoto.equals("S")) { out.print("<br>Uso Motociclo <b>Sì</b>"); } %>
              <% if(flgDispMoto.equals("N")) { out.print("<br>Uso Motociclo <b>No</b>"); } %>
              <% if(flgPendolarismo.equals("S")) { out.print("<br>Pendolarismo <b>Sì</b>"); } %>
              <% if(flgPendolarismo.equals("N")) { out.print("<br>Pendolarismo <b>No</b>"); } %>
              <% if(flgMobSett.equals("S")) { out.print("<br>Mob. Settimanale: <b>Sì</b>"); } %>
              <% if(flgMobSett.equals("N")) { out.print("<br>Mob. Settimanale: <b>No</b>"); } %>
              <br>Trasferta <b><%=descTrasferta%></b>
              </td>
            </tr>
            </table>
            </td>
          </tr>
      <%}%>
      </table>
      </td>

    <% if ( canModify ) { %>
      <!-- NUOVA DISPONIBILITA' SULLA MOBILITA' GEOGRAFICA -->
      <td align="center" width="50%" frame="LHS">
        <b>Nuova disponibilit&agrave;</b><br>&nbsp;<br>
        <table width="100%" frame="LHS" rules="none" cellspacing="2">
          <tr>
            <td class="etichetta">Mansioni</td>
            <td class="campo">
              <ps:mansioniComboBoxTag 
                name = "PRGMANSIONE" 
                moduleName= "M_ListMansioniDisponibileLavoro"/>
            </td>
          </tr>
          <%@ include file="MobGeoDettaglio.inc" %>
    
          <tr>
            <td colspan="2" align="center">
            <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="InsMobGeo()">
            </td>
          </tr>
        </table>
      </td>
    <% } %>
  </tr>
  </table>
</af:form>
</body>

</html>
