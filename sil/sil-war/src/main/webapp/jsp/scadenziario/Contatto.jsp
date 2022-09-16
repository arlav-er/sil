<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,it.eng.sil.security.*,it.eng.afExt.utils.*,it.eng.sil.util.*,java.util.*,it.eng.sil.security.PageAttribs,it.eng.afExt.utils.StringUtils,java.math.*,java.io.*,it.eng.sil.security.User,com.engiweb.framework.security.*" %>        

<%
        	SourceBean unitaRow = null;
        	SourceBean lavoratoreRow = null;
        	String strTelContatti = "";
        	String strTel = "";
        	String strTelRes = "";
        	String strTelDom = "";
        	String strTelAltro = "";
        	String strTelCell = "";
        	String strTelFax = "";
        	String txtContatto = "";
        	String strEffettoContatto = "";
        	String flgRicontattare = "";
        	String datEntroIl = "";
        	String _page = (String) serviceRequest.getAttribute("PAGE");
        	String _pageProvenienza = (String) serviceRequest.getAttribute("PAGEPROVENIENZA");

        	int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

        	String strPrgAzienda = serviceRequest.containsAttribute("PRGAZIENDA") ? serviceRequest.getAttribute("PRGAZIENDA").toString() : "";
        	String strPrgUnita = serviceRequest.containsAttribute("PRGUNITA") ? serviceRequest.getAttribute("PRGUNITA").toString() : "";
        	String strCdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE") ? serviceRequest.getAttribute("CDNLAVORATORE").toString() : "";
        	String strRecuperaInfo = serviceRequest.containsAttribute("RECUPERAINFO") ? serviceRequest.getAttribute("RECUPERAINFO").toString() : "";
        	String codCpi = serviceRequest.containsAttribute("CODCPI") ? serviceRequest.getAttribute("CODCPI").toString() : "";
        	String CodCpiApp = serviceRequest.containsAttribute("CodCpiApp") ? serviceRequest.getAttribute("CodCpiApp").toString() : "";

        	String datInizioIscr = serviceRequest.containsAttribute("DATINIZIOISCR") ? ((String) serviceRequest.getAttribute("DATINIZIOISCR")) : "";
        	String datStimata = serviceRequest.containsAttribute("DATSTIMATA") ? ((String) serviceRequest.getAttribute("DATSTIMATA")) : "";
        	String codEsito = serviceRequest.containsAttribute("CODESITO") ? ((String) serviceRequest.getAttribute("CODESITO")) : "";
        	String codAzione = serviceRequest.containsAttribute("CODAZIONE") ? ((String) serviceRequest.getAttribute("CODAZIONE")) : "";
        	String _strPrgAzienda_ = serviceRequest.containsAttribute("_PRGAZIENDA_") ? serviceRequest.getAttribute("_PRGAZIENDA_").toString() : "";
        	String _strPrgUnita_ = serviceRequest.containsAttribute("_PRGUNITA_") ? serviceRequest.getAttribute("_PRGUNITA_").toString() : "";
        	String _strCdnLavoratore_ = serviceRequest.containsAttribute("_CDNLAVORATORE_") ? serviceRequest.getAttribute("_CDNLAVORATORE_").toString() : "";
        	String strTipoScadenziario = serviceRequest.containsAttribute("SCADENZIARIO") ? serviceRequest.getAttribute("SCADENZIARIO").toString() : "";
        	
        	if (serviceRequest.containsAttribute("SALVACONTATTO")) {
        		serviceRequest.delAttribute("SALVACONTATTO");
        	}

        	/*
        	 * Se il cpi non è presente nella request, lo recupero dalla sessione
        	 */

        	if (codCpi.equals("") && ( !"VER2".equals(strTipoScadenziario) && !"VER4".equals(strTipoScadenziario)) ) {
        		//Recupero CODCPI
        		codCpi = user.getCodRif();
        	}

        	SourceBean contSpi = (SourceBean) serviceResponse.getAttribute("MSPI_UTENTE");
        	SourceBean rowSpi = (SourceBean) contSpi.getAttribute("ROWS.ROW");
        	String strSpiContatto = rowSpi.containsAttribute("prgspi") ? rowSpi.getAttribute("prgspi").toString() : "";

        	String strCodTipoVal = serviceRequest.containsAttribute("CODTIPOVALIDITA") ? serviceRequest.getAttribute("CODTIPOVALIDITA").toString() : "";
        	String strPatto297 = serviceRequest.containsAttribute("FLGPATTO") ? serviceRequest.getAttribute("FLGPATTO").toString() : "";
        	String strStatoValCV = serviceRequest.containsAttribute("statoValCV") ? serviceRequest.getAttribute("statoValCV").toString() : "";
        	
        	String OpzioneCV = serviceRequest.containsAttribute("OpzioneCV") ? serviceRequest.getAttribute("OpzioneCV").toString() : "";
        	
        	
        	User userCurr = (User) sessionContainer.getAttribute(User.USERID);
        	InfCorrentiLav infCorrentiLav = null;
        	InfCorrentiAzienda infCorrentiAzienda = null;
        	if (strCdnLavoratore.compareTo("") != 0) {
        		infCorrentiLav = new InfCorrentiLav(sessionContainer, strCdnLavoratore, userCurr);
        		infCorrentiLav.setSkipLista(true);
        		lavoratoreRow = (SourceBean) serviceResponse.getAttribute("M_GetLavoratoreRecapiti.ROW");
        		strTelRes = StringUtils.getAttributeStrNotNull(lavoratoreRow,"STRTELRES");
        		strTelDom = StringUtils.getAttributeStrNotNull(lavoratoreRow,"STRTELDOM");
        		strTelAltro = StringUtils.getAttributeStrNotNull(lavoratoreRow,"STRTELALTRO");
        		strTelCell = StringUtils.getAttributeStrNotNull(lavoratoreRow,"STRCELL");
        		if ((!strTelRes.equals("")) || (!strTelDom.equals("")) || (!strTelAltro.equals("")) || (!strTelCell.equals(""))) {
        			strTelContatti = " Riferimenti : tel. ";
        			if (!strTelDom.equals("")) {
        				strTelContatti = strTelContatti + "Dom <b>" + strTelDom + "</b>/";
        			}
        			if (!strTelRes.equals("")) {
        				strTelContatti = strTelContatti + "Res <b>" + strTelRes + "</b>/";
        			}
        			if (!strTelAltro.equals("")) {
        				strTelContatti = strTelContatti + "Altro <b>" + strTelAltro + "</b>/";
        			}
        			if (!strTelCell.equals("")) {
        				strTelContatti = strTelContatti + "Cell <b>" + strTelCell + "</b>/";
        			}
        			strTelContatti = strTelContatti.substring(0, strTelContatti.length() - 1);
        		}
        	} else {
        		unitaRow = (SourceBean) serviceResponse.getAttribute("M_GetUnitaAzienda.ROWS.ROW");
        		strTel = StringUtils.getAttributeStrNotNull(unitaRow, "strTel");
        		strTelFax = StringUtils.getAttributeStrNotNull(unitaRow,"strFax");
        		if ((!strTel.equals("")) || (!strTel.equals(""))) {
        			strTelContatti = " Riferimenti : ";
        			if (!strTel.equals("")) {
        				strTelContatti = strTelContatti + "tel. <b>" + strTel + "</b>/";
        			}
        			if (!strTelFax.equals("")) {
        				strTelContatti = strTelContatti + "Fax <b>" + strTelFax + "</b>/";
        			}
        			strTelContatti = strTelContatti.substring(0, strTelContatti.length() - 1);
        		}

        		infCorrentiAzienda = new InfCorrentiAzienda(strPrgAzienda, strPrgUnita);
        	}

        	String data_al = serviceRequest.containsAttribute("DATAAL") ? serviceRequest.getAttribute("DATAAL").toString() : "";
        	String data_dal = serviceRequest.containsAttribute("DATADAL") ? serviceRequest.getAttribute("DATADAL").toString() : "";
        	String datascadenza = serviceRequest.containsAttribute("DATASCADENZA") ? serviceRequest.getAttribute("DATASCADENZA").toString() : "";
        	String strFiltra = serviceRequest.containsAttribute("FILTRALISTA") ? serviceRequest.getAttribute("FILTRALISTA").toString() : "";
        	

        	String strDescMotivoContatto = serviceRequest.containsAttribute("MOTIVO_CONTATTO") ? serviceRequest.getAttribute("MOTIVO_CONTATTO").toString() : "";
        	String strMotivoContatto = strDescMotivoContatto;
        	String strDirezione = serviceRequest.containsAttribute("DIREZIONE") ? serviceRequest.getAttribute("DIREZIONE").toString() : "";
        	String strDirezioneContatto = strDirezione;
        	String strTipo = serviceRequest.containsAttribute("TIPO") ? serviceRequest.getAttribute("TIPO").toString() : "";
        	String strTipoContatto = strTipo;
        	String strPrgRosa = serviceRequest.containsAttribute("PRGROSA") ? serviceRequest.getAttribute("PRGROSA").toString() : "";
        	String strCpiRose = serviceRequest.containsAttribute("CPIROSE") ? serviceRequest.getAttribute("CPIROSE").toString() : "";
        	String strPrgRichiestaAz = serviceRequest.containsAttribute("PRGRICHIESTAAZ") ? serviceRequest.getAttribute("PRGRICHIESTAAZ").toString() : "";
        	
        	String giornoDB = "";
        	String meseDB = "";
        	String annoDB = "";
        	// Istanzio giornoDB, meseDb, annoDb per l'inserimento di un nuovo contatto
        	// Data Odierna
        	Calendar oggi = Calendar.getInstance();
        	giornoDB = Integer.toString(oggi.get(5));
        	meseDB = Integer.toString(oggi.get(2) + 1);
        	annoDB = Integer.toString(oggi.get(1));
        	String datContatto = giornoDB + "/" + meseDB + "/" + annoDB;
        	String ora = Integer.toString(oggi.get(Calendar.HOUR_OF_DAY));
        	if (ora.length() <= 1) {
        		ora = "0" + ora;
        	}
        	String minuti = Integer.toString(oggi.get(Calendar.MINUTE));
        	if (minuti.length() <= 1) {
        		minuti = "0" + minuti;
        	}
        	String strOraContatto = ora + ":" + minuti;
        	boolean canvisualizzaTuttiContatti = false;
        	boolean canNuovoContatto = false;
        	PageAttribs attributi = new PageAttribs(userCurr, _page);
        	List listaSezioni = attributi.getSectionList();
        	if (listaSezioni.contains("INSERISCI")) {
        		canNuovoContatto = true;
        	} else {
        		canNuovoContatto = false;
        	}

        	canvisualizzaTuttiContatti = attributi.containsButton("TUTTICONTATTI");

        	String htmlStreamTop = StyleUtils.roundTopTable(canNuovoContatto);
        	String htmlStreamBottom = StyleUtils.roundBottomTable(canNuovoContatto);

        	String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");
        	String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");
        	String messScad = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_SCAD");
        	String listPageScad = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_SCAD");

        	String token = "";
        	String urlDiLista = "";
        	if (sessionContainer != null) {
        		token = "_TOKEN_" + "SCADVERIFICHELISTAPAGE";
        		urlDiLista = "AdapterHTTP?" + (String) sessionContainer.getAttribute(token.toUpperCase());
        	}
        %>

<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Contatti</title>
  <script language="Javascript">

  function CercaTuttiContatti() {		
		url =  "AdapterHTTP?PAGE=<%=_page%>";		
		url += "&CDNFUNZIONE=<%=_funzione%>" ;
		url += "&is_foot_jump=Y";
		url += "&cdnLavoratore=<%=strCdnLavoratore%>";
		url += "&tuttiCPI=Y";
		url += "&prgAzienda=<%=strPrgAzienda%>";
		url += "&prgUnita=<%=strPrgUnita%>";
		
		setWindowLocation(url);		
	}

</script>
<script language="Javascript">

<%if (!strCdnLavoratore.equals(""))
				attributi.showHyperLinks(out, requestContainer,
						responseContainer, "cdnLavoratore=" + strCdnLavoratore);
			else
				attributi.showHyperLinks(out, requestContainer,
						responseContainer, "prgAzienda=" + strPrgAzienda
								+ "&prgUnita=" + strPrgUnita);%>
</SCRIPT>
</head>
<body class="gestione" onLoad="rinfresca()">
<%
	if (strCdnLavoratore.compareTo("") != 0) {
		infCorrentiLav.setSkipLista(true);
		infCorrentiLav.show(out);
	} else {
		infCorrentiAzienda.show(out);
	}

	String dataUltimoContatto = "";
	SourceBean riga = null;
	//Recupero la data dell'ultimo contatto
	SourceBean row = (SourceBean) serviceResponse
			.getAttribute("M_GETCONTATTILAVORATORE");
	Vector rows_VectorContatti = null;
	rows_VectorContatti = row.getAttributeAsVector("ROWS.ROW");
	if (rows_VectorContatti.size() > 0) {
		riga = (SourceBean) rows_VectorContatti.elementAt(0);
		dataUltimoContatto = riga.getAttribute("DATCONTATTO")
				.toString();
	}
%>

<font color="red">
  <af:showErrors/>
</font>
<font color="green">
</font>
<af:form name="frmContatto" action="AdapterHTTP" method="POST" onSubmit="controllaCampi()">
<input type="hidden" name="CODCPICONTATTO" value="<%=codCpi%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="PAGE" value="ScadSalvaContattoPage">
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="PRGUNITA" value="<%=strPrgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=strPrgAzienda%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=strCdnLavoratore%>">
<input type="hidden" name="CodCpiApp" value="<%=CodCpiApp%>">

<input type="hidden" name="_PRGUNITA_" value="<%=_strPrgUnita_%>">
<input type="hidden" name="_PRGAZIENDA_" value="<%=_strPrgAzienda_%>">
<input type="hidden" name="_CDNLAVORATORE_" value="<%=_strCdnLavoratore_%>">

<input type="hidden" name="RECUPERAINFO" value="<%=strRecuperaInfo%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<%-- Savino 13/03/2006: in verifiche-soggetti pronti all'incrocio il parametro data scadenza
          viene utilizzato per la descrizione cpi, col rischio di avere caratteri non accettati da javascript (Es.: reggio nell'emilia) 
--%>
<input type="hidden" name="DATASCADENZA" value="<%=StringUtils
										.formatValue4Javascript(datascadenza)%>">
<input type="hidden" name="PRGCONTATTO" value="">
<input type="hidden" name="DATAAL" value="<%=data_al%>">
<input type="hidden" name="DATADAL" value="<%=data_dal%>">
<input type="hidden" name="MOTIVO_CONTATTO" value="<%=strDescMotivoContatto%>"/>
<input type="hidden" name="FILTRALISTA" value="<%=strFiltra%>">
<input type="hidden" name="SCADENZIARIO" value="<%=strTipoScadenziario%>"/>
<input type="hidden" name="CODTIPOVALIDITA" value="<%=strCodTipoVal%>">
<input type="hidden" name="FLGPATTO" value="<%=strPatto297%>">
<input type="hidden" name="statoValCV" value="<%=strStatoValCV%>">
<input type="hidden" name="OpzioneCV" value="<%=OpzioneCV%>">


<input type="hidden" name="DIREZIONE" value="<%=strDirezione%>">
<input type="hidden" name="TIPO" value="<%=strTipo%>">
<input type="hidden" name="PRGROSA" value="<%=strPrgRosa%>">
<input type="hidden" name="CPIROSE" value="<%=strCpiRose%>">
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=strPrgRichiestaAz%>">

<input type="hidden" name="MESSAGE" value="<%=messRosa%>" >
<input type="hidden" name="LIST_PAGE" value="<%=listPageScad%>" >
<input type="hidden" name="MESSAGE_ROSA" value="<%=messRosa%>"/>
<input type="hidden" name="LIST_PAGE_ROSA" value="<%=listPageRosa%>"/>
<input type="hidden" name="MESSAGE_SCAD" value="<%=messScad%>"/>
<input type="hidden" name="LIST_PAGE_SCAD" value="<%=listPageScad%>"/>

<input type="hidden" name="DATINIZIOISCR" value="<%=datInizioIscr%>"/>
<input type="hidden" name="DATSTIMATA" value="<%=datStimata%>"/>
<input type="hidden" name="CODESITO" value="<%=codEsito%>"/>
<input type="hidden" name="CODAZIONE" value="<%=codAzione%>"/>

<%@ include file="Contatti_CommonScripts.inc" %>
<%
	if (canNuovoContatto) {
%>
  <p class="titolo">Nuovo Contatto</p>
  <font color="green">
  	<af:showMessages prefix="MSalvaContatto"/>
  </font>
  <%
  	if (!strTelContatti.equals("")) {
  %>
    <table>
    <tr><td align="left"><%=strTelContatti%>
    </td></tr>
    </table>
  <%
  	}
  			out.print(htmlStreamTop);
  %>
  <%@ include file="dett_contatto.inc" %> 
  <%
   	out.print(htmlStreamBottom);
   %>
  <p class="titolo">Contatti</p>
  <af:list moduleName="M_GetContattiLavoratore" jsSelect="DettaglioContatto"/>
  <p class="titolo">

  <%
  	if ((_pageProvenienza != null) && !_pageProvenienza.equals("") && !_pageProvenienza.equals("null")) {
  %>
      <input type="button" class="pulsanti" name="ANNULLA" value="Chiudi" onClick="javascript:conferma('BACK','<%=_pageProvenienza%>');">
  <%
  	}
  %>
  </p>
  <br>
<%
	} else {
%>
  <p class="titolo">Contatti</p>
  <af:list moduleName="M_GetContattiLavoratore" jsSelect="DettaglioContatto"/>
  <p class="titolo">
  <%
  	if ((_pageProvenienza != null) && !_pageProvenienza.equals("") && !_pageProvenienza.equals("null")) {
  %>
  		
      <input type="button" class="pulsanti" name="ANNULLA" value="Chiudi" onClick="javascript:conferma('BACK','<%=_pageProvenienza%>');">
  <%
  	}
  %>
  </p>
  <br>
<%
	}
%>
  <%
  	if (canvisualizzaTuttiContatti) {
  %>  		

      <center><input type="button" class="pulsanti" name="CaricaContatti" value="Visualizza Tutti Contatti" onClick="CercaTuttiContatti()"></center>
  <%
  	}
  %>
</af:form>
</body>
</html>