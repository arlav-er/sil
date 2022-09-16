<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.afExt.utils.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");

	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false); 
	
	//Recupero filtri di ricerca usati
	String codiceFiscale =  StringUtils.getAttributeStrNotNull(serviceRequest,"codiceFiscale");
	String cognome =		StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
	String nome =			StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
	String tipoRicerca = 	StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
	String codTipoValidita=	StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoValidita");
	String codTipoEvidenza =StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoEvidenza");
	String dataScad_DAL=	StringUtils.getAttributeStrNotNull(serviceRequest,"dataScadenza_DAL");
	String dataScad_AL=		StringUtils.getAttributeStrNotNull(serviceRequest,"dataScadenza_AL");
	String messaggio = 		StringUtils.getAttributeStrNotNull(serviceRequest,"messaggio");
	String codCPI=			StringUtils.getAttributeStrNotNull(serviceRequest,"codCPI");
	%>
	
<html>
<head>
<title>Ricerca evidenze lavoratore</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>
<script type="text/javascript">
function compareDate(date1,date2) {
    if (date1==null || date1=="") return -1;    
    if (date2==null || date2=="") return 1;
    if ( toDate(date1).getTime()<toDate(date2).getTime()) return -1;
    if ( toDate(date1).getTime()>toDate(date2).getTime()) return 1;
    //if ( toDate(date1).getTime()==toDate(date2).getTime()) 
    return 0;
}

function toDate(newDate) {
    var tokens = newDate.split('/');
    var usDate= tokens[2]+"/"+tokens[1]+"/"+tokens[0];
    return new Date(usDate);
}

function controlloCampi(){
	var procedi = false;
	var elem = document.form.CodiceFiscale;
	if (elem.value!=""){
		procedi=true;
		if(elem.value.length<6){
		alert("Digitare almeno 6 caratteri del codice fiscale.");
		return false;
		}
	}
	elem = document.form.Cognome;
	if(elem.value!=""){
		procedi=true; 
		if(elem.value.length<3){
		alert("Digitare almeno 3 caratteri del cognome.");
		return false;
		}
	}

	elem = document.form.Nome;
	if(elem.value!=""){
		procedi=true;
		if(elem.value.length<3){
		alert("Digitare almeno 3 caratteri del nome.");
		return false;
		}
	}

	elem = document.form.codTipoEvidenza;
	if(elem.selectedIndex>-1){
		procedi=true;
	}

	elem = document.form.codTipoValidita;
	if(elem.selectedIndex>0){
		procedi=true;
	}

	elem = document.form.dataScadenza_DAL;
	if(elem.value!=""){
		procedi=true;
	}

	elem = document.form.dataScadenza_AL;
	if(elem.value!=""){
		procedi=true;
	}

	elem = document.form.messaggio;
	if(elem.value!=""){
		procedi=true;
	}

	elem = document.form.codCPI;
	if(elem.selectedIndex>0){
		procedi=true;
	}

	if(!procedi){
		alert("Valorizzare almeno un filtro di ricerca.");
		return false;
	}
	return procedi;
	
}

function controlloDate(){
    var objDataDal = document.form.dataScadenza_DAL;
    var objDataAl = document.form.dataScadenza_AL;    
	if ((objDataDal.value != "") && (objDataAl.value != "")) {
      if (compareDate(objDataDal.value,objDataAl.value) > 0) {
      	alert(objDataDal.title + " maggiore di " + objDataAl.title);
      	objDataDal.focus();
	    return false;
	  }	
	}
	return true;
}

function controlloMessaggio(){
	var objMsg = document.form.messaggio;
	if (objMsg.value!="" && objMsg.value.length<3){
		alert("Il testo del messaggio dev'essere almeno di tre caratteri.");
		objMsg.focus();
		return false;
	}
	return true;
}

function valorizzaStrTipoEvidenza(){
	try{
	var tipiEvidenzaObj = document.form.codTipoEvidenza;
	var strObj = document.form.strTipoEvidenza;
	
	var k = tipiEvidenzaObj.options.length;
	for(var k=0;k<tipiEvidenzaObj.options.length;k++){
		if (tipiEvidenzaObj[k].selected==true)
			strObj.value+=tipiEvidenzaObj[k].text+",";
	}
	if(strObj.value!=""){
		strObj.value = (strObj.value).substring(0,(strObj.value).length-1);
	}
	return true;
	}catch(err){
		return false;
	}
}

function selectTipoEvidenza(){
	var tipoevidenza = "<%=codTipoEvidenza %>";
 	tokens = tipoevidenza.split(",");
 
 	for(var i=0;i<tokens.length;i++){
 		var selected = tokens[i];
 		if (selected != ""){
	 		for(var j=0;j<document.form.codTipoEvidenza.length;j++){
	 			if(document.form.codTipoEvidenza[j].value==selected){
	 				document.form.codTipoEvidenza[j].selected=true;
	 			}
	 		}
 		}
 	}
}

function valorizzaStrCPI(){
try{
	var strCPIobj = document.form.strCPI;
	strCPIobj.value = document.form.codCPI[document.form.codCPI.selectedIndex].text;
	return true;
}catch(err){return false;}
}
</script>
</head>
<body class="gestione" onload="selectTipoEvidenza()">
<p class="titolo">Ricerca evidenze</p>
<%= htmlStreamTop %>
<af:form name="form" action="AdapterHTTP" method="POST" onSubmit="controlloDate() && controlloMessaggio() && valorizzaStrTipoEvidenza() && valorizzaStrCPI() && controlloCampi()">
<input type="hidden" name="PAGE" value="RisultatiRicercaEvidenzePage" />
<input type="hidden" name="cdnFunzione" value="<%=cdnfunzione%>" />
<input type="hidden" name="strTipoEvidenza" value=""/>
<input type="hidden" name="strCPI" value=""/>
<table class="main">
        <tr><td colspan="2"/><br/>Per effettuare una ricerca inserire almeno un filtro di ricerca.</td></tr>
        <tr><td colspan="2"/>&nbsp;</td></tr>
        <tr>
          <td class="etichetta">Codice fiscale</td>
          <td class="campo">
            <af:textBox type="text" name="CodiceFiscale" value="<%=codiceFiscale%>" size="20" maxlength="16"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Cognome</td>
          <td class="campo">
            <af:textBox type="text" name="Cognome" value="<%=cognome%>" size="20" maxlength="50"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Nome</td>
          <td class="campo">
            <af:textBox type="text" name="Nome" value="<%=nome%>" size="20" maxlength="50"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Tipo ricerca</td>
          <td class="campo">
          <table colspacing="0" colpadding="0" border="0">
          <tr>
           <td><input type="radio" name="tipoRicerca" value="esatta" <%= !tipoRicerca.equals("iniziaPer")?"CHECKED":""%>/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
           <td><input type="radio" name="tipoRicerca" value="iniziaPer" <%= tipoRicerca.equals("iniziaPer")?"CHECKED":""%> /> inizia per</td>
          </tr>
          </table>
          </td>
        </tr>
        <tr>
        <td colspan="2">
        <hr>
        </td>
        </tr>
        <tr>
        	<td class="etichetta">Tipo evidenze</td>
        	<td class="campo">
        		<af:comboBox moduleName="MTipiEvidenze" multiple="true" size="10" classNameBase="input" name="codTipoEvidenza" title="Tipo evidenza" addBlank="false"/>     
        	</td>
        </tr>
        <tr>
        	<td class="etichetta">Tipo validità</td>
        	<td class="campo">
        		  <select classNameBase="input" name="codTipoValidita" title="Tipo validità">
        		  	<option value=""></option>
        		  	<option value="V" <%=codTipoValidita.equals("V")?"SELECTED":"" %>>Valida</option>
        		  	<option value="S" <%=codTipoValidita.equals("S")?"SELECTED":"" %>>Scaduta</option>
        		  </select>
        	</td>
        </tr>
        <tr>
        	<td class="etichetta">
	        	Data scadenza dal</td>
	        <td class="campo">
	        	<af:textBox type="date" value="<%=dataScad_DAL %>" name="dataScadenza_DAL" title="Data scadenza dal" size="11" maxlength="10"/>
	        	al
	        	<af:textBox type="date" value="<%=dataScad_AL %>" name="dataScadenza_AL" title="Data scadenza al" size="11" maxlength="10"/>
	        </td>
	    </tr>   
        <tr>
        	<td class="etichetta">Messaggio</td>
        	<td class="campo">
        		<af:textArea name="messaggio" rows="3" value="<%=messaggio %>"/>
        	</td>
        </tr>
        <tr>
        	<td class="etichetta">Centro per l'impiego</td>
        	<td class="campo">
        		<af:comboBox title="Centro per l'impiego"  moduleName="M_ELENCOCPI" selectedValue="<%=codCPI %>" name="codCPI" addBlank="true"></af:comboBox>
        	</td>
        </tr>
        <tr>
        	<td colspan="2">
			<span class="bottoni">
				<input type="submit" class="pulsanti" name="cerca" value="Cerca"/>
				&nbsp;&nbsp;
				<input type="reset" class="pulsanti" name="annulla" value="Annulla" />
			</span>
		</td>
        </tr>
        </table>
        <%= htmlStreamBottom %>
</af:form>
</body>