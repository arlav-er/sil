<table class="sapTabella">
  <tr>
    <td class="sapTitoloSezione" colspan="2">
      <input id="btnTitStuSAP" type="image" src="../../img/aperto.gif" onClick="toggle('btnTitStuSAP', 'tabTitStuSAP');toggle('btnTitStuSIL', 'tabTitStuSIL');return false" />
      <font size="2">Titoli di Studio</font>
      <input type="hidden" id="frmTitStu" value="Titoli di Studio"/>
    </td>
  </tr>
</table>
  
<font class="indenta" id="errTitStu" color="red"></font>

<table class="sapTabella" id="tabTitStuSAP">
  
  <%if (sapPortaleLav.getSapTitoloStudioList() != null) {
		int numTitoli = sapPortaleLav.getSapTitoloStudioList().length;%>
  <input type="hidden" name="numTitoli" value="<%=numTitoli%>"/>		
  		<%if (numTitoli > 1) {%>
  <tr>
    <td class="sapImporta" colspan="2">
      <input id="chkTitStu" type="checkbox" onClick="selectAllCheck('chkTitStu', 'frmTitStu')">
      Seleziona/Deseleziona
    </td>
  </tr>

  <%
  		}
		for (int i = 0; i < numTitoli; i++) {
        	SapTitoloStudioDTO sapTitoloStudio = sapPortaleLav.getSapTitoloStudioList(i);
        	String strDescrTitolo = InsertSAP.getDescrTitolo(sapTitoloStudio.getCodTitolo());
  %>  
  <tr>
    <td class="sapTitoloSezione" colspan="2">
      <table>
        <tr>
          <td style="width: 5%">
            <input name="<%="frmTitStu_chkImporta_" + i%>" type="checkbox">
          </td>
          <td class="sapTitoloSezione" style="width: 95%">
            <%=strDescrTitolo%>
          </td>
        </tr>
      </table>
    </td>
    <input type="hidden" name="<%="frmTitStu_descrTitolo_" + i%>" title="descrizione parlante titolo" value="<%=strDescrTitolo%>"/>
    <input type="hidden" name="<%="frmTitStu_codTitolo_" + i%>" title="codice titolo" value="<%=sapTitoloStudio.getCodTitolo()%>"/>
  </tr>  
   <tr>
      <td class="etichetta, grassetto, indenta">Anno di conseguimento</td>
      <td id="lbl.frmTitStu.numAnno.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapTitoloStudio.getNumAnno())%></td>
      <td id="edt.frmTitStu.numAnno.<%=i%>" style="display: none" align="left" class="inputView">
        <af:textBox type="text" name='<%="frmTitStu_numAnno_" + i%>' title="anno" required="false" value="<%=Utils.notNull(sapTitoloStudio.getNumAnno())%>" />
      </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Specifica</td>
    <td id="lbl.frmTitStu.strSpecifica.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapTitoloStudio.getStrSpecifica())%></td>
    <td id="edt.frmTitStu.strSpecifica.<%=i%>" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name='<%="frmTitStu_strSpecifica_" + i%>' title="specifica" required="false" value="<%=Utils.notNull(sapTitoloStudio.getStrSpecifica())%>" />
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Principale</td>
    <td id="lbl.frmTitStu.flgPrincipale.<%=i%>" align="left" class="inputView"><%=sapTitoloStudio.isFlgPrincipale() ? "Sì" : "No"%></td>
    <td id="edt.frmTitStu.flgPrincipale.<%=i%>" style="display: none" align="left" class="inputView">
      <af:comboBox name='<%="frmTitStu_flgPrincipale_" + i%>' title="principale" required="false" >
        <OPTION value="S" <%if (sapTitoloStudio.isFlgPrincipale()) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
        <OPTION value="N" <%if (!sapTitoloStudio.isFlgPrincipale()) out.print("SELECTED=\"true\"");%>>No</OPTION>       
      </af:comboBox>
    </td>
  </tr>
  <tr>
    <td id="eti.frmTitStu.codMonoStato.<%=i%>" class="etichetta, grassetto, indenta">Stato completamento</td>
    <td id="lbl.frmTitStu.codMonoStato.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapTitoloStudio.getCodMonoStatoDesc())%></td>
    <td id="edt.frmTitStu.codMonoStato.<%=i%>" style="display: none" align="left" class="inputView" nowrap>
      <af:comboBox moduleName="M_GetStatiTitoliStudio" selectedValue="<%=Utils.notNull(sapTitoloStudio.getCodMonoStato())%>" 
      	addBlank="true" name='<%="frmTitStu_codMonoStato_" + i%>' title="Stato completamento"/>&nbsp;*&nbsp;
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Istituto</td>
    <td id="lbl.frmTitStu.strIstScolastico.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapTitoloStudio.getStrNomeIstituto())%></td>
    <td id="edt.frmTitStu.strIstScolastico.<%=i%>" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name='<%="frmTitStu_strIstScolastico_" + i%>' title="istituto" required="false" value="<%=Utils.notNull(sapTitoloStudio.getStrNomeIstituto())%>" />
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Comune</td>
    <td id="lbl.frmTitStu.strLocalita.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapTitoloStudio.getCodComuneDesc())%></td>
    <td id="edt.frmTitStu.strLocalita.<%=i%>" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name='<%="frmTitStu_strLocalita_" + i%>' title="comune" required="false" value="<%=Utils.notNull(sapTitoloStudio.getCodComuneDesc())%>" />
    </td>
    <input type="hidden" name="<%="frmTitStu_codComune_" + i%>" value="<%=Utils.notNull(sapTitoloStudio.getCodComune())%>"/>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Votazione</td>
    <td id="lbl.frmTitStu.strVoto.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapTitoloStudio.getStrVotazione())%></td>
    <td id="edt.frmTitStu.strVoto.<%=i%>" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name='<%="frmTitStu_strVoto_" + i%>' title="voto" required="false" value="<%=Utils.notNull(sapTitoloStudio.getStrVotazione())%>"/>
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
