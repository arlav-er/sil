<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.dbaccess.sql.TimestampDecorator,
  it.eng.sil.security.PageAttribs,
  it.eng.sil.security.ProfileDataFilter,
  com.engiweb.framework.security.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
// bisogna ripulire lo url della listamemorizzato in sessione per evitare che venga chiamato di nuovo
// il modulo di associazione di una pubblicazione
String urlLista  = (String)sessionContainer.getAttribute("_TOKEN_IDOLISTAPUBBGIORLISTAPAGE");
urlLista = StringUtils.replace(urlLista, "MODULE=INSERISCI","MODULE=");
urlLista = StringUtils.replace(urlLista, "ASSOCIA=S","ASSOCIA=N");
sessionContainer.setAttribute("_TOKEN_IDOLISTAPUBBGIORLISTAPAGE", urlLista);
//

String anno="", numero="", strRagioneSociale="", strIndirizzo="", datPubblicazione="", 
	datScadenzaPubblicazione="", prgRichiestaAz="", prgElencoGiornale="", cdnFunzione="", 
	numPriorita="", _page="", prgUnita="", prgAzienda="";
	
	cdnFunzione = Utils.notNull(serviceRequest.getAttribute("cdnFunzione"));
  	_page = Utils.notNull(serviceRequest.getAttribute("page"));
	SourceBean row =(SourceBean) serviceResponse.getAttribute("M_DETTAGLIO_PUBB_GIORNALE.ROWS.ROW");
	if (row!=null ){
		anno = Utils.notNull(row.getAttribute("ANNO"));
		numero = Utils.notNull(row.getAttribute("NUMRICHIESTA"));
		strRagioneSociale = Utils.notNull(row.getAttribute("STRRAGIONESOCIALE"));
		strIndirizzo = Utils.notNull(row.getAttribute("STRINDIRIZZO"));
		datPubblicazione = Utils.notNull(row.getAttribute("DATPUBBLICAZIONE"));
		datScadenzaPubblicazione = Utils.notNull(row.getAttribute("DATSCADENZAPUBBLICAZIONE"));
		numPriorita = Utils.notNull(row.getAttribute("numPriorita"));	
		prgRichiestaAz = Utils.notNull(row.getAttribute("prgRichiestaAz"));	
		prgElencoGiornale = Utils.notNull(row.getAttribute("prgElencoGiornale"));	
		prgAzienda = Utils.notNull(row.getAttribute("prgAzienda"));	
		prgUnita = Utils.notNull(row.getAttribute("prgUnita"));	
	}

  
  
  // NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, _page);
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setPrgAzienda(new BigDecimal(prgAzienda));
	filter.setPrgUnita(new BigDecimal(prgUnita));
	boolean canModify= attributi.containsButton("AGGIORNA");	
	//canModify = true;
	
	
  
  
  
  
  
  
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);


%>

<html>

<head>
  <title>Dettaglio pubblicazione giornale</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
	<script>
	function fieldChanged() {        
  <% if ( canModify ) { %> 
    flagChanged = true;
  <% } %> 
}
	</script>
</head>
<body class="gestione">
<br/>
<br/>

<font color="green"><af:showMessages prefix="M_UPDATE_PUBB_GIORNALE"/></font>
<font color="red"><af:showErrors /></font>

<p align="center" class="titolo">Dettaglio pubblicazione giornale</p>
<af:form method="POST" action="AdapterHTTP" name="Frm1">
	<input type="hidden" name="PAGE" value="IdoDettaglioPuggGiornalePage"/>
	<input type="hidden" name="cdnFunzione" value="<%=cdnFunzione%>"/>
	<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>">
	<input type="hidden" name="PRGELENCOGIORNALE" value="<%=prgElencoGiornale%>">
	<div align="center">
<%out.print(htmlStreamTop);%>
		<table class="main">
			<tr>
				<td class="etichetta">Numero</td>
				<td class="campo">
					<af:textBox  name="NUMRICHIESTA" classNameBase ="input"  maxlength="10" size="10"
						readonly="true" value="<%=numero%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Anno</td>
				<td class="campo">
					<af:textBox  name="ANNO" classNameBase ="input"  maxlength="10" size="10"
						readonly="true" value="<%=anno%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Ragione sociale</td>
				<td class="campo">
					<af:textBox  name="STRRAGIONESOCIALE" classNameBase ="input"  maxlength="100" size="80"
						readonly="true" value="<%=strRagioneSociale%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Indirizzo</td>
				<td class="campo">
					<af:textBox  name="STRINDIRIZZO" classNameBase ="input"  maxlength="100" size="80"
						readonly="true" value="<%=strIndirizzo%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Data pubblicazione</td>
				<td class="campo">
					<af:textBox  name="DATPUBBLICAZIONE" classNameBase ="input"  maxlength="11" size="11"
						readonly="true" value="<%=datPubblicazione%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Data scadenza</td>
				<td class="campo">
					<af:textBox  name="DATSCADENZAPUBBLICAZIONE" classNameBase ="input"  maxlength="11" size="11"
						readonly="true" value="<%=datScadenzaPubblicazione%>"/>
				</td>
			</tr>
			<tr>
			<%
				SourceBean mPriorita = new SourceBean("ROWS");
				for (int i=1;i<=5;i++) {
					SourceBean sb = new SourceBean("ROW");
					sb.setAttribute("DESCRIZIONE",String.valueOf(i));
					sb.setAttribute("CODICE",String.valueOf(i));
					mPriorita.setAttribute(sb);
				}
				serviceResponse.setAttribute("M_PRIORITA_CUSTOM", mPriorita);
			%>
				<td class="etichetta">Numero priorit&agrave;</td>
				<td class="campo">
					<af:comboBox  name="NUMPRIORITA" classNameBase ="input"  onChange="fieldChanged();"
						required="true" moduleName="M_PRIORITA_CUSTOM"
						disabled="<%=String.valueOf(!canModify)%>" selectedValue="<%=numPriorita%>" />
				</td>
			</tr>
			<tr><td colspan="2">&nbsp;</tr> 
		<%
		if (canModify) {
		%>
			<tr>
				<td colspan="2" align="center">
				  <input type="submit" class="pulsanti" name="salva" value="Salva">
				</td>
			</tr>
		<%
		}
		%>
			<tr>
				<td colspan="2">
					<%out.print(InfCorrentiAzienda.formatBackList(sessionContainer, "IdoListaPubbGiorListaPage"));%>
				</td>
			</tr>
		</table>	

<%out.print(htmlStreamBottom);%>
	</div>
</af:form>
</body>
</html>
