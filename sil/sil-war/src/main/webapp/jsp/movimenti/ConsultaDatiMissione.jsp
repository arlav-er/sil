<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.afExt.utils.*,
                  java.math.*,
                  java.io.*,
                  java.util.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*"%>


<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
  String datInizioMis="";
  String datFineMis="";
  String datInizioContratto="";
  String datFineContratto="";
  String codOrario="";
  String descOrario="";
  String numOreSett="";
  String codTipoContratto="";
  String descTipoContratto="";
  String codMansione="";
  String descMansione="";
  String codAgevolazione="";
  String descrAge="";
  String codCCNL="";
  String descCCNL="";
  String codMvCessazione="";
  String descrCess="";
  String prgMovimento="";
  String prgMissione="";
  String codTipoMis="";
  String strAziNtNumContratto="";
  String descTipoMis="";
  String descTrasf="";
  String codTipoTrasf="";
  String strDesAttivita="";
  String numGgPrevistiAgr="";
  String strLavorazione="";
  String strAzIntRap="";
  String strPatInail="";
  String codMonoProv="";
  String concatenaContratto="";
  String concatenaMansione="";
  String concatenaCCNL="";
  String strRagioneUtil="";
  String strIndirizzoUtil="";
  String codComunicazione="";
  String strCodiceFiscaleAz="";
  String datComunicazione="";
  String numLivello="";
  String concatenaAgevolazioni="";
  String nomeCognome="";
  String codiceFisc="";
  String codMonoTipoInf="";
  BigDecimal cdnUtIns = null;
  String dtmIns="";
  BigDecimal cdnUtMod = null;
  String dtmMod="";
  String decRetribuzioneAnn = "";
  Testata operatoreInfo=null;
  
  prgMovimento = StringUtils.getAttributeStrNotNull(serviceRequest,"prgMovimento");
  codMonoTipoInf = StringUtils.getAttributeStrNotNull(serviceRequest,"CODMONOTIPOINF");
  
  boolean canModify = false;
  
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  
  SourceBean rowMiss = (SourceBean)serviceResponse.getAttribute("Mov_DatiMissione.ROWS.ROW");
  if(rowMiss!= null) {
  	datInizioMis = StringUtils.getAttributeStrNotNull(rowMiss, "datInizioMis");
  	datFineMis = StringUtils.getAttributeStrNotNull(rowMiss, "datFineMis");
  	datInizioContratto = StringUtils.getAttributeStrNotNull(rowMiss, "datazintiniziocontratto");
  	datFineContratto = StringUtils.getAttributeStrNotNull(rowMiss, "datazintfinecontratto");
  	codOrario = StringUtils.getAttributeStrNotNull(rowMiss, "codOrario");
  	descOrario = StringUtils.getAttributeStrNotNull(rowMiss, "descOrario");
  	numOreSett = StringUtils.getAttributeStrNotNull(rowMiss, "numOreSett");
  	codTipoContratto = StringUtils.getAttributeStrNotNull(rowMiss, "codTipoContratto");
  	descTipoContratto = StringUtils.getAttributeStrNotNull(rowMiss, "DESCTIPOCONTR");
  	codMansione = StringUtils.getAttributeStrNotNull(rowMiss, "codMansione");
  	descMansione = StringUtils.getAttributeStrNotNull(rowMiss, "descMansione");
  	codAgevolazione = StringUtils.getAttributeStrNotNull(rowMiss, "codAgevolazione");
  	descrAge = StringUtils.getAttributeStrNotNull(rowMiss, "descrAge");
  	codCCNL = StringUtils.getAttributeStrNotNull(rowMiss, "codCCNL");
  	descCCNL = StringUtils.getAttributeStrNotNull(rowMiss, "descCCNL");
  	codMvCessazione = StringUtils.getAttributeStrNotNull(rowMiss, "codMvCessazione");
  	descrCess = StringUtils.getAttributeStrNotNull(rowMiss, "descrCess");
  	strAziNtNumContratto = StringUtils.getAttributeStrNotNull(rowMiss, "STRAZINTNUMCONTRATTO");
  	codTipoMis = StringUtils.getAttributeStrNotNull(rowMiss, "codTipoMis");
  	descTipoMis = StringUtils.getAttributeStrNotNull(rowMiss, "DESCTIPOMOV");
  	strDesAttivita = StringUtils.getAttributeStrNotNull(rowMiss, "strDesAttivita");
  	numGgPrevistiAgr = StringUtils.getAttributeStrNotNull(rowMiss, "NUMGGPREVISTIAGR");
  	strLavorazione = StringUtils.getAttributeStrNotNull(rowMiss, "STRLAVORAZIONE");
  	strAzIntRap = StringUtils.getAttributeStrNotNull(rowMiss, "STRAZINTRAP");
  	strPatInail = StringUtils.getAttributeStrNotNull(rowMiss, "STRPATINAIL");
  	codMonoProv = StringUtils.getAttributeStrNotNull(rowMiss, "CODMONOPROV");
  	codComunicazione = StringUtils.getAttributeStrNotNull(rowMiss, "CODCOMUNICAZIONE");
  	strCodiceFiscaleAz = StringUtils.getAttributeStrNotNull(rowMiss, "STRCODICEFISCALE");
  	datComunicazione = StringUtils.getAttributeStrNotNull(rowMiss, "DATCOMUNICAZ");
  	numLivello = StringUtils.getAttributeStrNotNull(rowMiss, "NUMLIVELLO");
  	nomeCognome = StringUtils.getAttributeStrNotNull(rowMiss, "STRNOMECOGNOME");
    codiceFisc = StringUtils.getAttributeStrNotNull(rowMiss, "CF");
    strRagioneUtil = StringUtils.getAttributeStrNotNull(rowMiss,"STRRAGIONESOCIALE");
    strIndirizzoUtil = StringUtils.getAttributeStrNotNull(rowMiss,"STRINDIRIZZO");
    cdnUtIns =  (BigDecimal) rowMiss.getAttribute("CDNUTINS");
    dtmIns =  (String) rowMiss.getAttribute("DTMINS");
    cdnUtMod =  (BigDecimal) rowMiss.getAttribute("CDNUTMOD");
    dtmMod  =  (String) rowMiss.getAttribute("DTMMOD");
    operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
    decRetribuzioneAnn = StringUtils.getAttributeStrNotNull(rowMiss, "decRetribuzioneAnn"); 
  }
  
  concatenaContratto = codTipoContratto + " - " + descTipoContratto;
  concatenaMansione = codMansione + " - " + descMansione;
  concatenaCCNL = codCCNL + " - " + descCCNL;
  concatenaAgevolazioni = codAgevolazione + " - " + descrAge;
  
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
   
%>

<html>

<head>
<title>Dettaglio Missione</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/" />
<script language="JavaScript">

	function tornaLista() {
		if (isInSubmit()) return;
		var f;
      	f = "AdapterHTTP?PAGE=VisualizzaDatiMissionePage";
      	f = f + "&STRNOMECOGNOME=<%=nomeCognome%>";
	  	f = f + "&CF=<%=codiceFisc%>";
	  	f = f + "&prgMovimento=<%=prgMovimento%>";
	  	document.location = f;
  }

</script>

</head>
<body class="gestione" onload="rinfresca();">
	<br>
	<p class="titolo">Dettaglio Missione</p>

	<font color="red"><af:showErrors /></font>

	<af:form name="form1" method="POST" action="AdapterHTTP">

		<%= htmlStreamTop %>
		<table class="main" border="0" align="left">

			<tr>
				<td class="etichetta" >Codice comunicazione</td>
				<td class="campo"><b><%=codComunicazione%></b>
				</td>
			</tr>
			<tr>
				<td class="etichetta" >Rag.&nbsp;Soc.</td>
				<td class="campo"><b><%=strRagioneUtil%></b>
				</td>
			</tr>
			<tr>
				<td class="etichetta" >C.F. Azienda</td>
				<td class="campo"><b><%=strCodiceFiscaleAz%><b/></td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Indirizzo Azienda</td>
				<td class="campo"><b><%=strIndirizzoUtil%><b/></td>
			</tr>
			<tr>
				<td class="etichetta">Num. Contratto</td>
				<td class="campo"><b><%=strAziNtNumContratto%><b/></td>
			</tr>

			<tr>
				<td class="etichetta">Data Inizio Contratto</td>
				<td class="campo"><b><%=datInizioContratto%><b/></td>
			</tr>

			<tr>
				<td class="etichetta">Data Fine Contratto</td>
				<td class="campo"><b><%=datFineContratto%><b/></td>
			</tr>

			<tr>
				<td class="etichetta">Rap. Sede legale</td>
				<td class="campo"><b><%=strAzIntRap%><b/></td>
			</tr>

			<tr>
				<td class="etichetta">PAT Inail</td>
				<td class="campo"><b><%=strPatInail%><b/></td>
			</tr>
			<tr>
				<td class="etichetta">Tipo</td>
				<td class="campo"><b><%=descTipoMis%><b/></td>
			</tr>
			<tr>
				<td class="etichetta">Data di comunicazione</td>
				<td class="campo"><b><%=datComunicazione%><b/></td>
			</tr>
			<tr>
				<td class="etichetta">Data Inizio Missione</td>
				<td class="campo"><b><%=datInizioMis%><b/></td>
			</tr>
			<tr>
				<td class="etichetta">Data Fine Missione</td>
				<td class="campo"><b><%=datFineMis%><b/>
				</td>
			</tr>
			<tr>
				<td class="etichetta" >Orario</td>
				<td  class="campo2">
					<table style="border-collapse: collapse">
						<tr>
							<td class="campo2"><b><%=descOrario%><b/>
							</td>
							<td>&nbsp;</td>
							<td id="labelore"
								style="display:<%=(codOrario.equals("F") || codOrario.equals("TP1") || codOrario.equals("N") || codOrario.equals("")) ? "none" : "" %>;"
								class="etichetta2">Ore settimanali</td>
							<td class="campo2" id="numore"
								style="display:<%=(codOrario.equals("F") || codOrario.equals("TP1") || codOrario.equals("N") || codOrario.equals("")) ? "none" : "" %>;">
								<b><%=numOreSett%><b/>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Mansione</td>
				<td class="campo"><b><%=concatenaMansione%><b/></td>
			</tr>
			<tr>
				<td class="etichetta">CCNL</td>
				<td class="campo"><b><%=concatenaCCNL%><b/></td>
			</tr>
			<tr>
				<td class="etichetta">Livello</td>
				<td class="campo"><b><%=numLivello%><b/></td>
			</tr>
			<tr>
				<td class="etichetta">Compenso Annuale</td>
				<td class="campo"><b><%=decRetribuzioneAnn%><b/></td>
			</tr>
			<tr>
				<td class="etichetta">Agevolazioni</td>
				<td class="campo"><b><%=concatenaAgevolazioni%><b/></td>
			</tr>
			<!-- <tr>
	<td class="etichetta">Tipo di Contratto</td>
	<td class="campo" >
		 <af:textBox title="Descrizione del tipo di avviamento" value="<%=concatenaContratto%>" classNameBase="input" name="descTipoContratto" size="75" readonly="<%=String.valueOf(!canModify)%>"/>         
	</td>
</tr> -->
			<%
				if (codTipoMis.equals("CES")) {
			%>
			<tr>
				<td class="etichetta">Motivo Cessazione</td>
				<td class="campo"><b><%=descrCess%><b/></td>
			</tr>
			<%
				}
					if (codTipoMis.equals("TRA")) {
			%>
			<tr>
				<td class="etichetta">Motivo Trasformazione</td>
				<td class="campo"><b><%=codTipoTrasf%><b/></td>
			</tr>
			<%
				}
			%>
			<tr>
				<td class="etichetta">Giorni in agric.</td>
				<td class="campo"><b><%=numGgPrevistiAgr%><b/></td>
			</tr>
			<tr>
				<td class="etichetta">Lavorazione</td>
				<td class="campo"><b><%=strLavorazione%><b/></td>
			</tr>
			<tr>
				<td colspan="6">&nbsp;</td>
			</tr>
			<tr>
				<td align="center" colspan="6"></td>
			</tr>

		</table>
		<%=htmlStreamBottom%>
		<center>
			<%
				operatoreInfo.showHTML(out);
			%>
			<br>
			<%
				if (codMonoTipoInf.equals("")) {
						//Esposito - se codMonoTipoInf è valorizzato allora vengo dalla pagina del percorso lavoratore
						//e mostro solo il pulsante "Chiudi"
			%>
			<input type="button" class="pulsanti" value="torna alla lista"
				onclick="tornaLista()">
			<%
				}
			%>
			<input type="button" class="pulsanti" value="Chiudi"
				onClick="window.close();" />
		</center>
	</af:form>
</body>
</html>
