<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
    PageAttribs attributi = new PageAttribs(user, _current_page);
	
	boolean canInsert = false;
	boolean canDelete = false;
    boolean canModify = false;
    boolean canDocAssociati = false;
    boolean readOnlyStr = true;
    boolean readOnly;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}else{
      canInsert = attributi.containsButton("INSERISCI");
      canDelete = attributi.containsButton("RIMUOVI");
      canDocAssociati = attributi.containsButton("DOCUMENTI_ASSOCIATI");
    	
    	if(!canInsert){
    		canInsert=false;
    	}else{
    		canInsert=filter.canEditLavoratore();
    	}

    	if(!canDelete){
    		canDelete=false;
    	}else{
    		canDelete=filter.canEditLavoratore();
    	}
      
    }
	
	boolean canViewCodEdizione = false;
	if(serviceResponse.containsAttribute("M_ST_CONFIG_CONF_FP_CODED") && serviceResponse.getAttribute("M_ST_CONFIG_CONF_FP_CODED") !=null){
		SourceBean configurazione = (SourceBean) serviceResponse.getAttribute("M_ST_CONFIG_CONF_FP_CODED.ROWS.ROW");
		String valoreConfig =  configurazione.getAttribute("STRVALORE").toString();
		if(StringUtils.isFilledNoBlank(valoreConfig) && valoreConfig.equalsIgnoreCase("S")){
			canViewCodEdizione = true;
		}
	}
	
	
	
    readOnlyStr = !canInsert;
    readOnly = readOnlyStr;    
    canModify = canInsert;
    

	
%>

<%   
    Vector rows = serviceResponse.getAttributeAsVector("M_LoadForPro.ROWS.ROW");
    boolean infStor = serviceRequest.containsAttribute("INFSTOR");

    //String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
    String strNome       = (String)serviceRequest.getAttribute("LAV_NOME");
    String strCognome    = (String)serviceRequest.getAttribute("LAV_COGNOME");
 
  
    Vector vectListaConoscenze = serviceResponse.getAttributeAsVector("M_LISTFORPRO.ROWS.ROW");
  

    SourceBean row           = null;

    
    Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

  BigDecimal prgCorso=new BigDecimal(0);
  String codCorso="";
  String codTipoCertificato="";
  String descCorso="";  
  String strDescrizione="";
  String codEdizione="";
  String strEdizione="";
  String strEnte="";
  String flgCompletato="";
  String flgStage="";
  String strContenuto="";
  String codComEnte="";
  String strDescComEnte="";  
  String strLocalitaEnte="";
  String strIndirizzoEnte="";
  BigDecimal numAnno= null ;
  BigDecimal numMesi=null;
  BigDecimal numOre=null;
  BigDecimal numOreSpese= null;
  String strMotCessazione="";   
  String codTipoCorso="";
  BigDecimal cdnAmbitoDisciplinare=null;
  String strAmbitoDisciplinare="";
  String strAzienda="";
  String codComAzienda="";
  String strDescComAzienda="";  
  String strLocalitaAzienda="";
  String strIndirizzoAzienda="";
  String strNotaCorso="";
  BigDecimal numOreStage=null; 
  BigDecimal cdnUtIns=null;
  String dtmIns="";
  BigDecimal cdnUtMod=null;
  String dtmMod="";
  String flgImportato=null;
  Testata operatoreInfo   = null; 

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

    String PRG_TAB_DA_ASSOCIARE = null;
    String COD_LST_TAB = "PR_COR";
  
    int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
    String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
    if(rows != null && !rows.isEmpty()) {   
       
    }
%>
<%


  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));



  boolean nuovo = true;
  if(serviceResponse.containsAttribute("M_LoadForPro")) { nuovo = false; }
  else { nuovo = true; }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }

  String url_nuovo = "AdapterHTTP?PAGE=ForProPage" +
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + _funzione +
                     "&APRIDIV=1";
                 

  String SPRGCORSO="";
  String cdngruppo="";
  String datautorizzazione="";
  String strautorizzazione=""; 
  if(!nuovo) {
	// Sono in modalità "Dettaglio"
	Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_LOADFORPRO.ROWS.ROW");
	if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {
	
		SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);
		
		prgCorso           = (BigDecimal)beanLastInsert.getAttribute("PRGCORSO");
		codCorso           = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODCORSO");
		codTipoCertificato = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODTIPOCERTIFICATO");
		descCorso       = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCDECOD");    
		strDescrizione  = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCRIZIONE");
		codEdizione           = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODEDIZIONE");
		strEdizione           = StringUtils.getAttributeStrNotNull(beanLastInsert, "STREDIZIONE");
		strEnte         = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRENTE");
		numAnno         = (BigDecimal)beanLastInsert.getAttribute("NUMANNO");
		flgCompletato   = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGCOMPLETATO");
		flgStage        = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGSTAGE");
		strContenuto    = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRCONTENUTO");
		codComEnte      = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODCOMENTE");
		strDescComEnte  = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCCOMENTE");
		strLocalitaEnte = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRLOCALITAENTE");
		strIndirizzoEnte= StringUtils.getAttributeStrNotNull(beanLastInsert, "STRINDIRIZZOENTE");
		numAnno         = (BigDecimal)beanLastInsert.getAttribute("NUMANNO");
		numMesi         = (BigDecimal)beanLastInsert.getAttribute("NUMMESI");
		numOre          = (BigDecimal)beanLastInsert.getAttribute("NUMORE");
		numOreSpese     = (BigDecimal)beanLastInsert.getAttribute("NUMORESPESE");
		strMotCessazione= StringUtils.getAttributeStrNotNull(beanLastInsert, "STRMOTCESSAZIONE");
		codTipoCorso    = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODTIPOCORSO");
		cdnAmbitoDisciplinare=(BigDecimal)beanLastInsert.getAttribute("CDNAMBITODISCIPLINARE");
		if (cdnAmbitoDisciplinare != null) {
		    strAmbitoDisciplinare = cdnAmbitoDisciplinare.toString();
		}
		strAzienda      = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRAZIENDA");
		codComAzienda   = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODCOMAZIENDA");
		strDescComAzienda  = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCCOMAZIENDA");
		strLocalitaAzienda=StringUtils.getAttributeStrNotNull(beanLastInsert, "STRLOCALITAAZIENDA");
		strIndirizzoAzienda=StringUtils.getAttributeStrNotNull(beanLastInsert, "STRINDIRIZZOAZIENDA");
		strNotaCorso    = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRNOTACORSO");
		numOreStage     = (BigDecimal)beanLastInsert.getAttribute("NUMORESTAGE");
		cdnUtIns        = (BigDecimal)beanLastInsert.getAttribute("CDNUTINS");
		dtmIns          = beanLastInsert.containsAttribute("DTMINS") ? beanLastInsert.getAttribute("DTMINS").toString() : "";
		dtmMod          = beanLastInsert.containsAttribute("DTMMOD") ? beanLastInsert.getAttribute("DTMMOD").toString() : "";
		cdnUtMod        = (BigDecimal)beanLastInsert.getAttribute("CDNUTMOD");
		flgImportato        = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGIMPORTATO");
		// --- NOTE: Gestione Patto
		PRG_TAB_DA_ASSOCIARE=prgCorso.toString();
		row = beanLastInsert;
		// ---
    }
  }
   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

   boolean isImportato=readOnlyStr;
   if(StringUtils.isFilledNoBlank(flgImportato) && flgImportato.equalsIgnoreCase("S")) {
	   canModify=false;
	   //readOnlyStr = !canModify;
	   isImportato=true;
   }
   
   
   
   String divStreamTop = StyleUtils.roundLayerTop(canModify);
   String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
   
   
%> 
<html>

<head>
<title>Formazione professionale</title>
<!-- ..\jsp\presel\ForPro_Main.jsp-->

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  <%@ include file="../presel/ForPro_CommonScripts.inc" %>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
  <%@ include file="../patto/_sezioneDinamica_script.inc"%>
  var flagChanged = false;
  function Select(prg){
  	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
  
      var s= "AdapterHTTP?PAGE=ForProPage";
      s += "&MODULE=M_LoadForPro";
      s += "&PRGCORSO=" + prg;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";
      s += "&APRIDIV=1";
      setWindowLocation(s);
  }
  
  function Delete(prg, descrizione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Eliminare la visibilità per l'utente specifico\n";    
    s = s + descrizione.replace(/\^/g, '\'') + " ?" ;
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=ForProPage";
      s += "&MODULE=M_DeleteForPro";
      s += "&PRGCORSO=" + prg;

      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }  


    
  function Insert() {

    document.Frm1.MODULE.value= "M_InsertForPro";
    if(controllaFunzTL() && riportaControlloUtente( controllaPatto() ) )  {
      doFormSubmit(document.Frm1);
    }
    else
    return;
  }


function Update()
  {
    var datiOk = controllaFunzTL();
    if (datiOk) {
      document.Frm1.MODULE.value = "M_UpdateForPro";
      controllaStage();
      if (riportaControlloUtente( controllaPatto() ) )
        doFormSubmit(document.Frm1);
      else return false;
    }
  }
  
  function SelezionaDettaglio_onClick() {	

    codCorso= document.Frm1.CODCORSO;
    descCorso= document.Frm1.DESCCORSO;
    numAnno = document.Frm1.NUMANNO;

    window.open("AdapterHTTP?PAGE=ForProPage&MODULE=M_ListForProCorso&CODCORSO="+codCorso.value+"&DESCCORSO="+descCorso.value+"&NUMANNO="+numAnno.value,'','toolbar=0,scrollbars=1');
  }
  

  function fieldChanged() {
    flagChanged = true;

  
  }
   function getFormObj() {return document.Frm1;}
  <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
  
function chiudiLayer() {

  ok=true;
  if (flagChanged) {
     if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
         ok=false;
     } else { 
         // Vogliamo chiudere il layer. 
         // Pongo il flag = false per evitare che mi venga riproposto 
         // un "confirm" quando poi navigo con le linguette nella pagina principale
         flagChanged = false;
     }
     
  }
  if (ok) {
     ChiudiDivLayer('divLayerDett');
  }
}

var showButtonImg = new Image();
var hideButtonImg = new Image();
showButtonImg.src="../../img/chiuso.gif";
hideButtonImg.src="../../img/aperto.gif"



function onOff(){	
  var div = document.getElementById("dett");
  var idImm = document.getElementById("imm1");
  if (div.style.display=="")  {	
    nascondi("dett");
    mostra  ("labelVisulizza");
    nascondi("labelNascondi");
    idImm.src = showButtonImg.src;
  } 
  else  {	
    mostra  ("dett");
    nascondi("labelVisulizza");
    mostra  ("labelNascondi");
    idImm.src = hideButtonImg.src
  }
}//onOff()

function mostra(id){ 
  var div = document.getElementById(id);
  div.style.display="";
}

function nascondi(id){ 
  var div = document.getElementById(id);
  div.style.display="none";
}

function controllaStage() {
	if (document.Frm1.FLGSTAGE.value=="N") {
		document.Frm1.STRAZIENDA.value="";
		document.Frm1.strComAzienda.value="";
		document.Frm1.strComAziendaHid.value="";
		document.Frm1.NUMORESTAGE.value="";      
		document.Frm1.STRLOCALITAAZIENDA.value="";
		document.Frm1.STRINDIRIZZOAZIENDA.value="";
		document.Frm1.CODCOMAZIENDA.value="";
        document.Frm1.CODCOMAZIENDAHid.value="";
	}
	return true;
}
function controllaCompletato() {
	if (document.Frm1.FLGCOMPLETATO.value=="S" ){
    	document.Frm1.STRMOTCESSAZIONE.value="";      
    }
}

function onOff1(){	
  var div = document.getElementById("dett1");
  var idImm = document.getElementById("imm2");
  
  CorsoConStage_onClick();
  
  
  if (div.style.display=="")  {	
     nascondi("dett1");
     mostra  ("labelVisulizza1");
     nascondi("labelNascondi1");
     idImm.src = showButtonImg.src;
	 //nascondo gli eventuali dettagli dello stage
	 //document.Frm1.STRAZIENDA.value="";
     document.Frm1.STRAZIENDA.disabled=true;
     document.getElementById("str_azienda").style.display="none";

     //document.Frm1.strComAzienda.value="";
     document.Frm1.strComAzienda.disabled=true;
     document.getElementById("str_com_azienda").style.display="none";

     //document.Frm1.NUMORESTAGE.value="";      
     document.Frm1.NUMORESTAGE.disabled=true;

     //document.Frm1.STRLOCALITAAZIENDA.value="";
     document.Frm1.STRLOCALITAAZIENDA.disabled=true;
     document.getElementById("str_localita").style.display="none";

     //document.Frm1.STRINDIRIZZOAZIENDA.value="";
     document.Frm1.STRINDIRIZZOAZIENDA.disabled=true;
     document.getElementById("str_indirizzo").style.display="none";
  } 
  else   {	
    mostra  ("dett1");
    nascondi("labelVisulizza1");
    mostra  ("labelNascondi1");
    idImm.src = hideButtonImg.src
  }
}//onOff()
  
  
function controllaOre(inputName) {

  ore=document.forms[0].NUMORE.value;
  oreEff=document.forms[0].NUMORESPESE.value;
  ok=true;
  if (parseInt(ore) < parseInt(oreEff)) {
    ok=false;
    alert("Il numero di ore totali è minore del numero di ore effettive");
  }  

  if (ok) {
  	return ok; 
  }  
}


function controlloCorso(inputName) {
	corso=document.forms[0].CODCORSO.value;
	descrizione=document.forms[0].STRDESCRIZIONE.value;
	ok=true;
	if (corso=="" && descrizione=="") {
		ok=false;
		alert("Specificare il codice corso o la descrizione");
	}

	if (ok) {
		return ok;
	}
}

function controllaAnno(inputName) {
	  var dataObj = eval("document.forms[0]." + inputName);
	  anno=dataObj.value;
 
 	  var ok=true;
	  if (anno.toString().length != 4) {
	    ok=false;
	    alert("Il campo Anno deve essere un numero intero di 4 cifre");
	  }  

	  if (ok) {
	  	return ok; 
	  }  
	}

        
</SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
      %>
</script>
<script language="Javascript">
  window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
<script language="Javascript" src="../../js/docAssocia.js"></script>
</head>

<body class="gestione" onload="rinfresca();AbilitaCessazione_onClick()">

  <%
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>
  <script language="javascript">
    var flgInsert = <%=nuovo%>;
  </script>            
  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaStatoAtto(flgInsert,this) && controllaStage() && controllaCompletato()">
    <input type="hidden" name="PAGE" value="ForProPage">
    <input type="hidden" name="MODULE" value="M_InsertForPro"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
    <% if (!nuovo)  {%>
    	<input type="hidden" name="PRGCORSO" value="<%=prgCorso%>"/>
    <% } %>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="CDNUTINS" value="<%= codiceUtente %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtente %>"/>
    <input type="hidden" name="STREDIZIONE" value="<%= strEdizione %>"/>    
    <input type="hidden" name="APRIDIV" value="1" />
    <% if (nuovo)  {%>
		<input type="hidden" name="CODEDIZIONE" value=""/>
	<% } %>
	<input type="hidden" name="DATINIZIO" value=""/>
	<input type="hidden" name="DATFINE" value=""/>
	<input type="hidden" name="FLGIMPORTATO" value="N"/>


    <p align="center">
      <center>
        <font color="green">
<!--          <af:showMessages prefix="M_InsertForPro"/> -->
          <af:showMessages prefix="M_DeleteForPro"/>
<!--          <af:showMessages prefix="M_UpdateForPro"/>-->
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
      <div align="center">
          
      <af:list moduleName="M_ListForPro" skipNavigationButton="1"
               canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
               canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
               jsSelect="Select" jsDelete="Delete"
      />
    
    
    <%if(canInsert) {%>
          <br>         
          <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuova formazione professionale"/>
    <%}%>
    </div> 
    </p>
    
<!-- LAYER -->
   
    <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
     style="position:absolute; width:80%; left:50; top:20px; z-index:6; display:<%=apriDiv%>;">
    <!-- Stondature ELEMENTO TOP -->
    <a name="aLayerIns"></a>
    <%out.print(divStreamTop);%>
    <p>
            <font color="green">
	         <af:showMessages prefix="M_InsertForPro"/>
	          <af:showMessages prefix="M_UpdateForPro"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
    </p>
    
    
      <table width="100%" cellpadding="0" cellspacing="0">
        <tr width="100%">
          <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
          <td height="16" class="azzurro_bianco">
          <%if(nuovo){%>
            Nuova formazione professionale
          <%} else {%>
            Formazione professionale
          <%}%>
          </td>
          <td width="16" height="16" onClick="chiudiLayer()" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
        </tr>
      </table>
      
      <table width="100%" cellpadding="0" cellspacing="0">
      <%if(nuovo) {

      %>          
        
      <tr>
        <td class="etichetta">Codice</td>
        <td class="campo">
          <af:textBox
            name="CODCORSO"
            title="Codice Corso"
            classNameBase="input"
            value="<%= codCorso %>"
            size="20"
            readonly="true"      
            maxlength="20"/>      
        </td>
      </tr>
      <tr>
        <td class="etichetta">Corso</td>
        <td class="campo">
          <af:textArea cols="45" 
                    rows="5" 
                    name="DESCCORSO"
                    classNameBase="textArea"
                    readonly="<%= String.valueOf(isImportato)%>"
                    onKeyUp="fieldChanged();PulisciRricercaCorso('corso');"
                    inline="onKeyDown=\"SaveCorsoHidden();\" onkeypress=\" if (event.keyCode==13) { event.keyCode=9; this.blur(); } \""
                    maxlength="300"
                    value="<%= descCorso %>" validateWithFunction="controlloCorso"/>
          <% if ( (!readOnly) && (!isImportato)) { %>
            <% if (canViewCodEdizione) { %>
            	<a href="#" onClick="javascript:ricercaAvanzataCorso_onClick();"><img src="../../img/binocolo.gif" alt="Cerca"></a>
            <% }else { %>
            	<a href="#" onClick="javascript:SelezionaDettaglio_onClick();"><img src="../../img/binocolo.gif" alt="Cerca"></a>
             <%}%> 
          <%}%> 
          <input type="hidden" name="descCorsoHid" value="<%= descCorso %>"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Anno</td>
        <td class="campo">
                  <af:textBox name="NUMANNO" type="integer"  required="true" 
                  classNameBase="input" title="Anno" value="<%= Utils.notNull(numAnno) %>" 
                  readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()" 
                  validateOnPost="true" maxlength="4" size="4" validateWithFunction="controllaAnno"/>
        
            <input type="hidden" name="numAnnoHid" value="<%= Utils.notNull(numAnno) %>"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Descrizione</td>
        <td class="campo">
          <af:textBox name="STRDESCRIZIONE" classNameBase="input" title="Descrizione" value="<%= strDescrizione %>" size="50" maxlength="100" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Contenuto</td>
        <td class="campo">
          <af:textBox name="STRCONTENUTO" classNameBase="input" title="Contenuto" value="<%= strContenuto %>" size="50" maxlength="100" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
        </td>
      </tr>
      <tr>
        <td colspan="2"><HR></td>
      </tr>

      <!-- Gestione Linguetta a scomparsa -->
      <tr>
        <td  colspan="4">
        <br/><br/>
        <table cellpadding="0" cellspacing="0" width="100%" border="0">
            <tr>
                <td width="40%" align="right"><b>Ente</b></td>
                <td><a  href="#" onClick="onOff()" style="CURSOR: hand;"> 
                <img id="imm1" alt="mostra/nascondi" src="../../img/chiuso.gif" border="0"></a>
                </td>
                <td width="70%">
                    <div id="labelVisulizza" style="display:">(visualizza)</div>
                    <div id="labelNascondi" style="display:none">(nascondi)</div>
                </td>
            </tr>
        </table>
        </td>
      </tr>
      <tr><td colspan="2">
      <div id="dett" style="display:none">
            <table cellpadding="0" cellspacing="0" border="0" width="100%">
            <tr>
              <td class="etichetta">Ente</td>
              <td class="campo">
                <af:textBox name="STRENTE" classNameBase="input" title="Ente" value="<%= strEnte %>" size="50" maxlength="100" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
              </td>
            </tr>
            <tr>
              <td class="etichetta">Comune</td>
              <td class="campo">
                <af:textBox name="CODCOMENTE"
                            value="<%=codComEnte%>" 
                            size="4"
                            classNameBase="input"
                            maxlength="4"
                            readonly="<%= String.valueOf(isImportato)%>"
                            disabled="false"
                            onKeyUp="javascript:PulisciRicerca(document.Frm1.CODCOMENTE, document.Frm1.CODCOMENTEHid, document.Frm1.strComEnte, document.Frm1.strComEnteHid, null, null, 'codice');"/>&nbsp;
                  <INPUT type="hidden" name="CODCOMENTEHid" value="<%=codComEnte%>">
                  
                <% if ( !readOnly ) { %>    
                  <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.CODCOMENTE, document.Frm1.strComEnte, null, 'codice','',null,'inserisciComEnteNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
                  <af:textBox name="strComEnte"
                              value="<%=strDescComEnte%>"
                              size="30" maxlength="50" 
                              onFocus="if(event.keyCode==13) {event.keyCode=9; this.blur(); }"
                              classNameBase="input"
                              disabled="<%= String.valueOf(isImportato)%>"
                              onKeyUp="javascript:PulisciRicerca(document.Frm1.CODCOMENTE, document.Frm1.CODCOMENTEHid, document.Frm1.strComEnte, document.Frm1.strComEnteHid, null, null,  'descrizione');"/>&nbsp;<A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.CODCOMENTE, document.Frm1.strComEnte,  null, 'descrizione','',null,'inserisciComEnteNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
                <%}else{%>
                  <af:textBox name="strComEnte" value="<%=strDescComEnte%>" size="30" maxlength="50"  classNameBase="input"
                    readonly="<%= String.valueOf(isImportato)%>"/>          
                <%}%>
                <INPUT type="hidden" name="strComEnteHid" value="" >&nbsp;
              </td>
            </tr>
            <tr>
              <td class="etichetta">Località</td>
              <td class="campo">
                <af:textBox name="STRLOCALITAENTE" classNameBase="input" title="Località Ente" value="<%= strLocalitaEnte %>" size="50" maxlength="50" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
              </td>
            </tr>
            <tr>
              <td class="etichetta">Indirizzo</td>
              <td class="campo">
                <af:textBox name="STRINDIRIZZOENTE" classNameBase="input" title="Indirizzo Ente" value="<%= strIndirizzoEnte %>" size="50" maxlength="60" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
              </td>
            </tr>
            </table>
        </div>
        </td>
      </tr>
    <!-- Fine gestione linguetta a scoparsa -->
      <tr>
        <td colspan="2"><HR></td>
      </tr>
      <tr>
        <td class="etichetta">Mesi</td>
        <td class="campo">
          <af:textBox name="NUMMESI" type="integer" classNameBase="input" title="Mesi" value="<%= Utils.notNull(numMesi) %>" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()" validateOnPost="true" />
        </td>
      </tr>
      <tr>
        <td class="etichetta">Ore</td>
        <td class="campo">
          <af:textBox name="NUMORE" type="integer" classNameBase="input" title="Ore" value="<%= Utils.notNull(numOre) %>" size="4" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"  validateOnPost="true" validateWithFunction="controllaOre" />
        di cui effettive
          <af:textBox name="NUMORESPESE" type="integer" classNameBase="input" title="Ore effettive" value="<%= Utils.notNull(numOreSpese) %>" size="4"  readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()" validateOnPost="True"  />
        </td>  
      </tr>
      <tr>
        <td class="etichetta">Completato</td>
        <td class="campo">
          <af:comboBox 
            title="Formazione completata" 
            name="FLGCOMPLETATO"
            classNameBase="input"
            disabled="<%= String.valueOf(isImportato)%>"
            onChange="FlgCompletato_fieldChanged()">
        
            <option value=""  <%if( "".equals(flgCompletato) )  { %>SELECTED="true"<% } %> ></option>
            <option value="S" <% if ( "S".equals(flgCompletato) ) { %>SELECTED="true"<% } %> >Si</option>
            <option value="N" <% if ( "N".equals(flgCompletato) ) { %>SELECTED="true"<% } %> >No</option>
          </af:comboBox>
        </td>
      </tr>
      <tr id="mot_ces" name="mot_ces" style="display:none">
        <td class="etichetta">Motivo cessazione</td>
        <td class="campo">
          <af:textBox name="STRMOTCESSAZIONE" classNameBase="input" title="Ore Spese" value="<%= strMotCessazione %>" size="50" maxlength="100" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Tipo Certificazione</td>
        <td class="campo">
          <af:comboBox name="CODTIPOCERTIFICATO"
                       title="Tipo Certificazione"
                       classNameBase="input"
                       moduleName="M_ListForProTipoCorso"
                       disabled="<%= String.valueOf(isImportato)%>"
                       onChange="fieldChanged()"
                       addBlank="true" blankValue="" selectedValue="<%=codTipoCertificato.toString()%>"/>
                       
        </td>
      </tr>
      <tr>
        <td class="etichetta">Ambito disciplinare</td>
        <td class="campo">
          <af:comboBox name="CDNAMBITODISCIPLINARE"
                       title="Codice ambito disciplinare"
                       classNameBase="input"
                       moduleName="M_ListForProAmbDiscip"
                       disabled="<%= String.valueOf(isImportato)%>"
                       onChange="fieldChanged()"
                       addBlank="true" blankValue="" selectedValue="<%= strAmbitoDisciplinare %>"/>
        </td>
      </tr>
      <tr>
        <td colspan="2"><HR></td>
      </tr>
        
      <tr>
        <td  colspan="4">
          <table cellpadding="1" cellspacing="0" width="100%" border="0">
          <tr>
            <td width="40%" align="right"><b>Stage</b></td>
            <td>
              <a  href="#" onClick="onOff1()" style="CURSOR: hand;"> 
              <img align="absmiddle" id="imm2" alt="mostra1/nascondi1" src="../../img/chiuso.gif" border="0">
              </a>
            </td>
            <td width="70%">
             <div id="labelVisulizza1" style="display:">(visualizza)</div>
             <div id="labelNascondi1" style="display:none">(nascondi)</div>
            </td>
          </tr>
          </table>
        </td>
      </tr>
     
      <tr>
        <td colspan="2">
        <div id="dett1" style="display:none">
		<table cellpadding="0" cellspacing="0" border="0" width="100%"> 
          <tr>
            <td class="etichetta">Corso con Stage</td>
            <td class="campo">
              <af:comboBox 
                title="Conoscenza certificata" 
                name="FLGSTAGE"
                classNameBase="input"
                disabled="<%= String.valueOf(isImportato)%>"
                onChange="CorsoConStage_onClick()">
                
                <option value=""  <% if ( "".equals(flgStage) )  { %>SELECTED="true"<% } %> ></option>
                <option value="S" <% if ( "S".equals(flgStage) ) { %>SELECTED="true"<% } %> >Si</option>
                <option value="N" <% if ( "N".equals(flgStage) ) { %>SELECTED="true"<% } %> >No</option>
              </af:comboBox>
            di ore
              <af:textBox name="NUMORESTAGE" classNameBase="input" title="Numero ore di stage" value="<%= Utils.notNull(numOreStage) %>" size="4" maxlength="100" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
            </td>
          </tr>
         </table>
             </div>
        </td>
      </tr>
  
      <tr id="str_azienda" name="str_azienda" style="display:none">
        <td class="etichetta">Azienda</td>
        <td class="campo">
          <af:textBox name="STRAZIENDA" title="Ragione Sociale dell'Azienda di Stage" value="<%= strAzienda %>" size="50" maxlength="100" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
        </td>
      </tr>
      <tr id="str_com_azienda" name="str_com_azienda" style="display:none">
        <td class="etichetta">Comune</td>
        <td class="campo">
          <af:textBox name="CODCOMAZIENDA" value="<%=codComAzienda%>" 
            size="4" maxlength="4" disabled="false"
            onKeyUp="javascript:PulisciRicerca(document.Frm1.CODCOMAZIENDA, document.Frm1.CODCOMAZIENDAHid, document.Frm1.strComAzienda, document.Frm1.strComAziendaHid, null, null,  'codice');"/>&nbsp;
          <INPUT type="hidden" id="CODCOMAZIENDAHid" name="CODCOMAZIENDAHid" value="<%=codComAzienda%>">
          <% if ( !readOnly ) { %>   
            <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.CODCOMAZIENDA, document.Frm1.strComAzienda, null, 'codice','',null,'inserisciCOMAZIENDANonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
            <af:textBox name="strComAzienda" value="<%=strDescComAzienda%>" size="30" maxlength="50" 
              onFocus="if(event.keyCode==13) {event.keyCode=9; this.blur(); }" onKeyUp="javascript:PulisciRicerca(document.Frm1.CODCOMAZIENDA, document.Frm1.CODCOMAZIENDAHid, document.Frm1.strComAzienda, document.Frm1.strComAziendaHid, null, null, 'descrizione');"/>
              &nbsp;<A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.CODCOMAZIENDA, document.Frm1.strComAzienda, null, 'descrizione','',null,'inserisciCOMAZIENDANonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
          <%}else{%>
            <af:textBox name="strComAzienda" value="<%=strDescComAzienda%>" size="30" maxlength="50" 
              readonly="<%= String.valueOf(isImportato)%>"/>    
          <%}%>
          <INPUT type="hidden" id="strComAziendaHid" name="strComAziendaHid" value="">&nbsp;
        </td>
      </tr>
      <tr id="str_localita" name="str_localita" style="display:none">
        <td class="etichetta">Località</td>
        <td class="campo">
          <af:textBox name="STRLOCALITAAZIENDA" title="Località dell'Azienda in Stage" value="<%= strLocalitaAzienda %>" size="50" maxlength="50" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
        </td>
      </tr>
      <tr id="str_indirizzo" name="str_indirizzo" style="display:none">
        <td class="etichetta">Indirizzo</td>
        <td class="campo">
          <af:textBox name="STRINDIRIZZOAZIENDA" title="Indirizzo dell'Azienda in Stage" value="<%= strIndirizzoAzienda %>" size="50" maxlength="60" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Note</td>
        <td class="campo">
           <af:textArea cols="30" 
                    rows="4" 
                    name="STRNOTACORSO"
                    classNameBase="textarea"
                    readonly="<%= String.valueOf(isImportato)%>"
                    onKeyUp="fieldChanged()"
                    maxlength="2000" value="<%= strNotaCorso %>"/>
        </td>
      </tr>

      <tr>
          <td colspan="4"><%@ include file="../patto/_associazioneDettaglioXPatto.inc"%>      
          </td>
      </tr>
      <tr>
        <td colspan="2" align="center">
        <%if(nuovo) {%>
             <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">&nbsp;&nbsp;
             <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer()">
        <%}%>
        <%if(readOnly){%>
            <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update()">
        <%}%>
        <%if(infStor){%>
          &nbsp;&nbsp;<input class="pulsante" type="button" class="pulsanti" name="annulla" value="Torna alle lista"
                 onclick="openPage('IndispTempInfStorPage','&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%=_cdnFunz%>')">
         <%} %>
        </td>
      </tr>
    <%}%>
    <%if(!nuovo) {%>          
      <tr>
        <td class="etichetta">Codice</td>
        <td class="campo">
          <af:textBox
            name="CODCORSO"
            title="Codice Corso"
            classNameBase="input"
            value="<%= codCorso %>"
            size="20"
            readonly="true"      
            maxlength="20"
            validateWithFunction="controlloCorso"/>      
        </td>
      </tr>
      <tr>
        <td class="etichetta">Corso</td>
        <td class="campo">
          <af:textArea cols="45" 
                    rows="5" 
                    name="DESCCORSO"
                    classNameBase="textArea"
                    readonly="<%= String.valueOf(isImportato)%>"
                    onKeyUp="fieldChanged();PulisciRricercaCorso('corso');"
                    inline="onKeyDown=\"SaveCorsoHidden();\" onkeypress=\" if (event.keyCode==13) { event.keyCode=9; this.blur(); } \""
                    maxlength="300"
                    value="<%= descCorso %>"/>
      
        <% if ( (!readOnly) && (! isImportato)) { %>
          <% if (canViewCodEdizione) { %>
            	<a href="#" onClick="javascript:ricercaAvanzataCorso_onClick();"><img src="../../img/binocolo.gif" alt="Cerca"></a>
            <%} else { %>
            	<a href="#" onClick="javascript:SelezionaDettaglio_onClick();"><img src="../../img/binocolo.gif" alt="Cerca"></a>
             <%}%> 
        <%}%> 
        <input type="hidden" name="descCorsoHid" value="<%= descCorso %>"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Anno</td>
        <td class="campo">
             <af:textBox name="NUMANNO" type="integer"  required="true" 
                  classNameBase="input" title="Anno" value="<%= Utils.notNull(numAnno) %>" 
                  readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()" 
                  validateOnPost="true" maxlength="4" size="4" validateWithFunction="controllaAnno"/>
                  
            <input type="hidden" name="numAnnoHid" value="<%= Utils.notNull(numAnno) %>"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Descrizione</td>
        <td class="campo">
          <af:textBox name="STRDESCRIZIONE" classNameBase="input" title="Descrizione" value="<%= strDescrizione %>" size="50" maxlength="100" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
        </td>
      </tr>
      <% if (canViewCodEdizione) { %>
      <tr>
        <td class="etichetta">Codice Edizione</td>
        <td class="campo">
          <af:textBox
            name="CODEDIZIONE"
            title="Codice Edizione"
            classNameBase="input"
            value="<%= codEdizione %>"
            size="5"
            readonly="true"      
            maxlength="3"/>      
        </td>
      </tr>
      <%} %>
      <tr>
        <td class="etichetta">Contenuto</td>
        <td class="campo">
          <af:textBox name="STRCONTENUTO" classNameBase="input" title="Contenuto" value="<%= strContenuto %>" size="50" maxlength="100" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
        </td>
      </tr>


            
<!-- Gestione Linguetta a scomparsa -->
      <tr>
        <td  colspan="2">
          <table cellpadding="1" cellspacing="0" width="100%" border="0">
              <tr>
                  <td width="40%" align="right"><b>Ente</b></td>
                  <td> 
                    <a  href="#" onClick="onOff()" style="CURSOR: hand;"> 
                    <img id="imm1" alt="mostra/nascondi" src="../../img/chiuso.gif" border="0">
                    </a>
                  </td>
                  <td width="70%">
                      <div id="labelVisulizza" style="display:">(visualizza)</div>
                      <div id="labelNascondi" style="display:none">(nascondi)</div>
                  </td>
              </tr>
          </table>
        </td>
      </tr>
      <tr>
      <tr>
        <td colspan="2">
        <div id="dett" style="display:none">
          <table cellpadding="0" cellspacing="0" border="0" width="100%">
          <tr>
            <td class="etichetta">Ente</td>
            <td class="campo">
              <af:textBox name="STRENTE" classNameBase="input" title="Ente" value="<%= strEnte %>" size="50" maxlength="100" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
            </td>
          </tr>
          <tr>
            <td class="etichetta">Comune</td>
            <td class="campo">
              <af:textBox name="CODCOMENTE"
                          value="<%=codComEnte%>" 
                          size="4"
                          classNameBase="input"
                          maxlength="4"
                          readonly="<%= String.valueOf(isImportato)%>"
                          disabled="false"
                          onKeyUp="javascript:PulisciRicerca(document.Frm1.CODCOMENTE, document.Frm1.CODCOMENTEHid, document.Frm1.strComEnte, document.Frm1.strComEnteHid, null, null, 'codice');"/>&nbsp;
                <INPUT type="hidden" name="CODCOMENTEHid" value="<%=codComEnte%>">
                
              <% if ( !readOnly ) { %>    
                <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.CODCOMENTE, document.Frm1.strComEnte, null, 'codice','',null,'inserisciComEnteNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
                <af:textBox name="strComEnte"
                            value="<%=strDescComEnte%>"
                            size="30" maxlength="50" 
                            onFocus="if(event.keyCode==13) {event.keyCode=9; this.blur(); }"
                            classNameBase="input"
                            disabled="<%= String.valueOf(isImportato)%>"
                            onKeyUp="javascript:PulisciRicerca(document.Frm1.CODCOMENTE, document.Frm1.CODCOMENTEHid, document.Frm1.strComEnte, document.Frm1.strComEnteHid, null, null,  'descrizione');"/>&nbsp;<A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.CODCOMENTE, document.Frm1.strComEnte,  null, 'descrizione','',null,'inserisciComEnteNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
              <%}else{%>
                <af:textBox name="strComEnte" value="<%=strDescComEnte%>" size="30" maxlength="50"  classNameBase="input"
                  readonly="<%= String.valueOf(isImportato)%>"/>          
              <%}%>
              <INPUT type="hidden" name="strComEnteHid" value="" >&nbsp;
            </td>
          </tr>
          <tr>
            <td class="etichetta">Località</td>
            <td class="campo">
              <af:textBox name="STRLOCALITAENTE" classNameBase="input" title="Località Ente" value="<%= strLocalitaEnte %>" size="50" maxlength="50" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
            </td>
          </tr>
          <tr>
            <td class="etichetta">Indirizzo</td>
            <td class="campo">
              <af:textBox name="STRINDIRIZZOENTE" classNameBase="input" title="Indirizzo Ente" value="<%= strIndirizzoEnte %>" size="50" maxlength="60" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
            </td>
          </tr>
          </table>
        </div>
        </td>
      </tr>
      <!-- Fine gestione linguetta a scoparsa -->
      <tr>
        <td colspan="2"><HR></td>
      </tr>
      <tr>
        <td class="etichetta">Mesi</td>
        <td class="campo">
        	<af:textBox name="NUMMESI" type="integer" classNameBase="input" title="Mesi" value="<%= Utils.notNull(numMesi) %>"  readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()" validateOnPost="true" />
        </td>
      </tr>
      <tr>
        <td class="etichetta">Ore</td>
        <td class="campo">
          <af:textBox name="NUMORE" type="integer" classNameBase="input" title="Ore" value="<%= Utils.notNull(numOre) %>" size="4" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()" validateOnPost="true" validateWithFunction="controllaOre"/>
        di cui effettive
          <af:textBox name="NUMORESPESE" type="integer" classNameBase="input" title="Ore effettive" value="<%= Utils.notNull(numOreSpese) %>" size="4"  readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()" validateOnPost="true"  />
        </td>  
      </tr>
      <tr>
        <td class="etichetta">Completato</td>
        <td class="campo">
          <af:comboBox 
            title="Conoscenza certificata" 
            name="FLGCOMPLETATO"
            classNameBase="input"
            disabled="<%= String.valueOf(isImportato)%>"
            onChange="FlgCompletato_fieldChanged()">
        
            <option value=""  <% if ( "".equals(flgCompletato) )  { %>SELECTED="true"<% } %> ></option>
            <option value="S" <% if ( "S".equals(flgCompletato) ) { %>SELECTED="true"<% } %> >Si</option>
            <option value="N" <% if ( "N".equals(flgCompletato) ) { %>SELECTED="true"<% } %> >No</option>
          </af:comboBox>
        </td>
      </tr>
      <tr id="mot_ces" name="mot_ces" style="display:none">
        <td class="etichetta">Motivo cessazione</td>
        <td class="campo">
          <af:textBox name="STRMOTCESSAZIONE" classNameBase="input" title="Ore Spese" value="<%= strMotCessazione %>" size="50" maxlength="100" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Tipo Certificazione</td>
        <td class="campo">
          <af:comboBox name="CODTIPOCERTIFICATO"
                       title="Tipo Certificazione"
                       classNameBase="input"
                       moduleName="M_ListForProTipoCorso"
                       disabled="<%= String.valueOf(isImportato)%>"
                       onChange="fieldChanged()"
                       addBlank="true" blankValue="" selectedValue="<%=codTipoCertificato.toString()%>"/>
                       
        </td>
      </tr>
      <tr>
        <td class="etichetta">Ambito disciplinare</td>
        <td class="campo">
          <af:comboBox name="CDNAMBITODISCIPLINARE"
                       title="Codice ambito disciplinare"
                       classNameBase="input"
                       moduleName="M_ListForProAmbDiscip"
                       disabled="<%= String.valueOf(isImportato)%>"
                       onChange="fieldChanged()"
                       addBlank="true" blankValue="" selectedValue="<%= strAmbitoDisciplinare %>"/>
        </td>
      </tr>
      <tr>
        <td colspan="2"><HR></td>
      </tr>
          
      <tr>
        <td  colspan="2">
        <table cellpadding="1" cellspacing="0" width="100%" border="0">
        <tr>
          <td width="40%" align="right"><b>Stage</b></td>
          <td>
            <a  href="#" onClick="onOff1()" style="CURSOR: hand;"> 
            <img id="imm2" alt="mostra1/nascondi1" src="../../img/chiuso.gif" border="0">
            </a>
          </td>
          <td width="70%">
             <div id="labelVisulizza1" style="display:">(visualizza)</div>
             <div id="labelNascondi1" style="display:none">(nascondi)</div>
          </td>
        </tr>
        </table>
        </td>
      </tr>
      <tr>
        <td colspan="2">
        <div id="dett1" style="display:none">
          <table cellpadding="0" cellspacing="0" border="0" width="100%">
          <tr>
            <td class="etichetta">Corso con Stage</td>
            <td class="campo">
              <af:comboBox 
                title="Conoscenza certificata" 
                name="FLGSTAGE"
                classNameBase="input"
                disabled="<%= String.valueOf(isImportato)%>"
                onChange="CorsoConStage_onClick()">
                
                <option value=""  <% if ( "".equals(flgStage) )  { %>SELECTED="true"<% } %> ></option>
                <option value="S" <% if ( "S".equals(flgStage) ) { %>SELECTED="true"<% } %> >Si</option>
                <option value="N" <% if ( "N".equals(flgStage) ) { %>SELECTED="true"<% } %> >No</option>
              </af:comboBox>
            di ore
              <af:textBox name="NUMORESTAGE" classNameBase="input" title="Numero ore di stage" value="<%= Utils.notNull(numOreStage) %>" size="4" maxlength="100" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
            </td>
          </tr>
          </table>
        </div>
        </td>
      </tr>
      <tr id="str_azienda" name="str_azienda" style="display:none">
        <td class="etichetta">Azienda</td>
        <td class="campo">
          <af:textBox name="STRAZIENDA" title="Ragione Sociale dell'Azienda di Stage" value="<%= strAzienda %>" size="50" maxlength="100" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
        </td>
      </tr>
      <tr id="str_com_azienda" name="str_com_azienda" style="display:none">
        <td class="etichetta">Comune</td>
        <td class="campo">
          <af:textBox name="CODCOMAZIENDA" value="<%=codComAzienda%>" 
            size="4" maxlength="4" disabled="false"
            onKeyUp="javascript:PulisciRicerca(document.Frm1.CODCOMAZIENDA, document.Frm1.CODCOMAZIENDAHid, document.Frm1.strComAzienda, document.Frm1.strComAziendaHid, null, null,  'codice');"/>&nbsp;
          <INPUT type="hidden" id="CODCOMAZIENDAHid" name="CODCOMAZIENDAHid" value="<%=codComAzienda%>">
          <% if ( !readOnly ) { %>    
            <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.CODCOMAZIENDA, document.Frm1.strComAzienda, null, 'codice','',null,'inserisciCOMAZIENDANonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
            <af:textBox name="strComAzienda" value="<%=strDescComAzienda%>" size="30" maxlength="50" 
              onFocus="if(event.keyCode==13) {event.keyCode=9; this.blur(); }" onKeyUp="javascript:PulisciRicerca(document.Frm1.CODCOMAZIENDA, document.Frm1.CODCOMAZIENDAHid, document.Frm1.strComAzienda, document.Frm1.strComAziendaHid, null, null, 'descrizione');"/>
              &nbsp;<A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.CODCOMAZIENDA, document.Frm1.strComAzienda, null, 'descrizione','',null,'inserisciCOMAZIENDANonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
          <%}else{%>
            <af:textBox name="strComAzienda" value="<%=strDescComAzienda%>" size="30" maxlength="50" 
              readonly="<%= String.valueOf(isImportato)%>"/>    
          <%}%>
          <INPUT type="hidden" id="strComAziendaHid" name="strComAziendaHid" value="">&nbsp;
        </td>
      </tr>
      <tr id="str_localita" name="str_localita" style="display:none">
        <td class="etichetta">Località</td>
        <td class="campo">
          <af:textBox name="STRLOCALITAAZIENDA" title="Località dell'Azienda in Stage" value="<%= strLocalitaAzienda %>" size="50" maxlength="50" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
        </td>
      </tr>
      <tr id="str_indirizzo" name="str_indirizzo" style="display:none">
        <td class="etichetta">Indirizzo</td>
        <td class="campo">
          <af:textBox name="STRINDIRIZZOAZIENDA" title="Indirizzo dell'Azienda in Stage" value="<%= strIndirizzoAzienda %>" size="50" maxlength="60" readonly="<%= String.valueOf(isImportato)%>" onKeyUp="fieldChanged()"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Note</td>
        <td class="campo">
           <af:textArea cols="30" 
                    rows="4" 
                    name="STRNOTACORSO"
                    classNameBase="textarea"
                    readonly="<%= String.valueOf(isImportato)%>"
                    onKeyUp="fieldChanged()"
                    maxlength="2000" value="<%= strNotaCorso %>"/>
        </td>
      </tr>
      <tr>
          <td colspan="2"><%@ include file="../patto/_associazioneDettaglioXPatto.inc"%>      
          </td>
      </tr>
      <tr>
        <td colspan="2" align="center">
        <%if(nuovo) {%>
            <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
            <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer()">
        <%} else  {%>
            <% //operatoreInfo.showHTML(out); %>
        </td>
      <tr>
    <%}%>
    
    <%if(!readOnly){%>     
      <tr>
        <td colspan="2" align="center">
              <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update()">
              <input class="pulsante" type="button" class="pulsanti" name="annulla" value="Chiudi"
                  onclick="chiudiLayer()">&nbsp;
            <% if (canDocAssociati) { %>   
              <input type="button" class="pulsanti" onclick="docAssociati('<%=cdnLavoratore%>','ForProPage','<%=_funzione%>','','<%=prgCorso%>')" value="Documenti associati">
            <% }%> 
        </td>
      </tr>       
    <%}%>
      <tr>
        <td colspan="2" align="center">
        <% operatoreInfo.showHTML(out); %>
        </td>
      </tr>           
  <%}  // (!NUOVO)%>
  </table>   
  <!-- Stondature ELEMENTO BOTTOM -->
  <%out.print(divStreamBottom);%>
</div>
<!-- LAYER - END --> 
  </af:form>
</body>

</html>