<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_3.*,
			it.eng.sil.module.profiling.gg.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String  titolo = "";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	// Lettura parametri dalla REQUEST
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	

	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);
	boolean canPrintStoricoDettaglio = false;  

	boolean canModify = false;
	boolean isFromWs = false;
	
	String  tipoOperazione  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"OPERAZIONE_GG"); 
	if(StringUtils.isFilledNoBlank(tipoOperazione) && tipoOperazione.equalsIgnoreCase(it.eng.sil.utils.gg.Properties.OPERAZIONE_CALCOLO)){
		titolo="Esito Calcolo Profiling GG";
	}else if(StringUtils.isFilledNoBlank(tipoOperazione) && tipoOperazione.equalsIgnoreCase(it.eng.sil.utils.gg.Properties.OPERAZIONE_VERIFICA)){
		titolo="Verifica Profiling GG Ministeriale";
	}
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	BigDecimal prgProfilingGG =null;
	String descrTipoProfiling= null; 
	String strCodiceFiscale= null;
	String codiceProvincia = null;
	String strDataCalcolo = null;
	String codiceDurataPrIt= null;
	String codiceCondOccupaz= null;
	String codTitoloStudio = null;
	String descrTitolo=null;
	String strSesso= null;
	String strEta= null;
	String strIndice2= null;
	String descrIndiceProf2= null;
	
	//dati utente inserimento e modifica
	String codUtenteDataInserimento = null;
	String codUtenteDataModifica = null;
	
	YG_Profiling profilingWS = null;
 
	if(serviceResponse.containsAttribute("M_GestioneProfilingGG")){
		isFromWs = true;
	   	if(serviceResponse.containsAttribute("M_GestioneProfilingGG.PROFILING_GG_WS") && serviceResponse.getAttribute("M_GestioneProfilingGG.PROFILING_GG_WS")!=null){
	   		profilingWS = (YG_Profiling) serviceResponse.getAttribute("M_GestioneProfilingGG.PROFILING_GG_WS");
			strCodiceFiscale= profilingWS.getCf();
			codiceProvincia = profilingWS.getCodProv();
			Date dataCalcolo = profilingWS.getDataInserimento().getTime();
			strDataCalcolo =  DateUtils.getSimpleDateFormatFixBugMem(it.eng.sil.utils.gg.Properties.FORMATO_DATA_CALCOLO).format(dataCalcolo); 
			codiceDurataPrIt= profilingWS.getPresenzaIt();
			codiceCondOccupaz= profilingWS.getOccupazAp();
			codTitoloStudio = profilingWS.getTitoloStudio();
			strSesso= profilingWS.getCodGenere().getValue();
			strEta= profilingWS.getEta().toString();
			strIndice2= profilingWS.getIndice2().toString();
			descrIndiceProf2= profilingWS.getDesIndice2();
			if(serviceResponse.containsAttribute("M_GestioneProfilingGG.PROFILING_GG_TITOLO_STUDIO") && serviceResponse.getAttribute("M_GestioneProfilingGG.PROFILING_GG_TITOLO_STUDIO")!=null){
				descrTitolo = (String) serviceResponse.getAttribute("M_GestioneProfilingGG.PROFILING_GG_TITOLO_STUDIO");
			}
	   	}
	}
	
	SourceBean row = null;
	if(serviceResponse.containsAttribute("M_DettaglioProfiloGG")){
		titolo = "Dettaglio Storico Profiling GG";
		isFromWs= false;
		row= (SourceBean) serviceResponse.getAttribute("M_DettaglioProfiloGG.ROWS.ROW");
		canPrintStoricoDettaglio =  attributi.containsButton("ST_PROF_GG");
	}		
	if(row!=null){
		prgProfilingGG = SourceBeanUtils.getAttrBigDecimal(row, "PRGYGPROFILING", null);
		descrTipoProfiling= SourceBeanUtils.getAttrStrNotNull(row, "strTipoProfiling"); 
		strCodiceFiscale= SourceBeanUtils.getAttrStrNotNull(row, "STRCODICEFISCALE");
		codiceProvincia = SourceBeanUtils.getAttrStrNotNull(row, "CODPROVINCIA");
		strDataCalcolo = SourceBeanUtils.getAttrStrNotNull(row, "dataCalcolo");
		codiceDurataPrIt= SourceBeanUtils.getAttrStrNotNull(row, "CODPFPRESENZAIT");
		codiceCondOccupaz= SourceBeanUtils.getAttrStrNotNull(row, "CODPFCONDOCCUP");
		codTitoloStudio = SourceBeanUtils.getAttrStrNotNull(row, "CODTITOLO");
		descrTitolo = StringUtils.getAttributeStrNotNull(row,"descrTitolo");
		strSesso= SourceBeanUtils.getAttrStrNotNull(row, "STRSESSO");
		strEta= SourceBeanUtils.getAttrStrNotNull(row, "strNumEta");
		strIndice2= SourceBeanUtils.getAttrStrNotNull(row, "strIndice2");
		descrIndiceProf2= SourceBeanUtils.getAttrStrNotNull(row, "STRDESCINDICE2");
		codUtenteDataInserimento =  SourceBeanUtils.getAttrStrNotNull(row, "utenteIns"); 
		codUtenteDataModifica =  SourceBeanUtils.getAttrStrNotNull(row, "utenteMod"); 
	}

	// Sola lettura: viene usato per tutti i campi di input
	String readonly = String.valueOf( ! canModify );
	String required = String.valueOf( canModify );
	String readonlyAlways = String.valueOf(true);
	String nomeClass =isFromWs? "maincoop": "main";
	
	// Stringhe con HTML per layout tabelle
  	
  	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  	
  	String htmlStreamTopCoop = StyleUtils.roundTopTable("prof_ro_coop");
  	String htmlStreamBottomCoop = StyleUtils.roundBottomTable("prof_ro_coop");
  	String queryString = null;

%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>

<af:linkScript path="../../js/"/>
<%@ include file="../global/fieldChanged.inc" %>

<%-- INCLUDERE QUI ALTRI JAVASRIPT CON CLAUSOLE DEL TIPO:
<script language="Javascript" src="../../js/xxx.js"></script>
--%>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<script language="Javascript">

	/* Funzione per tornare alla pagina precedente */
  function tornaLista() {
	if (isInSubmit()) return;
	<%
	// Recupero l'eventuale URL generato dalla LISTA precedente
	String token = "_TOKEN_" + "AllProfilingGGPage";
	String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());	
	%>
	setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
  }
	
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}
	
	 function clearTitolo() {
		
		}
		
		function ricercaAvanzataTitoliStudio() {
		}
		
		function selectTitolo_onClick(codTitolo, codTitoloHid, strTitolo, strTipoTitolo) {
		}
		
		function Stampa(){
 
			apriGestioneDoc('RPT_STAMPA_PROFILING_GG','&PRGYGPROFILING=<%=prgProfilingGG%>&cdnLavoratore=<%=cdnLavoratore%>','PFGG');
		}
	     
		

<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
%>

</script>
</head>

<body class="gestione" onload="onLoad()">

<br/>
<p class="titolo"><%= titolo %></p>

	<p>
	 	<font color="green">
	 		<af:showMessages prefix="M_DettaglioProfiloGG"/>
			<af:showMessages prefix="M_GestioneProfilingGG"/>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>

<af:form name="Frm1" action="AdapterHTTP">

<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
<af:textBox type="hidden" name="CDNUTMOD" value="<%= cdnUtCorrenteStr %>" />
<input type="hidden" name="codTitoloHid"  />
<input type="hidden" name="strTipoTitolo"  />
<%if(!isFromWs){ %>
	<%out.print(htmlStreamTop);%>
	<table class="main">
<%}else{%>
	<%out.print(htmlStreamTopCoop);%>
	<table class="maincoop">
<%}%>
	<%--
	Tutti i campi di input sono nel file INC poiché sono
	condivisi dalle pagine di dettaglio e di nuovo.
	--%>
	<%-- ***************************************************************************** --%>
	<%@ include file="dettaglioCampi.inc" %> 
	<%-- ***************************************************************************** --%>
 		<%if(canPrintStoricoDettaglio){ %>
 			<tr>
				<td colspan="2" align="center">
					<input type="button" name="stampa" class="pulsanti" value="Stampa" onClick="Stampa();"/>
				</td>
			</tr>
		<%}%>
 		<%if(!isFromWs){ %>
 			<tr>
				<td colspan="2" align="center">
					<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onClick="tornaLista();"/>
				</td>
			</tr>
		<%}%>
		<tr>
			<td colspan="2" align="center">
				<input type="button" onClick="window.close();" class="pulsanti" value="Chiudi" >
			</td>
		</tr>
</table>
<%if(!isFromWs){ %>
	 <%out.print(htmlStreamBottom);%>
<%}else{%>
	<%out.print(htmlStreamBottomCoop);%>
<%}%>
		
		
<%if(!isFromWs){ %>
<center>
  	<table>
  		<tbody>
  			<tr>
	  			<td align="center">
					<table class="info_mod" align="center">
						<tr>
							<td class="info_mod">Inserimento</td>
							<td class="info_mod"><b><%= codUtenteDataInserimento %></b></td>
							<%if(StringUtils.isFilledNoBlank(codUtenteDataModifica)){ %>
							<td class="info_mod">Ultima Modifica</td>
							<td class="info_mod"><b><%= codUtenteDataModifica %></b></td>
							<%}%>
						</tr>
					</table>
				</td>
			</tr>
		</tbody>
	</table>
</center>
<%}%>

</af:form>

</body>
</html>
