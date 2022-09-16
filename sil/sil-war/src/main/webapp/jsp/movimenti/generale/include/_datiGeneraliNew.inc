<tr>
  <td>
    <div class='sezione2' id='movimento'></div>
      <table width="96%" border="0" cellpadding="0" cellspacing="0">
        <tr><td></td></tr>
        <tr>
          <td class="campo" nowrap>Tipo Movimento
            <% if (!precedente) {%>
                <af:comboBox classNameBase="input" name="CODTIPOMOV" moduleName="ComboTipoMovimento" title="Tipo di movimento" required="true" addBlank="true" selectedValue="<%=codTipoMov%>" onChange="fieldChanged();gestisciPrecedente();gestisciCampi();" disabled="<%=String.valueOf(!canModify || valida)%>"/>
            <%} else {%>
                <af:comboBox classNameBase="input" name="CODTIPOMOV" moduleName="ComboTipoMovimentoColl" title="Tipo di movimento" required="true" addBlank="true" selectedValue="<%=codTipoMov%>" onChange="fieldChanged();gestisciCampi();" disabled="<%=String.valueOf(!canModify || valida)%>"/>
              <%}%>        
          </td>
        </tr>
      </table>           
  </td>
</tr>