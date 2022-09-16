<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
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
	
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	String  _backpage  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"BACKPAGE"); 
		
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");

	//Profilatura ------------------------------------------------
 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
 	}

	String parameters = "";
	String queryString = "&cdnFunzione=" + cdnfunzione + "&PAGE=CigListaPage&stampa=stampa&BACKPAGE=" + _backpage;
	
	String lista = serviceRequest.containsAttribute("LISTA")?"1":"";
	if (!lista.equals("")) {
		parameters = parameters + "&LISTA="+lista;
		queryString = queryString + "&LISTA="+lista;
	}	
	
	String codTipoIscr = "";
	/*Vector CodTipoIscrVet = serviceRequest.getAttributeAsVector("codTipoIscr");
	if(CodTipoIscrVet.size() > 0) {		
		for(int i=0; i<CodTipoIscrVet.size(); i++) {
		  	if(!CodTipoIscrVet.elementAt(i).equals("")) {
			  	if(codTipoIscr.length() > 0) {
			  		codTipoIscr = codTipoIscr + "," + CodTipoIscrVet.elementAt(i);
				}
			  	else {
			  		codTipoIscr += CodTipoIscrVet.elementAt(i); 
			  	}
			}
		}
	}*/
	
	String tipoIscr = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"tipoIscr");
	if (!tipoIscr.equals("")) {
		tipoIscr = tipoIscr.trim();
		parameters = parameters + "&tipoIscr="+tipoIscr;
		queryString = queryString + "&tipoIscr="+tipoIscr;
	}
	
	String txtOut = "";
	boolean alreadyChosen = false;    
	String configurazione = serviceResponse.containsAttribute("M_GetConfigAltreIscr.CONFIGURAZIONE")?serviceResponse.getAttribute("M_GetConfigAltreIscr.CONFIGURAZIONE").toString():"0";
	String codiceFiscaleLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codiceFiscaleLavoratore");
	String cognome = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cognome");
	String nome = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"nome");
	
	String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"CDNLAVORATORE");
	if (!cdnLavoratore.equals("")) {
		parameters = parameters + "&cdnLavoratore="+cdnLavoratore;
		queryString = queryString + "&cdnLavoratore="+cdnLavoratore;
		alreadyChosen = true;
		txtOut += "Lavoratore (";
	
		if(!codiceFiscaleLavoratore.equals(""))
		{
			txtOut += "Codice Fiscale <strong>"+ codiceFiscaleLavoratore +"</strong>,";
		}
		if(cognome != null && !cognome.equals(""))
		{
			txtOut += "Cognome <strong>"+ cognome +"</strong>,";
		}
		if(nome != null && !nome.equals(""))
		{
			txtOut += "Nome <strong>"+ nome +"</strong>";
		}		
		txtOut += ")";
		
		parameters = parameters + "&codiceFiscaleLavoratore="+codiceFiscaleLavoratore+"&cognome="+cognome+"&nome="+nome;
		queryString = queryString + "&codiceFiscaleLavoratore="+codiceFiscaleLavoratore+"&cognome="+cognome+"&nome="+nome;	
	}

	String codFiscaleAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codFiscaleAzienda");
    String pIva = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"pIva");
    String ragioneSociale = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"ragioneSociale");
    String descrTipoAz = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"descrTipoAz");
    
    String prgAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PRGAZIENDA");
    if (!prgAzienda.equals("")) {
    	parameters = parameters + "&prgAzienda="+prgAzienda;
		queryString = queryString + "&prgAzienda="+prgAzienda;
    	if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
    	txtOut += "Azienda (";
    
		if(!codFiscaleAzienda.equals(""))
   		{
   			txtOut += "Codice Fiscale <strong>"+ codFiscaleAzienda +"</strong>,";
   		}
    	if(pIva != null && !pIva.equals(""))
    	{
    		txtOut += "Partita IVA <strong>"+ pIva +"</strong>,";
    	}
    	if(ragioneSociale != null && !ragioneSociale.equals(""))
    	{
    		txtOut += "Ragione Sociale <strong>"+ ragioneSociale +"</strong>,";
    	}		
    	if(descrTipoAz != null && !descrTipoAz.equals(""))
    	{
    		txtOut += "Tipo Azienda <strong>"+ descrTipoAz +"</strong>";
    	}
    	txtOut += ")";
        
    	parameters = parameters + "&codFiscaleAzienda="+codFiscaleAzienda+"&pIva="+pIva+"&ragioneSociale="+ragioneSociale+"&descrTipoAz="+descrTipoAz;
		queryString = queryString + "&codFiscaleAzienda="+codFiscaleAzienda+"&pIva="+pIva+"&ragioneSociale="+ragioneSociale+"&descrTipoAz="+descrTipoAz;	
    	
    } 
    
    String prgUnita = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
    if (configurazione.equals("1") && !prgUnita.equals("")) {
    	parameters = parameters + "&prgUnita="+prgUnita;
		queryString = queryString + "&prgUnita="+prgUnita;	
    }
    
    String valDa = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"datinizioda");
    String valA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"datinizioa");
    
    if (!valDa.equals("")) {
    	parameters = parameters + "&datinizioda="+valDa;
		queryString = queryString + "&datinizioda="+valDa;	
    }
    
    if (!valA.equals("")) {
    	parameters = parameters + "&datinizioa="+valA;
		queryString = queryString + "&datinizioa="+valA;	
    }
    
    if(!valDa.equals("") || !valA.equals("")){
    	if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
    	txtOut += "Data inizio";
 
    	if(!valDa.equals(""))
    	{
    		txtOut += " da <strong>"+ valDa +"</strong>";
    	}
    	if(!valA.equals(""))
    	{
    		txtOut += " a <strong>"+ valA +"</strong>";
    	}
    }

    valDa = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"datfineda");
    valA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"datfinea");
    
    if (!valDa.equals("")) {
    	parameters = parameters + "&datfineda="+valDa;
		queryString = queryString + "&datfineda="+valDa;	
    }
    
    if (!valA.equals("")) {
    	parameters = parameters + "&datfinea="+valA;
		queryString = queryString + "&datfinea="+valA;	
    }
    
    if(!valDa.equals("") || !valA.equals("")){
    	if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
    	txtOut += "Data fine";
 
    	if(!valDa.equals(""))
    	{
    		txtOut += " da <strong>"+ valDa +"</strong>";
    	}
    	if(!valA.equals(""))
    	{
    		txtOut += " a <strong>"+ valA +"</strong>";
    	}
    }
    
    String codstato = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codstato");
    if (!codstato.equals("")) {
    	parameters = parameters + "&codstato="+codstato;
		queryString = queryString + "&codstato="+codstato;	
    }
    String descrstato = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"descrstatohid");
    if(!descrstato.equals(""))
    {
    	if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
    	txtOut += "Stato iscrizione  <strong>"+ descrstato +"</strong>";
    	
    	parameters = parameters + "&descrstatohid="+descrstato;
  		queryString = queryString + "&descrstatohid="+descrstato;
    }    
    
    valDa = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"datcompetenzada");
    valA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"datcompetenzaa");
    
    if (!valDa.equals("")) {
    	parameters = parameters + "&datcompetenzada="+valDa;
		queryString = queryString + "&datcompetenzada="+valDa;	
    }
    
    if (!valA.equals("")) {
    	parameters = parameters + "&datcompetenzaa="+valA;
		queryString = queryString + "&datcompetenzaa="+valA;	
    }
    
    if(!valDa.equals("") || !valA.equals("")){
    	if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
    	txtOut += "Data competenza";
 
    	if(!valDa.equals(""))
    	{
    		txtOut += " da <strong>"+ valDa +"</strong>";
    	}
    	if(!valA.equals(""))
    	{
    		txtOut += " a <strong>"+ valA +"</strong>";
    	}
    }    
    
    String codMotChiusuraIscr = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codmotchiusuraiscr");
    if (!codMotChiusuraIscr.equals("")) {
    	parameters = parameters + "&codmotchiusuraiscr="+codMotChiusuraIscr;
		queryString = queryString + "&codmotchiusuraiscr="+codMotChiusuraIscr;	
    }
    String descrMotChiusuraIscr = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"descrmotchiusuraiscrhid");
    if(!descrMotChiusuraIscr.equals(""))
    {
    	if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
    	txtOut += "Motivo chiusura iscrizione  <strong>"+ descrMotChiusuraIscr +"</strong>";
    	
    	parameters = parameters + "&descrMotChiusuraIscr="+descrMotChiusuraIscr;
  		queryString = queryString + "&descrMotChiusuraIscr="+descrMotChiusuraIscr;
    }
    
    valDa = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"datchiusuraiscrda");
    valA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"datchiusuraiscra");
    
    if (!valDa.equals("")) {
    	parameters = parameters + "&datchiusuraiscrda="+valDa;
		queryString = queryString + "&datchiusuraiscrda="+valDa;	
    }
    
    if (!valA.equals("")) {
    	parameters = parameters + "&datchiusuraiscra="+valA;
		queryString = queryString + "&datchiusuraiscra="+valA;	
    }
    
    if(!valDa.equals("") || !valA.equals("")){
    	if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
    	txtOut += "Data chiusura iscrizione";
 
    	if(!valDa.equals(""))
    	{
    		txtOut += " da <strong>"+ valDa +"</strong>";
    	}
    	if(!valA.equals(""))
    	{
    		txtOut += " a <strong>"+ valA +"</strong>";
    	}
    }
    
	String codCPILav = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"CodCPILav");
	if (!codCPILav.equals("")) {
    	parameters = parameters + "&CodCPILav="+codCPILav;
		queryString = queryString + "&CodCPILav="+codCPILav;	
    }
	String compNonAmm = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"compNonAmm");
	if (!compNonAmm.equals("")) {
    	parameters = parameters + "&compNonAmm="+compNonAmm;
		queryString = queryString + "&compNonAmm="+compNonAmm;	
    }
    String descrCPILavhid = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"descrCPILavhid");
    if(!descrCPILavhid.equals(""))
    {	   
    	if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
    	txtOut += "CPI competente per il lavoratore <strong>"+ descrCPILavhid +"</strong>";
    	
    	parameters = parameters + "&descrCPILavhid="+descrCPILavhid;
  		queryString = queryString + "&descrCPILavhid="+descrCPILavhid;
    }
    
	if (serviceRequest.containsAttribute("compNonAmm")) {
		if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
		txtOut += "Competenza non amministrativa <strong>Sì</strong>";
	}
    
	String descrTipoIscr = "";
	String strCodice = "";
 	String strCodiceSel = "";
    SourceBean rowDescrizione = null;
    Vector vettTipoIscrSel = new Vector();
    
	if (!serviceRequest.containsAttribute("stampa")) { 
		Vector list = serviceRequest.getAttributeAsVector("descrTipoIscrhid");
		if(list.size()!=0) {
	  		for(int i=0; i<list.size(); i++) {
				if(descrTipoIscr.length()>0) 
					descrTipoIscr += ","; 
				if(!list.elementAt(i).equals("")) { 
					descrTipoIscr += list.elementAt(i); 
				}
	  		}
	  	}
	} else {
		
		Vector vettTipoIscr = serviceResponse.getAttributeAsVector("CI_TIPO_ISCR.ROWS.ROW");
  		if (tipoIscr != null && !tipoIscr.equals("")) {
  			vettTipoIscrSel = StringUtils.split(tipoIscr,",");
    	}
  		
  		for (int k=0;k<vettTipoIscrSel.size();k++) {
  		strCodiceSel = vettTipoIscrSel.get(k).toString();
  			for (int i=0;i<vettTipoIscr.size();i++)  {
  				rowDescrizione = (SourceBean)vettTipoIscr.get(i);
	 			strCodice = rowDescrizione.getAttribute("codice").toString();
	 			if (strCodiceSel.equals(strCodice)) {
	 				if (descrTipoIscr.equals("")) {
	 					descrTipoIscr = descrTipoIscr + rowDescrizione.getAttribute("DESCRIZIONE").toString();
					}
					else {
						descrTipoIscr = descrTipoIscr + ',' + rowDescrizione.getAttribute("DESCRIZIONE").toString();
					}
					break;		
	 			}
  		   }
  		}
    }
  	 
    if (!descrTipoIscr.equals("")) {
    	if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
    	txtOut += "Tipo Iscrizione (<strong>"+ descrTipoIscr +"</strong>) ";
    }
     
    String descrCPIAzhid = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"descrCPIAzhid");
    if(!descrCPIAzhid.equals(""))
    {	   
    	if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
    	txtOut += "CPI competente per l'azienda <strong>"+ descrCPIAzhid +"</strong>";
    	
    	parameters = parameters + "&descrCPIAzhid="+descrCPIAzhid;
  		queryString = queryString + "&descrCPIAzhid="+descrCPIAzhid;
    }
    
    String provUaz = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codProvAz");
    if (!"".equals(provUaz)) {
    	parameters = parameters + "&codProvAz="+provUaz;
		queryString = queryString + "&codProvAz="+provUaz;	
    }
    
    String descrProvAzhid = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"descrProvAzhid");
    if(!descrProvAzhid.equals(""))
    {	   
    	if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
    	txtOut += "Provincia per l'azienda <strong>"+ descrProvAzhid +"</strong>";
    }	    
    
    String codAccordo = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codAccordo");
    
    if (!"".equals(codAccordo)) {
		if (alreadyChosen) {
			txtOut += ",";
		} else {
			alreadyChosen = true;
		}
		parameters = parameters + "&codAccordo="+codAccordo;
		queryString = queryString + "&codAccordo="+codAccordo;		
		txtOut += "Codice accordo (finisce con) <strong>" + codAccordo + "</strong>";
	}
    
    
    
	boolean existCFLav = serviceRequest.containsAttribute("cbCfLav");
	boolean existCogNomLav = serviceRequest.containsAttribute("cbCogNomLav");
	boolean existDatIscr = serviceRequest.containsAttribute("cbDatIscr");
	boolean existTipoIscr = serviceRequest.containsAttribute("cbTipoIscr");
	boolean existComuneRes = serviceRequest.containsAttribute("cbComuneRes");
	boolean alreadyCheck = false;
	
	if ( existCFLav || existCogNomLav || existDatIscr || existTipoIscr || existComuneRes) {
		if (alreadyChosen) {
			txtOut += "; ";
		} else {
			alreadyChosen = true;
		}
		txtOut += "Ordinamento per (";
		
		if (existCFLav) {
			alreadyCheck = true;
			String radioCfLav = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"radioCfLav");
			parameters = parameters + "&cbCfLav=on";
			queryString = queryString + "&cbCfLav=on";
			parameters = parameters + "&radioCfLav="+radioCfLav;
			queryString = queryString + "&radioCfLav="+radioCfLav;
			String ord = "";
			if (radioCfLav.equals("DESC")) { 
				ord = " Decrescente";
			} else { 
				ord = " Crescente";
			}
			txtOut += "<strong>Codice Fiscale Lavoratore " + ord + "</strong>";
		}
		
		if (existCogNomLav) {
			if (alreadyCheck) {
				txtOut += ",";
			} else {
				alreadyCheck = true;
			}
			String radioCogNomLav = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"radioCogNomLav");
			parameters = parameters + "&cbCogNomLav=on";
			queryString = queryString + "&cbCogNomLav=on";
			parameters = parameters + "&radioCogNomLav="+radioCogNomLav;
			queryString = queryString + "&radioCogNomLav="+radioCogNomLav;
			String ord = "";
			if (radioCogNomLav.equals("DESC")) { 
				ord = " Decrescente";
			} else { 
				ord = " Crescente";
			}
			txtOut += "<strong>Cognome e Nome Lavoratore " + ord + "</strong>";
		}
		
		if (existDatIscr) {
			if (alreadyCheck) {
				txtOut += ",";
			} else {
				alreadyCheck = true;
			}
			String radioDatIscr = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"radioDatIscr");
			parameters = parameters + "&cbDatIscr=on";
			queryString = queryString + "&cbDatIscr=on";
			parameters = parameters + "&radioDatIscr="+radioDatIscr;
			queryString = queryString + "&radioDatIscr="+radioDatIscr;
			String ord = "";
			if (radioDatIscr.equals("DESC")) { 
				ord = " Decrescente";
			} else { 
				ord = " Crescente";
			}
			txtOut += "<strong>Data Iscrizione " + ord + "</strong>";
		}
		
		if (existTipoIscr) {
			if (alreadyCheck) {
				txtOut += ",";
			} else {
				alreadyCheck = true;
			}
			String radioTipoIscr = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"radioTipoIscr");
			parameters = parameters + "&cbTipoIscr=on";
			queryString = queryString + "&cbTipoIscr=on";
			parameters = parameters + "&radioTipoIscr="+radioTipoIscr;
			queryString = queryString + "&radioTipoIscr="+radioTipoIscr;
			String ord = "";
			if (radioTipoIscr.equals("DESC")) { 
				ord = " Decrescente";
			} else { 
				ord = " Crescente";
			}
			txtOut += "<strong>Tipo Iscrizione " + ord + "</strong>";
		}
		
		if (existComuneRes) {
			if (alreadyCheck) {
				txtOut += ",";
			} else {
				alreadyCheck = true;
			}
			String radioComuneRes = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"radioComuneRes");
			parameters = parameters + "&cbComuneRes=on";
			queryString = queryString + "&cbComuneRes=on";
			parameters = parameters + "&radioComuneRes="+radioComuneRes;
			queryString = queryString + "&radioComuneRes="+radioComuneRes;
			String ord = "";
			if (radioComuneRes.equals("DESC")) { 
				ord = " Decrescente";
			} else { 
				ord = " Crescente";
			}
			txtOut += "<strong>Comune Residenza " + ord + "</strong>";
		}		
		
		txtOut += ")";
	
	}
	
	
%>

<html>
<head>
<title>Lista delle Convenzioni</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<script language="javascript">	

	/*
	 * Torna alla pagina di ricerca
	 */
	function goBackRicerca() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=<%=_backpage%>" + "&cdnfunzione=<%=cdnfunzione%>";
		setWindowLocation(url);
	}
	
	function stampaLista() {
		apriGestioneDoc('RPT_ALTRE_ISCRIZIONI','<%=parameters%>+&stampa=stampa','CIGDOM');
	}


	
	
	
</script>

</head>

<body class="gestione" onload="rinfresca();rinfresca_laterale();">

<af:showErrors />

<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%}%>

<af:form name="Frm1" method="POST" action="AdapterHTTP">
	<%if (configurazione.equals("0")) {
	%>
	<af:list moduleName="M_ListaDomandeCig" getBack="true"/>
	<%}
	else {
	%>
	<af:list moduleName="M_ListaAltreIscrizioni" getBack="true"/>
	<%}%>
	<p align="center">
	<table>  
		<tr>
			<td>
				<input type="button" class="pulsante" name="ricerca" value="Torna alla ricerca" onclick="goBackRicerca()" />
			</td>
			<td>
				<input type="button" class="pulsante" name="btnStampa" value="Stampa lista" onclick="stampaLista()" />
			</td>
		</tr>
	</table>

</af:form>

</body>
</html>