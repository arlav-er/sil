<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="patto" prefix="pt" %>
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
    ////////////////////////////////////////// prova gestione in pattoTemplate
    boolean onlyInsert = serviceRequest.getAttribute("ONLY_INSERT")==null?false:true;
        
    //////////////////////////////////////////
    String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");

	SourceBean ultimoPattoAperto = (SourceBean)serviceResponse.getAttribute("M_GetPattoLav.ROWS.ROW");  
    Vector vectListaMansioni= serviceResponse.getAttributeAsVector("M_LISTMANSIONI.ROWS.ROW");

    String cdnUtins="";
    String dtmins="";
    String cdnUtmod="";
    String dtmmod="";

    //Object prgMansione= serviceRequest.getAttribute("PRGMANSIONE");
    boolean nuovaMansione= true;

    String codMansione= "",
         descMansione= "",
         desTipoMansione="",
         flgEsperienza= "",
         flgDisponibile= "",
         flgDispFormazione= "",
         flgEspForm= "",
         flgPianiInsProf= "",
         prgMansione="",
         codMonoTempo= "",
         strNote ="";
         
    String PRG_TAB_DA_ASSOCIARE = null;      
    SourceBean row = null;
    Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

    InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

    String _page = (String) serviceRequest.getAttribute("PAGE");
    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(cdnLavoratore));

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
    boolean trovataEsp = false;

    if(serviceResponse.containsAttribute("M_LoadMansione") && !onlyInsert) { nuovo = false; }
    else { nuovo = true; }

    String apriDiv = (String) serviceRequest.getAttribute("APRIDIV") ;
    if(apriDiv == null) { apriDiv = "none"; }
    else { apriDiv = ""; }
    apriDiv = (serviceRequest.containsAttribute("APRIDIV") || onlyInsert )?"":"none";
    String url_nuovo = "AdapterHTTP?PAGE=MansioniPage" +
                       "&CDNLAVORATORE=" + cdnLavoratore +
                       "&CDNFUNZIONE=" + _funzione +
                       "&APRIDIV=1";
                       
    if(!nuovo) {
        // Sono in modalità "Dettaglio"
        SourceBean dett = (SourceBean) serviceResponse.getAttribute("M_LoadMansione");
        SourceBean rowDett = (SourceBean) dett.getAttribute("ROWS.ROW");
        codMansione = StringUtils.getAttributeStrNotNull(rowDett, "codMansione");
        desTipoMansione = StringUtils.getAttributeStrNotNull(rowDett, "desTipoMansione");
        descMansione = StringUtils.getAttributeStrNotNull(rowDett, "desc_Mansione");
        flgEsperienza = StringUtils.getAttributeStrNotNull(rowDett, "flgEsperienza");
        flgEspForm = StringUtils.getAttributeStrNotNull(rowDett, "flgEspForm");
        flgDisponibile = StringUtils.getAttributeStrNotNull(rowDett, "flgDisponibile");
        codMonoTempo = StringUtils.getAttributeStrNotNull(rowDett, "codMonoTempo");
        flgDispFormazione = StringUtils.getAttributeStrNotNull(rowDett, "flgDispFormazione");
        flgPianiInsProf = StringUtils.getAttributeStrNotNull(rowDett, "flgPianiInsProf");
        strNote = StringUtils.getAttributeStrNotNull(rowDett, "strNote");
        // patto 150
        row = rowDett;
        prgMansione = it.eng.sil.util.Utils.notNull(row.getAttribute("PRGMANSIONE"));
        PRG_TAB_DA_ASSOCIARE = prgMansione;

        SourceBean checkEsp    = (SourceBean) serviceResponse.getAttribute("M_CheckEspLavMans");
        if(checkEsp.containsAttribute("ROWS.ROW")) trovataEsp = true;

        SourceBean row_mansione = (SourceBean) serviceResponse.getAttribute("M_LOADMANSIONE.ROWS.ROW");
        cdnUtins = row_mansione.containsAttribute("cdnUtins") ? row_mansione.getAttribute("cdnUtins").toString() : "";
        dtmins   = row_mansione.containsAttribute("dtmins") ? row_mansione.getAttribute("dtmins").toString() : "";
        cdnUtmod = row_mansione.containsAttribute("cdnUtmod") ? row_mansione.getAttribute("cdnUtmod").toString() : "";
        dtmmod   = row_mansione.containsAttribute("dtmmod") ? row_mansione.getAttribute("dtmmod").toString() : "";
   }              
        // --- NOTE: Gestione Patto

    String COD_LST_TAB="PR_MAN";
    boolean readOnlyStr = !canModify;
    // ---

    /* TODO Commentare 
    Utils.dumpObject("cdnLavoratore", cdnLavoratore, out);
    Utils.dumpObject("CDUT", codiceUtenteCorrente, out);
    */
    Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
    
    String ControllaCdn="";
    Vector rowDispo = serviceResponse.getAttributeAsVector("M_GETLAVCM.ROWS.ROW");
	if (rowDispo.size()>0) {
		SourceBean sb1 = (SourceBean)rowDispo.get(0);
		ControllaCdn = sb1.containsAttribute("CONTROLLACDN")? sb1.getAttribute("CONTROLLACDN").toString():"";
   	}
%>

<html>

<head>
  <title>Mansioni - Preselezione</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">
  <%@ include file="../patto/_sezioneDinamica_script.inc"%>
  <%if (onlyInsert) {
        // questa funzione js potra' essere chiamata soltanto quando la pagina viene chiamata dal template di associazione al patto
  %>
	    
    function indietroPopUpAssociazione() {
        var urlpage="AdapterHTTP?";
        urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
        urlpage+="CDNFUNZIONE=<%=_funzione %>&";
        <%Object codsObj = serviceRequest.getAttribute("COD_LST_TAB");
        List codLstTab = new ArrayList();
        if (codsObj instanceof String) {
            codLstTab.add(codsObj);
        }
        else codLstTab=(List)codsObj;
        for (int i=0;i<codLstTab.size();i++) {%>
            urlpage+="COD_LST_TAB=<%=(String)codLstTab.get(i)%>&";
        <%}%>
        urlpage+="statoSezioni=<%=serviceRequest.getAttribute("statoSezioni")%>&";
        urlpage+="PAGE=AssociazioneAlPattoTemplatePage&";
        urlpage+="pageChiamante=<%= serviceRequest.getAttribute("pageChiamante")%>";
        window.open(urlpage,"_self");
    }
    <%}%>
    
  // Rilevazione Modifiche da parte dell'utente
  var flagChanged = false;
  var flgOnsubmitOk = true;     

  function Select(prgMansione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=MansioniPage";
    s += "&MODULE=M_LoadMansione";
    s += "&PRGMANSIONE=" + prgMansione;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s += "&APRIDIV=1";
    setWindowLocation(s);
    
  }

  function Delete(prgMansione, descrizione, prgLavPattoScelta, espCollegata) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

	if (espCollegata == 'TRUE') {
	  var s="Cancellando la mansione, verranno cancellate\r\nanche tutte le esperienze corrispondenti.\r\n\r\nContinuare?";
	} else {
      var s="Eliminare la mansione\n" + descrizione.replace(/\^/g, '\'') + " ?";
    }
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=MansioniPage";
      s += "&MODULE=M_DeleteMansione";
      s += "&PRGMANSIONE=" + prgMansione;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";
      s+="&";
      s+=getParametersForPatto(prgLavPattoScelta);
      setWindowLocation(s);
    }
  }

  function controllaCodiceMansione (codMansione) {
    var strCodMansione = new String(codMansione);
    if (strCodMansione.substring(strCodMansione.length-2,strCodMansione.length) != '00') {
      return true;
    }
    else {
      return false;
    }
  }

  function checkDispLavorare() {
  	if(flgOnsubmitOk && document.Frm1.FLGDISPONIBILE.value == ''){
  		alert ("Attenzione: non si è indicato se il lavoratore è disponibile a lavorare con la mansione.");
  	}
  }

  function Insert() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	flgOnsubmitOk = true;
	if (isInSubmit()) return;
	
	var strCaptionMan = 'Non è stata indicata una mansione specifica, continuare ?';
	<% if (onlyInsert) {%>
        document.Frm1.CDNFUNZIONE.value="<%=serviceRequest.getAttribute("cdnFunzione")%>";
        document.Frm1.PAGE.value="AssociazioneAlPattoTemplatePage";
        if ( document.Frm1.CODMANSIONE.value.length == 0 ) {
          alert("Prima è necessario selezionare una mansione");
          flgOnsubmitOk = false;
          return;
        }
    	flgLav = document.Frm1.FLGDISPONIBILE.value;
 		if (flgLav == 'L' && document.Frm1.ControllaCdn.value==""){
    		alert("Il lavoratore non è in collocamento mirato!");
    	 	undoSubmit();
			flgOnsubmitOk = false;
    		return false;
   		}
        if ( document.Frm1.CODMANSIONE.value.length != 0 ) {
          if (controllaCodiceMansione(document.Frm1.CODMANSIONE.value)) {
            document.Frm1.MODULE.value= "M_InsertMansione";
            if( controllaFunzTL() ) doFormSubmit(document.Frm1);  
            else return false;
          }
          else {
            if (confirm(strCaptionMan)) {
              document.Frm1.MODULE.value= "M_InsertMansione";
              if( controllaFunzTL() ) doFormSubmit(document.Frm1);  
              else return false;
            }
          }                 
        }
    <%} else { %>
      if ( document.Frm1.CODMANSIONE.value.length == 0 ) {
        alert("Prima è necessario selezionare una mansione");
        flgOnsubmitOk = false;
        return;
      }
      flgLav = document.Frm1.FLGDISPONIBILE.value;
 	  if (flgLav == 'L' && document.Frm1.ControllaCdn.value==""){
    	  alert("Il lavoratore non è in collocamento mirato!");
    	  return false;
   	  }
      if ( document.Frm1.CODMANSIONE.value.length != 0 ) {
        if (controllaCodiceMansione(document.Frm1.CODMANSIONE.value)) {
          document.Frm1.MODULE.value= "M_InsertMansione";
          if(controllaFunzTL() && 
             riportaControlloUtente( controllaPatto() && controllaStatoAtto(flgInsert,document.Frm1) ) ) {
             doFormSubmit(document.Frm1);
          }
          else
          return false; 
        }
        else {
          if (confirm(strCaptionMan)) {
            document.Frm1.MODULE.value= "M_InsertMansione";
            if(controllaFunzTL() && 
               riportaControlloUtente( controllaPatto() && controllaStatoAtto(flgInsert,document.Frm1) ) ) {
               doFormSubmit(document.Frm1);
            }
            else
            return false; 
          }
        }
      }
  <%}%>
  <%
  	if(ultimoPattoAperto!=null){
  		String codStatoUltimoPatto = StringUtils.getAttributeStrNotNull(ultimoPattoAperto,"codStatoAtto");
  		if(codStatoUltimoPatto.equals("PR")){ %>
  			var visualizzaMsg = false;
  			if(document.Frm1.FLGDISPONIBILE.value == 'S'){ 
  				var tmp = '<%=flgDisponibile%>';
  				if(tmp != document.Frm1.FLGDISPONIBILE.value){
  					visualizzaMsg = true;
  				}
  			}else 
  				if(document.Frm1.FLGDISPFORMAZIONE.value == 'S'){
  					var tmp = '<%=flgDispFormazione%>';
	  				if(tmp != document.Frm1.FLGDISPFORMAZIONE.value){
	  					visualizzaMsg = true;
	  				}
  				}	
  			if(visualizzaMsg)
				alert("Si sta inserendo una mansione che verrà automaticamente associata al patto.");
  	  <%}
  	}
 	%>
}



/* obsoleta
  function TipiMansione_onClick(codTipoMansione) {

    if (codTipoMansione != "")
      window.open("AdapterHTTP?PAGE=CurrAlberoMansioniPage&padre="+codTipoMansione.value, "Tipi", 'toolbar=0, scrollbars=1');
  }

*/
function Update() {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  flgOnsubmitOk = true;
  if (isInSubmit()) return;
  
 var datiOk = controllaFunzTL();
  var strCaptionMan = 'Non è stata indicata una mansione specifica, continuare ?';
  if (document.Frm1.CODMANSIONE.value.length == 0 ) {
    alert("Prima è necessario selezionare una mansione");
    undoSubmit();
	flgOnsubmitOk = false;
    return false;
  }
  flgLav = document.Frm1.FLGDISPONIBILE.value;
  if (flgLav == 'L' && document.Frm1.ControllaCdn.value==""){
  	alert("Il lavoratore non è in collocamento mirato!");
    undoSubmit();
	flgOnsubmitOk = false;
    return false;
  }
  if (document.Frm1.CODMANSIONE.value.length != 0 ) {
    if (controllaCodiceMansione(document.Frm1.CODMANSIONE.value)) {
      if (datiOk) {
        document.Frm1.MODULE.value = "M_UpdateMansione";
        if (controllaPatto() && controllaStatoAtto(flgInsert,document.Frm1))
          doFormSubmit(document.Frm1);
        else { undoSubmit(); return false; }
      }
    }
    else {
      if (confirm(strCaptionMan)) {
        if (datiOk) {
          document.Frm1.MODULE.value = "M_UpdateMansione";
          if (controllaPatto() && controllaStatoAtto(flgInsert,document.Frm1))
            doFormSubmit(document.Frm1);
          else { undoSubmit(); return false; }
        }
      }
    }
  }
}

  function Salva_onClick() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	flgOnsubmitOk = true;
	if (isInSubmit()) return;

    <% if (onlyInsert) {%>
        document.Frm1.CDNFUNZIONE.value="<%=serviceRequest.getAttribute("cdnFunzione")%>";
        document.Frm1.PAGE.value="AssociazioneAlPattoTemplatePage";
        if ( document.Frm1.CODMANSIONE.value.length == 0 ) {
          alert("Prima è necessario selezionare una mansione");
          flgOnsubmitOk = false;
          return;
        }    
        doFormSubmit(document.Frm1);  
        return;
    <%} else { %>
    if ( document.Frm1.CODMANSIONE.value.length == 0 ) {

      alert("Prima è necessario selezionare una mansione");
      flgOnsubmitOk = false;
      return;
    }
    if (!controllaPatto())  return;
    doFormSubmit(document.Frm1);    
    <%}%>
  }

  function Tipi_onChange() {

    // Cambiando tipo il dettaglio non corrisponde più
    // quindi azzero la scelta fatta
    Frm1.CODMANSIONE.value= "";
    Frm1.DESCMANSIONE.value= "";
  }

  // NOTE: fieldChanged è presente senza fare nulla.
  // Non fa nulla perchè in questa pagina
  // non è supportata la notifica delle modifiche,
  // presente perchè usata in ConoInfo_CommonScripts
  //??? Perchè ??? Davide (io l'abilito)
  function fieldChanged() {
   flagChanged = true;
  }
 function getFormObj() {return document.Frm1;}
 
     <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
     if(window.top.menu != undefined){
       window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
     }
    function associazioneAlPattoPossibile() { 
    	dispLavoro = document.Frm1.FLGDISPONIBILE.value;
    	dispFormazione = document.Frm1.FLGDISPFORMAZIONE.value;
    	if (dispFormazione != 'S') {
    		if (dispLavoro != 'S' && dispLavoro != 'P') {
    			return "associazione al patto non possibile: dare almeno una disponibilità";
    		} 
    	}
    	<%if (Sottosistema.CM.isOn()) {%>
    	document.Frm1.FLGDISPONIBILEHID.value = dispLavoro;
    	<%}%>
    	return null;
    }
    function controlloAssPatto(flgObj) {
    	<%if (!onlyInsert) {%>
    	var flgtoset = '';
    	var flgLavHid = 'S';
    	flgLav = document.Frm1.FLGDISPONIBILE.value; 
    	flgForm = document.Frm1.FLGDISPFORMAZIONE.value;
    	<%if (Sottosistema.CM.isOn()) {%>
    	flgLavHid = document.Frm1.FLGDISPONIBILEHID.value;
    	<%}%>
    	if (flgObj == document.Frm1.FLGDISPONIBILE) {
    		flgtoset = flgLavHid;
    	} 
    	else if (flgObj == document.Frm1.FLGDISPFORMAZIONE) {
    		flgtoset = 'S';
    	}
    	
    	if (((flgLav!='S' && flgLav!='P') && flgForm!='S') && document.Frm1.PRG_PATTO_LAVORATORE.value!=""){
    		// 
    		alert("Impossibile togliere la disponibilità all'ordinario se la mansione e' associata al patto");
    		flgObj.value=flgtoset;
    		return false;
    	}
    	<%}%>
    }
    
  </SCRIPT>

  <script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>

  <%@ include file="Mansioni_CommonScripts.inc" %>

    
</head>

<body class="gestione" onload="rinfresca()">

<% 
    if (!onlyInsert) {
        infCorrentiLav.show(out); 
        linguette.show(out);
    } 
%>
  

  <script language="javascript">
    var flgInsert = <%=nuovo%>;
  </script>
  <af:form method="POST" action="AdapterHTTP" name="Frm1" id="Frm1" dontValidate="true"> <!-- return false -->

    <input type="hidden" name="PAGE" value="MansioniPage">
    <input type="hidden" name="MODULE" value="M_InsertMansione"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione %>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="CDNUTINS" value="<%= codiceUtenteCorrente %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>
    <input type="hidden" name="PRGMANSIONE" value="<%= prgMansione %>"/>
    <input type="hidden" name="ControllaCdn" value="<%=ControllaCdn%>">
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
        <font color="green">
          <af:showMessages prefix="M_InsertMansione"/>
          <af:showMessages prefix="M_DeleteMansione"/>
          <af:showMessages prefix="M_UpdateMansione"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
      <div align="center">
    <%if (!onlyInsert) { %>
      <af:list moduleName="M_ListMansioni" skipNavigationButton="1"
               canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
               canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
               jsSelect="Select" jsDelete="Delete"
      />
    <%}%>
    
    <%if(canInsert && !onlyInsert) {%>
          <br>
          <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuova mansione"/>        
    <%}%>
    </div>
    </p>



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
          <td class="azzurro_bianco" height="16">
          <%if(nuovo){%>
            Nuova mansione
          <%} else {%>
            Mansione
          <%}%>
          </td>
          <td width="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
        </tr>
      </table>
      <br>
      <table width="80%" cellspacing="2">
  <tr valign="top">
      <td class="etichetta">Ricerca limitata alle sole mansioni di uso frequente</td>
      <td class="campo">
        <% if (nuovo) { %>
          <input type="checkbox" name="flgFrequente" value="" checked="true"/>        
        <% } else { %>
          <input type="checkbox" name="flgFrequente" value="" checked="true" <% if (readOnlyStr) out.print("disabled"); %>/>
        <% } %>
      </td>
  </tr>
 <tr>
    <td class="etichetta">Codice mansione</td>
    <td class="campo">
      <af:textBox 
        classNameBase="input" 
        name="CODMANSIONE" 
        size="7" 
        maxlength="7" 
        value="<%= codMansione.toString() %>" 
        readonly="<%= String.valueOf(!canModify) %>" 
      />
      
      <af:textBox 
        type="hidden" 
        name="codMansioneHid" 
        value="<%= codMansione.toString() %>" 
      />
      
      <% if (canModify) { %>
          <a href="javascript:selectMansione_onClick(Frm1.CODMANSIONE, Frm1.codMansioneHid, Frm1.DESCMANSIONE,  Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
          <A href="javascript:ricercaAvanzataMansioni();">
              Ricerca avanzata
          </A>
      <%}%>
    </td>
  </tr>           
<tr valign="top">
    <td class="etichetta">Tipo</td>
    <td class="campo">
      <af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
      <af:textBox classNameBase="input" name="strTipoMansione" value="<%=desTipoMansione%>" readonly="true" size="48" />
    </td>
  </tr>
<tr>
  <td class="etichetta">Descrizione</td>
  <td class="campo">
      <af:textArea cols="30" 
                   rows="2" 
                   name="DESCMANSIONE" 
                   classNameBase="textarea"
                   readonly="true" 
                   required="true"
                   maxlength="100"
                   value="<%= descMansione %>" />
  </td>
</tr>
<tr>
  <td>&nbsp;</td>
</tr>
<tr>
  <td class="etichetta">Esperienza ?</td>
  <td>
    <table>
      
      <td class="campo">
        <af:comboBox
          title="Esperienza"
          name="FLGESPERIENZA"
          classNameBase="input"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged()"
        >
          <option value=""  <% if ( "".equals(flgEsperienza) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(flgEsperienza) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
          <%if(!trovataEsp){%>
          <option value="N" <% if ( "N".equals(flgEsperienza) ) { out.print("SELECTED=\"true\""); } %> >No</option>
          <%}%>
          <option value="E" <% if ( "E".equals(flgEsperienza) ) { out.print("SELECTED=\"true\""); } %> >Non documentata</option>
        </af:comboBox>
      </td>
      
      <td class="etichetta">Esperienza formativa ?</td>
      <td class="campo">
        <af:comboBox 
          title="Esperienza formativa"
          name="FLGESPFORM"
          classNameBase="input"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged()"
        >
          <option value=""  <% if ( "".equals(flgEspForm) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(flgEspForm) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
          <option value="N" <% if ( "N".equals(flgEspForm) ) { out.print("SELECTED=\"true\""); } %> >No</option>
        </af:comboBox>
      </td>
    </table>
  </td>
</tr>

<tr>
  <td class="etichetta">Note<br/>(per invio a Cliclavoro)</td>
  <td class="campo">
      <af:textArea cols="30" 
                   rows="2" 
                   name="strNote" 
                   classNameBase="textarea"
                   readonly="<%= String.valueOf( !canModify ) %>" 
                   required="false"
                   maxlength="300"
                   value="<%= strNote %>" />
  </td>
</tr>

<tr>
  <td colspan="2" >
    <br/>
    <b>&nbsp;&nbsp;Disponibilit&agrave;</b>
    <br/>
    <hr width="90%"/>
  </td>
</tr>

<tr>
  
  <%
  // INIT-PARTE-TEMP
  if (Sottosistema.CM.isOff()) {	
  // END-PARTE-TEMP
  %>
  <td class="etichetta">Disponibile a lavorare con la mansione ?</td>
  <%}else{%>
  <td class="etichetta">Disponibile a lavorare con: </td>
  <%}%>
  
  <td>
    <table>
      <td class="campo">

    <%
    // INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) {	
	// END-PARTE-TEMP
    %>
        <af:comboBox 
          title="Disponibile a lavorare con la mansione"
          name="FLGDISPONIBILE"
          classNameBase="input"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged();controlloAssPatto(this)">
          <option value=""  <% if ( "".equals(flgDisponibile) )   { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Sì</option>
          <option value="N" <% if ( "N".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >No</option>
        </af:comboBox>
    
	<%}else{%>        
        <af:comboBox 
          title="Disponibile a lavorare con la mansione"
          name="FLGDISPONIBILE"
          classNameBase="input"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged();controlloAssPatto(this);">
          <option value=""  <% if ( "".equals(flgDisponibile) )   { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="P" <% if ( "P".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Preselezione ordinaria</option>
          <option value="L" <% if ( "L".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Legge 68</option>
          <option value="S" <% if ( "S".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Entrambe</option>
          <option value="N" <% if ( "N".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Nessuna</option>
        </af:comboBox>
        <af:textBox type="hidden" name="FLGDISPONIBILEHID" value="<%=flgDisponibile%>" />
    <%}%>
  <!--    <td class="etichetta">Mansione a tempo </td>
      <td class="campo">
        <af:comboBox 
          title="Mansione a tempo"
          name="CODMONOTEMPO"
          classNameBase="combobox"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged()"
        >
          <option value=""  <% if ( "".equals(codMonoTempo) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="D" <% if ( "D".equals(codMonoTempo) ) { out.print("SELECTED=\"true\""); } %> >Determinato</option>
          <option value="I" <% if ( "I".equals(codMonoTempo) ) { out.print("SELECTED=\"true\""); } %> >Indeterminato</option>
          <option value="E" <% if ( "E".equals(codMonoTempo) ) { out.print("SELECTED=\"true\""); } %> >Determinato / Indeterminato</option>
      </af:comboBox>
      </td> -->
    </table>
  </td>  
</tr>
<tr>
  <td class="etichetta">Disponibile alla formazione professionale?</td>
  <td>
    <table>
      <td class="campo">
        <af:comboBox 
          title="Disponibile alla formazione"
          name="FLGDISPFORMAZIONE"
          classNameBase="input"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged();controlloAssPatto(this)"
        >
          <option value=""  <% if ( "".equals(flgDispFormazione) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(flgDispFormazione) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
          <option value="N" <% if ( "N".equals(flgDispFormazione) ) { out.print("SELECTED=\"true\""); } %> >No</option>
        </af:comboBox>
      </td>
   <!--   <td class="etichetta">Disponibile a PIP / tirocinii / stage ?</td>
      <td class="campo">
        <af:comboBox 
          title="Disponibile a PIP / tirocinii / stage"
          name="FLGPIP"
          classNameBase="combobox"
          disabled="<%= String.valueOf( !canModify ) %>"
          onChange="fieldChanged()"
        >
          <option value=""  <% if ( "".equals(flgPianiInsProf) )  { out.print("SELECTED=\"true\""); } %> ></option>
          <option value="S" <% if ( "S".equals(flgPianiInsProf) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
          <option value="N" <% if ( "N".equals(flgPianiInsProf) ) { out.print("SELECTED=\"true\""); } %> >No</option>
        </af:comboBox>
      </td> -->

    </table>
    
  </td>
</tr>
       <tr>
                <td colspan=2>
                <% if (!onlyInsert) {%>
                <%@ include file="../patto/_associazioneDettaglioXPatto.inc" %>
                <%}%>
                </td>
        </tr>
        <%if(nuovo) {%>
        <tr>
          <td colspan="2" align="center">
          <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();checkDispLavorare();">
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
            <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update();checkDispLavorare();">
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
      <table>  
        <tr><td colspan="2" width="70%" align="left"><%operatoreInfo.showHTML(out);%></td></tr>
      </table>
      <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>
    </div>
  </af:form>
</body>

</html>

<% } %>