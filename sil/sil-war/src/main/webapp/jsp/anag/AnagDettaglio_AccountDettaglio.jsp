<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                it.eng.sil.security.ProfileDataFilter, 
                java.text.*, java.util.*,it.eng.sil.util.*, java.math.*,
                it.eng.sil.security.* "%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String cdnLavoratore = (String )serviceRequest.getAttribute("CDNLAVORATORE");
    String idPfPrincipal = (String )serviceRequest.getAttribute("idPfPrincipal");
    String inserisciNuovo= (String )serviceRequest.getAttribute("inserisciNuovo");
   

	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	String titoloRisultato = "";
	String messaggioRisultato = "";
	String messaggioRisultatoStatus = "";
	


	boolean isRisultato = false;
	
	SourceBean row_result= null;
	SourceBean row_resultStatusSil = null;
	String response_xml_insert="M_InserisciNuovoAccountPortale";
    SourceBean row_insert=(SourceBean) serviceResponse.getAttribute(response_xml_insert);
    if (row_insert!=null) {row_result = row_insert;}
    else {
    	String response_xml_aggiornaportale="M_AggiornaDatiPortale";
        SourceBean row_aggiornaPortale=(SourceBean) serviceResponse.getAttribute(response_xml_aggiornaportale);
        if (row_aggiornaPortale!=null) {
        	row_result = row_aggiornaPortale;
        }
        else {
        	String response_xml_aggiornasil="M_AggiornaAnagLavAccountSil";
       		SourceBean row_aggiornaSil=(SourceBean) serviceResponse.getAttribute(response_xml_aggiornasil);
        	if (row_aggiornaSil!=null) {
        		row_result = row_aggiornaSil; 
        	}
        	String response_xml_aggiornastatusSil="M_AggiornaExPermSoggAccountSil";
       		SourceBean row_aggiornastatusSil=(SourceBean) serviceResponse.getAttribute(response_xml_aggiornastatusSil);
        	if (row_aggiornastatusSil!=null) {
        		row_resultStatusSil = row_aggiornastatusSil; 
        	}
       	}
    }
    if (row_result!=null &&	row_result.containsAttribute("RISULTATO")&&row_result.containsAttribute("TITOLO")) {
    	 isRisultato = true;
    	 titoloRisultato = row_result.getAttribute("TITOLO").toString();
    	 messaggioRisultato = row_result.getAttribute("RISULTATO").toString(); 
    }
    if (row_resultStatusSil!=null && row_resultStatusSil.containsAttribute("RISULTATO")) {
    	messaggioRisultatoStatus = row_resultStatusSil.getAttribute("RISULTATO").toString();
    }
    
    
	boolean isInsert = (inserisciNuovo!=null&&inserisciNuovo.equals("1"));
	
	boolean canModify = true;
	
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
		PageAttribs attributi = new PageAttribs(user, _page);
		boolean existInserisciNuovo = attributi.containsButton("INSERISCI");
		boolean existAggiornaSil = attributi.containsButton("AGGIORNASIL");
		boolean existAggiornaPortale = attributi.containsButton("AGGIORNAPORTALE");
		boolean existReinoltraMail = attributi.containsButton("REINOLTRAMAIL");


	
	    InfCorrentiLav infCorrentiLav= new InfCorrentiLav(requestContainer.getSessionContainer(), cdnLavoratore, user);
	
	  
	    String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);	
	    

	    //inizializzo i campi
//SIL
	    String strCognome="";
	    String strNome="";
	    String strMail="";
	    String codComNas="";
	    String strComNas="";
	    String strCodiceFiscale="";
	    String datNasc="";
	    String strCittadinanza="";
	    String codCittadinanza="";
	    String strDocumento="";
	    String strNumDoc="";
	    String datScad="";
	    String codComdom="";
	    String strComdom="";
	    String strIndirizzodom="";
	    String codiceProvinciaSil="";
	    String strAbilServSil ="";
	    String strStatoAccountSil ="";
	    BigDecimal numKloLavoratore = null;
	    String numKloLavStr = "";
	    
//PORTALE	    
	    String strUsernamePortale ="";
	    String strCognomePortale ="";
	    String strNomePortale ="";
	    String strMailPortale ="";
	    String strComuneNascitaPortale ="";
	    String codComuneNascitaPortale ="";
	    String strComuneDomicilioPortale ="";
	    String codComuneDomicilioPortale ="";
	    String strIndirizzoDomicilioPortale ="";
	    String strCodiceFiscalePortale ="";
	    String datNascitaPortale ="";
	    String strCittadinanzaPortale ="";
	    String codCittadinanzaPortale ="";
	    String strDocIdPortale ="";
	    String strNumDocPortale ="";
	    String datScadPortale ="";
	    String strAbilServPortale ="";
	    String strStatoAccountPortale ="";
	    String strAbilServPortaleDesc ="";
	    String strStatoAccountPortaleDesc ="";
	    String codStatoPortale ="";
		try {	   
		    
		    String response_xml_lav="M_GETLAVORATOREANAG.ROWS.ROW";
		    String response_xml_ind="M_GETLAVORATOREINDIRIZZI.ROW";
		    String response_xml_port="M_GETLAVORATOREACCOUNTDETTAGLIO.ROW";
		    String response_xml_sil="M_GETLAVORATOREACCOUNTDETTAGLIOSIL.ROW";



		    SourceBean row_lav=(SourceBean) serviceResponse.getAttribute(response_xml_lav);
		    SourceBean row_ind=(SourceBean) serviceResponse.getAttribute(response_xml_ind);
		    SourceBean row_port=(SourceBean) serviceResponse.getAttribute(response_xml_port);
		    SourceBean row_sil=(SourceBean) serviceResponse.getAttribute(response_xml_sil);


			//SIL
			if (row_lav != null) {
				 if (row_lav.containsAttribute("NUMKLOLAVORATORE")) {
		        	numKloLavoratore = (BigDecimal)row_lav.getAttribute("NUMKLOLAVORATORE");
		        	if (numKloLavoratore != null) {
		        		numKloLavoratore = numKloLavoratore.add(new BigDecimal(1));
		        		numKloLavStr = numKloLavoratore.toString();
					}
		        }	
			}
			
			if (row_lav != null && row_sil!=null) {
		        if (row_lav.containsAttribute("strCognome")) {strCognome=row_lav.getAttribute("strCognome").toString();}
		        if (row_lav.containsAttribute("strNome")) {strNome=row_lav.getAttribute("strNome").toString();}
		        
		        if (row_ind.containsAttribute("codComdom")) {codComdom=row_ind.getAttribute("codComdom").toString();}
		        if (row_ind.containsAttribute("strComdom")) {strComdom=row_ind.getAttribute("strComdom").toString();}
		        if (row_ind.containsAttribute("strIndirizzodom")) {strIndirizzodom=row_ind.getAttribute("strIndirizzodom").toString();}
	
		        
		        if (row_lav.containsAttribute("codComNas")) {codComNas=row_lav.getAttribute("codComNas").toString();}
		        if (row_lav.containsAttribute("strComNas")) {strComNas=row_lav.getAttribute("strComNas").toString();}
		        
		        if (row_lav.containsAttribute("strCodiceFiscale")) {strCodiceFiscale=row_lav.getAttribute("strCodiceFiscale").toString();}
		        if (row_lav.containsAttribute("datNasc")) {datNasc=row_lav.getAttribute("datNasc").toString(); }
		        if (row_lav.containsAttribute("strCittadinanza")) {strCittadinanza=row_lav.getAttribute("strCittadinanza").toString();}
		        if (row_lav.containsAttribute("codCittadinanza")) {codCittadinanza=row_lav.getAttribute("codCittadinanza").toString();}
		        
		       
	
		        if (row_sil.containsAttribute("codiceProvinciaSil")) {codiceProvinciaSil=row_sil.getAttribute("codiceProvinciaSil").toString();}
		        if (row_sil.containsAttribute("strDocumento")) {strDocumento=row_sil.getAttribute("strDocumento").toString();}
		        if (row_sil.containsAttribute("strNumDoc")) {strNumDoc=row_sil.getAttribute("strNumDoc").toString(); }
		        if (row_sil.containsAttribute("datScad")) {datScad=row_sil.getAttribute("datScad").toString();}
		        if (row_sil.containsAttribute("strmail")) {strMail=row_sil.getAttribute("strmail").toString();}
				if (row_sil.containsAttribute("AbilitatoServiziAmministrativi")) {strAbilServSil=row_sil.getAttribute("AbilitatoServiziAmministrativi").toString();}
				if (row_sil.containsAttribute("abilitato")) {strStatoAccountSil=row_sil.getAttribute("abilitato").toString();}
	    
			}
	    	//PORTALE     
		    if (row_port != null) {
		        if (row_port.containsAttribute("Username")) {strUsernamePortale=row_port.getAttribute("Username").toString();}
				if (row_port.containsAttribute("Cognome")) {strCognomePortale=row_port.getAttribute("Cognome").toString();}
				if (row_port.containsAttribute("Nome")) {strNomePortale=row_port.getAttribute("Nome").toString();}
				if (row_port.containsAttribute("EMail")) {strMailPortale=row_port.getAttribute("EMail").toString();}
				if (row_port.containsAttribute("comuneNascita")) {codComuneNascitaPortale=row_port.getAttribute("comuneNascita").toString();}
				if (row_port.containsAttribute("descComunmeNascita")) {strComuneNascitaPortale=row_port.getAttribute("descComunmeNascita").toString();}
				if (row_port.containsAttribute("comuneDomicilio")) {codComuneDomicilioPortale=row_port.getAttribute("comuneDomicilio").toString();}
				if (row_port.containsAttribute("descComunmeDomicilio")) {strComuneDomicilioPortale=row_port.getAttribute("descComunmeDomicilio").toString();}
				if (row_port.containsAttribute("IndirizzoDomicilio")) {strIndirizzoDomicilioPortale=row_port.getAttribute("IndirizzoDomicilio").toString();}
				if (row_port.containsAttribute("CodiceFiscale")) {strCodiceFiscalePortale=row_port.getAttribute("CodiceFiscale").toString();}
				if (row_port.containsAttribute("dataNascita")) {datNascitaPortale=row_port.getAttribute("dataNascita").toString();}
				if (row_port.containsAttribute("Cittadinanza")) {codCittadinanzaPortale=row_port.getAttribute("Cittadinanza").toString();}
				if (row_port.containsAttribute("descCittadinanza")) {strCittadinanzaPortale=row_port.getAttribute("descCittadinanza").toString();}
				if (row_port.containsAttribute("documentoIdentita")) {strDocIdPortale=row_port.getAttribute("documentoIdentita").toString();}
				if (row_port.containsAttribute("numeroDocumento")) {strNumDocPortale=row_port.getAttribute("numeroDocumento").toString();}
				if (row_port.containsAttribute("dtScadenzaDocumento")) {datScadPortale=row_port.getAttribute("dtScadenzaDocumento").toString();}
				if (row_port.containsAttribute("AbilitatoServiziAmministrativi")) {strAbilServPortale=row_port.getAttribute("AbilitatoServiziAmministrativi").toString();}
				if (row_port.containsAttribute("abilitato")) {strStatoAccountPortale=row_port.getAttribute("abilitato").toString();}
				if (row_port.containsAttribute("AbilitatoServiziAmministrativiDesc")) {strAbilServPortaleDesc=row_port.getAttribute("AbilitatoServiziAmministrativiDesc").toString();}
				if (row_port.containsAttribute("abilitato")) {strStatoAccountPortaleDesc=row_port.getAttribute("abilitatoDesc").toString();}
				if (row_port.containsAttribute("codStatus")) {codStatoPortale=row_port.getAttribute("codStatus").toString();}


		    }	
	    }
	    catch(Exception ex){
	    } 
		
		boolean canReInvioEmail = (existReinoltraMail && strStatoAccountPortale.equalsIgnoreCase("N"));
		int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	
		String required = "false";
		if (isInsert) { 
			 required = "true";
		}
		
		// errore durante la lettura dei dati da ws?
		boolean isErrore = false;
		String response_xml="M_GetLavoratoreAccountDettaglio";
		SourceBean row_account=(SourceBean) serviceResponse.getAttribute(response_xml);
		if (row_account != null) {
			isErrore = row_account.containsAttribute("ERRORE");
		}
		
%>
<html>
<head>
<title><%=titoloRisultato%></title>
<SCRIPT>
	var urlpage="AdapterHTTP?";
	
	function getURLPageBase() {    
	    urlpage+="CDNFUNZIONE=<%=_funzione%>&";

	    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
	    return urlpage;
	}
	function goListaAccount() {

	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
	    urlpage = getURLPageBase();
	    urlpage+="PAGE=AnagDettaglioPageAccount&";
	    setWindowLocation(urlpage);
    }

    function controlloIndirizzoDomicilioSil() {
        var indirizzoDomicilio = document.forms[0].INDIRIZZODOM_SIL.value;
        if (!indirizzoDomicilio || indirizzoDomicilio === "") {
        	alert("Il campo indirizzo domicilio SIL (compilabile nella sezione 'Indirizzi') è obbligatorio.");
        	return false;
        }
        return true;
    }

	function controlloAccount() {
	<%if (isInsert) {%>
		 obj = eval("document.forms[0].USERNAME_PORT");
		 val=obj.value;
		 regexp = /^[a-zA-Z0-9]+$/;

		if (!regexp.test(val)) {
			alert("Per la username sono consentiti solo numeri e lettere.");
			return false;
		}
		
		if (val.length <3) {
			alert("La lunghezza dello username deve essere compresa fra 3 e 16 caratteri");
			return false;
	    }

		obj = eval("document.forms[0].MAIL_SIL");
		val=obj.value;
		regexp = new RegExp('^[-!#$%&\'*+\\./0-9=?A-Z^_`a-z{|}~]+@[-!#$%&\'*+\\/0-9=?A-Z^_`a-z{|}~]+\.[-!#$%&\'*+\\./0-9=?A-Z^_`a-z{|}~]+$');
		if (!regexp.test(val)) {
			alert("Formato e-mail errato.");
			return false;		 
		}		
	
	
    <%}%>
		return confirm('Confermi operazione?');
	}

   window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
</SCRIPT>
<%		      
	   if (isRisultato) {	
%>
 <af:linkScript path="../../js/"/>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetailCoop.css" type="text/css">




</head>
<body class="gestione">
<%
 Linguette l  = new Linguette(user,  _funzione, "AnagDettaglioPageAccount", new BigDecimal(cdnLavoratore));
 infCorrentiLav.show(out); 
 l.show(out);
 %>
<p class="titolo"><%=titoloRisultato%></p>

<%out.print(htmlStreamTop);%>
<p><%=messaggioRisultato%></p>
<p><%=messaggioRisultatoStatus%></p>
<input type="button" class="pulsanti"  name = "chiudi" value="Chiudi"  onclick="goListaAccount()">
<%out.print(htmlStreamBottom);%>
</body>
</html>
<% } else { %>


<head>
<title>Anagrafica dettaglio</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
 <af:linkScript path="../../js/"/>
<style>
td.etichetta {
    color: #000066;
    font-family: Verdana, Arial, Helvetica, Sans-serif;
    font-size: 11px;
    text-align: right;
    font-weight: normal;
    text-decoration: none;
    padding-right: 12px;
    cell-spacing: 0px;
    margin: 0px;
    border: 0px;
}
td.coop {
    background-color: #ffdead;
    color: #000066;
    font-family: Verdana, Arial, Helvetica, Sans-serif;
    font-size: 11px;
    font-weight: normal;
    margin: 0px;
    border: 0px;
    text-align: left;
    cell-spacing: 0px;
   
}

td.coopTitolo {
    background-color: #ffdead;
    color: #000066;
    font-family: Verdana, Arial, Helvetica, Sans-serif;
    font-size: 11px;
    font-weight: normal;
    margin: 0px;
    border: 0px;
    border-color: : #ffdead;
    text-align: center;
   
}

td.campo30 {
    color: #000066;
    font-family: Verdana, Arial, Helvetica, Sans-serif;
    font-size: 11px;
    text-align: left;
    text-decoration: none;
    cell-spacing: 0px;
}
</style>

</head>
<body class="gestione">
<%
  Linguette l  = new Linguette(user,  _funzione, "AnagDettaglioPageAccount", new BigDecimal(cdnLavoratore));
 infCorrentiLav.show(out); 
 l.show(out);
 %>

 <font color="red">
     <af:showErrors/>
     <af:showMessages prefix="M_GetLavoratoreAccountDettaglio"/>
 </font>
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controlloAccount()" >
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="idPfPrincipal" value="<%=idPfPrincipal%>"/>
<input type="hidden" name="CODICEPROVINCIASIL" value="<%=codiceProvinciaSil%>"/>



<%if (isInsert) {%>
<p class="titolo">Inserimento Nuovo Account Portale</p>
<%} else { %>
 <p class="titolo">Dettaglio Account Portale</p>
<%} %>  
 

<%out.print(htmlStreamTop);%>


	  <table class="main">
	    <%if (!isInsert) {%>
        <tr style=" height : 10px;">
            <td width="33%"></td>
            <td width="33%"><b>DATI SIL</b></td>
            <td width="33%" class="coop2" ><b>DATI PORTALE</b></td>
        </tr>
		<%} else {%>
		    <tr >
            <td class="etichetta">Username</td>
            <td nowrap align="left">
            <af:textBox required="<%=required%>"   title="Username"  name="USERNAME_PORT" size="20" maxlength="16"/>
        </tr>  
         <%} %> 
        <td class="etichetta">Cognome</td>
            <td class="campo30" nowrap>
                <af:textBox name="COGNOME_SIL"   required="<%=required%>" title="Cognome"  value="<%=strCognome%>" readonly="TRUE"  classNameBase="input" size="30" maxlength="30" />
            </td>
            <%if (!isInsert) {%>
            <td  class="coop">
                <img width="16" height="9" src="../../img//b.gif">
                <af:textBox name="COGNOME_PORT"  value="<%=strCognomePortale%>" readonly="TRUE"  classNameBase="input" size="30" maxlength="30" />
            </td>
            <%} %> 
        </tr>
        <tr>
            <td class="etichetta">Nome</td>

            <td class="campo30"nowrap>
                <af:textBox name="NOME_SIL" required="<%=required%>" title="Nome"  value="<%=strNome%>" readonly="TRUE"  classNameBase="input" size="30" maxlength="30" />
            </td>
            <%if (!isInsert) {%>           
            <td  class="coop">
                <img width="16" height="9" src="../../img//b.gif">
                <af:textBox name="NOME_PORT" value="<%=strNomePortale%>"  readonly="TRUE"  classNameBase="input"  size="30" maxlength="30" />
            </td>
            <%}%>            
        </tr>
        <tr>
            <td class="etichetta">e-mail</td>
            <td class="campo30" nowrap>
                <af:textBox name="MAIL_SIL" required="<%=required%>" title="e-Mail (compilabile nella sezione 'Indirizzi')"  value="<%=strMail%>" readonly="TRUE"  classNameBase="input"  size="30" maxlength="30" />
            </td>
            <%if (!isInsert) {%>           
 			<td  nowrap class="coop">
                <img  src="../../img/freccia_a_sx.gif">
                <af:textBox name="MAIL_PORT"  value="<%=strMailPortale%>"  readonly="TRUE"  classNameBase="input"  size="30" maxlength="30" />
            </td>            
            <%}%>  
        </tr>
        <tr>
            <td class="etichetta">Comune di Nascita</td>
            <td class="campo30" nowrap>
                <af:textBox name="COMUNENAS_SIL"  required="<%=required%>" title="Comune di Nascita" value="<%=strComNas%>" readonly="TRUE"  classNameBase="input" size="30" maxlength="50"/>
                <input type="hidden" name="COD_COMUNENAS_SIL" value="<%=codComNas%>" />
                
                <%if (!isInsert) {%><img  src="../../img/freccia_a_dx.gif"><%}%>
            </td>
            <%if (!isInsert) {%>            
            <td  nowrap class="coop">
                <img width="16" height="9" src="../../img//b.gif">
                <af:textBox name="COMUNENAS_PORT"  value="<%=strComuneNascitaPortale%>" readonly="TRUE"  classNameBase="input" size="30" maxlength="50"/>
                <input type="hidden" name="COD_COMUNENAS_PORT" value="<%=codComuneNascitaPortale%>"/>
            </td>
            <%}%> 
        </tr>
        <tr>
            <td class="etichetta">Comune di Domicilio</td>
            <td class="campo30" nowrap >
                <af:textBox name="COMUNDOM_SIL"  required="<%=required%>" title="Comune di Domicilio"  value="<%=strComdom%>" readonly="TRUE"  classNameBase="input" size="30" maxlength="50"/>
                <input type="hidden" name="COD_COMUNDOM_SIL" value="<%=codComdom%>"/>
                
                <%if (!isInsert) {%><img  src="../../img/freccia_a_dx.gif"><%}%>
            </td>
            <%if (!isInsert) {%>            
            <td  nowrap class="coop">
                <img width="16" height="9" src="../../img//b.gif">
                <af:textBox name="COMUNDOM_PORT"  value="<%=strComuneDomicilioPortale%>" readonly="TRUE"  classNameBase="input" size="30" maxlength="50"/>
                <input type="hidden" name="COD_COMUNDOM_PORT" value="<%=codComuneDomicilioPortale%>"/>
             </td>
            <%}%>
        </tr>
        <tr>
            <td class="etichetta">Indirizzo Domicilio</td>
            <td class="campo30" nowrap>
                <af:textBox name="INDIRIZZODOM_SIL" title = "Indirizzo Domicilio  (compilabile nella sezione 'Indirizzi')" required="<%=required%>" value="<%=strIndirizzodom%>" readonly="TRUE"  classNameBase="input" size="50" maxlength="50"/>
                <%if (!isInsert) {%><img  src="../../img/freccia_a_dx.gif"><%}%>
            </td>
            <%if (!isInsert) {%>   
            <td  nowrap class="coop">
                <img width="16" height="9" src="../../img//b.gif">
                <af:textBox name="INDIRIZZODOM_PORT" value="<%=strIndirizzoDomicilioPortale%>"  readonly="TRUE"  classNameBase="input" size="50" maxlength="50"/>
            </td>
            <%}%>
        </tr>
        </tr>
        <tr>
            <td class="etichetta">Codice Fiscale</td>
            <td class="campo30" nowrap>
                <af:textBox name="CODICEFISCALE_SIL" required="<%=required%>" title="Codice Fiscale"  value="<%=strCodiceFiscale%>"   readonly="TRUE"  classNameBase="input" size="21" maxlength="16"/>
                <%if (!isInsert) {%><img  src="../../img/freccia_a_dx.gif"><%}%>
            </td>
            <%if (!isInsert) {%>   
            <td nowrap class="coop">
                <img width="16" height="9" src="../../img//b.gif">
                <af:textBox name="CODICEFISCALE_PORT"  value="<%=strCodiceFiscalePortale%>" readonly="TRUE"  classNameBase="input" size="21" maxlength="16"/>
            </td>
            <%}%>
        </tr>
        <tr>
            <td class="etichetta">Data Nascita</td>
            <td class="campo30" nowrap>
                <af:textBox name="DATANASCITA_SIL"  title="Data Nascita"  required="<%=required%>" value="<%=datNasc%>"  readonly="TRUE"  classNameBase="input"  size="11" maxlength="12"/>
                <%if (!isInsert) {%><img  src="../../img/freccia_a_dx.gif"><%}%>
            </td>
            <%if (!isInsert) {%>   
            <td  nowrap class="coop">
                <img width="16" height="9" src="../../img//b.gif">
                <af:textBox name="DATANAS_PORT" value="<%=datNascitaPortale%>" readonly="TRUE"  classNameBase="input"  size="11" maxlength="12"/>
            </td>
            <%}%>
        </tr>
        <tr>
            <td class="etichetta">Cittadinanza</td>
            <td class="campo30" nowrap>
                <af:textBox name="CITTADINANZA_SIL"  required="<%=required%>" title="Cittadinanza" value="<%=strCittadinanza%>"  readonly="TRUE"  classNameBase="input"  size="30" maxlength="40"/>
  				<input type="hidden" name="CODCITTADINANZA_SIL" value="<%=codCittadinanza%>"/>
                <%if (!isInsert) {%><img  src="../../img/freccia_a_dx.gif"><%}%>
            </td>
            <%if (!isInsert) {%>   
            <td  nowrap class="coop">
                <img  src="../../img/freccia_a_sx.gif">
                <af:textBox name="DESCITTADINANZA_PORT" value="<%=strCittadinanzaPortale%>" readonly="TRUE"  classNameBase="input"  size="30" maxlength="40"/>
                <input type="hidden" name="CITTADINANZA_PORT" value="<%=codCittadinanzaPortale%>"/>
             </td>
            <%}%>
        </tr>
        <tr>
            <td class="etichetta">Documento Identità</td>
            <td class="campo30" nowrap>
                <af:textBox name="DOCUMENTO_SIL"  required="<%=required%>" title="Documento Identità" readonly="TRUE"  value="<%=strDocumento%>" classNameBase="input" />
                <%if (!isInsert) {%><img  src="../../img/freccia_a_dx.gif"><%}%>
            </td>
            <%if (!isInsert) {%>   
            <td  nowrap class="coop">
                <img  src="../../img/freccia_a_sx.gif">
                <af:textBox name="DOCUMENTO_PORT" value="<%=strDocIdPortale%>" readonly="TRUE"  classNameBase="input"/>
            </td>
             <%}%>
        </tr>
        <tr>
            <td class="etichetta">Numero Documento</td>
            <td class="campo30" nowrap>
                <af:textBox name="NUMDOC_SIL" required="<%=required%>" title="Numero Documento"  value="<%=strNumDoc%>" readonly="TRUE"  classNameBase="input" size="3" maxlength="1"/>
                <%if (!isInsert) {%><img  src="../../img/freccia_a_dx.gif"><%}%>
            </td>
            <%if (!isInsert) {%>   
            <td  nowrap class="coop">
                <img  src="../../img/freccia_a_sx.gif">
                <af:textBox name="NUMDOC_PORT"  value="<%=strNumDocPortale%>" readonly="TRUE"  classNameBase="input"/>
            </td>
            <%}%>
        </tr>
        <tr>
            <td class="etichetta">Data Scad. Documento</td>
            <td class="campo30" nowrap>
               <af:textBox name="DATASCADDOC_SIL" required="<%=required%>" title="Data Scad. Documento" value="<%=datScad%>" readonly="TRUE"  classNameBase="input" size="11" maxlength="12"/>
               <%if (!isInsert) {%><img  src="../../img/freccia_a_dx.gif"><%}%>
            </td>
            <%if (!isInsert) {%>   
            <td  nowrap class="coop">
                <img  src="../../img/freccia_a_sx.gif">
                <af:textBox name="DATASCADDOC_PORT" value="<%=datScadPortale%>" readonly="TRUE"  classNameBase="input" size="11" maxlength="12"/>
            </td>
             <%}%>
        </tr>
        
        <%if (!isInsert) {%>   
         <tr>
            <td class="etichetta">Username</td>
            <td class="campo30" nowrap>
            </td>
            <td  nowrap class="coop">
                 <img width="16" height="9" src="../../img//b.gif">
                 <af:textBox name="USERNAME_PORT" value="<%=strUsernamePortale%>" readonly="TRUE"  classNameBase="input"/>
            </td>
        </tr>  
        <%}%>        
        
        
        <tr>
            <td class="etichetta">Abil. Servizi Amm.</td>
             <%if (isInsert) {%>
            <td class="campo30" nowrap>
                <af:textBox name="ABILSERVIZIAMM_SIL" readonly="TRUE"  value="<%=strAbilServSil%>" classNameBase="input"/>
            </td>
             <% } else {%>
             <td class="campo30" nowrap>
                <af:textBox name="ABILSERVIZIAMM_SIL" readonly="TRUE"   classNameBase="input"/>
            </td>   
            <td nowrap class="coop">
                <img width="16" height="9" src="../../img//b.gif">
                <af:textBox name="ABILSERVIZIAMM_PORT" value="<%=strAbilServPortaleDesc%>" readonly="TRUE"  classNameBase="input"/>
            </td>
            <%}%>
        </tr>
        <tr>             
            <td class="etichetta">Stato Account</td>
             <%if (isInsert) {%>
            <td class="campo30" nowrap>
                <af:textBox name="STATOACCOUNT_SIL" readonly="TRUE"  value="<%=strStatoAccountSil%>"  classNameBase="input"/>
            </td>
             <% } else {%>
            <td class="campo30" nowrap>
                <af:textBox name="STATOACCOUNT_SIL" readonly="TRUE"  classNameBase="input"/>
            </td>  
            <td  nowrap class="coop">
                <img width="16" height="9" src="../../img//b.gif">
                <input type="hidden" name="CODSTATO_PORT" value="<%=codStatoPortale%>"/>
                <af:textBox name="STATOACCOUNT_PORT" value="<%=strStatoAccountPortaleDesc%>"  readonly="TRUE"  classNameBase="input"/>
                 <%if (canReInvioEmail && !isErrore) { %>
                 	<input class="pulsante" type="button" name="reinoltramail" value="Reinoltra email accreditamento" onclick="goToReinvioEmail();">
                  <%}%>
            </td>
             <%}%>
        </tr>
         <%if (!isInsert) {%>   
         <tr>
            <td class="etichetta"></td>
            <td nowrap>
                Il SIL sovrascrive il dato del Portale  <img  src="../../img/freccia_a_dx.gif">
            </td>
            <td nowrap class="coop2" >
                 <img  src="../../img/freccia_a_sx.gif"> Il Portale sovrascrive i dati del SIL  
            </td>
        </tr>
        <tr>
            <td class="etichetta"></td>
            <td>
              <% if (existAggiornaPortale && !isErrore) { %>
                  <input class="pulsante"   type="submit" name="salvaportale" value="Aggiorna Dati Portale >>" onclick="return controlloIndirizzoDomicilioSil();">
               <%}%>
            </td>
            <td class="coop2">
               <% if (existAggiornaPortale && !isErrore) { %>
                  <input class="pulsante"   type="submit" name="salvasil" value="<< Aggiorna Dati SIL " >
               <%}%>
            </td>
        </tr>
        <%}%>
</table>


<%out.print(htmlStreamBottom);%>


<center>
<% 
   if (isInsert) {  
     if (existInserisciNuovo) { %>
       <input class="pulsante" type="submit" name="INSERISCI" value="Inserisci Nuovo Account" >
 <% }
   } %>
<input type="button" class="pulsanti"  name = "chiudi" value="Chiudi"  onclick="goListaAccount()">

</center>

<br/>
<p align="center">
</p>
<br/>
<input type="hidden" name="PAGE" value="AnagDettaglioPageAccountDettaglio">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
<%if (!isInsert) {%>
	<input type="hidden" name="NUMKLOLAVORATORE" value="<%=numKloLavStr%>"/>
	<%if (!codStatoPortale.equals("")) {%>
		<input type="hidden" name="STATUS_STRANIERO" value="extraCE"/>
	<%}
}%>
</af:form>

	<% if (canReInvioEmail) { %>
		<script>
		 
		function goToReinvioEmail() {
			  
			var _url = "AdapterHTTP?" + 
				"PAGE=AnagDettaglioPageAccountDettaglioReinvioEmail" +
				"&cdnFunzione=<%=_funzione%>" +
				"&CDNLAVORATORE=<%=cdnLavoratore%>" +
				"&idPfPrincipal=<%=idPfPrincipal%>" +
				"&emailPortale=<%=strMailPortale%>" +
				"&emailSil=<%=strMail%>";
		  	setWindowLocation(_url);
		  
		 }
		 
		 </script>
	<% } %>

</body>
</html>
<% } %>
<%}  %>
