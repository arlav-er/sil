<p align="center">
<table class="main">
<tr>
  <td class="etichetta">Data</td>
  <td class="campo">
  <af:textBox name="DATCONTATTO"
              size="11"
              maxlength="10"
              required="true"
              type="date"
              validateOnPost="true"
              value="<%=datContatto%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Orario</td>
  <td class="campo">
  <af:textBox name="STRORACONTATTO"
              size="5"
              maxlength="5"
              required="true"
              type="time"
              title="Orario"
              validateOnPost="true"
              value="<%=strOraContatto%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Operatore</td>
    <td class="campo">
      <af:comboBox name="PRGSPICONTATTO" size="1" title="Operatore"
                     multiple="false" disabled="false" required="true"
                     focusOn="false" moduleName="COMBO_SPI_SCAD"
                     selectedValue="<%=strSpiContatto%>" addBlank="true" blankValue=""/>
    </td>
</tr>
<tr>
  <td class="etichetta">Tipo</td>
  </td>
  <td class="campo">
    <af:comboBox name="PRGTIPOCONTATTO"
                 size="1" 
                 title="Tipo di Contatto"
                 multiple="false" 
                 disabled="false" 
                 required="true"
                 focusOn="false" 
                 moduleName="COMBO_TIPO_CONTATTO_AG"
                 selectedValue="<%=strTipoContatto%>" 
                 addBlank="true" 
                 blankValue=""/>
    &nbsp;&nbsp;
    <af:comboBox name="STRIO"
                 size="1" 
                 title="Tipo di Contatto"
                 multiple="false" 
                 disabled="false" 
                 required="true"
                 focusOn="false" 
                 selectedValue="<%=strDirezioneContatto%>" 
                 addBlank="true" 
                 blankValue="">
      <option value="O"  <%if(strDirezioneContatto.equals("O")) {%>SELECTED<%}%> >Dal CpI</option>
      <option value="I"  <%if(strDirezioneContatto.equals("I")) {%>SELECTED<%}%> >Al CpI</option>
    </af:comboBox>
  </td>
</tr>


<tr class="note">
  <td class="etichetta">Note</td>
  <td class="campo">
    <af:textArea classNameBase="textarea"
    			 maxlength="1000"
    			 name="TXTCONTATTO" 
                 cols="60" 
                 rows="4" 
                 title="Note"
                 value="<%=txtContatto%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Motivo</td>
  </td>
  <td class="campo">
    <af:comboBox name="PRGMOTCONTATTO" 
                 size="1" 
                 title="Motivo del Contatto"
                 multiple="false" 
                 disabled="false" 
                 required="false"
                 focusOn="false" 
                 moduleName="COMBO_MOTIVO_CONTATTO_AG"
                 selectedValue="<%=strMotivoContatto%>" 
                 addBlank="true" 
                 blankValue=""/>    
  </td>
</tr>
<tr>
  <td class="etichetta">Effetto</td>
  </td>
  <td class="campo">
    <af:comboBox name="PRGEFFETTOCONTATTO" 
                 size="1" 
                 title="Effetto del Contatto"
                 multiple="false" 
                 disabled="false" 
                 required="false"
                 focusOn="false" 
                 moduleName="COMBO_EFFETTO_CONTATTO_AG"
                 selectedValue="<%=strEffettoContatto%>" 
                 addBlank="true" 
                 blankValue=""/>
  </td>
</tr>

<tr>
  <td class="etichetta">Da Ricontattare</td>
  <td class="campo">
    <select name="FLGRICONTATTARE" onchange="javascript:aggiornaDataEntroIl();">
      <option value="" <%if(flgRicontattare.equals("")) {%>SELECTED<%}%> ></option>
      <option value="S"  <%if(flgRicontattare.equals("S")) {%>SELECTED<%}%> >S&igrave;</option>
      <option value="N"  <%if(flgRicontattare.equals("N")) {%>SELECTED<%}%> >No</option>
    </select>
    &nbsp;&nbsp;entro il&nbsp;&nbsp;
    <af:textBox name="DATENTROIL" 
                size="11"
                maxlength="10"
                required="false"
                type="date"
                title="Ricontattare Entro il"
                validateOnPost="true"
                value="<%=datEntroIl%>"/>
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  <input type="submit" class="pulsanti" name="salva" value="Salva">
  &nbsp;&nbsp;
  <input type="reset" class="pulsanti" name="reset" value="Annulla">
  </td>
</tr>
</table>
</p>

<script language="javascript">
  document.frmContatto.DATENTROIL.disabled = true;
</script>
