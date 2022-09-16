<tr>
<td>
<div id="datiAzienda">
  <!--Tabella con i dati dell'azienda-->
  <table border="0" width="96%" cellpadding="0" cellspacing="0">
  <tr>
  <td class="etichetta" width="15%">C.F.</td>
  <td class="campo" width="30%">
  <af:textBox classNameBase="input" name="strCodiceFiscaleAz" title="Codice Fiscale" size="20" value="<%=strCodiceFiscaleAz%>" readonly="true"/>
  </td>
  <td class="etichetta" width="15%">P.Iva</td>
  <td class="campo" width="40%">
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strPartitaIvaAz" title="Partita Iva" size="13" value="<%=strPartitaIvaAz%>" readonly="true" />
  &nbsp;&nbsp;&nbsp;Val. C.F. / P. IVA&nbsp;<af:textBox classNameBase="input" name="FLGDATIOK" title="Validità C.F. / P. IVA" size="2" value="<%=strFlgDatiOk%>" readonly="true" />
  </td>
  </tr>
  <tr valign="top">
  <td class="etichetta" width="15%">Rag. Soc.</td>
  <td class="campo" width="30%">
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strRagioneSocialeAzTrunc" title="Ragione Sociale" size="30" value="<%=strRagioneSocialeAzTrunc%>" readonly="true" required="true"/>
  <input type="hidden" name="strRagioneSocialeAz" value="<%=strRagioneSocialeAz%>"/>
  </td>
  <td class="etichetta" width="15%">Indirizzo</td>
  <td class="campo" width="30%" nowrap>
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strIndirizzoUAzVisualizzato" title="Indirizzo" size="40" value='<%=strIndirizzoUAz + " (" + strComuneUAz + ", " + strCapUAz + ")"%>' readonly="true" required="true"/>
  <input type="hidden" name="strIndirizzoUAz" value="<%=strIndirizzoUAz%>"/>
  </td>
  </tr>
  <tr valign="top">
  <td class="etichetta" width="15%">Natura az.</td>
  <td class="campo" width="30%">
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="natGiuridicaAz" title="Natura Giuridica" size="30" value="<%=natGiuridicaAz%>" readonly="true"/>
  <input type="hidden" name="CODNATGIURIDICAAZ" value="<%=codNatGiuridicaAz%>"/>
  </td>
  <td class="etichetta" width="15%">Tipologia</td>
  <td class="campo" width="30%">
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="DESCRTIPOAZIENDA" title="Tipologia" size="25" value="<%=descrTipoAzienda%>" readonly="true" required="true"/>         
  </td>
  <tr/>
  <tr valign="top">
  <td class="etichetta" width="15%">Attività</td>
  <td class="campo" width="30%">
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="codAtecoStrDesAttivitaAz" title="Attività Azienda" size="30" value='<%=codAtecoUAz + " - " + strDesAtecoUAz%>' readonly="true"/>
  <input type="hidden" name="codAtecoUAz" value="<%=codAtecoUAz%>"/>
  <input type="hidden" name="strDesAtecoUAz" value="<%=strDesAtecoUAz%>"/>
  </td>
  <td class="etichetta" width="15%">CCNL</td>
  <td class="campo" width="30%">
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="codDescrCCNLAz" title="CCNL Azienda" size="40" value='<%=codCCNLAz + " - " + descrCCNLAz%>' readonly="true"/>
  <input type="hidden" name="codCCNLAz" value="<%=codCCNLAz%>"/>
  <input type="hidden" name="descrCCNLAz" value="<%=descrCCNLAz%>"/>
  </td>
  </tr>               
  <tr valign="top">
  <td class="etichetta" width="15%">Telefono</td>
  <td class="campo" width="30%">
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strTelUAz" title="Telefono" size="25" value="<%=strTelUAz%>" readonly="true"/>
  </td>
  <td class="etichetta" width="15%">Fax</td>
  <td class="campo" width="30%">
    <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strFaxUAz" title="Fax" size="25" value='<%=strFaxUAz%>' readonly="true"/>
  </td>
  </tr>               
  <tr valign="top">
  <td class="etichetta" width="15%">Pos. INPS</td>
  <td class="campo" width="30%">
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();unificaPosInps();" name="STRPOSINPS1" title="Posizione Inps1" size="2" maxlength="2" value="<%=posInps1%>" readonly="<%=String.valueOf(!canModify)%>"/> - 
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();unificaPosInps();" name="STRPOSINPS2" title="Posizione Inps2" size="15" maxlength="13" value="<%=posInps2%>" readonly="<%=String.valueOf(!canModify)%>"/>
  <input type="hidden" name="STRPOSINPS" value="<%=posInps%>"/>
  </td>
  <td class="etichetta" width="15%">Pat INAIL</td>
  <td class="campo" width="30%">
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();unificaPatInail();" name="STRPATINAIL1" title="Pat INAIL 1" size="10" maxlength="8" value="<%=patInail1%>" readonly="<%=String.valueOf(!canModify)%>"/> - 
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();unificaPatInail();" name="STRPATINAIL2" title="Pat INAIL 2" size="3" maxlength="2" value="<%=patInail2%>" readonly="<%=String.valueOf(!canModify)%>"/>
  <input type="hidden" name="STRPATINAIL" value="<%=patInail%>"/>                    
  </td>  
  </tr>           
  <tr valign="top">
  <td class="etichetta" nowrap="nowrap">Rif. per pratica<br/>amm.</td>
  <td class="campo" colspan="3">
  	<af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="STRREFERENTE" title="Riferimento per pratica amministrativa" size="100" maxlength="100" value="<%=strReferente%>" readonly="<%=String.valueOf(!canModify)%>"/>                 
  </td>  
  </tr>
<%
	/*Decide se visualizzare il numero di iscrizione all'albo per aziende interinali*/
	String displayInterinali = "none";
	if (codTipoAzienda.equalsIgnoreCase(codInterinale)){ displayInterinali = "inline";}
%>
  <tr valign="top" id="alboInt" style="display: <%=displayInterinali%>;">
  <td class="etichetta">Iscr. Albo Interinale</td>
  <td class="campo">
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="STRNUMALBOINTERINALI" title="Numero di iscrizione all'albo interinale" size="10" value="<%=strNumAlboInterinali%>" readonly="true"/>
  </td>
  <tr/>
<%
	/*Decide se visualizzare il numero di iscrizione al registro committenti*/
	String displayCommittenti = "none";
	if ((strNumRegistroCommitt != null) && (!strNumRegistroCommitt.equals(""))){ displayCommittenti = "inline";}
%>
  <tr id="regCommit" style="display: <%=displayCommittenti%>;">
  <td class="etichetta">Iscr. Reg. Committenti</td>
  <td class="campo">
  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="STRNUMREGISTROCOMMITT" title="Numero di iscrizione al registro committenti" size="10" value="<%=strNumRegistroCommitt%>" readonly="true"/>
  </td>
  <td></td>                    
  </tr>
  <tr>
  <td class="campo" colspan="3" nowrap="nowrap">
  <div id="CheckboxPersonaleInterno" style="display:<%=(interinale ? "inline" : "none")%>">
    Personale interno
    <input id="flagPersonaleInterno" type="checkbox" title="Flag Personale Interno" name="FLGINTERASSPROPRIACHECKBOX" <%=(interinale && assInterna) ? "CHECKED" : ""%> value="" <%=canModify ? "" : "disabled='true'"%> onclick="fieldChanged();valorizzaHid(this);"/>
    <input id="flagPersonaleInternoFIELD" type="hidden" name="FLGINTERASSPROPRIA" value="<%=flgAssPropria%>"/>              
  &nbsp;
  </div>
  <%if(canModify || 
  		((!prgAziendaUtil.equals("") && !prgUnitaUtil.equals("")) || 
 		 (codTipoAss.substring(0,2).equalsIgnoreCase("AF") && 
 		  ("S".equalsIgnoreCase(flgInterAssPropria) || flgInterAssPropria == null || "".equalsIgnoreCase(flgInterAssPropria))
 		 )
 		)
 	){ %>
    <input type="button" class="pulsante" name="luogoLavoro" value="Azienda utilizzatrice / Ente promotore" onClick="javascript:apriAzUtil<%=(valida ? "Validazione" : "")%>();">
    <%--<input type="button" class="pulsante" name="luogoLavoro" value="Azienda utilizzatrice / Ente promotore" onClick="javascript:apriAzUtilValidazione();">--%>
    <%if (inserisci || rettifica){%>
        &nbsp;<a href="#" onClick="javascript:azzeraAziendaUtil(true);"><img src="../../img/del.gif" alt="Scollega azienda utilizzatrice"></a>
    <%}%>
  <%}%>
  </td>
  </tr>
  <tr>
  <td class="etichetta">Sede di lavoro</td>
  <td class="campo" colspan="3">
    <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="STRLUOGODILAVORO" title="Luogo di lavoro" size="90" maxlength="500" value="<%=strLuogoDiLavoro%>" readonly="<%=String.valueOf(!canModify)%>"/>
  </tr>
  </table>
</div>
</td>
</tr>