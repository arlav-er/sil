<table class="sapTabella">
  <tr>
    <td class="sapTitoloSezione" colspan="2">
      <input id="btnAltroSAP" type="image" src="../../img/aperto.gif" onclick="toggle('btnAltroSAP', 'tabAltroSAP');toggle('btnAltroSIL', 'tabAltroSIL');return false" />
      <font size="2">Altre Informazioni</font>
    </td>
  </tr>
</table>

<table class="sapTabella" id="tabAltroSAP">

  <tr>
    <td class="etichetta, grassetto, indenta">Note</td>
      <td id="lbl.frmAltro.Note" align="left" class="inputView"><%=Utils.notNull(sapPortaleLav.getStrAnnotazioniColloquio())%></td>
      <td id="edt.frmAltro.Note" style="display: none" align="left" class="inputView">
        <af:textArea cols="50" rows="3" classNameBase="textarea" name="frmAltro_strNote" required="false" value="<%=sapPortaleLav.getStrAnnotazioniColloquio()%>"/>
      </td>
  </tr>
    <tr>
      <td class="etichetta">&nbsp;</td>
      <td class="inputView">&nbsp;</td>
    </tr>
  
</table>

