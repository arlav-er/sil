<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
  it.eng.sil.util.*, 
  it.eng.afExt.utils.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
boolean canModify = false;
boolean bRigaCreataDatiGenerali = false;
int nColonne = 0;
//dichiarazioni dati generali
SourceBean richiestaRow = null;
String numRichiestaAnno = "";
String numAnnoRichiesta = "";
String datRichiesta = "";
String datScadenza = "";
String codCpi = "";
String descCpi = "";
String codTrasferta = "";
String descTrasferta = "";
String datPubblicazione = "";
String datScadenzaPubblicazione = "";

// Campi per la Pubblicazione
String codQualifica = "";
/* NON USATO:
	boolean flgPubbPalese = false;
	boolean flgPubbAnonima = false;
	String flgPubbWeb="";
	String flgPubbGiornali="";
	String flgPubbBacheca="";
*/
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
String descArea = "";
String flgFuoriSede = "";
String flgCodifica = "";
String strLocalita = "";
String flgVittoAlloggio = "";
String strSesso = "";
String strMotivSesso = "";
String cdnUtIns = "";
String dtmIns = "";
String cdnUtMod = "";
String dtmMod = "";
String descMacroQualifica = "";
String indCpi = "";
String telCpi = "";
String faxCpi = "";
String emailCpi = "";
String codEvasione = "";
String modEvasione = "";

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

String cdnUt = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNUT");
int i;
String strRif = "";

String emailAzienda = "",
 	descrizioneAzienda="";
BigDecimal	numeroRichiesta = null, 
	 numeroAnno = null;

 	
//Dati generali della richiesta
richiestaRow = (SourceBean) serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW");
if (richiestaRow != null) {
  prgRichiestaAZ = StringUtils.getAttributeStrNotNull(richiestaRow, "PRGRICHIESTAAZ");
  numRichiestaAnno = StringUtils.getAttributeStrNotNull(richiestaRow, "NUMRICHIESTAORIG");
  numAnnoRichiesta = StringUtils.getAttributeStrNotNull(richiestaRow, "NUMANNO");
  datRichiesta = StringUtils.getAttributeStrNotNull(richiestaRow, "datRichiesta");
  datScadenza = StringUtils.getAttributeStrNotNull(richiestaRow, "datScadenza");
  codCpi = StringUtils.getAttributeStrNotNull(richiestaRow, "codCpi");
  descCpi = StringUtils.getAttributeStrNotNull(richiestaRow, "descCpi");
  indCpi = StringUtils.getAttributeStrNotNull(richiestaRow, "indCpi");
  telCpi = StringUtils.getAttributeStrNotNull(richiestaRow, "telCpi");
  faxCpi = StringUtils.getAttributeStrNotNull(richiestaRow, "faxCpi");
  emailCpi = StringUtils.getAttributeStrNotNull(richiestaRow, "emailCpi");
  codTrasferta = StringUtils.getAttributeStrNotNull(richiestaRow, "codTrasferta");
  descTrasferta = StringUtils.getAttributeStrNotNull(richiestaRow, "descTrasferta");
  datPubblicazione = StringUtils.getAttributeStrNotNull(richiestaRow, "datPubblicazione");
  datScadenzaPubblicazione = StringUtils.getAttributeStrNotNull(richiestaRow, "datScadenzaPubblicazione");
  
  // Campi per la Pubblicazione
  codQualifica              = StringUtils.getAttributeStrNotNull(richiestaRow, "codQualifica");
  /* NON USATO:  (nel caso servisse, aggiungere anche il modulo M_IdoGetStatoRich di SIL)
	  SourceBean evasioneRow = (SourceBean) serviceResponse.getAttribute("M_IdoGetStatoRich.ROWS.ROW");
	  if (evasioneRow != null) {
	    String codEvasionePubbl = StringUtils.getAttributeStrNotNull(evasioneRow, "codEvasione");
	    flgPubbPalese   = (codEvasionePubbl.equalsIgnoreCase("DFD") || codEvasionePubbl.equalsIgnoreCase("DPR"));
	    flgPubbAnonima  = (codEvasionePubbl.equalsIgnoreCase("DFA") || codEvasionePubbl.equalsIgnoreCase("DRA"));
	    flgPubbWeb      = StringUtils.getAttributeStrNotNull(evasioneRow, "flgPubbWeb");
	    flgPubbGiornali = StringUtils.getAttributeStrNotNull(evasioneRow, "flgPubbGiornali");
	    flgPubbBacheca  = StringUtils.getAttributeStrNotNull(evasioneRow, "flgPubbBacheca");  
	  }
  */
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
  
  flgAutomunito  = StringUtils.getAttributeStrNotNull(richiestaRow, "flgAutomunito");
  flgMotomunito = StringUtils.getAttributeStrNotNull(richiestaRow, "flgMotomunito");
  flgFuoriSede = StringUtils.getAttributeStrNotNull(richiestaRow, "flgFuoriSede");  
  strLocalita = StringUtils.getAttributeStrNotNull(richiestaRow, "strLocalita");
  flgMilite = StringUtils.getAttributeStrNotNull(richiestaRow, "flgMilite"); 
  flgPubblicata = StringUtils.getAttributeStrNotNull(richiestaRow, "flgPubblicata");
  numProfRichiesti = StringUtils.getAttributeStrNotNull(richiestaRow, "numProfRichiesti");
  prgAzienda = StringUtils.getAttributeStrNotNull(richiestaRow, "PRGAZIENDA");
  prgUnita = StringUtils.getAttributeStrNotNull(richiestaRow, "PRGUNITA");
  prgSpi = StringUtils.getAttributeStrNotNull(richiestaRow, "prgSpi");
  strCognomeRiferimento = StringUtils.getAttributeStrNotNull(richiestaRow, "strCognomeRiferimento");
  strNomeRiferimento = StringUtils.getAttributeStrNotNull(richiestaRow, "strNomeRiferimento");
  strNominativo = strCognomeRiferimento + " " + strNomeRiferimento;
  strTelRiferimento = StringUtils.getAttributeStrNotNull(richiestaRow, "strTelRiferimento");
  strFaxRiferimento = StringUtils.getAttributeStrNotNull(richiestaRow, "strFaxRiferimento");
  strEmailRiferimento = StringUtils.getAttributeStrNotNull(richiestaRow, "strEmailRiferimento");
  txtFiguraProfessionale = StringUtils.getAttributeStrNotNull(richiestaRow, "txtFiguraProfessionale");  
  txtCaratteristFigProf = StringUtils.getAttributeStrNotNull(richiestaRow, "txtCaratteristFigProf"); 
  txtCondContrattuale = StringUtils.getAttributeStrNotNull(richiestaRow, "txtCondContrattuale");
  codArea = StringUtils.getAttributeStrNotNull(richiestaRow, "codArea"); 
  descArea = StringUtils.getAttributeStrNotNull(richiestaRow, "descArea"); 
  flgFuoriSede = StringUtils.getAttributeStrNotNull(richiestaRow, "flgFuoriSede");
  flgVittoAlloggio = StringUtils.getAttributeStrNotNull(richiestaRow, "flgVittoAlloggio"); 
  strSesso = StringUtils.getAttributeStrNotNull(richiestaRow, "strSesso"); 
  strMotivSesso = StringUtils.getAttributeStrNotNull(richiestaRow, "strMotivSesso");
  cdnUtIns = richiestaRow.getAttribute("cdnUtIns").toString();  
  dtmIns = StringUtils.getAttributeStrNotNull(richiestaRow, "dtmIns");  
  cdnUtMod = richiestaRow.getAttribute("cdnUtMod").toString();  
  dtmMod = StringUtils.getAttributeStrNotNull(richiestaRow, "dtmMod");  
  descMacroQualifica = StringUtils.getAttributeStrNotNull(richiestaRow, "descMacroQualifica");
  codEvasione = StringUtils.getAttributeStrNotNull(richiestaRow, "codEvasione");
  modEvasione = StringUtils.getAttributeStrNotNull(richiestaRow, "modEvasione");
}

SourceBean richiestaEmail = (SourceBean) serviceResponse.getAttribute("M_GetEmail.ROWS.ROW");
if (richiestaEmail != null) {
	emailAzienda = StringUtils.getAttributeStrNotNull(richiestaEmail, "stremailpubbl");
	numeroRichiesta = (BigDecimal) richiestaEmail.getAttribute("numrichiestaorig");
	numeroAnno =  (BigDecimal) richiestaEmail.getAttribute("numanno");
	descrizioneAzienda = StringUtils.getAttributeStrNotNull(richiestaEmail, "strdenominazione");
}
InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAZ);
//infCorrentiAzienda.show(out); 

String token = "";
String urlDiLista = "";
String refListaBtn = "";
if (sessionContainer!=null){
  token = "_TOKEN_" + "WEBLISTAPUBBPAGE";
  urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
  if (urlDiLista!=null) {
    refListaBtn ="<a href=\"AdapterHTTP?" + urlDiLista + "\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></a>";
    //refListaBtn ="<a href=\"AdapterHTTP?" + urlDiLista + "\" class=\"pulsanti\">Torna alla lista</a>";
  }
}

%>
<html>
<head>
	<title>Dettaglio Sintetico Richiesta</title>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
	<af:linkScript path="../../js/"/>
</head>
<body class="gestione">
<!-- Informazioni azienda, occultate se viste "lato cittadino" -->
<%if(!cdnUt.equals("")) { infCorrentiAzienda.show(out); }%>
<br/>
<%out.print(htmlStreamTop);%>
<!--p class="titolo">Dati Richiesta</p-->

<table class="main" border="0" width="100%">
<!--tr>
<td width="25%">&nbsp;</td>
<td width="25%">&nbsp;</td>
<td width="25%">&nbsp;</td>
<td width="25%">&nbsp;</td>
</tr-->
<tr><td colspan="4" class="azzurro_bianco">Dati Richiesta</td></tr>
<tr>
	<td class="etichetta2">Numero richiesta</td>
	<td class="campo2"><b><%=numRichiestaAnno%>/<%=numAnnoRichiesta%></b></td>
	<%-- GG 20-9-04: ELIMINATI I SEG.CAMPI:
	<td class="etichetta2">Data richiesta&nbsp;<b>< %=datRichiesta% ></b></td>
	<td class="etichetta2">Data scadenza&nbsp;<b>< %=datScadenza% ></b></td>
	--%>
</tr>

<tr>
	<td class="etichetta2">Centro per l'impiego</td>
	<td class="campo2" colspan="3"><b><%=descCpi%> - <%=codCpi%></b></td>
</tr>

<tr>
	<td class="etichetta2">Numero profili richiesti</td>
	<td class="campo2"><b><%=numProfRichiesti%></b></td>
	<td colspan="2">&nbsp;</td>
</tr>
<%if(!codArea.equals("")) {%>
	<tr>
		<td class="etichetta2">Area di inserimento</td>
		<td class="campo2"><b><%=descArea%></b></td>
		<td colspan="2">&nbsp;</td>
	</tr>
<%}%>

<tr>
<%
nColonne = 0;
if (!flgFuoriSede.equals("")) {
  nColonne = nColonne + 2;
  if (flgFuoriSede.toUpperCase().equals("N")) {
    flgCodifica = "No";
  }
  else {
    flgCodifica = "S&igrave;";
  }
%>
  <td class="etichetta2">Fuori sede</td>
  <td class="campo2"><b><%=flgCodifica%></b></td>
<%
}
flgCodifica = "";
if (!strLocalita.equals("")) {
  nColonne = nColonne + 2;  
%>
  <td class="etichetta2">Localit&agrave;</td>
  <td class="campo2"><b><%=strLocalita%></b></td>
<%
}

if (!flgAutomunito.equals("")) {
  if (flgAutomunito.toUpperCase().equals("N")) {
    flgCodifica = "No";
  }
  else {
    if (flgAutomunito.toUpperCase().equals("S")) {
      flgCodifica = "S&igrave;";
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
  <td class="campo2"><b><%=flgCodifica%></b></td>
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
  <td class="campo2"><b><%=descTrasferta%></b></td>
  <%
}

if (!flgMotomunito.equals("")) {
  if (flgMotomunito.toUpperCase().equals("N")) {
    flgCodifica = "No";
  }
  else {
    if (flgMotomunito.toUpperCase().equals("S")) {
      flgCodifica = "S&igrave;";
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
  <td class="campo2"><b><%=flgCodifica%></b></td>
  <%
}
flgCodifica = "";

if (!flgVittoAlloggio.equals("")) {
  if (flgVittoAlloggio.toUpperCase().equals("N")) {
    flgCodifica = "No";
  }
  else {  
    flgCodifica = "S&igrave;";
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
  <td class="etichetta2">Disponibilit&agrave; alloggio aziendale</td>
  <td class="campo2"><b><%=flgCodifica%></b></td>
  <%
}
flgCodifica = "";

if (!flgMilite.equals("")) {
  if (flgMilite.toUpperCase().equals("N")) {
    flgCodifica = "No";
  }
  else {
    if (flgMilite.toUpperCase().equals("S")) {
      flgCodifica = "S&igrave;";
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
  <td class="campo2"><b><%=flgCodifica%></b></td>
  <%
}
flgCodifica = "";

if (nColonne == 2) { 
%>
  <td colspan="2"></td>
<%}%>
</tr>

<%if (!strSesso.equals("")) {%>
  <tr>
	  <td class="etichetta2">Sesso</td>
	  <td class="campo2"><b><%=strSesso%></b></td>
	  <!--
	  <td class="etichetta2" valign="top">Motivazione</td>
	  <td class="campo2"><b><%=strMotivSesso%></b></td>
	  -->
	  <td class="etichetta2">&nbsp;</td>
	  <td class="campo2">&nbsp;</td>
  </tr>
<%}%>

<%if (codEvasione.equals("DFA") || codEvasione.equals("DRA")) {%>
<tr><td colspan="4"><div class="sezione2">Riferimento</div></td></tr>
	<tr>
		<td class="etichetta2">Riferimento</td>
		<td class="campo2" colspan="2"><b>Centro per l'Impiego di <%=descCpi%></b></td>
		<td colspan="2">&nbsp;</td>
	</tr>
<%} else {
  if (StringUtils.isFilledNoBlank(strNominativo) || StringUtils.isFilled(strEmailRiferimento) ||
      StringUtils.isFilled(strTelRiferimento) || StringUtils.isFilled(strFaxRiferimento) ) { %>

<tr><td colspan="4"><div class="sezione2">Riferimento</div></td></tr>

	<% if (StringUtils.isFilledNoBlank(strNominativo) || StringUtils.isFilled(strEmailRiferimento)) { %>
		<tr>
			<%if (StringUtils.isFilledNoBlank(strNominativo)) {%>
				<td class="etichetta2">Riferimento</td>
				<td class="campo2"><b><%=strNominativo%></b></td>
			<%}%>
			<%if (StringUtils.isFilled(strEmailRiferimento)) {%>
			    <td class="etichetta2">Email</td>
    			<td class="campo2"><b><%=strEmailRiferimento%></b></td>
			<%}%>
		</tr>
	<%}%>

	<%if (StringUtils.isFilled(strTelRiferimento) || StringUtils.isFilled(strFaxRiferimento)) { %>
		<tr>
			<%if (StringUtils.isFilled(strTelRiferimento)) {%>
				<td class="etichetta2">Telefono</td>
			    <td class="campo2"><b><%=strTelRiferimento%></b></td>
			<%}%>
			<%if (StringUtils.isFilled(strFaxRiferimento)) {%>
				<td class="etichetta2">Fax</td>
			    <td class="campo2"><b><%=strFaxRiferimento%></b></td>
			<%}%>
		</tr>
	<%}
  } 
}
%>



<%
if ("S".equalsIgnoreCase(flgPubblicata)) { %>
<tr><td colspan="4"><div class="sezione2">Pubblicazione</div></td></tr>
<tr>
	<td colspan="4" class="sotto_sezione">
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<% if (StringUtils.isFilled(datPubblicazione)) { %>
		  <tr>
			  <td class="etichetta2">Data</td>
			  <td class="campo2" colspan="3"><b><%=datPubblicazione%></b>
			  <% if (StringUtils.isFilled(datScadenzaPubblicazione)) { %>
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				  Data scadenza&nbsp;<b><%=datScadenzaPubblicazione%></b>
			  <% } %>
			  </td>
		  </tr>
		<% } %>
		
		<%-- NON USATE: flgPubbPalese, flgPubbAnonima --%>
		<% if (StringUtils.isFilled(descMacroQualifica)) { %>
		  <tr>
			  <td class="etichetta2">Categoria</td>
			  <td class="campo2" colspan="3"><b><%=descMacroQualifica%></b></td>
		  </tr>
		<% } %>

		  <%-- NON USO NEPPURE flgPubbWeb, flgPubbGiornali, flgPubbBacheca
			  <tr>
			  String outmod = "";
			  if ("S".equalsIgnoreCase(flgPubbWeb))      outmod += "Web &nbsp;";
			  if ("S".equalsIgnoreCase(flgPubbGiornali)) outmod += "Giornali &nbsp;";
			  if ("S".equalsIgnoreCase(flgPubbBacheca))  outmod += "Bacheca &nbsp;";
			  
			  if (StringUtils.isFilled(outmod)) { %>
				  <td class="etichetta2">Modalit&agrave; pubblicazione</td>
				  <td class="campo2"><b>< %= outmod % ></b></td>
			  < % } else { % >
				  <td class="campo2" colspan="2">&nbsp;</td>
			  < % } % >
			  </tr>
		  --%>

		  <%-- ERA GIA' PRESENTE MA NON USO:
		  <tr>
			  <td class="etichetta2">Modalit&agrave; di evasione</td>
			  <td class="campo2" colspan="3"><b><%=modEvasione%></b></td>
		  </tr>
		  --%>
		
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
		  							"Per candidarsi"+
		  							((emailAzienda.equals(""))?"":"<p align = right>Invia e-mail&nbsp;<a href=\"mailto:"+emailAzienda+"?"+"subject=Mi candido alla domanda di lavoro "+numeroRichiesta.toString()+"/"+numeroAnno.toString()+" di/del "+descrizioneAzienda+"\">"+"<img alt=\"invio e-mail per candidarsi\" src=\"../../img/icomail.gif\">"+"</a></p>")
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
		  	  if(x==0 && (codEvasione.equals("DRA") || codEvasione.equals("DFA"))) {
			  	//Se la richiesta è anonima la sezione "Dati Azienda" non deve essere visualizzata
			    // per cui passo alla successiva 
			    x++;
			  }
			  if (StringUtils.isFilledNoBlank(outdatArr[x])) {
				// Sostituisco gli "a-capo" con dei "BR" e gli spazi con NBSP
			    outdatArr[x] = StringUtils.replace(outdatArr[x], "\r\n", "<br/>");	  
			    outdatArr[x] = StringUtils.replace(outdatArr[x], "\n",   "<br/>");	  
			  	outdatArr[x] = StringUtils.replace(outdatArr[x], " ",    "&nbsp;");	  
			  	%>
				<tr>
					<td class="etichetta2" valign="top" style="border-top: 1 solid #C6D5FF;" valign=top><%= outmsgArr[x] %></td>
					<td class="campo2_read" colspan="3" style="border-top: 1 solid #C6D5FF;" valign=top>
					<b><%= outdatArr[x] %></b></td>	
				</tr>
				<%
			  }
		  } //for
		%>
		
		</table>
		</td>
	</tr>
<%
} //if flgPubblicata
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
%>
<%if(vectRichiesta.size() > 0) {%>
	<tr><td colspan="4"><div class="sezione2">Abilitazioni</div></td></tr>
	<tr>
		<td colspan="4" class="sotto_sezione">
		<table cellpadding="0" cellspacing="0" border="0">
	  	<%
	  	for (i=0;i<vectRichiesta.size();i++) {
	    	rigaRichiestaFisse = (SourceBean) vectRichiesta.elementAt(i);
	    	strDescTipoAbilitazione = StringUtils.getAttributeStrNotNull(rigaRichiestaFisse, "DESCRIZIONETIPOABILITAZIONE");
		    strDescAbilitazione = StringUtils.getAttributeStrNotNull(rigaRichiestaFisse, "STRDESCRIZIONE");
	    	strFlgIndispensabile = StringUtils.getAttributeStrNotNull(rigaRichiestaFisse, "FLGINDISPENSABILE");
    		if (strFlgIndispensabile.equalsIgnoreCase("S")) { strFlgIndispensabile = "S&igrave;"; }
    		else {
      			if (strFlgIndispensabile.equalsIgnoreCase("N")) { strFlgIndispensabile = "No"; }
    		}
	    %>
		    <tr class="note">
			    <td class="etichetta2">Tipo</td>
			    <td class="campo2"><b><%=strDescTipoAbilitazione%></b></td>
			    <td class="etichetta2">Descrizione</td>
			    <td class="campo2"><b><%=strDescAbilitazione%></b></td>
		    	<%if (!strFlgIndispensabile.equals("")) {%>
		      		<td class="etichetta2">Indispensabile</td>
		      		<td class="campo2"><b><%=strFlgIndispensabile%></b></td>
		    	<%} else {%>
			      	<td colspan="2">&nbsp;</td>
		    	<%}%>
		    </tr>
	    <%}%>
	    </table>
	  </td>
	</tr>
<%}%>

<%
vectRichiesta = null;
rigaRichiestaFisse = null;
String strDescOrario = "";
//Lista degli orari della richiesta
vectRichiesta=serviceResponse.getAttributeAsVector("MListaOrariRichiesta.ROWS.ROW");
%>
<%if (vectRichiesta.size() > 0) {%>
	<tr><td colspan="4"><div class="sezione2">Orari</div></td></tr>
	<tr>
		<td colspan="4" class="sotto_sezione">
		<table cellpadding="0" cellspacing="0" border="0">
		<%
		for (i=0;i<vectRichiesta.size();i++) {
    		rigaRichiestaFisse = (SourceBean) vectRichiesta.elementAt(i);
		    strDescOrario = StringUtils.getAttributeStrNotNull(rigaRichiestaFisse, "STRDESCRIZIONE");
    	%>
	    	<tr class="note">
			    <td class="etichetta2">Orario</td>
			    <td class="campo2"><b><%=strDescOrario%></b></td>
	    	</tr>
    	<%}%>
		</table>
		</td>
	</tr>
<%}%>


<%
vectRichiesta = null;
rigaRichiestaFisse = null;
String strDescTurno = "";
//Lista dei turni della richiesta
vectRichiesta=serviceResponse.getAttributeAsVector("M_GetTurniRichiesta_Dettaglio_Sintetico.ROWS.ROW");
%>
<%if (vectRichiesta.size() > 0) {%>
	<tr><td colspan="4"><div class="sezione2">Turni</div></td></tr>
	<tr>
		<td colspan="4" class="sotto_sezione">
		<table cellpadding="0" cellspacing="0" border="0">
		<%
  		for (i=0;i<vectRichiesta.size();i++) {
    		rigaRichiestaFisse = (SourceBean) vectRichiesta.elementAt(i);
    		strDescTurno = StringUtils.getAttributeStrNotNull(rigaRichiestaFisse, "STRDESCRIZIONE");
    	%>
	    	<tr class="note">
			    <td class="etichetta2">Descrizione</td>
			    <td class="campo2"><b><%=strDescTurno%></b></td>
	    	</tr>
    	<%}%>
		</table>
		</td>
	</tr>	
<%}%>

<%
vectRichiesta = null;
rigaRichiestaFisse = null;
//Lista dei territori della richiesta
vectRichiesta=serviceResponse.getAttributeAsVector("MListaTerritoriRichiestaDettaglioSintetico.ROWS.ROW");
%>
<%if (vectRichiesta.size() > 0) {%>
	<tr><td colspan="4"><div class="sezione2">Territori</div></td></tr>
	<tr>
		<td colspan="4" class="sotto_sezione">
		<table cellpadding="0" cellspacing="0" border="0">
		<%
		for (i=0;i<vectRichiesta.size();i++) {
		    rigaRichiestaFisse = (SourceBean) vectRichiesta.elementAt(i);
		    String strTipoTerr = SourceBeanUtils.getAttrStrNotNull(rigaRichiestaFisse, "TIPOTERR");
		    String strDescTerr = SourceBeanUtils.getAttrStrNotNull(rigaRichiestaFisse, "DESCRIZIONETERRITORIO");
    	%>
	    	<tr class="note">
			    <td class="etichetta2">
		    	<%
		    	if      (strTipoTerr.equalsIgnoreCase("C")) out.print("Comune di");
		    	else if (strTipoTerr.equalsIgnoreCase("P")) out.print("Provincia di");
		    	else                                        out.print("Localit&agrave;");
		    	%>
			    </td>
			    <td class="campo2"><b><%=strDescTerr%></b></td>
	    	</tr>
    	<%}%>
		</table>
		</td>
	</tr>	
<%}%>
		
<%
vectRichiesta = null;
rigaRichiestaFisse = null;
String strDescCittadinanza = "";
String strMotivCittadinanza = "";
//Lista delle cittadinanze della richiesta
vectRichiesta=serviceResponse.getAttributeAsVector("MListaCittadinanzaRichiestaDettaglio.ROWS.ROW");
%>
<%if (vectRichiesta.size() > 0) {%>
	<tr><td colspan="4"><div class="sezione2">Nazionalit&agrave;</div></td></tr>
	<tr>
		<td colspan="4" class="sotto_sezione">
		<table cellpadding="0" cellspacing="0" border="0">
		<%
		for (i=0;i<vectRichiesta.size();i++) {
			rigaRichiestaFisse = (SourceBean) vectRichiesta.elementAt(i);
		    strDescCittadinanza = StringUtils.getAttributeStrNotNull(rigaRichiestaFisse, "STRDESCRIZIONE");
		    strMotivCittadinanza = StringUtils.getAttributeStrNotNull(rigaRichiestaFisse, "STRMOTIVAZIONE");
		%>
	    	<tr class="note">
			    <td class="etichetta2">Cittadinanza</td>
			    <td class="campo2"><b><%=strDescCittadinanza%></b></td>
			    <!--
			    <td class="etichetta2">Motivazione</td>
			    <td class="campo2"><b><%=strMotivCittadinanza%></b></td>
			    -->
			    <td class="etichetta2">&nbsp;</td>
			    <td class="campo2">&nbsp;</td>
	    	</tr>
    	<%}%>
		</table>
		</td>
	</tr>	
<%}%>

<%
int nColonneEsperienza = 10;
vectRichiesta = null;
SourceBean rigaAlternativeEsperienze = null;
SourceBean rigaAlternativeStudi = null;
SourceBean rigaAlternativeInfo = null;
SourceBean rigaAlternativeLingua = null;
SourceBean rigaAlternativeCompetenze = null;
SourceBean rigaAlternativeContratti = null;
SourceBean rigaAlternativeAgevolazioni = null;
SourceBean rigaAlternativeMansioni = null;

String numEsperienzaDa = "";
String numEsperienzaA = "";
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

//Lista delle alternative della richiesta
Vector vectRichiestaEsperienze=serviceResponse.getAttributeAsVector("MListaEsperienzeDettaglioSintetico.ROWS.ROW");
String strTipoMansione = "";
String strDescMansione = "";
//Vector vectRichiestaMansione = serviceResponse.getAttributeAsVector("M_LIST_IDO__DETTAGLIO_SINTETICO.ROWS.ROW");
Vector vectRichiestaMansione = null;
SourceBean sbRichiestaMansione = (SourceBean) serviceResponse.getAttribute("M_LIST_IDO_MANSIONI_DETTAGLIO_SINTETICO");

//Vector vectRichiestaStudi = serviceResponse.getAttributeAsVector("M_GetStudiRichiestaDettaglioSintetico.ROWS.ROW");
Vector vectRichiestaStudi = null;
SourceBean sbRichiestaStudi = (SourceBean) serviceResponse.getAttribute("M_GetStudiRichiestaDettaglioSintetico");

//Vector vectRichiestaInfo = serviceResponse.getAttributeAsVector("M_GetInfoRichiestaDettaglioSintetico.ROWS.ROW");
Vector vectRichiestaInfo = null;
SourceBean sbRichiestaInfo = (SourceBean) serviceResponse.getAttribute("M_GetInfoRichiestaDettaglioSintetico");

//Vector vectRichiestaLingue = serviceResponse.getAttributeAsVector("M_GetLingueRichiestaDettaglioSintetico.ROWS.ROW");
Vector vectRichiestaLingue = null;
SourceBean sbRichiestaLingue = (SourceBean) serviceResponse.getAttribute("M_GetLingueRichiestaDettaglioSintetico");

//Vector vectRichiestaCompetenze = serviceResponse.getAttributeAsVector("M_GetCompetenzeRichiestaDettaglioSintetico.ROWS.ROW");
Vector vectRichiestaCompetenze = null;
SourceBean sbRichiestaCompetenze = (SourceBean) serviceResponse.getAttribute("M_GetCompetenzeRichiestaDettaglioSintetico");

//Vector vectRichiestaContratti = serviceResponse.getAttributeAsVector("M_GetContrattiRichiestaDettaglioSintetico.ROWS.ROW");
Vector vectRichiestaContratti = null;
SourceBean sbRichiestaContratti = (SourceBean) serviceResponse.getAttribute("M_GetContrattiRichiestaDettaglioSintetico");

//Vector vectRichiestaAgevolazioni = serviceResponse.getAttributeAsVector("M_GetAgevolazioniRichiestaDettaglioSintetico.ROWS.ROW");
Vector vectRichiestaAgevolazioni = null;
SourceBean sbRichiestaAgevolazioni = (SourceBean) serviceResponse.getAttribute("M_GetAgevolazioniRichiestaDettaglioSintetico");
%>
<%for (i=0;i<vectRichiestaEsperienze.size();i++) {%>
	<%
	//Esperienze
  	rigaAlternativeEsperienze = (SourceBean) vectRichiestaEsperienze.elementAt(i);
	strPrgAlternativaEsp = rigaAlternativeEsperienze.getAttribute("prgAlternativa").toString();
	numEsperienzaDa = StringUtils.getAttributeStrNotNull(rigaAlternativeEsperienze, "numda");
	numEsperienzaA = StringUtils.getAttributeStrNotNull(rigaAlternativeEsperienze, "numa");
	flgEsperienza  = StringUtils.getAttributeStrNotNull(rigaAlternativeEsperienze, "flgEsperienza");
	numAnniEsperienza = StringUtils.getAttributeStrNotNull(rigaAlternativeEsperienze, "numAnniEsperienza");
	if(flgEsperienza.equals("N")) { flgEsperienza = "No"; }
    else {
      if(flgEsperienza.equals("S")) { flgEsperienza = "S&igrave;"; }
      else {
      	 if(flgEsperienza.equals("P")) { flgEsperienza = "Preferibile"; }
      }
    }
	%>
	<tr><td colspan="4" class="azzurro_bianco"><b>Profilo n.<%=strPrgAlternativaEsp%></b></td></tr>
	<%if(!numEsperienzaDa.equals("") || !numEsperienzaA.equals("") || !flgEsperienza.equals("") || !numAnniEsperienza.equals("")) {%>
	<tr><td colspan="4"><div class="sezione2">Esperienze</div></td></tr>
	<tr>
		<td colspan="4" class="sotto_sezione">
		<table cellpadding="0" cellspacing="0" border="0">
		<tr class="note">
			<%if (!numEsperienzaDa.equals("")) {%>
				<td class="etichetta2">Et&agrave; Da</td>
	      		<td class="campo2"><b><%=numEsperienzaDa%></b></td>
	      	<%}%>
	      	<%if(!numEsperienzaA.equals("")) {%>
	      		<td class="etichetta2">Et&agrave; A</td>
      			<td class="campo2"><b><%=numEsperienzaA%></b></td>
	      	<%}%>
	      	<%if(!flgEsperienza.equals("")) {%>
	      		<td class="etichetta2">Esperienza</td>
      			<td class="campo2"><b><%=flgEsperienza%></b></td>
	      	<%}%>
	      	<%if(!numAnniEsperienza.equals("")) {%>
	      		<td class="etichetta2">Anni di Esperienza</td>
      			<td class="campo2"><b><%=numAnniEsperienza%></b></td>
	      	<%}%>
	     </tr>
		</table>
		</td>
	</tr>
	<%}%>
	
	<!-- Mansioni -->
	<%
	vectRichiestaMansione = sbRichiestaMansione.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "PRGALTERNATIVA", strPrgAlternativaEsp);
	%>
  	<%if (vectRichiestaMansione.size() > 0) {%>
  		<tr><td colspan="4"><div class="sezione2">Ambito professionale</div></td></tr>
		<tr>
			<td colspan="4" class="sotto_sezione">
			<table cellpadding="0" cellspacing="0" border="0">
			<%
			for (int j=0;j<vectRichiestaMansione.size();j++) {
      			rigaAlternativeMansioni = (SourceBean) vectRichiestaMansione.elementAt(j);
			    strPrgAlternativaMansioni = rigaAlternativeMansioni.getAttribute("prgAlternativa").toString();
			    strTipoMansione = StringUtils.getAttributeStrNotNull(rigaAlternativeMansioni, "desTipoMansione");
        		strDescMansione = StringUtils.getAttributeStrNotNull(rigaAlternativeMansioni, "desMansione");
        	%>
        		<%if(!strDescMansione.equals("")) {%>
	        	<tr class="note">
          			<td class="etichetta2">Tipo</td>
          			<td class="campo2"><b><%=strTipoMansione%></b></td>
			    	<td class="etichetta2">Mansione</td>
			        <td class="campo2"><b><%=strDescMansione%></b></td>
        		</tr>
        		<%}%>
        	<%}%>
			</table>
			</td>
		</tr>
  	<%}%>
  	
  	<!-- Studi -->
	<%
	vectRichiestaStudi = sbRichiestaStudi.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "PRGALTERNATIVA", strPrgAlternativaEsp);
	%>
  	<%if (vectRichiestaStudi.size() > 0) {%>
  		<tr><td colspan="4"><div class="sezione2">Titolo di studio</div></td></tr>
		<tr>
			<td colspan="4" class="sotto_sezione">
			<table cellpadding="0" cellspacing="0" border="0">
			<%
			for (int j=0;j<vectRichiestaStudi.size();j++) {
      			rigaAlternativeStudi = (SourceBean) vectRichiestaStudi.elementAt(j);
      			strPrgAlternativaStudi = rigaAlternativeStudi.getAttribute("prgAlternativa").toString();
				descTitolo = StringUtils.getAttributeStrNotNull(rigaAlternativeStudi, "DESCTITOLO");
        		descSpecifica = StringUtils.getAttributeStrNotNull(rigaAlternativeStudi, "SPECIFICA");
		        flgTitoloConseguito = StringUtils.getAttributeStrNotNull(rigaAlternativeStudi, "CONSEGUITO");
		        flgTitoloIndispensabile = StringUtils.getAttributeStrNotNull(rigaAlternativeStudi, "INDISPENSABILE");
        		if (flgTitoloIndispensabile.toUpperCase().equals("S")) { flgTitoloIndispensabile = "S&igrave;"; }
		        else {
          			if (flgTitoloIndispensabile.toUpperCase().equals("N")) { flgTitoloIndispensabile = "No"; }
        		}
        		if (flgTitoloConseguito.toUpperCase().equals("S")) { flgTitoloConseguito = "S&igrave;"; }
		        else { 
		        	if (flgTitoloConseguito.toUpperCase().equals("N")) { flgTitoloConseguito = "No"; }
        		}
        	%>
        		<tr class="note">
        		<%if(!descTitolo.equals("")) {%>
	          			<td class="etichetta2">Titolo</td>
				        <td class="campo2"><b><%=descTitolo%></b></td>
	        		<%} else {%>
	          			<td colspan="2">&nbsp;</td>
	        		<%}%>
	        		<%if (!descSpecifica.equals("")) {%>
	          			<td class="etichetta2">Specifica</td>
	          			<td class="campo2"><b><%=descSpecifica%></b></td>
	        		<%} else {%>
	          			<td colspan="2">&nbsp;</td>
	        		<%}%>
			        <%if(!flgTitoloConseguito.equals("")) {%>
	          			<td class="etichetta2">Conseguito</td>
	          			<td class="campo2"><b><%=flgTitoloConseguito%></b></td>
	        		<%} else {%>
	          			<td colspan="2">&nbsp;</td>
	        		<%}%>
			        <%if(!flgTitoloIndispensabile.equals("")) {%>
	          			<td class="etichetta2">Indispensabile</td>
	          			<td class="campo2"><b><%=flgTitoloIndispensabile%></b></td>
	        		<%} else {%>
	          			<td colspan="2">&nbsp;</td>
	        		<%}%>
	        		</tr>
      		<%}%>
			</table>
			</td>
		</tr>
  	<%}%>
  	
  	<!--Informatica-->
	<%
	vectRichiestaInfo = sbRichiestaInfo.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "PRGALTERNATIVA", strPrgAlternativaEsp);
	%>
  	<%if (vectRichiestaInfo.size() > 0) {%>
  		<tr><td colspan="4"><div class="sezione2">Informatica</div></td></tr>
		<tr>
			<td colspan="4" class="sotto_sezione">
			<table cellpadding="0" cellspacing="0" border="0">
			<%
			for (int j=0;j<vectRichiestaInfo.size();j++) {
      			rigaAlternativeInfo = (SourceBean) vectRichiestaInfo.elementAt(j);
      			strPrgAlternativaInfo = rigaAlternativeInfo.getAttribute("prgAlternativa").toString();
      			strDescTipoConoInfo = rigaAlternativeInfo.containsAttribute("DESCRIZIONETIPO")?rigaAlternativeInfo.getAttribute("DESCRIZIONETIPO").toString():"";
        		strDescConoInfo = StringUtils.getAttributeStrNotNull(rigaAlternativeInfo, "DESCRIZIONEDETT");
        		strDescGradoCono = StringUtils.getAttributeStrNotNull(rigaAlternativeInfo, "DESCRIZIONEGRADO");
        		flgConoIndispensabile = StringUtils.getAttributeStrNotNull(rigaAlternativeInfo, "FLGINDISPENSABILE");
        		if(flgConoIndispensabile.equals("S")) { flgConoIndispensabile = "S&igrave;"; }
        		else {
          			if (flgConoIndispensabile.equals("N")) { flgConoIndispensabile = "No"; }
        		}
        	%>
        		<tr class="note">
        			<%if (!strDescTipoConoInfo.equals("")) {%>
          				<td class="etichetta2">Tipo</td>
          				<td class="campo2"><b><%=strDescTipoConoInfo%></b></td>
        			<%} else {%>
          				<td colspan="2">&nbsp;</td>
        			<%}%>
        			<%if (!strDescConoInfo.equals("")) {%>
		          		<td class="etichetta2">Dettaglio</td>
		          		<td class="campo2"><b><%=strDescConoInfo%></b></td>
        			<%} else {%>
          				<td colspan="2">&nbsp;</td>
        			<%}%>
        			<%if (!strDescGradoCono.equals("")) {%>
          				<td class="etichetta2">Livello</td>
          				<td class="campo2"><b><%=strDescGradoCono%></b></td>
        			<%} else {%>
          				<td colspan="2">&nbsp;</td>
        			<%}%>
        			<%if (!flgConoIndispensabile.equals("")) {%>
          				<td class="etichetta2">Indispensabile</td>
          				<td class="campo2"><b><%=flgConoIndispensabile%></b></td>
        			<%} else {%>
          				<td colspan="2">&nbsp;</td>
        			<%}%>
        		</tr>
        	<%}%>
			</table>
			</td>
		</tr>
  	<%}%>
  	
  	<!-- Lingue -->
	<%
	vectRichiestaLingue = sbRichiestaLingue.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "PRGALTERNATIVA", strPrgAlternativaEsp);
	%>
  	<%if (vectRichiestaLingue.size() > 0) {%>
  		<tr><td colspan="4"><div class="sezione2">Lingue</div></td></tr>
		<tr>
			<td colspan="4" class="sotto_sezione">
			<table cellpadding="0" cellspacing="0" border="0">
			<%
			for (int j=0;j<vectRichiestaLingue.size();j++) {
      			rigaAlternativeLingua = (SourceBean) vectRichiestaLingue.elementAt(j);
      			strPrgAlternativaLingua = rigaAlternativeLingua.getAttribute("prgAlternativa").toString();
      			strLinguaRichiesta = StringUtils.getAttributeStrNotNull(rigaAlternativeLingua, "STRDENOMINAZIONE");
        		strGradoLetto = StringUtils.getAttributeStrNotNull(rigaAlternativeLingua, "DescrizioneLetto");
        		strGradoScritto = StringUtils.getAttributeStrNotNull(rigaAlternativeLingua, "DescrizioneScritto");
        		strGradoParlato = StringUtils.getAttributeStrNotNull(rigaAlternativeLingua, "DescrizioneParlato");
        		strFlgIndispensabile = StringUtils.getAttributeStrNotNull(rigaAlternativeLingua, "FLGINDISPENSABILE");
        		if (strFlgIndispensabile.equals("S")) { flgLinguaIndispensabile = "S&igrave;"; }
        		else {
          			if (strFlgIndispensabile.equals("N")) { flgLinguaIndispensabile = "No"; }
        		}
        	%>
        		<tr class="note">
        			<td class="etichetta2">Lingua</td>
        			<td class="campo2"><b><%=strLinguaRichiesta%></b></td>
        			<td class="etichetta2">Grado Letto</td>
        			<td class="campo2"><b><%=strGradoLetto%></b></td>
        			<td class="etichetta2">Grado Scritto</td>
        			<td class="campo2"><b><%=strGradoScritto%></b></td>
        			<td class="etichetta2">Grado Parlato</td>
        			<td class="campo2"><b><%=strGradoParlato%></b></td>
        			<%if (!strFlgIndispensabile.equals("")) {%>
          				<td class="etichetta2">Indispensabile</td>
          				<td class="campo2"><b><%=flgLinguaIndispensabile%></b></td>
        			<%} else {%>
          				<td colspan="2">&nbsp;</td>
        			<%}%>
        		</tr>	
        	<%}%>
			</table>
			</td>
		</tr>
  	<%}%>
  	
  	<!--  -->
	<%
	vectRichiestaCompetenze = sbRichiestaCompetenze.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "PRGALTERNATIVA", strPrgAlternativaEsp);
	%>
  	<%if (vectRichiestaCompetenze.size() > 0) {%>
  		<tr><td colspan="4"><div class="sezione2">Competenze</div></td></tr>
		<tr>
			<td colspan="4" class="sotto_sezione">
			<table cellpadding="0" cellspacing="0" border="0">
			<%
			for (int j=0;j<vectRichiestaCompetenze.size();j++) {
      			rigaAlternativeCompetenze = (SourceBean) vectRichiestaCompetenze.elementAt(j);
			    strPrgAlternativaCompetenze = rigaAlternativeCompetenze.getAttribute("prgAlternativa").toString();
			    strDescCompetenza = StringUtils.getAttributeStrNotNull(rigaAlternativeCompetenze, "DESCRIZIONECOMPETENZA");
        		flgIndispensabileComp = StringUtils.getAttributeStrNotNull(rigaAlternativeCompetenze, "FLGINDISPENSABILE");
        		if(flgIndispensabileComp.equals("S")) { flgIndispensabileComp = "S&igrave;"; }
        		else {
          			if(flgIndispensabileComp.toUpperCase().equals("N")) { flgIndispensabileComp = "No"; }
        		}
        	%>
	        	<tr class="note">
					<%if (!strDescCompetenza.equals("")) {%>
          				<td class="etichetta2">Tipo</td>
          				<td class="campo2"><b><%=strDescCompetenza%></b></td>
        			<%} else {%>
          				<td colspan="2">&nbsp;</td>
        			<%}%>
        			<%if (!flgIndispensabileComp.equals("")) {%>
          				<td class="etichetta2">Indispensabile</td>
          				<td class="campo2"><b><%=flgIndispensabileComp%></b></td>
        			<%} else {%>
          				<td colspan="2">&nbsp;</td>
        			<%}%>
        		</tr>
        	<%}%>
			</table>
			</td>
		</tr>
  	<%}%>
  	
  	<!-- Contratti -->
	<%
	vectRichiestaContratti = sbRichiestaContratti.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "PRGALTERNATIVA", strPrgAlternativaEsp);
	%>
  	<%if (vectRichiestaContratti.size() > 0) {%>
  		<tr><td colspan="4"><div class="sezione2">Tipologia rapporto</div></td></tr>
		<tr>
			<td colspan="4" class="sotto_sezione">
			<table cellpadding="0" cellspacing="0" border="0">
			<%
			for (int j=0;j<vectRichiestaContratti.size();j++) {
      			rigaAlternativeContratti = (SourceBean) vectRichiestaContratti.elementAt(j);
      			strPrgAlternativaContratti = rigaAlternativeContratti.getAttribute("prgAlternativa").toString();
      			strDescContratti = StringUtils.getAttributeStrNotNull(rigaAlternativeContratti, "STRDESCRIZIONE");
      		%>
      			<%if (!strDescContratti.equals("")) {%>
      				<tr class="note">
          				<td class="etichetta2">Tipologia</td>
          				<td class="campo2"><b><%=strDescContratti%></b></td>
          			</tr>
        		<%}%>
        	<%}%>
			</table>
			</td>
		</tr>
  	<%}%>
  	
  	<!-- Agevolazioni -->
	<%
	vectRichiestaAgevolazioni = sbRichiestaAgevolazioni.getFilteredSourceBeanAttributeAsVector("ROWS.ROW", "PRGALTERNATIVA", strPrgAlternativaEsp);
	%>
  	<%if (vectRichiestaAgevolazioni.size() > 0) {%>
  		<tr><td colspan="4"><div class="sezione2">Agevolazioni</div></td></tr>
		<tr>
			<td colspan="4" class="sotto_sezione">
			<table cellpadding="0" cellspacing="0" border="0">
			<%
			for (int j=0;j<vectRichiestaAgevolazioni.size();j++) {
      			rigaAlternativeAgevolazioni = (SourceBean) vectRichiestaAgevolazioni.elementAt(j);
      			strPrgAlternativaAgevolazioni = rigaAlternativeAgevolazioni.getAttribute("prgAlternativa").toString();
      			strDescAgevolazioni = StringUtils.getAttributeStrNotNull(rigaAlternativeAgevolazioni, "STRDESCRIZIONE");
        		flgAgevolazioneIndisp = StringUtils.getAttributeStrNotNull(rigaAlternativeAgevolazioni, "FLGINDISPENSABILE");
        		if(flgAgevolazioneIndisp.equals("S")) { flgAgevolazioneIndisp = "S&igrave;"; }
        		else {
          			if(flgAgevolazioneIndisp.equals("N")) { flgAgevolazioneIndisp = "No"; }
        		}
        	%>
 				<%if (!strDescAgevolazioni.equals("")) {%>
 					<tr class="note">
			          <td class="etichetta2">Tipologia</td>
			          <td class="campo2"><b><%=strDescAgevolazioni%></b></td>
        			  <%if (!flgAgevolazioneIndisp.equals("")) {%>
          			  		<td class="etichetta2">Indispensabile</td>
          					<td class="campo2"><b><%=flgAgevolazioneIndisp%></b></td>
        			  <%} else {%>
          					<td colspan="2">&nbsp;</td>
        			  <%}%>
        			</tr>
        		<%}%>
        	<%}%>
			</table>
			</td>
		</tr>
  	<%}%>
<%}%>
</table>
<br/>
<%out.print(htmlStreamBottom);%>
<%if(!refListaBtn.equals("")) {%>
	<p align="center"><%=refListaBtn%></p><br/>&nbsp;
<%}%>
<%@ include file="/jsp/MIT.inc" %>
</body>
</html>