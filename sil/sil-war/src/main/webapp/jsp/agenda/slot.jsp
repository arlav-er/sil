<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,
                it.eng.afExt.utils.*, java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.security.PageAttribs,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp"
%>

<%@ taglib uri="cal" prefix="cal" %>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>



<%
String streamDescrCpi = "";
SourceBean content = (SourceBean) serviceResponse.getAttribute("MDESCRCPI");
if(content!=null) {
  SourceBean row = (SourceBean) content.getAttribute("ROWS.ROW");
  if(row!=null) { streamDescrCpi = row.getAttribute("STRDESCRIZIONE").toString(); }
}

int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();
String codCpi;
if(cdnTipoGruppo==1) { codCpi =  user.getCodRif(); }
else { codCpi = requestContainer.getAttribute("agenda_codCpi").toString(); }

%>


<%
String giornoDB = "";
String meseDB = "";
String annoDB = "";
// Istanzio giornoDB, meseDb, annoDb per l'inserimento di un nuovo appuntamento
// Data Odierna
Calendar oggi = Calendar.getInstance();
giornoDB = Integer.toString(oggi.get(5));
meseDB = Integer.toString(oggi.get(2)+1);
annoDB = Integer.toString(oggi.get(1));

String nrosDB = "";
String giorno = "";
String mese = "";
String anno = "";
String cod_vista = "";

int mod = 0;

if(serviceRequest.containsAttribute("giorno")) { giorno = serviceRequest.getAttribute("giorno").toString(); }
if(serviceRequest.containsAttribute("mese")) { mese = serviceRequest.getAttribute("mese").toString(); }
if(serviceRequest.containsAttribute("anno")) { anno = serviceRequest.getAttribute("anno").toString(); }
if(serviceRequest.containsAttribute("nrosDB")) { nrosDB = serviceRequest.getAttribute("nrosDB").toString(); }
if(serviceRequest.containsAttribute("giornoDB")) { giornoDB = serviceRequest.getAttribute("giornoDB").toString(); }
if(serviceRequest.containsAttribute("meseDB")) { meseDB = serviceRequest.getAttribute("meseDB").toString(); }
if(serviceRequest.containsAttribute("annoDB")) { annoDB = serviceRequest.getAttribute("annoDB").toString(); }
if(serviceRequest.containsAttribute("cod_vista")) { cod_vista = serviceRequest.getAttribute("cod_vista").toString(); }

if(serviceRequest.containsAttribute("MOD")) { mod = Integer.parseInt(serviceRequest.getAttribute("MOD").toString()); }
else { 
  sessionContainer.delAttribute("slot_FMiei");
  sessionContainer.delAttribute("slot_FOperatore");
  sessionContainer.delAttribute("slot_FServizio");
  sessionContainer.delAttribute("slot_FAula");
}
String MOD = Integer.toString(mod);
String htmlStream = "";
if(mod == 1) {
  htmlStream = ShowApp.listaSlotSettimana(requestContainer, serviceResponse, codCpi);
}
%>

<%
boolean fmiei;
boolean foper;
boolean fserv;
boolean faula;

if(sessionContainer.getAttribute("slot_FMiei")!=null) { 
  if(!sessionContainer.getAttribute("slot_FMiei").toString().equals("")) { fmiei =  true; }
  else { fmiei = false; }
} else { fmiei = false; }
if(sessionContainer.getAttribute("slot_FOperatore")!=null) { 
  if(!sessionContainer.getAttribute("slot_FOperatore").toString().equals("")) { foper =  true; }
  else { foper = false; }
} else { foper = false; }
if(sessionContainer.getAttribute("slot_FServizio")!=null) { 
  if(!sessionContainer.getAttribute("slot_FServizio").toString().equals("")) { fserv =  true; }
  else { fserv = false; }
} else { fserv = false; }
if(sessionContainer.getAttribute("slot_FAula")!=null) { 
  if(!sessionContainer.getAttribute("slot_FAula").toString().equals("")) { faula =  true; }
  else { faula = false; }
} else { faula = false; }

%>

<%
PageAttribs attributi = new PageAttribs(user, "GestSlotPage");
boolean canInsSlot = attributi.containsButton("NEW_APP");
boolean canSearch = attributi.containsButton("CERCA");
boolean canPrintG = attributi.containsButton("STAMPAG");
boolean canInsSlotTipo = attributi.containsButton("SLOT_SETT_TIPO");
boolean canCancMassiva = attributi.containsButton("CANC_MASSIVA");
boolean canDelSlot = attributi.containsButton("DEL_APP");
String labelServizio ="Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equals("1")){
	labelServizio = "Area";
}
%>


<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Gestione Slot</title>
  <af:linkScript path="../../js/" />
  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
  <script language="Javascript">
  function cerca_sub(n)
  {
      // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;

      if(n==1) { document.form_cerca.PAGE.value="FiltriSlotPage"; }
      if(n==2) { document.form_cerca.PAGE.value="RicercaSlotPage"; }
      if(n==3) { document.form_cerca.PAGE.value="SlotDaSettTipoPage"; }
      if(n==4) { document.form_cerca.PAGE.value="ParCancMassivaSlotPage"; }
      if(n==5) { document.form_cerca.PAGE.value="ImpostaSlotPage"; }
      doFormSubmit(document.form_cerca);
  }


  function conferma_del_slot(url, alertFlag) 
  {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var _url = "AdapterHTTP?" + url;
    if (alertFlag == 'TRUE' ) {
      var msg = "";
      var params = url.split("&");
      var par, nome, val;
      var i, j;
      trovato = false;
      for(i=0; i< params.length && !trovato; i++) {
        param = params[i].split("=");
        nome = param[0];
        val = param[1];
        if(nome == "PRGAPPUNTAMENTO") {
          trovato = true;
        }    
      }
      if(trovato) {
        if(val != "" && val!= "PRGAPPUNTAMENTO") {
            msg = "Lo slot è collegato ad un appuntamento.\nSei sicuro di volerlo cancellare?";
          } else { msg = "Confermi la cancellazione dello Slot?"; }
      } else {
        msg = "Confermi la cancellazione dello Slot?";
      }
      if (confirm(msg))
        setWindowLocation(_url);
    } else { setWindowLocation(_url); }
  }
  </script>
</head>

<body class="gestione" onload="rinfresca()">
<h2>
  <%if(!streamDescrCpi.equals("")) {%>
  <b>Gestione degli Slot - Centro per l'Impiego di <%=streamDescrCpi%></b>
  <%}%>
</h2>
<!-- Modifiche per le stondature S.O. 08/03/2004 -->
<table maxwidth="96%" width="96%" align="center" margin="0" cellpadding="0" cellspacing="0">
<tr>
  <td class="cal_header" valign="top" align="left" width="6" height="6px"><img src="../../img/angoli/bia1.gif" width="6" height="6"></td>
  <td class="cal_header" height="6px">&nbsp;</td>
  <td class="cal_header" valign="top" align="right" width="6" height="6px"><img src="../../img/angoli/bia2.gif" width="6" height="6"></td>
</tr>
<tr>
<td class="cal" width="6">&nbsp;</td>
<td class="cal" align="center">
<!-- end top -->
  <table class="cal" align="center" cellspacing="1">
    <tr class="note">
      <td align="left" class="cal_legenda1">
      <af:form name="formVista" action="AdapterHTTP" method="POST" dontValidate="true">
      Evidenzia:&nbsp;
      <af:comboBox name="cod_vista"
                     size="1"
                     title="Scelta Vista"
                     multiple="false"
                     disabled="false"
                     focusOn="false"
                     moduleName="MSel_TipoVista_Slot"
                     selectedValue="<%=cod_vista%>"
                     addBlank="true"
                     blankValue=""
        /><input type="submit" class="pulsanti" value="Evidenzia"/>
        
      <input name="PAGE" type="hidden" value="GestSlotPage"/>
      <% if(mod==0) {%>
            <input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
            <input name="meseDB" type="hidden" value="<%=meseDB%>"/>
            <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
      <% } else { %>
            <% if(mod==1) { %>
                <input name="nrosDB" type="hidden" value="<%=nrosDB%>"/>
                <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
            <% } else {%>
                <%if(mod==2) { // Ricerca precedente%>
                  <input name="sel_operatore" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore")%>"/>
                  <input name="sel_servizio" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio")%>"/>
                  <input name="sel_aula" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula")%>"/>
                  <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDal")%>"/>
                  <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
                <%}%>
            <%}%>
      <% } %>
      <input name="giorno" type="hidden" value="<%=giorno%>"/>
      <input name="mese" type="hidden" value="<%=mese%>"/>
      <input name="anno" type="hidden" value="<%=anno%>"/>
      <input name="MOD" type="hidden" value="<%=mod%>"/>
      <input name="CODCPI" type="hidden" value="<%=codCpi%>"/>
      </af:form>
      </td>
      
      <td class="cal_legenda2">
      <table>
      <tr valign="middle">
        <td class="cal"><b>Filtri Attivati</b></td>
        <td align="left">
          <button type="button" onClick="cerca_sub(1);"  class="ListButtonChangePage"><img src="../../img/filtro.gif" alt="">&nbsp;Impostazione Filtri</button>
        </td>
      </tr>

      <tr valign="middle">
        <td class="cal">
        <% if(fmiei) { %> 
            <img src="../../img/check.gif" alt="S&igrave;">
        <% } else { %> 
            <img src="../../img/no_check.gif" alt="No">
        <% }%>
        &nbsp;Miei Slot&nbsp;
        </td>
        <td class="cal">
        <%if(foper) { %>
            <img src="../../img/check.gif" alt="S&igrave;">
        <%} else { %> 
            <img src="../../img/no_check.gif" alt="No">
        <% }%>
        &nbsp;Operatore
        </td>
      </tr>
      <tr valign="middle">
        <td class="cal">
        <%if(fserv) { %>
            <img src="../../img/check.gif" alt="S&igrave;">
        <%} else { %>
            <img src="../../img/no_check.gif" alt="No">
        <% }%>
        &nbsp;<%=labelServizio %>
        </td>
        <td class="cal" style="white-spaces: nowrap">
        <%if(faula) { %>
            <img src="../../img/check.gif" alt="S&igrave;">
        <%} else { %>
            <img src="../../img/no_check.gif" alt="No">
        <% }%>
        &nbsp;Ambiente/Aula
        </td>
      </tr>
      </table>
      </td>
    </tr>
    <tr>
      <td class="cal_legenda">
        <table margin="0" cellpadding="1" cellspacing="0">
        <tr>
          <td class="cal_evid_nobg">
            <span class="cal_evid"><img src="../../img/quadro_trasp.gif"></span>
            &nbsp;Giorno Evidenziato&nbsp;
          </td>
          <td class="cal_sel_nobg">
            <span class="cal_sel"><img src="../../img/quadro_trasp.gif"></span>
            &nbsp;Giorno Selezionato
          </td>
          <td class="cal_vuoto_nobg">
            <span class="cal_vuoto"><img src="../../img/quadro_trasp.gif"></span>
            &nbsp;Giorno Non Lavorativo
          </td>
        </tr>
        </table>
      </td>
      <td class="cal_legenda">&nbsp;
          <img src="../../img/warning_trasp.gif">&nbsp;Incongruenze&nbsp;&nbsp;
          <img src="../../img/caselle_trasp.gif">&nbsp;Slot&nbsp;&nbsp;  
          </td>
    </tr>
    </table>
<!--Modifiche per le stondature (chiusura) S.O. 08/03/2004-->
</td>
<td class="cal" width="6">&nbsp;</td>
</tr>
<tr valign="bottom">
  <td class="cal_header" valign="bottom" align="left" width="6" height="6px"><img src="../../img/angoli/bia4.gif" width="6" height="6"></td>
  <td class="cal_header" height="6px">&nbsp;</td>
  <td class="cal_header" valign="bottom" align="right" width="6" height="6px"><img src="../../img/angoli/bia3.gif" width="6" height="6"></td>
</tr>
</table>
<!-- end bottom -->
<br>
<table width="96%" maxwidth="96%" align="center" cellpadding="0" cellspacing="0px">
<tr class="note">
  <td align="left" width="250px">
  <!-- Calendario Mensile Navigabile -->
      <cal:calendario moduleName="MGiorni_NL"  mod="<%=MOD%>" pageName="GestSlotPage" attivaFestivi="0" modVista="MGIORNIVISTASLOT"  codCpi="<%=codCpi%>"/>
      <br>
      <!-- PULSANTI PER LE RICERCHE -->
      <af:form name="form_cerca" action="AdapterHTTP" method="POST" dontValidate="true">
      <input name="PAGE" type="hidden" value=""/>
      <% if(mod==0) {%>
            <input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
            <input name="meseDB" type="hidden" value="<%=meseDB%>"/>
            <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
      <% } else { %>
            <% if(mod==1) { %>
                <input name="nrosDB" type="hidden" value="<%=nrosDB%>"/>
                <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
            <% } else {%>
                <%if(mod==2) { // Ricerca precedente%>
                  <input name="sel_operatore" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore")%>"/>
                  <input name="sel_servizio" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio")%>"/>
                  <input name="sel_aula" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula")%>"/>
                  <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDal")%>"/>
                  <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
                <%}%>
            <%}%>
      <% } %>
      <input name="giorno" type="hidden" value="<%=giorno%>"/>
      <input name="mese" type="hidden" value="<%=mese%>"/>
      <input name="anno" type="hidden" value="<%=anno%>"/>
      <input name="MOD" type="hidden" value="<%=mod%>"/>
      <input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
      <input name="CODCPI" type="hidden" value="<%=codCpi%>"/>

      <%if(canInsSlotTipo) {%>
        <div onClick="cerca_sub(3);"  class="ListButtonChangePage"><img src="../../img/caselle.gif" alt="">&nbsp;Inserimento Slot da Settimana Tipo</div>
        <br>
		<div onClick="cerca_sub(5);"  class="ListButtonChangePage"><img src="../../img/caselle.gif" alt="">&nbsp;Inserimento Massivo Slot</div>        
		<br>
      <%}%>
      <%if(canCancMassiva) {%>
        <div onClick="cerca_sub(4);"  class="ListButtonChangePage"><img src="../../img/del.gif" alt="">&nbsp;Cancellazione Massiva Slot</div>
        <br>
      <%}%>
      <%if(canSearch) {%>
        <div onClick="cerca_sub(2);"  class="ListButtonChangePage"><img src="../../img/binocolo.gif" alt="">&nbsp;Cerca Slot</div>
        <br>
      <%}%>
      <%if(canPrintG) {%>
        <div onClick="avviso();"  class="ListButtonChangePage" disabled><img src="../../img/stampa.gif" alt="" disabled>&nbsp;Stampa Giornaliera</div>
      <%}%>
      </af:form>
  </td>
  <td width="16px">&nbsp;</td>
  <td align="left" valign="top">
    <% if(mod==0 || mod==2) { %>
        <cal:error/>
        <%if(mod==0) {%>
            <cal:list moduleName="MSlot" mod="<%=MOD%>" linkPage="GestSlotPage" configLabelServizio="<%=umbriaGestAz %>" 
            titolo="Slot" extra="0" nuovoPage="NuovoSlotPage" nuovoTxt="Inserisci" codCpi="<%=codCpi%>"/>
        <%}else{%>
             <cal:list moduleName="MSlotRicerca" mod="<%=MOD%>" linkPage="GestSlotPage"
             titolo="Slot" extra="0"  codCpi="<%=codCpi%>"/>
        <%}%>
        <!-- LEGENDA -->
        <!--div class="sezione2">Legenda</div>
        <div align="left">
          <img src="../../img/detail.gif"/>&nbsp;Dettaglio&nbsp;
          <img src="../../img/warning.gif"/>&nbsp;App. con Incongruenze
        </div-->
    <% } else { %>
          <%
            if(mod==1) {
              out.print(htmlStream);
            }
          %>
    <%} // if(mod==0)%>
  </td>
</tr>
</table>


</body>
</html>
