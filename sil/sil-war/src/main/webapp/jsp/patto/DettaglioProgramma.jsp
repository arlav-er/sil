<%@page import="it.eng.afExt.utils.StringUtils"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="patto" prefix="pt" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.module.movimenti.constant.Properties,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,                   
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  java.lang.*,
                  java.text.*,it.eng.sil.security.PageAttribs"%>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavProgramma = null;
	String prgPattoLavoratore = null;
	String cdnLavoratore = (String )serviceRequest.getAttribute("CDNLAVORATORE");
	if (cdnLavoratore == null || cdnLavoratore.equals("")) {
		prgPattoLavoratore = Utils.notNull((String)serviceRequest.getAttribute("prgPattoLavoratore"));
		SourceBean infoPatto = (SourceBean)serviceResponse.getAttribute("NotePattoStoricizzato.ROWS.ROW");
		if (infoPatto != null) {
			cdnLavProgramma = (String)infoPatto.getAttribute("CDNLAVORATORE");
			cdnLavoratore = cdnLavProgramma;
		}
	}
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
 	PageAttribs pageAtts = new PageAttribs(user, _current_page);
	
	boolean canModify = false;
	
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String htmlStreamTop = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
    
    SourceBean rowRegione = (SourceBean) serviceResponse.getAttribute("M_GetCodRegione.ROWS.ROW");
	String regione = StringUtils.getAttributeStrNotNull(rowRegione, "codregione"); 
	String azione = "Azione";
	String misura = "Obiettivo";
	if(Properties.RER.equalsIgnoreCase(regione)) {	
		azione = "Attività";
		misura = "Prestazione";
	}
	
	String umbriaGestAz = Properties.DEFAULT_CONFIG;

  	if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
  		umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
  	}
  	if(umbriaGestAz.equalsIgnoreCase(Properties.CUSTOM_CONFIG)){
  		azione = "Misura";
		misura = "Servizio";
  	}
%>
<html>
<head>
<title>Azioni Programma</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/"/>

<SCRIPT language="JavaScript">
function goToProgrammi(){
	var url = "AdapterHTTP?PAGE=RiepilogoProgrammiPage";
	<%if (cdnLavProgramma == null) {%>
		url += "&cdnLavoratore=<%=cdnLavoratore%>";
	<%} else{%>
		url += "&prgPattoLavoratore=<%=prgPattoLavoratore%>";
	<%} %>
  	url += "&CDNFUNZIONE=<%=cdnFunzione%>";
   	setWindowLocation(url);
	
}
</SCRIPT>

</head>
<body class="gestione" onload="rinfresca()">
<font color="red">
   <af:showErrors/>
</font>
<font color="green">
</font>
<%
InfCorrentiLav _testata= new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
Linguette _linguetta = null;
if (cdnLavProgramma == null) {
	_linguetta = new Linguette(user, Integer.parseInt(cdnFunzione), _current_page, new BigDecimal(cdnLavoratore));
	_testata.setPaginaLista("ListaPattoLavPage");	
}
else {
	_linguetta = new Linguette(user, Integer.parseInt(cdnFunzione), _current_page, new BigDecimal(prgPattoLavoratore));
	_linguetta.setCodiceItem("prgPattoLavoratore");
	_testata.setPaginaLista("PattoInformazioniStorichePage");	
}
_testata.show(out);
_linguetta.show(out);

Vector data = serviceResponse.getAttributeAsVector("M_RiepilogoAzioniProgrammaPatto.ROWS.ROW");
%>
<br>
<center>
<%out.print(htmlStreamTop);%>
<table class="main">
<%
if (data != null && data.size() > 0) {
	SourceBean infoColloquio = (SourceBean) data.elementAt(0);
	String flgProgramma = infoColloquio.containsAttribute("flgprogramma") ? infoColloquio.getAttribute("flgprogramma").toString() : "";
	String codMonoProgramma = infoColloquio.containsAttribute("codmonoprogramma") ? infoColloquio.getAttribute("codmonoprogramma").toString() : "";
	String descServizioProgramma = infoColloquio.containsAttribute("descProgramma") ? infoColloquio.getAttribute("descProgramma").toString() : "";
	String dataServizioProgramma = infoColloquio.containsAttribute("dataProgramma") ? infoColloquio.getAttribute("dataProgramma").toString() : "";
	String dataFineProgramma = infoColloquio.containsAttribute("DATAFINEPROGRAMMA") ? infoColloquio.getAttribute("DATAFINEPROGRAMMA").toString() : "";
	if (codMonoProgramma != null && !codMonoProgramma.equals("")) {%>
		<tr>
    	<td class="etichetta">Programma:&nbsp;</td>
		<td class="campo">
			<af:textBox name="nomeProgramma" value="<%=descServizioProgramma%>" classNameBase="input" type="text"
              readonly="true" size="100"/>
		</td>
		</tr>
	<%	
	}
	else {%>
		<tr>
    	<td class="etichetta">Servizio:&nbsp;</td>
		<td class="campo">
			<af:textBox name="nomeServizio" value="<%=descServizioProgramma%>" classNameBase="input" type="text"
              readonly="true" size="100"/>
		</td>
		</tr>
	<%
	}
	BigDecimal prgAzioneRaggPrec = null;
    for(int i=0; i<data.size(); i++) {
		SourceBean row = (SourceBean) data.elementAt(i);
      	if (row != null) {
        	String prestazione = row.containsAttribute("prestazione") ? row.getAttribute("prestazione").toString() : "";
        	String descAzione = row.containsAttribute("descAzione") ? row.getAttribute("descAzione").toString() : "";
        	String descEsito = row.containsAttribute("esito") ? row.getAttribute("esito").toString() : "";
        	String esito = row.containsAttribute("codesito") ? row.getAttribute("codesito").toString() : "";
        	String dataStimata = row.containsAttribute("dataStimata") ? row.getAttribute("dataStimata").toString() : "";
        	String dataAvvio = row.containsAttribute("dataAvvio") ? row.getAttribute("dataAvvio").toString() : "";
        	String dataConclusione = row.containsAttribute("dataConclusione") ? row.getAttribute("dataConclusione").toString() : "";
        	BigDecimal prgAzioneRaggCurr = row.containsAttribute("prgazioniragg") ? (BigDecimal)row.getAttribute("prgazioniragg") : new BigDecimal("0");
        	String nomeCampoPrestazione = "misura_" + i;
        	String nomeCampoAzione = "azione_" + i;
        	String dataPrima = "dataPrima_" + i;
        	String labelData = "";
        	String valueData = "";
        	
        	String dataSeconda = "dataSeconda_" + i;
        	String labelDataSeconda = "";
        	String valueDataSeconda = "";
        	
        	if (esito.equalsIgnoreCase("FC")) {
        		labelData = "data avvio";
        		valueData = dataAvvio;
        		
        		labelDataSeconda = "data conclusione";
    			valueDataSeconda = dataConclusione;
        	}
        	else {
        		if (esito.equalsIgnoreCase("AVV")) {
        			labelData = "data avvio";
        			valueData = dataAvvio;
        			
        			labelDataSeconda = "data prevista conclusione";
        			valueDataSeconda = dataConclusione;
        		}
        		else {
        			labelData = "da svolgersi entro il";
        			valueData = dataStimata;
        		}
        	}
        	if (prgAzioneRaggPrec == null || !prgAzioneRaggPrec.equals(prgAzioneRaggCurr)) {
	        	%>
	        	<tr>
	        	<td colspan="2">&nbsp;</td>
	        	</tr>
	        	<tr>
	        	<td class="etichetta"><%=misura%></td>
				<td class="campo">
					<af:textBox name="<%=nomeCampoPrestazione%>" value="<%=prestazione%>" classNameBase="input" type="text"
	                  readonly="true" size="100"/>
				</td>
				</tr>
	        	<%
        	}
        	prgAzioneRaggPrec = prgAzioneRaggCurr;
        	%>
        	<tr>
	        <td class="etichetta"><%=azione%></td>
			<td class="campo">
				<af:textBox name="<%=nomeCampoAzione%>" value="<%=descAzione%>" classNameBase="input" type="text"
                  readonly="true" size="100"/>
			</td>
			</tr>
			<tr>
	        <td class="etichetta">Esito</td>
			<td class="campo">
				<af:textBox name="descEsito" value="<%=descEsito%>" classNameBase="input" type="text"
                  readonly="true" size="100"/>
			</td>
			</tr>
			<tr>
	        <td class="etichetta"><%=labelData%></td>
			<td class="campo">
				<af:textBox name="<%=dataPrima%>" value="<%=valueData%>" classNameBase="input" type="text"
                  readonly="true" size="100"/>
			</td>
			</tr>
        	<%
        	if (!valueDataSeconda.equals("")) {
        	%>
        		<tr>
		        <td class="etichetta"><%=labelDataSeconda%></td>
				<td class="campo">
					<af:textBox name="<%=dataSeconda%>" value="<%=valueDataSeconda%>" classNameBase="input" type="text"
	                  readonly="true" size="100"/>
				</td>
				</tr>
        	<%
        	}
      	}
   	}	
}



%>

</table>

<table class="main">

<tr>
<td colspan="2">
	<center>
		<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="goToProgrammi();" />
		</center>
</td>
</tr>

</table>

<%out.print(htmlStreamBottom);%>
</center>

<br>

</body>
</html>
