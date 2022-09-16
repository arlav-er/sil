<script language="javascript">
 /*
  * Metodo per il calcolo dei giorni di ritardo tra due date.
  * DataComunicazione (data1) - datainiziomov (data2)
  */
  function getNumRitardo(data1, data2){
    var gd1;
    var gd2;
    var dayDiff = 0;

    data1 = data1.substr(3,2) + "/" + data1.substr(0,2) + "/" + data1.substr(6,4);
    data2 = data2.substr(3,2) + "/" + data2.substr(0,2) + "/" + data2.substr(6,4);

    gd1 = new Date(data1);
    gd2 = new Date(data2);

    //Num gg che intercorrono tra due date
    dayDiff = (gd1.valueOf() - gd2.valueOf())/(1000*60*60*24);
    
    return dayDiff;
  }

  /*
   * Calcolo effettivo dei giorni che intercorrono tra due date e settaggio del
   * campo con il rispettivo valore
   * obj: data inizio
   * elem: campo che conterrà la differenza appena calcolata
   * range: numero massimo di ritardo memorizzati nella TS_GENERALE
   */
  function calcolaDiffGiorni(obj, elem, range, oggettoInSessione){
    var num;
    var nl;
    var codTipoAz = document.Frm1.CODTIPOAZIENDA.value;
    var confrontoRitardo = 0;
    if ((obj.value != "") && !oggettoInSessione){
      //Formattazione della data:gg/mm/aaaa
      checkFormatDate(obj);
      giornoMov=obj.value.substring(0,2);  
	  meseMov=obj.value.substring(3, 5)-1; //il conteggio dei mesi parte da zero :P
	  annoMov=obj.value.substring(6,10);
      dataMov = new Date(annoMov, meseMov, giornoMov);
      var dataComunicazioneMov = "<%=datComunicaz%>";
      var annoSucc = annoMov;
      var meseSucc = meseMov;
      if (meseMov == 11) {
	  	annoSucc = annoSucc + 1;
		dataSuccLegge2006 = new Date(annoSucc, "00", "20");
	  }
	  else {
		meseSucc = meseSucc + 1;
		var strMeseSucc = meseSucc<10?("0"+meseSucc):""+meseSucc;
		dataSuccLegge2006 = new Date(annoSucc, strMeseSucc, "20");
	  }
      objDataCom  = new Date(dataComunicazioneMov.substring(6,10) , dataComunicazioneMov.substring(3, 5)-1, dataComunicazioneMov.substring(0,2));
      <% if(codMonoMovDich.equals("O")) {%>
        num = parseInt(getNumRitardo('<%=datComunicaz%>', obj.value));
        //alert(num);
        <% if( (!codAtecoUAz.equals("")) && (codAtecoUAz.length() > 1) && 
              (codAtecoUAz.substring(0,2).equals("45")) && (codTipoMov.equalsIgnoreCase("AVV")) ) { %>
			  dataRitardoEdile = new Date("2006", "07", "12");
			  dataFineRitardoEdile = new Date("2006", "11", "31");
			  if (dataMov >= dataRitardoEdile && dataMov <= dataFineRitardoEdile) {
			    confrontoRitardo = 1;
			    if(codTipoAz.toUpperCase() != "AGR") {
	           		nl = getNumGiorniNL('<%=datComunicaz%>', obj.value, num);
	           		num = num - nl;
	           	}
			    if (num < 0) num = 0;
			  	elem.value = num;
	          	if(objDataCom >= dataMov) alert("Comunicazione in ritardo > 1 giorno (edilizia - Legge 248/06).");
	          }     
      	<%}%>
      	if (confrontoRitardo == 0) {
      		if(codTipoAz.toUpperCase() != "AGR") {
	           nl = getNumGiorniNL('<%=datComunicaz%>', obj.value, num);
	           num = num - nl;
	       	}
      		if (num < 0) num = 0;
	        elem.value = num;
      		dataFinanziaria06 = new Date("2007", "00", "01");
      		//CONTROLLI AVV PER LE AGENZIE DIVERSE DA QUELLE DI SOMMINISTRAZIONE
      		if( ('<%=codTipoMov%>' == 'AVV') && (codTipoAz.toUpperCase() != 'INT') && (dataMov >= dataFinanziaria06) ) {
      			if(objDataCom >= dataMov) alert("La comunicazione è pervenuta in ritardo");
      		}
      		else {
      			//CONTROLLI CES PER LAVORATORI IN COLLOCAMENTO MIRATO
      			if ( ('<%=codTipoMov%>' == 'CES') && (dataMov >= dataFinanziaria06) && (document.Frm1.LAVORATOREDISABILE != null) && (document.Frm1.LAVORATOREDISABILE.value == 'true') ) {
      				if (num > 10) alert("La comunicazione è pervenuta in ritardo");
				}
      			else {
      				//CONTROLLI PER AGENZIE DI SOMMINISTRAZIONE (AVV, PRO/TRA, CES)
	      			if( (codTipoAz.toUpperCase() == 'INT') && (dataMov >= dataFinanziaria06) ) {
	      				if(objDataCom > dataSuccLegge2006) alert("La comunicazione è pervenuta in ritardo");
	      			}
	      		    else {
	      		    	//CONTROLLI CES PER AGENZIE DIVERSE DA QUELLE DI SOMMINISTRAZIONE
	      		    	if ( ('<%=codTipoMov%>' == 'CES') && (dataMov >= dataFinanziaria06) && (codTipoAz.toUpperCase() != 'INT') ) {
		      				if (num > 5) alert("La comunicazione è pervenuta in ritardo");
						}			
		      			else {
		      				//CONTROLLI PRO/TRA PER LE AGENZIE DIVERSE DA QUELLE DI SOMMINISTRAZIONE
		      				if ( ('<%=codTipoMov%>' == 'PRO' || '<%=codTipoMov%>' == 'TRA') && (dataMov >= dataFinanziaria06) && (codTipoAz.toUpperCase() != 'INT') ) {
			      				if (num > 5) alert("La comunicazione è pervenuta in ritardo");
							}
		      				else {
		      					if( (dataMov < dataFinanziaria06) && (num > range) ) alert("La comunicazione è pervenuta in ritardo");
		      				}	
		      			}
		      		}	
	      		}
      		}
	   	}	
      <%} else { %>
            elem.value = "";
       <% } %>  
    }
  }

function calcolaDiffGiorni2(dataCom, obj, elem, range){
    var num;
    var nl;
    if ((obj.value != "") && (dataCom.value != "")){
      //Formattazione della data:gg/mm/aaaa
      checkFormatDate(obj);
      checkFormatDate(dataCom);
      
        num = parseInt(getNumRitardo(dataCom.value, obj.value));
        //alert(num);
      
        if(codTipoAzienda != "AGR"){
          nl = getNumGiorniNL(dataCom.value, obj.value, num);
          //alert(nl);
          num = num - nl;
        }

        if (num < 0) num = 0;
        elem.value = num;
        if(num > range) alert("La comunicazione è pervenuta in ritardo");
    }
  }


  /*
  * Metodo per il calcolo delle domeniche che intercorrono tra due date.
  * bassa; prende inoltre in input il numero di giorni che intercorrono tra le due date.
  */ 
	function getNumGiorniNL(dat1, dat2, diff){
		var count = 0;
    var i = 0;
    var giorno = 0;
    
    var unGiornoMil = 1000*60*60*24;
    var unaSettimanaMil = 1000*60*60*24*7;
    var initialDate = new Date("01/01/1970");
    var inizio = initialDate.valueOf();
    
    var d1 = dat1.substr(3,2) + "/" + dat1.substr(0,2) + "/" + dat1.substr(6,4);
    var d2 = dat2.substr(3,2) + "/" + dat2.substr(0,2) + "/" + dat2.substr(6,4);

    var tmpMin = new Date(d1); 
    var tmpMax = new Date(d2);

    //Numero relativo alla data
    dat1 = dat1.substr(6,4) + dat1.substr(3,2) + dat1.substr(0,2);
    dat2 = dat2.substr(6,4) + dat2.substr(3,2) + dat2.substr(0,2);
    
    if(dat1 > dat2){
      tmpMin = new Date(d2); 
      tmpMax = new Date(d1);
    }

    tempdMin = tmpMin.valueOf();
    tempdMax = tmpMax.valueOf();
    
      
    while(tempdMin <= tempdMax){
      giorno = tmpMin.getDay();
      if ((giorno == 0) && (i != 0)){
        count += 1;
        break;
      }
      //Incremento di un giorno alla volta tmpMin
      tempdMin = tempdMin + unGiornoMil;
      tmpMin = new Date(tempdMin-inizio);
      i++;
    }

    if(count > 0){
      while(tempdMin <= tempdMax){
        //Incremento di una settimana alla volta
        tempdMin = tempdMin + unaSettimanaMil;
        if (tempdMin <= tempdMax){
          count = count + 1;
        }
      }
    }  
 	return count;
  }


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

  function callBackDateFunction(){
	calcolaDiffGiorni(document.Frm1.datInizioMov,document.Frm1.NUMGGTRAMOVCOMUNICAZIONE,varRange);
  }
  
  -->
</script>