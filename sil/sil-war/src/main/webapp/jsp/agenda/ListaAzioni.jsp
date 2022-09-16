<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ taglib uri="aftags" prefix="af"%>

<%@ page
	import="com.engiweb.framework.base.*,
	com.engiweb.framework.configuration.ConfigSingleton,
	com.engiweb.framework.error.EMFErrorHandler,
    it.eng.afExt.utils.DateUtils, 
    it.eng.sil.security.*, 
    it.eng.afExt.utils.*,
    it.eng.sil.util.*, 
    
    java.lang.*,
    java.text.*, java.math.*,  java.sql.*,   oracle.sql.*,  java.util.*"	
    extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE");
    ProfileDataFilter filter = new ProfileDataFilter(user, _page);
    PageAttribs attributi = new PageAttribs(user, _page);
    
    String h = (String) serviceRequest.getAttribute("CDNFUNZIONE");
    SourceBean serviziRows=(SourceBean) serviceResponse.getAttribute("MListaAzioni");
    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    
    boolean canNuovo=false;
    
    
    if (! filter.canView()){
  		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	} else {
  		
  		//canNuovo = attributi.containsButton("INSERISCI");
  	}

	String codAzione = "";
	String btnSalva = "Inserisci";
	String btnAnnulla = "";

	String txtOut = "";
	String comboObbiettivoMisuraYei = null;
	String comboTipoAttivita = null;
	String comboPrestazione = null;
	String azione = null;
	String flagMisuraYei = null;
	String flagPolAttiva  = null;
	String descObbiettivoMisuraYei = null;
	String descTipoAttivita  = null;
	String descPrestazione = null;
	
	
	comboObbiettivoMisuraYei = (String) serviceRequest.getAttribute("comboObbiettivoMisuraYei");
	comboTipoAttivita = (String) serviceRequest.getAttribute("comboTipoAttivita");
	comboPrestazione = (String) serviceRequest.getAttribute("comboPrestazione");
	azione  = (String) serviceRequest.getAttribute("azione");
	flagMisuraYei  = (String) serviceRequest.getAttribute("flagMisuraYei");
	flagPolAttiva = (String) serviceRequest.getAttribute("flagPolAttiva");
	descObbiettivoMisuraYei = (String) serviceRequest.getAttribute("descObbiettivoMisuraYei"); 
	descPrestazione  = (String) serviceRequest.getAttribute("descPrestazione");
	descTipoAttivita  = (String) serviceRequest.getAttribute("descTipoAttivita");


	if (descObbiettivoMisuraYei != null && !descObbiettivoMisuraYei.equals("")) {
		txtOut += "Obbiettivo <strong>" + descObbiettivoMisuraYei + "</strong>; ";
	}

	if (azione != null && !azione.equals("")) {
		txtOut += "Azione <strong>" + azione + "</strong>; ";
	}

	if (descTipoAttivita != null && !descTipoAttivita.equals("")) {
		txtOut += "Tipo attivit&agrave; <strong>" + descTipoAttivita + "</strong>; ";
	}

	if (descPrestazione != null && !descPrestazione.equals("")) {
		txtOut += "Prestazione <strong>" + descPrestazione + "</strong>; ";
	}

	if (flagMisuraYei != null && !flagMisuraYei.equals("")
			&& !flagMisuraYei.equalsIgnoreCase("N")) {
		txtOut += "Misura YEI <strong>Sì</strong>;";
	}
	
	if (flagPolAttiva != null && !flagPolAttiva.equals("")
			&& !flagPolAttiva.equalsIgnoreCase("N")) {
		txtOut += "Politica attiva <strong>Sì</strong>;";
	}
%>



<%
	/*NavigationCache sceltaUnitaAzienda = null;
	String[] fields = {"POLATTIVA", "PRESTAZIONE", "TIPOATTIVITA"};
	sceltaUnitaAzienda = new NavigationCache(fields);
	if (sessionContainer.getAttribute("SERVIZIOCACHE") != null) {
		sceltaUnitaAzienda = (NavigationCache) sessionContainer.getAttribute("SERVIZIOCACHE");
		tipoAttivita = sceltaUnitaAzienda.getField("TIPOATTIVITA").toString();
		prestazione = sceltaUnitaAzienda.getField("PRESTAZIONE").toString();
		polAttiva = sceltaUnitaAzienda.getField("POLATTIVA").toString();

	} else {
		sceltaUnitaAzienda.enable();
		sceltaUnitaAzienda.setField("TIPOATTIVITA", tipoAttivita);
		sceltaUnitaAzienda.setField("PRESTAZIONE", prestazione);
		sceltaUnitaAzienda.setField("POLATTIVA", polAttiva);
		if (polAttiva == null)
			sceltaUnitaAzienda.setField("POLATTIVA", "");
		if (tipoAttivita == null)
			sceltaUnitaAzienda.setField("TIPOATTIVITA", "");
		if (prestazione == null)
			sceltaUnitaAzienda.setField("PRESTAZIONE", "");
		sessionContainer.setAttribute("SERVIZIOCACHE",
				sceltaUnitaAzienda);

	}*/
%>




<html>
<head>
<title>Lista Azioni</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
<script type="text/javascript">
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

	function goConfirm(codAzione, funzione, alertFlag) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit())
			return;
		var s = "AdapterHTTP"
		if (alertFlag == 'CANCELLA') {
			if (confirm('Confermi Operazione')) {
				s += "?PAGE=GestAzioniPage";
				s += "&MODULE=MDeleteAzione";
				s += "&COMBOOBBIETTIVOMISURAYEI=" + comboObbiettivoMisuraYei;
				s += "&COMBOTIPOATTIVITA=" + comboTipoAttivita;
				s += "&COMBOPRESTAZIONE=" + comboPrestazione;
				s += "&AZIONE=" + azione;
				s += "&FLAGMISURAYEI=" + flagMisuraYei;
				s += "&FLAGPOLATTIVA=" + flagPolAttiva
				setWindowLocation(s);
			}
		} else {
			s += "?PAGE=DettaglioAzioniPage";
			s += "&COMBOOBBIETTIVOMISURAYEI=" + comboObbiettivoMisuraYei;
			s += "&COMBOTIPOATTIVITA=" + comboTipoAttivita;
			s += "&COMBOPRESTAZIONE=" + comboPrestazione;
			s += "&AZIONE=" + azione;
			s += "&FLAGMISURAYEI=" + flagMisuraYei;
			s += "&FLAGPOLATTIVA=" + flagPolAttiva
			s += "&CDNFUNZIONE=" + funzione;
			setWindowLocation(s);
		}
	}
</script>
</head>
<body class="gestione">

	<font color="red"> <af:showErrors />
	</font>
	<font color="green"> <af:showMessages prefix="MDeleteAzione" />
		<af:showMessages prefix="MSalvaAzione" /> <af:showMessages
			prefix="MAggiornaAzione" />
	</font>

	<p align="center">
		<%
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
	<%%>

	<af:list moduleName="MListaAzioni" />
	<center>
		<af:form method="POST" action="AdapterHTTP" dontValidate="true">
			<input type="hidden" name="PAGE" value="InsAzioniPage" />
			<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>" />

			<input type="hidden" name="COMBOOBBIETTIVOMISURAYEI" value="<%=comboObbiettivoMisuraYei%>" />
			<input type="hidden" name="COMBOTIPOATTIVITA" value="<%=comboTipoAttivita%>" />
			<input type="hidden" name="COMBOPRESTAZIONE" value="<%=comboPrestazione%>" />
			<input type="hidden" name="AZIONE" value="<%=azione%>" />
			<input type="hidden" name="FLAGMISURAYEI" value="<%=flagMisuraYei == null ? "" : flagMisuraYei%>" />
			<input type="hidden" name="FLAGPOLATTIVA" value="<%=flagPolAttiva == null ? "" : flagPolAttiva%>" />
			<input type="hidden" name="DESCOBBIETTIVOMISURAYEI" value="<%=descObbiettivoMisuraYei%>" /> 
			<input type="hidden" name="DESCTIPOATTIVITA" value="<%=descTipoAttivita%>" />
			<input type="hidden" name="DESCPRESTAZIONE" value="<%=descPrestazione%>" />
						
			<!--  <input class="pulsanti" type="submit" name="inserisci" 	value="Nuovo Azione" /> -->
		<% if(canNuovo) {%>
          <input class="pulsante" type = "submit" name="inserisci" value="Nuova Azione" onClick=""/>
		<%}%>
		
			<input type="button" class="pulsanti" name="torna"	value="Torna alla ricerca"
				onclick="go('PAGE=RicercaAzioniPage&cdnFunzione=<%=_funzione%>&comboObbiettivoMisuraYei=<%=comboObbiettivoMisuraYei%>&comboTipoAttivita=<%=comboTipoAttivita%>&comboPrestazione=<%=comboPrestazione%>&azione=<%=azione%>&flagMisuraYei=<%=flagMisuraYei%>&flagPolAttiva=<%=flagPolAttiva%>')">

		</af:form>
	</center>
</body>
</html>