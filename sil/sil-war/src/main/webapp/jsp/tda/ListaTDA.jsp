<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  //NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean attivaTitolo = false;
  boolean totaliVCH = false;
  
  if (!filter.canView()) {
  	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	return;
  }
  attivaTitolo = attributi.containsButton("ATTIVATDA");
  totaliVCH = attributi.containsButton("TOTALIVCH");

  
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
  String strCodiceFiscale   = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
  String strCognome         = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
  String strNome            = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
  String strCodAttivazione  = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodAttivazione");
  String statoTDA        	= StringUtils.getAttributeStrNotNull(serviceRequest,"statoTDA");
  String dataStatoDa		= StringUtils.getAttributeStrNotNull(serviceRequest, "dataStatoDa");  
  String dataStatoA		    = StringUtils.getAttributeStrNotNull(serviceRequest, "dataStatoA"); 
  String azioneTDA          = StringUtils.getAttributeStrNotNull(serviceRequest,"azioneTDA");
  String codAnnullTDA		= StringUtils.getAttributeStrNotNull(serviceRequest,"codAnnullTDA");
  String statoPagamentoTDA  = StringUtils.getAttributeStrNotNull(serviceRequest,"statoPagamentoTDA");
  String assegnatiScaduti	= StringUtils.getAttributeStrNotNull(serviceRequest,"assegnatiScaduti");
  String attivatiScaduti	= StringUtils.getAttributeStrNotNull(serviceRequest,"attivatiScaduti");
  String codCPI 		    = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPI");
  String descrCpiSel 		= StringUtils.getAttributeStrNotNull(serviceRequest,"descrCodCpiSel");
  String descrSedeSA		= StringUtils.getAttributeStrNotNull(serviceRequest,"descrSedeSA");
  String descrStato			= StringUtils.getAttributeStrNotNull(serviceRequest,"descrStato");
  String descrPagamento		= StringUtils.getAttributeStrNotNull(serviceRequest,"descrPagamento");
  String descrAnnull		= StringUtils.getAttributeStrNotNull(serviceRequest,"descrAnnull");
  String descrAzione		= StringUtils.getAttributeStrNotNull(serviceRequest,"descrAzione");
  String tipoRicerca        = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
  String cfEnteCollegato    = StringUtils.getAttributeStrNotNull(serviceRequest,"cfEnteAtt");
  String sedeEnteCollegato  = StringUtils.getAttributeStrNotNull(serviceRequest,"sedeEnteAtt");
  
  String tipoGruppoCollegato = user.getCodTipo();
  
  if (tipoGruppoCollegato.equalsIgnoreCase(it.eng.sil.module.voucher.Properties.SOGGETTO_ACCREDITATO)) {
	  cfEnteCollegato = user.getCfUtenteCollegato(); 		  
  }
  else {
 	  if (!sedeEnteCollegato.equalsIgnoreCase("")) {
 		String []sede = sedeEnteCollegato.split("!");
 		cfEnteCollegato = sede[0].toUpperCase();
 	  }
  }
%>

<html>
<head>
  <title>Risultati della ricerca</title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  
  <af:linkScript path="../../js/"/>

  <script type="text/Javascript">
  function tornaAllaRicerca()
  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
      
	 if (isInSubmit()) return;
  
     url="AdapterHTTP?PAGE=RicercaTDAPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&strCodAttivazione="+"<%=strCodAttivazione%>";
     url += "&strCodiceFiscale="+"<%=strCodiceFiscale%>";
     url += "&strCognome="+"<%=strCognome%>";
     url += "&strNome="+"<%=strNome%>";
     url += "&tipoRicerca="+"<%=tipoRicerca%>";
     url += "&statoTDA="+"<%=statoTDA%>";
     url += "&dataStatoDa="+"<%=dataStatoDa%>";
     url += "&dataStatoA="+"<%=dataStatoA%>";
     url += "&assegnatiScaduti="+"<%=assegnatiScaduti%>";
     url += "&attivatiScaduti="+"<%=attivatiScaduti%>";
     url += "&statoPagamentoTDA="+"<%=statoPagamentoTDA%>";
     url += "&codAnnullTDA="+"<%=codAnnullTDA%>";
     url += "&azioneTDA="+"<%=azioneTDA%>";
     url += "&cfEnteAtt="+"<%=cfEnteCollegato%>";
     url += "&sedeEnteAtt="+"<%=sedeEnteCollegato%>";
     url += "&codCPI="+"<%=codCPI%>";
     url += "&descrCodCpiSel="+"<%=descrCpiSel%>";
     url += "&descrSedeSA="+"<%=descrSedeSA%>";
     url += "&descrStato="+"<%=descrStato%>";
     url += "&descrPagamento="+"<%=descrPagamento%>";
	 url += "&descrAnnull="+"<%=descrAnnull%>";
	 url += "&descrAzione="+"<%=descrAzione%>";
     setWindowLocation(url);
  }

  function attivaTitolo() {
     if (isInSubmit()) return;
     
     url="AdapterHTTP?PAGE=AttivaTDAPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&strCodiceFiscale="+"<%=strCodiceFiscale%>";
     url += "&strCognome="+"<%=strCognome%>";
     url += "&strNome="+"<%=strNome%>";
     url += "&azioneTDA="+"<%=azioneTDA%>";
     url += "&statoTDA="+"<%=statoTDA%>";
     url += "&codCPI="+"<%=codCPI%>";
     url += "&tipoRicerca="+"<%=tipoRicerca%>";
     url += "&cfEnte="+"<%=cfEnteCollegato%>";
     url += "&sedeEnteAtt="+"<%=sedeEnteCollegato%>";
     url += "&strCodAttivazione="+"<%=strCodAttivazione%>";
     url += "&dataStatoDa="+"<%=dataStatoDa%>";
     url += "&dataStatoA="+"<%=dataStatoA%>";
     url += "&assegnatiScaduti="+"<%=assegnatiScaduti%>";
     url += "&attivatiScaduti="+"<%=attivatiScaduti%>";
     url += "&statoPagamentoTDA="+"<%=statoPagamentoTDA%>";
     url += "&codAnnullTDA="+"<%=codAnnullTDA%>";
     url += "&descrCodCpiSel="+"<%=descrCpiSel%>";
     url += "&descrSedeSA="+"<%=descrSedeSA%>";
     url += "&descrStato="+"<%=descrStato%>";
     url += "&descrPagamento="+"<%=descrPagamento%>";
	 url += "&descrAnnull="+"<%=descrAnnull%>";
	 url += "&descrAzione="+"<%=descrAzione%>";
     setWindowLocation(url);
  }

  /*Funzione che chiama la pagina dei totali del voucher */
  function chiamaTotali()
  {  
     // Se la pagina è già in submit, ignoro questo nuovo invio!
     //aggiunti altri parametri per query
     if (isInSubmit()) return;
     url="AdapterHTTP?PAGE=visualizzaTotaliPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&cf="+"<%=strCodiceFiscale%>";
     url += "&cfSel="+"<%=strCodiceFiscale%>";
	 url += "&cognomeSel="+"<%=strCognome%>";
     url += "&nomeSel="+"<%=strNome%>";
     url += "&PRGAZIONE="+"<%=azioneTDA%>";
     url += "&PRGSTATO="+"<%=statoTDA%>";
     url += "&codCPI="+"<%=codCPI%>";
     url += "&tipoRicerca="+"<%=tipoRicerca%>";
     url += "&cfEnte="+"<%=cfEnteCollegato%>";
     url += "&cfSedeEnte="+"<%=sedeEnteCollegato%>";
     url += "&dataStatoDa="+"<%=dataStatoDa%>";
     url += "&dataStatoA="+"<%=dataStatoA%>";
     url += "&codAnnullTDA="+"<%=codAnnullTDA%>";
     url += "&assegnatiScaduti="+"<%=assegnatiScaduti%>";
     url += "&attivatiScaduti="+"<%=attivatiScaduti%>";
     url += "&statoPagamentoTDA="+"<%=statoPagamentoTDA%>";
     url += "&strCodAttivazione="+"<%=strCodAttivazione%>";
     url += "&descrCodCpiSel="+"<%=descrCpiSel%>";
     url += "&descrSedeSA="+"<%=descrSedeSA%>";
     url += "&descrStato="+"<%=descrStato%>";
     url += "&descrPagamento="+"<%=descrPagamento%>";
	 url += "&descrAnnull="+"<%=descrAnnull%>";
	 url += "&descrAzione="+"<%=descrAzione%>";

     window.open(url, "Totali", 'toolbar=0, scrollbars=1, height=800, width=800');
  }
  
	function stampaVouchers(tipoFile) {
		
		var parameters="AdapterHTTP?ACTION_NAME=RPT_LISTA_TDA";
		  parameters += "&apri=true";
		  parameters += "&tipoFile=" + tipoFile;
		  parameters += "&CDNFUNZIONE="+"<%=_funzione%>";
		  parameters += "&strCodiceFiscale="+"<%=strCodiceFiscale%>";
		  parameters += "&strCognome="+"<%=strCognome%>";
		  parameters += "&strNome="+"<%=strNome%>";
		  parameters += "&azioneTDA="+"<%=azioneTDA%>";
		  parameters += "&statoTDA="+"<%=statoTDA%>";
		  parameters += "&codCPI="+"<%=codCPI%>";
		  parameters += "&tipoRicerca="+"<%=tipoRicerca%>";
		  parameters += "&cfEnteAtt="+"<%=cfEnteCollegato%>";
		  parameters += "&sedeEnteAtt="+"<%=sedeEnteCollegato%>";
		  parameters += "&strCodAttivazione="+"<%=strCodAttivazione%>";
 		  parameters += "&dataStatoDa="+"<%=dataStatoDa%>";
 		  parameters += "&dataStatoA="+"<%=dataStatoA%>";
 		  parameters += "&assegnatiScaduti="+"<%=assegnatiScaduti%>";
 		  parameters += "&attivatiScaduti="+"<%=attivatiScaduti%>";
 		  parameters += "&statoPagamentoTDA="+"<%=statoPagamentoTDA%>";
 		  parameters += "&codAnnullTDA="+"<%=codAnnullTDA%>";
 		  parameters += "&descrCodCpiSel="+"<%=descrCpiSel%>";
 		 parameters += "&descrSedeSA="+"<%=descrSedeSA%>";
 		 parameters += "&descrStato="+"<%=descrStato%>";
 		 parameters += "&descrPagamento="+"<%=descrPagamento%>";
 		 parameters += "&descrAnnull="+"<%=descrAnnull%>";
 		 parameters += "&descrAzione="+"<%=descrAzione%>";
 		 
 		 
  		  //alert('stampaVouchersPDF parameters: ' + parameters);
		  
		  //apriGestioneDoc('RPT_LISTA_TDA',parameters ,'TDA');
		  //setWindowLocation(parameters);
		  
		  var w = 1366;
          var h = 768;
          var left = Number((screen.width/2)-(w/2));
          var tops = Number((screen.height/2)-(h/2));
    
    	  //window.open(parameters, "Stampa", 'toolbar=0, scrollbars=1, height=800, width=800');
		  window.open(parameters, "Stampa", 'toolbar=0, scrollbars=1, width=' + w + ', height=' + h + ', top=' + tops + ', left=' + left);
	}
  
  </script>
 
</head>
<body  class="gestione" onload="rinfresca();">

<%
		String txtOut = "";
		String valore ="";
 		  
		if (StringUtils.isFilledNoBlank(strCodiceFiscale)) {
			txtOut += "Codice fiscale <strong>" + strCodiceFiscale
					+ "</strong>; ";
		}
		if (StringUtils.isFilledNoBlank(strCognome)) {
			txtOut += "Cognome <strong>" + strCognome + "</strong>; ";

		}
		if (StringUtils.isFilledNoBlank(strNome)) {
			txtOut += "Nome <strong>" + strNome + "</strong>; ";
		}
		if (StringUtils.isFilledNoBlank(statoTDA)) {
			valore = (String) serviceRequest.getAttribute("descrStato");
			txtOut += "Stato <strong>" + valore + "</strong>; ";
		}
		if (StringUtils.isFilledNoBlank(azioneTDA )) {
			valore = (String) serviceRequest.getAttribute("descrAzione");
			txtOut += "Azione <strong>" + valore + "</strong>; ";
		}
		if (StringUtils.isFilledNoBlank(strCodAttivazione)) {
			txtOut += "Codice Attivazione <strong>" + strCodAttivazione
					+ "</strong>; ";
		}
		if (StringUtils.isFilledNoBlank(statoPagamentoTDA)) {
			valore = (String) serviceRequest.getAttribute("descrPagamento");
			txtOut += "Stato pagamento <strong>" + valore
					+ "</strong>; ";
		}
		if (StringUtils.isFilledNoBlank(codAnnullTDA)) {
			valore = (String) serviceRequest.getAttribute("descrAnnull");
			txtOut += "Motivo annullamento <strong>" + valore
					+ "</strong>; ";
		}
		if (StringUtils.isFilledNoBlank(assegnatiScaduti)) {
			txtOut += "Assegnati scaduti <strong>" + "Si"
					+ "</strong>; ";
		}
		if (StringUtils.isFilledNoBlank(attivatiScaduti)) {
			txtOut += "Attivati scaduti <strong>" + "Si"
					+ "</strong>; ";
		}
		if (StringUtils.isFilledNoBlank(descrCpiSel)) {
			txtOut += "Codice CPI <strong>" + descrCpiSel + "</strong>; ";
		}
		if (StringUtils.isFilledNoBlank(sedeEnteCollegato)) {
			if(serviceRequest.containsAttribute("descrSedeSA")){
				valore = (String) serviceRequest.getAttribute("descrSedeSA");
				txtOut += "Sede ente <strong>" + valore + "</strong>; ";
			}
			
		}
	%>


	<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%}%>




<p>
	<font color="green">
		<af:showMessages prefix="M_AttivaTDA"/>
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:list moduleName="M_ListaTDA" getBack="true"/>

<center>
<table class="main">
<%if (attivaTitolo) {%>
<tr>
<td>
<input class="pulsante" type = "button" name="btnAttiva" value="Attiva Titolo" onclick="attivaTitolo()"/>
</td>
</tr>
<%}%>

<%if (totaliVCH) {%>
<tr>
<td>
<input class="pulsante" type="button" name="totali" value="Totali" onclick="chiamaTotali()"/>
</td>
</tr>
<%}%>

<tr>
<td>
<input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/>
</td>
</tr>
</table>

</center>

	<!--
	<br/>
	<center><input type="button" class="pulsanti" value="Stampa" onclick="stampaVouchers('pdf')" /></center>
	-->
	<br/>
	<center><input type="button" class="pulsanti" value="Stampa in Excel" onclick="stampaVouchers('xls')" /></center>
	<br/>

</body>
</html>