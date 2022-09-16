  <tr>
  <td class="etichetta" nowrap>Profilo n.&nbsp;</td>
  <td class="campo" nowrap><INPUT type="text" name="prgAlternativa" size="2" value="<%=strAlternativa%>" READONLY class="inputView"/></td>
  </tr>
  <tr>
  <td class="etichetta" nowrap>Agevolazioni &nbsp;</td>
  <td class="campo" nowrap>
  <af:comboBox name="CODICE"
                   title="Agevolazioni"
                   multiple="true" required="true"
                   classNameBase="input"
                   disabled="<%= String.valueOf(!canManage) %>"
                   size="5" selectedValue="<%=codAgevolazione%>"
                   moduleName="M_ListAgevolazioni"/>
  </td>
  </tr>
  <tr>
  <td class="etichetta" nowrap>Indispensabile &nbsp;</td>
    <td class="campo" nowrap>
    <af:comboBox name="FLGINDISPENSABILE"
                 title="Indispensabile" required="false"
                 classNameBase="input"
                 disabled="<%= String.valueOf(!canManage) %>"
                 selectedValue="<%= strFlgIndispensabile %>">
      <option value=""  <% if ( "".equals(strFlgIndispensabile) )  { out.print("SELECTED=\"true\""); } %> ></option>
      <option value="S" <% if ( "S".equals(strFlgIndispensabile) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
      <option value="N" <% if ( "N".equals(strFlgIndispensabile) ) { out.print("SELECTED=\"true\""); } %> >No</option>
    </af:comboBox>  
    </td>
  </tr>