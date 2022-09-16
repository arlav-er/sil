<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.User,
  com.engiweb.framework.util.JavaScript,
  com.engiweb.framework.security.*" %>
  
 <%! private String getQueryString(SourceBean _serviceRequest) {
		StringBuffer queryStringBuffer = new StringBuffer();
		Vector queryParameters = _serviceRequest.getContainedAttributes();
		for (int i = 0; i < queryParameters.size(); i++) {
			SourceBeanAttribute parameter = (SourceBeanAttribute) queryParameters.get(i);
			String parameterKey = parameter.getKey();
			if ( parameterKey.equalsIgnoreCase("PAGE")
				|| parameterKey.equalsIgnoreCase("MODULE")
				|| parameterKey.equalsIgnoreCase("MESSAGE")
				|| parameterKey.equalsIgnoreCase("LIST_PAGE")
				|| parameterKey.equalsIgnoreCase("LIST_NOCACHE"))
				continue;
			String parameterValue = parameter.getValue().toString();
			queryStringBuffer.append(JavaScript.escape(parameterKey.toUpperCase()));
			queryStringBuffer.append("=");
			queryStringBuffer.append(JavaScript.escape(parameterValue));
			queryStringBuffer.append("&");
		} // for (int i = 0; i < queryParameters.size(); i++)
		return queryStringBuffer.toString();
	} // private String getQueryString()
	%>  
  
  
<%

    String _page = "ScadListaPage";
	String queryString = "PAGE=" + _page + "&" + getQueryString(serviceRequest)
	+ "MESSAGE=LIST_PAGE&LIST_PAGE=" + serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.CURRENT_PAGE");

	//Profilatura    
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);	
    PageAttribs attributi = new PageAttribs(user, _page);
   

	boolean canPrint = false;
	
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
		canPrint = attributi.containsButton("StampaLavRicontattare");                
    }
      

////////////// Memorizzo il token in sessione per poter tornare indietro dalle pagine richiamate
		sessionContainer.setAttribute("_TOKEN_" + _page.toUpperCase(), "PAGE=" + _page + "&" + getQueryString(serviceRequest)
			+ "MESSAGE=LIST_PAGE&LIST_PAGE=" + serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.CURRENT_PAGE") );	

		
		sessionContainer.setAttribute("_BACKPAGE_",queryString);
		sessionContainer.setAttribute("_BACKURL_",queryString);
	


String _pageProvenienza = serviceRequest.containsAttribute("PAGEPROVENIENZA")? serviceRequest.getAttribute("PAGEPROVENIENZA").toString():"";
if (_pageProvenienza.equals("")) {
  _pageProvenienza = "ScadListaPage";
}
String data_al = serviceRequest.containsAttribute("DATAAL")? serviceRequest.getAttribute("DATAAL").toString():"";
String data_dal = serviceRequest.containsAttribute("DATADAL")? serviceRequest.getAttribute("DATADAL").toString():"";
String strDirezione = serviceRequest.containsAttribute("DIREZIONE")? serviceRequest.getAttribute("DIREZIONE").toString():"";

String strCodTipoVal = serviceRequest.containsAttribute("CODTIPOVALIDITA")? serviceRequest.getAttribute("CODTIPOVALIDITA").toString():"";
String codificaPatto = serviceRequest.containsAttribute("TIPOLOGIAPATTO")? serviceRequest.getAttribute("TIPOLOGIAPATTO").toString():"";

String OpzioneCV = serviceRequest.containsAttribute("OpzioneCV")? serviceRequest.getAttribute("OpzioneCV").toString():"";
String statoValCV = serviceRequest.containsAttribute("statoValCV")? serviceRequest.getAttribute("statoValCV").toString():"";

String motivo_contatto = serviceRequest.containsAttribute("MOTIVO_CONTATTO")? serviceRequest.getAttribute("MOTIVO_CONTATTO").toString():"";


String vettIntestazioniBottoniComuni[]=new String[2];
String vettIconeBottoniComuni[]=new String[2];
String vettAzioniBottoniComuni[]=new String[2];

vettIntestazioniBottoniComuni[0] = "";
vettIconeBottoniComuni[0] = "";
vettAzioniBottoniComuni[0] = "";

vettIntestazioniBottoniComuni[1] = "";
vettIconeBottoniComuni[1] = "";
vettAzioniBottoniComuni[1] = "";



int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();
boolean isCpi = cdnTipoGruppo == 1;


String codCpi = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
String strCodScadenza = serviceRequest.containsAttribute("SCADENZIARIO")? serviceRequest.getAttribute("SCADENZIARIO").toString():"";
String strFiltra = serviceRequest.containsAttribute("FILTRALISTA")? serviceRequest.getAttribute("FILTRALISTA").toString():"0";
String codCpiApp = serviceRequest.containsAttribute("codCpiApp")? serviceRequest.getAttribute("codCpiApp").toString():"";

String strVisibleApp = "";
String strVisibleCont = "";
String strVisibleEsiti = "";
String strVisibleColloqui = "";
String strVisibleScadenza = "";
String strVisibleVerifiche = "";
String strHtmlLista = "";
int nDim=0;
int nDimBottoniComuni=0;
String strTitoloLista="";


String numeroPagine;
String numeroRecords;
%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%@ include file="CommonFunction_Scadenziario.inc" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>  
  <%@ include file="../documenti/_apriGestioneDoc.inc"%>
  
  <title>Scadenziario</title>
  <script language="Javascript">
  
    function ApriSchedaAzienda (codazienda,codunita) {
      document.frmListaScadenziario.PAGE.value = "ScadTestataAziendaPage";
      document.frmListaScadenziario.PRGAZIENDA.value = codazienda;
      document.frmListaScadenziario.PRGUNITA.value = codunita;
      doFormSubmit(document.frmListaScadenziario);
    }
  
    function ApriSchedaLavoratore (cdnLavoratore) {

        url =  "AdapterHTTP?PAGE=AnagDettaglioPageAnag";		
 		url += "&CDNFUNZIONE=1" ;
 		url += "&MODULE=M_GetLavoratoreAnag";
 		url += "&CDNLAVORATORE="+ cdnLavoratore; 
		url += "&skipLista=false"; 					
 		setWindowLocation(url);

 		      
        /*var s= "AdapterHTTP?PAGE=AmstrInfoCorrentiPage";
        s += "&cdnLavoratore=" + cdnLavoratore;
        s += "&APRI_EV=1";
        window.open (s,"InfoCorrenti", "toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES");*/
    }

    function ApriContattoLavoratore (cdnLavoratore,datascadenza) {
      document.frmListaScadenziario.PAGE.value = "ScadContattoPage";
      document.frmListaScadenziario.CDNLAVORATORE.value = cdnLavoratore;
      document.frmListaScadenziario.DATASCADENZA.value = datascadenza;
      document.frmListaScadenziario.RECUPERAINFO.value = "LAVORATORE";   
      document.frmListaScadenziario.LIST_PAGE_SCAD.value = <%=serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.CURRENT_PAGE")%>;
      document.frmListaScadenziario.MESSAGE_SCAD.value = "LIST_PAGE";
      doFormSubmit(document.frmListaScadenziario);
    }

    function ApriAppuntamentoLavoratore (cdnLavoratore,datascadenza) {
      document.frmListaScadenziario.PAGE.value = "ScadAppuntamentoPage";  
      document.frmListaScadenziario.CDNLAVORATORE.value = cdnLavoratore;
      document.frmListaScadenziario.DATASCADENZA.value = datascadenza;
      document.frmListaScadenziario.LIST_PAGE_SCAD.value = <%=serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.CURRENT_PAGE")%>;
      document.frmListaScadenziario.MESSAGE_SCAD.value = "LIST_PAGE";
      doFormSubmit(document.frmListaScadenziario);
    }

    function ApriContattoAzienda (prgazienda, prgunita ,datascadenza) {
      document.frmListaScadenziario.PAGE.value = "ScadContattoPage";
      document.frmListaScadenziario.PRGAZIENDA.value = prgazienda;
      document.frmListaScadenziario.PRGUNITA.value = prgunita;
      document.frmListaScadenziario.DATASCADENZA.value = datascadenza;
      document.frmListaScadenziario.RECUPERAINFO.value = "AZIENDA";
      document.frmListaScadenziario.LIST_PAGE_SCAD.value = <%=serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.CURRENT_PAGE")%>;
      document.frmListaScadenziario.MESSAGE_SCAD.value = "LIST_PAGE";
      doFormSubmit(document.frmListaScadenziario);
    }

    function ApriColloquiLavoratore (cdnLavoratore,datascadenza) {
      window.open("AdapterHTTP?PAGE=ScadColloquiPage&CDNLAVORATORE="+cdnLavoratore+"&CODCPI=<%=codCpi%>",'','toolbar=0,scrollbars=1,width=480,height=350');
    }

    function ApriAppuntamentoAzienda (prgazienda, prgunita ,datascadenza) {
      document.frmListaScadenziario.PAGE.value = "ScadAppuntamentoPage";
      document.frmListaScadenziario.PRGAZIENDA.value = prgazienda;
      document.frmListaScadenziario.PRGUNITA.value = prgunita;
      document.frmListaScadenziario.DATASCADENZA.value = datascadenza;
      document.frmListaScadenziario.LIST_PAGE_SCAD.value = <%=serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.CURRENT_PAGE")%>;
      document.frmListaScadenziario.MESSAGE_SCAD.value = "LIST_PAGE";
      doFormSubmit(document.frmListaScadenziario);
    }

    function ApriScadenzeLavoratore (cdnLavoratore, codcpi) {
      window.open("AdapterHTTP?PAGE=ScadenzeLavoratorePage&CDNLAVORATORE="+cdnLavoratore+"&CODCPI="+codcpi,'','toolbar=0,scrollbars=1');
    }

    function ApriVerificheLavoratore (cdnLavoratore, codcpi) {
      window.open("AdapterHTTP?PAGE=VerificheLavoratorePage&CDNLAVORATORE="+cdnLavoratore+"&CODCPI="+codcpi+"&POPUP=true&ISCPI=",'','toolbar=0,scrollbars=1');
    }

    function TornaAllaRicerca() {
      document.frmListaScadenziario.PAGE.value = "ScadRicercaPage";
      doFormSubmit(document.frmListaScadenziario);  
    }
    
    function TornaAlleVerifiche() {
      document.frmListaScadenziario.PAGE.value = "VerLavoratoriPage";
      doFormSubmit(document.frmListaScadenziario);  
    }

    function FiltraNew (objFiltro) {
      document.frmListaScadenziario.FILTRALISTA.value = objFiltro.value;
      document.frmListaScadenziario.PAGE.value = "ScadListaPage";
      doFormSubmit(document.frmListaScadenziario);
    }
    

    function SelezionaRighe() {
      if (document.frmListaScadenziario.rigaScadenza.length > 0) {
        for (jRiga=0;jRiga < document.frmListaScadenziario.rigaScadenza.length; jRiga++) {
          document.frmListaScadenziario.rigaScadenza[jRiga].checked = document.frmListaScadenziario.selAllRighe.checked;
        }
      }
      else {
        document.frmListaScadenziario.rigaScadenza.checked = document.frmListaScadenziario.selAllRighe.checked;
      }
    }
	
	//Gestione paginazione della lista
	var numRighe = 15;
    var totalePagine = <%=serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.NUM_PAGES")%>;
    var posizioneAttuale = <%=serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.CURRENT_PAGE")%>;
    
    function calcolaPrecedente() {
      if (posizioneAttuale <= 1 ) {return 1;}
      else return posizioneAttuale - 1;
    }

    function calcolaSuccessiva() {
     // if (posizioneAttuale >= totalePagine ) {return totalePagine;}
     // else return posizioneAttuale + 1;
      if (totalePagine==-1)  
	       return posizioneAttuale + 1;
	            
      if (posizioneAttuale >= totalePagine ) {
      	return totalePagine;
      }      
    }	
    
    //Effettua la navigazione tra le pagine
    function naviga(listpage, message) {
      if (listpage != posizioneAttuale) {
	      document.FormListaNavigazione.LIST_PAGE.value = listpage;
	      document.FormListaNavigazione.MESSAGE.value = message;
	      doFormSubmit(document.FormListaNavigazione);
      }
	}	
	//Fine gestione paginazione della lista
    
    
    //Stampa Lavoratore Ricontattare
    
    function StampaLavRicontattare(){
    
    	var queryString = "&codCpi=<%=codCpi%>&dataal=<%=data_al%>&datadal=<%=data_dal%>&strFiltra=<%=strFiltra%>&SCADENZIARIO=<%=strCodScadenza%>&MOTIVO_CONTATTO=<%=motivo_contatto%>&"; 
    	apriGestioneDoc('RPT_STAMPA_LAV_RICONTATTARE',queryString,'ST_TNT');
    	
	}
    
    
    
  </script>
</head>
<body class="gestione" onload="rinfresca()">
<%
	String attr   = null;
	String valore = null;  
	String txtOut = "";
	SourceBean rowMotivo = null;
	String strDescMotivoContatto = "";
	String strCodiceContatto = "";
	SourceBean row = (SourceBean) serviceResponse.getAttribute("M_GETTIPISCADENZE.ROWS.ROW");
	if (row!=null){
		strTitoloLista = StringUtils.getAttributeStrNotNull(row,"STRDESCRIZIONE");
		strVisibleApp = StringUtils.getAttributeStrNotNull(row,"FLGAZIONEAPPUNTAMENTO").toUpperCase();
		strVisibleCont = StringUtils.getAttributeStrNotNull(row,"FLGAZIONECONTATTO").toUpperCase();
		strVisibleEsiti = StringUtils.getAttributeStrNotNull(row,"FLGAZIONEESITI").toUpperCase();
		strVisibleColloqui = StringUtils.getAttributeStrNotNull(row,"FLGVISTACOLLOQUI").toUpperCase();
		strVisibleScadenza = StringUtils.getAttributeStrNotNull(row,"FLGSCADENZA").toUpperCase();
		strVisibleVerifiche = StringUtils.getAttributeStrNotNull(row,"FLGVERIFICA").toUpperCase();
	}
	
	String descrizioneTipologiaPatto = "";
	if (!codificaPatto.equals("")) {
		Vector rowCodificaPatto  = serviceResponse.containsAttribute("M_GetCodificaTipoPatto.ROWS.ROW")?
			serviceResponse.getAttributeAsVector("M_GetCodificaTipoPatto.ROWS.ROW"):null;
		SourceBean row_CurrPatto = null;
		if (rowCodificaPatto != null) {
			for(int indexPatto=0; (indexPatto < rowCodificaPatto.size() && descrizioneTipologiaPatto.equals("")); indexPatto++)  { 
				row_CurrPatto = (SourceBean) rowCodificaPatto.elementAt(indexPatto);
				if (codificaPatto.equalsIgnoreCase(row_CurrPatto.getAttribute("CODICE").toString())) {
					descrizioneTipologiaPatto = row_CurrPatto.getAttribute("descrizioneDB").toString();
				}
			}
		}	
	}
	
	
%>
<center>
<p class="titolo"><%=strTitoloLista%></p>
<%
int numRecords = (((Integer) serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.NUM_RECORDS")) != null)?((Integer) serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.NUM_RECORDS")).intValue():0;
if(numRecords > 0) {

%>
<!--Form per la navigazione tra le pagine-->
    <af:form name="FormListaNavigazione" method="POST" action="AdapterHTTP">
      <!--Salvo i parametri per la prossima ricerca sulla navigazione delle pagine-->
      <input type="hidden" name="LIST_PAGE" value=""/>
      <input type="hidden" name="MESSAGE" value=""/>
    <% if (serviceRequest.containsAttribute("DATAAL")) { %>
      <input type="hidden" name="DATAAL" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "DATAAL")%>"/>
    <% } %>
    <% if (serviceRequest.containsAttribute("DATADAL")) { %>    
      <input type="hidden" name="DATADAL" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "DATADAL")%>"/>      
    <% } %>
     <% if (serviceRequest.containsAttribute("MOTIVO_CONTATTO")) { %>    
      <input type="hidden" name="MOTIVO_CONTATTO" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "MOTIVO_CONTATTO")%>"/>      
    <% } %>
    <% if (serviceRequest.containsAttribute("FILTRALISTA")) { %>        
      <input type="hidden" name="FILTRALISTA" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "FILTRALISTA")%>"/>      
    <% } %>      
    <% if (serviceRequest.containsAttribute("OpzioneCV")) { %>    
      <input type="hidden" name="OpzioneCV" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "OpzioneCV")%>"/>      
    <% } %>
    <% if (serviceRequest.containsAttribute("statoValCV")) { %>    
      <input type="hidden" name="statoValCV" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "statoValCV")%>"/>      
    <% } %>
    <% if (serviceRequest.containsAttribute("codCpiApp")) { %>    
      <input type="hidden" name="codCpiApp" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest, "codCpiApp")%>"/>      
    <% } %>
      <input type="hidden" name="PAGE" value="<%=serviceRequest.getAttribute("PAGE")%>"/>  
      <input type="hidden" name="CDNFUNZIONE" value="<%=serviceRequest.getAttribute("CDNFUNZIONE")%>"/>  
      <input type="hidden" name="CODCPI" value="<%=serviceRequest.getAttribute("CODCPI")%>"/>  
	  <!--Questo parametro è utile affinchè si possa eseguire il modulo M_LISTSCADENZE quando si naviga fra le pagine-->
      <input type="hidden" name="ISCPI" value=""/>  
      <input type="hidden" name="CODTIPOVALIDITA" value="<%=strCodTipoVal%>">
      <input type="hidden" name="SCADENZIARIO" value="<%=serviceRequest.getAttribute("SCADENZIARIO")%>"/>  

      <!--pulsanti di navigazione-->
        <img src="../../img/list_first.gif" alt="Prima pagina" onclick="naviga(1, 'LIST_PAGE');" />
   		&nbsp;
        <img src="../../img/list_prev.gif" alt="Pagina precedente" onclick="naviga(calcolaPrecedente(), 'LIST_PAGE');" />
		&nbsp;
        <img src="../../img/list_next.gif" alt="Pagina successiva" onclick="naviga(calcolaSuccessiva(), 'LIST_PAGE');" />
		&nbsp;
        <img src="../../img/list_last.gif" alt="Ultima pagina" onclick="naviga(<%=serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.NUM_PAGES")%>, 'LIST_PAGE');" />
      	<br>
    </af:form>
    <% 
       int currentpage = ((Integer) serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.CURRENT_PAGE")).intValue();
       int numpages = ((Integer) serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.NUM_PAGES")).intValue();
       int numrecords = ((Integer) serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.NUM_RECORDS")).intValue();
       int rowsxpage = ((Integer) serviceResponse.getAttribute("M_LISTSCADENZE.ROWS.ROWS_X_PAGE")).intValue();
       int numeroda = ((currentpage - 1) * rowsxpage) +1;
       int numeroa = (currentpage == numpages ? numrecords : currentpage * rowsxpage);
       
       if(numpages != -1){
			numeroPagine= " di " + numpages;
			numeroRecords=" di " + numrecords;
		}else{
			numeroPagine= "";
			numeroRecords="";
		}
    %>
    <p class="titolo"><b>Pag. <%=currentpage%><%=numeroPagine%> (da <%=numeroda%> a <%=numeroa%><%=numeroRecords%>)</b></p>
<!--Fine Form per la navigazione tra le pagine-->
<%}%>
</center>
<af:form name="frmListaScadenziario" action="AdapterHTTP" method="POST">
<input type="hidden" name="PAGE" value="">
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="DATAAL" value="<%=data_al%>">
<input type="hidden" name="DATADAL" value="<%=data_dal%>">
<input type="hidden" name="DIREZIONE" value="<%=strDirezione%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="CDNLAVORATORE" value="">
<input type="hidden" name="PRGAZIENDA" value="">
<input type="hidden" name="PRGUNITA" value="">
<input type="hidden" name="RECUPERAINFO" value="">
<input type="hidden" name="DATASCADENZA" value="">
<input type="hidden" name="CODCPI" value="<%=codCpi%>"> 
<input type="hidden" name="codCpiApp" value="<%=codCpiApp%>"/>
<input type="hidden" name="CODTIPOVALIDITA" value="<%=strCodTipoVal%>">
<input type="hidden" name="TIPOLOGIAPATTO" value="<%=codificaPatto%>">
<input type="hidden" name="LIST_PAGE_SCAD" value=""/>
<input type="hidden" name="MESSAGE_SCAD" value=""/>
<input type="hidden" name="OpzioneCV" value="<%=OpzioneCV%>"/>
<input type="hidden" name="statoValCV" value="<%=statoValCV%>"/>


<%
int nCodScadenza = 0;

if (strCodScadenza.compareTo("ORG1") == 0) {
  nCodScadenza = 1;
} else if (strCodScadenza.compareTo("ORG2") == 0) {
    nCodScadenza = 2;
} else if (strCodScadenza.compareTo("ORG3") == 0) {
    nCodScadenza = 3;
} else if (strCodScadenza.compareTo("ORG4") == 0) {
    nCodScadenza = 4;
} else if (strCodScadenza.compareTo("AMM1") == 0) {
    nCodScadenza = 101;
} else if (strCodScadenza.compareTo("AMM2") == 0) {
    nCodScadenza = 102;
} else if (strCodScadenza.compareTo("AMM3") == 0) {
    nCodScadenza = 103;
} else if (strCodScadenza.compareTo("AMM4") == 0) {
    nCodScadenza = 104;
} else if (strCodScadenza.compareTo("VER1") == 0) {
    nCodScadenza = 1001;
} else if (strCodScadenza.compareTo("VER2") == 0) {
    nCodScadenza = 1002;
} else  if (strCodScadenza.compareTo("VER3") == 0) {
    nCodScadenza = 1003;
}  else if (strCodScadenza.compareTo("VER4") == 0) {
	nCodScadenza=1004;
}else if (strCodScadenza.compareTo("VER5")==0){
	nCodScadenza=5;
}

//vettore intestazioni dei bottoni da aggiungere alla fine
String vettIntestazioniBottoni[]=new String[3];
//vettore delle icone dei bottoni
String vettIconeBottoni[]=new String[3];
//vettore delle azioni dei bottoni
String vettAzioniBottoni[]=new String[3];

vettIntestazioniBottoni[0]="";
vettIntestazioniBottoni[1]="";
vettIntestazioniBottoni[2]="";

vettIconeBottoni[0]="";
vettIconeBottoni[1]="";
vettIconeBottoni[2]="";

vettAzioniBottoni[0]="";
vettAzioniBottoni[1]="";
vettAzioniBottoni[2]="";

if (strVisibleScadenza.compareTo("S") == 0) {
  vettIntestazioniBottoniComuni[nDimBottoniComuni] = "Scadenze";
  vettIconeBottoniComuni[nDimBottoniComuni] = "../../img/scadenze.gif";
  vettAzioniBottoniComuni[nDimBottoniComuni] = "javascript:ApriScadenzeLavoratore";
  nDimBottoniComuni = nDimBottoniComuni + 1;
}

if (strVisibleVerifiche.compareTo("S") == 0) {
  vettIntestazioniBottoniComuni[nDimBottoniComuni] = "Verifiche";
  vettIconeBottoniComuni[nDimBottoniComuni] = "../../img/verifiche.gif";
  vettAzioniBottoniComuni[nDimBottoniComuni] = "javascript:ApriVerificheLavoratore";
  nDimBottoniComuni = nDimBottoniComuni + 1;
}


SourceBean rowScadenze = null;
SourceBean contScadenze = (SourceBean) serviceResponse.getAttribute("M_LISTSCADENZE");
Vector rows_VectorScadenze = null;
rows_VectorScadenze = (contScadenze != null)?contScadenze.getAttributeAsVector("ROWS.ROW"):new Vector();
switch(nCodScadenza) {
  case 1: 
    rowMotivo = (SourceBean) serviceResponse.getAttribute("COMBO_MOTIVO_CONTATTO_AG_DETTAGLIO.ROWS.ROW");
    if (rowMotivo != null) {
      strCodiceContatto = rowMotivo.containsAttribute("CODICE")? rowMotivo.getAttribute("CODICE").toString():"";
      strDescMotivoContatto = rowMotivo.containsAttribute("DESCRIZIONE")? rowMotivo.getAttribute("DESCRIZIONE").toString():"";
    }
    
    if (data_dal != null && !data_dal.equals("")) {
      txtOut += "Data Dal <strong>"+ data_dal +"</strong>; ";
    }
    if (data_al != null && !data_al.equals("")) {
      txtOut += "Data Al <strong>"+ data_al +"</strong>; ";
    }
    if (strDescMotivoContatto != null && !strDescMotivoContatto.equals("")) {
      txtOut += "Motivo contatto <strong>"+ strDescMotivoContatto +"</strong>; ";
    }
    %>
    <p align="center">
    <%
    if (txtOut.length() > 0) { 
      txtOut = "Filtri di ricerca:<br/> " + txtOut;
    %>
      <table cellpadding="2" cellspacing="10" border="0" width="100%">
      <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%
      out.print(txtOut);
      %>
      </td></tr>
      </table>
    <%
    }
      
    //vettore delle intestazioni della lista
    String vettIntestazioni1[]=new String[9];
    //vettore delle colonne presenti nella serviceResponse
    String vettColonne1[]=new String[9];
    //vettore che permette di definire dei link per le colonne
    boolean vettColonneLink1[]=new boolean[9];
    //vettore delle azioni dei link per le colonne
    String vettAzioniColonneLink1[]=new String[9];
    //vettore delle icone dei link per le colonne
    String vettIconeColonneLink1[]=new String[9];
    //vettore che indica se una colonna è visibile
    boolean vettColonneVisibili1[]=new boolean[9];
    
    vettIntestazioni1[0]="&nbsp;";
    vettIntestazioni1[1]="Cognome";
    vettIntestazioni1[2]="Nome";
    vettIntestazioni1[3]="Codice Fiscale";
    vettIntestazioni1[4]="Data Nascita";
    vettIntestazioni1[5]="Indirizzo Domicilio";
    vettIntestazioni1[6]="Comune Domicilio";
    vettIntestazioni1[7]="Data Contatto";
    vettIntestazioni1[8]="Data Scadenza";

    vettColonne1[0]="CDNLAVORATORE";
    vettColonne1[1]="STRCOGNOME";
    vettColonne1[2]="STRNOME";
    vettColonne1[3]="STRCODICEFISCALE";
    vettColonne1[4]="DATNASC";
    vettColonne1[5]="strindirizzodom";
    vettColonne1[6]="STRcittadomi";
    vettColonne1[7]="STRDATCONTATTO";
    vettColonne1[8]="STRDATENTROIL";

    vettColonneLink1[0]=true;
    vettColonneLink1[1]=false;
    vettColonneLink1[2]=false;
    vettColonneLink1[3]=false;
    vettColonneLink1[4]=false;
    vettColonneLink1[5]=false;
    vettColonneLink1[6]=false;
    vettColonneLink1[7]=false;
    vettColonneLink1[8]=false;

    vettAzioniColonneLink1[0]="javascript:ApriSchedaLavoratore";
    vettAzioniColonneLink1[1]="";
    vettAzioniColonneLink1[2]="";
    vettAzioniColonneLink1[3]="";
    vettAzioniColonneLink1[4]="";
    vettAzioniColonneLink1[5]="";
    vettAzioniColonneLink1[6]="";
    vettAzioniColonneLink1[7]="";
    vettAzioniColonneLink1[8]="";
    
    vettIconeColonneLink1[0]="../../img/detail.gif";
    vettIconeColonneLink1[1]="";
    vettIconeColonneLink1[2]="";
    vettIconeColonneLink1[3]="";
    vettIconeColonneLink1[4]="";
    vettIconeColonneLink1[5]="";
    vettIconeColonneLink1[6]="";
    vettIconeColonneLink1[7]="";
    vettIconeColonneLink1[8]="";

    vettColonneVisibili1[0]=true;
    vettColonneVisibili1[1]=true;
    vettColonneVisibili1[2]=true;
    vettColonneVisibili1[3]=true;
    vettColonneVisibili1[4]=true;
    vettColonneVisibili1[5]=true;
    vettColonneVisibili1[6]=true;
    vettColonneVisibili1[7]=true;
    vettColonneVisibili1[8]=true;
    
  
    if (strVisibleApp.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="App.to";
      vettIconeBottoni[nDim]="../../img/agendina.gif";
      vettAzioniBottoni[nDim]="javascript:ApriAppuntamentoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleCont.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Con.to";
      vettIconeBottoni[nDim]="../../img/contatti.gif";
      vettAzioniBottoni[nDim]="javascript:ApriContattoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleEsiti.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Esiti";
      vettIconeBottoni[nDim]="";
      vettAzioniBottoni[nDim]="";
      nDim = nDim + 1;
    }

    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioni1, vettColonne1, vettColonneLink1, vettAzioniColonneLink1, vettIconeColonneLink1, vettColonneVisibili1, vettIntestazioniBottoni, vettIconeBottoni, vettAzioniBottoni, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, true);
    break;
      
  case 2:
    rowMotivo = (SourceBean) serviceResponse.getAttribute("COMBO_MOTIVO_CONTATTO_AG_DETTAGLIO.ROWS.ROW");
    if (rowMotivo != null) {
      strCodiceContatto = rowMotivo.containsAttribute("CODICE")? rowMotivo.getAttribute("CODICE").toString():"";
      strDescMotivoContatto = rowMotivo.containsAttribute("DESCRIZIONE")? rowMotivo.getAttribute("DESCRIZIONE").toString():"";
    }
    if (data_dal != null && !data_dal.equals("")) {
      txtOut += "Data Dal <strong>"+ data_dal +"</strong>; ";
    }
    if (data_al != null && !data_al.equals("")) {
      txtOut += "Data Al <strong>"+ data_al +"</strong>; ";
    }
    if (strDescMotivoContatto != null && !strDescMotivoContatto.equals("")) {
      txtOut += "Motivo contatto <strong>"+ strDescMotivoContatto +"</strong>; ";
    }
    %>
    <p align="center">
    <%
    if (txtOut.length() > 0) { 
      txtOut = "Filtri di ricerca:<br/> " + txtOut;
    %>
      <table cellpadding="2" cellspacing="10" border="0" width="100%">
      <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%
      out.print(txtOut);
      %>
      </td></tr>
      </table>
    <%
    }
      
    //vettore delle intestazioni della lista
    String vettIntestazioni2[]=new String[9];
    //vettore delle colonne presenti nella serviceResponse
    String vettColonne2[]=new String[9];
    //vettore che permette di definire dei link per le colonne
    boolean vettColonneLink2[]=new boolean[9];
    //vettore delle azioni dei link per le colonne
    String vettAzioniColonneLink2[]=new String[9];
    //vettore delle icone dei link per le colonne
    String vettIconeColonneLink2[]=new String[9];
    //vettore che indica se una colonna è visibile
    boolean vettColonneVisibili2[]=new boolean[9];
    
    vettIntestazioni2[0]="&nbsp;";
    vettIntestazioni2[1]="Tipo";
    vettIntestazioni2[2]="Codice Fiscale";
    vettIntestazioni2[3]="Partita Iva";
    vettIntestazioni2[4]="Ragione Sociale";
    vettIntestazioni2[5]="Indirizzo";
    vettIntestazioni2[6]="Comune";
    vettIntestazioni2[7]="Data Contatto";
    vettIntestazioni2[8]="Data Scadenza";
    
    vettColonne2[0]="codazienda,codunitaazienda";
    vettColonne2[1]="descrizionetipo";
    vettColonne2[2]="codicefiscale";
    vettColonne2[3]="piva";
    vettColonne2[4]="ragionesociale";
    vettColonne2[5]="indirizzo";
    vettColonne2[6]="comune";
    vettColonne2[7]="STRDATCONTATTO";
    vettColonne2[8]="STRDATENTROIL";
    
    vettColonneLink2[0]=true;
    vettColonneLink2[1]=false;
    vettColonneLink2[2]=false;
    vettColonneLink2[3]=false;
    vettColonneLink2[4]=false;
    vettColonneLink2[5]=false;
    vettColonneLink2[6]=false;
    vettColonneLink2[7]=false;
    vettColonneLink2[8]=false;

    vettAzioniColonneLink2[0]="javascript:ApriSchedaAzienda";
    vettAzioniColonneLink2[1]="";
    vettAzioniColonneLink2[2]="";
    vettAzioniColonneLink2[3]="";
    vettAzioniColonneLink2[4]="";
    vettAzioniColonneLink2[5]="";
    vettAzioniColonneLink2[6]="";
    vettAzioniColonneLink2[7]="";
    vettAzioniColonneLink2[8]="";
      
    vettIconeColonneLink2[0]="../../img/detail.gif";
    vettIconeColonneLink2[1]="";
    vettIconeColonneLink2[2]="";
    vettIconeColonneLink2[3]="";
    vettIconeColonneLink2[4]="";
    vettIconeColonneLink2[5]="";
    vettIconeColonneLink2[6]="";
    vettIconeColonneLink2[7]="";
    vettIconeColonneLink2[8]="";
      
    vettColonneVisibili2[0]=true;
    vettColonneVisibili2[1]=true;
    vettColonneVisibili2[2]=true;
    vettColonneVisibili2[3]=true;
    vettColonneVisibili2[4]=true;
    vettColonneVisibili2[5]=true;
    vettColonneVisibili2[6]=true;
    vettColonneVisibili2[7]=true;
    vettColonneVisibili2[8]=true;
      
    if (strVisibleApp.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="App.to";
      vettIconeBottoni[nDim]="../../img/agendina.gif";
      vettAzioniBottoni[nDim]="javascript:ApriAppuntamentoAzienda";
      nDim = nDim + 1;
    }
    if (strVisibleCont.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Con.to";
      vettIconeBottoni[nDim]="../../img/contatti.gif";
      vettAzioniBottoni[nDim]="javascript:ApriContattoAzienda";
      nDim = nDim + 1;
    }
    if (strVisibleEsiti.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Esiti";
      vettIconeBottoni[nDim]="";
      vettAzioniBottoni[nDim]="";
      nDim = nDim + 1;
    }
    
    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioni2, vettColonne2, vettColonneLink2, vettAzioniColonneLink2, vettIconeColonneLink2, vettColonneVisibili2, vettIntestazioniBottoni, vettIconeBottoni, vettAzioniBottoni, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, true);
    break;

  case 3: 
    if (data_dal != null && !data_dal.equals("")) {
      txtOut += "Data Dal <strong>"+ data_dal +"</strong>; ";
    }
    if (data_al != null && !data_al.equals("")) {
      txtOut += "Data Al <strong>"+ data_al +"</strong>; ";
    }
    String strDescValidita = "";
    SourceBean rowDescValidita = null;
    rowDescValidita = (SourceBean) serviceResponse.getAttribute("M_GetValidita_SCAD.ROWS.ROW");
    if (rowDescValidita != null) {
      strDescValidita = rowDescValidita.containsAttribute("DESCRIZIONE") ? rowDescValidita.getAttribute("DESCRIZIONE").toString() : "";
    }    
    if (!strDescValidita.equals("")) {
      txtOut += "Validità <strong>"+ strDescValidita +"</strong>; ";
    }
    
    String strDescValiditaCurr = "";
    SourceBean rowDescValiditaCurr = null;
    rowDescValiditaCurr = (SourceBean) serviceResponse.getAttribute("M_GetValiditaCurr.ROWS.ROW");
    if (rowDescValiditaCurr != null) {
      strDescValiditaCurr = rowDescValiditaCurr.containsAttribute("DESCRIZIONE") ? rowDescValiditaCurr.getAttribute("DESCRIZIONE").toString() : "";
    }
        
    if (!strDescValiditaCurr.equals("")) {
      txtOut += "Stato di validità del CV <strong>"+ strDescValiditaCurr +"</strong>; ";
    }
    
 	String strCpiAppoggio = serviceRequest.containsAttribute("CodCpiApp") ? serviceRequest.getAttribute("CodCpiApp").toString(): "";	
	
	if(!strCpiAppoggio.equals("")) {
		String strCodiceImpiego = "";
    	SourceBean rows = null;
    	rows = (SourceBean) serviceResponse.getAttribute("M_CpiPoloProv.ROWS.ROW");
    	if (rows != null) {
      		strCodiceImpiego = rows.containsAttribute("DESCRIZIONE") ? rows.getAttribute("DESCRIZIONE").toString() : "";
    	}    
    	if (!strCodiceImpiego.equals("")) {
      		txtOut += "Cpi comp.: <strong>"+ strCodiceImpiego +"</strong>; ";
    	}
    }
    		
    %>
    <p align="center">
    <%
    if (txtOut.length() > 0) { 
      txtOut = "Filtri di ricerca:<br/> " + txtOut;
    %>
      <table cellpadding="2" cellspacing="10" border="0" width="100%">
      <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%
      out.print(txtOut);
      %>
      </td></tr>
      </table>
    <%
    }
      
    //vettore delle intestazioni della lista
    String vettIntestazioni3[]=new String[6];
    //vettore delle colonne presenti nella serviceResponse
    String vettColonne3[]=new String[6];
    //vettore che permette di definire dei link per le colonne
    boolean vettColonneLink3[]=new boolean[6];
    //vettore delle azioni dei link per le colonne
    String vettAzioniColonneLink3[]=new String[6];
    //vettore delle icone dei link per le colonne
    String vettIconeColonneLink3[]=new String[6];
    //vettore che indica se una colonna è visibile
    boolean vettColonneVisibili3[]=new boolean[6];
    
    vettIntestazioni3[0]="&nbsp;";
    vettIntestazioni3[1]="Cognome";
    vettIntestazioni3[2]="Nome";
    vettIntestazioni3[3]="Codice Fiscale";
    vettIntestazioni3[4]="Data Nascita";
    vettIntestazioni3[5]="Data Scadenza";

    vettColonne3[0]="CDNLAVORATORE";
    vettColonne3[1]="STRCOGNOME";
    vettColonne3[2]="STRNOME";
    vettColonne3[3]="STRCODICEFISCALE";
    vettColonne3[4]="DATNASC";
    vettColonne3[5]="STRDATFINECURR";

    vettColonneLink3[0]=true;
    vettColonneLink3[1]=false;
    vettColonneLink3[2]=false;
    vettColonneLink3[3]=false;
    vettColonneLink3[4]=false;
    vettColonneLink3[5]=false;

    vettAzioniColonneLink3[0]="javascript:ApriSchedaLavoratore";
    vettAzioniColonneLink3[1]="";
    vettAzioniColonneLink3[2]="";
    vettAzioniColonneLink3[3]="";
    vettAzioniColonneLink3[4]="";
    vettAzioniColonneLink3[5]="";

    vettIconeColonneLink3[0]="../../img/detail.gif";
    vettIconeColonneLink3[1]="";
    vettIconeColonneLink3[2]="";
    vettIconeColonneLink3[3]="";
    vettIconeColonneLink3[4]="";
    vettIconeColonneLink3[5]="";

    vettColonneVisibili3[0]=true;
    vettColonneVisibili3[1]=true;
    vettColonneVisibili3[2]=true;
    vettColonneVisibili3[3]=true;
    vettColonneVisibili3[4]=true;
    vettColonneVisibili3[5]=true;
  
    if (strVisibleApp.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="App.to";
      vettIconeBottoni[nDim]="../../img/agendina.gif";
      vettAzioniBottoni[nDim]="javascript:ApriAppuntamentoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleCont.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Con.to";
      vettIconeBottoni[nDim]="../../img/contatti.gif";
      vettAzioniBottoni[nDim]="javascript:ApriContattoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleEsiti.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Esiti";
      vettIconeBottoni[nDim]="";
      vettAzioniBottoni[nDim]="";
      nDim = nDim + 1;
    }

    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioni3, vettColonne3, vettColonneLink3, vettAzioniColonneLink3, vettIconeColonneLink3, vettColonneVisibili3, vettIntestazioniBottoni, vettIconeBottoni, vettAzioniBottoni, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, true);
    break;

  case 4: 
    if (data_dal != null && !data_dal.equals("")) {
      txtOut += "Data Dal <strong>"+ data_dal +"</strong>; ";
    }
    if (data_al != null && !data_al.equals("")) {
      txtOut += "Data Al <strong>"+ data_al +"</strong>; ";
    }
    %>
    <p align="center">
    <%
    if (txtOut.length() > 0) { 
      txtOut = "Filtri di ricerca:<br/> " + txtOut;
    %>
      <table cellpadding="2" cellspacing="10" border="0" width="100%">
      <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%
      out.print(txtOut);
      %>
      </td></tr>
      </table>
    <%
    }

    //vettore delle intestazioni della lista
    String vettIntestazioni4[]=new String[6];
    //vettore delle colonne presenti nella serviceResponse
    String vettColonne4[]=new String[6];
    //vettore che permette di definire dei link per le colonne
    boolean vettColonneLink4[]=new boolean[6];
    //vettore delle azioni dei link per le colonne
    String vettAzioniColonneLink4[]=new String[6];
    //vettore delle icone dei link per le colonne
    String vettIconeColonneLink4[]=new String[6];
    //vettore che indica se una colonna è visibile
    boolean vettColonneVisibili4[]=new boolean[6];
    
    vettIntestazioni4[0]="&nbsp;";
    vettIntestazioni4[1]="Cognome";
    vettIntestazioni4[2]="Nome";
    vettIntestazioni4[3]="Codice Fiscale";
    vettIntestazioni4[4]="Data Nascita";
    vettIntestazioni4[5]="Data Scadenza Permesso";

    vettColonne4[0]="CDNLAVORATORE";
    vettColonne4[1]="STRCOGNOME";
    vettColonne4[2]="STRNOME";
    vettColonne4[3]="STRCODICEFISCALE";
    vettColonne4[4]="DATNASC";
    vettColonne4[5]="DATSCADENZA";

    vettColonneLink4[0]=true;
    vettColonneLink4[1]=false;
    vettColonneLink4[2]=false;
    vettColonneLink4[3]=false;
    vettColonneLink4[4]=false;
    vettColonneLink4[5]=false;

    vettAzioniColonneLink4[0]="javascript:ApriSchedaLavoratore";
    vettAzioniColonneLink4[1]="";
    vettAzioniColonneLink4[2]="";
    vettAzioniColonneLink4[3]="";
    vettAzioniColonneLink4[4]="";
    vettAzioniColonneLink4[5]="";

    vettIconeColonneLink4[0]="../../img/detail.gif";
    vettIconeColonneLink4[1]="";
    vettIconeColonneLink4[2]="";
    vettIconeColonneLink4[3]="";
    vettIconeColonneLink4[4]="";
    vettIconeColonneLink4[5]="";

    vettColonneVisibili4[0]=true;
    vettColonneVisibili4[1]=true;
    vettColonneVisibili4[2]=true;
    vettColonneVisibili4[3]=true;
    vettColonneVisibili4[4]=true;
    vettColonneVisibili4[5]=true;
  
    if (strVisibleApp.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="App.to";
      vettIconeBottoni[nDim]="../../img/agendina.gif";
      vettAzioniBottoni[nDim]="javascript:ApriAppuntamentoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleCont.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Con.to";
      vettIconeBottoni[nDim]="../../img/contatti.gif";
      vettAzioniBottoni[nDim]="javascript:ApriContattoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleEsiti.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Esiti";
      vettIconeBottoni[nDim]="";
      vettAzioniBottoni[nDim]="";
      nDim = nDim + 1;
    }

    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioni4, vettColonne4, vettColonneLink4, vettAzioniColonneLink4, vettIconeColonneLink4, vettColonneVisibili4, vettIntestazioniBottoni, vettIconeBottoni, vettAzioniBottoni, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, true);
    break;

    //CANDIDATURE INVIATE A CLIC LAVORO
  case 5:
	  	String dataInvio_al = 	serviceRequest.containsAttribute("DATAINVIO_AL")? serviceRequest.getAttribute("DATAINVIO_AL").toString():"";
		String dataInvio_dal= 	serviceRequest.containsAttribute("DATAINVIO_DAL")? serviceRequest.getAttribute("DATAINVIO_DAL").toString():"";
		String dataScadenza_al= serviceRequest.containsAttribute("DATASCADENZA_AL")? serviceRequest.getAttribute("DATASCADENZA_AL").toString():"";
		String dataScadenza_dal=serviceRequest.containsAttribute("DATASCADENZA_DAL")? serviceRequest.getAttribute("DATASCADENZA_DAL").toString():"";
		String codCPI = 		serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
		String codAmbitoDiff   =serviceRequest.containsAttribute("COD_AMBITO_DIFFUSIONE")? serviceRequest.getAttribute("COD_AMBITO_DIFFUSIONE").toString():"";  
		String strCPI = 		serviceRequest.containsAttribute("STRCPI")? serviceRequest.getAttribute("STRCPI").toString():"";
		String strAmbitoDiff=	serviceRequest.containsAttribute("STR_AMBITO_DIFFUSIONE")? serviceRequest.getAttribute("STR_AMBITO_DIFFUSIONE").toString():"";
		
	  if (dataInvio_dal != null && !dataInvio_dal.equals("")) {
	      txtOut += "Data invio Dal <strong>"+ dataInvio_dal +"</strong>; ";
	    }
	    if (dataInvio_al != null && !dataInvio_al.equals("")) {
	      txtOut += "Data invio Al <strong>"+ dataInvio_al +"</strong>; ";
	    }
	    if (dataScadenza_dal != null && !dataScadenza_dal.equals("")) {
		      txtOut += "Data scadenza Dal <strong>"+ dataScadenza_dal +"</strong>; ";
		}
		if (dataScadenza_al != null && !dataScadenza_al.equals("")) {
		      txtOut += "Data scadenza Al <strong>"+ dataScadenza_al +"</strong>; ";
		}
		
		if (strCPI != null && !strCPI.equals("")){
			txtOut += "CPI intermediario <strong>"+ strCPI +"</strong>; ";	
		}
		if (strAmbitoDiff!=null && !strAmbitoDiff.equals("")){
			txtOut += "Ambito di diffusione <strong>"+ strAmbitoDiff +"</strong>; ";	
		}
	    %>
	    <p align="center">
	    <%
	    if (txtOut.length() > 0) { 
	      txtOut = "Filtri di ricerca:<br/> " + txtOut;
	    %>
  		<table cellpadding="2" cellspacing="10" border="0" width="100%">
      	<tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      	<%
      	out.print(txtOut);
	      %>
	      </td></tr>
	      </table>
	    <%}
	  //vettore delle intestazioni della lista
	    String vettIntestazioniSeg5[]=new String[5];
	    //vettore delle colonne presenti nella serviceResponse
	    String vettColonneSeg5[]=new String[5];
	    //vettore che permette di definire dei link per le colonne
	    boolean vettColonneLinkSeg5[]=new boolean[5];
	    //vettore delle azioni dei link per le colonne
	    String vettAzioniColonneLinkSeg5[]=new String[5];
	    //vettore delle icone dei link per le colonne
	    String vettIconeColonneLinkSeg5[]=new String[5];
	    //vettore che indica se una colonna è visibile
	    boolean vettColonneVisibiliSeg5[]=new boolean[5];
	    
	    vettIntestazioniSeg5[0]="&nbsp;";
	    vettIntestazioniSeg5[1]="Cognome";
	    vettIntestazioniSeg5[2]="Nome";
	    vettIntestazioniSeg5[3]="Codice Fiscale";
	    vettIntestazioniSeg5[4]="Data Nascita";
	    
	    vettColonneSeg5[0]="CDNLAVORATORE";
	    vettColonneSeg5[1]="STRCOGNOME";
	    vettColonneSeg5[2]="STRNOME";
	    vettColonneSeg5[3]="STRCODICEFISCALE";
	    vettColonneSeg5[4]="DATNASC";
	    
	    vettColonneLinkSeg5[0]=true;
	    vettColonneLinkSeg5[1]=false;
	    vettColonneLinkSeg5[2]=false;
	    vettColonneLinkSeg5[3]=false;
	    vettColonneLinkSeg5[4]=false;
	    
	    vettAzioniColonneLinkSeg5[0]="javascript:ApriSchedaLavoratore";
	    vettAzioniColonneLinkSeg5[1]="";
	    vettAzioniColonneLinkSeg5[2]="";
	    vettAzioniColonneLinkSeg5[3]="";
	    vettAzioniColonneLinkSeg5[4]="";
	    
	    vettIconeColonneLinkSeg5[0]="../../img/detail.gif";
	    vettIconeColonneLinkSeg5[1]="";
	    vettIconeColonneLinkSeg5[2]="";
	    vettIconeColonneLinkSeg5[3]="";
	    vettIconeColonneLinkSeg5[4]="";
	    
	    vettColonneVisibiliSeg5[0]=true;
	    vettColonneVisibiliSeg5[1]=true;
	    vettColonneVisibiliSeg5[2]=true;
	    vettColonneVisibiliSeg5[3]=true;
	    vettColonneVisibiliSeg5[4]=true;
	    
	    if (strVisibleApp.compareTo("S") == 0) {
	      vettIntestazioniBottoni[nDim]="App.to";
	      vettIconeBottoni[nDim]="../../img/agendina.gif";
	      vettAzioniBottoni[nDim]="javascript:ApriAppuntamentoLavoratore";
	      nDim = nDim + 1;
	    }
	    if (strVisibleCont.compareTo("S") == 0) {
	      vettIntestazioniBottoni[nDim]="Con.to";
	      vettIconeBottoni[nDim]="../../img/contatti.gif";
	      vettAzioniBottoni[nDim]="javascript:ApriContattoLavoratore";
	      nDim = nDim + 1;
	    }
	    if (strVisibleEsiti.compareTo("S") == 0) {
	      vettIntestazioniBottoni[nDim]="Esiti";
	      vettIconeBottoni[nDim]="";
	      vettAzioniBottoni[nDim]="";
	      nDim = nDim + 1;
	    }
	//Candidature inviate a ClicLavoro
	    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioniSeg5, vettColonneSeg5, vettColonneLinkSeg5, vettAzioniColonneLinkSeg5, vettIconeColonneLinkSeg5, vettColonneVisibiliSeg5, vettIntestazioniBottoni, vettIconeBottoni, vettAzioniBottoni, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, true);
  
   break;
    
  case 101:
    if (data_dal != null && !data_dal.equals("")) {
      txtOut += "Data Dal <strong>"+ data_dal +"</strong>; ";
    }
    if (data_al != null && !data_al.equals("")) {
      txtOut += "Data Al <strong>"+ data_al +"</strong>; ";
    }
    if (!descrizioneTipologiaPatto.equals("")) {
    	txtOut += "Tipologia Patto <strong>"+ descrizioneTipologiaPatto +"</strong>; ";	
    }
    %>
    <p align="center">
    <%
    if (txtOut.length() > 0) { 
      txtOut = "Filtri di ricerca:<br/> " + txtOut;
    %>
      <table cellpadding="2" cellspacing="10" border="0" width="100%">
      <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%
      out.print(txtOut);
      %>
      </td></tr>
      </table>
    <%
    }
      
    //vettore delle intestazioni della lista
    String vettIntestazioniAmm1[]=new String[7];
    //vettore delle colonne presenti nella serviceResponse
    String vettColonneAmm1[]=new String[7];
    //vettore che permette di definire dei link per le colonne
    boolean vettColonneLinkAmm1[]=new boolean[7];
    //vettore delle azioni dei link per le colonne
    String vettAzioniColonneLinkAmm1[]=new String[7];
    //vettore delle icone dei link per le colonne
    String vettIconeColonneLinkAmm1[]=new String[7];
    //vettore che indica se una colonna è visibile
    boolean vettColonneVisibiliAmm1[]=new boolean[7];
    
    vettIntestazioniAmm1[0]="&nbsp;";
    vettIntestazioniAmm1[1]="Cognome";
    vettIntestazioniAmm1[2]="Nome";
    vettIntestazioniAmm1[3]="Codice Fiscale";
    vettIntestazioniAmm1[4]="Data Nascita";
    vettIntestazioniAmm1[5]="Scad.stimata";
    vettIntestazioniAmm1[6]="Azione";

    vettColonneAmm1[0]="CDNLAVORATORE";
    vettColonneAmm1[1]="STRCOGNOME";
    vettColonneAmm1[2]="STRNOME";
    vettColonneAmm1[3]="STRCODICEFISCALE";
    vettColonneAmm1[4]="DATNASC";
    vettColonneAmm1[5]="STRDATSTIMATA";
    vettColonneAmm1[6]="STRDESCRIZIONE";
      
    vettColonneLinkAmm1[0]=true;
    vettColonneLinkAmm1[1]=false;
    vettColonneLinkAmm1[2]=false;
    vettColonneLinkAmm1[3]=false;
    vettColonneLinkAmm1[4]=false;
    vettColonneLinkAmm1[5]=false;
    vettColonneLinkAmm1[6]=false;

    vettAzioniColonneLinkAmm1[0]="javascript:ApriSchedaLavoratore";
    vettAzioniColonneLinkAmm1[1]="";
    vettAzioniColonneLinkAmm1[2]="";
    vettAzioniColonneLinkAmm1[3]="";
    vettAzioniColonneLinkAmm1[4]="";
    vettAzioniColonneLinkAmm1[5]="";
    vettAzioniColonneLinkAmm1[6]="";

    vettIconeColonneLinkAmm1[0]="../../img/detail.gif";
    vettIconeColonneLinkAmm1[1]="";
    vettIconeColonneLinkAmm1[2]="";
    vettIconeColonneLinkAmm1[3]="";
    vettIconeColonneLinkAmm1[4]="";
    vettIconeColonneLinkAmm1[5]="";
    vettIconeColonneLinkAmm1[6]="";

    vettColonneVisibiliAmm1[0]=true;
    vettColonneVisibiliAmm1[1]=true;
    vettColonneVisibiliAmm1[2]=true;
    vettColonneVisibiliAmm1[3]=true;
    vettColonneVisibiliAmm1[4]=true;
    vettColonneVisibiliAmm1[5]=true;
    vettColonneVisibiliAmm1[6]=true;
  
    if (strVisibleApp.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="App.to";
      vettIconeBottoni[nDim]="../../img/agendina.gif";
      vettAzioniBottoni[nDim]="javascript:ApriAppuntamentoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleCont.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Con.to";
      vettIconeBottoni[nDim]="../../img/contatti.gif";
      vettAzioniBottoni[nDim]="javascript:ApriContattoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleEsiti.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Esiti";
      vettIconeBottoni[nDim]="";
      vettAzioniBottoni[nDim]="";
      nDim = nDim + 1;
    }

    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioniAmm1, vettColonneAmm1, vettColonneLinkAmm1, vettAzioniColonneLinkAmm1, vettIconeColonneLinkAmm1, vettColonneVisibiliAmm1, vettIntestazioniBottoni, vettIconeBottoni, vettAzioniBottoni, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, true);
    break;

  case 102:
    if (data_dal != null && !data_dal.equals("")) {
      txtOut += "Data Dal <strong>"+ data_dal +"</strong>; ";
    }
    if (data_al != null && !data_al.equals("")) {
      txtOut += "Data Al <strong>"+ data_al +"</strong>; ";
    }
    %>
    <p align="center">
    <%
    if (txtOut.length() > 0) { 
      txtOut = "Filtri di ricerca:<br/> " + txtOut;
    %>
      <table cellpadding="2" cellspacing="10" border="0" width="100%">
      <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%
      out.print(txtOut);
      %>
      </td></tr>
      </table>
    <%
    }
      
    //vettore delle intestazioni della lista
    String vettIntestazioniAmm2[]=new String[6];
    //vettore delle colonne presenti nella serviceResponse
    String vettColonneAmm2[]=new String[6];
    //vettore che permette di definire dei link per le colonne
    boolean vettColonneLinkAmm2[]=new boolean[6];
    //vettore delle azioni dei link per le colonne
    String vettAzioniColonneLinkAmm2[]=new String[6];
    //vettore delle icone dei link per le colonne
    String vettIconeColonneLinkAmm2[]=new String[6];
    //vettore che indica se una colonna è visibile
    boolean vettColonneVisibiliAmm2[]=new boolean[6];
    //vettore intestazioni dei bottoni da aggiungere alla fine
    String vettIntestazioniBottoniAmm2[]=new String[4];
    //vettore delle icone dei bottoni
    String vettIconeBottoniAmm2[]=new String[4];
    //vettore delle azioni dei bottoni
    String vettAzioniBottoniAmm2[]=new String[4];

    vettIntestazioniAmm2[0]="&nbsp;";
    vettIntestazioniAmm2[1]="Cognome";
    vettIntestazioniAmm2[2]="Nome";
    vettIntestazioniAmm2[3]="Codice Fiscale";
    vettIntestazioniAmm2[4]="Data Nascita";
    vettIntestazioniAmm2[5]="Data Scadenza";
      
    vettColonneAmm2[0]="CDNLAVORATORE";
    vettColonneAmm2[1]="STRCOGNOME";
    vettColonneAmm2[2]="STRNOME";
    vettColonneAmm2[3]="STRCODICEFISCALE";
    vettColonneAmm2[4]="DATNASC";
    vettColonneAmm2[5]="SCADENZA";
      
    vettColonneLinkAmm2[0]=true;
    vettColonneLinkAmm2[1]=false;
    vettColonneLinkAmm2[2]=false;
    vettColonneLinkAmm2[3]=false;
    vettColonneLinkAmm2[4]=false;
    vettColonneLinkAmm2[5]=false;

    vettAzioniColonneLinkAmm2[0]="javascript:ApriSchedaLavoratore";
    vettAzioniColonneLinkAmm2[1]="";
    vettAzioniColonneLinkAmm2[2]="";
    vettAzioniColonneLinkAmm2[3]="";
    vettAzioniColonneLinkAmm2[4]="";
    vettAzioniColonneLinkAmm2[5]="";
      
    vettIconeColonneLinkAmm2[0]="../../img/detail.gif";
    vettIconeColonneLinkAmm2[1]="";
    vettIconeColonneLinkAmm2[2]="";
    vettIconeColonneLinkAmm2[3]="";
    vettIconeColonneLinkAmm2[4]="";
    vettIconeColonneLinkAmm2[5]="";
      
    vettColonneVisibiliAmm2[0]=true;
    vettColonneVisibiliAmm2[1]=true;
    vettColonneVisibiliAmm2[2]=true;
    vettColonneVisibiliAmm2[3]=true;
    vettColonneVisibiliAmm2[4]=true;
    vettColonneVisibiliAmm2[5]=true;
      
    vettIntestazioniBottoniAmm2[0]="";
    vettIntestazioniBottoniAmm2[1]="";
    vettIntestazioniBottoniAmm2[2]="";
    vettIntestazioniBottoniAmm2[3]="";

    vettIconeBottoniAmm2[0]="";
    vettIconeBottoniAmm2[1]="";
    vettIconeBottoniAmm2[2]="";
    vettIconeBottoniAmm2[3]="";

    vettAzioniBottoniAmm2[0]="";
    vettAzioniBottoniAmm2[1]="";
    vettAzioniBottoniAmm2[2]="";
    vettAzioniBottoniAmm2[3]="";

    if (strVisibleApp.compareTo("S") == 0) {
      vettIntestazioniBottoniAmm2[nDim]="App.to";
      vettIconeBottoniAmm2[nDim]="../../img/agendina.gif";
      vettAzioniBottoniAmm2[nDim]="javascript:ApriAppuntamentoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleCont.compareTo("S") == 0) {
      vettIntestazioniBottoniAmm2[nDim]="Con.to";
      vettIconeBottoniAmm2[nDim]="../../img/contatti.gif";
      vettAzioniBottoniAmm2[nDim]="javascript:ApriContattoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleColloqui.compareTo("S") == 0) {
      vettIntestazioniBottoniAmm2[nDim]="Colloqui";
      vettIconeBottoniAmm2[nDim]="../../img/info_soggetto.gif";
      vettAzioniBottoniAmm2[nDim]="javascript:ApriColloquiLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleEsiti.compareTo("S") == 0) {
      vettIntestazioniBottoniAmm2[nDim]="Esiti";
      vettIconeBottoniAmm2[nDim]="";
      vettAzioniBottoniAmm2[nDim]="";
      nDim = nDim + 1;
    }

    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioniAmm2, vettColonneAmm2, vettColonneLinkAmm2, vettAzioniColonneLinkAmm2, vettIconeColonneLinkAmm2, vettColonneVisibiliAmm2, vettIntestazioniBottoniAmm2, vettIconeBottoniAmm2, vettAzioniBottoniAmm2, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, true);
    break;

  case 103:
    if (data_dal != null && !data_dal.equals("")) {
      txtOut += "Data Dal <strong>"+ data_dal +"</strong>; ";
    }
    if (data_al != null && !data_al.equals("")) {
      txtOut += "Data Al <strong>"+ data_al +"</strong>; ";
    }
    %>
    <p align="center">
    <%
    if (txtOut.length() > 0) { 
      txtOut = "Filtri di ricerca:<br/> " + txtOut;
    %>
      <table cellpadding="2" cellspacing="10" border="0" width="100%">
      <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%
      out.print(txtOut);
      %>
      </td></tr>
      </table>
    <%
    }

    //vettore delle intestazioni della lista
    String vettIntestazioniAmm3[]=new String[6];
    //vettore delle colonne presenti nella serviceResponse
    String vettColonneAmm3[]=new String[6];
    //vettore che permette di definire dei link per le colonne
    boolean vettColonneLinkAmm3[]=new boolean[6];
    //vettore delle azioni dei link per le colonne
    String vettAzioniColonneLinkAmm3[]=new String[6];
    //vettore delle icone dei link per le colonne
    String vettIconeColonneLinkAmm3[]=new String[6];
    //vettore che indica se una colonna è visibile
    boolean vettColonneVisibiliAmm3[]=new boolean[6];
    
    vettIntestazioniAmm3[0]="&nbsp;";
    vettIntestazioniAmm3[1]="Cognome";
    vettIntestazioniAmm3[2]="Nome";
    vettIntestazioniAmm3[3]="Codice Fiscale";
    vettIntestazioniAmm3[4]="Data Nascita";
    vettIntestazioniAmm3[5]="Data Scadenza";
      
    vettColonneAmm3[0]="CDNLAVORATORE";
    vettColonneAmm3[1]="STRCOGNOME";
    vettColonneAmm3[2]="STRNOME";
    vettColonneAmm3[3]="STRCODICEFISCALE";
    vettColonneAmm3[4]="DATNASC";
    vettColonneAmm3[5]="SCADENZA";
      
    vettColonneLinkAmm3[0]=true;
    vettColonneLinkAmm3[1]=false;
    vettColonneLinkAmm3[2]=false;
    vettColonneLinkAmm3[3]=false;
    vettColonneLinkAmm3[4]=false;
    vettColonneLinkAmm3[5]=false;

    vettAzioniColonneLinkAmm3[0]="javascript:ApriSchedaLavoratore";
    vettAzioniColonneLinkAmm3[1]="";
    vettAzioniColonneLinkAmm3[2]="";
    vettAzioniColonneLinkAmm3[3]="";
    vettAzioniColonneLinkAmm3[4]="";
    vettAzioniColonneLinkAmm3[5]="";
      
    vettIconeColonneLinkAmm3[0]="../../img/detail.gif";
    vettIconeColonneLinkAmm3[1]="";
    vettIconeColonneLinkAmm3[2]="";
    vettIconeColonneLinkAmm3[3]="";
    vettIconeColonneLinkAmm3[4]="";
    vettIconeColonneLinkAmm3[5]="";
      
    vettColonneVisibiliAmm3[0]=true;
    vettColonneVisibiliAmm3[1]=true;
    vettColonneVisibiliAmm3[2]=true;
    vettColonneVisibiliAmm3[3]=true;
    vettColonneVisibiliAmm3[4]=true;
    vettColonneVisibiliAmm3[5]=true;
      
    if (strVisibleApp.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="App.to";
      vettIconeBottoni[nDim]="../../img/agendina.gif";
      vettAzioniBottoni[nDim]="javascript:ApriAppuntamentoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleCont.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Con.to";
      vettIconeBottoni[nDim]="../../img/contatti.gif";
      vettAzioniBottoni[nDim]="javascript:ApriContattoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleEsiti.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Esiti";
      vettIconeBottoni[nDim]="";
      vettAzioniBottoni[nDim]="";
      nDim = nDim + 1;
    }

    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioniAmm3, vettColonneAmm3, vettColonneLinkAmm3, vettAzioniColonneLinkAmm3, vettIconeColonneLinkAmm3, vettColonneVisibiliAmm3, vettIntestazioniBottoni, vettIconeBottoni, vettAzioniBottoni, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, true);
    break;
    
  case 104:
    if (data_dal != null && !data_dal.equals("")) {
      txtOut += "Data Dal <strong>"+ data_dal +"</strong>; ";
    }
    if (data_al != null && !data_al.equals("")) {
      txtOut += "Data Al <strong>"+ data_al +"</strong>; ";
    }
    if (!descrizioneTipologiaPatto.equals("")) {
    	txtOut += "Tipologia Patto <strong>"+ descrizioneTipologiaPatto +"</strong>; ";	
    }
    %>
    <p align="center">
    <%
    if (txtOut.length() > 0) { 
      txtOut = "Filtri di ricerca:<br/> " + txtOut;
    %>
      <table cellpadding="2" cellspacing="10" border="0" width="100%">
      <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%
      out.print(txtOut);
      %>
      </td></tr>
      </table>
    <%
    }
      
    //vettore delle intestazioni della lista
    String vettIntestazioniAmm4[]=new String[6];
    //vettore delle colonne presenti nella serviceResponse
    String vettColonneAmm4[]=new String[6];
    //vettore che permette di definire dei link per le colonne
    boolean vettColonneLinkAmm4[]=new boolean[6];
    //vettore delle azioni dei link per le colonne
    String vettAzioniColonneLinkAmm4[]=new String[6];
    //vettore delle icone dei link per le colonne
    String vettIconeColonneLinkAmm4[]=new String[6];
    //vettore che indica se una colonna è visibile
    boolean vettColonneVisibiliAmm4[]=new boolean[6];
    
    vettIntestazioniAmm4[0]="&nbsp;";
    vettIntestazioniAmm4[1]="Cognome";
    vettIntestazioniAmm4[2]="Nome";
    vettIntestazioniAmm4[3]="Codice Fiscale";
    vettIntestazioniAmm4[4]="Data Nascita";
    vettIntestazioniAmm4[5]="Data Scadenza";
      
    vettColonneAmm4[0]="CDNLAVORATORE";
    vettColonneAmm4[1]="STRCOGNOME";
    vettColonneAmm4[2]="STRNOME";
    vettColonneAmm4[3]="STRCODICEFISCALE";
    vettColonneAmm4[4]="DATNASC";
    vettColonneAmm4[5]="SCADENZA";
      
    vettColonneLinkAmm4[0]=true;
    vettColonneLinkAmm4[1]=false;
    vettColonneLinkAmm4[2]=false;
    vettColonneLinkAmm4[3]=false;
    vettColonneLinkAmm4[4]=false;
    vettColonneLinkAmm4[5]=false;

    vettAzioniColonneLinkAmm4[0]="javascript:ApriSchedaLavoratore";
    vettAzioniColonneLinkAmm4[1]="";
    vettAzioniColonneLinkAmm4[2]="";
    vettAzioniColonneLinkAmm4[3]="";
    vettAzioniColonneLinkAmm4[4]="";
    vettAzioniColonneLinkAmm4[5]="";
      
    vettIconeColonneLinkAmm4[0]="../../img/detail.gif";
    vettIconeColonneLinkAmm4[1]="";
    vettIconeColonneLinkAmm4[2]="";
    vettIconeColonneLinkAmm4[3]="";
    vettIconeColonneLinkAmm4[4]="";
    vettIconeColonneLinkAmm4[5]="";
      
    vettColonneVisibiliAmm4[0]=true;
    vettColonneVisibiliAmm4[1]=true;
    vettColonneVisibiliAmm4[2]=true;
    vettColonneVisibiliAmm4[3]=true;
    vettColonneVisibiliAmm4[4]=true;
    vettColonneVisibiliAmm4[5]=true;
      
    if (strVisibleApp.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="App.to";
      vettIconeBottoni[nDim]="../../img/agendina.gif";
      vettAzioniBottoni[nDim]="javascript:ApriAppuntamentoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleCont.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Con.to";
      vettIconeBottoni[nDim]="../../img/contatti.gif";
      vettAzioniBottoni[nDim]="javascript:ApriContattoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleEsiti.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Esiti";
      vettIconeBottoni[nDim]="";
      vettAzioniBottoni[nDim]="";
      nDim = nDim + 1;
    }

    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioniAmm4, vettColonneAmm4, vettColonneLinkAmm4, vettAzioniColonneLinkAmm4, vettIconeColonneLinkAmm4, vettColonneVisibiliAmm4, vettIntestazioniBottoni, vettIconeBottoni, vettAzioniBottoni, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, true);
    break;

  case 1001:
    //vettore delle intestazioni della lista
    String vettIntestazioniSeg1[]=new String[7];
    //vettore delle colonne presenti nella serviceResponse
    String vettColonneSeg1[]=new String[7];
    //vettore che permette di definire dei link per le colonne
    boolean vettColonneLinkSeg1[]=new boolean[7];
    //vettore delle azioni dei link per le colonne
    String vettAzioniColonneLinkSeg1[]=new String[7];
    //vettore delle icone dei link per le colonne
    String vettIconeColonneLinkSeg1[]=new String[7];
    //vettore che indica se una colonna è visibile
    boolean vettColonneVisibiliSeg1[]=new boolean[7];
    
    vettIntestazioniSeg1[0]="&nbsp;";
    vettIntestazioniSeg1[1]="Cognome";
    vettIntestazioniSeg1[2]="Nome";
    vettIntestazioniSeg1[3]="Codice Fiscale";
    vettIntestazioniSeg1[4]="Data Nascita";
    vettIntestazioniSeg1[5]="Data App.to";
    vettIntestazioniSeg1[6]="Stato occup.le";

    vettColonneSeg1[0]="CDNLAVORATORE";
    vettColonneSeg1[1]="STRCOGNOME";
    vettColonneSeg1[2]="STRNOME";
    vettColonneSeg1[3]="STRCODICEFISCALE";
    vettColonneSeg1[4]="DATNASC";
    vettColonneSeg1[5]="DATAAPPUNTAMENTO";
    vettColonneSeg1[6]="STRDESCSTATOOCC";

    vettColonneLinkSeg1[0]=true;
    vettColonneLinkSeg1[1]=false;
    vettColonneLinkSeg1[2]=false;
    vettColonneLinkSeg1[3]=false;
    vettColonneLinkSeg1[4]=false;
    vettColonneLinkSeg1[5]=false;
    vettColonneLinkSeg1[6]=false;
    
    vettAzioniColonneLinkSeg1[0]="javascript:ApriSchedaLavoratore";
    vettAzioniColonneLinkSeg1[1]="";
    vettAzioniColonneLinkSeg1[2]="";
    vettAzioniColonneLinkSeg1[3]="";
    vettAzioniColonneLinkSeg1[4]="";
    vettAzioniColonneLinkSeg1[5]="";
    vettAzioniColonneLinkSeg1[6]="";
    
    vettIconeColonneLinkSeg1[0]="../../img/detail.gif";
    vettIconeColonneLinkSeg1[1]="";
    vettIconeColonneLinkSeg1[2]="";
    vettIconeColonneLinkSeg1[3]="";
    vettIconeColonneLinkSeg1[4]="";
    vettIconeColonneLinkSeg1[5]="";
    vettIconeColonneLinkSeg1[6]="";
    
    vettColonneVisibiliSeg1[0]=true;
    vettColonneVisibiliSeg1[1]=true;
    vettColonneVisibiliSeg1[2]=true;
    vettColonneVisibiliSeg1[3]=true;
    vettColonneVisibiliSeg1[4]=true;
    vettColonneVisibiliSeg1[5]=true;
    vettColonneVisibiliSeg1[6]=true;
    
    if (strVisibleApp.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="App.to";
      vettIconeBottoni[nDim]="../../img/agendina.gif";
      vettAzioniBottoni[nDim]="javascript:ApriAppuntamentoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleCont.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Con.to";
      vettIconeBottoni[nDim]="../../img/contatti.gif";
      vettAzioniBottoni[nDim]="javascript:ApriContattoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleEsiti.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Esiti";
      vettIconeBottoni[nDim]="";
      vettAzioniBottoni[nDim]="";
      nDim = nDim + 1;
    }
// Soggetti con appuntamenti e stato occupazionale variato 

 

    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioniSeg1, vettColonneSeg1, vettColonneLinkSeg1, vettAzioniColonneLinkSeg1, vettIconeColonneLinkSeg1, vettColonneVisibiliSeg1, vettIntestazioniBottoni, vettIconeBottoni, vettAzioniBottoni, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, isCpi);
 
 

    break;

  case 1002:
    //vettore delle intestazioni della lista
    String vettIntestazioniSeg2[]=new String[5];
    //vettore delle colonne presenti nella serviceResponse
    String vettColonneSeg2[]=new String[5];
    //vettore che permette di definire dei link per le colonne
    boolean vettColonneLinkSeg2[]=new boolean[5];
    //vettore delle azioni dei link per le colonne
    String vettAzioniColonneLinkSeg2[]=new String[5];
    //vettore delle icone dei link per le colonne
    String vettIconeColonneLinkSeg2[]=new String[5];
    //vettore che indica se una colonna è visibile
    boolean vettColonneVisibiliSeg2[]=new boolean[5];
    
    vettIntestazioniSeg2[0]="&nbsp;";
    vettIntestazioniSeg2[1]="Cognome";
    vettIntestazioniSeg2[2]="Nome";
    vettIntestazioniSeg2[3]="Codice Fiscale";
    vettIntestazioniSeg2[4]="Data Nascita";
    
    vettColonneSeg2[0]="CDNLAVORATORE";
    vettColonneSeg2[1]="STRCOGNOME";
    vettColonneSeg2[2]="STRNOME";
    vettColonneSeg2[3]="STRCODICEFISCALE";
    vettColonneSeg2[4]="DATNASC";
    
    vettColonneLinkSeg2[0]=true;
    vettColonneLinkSeg2[1]=false;
    vettColonneLinkSeg2[2]=false;
    vettColonneLinkSeg2[3]=false;
    vettColonneLinkSeg2[4]=false;
    
    vettAzioniColonneLinkSeg2[0]="javascript:ApriSchedaLavoratore";
    vettAzioniColonneLinkSeg2[1]="";
    vettAzioniColonneLinkSeg2[2]="";
    vettAzioniColonneLinkSeg2[3]="";
    vettAzioniColonneLinkSeg2[4]="";
    
    vettIconeColonneLinkSeg2[0]="../../img/detail.gif";
    vettIconeColonneLinkSeg2[1]="";
    vettIconeColonneLinkSeg2[2]="";
    vettIconeColonneLinkSeg2[3]="";
    vettIconeColonneLinkSeg2[4]="";
    
    vettColonneVisibiliSeg2[0]=true;
    vettColonneVisibiliSeg2[1]=true;
    vettColonneVisibiliSeg2[2]=true;
    vettColonneVisibiliSeg2[3]=true;
    vettColonneVisibiliSeg2[4]=true;
    
    if (strVisibleApp.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="App.to";
      vettIconeBottoni[nDim]="../../img/agendina.gif";
      vettAzioniBottoni[nDim]="javascript:ApriAppuntamentoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleCont.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Con.to";
      vettIconeBottoni[nDim]="../../img/contatti.gif";
      vettAzioniBottoni[nDim]="javascript:ApriContattoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleEsiti.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Esiti";
      vettIconeBottoni[nDim]="";
      vettAzioniBottoni[nDim]="";
      nDim = nDim + 1;
    }
//Soggetti senza disponibilità territoriale
    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioniSeg2, vettColonneSeg2, vettColonneLinkSeg2, vettAzioniColonneLinkSeg2, vettIconeColonneLinkSeg2, vettColonneVisibiliSeg2, vettIntestazioniBottoni, vettIconeBottoni, vettAzioniBottoni, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, true);
    break;

  case 1003:
    //vettore delle intestazioni della lista
    String vettIntestazioniSeg3[]=new String[6];
    //vettore delle colonne presenti nella serviceResponse
    String vettColonneSeg3[]=new String[6];
    //vettore che permette di definire dei link per le colonne
    boolean vettColonneLinkSeg3[]=new boolean[6];
    //vettore delle azioni dei link per le colonne
    String vettAzioniColonneLinkSeg3[]=new String[6];
    //vettore delle icone dei link per le colonne
    String vettIconeColonneLinkSeg3[]=new String[6];
    //vettore che indica se una colonna è visibile
    boolean vettColonneVisibiliSeg3[]=new boolean[6];
    
    vettIntestazioniSeg3[0]="&nbsp;";
    vettIntestazioniSeg3[1]="Cognome";
    vettIntestazioniSeg3[2]="Nome";
    vettIntestazioniSeg3[3]="Codice Fiscale";
    vettIntestazioniSeg3[4]="Data Nascita";
    vettIntestazioniSeg3[5]="Esclusioni";
    
    vettColonneSeg3[0]="CDNLAVORATORE";
    vettColonneSeg3[1]="STRCOGNOME";
    vettColonneSeg3[2]="STRNOME";
    vettColonneSeg3[3]="STRCODICEFISCALE";
    vettColonneSeg3[4]="DATNASC";
    vettColonneSeg3[5]="NUMESCLUSIONI";    
    
    vettColonneLinkSeg3[0]=true;
    vettColonneLinkSeg3[1]=false;
    vettColonneLinkSeg3[2]=false;
    vettColonneLinkSeg3[3]=false;
    vettColonneLinkSeg3[4]=false;
    vettColonneLinkSeg3[5]=false;    
    
    vettAzioniColonneLinkSeg3[0]="javascript:ApriSchedaLavoratore";
    vettAzioniColonneLinkSeg3[1]="";
    vettAzioniColonneLinkSeg3[2]="";
    vettAzioniColonneLinkSeg3[3]="";
    vettAzioniColonneLinkSeg3[4]="";
    vettAzioniColonneLinkSeg3[5]="";
    
    vettIconeColonneLinkSeg3[0]="../../img/detail.gif";
    vettIconeColonneLinkSeg3[1]="";
    vettIconeColonneLinkSeg3[2]="";
    vettIconeColonneLinkSeg3[3]="";
    vettIconeColonneLinkSeg3[4]="";
    vettIconeColonneLinkSeg3[5]="";
    
    vettColonneVisibiliSeg3[0]=true;
    vettColonneVisibiliSeg3[1]=true;
    vettColonneVisibiliSeg3[2]=true;
    vettColonneVisibiliSeg3[3]=true;
    vettColonneVisibiliSeg3[4]=true;
    vettColonneVisibiliSeg3[5]=true;
    
    if (strVisibleApp.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="App.to";
      vettIconeBottoni[nDim]="../../img/agendina.gif";
      vettAzioniBottoni[nDim]="javascript:ApriAppuntamentoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleCont.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Con.to";
      vettIconeBottoni[nDim]="../../img/contatti.gif";
      vettAzioniBottoni[nDim]="javascript:ApriContattoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleEsiti.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Esiti";
      vettIconeBottoni[nDim]="";
      vettAzioniBottoni[nDim]="";
      nDim = nDim + 1;
    }
//Soggetti esclusi dalla rosa dopo la DID
    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioniSeg3, vettColonneSeg3, vettColonneLinkSeg3, vettAzioniColonneLinkSeg3, vettIconeColonneLinkSeg3, vettColonneVisibiliSeg3, vettIntestazioniBottoni, vettIconeBottoni, vettAzioniBottoni, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, true);
    break;
 

  case 1004:
    //vettore delle intestazioni della lista
    String vettIntestazioniSeg4[]=new String[7];
    //vettore delle colonne presenti nella serviceResponse
    String vettColonneSeg4[]=new String[7];
    //vettore che permette di definire dei link per le colonne
    boolean vettColonneLinkSeg4[]=new boolean[7];
    //vettore delle azioni dei link per le colonne
    String vettAzioniColonneLinkSeg4[]=new String[7];
    //vettore delle icone dei link per le colonne
    String vettIconeColonneLinkSeg4[]=new String[7];
    //vettore che indica se una colonna è visibile
    boolean vettColonneVisibiliSeg4[]=new boolean[7];
    
    vettIntestazioniSeg4[0]="&nbsp;";
    vettIntestazioniSeg4[1]="Cognome";
    vettIntestazioniSeg4[2]="Nome";
    vettIntestazioniSeg4[3]="Codice Fiscale";
    vettIntestazioniSeg4[4]="CPI Titolare";
    vettIntestazioniSeg4[5]="Inizio validit&agrave;";
    vettIntestazioniSeg4[6]="Fine validit&agrave;";
    
    vettColonneSeg4[0]="CDNLAVORATORE";
    vettColonneSeg4[1]="STRCOGNOME";
    vettColonneSeg4[2]="STRNOME";
    vettColonneSeg4[3]="STRCODICEFISCALE";
    vettColonneSeg4[4]="CPITIT";    
    vettColonneSeg4[5]="DATINIZIOCURR";    
    vettColonneSeg4[6]="DATFINECURR";            
    
    vettColonneLinkSeg4[0]=true;
    vettColonneLinkSeg4[1]=false;
    vettColonneLinkSeg4[2]=false;
    vettColonneLinkSeg4[3]=false;
    vettColonneLinkSeg4[4]=false;    
    vettColonneLinkSeg4[5]=false;        
    vettColonneLinkSeg4[6]=false;    
    
    vettAzioniColonneLinkSeg4[0]="javascript:ApriSchedaLavoratore";
    vettAzioniColonneLinkSeg4[1]="";
    vettAzioniColonneLinkSeg4[2]="";
    vettAzioniColonneLinkSeg4[3]="";
    vettAzioniColonneLinkSeg4[4]="";
    vettAzioniColonneLinkSeg4[5]="";
    vettAzioniColonneLinkSeg4[6]="";        
    
    vettIconeColonneLinkSeg4[0]="../../img/detail.gif";
    vettIconeColonneLinkSeg4[1]="";
    vettIconeColonneLinkSeg4[2]="";
    vettIconeColonneLinkSeg4[3]="";
    vettIconeColonneLinkSeg4[4]="";
    vettIconeColonneLinkSeg4[5]="";
    vettIconeColonneLinkSeg4[6]="";        
    
    vettColonneVisibiliSeg4[0]=true;
    vettColonneVisibiliSeg4[1]=true;
    vettColonneVisibiliSeg4[2]=true;
    vettColonneVisibiliSeg4[3]=true;
    vettColonneVisibiliSeg4[4]=true;
    vettColonneVisibiliSeg4[5]=true;    
    vettColonneVisibiliSeg4[6]=true;    
    
    if (strVisibleApp.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="App.to";
      vettIconeBottoni[nDim]="../../img/agendina.gif";
      vettAzioniBottoni[nDim]="javascript:ApriAppuntamentoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleCont.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Con.to";
      vettIconeBottoni[nDim]="../../img/contatti.gif";
      vettAzioniBottoni[nDim]="javascript:ApriContattoLavoratore";
      nDim = nDim + 1;
    }
    if (strVisibleEsiti.compareTo("S") == 0) {
      vettIntestazioniBottoni[nDim]="Esiti";
      vettIconeBottoni[nDim]="";
      vettAzioniBottoni[nDim]="";
      nDim = nDim + 1;
    }
//Soggetti pronti all'incrocio senza mansioni
    strHtmlLista = StampaLista (codCpi, strTitoloLista, strFiltra, vettIntestazioniSeg4, vettColonneSeg4, vettColonneLinkSeg4, vettAzioniColonneLinkSeg4, vettIconeColonneLinkSeg4, vettColonneVisibiliSeg4, vettIntestazioniBottoni, vettIconeBottoni, vettAzioniBottoni, vettIntestazioniBottoniComuni, vettIconeBottoniComuni, vettAzioniBottoniComuni, rows_VectorScadenze, nCodScadenza, true);

    break;
}
%>
<center>
<table><tr><td>
<%=strHtmlLista%>
</td></tr></table>
</center>
<%
if ((nCodScadenza != 1001) && (nCodScadenza != 1002) && (nCodScadenza != 5) && (nCodScadenza != 1003) && (nCodScadenza!=1004)) {
  %>
  <center>
  <table>
  <tr><td>
  <input class="pulsanti" type="button" name="btnBack" value="Torna alla pagina di ricerca" onclick="javascript:TornaAllaRicerca();"/>
  </td></tr>
  
  <%
  	if (canPrint && numRecords > 0 && nCodScadenza == 1) {
  %>  		
      <tr><td>
    <center> <input type="button" class="pulsanti" name="Stampa Lav Ricontattare" value="Stampa" onClick="StampaLavRicontattare()"> </center>
      </td></tr>
  <%
  	}
  %>
  
  
  </table>
  </center>
  
  
  
  
  
<%
} else {//sono nelle verifiche
%>
  <center>
  <table>
  <tr><td>
  <input class="pulsanti" type="button" name="btnBack" value="Torna alle verifiche" onclick="javascript:TornaAlleVerifiche();"/>
  </td></tr>
  </table>
  </center>
<%
}
%>
<input type="hidden" name="FILTRALISTA" value="<%=strFiltra%>">
<input type="hidden" name="SCADENZIARIO" value="<%=strCodScadenza%>"/>
<input type="hidden" name="MOTIVO_CONTATTO" value="<%=strCodiceContatto%>"/>
</af:form>
</body>
</html>