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
  // --- NOTE: Gestione Patto
  String PRG_TAB_DA_ASSOCIARE=null;
  SourceBean row = null;
  // ---
  
  String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");

 // Vector vectListaMansioni= serviceResponse.getAttributeAsVector("M_LISTMANSIONI.ROWS.ROW");

  boolean nuovaMansione= false;

  Object prgMansione= "",
         codMansione= "",
         cdnUtIns= "",
         dtmIns= "",
         cdnUtMod= "",
         dtmMod= "";
         
  String descMansione= "",
        desTipoMansione="",
         flgEsperienza= "",
         flgDisponibile= "",
         flgDispFormazione= "",
         flgEspForm= "",
         flgPianiInsProf= "",
         codMonoTempo= "",
         codTipoMisura= "";
  
  Vector vectMansione= serviceResponse.getAttributeAsVector("M_LOADMANSIONE.ROWS.ROW");
  if ( (vectMansione != null) && (vectMansione.size() > 0) ) {

    SourceBean beanLastInsert = (SourceBean)vectMansione.get(0);

    prgMansione       = beanLastInsert.getAttribute("PRGMANSIONE");
    codMansione       = beanLastInsert.getAttribute("CODMANSIONE");
    descMansione      = StringUtils.getAttributeStrNotNull(beanLastInsert, "DESC_MANSIONE");
    desTipoMansione   = StringUtils.getAttributeStrNotNull(beanLastInsert, "DESTIPOMANSIONE");
    flgEsperienza     = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGESPERIENZA");
    flgDisponibile    = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGDISPONIBILE");
    flgDispFormazione = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGDISPFORMAZIONE");
    flgEspForm        = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGESPFORM");
    flgPianiInsProf   = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGPIP");
    codMonoTempo      = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODMONOTEMPO");
    codTipoMisura     = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODTIPOMISURA");
    cdnUtIns          = beanLastInsert.getAttribute("CDNUTINS");
    dtmIns            = beanLastInsert.getAttribute("DTMINS");
    cdnUtMod          = beanLastInsert.getAttribute("CDNUTMOD");
    dtmMod            = beanLastInsert.getAttribute("DTMMOD");
    PRG_TAB_DA_ASSOCIARE=prgMansione.toString();
    row = beanLastInsert;
  /*  try {
        PRG_TAB_DA_ASSOCIARE=prgMansione.toString();
        PRG_PATTO_LAVORATORE=((BigDecimal)beanLastInsert.getAttribute("PRGPATTOLAVORATORE")).toString();
        PRG_LAV_PATTO_SCELTA=((BigDecimal)beanLastInsert.getAttribute("PRGLAVPATTOSCELTA")).toString();
        STATO_ATTO=(String) beanLastInsert.getAttribute("STATOATTO");
        DATA_STIPULA=(String) beanLastInsert.getAttribute("datastipula");
        TIPOLOGIA=(String) beanLastInsert.getAttribute("FLGPATTO297");        
        if (TIPOLOGIA!=null)
                TIPOLOGIA=TIPOLOGIA.equalsIgnoreCase("S")?"Patto 150":"Accordo Generico";
    }catch (NullPointerException npe) {}*/
  }

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  Testata testata= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(cdnLavoratore));

  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("salva");

  // --- NOTE: Gestione Patto
  String COD_LST_TAB="PR_MAN";
  boolean readOnlyStr = !canModify;
  // ---

  /* TODO Commentare 
  Utils.dumpObject("_funzione", new Integer(_funzione), out);
  Utils.dumpObject("cdnLavoratore", cdnLavoratore, out);
  Utils.dumpObject("CDUT", codiceUtenteCorrente, out);
  Utils.dumpObject("prgMansione",       prgMansione, out);
  Utils.dumpObject("codMansione",       codMansione, out);
  Utils.dumpObject("descMansione",      descMansione, out);
  Utils.dumpObject("flgEsperienza",     flgEsperienza, out);
  Utils.dumpObject("flgDisponibile",    flgDisponibile, out);
  Utils.dumpObject("flgDispFormazione", flgDispFormazione, out);
  Utils.dumpObject("flgEspForm",        flgEspForm, out);
  Utils.dumpObject("flgPianiInsProf",   flgPianiInsProf, out);
  Utils.dumpObject("codMonoTempo",      codMonoTempo, out);
  Utils.dumpObject("codTipoMisura",     codTipoMisura, out);
  Utils.dumpObject("cdnUtIns",          cdnUtIns, out);
  Utils.dumpObject("dtmIns",            dtmIns, out);
  Utils.dumpObject("cdnUtMod",          cdnUtMod, out);
  Utils.dumpObject("dtmMod",            dtmMod, out);
  Utils.dumpObject("testata",           testata, out);
  */
%>

<html>

<head>
  <title>Mansione</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">

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


   function getFormObj() {return document.Frm1;}
   <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
   <%@ include file="../patto/_sezioneDinamica_script.inc"%>

  </SCRIPT>

  <%@ include file="Mansioni_CommonScripts.inc" %>


</head>

<body class="gestione" onload="rinfresca()">

  <%
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>

  <af:form method="POST" action="AdapterHTTP" name="Frm1" id="Frm1" onSubmit="controllaPatto()" dontValidate="true">

    <input type="hidden" name="PAGE" value="MansioniPage">
    <input type="hidden" name="MODULE" value="M_UpdateMansione"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione %>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <!--<input type="hidden" name="CODMANSIONE" value="<%= codMansione %>"/>-->
    <input type="hidden" name="PRGMANSIONE" value="<%= prgMansione %>"/>

    <input type="hidden" name="CDNUTINS" value="<%= cdnUtIns %>"/>
    <input type="hidden" name="DTMINS" value="<%= dtmIns %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>

    <p class="titolo">Mansione</p>

    <center>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>

    <p align="center">

    <table class="main">
      <tr>
        <td/>
      </tr>
      <tr>
        <td colspan="2">
          <center>
            <font color="green">
              <af:showMessages prefix="M_UpdateMansione"/>
            </font>
          </center>
        </td>
      </tr>
      <%@ include file="Mansioni_Elemento.inc" %>
      <tr>
        <td colspan="2"><%@ include file="../patto/_associazioneDettaglioXPatto.inc"%></td>
    </tr> 
    </table>
    <br/>
    <center>
      <% if ( canModify ) { %>
        <input class="pulsante" type="submit" name="salva" value="Aggiorna">
      <% } %>
      <input 
        class="pulsante" 
        type="button" 
        name="annulla" 
        value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
        onclick="GoToMainPage()">
    </center>
    <br/>
    <center>
      <% testata.showHTML(out); %>
    </center>
  </af:form>
</body>

</html>
