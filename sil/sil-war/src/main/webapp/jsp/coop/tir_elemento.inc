             <tr>
                <td colspan="2">
                  <br/>
                </td>
              </tr>
              <tr valign="top">
                <td class="etichetta">Mansioni da associare</td>
                <td class="campo">
                   <af:textBox classNameBase="input" title="Mansioni da associare" name="MansioniInserite" value="<%=descrizioneMansione%>" required="true"  readonly="true" />
                  <af:textBox type="hidden" value="0" name="InserimentoMansione"/>
                </td>
              </tr>      
             
            <tr>
                <td colspan="2" align="center">
                      <DIV id="scelta_mansione" style="display:none" align="center">
                           <table class="lista" width="50%">
                          </table>
                      </DIV>
                </td>
            </tr>

               <tr>
                <td colspan="2"><HR></td>
              </tr>
              <tr valign="top">
                <td class="etichetta">Tipo di rapporto</td>
                <td class="campo">
                <%String codice="";
                          String descrizione="";
                          String flgAutonomo="";     
                          String arrayFlgAutonomoString="";
                %>
                  <af:comboBox classNameBase="input" title="Contratto" name="codContratto" required="true"  disabled="<%= String.valueOf(!canModify) %>"  >
                        <OPTION value="" ></OPTION>
                      <%  
                          for (int i=0; i<contrattiRows.size(); i++){
                            row_Contratti=(SourceBean) contrattiRows.elementAt(i);
                            codice= row_Contratti.getAttribute("CODICE").toString();
                            descrizione= row_Contratti.getAttribute("DESCRIZIONE").toString();
                            flgAutonomo=(row_Contratti.containsAttribute("FLGAUTONOMO"))?row_Contratti.getAttribute("FLGAUTONOMO").toString():""; 
                            arrayFlgAutonomoString+=(flgAutonomo.equals("S"))?"flgAutonomo_array.push('"+codice+"');\n":"";%>
                             <OPTION value="<%=codice%>" <%if (codice.equals(codContratto)) out.print(" selected=\"true\" ");%>><%=descrizione%></OPTION>
                      <%}%>                  
                    </af:comboBox>
                    <SCRIPT type="text/javascript">
                    <%out.print(arrayFlgAutonomoString);%>
                  </SCRIPT>
                </td>
              </tr>

          <table id="vis_ccnl" style="display:none" align="center"  width="100%">
            </table>
            <table align="center"  width="100%">
              <tr>
                <td class="etichetta">Descrizione delle attività svolte</td>
                <td class="campo">
                  <af:textArea classNameBase="textarea" maxlength="300"  name="strDesAttivita" cols="50" rows="5" value="<%=strDesAttivita%>" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
            </table>            
            <table align="center" width="100%">
              <tr valign="top">
                <td class="etichetta">Area</td>
                <td class="campo">
                  <af:comboBox classNameBase="input"  name="codArea" moduleName="M_GetArea" addBlank="True" selectedValue="<%=codArea%>"  disabled="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              <tr>
                <td colspan="2"><HR></td>
              </tr>
              <tr>              
                <td class="etichetta">Mese/anno di inizio</td>
                <td class="campo">
                  <af:textBox classNameBase="input"  title="Mese di inizio" type="Integer" name="numMeseInizio" size="2" maxlength="2" value="<%=numMeseInizio%>"  readonly="<%= String.valueOf(!canModify) %>" />&nbsp;/&nbsp;
                  <af:textBox classNameBase="input"  title="Anno di inizio" type="Integer" name="numAnnoInizio" size="4" required="True" maxlength="4"  value="<%=numAnnoInizio%>"   readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
             <tr>
                <td class="etichetta">Mese/anno di fine</td>
                <td class="campo">
                  <af:textBox classNameBase="input"  title="Mese di fine" type="Integer" name="numMeseFine" size="2" maxlength="2" value="<%=numMeseFine%>"   readonly="<%= String.valueOf(!canModify) %>"  />&nbsp;/&nbsp;
                  <af:textBox classNameBase="input" title="Anno di fine" type="Integer" name="numAnnoFine" size="4" maxlength="4" value="<%=numAnnoFine%>"   readonly="<%= String.valueOf(!canModify) %>"  />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Durata in mesi</td>
                <td class="campo">
                  <af:textBox classNameBase="input"  type="Integer" name="numMesi" size="3" value="<%=numMesi%>"  readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              
             </TABLE>
              <table id="vis_formprof" style="display:none" align="center" width="100%">
              <tr>
                <td class="etichetta">Certificato di formazione professionale</td>
                <td class="campo">
                    <af:comboBox classNameBase="input"  name="codTipoCertificato" moduleName="M_GetTipiCertificato" addBlank="True" selectedValue="<%=codTipoCertificato%>" disabled="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              </table>
              <table id="vis_completato" style="display:none" align="center" width="100%">
              <tr>
                <td class="etichetta">Completato (stage o tirocini)</td>
                <td class="campo">
                    <af:comboBox classNameBase="input" name="FlgCompletato"  disabled="<%= String.valueOf(!canModify) %>">
                        <OPTION value="" ></OPTION>
                        <OPTION value="S" <% if (flgCompletato.equals("S")) out.print(" selected=\"true\" ");%> >Si</OPTION>
                        <OPTION value="N" <% if (flgCompletato.equals("N")) out.print(" selected=\"true\" ");%>>No</OPTION>
                    </af:comboBox>
                </td>
              </tr>
              </table>
              <table >
              <tr>
                <td class="etichetta">Cessazione</td>
                <td class="campo">
                    <af:comboBox classNameBase="input" name="codMvCessazione" moduleName="M_GetTipiCessazione" addBlank="True" selectedValue="<%=codMvCessazione%>" disabled="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
          </table>
          <table id="motivazioni" style="display:none" align="center" width="100%">  
              <tr>
                <td class="etichetta">Motivazioni</td>
                <td class="campo">
                  <af:textArea classNameBase="textarea"  name="strMotCessazione" cols="50" rows="5" value="<%=strMotCessazione%>" readonly="<%= String.valueOf(!canModify) %>"/>
                </td>
              </tr>              
           </table>




<table width="100%" border="0">
      <tr>
      <td colspan="3"><HR/></td>
      </tr>
      <tr>
          <td align="right" width="40%"><b>Impresa</b>
          <td> <a  href="#aaa" onClick="onOff1()" style="CURSOR: hand;"> 
          <img align="absmiddle" id="imm1" alt="mostra/nascondi" src="../../img/chiuso.gif" border="0">
          </td>
          <td width="70%">
          <div id="labelVisulizza" style="display:">(visualizza)</div>
          <div id="labelNascondi" style="display:none">(nascondi)</div> &nbsp; &nbsp;
          </td>
      </tr>
</table>


         <table <%=(serviceRequest.getAttribute("PAGE").equals("CurrEspLavMainPage"))?"align=\"center\" frame=\"LHS\" rules=\"none\" width=\"100%\"":"class=\"main\""%>>






<script language="JavaScript">
<!--
var showButtonImg = new Image();
var hideButtonImg = new Image();
showButtonImg.src="../../img/chiuso.gif";
hideButtonImg.src="../../img/aperto.gif"



function onOff1()
{	var div1 = document.getElementById("dett");
	var idImm = document.getElementById("imm1");
	if (div1.style.display=="")
  {	nascondi("dett");
		mostra  ("labelVisulizza");
		nascondi("labelNascondi");
    nascondi ("ButtonBinocolo")
		idImm.src = showButtonImg.src;
	} 
	else
  {	mostra  ("dett");
    nascondi("labelVisulizza");
		mostra  ("labelNascondi");
    mostra ("ButtonBinocolo")
		idImm.src = hideButtonImg.src
	}
}//onOff1()

function mostra(id)
{ var div = document.getElementById(id);
  div.style.display="";
}

function nascondi(id)
{ var div = document.getElementById(id);
  div.style.display="none";
}

//-->
</script>


<tr><td colspan="4">
<div id="dett" style="display:none">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
          <tr>
         <td class="etichetta">Impresa</td> 
           <td> <div id="ButtonBinocolo" style="display:none">            
            &nbsp;&nbsp;
            </div>
             </td>
         </tr>
          <tr>
                <td class="etichetta">Codice Attività</td>
                <td class="campo">
                  <af:textBox classNameBase="input" name="codAteco" size="6" maxlength="6" value="<%=codAteco%>" readonly="<%= String.valueOf(!canModify) %>" />
                  <af:textBox type="hidden" name="codAtecoHid" value="<%=codAteco%>" />                  
                </td>
              </tr>           
              <tr valign="top">
                <td class="etichetta">Tipo</td>
                <td class="campo">
                  <af:textBox classNameBase="input" name="strTipoAteco" value="<%=tipo_ateco%>" readonly="true" size="48" />
                </td>
              </tr>

              <tr>
                <td class="etichetta">Attività</td>
                <td class="campo">
                     <af:textBox classNameBase="input" name="strAteco" size="48" readonly="true" value="<%=strDesAteco%>" />
                </td>
              </tr>           

              <tr>
                <td class="etichetta">Codice fiscale</td>
                <td class="campo"><af:textBox classNameBase="input"  name="strCodFiscaleAzienda" maxlength="16" value="<%=strCodFiscaleAzienda%>" readonly="<%= String.valueOf(!canModify) %>" /></td>
              </tr>
              <tr>
                <td class="etichetta">Partita iva</td>
                <td class="campo"><af:textBox classNameBase="input"  name="strPartitaIvaAzienda" maxlength="11"  value="<%=strPartitaIvaAzienda%>" size="15" readonly="<%= String.valueOf(!canModify) %>" /></td>
              </tr>
              <tr>
                <td class="etichetta">Ragione sociale</td>
                <td class="campo"><af:textBox classNameBase="input"  name="strRagSocialeAzienda" maxlength="100" value="<%=strRagSocialeAzienda%>" size="50" readonly="<%= String.valueOf(!canModify) %>" /></td>
              </tr>
              <tr>
                <td class="etichetta">Indirizzo</td>
                <td class="campo"><af:textBox classNameBase="input"  name="strIndirizzoAzienda" maxlength="60" value="<%=strIndirizzoAzienda%>" size="50" readonly="<%= String.valueOf(!canModify) %>" /></td>
              </tr>
              <tr>
                <td class="etichetta">Comune</td>
                <td class="campo"><af:textBox classNameBase="input" onKeyUp="PulisciRicerca(document.Frm1.codComAzienda, document.Frm1.codComAziendaHid, document.Frm1.strComAzienda, document.Frm1.strComAziendaHid, null, null, 'codice');" type="text" name="codComAzienda" value="<%=codComAzienda%>" size="4" maxlength="4" validateWithFunction="codComuneUpperCase" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;                
                <af:textBox type="hidden" name="codComAziendaHid" value="<%=codComAzienda%>" />
                <af:textBox classNameBase="input" onKeyUp="PulisciRicerca(document.Frm1.codComAzienda, document.Frm1.codComAziendaHid, document.Frm1.strComAzienda, document.Frm1.strComAziendaHid, null, null, 'descrizione');" type="text" name="strComAzienda" value="<%=strComAzienda%>" size="30" maxlength="50" inline="
                    onkeypress=\"if(event.keyCode==13) { event.keyCode=9; this.blur(); }\""
                    readonly="<%= String.valueOf(!canModify) %>" />&nbsp;                
                <af:textBox type="hidden" name="strComAziendaHid" value="<%=strComAzienda%>" />&nbsp;
                </td>                
              </tr>            
              <tr>
                <td class="etichetta">Natura azienda</td>
                <td class="campo"><af:comboBox classNameBase="input"  name="codNatGiuridica" moduleName="M_GETTIPINATGIURIDICA" addBlank="true" selectedValue="<%=codNatGiuridica%>" disabled="<%= String.valueOf(!canModify) %>" /></td>
              </tr>
              <tr>
                <td class="etichetta">Tipologia/natura dei clienti</td>
                <td class="campo"><af:textBox classNameBase="input"  type="text" name="strTipoClienti" maxlength="100" value="<%=strTipoClienti%>" readonly="<%= String.valueOf(!canModify) %>" /></td>
              </tr>

</table>
</div>
              <tr>
                <td colspan="2"><br></td>
              </Tr>
