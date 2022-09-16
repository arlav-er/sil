<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			it.gov.mlps.DataModels.InformationDelivery.VerificaCondizioniNEET._1_0.*,
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
	boolean canModify = false;

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

	boolean isFromWs = false;
	
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	BigDecimal prgVerificaNeet =null;
	String tipoVerificaNeet= null; 
	String strDataRiferimento =  null;
	String strCodiceFiscale= null;
	String esitoVerificaNeet = null;
	String flgScuola = null;
	String flgEta = null;
	String flgDisocc = null;
	String flgResidenza = null;
	String flgUni = null;
	String esitoMinistero= null;

	
	 
	//dati utente inserimento e modifica
	String codUtenteDataInserimento = null;
	String codUtenteDataModifica = null;
	
	VerificaCondizioniNEET_Output verificaNeetWS = null;
	SourceBean row = null;
	//
	if(serviceRequest.containsAttribute("OPERAZIONE_NEET")){
		isFromWs = true;
		titolo = "Esito Verifica Condizioni NEET";
	}else{
		titolo = "Dettaglio Storico Verifica Condizioni NEET";
		isFromWs= false;
	}
	
	if(serviceResponse.containsAttribute("M_GestioneNeetGG.NEET_GG_WS") && serviceResponse.getAttribute("M_GestioneNeetGG.NEET_GG_WS")!=null ){
		
		verificaNeetWS = (VerificaCondizioniNEET_Output) serviceResponse.getAttribute("M_GestioneNeetGG.NEET_GG_WS");
		
		strCodiceFiscale= verificaNeetWS.getCodiceFiscale();
		Date dataCalcolo = verificaNeetWS.getDataRiferimento().getTime();
		strDataRiferimento =  DateUtils.getSimpleDateFormatFixBugMem(it.eng.sil.utils.gg.Properties.FORMATO_DATA_CALCOLO).format(dataCalcolo);
		tipoVerificaNeet= (String) serviceResponse.getAttribute("M_GestioneNeetGG.TIPO_VERIFICA_NEET");
		esitoMinistero= verificaNeetWS.getEsito();
		
		CondizioneNEET[] condizioni = verificaNeetWS.getCondizioniNEET();
		for(int i =0; i < condizioni.length; i++){
			CondizioneNEET cond = condizioni[i];
			String decodifica = cond.getDecodifica();
			if(decodifica.equalsIgnoreCase(it.eng.sil.utils.gg.Properties.ETA)){
				flgEta = cond.isNEET()? "Sì" : "No";
			}
			if(decodifica.equalsIgnoreCase(it.eng.sil.utils.gg.Properties.RESIDENZA)){
				flgResidenza = cond.isNEET()? "Sì" : "No";
			}
			if(decodifica.equalsIgnoreCase(it.eng.sil.utils.gg.Properties.DISOCCUPAZIONE)){
				flgDisocc = cond.isNEET()? "Sì" : "No";
			}
			if(decodifica.equalsIgnoreCase(it.eng.sil.utils.gg.Properties.SCUOLA)){
				flgScuola = cond.isNEET()? "Sì" : "No";
			}
			if(decodifica.equalsIgnoreCase(it.eng.sil.utils.gg.Properties.UNIVERSITA)){
				flgUni = cond.isNEET()? "Sì" : "No";
			}
		
			esitoVerificaNeet= (String) serviceResponse.getAttribute("M_GestioneNeetGG.ESITO_VERIFICA_NEET");
	  }	
	}else if(serviceResponse.containsAttribute("M_DettaglioNeetGG")){
		
		row= (SourceBean) serviceResponse.getAttribute("M_DettaglioNeetGG.ROWS.ROW");
		if(row!=null){
			prgVerificaNeet = SourceBeanUtils.getAttrBigDecimal(row, "PRGVERIFICANEET", null);
			strCodiceFiscale= SourceBeanUtils.getAttrStrNotNull(row, "STRCODICEFISCALE");
			strDataRiferimento = SourceBeanUtils.getAttrStrNotNull(row, "strDatRif");
			tipoVerificaNeet= SourceBeanUtils.getAttrStrNotNull(row, "CODVERIFICANEET");
			esitoVerificaNeet= SourceBeanUtils.getAttrStrNotNull(row, "strFlgNeet");
			flgScuola = SourceBeanUtils.getAttrStrNotNull(row, "strFlgScuola");
			flgEta = SourceBeanUtils.getAttrStrNotNull(row, "strFlgEta");
			flgDisocc = SourceBeanUtils.getAttrStrNotNull(row, "strFlgDis");
			flgResidenza = SourceBeanUtils.getAttrStrNotNull(row, "strFlgRes");
			flgUni = SourceBeanUtils.getAttrStrNotNull(row, "strFlgUni");
			esitoMinistero= SourceBeanUtils.getAttrStrNotNull(row, "CODESITO");
			codUtenteDataInserimento =  SourceBeanUtils.getAttrStrNotNull(row, "utenteIns"); 
			codUtenteDataModifica =  SourceBeanUtils.getAttrStrNotNull(row, "utenteMod"); 
		}

	}		

	// Sola lettura: viene usato per tutti i campi di input
	String nomeClass =isFromWs? "maincoop": "main";
	
	// Stringhe con HTML per layout tabelle
  	
  	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  	
  	String htmlStreamTopCoop = StyleUtils.roundTopTable("prof_ro_coop");
  	String htmlStreamBottomCoop = StyleUtils.roundBottomTable("prof_ro_coop");
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
 
<script language="Javascript">

	/* Funzione per tornare alla pagina precedente */
  function tornaLista() {
	if (isInSubmit()) return;
	<%
	// Recupero l'eventuale URL generato dalla LISTA precedente
	String token = "_TOKEN_" + "AllNeetGGPage";
	String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());	
	%>
	setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
  }
	
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
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
	 		<af:showMessages prefix="M_DettaglioNeetGG"/>
			<af:showMessages prefix="M_GestioneNeetGG"/>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>

<af:form name="Frm1" action="AdapterHTTP">

<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
<af:textBox type="hidden" name="CDNUTMOD" value="<%= cdnUtCorrenteStr %>" />
 
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
