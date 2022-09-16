<SCRIPT TYPE="text/javascript">

  function GoToMainPage() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var url = "AdapterHTTP?PAGE=ValidCurPage&CDNLAVORATORE=<%= cdnLavoratore %>&CDNFUNZIONE=<%= _funzione %>&START_PAGE=<%= _page%>";
    setWindowLocation(url);
  }

  function controllaDate(){

    var dataI = "";
    var dataF = "";
    var dataDalCercaInt = 0;
    var dataAlCercaInt = 0;

    if (document.Frm1.DATINIZIOCURR.value != "") {
      dataI = new String(document.Frm1.DATINIZIOCURR.value);
      annoDataDal = dataI.substr(6,4);
      meseDataDal = dataI.substr(3,2);
      giornoDataDal = dataI.substr(0,2);
      dataDalCercaInt = parseInt(annoDataDal + meseDataDal + giornoDataDal,10);
    }

    if (document.Frm1.DATFINECURR.value != "") {
      dataF = new String(document.Frm1.DATFINECURR.value);
      annoDataAl = dataF.substr(6,4);
      meseDataAl = dataF.substr(3,2);
      giornoDataAl = dataF.substr(0,2);
      dataAlCercaInt = parseInt(annoDataAl + meseDataAl + giornoDataAl,10);            
    }

    if (dataDalCercaInt > dataAlCercaInt){
      alert ("La data di inizio deve precedere la data di fine");
	  return false;	
    }
    return true;
  }

  function obbligatori(){
    if ( (document.Frm1.DATFINECURR.value == "") || (document.Frm1.DATINIZIOCURR.value == "") || (document.Frm1.CODSTATOLAV.value == "") ) {
      alert("I campi contrassegnati con asterisco sono obbligatori.");
      return false;
    }
    return true;
  }
  

  function controllaDateRange(range){
    var dataI = "";
    var dataF = "";
    var ONE_DAY = 1000 * 60 * 60 * 24;
	

    if (document.Frm1.DATINIZIOCURR.value != "") {
      dataI = new String(document.Frm1.DATINIZIOCURR.value);
      annoDataDal = dataI.substr(6,4);
      meseDataDal = dataI.substr(3,2);
      giornoDataDal = dataI.substr(0,2);
    }

    if (document.Frm1.DATFINECURR.value != "") {
      dataF = new String(document.Frm1.DATFINECURR.value);
      annoDataAl = dataF.substr(6,4);
      meseDataAl = dataF.substr(3,2);
      giornoDataAl = dataF.substr(0,2);
    }
    dataDal = new Date(annoDataDal, meseDataDal-1, giornoDataDal);
	dataAl = new Date(annoDataAl, meseDataAl-1, giornoDataAl);

	var dataDal_ms = dataDal.getTime();
    var dataAl_ms = dataAl.getTime();
    var difference_ms = Math.abs(dataDal_ms - dataAl_ms);
    var delta = 1 + Math.round(difference_ms/ONE_DAY);   

	if (delta > range) {
		alert("Superato periodo massimo di validità consentito");
		return false;
	}
	
    return true;
  }

  
</SCRIPT>
