<%
  //Visualizzazione delle informazioni sul tipo movimento e data comunicazione
%>
<tr>
  <td>
    <div class='sezione2' id='movimento'>
    </div>
      <table width="96%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="campo" nowrap>Tipo Movimento<!--</td>
          <td class="campo" nowrap>-->
            <% if (!precedente) {%>
                <af:comboBox classNameBase="input" name="CODTIPOMOV" moduleName="ComboTipoMovimento" title="Tipo di movimento" required="true" addBlank="true" selectedValue="<%=codTipoMov%>" onChange="fieldChanged();gestisciPrecedente();gestisciLinguette(this.options[this.selectedIndex].value);" disabled="<%=String.valueOf(!canModify || daLinguetta || valida)%>"/>
            <%} else {%>
                <af:comboBox classNameBase="input" name="CODTIPOMOV" moduleName="ComboTipoMovimentoColl" title="Tipo di movimento" required="true" addBlank="true" selectedValue="<%=codTipoMov%>" onChange="fieldChanged();gestisciLinguette(this.options[this.selectedIndex].value);" disabled="<%=String.valueOf(!canModify || daLinguetta || valida)%>"/>
              <%}%>        
          </td>
        </tr>
      </table>           
  </td>
</tr>