<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
 
  int _funzione=0;
  InfCorrentiLav infCorrentiLav= null;
  	
  Testata operatoreInfo = null;
  String prgDiagnosiFunzionale = null;
  String cdnLavoratore = null;
  String flgInterdetto = null;
  String strCognomeNomeTutore = null;
  String strIndirizzoTutore = null;
  String cap_tut = null;
  String com_tut = null;
  String strAutonomia = null;
  String CODCOMTUTORE = null;
  String strNoteProgettoSocioAss = null;
  String strCondizLavIncompat = null;
  String numklodiagnosifunzionale = null;
  BigDecimal cdnUtIns = null;
  BigDecimal cdnUtMod = null;
  String dtmIns = null;
  String dtmMod = null;
  String finestra = null;
  String aggiornaSocio = null;
  String descrCom = null;
  

  String cercaComune = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cercaComune");
  
  prgDiagnosiFunzionale = "" + RequestContainer.getRequestContainer().getSessionContainer().getAttribute("prgDiagnosiFunzionale");

  _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

  cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
//  cdnLavoratore = EncryptDecryptUtils.encrypt(cdnLavoratoreDecrypt);
  
  finestra = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"finestra");
  aggiornaSocio = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"aggiornaSocio");

  SourceBean row = null;
  Vector rows= serviceResponse.getAttributeAsVector("M_Load_SocioLav.ROWS.ROW");
  // siamo in dettaglio per cui avro' al massimo una riga
  if (rows.size()==1) {
	        row = (SourceBean)rows.get(0);

		    prgDiagnosiFunzionale = Utils.notNull(row.getAttribute("prgDiagnosiFunzionale"));
		    //cdnLavoratore = Utils.notNull(row.getAttribute("cdnLavoratore"));
		
		    flgInterdetto = Utils.notNull(row.getAttribute("flgInterdetto")); 
		    strCognomeNomeTutore = Utils.notNull(row.getAttribute("strCognomeNomeTutore"));
		    
		    strIndirizzoTutore = Utils.notNull(row.getAttribute("strIndirizzoTutore")); 
		    cap_tut = Utils.notNull(row.getAttribute("cap_tut"));  
		    CODCOMTUTORE = Utils.notNull(row.getAttribute("CODCOMTUTORE"));  
		    strAutonomia = Utils.notNull(row.getAttribute("strAutonomia"));  
		    strNoteProgettoSocioAss = Utils.notNull(row.getAttribute("strNoteProgettoSocioAss"));  
		    strCondizLavIncompat = Utils.notNull(row.getAttribute("strCondizLavIncompat"));  
	        
	        descrCom = Utils.notNull(row.getAttribute("descrCom"));
	        
	        // aggiornamento del lock ottimistico in aggiornamento
        	numklodiagnosifunzionale = String.valueOf(((BigDecimal)row.getAttribute("NUMKLODIAGNOSIFUNZIONALE")).intValue());
	        
	        cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
	        dtmIns = (String) row.getAttribute("DTMINS");

		    cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
	    	//cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		    	    
	        dtmMod = (String) row.getAttribute("DTMMOD");
	        operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);        
  }  


  //Profilatura ------------------------------------------------
  String _page = (String) serviceRequest.getAttribute("PAGE");   
  PageAttribs attributi = new PageAttribs(user, _page);
  
  boolean canModify= attributi.containsButton("aggiorna");
  boolean canDelete= attributi.containsButton("RIMUOVI");
  boolean canInsert= attributi.containsButton("INSERISCI");
   
  boolean readOnlyStr = !canModify;
  String fieldReadOnly = canModify?"false":"true"; 
  
  /*------------------------ inserire il parametro cdnFunzione ---------------------------*/
  _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
 
  //RequestContainer.getRequestContainer().getSessionContainer().setAttribute("fromLinguette","1");
  
  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);
  
  String codice = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codice");
  String descrizione = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"descrizione");  

  //prgDiagnosiFunzionale = Utils.notNull(row.getAttribute("prgDiagnosiFunzionale"));
  
  Vector tipiServizi = serviceResponse.getAttributeAsVector("ComboServizi.ROWS.ROW");
  Vector tipiServiziSelezionati = serviceResponse.getAttributeAsVector("ComboServiziSelezionati.ROWS.ROW");
  
    
%>



<html>
<head>
	<%if(!finestra.equals("")){%>
    	<title>Lista servizi</title>	
	<%}else{%>
    	<title>SocioLav.jsp</title>
    <%}%>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
	<%@ include file="../global/Function_CommonRicercaComune.inc" %>
    <af:linkScript path="../../js/"/>

	<script language="JavaScript">
          
          var flagChanged = false;
          
          function fieldChanged() {
           <% if (!readOnlyStr){ %> 
              flagChanged = true;
           <%}%> 
          }

		  
		  function go(url, alertFlag) {
		  	  // Se la pagina è già in submit, ignoro questo nuovo invio!
			  if (isInSubmit()) return;
  
			  var _url = "AdapterHTTP?" + url;
			  if (alertFlag == 'TRUE' ) {
			    if (confirm('Confermi operazione')) {
			      setWindowLocation(_url);
			    }
			  }
			  else {
			    setWindowLocation(_url);
			  }
		  }

		  
		  var urlpage="AdapterHTTP?";	

		  function getURLPageBase() {    
		    urlpage+="CDNFUNZIONE=<%=_funzione%>&";
		    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
		    return urlpage;
 		  }
          
		  function cfIsChecked(){
		      var flg = eval(document.forms[0].FLGINTERDETTO);
			  //alert("Is check CF? "+flg.value);
			  if((flg.value!=null) && (flg.value=="S"))
			  { nascondi("CF_NOcheck");
			  }
			  else
			  { mostra("CF_NOcheck");
			  }
		  }
		  function mostra(id)
		  {   
		  	  var div = document.getElementById(id);
			  div.style.display="";
		  }
		  
		  function nascondi(id)
		  {   var div = document.getElementById(id);
			  div.style.display="none";
		  }
		  

		  function aggiornaServizi(){
			  if (flagChanged) { 
			  	alert("I dati sono cambiati.\nSalvarli prima di gestire i servizi!");
			  }	else {		  
			  	  var f;
	 		      f = "AdapterHTTP?PAGE=CMSocioLavPage&finestra=1";
	 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
	 		      f = f + "&cdnLavoratore=<%=cdnLavoratore%>";
				  f = f + "&prgDiagnosiFunzionale=<%=prgDiagnosiFunzionale%>";
				  
				  var t = "_blank";
				  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=300,height=200,top=100,left=100";
	 		  	  window.open(f, t, feat);
	 		  }
		  }
		  
		  function inserisciServizi(){
			  var url;
 		      url = "AdapterHTTP?PAGE=CMSocioLavPage&nuovoServizio=1";
 		      url = url + "&CDNFUNZIONE=<%=_funzione%>";
 		      url = url + "&CDNLAVORATORE=<%=cdnLavoratore%>";
		      <%
		      for (int j = 0; j < tipiServizi.size(); j++) { //ciclo nella query
					SourceBean rowTipiServizi = (SourceBean)tipiServizi.get(j);
		
					String codTipiServizi = (String)rowTipiServizi.getAttribute("CODICE");
					String dscTipiServizi = (String)rowTipiServizi.getAttribute("DESCRIZIONE");
				    %>
				    var k = 0;
				    <%
					for (int i = 1; i <= tipiServizi.size(); i++) { //ciclo nel javascript
					%>
						k++;
						if(document.Frm2.servizi.options[<%=i%>].value == "<%=codTipiServizi%>" && document.Frm2.servizi.options[<%=i%>].selected){
							var codice = document.Frm2.servizi.options[<%=i%>].value;
							url = url + "&codServizio"+k+"="+codice;	
						}	
					<%
					}
			  }
			  %>
			  url = url + "&numRows=" + "<%=tipiServizi.size()%>";
			  //alert(url);
			  window.opener.document.location = url;
			  window.close();
		  }
		  
		  function deleteServ(codice){
			  if (flagChanged) { 
			  	alert("I dati sono cambiati.\nSalvarli prima di gestire i servizi!");
			  }	else {		  
			  	  var f;
	 		      f = "AdapterHTTP?PAGE=CMSocioLavPage&eliminaServizio=1&MODULE=M_Elimina_Servizio";
	 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
	 		      f = f + "&cdnLavoratore=<%=cdnLavoratore%>";
				  f = f + "&prgDiagnosiFunzionale=<%=prgDiagnosiFunzionale%>";
				  f = f + "&codServizio=" + codice;
				  
				  setWindowLocation(f);
	 		  }
		  }
		  
		  //Cerca comune tutore
		  function cercaCom(criterio){
			  var f;
 		      f = "AdapterHTTP?PAGE=CMSocioLavPage&cercaComune=1";
 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
			  f = f + "&CRITERIO=" + criterio;
			  f = f + "&cod=" + document.Frm1.CODCOMTUTORE.value;
			  f = f + "&descr=" + document.Frm1.descrCom.value;
			  f = f + "&cap=" + document.Frm1.cap_tut.value;
			  //manca il cap
			  
			  var t = "_blank";
			  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100";
			  if(criterio=="codice" && document.Frm1.CODCOMTUTORE.value!="")
			  	window.open(f, t, feat);
			  if(criterio=="descrizione" && document.Frm1.descrCom.value!="")
			  	window.open(f, t, feat);
			  if(criterio=="cap" && document.Frm1.cap_tut.value!="")
			  	window.open(f, t, feat);
		  }
		   
		  function AggiornaForm (codice, descrizione, cap) {
			  window.opener.document.Frm1.CODCOMTUTORE.value = codice;
			  window.opener.document.Frm1.descrCom.value = descrizione;
			  window.opener.document.Frm1.cap_tut.value = cap;
			  window.close();
		  }
		  
		  /*		  
		  NB: l'utente viene preso dalla sessione
		  function aggiornaPage(){
		  	  document.Frm1.cdnUtMod.value = "<%=((BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_"))%>";
		  }
		  */
		  
		  function abilitaTutore(){
		  	if(document.Frm1.flgInterdetto.value == "S"){
				document.Frm1.strCognomeNomeTutore.disabled = false;
				document.Frm1.strIndirizzoTutore.disabled = false;
				document.Frm1.CODCOMTUTORE.disabled = false;
				document.Frm1.descrCom.disabled = false;
				document.Frm1.cap_tut.disabled = false;				  
			}
			else{
				document.Frm1.strCognomeNomeTutore.disabled = true;
				document.Frm1.strCognomeNomeTutore.value = "";
				
				document.Frm1.strIndirizzoTutore.disabled = true;
				document.Frm1.strIndirizzoTutore.value = "";
				
				document.Frm1.CODCOMTUTORE.disabled = true;
				document.Frm1.CODCOMTUTORE.value = "";
				
				document.Frm1.descrCom.disabled = true;
				document.Frm1.descrCom.value = "";
				
				document.Frm1.cap_tut.disabled = true;
				document.Frm1.cap_tut.value = "";
			}
		  }
		  
		  function controlloReset(){
		  	<%if ( ("S".equalsIgnoreCase(flgInterdetto)) ) {%>
				document.Frm1.strCognomeNomeTutore.disabled = false;
				document.Frm1.strIndirizzoTutore.disabled = false;
				document.Frm1.CODCOMTUTORE.disabled = false;
				document.Frm1.descrCom.disabled = false;
				document.Frm1.cap_tut.disabled = false;				  
			<%}else{%>
				document.Frm1.strCognomeNomeTutore.disabled = true;
				document.Frm1.strIndirizzoTutore.disabled = true;
				document.Frm1.CODCOMTUTORE.disabled = true;
				document.Frm1.descrCom.disabled = true;
				document.Frm1.cap_tut.disabled = true;
			<%}%>
		  }
		  
	</script>

</head>


<body class="gestione" onload="rinfresca()">



	<%if(!finestra.equals("")){%>
		<br><br>
		<af:form method="POST" action="AdapterHTTP" name="Frm2" onSubmit="">	  
		    <table align="center" border="0">
		    	<tr >
				    <td colspan="2" align="center">
				    	<p class="titolo">
					      Servizi di riferimento
					    </p>  
				    </td>	
	    		</tr>
		    	<tr >
				    <td colspan="2" align="center">
				      &nbsp;
				    </td>	
	    		</tr>
		    	<tr >
				    <td colspan="2" align="center">
				      <af:comboBox name="servizi" moduleName="ComboServizi"	addBlank="true" multiple="true" />
				    </td>	
	    		</tr>
		    	<tr >
				    <td colspan="2" align="center">
				      &nbsp;
				    </td>	
	    		</tr>
		    	<tr>
				    <td align="center">
				      <input type="button" class="pulsanti" value="Inserisci" onClick="inserisciServizi()">
				    </td>	
				    <td align="center">
				      <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
				    </td>	
	    		</tr>    
		    </table>
	    </af:form>
	<%
	}
	if(!cercaComune.equals("")){%>
		<br><br>
		<af:list moduleName="M_CercaComune"  skipNavigationButton="1" jsSelect="AggiornaForm"
	             canDelete="<%= canDelete ? \"1\" : \"0\" %>" 
	             canInsert="<%= canInsert ? \"1\" : \"0\" %>"   />

	    <table align="center">
	    	<tr>
			    <td>
			      <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
			    </td>	
    		</tr>    
	    </table>
	<%
	
	}
	if(cercaComune.equals("") && finestra.equals("")){
	
		InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user, true);
	  	testata.setSkipLista(true);
	
		testata.show(out);
		
		//Linguette_Parametro l = new Linguette_Parametro(user, _funzione, "CMSocioLavPage", cdnLavoratore, "1", true);
		LinguetteConfigurazioneRegione l = new LinguetteConfigurazioneRegione (user, _funzione, "CMSocioLavPage" , cdnLavoratore , "1", true , "LNDGNFNZ");
					
		l.show(out);
    %>

	<center>
		<font color="green">
			<af:showMessages prefix="M_inserisci_servizio"/>
			<af:showMessages prefix="M_Elimina_Servizio"/>
			<af:showMessages prefix="M_Aggiorna_SocioLav"/>
    	</font>
	</center>
	
	<af:showErrors />

	
	<p class="titolo"></p>

	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="">

		<input type="hidden" name="PAGE" value="CMSocioLavPage" />		
		<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
		<input type="hidden" name="aggiornaSocio" value="1">
		
		<input type="hidden" name="NUMKLODIAGNOSIFUNZIONALE" value="<%= numklodiagnosifunzionale %>">

		
		<%= htmlStreamTop %>

<%-- ************************************************* --%>

		      <table class="main" border="0">
		        <tr>
		          <td colspan="5">&nbsp;</td>	
		        </tr>
		        <tr>
		          <td class="etichetta" colspan="1">Persona interdetta</td>	
				  <td class="campo" colspan="4">
					    <af:comboBox 
					      name="flgInterdetto"
					      classNameBase="input"
					      disabled="<%= String.valueOf(!canModify) %>"
					      onChange="fieldChanged(); abilitaTutore();">
					      <option value=""  <% if ( "".equalsIgnoreCase(flgInterdetto) )  { %>SELECTED="true"<% } %> ></option>
					      <option value="S" <% if ( "S".equalsIgnoreCase(flgInterdetto) ) { %>SELECTED="true"<% } %> >Sì</option>
					      <option value="N" <% if ( "N".equalsIgnoreCase(flgInterdetto) ) { %>SELECTED="true"<% } %> >No</option>
					    </af:comboBox>&nbsp;&nbsp;&nbsp;
				  </td>
		        </tr>
		        <tr>
		          <td colspan="5">&nbsp;</td>
		        </tr>
		        <tr>
		          <td colspan="5"><br/><div class="sezione2">Tutore per la persona interdetta</div></td>
		        </tr>
		        <tr>
		          <td class="etichetta">Cognome e Nome&nbsp;</td>
				  <td class="campo" colspan="4"><af:textBox validateOnPost="true" disabled="false" classNameBase="input" type="text" name="strCognomeNomeTutore" value="<%=strCognomeNomeTutore%>" size="40" maxlength="100" readonly="<%=fieldReadOnly%>" onKeyUp="fieldChanged();"/>&nbsp;</td>
		        </tr>
		        <tr>
		          <td class="etichetta">Indirizzo&nbsp;</td>
				  <td class="campo" colspan="4"><af:textBox validateOnPost="true" disabled="false" classNameBase="input" type="text" name="strIndirizzoTutore" value="<%=strIndirizzoTutore%>" size="40" maxlength="100" readonly="<%=fieldReadOnly%>" onKeyUp="fieldChanged();"/>&nbsp;</td>
		        </tr>
		        <tr>   
				  <td class="etichetta">Comune res/dom&nbsp;</td>
				  <td class="campo" colspan="2">
		                  <af:textBox title="" value="<%=CODCOMTUTORE%>" disabled="false" classNameBase="input" name="CODCOMTUTORE" size="4" readonly="<%=fieldReadOnly%>" onKeyUp="fieldChanged(); PulisciRicerca(document.Frm1.CODCOMTUTORE, document.Frm1.CODCOMTUTOREHID, document.Frm1.descrCom, document.Frm1.descrComHid, document.Frm1.cap_tut, document.Frm1.cap_tut_Hid, 'codice');"  /> 
		                  &nbsp;
		                  <%if (canModify) { %>
		                  <a href="javascript:btFindComuneCAP_onclick(document.Frm1.CODCOMTUTORE, 
		                  											  document.Frm1.descrCom, 	
		                  											  document.Frm1.cap_tut, 
		                  											  'codice',
		                  											  'compatibilitaComuneCPI()',null,'inserisciComSocioLavNonScaduto()');">
		                  	<img src="../../img/binocolo.gif" alt="Cerca per codice"></a>
		                  <%}%>
		                  <af:textBox type="hidden" name="CODCOMTUTOREHID" value="<%=CODCOMTUTORE%>"/>
		                  &nbsp;&nbsp;
		                  
		                  
		                  <af:textBox title="" value="<%=descrCom%>" disabled="false" classNameBase="input" name="descrCom" size="30" readonly="<%=fieldReadOnly%>" onKeyUp="fieldChanged(); PulisciRicerca(document.Frm1.CODCOMTUTORE, document.Frm1.CODCOMTUTOREHID, document.Frm1.descrCom, document.Frm1.descrComHid, document.Frm1.cap_tut, document.Frm1.cap_tut_Hid, 'descrizione');" />&nbsp;
		                  
		                  <%if (canModify) { %>
		                  <a href="javascript:btFindComuneCAP_onclick(document.Frm1.CODCOMTUTORE, 
		                  											  document.Frm1.descrCom, 
		                  											  document.Frm1.cap_tut, 
		                  											  'descrizione',
		                  											  'compatibilitaComuneCPI()',null,'inserisciComSocioLavNonScaduto()');">
		                  	<img src="../../img/binocolo.gif" alt="Cerca per descrizione"></a>   
		                  <%}%>
		                  <af:textBox type="hidden" name="descrComHid" value="<%=descrCom%>"/>
				  </td>
				  <td class="etichetta">Cap&nbsp;</td>
				  <td class="campo">
		                  <af:textBox title="" value="<%=cap_tut%>" disabled="false" classNameBase="input" name="cap_tut" size="5" maxlength="5" readonly="<%=fieldReadOnly%>" onKeyUp="fieldChanged(); PulisciRicerca(document.Frm1.CODCOMTUTORE, document.Frm1.CODCOMTUTOREHID, document.Frm1.descrCom, document.Frm1.descrComHid, document.Frm1.cap_tut, document.Frm1.cap_tut_Hid, 'cap');" />&nbsp;
		                  <af:textBox type="hidden" name="cap_tut_Hid" value="<%=cap_tut%>"/>
				  </td>
				  
		        </tr>		        
		      </table>
		      <br>

  	          <%if ( !("S".equalsIgnoreCase(flgInterdetto)) ) {%> 
					<SCRIPT language="JavaScript">
						document.Frm1.strCognomeNomeTutore.disabled = true;
						document.Frm1.strIndirizzoTutore.disabled = true;
						document.Frm1.CODCOMTUTORE.disabled = true;
						document.Frm1.descrCom.disabled = true;
						document.Frm1.cap_tut.disabled = true;
					</SCRIPT>
			  <%}%>

<%-- ************************************************* --%>
		      <table border="0" class="main">
		        <tr>
		          <td colspan="2"><br/><div class="sezione2">Autonomia personale</div></td>
		        </tr>
		        <tr>
		          <td colspan="2" class="campo">&nbsp;&nbsp;
		          	<af:textArea cols="80" rows="5" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="input"  
                		name="strAutonomia" 
                		value="<%=strAutonomia%>"  
                        required="false" title="Note" onKeyUp="fieldChanged();"/>
				  </td>
		        </tr>
		        <tr>
		          <td>&nbsp;</td>
		          <td>&nbsp;</td>
		        </tr>		        
		        <tr>
		          <td colspan="2"><br/><div class="sezione2">Progetto socio-assistenziale</div></td>
		        </tr>
		        <tr>
					<td class="campo">
							<af:list moduleName="ComboServiziSelezionati"  skipNavigationButton="1" jsDelete="deleteServ"
						             canDelete="<%= canModify ? \"1\" : \"0\" %>" 
						             canInsert="<%= canInsert ? \"1\" : \"0\" %>"   />
					
							&nbsp;&nbsp;	
							<%if (canModify) { %>				
								<input type="button" onclick="aggiornaServizi();" value = "Aggiungi servizio" class="pulsanti">
								&nbsp;&nbsp;
							<%}%>
					</td>					
					<td>
					        <af:textArea cols="20" rows="5" maxlength="1000" readonly="<%=fieldReadOnly%>" classNameBase="input"  
				               		name="strNoteProgettoSocioAss" 
				               		value="<%=strNoteProgettoSocioAss%>" validateOnPost="true" 
				                    required="false" title="Note" onKeyUp="fieldChanged();"/>
					</td>
		        </tr>
		        <tr>
		          <td>&nbsp;</td>
		          <td>&nbsp;</td>
		        </tr>
		        <tr>
		          <td>&nbsp;</td>
		          <td>&nbsp;</td>
		        </tr>
		        <tr>
		          <td class="campo">&nbsp;&nbsp;&nbsp;Condizioni di lavoro incompatibili</td>
		          <td>&nbsp;</td>
		        </tr>
		        <tr>
					<td class="campo">&nbsp;&nbsp;
				        <af:textArea cols="60" rows="5" maxlength="1000" readonly="<%=fieldReadOnly%>" classNameBase="input"  
			               		name="strCondizLavIncompat" 
			               		value="<%=strCondizLavIncompat%>" validateOnPost="true" 
			                    required="false" title="Note" onKeyUp="fieldChanged();"/>
					</td>
					<td>&nbsp;</td>
				</tr>
		        <tr>
		          <td colspan="2">
			          &nbsp;
		          </td>
		        </tr>		          		    
		        <tr>
		          <td colspan="2">
			          &nbsp;
		          </td>
		        </tr>				
		        <tr>
		          <td colspan="2" align="center">
			          <!--<input class="pulsanti" type="submit" name="inserisci" value="Inserisci"/>-->
					  <%if (canModify) { %>
			          	<input type="submit" class="pulsanti" name="aggiorna" value="Aggiorna">			         
			            &nbsp;&nbsp;
			            <input type="reset" class="pulsanti" value="Annulla" onClick="controlloReset();"/>
			            &nbsp;&nbsp;
			          <%}%>  
		          </td>
		        </tr>				
			  </table>
			  <br><br>
			  
			  <%= htmlStreamBottom %>
	      	  <center>
	      	    <table>
	      	  	  <tr>
	      			<td align="center">
	      				<% operatoreInfo.showHTML(out); %>
	      			</td>
	      		  </tr>
	      	    </table>
	          </center>

	</af:form>
	<%} //fine else%>
</body>
</html>
