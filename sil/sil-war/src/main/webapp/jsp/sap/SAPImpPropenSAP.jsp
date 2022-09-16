<table class="sapTabella">
  <tr>
    <td class="sapTitoloSezione" colspan="2">
      <input id="btnPropenSAP" type="image" src="../../img/aperto.gif" onclick="toggle('btnPropenSAP', 'tabPropenSAP');toggle('btnPropenSIL', 'tabPropenSIL');return false" />
      <font size="2">Propensioni</font>
      <input type="hidden" id="frmPropen" value="Propensioni"/>
    </td>
  </tr>
</table>

<font class="indenta" id="errPropen" color="red"></font>

<table class="sapTabella" id="tabPropenSAP">
  
  <%if (sapPortaleLav.getSapPropensioneList() != null) {
		int numPropen = sapPortaleLav.getSapPropensioneList().length;%>
<input type="hidden" name="numPropen" value="<%=numPropen%>"/>		
		<%if (numPropen > 1) {%>
  <tr>
    <td class="sapImporta" colspan="2">
      <input id="chkPropen" type="checkbox" onClick="selectAllCheck('chkPropen', 'frmPropen')">
      Seleziona/Deseleziona
    </td>
  </tr>
    <%
  		}
		for (int i = 0; i < sapPortaleLav.getSapPropensioneList().length; i++) {
            SapPropensioneDTO sapPropensione = sapPortaleLav.getSapPropensioneList(i);
    %>
    <tr>
      <td class="sapTitoloSezione" colspan="2">
        <input name="<%="frmPropen_chkImporta_" + i%>" type="checkbox">
        <%=Utils.notNull(sapPropensione.getCodMansioneMinDesc())%>
        <input type="hidden" name="<%="frmPropen_codMansione_" + i%>" value="<%=sapPropensione.getCodMansioneMin()%>"/>
        <input type="hidden" name="<%="frmPropen_descrMansione_" + i%>" value="<%=sapPropensione.getCodMansioneMinDesc()%>"/>
      </td>
    </tr>
    <tr style="display:none">
      <td class="etichetta, grassetto, indenta">Descrizione</td>
      <td id="lbl.frmPropen.strDescrizione.<%=i%>" align="left" class="inputView"><%=Utils.notNull(sapPropensione.getStrDescrizione())%></td>
      <td id="edt.frmPropen.strDescrizione.<%=i%>" style="display: none" align="left" class="inputView">
        <af:textArea cols="50" rows="3" classNameBase="textarea" name='<%="frmPropen_strDescrizione_" + i%>' required="false" value="<%=Utils.notNull(sapPropensione.getStrDescrizione())%>"/>
      </td>
    </tr>
  	<%if (sapPropensione.getSapDisponibilitaOrarioList() != null) {%>
    <tr>
      <td id="eti.frmPropen.codOrarioHid.<%=i%>" valign="top" class="etichetta, grassetto, indenta">Orari</td>
	  <td>
      	<table class="sapTabella" id="tabPropensioniSAPOrari">
		<%
		for (int j = 0; j < sapPropensione.getSapDisponibilitaOrarioList().length; j++) {
            SapDisponibilitaOrarioDTO orario = sapPropensione.getSapDisponibilitaOrarioList(j);
	    %>	      	
      	<tr>
	      <td id="lbl.frmPropen.codOrarioHid.<%=i%>.<%=j%>" align="left" class="inputView">
	      <%=Utils.notNull(orario.getCodOrarioDesc())%>
	      <input type="hidden" name="frmPropen_codOrario_<%=i%>" value="on"/>
	      </td>			       
	      <td id="edt.frmPropen.codOrarioHid.<%=i%>.<%=j%>" style="display: none" align="left" class="inputView" nowrap>
	        <%if (Utils.notNull(orario.getCodOrario()).equals("PT")) {%>
	      		<af:comboBox moduleName="M_ListSAPPTPropOrari" addBlank="true" onChange='<%="setCampoHidden(this, 'frmPropen_codOrarioHid_" + i + "_" + j + "')"%>' 
	      					name='<%="frmPropen_codOrario_" + i + "_" + j%>'/>&nbsp;*&nbsp;
	      		<input type="hidden" name="<%="frmPropen_codOrarioHid_" + i + "_" + j%>" value=""/>
	      	<%} else if (Utils.notNull(orario.getCodOrario()).equals("FT")) {%>
      			<af:comboBox moduleName="M_ListSAPFTPropOrari" addBlank="true" onChange='<%="setCampoHidden(this, 'frmPropen_codOrarioHid_" + i + "_" + j + "')"%>' 
							name='<%="frmPropen_codOrario_" + i + "_" + j%>'/>&nbsp;*&nbsp;
				<input type="hidden" name="<%="frmPropen_codOrarioHid_" + i + "_" + j%>" value=""/>
			<%} else {
				//non invio codici orario che non sono traducibili per il SIL%>	
				<input type="hidden" name="<%="frmPropen_codOrario_" + i + "_" + j%>" value=""/>
				<input type="hidden" name="<%="frmPropen_codOrarioHid_" + i + "_" + j%>" value="<%=Utils.notNull(orario.getCodOrario())%>"/>
			<%}%>
			<input type="hidden" name="<%="frmPropen_descrOrario_" + i + "_" + j%>" value="<%=Utils.notNull(orario.getCodOrarioDesc())%>"/>
	      </td>	
  	      	</tr>
	    <%}%>   	      	
      	</table>
      </td>
    </tr>       
    <%}
	  if (sapPropensione.getSapDisponibilitaTurnoList() != null) {%>
    <tr>
      <td id="eti.frmPropen.codTurnoHid.<%=i%>" valign="top" class="etichetta, grassetto, indenta">Turni</td>
	  <td>
      	<table class="sapTabella" id="tabPropensioniSAPTurni">
		<%
		for (int j = 0; j < sapPropensione.getSapDisponibilitaTurnoList().length; j++) {
            SapDisponibilitaTurnoDTO turno = sapPropensione.getSapDisponibilitaTurnoList(j);
	    %>	      	
      	<tr>
	      <td id="lbl.frmPropen.codTurnoHid.<%=i%>.<%=j%>" align="left" class="inputView">
	      <%=Utils.notNull(turno.getCodTurnoDesc())%>
	      <input type="hidden" name="frmPropen_codTurno_<%=i%>" value="on"/>
	      </td>			       
	      <td id="edt.frmPropen.codTurnoHid.<%=i%>.<%=j%>" style="display: none" align="left" class="inputView" nowrap>
	      	<input type="hidden" name="<%="frmPropen_codTurno_" + i + "_" + j%>" value="<%=Utils.notNull(turno.getCodTurno())%>"/>
			<input type="hidden" name="<%="frmPropen_codTurnoHid_" + i + "_" + j%>" value="<%=Utils.notNull(turno.getCodTurno())%>"/>
			<input type="hidden" name="<%="frmPropen_descrTurno_" + i + "_" + j%>" value="<%=Utils.notNull(turno.getCodTurnoDesc())%>"/>
	      </td>	
  	      	</tr>
	    <%}%>   	      	
      	</table>
      </td>
    </tr>	  
    <%}
	  if (sapPropensione.getSapDisponibilitaComuneList() != null) {%>
    <tr>
      <td id="eti.frmPropen.codComHid.<%=i%>" valign="top" class="etichetta, grassetto, indenta">Comuni</td>
	  <td>
      	<table class="sapTabella" id="tabPropensioniSAPComuni">
		<%
		for (int j = 0; j < sapPropensione.getSapDisponibilitaComuneList().length; j++) {
            SapDisponibilitaComuneDTO comune = sapPropensione.getSapDisponibilitaComuneList(j);
	    %>	      	
      	<tr>
	      <td id="lbl.frmPropen.codComHid.<%=i%>.<%=j%>" align="left" class="inputView">
	      <%=Utils.notNull(comune.getCodComuneDesc())%>
	      <input type="hidden" name="frmPropen_codCom_<%=i%>" value="on"/>
	      </td>			       
	      <td id="edt.frmPropen.codComHid.<%=i%>.<%=j%>" style="display: none" align="left" class="inputView" nowrap>
	      	<input type="hidden" name="<%="frmPropen_codCom_" + i + "_" + j%>" value="<%=Utils.notNull(comune.getCodComune())%>"/>
			<input type="hidden" name="<%="frmPropen_codComHid_" + i + "_" + j%>" value="<%=Utils.notNull(comune.getCodComune())%>"/>
			<input type="hidden" name="<%="frmPropen_descrCom_" + i + "_" + j%>" value="<%=Utils.notNull(comune.getCodComuneDesc())%>"/>
	      </td>	
  	      	</tr>
	    <%}%>   	      	
      	</table>
      </td>
    </tr>	  
    <%}
	  if (sapPropensione.getSapDisponibilitaProvinciaList() != null) {%>    
    <tr>
      <td id="eti.frmPropen.codProvinciaHid.<%=i%>" valign="top" class="etichetta, grassetto, indenta">Province</td>
	  <td>
      	<table class="sapTabella" id="tabPropensioniSAPProvince">
		<%
		for (int j = 0; j < sapPropensione.getSapDisponibilitaProvinciaList().length; j++) {
            SapDisponibilitaProvinciaDTO provincia = sapPropensione.getSapDisponibilitaProvinciaList(j);
	    %>	      	
      	<tr>
	      <td id="lbl.frmPropen.codProvinciaHid.<%=i%>.<%=j%>" align="left" class="inputView">
	      <%=Utils.notNull(provincia.getCodProvinciaDesc())%>
	      <input type="hidden" name="frmPropen_codProvincia_<%=i%>" value="on"/>
	      </td>			       
	      <td id="edt.frmPropen.codProvinciaHid.<%=i%>.<%=j%>" style="display: none" align="left" class="inputView" nowrap>
	      	<input type="hidden" name="<%="frmPropen_codProvincia_" + i + "_" + j%>" value="<%=Utils.notNull(provincia.getCodProvincia())%>"/>
			<input type="hidden" name="<%="frmPropen_codProvinciaHid_" + i + "_" + j%>" value="<%=Utils.notNull(provincia.getCodProvincia())%>"/>
			<input type="hidden" name="<%="frmPropen_descrProvincia_" + i + "_" + j%>" value="<%=Utils.notNull(provincia.getCodProvinciaDesc())%>"/>
	      </td>	
  	      	</tr>
	    <%}%>   	      	
      	</table>
      </td>
    </tr>	  
    <%}
	  if (sapPropensione.getSapDisponibilitaRegioneList() != null) {%>  
    <tr>
      <td id="eti.frmPropen.codRegioneHid.<%=i%>" valign="top" class="etichetta, grassetto, indenta">Regioni</td>
	  <td>
      	<table class="sapTabella" id="tabPropensioniSAPRegioni">
		<%
		for (int j = 0; j < sapPropensione.getSapDisponibilitaRegioneList().length; j++) {
            SapDisponibilitaRegioneDTO regione = sapPropensione.getSapDisponibilitaRegioneList(j);
	    %>	      	
      	<tr>
	      <td id="lbl.frmPropen.codRegioneHid.<%=i%>.<%=j%>" align="left" class="inputView">
	      <%=Utils.notNull(regione.getCodRegioneDesc())%>
	      <input type="hidden" name="frmPropen_codRegione_<%=i%>" value="on"/>
	      </td>			       
	      <td id="edt.frmPropen.codRegioneHid.<%=i%>.<%=j%>" style="display: none" align="left" class="inputView" nowrap>
	      	<input type="hidden" name="<%="frmPropen_codRegione_" + i + "_" + j%>" value="<%=Utils.notNull(regione.getCodRegione())%>"/>
			<input type="hidden" name="<%="frmPropen_codRegioneHid_" + i + "_" + j%>" value="<%=Utils.notNull(regione.getCodRegione())%>"/>
			<input type="hidden" name="<%="frmPropen_descrRegione_" + i + "_" + j%>" value="<%=Utils.notNull(regione.getCodRegioneDesc())%>"/>
	      </td>	
  	      	</tr>
	    <%}%>   	      	
      	</table>
      </td>
    </tr>	  
    <%}
	  if (sapPropensione.getSapDisponibilitaStatoList() != null) {%>  
    <tr>
      <td id="eti.frmPropen.codStatoHid.<%=i%>" valign="top" class="etichetta, grassetto, indenta">Stati</td>
	  <td>
      	<table class="sapTabella" id="tabPropensioniSAPStati">
		<%
		for (int j = 0; j < sapPropensione.getSapDisponibilitaStatoList().length; j++) {
            SapDisponibilitaStatoDTO stato = sapPropensione.getSapDisponibilitaStatoList(j);
	    %>	      	
      	<tr>
	      <td id="lbl.frmPropen.codStatoHid.<%=i%>.<%=j%>" align="left" class="inputView">
	      <%=Utils.notNull(stato.getCodComuneDesc())%>
	      <input type="hidden" name="frmPropen_codStato_<%=i%>" value="on"/>
	      </td>			       
	      <td id="edt.frmPropen.codStatoHid.<%=i%>.<%=j%>" style="display: none" align="left" class="inputView" nowrap>
	      	<input type="hidden" name="<%="frmPropen_codStato_" + i + "_" + j%>" value="<%=Utils.notNull(stato.getCodComune())%>"/>
			<input type="hidden" name="<%="frmPropen_codStatoHid_" + i + "_" + j%>" value="<%=Utils.notNull(stato.getCodComune())%>"/>
			<input type="hidden" name="<%="frmPropen_descrStato_" + i + "_" + j%>" value="<%=Utils.notNull(stato.getCodComuneDesc())%>"/>
	      </td>	
  	      	</tr>
	    <%}%>   	      	
      	</table>
      </td>
    </tr>	  
    <%}%>     
    <%
		Boolean flgAutoMunito = sapPropensione.getFlgAutomunito();	
    	String flgDispAuto = "";
		if (flgAutoMunito != null) {
			if (flgAutoMunito.booleanValue()) { 
				flgDispAuto = "Sì";
			} else {
				flgDispAuto = "No";
			}
		}
  	%>
	<tr>
      <td class="etichetta, grassetto, indenta">Automunito</td>
      <td id="lbl.frmPropen.flgDispAuto.<%=i%>" align="left" class="inputView"><%=flgDispAuto%></td>
      <td id="edt.frmPropen.flgDispAuto.<%=i%>" style="display: none" align="left" class="inputView">
        <af:comboBox name='<%="frmPropen_flgDispAuto_" + i%>' title="automunito" required="false">
	   		<OPTION value="" <%if ("".equals(flgDispAuto)) out.print("SELECTED=\"true\"");%>></OPTION>
			<OPTION value="S" <%if ("Sì".equals(flgDispAuto)) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
	      	<OPTION value="N" <%if ("No".equals(flgDispAuto)) out.print("SELECTED=\"true\"");%>>No</OPTION>       
	    </af:comboBox>
	  </td>
    </tr>
    <%
		Boolean flgMotoMunito = sapPropensione.getFlgMotomunito();	
    	String flgDispMoto = "";
		if (flgMotoMunito != null) {
			if (flgMotoMunito.booleanValue()) { 
				flgDispMoto = "Sì";
			} else {
				flgDispMoto = "No";
			}
		}
  	%>    
    <tr>
      <td class="etichetta, grassetto, indenta">Motomunito</td>
      <td id="lbl.frmPropen.flgDispMoto.<%=i%>" align="left" class="inputView"><%=flgDispMoto%></td>
      <td id="edt.frmPropen.flgDispMoto.<%=i%>" style="display: none" align="left" class="inputView">
        <af:comboBox name='<%="frmPropen_flgDispMoto_" + i%>' title="motomunito" required="false">
	   		<OPTION value="" <%if ("".equals(flgDispMoto)) out.print("SELECTED=\"true\"");%>></OPTION>
			<OPTION value="S" <%if ("Sì".equals(flgDispMoto)) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
	      	<OPTION value="N" <%if ("No".equals(flgDispMoto)) out.print("SELECTED=\"true\"");%>>No</OPTION>       
	    </af:comboBox>
	  </td>
    </tr>
    <%
		Boolean flgMezzi = sapPropensione.getFlgMezzipub();	
    	String flgMezziPub = "";
		if (flgMezzi != null) {
			if (flgMezzi.booleanValue()) { 
				flgMezziPub = "Sì";
			} else {
				flgMezziPub = "No";
			}
		}
  	%>    
    <tr>
      <td class="etichetta, grassetto, indenta">Uso mezzi pubblici</td>
      <td id="lbl.frmPropen.flgMezziPub.<%=i%>" align="left" class="inputView"><%=flgMezziPub%></td>
      <td id="edt.frmPropen.flgMezziPub.<%=i%>" style="display: none" align="left" class="inputView">
        <af:comboBox name='<%="frmPropen_flgMezziPub_" + i%>' title="usoMezziPubblici" required="false">
	   		<OPTION value="" <%if ("".equals(flgMezziPub)) out.print("SELECTED=\"true\"");%>></OPTION>
			<OPTION value="S" <%if ("Sì".equals(flgMezziPub)) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
	      	<OPTION value="N" <%if ("No".equals(flgMezziPub)) out.print("SELECTED=\"true\"");%>>No</OPTION>       
	    </af:comboBox>
	  </td>
    </tr>
    <tr>
      <td class="etichetta">&nbsp;</td>
      <td class="inputView">&nbsp;</td>
    </tr>
	<%
		}
	%>
<%
	}
%>	
</table>
