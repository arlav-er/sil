<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.security.PageAttribs,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>

<%@ taglib uri="cal" prefix="cal" %>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
// Response
//ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(request);
//SourceBean serviceResponse = responseContainer.getServiceResponse();

//Request
//RequestContainer requestContainer = RequestContainerAccess.getRequestContainer(request);
//SourceBean serviceRequest= requestContainer.getServiceRequest();

String streamDescrCpi = "";
SourceBean content = (SourceBean) serviceResponse.getAttribute("MDESCRCPI");
if(content!=null) {
  SourceBean row = (SourceBean) content.getAttribute("ROWS.ROW");
  if(row!=null) { streamDescrCpi = row.getAttribute("STRDESCRIZIONE").toString(); }
}

int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();

String giornoDB = "";
String meseDB = "";
String annoDB = "";
// Istanzio giornoDB, meseDb, annoDb per l'inserimento di un nuovo appuntamento
// Data Odierna
Calendar oggi = Calendar.getInstance();
giornoDB = Integer.toString(oggi.get(5));
meseDB = Integer.toString(oggi.get(2)+1);
annoDB = Integer.toString(oggi.get(1));

String codCpi  = StringUtils.getAttributeStrNotNull(serviceRequest,"CODCPI");
if (codCpi.equalsIgnoreCase("")) {
	if(cdnTipoGruppo==1) { 
		codCpi = user.getCodRif(); 
	}
	if(codCpi==null || cdnTipoGruppo!=1 || codCpi.equalsIgnoreCase("")) { 
		response.sendRedirect("../../servlet/fv/AdapterHTTP?PAGE=SelezionaCPIPage&giornoDB="+giornoDB+"&meseDB="+meseDB+"&annoDB="+annoDB);
	}
}

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
  sessionContainer.delAttribute("agenda_FMiei");
  sessionContainer.delAttribute("agenda_FOperatore");
  sessionContainer.delAttribute("agenda_FServizio");
  sessionContainer.delAttribute("agenda_FAula");
  sessionContainer.delAttribute("agenda_FAttivi");
}
String MOD = Integer.toString(mod);
String htmlStream = "";
if(mod == 1) {
  htmlStream = ShowApp.listaAppSettimana(requestContainer, serviceResponse, codCpi);
}

String labelServizio ="Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equals("1")){
	labelServizio = "Area";
}
%>

<%
String queryString = null;
boolean fmiei;
boolean foper;
boolean fserv;
boolean faula;
boolean fattivi;

if(sessionContainer.getAttribute("agenda_FMiei")!=null) {
  if(!sessionContainer.getAttribute("agenda_FMiei").toString().equals("")) { fmiei =  true; }
  else { fmiei = false; }
} else { fmiei = false; }
if(sessionContainer.getAttribute("agenda_FOperatore")!=null) {
  if(!sessionContainer.getAttribute("agenda_FOperatore").toString().equals("")) { foper =  true; }
  else { foper = false; }
} else { foper = false; }
if(sessionContainer.getAttribute("agenda_FServizio")!=null) {
  if(!sessionContainer.getAttribute("agenda_FServizio").toString().equals("")) { fserv =  true; }
  else { fserv = false; }
} else { fserv = false; }
if(sessionContainer.getAttribute("agenda_FAula")!=null) {
  if(!sessionContainer.getAttribute("agenda_FAula").toString().equals("")) { faula =  true; }
  else { faula = false; }
} else { faula = false; }
if(sessionContainer.getAttribute("agenda_FAttivi")!=null) {
  if(!sessionContainer.getAttribute("agenda_FAttivi").toString().equals("")) { fattivi =  true; }
  else { fattivi = false; }
} else { fattivi = false; }

String sel_operatore = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore");
String sel_servizio = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio");
String sel_aula = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula");
String esitoApp = StringUtils.getAttributeStrNotNull(serviceRequest,"esitoApp");
String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
String strNome = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
String strRagSoc = StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc");
String dataDal = StringUtils.getAttributeStrNotNull(serviceRequest,"dataDal");
String dataAl = StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl");
String piva = StringUtils.getAttributeStrNotNull(serviceRequest,"piva");
String strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscaleAz");
%>

<%
PageAttribs attributi = new PageAttribs(user, "PCalendario");
boolean canInsApp = attributi.containsButton("NEW_APP");
boolean canInsEv = attributi.containsButton("NEW_EV");
boolean canInsCont = attributi.containsButton("NEW_CONT");
boolean canSearch = attributi.containsButton("CERCA");
boolean canPrintG = attributi.containsButton("STAMPAG");
boolean canChangeCpi = attributi.containsButton("CAMBIA_CPI");
%>


<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Calendario</title>
  <af:linkScript path="../../js/"/>
  <%@ include file="../documenti/_apriGestioneDoc.inc"%>
  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
  <script language="Javascript">

  function EffettuaStampaRicerca () {
    //apriGestioneDoc('RPT_RICERCA_APP_AGENGA','&codcpi=<%=codCpi%>&codicefiscale=<%=strCodiceFiscale%>&cognome=<%=strCognome%>&nome=<%=strNome%>&ragionesociale=<%=strRagSoc%>&operatore=<%=sel_operatore%>&servizio=<%=sel_servizio%>&ambiente=<%=sel_aula%>&esito=<%=esitoApp%>&dataDal=<%=dataDal%>&dataAl=<%=dataAl%>&','RAP');
    var txtRic = '&codcpi=<%=codCpi%>&codicefiscale=<%=strCodiceFiscale%>&cognome=<%=strCognome%>&nome=<%=strNome%>';
    txtRic += '&ragionesociale=<%=strRagSoc%>&piva=<%=piva%>&strCodiceFiscaleAz=<%=strCodiceFiscaleAz%>';
    txtRic += '&operatore=<%=sel_operatore%>&servizio=<%=sel_servizio%>';
    txtRic += '&ambiente=<%=sel_aula%>&esito=<%=esitoApp%>';
    txtRic += '&dataDal=<%=dataDal%>&dataAl=<%=dataAl%>&';
    apriGestioneDoc('RPT_RICERCA_APP_AGENGA', txtRic, 'RAP');
  }
  
  function cerca_sub(n)
  {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
  
      if(n==1) { document.form_cerca.PAGE.value="PFiltriAppuntamenti"; }
      if(n==2) { document.form_cerca.PAGE.value="PRicercaAppuntamenti"; }
      if(n==3) { document.form_cerca.PAGE.value="PRicercaContatti"; }
      if(n==4) { document.form_cerca.PAGE.value="SelezionaCPIPage"; }
      doFormSubmit(document.form_cerca);
  }

  function open_agStampe_PopUP() {
     window.open ("AdapterHTTP?PAGE=AGENDA_STAMPA_GIORNALIERA_PAGE&CODCPI=<%=codCpi%>", '', 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
  }

  function open_agStampe_SMS() {
    window.open ("AdapterHTTP?PAGE=AGENDA_STAMPA_SMS_PAGE&CODCPI=<%=codCpi%>&giorno=<%=giornoDB%>&mese=<%=meseDB%>&anno=<%=annoDB%>", '', 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
  }

  </script>
</head>

<body class="gestione" onload="rinfresca()">
<h2>
<%if(!streamDescrCpi.equals("")) {%>
  <b>Agenda Appuntamenti - Centro per l'Impiego di <%=streamDescrCpi%></b>
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
                     required="false"
                     focusOn="false"
                     moduleName="MSel_TipoVista_Giorni"
                     selectedValue="<%=cod_vista%>"
                     addBlank="true"
                     blankValue=""
        /><input type="submit" class="pulsanti" value="Evidenzia"/>
        <input name="PAGE" type="hidden" value="PCalendario"/>
        <% if(mod==0) { %>
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
                    <input name="esitoApp" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"esitoApp")%>"/>
                    <input name="strCodiceFiscale" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale")%>"/>
                    <input name="strCognome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome")%>"/>
                    <input name="strNome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strNome")%>"/>
	                <input name="strCodiceFiscaleAz" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscaleAz")%>"/>
	                <input name="piva" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"piva")%>"/>
                    <input name="strRagSoc" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc")%>"/>
                    <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDal")%>"/>
                    <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
                  <%} else { %>
	               <%if(mod==3) {%>
		              <input name="sel_operatore" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore")%>"/>
		              <input name="sel_tipo" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_tipo")%>"/>
		              <input name="STRIO" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"STRIO")%>"/>
		              <input name="sel_motivo" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_motivo")%>"/>
		              <input name="effettoCon" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"effettoCon")%>"/>
		              <input name="strCodiceFiscale" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale")%>"/>
		              <input name="strCognome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome")%>"/>
		              <input name="strNome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strNome")%>"/>
	                  <input name="strCodiceFiscaleAz" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscaleAz")%>"/>
	                  <input name="piva" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"piva")%>"/>
		              <input name="strRagSoc" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc")%>"/>
		              <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDal")%>"/>
		              <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
	 			   <% } %>
	 		   <% } %>
           <%}%>
        <% } %>
        <input name="CODCPI" type="hidden" value="<%=codCpi%>"/>
        <input name="giorno" type="hidden" value="<%=giorno%>"/>
        <input name="mese" type="hidden" value="<%=mese%>"/>
        <input name="anno" type="hidden" value="<%=anno%>"/>
        <input name="MOD" type="hidden" value="<%=mod%>"/>
        <input name="umbriaGestAz" type="hidden" value="<%=umbriaGestAz%>"/>
        
      </af:form>
      </td>

      <td class="cal_legenda2">
      <table>
      <tr valign="middle">
        <td class="cal"><b>Filtri Attivati</b></td>
        <td align="left" colspan="2">
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
        &nbsp;Miei appuntamenti&nbsp;
        </td>
        <td class="cal">
        <%if(foper) { %>
            <img src="../../img/check.gif" alt="S&igrave;">
        <%} else { %>
            <img src="../../img/no_check.gif" alt="No">
        <% }%>
        &nbsp;Operatore
        </td>
        <td class="cal">
        <%if(fattivi) { %>
            <img src="../../img/check.gif" alt="S&igrave;">
        <%} else { %>
            <img src="../../img/no_check.gif" alt="No">
        <% }%>
        &nbsp;Attivi
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
      <!-- Calendario -->
      <cal:calendario moduleName="MGiorni_NL"  mod="<%=MOD%>" pageName="PCalendario" attivaFestivi="0" modVista="MGIORNI_VISTA" codCpi="<%=codCpi%>"/>
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
                  <input name="esitoApp" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"esitoApp")%>"/>
                  <input name="strCodiceFiscale" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale")%>"/>
                  <input name="strCognome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome")%>"/>
                  <input name="strNome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strNome")%>"/>
                  <input name="strCodiceFiscaleAz" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscaleAz")%>"/>
                  <input name="piva" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"piva")%>"/>
                  <input name="strRagSoc" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc")%>"/>
                  <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDal")%>"/>
                  <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
                <%} else  { %>
               <%if(mod==3) {%>
	              <input name="sel_operatore" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore")%>"/>
	              <input name="sel_tipo" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_tipo")%>"/>
	              <input name="STRIO" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"STRIO")%>"/>
	              <input name="sel_motivo" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_motivo")%>"/>
	              <input name="effettoCon" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"effettoCon")%>"/>
	              <input name="strCodiceFiscale" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale")%>"/>
	              <input name="strCognome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome")%>"/>
	              <input name="strNome" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strNome")%>"/>
                  <input name="strCodiceFiscaleAz" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscaleAz")%>"/>
                  <input name="piva" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"piva")%>"/>
	              <input name="strRagSoc" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc")%>"/>
	              <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDal")%>"/>
	              <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
 			   <% } %>
 		   <% } %>
          <%}%>
      <% } %>
      <input name="giorno" type="hidden" value="<%=giorno%>"/>
      <input name="mese" type="hidden" value="<%=mese%>"/>
      <input name="anno" type="hidden" value="<%=anno%>"/>
      <input name="MOD" type="hidden" value="<%=mod%>"/>
      <input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
      <input name="CODCPI" type="hidden" value="<%=codCpi%>"/>

      <%if(canSearch) {%>
        <div onClick="cerca_sub(2);"  class="ListButtonChangePage"><img src="../../img/binocolo.gif" alt="">&nbsp;Cerca Appuntamento</div>
        <div onClick="cerca_sub(3);" class="ListButtonChangePage"><img src="../../img/binocolo.gif" alt="">&nbsp;Cerca Contatto</div>
      <%}%>
      <%if(canPrintG) {%>
        <div onClick="open_agStampe_PopUP();"  class="ListButtonChangePage"><img src="../../img/stampa.gif" alt="">&nbsp;Stampa Giornaliera</div>
      <%}%>
      <div onClick="open_agStampe_SMS();"  class="ListButtonChangePage"><img src="../../img/stampa.gif" alt="">&nbsp;Stampa SMS</div>
      <%if(canChangeCpi) {%>
        <div onClick="cerca_sub(4);"  class="ListButtonChangePage"><img src="../../img/coop_app.gif" alt="">&nbsp;Cambia CPI</div>
      <%}%>

      </af:form>
  </td>
  <td width="16px">&nbsp;</td>
  <td align="left" valign="top">
    <% if(mod==0 || mod==2) { %>
        <cal:error/>
        <%if(mod==0) {%>
            <cal:list moduleName="MAppuntamenti" mod="<%=MOD%>" linkPage="PCalendario" codCpi="<%=codCpi%>" configLabelServizio="<%=umbriaGestAz %>"  
                       titolo="Appuntamenti" extra="1" nuovoPage="NEW_AGENDA_PAGE" nuovoTxt="Inserisci"/>
            <br>
            <%
              /* Spostato in data 09/03/2004 nella tag library a causa degli stili */
              //String htmlContatti = ShowApp.listaContattiGiorno(requestContainer, serviceResponse, responseContainer);
              //out.print(htmlContatti);
            %>
        <%}else{
           
        	SourceBean rowAppRicerca = null;
            rowAppRicerca = (SourceBean) serviceResponse.getAttribute("MAppuntamentiRicerca");
            Vector rows_VectorAppuntamenti = null;
            rows_VectorAppuntamenti = rowAppRicerca.getAttributeAsVector("PAGED_LIST.ROWS.ROW");
           
          %>
             <af:list moduleName="MAppuntamentiRicerca" />
          <%if (rows_VectorAppuntamenti.size() > 0) {%>
             <center>
             <table><tr><td align="center">
             <input class="pulsante" type="button" name="btnStampaRicerca" value="Stampa" onclick="javascript:EffettuaStampaRicerca();"/>
             </td></tr></table>
             </center>
          <%}%>
        <%}%>
    <% } else { %>
          <%
            if(mod==1) {
              out.print(htmlStream);
            }
          %>
          <%
            if(mod==3) {%>
            <af:list moduleName="MContattiRicerca"/>
             <%        
            /*
              SourceBean rowConRicerca = null;
              rowConRicerca = (SourceBean) serviceResponse.getAttribute("MContattiRicerca");
              Vector rows_VectorContatti = null;
              rows_VectorContatti = rowConRicerca.getAttributeAsVector("PAGED_LIST.ROWS.ROW");
              String htmlContatti = ShowApp.listContattiRicerca (requestContainer, serviceResponse, responseContainer, codCpi);
              out.print(htmlContatti);
            */  
            }
          %>
	
    <%  } // if(mod==0)%>
  </td>
</tr>
</table>
<%//out.print(serviceResponse.toXML());%>

</body>
</html>
