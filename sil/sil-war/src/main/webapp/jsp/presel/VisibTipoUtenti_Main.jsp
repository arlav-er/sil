<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
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
  String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));

  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canInsert = attributi.containsButton("inserisci");
  boolean canDelete = attributi.containsButton("cancella");
  boolean canModify = attributi.containsButton("salva");

  boolean nuovo = true;
  //if(serviceResponse.containsAttribute("MDETTMOBGEOMANSIONE")) { nuovo = false; }
  //else { nuovo = true; }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=PreselVisibTipoUtentiPage" +
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + _funzione +
                     "&APRIDIV=1";
  Testata testata = new Testata(null, null, null, null);                     

  /* TODO: Commentare 
  Utils.dumpObject("cdnLavoratore", cdnLavoratore, out);
  Utils.dumpObject("CDUT", codiceUtenteCorrente, out);
  */
%>

<html>

<head>
  <title>Gestione Visibilit&agrave; curriculum</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
  var flagChanged = false;
  function Select(){
    return;
  }
  
  function Delete(prg, descrizione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Eliminare la visibilità per il tipo utente\n";    
    s = s + descrizione.replace(/\^/g, '\'') + " ?" ;
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=PreselVisibTipoUtentiPage";
      s += "&MODULE=PRESEL_M_DeleteVisibTipoUtenti";
      s += "&PRGVISIBTIPOGRUPPO=" + prg;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }  

  function Insert() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if ( document.Frm1.CDNTIPOGRUPPO.value == "" ) {

      alert("Selezionare un tipo utente");
      return;
    }
    
    document.Frm1.MODULE.value= "PRESEL_M_InsertVisibTipoUtenti";
    doFormSubmit(document.Frm1);
  }
  
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
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>
  
  <af:form method="POST" action="AdapterHTTP" name="Frm1" id="Frm1" dontValidate="true">

    <input type="hidden" name="PAGE" value="PreselVisibTipoUtentiPage">
    <input type="hidden" name="MODULE" value="PRESEL_M_InsertVisibTipoUtenti"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="CDNUTINS" value="<%= codiceUtenteCorrente %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>

    <p align="center">
      <center>
        <font color="green">
          <af:showMessages prefix="PRESEL_M_InsertVisibTipoUtenti"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
      <div align="center">
          
      <af:list moduleName="PRESEL_M_LISTVISIBTIPOUTENTI" skipNavigationButton="1"
               canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
               canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
               jsSelect="" jsDelete="Delete"
      />
    
    
    <%if(canInsert) {%>
          <br>
          <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuova visibilità"/>        
    <%}%>
    </p>
    <div id="divLayerDett" name="divLayerDett" class="layerDett"
     style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">

      <table width="100%">
        <tr width="100%">
          <td width="5%" class="menu"><img src="../../img/move.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
          <td>&nbsp;</td>
          <td width="5%" onClick="ChiudiDivLayer('divLayerDett')" class="menu"><img src="../../img/chiudi_menu.gif" alt="Chiudi"></td>
        </tr>
      </table>
      <%if(nuovo){%>
      <p class="titolo">Nuova visibilit&agrave;</p>
      <%} else {%>
      <p class="titolo">Visibilit&agrave;</p>
      <%}%>
      <table width="80%" cellspacing="2">
      <%if(nuovo) {%>          
        
        <tr>
          <td class="etichetta">Tipo utente</td>
          <td class="campo">
            <af:comboBox name="CDNTIPOGRUPPO"
                         title="Tipo utenti" required="true"
                         moduleName="M_GetPreselVisibTipoUtenti" addBlank="true" blankValue=""/>
          </td>
        </tr>        
      <%} else {%>
        
      <%}%>        
        <tr><td colspan="2">&nbsp;</td></tr>
        <%if(nuovo) {%>
        <tr>
          <td colspan="2" align="center">
          <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
          <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
          </td>
        </tr>
        <%}%>
        <%if(!nuovo && canModify) {%>
        <tr>
          <td colspan="2" align="center">
          <% if ( canModify ) { %>
            <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="">
          <% } %>
            <input type="button" 
              class="pulsanti" 
              name="annulla" 
              value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
              onClick="ChiudiDivLayer('divLayerDett')">
         
            &nbsp;&nbsp;
            <input type="reset" class="pulsanti" value="Annulla">
          </td>
        </tr>
        <%}%>
      </table>   
    </div>
  </af:form>
</body>

</html>
