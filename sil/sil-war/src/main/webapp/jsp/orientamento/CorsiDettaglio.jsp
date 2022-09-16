<%@ page contentType="text/html;charset=utf-8"%>
<%-- /////////////////////////////////// --%>
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                  
                  it.eng.sil.util.*,
                  java.util.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*,
                  java.math.*" %>
<%-- /////////////////////////////////// --%>
<%@ taglib uri="aftags" prefix="af" %>
<%-- /////////////////////////////////// --%>        
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%-- /////////////////////////////////// --%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%-- /////////////////////////////////// --%>
<%
boolean onlyInsert = serviceRequest.getAttribute("ONLY_INSERT")==null?false:true;
String COD_LST_TAB = "";
if(onlyInsert){ 
  COD_LST_TAB = "OR_PER";//(String)serviceRequest.getAttribute("COD_LST_TAB");
}


int _funzione = Integer.parseInt(StringUtils.getAttributeStrNotNull(serviceRequest, "CDNFUNZIONE"));
String cdnFunzione= (String)serviceRequest.getAttribute("CDNFUNZIONE");
String _page = (String) serviceRequest.getAttribute("PAGE"); 
String nonFiltrare = Utils.notNull(serviceRequest.getAttribute("NONFILTRARE"));



ProfileDataFilter filter = new ProfileDataFilter(user, _page);
PageAttribs attributi = new PageAttribs(user, _page);
boolean canUpdate = true;
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
} else {
	canUpdate = attributi.containsButton("AGGIORNA");
}



/* inserisci un nuovo Corso  */
boolean inserisciNuovo = !(serviceRequest.getAttribute("inserisciNuovo")==null || 
                            ((String)serviceRequest.getAttribute("inserisciNuovo")).length()==0);
/////////////////////////////////////

String cdnLavoratore = null;
//cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");

//Azienda
String STRRAGIONESOCIALE = "";
String STRCODICEFISCALE = "";
String STRPARTITAIVA = "";
String prgAzienda = "";
String prgUnita =  "";

//Dati Corso
  String     prgcorso         = null;
  String     stredizionecorso           = null; 
  String     prgprogrammaq  = null; 
  String     datiniziocorso         = null; 
  String     datfinecorso           = null; 
  String     strnotecorso = null;
  String     strdescrizionecorso = null;
  String     strtitolocorso      = null;
  String     strsedeformazione      = null;
  String     numore      = null;
  String     numklocorso      = null;
  String     dtmIns             = null; 
  String     dtmMod             = null;    
  BigDecimal cdnUtIns           = null; 
  BigDecimal cdnUtMod           = null;

  //titolo programma
  String titoloprog = null; 
  
  
  
  
  
  
if (inserisciNuovo){ 
  
	prgcorso           =          Utils.notNull(serviceRequest.getAttribute("prgcorso"));
	stredizionecorso             =          Utils.notNull(serviceRequest.getAttribute("stredizionecorso"));
	prgprogrammaq    =          Utils.notNull(serviceRequest.getAttribute("prgprogrammaq"));
	datiniziocorso           =          Utils.notNull(serviceRequest.getAttribute("datiniziocorso"));         
	datfinecorso             =          Utils.notNull(serviceRequest.getAttribute("datfinecorso"));         
	strnotecorso   =          Utils.notNull(serviceRequest.getAttribute("strnotecorso"));   	
	strdescrizionecorso   =          Utils.notNull(serviceRequest.getAttribute("strdescrizionecorso"));
	strtitolocorso   =          Utils.notNull(serviceRequest.getAttribute("strtitolocorso"));
	numore   =          Utils.notNull(serviceRequest.getAttribute("numore"));
	numklocorso   =          Utils.notNull(serviceRequest.getAttribute("numklocorso"));
    dtmIns              =          Utils.notNull(serviceRequest.getAttribute("DTMINS"));         
    dtmMod              =          Utils.notNull(serviceRequest.getAttribute("DTMMOD"));         
    cdnUtIns            =          (BigDecimal)  serviceRequest.getAttribute("CDNUTINS");         
    cdnUtMod            =          (BigDecimal)  serviceRequest.getAttribute("CDNUTMOD") ;
    strsedeformazione   =          Utils.notNull(serviceRequest.getAttribute("strsedeformazione"));
    titoloprog = Utils.notNull(serviceRequest.getAttribute("titoloprog"));  
    
}
if (!inserisciNuovo) { // dettaglio colloquio lavoratore
	SourceBean row = null;
    Vector rows= serviceResponse.getAttributeAsVector("M_Load_Corsi.ROWS.ROW");
    // siamo in dettaglio per cui avro' al massimo una riga
    if (rows.size()==1) {
        row = (SourceBean)rows.get(0);
        
        prgcorso           =          Utils.notNull(row.getAttribute("prgcorso"));        
        stredizionecorso             =          Utils.notNull(row.getAttribute("stredizionecorso"));
        prgprogrammaq    =          Utils.notNull(row.getAttribute("prgprogrammaq"));        
        datiniziocorso           =          Utils.notNull(row.getAttribute("datiniziocorso"));         
        datfinecorso             =          Utils.notNull(row.getAttribute("datfinecorso"));        
        strnotecorso   =          Utils.notNull(row.getAttribute("strnotecorso"));        
        strdescrizionecorso             =          Utils.notNull(row.getAttribute("strdescrizionecorso"));
        strtitolocorso             =          Utils.notNull(row.getAttribute("strtitolocorso"));
        numore             =          Utils.notNull(row.getAttribute("numore"));
        numklocorso             =          Utils.notNull(row.getAttribute("numklocorso"));
        dtmIns              =          Utils.notNull(row.getAttribute("DTMINS"));         
        dtmMod              =          Utils.notNull(row.getAttribute("DTMMOD"));         
        cdnUtIns            =          (BigDecimal)  row.getAttribute("CDNUTINS");         
        cdnUtMod            =          (BigDecimal)  row.getAttribute("CDNUTMOD");
        strsedeformazione   =          Utils.notNull(row.getAttribute("strsedeformazione"));    
        titoloprog          =                Utils.notNull(row.getAttribute("strtitolo"));
                    
    }
} 

Testata operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);  

 
 boolean readOnlyStr = false;
 String fieldReadOnly = null;
 if(inserisciNuovo){
 	fieldReadOnly = "false";
 }else{
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
String htmlStreamBottom = StyleUtils.roundBottomTable(!inserisciNuovo);
//Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);






%>



<html>
<head>
<title>Corso Dettaglio</title>
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
	    	<%if (readOnlyStr){ %> 
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
	  
		var urlpage="AdapterHTTP?";

		function getURLPageBase() {    
		    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
		    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&"; 
		    return urlpage;
		}
	
		function indietroC() {
		  // Se la pagina è già in submit, ignoro questo nuovo invio!
		  if (isInSubmit()) return;
		
		 if(flagChanged)
		 { if(!confirm("I dati sono cambiati.\nProcedere lo stesso?"))
		   { return false;
		   }
		 }
		    urlpage = getURLPageBase();
		    urlpage+="PAGE=ListaCorsiPage&";
		    <%if(onlyInsert){%>
		      urlpage+="COD_LST_TAB=<%=COD_LST_TAB%>&";
		      urlpage+="ONLY_INSERT=&";
		    <%}%>
		    urlpage+="prgprogrammaq=<%=prgprogrammaq%>&";
		    urlpage+="titoloprog=<%=titoloprog%>&";		    	
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
			    <%if(onlyInsert){%>
			      urlpage+="COD_LST_TAB=<%=COD_LST_TAB%>&";
			      urlpage+="ONLY_INSERT=&";
			    <%}%>
			    urlpage+="prgprogrammaq=<%=prgprogrammaq%>&";
			    urlpage+="prgcorso=<%=prgcorso%>&";
			    urlpage+="titoloprog=<%=titoloprog%>&";
			    urlpage+="strtitolocorso=<%=strtitolocorso%>&";
			    			    
			    setWindowLocation(urlpage);
			}

		
		
		function dissociaAziendaLav(azLav,aggAzLav){
	 		 if(document.Frm1.prgprogrammaq.value != ""){
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
			 }
			 apriSelezionaSoggetto(azLav,aggAzLav,'','','');			 	
		 }
	
		function aggiorna(){    
		    if ( controllaFunzTL() ) { // e' il controllo che di default fa il tag af:form bypassato dalla funzione js				
				document.Frm1.MODULE.value="M_SaveCorsi";
				document.Frm1.action+="?pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE")%>";//Per il ritorno dall'associa
		        doFormSubmit(document.Frm1);
		    }
		    else return;
		}
	
		function inserisci(){
		    if ( controllaFunzTL() ) {
		    	document.Frm1.MODULE.value="M_Insert_Corsi";
		        document.Frm1.action+="?pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE")%>";//Per il ritorno dall'associa
		        doFormSubmit(document.Frm1);
		    }
		    else return;
		}
		
</script>

<script language="Javascript">
    if(window.top.menu != undefined)
       window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
<body class="gestione" onload="rinfresca()">
<af:showMessages prefix="M_Insert_Corsi"/>
<af:showMessages prefix="M_SaveCorsi"/>



<font color="red"><af:showErrors/></font>

<p class="titolo"> Programma <%=titoloprog%></p>
<%if (inserisciNuovo) {%>
<p class="titolo"> Inserimento Corso </p>
 <%} else {%>
<p class="titolo"> Corso <%=strtitolocorso%> </p>
<%}%>
 
 


<af:form name="Frm1" method="POST" action="AdapterHTTP">    
    <input type="hidden" name="CDNUTINS" value="<%=cdnUtIns%>">
    <input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">    
    <input type="hidden" name="MODULE" value=""><%-- campo valorizzato dalla funzione js specifica della azione richiesta --%>    
    <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione %>">
    <input type="hidden" name="prgprogrammaq" value="<%=Utils.notNull(prgprogrammaq) %>">
    <input type="hidden" name="prgcorso" value="<%=Utils.notNull(prgcorso) %>">
    <input type="hidden" name="numklocorso" value="<%=numklocorso%>">                
    <input type="hidden" name="NONFILTRARE" value="<%=nonFiltrare%>">                
    <input type="hidden" name="PAGE" VALUE="CorsiPage"><%-- valore eventualmente cambiato dalla funzione js handler --%>        
   
    
       
         <%=htmlStreamTop%>
         <table class="main">
         
         <tr><td class="etichetta">Titolo</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="text"
                      name="strtitolocorso" value="<%=strtitolocorso%>" validateOnPost="true" required="true"
                      size="30" maxlength="30" onKeyUp="fieldChanged();" title="Titolo"/>
            </td>
        </tr>
      
        <tr><td class="etichetta">Edizione</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="text"
                      name="stredizionecorso" value="<%=stredizionecorso%>" validateOnPost="true" required="true"
                      size="30" maxlength="30" onKeyUp="fieldChanged();" title="Titolo"/>
            </td>
        </tr>
          <tr><td class="etichetta">Descrizione</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="text"
                      name="strdescrizionecorso" value="<%=strdescrizionecorso%>" validateOnPost="true" required="true"
                      size="30" maxlength="30" onKeyUp="fieldChanged();" title="strcodiceesterno"/>
            </td>
        </tr>
        
          <tr><td class="etichetta">Data Inizio</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
                      name="datiniziocorso" value="<%=datiniziocorso%>" validateOnPost="true" required="false"
                      size="12" maxlength="10" onKeyUp="fieldChanged();" title="Data Inizio"/>
            </td>
        </tr>
        
        <tr><td class="etichetta">Data Fine</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
                      name="datfinecorso" value="<%=datfinecorso%>" validateOnPost="true" required="false"
                      size="12" maxlength="10" onKeyUp="fieldChanged();" title="Data Fine"/>
            </td>
        </tr>
        
        <tr><td class="etichetta">Sede Formazione</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="text"
                      name="strsedeformazione" value="<%=strsedeformazione%>" validateOnPost="true" required="true"
                      size="30" maxlength="30" onKeyUp="fieldChanged();" title="Titolo"/>
            </td>
        </tr>
        
        <tr><td class="etichetta">Numero Ore</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="text"
                      name="numore" value="<%=numore%>" validateOnPost="true" required="true"
                      size="30" maxlength="30" onKeyUp="fieldChanged();" title="Titolo"/>
            </td>
        </tr>
       
        
        <tr>
            <td class="etichetta">Note</td>
            <td class="campo">
              <af:textArea classNameBase="textarea"  name="strnotecorso" cols="27" value="<%=strnotecorso%>"     readonly="<%=fieldReadOnly%>"  />
            </td>
          </tr>
        
        

        <tr>
            <td colspan="2" align=center>
                <table border="0" >
                    <tr>
                        <%if (!inserisciNuovo) {%>
                            <%if (canUpdate) {%>
                                <td align="center"><input type="button" onclick="aggiorna()" value="Aggiorna" class="pulsanti"></td>    
                            <%}%>                                     
                                <input type="hidden" name="aggiornamento" value="1">
                                <td align="center"><input type="button" onclick="ListaIscritti()" value = "Lista Iscritti" class="pulsanti"></td>                           
                        <%} else {%>
                                <input class="pulsanti" type="submit" name="inserisci" value="Inserisci">
                                <input type="hidden" name="insDoc" value="0">
				             	<input type="hidden" name="inserisciNew" value="1">
                        <%}%>
                        <td align="center"><input type="button" onclick="indietroC()" value = "Torna alla Lista" class="pulsanti"></td>
                        
                    </tr>                        
                </table>
                
                
                
                
            </td>
        </tr>        
    
    </table>     
 <%=htmlStreamBottom%>    
 <center><% operatoreInfo.showHTML(out); %></center>  
</af:form>
<%	out.print(htmlStreamBottom);   %>
</body>
</html>

