<table width="80%" cellspacing="2">
  <tr valign="top">
      <td class="etichetta">Ricerca limitata alle sole mansioni di uso frequente</td>
      <td class="campo">
          <input type="checkbox" name="flgFrequente" value="" <%if (!canModify){%>disabled="true" <%}%>/>
      </td>
  </tr>
 <tr>
    <td class="etichetta">Codice mansione</td>
    <td class="campo">
      <af:textBox 
      	readonly="<%= String.valueOf(!canModify) %>"
        name="CODMANSIONE" 
        size="7" 
        maxlength="7" 
        value="<%= codMansione.toString() %>" 
      />
      
      <af:textBox 
        type="hidden" 
        name="codMansioneHid" 
        value="<%= codMansione.toString() %>" 
      />
      
     
          <a id="D08_link_1" href="javascript:selectMansione_onClick(Frm1.CODMANSIONE, Frm1.codMansioneHid, Frm1.DESCMANSIONE,  Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
          <A id="D08_link_2" href="javascript:ricercaAvanzataMansioni();">
              Ricerca avanzata
          </A>
     
    </td>
  </tr>           
<tr valign="top">
    <td class="etichetta">Tipo</td>
    <td class="campo">
      <af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
      <af:textBox classNameBase="input" name="strTipoMansione" value="<%=desTipoMansione%>" readonly="true" size="48" />
    </td>
  </tr>
<tr>
  <td class="etichetta">Descrizione</td>
  <td class="campo">
      <af:textArea cols="30" 
                   rows="2" 
                   name="DESCMANSIONE" 
                   classNameBase="textarea"
                   readonly="true" 
                   maxlength="100"
                   value="<%= descMansione %>" />
  </td>
</tr>

 </table> 