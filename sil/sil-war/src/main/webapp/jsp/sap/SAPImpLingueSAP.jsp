<table class="sapTabella">
  <tr>
    <td class="sapTitoloSezione" colspan="2">
      <input id="btnLingueSAP" type="image" src="../../img/aperto.gif" onclick="toggle('btnLingueSAP', 'tabLingueSAP');toggle('btnLingueSIL', 'tabLingueSIL');return false" />
      <font size="2">Lingue</font>
      <input type="hidden" id="frmLingue" value="Lingue"/>
    </td>
  </tr>
</table>

<font class="indenta" id="errLingue" color="red"></font>

<table class="sapTabella" id="tabLingueSAP">

  <%if (sapPortaleLav.getSapLinguaList() != null) {
		int numLingue = sapPortaleLav.getSapLinguaList().length;%>
  <input type="hidden" name="numLingue" value="<%=numLingue%>"/>		
		<%if (numLingue > 1) {%>
  <tr>
    <td class="sapImporta" colspan="2">
      <input id="chkLingue" type="checkbox" onClick="selectAllCheck('chkLingue', 'frmLingue')">
      Seleziona/Deseleziona 
    </td>
  </tr>

  <%
  		}
		for (int i = 0; i < sapPortaleLav.getSapLinguaList().length; i++) {
        	SapLinguaDTO sapLingua = sapPortaleLav.getSapLinguaList(i);
  %>
  <tr>
    <td class="sapTitoloSezione" colspan="2">
      <input name="<%="frmLingue_chkImporta_" + i%>" type="checkbox">
      <%=sapLingua.getCodLinguaDesc()%>
    </td>
    <input type="hidden" name="<%="frmLingue_descrLingua_" + i%>" value="<%=sapLingua.getCodLinguaDesc()%>"/>
    <input type="hidden" name="<%="frmLingue_codLingua_" + i%>" value="<%=sapLingua.getCodLingua()%>"/>
  </tr>
  <%
		Boolean madreLin = sapLingua.getFlgMadrelingua();	
    	boolean madreLingua = false;
		if (madreLin != null) {
			if (madreLin.booleanValue()) { 
				madreLingua = true;
			} 
		}
  %>
  <tr>
    <td class="etichetta, grassetto, indenta">Madrelingua</td>
    <td id="lbl.frmLingue.codModlingua.<%=i%>" align="left" class="inputView"><%=madreLingua ? "Sì" : "No"%></td>
    <td id="edt.frmLingue.codModlingua.<%=i%>" style="display: none" align="left" class="inputView">
       	<af:comboBox name='<%="frmLingue_codModlingua_" + i%>' title="madrelingua" required="false" >
      		<OPTION value="D" <%if (madreLingua) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
	        <OPTION value="" <%if (!madreLingua) out.print("SELECTED=\"true\"");%>>No</OPTION>       
	    </af:comboBox>
    </td>   
  </tr>
  <tr>
    <td id="eti.frmLingue.cdnGradoLetto.<%=i%>" class="etichetta, grassetto, indenta">Livello Lettura</td>
    <td id="lbl.frmLingue.cdnGradoLetto.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapLingua.getCodGradoLettoDesc())%></td>
    <td id="edt.frmLingue.cdnGradoLetto.<%=i%>" style="display: none" align="left" class="inputView" nowrap>
    <%String codGradoLetto = Utils.notNull(sapLingua.getCodGradoLetto());
      if ("".equals(codGradoLetto) || "A1".equals(codGradoLetto)) {%>
    	<af:comboBox moduleName="M_ListSAPGradoLingue" addBlank="true" name='<%="frmLingue_cdnGradoLetto_" + i%>' title="Livello Lettura"/>&nbsp;*&nbsp;
    <%} else if ("C2".equals(codGradoLetto)) {%>
    	<af:comboBox moduleName="M_ListSAPC2GradoLingue" addBlank="true" name='<%="frmLingue_cdnGradoLetto_" + i%>' title="Livello Lettura"/>&nbsp;*&nbsp;
	<%} else {%>	
		<input type="hidden" name="<%="frmLingue_cdnGradoLetto_" + i%>" value="<%=Utils.notNull(sapLingua.getCodGradoLetto())%>"/>
	<%}%>	
    </td>
  </tr>
  <tr>
    <td id="eti.frmLingue.cdnGradoScritto.<%=i%>" class="etichetta, grassetto, indenta">Livello Scrittura</td>
    <td id="lbl.frmLingue.cdnGradoScritto.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapLingua.getCodGradoScrittoDesc())%></td>
    <td id="edt.frmLingue.cdnGradoScritto.<%=i%>" style="display: none" align="left" class="inputView" nowrap>
     <%String codGradoScritto = Utils.notNull(sapLingua.getCodGradoScritto());
      if ("".equals(codGradoScritto) || "A1".equals(codGradoScritto)) {%>
    	<af:comboBox moduleName="M_ListSAPGradoLingue" addBlank="true" name='<%="frmLingue_cdnGradoScritto_" + i%>' title="Livello Scrittura"/>&nbsp;*&nbsp;
    <%} else if ("C2".equals(codGradoScritto)) {%>
    	<af:comboBox moduleName="M_ListSAPC2GradoLingue" addBlank="true" name='<%="frmLingue_cdnGradoScritto_" + i%>' title="Livello Scrittura"/>&nbsp;*&nbsp;
	<%} else {%>	
		<input type="hidden" name="<%="frmLingue_cdnGradoScritto_" + i%>" value="<%=Utils.notNull(sapLingua.getCodGradoScritto())%>"/>
	<%}%>   
  </tr>
  <tr>
    <td id="eti.frmLingue.cdnGradoParlato.<%=i%>" class="etichetta, grassetto, indenta">Livello espressione orale</td>
    <td id="lbl.frmLingue.cdnGradoParlato.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapLingua.getCodGradoParlatoDesc())%></td>
    <td id="edt.frmLingue.cdnGradoParlato.<%=i%>" style="display: none" align="left" class="inputView" nowrap>
     <%String codGradoParlato = Utils.notNull(sapLingua.getCodGradoParlato());
      if ("".equals(codGradoParlato) || "A1".equals(codGradoParlato)) {%>    
    	<af:comboBox moduleName="M_ListSAPGradoLingue" addBlank="true" name='<%="frmLingue_cdnGradoParlato_" + i%>' title="Livello espressione orale"/>&nbsp;*&nbsp;
    <%} else if ("C2".equals(codGradoParlato)) {%>
    	<af:comboBox moduleName="M_ListSAPC2GradoLingue" addBlank="true" name='<%="frmLingue_cdnGradoParlato_" + i%>' title="Livello espressione orale"/>&nbsp;*&nbsp;
	<%} else {%>	
		<input type="hidden" name="<%="frmLingue_cdnGradoParlato_" + i%>" value="<%=Utils.notNull(sapLingua.getCodGradoParlato())%>"/>
	<%}%>
    </td>
  </tr>
  <%
		Boolean flgCert = sapLingua.getFlgCertificazione();	
    	String flgCertificazione = "";
		if (flgCert != null) {
			if (flgCert.booleanValue()) { 
				flgCertificazione = "Sì";
			} else {
				flgCertificazione = "No";
			}
		}
  %>
  <tr>
    <td class="etichetta, grassetto, indenta">Conoscenza Certificata</td>
    <td id="lbl.frmLingue.flgCertificato.<%=i%>" align="left" class="inputView"><%=flgCertificazione%></td>
    <td id="edt.frmLingue.flgCertificato.<%=i%>" style="display: none" align="left" class="inputView">
      <af:comboBox name='<%="frmLingue_flgCertificato_" + i%>' title="conoscenzaCertificata" required="false" >
     	<OPTION value="" <%if (flgCertificazione.equals("")) out.print("SELECTED=\"true\"");%>></OPTION>
		<OPTION value="S" <%if (flgCertificazione.equals("Sì")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
        <OPTION value="N" <%if (flgCertificazione.equals("No")) out.print("SELECTED=\"true\"");%>>No</OPTION> 
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

