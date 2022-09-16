<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,com.engiweb.framework.security.*,java.lang.*,java.text.*,java.util.*,it.eng.afExt.utils.*,java.math.*,it.eng.sil.util.*,it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE");
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);

	String cdnLavoratore = (String) serviceRequest
			.getAttribute("cdnLavoratore");
	/*String prgNominativo = (String) serviceRequest
			.getAttribute("prgNominativo");*/

	/*	String p_page = (String) serviceRequest.getAttribute("OLD_PAGE");
		String prgRosa = (String) serviceRequest.getAttribute("prgRosa");
		String prgTipoRosa = (String) serviceRequest.getAttribute("prgTipoRosa");
		String prgTipoIncrocio = (String) serviceRequest.getAttribute("prgTipoIncrocio");
		String prgRichiestaAz = (String) serviceRequest.getAttribute("prgRichiestaAz");
		String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");  	 	  	
		String prgUnita = (String) serviceRequest.getAttribute("prgUnita");  	*/
	String cdnFunzione = (String) serviceRequest
			.getAttribute("cdnFunzione");

	String datAnzianita68 = "";
	String numMesiAnz = "";
	String descrTipoIscr = "";
	String numPercInvalidita = "";
	String numCaricoFam = "";
	String numReddito = "";
	String dataPunteggio = "";
	
	String tipoincrocio= "";

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

	String flagDisocTi = "";
	String flagDisocTiPres = "";
	String descCmAnnota = "";
	String codCmAnnotaPres = "";

	String persone_carico = "";
	
	String punt_iniziale = "";
	String punt_carico_fam = "";
	String punt_reddito = "";
	String punt_anzianita = "";
	String punt_invalidita = "";
	String punt_locomozione = "";
	String punt_patente = "";
	String punt_totale = "";
	
	String codiceRit = "";
	
	titoloPag = "Punteggio Presunto";
	
	
	String versioneGraduatoria = serviceResponse
	.containsAttribute("M_GetConfigGraduatoria_cm.ROWS.ROW.codmonotipogradcm") ? serviceResponse
	.getAttribute(
			"M_GetConfigGraduatoria_cm.ROWS.ROW.codmonotipogradcm")
	.toString()
	: "0";



	
	SourceBean punteggioPresSB = (SourceBean) serviceResponse
			.getAttribute("M_CMIscrizioniLavCalcolaPunteggioPresunto.ROW");
	
/*	if (punteggioSB != null) {
		if (("3").equalsIgnoreCase(versioneGraduatoria)) {
			datAnzianita68 = punteggioSB.getAttribute("DATANZIANITA68") == null ? ""
					: (String) punteggioSB
							.getAttribute("DATANZIANITA68");
			numMesiAnz = punteggioSB.getAttribute("NUMMESIANZ") == null ? ""
					: ((BigDecimal) punteggioSB
							.getAttribute("NUMMESIANZ")).toString();
			descrTipoIscr = punteggioSB.getAttribute("DESCRTIPOISCR") == null ? ""
					: (String) punteggioSB
							.getAttribute("DESCRTIPOISCR");
			numPercInvalidita = punteggioSB
					.getAttribute("NUMPERCINVALIDITA") == null ? ""
					: ((BigDecimal) punteggioSB
							.getAttribute("NUMPERCINVALIDITA"))
							.toString();
			dataPunteggio = punteggioSB.getAttribute("DATAPUNTEGGIO") == null ? ""
					: (String) punteggioSB
							.getAttribute("DATAPUNTEGGIO");
			flagDisocTi = punteggioSB.getAttribute("FLGDISOCTI") == null ? ""
					: (String) punteggioSB.getAttribute("FLGDISOCTI");
			descCmAnnota = punteggioSB.getAttribute("CODCMANNOTA") == null ? ""
					: ((String) punteggioSB.getAttribute("CODCMANNOTA"))
							.toUpperCase();
		} else {
			datAnzianita68 = punteggioSB.getAttribute("DATANZIANITA68") == null ? ""
					: (String) punteggioSB
							.getAttribute("DATANZIANITA68");
			numMesiAnz = punteggioSB.getAttribute("NUMMESIANZ") == null ? ""
					: ((BigDecimal) punteggioSB
							.getAttribute("NUMMESIANZ")).toString();
			descrTipoIscr = punteggioSB.getAttribute("DESCRTIPOISCR") == null ? ""
					: (String) punteggioSB
							.getAttribute("DESCRTIPOISCR");
			numPercInvalidita = punteggioSB
					.getAttribute("NUMPERCINVALIDITA") == null ? ""
					: ((BigDecimal) punteggioSB
							.getAttribute("NUMPERCINVALIDITA"))
							.toString();
			numCaricoFam = punteggioSB.getAttribute("CARICO") == null ? ""
					: (String) punteggioSB.getAttribute("CARICO");
			numReddito = punteggioSB.getAttribute("NUMREDDITO") == null ? ""
					: ((BigDecimal) punteggioSB
							.getAttribute("NUMREDDITO")).toString();
			dataPunteggio = punteggioSB.getAttribute("DATAPUNTEGGIO") == null ? ""
					: (String) punteggioSB
							.getAttribute("DATAPUNTEGGIO");
			if (("2").equalsIgnoreCase(versioneGraduatoria)) {
				flagPatente = punteggioSB.getAttribute("FLGPATENTE") == null ? ""
						: (String) punteggioSB
								.getAttribute("FLGPATENTE");
				codgradocapacitaloc = punteggioSB
						.getAttribute("CODGRADOCAPACITALOC") == null ? ""
						: (String) punteggioSB
								.getAttribute("CODGRADOCAPACITALOC");
			}
		}
	}*/


	if (punteggioPresSB != null) {
		datAnzianita68Pres = punteggioPresSB
				.getAttribute("datanzianita68Pres") == null ? ""
				: (String) punteggioPresSB
						.getAttribute("datanzianita68Pres");
		numMesiAnzPres = punteggioPresSB
				.getAttribute("mesianzianitaPres") == null ? ""
				: (String) punteggioPresSB
						.getAttribute("mesianzianitaPres");
		codCMTipoIscrPres = punteggioPresSB
				.getAttribute("codcmtipoiscrPres") == null ? ""
				: (String) punteggioPresSB
						.getAttribute("codcmtipoiscrPres");
		numPercInvaliditaPres = punteggioPresSB
				.getAttribute("numpercinvaliditaPres") == null ? ""
				: (String) punteggioPresSB
						.getAttribute("numpercinvaliditaPres");
		numCaricoFamPres = punteggioPresSB
				.getAttribute("numPersonePres") == null ? ""
				: (String) punteggioPresSB
						.getAttribute("numPersonePres");
		datCaricoFamPres = punteggioPresSB
				.getAttribute("datdichcaricoPres") == null ? ""
				: (String) punteggioPresSB
						.getAttribute("datdichcaricoPres");
		numRedditoPres = punteggioPresSB.getAttribute("numRedditoPres") == null ? ""
				: (String) punteggioPresSB
						.getAttribute("numRedditoPres");
		flagDisocTiPres = punteggioPresSB
				.getAttribute("flgDisocTiPres") == null ? ""
				: (String) punteggioPresSB
						.getAttribute("flgDisocTiPres");
		codCmAnnotaPres = punteggioPresSB
				.getAttribute("codCmAnnotaPres") == null ? ""
				: (String) punteggioPresSB
						.getAttribute("codCmAnnotaPres");
		
		persone_carico = punteggioPresSB
		.getAttribute("persone_carico") == null ? ""
		: (String) punteggioPresSB
				.getAttribute("persone_carico");
		

		if (!("").equals(numCaricoFamPres)) {
			strNumCaricoPres = numCaricoFamPres + " del "
					+ datCaricoFamPres;
		}

		if (("2").equalsIgnoreCase(versioneGraduatoria)) {
			flagPatentePres = punteggioPresSB
					.getAttribute("FLGPATENTE") == null ? ""
					: (String) punteggioPresSB
							.getAttribute("FLGPATENTE");
			codgradocapacitalocPres = punteggioPresSB
					.getAttribute("CODGRADOCAPACITALOC") == null ? ""
					: (String) punteggioPresSB
							.getAttribute("CODGRADOCAPACITALOC");
		}
		
		//punteggio		
		
		tipoincrocio = punteggioPresSB
		.getAttribute("tipoincrocio") == null ? ""
				: (String) punteggioPresSB
						.getAttribute("tipoincrocio");
		
		
		if("D".equalsIgnoreCase(tipoincrocio)){
			tipoincrocio="Disabili";
		}else {
			if("A".equalsIgnoreCase(tipoincrocio)){
				tipoincrocio="Altre categorie protette";
			}				
		}	
			
		punt_iniziale = punteggioPresSB
		.getAttribute("punt_iniziale") == null ? ""
		: (String) punteggioPresSB
				.getAttribute("punt_iniziale");
		
		punt_carico_fam = punteggioPresSB
		.getAttribute("punt_carico_fam") == null ? ""
		: (String) punteggioPresSB
				.getAttribute("punt_carico_fam");
		
		
		punt_reddito = punteggioPresSB
		.getAttribute("punt_reddito") == null ? ""
		: (String) punteggioPresSB
				.getAttribute("punt_reddito");
		
		
		
		punt_anzianita = punteggioPresSB
		.getAttribute("punt_anzianita") == null ? ""
		: (String) punteggioPresSB
				.getAttribute("punt_anzianita");
		
		
		
		punt_invalidita = punteggioPresSB
		.getAttribute("punt_invalidita") == null ? ""
		: (String) punteggioPresSB
				.getAttribute("punt_invalidita");
		
		
		punt_locomozione = punteggioPresSB
		.getAttribute("punt_locomozione") == null ? ""
		: (String) punteggioPresSB
				.getAttribute("punt_locomozione");
		
		
		punt_patente = punteggioPresSB
		.getAttribute("punt_patente") == null ? ""
		: (String) punteggioPresSB
				.getAttribute("punt_patente");
		
		
		punt_totale = punteggioPresSB
		.getAttribute("punt_totale") == null ? ""
		: (String) punteggioPresSB
				.getAttribute("punt_totale");
		
		codiceRit = punteggioPresSB
		.getAttribute("codiceRit") == null ? ""
		: (String) punteggioPresSB
				.getAttribute("codiceRit");
		
		
		
		
		
	}

	String codCpiApp = (String) serviceRequest.getAttribute("codCpi");
	String ConcatenaCpi = (String) serviceRequest
			.getAttribute("ConcatenaCpi");

	String message = (String) serviceRequest.getAttribute("MESSAGE");
	String listPage = (String) serviceRequest
			.getAttribute("OLD_LIST_PAGE");
	if (("").equals(listPage) || listPage == null) {
		if (("LIST_FIRST").equalsIgnoreCase(message)) {
			listPage = "1";
		} else if (("LIST_LAST").equalsIgnoreCase(message)) {
			listPage = "-1";
		} else {
			listPage = "1";
		}
	}
	PageAttribs pageAtts = new PageAttribs((User) sessionContainer
			.getAttribute(User.USERID), (String) serviceRequest
			.getAttribute("PAGE"));
%>


<html>
<head>
<title>Punteggio presunto</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
</head>

<body class="gestione">


<%
	//InfCorrentiLav _testata = new InfCorrentiLav(RequestContainer
	//		.getRequestContainer().getSessionContainer(),
	//		cdnLavoratore, user);
	//_testata.setSkipLista(true);
	//_testata.show(out);
%>
				


   <br><center><p class="titolo">Punteggio presunto</p></center>
<%
   	out.print(htmlStreamTop);
   %>	

	<!-- <table cellpadding="3" cellspacing="3" width="500"> -->
	
	<%
			if (("-1").equalsIgnoreCase(codiceRit)) {
		%>
	
		<table>
			<tr>	
			    		
				<center><td class="campo_readFree" valign="top"> Non è stato possibile calcolare il Punteggio Presunto. </td></center>
									
			</tr>
		</table>
		<center><input type="button" class="pulsanti" value="Chiudi" onclick="window.close()"></center>
	<% } else { %>	
	 <table>
	 			
	 	
	 	
	 			
	    <td>
	 	<table>
		<tr>
					
			<td colspan=2 class="campo_readFree" valign="top">Dati del Lavoratore</td>					
		</tr>
		
		<tr>
    		<td class="etichetta">Tipo</td>			
			<td class="campo"><%=tipoincrocio%></td>		
		</tr>
		<tr>
			<td class="etichetta">Data Anzianità</td>			
			<td class="campo"><%=datAnzianita68Pres%></td>
		</tr>
	
		<tr>
			<td class="etichetta">Num Persone carico</td>			
			<td class="campo"><%=persone_carico%></td>			
		</tr>
	      
	
	
	
		
		<tr>
			<td class="etichetta">Percentuale invalidante/categoria</td>			
			<td class="campo"><%=numPercInvaliditaPres%></td>
		</tr>
		<%
			if (("3").equalsIgnoreCase(versioneGraduatoria)) {
		%>
			<tr>
				<td class="etichetta">Lavoratore privo di occupazione a TI</td>
				<td class="campo"><%=flagDisocTi%></td>
				<%
					if (("").equals(flagDisocTi)) {
				%>
					<td class="campo"></td>
				<%
					} else {
				%>
					<td class="campo"><%=(flagDisocTiPres == "S" ? "SI" : "NO")%></td>
				<%
					}
				%>
			</tr>
			
			<tr>
				<td class="etichetta">Annota fuori lista</td>
				<td class="campo"><%=descCmAnnota%></td>
				<%
					if (("").equals(codCmAnnotaPres)) {
				%>
					<td class="campo"></td>
				<%
					} else {
				%>
					<td class="campo"><%=codCmAnnotaPres%></td>
				<%
					}
				%>
			</tr>
			
		<%
						} else {
					%>
			
			
			
			
			<tr>
				<td class="etichetta">Reddito CM</td>
			
				<td class="campo"><%=numRedditoPres%></td>
			</tr>
			<%
				if (("2").equalsIgnoreCase(versioneGraduatoria)) {
			%>
				<tr>
					<td class="etichetta">Patente</td>
					
					<%
						if (("S").equalsIgnoreCase(flagPatentePres)) {
					%>
						<td class="campo">Patente presente</td>
					<%
						} else {
					%>
					 	<td class="campo">Patente assente</td>
					<%
						}
					%>
				</tr>
				<tr>
					<td class="etichetta">Locomozione</td>				
					<%
						if (("3").equalsIgnoreCase(codgradocapacitalocPres)) {
					%>
						<td class="campo">Lieve</td>
					<%
						}
									if (("4").equalsIgnoreCase(codgradocapacitalocPres)) {
					%>
						<td class="campo">Media</td>
					<%
						}
									if (("5").equalsIgnoreCase(codgradocapacitalocPres)) {
					%>
							<td class="campo">Massima</td>
					<%
						} else {
					%>
					 	<td class="campo"></td>
					<%
						}
					%>
				</tr>
		<%
			}
				}
		%>
		
		
		<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
	
		<tr>
			<td>&nbsp;</td>		
			<td>&nbsp;</td>					
		</tr>
	
		
		
		</table>
		  </td>
		
	  
		
	    <td>
    	<table>
		<tr>
					
			<td colspan=2 class="campo_readFree" valign="top">Dettaglio del Punteggio</td>					
		</tr>

		<tr>
			<td class="etichetta">Iniziale</td>			
			<td class="campo"><%=punt_iniziale%></td>
		</tr>
		
		<tr>
			<td class="etichetta">Anzianita</td>			
			<td class="campo"><%=punt_anzianita%></td>
		</tr>


			
	 
	 	<tr>
			<td class="etichetta">Carico Familiare</td>			
			<td class="campo"><%=punt_carico_fam%></td>
		</tr>	
	
	
		<tr>
			<td class="etichetta">Invalidita</td>			
			<td class="campo"><%=punt_invalidita%></td>
		</tr>	
	
	
		<tr>
			<td class="etichetta">Reddito</td>			
			<td class="campo"><%=punt_reddito%></td>
		</tr>	
	 
	 		
	 
	 
	 
	 		
	 	<tr>
			<td class="etichetta">Patente</td>			
			<td class="campo"><%=punt_patente%></td>
		</tr>	
		
		<tr>
			<td class="etichetta">Locomozione</td>			
			<td class="campo"><%=punt_locomozione%></td>
		</tr>
	 	<tr>
			<td class="etichetta"><b>Punteggio Totale</b></td>			
			<td class="campo"><b><%=punt_totale%></b></td>
		</tr>
		

		</table>
	 </td>
		
	 
	</table>
	<center><input type="button" class="pulsanti" value="Chiudi" onclick="window.close()"></center>
	<% } %>	
	
<%
	out.print(htmlStreamBottom);
%>


</body>
</html>