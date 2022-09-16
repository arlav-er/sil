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

  //Vector vectListaTurniMansioni= serviceResponse.getAttributeAsVector("M_LISTTURNIMANSIONI.ROWS.ROW");

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
  
  String url_nuovo = "AdapterHTTP?PAGE=TurniMansioniPage" +
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
  <title>Disponibilità di turni per mansioni</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
  var flagChanged = false;
  function TurnoDelete(prgDisTurno, descTurno, descMansione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Eliminare il turno\n" 
      + descTurno.replace(/\^/g, '\'') 
      + "\nper la mansione\n"
      + descMansione 
      + " ?" ;
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=TurniMansioniPage";
      s += "&MODULE=M_DeleteTurnoInMansione";
      s += "&PRGDISTURNO=" + prgDisTurno;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

  function Salva_onClick() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if ( !IsElementiSelezionati('PRGMANSIONE') || 
         !IsElementiSelezionati('CODTURNO')
    ) 
      return;

    doFormSubmit(document.MainForm);
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
      alert("E' necessario scegliere\nalmeno una mansione\ne un turno");
      return false;
    }
    return true;
  }
  
  
  
  //gestione checkbox
  function selDeselTurno()
  {
  	var coll = document.getElementsByName("SD_Turno");
  	var b = coll[0].checked;
  	//alert(b);
  	var i;
  	coll= document.getElementsByName("CK_CANCTURNO");
  	for(i=0; i<coll.length; i++) {
  		coll[i].checked = b;
  	}
  }
  
  function cancellaMassiva() {
  	
  	 if ( confirm("Eliminare tutti i turni selezionati?") ) {

      var s= "AdapterHTTP?PAGE=TurniMansioniPage&CANCELLAMASSIVA=true";

      var chkboxObjEval = document.getElementsByName("CK_CANCTURNO");           
      var chkboxObj=eval(chkboxObjEval);
      var strPrgDisTurno="";
      for(i=0; i<chkboxObj.length; i++) {
  		if(chkboxObj[i].checked) {
  			if(strPrgDisTurno.length>0) { strPrgDisTurno += ","; }
  			strPrgDisTurno += chkboxObj[i].value;
  		}
  	}
     
      s += "&PRGDISTURNOCANCMASSIVA=" + strPrgDisTurno;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
     
    }
  }
  
  
  
window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
  </SCRIPT>
<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer--> 
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>

</head>

<body class="gestione" onload="rinfresca()">

  <%
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>
  
  <af:form method="POST" action="AdapterHTTP" name="MainForm" id="MainForm" dontValidate="true">

    <input type="hidden" name="PAGE" value="TurniMansioniPage">
    <input type="hidden" name="MODULE" value="M_InsertTurnoInMansione"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="CDNUTINS" value="<%= codiceUtenteCorrente %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>

    <p align="center">
      <center>
        <font color="green">
          <af:showMessages prefix="M_InsertTurnoInMansione"/>
          <af:showMessages prefix="M_DeleteTurnoInMansione"/>
          <af:showMessages prefix="M_DeleteMASSIVATurnoInMansione"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
      <div align="center">
<% if (canDelete) { %>
	<table width="96%" align="center">
		<tr valign="middle">
			<td align="left" class="azzurro_bianco">
			<input type="checkbox" name="SD_Turno" onClick="selDeselTurno();"/>&nbsp;Seleziona/Deseleziona tutti
			&nbsp;&nbsp;
			<button type="button" onClick="cancellaMassiva();" class="ListButtonChangePage">
			<img src="../../img/del.gif" alt="">&nbsp;Cancella selezionati
			</button>
			</td>
			<td>&nbsp;</td>
		</tr>
		</table>
<% } %>
          
      <af:list moduleName="M_LISTTURNIMANSIONI" skipNavigationButton="1"
               canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
               canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
               jsSelect="" jsDelete="TurnoDelete"
               configProviderClass="it.eng.sil.module.presel.DynListConfigListTurniMansioni"
      />
    
    
    <%if(canInsert) {%>
          <br>
          <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuovo turno"/>        
    <%}%>
    </p>
<!-- LAYER (solo in inserimento) -->
<%
String divStreamTop = StyleUtils.roundLayerTop(true);
String divStreamBottom = StyleUtils.roundLayerBottom(true);
%>      
        
    <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
     style="position:absolute; width:60%; left:280; top:100px; z-index:6; display:<%=apriDiv%>;">
	<a name="aLayerIns"></a>
	
  <!-- Stondature ELEMENTO TOP -->
  <%out.print(divStreamTop);%>

    <table width="100%">
      <tr>
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">
        <%if(nuovo){%>
          Nuovo Turno
        <%} else {%>
          Turno
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
          <td class="etichetta">Turno</td>
          <td class="campo"><%//=descMansioneD%></td>
        </tr>
        <input type="hidden" name="prgMansione" value="<%//=prgMansioneD%>">
      <%}%>
        <tr>
          <td class="etichetta">Turni</td>
          <td class="campo">
            <af:comboBox name="CODTURNO"
                         title="Tipi"
                         multiple="true"
                         size="5"
                         moduleName="M_ListTurni" />
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
            <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="">
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
      </table>   
    </div>
    <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>           
  </af:form>
</body>

</html>

<% } %>