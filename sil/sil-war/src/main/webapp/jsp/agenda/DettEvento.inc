<script type="text/javascript">
    // Per rilevare la modifica dei dati da parte dell'utente
    var flagChanged = false;  
    
    
    function fieldChanged() {
        <%if (canModify) {out.print("flagChanged = true;");}%>
    }
    
    function checkSubmit()
    {
        if (flagChanged==true){
          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return(false); }
          else { return(true); }
        } else { return(true); }
    }
</script>
<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<p align="center">
  <font color="green"><af:showMessages prefix="MSALVAEVENTO"/></font>
  <font color="red"><af:showErrors /></font>
</p>
<%out.print(htmlStreamTop);%>
  <table class="main">
  <af:form name="formEvento" action="AdapterHTTP" method="POST">
  <input name="PAGE" type="hidden" value="PEvento"/>
  <input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
  <input name="meseDB" type="hidden" value="<%=meseDB%>"/>
  <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
  <input name="giorno" type="hidden" value="<%=giorno%>"/>
  <input name="mese" type="hidden" value="<%=mese%>"/>
  <input name="anno" type="hidden" value="<%=anno%>"/>
  <input name="MOD" type="hidden" value="<%=mod%>"/>
  <input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
  <input name="CODCPI" type="hidden" value="<%=codCpi%>"/>

  <input name="codCpiEvento" type="hidden" value="<%=codCpi%>"/>
  <input name="prgEvento" type="hidden" value="<%=prgEvento%>"/>
  <input name="numKloEvento" type="hidden" value="<%=numKloEvento%>"/>
  <tr>
    <td class="etichetta">Data</td>
    <td class="campo">
    <%if(canModify) {%>
        <af:textBox name="datevento"
                value="<%=data%>"
                size="11"
                maxlength="10"
                disabled="true"
                title="Data Evento"
                alt="Data"
                type="date"
                validateOnPost="true"
    />
    <%} else {%>
        <af:textBox name="datevento"
                value="<%=data%>"
                size="11"
                maxlength="10"
                title="Data Evento"
                alt="Data"
                type="date"
                validateOnPost="true"
                classNameBase="input"
                readonly="<%=String.valueOf(!canModify)%>"
                onKeyUp="fieldChanged();"
    />
    <%}%>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Breve Descrizione</td>
    <td class="campo">
    <af:textBox name="strDescrizioneBreve"
                value="<%=strDescrizioneBreve%>"
                size="60"
                maxlength="100"
                title="Breve Descrizione"
                alt="Breve Descrizione"
                required="true"
                type="text"
                onKeyUp="fieldChanged();"
                classNameBase="input"
                readonly="<%=String.valueOf(!canModify)%>"
    />
    </td>
  </tr>
  <tr class="note">
    <td class="etichetta">Descrizione Estesa</td>
    <td class="campo">
    <!--textarea name="txtEvento" cols="60" rows="4"><%=txtEvento%></textarea-->
    <af:textArea name="txtEvento" 
                 cols="60" 
                 rows="4" 
                 title="Descrizione Estesa"
                 value="<%=txtEvento%>"
                 onKeyUp="fieldChanged();"
                 classNameBase="input"
                 readonly="<%=String.valueOf(!canModify)%>"
    />
    </td>
  </tr>
  <tr>
    <td class="etichetta">Data Pubblicazione</td>
    <td class="campo">
    <af:textBox name="datInizioPub"
                value="<%=datInizioPub%>"
                size="10"
                maxlength="10"
                title="Data Pubblicazione"
                alt="Data Pubblicazione"
                type="date"
                validateOnPost="true"
                onKeyUp="fieldChanged();"
                classNameBase="input"
                readonly="<%=String.valueOf(!canModify)%>"
    />
    </td>
  </tr>
  <tr>
    <td class="etichetta">Evento Pubblico</td>
    <td class="campo">
    <af:comboBox name="flgPubblico"
                 size="1"
                 title="Evento Pubblico"
                 required="true"
                 moduleName=""
                 selectedValue="<%=flgPubblico%>"
                 addBlank="true"
                 blankValue=""
                 classNameBase="input"
                 disabled="<%= String.valueOf( !canModify ) %>"
                 onChange="fieldChanged()">
      <option value="S" <%if(flgPubblico.equals("S")) {%>SELECTED<%}%> >S&igrave;</option>
      <option value="N" <%if(flgPubblico.equals("N")) {%>SELECTED<%}%>>No</option>
    </af:comboBox>
    </td>
  </tr>
  <tr><td colspan="2">&nbsp;</td></tr>
  <%if(canModify) {%>
      <tr>
        <td colspan="2" align="center">
        <input type="submit" class="pulsanti" value="<%=btnSalva%>" name="SALVA"/>
        &nbsp;
        &nbsp;
        <input type="reset" class="pulsanti" value="Annulla" name="ann_ev"/>
        </td>
      </tr>
  <%}%>
  </af:form>
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr>
    <td colspan="2" align="center">
    <af:form name="backAg" action="AdapterHTTP" method="POST" dontValidate="true">
      <input name="PAGE" type="hidden" value="PCalendario"/>
      <input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
      <input name="meseDB" type="hidden" value="<%=meseDB%>"/>
      <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
      <input name="giorno" type="hidden" value="<%=giorno%>"/>
      <input name="mese" type="hidden" value="<%=mese%>"/>
      <input name="anno" type="hidden" value="<%=anno%>"/>
      <input name="MOD" type="hidden" value="<%=mod%>"/>
      <input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
      <input name="CODCPI" type="hidden" value="<%=codCpi%>"/>

      <input type="submit" class="pulsanti" name="back" value="<%=btnChiudi%>" onClick="return checkSubmit()"/>
    </af:form>
    </td>
  </tr>
  </table>
  <% if(testata!=null) { %>
  	  <div align="center">
      <%testata.showHTML(out);%>
      </div>
  <%}%>
  <%out.print(htmlStreamBottom);%>


  <%//out.print(serviceResponse.toXML());%>