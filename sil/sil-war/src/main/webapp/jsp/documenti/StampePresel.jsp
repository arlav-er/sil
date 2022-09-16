<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="com.engiweb.framework.base.*,
				java.util.*,
				it.eng.sil.security.*,
				it.eng.sil.util.*,
				it.eng.afExt.utils.*" %>
      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
	String codCPI = user.getCodRif();

	// Leggo dal modulo "M_StampeGetCPItit" lo stato di competenza del lavoratore
	SourceBean competenzaResponse = (SourceBean) serviceResponse.getAttribute("M_StampeGetCPItit.ROWS.ROW");
	boolean eStatoCompetente = SourceBeanUtils.getAttrBoolean(competenzaResponse, "E_STATO_COMPETENTE", false);
	boolean eOraCompetente   = SourceBeanUtils.getAttrBoolean(competenzaResponse, "E_ORA_COMPETENTE",   false);
	SourceBean rowConfigSSL = (SourceBean) serviceResponse.getAttribute("M_ConfigStampaSituazioneLavorativa.ROWS.ROW");
	String configSSL = it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
	if (rowConfigSSL != null){
		configSSL = rowConfigSSL.getAttribute("num").toString();
	}
	//se utente è nel patronato, la stampa sit.lav. va abilitata
	boolean isPatronato = false;
	if (("P").compareTo(user.getCodTipo())==0)
		isPatronato = true;
	
	
	
	// Layout grafico
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);

	// NOTE: Attributi della pagina (pulsanti e link) 
	String _page = (String) serviceRequest.getAttribute("PAGE");
	String cdnFunz = serviceRequest.getAttribute("CDNFUNZIONE")!=null?serviceRequest.getAttribute("CDNFUNZIONE").toString():"";
	PageAttribs attributi = new PageAttribs(user,_page);
	String queryStringBack = "page=" + _page + "&CDNLAVORATORE=" + cdnLavoratore + "&cdnFunzione=" + cdnFunz;
	
	boolean canPrintSap     = attributi.containsButton("SCHEDAANAGPRO");
	boolean canPrintSapMinisteriale     = attributi.containsButton("SCHEDAANAGPRO_M");
	boolean canPrintCVEuropeo     = attributi.containsButton("CURREU");
	boolean canPrintCVSintetico     = attributi.containsButton("CURRSIN");
	boolean canPrintSchedaAnagrafica     = attributi.containsButton("SCHEDAANAG");
	boolean canPrintDispo     = attributi.containsButton("CURRICULUM");
	boolean canPrintSituazioneLavorativa     = attributi.containsButton("SITLAVORATIVA");
	boolean canPrintCVSinteticoCM     = attributi.containsButton("STAMPA_CV_SINTETICO_CM");
	boolean canPrintSchedaDispCM      = attributi.containsButton("STAMPA_SCHEDA_DISP_CM");
	boolean canPrintSchedaTit      = attributi.containsButton("STAMPA_SCHEDA_TITOLI_MIN");
	boolean canPrintRicevuta = attributi.containsButton("RICEVUTA");
	boolean canPrintMesiSucc = attributi.containsButton("MESISUCC");
	boolean canPrintRichMob = attributi.containsButton("RICHMOB");
	boolean canPrintIndennitaMob = attributi.containsButton("INDENNMOB");
	boolean canPrintSostegnoReddito = attributi.containsButton("SOSTREDD");
	boolean canPrintSituazioneLavorativaPatronato = attributi.containsButton("SITUAZIONE_LAV_PATRONATO");
	boolean canPrintAllegatoU3 = attributi.containsButton("STAMPA_ALLEGATOU3");
	boolean canPrintU009 = attributi.containsButton("STAMPA_U009");
	boolean canPrintU010 = attributi.containsButton("STAMPA_U010");
	boolean canPrintU013 = attributi.containsButton("STAMPA_U013");
	
	String cdnLavoratoreEncrypt = EncryptDecryptUtils.encrypt(cdnLavoratore);

%>

<html>
<head>
<title>Stampe Presel</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<%-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) --%>
<% String queryString = null; %>
<%@ include file="_apriGestioneDoc.inc"%>
<%@ include file="parametriche/_apriGestioneDocParam.inc"%>
    
<script language="JavaScript">
    
	function impostaNomeReport(nome){
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		document.form1.REPORT.value = nome;
		doFormSubmit(document.form1);
	}

	function noCPItitolare() {
		alert("Non è possibile effettuare la stampa:\nil lavoratore non appartiene a questo CPI");
	}

	function noEStatoCompetente() {
		alert("Non è possibile effettuare la stampa:\nil lavoratore non è o non è mai stato competente");
	}

	function noEOraCompetente() {
		alert("Non è possibile effettuare la stampa:\nil lavoratore non è competente");
	}

</script>
</head>

<body class="gestione" onload="rinfresca()">
<br/>

<af:form name="form1" method="POST" action="AdapterHTTP">

<af:textBox type="hidden" name="PAGE" value="stampaReportPage" />
<af:textBox type="hidden" name="REPORT" value="(ND)" />
<af:textBox type="hidden" name="PROMPT0" value="<%=cdnLavoratore%>" />

<% String onClick; %>
<%= htmlStreamTop %>
<table class="main">
	<tr><td width="33%">&nbsp;</td><td width="34%">&nbsp;</td><td width="33%">&nbsp;</td></tr>
	<tr><td colspan="3"><p class="titolo">Gestione stampe</p></td></tr>
	<tr><td>&nbsp;</td></tr>
	
	<%if(canPrintSap){ %>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = eStatoCompetente
			? "apriGestioneDoc('RPT_ANAG_MAIN','&cdnLavoratore=" + cdnLavoratore + "&cdnLavoratoreEncrypt=" + cdnLavoratoreEncrypt + "','SAP')"  
			: "noEStatoCompetente()";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Visualizza la scheda relativa all'anagrafica professionale" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Scheda anagrafico-professionale</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintSapMinisteriale){ %>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = eStatoCompetente
			? "apriGestioneDoc('RPT_ANAG_MINISTERIALE','&cdnLavoratore=" + cdnLavoratore + "&cdnLavoratoreEncrypt=" + cdnLavoratoreEncrypt + "','SAPM')"  
			: "noEStatoCompetente()";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Visualizza la scheda relativa all'anagrafica professionale ministeriale" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Scheda anagrafico-professionale ministeriale</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintSchedaAnagrafica){ %>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = eStatoCompetente
			? "apriGestioneDoc('RPT_ANAGRAFICA','&cdnLavoratore=" + cdnLavoratore + "&codCPI=" + codCPI + "','SA')"
			: "noEStatoCompetente()";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Visualizza la scheda relativa all'anagrafica lavoratore" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Sola scheda anagrafica</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintCVSinteticoCM){%>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = "apriGestioneDoc('RPT_CURR_DISP','&cdnLavoratore=" + cdnLavoratore + "&mostraPerLavoratore=false','CUA')";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Visualizza la scheda relativa al Curriculum Vitae sintetico CM" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Curriculum sintetico - CM</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	<%if(canPrintSchedaDispCM){%>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = "apriGestioneDoc('RPT_CURR_DISP','&cdnLavoratore=" + cdnLavoratore + "&mostraPerLavoratore=true','CUL')";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Visualizza la scheda disponibilità CM" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Scheda disponibilit&agrave; - CM</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	<%-- --------------------------------------- --%>
	<%if(canPrintSchedaTit){%>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = "apriGestioneDoc('RPT_TIT_MIN','&cdnLavoratore=" + cdnLavoratore + "&mostraPerLavoratore=true','CUL')";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Visualizza la scheda Titoli di Studio" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Scheda Titoli di Studio</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintCVSintetico){%>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = "apriGestioneDoc('RPT_CURR_DISP','&cdnLavoratore=" + cdnLavoratore + "&mostraPerLavoratore=false&isCM=false','CUA')";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Visualizza la scheda relativa al Curriculum Vitae sintetico" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Curriculum sintetico</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintDispo){%>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = "apriGestioneDoc('RPT_CURR_DISP','&cdnLavoratore=" + cdnLavoratore + "&mostraPerLavoratore=true&isCM=false','CUL')";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Visualizza la scheda disponibilità" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Scheda disponibilit&agrave;</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	<%-- --------------------------------------- --%>
	
	<%if(canPrintCVEuropeo){%>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = "apriGestioneDoc('RPT_CURR_EUR','&cdnLavoratore=" + cdnLavoratore + "','CUE')";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Visualizza il Curriculum Europeo" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Curriculum europeo</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintSituazioneLavorativa){%>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		if (configSSL.equals(it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG)){
			onClick = eOraCompetente
				? "apriGestioneDoc('RPT_SITUAZIONE_LAV','&codCPI=" + codCPI + "&cdnLavoratore=" + cdnLavoratore + "','SSL')"
				: "noEOraCompetente()";
		}
		else {
			onClick = eOraCompetente
				? "apriGestioneProtocollazioneParam('&codCPI=" + codCPI + "&cdnLavoratore=" + cdnLavoratore + "&cdnFunzione=" + cdnFunz + "','SSL', 'STAMPEPRESELPAGE')"
				: "noEOraCompetente()";
			
		}
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Visualizza situazione lavorativa" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Situazione lavorativa</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if (canPrintSituazioneLavorativaPatronato){%>
	<!-- Rimossa la profilatura, non viene più chiamata -->
		<tr>
		 <td></td>
	  	 <td align="left">&nbsp;
		 <%
		 onClick = eOraCompetente||isPatronato
         ? "apriGestioneDoc('RPT_SITUAZIONE_LAV_PATRONATO','&codCPI=" + codCPI + "&cdnLavoratore=" + cdnLavoratore + "','SSLP')"
        		 : "noEOraCompetente()";
		 %>
		 <a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Visualizza situazione lavorativa (Patronato)" /></a>&nbsp;
		 <a href="#" onClick="<%= onClick %>">Situazione lavorativa (Patronato)</a>
	     </td>
	     <td></td>
		</tr>
	<% }%>	
	<%if(canPrintMesiSucc){ %>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = eStatoCompetente
			? "apriGestioneDoc('RPT_DICH_MESI_SUCC','&cdnLavoratore=" + cdnLavoratore + "&codCPI=" + codCPI + "','STLAVTNT')"
			: "noEStatoCompetente()";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Dichiarazione Mesi Successivi" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Dichiarazione Mesi Successivi</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintRichMob){ %>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = eStatoCompetente
			? "apriGestioneDoc('RPT_RICH_MOB','&cdnLavoratore=" + cdnLavoratore + "&codCPI=" + codCPI + "','STLAVTNT')"
			: "noEStatoCompetente()";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Richiesta Iscrizione Mobilità" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Richiesta Iscrizione Mobilità</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintIndennitaMob){ %>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = eStatoCompetente
			? "apriGestioneDoc('RPT_INDENNITA_MOB','&cdnLavoratore=" + cdnLavoratore + "&codCPI=" + codCPI + "','STLAVTNT')"
			: "noEStatoCompetente()";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Domanda Indennità regionale di Mobilità" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Domanda Indennità Mobilità</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintSostegnoReddito){ %>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = eStatoCompetente
			? "apriGestioneDoc('RPT_SOSTEGNO_REDDITO','&cdnLavoratore=" + cdnLavoratore + "&codCPI=" + codCPI + "','STLAVTNT')"
			: "noEStatoCompetente()";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Domanda di Sostegno al Reddito" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Domanda Sostegno Reddito</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintRicevuta){ %>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = eStatoCompetente
			? "apriGestioneDoc('RPT_RICEVUTA_LAVORATORE','&cdnLavoratore=" + cdnLavoratore + "&codCPI=" + codCPI + "','ST_TNT')"
			: "noEStatoCompetente()";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Ricevuta" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Ricevuta</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintAllegatoU3){ %>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = eStatoCompetente
			? "apriGestioneDoc('RPT_ALLEGATOU3','&cdnLavoratore=" + cdnLavoratore + "&codCPI=" + codCPI + "','U3')"
			: "noEStatoCompetente()";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Ricevuta" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">Allegato U3</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintU009){ %>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = eStatoCompetente
			? "apriGestioneDoc('RPT_U009','&cdnLavoratore=" + cdnLavoratore + "&codCPI=" + codCPI + "','U009')"
			: "noEStatoCompetente()";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Ricevuta" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">U009</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintU010){ %>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = eStatoCompetente
			? "apriGestioneDoc('RPT_U010','&cdnLavoratore=" + cdnLavoratore + "&codCPI=" + codCPI + "','U010')"
			: "noEStatoCompetente()";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Ricevuta" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">U010</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<%if(canPrintU013){ %>
	<tr>
	  <td></td>
	  <td align="left">&nbsp;
		<%
		onClick = eStatoCompetente
			? "apriGestioneDoc('RPT_U013','&cdnLavoratore=" + cdnLavoratore + "&codCPI=" + codCPI + "','U013')"
			: "noEStatoCompetente()";
		%>
		<a href="#" onClick="<%= onClick %>"><img name="dettImg" src="../../img/text.gif"
			alt="Ricevuta" /></a>&nbsp;
		<a href="#" onClick="<%= onClick %>">U013</a>
	  </td>
	  <td></td>
	</tr>
	<%}%>
	
	<tr><td colspan="3">&nbsp;</td></tr>
	<tr><td colspan="3">&nbsp;</td></tr>
</table>
<%= htmlStreamBottom %>

</af:form>

<af:form name="frmTemplate" method="POST" action="AdapterHTTP">
	<input type="hidden" name="PAGE" value="ElaboraStampaParametricaPage">
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
	<input type="hidden" name="CODCPI" value="<%=codCPI%>">
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunz%>">
	<input type="hidden" name="PRGTEMPLATESTAMPA" value="">
	<input type="hidden" name="GENERASTAMPA" value="">
	<input type="hidden" name="PROTOCOL" value="">
	<input type="hidden" name="HOST" value="">
	<input type="hidden" name="PORT" value="">
	<input type="hidden" name="TIPODOC" value="">
	<input type="hidden" name="NOMETEMPLATE" value="">
	<input type="hidden" name="PAGEBACK" value="<%=queryStringBack%>">
</af:form>

</body>
</html>
