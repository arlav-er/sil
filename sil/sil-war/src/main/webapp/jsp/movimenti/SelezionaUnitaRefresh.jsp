1<!-- @author: Paolo Roccetti - Agosto 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.movimenti.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	//Recupero dati
	String prgMovimentoApp = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTOAPP");
	//il parametro PRGMOBILITAISCRAPP è valorizzato quando la pagina viene chiamata dalla validazione manuale mobilità
	String prgMobilitaApp = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOBILITAISCRAPP");
	String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
	String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
	String funz_agg = StringUtils.getAttributeStrNotNull(serviceRequest, "FUNZ_AGGIORNAMENTO");	
    String modificataAz = StringUtils.getAttributeStrNotNull(serviceRequest, "MODIFICATAAZIENDA");
    String daMovimentiNew = StringUtils.getAttributeStrNotNull(serviceRequest, "daMovimentiNew");

	//Mi dice se sto operando per l'azienda principale o per quella utilizzatrice
	String contesto = StringUtils.getAttributeStrNotNull(serviceRequest, "CONTESTO");
	boolean contestoAzienda = contesto.equalsIgnoreCase("AZIENDA");
	boolean contestoAzUtil = contesto.equalsIgnoreCase("AZUTIL");
	//boolean contestoEntrambeAZ = contesto.equalsIgnoreCase("ENTRAMBEAZ");
	
	String ccnlaz="";
  	String strragionesociale="";
    String strpartitaiva="";
    String strcodicefiscale="";
    String strindirizzo="";
    String comune_az="";
    String codcom="";
    String prov_az="";
    String codtipoazienda="";
    String descrtipoaz="";
    String natgiuraz="";
    String strnumalbointerinali="";
    String strnumregistrocommitt="";
    String strtel="";
    String strfax="";
    String strcap="";
    String strdescrizioneccnl=""; 
    String codateco="";
    String strdesatecouaz="";
    String tipoateco="";
    String strpatinail="";
    String strnumeroinps="";
    String flgdatiok="";
    String codnatgiuridica="";
    String strreferente="";
    String codcpi="";
    String codregione="";
    String strEmail ="";
    
    String pIvaMov = "";
 	String ragSocMov = "";
  	String codCcnlMov = "";
  	String descrCcnlMov = "";
  	String numIscrAlboIntMov = "";
 	String indirMov = "";
  	String capMov = "";
  	String codAtecoMov = "";
  	String telMov = "";
  	String FaxMov = "";
  	String emailMov = "";
  	String esistonoDifferenze = "false";
  	
  	String cfSommAzEstera = "";
  	String ragSocSommAzEstera = "";
	
	SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW");
	
	if (!prgMovimentoApp.equals("")) {
		//Imposto i dati nell'oggetto in sessione
		NavigationCache azienda = null;
		if (sessionContainer.getAttribute("SCELTAUNITAAZIENDA") == null) {
	    	String[] fields = {"PRGMOVIMENTOAPP", "PRGAZIENDA", "PRGUNITA", "PRGAZIENDAUTIL", "PRGUNITAUTIL","MODIFICATAAZIENDA"};
	    	azienda = new NavigationCache(fields);
	    	azienda.enable();
	    	sessionContainer.setAttribute("SCELTAUNITAAZIENDA", azienda);
		} else {
			azienda = (NavigationCache) sessionContainer.getAttribute("SCELTAUNITAAZIENDA");
		}
	
		azienda.setField("PRGMOVIMENTOAPP", prgMovimentoApp);
		if (contestoAzienda) {
			azienda.setField("PRGAZIENDA", prgAzienda);	
			azienda.setField("PRGUNITA", prgUnita);	
			azienda.setField("MODIFICATAAZIENDA",modificataAz);
		} else if (contestoAzUtil) {
			azienda.setField("PRGAZIENDAUTIL", prgAzienda);	
			azienda.setField("PRGUNITAUTIL", prgUnita);
		}
	}
	else {
		NavigationCache azienda = null;
		if (sessionContainer.getAttribute("SCELTAUNITAAZIENDA_VALIDAZIONEMOB") == null) {
			String[] fields = {"PRGMOBILITAISCRAPP", "PRGAZIENDA", "PRGUNITA", "MODIFICATAAZIENDA"};
	    	azienda = new NavigationCache(fields);
	    	azienda.enable();
	    	sessionContainer.setAttribute("SCELTAUNITAAZIENDA_VALIDAZIONEMOB", azienda);
	   	} else {
			azienda = (NavigationCache) sessionContainer.getAttribute("SCELTAUNITAAZIENDA_VALIDAZIONEMOB");
		}
		azienda.setField("PRGMOBILITAISCRAPP", prgMobilitaApp);
		azienda.setField("PRGAZIENDA", prgAzienda);	
		azienda.setField("PRGUNITA", prgUnita);	
		azienda.setField("MODIFICATAAZIENDA",modificataAz);
	}
	
		if(dataOrigin != null) {
			pIvaMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strPartitaIvaAz");
			ragSocMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAz");
			codCcnlMov = StringUtils.getAttributeStrNotNull(dataOrigin, "codCCNLAz");
			descrCcnlMov = StringUtils.getAttributeStrNotNull(dataOrigin, "descrCCNLAz");
			numIscrAlboIntMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strNumAlboInterinali");
			indirMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAz");
			capMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strCapUAz");
			codAtecoMov = StringUtils.getAttributeStrNotNull(dataOrigin, "codAtecoUAz");
			telMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strTelUAz");
			FaxMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strFaxUAz");
			emailMov = StringUtils.getAttributeStrNotNull(dataOrigin, "strEmailUAz");
		}
	
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Refresh sessione per scelta utente</title>
  <script language="Javascript">
   function aggiornaSceltaAZ() {
     // alert(window.opener.document.Frm1.MODIFICATAAZIENDA.value);
      <%if(contestoAzienda) {%>
      	if(window.opener.document.Frm1.PRGAZIENDA!=null) { 
      		window.opener.document.Frm1.PRGAZIENDA.value = "<%=prgAzienda%>"; 
      	}
      	if(window.opener.document.Frm1.PRGUNITA != null) {
      		window.opener.document.Frm1.PRGUNITA.value = "<%=prgUnita%>"; 
      	}
     <%}
     
     if (!prgMovimentoApp.equals("") && daMovimentiNew.equals("S")) {  
     	SourceBean datiAzi = (SourceBean)serviceResponse.getAttribute("M_GET_DATI_AZI.ROWS.ROW");
  			if (datiAzi != null) {
  				ccnlaz = StringUtils.getAttributeStrNotNull(datiAzi, "ccnlaz");
  				strragionesociale= StringUtils.getAttributeStrNotNull(datiAzi, "strragionesociale");
        		strpartitaiva= StringUtils.getAttributeStrNotNull(datiAzi, "strpartitaiva");
        		strcodicefiscale= StringUtils.getAttributeStrNotNull(datiAzi, "strcodicefiscale");
        		strindirizzo= StringUtils.getAttributeStrNotNull(datiAzi, "strindirizzo");
        		comune_az= StringUtils.getAttributeStrNotNull(datiAzi, "comune_az");
        		codcom= StringUtils.getAttributeStrNotNull(datiAzi, "codcom");
        		prov_az= StringUtils.getAttributeStrNotNull(datiAzi, "prov_az");
        		codtipoazienda= StringUtils.getAttributeStrNotNull(datiAzi, "codtipoazienda");
        		descrtipoaz= StringUtils.getAttributeStrNotNull(datiAzi, "descrtipoaz");
        		natgiuraz= StringUtils.getAttributeStrNotNull(datiAzi, "natgiuraz");
        		strnumalbointerinali= StringUtils.getAttributeStrNotNull(datiAzi, "strnumalbointerinali");
        		strnumregistrocommitt= StringUtils.getAttributeStrNotNull(datiAzi, "strnumregistrocommitt");
        		strtel= StringUtils.getAttributeStrNotNull(datiAzi, "strtel");
        		strfax= StringUtils.getAttributeStrNotNull(datiAzi, "strfax");
        		strcap= StringUtils.getAttributeStrNotNull(datiAzi, "strcap");
        		strdescrizioneccnl= StringUtils.getAttributeStrNotNull(datiAzi, "strdescrizioneccnl"); 
        		codateco= StringUtils.getAttributeStrNotNull(datiAzi, "codateco");
        		strdesatecouaz= StringUtils.getAttributeStrNotNull(datiAzi, "strdesatecouaz");
        		tipoateco= StringUtils.getAttributeStrNotNull(datiAzi, "tipoateco");
        		strpatinail= StringUtils.getAttributeStrNotNull(datiAzi, "strpatinail");
        		strnumeroinps= StringUtils.getAttributeStrNotNull(datiAzi, "strnumeroinps");
        		flgdatiok= StringUtils.getAttributeStrNotNull(datiAzi, "flgdatiok");
        		codnatgiuridica= StringUtils.getAttributeStrNotNull(datiAzi, "codnatgiuridica");
        		strreferente= StringUtils.getAttributeStrNotNull(datiAzi, "strreferente");
        		codcpi= StringUtils.getAttributeStrNotNull(datiAzi, "codcpi");
        		strEmail= StringUtils.getAttributeStrNotNull(datiAzi, "strEmail");
        		cfSommAzEstera = StringUtils.getAttributeStrNotNull(datiAzi, "CODFISCAZESTERA");
        	  	ragSocSommAzEstera = StringUtils.getAttributeStrNotNull(datiAzi, "RAGSOCAZESTERA");
        	}
	 	
	 		int diffCounter = 0;
  			if (!pIvaMov.equals("") && !pIvaMov.equalsIgnoreCase(strpartitaiva)) {diffCounter += 1;}
  			if (!ragSocMov.equals("") && !ragSocMov.equalsIgnoreCase(strragionesociale)) {diffCounter += 1;}
  			if (!codCcnlMov.equals("") && !codCcnlMov.equalsIgnoreCase(ccnlaz)) {diffCounter += 1;}
  			if (!numIscrAlboIntMov.equals("") && !numIscrAlboIntMov.equalsIgnoreCase(strnumalbointerinali)) {diffCounter += 1;}
  			if (!indirMov.equals("") && !indirMov.equalsIgnoreCase(strindirizzo)) {diffCounter += 1;}
  			if (!capMov.equals("") && !capMov.equalsIgnoreCase(strcap)) {diffCounter += 1;}
  			if (!codAtecoMov.equals("") && !codAtecoMov.equalsIgnoreCase(codateco)){diffCounter += 1;}
  			if (!telMov.equals("") && !telMov.equalsIgnoreCase(strtel)){diffCounter += 1;}
  			if (!FaxMov.equals("") && !FaxMov.equalsIgnoreCase(strfax)) {diffCounter += 1;}
  			if (!emailMov.equals("") && !emailMov.equalsIgnoreCase(strEmail)) {diffCounter += 1;} 
  		
  			if (diffCounter > 0) { esistonoDifferenze = "true"; } %>
   		
   		window.opener.document.Frm1.strPartitaIvaAz.value = "<%=strpartitaiva%>"; 
   		window.opener.document.Frm1.strRagioneSocialeAz.value = '<%=StringUtils.formatValue4Javascript(strragionesociale)%>'; 
   		if (window.opener.document.Frm1.strRagioneSocialeAz.value > 36) {
    		window.opener.document.Frm1.strRagioneSocialeAzTrunc.value = window.opener.document.Frm1.strRagioneSocialeAz.value.substring(0,34) + "...";
    	} 
 		else {
    		window.opener.document.Frm1.strRagioneSocialeAzTrunc.value = '<%=StringUtils.formatValue4Javascript(strragionesociale)%>'; 
  		}
   		window.opener.document.Frm1.strCodiceFiscaleAz.value = "<%=strcodicefiscale%>"; 
    	window.opener.document.Frm1.strIndirizzoUAzVisualizzato.value = "<%=strindirizzo%> ( <%=comune_az%>, <%=strcap%> )";  
  		window.opener.document.Frm1.codDescrCCNLAz.value = "<%=ccnlaz%> - <%=strdescrizioneccnl%>";
     	window.opener.document.Frm1.codAtecoStrDesAttivitaAz.value = "<%=codateco%> - <%=strdesatecouaz%>";
   		window.opener.document.Frm1.strTelUAz.value = "<%=strtel%>";
   		window.opener.document.Frm1.strFaxUAz.value = "<%=strfax%>";
    	window.opener.document.Frm1.STRNUMREGISTROCOMMITT.value = "<%=strnumregistrocommitt%>";
   		window.opener.document.Frm1.CODTIPOAZIENDA.value = "<%=codtipoazienda%>";
  		window.opener.document.Frm1.DESCRTIPOAZIENDA.value = "<%=descrtipoaz%>";
    	window.opener.document.Frm1.STRNUMALBOINTERINALI.value = "<%=strnumalbointerinali%>";
    	window.opener.document.Frm1.natGiuridicaAz.value = "<%=natgiuraz%>";
    	window.opener.document.Frm1.ADD_MOVIMENTO.value = "aggiungiMov";
    	window.opener.document.Frm1.Differenze.value = "<%=esistonoDifferenze%>";
    	window.opener.document.Frm1.CODFISCAZESTERA.value = "<%=cfSommAzEstera%>";
    	window.opener.document.Frm1.RAGSOCAZESTERA.value = "<%=ragSocSommAzEstera%>";
   	<%}%>
   	 window.opener.<%=funz_agg%>();
     window.close();
   }	
  </script>
</head>

<body class="gestione" onload="aggiornaSceltaAZ()">

</body>  
  
  
  
  

