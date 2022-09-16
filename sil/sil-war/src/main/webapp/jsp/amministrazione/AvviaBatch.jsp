<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.afExt.utils.StringUtils,                  
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.text.*,
                  it.eng.sil.util.amministrazione.impatti.LogBatch,
                  it.eng.afExt.utils.DateUtils,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.*" %>
                  
           
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
 String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
 String _funzione = (String) serviceRequest.getAttribute("cdnFunzione"); 
 String strLog = "";
 File fileLog;
 String nomeFileLog = "";
 String queryString = null;
 boolean canModify = attributi.containsButton("AVVIA");
 canModify = true;
 String htmlStreamTop = StyleUtils.roundTopTable(canModify);
 String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
 String dataScansione1 = (String) serviceRequest.getAttribute("DATABATCH1");
 String dataScansione2 = (String) serviceRequest.getAttribute("DATABATCH2");
 String tipoBatch = (String) serviceRequest.getAttribute("tipoBatch");
 String nonTrattatti = (String) serviceRequest.getAttribute("nonTrattati");
 String dataInserimentoDa = StringUtils.getAttributeStrNotNull(serviceRequest, "DATAINSDA");
 String dataInserimentoA =  StringUtils.getAttributeStrNotNull(serviceRequest ,"DATAINSA");
 String codicefiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "CODICEFISCALE");
 String dataSiferDa = StringUtils.getAttributeStrNotNull(serviceRequest, "DATASIFERDA");
 String dataSiferA =  StringUtils.getAttributeStrNotNull(serviceRequest ,"DATASIFERA");
 SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
 if(StringUtils.isEmptyNoBlank(dataSiferDa)){
	 Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date inizio = new Date(cal.getTimeInMillis());
	 dataSiferDa = sdf.format(inizio);
 }
 if(StringUtils.isEmptyNoBlank(dataSiferA)){
	 dataSiferA = sdf.format(new Date());
 }
 String numMaxMesi = StringUtils.getAttributeStrNotNull(serviceRequest, "numMaxMesi");
 String numLavoratoriDaProcessare = StringUtils.getAttributeStrNotNull(serviceRequest, "numLavDaProcessare");
 
 boolean forzaInserimento = false;
 boolean sOccAnnoBatchAnni = false;
 
 forzaInserimento = serviceRequest.containsAttribute("FORZA_INSERIMENTO") && serviceRequest.getAttribute("FORZA_INSERIMENTO").equals("S");
 sOccAnnoBatchAnni = serviceRequest.containsAttribute("SoccAnno") && serviceRequest.getAttribute("SoccAnno").equals("S");
 
 SourceBean ritorno = (SourceBean)serviceResponse.getAttribute("M_AvviaBatch");
 if (ritorno!=null){
  dataScansione1 = (String)ritorno.getAttribute("DATABATCH1");
  dataScansione2 = (String)ritorno.getAttribute("DATABATCH2");
  dataInserimentoDa = StringUtils.getAttributeStrNotNull(ritorno, "DATAINSDA");
  dataInserimentoA = StringUtils.getAttributeStrNotNull(ritorno, "DATAINSA");
  tipoBatch = ritorno.getAttribute("tipoBatch").toString();
  nonTrattatti = (String) ritorno.getAttribute("nonTrattati");
  forzaInserimento = ritorno.containsAttribute("FORZA_INSERIMENTO") && ritorno.getAttribute("FORZA_INSERIMENTO").equals("true");
  sOccAnnoBatchAnni = ritorno.containsAttribute("SoccAnno") && ritorno.getAttribute("SoccAnno").equals("true");
  numMaxMesi = (String) ritorno.getAttribute("numMaxMesi");
  numLavoratoriDaProcessare = (String) ritorno.getAttribute("numLavDaProcessare");
 }
 
  String tmp = "";
  String dir = ConfigSingleton.getLogBatchPath();
  
  if(tipoBatch!=null && !tipoBatch.equals("") && (tipoBatch.equals("8") || tipoBatch.equals("9") || tipoBatch.equals("10") || tipoBatch.equals("11") || tipoBatch.equals("13"))){
		tmp = DateUtils.getNow();
		tmp = tmp.replace('/','-');
		nomeFileLog = tmp + ".log";
  }
  else {
	  if((dataScansione1!=null) && !dataScansione1.equals("")){
	    tmp = dataScansione1;
	    tmp = tmp.replace('/','-');
	    nomeFileLog = tmp + ".log";
	  } else {
	        if((dataScansione2!=null) && !dataScansione2.equals("")) {
	          tmp = dataScansione2;
	          tmp = tmp.replace('/','-');
	          nomeFileLog = tmp + ".log";
	        }
	  }
  }
  if((tipoBatch!=null) && !tipoBatch.equals("") && !tipoBatch.equals("null")) {    
    switch(Integer.parseInt(tipoBatch)){
      case 0:
            nomeFileLog = "BatchCessazioni" + nomeFileLog;
            break;
      case 1:
            nomeFileLog = "BatchMovFuturi" + nomeFileLog;
            break;
      case 2:
            nomeFileLog = "BatchTirociniSenzaContratto" + nomeFileLog;
            break;
	  case 3:
            nomeFileLog = "BatchFineValiditaCurriculum";
            break;
	  case 4:
            nomeFileLog = "BatchInizioValiditaCurriculum";
            break;
      case 5:
            nomeFileLog = "BatchFineMobilita" + nomeFileLog;
            break;
      case 6:
            nomeFileLog = "BatchImpattiTraAnni" + nomeFileLog;
            break;
      case 7:
      		nomeFileLog = "BatchAggiornamentoValiditaCurriculum";
      		break;
      case 8:
			nomeFileLog = "BatchCancellaMovAppErr" + nomeFileLog;
			break;
      case 9:
    	    nomeFileLog = "BatchChiusuraCmSuperamentoEta" + nomeFileLog;
			break;
      case 10:
    	    nomeFileLog = "BatchInvioSifer" + nomeFileLog;
			break;
      case 11:
  	    	nomeFileLog = "BatchAvvASelezioni" + nomeFileLog;
			break;
      case 12:
	    	nomeFileLog = "BatchFineSospensione" + nomeFileLog;
			break;
      case 13:
	    	nomeFileLog = "BatchRicalcolaImpatti" + nomeFileLog;
			break;
    }

	  if ((Integer.parseInt(tipoBatch) != 3) && (Integer.parseInt(tipoBatch) != 4) && (Integer.parseInt(tipoBatch) != 7)) {
	  	fileLog = new File(dir + File.separator + nomeFileLog);
     	if(fileLog.exists()){
		  LogBatch lb = new LogBatch(nomeFileLog, dir);
		  strLog = lb.readFile();
		} else {
		  strLog = "Attendere! Il batch sta recuperando i dati da processare.";
		}	  	
	  } else {

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		String oggi = df.format(new Date());
		oggi = oggi.replace('/', '-');

		fileLog = new File(dir + File.separator + nomeFileLog + "_" + oggi+ ".log");
		
		if (fileLog.exists() ) {
	        FileInputStream src = new FileInputStream(fileLog);
	        BufferedReader d = new BufferedReader(new InputStreamReader(src));
	
	        String campoTmp = d.readLine();
	        while(campoTmp!=null)
	        {
	          strLog = strLog + campoTmp + "\r\n";
	          campoTmp = d.readLine();
	        }
	        d.close();
	        src.close();
        } else {
		  strLog = "Nessun File trovato con i parametri specificati.";
		}
      }
  }

  
%>

<html>
<head>
    <title>AvviaBatch.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>

<script language="javascript">
<!--
  var dataOdierna = '<%=DateUtils.getNow()%>';
  var annoAttuale = dataOdierna.substr(6,4);
  
  function cambiaTendina(tendina, elem){
      var obj = document.getElementById(elem);
      var imgAperto = "../../img/aperto.gif";
      var imgChiuso = "../../img/chiuso.gif"; 
      if(tendina.alt == "Apri"){
        obj.style.display = "inline";
        tendina.alt = "Chiudi";
        tendina.src = imgAperto;
      } else {
          obj.style.display = "none";
          tendina.alt = "Apri";
          tendina.src = imgChiuso;
        }
   }

   function aggiornaLog(){
      // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;
   		
      var url = "AdapterHTTP?PAGE=AvviaBatchPage&DATABATCH2=" + document.form1.DATABATCH2.value;
      url += "&tipoBatch=" + document.form1.tipoBatch.value;
      url += "&nonTrattati=" + document.form1.nonTrattati.value;
      url += "&FORZA_INSERIMENTO=" + document.form1.FORZA_INSERIMENTO.value;
      url += "&SoccAnno=" + document.form1.SoccAnno.value;
      url += "&DATAINSDA=" + document.form1.DATAINSDA.value;
      url += "&DATAINSA=" + document.form1.DATAINSA.value;
      url += "&numMaxMesi=" + document.form1.numMaxMesi.value;
      url += "&numLavDaProcessare=" + document.form1.numLavDaProcessare.value;
      setWindowLocation(url);
   }

   function controllaDati(){
   	  var tipoBatch = new String(document.form1.tipoBatch.value);
   	  var oggi = '<%=DateUtils.getNow()%>';

      intTipoBatch = -1;
      if(tipoBatch)
		intTipoBatch = parseInt(tipoBatch)
		
		      switch (intTipoBatch)	{ 
		      	case 11:
						var nmMesi = new String(document.form1.numMaxMesi.value);
						if(nmMesi==""){
						 			alert("Attenzione: Indicare il Numero Max Mesi");
						 			return false;
						 }
		      		return true;
			      	break;		      
		      	case 10:
						var dataDa = new String(document.form1.DATASIFERDA.value);
						var dataA = new String(document.form1.DATASIFERA.value);
						if(dataDa==""){
						 			alert("Attenzione: Compilare data Inizio");
						 			return false;
						 }
						if(dataA==""){
						 			alert("Attenzione: Compilare data Fine");
						 			return false;
						 }
		      		return true;
			      	break;
		      	case 13:
					var nLavDaProcessare = new String(document.form1.numLavDaProcessare.value);
					if(nLavDaProcessare==""){
					 			alert("Attenzione: Indicare il Numero Lavoratori da processare");
					 			return false;
					 }
					else {
						intNumLavDaProcessare = -1;
						intNumLavDaProcessare = parseInt(nLavDaProcessare);
						if (intNumLavDaProcessare <= 0 || intNumLavDaProcessare > 500){
							alert("Attenzione: Numero Lavoratori da processare deve essere compreso tra 1 e 500");
				 			return false;	
						}
					}
	      		return true;
		      	break;
		    	default:
		    		 if(tipoBatch == "8"){
		    	   	  	  var dataDa = new String(document.form1.DATAINSDA.value);
		    	   	  	  var dataA = new String(document.form1.DATAINSA.value);
		    	   	  	  var ogf = oggi.substr(6,4) + oggi.substr(3,2) + oggi.substr(0,2);
		    	   	  	  var f = dataDa.substr(6,4) + dataDa.substr(3,2) + dataDa.substr(0,2);
		    		      var n = dataA.substr(6,4) + dataA.substr(3,2) + dataA.substr(0,2);
		    	   	  	  
		    	   	  	  if(dataDa!=""){
		    	   	  	  		if(f>ogf){
		    	   	  	  			alert("Attenzione: La data inserimento inizio è maggiore della data odierna");
		    	   	  	  			return false;
		    	   	  	  		}
		    	   	  	  }
		    	   	  	  
		    	   	  	  if(dataA!=""){
		    		  	  		if(n>ogf){
		    		  	  			alert("Attenzione: La data inserimento fine è maggiore della data odierna");
		    		  	  			return false;
		    		  	  		}
		    	   	  	  }
		    	   	  	  
		    	   	  	  if(dataDa!="" && dataA!=""){
		    		      	if(n<f){
		    		      		alert("Attenzione: la data inserimento inizio è maggiore della data inserimento fine");
		    		      		return false;
		    		      	}   	  	  
		    	   	  	  }
		    	   	  	  return true;
		    	   	  }
		    	   	  else {
		    		      var data = new String(document.form1.DATABATCH2.value);
		    		      if(data==""){
		    			  	alert("Attenzione: Il campo Data di scansione batch è obbligatorio");
		    			  	return false;
		    			  }
		    		      var f = oggi.substr(6,4) + oggi.substr(3,2) + oggi.substr(0,2);
		    		      var n = data.substr(6,4) + data.substr(3,2) + data.substr(0,2);
		    		      if(f < n){
		    		        //alert("Inserire una data non superiore ad oggi.");
		    		        alert("Attenzione: la data è posteriore alla data odierna.\nNon è possibile proseguire.");
		    		        return false;
		    		      }
		    		      
		    		      if (tipoBatch == "6") {
		    		      	var meseData = data.substr(3,2);
		    		      	var giornoData = data.substr(0,2);
		    		      	if (giornoData != "01" || meseData != "01") {
		    		      		alert("Attenzione: la data deve essere impostata all'inizio dell'anno specificato");
		    		      		return false;
		    		      	}
		    		      }
		    		      
		    		      
		    		      if (f > n) {
		    		      	if (tipoBatch != "2") {
		    		      		return confirm("Attenzione: la data è pregressa alla data odierna.\n Vuoi proseguire?");
		    		      	} else {
		    		          alert("Attenzione: la data è pregressa alla data odierna.\nNon è possibile proseguire.");
		    		          return false;      	
		    		      	}
		    		      }
		    		      return true;
		    		  }
			    	
		      }
      
			      
		
   	  
   	 
   }//function controllaDati()

   function gestisciCheck(obj){
    if(obj.value=="S")
      obj.value = "N";
    else
      obj.value = "S";
   }
   
   function controllo() {
   	if (document.form1.tipoBatch.value == "2") {
   		alert("Funzionalità non ancora implementata!");
   	}
   }
   
   function conferma() {
	   var tipoBatch = new String(document.form1.tipoBatch.value);

	      intTipoBatch = -1;
	      if(tipoBatch)
			intTipoBatch = parseInt(tipoBatch)
			switch (intTipoBatch)	{ 
		      	case 10:
		      	case 11:
		      	case 13:
		      		return true;
			      	break;
			      	default:
			      		if (document.form1.tipoBatch.value == "2") {
			      	   		alert("Funzionalità non ancora implementata!");
			      	   		return false;
			      	   	}
			      	   	if (document.form1.tipoBatch.value == "7") {
			      	   		return confirm("Attenzione: \n" + 
			      	   			"Il batch aggiorna la validità curriculum dei lavoratori. \n" +
			      	   			"Vuoi proseguire?");
			      	   	}
			      	   	if (document.form1.tipoBatch.value == "8") {
			      	   		return confirm("Attenzione: \n" + 
			      	   			"Il batch cancella i movimenti doppi dalla tabella di appoggio. \n" +
			      	   			"Vuoi proseguire?");
			      	   	}
			      	    if (document.form1.tipoBatch.value!="3" && document.form1.tipoBatch.value!="4" && document.form1.tipoBatch.value!="9") {
			      	   		return confirm("Attenzione: \n" + 
			      	   			"Il batch ricalcola la situazione occupazionale dei lavoratori \n" +
			      	   			"in corrispondenza ad eventuali movimenti coinvolti. \n" +
			      	   			"Vuoi proseguire?");
			      	   	}	 
			      	    if (document.form1.tipoBatch.value=="3") {
			      	   		return confirm("Attenzione: \n" + 
			      	   			"Il batch modifica lo stato occupazionale dei lavoratori da \n" +
			      	   			"\"OCCUPATO: IN CERCA DI ALTRA OCCUPAZIONE\" ad \"OCCUPATO: OCCUPATO\" \n" + 
			      	   			"in corrispondenza di fine validità del curriculum. \n" +
			      	   			"Vuoi proseguire?");
			      	   		} 
			      	    if (document.form1.tipoBatch.value=="4") {
			      	   		return confirm("Attenzione: \n" + 
			      	   			"Il batch modifica lo stato occupazionale dei lavoratori da \n" +
			      	   			"\"OCCUPATO: OCCUPATO\" ad \"OCCUPATO: IN CERCA DI ALTRA OCCUPAZIONE\" \n" + 
			      	   			"in corrispondenza di inizio validità del curriculum. \n" +
			      	   			"Vuoi proseguire?");
			      	   		} 
			      	   	 if (document.form1.tipoBatch.value=="9") {
			      	   		return confirm("Attenzione: \n" + 
			      	   			"Il batch chiude le iscrizioni al collocamento mirato dei lavoratori che hanno \n" +
			      	   			"superato l'età massima di iscrizione. \n" +
			      	   			"Vuoi proseguire?");
			      	   		} 
			      	   	 return true;
			     }//switch (intTipoBatch)	
			
   
   }

  	function toggleVisOpzioni(tipoBatch, data, elem1, elem2, elem3) {
      var obj1 = document.getElementById(elem1);
      var obj2 = document.getElementById(elem2);
      var obj3 = document.getElementById(elem3);
      var objTblBatchAll = document.getElementById("tblBatchAll");
      var objTblBatch8 = document.getElementById("tblBatch8");
      var objTblBatch10 = document.getElementById("tblBatch10");
      var objTblBatch11 = document.getElementById("tblBatch11");
      var objTblBatch13 = document.getElementById("tblBatch13");
      intTipoBatch = -1;
      if(tipoBatch)
    	  if(tipoBatch.value)
		intTipoBatch = parseInt(tipoBatch.value)
      switch (intTipoBatch)	{
	case 11:
		obj1.style.display = "none";
	  	obj2.style.display = "none";
	 	obj3.style.display = "none";
	 	objTblBatchAll.style.display = "none";
	 	objTblBatch8.style.display = "none";
	 	objTblBatch10.style.display = "none";
	 	objTblBatch11.style.display = "inline";
	 	objTblBatch13.style.display = "none";
	  break;      
	case 9:
		obj1.style.display = "none";
	  	obj2.style.display = "none";
	 	obj3.style.display = "none";
	 	objTblBatchAll.style.display = "inline";
	 	objTblBatch8.style.display = "none";
	 	objTblBatch10.style.display = "none";
	 	objTblBatch11.style.display = "none";
	 	objTblBatch13.style.display = "none";
	  break;
	case 8:
		obj1.style.display = "none";
	  	obj2.style.display = "none";
	 	obj3.style.display = "none";
	 	objTblBatchAll.style.display = "none";
	 	objTblBatch8.style.display = "inline";
	    objTblBatch10.style.display = "none";
	    objTblBatch11.style.display = "none";
	    objTblBatch13.style.display = "none";
	  break;
	case 10:
		obj1.style.display = "none";
	  	obj2.style.display = "none";
	 	obj3.style.display = "none";
	 	objTblBatchAll.style.display = "none";
	 	objTblBatch8.style.display = "none";
	  	objTblBatch10.style.display = "inline";
	  	objTblBatch11.style.display = "none";
	  	objTblBatch13.style.display = "none";
	  break;
	case 12:
	  	objTblBatch11.style.display = "none";
		objTblBatchAll.style.display = "inline";
		objTblBatch8.style.display = "none";
		objTblBatch10.style.display = "none";
		obj3.style.display = "none";
		obj1.style.display = "none";
		obj2.style.display = "inline";
		objTblBatch13.style.display = "none";
	  break;
	case 13:
		obj1.style.display = "none";
	  	obj2.style.display = "none";
	 	obj3.style.display = "none";
	 	objTblBatchAll.style.display = "none";
	 	objTblBatch8.style.display = "none";
	 	objTblBatch10.style.display = "none";
	 	objTblBatch11.style.display = "none";
	 	objTblBatch13.style.display = "inline";
	  break;
	default:
		objTblBatch13.style.display = "none";
		objTblBatch11.style.display = "none";
		objTblBatchAll.style.display = "inline";
		objTblBatch8.style.display = "none";
		objTblBatch10.style.display = "none";
		obj3.style.display = "inline";
		if (tipoBatch.value != "" && tipoBatch.value != "6") {
			obj3.style.display = "none";
		}
		if(tipoBatch.value == "3" || tipoBatch.value == "4" || tipoBatch.value == "7") {
		  obj1.style.display = "none";
		  obj2.style.display = "none";
		} else {
		  if (tipoBatch.value == "5") {
		  	obj1.style.display = "none";
		  } else {
		  	obj1.style.display = "inline";
		  }
		  obj2.style.display = "inline";
		}
		if(tipoBatch.value == "6") {
			data.value = "01/01/" + annoAttuale;
		}
	}//switch (intTipoBatch)	
/*
       if(tipoBatch.value == '9') {
      	obj1.style.display = "none";
      	obj2.style.display = "none";
     	obj3.style.display = "none";
     	objTblBatchAll.style.display = "inline";
     	objTblBatch8.style.display = "none";
     	objTblBatch10.style.display = "none";
       }
       else{
	       if(tipoBatch.value == '8') {
	      	obj1.style.display = "none";
	      	obj2.style.display = "none";
	     	obj3.style.display = "none";
	     	objTblBatchAll.style.display = "none";
	     	objTblBatch8.style.display = "inline";
	        objTblBatch10.style.display = "none";
	       }
	       else 
	       if(tipoBatch.value == '10') {
	      	obj1.style.display = "none";
	      	obj2.style.display = "none";
	     	obj3.style.display = "none";
	     	objTblBatchAll.style.display = "none";
	     	objTblBatch8.style.display = "none";
	      	objTblBatch10.style.display = "inline";
	       }
	       else {
	      	  objTblBatchAll.style.display = "inline";
	      	  objTblBatch8.style.display = "none";
	      	  objTblBatch10.style.display = "none";
		      obj3.style.display = "inline";
		      if (tipoBatch.value != "" && tipoBatch.value != "6") {
		      	obj3.style.display = "none";
		      }
		      if(tipoBatch.value == "3" || tipoBatch.value == "4" || tipoBatch.value == "7") {
		        obj1.style.display = "none";
		        obj2.style.display = "none";
		      } else {
		        if (tipoBatch.value == "5") {
		        	obj1.style.display = "none";
		        } else {
		        	obj1.style.display = "inline";
		        }
		        obj2.style.display = "inline";
		      }
		      if(tipoBatch.value == "6") {
		      	data.value = "01/01/" + annoAttuale;
		      }
		 }
	  }
 	  */
   }
   
-->
</script>
</head>

<body class="gestione" onload="rinfresca();toggleVisOpzioni(document.form1.tipoBatch,document.form1.DATABATCH2,'opzioni1','opzioni2','opzioni3')">
<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_AvviaBatch"/>
</font>
<br/>

<%out.print(htmlStreamTop);%>
<p class="titolo">Gestione avvio batch</p>

<af:form name="form1" method="POST" action="AdapterHTTP" onSubmit="controllaDati() && conferma()">
<af:textBox type="hidden" name="PAGE" value="AvviaBatchPage" />

<table class="main">
<tr>
	<td colspan="2">
		<table>
			<tr><td>
				<div id="tblBatchAll">
				<table>
					<tr>
						<td class="etichetta" nowrap>Alla data</td>
						<td class="campo" colspan="3" nowrap><af:textBox type="date" classNameBase="input" name="DATABATCH2" title="Data di scansione batch" size="12" maxlength="10"
							validateOnPost="true" value="<%=dataScansione2%>" />&nbsp;&nbsp;
						</td>
					</tr>
				</table>
				</div>
			</td></tr>
		  <tr>
			<td>			
				<div id="tblBatch8">
				<table>
					<tr>
						<td class="etichetta" nowrap>Data inserimento DA</td>
						<td class="campo" colspan="3" nowrap><af:textBox type="date" classNameBase="input" name="DATAINSDA" title="Data di inserimento inizio" size="12" maxlength="10"
							validateOnPost="true" value="<%=dataInserimentoDa %>" />&nbsp;&nbsp;</td>
						<td class="etichetta" nowrap> A</td>
						<td class="campo" colspan="3" nowrap><af:textBox type="date" classNameBase="input" name="DATAINSA" title="Data di inserimento fine" size="12" maxlength="10"
							validateOnPost="true" value="<%=dataInserimentoA %>" />&nbsp;&nbsp;</td>
					</tr>
				</table>
				</div>
				<div id="tblBatch10">
				<table>
					<tr>
						<td class="etichetta" nowrap>Codice Fiscale</td>
						<td class="campo" colspan="3" nowrap>
						<af:textBox type="text" classNameBase="input"
							name="CODICEFISCALE" title="Data di inserimento inizio"
							size="12" maxlength="10" validateOnPost="true"
							value="<%=codicefiscale %>" />&nbsp;&nbsp;</td>
						<td class="etichetta" nowrap>Data Presa in Carico DA</td>
						<td class="campo" colspan="3" nowrap>
						<af:textBox type="date" classNameBase="input" name="DATASIFERDA"
							title="Data di inizio" size="12" maxlength="10"
							validateOnPost="true" value="<%=dataSiferDa %>" />&nbsp;&nbsp;</td>
						<td class="etichetta" nowrap>A</td>
						<td class="campo" colspan="3" nowrap>
						<af:textBox type="date" classNameBase="input" name="DATASIFERA"
							title="Data di fine" size="12" maxlength="10"
							validateOnPost="true" value="<%=dataSiferA %>" />&nbsp;&nbsp;</td>
					</tr>
				</table>
				</div>
				<div id="tblBatch11">
				<table>
					<tr>
						<td class="etichetta" nowrap>Numero Max Mesi</td>
						<td class="campo" colspan="3" nowrap>
							<af:textBox classNameBase="input" title="Numero Max Mesi" type="integer"  
							name="numMaxMesi" value="<%=numMaxMesi%>" validateOnPost="true" 
		              		size="3" maxlength="2" />						
						</td>
					</tr>
				</table>
				</div>
				<div id="tblBatch13">
				<table>
					<tr>
						<td class="etichetta" nowrap>Numero Lavoratori da processare (max 500)</td>
						<td class="campo" colspan="3" nowrap>
							<af:textBox classNameBase="input" title="Numero Lavoratori da processare" type="integer"  
							name="numLavDaProcessare" value="<%=numLavoratoriDaProcessare%>" validateOnPost="true" 
		              		size="3" maxlength="3" />						
						</td>
					</tr>
				</table>
				</div>				
			</td>
		  </tr>
		</table>
   </td>
</tr>

<tr>
  <td class="etichetta" nowrap>Tipo Batch</td>
  <td class="campo" colspan="3" nowrap>
    <af:comboBox classNameBase="input" name="tipoBatch" title="Tipo batch da avviare" 
      disabled="<%=String.valueOf(!canModify)%>" required="true" addBlank="true" selectedValue="<%=tipoBatch%>" onChange="toggleVisOpzioni(this,document.form1.DATABATCH2,'opzioni1','opzioni2','opzioni3');controllo();">
      <option value="0" <% if((tipoBatch!=null) && tipoBatch.equals("0")){%>selected="selected"<%}%>>Batch per cessare TD (senza mov cess) alla data fine</option>
      <option value="1" <% if((tipoBatch!=null) && tipoBatch.equals("1")){%>selected="selected"<%}%>>Batch per i movimenti futuri</option>              
      <option value="3" <% if((tipoBatch!=null) && tipoBatch.equals("3")){%>selected="selected"<%}%>>Batch per la fine della validit&agrave; curriculum</option>
      <option value="4" <% if((tipoBatch!=null) && tipoBatch.equals("4")){%>selected="selected"<%}%>>Batch per l'inizio della validit&agrave; curriculum</option>
      <option value="5" <% if((tipoBatch!=null) && tipoBatch.equals("5")){%>selected="selected"<%}%>>Batch per la fine della mobilit&agrave;</option>
      <option value="6" <% if((tipoBatch!=null) && tipoBatch.equals("6")){%>selected="selected"<%}%>>Batch per movimenti a cavallo anni</option>
      <!--<option value="7" <% //if((tipoBatch!=null) && tipoBatch.equals("7")){%>selected="selected"<%//}%>>Batch per l'aggiornamento della validit&agrave; curriculum</option>-->
      <option value="8" <% if((tipoBatch!=null) && tipoBatch.equals("8")){%>selected="selected"<%}%>>Batch per la cancellazione movimenti doppi da validare</option>
      <option value="9" <% if((tipoBatch!=null) && tipoBatch.equals("9")){%>selected="selected"<%}%>>Batch per la chiusura iscrizioni CM per superamento età</option>
      <option value="10" <% if("10".equalsIgnoreCase(tipoBatch)){%>selected="selected"<%}%>>Batch Invio Sifer</option>
      <option value="11" <% if("11".equalsIgnoreCase(tipoBatch)){%>selected="selected"<%}%>>Batch di aggiornamento dell'avvio a selezione da movimento</option>
      <option value="12" <% if((tipoBatch!=null) && tipoBatch.equals("12")){%>selected="selected"<%}%>>Batch per fine sospensione</option>
      <option value="13" <% if((tipoBatch!=null) && tipoBatch.equals("13")){%>selected="selected"<%}%>>Batch ricalcola impatti</option>
    </af:comboBox>    
  </td>
</tr>

<tr>
  <td colspan="2">&nbsp;</td>
</tr>

<tr>
  <td colspan="2" nowrap>
      <table class="main">
	  	<tr><td>
	  	<div id="opzioni1">
	  	 <table>
		  	<tr>
		  	<td nowrap class="etichetta" nowrap>Considera solo i movimenti<br/>non ancora trattati dal batch</td>
			<td class="campo" nowrap>
			  <input type="checkbox" name="nonTrattati" value="S" onClick="javascript:gestisciCheck(this);" checked/>
			</td>
			</tr>
		 </table>
		 </div>
		 </td></tr>
		<tr><td>
		<div id="opzioni2">
		<table>
			<tr>
			<td nowrap class="etichetta" nowrap>Forza inserimento in caso di presenza DID/MOBILITA' e stato occupazionale manuale</td>
			<td class="campo" nowrap>
			  <%if(forzaInserimento){%>
			  		<input type="checkbox" name="FORZA_INSERIMENTO" value="S" onClick="javascript:gestisciCheck(this);" checked/>
			  <%}else{%>
				  	<input type="checkbox" name="FORZA_INSERIMENTO" value="N" onClick="javascript:gestisciCheck(this);"/>
			  <%}%>
			</td>
			</tr>
		</table>
		</div>
		
		<div id="opzioni3">
		<table>
			<tr>
			<td colspan="2" class="campo">
			<input type="hidden" name="SoccAnno" value="N"/>
			</td>
			</tr>
		</table>
		</div>
		
		</td></tr>	
	  </table>
  </td>
</tr>

<tr>
  <td colspan="2">&nbsp;</td>
</tr>
</table>

<table class="main">
<tr>
  <td colspan="4">
    <div class="sezione2">
      <img id='tendina' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendina(this,'log');"/>&nbsp;File di log del batch
      <img id='aggiorna' alt="Aggiorna log" src="../../img/add.gif" onclick="javascript:aggiornaLog();"/>    
    </div>
  </td>
</tr>

<tr>
  <td colspan="4" id="log" style="display:inline">
    <textArea  class="textareaEdit" name="strLog" cols="80" rows="15"><%=strLog%></textarea>
  </td>
</tr>
<tr>
  <td><br/></td>
</tr>
<tr>
  <td colspan="4">
     <%if(canModify){%>
  		 <input class="pulsante" type="submit" name="avvia" value="Avvia" />
     <%}%>
     <input class="pulsante" type="button" onClick="window.close();" name="chiudi" value="Chiudi"/>
  </td>
</tr>
</table>
</af:form>

<%out.print(htmlStreamBottom);%>
</body>
</html>