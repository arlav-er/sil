  <table align="center" width="100%">

  <tr>
  <td class="etichetta" nowrap>Tipo di competenza &nbsp;</td>
  <td class="campo" nowrap>
  <af:comboBox name="CODTIPOCOMPETENZA" size="1" title="Tipo di competenza"
                     multiple="false" required="true"
                     focusOn="false" moduleName="M_GetTipiCompetenza"
                     classNameBase="input"
                     disabled="<%= String.valueOf(!canManage) %>"
                     addBlank="true" blankValue="" selectedValue="<%=codTipoCompetenza%>"
                     onChange="javascript:caricaCompetenze(MainForm.CODTIPOCOMPETENZA.value,'','dettaglio');"/>
  </td></tr>

  <tr>
  <td class="etichetta">Competenza &nbsp;</td>
  <td class="campo">
    <af:comboBox name="CODCOMPETENZA" size="1" title="Competenza"
                     multiple="false" required="true"
                     focusOn="false" 
                     classNameBase="input"
                     disabled="<%= String.valueOf(!canManage) %>"
                     moduleName="M_GETCOMPETENZE"
                     addBlank="true" blankValue="" selectedValue="<%= codCompetenza%>"  />
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
  </table>