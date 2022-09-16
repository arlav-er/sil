<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../amministrazione/openPage.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,  
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %> 
<%--
	I PARAMETRI CARATTERISTICI SONO:
		PROVENIENZA
		TIPO_INFO
		dataInizio
		dataFine
		intestazione
		
	Per la stampa ed il ritorna indietro: la viariabile infoPerURL e' il vettore di parametri con le info selezionate
		da estrarre
--%>
<%
	// necessaria per il ritorno indietro dalla stampa
	String queryString=null;
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	String cdnLavoratore = (String )serviceRequest.getAttribute("CDNLAVORATORE"); 
	Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");
	String strCodiceUtenteCorrente=codiceUtenteCorrente.toString();
	String cdnLavoratoreEncrypt = EncryptDecryptUtils.encrypt(cdnLavoratore);
	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
	String provenienza = Utils.notNull((String) serviceRequest.getAttribute("PROVENIENZA")); 
	String dataInizio = Utils.notNull((String)serviceRequest.getAttribute("dataInizio"));
    String dataFine = Utils.notNull((String)serviceRequest.getAttribute("dataFine"));
    String intestazione = Utils.notNull((String)serviceRequest.getAttribute("intestazione"));
  	Object tipoInfo = serviceRequest.getAttribute("TIPO_INFO");
  	Vector infoDaVisualizzare = null;
  	String codCpi="";
	if (tipoInfo!=null) {		
		if (tipoInfo instanceof Vector)
			infoDaVisualizzare = (Vector)tipoInfo;
		else {
			infoDaVisualizzare = new Vector();
			infoDaVisualizzare.add(tipoInfo);
		}
	}
	else infoDaVisualizzare = new Vector();
	// l'intestazione di defalut e' PERCORSO LAVORATORE. Quindi se non era stata visualizzata bisogna utilizzare
	// il defalut. 
	// Pero' nel tornare indietro bisogna riportare il valore originario (nel caso di default stringa vuota)
  	String titolo = intestazione.equals("")?"Percorso lavoratore":intestazione;
  	//
  	String infoPerURL = "";
	for (int i=0;i<infoDaVisualizzare.size();i++){
		infoPerURL+="&TIPO_INFO="+infoDaVisualizzare.get(i);
	}
	// gestione visibilita' ed profilatura
    PageAttribs attributi = new PageAttribs(user, _current_page);
    ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
    filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	if (! filter.canViewLavoratore())
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	boolean canPrint = attributi.containsButton("STAMPA");	
	// era previsto che si potesse arrivare al percorso lavoratore anche dal menu generale.
	// String provenienzaMenu = (String) serviceRequest.getAttribute("PROVENIENZA_MENU");
  
    
	InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
	
	String showQualCodB = StringUtils.getAttributeStrNotNull(serviceRequest,"movAmmComObbligatoria");
	String showQualCodO = StringUtils.getAttributeStrNotNull(serviceRequest,"movAmmDichLav");
%>

<html>

<head>
  <title>Lista Percorso Lavoratore</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 <%@ include file="../documenti/_apriGestioneDoc.inc"%>
  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">
    function tornaAllaRicerca( ){
		var s = "AdapterHTTP?PAGE=CercaPercorsoLavoratorePage";
		s += "&CDNFUNZIONE=<%= cdnFunzione %>";
    	s += "&CDNLAVORATORE=<%= cdnLavoratore %>"; 
		s += "&dataInizio=<%=dataInizio%>";
		s += "&dataFine=<%=dataFine%>";
		s += "&intestazione=<%=StringUtils.formatValue4Javascript(intestazione)%>";
		s += "&TIPO_INFO=<%=infoPerURL%>";
		s += "&PROVENIENZA=vista";
		<%--s += "&PROVENIENZA_MENU=<%=provenienzaMenu%>";--%>
      	setWindowLocation(s);
    }
      
	function doSelect(urlBase, codMonoTipoInf, cdnLavoratore, chiaveDettaglio, cdnFunzione) {   
   		url = "AdapterHTTP?";
   		url += urlBase;
   		url += "&codMonoTipoInf="+codMonoTipoInf;
   		url += "&cdnLavoratore="+cdnLavoratore;
   		url += "&cdnFunzione="+cdnFunzione; 
   		<% codCpi = user.getCodRif(); %>
   		url += "&codCpi=<%=codCpi%>";
   		feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=400,top=30,left=180";
   		window.open(url, '_blank', feat);
   	}
   	
   	function stampa() {	   		
  		apriGestioneDoc('RPT_PERCORSO_LAV',
  			            '&cdnLavoratore=<%=Utils.notNull(cdnLavoratore)%>&cdnLavoratoreEncrypt=<%=Utils.notNull(cdnLavoratoreEncrypt)%>&datainizio=<%=dataInizio%>&dataFine=<%=dataFine%>'
  				            +'&intestazione=<%=intestazione%><%=infoPerURL%>&showQualCodB=<%=showQualCodB%>&showQualCodO=<%=showQualCodO%>',
    		            'PERC_LAV');
	}
	
	<%
       attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
    %>
  </SCRIPT>

</head>

<body class="gestione" onload="rinfresca()">

<%testata.show(out);%>
  
  <center>
    <font color="red">
      <af:showErrors />
    </font>
  </center>
  <p class="titolo"><%=titolo.toUpperCase()%></p>
      <p align="center">
          <af:list moduleName="M_ListPercorsoLavoratore" skipNavigationButton="0" jsSelect="doSelect" />          
          <center>
          	<input class="pulsanti" type="button" value="Impostazioni" onclick="tornaAllaRicerca()"/>
          	
          	<%-- se il requisito di non arrivare in questa pagina dal menu generale sara' definitivo,
          		 cancellare
          	if (provenienzaMenu.equals("GEN")) 
          		out.print(InfCorrentiAzienda.formatBackList(sessionContainer, "LISTALAVORATORIPERCORSOPAGE" ));
          	--%>   
          	<%if (canPrint) {%>
          		<input type="button" class="pulsanti" value="Stampa" onclick="stampa()">	    
          	<%}%>
          	</center>
            <br>
</body>
</html>