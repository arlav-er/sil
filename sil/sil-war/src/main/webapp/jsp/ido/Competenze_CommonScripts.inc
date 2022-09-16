<SCRIPT TYPE="text/javascript">

  function annulla (objForm, objModule) {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
  
    objModule.value = "";
    objModule.disabled = true;
    doFormSubmit(objForm);
  }

  function CompetenzaDelete(prgCompetenza, prgRichiestaAz,funzione) {
    if ( confirm("Confermi operazione ?") ) {
      var s= "AdapterHTTP?PAGE=IdoCompetenzePage";
      s += "&CANCELLA=Y";
      s += "&PRGCOMPETENZA=" + prgCompetenza;
      s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
      s += "&CDNFUNZIONE=" + funzione;
      setWindowLocation(s);
    }
  }
  
  function caricaCompetenze(codiceTipoCompetenza,codCompetenza,strProvenienza) {
    var dett_tipo=new Array();
    var dett_cod=new Array();
    var dett_des=new Array();
    var indiceDett=0;
<%  for(int i=0; i<competenze_Rows.size(); i++)  { 
      row_Dett = (SourceBean) competenze_Rows.elementAt(i);
      out.print("dett_tipo["+i+"]=\""+ row_Dett.getAttribute("TIPO").toString()+"\";\n");
      out.print("dett_cod["+i+"]=\""+ row_Dett.getAttribute("CODICE").toString()+"\";\n");
      out.print("dett_des["+i+"]=\""+ row_Dett.getAttribute("DESCRIZIONE").toString()+"\";\n");
    }
%>
     i=0;
     j=0;
     maxcombo=15;
     while (document.MainForm.CODCOMPETENZA.options.length>0) {
          document.MainForm.CODCOMPETENZA.options[0]=null;
      }

    for (i=0; i<dett_tipo.length ;i++) {
      if (dett_tipo[i]==codiceTipoCompetenza) {
        if (dett_cod[i] == codCompetenza) {
          indiceDett=j;
        }
        document.MainForm.CODCOMPETENZA.options[j]=new Option(dett_des[i], dett_cod[i], false, false);
        j++;
      }
    } 
    if (j>maxcombo) {j=maxcombo;} //imposto un massimo per la lunghezza della combo
    if (codCompetenza != '') {
      document.MainForm.CODCOMPETENZA.selectedIndex=indiceDett;
    }
    if (strProvenienza == 'nuovo') {
      document.MainForm.CODCOMPETENZA.size=j;
    }
  }

  function CompetenzaSelect (prgCompetenza, prgAzienda, prgRichiestaAz, funzione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    document.MainForm.PAGE.value = "IdoDettaglioCompetenzaPage";
    document.MainForm.PRGCOMPETENZA.value = prgCompetenza;
    doFormSubmit(document.MainForm);
  }
  
</SCRIPT>
