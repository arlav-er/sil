
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.module.movimenti.constant.Properties,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<% 
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
    
  	String COGNOME=StringUtils.getAttributeStrNotNull(serviceRequest,"COGNOME");
  	String NOME=StringUtils.getAttributeStrNotNull(serviceRequest,"NOME");
  	String CF=StringUtils.getAttributeStrNotNull(serviceRequest,"CF");
  	String CodCPI=StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
  	String flgPatto297=StringUtils.getAttributeStrNotNull(serviceRequest,"flgPatto297");
  	String codCodificaPatto=StringUtils.getAttributeStrNotNull(serviceRequest,"codCodificaPatto");
  	String codTipoPatto=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoPatto");
  	String AttiAperti=StringUtils.getAttributeStrNotNull(serviceRequest,"AttiAperti"); 
  	boolean isPattoOnLine = false;
  	String pattiOnLine = null;
  	String codAccOnLine = null;
	String configPattoOnLine = serviceResponse.containsAttribute("M_Config_PattoOnLine.ROWS.ROW.STRVALORECONFIG")?
		  	serviceResponse.getAttribute("M_Config_PattoOnLine.ROWS.ROW.STRVALORECONFIG").toString():Properties.DEFAULT_CONFIG;
  	if(configPattoOnLine.equalsIgnoreCase(Properties.CUSTOM_CONFIG)){
  		isPattoOnLine = true;
  		pattiOnLine=StringUtils.getAttributeStrNotNull(serviceRequest,"PattiOnLine");
  		codAccOnLine=StringUtils.getAttributeStrNotNull(serviceRequest,"codAccOnLine");
   	}
  	String datInizioDa=StringUtils.getAttributeStrNotNull(serviceRequest,"datInizioDa");
  	String datInizioA=StringUtils.getAttributeStrNotNull(serviceRequest,"datInizioA");      
  	String scadconfda=StringUtils.getAttributeStrNotNull(serviceRequest,"scadconfda");
  	String scadconfa=StringUtils.getAttributeStrNotNull(serviceRequest,"scadconfa");      
  	String dataFineAttoDa=StringUtils.getAttributeStrNotNull(serviceRequest,"dataFineAttoDa");
  	String dataFineAttoA=StringUtils.getAttributeStrNotNull(serviceRequest,"dataFineAttoA");  
  	String MotivoFine=StringUtils.getAttributeStrNotNull(serviceRequest,"MotivoFine");    
  	String tipoRicerca=StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
  	if (tipoRicerca.equals("")) tipoRicerca="esatta";
  	Vector rowCodificaPatto  = serviceResponse.getAttributeAsVector("M_GetCodificaTipoPatto.ROWS.ROW");
  	SourceBean row_CurrPatto = null;
  	String numConfigPA = serviceResponse.containsAttribute("M_CONFIG_PACCHETTO_ADULTI.ROWS.ROW.NUM")?
  						 serviceResponse.getAttribute("M_CONFIG_PACCHETTO_ADULTI.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;
%>

<%	String visualizza_display = "none";
	String imgChiudi = "../../img/chiuso.gif";
	String imgApri = "../../img/aperto.gif"; 
%>
<html>
<head>
	<title>Ricerca Patto/Accordo Lavoratori</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<script language="Javascript">
<%@ include file="../documenti/RicercaCheck.inc" %>
</script>
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
<script language="Javascript">

var arrayCodificaPatto = new Array();
var arrayFlgPatto297 = new Array();
<%for(int indexPatto=0; indexPatto < rowCodificaPatto.size(); indexPatto++)  { 
	row_CurrPatto = (SourceBean) rowCodificaPatto.elementAt(indexPatto);
    out.print("arrayCodificaPatto["+indexPatto+"]=\""+ row_CurrPatto.getAttribute("CODICE").toString()+"\";\n");
    out.print("arrayFlgPatto297["+indexPatto+"]=\""+ row_CurrPatto.getAttribute("FLGPATTO297").toString()+"\";\n");
}
%>

function cambia(immagine, sezione) {
		if (sezione.aperta==null) {
			sezione.aperta=true;
		}
		if (sezione.aperta) {
			sezione.style.display="none";
			sezione.aperta=false;
			immagine.src="../../img/chiuso.gif";
		}
		else {
			sezione.style.display="inline";
			sezione.aperta=true;
			immagine.src="../../img/aperto.gif";
		}
	}
	
	function Sezione(sezione, img,aperta){    
	    this.sezione=sezione;
	    this.aperta=aperta;
	    this.img=img;
	}

	
	function controllaFiltriRicerca(){
		var msg = "Attenzione!\r\n";
			msg+= "La ricerca potrebbe essere pesante e\r\n";
			msg+= "rallentare l'applicazione.\r\n\r\nVuoi proseguire?";
		var showAlert = true;
		var form = document.form1;
		if (form.COGNOME.value!="" || form.NOME.value!="" || form.CF.value!="" ||
			form.flgPatto297.value!="" || form.codTipoPatto.value!="" || form.datInizioDa.value!="" ||
			form.datInizioA.value!="" || form.scadconfda.value!="" || form.scadconfa.value!="" || 
			form.dataFineAttoDa.value!="" || form.dataFineAttoA.value!="" || form.AttiAperti.value=="" ||
			form.MotivoFine.value!="" || form.PattiOnLine.value!="" || form.codAccOnLine.value!="")
			    
		{
		  showAlert = false;
		}
			
		if (showAlert) { 
		  if (confirm(msg)) {
			return true;
		  } else {
		    return false;
		  }
		}

	    return true;		
	}

  function controlloDate(){
  	
  	 var dataStipDa = document.form1.datInizioDa.value;
     var dataStipA = document.form1.datInizioA.value; 
     var numeroGiorni = confrontaDate(dataStipDa, dataStipA) + 1;
		if (document.form1.AttiAperti.value == ""){
		
    		if ( ((dataStipDa == "") && (dataStipA == "")) || (numeroGiorni > 90) ){ 
    			alert ("Parametri generici.\n" + "Data Stipula da... a... con periodo max 90 gg.");
			 	return false;
			 }
			if ( ((dataStipDa != "") && (dataStipA == ""))){
				alert("Il campo Data Stipula a è obbligatorio");
				return false;
			}
			if ( ((dataStipDa == "") && (dataStipA != ""))){
				alert("Il campo Data Stipula da è obbligatorio");
				return false;
			}
			if ((document.form1.COGNOME.value=="" && document.form1.NOME.value=="" && document.form1.CF.value=="" &&
				document.form1.flgPatto297.value=="" && document.form1.codTipoPatto.value=="" && document.form1.scadconfda.value=="" 
				&& document.form1.scadconfa.value=="" && document.form1.dataFineAttoDa.value=="" && document.form1.dataFineAttoA.value==""
				&& document.form1.MotivoFine.value=="")) {
			
					return confirm("ATTENZIONE!\r\n La ricerca potrebbe essere pesante e\r\n rallentare l'applicazione.\r\n\r\n Vuoi continuare?");
			}
			return true;	
    	}
		
  	
  		var objDataDa = document.form1.datInizioDa;
    	var objDataA = document.form1.datInizioA;    
		if ((objDataDa.value != "") && (objDataA.value != "")) {
      		if (compareDate(objDataDa.value,objDataA.value) > 0) {
      			alert(objDataDa.title + " maggiore di " + objDataA.title);
      			objDataDa.focus();
	    		return false;
	  		}	
		}
    	objDataDa = document.form1.scadconfda;
    	objDataA = document.form1.scadconfa;	
		if ((objDataDa.value != "") && (objDataA.value != "")) {
      		if (compareDate(objDataDa.value,objDataA.value) > 0) {
      			alert(objDataDa.title + " maggiore di " + objDataA.title);
      			objDataDa.focus();
	    		return false;
	  		}	
		}
		objDataDa = document.form1.dataFineAttoDa;
    	objDataA = document.form1.dataFineAttoA;
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
  	document.form1.descFlgPatto297_H.value = document.form1.codCodificaPatto.options[document.form1.codCodificaPatto.selectedIndex].text;
  	document.form1.descCodTipoPatto_H.value = document.form1.codTipoPatto.options[document.form1.codTipoPatto.selectedIndex].text;  	
  	document.form1.descCPI_H.value = document.form1.CodCPI.options[document.form1.CodCPI.selectedIndex].text;
  	document.form1.motSelected_H.value = document.form1.MotivoFine.options[document.form1.MotivoFine.selectedIndex].text;
  	return true;
  }
  
  	function visualizza(val) {
	var sezioneTitolo = document.getElementById("titoloSezione");
    var sezione = document.getElementById("TBL0");
    var image = document.getElementById("IMG0");
    
    if ((val == "No") || (val == "")) {
    	  sezioneTitolo.style.display="inline";
    	  sezione.style.display="inline";
    	  image.src="../../img/aperto.gif"; }
    else{
    	sezioneTitolo.style.display="none";
      	sezione.style.display="none";
      	image.src="../../img/chiuso.gif";}
	}  

	function ripristina(){
		var sezione = document.getElementById("TBL0");
		var sezioneTitolo = document.getElementById("titoloSezione");
		var image = document.getElementById("IMG0");
	   	sezione.style.display="none";
	   	sezioneTitolo.style.display="none";
	   	image.src="../../img/chiuso.gif";
   	}

	function settaFlag297() {
		var codificaTipologiaPatto = document.form1.codCodificaPatto.value;
		var indiceP = 0;
		for (indiceP=0; indiceP<arrayCodificaPatto.length ;indiceP++) {
			if (arrayCodificaPatto[indiceP] == codificaTipologiaPatto) {
				flagPatto297 = arrayFlgPatto297[indiceP];
				document.form1.flgPatto297.value = flagPatto297;
			}
		}
		return true;
	}
	
	function controllaAccettazionePtOnline(value){
		var valueSiNo = document.form1.PattiOnLine.options[document.form1.PattiOnLine.selectedIndex].text;
		if(value!=='' && valueSiNo!=='Si'){
			document.form1.PattiOnLine.options[1].selected = true;
		}else if(value ===''){
			document.form1.PattiOnLine.options[0].selected = true;
		}
	}
	
	function controllaPtOnline(value){
 		if(value==='No'){
			document.form1.codAccOnLine.options[0].selected = true;
		} 
	}
	
</script>
<style>
table.sezione2 {
	border-collapse:collapse;
	font-family: Verdana,"Times New Roman", Arial, Sans-serif; 
	font-size: 12px;
	font-weight: bold;
	border-bottom-width: 2px; 
	border-bottom-style: solid;
	border-bottom-color: #000080;
	color:#000080; 
	position: relative;
	left:1%;
	width: 94%;
	text-align: left;
	text-decoration: none;	
}
</STYLE>

</head>

<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca Patto/Accordo Lavoratori</p>
<p align="center">
  <af:form  name="form1" action="AdapterHTTP?PAGE=ListaPattoLavPage" method="POST" onSubmit="controllaFiltriRicerca() && controlloDate() && valorizzaHidden()">        	
  <%out.print(htmlStreamTop);%>
  <table>    
  <tr><td colspan="2"/>&nbsp;</td></tr>
  <tr>
    <td class="etichetta">Codice Fiscale</td>
    <td class="campo"><af:textBox type="text" name="CF" value="<%=CF%>" size="20" maxlength="16"/></td>
  </tr>
  <tr>
    <td class="etichetta">Cognome</td>
    <td><af:textBox type="text" name="COGNOME" value="<%=COGNOME%>" size="20" maxlength="50" /></td>
  </tr>
  <tr>
    <td class="etichetta">Nome</td>
    <td class="campo"><af:textBox type="text" name="NOME" value="<%=NOME%>" size="20" maxlength="50" /></td>
  </tr>
  <tr>
    <td class="etichetta">tipo ricerca</td>
    <td class="campo">
    <table colspacing="0" colpadding="0" border="0">
    <tr>
     <td><input type="radio" name="tipoRicerca" value="esatta" CHECKED/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
     <td><input type="radio" name="tipoRicerca" value="iniziaPer"/> inizia per</td>
    </tr>
    </table>
    </td>
  </tr>
  
  <tr><td colspan="2"><hr width="90%"/></td></tr>
  <tr>
  <td class="etichetta">Tipologia&nbsp;</td> 
    <td>
      <input type="hidden" name="flgPatto297" value="<%=flgPatto297%>" size="10" maxlength="10">
      <af:comboBox name="codCodificaPatto" classNameBase="input"  addBlank="true" title="Tipologia" 
      	moduleName="M_GetCodificaTipoPatto" selectedValue="<%=codCodificaPatto%>" onChange="settaFlag297();" />
    </td>
  </tr>
  <tr>
    <td class=etichetta2>Obiettivi del patto&nbsp;</td>
    <td nowrap>
        <af:comboBox name="codTipoPatto" classNameBase="input" addBlank="true" title="Tipologia" 
            selectedValue="<%=codTipoPatto%>" moduleName="M_GETDETIPOPATTO" />
    </td>
  </tr>
  <tr>
      <td class="etichetta">Atti aperti</td>
    	 <td class="campo">
        	 <af:comboBox classNameBase="input" name="AttiAperti" onChange="visualizza(this.value)" addBlank="true" selectedValue="<%=user.getCodRif()%>" > 
            	<option value="Si" selected>Si</option>
            	<option value="No">No</option> 
        	 </af:comboBox>
    	</td>
  </tr>
  <%if(isPattoOnLine){ %>
   <tr>
      <td class="etichetta">Patto/Accordo stipulato con modalità online</td>
    	 <td class="campo">
        	 <af:comboBox classNameBase="input" name="PattiOnLine" onChange="controllaPtOnline(this.value);" addBlank="true" selectedValue="<%=pattiOnLine%>" > 
            	<option value="Si">Si</option>
            	<option value="No">No</option> 
        	 </af:comboBox>&nbsp;&nbsp; Stato Accettazione&nbsp;&nbsp;
         	<af:comboBox name="codAccOnLine" classNameBase="input" addBlank="true" title="Stato Accettazione Patto On Line" onChange="controllaAccettazionePtOnline(this.value);"
            selectedValue="<%=codTipoPatto%>" moduleName="M_CODMONOACCETTAZIONE_PTOnLine" />
    	</td>
  </tr>
  <%}else{ %>
  	<input type="hidden" value="" name="PattiOnLine" />
  	<input type="hidden" value="" name="codAccOnLine" />
  <%}  %>
  <tr>
    <td class="etichetta">Data Stipula da</td>
    <td class="campo">
      <af:textBox type="date" name="datInizioDa" title="Data Stipula da" value="<%=datInizioDa%>" size="12" maxlength="10"  validateOnPost="true" />
      a&nbsp;&nbsp;<af:textBox type="date" name="datInizioA" title="Data Stipula a" value="<%=datInizioA%>" size="12" maxlength="10"  validateOnPost="true" />
    </td>
  <tr>
  <tr>
    <td class="etichetta">Data Scadenza Conferma da</td>
    <td class="campo">
      <af:textBox type="date" name="scadconfda" title="Data Scadenza Conferma da" value="<%=scadconfda%>" size="12" maxlength="10"   validateOnPost="true"  />
      a&nbsp;&nbsp;<af:textBox type="date" name="scadconfa" title="Data Scadenza Conferma a" value="<%=scadconfa%>" size="12" maxlength="10"   validateOnPost="true"  />
    </td>
  </tr>
  <tr><td colspan="2"><hr width="90%"/></td></tr>
  <tr>
    <td class="etichetta">Centro per l'Impiego competente</td>
    <td class="campo"><af:comboBox name="CodCPI" title="Centro per l'Impiego competente" addBlank="true" moduleName="M_ELENCOCPI" selectedValue="<%=CodCPI%>" required="true"/></td>
  </tr>
  
	<tr><td colspan=2>&nbsp;</td></tr>
	
	<tr>
    <td colspan=2>    
        <table id="titoloSezione" class="sezione2" style='display:<%=visualizza_display%>'>
            <tr>
                <td width='1%'>
                    <img id='IMG0' src='<%=imgChiudi %>' onclick='cambia(this, document.getElementById("TBL0"))'></td>
                        <td width='9%' nowrap class='titolo_sezione'>Dati Chiusura dell' atto</td>
            <td align='right' width='90%'></td>
            </tr>
        </table>
    </td>
 	</tr>
 
	<tr><td colspan=2>&nbsp;</td></tr>	
	<tr>
 		<td colspan=2>
 		<table id='TBL0' style='display:<%=visualizza_display%>'>  
 			<script>new Sezione(document.getElementById('TBL0'),document.getElementById('IMG0'),false);</script>
     		<tr>
             	<td class="etichetta">Data fine atto da</td>
             	<td class="campo">
               	<af:textBox title="Data fine atto da" type="date" name="dataFineAttoDa" size="12" value="<%=dataFineAttoDa%>" maxlength="10" validateOnPost="true"/>
               	&nbsp;&nbsp;a&nbsp;&nbsp;
	  			<af:textBox type="date" title="Data fine atto a" name="dataFineAttoA" value="<%=dataFineAttoA%>" size="12" maxlength="10" validateOnPost="true" />
			</td>
         	</tr>
       		<tr>
	       		<td class="etichetta">Motivo fine atto</td>
	            <td class="campo">
	            <af:comboBox name="MotivoFine" title="Motivo fine atto" moduleName="M_MotFineAtto" classNameBase ="input" selectedValue="<%=MotivoFine%>" addBlank="true" onChange="cambiaMotivo()"/>
	           	</td>
           	</tr>
    	</table>
    	</td>
  </tr>
  	<tr><td colspan=2>&nbsp;</td></tr>
	<%if (numConfigPA.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {
		String numPattiGG = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numPattiGG")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numPattiGG").toString():"0";
		String numPattiOver30 = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numPattiOver30")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numPattiOver30").toString():"0";
		String numPattiOver45 = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numPattiOver45")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numPattiOver45").toString():"0";
		String numPattiMinAt = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numPattiMinat")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numPattiMinat").toString():"0";
		String numPattiGGU = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numPattiGGU")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numPattiGGU").toString():"0";	
		String numC06MGG = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC06MGG")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC06MGG").toString():"0";
		String numC07MGG = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC07MGG")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC07MGG").toString():"0";
		String numC06MGO30 = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC06MGO30")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC06MGO30").toString():"0";
		String numC07MGO30 = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC07MGO30")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC07MGO30").toString():"0";
		String numC06MGO45 = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC06MGO45")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC06MGO45").toString():"0";
		String numC07MGO45 = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC07MGO45")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC07MGO45").toString():"0";
		String numC06MINAT = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC06MINAT")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numC06MINAT").toString():"0";
				
		String numIscrittiL6899Disabili = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numIscrittiL6899Disabili")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numIscrittiL6899Disabili").toString():"0";
		String numIscrittiEta45 = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numIscrittiEta45")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numIscrittiEta45").toString():"0";
		String numIscritti45AnniMin24mesi = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numIscritti45AnniMin24mesi")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numIscritti45AnniMin24mesi").toString():"0";
		String numIscritti45AnniMag24mesi = serviceResponse.containsAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numIscritti45AnniMag24mesi")?
				serviceResponse.getAttribute("M_GET_DATI_STATISTICI_PATTO.ROWS.ROW.numIscritti45AnniMag24mesi").toString():"0";
	%>
	<tr>
	    <td colspan=2>    
	        <table id="titoloSezionePA" class="sezione2" style="display:inline">
	            <tr>
	                <td width='1%'>
	                    <img id='IMGPA1' src='<%=imgChiudi %>' onclick='cambia(this, document.getElementById("TBLPA1"))'></td>
	                        <td width='9%' nowrap class='titolo_sezione'>Pacchetto Adulti: Azioni e Patti</td>
	            <td align='right' width='90%'></td>
	            </tr>
	        </table>
	    </td>
	 </tr>
	 <tr>
	 		<td colspan=2>
	 		<table id='TBLPA1' style="display:inline">  
	 			<script>new Sezione(document.getElementById('TBLPA'),document.getElementById('IMGPA'),false);</script>
	     		<tr>
	             	<td class="etichetta" nowrap>Num. Patti GG</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numPattiGG" value="<%=numPattiGG%>" size="5" title="Num. Patti GG" readonly="true"/>
					</td>
					<td class="etichetta" nowrap>Num. Patti L68/99 e Over 45 disoccupati LD</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numPattiOver45" value="<%=numPattiOver45%>" size="5" title="Num. Patti Over 45" readonly="true"/>
					</td>
				</tr>
				<tr>
					<td class="etichetta" nowrap>Num. Patti Over 30</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numPattiOver30" value="<%=numPattiOver30%>" size="5" title="Num. Patti Over 30" readonly="true"/>
					</td>
					<td class="etichetta" nowrap>Num. Patti Inclusione Attiva</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numPattiMinAt" value="<%=numPattiMinAt%>" size="5" title="Num. Patti Inclusione Attiva" readonly="true"/>
					</td>	
	         	</tr>
	         	<tr>
					<td class="etichetta" nowrap>Num. Patti GGU</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numPattiGGU" value="<%=numPattiGGU%>" size="5" title="Num. Patti GGU" readonly="true"/>
					</td>
	         	</tr>
	         	<tr><td colspan=4>&nbsp;</td></tr>
	         	<tr>
	         		<td class="etichetta" nowrap>Num. C06 in Patti GG</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numC06MGG" value="<%=numC06MGG%>" size="5" title="Num. C06 in Patti GG" readonly="true"/>
					</td>
					<td class="etichetta" nowrap>Num. C06 in Patti L68/99 e Over 45 disoccupati LD</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numC06MGO45" value="<%=numC06MGO45%>" size="5" title="Num. C06 in Patti Over 45" readonly="true"/>
					</td>
				</tr>
				<tr>
					<td class="etichetta" nowrap>Num. C06 in Patti Over 30</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numC06MGO30" value="<%=numC06MGO30%>" size="5" title="Num. C06 in Patti Over 30" readonly="true"/>
					</td>
					<td class="etichetta" nowrap>Num. C06 in Inclusione Attiva</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numC06MINAT" value="<%=numC06MINAT%>" size="5" title="Num. C06 in Inclusione Attiva" readonly="true"/>
					</td>
				</tr>
				<tr><td colspan=2>&nbsp;</td></tr>
				<tr>
					<td class="etichetta" nowrap>Num. C07 in Patti GG</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numC07MGG" value="<%=numC07MGG%>" size="5" title="Num. C07 in Patti GG" readonly="true"/>
					</td>
					<td class="etichetta" nowrap>Num. C07 in Patti L68/99 e Over 45 disoccupati LD</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numC07MGO45" value="<%=numC07MGO45%>" size="5" title="Num. C07 in Patti Over 45" readonly="true"/>
					</td>
	         	</tr>
	         	<tr>
	         		<td class="etichetta" nowrap>Num. C07 in Patti Over 30</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numC07MGO30" value="<%=numC07MGO30%>" size="5" title="Num. C07 in Patti Over 30" readonly="true"/>
					</td>
	         	</tr>
	    	</table>
	    	</td>
	 </tr>
	 <tr><td colspan=2>&nbsp;</td></tr>
	 <tr>
	    <td colspan=2>    
	        <table id="titoloSezionePA" class="sezione2" style="display:inline">
	            <tr>
	                <td width='1%'>
	                    <img id='IMGPA2' src='<%=imgChiudi %>' onclick='cambia(this, document.getElementById("TBLPA2"))'></td>
	                        <td width='9%' nowrap class='titolo_sezione'>Pacchetto Adulti: Adesioni per età e anzianità DID</td>
	            <td align='right' width='90%'></td>
	            </tr>
	        </table>
	    </td>
	 </tr>
	 <tr>
	 		<td colspan=2>
	 		<table id='TBLPA2' style="display:inline">  
	 			<script>new Sezione(document.getElementById('TBLPA'),document.getElementById('IMGPA'),false);</script>
	     		<tr>
	             	<td class="etichetta" nowrap>Num. Iscritti L68/99 Disabili</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numIscrittiL6899Disabili" value="<%=numIscrittiL6899Disabili%>" size="5" title="Num. Iscritti L68/99 Disabili" readonly="true"/>
					</td>
					<td class="etichetta" nowrap>Num. Iscritti età &lt; 45 anni</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numIscrittiEta45" value="<%=numIscrittiEta45%>" size="5" title="Num. Iscritti età &lt; 45 anni" readonly="true"/>
					</td>
				</tr>
				<tr>
					<td class="etichetta" nowrap>Num. Iscritti età &gt;= 45 anni con DID &lt;24 mesi</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numIscritti45AnniMin24mesi" value="<%=numIscritti45AnniMin24mesi%>" size="5" title="Num. Iscritti età &gt;= 45 anni con DID &lt;24 mesi" readonly="true"/>
					</td>
					<td class="etichetta" nowrap>Num. Iscritti età &gt;= 45 anni con DID &gt;=24 mesi</td>
	             	<td class="campo">
	               	<af:textBox type="text" classNameBase="input" name="numIscritti45AnniMag24mesi" value="<%=numIscritti45AnniMag24mesi%>" size="5" title="Num. Iscritti età &gt;= 45 anni con DID &gt;=24 mesi" readonly="true"/>
					</td>	
	         	</tr>
	    	</table>
	    	</td>
	 </tr>
	<%}%>
  
  <input  type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>" size="22" maxlength="16">
  <input  type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" size="22" maxlength="16">
  <input  type="hidden" name="descFlgPatto297_H" value=""/>  
  <input  type="hidden" name="descCodTipoPatto_H" value=""/>
  <input  type="hidden" name="descCPI_H" value=""/> 
  <input  type="hidden" name="motSelected_H" value=""/> 
    
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr>
    <td colspan="2" align="center">
      <input name="Invia" type="submit" class="pulsanti" value="Cerca">
      &nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla" onclick="ripristina()">
    </td>
    </tr>
  </table>
  <%out.print(htmlStreamBottom);%>  
  </af:form>
</p>
<table>
<tr><td class="campo2">N.B.: Se il campo "Atti aperti" è nullo vengono filtrati sia gli atti aperti che quelli chiusi.</td></tr>
</table>

</body>
</html>
