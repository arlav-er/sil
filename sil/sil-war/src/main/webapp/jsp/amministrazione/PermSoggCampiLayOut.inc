
<script type="text/JavaScript">
function mostra(id)
{
	var div = document.getElementById(id);
	div.style.display="";
}

function nascondi(id)
{
	var div = document.getElementById(id);
	div.style.display="none";
}

function azzera(id)
{
	var field = document.getElementById(id);
	field.value = "";
}

//by Davide                                                                    
function showIfsetVar() { 
  var index = document.form1.codStatus.options.selectedIndex;                 
  var opzioneSel = document.form1.codStatus.options[index];                   
                                                                               
  if(opzioneSel.value != "" && opzioneSel.value != "2" && opzioneSel.value != "5")
  {
  	mostra("divScad");
  }
  else if(opzioneSel.value == "" || opzioneSel.value == "2" || opzioneSel.value == "5")                   
  {
  	nascondi("divScad");
  	azzera("dataScadenza");
  }
}
</script>

<table class="main">
  <tr>
	<td class="etichetta">Documento</td>
    <td class="campo">
    	<table style="border-collapse:collapse">
		<tr valign="left">
			<td class="campo">
      			<af:comboBox title="Documento" name="codStatus" classNameBase="input" moduleName="M_GETCODSTATUS" selectedValue="<%=codStatus%>" 
	     		required="true" addBlank="true" onChange="javascript:fieldChanged();cambiaTitoloScadenza();visualizzaData(this);" disabled="<%=String.valueOf(readOnlyStr)%>"/>  
      		</td>
				<td>&nbsp;</td>
     		 <td class="etichetta">Numero documento</td>
        	 <td class="campo">
				<af:textBox type="text" name="strNumDocumento" value="<%=strNumDocumento%>" size="18" maxlength="15" readonly="<%=String.valueOf(readOnlyStr)%>"/>
	 		</td>
	 	</tr>    
    	</table>
    </td>
  </tr>
<tr><td>&nbsp;</td></tr>
<tr>
    <td colspan=2>
        <table class='sezione2' cellspacing=0 cellpadding=0>
            <tr>
                <td  width=18><img id='IMG0' src='<%=img0%>' onclick='cambia(this, document.getElementById("TBL0"))'></td>
                <td  class='titolo_sezione' id="TITOLO_SCADENZA"><%=(codStatus != null && (codStatus.equalsIgnoreCase("2") || codStatus.equalsIgnoreCase("5")))?"Revoca carta":"Scadenza documento"%></td>
                <td align='right' width='30'></td>
            </tr>
        </table>
    </td>
</tr>        
<tr>
    <td>
    	<div id='divScad'>
        <TABLE id='TBL0' style='width:100%;' align='center'>     
        <script>initSezioni(new Sezione(document.getElementById('TBL0'),document.getElementById('IMG0'),<%=(datScad!=null && datScad.length()>0) %>));</script>
            <tr>   
                <td width="0"></td>
                <td class="etichetta">In data</td>
                <td class="campo"><af:textBox classNameBase="input" type="date" name="dataScadenza" value="<%=datScad%>" validateOnPost="true" 
                    readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/>
                </td>
            </tr>
        </table> 
        </div>
    </td>
    <td>
      <TABLE id="tableDataFine" style="display:<%=display1%>" align="center">
      <tr>
      <td class="etichetta" nowrap>Inf. valida fino al</td>
      <td class="campo"><af:textBox classNameBase="input" type="date" name="dataFine" value="<%=dataFine%>" validateOnPost="true" 
          readonly="true" onKeyUp="fieldChanged();" size="11" maxlength="10"/>
      </td><%-- =String.valueOf(readOnlyStr)--%>
      </tr>
      </table>
      <script>
        if ('<%=codStatus%>' != '2' && '<%=codStatus%>' != '5') {
          document.getElementById("tableDataFine").style.display = "inline";
        }        
      </script>
    </td>
</tr>
<tr>
  <td colspan="2">
      <table border="0" width="100%">
      	  <tr>
	          <td class="etichetta">Motivo della carta/documento</td>
	          <td><af:comboBox name="codMotRilascio" classNameBase="input" moduleName="M_GETMOTIVOSOGG" selectedValue="<%=codMotivoRil%>" 
	                           required="false" addBlank="true" onChange="fieldChanged();" disabled="<%=String.valueOf(readOnlyStr)%>"/></td>
	      </tr>
          <tr>
              <td class="etichetta">Note:<br/></td>
              <td class="campo" colspan="3">
                   <af:textArea classNameBase="textarea" name="strNote" value="<%=Utils.notNull(notePermSogg)%>"
                             cols="60" rows="4" maxlength="1000"
                             readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();"/>
              </td>
          </tr>
      </table>
  </td>
</tr>
<tr><td colspan="2">&nbsp;<br></td></tr>
<!--
<tr>
  <td colspan="2" >
    <div class="sezione2">Informazioni relative alla richiesta del documento</div>
  </td>
</tr>
-->
<tr> 
  <td colspan=2>
      <table class='sezione2' cellspacing=0 cellpadding=0>
          <tr>
              <td  width=18><img id='IMG1' src='<%=img0%>' onclick='cambia(this, document.getElementById("TBL1"))'></td>
              <td  class='titolo_sezione' id="Info_ric">Informazioni relative alla richiesta del documento</td>
              <td align='right' width='30'></td>
          </tr>
      </table>
  </td>
</tr>
<tr>
  <td colspan="2">
    <TABLE id='TBL1' style='width:100%;display:none'>     
    <script>
          if ((document.form1.dataRichiesta != "") || (document.form1.codStatoRichiesta != "") || (document.form1.codMotRilascio != "") ){
            document.getElementById("IMG1").src="../../img/aperto.gif";
            document.getElementById("TBL1").style.display="inline";
          } else {
              document.getElementById("IMG1").src="../../img/chiuso.gif";
              document.getElementById("TBL1").style.display="none";
            }
    </script>
      <tr>
        <td class="etichetta"><br/>Data richiesta</td>
        <td><br/>
          <af:textBox classNameBase="input" type="date" name="dataRichiesta" value="<%=Utils.notNull(datRichiesta)%>" validateOnPost="true" 
                      required="false" readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/>
        </td>
      </tr>
      <tr>
          <td class="etichetta">Stato richiesta</td>
          <td><af:comboBox name="codStatoRichiesta" classNameBase="input" moduleName="M_GETSTATORIC" selectedValue="<%=codStatoRic%>" 
                           required="false" addBlank="true" onChange="fieldChanged();" disabled="<%=String.valueOf(readOnlyStr)%>"/></td>
      </tr>
    </table>
  </td>
</tr>
<tr><td colspan="2">&nbsp;<br></td></tr>
</table>