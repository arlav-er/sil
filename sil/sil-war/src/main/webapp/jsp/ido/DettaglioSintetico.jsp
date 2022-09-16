<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
  it.eng.sil.util.*, 
  it.eng.afExt.utils.*,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*" %>
  
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
boolean canModify = false;
boolean bRigaCreataDatiGenerali = false;
boolean bTabellaCreata = false;

boolean checkVdA = false;
PageAttribs attributi = new PageAttribs(user, "IdoTestataRichiestaPage");
checkVdA = attributi.containsButton("CHECKVDA"); 

int nColonne = 0;
//dichiarazioni dati generali
SourceBean richiestaRow = null;
String numRichiestaAnno = "";
String numRichiestaAnnoVis = "";
String numAnnoRichiesta = "";
String datRichiesta = "";
String datScadenza = "";
String codCpi = "";
String codTrasferta = "";
String datPubblicazione = "";
String datScadenzaPubblicazione = "";

// Campi per la Pubblicazione
String codQualifica = "";
boolean flgPubbPalese = false;
boolean flgPubbAnonima = false;
String flgPubbWeb="";
String flgPubbGiornali="";
String flgPubbBacheca="";
String strCognomeRifPubb="";
String strNomeRifPubb="";
String strTelRifPubb="";
String strFaxRifPubb="";
String strEmailRifPubb="";
String strDatiAziendaPubb="";
String strMansionePubb="";
String strLuogoLavoro="";
String strFormazionePubb="";
String strConoscenzePubb="";
String strNoteOrarioPubb="";
String strRifCandidaturaPubb="";


String flgAutomunito = "";
String flgMotomunito = "";
String flgMilite = "";
String flgPubblicata = "";
String numProfRichiesti = "";
String prgAzienda = "";
String prgUnita = "";
String prgRichiestaAZ = "";
String prgSpi = "";
String strCognomeRiferimento = "";
String strNomeRiferimento = "";
String strNominativo = "";
String strTelRiferimento = "";
String strFaxRiferimento = "";
String strEmailRiferimento = "";
String txtFiguraProfessionale = "";
String txtCaratteristFigProf = "";
String txtCondContrattuale = "";
String codArea = "";
String flgFuoriSede = "";
String flgCodifica = "";
String strLocalita = "";
String flgVittoAlloggio = "";
String flgVitto = "";
String flgTurismo = "";
String strSesso = "";
String codMotGenere = "";
String strMotivSesso = "";
String cdnUtIns = "";
String dtmIns = "";
String cdnUtMod = "";
String dtmMod = "";
Testata operatoreInfo = null;
String codEvasione = "";
String strMonoCatCM = "";

String cdnFunzione = serviceRequest.containsAttribute("CDNFUNZIONE") ? serviceRequest.getAttribute("CDNFUNZIONE").toString() : "";  
String cdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE") ? serviceRequest.getAttribute("CDNLAVORATORE").toString() : "";
String stampa = serviceRequest.containsAttribute("STAMPA_PAR") ? serviceRequest.getAttribute("STAMPA_PAR").toString() : "";
//if (stampa != null) {
//	serviceRequest.delAttribute("STAMPA_PAR");
//}
  
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<html>
<head>
<title>Dettaglio Richiesta</title>
<style type="text/css"> 
.titoloSezioni {
	text-decoration: none;
	border: 0px;
	font-family: Verdana, Arial, Helvetica, Sans-serif; 
	font-size: 11px;
	font-weight: bold;
	color: #000066;
	text-align: center;
}
</style>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<script>
	function goBack() {
		<%
		if (("").equals(stampa)) {
		%>
			javascript:history.back();
		<%	
		}
		else {			
		%>
			url="AdapterHTTP?PAGE=CMListaAdesioniPage";
		    url += "&CDNFUNZIONE="+"<%=cdnFunzione%>";      
		    url += "&cdnLavoratore="+"<%=cdnLavoratore%>";     
		     
		    setWindowLocation(url);
		<%
		}
		%>			
	}
</script>

</head>
<body class="gestione" onload="rinfresca();">
<%
String valueBtnChiudi = "";
String strPopUp = serviceRequest.containsAttribute("POPUP")? serviceRequest.getAttribute("POPUP").toString():"";
if (strPopUp.equals("")) {	  
  valueBtnChiudi = "Torna alla lista";
}
else {
  valueBtnChiudi = "Chiudi";
}

int i;
//Dati generali della richiesta
richiestaRow = (SourceBean) serviceResponse.getAttribute("M_GetTestataRichiestaSintetico.ROWS.ROW");
if (richiestaRow != null) {
  prgRichiestaAZ            = richiestaRow.containsAttribute("prgrichiestaaz") ? richiestaRow.getAttribute("prgrichiestaaz").toString() : "";
  numRichiestaAnno          = richiestaRow.containsAttribute("NUMRICHIESTA") ? richiestaRow.getAttribute("NUMRICHIESTA").toString() : "";
  numRichiestaAnnoVis       = richiestaRow.containsAttribute("NUMRICHIESTAVIS") ? richiestaRow.getAttribute("NUMRICHIESTAVIS").toString() : "";
  numAnnoRichiesta          = richiestaRow.containsAttribute("NUMANNO") ? richiestaRow.getAttribute("NUMANNO").toString() : "";
  datRichiesta              = StringUtils.getAttributeStrNotNull(richiestaRow, "datRichiesta");
  datScadenza               = StringUtils.getAttributeStrNotNull(richiestaRow, "datScadenza");
  codCpi                    = StringUtils.getAttributeStrNotNull(richiestaRow, "codCpi");
  codTrasferta              = StringUtils.getAttributeStrNotNull(richiestaRow, "codTrasferta");
  datPubblicazione          = StringUtils.getAttributeStrNotNull(richiestaRow, "datPubblicazione");
  datScadenzaPubblicazione  = StringUtils.getAttributeStrNotNull(richiestaRow, "datScadenzaPubblicazione");
  // Campi per la Pubblicazione
  codQualifica              = StringUtils.getAttributeStrNotNull(richiestaRow, "codQualifica");
  SourceBean evasioneRow = (SourceBean) serviceResponse.getAttribute("M_IdoGetStatoRich.ROWS.ROW");
  if (evasioneRow != null) {
    codEvasione     = StringUtils.getAttributeStrNotNull(evasioneRow, "codEvasione");
    flgPubbPalese   = (codEvasione.equalsIgnoreCase("DFD") || codEvasione.equalsIgnoreCase("DPR"));
    flgPubbAnonima  = (codEvasione.equalsIgnoreCase("DFA") || codEvasione.equalsIgnoreCase("DRA"));
    flgPubbWeb      = StringUtils.getAttributeStrNotNull(evasioneRow, "flgPubbWeb");
    flgPubbGiornali = StringUtils.getAttributeStrNotNull(evasioneRow, "flgPubbGiornali");
    flgPubbBacheca  = StringUtils.getAttributeStrNotNull(evasioneRow, "flgPubbBacheca");  
  }
  strCognomeRifPubb    = StringUtils.getAttributeStrNotNull(richiestaRow, "strCognomeRifPubb");
  strNomeRifPubb       = StringUtils.getAttributeStrNotNull(richiestaRow, "strNomeRifPubb");
  strTelRifPubb        = StringUtils.getAttributeStrNotNull(richiestaRow, "strTelRifPubb");
  strFaxRifPubb        = StringUtils.getAttributeStrNotNull(richiestaRow, "strFaxRifPubb");
  strEmailRifPubb      = StringUtils.getAttributeStrNotNull(richiestaRow, "strEmailRifPubb");    
  strDatiAziendaPubb     = StringUtils.getAttributeStrNotNull(richiestaRow, "strDatiAziendaPubb");
  strMansionePubb        = StringUtils.getAttributeStrNotNull(richiestaRow, "strMansionePubb");
  txtFiguraProfessionale = StringUtils.getAttributeStrNotNull(richiestaRow, "txtFiguraProfessionale");
  strLuogoLavoro         = StringUtils.getAttributeStrNotNull(richiestaRow, "strLuogoLavoro");
  strFormazionePubb      = StringUtils.getAttributeStrNotNull(richiestaRow, "strFormazionePubb");
  txtCondContrattuale    = StringUtils.getAttributeStrNotNull(richiestaRow, "txtCondContrattuale");
  strConoscenzePubb      = StringUtils.getAttributeStrNotNull(richiestaRow, "strConoscenzePubb");
  strNoteOrarioPubb        = StringUtils.getAttributeStrNotNull(richiestaRow, "strNoteOrarioPubb");  
  strRifCandidaturaPubb    = StringUtils.getAttributeStrNotNull(richiestaRow, "strRifCandidaturaPubb");  
  
  flgAutomunito             = StringUtils.getAttributeStrNotNull(richiestaRow, "flgAutomunito");
  flgMotomunito             = StringUtils.getAttributeStrNotNull(richiestaRow, "flgMotomunito");
  flgFuoriSede              = StringUtils.getAttributeStrNotNull(richiestaRow, "flgFuoriSede");
  strLocalita               = StringUtils.getAttributeStrNotNull(richiestaRow, "strLocalita");
  flgMilite                 = StringUtils.getAttributeStrNotNull(richiestaRow, "flgMilite"); 
  flgPubblicata             = StringUtils.getAttributeStrNotNull(richiestaRow, "flgPubblicata");
  numProfRichiesti          = richiestaRow.getAttribute("numProfRichiesti").toString();
  prgAzienda                = richiestaRow.getAttribute("PRGAZIENDA").toString();
  prgUnita                  = richiestaRow.getAttribute("PRGUNITA").toString();
  prgSpi                    = richiestaRow.containsAttribute("prgSpi") ? richiestaRow.getAttribute("prgSpi").toString() : "";
  strCognomeRiferimento     = StringUtils.getAttributeStrNotNull(richiestaRow, "strCognomeRiferimento");
  strNomeRiferimento        = StringUtils.getAttributeStrNotNull(richiestaRow, "strNomeRiferimento");
  strNominativo             = strCognomeRiferimento + " " + strNomeRiferimento;
  strTelRiferimento         = StringUtils.getAttributeStrNotNull(richiestaRow, "strTelRiferimento");
  strFaxRiferimento         = StringUtils.getAttributeStrNotNull(richiestaRow, "strFaxRiferimento");
  strEmailRiferimento       = StringUtils.getAttributeStrNotNull(richiestaRow, "strEmailRiferimento");
  txtFiguraProfessionale    = StringUtils.getAttributeStrNotNull(richiestaRow, "txtFiguraProfessionale");
  txtCaratteristFigProf     = StringUtils.getAttributeStrNotNull(richiestaRow, "txtCaratteristFigProf"); 
  txtCondContrattuale       = StringUtils.getAttributeStrNotNull(richiestaRow, "txtCondContrattuale");
  codArea                   = StringUtils.getAttributeStrNotNull(richiestaRow, "codArea"); 
  flgFuoriSede              = StringUtils.getAttributeStrNotNull(richiestaRow, "flgFuoriSede");
  flgVittoAlloggio          = StringUtils.getAttributeStrNotNull(richiestaRow, "flgVittoAlloggio"); 
  flgVitto			        = StringUtils.getAttributeStrNotNull(richiestaRow, "flgVitto");
  flgTurismo          		= StringUtils.getAttributeStrNotNull(richiestaRow, "flgTurismo");
  strSesso                  = StringUtils.getAttributeStrNotNull(richiestaRow, "strSesso");
  codMotGenere              = StringUtils.getAttributeStrNotNull(richiestaRow, "CODMOTGENERE");
  strMotivSesso             = StringUtils.getAttributeStrNotNull(richiestaRow, "strMotivSesso");
  cdnUtIns                  = richiestaRow.getAttribute("cdnUtIns").toString();
  dtmIns                    = StringUtils.getAttributeStrNotNull(richiestaRow, "dtmIns");
  cdnUtMod                  = richiestaRow.getAttribute("cdnUtMod").toString();
  dtmMod                    = StringUtils.getAttributeStrNotNull(richiestaRow, "dtmMod");
}
operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAZ);
infCorrentiAzienda.show(out); 
%>
<p class="titolo">Dati Generali</p>
<%out.print(htmlStreamTop);%>
<table class="main" border="0">
<tr>
<td width="25%">&nbsp;</td>
<td width="25%">&nbsp;</td>
<td width="25%">&nbsp;</td>
<td width="25%">&nbsp;</td>
</tr>

<tr>
<td class="etichetta2">Numero richiesta</td>
<td class="campo2_read"><b><%=numRichiestaAnnoVis%>/<%=numAnnoRichiesta%></b></td>
<td class="etichetta2">Data richiesta&nbsp;
<b><%=datRichiesta%></b>
</td>
<td class="etichetta2">Data scadenza&nbsp;
<b><%=datScadenza%></b>
</td>
</tr>
<tr>
<td class="etichetta2">Centro per l'impiego</td>
<td class="campo2" colspan="3">
  <af:comboBox classNameBase="input" name="codCpiRichiesta" selectedValue="<%=codCpi%>" disabled="true" moduleName="M_ElencoCPI"/>
</td>
</tr>

<tr>
<td class="etichetta2">Numero profili richiesti</td>
<td class="campo2"><b><%=numProfRichiesti%></b></td>
  <td colspan="2">&nbsp;</td>
<%

if (!codArea.equals("")) {
  if (!bRigaCreataDatiGenerali) {
  %>
    <tr valign="top">
  <%
    bRigaCreataDatiGenerali = true;
  }
%>
  <td class="etichetta2">Area di inserimento</td>
  <td class="campo2">
    <af:comboBox classNameBase="input" name="areaInsRichiestaAZ" selectedValue="<%=codArea%>" disabled="true" moduleName="M_GetArea"/>
  </td>
<%
  nColonne = nColonne + 2;
}

if (bRigaCreataDatiGenerali) {
  if (nColonne == 2) {
  %>
    <td colspan="2">&nbsp;</td>
  <%
  }
  %>
  </tr>
  <%
}
bRigaCreataDatiGenerali = false;
nColonne = 0;
%>

<tr>
<%
if (!flgFuoriSede.equals("")) {
  nColonne = nColonne + 2;
  if (flgFuoriSede.toUpperCase().equals("N")) {
    flgCodifica = "No";
  }
  else {
    flgCodifica = "Sì";
  }
%>
  <td class="etichetta2">Fuori sede</td>
  <td class="campo2">
    <af:textBox classNameBase="input" name="fuoriSedeRichiestaAZ" size="11" value="<%=flgCodifica%>" readonly="true"/>
  </td>
<%
}
flgCodifica = "";
if (!strLocalita.equals("")) {
  nColonne = nColonne + 2;
%>
  <td class="etichetta2">Località</td>
  <td class="campo2">
    <af:textBox classNameBase="input" name="localitaSedeRichiestaAZ" value="<%=strLocalita%>" readonly="true"/>
  </td>
<%
}

if (!flgAutomunito.equals("")) {
  if (flgAutomunito.toUpperCase().equals("N")) {
    flgCodifica = "No";
  }
  else {
    if (flgAutomunito.toUpperCase().equals("S")) {
      flgCodifica = "Sì";
    }
    else {
      if (flgAutomunito.toUpperCase().equals("P")) {
        flgCodifica = "Preferibile";
      }
    }
  }
  if (nColonne == 4) {
  %>
    </tr>
    <tr>
  <%
    nColonne = 0;
  }
  nColonne = nColonne + 2;
  %>
  <td class="etichetta2">Automunito</td>
  <td class="campo2">
    <af:textBox classNameBase="input" name="flgAutoRichiestaAZ" value="<%=flgCodifica%>" readonly="true"/>
  </td>
  <%
}
flgCodifica = "";

if (!codTrasferta.equals("")) {
  if (nColonne == 4) {
  %>
    </tr>
    <tr>
  <%
    nColonne = 0;
  }
  nColonne = nColonne + 2;
  %>
  <td class="etichetta2">Trasferta</td>
  <td class="campo2">
    <af:comboBox classNameBase="input" name="trasfertaRichiestaAZ" selectedValue="<%=codTrasferta%>" disabled="true" moduleName="MListTrasferte"/>
  </td>
  <%
}

if (!flgMotomunito.equals("")) {
  if (flgMotomunito.toUpperCase().equals("N")) {
    flgCodifica = "No";
  }
  else {
    if (flgMotomunito.toUpperCase().equals("S")) {
      flgCodifica = "Sì";
    }
    else {
      if (flgMotomunito.toUpperCase().equals("P")) {
        flgCodifica = "Preferibile";
      }
    }
  }
  if (nColonne == 4) {
  %>
    </tr>
    <tr>
  <%
    nColonne = 0;
  }
  nColonne = nColonne + 2;
  %>
  <td class="etichetta2">Motomunito</td>
  <td class="campo2">
    <af:textBox classNameBase="input" name="flgMotoRichiestaAZ" value="<%=flgCodifica%>" readonly="true"/>
  </td>
  <%
}
flgCodifica = "";

if (!flgVittoAlloggio.equals("")) {
  if (flgVittoAlloggio.toUpperCase().equals("N")) {
    flgCodifica = "No";
  }
  else {  
    flgCodifica = "Sì";
  }
  if (nColonne == 4) {
  %>
    </tr>
    <tr>
  <%
    nColonne = 0;
  }
  nColonne = nColonne + 2;
  %>
  <td class="etichetta2">Disponibilità alloggio aziendale</td>
  <td class="campo2">
    <af:textBox classNameBase="input" name="vittoRichiestaAZ" value="<%=flgCodifica%>" readonly="true"/>
  </td>
  <%
}

  
if (checkVdA) {

	flgCodifica = "";   
	
	if (!flgVitto.equals("")) {
	  if (flgVitto.toUpperCase().equals("N")) {
	    flgCodifica = "No";
	  }
	  else {  
	    flgCodifica = "Sì";
	  }
	  if (nColonne == 4) {
	  %>
	    </tr>
	    <tr>
	  <%
	    nColonne = 0;
	  }
	  nColonne = nColonne + 2;
	  %>
	  <td class="etichetta2">Solo richieste con vitto</td>
	  <td class="campo2">
	    <af:textBox classNameBase="input" name="soloVittoRichiestaAZ" value="<%=flgCodifica%>" readonly="true"/>
	  </td>
	  <%
	}

	flgCodifica = "";

	if (!flgTurismo.equals("")) {
	  if (flgTurismo.toUpperCase().equals("N")) {
	    flgCodifica = "No";
	  }
	  else {  
	    flgCodifica = "Sì";
	  }
	  if (nColonne == 4) {
	  %>
	    </tr>
	    <tr>
	  <%
	    nColonne = 0;
	  }
	  nColonne = nColonne + 2;
	  %>
	  <td class="etichetta2">Turismo</td>
	  <td class="campo2">
	    <af:textBox classNameBase="input" name="turismoRichiestaAZ" value="<%=flgCodifica%>" readonly="true"/>
	  </td>
	  <%
	}

}	
	
flgCodifica = "";

if (!flgMilite.equals("")) {
  if (flgMilite.toUpperCase().equals("N")) {
    flgCodifica = "No";
  }
  else {
    if (flgMilite.toUpperCase().equals("S")) {
      flgCodifica = "Sì";
    }
    else {
      if (flgMilite.toUpperCase().equals("P")) {  
        flgCodifica = "Preferibile";
      }
    }
  }
  if (nColonne == 4) {
  %>
    </tr>
    <tr>
  <%
    nColonne = 0;
  }
  nColonne = nColonne + 2;
  %>
  <td class="etichetta2">Milite esente/assolto</td>
  <td class="campo2">
    <af:textBox classNameBase="input" name="militeRichiestaAZ" value="<%=flgCodifica%>" readonly="true"/>
  </td>
  <%
}
flgCodifica = "";

if (nColonne == 2) { 
%>
  <td colspan="2"></td>
<%
}
%>
</tr>

<%
if (!strSesso.equals("")) {
%>
  <tr>
  <td class="etichetta2">Sesso&nbsp;<b><%=strSesso%></b></td>
  <%
  if (!codMotGenere.equals("ALT")) {
  %>
	  <td class="etichetta2" colspan="3" valign="top">Motivazione&nbsp;
		  <af:comboBox name="codMotGenere" selectedValue="<%=codMotGenere%>"
			            			 moduleName="COMBO_MOTIVO_SESSO"  
			            			 disabled="true" classNameBase="input"/>
	  </td>
  <%
  }
  else {
  %>
  	<td class="etichetta2" colspan="3" valign="top">Motivazione&nbsp;<b><%=strMotivSesso%></b></td>
  <%
  }
  %>
  </tr>
<%
}
%>
</table>

<%
if ((!strCognomeRiferimento.equals("")) ||
    (!strNomeRiferimento.equals("")) ||
    (!strEmailRiferimento.equals("")) || 
    (!strTelRiferimento.equals("")) || 
    (!strFaxRiferimento.equals(""))) {

  bTabellaCreata = true;
%>
  <table id="sezione_riferimento" width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
  <tr>
  	<td width="25%"></td><td width="25%"></td><td width="25%"></td><td width="25%"></td>
  </tr>
  <tr valign="bottom"><td colspan="4" width="100%">
  <font class="titoloSezioni">Riferimento</font>
  </td></tr>
  <tr valign="top"><td colspan="4" width="100%"><hr></td></tr>
  <!--<div class="sezione">Riferimento</div></td></tr>-->
  <tr>
  <%
  if ((!strCognomeRiferimento.equals("")) || (!strNomeRiferimento.equals(""))) {
  %>
    <td class="etichetta2">Riferimento</td>
    <td class="campo2"><b><%=strNominativo%></b></td>
  <%
  }
  if (!strEmailRiferimento.equals("")) {
  %>
    <td class="etichetta2">Email</td>
    <td class="campo2"><b><%=strEmailRiferimento%></b></td>
  <%
  }
  %>
  </tr>
  <tr>
  <%
  if (!strTelRiferimento.equals("")) {
  %>
    <td class="etichetta2">Telefono</td>
    <td class="campo2"><b><%=strTelRiferimento%></b></td>
  <%
  }
  if (!strFaxRiferimento.equals("")) {
  %>
    <td class="etichetta2">Fax</td>
    <td class="campo2"><b><%=strFaxRiferimento%></b></td>
  <%
  }
  %>
  </tr>
<%
}
if ("S".equalsIgnoreCase(flgPubblicata)) {
  flgCodifica = "Sì";
  if (!bTabellaCreata) {
    bTabellaCreata = true;
  %>
    <table id="sezione_riferimento" width="100%" border="0">
  <%
  }
  %>
  <tr valign="bottom"><td colspan="4" width="100%">
	  <font class="titoloSezioni">Pubblicazione</font>
	  </td></tr>
	  <tr valign="top"><td colspan="4" width="100%"><hr></td></tr>
	  <!--<div class="sezione">Pubblicazione</div></td></tr>-->
  <tr>
	  <td class="etichetta2">Pubblicata</td>
	  <td class="campo2"><b><%=flgCodifica%></b></td>
	  <td class="etichetta2">Data&nbsp;<b><%=datPubblicazione%></b></td>
	  <td class="etichetta2">Data scadenza&nbsp;<b><%=datScadenzaPubblicazione%></b></td>
  </tr>

  <%-- NON USATE: flgPubbPalese, flgPubbAnonima --%>

  <tr>
	  <td class="etichetta2">Categoria</td>
	  <td class="campo2">
	        <af:comboBox name="codQualifica" selectedValue="<%=codQualifica%>"
	            			 moduleName="M_GetIdoTipiQualificaPub"  
	            			 disabled="true" classNameBase="input"/>
	  </td>
	  <%
	  String outmod = "";
	  if ("S".equalsIgnoreCase(flgPubbWeb))      outmod += "Web &nbsp;";
	  if ("S".equalsIgnoreCase(flgPubbGiornali)) outmod += "Giornali &nbsp;";
	  if ("S".equalsIgnoreCase(flgPubbBacheca))  outmod += "Bacheca &nbsp;";
	  
	  if (StringUtils.isFilled(outmod)) { %>
		  <td class="etichetta2">Modalit&agrave; pubblicazione</td>
		  <td class="campo2"><b><%= outmod %></b></td>
	  <% } else { %>
		  <td class="campo2" colspan="2">&nbsp;</td>
	  <% } %>
  </tr>

  <%
  String outrif = "";
  if (StringUtils.isFilled(strCognomeRifPubb)) outrif += "<nobr><b>" + strCognomeRifPubb  + "</b></nobr> &nbsp;";
  if (StringUtils.isFilled(strNomeRifPubb))    outrif += "<nobr><b>" + strNomeRifPubb     + "</b></nobr> &nbsp;";
  if (StringUtils.isFilled(strTelRifPubb))     outrif += "<nobr>Tel: <b>" + strTelRifPubb + "</b></nobr> &nbsp;";
  if (StringUtils.isFilled(strFaxRifPubb))     outrif += "<nobr>Fax: <b>" + strFaxRifPubb + "</b></nobr> &nbsp;";
  if (StringUtils.isFilled(strEmailRifPubb))   outrif += "<nobr>E-mail: <b>" + strEmailRifPubb + "</b></nobr> &nbsp;";
  
  if (StringUtils.isFilled(outrif)) { %>
	  <tr>
		  <td class="etichetta2">Riferimento</td>
		  <td class="campo2" colspan="3"><%= outrif %></td>
	  </tr>
  <% } %>

  <% 
  	String[] outmsgArr = new String[] {
  							"Dati Azienda",
  							"Mansione",
  							"Contenuto e contesto lavoro",
  							"Luogo di lavoro",
  							"Formazione",
  							"Contratto",
  							"Conoscenze",
  							"Caratteristiche del candidato",
  							"Orario",
  							"Per candidarsi"
  						};
  	String[] outdatArr = new String[] {
							strDatiAziendaPubb,
							strMansionePubb,
							txtFiguraProfessionale,
							strLuogoLavoro,
							strFormazionePubb,
							txtCondContrattuale,
							strConoscenzePubb,
							txtCaratteristFigProf,
							strNoteOrarioPubb,
							strRifCandidaturaPubb
						};

  for (int x = 0; x < outdatArr.length; x++) {
	  if (StringUtils.isFilledNoBlank(outdatArr[x])) {
		// Sostituisco gli "a-capo" con dei "BR" e gli spazi con NBSP
	    outdatArr[x] = StringUtils.replace(outdatArr[x], "\r\n", "<br/>");	  
	    outdatArr[x] = StringUtils.replace(outdatArr[x], "\n",   "<br/>");	  
	  	outdatArr[x] = StringUtils.replace(outdatArr[x], " ",    "&nbsp;");	  
	  	%>
		<tr>
			<td class="etichetta2" valign="top" style="border-top: 1 solid #C6D5FF;"><%= outmsgArr[x] %></td>
			<td class="campo2_read" colspan="3" style="border-top: 1 solid #C6D5FF;">
			<b><%= outdatArr[x] %></b></td>	
		</tr>
		<%
	  }
  } //for

}

if (bTabellaCreata) {
  bTabellaCreata = false;
  %>
</table>
<%	}
	out.print(htmlStreamBottom);
%>


<!-- dati e table relativi alla Asta * Articolo 16 -->

<%
  	// INIT-PARTE-TEMP
	if (Sottosistema.AS.isOff()) {	
	
	} else {
			
	// END-PARTE-TEMP 
		SourceBean richiesta = null;
		richiesta	  		 = (SourceBean) serviceResponse.getAttribute("M_GETTESTATARICHIESTA.ROWS.ROW");
		if ( richiesta != null) {
			
			BigDecimal PostiAS		= richiesta.getAttribute("NUMPOSTOAS") != null?(BigDecimal) richiesta.getAttribute("NUMPOSTOAS"):null;
			BigDecimal PostiMob		= richiesta.getAttribute("NUMPOSTOMB") != null?(BigDecimal) richiesta.getAttribute("NUMPOSTOMB"):null;
			BigDecimal PostiExMil	= richiesta.getAttribute("NUMPOSTOMILITARE") != null?(BigDecimal) richiesta.getAttribute("NUMPOSTOMILITARE"):null;
			BigDecimal PostiExLSU	= richiesta.getAttribute("NUMPOSTOLSU") != null?(BigDecimal) richiesta.getAttribute("NUMPOSTOLSU"):null;
			String strDataChiam 	= StringUtils.getAttributeStrNotNull(richiesta, "DATCHIAMATA");
			String strRiutGrad		= StringUtils.getAttributeStrNotNull(richiesta, "FLGRIUSOGRADUATORIA");
			String strTipoLSU		= StringUtils.getAttributeStrNotNull(richiesta, "TIPOLSU");
			
			String strPostiAS = 	"";
			if (PostiAS == null) {
				strPostiAS = "";
			} else {
					strPostiAS = PostiAS.toString();
			}
			
			String strPostiMob = 	"";
			if (PostiMob == null) {
				strPostiMob = "";
			} else {
					strPostiMob = PostiMob.toString();
			}
			
			String strPostiExMil = 	"";
			if (PostiExMil == null) {
				strPostiExMil = "";
			} else {
					strPostiExMil = PostiExMil.toString();
			}
			
			String strPostiExLSU = 	"";
			if (PostiExLSU == null) {
				strPostiExLSU = "";
			} else {
					strPostiExLSU = PostiExLSU.toString();
			}
			
			if (!strRiutGrad.equals("")) {
				if (strRiutGrad.toUpperCase().equals("N")) {
				    flgCodifica = "No";
				} else {
				    if (strRiutGrad.toUpperCase().equals("S")) {
				      flgCodifica = "Sì";
				    }
				}
			}

			if ((!strPostiAS.equals(""))   ||
			    (!strPostiMob.equals(""))  ||
			    (!strPostiExMil.equals(""))|| 
			    (!strPostiExLSU.equals(""))|| 
			    (!strDataChiam.equals("")) ||
			    (!strRiutGrad.equals(""))) {
			    
				 out.print(htmlStreamTop);
%>
<table id="sezione_asta" width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
	<tr>
  		<td width="25%"></td><td width="25%"></td><td width="25%"></td><td width="25%"></td>
  	</tr>
  	<p></p>
  	<tr valign="bottom">
  		<td colspan="6" width="100%">
  		<font class="titoloSezioni">Dati asta art.16 Legge 56/87</font>
  		</td>
  	</tr>
  	<tr valign="top">
  		<td colspan="6" width="100%"><hr>
  		</td>
  	</tr>
  	<!--<div class="sezione">Riferimento</div></td></tr>-->
  	<tr valign="middle">
<%
if(!strDataChiam.equals("")){
%>
       	<td class="etichetta2">Data chiamata</td>
  		<td class="campo2">
    		<af:textBox classNameBase="input" title="Data chiamata" type="date" 
			validateOnPost="true" onKeyUp="fieldChanged();" 
			name="datChiamata" size="11" maxlength="10" value="<%=strDataChiam%>"   
			readonly="true" />  
  		</td>
<%}%>
<%
if(!strPostiMob.equals("")){
%>   
       	<td class="etichetta2">n. posti mobilità</td>
       	<td class="campo2">
       		<af:textBox classNameBase="input" 
			name="numPostoMB" size="5" value="<%=strPostiMob%>" 
  			readonly="true" maxlength="4" />
		</td>
<%}%>

	</tr>
	<tr valign="middle">
<%
if(!strPostiAS.equals("")){
%> 		
		<td class="etichetta2">n. posti art. 16</td>
    	<td class="campo2">
      	<af:textBox classNameBase="input" 
      		name="numPostoAS" size="5" value="<%=strPostiAS%>" 
      		readonly="true" maxlength="4" />
    	</td>
<%}%>
<%
if(!strPostiExMil.equals("")){
%> 
    	<td class="etichetta2">n. posti ex militari</td>
    	<td class="campo2">
      	<af:textBox classNameBase="input" 
      		name="numPostoMilitare" size="5" value="<%=strPostiExMil%>" 
          	readonly="true" maxlength="4" />
        </td>
<%}%>
   	</tr>
	<tr valign="middle">
<%
if(!flgCodifica.equals("")){
%> 	
    	<td class="etichetta2">Riutilizzo graduatoria</td>
        <td class="campo2">
        <af:textBox classNameBase="input"  
         	name="Riutilizzo graduatoria" size="5" value="<%=flgCodifica%>" 
      		readonly="true" maxlength="4" />                      
    	</td>
<%}%>
<%
if(!strPostiExLSU.equals("")){
%>
    	<td class="etichetta2">n. posti ex LSU</td>
        <td class="campo2">
        <af:textBox classNameBase="input"  
         	name="postiLsu" size="5" value="<%=strPostiExLSU%>" 
      		readonly="true" maxlength="4" />                      
    	</td>
<%}%>      	
	</tr>
<%
if(!strTipoLSU.equals("")){
%>
	<tr valign="middle">
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td class="etichetta2">Tipo ex LSU</td>
		<td class="campo2">
		<af:textBox classNameBase="input"  
         	name="tipoLSU" size="30" value="<%=strTipoLSU%>" 
      		readonly="true" maxlength="100" />                      
    	</td> 
    </tr>
<%}%>         	
</table>
			 
<% 		out.print(htmlStreamBottom);
	}%>
<%}%>

<%	
	// INIT-PARTE-TEMP
	}
   	// END-PARTE-TEMP
%>
<!-- dati e table relativi alla Asta CM -->
 
<%
  	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) {	
	
	} else {
			
	// END-PARTE-TEMP 
		SourceBean richiesta = null;
		richiesta	  		 = (SourceBean) serviceResponse.getAttribute("M_GETTESTATARICHIESTA.ROWS.ROW");
		if ( richiesta != null) {
			
			BigDecimal numPostiCM = richiesta.getAttribute("NUMPOSTICM") != null?(BigDecimal) richiesta.getAttribute("NUMPOSTICM"):null;
			//modifica Esposito
			BigDecimal numAnnoRedditoCM = richiesta.getAttribute("NUMANNOREDDITOCM") != null?(BigDecimal) richiesta.getAttribute("NUMANNOREDDITOCM"):null;
			
			String codMonoCMcategoria = StringUtils.getAttributeStrNotNull(richiesta, "CODMONOCMCATEGORIA");
			
			if( codMonoCMcategoria.equals("D") ) strMonoCatCM = "Disabili";
			else if( codMonoCMcategoria.equals("A") ) strMonoCatCM = "Categoria protetta ex. Art. 18";
				 else if( codMonoCMcategoria.equals("E") ) strMonoCatCM = "Entrambi";
			
			String strDescrLista = StringUtils.getAttributeStrNotNull(richiesta, "DESCRLISTA");
			String strDataChiamataCM = StringUtils.getAttributeStrNotNull(richiesta, "DATCHIAMATACM");
			String strCodMonoTipoGrad = StringUtils.getAttributeStrNotNull(richiesta, "CODMONOTIPOGRAD");
			String strCodTipoLista = StringUtils.getAttributeStrNotNull(richiesta, "CODTIPOLISTA");
			
			String strNumPostiCM = 	"";
			if (numPostiCM == null) {
				strNumPostiCM = "";
			} else {
				strNumPostiCM = numPostiCM.toString();
			}
			//modifica Esposito
			String strNumAnnoRedditoCM = 	"";
			if (numAnnoRedditoCM == null) {
				strNumAnnoRedditoCM = "";
			} else {
				strNumAnnoRedditoCM = numAnnoRedditoCM.toString();
			}
			
			String strMonoGrad = "";
			if (!"".equalsIgnoreCase(strCodMonoTipoGrad)) {
				if ("D".equalsIgnoreCase(strCodMonoTipoGrad)) {   
					strMonoGrad = "Avviamento numerico art.8";
				}
				if ("A".equalsIgnoreCase(strCodMonoTipoGrad)) {
					strMonoGrad = "Avviamento numerico art.18";
				}
				if ("G".equalsIgnoreCase(strCodMonoTipoGrad)) {
					strMonoGrad = "Graduatoria art.1";
				}				
			}

			if (!("").equals(strNumPostiCM)) {
			    
				 out.print(htmlStreamTop);
%>
<table id="sezione_asta_cm" width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
	<tr>
  		<td width="25%"></td><td width="25%"></td><td width="25%"></td><td width="25%"></td>
  	</tr>
  	<p></p>
  	<tr valign="bottom">
  		<td colspan="4" width="100%">
  		<font class="titoloSezioni">Dati graduatorie CM</font>
  		</td>
  	</tr>
  	<tr valign="top">
  		<td colspan="4" width="100%"><hr>
  		</td>
  	</tr>
<%
	if (!("").equals(strCodMonoTipoGrad)) {
%>  	
		<tr valign="top">				        	
			<td class="etichetta2">Tipo </td>
	        <td class="campo2" colspan="3">
	        	<af:textBox classNameBase="input"
	          	name="codMonoTipoGrad" size="100" value="<%= strMonoGrad %>"    
	          	readonly="true" /> 
	        </td>	
	    </tr>
<%
	}
	if (!("").equals(strNumPostiCM)) {
%>	    
	    <tr>
	        <td class="etichetta2">n. posti</td>
	        <td class="campo2" colspan="3">
	          <af:textBox classNameBase="input"
	          	name="numPostiCM" size="5" value="<%= strNumPostiCM %>" 
	          	readonly="true" />
	        </td>					            				            				            			            
		</tr>  
<%
	}
	//modifica Esposito
	if (!("").equals(strNumAnnoRedditoCM)) {
%>	    
	    <tr>
	        <td class="etichetta2">anno di riferimento per il reddito</td>
	        <td class="campo2" colspan="3">
	          <af:textBox classNameBase="input"
	          	name="numAnnoRedditoCM" size="5" value="<%= strNumAnnoRedditoCM %>" 
	          	readonly="true" />
	        </td>					            				            				            			            
		</tr>  
<%
	}
	
	if (!("").equals(strDataChiamataCM)) {
%>	
		<tr valign="top">
			<td class="etichetta2">Data chiamata</td>
			<td class="campo2" colspan="3">
				<af:textBox classNameBase="input" title="Data chiamata CM" type="date" 
					validateOnPost="true" 
					name="datChiamataCM" size="11" value="<%=strDataChiamataCM%>"   
					readonly="true" />   
			</td>			      		
		</tr>	
<%
	}
	if (!("").equals(strCodTipoLista)) {
%>	
		<tr valign="top">
			<td class="etichetta2">Tipo iscrizione art.1</td>
	    	<td class="campo2" colspan="3">
		       <af:textBox classNameBase="input"
	          	name="" size="100" value="<%= strDescrLista %>" 
	          	readonly="true" />   
	      	</td>	    		      		
		</tr>  	 
<%
	}
%>	  	
</table>
			 
<% 		out.print(htmlStreamBottom);
	}%>
<%}%>

<%
	if( (codEvasione.equals("MIR") || codEvasione.equals("MPP") || codEvasione.equals("MPA")) && !strMonoCatCM.equals("") ){
 		out.print(htmlStreamTop);
%>	
<table id="sezione_categoria_cm" width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
	<tr>
  		<td width="25%"></td><td width="25%"></td><td width="25%"></td><td width="25%"></td>
  	</tr>
  	<p></p>
  	<tr valign="bottom">
  		<td colspan="4" width="100%">
  		<font class="titoloSezioni">Categoria CM</font>
  		</td>
  	</tr>
  	<tr valign="top">
  		<td colspan="4" width="100%"><hr>
  		</td>
  	</tr>
	<tr valign="top">				        	
		<td class="etichetta2">Categoria CM</td>
        <td class="campo2" colspan="3">
        	<af:textBox classNameBase="input"
          	name="codMonoCMcategoria" size="100" value="<%= strMonoCatCM %>"    
          	readonly="true" /> 
        </td>	
    </tr>
</table>		
<%
		out.print(htmlStreamBottom);
	}
%>

<%	
	// INIT-PARTE-TEMP
	}
   	// END-PARTE-TEMP
%> 
 
 

<%


SourceBean rigaRichiestaFisse = null;
flgCodifica = "";
String strFlgIndispensabile = "";
Vector vectRichiesta = null;
String strDescAbilitazione = "";
String strDescTipoAbilitazione = "";
//Lista delle abilitazioni della richiesta
vectRichiesta=serviceResponse.getAttributeAsVector("M_ListAbilRich_Dettaglio_Sintetico.ROWS.ROW");
if (vectRichiesta.size() > 0) {
  if (!bTabellaCreata) {
    bTabellaCreata = true;
    out.print(htmlStreamTop);
    %>
    <table border="0" width="100%" cellspacing="0" margin="0" cellpadding="0">
    <tr valign="bottom"><td>
    <%
  }
  %>
  <table border="0" width="100%" cellspacing="0" margin="0" cellpadding="0">
  <tr valign="bottom"><td colspan="6">
  <font class="titoloSezioni">Abilitazioni</font>
  </td></tr>
  <tr valign="top"><td colspan="6" width="100%"><hr></td></tr>
  <!--<div class="sezione">Abilitazioni</div></td></tr>-->
  </table>
  <table>
  <%
  for (i=0;i<vectRichiesta.size();i++) {
    rigaRichiestaFisse = (SourceBean) vectRichiesta.elementAt(i);
    strDescTipoAbilitazione = rigaRichiestaFisse.containsAttribute("DESCRIZIONETIPOABILITAZIONE")?rigaRichiestaFisse.getAttribute("DESCRIZIONETIPOABILITAZIONE").toString():"";
    strDescAbilitazione = rigaRichiestaFisse.containsAttribute("STRDESCRIZIONE")?rigaRichiestaFisse.getAttribute("STRDESCRIZIONE").toString():"";
    strFlgIndispensabile = rigaRichiestaFisse.containsAttribute("FLGINDISPENSABILE")?rigaRichiestaFisse.getAttribute("FLGINDISPENSABILE").toString():"";
    %>
    <tr valign="top">
    <td class="etichetta2">Tipo</td>
    <td class="campo2"><b><%=strDescTipoAbilitazione%></b></td>
    <td class="etichetta2">Descrizione</td>
    <td class="campo2"><b><%=strDescAbilitazione%></b></td>
    <%
    if (!strFlgIndispensabile.equals("")) {
    %>
      <td class="etichetta2">Indispensabile</td>
      <td class="campo2"><b><%=strFlgIndispensabile%></b></td>
    <%
    }
    else {
    %>
      <td colspan="2">&nbsp;</td>
    <%
    }
    %>
    </tr>
    <%
  }
  %>
  <tr><td></td></tr>
  </table>
  <%
}

vectRichiesta = null;
rigaRichiestaFisse = null;
String strDescOrario = "";
//Lista degli orari della richiesta
vectRichiesta=serviceResponse.getAttributeAsVector("MListaOrariRichiesta.ROWS.ROW");
if (vectRichiesta.size() > 0) {
  if (!bTabellaCreata) {
    bTabellaCreata = true;
    out.print(htmlStreamTop);
    %>
    <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
    <tr valign="top"><td>
    <%
  }
  %>
  <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
  <tr valign="bottom"><td colspan="2">
  <font class="titoloSezioni">Orari</font>
  </td></tr>
  <tr valign="top"><td colspan="2" width="100%"><hr></td></tr>
  <!--<div class="sezione">Orari</div></td></tr>-->
  </table>
  <table>
  <%
  for (i=0;i<vectRichiesta.size();i++) {
    rigaRichiestaFisse = (SourceBean) vectRichiesta.elementAt(i);
    strDescOrario = rigaRichiestaFisse.containsAttribute("STRDESCRIZIONE")?rigaRichiestaFisse.getAttribute("STRDESCRIZIONE").toString():"";
    %>
    <tr valign="top">
    <td class="etichetta2">Orario</td>
    <td class="campo2"><b><%=strDescOrario%></b></td>
    </tr>
    <%
  }
  %>
  <tr><td></td></tr>
  </table>
  <%  
}

vectRichiesta = null;
rigaRichiestaFisse = null;
String strDescTurno = "";
//Lista dei turni della richiesta
vectRichiesta=serviceResponse.getAttributeAsVector("M_GetTurniRichiesta_Dettaglio_Sintetico.ROWS.ROW");
if (vectRichiesta.size() > 0) {
  if (!bTabellaCreata) {
    bTabellaCreata = true;
    out.print(htmlStreamTop);
    %>
    <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
    <tr valign="top"><td>
    <%
  }
  %>
  <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
  <tr valign="bottom"><td colspan="2">
  <font class="titoloSezioni">Turni</font>
  </td></tr>
  <tr valign="top"><td colspan="2" width="100%"><hr></td></tr>
  <!--<div class="sezione">Turni</div></td></tr>-->
  </table>
  <table>
  <%
  for (i=0;i<vectRichiesta.size();i++) {
    rigaRichiestaFisse = (SourceBean) vectRichiesta.elementAt(i);
    strDescTurno = rigaRichiestaFisse.containsAttribute("STRDESCRIZIONE")?rigaRichiestaFisse.getAttribute("STRDESCRIZIONE").toString():"";
    %>
    <tr valign="top">
    <td class="etichetta2">Descrizione</td>
    <td class="campo2"><b><%=strDescTurno%></b></td>
    </tr>
    <%
  }
  %>
  <tr><td></td></tr>
  </table>
  <%
}

vectRichiesta = null;
rigaRichiestaFisse = null;
//Lista dei territori della richiesta
vectRichiesta=serviceResponse.getAttributeAsVector("MListaTerritoriRichiestaDettaglioSintetico.ROWS.ROW");
if (vectRichiesta.size() > 0) {
  if (!bTabellaCreata) {
    bTabellaCreata = true;
    out.print(htmlStreamTop);
    %>
    <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
    <tr valign="top"><td>
    <%
  }
  %>
  <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
  <tr valign="bottom"><td colspan="2">
  <font class="titoloSezioni">Territori</font>
  </td></tr>
  <tr valign="top"><td colspan="2" width="100%"><hr></td></tr>
  <!--<div class="sezione">Territori</div></td></tr>-->
  </table>
  <table>
  <%
  for (i=0;i<vectRichiesta.size();i++) {
    rigaRichiestaFisse = (SourceBean) vectRichiesta.elementAt(i);
    String strTipoTerr = SourceBeanUtils.getAttrStrNotNull(rigaRichiestaFisse, "TIPOTERR");
    String strDescTerr = SourceBeanUtils.getAttrStrNotNull(rigaRichiestaFisse, "DESCRIZIONETERRITORIO");
    %>
    <tr valign="top">
    <td class="etichetta2">
    	<%
    	if      (strTipoTerr.equalsIgnoreCase("C")) out.print("Comune di");
    	else if (strTipoTerr.equalsIgnoreCase("P")) out.print("Provincia di");
    	else                                        out.print("Localit&agrave;");
    	%>
    </td>
    <td class="campo2"><b><%=strDescTerr%></b></td>
    </tr>
    <%
  }
  %>
  <tr><td></td></tr>
  </table>
  <%  
}

vectRichiesta = null;
rigaRichiestaFisse = null;
String strDescCittadinanza = "";
String strMotivCittadinanza = "";
//Lista delle cittadinanze della richiesta
vectRichiesta=serviceResponse.getAttributeAsVector("MListaCittadinanzaRichiestaDettaglio.ROWS.ROW");
if (vectRichiesta.size() > 0) {
  if (!bTabellaCreata) {
    bTabellaCreata = true;
    out.print(htmlStreamTop);
    %>
    <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
    <tr valign="top"><td>
    <%
  }
  %>
  <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
  <tr valign="bottom"><td colspan="4">
  <font class="titoloSezioni">Nazionalità</font>
  </td></tr>
  <tr valign="top"><td colspan="4" width="100%"><hr></td></tr>
  <!--<div class="sezione">Nazionalità</div></td></tr>-->
  </table>
  <table>
  <%
  for (i=0;i<vectRichiesta.size();i++) {
    rigaRichiestaFisse = (SourceBean) vectRichiesta.elementAt(i);
    strDescCittadinanza = rigaRichiestaFisse.containsAttribute("STRDESCRIZIONE")?rigaRichiestaFisse.getAttribute("STRDESCRIZIONE").toString():"";
    strMotivCittadinanza = rigaRichiestaFisse.containsAttribute("STRMOTIVAZIONE")?rigaRichiestaFisse.getAttribute("STRMOTIVAZIONE").toString():"";
    %>
    <tr valign="top">
    <td class="etichetta2">Cittadinanza</td>
    <td class="campo2"><b><%=strDescCittadinanza%></b></td>
    <td class="etichetta2">Motivazione</td>
    <td class="campo2"><b><%=strMotivCittadinanza%></b></td>
    </tr>
    <%
  }
  %>
  <tr><td></td></tr>
  </table>
  <%  
}

if (bTabellaCreata) {
  bTabellaCreata = false;
  %>
  </td></tr></table>
  <%
  out.print(htmlStreamBottom);
}

int nColonneEsperienza = 10;
vectRichiesta = null;
SourceBean rigaAlternativeEsperienze = null;
SourceBean rigaAlternativeStudi = null;
SourceBean rigaAlternativeFormProf = null;
SourceBean rigaAlternativeInfo = null;
SourceBean rigaAlternativeLingua = null;
SourceBean rigaAlternativeCompetenze = null;
SourceBean rigaAlternativeContratti = null;
SourceBean rigaAlternativeAgevolazioni = null;
SourceBean rigaAlternativeMansioni = null;

String numEsperienzaDa = "";
String numEsperienzaA = "";
String codMotEta = "";
String strAltraMotivazione = "";
String flgEsperienza = "";
String numAnniEsperienza = "";
String strPrgAlternativaEsp = "";
String strPrgAlternativaAgevolazioni = "";
String strPrgAlternativaMansioni = "";

String descTitolo = "";
String strPrgAlternativaStudi = "";
String descSpecifica = "";
String flgTitoloConseguito = "";
String flgTitoloIndispensabile = "";
boolean bTableCreata = false;
String strPrgAlternativaInfo = "";
String strPrgAlternativaLingua = "";
String strDescTipoConoInfo = "";
String strDescGradoCono = "";
String flgConoIndispensabile = "";
String flgLinguaIndispensabile = "";
String strDescConoInfo = "";
String strPrgAlternativaCompetenze = "";
String strDescCompetenza = "";
String flgIndispensabileComp = "";
String strPrgAlternativaContratti = "";
String strDescContratti = "";
String strDescAgevolazioni = "";
String flgAgevolazioneIndisp = "";
String strLinguaRichiesta = "";
String strGradoLetto = "";
String strGradoScritto = "";
String strGradoParlato = "";
String strDescrizione = "";
String flgCorsoIndispensabile = "";
String strPrgFormazioneProfessionale = "";

//Lista delle alternative della richiesta
Vector vectRichiestaEsperienze=serviceResponse.getAttributeAsVector("MListaEsperienzeDettaglioSintetico.ROWS.ROW");
String strTipoMansione = "";
String strDescMansione = "";
Vector vectRichiestaMansione=serviceResponse.getAttributeAsVector("M_LIST_IDO_MANSIONI_DETTAGLIO_SINTETICO.ROWS.ROW");
Vector vectFormazioneProfessionale=serviceResponse.getAttributeAsVector("M_GetFormazioneProfessionaleDettaglioSintetico.ROWS.ROW");

Vector vectRichiestaStudi=serviceResponse.getAttributeAsVector("M_GetStudiRichiestaDettaglioSintetico.ROWS.ROW");
Vector vectRichiestaInfo=serviceResponse.getAttributeAsVector("M_GetInfoRichiestaDettaglioSintetico.ROWS.ROW");
Vector vectRichiestaLingue=serviceResponse.getAttributeAsVector("M_GetLingueRichiestaDettaglioSintetico.ROWS.ROW");
Vector vectRichiestaCompetenze=serviceResponse.getAttributeAsVector("M_GetCompetenzeRichiestaDettaglioSintetico.ROWS.ROW");
Vector vectRichiestaContratti=serviceResponse.getAttributeAsVector("M_GetContrattiRichiestaDettaglioSintetico.ROWS.ROW");
Vector vectRichiestaAgevolazioni=serviceResponse.getAttributeAsVector("M_GetAgevolazioniRichiestaDettaglioSintetico.ROWS.ROW");

for (i=0;i<vectRichiestaEsperienze.size();i++) {
  //Esperienze
  bTabellaCreata = false;
  nColonne = 0;
  rigaAlternativeEsperienze = (SourceBean) vectRichiestaEsperienze.elementAt(i);
  strPrgAlternativaEsp = rigaAlternativeEsperienze.getAttribute("prgAlternativa").toString();
  numEsperienzaDa = rigaAlternativeEsperienze.containsAttribute("numda")?rigaAlternativeEsperienze.getAttribute("numda").toString():"";
  numEsperienzaA = rigaAlternativeEsperienze.containsAttribute("numa")?rigaAlternativeEsperienze.getAttribute("numa").toString():"";
  flgEsperienza  = rigaAlternativeEsperienze.containsAttribute("flgEsperienza")?rigaAlternativeEsperienze.getAttribute("flgEsperienza").toString():"";
  numAnniEsperienza = rigaAlternativeEsperienze.containsAttribute("numAnniEsperienza")?rigaAlternativeEsperienze.getAttribute("numAnniEsperienza").toString():"";
  codMotEta = rigaAlternativeEsperienze.containsAttribute("codMotEta")?rigaAlternativeEsperienze.getAttribute("codMotEta").toString():"";
  strAltraMotivazione = rigaAlternativeEsperienze.containsAttribute("strMotiveta")?rigaAlternativeEsperienze.getAttribute("strMotiveta").toString():"";

  if ((!numEsperienzaDa.equals("")) ||
      (!numEsperienzaA.equals("")) ||
      (!flgEsperienza.equals("")) || 
      (!numAnniEsperienza.equals(""))) {
    bTabellaCreata = true;
  %>
    <p class="titolo">Profilo n.<%=strPrgAlternativaEsp%></p>
    <%
    out.print(htmlStreamTop);
    %>
    <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
    <tr valign="top"><td>
    <%
    if (!bTableCreata) {
      bTableCreata = true;
    %>
      <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
      <tr valign="bottom"><td colspan="<%=nColonneEsperienza%>">
      <font class="titoloSezioni">Esperienze</font>
      </td></tr>
      <tr valign="top"><td colspan="<%=nColonneEsperienza%>" width="100%"><hr></td></tr>
      <!--<div class="sezione">Esperienze</div></td></tr>-->
      </table>
      <table>
      <%
    }

    if (flgEsperienza.toUpperCase().equals("N")) {
      flgEsperienza = "No";
    }
    else {
      if (flgEsperienza.toUpperCase().equals("S")) {  
        flgEsperienza = "Sì";
      }
    }
    %>
    <tr valign="top">
    <%
    
    if (!numEsperienzaDa.equals("")) {
      nColonne = nColonne + 2;
    %>
      <td class="etichetta2">Età Da</td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="camponumEsperienzaDa" size="5" value="<%=numEsperienzaDa%>" readonly="true"/>
      </td>
    <%
    }
    
    if (!numEsperienzaA.equals("")) {
      nColonne = nColonne + 2;
    %>
      <td class="etichetta2">Età A</td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="camponumEsperienzaA" size="5" value="<%=numEsperienzaA%>" readonly="true"/>
      </td>
    <%
    }
     
    if (!flgEsperienza.equals("")) {
      nColonne = nColonne + 2;
    %>
      <td class="etichetta2">Esperienza</td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="flgEsperienzaRich" size="5" value="<%=flgEsperienza%>" readonly="true"/>
      </td>
    <%
    }
    if (!numAnniEsperienza.equals("")) {
      nColonne = nColonne + 2;
    %>
      <td class="etichetta2">Anni di Esperienza</td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="numAnniEsperienzaRich" size="5" value="<%=numAnniEsperienza%>" readonly="true"/>
      </td>
    <%
    }
    if (nColonne < nColonneEsperienza) {
    %>
      <td colspan="<%=nColonneEsperienza - nColonne%>">&nbsp;</td>
    <%
    }
    %>
    </tr>
  <%
  }

  if (bTableCreata) {
    bTableCreata = false;
  %>
    <tr><td></td></tr>
    </table>
  <%
  }


  //Mansioni
  if (vectRichiestaMansione.size() > 0) {
    for (int j=0;j<vectRichiestaMansione.size();j++) {
      rigaAlternativeMansioni = (SourceBean) vectRichiestaMansione.elementAt(j);
      strPrgAlternativaMansioni = rigaAlternativeMansioni.getAttribute("prgAlternativa").toString();
      if (strPrgAlternativaMansioni.equals(strPrgAlternativaEsp)) {
        if (!bTabellaCreata) {
          bTabellaCreata = true;
        %>
          <p class="titolo">Profilo n.<%=strPrgAlternativaEsp%></p>
          <%
          out.print(htmlStreamTop);
          %>
          <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="top"><td>
        <%  
        }

        if (!bTableCreata) {
          bTableCreata = true;
        %>
          <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="bottom"><td colspan="<%=nColonneEsperienza%>">
          <font class="titoloSezioni">Ambito professionale</font>
          </td></tr>
          <tr valign="top"><td colspan="<%=nColonneEsperienza%>" width="100%"><hr></td></tr>
          </table>
          <table>
        <%
        }
        %>
        <tr valign="top">
        <%
        strTipoMansione = rigaAlternativeMansioni.containsAttribute("desTipoMansione")?rigaAlternativeMansioni.getAttribute("desTipoMansione").toString():"";
        strDescMansione = rigaAlternativeMansioni.containsAttribute("desMansione")?rigaAlternativeMansioni.getAttribute("desMansione").toString():"";
        if (!strTipoMansione.equals("")) {
        %>
          <td class="etichetta2">Tipo</td>
          <td class="campo2"><b><%=strTipoMansione%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        if (!strDescMansione.equals("")) {
        %>
          <td class="etichetta2">Mansione</td>
          <td class="campo2"><b><%=strDescMansione%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        %>
        </tr>
        <%
      }
    }
  }
  
  if (bTableCreata) {
    bTableCreata = false;
  %>
    <tr><td></td></tr>
    </table>
  <%
  }
  
  //Formazione Professionale
  if (vectFormazioneProfessionale.size() > 0) {
    for (int j=0;j<vectFormazioneProfessionale.size();j++) {
      rigaAlternativeFormProf = (SourceBean) vectFormazioneProfessionale.elementAt(j);
      strPrgFormazioneProfessionale = rigaAlternativeFormProf.getAttribute("prgAlternativa").toString();
      if (strPrgFormazioneProfessionale.equals(strPrgAlternativaEsp)) {
        if (!bTabellaCreata) {
          bTabellaCreata = true;
        %>
          <p class="titolo">Profilo n.<%=strPrgFormazioneProfessionale%></p>
          <%
          out.print(htmlStreamTop);
          %>
          <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="top"><td>
        <%  
        }

        if (!bTableCreata) {
          bTableCreata = true;
        %>
          <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="bottom"><td colspan="<%=nColonneEsperienza%>">
          <font class="titoloSezioni">Formazione professionale</font>
          </td></tr>
          <tr valign="top"><td colspan="<%=nColonneEsperienza%>" width="100%"><hr></td></tr>
          </table>
          <table>
        <%
        }
        %>
        <tr valign="top">
        <%
        strDescrizione = rigaAlternativeFormProf.containsAttribute("STRDESCRIZIONE")?rigaAlternativeFormProf.getAttribute("STRDESCRIZIONE").toString():"";
        flgCorsoIndispensabile = rigaAlternativeFormProf.containsAttribute("INDISPENSABILE")?rigaAlternativeFormProf.getAttribute("INDISPENSABILE").toString():"";
        if (flgCorsoIndispensabile.toUpperCase().equals("S")) {
        	flgCorsoIndispensabile = "Sì"; 
        }
        else {          
        	flgCorsoIndispensabile = "No";
        }
        if (!strDescrizione.equals("")) {
        %>
          <td class="etichetta2">Descrizione</td>
          <td class="campo2"><b><%=strDescrizione%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        if (!flgCorsoIndispensabile.equals("")) {
        %>
          <td class="etichetta2">Indispensabile</td>
          <td class="campo2"><b><%=flgCorsoIndispensabile%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        %>
        </tr>
        <%
      }
    }
  }
  if (bTableCreata) {
	    bTableCreata = false;
	  %>
	    <tr><td></td></tr>
	    </table>
	  <%
	  }
  
  //Studi
  if (vectRichiestaStudi.size() > 0) {
    for (int j=0;j<vectRichiestaStudi.size();j++) {
      rigaAlternativeStudi = (SourceBean) vectRichiestaStudi.elementAt(j);
      strPrgAlternativaStudi = rigaAlternativeStudi.getAttribute("prgAlternativa").toString();
      if (strPrgAlternativaStudi.equals(strPrgAlternativaEsp)) {
        if (!bTabellaCreata) {
          bTabellaCreata = true;
        %>
          <p class="titolo">Profilo n.<%=strPrgAlternativaEsp%></p>
          <%
          out.print(htmlStreamTop);
          %>
          <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="top"><td>
        <%  
        }

        if (!bTableCreata) {
          bTableCreata = true;
        %>
          <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="bottom"><td colspan="<%=nColonneEsperienza%>">
          <font class="titoloSezioni">Studi</font>
          </td></tr>
          <tr valign="top"><td colspan="<%=nColonneEsperienza%>" width="100%"><hr></td></tr>
          <!--<div class="sezione">Studi</div></td></tr>-->
          </table>
          <table>
        <%
        }
        %>
        <tr valign="top">
        <%
        descTitolo = rigaAlternativeStudi.containsAttribute("DESCTITOLO")?rigaAlternativeStudi.getAttribute("DESCTITOLO").toString():"";
        descSpecifica = rigaAlternativeStudi.containsAttribute("SPECIFICA")?rigaAlternativeStudi.getAttribute("SPECIFICA").toString():"";
        flgTitoloConseguito = rigaAlternativeStudi.containsAttribute("CONSEGUITO")?rigaAlternativeStudi.getAttribute("CONSEGUITO").toString():"";
        flgTitoloIndispensabile = rigaAlternativeStudi.containsAttribute("INDISPENSABILE")?rigaAlternativeStudi.getAttribute("INDISPENSABILE").toString():"";
        if (flgTitoloIndispensabile.toUpperCase().equals("S")) {
          flgTitoloIndispensabile = "Sì"; 
        }
        else {
          if (flgTitoloIndispensabile.toUpperCase().equals("N")) {
            flgTitoloIndispensabile = "No";
          }
        }
        if (flgTitoloConseguito.toUpperCase().equals("S")) {
          flgTitoloConseguito = "Sì"; 
        }
        else {
          if (flgTitoloConseguito.toUpperCase().equals("N")) {
            flgTitoloConseguito = "No";
          }
        }
      
        if (!descTitolo.equals("")) {
        %>
          <td class="etichetta2">Titolo</td>
          <td class="campo2"><b><%=descTitolo%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        if (!descSpecifica.equals("")) {
        %>
          <td class="etichetta2">Specifica</td>
          <td class="campo2"><b><%=descSpecifica%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        if (!flgTitoloConseguito.equals("")) {
        %>
          <td class="etichetta2">Conseguito</td>
          <td class="campo2"><b><%=flgTitoloConseguito%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        if (!flgTitoloIndispensabile.equals("")) {
        %>
          <td class="etichetta2">Indispensabile</td>
          <td class="campo2"><b><%=flgTitoloIndispensabile%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        %>
        </tr>
        <%
      }
    }
  }
  
  if (bTableCreata) {
    bTableCreata = false;
  %>
    <tr><td></td></tr>
    </table>
  <%
  }
    
  //Informatica
  if (vectRichiestaInfo.size() > 0) {
    for (int j=0;j<vectRichiestaInfo.size();j++) {
      rigaAlternativeInfo = (SourceBean) vectRichiestaInfo.elementAt(j);
      strPrgAlternativaInfo = rigaAlternativeInfo.getAttribute("prgAlternativa").toString();
      if (strPrgAlternativaInfo.equals(strPrgAlternativaEsp)) {
        if (!bTabellaCreata) {
          bTabellaCreata = true;
        %>
          <p class="titolo">Profilo n.<%=strPrgAlternativaEsp%></p>
          <%
          out.print(htmlStreamTop);
          %>
          <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="top"><td>
        <%  
        }
        if (!bTableCreata) {
          bTableCreata = true;
        %>
          <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="bottom"><td colspan="<%=nColonneEsperienza%>">
          <font class="titoloSezioni">Informatica</font>
          </td></tr>
          <tr valign="top"><td colspan="<%=nColonneEsperienza%>" width="100%"><hr></td></tr>
          <!--<div class="sezione">Informatica</div></td></tr>-->
          </table>
          <table>
        <%
        }
        %>
        <tr valign="top">
        <%
        strDescTipoConoInfo = rigaAlternativeInfo.containsAttribute("DESCRIZIONETIPO")?rigaAlternativeInfo.getAttribute("DESCRIZIONETIPO").toString():"";
        strDescConoInfo = rigaAlternativeInfo.containsAttribute("DESCRIZIONEDETT")?rigaAlternativeInfo.getAttribute("DESCRIZIONEDETT").toString():"";
        strDescGradoCono = rigaAlternativeInfo.containsAttribute("DESCRIZIONEGRADO")?rigaAlternativeInfo.getAttribute("DESCRIZIONEGRADO").toString():"";
        flgConoIndispensabile = rigaAlternativeInfo.containsAttribute("FLGINDISPENSABILE")?rigaAlternativeInfo.getAttribute("FLGINDISPENSABILE").toString():"";

        if (flgConoIndispensabile.toUpperCase().equals("S")) {
          flgConoIndispensabile = "Sì"; 
        }
        else {
          if (flgConoIndispensabile.toUpperCase().equals("N")) {
            flgConoIndispensabile = "No";
          }
        }
      
        if (!strDescTipoConoInfo.equals("")) {
        %>
          <td class="etichetta2">Tipo</td>
          <td class="campo2"><b><%=strDescTipoConoInfo%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        if (!strDescConoInfo.equals("")) {
        %>
          <td class="etichetta2">Dettaglio</td>
          <td class="campo2"><b><%=strDescConoInfo%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        if (!strDescGradoCono.equals("")) {
        %>
          <td class="etichetta2">Livello</td>
          <td class="campo2"><b><%=strDescGradoCono%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        if (!flgConoIndispensabile.equals("")) {
        %>
          <td class="etichetta2">Indispensabile</td>
          <td class="campo2"><b><%=flgConoIndispensabile%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        %>
        </tr>
        <%
      } 
    }
  }
  if (bTableCreata) {
    bTableCreata = false;
  %>
    <tr><td></td></tr>
    </table>
  <%
  }

  //Lingue
  strFlgIndispensabile = "";
  if (vectRichiestaLingue.size() > 0) {
    for (int j=0;j<vectRichiestaLingue.size();j++) {
      rigaAlternativeLingua = (SourceBean) vectRichiestaLingue.elementAt(j);
      strPrgAlternativaLingua = rigaAlternativeLingua.getAttribute("prgAlternativa").toString();
      if (strPrgAlternativaLingua.equals(strPrgAlternativaEsp)) {
        if (!bTabellaCreata) {
          bTabellaCreata = true;
        %>
          <p class="titolo">Profilo n.<%=strPrgAlternativaEsp%></p>
          <%
          out.print(htmlStreamTop);
          %>
          <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="top"><td>
        <%  
        }
        if (!bTableCreata) {
          bTableCreata = true;
        %>
          <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="bottom"><td colspan="<%=nColonneEsperienza%>">
          <font class="titoloSezioni">Lingue</font>
          </td></tr>
          <tr valign="top"><td colspan="<%=nColonneEsperienza%>" width="100%"><hr></td></tr>
          <!--<div class="sezione">Informatica</div></td></tr>-->
          </table>
          <table>
        <%
        }
        %>
        <tr valign="top">
        <%
        strLinguaRichiesta = rigaAlternativeLingua.containsAttribute("STRDENOMINAZIONE")?rigaAlternativeLingua.getAttribute("STRDENOMINAZIONE").toString():"";
        strGradoLetto = rigaAlternativeLingua.containsAttribute("DescrizioneLetto")?rigaAlternativeLingua.getAttribute("DescrizioneLetto").toString():"";
        strGradoScritto = rigaAlternativeLingua.containsAttribute("DescrizioneScritto")?rigaAlternativeLingua.getAttribute("DescrizioneScritto").toString():"";
        strGradoParlato = rigaAlternativeLingua.containsAttribute("DescrizioneParlato")?rigaAlternativeLingua.getAttribute("DescrizioneParlato").toString():"";
        strFlgIndispensabile = rigaAlternativeLingua.containsAttribute("FLGINDISPENSABILE")?rigaAlternativeLingua.getAttribute("FLGINDISPENSABILE").toString():"";
        if (strFlgIndispensabile.toUpperCase().equals("S")) {
          flgLinguaIndispensabile = "Sì"; 
        }
        else {
          if (strFlgIndispensabile.toUpperCase().equals("N")) {
            flgLinguaIndispensabile = "No";
          }
        }
        %>
        <td class="etichetta2">Lingua</td>
        <td class="campo2"><b><%=strLinguaRichiesta%></b></td>
        <td class="etichetta2">Grado Letto</td>
        <td class="campo2"><b><%=strGradoLetto%></b></td>
        <td class="etichetta2">Grado Scritto</td>
        <td class="campo2"><b><%=strGradoScritto%></b></td>
        <td class="etichetta2">Grado Parlato</td>
        <td class="campo2"><b><%=strGradoParlato%></b></td>
        <%
        if (!strFlgIndispensabile.equals("")) {
        %>
          <td class="etichetta2">Indispensabile</td>
          <td class="campo2"><b><%=flgLinguaIndispensabile%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        %>
        </tr>
        <%
      } 
    }
  }
  if (bTableCreata) {
    bTableCreata = false;
  %>
    <tr><td></td></tr>
    </table>
  <%
  }

  //Competenze
  if (vectRichiestaCompetenze.size() > 0) {
    for (int j=0;j<vectRichiestaCompetenze.size();j++) {
      rigaAlternativeCompetenze = (SourceBean) vectRichiestaCompetenze.elementAt(j);
      strPrgAlternativaCompetenze = rigaAlternativeCompetenze.getAttribute("prgAlternativa").toString();
      if (strPrgAlternativaCompetenze.equals(strPrgAlternativaEsp)) {
        if (!bTabellaCreata) {
          bTabellaCreata = true;
        %>
          <p class="titolo">Profilo n.<%=strPrgAlternativaEsp%></p>
          <%
          out.print(htmlStreamTop);
          %>
          <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="top"><td>
        <%  
        }
        if (!bTableCreata) {
          bTableCreata = true;
        %>
          <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="bottom"><td colspan="<%=nColonneEsperienza%>">
          <font class="titoloSezioni">Competenze</font>
          </td></tr>
          <tr valign="top"><td colspan="<%=nColonneEsperienza%>" width="100%"><hr></td></tr>
          <!--<div class="sezione">Competenze</div></td></tr>-->
          </table>
          <table>
        <%
        }
        %>
        <tr valign="top">
        <%
        strDescCompetenza = rigaAlternativeCompetenze.containsAttribute("DESCRIZIONECOMPETENZA")?rigaAlternativeCompetenze.getAttribute("DESCRIZIONECOMPETENZA").toString():"";
        flgIndispensabileComp = rigaAlternativeCompetenze.containsAttribute("FLGINDISPENSABILE")?rigaAlternativeCompetenze.getAttribute("FLGINDISPENSABILE").toString():"";
        
        if (flgIndispensabileComp.toUpperCase().equals("S")) {
          flgIndispensabileComp = "Sì"; 
        }
        else {
          if (flgIndispensabileComp.toUpperCase().equals("N")) {
            flgIndispensabileComp = "No";
          }
        }
      
        if (!strDescCompetenza.equals("")) {
        %>
          <td class="etichetta2">Tipo</td>
          <td class="campo2"><b><%=strDescCompetenza%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        if (!flgIndispensabileComp.equals("")) {
        %>
          <td class="etichetta2">Indispensabile</td>
          <td class="campo2"><b><%=flgIndispensabileComp%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        %>
        </tr>
        <%
      } 
    }
  }
  if (bTableCreata) {
    bTableCreata = false;
  %>
    <tr><td></td></tr>
    </table>
  <%
  }
  
  //Tipologia rapporto
  if (vectRichiestaContratti.size() > 0) {
    for (int j=0;j<vectRichiestaContratti.size();j++) {
      rigaAlternativeContratti = (SourceBean) vectRichiestaContratti.elementAt(j);
      strPrgAlternativaContratti = rigaAlternativeContratti.getAttribute("prgAlternativa").toString();
      if (strPrgAlternativaContratti.equals(strPrgAlternativaEsp)) {
        if (!bTabellaCreata) {
          bTabellaCreata = true;
        %>
          <p class="titolo">Profilo n.<%=strPrgAlternativaEsp%></p>
          <%
          out.print(htmlStreamTop);
          %>
          <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="top"><td>
        <%  
        }
        if (!bTableCreata) {
          bTableCreata = true;
        %>
          <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="bottom"><td colspan="<%=nColonneEsperienza%>">
          <font class="titoloSezioni">Tipologia rapporto</font>
          </td></tr>
          <tr valign="top"><td colspan="<%=nColonneEsperienza%>" width="100%"><hr></td></tr>
          <!--<div class="sezione">Tipologia rapporto</div></td></tr>-->
          </table>
          <table>
        <%
        }
        %>
        <tr valign="top">
        <%
        strDescContratti = rigaAlternativeContratti.containsAttribute("STRDESCRIZIONE")?rigaAlternativeContratti.getAttribute("STRDESCRIZIONE").toString():"";
        if (!strDescContratti.equals("")) {
        %>
          <td class="etichetta2">Tipologia</td>
          <td class="campo2"><b><%=strDescContratti%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        %>
        </tr>
        <%
      } 
    }
  }
  if (bTableCreata) {
    bTableCreata = false;
  %>
    <tr><td></td></tr>
    </table>
  <%
  }

  //Agevolazioni
  if (vectRichiestaAgevolazioni.size() > 0) {
    for (int j=0;j<vectRichiestaAgevolazioni.size();j++) {
      rigaAlternativeAgevolazioni = (SourceBean) vectRichiestaAgevolazioni.elementAt(j);
      strPrgAlternativaAgevolazioni = rigaAlternativeAgevolazioni.getAttribute("prgAlternativa").toString();
      if (strPrgAlternativaAgevolazioni.equals(strPrgAlternativaEsp)) {
        if (!bTabellaCreata) {
          bTabellaCreata = true;
        %>
          <p class="titolo">Profilo n.<%=strPrgAlternativaEsp%></p>
          <%
          out.print(htmlStreamTop);
          %>
          <table width="100%" border="0" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="top"><td>
        <%  
        }
        if (!bTableCreata) {
          bTableCreata = true;
        %>        
          <table border="0" width="100%" margin="0" cellspacing="0" cellpadding="0">
          <tr valign="bottom"><td colspan="<%=nColonneEsperienza%>">
          <font class="titoloSezioni">Agevolazioni</font>
          </td></tr>
          <tr valign="top"><td colspan="<%=nColonneEsperienza%>" width="100%"><hr></td></tr>
          <!--<div class="sezione">Agevolazioni</div></td></tr>-->
          </table>
          <table>
        <%
        }
        %>
        <tr valign="top">
        <%
        strDescAgevolazioni = rigaAlternativeAgevolazioni.containsAttribute("STRDESCRIZIONE")?rigaAlternativeAgevolazioni.getAttribute("STRDESCRIZIONE").toString():"";
        flgAgevolazioneIndisp = rigaAlternativeAgevolazioni.containsAttribute("FLGINDISPENSABILE")?rigaAlternativeAgevolazioni.getAttribute("FLGINDISPENSABILE").toString():"";

        if (flgAgevolazioneIndisp.toUpperCase().equals("S")) {
          flgAgevolazioneIndisp = "Sì"; 
        }
        else {
          if (flgAgevolazioneIndisp.toUpperCase().equals("N")) {
            flgAgevolazioneIndisp = "No";
          }
        }
        if (!strDescAgevolazioni.equals("")) {
        %>
          <td class="etichetta2">Tipologia</td>
          <td class="campo2"><b><%=strDescAgevolazioni%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        if (!flgAgevolazioneIndisp.equals("")) {
        %>
          <td class="etichetta2">Indispensabile</td>
          <td class="campo2"><b><%=flgAgevolazioneIndisp%></b></td>
        <%
        }
        else {
        %>
          <td colspan="2">&nbsp;</td>
        <%
        }
        %>
        </tr>
        <%
      } 
    }
  }
  if (bTableCreata) {
    bTableCreata = false;
  %>
    <tr><td></td></tr>
    </table>
  <%
  }

  if (bTabellaCreata) {
    %>
    </td></tr></table>
    <%
    out.print(htmlStreamBottom);
  }
}
%>
<br>
<center>
	<% operatoreInfo.showHTML(out);%>	
	<br>
<%
if (strPopUp.equals("")) {
%>
  <input class="pulsante" type="button" name="back" value="<%=valueBtnChiudi%>" onclick="goBack();"/>
<%
}
else {
%>
  <input class="pulsante" type="button" name="back" value="<%=valueBtnChiudi%>" onclick="javascript:window.close();"/>
<%
}
%>
<center>
<br>
</body>
</html>