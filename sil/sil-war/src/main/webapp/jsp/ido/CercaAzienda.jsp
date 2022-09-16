<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import="
  com.engiweb.framework.base.*,
  java.lang.*,
  java.text.*,
  java.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.*,
  it.eng.sil.util.*,
  com.engiweb.framework.util.*" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../anag/Function_CommonRicercaCPI.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "IdoAziendaRicercaPage");
  boolean canInsert = attributi.containsButton("INSERISCI");
  int _funzione= Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  String RagioneSociale = StringUtils.getAttributeStrNotNull(serviceRequest,"RagioneSociale");
  RagioneSociale = com.engiweb.framework.tags.Util.quote(RagioneSociale); 
  String cf = StringUtils.getAttributeStrNotNull(serviceRequest,"cf");
  String codAzStato = StringUtils.getAttributeStrNotNull(serviceRequest,"codAzStato");
  String codCPI = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPI");
  String codCPIHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIHid");
  String codCPIifDOMeqRESHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIifDOMeqRESHid");
  String codComAz = StringUtils.getAttributeStrNotNull(serviceRequest,"codComAz");
  String codComAzHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codComAzHid");
  String codProvincia = StringUtils.getAttributeStrNotNull(serviceRequest,"codProvincia");
  String codTipoAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAzienda");
  String indirizzo = StringUtils.getAttributeStrNotNull(serviceRequest,"indirizzo");
  indirizzo = com.engiweb.framework.tags.Util.quote(indirizzo); 
  String piva = StringUtils.getAttributeStrNotNull(serviceRequest,"piva");
  String strCPI = StringUtils.getAttributeStrNotNull(serviceRequest,"strCPI");
  String strCPIHid = StringUtils.getAttributeStrNotNull(serviceRequest,"strCPIHid=");
  String strComAz = StringUtils.getAttributeStrNotNull(serviceRequest,"strComAz");
  String strComAzHid = StringUtils.getAttributeStrNotNull(serviceRequest,"strComAzHid");
  String codNatGiuridica = StringUtils.getAttributeStrNotNull(serviceRequest,"codNatGiuridica");
  String strDenominazioneAz = StringUtils.getAttributeStrNotNull(serviceRequest,"strDenominazioneAz");
  
  String codAteco = StringUtils.getAttributeStrNotNull(serviceRequest,"codAteco");
  String strTipoAteco = StringUtils.getAttributeStrNotNull(serviceRequest,"strTipoAteco");
  String strAteco = StringUtils.getAttributeStrNotNull(serviceRequest,"strAteco");
  String codAtecoHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codAtecoHid");
  
  String flgSedeLegale = StringUtils.getAttributeStrNotNull(serviceRequest,"flgSedeLegale");
  strDenominazioneAz = com.engiweb.framework.tags.Util.quote(strDenominazioneAz); 
  
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Cerca azienda</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

 <SCRIPT language="JavaScript" src="../../js/Function_CommonRicercaCCNL.js"></SCRIPT> 

     <script language="Javascript">
     var inputs = new Array(0);
     var flagRicercaPage = "S";
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>

      function checkCampiObbligatori()
      { 
        if ((document.Frm1.RagioneSociale.value.length > 0 ) || (document.Frm1.indirizzo.value.length > 0) ||
            (document.Frm1.cf.value.length > 0) || (document.Frm1.piva.value.length > 0) ||
            (document.Frm1.codComAz.value.length > 0) || (document.Frm1.codAteco.value.length > 0)) {
          return true;
        }
        alert("Inserire almeno uno dei seguenti campi nella ricerca:\n\tComune\n\tIndirizzo\n\tCodice Fiscale\n\tPartita IVA\n\tRagione Sociale\n\tCodice Attività\n");
        return false;
      }

     function cercaTestata()
     {
        document.Frm1.PAGE.value = "IdoListaTestateAziendePage";
        //doFormSubmit(document.Frm1);    
     }

     function cercaUnita()
     {
       document.Frm1.PAGE.value = "IdoListaAziendePage";
       //doFormSubmit(document.Frm1);    
     }
     
	if (window.top.menu == undefined){
  		window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
	}
	
 function selectATECO_onClick(codAteco, codAtecoHid, strAteco, strTipoAteco) {	
	if (codAteco.value == "") {
    strAteco.value = "";
    strTipoAteco.value = "";
  }

  else {
  		var w=1000; var l=((screen.availWidth)-w)/2;
  		var h=750; var t=((screen.availHeight)-h)/2;
  		var feat = "toolbar=0, scrollbars=1,height="+h+",width="+w+",top="+t+",left="+l;
		var opened=window.open("AdapterHTTP?PAGE=RicercaAtecoPage&codAteco="+codAteco.value, "Attività", feat);
		opened.focus();
      	
      	//window.open("AdapterHTTP?PAGE=RicercaAtecoPage&codAteco="+codAteco.value, "Attività", "toolbar=0, scrollbars=1");
    	}
    }

function ricercaAvanzataAteco() {
		var w=1000; var l=((screen.availWidth)-w)/2;
  		var h=750; var t=((screen.availHeight)-h)/2;
  		var feat = "toolbar=0, scrollbars=1,height="+h+",width="+w+",top="+t+",left="+l;
		var opened = window.open("AdapterHTTP?PAGE=RicercaAtecoAvanzataPage", "Attività", feat);
		opened.focus();
  		
  		//window.open("AdapterHTTP?PAGE=RicercaAtecoAvanzataPage", "Attività", "toolbar=0, scrollbars=1");
}

</script>
</head>

<body class="gestione" onload="rinfresca();">
<p class="titolo">Ricerca sull'anagrafica Aziende</p>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="checkCampiObbligatori()">
<p align="center">
<table class="main"> 
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td class="etichetta">Tipo Azienda</td>
  <td class="campo">
    <af:comboBox classNameBase="input" title="Tipo Azienda" name="codTipoAzienda" moduleName="M_GetTipiAzienda" addBlank="true" selectedValue="<%=codTipoAzienda%>" />
  </td>
</tr>
<tr>
  <td class="etichetta">Stato Azienda</td>
  <td class="campo">
    <af:comboBox classNameBase="input" title="Stato Azienda" name="codAzStato" moduleName="M_GetStatiAzienda" addBlank="true" 
      selectedValue="<%=codAzStato%>" />
  </td>
</tr>
<tr valign="top">
  <td class="etichetta">Natura azienda</td>
  <td class="campo">
    <af:comboBox classNameBase="input" title="Natura Giuridica" name="codNatGiuridica" moduleName="M_GETTIPINATGIURIDICA" addBlank="true" 
      selectedValue="<%=codNatGiuridica%>"  />
  </td>
</tr>

<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td class="etichetta">Provincia</td>
  <td class="campo">
    <!--<af:comboBox classNameBase="input" title="Tipo di Ricerca" name="tipoRicerca">
      <OPTION value="S">della sede legale</OPTION>
      <OPTION value="U">di tutte le unità</OPTION>
    </af:comboBox>&nbsp; -->
    <af:comboBox classNameBase="input" title="Provincia" name="codProvincia" moduleName="M_GetIDOProvince" addBlank="true" selectedValue="<%=codProvincia%>" />
  </td>
</tr>
<tr>
    <td class="etichetta">Comune</td>
    <td class="campo">
    <af:textBox classNameBase="input" name="codComAz" title="Comune azienda" onKeyUp="PulisciRicerca(document.Frm1.codComAz, document.Frm1.codComAzHid, document.Frm1.strComAz, document.Frm1.strComAzHid, null, null, 'codice');" 
    type="text" value="<%=codComAz%>" size="4" maxlength="4" />&nbsp;
      <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComAz, document.Frm1.strComAz, null, 'codice','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
    <af:textBox type="hidden" name="codComAzHid" value="<%=codComAzHid%>"/>
    <af:textBox type="text" classNameBase="input" onKeyUp="PulisciRicerca(document.Frm1.codComAz, document.Frm1.codComAzHid, document.Frm1.strComAz, document.Frm1.strComAzHid, null, null, 'descrizione');" 
                name="strComAz"  value="<%=strComAz%>" size="30" maxlength="50" 
                 />
    <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComAz, document.Frm1.strComAz, null, 'descrizione','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>&nbsp;
    <af:textBox type="hidden"  name="strComAzHid" value="<%=strComAzHid%>"/>
    </td>
</tr>
<tr>
	<td class="etichetta">Indirizzo</td>
  <td class="campo">
    <input type="text" name="indirizzo" value="<%=indirizzo%>" size="30" maxlength="60"/>
  </td>
</tr>

<tr>
  <td class="etichetta">Sede legale</td>
  <td class="campo">
    <input type="checkbox" name="flgSedeLegale" <%=flgSedeLegale.equalsIgnoreCase("ON")?"checked":"" %>>
     <script>inputs.push(document.Frm1.Sede);</script>
  </td>
</tr>
<tr valign="top">
  <td class="etichetta">Denominazione</td>
  <td class="campo">
    <input type="text" name="strDenominazioneAz" value="<%=strDenominazioneAz%>" size="50" maxlength="100" />
  </td>
</tr>

<%-- modificato il 12/04/2006 da Anna Paola Coppola
	Aggiunta nella pagina generale la ricerca aziende anche per codice attività --%>

  <tr>
       <td class="etichetta">Codice Attività</td>
       <td class="campo">
         <af:textBox classNameBase="input" name="codAteco" title="Codice Attività" size="6" maxlength="6" 
          	value="<%=codAteco%>" validateOnPost="true" 
          	onKeyUp="PulisciRicercaCCNL(document.Frm1.codAteco, document.Frm1.codAtecoHid, document.Frm1.strTipoAteco, document.Frm1.strAteco, 'codice');"/>  
          	<af:textBox type="hidden" name="codAtecoHid" value="<%=codAteco%>" />  
          	<a href="javascript:selectATECO_onClick(document.Frm1.codAteco, document.Frm1.codAtecoHid, document.Frm1.strAteco,  document.Frm1.strTipoAteco);"><img src="../../img/binocolo.gif" alt="cerca codice"></A>&nbsp;&nbsp;
          	<A href="javascript:ricercaAvanzataAteco();"> Ricerca avanzata </A>
        </td>
      </tr> 
      
    <tr valign="top">
        <td class="etichetta">Tipo</td>
        <td class="campo">
          <af:textBox classNameBase="input" name="strTipoAteco" value="<%=strTipoAteco%>" readonly="true" size="80" maxlength="300"/>
        </td>
      </tr>

      <tr>
        <td class="etichetta">Attività</td>
        <td class="campo">
             <af:textBox classNameBase="input" name="strAteco" size="80" readonly="true" value="<%=strAteco%>" maxlength="300"/>
        </td>
      </tr>
      
<tr><td colspan="2"><div class="sezione2"/></td></tr>
<tr>
  <td class="etichetta">Centro per l'impiego</td>
  <td class="campo">
      <af:textBox classNameBase="input" type="text" name="codCPI" value="<%=codCPI%>" 
          onKeyUp="PulisciRicercaCPI(document.Frm1.codCPI, document.Frm1.codCPIHid, document.Frm1.strCPI, document.Frm1.strCPIHid, 'codice');" 
          size="10" maxlength="9"
      />&nbsp;
      <A HREF="javascript:btFindCPI_onclick(  document.Frm1.codCPI, 
                                              document.Frm1.codCPIHid, 
                                              document.Frm1.strCPI, 
                                              document.Frm1.strCPIHid, 
                                              'codice');">
          <IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
      <af:textBox type="hidden" name="codCPIHid" value="<%=codCPIHid%>" />
      <af:textBox type="text" classNameBase="input" name="strCPI" value="<%=strCPI%>"
          onKeyUp="PulisciRicercaCPI(document.Frm1.codCPI, document.Frm1.codCPIHid, document.Frm1.strCPI, document.Frm1.strCPIHid, 'descrizione');" 
          size="30" maxlength="50" 
          inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\""
      />&nbsp;
      <A HREF="javascript:btFindCPI_onclick(  document.Frm1.codCPI, 
                                              document.Frm1.codCPIHid, 
                                              document.Frm1.strCPI, 
                                              document.Frm1.strCPIHid, 
                                              'descrizione');">
          <IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
      <af:textBox type="hidden" name="strCPIHid" value="<%=strCPIHid%>" />
      <%-- campo valorizzato dalla funzione javaScript di FindComune.jsp --%>
      <%-- tolto perchè non è più richiesta la compilazione automatica del cpi --%>
<!--      <af:textBox type="hidden" name="codCPIifDOMeqRESHid" value="<%//=codCPIifDOMeqRESHid%>" /> -->
  </td>
 </tr>

<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td class="etichetta">Codice Fiscale</td>
  <td class="campo">
    <input type="text" name="cf" value="<%=cf%>" size="20" maxlength="16"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Partita IVA</td>
  <td class="campo">
    <input type="text" name="piva" value="<%=piva%>" size="20" maxlength="11"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Ragione Sociale</td>
  <td class="campo">
    <input type="text" name="RagioneSociale" value="<%=RagioneSociale%>" size="20" maxlength="100"/>
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
    <input type="submit" class="pulsanti" name="cerca" value="Cerca" onclick="cercaUnita();" />&nbsp;&nbsp;
    <input type="reset"  class="pulsanti" value="Annulla" />&nbsp;&nbsp;
    <input type="submit" class="pulsanti" name="cercatestata" value="Cerca testata" onclick="cercaTestata();" />&nbsp;&nbsp;    
  </td>
</tr> 
</table>

<input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
<input type="hidden" name="PAGE" value="IdoListaAziendePage"/>
<br/>
</af:form>

<af:form name="Frm2" method="POST" action="AdapterHTTP" >
<input type="hidden" name="PAGE" value="IdoTestataAziendaPage"/>
<input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
<%if (canInsert) { %>
<center><input class="pulsante" type="submit" name="inserisci" value="Nuova azienda"/></center>
<%}%>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
