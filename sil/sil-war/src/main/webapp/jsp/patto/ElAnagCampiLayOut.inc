<table>
<tr><td class=etichetta>&nbsp;</td><td width=30%>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
<tr><td colspan="4"><br/><div class="sezione">Inf. valide all'atto dell'ins. nell'elenco anagrafico</div></td></tr>
<tr>
    <td class="etichetta2"> Cpi competente&nbsp;</td>
    <td colspan="2"><%String strCpI = null;
          if(codCPI != null && descCPI != null) {
            strCpI = descCPI;
            strCpI += " - " + codCPI;
          }%>
           <af:textBox classNameBase="input" type="text" name="codCPI" title="CPI competente" value="<%=Utils.notNull(strCpI)%>" 
                        validateOnPost="true"  required="true" readonly="true" onKeyUp="fieldChanged();" size="45" maxlength="30"/>
    </td>
    <td></td>
</tr>
<tr><td colspan="4"><div class="sezione"></div></td></tr>
<tr>
    <td class="etichetta2" colspan=2> Data inserimento nell'elenco anagrafico &nbsp;</td>
    <td class="campo">
       <af:textBox classNameBase="input" type="date" name="datInizio" title="Data inserimento nell'elenco anagrafico"
                   value="<%=datInizio%>" required="true" validateOnPost="true"
                   readonly="<%=String.valueOf(rdOnly||!flag_insert)%>" onKeyUp="fieldChanged();" size="12" maxlength="10"/> 
    </td>        
    <td></td>
</tr>

<tr><td colspan=4><br/></td></tr>
<tr>
    <td colspan="4">
        <table class='sezione2' cellspacing=0 cellpadding=0>
            <tr>
                <td  width=18>
                    <img id='IMG1' src='<%=img1%>' onclick='cambia(this, document.getElementById("TBL1"))'></td>
                        <td  class='titolo_sezione'>Uscita dall'elenco anagrafico</td>    				
                <td align='right' width='30'></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td colspan=4 align="center">
        <TABLE id='TBL1' style='width:100%;display:<%=display1%>'>     
        <script>initSezioni(new Sezione(document.getElementById('TBL1'),document.getElementById('IMG1'),<%=hasDataUscita%>));</script>
			<tr>
			    <td class="etichetta2" width="40%">Data uscita dall'elenco anagrafico &nbsp;</td>
			    <td  class="campo"> 
                    <af:textBox classNameBase="input" type="date" name="dtmcan" value="<%=dataUscita%>"
			               readonly="true" size="12" maxlength="10"/></td>
			</tr>
			<tr>
			    <td class="etichetta">Motivo uscita dall'elenco anagrafico &nbsp;</td>
			    <td><af:comboBox name="cdntipocan" moduleName="M_GetDeMotFineAtto" selectedValue="<%=cdntipocan%>" 
			                     addBlank="true" disabled="<%=String.valueOf(rdOnly||isHistory|| flag_insert)%>" onChange="fieldChanged();"/></td>
			</tr>
        </table>
    </td>
</tr>

<tr><td colspan=4><br></td></tr>
<%-- 
if (((String)serviceRequest.getAttribute("PAGE")).equalsIgnoreCase("ElAnagDettaglioPage")) { %>
<tr >
    <td colspan="4">
        <table class='sezione2' cellspacing=0 cellpadding=0>
            <tr>
                <td  width=18>
                    <img id='IMG0' src='<%=img0%>' onclick='cambia(this, document.getElementById("TBL0"))'></td>
                        <td  class='titolo_sezione'>Autorizzazione al trattamento dei dati personali</td>    				
                <td align='right' width='30'></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td colspan=4 align="center">
        <TABLE id='TBL0' style='width:70%;display:<%=display0%>'>	
            <script>initSezioni(new Sezione(document.getElementById('TBL0'),document.getElementById('IMG0'),<%=hasPrivacy%>));</script>
            <tr>
                <td class=etichetta2 width="70%">Autorizzazione al trattamento dei dati personali</td>
                <td class=campo2><af:textBox classNameBase="input" type="text" name="flgAutoriz" value="<%=Utils.notNull(flgAutoriz)%>"
                     readonly="true"  size="1" maxlength="1" onKeyUp="fieldChanged();"/>                
                </td>
            </tr>
            <tr>
                <td class=etichetta2>Data inizio</td>
                <td class=campo2><af:textBox classNameBase="input" type="date" name="datPrivacy" value="<%=Utils.notNull(dataInizioPrivacy)%>"
                                  validateOnPost="true"  readonly="true" onKeyUp="fieldChanged();" size="12" maxlength="10"/>                
                </td>
            </tr>
        </table>
    </td>
</tr>
<%}--%>
<tr><td colspan=4><br></td></tr>

<tr>
    <td class="etichetta">Note&nbsp;</td>
    <td class="campo" colspan=3>
      <af:textArea classNameBase="textarea" name="strNote" value="<%=Utils.notNull(STRNOTE)%>"
                   cols="60" rows="4" maxlength="100"
                   readonly="<%=String.valueOf(rdOnly)%>" onKeyUp="fieldChanged();"/>
    </td>
</tr>
</table>   