<script language="JavaScript">

var showButtonImg = new Image();
var hideButtonImg = new Image();
showButtonImg.src=" ../../img/showInfoBut.gif";
hideButtonImg.src=" ../../img/hideInfoBut.gif"

function isSetVar()
{ var opzioneSel = "";

  /*if(document.form1.codStatoAtto.options.selectedIndex != null)
  { var index = document.form1.codStatoAtto.options.selectedIndex;
    opzioneSel = document.form1.codStatoAtto.options[index];
  }
  else
  {try
   {var index = document.form1.codStatoAtto.options.selectedIndex;
    opzioneSel = document.form1.codStatoAtto.options[index];
   }
   catch(e) { opzioneSel =""; }
  }*/
  opzioneSel = document.form1.codStatoAtto.value;
  alert("opzioneSel"+opzioneSel);
  var div4 = document.getElementById("datiRicorsoInAtto");
  
  if(opzioneSel.value != "" )/*|| document.form1.strNumAtto.value != "")*/
  { onOff();
    div4.style.display="";
  }
  
}

function onOff(){
	var div1 = document.getElementById("dett");
	var div2 = document.getElementById("labelVisulizza");
	var div3 = document.getElementById("labelNascondi");
  
	var idImm = document.getElementById("imm1");
	if (div1.style.display==""){
		div1.style.display="none";
		div2.style.display="";
		div3.style.display="none";
		idImm.src = showButtonImg.src;
	} 
	else { 
		div1.style.display="";
		div2.style.display="none";
		div3.style.display="";
		idImm.src = hideButtonImg.src
	}
}


</script>

<table class="main">
<tr>
  <td class="etichetta">Data inizio</td>    
  <td colspan="3"> 
    <table align="left" border="0" width="100%">
      <tr><td width="25%">
            <af:textBox classNameBase="input" type="date" name="datInizio" value="<%=dataInizio%>" validateOnPost="true" 
                        required="true" readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
            <td colspan=2></td>
<%--
          <td class="etichetta">Data fine</td>
          <td><af:textBox classNameBase="input" type="date" name="datFine" value="<%=dataFine%>" validateOnPost="true" 
                          readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="11"/></td>
--%>                          
      </tr>
    </table>              
  </td>
</tr>

<tr>
  <td class="etichetta">Stato occupazionale </td>
  <td colspan="3">
  <table width="100%"><tr><td>
     <af:comboBox name="codStatoOcc" moduleName="M_GETDESTATOOCC" selectedValue="<%=codStatoOccLetto%>" addBlank="true"
                  title="Stato occupazionale" required="true" onChange="fieldChanged();" disabled="<%=String.valueOf(readOnlyStr)%>" />
  </td></tr></table>
  </td>
</tr>

<tr>
  <td class="etichetta">Categoria dlg.&nbsp;181 </td>
  <td colspan="3">
    <table border="0" width="100%">
    <tr><td width="30%">
          <af:comboBox name="categoria181" moduleName="M_GET181CAT" selectedValue="<%=cod181%>"
                       addBlank="true" required="false" onChange="fieldChanged();" disabled="<%=String.valueOf(readOnlyStr)%>" />
        </td>
        <td cospan=2></td>
        <%--
        <td class="etichetta">Pensionato</td>
        <td>
          <af:comboBox name="flgPensionato" addBlank="false" onChange="fieldChanged();" disabled="<%=String.valueOf(readOnlyStr)%>">
              <OPTION value=""  <%if (pensionato == null) out.print("SELECTED=\"true\"");%>></OPTION>
              <OPTION value="S" <%if (pensionato != null && pensionato.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
              <OPTION value="N" <%if (pensionato != null && pensionato.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
          </af:comboBox>     
        </td>
        --%>
    </tr>
    </table>              
  </td>
</tr>

<tr>
  <td  class="etichetta">Reddito mensile </td>
  <td colspan="3">
     <table width="100%"><tr><td>
      <af:textBox classNameBase="input" type="text" name="reddito" value="<%=Utils.notNull(redditoStr)%>" validateOnPost="true" 
                  readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/> euro
   </td></tr></table>              
  </td>
</tr>

<tr ><td colspan="4"><br><b>Anzianità di disoccupazione</b><br/><hr width="90%"/></td></tr>

<tr>
  <td class="etichetta" align="top">dal </td>
  <td colspan="3">
    <table border="0" width="100%">
    <tr><td width="25%">
        <af:textBox classNameBase="input" type="date" name="datAnzianitaDisoc" value="<%=dataAnzDisoc%>" validateOnPost="true" 
                    readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
        <td class="etichetta">mesi sosp.</td>
        <td><af:textBox classNameBase="input" type="text" name="numMesiSosp" value="<%=numMesiSospInt%>" validateOnPost="true" 
                    readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();"  size="4" maxlength="3"/></td>
    </tr>
    </table>              
  </td>
</tr>

<tr>
   <td class="etichetta">Mesi anzianità</td>
   <td colspan="3">
     <table border="0" width="100%">
     <tr>
      <td width="10%">
      <af:textBox classNameBase="input" type="text" name="numMesiAnzianita" value="<%=mesiAnzInt%>" validateOnPost="true" 
                  readonly="true" onKeyUp="fieldChanged();" size="4" maxlength="3"/></td>
       <td>
          <%if(mesiAnz != null )
            { if(mesiAnz.compareTo(new BigDecimal(12))>0 || ( mesiAnz.compareTo(new BigDecimal(6)) > 0 && cod181.equalsIgnoreCase("G") ))
              {%><af:textBox classNameBase="input" type="text" name="tipoAnzianita" readonly="true" 
                             value="disoccupato/inoccupato di lunga durata" size="40"/>
            <%}
               if(mesiAnz.compareTo(new BigDecimal(24)) > 0)
                 {%><af:textBox classNameBase="input" type="text" name="tipoAnzianita" readonly="true"
                                value="soggetto alla legge 407/90" size="40"/>
               <%}
            }//if(mesiAnz != null )%>
       </td>
     </tr>
     </table>
  </td>
</tr>
<%--
<tr>
    <td class="etichetta">Indennizzato</td>
    <td colspan="3">
    <table width="100%"><tr><td>
      <af:comboBox name="flgIndennizzato" addBlank="false" onChange="fieldChanged();" disabled="<%=String.valueOf(readOnlyStr)%>">
          <OPTION value=""  <%if (indennizzo == null) out.print("SELECTED=\"true\"");%>></OPTION>
          <OPTION value="S" <%if (indennizzo != null && indennizzo.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
          <OPTION value="N" <%if (indennizzo != null && indennizzo.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
      </af:comboBox>
    </td></tr></table>
    </td>
</tr>
--%>
<tr >
  <td colspan="4">
    <a name="aaa" href="#aaa" onClick="onOff()" style="CURSOR: hand;"><br/><br/>
    <table cellpadding="1" cellspacing="0" width="100%">
     <tr><td width="25%"><b>Revisione / Ricorso</b></td>
         <td><img align="absmiddle" id="imm1" alt="mostra/nascondi" src=" ../../img/showInfoBut.gif" border="0"></td>
         <td width="70%">
             <div id="labelVisulizza" style="display">(visualizza)</div>
             <div id="labelNascondi" style="display:none">(nascondi)</div>
         </td>
     </tr>
    </table>
    </a><hr width="90%"/>
  </td>
</tr>


<tr><td colspan="4">
<div id="dett" style="display:none">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
 <tr>
   <td class="etichetta">Num. Atto</td>
   <td>
    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
      <tr><td width="25%">
          <af:textBox classNameBase="input" type="text" name="strNumAtto" value="<%=Utils.notNull(strNumeroAtto)%>"  
                      readonly="true" onKeyUp="fieldChanged();" size="4"/></td>
          <td class="etichetta">Data Atto</td>
          <td><af:textBox classNameBase="input" type="date" name="datAtto" value="<%=dataAtto%>"  
                  readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
      </tr>
    </table>
   </td>
 </tr>
 <tr>
    <td class="etichetta">Stato Atto</td>
    <td colspan="3">
       <af:comboBoxProva name="codStatoAtto" moduleName="M_GETSTATOATTO" selectedValue="<%=Utils.notNull(codStatoAt)%>"
                    addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>"/>
    </td>
 </tr>
 <tr>
    <td class="etichetta">Data Revisione</td>
    <td colspan="3"><af:textBox classNameBase="input" type="date" name="datRichRevisione" value="<%=dataRichRevisione%>"  
                                readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
 </tr>
 <tr>
    <td class="etichetta">Data Ricorso</td>
    <td colspan="3"><af:textBox classNameBase="input" type="date" name="datRicorsoGiurisdz" value="<%=dataRicGiurisdz%>"  
                  readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
 </tr>
</table></div>
</td></tr>

<tr ><td colspan="4"><hr width="90%"/></td></tr>
<tr><td colspan="4">
  <table bordre="0" width="100%">
  <tr>
  <td class="etichetta">Note<br/></td>
  <td class="campo">
    <af:textArea classNameBase="textarea" name="strNote" value="<%=strNote%>"
                 cols="60" rows="4" maxlength="100"
                 onKeyUp="fieldChanged();" readonly="<%=String.valueOf(readOnlyStr)%>"  />
  </td>
  </tr>
  </table>
</td></tr>
</table>
