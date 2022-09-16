<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*,
                java.math.BigDecimal,
                java.net.URLEncoder" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  // NOTE: Attributi della pagina (pulsanti e link)
  String _page = (String) serviceRequest.getAttribute("PAGE");
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canDelete=true;//attributi.containsButton("aggiorna");
  //E' stato disabilitato il pulsante di cancellazione sulla lista (momentaneamente)
  canDelete=false;
  boolean canPrint = attributi.containsButton("MOBLIS");
  boolean canExportCsv = attributi.containsButton("MOBLISCSV");

  String resultConfigMob = serviceResponse.containsAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM").toString():"0";
  
  String cognome=StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
  String nome=StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
  //String CF=StringUtils.getAttributeStrNotNull(serviceRequest,"CF");
  String CF=StringUtils.getAttributeStrNotNull(serviceRequest,"codiceFiscaleLavoratore");
  String FlgCFok=StringUtils.getAttributeStrNotNull(serviceRequest,"FlgCFok");
  String CDNLavoratore=StringUtils.getAttributeStrNotNull(serviceRequest,"CDNLavoratore");
  String CodCPI=StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
  String CodProv = StringUtils.getAttributeStrNotNull(serviceRequest,"CodProv");  
  String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGAZIENDA"); 
  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest,"prgUnita");
  
  String codTipoAz=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAz");
  String descrTipoAz=StringUtils.getAttributeStrNotNull(serviceRequest,"descrTipoAz");
  String FLGDATIOK=StringUtils.getAttributeStrNotNull(serviceRequest,"FLGDATIOK");
  String CODNATGIURIDICA=StringUtils.getAttributeStrNotNull(serviceRequest,"CODNATGIURIDICA");
  String IndirizzoAzienda=StringUtils.getAttributeStrNotNull(serviceRequest,"IndirizzoAzienda");
  String codFiscaleAzienda=StringUtils.getAttributeStrNotNull(serviceRequest,"IndirizzoAzienda");
  String pIva=StringUtils.getAttributeStrNotNull(serviceRequest,"pIva");
  String ragioneSociale=StringUtils.getAttributeStrNotNull(serviceRequest,"ragioneSociale");
  
  String CodMotivoScor = StringUtils.getAttributeStrNotNull(serviceRequest,"CodMotivoScor");
  String mobInd = StringUtils.getAttributeStrNotNull(serviceRequest,"mobInd");
  String successUpdate = StringUtils.getAttributeStrNotNull(serviceResponse, "M_AGGIORNAMOBRICERCA.UPDATE_OK");
  String flgNonImprenditore=StringUtils.getAttributeStrNotNull(serviceRequest,"flgNonImprenditore");
  String flgCasoDubbio = StringUtils.getAttributeStrNotNull(serviceRequest,"flgCasoDubbio");
  
  Vector rowsTipoListaSb = serviceResponse.getAttributeAsVector("M_GetDeMbTipo.ROWS.ROW");
  Vector rowsMotivoFine = serviceResponse.getAttributeAsVector("M_MO_MOTIVO_FINE.ROWS.ROW");
  Vector rowsCPI = serviceResponse.getAttributeAsVector("M_ELENCOCPI.ROWS.ROW");
  int sizeTipoLista = rowsTipoListaSb.size();
  int sizeMotivoFine = rowsMotivoFine.size();
  int sizeCPI = rowsCPI.size();
  String descTipoLista = "";
  String descStatoLista = "";
  String descTipoMotivo = "";
  String descCPI = "";
  String CodTipoLista = "";
  String CodMotFineMob = "";
  Vector CodTipoListaVet = null;
  Vector CodMotFineListaVet = null;
  Vector CodStatoMob = null;
  
  for (int j=0;j<sizeCPI;j++) {
	SourceBean rowCurr = (SourceBean)rowsCPI.get(j);
	String codiceCPI = rowCurr.getAttribute("CODICE").toString().trim();
	if (codiceCPI.equalsIgnoreCase(CodCPI)) {
		descCPI = StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");
		break;
	}
  }
		
  
  
  //INIT-PARTE-TEMP
  if (Sottosistema.MO.isOff()) {
  	CodTipoLista = StringUtils.getAttributeStrNotNull(serviceRequest,"CodTipoLista");
  	for (int j=0;j<sizeTipoLista;j++) {
  		SourceBean rowCurr = (SourceBean)rowsTipoListaSb.get(j);
  		String codiceLista = rowCurr.getAttribute("CODICE").toString().trim();
  		if (codiceLista.equalsIgnoreCase(CodTipoLista)) {
  			if (descTipoLista.equals("")) {
  				descTipoLista = StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");	
  			}
  			else {
  				descTipoLista = descTipoLista + ", " + StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");
  			}
  			break;
  		}
  	}
  } 
  else {
  //END-PARTE-TEMP
    if (serviceRequest.containsAttribute("aggiorna_stato")) {
    	String listTipi = StringUtils.getAttributeStrNotNull(serviceRequest, "CodTipoLista");
    	CodTipoListaVet = StringUtils.split(listTipi, ",");	
    }
    else {
    	CodTipoListaVet = serviceRequest.getAttributeAsVector("CodTipoLista");	
    }
	if(CodTipoListaVet.size() > 0) {		
		for(int i=0; i<CodTipoListaVet.size(); i++) {
		  	if(!CodTipoListaVet.elementAt(i).equals("")) {
		  		String tipoListaCurr = CodTipoListaVet.elementAt(i).toString().trim();
		  		for (int j=0;j<sizeTipoLista;j++) {
		  	  		SourceBean rowCurr = (SourceBean)rowsTipoListaSb.get(j);
		  	  		String codiceLista = rowCurr.getAttribute("CODICE").toString().trim();
		  	  		if (codiceLista.equalsIgnoreCase(tipoListaCurr)) {
		  	  			if (descTipoLista.equals("")) {
		  	  				descTipoLista = StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");	
		  	  			}
		  	  			else {
		  	  				descTipoLista = descTipoLista + ", " + StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");
		  	  			}
		  	  			break;
		  	  		}
		  	  	}
			  	if(CodTipoLista.length() > 0) {
				 	CodTipoLista = CodTipoLista + "," + tipoListaCurr;
				}
			  	else {
				 	CodTipoLista += tipoListaCurr; 
			  	}
			}
		}
	  }
	
	//INIT-PARTE-TEMP
	if (serviceRequest.containsAttribute("aggiorna_stato")) {
    	String listMotivi = StringUtils.getAttributeStrNotNull(serviceRequest, "CodMotivoMob");
    	CodMotFineListaVet = StringUtils.split(listMotivi, ",");	
    }
    else {
    	CodMotFineListaVet = serviceRequest.getAttributeAsVector("CodMotivoMob");	
    }
	
	if(CodMotFineListaVet.size() > 0) {		
		for(int i=0; i<CodMotFineListaVet.size(); i++) {
		  	if(!CodMotFineListaVet.elementAt(i).equals("")) {
		  		String tipoMotivoCurr = CodMotFineListaVet.elementAt(i).toString().trim();
		  		for (int j=0;j<sizeMotivoFine;j++) {
		  	  		SourceBean rowCurr = (SourceBean)rowsMotivoFine.get(j);
		  	  		String codiceMotivo = rowCurr.getAttribute("CODICE").toString().trim();
		  	  		if (codiceMotivo.equalsIgnoreCase(tipoMotivoCurr)) {
		  	  			if (descTipoMotivo.equals("")) {
		  	  				descTipoMotivo = StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");	
		  	  			}
		  	  			else {
		  	  				descTipoMotivo = descTipoMotivo + ", " + StringUtils.getAttributeStrNotNull(rowCurr,"DESCRIZIONE");
		  	  			}
		  	  			break;
		  	  		}
		  	  	}
			  	if(CodMotFineMob.length() > 0) {
			  		CodMotFineMob = CodMotFineMob + "," + tipoMotivoCurr;
				}
			  	else {
			  		CodMotFineMob += tipoMotivoCurr; 
			  	}
			}
		}
	}
	 //END-PARTE-TEMP
  }
 
  
  String codStatiMob = "";
  if (serviceRequest.containsAttribute("aggiorna_stato")) {
	String statiMobRicerca = StringUtils.getAttributeStrNotNull(serviceRequest, "CodStatoMob");
	CodStatoMob = StringUtils.split(statiMobRicerca, ",");
  }
  else {
    CodStatoMob = serviceRequest.getAttributeAsVector("CodStatoMob");  
  }
  if(CodStatoMob.size()!=0){
	  for(int i=0;i<CodStatoMob.size();i++){
		  if(codStatiMob.equals("")){
			  codStatiMob += CodStatoMob.get(i);
		  }else{
			  codStatiMob = codStatiMob + ", " + CodStatoMob.get(i);
		  }
	  }
  }
  
  String datcrtda=StringUtils.getAttributeStrNotNull(serviceRequest,"datcrtda");
  String datcrta=StringUtils.getAttributeStrNotNull(serviceRequest,"datcrta");      
  String datfineda=StringUtils.getAttributeStrNotNull(serviceRequest,"datfineda");
  String datfinea=StringUtils.getAttributeStrNotNull(serviceRequest,"datfinea");      
  String datinizioda=StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioda");
  String datinizioa=StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioa");
  
  String datdomandada=StringUtils.getAttributeStrNotNull(serviceRequest,"datdomandada");
  String datdomandaa=StringUtils.getAttributeStrNotNull(serviceRequest,"datdomandaa");
  String datmaxdiffda=StringUtils.getAttributeStrNotNull(serviceRequest,"datmaxdiffda");
  String datmaxdiffa=StringUtils.getAttributeStrNotNull(serviceRequest,"datmaxdiffa");      
  String tipoRicerca=StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
  String numDelReg=StringUtils.getAttributeStrNotNull(serviceRequest,"numDelReg");
  String _funzione=serviceRequest.getAttribute("CDNFUNZIONE").toString();
  Vector listaStati = serviceResponse.getAttributeAsVector("M_GETSTATOMOB.ROWS.ROW");

  HashMap statiRichiesta = new HashMap();
  
  for(int i=0;i<listaStati.size();i++){
	  SourceBean stato = (SourceBean)listaStati.get(i);
	  BigDecimal key = (BigDecimal)stato.getAttribute("codice");
	  String value = (String)stato.getAttribute("descrizione");
	  statiRichiesta.put(key, value);
 }
  
  String filtroStato = "";
  
  if(codStatiMob.length()!=0){
	  StringTokenizer tk = new StringTokenizer(codStatiMob, ", ");
		  while(tk.hasMoreElements()){
			  BigDecimal val = new BigDecimal(tk.nextToken());
			  if(filtroStato.length()==0){
				  filtroStato += statiRichiesta.get(val);
			  }else{
				  filtroStato += ", " + statiRichiesta.get(val);
			  }
		  }
  }
  
  String parameters= "&cognome=" + cognome + "&nome=" + nome + 
  					"&flgcfok="+FlgCFok+"&cdnlavoratore="+CDNLavoratore+
  					"&codiceFiscaleLavoratore=" + CF + "&CodCPI=" + CodCPI + 
  					"&CodProv=" + CodProv + 
  					"&prgAzienda=" + prgAzienda + "&prgUnita=" + prgUnita +
  					"&codTipoAz="+codTipoAz+"&descrTipoAz="+descrTipoAz+"&FLGDATIOK="+FLGDATIOK+
  					"&CODNATGIURIDICA="+CODNATGIURIDICA+"&IndirizzoAzienda="+IndirizzoAzienda+
  					"&codFiscaleAzienda="+codFiscaleAzienda+"&pIva="+pIva+"&ragioneSociale="+URLEncoder.encode(ragioneSociale)+
  					"&CodMotivoScor=" + CodMotivoScor + "&mobInd=" + mobInd +
  					"&CodTipoLista=" + CodTipoLista + "&datcrtda=" + datcrtda + 
  					"&datcrta=" + datcrta + "&datfineda=" + datfineda + 
  					"&datfinea=" + datfinea + "&datinizioda=" + datinizioda + 
  					"&datinizioa=" + datinizioa + "&datmaxdiffda=" + datmaxdiffda +
  					"&datdomandada=" + datdomandada + "&datdomandaa=" + datdomandaa +
					"&numDelReg=" + numDelReg +"&flgNonImprenditore="+flgNonImprenditore+"&flgCasoDubbio="+flgCasoDubbio+
  					"&datmaxdiffa=" + datmaxdiffa + "&tipoRicerca=" + tipoRicerca + "&CDNFUNZIONE=" + _funzione;		
					
					//INIT-PARTE-TEMP
					  if (Sottosistema.MO.isOff()) {	
					  } else {
					//END-PARTE-TEMP  
		  					String codMotivoScor_H	= StringUtils.getAttributeStrNotNull(serviceRequest,"codMotivoScor_H");
		  					String codProv_H		= StringUtils.getAttributeStrNotNull(serviceRequest,"codProv_H");
		  					String mobInd_H			= StringUtils.getAttributeStrNotNull(serviceRequest,"mobInd_H");	
		  					String datinizioIndDa	= StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioIndDa");
		  					String datinizioIndA	= StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioIndA");	
		  					String datfineIndDa		= StringUtils.getAttributeStrNotNull(serviceRequest,"datfineIndDa");	
		  					String datfineIndA		= StringUtils.getAttributeStrNotNull(serviceRequest,"datfineIndA");  					
		  					String soggettiCM 		= StringUtils.getAttributeStrNotNull(serviceRequest,"SoggettiCM");
		  					String soggettiConMov 	= StringUtils.getAttributeStrNotNull(serviceRequest,"SoggettiConMov");
		  					String flgMobAperte		= StringUtils.getAttributeStrNotNull(serviceRequest,"FlgMobAperte");
		  					String flgMobStor     = StringUtils.getAttributeStrNotNull(serviceRequest,"FlgMobStor");
		  					String flgEscludiMotFineMob = StringUtils.getAttributeStrNotNull(serviceRequest,"FlgEscludiFineMob");
		  					String datInizioMovDa 	= StringUtils.getAttributeStrNotNull(serviceRequest,"datInizioMovDa");
		  					String datInizioMovA	= StringUtils.getAttributeStrNotNull(serviceRequest,"datInizioMovA");
		  					
		  					codMotivoScor_H= StringUtils.formatValue4Javascript(codMotivoScor_H);
		  					
		  					String addParameter = "&codMotivoScor_H=" + codMotivoScor_H + "&CodMotivoMob=" + CodMotFineMob +
		  										  "&codProv_H=" +  codProv_H + "&mobInd_H=" + mobInd_H +
		  										  "&datinizioIndDa=" + datinizioIndDa + "&datinizioIndA="+ datinizioIndA +
		  										  "&datfineIndDa=" + datfineIndDa + "&datfineIndA=" + datfineIndA +
		  										  "&SoggettiCM=" + soggettiCM + "&SoggettiConMov=" + soggettiConMov + 
		  										  "&FlgMobAperte=" + flgMobAperte + "&FlgEscludiFineMob=" + flgEscludiMotFineMob +
		  										  "&FlgMobStor=" + flgMobStor +
		  										  "&datInizioMovDa=" + datInizioMovDa + "&datInizioMovA=" + datInizioMovA;
		  										   
		  					parameters += addParameter;
		  					if(codStatiMob.length()>0){
		  						parameters += "&codStatoMob=" + codStatiMob;
		  					}
		  					
		  					
  					//INIT-PARTE-TEMP
					  }
					//END-PARTE-TEMP
  
  
  
  String queryString = null;
%>

<html>
<head>
 <title>Inf Storiche Stato Occupazionale</title>
 <link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
 <af:linkScript path="../../js/"/>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<script type="text/JavaScript">
function backToList()
{ // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
    
	url="AdapterHTTP?PAGE=MobilitaRicercaPage";
    url += "<%=parameters%>";             
    setWindowLocation(url);
}

function aggiornaStati(){
	
	var countSel = 0;
	
	if(form1.checkboxmob!=undefined){
		var lenChecks = form1.checkboxmob.length;
		
		url = "AdapterHTTP?PAGE=MobilitaRisultRicercaPage";
		url += "<%= parameters %>";
		url += "&statoric=" + form1.codStatoMob.value;
		url += "&aggiorna_stato=true&prgmobilita=";
		
		if(lenChecks!=undefined && lenChecks>1){
			for(i=0;i<lenChecks;i++){
				if(form1.checkboxmob[i].checked){
					countSel++;
					if(i==0){
						url += form1.checkboxmob[i].value;
					}else{
						url += ", " + form1.checkboxmob[i].value;
					}
				}
			}
		}else{
			if(form1.checkboxmob.checked){
				countSel++;
				url += form1.checkboxmob.value;
			}
		}
	}
	
	if(countSel==0){
		alert("Bisogna selezionare almeno una mobilità");
		return false;
	}else{
		setWindowLocation(url);
	}
}

function stampaMobilita(){
	apriGestioneDoc('RPT_MOBILITA','<%=parameters%>+&stampa=stampa','MOBLIS')
}

function esportaCsv() {    
	url="AdapterHTTP?PAGE=MobilitaRisultRicercaCsvPage";
    url += "<%=parameters%>";             
    window.location = url;
    return false;
}

function doSelect(pageInfoValida, pageInfoStoricizzata, cdnFunzione, cdnLavoratore, provenienza, prgMobilitaIscr, module, storicizzato ) {
	if (isInSubmit()) return;
	var s = "AdapterHTTP?PAGE_LISTA_MOB="+provenienza+"&CDNFUNZIONE="+cdnFunzione+"&CDNLAVORATORE="+cdnLavoratore;
	
	if (storicizzato=='true') {
		s+="&PAGE="+pageInfoStoricizzata;
		s+="&MODULE="+module;
		s+="&PRGMOBILITAISCR="+prgMobilitaIscr;
	}
	else {
		s+="&PAGE="+pageInfoValida;
	}
	setWindowLocation(s);
}

function checkAllCheckboxMob(sel){

	if(form1.checkboxmob!=undefined){
		var lenChecks = form1.checkboxmob.length;
		
		if(lenChecks>1){
			for(i=0;i<lenChecks;i++){
				form1.checkboxmob[i].checked = !form1.checkboxmob[i].checked;
			}
		}else{
			form1.checkboxmob.checked = !form1.checkboxmob.checked;
		}
	}
}

</script>

</head>

<body onload="checkError();rinfresca()" class="gestione">
<af:error/>
<br/><p class="titolo">RISULTATO DELLA RICERCA SULLA MOBILITA'</p>
 

<%String attr   = null;
  String attr1   = null;
  String valore = null;
  String valore1 = null;
  String txtOut = "";
  String labelStatoRich = "Stato della richiesta";
  if (resultConfigMob.equals("1")) {
	  labelStatoRich = "Stato della domanda";  
  }
  
%>
     <%attr= "cognome";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "cognome <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "nome";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "nome <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "CF";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "codice fiscale <strong>"+ valore +"</strong>; ";
       }%>
     <%if (!descTipoLista.equals("")) {
         txtOut += "Tipo lista <strong>"+ descTipoLista +"</strong>; ";   
       }%>
       <%attr= "datdomandada";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "  Data domanda da <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "datdomandaa";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data domanda a <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "datinizioda";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "  Data inizio da <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "datinizioa";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data inizio a <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "datfineda";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data fine da <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "datfinea";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data fine a <strong>"+ valore +"</strong>; ";
       }%>
       <%attr= "FlgMobAperte";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && valore.equals("S"))
       {txtOut += "Flag mobilità aperte <strong> SI </strong>; ";
       }%>
       <%attr= "FlgMobStor";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && valore.equals("S"))
       {txtOut += "Flag mobilità storicizzate <strong> SI </strong>; ";
       }%>
     <%attr= "datmaxdiffda";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data max differimento da <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "datmaxdiffa";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data max differimento a <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "datcrtda";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data CRT da <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "datcrta";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data CRT a <strong>"+ valore +"</strong>; ";
       }%>
     <%if(descCPI != null && !descCPI.equals(""))
       {txtOut += " Cpi comp.:<strong>" + descCPI + "</strong>; ";
       }
       if(codStatiMob.length()!=0){
    	txtOut += labelStatoRich + ":<strong>" + filtroStato + "</strong>; ";
       }
       %>
       
<%
//INIT-PARTE-TEMP
	  if (Sottosistema.MO.isOff()) {	
	  } else {
//END-PARTE-TEMP
	  attr= "NumDelReg";
	  valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && !valore.equals(""))
      {txtOut += " Numero Delibera Regionale:<strong>" + valore + "</strong>; ";
      }
	  attr= "ragioneSociale";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && !valore.equals(""))
      {txtOut += " Ragione Sociale:<strong>" + valore + "</strong>; ";
      }
      attr= "IndirizzoAzienda";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && !valore.equals(""))
      {txtOut += " Indirizzo (Comune):<strong>" + valore + "</strong>; ";
      }     
      attr1 = "FlgEscludiFineMob";
      valore1 = (String) serviceRequest.getAttribute(attr1);
      if(descTipoMotivo != null && !descTipoMotivo.equals("")) {
        if(valore1 != null && valore1.equals("S")) {
        	txtOut += " Motivo fine Mob diverso da:<strong>" + descTipoMotivo + "</strong>; ";
      	}
      	else {
      		txtOut += " Motivo fine Mob:<strong>" + descTipoMotivo + "</strong>; ";
      	}
      }
      attr= "codMotivoScor_H";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && !valore.equals(""))
      {txtOut += " Motivo scorrimento:<strong>" + valore + "</strong>; ";
      }
      attr= "codProv_H";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && !valore.equals(""))
      {txtOut += " Provenienza:<strong>" + valore + "</strong>; ";
      }
      attr= "mobInd_H";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && !valore.equals(""))
      {txtOut += " Mob Indennizzata:<strong>" + valore + "</strong>; ";
      }
      attr= "datinizioIndDa";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && !valore.equals(""))
      {txtOut += " data inizio da:<strong>" + valore + "</strong>; ";
      }
      attr= "datinizioIndA";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && !valore.equals(""))
      {txtOut += " data inizio a:<strong>" + valore + "</strong>; ";
      }
      attr= "datfineIndDa";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && !valore.equals(""))
      {txtOut += " data fine da:<strong>" + valore + "</strong>; ";
      }
      attr= "datfineIndA";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && !valore.equals(""))
      {txtOut += " data fine a:<strong>" + valore + "</strong>; ";
      }
      attr= "SoggettiCM";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && valore.equals("S"))
      {txtOut += " Soggetti L. 68/99 :<strong> Si </strong>";
      }
      attr= "SoggettiCM";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && valore.equals("S"))
      {txtOut += " Soggetti L. 68/99 :<strong> Si </strong>";
      }
      attr= "flgNonImprenditore";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && valore.equals("S"))
      {txtOut += " Tipologia datore di lavoro :<strong> Non imprenditore </strong>";
      }
      if(valore != null && valore.equals("N"))
      {txtOut += " Tipologia datore di lavoro :<strong> Imprenditore </strong>";
      }
      attr= "flgCasoDubbio";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && valore.equals("S"))
      {txtOut += " Caso dubbio :<strong> Si </strong>";
      }
      if(valore != null && valore.equals("N"))
      {txtOut += " Caso dubbio :<strong> No </strong>";
      }
      attr= "datInizioMovDa";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && !valore.equals(""))
      {txtOut += " data inizio movimento da:<strong>" + valore + "</strong>; ";
      }
      attr= "datInizioMovA";
      valore = (String) serviceRequest.getAttribute(attr);
      if(valore != null && !valore.equals(""))
      {txtOut += " data inizio movimento a:<strong>" + valore + "</strong>; ";
      }
%>
<%
//INIT-PARTE-TEMP
	  }
//END-PARTE-TEMP	  
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

<af:form dontValidate="true" name="form1">

<table width="96%" align="center">
		<tr valign="middle">
			<td align="left" class="azzurro_bianco">	
				<table width="100%">
					<tr>	
						<td>
							<input type="checkbox" value="" name="SelectAll" onclick="javascript:checkAllCheckboxMob(this.checked);"/>&nbsp;Seleziona/Deseleziona tutti
							<input id="empty" type="hidden" value="EMPTY" name="CHECKBOXADES"/>
							&nbsp;&nbsp;
						<td>
						<td>
							&nbsp; 
							<input type="image" src="../../img/add.gif" border="0" value="Associa" onclick="return aggiornaStati()">
							Associa selezionati &nbsp;&nbsp;
						</td>		
						<td>
							&nbsp; <%=labelStatoRich %> 
							 <af:comboBox name="codStatoMob" moduleName="M_GetStatoMob"
							  classNameBase="input" addBlank="true" title="<%=labelStatoRich %>" />
						</td>
					</tr>
				</table>						
			</td>			
			<td>&nbsp;</td>
		</tr>
	</table> 
	
	<br>

	<font color="red"><af:showErrors/></font>
	<font color="green"><af:showMessages prefix="M_AggiornaMobRicerca"/></font>

<br>

<af:list moduleName="M_MobilitaRicerca" canDelete="0" jsSelect="doSelect" configProviderClass="it.eng.sil.module.amministrazione.MobilitaListConfig"/>

<input type="hidden" name="CodTipoLista" value=""/>
<input type="hidden" name="CodMotivoMob" value=""/>

<table class="main">
  <tr><td>&nbsp;</td></tr>
  <tr>
  	<td colspan="2" align="center">
  		<input type="button" class="pulsanti" value="Torna alla ricerca" onClick="backToList()">
  		&nbsp;&nbsp;
  		 <% if(canPrint &&  Sottosistema.MO.isOn() ){%> 
  		<input type="button" class="pulsanti" value="Stampa Mobilità" onClick="stampaMobilita()">
  		 <%}%>
  		 
  		 <% if(canExportCsv){%>
  		 &nbsp;&nbsp;
  		<input type="button" class="pulsanti" value="Esporta in CSV" onClick="esportaCsv()">
  		 <%}%>
  		 
  	</td>
  </tr>
	
</table>


</af:form>

</body>
</html>