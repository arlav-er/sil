
   <tr>
     <td class="etichetta">Gruppo</td>
     <td class="campo"><af:textBox type="text" title="Gruppo" name="STRDENOMINAZIONE" value="<%=strDenominazione%>" size="40" maxlength="100" disabled="<%=String.valueOf(readOnlyStr)%>" required="true" validateOnPost="true"/></td>
   </tr>   
   <tr>
    <td class="etichetta">Tipo gruppo </td>
    <td  class="campo"><af:comboBox name="CDNTIPOGRUPPO" moduleName='<%=disabilitaCmbCdnTipoGruppo?"M_ComboTipoGruppo":"M_ComboTipoGruppo_FiltrataPerInsertNuovoGruppo"%>' classNameBase="input" addBlank="true" required="true" title="Tipo gruppo"
                     disabled="<%=String.valueOf( disabilitaCmbCdnTipoGruppo )%>" selectedValue="<%=String.valueOf(cdnTipoGruppo)%>" onChange="javascript:abilitaCodRif();javascript:abilitaEmail();" />
    </td>
   </tr>
   
   <%if ((cdnTipoGruppo!=null) && ( cdnTipoGruppo.toString().equals("1")||listaCdnTipoGruppoPatronatoSogg.contains(cdnTipoGruppo) )){
   	
   %>
	   <tr>
	    <td class="etichetta">Codice di riferimento</td>
	    <td  class="campo"><af:comboBox name="STRCODRIF" moduleName="M_ElencoCPI" classNameBase="input" addBlank="true"
	                     disabled="<%=String.valueOf(readOnlyStr)%>" selectedValue="<%=strCodRif%>"/></td>
	   </tr>
	
	<%} else { %>   
      <af:textBox name="STRCODRIF" type="hidden"   value="<%=strCodRif%>"/>
   <%} %>   
   
   
   <tr>
     <td class="etichetta">Luogo di riferimento</td>
     <td class="campo"><af:textBox type="text" name="STRLUOGORIF" value="<%=strLuogoRif%>" size="50" maxlength="100"  disabled="<%=String.valueOf(readOnlyStr)%>"/></td>
   </tr>
   
    <tr>
        <td class="etichetta">Comune di riferimento</td>
        <td class="campo">
          <af:textBox classNameBase="input" title="Codice comune di riferimento" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComNas, document.Frm1.codComNasHid, document.Frm1.strComNas, document.Frm1.strComNasHid, null, null, 'codice');"
                      type="text" name="codComNas" value="<%=codComRif%>" size="4" maxlength="4"
                      readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
        <%if (canModify) { %>
          <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComNas, document.Frm1.strComNas, null, 'codice','',null,'inserisciComNasNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
        <%}%>
        <af:textBox type="hidden" name="codComNasHid" value="<%=codComRif%>"/>
        <af:textBox type="text" classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComNas, document.Frm1.codComNasHid, document.Frm1.strComNas, document.Frm1.strComNasHid, null, null, 'descrizione');" 
                    name="strComNas"  value="<%=strComRif%>" size="30" maxlength="50"
                    readonly="<%= String.valueOf(!canModify) %>" title="comune di riferimento" />&nbsp;&nbsp;&nbsp;
        <%if (canModify) { %>   
        <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComNas, document.Frm1.strComNas, null, 'descrizione','',null,'inserisciComNasNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>&nbsp;
        <%}%>
        <af:textBox type="hidden"  name="strComNasHid" value="<%=strComRif%>"/>
        </td>
    </tr>
    
	<tr name="emailFields">
     <td class="etichetta" >E-mail per pubblicazioni</td>
     <td class="campo"><af:textBox type="text" name="STREMAILPUBBL" value="<%=email%>" size="30" maxlength="100"  disabled="<%=String.valueOf(readOnlyStr)%>"/></td>
   </tr>
   
   <tr>
     <td class="etichetta">Nota</td>
     <td class="campo"><af:textArea name="STRNOTA" value="<%=strNota%>" rows="5" cols="50" maxlength="100"  disabled="<%=String.valueOf(readOnlyStr)%>"/></td>
   </tr>   

   <tr><td colspan="2">&nbsp;</td></tr>
   <tr><td colspan="2">&nbsp;</td></tr>