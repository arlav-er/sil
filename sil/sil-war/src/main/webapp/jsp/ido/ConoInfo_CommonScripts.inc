<SCRIPT TYPE="text/javascript">

  function ConoscenzaDelete(prgInfo, prgRichiestaAz,funzione) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    if ( confirm("Confermi operazione ?") ) {
      var s= "AdapterHTTP?PAGE=IdoInformaticaPage";
      s += "&cancella=Y";
      s += "&PRGINFO=" + prgInfo;
      s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
      s += "&CDNFUNZIONE=" + funzione;
      setWindowLocation(s);
    }
  }
  
  function inibisciScelta(combo, scelta){
	var comboValue= combo[combo.selectedIndex].value;
	if(comboValue == scelta){
		alert("Scelta non valida");
		if('<%=codTipo%>' == scelta){
			combo[0].selected=true;
			return;
		}
		for(i=0; i< combo.options.length; i++){
			if(combo[i].value == '<%=codTipo%>'){
			break;
		  }
		}
		combo[i].selected=true;
		
		
		
		for(i=0; i< document.MainForm.CODICE.options.length; i++){
			if(document.MainForm.CODICE[i].value == '<%=codDettTipo%>'){
			break;
		  }
		}
		document.MainForm.CODICE[i].selected = true;		
	}
  }
  function caricaDettInfo(codiceTipoInfo,codDettTipo,strProvenienza) {

    var dett_tipo=new Array();
    var dett_cod=new Array();
    var dett_des=new Array();
    var indiceDett=0;
<%  for(int i=0; i<tipiDettInfoRows.size(); i++)  { 
      row_dettInfo = (SourceBean) tipiDettInfoRows.elementAt(i);
      out.print("dett_tipo["+i+"]=\""+ row_dettInfo.getAttribute("CODICETIPO").toString()+"\";\n");
      out.print("dett_cod["+i+"]=\""+ row_dettInfo.getAttribute("CODICE").toString()+"\";\n");
      out.print("dett_des["+i+"]=\""+ row_dettInfo.getAttribute("DESCRIZIONE").toString()+"\";\n");
    }
%>
   i=0;
   j=0;
   maxcombo=15;
   if(document.MainForm.CODICE.options != null)
   { while (document.MainForm.CODICE.options.length>0) {
          document.MainForm.CODICE.options[0]=null;
     }

    for (i=0; i<dett_tipo.length ;i++) {
      if (dett_tipo[i]==codiceTipoInfo) {
        if (dett_cod[i] == codDettTipo) {
          indiceDett=j;
        }
        document.MainForm.CODICE.options[j]=new Option(dett_des[i], dett_cod[i], false, false);
        j++;
      }
    } 

    if (strProvenienza != 'nuovo') {
      document.MainForm.CODICE.options[j]=new Option('', '', false, false);
      j++;
    }
   }//if(document.MainForm.CODICE.options != null)
   
    if (j>maxcombo) {j=maxcombo;} //imposto un massimo per la lunghezza della combo
    
    if (codDettTipo != '') {
      document.MainForm.CODICE.selectedIndex=indiceDett;
    }
    else {
      document.MainForm.CODICE.selectedIndex=-1;
    }
    
    if (strProvenienza == 'nuovo') {
      document.MainForm.CODICE.size=j;
    }
  }

  function ConoscenzaSelect (prgInfo, prgAzienda, prgRichiestaAz, funzione) {
    document.MainForm.PAGE.value = "IdoDettaglioInfoPage";
    document.MainForm.PRGINFO.value = prgInfo;
    doFormSubmit(document.MainForm);
  }

</SCRIPT>
