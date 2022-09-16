<script language="JavaScript">
  function checkDateWithNowDate (strdata) {
    annoVar = strdata.substr(6,4);
    meseVar = strdata.substr(3,2);
    giornoVar = strdata.substr(0,2);
    dataVarInt = parseInt(annoVar + meseVar + giornoVar, 10);
    var dLocal = new Date();
    annoVarOdierno = dLocal.getYear().toString();
    meseVarOdierno = (dLocal.getMonth() + 1).toString();
    if (meseVarOdierno.length == 1) {
      meseVarOdierno = '0' + meseVarOdierno;
    }
    giornoVarOdierno = dLocal.getDate().toString();
    if (giornoVarOdierno.length == 1) {
      giornoVarOdierno = '0' + giornoVarOdierno;
    }
    var dataLocalOdierna = parseInt(annoVarOdierno + meseVarOdierno + giornoVarOdierno, 10);
    if (dataVarInt < dataLocalOdierna) {
      return 1;
    }
    else {
      if (dataVarInt > dataLocalOdierna) {
        return 2;
      }
      else {
        return 0;
      }
    }
  }
  
  function compareDate(d1,d2)
  {  
    if(d1.length<10) {
      alert("La data " + d1 + " non è nel formato corretto\ninserire \'gg/mm/aaaa\'");
      return false;
    }
    if(d2.length<10) {
      alert("La data " + d2 + " non è nel formato corretto\ninserire \'gg/mm/aaaa\'");
      return false;
    }

    //costruisco la prima data
    d1giorno=d1.substring(0,2);
    d1mese=d1.substring(3, 5)-1; //il conteggio dei mesi parte da zero :P
    d1anno=d1.substring(6,10);
    data1=new Date(d1anno, d1mese, d1giorno);
    
    //costruisce la seconda data
    d2giorno=d2.substring(0,2);  
    d2mese=d2.substring(3, 5)-1; //il conteggio dei mesi parte da zero :P
    d2anno=d2.substring(6,10);
    data2=new Date(d2anno, d2mese, d2giorno);
    
    if (data1 < data2) {
    	return -1;
    } 
    else if (data1 > data2){
    	return  1;
    }
    else {
    	return  0;
    }
  }//compareDate(_,_)
  
  function calcolaEta(dataNas)
  { var anniDif = 0;
    //costruisco la prima data
    d1giorno=parseInt(dataNas.substr(0,2),10);
    d1mese=parseInt(dataNas.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
    d1anno=parseInt(dataNas.substr(6,4),10);
    data1=new Date(d1anno, d1mese, d1giorno);
    
    //costruisce la seconda data
    var dLocal = new Date();
    dLgiorno= dLocal.getDate();
    dLmese  = dLocal.getMonth();
    dLanno  = dLocal.getFullYear();
    
    if (dLocal < data1) {
        alert("La data di nascita \'"+dataNas+"\' inserita\nè posteriore alla data di oggi");
        return -1;
    }
    
    //Calcolo gli anni
    anniDif = dLanno - d1anno;
    if (dLmese < d1mese ) {
    	anniDif--;
    } 
    else if (d1mese == dLmese ) {
    	if (dLgiorno < d1giorno)
    	  anniDif--;
    }
    return anniDif;
    
  }//end function




  //La seguente funzione è incompleta
  /*
  function timeBetweenDate(date1,date2,typeReturn)
  { var anniDif = 0;
    var mesiDif = 0;
    var giornDif= 0;
    
    if(date1.length<10) {
      alert("La data "+data1+" non è nel formato corretto\ninserire \'gg/mm/aaaa\'");
      return false;
    }
    if(date2.length<10) {
      alert("La data "+data2+" non è nel formato corretto\ninserire \'gg/mm/aaaa\'");
      return false;
    }
   
    //costruisco la prima data
    d1giorno=parseInt(data1.substr(0,2),10);
    d1mese=parseInt(data1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
    d1anno=parseInt(data1.substr(6,4),10);
    //data1=new Date(d1anno, d1mese, d1giorno);
    
    //costruisce la seconda data
    d2giorno=parseInt(data2.substr(0,2),10);
    d2mese=parseInt(data2.substr(3,2),10)-1;
    d2anno=parseInt(data2.substr(6,4),10);
    //data2=new Date(d2anno, d2mese, d2giorno);
    
    if(typeReturn =='G' || typeReturn =='g') {
     
    }
    else if(typeReturn =='M' || typeReturn =='m') {
      //Calcolo i mesi
      anniDif = d2anno - d1anno;
      
      if (d1mese == d2mese ) {
      	mesiDif = (anniDif*12);
      	if (d2giorno < d1giorno) {
      	   mesiDif--;
      	}
      }
      else {
      	if (d2mese < d1mese ) {
      	    anniDif --;
        }
        mesiDif = d2mese + (12-d1mese) + 12*anniDif;
        
      	if (d2giorno < d1giorno) {
      	  mesiDif --;
      	}
      	
      } 
      
    }
    else if(typeReturn =='A' || typeReturn =='a') {
      //Calcolo gli anni
      anniDif = d2anno - d1anno;
      if (d2mese < d1mese ) {
      	anniDif --;
      } 
      else if (d1mese == d2mese ) {
      	if (d2giorno < d1giorno)
      	  anniDif --;
      }
      return anniDif;
    }
    else {
    	alert("Errore:\nil terzo parametro della funzione timeBetweenDate()\npuò assumere solo uno dei seguenti vaolri:\n- G: giorni\n- M:mesi\n- A:anni");
    	return -1;
    }
  }*/
</script>
