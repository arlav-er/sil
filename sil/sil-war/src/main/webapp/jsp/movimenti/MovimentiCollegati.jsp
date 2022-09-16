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
    PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
    boolean canLinkMov = attributi.containsButton("COLLEGA_SUCCESSIVO");
  
	//Oggetti per l'applicazione dello stile
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	
	String prgMovPrec = "";
	String prgMovSucc = "";
	boolean sezioneDatiAperta = false;
	
	String prgMovColl = StringUtils.getAttributeStrNotNull(serviceRequest, "PrgMovimentoColl");
	String prgPrimoPrec = StringUtils.getAttributeStrNotNull(serviceRequest, "PrgPrimoPrec");
	String prgPrimoSucc = StringUtils.getAttributeStrNotNull(serviceRequest, "PrgPrimoSucc");
	String codTipoMovimento = StringUtils.getAttributeStrNotNull(serviceRequest, "codtipomovimento");
	
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
	boolean precedente = !prgMovPrec.equalsIgnoreCase("");
	boolean successivo = !prgMovSucc.equalsIgnoreCase("");	
%>
<html>
	<head>
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
	<script type="text/javascript" src="../../js/movimenti/common/GestioneCollegati.js" language="JavaScript"></script>
	<script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
	<SCRIPT language="javascript">
	document.impostaCollegati = impostaCollegati;
	</SCRIPT>
	</head>
	<body onload="rinfresca();inizializzaSezione(document.getElementById('pulsanteCollegati'), document.getElementById('sezioneCollegato'), <%=sezioneDatiAperta%>);reimpostaPulsantiCollegati(<%=precedente%>, <%=successivo%>);">
      <af:form name="Form1" method="POST" action="AdapterHTTP">
		<center>
			<%out.print(htmlStreamTop);%>
			<table width="100%">
				<tr>
					<td align="center" width="100%">
              			<div class='sezione2' id='tendinaCollegati'>
                			<img id='pulsanteCollegati' alt='Apri' src='../../img/chiuso.gif' onclick='gestisciFrameCollegato(this, document.getElementById("sezioneCollegato"));'/>
                			&nbsp;Consulta movimenti collegati               
             			</div>
    					<div id="sezioneCollegato" style="display: none;">
							<table width="100%">
								<tr>
									<td width="32%">
										<a style="display: none;" id="PulsantePrecedente" href="#" onClick="consultaCollegato('indietro');">
                    						<img src="../../img/indietro.gif" alt="vai al precedente"/>Precedente
                  						</a>
									</td>
									<td  width="32%" align="center">
										<div id='titoloCollegato'>
										<%if (dataType.equalsIgnoreCase("AVV")) {%><strong>Avviamento</strong>
										<%} else if (dataType.equalsIgnoreCase("PRO")) {%><strong>Proroga</strong>
										<%} else if (dataType.equalsIgnoreCase("TRA")) {%><strong>Trasformazione</strong>
										<%} else if (dataType.equalsIgnoreCase("CES")) {%><strong>Cessazione</strong>
										<%} else if (dataType.equalsIgnoreCase("ERROR")) {%><strong>Errore</strong>
										<%}%>
										</div>
									</td>
									<td  width="32%" align="right">
										<a style="display: none;" id="PulsanteSuccessivo" href="#" onClick="consultaCollegato('avanti');">
                    						Successivo<img src="../../img/avanti.gif" alt="vai al successivo"/>
                  						</a>
                  						<%if (canLinkMov && !codTipoMovimento.equalsIgnoreCase("CES")) {%>
										<a style="display: none;" id="PulsanteCollegaSuccessivo" href="#" onClick="window.parent.collegaSuccessivo();">
                    						Collega a Successivo<img src="../../img/collega_movimento.gif" alt="collega movimento successivo"/>
                  						</a>               
                  						<%}%>   						
									</td>
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
			<input type="hidden" name="codtipomovimento" value="<%=codTipoMovimento%>"/>	
			<input type="hidden" name="PrgPrimoPrec" value="<%=prgPrimoPrec%>"/>
			<input type="hidden" name="PrgPrimoSucc" value="<%=prgPrimoSucc%>"/>			
			<input type="hidden" name="PrgMovimentoColl" value="<%=prgMovColl%>"/>
		</center>
	</af:form>
</body>
</html>