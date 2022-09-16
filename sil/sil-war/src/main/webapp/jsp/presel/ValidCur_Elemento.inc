<tr>
  <td class="etichetta">Tipologia di validit&agrave</td>  
  <td class="campo">
    <%String comboDisabled;
    if (modalita=="NEW"){
      comboDisabled=fieldReadOnly;
    }else{
      comboDisabled="true";
    }%>    
    <af:comboBox name="CODTIPOVALIDITA"
                 title="Tipologia di validit&agrave"
                 moduleName="M_ListTipoValid"
                 classNameBase="input"
                 disabled="<%= comboDisabled %>"
                 onChange="fieldChanged()"
                 selectedValue="<%= codTipoValidita %>" />
  </td>
</tr>
<tr>
  <td class="etichetta">Data inizio</td>
  <td class="campo">
    <af:textBox name="DATINIZIOCURR"
                type="date"
                title="Data inizio"
                classNameBase="input"
                value="<%= datInizioCurr %>"
                size="11"
                maxlength="10"
                required="true"
                validateOnPost="true"
                readonly="<%= fieldReadOnly %>"
                onKeyUp="fieldChanged()"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Data fine</td>
  <td class="campo">
    <af:textBox name="DATFINECURR"
                type="date"
                title="Data fine"
                classNameBase="input"
                value="<%= datFineCurr %>"
                size="11"
                maxlength="10"
                required="true"
                validateOnPost="true"
                readonly="<%= fieldReadOnly %>"
                onKeyUp="fieldChanged()"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Stato del curriculum</td>
  <td class="campo">
    <af:comboBox name="CODSTATOLAV"
                 title="Stato del curriculum"
                 classNameBase="input"
                 moduleName="M_ListStatoLav"
                 required="true"
                 disabled="<%= fieldReadOnly %>"
                 onChange="fieldChanged()"
                 selectedValue="<%= codStatoLav%>" 
                 addBlank="true"/>
    
  </td>
</tr>