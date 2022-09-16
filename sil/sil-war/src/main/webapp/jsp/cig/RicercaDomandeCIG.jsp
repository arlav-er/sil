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

<%
  String _page=SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

  ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

  if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }

  String _funzione=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");

  SourceBean row = null;
  
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<%@page import="it.eng.afExt.utils.SourceBeanUtils"%>
<html>
<head>
<title>Ricerca Iscrizioni CIG</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<%@ include file="RicercaSoggettoCIG.inc" %>

<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

<script language="JavaScript">

  function checkobjDate(objDataDa, objDataA) {
	if ((objDataDa.value != "") && (objDataA.value != "")) {
		if (compareDate(objDataDa.value,objDataA.value) > 0) {
	    	alert(objDataDa.title + " maggiore di " + objDataA.title);
	      	objDataDa.focus();
		    return false;
		}	
	}
	return true;
  }

  function controlloDate(){
    if (!checkobjDate(document.form1.datinizioda, document.form1.datinizioa)) return false;
    
	if (!checkobjDate(document.form1.datfineda, document.form1.datfinea)) return false;

	if (!checkobjDate(document.form1.datcompetenzada, document.form1.datcompetenzaa)) return false;    
    
	if (!checkobjDate(document.form1.datchiusuraiscrda, document.form1.datchiusuraiscra)) return false;

    return true;
  }

  function aggiornaAzienda(){
        document.form1.ragioneSociale.value = opened.dati.ragioneSociale;
        document.form1.pIva.value = opened.dati.partitaIva;
        document.form1.codFiscaleAzienda.value = opened.dati.codiceFiscaleAzienda;
        document.form1.PRGAZIENDA.value = opened.dati.prgAzienda;
        document.form1.codTipoAz.value = opened.dati.codTipoAz;
        document.form1.descrTipoAz.value = opened.dati.descrTipoAz;
        document.form1.FLGDATIOK.value = opened.dati.FLGDATIOK;
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
        
    }
    
	function gestioneAggiuntivaSuReset() {
		//chiudo le tendine Az e Lav 
		var imgA = document.getElementById("tendinaAzienda");
		var imgL = document.getElementById("tendinaLav");
		cambiaLavMC("aziendaSez","none");
		cambiaLavMC("lavoratoreSez","none");
		imgA.src=imgChiusa; 
		imgL.src=imgChiusa;
		//svuoto la combo dei motivi chiusura
		while (document.form1.codMotChiusuraIscr.options.length>0) {
			document.form1.codMotChiusuraIscr.options[0]=null;
		}   
    }

	function aggiornaDescrizioni(){
		var descrmotchiusuraiscr = document.form1.descrmotchiusuraiscrhid;
		var descCPILav = document.form1.descrCPILavhid;
		var descrstato = document.form1.descrstatohid;
		var descCPIAz = document.form1.descrCPIAzhid;
		var descProvAz = document.form1.descrProvAzhid;
		var codMotChiusuraIscr = document.form1.codMotChiusuraIscr;
		var codCPILav = document.form1.CodCPILav;
		var codstato = document.form1.codstato;
		var codTipoIscr = document.form1.codTipoIscr;
		var codCPIAz = document.form1.CodCPIAz;
		var codProvAz = document.form1.codProvAz;
		
		if(codMotChiusuraIscr.value != ""){
			descrmotchiusuraiscr.value = codMotChiusuraIscr.options[codMotChiusuraIscr.selectedIndex].text;
		}
		if(codCPILav.value != ""){
			descCPILav.value = codCPILav.options[codCPILav.selectedIndex].text;	
		}	
		if(codstato.value != ""){
			descrstato.value = codstato.options[codstato.selectedIndex].text;	
		}

		var descrTipoIscr = "";
		for(var i=0; i<codTipoIscr.options.length;i++){
			if(codTipoIscr.options[i].selected){
				if(descrTipoIscr != ""){
					descrTipoIscr += ",";
				}
				descrTipoIscr += codTipoIscr.options[i].text;
			}
		}
		if(descrTipoIscr != ""){
			document.form1.descrTipoIscrhid.value = descrTipoIscr;	
		}
		
		if(codCPIAz.value != ""){
			descCPIAz.value = codCPIAz.options[codCPIAz.selectedIndex].text;	
		}
		
		if(codProvAz.value != ""){
			descProvAz.value = codProvAz.options[codProvAz.selectedIndex].text;	
		}		
		
		return true;
	} 
	
	var motiviChiusura=new Array();
	var motiviChiusura_TipoIscr=new Array();
	<%
		Vector motChiusRows=serviceResponse.getAttributeAsVector("CI_MOTIVO_CHIUSURA.ROWS.ROW");
		String descmotChius="";
		String codmotChius="";
		Iterator record=motChiusRows.iterator();
		while(record.hasNext()) {
			row= (SourceBean) record.next();
			codmotChius=StringUtils.getAttributeStrNotNull(row, "CODICE");
			String codTipoIscrMot = codmotChius.split("-")[0];
			descmotChius=StringUtils.getAttributeStrNotNull(row, "DESCRIZIONE");
			descmotChius=(descmotChius.length()>60)?descmotChius.substring(0,60) + "..." : descmotChius;
			out.print("motiviChiusura[\""+codmotChius+"\"]=\""+descmotChius+"\";\n");
			out.print("motiviChiusura_TipoIscr[\""+codmotChius+"\"]=\""+codTipoIscrMot+"\";\n");
		}
	
	%>    
    
    function gestisciMotChiusura(){
    	var j=1;
    	var addVoce = true;
 		//svuoto 
		while (document.form1.codMotChiusuraIscr.options.length>0) {
			document.form1.codMotChiusuraIscr.options[0]=null;
		}
    	for(var i=1; i<document.form1.codTipoIscr.options.length;i++){
			if(document.form1.codTipoIscr.options[i].selected){
				var codtipoiscr = document.form1.codTipoIscr.options[i].value;
				//riempio a meno di doppioni
				for (prop in motiviChiusura_TipoIscr) {
	     			if (motiviChiusura_TipoIscr[prop]==codtipoiscr) { 
	     				for(var k=1; k<document.form1.codMotChiusuraIscr.options.length;k++){ 
							if (document.form1.codMotChiusuraIscr.options[k].value == prop.split("-")[1] ) 
								addVoce = false;
						}
	    		     	if (addVoce) {
	    		     		document.form1.codMotChiusuraIscr.options[j]=new Option(motiviChiusura[prop], prop.split("-")[1] , false, false);      			
	    		     		j++;
	    		     	}
	    			}
	     		}
				addVoce = true;
			}
		}
	}


    function creaStringa(){
  		var tipoIscrStr = '';
 		var contSelezionati = 0;
 		for (i=0;i<document.form1.codTipoIscr.length;i++) {
   			if (document.form1.codTipoIscr[i].selected) {
   				contSelezionati = contSelezionati + 1;
  				if (tipoIscrStr == '') {
  					tipoIscrStr = tipoIscrStr + document.form1.codTipoIscr[i].value;
				}
				else {
					tipoIscrStr = tipoIscrStr + ',' + document.form1.codTipoIscr[i].value;
				}
		 	}
		}

		document.form1.tipoIscr.value=tipoIscrStr;
		return true;
}	   
</SCRIPT>

</head>


<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca altre iscrizioni</p>
<p align="center">
  <af:form name="form1" action="AdapterHTTP" method="POST" onSubmit="controlloDate() && aggiornaDescrizioni() && creaStringa() "> 
  <%out.print(htmlStreamTop);%> 

  <table class="main" border="0">
   
<!-- sezione lavoratore -->
	<tr class="note">
	    <td colspan="2">
	      <div class="sezione2">
	          <img id='tendinaLav' alt='Chiudi' src='../../img/aperto.gif' onclick="cambiaTendina(this,'lavoratoreSez', document.form1.codiceFiscaleLavoratore);" />&nbsp;&nbsp;&nbsp;Lavoratore
	      &nbsp;&nbsp;
	      <a href="#" onClick="javascript:apriSelezionaSoggetto('Lavoratori', 'aggiornaLavoratore')"><img src="../../img/binocolo.gif" alt="Cerca"></a>     
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
	              <af:textBox classNameBase="input" type="text" name="codiceFiscaleLavoratore" readonly="true" size="30" maxlength="16"/>
	              &nbsp;&nbsp;&nbsp;Validità C.F.&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGCFOK" readonly="true" size="3" maxlength="3"/>
	            </td>
	          </tr>
	          <tr >
	            <td class="etichetta">Cognome</td>
	            <td class="campo">
	              <af:textBox classNameBase="input" type="text" name="cognome" readonly="true" size="30" maxlength="50"/>
	            </td>
	          </tr>
	          <tr>
	            <td class="etichetta">Nome</td>
	            <td class="campo">
	              <af:textBox classNameBase="input" type="text" name="nome" readonly="true" size="30" maxlength="50"/>
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
			onclick="cambiaTendina(this,'aziendaSez',document.form1.codFiscaleAzienda);" />&nbsp;&nbsp;&nbsp;Azienda
		&nbsp;&nbsp; 
		<a href="#"
			onClick="javascript:apriSelezionaSoggetto('Testate', 'aggiornaAzienda','','','');"><img
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
						size="30" maxlength="16" /></td>
				</tr>
				<tr>
					<td class="etichetta">Partita IVA</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="pIva" readonly="true" size="30"
						maxlength="11" /> &nbsp;&nbsp;&nbsp;Validità C.F./P.
					IVA&nbsp;&nbsp;<af:textBox classNameBase="input" type="text"
						name="FLGDATIOK" readonly="true" 
						size="3" maxlength="3" /></td>
				</tr>
				<tr>
					<td class="etichetta">Ragione Sociale</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="ragioneSociale" readonly="true" 
						size="60" maxlength="100" /></td>
				</tr>
				<tr>
					<td class="etichetta">Tipo Azienda</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="descrTipoAz" readonly="true" 
						size="30" maxlength="30" /> <af:textBox classNameBase="input"
						type="hidden" name="codTipoAz" readonly="true"
						size="10" maxlength="10" /> <af:textBox
						classNameBase="input" type="hidden" name="CODNATGIURIDICA"
						readonly="true" size="10" maxlength="10" />
					</td>
				</tr>
		</table>
		</div>
		</td>
	</tr>
	<tr><td>&nbsp;</td><tr>
    <tr><td colspan="2" ><div class="sezione2"/>Dati Iscrizione</td></tr>
   
    <tr>
		<td class="etichetta" nowrap>Data inizio da</td>
		<td class="campo" >
   		<af:textBox type="date" name="datinizioda" title="Data inizio da" validateOnPost="true" size="10" maxlength="10"/>
		&nbsp;&nbsp;a&nbsp;<af:textBox type="date" name="datinizioa" title="Data inizio a" validateOnPost="true" size="10" maxlength="10"/>
		</td>            
 	</tr>
   	
   	<tr>
	 	<td class="etichetta" nowrap>Data fine da</td>
	 	<td class="campo" >
	    	<af:textBox type="date" name="datfineda" title="Data fine da" validateOnPost="true" size="10" maxlength="10"/>
	    	&nbsp;&nbsp;a&nbsp;<af:textBox type="date" name="datfinea" title="Data fine a" validateOnPost="true" size="10" maxlength="10"/>
	 	</td>            
   	</tr>
   	
   	<tr> 
		<td class="etichetta">Stato iscrizione</td>
	    <td class="campo">
		<af:comboBox classNameBase="input" name="codstato" title="Stato iscrizione" moduleName="CI_STATO_ALTRA_ISCR" 
		addBlank="true">
			<option value="VA">Valido</option>
		</af:comboBox>
		<input name="descrstatohid" type="hidden" value=""/>     
	    </td>
	</tr>
	
    <tr>
	 	<td class="etichetta" nowrap>Data competenza da</td>
	 	<td class="campo" >
	    	<af:textBox type="date" name="datcompetenzada" title="Data competenza da" validateOnPost="true" size="10" maxlength="10"/>
	    	&nbsp;&nbsp;a&nbsp;<af:textBox type="date" name="datcompetenzaa" title="Data competenza a" validateOnPost="true" size="10" maxlength="10"/>
	 	</td>            
   	</tr>	
   	
	<tr> 
		<td class="etichetta">Motivo chiusura iscrizione</td>
	    <td class="campo">
		<af:comboBox classNameBase="input" name="codMotChiusuraIscr" title="Motivo chiusura iscrizione" addBlank="true"/>            
		<input name="descrmotchiusuraiscrhid" type="hidden" value=""/>     
	    </td>
	</tr> 
	
   	<tr>
	 	<td class="etichetta" nowrap>Data chiusura iscrizione da</td>
	 	<td class="campo" >
	    	<af:textBox type="date" name="datchiusuraiscrda" title="Data chiusura iscrizione da" validateOnPost="true" size="10" maxlength="10"/>
	    	&nbsp;&nbsp;a&nbsp;<af:textBox type="date" name="datchiusuraiscra" title="Data chiusura iscrizione a" validateOnPost="true" size="10" maxlength="10"/>
	 	</td>            
   	</tr>
   	
 	<tr>
		<td class="etichetta">CPI competente per il lavoratore</td>
		<td class="campo">
			<af:comboBox classNameBase="input" title="CPI competente per il lavoratore" name="CodCPILav" 
				moduleName="M_ELENCOCPI" addBlank="true" />
			<input name="descrCPILavhid" type="hidden" value=""/>
		</td>
	</tr> 
	
 	<tr>
		<td class="etichetta">Competenza non amministrativa</td>
		<td class="campo">
			<input type="checkbox" name="compNonAmm" />
		</td>
	</tr>	
	
  	<tr>
	    <td class="etichetta">Tipo iscrizione</td>
	    <td class="campo">
	    	<af:comboBox classNameBase="input" title="Tipo iscrizione" name="codTipoIscr" 
				moduleName="CI_TIPO_ISCR" addBlank="true" onChange="gestisciMotChiusura();" multiple="true" />
			<input name="descrTipoIscrhid" type="hidden" value=""/>
			<input name="tipoIscr" type="hidden" value=""/>  
	    </td>
	<tr>
		
 	<tr>
		<td class="etichetta">CPI competente per l'azienda</td>
		<td class="campo">
			<af:comboBox classNameBase="input" title="CPI competente per l'azienda" name="CodCPIAz" 
				moduleName="M_ELENCOCPI" addBlank="true" />
			<input name="descrCPIAzhid" type="hidden" value=""/>
		</td>
	</tr>	
	
	<tr>
		<td class="etichetta">Provincia per l'azienda</td>
		<td class="campo">
			<af:comboBox classNameBase="input" title="Provincia per l'azienda" name="codProvAz" 
				addBlank="true" moduleName="M_GetIDOProvince"/>
			<input name="descrProvAzhid" type="hidden" value=""/>
	    </td>
	<tr>
		
	<tr>
		<td class="etichetta">Codice domanda </td><td class="campo">
		<af:textBox  name="codAccordo" type="text" size="20" maxlength="20"/>
		(Finisce con)
		</td>
	</tr>
	
			
	<tr><td>&nbsp;</td><tr>
    <tr><td colspan="2" ><div class="sezione2"/>Ordinamento</td></tr>
   
    <tr>
		<td class="etichetta">
			Codice Fiscale Lavoratore<input type="checkbox" name="cbCfLav"/>
		</td>
		<td class="campo" >
			Crescente<input type="radio" name="radioCfLav" value="ASC" />&nbsp;&nbsp;&nbsp;
	    	Decrescente<input type="radio" name="radioCfLav" value="DESC" checked/> 
	    </td>            
 	</tr>
    <tr>
		<td class="etichetta">
			Cognome e Nome Lavoratore<input type="checkbox" name="cbCogNomLav"/>
		</td>
		<td class="campo" >
			Crescente<input type="radio" name="radioCogNomLav" value="ASC" />&nbsp;&nbsp;&nbsp;
	    	Decrescente<input type="radio" name="radioCogNomLav" value="DESC" checked/> 
	    </td>            
 	</tr>
    <tr>
		<td class="etichetta">
			Data Iscrizione<input type="checkbox" name="cbDatIscr" checked/>
		</td>
		<td class="campo" >
			Crescente<input type="radio" name="radioDatIscr" value="ASC" />&nbsp;&nbsp;&nbsp;
	    	Decrescente<input type="radio" name="radioDatIscr" value="DESC" checked/> 
	    </td>            
 	</tr>
     <tr>
		<td class="etichetta">
			Tipo Iscrizione<input type="checkbox" name="cbTipoIscr"/>
		</td>
		<td class="campo" >
			Crescente<input type="radio" name="radioTipoIscr" value="ASC" />&nbsp;&nbsp;&nbsp;
	    	Decrescente<input type="radio" name="radioTipoIscr" value="DESC" checked/> 
	    </td>            
 	</tr>
 	<tr>
		<td class="etichetta">
			Comune Residenza<input type="checkbox" name="cbComuneRes"/>
		</td>
		<td class="campo" >
			Crescente<input type="radio" name="radioComuneRes" value="ASC" />&nbsp;&nbsp;&nbsp;
	    	Decrescente<input type="radio" name="radioComuneRes" value="DESC" checked/> 
	    </td>            
 	</tr>
	<tr><td>&nbsp;</td></tr>
   	<tr><td>&nbsp;</td></tr>
   	<tr>
   		<td colspan="2" align="center">
      		<input class="pulsante" type="submit" name="cerca" value="Cerca"/>
      		&nbsp;&nbsp;
      	<input name="reset" type="reset" class="pulsanti" value="Annulla" onClick="gestioneAggiuntivaSuReset();"/> 
    	</td>
   	</tr>

   	<input type="hidden" name="BACKPAGE" value="<%=_page%>"/>
   	<input type="hidden" name="PAGE" value="CigListaPage"/>
   	<input type="hidden" name="cdnfunzione" value="<%=_funzione%>"/>
	
	<input type="hidden" name="CDNLAVORATORE" value="" />
   	<input type="hidden" name="PRGAZIENDA" value="" />

</table>
</af:form>
</p>
<%out.print(htmlStreamBottom);%>

</body>
</html>
