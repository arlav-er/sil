<tr>
  <td class="etichetta" nowrap>Nazionalità</td>
  <td class="campo" nowrap>
      <af:comboBox name="CODCITTADINANZA" size="1" title="Cittadinanza"
                     multiple="false" required="true"
                     classNameBase="input"
                     disabled="<%=String.valueOf(!canManage)%>"
                     focusOn="false" moduleName="M_ListCittadinanza"
                     addBlank="true" blankValue="" selectedValue="<%=codCittadinanza%>"
                     onChange="fieldChanged();inibisciScelta(this, 'NT')"/>
  </td>
</tr>

<tr valign="top"><td colspan="2">
<div id="MOTIVAZIONE_NAZIONALITA" style="display:<%=displayMotNaz%>">
<table colspacing="0" cellspacing="0" border="0" width="100%">
<tr>
<td class="etichetta">Motivazione</td>
  <td class="campo2" colspan="3" nowrap>
    <af:comboBox classNameBase="input"
          onChange="fieldChanged();AbilitaAltraMotivNaz();"
          name="codMotNazionalita"
          required="true"
          title="Motivazione"
          selectedValue="<%=codMotNazionalita%>"
          disabled="<%= String.valueOf(!canManage) %>"
          moduleName="COMBO_MOTIVO_NAZIONALITA"
          addBlank="true" />
   </td>
</tr>  

<tr>  
<td class="etichetta"></td>
<td class="campo">
 <af:textArea classNameBase="textarea"
    name="Motivazione" cols="50" value="<%=strMotivazione%>" 
    required="false" maxlength="100" readonly="<%= String.valueOf(!canManage) %>"
    onKeyUp="fieldChanged()"/>
   <script language="javascript">
    	if (<%=codMotNazionalita.equals("ALT")%>) {
        	document.DivForm.Motivazione.disabled = false;
    	}
        else {
          	document.DivForm.Motivazione.disabled = true;
       	}
  	</script>
  </td>
</tr>
</table>
</div>
</td></tr>



 