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

	/* inserisci un nuovo Corso  */
	boolean inserisciNuovo = !(serviceRequest
			.getAttribute("inserisciNuovo") == null || ((String) serviceRequest
			.getAttribute("inserisciNuovo")).length() == 0);
	/////////////////////////////////////

	String cdnLavoratore = null;
	//cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");
    //String tornare = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"tornare");
	String titoloprog = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"titoloprog"); 
    String strtitolocorso = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"strtitolocorso");
    
    
    SourceBean rowcount = (SourceBean) serviceResponse.getAttribute("M_COUNT_ISCRICORSO.ROWS.ROW");	
	int     countIscr = SourceBeanUtils.getAttrInt(rowcount, "NUMISCR", 0);
	String  strCountIscr = SourceBeanUtils.getAttrStrNotNull(rowcount, "NUMISCR");
	
	//Azienda
	String STRRAGIONESOCIALE = "";
	String STRCODICEFISCALE = "";
	String STRPARTITAIVA = "";
	String prgAzienda = "";
	String prgUnita = "";

	//Dati Iscr Corso
	String prgcorso = null;
	String prgcolloquio = null;
	String prgprogrammaq = null;
	String prgpercorso = null;
	String strnotecorso = null;
	String strdescrizionecorso = null;
	
	String codesitocorso = null;
	String dtmIns = null;
	String dtmMod = null;
	BigDecimal cdnUtIns = null;
	BigDecimal cdnUtMod = null;
	String iscritto = null;
	
	boolean aggiornamento= false;
	String codice = null;

	if (inserisciNuovo) {
		prgcorso = Utils.notNull(serviceRequest
				.getAttribute("prgcorso"));
		prgprogrammaq = Utils.notNull(serviceRequest
				.getAttribute("prgprogrammaq"));
		strnotecorso = Utils.notNull(serviceRequest
				.getAttribute("strnotecorso"));
		strdescrizionecorso = Utils.notNull(serviceRequest
				.getAttribute("strdescrizionecorso"));
		
		dtmIns = Utils.notNull(serviceRequest.getAttribute("DTMINS"));
		dtmMod = Utils.notNull(serviceRequest.getAttribute("DTMMOD"));
		/*cdnUtIns = (BigDecimal) serviceRequest.getAttribute("CDNUTINS");
		cdnUtMod = (BigDecimal) serviceRequest.getAttribute("CDNUTMOD");*/
		prgcolloquio = Utils.notNull(serviceRequest
				.getAttribute("prgcolloquio"));
		codesitocorso = Utils.notNull(serviceRequest
				.getAttribute("codesitocorso"));
		prgpercorso = Utils.notNull(serviceRequest
				.getAttribute("prgpercorso"));
		
		//Vector rowsprg = serviceResponse.getAttributeAsVector("M_Load_IscrittiCorsi.ROWS.ROW");
	}
	if (!inserisciNuovo) { // dettaglio 
		SourceBean row = null;
		Vector rows = serviceResponse
				.getAttributeAsVector("M_Load_IscrittiCorsi.ROWS.ROW");
		
		
		// siamo in dettaglio per cui avro' al massimo una riga
		if (rows.size() == 1) {
			row = (SourceBean) rows.get(0);
			prgcorso = Utils.notNull(row.getAttribute("prgcorso"));
			prgprogrammaq = Utils.notNull(row
					.getAttribute("prgprogrammaq"));
			strnotecorso = Utils.notNull(row
					.getAttribute("strnotecorso"));
			strdescrizionecorso = Utils.notNull(row
					.getAttribute("strdescrizionecorso"));
			
			dtmIns = Utils.notNull(row.getAttribute("DTMINS"));
			dtmMod = Utils.notNull(row.getAttribute("DTMMOD"));
			cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
			cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
			prgcolloquio = Utils.notNull(row.getAttribute("prgcolloquio"));
			codesitocorso = Utils.notNull(row.getAttribute("codesitocorso"));
			prgpercorso = Utils
					.notNull(row.getAttribute("prgpercorso"));
			
			iscritto =  Utils.notNull(row
					.getAttribute("iscritto"));
			
			//Verifico se è dettaglio
			if(serviceRequest.containsAttribute("dettaglio")||serviceRequest.containsAttribute("aggiornamento")){
				aggiornamento = true;
			}
			

			                    
		}
	}
	
	Testata operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);  
	
	// Prendo i dati per tornare dopo inserimento
	if(prgcorso==null){
		prgcorso = Utils.notNull(serviceRequest.getAttribute("prgcorso"));			
	}
	
	if(prgprogrammaq==null){	
		prgprogrammaq = Utils.notNull(serviceRequest.getAttribute("prgprogrammaq"));
	}

	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	PageAttribs attributi = new PageAttribs(user, _page);
	boolean canupdate = true;
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");

	} else {
		canupdate = attributi.containsButton("AGGIORNA");
		
	}

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
<title>Lista Iscritti al Corso </title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/"/> 
	<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
	
	<%@ include file="../movimenti/MovimentiRicercaSoggetto.inc" %>
	<%@ include file="../movimenti/MovimentiSezioniATendina.inc" %>
	<%@ include file="../movimenti/Common_Function_Mov.inc" %>
	<%@ include file="../movimenti/DynamicRefreshCombo.inc" %> 
   <script src="../../js/ComboPair.js"></script>
   <script language="JavaScript">
	   	// Rilevazione Modifiche da parte dell'utente
	   	var flagChanged = false;
	   	var turn = true;
	   
	   	function fieldChanged() {
	    	<%if (readOnlyStr) {%> 
	       		flagChanged = true;
	    	<%}%> 
	   	}

	   
	  
		var urlpage="AdapterHTTP?";

		function getURLPageBase() {    
		    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
		    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&"; 
		    return urlpage;
		}
	
		function indietroI() {
		  // Se la pagina è già in submit, ignoro questo nuovo invio!
		  if (isInSubmit()) return;		 
		 if(flagChanged)
		 { if(!confirm("I dati sono cambiati.\nProcedere lo stesso?"))
		   { return false;
		   }
		 }
		    urlpage = getURLPageBase();
		    urlpage+="PAGE=ListaIscrittiCorsiPage&";
		    <%if (onlyInsert) {%>
		      urlpage+="COD_LST_TAB=<%=COD_LST_TAB%>&";
		      urlpage+="ONLY_INSERT=&";
		    <%}%>
		    urlpage+="prgprogrammaq=<%=prgprogrammaq%>&";
		    urlpage+="prgcorso=<%=prgcorso%>&";
		    urlpage+="titoloprog=<%=Utils.notNull(titoloprog)%>&";		
		    urlpage+="strtitolocorso=<%=Utils.notNull(strtitolocorso)%>&";			    
		    setWindowLocation(urlpage);
		}

		function ListaIscritti() {
			  // Se la pagina è già in submit, ignoro questo nuovo invio!
			  if (isInSubmit()) return;
			
			 if(flagChanged)
			 { if(!confirm("I dati sono cambiati.\nProcedere lo stesso?"))
			   { return false;
			   }
			 }
			    urlpage = getURLPageBase();
			    urlpage+="PAGE=ListaIscrittiCorsiPage&";
			    <%if (onlyInsert) {%>
			      urlpage+="COD_LST_TAB=<%=COD_LST_TAB%>&";
			      urlpage+="ONLY_INSERT=&";
			    <%}%>
			    urlpage+="prgprogrammaq=<%=prgprogrammaq%>&"
			    urlpage+="prgcorso=<%=prgcorso%>&"
			    setWindowLocation(urlpage);
			}

		
		function aggiorna(){    
		    if ( controllaFunzTL() ) { // e' il controllo che di default fa il tag af:form bypassato dalla funzione js				
				document.Frm1.MODULE.value="M_SaveIscrittiCorsi";
				document.Frm1.action+="?pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,
					"PAGECHIAMANTE")%>";//Per il ritorno dall'associa
		        doFormSubmit(document.Frm1);
		    }
		    else return;
		}

		function inserisci(){
		    if ( controllaFunzTL() ) {
		    	document.Frm1.MODULE.value="M_Insert_IscrittiCorsi";
		        document.Frm1.action+="?pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,
					"PAGECHIAMANTE")%>";//Per il ritorno dall'associa
		        doFormSubmit(document.Frm1);
		    }
		    else return;
		}

		

		function IscheckedIscrLav(){
			var IscrLavcheck= false;
        	for (var j = 0; j < document.Frm1.length; j++){
    			var checkbox = document.Frm1.elements[j];
    			if (checkbox.name == "codici_percorso_col") {    				 
    				if (checkbox.type == "checkbox") {    					 
    					if (checkbox.checked == true) {
    						IscrLavcheck = true;
    						break;	
    					}else {    							
    						IscrLavcheck = false;
    					}
    			    }
    		    }
    	    }	
        	if (IscrLavcheck == true) {
        		inserisci();							
				//return true;	
			}else {
				alert('Deve scegliere almeno un lavoratore.');				
				return false;	
			}
		}


		 function verificaEsisteIscr() {
			  // Se la pagina è già in submit, ignoro questo nuovo invio!
			  if (<%=countIscr%> > 0 ){			  
				  IscheckedIscrLav();
			  }else{
				  alert('Non esistono lavoratori con azioni concordate che possono esser iscritti.');
				  return false; 
			  }
	     }
		
		
		
		
</script>

<script language="Javascript">
    if(window.top.menu != undefined)
       window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
<%if ((aggiornamento) || (inserisciNuovo &&  countIscr > 0)){	%>
<body class="gestione" onload="rinfresca()">
<!--<body class="gestione" onload="rinfresca()">-->
<%	} else {%>
  <body class="gestione" onload="indietroI()">


<%}	%>


<font color="red"><af:showErrors/></font>
<af:showMessages prefix="M_Insert_IscrittiCorsi"/>
<af:showMessages prefix="M_SaveIscrittiCorsi"/>   



<p class="titolo"> Programma <%=titoloprog%> -  Corso <%=strtitolocorso%> </p>


 <%if (!inserisciNuovo) {	%>
<p class="titolo"> Lavoratore Iscritto  </p>
<%}else{%> 
<p class="titolo"> Elenco Iscrivibili <br>(Hanno una Azione Concordata)</p>
<!-- <p class="titolo"> Lavoratori che possano esser Iscritti al Corso </p>  -->
<%}%> 
<af:form name="Frm1" method="POST" action="AdapterHTTP">    
    <input type="hidden" name="CDNUTINS" value="<%=cdnUtIns%>">
    <input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">    
    <input type="hidden" name="MODULE" value=""><%-- campo valorizzato dalla funzione js specifica della azione richiesta --%>    
    <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
   
    <input type="hidden" name="prgprogrammaq" value="<%=Utils.notNull(prgprogrammaq)%>">
    <input type="hidden" name="prgcolloquio" value="<%=Utils.notNull(prgcolloquio)%>">
    <input type="hidden" name="prgpercorso" value="<%=Utils.notNull(prgpercorso)%>">
    <input type="hidden" name="prgcorso" value="<%=Utils.notNull(prgcorso)%>">
    
    <input type="hidden" name="titoloprog" value="<%=Utils.notNull(titoloprog)%>">
    <input type="hidden" name="strtitolocorso" value="<%=Utils.notNull(strtitolocorso)%>">
    
    <input type="hidden" name="NONFILTRARE" value="<%=nonFiltrare%>">                
    <input type="hidden" name="PAGE" VALUE="IscrittiCorsiPage">        
    <%if (inserisciNuovo) {	%>
    <input type="hidden" name="rowsprgColloquioprgPercorso" VALUE="rowsprgColloquioprgPercorso">    
    <%}%> 
     
     
         <%=htmlStreamTop%>
         <table class="main">
      
             
   

    
			<%
				if (inserisciNuovo) {
			%>

			
			
			<tr>
			<td colspan="5"><af:list moduleName="M_List_Scelta_Multipla_Iscr" /></td>
			
			</tr> 
			<tr>
            <td colspan="5" align="center" class="" >Esito del Corso </td>
            </tr>
            <tr>
            <td colspan="2" class="" align="center">
                <af:comboBox classNameBase="input" onChange="fieldChanged()" name="codesitocorso" moduleName="M_Combo_Esito_Corso" required="true" 
                		selectedValue="<%=codesitocorso%>" addBlank="true" disabled="<%=fieldReadOnly%>"  title="Esito del Corso"/>
            </td>
            
            </tr>
            

			<%
				} else if(aggiornamento){
			%>
			<tr><td class="etichetta">Lavoratore</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="true"  type="text"
                      name="iscritto" value="<%=iscritto%>" validateOnPost="true" required="true"
                      size="80" maxlength="80" title="iscritto"/>
            </td>
            </tr>
            <tr>
            <td class="etichetta">Esito del Corso </td>
            <td class="campo" nowrap>
                <af:comboBox classNameBase="input" onChange="fieldChanged()" name="codesitocorso" moduleName="M_Combo_Esito_Corso" required="true" 
                		selectedValue="<%=codesitocorso%>" addBlank="true" disabled="<%=fieldReadOnly%>"  title="Esito del Corso"/>
            </td>
            </tr>
            
            <%
				} 
			%>
            

        <tr>
            <td colspan="2" align=center>
                <table border="0" >
                    <tr>
                        <%	if (inserisciNuovo) {  %>      
                        	    <br>
                        	<!--       <input class="pulsanti" type="submit" name="inserisci" value="Inserisci" onclick="IscheckedIscrLav()">-->
                                <input type="hidden" name="insDoc" value="0">
				             	<input type="hidden" name="inserisciNew" value="1">				              
				             	<input type="hidden" name="inserisciNuovo" value="1">
				             	<td align="center"><input type="button" onclick="verificaEsisteIscr()" value="Inserisci" class="pulsanti"></td>
				             	                           
                        <%  } else  if (aggiornamento) {   %>
                        	<%	if (canupdate) {  %>     
                                <td align="center"><input type="button" onclick="aggiorna()" value="Aggiorna" class="pulsanti"></td>
                                <input type="hidden" name="aggiornamento" value="1">     
                             <% }  %>
                        <% }  %>
                        <td align="center"><input type="button" onclick="indietroI()" value = "Torna alla Lista degli Iscritti al Corso" class="pulsanti"></td>                        
                    </tr>                        
                </table>
                
            </td>
        </tr>        
    
    </table>     
 <%=htmlStreamBottom%>     
 <center><% operatoreInfo.showHTML(out); %></center> 
</af:form>
<%
	out.print(htmlStreamBottom);
%>
</body>
</html>

