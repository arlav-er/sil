<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, 
                java.math.*,
                it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%         
  	String _page = (String) serviceRequest.getAttribute("PAGE"); 
  	String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  	    
  	String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
  	String prgNominativo = (String) serviceRequest.getAttribute("prgNominativo");
  	
  	String p_page = (String) serviceRequest.getAttribute("OLD_PAGE");
  	String prgRosa = (String) serviceRequest.getAttribute("prgRosa");
  	String prgTipoRosa = (String) serviceRequest.getAttribute("prgTipoRosa");
  	String prgTipoIncrocio = (String) serviceRequest.getAttribute("prgTipoIncrocio");
  	String prgRichiestaAz = (String) serviceRequest.getAttribute("prgRichiestaAz");
  	String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");  	 	  	
  	String prgUnita = (String) serviceRequest.getAttribute("prgUnita");  	
  	String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
  	
  	String datAnzianita68 = "";
  	String numMesiAnz = "";
  	String descrTipoIscr = "";
  	String numPercInvalidita = "";
  	String numCaricoFam = "";
  	String numReddito = "";
  	String dataPunteggio = "";
  	
  	String datAnzianita68Pres = "";
  	String numMesiAnzPres = "";
  	String codCMTipoIscrPres = "";
  	String numPercInvaliditaPres = "";
  	String numCaricoFamPres = "";
  	String datCaricoFamPres = "";
  	String numRedditoPres = "";
  	String dataPunteggioPres = "";
  	String titoloPag = "";
  	String strNumCaricoPres = "";
  	
  	String flagPatente = "";
  	String codgradocapacitaloc = "";
  	String flagPatentePres = "";
  	String codgradocapacitalocPres = "";
  	
  	String flagDisocTi  = "";
  	String flagDisocTiPres  = "";
  	String descCmAnnota = "";
  	String codCmAnnotaPres = "";
  	
	if (!("3").equalsIgnoreCase(prgTipoRosa)) {  
 		titoloPag = "Confronto punteggio";
	}
	else {
		titoloPag = "Storicizzazione dati punteggio";
	}
  	
	String versioneGraduatoria = "1";
	SourceBean checkVersione = (SourceBean) serviceResponse.getAttribute("CHECK_VERSIONE_GRADUATORIA.ROWS.ROW");
	if (checkVersione != null) {
		versioneGraduatoria = checkVersione.getAttribute("CODMONOTIPOGRADCM") == null? "" : (String)checkVersione.getAttribute("CODMONOTIPOGRADCM");
	}
	
  	SourceBean punteggioSB = (SourceBean) serviceResponse.getAttribute("CMDettaglioPunteggioPresLavoratoreModule.ROWS.ROW");
	if (punteggioSB != null) {
		if (("3").equalsIgnoreCase(versioneGraduatoria)) {
			datAnzianita68 = punteggioSB.getAttribute("DATANZIANITA68") == null? "" : (String)punteggioSB.getAttribute("DATANZIANITA68");
		  	numMesiAnz = punteggioSB.getAttribute("NUMMESIANZ") == null? "" : ((BigDecimal)punteggioSB.getAttribute("NUMMESIANZ")).toString();
	  		descrTipoIscr = punteggioSB.getAttribute("DESCRTIPOISCR") == null? "" : (String)punteggioSB.getAttribute("DESCRTIPOISCR");	
	  		numPercInvalidita = punteggioSB.getAttribute("NUMPERCINVALIDITA") == null? "" : ((BigDecimal)punteggioSB.getAttribute("NUMPERCINVALIDITA")).toString();
	  		dataPunteggio = punteggioSB.getAttribute("DATAPUNTEGGIO") == null? "" : (String)punteggioSB.getAttribute("DATAPUNTEGGIO");
	  		flagDisocTi = punteggioSB.getAttribute("FLGDISOCTI") == null? "" : (String)punteggioSB.getAttribute("FLGDISOCTI");
	  		descCmAnnota = punteggioSB.getAttribute("CODCMANNOTA") == null? "" : ((String)punteggioSB.getAttribute("CODCMANNOTA")).toUpperCase();
		}
		else if (("4").equalsIgnoreCase(versioneGraduatoria)) {
			datAnzianita68 = punteggioSB.getAttribute("DATANZIANITA68") == null? "" : (String)punteggioSB.getAttribute("DATANZIANITA68");
		  	numMesiAnz = punteggioSB.getAttribute("NUMMESIANZ") == null? "" : ((BigDecimal)punteggioSB.getAttribute("NUMMESIANZ")).toString();
	  		descrTipoIscr = punteggioSB.getAttribute("DESCRTIPOISCR") == null? "" : (String)punteggioSB.getAttribute("DESCRTIPOISCR");	
	  		numPercInvalidita = punteggioSB.getAttribute("NUMPERCINVALIDITA") == null? "" : ((BigDecimal)punteggioSB.getAttribute("NUMPERCINVALIDITA")).toString();
	  		numReddito = punteggioSB.getAttribute("NUMREDDITO") == null? "" : ((BigDecimal)punteggioSB.getAttribute("NUMREDDITO")).toString();
	  		dataPunteggio = punteggioSB.getAttribute("DATAPUNTEGGIO") == null? "" : (String)punteggioSB.getAttribute("DATAPUNTEGGIO");	  		
		}
		else {
			datAnzianita68 = punteggioSB.getAttribute("DATANZIANITA68") == null? "" : (String)punteggioSB.getAttribute("DATANZIANITA68");
		  	numMesiAnz = punteggioSB.getAttribute("NUMMESIANZ") == null? "" : ((BigDecimal)punteggioSB.getAttribute("NUMMESIANZ")).toString();
		  	descrTipoIscr = punteggioSB.getAttribute("DESCRTIPOISCR") == null? "" : (String)punteggioSB.getAttribute("DESCRTIPOISCR");
		  	numPercInvalidita = punteggioSB.getAttribute("NUMPERCINVALIDITA") == null? "" : ((BigDecimal)punteggioSB.getAttribute("NUMPERCINVALIDITA")).toString();
		  	numCaricoFam = punteggioSB.getAttribute("CARICO") == null? "" : (String)punteggioSB.getAttribute("CARICO");
		  	numReddito = punteggioSB.getAttribute("NUMREDDITO") == null? "" : ((BigDecimal)punteggioSB.getAttribute("NUMREDDITO")).toString();
		  	dataPunteggio = punteggioSB.getAttribute("DATAPUNTEGGIO") == null? "" : (String)punteggioSB.getAttribute("DATAPUNTEGGIO");
		  	if (("2").equalsIgnoreCase(versioneGraduatoria)) {
		  		flagPatente = punteggioSB.getAttribute("FLGPATENTE") == null? "" : (String)punteggioSB.getAttribute("FLGPATENTE");
		  		codgradocapacitaloc = punteggioSB.getAttribute("CODGRADOCAPACITALOC") == null? "" : (String)punteggioSB.getAttribute("CODGRADOCAPACITALOC");
		  	}
		}
	}
  	
  	if (!("3").equalsIgnoreCase(prgTipoRosa)) {  	  	
	  	SourceBean punteggioPresSB = (SourceBean) serviceResponse.getAttribute("GETCMPUNTEGGIOPRESUNTOMODULE.ROW");
		if (punteggioPresSB != null) {
			datAnzianita68Pres = punteggioPresSB.getAttribute("datanzianita68Pres") == null? "" : (String)punteggioPresSB.getAttribute("datanzianita68Pres");
		  	numMesiAnzPres = punteggioPresSB.getAttribute("mesianzianitaPres") == null? "" : (String)punteggioPresSB.getAttribute("mesianzianitaPres");
		  	codCMTipoIscrPres = punteggioPresSB.getAttribute("codcmtipoiscrPres") == null? "" : (String)punteggioPresSB.getAttribute("codcmtipoiscrPres");
		  	numPercInvaliditaPres = punteggioPresSB.getAttribute("numpercinvaliditaPres") == null? "" : (String)punteggioPresSB.getAttribute("numpercinvaliditaPres");
		  	numCaricoFamPres = punteggioPresSB.getAttribute("numPersonePres") == null? "" : (String)punteggioPresSB.getAttribute("numPersonePres");
		  	datCaricoFamPres = punteggioPresSB.getAttribute("datdichcaricoPres") == null? "" : (String)punteggioPresSB.getAttribute("datdichcaricoPres");
		  	numRedditoPres = punteggioPresSB.getAttribute("numRedditoPres") == null? "" : (String)punteggioPresSB.getAttribute("numRedditoPres");
		  	flagDisocTiPres  = punteggioPresSB.getAttribute("flgDisocTiPres") == null? "" : (String)punteggioPresSB.getAttribute("flgDisocTiPres");
		  	codCmAnnotaPres = punteggioPresSB.getAttribute("codCmAnnotaPres") == null? "" : (String)punteggioPresSB.getAttribute("codCmAnnotaPres");
		  	
		  	if (!("").equals(numCaricoFamPres)) {
		  		strNumCaricoPres = numCaricoFamPres + " del " +datCaricoFamPres;
		  	}
		  	
		  	if (("2").equalsIgnoreCase(versioneGraduatoria)) {
		  		flagPatentePres = punteggioPresSB.getAttribute("FLGPATENTEPRES") == null? "" : (String)punteggioPresSB.getAttribute("FLGPATENTEPRES");
		  		codgradocapacitalocPres = punteggioPresSB.getAttribute("CODGRADOCAPACITALOCPRES") == null? "" : (String)punteggioPresSB.getAttribute("CODGRADOCAPACITALOCPRES");
		  	}
		}  	
	}
  	  	
  	String codCpiApp = (String) serviceRequest.getAttribute("codCpi");
	String ConcatenaCpi = (String) serviceRequest.getAttribute("ConcatenaCpi");
	
  	String message = (String) serviceRequest.getAttribute("MESSAGE");
  	String listPage = (String) serviceRequest.getAttribute("OLD_LIST_PAGE");
  	if (("").equals(listPage) || listPage == null) {  		
  		if (("LIST_FIRST").equalsIgnoreCase(message)) {
  			listPage = "1";
  		}
  		else if (("LIST_LAST").equalsIgnoreCase(message)) {
  			listPage = "-1";
  		}
  		else { 
	  		listPage = "1";
	  	}
  	}	  	
  	PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID),(String) serviceRequest.getAttribute("PAGE"));    
  	
%>


<script>  
	
	function indietro(){   
	    <%
	    if (("CMStoricoDettGraduatoriaPage").equalsIgnoreCase(p_page)) {
	    %>
			window.location="AdapterHTTP?PAGE=CMStoricoDettGraduatoriaPage&MODULE=CMStoricoCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&LIST_PAGE=<%=listPage%>"; 	
	    <%
  		}
  		else {
	    %>  
	 		window.location="AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage&MODULE=CMCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&MESSAGE=<%=message%>&LIST_PAGE=<%=listPage%>";
	 	<%
	 	}
	    %> 
	}

</script>

<html>
<head>
<title></title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
</head>

<body onload="rinfresca()">


<%     
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
%>
				
<p class="titolo"><%=titoloPag%></p>
   
<%out.print(htmlStreamTop);%>	
<%
if (!("3").equalsIgnoreCase(prgTipoRosa)) {  
%>
	<table cellpadding="3" cellspacing="3" width="500">
		<tr>
			<td>&nbsp;</td>
			<td class="campo_readFree" valign="top">Punteggio reale <br> (<%=dataPunteggio%>)</td>
			<td class="campo_readFree" nowrap valign="top">Punteggio presunto</td>					
		</tr>
		<tr>
			<td class="etichetta">Data Anzianità</td>
			<td class="campocentrato"><%=datAnzianita68%></td>
			<td class="campocentrato"><%=datAnzianita68Pres%></td>
		</tr>
		<tr>
			<td class="etichetta">Mesi anzianità alla data di pubblicazione</td>
			<td class="campocentrato"><%=numMesiAnz%></td>
			<td class="campocentrato"><%=numMesiAnzPres%></td>
		</tr>
		<tr>
			<td class="etichetta">Tipo Iscrizione</td>
			<td class="campocentrato"><%=descrTipoIscr%></td>
			<td class="campocentrato">
				<af:comboBox name="codCMTipoIscrD" moduleName="CM_GET_DE_TIPO_ISCR" 
    				selectedValue="<%=codCMTipoIscrPres%>" className="inputViewNoBold" title="Categoria iscrizione" 
    				addBlank="true" disabled="true" />
			</td>			
		</tr>
		<tr>
			<td class="etichetta">Percentuale invalidante/categoria</td>
			<td class="campocentrato"><%=numPercInvalidita%></td>
			<td class="campocentrato"><%=numPercInvaliditaPres%></td>
		</tr>
		<%
		if (("3").equalsIgnoreCase(versioneGraduatoria)) {
		%>
			<tr>
				<td class="etichetta">Lavoratore privo di occupazione a TI</td>
				<td class="campocentrato"><%=flagDisocTi%></td>
				<%
				if (("").equals(flagDisocTi)) {
				%>
					<td class="campocentrato"></td>
				<%
				}
				else {
				%>
					<td class="campocentrato"><%=(flagDisocTiPres=="S"?"SI":"NO")%></td>
				<%
				}
				%>
			</tr>
			
			<tr>
				<td class="etichetta">Annota fuori lista</td>
				<td class="campocentrato"><%=descCmAnnota%></td>
				<%
				if (("").equals(codCmAnnotaPres)) {
				%>
					<td class="campocentrato"></td>
				<%
				}
				else {
				%>
					<td class="campocentrato"><%=codCmAnnotaPres%></td>
				<%
				}
				%>
			</tr>
			
		<%
		}		
		else if (("4").equalsIgnoreCase(versioneGraduatoria)) {
		%>
			<tr>
				<td class="etichetta">Reddito ISEE</td>
				<td class="campocentrato"><%=numReddito%></td>
				<td class="campocentrato"><%=numRedditoPres%></td>
			</tr>		
		<%	
		}	
		else {
		%>
			<tr>
				<td class="etichetta">Carico familiare e data della dichiarazione</td>
				<td class="campocentrato"><%=numCaricoFam%></td>
				<%
				if (("").equals(strNumCaricoPres)) {
				%>
					<td class="campocentrato"></td>
				<%
				}
				else {
				%>
					<td class="campocentrato"><%=strNumCaricoPres%></td>
				<%
				}
				%>
			</tr>
			<tr>
				<td class="etichetta">Reddito CM</td>
				<td class="campocentrato"><%=numReddito%></td>
				<td class="campocentrato"><%=numRedditoPres%></td>
			</tr>
			<%
			if (("2").equalsIgnoreCase(versioneGraduatoria)) {
			%>
				<tr>
					<td class="etichetta">Patente</td>
					<td class="campocentrato"><%=flagPatente%></td>
					<%			
					if (("S").equalsIgnoreCase(flagPatentePres)) {
					%>
						<td class="campocentrato">Patente presente</td>
					<%
					}
					else {
					%>
					 	<td class="campocentrato">Patente assente</td>
					<% 
					}
					%>
				</tr>
				<tr>
					<td class="etichetta">Locomozione</td>
					<td class="campocentrato"><%=codgradocapacitaloc%></td>
					<%			
					if (("3").equalsIgnoreCase(codgradocapacitalocPres)) {
					%>
						<td class="campocentrato">Lieve</td>
					<%
					}
					if (("4").equalsIgnoreCase(codgradocapacitalocPres)) {
					%>
						<td class="campocentrato">Media</td>
					<%	
					}	
					if (("5").equalsIgnoreCase(codgradocapacitalocPres)) {
					%>
							<td class="campocentrato">Massima</td>
					<%	
					}	
					else {
					%>
					 	<td class="campocentrato"></td>
					<% 
					}
					%>
				</tr>
		<%
			}
		}
		%>
		
	</table>	
<%
}
else {
%>
	<table cellpadding="3" cellspacing="3" width="400">
		<tr>
			<td>&nbsp;</td>
			<td class="campo_readFree" valign="top">Punteggio reale <br> (<%=dataPunteggio%>)</td>			
		</tr>
		<tr>
			<td class="etichetta">Data Anzianità</td>
			<td class="campocentrato" ><%=datAnzianita68%></td>
		</tr>
		<tr>
			<td class="etichetta">Mesi anzianità alla data di pubblicazione</td>
			<td class="campocentrato"><%=numMesiAnz%></td>
		</tr>
		<tr>
			<td class="etichetta">Tipo Iscrizione</td>
			<td class="campocentrato"><%=descrTipoIscr%></td>			
		</tr>
		<tr>
			<td class="etichetta">Percentuale invalidante/categoria</td>
			<td class="campocentrato"><%=numPercInvalidita%></td>
		</tr>
		<%
		if (("3").equalsIgnoreCase(versioneGraduatoria)) {
		%>
			<tr>
				<td class="etichetta">Disoccupato non precario</td>
				<td class="campocentrato"><%=flagDisocTi%></td>			
			</tr>
			
			<tr>
				<td class="etichetta">Annota fuori lista</td>
				<td class="campocentrato"><%=descCmAnnota%></td>				
			</tr>
		<%
		}
		else {
		%>
			<tr>
				<td class="etichetta">Carico familiare e data della dichiarazione</td>
				<td class="campocentrato"><%=numCaricoFam%></td>			
			</tr>
			<tr>
				<td class="etichetta">Reddito CM</td>
				<td class="campocentrato"><%=numReddito%></td>
			</tr>
			<%
			if (("2").equalsIgnoreCase(versioneGraduatoria)) {
			%>
				<tr>
					<td class="etichetta">Patente</td>
					<td class="campocentrato"><%=flagPatente%></td>				
				</tr>
				<tr>
					<td class="etichetta">Locomozione</td>
					<td class="campocentrato"><%=codgradocapacitaloc%></td>				
				</tr>
		<%
			}
		}
		%>
	</table>
<%
}
%>
<%out.print(htmlStreamBottom);%>


</body>
</html>