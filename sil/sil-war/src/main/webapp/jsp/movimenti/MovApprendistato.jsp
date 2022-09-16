<!-- GESTIONE APPRENDISTATO -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import="
  com.engiweb.framework.base.*,
  java.lang.*,
  java.text.*,
  java.util.*,
  java.math.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.*,
  it.eng.sil.util.*" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
boolean actionprecconsulta = false;
%>
<%@ include file="GestioneOggettoMovimento.inc" %> 
<%
boolean disabledField = false;
boolean canModify = true;
String contesto = "";
if (serviceRequest.containsAttribute("CURRENTCONTEXT")){
  contesto = serviceRequest.getAttribute("CURRENTCONTEXT").toString();
  disabledField = contesto.equalsIgnoreCase("consulta");
  //gestione sfondo di visualizzazione
  canModify = !disabledField;
}

boolean gestioneCampi = (!disabledField && canModify);

String strCognomeTutore = "";
String strNomeTutore = "";
String strCodiceFiscaleTutore = "";
String flgTitolareTutore = "";
boolean titolareTutore = false;
String numAnniEspTutore = "";
String strLivelloTutore = "";
String codMansione = "";
String descMansioneTutore = "";
String strtipomansioneTutore = "";
String strNote = "";
String numMesiApprendistato = "";
String flgArtigiana = "";
String codTipoAzienda = "";
boolean artigiano = false;
String prgMovimentoApp = "";
String flgCambiamentiDati = "";

String codQualificaSrq = "";
String descQualificaSrq = "";

boolean cambiato = false;

String codTipoAss = StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAss");
String prgMovimento = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGMOVIMENTO");
String funzione = StringUtils.getAttributeStrNotNull(serviceRequest,"updateFunctionName");
String codRegioneProv = StringUtils.getAttributeStrNotNull(serviceRequest,"codRegioneProv");
String codTipoMov = StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoMov");

if (serviceRequest.containsAttribute("flgCambiamentiDati")){
  flgCambiamentiDati = (String)serviceRequest.getAttribute("flgCambiamentiDati");
  if (flgCambiamentiDati.length()>=1){
    String elem = flgCambiamentiDati.substring(flgCambiamentiDati.length()-1,flgCambiamentiDati.length());
    if( elem.equals("P") ){
      cambiato = true;
    }
  }
}

if(serviceRequest.containsAttribute("PRGMOVIMENTOAPP") && contesto.startsWith("valida")){
  prgMovimentoApp = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGMOVIMENTOAPP");
}
if (!disabledField){
  codTipoAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"CODTIPOAZIENDA");
}

SourceBean row = serviceRequest;
if (!canModify) {
 	row = (SourceBean)serviceResponse.getAttribute("M_MovGetApprendistato.ROWS.ROW");
 	if (row != null) {
	 	descQualificaSrq = StringUtils.getAttributeStrNotNull(row,"DESCQUALIFICASRQ");
	 	descMansioneTutore = StringUtils.getAttributeStrNotNull(row, "STRMANSIONETUTORE");
	 	strtipomansioneTutore = StringUtils.getAttributeStrNotNull(row, "STRTIPOMANSIONETUTORE");
 	}
}
else {
	if (serviceResponse.containsAttribute("M_GetDescQualificaSRQ.ROWS.ROW.STRDESCRIZIONE")) {
		descQualificaSrq = serviceResponse.getAttribute("M_GetDescQualificaSRQ.ROWS.ROW.STRDESCRIZIONE").toString();	
	}
	if (serviceResponse.containsAttribute("M_GetDescMansioneSpec.ROWS.ROW.STRDESCRIZIONE")) {
		descMansioneTutore = serviceResponse.getAttribute("M_GetDescMansioneSpec.ROWS.ROW.STRDESCRIZIONE").toString();	
	}
	if (serviceResponse.containsAttribute("M_GetDescMansioneSpec.ROWS.ROW.STRDESCRIZIONETIPO")) {
		strtipomansioneTutore = serviceResponse.getAttribute("M_GetDescMansioneSpec.ROWS.ROW.STRDESCRIZIONETIPO").toString();	
	}
}



//if ((prgMovimento != null && !prgMovimento.equals("")) || (prgMovimentoApp != null && !prgMovimentoApp.equals("")) && !cambiato){
// row = (SourceBean)serviceResponse.getAttribute("M_MovGetApprendistato.ROWS.ROW");
//}

if (row != null) {
  strCognomeTutore = StringUtils.getAttributeStrNotNull(row,"STRCOGNOMETUTORE");
  strNomeTutore = StringUtils.getAttributeStrNotNull(row,"STRNOMETUTORE");
  strCodiceFiscaleTutore = StringUtils.getAttributeStrNotNull(row,"STRCODICEFISCALETUTORE");
  flgTitolareTutore = StringUtils.getAttributeStrNotNull(row,"FLGTITOLARETUTORE");
  if (flgTitolareTutore.equals("S")) titolareTutore = true;
  if (row.getAttribute("NUMANNIESPTUTORE") != null && !row.getAttribute("NUMANNIESPTUTORE").equals("") ) {
	  numAnniEspTutore = (row.getAttribute("NUMANNIESPTUTORE")).toString();	  
  }
  strLivelloTutore = StringUtils.getAttributeStrNotNull(row,"STRLIVELLOTUTORE");
  codMansione = StringUtils.getAttributeStrNotNull(row,"CODMANSIONETUTORE");
  strNote = StringUtils.getAttributeStrNotNull(row,"STRNOTE");
  codQualificaSrq = StringUtils.getAttributeStrNotNull(row,"CODQUALIFICASRQ");
  
  flgArtigiana = StringUtils.getAttributeStrNotNull(row,"FLGARTIGIANA");
  if (flgArtigiana.equals("S")) artigiano = true;  

  if(!codTipoAzienda.equals("") && codTipoAzienda.equals("ART")){
    artigiano = true;
  }
}

if(numMesiApprendistato.equals("") && (boolean)serviceResponse.containsAttribute("M_MovGetNumDurataApprendist.rows.row.NUMMESIAPPRENDISTATO")){
	numMesiApprendistato = serviceResponse.getAttribute("M_MovGetNumDurataApprendist.ROWS.ROW.NUMMESIAPPRENDISTATO").toString();
}

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Gestione Apprendistato</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<script language="Javascript">

   function gestioneFlag(){
    <%if(titolareTutore){%>
      document.Frm1.FLGTITOLARETUTORE.value = "S";
      cambia(document.getElementById("esperienza"));
      cambia(document.getElementById("qualifica"));
      cambia(document.getElementById("tipo"));
      cambia(document.getElementById("mansioneD"));
    <%}%>
  }
	
  function notificaCambioDati(){
    <%if(canModify){%>
      <%if(!cambiato){%>
          if(document.Frm1.STRCOGNOMETUTORE.value != "<%=strCognomeTutore%>"){
          <%
            flgCambiamentiDati += "P";
            cambiato = true;
          %>
          }
      <%}%>
      <%if(!cambiato){%>
          if(document.Frm1.STRNOMETUTORE.value != "<%=strNomeTutore%>"){
          <%
            flgCambiamentiDati += "P";
            cambiato = true;
          %>
          }
      <%}%>
      <%if(!cambiato){%>
          if(document.Frm1.STRCODICEFISCALETUTORE.value != "<%=strCodiceFiscaleTutore%>"){
          <%
            flgCambiamentiDati += "P";
            cambiato = true;
          %>
          }
      <%}%>
      <%if(!cambiato){%>
          if(document.Frm1.FLGTITOLARETUTORE.value != "<%=flgTitolareTutore%>"){
          <%
            flgCambiamentiDati += "P";
            cambiato = true;
          %>
          }
      <%}%>   
      <%if(!cambiato){%>
          if(document.Frm1.NUMANNIESPTUTORE.value != "<%=numAnniEspTutore%>"){
          <%
            flgCambiamentiDati += "P";
            cambiato = true;
          %>
          }
      <%}%>          
      <%if(!cambiato){%>
          if(document.Frm1.STRLIVELLOTUTORE.value != "<%=strLivelloTutore%>"){
          <%
            flgCambiamentiDati += "P";
            cambiato = true;
          %>
          }
      <%}%>  
      <%if(!cambiato){%>
          if(document.Frm1.CODMANSIONE.value != "<%=codMansione%>"){
          <%
            flgCambiamentiDati += "P";
            cambiato = true;
          %>
          }
      <%}%>   
      <%if(!cambiato){%>
          if(document.Frm1.STRNOTE.value != "<%=strNote%>"){
          <%
            flgCambiamentiDati += "P";
            cambiato = true;
          %>
          }
      <%}%>   
      <%if(!cambiato){%>
          if(document.Frm1.CODQUALIFICASRQ.value != "<%=codQualificaSrq%>"){
          <%
            flgCambiamentiDati += "P";
            cambiato = true;
          %>
          }
      <%}%>
      <%if(!cambiato){%>
          if(document.Frm1.DESCQUALIFICASRQ.value != "<%=descQualificaSrq%>"){
          <%
            flgCambiamentiDati += "P";
            cambiato = true;
          %>
          }
      <%}%>   
      <%if(!cambiato){%>
          if(document.Frm1.FLGARTIGIANA.value != "<%=flgArtigiana%>"){
          <%
            flgCambiamentiDati += "P";
            cambiato = true;
          %>
          }
      <%}%>   
      <%if(!cambiato){%>
          if(document.Frm1.STRCOGNOMETUTORE.value != "<%=codTipoAzienda%>"){
          <%
            flgCambiamentiDati += "P";
            cambiato = true;
          %>
          }
      <%}%>   
        document.Frm1.flgCambiamentiDati.value="<%=flgCambiamentiDati%>";
    <%}%>
  }
  
  //ricerca qualifica SRQ con pulsante di lookup
  function selectQualificaSRQOnClick(codQualificaSrq, descQualificaSrq, tipoRicerca) {
	if (tipoRicerca == 'codice') {
		if (codQualificaSrq.value == "") {
			descQualificaSrq.value = "";	
		}
		else {
			window.open("AdapterHTTP?PAGE=RicercaQualificaSRQPage&tipoRicerca=codice&CODQUALIFICASRQ="+codQualificaSrq.value, "Ricerca", 'toolbar=0, scrollbars=1');
		}
	}
	else {
		if (tipoRicerca == 'descrizione') {
			if (descQualificaSrq.value != "") {
				window.open("AdapterHTTP?PAGE=RicercaQualificaSRQPage&tipoRicerca=descrizione&descQualificaSrq="+descQualificaSrq.value, "Ricerca", 'toolbar=0, scrollbars=1');
			}
		}
	} 
  }

  function ricercaAvanzataQualificaSRQ() {
  		var t="AdapterHTTP?PAGE=RicercaQualificaSRQAvanzataPage";
		window.open(t, "Ricerca", 'toolbar=0, scrollbars=1');
  }

  	function selectMansione_onClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {
		flgFrequente=Frm1.flgFrequente;
	    paramFrequente=(flgFrequente.checked)?"&flgFrequente=true":"";
	    if (codMansione.value==""){

	      descMansione.value="";
	      strTipoMansione.value="";      
	    }
	    else if (codMansione.value!=codMansioneHid.value) {
	        window.open("AdapterHTTP?PAGE=RicercaMansionePage&codMansione="+codMansione.value+paramFrequente, "Mansioni", 'toolbar=0, scrollbars=1');     
	    }
	}

	function ricercaAvanzataMansioni() {
	  	var w=800; var l=((screen.availWidth)-w)/2;
	  	var h=500; var t=((screen.availHeight)-h)/2;
	  	var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;
	  	window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage", "Mansioni", feat);
	}
	
</script>
<%@ include file="../movimenti/common/include/GestioneApprendistato.inc" %>
<script type="text/javascript" src="../../js/movimenti/common/GestioneApprendistatoFunc.js" language="JavaScript"></script>
</head>

<body class="gestione" onload="gestioneFlag();">
<br/>
<p class="titolo">Gestione apprendistato</p>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<table class="main" cellpadding="0" border="0" cellspacing="0">
  <tr>
    <td colspan="4">          
      <div class='sezione2' id='SedeAzienda'>
        Dati Tutore
      </div>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Cognome</td>
    <td class="Campo" nowrap>
      <af:textBox 
        classNameBase="input" 
        name="STRCOGNOMETUTORE" 
        size="40" 
        maxlength="40" 
        value="<%=strCognomeTutore%>" 
        readonly="<%= String.valueOf(!gestioneCampi) %>"
        required="false" 
        onBlur="javascript:notificaCambioDati();" />
    </td>
    <td class="etichetta">Nome</td>
    <td class="Campo" nowrap>
       <af:textBox 
        classNameBase="input" 
        name="STRNOMETUTORE" 
        size="40" 
        maxlength="40" 
        value="<%=strNomeTutore%>" 
        readonly="<%= String.valueOf(!gestioneCampi) %>"
        required="false"
        onBlur="javascript:notificaCambioDati();" />
    </td>
  </tr>
  <tr>
    <td class="etichetta">Codice Fiscale</td>
    <td colspan="3" class="Campo">
       <af:textBox 
        classNameBase="input" 
        name="STRCODICEFISCALETUTORE" 
        size="20" 
        maxlength="16" 
        value="<%=strCodiceFiscaleTutore%>" 
        readonly="<%= String.valueOf(!gestioneCampi) %>"
        required="false" 
        onBlur="javascript:notificaCambioDati();" />
    </td>
  </tr>
  <tr>
    <td class="etichetta">Titolare Impresa</td>
    <td colspan="3" class="campo">
      <%if (titolareTutore){%>
        <input type="checkbox" name="FLGTITOLARETUTORE" value="S" onClick="javascript:impostaValoreCheck(this);notificaCambioDati();" <% if(!gestioneCampi) out.print("disabled"); %> checked />
      <%}else{%>
          <input type="checkbox" name="FLGTITOLARETUTORE" value="N" onClick="javascript:impostaValoreCheck(this);notificaCambioDati();" <% if(!gestioneCampi) out.print("disabled"); %> />
      <%}%>
    </td>
  </tr>

  <tr id="esperienza" style="display:'inline'">
    <td class="etichetta">Anni esperienza</td>
    <td class="Campo">
       <af:textBox 
        classNameBase="input" 
        name="NUMANNIESPTUTORE" 
        size="8" 
        maxlength="38" 
        value="<%=numAnniEspTutore%>" 
        readonly="<%= String.valueOf(!gestioneCampi) %>"
        onBlur="javascript:notificaCambioDati();" />
    </td>
    <td class="etichetta">Livello</td>
    <td class="Campo">
       <af:textBox 
        classNameBase="input" 
        name="STRLIVELLOTUTORE" 
        size="8" 
        maxlength="8" 
        value="<%=strLivelloTutore%>" 
        readonly="<%= String.valueOf(!gestioneCampi) %>"
        onBlur="javascript:notificaCambioDati();" />
    </td>
  </tr>
  <tr id="qualifica" style="display:''">
    <td class="etichetta" nowrap>Codice mansione</td>
    <td class="campo">
      <af:textBox 
        classNameBase="input" 
        name="CODMANSIONE" 
        size="7" 
        maxlength="7" 
        value="<%=codMansione%>" 
        readonly="<%= String.valueOf(!gestioneCampi) %>" 
        onBlur="javascript:notificaCambioDati();" />
      
      <af:textBox 
        type="hidden" 
        name="codMansioneHid" 
        value=""/>
      
      <% if (canModify) { %>
          <a href="javascript:selectMansione_onClick(Frm1.CODMANSIONE, Frm1.codMansioneHid, Frm1.DESCMANSIONE,  Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
          <A href="javascript:ricercaAvanzataMansioni();">
              Ricerca avanzata
          </A>
      <%}%>
    </td>
  </tr>

  <tr valign="middle" id="tipo" style="display:'inline'">
    <td class="etichetta">Tipo</td>
    <td colspan="3" class="campo">
      <af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
      <af:textBox classNameBase="input" name="strTipoMansione" value="<%=strtipomansioneTutore%>" readonly="true" size="48" maxlength="100"/>
    </td>
  </tr>
  <tr id="mansioneD" style="display:'inline'">
    <td class="etichetta" >Descrizione</td>
    <td class="campo" colspan="3">
        <af:textArea cols="50" 
                     rows="4" 
                     title="Mansione"
                     name="DESCMANSIONE" 
                     classNameBase="textarea"
                     readonly="true" 
                     required="false"
                     maxlength="100"
                     value="<%=descMansioneTutore%>" />
    </td>
  </tr>
  
   <tr>
    <td colspan="4">          
      <div class='sezione2' id='SedeAzienda'>
        Altri Dati
      </div>
    </td>
  </tr>
  <tr>
	<td class="etichetta">Qualifica SRQ</td>
	<td class="campo" colspan="3">
	<af:textBox classNameBase="input" 
				title="Qualifica SRQ" 
				name="CODQUALIFICASRQ" size="7" maxlength="4" 
				value="<%=codQualificaSrq%>" readonly="<%=String.valueOf(!gestioneCampi)%>"/>
				<%if (canModify) {%>   
					<a href="javascript:selectQualificaSRQOnClick(document.Frm1.CODQUALIFICASRQ, document.Frm1.DESCQUALIFICASRQ, 'codice');">
					<img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;
				<%}%>
				<af:textBox classNameBase="input" type="text" size="60" name="DESCQUALIFICASRQ" value="<%=descQualificaSrq%>" readonly="<%=String.valueOf(!canModify)%>"/>
    			<%if (canModify) {%> 
					<a href="javascript:selectQualificaSRQOnClick(document.Frm1.CODQUALIFICASRQ, document.Frm1.DESCQUALIFICASRQ, 'descrizione');">
					<img src="../../img/binocolo.gif" alt="Cerca per descrizione">
				</a>
				<a href="javascript:ricercaAvanzataQualificaSRQ();">Ricerca avanzata</A>
				<%}%>
	</td>
  </tr>
  <tr>
    <td class="etichetta" nowrap>Impresa artigiana</td>
    <td colspan="3" class="campo">
      <% if(artigiano){ %>    
          <input type="checkbox" name="FLGARTIGIANA" value="S" onClick="javascript:impostaValoreCheck(this);notificaCambioDati();" <% if(!gestioneCampi) out.print("disabled"); %> checked />
      <%}else{%>
          <input type="checkbox" name="FLGARTIGIANA" value="N" onClick="javascript:impostaValoreCheck(this);notificaCambioDati();" <% if(!gestioneCampi) out.print("disabled"); %> />
      <%}%>
    </td>
  </tr>
   <tr>
    <td class="etichetta">Note</td>
    <td class="campo" colspan="3">
        <af:textArea cols="50" 
                     rows="4" 
                     title="Mansione"
                     name="STRNOTE" 
                     classNameBase="textarea"
                     readonly="<%= String.valueOf(!gestioneCampi) %>" 
                     maxlength="1000"
                     value="<%=strNote%>"
                     onBlur="javascript:notificaCambioDati();" />
    </td>
  </tr>
  <tr><td><br/></td></tr>
  <tr>
    <%if(gestioneCampi){%>
      <td colspan="2" align="right">    
      <input type="button" class="pulsante" name="confermaApprendistato" value="Avanti" onclick="javascript:aggiorna();">
      </td>    
      <td colspan="2" align="left">
        <input type="button" class="pulsante" name="chiudiApprendistato" value="Chiudi" onclick="javascript:window.close();">
      </td>
    <%} else {%>
          <td colspan="4" align="center">
            <input type="button" class="pulsante" name="chiudiApprendistato" value="Chiudi" onclick="javascript:window.close();">
          </td>
      <%}%>
  </tr>
  <tr style="display: none;">
    <td>
      <input type="checkbox" name="flgFrequente" value="" />    
    </td>
  </tr>
</table>

<!--<input type="hidden" name="PAGE" value="MovApprendistatoRefreshPage">-->
<input type="hidden" name="FUNZ_AGG" value="<%=funzione%>">
<input type="hidden" name="PRGMOVIMENTO" value="<%=prgMovimento%>">
<input type="hidden" name="flgCambiamentiDati" value="<%=flgCambiamentiDati%>">
</af:form>
<%out.print(htmlStreamBottom);%>

</body>
</html>