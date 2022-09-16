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
                  it.eng.afExt.utils.*,
                  java.util.*, java.math.*,
                  it.eng.sil.bean.*,
                  java.text.DateFormat,
                  java.text.SimpleDateFormat,
                  com.engiweb.framework.security.*,
                  it.eng.sil.module.pi3.Pi3Constants.*,
                  it.eng.sil.module.movimenti.InfoLavoratore
                  " %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
SourceBean activeModule = (SourceBean) serviceResponse.getAttribute("DettagliDocumento");
String queryString = SourceBeanUtils.getAttrStrNotNull(activeModule, "QUERY_STRING");
	final String FORMATO_DATA     = "dd/MM/yyyy";
	SimpleDateFormat df = new SimpleDateFormat(FORMATO_DATA);

	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
	String prgDocumento = (String) serviceRequest.getAttribute("prgDocumento");
	String nomeDocumento = (String) serviceRequest.getAttribute("nomeDocumento");
	String strChiaveTabella = (String) serviceRequest.getAttribute("strChiaveTabella");
	String numeroProtocollo = (String) serviceRequest.getAttribute("NUMPROT");
	String consenso = (String) serviceRequest.getAttribute("CONSENSO");
	String docType = (String) serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.docType");
	String descrizioneTipoProt = (String) serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.descrizioneTipoProt");
	Documento documentoSil = (Documento) serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.documentoSIL");
	String dataacqril = (String) serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.dataacqril");
	String oggetto = (String) serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.oggetto");
	String codicePAT = (String) serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.codicePAT");
	String _protocollazionePi3 = (String) serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.PROTOCOLLAZIONE_PI3");
	String _protocollazionePi3Error = (String) serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.PROTOCOLLAZIONE_PI3_ERROR");
	
	String isPraticaAlreadyProcessed = (String) serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.isPraticaAlreadyProcessed");
	String numeroProtocollazione = (String)  serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.numeroProtocollo");
	String dataProtocollo = (String)  serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.dataProtocollo");
	String dataInvio = (String)  serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.dataInvio");
	String statoDocInviato = (String)  serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.statoDocInviato");
	
	//ProfileDataFilter filter = new ProfileDataFilter(user, _page); //per ogni pagina da profilare
	/** Per qualsiasi pagina da profilare appartenente alla 'sezione' Gestione Consenso */ 
	ProfileDataFilter filter = new ProfileDataFilter(user, "Pi3HomePage");
	
	String codiceTrattamento = (String) serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.CODTIPOTRATTAMENTO");
	String descrizioneTrattamento = (String) serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.DESCRIZIONETRATTAMENTO");
	boolean docFirmato = false;
	if(serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.IS_DOC_FIRMATO")!=null){
		docFirmato = ((Boolean)serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.IS_DOC_FIRMATO")).booleanValue();
	}
		
	boolean docFirmabile = false;
	if(serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.IS_DOC_FIRMABILE")!=null){
		docFirmabile =  ((Boolean)serviceResponse.getAttribute("M_GETINFOPROTOCOLLAZIONEPATTOPI3.IS_DOC_FIRMABILE")).booleanValue();
	}
	
	boolean isConsensoAttivo = false;
	if(consenso!=null)
		isConsensoAttivo = Boolean.parseBoolean(consenso);
		
	
	String message = "";
	String messageTest = "docType: " + docType ;
	messageTest = messageTest + " codiceTrattamento: " + codiceTrattamento ;
	messageTest = messageTest + "\n docFirmabile: " + docFirmabile ;
	messageTest = messageTest + "\n docFirmato: " + docFirmato ;
	messageTest = messageTest + "\n isConsensoAttivo: " + isConsensoAttivo ;
	
	
	if(docType!=null&&docType.equals("G")&&codiceTrattamento!=null&&!codiceTrattamento.equals("P") && docFirmabile &&isConsensoAttivo)
		message = "Si e' verificato un problema con la firma grafometrica, la stampa andra' sottoscritta con firma autografa. Saranno inviati in automatico a PiTre solo i dati principali della pratica";
	
	if(docType!=null&&docType.equals("G")&&codiceTrattamento!=null&&!codiceTrattamento.equals("P") && !docFirmabile )
		message = "La stampa andra' sottoscritta con firma autografa. Saranno inviati in automatico a PiTre solo i dati principali della pratica";
	
	if(docType!=null&&docType.equals("G")&&codiceTrattamento!=null&&!codiceTrattamento.equals("P") &&isConsensoAttivo && docFirmabile  && docFirmato )
		message = "la stampa e' stata sottoscritta con firma grafometrica e sara' inviata in automatico a PiTre insieme ai dati principali della pratica";
	
	if(docType!=null&&docType.equals("G")&&codiceTrattamento!=null&&!codiceTrattamento.equals("P") && docFirmabile &&!isConsensoAttivo)
		message = "Il consenso all'uso della firma per il lavoratore e' " + serviceRequest.getAttribute("STATOCONSENSO") + ", pertanto la stampa andra' sottoscritta con firma autografa. " +
		"Saranno inviati in automatico a PiTre solo i dati principali della pratica";

	if (isPraticaAlreadyProcessed.equalsIgnoreCase("TRUE")){
		message = "La pratica e' stata inviata al Sistema PiTre";
	}
	
	//PageAttribs attributi = new PageAttribs(user, _page); //per ogni pagina da profilare
	/** Per ogni attributo della pagina da profilare appartenente alla 'sezione' Gestione Consenso */
	PageAttribs attributi = new PageAttribs(user, "Pi3HomePage");
	
	
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	if (!filter.canView()) {
		//response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
	}
	
	InfoLavoratore infoLav = new InfoLavoratore(new BigDecimal(cdnLavoratore));

	String htmlStreamTop = StyleUtils.roundTopTable(false);
	
	if (isPraticaAlreadyProcessed.equalsIgnoreCase("FALSE")){
		htmlStreamTop = StyleUtils.roundTopTable(true);
	}
	
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	
	GregorianCalendar cal = new GregorianCalendar();
	String numeroPratica = cal.get(Calendar.YEAR) + "/" + numeroProtocollo;
    
	

	
	
%>
<html>
<head>
<title>Protocollazione Patto Pi3</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
<af:linkScript path="../../js/"/>  
<script language="Javascript" src="../../js/documenti/docPopup.js"></script>
<script language="Javascript" src="../../js/documenti/dettagliDocumento.js"></script>

<%@ include file="_apriGestioneDoc.inc" %>
</head>
<body class="gestione" onload="rinfresca();">
	
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
					if (!StringUtils.isEmpty(message)){ %>
					
						<tr>
							<td class="etichetta" colspan="2" style="text-align: center;"><%=message%></td>
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
					<td class="etichetta" nowrap>Numero Protocollo&nbsp;</td>
					<td class="campo">
						<af:textBox classNameBase="input" type="text" name="numeroProtocollo" value="<%=numeroProtocollazione%>" readonly="true" title="numeroProtocollo" size="21" />
					</td>
				</tr>
				
				<tr>
					<td class="etichetta" nowrap>Data Protocollo&nbsp;</td>
					<td class="campo">
						<af:textBox classNameBase="input" type="text" name="dataProtocollo" value="<%=dataProtocollo%>" readonly="true" title="dataProtocollo" size="11" />
					</td>
				</tr>
				
				<tr>
					<td class="etichetta" nowrap>Data Invio&nbsp;</td>
					<td class="campo">
						<af:textBox classNameBase="input" type="text" name="dataInvio" value="<%=dataInvio%>" readonly="true" title="dataInvio" size="11" />
					</td>
				</tr>
				
				<tr>
					<td class="etichetta" nowrap>Stato del documento inviato&nbsp;</td>
					<td class="campo">
						<af:textBox classNameBase="input" type="text" name="statoDocInviato" value="<%=statoDocInviato%>" readonly="true" title="statoDocInviato" size="31" />
					</td>
				</tr>
				
				<tr><td colspan="2">&nbsp;</td></tr>
				

				<tr>
					<td class="etichetta" nowrap>Numero Pratica SPIL&nbsp;</td>
					<td class="campo">
						<af:textBox classNameBase="input" type="text" name="numeroPratica" value="<%=numeroPratica%>" readonly="true" title="numeroPratica" size="15" />
					</td>
				</tr>
				<tr>
					<td class="etichetta" nowrap>Tipo protocollazione &nbsp;</td>
					<td class="campo">
						<b>
							<af:textBox classNameBase="input" type="text" name="trattamentoDocumento" value="<%=descrizioneTipoProt%>" readonly="true" title="trattamentoDocumento" size="15" /> 
						</b>
					</td>
				</tr>
				<tr>
					<td class="etichetta" nowrap>Trattamento &nbsp;</td>
					<td class="campo">
						<b>
							<af:textBox classNameBase="input" type="text" name="trattamento" value="<%=descrizioneTrattamento%>" readonly="true" title="trattamento" size="15" /> 
						</b>
					</td>
				</tr>

<%--				
				<tr>
					<td class="etichetta" nowrap>Data ricevimento documento&nbsp;</td>
					<td style="color: #000066; font-family: Verdana, Arial, Helvetica, Sans-serif; font-size: 11px; text-decoration: none;">
						<b><%=dataacqril%></b>
					</td>
				</tr>
				
				<tr>
					<td class="etichetta" nowrap>Mittente/destinatario&nbsp;</td>
					<td style="color: #000066; font-family: Verdana, Arial, Helvetica, Sans-serif; font-size: 11px; text-decoration: none;">
						<b><%=infoLav.getCodFisc() %></b>
					</td>
				</tr>
 --%>				
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
						codicePAT = "24.3";
							if(codicePAT != null){ 
							
								Vector codiciDescTitolarioVect = serviceResponse.getAttributeAsVector("M_GET_CODICI_FROM_TITOLARIO.ROWS.ROW");
								if(codiciDescTitolarioVect!=null && codiciDescTitolarioVect.size()>0){
									for (int cdDescTit = 0; cdDescTit < codiciDescTitolarioVect.size(); cdDescTit++) {
										SourceBean codiciDescTitolario = (SourceBean)codiciDescTitolarioVect.get(cdDescTit);
										if(codiciDescTitolario!=null && codiciDescTitolario.getAttribute("codice").equals(codicePAT)){
											String descrizioneCodice = (String)codiciDescTitolario.getAttribute("descrizione");
										%>
										<af:textBox classNameBase="input" type="text" name="codicePATDESC" value="<%=descrizioneCodice%>" readonly="true" title="codicePAT" size="35" />
										<%
										}
									}
								}
							%>
								<input type="hidden" name="codicePAT" value="<%=codicePAT%>" />
							<%} else{ %>
								<af:comboBox addBlank="true" name="codicePAT" required="true" moduleName="M_GET_CODICI_FROM_TITOLARIO" selectedValue="<%=codicePAT%>" />
							<%
							}
						%>
					</td>
				</tr>
				<% 
				 boolean giaEsisteFileInDb = StringUtils.isFilled(nomeDocumento);
				boolean blobVuoto = !documentoSil.getExistBlob();
				if (giaEsisteFileInDb) { %>
				<tr>
			         <td class="etichetta" nowrap>
			           Nome documento&nbsp;
			         </td>
			         <td class="campo" nowrap="nowrap">
			         
			         	<af:textBox classNameBase="input" type="text" name="nomeDocumento" value="<%=nomeDocumento%>" readonly="true" title="nomeDocumento" size="39" />
			         <%
						String myJsBlobVuoto = "javascript:alert('Nel database non e' stato trovato il file:" + nomeDocumento + "')";
						%>
						Apri
						<%
						String myJsA;
						if (blobVuoto) myJsA = myJsBlobVuoto;
						else myJsA = "javascript:visualizzaDocumento('DOWNLOAD',''," + prgDocumento + ")";
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
			
				<%	
					if (filter.canView()) { %>
						
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
				<tr><td colspan="2">&nbsp;</td></tr>
				<%--
				<tr>
				<td colspan="2">
					<p>
					
						<font color="green"><%=messageTest%></font><br />
	 					<font color="green"><%=message%></font>
	  				</p>
				</td>
				</tr>
				 --%>
		</table>

	  	<input type="hidden" name="PAGE" value="Pi3HomePattoPage">
	  	<input type="hidden" name="pagina" value="PattoLavDettaglioPage">
	  	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>">
	  	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
	  	<input type="hidden" name="NUMPROT" value="<%=numeroProtocollo%>">
	  	<input type="hidden" name="docType" value="<%=docType%>">
	  	<input type="hidden" name="codTrattamento" value="<%=codiceTrattamento%>">
	  	<input type="hidden" name="actionPi3" value="W">
	  	<input type="hidden" name="prgDocumento" value="<%=prgDocumento%>">
	  	<input type="hidden" name="docFirmato" value="<%=docFirmato%>">
	  	<input type="hidden" name="docFirmabile" value="<%=docFirmabile%>">
	  	<input type="hidden" name="CONSENSO" value="<%=isConsensoAttivo%>">
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
