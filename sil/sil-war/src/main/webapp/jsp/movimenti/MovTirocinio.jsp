<!-- GESTIONE Tirocinio -->
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
String configuarazioneTir = "0";
if (serviceResponse.containsAttribute("M_GetConfig_Tirocinio.ROWS.ROW.NUM")) {
	configuarazioneTir = serviceResponse.getAttribute("M_GetConfig_Tirocinio.ROWS.ROW.NUM").toString();		
}

String strCognomeTutore = "";
String strNomeTutore = "";
String strCodiceFiscaleTutore = "";
String strCodFiscPromotoreTir = "";
String codSoggPromotoreMin = "";
String codTipoEntePromotore = "";
String strDenominazioneTir = "";
String codCategoriaTir = "";
String codTipologiaTir = "";
String prgMovimentoApp = "";
String prgMovimento = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGMOVIMENTO");
String funzione = StringUtils.getAttributeStrNotNull(serviceRequest,"updateFunctionName");
String codQualificaSrq = "";
String descQualificaSrq = "";
if(serviceRequest.containsAttribute("PRGMOVIMENTOAPP") && contesto.equalsIgnoreCase("valida")){
  prgMovimentoApp = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGMOVIMENTOAPP");
}


SourceBean row = serviceRequest;
if (!canModify) {
 	row = (SourceBean)serviceResponse.getAttribute("M_MovGetApprendistato.ROWS.ROW");
}

if (row != null){
  	strCognomeTutore = StringUtils.getAttributeStrNotNull(row,"STRCOGNOMETUTORE");
  	strNomeTutore = StringUtils.getAttributeStrNotNull(row,"STRNOMETUTORE");
  	strCodiceFiscaleTutore = StringUtils.getAttributeStrNotNull(row,"STRCODICEFISCALETUTORE");
  	strCodFiscPromotoreTir = StringUtils.getAttributeStrNotNull(row,"STRCODFISCPROMOTORETIR");
  	codSoggPromotoreMin = StringUtils.getAttributeStrNotNull(row,"CODSOGGPROMOTOREMIN");
  	codTipoEntePromotore = StringUtils.getAttributeStrNotNull(row,"CODTIPOENTEPROMOTORE");
  	strDenominazioneTir = StringUtils.getAttributeStrNotNull(row,"STRDENOMINAZIONETIR");
  	codCategoriaTir = StringUtils.getAttributeStrNotNull(row,"CODCATEGORIATIR");
  	codTipologiaTir = StringUtils.getAttributeStrNotNull(row,"CODTIPOLOGIATIR");
  	if (configuarazioneTir.equals("1")) {
  		codQualificaSrq = StringUtils.getAttributeStrNotNull(row,"CODQUALIFICASRQ");
  		if (!canModify) {
  			descQualificaSrq = StringUtils.getAttributeStrNotNull(row,"DESCQUALIFICASRQ");	
  		}
  		else {
  			descQualificaSrq = serviceResponse.containsAttribute("M_GetDescQualificaSRQ.ROWS.ROW.STRDESCRIZIONE")?
  	  				serviceResponse.getAttribute("M_GetDescQualificaSRQ.ROWS.ROW.STRDESCRIZIONE").toString():"";	
  		}
  	} 	
}
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Gestione Tirocinio</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<script language="Javascript">  
  	function aggiorna() {
  		window.opener.<%=funzione%>();
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

</script>
</head>

<body class="gestione">
<br/>
<p class="titolo">Gestione Tirocinio</p>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<table class="main" cellpadding="0" border="0" cellspacing="0">
  <tr>
    <td colspan="4">          
      <div class="sezione2">
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
        required="false" />
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
        required="false" />
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
        required="false" />
    </td>
  </tr>
  
    <tr>
    <td colspan="4">          
      <div class="sezione2">
        Dati Promotore Tirocinio
      </div>
    </td>
  </tr>
  
  <tr>
    <td class="etichetta">C.F. Promotore Tirocinio</td>
    <td class="Campo">
       <af:textBox 
        classNameBase="input" 
        name="STRCODFISCPROMOTORETIR" 
        size="20" 
        maxlength="16" 
        value="<%=strCodFiscPromotoreTir%>" 
        readonly="<%= String.valueOf(!gestioneCampi) %>"
        required="true" />
    </td>
    <td class="etichetta">Denominazione</td>
    <td class="Campo">
       <af:textBox 
        classNameBase="input" 
        name="STRDENOMINAZIONETIR" 
        size="35" 
        maxlength="100" 
        value="<%=strDenominazioneTir%>" 
        readonly="<%= String.valueOf(!gestioneCampi) %>"
        required="true" />
    </td>
  </tr>
  
  	<tr>
  		<td class="etichetta" nowrap>Tipo soggetto promotore</td>
		<td colspan="3" class="campo">
			<af:comboBox 
				moduleName="ComboTipoSoggPromotore" 
				name="codSoggPromotoreMin" 
				classNameBase="input" 
				addBlank="true"
				selectedValue="<%=codSoggPromotoreMin%>" 
				disabled="<%=String.valueOf(!gestioneCampi)%>" 
				title="Tipo soggetto promotore"
				required="true" />
		</td>
	</tr>
	
	<tr>
  		<td class="etichetta" nowrap>Categoria tirocinante</td>
		<td colspan="3" class="campo">
			<af:comboBox 
				moduleName="ComboCategoriaTirocinante" 
				name="codCategoriaTir" 
				classNameBase="input" 
				addBlank="true"
				selectedValue="<%=codCategoriaTir%>" 
				disabled="<%=String.valueOf(!gestioneCampi)%>" 
				title="Categoria Tirocinante"
				required="true" />
		</td>
	</tr>
	<tr>
		<td class="etichetta" nowrap>Tipologia tirocinio</td>
		<td colspan="3" class="campo">
			<af:comboBox 
				moduleName="ComboTipologiaTirocinio" 
				name="codTipologiaTir" 
				classNameBase="input" 
				addBlank="true"
				selectedValue="<%=codTipologiaTir%>" 
				disabled="<%=String.valueOf(!gestioneCampi)%>" 
				title="Tipologia tirocinio"
				required="true" />
		</td>
	</tr>
  
  <%if (configuarazioneTir.equals("1")) {%>
  <tr>
    <td colspan="4">          
      <div class="sezione2">
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
				<%if (gestioneCampi) {%>   
					<a href="javascript:selectQualificaSRQOnClick(document.Frm1.CODQUALIFICASRQ, document.Frm1.DESCQUALIFICASRQ, 'codice');">
					<img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;
				<%}%>
				<af:textBox classNameBase="input" type="text" size="60" name="DESCQUALIFICASRQ" value="<%=descQualificaSrq%>" readonly="<%=String.valueOf(!gestioneCampi)%>"/>
    			<%if (gestioneCampi) {%> 
					<a href="javascript:selectQualificaSRQOnClick(document.Frm1.CODQUALIFICASRQ, document.Frm1.DESCQUALIFICASRQ, 'descrizione');">
					<img src="../../img/binocolo.gif" alt="Cerca per descrizione">
				</a>
				<a href="javascript:ricercaAvanzataQualificaSRQ();">Ricerca avanzata</A>
				<%}%>
	</td>
  </tr>
  <tr>
	<td class="etichetta">Tipo soggetto promotore (RER)</td>
	<td class="campo" colspan="3">
		<af:comboBox moduleName="ComboTipoEntePromotore" name="codTipoEntePromotore" classNameBase="input" addBlank="true"
				selectedValue="<%=codTipoEntePromotore%>" disabled="<%=String.valueOf(!canModify)%>" title="Tipo soggetto promotore (RER)"/>
	</td>
  </tr>
  <%} else {%>
  	<input type="hidden" name="CODQUALIFICASRQ" value="">
	<input type="hidden" name="DESCQUALIFICASRQ" value="">
  <%}%>
  <tr><td><br/></td></tr>
  <tr>
    <%if(gestioneCampi){%>
      <td colspan="2" align="right">    
      <input type="button" class="pulsante" name="confermaTirocinio" value="Avanti " onclick="javascript:aggiorna();">
      </td>    
      <td colspan="2" align="left">
        <input type="button" class="pulsante" name="chiudiTirocinio" value="Chiudi" onclick="javascript:window.close();">
      </td>
    <%} else {%>
          <td colspan="4" align="center">
            <input type="button" class="pulsante" name="chiudiTirocinio" value="Chiudi" onclick="javascript:window.close();">
          </td>
      <%}%>
  </tr>
</table>

<input type="hidden" name="FUNZ_AGG" value="<%=funzione%>">
<input type="hidden" name="PRGMOVIMENTO" value="<%=prgMovimento%>">
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
