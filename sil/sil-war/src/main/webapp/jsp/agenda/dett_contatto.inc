<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<script type="text/javascript">
    // Per rilevare la modifica dei dati da parte dell'utente
    var flagChanged = false;  
    
    
    function fieldChanged() {
        <%if (canModify) {out.print("flagChanged = true;");}%>
    }

    function conferma(azione){
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      if (azione=="BACK"){
        if (flagChanged==true){
          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return; }
          else {
              document.frmNuovoContatto.PAGE.value="PCalendario";
              doFormSubmit(document.frmNuovoContatto);
			   }
        } else {
          document.frmNuovoContatto.PAGE.value="PCalendario";
          doFormSubmit(document.frmNuovoContatto);
        }
      }
      
    }

    function apriListaLavoratori(prgcontatto,codcpi,linkDett){
        var f = "AdapterHTTP?PAGE=RICERCA_AGENDA_LAVORATORI_PAGE&PROV=CONTATTI";
        f += "&PRGCONTATTO=" + prgcontatto;
        f += "&CODCPI=" + codcpi + linkDett;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100"
        window.open(f, t, feat);
    }

    function apriListaAziendeUnita(prgcontatto,codcpi){
        var f = "AdapterHTTP?PAGE=PRicercaAzienda&PROV=CONTATTI"
        f+= "&PRGCONTATTO=" + prgcontatto + "&CODCPI=" + codcpi;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=600,height=400,top=100,left=100"
        window.open(f, t, feat);
    }

    function eliminaContatto()
    {
      document.frmNuovoContatto.PRGAZIENDA.value = "";
      document.frmNuovoContatto.PRGUNITA.value = "";
      document.frmNuovoContatto.CDNLAVORATORE.value = "";
      document.frmNuovoContatto.TXTDATICONTATTO.value = "";
      document.frmNuovoContatto.TContatto.src = "../../img/b.gif";
    }

    function annulla()
    {
      document.frmNuovoContatto.PRGAZIENDA.value = "<%=prgAzienda%>";
      document.frmNuovoContatto.PRGUNITA.value = "<%=prgUnita%>";
      document.frmNuovoContatto.CDNLAVORATORE.value = "<%=cdnLavoratore%>";
      document.frmNuovoContatto.TContatto.src = "<%=ico%>";
    }

    function controllaFlgRicontattare()
    {
      var v = document.frmNuovoContatto.FLGRICONTATTARE.value;
      if(document.frmNuovoContatto.DATENTROIL.value != "") {
        if(v!="S") { 
          document.frmNuovoContatto.DATENTROIL.value = "";
        }
      }
      return(true);
    }
</script>

<font color="green"><af:showMessages prefix="MSALVACONTATTO"/></font>
<font color="red"><af:showErrors /></font>

<af:form name="frmNuovoContatto" action="AdapterHTTP" method="POST" onSubmit="controllaFlgRicontattare()">
<input type="hidden" name="PAGE" value="ModContattoPage">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="CODCPICONTATTO" value="<%=codCpi%>">
<input type="hidden" name="PRGCONTATTO" value="<%=prgContatto%>">
<input type="hidden" name="NUMKLOCONTATTO" value="<%=numKloContatto%>">
<input type="hidden" name="PRGSPI" value="<%=prgSpiContatto%>">

<%out.print(htmlStreamTop);%>
<table class="main">
<tr>
  <td class="etichetta">Data</td>
  <td class="campo">
  <%if(canModify) {%>
  <af:textBox name="DATCONTATTO"
              size="11"
              maxlength="10"
              required="true"
              validateOnPost="true"
              disabled="true"
              value="<%=datContatto%>"/>
  <%} else {%>
      <af:textBox name="DATCONTATTO"
              size="11"
              maxlength="10"
              required="true"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
              value="<%=datContatto%>"/>
  <%}%>
  </td>
</tr>
<tr>
  <td class="etichetta">Orario</td>
  <td class="campo">
  <af:textBox name="STRORACONTATTO"
              size="5"
              maxlength="5"
              required="true"
              type="time"
              title="Orario"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
              value="<%=strOraContatto%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Operatore</td>
    <td class="campo">
      <af:comboBox name="PRGSPICONTATTO" size="1" title="Operatore"
                     multiple="false" required="true"
                     focusOn="false" moduleName="COMBO_SPI_SCAD"
                     selectedValue="<%=prgSpiContatto%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"/>    
    </td>
</tr>
<tr>
  <td class="etichetta">Tipo</td>
  </td>
  <td class="campo">
    <af:comboBox name="PRGTIPOCONTATTO" 
                 size="1" 
                 title="Tipo di Contatto"
                 multiple="false" 
                 required="true"
                 focusOn="false" 
                 moduleName="COMBO_TIPO_CONTATTO_AG"
                 selectedValue="<%=prgTipoContatto%>" 
                 addBlank="true" 
                 blankValue=""
                 classNameBase="input"
                 disabled="<%= String.valueOf( !canModify ) %>"
                 onChange="fieldChanged()"
    />
    &nbsp;&nbsp;
    <af:comboBox name="PARSTRIO"
                 size="1" 
                 title="Tipo di Contatto"
                 multiple="false" 
                 required="true"
                 focusOn="false" 
                 selectedValue="<%=strIo%>" 
                 addBlank="true" 
                 blankValue=""
                 classNameBase="input"
                 disabled="<%= String.valueOf( !canModify ) %>"
                 onChange="fieldChanged()"
    >
      <option value="O"  <%if(strIo.equals("O")) {%>SELECTED<%}%> >Dal CpI</option>
      <option value="I"  <%if(strIo.equals("I")) {%>SELECTED<%}%> >Al CpI</option>
    </af:comboBox>
  </td>
</tr>


<tr class="note">
  <td class="etichetta">Note</td>
  <td class="campo">
    <!--textarea cols=60 rows=4 name="TXTCONTATTO"><%=txtContatto%></textarea-->
    <af:textArea name="TXTCONTATTO" 
                 cols="60" 
                 rows="4" 
                 title="Note"
                 maxlength="4000"
                 onKeyUp="fieldChanged();"
                 classNameBase="input"
                 readonly="<%=String.valueOf(!canModify)%>"
                 value="<%=txtContatto%>"
    />
  </td>
</tr>
<tr>
  <td class="etichetta">Motivo</td>
  </td>
  <td class="campo">
    <af:comboBox name="PRGMOTCONTATTO" 
                 size="1" 
                 title="Motivo del Contatto"
                 multiple="false" 
                 required="false"
                 focusOn="false" 
                 moduleName="COMBO_MOTIVO_CONTATTO_AG"
                 selectedValue="<%=prgMotContatto%>" 
                 addBlank="true" 
                 blankValue=""
                 classNameBase="input"
                 disabled="<%= String.valueOf( !canModify ) %>"
                 onChange="fieldChanged()"
    />    
  </td>
</tr>
<tr>
  <td class="etichetta">Effetto</td>
  </td>
  <td class="campo">
    <af:comboBox name="PRGEFFETTOCONTATTO" 
                 size="1" 
                 title="Effetto del Contatto"
                 multiple="false" 
                 required="false"
                 focusOn="false" 
                 moduleName="COMBO_EFFETTO_CONTATTO_AG"
                 selectedValue="<%=prgEffettoContatto%>" 
                 addBlank="true" 
                 blankValue=""
                 classNameBase="input"
                 disabled="<%= String.valueOf( !canModify ) %>"
                 onChange="fieldChanged()"
    />    
  </td>
</tr>

<tr>
  <td class="etichetta">Esito disp. rosa</td>
  <td class="campo">
  <af:textBox name="STRDISPONIBILITAROSA"
              size="30"       
              title="Esito disp. rosa"       
              classNameBase="input"
              readonly="true"
              value="<%=strDisponibilitaRosa%>"/>
  </td>
</tr>
<!-- Invio SMS -->
<tr>
  <td class="etichetta">Inviato (e-mail o SMS)</td>
  <td class="campo">
  <af:textBox name="FLGINVIATOSMS"
              size="30"       
              title="Inviato"       
              classNameBase="input"
              readonly="true"
              value="<%=flgInviatoSMS%>"/>
  </td>
</tr>

<tr>
  <td class="etichetta">Destinatario (cell. o indirizzo e-mail)</td>
  <td class="campo">
  <af:textBox name="STRCELLSMSINVIO"
              size="80"       
              title="Destinatario"       
              classNameBase="input"
              readonly="true"
              value="<%=strCellSMSInvio%>"/>
  </td>
</tr>



<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">

<tr class="note">
  <td colspan="2">
  <div class="sezione2">Contatto
  <%if(canModify) {%>
      &nbsp;&nbsp;
      <a href="#" onClick="javascript:eliminaContatto();"><img src="../../img/del.gif" alt="Elimina"></a>
  <%}%>
  </div>
  </td>
</tr>

<tr class="note">
  <td class="etichetta">
    <%if(canModify) {%>
      Ricerca&nbsp;Azienda&nbsp;&nbsp;
      <a href="#" onClick="javascript:apriListaAziendeUnita('<%=prgContatto%>','<%=codCpi%>');">
      <img src="../../img/binocolo.gif" alt="Cerca Azienda"></a>
      <br>
      Ricerca&nbsp;Lavoratore&nbsp;&nbsp;
      <a href="#" onClick="javascript:apriListaLavoratori('<%=prgContatto%>','<%=codCpi%>')">
      <img src="../../img/binocolo.gif" alt="Cerca Lavoratore"></a>
    <%}%>
  </td>
  <td class="campo">
    <div><img name="TContatto" id="TContatto" src="<%=ico%>" alt=""/></div>
    <textarea cols=60 rows=4 name="TXTDATICONTATTO" disabled><%=strDatiContatto%></textarea>    
  </td>
</tr>
<tr class="note">  <td colspan="2"><div class="sezione2">&nbsp;&nbsp;</div></td></tr>
<tr>
  <td class="etichetta">Da Ricontattare</td>
  <td class="campo">
    <!--select name="FLGRICONTATTARE">
      <option value="" <%if(flgRicontattare.equals("")) {%>SELECTED<%}%> ></option>
      <option value="S"  <%if(flgRicontattare.equals("S")) {%>SELECTED<%}%> >S&igrave;</option>
      <option value="N"  <%if(flgRicontattare.equals("N")) {%>SELECTED<%}%> >No</option>
    </select-->
    <af:comboBox name="FLGRICONTATTARE" 
                 size="1" 
                 title="Da Ricontattare"
                 multiple="false" 
                 required="false"
                 focusOn="false" 
                 moduleName=""
                 selectedValue="<%=flgRicontattare%>" 
                 addBlank="true" 
                 blankValue=""
                 classNameBase="input"
                 disabled="<%= String.valueOf( !canModify ) %>"
                 onChange="fieldChanged()">    
      <option value="S"  <%if(flgRicontattare.equals("S")) {%>SELECTED<%}%> >S&igrave;</option>
      <option value="N"  <%if(flgRicontattare.equals("N")) {%>SELECTED<%}%> >No</option>           
    </af:comboBox>
    
    &nbsp;&nbsp;entro il&nbsp;&nbsp;
    <af:textBox name="DATENTROIL" 
                size="11"
                maxlength="10"
                required="false"
                type="date"
                title="Ricontattare Entro il"
                validateOnPost="true"
                value="<%=datEntroIl%>"
                onKeyUp="fieldChanged();"
                classNameBase="input"
                readonly="<%=String.valueOf(!canModify)%>"
                
    />
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<%if(canModify) { %>
  <tr>
    <td colspan="2" align="center">
    <input type="submit" class="pulsanti" name="SALVA" value="<%=btnSalva%>">  
    &nbsp;&nbsp;
    <input type="reset" class="pulsanti" name="reset" value="Annulla" onClick="annulla()">
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
        <input name="strCodiceFiscaleAz" type="hidden" value="<%=strCodiceFiscaleAz%>"/>
        <input name="piva" type="hidden" value="<%=piva%>"/>
        <input name="strRagSoc" type="hidden" value="<%=strRagSoc%>"/>
        <input name="strCodiceFiscale" type="hidden" value="<%=strCodiceFiscale%>"/>
        <input name="strCognome" type="hidden" value="<%=strCognome%>"/>
        <input name="strNome" type="hidden" value="<%=strNome%>"/>
        <input name="mese" type="hidden" value="<%=mese%>"/>
        <input name="anno" type="hidden" value="<%=anno%>"/>
        <input name="dataDal" type="hidden" value="<%=dataDal%>"/>
        <input name="dataAl" type="hidden" value="<%=dataAl%>"/>
  <% } else  { %>
           <%if(mod.equals("3")) {%>
	          <input name="emailoSMS" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"emailoSMS")%>"/>
              <input name="sel_operatore" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore")%>"/>
              <input name="sel_tipo" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_tipo")%>"/>
              <input name="STRIO" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"STRIO")%>"/>
              <input name="sel_motivo" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_motivo")%>"/>
              <input name="effettoCon" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"effettoCon")%>"/>
              <input name="strCodiceFiscale" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale")%>"/>
              <input name="strCodiceFiscaleAz" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscaleAz")%>"/>
              <input name="strCognome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome")%>"/>
              <input name="strNome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strNome")%>"/>
              <input name="piva" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"piva")%>"/>
              <input name="strRagSoc" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc")%>"/>
              <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDal")%>"/>
              <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
    	   <% } %>
 	   <% } %>
<%}%>

<tr>
  <td colspan="2" align="center">
  <input type="button" class="pulsanti" name="INDIETRO" value="<%=btnChiudi%>" onCLick="javascript:conferma('BACK');">  
  </td>
</tr>
</table>
</af:form>
<% if(testata!=null) { %>
  <div align="center">
  <%testata.showHTML(out);%>
  </div>
<%}%>
<%out.print(htmlStreamBottom);%>


