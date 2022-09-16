<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import="
  com.engiweb.framework.base.*,
  java.lang.*,
  java.text.*,
  java.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.module.movimenti.constant.Properties,
  it.eng.sil.security.*,
  it.eng.sil.util.*,
  it.eng.sil.util.Sottosistema" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>      

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<%@ include file="../anag/Function_CommonRicercaCPI.inc" %>
<%@ include file="Mansioni_Ido_CommonScripts.inc" %>
<%
 // NOTE: Attributi della pagina (pulsanti e link) 
//  PageAttribs attributi = new PageAttribs(user, "AnagMainPage");
//  boolean canModify = attributi.containsButton("nuovo");

  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  PageAttribs attributi = new PageAttribs(user, "IdoRichiestaRicercaPage");
  boolean canInsert=attributi.containsButton("INSERISCI");
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  String prgRichiestaAz = StringUtils.getAttributeStrNotNull(serviceRequest,"prgRichiestaAz");
  String anno = StringUtils.getAttributeStrNotNull(serviceRequest,"anno");
  String datRichiestaDal = StringUtils.getAttributeStrNotNull(serviceRequest,"datRichiestaDal");
  String datRichiestaAl  = StringUtils.getAttributeStrNotNull(serviceRequest,"datRichiestaAl");
  String datScadenzaDal = StringUtils.getAttributeStrNotNull(serviceRequest,"datScadenzaDal");
  String datScadenzaAl = StringUtils.getAttributeStrNotNull(serviceRequest,"datScadenzaAl");
  String codTipoAzienda= StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAzienda");
  String cf= StringUtils.getAttributeStrNotNull(serviceRequest,"cf");
  String piva = StringUtils.getAttributeStrNotNull(serviceRequest,"piva");
  String RagioneSociale = StringUtils.getAttributeStrNotNull(serviceRequest,"RagioneSociale");
  String Indirizzo = StringUtils.getAttributeStrNotNull(serviceRequest,"Indirizzo");
  String codCom = StringUtils.getAttributeStrNotNull(serviceRequest,"codCom");
  String codComHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codComHid");
  String desComune = StringUtils.getAttributeStrNotNull(serviceRequest,"desComune");
  String desComuneHid = StringUtils.getAttributeStrNotNull(serviceRequest,"desComuneHid");
  String codCPI = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPI");
  String codCPIHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIHid");
  String strCPI = StringUtils.getAttributeStrNotNull(serviceRequest,"strCPI");
  String strCPIHid = StringUtils.getAttributeStrNotNull(serviceRequest,"strCPIHid");
  String codCPIifDOMeqRESHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIifDOMeqRESHid");
  String CODMANSIONE = StringUtils.getAttributeStrNotNull(serviceRequest,"CODMANSIONE");
  String CODMANSIONEHid = StringUtils.getAttributeStrNotNull(serviceRequest,"CODMANSIONEHid");
  String CODTIPOMANSIONE = StringUtils.getAttributeStrNotNull(serviceRequest,"CODTIPOMANSIONE");
  String strTipoMansione = StringUtils.getAttributeStrNotNull(serviceRequest,"strTipoMansione");
  String DESCMANSIONE = StringUtils.getAttributeStrNotNull(serviceRequest,"DESCMANSIONE");
  String codMonoStatoRich = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoStatoRich");
  String cdnStatoRich = StringUtils.getAttributeStrNotNull(serviceRequest,"cdnStatoRich");
  String utente = StringUtils.getAttributeStrNotNull(serviceRequest,"utente");
  String codMonoTipoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoTipoGrad");
  String codMonoCMcategoria = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoCMcategoria");
  String flagCresco = StringUtils.getAttributeStrNotNull(serviceRequest, "flagCresco");
  
  if (utente==""){   
    utente="1";
  }
  boolean CRESCO = false;
  String numConfigCresco = serviceResponse.containsAttribute("M_CONFIG_CRESCO.ROWS.ROW.NUM")?
  	serviceResponse.getAttribute("M_CONFIG_CRESCO.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;
  if(Properties.CUSTOM_CONFIG.equalsIgnoreCase(numConfigCresco)){
  	CRESCO = true;
  } 
%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Cerca richiesta</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<script language="Javascript">
<% 
//Genera il Javascript che si occuperà di inserire i links nel footer
  attributi.showHyperLinks(out, requestContainer,responseContainer,"");
%>
</script>

<script language="Javascript">
  
  var flagRicercaPage = "S";
  
  function resetFieldsAzienda(){
    document.Frm1.prgAzienda.value = "";
    document.Frm1.prgUnita.value = "";
    document.Frm1.codTipoAzienda.selectedIndex = 0;
    document.Frm1.cf.value = "";
    document.Frm1.piva.value = "";
    document.Frm1.RagioneSociale.value = "";
    document.Frm1.Indirizzo.value = "";
    document.Frm1.codCom.value = "";
    document.Frm1.desComune.value = "";
  }
  
  function enableFieldsAzienda(){

    document.Frm1.codTipoAzienda.disabled = false;
    document.Frm1.codTipoAzienda.className = "input";
    
    document.Frm1.cf.disabled = false;
    document.Frm1.cf.className = "input";

    document.Frm1.piva.disabled = false;
    document.Frm1.piva.className = "input";

    document.Frm1.RagioneSociale.disabled = false;
    document.Frm1.RagioneSociale.className = "input";

    document.Frm1.Indirizzo.disabled = false;
    document.Frm1.Indirizzo.className = "input";

    document.Frm1.codCom.disabled = false;
    document.Frm1.codCom.className = "input";

    document.Frm1.desComune.disabled = false;
    document.Frm1.desComune.className = "input";
  }

  function disableFieldsAzienda(){

      document.Frm1.codTipoAzienda.disabled = true;
      document.Frm1.codTipoAzienda.className = "inputView";
    
      document.Frm1.cf.disabled = true;
      document.Frm1.cf.className = "inputView";

      document.Frm1.piva.disabled = true;
      document.Frm1.piva.className = "inputView";

      document.Frm1.RagioneSociale.disabled = true;
      document.Frm1.RagioneSociale.className = "inputView";

      document.Frm1.Indirizzo.disabled = true;
      document.Frm1.Indirizzo.className = "inputView";

      document.Frm1.codCom.disabled = true;
      document.Frm1.codCom.className = "inputView";

      document.Frm1.desComune.disabled = true;
      document.Frm1.desComune.className = "inputView";
  }
  
  function btFindAzienda_onClick(TipoAzienda,CodiceFiscale,PIva,RagioneSociale,Indirizzo,CodComune,DesComune){
    //disableFieldsAzienda();

	if ((RagioneSociale.value.length > 0 ) || (Indirizzo.value.length > 0) ||
            (CodiceFiscale.value.length > 0) || (PIva.value.length > 0) ||
            (CodComune.value.length > 0) ) {
    
		    var s= "AdapterHTTP?PAGE=RicercaAziendaPage";
		    s = s + "&CodTipoAzienda="+TipoAzienda.value;
		    s = s + "&CodiceFiscale="+CodiceFiscale.value;
		    s = s + "&piva="+PIva.value;
		    s = s + "&RagioneSociale="+RagioneSociale.value;
		    s = s + "&Indirizzo="+Indirizzo.value;
		    s = s + "&CodComune="+CodComune.value;
		    s = s + "&DesComune="+DesComune.value;    
		    window.open(s,"Azienda", 'toolbar=0, scrollbars=1');
	} else {
		 alert("Inserire almeno uno dei seguenti campi nella ricerca:\n\tComune\n\tIndirizzo\n\tCodice Fiscale\n\tPartita IVA\n\tRagione Sociale\n");	
	}
  }

  function annullaRicerca(){
    resetFieldsAzienda();
    enableFieldsAzienda();
  }
  
  function codComuneUpperCase(inputName){
    var ctrlObj = eval("document.forms[0]." + inputName);
    eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
    return true;
  }
  
  function impostaParAzienda()
  {
	 // Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;

    var url = "AdapterHTTP?PAGE=IdoTestataRichiestaPage";
        url += "&cdnFunzione=" + <%= _funzione %>;
        url += "&prgAzienda=" + document.Frm1.prgAzienda.value;
        url += "&prgUnita=" + document.Frm1.prgUnita.value;
        url += "&inserisci=Nuova richiesta";
    setWindowLocation(url);
  }  

	function fieldChanged() {} // lasciata per compatibilità
	function enableFields(){} // lasciata per compatibilità
	function CaricaRiferimenti(ArrayRif){} // lasciata per compatibilità

	function valorizzaHidden() {
		<%
	  	// INIT-PARTE-TEMP
		if (Sottosistema.AS.isOff()) {	
		%>
		return true;
		<%
		} else {
		// END-PARTE-TEMP
				String codEvasione = StringUtils.getAttributeStrNotNull(serviceRequest,"codEvasione");
	  			String dataChiam = StringUtils.getAttributeStrNotNull(serviceRequest,"dataChiam");	 
		%>		 
		  document.Frm1.evas.value = document.Frm1.codEvasione.options[document.Frm1.codEvasione.selectedIndex].text;
		  document.Frm1.statoev.value = document.Frm1.cdnStatoRich.options[document.Frm1.cdnStatoRich.selectedIndex].text;
		  document.Frm1.statorich.value = document.Frm1.codMonoStatoRich.options[document.Frm1.codMonoStatoRich.selectedIndex].text;
		  return true;		  
	    <% 
		// INIT-PARTE-TEMP
		}
	   	// END-PARTE-TEMP
		%>
	}  
</script>
</head>

<body class="gestione" onload="rinfresca();">
<p class="titolo">Ricerca richieste</p>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="valorizzaHidden()" >
<input type="hidden" name="prgAzienda"  />
<input type="hidden" name="prgUnita"  />
<p align="center">
<table class="main"> 
<tr>
  <td class="etichetta">Numero richiesta</td>
  <td class="campo">
    <af:textBox name="prgRichiestaAz" value="<%=prgRichiestaAz%>" size="12" maxlength="10"/>&nbsp;&nbsp;
    Anno
    <af:textBox name="anno" type="integer" value="<%=anno%>" validateOnPost="true" size="4" maxlength="4"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Dal giorno</td>
  <td class="campo">
    <af:textBox type="date" name="datRichiestaDal" title="Data richiesta" value="<%=datRichiestaDal%>" size="12" maxlength="10" validateOnPost="true"/>&nbsp;&nbsp;
    Al giorno
    <af:textBox type="date" name="datRichiestaAl" title="Data richiesta" value="<%=datRichiestaAl%>" size="12" maxlength="10" validateOnPost="true" />
  </td>
</tr>
<tr>
    <td class="etichetta">Data scadenza dal giorno</td>
    <td class="campo">
      <af:textBox type="date" name="datScadenzaDal" title="Data scadenza" value="<%=datScadenzaDal%>" size="12" maxlength="10" validateOnPost="true"/>&nbsp;&nbsp;
      Al giorno
      <af:textBox type="date" name="datScadenzaAl" title="Data scadenza" value="<%=datScadenzaAl%>" size="12" maxlength="10" validateOnPost="true" />
    </td>
  </tr>
  <tr><td colspan="2"><hr width="90%"/></td></tr>
  <tr>
    <td class="etichetta">Tipo Azienda</td>
    <td class="campo">
      <af:comboBox classNameBase="input"
                  title="Tipo Azienda"
                  name="codTipoAzienda"
                  moduleName="M_GetTipiAzienda"
                  selectedValue="<%= codTipoAzienda %>" 
                  addBlank="true" />
    </td>
  </tr>
  <tr>
    <td class="etichetta">Codice Fiscale</td>
    <td class="campo" colspan="3">
      <input type="text" name="cf" value="<%=cf%>" size="20" maxlength="16"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Partita IVA</td>
    <td class="campo" colspan="3">
      <input type="text" name="piva" value="<%=piva%>" size="20" maxlength="11"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Ragione Sociale</td>
    <td class="campo" colspan="3">
      <input type="text" name="RagioneSociale" value="<%=RagioneSociale%>" size="20" maxlength="100"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Indirizzo</td>
    <td class="campo" colspan="3">
      <input type="text" name="Indirizzo" value="<%=Indirizzo%>" size="20" maxlength="100"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Comune</td>
    <td class="campo">
      <INPUT type="text" name="codCom" size="4" value="<%=codCom%>" maxlength="4" class="inputEdit" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.desComune, document.Frm1.desComuneHid, null, null, 'codice');"/>
      <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codCom, document.Frm1.desComune, null, 'codice','comuni');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
      <INPUT type="HIDDEN" name="codComHid" size="20" value="<%=codComHid%>"/>
      <INPUT type="text" name="desComune" size="30" value="<%=desComune%>" maxlength="50" class="inputEdit" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.desComune, document.Frm1.desComuneHid, null, null, 'descrizione');" onkeypress="if(event.keyCode==13) { event.keyCode=9; this.blur(); }"/>
      <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codCom, document.Frm1.desComune, null, 'descrizione','comuni');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
      <INPUT type="HIDDEN" name="desComuneHid" size="20" value="<%=desComuneHid%>"/>
   </td>                
</tr>
<tr>
  <td colspan="2" align="center">
    <input class="pulsante" type="button" name="ricercaAzienda" value="Ricerca azienda" onclick="btFindAzienda_onClick(Frm1.codTipoAzienda,Frm1.cf,Frm1.piva,Frm1.RagioneSociale,Frm1.Indirizzo,Frm1.codCom,Frm1.desComune)"/>
    <input class="pulsante" type="button" name="annullaRicercaAzienda" value="Cancella" onclick="annullaRicerca()"/>
  </td>
</tr>
    
<tr><td colspan="2"><hr width="90%"/></td></tr>
<tr>
  <td class="etichetta">Centro per l'impiego</td>
  <td class="campo">
      <af:textBox classNameBase="input" type="text" name="codCPI" value="<%=codCPI%>" 
          onKeyUp="fieldChanged();PulisciRicercaCPI(document.Frm1.codCPI, document.Frm1.codCPIHid, document.Frm1.strCPI, document.Frm1.strCPIHid, 'codice');" 
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
          onKeyUp="fieldChanged();PulisciRicercaCPI(document.Frm1.codCPI, document.Frm1.codCPIHid, document.Frm1.strCPI, document.Frm1.strCPIHid, 'descrizione');" 
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
      <af:textBox type="hidden" name="codCPIifDOMeqRESHid" value="<%=codCPIifDOMeqRESHid%>" />
  </td>
 </tr>
 <tr>
  <td class="etichetta">Codice mansione</td>
  <td class="campo">
    <af:textBox 
      classNameBase="input" 
      name="CODMANSIONE" 
      size="7" 
      maxlength="7"    
      value="<%=CODMANSIONE%>"
    />
      
    <af:textBox 
      type="hidden" 
      name="codMansioneHid" 
      value="<%=CODMANSIONEHid%>"
    />
      
    <a href="javascript:selectMansione_onClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
    <A href="javascript:ricercaAvanzataMansioni();">Ricerca avanzata</A>
  </td>
</tr>           
<tr valign="top">
    <td class="etichetta">Tipo</td>
    <td class="campo">
      <af:textBox type="hidden" name="CODTIPOMANSIONE" value="<%=CODTIPOMANSIONE%>" />
      <af:textBox classNameBase="input" name="strTipoMansione" value="<%=strTipoMansione%>" readonly="true" size="48" />
    </td>
  </tr>
<tr>
  <td class="etichetta">Mansione</td>
  <td class="campo">
      <af:textArea cols="60" 
                   rows="2" 
                   title="Mansione"
                   name="DESCMANSIONE" 
                   classNameBase="textarea"
                   readonly="true" 
                   maxlength="100"
                   value="<%=DESCMANSIONE%>" />
  </td>
</tr>
<tr>
  <td class="etichetta">Stato richiesta</td>
  <td class="campo" >
    <af:comboBox
      classNameBase="input"
      onChange="fieldChanged();"
      name="codMonoStatoRich"
      moduleName="M_StatoRichAz"
      selectedValue="<%=codMonoStatoRich%>"
      addBlank="true"/>&nbsp;&nbsp;
      Stato evasione
          <af:comboBox
      classNameBase="input"
      onChange="fieldChanged();"
      name="cdnStatoRich"
      moduleName="M_StatoEvRichAz"
      selectedValue="<%=cdnStatoRich%>"
      addBlank="true"/>
  </td>
</tr>
<%
  	// INIT-PARTE-TEMP
	if (Sottosistema.AS.isOff()) {	

	} else {
	// END-PARTE-TEMP
			String codEvasione = StringUtils.getAttributeStrNotNull(serviceRequest,"codEvasione");
  			String dataChiam = StringUtils.getAttributeStrNotNull(serviceRequest,"dataChiam");
 
%>
<tr>
  <td class="etichetta">Modalità di evasione</td>
  <td class="campo" >
    <af:comboBox
      classNameBase="input"
      onChange="fieldChanged();"
      name="codEvasione"
      moduleName="M_EvasioneRichAz"
      selectedValue="<%=codEvasione%>"
      addBlank="true"/>&nbsp;&nbsp;           
      Data chiamata
 	 <af:textBox name="dataChiam" title="Data"
              type="date"
              size="10"
              maxlength="10"
              required="false"
              validateOnPost="true"
              disabled="false"
              value="<%=dataChiam%>"/>
  </td> 
</tr>
<tr>
  <td class="etichetta">Tipo CM</td>
  <td class="campo" >
	<af:comboBox name="codMonoTipoGrad" classNameBase="input" disabled="false" onChange="fieldChanged();">	  	
	   	<option value=""  <% if ( "".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %> ></option>            
	   	<option value="D" <% if ( "D".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %>>Avviamento numerico art.8</option>
	   	<option value="A" <% if ( "A".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %>>Avviamento numerico art.18</option> 
	   	<option value="G" <% if ( "G".equalsIgnoreCase(codMonoTipoGrad) )  { %>SELECTED="true"<% } %>>Graduatoria art.1</option>               					        		
	</af:comboBox>
  </td> 
</tr>
<tr>
  <td class="etichetta">Categoria CM</td>
  <td class="campo" >
	<af:comboBox name="codMonoCMcategoria" classNameBase="input" disabled="false" onChange="fieldChanged();">	  	
	   	<option value=""  <% if ( "".equalsIgnoreCase(codMonoCMcategoria) )  { %>SELECTED="true"<% } %> ></option>            
	   	<option value="D" <% if ( "D".equalsIgnoreCase(codMonoCMcategoria) )  { %>SELECTED="true"<% } %>>Disabili</option>
	   	<option value="A" <% if ( "A".equalsIgnoreCase(codMonoCMcategoria) )  { %>SELECTED="true"<% } %>>Categoria protetta ex. Art. 18</option> 
	   	<option value="E" <% if ( "E".equalsIgnoreCase(codMonoCMcategoria) )  { %>SELECTED="true"<% } %>>Entrambi</option>               					        		
	</af:comboBox>
  </td> 
</tr>
<%if(CRESCO){ %>
<tr>
	<td class="etichetta">Cresco</td>
	<td class="campo">
		<input type="checkbox" name="FLAGCRESCO"
			<%=(flagCresco != null && flagCresco.equals("on")) ? "checked='checked'" : ""%> />
	</td>
</tr>
<%} %>
<input type="hidden" name="evas" value="" />
<input type="hidden" name="statoev"  value="" />
<input type="hidden" name="statorich"  value="" />

<% 
	// INIT-PARTE-TEMP
	}
   	// END-PARTE-TEMP
%>
<!-- questa porzione di codice è aggiunta con l'utilizzo degli interruttori e riguarda AS -->

<!-- fine del codice aggiunto con gli interruttori -->

<tr ><td colspan="2" ><hr width="90%"/></td></tr>      

<tr>
	<td class="etichetta">Stato invio a Cliclavoro</td>
  	<td class="campo" >
		<af:comboBox name="codStatoInvioVacancy" classNameBase="input" disabled="false">	  	
		   	<option value=""  SELECTED="true" ></option>            
		   	<option value="INVIATE" >Solo le richieste inviate</option>
		   	<option value="IN_ATTESA_INVIO" >Solo le richieste in attesa d'invio</option> 
		</af:comboBox>
  	</td>
</tr>


<tr ><td colspan="2" ><hr width="90%"/></td></tr>
<tr >
    <td class="etichetta">Utente di inserimento</td>
	<td class="campo" >
	    <input type="radio" name="utente" value="1" <%if(utente.equals("1")){out.print("checked");}%>> Mie
	    <input type="radio" name="utente" value="2" <%if(utente.equals("2")){out.print("checked");}%>> Mio gruppo
	    <input type="radio" name="utente" value=""  <%if(utente.equals("")){out.print("checked");}%>> Tutte
	</td>
</tr>      
</table>

<table class="main">
  <%--tr><td colspan="2">&nbsp;</td></tr--%>
  <tr>
    <td colspan="2" align="center">
      <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>&nbsp;&nbsp;
      <input type="reset" class="pulsanti" value="Annulla" />
    </td>
  </tr>
</table>
 <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
 <input type="hidden" name="PAGE" value="IdoListaRichiestePage"/>
 <br/>
</af:form>
<af:form name="Frm2" method="POST" action="AdapterHTTP" >
<input type="hidden" name="PAGE" value="IdoTestataRichiestaPage"/>
<input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
<% if (canInsert) { %>
<center><input class="pulsante" type="button" onclick="impostaParAzienda()" name="inserisci" value="Nuova richiesta"/></center>
<% } %>
</af:form>
<%out.print(htmlStreamBottom);%>

</body>
</html>

