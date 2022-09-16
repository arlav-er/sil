<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
    // NOTE: Attributi della pagina (pulsanti e link) 
    /****
    PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
    boolean canLinkMov = attributi.containsButton("COLLEGA_SUCCESSIVO");
  	*/
  	boolean canLinkMov = false;
	//Oggetti per l'applicazione dello stile
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	
	String prgMovPrec = "";
	String prgMovSucc = "";
	BigDecimal cdnUtIns             = null;
    String     dtmIns               = null;
    BigDecimal cdnUtMod             = null;
    String     dtmMod               = null;
	boolean sezioneDatiAperta = false;
	
	String prgMovColl = StringUtils.getAttributeStrNotNull(serviceRequest, "PrgMovimentoColl");
	String prgPrimoPrec = StringUtils.getAttributeStrNotNull(serviceRequest, "PrgPrimoPrec");
	String prgPrimoSucc = StringUtils.getAttributeStrNotNull(serviceRequest, "PrgPrimoSucc");
	SourceBean dati = null;
	//Guardo che cosa devo visualizzare nella sezione dati:
	//Se il prgMovColl è null o vuoto non ho ancora inizializzato il frame
	String dataType = "";
	if (prgMovColl == null || prgMovColl.equals("")) {
		dataType = "NONINIZIALIZZATO";
	} else {
		sezioneDatiAperta = true;
		if (prgMovColl.equalsIgnoreCase("CORRENTE")) {
			//Sono sul movimento corrente, ripristino i valori dei progressivi primi
			dataType = "CORRENTE";
			prgMovPrec = prgPrimoPrec;
			prgMovSucc = prgPrimoSucc;
		} else {
			//Devo visualizzare i dati di un movimento, li estraggo dalla response
			dati = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovColl.ROWS.ROW");
			//Se non ho dati mostro un messaggio di errore
			if (dati == null) {
				dataType = "ERROR";
			} else {
				//ne estraggo il tipo e i progressivi relativi
				dataType = StringUtils.getAttributeStrNotNull(dati, "codTipoMov");
				prgMovPrec = StringUtils.getAttributeStrNotNull(dati, "prgMovimentoPrec");
				prgMovSucc = StringUtils.getAttributeStrNotNull(dati, "prgMovimentoSucc");
				cdnUtIns =  (BigDecimal) dati.getAttribute("CDNUTINS");
				dtmIns =  (String)     dati.getAttribute("DTMINS");
				cdnUtMod =  (BigDecimal) dati.getAttribute("CDNUTMOD");
				dtmMod  =  (String) dati.getAttribute("DTMMOD");
			}
			//Controllo se sono sul primo precedente o successivo e imposto
			//i valori dei progressivi corrispondenti a "CORRENTE"
			if (prgMovColl.equalsIgnoreCase(prgPrimoPrec)) {
				prgMovSucc = "CORRENTE";
			} else if (prgMovColl.equalsIgnoreCase(prgPrimoSucc)) {
				prgMovPrec = "CORRENTE";
			}
		}
	}
	SourceBean azienda = null;
	// VERRA' RITORNATA SEMPRE E SOLO UN RECORD?
	// PER ORA MI CAUTELO COSI' 
	// ANDREA (10/01/2005)
	try {
		azienda = (SourceBean)serviceResponse.getAttribute("M_GETDETTAZIENDA.rows.row");
	}catch (ClassCastException cce) {
		Vector v = serviceResponse.getAttributeAsVector("M_GETDETTAZIENDA.rows.row");
		if (v.size()>0) azienda = (SourceBean ) v.get(0);
	}
	String strRagioneSociale = "", strLuogoDiLavoro="", strIndirizzo="", strComune="",codCom="";
	if (azienda !=null) {
		strRagioneSociale = StringUtils.getAttributeStrNotNull(azienda, "strRagioneSocialeAz");
		strIndirizzo = StringUtils.getAttributeStrNotNull(azienda, "strIndirizzoUaz");
		strComune = StringUtils.getAttributeStrNotNull(azienda, "strComuneUaz");
		codCom = StringUtils.getAttributeStrNotNull(azienda, "codComUaz");
	}
	Testata operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	boolean precedente = !prgMovPrec.equalsIgnoreCase("");
	boolean successivo = !prgMovSucc.equalsIgnoreCase("");	
%>
<html>
	<head>
		<title>Percorso lavoratore: movimento</title>
		<link rel="stylesheet" href="../../css/stili.css" type="text/css">
		<af:linkScript path="../../js/"/>
	<SCRIPT language="JavaScript">
		var prgMovimentoPrec = '<%=prgMovPrec%>';
		var prgMovimentoSucc = '<%=prgMovSucc%>';
		//Imposta gli attibuti della sezione e la apre se deve (se il terzo argomento è true)
		function inizializzaSezione(immagine, sezione, daAprire) {
			sezione.aperta = false;
			if (daAprire) {cambia(immagine, sezione);}
		}
	</SCRIPT>	
	</head>
	<body onload="rinfresca();">
		<br>
      <af:form name="Form1" method="POST" action="AdapterHTTP">
		<center>		
			<%out.print(htmlStreamTop);%>
			<table width="100%">
			<tr><td ><p class="titolo">Sintesi movimento</p></td></tr>
				<tr>
					<td align="center" width="100%">              			
    					<div id="sezioneCollegato" style="display: ">
							<table width="100%">
								
								<tr><td colspan="3">&nbsp;</tr>
								<tr>
									<td class="etichetta">Ragione sociale: 
									<td class="campo" colspan="2"><strong><%=strRagioneSociale%></strong>
								</tr>
								<tr>
									<td class="etichetta">Indirizzo: 
									<td class="campo" colspan="2"><strong><%=strIndirizzo%></strong>
								</tr>
								<tr>
									<td class="etichetta">Comune: 
									<td class="campo" colspan="2"><strong><%=codCom%>&nbsp;&nbsp;<%=strComune%></strong>
								</tr>
								<tr>
									<td colspan=3>
										<div id='datiCollegato'>
											<%if (dataType.equalsIgnoreCase("AVV")) {%>
												<%@ include file="../movimenti/common/include/ConsultaDatiAvviamento.inc" %>								
											<%} else if (dataType.equalsIgnoreCase("PRO")) {%>
												<%@ include file="../movimenti/common/include/ConsultaDatiProroga.inc" %>
											<%} else if (dataType.equalsIgnoreCase("TRA")) {%>
												<%@ include file="../movimenti/common/include/ConsultaDatiTrasformazione.inc" %>
											<%} else if (dataType.equalsIgnoreCase("CES")) {%>
												<%@ include file="../movimenti/common/include/ConsultaDatiCessazione.inc" %>
											<%} else if (dataType.equalsIgnoreCase("CORRENTE")) {%><p align="center"><strong>La posizione corrisponde al movimento corrente</strong></p>
											<%} else if (dataType.equalsIgnoreCase("NONINIZIALIZZATO")) {%><p align="center"><strong>Frame di consultazione non inizializzato</strong></p>
											<%} else if (dataType.equalsIgnoreCase("ERROR")) {%><p align="center"><strong>Impossibile visualizzare i dati</strong></p>
											<%}%>
										</div>
									</td>
								</tr>
							</table>
    					</div>
					</td>
				</tr>
			</table>
			<%out.print(htmlStreamBottom);%>
			<input type="hidden" name="PAGE" value="MovimentiCollegatiPage"/>	
			<input type="hidden" name="PrgPrimoPrec" value="<%=prgPrimoPrec%>"/>
			<input type="hidden" name="PrgPrimoSucc" value="<%=prgPrimoSucc%>"/>			
			<input type="hidden" name="PrgMovimentoColl" value="<%=prgMovColl%>"/>
		</center>
		<center>
			<% operatoreInfo.showHTML(out);%>	
			<br>
			<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
		</center>
	</af:form>
</body>
</html>