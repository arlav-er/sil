<table class="sapTabella">
  <tr>
    <td class="sapTitoloSezione" colspan="2">
      <input id="btnForProSAP" type="image" src="../../img/aperto.gif" onclick="toggle('btnForProSAP', 'tabForProSAP');toggle('btnForProSIL', 'tabForProSIL');return false" />
      <font size="2">Formazione Professionale</font>
      <input type="hidden" id="frmForPro" value="Formazione Professionale"/>
    </td>
  </tr>
</table>

<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.engiweb.framework.error.EMFAbstractError" %>
<%@ page import="com.engiweb.framework.error.EMFSAPError" %>

<font class="indenta" id="errForPro" color="red"></font>

<table class="sapTabella" id="tabForProSAP">

  <%if (sapPortaleLav.getSapFormazioneList() != null) {
		int numCorsi = sapPortaleLav.getSapFormazioneList().length;%>
  <input type="hidden" name="numCorsi" value="<%=numCorsi%>"/>		
		<%if (numCorsi > 1) {%>	  
  <tr>
    <td class="sapImporta" colspan="2">
      <input id="chkForPro" type="checkbox" onClick="selectAllCheck('chkForPro', 'frmForPro')">
      Seleziona/Deseleziona 
    </td>
  </tr>

    <%
  		}
		for (int i = 0; i < sapPortaleLav.getSapFormazioneList().length; i++) {
            SapFormazioneDTO sapFormazione = sapPortaleLav.getSapFormazioneList(i);
            String strDescrCorso = Utils.notNull(sapFormazione.getCodCorsoDesc());
            if (strDescrCorso.equals("")) 
              strDescrCorso = sapFormazione.getStrTitoloCorso();
    %>
    <tr>
      <td class="sapTitoloSezione" colspan="2">
        <input name="<%="frmForPro_chkImporta_" + i%>" type="checkbox">
        <%=strDescrCorso%>
      </td>
      <input type="hidden" name="<%="frmForPro_strDescrizione_" + i%>"  value="<%=strDescrCorso%>"/>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Codice corso</td>
      <td id="lbl.frmForPro.codCorso.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapFormazione.getCodCorso())%></td>
      <td id="edt.frmForPro.codCorso.<%=i%>" style="display: none" align="left" class="inputView"><af:textBox type="text" name='<%="frmForPro_strCorso_" + i%>' required="false" value="<%=Utils.notNull(sapFormazione.getCodCorsoDesc())%>" /></td>
      <input type="hidden" name="<%="frmForPro_codCorso_" + i%>"  value="<%=Utils.notNull(sapFormazione.getCodCorso())%>"/>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Anno di conseguimento</td>
      <td id="lbl.frmForPro.numAnno.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapFormazione.getNumAnnoConseguimento())%></td>
      <td id="edt.frmForPro.numAnno.<%=i%>" style="display: none" align="left" class="inputView">
      <af:textBox type="text" name='<%="frmForPro_numAnno_" + i%>' required="false" value="<%=Utils.notNull(sapFormazione.getNumAnnoConseguimento())%>" /></td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Tematiche principali</td>
      <td id="lbl.frmForPro.strContenuto.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapFormazione.getStrTematiche())%></td>
      <td id="edt.frmForPro.strContenuto.<%=i%>" style="display: none" align="left" class="inputView">
        <af:textArea cols="50" rows="3" classNameBase="textarea" name='<%="frmForPro_strContenuto_" + i%>' required="false" value="<%=Utils.notNull(sapFormazione.getStrTematiche())%>"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta, grassetto, indenta">Istituto</td>
      <td id="lbl.frmForPro.strEnte.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapFormazione.getStrNomeIstituto())%></td>
      <td id="edt.frmForPro.strEnte.<%=i%>" style="display: none" align="left" class="inputView">
      <af:textBox type="text" name='<%="frmForPro_strEnte_" + i%>' required="false" value="<%=Utils.notNull(sapFormazione.getStrNomeIstituto())%>" /></td>
    </tr>
    <%
		Boolean compl = sapFormazione.getFlgCompletato();	
    	String completato = "";
		if (compl != null) {
			if (compl.booleanValue()) { 
				completato = "Sì";
			} else {
				completato = "No";
			}
		}
    %>
    <tr>
      <td class="etichetta, grassetto, indenta">Completato</td>
      <td id="lbl.frmForPro.flgCompletato.<%=i%>" align="left" class="inputView"><%=completato%></td>
      <td id="edt.frmForPro.flgCompletato.<%=i%>" style="display: none" align="left" class="inputView">
      	<af:comboBox name='<%="frmForPro_flgCompletato_" + i%>' title="completato" required="false" >
      		<OPTION value="" <%if ("".equals(completato)) out.print("SELECTED=\"true\"");%>></OPTION>
			<OPTION value="S" <%if ("Sì".equals(completato)) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
	        <OPTION value="N" <%if ("No".equals(completato)) out.print("SELECTED=\"true\"");%>>No</OPTION>       
	    </af:comboBox>
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

