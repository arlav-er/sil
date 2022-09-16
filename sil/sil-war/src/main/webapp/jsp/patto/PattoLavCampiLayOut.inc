<table>
<tr>
    <td class="etichetta">Tipologia &nbsp;</td>
    <td>
      <af:comboBox classNameBase="combobox" addBlank="false" required="true" name="flgPatto297" title="Patto 150" onChange="javasript:showIfsetVar();fieldChanged();" disabled="<%=String.valueOf(!flag_insert)%>" >
          <OPTION value="" ></OPTION>
          <OPTION value="S" <%if (FLGPATTO297 != null && FLGPATTO297.equalsIgnoreCase("S")) out.print("SELECTED");%>>Patto&nbsp;150</OPTION>
          <OPTION value="N" <%if (FLGPATTO297 != null && FLGPATTO297.equalsIgnoreCase("N")) out.print("SELECTED");%>>Accordo&nbsp;generico</OPTION>
      </af:comboBox>
    </td>
</tr>
<tr><td></br></td></tr>
<tr>
  <td colspan="2"><!-- Le tre etichette potrebbero diventare diverse --->
    <div id="titoloIni" style="display:"     class="sezione2">Informazioni valide all'atto della stipula (selezionare la tipologia)</div>
    <div id="titolo297" style="display:none" class="sezione2">Informazioni valide all'atto della stipula</div>
    <div id="titoloAcc" style="display:none" class="sezione2">Informazioni valide all'atto della stipula</div>
  </td>
</tr>      

<tr><td colspan="2">
<div id="infoPatto1" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td class="etichetta"> 
      Data inserimento nell'elenco anagrafico 
    </td>
    <td class="campo">
      <af:textBox classNameBase="input" type="date" validateOnPost="true" name="datInizio" value="<%=it.eng.sil.util.Utils.notNull(datInizio)%>"
                  readonly="true" required="true" onKeyUp="fieldChanged();"
                  size="12" maxlength="10"/>
    </td>            
  </tr>
</table></div>
</td></tr>

<tr><td colspan="2">
<div id="infoPatto2" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td class="etichetta"> 
      <div id="labelVisulizza" style="display">Cpi titolare dei dati&nbsp;</div>
      <div id="labelNascondi" style="display:none">Cpi con cui &egrave; stato stipulato l'accordo</div>
    </td>
    <td>
     <input type="hidden" name="codCPI" value="<%=it.eng.sil.util.Utils.notNull(codCPI)%>" size="10" maxlength="10"  >    
     <af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="text" name="codCPI2" value="<%=it.eng.sil.util.Utils.notNull(descCPI)%>" validateOnPost="true" 
                 required="true" readonly="true" size="32" maxlength="16"/>
    </td>    
  </tr>
</table></div>
</td></tr>

<tr><td colspan="2">
<div id="infoPatto3" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td colspan="1" class="etichetta">
      Data immediata disponibilità 
    </td>
    <td colspan="1">    
      <af:textBox classNameBase="input" type="date" name="datDichDisponibilita" value="<%=it.eng.sil.util.Utils.notNull(DATDICHIARAZIONE)%>"
                  required="true" readonly="true" validateOnPost="true" onKeyUp="fieldChanged();" size="12" maxlength="10"/>
    </td>
  </tr>
</table></div>
</td></tr>

<tr><td colspan="2">
<div id="infoPatto4" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
<tr>
  <td class="etichetta">
    Stato occupazionale
  </td>
  <td  class="campo">
    <input type="hidden" name="prgStatoOccupaz" value="<%=it.eng.sil.util.Utils.notNull(PRGSTATOOCCUPAZ)%>" size="10" maxlength="10"  >            
    <%int size = 11, max = 65;
      if(descStato != null)
      { size = descStato.length()+3;
        if(size > max) 
        { descStato = descStato.substring(0,max)+"...";
          size = max + 4;
        }
      }
    %><af:textBox classNameBase="input" name="prgStatoOccupaz2" value="<%=it.eng.sil.util.Utils.notNull(descStato)%>"
                  readonly="true" required="true" onKeyUp="fieldChanged();" size="<%=size%>" maxlength="100"/>
  </td>
</tr>
</table></div>
</td></tr>

  <tr >
    <td valign="top" colspan="2"><div class="sezione2"/></td>
  </tr>

  <tr>
    <td colspan="1" class="etichetta">
      Data stipula &nbsp;
    </td>
    <td colspan="1">
<%  if(!flag_insert) {%>
    <table cellpadding="0" cellspacing="0" border="0" frame="box" width="100%">
      <tr><td width="25%">
          <af:textBox readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" classNameBase="input" type="date" validateOnPost="true" required="true" name="DATSTIPULA" value="<%=it.eng.sil.util.Utils.notNull(DATSTIPULA)%>" size="12" maxlength="10"/>
          </td>
          <td>Stato&nbsp;patto
          <af:comboBox disabled="<%=String.valueOf(!canModify)%>" onChange="fieldChanged();" classNameBase="combobox" name="CODSTATOATTO"  moduleName="M_STATOATTOPATTO" selectedValue="<%=CODSTATOATTO%>"  addBlank="true" blankValue="" required="true"/>
      </td></tr>
    </table>
<%  } else if(flag_insert) {%>
    <table cellpadding="0" cellspacing="0" border="0" frame="box" width="100%">
      <tr><td>
      <af:textBox readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" classNameBase="input" type="date" validateOnPost="true" required="true" name="DATSTIPULA" value="" size="12" maxlength="10"/>
      </td>
      <td>Stato patto&nbsp&nbsp;
          <af:comboBox disabled="<%=String.valueOf(!canModify)%>" onChange="fieldChanged();" classNameBase="combobox" name="CODSTATOATTO"  moduleName="M_STATOATTOPATTO" addBlank="true" blankValue="" required="true"/>
      </td></tr>
    </table>
    <%}%>
    </td>    
</tr>
  <tr>
    <td class="etichetta">
      Data scadenza conferma &nbsp;
    </td>
    <td class="campo">
      <af:textBox readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" classNameBase="input" type="date" validateOnPost="true" name="DATSCADCONFERMA" value="<%=it.eng.sil.util.Utils.notNull(DATSCADCONFERMA)%>" required="true" size="12" maxlength="10"/>
    </td>
  </tr>
  <tr >
    <td class="etichetta"><br/>Impegno di comuncazione esiti</td><td></td>
  </tr>
  <tr><td></br></td></tr>
  <tr>
    <td class="etichetta">
      Data fine patto &nbsp;
    </td>
    <td class="campo">
      <af:textBox readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" classNameBase="input" type="date" validateOnPost="true"  name="DATFINE" value="<%=it.eng.sil.util.Utils.notNull(DATFINE)%>" size="12" maxlength="10"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">
      Motivo fine patto&nbsp;
    </td>
    <td class="campo">
      <af:comboBox disabled="<%=String.valueOf(!canModify)%>" onChange="fieldChanged();" classNameBase="combobox" name="CODMOTIVOFINEATTO"  moduleName="M_MOTFINEATTOPATTO" selectedValue="<%=CODMOTIVOFINEATTO%>" addBlank="true"/>
    </td>
  </tr>
  <tr >
    <td colspan="2"><br></td>
  </tr>
  <tr>
    <td class="etichetta">
      Note&nbsp;
    </td>
    <td class="campo">
      <af:textArea classNameBase="textarea" name="strNote" value="<%=it.eng.sil.util.Utils.notNull(STRNOTE)%>"
                   cols="60" rows="4" maxlength="100"
                   readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();"/>
    </td>
  </tr>


  <%if (!flag_insert) {%>
  <tr>
  <td>
    <input type="hidden" name="NUMKLOPATTOLAVORATORE2" value="<%=NUMKLOPATTOLAVORATORE%>" size="10" maxlength="10"  >
    <%NUMKLOPATTOLAVORATORE= NUMKLOPATTOLAVORATORE.add(new BigDecimal(1)); %>
    <input type="hidden" name="NUMKLOPATTOLAVORATORE" value="<%=NUMKLOPATTOLAVORATORE%>">
  </td>        
  </tr>
<%}%>
</table>