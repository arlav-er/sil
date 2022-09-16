<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,
                it.eng.afExt.utils.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
String giornoDB = "";
String meseDB = "";
String annoDB = "";
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

String labelServizio = "Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equalsIgnoreCase("1")){
	labelServizio = "Area";
}
%>

<%
String miei = "";
String oper = "";
String serv = "";
String aula = "";
String attivi = "";
String codCpi  = StringUtils.getAttributeStrNotNull(serviceRequest,"CODCPI");

if(sessionContainer.getAttribute("agenda_FMiei")!=null) { miei = sessionContainer.getAttribute("agenda_FMiei").toString(); }
if(sessionContainer.getAttribute("agenda_FOperatore")!=null) { oper = sessionContainer.getAttribute("agenda_FOperatore").toString(); }
if(sessionContainer.getAttribute("agenda_FServizio")!=null) { serv = sessionContainer.getAttribute("agenda_FServizio").toString(); }
if(sessionContainer.getAttribute("agenda_FAula")!=null) { aula = sessionContainer.getAttribute("agenda_FAula").toString(); }
if(sessionContainer.getAttribute("agenda_FAttivi")!=null) { attivi = sessionContainer.getAttribute("agenda_FAttivi").toString(); }
%>
<%
/*
  NOTA: le pagine di ricerca devono avere lo stile prof_ro -> quindi invece di
  canModify si deve passare il valore false
*/
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>


<html>
<head>
  <title>Filtri Appuntamenti/Contatti</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/" />
  <script language="Javascript">
  function checkOperatore(n)
  {
    if(n==1) {
      if(document.form.solo_miei.checked && (document.form.fsel_operatore.selectedIndex!=0)) {
        document.form.fsel_operatore.selectedIndex=0;
      }
    }

    if(n==2) {
      if((document.form.fsel_operatore.selectedIndex!=0) && document.form.solo_miei.checked) {
        document.form.solo_miei.checked = false;
      }
    }
  }

  function disattivaFiltri()
  {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    document.form.solo_miei.checked = false;
    document.form.fsel_operatore.selectedIndex = 0;
    document.form.fsel_servizio.selectedIndex = 0;
    document.form.fsel_aula.selectedIndex = 0;
    document.form.solo_attivi.checked = false;
    doFormSubmit(document.form);
  }
  </script>
</head>

<body class="gestione">


<br>
<p class="titolo">Impostazione Filtri sugli Appuntamenti e sui Contatti</p>
<%out.print(htmlStreamTop);%>
<af:form action="AdapterHTTP" name="form" method="POST" dontValidate="true">
<input name="PAGE" type="hidden" value="PImpostaFiltriApp"/>
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
            <input name="strIdCoap" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strIdCoap")%>"/>
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
                  <input name="strIdCoap" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strIdCoap")%>"/>
 			   <% } %>
 		   <% } %>
      <%}%>
<% } %>
<input name="giorno" type="hidden" value="<%=giorno%>"/>
<input name="mese" type="hidden" value="<%=mese%>"/>
<input name="anno" type="hidden" value="<%=anno%>"/>
<input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
<input name="MOD" type="hidden" value="<%=mod%>"/>
<input name="CODCPI" type="hidden" value="<%=codCpi%>"/>

<table class="main">
<tr>
  <td class="etichetta">Solo i Miei Appuntamenti/Contatti</td>
  <td class="campo">
  <input type="checkbox" name="solo_miei" value="1" onClick="checkOperatore(1)"
  <% if(miei.equals("1")) { out.print("CHECKED"); }%>
  >
  </td>
</tr>
<tr>
  <td class="etichetta">Operatore</td>
  <td class="campo">
    <af:comboBox name="fsel_operatore"
                 size="1"
                 title="Scelta Operatore"
                 multiple="false"
                 required="false"
                 focusOn="false"
                 moduleName="COMBO_SPI"
                 addBlank="true"
                 blankValue=""
                 onChange="checkOperatore(2)"
                 selectedValue = "<%=oper%>"
    />
  </td>
</tr>

<tr>
  <td class="etichetta">Solo Attivi</td>
  <td class="campo">
  <input type="checkbox" name="solo_attivi" value="1" 
  <% if(attivi.equals("1")) { out.print("CHECKED"); }%>
  >
  </td>
</tr>


<tr>
	<%String titleServizio = "Scelta "+labelServizio; %>
  <td class="etichetta"><%=labelServizio %></td>
  <td class="campo">
    <af:comboBox name="fsel_servizio"
                 size="1"
                 title="<%=titleServizio %>"
                 multiple="false"
                 required="false"
                 focusOn="false"
                 moduleName="COMBO_SERVIZIO"
                 addBlank="true"
                 blankValue=""
                 selectedValue = "<%=serv%>"
    />
  </td>
</tr>
<tr>
  <td class="etichetta">AmbienteAula</td>
  <td class="campo">
    <af:comboBox name="fsel_aula"
                 size="1"
                 title="Scelta Ambiente/Aula"
                 multiple="false"
                 required="false"
                 focusOn="false"
                 moduleName="COMBO_AMBIENTE"
                 addBlank="true"
                 blankValue=""
                 selectedValue = "<%=aula%>"
    />
  </td>
</tr>
</table>
<br>
<span class="bottoni">
  <input type="submit" class="pulsanti" value="Attiva Filtri">
  &nbsp;&nbsp;
  <input type="reset" class="pulsanti" value="Annulla">
  &nbsp;&nbsp;
  <input type="button" class="pulsanti" value="Disattiva Filtri" onClick="disattivaFiltri();">
</span>
</p>
</af:form>

<af:form name="formBack" action="AdapterHTTP" method="POST" dontValidate="true">
  <input name="PAGE" type="hidden" value="PCalendario"/>
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
                  <input name="strIdCoap" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strIdCoap")%>"/>
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
                      <input name="strIdCoap" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strIdCoap")%>"/>
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

  <p align="center">  
  <span class="bottoni"><input type="submit" class="pulsanti" value="Chiudi"/></span>
  </p>
</af:form>
<%out.print(htmlStreamBottom);%>
<%//out.print(serviceResponse.toXML());%>
</body>
</html>
