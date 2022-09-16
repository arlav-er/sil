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
String prgOrig = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();

//String _page = serviceRequest.getAttribute("PAGE").toString();
//PageAttribs attributi = new PageAttribs(user, _page);

String p_codCpi = user.getCodRif();
int nroMansioni = 0;

int EM = Integer.parseInt(serviceRequest.getAttribute("EM").toString());
String titolo = "Matching Mobilità - Risultati";
String anteprimaMod = "";
int daMatch=1;
  
  
String MATCH_OK = "";
String errCode = "";
int CodiceRit = 0;
SourceBean risMatch = null;
SourceBean rowRisMatch = null;

risMatch = (SourceBean) serviceResponse.getAttribute("MEseguiMatchingMobilita");
rowRisMatch = (SourceBean) risMatch.getAttribute("ROW");
CodiceRit = Integer.parseInt(rowRisMatch.getAttribute("CodiceRit").toString());
errCode = StringUtils.getAttributeStrNotNull(rowRisMatch, "errCode");

if(CodiceRit == -1) { CodiceRit = 3; }
String errMsg[] = { "Estrazione avvenuta correttamente",
                    "L'estrazione non è stata possibile in quanto nella richiesta non è stata specificata alcuna mansione.",
                    "Non &egrave; stato possibile eseguire l'incrocio in quanto la richiesta &egrave; stata chiusa.",
                    "Errore nell'esecuzione della procedura.",
                    "Non &egrave; stata valorizzata la data di pubblicazione della richiesta. "
                  };

Object prgIncrocio = "";
Object prgRosa = "";
Object numEstratti = "";
Object cdnStatoRich = "";
Object prgTipoIncrocio = "";   
Object prgTipoRosa = "";
Object cpi = "";
     
SourceBean rowMatch = (SourceBean) serviceResponse.getAttribute("MInfoIncrocioMobilita.ROWS.ROW");

if(rowMatch!=null && CodiceRit == 0) {
  prgIncrocio = rowMatch.getAttribute("PRGINCROCIO");
  prgRosa = rowMatch.getAttribute("PRGROSA");
  prgTipoIncrocio = rowMatch.getAttribute("PRGTIPOINCROCIO");
  prgTipoRosa = rowMatch.getAttribute("PRGTIPOROSA");
  numEstratti = rowMatch.getAttribute("NUMESTRATTI");
  cdnStatoRich = rowMatch.getAttribute("CDNSTATORICH");
  cpi = rowMatch.getAttribute("CODCPI");
  
}
                  
String cpiRose = user.getCodRif();
String _cdnFunzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();


// Attributi della pagina GestIncrocioPage
PageAttribs attrIncrocio = new PageAttribs(user, "GestIncrocioPage");
boolean gestCopia = false; //attrIncrocio.containsButton("GEST_COPIA");    
String prgC1 = prgRichiestaAz;

boolean viewPar = false;
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
        pag = "ASMatchDettGraduatoriaPage";
        mod = "ASCandidatiGraduatoria";
        ok = true;
        break;
      case 2 :
        pag = "GestIncrocioPage";
        mod = "";
        ok = true;
        break;
      default :
        pag = "";
        ok = false;
        break;
      }
    
    if(ok) {      
      document.form_match.PAGE.value = pag;
      document.form_match.MODULE.value = mod;
      if(n==2) { 
      	document.form_match.PRGRICHIESTAAZ.value = "<%=prgRichiestaAz%>";       	
      }
      doFormSubmit(document.form_match);
    } else {
      avviso();
    }
  }
  
  </script>
  
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_cdnFunzione%>,<%=prgAzienda%>, <%=prgUnita%>);
  </script>
</head>

<body class="gestione" onload="window.status='Operazione completata';rinfresca();">
<%@ include file="InfoCorrRichiesta.inc" %>
<h2><%=titolo%></h2>
<af:form name="form_match" action="AdapterHTTP" method="GET" dontValidate="true">
<input name="PAGE" type="hidden" value=""/>
<input name="MODULE" type="hidden" value=""/>
<input name="DAMATCH" type="hidden" value="9"/>
<input name="CALC_POSIZIONE" type="hidden" value="ASCalcolaPosizioneModule"/>
<input name="PRGROSA" type="hidden" value="<%=prgRosa%>"/>
<input name="PRGTIPOROSA" type="hidden" value="<%=prgTipoRosa%>"/>
<input name="PRGRICHIESTAAZ" type="hidden" value="<%=prgRichiestaAz%>"/>
<input name="CDNSTATORICH" type="hidden" value="<%=cdnStatoRich%>"/>
<input name="PRGTIPOINCROCIO" type="hidden" value="<%=prgTipoIncrocio%>"/>
<input name="codCpi" type="hidden" value="<%=cpi%>"/>   
<input name="PRGORIG" type="hidden" value="<%=prgOrig%>"/>
<input name="PRGAZIENDA" type="hidden" value="<%=prgAzienda%>"/>
<input name="PRGUNITA" type="hidden" value="<%=prgUnita%>"/>
<input name="CPIROSE" type="hidden" value="<%=cpiRose%>"/>

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
