<tr>
  <td class="etichetta">Codice</td>
  <td class="campo">
    <af:textBox
      name="CODCORSO"
      title="Codice Corso"
      classNameBase="input"
      value="<%= codCorso %>"
      size="8"
      readonly="true"      
      maxlength="8"/>      
  </td>
</tr>
<tr>
  <td class="etichetta">Corso</td>
  <td class="campo">
    <af:textArea cols="30" 
              rows="4" 
              name="DESCCORSO"
              classNameBase="textArea"
              readonly="<%= fieldReadOnly %>"
              onKeyUp="fieldChanged();PulisciRricercaCorso('corso');"
              inline="onKeyDown=\"SaveCorsoHidden();\" onkeypress=\" if (event.keyCode==13) { event.keyCode=9; this.blur(); } \""
              maxlength="200"
              value="<%= descCorso %>"/>

  <% if ( canModify ) { %>
    <a href="#" onClick="javascript:SelezionaDettaglio_onClick();"><img src="../../img/binocolo.gif" alt="Cerca"></a>
  <%}%> 
  <input type="hidden" name="descCorsoHid" value="<%= descCorso %>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Anno</td>
  <td class="campo">
    <af:textBox
      name="NUMANNO"
      title="Anno"
      classNameBase="input"
      value="<%= Utils.notNull(numAnno) %>"
      size="4"
      type="integer"
      required="true"
      readonly="<%= fieldReadOnly %>"
      onKeyUp="fieldChanged();"
      maxlength="5"/>
      <input type="hidden" name="numAnnoHid" value="<%= Utils.notNull(numAnno) %>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Descrizione</td>
  <td class="campo">
    <af:textBox name="STRDESCRIZIONE" classNameBase="input" title="Descrizione" value="<%= strDescrizione %>" size="50" maxlength="100" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Contenuto</td>
  <td class="campo">
    <af:textBox name="STRCONTENUTO" classNameBase="input" title="Contenuto" value="<%= strContenuto %>" size="50" maxlength="100" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>
</tr>
<tr>
  <td colspan="2"><HR></td>
</tr>





<script language="JavaScript">
<!--
var showButtonImg = new Image();
var hideButtonImg = new Image();
showButtonImg.src="../../img/chiuso.gif";
hideButtonImg.src="../../img/aperto.gif"



function onOff()
{	var div = document.getElementById("dett");
	var idImm = document.getElementById("imm1");
	if (div.style.display=="")
  {	nascondi("dett");
		mostra  ("labelVisulizza");
		nascondi("labelNascondi");
		idImm.src = showButtonImg.src;
	} 
	else
  {	mostra  ("dett");
    nascondi("labelVisulizza");
		mostra  ("labelNascondi");
		idImm.src = hideButtonImg.src
	}
}//onOff()

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



<!-- Gestione Linguetta a scomparsa -->


<tr>
<td  colspan="4">
<br/><br/>


<table cellpadding="1" cellspacing="0" width="100%" border="0">
    <tr>
        <td width="40%" align="right"><b>Ente</b></td>
        <td> <a  href="#aaa" onClick="onOff()" style="CURSOR: hand;"> 
        <img align="absmiddle" id="imm1" alt="mostra/nascondi" src="../../img/chiuso.gif" border="0">
        </td>
        <td width="70%">
            <div id="labelVisulizza" style="display:">(visualizza)</div>
            <div id="labelNascondi" style="display:none">(nascondi)</div>
        </td>
    </tr>
</table>


</td>
</tr>

<tr>
<tr><td colspan="2">
<div id="dett" style="display:none">
<table cellpadding="0" cellspacing="0" border="0" width="100%">


  <td class="etichetta">Ente</td>
  <td class="campo">
    <af:textBox name="STRENTE" classNameBase="input" title="Ente" value="<%= strEnte %>" size="50" maxlength="100" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Comune</td>
  <td class="campo">
    <af:textBox name="CODCOMENTE"
                value="<%=codComEnte%>" 
                size="4"
                classNameBase="input"
                maxlength="4"
                readonly="<%= fieldReadOnly %>"
                disabled="false"
                onKeyUp="javascript:PulisciRicerca(Frm1.CODCOMENTE, Frm1.CODCOMENTEHid, Frm1.strComEnte, Frm1.strComEnteHid, null, null, 'codice');"/>&nbsp;
      
    <% if ( canModify ) { %>    
      <A HREF="javascript:btFindComuneCAP_onclick(Frm1.CODCOMENTE, Frm1.CODCOMENTEHid, Frm1.CODCOMENTE, Frm1.strComEnte, Frm1.strComEnteHid, Frm1.strComEnte, null, null, 'codice');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
      <INPUT type="hidden" name="CODCOMENTEHid" value="<%=codComEnte%>">
      <af:textBox name="strComEnte"
                  value="<%=strDescComEnte%>"
                  size="30" maxlength="50" 
                  onFocus="if(event.keyCode==13) {event.keyCode=9; this.blur(); }"
                  classNameBase="input"
                  disabled="<%= fieldReadOnly %>"
                  onKeyUp="javascript:PulisciRicerca(Frm1.CODCOMENTE, Frm1.CODCOMENTEHid, Frm1.strComEnte, Frm1.strComEnteHid, null, null,  'descrizione');"/>&nbsp;<A HREF="javascript:btFindComuneCAP_onclick(Frm1.CODCOMENTE,Frm1.CODCOMENTEHid, Frm1.CODCOMENTE, Frm1.strComEnte, Frm1.strComEnteHid, Frm1.strComEnte, null, null, 'descrizione');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
    <%}else{%>
      <af:textBox name="strComEnte" value="<%=strDescComEnte%>" size="30" maxlength="50"  classNameBase="input"
        readonly="<%= fieldReadOnly %>"/>          
    <%}%>
    <INPUT type="hidden" name="strComEnteHid" value="" >&nbsp;
  </td>
</tr>
<tr>
  <td class="etichetta">Località</td>
  <td class="campo">
    <af:textBox name="STRLOCALITAENTE" classNameBase="input" title="Località Ente" value="<%= strLocalitaEnte %>" size="50" maxlength="50" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Indirizzo</td>
  <td class="campo">
    <af:textBox name="STRINDIRIZZOENTE" classNameBase="input" title="Indirizzo Ente" value="<%= strIndirizzoEnte %>" size="50" maxlength="60" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>
</tr>
</table></div>
</td></tr>

<!-- Fine gestione linguetta a scoparsa -->






<tr>
  <td colspan="2"><HR></td>
</tr>
<tr>
  <td class="etichetta">Mesi</td>
  <td class="campo">
    <af:textBox name="NUMMESI"  classNameBase="input" title="Mesi" value="<%= Utils.notNull(numMesi) %>" type="integer" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Ore</td>
  <td class="campo">
    <af:textBox name="NUMORE" classNameBase="input" title="Ore" value="<%= Utils.notNull(numOre) %>" size="4" type="integer" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  di cui effettive
    <af:textBox name="NUMORESPESE" classNameBase="input" title="Ore Spese" value="<%= Utils.notNull(numOreSpese) %>" size="4" type="integer" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>  
</tr>
<tr>
  <td class="etichetta">Completato</td>
  <td class="campo">
    <af:comboBox 
      title="Conoscenza certificata" 
      name="FLGCOMPLETATO"
      classNameBase="input"
      disabled="<%= fieldReadOnly %>"
      onChange="FlgCompletato_fieldChanged()">
  
      <option value=""  <% if ( "".equals(flgCompletato) )  { %>SELECTED="true"<% } %> ></option>
      <option value="S" <% if ( "S".equals(flgCompletato) ) { %>SELECTED="true"<% } %> >Si</option>
      <option value="N" <% if ( "N".equals(flgCompletato) ) { %>SELECTED="true"<% } %> >No</option>
    </af:comboBox>
  </td>
</tr>
<tr id="mot_ces" name="mot_ces" style="display:none">
  <td class="etichetta">Motivo cessazione</td>
  <td class="campo">
    <af:textBox name="STRMOTCESSAZIONE"classNameBase="input" title="Ore Spese" value="<%= strMotCessazione %>" size="50" maxlength="100" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Tipo Certificazione</td>
  <td class="campo">
    <af:comboBox name="CODTIPOCERTIFICATO"
                 title="Tipo Certificazione"
                 classNameBase="input"
                 moduleName="M_ListForProTipoCorso"
                 disabled="<%= fieldReadOnly %>"
                 onChange="fieldChanged()"
                 addBlank="true" blankValue="" selectedValue="<%=codTipoCertificato.toString()%>"/>
                 
  </td>
</tr>
<tr>
  <td class="etichetta">Ambito disciplinare</td>
  <td class="campo">
    <af:comboBox name="CDNAMBITODISCIPLINARE"
                 title="Codice ambito disciplinare"
                 classNameBase="input"
                 moduleName="M_ListForProAmbDiscip"
                 disabled="<%= fieldReadOnly %>"
                 onChange="fieldChanged()"
                 addBlank="true" blankValue="" selectedValue="<%= strAmbitoDisciplinare %>"/>
  </td>
</tr>

<tr>
  <td colspan="2"><HR></td>
</tr>













<!-- Gestione Linguetta a scomparsa -->

<script language="JavaScript">
<!--



function onOff1()
{	var div = document.getElementById("dett1");
	var idImm = document.getElementById("imm2");
	if (div.style.display=="")
  {	nascondi("dett1");
		mostra  ("labelVisulizza1");
		nascondi("labelNascondi1");
		idImm.src = showButtonImg.src;
	} 
	else
  {	mostra  ("dett1");
    nascondi("labelVisulizza1");
		mostra  ("labelNascondi1");
		idImm.src = hideButtonImg.src
	}
}//onOff()



//-->
</script>




<tr>
 <td  colspan="4">
 <br/><br/>
      <table cellpadding="1" cellspacing="0" width="100%" border="0">
     <tr>
        <td width="40%" align="right"><b>Stage</b></td>
    <td> <a  href="#aaa" onClick="onOff1()" style="CURSOR: hand;"> 
                 <img align="absmiddle" id="imm2" alt="mostra1/nascondi1" src="../../img/chiuso.gif" border="0">
     
         
    </td>
    
         <td width="70%">
             <div id="labelVisulizza1" style="display:">(visualizza)</div>
             <div id="labelNascondi1" style="display:none">(nascondi)</div>
      </td>
         
     </tr>
      </table>
</a> 
 
  </a>&nbsp;
 
 </td>
</tr>

<tr>
<tr><td colspan="2">
<div id="dett1" style="display:none">
<table cellpadding="0" cellspacing="0" border="0" width="100%">

<tr>
  <td class="etichetta">Corso con Stage</td>
  <td class="campo">
    <af:comboBox 
      title="Conoscenza certificata" 
      name="FLGSTAGE"
      classNameBase="input"
      disabled="<%= fieldReadOnly %>"
      onChange="CorsoConStage_onClick()">
      
      <option value=""  <% if ( "".equals(flgStage) )  { %>SELECTED="true"<% } %> ></option>
      <option value="S" <% if ( "S".equals(flgStage) ) { %>SELECTED="true"<% } %> >Si</option>
      <option value="N" <% if ( "N".equals(flgStage) ) { %>SELECTED="true"<% } %> >No</option>
    </af:comboBox>
  di ore
    <af:textBox name="NUMORESTAGE" classNameBase="input" title="Numero ore di stage" value="<%= Utils.notNull(numOreStage) %>" size="4" maxlength="100" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>
</tr>


</table></div>
</td></tr>

















<!-- Fine gestione linguetta a scoparsa -->













<tr id="str_azienda" name="str_azienda" style="display:none">
  <td class="etichetta">Azienda</td>
  <td class="campo">
    <af:textBox name="STRAZIENDA" title="Ragione Sociale dell'Azienda di Stage" value="<%= strAzienda %>" size="50" maxlength="100" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>
</tr>
<tr id="str_com_azienda" name="str_com_azienda" style="display:none">
  <td class="etichetta">Comune</td>
  <td class="campo">
    <af:textBox name="CODCOMAZIENDA" value="<%=codComAzienda%>" 
      size="4" maxlength="4" disabled="false"
      onKeyUp="javascript:PulisciRicerca(Frm1.CODCOMAZIENDA, Frm1.CODCOMAZIENDAHid, Frm1.strComAzienda, Frm1.strComAziendaHid, null, null,  'codice');"/>&nbsp;
      <A HREF="javascript:btFindComuneCAP_onclick(Frm1.CODCOMAZIENDA, Frm1.CODCOMAZIENDAHid, Frm1.CODCOMAZIENDA, Frm1.strComAzienda, Frm1.strComAziendaHid, Frm1.strComAzienda, null, null, 'codice');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
    <INPUT type="hidden" id="CODCOMAZIENDAHid" name="CODCOMAZIENDAHid" value="<%=codComAzienda%>">
    <% if ( canModify ) { %>    
      <af:textBox name="strComAzienda" value="<%=strDescComAzienda%>" size="30" maxlength="50" 
        onFocus="if(event.keyCode==13) {event.keyCode=9; this.blur(); }" onKeyUp="javascript:PulisciRicerca(Frm1.CODCOMAZIENDA, Frm1.CODCOMAZIENDAHid, Frm1.strComAzienda, Frm1.strComAziendaHid, null, null, 'descrizione');"/>
        &nbsp;<A HREF="javascript:btFindComuneCAP_onclick(Frm1.CODCOMAZIENDA, Frm1.CODCOMAZIENDAHid, Frm1.CODCOMAZIENDA, Frm1.strComAzienda, Frm1.strComAziendaHid, Frm1.strComAzienda, null, null, 'descrizione');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
    <%}else{%>
      <af:textBox name="strComAzienda" value="<%=strDescComAzienda%>" size="30" maxlength="50" 
        readonly="<%= fieldReadOnly %>"/>    
    <%}%>
    <INPUT type="hidden" id="strComAziendaHid" name="strComAziendaHid" value="">&nbsp;
  </td>
</tr>
<tr id="str_localita" name="str_localita" style="display:none">
  <td class="etichetta">Località</td>
  <td class="campo">
    <af:textBox name="STRLOCALITAAZIENDA" title="Località dell'Azienda in Stage" value="<%= strLocalitaAzienda %>" size="50" maxlength="50" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>
</tr>
<tr id="str_indirizzo" name="str_indirizzo" style="display:none">
  <td class="etichetta">Indirizzo</td>
  <td class="campo">
    <af:textBox name="STRINDIRIZZOAZIENDA" title="Indirizzo dell'Azienda in Stage" value="<%= strIndirizzoAzienda %>" size="50" maxlength="60" readonly="<%= fieldReadOnly %>" onKeyUp="fieldChanged()"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Note</td>
  <td class="campo">
     <af:textArea cols="30" 
              rows="4" 
              name="STRNOTACORSO"
              classNameBase="textarea"
              readonly="<%= fieldReadOnly %>"
              onKeyUp="fieldChanged()"
              maxlength="2000" value="<%= strNotaCorso %>"/>
  </td>
</tr>