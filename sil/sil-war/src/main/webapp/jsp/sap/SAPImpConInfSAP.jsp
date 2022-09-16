<table class="sapTabella">
  <tr>
    <td class="sapTitoloSezione" colspan="2">
      <input id="btnConInfSAP" type="image" src="../../img/aperto.gif" onclick="toggle('btnConInfSAP', 'tabConInfSAP');toggle('btnConInfSIL', 'tabConInfSIL');return false" />
      <font size="2">Conoscenze Informatiche</font>
      <input type="hidden" id="frmConInf" value="Conoscenze Informatiche"/>
    </td>
  </tr>
</table>

<font class="indenta" id="errConInf" color="red"></font>

<table class="sapTabella" style="table-layout:fixed;" id="tabConInfSAP">

  <%if (sapPortaleLav.getSapConoscenzeInfoList() != null) {
		int numConInfo = sapPortaleLav.getSapConoscenzeInfoList().length;%>
  <input type="hidden" name="numConInfo" value="<%=numConInfo%>"/>		
		<%if (numConInfo > 1) {%>
  <tr>
    <td class="sapImporta" colspan="2">
      <input id="chkConInf" type="checkbox" onClick="selectAllCheck('chkConInf', 'frmConInf')">
      Seleziona/Deseleziona
    </td>
  </tr>
    <%
  		}
		for (int i = 0; i < sapPortaleLav.getSapConoscenzeInfoList().length; i++) {
            SapConoscenzeInfoDTO sapConoscenzeInfo = sapPortaleLav.getSapConoscenzeInfoList(i);
    %>
    <tr>
      <td class="sapTitoloSezione" colspan="2">
        <input name="<%="frmConInf_chkImporta_" + i%>" type="checkbox">
        <%=Utils.notNull(sapConoscenzeInfo.getCodDettaglioConInformaticaDesc())%>
      </td>
      <input type="hidden" name="<%="frmConInf_codDettInfo_" + i%>" value="<%=Utils.notNull(sapConoscenzeInfo.getCodDettaglioConInformatica())%>"/>
      <input type="hidden" name="<%="frmConInf_descrDettInfo_" + i%>" value="<%=Utils.notNull(sapConoscenzeInfo.getCodDettaglioConInformaticaDesc())%>"/>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Tipo</td>
      <td id="lbl.frmConInf.codTipoInfo.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapConoscenzeInfo.getCodTipoConInformaticaDesc())%></td>
      <td id="edt.frmConInf.codTipoInfo.<%=i%>" style="display: none" align="left" class="inputView">
        <input type="hidden" name="<%="frmConInf_codTipoInfo_" + i%>" value="<%=Utils.notNull(sapConoscenzeInfo.getCodTipoConInformatica())%>"/>
      </td>
    </tr>
   <tr>
      <td class="etichetta, grassetto, indenta">Livello Conoscenza</td>
      <td id="lbl.frmConInf.cdnGrado.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapConoscenzeInfo.getCodGradoConInformaticaDesc())%></td>
      <td id="edt.frmConInf.cdnGrado.<%=i%>" style="display: none" align="left" class="inputView">
        <input type="hidden" name="<%="frmConInf_cdnGrado_" + i%>" value="<%=Utils.notNull(sapConoscenzeInfo.getCodGradoConInformatica())%>"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Descrizione</td>
      <td id="lbl.frmConInf.strDescInfo.<%=i%>" align="left" class="inputView" style="word-wrap:break-word;"><%=Utils.notNull(sapConoscenzeInfo.getStrDescrizione())%></td>
      <td id="edt.frmConInf.strDescInfo.<%=i%>" style="display: none" align="left" class="inputView">
        <af:textArea cols="50" rows="3" classNameBase="textarea" name='<%="frmConInf_strDescInfo_" + i%>' required="false" value="<%=Utils.notNull(sapConoscenzeInfo.getStrDescrizione())%>"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta, grassetto">&nbsp;</td>
      <td class="inputView">&nbsp;</td>
    </tr>
    <%
      }
    %>
<%
  }
%>    
</table>

