<script language="javascript">
  function ControllaGrado() {
    if (document.frmMascheraLinguaRichiesta.CDNGRADOSCRITTO.value == '') {
      document.frmMascheraLinguaRichiesta.CDNGRADOSCRITTO.value = document.frmMascheraLinguaRichiesta.CDNGRADOLETTO.value;
    }
    if (document.frmMascheraLinguaRichiesta.CDNGRADOPARLATO.value == '') {
      document.frmMascheraLinguaRichiesta.CDNGRADOPARLATO.value = document.frmMascheraLinguaRichiesta.CDNGRADOLETTO.value;
    }
  }
</script>
<%
  String newCdnGradoLetto="";
  String newCdnGradoScritto="";
  String newCdnGradoParlato="";

  if (cdnGradoLetto != null) {
    newCdnGradoLetto=cdnGradoLetto.toString();
  }
  if (cdnGradoScritto != null) {
    newCdnGradoScritto=cdnGradoScritto.toString();
  }
  if (cdnGradoParlato != null) {
    newCdnGradoParlato=cdnGradoParlato.toString();
  }
%>
<tr valign="top">
  <td class="etichetta">Profilo n.&nbsp;</td>
  <td class="campo"><INPUT type="text" name="PRGALTERNATIVA" size="2" value="<%=strAlternativa%>" READONLY class="inputView"/></td>
</tr>
<tr valign="top">
  <td class="etichetta">Lingua</td>
    <td class="campo">
    <af:comboBox name="CODLINGUA"
                 title="Lingua"
                 moduleName="M_ListLingue"
                 addBlank="true" required="true"
                 classNameBase="input"
                 disabled="<%= String.valueOf(!canManage) %>"
                 selectedValue="<%= codLingua %>" 
                 onChange="inibisciScelta(this, 'NT')"/>
    </td>
</tr>

<tr valign="top">
    <td colspan="3"><br/><div class="sezione2">Livello di conoscenza</div>
</td></tr>
<tr valign="top">
  <td class="etichetta">Letto</td>
    <td class="campo">
    <af:comboBox name="CDNGRADOLETTO"
                 title="Grado conoscenza letto"
                 moduleName="M_ListGradoLingue"
                 addBlank="true" required="true"
                 classNameBase="input"
                 disabled="<%= String.valueOf(!canManage) %>"
                 selectedValue="<%= newCdnGradoLetto %>" />
    </td>
</tr>
<tr valign="top">
  <td class="etichetta">Scritto</td>
    <td class="campo">
    <af:comboBox name="CDNGRADOSCRITTO"
                 title="Grado conoscenza scritto"
                 moduleName="M_ListGradoLingue"
                 addBlank="true" required="false"
                 classNameBase="input"
                 disabled="<%= String.valueOf(!canManage) %>"
                 selectedValue="<%= newCdnGradoScritto %>" />
    </td>
</tr>
<tr valign="top">
  <td class="etichetta">Parlato</td>
    <td class="campo">
    <af:comboBox name="CDNGRADOPARLATO"
                 title="Grado conoscenza parlato"
                 moduleName="M_ListGradoLingue"
                 addBlank="true" required="false"
                 classNameBase="input"
                 disabled="<%= String.valueOf(!canManage) %>"
                 selectedValue="<%= newCdnGradoParlato %>" />
    </td>
</tr>

<tr valign="top">
  <td class="etichetta">Indispensabile</td>
    <td class="campo">
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