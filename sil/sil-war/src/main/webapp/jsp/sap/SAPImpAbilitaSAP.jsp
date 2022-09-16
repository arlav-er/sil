<table class="sapTabella">
  <tr>
    <td class="sapTitoloSezione" colspan="2">
      <input id="btnAbilitaSAP" type="image" src="../../img/aperto.gif" onclick="toggle('btnAbilitaSAP', 'tabAbilitaSAP');toggle('btnAbilitaSIL', 'tabAbilitaSIL');return false" />
      <font size="2">Abilitazioni</font>
      <input type="hidden" id="frmAbilita" value="Abilitazioni"/>
    </td>
  </tr>
</table>

<font class="indenta" id="errAbilita" color="red"></font>

<table class="sapTabella" id="tabAbilitaSAP">

  <%if (sapPortaleLav.getSapPatenteList() != null || sapPortaleLav.getSapPatentinoList() != null || sapPortaleLav.getSapAlboList() != null) {
		if (sapPortaleLav.getSapPatenteList() != null) {%>
	  <tr>
	      <td valign="top" id="eti.frmAbilita.Patenti.codAbilitazioneGenHid" class="etichetta, grassetto, indenta"><input name="frmAbilita_Patenti_chkImporta" type="checkbox">Patenti</td>
	      <td>
	      	<table class="sapTabella" id="tabAbilitaSAPPatenti">
			<%
		      for (int i = 0; i < sapPortaleLav.getSapPatenteList().length; i++) {
		            SapPatenteDTO sapPatente = sapPortaleLav.getSapPatenteList(i);
		    %>	      	
	      	<tr>
		      <td id="lbl.frmAbilita.Patenti.codAbilitazioneGenHid.<%=i%>" align="left" class="inputView">
		      <%=Utils.notNull(sapPatente.getCodPatenteDesc())%>
		      <input type="hidden" name="frmAbilita_Patenti_codAbilitazioneGen" value="on"/>
		      </td>			       
		      <td id="edt.frmAbilita.Patenti.codAbilitazioneGenHid.<%=i%>" style="display: none" align="left" class="inputView" nowrap>
		        <%if (sapPatente.getCodPatente().equals("01")) {%>
		      		<af:comboBox moduleName="M_ListSAP01AbilitaPatenti" addBlank="true" onChange='<%="setCampoHidden(this, 'frmAbilita_Patenti_codAbilitazioneGenHid_" + i + "')"%>' 
		      					name='<%="frmAbilita_Patenti_codAbilitazioneGen_" + i%>'/>&nbsp;*&nbsp;
		      		<input type="hidden" name="<%="frmAbilita_Patenti_codAbilitazioneGenHid_" + i%>" value=""/>
		      	<%} else {%>	
		      		<input type="hidden" name="<%="frmAbilita_Patenti_descrAbilitazioneGen_" + i%>" value="<%=sapPatente.getCodPatenteDesc()%>"/>
					<input type="hidden" name="<%="frmAbilita_Patenti_codAbilitazioneGen_" + i%>" value="<%=sapPatente.getCodPatente()%>"/>
					<input type="hidden" name="<%="frmAbilita_Patenti_codAbilitazioneGenHid_" + i%>" value="<%=sapPatente.getCodPatente()%>"/>
				<%}%>
		      </td>	
   	      	</tr>
		    <%}%>   	      	
	      	</table>
	      </td>	
	    </tr>
	  <%
		}
	   	if (sapPortaleLav.getSapPatentinoList() != null) {%>   
	    <tr>
	      <td valign="top" class="etichetta, grassetto, indenta"><input name="frmAbilita_Patentini_chkImporta" type="checkbox">Patentini</td>
	      <td>
	      	<table class="sapTabella" id="tabAbilitaSAPPatentini">
	      	<%
				for (int i = 0; i < sapPortaleLav.getSapPatentinoList().length; i++) {
		            SapPatentinoDTO sapPatentino = sapPortaleLav.getSapPatentinoList(i);
		    %>
	      	<tr>	      
	      	<td id="lbl.frmAbilita.Patentini.codAbilitazioneGen.<%=i%>" align="left" class="inputView">
	      	<%=Utils.notNull(sapPatentino.getCodPatentinoDesc())%>
	      	<input type="hidden" name="frmAbilita_Patentini_codAbilitazioneGen" value="on"/>
	      	</td>
          	<td id="edt.frmAbilita.Patentini.codAbilitazioneGen.<%=i%>" style="display: none" align="left" class="inputView">
          		<input type="hidden" name="<%="frmAbilita_Patentini_descrAbilitazioneGen_" + i%>" value="<%=sapPatentino.getCodPatentinoDesc()%>"/>
        		<input type="hidden" name="<%="frmAbilita_Patentini_codAbilitazioneGen_" + i%>" value="<%=sapPatentino.getCodPatentino()%>"/>
          	</td>  	           	
	      	</tr>
		    <%}%>	      	
	      	</table>
	      </td>
	    </tr>
	  <%
	   	}
	   	if (sapPortaleLav.getSapAlboList() != null) {%>    
	    <tr>
	      <td valign="top" class="etichetta, grassetto, indenta"><input name="frmAbilita_Albi_chkImporta" type="checkbox">Albi</td>
	      <td>
	      	<table class="sapTabella" id="tabAbilitaSAPAlbi">
	      	<%
				for (int i = 0; i < sapPortaleLav.getSapAlboList().length; i++) {
		            SapAlboDTO sapAlbo = sapPortaleLav.getSapAlboList(i);
		    %>
	      	<tr>	      
	      	<td id="lbl.frmAbilita.Albi.codAbilitazioneGen.<%=i%>" align="left" class="inputView">
	      	<%=Utils.notNull(sapAlbo.getCodAlboDesc())%>
	      	<input type="hidden" name="frmAbilita_Albi_codAbilitazioneGen" value="on"/>
			</td>
  			<td id="edt.frmAbilita.Albi.codAbilitazioneGen.<%=i%>" style="display: none" align="left" class="inputView">
  				<input type="hidden" name="<%="frmAbilita_Albi_descrAbilitazioneGen_" + i%>" value="<%=sapAlbo.getCodAlboDesc()%>"/>
				<input type="hidden" name="<%="frmAbilita_Albi_codAbilitazioneGen_" + i%>" value="<%=sapAlbo.getCodAlbo()%>"/>
			</td>  	           	
	      	</tr>
	      	<%}%>
	      	</table>
	      </td>		  
	    </tr>
	  <%
	   	}
	  %>	    
	    <tr>
	      <td class="etichetta">&nbsp;</td>
	      <td class="inputView">&nbsp;</td>
	    </tr>
<%
   }
%>    
</table>

    