<SCRIPT TYPE="text/javascript">

	function checkCF (inputName) {
		var cfObj = eval("document.Frm1." + inputName);
	  	cfObj.value = cfObj.value.toUpperCase();
	  	var cf = cfObj.value;
	  	var ok = true;
	  	var msg = "";

		if (cf.length==16) {
	        for (i=0; i<16 && ok; i++) {
	            c = cf.charAt(i);
	            if (i>=0 && i<=5){
                		ok=!isDigit(c);
                		msg="Errore nei primi sei caratteri del codice fiscale";
	            } else if  (i==6 || i==7) { 
	                    ok=isDigit(c);
	                    msg="Errore nel settimo o nell'ottavo carattere del codice fiscale";
	            } else if (i==8) {
	                    ok=!isDigit(c);
	                    msg="Errore nel nono carattere del codice fiscale";
	            } else if (i==9 || i==10) {
	                    ok=isDigit(c);
	                    msg="Erore nel decimo o nell'undicesimo carattere del codice fiscale";
	            } else if (i==11) {
	                    ok=!isDigit(c);
	                    msg="Errore nel dodicesimo carattere del codice fiscale";
	            } else if (i==15) {
	                    ok=!isDigit(c);
	                    msg="Errore nell'ultimo carattere del codice fiscale: deve essere una lettera";
	            }
	        }           
	    } else {
	      ok=false;
	      msg="Errore nella lunghezza del codice fiscale";
	    }
	    
	    if (!ok) {
	        alert(msg);
	        cfObj.focus();
	    }
	 	return ok;
	}
	
	function isDigit(c) {   
      return ( (c >= '0') && (c <=  '9') );
  	}
  	
  	
  	function checkCFAz (inputName) {
  		var cfObj = eval("document.forms[0]." + inputName);
	  	cfObj.value = cfObj.value.toUpperCase();
	  	var cf = cfObj.value;
	  	var ok = true;
	  	var msg = "";
	  	  
		if (cf.length == 11) {
			//se di 11 caratteri deve essere composto di soli numeri
			msg = "Errore nel codice fiscale: se lungo 11 caratteri deve essere costituito da numeri";
			for (i=0; i<11 && ok; i++) {
		    	c = cf.charAt(i);
		        ok = isDigit(c);
			}		
		}
		else {
			if (cf.length==16) {
		        for (i=0; i<16 && ok; i++) {
		            c = cf.charAt(i);
		            if (i>=0 && i<=5){
		                    ok=!isDigit(c);
		                    msg="Errore nei primi sei caratteri del codice fiscale";
		            } else if  (i==6 || i==7) { 
		                    ok=isDigit(c);
		                    msg="Errore nel settimo o nell'ottavo carattere del codice fiscale";
		            } else if (i==8) {
		                    ok=!isDigit(c);
		                    msg="Errore nel nono carattere del codice fiscale";
		            } else if (i==9 || i==10) {
		                    ok=isDigit(c);
		                    msg="Erore nel decimo o nell'undicesimo carattere del codice fiscale";
		            } else if (i==11) {
		                    ok=!isDigit(c);
		                    msg="Errore nel dodicesimo carattere del codice fiscale";
		            } else if (i==15) {
		                    ok=!isDigit(c);
		                    msg="Errore nell'ultimo carattere del codice fiscale: deve essere una lettera";
		            }
		        }
		    }        
		    else {
		      ok = false;
		      msg="Errore nella lunghezza del codice fiscale";
		    }
		}
	    if (!ok) {
	        alert(msg);
	        cfObj.focus();
	    }
 		return ok;
	}
  	

</SCRIPT>
