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
  it.eng.sil.security.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
  
  //Vector vectListaContrattiMansioni= serviceResponse.getAttributeAsVector("M_LISTCONTRATTIMANSIONI.ROWS.ROW");

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));

  // NOTE: Attributi della pagina (pulsanti e link) 
  //PageAttribs attributi = new PageAttribs(user, _page);
  //boolean canModify= attributi.containsButton("inserisci");
  //boolean canDelete= attributi.containsButton("rimuovi");

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
  //if(serviceResponse.containsAttribute("MDETTMOBGEOMANSIONE")) { nuovo = false; }
  //else { nuovo = true; }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=ContrattiMansioniPage" +
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + _funzione +
                     "&APRIDIV=1";
  Testata testata = new Testata(null, null, null, null);
  /*if(!nuovo) {
    // Sono in modalità "Dettaglio"
    SourceBean cont_dett = (SourceBean) serviceResponse.getAttribute("MDETTMOBGEOMANSIONE");
    SourceBean rowDett = (SourceBean) cont_dett.getAttribute("ROWS.ROW");
    BigDecimal prgMansD = (BigDecimal) rowDett.getAttribute("PRGMANSIONE");
    if(prgMansD!=null) { prgMansioneD = prgMansD.toString(); }
    descMansioneD = StringUtils.getAttributeStrNotNull(rowDett, "STRDESCRIZIONE");
    flgAutoD = StringUtils.getAttributeStrNotNull(rowDett, "FLGDISPAUTO");
    flgMotoD = StringUtils.getAttributeStrNotNull(rowDett, "FLGDISPMOTO");
    flgMezziPub = StringUtils.getAttributeStrNotNull(rowDett, "flgMezziPub");
    flgPendoD = StringUtils.getAttributeStrNotNull(rowDett, "FLGPENDOLARISMO");
    flgMobSettD = StringUtils.getAttributeStrNotNull(rowDett, "FLGMOBSETT");
    BigDecimal maxOreRow = (BigDecimal) rowDett.getAttribute("NUMOREPERC");
    if(maxOreRow != null) { maxOreD = maxOreRow.toString(); }
    codTrasfD = StringUtils.getAttributeStrNotNull(rowDett, "CODTRASFERTA");
    strNoteD = StringUtils.getAttributeStrNotNull(rowDett, "STRNOTE");

    BigDecimal cdnUtIns = (BigDecimal) rowDett.getAttribute("CDNUTINS");
    String dtmIns = (String) rowDett.getAttribute("DTMINS");
    BigDecimal cdnUtMod = (BigDecimal) rowDett.getAttribute("CDNUTMOD");
    String dtmMod = (String) rowDett.getAttribute("DTMMOD");

    testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
  } else {
    testata = new Testata(null, null, null, null);
  }*/
  /* TODO Commentare 
  Utils.dumpObject("cdnLavoratore", cdnLavoratore, out);
  Utils.dumpObject("CDUT", codiceUtenteCorrente, out);
  */
%>

<html>

<head>
  <title>Disponibilità di tipi di rapporto per mansioni</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js">
  </SCRIPT>

  <SCRIPT TYPE="text/javascript">
  var flagChanged = false;
  function ContrattoDelete(prgDisContratto, descContratto, descMansione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Eliminare il contratto\n" 
      + descContratto.replace(/\^/g, '\'') 
      + "\nper la mansione\n"
      + descMansione 
      + " ?" ;
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=ContrattiMansioniPage";
      s += "&MODULE=M_DeleteContrattoInMansione";
      s += "&PRGDISCONTRATTO=" + prgDisContratto;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
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
      alert("E' necessario scegliere\nalmeno una mansione\ne un contratto");
      return false;
    }
    return true;
  }
  
  function Salva_onClick() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if ( !IsElementiSelezionati('PRGMANSIONE') || 
         !IsElementiSelezionati('CODCONTRATTO')
    ) 
      return;
      
    doFormSubmit(document.MainForm);
}


//gestione checkbox
  function selDeselContratti()
  {
  	var coll = document.getElementsByName("SD_Contratto");
  	var b = coll[0].checked;
  	//alert(b);
  	var i;
  	coll= document.getElementsByName("CK_CANCCONTRATTO");
  	for(i=0; i<coll.length; i++) {
  		coll[i].checked = b;
  	}
  }
  
  function cancellaMassiva() {
  	
  	 if ( confirm("Eliminare tutti i tipi di rapporto selezionati?") ) {

      var s= "AdapterHTTP?PAGE=ContrattiMansioniPage&CANCELLAMASSIVA=true";
      
      var chkboxObjEval = document.getElementsByName("CK_CANCCONTRATTO");
      var chkboxObj=eval(chkboxObjEval);
      var strPrgDisContratto="";

      for(i=0; i<chkboxObj.length; i++) {
  		if(chkboxObj[i].checked) {
  			if(strPrgDisContratto.length>0) { strPrgDisContratto += ","; }
  			strPrgDisContratto += chkboxObj[i].value;
  		}
  	}
     
      s += "&PRGDISCONTRATTOCANCMASSIVA=" + strPrgDisContratto;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }



  </SCRIPT>
<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>

window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);     
</script>
</head>

<body class="gestione" onload="rinfresca()">

  <%
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>
  
  <!--form method="POST" action="AdapterHTTP" name="MainForm" id="MainForm"-->
  <af:form method="POST" action="AdapterHTTP" name="MainForm">
    <input type="hidden" name="PAGE" value="ContrattiMansioniPage">
    <input type="hidden" name="MODULE" value="M_InsertContrattoInMansione"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="CDNUTINS" value="<%= codiceUtenteCorrente %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>
    

   <p align="center">
      <font color="green">
        <af:showMessages prefix="M_InsertContrattoInMansione"/>
        <af:showMessages prefix="M_DELETECONTRATTOINMANSIONE"/>               
        <af:showMessages prefix="M_DeleteMASSIVAContrattoInMansione"/>
      </font>
      <font color="red">
        <af:showErrors />
      </font>
	</p>

    <div align="center">
    
<% if (canDelete) { %>    
	<table width="96%" align="center">
		<tr valign="middle">
			<td align="left" class="azzurro_bianco">
			<input type="checkbox" name="SD_Contratto" onClick="selDeselContratti();"/>&nbsp;Seleziona/Deseleziona tutti
			&nbsp;&nbsp;
			<button type="button" onClick="cancellaMassiva();" class="ListButtonChangePage">
			<img src="../../img/del.gif" alt="">&nbsp;Cancella selezionati
			</button>
			</td>
			<td>&nbsp;</td>
		</tr>
		</table>    
<% } %>    
         
      <af:list moduleName="M_LISTCONTRATTIMANSIONI" skipNavigationButton="1"
               canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
               canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
               jsSelect="" jsDelete="ContrattoDelete"
               configProviderClass="it.eng.sil.module.presel.DynListConfigListContrattiMansioni"
      />
    
    
    <%if(canInsert) {%>
          <br>
          <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuovo tipo di rapporto"/>        
    <%}%>
<!-- LAYER (solo in inserimento) -->
<%
String divStreamTop = StyleUtils.roundLayerTop(true);
String divStreamBottom = StyleUtils.roundLayerBottom(true);
%>   
    
    <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
     style="position:absolute; width:60%; left:150; top:100px; z-index:6; display:<%=apriDiv%>;">
     <a name="aLayerIns"></a>
  <!-- Stondature ELEMENTO TOP -->
  <%out.print(divStreamTop);%>

    <table width="100%">
      <tr>
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">
        <%if(nuovo){%>
          Nuovo Tipo di rapporto
        <%} else {%>
          Tipo di rapporto
        <%}%>   
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>
      
      <table width="60%" cellspacing="2">
      <%if(nuovo) {%>          
        <tr>
          <td class="etichetta">Mansioni</td>
          <td class="campo">
            <ps:mansioniComboBoxTag 
              name = "PRGMANSIONE" 
              moduleName= "M_ListMansioniDisponibileLavoro"/>
          </td>
        </tr>
      <%} else {%>
        <tr>
          <td class="etichetta">Mansione</td>
          <td class="campo"><%//=descMansioneD%></td>
        </tr>
        <input type="hidden" name="prgMansione" value="<%//=prgMansioneD%>">
      <%}%>      
        <tr>
          <td>
            &nbsp;
          </td>
        </tr>
        <tr>
          <td class="etichetta">Tipi di rapporti</td>
          <td class="campo">
            <af:comboBox name="CODCONTRATTO"
                         title="Tipi"
                         multiple="true"
                         size="17"
                         moduleName="M_ListContratti" />
          </td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <%if(nuovo) {%>
        <tr>
          <td colspan="2" align="center">
          <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Salva_onClick();">
          &nbsp;&nbsp;
          <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
          </td>
        </tr>
        <%}%>
        <%if(!nuovo && canModify) {%>
        <tr>
          <td colspan="2" align="center">
          <% if ( canModify ) { %>
            <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="SalvaMobGeo()">
          <% } %>
            <input type="button" 
              class="pulsanti" 
              name="annulla" 
              value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
              onClick="ChiudiDivLayer('divLayerDett')">
         
            &nbsp;&nbsp;
            <input type="reset" class="pulsanti" value="Annulla">-->
          </td>
        </tr>
        <%}%>
        <tr><td colspan="2" width="70%" align="left"><%testata.showHTML(out);%></td></tr>
      </table>
    </div>  
    <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>   
  <!--/form-->
  </af:form>
</body>

</html>

<% } %>