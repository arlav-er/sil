<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.sil.util.*,
                  java.util.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

  		<%-- 
  			Modificata il 27-mar-06
  			@author riccardi
 		--%>

<%
  String resultConfigMob = serviceResponse.containsAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM").toString():"0";
  String resultConfigCasoDubbio = serviceResponse.containsAttribute("M_GetConfigValue.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigValue.ROWS.ROW.NUM").toString():"0";
  String _page=it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(serviceRequest,"page");
  PageAttribs attributi = new PageAttribs(user, _page);

  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  String queryString = null;
 // boolean canPrint = attributi.containsButton("Stampa");
  boolean canExportDBF = attributi.containsButton("EXPORT_DBF");  
  
  
  String CodCPI=StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
  String CodTipoLista=StringUtils.getAttributeStrNotNull(serviceRequest,"CodTipoLista");
  String CodMotivoMob=StringUtils.getAttributeStrNotNull(serviceRequest,"CodMotivoMob");
  String CodMotivoScor=StringUtils.getAttributeStrNotNull(serviceRequest,"CodMotivoScor");
  String datcrtda=StringUtils.getAttributeStrNotNull(serviceRequest,"datcrtda");
  String datcrta=StringUtils.getAttributeStrNotNull(serviceRequest,"datcrta");      
  String datfineda=StringUtils.getAttributeStrNotNull(serviceRequest,"datfineda");
  String datfinea=StringUtils.getAttributeStrNotNull(serviceRequest,"datfinea");      
  String datinizioda=StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioda");
  String datinizioa=StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioa");
  String datdomandada=StringUtils.getAttributeStrNotNull(serviceRequest,"datdomandada");
  String datdomandaa=StringUtils.getAttributeStrNotNull(serviceRequest,"datdomandaa");
  String datmaxdiffda=StringUtils.getAttributeStrNotNull(serviceRequest,"datmaxdiffda");
  String datmaxdiffa=StringUtils.getAttributeStrNotNull(serviceRequest,"datmaxdiffa");     
  String dataInizioIndDa=StringUtils.getAttributeStrNotNull(serviceRequest,"datInizioIndDa");  
  String dataInizioIndA=StringUtils.getAttributeStrNotNull(serviceRequest,"datInizioIndA");  
  String dataFineIndDa=StringUtils.getAttributeStrNotNull(serviceRequest,"datfineIndDa"); 
  String dataFineIndA=  StringUtils.getAttributeStrNotNull(serviceRequest,"datfineIndA"); 
  String tipoRicerca=StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
  String CodStatiMob=StringUtils.getAttributeStrNotNull(serviceRequest,"codStatoMob");
  String flgMobStor=StringUtils.getAttributeStrNotNull(serviceRequest,"flgMobStor");
  String flgMobAperte=StringUtils.getAttributeStrNotNull(serviceRequest,"flgMobAperte");
  String flgEscludiFineMob=StringUtils.getAttributeStrNotNull(serviceRequest,"flgEscludiFineMob");
  String flgNonImprenditore=StringUtils.getAttributeStrNotNull(serviceRequest,"flgNonImprenditore");
  
  String NumDelReg=StringUtils.getAttributeStrNotNull(serviceRequest,"NumDelReg");
  String CodProv=StringUtils.getAttributeStrNotNull(serviceRequest,"CodProv");
  String mobInd=StringUtils.getAttributeStrNotNull(serviceRequest,"mobInd");
  String soggettiCM=StringUtils.getAttributeStrNotNull(serviceRequest,"soggettiCM");
  String soggettiConMov=StringUtils.getAttributeStrNotNull(serviceRequest,"SoggettiConMov");
  String flgCasoDubbio = (String)serviceRequest.getAttribute("flgCasoDubbio");
  
  if (tipoRicerca.equals("")) tipoRicerca="esatta";
  
  String labelDataCrt = "";
  String labelNumDelibera = "";
  String titleDataCrtDa = "";
  String titleDataCrtA = "";
  String labelStatoRich = "";
  if (resultConfigMob.equals("0")) {
	  labelDataCrt = "Data CRT/Delibera Regionale da";
	  titleDataCrtDa = "Data CRT da";
	  titleDataCrtA = "Data CRT a";
	  labelNumDelibera = "Numero Delibera Regionale";
	  labelStatoRich = "Stato della richiesta";
  }
  else {
	  labelDataCrt = "Data CPM/Delibera Provinciale da";
	  titleDataCrtDa = "Data CPM da";
	  titleDataCrtDa = "Data CPM a";
	  labelNumDelibera = "Numero Delibera Provinciale";
	  labelStatoRich = "Stato della domanda";
  }

  //TODO inserire l'interruttore MO	  
  //Genero informazioni sul lavoratore 
  String cognome=StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
  String nome=StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
  String CF=StringUtils.getAttributeStrNotNull(serviceRequest,"codiceFiscaleLavoratore");
  String cdnlavoratore=StringUtils.getAttributeStrNotNull(serviceRequest,"cdnlavoratore");
  String FlgCFok=StringUtils.getAttributeStrNotNull(serviceRequest,"FlgCFok"); 
  String fSelLav = "";
  //Dati per l'azienda
  String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
  String codTipoAz=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAz");
  String descrTipoAz=StringUtils.getAttributeStrNotNull(serviceRequest,"descrTipoAz");
  String FLGDATIOK=StringUtils.getAttributeStrNotNull(serviceRequest,"FLGDATIOK");
  String CODNATGIURIDICA=StringUtils.getAttributeStrNotNull(serviceRequest,"CODNATGIURIDICA");
  String IndirizzoAzienda=StringUtils.getAttributeStrNotNull(serviceRequest,"IndirizzoAzienda");
  String codFiscaleAzienda=StringUtils.getAttributeStrNotNull(serviceRequest,"IndirizzoAzienda");
  String pIva=StringUtils.getAttributeStrNotNull(serviceRequest,"pIva");
  String ragioneSociale=StringUtils.getAttributeStrNotNull(serviceRequest,"ragioneSociale");
  
 
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  boolean readOnlyStr = false;
  boolean button = true;
%>
<html>
<head>
<title>Ricerca Mobilit&agrave;</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>  
<af:linkScript path="../../js/"/>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<%@ include file="MovimentiRicercaSoggetto.inc" %>
<%@ include file="MovimentiSezioniATendina.inc" %>

<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

<script language="JavaScript">
<% 
   //Genera il Javascript che si occuperà di inserire i links nel footer
   attributi.showHyperLinks(out, requestContainer,responseContainer,"");
%>

function checkCampiObbligatori()
{
var ok;
var msg;
<%
// INIT-PARTE-TEMP
if (Sottosistema.MO.isOff()) {	
%>  
  if ((document.form1.datinizioda.value != "") || (document.form1.datinizioa.value != "")) 
       return true;
    alert("E' necessario inserire la data inizio ('da' o 'a')");
    return false;
<%
} else {
// END-PARTE-TEMP
%>
	if (document.form1.FlgMobAperte.checked) {
		if (document.form1.FlgMobStor.checked) {
			alert("Non si può selezionare il flag mobilità aperte e il flag mobilità storicizzate");
   			return false;	
		}
   		if ( (document.form1.datfineda.value != "") || (document.form1.datfinea.value != "") ) {
   			alert("Non bisogna indicare la data fine ('da' o 'a') quando si seleziona il flag mobilità aperte");
   			return false;
   		}
   		else {
   			return true;
   		}
   	}
   	if (document.form1.FlgMobStor.checked) {
   		if ( (document.form1.datfineda.value != "") || (document.form1.datfinea.value != "") ) {
   			alert("Non bisogna indicare la data fine ('da' o 'a') quando si seleziona il flag mobilità storicizzate");
   			return false;
   		}
   		else {
   			return true;
   		}
   	}
   	if ((document.form1.CDNLAVORATORE.value != "") || (document.form1.PRGAZIENDA.value != "") ) {
   		return true;
   	}
   	var numPeriodoMaxGG = 0;
   	var numPeriodoMaxGGMov = 0;
   	if (document.form1.CodCPI.value != "") {
   		numPeriodoMaxGG = 365;
   		numPeriodoMaxGGMov = 365;	
   	}
   	else {
   		numPeriodoMaxGG = 90;
   		numPeriodoMaxGGMov = 30;	
   	}
	if ((document.form1.datinizioda.value != "") && (document.form1.datinizioa.value != "") ) {
		ok = controllaDateRange(document.form1.datinizioda.value, document.form1.datinizioa.value, numPeriodoMaxGG);
		if (ok) return true;
	}
	if ((document.form1.datfineda.value != "") && (document.form1.datfinea.value != "") ) {
		ok = controllaDateRange(document.form1.datfineda.value, document.form1.datfinea.value, numPeriodoMaxGG);
		if (ok) return true;
	}
	if ((document.form1.datmaxdiffda.value != "") && (document.form1.datmaxdiffa.value != "") ) {
		ok = controllaDateRange(document.form1.datmaxdiffda.value, document.form1.datmaxdiffa.value, numPeriodoMaxGG);
		if (ok) return true;
	}
	if ((document.form1.datcrtda.value != "") && (document.form1.datcrta.value != "") ) {
		ok = controllaDateRange(document.form1.datcrtda.value, document.form1.datcrta.value, numPeriodoMaxGG);
		if (ok) return true;
	}
	if ((document.form1.datinizioIndDa.value != "") && (document.form1.datinizioIndA.value != "") ) {
		ok = controllaDateRange(document.form1.datinizioIndDa.value, document.form1.datinizioIndA.value, numPeriodoMaxGG);
		if (ok) return true;
	}
	if ((document.form1.datfineIndDa.value != "") && (document.form1.datfineIndA.value != "") ) {
		ok = controllaDateRange(document.form1.datfineIndDa.value, document.form1.datfineIndA.value, numPeriodoMaxGG);
		if (ok) return true;
	}
	if (document.form1.NumDelReg.value != "") {
		return true;
	}
	if (document.form1.SoggettiConMov.checked) {
		if ((document.form1.datInizioMovDa.value != "") && (document.form1.datInizioMovA.value != "") ) {
			ok = controllaDateRange(document.form1.datInizioMovDa.value, document.form1.datInizioMovA.value, numPeriodoMaxGGMov);
			if (ok) return true;
		}
	}
	
	for(i=0;i<document.form1.CodStatoMob.length;i++){
		if(document.form1.CodStatoMob.options[i].text!=""){
			if(document.form1.CodStatoMob.options[i].selected){
				return true;
			}
		}
	}
	
	msg = "Parametri generici.\n"+
			"OPZIONE 1 - Uno dei seguenti valori: Lavoratore / Azienda.\n" +
			"OPZIONE 2 - Periodo data da... a...  con periodo max 90 gg.\n" + 
			"OPZIONE 3 - Solo mobilità aperte.\n" + 
			"OPZIONE 4 - Numero Delibera.\n" +
			"OPZIONE 5 - Periodo data movimento da... a... con periodo max 30 gg.\n" +
			"OPZIONE 6 - Centro per l'impiego e periodo data da... a... con periodo max 365 gg.\n" + 
			"OPZIONE 7 - Stato della richiesta di mobilità.\n" +
			"OPZIONE 8 - Solo mobilità storicizzate";
			
		
	alert(msg);     
	return (false);	
<% 
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>	
}

function controllaDateRange(di, df, range){
	var dataI = "";
    var dataF = "";
    var ONE_DAY = 1000 * 60 * 60 * 24;

    if (di != "") {
      dataI = new String(di);
      var annoDataDal = dataI.substr(6,4);
      var meseDataDal = dataI.substr(3,2);
      var giornoDataDal = dataI.substr(0,2);
    } else {
    	return false;
    }

    if (df != "") {
      dataF = new String(df);
      var annoDataAl = dataF.substr(6,4);
      var meseDataAl = dataF.substr(3,2);
      var giornoDataAl = dataF.substr(0,2);
    } else {
    	return false;
    }

    var dataDal = new Date(annoDataDal, meseDataDal-1, giornoDataDal);
	var dataAl = new Date(annoDataAl, meseDataAl-1, giornoDataAl);

	var dataDal_ms = dataDal.getTime();
    var dataAl_ms = dataAl.getTime();
    var difference_ms = Math.abs(dataDal_ms - dataAl_ms);
    var delta = 1 + Math.round(difference_ms/ONE_DAY);   

	if (delta > range) {
		return false;
	}
    return true;
}

<%if (canExportDBF) {%>
	function confirmExport()
	{ 
		
		if ( confirm("L'esportazione della mobilità INTERA potrebbe durare alcuni minuti.\nConfermare per procedere con l'operazione.")){
			if (document.form1.EsportaAncheInviati.checked){	
				window.location.href="../../servlet/fv/AdapterHTTP?ACTION_NAME=MOB_ESPORTA_DBF&sentoo=y";
			}
			else{
				window.location.href="../../servlet/fv/AdapterHTTP?ACTION_NAME=MOB_ESPORTA_DBF";	
			}
		}
	}
<%}%>


</script>

<SCRIPT TYPE="text/javascript">
  function controlloDate(){
    var objDataDa = document.form1.datinizioda;
    var objDataA = document.form1.datinizioa;    
	if ((objDataDa.value != "") && (objDataA.value != "")) {
      if (compareDate(objDataDa.value,objDataA.value) > 0) {
      	alert(objDataDa.title + " maggiore di " + objDataA.title);
      	objDataDa.focus();
	    return false;
	  }	
	}
    objDataDa = document.form1.datfineda;
    objDataA = document.form1.datfinea;	
	if ((objDataDa.value != "") && (objDataA.value != "")) {
      if (compareDate(objDataDa.value,objDataA.value) > 0) {
      	alert(objDataDa.title + " maggiore di " + objDataA.title);
      	objDataDa.focus();
	    return false;
	  }	
	}
    objDataDa = document.form1.datmaxdiffda;
    objDataA = document.form1.datmaxdiffa;	
	if ((objDataDa.value != "") && (objDataA.value != "")) {
      if (compareDate(objDataDa.value,objDataA.value) > 0) {
      	alert(objDataDa.title + " maggiore di " + objDataA.title);
      	objDataDa.focus();
	    return false;
	  }	
	}
    objDataDa = document.form1.datcrtda;
    objDataA = document.form1.datcrta;	
	if ((objDataDa.value != "") && (objDataA.value != "")) {
      if (compareDate(objDataDa.value,objDataA.value) > 0) {
      	alert(objDataDa.title + " maggiore di " + objDataA.title);
      	objDataDa.focus();
	    return false;
	  }	
	}
  	<%
	// INIT-PARTE-TEMP
	if (Sottosistema.MO.isOn()) {	
	%>
	objDataDa = document.form1.datInizioMovDa;
    objDataA = document.form1.datInizioMovA;	
	if ((objDataDa.value != "") && (objDataA.value != "")) {
      if (compareDate(objDataDa.value,objDataA.value) > 0) {
      	alert(objDataDa.title + " maggiore di " + objDataA.title);
      	objDataDa.focus();
	    return false;
	  }	
	}
	<%
	} 
	// END-PARTE-TEMP
	%>
    return true;
  }

  function valorizzaHidden() {
  	<%
	// INIT-PARTE-TEMP
	if (Sottosistema.MO.isOff()) {	
	%>
	document.form1.descCodTipoLista_H.value = document.form1.CodTipoLista.options[document.form1.CodTipoLista.selectedIndex].text;  
	<%
	} else {
	// END-PARTE-TEMP
	%>
	var descTipoLista = '';
  	for (i=0;i<document.form1.CodTipoLista.length;i++) {
  		if (document.form1.CodTipoLista.options[i].selected) {
  			if (descTipoLista == '') {
  				descTipoLista = document.form1.CodTipoLista.options[i].text;
  			}
  			else {
  				descTipoLista = descTipoLista + ", " + document.form1.CodTipoLista.options[i].text; 
  			}
  		}
  	}
  	document.form1.descCodTipoLista_H.value = descTipoLista;
	<%}%>
  	document.form1.descCPI_H.value = document.form1.CodCPI.options[document.form1.CodCPI.selectedIndex].text;
	//questa sezione è necessaria per valorizzare i campi hidden con le descrizioni dei codici delle combo, utile per
	//la sintesi dei parametri di ricerca nei risultati della lista
	<%
	// INIT-PARTE-TEMP
	if (Sottosistema.MO.isOff()) {	
	%>  
	<%
	} else {
	// END-PARTE-TEMP
	%>
	var descFineLista = '';
  	for (i=0;i<document.form1.CodMotivoMob.length;i++) {
  		if (document.form1.CodMotivoMob.options[i].selected) {
  			if (descFineLista == '') {
  				descFineLista = document.form1.CodMotivoMob.options[i].text;
  			}
  			else {
  				descFineLista = descFineLista + ", " + document.form1.CodMotivoMob.options[i].text; 
  			}
  		}
  	}
  	document.form1.descMobFineLista_H.value = descFineLista;
	document.form1.codMotivoScor_H.value = document.form1.CodMotivoScor.options[document.form1.CodMotivoScor.selectedIndex].text;
	document.form1.codProv_H.value = document.form1.CodProv.options[document.form1.CodProv.selectedIndex].text;
	document.form1.mobInd_H.value = document.form1.mobInd.options[document.form1.mobInd.selectedIndex].text;
	<% 
	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
	%>	  
  	return true;
  }

// questa parte di script è necessaria per le sezioni a tendina del lavoratore e della azienda
<%
// INIT-PARTE-TEMP
if (Sottosistema.MO.isOff()) {	
%>
<%
} else {
// END-PARTE-TEMP
%>	
    function aggiornaAzienda(){
        document.form1.ragioneSociale.value = opened.dati.ragioneSociale;
        document.form1.pIva.value = opened.dati.partitaIva;
        document.form1.codFiscaleAzienda.value = opened.dati.codiceFiscaleAzienda;
        document.form1.IndirizzoAzienda.value = opened.dati.strIndirizzoAzienda + " (" + opened.dati.comuneAzienda + ")";
        document.form1.PRGAZIENDA.value = opened.dati.prgAzienda;
        document.form1.PRGUNITA.value = opened.dati.prgUnita;
        document.form1.codTipoAz.value = opened.dati.codTipoAz;
        document.form1.descrTipoAz.value = opened.dati.descrTipoAz;
        document.form1.FLGDATIOK.value = opened.dati.FLGDATIOK;
        prgAzienda = opened.dati.prgAzienda;
        prgUnita = opened.dati.prgUnita;
        if ( document.form1.FLGDATIOK.value == "S" ){ 
                    document.form1.FLGDATIOK.value = "Si";
        }else 
              if ( document.form1.FLGDATIOK.value != "" ){
                document.form1.FLGDATIOK.value = "No";
              }
        document.form1.CODNATGIURIDICA.value = opened.dati.codNatGiurAz;
        opened.close();
        var imgV = document.getElementById("tendinaAzienda");
        cambiaLavMC("aziendaSez","inline");
        imgV.src=imgAperta;
        
    }

    function aggiornaLavoratore(){
        document.form1.codiceFiscaleLavoratore.value = opened.dati.codiceFiscaleLavoratore;
        document.form1.cognome.value = opened.dati.cognome;
        document.form1.nome.value = opened.dati.nome;
        document.form1.CDNLAVORATORE.value = opened.dati.cdnLavoratore;
        document.form1.FLGCFOK.value = opened.dati.FLGCFOK;
        if ( document.form1.FLGCFOK.value == "S" ){ 
                    document.form1.FLGCFOK.value = "Si";
        }else 
              if ( document.form1.FLGCFOK.value != "" ){
                document.form1.FLGCFOK.value = "No";
              }
        opened.close();
        var imgV = document.getElementById("tendinaLav");
        cambiaLavMC("lavoratoreSez","inline");
        imgV.src=imgAperta;
        cdnLavoratore = opened.dati.cdnLavoratore; 
        provenienza = "lavoratore";
        
    }
    
    function gestioneSezioneMov(campo) {
		var objBtnSez1 = document.getElementById('sezione_movimento1');
		var objBtnSez2 = document.getElementById('sezione_movimento2');
		if (campo.checked) {
			objBtnSez1.style.display = "inline";
			objBtnSez2.style.display = "inline";
		}
		else {
			document.form1.datInizioMovDa.value = '';
			document.form1.datInizioMovA.value = '';
			objBtnSez1.style.display = "none";
			objBtnSez2.style.display = "none";
		}
	}
<% 
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>

 function creaStringa(){
 	var tipoListaStr = '';
 	var contSelezionati = 0;
 		for (i=0;i<document.form1.CodTipoLista.length;i++) {
   			if (document.form1.CodTipoLista[i].selected) {
   				if (tipoListaStr == '') {
					tipoListaStr = tipoListaStr + document.form1.CodTipoLista[i].value;
				}
				else {
					tipoListaStr = tipoListaStr + ',' + document.form1.CodTipoLista[i].value;
				}
		 	}
		}
		
		document.form1.tipoLista.value=tipoListaStr;
		return true;
 }
 
 function selezionaInCombo(){
 	var statilista = "<%= CodStatiMob %>";
 	var tokens = statilista.split(", ");
 
 	for(var i=0;i<tokens.length;i++){
 		var selected = tokens[i];
 		if (selected != ""){
	 		for(var j=0;j<document.form1.CodStatoMob.length;j++){
	 			if(document.form1.CodStatoMob[j].value==selected){
	 				document.form1.CodStatoMob[j].selected=true;
	 				}
	 		}
 		}
 	}
 	
 	var tipolista = "<%= CodTipoLista %>";
 	tokens = tipolista.split(",");
 
 	for(var i=0;i<tokens.length;i++){
 		var selected = tokens[i];
 		if (selected != ""){
	 		for(var j=0;j<document.form1.CodTipoLista.length;j++){
	 			if(document.form1.CodTipoLista[j].value==selected){
	 				document.form1.CodTipoLista[j].selected=true;
	 				}
	 		}
 		}
 	}

 	var motivoMob = "<%= CodMotivoMob %>";
 	tokens = motivoMob.split(",");
 
 	for(var i=0;i<tokens.length;i++){
 		var selected = tokens[i];
 		if (selected != ""){
	 		for(var j=0;j<document.form1.CodMotivoMob.length;j++){
	 			if(document.form1.CodMotivoMob[j].value==selected){
	 				document.form1.CodMotivoMob[j].selected=true;
	 				}
	 		}
 		}
 	}
 	
 }

 function aziendaElavoratore(){
	if (document.form1.codiceFiscaleLavoratore.value!=""){
		var imgV = document.getElementById("tendinaLav");
        cambiaLavMC("lavoratoreSez","inline");
        imgV.src=imgAperta;
	}

	if(document.form1.codFiscaleAzienda.value!=""){
		var imgV = document.getElementById("tendinaAzienda");
        cambiaLavMC("aziendaSez","inline");
        imgV.src=imgAperta;
		
	}

}

</SCRIPT>

</head>


<body class="gestione" onload="aziendaElavoratore();selezionaInCombo();">
<br>
<p class="titolo">Ricerca mobilit&agrave;</p>
<p align="center">
  <af:form name="form1" action="AdapterHTTP" method="GET" onSubmit="checkCampiObbligatori() && controlloDate() && valorizzaHidden() && creaStringa()">
  	<input type="hidden" name="descCodTipoLista_H" value=""/>
  	<input type="hidden" name="descMobFineLista_H" value=""/>
  	<input type="hidden" name="descCPI_H" value=""/>  
  <%out.print(htmlStreamTop);%> 

  <table class="main" border="0">
   
<%
// INIT-PARTE-TEMP
if (Sottosistema.MO.isOff()) {	
%>   
   <tr><td colspan="2"/>&nbsp;</td></tr>
   <tr>
     <td class="etichetta">Codice fiscale</td>
     <td class="campo"><af:textBox type="text" name="CF" value="<%=CF%>" maxlength="16" /></td>
   </tr>
   <tr>
     <td class="etichetta">Cognome </td>
     <td class="campo"> <af:textBox type="text" name="cognome" value="<%=cognome%>" maxlength="100"/></td>
   </tr>   
   <tr>
     <td class="etichetta">Nome</td>
     <td class="campo"><af:textBox type="text" name="nome" value="<%=nome%>" maxlength="100" /></td>
   </tr>
   <tr>
      <td class="etichetta">tipo ricerca</td>
      <td class="campo">
      <table colspacing="0" colpadding="0" border="0">
      <tr>
       <td><input type="radio" name="tipoRicerca" value="esatta" <% if (tipoRicerca.equals("esatta")) {%> CHECKED <%}%>/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
       <td><input type="radio" name="tipoRicerca" value="iniziaPer" <% if (tipoRicerca.equals("iniziaPer")) {%> CHECKED <%}%>/> inizia per</td>
      </tr>
      </table>
      </td>
    </tr>
<%
} else {
// END-PARTE-TEMP
%>	
<!-- sezione lavoratore -->
	<tr class="note">
	    <td colspan="2">
	      <div class="sezione2">
	          <img id='tendinaLav' alt='Chiudi' src='../../img/aperto.gif' onclick="cambiaTendina(this,'lavoratoreSez', document.form1.nome);" />&nbsp;&nbsp;&nbsp;Lavoratore
	      &nbsp;&nbsp;
	      
	        <a href="#" onClick="javascript:apriSelezionaSoggetto('Lavoratori', 'aggiornaLavoratore');<%=fSelLav%>"><img src="../../img/binocolo.gif" alt="Cerca"></a>
	           
	      </div>
	    </td>
	</tr>
	<tr>
	  <td colspan="2">
	    <div id="lavoratoreSez" style="display: none;">
	      <table class="main" width="100%" border="0">
	          <tr>
	            <td class="etichetta">Codice Fiscale</td>
	            <td class="campo" valign="bottom">
	              <af:textBox classNameBase="input" type="text" name="codiceFiscaleLavoratore" readonly="true" value="<%=CF%>" size="30" maxlength="16"/>
	              &nbsp;&nbsp;&nbsp;Validità C.F.&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGCFOK" readonly="true" value="<%=FlgCFok%>" size="3" maxlength="3"/>
	            </td>
	          </tr>
	          <tr >
	            <td class="etichetta">Cognome</td>
	            <td class="campo">
	              <af:textBox classNameBase="input" type="text" name="cognome" readonly="true" value="<%=cognome%>" size="30" maxlength="50"/>
	            </td>
	          </tr>
	          <tr>
	            <td class="etichetta">Nome</td>
	            <td class="campo">
	              <af:textBox classNameBase="input" type="text" name="nome" readonly="true" value="<%=nome%>" size="30" maxlength="50"/>
	            </td>
	          </tr>
	      </table>
	    </div>
	  </td>
	</tr>		
<!-- sezione azienda -->
	<tr class="note">
		<td colspan="2">
		<div class="sezione2"><img id='tendinaAzienda' alt="Chiudi"
			src="../../img/aperto.gif"
			onclick="cambiaTendina(this,'aziendaSez',document.form1.pIva);" />&nbsp;&nbsp;&nbsp;Azienda
		&nbsp;&nbsp; 
		<a href="#"
			onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');"><img
			src="../../img/binocolo.gif" alt="Cerca"></a> </div>
		</td>
	</tr>	
	<tr>
		<td colspan="2">
		<div id="aziendaSez" style="display: none">
		<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Codice Fiscale</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="codFiscaleAzienda" readonly="true"
						value="<%=codFiscaleAzienda%>" size="30" maxlength="16" /></td>
				</tr>
				<tr>
					<td class="etichetta">Partita IVA</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="pIva" readonly="true" value="<%=pIva%>" size="30"
						maxlength="11" /> &nbsp;&nbsp;&nbsp;Validità C.F./P.
					IVA&nbsp;&nbsp;<af:textBox classNameBase="input" type="text"
						name="FLGDATIOK" readonly="true" value="<%=FLGDATIOK%>"
						size="3" maxlength="3" /></td>
				</tr>
				<tr>
					<td class="etichetta">Ragione Sociale</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="ragioneSociale" readonly="true" value="<%=ragioneSociale%>"
						size="60" maxlength="100" /></td>
				</tr>
				<tr>
					<td class="etichetta">Indirizzo (Comune)</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="IndirizzoAzienda" readonly="true"
						value="<%=IndirizzoAzienda%>" size="60" maxlength="100" /></td>
				</tr>
				<tr>
					<td class="etichetta">Tipo Azienda</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="descrTipoAz" readonly="true" value="<%=descrTipoAz%>"
						size="30" maxlength="30" /> <af:textBox classNameBase="input"
						type="hidden" name="codTipoAz" readonly="true"
						value="<%=codTipoAz%>" size="10" maxlength="10" /> <af:textBox
						classNameBase="input" type="hidden" name="CODNATGIURIDICA"
						readonly="true" value="<%=CODNATGIURIDICA%>" size="10" maxlength="10" />
					</td>
				</tr>
		</table>
		</div>
		</td>
	</tr>
<% 
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>
    <tr><td>&nbsp;</td><tr>
    <tr ><td colspan="2" ><div class="sezione2"/>Dati Iscrizione</td></tr>
   
<input type="hidden" name="tipoLista"/>
<%

// INIT-PARTE-TEMP
if (Sottosistema.MO.isOff()) {	
%>   
   <tr> 
     <td class="etichetta">Tipo lista</td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="CodTipoLista" title="Tipo lista" moduleName="M_GetDeMbTipo" 
		addBlank="true"/>     
     </td>
   </tr>
<%
} else {
// END-PARTE-TEMP
%>	

  <tr> 
     <td class="etichetta">Tipo lista</td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="CodTipoLista" title="Tipo lista" moduleName="M_MO_TIPO_LISTA" 
		multiple="true" size="8" addBlank="false"></af:comboBox>
     </td>
   </tr>
<% 
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>
	 
	<tr>
	 <td class="etichetta" nowrap>Data domanda da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datdomandada" title="Data domanda da" validateOnPost="true" value="<%=datdomandada%>" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datdomandaa" title="Data domanda a" validateOnPost="true" value="<%=datdomandaa%>" size="10" maxlength="10"/>
	 </td>            
   </tr>

   <tr>
	 <td class="etichetta" nowrap>Data inizio da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datinizioda" title="Data inizio da" validateOnPost="true" value="<%=datinizioda%>" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datinizioa" title="Data inizio a" validateOnPost="true" value="<%=datinizioa%>" size="10" maxlength="10"/>
	 </td>            
   </tr>
   <tr>
	 <td class="etichetta" nowrap>Data fine da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datfineda" title="Data fine da" validateOnPost="true" value="<%=datfineda%>" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datfinea" title="Data fine a" validateOnPost="true" value="<%=datfinea%>" size="10" maxlength="10"/>
	 </td>            
   </tr>  
   
<%   
// INIT-PARTE-TEMP
if (Sottosistema.MO.isOff()) {	
%>  
<%
} else {
// END-PARTE-TEMP
%>
  <tr>
	 <td class="etichetta" nowrap>Flag mobilità aperte</td>
	 <td class="campo">
 		<input type="checkbox" name="FlgMobAperte" value="S" <% if (flgMobAperte.equals("S")) {%> CHECKED <%}%>/>
 	</td>            
  </tr>
  <tr>
	 <td class="etichetta" nowrap>Flag mobilità storicizzate</td>
	 <td class="campo">
 		<input type="checkbox" name="FlgMobStor" value="S" <% if (flgMobStor.equals("S")) {%> CHECKED <%}%>/>
 	</td>            
  </tr>	
  <tr> 
     <td class="etichetta">Motivo fine mobilità</td>
     <td class="campo">
		<af:comboBox addBlank="false" classNameBase="input" name="CodMotivoMob" title="Motivo fine mobilità" 
		    moduleName="M_MO_MOTIVO_FINE" multiple="true"/>     
     </td>
   </tr>
   <tr>
	 <td class="etichetta" nowrap>Escludi motivi fine mobilità selezionati</td>
	 <td class="campo">
 		<input type="checkbox" name="FlgEscludiFineMob" value="S" <% if (flgEscludiFineMob.equals("S")) {%> CHECKED <%}%>/>
 	</td>            
  </tr>
<% 
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>     
   <tr>
	 <td class="etichetta" nowrap>Data max differimento da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datmaxdiffda" title="Data max differimento da" validateOnPost="true" value="<%=datmaxdiffda%>" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datmaxdiffa" title="Data max differimento a" validateOnPost="true" value="<%=datmaxdiffa%>" size="10" maxlength="10"/>
	 </td>            
   </tr>
<%
// INIT-PARTE-TEMP
if (Sottosistema.MO.isOff()) {	
%>
   <tr>
	 <td class="etichetta" nowrap>Data CRT</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datcrtda" title="Data CRT da" validateOnPost="true" value="<%=datcrtda%>" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datcrta" title="Data CRT a" validateOnPost="true" value="<%=datcrta%>" size="10" maxlength="10"/>
	 </td>            
   </tr>  
<%
} else {
// END-PARTE-TEMP
%>	
  <tr> 
     <td class="etichetta">Motivo scorrimento</td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="CodMotivoScor" selectedValue="<%=CodMotivoScor %>" title="Motivo scorrimento" moduleName="M_MO_MOTIVO_SCOR"  addBlank="true"/>     
     </td>
   </tr>
   <tr>
	 <td class="etichetta" nowrap><%=labelDataCrt%></td>
	 <td class="campo" >
	    <af:textBox type="date" name="datcrtda" title="<%=titleDataCrtDa%>" validateOnPost="true" value="<%=datcrtda%>" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datcrta" title="<%=titleDataCrtA%>" validateOnPost="true" value="<%=datcrta%>" size="10" maxlength="10"/>
	 </td>            
   </tr>
<% 
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>  

<%
// INIT-PARTE-TEMP
if (Sottosistema.MO.isOff()) {	
%>  
<%
} else {
// END-PARTE-TEMP
%>	   
   <tr> 
     <td class="etichetta"><%=labelNumDelibera%></td>
     <td class="campo">
		<af:textBox type="text" name="NumDelReg" title="<%=labelNumDelibera%>" validateOnPost="true" size="15" maxlength="15" value="<%=NumDelReg %>"/>
     </td>
   </tr>
   <tr> 
     <td class="etichetta">Provenienza</td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="CodProv" title="Provenienza" moduleName="M_MO_PROVENIENZA" selectedValue="<%=CodProv %>" addBlank="true"/>     
     </td>
   </tr>
   <tr> 
     <td class="etichetta"><%=labelStatoRich %></td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="CodStatoMob" title="<%=labelStatoRich %>" 
		    moduleName="M_MO_STATO" multiple="true" selectedValue="<%=CodStatiMob %>"/>     
     </td>
   </tr>
   <tr><td>&nbsp;</td><tr>
   <tr><td colspan="2"><div class="sezione2">Indennizzo</td></tr>
   <tr>
   <tr> 
     <td class="etichetta">Mobilità indennizzata</td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="mobInd" title="Mobilita Indennizzata" selectedValue="<%=mobInd %>" moduleName="M_MO_INDENNITA" addBlank="true"/>     
     </td>
   </tr>
   <tr>
	 <td class="etichetta" nowrap>Data inizio indennizzo da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datinizioIndDa" title="Data inizio Ind Da" validateOnPost="true" value="<%=dataInizioIndDa %>" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datinizioIndA" title="inizio fine Ind A" validateOnPost="true" value="<%=dataInizioIndA %>" size="10" maxlength="10"/>
	 </td>            
   </tr>
   <tr>
	 <td class="etichetta" nowrap>Data fine indennizzo da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datfineIndDa" title="Data fine Ind Da" validateOnPost="true" value="<%=dataFineIndDa%>" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datfineIndA" title="Data fine Ind A" validateOnPost="true" value="<%=dataFineIndA%>" size="10" maxlength="10"/>
	 </td>            
   </tr> 
   <tr> 
     <td class="etichetta" nowrap>Soggetti L. 68/99</td>
	 <td class="campo">
	  <input type="checkbox" name="SoggettiCM" value="S" <% if (soggettiCM.equals("S")) {%> CHECKED <%}%>/>
	 </td>
   </tr>
   <tr> 
     <td class="etichetta">Soggetti con movimenti</td>
     <td class="campo">
     	<input type="checkbox" name="SoggettiConMov" value="S" onclick="gestioneSezioneMov(this);" <% if (soggettiConMov.equals("S")) {%> CHECKED <%}%>/>
     </td>
   </tr>
   <tr>
   	 <td class="etichetta" nowrap><div id="sezione_movimento1" style="display:none">Data inizio movimento da</div></td>
	 <td class="campo" ><div id="sezione_movimento2" style="display:none">
	    <af:textBox type="date" name="datInizioMovDa" title="Data inizio movimento da" validateOnPost="true" value="" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datInizioMovA" title="Data inizio movimento a" validateOnPost="true" value="" size="10" maxlength="10"/>
	 </div>
	 </td>        
   </tr>
   <%
	if ("1".equals(resultConfigCasoDubbio) ){
	%>
	<tr>
	    <td class="etichetta">Caso dubbio</td>
	    <td class="campo" colspan="2">
	 	    <af:comboBox name="flgCasoDubbio" classNameBase="input" addBlank="false" required="false" 
	 	     title="Flag caso dubbio"> 
		        <OPTION value=""></OPTION>
		        <OPTION value="S" <%if (flgCasoDubbio != null && flgCasoDubbio.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
		        <OPTION value="N" <%if (flgCasoDubbio != null && flgCasoDubbio.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
		    </af:comboBox>
		 </td>
	</tr>
	<%}%>
	<tr>
		<td class="etichetta" nowrap>Tipologia datore di lavoro</td>
		<td class="campo" colspan="2">
		    <af:comboBox name="flgNonImprenditore" title="Tipologia datore di lavoro" classNameBase="input" addBlank="false" > 
		        <OPTION value=""></OPTION>
		        <OPTION value="N" <%if (flgNonImprenditore != null && flgNonImprenditore.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>Imprenditore</OPTION>
		        <OPTION value="S" <%if (flgNonImprenditore != null && flgNonImprenditore.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Non imprenditore</OPTION>
		    </af:comboBox>  
	    </td>
	</tr>
   
<% 
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>
   <tr><td>&nbsp;</td><tr>
<%
// INIT-PARTE-TEMP
if (Sottosistema.MO.isOff()) {	
%>  
   <tr><td colspan="2"><div class="sezione2"></td></tr>
   <tr>
    <td class="etichetta">Centro per l'Impiego competente</td>
    <td class="campo">
      <af:comboBox name="CodCPI" title="Centro per l'Impiego competente" moduleName="M_ELENCOCPI" addBlank="true" selectedValue="<%=CodCPI%>" required="true"/>
    </td>
  <tr>
<%
} else {
// END-PARTE-TEMP
%>	
   <tr><td colspan="2"><div class="sezione2"></td></tr>
   <tr>
    <td class="etichetta">Centro per l'Impiego competente</td>
    <td class="campo">
      <af:comboBox name="CodCPI" title="Centro per l'Impiego competente" moduleName="M_ELENCOCPI" addBlank="true" selectedValue="<%=CodCPI%>"/>
    </td>
  <tr>
<% 
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>  
   <tr><td>&nbsp;</td></tr>
   <tr><td>&nbsp;</td></tr>
   <tr>
    <td colspan="2" align="center">
      <input class="pulsante" type="submit" name="cerca" value="Cerca"/>
      &nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla" onClick="document.form1.flgNonImprenditore.value='';"/> <!--onClick="javascript:visualizzaNumero('N');document.form1.codStatoOcc.value='';caricaDettStato('');" >-->
    </td>
   </tr>

<%if (canExportDBF) {%>
	<tr><td colspan="2"></td></tr>
	<tr><td colspan="2"><div class="sezione2"></td></tr>

   <tr>
   <td colspan="2"  align="center">Esporta anche mobilità sospese
con stato della richiesta INVIATO
       <input type="checkbox" name="EsportaAncheInviati" value="S"/>
   
      <input class="pulsante" type="button" name="mobesportaDBF" value="Esportazione DBF" onClick="confirmExport()"/>
    </td>
   </tr>
<%}%>   
   
   <input type="hidden" name="NEW_SESSION" value="TRUE"/>
   <input type="hidden" name="PAGE" value="MobilitaRisultRicercaPage"/>
   <input type="hidden" name="cdnfunzione" value="<%=_funzione%>"/>
<%
// INIT-PARTE-TEMP
if (Sottosistema.MO.isOff()) {	
%>  
<%
} else {
// END-PARTE-TEMP
%>  
   <!-- questi campi sono necessari per il corretto funzionamento delle tendine --> 
   <input type="hidden" name="CDNLAVORATORE" value="<%=cdnlavoratore%>" />
   <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>" />
   <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>" />
   <input type="hidden" name="codMotivoMob_H" value="" />
   <input type="hidden" name="codMotivoScor_H" value="" />
   <input type="hidden" name="codProv_H" value="" />
   <input type="hidden" name="mobInd_H" value="" />
<% 
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>
  </table>
</af:form>
</p>
 <%out.print(htmlStreamBottom);%>
<%
// INIT-PARTE-TEMP
if (Sottosistema.MO.isOff()) {	
%>  
<%
} else {
// END-PARTE-TEMP
%>		 
	 <script language="javascript">
	  var imgV = document.getElementById("tendinaLav");
	  <%if (!nome.equals("")) {%>
	    cambiaLavMC("lavoratoreSez","inline");
	    imgV.src = imgAperta;
	    imgV.alt="Chiudi";
	  <%} else {%>
	    cambiaTendina(imgV,"lavoratoreSez",document.form1.nome);
	  <%}%>
	        
	  imgV = document.getElementById("tendinaAzienda");
	  <%if (!pIva.equals("")) {%>
	    cambiaLavMC("aziendaSez","inline");
	    imgV.src = imgAperta;
	    imgV.alt="Chiudi";
	  <%} else {%>
	    cambiaTendina(imgV,"aziendaSez",document.form1.pIva)
	  <%}%>
	  
<% 
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>
</script>

</body>
</html>
