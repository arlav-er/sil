<tr>
    <td class="etichetta">Descrizione</td>
    <!-- NOTE: Per af:textArea / af:textBox la gestione delle abilitazioni 
         si ha con gli attributi seguenti 
         - classNameBase : viene cercato il CSS [name]View o [name]Edit
         - readonly
         - onKeyUp : attiva il flag di changed
    -->
    <td class="campo">
      <af:textArea 
        cols="30" 
        rows="4" 
        name="STRDESCINFO" 
        classNameBase="textarea"
        readonly="<%= String.valueOf( !canModify ) %>"
        onKeyUp="fieldChanged()"
        maxlength="100"
        value="<%= strDescInfo %>" />
    </td>
</tr>
<tr>
  <td class="etichetta">Livello</td>
  <td class="campo">
    <!-- NOTE: Per af:comboBox la gestione delle abilitazioni si ha con 
         gli attributi seguenti 
         - classNameBase : viene cercato il CSS [name]View o [name]Edit
         - disabled
         - onChange : attiva il flag di changed
    -->
    <af:comboBox name="CDNGRADO"
      title="Livello di conoscenza"
      moduleName="M_ListGradiConoscenzaInfo"
      classNameBase="input"
      disabled="<%= String.valueOf( !canModify ) %>"
      onChange="fieldChanged()"
      selectedValue="<%= (cdnGrado != null) ? cdnGrado.toString() : \"\" %>" 
      addBlank="true" 
      required="true"  />
  </td>
</tr>
<tr>
  <td class="etichetta">Modalità di acquisizione</td>
  <td class="campo">
    <af:comboBox name="CODMODOINFO"
      title="Modalità acquisizione"
      moduleName="M_ListModiConoscenzaInfo"
      classNameBase="input"
      disabled="<%= String.valueOf( !canModify ) %>"
      onChange="fieldChanged()"
      selectedValue="<%= (codModoInfo != null) ? codModoInfo.toString() : \"\" %>" 
      addBlank="true"/>
  </td>
</tr>
<tr>
    <td class="etichetta">Note</td>
    <td class="campo">
      <af:textArea cols="30" 
        rows="4" 
        name="STRMODINFO" 
        classNameBase="textarea"
        readonly="<%= String.valueOf( !canModify ) %>"
        onKeyUp="fieldChanged()"
        maxlength="100"
        value="<%= strModInfo %>" />
    </td>
</tr>
<tr>
  <td class="etichetta">Conoscenza certificata ?</td>
  <td class="campo">
    <af:comboBox 
      title="Conoscenza certificata" 
      name="FLGCERTIFICATO"
      classNameBase="input"
      disabled="<%= String.valueOf( !canModify ) %>"
      onChange="fieldChanged()"
    >
      <option value=""  <% if ( "".equals(flgCertificato) )  { out.print("SELECTED=\"true\""); } %> ></option>
      <option value="S" <% if ( "S".equals(flgCertificato) ) { out.print("SELECTED=\"true\""); } %> >Si</option>
      <option value="N" <% if ( "N".equals(flgCertificato) ) { out.print("SELECTED=\"true\""); } %> >No</option>
    </af:comboBox>     
  </td>
</tr>
