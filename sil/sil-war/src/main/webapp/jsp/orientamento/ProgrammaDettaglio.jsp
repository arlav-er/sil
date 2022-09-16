<%@ page contentType="text/html;charset=utf-8"%>
<%-- /////////////////////////////////// --%>
<%@ page import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,it.eng.sil.security.User,it.eng.sil.security.PageAttribs,it.eng.sil.security.ProfileDataFilter,it.eng.sil.util.*,java.util.*,it.eng.afExt.utils.*,com.engiweb.framework.security.*,java.math.*" %>
<%-- /////////////////////////////////// --%>
<%@ taglib uri="aftags" prefix="af" %>
<%-- /////////////////////////////////// --%>        
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%-- /////////////////////////////////// --%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%-- /////////////////////////////////// --%>
<%
	boolean onlyInsert = serviceRequest.getAttribute("ONLY_INSERT") == null ? false
			: true;
	String COD_LST_TAB = "";
	if (onlyInsert) {
		COD_LST_TAB = "OR_PER";//(String)serviceRequest.getAttribute("COD_LST_TAB");
	}

	int _funzione = Integer.parseInt(StringUtils
			.getAttributeStrNotNull(serviceRequest, "CDNFUNZIONE"));
	String cdnFunzione = (String) serviceRequest
			.getAttribute("CDNFUNZIONE");
	String _page = (String) serviceRequest.getAttribute("PAGE");
	String nonFiltrare = Utils.notNull(serviceRequest
			.getAttribute("NONFILTRARE"));

	/* inserisci un nuovo Programm */
	boolean inserisciNuovo = !(serviceRequest
			.getAttribute("inserisciNuovo") == null || ((String) serviceRequest
			.getAttribute("inserisciNuovo")).length() == 0);
	/////////////////////////////////////

	String cdnLavoratore = null;
	//cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");

	//Azienda
	String STRRAGIONESOCIALE = "";
	String STRCODICEFISCALE = "";
	String STRPARTITAIVA = "";
	String prgAzienda = "";
	String prgUnita = "";

	//StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");

	//Dati Programmi
	String strtitolo = null;
	String strnote = null;
	String strcodiceesterno = null;
	String datinizio = null;
	String datfine = null;
	String numklokloprogrammaq = null;
	String codstatoprogramma = null;
	String prgprogrammaq = null;
	String dtmIns = null;
	String dtmMod = null;
	BigDecimal cdnUtIns = null;
	BigDecimal cdnUtMod = null;

	if (inserisciNuovo) {

		strtitolo = Utils.notNull(serviceRequest
				.getAttribute("strtitolo"));
		strnote = Utils.notNull(serviceRequest.getAttribute("strnote"));
		strcodiceesterno = Utils.notNull(serviceRequest
				.getAttribute("strcodiceesterno"));
		datinizio = Utils.notNull(serviceRequest
				.getAttribute("datinizio"));
		datfine = Utils.notNull(serviceRequest.getAttribute("datfine"));
		codstatoprogramma = Utils.notNull(serviceRequest
				.getAttribute("codstatoprogramma"));

		dtmIns = Utils.notNull(serviceRequest.getAttribute("DTMINS"));
		dtmMod = Utils.notNull(serviceRequest.getAttribute("DTMMOD"));
		cdnUtIns = (BigDecimal) serviceRequest.getAttribute("CDNUTINS");
		cdnUtMod = (BigDecimal) serviceRequest.getAttribute("CDNUTMOD");

		prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest,
				"PRGUNITA");
		prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,
				"prgAzienda");		
	}
	if (!inserisciNuovo) { // dettaglio colloquio lavoratore
		SourceBean row = null;
		Vector rows = serviceResponse
				.getAttributeAsVector("M_Load_Programmi.ROWS.ROW");
		// siamo in dettaglio per cui avro' al massimo una riga
		if (rows.size() == 1) {
			row = (SourceBean) rows.get(0);

			strtitolo = Utils.notNull(row.getAttribute("strtitolo"));
			strnote = Utils.notNull(row.getAttribute("strnote"));
			strcodiceesterno = Utils.notNull(row
					.getAttribute("strcodiceesterno"));
			datinizio = Utils.notNull(row.getAttribute("datinizio"));
			datfine = Utils.notNull(row.getAttribute("datfine"));
			codstatoprogramma = Utils.notNull(row
					.getAttribute("codstatoprogramma"));
			dtmIns = Utils.notNull(row.getAttribute("DTMINS"));
			dtmMod = Utils.notNull(row.getAttribute("DTMMOD"));
			cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
			cdnUtMod = (BigDecimal) row.getAttribute("CNDUTMOD");
			prgUnita = Utils.notNull(row.getAttribute("PRGUNITA"));
			prgAzienda = Utils.notNull(row.getAttribute("prgAzienda"));
			prgprogrammaq = Utils.notNull(row
					.getAttribute("prgprogrammaq"));
			numklokloprogrammaq = Utils.notNull(row
					.getAttribute("numklokloprogrammaq"));
			

		}
	}

	Testata operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod,
			dtmMod);

	//Profilatura
	
	boolean canUpdate = true;
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	PageAttribs attributi = new PageAttribs(user, _page);
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		canUpdate = attributi.containsButton("UPDATE");		
	}




	// 
	boolean readOnlyStr = false;
	String fieldReadOnly = null;
	if (inserisciNuovo) {
		fieldReadOnly = "false";
	} else {
		fieldReadOnly = "false";
	}

	String strFlgCfOk = "";
	String strFlgDatiOk = "";
	String IndirizzoAzienda = "";
	String descrTipoAz = "";
	String codTipoAz = "";
	String codnatGiurAz = "";

	if (!prgAzienda.equals("") && !prgUnita.equals("")) {
		InfCorrentiAzienda currAz = new InfCorrentiAzienda(prgAzienda,
				prgUnita);
		STRRAGIONESOCIALE = currAz.getRagioneSociale();
		STRPARTITAIVA = currAz.getPIva();
		STRCODICEFISCALE = currAz.getCodiceFiscale();
		IndirizzoAzienda = currAz.getIndirizzo();
		descrTipoAz = currAz.getDescrTipoAz();
		codTipoAz = currAz.getTipoAz();
		codnatGiurAz = currAz.getCodNatGiurAz();
		strFlgDatiOk = currAz.getFlgDatiOk();
		if (strFlgDatiOk != null) {
			if (strFlgDatiOk.equalsIgnoreCase("S")) {
				strFlgDatiOk = "Si";
			} else if (strFlgDatiOk.equalsIgnoreCase("N")) {
				strFlgDatiOk = "No";
			}
		}
	}

	String htmlStreamTop = StyleUtils.roundTopTable(!inserisciNuovo);
	String htmlStreamBottom = StyleUtils
			.roundBottomTable(!inserisciNuovo);
	//Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
%>



<html>
<head>
<title>Programma dettaglio</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<af:linkScript path="../../js/"/> 
	<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
	
	<%@ include file="../movimenti/MovimentiRicercaSoggetto.inc" %>
	<%@ include file="../movimenti/MovimentiSezioniATendina.inc" %>
	<%@ include file="../movimenti/Common_Function_Mov.inc" %>
	<%@ include file="../movimenti/DynamicRefreshCombo.inc" %> 

   <script language="JavaScript">
	   	// Rilevazione Modifiche da parte dell'utente
	   	var flagChanged = false;
	   
	   	function fieldChanged() {
	    	<%if (readOnlyStr) {%> 
	       		flagChanged = true;
	    	<%}%> 
	   	}

	    function aggiornaAzienda(){
	        document.Frm1.STRRAGIONESOCIALE.value = opened.dati.ragioneSociale;
	        document.Frm1.STRPARTITAIVA.value = opened.dati.partitaIva;
	        document.Frm1.STRCODICEFISCALE.value = opened.dati.codiceFiscaleAzienda;
	        document.Frm1.PRGAZIENDA.value = opened.dati.prgAzienda;
	        document.Frm1.PRGUNITA.value = opened.dati.prgUnita;
	        opened.close();
	      }
	      
		
	  
		var urlpage="AdapterHTTP?";

		function getURLPageBase() {    
		    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
		    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&"; 
		    return urlpage;
		}
	
		function indietroP() {
		  // Se la pagina è già in submit, ignoro questo nuovo invio!
		  if (isInSubmit()) return;
		
		 if(flagChanged)
		 { if(!confirm("I dati sono cambiati.\nProcedere lo stesso?"))
		   { return false;
		   }
		 }
		    urlpage = getURLPageBase();
		    urlpage+="PAGE=ListaProgrammiPage&";
		    <%if (onlyInsert) {%>
		      urlpage+="COD_LST_TAB=<%=COD_LST_TAB%>&";
		      urlpage+="ONLY_INSERT=&";
		    <%}%>
		    urlpage+="NONFILTRARE=<%=nonFiltrare%>&"
		    setWindowLocation(urlpage);
		}

		function Corsi() {
			  // Se la pagina è già in submit, ignoro questo nuovo invio!
			  if (isInSubmit()) return;
			
			 if(flagChanged)
			 { if(!confirm("I dati sono cambiati.\nProcedere lo stesso?"))
			   { return false;
			   }
			 }
			    urlpage = getURLPageBase();
			    urlpage+="PAGE=ListaCorsiPage&";
			    <%if (onlyInsert) {%>
			      urlpage+="COD_LST_TAB=<%=COD_LST_TAB%>&";
			      urlpage+="ONLY_INSERT=&";
			    <%}%>
			    urlpage+="NONFILTRARE=<%=nonFiltrare%>&";
			    urlpage+="prgprogrammaq=<%=Utils.notNull(prgprogrammaq)%>&";
			    urlpage+="strtitolo=<%=Utils.notNull(strtitolo)%>&";
			    setWindowLocation(urlpage);
			}

		function Azienda(azLav,aggAzLav){
	 		/* if(document.Frm1.prgprogrammaq.value != ""){
			 	if(!confirm("L'associazione al movimento verrà eliminata, continuare?")) {
					return false;
				}
				document.Frm1.PRGMOVIMENTO.value = ""; 
				document.Frm1.datInizioMov.value = ""; 
				document.Frm1.codStatoAttoMov.value = ""; 
				document.Frm1.codFiscaleAzienda.value = ""; 
				document.Frm1.codFiscaleLavoratore.value = "";				
				document.getElementById("IMG0").src="../../img/patto_ass.gif";
				document.Frm1.assDiss.value="(Associa Movimento)";
			 }*/
			 apriSelezionaSoggetto(azLav,aggAzLav,'','','');			 	
		 }
	
		function aggiorna(){    
		    if ( controllaFunzTL() ) { // e' il controllo che di default fa il tag af:form bypassato dalla funzione js				
				document.Frm1.MODULE.value="M_SaveProgrammi";
				document.Frm1.action+="?pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,
					"PAGECHIAMANTE")%>";//Per il ritorno dall'associa
		        doFormSubmit(document.Frm1);
		    }
		    else return;
		}
	
		function inserisci(){
		    if ( controllaFunzTL() ) {
		    	document.Frm1.MODULE.value="M_Insert_Programmi";
		        document.Frm1.action+="?pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,
					"PAGECHIAMANTE")%>";//Per il ritorno dall'associa
		        doFormSubmit(document.Frm1);
		    }
		    else return;
		}


		function AzioniConcordate(){

			   <%if (inserisciNuovo) {   %>
			       alert('Azione non possibile. Il programma deve esser inserito e collegato al percorso del lavoratore.');
			   <% } else {   %>    urlpage = getURLPageBase();
			    urlpage+="PAGE=AzioniConcordateListaPage&";
			    urlpage+="prgprogrammaq=<%=Utils.notNull(prgprogrammaq)%>&";
			    urlpage+="strtitolo=<%=Utils.notNull(strtitolo)%>&";
			    setWindowLocation(urlpage);
			    <% } %>
		}
		
		

		
		
</script>

<script language="Javascript">
    if(window.top.menu != undefined)
       window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
<body class="gestione" onload="rinfresca()">
<af:showMessages prefix="M_Insert_Programmi"/>
<af:showMessages prefix="M_SaveProgrammi"/>
<af:showMessages prefix="M_del_Programmi"/>

<font color="red"><af:showErrors/></font>
 


<%
 	if (inserisciNuovo) {
 %>
<p class="titolo"> Inserimento Programma </p>
 <%
 	} else {
 %>
<p class="titolo"> Programma <%=strtitolo%> </p>
<%
	}
%>


<af:form name="Frm1" method="POST" action="AdapterHTTP">
    <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"> 
    <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>">
    <input type="hidden" name="CDNUTINS" value="<%=cdnUtIns%>">
    <input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">    
    <input type="hidden" name="MODULE" value=""><%-- campo valorizzato dalla funzione js specifica della azione richiesta --%>    
    <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
    <input type="hidden" name="prgprogrammaq" value="<%=Utils.notNull(prgprogrammaq)%>">
    <input type="hidden" name="numklokloprogrammaq" value="<%=numklokloprogrammaq%>">                
    <input type="hidden" name="NONFILTRARE" value="<%=nonFiltrare%>">                
    <input type="hidden" name="PAGE" VALUE="ProgrammiPage"><%-- valore eventualmente cambiato dalla funzione js handler --%>           
    
    
    
    
        <%-- Azienda --%>
		    <div id="divLookAzi_look" style="display:">
				<span class="sezioneMinimale">
					Azienda&nbsp;			
					&nbsp;
					<a href="#" onClick="Azienda('Aziende', 'aggiornaAzienda');"><img src="../../img/binocolo.gif" alt="Cerca"></a>&nbsp;
				
				</span>
				<%=htmlStreamTop%>
				<table class="main" width="100%">
					<tr valign="top">
						<td class="etichetta">Codice Fiscale</td>
						<td class="campo">
							<input title="Azienda" type="text" name="STRCODICEFISCALE" class="inputView" readonly="true" value="<%=STRCODICEFISCALE%>" size="30" maxlength="16"/>
							<!-- <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('STRCODICEFISCALE')"; </SCRIPT>  -->
						</td>
					</tr>
					<tr valign="top">
						<td class="etichetta">Partita IVA</td>
						<td class="campo">
							<input type="text" name="STRPARTITAIVA" class="inputView" readonly="true" value="<%=STRPARTITAIVA%>" size="30" maxlength="30"/>
						</td>
					</tr>
					<tr valign="top">
						<td class="etichetta">Ragione Sociale</td>
						<td class="campo">
							<input type="text" name="STRRAGIONESOCIALE" class="inputView" readonly="true" value="<%=STRRAGIONESOCIALE%>" size="75" maxlength="120"/>
						</td>
					</tr>
				</table>
				<%=htmlStreamBottom%>
			</div>
			
			  <%=htmlStreamTop%>
         <table class="main">
      
        <tr><td class="etichetta">Titolo</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="text"
                      name="strtitolo" value="<%=strtitolo%>" validateOnPost="true" required="true"
                      size="30" maxlength="30" onKeyUp="fieldChanged();" title="Titolo"/>
            </td>
        </tr>
        
         	   
        <tr><td class="etichetta">Codice Esterno</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="text"
                      name="strcodiceesterno" value="<%=strcodiceesterno%>" validateOnPost="true" required="false"
                      size="30" maxlength="30" onKeyUp="fieldChanged();" title="strcodiceesterno"/>
            </td>
        </tr>
      
        <tr><td class="etichetta">Data Inizio</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
                      name="datinizio" value="<%=datinizio%>" validateOnPost="true" required="true"
                      size="12" maxlength="10" onKeyUp="fieldChanged();" title="Data Inizio"/>
            </td>
        </tr>
        
        <tr><td class="etichetta">Data Fine</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
                      name="datfine" value="<%=datfine%>" validateOnPost="true" required="false"
                      size="12" maxlength="10" onKeyUp="fieldChanged();" title="Data Fine"/>
            </td>
        </tr>
    
        <tr>
            <td class="etichetta">Stato del Programma </td>
            <td class="campo" nowrap>
                <af:comboBox classNameBase="input" onChange="fieldChanged()" name="codstatoprogramma" moduleName="M_Combo_Stato_Programma" required="true" 
                		selectedValue="<%=codstatoprogramma%>" addBlank="true" disabled="<%=fieldReadOnly%>"  title="Stato Programma"/>
                		
                		
                		
         		
            </td>
        </tr>
        
        <tr>
            <td class="etichetta">Note</td>
            <td class="campo">
              <af:textArea classNameBase="textarea"  name="strnote" cols="27" value="<%=strnote%>"     readonly="<%=fieldReadOnly%>"  />
            </td>
          </tr>
        
        

        <tr>
            <td colspan="2" align=center>
                <table border="0" >
                    <tr>
							<%	if (!inserisciNuovo) {  %>
								<% if (canUpdate){			%>
							    <td align="center"><input type="button" onclick="aggiorna()" value="Aggiorna" class="pulsanti">
							    <%  }   %>							
							
							</td>
							<td align="center"><input type="button" onclick="Corsi()"
								value="Corsi" class="pulsanti">
							</td>
							<input type="hidden" name="aggiornamento" value="1">
							<%     	} else {   %>
							<input class="pulsanti" type="submit" name="inserisci"	value="Inserisci">
							<input type="hidden" name="insDoc" value="0">
							<input type="hidden" name="inserisciNew" value="1">


							<%  }   %>
							<td align="center"><input type="button" onclick="AzioniConcordate()" value = "Azioni Concordate" class="pulsanti"></td>
							  <td align="center"><input type="button" onclick="indietroP()" value = "Torna alla Lista" class="pulsanti"></td>		
                    </tr>   
                  
                  
                                     
                </table>
                
                
                
                
            </td>
        </tr>        
    
    </table>     
 <%=htmlStreamBottom%>    
 <center><%
     	operatoreInfo.showHTML(out);
     %></center> 
</af:form>
<%
	out.print(htmlStreamBottom);
%>
</body>
</html>

