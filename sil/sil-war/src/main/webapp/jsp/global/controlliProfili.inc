<script language="JavaScript">

function checkCodiceFiscale(inputName) {

  if (document.forms[0].CODTIPO.value != 'S') {
  	return true;
  }
  else {
  	var cfObj = eval("document.forms[0]." + inputName);
  	cfObj.value=cfObj.value.toUpperCase();
  	cf=cfObj.value;
  	ok=true;
  	msg="";
  	if (cf.length==16 ) {
    	for (i=0; i<16 && ok; i++) {
        	c=cf.charAt(i);
            if (i>=0 && i<=5){
            	ok=!isDigit(c);
               	msg="Errore nei primi sei caratteri del codice fiscale:\ndevono essere delle lettere";
           	} else if  (i==6 || i==7) { 
               	ok=isDigit(c);
               	msg="Errore nel settimo o nell'ottavo carattere del codice fiscale:\ndevono essere numeri";
            } else if (i==8) {
               	ok=!isDigit(c);
               	msg="Errore nel nono carattere del codice fiscale:\ndeve essere una lettera";
            } else if (i==9 || i==10) {
               	ok=isDigit(c);
            	msg="Erore nel decimo o nell'undicesimo carattere del codice fiscale";
            } else if (i==11) {
               	ok=!isDigit(c);
               	msg="Errore nell'undicesimo carattere del codice fiscale:\ndeve essere una lettera";
            } else if (i>=12 && i <=14) {
               	ok=isDigit(c);
               	msg="Errore nel tredicesimo, nel quattordicesimo o nel penultimo carattere del codice fiscale:\ndevono essere dei numeri";
            } else if (i==15) {
               	ok=!isDigit(c);
               	msg="Errore nell'ultimo carattere del codice fiscale:\ndeve essere una lettera";
            }
        }
    } 
    else {
    	if (cf.length==11) {
       		var regEx = /^[0-9]{11}/;
           	if (cf.search(regEx)==-1) { 
           		msg="Se di 11 cifre il codice fiscale deve essere solo numerico";
              	ok=false;
           	}
    	}
    	else {
      		ok=false;
      		msg="Il codice fiscale deve essere di 11 o di 16 caratteri";
    	}
    }
     
    if (!ok) {
        alert(msg);
        cfObj.focus();
    }
    
  	return ok;
  }
  
}

function abilitaCF() {
	var e = document.forms[0].CODTIPO;
	var cfLabel = document.getElementById("cfFieldsLbl");
	var cfFields = document.getElementById("cfFieldsCampo");
	var cftextbox = document.forms[0].strCodiceFiscale;
	if (e.value == 'S') {
		cfLabel.style.display = '';
		cfFields.style.display = '';
	} else {
		cftextbox.value = "";
		cfLabel.style.display = 'none';
		cfFields.style.display = 'none';
	}
}

</script>
