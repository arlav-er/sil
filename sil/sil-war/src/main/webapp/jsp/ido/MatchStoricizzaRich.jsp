<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                com.engiweb.framework.security.*,
                it.eng.afExt.utils.*, java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String prgOrig = serviceRequest.getAttribute("PRGORIG").toString();
String prgC1 = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
String prgAlternativa = serviceRequest.getAttribute("PRGALTERNATIVA").toString();
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
String _cdnFunzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();
String emValue = serviceRequest.getAttribute("EM").toString();

String moduleName = "MStoricizzaXIncrocio";
SourceBean content = (SourceBean) serviceResponse.getAttribute(moduleName);
SourceBean row = (SourceBean) content.getAttribute("ROW");
int CodiceRit = Integer.parseInt(row.getAttribute("CodiceRit").toString());
if(CodiceRit == -1) { CodiceRit = 1; }
String prgRichiestaAz = StringUtils.getAttributeStrNotNull(row, "NEWPRGRICHIESTAAZ");
String errMsg[] = { "Storicizzazione della copia di lavoro per l'incrocio avvenuta correttamente.",
                    "Errore nell'esecuzione della procedura."
                  };



String htmlStreamTopInfo = StyleUtils.roundTopTableInfo();
String htmlStreamBottomInfo = StyleUtils.roundBottomTableInfo();

int nroMansioni = 0;

String cpiRose = user.getCodRif();



// PARAMETRI RELATIVI AL MATCHING
String p_prgRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGROSA");
String p_cdnUtente = StringUtils.getAttributeStrNotNull(serviceRequest, "P_CDNUTENTE");
String p_dataValCV = StringUtils.getAttributeStrNotNull(serviceRequest,"dataCV");
String p_db = StringUtils.getAttributeStrNotNull(serviceRequest, "db");
String p_flgIncMir = "";
String p_codMonoCMCategoria = "";
// INIT-PARTE-TEMP
if (Sottosistema.CM.isOff()) {	
// END-PARTE-TEMP

// INIT-PARTE-TEMP
} else {
// END-PARTE-TEMP
	p_flgIncMir = StringUtils.getAttributeStrNotNull(serviceRequest, "flgIncMir");   
	p_codMonoCMCategoria = StringUtils.getAttributeStrNotNull(serviceRequest, "codMonoCMCategoria");   
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP


// Parametri specifici - MATCHING ESATTO
String p_statoCv = StringUtils.getAttributeStrNotNull(serviceRequest, "statoCV");
String p_usaPref = StringUtils.getAttributeStrNotNull(serviceRequest, "usaPref");
String p_usaNonInd = StringUtils.getAttributeStrNotNull(serviceRequest, "usaNonInd");
String p_flagDI = StringUtils.getAttributeStrNotNull(serviceRequest, "flagDI");
String p_flagGG = StringUtils.getAttributeStrNotNull(serviceRequest, "flagGG");
String flagNoMansione = StringUtils.getAttributeStrNotNull(serviceRequest, "flagNoMansione");
String p_codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "P_CODCPI");      

// Parametri specifici - MATCHING PESATO
String p_numPFasciaEtaPrima = StringUtils.getAttributeStrNotNull(serviceRequest, "numPFasciaEtaPrima");
String p_numPFasciaEtaSeconda = StringUtils.getAttributeStrNotNull(serviceRequest, "numPFasciaEtaSeconda");
String p_numPStudioGruppo = StringUtils.getAttributeStrNotNull(serviceRequest, "numPStudioGruppo");
String p_numPStudioAlias = StringUtils.getAttributeStrNotNull(serviceRequest, "numPStudioAlias");
String p_numPMansioneGruppo = StringUtils.getAttributeStrNotNull(serviceRequest, "numPMansioneGruppo");
String p_numPMansioneAlias = StringUtils.getAttributeStrNotNull(serviceRequest, "numPMansioneAlias");
String p_numPNoEsperienza = StringUtils.getAttributeStrNotNull(serviceRequest, "numPNoEsperienza");
String p_numPEsperienzaAlias = StringUtils.getAttributeStrNotNull(serviceRequest, "numPEsperienzaAlias");
String p_numPInfoMin = StringUtils.getAttributeStrNotNull(serviceRequest, "numPInfoMin");
String p_numPInfoGruppo = StringUtils.getAttributeStrNotNull(serviceRequest, "numPInfoGruppo");
String p_numPInfoGruppoMin = StringUtils.getAttributeStrNotNull(serviceRequest, "numPInfoGruppoMin");
String p_numPLinguaInf = StringUtils.getAttributeStrNotNull(serviceRequest, "numPLinguaInf");
String p_numPSogliaRichiesta = StringUtils.getAttributeStrNotNull(serviceRequest, "numPSogliaRichiesta");
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Matching</title>
  <af:linkScript path="../../js/" />
  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
  <script language="Javascript">
    function procedi()
    {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
	 
      doFormSubmit(document.form_match);
    }

    function controllaRit(i)
    {
      if(i==0) { 
      	msgLoad();
      	setTimeout("procedi()", 1000); 
      }
    }
  function openRich_PopUP() {
     window.open ("AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&CDNFUNZIONE=<%=_cdnFunzione%>&POPUP=1", "DettaglioSintetico", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
  }
  
  function msgLoad() {
  	var coll = document.getElementsByName("loadDiv");
  	coll[0].style.display = "";
  	window.status = "Elaborazione match in corso";
  	//alert(coll[0].innerHTML);
  }
  </script>
  
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_cdnFunzione%>,<%=prgAzienda%>, <%=prgUnita%>);
  </script>

</head>

<body class="gestione" onload="rinfresca();controllaRit(<%=CodiceRit%>)">
<%if(CodiceRit != 0) { %>
    <%
//    SourceBean cont = (SourceBean) serviceResponse.getAttribute("MINFORICHIESTA");
    SourceBean infoRich = (SourceBean) serviceResponse.getAttribute("MINFORICHIESTA.ROWS.ROW");
//    cont = (SourceBean) serviceResponse.getAttribute("MINFORICHMANSIONI");
    Vector infoRichMan = serviceResponse.getAttributeAsVector("MINFORICHMANSIONI.ROWS.ROW");
    SourceBean rowMan = null;
    int i = 0;
    if(infoRichMan.size()>0) { 
    	nroMansioni = infoRichMan.size(); 
    }
    String _indirizzo = "";
    String ragSoc = StringUtils.getAttributeStrNotNull(infoRich,"STRRAGIONESOCIALE");
    String numRichiestaOrig = StringUtils.getAttributeStrNotNull(infoRich,"NUMRICHIESTAORIG");
    String numRichiesta = StringUtils.getAttributeStrNotNull(infoRich,"NUMRICHIESTA");
    String numAnno = StringUtils.getAttributeStrNotNull(infoRich,"NUMANNO");
    String dataRich = StringUtils.getAttributeStrNotNull(infoRich,"DATRICHIESTA");
    String dataScad = StringUtils.getAttributeStrNotNull(infoRich,"DATSCADENZA");
    String stato_richiesta = StringUtils.getAttributeStrNotNull(infoRich,"stato_richiesta");
    String stato_evasione = StringUtils.getAttributeStrNotNull(infoRich,"stato_evasione");
    String codCpi = StringUtils.getAttributeStrNotNull(infoRich,"CODCPI");
    String descCpi = StringUtils.getAttributeStrNotNull(infoRich,"desc_cpi");
    String indir = StringUtils.getAttributeStrNotNull(infoRich,"STRINDIRIZZO");
    String cap = StringUtils.getAttributeStrNotNull(infoRich,"STRCAP");
    String loc = StringUtils.getAttributeStrNotNull(infoRich,"STRLOCALITA");
    String comune = StringUtils.getAttributeStrNotNull(infoRich,"comune_az");
    String targa = StringUtils.getAttributeStrNotNull(infoRich,"STRTARGA");
    
    String utIns = StringUtils.getAttributeStrNotNull(infoRich, "UT_INS");
    String utMod = StringUtils.getAttributeStrNotNull(infoRich, "UT_MOD");
    if(infoRich!=null) {
      
      if(indir!="") { _indirizzo += indir; }
      if(_indirizzo.length()>0) { _indirizzo += "&nbsp;&nbsp;"; }
      if(cap!="") { _indirizzo += cap; }
      if(_indirizzo.length()>0) { _indirizzo += "&nbsp;&nbsp;"; }
      if(loc!="") { _indirizzo += "- " + loc; }
      if(_indirizzo.length()>0) { _indirizzo += "&nbsp;&nbsp;"; }
      if(comune!="") { _indirizzo += "- " + comune; }
      if(_indirizzo.length()>0) { _indirizzo += "&nbsp;&nbsp;"; }
      if(targa!=null) { _indirizzo += "(" + targa + ")"; }
    }
    %>
    
    <%if(infoRich!=null) {%>
      <br>
      <%out.print(htmlStreamTopInfo);%>
      <p class="info_lav">
      <a class="info_lav" href="#" onClick="openRich_PopUP()">Richiesta num. <strong><%=numRichiestaOrig%>/<%=numAnno%></strong></a>
      <br>
      Data Richiesta <strong><%=dataRich%></strong>&nbsp;&nbsp;
      <br>
      Data Scadenza <strong><%=dataScad%></strong>
      <br>
      CpI di riferimento <strong><%=codCpi%> - <%=descCpi%></strong>
      <br>
      Stato della Richiesta <strong><%=stato_richiesta%></strong>&nbsp;&nbsp;
      <br>
      Stato di Evasione <strong><%=stato_evasione%></strong>&nbsp;&nbsp;
      <br>
      Richiedente <strong><%=ragSoc%></strong>&nbsp;&nbsp;
      <strong><%= _indirizzo%></strong>
      <%if(nroMansioni>1) {%>
        <br>
        <table  class="info" align="left" margin="0" cellspacing="0" cellpadding="0">
        <tr valign="top">
          <td align="left" class="info">Mansioni richieste:&nbsp;</td>
          <td align="left"><ul type="square">
            <%for(i=0; i < nroMansioni; i++) {%>
              <%rowMan = (SourceBean) infoRichMan.elementAt(i); %>
              <li class="info"><strong><%=StringUtils.getAttributeStrNotNull(rowMan,"MANSIONE")%></strong></li>
            <%}%>
          </ul></td>
          <td class="info">&nbsp;</td>
        </tr>
        <tr>
          <td colspan="3" class="info">
          Inserimento&nbsp;&nbsp;<strong><%=utIns%></strong>&nbsp;&nbsp;Ultima modifica&nbsp;&nbsp;<strong><%=utMod%></strong>
          </td>
        </tr>
        </table>
      <%} else {
          if(nroMansioni==1){
             rowMan = (SourceBean) infoRichMan.elementAt(0);
      %> 
             <br>Mansione richiesta: <strong><%=StringUtils.getAttributeStrNotNull(rowMan, "MANSIONE")%></strong>
             <br>Inserimento&nbsp;&nbsp;<strong><%=utIns%></strong>&nbsp;&nbsp;Ultima modifica&nbsp;&nbsp;<strong><%=utMod%></strong>
          <%}
        }%>
      </p>
      <%out.print(htmlStreamBottomInfo);%>
    <%}%>
<%} // end if(CodiceRit!=0)%>


<af:form name="form_match" action="AdapterHTTP" method="POST">
<input name="PAGE" type="hidden" value="<%=((CodiceRit==0)?"MatchAnteprimaPage":"GestIncrocioPage")%>"/>
<input name="PRGRICHIESTAAZ" type="hidden" value="<%=((CodiceRit==0)?prgRichiestaAz:prgC1)%>"/>
<input name="C1" type="hidden" value="<%=prgC1%>"/>
<input name="PRGORIG" type="hidden" value="<%=prgOrig%>"/>
<input type="hidden" name="PRGALTERNATIVA" value="<%=prgAlternativa%>"/>
<input name="PRGAZIENDA" type="hidden" value="<%=prgAzienda%>"/>
<input name="PRGUNITA" type="hidden" value="<%=prgUnita%>"/>
<input name="EM" type="hidden" value="<%=emValue%>"/>
<input name="CDNFUNZIONE" type="hidden" value="<%=_cdnFunzione%>"/>

<input name="PRGROSA" type="hidden" value="<%=p_prgRosa%>"/>
<input name="P_CDNUTENTE" type="hidden" value="<%=p_cdnUtente%>"/>
<input name="dataValCV" type="hidden" value="<%=p_dataValCV%>"/>
<input name="db" type="hidden" value="<%=p_db%>"/>
<%
// INIT-PARTE-TEMP
if (Sottosistema.CM.isOff()) {	
// END-PARTE-TEMP
%>

<%	
// INIT-PARTE-TEMP
} else {
// END-PARTE-TEMP
%>
	<input name="flgIncMir" type="hidden" value="<%= p_flgIncMir%>"/>   
	<input name="codMonoCMCategoria" type="hidden" value="<%= p_codMonoCMCategoria%>"/>   
<%
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>
<%if(emValue.equals("1")) {%>
  <input name="statoCv" type="hidden" value="<%=p_statoCv%>"/>
  <input name="usaPref" type="hidden" value="<%=p_usaPref%>"/>
  <input name="usaNonInd" type="hidden" value="<%=p_usaNonInd%>"/>
  <input name="flagDI" type="hidden" value="<%=p_flagDI%>"/>
  <input name="flagGG" type="hidden" value="<%=p_flagGG%>"/>
  <input name="P_CODCPI" type="hidden" value="<%=p_codCpi%>"/>
  <input name="flagNoMansione" type="hidden" value="<%=flagNoMansione%>"/>
<%}%>
<%if(emValue.equals("2")) {%>
  <input name="numPFasciaEtaPrima" type="hidden" value="<%=p_numPFasciaEtaPrima%>"/>
  <input name="numPFasciaEtaSeconda" type="hidden" value="<%=p_numPFasciaEtaSeconda%>"/>
  <input name="numPStudioGruppo" type="hidden" value="<%=p_numPStudioGruppo%>"/>
  <input name="numPStudioAlias" type="hidden" value="<%=p_numPStudioAlias%>"/>
  <input name="numPMansioneGruppo" type="hidden" value="<%=p_numPMansioneGruppo%>"/>
  <input name="numPMansioneAlias" type="hidden" value="<%=p_numPMansioneAlias%>"/>
  <input name="numPNoEsperienza" type="hidden" value="<%=p_numPNoEsperienza%>"/>
  <input name="numPEsperienzaAlias" type="hidden" value="<%=p_numPEsperienzaAlias%>"/>
  <input name="numPInfoMin" type="hidden" value="<%=p_numPInfoMin%>"/>
  <input name="numPInfoGruppo" type="hidden" value="<%=p_numPInfoGruppo%>"/>
  <input name="numPInfoGruppoMin" type="hidden" value="<%=p_numPInfoGruppoMin%>"/>
  <input name="numPLinguaInf" type="hidden" value="<%=p_numPLinguaInf%>"/>
  <input name="numPSogliaRichiesta" type="hidden" value="<%=p_numPSogliaRichiesta%>"/>
<%}%>
<div name="loadDiv" id="loadDiv" style="display: none">
<br>
<%out.print(htmlStreamTopInfo);%>
<table class="main">
<tr valign="middle">
	<td align="center">
			<IMG border="0" src="../../img/hourglass.gif" width="32" height="32"><br>
			<h2>Elaborazione in corso ...</h2></td>
</tr>
</table>
<%out.print(htmlStreamBottomInfo);%>
</div>

<%if(CodiceRit != 0) {%>
    <h2>Storicizzazione per Incrocio</h2>
    <%
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
    %>
    
    <%out.print(htmlStreamTop);%>
    <table class="main">
    <tr>
      <td align="justify"><%=errMsg[CodiceRit]%></td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr>
      <td align="center">
      <input type="submit" class="pulsanti" onClick="procedi()" value="Continua"/>
      </td>
    </tr>
    </table>
    <%out.print(htmlStreamBottom);%>
<%} // end if(CodiceRit!=0)%>

</af:form>
  
</body>
</html>
