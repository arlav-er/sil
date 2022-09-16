<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%  
  // --- NOTE: Gestione Patto
  String PRG_TAB_DA_ASSOCIARE=null;
  String COD_LST_TAB="PR_IND";
  SourceBean row = null;
  // ---

  String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");

  BigDecimal prgIndisponibilita=new BigDecimal(0);
  BigDecimal prgAzienda=new BigDecimal(0);
  String strRagioneSociale= "";
  String strNota= "";
  BigDecimal cdnUtIns=new BigDecimal(0);
  Object dtmIns="";
  BigDecimal cdnUtMod=new BigDecimal(0);;
  Object dtmMod="";

  Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_LOADINDISP.ROWS.ROW");
  if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {

    SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);

    prgIndisponibilita        = (BigDecimal) beanLastInsert.getAttribute("PRGINDISPONIBILITA");
    prgAzienda = (BigDecimal) beanLastInsert.getAttribute("PRGAZIENDA");
      
    strRagioneSociale      = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRRAGSOCIALEAZIENDA");
    strNota=        StringUtils.getAttributeStrNotNull(beanLastInsert, "STRNOTA");
    cdnUtIns        = (BigDecimal)beanLastInsert.getAttribute("CDNUTINS");
    dtmIns          = beanLastInsert.getAttribute("DTMINS");
    cdnUtMod        = (BigDecimal)beanLastInsert.getAttribute("CDNUTMOD");
    dtmMod          = beanLastInsert.getAttribute("DTMMOD");    

    // --- NOTE: Gestione Patto
    PRG_TAB_DA_ASSOCIARE=prgIndisponibilita.toString();
    row = beanLastInsert;
    // ---
  }

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  Testata testata= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

  String _page = (String) serviceRequest.getAttribute("PAGE");
  
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(cdnLavoratore));

  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("salva");  
  String fieldReadOnly;
  /////////////////////////
    boolean readOnlyStr = !canModify;
  ///////////////////////////
  if (canModify) {fieldReadOnly="false";}
  else {fieldReadOnly="true";}
  
%>

<html>

<head>
  <title>Dettaglio Indisponibilità</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">
    var flagChanged = false; 
  
    function fieldChanged() {
      //alert("field changed !")    
      <% if ( canModify ) { %> 
        flagChanged = true;
      <% } %> 
    }

    // --- NOTE: Gestione Patto
    function getFormObj() {return document.Frm1;}
    <%@ include file="../patto/_sezioneDinamica_script.inc" %>
    <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
    // ---

  </SCRIPT>

  <%@ include file="Indisp_CommonScripts.inc" %>
  <%@ include file="../movimenti/MovimentiRicercaSoggetto.inc" %>
</head>

<body class="gestione" onload="rinfresca()">

  <%
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>

  <!-- --- NOTE: Gestione Patto
           aggiungere onSubmit="controllaPatto()" solo se non si ha 
           Salva_onClick(), altrimenti il controllo può essere fatto li 
  -->
  <script language="javascript">
    var flgInsert = false;
  </script>
  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="Salva_onClick() && controllaStatoAtto(flgInsert,this)">

    <input type="hidden" name="PAGE" value="IndispPage">
    <input type="hidden" name="MODULE" value="M_UpdateIndisp"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="PRGINDISPONIBILITA" value="<%= prgIndisponibilita %>"/>
    <input type="hidden" name="PRGAZIENDA" value="<%= prgAzienda %>"/>
    <input type="hidden" name="CDNUTINS" value="<%= cdnUtIns %>"/>
    <input type="hidden" name="DTMINS" value="<%= dtmIns %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>

    <!--<p class="titolo">Altri Crediti</p>-->

    <center>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>

    <p align="center">

    <table class="main">
      <tr>
        <td/></td>
      </tr>
      <tr>
        <td colspan="2">
          <center>
            <font color="green">
              <af:showMessages prefix="M_UpdateIndisp"/>
            </font>
          </center>
        </td>
      </tr>
      <%@ include file="Indisp_Elemento.inc" %>
      <tr>
        <!-- --- NOTE: Gestione Patto
        -->
        <td colspan="2">
          <%@ include file="../patto/_associazioneDettaglioXPatto.inc"%>
        </td>
        <!-- --- -->
      </tr>
    </table>
    <br/>
    <center>
      <% if ( canModify ) { %>    
        <input 
          class="pulsante" 
          type="submit" 
          name="salva" 
          value="Aggiorna"/>
      <%}%>
      <input class="pulsante" type="submit" name="annulla" 
        value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>"
        onclick="javascript:GoToMainPage();">
    </center>
    <br/>
    <center>
      <% testata.showHTML(out); %>
    </center>
  </af:form>
</body>

<SCRIPT TYPE="text/javascript">
  if ('<%=prgAzienda%>' == 'null') {
    document.Frm1.PRGAZIENDA.value = "";
  }
</SCRIPT>

</html>
