<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.sil.util.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css" />
<af:linkScript path="../../js/" />
<script language="Javascript">
	
	
	
	function checkListaComuni(comuni) {
		if (comuni.value == "") {
			document.Frm1.INSIEMEDICOMUNI.value = "";	
		}
	}
	
	function checkDisponibilita(){
		var comuneDispo = document.Frm1.codComune;
		var comuniDispo = document.Frm1.INSIEMEDICOMUNI;
		var provinciaDispo = document.Frm1.CODPROVINCIA;
		if ( (comuneDispo.value != "" && comuniDispo.value != "") || 
		     (comuneDispo.value != "" && provinciaDispo.value != "") ||
		     (comuniDispo.value != "" && provinciaDispo.value != "") ) {
			alert("Indicare come disponibilità territoriale il comune o la zona o la provincia.");
			return false;
		}
		return true;
	}
	
	function jsSelezComuniInZona() {
		var combo = document.Frm1.CODZONA;
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
  	
  	function jsSelezComuniInXxxOpenUrl(urlParams) {

		var url = "AdapterHTTP?PAGE=SelezComuniInXxxPage" +
					urlParams;
	
		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes," +
					"left=272,top=89,width=570,height=460"
		opened = window.open(url, "selComuni", feat);
		opened.focus();
	}
	
	function ReplaceAll(Source,stringToFind,stringToReplace){
	  	var temp = Source;
	    var index = temp.indexOf(stringToFind);
        while(index != -1){
            temp = temp.replace(stringToFind,stringToReplace);
            index = temp.indexOf(stringToFind);
        }
        return temp;
	}
	
	function jsSelezComuniRitornoConComuni(codComSepStr) {
		var newCodCom = ReplaceAll(codComSepStr, '#', ',');
		document.Frm1.INSIEMEDICOMUNI.value = newCodCom;
    }
    
    function btFindCittadinanza_onclick(codcittadinanza, codcittadinanzahid, cittadinanza, cittadinanzahid, nazione, nazionehid) {
		if (nazione.value == ""){
			nazionehid.value = "";
			codcittadinanzahid.value = "";
			codcittadinanza.value = "";
 			cittadinanzahid.value = "";
			cittadinanza.value = "";
			} else
		if (nazione.value != nazionehid.value) 
			window.open ("AdapterHTTP?PAGE=RicercaCittadinanzaPage&nazione="+nazione.value.toUpperCase()+"&retcod="+codcittadinanza.name+"&retcittadinanza="+cittadinanza.name+"&retnazione="+nazione.name, "Nazionalità", 'toolbar=0, scrollbars=1');
    
	}
	
	function clearTitolo() {
	  if (Frm1.codTitolo.value=="") {   
	    Frm1.codTitoloHid.value="";
	    Frm1.strTitolo.value=""; 
    	Frm1.strTipoTitolo.value=""; 
	  }
	}
	
	function ricercaAvanzataTitoliStudio() {
		var w=800; var l=((screen.availWidth)-w)/2;
	  	var h=500; var t=((screen.availHeight)-h)/2;
		//var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
	 	var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
	  	window.open("AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage", "Titoli", feat);
	}
	
	function selectTitolo_onClick(codTitolo, codTitoloHid, strTitolo, strTipoTitolo) {
	  if (codTitolo.value==""){
	    strTitolo.value="";
	    strTipoTitolo.value="";
	      
	  }
	  else if (codTitolo.value!=codTitoloHid.value){
	    window.open("AdapterHTTP?PAGE=RicercaTitoloStudioPage&codTitolo="+codTitolo.value, "Corsi", 'toolbar=0, scrollbars=1');
	  }
	}
	
	function toggleVisStato(codMonoStato) {
    }
    
    function ToUpperCase(inputName){
		var ctrlObj = eval("document.forms[0]." + inputName);
	    ctrlObj.value = ctrlObj.value.toUpperCase();
		return true;
	}
    
	function getTxt(elem){
		
		var txtSelected = elem.options[elem.selectedIndex].text;
		var provinciaTerrit = document.Frm1.provinciaTerrit;
		provinciaTerrit.value = txtSelected;
	}

</script>

</head>

<% boolean readOnlyStr = false;
   String htmlStreamTop = StyleUtils.roundTopTable(false);
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);
   
   
   String PROVINCIA_ISCR      = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVINCIA_ISCR");
   InfoRegioneSingleton regione = InfoRegioneSingleton.getInstance();
%>

<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca Lista Iscritti CM</p>
<p align="center">
	<af:form action="AdapterHTTP" method="GET" name="Frm1" onSubmit="checkDisponibilita()">
	<%out.print(htmlStreamTop);%>
	<table class="main">
		<tbody>
			
			<tr>
				<td colspan="2" />&nbsp;</td>
			</tr>

			<tr>
				<td>&nbsp;</td>
			</tr>
		    <tr>
	          <td class="etichetta">Ambito Territoriale</td>
	          <td class="campo">
	    	  	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
	        	classNameBase="input" title="Ambito Territoriale"  addBlank="true" onChange="getTxt(this)" />
	        	<input type="hidden" name="provinciaTerrit" />
	       	  </td>
	       	</tr>
			<tr>
				<td class="etichetta">Tipo iscrizione</td>
				<td class="campo">
				<af:comboBox name="codCMTipoIscr" moduleName="M_GETDECMTIPOISCR" classNameBase="input"
					addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>" />
				</td>
			</tr>
			
			<tr>
				<td class="etichetta">Tipo invalidità</td>
				<td class="campo">
				<af:comboBox name="codCMTipoInvalidita" moduleName="M_GETDECMTIPOINVALIDITA" classNameBase="input"
					addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>" />
				</td>
			</tr>
			
			<tr>
			   	<td class="etichetta">Stato dell'atto</td>
			   	<td class="campo">
			   	<af:comboBox  classNameBase="input" title="Stato dell'atto" name="CODSTATOATTO" selectedValue="PR" 
			   		moduleName="M_GETSTATOATTOISCR" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>" />
      			</td> 
      		</tr>
      		
			<tr>
				<td class="etichetta" nowrap>Data inizio da</td>
					 <td class="campo" >
					    <af:textBox type="date" name="datinizioda" title="Data inizio da" validateOnPost="true" value="" size="10" maxlength="10" />
					    a&nbsp;&nbsp;<af:textBox type="date" name="datinizioa" title="Data inizio a" validateOnPost="true" value="" size="10" maxlength="10"/>
				 </td>         
			</tr>
			
			<tr>
				<td class="etichetta">Percentuale invalidità da</td>
				<td class="campo"><af:textBox classNameBase="input" type="integer"
					name="numPercInvalilditaDa"
					readonly="<%=String.valueOf(readOnlyStr)%>" size="4" maxlength="3" />%&nbsp;
					a&nbsp;&nbsp;<af:textBox classNameBase="input" type="integer"
					name="numPercInvalilditaA"
					readonly="<%=String.valueOf(readOnlyStr)%>" size="4" maxlength="3" />%</td>
			</tr>
			
			
			<tr>
		    <td class="etichetta">Nazione di cittadinanza&nbsp;</td>
		    <td class="campo">
		        <af:textBox classNameBase="input" title="Cittadinanza" type="text" name="strNazione" value="" 
		        size="30" maxlength="40" inline="onkeypress=\"if(event.keyCode==13) { event.keyCode=9; this.blur(); }\" 
		        onBlur=\"btFindCittadinanza_onclick(document.Frm1.codCittadinanza, document.Frm1.codCittadinanzaHid, 
		                                           document.Frm1.strCittadinanza, document.Frm1.strCittadinanzaHid,
		                                           document.Frm1.strNazione, document.Frm1.strNazioneHid)\"" />
		    <af:textBox type="hidden" name="strNazioneHid" value=""/>
		    <af:textBox title="Cittadinanza" classNameBase="input" type="text" name="codCittadinanza" value="" size="5" maxlength="5" readonly="true" />&nbsp;
		    <af:textBox type="hidden" name="codCittadinanzaHid" value=""/>
		    <af:textBox type="text" classNameBase="input" name="strCittadinanza" value="" size="20" readonly="true"  />&nbsp;
		    <af:textBox type="hidden" name="strCittadinanzaHid" value=""/>
		
		    </td>
		  </tr>
			
		   <tr>
              <td class="etichetta">Titolo di studio</td>
              <td class="campo">
                    <af:textBox classNameBase="textbox" title="Codice del titolo" name="codTitolo" size="10" maxlength="8" onBlur="clearTitolo();" />&nbsp;                    
                    <af:textBox type="hidden" name="codTitoloHid" />
                    <A href="javascript:selectTitolo_onClick(Frm1.codTitolo, Frm1.codTitoloHid, Frm1.strTitolo,  Frm1.strTipoTitolo);">
                      <img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
                    <A href="javascript:ricercaAvanzataTitoliStudio();">
                      Ricerca avanzata
                    </A>                
              </td>
            </tr>
            
            <tr>
              <td class="etichetta">Tipo</td>
              <td class="campo">
                  <af:textBox type="hidden" name="flgLaurea" />
                  <af:textArea cols="50" 
                               rows="3"
                               classNameBase="textarea" title="Tipo del titolo" name="strTipoTitolo" readonly="true"  /> 
              </td>
            </tr>
            
            <tr>
              <td class="etichetta">Corso</td>
              <td class="campo">
                  <af:textArea cols="50" 
                               rows="4"  
                               classNameBase="textarea" name="strTitolo" readonly="true" />
              </td>
            </tr>
            
            <tr>
              <td class="etichetta">Profilo</td>
              <td class="campo">
                <af:comboBox name="CODPROFILO"
                             title="Profilo" moduleName="M_Combo_Suggerimenti" addBlank="true" blankValue=""/>
              </td>
            </tr>
			
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2">
				<div class="sezione2">Disponibilità territoriali</div>
				</td>
			</tr>
			
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
            
            <tr>
            
             <td class="etichetta" nowrap>Zona</td>
             <td class="campo" nowrap>
                <af:comboBox name="CODZONA"
                             title="Zona" moduleName="M_ListZone" addBlank="true" blankValue="" onChange="checkListaComuni(this)"/>&nbsp;&nbsp;
                <input type="button" class="pulsante" name="avanti" value="Avanti &gt;&gt;"
                 		onclick="jsSelezComuniInZona();">
              </td>
			</tr>
			
			<tr>
              <td class="etichetta">Provincia</td>
              <td class="campo">
                <af:comboBox name="CODPROVINCIA"
                             title="Province" moduleName="M_GetIDOProvince" addBlank="true" blankValue=""/>
              </td>
            </tr>
			
			<tr>
				<td>&nbsp;</td>
			</tr>
			
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input name="cerca" type="submit" class="pulsanti" value="Cerca" /> &nbsp;&nbsp; 
					<input name="reset"	type="reset" class="pulsanti" value="Annulla">
				</td>
			</tr>
		</tbody>
	</table>
	<%out.print(htmlStreamBottom);%>
	<input type="hidden" name="PAGE" value="CMListaIscrittiPage"/>
	<input type="hidden" name="cdnfunzione" value='<%=((String) serviceRequest.getAttribute("CDNFUNZIONE"))%>' />
	<input type="hidden" name="INSIEMEDICOMUNI" value=""/>
	
</af:form></p>

</body>
</html>
