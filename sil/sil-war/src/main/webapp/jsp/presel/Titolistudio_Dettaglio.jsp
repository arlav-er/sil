<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.ProfileDataFilter,  
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, "CurrStudiMainPage");
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  PageAttribs attributi = new PageAttribs(user, "CurrStudiMainPage");
	
	boolean canModify = false;
	boolean canDelete = false;
	boolean canDocAssociati = false;
  boolean readOnlyStr = true;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      canModify = attributi.containsButton("inserisci");
      canDelete = attributi.containsButton("rimuovi");
      canDocAssociati = attributi.containsButton("DOCUMENTI_ASSOCIATI");
      
    	
    	if(!canModify){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();
    	}

    	if(!canDelete){
    		canDelete=false;
    	}else{
    		canDelete=filter.canEditLavoratore();
    	}
      
    }
    readOnlyStr = !canModify;
%>

<%
  // --- NOTE: Gestione Patto
  String PRG_TAB_DA_ASSOCIARE=null;
  String COD_LST_TAB="PR_STU";
  SourceBean row = null;
  // ---
 
  //String cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");
  boolean insert_done= serviceRequest.containsAttribute("inserisci");
  String prgStudio=(!insert_done)? (String)serviceRequest.getAttribute("prgStudio") : (serviceResponse.containsAttribute("M_GETPROSSIMOTITOLO.ROWS.ROW.prgstudio")?serviceResponse.getAttribute("M_GETPROSSIMOTITOLO.ROWS.ROW.prgstudio").toString():"");

  Vector map_stato_titoli_flagcompletato = serviceResponse.getAttributeAsVector("M_Map_Cod_Stato_Tit_studio_flagcompletato.ROWS.ROW");
  
  
  Vector tipiTitoliRows=null;
  tipiTitoliRows= serviceResponse.getAttributeAsVector("M_GETTIPOTITOLI.ROWS.ROW");  

  SourceBean row_titoloLavoratore= (SourceBean) serviceResponse.getAttribute("M_GETTITOLO.ROWS.ROW");
  String codTitolo=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "CODTITOLO");
  String codTitoloOld=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "CODTITOLOOLD");
  String desTitolo=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "desTitolo");
  String desTitoloOld=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "DESTITOLOOLD");
  String codTipoTitolo=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "codTipoTitolo");
  String desTipoTitolo=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "desTipoTitolo");
  String strSpecifica=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strSpecifica");
  String numAnno= row_titoloLavoratore.containsAttribute("numAnno") ? row_titoloLavoratore.getAttribute("numAnno").toString() : "";
  String flgPrincipale= StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "flgPrincipale");
  String strIstScolastico= StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strIstScolastico");
  String strIndirizzo= StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strIndirizzo"); 
  String strLocalita= StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strLocalita");  
  String codCom=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "codCom");  
  String strCom=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strCom"); 
  String provincia=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "provincia");
  strCom=strCom+(!provincia.equals("")?" ("+provincia+")":"");
  String CodMonoStatoTit=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "CodMonoStatoTit");

  String codMonoStato=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "codMonoStato");
  String flgCompletato=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "flgCompletato"); //visualizzare i dettagli del corso
  String numAnniFreq= row_titoloLavoratore.containsAttribute("numAnniFreq") ? row_titoloLavoratore.getAttribute("numAnniFreq").toString() : "";
  String numAnniPrev=row_titoloLavoratore.containsAttribute("numAnniPrev") ? row_titoloLavoratore.getAttribute("numAnniPrev").toString() : "";
  String strMotAbbandono=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strMotAbbandono");
  String strVoto=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strVoto");
  String strEsimi=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strEsimi");
  String strTitTesi=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strTitTesi");
  String strArgTesi=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strArgTesi");
  String flgLode=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "flgLode");
  String flgLaurea=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "flgLaurea");
  
  String cdnUtins=row_titoloLavoratore.containsAttribute("cdnUtins") ? row_titoloLavoratore.getAttribute("cdnUtins").toString() : "";
  String dtmins=row_titoloLavoratore.containsAttribute("dtmins") ? row_titoloLavoratore.getAttribute("dtmins").toString() : "";
  String cdnUtmod=row_titoloLavoratore.containsAttribute("cdnUtmod") ? row_titoloLavoratore.getAttribute("cdnUtmod").toString() : "";
  String dtmmod=row_titoloLavoratore.containsAttribute("dtmmod") ? row_titoloLavoratore.getAttribute("dtmmod").toString() : "";
  

  String prgPrincipaleGiaInserito = serviceResponse.containsAttribute("M_GetPrincTitolo.ROWS.ROW.prgstudio")?serviceResponse.getAttribute("M_GetPrincTitolo.ROWS.ROW.prgstudio").toString():"";
  String desPrincipaleGiaInserito = serviceResponse.containsAttribute("M_GetPrincTitolo.ROWS.ROW.strdescrizione")?serviceResponse.getAttribute("M_GetPrincTitolo.ROWS.ROW.strdescrizione").toString():"";
  
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);

  /*PageAttribs attributi = new PageAttribs(user, "CurrStudiMainPage");
  boolean canModify = attributi.containsButton("aggiorna");
  boolean canDelete = attributi.containsButton("rimuovi");
  /////////////////////////
    boolean readOnlyStr = !canModify;
  ///////////////////////////*/

  // --- NOTE: Gestione Patto
  PRG_TAB_DA_ASSOCIARE=prgStudio.toString();
  row = row_titoloLavoratore;
  // ---
%>
<html>

<head>
  <title>Titoli di studio</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">

  // --- NOTE: Gestione Patto
  function getFormObj() {return document.Frm1;}
  <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
  <%@ include file="../patto/_sezioneDinamica_script.inc"%>
  // ---

<!--
// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


function fieldChanged() {

  // DEBUG: Scommentare per vedere "field changed !" ad ogni cambiamento
  //alert("field changed !")  
    
  // NOTE: field-check solo se canModify 
  <% if ( canModify ) { %> 
    flagChanged = true;
  <% } %> 
}

function chiudi() {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  ok=true;
  if (flagChanged) {
     if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
         ok=false;
     }
  }
  if (ok) {
     var url = 'AdapterHTTP?PAGE=CurrStudiMainPage&cdnLavoratore=<%=cdnLavoratore%>&cdnFunzione=<%=_funzione%>';
     setWindowLocation(url);
  }
}



<% if (prgPrincipaleGiaInserito.equals("") ||  prgPrincipaleGiaInserito.equals(prgStudio))  {
      out.print("principale_gia_inserito=false;\n"); 
      out.print("des_principale_gia_inserito=\"\"");
   } else {
      out.print("principale_gia_inserito=true;\n");
      out.print("des_principale_gia_inserito=\""+desPrincipaleGiaInserito+"\";");
    
   }%>
   
function selectTitolo_onClick(codTitolo, codTitoloHid, strTitolo, strTipoTitolo) {	

  if (codTitolo.value==""){
    strTitolo.value="";
    strTipoTitolo.value="";
      
  }
  else if (codTitolo.value!=codTitoloHid.value){
    window.open("AdapterHTTP?PAGE=RicercaTitoloStudioPage&codTitolo="+codTitolo.value, "Titoli", 'toolbar=0, scrollbars=1');
  }
}


function codComuneUpperCase(inputName){

  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
	return true;
}

function toggleVisStato(){
	var divtesi = document.getElementById("tesi");
	var divcompletamento = document.getElementById("completamento");
	var divabbandono= document.getElementById("abbandono");
	var divfreq_prev=document.getElementById("freq_prev");
	var divconseguito = document.getElementById("conseguito");

	var vis_tesi=false;
	var vis_completamento=<%=flgCompletato.equals("S")?"true;":"false;"%>
	var vis_abbandono=false;
	var vis_freq_prev=false;
	
	var map = new Array();
	<%//creo un array [codice][flag] per confrontare se il valore della combo condiziona la visualizzazione
	//di altri campi. Inserire un record in de_titolo_italiano con flgcompleto = S per visualizzare i campi
	//relativi al completamento del titolo di studi.
	String codice = null;
	String flag= null;
	SourceBean rowFlag = null;
	for (int i=0;i<map_stato_titoli_flagcompletato.size();i++){
		rowFlag = (SourceBean) map_stato_titoli_flagcompletato.elementAt(i);
		codice = (String)rowFlag.getAttribute("codice");
		flag = StringUtils.getAttributeStrNotNull(rowFlag,"flag").equalsIgnoreCase("S")?"true;":"false;";
		out.print("map['"+codice+"']="+flag);
	}
	%>
	var codiceSelezionato = document.Frm1.codMonoStato.value;

	if (map[codiceSelezionato]){
		vis_tesi = (document.forms[0].flgLaurea.value=="S") ? true : false;
        vis_completamento=true;
        vis_abbandono=false;
        vis_freq_prev=false;
        vis_conseguito_estero=true;
        document.forms[0].flgPrincipale.disabled=false;
        document.forms[0].CodMonoStatoTit.disabled=false;
	}else{
		vis_tesi=false;
        vis_completamento=false;
        vis_freq_prev=true;
        if (codiceSelezionato=='A'){
    		vis_abbandono=true;
        }
    	else{
    		vis_abbandono=false;	
    	}
    	if(codiceSelezionato==''){
    		vis_freq_prev=false;
    	}
        
        vis_conseguito_estero=false;
        document.forms[0].flgPrincipale.value="N";
        document.forms[0].flgPrincipale.disabled=true;
        document.forms[0].CodMonoStatoTit.disabled=true;
	}

	
	

	divtesi.style.display=(vis_tesi)?"":"none";
	divcompletamento.style.display=(vis_completamento)?"":"none";
	divabbandono.style.display=(vis_abbandono)?"":"none";
	divfreq_prev.style.display=(vis_freq_prev)?"":"none";
	divconseguito.style.display=(vis_conseguito_estero)?"":"none";
}


function checkPrincipale(inputName)
{
  var ok=true;
  if (principale_gia_inserito && (document.forms[0].flgPrincipale.value=="S")) {
          if (!(confirm("Titolo di studio principale già inserito: "+des_principale_gia_inserito +"\n\n Assegnare lo status di principale al titolo corrente?")))
            { document.forms[0].flgPrincipale.focus();
              ok=false;}
    } 

  //Nel caso in cui venga modificato il flag di un titolo che era principale
  //impostandolo come non principale, segnaliamo la cosa:
  if (ok && document.forms[0].flgPrincipale.value=="N" && "S"=="<%=flgPrincipale%>"){
      if (!(confirm("Dopo l'aggiornamento questo titolo non sarà più il titolo principale. Continuare?"))) {
        document.forms[0].flgPrincipale.focus();
        ok=false;
      }
  } 
  return ok;
}




function clearTitolo() {

  if (document.Frm1.codTitolo.value=="") {   
    document.Frm1.codTitoloHid.value=""; 
    document.Frm1.strTitolo.value=""; 
    document.Frm1.strTipoTitolo.value=""; 
  }

}

function ricercaAvanzataTitoliStudio() {
  window.open("AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage", "Titoli", 'toolbar=0, scrollbars=1');
}

function controllaTitoloStudio(codTitolo) {
  var strCodTitolo = new String(codTitolo);
  if (strCodTitolo.substring(strCodTitolo.length-2,strCodTitolo.length) != '00') {
    return true;
  }
  else {
    if (confirm('Non è stato indicato un titolo di studio specifico, continuare ?')) {
      return true;
    }
    else {
      return false;
    }
  }
}

function controllaStatoCorso() {
	ret = true;
	switch (document.forms[0].codMonoStato.value)  { 
    	case "C": 
    		document.forms[0].strMotAbbandono.value="";    	
    		document.forms[0].numAnniFreq.value="";
    		document.forms[0].numAnniPrev.value="";
    		document.forms[0].strMotAbbandono.value="";    		
    		break;
    	case "P": 
    		document.forms[0].strMotAbbandono.value="";    	
    		document.forms[0].numAnniFreq.value="";
    		document.forms[0].numAnniPrev.value="";
    		document.forms[0].strMotAbbandono.value="";    		
    		break;
    	case "I":
    		document.forms[0].CodMonoStatoTit.value="";
    		document.forms[0].strMotAbbandono.value="";
    		document.forms[0].strVoto.value="";
    		document.forms[0].strEsimi.value="";
    		document.forms[0].numAnno.value="";    		
    		document.forms[0].strTitTesi.value="";
    		document.forms[0].strArgTesi.value="";
    		document.forms[0].flgLode.value="";
    		break;
    	case "A":
    		document.forms[0].CodMonoStatoTit.value="";
    		document.forms[0].strVoto.value="";
    		document.forms[0].strEsimi.value="";
    		document.forms[0].numAnno.value="";
    		document.forms[0].strTitTesi.value="";
    		document.forms[0].strArgTesi.value="";
    		document.forms[0].flgLode.value="";
    		break;
    	default:
    		alert("impossibile continuare: selezionare uno stato di completamento del corso.");
    		ret=false;
	}
	return ret;
}

function DettaglioTitolo(prgStudio) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=CurrStudiTitoloPage";
    s += "&MODULE=M_GetTitolo";
    s += "&PRGSTUDIO=" + prgStudio;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%= _funzione %>";

    setWindowLocation(s);
  }

 function DeleteTitolo(prgStudio, tipo, principale, PRGLAVPATTOSCELTA) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var boolprincipale;
    
    if (principale=='S') 
       boolPrincipale=true;
    else
       boolPrincipale=false;   
    var s="Sicuri di voler rimuovere il titolo '";
    s+= tipo.replace('^','\'')+"'?\n";
    s+=(boolprincipale)?"\nAttenzione: si sta eliminando il titolo di studio principale.":"";
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=CurrStudiDelTitoloPage";
      s += "&MODULE=M_DelTitolo";
      s += "&PRGSTUDIO=" + prgStudio;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";
      // --- NOTE: Gestione Patto
      s += "&" + getParametersForPatto(PRGLAVPATTOSCELTA);
      // ---

      setWindowLocation(s);
    }
  }
  
  
function controllaAnnoConseguimento(){
	var strAnno =document.Frm1.numAnno.value;
	var anno = parseInt(strAnno, 10);	
	if(anno >= 1900 && anno <= 2100 || strAnno == ""){
		return true;
	}else{
		alert("Anno di conseguimento non valido");
		return false;
	}
}

-->


  </SCRIPT>

  <script language="Javascript" src="../../js/docAssocia.js"></script>

<script language="javascript">
  var flgInsert = false;
</script>

</head>

<body class="gestione" onLoad="toggleVisStato();">

  <%
    
    
    Linguette l = new Linguette( user,  _funzione, "CurrStudiMainPage", new BigDecimal(cdnLavoratore));
    infCorrentiLav.show(out); 
    l.show(out);
    
  %>   

  
  <p align="center">
      <af:list moduleName="M_GetLavoratoreTitoli" skipNavigationButton="1"
             canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
             canInsert="<%=canModify ? \"1\" : \"0\"%>" 
             jsSelect="DettaglioTitolo" jsDelete="DeleteTitolo"/>          
  </p>
  
<!-- LAYER -->
<%
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>
  
<div id="divLayerDett" name="divLayerDett" class="t_layerDett"
 style="position:absolute; width:80%; left:50; top:100px; z-index:6; display:'';">


<!-- Stondature ELEMENTO TOP -->
<%out.print(divStreamTop);%>
  <af:showMessages prefix="M_SaveTitolo" />
  <af:showMessages prefix="M_InsertTitolo" />
  <af:showErrors />
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaPatto() && controllaTitoloStudio(Frm1.codTitolo.value) && controllaStatoAtto(flgInsert,this) && controllaStatoCorso() && controllaAnnoConseguimento()">

<table width="100%" cellpadding="0" cellspacing="0">

 <tr width="100%"><td>
    <table width="100%" cellpadding="0" cellspacing="0">
      <tr width="100%">
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">Corso di studio</td>
        <td width="16" height="16" onClick="chiudi()" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>
  </td></tr>

          <!-- NOTE: Gestione Patto
                   aggiungere onSubmit="controllaPatto()"
          -->
  
  <tr><td>        
            <table class="main" width="100%">
              <!--tr>
                <td colspan="4" ><center><b>Corso di studio</b></center></td>
              </tr-->
              <tr valign="top">
                <td class="etichetta">Codice</td>
                <td class="campo" colspan="3">
                    <af:textBox classNameBase="input" title="Codice del titolo" value="<%=codTitolo%>" name="codTitolo" size="10" maxlength="8" onBlur="clearTitolo();" required="True" onKeyUp="fieldChanged();" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;                    
                    <af:textBox type="hidden" name="codTitoloHid" value="<%=codTitolo%>" />
                    <%if (canModify) { %>
                        <A href="javascript:selectTitolo_onClick(document.Frm1.codTitolo, document.Frm1.codTitoloHid, document.Frm1.strTitolo,  document.Frm1.strTipoTitolo);">
                          <img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
                        <A href="javascript:ricercaAvanzataTitoliStudio();">
                          Ricerca avanzata
                        </A>
                    <%}%>
                </td>
              </tr>
              <tr valign="top">
                <td class="etichetta">Tipo</td>
                <td class="campo" colspan="3">
                  <af:textBox type="hidden" name="flgLaurea" value="<%=flgLaurea%>" />
                  <af:textArea cols="40" 
                               rows="3"  
                               value="<%=desTipoTitolo%>"
                               classNameBase="textarea" title="Tipo del titolo" name="strTipoTitolo" readonly="true"  /> 
                </td>
              </tr>
              <tr>
                <td class="etichetta">Corso</td>
                <td class="campo" colspan="1">
                  <af:textArea cols="40" 
                               rows="4"  
                               classNameBase="textarea" name="strTitolo" value="<%=desTitolo%>" readonly="true" required="true" />
                </td>
                <%if ((desTitoloOld != null)&&(desTitoloOld != "")) {%>
               <td class="etichetta">Dati<br/>salvati</td>
                <td class="campo" colspan="1">
                  <af:textArea cols="40" 
                               rows="4"  
                               classNameBase="textarea" name="strTitoloOld" value='<%=codTitoloOld + " - " + desTitoloOld%>' readonly="true" />
                </td>
                <%}%>
              </tr>
              <tr>
                <td class="etichetta">Specifica</td>
                <td class="campo" colspan="3">
                  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strSpecifica" value="<%=strSpecifica%>" size="50" maxlength="200" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;
                </td>
              </tr>
              <tr>
                <td class="etichetta">Principale</td>
                <td class="campo" colspan="3">
                  <af:comboBox classNameBase="input" onChange="fieldChanged();" name="flgPrincipale" validateWithFunction="checkPrincipale" disabled="<%= String.valueOf(!canModify) %>">
                    <OPTION value="S" <%if (flgPrincipale.equals("S")) out.print(" selected=\"true\" ");%>>S</OPTION>
                    <OPTION value="N" <%if (flgPrincipale.equals("N")) out.print (" selected=\"true\" ");%>>N</OPTION>
                  </af:comboBox>
                </td>
              </tr>
              <tr>
                <td colspan="4"><HR></td>
              </tr>
             
              <tr>
                <td class="etichetta">Stato</td>
                <td class="campo" colspan="3">
                    <af:comboBox  addBlank="true" selectedValue="<%=codMonoStato %>" moduleName="M_GetStatiTitoliStudio" classNameBase="input" name="codMonoStato" required="true"  onChange="javascript:toggleVisStato();fieldChanged();" disabled="<%= String.valueOf(!canModify) %>"/>
                </td>
              </tr>
              <tr><td>
              	
           	  </td></tr>
           	  <%--
                <td class="etichetta" nowrap>Conseguito all'estero</td>
                <td class="campo" nowrap>
                      <af:comboBox name="CodMonoStatoTit" size="1" title="Corso Estero"
                         multiple="false" onChange="fieldChanged();" 
                         focusOn="false" moduleName="M_ListTitoloEstero"
                         addBlank="true" blankValue="" selectedValue="<%=CodMonoStatoTit%>" 
                         disabled="<%= String.valueOf(!canModify) %>"/>
                         
                </td>
              </tr>
                    --%> 
            </table>
  </td></tr>
    <tr><td>
<table class="main" width="100%" id="conseguito" style="display:none">
              		<tr>
	              		<td></td>
	              		<td></td>
	              		<td></td>
	              		<td></td>
              		</tr>
              		<tr>
                		<td class="etichetta" nowrap>Conseguito all'estero</td>
                		<td class="campo" nowrap colspan="3">
                      <af:comboBox name="CodMonoStatoTit" size="1" title="Corso Estero"
                         multiple="false" onChange="fieldChanged();" 
                         focusOn="false" moduleName="M_ListTitoloEstero"
                         addBlank="true" blankValue="" selectedValue="<%=CodMonoStatoTit%>" 
                         disabled="<%= String.valueOf(!canModify) %>"/>                         
                		</td>
              		</tr>
           		</table>
</td></tr>           		
  <tr><td>
          <table class="main" width="100%" id="abbandono" style="display:none">
              <tr>
	              		<td></td>
	              		<td></td>
	              		<td></td>
	              		<td></td>
              		</tr>
              <tr>
                <td class="etichetta">Motivo dell'abbandono</td>
                <td class="campo" colspan="3"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strMotAbbandono" value="<%=strMotAbbandono%>" size="50" maxlength="100" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
           </table>
  </td></tr>
  <tr><td>        
          <table class="main" width="100%" id="freq_prev" style="display:none">           
              <tr>
	              		<td></td>
	              		<td></td>
	              		<td></td>
	              		<td></td>
              		</tr>
              <tr>
                <td class="etichetta">Numero anni frequentati/previsti</td>
                <td class="campo" colspan="3">
                        <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="integer" name="numAnniFreq" value="<%=numAnniFreq%>" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;/&nbsp;
                        <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="integer" name="numAnniPrev" value="<%=numAnniPrev%>" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
          </table>
  </td></tr>
          
  <tr><td>
          <table class="main" width="100%" id="completamento" style="display:none">            
              <tr>
	              		<td></td>
	              		<td></td>
	              		<td></td>
	              		<td></td>
              		</tr>
              <tr>
                <td class="etichetta">Anno di conseguimento</td>
                <td class="campo" colspan="3"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="integer" name="numAnno" value="<%=numAnno%>" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Voto</td>
                <td class="campo" colspan="3">
                    <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strVoto" maxlength="10" value="<%=strVoto%>" readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;/&nbsp;
                    <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strEsimi" maxlength="100" value="<%=strEsimi%>" readonly="<%= String.valueOf(!canModify) %>"/>
                </td>
              </tr>
          </table>
  </td></tr>

  <tr><td>
         <table class="main" width="100%">
              <tr>
                <td colspan="4"><HR></td>
              </tr>

              <tr>
                <td class="etichetta">Istituto scolastico</td>
                <td class="campo" colspan="3">
                  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strIstScolastico" value="<%=strIstScolastico%>" size="50" maxlength="100" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Indirizzo</td>
                <td class="campo" colspan="3" ><af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strIndirizzo" value="<%=strIndirizzo%>" size="50" maxlength="60" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Località</td>
                <td class="campo" colspan="3"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strLocalita" value="<%=strLocalita%>" size="50" maxlength="50" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Comune</td>
                <td class="campo" colspan="3">
                  <af:textBox classNameBase="input"
                              type="text"
                              name="codCom"
                              value="<%=codCom%>"
                              size="4"
                              maxlength="4" 
                              validateWithFunction="codComuneUpperCase"
                              readonly="<%= String.valueOf(!canModify) %>"
                              onKeyUp="javascript:PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.strCom, document.Frm1.strComHid, null, null, 'codice');"/>&nbsp;
                <% if(canModify) {%>
                  <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codCom, document.Frm1.strCom, null, 'codice', '',null,'inserisciComuneNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
                <%}%>
                <af:textBox type="hidden" name="codComHid" value="<%=codCom%>" />
                <af:textBox classNameBase="input"
                            type="text"
                            name="strCom"
                            value="<%=strCom%>"
                            size="30"
                            maxlength="50"
                            readonly="<%= String.valueOf(!canModify) %>"
                            inline="onkeypress=\"if(event.keyCode==13) { event.keyCode=9; this.blur(); }\""
                            onKeyUp="javascript:PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.strCom, document.Frm1.strComHid, null, null, 'descrizione');"/>&nbsp;
                <% if(canModify) {%>
                <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codCom, Frm1.strCom, null, 'descrizione','',null,'inserisciComuneNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>&nbsp;
                <%}%>
                <af:textBox type="hidden" name="strComHid" value="<%=strCom%>" />&nbsp;
                </td>                
              </tr>    
              <tr>
                 <td colspan="4"><HR></td>
              </tr>

            </table>
   </td></tr>

   <tr><td>
            <table id="tesi" style="display:none">
               <tr>
                  <td class="etichetta">Titolo tesi</td>
                  <td class="campo"><af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strTitTesi" value="<%=strTitTesi%>" cols="30" maxlength="200" readonly="<%= String.valueOf(!canModify) %>" />
                  </td>
                  <td class="etichetta">Argomento tesi</td>
                  <td class="campo"><af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strArgTesi" value="<%=strArgTesi%>" cols="30" maxlength="1000" readonly="<%= String.valueOf(!canModify) %>" />
                  </td>
                  <td class="etichetta">Lode</td>
                  <td class="campo">
                      <af:comboBox name="flgLode" onChange="fieldChanged();" disabled="<%= String.valueOf(!canModify) %>" >
                          <OPTION value=""></OPTION>
                          <OPTION value="S" <%if (flgLode.equals("S")) out.print(" selected=\"true\" ");%>>S</OPTION>
                          <OPTION value="N" <%if (flgLode.equals("N")) out.print(" selected=\"true\" ");%>>N</OPTION>
                      </af:comboBox>
                  </td>
                </tr>
              </table>
  </td></tr>
  <tr><td>
            
              <!-- NOTE: Gestione Patto-->
              <%@ include file="../patto/_associazioneDettaglioXPatto.inc"%>
              <!--  -->
  </td></tr>
   <tr><td align="center">
                <input type="hidden" name="PAGE" value="CurrStudiTitoloPage"/>
                <input type="hidden" name="cdnLavoratore" value="<%= cdnLavoratore %>"/>
                <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
                <input type="hidden" name="PRGStudio" value="<%=prgStudio%>" />
                <input type="hidden" name="flgNonPiuPrincipale" value="" />
<% if (canModify) { %>
              <input class="pulsante" type="submit" name="salva" value="Aggiorna" />
<% }%>
              <input class="pulsante" type="button" name="annulla" value="Chiudi" onclick="chiudi();">&nbsp;              
<% if (canDocAssociati) { %>   
              <input type="button" class="pulsanti" onclick="docAssociati('<%=cdnLavoratore%>','CurrStudiMainPage','<%=_funzione%>','','<%=prgStudio%>')" value="Documenti associati">
<% }%>              
   </td></tr>
   <tr><td align="center"><%operatoreInfo.showHTML(out);%></td></tr>
  </table>
</af:form>  
  <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>
    </div>
<!-- LAYER - END --> 


</body>

</html>