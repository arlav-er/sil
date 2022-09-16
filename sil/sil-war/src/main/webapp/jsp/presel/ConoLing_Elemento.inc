

<script language="javascript">
  function ControllaGrado() {
    if (document.MainForm.CDNGRADOSCRITTO.value == '') {
      document.MainForm.CDNGRADOSCRITTO.value = document.MainForm.CDNGRADOLETTO.value;
    }
    if (document.MainForm.CDNGRADOPARLATO.value == '') {
      document.MainForm.CDNGRADOPARLATO.value = document.MainForm.CDNGRADOLETTO.value;
    }
  }
</script>

<%


  if (strGradoLetto != null) {
    newCdnGradoLetto=strGradoLetto.toString();
  }
  if (strGradoScritto != null) {
    newCdnGradoScritto=strGradoScritto.toString();
  }
  if (strGradoParlato != null) {
    newCdnGradoParlato=strGradoParlato.toString();
  }
%>







<tr>
  <td class="etichetta">Lingua</td>
  <td class="campo">
    <af:comboBox name="CODLINGUA"
                 title="Lingua"
                 required="true"
                 moduleName="M_ListLingue"
                 addBlank="true"
                 classNameBase="input"
                 disabled="<%= fieldReadOnly %>"
     
                 selectedValue="<%= codLingua %>" />
  </td>
</tr>

<tr>
  <td align="left" colspan="2"><b>Livello di conoscenza</b></td>
</tr>
<tr>
  <td colspan="2"><HR></td>
</tr>
<tr>
  <td class="etichetta">Letto</td>
  <td class="campo">
    <af:comboBox name="CDNGRADOLETTO"
                 title="Grado conoscenza letto"
                 moduleName="M_ListGradoLingue"
                 classNameBase="input"
                 addBlank="true" 
                 required="true" 
                 disabled="<%= fieldReadOnly %>"
                
                 selectedValue="<%= newCdnGradoLetto %>" />
  </td>
</tr>
<tr>
  <td class="etichetta">Scritto</td>
  <td class="campo">
    <af:comboBox name="CDNGRADOSCRITTO"
                 title="Grado conoscenza scritto"
                 moduleName="M_ListGradoLingue"
                 classNameBase="input"
                 addBlank="true"
                 disabled="<%= fieldReadOnly %>"
           
                 selectedValue="<%= newCdnGradoScritto %>" />
  </td>
</tr>
<tr>
  <td class="etichetta">Parlato</td>
  <td class="campo">
    <af:comboBox name="CDNGRADOPARLATO"
                 title="Grado conoscenza scritto"
                 moduleName="M_ListGradoLingue"
                 classNameBase="input"
                 addBlank="true"
                 disabled="<%= fieldReadOnly %>"
              
                 selectedValue="<%= newCdnGradoParlato %>" />
  </td>
</tr>

<tr>
  <td colspan="2"><HR></td>
</tr>
<tr>
  <td class="etichetta">Modalità di Acquisizione</td>
  <td class="campo">
    <af:comboBox name="CODMODLINGUA"
                 title="Modalità di acquisizione"
                 moduleName="M_ListModLingue"
                 classNameBase="input"
                 addBlank="true"
                 disabled="<%= fieldReadOnly %>"
                
                 selectedValue="<%= codModLingua %>" />
  </td>
</tr>
<tr>
  <td class="etichetta">Altra Modalità</td>
  <td class="campo">
    <af:textBox name="STRMODLINGUA" classNameBase="input" maxlength="100" title="Altra Modalità" value="<%= strModLingua %>" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Conoscenza certificata ?</td>
  <td class="campo">
    <af:comboBox 
      title="Conoscenza certificata" 
      name="FLGCERTIFICATO"
      classNameBase="input"
      disabled="<%= fieldReadOnly %>"
      onChange="fieldChanged()">
  
      <option value=""  <% if ( "".equals(flgCertificato) )  { %>SELECTED<% } %> ></option>
      <option value="S" <% if ( "S".equals(flgCertificato) ) { %>SELECTED<% } %> >Si</option>
      <option value="N" <% if ( "N".equals(flgCertificato) ) { %>SELECTED<% } %> >No</option>
    </af:comboBox>
  </td>
</tr>
<!-- <tr>
<td class="etichetta">Prima lingua ?</td>
  <td class="campo">
    <af:comboBox 
      title="Conoscenza certificata" 
      name="FLGPRIMALINGUA"
      classNameBase="input"
      disabled="<%= fieldReadOnly %>"
      onChange="fieldChanged()">
  
      <option value=""  <% if ( "".equals(flgPrimaLingua) )  { %>SELECTED<% } %> ></option>
      <option value="S" <% if ( "S".equals(flgPrimaLingua) ) { %>SELECTED<% } %> >Si</option>
      <option value="N" <% if ( "N".equals(flgPrimaLingua) ) { %>SELECTED<% } %> >No</option>
    </af:comboBox>
  </td>
</tr> -->
