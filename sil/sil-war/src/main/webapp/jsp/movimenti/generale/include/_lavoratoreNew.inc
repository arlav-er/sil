<%
  //Parte fissa della visualizzazione dei dati del lavoratore
%>
<tr>
  <td colspan="2">
  <div id="datiLavoratore" style="display:''">
    <!--Tabella con i dati del lavoratore-->
      <table border="0" width="96%" cellpadding="0" cellspacing="0">
        <tr valign="top">
          <td class="etichetta">C.F.&nbsp;
          	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strCodiceFiscaleLav" title="Codice Fiscale Lavoratore"  size="20" maxlength="20" value="<%=strCodiceFiscaleLav%>" readonly="true" required="true"/>
          	 <input type="hidden" name="FLGCFOK"/>
          </td>
          <td class="etichetta" width="15%" nowrap>Nome&nbsp;
          	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strNomeCognomeLav" title="Nome Lavoratore" size="25" value="<%=strNomeCognomeLav%>" readonly="true" required="true"/>
            <input type="hidden" name="strNomeLav" value="<%=strNomeLav%>"/>
            <input type="hidden" name="strCognomeLav" value="<%=strCognomeLav%>"/>
          </td>
          <td class="etichetta" nowrap>Nato il&nbsp;
          	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="datNascLav" title="Data di nascita lavoratore" size="11" maxlength="10" value="<%=datNascLav%>" readonly="true" required="true"/>
          </td>
        </tr>
        </table>
        <table border="0" width="96%" cellpadding="0" cellspacing="0">
        <tr valign="top">
      	  <td class="etichetta" nowrap>STATO&nbsp;OCC.:&nbsp;classif.&nbsp;
          	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="descrStatoOcc" type="text" size="35" maxlength="35" value="<%=descrStatoOcc%>" readonly="true"/>
          </td>
          <td class="etichetta" width="15%" nowrap>inizio&nbsp;
          	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="datInizioOcc" size="16" value="<%=datInizioOcc%>" readonly="true"/>
          </td>
          <td class="etichetta" nowrap>anz. di disoc.&nbsp;
          	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="datAnzOcc" size="11" maxlength="10" value="<%=datAnzOcc%>" readonly="true"/>
          </td>
        </tr>
      </table>
    </div>
 </td>
</tr>
<tr id="datiLav" style='display:none'>
<td>
	<% if( !cdnLavoratore.equals("") ) {
			InfCorrentiLavNew testata = new InfCorrentiLavNew(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
			testata.setSkipLista(true);
  			testata.show(out);
	   }
	%>
	</td>
</tr>
