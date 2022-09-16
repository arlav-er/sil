<tr class="note">
  <td colspan="2">
  <div class="sezione2">Azienda
  &nbsp;&nbsp;
  <% if ( canModify ) { %> 
  <a href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
  <%}%>
  </div>
  </td>
<tr>
<tr>
  <td class="etichetta">Azienda</td>
  <td class="campo">
    <af:textBox name="STRRAGIONESOCIALE"
                type="text"
                title="Ragione sociale"
                value="<%= strRagioneSociale %>"
                maxlength="200"
                size="45"
                required="true"
                readonly="true"
                classNameBase="input"
                onKeyUp="fieldChanged();cancellaCodiceAzienda();"
                validateOnPost="true"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Nota</td>
  <td class="campo">
     <af:textArea cols="30" 
              rows="4" 
              name="STRNOTA" 
              readonly="<%= fieldReadOnly %>"
              classNameBase="textarea"
              onKeyUp="fieldChanged()"
              maxlength="100" value="<%= strNota %>"/>
  </td>
</tr>
