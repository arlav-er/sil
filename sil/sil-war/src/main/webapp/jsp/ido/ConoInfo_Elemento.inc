  <table align="center" width="100%">
  <tr>
  <td class="etichetta" nowrap>Tipo &nbsp;</td>
  <td class="campo" nowrap>
  <af:comboBox name="CODTIPOINFO" size="1" title="Tipo Conoscenza"
                     multiple="false" required="true"
                     classNameBase="input"
                     disabled="<%= String.valueOf(!canManage) %>"
                     focusOn="false" moduleName="M_ListTipiConoscenzaInfo" 
                     addBlank="true" blankValue="" selectedValue="<%=codTipo%>"                     
                     onChange="inibisciScelta(this, 'NT');caricaDettInfo(MainForm.CODTIPOINFO.value,'','dettaglio')"/>
  <tr>
  <td class="etichetta">Dettaglio &nbsp;</td>
  <td class="campo">
    <af:comboBox name="CODICE" size="1" 
                 title="Dettaglio"
                 classNameBase="input"
                 required="true"
                 disabled="<%= String.valueOf(!canManage) %>"
                 moduleName="M_LISTDETTAGLIALLCONOSCENZAINFO"
                 addBlank="true" blankValue="" selectedValue="<%=codDettTipo%>">               
    </af:comboBox>
  </td>
  </tr>
  <tr>
  <td class="etichetta" nowrap>Livello &nbsp;</td>
  <td class="campo" nowrap>
  <af:comboBox name="CDNGRADO" size="1" title="Livello Conoscenza"
                     multiple="false" required="true"
                     classNameBase="input"
                     disabled="<%= String.valueOf(!canManage) %>"
                     focusOn="false" moduleName="M_ListGradiConoscenzaInfo"
                     addBlank="true" blankValue="" selectedValue="<%=strGradoConoscenza%>"/>
  </td></tr>
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
  </table>