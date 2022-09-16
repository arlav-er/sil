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
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
String daMatch = StringUtils.getAttributeStrNotNull(serviceRequest, "DAMATCH");
String _page = serviceRequest.getAttribute("PAGE").toString();
String _cdnFunzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();
boolean errStoricizzaRichiesta = false;
SourceBean contStoricizz = (SourceBean) serviceResponse.getAttribute("M_STORICIZZARICHIESTA");
// Determino il progressivo della copia di lavoro (il valore mi viene passato o dalla Storicizzazione o dalla request)
String prgC1 = "";
if(contStoricizz!=null) { prgC1 =StringUtils.getAttributeStrNotNull(contStoricizz,"PRGRICHIESTAAZ"); }
if(prgC1.equals("")) { prgC1 = prgRichiestaAz; }
errStoricizzaRichiesta = Utils.notNull(serviceResponse.getAttribute("M_STORICIZZARICHIESTA.row.codiceRit")).equals("-1");
String backFun = "";
//if(daMatch.equals("1")) { backFun = "match_sub(10)"; }
//else { backFun = "javascript:history.go(-1)"; }
backFun = "match_sub(10)";

PageAttribs attributi = new PageAttribs(user, "GestIncrocioPage");

boolean matchEsatto = attributi.containsButton("MATCH_ESATTO");
boolean matchPesato = attributi.containsButton("MATCH_PESATO");
// il match incrementale e' stato sospeso
boolean matchIncr = attributi.containsButton("MATCH_INCR");       
matchIncr = false;
boolean matchAnteprima = attributi.containsButton("MATCH_ANTEPRIMA");
boolean gestRose = attributi.containsButton("GEST_ROSE");
boolean gestCopia = attributi.containsButton("GEST_COPIA");
boolean riallineaCopia = attributi.containsButton("RIALLINEA");
boolean nuovaRosaNominativa = attributi.containsButton("NUOVA_RNG");
boolean canGraduatoria = attributi.containsButton("GRADUATORIA");

/* Settare in base al risultato del modulo MCercaUltimaAnteprima */
boolean eAnteprima = false;
//SourceBean contRosa = (SourceBean) serviceResponse.getAttribute("MCERCAULTIMAANTEPRIMA");
// Andrea Savino 18/04/05 - mancata visualizzazione del pulsante recupera anteprima
SourceBean rowRosa = (SourceBean) serviceResponse.getAttribute("MCERCAULTIMAANTEPRIMA.ROWS.ROW");
String prgRosa = "";
String prgIncrocio = "";
String prgTipoIncrocio = "";
String prgCN = "";
if(rowRosa!=null) {
  eAnteprima = true;
  prgRosa = rowRosa.getAttribute("PRGROSA").toString();
  prgIncrocio = rowRosa.getAttribute("PRGINCROCIO").toString();
  prgTipoIncrocio = rowRosa.getAttribute("PRGTIPOINCROCIO").toString();
  prgCN = rowRosa.getAttribute("CN").toString();
}
String emValue = "";
if(prgTipoIncrocio.equals("1")) { emValue = "4"; }
if(prgTipoIncrocio.equals("2")) { emValue = "5"; }

//	INIT-PARTE-TEMP
if (Sottosistema.AS.isOff()) {	
// END-PARTE-TEMP

// INIT-PARTE-TEMP
} else {
// END-PARTE-TEMP
	
	if (("").equalsIgnoreCase(emValue)) {
		eAnteprima = false;
	}

// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
int nroMansioni = 0;

String cpiRose = user.getCodRif();
%>

<%
String token = "";
String urlDiLista = "";
String refListaBtn = "";
String refLista = "";
if (sessionContainer!=null){
  token = "_TOKEN_" + "IDOLISTARICHIESTEPAGE";
  urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
  if (urlDiLista!=null) {
    refListaBtn ="<a href=\"AdapterHTTP?" + urlDiLista + "\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></a>";
    refLista = "prepareSubmit();window.open('AdapterHTTP?" + urlDiLista + "', '_self')";
  } else { urlDiLista = ""; }
}
String htmlStreamTopInfo = StyleUtils.roundTopTableInfoRetLista(urlDiLista);
//String htmlStreamTopInfo = StyleUtils.roundTopTableInfo();
String htmlStreamBottomInfo = StyleUtils.roundBottomTableInfoNobr();
//String htmlStreamBottomInfo = StyleUtils.roundBottomTableInfo();
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

//SourceBean contStato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG");
SourceBean sbStato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG.ROWS.ROW");
String codEvasione = StringUtils.getAttributeStrNotNull(sbStato, "CODEVASIONE");
String cdnStatoRich = StringUtils.getAttributeStrNotNull(sbStato, "CDNSTATORICH");
String strStatoRichOrig = StringUtils.getAttributeStrNotNull(sbStato, "STATORICHORIG");
boolean flgIncrocio = StringUtils.getAttributeStrNotNull(sbStato, "FLGINCROCIO").equals("S");
String flgGraduatoria  = (String)sbStato.getAttribute("FLGGRADUATORIA");
String flagPubblicata = StringUtils.getAttributeStrNotNull(sbStato, "Flgpubblicata");
String flagCresco = StringUtils.getAttributeStrNotNull(sbStato, "Flgpubbcresco");
String dataFineRich = StringUtils.getAttributeStrNotNull(sbStato, "dataFine");
String dataUltimoInvioVacancy = StringUtils.getAttributeStrNotNull(sbStato, "dataUltimoInvioVacancy");

String dataOggi = DateUtils.getNow();
if("S".equals(flagCresco)){
	if (!"S".equals(flagPubblicata) || "".equals(dataUltimoInvioVacancy) || DateUtils.compare(dataFineRich, dataOggi) >= 0) {
		flgIncrocio = false;
	}
}
%>

<%
//SourceBean cont = (SourceBean) serviceResponse.getAttribute("MINFORICHIESTAORIG");
SourceBean infoRich = (SourceBean) serviceResponse.getAttribute("MINFORICHIESTAORIG.ROWS.ROW");
//cont = (SourceBean) serviceResponse.getAttribute("MINFORICHMANSIONI");
Vector infoRichMan = serviceResponse.getAttributeAsVector("MINFORICHMANSIONI.ROWS.ROW");
SourceBean rowMan = null;
int i = 0;
if(infoRichMan.size()>0) { 
	nroMansioni = infoRichMan.size(); 
}
String _indirizzo = "";
String ragSoc = StringUtils.getAttributeStrNotNull(infoRich,"STRRAGIONESOCIALE");
String numRichiesta = StringUtils.getAttributeStrNotNull(infoRich,"NUMRICHIESTA");
String numRichiestaOrig = StringUtils.getAttributeStrNotNull(infoRich,"NUMRICHIESTAORIG");
String numAnno = StringUtils.getAttributeStrNotNull(infoRich,"NUMANNO");
String dataRich = StringUtils.getAttributeStrNotNull(infoRich,"DATRICHIESTA");
String dataScad = StringUtils.getAttributeStrNotNull(infoRich,"DATSCADENZA");
String stato_richiesta = StringUtils.getAttributeStrNotNull(infoRich,"stato_richiesta");
String stato_evasione = StringUtils.getAttributeStrNotNull(infoRich,"stato_evasione");
BigDecimal numpostomb = (BigDecimal)infoRich.getAttribute("numpostomb");
String codCpi = StringUtils.getAttributeStrNotNull(infoRich,"CODCPI");
String descCpi = StringUtils.getAttributeStrNotNull(infoRich,"desc_cpi");
String indir = StringUtils.getAttributeStrNotNull(infoRich,"STRINDIRIZZO");
String cap = StringUtils.getAttributeStrNotNull(infoRich,"STRCAP");
String loc = StringUtils.getAttributeStrNotNull(infoRich,"STRLOCALITA");
String comune = StringUtils.getAttributeStrNotNull(infoRich,"comune_az");
String targa = StringUtils.getAttributeStrNotNull(infoRich,"STRTARGA");

String prgOrig = infoRich.getAttribute("PRGRICHIESTAAZ").toString();
if (errStoricizzaRichiesta) prgC1 = prgOrig;
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


<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Matching</title>
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
        pag = "ParMatchEsattoPage";
        document.form_match.PRGRICHIESTAAZ.value = "<%=prgC1%>";
        ok = true;
        break;
      case 2 :
        pag = "ParMatchPesatoPage";
        document.form_match.PRGRICHIESTAAZ.value = "<%=prgC1%>";
        ok = true;
        break;
      case 4 :
        pag = "MatchAnteprimaPage";
        document.form_match.PRGRICHIESTAAZ.value = "<%=prgCN%>";
        ok = true;
        break;
      case 5 :
        pag = "MatchListaRosePage";
        document.form_match.PRGRICHIESTAAZ.value = "<%=prgC1%>";
        ok = true;
        break;
      case 6 :
        pag = "MatchDettRosaPage";
        document.form_match.PRGRICHIESTAAZ.value = "<%=prgOrig%>";
        document.form_match.MODULE.disabled=false;
        document.form_match.CRNG_MODULE.disabled=false;
        ok = true;
        break;
      case 7 :
        pag = "ParMatchMobilitaPage";
        document.form_match.PRGRICHIESTAAZ.value = "<%=prgC1%>";
        ok = true;
        break;  
      case 10 :
        pag = "IdoListaRichiestePage";
        document.form_match.PRGRICHIESTAAZ.value = "";
        ok = true;
        break;
      default :
        pag = "";
        ok = false;
        break;
      }
    //if(n==3) { document.form_match.PAGE.value="ParMatchIncremPage"; }
    //if(n==5) { document.form_match.PAGE.value="GestRosePage"; }
    if(ok) {      
      document.form_match.PAGE.value = pag; 
      //alert("PRGRICHIESTAAZ=" + document.form_match.PRGRICHIESTAAZ.value);
      doFormSubmit(document.form_match);
    } else {
      avviso();
    }
  }
  function openRich_PopUP(prgRich) {
     window.open ("AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=" +prgRich +"&CDNFUNZIONE=<%=_cdnFunzione%>&POPUP=1", "DettaglioSintetico", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
  }
  
  function openRich_Gestione(prgRich) {
    var w = "AdapterHTTP?PAGE=IdoTestataRichiestaPage&PRGRICHIESTAAZ=" +prgRich +
            "&CDNFUNZIONE=<%=_cdnFunzione%>" +
            "&PRGAZIENDA=<%=prgAzienda%>" +
            "&PRGUNITA=<%=prgUnita%>" +
            "&ret=GestIncrocioPage&PRGORIG=<%=prgOrig%>";
    var t = "_self";
    window.open(w, t);
  }
  <%-- il prgRich passato e' il prgOrig (vedi chiamata funzione)  --%>
  function resetCopiaRichiesta(prgRich){
  
  	if ( isInSubmit() ){
  		return;
  	}
  	 
  	if (confirm("Vuoi riallineare la copia di lavoro con la richiesta originale?")){
  		prepareSubmit();
  	 	var w = "AdapterHTTP?PAGE=GestIncrocioPage&PRGRICHIESTAAZ=" +prgRich +
            "&CDNFUNZIONE=<%=_cdnFunzione%>" +
            "&PRGAZIENDA=<%=prgAzienda%>" +
            "&PRGUNITA=<%=prgUnita%>" +
            "&ret=GestIncrocioPage&PRGORIG=<%=prgOrig%>"+
            "&resetCopia=true"+
            "&StoricizzaRichiesta=true";
    		var t = "_self";
    		window.open(w, t);
     }
  }
  
  </script>
  
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_cdnFunzione%>,<%=prgAzienda%>, <%=prgUnita%>);
  </script>

</head>

<body class="gestione" onload="rinfresca();">


<%if(infoRich!=null) {%>
  <br>
  <%out.print(htmlStreamTopInfo);%>
  <p class="info_lav">
  <a class="info_lav" href="#" onClick="openRich_PopUP('<%=prgOrig%>')"><img src="../../img/info_soggetto.gif" alt="Dettaglio"/></a>&nbsp;<strong>Richiesta num. <%=numRichiestaOrig%>/<%=numAnno%></strong>
  <br>
  Data Richiesta <strong><%=dataRich%></strong>&nbsp;&nbsp;-&nbsp;&nbsp;
  Data Scadenza <strong><%=dataScad%></strong>
  <br>
  Stato della Richiesta <strong><%=stato_richiesta%></strong>&nbsp;&nbsp;
  -&nbsp;&nbsp;
  Stato di Evasione <strong><%=strStatoRichOrig%></strong>&nbsp;&nbsp;
  <br>
  Richiedente <strong><%=ragSoc%></strong>&nbsp;&nbsp;
  <strong><%= _indirizzo%></strong>
  </p>
  <%out.print(htmlStreamBottomInfo);%>
<%}%>

<af:showMessages prefix="ResetCopiaRichiesta"/>

<h2>Gestione Incrocio</h2>

<%if (errStoricizzaRichiesta) {%>
	<%-- SI E' VERIFICATO UN ERRORE NELLA STORICIZZAZIONE DELLA RICHIESTA --%>
	<%out.print(htmlStreamTop);%>
	<P>Si è verificato un errore nella storicizzazione della richiesta.</P>
	<button class="pulsante" onclick="openRich_Gestione('<%=prgOrig%>')">Dettaglio richiesta</button>
	<%if(!refLista.equals("")) {%>
	  <!--td colspan="2" align="center"><input class="pulsante" type="button" name="back" value="Torna alla lista" onclick="<%=backFun%>;" /></td-->
	  <button class="pulsante" onclick="<%=refLista%>;" />Torna alla lista</button>
	<%}%>
	<%out.print(htmlStreamBottom);%>
<%
} else {%>        
<%--------------------------------------------------------------------------------------%>
<%-- NON CI SONO STATI ERRORI NELLA STORICIZZAZIONE. LA PAGINA VIENE CARICATA IN TOTO --%>
<%--                                                                                  --%>
<af:form name="form_match" action="AdapterHTTP" method="POST">
<input name="PAGE" type="hidden" value=""/>
<input name="PRGRICHIESTAAZ" type="hidden" value="<%=prgRichiestaAz%>"/>
<input name="C1" type="hidden" value="<%=prgC1%>"/>
<input name="PRGORIG" type="hidden" value="<%=prgOrig%>"/>
<input name="PRGAZIENDA" type="hidden" value="<%=prgAzienda%>"/>
<input name="PRGUNITA" type="hidden" value="<%=prgUnita%>"/>
<input name="CERCA" type="hidden" value="cerca"/>
<input name="EM" type="hidden" value="<%=emValue%>"/>
<input name="PRGROSA" type="hidden" value="<%=prgRosa%>"/>
<input name="PRGINCROCIO" type="hidden" value="<%=prgIncrocio%>"/>
<input name="CDNFUNZIONE" type="hidden" value="<%=_cdnFunzione%>"/>
<input name="CPIROSE" type="hidden" value="<%=cpiRose%>"/>
<%-- parametri necessari per la funzione 'nuova rosa nominativa'  --%>
<input name="CDNSTATORICH" type="hidden" value="<%=cdnStatoRich%>"/>
<input name="P_CDNUTENTE" type="hidden" value="<%=user.getCodut()%>"/>
<input name="MODULE" type="hidden" value="CRNG" disabled/>
<input name="CRNG_MODULE" type="hidden" value="Yes" disabled/>


<%

//SourceBean contCopia = (SourceBean) serviceResponse.getAttribute("MINFORICHIESTA");

// Le informazioni estratte si riferiscono alla copia di lavoro se abilitata o alla richesta originale 
// se la copia non e' abilitata
SourceBean infoRichCopia = (SourceBean) serviceResponse.getAttribute("MINFORICHIESTA.ROWS.ROW");
if (infoRichCopia==null) infoRichCopia = new SourceBean("ROW");
//contCopia = (SourceBean) serviceResponse.getAttribute("MINFORICHMANSIONI");
Vector infoRichManCopia = serviceResponse.getAttributeAsVector("MINFORICHMANSIONI.ROWS.ROW");
SourceBean rowManCopia = null;
i = 0;
int nroMansioniCopia = 0;
if(infoRichManCopia.size()>0) { 
	nroMansioniCopia = infoRichManCopia.size(); 
}
dataRich = StringUtils.getAttributeStrNotNull(infoRichCopia,"DATRICHIESTA");
dataScad = StringUtils.getAttributeStrNotNull(infoRichCopia,"DATSCADENZA");
codCpi = StringUtils.getAttributeStrNotNull(infoRichCopia,"CODCPI");
descCpi = StringUtils.getAttributeStrNotNull(infoRichCopia,"desc_cpi");
utIns = StringUtils.getAttributeStrNotNull(infoRichCopia, "UT_INS");
utMod = StringUtils.getAttributeStrNotNull(infoRichCopia, "UT_MOD");
%>
    
<%out.print(htmlStreamTop);%>
<table class="main">
<tr>
  <td colspan="2"><div class="sezione2">Matching</div></td>
</tr>
<%if("S".equals(flagCresco) && !(cdnStatoRich.equals("4") || cdnStatoRich.equals("5")) && !flgIncrocio) {%>
<tr>
  <td align="left" colspan="2">
	<%if("".equals(dataUltimoInvioVacancy)) {%>
		Il matching non pu&ograve; essere effettuato perch&egrave; la richiesta di personale <b>Cre.s.c.o</b> non &egrave; stata ancora pubblicata sul Portale.
	<%} else if(DateUtils.compare(dataFineRich, dataOggi) >= 0) {%>
		Il matching non pu&ograve; essere effettuato perch&egrave; la richiesta di personale <b>Cre.s.c.o</b> risulta ancora valida. L'operazione sar&agrave; consentita a partire dal giorno <b><%=DateUtils.giornoSuccessivo(dataFineRich)%></b>.
	<%}%>
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>	
<%}%>
<%if(gestCopia && flgIncrocio) {%>
    <tr>
      <td align="left" colspan="2">
        <%if (!(cdnStatoRich.equals("4") || cdnStatoRich.equals("5"))) {%>
        <a class="info_lav" href="#" onClick="openRich_PopUP('<%=prgC1%>')"><img src="../../img/copiarich.gif" alt="Dettaglio"/></a>
        &nbsp;        
        <a class="info_lav" href="#" onClick="openRich_Gestione('<%=prgC1%>')"><img src="../../img/detail.gif" alt="Gestione Copia di Lavoro"/></a>
        &nbsp;
        <% if(riallineaCopia){ %>
        	<input type="button" onClick="resetCopiaRichiesta('<%=prgOrig%>')" class="pulsanti" value="Riallinea copia">&nbsp;
        <% } %>
        <b>Copia di lavoro</b>
        <%}%>
      </td>
    </tr>
<%}%>
<tr>
  <td class="etichetta">CpI di riferimento</td>
  <td class="campo"><strong><%=codCpi%> - <%=descCpi%></strong></td>
</tr>
<tr valign="top">
  <td class="etichetta">Mansioni richieste:&nbsp;</td>
  <td class="campo">
    <%if(nroMansioniCopia>0) {%>
    <table align="left" maxwidth="98%">
        <%
        String nroAlternativa = "";
        String nroAlt = "";
        int nl = 0;
        %>
        <%for(i=0; i < nroMansioni; i++) {%>
          <%
          rowMan = (SourceBean) infoRichMan.elementAt(i); 
          nroAlternativa = StringUtils.getAttributeStrNotNull(rowMan, "PRGALTERNATIVA");
          if(!nroAlt.equals(nroAlternativa)) {
            if(nl >0 ) {
          %>
              </ul></td></tr>
            <%}%>
            <tr valign="top"><td><b>Profilo&nbsp;n.&nbsp;<%=nroAlternativa%></b></td><td><ul type="square">
            <%
            nl += 1;
            nroAlt = nroAlternativa;
            %>
          <%}%>
          <li><b><%=StringUtils.getAttributeStrNotNull(rowMan,"MANSIONE")%></b></li>
        <%}%>
      </ul>
      </td></tr>
      </table>
    <%}%>   
  </td>
</tr>
<%--
<tr>
  <td class="etichetta">Inserimento</td>
  <td class="campo"><strong><%=utIns%></strong></td>
</tr>
<tr>
  <td class="etichetta">Ultima modifica</td>
  <td class="campo"><strong><%=utMod%></strong></td>
</tr>
--%>

<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td align="center" colspan="2">
  <%if(matchEsatto && flgIncrocio && !cdnStatoRich.equals("4") && !cdnStatoRich.equals("5")) {%>
    <input type="button" onClick="match_sub(1);" class="pulsanti" value="Matching Esatto">
        &nbsp;&nbsp; 
  <%}%>
  <%if(matchPesato && flgIncrocio && !cdnStatoRich.equals("4") && !cdnStatoRich.equals("5")) {%>
    <input type="button" onClick="match_sub(2);" class="pulsanti" value="Matching Pesato">
        &nbsp;&nbsp;
  <%}%>
  <%if(matchIncr) {%>
    <input type="button" onClick="match_sub(3);" class="pulsanti" value="Matching Incrementale" disabled>
    &nbsp;&nbsp;
  <%}%>

<%
//	INIT-PARTE-TEMP
if (Sottosistema.MO.isOff()) {	
// END-PARTE-TEMP
%>

<%
// INIT-PARTE-TEMP
} else {
// END-PARTE-TEMP
%>	

  <%
  if(("S").equalsIgnoreCase(flgGraduatoria)){
  	if (numpostomb.compareTo((new BigDecimal("0"))) != 0) {    
  %>
  		<input type="button" onClick="match_sub(7);" class="pulsanti" value="Matching Mobilità">
    	&nbsp;&nbsp;
  <%
  	}
  }
  %>

<%
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>  
  
<%  
	if(!("S").equalsIgnoreCase(flgGraduatoria)){
  		if(nuovaRosaNominativa && !cdnStatoRich.equals("5")) {%>  	 
     	 	<input type="button" onClick="match_sub(6);" class="pulsanti" value="Nuova Rosa Nominativa">
<%	
  		}
    }%>
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2"><div class="sezione2">Rose</div></td>
</tr>
<%
if (serviceResponse.containsAttribute("MATCHREPORTROSENOMINATIVE.ROWS.ROW")) {
	SourceBean repRoseNom = (SourceBean)serviceResponse.getAttribute("MATCHREPORTROSENOMINATIVE.ROWS.ROW");
	String numGrezzeNom = StringUtils.getAttributeStrNotNull(repRoseNom,"numGrezzeNom");
	String numDefNom = StringUtils.getAttributeStrNotNull(repRoseNom,"numDefNom");
	if (!(numGrezzeNom.equals("0") && numDefNom.equals("0"))) {	
%>
	<tr valign="top">
        <td class="etichetta"><b>Rose nominative</b></td>
        <td class="campo">
          <table  align="left" >
          <tr>
            <td>Definitive:</td>            
            <td align="left"><%=numDefNom%></td>
            <td width="10%">&nbsp;</td>
          </tr>
          <tr>
            <td>Grezze:</td>
            <td align="left"><%=numGrezzeNom%></td>
            <td width="10%">&nbsp;</td>                
          </tr>		
	      </table>
	   </td>
	</tr>	
<%  }
}


//SourceBean contRep = (SourceBean) serviceResponse.getAttribute("MATCHREPORTROSE");
Vector rowsRep = serviceResponse.getAttributeAsVector("MATCHREPORTROSE.ROWS.ROW");
SourceBean rep = null;
if(flgIncrocio && rowsRep.size()>0) {
%>
    <%for(i=0; i<rowsRep.size(); i++) {%>
      <%rep = (SourceBean) rowsRep.elementAt(i);%>
      <tr valign="top">
        <td class="etichetta"><b>Profilo n. <%=StringUtils.getAttributeStrNotNull(rep, "PRGALTERNATIVA")%></b></td>
        <td class="campo">
          <table maxwidth="95%" width="90%" align="left">
          <tr>
            <td>Rose Definitive:</td>
            <%if(matchEsatto) {%>
                <td align="right"><%=StringUtils.getAttributeStrNotNull(rep, "NRODEFESATTO")%></td>
                <td>da&nbsp;Matching Esatto</td>
            <%}%>
            <%if(matchPesato) {%>
                <td align="right"><%=StringUtils.getAttributeStrNotNull(rep, "NRODEFPESATO")%></td>
                <td>da&nbsp;Matching Pesato</td>
            <%}%>
          </tr>
          <tr>
            <td>Rose Grezze:</td>
            <%if(matchEsatto) {%>
                <td align="right"><%=StringUtils.getAttributeStrNotNull(rep, "NROGREZZEESATTO")%></td>
                <td>da&nbsp;Matching Esatto</td>
            <%}%>
            <%if(matchPesato) {%>
                <td align="right"><%=StringUtils.getAttributeStrNotNull(rep, "NROGREZZEPESATO")%></td>
                <td>da&nbsp;Matching Pesato</td>
            <%}%>
          </tr>
          </table>
        </td>
      </tr>
    <%}%>
<%}%>


<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2">
  <%if( flgIncrocio && matchAnteprima && eAnteprima && (!cdnStatoRich.equals("4") && !cdnStatoRich.equals("5"))) {%>
    <input type="button" onClick="match_sub(4);" class="pulsanti" value="Recupera Anteprima">
        &nbsp;&nbsp;
  <%}%>
  <%--if((matchAnteprima && eAnteprima) && gestRose){%>
		
  <%}--%>
<%
	if(!("S").equalsIgnoreCase(flgGraduatoria)){
  		if(gestRose) {
%>
	   		<input type="button" onClick="match_sub(5);" class="pulsanti" value="Gestione Rose">
<%
		}  
	}
%>
  
  	<%
	if(canGraduatoria) {      
		if(("S").equalsIgnoreCase(flgGraduatoria)){
			String parGraduatoria = "";
			
			//	INIT-PARTE-TEMP
			if (Sottosistema.CM.isOff()) {	
			// END-PARTE-TEMP
			
				parGraduatoria = "PAGE=ASGestGraduatoriePage&cdnFunzione="+_cdnFunzione+"&prgAzienda="+prgAzienda+"&prgRichiestaAZ="+prgRichiestaAz+"&prgUnita="+prgUnita+"&prgOrig="+prgRichiestaAz;			
			
			// INIT-PARTE-TEMP
			} else {
			// END-PARTE-TEMP
				
				if (("AS").equalsIgnoreCase(codEvasione)) {
					parGraduatoria = "PAGE=ASGestGraduatoriePage&cdnFunzione="+_cdnFunzione+"&prgAzienda="+prgAzienda+"&prgRichiestaAZ="+prgRichiestaAz+"&prgUnita="+prgUnita+"&prgOrig="+prgRichiestaAz;
				}
				else {
					parGraduatoria = "PAGE=CMGestGraduatoriePage&cdnFunzione="+_cdnFunzione+"&prgAzienda="+prgAzienda+"&prgRichiestaAZ="+prgRichiestaAz+"&prgUnita="+prgUnita+"&prgOrig="+prgRichiestaAz;
				}
			// INIT-PARTE-TEMP
			} 
			// END-PARTE-TEMP
								
	%>
			<input class="pulsante" type="button" name="PulsanteGraduatoria" value="Graduatorie"
            onclick="goTo('<%=parGraduatoria%>')" />   
	<%
		}
	}         
	%>
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2"><div class="sezione2">&nbsp;</div></td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<%if(!refLista.equals("")) {%>
<tr>
  <!--td colspan="2" align="center"><input class="pulsante" type="button" name="back" value="Torna alla lista" onclick="<%=backFun%>;" /></td-->
  <td colspan="2" align="center"><input class="pulsante" type="button" name="back" value="Torna alla lista" onclick="<%=refLista%>;" /></td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<%}%>
</table>
<%out.print(htmlStreamBottom);%>

</af:form>
<%}%>  
</body>
</html>
