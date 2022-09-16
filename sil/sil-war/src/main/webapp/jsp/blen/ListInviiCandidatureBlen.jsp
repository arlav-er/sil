<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
com.engiweb.framework.dispatching.module.AbstractModule,
com.engiweb.framework.util.QueryExecutor,
it.eng.sil.security.User,it.eng.afExt.utils.*,
it.eng.sil.util.*,java.util.*,java.math.*,java.text.SimpleDateFormat,
java.io.*,com.engiweb.framework.error.EMFErrorSeverity,
com.engiweb.framework.error.*,
com.engiweb.framework.dbaccess.sql.TimestampDecorator,
it.eng.sil.security.PageAttribs,it.eng.sil.security.ProfileDataFilter,
com.engiweb.framework.security.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

 	String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect( request.getContextPath()+ "/servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
		
  final String FORMATO_DATA     = "dd/MM/yyyy";
  SimpleDateFormat df = new SimpleDateFormat(FORMATO_DATA);

	String codiceFiscale = (String) serviceRequest.getAttribute("CODICEFISCALE");
  String prgValidita = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PRGVALIDITA");

  BigDecimal numKloValidita = null;
  
  Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

  String modalita="NEW";     //Indica se nell'include VaidCur_elemetno.inc si deve bloccare o meno la select list
  String codTipoValiditaList      = "";
  String strDescValiditaList      = "";
  String datInizioCurrList = "";
  String datFineCurrList = "";

  String codTipoValidita      = "";
  String strDescValidita      = "";
  String datInizioCurr        = "";  
  String datFineCurr          = "";
  String strStatoCurriculum   = "";
  String codStatoLav          = "";
  BigDecimal cdnUtins = null;
  String dtmins = null;
  BigDecimal cdnUtmod = null;
  String dtmmod = null;
  Testata operatoreInfo = null;

  //Inizializzazione delle date
  datInizioCurr = DateUtils.getNow();
  GregorianCalendar dataFineCur = new GregorianCalendar();
    
  //Validità periodi
  
  SourceBean rowValidCurr = null;
  boolean periodoIsOk = false;
  String datInizioVC = "";
  String datFineVC = "";
  String tipologiaVal = "";
  String prgVal = ""; 
  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  
  PageAttribs attributi = new PageAttribs(user, _page);
  int _funzione = 1;
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  
%>

    <%
  // se la pagina e' in inserimento prendo come attributo di default conInsert (in futuro aggiungere canManage come da standard)
  //if (nuovo)  	canModify = canInsert;
  //Servono per gestire il layout
  
	SourceBean esisteCandidaturaValidaSb = (SourceBean) serviceResponse.getAttribute("M_BL_ESISTE_CANDIDATURA_VALIDA.ROWS.ROW");
	boolean esisteCandidaturaValida = !(esisteCandidaturaValidaSb == null || esisteCandidaturaValidaSb.getContainedAttributes().isEmpty());
	SourceBean candidatureRows= (SourceBean) serviceResponse.getAttribute("M_BL_LIST_INVII_CANDIDATURE.ROWS");
	
	Vector vCan=candidatureRows.getAttributeAsVector("ROW");
	
    boolean isCandidabile = (!esisteCandidaturaValida || vCan.isEmpty());
	
	boolean isButtVisible = attributi.containsButton("NUOVA");
	boolean canModify = isButtVisible && isCandidabile;
	
    boolean nuovo = true;
    boolean canInsert= false;
    boolean canDelete = false;
    
  String fieldReadOnly;
  
  if (canModify) {fieldReadOnly="false";}  else {fieldReadOnly="true";}
  
  String htmlStreamTopList    = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottomList = StyleUtils.roundBottomTable(canModify);
  String htmlStreamTop    = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>

<head>
<title>Lista delle candidature BLEN</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<script type="text/javascript" language="JavaScript"	src="../../js/layers.js" />

<af:linkScript path="../../js/"/>

  <script type="text/javascript">
    <%--@ include file="../patto/_sezioneDinamica_script.inc"--%>
    var flagChanged = false;
    var range = 0;

  // *****************************
    // Serve per il menu contestuale
    // *****************************
    function caricaMenuDX(cdnLavoratore, strNome, strCognome){

      var url = "AdapterHTTP?PAGE=MenuCompletoPage"
      url += "&CDNLAVORATORE=" + cdnLavoratore;
      url += "&STRNOME=" + strNome;
      url += "&STRCOGNOME=" + strCognome;
    
      window.top.menu.location = url;
    }

    function fieldChanged() {
      //alert("field changed !")      
      <% if ( canModify ) { %> 
        flagChanged = true;
      <% } %> 
    }
    
    function nuovaCandidatura() {
      <% if ( canModify ) {
    	  //nuova candidatura inviabile - faccio il post
    	  %> 
        document.forms['FormNuovaCandidatura'].submit();
      <%       } else {    
    	  //Altrimenti alert
    	  %> 
    	  alert('Non è possibile inserire una nuova candidatura perchè, alla data attuale, esiste già una candidatura valida');
      <%      }    	  %> 
    }


  
    function ValidCurSelect(prgValidita, numkloValidita) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      var s= "AdapterHTTP?PAGE=ValidCurPage";
      s += "&MODULE=M_ListTipoValid";
      s += "&SELDETAIL=TRUE";
      s += "&PRGVALIDITA=" + prgValidita;
      s += "&NUMKLOVALIDITA=" + numkloValidita;
      s += "&CDNLAVORATORE=" + <%= cdnLavoratore %>;
      s += "&CDNFUNZIONE=<%= _funzione %>";
      s += "&START_PAGE=<%= _page %>";
      s += "&APRIDIV=1";

      setWindowLocation(s);
    }

    function ValidCurDelete(prgValidita) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      if ( confirm("Eliminare l'oggetto selezionato?") ) {

        var s= "AdapterHTTP?PAGE=ValidCurPage";
        s += "&MODULE=M_DeleteValidCur";
        s += "&PRGVALIDITA=" + prgValidita;
        s += "&CDNLAVORATORE=" + <%= cdnLavoratore %>;
        s += "&CDNFUNZIONE=<%= _funzione %>";
        s += "&START_PAGE=<%= _page%>";

        setWindowLocation(s);
      }
    }

    function fieldChanged() {
    }

	function updateMain() {
		if (window.opener != null) {
			window.opener.top.main.location.reload();
		}
	}
  </script>

<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
 	 function dettaglioInvioBlen(prg) {
			// Apre la finestra
			var url = "AdapterHTTP?PAGE=BL_INVIO_CANDIDATURA&CODICEFISCALE=<%=codiceFiscale%>"+
			"&CDNLAVORATORE=<%=cdnLavoratore%>&TIPOLOGIA=1&prgcandidatura="+prg+"&popUp=true";
			window.location.assign(url);
		  }
  </script>
</head>

<body class="gestione">

	<%
    //String _page = (String) serviceRequest.getAttribute("PAGE"); 
    infCorrentiLav.setSkipLista(true);
    infCorrentiLav.show(out);     
    //l.show(out);
  %> 	
	<p align="center">
	<center>
		<af:showErrors />
	</center>

	<center>
		<p class="titolo">Lista delle candidature BLEN</p>
	</center>

	<div align="center">
		<af:list moduleName="M_BL_LIST_INVII_CANDIDATURE"
			canInsert="<%=canModify ? \"1\" : \"0\"%>" skipNavigationButton="1"
			jsSelect="dettaglioInvioBlen" />
		<%
	 if (isButtVisible){
	 %>
		<input type="button" class="pulsanti" value="Nuova Candidatura"
			onclick="nuovaCandidatura()" />
		<%	 }	 %>
		<input type="button" class="pulsanti" onClick="window.close();"
			value="Chiudi" />
	</div>
	<p />
		<%
	 if (canModify){
	 %>

		<af:form name="FormNuovaCandidatura" action="AdapterHTTP" method="post">
			<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>">
			<input type="hidden" name="CODICEFISCALE" value="<%=codiceFiscale%>">
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>">
			<input type="hidden" name="TIPOLOGIA" value="1">
			<input type="hidden" name="PAGE" value="BL_INVIO_CANDIDATURA">
			<input type="hidden" name="codTipoComunicazioneCl" value="01_BLEN">
		</af:form>
	<%		 
	 }
	 %>
	<!-- LAYER -->
	<%
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>
</body>
</html>
<% } %>
