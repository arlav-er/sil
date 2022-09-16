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
                  java.math.*,
                  java.util.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
  String _page = StringUtils.getAttributeStrNotNull(serviceRequest,"page");
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canSearch = attributi.containsButton("CERCA");
  boolean canReset = attributi.containsButton("ANNULLA");
  boolean canValidateAll = attributi.containsButton("VALIDATUTTI");
  boolean canStopValidation = attributi.containsButton("ARRESTA");
  boolean canViewResult = attributi.containsButton("RISULTATOTUTTI");
  boolean validazioneInCorso = false;
  if (sessionContainer.getAttribute("VALIDATOREMASSIVOMOBILITACORRENTE") != null) {
  	validazioneInCorso = true;
  }
  boolean risultatiInSessione = false;
  String prgUltimaValidazioneMassiva = "";
  BigDecimal prgMobilitaRis = null;	
  SourceBean risultatoUltimaVal = (SourceBean) serviceResponse.getAttribute("M_MobGetRisultatiUltimaValidazioneUtente.ROWS.ROW");
  if (risultatoUltimaVal != null) {
  	risultatiInSessione = risultatoUltimaVal.containsAttribute("PRGMOBILITARIS");
  	prgMobilitaRis = (BigDecimal)risultatoUltimaVal.getAttribute("PRGMOBILITARIS");
  	if (prgMobilitaRis != null) {
  		prgUltimaValidazioneMassiva = prgMobilitaRis.toString();	
  	}
  }
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title>Ricerca Mobilit&agrave;</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<%@ include file="MovimentiRicercaSoggetto.inc" %>
<%@ include file="MovimentiSezioniATendina.inc" %>
<%@ include file="Common_Function_Mov.inc" %>

<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

<script language="JavaScript">
<% 
   //Genera il Javascript che si occuperà di inserire i links nel footer
   attributi.showHyperLinks(out, requestContainer,responseContainer,"");
%>

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

function checkCampiObbligatori() {
	return true;
	//if ( (document.Frm1.cognome.value != "") || (document.Frm1.nome.value != "") || (document.Frm1.codiceFiscaleLavoratore.value != "") ) {
   	//	return true;
   	//}
   	//if ( (document.Frm1.codFiscaleAzienda.value != "") || (document.Frm1.ragioneSociale.value != "") || (document.Frm1.pIva.value != "") ) {
   	//	return true;
   	//}
   	//if ((document.Frm1.datinizioda.value != "") && (document.Frm1.datinizioa.value != "") ) {
	//	ok = controllaDateRange(document.Frm1.datinizioda.value, document.Frm1.datinizioa.value, 90);
	//	if (ok) return true;
	//}
	//if ((document.Frm1.datfineda.value != "") && (document.Frm1.datfinea.value != "") ) {
	//	ok = controllaDateRange(document.Frm1.datfineda.value, document.Frm1.datfinea.value, 90);
	//	if (ok) return true;
	//}
	//if ((document.Frm1.datmaxdiffda.value != "") && (document.Frm1.datmaxdiffa.value != "") ) {
	//	ok = controllaDateRange(document.Frm1.datmaxdiffda.value, document.Frm1.datmaxdiffa.value, 90);
	//	if (ok) return true;
	//}
	//if ((document.Frm1.datcrtda.value != "") && (document.Frm1.datcrta.value != "") ) {
	//	ok = controllaDateRange(document.Frm1.datcrtda.value, document.Frm1.datcrta.value, 90);
	//	if (ok) return true;
	//}
	//if (document.Frm1.NumDelReg.value != "") {
	//	return true;
	//}
	//msg = "Parametri generici.\n"+
	//		"OPZIONE 1 - Uno dei seguenti valori: Lavoratore / Azienda.\n" +
	//		"OPZIONE 2 - Periodo data da... a...  con periodo max 90 gg.\n" + 
	//		"OPZIONE 3 - Numero Delibera.";
	//	
	//alert(msg);     
	//return (false);
}

//funzione di get per il pulsante valida tutti
function validateAll() {
	if (confirm('Vuoi validare tutti le mobilità presenti nel sistema?')) {
		var get = "AdapterHTTP?PAGE=MobilitaValidazionePage&cdnFunzione=<%=_funzione%>" 
				  + "&azioneScelta=validaTutti";
		setWindowLocation(get);
	}
}

//funzione di get per l'arresto della validazione massiva
function arrestaValidazione() {
	if (confirm('Vuoi arrestare la validazione massiva corrente?')) {
		var get = "AdapterHTTP?PAGE=MobilitaValidazionePage&cdnFunzione=<%=_funzione%>&azioneScelta=arresta";
		setWindowLocation(get);
	}
}

//funzione di get per la visualizzazione dei risultati della validazione massiva
function visulizzaRisultati() {
	var get = "AdapterHTTP?PAGE=MobVisualizzaRisultValidazionePage&cdnFunzione=<%=_funzione%>&PAGERISULTVALMASSIVA=FIRST&prgUltimaValidazioneMassiva=<%=prgUltimaValidazioneMassiva%>";
	setWindowLocation(get);
}

function aggiornaLavoratore(){
    document.Frm1.codiceFiscaleLavoratore.value = opened.dati.codiceFiscaleLavoratore;
    document.Frm1.cognome.value = opened.dati.cognome;
    document.Frm1.nome.value = opened.dati.nome;
    document.Frm1.CDNLAVORATORE.value = opened.dati.cdnLavoratore;
    document.Frm1.FLGCFOK.value = opened.dati.FLGCFOK;
    if ( document.Frm1.FLGCFOK.value == "S" ){ 
                document.Frm1.FLGCFOK.value = "Si";
    }else 
          if ( document.Frm1.FLGCFOK.value != "" ){
            document.Frm1.FLGCFOK.value = "No";
          }
    opened.close();
    var imgV = document.getElementById("tendinaLav");
    cambiaLavMC("lavoratoreSez","inline");
    imgV.src=imgAperta;
}

function aggiornaAzienda(){
    document.Frm1.ragioneSociale.value = opened.dati.ragioneSociale;
    document.Frm1.pIva.value = opened.dati.partitaIva;
    document.Frm1.codFiscaleAzienda.value = opened.dati.codiceFiscaleAzienda;
    document.Frm1.PRGAZIENDA.value = opened.dati.prgAzienda;
    document.Frm1.PRGUNITA.value = opened.dati.prgUnita;
    document.Frm1.codTipoAzienda.value = opened.dati.codTipoAz;
    document.Frm1.FLGDATIOK.value = opened.dati.FLGDATIOK;
    prgAzienda = opened.dati.prgAzienda;
    prgUnita = opened.dati.prgUnita;
    if ( document.Frm1.FLGDATIOK.value == "S" ) { 
    	document.Frm1.FLGDATIOK.value = "Si";
    }
    else { 
   		if ( document.Frm1.FLGDATIOK.value != "" ) {
      		document.Frm1.FLGDATIOK.value = "No";
       	}
    }   	
    opened.close();
    var imgV = document.getElementById("tendinaAzienda");
    cambiaLavMC("aziendaSez","inline");
    imgV.src=imgAperta;
    
}

</script>

<SCRIPT TYPE="text/javascript">
  function controlloDate(){
    var objDataDa = document.Frm1.datinizioda;
    var objDataA = document.Frm1.datinizioa;    
	if ((objDataDa.value != "") && (objDataA.value != "")) {
      if (compareDate(objDataDa.value,objDataA.value) > 0) {
      	alert(objDataDa.title + " maggiore di " + objDataA.title);
      	objDataDa.focus();
	    return false;
	  }	
	}
    objDataDa = document.Frm1.datfineda;
    objDataA = document.Frm1.datfinea;	
	if ((objDataDa.value != "") && (objDataA.value != "")) {
      if (compareDate(objDataDa.value,objDataA.value) > 0) {
      	alert(objDataDa.title + " maggiore di " + objDataA.title);
      	objDataDa.focus();
	    return false;
	  }	
	}
    objDataDa = document.Frm1.datmaxdiffda;
    objDataA = document.Frm1.datmaxdiffa;	
	if ((objDataDa.value != "") && (objDataA.value != "")) {
      if (compareDate(objDataDa.value,objDataA.value) > 0) {
      	alert(objDataDa.title + " maggiore di " + objDataA.title);
      	objDataDa.focus();
	    return false;
	  }	
	}
    objDataDa = document.Frm1.datcrtda;
    objDataA = document.Frm1.datcrta;	
	if ((objDataDa.value != "") && (objDataA.value != "")) {
      if (compareDate(objDataDa.value,objDataA.value) > 0) {
      	alert(objDataDa.title + " maggiore di " + objDataA.title);
      	objDataDa.focus();
	    return false;
	  }	
	}
    return true;
  }

  function valorizzaHidden() {
  	document.Frm1.descCodTipoLista_H.value = document.Frm1.CodTipoLista.options[document.Frm1.CodTipoLista.selectedIndex].text;
  	document.Frm1.descCPI_H.value = document.Frm1.CodCPI.options[document.Frm1.CodCPI.selectedIndex].text;
  	return true;
  }

</SCRIPT>

</head>

<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca mobilit&agrave; da validare</p>
<p align="center">
  <af:form name="Frm1" action="AdapterHTTP" method="POST" onSubmit="checkCampiObbligatori() && controlloDate() && valorizzaHidden()">
  	<input type="hidden" name="descCodTipoLista_H" value=""/>
  	<input type="hidden" name="descCPI_H" value=""/>  
  <%out.print(htmlStreamTop);%> 
  <table class="main" border="0">    
   <!-- sezione lavoratore -->
	<tr class="note">
	    <td colspan="2">
	      <div class="sezione2">
	          <img id='tendinaLav' alt='Chiudi' src='../../img/aperto.gif' onclick="cambiaTendina(this,'lavoratoreSez', document.Frm1.nome);" />&nbsp;&nbsp;&nbsp;Lavoratore
	      &nbsp;&nbsp;
	        <a href="#" onClick="javascript:apriSelezionaSoggetto('Lavoratori', 'aggiornaLavoratore');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
	        &nbsp;<a href="#" onClick="javascript:azzeraLavoratore();"><img src="../../img/del.gif" alt="Azzera selezione"></a>   
	      </div>
	    </td>
	</tr>
	<tr>
	  <td colspan="2">
	    <div id="lavoratoreSez" style="display:inline;">
	      <table class="main" width="100%" border="0">
	          <tr>
	            <td class="etichetta">Codice Fiscale</td>
	            <td class="campo" valign="bottom">
	              <af:textBox classNameBase="input" type="text" name="codiceFiscaleLavoratore" value="" size="30" maxlength="16"/>
	              &nbsp;&nbsp;&nbsp;Validità C.F.&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGCFOK" readonly="true" value="" size="3" maxlength="3"/>
	            </td>
	          </tr>
	          <tr >
	            <td class="etichetta">Cognome</td>
	            <td class="campo">
	              <af:textBox classNameBase="input" type="text" name="cognome" value="" size="30" maxlength="50"/>
	            </td>
	          </tr>
	          <tr>
	            <td class="etichetta">Nome</td>
	            <td class="campo">
	              <af:textBox classNameBase="input" type="text" name="nome" value="" size="30" maxlength="50"/>
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
			src="../../img/aperto.gif" onclick="cambiaTendina(this,'aziendaSez',document.Frm1.pIva);" />&nbsp;&nbsp;&nbsp;Azienda
			&nbsp;&nbsp;<a href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
			&nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
		</div>
		</td>
	</tr>	
	<tr>
		<td colspan="2">
		<div id="aziendaSez" style="display:inline;">
		<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Codice Fiscale</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="codFiscaleAzienda" value="" size="30" maxlength="16" /></td>
				</tr>
				<tr>
					<td class="etichetta">Partita IVA</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="pIva" value="" size="30" maxlength="11" /> &nbsp;&nbsp;&nbsp;Validità C.F./P.
					IVA&nbsp;&nbsp;<af:textBox classNameBase="input" type="text"
						name="FLGDATIOK" readonly="true" value="" size="3" maxlength="3" /></td>
				</tr>
				<tr>
					<td class="etichetta">Ragione Sociale</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="ragioneSociale" value="" size="60" maxlength="100" />
						<input type="hidden" name="IndirizzoAzienda" value="" />
					</td>
				</tr>
				<tr>
				  <td class="etichetta">Tipo Azienda</td>
				  <td class="campo">
				    <af:comboBox classNameBase="input" title="Tipo Azienda" name="codTipoAzienda" moduleName="M_GetTipiAzienda" addBlank="true" />
				  </td>
                </tr>
		</table>
		</div>
		</td>
	</tr>
	
   <tr><td colspan="2" ><div class="sezione2"/></td></tr>
   <tr>
     <td class="etichetta">Tipo lista</td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="CodTipoLista" title="Tipo lista" moduleName="M_GetDeMbTipo" 
		addBlank="true"/>     
     </td>
   </tr>
   <tr>
	 <td class="etichetta" nowrap>Data inizio da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datinizioda" title="Data inizio da" validateOnPost="true" value="" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datinizioa" title="Data inizio a" validateOnPost="true" value="" size="10" maxlength="10"/>
	 </td>            
   </tr>
   <tr>
	 <td class="etichetta" nowrap>Data fine da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datfineda" title="Data fine da" validateOnPost="true" value="" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datfinea" title="Data fine a" validateOnPost="true" value="" size="10" maxlength="10"/>
	 </td>            
   </tr>
   <tr>
	 <td class="etichetta" nowrap>Data max differimento da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datmaxdiffda" title="Data max differimento da" validateOnPost="true" value="" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datmaxdiffa" title="Data max differimento a" validateOnPost="true" value="" size="10" maxlength="10"/>
	 </td>            
   </tr>
   <tr>
	 <td class="etichetta" nowrap>Data CRT da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="datcrtda" title="Data CRT da" validateOnPost="true" value="" size="10" maxlength="10"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="datcrta" title="Data CRT a" validateOnPost="true" value="" size="10" maxlength="10"/>
	 </td>            
   </tr>
   <tr> 
     <td class="etichetta">Numero Delibera Regionale </td>
     <td class="campo">
		<af:textBox type="text" name="NumDelReg" title="Numero delibera regionale" validateOnPost="true" size="15" maxlength="15"/>
     </td>
   </tr>
   <tr>
	 <td class="etichetta" nowrap>Domande di mobilità respinte</td>
	 <td class="campo">
 		<input type="checkbox" name="FlgMobRespinte" value="I"/>
 	 </td>            
   </tr>
   <tr><td>&nbsp;</td><tr>
   <tr><td colspan="2"><div class="sezione2"></td></tr>
   <tr>
    <td class="etichetta">Centro per l'Impiego competente</td>
    <td class="campo">
      <af:comboBox name="CodCPI" title="Centro per l'Impiego competente" moduleName="M_ELENCOCPI" addBlank="true"/>
    </td>
  <tr>
   <tr><td>&nbsp;</td></tr>
   <tr><td>&nbsp;</td></tr>
   <tr>
    <td colspan="2" align="center">
    <% if (canSearch) { %> 
	      <input class="pulsante" type="submit" name="cerca" value="Cerca"/>
	      &nbsp;&nbsp;
	<% }
	if (canReset) { %>        
      	  <input name="reset" type="reset" class="pulsanti" value="Annulla"/>
      	  &nbsp;&nbsp;
    <% }
    if (!validazioneInCorso) {   
		if (canValidateAll) {%> 
        	<input class="pulsanti" type="button" onclick="validateAll();" name="validaTutti" value="Valida tutti" title="Valida tutte le mobilità presenti nel sistema"/>
        	&nbsp;&nbsp;
	   	<%} 
	} else {
		if (canStopValidation) {%>  
	        <input class="pulsanti" type="button" onclick="arrestaValidazione();" name="reset" value="Arresta Validazione" title="Ferma la validazione massiva attualmente in esecuzione"/>
	        &nbsp;&nbsp;
	    <%}
	}
	
	if (canViewResult) {
      	if (risultatiInSessione) {%>
      		<input class="pulsanti" type="button" onclick="visulizzaRisultati();" name="azioneVisualizzaRis" value="Risultati ultima validazione" title="Visualizza i risultati dell'ultima validazione massiva eseguita"/>
  		<%}
  	 }%>
    </td>
   </tr>
   <input type="hidden" name="NEW_SESSION" value="TRUE"/>
   <input type="hidden" name="PAGE" value="MobilitaValidazioneRisultRicercaPage"/>
   <input type="hidden" name="cdnfunzione" value="<%=_funzione%>"/>
   <!-- questi campi sono necessari per il corretto funzionamento delle tendine -->
   <input type="hidden" name="CDNLAVORATORE" value="" />
   <input type="hidden" name="PRGAZIENDA" value="" />
   <input type="hidden" name="PRGUNITA" value="" />
   <input type="hidden" name="codMotivoMob_H" value="" />
   <input type="hidden" name="codMotivoScor_H" value="" />
   <input type="hidden" name="codProv_H" value="" />
   <input type="hidden" name="mobInd_H" value="" />
  </table>
</af:form>
</p>
 <%out.print(htmlStreamBottom);%>
</body>
</html>
