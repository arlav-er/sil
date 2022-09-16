<%-- 28/10/2004 (Andrea) Aggiunte variabili contenenti le funzioni js aggiuntive da chiamare sull' onclick dei bottoni --%>
<tr class="note">
    <td colspan="2">
      <div class="sezione2">
          <img id='tendinaLav' alt='Chiudi' src='../../img/aperto.gif' onclick="cambiaTendina(this,'lavoratoreSez', document.Frm1.nome);" />&nbsp;&nbsp;&nbsp;Lavoratore
      &nbsp;&nbsp;
      <% if (strNomeLav.equals("")){ %>
        <a href="#" onClick="javascript:apriSelezionaSoggetto('Lavoratori', 'aggiornaLavoratore');<%=fSelLav%>"><img src="../../img/binocolo.gif" alt="Cerca"></a>
      <%}%>      
      </div>
    </td>
</tr>
<tr>
  <td colspan="2">
    <div id="lavoratoreSez" style="display: none;">
      <table class="main" width="100%" border="0">
          <tr>
            <td class="etichetta">Codice Fiscale</td>
            <td class="campo" valign="bottom">
              <af:textBox classNameBase="input" type="text" name="codiceFiscaleLavoratore" readonly="true" value="<%=strCodiceFiscaleLav%>" size="30" maxlength="16"/>
              &nbsp;&nbsp;&nbsp;Validità C.F.&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGCFOK" readonly="true" value="<%=strFlgCfOk%>" size="3" maxlength="3"/>
            </td>
          </tr>
          <tr >
            <td class="etichetta">Cognome</td>
            <td class="campo">
              <af:textBox classNameBase="input" type="text" name="cognome" readonly="true" value="<%=strCognomeLav%>" size="30" maxlength="50"/>
            </td>
          </tr>
          <tr>
            <td class="etichetta">Nome</td>
            <td class="campo">
              <af:textBox classNameBase="input" type="text" name="nome" readonly="true" value="<%=strNomeLav%>" size="30" maxlength="50"/>
            </td>
          </tr>
      </table>
    </div>
  </td>
</tr>