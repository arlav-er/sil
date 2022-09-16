<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,java.lang.*,java.text.*,java.util.*"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  com.engiweb.framework.base.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
                  
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
    String exDati = "";
    String _page = "";
    // NOTE: Attributi della pagina (pulsanti e link) 
    PageAttribs attributi = new PageAttribs(user, "MovimentiRicercaPage");
    boolean canInsert = attributi.containsButton("INSERISCI");
    boolean canDelete = attributi.containsButton("CANCELLA");
    boolean canConsultaCO = false;

    
    String canConsultArchivioLav = "false";
	Object canConsultArchivio = serviceResponse.getAttribute("M_CheckConsultArchivioMov.ArchivioConsultabile");
	if (canConsultArchivio != null) {
		canConsultArchivioLav = (String) canConsultArchivio;
	}
    
    String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
    String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
    String strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(serviceRequest, "codiceFiscaleLavoratore");
    String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
    String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");

    //Per identificare se si proviene da un menu contestuale o meno
    String buttonImportaMov = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVENIENZA");
    String pageDiRicercaDett = StringUtils.getAttributeStrNotNull(serviceRequest,"RICERCA_DA");    

	String token = "";
	String urlDiLista = "";
	String urlDiListaBack = "";
    
    if (buttonImportaMov.equalsIgnoreCase("lavoratore")){
      canConsultaCO = attributi.containsButton("CONSULTA_CO");
      exDati = "&CDNLAVORATORE=" + cdnLavoratore;
      _page = "MovimentiRicercaLavPage";
    } else 
          if (buttonImportaMov.equalsIgnoreCase("azienda")){
            exDati = "&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=" + prgUnita;
            _page = "MovimentiRicercaAziendaPage";
          } else{
              exDati = "&PROVENIENZA=ListaMov";
              _page = "MovimentiRicercaPage";
            }
    if (!pageDiRicercaDett.equals("")) {
    	_page = pageDiRicercaDett;	
    }
%> 

<html>
<head>
  <title>Lista Movimenti</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
	<%-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) --%>
	<% String queryString = "PAGE=" + _page + "&cdnFunzione=" + _funzione + exDati; %>
	<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<script language="Javascript">
  <%
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer, responseContainer,"");
       String parametriRicerca = null;
       String backUrl = null;
  %>
  <!--
  function aggiungiCollegato(page, prgMovPrec, prgMovSucc, cdnFunzione, provenienza, currentcontext, action, collegato, tipomov,codTipoCess, pageRitLista, codStatoAtto, codMonoTipoAss, cdnLavoratore, codTipoAss) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
    if (tipomov == 'C' && codTipoCess != 'SC') {
      alert("Impossibile inserire un movimento collegato ad una cessazione!");
    } else 
        if (prgMovSucc != null && prgMovSucc != '') {
          alert("Il movimento ha già un successivo! Impossibile inserire un movimento prima del successivo.\n Funzionalità non ancora implementata.");
        } else 
            if(codStatoAtto != 'PR'){
              alert("Impossibile inserire un movimento collegato ad un movimento annullato!");
            }else {
                //Devo fare la richiesta dell page manualmente con i parametri passati
                var url = 'AdapterHTTP?' + 
                                  'PAGE=' + page + "&" +
                                  'PRGMOVIMENTOPREC=' + prgMovPrec + "&" +
                                  'cdnFunzione=' + cdnFunzione + "&" +
                                  'PROVENIENZA=' + provenienza + "&" +
                                  'COLLEGATO=' + collegato + "&" +
                                  'CURRENTCONTEXT=' + currentcontext + "&" +
                                  'PageRitornoLista=' + pageRitLista + "&" +
                                  'CODMONOTIPOASS=' + codMonoTipoAss +'&' +  
                                  'ACTION=' + action + '&' + 
                                  'CDNLAVORATORE=' + cdnLavoratore + '&' +
                                  'CODTIPOMOVPREC=' + tipomov + '&' + 
                                  'CODTIPOASS=' + codTipoAss + '&' +
                                  'CODMVCESSAZIONEPREC=' + codTipoCess;
                setWindowLocation(url);
              }
  } 


	function vaiVia() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		var url = "AdapterHTTP?PAGE=<%=_page%>&cdnFunzione=<%=_funzione%><%=exDati%>";
		setWindowLocation(url);
	} 

	function stampa(params) {
		// passo il contenuto del token alla page in modo da poter rifare la ricerca con gli stessi params
		HTTPrequest = '<%=getQueryString("PAGE=" + _page + "&cdnFunzione=" + _funzione + exDati)%>';
		apriGestioneDoc('RPT_MOVIMENTI','&TIPOSTAMPA=LISTA&'+ parametriRicerca,'STMOV');
	} 

    function stampaMovimento(prgMovimento){
        HTTPrequest = backUrl;
		apriGestioneDoc('RPT_MOVIMENTI','&TIPOSTAMPA=SINGOLO&prgMovimento='+ prgMovimento,'STMOV')
    }

	function consultaArchivioLav(strCodFisc,page,cdnfunzione) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		var urlArchivio =  "AdapterHTTP?PAGE=" + page + "&CDNFUNZIONE=" + cdnfunzione + "&codiceFiscaleLavoratore=" + strCodFisc + 
							"&CONTEXT=validaArchivio&MESSAGE=LIST_FIRST&LIST_PAGE=1";
		
		// Apre la finestra
		var w=(screen.availWidth)*0.83;  var l=(screen.availHeight)*0.1;
		var h=(screen.availHeight)*0.73;  var t=(screen.availHeight)*0.1;
		var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES," +
		  			 "height="+h+",width="+w+",top="+t+",left="+l;
		var opened = window.open(urlArchivio, "_blank", feat);
		opened.focus();
	}
	
	
	function goConsultaCO(cfLav){
  		var urlpage="AdapterHTTP?";
  		urlpage +="CDNLAVORATORE=<%=cdnLavoratore%>";
  		urlpage +="&CFLAVORATORE="+cfLav;
  		urlpage+="&CDNFUNZIONE=<%=_funzione%>";
  		urlpage+="&PAGE=ConsultaCOPage";
  		var opened = window.open(urlpage,'Consulta CO','toolbar=NO,statusbar=YES,width=900,height=500,top=50,left=100,scrollbars=YES,resizable=YES');
  		opened.focus();
 	}
	
	
  -->
</script>


  
</head>

<body onload="checkError();rinfresca();" class="gestione">
<af:error/>
<p align="center">

  <br/>
  <center>
    
    <af:form dontValidate="true">
	<af:JSButtonList moduleName="M_MovimentiGetMovimenti" configProviderClass="it.eng.sil.module.movimenti.ButtonsListaMovConfig" canDelete="<%= canDelete ? \"1\" : \"0\" %>" canInsert="<%= canInsert ? \"1\" : \"0\" %>" jsCaptions="stampaMovimento;aggiungiCollegato"/>    
	<SCRIPT>
	<%if (sessionContainer!=null){
	  token = "_TOKEN_" + "MOVIMENTIRISULTRICERCAPAGE";
	  urlDiLista = (String)sessionContainer.getAttribute(token.toUpperCase());
	  urlDiListaBack = (String)sessionContainer.getAttribute(token.toUpperCase());
      int pos1 = urlDiLista.indexOf("PAGE");
      int pos2 = urlDiLista.indexOf("&",pos1);
      String str1 = urlDiLista.substring (0,pos1);
      String str2 = urlDiLista.substring (pos2+1);
      urlDiLista = str1.concat(str2);
	}%>
	backUrl = "<%=getQueryString(urlDiListaBack)%>";
    parametriRicerca = "<%=urlDiLista%>";
	</SCRIPT>
	
    <input class="pulsante" type="button" name="ritornaRicerca" value="Ritorna alla pagina di ricerca"
    		onclick="vaiVia()" />
    <input class="pulsante" type="button" name="stampaRicerca" value="Stampa lista movimenti"
    		onclick="stampa()" />
    <% if (_page.equals("MovimentiRicercaLavPage") && canConsultArchivioLav.equalsIgnoreCase("true")){ %>
    <input class="pulsante" type="button" name="consultaArchivio" value="Consulta Archivio"
    		onclick="consultaArchivioLav('<%=strCodiceFiscaleLav%>','MovListaValidazionePage','<%=_funzione%>')" />    
    <%} %>
    <% if (canConsultaCO){ %>
    <input class="pulsante" type="button" name="consultaCO" value="Consulta CO" onclick="goConsultaCO('<%=strCodiceFiscaleLav%>')" 		 />    
    <%} %>
    </af:form>
  </center>
  <br/>
  <table width="100%">
    <tr><td colspan=3 style="campo2">N.B.: "&lt;--" Indica che non esiste un movimento precedente collegato per una trasformazione o proroga o cessazione.</td></tr>
    <tr><td colspan=3 style="campo2">N.B.: "R." Indica il reddito mensile inserito in fase di registrazione o validazione del movimento.</td></tr>
    <tr><td colspan=3 style="campo2">N.B.: "S." Indica il reddito sanato (se non compare significa che il movimento non &egrave; stato coinvolto in nessuna dichiarazione di reddito).</td></tr>
  </table>
  
  </body>
</html>


