<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                   
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.text.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
  
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  
  
  boolean isEsito = false;
  boolean isEsitoOK = false;
  boolean isCateneOk = true;
  String esistenzaCatene = "no";
  String strEsito = null;	
  Vector movimentiFiltrati= null; 	
  BigDecimal prgForzatura = null;
	if(serviceResponse.containsAttribute("M_ControllaModificheForzatura.ESITO") ){
		isEsito = true;
		strEsito =(String) serviceResponse.getAttribute("M_ControllaModificheForzatura.ESITO");
		if(StringUtils.isFilledNoBlank(strEsito) && strEsito.equalsIgnoreCase("OK")){
			isEsitoOK = true;
			if(serviceResponse.containsAttribute("M_VerificaCateneMovLav")){
				Vector cateneErrate= serviceResponse.getAttributeAsVector("M_VerificaCateneMovLav.ROWS.ROW");   
				if(cateneErrate!=null && !cateneErrate.isEmpty()){
					isCateneOk = false;
					esistenzaCatene="si";
				}
			}
		}else if(StringUtils.isFilledNoBlank(strEsito) && strEsito.equalsIgnoreCase("KO")){
			if(serviceResponse.containsAttribute("M_ListaForzatureErrateConMovimenti")){
				movimentiFiltrati = serviceResponse.getAttributeAsVector("M_ListaForzatureErrateConMovimenti.ROWS.ROW");   
				prgForzatura=(BigDecimal) serviceResponse.getAttribute("M_CONTROLLAMODIFICHEFORZATURA.PRGFORZATURA");
			}
		}else{
			isEsitoOK = false;
		}
	}else{
		movimentiFiltrati = serviceResponse.getAttributeAsVector("M_ListaForzaMovModificabili.ROWS.ROW");   
	}
	
  //Ottengo il vettore con tutti i movimenti ricercati
	 boolean permettiValidazione = (movimentiFiltrati!=null && movimentiFiltrati.size()>=1)? true : false;

  String root_page = StringUtils.getAttributeStrNotNull(serviceRequest,"ROOT_PAGE");
  
	String numeroPagine = "";
	String numeroRecords = "";
 
  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  
  //Creazione del token per il pulsante di ritorno alla lista,
  // devo farlo a manina visto che non uso il tag af:list
  Integer numpage = (Integer) serviceResponse.getAttribute("M_ListaForzaMovModificabili.ROWS.CURRENT_PAGE");
  String upperPageName = ((String) serviceRequest.getAttribute("PAGE")).toUpperCase();
  
  String providerURL = "PAGE=" + upperPageName + "&MODULE=M_ListaForzaMovModificabili&";
  
  StringBuffer queryStringBuffer = new StringBuffer();
  Vector queryParameters = serviceRequest.getContainedAttributes();
  for (int i = 0; i < queryParameters.size(); i++) {
	SourceBeanAttribute parameter = (SourceBeanAttribute) queryParameters.get(i);
	String parameterKey = parameter.getKey();
	if ( parameterKey.equalsIgnoreCase("PAGE")
		|| parameterKey.equalsIgnoreCase("MODULE")
		|| parameterKey.equalsIgnoreCase("MESSAGE")
		|| parameterKey.equalsIgnoreCase("LIST_PAGE")
		|| parameterKey.equalsIgnoreCase("LIST_NOCACHE"))
		continue;
	String parameterValue = parameter.getValue().toString();
	queryStringBuffer.append(JavaScript.escape(parameterKey.toUpperCase()));
	queryStringBuffer.append("=");
	queryStringBuffer.append(JavaScript.escape(parameterValue));
	queryStringBuffer.append("&");
  } 
//  String queryString = queryStringBuffer.toString();
  
  sessionContainer.setAttribute("_TOKEN_" + upperPageName, 
  	providerURL + queryStringBuffer.toString() + "MESSAGE=LIST_PAGE&LIST_PAGE=" + numpage);
   
  //parametri per tornare indietro
    String cdnLav=StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
    String prgAz=StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
    String prgUn=StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
    String prgAzUt=StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDAUt");
    String prgUnUt=StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITAUt");
    String datMovDa=StringUtils.getAttributeStrNotNull(serviceRequest, "datmovimentoda");
    String datMovA=StringUtils.getAttributeStrNotNull(serviceRequest, "datmovimentoa");
    String tipoMovParam=StringUtils.getAttributeStrNotNull(serviceRequest, "tipoMovimento");
	String statoParam=StringUtils.getAttributeStrNotNull(serviceRequest, "CODSTATOATTO");
	String statoMovimentoDescr=StringUtils.getAttributeStrNotNull(serviceRequest, "statoDescr");
	String movimentoDescr=StringUtils.getAttributeStrNotNull(serviceRequest, "movimentoDescr");
	String ragioneSocialeUt=StringUtils.getAttributeStrNotNull(serviceRequest, "ragioneSocialeUt");
	String ragioneSociale=StringUtils.getAttributeStrNotNull(serviceRequest, "ragioneSociale");
	String datanasc=StringUtils.getAttributeStrNotNull(serviceRequest, "datnasc");
	String nome=StringUtils.getAttributeStrNotNull(serviceRequest, "nome");
	String cognome=StringUtils.getAttributeStrNotNull(serviceRequest, "cognome");
	String codiceFiscaleLavoratore=StringUtils.getAttributeStrNotNull(serviceRequest, "codiceFiscaleLavoratore");
	
	String txtFilters="Lavoratore: "+codiceFiscaleLavoratore+" - "+cognome.toUpperCase()+" "+nome.toUpperCase()+" - "+datanasc;
%>
 
 
<html>
  <head>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
   <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
    
    <af:linkScript path="../../js/"/>
  <script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
  <script src="../../js/jqueryui/jquery-ui.min.js"></script>
    <title>Forzature Movimenti</title>
     <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
    <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
<!--      
    var numRighe = 15;
 
    var cateneVar = "<%= esistenzaCatene%>";
    
    function controllaCatene(){
    	if(cateneVar == "si"){
    		alert("Attenzione: ci sono ancora catene errate nei movimenti del lavoratore, verificare.");
    	}
    }
    
    var totalePagine = <%=serviceResponse.getAttribute("M_ListaForzaMovModificabili.ROWS.NUM_PAGES")%>;
    var posizioneAttuale = <%=serviceResponse.getAttribute("M_ListaForzaMovModificabili.ROWS.CURRENT_PAGE")%>;

    function calcolaPrecedente() {
      if (posizioneAttuale <= 1 ) {return 1;}
      else return posizioneAttuale - 1;
    }

    function calcolaSuccessiva() {
     if (totalePagine==-1)  
	       return posizioneAttuale + 1;
	            
      if (posizioneAttuale >= totalePagine ) {
      	return totalePagine;
      }      
    }
    
    //Effettua la navigazione tra le pagine
    function naviga(listpage, message) {
      if (listpage != posizioneAttuale) {
	      document.FormListaNavigazione.LIST_PAGE.value = listpage;
	      document.FormListaNavigazione.MESSAGE.value = message;
	      doFormSubmit(document.FormListaNavigazione);
      }
	}	
	function tornaAllaLista() {
			 if (isInSubmit()) return;
		  
		     url="AdapterHTTP?PAGE=ListaForzaModMovPage";
		     url += "&CDNFUNZIONE="+"<%=_funzione%>";
		     url += "&CDNLAVORATORE="+"<%=cdnLav%>";
		     url += "&PRGAZIENDA="+"<%=prgAz%>";
		     url += "&PRGUNITA="+"<%=prgUn%>";
		     url += "&PRGAZIENDAUt="+"<%=prgAzUt%>";
		     url += "&PRGUNITAUt="+"<%=prgUnUt%>";
		     url += "&datmovimentoda="+"<%=datMovDa%>";
		     url += "&datmovimentoa="+"<%=datMovA%>";
		     url += "&tipoMovimento="+"<%=tipoMovParam%>";
		     url += "&CODSTATOATTO="+"<%=statoParam%>";
		     url += "&statoDescr="+"<%=statoMovimentoDescr%>";
		     url += "&movimentoDescr="+"<%=movimentoDescr%>";
		     url += "&ragioneSocialeUt="+"<%=ragioneSocialeUt%>";
		     url += "&ragioneSociale="+"<%=ragioneSociale%>";
		     url += "&datnasc="+"<%=datanasc%>";
		     url += "&nome="+"<%=nome%>";
		     url += "&cognome="+"<%=cognome%>";
		     url += "&codiceFiscaleLavoratore="+"<%=codiceFiscaleLavoratore%>";
		     
		     setWindowLocation(url);
	 
	}

	function apriRicalcola(){
		 if (isInSubmit()) return;
		  
	     url="AdapterHTTP?PAGE=AmmCalcolaStatoOccAllaDataPage";
	     url += "&CDNFUNZIONE="+"<%=_funzione%>";
	     url += "&CDNLAVORATORE="+"<%=cdnLav%>";
	     
	     setWindowLocation(url);
	}
	
function apriDettaglio( prgMovimento ){
	var f = "AdapterHTTP?PAGE=PercorsoMovimentiCollegatiPage" + 
	"&PrgMovimentoColl=" + prgMovimento 
	 ;
	 
	var t = "_blank";
	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=580,height=400,top=100,left=100"
	window.open(f, t, feat);
}
function getStyle(el, cssprop) {
	if (el.currentStyle)
		return el.currentStyle[cssprop];	 // IE
	else if (document.defaultView && document.defaultView.getComputedStyle)
		return document.defaultView.getComputedStyle(el, "")[cssprop];	// Firefox
	else
		return el.style[cssprop]; //try and get inline style
}

var oldColor, oldText, padTop, padBottom = "";
var countModifiche=-1;
var t = "<font color=\"red\">";
var n = t.length;

var editDone = "";

var idInput;
  function callbackPrecSucc (){
	var inputIdVarDoc = idInput.split("_")[1];
	var idRigaMod ="rigaMod_"+inputIdVarDoc;
	var modificaEffettuata = document.getElementById(idRigaMod).value;
	if(modificaEffettuata == "MOD"){
		countModifiche = countModifiche+1;
	}
}
  
function editaPrgPrecSucc(td, tipoMov) {
	 
	if (td.firstChild && td.firstChild.tagName == "INPUT")
		return;

	oldText = td.innerHTML.trim();
	var str = oldText;
	if(str.lastIndexOf("<font color=\"red\">") > -1){
		oldText = str.substring(str.lastIndexOf("<font color=\"red\">")+n, str.lastIndexOf("</font>"));
	}
	//oldColor = getStyle(td, "backgroundColor");
	padTop = getStyle(td, "paddingTop");
	padBottom = getStyle(td, "paddingBottom");

	/* var input = document.createElement("input");
	input.value = oldText;

	//// ------- input style -------
	var left = getStyle(td, "paddingLeft").replace("px", "");
	var right = getStyle(td, "paddingRight").replace("px", "");
	input.style.width = td.offsetWidth - left - right - (td.clientLeft * 2) - 2 + "px";
	input.style.height = td.offsetHeight - (td.clientTop * 2) - 2 + "px";
	input.style.border = "0px";
	input.style.fontFamily = "inherit";
	input.style.fontSize = "inherit";
	input.style.textAlign = "inherit";
	input.style.backgroundColor = "LightGoldenRodYellow";
	
	var inputNameForm =  "nameInput_"+ td.id.split("$")[1];
	 
	input.id =inputNameForm; */
	idInput ="id_"+ td.id.split("$")[1];
	
	var temp = idInput.split("_");
	var idCdnLav = temp[0] + "_" + temp[1] + "_" + "cdnLavoratore";
	
	var idPrg = temp[0] + "_" + temp[1] + "_" + "prgMovimento";
	
	var cdnLav = document.getElementById(idCdnLav).value;
	var prgMovEscludere =  document.getElementById(idPrg).value;
	
	//apriListaPrecSucc(prgMovEscludere, tipoMov, cdnLav, idInput, td.id, oldText );

	apriListaPrecSucc(prgMovEscludere, tipoMov, cdnLav, idInput, td.id, oldText );
	
	/* input.onblur = function () { endEdit(this, idInput); };

	td.innerHTML = "";
	td.style.paddingTop = "0px";
	td.style.paddingBottom = "0px";
	td.style.backgroundColor = "LightGoldenRodYellow";
	td.insertBefore(input, td.firstChild);
	input.select(); */
}

function comboPrgPrecSucc(td, tipoMov, indice)  {
	
	editDone = "";
	
	if (td.firstChild && td.firstChild.tagName == "SELECT") {
		return;
	}
	oldText = td.innerHTML.trim(); 
	var str = oldText;
	if(str.lastIndexOf("<font color=\"red\">") > -1){
		oldText = str.substring(str.lastIndexOf("<font color=\"red\">")+n, str.lastIndexOf("</font>"));
	}
	
	 var selObj = document.createElement("select");
	 selObj.setAttribute("class","inputEdit");
	 
	 selObj.style.backgroundColor = "LightGoldenRodYellow";
	 
	 var nullValue = document.createElement("option");
	 var testo = document.createTextNode("Elimina valore");
	 nullValue.setAttribute("value","");
	 nullValue.appendChild(testo);
	 selObj.appendChild(nullValue);
	 
	 var table = document.getElementById("tabellaForzature");
	 var eliminate = document.Frm1.rigaEliminate.value;
	 var numeroRigheTot = parseInt(document.Frm1.numeroRighe.value);
	 var numeroRighe = numeroRigheTot;
	 var indiciEliminati;
	 
	 var currentName = "id_"+indice+"_prgMovimento";
	 var currentPrg = document.getElementById(currentName).value;
	 
	if(eliminate && eliminate!="") {
			indiciEliminati = eliminate.split(",");
			numeroRighe = numeroRighe - eliminate.split(",").length;
	} 
	 		 
	for (var i=1; i<= numeroRigheTot; i ++){
		var interrompi="No";
	//	console.log(i);
		if(numeroRighe < numeroRigheTot && indiciEliminati){ 
			for (var j=0; j< indiciEliminati.length; j ++){
				var iTemp = parseInt(indiciEliminati[j]);
			
				if(i==iTemp  ){
					interrompi="Si";
					break;
				} 
			}
		}
		
		if(interrompi=="No"){
			var tempName = "id_"+i+"_prgMovimento";
			var prgMov = document.getElementById(tempName).value;
			tempName = "id_"+i+"_tipoMovimento";
			var tipoMovIter = document.getElementById(tempName).value;
			if(prgMov != currentPrg && tipoMov != tipoMovIter){				
				tempName = "id_"+i+"_dataInizio";
				var dataInizioMov = document.getElementById(tempName).value;
				var testo = prgMov +"-"+ tipoMovIter+"-"+ dataInizioMov;
				var o = document.createElement("option");
			    var t = document.createTextNode(testo);
			    o.setAttribute("value",prgMov);
			    o.appendChild(t);
			    selObj.appendChild(o);
			}
		}
	
	}
 
	var idInput ="id_"+ td.id.split("$")[1];
	selObj.onchange = function () { endEdit(this, idInput ); };
	
	selObj.onblur = function () { 
		if(editDone=="OK"){
			return;
		}else{
		 	var tdb = selObj.parentNode;
		 	tdb.removeChild(td.firstChild);	//remove input
			tdb.innerHTML = oldText;
			tdb.style.paddingTop = padTop;
			tdb.style.paddingBottom = padBottom;
		}
	
	};
		
	td.innerHTML = "";
	td.style.paddingTop = "0px";
	td.style.paddingBottom = "0px";
	td.style.backgroundColor = "LightGoldenRodYellow";
	td.insertBefore(selObj, td.firstChild);
	selObj.selectedIndex = -1;
	selObj.focus();
} 

function editaStato(td) {
	
	editDone="";
	if (td.firstChild && td.firstChild.tagName == "INPUT")
		return;

	oldText = td.innerHTML.trim();
	
	var str = oldText;
	if(str.lastIndexOf("<font color=\"red\">") > -1){
		oldText = str.substring(str.lastIndexOf("<font color=\"red\">")+n, str.lastIndexOf("</font>"));
	}
	
	//oldColor = getStyle(td, "backgroundColor");
	padTop = getStyle(td, "paddingTop");
	padBottom = getStyle(td, "paddingBottom");

	var radioInputPR = document.createElement("input");
	radioInputPR.type= "radio";
	radioInputPR.name="statoMov"+td.id;
	radioInputPR.value="PR";
	radioInputPR.id="PR_"+td.id;
	var lblPR = document.createElement("label");
	lblPR.innerHTML = "PR";
	if(oldText == "PR"){
		radioInputPR.checked="checked";
	}
	
	var radioInputAN = document.createElement("input");
	radioInputAN.type= "radio";
	radioInputAN.name="statoMov"+td.id;
	radioInputAN.value="AN";
	radioInputAN.id="AN_"+td.id;
	var lblAN = document.createElement("label");
	lblAN.innerHTML = "AN";
 
	if(oldText == "AN" || oldText == "AU"){
		radioInputAN.checked="checked";
	}
 
	//// ------- input style -------
	var left = getStyle(td, "paddingLeft").replace("px", "");
	var right = getStyle(td, "paddingRight").replace("px", "");
	radioInputPR.style.width = td.offsetWidth - left - right - (td.clientLeft * 2) - 2 + "px";
	radioInputPR.style.height = td.offsetHeight - (td.clientTop * 2) - 2 + "px";
	radioInputPR.style.border = "0px";
	radioInputPR.style.fontFamily = "inherit";
	radioInputPR.style.fontSize = "inherit";
	radioInputPR.style.textAlign = "inherit";
	//radioInputPR.style.backgroundColor = "LightGoldenRodYellow";
	
	radioInputAN.style.width = td.offsetWidth - left - right - (td.clientLeft * 2) - 2 + "px";
	radioInputAN.style.height = td.offsetHeight - (td.clientTop * 2) - 2 + "px";
	radioInputAN.style.border = "0px";
	radioInputAN.style.fontFamily = "inherit";
	radioInputAN.style.fontSize = "inherit";
	radioInputAN.style.textAlign = "inherit";
	//radioInputAN.style.backgroundColor = "LightGoldenRodYellow";
	
	var idInput ="id_"+ td.id.split("$")[1];
	
	radioInputAN.onblur = function () { endEditStato(this,idInput); }; 
	radioInputPR.onblur = function () { endEditStato(this,idInput); }; 

	td.innerHTML = "";
	td.style.paddingTop = "0px";
	td.style.paddingBottom = "0px";
	//td.style.backgroundColor = "LightGoldenRodYellow";
	td.insertBefore(lblAN, td.firstChild);
	td.insertBefore(radioInputAN, td.firstChild);
	td.insertBefore(lblPR, td.firstChild);
	td.insertBefore(radioInputPR, td.firstChild);
 
}
 
function editaData(td, tipoParagone, idAltraData) {
	
	editDone ="";
	if (td.firstChild && td.firstChild.tagName == "INPUT")
		return;

	oldText = td.innerHTML.trim();
	var str = oldText;
	if(str.lastIndexOf("<font color=\"red\">") > -1){
		 
		oldText = str.substring(str.lastIndexOf("<font color=\"red\">")+n, str.lastIndexOf("</font>"));
	}
	
//	oldColor = getStyle(td, "backgroundColor");
	padTop = getStyle(td, "paddingTop");
	padBottom = getStyle(td, "paddingBottom");

	var input = document.createElement("input");
	input.value = oldText;

	//// ------- input style -------
	var left = getStyle(td, "paddingLeft").replace("px", "");
	var right = getStyle(td, "paddingRight").replace("px", "");
	input.style.width = td.offsetWidth - left - right - (td.clientLeft * 2) - 2 + "px";
	input.style.height = td.offsetHeight - (td.clientTop * 2) - 2 + "px";
	input.style.border = "0px";
	input.style.fontFamily = "inherit";
	input.style.fontSize = "inherit";
	input.style.textAlign = "inherit";
	input.style.backgroundColor = "LightGoldenRodYellow";
	input.maxlength=12;

	var idInput ="id_"+ td.id.split("$")[1];
	input.onchange = function () { endEditData(this, idInput, tipoParagone, idAltraData); };

	input.onblur = function () { 
		if(editDone=="OK"){
			return;
		}else{
		 	var tdb = input.parentNode;
		 	tdb.removeChild(td.firstChild);	//remove input
			tdb.innerHTML = oldText;
			tdb.style.paddingTop = padTop;
			tdb.style.paddingBottom = padBottom;
		}
	
	};
	
	td.innerHTML = "";
	td.style.paddingTop = "0px";
	td.style.paddingBottom = "0px";
	//td.style.backgroundColor = "LightGoldenRodYellow";
	td.insertBefore(input, td.firstChild);
	input.select();
}

function editaCampi(){
	
		var numRighe = document.Frm1.numeroRighe.value;
		
		for (index = 1; index <= numRighe; index++) {
			var nameEdit ="editableDiv_"+index;
			document.getElementById(nameEdit).style.display = "";
			var nameNotEdit ="not_editableDiv_"+index;
			document.getElementById(nameNotEdit).style.display = "none";
		}
		
}
 
function endEditData(input, idInput, tipoParagone, idAltraData) {
	editDone = "OK";
	if(input.value && input.value!=''){
		var formatoData= new RegExp(/^(((0[1-9]|[12]\d|3[01])\/(0[13578]|1[02])\/((19|[2-9]\d)\d{2}))|((0[1-9]|[12]\d|30)\/(0[13456789]|1[012])\/((19|[2-9]\d)\d{2}))|((0[1-9]|1\d|2[0-8])\/02\/((19|[2-9]\d)\d{2}))|(29\/02\/((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|(([1][26]|[2468][048]|[3579][26])00))))$/g);
		 
		var isFormatoDataValido = formatoData.test(input.value);
		if(!isFormatoDataValido){
			alert("Formato o valora data non corretto inserire una data del tipo 'dd/mm/yyyy'");
			input.focus();
			return;
		}
		
		if(tipoParagone!="NO"){
			var tdAltraData = document.getElementById(idAltraData); 
			var altraDataTemp = tdAltraData.innerHTML.trim();
			var altraData =altraDataTemp;
			if(altraDataTemp.lastIndexOf("<font color=\"red\">") > -1){
				altraData = altraDataTemp.substring(altraDataTemp.lastIndexOf("<font color=\"red\">")+n, altraDataTemp.lastIndexOf("</font>"));
			}
			
			if(altraData!=""){
				if(tipoParagone=="PREC"){ //sto cambiano data inizio la confronto con data fine effettiva
					if(compareDate(input.value, altraData) >0 ){
						alert("Attenzione: la data inizio deve essere uguale o precedente alla data fine effettiva");
						input.focus();
						return;
					}
				}
				if(tipoParagone=="SUCC"){//sto cambiano data  fine effettiva la confronto con data inizio
					if(compareDate(input.value, altraData) <0 ){
						alert("Attenzione: la data fine effettiva deve essere uguale o precedente alla data inizio");
						input.focus();
						return;
					}
				}
			}
		}
	}
	
	document.getElementById(idInput).value=input.value;
	
	var td = input.parentNode;
	td.removeChild(td.firstChild);	//remove input
	var str=oldText;
	if(str.lastIndexOf("<font color=\"red\">") > -1){
		
		oldText = str.substring(str.lastIndexOf("<font color=\"red\">"), str.lastIndexOf("</font>"));
	}
	if (oldText != input.value.trim() ){
	//	td.style.color = "red";
		countModifiche = countModifiche+1;
		var indiceMod = idInput.split("_")[1];
		var idRigaMod ="rigaMod_"+indiceMod;
		document.getElementById(idRigaMod).value="MOD";
		td.innerHTML = "<del><font color=\"red\">"+oldText+"</font></del><br/><font color=\"red\">"+input.value+"</font>";
	}else{
		td.innerHTML = input.value;
	}
	td.style.paddingTop = padTop;
	td.style.paddingBottom = padBottom;
	//td.style.backgroundColor = oldColor;
	
}
function endEdit(select, idInput) {
	 
	editDone = "OK";
	/* var formatoNumerico= new RegExp(/^\d+$/);
	var isNumerico = formatoNumerico.test(input.value);
	if(!isNumerico){
		alert("Inserire un valore numerico");
		input.focus();
		return;
	}
 */	

	var selectedValue = select.options[select.selectedIndex].value;
 
 	document.getElementById(idInput).value=selectedValue;
	var str = oldText;
	var td = select.parentNode;
 	td.removeChild(td.firstChild);	//remove input
	if(str.lastIndexOf("<font color=\"red\">") > -1){
		
		oldText = str.substring(str.lastIndexOf("<font color=\"red\">"), str.lastIndexOf("</font>"));
	}
	if (oldText != selectedValue.trim() ){
		//td.style.color = "red";
		countModifiche = countModifiche+1;
		var indiceMod = idInput.split("_")[1];
		var idRigaMod ="rigaMod_"+indiceMod;
		document.getElementById(idRigaMod).value="MOD";
		if(selectedValue!="-1"){
			td.innerHTML = "<del><font color=\"red\">"+oldText+"</font></del><br/><font color=\"red\">"+selectedValue+"</font>";
		}else{
			td.innerHTML = "<del><font color=\"red\">"+oldText+"</font></del><br/><font color=\"red\"></font>";
		}
		
	}else{
		if(selectedValue!="-1"){
			td.innerHTML = selectedValue;
		}else{
			td.innerHTML = "";
		}
		
	}
	td.style.paddingTop = padTop;
	td.style.paddingBottom = padBottom;
//	td.style.backgroundColor = oldColor;
}
function endEditStato(input, idInput) {
	 
	document.getElementById(idInput).value=input.value;
	var str = oldText;
	
	var td = input.parentNode;
	td.removeChild(td.firstChild);	//remove input
	if(str.lastIndexOf("<font color=\"red\">") > -1){
		
		oldText = str.substring(str.lastIndexOf("<font color=\"red\">"), str.lastIndexOf("</font>"));
	}
	if (oldText != input.value.trim() ){
	//	td.style.color = "red";
		countModifiche = countModifiche+1;
		var indiceMod = idInput.split("_")[1];
		var idRigaMod ="rigaMod_"+indiceMod;
		document.getElementById(idRigaMod).value="MOD";
		td.innerHTML = "<del><font color=\"red\">"+oldText+"</font></del><br/><font color=\"red\">"+input.value+"</font>";
	}else{
		td.innerHTML = input.value;
	}
	td.style.paddingTop = padTop;
	td.style.paddingBottom = padBottom;
	//td.style.backgroundColor = oldColor;
}
function verficaEInvia(){
	if(countModifiche < 0){
		alert("Nessun movimento nella lista è stato modificato, effettuare almeno una modifica per poter proseguire.");
		return;
	}else{
		document.Frm1.operazione.value="FORZATURA";
		document.Frm1.submit();
	}
}

function eliminaRiga(indiceRiga){
 
	var table = document.getElementById("tabellaForzature");
	if (confirm("Attenzione: cancellare il movimento dall'elenco per la forzatura?")) {
		var precValue = document.Frm1.rigaEliminate.value;
		if(precValue && precValue!="") {
			var size = precValue.split(",").length;
			table.deleteRow(indiceRiga-size);
			document.Frm1.rigaEliminate.value=precValue +","+ indiceRiga;
			
		}else{
			document.Frm1.rigaEliminate.value=indiceRiga;
			table.deleteRow(indiceRiga);
		}
		
		
	}
	
}

$(function() {
    $(".up,.down").click(function(){
        var row = $(this).parents("tr:first");
        if ($(this).is(".up")) {
          if(row.prev()[0].id){
        	  row.insertBefore(row.prev());
          }
        } else {
	       	 if(row.next()[0].id){
	        	row.insertAfter(row.next());
	        }
        }
    });
});
-->
    </SCRIPT>
  </head>
  <body class="gestione" onload="rinfresca();controllaCatene();">
    <br/>
  <table cellpadding="2" cellspacing="10" border="0" width="100%">
	<tr>
		<td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color: #dcdcdc">
		<%
      	out.print(txtFilters);
        %>
		</td>
	</tr>
</table>
  <p class="titolo">
<%if(!isEsito || (isEsito && !isEsitoOK)){ %>		
 	I dati modificabili sono quelli evidenziati in giallo.<br/><br/>
  <%} %>
     Forzature Movimenti</p>
    
    <br/>
    
    <center>   
    <%
    	//Vector rows = serviceResponse.getAttributeAsVector("M_ListaForzaMovModificabili.ROWS.ROW");
		Enumeration _enum = null;
		if(movimentiFiltrati!=null)
			_enum = movimentiFiltrati.elements();
		int numrecords = -1;
        //Creo i pulsanti solo se ho almeno un risultato
        String moduleAttrName = "M_ListaForzaMovModificabili.ROWS";
        if(isEsito && !isEsitoOK){
        	moduleAttrName = "M_ListaForzatureErrateConMovimenti.ROWS";
        }
        
        if (_enum!=null && _enum.hasMoreElements()){
        
  			int currentpage = ((Integer) serviceResponse.getAttribute(moduleAttrName+".CURRENT_PAGE")).intValue();
  			int numpages = ((Integer) serviceResponse.getAttribute(moduleAttrName+".NUM_PAGES")).intValue();
  			numrecords = ((Integer) serviceResponse.getAttribute(moduleAttrName+".NUM_RECORDS")).intValue();
  			int rowsxpage = ((Integer) serviceResponse.getAttribute(moduleAttrName+".ROWS_X_PAGE")).intValue();
  			int numeroda = ((currentpage - 1) * rowsxpage) +1;
  			int numeroa = (currentpage == numpages ? numrecords : currentpage * rowsxpage);   
    %>
    <af:form name="FormListaNavigazione" method="POST" action="AdapterHTTP">
      <!--Salvo i parametri per la prossima ricerca sulla navigazione delle pagine-->
      
      <input type="hidden" name="LIST_PAGE" value=""/>
     <input type="hidden" name="MESSAGE" value=""/>
      <input type="hidden" name="PAGE" value="ListaMovModificabilePage"/>
      <input type="hidden" name="cdnFunzione" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"cdnFunzione")%>"/>
       <input type="hidden" name="ckeckboxMovimenti" value="<%=request.getParameterValues("ckeckboxMovimenti")%>"/>
  	 
      <!--pulsanti di navigazione-->
      
    <% 
		if(numpages != -1){
			numeroPagine= " di " + numpages;
			numeroRecords=" di " + numrecords;
		}
    
    	if (numpages != 1) {
    %> 
    
      <img src="../../img/list_first.gif" alt="Prima pagina" onclick="naviga(1, 'LIST_PAGE');"/>
      &nbsp;
      <img src="../../img/list_prev.gif" alt="Pagina precedente" onclick="naviga(calcolaPrecedente(), 'LIST_PAGE');" />
      &nbsp;
      <img src="../../img/list_next.gif" alt="Pagina successiva" onclick="naviga(calcolaSuccessiva(), 'LIST_PAGE');" />
      &nbsp;
      <img src="../../img/list_last.gif" alt="Ultima pagina" onclick="naviga(<%=serviceResponse.getAttribute(moduleAttrName+".NUM_PAGES")%>, 'LIST_PAGE');" />

    <% } else { %>
      
      <img src="../../img/list_first_gri.gif"/>
      &nbsp;
      <img src="../../img/list_prev_gri.gif" />
      &nbsp;
      <img src="../../img/list_next_gri.gif"/>
      &nbsp;
      <img src="../../img/list_last_gri.gif"/>
    
    <%  } %> 
    
     <br>  
       
    </af:form>

<p class="titolo"><b>Pag. <%=currentpage%><%=numeroPagine%> (da <%=numeroda%> a <%=numeroa%><%=numeroRecords%>)</b></p>
	<%}%>
	</center>
	
	<font color="red">
	    <af:showErrors/>
	</font>
 	<af:showMessages prefix="M_ControllaModificheForzatura"/>
 	<af:showMessages prefix="M_VerificaCateneMovLav"/>
 	
	<af:form name="Frm1" method="POST" action="AdapterHTTP">
       <input type="hidden" name="PAGE" value="ListaMovModificabilePage"/>
      <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
       <input type="hidden" name="rigaEliminate" value=""/>
      <input type="hidden" name="numeroRighe" value="<%=numrecords%>"/>
      <input type="hidden" name="operazione" value=""/>
        <input type="hidden" name="codiceFiscaleLavoratore" value="<%=codiceFiscaleLavoratore%>"/>
		<input type="hidden" name="cognome" value="<%=cognome%>"/>
		<input type="hidden" name="nome" value="<%=nome%>"/>
		<input type="hidden" name="datnasc" value="<%=datanasc%>"/>
		<input type="hidden" name="ragioneSociale" value="<%=ragioneSociale%>"/>
		<input type="hidden" name="ragioneSocialeUt" value="<%=ragioneSocialeUt%>"/>
		<input type="hidden" name="movimentoDescr" value="<%=movimentoDescr%>"/>
		<input type="hidden" name="statoDescr" value="<%=statoMovimentoDescr%>"/>
	    <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLav%>"/>
	    <input type="hidden" name="PRGAZIENDA" value="<%=prgAz%>"/>
	    <input type="hidden" name="PRGUNITA" value="<%=prgUn%>"/>
	    <input type="hidden" name="PRGAZIENDAUt" value="<%=prgAzUt%>"/>
	    <input type="hidden" name="PRGUNITAUt" value="<%=prgUnUt%>"/>
	    <input type="hidden" name="datmovimentoda" value="<%=datMovDa%>"/>
	    <input type="hidden" name="datmovimentoa" value="<%=datMovA%>"/>
	    <input type="hidden" name="tipoMovimento" value="<%=tipoMovParam%>"/>
		<input type="hidden" name="CODSTATOATTO" value="<%=statoParam%>"/>
		<%if(isEsito && !isEsitoOK) {
			String strPrgForzatura = prgForzatura==null? "" : prgForzatura.toPlainString();
		%>
			<input type="hidden" name="PRGFORZATURA" value="<%=strPrgForzatura%>"/>
		<%}%>
<%if(!isEsito || (isEsito && !isEsitoOK)){ %>		
  <%out.print(htmlStreamTop);%>  
  
     <TABLE class="lista" align="center" id="tabellaForzature">
 
      <%  
      	//Creo l'intestazione solo se ho almeno un risultato
        if (_enum!=null && _enum.hasMoreElements()){
      %>
        <tr>
          <th class="lista">&nbsp;</th>   
          <th class="lista">&nbsp;</th>
		  <th class="lista">Prg. Mov. Precedente</th>
          <th class="lista">Mov.</th>
		  <th class="lista">Prg. Mov. Successivo</th>
          <th class="lista">Stato</th>
          <th class="lista">Data inizio</th>
		  <th class="lista">Data fine</th>
		  <th class="lista">Data fine effettiva</th>
          <th class="lista">Tipo contratto</th>
          <th class="lista">Tempo</th>
          <th class="lista">Azienda</th>
		  <th class="lista">Cod. tipo fine</th>
          <th class="lista">&nbsp;</th>
        </tr>
      <%} else { //Indico che non ho movimenti%>
      	<tr><td colspan="2"><p align="center" class="titolo">Nessun movimento nella lista</p></td></tr>
      <%} 
      	int i = 0;
       
        while (_enum!=null && _enum.hasMoreElements()) {
	        //finchè ho righe le visualizzo
	        SourceBean row = (SourceBean) _enum.nextElement();
	        i++;
	   
	        String prgMovimento = it.eng.sil.util.Utils.notNull(row.getAttribute("prgmovimento")) ;
	        String prgMovimentoPrec = it.eng.sil.util.Utils.notNull(row.getAttribute("prgmovimentoprec")) ;
	        String prgMovimentoSucc = it.eng.sil.util.Utils.notNull(row.getAttribute("prgmovimentosucc")) ;
	        String dataInizio = it.eng.sil.util.Utils.notNull(row.getAttribute("datamov")) ;
	        String dataFineEffettiva =it.eng.sil.util.Utils.notNull(row.getAttribute("datfineeffettiva")) ; 
 	        String tipoMovimento = it.eng.sil.util.Utils.notNull(row.getAttribute("codtipomov"));
	        String statoMovimento = it.eng.sil.util.Utils.notNull(row.getAttribute("codstatoatto"));
	        String cdnLavoratore = it.eng.sil.util.Utils.notNull(row.getAttribute("cdnlavoratore"));
	        String prgMovimentoPrecMod = null;
	        String prgMovimentoSuccMod = null;
	        String datInizioMod = null;
	        String datFineEffMod = null;
	        String codStatoAttoMod = null;
	       
	        if(isEsito && !isEsitoOK){
	        	prgMovimentoPrecMod = it.eng.sil.util.Utils.notNull(row.getAttribute("precmod")) ;
	        	prgMovimentoSuccMod = it.eng.sil.util.Utils.notNull(row.getAttribute("succmod")) ;
	 	        datInizioMod = it.eng.sil.util.Utils.notNull(row.getAttribute("datiniziomod")) ;
	 	        datFineEffMod = it.eng.sil.util.Utils.notNull(row.getAttribute("datfineffmod")) ;
	 	        codStatoAttoMod = it.eng.sil.util.Utils.notNull(row.getAttribute("statomod")) ;
	        }
	       
	        boolean isEditable=true;
	        if(statoMovimento.equalsIgnoreCase("AR")){
	        	isEditable=false;
	        }
	        boolean isStatoEditabile = isEditable;
	        if(statoMovimento.equalsIgnoreCase("PA")){
	        	isStatoEditabile = false;
	        }
	        
	        String stileTd="";
      	  if(i % 2 == 0){
      		  stileTd="lista_pari";
	        }else{
	        	stileTd="lista_dispari";
	        }
      	 
      %>
       <%if(!isEditable) {  %>
          <!--elementi della riga-->
       <tr id="not_editableDiv_<%=i %>" class="lista">
		   
		      <TD class="<%=stileTd%>">
				<TABLE border="0" cellpadding="0" cellspacing="0" margin="0"><TR>
				<TD class="caption"><A href="javascript://" onclick="apriDettaglio(<%=prgMovimento%>)"><IMG name="image" border="0"  src="../../img/detail.gif" alt="Dettaglio Movimento" title="Dettaglio Movimento"/></A></TD>
				</TR></TABLE>
			  </TD>
        	 <TD class="<%=stileTd%>" nowrap>
				<a   class="up"><img src="../../img/su.gif" border="0" title="Sposta in alto la riga"/></a><br/>
				<a   class="down"><img src="../../img/giu.gif" border="0" title="Sposta in basso la riga"/></a>
			  </TD>
		  	  <td class="<%=stileTd%>"> <%=it.eng.sil.util.Utils.notNull(row.getAttribute("prgmovimentoprec")) %></td>
	          <td class="<%=stileTd%>"><%=tipoMovimento %><br/><%=it.eng.sil.util.Utils.notNull(row.getAttribute("prgmovimento")) %></td>
	          <td class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("prgmovimentosucc")) %></td>
	          <td  class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("codstatoatto")) %></td>
	          <td  class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("datamov")) %></td>
	          <td  class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("datfinemov")) %></td>
	          <td class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("datfineeffettiva")) %></td>
	          <td class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("codtipoass")) %></td>
			  <td class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("codmonotempo")) %></td>
			  <td class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("ragsocaz")) %></td>
			  <td class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("codmonotipofine")) %></td>
			  <TD class="<%=stileTd%>">
				<TABLE border="0" cellpadding="0" cellspacing="0" margin="0"><TR>
				<TD class="caption"><A href="javascript://" onclick="eliminaRiga(<%=i%>)"><IMG name="image" border="0"  src="../../img/del.gif" alt="Elimina da elenco" title="Elimina da elenco"/></A></TD>
				</TR></TABLE>
			  </TD>
			  <input type="hidden" value="<%=tipoMovimento %>" id="id_<%=i %>_tipoMovimento" />
			  <input type="hidden" value="<%=prgMovimento %>" name="prgMovimento_<%=i %>"  id="id_<%=i %>_prgMovimento" />
			  <input type="hidden" value="<%= dataInizio %>" id="id_<%=i %>_dataInizio" name="name_<%=i %>_dataInizio" />
		</tr>
			 <%} else {  %> 	 
         <tr id="editableDiv_<%=i %>"  class="lista">
	    
			   <TD class="<%=stileTd%>">
				<TABLE border="0" cellpadding="0" cellspacing="0" margin="0"><TR>
				<TD class="caption"><A href="javascript://" onclick="apriDettaglio(<%=prgMovimento%>)"><IMG name="image" border="0"  src="../../img/detail.gif" alt="Dettaglio Movimento" title="Dettaglio Movimento"/></A></TD>
				</TR></TABLE>
			  </TD>
        	 <TD class="<%=stileTd%>" nowrap>
				<a   class="up"><img src="../../img/su.gif" border="0" title="Sposta in alto la riga"/></a><br/>
				<a   class="down"><img src="../../img/giu.gif" border="0" title="Sposta in basso la riga"/></a>
			  </TD>
        	 
            <%	if(tipoMovimento.equalsIgnoreCase("AVV")){  %>
	         	 <td class="<%=stileTd%>" align="center"><%=prgMovimentoPrec %></td>
	           <% }else if(isEsito && !isEsitoOK && !prgMovimentoPrec.equalsIgnoreCase(prgMovimentoPrecMod)){ %> 
	         	 <td  class="<%=stileTd%>" align="center" id="td$<%=i %>_prgPrec" style="background-color:LightGoldenRodYellow;" onclick="comboPrgPrecSucc(this, 'CES', '<%=i%>')">
	         	 <del><font color="red"><%=prgMovimentoPrec %></font></del><br/><font color="red"><%=prgMovimentoPrecMod %></font>
	         	 </td>   	 
	            <% }else{ %> 
	         	 <td  class="<%=stileTd%>" align="center" id="td$<%=i %>_prgPrec" style="background-color:LightGoldenRodYellow;" onclick="comboPrgPrecSucc(this, 'CES', '<%=i%>')"><%=prgMovimentoPrec %></td>
	         	 
	           <% }  %>	
	          <td class="<%=stileTd%>" align="center"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("CODTIPOMOV")) %><br/><%=it.eng.sil.util.Utils.notNull(row.getAttribute("prgmovimento")) %></td>
	       		<%           	
	         	if(tipoMovimento.equalsIgnoreCase("CES")){ 
          		%>
	         	 <td class="<%=stileTd%>" align="center"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("prgmovimentosucc")) %></td>
	           <% }else if(isEsito && !isEsitoOK && !prgMovimentoSucc.equalsIgnoreCase(prgMovimentoSuccMod)){ %> 
	           	   <td  class="<%=stileTd%>" align="center" id="td$<%=i %>_prgSucc" style="background-color:LightGoldenRodYellow;" onclick="comboPrgPrecSucc(this, 'AVV', '<%=i%>')">
	           	   <del><font color="red"><%=prgMovimentoSucc %></font></del><br/><font color="red"><%=prgMovimentoSuccMod %></font>
 				</td>
	           <% }else{ %> <!-- editaPrgPrecSucc(this, 'AVV') -->
	           	  <td  class="<%=stileTd%>" align="center" id="td$<%=i %>_prgSucc" style="background-color:LightGoldenRodYellow;" onclick="comboPrgPrecSucc(this, 'AVV', '<%=i%>')"><%=prgMovimentoSucc %></td>
	           <% }  %>  
	           <%           	
	         	if(!isStatoEditabile){ 
          		%>
	         		 <td class="<%=stileTd%>"><%=statoMovimento %></td>
	          <% }else if(isEsito && !isEsitoOK && StringUtils.isFilledNoBlank(codStatoAttoMod) && !statoMovimento.equalsIgnoreCase(codStatoAttoMod)){ %> 
	         	  <td class="<%=stileTd%>" id="td$<%=i %>_stato" style="background-color:LightGoldenRodYellow;" onclick="editaStato(this)" >
	         	  <del><font color="red"><%=statoMovimento %></font></del><br/><font color="red"><%=codStatoAttoMod %></font>
					</td>
	           <% }else{ %> 
	           	  <td class="<%=stileTd%>" id="td$<%=i %>_stato" style="background-color:LightGoldenRodYellow;" onclick="editaStato(this)" ><%=statoMovimento%></td>
	           	  
	           <% }  %>  
	          
	         <%           	
	         	if(tipoMovimento.equalsIgnoreCase("CES") &&!isEsito){ 
          	  %>
          	  	<td class="<%=stileTd%>" id="td$<%=i %>_dataInizio" style="background-color:LightGoldenRodYellow;" onclick="editaData(this, 'NO', '')" ><%=dataInizio %></td>
          	 <% }else if(tipoMovimento.equalsIgnoreCase("CES") && isEsito && !isEsitoOK && StringUtils.isFilledNoBlank(datInizioMod) && !dataInizio.equalsIgnoreCase(datInizioMod)){ %> 
          	  	 <td class="<%=stileTd%>" id="td$<%=i %>_dataInizio" style="background-color:LightGoldenRodYellow;" onclick="editaData(this, 'NO', '')" >
          	  	  <del><font color="red"><%=dataInizio %></font></del><br/><font color="red"><%=datInizioMod %></font>
          	  	  </td>
          	 <% }else if(isEsito && !isEsitoOK && StringUtils.isFilledNoBlank(datInizioMod) && !dataInizio.equalsIgnoreCase(datInizioMod)){ %> 
          	  	 <td class="<%=stileTd%>" id="td$<%=i %>_dataInizio" style="background-color:LightGoldenRodYellow;" onclick="editaData(this, 'PREC', 'td$<%=i %>_dataFineEffettiva')" >
          	  	  <del><font color="red"><%=dataInizio %></font></del><br/><font color="red"><%=datInizioMod %></font>
          	  	  </td>
          	  <% }else{ %>
          	  		<td class="<%=stileTd%>" id="td$<%=i %>_dataInizio" style="background-color:LightGoldenRodYellow;" onclick="editaData(this, 'PREC', 'td$<%=i %>_dataFineEffettiva')" ><%=dataInizio %></td>  		
          	   <% }  %>   	
	          
	          <td class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("datfinemov")) %></td>
	          
	          
	          <%           	
	         	if(tipoMovimento.equalsIgnoreCase("CES")){ 
          	  %>
          	  	 <td class="<%=stileTd%>"><%=dataFineEffettiva %></td>
          	 <% }else if(isEsito && !isEsitoOK && StringUtils.isFilledNoBlank(datFineEffMod) && !dataFineEffettiva.equalsIgnoreCase(datFineEffMod)){ %> 
          	  	 <td class="<%=stileTd%>" id="td$<%=i %>_dataFineEffettiva" style="background-color:LightGoldenRodYellow;" onclick="editaData(this, 'SUCC', 'td$<%=i %>_dataInizio' )" >
          	  	  <del><font color="red"><%=dataFineEffettiva %></font></del><br/><font color="red"><%=datFineEffMod %></font>
				</td>
          	  <% }else{ %> 
	       		 <td class="<%=stileTd%>" id="td$<%=i %>_dataFineEffettiva" style="background-color:LightGoldenRodYellow;" onclick="editaData(this, 'SUCC', 'td$<%=i %>_dataInizio' )" ><%=dataFineEffettiva %></td>
	          <% }  %>   	
	          
	          <td class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("codtipoass")) %></td>
			  <td class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("codmonotempo")) %></td>
			  <td class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("ragsocaz")) %></td>
			  <td class="<%=stileTd%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("codmonotipofine")) %></td>
			  <TD class="<%=stileTd%>">
				<TABLE border="0" cellpadding="0" cellspacing="0" margin="0"><TR>
				<TD class="caption"><A href="javascript://" onclick="eliminaRiga(<%=i%>)"><IMG name="image" border="0"  src="../../img/del.gif" alt="Elimina da elenco" title="Elimina da elenco"/></A></TD>
				</TR></TABLE>
			  </TD>
			  <input type="hidden" value="" id="rigaMod_<%=i %>" name="rigaModificata_<%=i %>"/>
			  <input type="hidden" value="<%=tipoMovimento %>" id="id_<%=i %>_tipoMovimento" />
			  <input type="hidden" value="<%=prgMovimento %>" name="prgMovimento_<%=i %>"  id="id_<%=i %>_prgMovimento" />
			  <input type="hidden" value="<%=prgMovimentoPrec %>" id="id_<%=i %>_prgPrec" name="name_<%=i %>_prgPrec"/>
			  <input type="hidden" value="<%=prgMovimentoSucc %>" id="id_<%=i %>_prgSucc" name="name_<%=i %>_prgSucc" />
			  <input type="hidden" value="<%= statoMovimento %>" id="id_<%=i %>_stato" name="name_<%=i %>_stato"/>
			  <input type="hidden" value="<%= dataInizio %>" id="id_<%=i %>_dataInizio" name="name_<%=i %>_dataInizio" />
			  <input type="hidden" value="<%= dataFineEffettiva %>" id="id_<%=i %>_dataFineEffettiva" name="name_<%=i %>_dataFineEffettiva" />
			  <input type="hidden" id="id_<%=i %>_cdnLavoratore" value="<%=cdnLavoratore %>" />
 			  <input type="hidden" name="ckeckboxMovimenti" value="<%=prgMovimento%>"/>
			  
			 </tr>
			  <%}   %> 	
		
        <% 
		  
		  } //while 
          out.print("<script language=\"Javascript\"> numRighe = " + i + "</SCRIPT>");
 		%> 		
 		<tr><td>&nbsp;</td></tr>
        <tr><td>&nbsp;</td></tr>
        <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
         
				
 
      </td>
     
      </tr>
 
  </TABLE>
   <%out.print(htmlStreamBottom);%>  
    <% } %>
      
     <center>
	  <table>
	  	
	  	 	 <% //o sono appena arrivata qui o ho avuto esito negativo          	
	         	if(!isEsito || (isEsito && !isEsitoOK)){ 
          	  %>
          	   <tr>
			  <td colspan="2" align="center">
			      <input class="pulsanti" type="button" onclick="verficaEInvia();"  name="applica" value="Applica Modifiche" />
			  </td>
			  </tr>
			  <tr>
			  <td colspan="2" align="center">
			      <input class="pulsante" type="button" onclick="tornaAllaLista();" name="cerca" value="Torna alla lista"/>
			  </td>
		  </tr>
			 <% }    
	  	 	 	// forzatura andata a buon fine
	         	if(isEsito && isEsitoOK && isCateneOk){ 
          	 %>
          	  <tr>
			  <td colspan="2" align="center">
			      <input class="pulsanti" type="button" onclick="apriRicalcola();"  name="ricalcolaImpatti" value="Esegui Ricalcola Impatti" />
			  </td>
			 <% }  %> 	
		   
		  <tr>
		<td colspan="2" align="center">
		&nbsp;
		</td>
	  </tr>
	  </table>
  </center>
    </af:form>    
  </body>
</html>