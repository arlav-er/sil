<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>

<%@ page import="com.engiweb.framework.base.*,
		com.engiweb.framework.security.*,
		it.eng.afExt.utils.*,
		it.eng.sil.security.*,
		it.eng.sil.util.*,
		java.util.*,
		java.math.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));

ProfileDataFilter filter = new ProfileDataFilter(user, _page);
filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));

//boolean canModify = false;

if (! filter.canViewLavoratore()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}
else {	// questo else si chiude a fine file!

  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify = attributi.containsButton("aggiorna");
  boolean canDelete = attributi.containsButton("rimuovi");
  boolean canInsert = attributi.containsButton("inserisci");

  if ( !canModify && !canInsert && !canDelete ) {
    // nulla
  }
  else {
    boolean canEdit = filter.canEditLavoratore();
    if ( !canEdit ) {
      canModify = false;
      canInsert = false;
      canDelete = false;
    }
  }

  BigDecimal prgDisComune=new BigDecimal(0);

  /* TODO: Commentare  
  Utils.dumpObject("cdnLavoratore", cdnLavoratore, out);
  Utils.dumpObject("CDUT", codiceUtenteCorrente, out);
  Utils.dumpObject("vectListaTerritoriMansioni", vectListaTerritoriMansioni, out);
  */

  boolean nuovo = ! serviceResponse.containsAttribute("M_DettaglioDisComuneTerritorio");

  String apriDivIns = (serviceRequest.containsAttribute("APRIDIVINS"))?"":"none";
  String apriDivDet = (serviceRequest.containsAttribute("APRIDIVDET"))?"":"none";
  String url_nuovo = "AdapterHTTP?PAGE=TerritorioPage" +
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + _funzione +
                     "&APRIDIVINS=TRUE";


  String strMansione = null;
  String strComune = null;
  String strNoteCom = null;
  BigDecimal cdnUtIns = null;
  String dtmIns = null;
  BigDecimal cdnUtMod = null;
  String dtmMod = null;
  Testata testata =  null;

  if (! nuovo) {
      SourceBean dett = (SourceBean) serviceResponse.getAttribute("M_DettaglioDisComuneTerritorio");
      SourceBean rowDett = (SourceBean) dett.getAttribute("ROWS.ROW");

      strMansione  = StringUtils.getAttributeStrNotNull(rowDett,"strMansione");
      strComune    = StringUtils.getAttributeStrNotNull(rowDett,"strComune");
      strNoteCom   = StringUtils.getAttributeStrNotNull(rowDett,"strNote");
      prgDisComune = (BigDecimal) rowDett.getAttribute("prgDisComune");
      cdnUtIns     = (BigDecimal) rowDett.getAttribute("CDNUTINS");
      dtmIns       = (String) rowDett.getAttribute("DTMINS");
      cdnUtMod     = (BigDecimal) rowDett.getAttribute("CDNUTMOD");
      dtmMod       = (String) rowDett.getAttribute("DTMMOD");
      testata      = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);    
  }
  
%>

<html>

<head>
<title>Disponibilità geografica per le mansioni</title>

<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
<af:linkScript path="../../js/"/>

<SCRIPT TYPE="text/javascript">
  var flagChanged = false;

  function fieldChanged() {
   <% if (canModify){ %> 
      flagChanged = true;
   <% } %> 
  }
  
  function ToUpperCase(inputName){
    // by GG 20-09-2004
    var ctrlObj = eval("document.forms[0]." + inputName);
    ctrlObj.value = ctrlObj.value.toUpperCase();
	return true;
  }
  
  // Verifica che ci sia almeno 
  // un elemento selezionato
  // nella lista a multiselezione.
  //
  // @param listName nome del controllo lista multiselezione 
  // @return true se c'è almeno una selezione.
  function IsMansioneSelezionata() {

    var isElemScelto = false;
    var elementi = document.forms[0].elements['PRGMANSIONE'];
    for (var i= 0; i < elementi.length; i++) {
      if ( elementi[i].selected ) {
        isElemScelto = true;
        break;
      }
    }
    
    if ( ! isElemScelto ) {
      alert("E' necessario scegliere\nalmeno una mansione");
      return false;
    }
    return true;
  }

  function FindComune(codComune, nomeComune, nomeComuneHid) {	
		
    if ( nomeComune.value == "" ) {
        // Non fa niente. Il campo di riferimento è il codice del comune, 
        // che non viene cancellato se non direttamente dall'utente
		}
		else if ( nomeComune.value != nomeComuneHid.value ) { 

      var s= "AdapterHTTP?PAGE=RicercaComunePage";
      s += "&strdenominazione=" + nomeComune.value.toUpperCase();
      s += "&retcod="           + codComune.name;
      s += "&retnome="          + nomeComune.name;
      
      window.open(s, "Comuni", 'toolbar=0, scrollbars=1');
			//window.open("AdapterHTTP?PAGE=RicercaComunePage&strdenominazione="+nomecomune.value.toUpperCase()+"&retcod="+codcomune.name+"&retnome="+nomecomune.name, "Comuni", 'toolbar=0, scrollbars=1');
    }
  }

  function FindStato(codStato, nomeStato, nomeStatoHid) {	
		
    if ( nomeStato.value == "" ) {
        // Non fa niente. Il campo di riferimento è il codice dello stato, 
        // che non viene cancellato se non direttamente dall'utente
		}
		else if ( nomeStato.value != nomeStatoHid.value ) { 

      var s= "AdapterHTTP?PAGE=RicercaComunePage";
      s += "&strdenominazione=" + nomeStato.value.toUpperCase();
      s += "&retcod="           + codStato.name;
      s += "&retnome="          + nomeStato.name;
      s += "&retnomehid="       + nomeStatoHid.name;
      s += "&tipo=STATI";
      
      window.open(s, "Stati", 'toolbar=0, scrollbars=1');
			//window.open("AdapterHTTP?PAGE=RicercaComunePage&strdenominazione="+nomecomune.value.toUpperCase()+"&retcod="+codcomune.name+"&retnome="+nomecomune.name, "Comuni", 'toolbar=0, scrollbars=1');
    }
  }

  function ComuneDetail(prgDisComune) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
  
    var s= "AdapterHTTP?PAGE=TerritorioPage";
    s += "&SELDETTAGLIO=TRUE";
    s += "&prgDisComune=" + prgDisComune;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s += "&APRIDIVDET=TRUE";

    setWindowLocation(s);
  }

  
  function ComuneDelete(prgDisComune, descComune, descMansione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Eliminare il comune \n" 
      + descComune.replace(/\^/g, '\'') 
      + "\nper la mansione\n"
      + descMansione 
      + " ?" ;
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=TerritorioPage";
      s += "&MODULE=M_DeleteTerritorioComune";
      s += "&prgDisComune=" + prgDisComune;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

  function ProvinciaDelete(prgDisProvincia, descProvincia, descMansione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Eliminare la provincia \n" 
      + descProvincia.replace(/\^/g, '\'') 
      + "\nper la mansione\n"
      + descMansione 
      + " ?" ;
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=TerritorioPage";
      s += "&MODULE=M_DeleteTerritorioProvincia";
      s += "&PRGDISPROVINCIA=" + prgDisProvincia;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

  function RegioneDelete(prgDisRegione, descRegione, descMansione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Eliminare la regione \n" 
      + descRegione.replace(/\^/g, '\'') 
      + "\nper la mansione\n"
      + descMansione 
      + " ?" ;
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=TerritorioPage";
      s += "&MODULE=M_DeleteTerritorioRegione";
      s += "&PRGDISREGIONE=" + prgDisRegione;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

  function StatoDelete(prgDisStato, descStato, descMansione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Eliminare lo stato \n" 
      + descStato.replace(/\^/g, '\'') 
      + "\nper la mansione\n"
      + descMansione 
      + " ?" ;
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=TerritorioPage";
      s += "&MODULE=M_DeleteTerritorioStato";
      s += "&PRGDISSTATO=" + prgDisStato;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

  function ComuneInsert() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if ( !IsMansioneSelezionata('PRGMANSIONE') ) 
      return;
    
    if ( document.Frm1.codComune.value == "" ) {

      alert("Selezionare il comune");
      return;
    }
    
    document.Frm1.MODULE.value= "M_InsertTerritorioComune";
    doFormSubmit(document.Frm1);
  }

  <%-- NON PIU' USATA:
  function ProvinciaInsert() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if ( !IsMansioneSelezionata('PRGMANSIONE') ) 
      return;

    if ( document.Frm1.CODPROVINCIA.value == "" ) {
      alert("Selezionare la provincia");
      return;
    }
    
    document.Frm1.MODULE.value= "M_InsertTerritorioProvincia";
    doFormSubmit(document.Frm1);
  }
  --%>

  function RegioneInsert() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if ( !IsMansioneSelezionata('PRGMANSIONE') ) 
      return;

    if ( document.Frm1.CODREGIONE.value == "" ) {
      alert("Selezionare la regione");
      return;
    }
    
    document.Frm1.MODULE.value= "M_InsertTerritorioRegione";
    doFormSubmit(document.Frm1);
  }

  function StatoInsert() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if ( !IsMansioneSelezionata('PRGMANSIONE') ) 
      return;
    
    if ( document.Frm1.codStato.value == "" ) {

      alert("Selezionare lo stato");
      return;
    }

    document.Frm1.MODULE.value= "M_InsertTerritorioStato";
    doFormSubmit(document.Frm1);
  }

  <%-- NON PIU' USATA:
  function ZonaInsert() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if ( !IsMansioneSelezionata('PRGMANSIONE') ) 
      return;
    
    if ( document.Frm1.CODCPI.value == "" ) {

      alert("Selezionare la zona");
      return;
    }

    document.Frm1.MODULE.value= "M_InsertTerritorioZona";
    doFormSubmit(document.Frm1);
  }
  --%>

  <%-- NON PIU' USATA:
  function OpenZone() {
  
    window.open("AdapterHTTP?PAGE=AlberoZoneComuniPage", "Zone", 'toolbar=0, scrollbars=1');
  }
  --%>

  function jsSelezComuniInZona() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

	var combo = document.Frm1.CODZONA;

	if (! IsMansioneSelezionata()) return;
	if (combo.value == "") {
		alert("Selezionare la zona");
		combo.focus();
		return;
	}
	var urlParams = "&CODZONA=" + combo.value +
					"&jsFunctForXxx=" +
					"&jsFunctForCom=jsSelezComuniRitornoConComuni";
					// Lascio vuoto "jsFunctForXxx": salverò i comuni uno a uno!
	jsSelezComuniInXxxOpenUrl(urlParams);
  }


  function jsSelezComuniInProv() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

	var combo = document.Frm1.CODPROVINCIA;

	if (! IsMansioneSelezionata()) return;
	if (combo.value == "") {
		alert("Selezionare la provincia");
		combo.focus();
		return;
	}

	var urlParams = "&CODPROV=" + combo.value +
					"&jsFunctForXxx=jsSelezComuniRitornoConProv" +
					"&jsFunctForCom=jsSelezComuniRitornoConComuni";
	jsSelezComuniInXxxOpenUrl(urlParams);
  }


  function jsSelezComuniInXxxOpenUrl(urlParams) {

	var url = "AdapterHTTP?PAGE=SelezComuniInXxxPage" +
				urlParams;

	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes," +
				"left=272,top=89,width=570,height=460"
	opened = window.open(url, "selComuni", feat);
	opened.focus();
  }
  
  
  function jsSelezComuniRitornoConProv(codProv) {

	document.Frm1.CODPROVINCIA.value = codProv;
    document.Frm1.MODULE.value= "M_InsertTerritorioProvincia";
    doFormSubmit(document.Frm1);
  }


  function jsSelezComuniRitornoConComuni(codComSepStr) {
	
	document.Frm1.INSIEMEDICOMUNI.value = codComSepStr;
	document.Frm1.MODULE.value= "M_InsertTerritorioInsiemeDiComuni";
	doFormSubmit(document.Frm1);
  }
  

  //gestione checkbox
  function selDeselTerritorio()
  {
  	var coll = document.getElementsByName("SD_Territorio");
  	var b = coll[0].checked;
  	//alert(b);
  	var i;
  	coll= document.getElementsByName("CK_CANCCOMUNE");
  	for(i=0; i<coll.length; i++) {
  		coll[i].checked = b;
  	}
  	coll= document.getElementsByName("CK_CANCPROVINCIA");
  	for(i=0; i<coll.length; i++) {
  		coll[i].checked = b;
  	}
  	coll= document.getElementsByName("CK_CANCREGIONE");
  	for(i=0; i<coll.length; i++) {
  		coll[i].checked = b;
  	}
  	coll= document.getElementsByName("CK_CANCSTATO");
  	for(i=0; i<coll.length; i++) {
  		coll[i].checked = b;
  	}  	
  }
  
  function cancellaMassiva() {
  	
  	 if ( confirm("Eliminare tutti i territori selezionati?") ) {

      coll1= document.getElementsByName("CK_CANCCOMUNE");
	  coll2= document.getElementsByName("CK_CANCPROVINCIA");
	  coll3= document.getElementsByName("CK_CANCREGIONE");
	  coll4= document.getElementsByName("CK_CANCSTATO");

      var chkboxObj1 = eval(coll1);
      var chkboxObj2 = eval(coll2);
      var chkboxObj3 = eval(coll3);
      var chkboxObj4 = eval(coll4);
                        
      var strPrgComune="";
      var strPrgProvincia="";
      var strPrgRegione="";
      var strPrgStato="";

      for(i=0; i<chkboxObj1.length; i++) {
  		if(chkboxObj1[i].checked) {
  			if(strPrgComune.length>0) { strPrgComune += ","; }
  			strPrgComune += chkboxObj1[i].value;
  		}
  	  }
      for(i=0; i<chkboxObj2.length; i++) {
  		if(chkboxObj2[i].checked) {
  			if(strPrgProvincia.length>0) { strPrgProvincia += ","; }
  			strPrgProvincia += chkboxObj2[i].value;
  		}
  	  }
      for(i=0; i<chkboxObj3.length; i++) {
  		if(chkboxObj3[i].checked) {
  			if(strPrgRegione.length>0) { strPrgRegione += ","; }
  			strPrgRegione += chkboxObj3[i].value;
  		}
  	  }
      for(i=0; i<chkboxObj4.length; i++) {
  		if(chkboxObj4[i].checked) {
  			if(strPrgStato.length>0) { strPrgStato += ","; }
  			strPrgStato += chkboxObj4[i].value;
  		}
  	  }
     
      document.frmCancella.PRGCOMUNECANCMASSIVA.value = strPrgComune;
      document.frmCancella.PRGPROVINCIACANCMASSIVA.value = strPrgProvincia;
      document.frmCancella.PRGREGIONECANCMASSIVA.value = strPrgRegione;
      document.frmCancella.PRGSTATOCANCMASSIVA.value = strPrgStato;
     
      document.frmCancella.submit();
     
    }
  } 


  <!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>

  window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
</script>
</head>

<body class="gestione" onload="rinfresca();">

<%
	infCorrentiLav.show(out); 
	linguette.show(out);
%>

<p>
	<af:showMessages prefix="M_InsertTerritorioComune"/>
	<af:showMessages prefix="M_InsertTerritorioZona"/>
	<af:showMessages prefix="M_InsertTerritorioProvincia"/>
	<af:showMessages prefix="M_InsertTerritorioRegione"/>
	<af:showMessages prefix="M_InsertTerritorioStato"/>
	<af:showMessages prefix="M_AggiornaDisComuneTerritorio"/>
	<af:showMessages prefix="M_DeleteTerritorioComune"/>
	<af:showMessages prefix="M_DeleteTerritorioProvincia"/>
	<af:showMessages prefix="M_DeleteTerritorioRegione"/>
	<af:showMessages prefix="M_DeleteTerritorioStato"/>
	<af:showMessages prefix="M_DeleteMASSIVATerritoriInMansione"/>
	<af:showMessages prefix="M_InsertTerritorioInsiemeDiComuni"/>
	<af:showErrors/>
</p>

  
<div align="center" id="listaTerritori">

<% if (canDelete) { %>  
	<table width="96%" align="center">
		<tr valign="middle">
			<td align="left" class="azzurro_bianco">
				<input type="checkbox" name="SD_Territorio" onClick="selDeselTerritorio();"/>&nbsp;Seleziona/Deseleziona tutti
				&nbsp;&nbsp;
				<button type="button" onClick="cancellaMassiva();" class="ListButtonChangePage">
					&nbsp;<img src="../../img/del.gif" />&nbsp;Cancella selezionati
				</button>
			</td>
			<td>&nbsp;</td>
		</tr>
	</table>
<% } %>          

<br/>

<table width="96%" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td class="sfondo_lista" valign="top" align="left" width="6" height="6px"><img src="../../img/angoli/bia1.gif" width="6" height="6"></td>
    <td class="sfondo_lista" height="6px">&nbsp;</td>
    <td class="sfondo_lista" valign="top" align="right" width="6" height="6px"><img src="../../img/angoli/bia2.gif" width="6" height="6"></td>
  </tr>
  <tr>
    <td class="sfondo_lista" width="6">&nbsp;</td>

    <td class="sfondo_lista" align="center">
      <TABLE class="lista" align="center">

        <TR>
          <TH class="lista" width="16%">&nbsp;Mansione&nbsp;</TH>
          <TH class="lista" width="24%">&nbsp;Comuni&nbsp;</TH>
          <TH class="lista" width="20%">&nbsp;Province&nbsp;</TH>
          <TH class="lista" width="20%">&nbsp;Regioni&nbsp;</TH>
          <TH class="lista" width="20%">&nbsp;Stati&nbsp;</TH>
        </TR>
        <%
        String strDesMansione= "";
        String strDesComune= "";
        String strDesRegione= "";
        String strDesProvincia= "";
        String strDesStato= "";
        BigDecimal prgComune = new BigDecimal(0);
        BigDecimal prgRegione = new BigDecimal(0);
        BigDecimal prgProvincia = new BigDecimal(0);
        BigDecimal prgStato = new BigDecimal(0);
        BigDecimal prgMansione = new BigDecimal(0);

        boolean pari = false;
        boolean almenoUnaMans = false;
        String classeLista = pari ? "lista_pari" : "lista_dispari" ;
        SourceBean sbMansione = null;
        Vector vettMansioni = serviceResponse.getAttributeAsVector("M_ListTerritoriMansioni.MANSIONI.ROWS.ROW");
        for (int i=0; i< vettMansioni.size(); i++) {
	       sbMansione = (SourceBean) vettMansioni.get(i);
	       strDesMansione = StringUtils.getAttributeStrNotNull(sbMansione,"DESCRIZIONE");
	       prgMansione = (BigDecimal) (sbMansione.getAttribute ("PRGMANSIONE"));
	          
	       // Recupero subito tutti i vettori,
	       Vector vettComuni   = sbMansione.getAttributeAsVector("COMUNI.ROWS.ROW");
	       Vector vettProvince = sbMansione.getAttributeAsVector("PROVINCE.ROWS.ROW");
		   Vector vettRegioni  = sbMansione.getAttributeAsVector("REGIONI.ROWS.ROW");
		   Vector vettStati    = sbMansione.getAttributeAsVector("STATI.ROWS.ROW");

		   // NON STAMPO LA RIGA DELLA MANSIONE SE NON HA ALCUNCHE' DA MOSTRARE!
		   if (vettComuni.isEmpty()  && vettProvince.isEmpty() &&
		       vettRegioni.isEmpty() && vettStati.isEmpty() ) {
		         
		         // Nulla da fare
		   }
		   else {
	          %>
	          <TR class="lista">
	
	            <TD class="<%=classeLista%>">
	            	<%=strDesMansione%>
	            </TD>
	            
	            <TD class="<%=classeLista%>"><TABLE>
		            <%
		            if (vettComuni.isEmpty()) { %> <tr><td>&nbsp;</td></tr> <% }
		            else
			            for (int j=0; j<vettComuni.size(); j++) {
			                SourceBean sbComune = ((SourceBean) vettComuni.get(j));
			                strDesComune = StringUtils.getAttributeStrNotNull(sbComune,"STRDENOMINAZIONE");
			                prgComune = (BigDecimal) (sbComune.getAttribute ("PRGDISCOMUNE")); %>
			                <TR>
			                <% if (canModify) { %>
			                  <TD>
			                    <A href="javascript://" onclick="ComuneDetail(<%=prgComune%>); return false;">
			                    <IMG name="image" border="0"  src="../../img/detail.gif" alt="Selezionare un dettaglio"/></A>
			                  </TD>
			                  <TD>
				    			<INPUT type="checkbox" name="CK_CANCCOMUNE" value="<%=prgComune%>" />  
			                  </TD>
			                <% } %>                                 
			                  <TD>
			                  	<%=strDesComune.replace('\'', '`')%>
			                  </TD>
			                <% if (canDelete) { %>
			                  <TD align="right">
			                    <A href="javascript://" onclick="ComuneDelete(<%=prgComune%>,'<%=strDesComune.replace('\'', '`')%>','<%=strDesMansione.replace('\'', '`')%>'); return false;">
			                    <IMG name="image" border="0" src="../../img/del.gif" alt="Cancellare una riga"/></A>
			                  </TD>
			                <% } %>
			                </TR>
			            <% } %>
	            </TABLE></TD>
	
	            <TD class="<%=classeLista%>"><TABLE>
		            <%
		            if (vettProvince.isEmpty()) { %> <tr><td>&nbsp;</td></tr> <% }
		            else
			            for (int j=0; j<vettProvince.size(); j++) {
			                SourceBean sbProvincia = ((SourceBean) vettProvince.get(j));
			                strDesProvincia = StringUtils.getAttributeStrNotNull(sbProvincia,"STRDENOMINAZIONE");
			                prgProvincia = (BigDecimal) (sbProvincia.getAttribute ("PRGDISPROVINCIA")); %>
			                <TR>
			                <% if (canModify) { %>
			                  <TD>
				    			<INPUT type="checkbox" name="CK_CANCPROVINCIA" value="<%=prgProvincia%>" />  
			                  </TD>
			                <% } %>
			                  <TD>
			                  	<%=strDesProvincia.replace('\'', '`')%>
			                  </TD>
			                <% if (canDelete) { %>
			                  <TD align="right">
			                    <A href="javascript://" onclick="ProvinciaDelete(<%=prgProvincia%>,'<%=strDesProvincia.replace('\'', '`')%>','<%=strDesMansione.replace('\'', '`')%>'); return false;">
			                    <IMG name="image" border="0" src="../../img/del.gif" alt="Cancellare una riga"/></A>
			                  </TD>
			                <% } %>
			                <TR>
			            <% } %>
	            </TABLE></TD>
	
	            <TD class="<%=classeLista%>"><TABLE>
		            <%
		            if (vettRegioni.isEmpty()) { %> <tr><td>&nbsp;</td></tr> <% }
		            else
			            for (int j=0; j<vettRegioni.size(); j++) {
			                SourceBean sbRegione = ((SourceBean) vettRegioni.get(j));
			                strDesRegione = StringUtils.getAttributeStrNotNull(sbRegione,"STRDENOMINAZIONE");
			                prgRegione = (BigDecimal) (sbRegione.getAttribute ("PRGDISREGIONE")); %>
			                <TR>
			                <% if (canModify) { %>
			                  <TD>
			                  	<INPUT type="checkbox" name="CK_CANCREGIONE" value="<%=prgRegione%>" onClick=""/>  
			                  </TD>
			                <% } %>
			                  <TD>
			                  	<%=strDesRegione.replace('\'', '`')%>
			                  </TD>
			                <% if (canDelete) { %>
			                  <TD align="right">
			                    <A href="javascript://" onclick="RegioneDelete(<%=prgRegione%>,'<%=strDesRegione.replace('\'', '`')%>','<%=strDesMansione.replace('\'', '`')%>'); return false;">
			                    <IMG name="image" border="0" src="../../img/del.gif" alt="Cancellare una riga"/></A>
			                  </TD>
			                <% } %>
			                <TR>
			            <% } %>
	            </TABLE></TD>
	
	            <TD class="<%=classeLista%>"><TABLE>
		            <%
		            if (vettStati.isEmpty()) { %> <tr><td>&nbsp;</td></tr> <% }
		            else
			            for (int j=0; j<vettStati.size(); j++) {
			                SourceBean sbStato = ((SourceBean) vettStati.get(j));
			                strDesStato = StringUtils.getAttributeStrNotNull(sbStato,"STRDENOMINAZIONE");
			                prgStato = (BigDecimal) (sbStato.getAttribute ("PRGDISSTATO")); %>
			                <TR>
			                <% if (canModify) { %>
			                  <TD align="left">
			                  	<INPUT type="checkbox" name="CK_CANCSTATO" value="<%=prgStato%>" onClick=""/>  
			                  </TD>
			                <% } %>
			                  <TD>
			                  	<%=strDesStato.replace('\'', '`')%>
			                  </TD>
			                <% if (canDelete) { %>
			                  <TD align="right">
			                    <A href="javascript://" onclick="StatoDelete(<%=prgStato%>,'<%=strDesStato.replace('\'', '`')%>','<%=strDesMansione.replace('\'', '`')%>'); return false;">
			                    <IMG name="image" border="0" src="../../img/del.gif" alt="Cancellare una riga"/></A>
			                  </TD>
			                <% } %>
			                </TR>
			            <% } %>
	            </TABLE></TD>
	          </TR>
	          <%
	          almenoUnaMans = true;
	          pari = !pari;
	          classeLista = pari ? "lista_pari" : "lista_dispari";
		   } //else
        } //for i

		// Se non ho stampato neppure una riga, mostro il messaggio di avviso        
        if (! almenoUnaMans) {
        	%>
        	<tr>
        		<td colspan="6"><b>Non &egrave; stato trovato alcun risultato.</b></td>
        	</tr>
        	<%
        }
        %>

      </TABLE>
    </td>
    
    <td class="sfondo_lista" width="6">&nbsp;</td>
  </tr>

  <tr valign="bottom">
    <td class="sfondo_lista" valign="bottom" align="left" width="6" height="6px"><img src="../../img/angoli/bia4.gif" width="6" height="6"/></td>
    <td class="sfondo_lista" height="6px">&nbsp;</td><td class="sfondo_lista" valign="bottom" align="right" width="6" height="6px"><img src="../../img/angoli/bia3.gif" width="6" height="6"/></td>
  </tr>
</table>

<br/>
<% if(canInsert) { %>
	<table width="70%" align="center">
		<tr>
			<td align="center">
				<input type="button" class="pulsanti"  value="Nuova disponibilità"
					onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerIns','<%=url_nuovo%>');document.location='#aLayerIns';" />
			</td>
		</tr>
	</table>
<% } %>

</div>

<p/>

<%
String divStreamTop    = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>

<%
   /* Savino 04/07/05: presenza di due form nalla pagina, con conseguenti errori nei controlli automatici dei tag af.
	*	 Ora viene caricata solo una form, di default quella di inserimento. Il nome della seconda form, "Frm2", 
	*	 non sembra dare problemi: lasciata cosi' come'e'.
    */
if (!apriDivDet.equals("")) {%>
<%-- LAYER DI INSERIMENTO --%>
   <div id="divLayerIns" name="divLayerIns" class="t_layerDett"
        style="position:absolute; width:70%; left:50; top:0px; z-index:6; display:<%=apriDivIns%>;">
	<a name="aLayerIns"></a>

    <%-- Stondature ELEMENTO TOP --%>
    <%out.print(divStreamTop);%>
    
    <table class="main" cellpadding="0" cellspacing="0">
      <tr>
        <td class="azzurro_bianco" width="16" height="16" class="menu"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerIns');return false"></td>
        <td class="azzurro_bianco" height="16">
        Disponibilit&agrave; territoriale
        </td>
        <td class="azzurro_bianco" width="16" height="16" onClick="ChiudiDivLayer('divLayerIns')" class="menu"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>
  
   <af:form name="Frm1" id="Frm1" method="POST" action="AdapterHTTP" dontValidate="true">

    <input type="hidden" name="PAGE" value="TerritorioPage">
    <input type="hidden" name="MODULE" value=""/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione %>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>

	<input type="hidden" name="INSIEMEDICOMUNI" value=""/>
	<%--
	Campo usato per inserire un elenco di comuni
	(contiene una stringa con i codici comuni separati da un carattere separatore).
	--%>
    
    <table class="main" width="100%" cellpadding="0" cellspacing="0">
      <tr>
      <%-- Sezione inserimento --%>
      <% if ( canModify ) { %>
        <td valign="top">
          <table align="center" width="100%">
            <tr>
              <td>
                <br/>
              </td> 
            </tr>

            <%-- *** Sezione Mansioni *** --%>

            <tr>
              <td class="etichetta">Mansioni</td>
              <td class="campo">
                <ps:mansioniComboBoxTag 
                  name = "PRGMANSIONE" 
                  moduleName= "M_ListMansioniDisponibileLavoro"/>
              </td>
            </tr>

            <tr>
              <td colspan="6" valign="top"><hr/></td>
            </tr>

            <%-- *** Sezione Comuni *** --%>

            <tr>
              <td class="etichetta">Comune</td>
              <td colspan="5">
              <table cellspacing="0" cellpadding="0" border="0" width="100%">
	              <tr><td>
	                <af:textBox 
	                  type="text" 
	                  name="codComune"
	                  onKeyUp="javascript:PulisciRicerca(Frm1.codComune, Frm1.codComuneHid, Frm1.strComune, Frm1.strComuneHid, null, null,'codice');"
	                  value="" 
	                  size="4" 
	                  maxlength="4" 
	                  validateWithFunction="ToUpperCase"/>&nbsp;
	
	                <A HREF="javascript:btFindComuneCAP_onclick(Frm1.codComune, Frm1.strComune, null, 'codice','COMUNI',null,'inserisciComuneTerrMansNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
	
	                <af:textBox 
	                  type="hidden" 
	                  name="codComuneHid" 
	                  value="" />
	               </td>
	
	               <td>
	                <af:textBox 
	                  type="text"
	                  name="strComune"
	                  value=""
	                  required="true"
	                  size="30"
	                  maxlength="50"
	                  onKeyUp="if (event.keyCode == 13) { event.keyCode=9; this.blur(); } PulisciRicerca(Frm1.codComune, Frm1.codComuneHid, Frm1.strComune, Frm1.strComuneHid, null, null, 'descrizione');"/>&nbsp;
	
	                <A HREF="javascript:btFindComuneCAP_onclick(Frm1.codComune, Frm1.strComune, null, 'descrizione','COMUNI',null,'inserisciComuneTerrMansNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
	                
	                <af:textBox 
	                  type="hidden" 
	                  name="strComuneHid" 
	                  value=""/>
	               </td></tr>
               </table>
              </td>
            </tr>

            <%-- Campo Note --%>
            <tr>
              <td class="etichetta">Note</td>
              <td class="campo">
				  <af:textArea classNameBase="input" name="STRNOTE" 
	              			   cols="30" rows="4" maxlength="500"/>
              </td>

              <td colspan="4">
                <center>
                  <input class="pulsante" type="button" name="salva" value="Inserisci" onclick="ComuneInsert();">
                </center>
              </td>
            </tr>

            <tr>
              <td colspan="6" valign="top"><hr/></td>
            </tr>

            <%-- *** Sezione Zone *** --%>
            <tr>
              <td class="etichetta" nowrap>Zona</td>
              <td class="campo" nowrap>

              <%-- NON PIU' USATO IL LOOKUP:
                <input type="hidden" name="CODCPI" value=""> 
                <af:textBox 
                  type="text" 
                  name="DESCZONA"  
                  value="" 
                  size="30" 
                  readonly="true"
                  maxlength="50"/>&nbsp;
                <A HREF="javascript:OpenZone();"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="Seleziona zona"/></a>
                --%>

                <af:comboBox name="CODZONA"
                             title="Zona" required="true"
                             moduleName="M_ListZone" addBlank="true" blankValue=""/>
              </td>

              <td colspan="4">
                <center>
                 <%-- NON PIU' USATO:
                 <input class="pulsante" type="button" name="salva" value="Inserisci" onclick="ZonaInsert();">
                 --%>
                 <input type="button" class="pulsante" name="avanti" value="Avanti &gt;&gt;"
                 		onclick="jsSelezComuniInZona();">
                </center>
              </td>
            </tr>

            <tr>
              <td colspan="6" valign="top"><hr/></td>
            </tr>

            <%-- *** Sezione Province *** --%>
            
            <tr>
              <td class="etichetta">Provincia</td>
              <td class="campo">
                <af:comboBox name="CODPROVINCIA"
                             title="Province" required="true"
                             moduleName="M_GetIDOProvince" addBlank="true" blankValue=""/>
              </td>
  
              <td colspan="4">
                <center>
                 <%-- NON PIU' USATO:
                  <input class="pulsante" type="button" name="salva" value="Inserisci" onclick="ProvinciaInsert();">
                 --%>
                 <input type="button" class="pulsante" name="avanti" value="Avanti &gt;&gt;"
                 		onclick="jsSelezComuniInProv();">
                </center>
              </td>
            </tr>
            
            <tr>
              <td colspan="6" valign="top"><hr/></td>
            </tr>

            <%-- *** Sezione Regioni *** --%>
            
            <tr>
              <td class="etichetta">Regione</td>
              <td class="campo">
                <af:comboBox name="CODREGIONE"
                             title="Regioni" required="true"
                             moduleName="M_ListRegioni" addBlank="true" blankValue=""/>
              </td>

              <td colspan="4">
                <center>
                  <input class="pulsante" type="button" name="salva" value="Inserisci" onclick="RegioneInsert();">
                </center>
              </td>
            </tr>
            
            <tr>
              <td colspan="6" valign="top"><hr/></td>
            </tr>

            <%-- *** Sezione Stati *** --%>

            <tr>
              <td class="etichetta">Stato</td>
              
              <td class="campo">
                <%--
                NOTE: Questa textbox è hidden adesso ma la lascio ugualmente
                	  in previsione che possa tornare visibile in futuro.
                --%>
                <af:textBox 
                  type="hidden" 
                  name="codStato" 
                  value="" 
                  size="4" 
                  maxlength="4" 
                  validateWithFunction="ToUpperCase"/>
                
                <af:textBox 
                  type="hidden" 
                  name="codStatoHid" 
                  value="" />

                <af:textBox 
                  type="text" 
                  name="strStato"  
                  value=""
                  required="true"
                  size="30" 
                  maxlength="50"
                  onKeyUp="if (event.keyCode == 13) { event.keyCode=9; this.blur(); }"/>&nbsp;

                <A HREF="javascript:FindStato(Frm1.codStato, Frm1.strStato, Frm1.strStatoHid);"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="Seleziona stato"/></a>

               <af:textBox 
                  type="hidden" 
                  name="strStatoHid" 
                  value=""/>
              </td>

              <td colspan="4">
                <center>
                  <input class="pulsante" type="button" name="salva" value="Inserisci" onclick="StatoInsert();">
                </center>
              </td>
            </tr>

            <tr>
              <td colspan="6" valign="top"><hr/></td>
            </tr>

            <%-- *** Pulsanti *** --%>

            <tr>
              <td colspan="6" align="center">
              <input type="button" class="pulsanti" name="chiudi" value="Chiudi senza inserire" onClick="ChiudiDivLayer('divLayerIns')">
              </td>
            </tr>
            <%-- *** Fine sezioni *** --%>
          
          </table>
        </td>
      <% } %>
      </tr>
    </table>
   </af:form>
   
    <%-- Stondature ELEMENTO BOTTOM --%>
    <%out.print(divStreamBottom);%>
  </div>

<%} else {%>

<%-- LAYER DI MODIFICA --%>
   <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
        style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDivDet%>;">

    <%-- Stondature ELEMENTO TOP --%>
    <%out.print(divStreamTop);%>
    <%-- CONTENUTO DEL LAYER --%>
    
    <table  class="main" cellpadding="0" cellspacing="0">
      <tr>
        <td class="azzurro_bianco" width="16" height="16" class="menu"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td class="azzurro_bianco">Disponibilità territoriale</td>
        <td class="azzurro_bianco" width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="menu"><img src="../../img/chiudi_menu.gif" alt="Chiudi"></td>
      </tr>
    </table>
    <table class="main" cellpadding="0" cellspacing="0">
	  <af:form name="Frm2" id="Frm2" method="POST" action="AdapterHTTP" dontValidate="true">
	    <input type="hidden" name="PAGE" value="TerritorioPage">
	    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione %>"/>
	    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
	    <input type="hidden" name="prgDisComune" value="<%= prgDisComune %>"/>
	     <tr>
	      <td class="etichetta">Mansione</td>
	      <td class="campo">
		      <af:textBox classNameBase="input" name="strMansione" value="<%=strMansione%>" readonly="true" size="48" />
	      </td>
	    </tr>
	
	     <tr>
	      <td class="etichetta">Comune</td>
	      <td class="campo">
		      <af:textBox classNameBase="input" name="strComune" value="<%=strComune%>" readonly="true" size="48" />
	      </td>
	    </tr>
	
	       <tr>
	        <td class="etichetta">Note</td>
	        <td class="campo">
			  <af:textArea classNameBase="input" onKeyUp="fieldChanged();" name="strNoteCom" 
						  value="<%=strNoteCom%>" cols="30" rows="4" maxlength="500"/>        
	        </td>
	      </tr>
	      <%if(canModify) {%>
	      <tr>
	        <td colspan="2" align="center">
	        <% if ( canModify ) { %>
	          <input type="submit" class="pulsanti" name="aggiorna" value="Aggiorna">
	        <% } %>
	          <input type="button" 
	            class="pulsanti" 
	            name="annulla" 
	            value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
	            onClick="ChiudiDivLayer('divLayerDett')">
	        </td>
	      </tr>
	      <%}
	      if (!nuovo && (testata != null)) {%>
	        <tr><td colspan="2" width="70%" align="left"><%testata.showHTML(out);%></td></tr>      
	      <%}%>
	  </af:form>
	  
    </table>
    
    <%-- Stondature ELEMENTO BOTTOM --%>
    <%out.print(divStreamBottom);%>
    
   </div>
  <%}%>
  
  <form name="frmCancella" method="POST" action="AdapterHTTP">
  <input type="hidden" name="PAGE" value="TerritorioPage">
  <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione %>"/>
  <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
  <input type="hidden" name="CANCELLAMASSIVA" value="true">
  <input type="hidden" name="PRGCOMUNECANCMASSIVA" value="">
  <input type="hidden" name="PRGPROVINCIACANCMASSIVA" value="">
  <input type="hidden" name="PRGREGIONECANCMASSIVA" value="">
  <input type="hidden" name="PRGSTATOCANCMASSIVA" value="">
  </form>
  
</body>

</html>

<% } %>