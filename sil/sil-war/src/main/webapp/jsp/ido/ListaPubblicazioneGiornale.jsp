<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,                 
                  com.engiweb.framework.message.*,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.afExt.utils.MessageCodes,                  
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.security.PageAttribs,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.error.EMFUserError" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
//
int _funzione = Integer.parseInt((String)serviceRequest.getAttribute("CDNFUNZIONE"));

//String sessionId = requestContainer.getAttribute("HTTP_SESSION_ID").toString();
String sessionId= request.getSession().getId();
String codiceUtente = sessionContainer.getAttribute("_CDUT_").toString();
String cdnLavoratore = (String)serviceRequest.getAttribute("cdnLavoratore");
String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
String prgElencoGiornale = (String)serviceRequest.getAttribute("PRGELENCOGIORNALE");
String codGiornale = (String)serviceRequest.getAttribute("CODGIORNALE");
String datInizioSett = (String)serviceRequest.getAttribute("DATINIZIOSETT");
String datFineSettimana = (String)serviceRequest.getAttribute("DATFINESETTIMANA");
PageAttribs attributi = new PageAttribs(user, "IdoPubbGiorReportPage");
String nomeGiornale = (String)serviceResponse.getAttribute("M_DECODIFICAGIORNALE.ROWS.ROW.STRDESCRIZIONE");
String retRicerca = (String) StringUtils.getAttributeStrNotNull(serviceRequest, "RETRICERCA");

//gestione degli eventuali errori in fase di inserimento di una nuova lista
Collection errLista = responseContainer.getErrorHandler().getErrors();
Iterator iter 		= errLista.iterator();
String msg	 		= "";
int error			= 0;
while (iter.hasNext()){
	Object objerror = iter.next();
	if (objerror instanceof EMFUserError) {
		EMFUserError errorCode =(EMFUserError)objerror;		
		if (errorCode.getCode() == MessageCodes.General.ELEMENT_DUPLICATED) 
			msg = "Lista già presente, non è possibile eseguire l'inserimento";
			error =	errorCode.getCode();	
		break;
	}
}
if ("INSERISCI".equals(serviceRequest.getAttribute("module")) && (error != 10011)) {
	codGiornale = (String)serviceResponse.getAttribute("M_GetGiorPubb.rows.row.codGiornale");
	datInizioSett = (String)serviceResponse.getAttribute("M_GetGiorPubb.rows.row.datInizioSett");
	datFineSettimana = (String)serviceResponse.getAttribute("M_GetGiorPubb.rows.row.datFineSettimana");
	prgElencoGiornale = serviceResponse.getAttribute("M_GetGiorPubb.rows.row.prgElencoGiornale").toString();
} else {

	String strListPage = "1";
	String strMessage = "LIST_PAGE";
	String strDatInizioSett = "";
	String strDatFineSettimana = "";
	String strCodGiornale = "";
	String tok = "_TOKEN_" + "IDOLISTAPUBBGIORPAGE";
	String url = (String)sessionContainer.getAttribute(tok.toUpperCase());
	int i = 0;
	int j = 0;

	if ((url!=null) && !(url.equals(""))) {
		i = url.indexOf("LIST_PAGE=") + "LIST_PAGE=".length();
		j = url.indexOf("&", i);
		if (j == -1) {
			j = url.length();
		}
		strListPage = url.substring(i, j);
	
		i = url.indexOf("MESSAGE=") + "MESSAGE=".length();
		;
		j = url.indexOf("&", i);
		if (j == -1) {
			j = url.length();
		}
		strMessage = url.substring(i, j);
	
		i = url.indexOf("DATINIZIOSETT=") + "DATINIZIOSETT=".length();
		;
		j = url.indexOf("&", i);
		strDatInizioSett = url.substring(i, j);
	
		i = url.indexOf("DATFINESETTIMANA=") + "DATFINESETTIMANA=".length();
		;
		j = url.indexOf("&", i);
		strDatFineSettimana = url.substring(i, j);
	
		i = url.indexOf("CODGIORNALE=") + "CODGIORNALE=".length();
		;
		j = url.indexOf("&", i);
		strCodGiornale = url.substring(i, j);
	
		i = url.indexOf("PRGELENCOGIORNALE=") + "PRGELENCOGIORNALE=".length();
		j = url.indexOf("&", i);
		// String prgElencoGiornale = url.substring(i,j);
	}
}
boolean canModify = attributi.containsButton("inserisci");
boolean canDelete = attributi.containsButton("rimuovi");

//formattazione pagina jsp
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

cdnLavoratore = "1";
String queryString = null;

int listIns;
int listDel;
if (canModify) {
	listIns = 1;
} else {
	listIns = 0;
}
if (canDelete) {
	listDel = 1;
} else {
	listDel = 0;
}

listIns = 1;
listDel = 1;

queryString =
	"PRGELENCOGIORNALE=" + prgElencoGiornale
		+ "&CODGIORNALE=" + codGiornale
		+ "&DATINIZIOSETT=" + datInizioSett
		+ "&DATFINESETTIMANA=" + datFineSettimana
		+ "&CDNFUNZIONE=" + cdnFunzione
		+ "&PAGE=IDOLISTAPUBBGIORLISTAPAGE"
		+ "&RETRICERCA=" + retRicerca;
%>



<html>
<head>
<title>RicercaAssociaPubblicazione.jsp</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<SCRIPT language="Javascript" type="text/javascript">
  function apriPagina(cdnFunzione,codGiornale,datInizioSett,datFineSettimana) {
    /*var urlpage="";
    urlpage+="../servlet/AdapterHTTP?";
    urlpage+="CDNFUNZIONE=13&";
    urlpage+="PAGE=IdoAssociaPubbPage&";
    urlpage+="CODGIORNALE=" + codGiornale + "&";
    urlpage+="DATINIZIOSETT=" + datInizioSett + "&";
    urlpage+="DATFINESETTIMANA=" + datFineSettimana;    
    window.open(urlpage,"Associazioni", 'toolbar=0, scrollbars=1, resizable=1'); */

	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    doFormSubmit(document.Frm1);
  }

  function impostaNomeReport(nome){
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      document.form1.REPORT.value = nome;
      doFormSubmit(document.form1);
    
  }

function goBackLista() {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

	<%String token = "_TOKEN_" + "IdoListaPubbGiorPage";
String urlDiLista = (String)sessionContainer.getAttribute(token.toUpperCase());
if (urlDiLista != null) {%>
		setWindowLocation("AdapterHTTP?<%=urlDiLista%>");
		<%}%>
}		

  function apriStampa(RPT,paramPerReport,tipoDoc)
  { 
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
    
    //RPT: attributo name del tag ACTION nel file action.xml relativo al report da visualizzare
    //paramPerReport: parametri necessari a visualizzare il report 
    //tipoDoc: codice relativo al tipo di documento cosi come inserito nel campo CODTIPODOCUMENTO della tab. DE_DOC_TIPO
    
    var urlpage="AdapterHTTP?ACTION_NAME="+RPT;
    urlpage+=paramPerReport; //Quelli che nella calsse sono inseriti nel vettore params
    urlpage+="&tipoDoc="+tipoDoc;

    if(confirm("Vuoi PROTOCOLARE il file pirma di visualizzarlo?\n\nSeleziona\n- \"OK\"        per PROTOCOLLARE prima di visualizzare la stampa\n- \"Annulla\"  per visualizzare SENZA PROTOCOLLARE"))
    { urlpage+="&salvaDB=true";
    }
    else
    { urlpage+="&salvaDB=false";
    }
    
    setWindowLocation(urlpage);
    
  }//apriStampa()

  function stampa(codGiornale,datInizioSett,datFineSettimana){
    apriGestioneDoc('RPT_PUBB_GIOR','&cdnLavoratore=<%=cdnLavoratore%>&CODGIORNALE=' + codGiornale + '&DATINIZIOSETT=' + datInizioSett + '&DATFINESETTIMANA=' + datFineSettimana,'PUB_G');  	
  }
  function aggiungiPubblicazione() {
  	if (isInSubmit()) return;
  	document.Frm1.PAGE.value ="IdoRicercaAssociaPubbPage";
  	document.Frm1.ASSOCIA.value="N";
  	if (controllaFunzTL()) 
  		doFormSubmit(document.Frm1);
  }
  
  function goBack() {
	setWindowLocation("AdapterHTTP?PAGE=IdoPubbGiorReportPage&CDNFUNZIONE=<%=cdnFunzione%>");
  }		
  </SCRIPT>

<script language="Javascript">
     <%
//Genera il Javascript che si occuperà di inserire i links nel footer
attributi.showHyperLinks(out, requestContainer, responseContainer, "");%>
</script>
</head>

<body class="gestione" onload="rinfresca();">
<!-- gestione degli errori nella risposta dei moduli -->
<%if (error == 10011){%>
<p>&nbsp;</p>
<%out.print(htmlStreamTop);%>
<table class="main" align="center">
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td><b><%=msg%></b>
		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td align="center" colspan="1" >
		<%	String urlLista = (String) sessionContainer.getAttribute("_TOKEN_IDOLISTAPUBBGIORPAGE");
			if (urlLista != null) {
			out.println("<div align=\"center\"><a href=\"#\" onClick=\"goTo('" + urlLista + "')\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></div>");
			}
		%>
		</td>
	</tr>
</table>
<%out.print(htmlStreamBottom);%>
<%} else {%>
<af:form name="Frm1" method="POST" action="AdapterHTTP">	
	<input type="hidden" name="PAGE" value="IdoAssociaPubbPage" />
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
	<input type="hidden" name="PRGELENCOGIORNALE"
		value="<%=prgElencoGiornale%>" />
	<input type="hidden" name="CODGIORNALE" value="<%=codGiornale%>" />
	<input type="hidden" name="DATINIZIOSETT" value="<%=datInizioSett%>" />
	<input type="hidden" name="DATFINESETTIMANA"
		value="<%=datFineSettimana%>" />
	<%-- andrea 12/12/04 --%>
	<input type="hidden" name="ASSOCIA" value="N" />
	<input type="hidden" name="RETRICERCA" value="<%=retRicerca%>" />

	<table width="100%">
		<tr>
			<td align=right><!--<a  href="#" onclick="apriPagina('<%=_funzione%>','<%=codGiornale%>','<%=datInizioSett%>','<%=datFineSettimana%>')">
        <img src="../../img/binocolo.gif"></a>&nbsp;(associa pubblicazioni alla lista)-->
			<center><b>Pubblicazioni del <%=nomeGiornale%> per il periodo dal <%=datInizioSett%>
			al <%=datFineSettimana%></b></center>
			</td>
		</tr>
		<tr>
			<td align=right>
			<center><font color="red"> <af:showErrors /> </font></center>
			</td>
		</tr>
	</table>
</af:form>
<af:list moduleName="M_DYNRICERCALISTAGIORPUBB"
	canInsert="<%=String.valueOf(listIns)%>"
	canDelete="<%=String.valueOf(listDel)%>" />

<table width="100%">
	<tr>
		<td colspan="2">
		<center><input class="pulsante" type="button" name="btnAggiungi"
			value="Aggiungi pubblicazioni" onClick="aggiungiPubblicazione()" />
		<input class="pulsante" type="button"
			onClick="stampa('<%=codGiornale%>','<%=datInizioSett%>','<%=datFineSettimana%>')"
			name="btnReport" value="Stampa" /></center>
		</td>
	</tr>
	<tr>
	  <td align="center">
	  	<%if ("".equals(retRicerca)) {
	  		out.print(InfCorrentiAzienda.formatBackList(sessionContainer, "IdoListaPubbGiorPage"));
	  	  } else {%>
	  	  	<input class="pulsante" type="button" name="btnIndietro" value="Torna alla pagina di ricerca" onClick="goBack();"/>
	  	<%}%>
      </td>		
	</tr>
</table>
<%}%>

</body>
</html>


