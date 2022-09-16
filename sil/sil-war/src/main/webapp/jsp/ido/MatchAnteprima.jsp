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
String prgRichiestaAz = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
String prgOrig = serviceRequest.getAttribute("PRGORIG").toString();
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
String C1 = serviceRequest.getAttribute("C1").toString();
String flgincrociomirato = serviceRequest.getAttribute("flgIncMir")  == null ? "0" : (String)serviceRequest.getAttribute("flgIncMir");

//String _page = serviceRequest.getAttribute("PAGE").toString();
//PageAttribs attributi = new PageAttribs(user, _page);

String p_codCpi = user.getCodRif();
int nroMansioni = 0;

int EM = Integer.parseInt(serviceRequest.getAttribute("EM").toString());
String titolo = "";
String anteprimaMod = "";
int daMatch=1;
String prgTipoIncrocio = "";

switch(EM) {
  case 1:
    titolo = "Matching Esatto - Anteprima Risultati";
    anteprimaMod = "MRecuperaAnteprimaEsatto";
    prgTipoIncrocio = "1";
    break;
  case 4:
    titolo = "Matching Esatto - Anteprima Risultati";
    anteprimaMod = "MRecuperaAnteprimaEsatto";
    prgTipoIncrocio = "1";
    break;
  case 2:
    titolo = "Matching Pesato - Anteprima Risultati";
    anteprimaMod = "MRecuperaAnteprimaPesato";
    prgTipoIncrocio = "2";
    break;
  case 5:
    titolo = "Matching Pesato - Anteprima Risultati";
    anteprimaMod = "MRecuperaAnteprimaPesato";
    prgTipoIncrocio = "2";
    break;
  case 3:
    titolo = "Matching Incrementale - Anteprima Risultati";
    anteprimaMod = "M_MatchIncrementale";
    break;
  default:
    titolo = "";
    anteprimaMod = "";
    break;
  }
  
  
String MATCH_OK = "";
SourceBean rowMatch = null;
String errCode = "";
int CodiceRit = 0;
SourceBean risMatch = null;
SourceBean rowRisMatch = null;

if(EM==1 || EM==2) {
  risMatch = (SourceBean) serviceResponse.getAttribute("MESEGUIMATCHING");
  MATCH_OK = StringUtils.getAttributeStrNotNull(risMatch, "MATCH_OK");
  rowRisMatch = (SourceBean) risMatch.getAttribute("ROW");
  CodiceRit = Integer.parseInt(rowRisMatch.getAttribute("CodiceRit").toString());
  errCode = StringUtils.getAttributeStrNotNull(rowRisMatch, "errCode");
}

SourceBean matchCont = (SourceBean) serviceResponse.getAttribute(anteprimaMod);
if(matchCont != null) {
  rowMatch = (SourceBean) matchCont.getAttribute("ROW");
}

if(CodiceRit == -1) { CodiceRit = 3; }
String errMsg[] = { "Estrazione avvenuta correttamente",
                    "L'estrazione non è stata possibile in quanto nella richiesta non è stata specificata alcuna mansione.",
                    "Non &egrave; stato possibile eseguire l'incrocio in quanto la richiesta &egrave; stata chiusa.",
                    "Errore nell'esecuzione della procedura."
                  };
                  


String prgIncrocio = "";
String prgRosa = "";
String numEstratti = "";
String numDis = "";
String numOccupati = "";
String numKloRosa = "";
String numKloIncrocio = "";
String numFascia1 = "";
String numFascia2 = "";
String numFascia3 = "";
String numFascia4 = "";
String numFascia5 = "";
String numFascia6 = "";
String numFascia7 = "";
String numFascia8 = "";
String numFascia9 = "";
String numFascia10 = "";
String numFascia11 = "";

if(rowMatch!=null && CodiceRit == 0) {
  prgIncrocio = StringUtils.getAttributeStrNotNull(rowMatch, "PRGINCROCIO");
  prgRosa = StringUtils.getAttributeStrNotNull(rowMatch, "PRGROSA");
  numEstratti = StringUtils.getAttributeStrNotNull(rowMatch, "NUMESTRATTI");
  numDis = StringUtils.getAttributeStrNotNull(rowMatch, "NUMDIS");
  numOccupati = StringUtils.getAttributeStrNotNull(rowMatch, "NUMOCC");
  numKloRosa = StringUtils.getAttributeStrNotNull(rowMatch, "NUMKLOROSA");
  numKloIncrocio = StringUtils.getAttributeStrNotNull(rowMatch, "NUMKLOINCROCIO");
  if(EM==2 || EM==5) {
    numFascia1 = StringUtils.getAttributeStrNotNull(rowMatch, "NUMFASCIA1");
    numFascia2 = StringUtils.getAttributeStrNotNull(rowMatch, "NUMFASCIA2");
    numFascia3 = StringUtils.getAttributeStrNotNull(rowMatch, "NUMFASCIA3");
    numFascia4 = StringUtils.getAttributeStrNotNull(rowMatch, "NUMFASCIA4");
    numFascia5 = StringUtils.getAttributeStrNotNull(rowMatch, "NUMFASCIA5");
    numFascia6 = StringUtils.getAttributeStrNotNull(rowMatch, "NUMFASCIA6");
    numFascia7 = StringUtils.getAttributeStrNotNull(rowMatch, "NUMFASCIA7");
    numFascia8 = StringUtils.getAttributeStrNotNull(rowMatch, "NUMFASCIA8");
    numFascia9 = StringUtils.getAttributeStrNotNull(rowMatch, "NUMFASCIA9");
    numFascia10 = StringUtils.getAttributeStrNotNull(rowMatch, "NUMFASCIA10");
    numFascia11 = StringUtils.getAttributeStrNotNull(rowMatch, "NUMFASCIA11");
  }
}
String cpiRose = user.getCodRif();
String _cdnFunzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();


// Attributi della pagina GestIncrocioPage
PageAttribs attrIncrocio = new PageAttribs(user, "GestIncrocioPage");
boolean gestCopia = attrIncrocio.containsButton("GEST_COPIA");
String prgC1 = C1;

boolean viewPar = true;
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title><%=titolo%></title>
  <af:linkScript path="../../js/" />
  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
  <script language="Javascript">
  function match_sub(n)
  {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var pag = "";
    var ok = false;
    switch(n) {
      case 1 :
        pag = "MatchDettRosaPage";
        ok = true;
        break;
      case 2 :
        pag = "GestIncrocioPage";
        ok = true;
        break;
      default :
        pag = "";
        ok = false;
        break;
      }
    
    if(ok) {      
      document.form_match.PAGE.value = pag;
      if(n==2) { document.form_match.PRGRICHIESTAAZ.value = "<%=C1%>"; }
      //alert(document.form_match.PRGRICHIESTAAZ.value);
      doFormSubmit(document.form_match);
    } else {
      avviso();
    }
  }
  
  function openPar_Pesato_PopUP() {
     window.open ("AdapterHTTP?PAGE=MatchViewParPesatoPage&PRGINCROCIO=<%=prgIncrocio%>", "Parametri", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES,left=150,top=100');
  }
  
  function openPar_Esatto_PopUP() {
     window.open ("AdapterHTTP?PAGE=MatchViewParEsattoPage&PRGINCROCIO=<%=prgIncrocio%>", "Parametri", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES,left=150,top=100');
  }
  </script>
  
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_cdnFunzione%>,<%=prgAzienda%>, <%=prgUnita%>);
  </script>
</head>

<body class="gestione" onload="window.status='Operazione completata';rinfresca()">
<%@ include file="InfoCorrRichiesta.inc" %>
<h2><%=titolo%></h2>
<af:form name="form_match" action="AdapterHTTP" method="GET" dontValidate="true">
<input name="PAGE" type="hidden" value=""/>
<input name="PRGRICHIESTAAZ" type="hidden" value="<%=prgRichiestaAz%>"/>
<input name="PRGORIG" type="hidden" value="<%=prgOrig%>"/>
<input name="C1" type="hidden" value="<%=C1%>"/>
<input name="PRGAZIENDA" type="hidden" value="<%=prgAzienda%>"/>
<input name="PRGUNITA" type="hidden" value="<%=prgUnita%>"/>
<input name="CPIROSE" type="hidden" value="<%=cpiRose%>"/>

<input type="hidden" name="PRGINCROCIO" value="<%=prgIncrocio%>"/>
<input type="hidden" name="PRGROSA" value="<%=prgRosa%>"/>
<input type="hidden" name="NUMKLOROSA" value="<%=numKloRosa%>"/>
<input type="hidden" name="NUMKLOINCROCIO" value="<%=numKloIncrocio%>"/>
<input type="hidden" name="MODULE" value="SRG"/>
<input type="hidden" name="flgIncMir" value="<%=flgincrociomirato%>"/>
<input type="hidden" name="DAMATCH" value="<%=daMatch%>"/>

<input name="CDNFUNZIONE" type="hidden" value="<%=_cdnFunzione%>"/>
<input name="codiceRit" type="hidden" value="<%=CodiceRit%>" disabled/>
<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<%out.print(htmlStreamTop);%>
<table class="main">
<tr>
  <td colspan="2"><div class="sezione2">Risultati</div></td>
</tr>
<%if(CodiceRit==0) {%>
  <tr>
    <td class="etichetta">Totale Candidati</td>
    <td class="campo"><b><%=numEstratti%></b></td>
  </tr>
  <tr>
    <td class="etichetta">Num. Disoccupati/Inoccupati</td>
    <td class="campo"><b><%=numDis%></b></td>
  </tr>
  <tr>
    <td class="etichetta">Num. Occupati</td>
    <td class="campo"><b><%=numOccupati%></b></td>
  </tr>
  <%if(EM==2 || EM==5) {%>
      <tr><td colspan="2"><div class="sezione2">Risultati Pesatura</div></td></tr>
      <tr>
        <td class="etichetta">Num. con peso 100</td>
        <td class="campo"><b><%=numFascia1%></b></td>
      </tr>
      <tr>
        <td class="etichetta">Num. con peso tra 90 e 99</td>
        <td class="campo"><b><%=numFascia2%></b></td>
      </tr>
      <tr>
        <td class="etichetta">Num. con peso tra 80 e 89</td>
        <td class="campo"><b><%=numFascia3%></b></td>
      </tr>
      <tr>
        <td class="etichetta">Num. con peso tra 70 e 79</td>
        <td class="campo"><b><%=numFascia4%></b></td>
      </tr>
      <tr>
        <td class="etichetta">Num. con peso tra 60 e 69</td>
        <td class="campo"><b><%=numFascia5%></b></td>
      </tr>
      <tr>
        <td class="etichetta">Num. con peso tra 50 e 59</td>
        <td class="campo"><b><%=numFascia6%></b></td>
      </tr>
      <tr>
        <td class="etichetta">Num. con peso tra 40 e 49</td>
        <td class="campo"><b><%=numFascia7%></b></td>
      </tr>
      <tr>
        <td class="etichetta">Num. con peso tra 30 e 39</td>
        <td class="campo"><b><%=numFascia8%></b></td>
      </tr>
      <tr>
        <td class="etichetta">Num. con peso tra 20 e 29</td>
        <td class="campo"><b><%=numFascia9%></b></td>
      </tr>
      <tr>
        <td class="etichetta">Num. con peso tra 10 e 19</td>
        <td class="campo"><b><%=numFascia10%></b></td>
      </tr>
      <tr>
        <td class="etichetta">Num. con peso tra 0 e 9</td>
        <td class="campo"><b><%=numFascia11%></b></td>
      </tr>
  <%}%>
  
<%} else {%>
  <tr>
    <td colspan="2" align="justify"><b><%=errMsg[CodiceRit]%></b></td>
  </tr>
<%}%>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  <%if(CodiceRit==0) {%>
    <input class="pulsanti" type="button" name="sub" value="Salva Rosa Grezza" onClick="match_sub(1)">
    &nbsp;&nbsp;
  <%}%>
  <input class="pulsanti" type="button" name="back" value="Chiudi" onclick="match_sub(2)" />
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
</table>
<%out.print(htmlStreamBottom);%>

</af:form>
  
</body>
</html>
