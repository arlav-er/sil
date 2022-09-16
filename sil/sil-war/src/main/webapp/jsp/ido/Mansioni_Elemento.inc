<tr>
  <td class="etichetta" nowrap>Profilo n.&nbsp;</td>
  <td class="campo" nowrap><INPUT type="text" name="PRGALTERNATIVA" size="2" value="<%=strAlternativa%>" READONLY class="inputView"/></td>
</tr>
<tr valign="top">
    <td class="etichetta">Ricerca limitata alle sole mansioni di uso frequente</td>
    <td class="campo">
      <input type="checkbox" name="flgFrequente" value="" checked="true" />
    </td>
</tr>
 <tr>
    <td class="etichetta">Codice mansione</td>
    <td class="campo">
      <af:textBox 
        classNameBase="input" 
        name="CODMANSIONE" 
        size="7" 
        maxlength="7" 
        value="<%= codMansione.toString() %>" 
        readonly="<%= String.valueOf(!canManage) %>"/>
      
      <af:textBox 
        type="hidden" 
        name="codMansioneHid" 
        value="<%= codMansione.toString() %>"/>
      
      <% if (canManage) { %>
          <a href="javascript:selectMansione_onClick(Frm1.CODMANSIONE, Frm1.codMansioneHid, Frm1.DESCMANSIONE,  Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
          <A href="javascript:ricercaAvanzataMansioni();">
              Ricerca avanzata
          </A>
      <%}%>
    </td>
  </tr>           
<tr valign="top">
    <td class="etichetta">Tipo</td>
    <td class="campo">
      <af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
      <af:textBox classNameBase="input" name="strTipoMansione" value="<%=desTipoMansione%>" readonly="true" size="48" maxlength="100"/>
    </td>
  </tr>
<tr>
  <td class="etichetta">Mansione</td>
  <td class="campo">
      <af:textArea cols="30" 
                   rows="4" 
                   title="Mansione"
                   name="DESCMANSIONE" 
                   classNameBase="textarea"
                   readonly="true" 
                   required="true"
                   maxlength="100"
                   value="<%= descMansione %>" />
  </td>
</tr>
<%if ("1".equalsIgnoreCase(strAlternativa)){%>
<tr>

	<%if (conf_ClicLav.equalsIgnoreCase("0")) {%>
		<td class="etichetta">Invia a Cliclavoro/Blen</td>
	<%}else {%>
		<td class="etichetta">Invia a Portale Regionale/Cliclavoro/Blen</td>
	<%}%>
	<td class="campo">
		<input type="checkbox" name="INVIA_CLIC_LAVORO" value="" <%=flgInvioCL.equals("S") ? "CHECKED" : ""%> 
			   onclick="settaFlag();">
	</td>
	<input type="hidden" name="FLGINVIOCL" value="<%=flgInvioCL%>"/>
</tr>
<%
}
%>
