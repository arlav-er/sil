<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                   
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String datInizioMov = "";  
String funcAggiorna = "";
String dataFine = "";
String num = "";
datInizioMov = serviceRequest.getAttribute("dataInizio").toString();
funcAggiorna = serviceRequest.getAttribute("funzioneDiAggiornamento").toString();
if(serviceRequest.getAttribute("interazione") != null){ 
  num = serviceRequest.getAttribute("num").toString();
  dataFine = UtilityNumGGTraDate.sommaGiorni(datInizioMov,Integer.parseInt(num)-1);
}

//Oggetti per l'applicazione dello stile
String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Data fine movimento</title>

<script language="javascript">
<!--
//*** Gestione anno 2000: anno rottura secolo ***
  var breakY2K_nn  =    20;
  var breakY2K_nnn =   920;

function checkFormatDate(objName) {
  var strDate;
  var strDateArray;
  var strDay;
  var strMonth;
  var strYear;
  var intday;
  var intMonth;
  var intYear;
  var booFound = false;
  var datefield = objName;
  var strSeparatorArray = new Array("-"," ","/",".");
  var intElementNr;
  var err = 0;
  var strMonthArray = new Array(12);
  strMonthArray[0]  = "Gen";
  strMonthArray[1]  = "Feb";
  strMonthArray[2]  = "Mar";
  strMonthArray[3]  = "Apr";
  strMonthArray[4]  = "Mag";
  strMonthArray[5]  = "Giu";
  strMonthArray[6]  = "Lug";
  strMonthArray[7]  = "Ago";
  strMonthArray[8]  = "Set";
  strMonthArray[9]  = "Ott";
  strMonthArray[10] = "Nov";
  strMonthArray[11] = "Dic";

  strDate = datefield.value;
  if (strDate.length < 1) {
     return true;
  }
  for (intElementNr = 0; intElementNr < strSeparatorArray.length; intElementNr++) {
     if (strDate.indexOf(strSeparatorArray[intElementNr]) != -1) {
        strDateArray = strDate.split(strSeparatorArray[intElementNr]);
        if (strDateArray.length != 3) {
           err = 1;
           return false;
        }
        else {
           strDay = strDateArray[0];
           strMonth = strDateArray[1];
           strYear = strDateArray[2];
        }
        booFound = true;
     }
  }
  if (booFound == false) {
     if (strDate.length>5) {
        strDay = strDate.substr(0, 2);
        strMonth = strDate.substr(2, 2);
        strYear = strDate.substr(4);
     }
  }
  //if (strYear.length == 2) {
  //   strYear = '20' + strYear;
  //}
  intday = parseInt(strDay, 10);
  if (isNaN(intday)) {
     err = 2;
     return false;
  }
  intMonth = parseInt(strMonth, 10);
  if (isNaN(intMonth)) {
     for (i = 0;i<12;i++) {
        if (strMonth.toUpperCase() == strMonthArray[i].toUpperCase()) {
           intMonth = i+1;
           strMonth = strMonthArray[i];
           i = 12;
        }
     }
     if (isNaN(intMonth)) {
        err = 3;
        return false;
     }
  }
	
 	if (strYear.length > 4)
  		return false;
  intYear = parseInt(strYear, 10);
  if (isNaN(intYear)) {
     err = 4;
     return false;
  }
  else {
    if (intYear < 100) {
       if (intYear > breakY2K_nn) {
       	   intYear = intYear + 1900;
       }
       else {
    	   intYear = intYear + 2000;
       }
    }
    else if (intYear < 1000) {
       if (intYear > breakY2K_nnn) {
       	   intYear = intYear + 1000;
       }
       else {
    	   intYear = intYear + 2000;
       }
    }
  }

  if (intMonth>12 || intMonth<1) {
     err = 5;
     return false;
  }
  if ((intMonth == 1 || intMonth == 3 || intMonth == 5 || intMonth == 7 || intMonth == 8 || intMonth == 10 || intMonth == 12) && (intday > 31 || intday < 1)) {
     err = 6;
     return false;
  }
  if ((intMonth == 4 || intMonth == 6 || intMonth == 9 || intMonth == 11) && (intday > 30 || intday < 1)) {
     err = 7;
     return false;
  }
  if (intMonth == 2) {
     if (intday < 1) {
        err = 8;
        return false;
     }
     if (LeapYear(intYear) == true) {
        if (intday > 29) {
           err = 9;
           return false;
        }
     }
     else {
        if (intday > 28) {
           err = 10;
           return false;
        }
     }
   }
   //datefield.value = intday + " " + strMonthArray[intMonth-1] + " " + strYear;
   datefield.value = (intday<10?"0":"")   + intday   + "/"
                   + (intMonth<10?"0":"") + intMonth + "/" + intYear;
      
   return true;
}


function LeapYear(intYear) {
   if (intYear % 100 == 0) {
      if (intYear % 400 == 0) { return true; }
   }
   else {
      if ((intYear % 4) == 0) { return true; }
   }
   return false;
}
-->
</script>

<script language="javascript">    
  function aggiorna(){
    window.opener.<%=funcAggiorna%>();
  }

  function calcolaData(data){
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
  	
  	if(isNaN(document.Form1.addNum.value)){
		alert("Attenzione: il valore inserito non è un numero!");
	} else {	
    	if(document.Form1.addNum.value == "") document.Form1.addNum.value = "0";
    	var f = "AdapterHTTP?PAGE=MovSceltaDataFine&funzioneDiAggiornamento=valorizzaCampiData&dataInizio=" + document.Form1.datInizioMov.value + "&num=" + document.Form1.addNum.value + "&interazione=S";
    	setWindowLocation(f);
  	}
  } 
</script>
</head>
<body class="gestione">
      <af:form name="Form1" dontValidate="true">
    <br/>
    <table class="main" cellspacing="0"  cellpadding="0" width="96%" border="0">
      <tr>
        <td class="titolo" align="center">Data fine movimento</td>
      </tr>
    </table>
    <br/>
    <%out.print(htmlStreamTop);%>
    <table class="main" cellspacing="0"  cellpadding="0" width="96%" border="0">
        <tr>
          <td class="etichetta" nowrap>Data inizio</td>
          <td class="campo">
            <input type="hidden" name="func" value="<%=funcAggiorna%>" >
            <af:textBox classNameBase="input" type="date" name="datInizioMov" required="false" title="Data Inizio Assunzione"  size="12" maxlength="10" value="<%=datInizioMov%>" readonly="true"/>
          </td>
          <td class="etichetta" nowrap>Giorni da aggiungere</td>
          <td class="campo">
            <af:textBox classNameBase="input" name="addNum" title="Numero giorni" required="false" type="number" size="12" maxlength="100" value="<%=num%>" />
          </td>
        </tr>

        <tr>
          <td class="etichetta" nowrap>Data fine</td>
          <td class="campo" colspan="3">
            <af:textBox classNameBase="input" name="dataFine" title="Data Fine" required="false" size="12" maxlength="100" value="<%=dataFine%>" />
            &nbsp;&nbsp;
            <input type="button" class="pulsanti" name="calcola" value="Calcola" onClick="javascript:calcolaData();"/>
          </td>
        </tr>
        <br/>
        <tr>
          <td align="center" colspan="4">
            <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/>
            &nbsp;&nbsp;
            <input type="button" class="pulsanti" name="aggiorna" value="Aggiorna" onClick="javascript:window.aggiorna();"/>
          </td>
        </tr>
    </table>
    <%out.print(htmlStreamBottom);%>
</af:form>
<script language="javascript">
  checkFormatDate(document.Form1.datInizioMov);
</script>
</body>
</html>  