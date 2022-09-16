              <%@page import="it.eng.sil.module.cigs.bean.BeanUtils"%>
<%@page import="it.eng.afExt.utils.StringUtils"%>
<tr>
                <td colspan="2">
                  <br/>
                </td>
              </tr>
              <tr valign="top">
                <td class="etichetta">Mansioni da associare</td>
                <td class="campo">
                   <af:comboBox classNameBase="input" title="Mansioni da associare" name="MansioniInserite" onChange="javascript:onOff(MansioniInserite.value);fieldChanged();" required="true"  disabled="<%= String.valueOf(!canModify) %>" >
                  <OPTION value=""></OPTION>
                  <%  String mansioneInserita_cod="";
                      String mansioneInserita_des="";
                      //if (mansioniInseriteRows.size()!=0) out.print("<OPTION value=\""+CostantiPreselezione.Client.MANSIONI_TUTTE_FLAG+"\" >Tutte</OPTION>");
                      for(int i=0; i<mansioniInseriteRows.size(); i++)
                        { row_MansioniInserite = (SourceBean) mansioniInseriteRows.elementAt(i);
                            mansioneInserita_cod= row_MansioniInserite.getAttribute("PRGMANSIONE").toString();
                            mansioneInserita_des= row_MansioniInserite.getAttribute("DESC_MANSIONE").toString();%> 
                             <OPTION value="<%=mansioneInserita_cod%>" <%if (prgMansione.equals(mansioneInserita_cod)) {out.print(" selected=\"true\" ");descMansione=mansioneInserita_des;}%> ><%=mansioneInserita_des%></OPTION>
                      <%}%>
                  <OPTION value="-1">Nuova mansione --></OPTION>                      
                  </af:comboBox>
                  <af:textBox type="hidden" value="0" name="InserimentoMansione"/>
                </td>
              </tr>      
             
            <tr>
                <td colspan="2" align="center">
                      <DIV id="scelta_mansione" style="display:none; border: thin solid white" align="center">
                           <table  width="50%">
                              <%@include file="Mansioni_Elemento.inc"%>
                          </table>
                      </DIV>
                </td>
            </tr>

               <!--tr>
                <td colspan="2"><HR></td>
              </tr-->
              <tr><td colspan="2"><div class="sezione2">&nbsp;</div></td></tr>
              <tr valign="top">
                <td class="etichetta">Tipo di Rapporto</td>
                <td class="campo">
                <%String codice="";
                          String descrizione="";
                          String flgAutonomo="";     
                          String arrayFlgAutonomoString="";
                %>
                 <%
                 SourceBean tipiContrattiRows= (SourceBean) serviceResponse.getAttribute("M_GET_COMBO_DE_TIPO_CONTRATTO.ROWS");
                 Vector rows = tipiContrattiRows.getAttributeAsVector("ROW");
                		ComboPair docComboPair = new ComboPair(rows, null,"codcontratto");
                %>
                <!--
                Javascript richiamato su questo pulsante per far dipendere il tipo contratto da questo campo
                toggleVisContratto(document.Frm1.codContratto);comboPair.populate();  -->
                  <af:comboBox classNameBase="input" title="Tipo di Rapporto" name="codContratto" required="true"
                   onChange="javascript:fieldChanged();" disabled="<%= String.valueOf(!canModify) %>"  >
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
                    <script type="text/javascript">
                    <%out.print(arrayFlgAutonomoString);%>
                </script>
                </td>
              </tr>
<tr valign="top">
	<td class="etichetta">Tipo di Esperienza<br/>(per invio a Cliclavoro)</td>
	<td class="campo">
	<af:comboBox  onChange="fieldChanged();" name="codtipocontratto"  
			addBlank="true" title="Tipo di Esperienza" moduleName="M_GET_COMBO_DE_RAPLAV" selectedValue="<%=codtipocontratto%>" >
			
			</af:comboBox>
			</td>
</tr>
<script type="text/javascript">
	                var arrayFiglio = new Array();                
	                <%=docComboPair.makeArrayJSChild()%>
	                var comboPair = new ComboPair(document.Frm1.codContratto, document.Frm1.codtipocontratto, arrayFiglio,false);
                </script>

<table id="vis_ccnl" style="display:none" <%=(serviceRequest.getAttribute("PAGE").equals("CurrEspLavMainPage"))?"align=\"center\"   width=\"100%\"":"class=\"main\""%> >
              
              <!--tr>
                  <td class="etichetta">CCNL</td>
                  <td class="campo" >
                    <af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');" type="text" name="codCCNL" value="" size="10" maxlength="9" validateWithFunction="codCCNLUpperCase" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;
                    <A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
                    <af:textBox type="hidden" name="codCCNLHid" value="" />
                    <af:textBox type="text" classNameBase="input" onKeyUp="fieldChanged();PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'descrizione');" name="strCCNL" value="" size="25" maxlength="50" inline="
                        onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\""/>&nbsp;
                    <A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'descrizione');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
                    <af:textBox type="hidden" name="strCCNLHid" value="" />

                    <A href="javascript:ricercaAvanzataCCNL();">Ricerca avanzata</A>
                  </td>
              </tr-->
              <!--tr>
                <td class="etichetta">Riferimento di legge</td>
                <td class="campo">
                  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strRifLegge" size="50" maxlength="100" value="<%=strRifLegge%>"  readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr-->
            </table>
            <table <%=(serviceRequest.getAttribute("PAGE").equals("CurrEspLavMainPage"))?"align=\"center\"   width=\"100%\"":"class=\"main\""%> >
              <tr>
                <td class="etichetta">Descrizione delle attività svolte<br/>(per invio a Cliclavoro)</td>
                <td class="campo">
                  <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strDesAttivita" maxlength="300" cols="50" rows="5" value="<%=strDesAttivita%>" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
            </table>
            <!--table id="vis_livello" style="display:none" <%=(serviceRequest.getAttribute("PAGE").equals("CurrEspLavMainPage"))?"align=\"center\"   width=\"100%\"":"class=\"main\""%> >
              <tr>
                <td class="etichetta">Livello di inquadramento</td>
                <td class="campo">
                  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strLivello" maxlength="8" value="<%=strLivello%>" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
            </table-->
            <table <%=(serviceRequest.getAttribute("PAGE").equals("CurrEspLavMainPage"))?"align=\"center\"   width=\"100%\"":"class=\"main\""%> >
              <tr valign="top">
                <td class="etichetta">Area</td>
                <td class="campo">
                  <af:comboBox classNameBase="input" onChange="fieldChanged();" name="codArea" moduleName="M_GetArea" addBlank="True" selectedValue="<%=codArea%>"  disabled="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              <!--tr>
                <td colspan="2"><HR></td>
              </tr-->
              <tr><td colspan="2"><div class="sezione2">&nbsp;</div></td></tr>
              <tr>              
                <td class="etichetta">Mese/anno di inizio</td>
                <td class="campo">
                  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Mese di inizio" type="Integer" name="numMeseInizio" size="2" maxlength="2" value="<%=numMeseInizio%>" validateWithFunction="checkMeseInizioFine" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;/&nbsp;
                  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Anno di inizio" type="Integer" name="numAnnoInizio" size="4" required="True" maxlength="4"  value="<%=numAnnoInizio%>" validateWithFunction="checkAnnoInizioFine"  readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
             <tr>
                <td class="etichetta">Mese/anno di fine</td>
                <td class="campo">
                  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Mese di fine" type="Integer" name="numMeseFine" size="2" maxlength="2" value="<%=numMeseFine%>" validateWithFunction="checkMeseInizioFine"  readonly="<%= String.valueOf(!canModify) %>"  />&nbsp;/&nbsp;
                  <af:textBox classNameBase="input" title="Anno di fine" onKeyUp="fieldChanged();" type="Integer" name="numAnnoFine" size="4" maxlength="4" value="<%=numAnnoFine%>" validateWithFunction="checkAnnoInizioFine"  readonly="<%= String.valueOf(!canModify) %>"  />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Durata in mesi</td>
                <td class="campo">
                  <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="Integer" name="numMesi" size="3" value="<%=numMesi%>" validateWithFunction="calcolaMesi" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              
             </TABLE>
              <table id="vis_formprof" style="display:none" <%=(serviceRequest.getAttribute("PAGE").equals("CurrEspLavMainPage"))?"align=\"center\"  width=\"100%\"":"class=\"main\""%> >
              <tr>
                <td class="etichetta">Certificato di formazione professionale</td>
                <td class="campo">
                    <af:comboBox classNameBase="input" onChange="fieldChanged();" name="codTipoCertificato" moduleName="M_GetTipiCertificato" addBlank="True" selectedValue="<%=codTipoCertificato%>" disabled="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              </table>
              <table id="vis_completato" style="display:none" <%=(serviceRequest.getAttribute("PAGE").equals("CurrEspLavMainPage"))?"align=\"center\"   width=\"100%\"":"class=\"main\""%> >
              <tr>
                <td class="etichetta">Completato (stage o tirocini)</td>
                <td class="campo">
                    <af:comboBox classNameBase="input" name="FlgCompletato" onChange="fieldChanged();" disabled="<%= String.valueOf(!canModify) %>">
                        <OPTION value="" ></OPTION>
                        <OPTION value="S" <% if (flgCompletato.equals("S")) out.print(" selected=\"true\" ");%> >Si</OPTION>
                        <OPTION value="N" <% if (flgCompletato.equals("N")) out.print(" selected=\"true\" ");%>>No</OPTION>
                    </af:comboBox>
                </td>
              </tr>
              </table>
              <table <%=(serviceRequest.getAttribute("PAGE").equals("CurrEspLavMainPage"))?"align=\"center\"   width=\"100%\"":"class=\"main\""%> >
              <tr>
                <td class="etichetta">Cessazione</td>
                <td class="campo">
                    <af:comboBox classNameBase="input" name="codMvCessazione" moduleName="M_GetTipiCessazione" addBlank="True" selectedValue="<%=codMvCessazione%>" onChange="toggleVisMotivazioni(document.Frm1.codMvCessazione);fieldChanged();" disabled="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
          </table>
          <table id="motivazioni" style="display:none" <%=(serviceRequest.getAttribute("PAGE").equals("CurrEspLavMainPage"))?"align=\"center\"  width=\"100%\"":"class=\"main\""%>  >  
              <tr>
                <td class="etichetta">Motivazioni</td>
                <td class="campo">
                  <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strMotCessazione" maxlength="100" cols="50" rows="5" value="<%=strMotCessazione%>" readonly="<%= String.valueOf(!canModify) %>"/>
                </td>
              </tr>              
           </table>



<%if(canViewSectionImpresa) {%>
<table width="100%" border="0">
      <tr>
      <!--td colspan="3"><HR/></td-->
      <tr><td colspan="3"><div class="sezione2">&nbsp;</div></td></tr>
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
<%}%>

<table <%=(serviceRequest.getAttribute("PAGE").equals("CurrEspLavMainPage"))?"align=\"center\"   width=\"100%\"":"class=\"main\""%>>






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
            <% if (canModify){ %>
            <a href="javascript:ricercaAziende();"><img src="../../img/binocolo.gif" alt="Cerca"></A>
            <%}%>
            &nbsp;&nbsp;
            </div>
             </td>
         </tr>
          <tr>
                <td class="etichetta">Codice Attività</td>
                <td class="campo">
                  <af:textBox classNameBase="input" name="codAteco" size="6" maxlength="6" value="<%=codAteco%>" readonly="<%= String.valueOf(!canModify) %>" />
                  <af:textBox type="hidden" name="codAtecoHid" value="<%=codAteco%>" />
                  <% if (canModify) { %>
                      <a href="javascript:selectATECO_onClick(document.Frm1.codAteco, document.Frm1.codAtecoHid, document.Frm1.strAteco,  document.Frm1.strTipoAteco);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
                      <A href="javascript:ricercaAvanzataAteco();">
                          Ricerca avanzata
                      </A>
                  <%}%>
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
                <td class="campo"><af:textBox classNameBase="input" maxlength="16" onKeyUp="fieldChanged();" name="strCodFiscaleAzienda" value="<%=strCodFiscaleAzienda%>" readonly="<%= String.valueOf(!canModify) %>" /></td>
              </tr>
              <tr>
                <td class="etichetta">Partita iva</td>
                <td class="campo"><af:textBox classNameBase="input" maxlength="11" onKeyUp="fieldChanged();" name="strPartitaIvaAzienda" value="<%=strPartitaIvaAzienda%>" size="15"  readonly="<%= String.valueOf(!canModify) %>" /></td>
              </tr>
              <tr>
                <td class="etichetta">Ragione sociale</td>
                <td class="campo"><af:textBox classNameBase="input" maxlength="100" onKeyUp="fieldChanged();" name="strRagSocialeAzienda" value="<%=strRagSocialeAzienda%>" size="50" readonly="<%= String.valueOf(!canModify) %>" /></td>
              </tr>
              <tr>
                <td class="etichetta">Indirizzo</td>
                <td class="campo"><af:textBox classNameBase="input" maxlength="60" onKeyUp="fieldChanged();" name="strIndirizzoAzienda" value="<%=strIndirizzoAzienda%>" size="50" readonly="<%= String.valueOf(!canModify) %>" /></td>
              </tr>
              <tr>
                <td class="etichetta">Comune</td>
                <td class="campo"><af:textBox classNameBase="input" maxlength="4" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComAzienda, Frm1.codComAziendaHid, document.Frm1.strComAzienda, document.Frm1.strComAziendaHid, null, null, 'codice');" type="text" name="codComAzienda" value="<%=codComAzienda%>" size="4" validateWithFunction="codComuneUpperCase" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;
                <% if (canModify){ %>
                <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComAzienda,  document.Frm1.strComAzienda, null, 'codice','',null,'inserisciComAziendaNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
                <%}%>
                <af:textBox type="hidden" name="codComAziendaHid" value="<%=codComAzienda%>" />
                <af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComAzienda, document.Frm1.codComAziendaHid, document.Frm1.strComAzienda, document.Frm1.strComAziendaHid, null, null, 'descrizione');" type="text" name="strComAzienda" value="<%=strComAzienda%>" size="30" maxlength="50" inline="
                    onkeypress=\"if(event.keyCode==13) { event.keyCode=9; this.blur(); }\""
                    readonly="<%= String.valueOf(!canModify) %>" />&nbsp;
                <% if (canModify){ %>
                <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComAzienda,  document.Frm1.strComAzienda, null, 'descrizione','',null,'inserisciComAziendaNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
                <%}%>
                <af:textBox type="hidden" name="strComAziendaHid" value="<%=strComAzienda%>" />&nbsp;
                </td>                
              </tr>            
              <tr>
                <td class="etichetta">Natura azienda</td>
                <td class="campo"><af:comboBox classNameBase="input" onChange="fieldChanged();" name="codNatGiuridica" moduleName="M_GETTIPINATGIURIDICA" addBlank="true" selectedValue="<%=codNatGiuridica%>" disabled="<%= String.valueOf(!canModify) %>" /></td>
              </tr>
              <tr>
                <td class="etichetta">Tipologia/natura dei clienti</td>
                <td class="campo"><af:textBox classNameBase="input" maxlength="100" onKeyUp="fieldChanged();" type="text" name="strTipoClienti" value="<%=strTipoClienti%>" readonly="<%= String.valueOf(!canModify) %>" /></td>
              </tr>

</table>
</div>
<tr>
  <td colspan="2"><br></td>
</tr>
