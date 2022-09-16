<%@page import="it.eng.sil.action.report.UtilsConfig"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%

		Testata operatoreInfo = null;
		
		BigDecimal cdnUtins = null;
		String dtmins = "";
		BigDecimal cdnUtmod = null;
		String dtmmod = "";
		String datApp = "";
		String strOraApp = "";
		BigDecimal prgSpiApp = null;
		BigDecimal prgSpiAppEff = null;
		String strSpiApp = "";
		String strSpiAppEff = "";
		String codServizioApp = "";
		String prgAmbiente = "";		
		BigDecimal nDurataMinuti = null;
		String strDurata = "";
		String CODEFFETTOAPPUNT = "";
		String CODESITOAPPUNT = "";
		String CODSTATOAPPUNTAMENTO = "";
		String txtNoteApp = "";
		String _page = "";
		String strCognome ="";
		String strNome = "";
		String strCell = "";
		String testoSms ="";
		String maxLengthTestoSMS="";
		
		SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_AGENDA_SMS_DETTAPPUNTAMENTI");
		SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
		_page = (String) serviceRequest.getAttribute("PAGE");
		
		cdnUtins = row.getAttribute("cdnUtins") != null?(BigDecimal) row.getAttribute("cdnUtins"):null;
		dtmins = StringUtils.getAttributeStrNotNull(row, "dtmins");
		cdnUtmod = row.getAttribute("cdnUtmod") != null?(BigDecimal) row.getAttribute("cdnUtmod"):null;
		dtmmod = StringUtils.getAttributeStrNotNull(row, "dtmmod");
		
		//informazioni sull'operatore
		operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
		datApp = StringUtils.getAttributeStrNotNull(row, "DATA");
		strOraApp = StringUtils.getAttributeStrNotNull(row, "ORARIO");
		prgSpiApp = row.getAttribute("PRGSPI") != null?(BigDecimal) row.getAttribute("PRGSPI"):null;
		if (prgSpiApp != null) {
			strSpiApp = prgSpiApp.toString();
		}
		prgSpiAppEff = row.getAttribute("PRGSPIEFF") != null?(BigDecimal) row.getAttribute("PRGSPIEFF"):null;
		if (prgSpiAppEff != null) {
			strSpiAppEff = prgSpiAppEff.toString();
		}
		codServizioApp = StringUtils.getAttributeStrNotNull(row, "CODSERVIZIO");
		BigDecimal ambiente = row.getAttribute("PRGAMBIENTE") != null?(BigDecimal) row.getAttribute("PRGAMBIENTE"):null;
		if (ambiente != null) prgAmbiente = ambiente.toString();
		nDurataMinuti = row.getAttribute("NUMMINUTI") != null?(BigDecimal) row.getAttribute("NUMMINUTI"):null;
		if (nDurataMinuti != null) {
			strDurata = nDurataMinuti.toString();
		}
		txtNoteApp =	row.containsAttribute("TXTNOTE")? row.getAttribute("TXTNOTE").toString(): "";
		CODEFFETTOAPPUNT = StringUtils.getAttributeStrNotNull(row, "CODEFFETTOAPPUNT");
		CODESITOAPPUNT = StringUtils.getAttributeStrNotNull(row, "CODESITOAPPUNT");
		CODSTATOAPPUNTAMENTO = StringUtils.getAttributeStrNotNull(row, "CODSTATOAPPUNTAMENTO");
		
		String smsKeyContatto = (String) serviceRequest.getAttribute("SMSKEY");
		
		
		//formmattazione della tabella della pagina jsp
		String htmlStreamTop = StyleUtils.roundTopTable(false);
		String htmlStreamBottom = StyleUtils.roundBottomTable(false);
		
		String htmlStreamTopModify = StyleUtils.roundTopTable(true);
		String htmlStreamBottomModify = StyleUtils.roundBottomTable(true);
		
	    String labelServizio = "Servizio";
	    String umbriaGestAz = "0";
	    if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	    	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
	    }
	    if(umbriaGestAz.equalsIgnoreCase("1")){
	    	labelServizio = "Area";
	    }
	    
	    // Invio SMS Dati Lavoratore
	    SourceBean configSms = (SourceBean) serviceResponse.getAttribute("M_CONFIG_SMS_PERS.ROWS.ROW");
		String valConfigSms = StringUtils.getAttributeStrNotNull(configSms, "NUM");
		if (valConfigSms.equals("1")) {
			SourceBean contSms = (SourceBean) serviceResponse.getAttribute("M_GetLavoratoreAnag");
			SourceBean rowSms = (SourceBean) contSms.getAttribute("ROWS.ROW");
			strCognome = StringUtils.getAttributeStrNotNull(rowSms, "STRCOGNOME");
			strNome = StringUtils.getAttributeStrNotNull(rowSms, "STRNOME");
			strCell = StringUtils.getAttributeStrNotNull(rowSms, "STRCELL");
			
			SourceBean testoSmsBean = (SourceBean) serviceResponse.getAttribute("M_GET_TESTO_SMS_PERS");
			if (testoSmsBean!=null) {
				testoSms = StringUtils.getAttributeStrNotNull(testoSmsBean, "testo");
				maxLengthTestoSMS = StringUtils.getAttributeStrNotNull(testoSmsBean, "maxLengthTestoSMS");
			}
		}
	    	    
	   
%>

<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<af:linkScript path="../../js/" />
<title></title>
<script type="text/javascript">
  function tornaAllaRicerca()
  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
  
     url="AdapterHTTP?PAGE=AGENDA_SMS_LISTALAVORATORI_PAGE";
     
     setWindowLocation(url);
  }

  
</script>


</head>
<body class="gestione" onload="rinfresca()">
<br>
	<p align="center"><%out.print(htmlStreamTop);%> 
	<p class="titolo">Dettaglio Appuntamento</p>
	<input type="hidden" name="PAGE" value="AGENDA_SMS_LISTALAVORATORI_PAGE"></p>
	<table class="main">
		<TBODY>
			<tr>
				<td class="etichetta">Data</td>
				<td class="campo"><af:textBox name="DATAAPP" size="11"
					maxlength="10" type="date" readonly="true" classNameBase="input"
					value="<%=datApp%>" /></td>
			</tr>
			<tr>
				<td class="etichetta">Orario</td>
				<td class="campo"><af:textBox name="ORARIOAPP" size="5"
					maxlength="5" type="time" title="Orario" readonly="true"
					classNameBase="input" value="<%=strOraApp%>" /></td>
			</tr>
			<tr>
				<td class="etichetta">Durata</td>
				<td class="campo"><af:textBox name="DurataApp" size="5"
					maxlength="5" type="text" title="Durata" readonly="true"
					classNameBase="input" value="<%=strDurata%>" /></td>
			</tr>
			<tr>
				<td class="etichetta"><%=labelServizio %></td>
				<td class="campo"><af:comboBox name="SERVIZIOAPP" size="1"
					title="<%=labelServizio %>" multiple="false" disabled="true"
					classNameBase="input" focusOn="false"
					moduleName="COMBO_SERVIZIO"
					selectedValue="<%=codServizioApp%>" addBlank="true" blankValue="" />
				</td>
			</tr>
			<tr>
				<td class="etichetta">Operatore</td>
				<td class="campo"><af:comboBox name="PRGSPIAPP" size="1"
					title="Operatore" multiple="false" disabled="true"
					classNameBase="input" focusOn="false" moduleName="COMBO_SPI_SCAD"
					selectedValue="<%=strSpiApp%>" addBlank="true" blankValue="" /></td>
			</tr>
			<tr>
				<td class="etichetta">Operatore effettivo</td>
				<td class="campo"><af:comboBox name="PRGSPIAPPEFF" size="1"
					title="Operatore effettivo" multiple="false" disabled="true"
					classNameBase="input" focusOn="false" moduleName="COMBO_SPI_SCAD_EFF"
					selectedValue="<%=strSpiAppEff%>" addBlank="true" blankValue="" /></td>
			</tr>

			<tr>
				<td class="etichetta">Ambiente</td>
				<td class="campo"><af:comboBox name="AMBIENTEAPP" size="1"
					title="Ambiente" multiple="false" disabled="true"
					classNameBase="input" focusOn="false"
					moduleName="M_SMS_COMBO_AMBIENTE" selectedValue="<%=prgAmbiente%>"
					addBlank="true" blankValue="" /></td>
			</tr>
			<tr class="note">
				<td class="etichetta">Note</td>
				<td class="campo"><af:textArea name="TXTCONTATTOAPP" cols="60"
					rows="4" title="Note" readonly="true" classNameBase="input"
					value="<%=txtNoteApp%>" /></td>
			</tr>
			<tr>
				<td class="etichetta">Effetto Appuntamento</td>
				<td class="campo"><af:comboBox name="CODEFFETTOAPPUNT" size="1"
					title="Effetto appuntamento" multiple="false" required="false"
					focusOn="false" moduleName="COMBO_EFFETTO_APPUNTAMENTO"
					selectedValue="<%=CODEFFETTOAPPUNT%>" addBlank="true" blankValue=""
					classNameBase="input" disabled="true" /></td>
			</tr>
			<tr>
				<td class="etichetta">Esito appuntamento</td>
				<td class="campo"><af:comboBox name="CODESITOAPPUNT" size="1"
					title="Effetto appuntamento" multiple="false" required="false"
					focusOn="false" moduleName="M_SMS_COMBO_ESITO_APPUNTAMENTO"
					selectedValue="<%=CODESITOAPPUNT%>" addBlank="true" blankValue=""
					classNameBase="input" disabled="true" /></td>
			</tr>
			<tr>
				<td class="etichetta">Stato appuntamento</td>
				<td class="campo"><af:comboBox name="CODSTATOAPPUNTAMENTO" size="1"
					title="Stato appuntamento" multiple="false" required="true"
					focusOn="false" moduleName="COMBO_STATO_APPUNTAMENTO"
					selectedValue="<%=CODSTATOAPPUNTAMENTO%>" addBlank="true"
					blankValue="" classNameBase="input" disabled="true" /></td>
			</tr>
		</TBODY>
	</table>
<%out.print(htmlStreamBottom);%>
	
<% if (valConfigSms.equals("1")) { %>
<br>
<af:form name="frmInvioSmsPers" action="AdapterHTTP" method="POST">
	
	<input type="hidden" name="PAGE" value="AGENDA_SMS_INVIO_PAGE">
	<input type="hidden" name="INVIOMASSIVO" value="true">
	<input type="hidden" name="INVIOPERS" value="true">
	<input type="hidden" name="SELEZIONE" value="<%=smsKeyContatto%>">
	<input type="hidden" name="MAXLENGTHSMS" value="<%=maxLengthTestoSMS%>">
	
	<%out.print(htmlStreamTopModify);%>
	<p class="titolo">Invio SMS</p>
	<table class="main">
	<tbody>
		<tr>
				<td class="etichetta">Lavoratore</td>
				<td class="campo"><af:textBox name="LavoratoreSms" size="50"
					type="text" title="Lavoratore" readonly="true"
					classNameBase="input" value='<%=strCognome + " " +strNome%>' /></td>
		</tr>
		<tr>
				<td class="etichetta">Cellulare</td>
				<td class="campo"><af:textBox name="CellulareSms" size="50"
					type="text" title="Cellulare" readonly="true"
					classNameBase="input" value="<%=strCell%>" /></td>
		</tr>
		
		
		<tr>
				<td class="etichetta">Testo SMS</td>
				<td class="campo" valign="baseline">
					<af:textArea title="Testo SMS" required="true" classNameBase="textarea" name="TestoSms" readonly="false" 
					value="<%=testoSms%>" maxlength="<%=maxLengthTestoSMS%>"  rows="5" cols="60"/>
				</td>
			</tr>
		
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<input type="submit" class="pulsanti" value="Invia SMS">
			</td>	
		</tr>
	</tbody>
	</table>

<%out.print(htmlStreamBottomModify);%>
</af:form>
<% } %>
<br>
	
	<p class="titolo">Lavoratori</p>
	<af:list moduleName="LISTA_LAVORATORI_APPUNTAMENTO_SCAD" />

	<table align="center">
		<TBODY>
			<tr>
				<td align="center">
						<%
							String urlDiLista = (String) sessionContainer.getAttribute("_TOKEN_AGENDA_SMS_LISTALAVORATORI_PAGE");
							if (urlDiLista != null) {
								out.println("<div align=\"center\"><a href=\"#\" onClick=\"goTo('" + urlDiLista +"')\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></div>");
							}
						%>
				</td>					
			</tr>
		</TBODY>
	</table>

<div align="center"><%operatoreInfo.showHTML(out);%></div>
</body>
</html>
