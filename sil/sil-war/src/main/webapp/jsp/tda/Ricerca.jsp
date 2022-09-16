<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String _page = (String) serviceRequest.getAttribute("PAGE");
String _funzione = (String) serviceRequest.getAttribute("cdnFunzione");
// NOTE: Attributi della pagina (pulsanti e link) 
PageAttribs attributi = new PageAttribs(user, _page);

ProfileDataFilter filter = new ProfileDataFilter(user, _page);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}

String strCodiceFiscale 	= StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
String strCognome       	= StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
String strNome          	= StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
String strCodAttivazione	= StringUtils.getAttributeStrNotNull(serviceRequest,"strCodAttivazione");
String tipoRicerca      	= StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
String statoTDA        		= StringUtils.getAttributeStrNotNull(serviceRequest,"statoTDA");
String dataStatoDa			= StringUtils.getAttributeStrNotNull(serviceRequest, "dataStatoDa");  
String dataStatoA			= StringUtils.getAttributeStrNotNull(serviceRequest, "dataStatoA"); 
String azioneTDA        	= StringUtils.getAttributeStrNotNull(serviceRequest,"azioneTDA");
String codCPI 				= StringUtils.getAttributeStrNotNull(serviceRequest,"codCPI");
String descrCPI 			= StringUtils.getAttributeStrNotNull(serviceRequest,"descrCodCpiSel");
String descrCFSA 			= StringUtils.getAttributeStrNotNull(serviceRequest,"descrCFSA");
String descrSedeSA			= StringUtils.getAttributeStrNotNull(serviceRequest,"descrSedeSA");
String descrStato			= StringUtils.getAttributeStrNotNull(serviceRequest,"descrStato");
String descrPagamento		= StringUtils.getAttributeStrNotNull(serviceRequest,"descrPagamento");
String descrAnnull			= StringUtils.getAttributeStrNotNull(serviceRequest,"descrAnnull");
String descrAzione			= StringUtils.getAttributeStrNotNull(serviceRequest,"descrAzione");

String codAnnullTDA			= StringUtils.getAttributeStrNotNull(serviceRequest,"codAnnullTDA");
String statoPagamentoTDA	= StringUtils.getAttributeStrNotNull(serviceRequest,"statoPagamentoTDA");
String assegnatiScaduti		= StringUtils.getAttributeStrNotNull(serviceRequest,"assegnatiScaduti");
String attivatiScaduti		= StringUtils.getAttributeStrNotNull(serviceRequest,"attivatiScaduti");
String sedeEnteCollegato  	= StringUtils.getAttributeStrNotNull(serviceRequest,"sedeEnteAtt");


String tipoGruppoCollegato = user.getCodTipo();
String cfEnteCollegato = "";

if (tipoGruppoCollegato.equalsIgnoreCase(it.eng.sil.module.voucher.Properties.SOGGETTO_ACCREDITATO)) {
	cfEnteCollegato = user.getCfUtenteCollegato();
}

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
	<title>Ricerca TDA</title>
   	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
   	<af:linkScript path="../../js/" />

  	<script language="Javascript">
   	<% 
    	//Genera il Javascript che si occuperà di inserire i links nel footer
   		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  	%>
  	var tipoGruppoUtCollegato = '<%=tipoGruppoCollegato%>';
  	  	
  	function checkSedeEnte() {
     	if (tipoGruppoUtCollegato != 'S') {
      		if (document.Frm1.sedeEnteAtt.value != "") {
      			document.Frm1.cfEnteAtt.value = "";
      		}
      	}
      	return true;
    }
  	
  	function toggleData(isRicerca) {
  		if(isRicerca && document.Frm1.dataStatoDa.value=="" && document.Frm1.dataStatoA.value==""){
  			document.Frm1.dataStatoDa.disabled='true';
     		document.Frm1.dataStatoA.disabled='true';
     		
  		}else if(!isRicerca){
			document.Frm1.dataStatoDa.value="";
	   		document.Frm1.dataStatoA.value="";
  			if (document.Frm1.statoTDA.value != "") {
  	     		document.Frm1.dataStatoDa.disabled='';
  	     		document.Frm1.dataStatoA.disabled='';
  	     		document.Frm1.attivatiScaduti.checked=false;
   	  			document.Frm1.assegnatiScaduti.checked=false; 
  	        }else{
  	    		document.Frm1.dataStatoDa.disabled='true';
  	     		document.Frm1.dataStatoA.disabled='true';
  	    	}
  		}
     }
    function compDate(data1,data2) {
    	// Metodo che permette il confronto fra due date
    		
    		if (data1!="" && data2!="") {
    		   var y1,y2,m1,m2,d1,d2;
    		   var tmpdata1,tmpdata2;
    		     d1=data1.substring(0,2);
    		     d2=data2.substring(0,2);
    		     m1=data1.substring(3,5);
    		     m2=data2.substring(3,5);
    		     y1=data1.substring(6);
    		     y2=data2.substring(6);
    		  
    		   tmpdata1=y1+m1+d1;
    		   tmpdata2=y2+m2+d2;
    		  
    		   if(tmpdata1>tmpdata2)
    		      return 1;
    			// Ritorna 1 se la data1 e maggiore di data 2      
    		   if(tmpdata2>tmpdata1)   
    		      return 2;
    		      // Ritorna 2 se la data2 e maggiore di data 1
    		   if(tmpdata1==tmpdata2)   
    		      return 0;
    		      // Ritorna 0 se la data1 e uguale a data 2
    		}
    		return 0;
    	}
  	function controllaDate() {

		ok=true;
		
		var objDataStatoDa=eval("document.forms[0].dataStatoDa");      	
      	var objDataStatoA=eval("document.forms[0].dataStatoA");
     
		dataStatoDa=objDataStatoDa.value;
		dataStatoA=objDataStatoA.value;
 

		if (compDate(dataStatoDa, dataStatoA)==1) {//DA maggiore di A
			alert("Data stato \'da\' maggiore della data stato \'a\'");
			objDataStatoDa.focus();
			ok=false;
		}
		 
  		return ok;
      
      }
  	
  	 function handleChange(oggetto){
  		 if(oggetto.name.includes("assegnati")){
  			document.Frm1.attivatiScaduti.checked=false;
  		 }else if(oggetto.name.includes("attivati")){
  			document.Frm1.assegnatiScaduti.checked=false; 
  		 }
  		document.Frm1.statoTDA.value="";
  		document.Frm1.dataStatoDa.value="";
 		document.Frm1.dataStatoA.value="";
 		toggleData("checkbox");
  	 }

		function valorizzaDescrizione(oggetto) {

			var descrCampo = oggetto.options[oggetto.selectedIndex].text;
			if (oggetto.name.includes("codCPI")) {
				document.Frm1.descrCodCpiSel.value = descrCampo;
			}
			if (oggetto.name.includes("cfEnteAtt")) {
				document.Frm1.descrCFSA.value = descrCampo;
			}
			if (oggetto.name.includes("sedeEnteAtt")) {
				document.Frm1.descrSedeSA.value = descrCampo;
			}
			if (oggetto.name.includes("statoTDA")) {
				document.Frm1.descrStato.value = descrCampo;
			}
			if (oggetto.name.includes("statoPagamentoTDA")) {
				document.Frm1.descrPagamento.value = descrCampo;
			}
			if (oggetto.name.includes("codAnnullTDA")) {
				document.Frm1.descrAnnull.value = descrCampo;
			}
			if (oggetto.name.includes("azioneTDA")) {
				document.Frm1.descrAzione.value = descrCampo;
			}
		}

		function checkAnnullamento() {
			isOk = true;
			if (document.Frm1.codAnnullTDA.value != ""
					&& !(document.Frm1.statoTDA.value == "ANN" || document.Frm1.statoTDA.value == "")) {
				isOk = false;
				alert("Selezionando il Motivo di Annullamento il criterio \"Stato\" può essere solo nullo o Annullato");
			}
			return isOk;
		}

		function clearForm() {
			document.Frm1.reset();
		}
			</script>
</head>
    
<body class="gestione" onload="rinfresca();toggleData('onLoad')">
<p class="titolo">Ricerca Titoli D'acquisto</p>
<%out.print(htmlStreamTop);%>
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaDate() && checkSedeEnte() && checkAnnullamento()">
	<table class="main">
    <tr>
    	<td class="etichetta">Codice fiscale</td>
       	<td class="campo">
        	<af:textBox type="text" classNameBase="input" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="20" maxlength="16" title="Codice fiscale"/>
        </td>
   	</tr>
    <tr>
    	<td class="etichetta">Cognome</td>
        <td class="campo">
        	<af:textBox type="text" classNameBase="input" name="strCognome" value="<%=strCognome%>" size="20" maxlength="50" title="Cognome"/>
       	</td>
    </tr>
    <tr>
     	<td class="etichetta">Nome</td>
        <td class="campo">
        	<af:textBox type="text" classNameBase="input" name="strNome" value="<%=strNome%>" size="20" maxlength="50" title="Nome"/>
       	</td>
    </tr>
    <tr>
    	<td class="etichetta">tipo ricerca</td>
    	<td class="campo">
    	<table colspacing="0" colpadding="0" border="0">
         	<tr>
         	<%if (tipoRicerca.equalsIgnoreCase("iniziaPer")) {%>
         		<td><input type="radio" name="tipoRicerca" value="esatta"/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
	           	<td><input type="radio" name="tipoRicerca" value="iniziaPer" CHECKED /> inizia per</td>
	        <%} else {%>
	          	<td><input type="radio" name="tipoRicerca" value="esatta" CHECKED /> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
	          	<td><input type="radio" name="tipoRicerca" value="iniziaPer" /> inizia per</td> 	
	        <%}%>
         	</tr>
    	</table>
   		</td>
  	</tr>
  	 <tr>
    	<td class="etichetta">Codice attivazione</td>
       	<td class="campo">
        	<af:textBox type="text" classNameBase="input" name="strCodAttivazione" value="<%=strCodAttivazione%>" size="30" maxlength="20" title="Codice attivazione"/>
        </td>
   	</tr>
  	<tr>
    	<td class="etichetta">CF Soggetto Accreditato</td>
       	<td class="campo">
       		<%if (tipoGruppoCollegato.equalsIgnoreCase(it.eng.sil.module.voucher.Properties.SOGGETTO_ACCREDITATO)) {%>
       			<af:textBox type="text" classNameBase="input" name="cfEnteAtt" value="<%=cfEnteCollegato%>" size="20" maxlength="16" title="CF Soggetto Accreditato" readonly="true"/>
       		<%} else {%>
        		<af:comboBox classNameBase="input" title="Sede Soggetto Accreditato" name="cfEnteAtt" moduleName="M_EntiVoucher" addBlank="true" onChange="valorizzaDescrizione(this)" />
        	<%}%>
        </td>
    </tr>
    <tr>
    	<td class="etichetta">Sede Soggetto Accreditato</td>
       	<td class="campo">
       		<%if (tipoGruppoCollegato.equalsIgnoreCase(it.eng.sil.module.voucher.Properties.SOGGETTO_ACCREDITATO)) {%>
        		<af:comboBox classNameBase="input" title="Sede Soggetto Accreditato" name="sedeEnteAtt" moduleName="M_SediEnteCollegato" addBlank="true" onChange="valorizzaDescrizione(this)" />
        	<%} else {%>
        		<af:comboBox classNameBase="input" title="Sede Soggetto Accreditato" name="sedeEnteAtt" moduleName="M_SediEntiVoucher" addBlank="true" onChange="valorizzaDescrizione(this)" />
        	<%}%>
        </td>
    </tr>
	<tr>
    	<td class="etichetta">Stato</td>
    	<td class="campo">
    		<af:comboBox classNameBase="input" name="statoTDA" moduleName="M_StatiVoucher" 
            		addBlank="true" selectedValue="<%=statoTDA%>" title="Stato" onChange="valorizzaDescrizione(this); toggleData()"/>
       	&nbsp;&nbsp;Da
   
          <af:textBox type="date" title="Data stato da" name="dataStatoDa" value="<%=dataStatoDa%>" size="10" maxlength="10" required="false" validateOnPost="true" />
          &nbsp;a&nbsp; <af:textBox type="date" title="Data stato a" name="dataStatoA" value="<%=dataStatoA%>" size="10" maxlength="10" required="false" validateOnPost="true"/>
          </td>
   	</tr>
   	<tr>
          <td class="etichetta">Assegnati Scaduti</td>
          <td class="campo">
            <Input type="checkBox" onchange="handleChange(this);" title="Assegnati Scaduti" name="assegnatiScaduti" value="on" <%=assegnatiScaduti.equalsIgnoreCase("ON")?"checked":"" %> />
          </td>
        </tr>
    	<tr>
          <td class="etichetta">Attivati Scaduti</td>
          <td class="campo">
            <Input type="checkBox" onchange="handleChange(this);" title="Attivati Scaduti" name="attivatiScaduti" value="on" <%=attivatiScaduti.equalsIgnoreCase("ON")?"checked":"" %> />
          </td>
        </tr>
   	<tr>
       	<td class="etichetta">Stato Pagamento</td>
        <td class="campo">
       		<af:comboBox classNameBase="input" name="statoPagamentoTDA" moduleName="M_GETSTATOPAGAMENTO" onChange="valorizzaDescrizione(this)" 
            		addBlank="true" selectedValue="<%=statoPagamentoTDA%>" title="Stato Pagamento"/>
        </td>
   	</tr>
  	 <tr>
       	<td class="etichetta">Motivo Annullamento</td>
        <td class="campo">
       		<af:comboBox classNameBase="input" name="codAnnullTDA" moduleName="M_GET_MOTIVO_ANNULL_VOUCHER" onChange="valorizzaDescrizione(this)" 
            		addBlank="true" selectedValue="<%=codAnnullTDA%>" title="Motivo Annullamento"/>
        </td>
   	</tr>
    <tr>
       	<td class="etichetta">Azione</td>
        <td class="campo">
       		<af:comboBox classNameBase="input" name="azioneTDA" moduleName="M_AzioniModelloVoucher" onChange="valorizzaDescrizione(this)" 
            		addBlank="true" selectedValue="<%=azioneTDA%>" title="Azione"/>
        </td>
   	</tr>
   	<tr>
	    <td class="etichetta">Centro per l'Impiego</td>
	    <td class="campo">
	      <af:comboBox name="codCPI" title="Centro per l'Impiego competente" moduleName="M_ELENCOCPI" onChange="valorizzaDescrizione(this)" 
	      	classNameBase="input" addBlank="true" selectedValue="<%=codCPI%>"/>
	      	 
	    </td>
  	<tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
	  	<td colspan="2" align="center">
	  	<input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
	  	&nbsp;&nbsp;
	  	<input type="reset" class="pulsanti" value="Annulla" onclick="clearForm();"/>
	  	</td>
	</tr>
	<af:textBox type="hidden" name="descrCodCpiSel" value="<%=descrCPI%>" />
	<af:textBox type="hidden" name="descrCFSA" value="<%=descrCFSA%>" />
	<af:textBox type="hidden" name="descrSedeSA" value="<%=descrSedeSA%>" />
	<af:textBox type="hidden" name="descrStato" value="<%=descrStato%>" />
	<af:textBox type="hidden" name="descrPagamento" value="<%=descrPagamento%>" />
	<af:textBox type="hidden" name="descrAnnull" value="<%=descrAnnull%>" />
	<af:textBox type="hidden" name="descrAzione" value="<%=descrAzione%>" />
	
	<input type="hidden" name="PAGE" value="ListaTDAPage"/>
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
</table>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>