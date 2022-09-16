<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.batch.Constants,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String _page = (String) serviceRequest.getAttribute("PAGE");
String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
//Servono per gestire il layout grafico
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String txtParametriProgr = "";
Vector listaTipiBatch = serviceResponse.getAttributeAsVector("M_GetTipoBatchProgrammazione.ROWS.ROW");
Vector listaMotContatti = serviceResponse.getAttributeAsVector("M_GetMotivoContattoAG.ROWS.ROW");
Vector listaMotFineAttoDid = serviceResponse.getAttributeAsVector("M_MOTFINEATTO.ROWS.ROW");
Vector listaServizi = serviceResponse.getAttributeAsVector("M_GetServizioAppuntamentoAG.ROWS.ROW");
Vector listaStatoAppuntamento = serviceResponse.getAttributeAsVector("M_GetStatoAppuntamentoAG.ROWS.ROW");
Vector listaAzioni = serviceResponse.getAttributeAsVector("M_DEAZIONI.ROWS.ROW");
Vector listaEsitoAzioni = serviceResponse.getAttributeAsVector("M_DEESITO.ROWS.ROW");
String codtipobatch = Utils.notNull(serviceRequest.getAttribute("codtipobatch"));
String codMotivoContatto = Utils.notNull(serviceRequest.getAttribute("prgmotcontatto"));
String ggPrecAvviso = Utils.notNull(serviceRequest.getAttribute("numggprecavviso"));
String ggPeriodoDate = Utils.notNull(serviceRequest.getAttribute("numggperiododate"));
String codServizio = Utils.notNull(serviceRequest.getAttribute("codservizio"));
String codStatoApp = Utils.notNull(serviceRequest.getAttribute("codstatoappuntamento"));
String codMotivoFineAttoDid = Utils.notNull(serviceRequest.getAttribute("codmotivofineattodid"));
String flgCmIscr = Utils.notNull(serviceRequest.getAttribute("flgcmiscr"));
String codAzione = Utils.notNull(serviceRequest.getAttribute("prgazioni"));
String codEsito = Utils.notNull(serviceRequest.getAttribute("codesito"));
Vector listaCpi = serviceResponse.getAttributeAsVector("M_GetCpiPoloProvincialeAll.ROWS.ROW");
String codCpi = Utils.notNull(serviceRequest.getAttribute("codcpi"));
for (int j = 0; j < listaTipiBatch.size(); j++) {
	SourceBean rowBatch = (SourceBean) listaTipiBatch.get(j);
	String codTipoBatchCurr = Utils.notNull(rowBatch.getAttribute("CODICE"));
	if (codTipoBatchCurr.equalsIgnoreCase(codtipobatch)) {
		String descProgrammazione = Utils.notNull(rowBatch.getAttribute("DESCRIZIONE"));
		txtParametriProgr += "Tipo Programmazione <strong>" + descProgrammazione + "</strong>; ";
		j = listaTipiBatch.size();
	}
}
if (!codMotivoContatto.equals("")) {
	for (int j = 0; j < listaMotContatti.size(); j++) {
		SourceBean rowContatti = (SourceBean) listaMotContatti.get(j);
		String codMotivoContattoCurr = Utils.notNull(rowContatti.getAttribute("CODICE"));
		if (codMotivoContattoCurr.equalsIgnoreCase(codMotivoContatto)) {
			String descContatto = Utils.notNull(rowContatti.getAttribute("DESCRIZIONE"));
			txtParametriProgr += "Motivo contatto <strong>" + descContatto + "</strong>; ";
			j = listaMotContatti.size();
		}
	}	
}
if (!codMotivoFineAttoDid.equals("")) {
	for (int j = 0; j < listaMotFineAttoDid.size(); j++) {
		SourceBean rowMotivoFine = (SourceBean) listaMotFineAttoDid.get(j);
		String codMotivoFineCurr = Utils.notNull(rowMotivoFine.getAttribute("CODICE"));
		if (codMotivoFineCurr.equalsIgnoreCase(codMotivoFineAttoDid)) {
			String descMotivo = Utils.notNull(rowMotivoFine.getAttribute("DESCRIZIONE"));
			txtParametriProgr += "Motivo chiusura DID <strong>" + descMotivo + "</strong>; ";
			j = listaMotFineAttoDid.size();
		}
	}	
}
if (!flgCmIscr.equals("")) {
	if (flgCmIscr.equalsIgnoreCase("N")) {
		txtParametriProgr += "Iscritti CM <strong>No</strong>; ";
		
	}
	else {
		if (flgCmIscr.equalsIgnoreCase("S")) {
			txtParametriProgr += "Iscritti CM <strong>Sì</strong>; ";	
		}
	}
}
if (!ggPrecAvviso.equals("")) {
	txtParametriProgr += "Giorni ultimo contatto <strong>" + ggPrecAvviso + "</strong>; ";
}
if (!codServizio.equals("")) {
    String labelServizio = "Servizio";
    String umbriaGestAz = "0";
    if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
    	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
    }
    if(umbriaGestAz.equalsIgnoreCase("1")){
    	labelServizio = "Area";
    }
	
	for (int j = 0; j < listaServizi.size(); j++) {
		SourceBean rowServizi = (SourceBean) listaServizi.get(j);
		String codServizioCurr = Utils.notNull(rowServizi.getAttribute("CODICE"));
		if (codServizioCurr.equalsIgnoreCase(codServizio)) {
			String descServizio = Utils.notNull(rowServizi.getAttribute("DESCRIZIONE"));
			txtParametriProgr += labelServizio + " appuntamento <strong>" + descServizio + "</strong>; ";
			j = listaServizi.size();
		}
	}	
}
if (!codStatoApp.equals("")) {
	for (int j = 0; j < listaStatoAppuntamento.size(); j++) {
		SourceBean rowStato = (SourceBean) listaStatoAppuntamento.get(j);
		String codStatoAppCurr = Utils.notNull(rowStato.getAttribute("CODICE"));
		if (codStatoAppCurr.equalsIgnoreCase(codStatoApp)) {
			String descStatoApp = Utils.notNull(rowStato.getAttribute("DESCRIZIONE"));
			txtParametriProgr += "Stato appuntamento <strong>" + descStatoApp + "</strong>; ";
			j = listaStatoAppuntamento.size();
		}
	}	
}
if (!codAzione.equals("")) {
	for (int j = 0; j < listaAzioni.size(); j++) {
		SourceBean rowAzione = (SourceBean) listaAzioni.get(j);
		String codAzioneCurr = Utils.notNull(rowAzione.getAttribute("CODICE"));
		if (codAzioneCurr.equalsIgnoreCase(codAzione)) {
			String descAzione = Utils.notNull(rowAzione.getAttribute("DESCRIZIONE"));
			txtParametriProgr += "Azione <strong>" + descAzione + "</strong>; ";
			j = listaAzioni.size();
		}
	}	
}
if (!codEsito.equals("")) {
	for (int j = 0; j < listaEsitoAzioni.size(); j++) {
		SourceBean rowEsiti = (SourceBean) listaEsitoAzioni.get(j);
		String codEsitoCurr = Utils.notNull(rowEsiti.getAttribute("CODICE"));
		if (codEsitoCurr.equalsIgnoreCase(codEsito)) {
			String descEsito = Utils.notNull(rowEsiti.getAttribute("DESCRIZIONE"));
			txtParametriProgr += "Esito azione <strong>" + descEsito + "</strong>; ";
			j = listaEsitoAzioni.size();
		}
	}	
}
if (codtipobatch.equalsIgnoreCase(Constants.BATCH_APPUNTAMENTI)) {
	txtParametriProgr += "Giorni range appuntamento <strong>" + ggPeriodoDate + "</strong>; ";	
}
else {
	if (codtipobatch.equalsIgnoreCase(Constants.BATCH_AZIONE_PROGRAMMATE)) {
		txtParametriProgr += "Giorni range data stimata <strong>" + ggPeriodoDate + "</strong>; ";	
	}
	else {
		if (codtipobatch.equalsIgnoreCase(Constants.BATCH_PERDITA_DISOC)) {
			txtParametriProgr += "Giorni rilevazione <strong>" + ggPeriodoDate + "</strong>; ";	
		}
	}
}
if (!codCpi.equals("")) {
	for (int j = 0; j < listaCpi.size(); j++) {
		SourceBean rowCpi = (SourceBean) listaCpi.get(j);
		String codCpiCurr = Utils.notNull(rowCpi.getAttribute("CODICE"));
		if (codCpiCurr.equalsIgnoreCase(codCpi)) {
			String descCpi = Utils.notNull(rowCpi.getAttribute("DESCRIZIONE"));
			txtParametriProgr += "CPI di rif. <strong>" + descCpi + "</strong>; ";
			j = listaCpi.size();
		}
	}	
}
%>
<html>
<head>
   <title>Simulazione programmazione</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/"/>

<script language="JavaScript">

function tornaAllaLista() {
   var urlListaProgr="AdapterHTTP?PAGE=ListaProgrammazioniPage";
   urlListaProgr += "&CDNFUNZIONE=<%=_funzione%>";
   setWindowLocation(urlListaProgr);
}

</script>
</head>

<body class="gestione" onload="rinfresca();rinfresca_laterale();">
	<font color="red">
		<af:showErrors />
	</font>
	<font color="green">
		<af:showMessages prefix="M_GetSimulazioneProgrammazione" />
	</font>
	
	<%if(txtParametriProgr.length() > 0) {
		txtParametriProgr = "Parametri programmazione:<br/> " + txtParametriProgr;%>
        <table cellpadding="2" cellspacing="10" border="0" width="100%">
        <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
        <%out.print(txtParametriProgr);%>
        </td></tr>
       	</table>
   	<%}%>
	
	<af:list moduleName="M_GetSimulazioneProgrammazione" getBack="true"/>
	
	<center>
		<input class="pulsante" type = "button" name="btnTorna" value="Torna alla lista" onclick="tornaAllaLista()"/>
	</center>
	
</body>
</html>