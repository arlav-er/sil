<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.User,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%
	
	String  titolo = "TOTALI VOUCHER";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	//int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	//int cdnfunzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");

	

	// CONTROLLO ACCESSO ALLA PAGINA
	/* Commentato per test
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	*/

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canModify = attributi.containsButton("aggiorna");
	boolean canDelete = attributi.containsButton("rimuovi");
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	
	// Recupero URL della LISTA da cui sono venuto al dettaglio (se esiste)
	String token = "_TOKEN_" + "ListaPage";
	String goBackInf = "Torna alla pagina di ricerca";
	String goBackUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
	if (StringUtils.isEmpty(goBackUrl)) {
		goBackInf = "Torna alla ricerca";
		goBackUrl = "PAGE=VoucherRicercaPage&cdnfunzione=" + cdnfunzioneStr;
	}

	// Sola lettura: viene usato per tutti i campi di input
	String readonly = String.valueOf( ! canModify );
	
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	String prgStatiVoucher= StringUtils.getAttributeStrNotNull(serviceRequest, "prgStatiVoucher");
	String prgAzioneVoucher= StringUtils.getAttributeStrNotNull(serviceRequest, "prgAzioneVoucher");
	String fcognome=StringUtils.getAttributeStrNotNull(serviceRequest, "FSTRCOGNOME");
	String fnome=StringUtils.getAttributeStrNotNull(serviceRequest, "FSTRNOME");
	String fcf=StringUtils.getAttributeStrNotNull(serviceRequest, "FSTRCODICEFISCALE");
	
	String strCodiceFiscale=fcf;
	String strCognome=fcognome;
	String strNome=fnome;
	
	int cdnUt = user.getCodut();
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	String strCodCpi;
	strCodCpi =  user.getCodRif();
	//Solamente per testare ricordare di eliminare la riga
	canModify=true;
	
	SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_CAMPITOTALIVOUCHER");
	SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
	BigDecimal BconteggioTot = null;
	BigDecimal BinAttesaTot = null;
	BigDecimal BpagatoEuroTot = null;
	
	//BconteggioTot=row.getAttribute("conteggioTot") != null?(BigDecimal) row.getAttribute("conteggioTot"):null;
	String conteggioTot="0";
	String inAttesaTot="0";
	String pagatoEuroTot="0";
	String strCF="";
	String cfRicercato="";
	String strCognomeRic="";
	String cognomeRicercato="";
	String strNomeRic="";
	String nomeRicercato="";
	String strStatoRic="";
	String statoRicercato="";
	String strAzioneRic="";
	String azioneRicercato="";
	String nessunTrovati="";
	String codiceAttivazione="";
	String statoPagamento="";
	String motivoAnull="";
	String assegnatiScaduti="";
	String attivatiScaduti="";
	String codCpi="";
	String sedeEnte="";
	String strcodiceAttivazione="";
	String strstatoPagamento="";
	String strmotivoAnullo="";
	String strassegnatiScaduti="";
	String strattivatiScaduti="";
	String strcodCpi="";
	String descrCPI ="";
	String strsedeEnte="";
	
	boolean nessunTrov=false;
	
	
	if (row.getAttribute("CONTEGGIO") != null){
		conteggioTot=row.getAttribute("CONTEGGIO").toString();
	}
	
	if (row.getAttribute("TOT_EURO_ATTESA") != null){
		inAttesaTot=row.getAttribute("TOT_EURO_ATTESA").toString();
	}
	
	if (row.getAttribute("TOT_EURO_PAGATI") != null){
		pagatoEuroTot=row.getAttribute("TOT_EURO_PAGATI").toString();
	}
	if (cont.getAttribute("codiceFiscaleSel") != null && !"".equals(cont.getAttribute("codiceFiscaleSel"))){
		strCF="YES";
		cfRicercato=cont.getAttribute("codiceFiscaleSel").toString();
		nessunTrov=true;
	}
	if (cont.getAttribute("cognomeSel") != null && !"".equals(cont.getAttribute("cognomeSel"))){
		strCognomeRic="YES";
		cognomeRicercato=cont.getAttribute("cognomeSel").toString();
		nessunTrov=true;
	}
	if (cont.getAttribute("nomeSel") != null && !"".equals(cont.getAttribute("nomeSel"))){
		strNomeRic="YES";
		nomeRicercato=cont.getAttribute("nomeSel").toString();
		nessunTrov=true;
	}
	//Modifica parametri nuovi inserimenti
	if (cont.getAttribute("azioneSel") != null && !"".equals(cont.getAttribute("azioneSel"))){
		strAzioneRic="YES";
		azioneRicercato=cont.getAttribute("azioneSel").toString();
		nessunTrov=true;
	}
	
	if (cont.getAttribute("statoSel") != null && !"".equals(cont.getAttribute("statoSel")) ){
		strStatoRic="YES";
		statoRicercato=cont.getAttribute("statoSel").toString();
		nessunTrov=true;
	}
	
	if (cont.getAttribute("codiceAttivazioneSel") != null && !"".equals(cont.getAttribute("codiceAttivazioneSel")) ){
		strcodiceAttivazione="YES";
		codiceAttivazione=cont.getAttribute("codiceAttivazioneSel").toString();
		nessunTrov=true;
	}
	
	if (cont.getAttribute("statoPagamentoSel") != null && !"".equals(cont.getAttribute("statoPagamentoSel")) ){
		strstatoPagamento="YES";
		statoPagamento=cont.getAttribute("statoPagamentoSel").toString();
		nessunTrov=true;
	}
	
	if (cont.getAttribute("motivoAnullSel") != null && !"".equals(cont.getAttribute("motivoAnullSel")) ){
		strmotivoAnullo="YES";
		motivoAnull=cont.getAttribute("motivoAnullSel").toString();
		nessunTrov=true;
	}
	
	
	if (cont.getAttribute("assegnatiScadutiSel") != null && !"".equals(cont.getAttribute("assegnatiScadutiSel")) ){
		strassegnatiScaduti="YES";
		assegnatiScaduti="Selezionato";
		nessunTrov=true;
	}
	
	if (cont.getAttribute("attivatiScadutiSel") != null && !"".equals(cont.getAttribute("attivatiScadutiSel")) ){
		strattivatiScaduti="YES";
		attivatiScaduti="Selezionato";
		nessunTrov=true;
	}
	 
	if (cont.getAttribute("codCpiSel") != null && !"".equals(cont.getAttribute("codCpiSel")) ){
		strcodCpi="YES";
		codCpi=cont.getAttribute("codCpiSel").toString();
		descrCPI = cont.getAttribute("descrCodCpiSel").toString();
		nessunTrov=true;
	}
	
	if (cont.getAttribute("sedeEnteSel") != null && !"".equals(cont.getAttribute("sedeEnteSel")) ){
		strsedeEnte="YES";
		sedeEnte=cont.getAttribute("sedeEnteSel").toString();
		nessunTrov=true;
	}
	
	if (nessunTrov==false){
		nessunTrovati="YES";
	}
	
 
	
%>
<html>
<head>
<title><%= titolo %></title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
 


<script language="Javascript">

	/* Funzione per tornare alla pagina precedente */
	function goBack() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		goTo("<%= goBackUrl %>");
	}

</script>
</head>

<body  class="gestione" onload="rinfresca();">


<p class="titolo"><%= titolo %></p>
	<%
		String txtOut = "";

		if (strCF != null && !"".equals(strCF) && strCF.equals("YES")) {
			txtOut += "Codice fiscale <strong>" + cfRicercato
					+ "</strong>; ";
		}
		if (strCognomeRic != null && !"".equals(strCognomeRic)
				&& strCognomeRic.equals("YES")) {
			txtOut += "Cognome <strong>" + cognomeRicercato + "</strong>; ";

		}
		if (strNomeRic != null && !"".equals(strNomeRic)
				&& strNomeRic.equals("YES")) {
			txtOut += "Nome <strong>" + nomeRicercato + "</strong>; ";
		}
		if (strStatoRic != null && !"".equals(strStatoRic)
				&& strStatoRic.equals("YES")) {
			txtOut += "Stato <strong>" + statoRicercato + "</strong>; ";
		}
		if (strAzioneRic != null && !"".equals(strAzioneRic)
				&& strAzioneRic.equals("YES")) {
			txtOut += "Azione <strong>" + azioneRicercato + "</strong>; ";
		}
		if (strcodiceAttivazione != null
				&& !"".equals(strcodiceAttivazione)
				&& strcodiceAttivazione.equals("YES")) {
			txtOut += "Codice Attivazione <strong>" + codiceAttivazione
					+ "</strong>; ";
		}
		if (strstatoPagamento != null && !"".equals(strstatoPagamento)
				&& strstatoPagamento.equals("YES")) {
			txtOut += "Stato pagamento <strong>" + statoPagamento
					+ "</strong>; ";
		}
		if (strmotivoAnullo != null && !"".equals(strmotivoAnullo)
				&& strmotivoAnullo.equals("YES")) {
			txtOut += "Motivo annullamento <strong>" + motivoAnull
					+ "</strong>; ";
		}
		if (strassegnatiScaduti != null && !"".equals(strassegnatiScaduti)
				&& strassegnatiScaduti.equals("YES")) {
			txtOut += "Assegnati scaduti <strong>" + "Si"
					+ "</strong>; ";
		}
		if (strattivatiScaduti != null && !"".equals(strattivatiScaduti)
				&& strattivatiScaduti.equals("YES")) {
			txtOut += "Attivati scaduti <strong>" + "Si"
					+ "</strong>; ";
		}
		if (strcodCpi != null && !"".equals(strcodCpi)
				&& strcodCpi.equals("YES")) {
			txtOut += "Codice CPI <strong>" + descrCPI + "</strong>; ";
		}
		if (strsedeEnte != null && !"".equals(strsedeEnte)
				&& strsedeEnte.equals("YES")) {
			txtOut += "Sede ente <strong>" + sedeEnte + "</strong>; ";
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
		<af:showMessages />
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:list moduleName="M_ListaTotaliVoucher" getBack="true"/>


	 
	
	<table class="lista" border="0" cellspacing="0" cellpadding="1px" align="center" maxwidth="55%">
		<tr> 
			<td colspan="3">&nbsp;</td>
		</tr>
 
		<tr >
			
					<td  width="25%">Totali</td>
					<td class="etichetta" colspan="2" align="left" valign="left" width="80%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						Conteggio:
						<af:textBox classNameBase="input" name="conteggio" type="text" readonly="true" size="5" value="<%=conteggioTot%>" />
						in Attesa:
						<af:textBox classNameBase="input" name="inAttesa" type="text" readonly="true" size="5" value="<%=inAttesaTot%>" />
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						Pagato Euro:
						<af:textBox classNameBase="input" name="pagatoEuro" type="text" readonly="true" size="5" value="<%=pagatoEuroTot%>" />
					</td>
					<td  width="10%"> &nbsp;</td>
			
		</tr>
		<tr> 
			<td colspan="3">&nbsp;</td>
		</tr>
	 </table>
	<center>
	<table class="main">
	
		<tr>
			<td colspan="2">
				<input type="button" class="pulsante" name="back" value="Chiudi"
						onclick="window.close();" />
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>

	</table>
 </center>

</body>
</html>	

