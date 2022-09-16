<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.module.voucher.*, it.eng.sil.util.*,
                it.eng.sil.security.*,
                java.math.*,
                java.util.Date, java.text.DateFormat, java.text.SimpleDateFormat, java.util.Calendar" %>
                
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
 
  //NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canConcludi = false;
  boolean canAggiornaPagamenti = false;
  boolean canRiapriVoucher = false;
  boolean canModify = true;
  boolean canStampaVoucher = false;
  boolean canGestioneIban = false;
  String readonly = "false";
  String codCpiUtenteCollegato = user.getCodRif();
  
  if (!filter.canView()) {
  	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	return;
  }
  
  // NOTE: Attributi della pagina (pulsanti e link)
  canConcludi = attributi.containsButton("CHIUSURA");
  canAggiornaPagamenti = attributi.containsButton("AGGIORNA_PAG");
  canRiapriVoucher = attributi.containsButton("RIAPRI");
  canStampaVoucher = attributi.containsButton("STAMPA");
  canGestioneIban = attributi.containsButton("AGGIORNA_IBAN");
  if (!canConcludi) {
  	readonly = "true";	
  }
  
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  String prgVoucher = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGVOUCHER");
  
  Testata operatoreInfo = null;
  
  String datMaxAttivazione = "";
  String codTipoServizio = "";
  String codStatoVch = "";
  
  BigDecimal numkloVoucher = null;
  String strNumkloVoucher = "";
  String codAttivazione = "";
  String descStatoVch = "";
  String descAzione = "";
  String descAnnullVch= "";
  String descRaggAzione = "";
  String codVchTipoRisultato = "";
  String descRisultatoRaggiunto = "";
  String cfLav = "";
  String cognomeLav = "";
  String nomeLav = "";
  String datAssegnazione = "";
  String datAttivazione = "";
  String datChiusura = "";
  String datMaxChiusura = "";
  String cfEnteAccreditato = "";
  String sedeEnteAccreditato = "";
  String decValTotaleStr = "";
  String decValSpesoStr = "";
  String decValUnitarioStr = "";
  Object decValTotale = null;
  Object decValSpeso = null;
  Object decValUnitario = null;
  String codVchStatoPagamento = "";
  Object decSpesoPagamento = null;
  String decSpesoPagamentoStr = "";
  String cpiCompTit = "";
  String codCpiTitolareVch = "";
  Object cdnLavoratore = "";
  String queryString = null;
  String codiceIBAN = "";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  
  SourceBean rowVoucher = (SourceBean)serviceResponse.getAttribute("M_DettaglioVoucher.ROWS.ROW");
  
  if (rowVoucher != null) {
		datMaxAttivazione = StringUtils.getAttributeStrNotNull(rowVoucher,"datmaxattivazione");
		codStatoVch = StringUtils.getAttributeStrNotNull(rowVoucher,"codstatovoucher");
		numkloVoucher = (BigDecimal)rowVoucher.getAttribute("numklovoucher");
		if (numkloVoucher != null) {
			strNumkloVoucher = numkloVoucher.toString();
		}
		codAttivazione = StringUtils.getAttributeStrNotNull(rowVoucher,"codattivazione");
		descStatoVch = StringUtils.getAttributeStrNotNull(rowVoucher,"descstatovoucher");
		descAnnullVch = StringUtils.getAttributeStrNotNull(rowVoucher,"descannullvoucher");
		descAzione = StringUtils.getAttributeStrNotNull(rowVoucher,"descazione");
		codTipoServizio = StringUtils.getAttributeStrNotNull(rowVoucher,"codtiposervizio");
		descRaggAzione = StringUtils.getAttributeStrNotNull(rowVoucher,"descraggazione");
		cdnLavoratore = rowVoucher.getAttribute("cdnlavoratore");
		cfLav = StringUtils.getAttributeStrNotNull(rowVoucher,"strcodicefiscale");
		cognomeLav = StringUtils.getAttributeStrNotNull(rowVoucher,"strcognome");
		nomeLav = StringUtils.getAttributeStrNotNull(rowVoucher,"strnome");
		datAssegnazione = StringUtils.getAttributeStrNotNull(rowVoucher,"datassegnazione");
		datAttivazione = StringUtils.getAttributeStrNotNull(rowVoucher,"datattivazione");
		datChiusura = StringUtils.getAttributeStrNotNull(rowVoucher,"datfineerogazione");
		datMaxChiusura = StringUtils.getAttributeStrNotNull(rowVoucher,"datmaxerogazione");
		cfEnteAccreditato = StringUtils.getAttributeStrNotNull(rowVoucher,"strcfenteaccreditato");
		sedeEnteAccreditato = StringUtils.getAttributeStrNotNull(rowVoucher,"sedeEnteAccreditato");
		codVchTipoRisultato = StringUtils.getAttributeStrNotNull(rowVoucher,"codVchTipoRisultato");
		cpiCompTit = StringUtils.getAttributeStrNotNull(rowVoucher,"cpiCompTit");
		codCpiTitolareVch = StringUtils.getAttributeStrNotNull(rowVoucher,"codcpi");
		descRisultatoRaggiunto = StringUtils.getAttributeStrNotNull(rowVoucher,"strdesrisultato");
		if (codTipoServizio.equalsIgnoreCase(Properties.SERVIZIO_A_RISULTATO) && codStatoVch.equalsIgnoreCase(StatoEnum.CONCLUSO.getCodice()) &&
			codVchTipoRisultato.equals("")) {
			codVchTipoRisultato = Properties.COD_RISULTATO_NON_CONSEGUITO;
		}
		codVchStatoPagamento = StringUtils.getAttributeStrNotNull(rowVoucher,"codVchStatoPagamento");
		decValTotale = rowVoucher.getAttribute("decvaltot");
		decValSpeso = rowVoucher.getAttribute("decspesaeffettiva");
		decValUnitario = rowVoucher.getAttribute("decvalunitario");
		decSpesoPagamento = rowVoucher.getAttribute("decpagato");
		if (decValTotale != null) {
			decValTotaleStr = decValTotale.toString();
		}
		if (decValSpeso != null) {
			decValSpesoStr = decValSpeso.toString();
		}
		if (decValUnitario != null) {
			decValUnitarioStr = decValUnitario.toString();
		}
		if (decSpesoPagamento != null) {
			decSpesoPagamentoStr = decSpesoPagamento.toString();
		}
		codiceIBAN = StringUtils.getAttributeStrNotNull(rowVoucher,"stribanfattura");
		cdnUtins = SourceBeanUtils.getAttrStrNotNull(rowVoucher, "cdnUtins");
		dtmins = SourceBeanUtils.getAttrStrNotNull(rowVoucher, "dtmins");
		cdnUtmod = SourceBeanUtils.getAttrStrNotNull(rowVoucher, "cdnUtmod");
		dtmmod = SourceBeanUtils.getAttrStrNotNull(rowVoucher, "dtmmod");
		
		operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  }
  
  if (!codStatoVch.equalsIgnoreCase(StatoEnum.ATTIVATO.getCodice())) {
	  readonly = "true";
	  canConcludi = false;
  }
  
  if (!codStatoVch.equalsIgnoreCase(StatoEnum.CONCLUSO.getCodice())) {
	  canAggiornaPagamenti = false;
	  canRiapriVoucher = false;
	  canStampaVoucher = false;
  }
  
  if (codStatoVch.equalsIgnoreCase(StatoEnum.CONCLUSO.getCodice()) && 
		  (StringUtils.isEmptyNoBlank(decValSpesoStr) || decValSpesoStr.equals("0.00") ||
				  codTipoServizio.equalsIgnoreCase(Properties.SERVIZIO_A_RISULTATO))) {
	  canStampaVoucher = false;
  }
  
  if (canGestioneIban) {
	  if ( (codStatoVch.equalsIgnoreCase(StatoEnum.ATTIVATO.getCodice())) || 
		   (codStatoVch.equalsIgnoreCase(StatoEnum.CONCLUSO.getCodice()) && 
	        codVchStatoPagamento.equalsIgnoreCase(it.eng.sil.module.voucher.Properties.STATO_PAGAMENTO_IN_ATTESA)) ) {
		  canGestioneIban = true;	  
	  }
	  else {
		  canGestioneIban = false;	  
	  }
  }
  
  String htmlStreamTop = StyleUtils.roundTopTable(canConcludi || canGestioneIban);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canConcludi || canGestioneIban);
  
  BigDecimal ggMaxRiapriTDA = null;
  SourceBean rowMaxGGRiapriTDA = (SourceBean)serviceResponse.getAttribute("M_GetMaxGGRiapriTDA.ROWS.ROW");
  if (rowMaxGGRiapriTDA != null) {
	  ggMaxRiapriTDA = new BigDecimal(rowMaxGGRiapriTDA.getAttribute("num").toString());
  }
  
  boolean canRiapriPlus = true;
  long giorni = 0;
  if (datChiusura != ""){
	  DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	  Date dateChiusura = format.parse(datChiusura);
	  
	  Calendar calendarChiusura = Calendar.getInstance();
	  calendarChiusura.setTime(dateChiusura);
	  
	  Calendar calendarOgg = Calendar.getInstance();
	  calendarOgg.setTime(new Date());
	  
	  giorni = (calendarOgg.getTime().getTime() - calendarChiusura.getTime().getTime()) / (24 * 3600 * 1000);
	  
	  if(giorni > ggMaxRiapriTDA.longValue()){
		  canRiapriPlus = false;
	  }
  }

  
  
%>
<html>
<head>
  <title>Titoli di Acquisto</title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  
  <%@ include file="../global/fieldChanged.inc" %>
  <%@ include file="../documenti/_apriGestioneDoc.inc"%>
  
  <af:linkScript path="../../js/"/>

  <script type="text/Javascript">

  var codTipoServizioVoucher = "<%=codTipoServizio%>";

  function tornaLista() {
	if (isInSubmit()) return;
	<%
	// Recupero l'eventuale URL generato dalla LISTA precedente
	String token = "_TOKEN_" + "ListaTDAPage";
	String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());	
	
	if (goBackListUrl != null) {
		
		int occorrenzaCancella=goBackListUrl.indexOf("ATTIVATDA");
		if (occorrenzaCancella!=-1) {
			int occorrenzaFineCancella=goBackListUrl.indexOf('&', occorrenzaCancella);
			String urlDilistacomodo=goBackListUrl.substring(0, occorrenzaCancella);
			String urlDilistacomodo2=goBackListUrl.substring(occorrenzaFineCancella);
			goBackListUrl=urlDilistacomodo + urlDilistacomodo2;
		}

	 }
%>
	setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
  }

  function checkConcludi() {
	if (isInSubmit()) return;

	var okConcludi = true;
	if (codTipoServizioVoucher == "SR") {
		if (document.frmAzione.codVchTipoRisultato.value == "") {
			alert("E'necessario indicare il tipo del risultato raggiunto");
			okConcludi = false;
		}
	}
	if( !validateDate('datconclusione'))  {
		okConcludi = false;
	}
	if (okConcludi) {
		
		if (document.frmAzione.datconclusione.value == "") {
			alert("E' necessario valorizzare la data di fine erogazione");
		}
		else {
			if (confirm("ATTENZIONE: L'operazione di conclusione chiude definitivamente la gestione del Titolo di Acquisto.\n" + 
						"Dopo la chiusura non sarà più possibile modificare i dati. Vuoi procedere?")) {
				document.frmAzione.OPERAZIONETDA.value = "CHIUSURA";
				doFormSubmit(document.frmAzione);
			}
		}
	}
  }

  function checkAggiornaPagamenti() {
	if (isInSubmit()) return;

	var okAggiornaPag = true;

	if (document.frmAzione.codVchStatoPagamento.value == "") {
		alert("E' necessario valorizzare lo stato pagamento");
		okAggiornaPag = false;
	}

	if (!controllaFixedFloat('decpagato', 7, 2)) {
		okAggiornaPag = false;
	}
	
	if (okAggiornaPag) {
		if (confirm("ATTENZIONE: Confermare l'operazione di aggiornamento pagamenti?")) {
			document.frmAzione.OPERAZIONETDA.value = "AGGIORNAPAGAMENTI";
			doFormSubmit(document.frmAzione);	
		}
	}
  }

  function checkRiapri() {
    if (!<%=canRiapriPlus%>){
    	alert("Non è più possibile utilizzare la funzione di riapri per superato limite di tempo (<%=ggMaxRiapriTDA%> giorni) dalla data di Conclusione");
    	return;
	}
	
	if (isInSubmit()) return;
	
	if (confirm("ATTENZIONE: Confermare la riapertura del Titolo di Acquisto?")) {
		document.frmAzione.OPERAZIONETDA.value = "RIAPRI";
		doFormSubmit(document.frmAzione);	
	}
  }

  function stampaTDAVoucher(prgVoucher) {
	apriGestioneDoc('RPT_STAMPA_TDA_ATTESTAZIONE','&cdnLavoratore=<%=cdnLavoratore.toString()%>&cdnFunzione=<%=_funzione%>&strchiavetabella=' + prgVoucher, '<%=it.eng.sil.module.voucher.Properties.TIPO_DOC_TDA_PARTECIPAZIONE%>');
  }

  function checkAggiornaIban() {
	if (isInSubmit()) return;
	
	if (confirm("ATTENZIONE: Confermare l'operazione di aggiornamento IBAN/Numero Fattura?")) {
		document.frmAzione.OPERAZIONETDA.value = "AGGIORNAIBAN";
		doFormSubmit(document.frmAzione);	
	}
  }
  
  </script>
 
</head>
<body  class="gestione" onload="rinfresca();">
<p>
	<font color="green">
		<af:showMessages prefix="M_ConcludiTDA"/>
		<af:showMessages prefix="M_AggiornaPagamentiTDA"/>
		<af:showMessages prefix="M_RiapriTDA"/>
		<af:showMessages prefix="M_AggiornaIbanTDA"/>
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<%
Linguette _linguetta = new Linguette(user, new Integer(_funzione).intValue() , _page, new BigDecimal(prgVoucher)); 
_linguetta.setCodiceItem("prgVoucher");
_linguetta.show(out);


%>


<p class="titolo">Titolo Di Acquisto</p>

<%@ include file="titolo_acquisto.inc" %>

<center>
  <table>
  <tr><td align="center">
<%
  operatoreInfo.showHTML(out);
%>
  </td></tr>
  </table>
</center>
</body>
</html>