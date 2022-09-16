<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.text.SimpleDateFormat,
  java.io.*,
  com.engiweb.framework.dbaccess.sql.TimestampDecorator,
  it.eng.sil.security.PageAttribs,
  it.eng.sil.security.ProfileDataFilter,
  com.engiweb.framework.security.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  final String FORMATO_DATA     = "dd/MM/yyyy";
  SimpleDateFormat df = new SimpleDateFormat(FORMATO_DATA);

  String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
  
  String prgValidita = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PRGVALIDITA");

  BigDecimal numKloValidita = null;
  
  Vector vectListaConoscenze = serviceResponse.getAttributeAsVector("M_LISTVALIDCUR.ROWS.ROW");
  
  Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

  String modalita="NEW";     //Indica se nell'include VaidCur_elemetno.inc si deve bloccare o meno la select list
  String codTipoValiditaList      = "";
  String strDescValiditaList      = "";
  String datInizioCurrList = "";
  String datFineCurrList = "";
  //TimestampDecorator datInizioCurrListTime ;  
  //TimestampDecorator datFineCurrListTime ;

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
  SourceBean numGg = (SourceBean)serviceResponse.getAttribute("M_GetNumGgCurr.ROWS.ROW");
  int numGPubb = Integer.parseInt(numGg.getAttribute("NUM").toString());

  SourceBean numMaxGg = (SourceBean)serviceResponse.getAttribute("M_GetMaxNumGgValCurr.ROWS.ROW");
  String numMaxGPubb = numMaxGg.getAttribute("NUM").toString();

  //Validità periodi
  Vector rows = serviceResponse.getAttributeAsVector("M_LoadAllValidCur.ROWS.ROW");
  SourceBean rowValidCurr = null;
  boolean periodoIsOk = false;
  String datInizioVC = "";
  String datFineVC = "";
  String tipologiaVal = "";
  String prgVal = "";
    

  dataFineCur.add(Calendar.DATE,numGPubb);
  datFineCurr = df.format(dataFineCur.getTime());
  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  String _page = (String) serviceRequest.getAttribute("START_PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  //Linguette l = new Linguette(user, 1, _page, new BigDecimal(cdnLavoratore));
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  

  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{

    //Linguette linguette = new Linguette( _funzione, _page, new BigDecimal(cdnLavoratore));
    // 13/04/2005 savino non veniva usata la page associata alla jsp ma qualla ottenuta dall'attributo "START_PAGE"
//    PageAttribs attributi = new PageAttribs(user, _page);
	PageAttribs attributi = new PageAttribs(user, (String)serviceRequest.getAttribute("page"));
    boolean canModify= attributi.containsButton("salva");
    boolean canDelete= attributi.containsButton("rimuovi");
    boolean canInsert = attributi.containsButton("inserisci");
    boolean nuovo = true;
    boolean flgErrori = false;
	boolean flgStatoOccupazModif = false;
	
    if ( !canModify && !canDelete && !canInsert) {
      
    } else {
      boolean canEdit = filter.canEditLavoratore();
      if ( !canEdit ) {
        canModify = false;
        canDelete = false;
        canInsert = false;
      }
    }

  if(serviceResponse.containsAttribute("M_LoadValidCur")) { nuovo = false; }
  else { nuovo = true; }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV") ;
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  apriDiv = (serviceRequest.containsAttribute("APRIDIV"))?"":"none";
  String url_nuovo = "AdapterHTTP?PAGE=ValidCurPage" +
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + _funzione +
                     "&APRIDIV=1" + 
                     "&START_PAGE=" + _page;

  if(serviceResponse.containsAttribute("M_CambiaStatoOccupaz.ERR_STATO_OCCUPAZ")) { 
	flgErrori = true;
  	apriDiv=""; 
  }

  if(serviceResponse.containsAttribute("M_CambiaStatoOccupaz.FLGSTATOOCCUPAZMODIFICATO") && 
  	 !serviceResponse.containsAttribute("M_CambiaStatoOccupaz.ERR_INSERT_CURR") && !flgErrori) {
	flgStatoOccupazModif = true;
  }  

  if(!nuovo)
  {
    SourceBean row = (SourceBean) serviceResponse.getAttribute("M_LoadValidCur.ROWS.ROW");
    codTipoValidita  = (String)row.getAttribute("CODTIPOVALIDITA");
    datInizioCurr    = (String)row.getAttribute("DATINIZIOCURR");
    datFineCurr      = (String)row.getAttribute("DATFINECURR");
    strDescValidita  = (String)row.getAttribute("STRDESCVALIDITA");    
    codStatoLav     =  (String)row.getAttribute("CODSTATOLAV"); 
    cdnUtins = (BigDecimal) row.getAttribute("cdnUtins");
    dtmins = StringUtils.getAttributeStrNotNull(row, "dtmins");
    cdnUtmod = (BigDecimal) row.getAttribute("cdnUtmod");
    dtmmod = StringUtils.getAttributeStrNotNull(row, "dtmmod");
    numKloValidita = (BigDecimal)  row.getAttribute("numKloValidita");
    operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
 }
  
  if (flgErrori) {
  	SourceBean row = null;
	if (serviceResponse.containsAttribute("M_CambiaStatoOccupaz")) {
		row = (SourceBean)serviceResponse.getAttribute("M_CambiaStatoOccupaz");
		//mettere if
		if (serviceRequest.containsAttribute("inserisci")) {
			nuovo = true;
		}
		else {
			if (serviceRequest.containsAttribute("SALVA")) {
				nuovo = false;
			}
		}
	}	
	
	if (row!=null) {
	    codTipoValidita  = (String)row.getAttribute("CODTIPOVALIDITA");
	    datInizioCurr    = (String)row.getAttribute("DATINIZIOCURR");
	    datFineCurr      = (String)row.getAttribute("DATFINECURR");
	    codStatoLav     =  (String)row.getAttribute("CODSTATOLAV"); 
    }
  	
  }
  // se la pagina e' in inserimento prendo come attributo di default conInsert (in futuro aggiungere canManage come da standard)
  if (nuovo)
  	canModify = canInsert;
  //Servono per gestire il layout
  String htmlStreamTopList    = StyleUtils.roundTopTable(false);
  String htmlStreamBottomList = StyleUtils.roundBottomTable(false);
  String htmlStreamTop    = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

  String fieldReadOnly;
  
  if (canModify) {fieldReadOnly="false";}
  else {fieldReadOnly="true";}
%>

<html>

<head>
  <title>Validit&agrave; Curriculum</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>

  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">
    <%@ include file="../patto/_sezioneDinamica_script.inc"%>
    var flagChanged = false;
    var range = <%=numMaxGPubb%>;

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
	
	function controllaPeriodo(){
		var datIn;
		var datFine;
		var datInVec;
		var datFineVec;
		var isOk = true;
		var tipologiaValidInsert = document.Frm1.CODTIPOVALIDITA.value
		var tipValCurr;
		
	    if (document.Frm1.DATINIZIOCURR.value != "") {
	      dataI = new String(document.Frm1.DATINIZIOCURR.value);
	      annoDataI = dataI.substr(6,4);
	      meseDataI = dataI.substr(3,2);
	      giornoDataI = dataI.substr(0,2);
	    }	
	    if (document.Frm1.DATFINECURR.value != "") {
	      dataF = new String(document.Frm1.DATFINECURR.value);
	      annoDataF = dataF.substr(6,4);
	      meseDataF = dataF.substr(3,2);
	      giornoDataF = dataF.substr(0,2);
	    }
	    datI = new Date(annoDataI, meseDataI - 1, giornoDataI);
		datF = new Date(annoDataF, meseDataF - 1, giornoDataF);
		
<%
		for(int i= 0; i < rows.size(); i++)  { 
			  rowValidCurr  = (SourceBean) rows.elementAt(i);    
		
			  datInizioVC = "" + Utils.notNull(rowValidCurr.getAttribute("datiniziocurr"));
			  datFineVC = "" + Utils.notNull(rowValidCurr.getAttribute("datfinecurr"));
			  tipologiaVal = "" + Utils.notNull(rowValidCurr.getAttribute("CODTIPOVALIDITA"));
			  prgVal = "" + Utils.notNull(rowValidCurr.getAttribute("PRGVALIDITA"));
			  
			  if(!prgVal.equals(prgValidita)){
%>
				  datInVec = '<%=datInizioVC%>';
				  datFineVec = '<%=datFineVC%>';
				  tipValCurr = '<%=tipologiaVal%>';
				  
				  if (datInVec != "") {
				      dataI = new String(datInVec);
				      annoDataI = dataI.substr(6,4);
				      meseDataI = dataI.substr(3,2);
				      giornoDataI = dataI.substr(0,2);
				  }	
				  if (datFineVec != "") {
				      dataF = new String(datFineVec);
				      annoDataF = dataF.substr(6,4);
				      meseDataF = dataF.substr(3,2);
				      giornoDataF = dataF.substr(0,2);
				  }
				  datIn = new Date(annoDataI, meseDataI - 1, giornoDataI);
				  datFine = new Date(annoDataF, meseDataF - 1, giornoDataF);
	
				  if(tipologiaValidInsert == tipValCurr){
					  if((datI >= datIn && datI <= datFine) || (datF >= datIn && datF <= datFine)){
					  		isOk = false;
					  }
				  }
<%			  }
		}
%>
		if(isOk == false) alert("A parità di lavoratore e di tipologia di validità" + 
								"\ni periodi di durata validità del curriculum non devono intersecarsi");
		
		return isOk;
	}
	
  </SCRIPT>
  <% if ( canModify ) { %>
  	<%@ include file="validCur_CommonScripts.inc" %>
  <%}%>
  
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
  </script>
</head>

<body class="gestione" <%=(flgStatoOccupazModif ? "onunload='updateMain();'" : "")%> >  

  <%
    //String _page = (String) serviceRequest.getAttribute("PAGE"); 
    infCorrentiLav.setSkipLista(true);
    infCorrentiLav.show(out);     
    //l.show(out);
  %>
 <p align="center">
 <center>
 <af:showErrors/>
 <af:showMessages prefix="M_CambiaStatoOccupaz"/>
 <af:showMessages prefix="M_DeleteValidCur"/>
 </center>
 
 <div align="center">
  <af:list moduleName="M_ListValidCur" skipNavigationButton="1"
         canDelete="<%=canDelete ? \"1\" : \"0\"%>"  
         jsSelect="ValidCurSelect" jsDelete="ValidCurDelete"/>

  <%if (flgStatoOccupazModif) {%>
	<SCRIPT TYPE="text/javascript">
	 	var msg = "Stato occupazionale cambiato:\r\n";
		  	msg += "Occupato -> In cerca di altra occupazione";
	  	alert(msg);
	</SCRIPT>
  
  <%}%>
  
  <%if(canInsert) {%>
        <br>
        <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuova validità"/>        
  <%}%>
  <input type="button" class="pulsanti" onClick="window.close();" value="Chiudi"/>       
</div>
 <p/>

<!-- LAYER -->
<%
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>
  <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
   style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
  <!-- Stondature ELEMENTO TOP -->
  <%out.print(divStreamTop);%>

    <table width="100%">
      <tr>
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">
        <%if(nuovo){%>
          Nuova validità
        <%} else {%>
          Validità
        <%}%>   
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>

  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaDate() && controllaDateRange(range) && controllaPeriodo()">

    <input type="hidden" name="PAGE" value="ValidCurPage">
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>    
    <input type="hidden" name="CDNUTINS" value="<%= codiceUtente %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtente %>"/>
    <input type="hidden" name="START_PAGE" value="<%= _page %>"/>
	<input type="hidden" name="NUMKLOVALIDITA" value="<%= numKloValidita %>"/>
	<input type="hidden" name="PRGVALIDITA" value="<%= prgValidita %>"/>
	
    <!--<p class="titolo">Formazione Professionale</p>-->

    <table align="center" width="100%">
      <%@ include file="ValidCur_Elemento.inc" %>
      <tr><td><br/></td></tr>
      <%if(nuovo) {%>
      <tr>
        <td colspan="2" align="center">
    	<input type="submit" class="pulsanti" name="inserisci" value="Inserisci">        
        <input type="button" class="pulsanti" name="chiudi" value="Chiudi senza inserire" onClick="ChiudiDivLayer('divLayerDett')">
        </td>
      </tr>
      <% } else { %>
      <tr>
        <td colspan="2" align="center">
          <% if ( canModify ) { %>
    		<input type="submit" class="pulsanti" name="salva" value="Aggiorna">
          <% } %>
            <input type="button" 
              class="pulsanti" 
              name="annulla" 
              value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
              onClick="ChiudiDivLayer('divLayerDett')">
        </td>
      </tr>
      <%}%>
    </table>
    <table>
	  <tr><td colspan="2" align="center">
		<%if (!nuovo && operatoreInfo!=null) operatoreInfo.showHTML(out);%>
	  </td></tr>    
    </table>
  </af:form>

  <% if (flgErrori) {%>
    	<af:form method="POST" action="AdapterHTTP" name="Frm2" onSubmit="controllaDate() && controllaDateRange(range)">

	    <input type="hidden" name="PAGE" value="ValidCurPage">
	    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
	    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>    
	    <input type="hidden" name="CDNUTINS" value="<%= codiceUtente %>"/>
	    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtente %>"/>
	    <input type="hidden" name="START_PAGE" value="<%= _page %>"/>
	    <input type="hidden" name="CODTIPOVALIDITA" value="<%= codTipoValidita %>"/>
	    <input type="hidden" name="DATINIZIOCURR" value="<%= datInizioCurr %>"/>
	    <input type="hidden" name="DATFINECURR" value="<%= datFineCurr %>"/>
	    <input type="hidden" name="CODSTATOLAV" value="<%= codStatoLav %>"/>
	    <input type="hidden" name="NO_CAMBIO_STATO_OCCUPAZ" value=""/>	    
	<%if (nuovo) { %>
    	<input type="hidden" class="pulsanti" name="inserisci" value="Inserisci">        
    <%} else {%>
   		<input type="hidden" class="pulsanti" name="salva" value="Aggiorna">
   	<%}%>
		 <SCRIPT TYPE="text/javascript">
		  	var msg = "Impossibile modificare lo stato occupazionale del lavoratore.\r\n";
		  	msg += "Sarà comunque possibile inserire/modificare la validità del curriculum\r\n";
		  	msg += "senza modificare lo stato occupazionale.\r\n\r\n";
		  	msg += "Procedere con l'operazione?";
		  	//alert(msg);
		  	if (confirm(msg)) {
		  		doFormSubmit(document.Frm2);
		  	} else {
		  		var new_url = "AdapterHTTP?PAGE=ValidCurPage";
		  			new_url+= "&CDNLAVORATORE=" + <%=cdnLavoratore%>;
					new_url+= "&CDNFUNZIONE=" + <%=_funzione%>;
					new_url+= "&MODULE=M_ListValidCur";
					new_url+= "&START_PAGE=AnagDettaglioPageAnag";
	
				setWindowLocation(new_url);
		  	}
		 </SCRIPT>

  	</af:form>
  <%}%> 
    <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>
   
  </div>
</body>

</html>

<% } %>