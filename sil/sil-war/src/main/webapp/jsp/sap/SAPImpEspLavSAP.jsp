<%@page import="it.eng.sil.util.Utils"%>
<table class="sapTabella">
  <tr>
    <td class="sapTitoloSezione" colspan="2">
      <input id="btnEspLavSAP" type="image" src="../../img/aperto.gif" onclick="toggle('btnEspLavSAP', 'tabEspLavSAP');toggle('btnEspLavSIL', 'tabEspLavSIL');return false" />
      <font size="2">Esperienze di Lavoro</font>
      <input type="hidden" id="frmEspLav" value="Esperienze di Lavoro"/>
    </td>
  </tr>
</table>

<font class="indenta" id="errEspLav" color="red"></font>

<table class="sapTabella" id="tabEspLavSAP">

  <%if (sapPortaleLav.getSapEsperienzaLavList() != null) {
		int numEspLav = sapPortaleLav.getSapEsperienzaLavList().length;%>
  <input type="hidden" name="numEspLav" value="<%=numEspLav%>"/>		
		<%if (numEspLav > 1) {%>
  <tr>
    <td class="sapImporta" colspan="2">
      <input id="chkEspLav" type="checkbox" onClick="selectAllCheck('chkEspLav', 'frmEspLav')">
      Seleziona/Deseleziona
    </td>
  </tr>

  <%
  		}
      	for (int i = 0; i < sapPortaleLav.getSapEsperienzaLavList().length; i++) {
        	SapEsperienzaLavDTO sapEsperienzaLav = sapPortaleLav.getSapEsperienzaLavList(i);
  %>
  <tr>
    <td class="sapTitoloSezione" colspan="2">
      <input name="<%="frmEspLav_chkImporta_" + i%>" type="checkbox">
      <%=sapEsperienzaLav.getCodMansioneMinDesc()%>
      <input type="hidden" name="<%="frmEspLav_codMansione_" + i%>" value="<%=sapEsperienzaLav.getCodMansioneMin()%>"/>
      <input type="hidden" name="<%="frmEspLav_descrMansione_" + i%>" value="<%=sapEsperienzaLav.getCodMansioneMinDesc()%>"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Gruppo professionale</td>
    <td id="lbl.frmEspLav.strGruppo.<%=i%>" align="left" class="inputView"><%=sapEsperienzaLav.getCodMansioneDesc()%></td>
    <td id="edt.frmEspLav.strGruppo.<%=i%>" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name='<%="frmEspLav_strGruppo_" + i%>' value="<%=sapEsperienzaLav.getCodMansione()%>"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Attivit&agrave;/Responsabilit&agrave;</td>
    <td id="lbl.frmEspLav.strDescrAttivita.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapEsperienzaLav.getStrDescrAttivita())%></td>
    <td id="edt.frmEspLav.strDescrAttivita.<%=i%>" style="display: none" align="left" class="inputView">
    	<af:textArea cols="50" rows="3" classNameBase="textarea" name='<%="frmEspLav_strDescrAttivita_" + i%>' value="<%=Utils.notNull(sapEsperienzaLav.getStrDescrAttivita())%>"/>
    </td>
  </tr>
  <tr>
    <td id="eti.frmEspLav.codContratto.<%=i%>" class="etichetta, grassetto, indenta">Tipologia di contratto</td>
    <td id="lbl.frmEspLav.codContratto.<%=i%>" align="left" class="inputView"></td>
    <td id="edt.frmEspLav.codContratto.<%=i%>" style="display: none" align="left" class="inputView" nowrap>
    	<af:comboBox moduleName="M_ListSAPEspTipiContratto" addBlank="true" name='<%="frmEspLav_codContratto_" + i%>'/>&nbsp;*&nbsp;
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Tipo di esperienza<br/>(per invio a ClicLavoro)</td>
    <td id="lbl.frmEspLav.codContrattoLav.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapEsperienzaLav.getCodContrattoDesc())%></td>
    <td id="edt.frmEspLav.codContrattoLav.<%=i%>" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name='<%="frmEspLav_codContrattoLav_" + i%>' value="<%=Utils.notNull(sapEsperienzaLav.getCodContratto())%>"/>
    </td>
  </tr> 
  <tr>
    <td class="etichetta, grassetto, indenta">Datore di lavoro</td>
    <td id="lbl.frmEspLav.strDatoreLavoro.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapEsperienzaLav.getStrDatoreLavoro())%></td>
    <td id="edt.frmEspLav.strDatoreLavoro.<%=i%>" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name='<%="frmEspLav_strDatoreLavoro_" + i%>' value="<%=Utils.notNull(sapEsperienzaLav.getStrDatoreLavoro())%>"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Attivit&agrave; del datore di lavoro</td>
    <td id="lbl.frmEspLav.strAttivitaMin.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapEsperienzaLav.getCodAttivitaMinDesc())%></td>
    <td id="edt.frmEspLav.strAttivitaMin.<%=i%>" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name='<%="frmEspLav_strAttivitaMin_" + i%>' value="<%=Utils.notNull(sapEsperienzaLav.getCodAttivitaMin())%>"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Data inizio Rapporto</td>
    <td id="lbl.frmEspLav.dtmInizio.<%=i%>" align="left" class="inputView"><%=sapEsperienzaLav.getDtInizio() == null ? "" : DateUtils.format(sapEsperienzaLav.getDtInizio().getTime())%></td>
    <td id="edt.frmEspLav.dtmInizio.<%=i%>" style="display: none" align="left" class="inputView">
      <af:textBox type="text" name='<%="frmEspLav_dtmInizio_" + i%>' value="<%=sapEsperienzaLav.getDtInizio() == null ? "" : DateUtils.format(sapEsperienzaLav.getDtInizio().getTime())%>"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Data fine Rapporto</td>
    <td id="lbl.frmEspLav.dtmFine.<%=i%>" align="left" class="inputView"><%=sapEsperienzaLav.getDtFine() == null ? "" : DateUtils.format(sapEsperienzaLav.getDtFine().getTime())%></td>
    <td id="edt.frmEspLav.dtmFine.<%=i%>" style="display: none" style="display: none" align="left" class="inputView">
      <af:textBox type="text" name='<%="frmEspLav_dtmFine_" + i%>' value='<%=sapEsperienzaLav.getDtFine() == null ? "" : DateUtils.format(sapEsperienzaLav.getDtFine().getTime())%>'/>
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

