<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
	// viene profilata la pagina di ricerca tramite MENU
	String _page = "RicercaRedditiAttivazionePage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);

	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
		//verifico se sono in ambito contestuale
		boolean isContestuale = serviceRequest.containsAttribute("CDNLAVORATORE");
		String cdnLavoratore = "";
		InfCorrentiLav infCorrentiLav = null;
		String CF = "";
		String COGNOME = "";
		String NOME = "";
		
		String datprestazioneda = "";
		String datprestazionea = "";
		String stato = "";
		String datcaricamentoda = "";
		String datcaricamentoa = "";
		String descrStato = "";	
		String tipoRicerca = "";
		String codProvenienza = "";
		String descrProvenienza = "";
		String nomeFile = "";
		String descrNomeFile = "";		
		String txtOut = "";
		String parameters = "";
		String queryString = null;
		
		 if (isContestuale) {
		 	cdnLavoratore=(String) StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
			infCorrentiLav = new InfCorrentiLav(sessionContainer, cdnLavoratore, user); 
			infCorrentiLav.setSkipLista(true);
		 } else {		
			CF = StringUtils.getAttributeStrNotNull(serviceRequest, "CF");
			COGNOME = StringUtils.getAttributeStrNotNull(serviceRequest, "COGNOME");
			NOME = StringUtils.getAttributeStrNotNull(serviceRequest, "NOME");
			
			datprestazioneda = StringUtils.getAttributeStrNotNull(serviceRequest, "datprestazioneda");
			datprestazionea = StringUtils.getAttributeStrNotNull(serviceRequest, "datprestazionea");
			stato = StringUtils.getAttributeStrNotNull(serviceRequest, "stato");
			datcaricamentoda = StringUtils.getAttributeStrNotNull(serviceRequest, "datcaricamentoda");
			datcaricamentoa = StringUtils.getAttributeStrNotNull(serviceRequest, "datcaricamentoa");	
			descrStato = StringUtils.getAttributeStrNotNull(serviceRequest, "statoSelected_H");
			tipoRicerca = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoRicerca");
			codProvenienza = StringUtils.getAttributeStrNotNull(serviceRequest, "codProvenienza");
			descrProvenienza = StringUtils.getAttributeStrNotNull(serviceRequest, "provenienzaSelected_H");
			nomeFile = StringUtils.getAttributeStrNotNull(serviceRequest, "nomeFile");
			descrNomeFile = StringUtils.getAttributeStrNotNull(serviceRequest, "fileSelected_H");	
		
	        if (!CF.equals("")) {
	        	parameters = parameters + "&CF="+CF;
	        	txtOut += "Codice Fiscale Lavoratore <strong>"+ CF +"</strong>; ";
	        }
	        if (!COGNOME.equals("")) {
	        	parameters = parameters + "&COGNOME="+COGNOME;
	        	txtOut += "Cognome Lavoratore <strong>"+ COGNOME +"</strong>; ";
	        }
	        if(!NOME.equals("")) {
	        	parameters = parameters + "&NOME="+NOME;
	        	txtOut += "Nome Lavoratore <strong>"+ NOME +"</strong>; ";
	        }
	        
	        parameters = parameters + "&tipoRicerca="+tipoRicerca;
	        
	        if (!CF.equals("") || !COGNOME.equals("") || !NOME.equals("")) {
	    		String tipoRic = tipoRicerca;
	    		if (tipoRic.equalsIgnoreCase("iniziaPer")) tipoRic = "inizia per";
	    		txtOut += "tipo di ricerca <strong>" + tipoRic + "</strong>; ";
	    	}
	        if (!codProvenienza.equals("")) {
	        	parameters = parameters + "&codProvenienza="+codProvenienza; 	
	        }
	        if(!descrProvenienza.equals("")) {
	        	txtOut += "Provenienza <strong>"+ descrProvenienza +"</strong>; ";
	        }
	        if(!datprestazioneda.equals("")) {
	        	parameters = parameters + "&datprestazioneda="+datprestazioneda;
	        	txtOut += "Data Prestazione da <strong>"+ datprestazioneda +"</strong>; ";
	        }
	        if(!datprestazionea.equals("")) {
	        	parameters = parameters + "&datprestazionea="+datprestazionea;
	        	txtOut += "Data Prestazione a <strong>"+ datprestazionea +"</strong>; "; 
	        }
	        if (!stato.equals("")) {
	        	parameters = parameters + "&stato="+stato; 	
	        }
	        if(!descrStato.equals("")) {
	        	txtOut += "Stato <strong>"+ descrStato +"</strong>; ";
	        }
	        if(!datcaricamentoda.equals("")) {
	        	parameters = parameters + "&datcaricamentoda="+datcaricamentoda;
	        	txtOut += "Data Caricamento da <strong>"+ datcaricamentoda +"</strong>; ";
	        }
	        if(!datcaricamentoa.equals("")) {
	        	parameters = parameters + "&datcaricamentoa="+datcaricamentoa;
	        	txtOut += "Data Caricamento a <strong>"+ datcaricamentoa +"</strong>; ";
	        }
	        if (!nomeFile.equals("")) {
	        	parameters = parameters + "&nomeFile="+nomeFile; 	
	        }
	        if(!descrNomeFile.equals("")) {
	        	txtOut += "Nome file <strong>"+ descrNomeFile +"</strong>; ";
	        }
	         
		 }
%>

<html>
<head>
<title>Lista Redditi di Attivazione</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<script type="text/Javascript">
	<%if (!isContestuale) {%>
	if (window.top.menu != undefined){
			window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
	}
	<%}%>

	function tornaAllaRicerca()
  	{  // Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
  
     	url="AdapterHTTP?PAGE=RicercaRedditiAttivazionePage";
     	url += "&CDNFUNZIONE="+"<%=_funzione%>";
     	url += "&CF="+"<%=CF%>";
     	url += "&COGNOME="+"<%=COGNOME%>";    	
     	url += "&NOME="+"<%=NOME%>";
     	url += "&datprestazioneda="+"<%=datprestazioneda%>";
     	url += "&datprestazionea="+"<%=datprestazionea%>";
     	url += "&stato="+"<%=stato%>";
     	url += "&datcaricamentoda="+"<%=datcaricamentoda%>";
     	url += "&datcaricamentoa="+"<%=datcaricamentoa%>";
        url += "&tipoRicerca="+"<%=tipoRicerca%>";
        url += "&codProvenienza="+"<%=codProvenienza%>";
        url += "&nomeFile="+"<%=nomeFile%>";
	
		setWindowLocation(url);
	}
	
  	function stampaListaRedditoAttivazione() {
  		apriGestioneDoc('RPT_LISTA_REDDITO_ATTIVAZIONE','<%=parameters%>','STRA');
	}
</script>

</head>
<body class="gestione" onload="rinfresca();">
	<%if (isContestuale) {%>
	<script language="Javascript">
		window.top.menu.caricaMenuLav(<%=_funzione%>,<%=cdnLavoratore%>);
	</script>
	<%infCorrentiLav.show(out);
	} %>
	<center>
		<font color="red"> 
			<af:showErrors />
		</font>
		
		<%if(txtOut.length() > 0)
          { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
            <table cellpadding="2" cellspacing="10" border="0" width="100%">
             <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
              <%out.print(txtOut);%>
             </td></tr>
            </table>
        <%}%>
        
	</center>
	<af:form dontValidate="true">
	
		<af:list moduleName="M_DynamicListaRA"
			configProviderClass="it.eng.sil.module.anag.DynamicListaRAConfig"
			getBack="true" />
		
		<%if (!isContestuale) {%>
		<center>
			<input class="pulsante" type="button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()" />
			<input class="pulsante" type="button" name="stampa" value="Stampa" onclick="stampaListaRedditoAttivazione()" />
		</center>
		<%}%>
	</af:form>
	<br />
	
</body>
</html>
<%
	}
%>