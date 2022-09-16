<script type="text/javascript">
    // Per rilevare la modifica dei dati da parte dell'utente
    var flagChanged = false;  
    
    
    function fieldChanged() {
        <%if (canModify) {out.print("flagChanged = true;");}%>
    }
</script>

<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

String data_cod = StringUtils.getAttributeStrNotNull(serviceRequest,"DATA_COD");

%>
<script type="text/javascript">
    function apriListaOperatori(prgslot,codcpi,linkDett){
        //var f = "AdapterHTTP?PAGE=AgListaOperatoriPage&PRGSLOT=" + prgslot;
        var f = "AdapterHTTP?PAGE=RicercaAgOperatoriPage&PRGSLOT=" + prgslot;
        f+= "&CODCPI=" + codcpi + linkDett;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100"
        window.open(f, t, feat);
    }

    function eliminaOperatore(prgSpi){
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      document.frmNuovoSlot.PRGSPI.value = prgSpi;
      document.frmNuovoSlot.AZIONE_DEL.value="DEL";
      doFormSubmit(document.frmNuovoSlot);
    }


    function conferma(azione){
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      if (azione=="BACK"){
        if (flagChanged==true){
          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return; }
          else {
                document.frmNuovoSlot.PAGE.value="GestSlotPage";
                doFormSubmit(document.frmNuovoSlot);
          }
        } else {
          document.frmNuovoSlot.PAGE.value="GestSlotPage";
          doFormSubmit(document.frmNuovoSlot);
        }
      }
    }

    function set_num_aziende() {
      if(document.frmNuovoSlot.CHECKAZ.checked) {
        document.frmNuovoSlot.NUMAZ.value = 1;
        document.frmNuovoSlot.NUMAZIENDE.value = 1;
      } else {
        document.frmNuovoSlot.NUMAZ.value = "";
        document.frmNuovoSlot.NUMAZIENDE.value = "";
      }
    }
    
     var openedWindow;
    function listaAssegnazione() {    	
    	codServizio = document.getElementsByName("CODSERVIZIO");
    	prgAmbiente = document.getElementsByName("PRGAMBIENTE");
    	codCpi = document.getElementsByName("CODCPI");
    	// possono essere presenti o il prgSpi o il prgSpiDaAssegnazione
    	// se  in inserimento prgSpi
    	// se in modifica prgSpiDaAssegnazione
    	inInserimento = <%=((String)serviceRequest.getAttribute("PAGE")).equalsIgnoreCase("NuovoSlotPage") %>;
    	if (inInserimento)
    		prgSpi = document.getElementsByName("PRGSPI");
    	else
    		prgSpi = document.getElementsByName("PRGSPI_DA_AGGIORNARE");
    	if (prgSpi[0]==null) prgSpiValue = "";
    	else prgSpiValue = prgSpi[0].value;    	
    	var f = "AdapterHTTP?PAGE=ListaAssegnazioniPage" + 
    		"&PRGAMBIENTE=" + prgAmbiente[0].value + 
    		"&CODSERVIZIO="+codServizio[0].value + 
    		"&PRGSPI=" + prgSpiValue + 
        	"&AGENDA_CODCPI=" + codCpi[0].value;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100"
        openedWindow = window.open(f, t, feat);
    }
    function assegnazioneScelta(prgSpi, codServizio, prgAmbiente, cognome, nome){
    	inInserimento = <%=((String)serviceRequest.getAttribute("PAGE")).equalsIgnoreCase("NuovoSlotPage") %>;
    	
    	document.frmNuovoSlot.CODSERVIZIO.value = codServizio;
    	document.frmNuovoSlot.PRGAMBIENTE.value = prgAmbiente;
    	datiAssegnazione.codServizio = codServizio;
    	datiAssegnazione.prgAmbiente = prgAmbiente;
    	
    	flagChanged = true;
		if (inInserimento) {
			document.frmNuovoSlot.DA_ASSEGNAZIONE.disabled=false;
			document.frmNuovoSlot.PRGSPI.value=prgSpi;
	    	document.getElementById("sezOperatore").style.display="";
	    	document.getElementById("sezTitoloOperatore").style.display="";
	    	document.frmNuovoSlot.strNome.value=nome;
	    	document.frmNuovoSlot.strCognome.value=cognome;
			if (controllaFunzTL()  && setDtmDataOra() && chkStatoSlot()) {
				// per caricare il modulo di inserimento bisogna attivare il campo salva 
				// simula il click del tasto di submit "Salva"
				salva=document.getElementsByName("SALVA");
				salva[0].disabled=false;
				salva[1].disabled=false;
				document.frmNuovoSlot.submit();
			}
    	}
    	else {
			if (controllaFunzTL()  && setDtmDataOra() && chkStatoSlot()) {			
				document.frmNuovoSlot.DA_ASSEGNAZIONE.disabled=false;
				prgSpiObj = document.getElementsByName("PRGSPI_DA_AGGIORNARE");
				// se il campo e' presente lo si abilita
				// modifica - slot gia' legato ad un operatore: bisogna sostituirlo
				if (prgSpiObj!=null && prgSpiObj[0]!=null)
					prgSpiObj[0].disabled=false; 
				document.frmNuovoSlot.PRGSPI.value=prgSpi;
				// per caricare il modulo di aggiornamento bisogna attivare il campo salva 
				// simula il click del tasto di submit "Salva"
				salva=document.getElementsByName("SALVA");
				salva[0].disabled=false;
				salva[1].disabled=false;
				document.frmNuovoSlot.submit();
			}
    	}
    	openedWindow.close();
    }
    function controllaAssegnazione() {
    	<%-- Savino 20/09/05
    	// se i dati sono stati valorizzati dalla chiamata della funzione di assegnazione, bisogna controllare
    	// che l'operatore non abbia modificato il valore del servizio o dell'ambiente, risultando cosi' non piu'
    	// associati all'operatore della assegnazione selezionata.
    	// questo aveva senso con la vecchia gestione, quando l'operatore scelto non era visibile, ma ora e' 
    	// completamente gestibile per cui non serve piu'.
    	// IN FUTURO CANCELLARLA
    	if (!document.frmNuovoSlot.DA_ASSEGNAZIONE.disabled) {    	
	    	codServizio = document.frmNuovoSlot.CODSERVIZIO.value;
    		prgAmbiente = document.frmNuovoSlot.PRGAMBIENTE.value;
    		if (datiAssegnazione.codServizio != codServizio || datiAssegnazione.prgAmbiente != prgAmbiente) {
    			document.frmNuovoSlot.DA_ASSEGNAZIONE.disabled=true;
    			document.frmNuovoSlot.PRGSPI.value = "";
    		}
    	}
    	--%>
    	return true;
    }
    function eliminaOperatoreAss() {
    	document.frmNuovoSlot.strNome.value="";
	    document.frmNuovoSlot.strCognome.value="";
	    document.frmNuovoSlot.DA_ASSEGNAZIONE.disabled=true;
	    document.frmNuovoSlot.PRGSPI.value="";
	   	document.getElementById("sezOperatore").style.display="none";
	    document.getElementById("sezTitoloOperatore").style.display="none";
	     
    }
    var datiAssegnazione=new Object();
  </script>


<script language="Javascript">
<!--
  function setDtmDataOra()
  {

    var data = document.frmNuovoSlot.data_slot.value;
    var orario = document.frmNuovoSlot.orario.value;
    var dtm = data + " " + orario;
    //alert(dtm);
    document.frmNuovoSlot.DTMDATAORA.value=dtm;
    //alert(document.frmNuovoSlot.DTMDATAORA.value);
    return(true);
  }

  function chkStatoSlot()
  {
    var msg = "La modifica non è consentita a causa di un cambiamento non lecito dello stato slot";
    switch ("<%=codStatoSlot%>"){
    case "":
      return (true);
      break;

    case "AA":
      switch (document.frmNuovoSlot.CODSTATOSLOT.value){
        case "AA":
          return (true);
          break;
        default: 
          alert (msg);
          return (false);
          break;
      }

    case "P":
      switch (document.frmNuovoSlot.CODSTATOSLOT.value){
        case "P":
          return (true);
          break;
        default: 
          alert (msg);
          return (false);
          break;
      }

    case "T":
      switch (document.frmNuovoSlot.CODSTATOSLOT.value){
        case "T":
          return (true);
          break;
        default: 
          alert (msg);
          return (false);
          break;
      }

    case "C":
      switch (document.frmNuovoSlot.CODSTATOSLOT.value){
        case "C":
          return (true);
          break;
        case "B":
          return (true);
          break;
        case "A":
          return (true);
          break;
        default: 
          alert (msg);
          return (false);
          break;
      }

    case "B":
      switch (document.frmNuovoSlot.CODSTATOSLOT.value){
        case "C":
          return (true);
          break;
        case "B":
          return (true);
          break;
        case "A":
          return (true);
          break;
        default: 
          alert (msg);
          return (false);
          break;
      }

    case "A":
      switch (document.frmNuovoSlot.CODSTATOSLOT.value){
        case "C":
          return (true);
          break;
        case "B":
          return (true);
          break;
        case "A":
          return (true);
          break;
        default: 
          alert (msg);
          return (false);
          break;
      }
    default:
      return(true);
    }
  }

// -->
</script>

 <font color="green"><af:showMessages prefix="MSALVASLOT"/></font>
 <font color="red"><af:showErrors /></font>
<%out.print(htmlStreamTop);%>
<af:form name="frmNuovoSlot" action="AdapterHTTP" method="POST" onSubmit="(setDtmDataOra() && chkStatoSlot() && controllaAssegnazione())">
<input type="hidden" name="PAGE" value="DettaglioSlotPage">

<input type="hidden" name="PRGSPI" value="">
<input type="hidden" name="AZIONE_DEL" value="">
<input type="hidden" name="DA_ASSEGNAZIONE" value="" disabled> <%-- gestione assegnazione--%>
<input type="hidden" name="SALVA" value="" disabled><%-- gestione form da assegnazione --%>
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="PRGSLOT" value="<%=prgSlot%>">
<input type="hidden" name="NUMKLOSTATOSLOT" value="<%=numKloSlot%>">
<input type="hidden" name="DTMDATAORA" value="">

<input name="DATA_COD" type="hidden" value="<%=data_cod%>"/>

<table class="main">
<tr>
  <td class="etichetta">Data</td>
  <td class="campo">
  <%if(canModify) {%>
      <af:textBox name="data_slot"
              size="11"
              maxlength="10"
              required="true"
              validateOnPost="true"
              disabled="true"
              value="<%=data%>"/>
  <%} else {%>
      <af:textBox name="data_slot"
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
  <td class="etichetta">Durata</td>
  <td class="campo">
  <af:textBox name="NUMMINUTI" 
              size="5" 
              maxlength="3" 
              type="integer"
              validateOnPost="true"
              title="Durata Appuntamento"
              value="<%=numMinuti%>"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
              required="true"
  />&nbsp;minuti
  </td>
</tr>
<tr>
  <td class="etichetta"><%=labelServizio%></td>
    <td class="campo">
      <af:comboBox name="CODSERVIZIO" size="1" title="<%=titleServizio%>"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_SERVIZIO_SCAD"
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"
                     selectedValue="<%=codServizio%>" addBlank="true" blankValue=""/>    
          <%-- if (serviceRequest.getAttribute("PAGE").toString().equalsIgnoreCase("NuovoSlotPage")) {--%>
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" class="pulsanti"  value="Assegnazione" onclick="listaAssegnazione()"/>
          <%--}--%>
    </td>
</tr>
<tr>
  <td class="etichetta">Ambiente/Aula</td>
    <td class="campo">
      <af:comboBox name="PRGAMBIENTE" size="1" title="Ambiente/Aula"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_AMBIENTE"
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"
                     selectedValue="<%=prgAmbiente%>" addBlank="true" blankValue=""/>    
    </td>
</tr>
<tr class="note">
  <td class="etichetta">Note</td>
  <td class="campo">
    <af:textArea name="STRNOTE" 
                 cols="60" 
                 rows="4" 
                 maxlength="4000"
                 title="Note"
                 value="<%=strNote%>"
                 onKeyUp="fieldChanged();"
                 classNameBase="input"
                 readonly="<%=String.valueOf(!canModify)%>"
    />
  </td>
</tr>
<tr>
  <td class="etichetta">Prenotabile da utenti azienda e lavoratore</td>
  <td class="campo">
  <af:comboBox name="flgPubblico"
               size="1"
               title="Prenotabile da utenti azienda e lavoratore"
               required="false"
               moduleName=""
               selectedValue="<%=flgPubblico%>"
               addBlank="true"
               classNameBase="input"
               disabled="<%= String.valueOf( !canModify ) %>"
               onChange="fieldChanged()"
               blankValue="">
    <option value="S" <%if(flgPubblico.equals("S")) {%>SELECTED<%}%> >S&igrave;</option>
    <option value="N" <%if(flgPubblico.equals("N")) {%>SELECTED<%}%>>No</option>
  </af:comboBox>
  </td>
</tr>
<tr>
  <td class="etichetta"><!--Numero max. Aziende-->Azienda</td>
  <td class="campo">
    <input type="text" name="NUMAZ" value="<%=numAziende%>" size="4" maxlength="2" disabled style="display: none">
    &nbsp;
    <input type="checkbox" name="CHECKAZ" onClick="set_num_aziende()" 
      <%if(numAziende.equals("1")) {%>
        checked
      <%}%>
      <%if(!canModify) { %>
        disabled="true"
      <%}%>
    >
    <input type="hidden" name="NUMAZIENDE" value="<%=numAziende%>"/>
    <!--af:textBox name="NUMAZIENDE" 
                value="<%=numAziende%>"
                size="4"
                maxlength="3"
                title="Numero max. Aziende"
                disabled="false"
                required="false"
                type="integer"
                validateOnPost="true"
    /-->
  </td>
</tr>
<tr>
  <td class="etichetta">Numero max. Lavoratori</td>
  <td class="campo">
    <!--input type="text" name="NUMLAVORATORI" value="<%=numLavoratori%>"-->
    <af:textBox name="NUMLAVORATORI" 
                value="<%=numLavoratori%>"
                size="4"
                title="Numero max. Lavoratori"
                disabled="false"
                required="false"
                type="integer"
                validateOnPost="true"
                maxlength="3"
                onKeyUp="fieldChanged();"
                classNameBase="input"
                readonly="<%=String.valueOf(!canModify)%>"
    />
  </td>
</tr>
<% if(!nuovo) {%>
  <tr>
      <td class="etichetta">Aziende gi&agrave; prenotate</td>
      <td class="campo"><b><%=numAziendePrenotate%></b></td>
  </tr>
  <tr>
      <td class="etichetta">Lavoratori gi&agrave; prenotati</td>
      <td class="campo"><b><%=numLavPrenotati%></b></td>
  </tr>
<% }%>
<tr>
  <td class="etichetta">Stato Slot</td>
  <td class="campo">
      <af:comboBox name="CODSTATOSLOT" size="1" title="Stato Slot"
                     multiple="false" 
                     focusOn="false" moduleName="<%=comboSlot%>"
                     selectedValue="<%=codStatoSlot%>" addBlank="true" blankValue=""
                     required="true"
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"
      />
    </td>
</tr>

<tr><td colspan="2">&nbsp;</td></tr>

<%if(nuovo) {%>

    <tr>
      <td align="justify" colspan="2">
      <b>
      Per inserire i nominativi degli operatori impegnati per lo slot &egrave;
      necessario effettuare l'inserimento dello slot (pulsante &quot;<%=btnSalva%>&quot;) oppure selezionare 
      un operatore dalla lista delle assegnazioni.
      <%--
      	Savino 19/09/05: modificata etichetta 
      --%>
      </b>
      </td>
    </tr>
    <tr><td colspan="2">&nbsp;</td></tr>
    <tr>
      <td colspan="2" id="sezTitoloOperatore" style="display:none"><div class="sezione2">Operatori</div></td>
    </tr>
	<tr class="note">    
      <td class="etichetta">&nbsp;</td>  
      <td class="campo">
    	  <table id="sezOperatore" style="display:none">    	  	  
	    	  <tr>
	    	    <td width="5%" class="sottolineato">                
                    <a href="#" onClick="javascript:eliminaOperatoreAss();">
                    <img src="../../img/del.gif"></a>
                </td>
	    	  
                <td class="sottolineato" align="left"><input style="background-color: transparent;border-style: none;font-size: 12px;" name="strCognome"></td>
                <td class="sottolineato" align="left"><input style="background-color: transparent;border-style: none;font-size: 12px;" name="strNome"></td>      
              </tr>
    	  </table>
      </td>
  </tr>
	<tr>
<%} else {%>
    <%
    String moduleName = "MLISTAOPERATORISLOT";
    SourceBean content = null;
    content = (SourceBean) serviceResponse.getAttribute(moduleName);
    Vector rows = content.getAttributeAsVector("ROWS.ROW");
    int nro_oper = rows.size(); 
    %>
    <tr>
      <td colspan="2">
      <div class="sezione2">Operatori
      &nbsp;&nbsp;
      <%if(nro_oper==0) {%>
      <a href="#" onClick="javascript:apriListaOperatori('<%=prgSlot%>','<%=codCpi%>','<%=linkDett%>')"><img src="../../img/cerca.gif" alt="Aggiungi"></a>
      <%}%>
      </div>
      </td>
    </tr>
    <tr class="note">
      <td class="etichetta">&nbsp;</td>
      <td class="campo">
      <table>
        <%
            String prgSpi = "";
            String strCognome="";
            String strNome="";
            Boolean flgSpiValido;
            //if(content!=null){
            if(rows!=null) {
              SourceBean row = null;
              int i;
              for(i=0; i < rows.size(); i++) {
                row = (SourceBean) rows.elementAt(i);
                prgSpi = row.getAttribute("PRGSPI").toString();
                strCognome = StringUtils.getAttributeStrNotNull(row, "STRCOGNOME");
                strNome = StringUtils.getAttributeStrNotNull(row, "STRNOME");
                flgSpiValido = Boolean.valueOf( row.getAttribute("FLGSPIVALIDO").toString());
              %>
              <tr>
                <td width="5%" class="sottolineato">
                  <%if(canModify) {%>
                    <a href="#" onClick="javascript:eliminaOperatore('<%=prgSpi%>');">
                    <img src="../../img/del.gif"></a>
                    <input type="hidden" name="PRGSPI_DA_AGGIORNARE" value="<%=prgSpi%>" disabled>
                  <%}%>
                </td>
                <td class="sottolineato" align="left"><%=strCognome%></td>
                <td class="sottolineato" align="left"><%=strNome%></td>   
                <td width="5%" class="sottolineato">
                  <%if(flgSpiValido == Boolean.FALSE) {%>
                    <img src="../../img/warning_trasp.gif"></a>
                  <%}%>
                </td>  
              </tr>
            <%} //end for
          } //end if (rows!=null)%>
      </table>
      </td>
  </tr>
<%} // if(nuovo)%>


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
        <input name="sel_servizio" type="hidden" value="<%=sel_servizio%>"/>
        <input name="sel_aula" type="hidden" value="<%=sel_aula%>"/>
        <input name="mese" type="hidden" value="<%=mese%>"/>
        <input name="anno" type="hidden" value="<%=anno%>"/>
        <input name="dataDal" type="hidden" value="<%=dataDal%>"/>
        <input name="dataAl" type="hidden" value="<%=dataAl%>"/>
  <%}%>
<%}%>

<tr>
  <td colspan="2" align="center">
  <input type="button" class="pulsanti" name="INDIETRO" value="<%=btnChiudi%>" onCLick="javascript:conferma('BACK');">  
  </td>
</tr>
</table>
</af:form>

<% if(testata!=null) { %>
  <%testata.showHTML(out);%>
<%}%>

<%out.print(htmlStreamBottom);%>
