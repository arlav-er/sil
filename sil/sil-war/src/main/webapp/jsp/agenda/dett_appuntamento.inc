<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<script type="text/javascript">
    // Per rilevare la modifica dei dati da parte dell'utente
    var flagChanged = false;  
    var map = new Object(); // or var map = {};
	var flgMinistero = '<%=flgInvioEsitoAnpal%>';
	var si = new String("S").valueOf();
	var no = new String("N").valueOf();
    function mappaEsiti(){
    	flgMinistero = new String(flgMinistero).valueOf().trim();
    
         if(flgMinistero==si){ 
        	 
      		  document.getElementById("invioAnpal").disabled = false;
      		 document.getElementById("invioAnpal").className = "pulsanti";

        }else          if(flgMinistero==no){ 
        	 
        	document.getElementById("invioAnpal").disabled = true;
       	 document.getElementById("invioAnpal").className = "pulsantiDisabled";
        } 
    	
    	
     	<%for (Iterator i = rowsEsito.iterator(); i.hasNext(); ){
    		SourceBean esito = (SourceBean) i.next();
    		String invioMin = (String) esito.getAttribute("FLGINVIOMIN");
    		String codice = (String) esito.getAttribute("CODICE");
    	%>
    		map['<%=codice%>'] = "<%=invioMin%>";
    	<%}%>
    	 
    }
    
    function getMappaEsiti(k) {
    	return map[k];
	}
    
    function fieldChanged() {
        <%if (canModify) {out.print("flagChanged = true;");}%>
    }

	function caricaComboEsito(){
		comboPair.populate();
		document.getElementById("invioAnpal").disabled = true;
      	 document.getElementById("invioAnpal").className = "pulsantiDisabled";
		fieldChanged();
	}
	
	function controllaEsito(){
		fieldChanged(); 
		var selEsito = document.frmNuovoAppuntamento.CODESITOAPPUNT;
		 
		var temp = getMappaEsiti(selEsito.value);
		 
		flgMinistero = new String(temp).valueOf().trim();
		 
		 if(flgMinistero==si){ 
 
     		  document.getElementById("invioAnpal").disabled = false;
     		 document.getElementById("invioAnpal").className = "pulsanti";
       }else          if(flgMinistero==no){ 
        	document.getElementById("invioAnpal").disabled = true;
       	document.getElementById("invioAnpal").className = "pulsantiDisabled";
       } 
// 05-03-2020 codice commentato per consolidamento lr_ag_anpal_p2_da_3.25.0	       
// 		if(selEsito.value == "CRI" || selEsito.value == "LRI" ){
// 			alert("Attenzione è necessario indicare il nuovo appuntamento");
// 		}
	}

    function stampaPromemoria(cdnlavoratore,prgAzienda,prgUnita,prgAppuntamentoStr, codcpi ) {
      var f = "AdapterHTTP?PAGE=StampaPromemoriaPage&PRGAPPUNTAMENTO=" + prgAppuntamentoStr;
      if(codcpi != '' && codcpi != undefined){f += "&CODCPI=" + codcpi ;}
      if (cdnlavoratore != '') {
        f+= "&CDNLAVORATORE=" + cdnlavoratore;
      }
      else {
        f+= "&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=" + prgUnita;
      }
      var t = "_blank";
      var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=600,height=300,top=100,left=100"
      window.open(f, t, feat);
    }
  
    function apriListaLavoratori(prgappuntamento,codcpi,linkDett){
        var f = "AdapterHTTP?PAGE=RICERCA_AGENDA_LAVORATORI_PAGE&PRGAPPUNTAMENTO=" + prgappuntamento;
        f+= "&CODCPI=" + codcpi + linkDett;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100"
        window.open(f, t, feat);
    }

    function apri_patto_lav(cdnLavoratore, prgLavPattoScelta, prgappuntamento, codcpi, linkDett){
        var f = "AdapterHTTP?PAGE=AgPattiLavPage";
        f+= "&CDNLAVORATORE=" + cdnLavoratore;
        f+= "&PRGLAVPATTOSCELTA=" + prgLavPattoScelta;
        f+= "&PRGAPPUNTAMENTO=" + prgappuntamento;
        f+= "&CODCPI=" + codcpi + linkDett;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=600,height=400,top=100,left=100"
        window.open(f, t, feat);
    }

    function eliminaLavoratore(cdnlavoratore,prglavpattoscelta){
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      var msg = "Attenzione!\nL'appuntamento è stato preso a fronte di un obbligo amministrativo.\n";
      msg += "Cancellare comunque il lavoratore da questo appuntamento?";
      var ok = true;
      if(prglavpattoscelta!="") {
        ok = confirm(msg);
      }
      if(ok) {
        document.frmNuovoAppuntamento.CDNLAVORATORE.value=cdnlavoratore;
        document.frmNuovoAppuntamento.PRG_LAV_PATTO_SCELTA.value = prglavpattoscelta;
        document.frmNuovoAppuntamento.AZIONE_DEL.value="DEL";
        doFormSubmit(document.frmNuovoAppuntamento);
      }
    }

    function eliminaPattoLavoratore(prgLavPattoScelta){
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
    
      document.frmNuovoAppuntamento.CDNLAVORATORE.value = "";
      document.frmNuovoAppuntamento.PRG_LAV_PATTO_SCELTA.value = prgLavPattoScelta;
      document.frmNuovoAppuntamento.AZIONE_DEL.value="DELPATTO";
      doFormSubmit(document.frmNuovoAppuntamento);
    }

    function apriListaAziendeUnita(prgappuntamento,codcpi){
        //window.open('AdapterHTTP?PAGE=LISTA_AZIENDE_UNITA_PAGE','','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,width=400,height=300,top=100,left=100');
        var f = "AdapterHTTP?PAGE=PRicercaAzienda"
        f+= "&PRGAPPUNTAMENTO=" + prgappuntamento + "&CODCPI=" + codcpi + "<%=linkDett%>";
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=600,height=400,top=100,left=100"
        window.open(f, t, feat);
    }

    function conferma(azione){
		  // Se la pagina è già in submit, ignoro questo nuovo invio!
		  if (isInSubmit()) return;
	
	      /*if (azione=="MOD"){
	        document.frmNuovoAppuntamento.PAGE.value="UPDATE_AGENDA_PAGE";
	      }*/
	      if (azione=="BACK"){
	        if (flagChanged==true){
	          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return; }
	          else {
	            document.frmNuovoAppuntamento.PAGE.value="PCalendario";
	            doFormSubmit(document.frmNuovoAppuntamento);
	          }
	        } else {
	          document.frmNuovoAppuntamento.PAGE.value="PCalendario";
	          doFormSubmit(document.frmNuovoAppuntamento);
	        }
	      }
	      if (azione=="ANPAL"){
		        if (flagChanged==true){
		         	alert("I dati sono stati modificati. Per poter inviare l'esito è necessario prima salvare le modifiche");
		        	 return;
		        } else {
		        	if (!confirm("ATTENZIONE: l'esito dell'appuntamento verrà inviato ad ANPAL. Si vuole procedere?")) { return; }
		        	else{
		        		 document.frmNuovoAppuntamento.ANPAL.value="INVIOANPAL";	
		        		 doFormSubmit(document.frmNuovoAppuntamento);
		        	}
		        }
		      }
	 }

    function eliminaAzienda()
    {
      document.frmNuovoAppuntamento.PRGAZIENDA.value = "";
      document.frmNuovoAppuntamento.PRGUNITA.value = "";
      document.frmNuovoAppuntamento.Ragione.value = "";
      document.frmNuovoAppuntamento.Indirizzo.value = "";
      document.frmNuovoAppuntamento.Comune.value = "";
      //document.frmNuovoAppuntamento.PIva.value = "Part. IVA ";
      document.frmNuovoAppuntamento.PartIva.value = "";
      //document.frmNuovoAppuntamento.strTel.value = "Tel. ";
      document.frmNuovoAppuntamento.strTel.value = "";
    }
    var openedWindow;
    function listaAssegnazione() {
    	prgSpi = document.getElementsByName("PRGSPI");
    	codServizio = document.getElementsByName("CODSERVIZIO");
    	prgAmbiente = document.getElementsByName("PRGAMBIENTE");
    	codCpi = document.getElementsByName("CODCPI");
    	var f = "AdapterHTTP?PAGE=ListaAssegnazioniPage" + 
    		"&PRGAMBIENTE=" + prgAmbiente[0].value + 
    		"&CODSERVIZIO="+codServizio[0].value + 
    		"&PRGSPI="+prgSpi[0].value + 
        	"&AGENDA_CODCPI=" + codCpi[0].value;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100"
        openedWindow = window.open(f, t, feat);
    }
    function assegnazioneScelta(prgSpi, codServizio, prgAmbiente){
    	document.frmNuovoAppuntamento.PRGSPI.value=prgSpi;
    	document.frmNuovoAppuntamento.CODSERVIZIO.value = codServizio;
    	document.frmNuovoAppuntamento.PRGAMBIENTE.value = prgAmbiente;
    	flagChanged = true;
    	openedWindow.close();
    }
    
    
    function storicoInviiAnpal() {
    	var appuntamentoPrg = document.frmNuovoAppuntamento.PRGAPPUNTAMENTO.value;
    	var f = "AdapterHTTP?PAGE=STORICO_INVIO_APP_ANPAL" + 
    		"&PRGAPPUNTAMENTO=" + appuntamentoPrg ;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=580,height=400,top=100,left=100"
        window.open(f, t, feat);
    }
    
  </script>


<script language="Javascript">
<!--
  function setDtmDataOra()
  {

    var data = document.frmNuovoAppuntamento.data_app.value;
    var orario = document.frmNuovoAppuntamento.orario.value;
    var dtm = data + " " + orario;
    document.frmNuovoAppuntamento.DTMDATAORA.value=dtm;

    var oraSlot = "<%=oraSlot%>";
    var minutiSlot = "<%=minutiSlot%>";
    var codServizioSlot = "<%=codServizioSlot%>";
    var prgSpiSlot = "<%=prgSpiSlot%>";
    var b = <%=nuovoApp%>;

    //alert("ora slot= " + oraSlot + " - " + document.frmNuovoAppuntamento.orario.value);
    //alert("minuti slot= " + minutiSlot + " - " + document.frmNuovoAppuntamento.NUMMINUTI.value);
    //alert("servizio slot= " + codServizioSlot + " - " + document.frmNuovoAppuntamento.CODSERVIZIO.value);
    //alert("spi slot= " + prgSpiSlot + " - " + document.frmNuovoAppuntamento.PRGSPI.value);
     
    if(b==false) {
      // Se non è un nuovo appuntamento
      var ch = false;
      if(oraSlot!="" && minutiSlot!="" && codServizioSlot!="" && prgSpiSlot!="")
      {
        if(oraSlot != document.frmNuovoAppuntamento.orario.value) { ch = true; }
        if(minutiSlot != document.frmNuovoAppuntamento.NUMMINUTI.value) { ch = true; }
        if(codServizioSlot != document.frmNuovoAppuntamento.CODSERVIZIO.value) { ch = true; }
        if(prgSpiSlot != document.frmNuovoAppuntamento.PRGSPI.value) { ch = true; }
      }
      //alert(ch);
      var txt = "L'appuntamento è stato modificato rispetto allo slot cui era legato.\n";
      txt += "Continuando, l'appuntamento verrà slegato dallo slot.\n";
      txt += "Scegliere OK per continuare, scegliere ANNULLA per annullare l'operazione\ne riallineare l'appuntamento con lo slot.";
      if(ch) {
        var allinea = confirm(txt);
        if(!allinea) { document.frmNuovoAppuntamento.bParAllineaAppuntamento.value = 1; }
        else {document.frmNuovoAppuntamento.bParAllineaAppuntamento.value = 0; }
      }
    }
    //alert("BPAR=" + document.frmNuovoAppuntamento.bParAllineaAppuntamento.value);
    //undoSubmit(); return(false);
    return(true);
  }

  function checkPostoLav()
  {

    var postiLavDisponibili = "<%=numLavoratoriDisp%>";

    var data = document.frmNuovoAppuntamento.data_app.value;
    var orario = document.frmNuovoAppuntamento.orario.value;
    var dtm = data + " " + orario;
    document.frmNuovoAppuntamento.DTMDATAORA.value=dtm;

    var oraSlot = "<%=oraSlot%>";
    var minutiSlot = "<%=minutiSlot%>";
    var codServizioSlot = "<%=codServizioSlot%>";
    var prgSpiSlot = "<%=prgSpiSlot%>";
    var b = <%=nuovoApp%>;
     
    if(b==false) {
      // Se non è un nuovo appuntamento
      var ch = false;
      if(oraSlot!="" && minutiSlot!="" && codServizioSlot!="" && prgSpiSlot!="")
      {
        if(postiLavDisponibili==0) { ch = true; }
      }
      //alert(ch);
      var txt = "Il numero di lavoratori prenotati per questo appuntamento ha raggiunto\n";
      txt += "la soglia limite prevista per lo slot cui è legato.\nContinuando, l'appuntamento verrà slegato dallo slot.\n";
      txt += "Scegliere OK per continuare, scegliere ANNULLA per annullare l'operazione.";
      if(ch) {
        var allinea = confirm(txt);
        if(allinea) { return true; }
        else { return false; }
      }
    }
    return(true);
  }

  function verificaInvioAnpal(){
		<%if(!isAppAnpal){%>
			document.getElementById("invioAnpal").disabled = true;
     		document.getElementById("invioAnpal").className = "pulsantiDisabled";
     	<%}%>
  }
// -->
</script>
 <af:showMessages prefix="M_DELETEAMLAVPATTOSCELTA"/>
 <af:showMessages prefix="MSALVAAPPUNTAMENTO"/>
 
 <af:showErrors />
</p>
<af:form name="frmNuovoAppuntamento" action="AdapterHTTP" method="GET" onSubmit="setDtmDataOra()">
<input type="hidden" name="PAGE" value="SELECT_AGENDA_PAGE">

<input type="hidden" name="CDNLAVORATORE" value="">
<input type="hidden" name="PRG_LAV_PATTO_SCELTA" value="">
<input type="hidden" name="AZIONE_DEL" value="">
<input type="hidden" name="ANPAL" value="">

<input type="hidden" name="CODCPIANPAL" value="<%=CODCPI%>">
<input type="hidden" name="CODCPI" value="<%=CODCPI%>">
<input type="hidden" name="CODCPIMIN" value="<%=codCpiMin%>">
<input type="hidden" name="PRGAPPUNTAMENTO" value="<%=prgAppuntamento%>">
<input type="hidden" name="IDAPPUNTAMENTOCOAP" value="<%=idAppuntamentoAnpal%>">
<input type="hidden" name="CODESITOMINAPP" value="<%=codEsitoMinAppuntamento%>">
<input type="hidden" name="NUMKLOAGENDA" value="<%=NUMKLOAGENDA%>">
<input type="hidden" name="DTMDATAORA" value="">
<input type="hidden" name="PRGSLOT" value="<%=prgSlot%>">
<input type="hidden" name="OLDSTATO" value="<%=OLD_STATO%>">
<input type="hidden" name="bParAllineaAppuntamento" value="">
<input type="hidden" name="cdnParUtente" value="<%=cdnParUtente%>">
<input type="hidden" name="ALLINEA_SLOT" value="1">
<input name="DATA_COD" type="hidden" value="<%=data_cod%>"/>

<%out.print(htmlStreamTop);%>
<table class="main" align="center">
<tr>
  <td class="etichetta">Data</td>
  <td class="campo">
  <%if(canModify) {%>
  <af:textBox name="data_app"
              size="11"
              maxlength="10"
              required="true"
              validateOnPost="true"
              disabled="true"      
              value="<%=data%>"/>
  <%} else {%>
      <af:textBox name="data_app"
              size="11"
              maxlength="10"
              required="true"
              validateOnPost="true"  
              classNameBase="input" readonly="<%=String.valueOf(!canModify)%>"       
              value="<%=data%>"/>
  <%}%>
  </td>
</tr>
<tr>
  <td class="etichetta">Orario</td>
  <td class="campo">
  <af:textBox name="orario"
              size="5"
              maxlength="5"
              required="true"
              type="time"
              title="Orario"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
              value="<%=orario%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Durata Appuntamento</td>
  <td class="campo">
  <af:textBox name="NUMMINUTI" 
              size="5" 
              maxlength="3" 
              type="integer"
              validateOnPost="true"
              title="Durata Appuntamento"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
              value="<%=NUMMINUTI%>"
  />&nbsp;minuti
  </td>
</tr>
<tr>
  <td class="etichetta">Operatore</td>
  <td class="campo" style="vertical-align:baseline">
      				<af:comboBox name="PRGSPI" size="1" title="Operatore"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_SPI_SCAD"
                     selectedValue="<%=PRGSPI%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"/>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" class="pulsanti"  value="Assegnazione" onclick="listaAssegnazione()"/>
    </td>
</tr>
<%if(!isAppAnpal){%>
<tr>
  <td class="etichetta"><%=labelServizio%></td>
  <td class="campo">
    <af:comboBox name="CODSERVIZIO" size="1" title="<%=titleServizio%>"
         multiple="false" required="false"
         focusOn="false" moduleName="COMBO_SERVIZIO_SCAD"
         selectedValue="<%=CODSERVIZIO%>" addBlank="true" blankValue=""
         classNameBase="input"
         disabled="<%= String.valueOf( !canModify ) %>"
         onChange="fieldChanged()"/>    
   </td>
</tr>
<%}else{%>
<tr>
  <td class="etichetta"><%=labelServizio%></td>
  <td class="campo">
    <af:comboBox name="CODSERVIZIO" size="1" title="<%=titleServizio%>"
         multiple="false" required="false"
         focusOn="false" moduleName="COMBO_SERVIZIO_SCAD"
         selectedValue="<%=CODSERVIZIO%>" addBlank="true" blankValue=""
         classNameBase="input"
         disabled="true"
         onChange="fieldChanged()"/>    
   </td>
</tr>
<%}%>
<tr>
  <td class="etichetta">Ambiente/Aula</td>
  <td class="campo">
	  	<af:comboBox name="PRGAMBIENTE" size="1" title="Ambiente/Aula"
	     multiple="false" required="false"
	     focusOn="false" moduleName="COMBO_AMBIENTE"
	     selectedValue="<%=PRGAMBIENTE%>" addBlank="true" blankValue=""
	     classNameBase="input"
	     disabled="<%= String.valueOf( !canModify ) %>"
	     onChange="fieldChanged()"/>    
	</td>
</tr>
<tr class="note">
  <td colspan="2">
  <div class="sezione2">Dati Effettivi</div>
  </td>
</tr>
<tr>
  <td class="etichetta">Orario Effettivo di Inizio</td>
  <td class="campo">
    <af:textBox name="NUMORAINIZIOEFF"
              size="5"
              maxlength="5"
              required="false"
              type="time"
              title="Orario Effettivo di Inizio"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
              value="<%=numOraInizioEff%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Orario Effettivo di Fine</td>
  <td class="campo">
    <af:textBox name="NUMORAFINEEFF"
              size="5"
              maxlength="5"
              required="false"
              type="time"
              title="Orario Effettivo di Fine"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
              value="<%=numOraFineEff%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Operatore Effettivo</td>
  </td>
  <td class="campo">
    <af:comboBox name="PRGSPIEFF" size="1" title="Operatore Effettivo"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_SPI_SCAD_EFF"
                     selectedValue="<%=PRGSPIEFF%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"/>    
  </td>
</tr>
<tr class="note">
  <td colspan="2">
  <div class="sezione2">&nbsp;</div>
  </td>
</tr>
<tr class="note">
  <td class="etichetta">Note</td>
  <td class="campo">
    <!--textarea cols=60 rows=4 name="TXTNOTE"><%=txtNote%></textarea-->
    <af:textArea name="TXTNOTE" 
                 cols="60" 
                 rows="4" 
                 maxlength="4000"
                 title="Note"
                 value="<%=txtNote%>"
                 classNameBase="input"
                 disabled="<%= String.valueOf( !canModify ) %>"
                 onKeyUp="fieldChanged()"
    />
  </td>
</tr>
<tr>
  <td class="etichetta">Tipo di Prenotazione</td>
  <td class="campo">
      <af:comboBox name="PRGTIPOPRENOTAZIONE" size="1" title="Progressivo SPI"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_TIPO_PRENOTAZIONE"
                     selectedValue="<%=PRGTIPOPRENOTAZIONE%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"/>        
  </td>
</tr>
<tr>
  <td class="etichetta">Telefono</td>
  <td class="campo">
    <af:textBox name="STRTELRIF" value="<%=STRTELRIF%>" maxlength="20"
    onKeyUp="fieldChanged();"
    classNameBase="input"
    readonly="<%=String.valueOf(!canModify)%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">E-mail</td>
  <td class="campo"><af:textBox name="STREMAILRIF" value="<%=STREMAILRIF%>" maxlength="50"
  onKeyUp="fieldChanged();"
  classNameBase="input"
  readonly="<%=String.valueOf(!canModify)%>"/></td>
</tr>
<tr>
  <td class="etichetta">Cellulare</td>
  <td class="campo"><af:textBox name="STRTELMOBILERIF" value="<%=STRTELMOBILERIF%>" maxlength="20"
  onKeyUp="fieldChanged();"
  classNameBase="input"
  readonly="<%=String.valueOf(!canModify)%>"/></td>
</tr>
<tr>
  <td class="etichetta">Effetto Appuntamento</td>
  <td class="campo">
      <af:comboBox name="CODEFFETTOAPPUNT" size="1" title="Effetto appuntamento"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_EFFETTO_APPUNTAMENTO"
                     selectedValue="<%=CODEFFETTOAPPUNT%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"/>    
    </td>
</tr>
<tr>
  <td class="etichetta">Stato appuntamento</td>
  
 				 <%
                 
                    ComboPair docComboPair = new ComboPair(rowsEsito, CODESITOAPPUNT, "CODSTATOAPPUNTAMENTO");
 				 	if(StringUtils.isEmptyNoBlank(CODESITOAPPUNT)){
 				 		CODESITOAPPUNT = docComboPair.getCodRef();
 				 	}
                %>
  
  <td class="campo">
      <af:comboBox name="CODSTATOAPPUNTAMENTO" size="1" title="Stato appuntamento"
                     multiple="false" required="true"
                     focusOn="false" moduleName="COMBO_STATO_APPUNTAMENTO"
                     selectedValue="<%=CODSTATOAPPUNTAMENTO%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="caricaComboEsito()"/>        
  </td>
</tr>
<tr>
  <td class="etichetta">Esito appuntamento</td>
  <td class="campo">
      <af:comboBox name="CODESITOAPPUNT" size="1" title="Esito appuntamento"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_ESITO_APPUNTAMENTO"
                     selectedValue="<%=CODESITOAPPUNT%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="controllaEsito()"/>    
		 <%if(canInvioAnpal){%>
			 <input type="button" class="pulsanti" name="ANPAL_BTN" value="Invio esito ad ANPAL" id="invioAnpal"  onCLick="javascript:conferma('ANPAL');">  
			   
		 <%}else{%> 
		 			 <input type="hidden" id="invioAnpal" />    
		 	
		 <%}%> 		
  </td>
   <script>
                var arrayFiglio = new Array();                
                <%=docComboPair.makeArrayJSChild()%>
                var comboPair = new ComboPair(document.frmNuovoAppuntamento.CODSTATOAPPUNTAMENTO, document.frmNuovoAppuntamento.CODESITOAPPUNT, arrayFiglio,true);
                comboPair.populate('<%=docComboPair.getCodRef()%>', '<%=CODESITOAPPUNT%>');
               
  </script>
  
  
</tr>

<%if(isAppAnpal){%>
<tr>
  <td class="etichetta">Codice app.to ANPAL</td>
  <td class="campo">
  <af:textBox name="strCodAnpal" value="<%=idAppuntamentoAnpal%>"   classNameBase="input"  readonly="true"/>
  </td>
 </tr>
<%}%>
<%if(viewUltimoInvioAnpal){%>
	<tr>
	  <td class="etichetta">Data e ora invio</td>
	  <td class="campo">
	  <af:textBox name="dataUltimoInvioAnpal" value="<%=dataUltimoInvioAnpal%>"   classNameBase="input"  readonly="true"/>
	  </td>
	</tr>
	<tr>
	  <td class="etichetta">Esito invio</td>
	  <td class="campo">
	  <af:textBox name="esitoNotificaUltimoInvioAnpal" size="60" value="<%=esitoNotificaUltimoInvioAnpal%>"   classNameBase="input"  readonly="true" />
	  	<%if(canInvioAnpal){%>
			<input type="button" class="pulsanti" name="STORICO_ANPAL_BTN" value="Storico Invii ANPAL" onCLick="javascript:storicoInviiAnpal();"/>
		<%}%>
	  </td>
	</tr>
	<tr>
	  <td class="etichetta">Esito appuntamento inviato</td>
	  <td class="campo">
	  <af:textBox name="esitoAppuntamentoUltimoInvioAnpal" size="60" value="<%=esitoAppuntamentoUltimoInvioAnpal%>"   classNameBase="input"  readonly="true"/>
	  </td>
	</tr>
<%}%>

<input type="hidden" name="PRGUNITA" value="<%=PRGUNITA%>">
<input type="hidden" name="PRGAZIENDA" value="<%=PRGAZIENDA%>">
<tr class="note">
  <td colspan="2">
  <div class="sezione2">Azienda
  <%
  if (!PRGAZIENDA.equals("")) {
  %>
    &nbsp;&nbsp;
    <a href="#" onClick="javascript:stampaPromemoria('','<%=PRGAZIENDA%>','<%=PRGUNITA%>','<%=prgAppuntamento%>', '<%=CODCPI%>');"><img src="../../img/stampa.gif" alt="Stampa promemoria"></a>
  <%
  }
  %>
  <%if(canModify) {%>
      &nbsp;&nbsp;
      <a href="#" onClick="javascript:apriListaAziendeUnita('<%=prgAppuntamento%>','<%=CODCPI%>');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
      &nbsp;&nbsp;
      <a href="#" onClick="javascript:eliminaAzienda();"><img src="../../img/del.gif" alt="Elimina"></a>
  <%}%>
  </div>
  </td>
</tr>
<tr>
  <td class="etichetta">Ragione Sociale</td>
  <td class="campo">
    <input type="text" class="inputView" readonly="true" name="Ragione" value="<%=rowRagioneSociale%>" size="60" />
  </td>
</tr>

<tr>
  <td class="etichetta">Indirizzo</td>
  <td class="campo">
    <input type="text" class="inputView" readonly="true" name="Indirizzo" value="<%=rowIndirizzo%>" size="60" />
  </td>
</tr>

<tr>
  <td class="etichetta">Comune</td>
  <td class="campo">
    <input type="text" class="inputView" readonly="true" name="Comune" value="<%=rowComune%>" size="60" />
  </td>
</tr>

<tr>
  <td class="etichetta">Partita IVA</td>
  <td class="campo">
    <input type="text" class="inputView" readonly="true" name="PartIva" value="<%=rowPIva%>" size="60" />
  </td>
</tr>

<tr>
  <td class="etichetta">Telefono</td>
  <td class="campo">
    <input type="text" class="inputView" readonly="true" name="strTel" value="<%=rowTel%>" size="60" />
  </td>
</tr>

<tr><td colspan="2">&nbsp;</td></tr>

<%if(prgAppuntamento==null || prgAppuntamento.equals("")) {%>

    <tr>
      <td align="justify" colspan="2">
      <b>
      Per inserire i nominativi dei lavoratori prenotati per l'appuntamento &egrave;
      necessario effettuare l'inserimento dell'appuntamento (pulsante &quot;<%=btnSalva%>&quot;).
      </b>
      </td>
    </tr>

<%} else {%>
    <tr>
      <td colspan="2">
      <div class="sezione2">Lavoratori
      <%if(canModify && !isAppAnpal) {%>
          &nbsp;&nbsp;
          <a href="#" onClick="javascript:(checkPostoLav() && apriListaLavoratori('<%=prgAppuntamento%>','<%=CODCPI%>','<%=linkDett%>'))"><img src="../../img/binocolo.gif" alt="Aggiungi"></a>
      <%}%>
      </div>
      </td>
    </tr>
    <tr class="note">
      <td class="etichetta">&nbsp;</td>
      <td class="campo">
      <table>
      <tr>
        <td class="bordato" align="center" colspan="2"><b><font size="-2">C. F.</font></b></td>
        <td class="bordato" align="center"><b><font size="-2">Lavoratore</font></b></td>
        <td class="bordato" align="center"><b><font size="-2">Tipologia</font></b></td>
        <td class="bordato" align="center"><b><font size="-2">Stato Patto /<br> Data Prot. Inf.</font></b></td>
        <!--td class="bordato" align="center"><b><font size="-2">Motivo Fine Atto</font></b></td-->
        <td class="bordato" align="center"><b><font size="-2">Scelta Patto</font></b></td>
      </tr>
        <%
            String cdnlavoratore = "";
            String codicefiscale = "";
            String cognome="";
            String nome="";
            String prgLavPattoScelta = "";
            BigDecimal prgLavPatto = null;
            String dataStipula = "";
            String patto297 = "";
            String tipoPatto = "";
            String motivo = "";

            String moduleName = "LISTA_LAVORATORI_APPUNTAMENTO_AGENDA_MOD";
            SourceBean content = null;

            content = (SourceBean) serviceResponse.getAttribute(moduleName);

            if (content!=null){
              Vector rows = content.getAttributeAsVector("ROWS.ROW");
              SourceBean row = null;
              int i;
              if(rows.size()==0) {
              }
              for(i=0; i < rows.size(); i++) {
                row = (SourceBean) rows.elementAt(i);
                cdnlavoratore = row.getAttribute("CDNLAVORATORE").toString();
                codicefiscale = StringUtils.getAttributeStrNotNull(row, "STRCODICEFISCALE");
                if(codicefiscale.equals("")) { codicefiscale = "&nbsp;"; }
                cognome = StringUtils.getAttributeStrNotNull(row, "STRCOGNOME");
                nome = StringUtils.getAttributeStrNotNull(row, "STRNOME");
                prgLavPatto = (BigDecimal) row.getAttribute("PRGLAVPATTOSCELTA");
                if(prgLavPatto != null) { prgLavPattoScelta = prgLavPatto.toString(); }
                else { prgLavPattoScelta = ""; }
                dataStipula = StringUtils.getAttributeStrNotNull(row, "DATA_STIPULA");
                if(dataStipula.equals("")) { dataStipula = "&nbsp;"; }
                patto297 = StringUtils.getAttributeStrNotNull(row, "PATTO297");
                if(patto297.equals("")) { patto297 = "&nbsp;"; }
                tipoPatto = StringUtils.getAttributeStrNotNull(row, "TIPO_PATTO");
                if(tipoPatto.equals("")) { tipoPatto = "&nbsp;"; }
                motivo = StringUtils.getAttributeStrNotNull(row, "MOT_FINE_ATTO");
                if(motivo.equals("")) { motivo = "&nbsp;"; }
              %>
              <tr>
                <td width="5%" class="sottolineato">
                  <a href="#" onClick="javascript:stampaPromemoria('<%=cdnlavoratore%>','','','<%=prgAppuntamento%>', '<%=CODCPI%>');">
                  <img src="../../img/stampa.gif" alt="Stampa promemoria"></a>
                  
                        &nbsp;
						<%if(!isAppAnpal){%>
                        <a href="#" onClick="javascript:eliminaLavoratore('<%=cdnlavoratore%>','<%=prgLavPattoScelta%>');">
                        <img src="../../img/del.gif" alt="Cancella il lavoratore dall'appuntamento"></a>
						<%}%>
                </td>
                <td class="sottolineato" align="left"><%=codicefiscale%></td>
                <td class="sottolineato" align="left"><%=cognome%>&nbsp;<%=nome%></td>
                <td class="sottolineato" align="left"><%=patto297%></td>
                <td class="sottolineato" align="left"><%=tipoPatto%></td>
                <!--td class="sottolineato" align="left"><%=motivo%></td-->
                <td class="sottolineato" align="left">
                  <a href="#" onClick="apri_patto_lav('<%=cdnlavoratore%>','<%=prgLavPattoScelta%>','<%=prgAppuntamento%>','<%=CODCPI%>','<%=linkDett%>')">
                  <img src="../../img/cerca.gif" alt="Scegli Patto"></a>
                  &nbsp;
                  <% if(prgLavPattoScelta!=null && !prgLavPattoScelta.equals("")) {%>
                    <a href="#" onClick="javascript:eliminaPattoLavoratore('<%=prgLavPattoScelta%>');">
                    <img src="../../img/del.gif" alt="Cancella collegamento con il patto"></a>
                  <% } %>
                </td>
              </tr>
            <%} //end for
          } //end if (content!=null)%>
      </table>
      </td>
  </tr>
<%} // if(prgAppuntamento==null || prgAppuntamento.equals(""))%>


<tr><td colspan="2">&nbsp;</td></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<%if(canModify) {%>
    <tr>
      <td colspan="2" align="center">
      <input type="submit" class="pulsanti" name="SALVA" value="<%=btnSalva%>">  
      &nbsp;&nbsp;
      <input type="reset" class="pulsanti" name="reset" value="Annulla">
      </td>
    </tr>
    <tr><td colspan="2">&nbsp;</td></tr>
<%}%>
<input name="MOD" type="hidden" value="<%=mod%>"/>
<input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
<%if(mod.equals("0")) {%>
    <input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
    <input name="meseDB" type="hidden" value="<%=meseDB%>"/>
    <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
    <input name="giorno" type="hidden" value="<%=giorno%>"/>
    <input name="mese" type="hidden" value="<%=mese%>"/>
    <input name="anno" type="hidden" value="<%=anno%>"/>
<%} else {%>
  <%if(mod.equals("2")) {%>
        <input name="sel_operatore" type="hidden" value="<%=sel_operatore%>"/>
        <input name="strIdCoap" type="hidden" value="<%=sel_anpal%>"/>
        <input name="sel_servizio" type="hidden" value="<%=sel_servizio%>"/>
        <input name="sel_aula" type="hidden" value="<%=sel_aula%>"/>
        <input name="esitoApp" type="hidden" value="<%=esitoApp%>"/>
        <input name="strRagSoc" type="hidden" value="<%=strRagSoc%>"/>
        <input name="strCodiceFiscale" type="hidden" value="<%=strCodiceFiscale%>"/>
        <input name="strCognome" type="hidden" value="<%=strCognome%>"/>
        <input name="strNome" type="hidden" value="<%=strNome%>"/>
        <input name="mese" type="hidden" value="<%=mese%>"/>
        <input name="anno" type="hidden" value="<%=anno%>"/>
        <input name="dataDal" type="hidden" value="<%=dataDal%>"/>
        <input name="dataAl" type="hidden" value="<%=dataAl%>"/>
        <input name="piva" type="hidden" value="<%=piva%>"/>
        <input name="strCodiceFiscaleAz" type="hidden" value="<%=strCodiceFiscaleAz%>"/>        
  <%}%>
<%}%>

<tr>
  <td colspan="2" align="center">
  <input type="button" class="pulsanti" name="INDIETRO" value="<%=btnChiudi%>" onCLick="javascript:conferma('BACK');">  
  </td>
</tr>
<% if(testata!=null) { %>
  <tr><td colspan="2" align="center">
  <%testata.showHTML(out);%>
  </td></tr>
<%}%>
</table>
</af:form>
<%out.print(htmlStreamBottom);%>



<%//out.print(serviceResponse.toXML());%>