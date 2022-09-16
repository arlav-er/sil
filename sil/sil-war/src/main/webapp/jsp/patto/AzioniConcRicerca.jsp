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
	InfoRegioneSingleton regione = InfoRegioneSingleton.getInstance();  
	String regionePolo = regione.getCodice(); 
	String azioni = "Azioni";
	if("8".equalsIgnoreCase(regionePolo)) {	
		azioni = "Attività";
	}
	
 	// NOTE: Attributi della pagina (pulsanti e link) 
  	String _page=StringUtils.getAttributeStrNotNull(serviceRequest,"page");
  	PageAttribs attributi = new PageAttribs(user, _page);
  
  	String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  	String cdnLavoratore=StringUtils.getAttributeStrNotNull(serviceRequest,"cdnLavoratore");
  	String queryString = null;
  	boolean canPrint = attributi.containsButton("Stampa");

	String codServizio=StringUtils.getAttributeStrNotNull(serviceRequest, "codServizio");
	String dataColloquioDa=StringUtils.getAttributeStrNotNull(serviceRequest, "dataColloquioDa");
	String dataColloquioA=StringUtils.getAttributeStrNotNull(serviceRequest, "dataColloquioA");
	String prgAzioniRag=StringUtils.getAttributeStrNotNull(serviceRequest, "prgAzioniRag"); 
	String prgAzioni=StringUtils.getAttributeStrNotNull(serviceRequest, "prgAzioni");  
	String dataStimataDa=StringUtils.getAttributeStrNotNull(serviceRequest, "dataStimataDa");  
	String dataStimataA=StringUtils.getAttributeStrNotNull(serviceRequest, "dataStimataA"); 
	String codEsito=StringUtils.getAttributeStrNotNull(serviceRequest, "codEsito");
	String codEsitoSifer=StringUtils.getAttributeStrNotNull(serviceRequest, "codEsitoSifer");
	String codEsitoRendicont=StringUtils.getAttributeStrNotNull(serviceRequest, "codEsitoRendicont"); 
	String dataSvolgimentoDa=StringUtils.getAttributeStrNotNull(serviceRequest, "dataSvolgimentoDa");  
	String dataSvolgimentoA=StringUtils.getAttributeStrNotNull(serviceRequest, "dataSvolgimentoA");   
	String azioniNonConclCheck=StringUtils.getAttributeStrNotNull(serviceRequest, "azioniNonConclCheck");
	String azioniEsitoDiverso=StringUtils.getAttributeStrNotNull(serviceRequest, "azioniEsitoDiverso");
	String codCPI=StringUtils.getAttributeStrNotNull(serviceRequest, "codCPI");   
	String azioniVoucherCheck=StringUtils.getAttributeStrNotNull(serviceRequest, "azioniVoucherCheck");
	String cfEnteAtt=StringUtils.getAttributeStrNotNull(serviceRequest, "cfEnteAtt");
	String sedeEnteAtt=StringUtils.getAttributeStrNotNull(serviceRequest, "sedeEnteAtt");
	
	String numConfigVoucher = serviceResponse.containsAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM")?
  			serviceResponse.getAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM").toString():it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
	
 	SourceBean azioniSb=(SourceBean) serviceResponse.getAttribute("M_DeAzioni");
 	
	//NOTA: le pagine di ricerca devono avere lo stile prof_ro -> quindi invece di
  	//canModify si deve passare il valore false

	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	
	String labelServizio = "Servizio";
	String labelAzione = "Azione";
	String labelAzioni = "Azioni";
	String labelObiettivo = "Obiettivo";
	String umbriaGestAz = "0";
	if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
		umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
	}
	if(umbriaGestAz.equalsIgnoreCase("1")){
		labelServizio = "Area";
		labelAzione = "Misura";
		labelAzioni = "Misure";
		labelObiettivo = "Servizio";
	}
%>
<html>
	<head>
	<title>Estrazione <%=azioni%> Concordate</title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
	  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery.selectBoxIt.css">
	  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">
	<af:linkScript path="../../js/" />
	
	  <script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
	  <script src="../../js/jqueryui/jquery-ui.min.js"></script>
	  <script src="../../js/jqueryui/jquery.selectBoxIt.min.js"></script>
	
	  <script type="text/javascript">
	    $(function() {
	    	$("[name='prgAzioniRag']").selectBoxIt({
	            theme: "default",
	            autoWidth: false
	        });
			$("[name='prgAzioni']").selectBoxIt({
	            theme: "default",
	            autoWidth: false
	        });
	    	$("[name='prgAzioniRag']").on('change', function() {
	    		riempiAzioni(this.value);
	    		salvaDescrizioneCombo('prgAzioniRag', 'obiettivo_H');
	    		$("[name='prgAzioni']").data("selectBox-selectBoxIt").refresh();
	    	});	       	
	    	$("[name='prgAzioni']").on('change', function() {
	    		riempiObiettivi(this.value);
	    		salvaDescrizioneCombo('prgAzioni', 'azione_H');
	    		$("[name='prgAzioniRag']").data("selectBox-selectBoxIt").refresh();
	    	});		               
	    });	    
	    </script>    

    <!-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) -->
  <%@ include file="../documenti/_apriGestioneDoc.inc"%>

     <script language="javascript"><!--
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
     %>
  
     var configVoucherDB = '<%=numConfigVoucher%>';
     
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
		      return 1; // Ritorna 1 se la data1 e maggiore di data 2      			
		   if(tmpdata2>tmpdata1)   
		      return 2; // Ritorna 2 se la data2 e maggiore di data 1
		   if(tmpdata1==tmpdata2)   
		      return 0; // Ritorna 0 se la data1 e uguale a data 2
		}
		return 0;
	}
    
      
      function controllaDate() {

		ok=true;
		
		var objDataColloquioDa=eval("document.forms[0].dataColloquioDa");         
      	var objDataColloquioA=eval("document.forms[0].dataColloquioA");
   	
		var objDataStimataDa=eval("document.forms[0].dataStimataDa");      	
      	var objDataStimataA=eval("document.forms[0].dataStimataA");
      	      	
      	var objDataSvolgimentoDa=eval("document.forms[0].dataSvolgimentoDa");
      	var objDataSvolgimentoA=eval("document.forms[0].dataSvolgimentoA");
      			
		dataColloquioDa=objDataColloquioDa.value;
		dataColloquioA=objDataColloquioA.value;
		
		dataStimataDa=objDataStimataDa.value;
		dataStimataA=objDataStimataA.value;
		
		dataSvolgimentoDa=objDataSvolgimentoDa.value;
		dataSvolgimentoA=objDataSvolgimentoA.value;

		if (compDate(dataColloquioDa, dataColloquioA)==1) {//DA maggiore di A
			alert("Data programma \'da\' maggiore della data programma \'a\'");
			objDataColloquioDa.focus();
			ok=false;
		}
		
		if (ok && (compDate(dataStimataDa, dataStimataA)==1)) {
			alert("Data stimata \'da\' maggiore della data stimata \'a\'");
			objDataStimataDa.focus();
			ok=false;
		}
		
		if (ok &&(compDate(dataSvolgimentoDa, dataSvolgimentoA)==1)) {
				alert("Data di svolgimento/conclusione \'da\' maggiore della data di svolgimento/conclusione \'a\'");
				objDataSvolgimentoDa.focus();
				ok=false;
		}

  		return ok;
      
      }
     
   <%--  function toggleVisDataSvolgimento(esito) {
        var divDataSvolgimento = document.getElementById("dataSvolgimentoDiv");
        divDataSvolgimento.style.display=(esito=="FC")?"":"none";
      	document.Frm1.azioniNonConclCheck.checked=false;
      		if (esito!="FC") { 
      			document.Frm1.dataSvolgimentoDa.value="";
      			document.Frm1.dataSvolgimentoA.value="";
      		}
      	}  --%>  
      
      
       function toggleVisDataSvolgimento(esitoRendicont) {
           var divDataSvolgimento = document.getElementById("dataSvolgimentoDiv");
      	   var visDataSvolgimento = false;
      	   var j=0;
      	   for(i=0;i<esitoRendicont.length;i++) {
	  	   		if (esitoRendicont[i].selected) {
	  	   			j++;
	      	  		if (j==1 && esitoRendicont[i].value == 'E') {
	      	  			visDataSvolgimento = true;
	      	   		}
	      	   		else {
	      	   			visDataSvolgimento = false;
	      	   		}
	      	   	}
      	   }
      	   
      	   if (visDataSvolgimento) {
      	   	divDataSvolgimento.style.display="";
      	   } else {
      	     	divDataSvolgimento.style.display="none";
      	     	document.Frm1.dataSvolgimentoDa.value="";
      		  	document.Frm1.dataSvolgimentoA.value="";
      	   }
      	   document.Frm1.azioniNonConclCheck.checked=false;
     } 
		
	function resetComboEsito () {
		if (document.Frm1.azioniNonConclCheck.checked) {
			var divDataSvolgimento = document.getElementById("dataSvolgimentoDiv");
      		divDataSvolgimento.style.display="none";
      		document.Frm1.dataSvolgimentoDa.value="";
	      	document.Frm1.dataSvolgimentoA.value="";
	      	for(j=0;j<document.Frm1.codEsitoRendicont.length;j++) {
				if(document.Frm1.codEsitoRendicont[j].selected) {
					if (document.Frm1.codEsitoRendicont[j].value == 'E') {
						document.Frm1.codEsitoRendicont[j].selected = false;							
					}
				}	
			}
	      	
		}
		else {
			for(j=0;j<document.Frm1.codEsitoRendicont.length;j++) {
				document.Frm1.codEsitoRendicont[j].selected = true;
			}
		}
		
   	}
     
     <%-- function resetComboEsito () { 
    	
     	if (document.Frm1.codEsito!=null){
      		if (document.Frm1.codEsito.value=="FC") {
	      		var divDataSvolgimento = document.getElementById("dataSvolgimentoDiv");
      			divDataSvolgimento.style.display="none";
      			document.Frm1.dataSvolgimentoDa.value="";
	      		document.Frm1.dataSvolgimentoA.value="";
      		}
      		document.Frm1.codEsito.value="";
      	}
      } --%>
    
      
     
      //vettore di correlazione azioni-obiettivi
      //mixato al vettore delle azioni 
      var azione=new Array();
      var azione_obiettivo=new Array();
      <%
        Vector azioniObiettiviRows= serviceResponse.getAttributeAsVector("M_DeAzioni.ROWS.ROW");
	    SourceBean row = null;
	    Object codAzione=null;
	    String descAzione="";
	    Object codObiettivo=null;
	    Iterator record = azioniObiettiviRows.iterator();
	    while(record.hasNext()) { 
     	    row  = (SourceBean) record.next();
        	codAzione     =  row.getAttribute("CODICE");
        	descAzione     = StringUtils.getAttributeStrNotNull(row, "DESCRIZIONE");
	        codObiettivo  = row.getAttribute("PRGAZIONERAGG");
	        descAzione= (descAzione.length()>75)? descAzione.substring(0, 75) + "...":descAzione;
	        out.print("azione["+codAzione+"]=\"" + descAzione +"\";\n");
	        out.print("azione_obiettivo["+codAzione+"]="+codObiettivo+";\n");
	        
    	}
    
      %>
	//vettore degli obiettivi
	var obiettivo=new Array();
	<%
		Vector obiettiviRows=serviceResponse.getAttributeAsVector("M_DeAzioniRagg.ROWS.ROW");
		String descObiettivo="";
		record=obiettiviRows.iterator();
		while(record.hasNext()) {
			row= (SourceBean) record.next();
			codObiettivo=row.getAttribute("CODICE");
			descObiettivo=StringUtils.getAttributeStrNotNull(row, "DESCRIZIONE");
			descObiettivo=(descObiettivo.length()>60)?descObiettivo.substring(0,60) + "..." : descObiettivo;
			out.print("obiettivo["+codObiettivo+"]=\""+descObiettivo+"\";\n");
		}
	
	%>      
      
      function riempiObiettivi(codAzione) {
       if (codAzione!="") {     	
		   while (document.forms[0].prgAzioniRag.options.length>0) {
	    	    document.forms[0].prgAzioniRag.options[0]=null;
		    }
	       codObiettivo=azione_obiettivo[codAzione];
	       document.forms[0].prgAzioniRag.options[0]=new Option(obiettivo[codObiettivo], codObiettivo, false, true);
	    }
	    else ripristinaCombo();
      }
      
      function riempiAzioni(codObiettivo) {
	      if (codObiettivo!="") {
				while (document.forms[0].prgAzioni.options.length>0) {
					document.forms[0].prgAzioni.options[0]=null;
				}
		      
		      	//riempio con le azioni
		      	j=1;
		       document.forms[0].prgAzioni.options[0]=new Option();
		      	for (i=1; i <= azione_obiettivo.length; i++) {    
		      		if (azione_obiettivo[i]==codObiettivo) {
		      		     document.forms[0].prgAzioni.options[j]=new Option(azione[i], i, false, false);      			
		      		     j++;
		      		}
		      	}
		  }      	
      	  else ripristinaCombo();
      }

	function ripristinaCombo(){
		//azzero le combo		
		while (document.forms[0].prgAzioni.options.length>0) {
			document.forms[0].prgAzioni.options[0]=null;
		}
		while (document.forms[0].prgAzioniRag.options.length>0) {
    	    document.forms[0].prgAzioniRag.options[0]=null;
	    }
	
		//le riempio
		document.forms[0].prgAzioniRag.options[0]= new Option();
		for (i=1; i<obiettivo.length; i++) {
			document.forms[0].prgAzioniRag.options[i]=new Option(obiettivo[i], i, false, false);
		}
		
		document.forms[0].prgAzioni.options[0]= new Option();
		for (i=1; i<azione.length; i++) {
			document.forms[0].prgAzioni.options[i]=new Option(azione[i], i, false, false);
		}
	}
	
	function salvaDescrizioneCombo(sourceName, destName) {
		sourceObj=eval("document.forms[0]."+ sourceName);
		destObj=eval("document.forms[0]."+destName);
		sourceOption=sourceObj.options.selectedIndex;
		sourceText=sourceObj[sourceOption].text;
		destObj.value=sourceText;
	}
	
	function stampa() {
	var codSelezionati = 0;
      if (!controllaFunzTL()) return;
      
      for (i=0;i<document.Frm1.codEsito.length;i++) {
      	if (document.Frm1.codEsito[i].selected) {
      		
   		    codSelezionati = codSelezionati + 1;
   	  	}
   	  }	
   	  if (codSelezionati == 0) {
   	     alert ("Il campo Esito è obbligatorio");
	  }
	  else{ 
		if (controllaDate()) {
      
      	var codServizio 	 = document.Frm1.codServizio.value;
	  	var dataColloquioDa  = document.Frm1.dataColloquioDa.value;
	  	var dataColloquioA   = document.Frm1.dataColloquioA.value;
	  	var prgAzioniRag		 = document.Frm1.prgAzioniRag.value;
	  	var prgAzioni		     = document.Frm1.prgAzioni.value;
	  	var dataStimataDa	 = document.Frm1.dataStimataDa.value;
	  	var dataStimataA	 = document.Frm1.dataStimataA.value;
	  
		  var esitoStr = '';
	  		for (i=0;i<document.Frm1.codEsito.length;i++) {
	  		  if (document.Frm1.codEsito[i].selected) {
	  			if (esitoStr == '') {
					esitoStr = esitoStr + document.Frm1.codEsito[i].value ;
				}
				else {
					esitoStr = esitoStr + ',' + document.Frm1.codEsito[i].value;
				}
	  		  }
	  		}

		  var esitoRendicontStr = '';
		  	for (i=0;i<document.Frm1.codEsitoRendicont.length;i++) {
	  		  if (document.Frm1.codEsitoRendicont[i].selected) {
	  			if (esitoRendicontStr == '') {
	  				esitoRendicontStr = esitoRendicontStr + document.Frm1.codEsitoRendicont[i].value ;
				}
				else {
					esitoRendicontStr = esitoRendicontStr + ',' + document.Frm1.codEsitoRendicont[i].value;
				}
	  		  }
	  		}
		  	
	  
	  var dataSvolgimentoDa   = document.Frm1.dataSvolgimentoDa.value;
	  var dataSvolgimentoA    = document.Frm1.dataSvolgimentoA.value;
	  var azioniNonConclCheck = document.Frm1.azioniNonConclCheck.checked ? "on":"";
	  var azioniVoucherCheck  = document.Frm1.azioniVoucherCheck.checked ? "on":"";
	  var codCPI			  = document.Frm1.codCPI.value;
	  var cfEnteAtt 		  = document.Frm1.cfEnteAtt.value;
	  var sedeEnteAtt 		  = document.Frm1.sedeEnteAtt.value;
 
     apriGestioneDoc('RPT_AZIONI_CONCORDATE','&cdnLavoratore=<%=cdnLavoratore%>&codServizio='+codServizio     
      +'&dataColloquioDa='+dataColloquioDa
      +'&dataColloquioA='+dataColloquioA
      +'&prgAzioniRag='+prgAzioniRag
      +'&prgAzioni='+prgAzioni
      +'&dataStimataDa='+dataStimataDa
      +'&dataStimataA='+dataStimataA
      +'&esito='+esitoStr
      +'&esitorendicont='+esitoRendicontStr
      +'&dataSvolgimentoDa='+dataSvolgimentoDa
      +'&dataSvolgimentoA='+dataSvolgimentoA
      +'&azioniNonConclCheck='+azioniNonConclCheck
      +'&azioniVoucherCheck='+azioniVoucherCheck
      +'&cfEnteAtt='+cfEnteAtt
      +'&sedeEnteAtt='+sedeEnteAtt
      +'&codCPI='+codCPI
      ,'ALVARO');
      
      }
    }    
      
      /* 28/08/2005 DAVIDE
         Riabilito i pulsanti: 
         serve qualora si scelga, nella pagina di gestione dei documenti,
         di salvare senza visualizzare. Tale scelta non apre nessuna pagina e non essendoci
         cambio di contesto i pulsanti rimangono diabilitati impedendo altre operazioni.         
      */
      undoSubmit();
      
    }

    function creaStringa(){
  		return (creaStringaEsito() && creaStringaEsitoRendicont());
  	}

	function creaStringaEsito() {
		var esitoStr = '';
		var contSelezionati = 0;
		for (i=0;i<document.Frm1.codEsito.length;i++) {
 			if (document.Frm1.codEsito[i].selected) {
 				contSelezionati = contSelezionati + 1;
				if (esitoStr == '') {
					esitoStr = esitoStr + document.Frm1.codEsito[i].value;
				}
				else {
					esitoStr = esitoStr + ',' + document.Frm1.codEsito[i].value;
				}
		 	}
		}
		
		if (contSelezionati == 0) {
			alert ("Il campo Esito è obbligatorio");
			return false;
		}
		 			
		document.Frm1.esito.value=esitoStr;
		return true;
  }

  function creaStringaEsitoRendicont() {
		var esitoRendicontStr = '';
		var contSelezionati = 0;
		for (i=0;i<document.Frm1.codEsitoRendicont.length;i++) {
 			if (document.Frm1.codEsitoRendicont[i].selected) {
 				contSelezionati = contSelezionati + 1;
				if (esitoRendicontStr == '') {
					esitoRendicontStr = esitoRendicontStr + document.Frm1.codEsitoRendicont[i].value;
				}
				else {
					esitoRendicontStr = esitoRendicontStr + ',' + document.Frm1.codEsitoRendicont[i].value;
				}
		 	}
		}
		
			 			
		document.Frm1.esitorendicont.value=esitoRendicontStr;
		return true;	  
  } 

  function controllaEnteVoucher() {
	  if (configVoucherDB == '1') {
		  if (document.Frm1.sedeEnteAtt.value != "") {
			  document.Frm1.cfEnteAtt.value = "";
		  }
	  }
	  return true;
  }  

</script>
    
</head>
	<body class="gestione" onload="rinfresca();">
  <p class="titolo">Estrazione <%=labelAzioni %> Concordate</p>
    <%out.print(htmlStreamTop);%>
	<af:form method="GET" action="AdapterHTTP" name="Frm1" onSubmit="controllaDate() && creaStringa() && controllaEnteVoucher()" >
      <table class="main">
            
        <tr><td class="etichetta">&nbsp;</td></tr>
        <tr>
          <td class="etichetta"><%=labelServizio %></td>
          <td class="campo">
          <af:comboBox classNameBase="input" title="<%=labelServizio %>" name="codServizio" moduleName="M_De_List_Servizi" addBlank="true" selectedValue="<%=codServizio%>" onChange="salvaDescrizioneCombo('codServizio', 'servizio_H')" />
            <input type="hidden" name="servizio_H" value=""/>
          </td>
        </tr>        
        <tr>
          <td class="etichetta">Data programma da</td>
          <td class="campo">
          <af:textBox type="date" title="Data programma da" name="dataColloquioDa" value="<%=dataColloquioDa%>" size="10" maxlength="10"  validateOnPost="true"/>
          &nbsp;a&nbsp;          <af:textBox type="date" title="Data programma a" name="dataColloquioA" value="<%=dataColloquioA%>" size="10" maxlength="10" validateOnPost="true"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta"><%=labelObiettivo %></td>
          <td class="campo">
            <af:comboBox classNameBase="input" title="<%=labelObiettivo %>" name="prgAzioniRag" moduleName="M_DeAzioniRagg" selectedValue="<%=prgAzioniRag%>" addBlank="true"/>
            <input type="hidden" name="obiettivo_H" value=""/>
          </td>
        </tr>     
        <tr>
          <td class="etichetta"><%=labelAzione %></td>
          <td class="campo">
            <af:comboBox classNameBase="input" title="<%=labelAzione %>" name="prgAzioni" moduleName="M_DeAzioni" selectedValue="<%=prgAzioni%>" addBlank="true"/>
            <input type="hidden" name="azione_H" value=""/>
          </td>
        </tr>     
        <tr>
          <td class="etichetta">Data stimata da</td>
          <td class="campo">
          <af:textBox type="date" title="Data stimata da" name="dataStimataDa" value="<%=dataStimataDa%>" size="10" maxlength="10" required="true" validateOnPost="true"/>
          &nbsp;a&nbsp; <af:textBox type="date" title="Data stimata a" name="dataStimataA" value="<%=dataStimataA%>" size="10" maxlength="10" required="true" validateOnPost="true"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Esito</td>
          <td class="campo">
          
        <%-- Modificato il 03/04/2006
             @author Anna Paola Coppola 
             Multiselezione per il parametro "esito". --%>
        
        <input type="hidden" name="esito"/>
        <af:comboBox classNameBase="input" title="Esito" name="codEsito" moduleName="M_DeEsito" selectedAll="true"
                     multiple="true" size="8" addBlank="false">
       	</af:comboBox> 
        <%
        //se è satto passato il parametro codEsito devo selezionare solo gli esiti passati come parametro e non tutti
        if (!codEsito.equals("")) {
        	//esiti da selezionare
        	Vector esitiSel = StringUtils.split(codEsito,",");
        	%>
        	<script>
        		var vettEsiti = new Array();
        		<%
        		//inizializza il vettore con gli esiti
		    	for (int k=0;k<esitiSel.size();k++) {
		      		out.print("vettEsiti["+k+"]='"+esitiSel.get(k).toString()+"';\n");
		      	}      
			    %>
			    //deseleziona tutto
			    for(j=0;j<document.Frm1.codEsito.length;j++) {
			   		document.Frm1.codEsito[j].selected = false; 
			    }

			    //scorri gli esiti da selezionare
        		for (iVett=0; iVett < vettEsiti.length; iVett++) {
        			for(j=0;j<document.Frm1.codEsito.length;j++) {
        				if(document.Frm1.codEsito[j].value == vettEsiti[iVett]) {
							document.Frm1.codEsito[j].selected = true;
							break;
						}
					}
				}
			</script>
        <%}%>
        </td>
        </tr>
        <tr>
          <td class="etichetta">Esito Formazione</td>
          <td class="campo">
            <af:comboBox classNameBase="input" title="Esito formazione" name="codEsitoSifer" moduleName="M_DeEsito_Sifer" 
            	selectedValue="<%=codEsitoSifer%>" addBlank="true" />
          </td>
        </tr>
        <tr>
          <td class="etichetta">Esito Formazione Diverso da Esito SIL</td>
          <td class="campo">
            <Input type="checkBox" title="Esito Formazione Diverso da Esito SIL" name="azioniEsitoDiverso" value="on" <%=azioniEsitoDiverso.equalsIgnoreCase("ON")?"checked":"" %> />
          </td>
        </tr>
         <tr>
          <td class="etichetta">Esito rendicontazione</td>
          <td class="campo">
          
          <input type="hidden" name="esitorendicont"/>
          <af:comboBox classNameBase="input" title="Esito rendicontazione" name="codEsitoRendicont" moduleName="M_DeEsitoRendicont" selectedAll="true"
                     multiple="true" size="4" addBlank="false" onChange="toggleVisDataSvolgimento(codEsitoRendicont);">
       	  </af:comboBox> 
         <%
         //se è satto passato il parametro codEsitoRendicont devo selezionare solo gli esiti passati come parametro e non tutti
        	if (!codEsitoRendicont.equals("")) {
        	 //esiti da selezionare
        	 Vector esitiRendicontSel = StringUtils.split(codEsitoRendicont,",");
        	%>
        	<script>
        		var vettEsitiRendicont = new Array();
        		<%
        		//inizializza il vettore con gli esiti
		    	for (int k=0;k<esitiRendicontSel.size();k++) {
		      		out.print("vettEsitiRendicont["+k+"]='"+esitiRendicontSel.get(k).toString()+"';\n");
		      	}      
			    %>
			    deseleziona tutto
			    for(j=0;j<document.Frm1.codEsitoRendicont.length;j++) {
			   		document.Frm1.codEsitoRendicont[j].selected = false; 
			    }

			    //scorri gli esiti da selezionare
        		for (iVett=0; iVett < vettEsitiRendicont.length; iVett++) {
        			for(j=0;j<document.Frm1.codEsitoRendicont.length;j++) {
        				if(document.Frm1.codEsitoRendicont[j].value == vettEsitiRendicont[iVett]) {
							document.Frm1.codEsitoRendicont[j].selected = true;
							break;
						}
					}
				}
			</script>
        <%}%>
        </td>
        </tr>           
        
        
                     
	<%-- <tr id="dataSvolgimentoDiv" style="display:<%if (codEsito.length() == 1) {%><%=codEsito.get(0).equals("FC")?"":"none"%> <% } else%> <%="none"%>"> --%>
	<%--	<tr id="dataSvolgimentoDiv" style="display:<%=codEsito.equals("FC")?"":"none"%>"> --%>
		 <tr id="dataSvolgimentoDiv" style="display:none"> 
	
	<%-- <tr id="dataSvolgimentoDiv" style="display:<%=codEsito.get(0).equals("FC")?"":"none"%>"> --%>
	<%-- <tr id="dataSvolgimentoDiv" style="display:none" > --%>
          <td class="etichetta">Data di svolgimento/conclusione da</td>
          <td class="campo">
          <af:textBox type="date" name="dataSvolgimentoDa" value="<%=dataSvolgimentoDa%>" size="10" maxlength="10" validateOnPost="true"/>
          &nbsp;a&nbsp;   <af:textBox type="date" name="dataSvolgimentoA" value="<%=dataSvolgimentoA%>" size="10" maxlength="10" validateOnPost="true"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Solo azioni non concluse</td>
          <td class="campo">
            <Input type="checkBox" title="Solo azioni non concluse" name="azioniNonConclCheck" value="on" <%=azioniNonConclCheck.equalsIgnoreCase("ON")?"checked":"" %> onclick="resetComboEsito();"/>
          </td>
        </tr> 
        <%if (numConfigVoucher.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.CUSTOM_CONFIG)) {%>
	        <tr>
	          <td class="etichetta">Solo Voucher</td>
	          <td class="campo">
	            <Input type="checkBox" title="Solo Voucher" name="azioniVoucherCheck" value="on" <%=azioniVoucherCheck.equalsIgnoreCase("ON")?"checked":"" %> />
	          </td>
	        </tr>
	        <tr>
	          <td class="etichetta">cf ente attivato</td>
	          <td class="campo">
	          <af:comboBox classNameBase="input" title="cf ente attivato" name="cfEnteAtt" moduleName="M_EntiVoucher" 
            	selectedValue="<%=cfEnteAtt%>" addBlank="true" />
            </td>
            </tr>
            <tr>
	          <td class="etichetta">sede ente attivato</td>
	          <td class="campo">
            	<af:comboBox classNameBase="input" title="sede ente attivato" name="sedeEnteAtt" moduleName="M_SediEntiVoucher" 
            	selectedValue="<%=sedeEnteAtt%>" addBlank="true" />
	          </td>
	        </tr>
        <%}%>
        <tr><td class="etichetta">&nbsp;</td></tr>        
        <tr>
          <td class="etichetta">Centro per l'Impiego</td>
          <td class="campo">
            <af:comboBox classNameBase="input" title="Centro per l'Impiego" name="codCPI" moduleName="M_ElencoCPI" selectedValue="<%=codCPI%>" addBlank="true" onChange="salvaDescrizioneCombo('codCPI', 'cpi_H');" required="true" />
            <input type="hidden" name="cpi_H" value="" />
          </td>
        </tr>  
              
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          <td colspan="2" align="center">
          <input type="submit" class="pulsanti"  name="cerca" value="Cerca"/>
          &nbsp;&nbsp;
          <input type="reset" class="pulsanti" value="Annulla" onclick="ripristinaCombo()" />
          &nbsp;&nbsp;
	      <% if (canPrint) { %>
          <input type="button" class="pulsanti" value="Stampa" onclick="stampa()" />
	      <%  } %>
          </td>
        </tr>
        <input type="hidden" name="NEW_SESSION" value="TRUE"/>
        <input type="hidden" name="PAGE" value="AzioniConcordateListaPage"/>
        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
      </table>
      </af:form>
      

<%out.print(htmlStreamBottom);%>

	</body>
</html>

