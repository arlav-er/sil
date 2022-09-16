<%@page import="it.eng.sil.module.pi3.Pi3Constants"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.bean.*,
                  it.eng.afExt.utils.*,
                  java.util.*, java.math.*,
                  java.text.DateFormat,
                  java.text.SimpleDateFormat,
                  com.engiweb.framework.security.*,
                  it.eng.sil.module.pi3.Pi3Constants.*,
                  it.eng.sil.module.movimenti.InfoLavoratore
                  " %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

	final String FORMATO_DATA     = "dd/MM/yyyy";
	SimpleDateFormat df = new SimpleDateFormat(FORMATO_DATA);

	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
	String prgDocumento = (String) serviceRequest.getAttribute("prgDocumento");
	String strChiaveTabella = (String) serviceRequest.getAttribute("strChiaveTabella");
	String numeroProtocollo = (String) serviceRequest.getAttribute("NUMPROT");
	String strDescrizioneDoc = (String) serviceRequest.getAttribute("nomeDoc");
	String docType = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.docType");
	String dataacqril = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.dataacqril");
	String oggetto = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.oggetto");
	String codicePAT = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.codicePAT");
	Documento documentoSil = (Documento) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.documentoSIL");
	String segnature = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.numeroProtocollo");
	String descrizioneInvioPi3 = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.descrizioneInvioPi3");
	Object lstDocAllegatiObj = (Object) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.lstDocAllegati");
	String descrizioneTipoProt = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.descrizioneTipoProt");
	String descrizioneTrattamento = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.DESCRIZIONETRATTAMENTO");
	String isPraticaAlreadyProcessed = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.isPraticaAlreadyProcessed");

	String _protocollazionePi3 = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.PROTOCOLLAZIONE_PI3");
	String _protocollazionePi3Error = (String) serviceResponse.getAttribute("M_GetInfoProtocollazioneStampeParamPi3.PROTOCOLLAZIONE_PI3_ERROR");
	
	//ProfileDataFilter filter = new ProfileDataFilter(user, _page); //per ogni pagina da profilare
	/** Per qualsiasi pagina da profilare appartenente alla 'sezione' Gestione Consenso */ 
	ProfileDataFilter filter = new ProfileDataFilter(user, "ListaStampeParLavPage");
	
	//PageAttribs attributi = new PageAttribs(user, _page); //per ogni pagina da profilare
	/** Per ogni attributo della pagina da profilare appartenente alla 'sezione' Gestione Consenso */
	PageAttribs attributi = new PageAttribs(user, "ListaStampeParLavPage");
	
	boolean preparaInvio = attributi.containsButton("INVIA_PROTOCOLLO_PI3");
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	if (!filter.canView()) {
		//response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
	}
	
	InfoLavoratore infoLav = new InfoLavoratore(new BigDecimal(cdnLavoratore));

	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	
	GregorianCalendar cal = new GregorianCalendar();
	String numeroPratica = cal.get(Calendar.YEAR) + "/" + numeroProtocollo;
	
	//SourceBean lavoratore = (SourceBean) serviceResponse.getAttribute("M_GetInfoLavAdesioneGG.ROWS.ROW");
	//String codfisclav = lavoratore.getAttribute("strcodicefiscale").toString();
	
	String strNomeDoc = "";
	

	boolean giaEsisteFileInDb = false;
	boolean blobVuoto = false;
	if (documentoSil != null){
		strNomeDoc = documentoSil.getStrNomeDoc();
	  	giaEsisteFileInDb = StringUtils.isFilled(strNomeDoc);
	  	blobVuoto = !documentoSil.getExistBlob();
	}
  	//BigDecimal prgDocumento = documentoSil.getPrgDocumento();
  	
  	String queryString = "aaa";
  	
  	String img0 = "../../img/chiuso.gif";    
    String img1 = "../../img/chiuso.gif";
    
%>

<%@ include file="_apriGestioneDoc.inc"%>

<html>
<head>
<title>Protocollazione Pi3</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/"/>
<script language="Javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js"></script>

<script language="Javascript">
</script>

</head>
<body class="gestione">
	
	<p>
	 	<font color="green" />
	  	<font color="red"><af:showErrors /></font>
	</p>

<p class="titolo">Dati da inviare al Protocollo PiTre</p>

	<af:form name="form1" action="AdapterHTTP" method="POST">
	<%out.print(htmlStreamTop);%>

	<%
		if(StringUtils.isEmpty(_protocollazionePi3) || _protocollazionePi3.equalsIgnoreCase("FALSE")){
	%>

		<table class="main" border="0">
		
		
				<%
					if (!StringUtils.isEmpty(descrizioneInvioPi3)){ %>
					
						<tr>
							<td class="etichetta" colspan="2" style="text-align: center;"><%=descrizioneInvioPi3%></td>
						</tr>
						<tr>
							<td class="etichetta" nowrap colspan="2">&nbsp;</td>
						</tr>
						<tr>
							<td class="etichetta" nowrap colspan="2">&nbsp;</td>
						</tr>
						
				<%
					}
				%>
				
				<tr>
					<td class="etichetta" nowrap>Numero Pratica SPIL&nbsp;</td>
					<td class="campo">
					<%
					if (!StringUtils.isEmpty(segnature)){
						%>
						<af:textBox classNameBase="input" type="text" name="numeroPratica" value="<%=segnature%>" readonly="true" title="numeroPratica" size="11" />
					<%
					}else{
					%>
						<af:textBox classNameBase="input" type="text" name="numeroPratica" value="<%=numeroPratica%>" readonly="true" title="numeroPratica" size="11" />
					<%} %>
					</td>
				</tr>
				<tr>
					<td class="etichetta" nowrap>Tipo protocollazione&nbsp;</td>
					<td class="campo">
						<b>
							<af:textBox classNameBase="input" type="text" name="trattamentoDocumento" value="<%=descrizioneTipoProt%>" readonly="true" title="trattamentoDocumento" size="25" />
						</b>
					</td>
				</tr>
				<%--
				<tr>
					<td class="etichetta" nowrap>Trattamento &nbsp;</td>
					<td class="campo">
						<b>
							<af:textBox classNameBase="input" type="text" name="trattamento" value="<%=descrizioneTrattamento%>" readonly="true" title="trattamento" size="15" /> 
						</b>
					</td>
				</tr>
				 --%>
				<tr>
					<td class="etichetta" nowrap>Data ricevimento documento&nbsp;</td>
					<td class="campo">
						<af:textBox classNameBase="input" type="text" name="dataRicevimentoDocumento" value="<%=dataacqril%>" readonly="true" title="dataRicevimentoDocumento" size="11" />
					</td>
				</tr>
				
				<tr>
					<td class="etichetta" nowrap>Mittente/destinatario&nbsp;</td>
					<td class="campo">
						<af:textBox classNameBase="input" type="text" name="mittenteDestinatario" value="<%=infoLav.getCodFisc()%>" readonly="true" title="mittenteDestinatario" size="18" />
					</td>
				</tr>
				
				<tr>
					<td class="etichetta" nowrap>Oggetto&nbsp;</td>
					<td class="campo">
						<af:textBox classNameBase="input" type="text" name="oggetto" value="<%=oggetto%>" readonly="true" title="oggetto" size="60" />
					</td>
				</tr>
				
				<tr>
					<td class="etichetta" nowrap>Codice di classificazione da titolario PAT&nbsp;</td>
					<td class="campo">
						<%
							if(codicePAT != null){ %>
								<af:textBox classNameBase="input" type="text" name="codicePAT" value="<%=codicePAT%>" readonly="true" title="codicePAT" size="11" />
							<%} else{ %>
								<af:comboBox addBlank="true" name="codicePAT" moduleName="M_GET_CODICI_FROM_TITOLARIO" selectedValue="<%=codicePAT%>" />
							<%
							}
						%>
					</td>
				</tr>
				
				
				<% if (giaEsisteFileInDb) { %>
			       <tr>
			         <td class="etichetta" nowrap>
			           Nome documento&nbsp;
			         </td>
			         <td class="campo" nowrap="nowrap">
			         
			         	<af:textBox classNameBase="input" type="text" name="nomeDocumento" value="<%=strNomeDoc%>" readonly="true" title="nomeDocumento" size="39" />
			         
						<%
						String myJsBlobVuoto = "javascript:alert('Nel database non e' stato trovato il file:" + strNomeDoc + "')";
						%>
						Apri
						<%
						String myJsA;
						if (blobVuoto) myJsA = myJsBlobVuoto;
						else           myJsA = "javascript:visualizzaDocumento('DOWNLOAD',''," + prgDocumento + ")";
						%>
						<a href="<%= myJsA %>" onclick="return canGoAway();"><img src="../../img/text.gif" border="0" alt="Apre il documento" /></a>
						&nbsp;&nbsp;Scarica
						<%
						String myJsS;
						if (blobVuoto) myJsS = myJsBlobVuoto;
						else           myJsS = "AdapterHTTP?ACTION_NAME=DOWNLOAD&PRGDOCUMENTO=" + prgDocumento + "&asAttachment=true";
						%>
						<a href="<%= myJsS %>"><img src="../../img/download.gif" border="0" alt="Salva il documento" /></a>
					</td>
					</tr>		
				<%}%>
				
				<tr><td colspan="2">&nbsp;</td></tr>
				<tr><td colspan="2">&nbsp;</td></tr>
				<tr><td colspan="2">&nbsp;</td></tr>
				
				
				<tr>
		         	<td class="etichetta" colspan="2">
		           	<div class='sezione2' id='UlteInfoAllegati'>
		              	 <img id='tendinaInfo' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("altreInfoSezAllegati"));'/>
		               	Allegati&nbsp;  
		           	</div>
		         	</td>
		       	</tr>
				
				<tr>
		         	<td colspan="2">
		           	<div id="altreInfoSezAllegati" style="display:inline">
					<table  width="100%" cellspacing="0" cellpadding="3" border="0">
						<tr>
						<td colspan="2">
						<af:list moduleName="M_GetDocAssociatiPi3" skipNavigationButton="1" />
						</td>
						</tr>
					</table>
					</div>
				</td>
				</tr>
				
				<%	
				if(preparaInvio) { %>
						
						<%
							if(StringUtils.isEmpty(isPraticaAlreadyProcessed) || isPraticaAlreadyProcessed.equalsIgnoreCase("FALSE")){
						%>
							<tr>
								<td colspan="2" align="center">
									<!-- <input type="button" name="inviaPi3" class="pulsanti" value="Invia Protocollazione" onClick="sendPi3()" /> -->
									<input type="submit" name="inviaPi3" class="pulsanti" value="Invia Protocollazione" />
								</td>
							</tr>
						<%} else { %>
							<tr>
								<td colspan="2" align="center"><input disabled="disabled" type="button" name="inviaPi3" class="pulsantiDisabled" value="Invia Protocollazione" onClick="sendPi3()" /></td>
							</tr>
						<% } %>
							
				<% } else { %>
					<tr>
						<td colspan="2" align="center"><input disabled="disabled" type="button" name="inviaPi3" class="pulsantiDisabled" value="Invia Protocollazione" onClick="sendPi3()" /></td>
					</tr>
				<% } %>
				
				
				
				
		</table>

	  	<input type="hidden" name="PAGE" value="Pi3StampeHomePage">
	  	<input type="hidden" name="pagina" value="Pi3StampeHomePage">
	  	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>">
	  	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
	  	<input type="hidden" name="NUMPROT" value="<%=numeroProtocollo%>">
	  	<input type="hidden" name="docType" value="<%=docType%>">
	  	<input type="hidden" name="DOCUMENTTYPE" value="<%=docType%>">
	  	<input type="hidden" name="actionPi3" value="W">
	  	<input type="hidden" name="nomeDoc" value="<%=strDescrizioneDoc%>">
	  	<input type="hidden" name="prgDocumento" value="<%=prgDocumento%>">
	  	<input type="hidden" name="strChiaveTabella" value="<%=strChiaveTabella%>">
	  	
	<%} else if (_protocollazionePi3.equalsIgnoreCase("TRUE")){ %>
	
		<br/><br/>
		<p class="titolo">Protocollazione Pi3 inviata con successo</p>
		
		<br/><br/>
		<input type="button" value="Chiudi" onclick="self.close()">
	
	<%} else if (_protocollazionePi3.equalsIgnoreCase("ERROR")){ %>
		
		<br/><br/>
		<p class="titolo">Protocollazione Pi3 non inviata.
		<br/>
		Motivo: <%=_protocollazionePi3Error%></p>
		
		<br/><br/>
		<input type="button" value="Chiudi" onclick="self.close()">
	
	<%} %>
	 
	<%out.print(htmlStreamBottom);%>
	</af:form>
  	
	 
</body>
</html>
