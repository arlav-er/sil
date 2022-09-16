<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<!-- --- NOTE: Gestione Patto
-->
<%@ taglib uri="patto" prefix="pt" %>
<!-- --- -->
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
  it.eng.sil.security.ProfileDataFilter,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %> 

<%  
    ////////////////////////////////////////// prova gestione in pattoTemplate
    boolean onlyInsert = serviceRequest.getAttribute("ONLY_INSERT")==null?false:true;
        
    //////////////////////////////////////////
  // --- NOTE: Gestione Patto
  String PRG_TAB_DA_ASSOCIARE = null;
  SourceBean row = null;
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  String COD_LST_TAB = "PR_IND";
  // ---

  String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");

  Vector vectListaConoscenze = serviceResponse.getAttributeAsVector("M_LISTINDISP.ROWS.ROW");

  BigDecimal prgIndisponibilitaList=new BigDecimal(0);
  String strRagioneSocialeList= "";
  String strRagioneSociale= "";  
  String strNota= "";

  BigDecimal prgIndisponibilita=new BigDecimal(0);
  BigDecimal prgAzienda=new BigDecimal(0);
  BigDecimal cdnUtIns=new BigDecimal(0);
  //Object dtmIns="";
  String dtmIns = "";
  BigDecimal cdnUtMod=new BigDecimal(0);;
  Object dtmMod="";
  
  //Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

InfCorrentiLav infCorrentiLav=null;
Linguette linguette=null;

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  if (!onlyInsert) {
      infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
      linguette = new Linguette( user, _funzione, _page, new BigDecimal(cdnLavoratore));
  }

    ProfileDataFilter filter = new ProfileDataFilter(user, _page);
    filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
    boolean canView=filter.canViewLavoratore();
    if (! canView){
      response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
    }else{

      PageAttribs attributi = new PageAttribs(user, _page);
      boolean canInsert = attributi.containsButton("inserisci");
      boolean canDelete = attributi.containsButton("rimuovi");
      boolean canModify = attributi.containsButton("salva");

      if ( !canModify && !canInsert && !canDelete ) {
      
      } else {
        boolean canEdit = filter.canEditLavoratore();
        if ( !canEdit ) {
          canModify = false;
          canInsert = false;
          canDelete = false;
        }
      
      }
  
  boolean nuovo = true;

  if(serviceResponse.containsAttribute("M_LoadIndisp") && !onlyInsert) { nuovo = false; }
  else { nuovo = true; }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV") ;
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  apriDiv = (serviceRequest.containsAttribute("APRIDIV") || onlyInsert )?"":"none";
  String url_nuovo = "AdapterHTTP?PAGE=IndispPage" +
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + _funzione +
                     "&APRIDIV=1";

  if(!nuovo)
  {
  
      SourceBean dett = (SourceBean) serviceResponse.getAttribute("M_LoadIndisp");
      SourceBean rowDett = (SourceBean) dett.getAttribute("ROWS.ROW");
      prgIndisponibilita        = (BigDecimal) rowDett.getAttribute("PRGINDISPONIBILITA");
      prgAzienda = (BigDecimal) rowDett.getAttribute("PRGAZIENDA");
      strRagioneSociale      = StringUtils.getAttributeStrNotNull(rowDett, "STRRAGSOCIALEAZIENDA");
      strNota=        StringUtils.getAttributeStrNotNull(rowDett, "STRNOTA");
      cdnUtIns        = (BigDecimal)rowDett.getAttribute("CDNUTINS");
      dtmIns          = rowDett.getAttribute("DTMINS").toString();
      cdnUtMod        = (BigDecimal)rowDett.getAttribute("CDNUTMOD");
      dtmMod          = rowDett.getAttribute("DTMMOD");    
      // --- NOTE: Gestione Patto
      PRG_TAB_DA_ASSOCIARE=prgIndisponibilita.toString();
      row = rowDett;
  }

//  boolean canModify= attributi.containsButton("inserisci");
//  boolean canDelete= attributi.containsButton("rimuovi");  
//  boolean canInsert= attributi.containsButton("salva");  
  /////////////////////////
    boolean readOnlyStr = !canModify;
  ///////////////////////////
  String fieldReadOnly;
  
  if (canModify) {fieldReadOnly="false";}
  else {fieldReadOnly="true";}

  Testata operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod.toString());
 
%>

<html>

<head>
  <title>Indisponibilità</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/> 
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">
  <%@ include file="../patto/_sezioneDinamica_script.inc"%>
    var flagChanged = false;

<%if (onlyInsert) {%>
    function indietroPopUpAssociazione() {
        var urlpage="AdapterHTTP?";
        urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
        urlpage+="CDNFUNZIONE=<%=_funzione %>&";
        <%
        Object codsObj = serviceRequest.getAttribute("COD_LST_TAB");
        List codLstTab = new ArrayList();
        if (codsObj instanceof String) {
            codLstTab.add(codsObj);
        }
        else codLstTab=(List)codsObj;
        for (int i=0;i<codLstTab.size();i++) { %>
            urlpage+="COD_LST_TAB=<%=(String)codLstTab.get(i)%>&";
        <%}%>
        urlpage+="statoSezioni=<%=serviceRequest.getAttribute("statoSezioni")%>&";
        urlpage+="PAGE=AssociazioneAlPattoTemplatePage&";
        urlpage+="pageChiamante=<%=serviceRequest.getAttribute("pageChiamante")%>";
        window.open(urlpage,"_self");
    }
<%}%>
  

    // --- NOTE: Gestione Patto
    <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
    function getFormObj() {return document.Frm1;}
    // ---

    function IndispSelect(prgIndisp) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      var s= "AdapterHTTP?PAGE=IndispPage";
      s += "&MODULE=M_LoadIndisp";
      s += "&PRGINDISPONIBILITA=" + prgIndisp;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%= _funzione %>";
      s += "&APRIDIV=1";

      setWindowLocation(s);
    }

    function IndispDelete(prgIndisp, dettaglio, prgLavPattoScelta) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      var s="Eliminare l'azienda ";

      if ( (dettaglio != null) && (dettaglio.length > 0) ) {

        s += dettaglio.replace(/\^/g, '\'').replace(/\|/g, '\'');
      }

      s += " ?";
    
      if ( confirm(s) ) {

        var s= "AdapterHTTP?PAGE=IndispPage";
        s += "&MODULE=M_DeleteIndisp";
        s += "&PRGINDISPONIBILITA=" + prgIndisp;
        s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
        s += "&CDNFUNZIONE=<%=_funzione%>";
        // --- NOTE: Gestione Patto
        s += "&" + getParametersForPatto(prgLavPattoScelta);
        // ---

        setWindowLocation(s);
      }
    
    }

    function fieldChanged() {
      flagChanged = true;
    }


    function insertIndisp(){
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

      <% if (!onlyInsert) {%>
          var datiOk = controllaFunzTL();
          if(datiOk && riportaControlloUtente(Salva_onClick() && controllaStatoAtto(flgInsert,document.Frm1))){
            document.Frm1.action = "AdapterHTTP?inserisci=&";
            doFormSubmit(document.Frm1);
          }
          else return false;
      <%}else{%>
            document.Frm1.CDNFUNZIONE.value="<%=serviceRequest.getAttribute("cdnFunzione")%>";
            document.Frm1.PAGE.value="AssociazioneAlPattoTemplatePage";
            var datiOk = controllaFunzTL();
            if(datiOk && riportaControlloUtente(Salva_onClick())){ 
              document.Frm1.MODULE.value= "M_InsertIndisp";
              doFormSubmit(document.Frm1);
            }
            else return false;
      <%}%>
    }

    
if(window.top.menu != undefined){
  window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
}
</script>
<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>

  <%@ include file="Indisp_CommonScripts.inc" %>
  <%@ include file="../movimenti/MovimentiRicercaSoggetto.inc" %>
  
</head>

<body class="gestione" onload="rinfresca()">

<% if (!onlyInsert) { // se la richiesta proviene dal template allora non visualizzo la linguetta
    infCorrentiLav.show(out); 
    linguette.show(out);
  } %>

  <!-- --- NOTE: Gestione Patto
           aggiungere onSubmit="controllaPatto()" solo se non si ha 
           Salva_onClick(), altrimenti il controllo può essere fatto li 
  -->
  <script language="javascript">
    var flgInsert = <%=nuovo%>;
  </script>
 
  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="Salva_onClick() && controllaStatoAtto(flgInsert,this)" >

    <input type="hidden" name="PAGE" value="IndispPage">
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="PRGINDISPONIBILITA" value="<%= prgIndisponibilita %>"/>
    <input type="hidden" name="PRGAZIENDA" value="<%= prgAzienda %>"/>
    <input type="hidden" name="CDNUTINS" value="<%= cdnUtIns %>"/>
    <input type="hidden" name="DTMINS" value="<%= dtmIns %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>
    <input type="hidden" name="MODULE" value="" />
    
    <%
        if (onlyInsert) {
        // la richesta proviene dal template di associazione al patto
            Object cods = serviceRequest.getAttribute("COD_LST_TAB");
             List codLstTab = new ArrayList();
            if (cods instanceof String) {
                codLstTab.add(cods);
            }
            else codLstTab=(List)cods;
            for (int i=0;i<codLstTab.size();i++) {
        %>
               <input type="hidden" name="COD_LST_TAB" value="<%= (String)codLstTab.get(i) %>"/>
            <%} // for %>
           <input type="hidden" name="pageChiamante" value="<%=(String)serviceRequest.getAttribute("pageChiamante")%>"/>
           <input type="hidden" name="statoSezioni" value="<%= it.eng.sil.util.Utils.notNull((String)serviceRequest.getAttribute("statoSezioni")) %>"/>
        <%} // if %>             

    <p align="center">
    <center>
      <af:showMessages prefix="M_InsertIndisp"/>
      <af:showMessages prefix="M_DeleteIndisp"/>
      <af:showMessages prefix="M_UpdateIndisp"/>
      
      <af:showErrors/>
    </center>

    <div align="center">
    <%if (!onlyInsert) { %>
      <af:list moduleName="M_ListIndisp" skipNavigationButton="1"
             canDelete="<%=canDelete ? \"1\" : \"0\"%>"  
             jsSelect="IndispSelect" jsDelete="IndispDelete"/>
    <%}%>

    <%if(canInsert && !onlyInsert) {%>
          <br>
          <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuova indisponibilità"/>        
    <%}%>
    </div>
    <p/>

<!-- LAYER -->
<%
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>    
    <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
     style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
	<a name="aLayerIns"></a>
	
    <!-- Stondature ELEMENTO TOP -->
    <%out.print(divStreamTop);%>
    
    
      <table width="100%">
        <tr width="100%">
          <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
          <td height="16" class="azzurro_bianco">
          <%if(nuovo){%>
                Nuova indisponibilità
          <%} else {%>
                Indisponibilità
          <%}%>
          </td>
          <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
        </tr>
      </table>
      <br>
      <table class="main">
        <%@ include file="Indisp_Elemento.inc" %>
        <!-- NOTE: Gestione Patto
        -->
        <tr>
          <td colspan=2>
          <% if (!onlyInsert) { %>
            <%@ include file="../patto/_associazioneDettaglioXPatto.inc" %>
          <%}%>
          </td>
        </tr>

        <%if(nuovo) {%>
        <tr>
          <td colspan="2" align="center">
          <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="insertIndisp();">
          <%if (onlyInsert) {
                // mostro il pulsante per tornare indietro alla pagina chiamante
            %>          
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" onclick="indietroPopUpAssociazione()" class="pulsanti" value="Indietro">
            <%} else {%>
          <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
          <%}%>
          </td>
        </tr>
        <%}%>
        <%if(!nuovo && canModify) {%>
        <tr>
          <td colspan="2" align="center">
          <% if ( canModify ) { %>
            <input type="submit" class="pulsanti" name="salva" value="Aggiorna">
          <% } %>
            <input type="button" 
              class="pulsanti" 
              name="annulla" 
              value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
              onClick="ChiudiDivLayer('divLayerDett')">
          </td>
        </tr>
        <%}%>
      </table>
      <%operatoreInfo.showHTML(out);%>
      <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>
    </div>
  </af:form>
</body>

</html>

<% } %>