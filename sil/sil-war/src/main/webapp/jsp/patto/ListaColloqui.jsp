<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page
	import="com.engiweb.framework.base.*,
                it.eng.sil.security.*,
                java.lang.*,
                java.text.*,
                java.util.*,
                it.eng.sil.module.voucher.Properties,
                it.eng.sil.util.*,
                java.math.BigDecimal"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%
	String cdnLavoratore = (String) serviceRequest
	.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user,
	_current_page);
	if ((cdnLavoratore != null) && !cdnLavoratore.equals("")) {
		filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	}
	PageAttribs attributi = new PageAttribs(user, _current_page);

	boolean canDelete = false;
	boolean canInsert = false;
	boolean canView = false;
	boolean canFormazione = false;
	boolean canAccreditamentoFormazione = false;
	boolean canRendicontazione = false;
	
    boolean canPromTirocini = false;
	boolean canCertTirocini = false;

	if ((cdnLavoratore != null) && !cdnLavoratore.equals("")) {
		canView = filter.canViewLavoratore();
	} else {
		canView = filter.canView();
	}

	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		boolean existsCanc = attributi.containsButton("cancella");
		canInsert = attributi.containsButton("inserisci");

		if (!existsCanc) {
	canDelete = false;
		} else {
	if ((cdnLavoratore != null) && !cdnLavoratore.equals("")) {
		canDelete = filter.canEditLavoratore();
	}
	if (canInsert) {
		canInsert = existsCanc;
	}
		}
	}
	
	canFormazione = attributi.containsButton("formazione");
	canRendicontazione = attributi.containsButton("INOLTRA_RENDICONTAZIONE");
	canPromTirocini = attributi.containsButton("promotiroc"); 
	canCertTirocini = attributi.containsButton("certtiroc"); 
	canAccreditamentoFormazione = attributi.containsButton("ACCREDITAMENTO");
	if (!user.getCodTipo().equalsIgnoreCase(Properties.SOGGETTO_ACCREDITATO)) {
		canAccreditamentoFormazione = false;	
	}
	
	String canCIG = (String) serviceResponse
	.getAttribute("M_Configurazione_CIG.ROWS.ROW.canCIG");
	String ricercaGenerale = (String) serviceRequest
	.getAttribute("ricerca_generale");
	ricercaGenerale = ricercaGenerale == null ? "" : ricercaGenerale;
	
	BigDecimal azioni = (BigDecimal) serviceResponse.getAttribute("M_Partecipante_GG_WS_DATIOBB.AZIONI");
	boolean canCallWS = false;
	if (azioni != null && new BigDecimal(0).compareTo(azioni) != 0) {
		canCallWS = true;
	}
	boolean canCallWSAccreditamento = false;
	BigDecimal nPattiArea1 = (BigDecimal) serviceResponse.getAttribute("M_Accreditamento_DATIOBB.ROWS.ROW.numPattiArea1");
	if (nPattiArea1 != null && new BigDecimal(0).compareTo(nPattiArea1) != 0) {
		BigDecimal nAzioniArea1 = (BigDecimal) serviceResponse.getAttribute("M_Accreditamento_AZIONI.ROWS.ROW.numPercorso");
		if (nAzioniArea1 != null && new BigDecimal(0).compareTo(nAzioniArea1) != 0) {
			canCallWSAccreditamento = true;	
		}
	}
%>

<%@page import="it.eng.sil.util.Utils"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
<script language="Javascript">
function consultaProm(sha) {
	var urlpage="AdapterHTTP?PAGE=ConsultaPromSiferPage&cpih=";
    urlpage+=sha;
    window.open (urlpage, 'PromSifer', 'titlebar=no,toolbar=no,location=no,status=no,menubar=no,height=400,width=800,top=200,left=250,scrollbars=NO,resizable=NO');
  }
function consultaCert(sha) {
	var urlpage="AdapterHTTP?PAGE=ConsultaCertSiferPage&cpih=";
    urlpage+=sha;
    window.open (urlpage, 'CertSifer', 'titlebar=no,toolbar=no,location=no,status=no,menubar=no,height=400,width=800,top=200,left=250,scrollbars=NO,resizable=NO');
  }
function consultaYG(cdn) {
    var urlpage="AdapterHTTP?PAGE=ConsultaStatoAdesioneYGPage&CDNLAVORATORE="+cdn;
    window.open (urlpage, 'AdesioneYG', 'titlebar=no,toolbar=no,location=no,status=no,menubar=no,height=400,width=800,top=200,left=250,scrollbars=NO,resizable=NO');
  }
<%if (cdnLavoratore != null)
				attributi.showHyperLinks(out, requestContainer,
						responseContainer, "cdnLavoratore=" + cdnLavoratore);%>
	function go(url, alertFlag) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit())
			return;

		var _url = "AdapterHTTP?" + url;
		if (alertFlag == 'TRUE') {
			if (confirm('Confermi operazione')) {
				setWindowLocation(_url);
			}
		} else {
			setWindowLocation(_url);
		}
	}
</script>
</head>

<body onload="rinfresca()">
<af:form name="MainForm">
	<br />
	<%
		String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
			String _page = (String) serviceRequest.getAttribute("PAGE");
			if (cdnLavoratore != null && ricercaGenerale.equals("")) {
		InfCorrentiLav _testata = new InfCorrentiLav(RequestContainer.getRequestContainer()
				.getSessionContainer(), cdnLavoratore, user);
		_testata.show(out);
		Linguette _linguetta = new Linguette(user, Integer.parseInt(cdnFunzione), _page, new BigDecimal(
				cdnLavoratore));
		if ((_linguetta != null) && (_linguetta.getSize() > 0)) {
			_linguetta.show(out);
		}
	%>
	<center>
		<font color="red"> <af:showErrors />
		</font> <font color="green"> <af:showMessages
				prefix="M_INSERT_COLLOQUIO" /> <af:showMessages
				prefix="M_SAVE_COLLOQUIO" /> <af:showMessages
				prefix="M_DELETE_COLLOQUIO" /> <af:showMessages
				prefix="M_PARTECIPANTE_GG_WS" /> <af:showMessages
				prefix="M_Accreditamento_Formazione_WS" />
                <af:showMessages prefix="M_Rendicontazione_WS" />		
</font>
	</center>
	<%
		if ((_linguetta != null) && (_linguetta.getSize() == 0)) {
	%>
	<p class="titolo">LISTA PROGRAMMI</p>
	<%
		}
			} else {
	%>
	<p class="titolo">LISTA PROGRAMMI</p>
	<%
		}
	%>

	<%
		String attr = null;
			String valore = null;
			String txtOut = "";
	%>
	<%
		attr = "COGNOME";
			valore = (String) serviceRequest.getAttribute(attr);
			if (valore != null && !valore.equals("")) {
		txtOut += "Cognome <strong>" + valore + "</strong>; ";
			}
	%>
	<%
		attr = "NOME";
			valore = (String) serviceRequest.getAttribute(attr);
			if (valore != null && !valore.equals("")) {
		txtOut += "Nome <strong>" + valore + "</strong>; ";
			}
	%>
	<%
		attr = "CF";
			valore = (String) serviceRequest.getAttribute(attr);
			if (valore != null && !valore.equals("")) {
		txtOut += "Codice fiscale <strong>" + valore + "</strong>; ";
			}
	%>
	<%
		attr = "progrAperti";
			valore = (String) serviceRequest.getAttribute(attr);
			if (valore != null && !valore.equals("") ) {
				if(valore.equals("S"))
					valore="Si";
				if(valore.equals("N"))
					valore="No";
		txtOut += "Programmi aperti <strong>" + valore + "</strong>; ";
			}
	%>
	<%
		attr = "descCodServizio_H";
			valore = (String) serviceRequest.getAttribute(attr);
			if (valore != null && !valore.equals("")) {
		txtOut += "Servizio <strong>" + valore + "</strong>; ";
			}
	%>
	<%
		attr = "dataInizio";
			valore = (String) serviceRequest.getAttribute(attr);
			if (valore != null && !valore.equals("")) {
		txtOut += "  Data da <strong>" + valore + "</strong>; ";
			}
	%>
	<%
		attr = "dataFine";
			valore = (String) serviceRequest.getAttribute(attr);
			if (valore != null && !valore.equals("")) {
		txtOut += "Data a <strong>" + valore + "</strong>; ";
			}
	%>
	<%
		if (canCIG.equals("true")) {
		if (serviceRequest.containsAttribute("conCig")) {
			txtOut += "Legato ad una iscrizione CIG: <strong>Sì</strong>; ";
		}
			}
	%>
	<%
		attr = "descCPI_H";
			valore = (String) serviceRequest.getAttribute(attr);
			if (valore != null && !valore.equals("")) {
		txtOut += " Cpi comp.:<strong>" + valore + "</strong>; ";
			}
	%>
	<%
		attr = "progCond";
			valore = (String) serviceRequest.getAttribute(attr);
			if (valore != null && valore.equals("on") ) {
				valore="Si";
				txtOut += "Sottoposti a condizionalità <strong>" + valore + "</strong>; ";
			}
	%>
	<%
		attr = "azioniNegativo";
			valore = (String) serviceRequest.getAttribute(attr);
			if (valore != null && valore.equals("on") ) {
				valore="Si";
				txtOut += "Attività con esiti negativi <strong>" + valore + "</strong>; ";
			}
	%>
	<%
		attr = "azioniCond";
			valore = (String) serviceRequest.getAttribute(attr);
			if (valore != null && valore.equals("on") ) {
				valore="Si";
				txtOut += "Con eventi di condizionalità <strong>" + valore + "</strong>; ";
			}
	%>	
	<p align="center">
		<%
			if (txtOut.length() > 0) {
				txtOut = "Filtri di ricerca:<br/> " + txtOut;
		%>
	
	<table cellpadding="2" cellspacing="10" border="0" width="100%">
		<tr>
			<td
				style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color: #dcdcdc">
				<%
					out.print(txtOut);
				%>
			</td>
		</tr>
	</table>
	<%
		}
	%>

	<af:list moduleName="M_LIST_COLLOQUI"
		configProviderClass="it.eng.sil.module.patto.DynamicEstrazioneColloquiConfig"
		canDelete="<%=canDelete ?\"1\" :\"0\"%>" />

		<%
		String shaDest = EncryptDecryptUtils.SHA1("codice_cpi:"+user.getCodRif());
		if ( (cdnLavoratore != null) && (!cdnLavoratore.equals("")) && (ricercaGenerale.equals("")) ) {
			if (canAccreditamentoFormazione) {%>
				<center>
				<input type="button" class="pulsanti<%=((canCallWSAccreditamento)?"":"Disabled")%>" name="accsifer"
					<%=((canCallWSAccreditamento)?"":"disabled=\"true\"")%> value="Invio dati" id="btnArea1WS"
					onclick="go('PAGE=ListaColloquiPage&cdnFunzione=<%=cdnFunzione%>&cdnLavoratore=<%=cdnLavoratore%>&MODULE=M_Accreditamento_Formazione_WS', 'FALSE')">
				</center>
				<br /> 
			<%}
			if (canRendicontazione) {%>
				<center>
				<input type="button" class="pulsanti<%=((canCallWS)?"":"Disabled")%>" name="sifer"
					<%=((canCallWS)?"":"disabled=\"true\"")%> value="Inoltra a Rendicontazione"
					onclick="go('PAGE=ListaColloquiPage&cdnFunzione=<%=cdnFunzione%>&cdnLavoratore=<%=cdnLavoratore%>&MODULE=M_Rendicontazione_WS', 'FALSE')">
				</center>
				<br /> 
			<% }
		}
		if (cdnLavoratore != null && canInsert && ricercaGenerale.equals("")) {
		// la richiesta proviene dal menu del lavoratore, quindi e' possibile inserire un nuovo colloquio per quel lavoratore
		%>
		<center>
		<% if (canFormazione) {%>
			<input type="button" class="pulsanti<%=((canCallWS)?"":"Disabled")%>" name="sifer"
				<%=((canCallWS)?"":"disabled=\"true\"")%> value="Inoltra a Formazione"
				onclick="go('PAGE=ListaColloquiPage&cdnFunzione=<%=cdnFunzione%>&cdnLavoratore=<%=cdnLavoratore%>&MODULE=M_Partecipante_GG_WS', 'FALSE')">
			<br /> 
		<% } %>
		
		<br />
		<!--input type="submit" class="pulsanti"  name = "inserisciNuovo" value="Inserisci nuovo Colloquio"-->
		<input type="button" class="pulsanti" name="torna"
			value="Ricerca programma"
			onclick="go('PAGE=RicercaColloquiLavoratorePage&cdnFunzione=<%=cdnFunzione%>&cdnLavoratore=<%=cdnLavoratore%>', 'FALSE')">
		<br /> 
		
		<% if (canPromTirocini) {%>
		<input type="button" class="pulsanti" name="torna"
            value="Consulta promotori di tirocini"
            onclick="consultaProm('<%=shaDest%>')">
        <% } %>   
        <% if (canCertTirocini) {%>
        <input type="button" class="pulsanti" name="torna"
            value="Consulta certificatori di tirocini"
            onclick="consultaCert('<%=shaDest%>')">
        <% } %>
        <br /> <input type="button" class="pulsanti"
			name="inserisciNuovo" value="Nuovo programma"
			onclick="go('PAGE=COLLOQUIOPAGE&cdnFunzione=<%=cdnFunzione%>&cdnLavoratore=<%=cdnLavoratore%>&inserisciNuovo=1&data_cod=&prgspi=', 'FALSE')">
	</center>
	<input type="hidden" name="CODCPI"
		value="<%=(String) serviceRequest.getAttribute("CODCPI")%>">
	<%
		} else {
	%>
	<center>
		<input type="button" class="pulsanti" name="torna"
			value="Nuova ricerca"
			onclick="go('PAGE=RicercaColloquiLavoratorePage&cdnFunzione=<%=cdnFunzione%>', 'FALSE')">
	</center>
	<%
		}
	%>
</af:form>
</body>
</html>
