<%
  //Parte fissa della visualizzazione dei dati del lavoratore
%>
<tr>
  <td colspan="2">
    <div id="datiLavoratore">
      <!--Tabella con i dati del lavoratore-->
      <table border="0" width="96%" cellpadding="0" cellspacing="0">
        <tr valign="top">
          <td class="etichetta" width="15%">C.F.</td>
          <td class="campo" width="15%" nowrap>
              <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strCodiceFiscaleLav" title="Codice Fiscale Lavoratore"  size="22" maxlength="16" value="<%=strCodiceFiscaleLav%>" readonly="true" required="true"/>
              &nbsp;Validità C.F.&nbsp;<af:textBox classNameBase="input" name="FLGCFOK" title="Validità C.F." size="2" value="<%=strFlgCfOk%>" readonly="true" />
          </td>
          <td class="etichetta" width="15%" nowrap>Cognome<BR/>Nome</td>
          <td class="campo" width="15%" nowrap>
            <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strNomeCognomeLav" title="Nome Lavoratore" size="25" value="<%=strNomeCognomeLav%>" readonly="true" required="true"/>
            <input type="hidden" name="strNomeLav" value="<%=strNomeLav%>"/>
            <input type="hidden" name="strCognomeLav" value="<%=strCognomeLav%>"/>
            <input type="hidden" name="strSessoLav" value="<%=strSessoLav%>"/>
            <input type="hidden" name="codComNascLav" value="<%=codComNascLav%>"/>
            <input type="hidden" name="codCittadinanzaLav" value="<%=codCittadinanzaLav%>"/>
 			<input type="hidden" name="codComDomLav" value="<%=codComDom%>"/>
 			<input type="hidden" name="strIndirizzoDomLav" value="<%=strIndirizzoDom%>"/>
 			<input type="hidden" name="strCapDomLav" value="<%=strCapDom%>"/>
 			<input type="hidden" name="strTelDomLav" value="<%=strTelDom%>"/>
 			<input type="hidden" name="codComResLav" value="<%=codComRes%>"/>
 			<input type="hidden" name="strIndirizzoResLav" value="<%=strIndirizzoRes%>"/>
 			<input type="hidden" name="strCapResLav" value="<%=strCapRes%>"/>
 			<input type="hidden" name="strTelResLav" value="<%=strTelRes%>"/>
 			<input type="hidden" name="datScadenza" value="<%=datScadenzaPermSogg%>"/>
 			<input type="hidden" name="codTipoTitolo" value="<%=codTipoTitolo%>"/>
 			<input type="hidden" name="codTipoTitoloGenerico" value="<%=codTipoTitoloGenerico%>"/>
 			<input type="hidden" name="strCellLav" value="<%=strCellLav%>"/>
 			<input type="hidden" name="strMailLav" value="<%=strMailLav%>"/>
 			<input type="hidden" name="codCpiLav" value="<%=codCpiLav%>"/>
 		  </td>
          <td class="etichetta" nowrap>Data di <br/> nascita</td>
          <td class="campo" width="15%" nowrap>
            <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="datNascLav" title="Data di nascita lavoratore" size="11" maxlength="10" value="<%=datNascLav%>" readonly="true" required="true"/>
          </td>
        </tr>
      </table>
    </div>          
  </td>
</tr>