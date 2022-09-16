<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.util.*,
                  java.util.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*,
                  java.math.*,
                  it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  //flag booleano che discrimina l'inserimento o la modifica
  boolean flag_insert=false;

  //inizializzo i campi
  BigDecimal cdnLavoratore = null;
  String strCognome = null;
  String strNome = null;
  
  BigDecimal prgDichDisponibilita = null;
  String CODSTATOATTO = null;
  
  String datDichiarazione = null;
  BigDecimal prgElencoAnagrafico= null ;
  String CODTIPODICHDISP = null;
  String CODULTIMOCONTRATTO = null;
  BigDecimal PRGSTATOOCCUPAZ = null;
  String CODSTATOOCCUPAZ = null;
  String descStato = null;
  String DATSCADCONFERMA = null;
  String DATSCADEROGAZSERVIZI = null;
  String STRNOTE = null;
  String CODMOTIVOFINEATTO = null;
  String DATFINE = null;
  String NUMDELIBERA = null;
  BigDecimal cdnUtIns = null;
  String dtmIns = null;
  BigDecimal cdnUtMod = null;
  String dtmMod = null;
  BigDecimal NUMKLODICHDISP = null;
  String datInizio = null;
  String codCPI = null;    
  String descCPI = null;  
  BigDecimal prgPatto = null;
  BigDecimal numKloPatto = null;
  String prgPattoStr = "";
  String numKloPattoStr = "";
  String codMotivoRiapertura = "";
  String motivoAnnullamento = "";
  String descMotivoAnnullamento = "";
  String flgRischioDisoccupazione = "";
  String flgLavoroAutonomo = "";
  String flgDidL68 = "";
  String datLicenziamento = null;
  boolean canAnnullaDid = true; 
  Testata operatoreInfo = null;
  
  /* @author riccardi
   *
   * E' stato aggiunto un nuovo parametro nel dettaglio 'flag_competenza' per individuare la competenza dell'utente sul lavoratore
   * Può assumere come valore:
   *                         -  false: quando non c'è la competenza
   *                         -  nessun valore perchè non viene passato nella serviceRequest: quando c'è la competenza 
   */
  
  boolean flag_competenza = serviceRequest.containsAttribute("flag_competenza") && !serviceRequest.getAttribute("flag_competenza").toString().equals("false");
  
  //se è stato richiesto l'inserimento abbiamo nella servicerequest
  //il parametro "inserisci" che corrisponde al pulsante premuto
  if (serviceRequest.containsAttribute("Inserisci")){
    flag_insert=true;
  } else {
    flag_insert=false;
  }

  if (flag_insert) {
    try{
      cdnUtIns = (BigDecimal)serviceRequest.getAttribute("cdnUtIns");
      prgDichDisponibilita = (BigDecimal)serviceRequest.getAttribute("prgDichDisponibilita");     
    }
    catch(Exception ex){
      // non faccio niente
    }
  }//if

  String numConfigDidL68 = serviceResponse.containsAttribute("M_CONFIG_DID_L68.ROWS.ROW.NUM")?
			serviceResponse.getAttribute("M_CONFIG_DID_L68.ROWS.ROW.NUM").toString():it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
  
  String codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_").toString();

    Vector rows= serviceResponse.getAttributeAsVector("M_DISPODETTAGLIOSTORICO.ROWS.ROW");

    if (rows.isEmpty())
    {   flag_insert=true;
        String cdnLavoratoreStr = (String) serviceRequest.getAttribute("CDNLAVORATORE");
        cdnLavoratore = new BigDecimal(cdnLavoratoreStr);
    } 
    
    if(!flag_insert && rows != null && !rows.isEmpty())
    {SourceBean row = (SourceBean) rows.elementAt(0);
     cdnLavoratore        = (BigDecimal) row.getAttribute("CDNLAVORATORE");
     strCognome           = (String)     row.getAttribute("strCognome");          																																				 
     strNome              = (String)     row.getAttribute("strNome");             																																	 
     prgDichDisponibilita = (BigDecimal) row.getAttribute("prgDichDisponibilita");																																	 
     CODSTATOATTO         = (String)     row.getAttribute("CODSTATOATTO");        																																	 
     PRGSTATOOCCUPAZ      = (BigDecimal) row.getAttribute("prgStatooccupaz");     																																	 
     CODSTATOOCCUPAZ      = (String)     row.getAttribute("codStatoOccupaz");     																																	 
     descStato            = (String)     row.getAttribute("DESCRIZIONESTATO");
     datDichiarazione     = (String)     row.getAttribute("datDichiarazione");    																																	 
     prgElencoAnagrafico  = (BigDecimal) row.getAttribute("prgElencoAnagrafico"); 																																	 
     CODTIPODICHDISP      = (String)     row.getAttribute("CODTIPODICHDISP");     																																	 
     CODULTIMOCONTRATTO   = (String)     row.getAttribute("CODULTIMOCONTRATTO"); 																																	 
     DATSCADCONFERMA      = (String)     row.getAttribute("DATSCADCONFERMA");     																																	 
     DATSCADEROGAZSERVIZI = (String)     row.getAttribute("DATSCADEROGAZSERVIZI");
     STRNOTE              = (String)	 row.getAttribute("STRNOTE");
     CODMOTIVOFINEATTO    = (String)     row.getAttribute("CODMOTIVOFINEATTO");   																																	 
     DATFINE              = (String)     row.getAttribute("DATFINE");
     NUMDELIBERA	      = (String) 	 row.getAttribute("NUMDELIBERA");
     NUMKLODICHDISP       = (BigDecimal) row.getAttribute("NUMKLODICHDISP");      																																	 
     datInizio            = (String)     row.getAttribute("datInizio");           																																	 
     codCPI               = (String)     row.getAttribute("codCpi");              																																	 
     descCPI              = (String)     row.getAttribute("descCPI");  
     motivoAnnullamento   = (String)     row.getAttribute("motivoannullamento");
     descMotivoAnnullamento = (String)   row.getAttribute("descmotivoannullamento");
     flgRischioDisoccupazione = StringUtils.getAttributeStrNotNull(row, "flgRischioDisoccupazione");
     flgLavoroAutonomo = StringUtils.getAttributeStrNotNull(row, "flgLavoroAutonomo");
     datLicenziamento = (String)     row.getAttribute("datLicenziamento");
     flgDidL68 = StringUtils.getAttributeStrNotNull(row, "flgDidL68");
     
     
     cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
     dtmIns   = (String)     row.getAttribute("DTMINS");
     cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
     dtmMod   = (String)     row.getAttribute("DTMMOD");
     codMotivoRiapertura = (String)row.getAttribute("CODMOTIVORIAPERTURAATTO");
     prgPatto = (BigDecimal) row.getAttribute("prgPatto");
     numKloPatto = (BigDecimal) row.getAttribute("numKloPatto");
     prgPattoStr = prgPatto!=null?prgPatto.toString():"";
     numKloPattoStr = numKloPatto!=null?numKloPatto.toString():"";
     //non posso annullare una DId già annullata
     Vector statoAttoDid = serviceResponse.getAttributeAsVector("M_getStatoAttoDid.ROWS.ROW");
     for(int i=0;i<statoAttoDid.size();i++){
     	SourceBean tmp = (SourceBean) statoAttoDid.get(i);
     	if(CODSTATOATTO.equalsIgnoreCase( (String)tmp.getAttribute("codice") ) &&
     	  	((String)tmp.getAttribute("CODMONOPRIMADOPOINS")).equalsIgnoreCase("D") ){
			canAnnullaDid = false;
			break;
     	}
     }
    } 
    else {
      flag_insert = true;
      Vector rowCpI  = serviceResponse.getAttributeAsVector("M_GETINFVALIDEDISPO.ROWS.ROW");
      if(rowCpI != null && !rowCpI.isEmpty()) {
         SourceBean row  = (SourceBean) rowCpI.elementAt(0);
         PRGSTATOOCCUPAZ = (BigDecimal) row.getAttribute("PRGSTATOOCCUPAZ");
         descStato       = (String)     row.getAttribute("DESCRIZIONESTATO");
         datInizio       = (String)     row.getAttribute("DATINIZIO");
         descCPI         = (String)     row.getAttribute("CPITITOLARE");              																																	 
         codCPI          = (String)     row.getAttribute("CODCPITIT");             																																	 
      }//if()
    }//else
    /*if ( flag_insert ) {
      cdnUtIns= codiceUtenteCorrente;
    }*/

   
    PageAttribs attributi = new PageAttribs(user, "DispoDettaglioPage");
    boolean rdOnly   = true; //!attributi.containsButton("AGGIORNA");
    boolean canInsert=  false; //attributi.containsButton("INSERISCI");
    boolean canPrint =  attributi.containsButton("STAMPA");

   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
   
   InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
   testata.setSkipLista(true);
   testata.show(out);

  //"Creo" le linguette --
  /*String cdnFun = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  if(cdnFun != null && cdnFun != "null" ) {
    int _funzione = Integer.parseInt(cdnFun);
    String _page = (String) serviceRequest.getAttribute("PAGE"); 
    Linguette l = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));   
    l.show(out);
  }*/
  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  //----------------------
  
  PageAttribs attributiDettaglio = new PageAttribs(user, "DispoLavDettaglioInformazioniStorichePage");
  boolean canAnnullaDidProfilato = attributiDettaglio.containsButton("ANNULLADID") && flag_competenza;
  boolean canRiapriDid =  attributiDettaglio.containsButton("RIAPRIDID") && flag_competenza;
  
  //SourceBean sourceRiapertura = (SourceBean)serviceRequest.getAttribute("M_CHECKRIAPERTURADID.ROW");
  String checkRiaperturaDid = StringUtils.getAttributeStrNotNull(serviceResponse,"M_CHECKRIAPERTURADID.ROW.checkRiaperturaDid");
  
   	rows= serviceResponse.getAttributeAsVector("M_PROTOCOLLATO.ROWS.ROW");
    BigDecimal numProt  = null;
    BigDecimal prgDocumento = null;
    String annoProt = "";
    String dataProt = "";
    String oraProt  = "";
    String docInOrOut = "";
    String docDiIO    = "";
    String docRif     = "";
    if(rows != null && !rows.isEmpty())
    { SourceBean row = (SourceBean) rows.elementAt(0);
      numProt  = SourceBeanUtils.getAttrBigDecimal(row, "numprotocollo", null);
      prgDocumento = SourceBeanUtils.getAttrBigDecimal(row,"prgdocumento", null);
      BigDecimal annoP = (BigDecimal) row.getAttribute("ANNOPROT");
      annoProt = annoP!=null ? annoP.toString() : "";
      dataProt = StringUtils.getAttributeStrNotNull(row,"datprot");
      oraProt  = StringUtils.getAttributeStrNotNull(row,"oraProt");
      docInOrOut  = StringUtils.getAttributeStrNotNull(row,"CODMONOIO");
      docRif  = StringUtils.getAttributeStrNotNull(row,"STRDESCRIZIONE");
    }
    
    if( docInOrOut.equalsIgnoreCase("I") ) 
    { docDiIO = "Input";
    } 
    else if ( docInOrOut.equalsIgnoreCase("O") )
    { docDiIO = "Output";
    } 

String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);

	//configurazione chiusura did multipla
	boolean canChiusuraDidMultipla = false;
	String numConfigurazioneChiusuraDidMultipla = SourceBeanUtils.getAttrStrNotNull(serviceResponse, "M_GetNumConfigurazioneChiusuraDidMultipla.ROWS.ROW.NUM");
	if ("1".equals(numConfigurazioneChiusuraDidMultipla)) {
		canChiusuraDidMultipla = true;
	}
	
%>


<html>
<head>
<title>Dettaglio delle disponibilità</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<af:linkScript path="../../js/"/>
<SCRIPT TYPE="text/javascript">
<!--

function btFindComune_onclick(codcomune, codcomunehid, nomecomune, nomecomunehid) {	
		if (nomecomune.value == ""){
			nomecomunehid.value = "";
			codcomunehid.value = "";
			codcomune.value = "";
			}
		else if (nomecomune != nomecomunehid) 
			window.open ("AdapterHTTP?PAGE=RicercaComunePage&strdenominazione="+nomecomune.value.toUpperCase()+"&retcod="+codcomune.name+"&retnome="+nomecomune.name);
    
}


function btFindCittadinanza_onclick(codcittadinanza, codcittadinanzahid, cittadinanza, cittadinanzahid, nazione, nazionehid) {
	
		if (nazione.value == ""){
			nazionehid.value = "";
			codcittadinanzahid.value = "";
			codcittadinanza.value = "";
 			cittadinanzahid.value = "";
			cittadinanza.value = "";
			} else
		if (nazione != nazionehid) 
			window.open ("AdapterHTTP?PAGE=RicercaCittadinanzaPage&nazione="+nazione.value.toUpperCase()+"&retcod="+codcittadinanza.name+"&retcittadinanza="+cittadinanza.name+"&retnazione="+nazione.name);
    
}

function apriPopUpStoriche (cdnlav) {
    popupurl="AdapterHTTP?PAGE=DispoLavInformazioniStorichePage&cdnLavoratore="+cdnlav;
    window.open (popupurl, "InformazioniStoriche", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
}

//-->


  function sceglipage(Scelta, cdnLavoratore, prgDichDisp){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

	   if (Scelta == 1) {

        document.Frm1.Salva.value = "Salva";
        document.Frm1.PAGE.value="DispoDettaglioPage";   
        doFormSubmit(document.Frm1);    
     }
  	 else if (Scelta == 2) {
             document.Frm1.REPORT.value = "patto/Dich_imm_dipo_tutto.rpt";
             document.Frm1.PROMPT0.value = cdnLavoratore;
             document.Frm1.PROMPT1.value = prgDichDisp;
          
        document.Frm1.PAGE.value="stampaReportPage";   
        doFormSubmit(document.Frm1);
     }
  	 else if (Scelta == 3) {
        document.Frm1.Inserisci.value = "Inserisci";
        document.Frm1.PAGE.value="DispoDettaglioPage";   
        doFormSubmit(document.Frm1);
     }
     else if (Scelta==4){
        document.Frm1.PAGE.value="DispoLavInformazioniStorichePage";
        doFormSubmit(document.Frm1);
     }
  }

function underConstr()
{ alert("Funzionaliltà non ancora attivata.");
}

/**
 * Questa funzione riapre la Dich.di Immediata Dispo.
 * il cui progressivo è preso in input come primo 
 * parametro e restituisce il controllo alla pagina chiamante (DispoDettaglio.jsp)
 */
function riapriDid(prgDichDisp,numKloDid,datDichiarazione,note){
	
	if(document.Frm1.codMotivoRiapertura==null || document.Frm1.codMotivoRiapertura.value==""){
		alert("Motivo di riapertura DID obbligatorio");
		return;	
	}
	
	if(confirm("La funzionalità riapre la DID (annullandone la chiusura) e ricalcola in corrispondenza gli impatti.\nVuoi proseguire?")){
		window.opener.riapriDid(prgDichDisp,numKloDid,datDichiarazione,note,'<%=prgPattoStr%>','<%=numKloPattoStr%>',document.Frm1.codMotivoRiapertura.value);
		window.close();
	}
}

function annullaDid(prgDichDisp, numKloDid){

	if(document.Frm1.CODSTATOATTO.value=="PR"){
		alert("Stato atto non modificato");
		return;
	}
	if(document.Frm1.CODSTATOATTO.value=="PA"){
		alert("Stato atto non gestibile");
		return;
	}
	
	if(document.Frm1.MotivoAnnullamentoAtto == null || document.Frm1.MotivoAnnullamentoAtto.value==""){
		alert("Motivo di annullamento DID obbligatorio");
		return;
	}
	
	if(confirm("La funzionalità annulla la DID e ricalcola in corrispondenza gli impatti.\n Vuoi proseguire?")){
		if (isInSubmit()) return;
				
		    window.opener.annullaDidChiusa(prgDichDisp,numKloDid,document.Frm1.CODSTATOATTO.value,document.Frm1.MotivoAnnullamentoAtto.value,'<%=prgPattoStr%>','<%=datDichiarazione%>','<%=prgDocumento%>');
		    window.close();
		   		
	}

}
function apriDettaglioStatoOccupazDich(){
  var urlPage = "AdapterHTTP?PAGE=InfoStatoOccupazPage&PRGDICHDISPONIBILITA=<%=prgDichDisponibilita%>";
  var t = "_blank";
  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=400,top=30,left=180";
  window.open(urlPage, t, feat);
}

function requireDatLicenziamento() {
	var visDatLicenziamento = document.getElementById("visDatLicenziamento");
	if (document.Frm1.flgRischioDisoccupazione.value == 'S') {
      	visDatLicenziamento.style.display = '';
    }
    else {
      document.Frm1.datLicenziamento.value = '';
      visDatLicenziamento.style.display = 'none';
    }     
}

</SCRIPT>


</head>
<body class="gestione" onLoad="requireDatLicenziamento();">
   <script language="Javascript">
   	   if((window.top != null) && (window.top.menu != null))
	       window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
   </script>

   <script language="JavaScript">
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
         <% if (!rdOnly){ %> 
            flagChanged = true;
         <%}%> 
        }
    </script>

<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_SaveDispo"/>
 <af:showMessages prefix="M_InsertDispo"/>
</font>

<af:form name="Frm1" method="POST" action="AdapterHTTP">
<p align="center">
<table class="main">
<tr><td class="titolo" colspan="5"><p class="titolo">Dichiarazione immediata disponibilità</p></td></tr>

	<tr>
		<td colspan="5">&nbsp;</td>
	</tr>

	<tr>
	  <td colspan="5">
	  	<%out.print(htmlStreamTop);%>
	    <table cellpadding="0" cellspacing="0" border="0" width="100%">
	    
		    <tr>
		    	<td class="etichetta2">Stato atto</td>
		        <td>
		        	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		        		<tr>
				        <%
				            String codAtto = Utils.notNull(CODSTATOATTO);
				            if (codAtto.equals("")) codAtto = "PA";
				        %>
				        
		        			<% boolean annullaDid = canAnnullaDidProfilato && canAnnullaDid; %>	
		        			<td>
		        				<af:comboBox  classNameBase="input" disabled ="<%= String.valueOf(!annullaDid)%>"  name="CODSTATOATTO"  
		                          moduleName="M_getStatoAttoDid" selectedValue="<%=Utils.notNull(CODSTATOATTO)%>" required="true"/>
		        			</td>
		        			<td align="right">anno&nbsp;</td>
		        			<td>
		        				<af:textBox classNameBase="input" type="text" name="annoProt" value="<%=annoProt!=null ? annoProt : \"\"%>"
		                                     readonly="true" title="Anno protocollo" size="5" maxlength="4" onKeyUp="fieldChanged();"/>
							</td>
		        			<td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
		        			<td><af:textBox classNameBase="input" type="text" name="numProt" value="<%=Utils.notNull(numProt)%>"
		                                     readonly="true" title="Numero protocollo" size="8" maxlength="100" onKeyUp="fieldChanged();"/>
		                    </td>
		        			<td class="etichetta2">data
		        		        <af:textBox name="dataProt" type="date" value="<%=dataProt%>" size="11" maxlength="10"
		                       				title="data di protocollazione" classNameBase="input" readonly="true" validateOnPost="true" 
		                       				required="false" trim ="false" onKeyUp="cambiAnnoProt(this,annoProt)" onBlur="checkFormatDate(this)"/>
		            		</td>
   		            		<% if (ProtocolloDocumentoUtil.protocollazioneLocale()) { %>
		        			<td class="etichetta2">ora
		           				<af:textBox name="oraProt" type="text" value="<%=oraProt%>" size="6" maxlength="5" title="data di protocollazione"  
		                       				classNameBase="input" readonly="true" validateOnPost="false" required="false" trim ="false"/>
		                    </td>
		                    <% } else { %>
		                    <td><input type="hidden" name="oraProt" value="00:00">
        		            <% } %>
		   				 </tr>
		    		</table>
		
		    	</td>
		    </tr>
	    
		    <tr>
				<td class="etichetta2">
				Motivo Annull. Atto
		    	</td>    	
		    	<td>
		    	
		    		<table cellpadding="0" cellspacing="0" border="0" width="100%">
		        		<tr>
		        			<td>
		        				<% if (!annullaDid) {%>
		        				<af:textBox name="MotivoAnnullamentoAtto" type="text" value="<%=descMotivoAnnullamento%>" size="45"
		                       		classNameBase="input" readonly="true" required="false"/>
		                       	<% } else {%>
		     					<af:comboBox classNameBase="input" name="MotivoAnnullamentoAtto" moduleName="M_getMotivoAnnullamentoDid" addBlank="true" selectedValue="<%=motivoAnnullamento%>" disabled="<%=String.valueOf(!annullaDid)%>"/>*
		     					<% } %>
		        			</td>	
		    				<td class="etichetta2"> Doc. di </td>
		    				<td class="campo2">
				               <af:textBox 	name="docDiIO" type="text" value="<%= docDiIO%>" size="6"  maxlength="6"
		                       			   	title="data di protocollazione" classNameBase="input" readonly="true" validateOnPost="false" 
		                       				required="false" trim ="false" />
				               <af:textBox name="docInOrOut" type="hidden" value="<%= docInOrOut%>" size="6" maxlength="6" title="data di protocollazione"  
				                           classNameBase="input" readonly="true" validateOnPost="false" required="false" trim ="false"/>
				            </td>
		            		<td class="etichetta2">Rif.</td>
		            		<td class="campo2">
		               			<af:textBox name="docInOrOut" type="text" value="<%=docRif%>" size="<%=docRif.length()%>" 
		                           			title="data di protocollazione" classNameBase="input" readonly="true"
		                           			validateOnPost="false" required="false" trim ="false"/>
		                       			
					            </td>
				             <% if(annullaDid){ %>
				             <td>
				             	<input type="button" class="pulsanti" name="AnnullaDId" value="Salva" onclick="annullaDid('<%=prgDichDisponibilita%>','<%=NUMKLODICHDISP%>')">
				             </td>
				             <% } %>
		        		</tr>
		       		</table>
		    	</td>
		    </tr>
	    
		</table>
		<%out.print(htmlStreamBottom);%>
	</td>	
</tr>

<tr ><td colspan="5"><div class="sezione">Informazioni amministrative collegate</div></td></tr>
<tr>
   <td class="etichetta"> Data inserimento nell'elenco anagrafico &nbsp;</td>
     <td class="campo"><af:textBox classNameBase="input" type="date" name="datInizio" value="<%=Utils.notNull(datInizio)%>"
                                   validateOnPost="true" readonly="true" onKeyUp="fieldChanged();"
                                   required="true" title="Data inserimento nell'elenco anagrafico"
                                   size="11" maxlength="10"/></td>
</tr>
<tr>
    <td class="etichetta"> Cpi titolare dei dati&nbsp;</td>
    <td class="campo"><%String strCpI = null;
          if(descCPI != null) {
            strCpI = descCPI;
            strCpI += " - " + codCPI;
          }%>
          <af:textBox classNameBase="input" type="text" name="codCPI" value="<%=Utils.notNull(strCpI)%>" validateOnPost="true" 
                      readonly="true" onKeyUp="fieldChanged();" required="true" title="Cpi titolare dei dati"
                      size="45" maxlength="16"/>
    </td>
  
</tr>

<tr>
 <td class="etichetta">Stato occupazionale&nbsp;</td>
  <td nowrap class="campo">
    <input type="hidden" name="prgStatoOccupaz" value="<%=Utils.notNull(PRGSTATOOCCUPAZ)%>" >          
    <input type="hidden" name="codStatoOccupaz" value="<%=Utils.notNull(CODSTATOOCCUPAZ)%>" > 
    <%int size = 11, max = 65;
      if(descStato != null)
      { size = descStato.length()+3;
        if(size > max) 
        { descStato = descStato.substring(0,max)+"...";
          size = max + 4;
        }
      }
    %><af:textBox classNameBase="input" name="prgStatoOccupaz2" readonly="true" value="<%=Utils.notNull(descStato)%>"
                  required="true" title="Stato occupazionale" onKeyUp="fieldChanged();" size="<%=size%>" maxlength="100"/>
  </td>
  
	<% if(codAtto.equals("PR") || !canAnnullaDid){ %>
      <td>
        <input type="button" name="dettaglioStatooccupaz"  class="pulsanti"  value="Dettaglio" onClick="javascript:apriDettaglioStatoOccupazDich()">
      </td>
  	<% } %>  
</tr>

<tr ><td colspan="5"><div class="sezione"></div></td></tr>

<tr>
  <td colspan="1" class="etichetta">Data dichiarazione/conferma 
  </td>
  <td  class="campo" colspan="1">
    <af:textBox classNameBase="input" type="date" name="datDichiarazione" value="<%=Utils.notNull(datDichiarazione)%>"
                readonly="<%=String.valueOf(rdOnly)%>" onKeyUp="fieldChanged();" required="true" title="Data dichiarazione/conferma"
                validateOnPost="true" size="12" maxlength="10" />
  </td>
</tr>

<tr>
<!--    <td>prgDichDisponibilita&nbsp;</td> -->
<% if (prgDichDisponibilita!=null) {
    /*
    *   Questo perche' il prgDichDisponibilita e' valorizzato quando c' e' una riga da visualizzare. In fase di inserimento questo 
    *   valore e' assente e per non doverlo mettere in sessione conviene nascondere questo parametro. Il modulo che va ad eseguire 
    *   la query, non trovando questo parametro eseguira' la query (equivalente) che utilizza il parametro cdnLavoratore, sempre
    *   presente nella pagina in ogni suo stato.
    */
%> 
    <td><input type="hidden" name="prgDichDisponibilita" value="<%=Utils.notNull(prgDichDisponibilita)%>" size="22" maxlength="16"></td>
    <%}%>
</tr>






<tr colspan>
  <td class="etichetta">Tipo dichiarazione&nbsp;</td>
  <td   class="campo">
    <af:comboBox onChange="fieldChanged();" disabled="<%=String.valueOf(rdOnly)%>"  name="CODTIPODICHDISP"  moduleName="M_TIPODICHDISP" selectedValue="<%=Utils.notNull(CODTIPODICHDISP)%>"
                 addBlank="true" blankValue="" required="true" title="Tipo dichiarazione"/>
  </td>
</tr>

<tr>
  <td class="etichetta">Attività lavorativa precedente&nbsp;</td>
  <td   class="campo">
   <af:comboBox onChange="fieldChanged();" disabled ="<%=String.valueOf(rdOnly)%>"  name="CODULTIMOCONTRATTO"  moduleName="M_CONTRATTO" selectedValue="<%=Utils.notNull(CODULTIMOCONTRATTO)%>"  addBlank="true"/>
  </td>
</tr>

<tr><td><br/></td></tr>

<tr>
    <td class="etichetta">Data scadenza primo colloquio orientamento &nbsp;</td>
    <td class="campo">
        <af:textBox classNameBase="input" readonly="<%=String.valueOf(rdOnly)%>" onKeyUp="fieldChanged();" type="date"
                    name="DATSCADCONFERMA" value="<%=Utils.notNull(DATSCADCONFERMA)%>" validateOnPost="true"
                    size="12" maxlength="10"/></td>
</tr>

<tr>
    <td class="etichetta">Data scadenza stipula patto &nbsp;</td>
    <td class="campo">
       <af:textBox classNameBase="input" readonly="<%=String.valueOf(rdOnly)%>" onKeyUp="fieldChanged();" type="date"
                   name="DATSCADEROGAZSERVIZI" value="<%=Utils.notNull(DATSCADEROGAZSERVIZI)%>" validateOnPost="true"
                   size="12" maxlength="10"/></td>
  
</tr>

<tr>
    <td class="etichetta">Dich. P.Iva non movimentata &nbsp;</td>
    <td class="campo">
    	<af:comboBox classNameBase="input" name="flgLavoroAutonomo" addBlank="false" onChange="fieldChanged();" 
    		title="Dich. P.Iva non movimentata" disabled ="<%=String.valueOf(rdOnly)%>">
            <OPTION value=""  <%if (flgLavoroAutonomo.equals("") || flgLavoroAutonomo.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>></OPTION>
            <OPTION value="S" <%if (flgLavoroAutonomo.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
        </af:comboBox>
    </td>
</tr>

<tr>
    <td class="etichetta">Rischio disoccupazione &nbsp;</td>
    <td class="campo">
    	<af:comboBox classNameBase="input" name="flgRischioDisoccupazione" addBlank="false" onChange="fieldChanged();" 
    		title="Rischio disoccupazione" disabled ="<%=String.valueOf(rdOnly)%>">
            <OPTION value=""  <%if (flgRischioDisoccupazione.equals("") || flgRischioDisoccupazione.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>></OPTION>
            <OPTION value="S" <%if (flgRischioDisoccupazione.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
        </af:comboBox>
    </td>
</tr>

<tr id="visDatLicenziamento" style="display: none">
    <td class="etichetta">Data licenziamento&nbsp;</td>
    <td class="campo">
       <af:textBox classNameBase="input" readonly="<%=String.valueOf(rdOnly)%>" onKeyUp="fieldChanged();" type="date"
                   name="datLicenziamento" value="<%=Utils.notNull(datLicenziamento)%>" validateOnPost="true"  
                   size="12" maxlength="10"/>&nbsp;*&nbsp;</td>
</tr>

<%if (numConfigDidL68.equals(it.eng.sil.module.movimenti.constant.Properties.CUSTOM_CONFIG)) {%>
	<tr>
	    <td class="etichetta">DID fittizia finalizzata all'iscrizione L.68/99 &nbsp;</td>
	    <td class="campo">
	    	<af:comboBox classNameBase="input" name="flgDidL68" addBlank="false" onChange="fieldChanged();" 
	    		title="DID fittizia finalizzata all'iscrizione L.68/99" disabled ="<%=String.valueOf(rdOnly)%>">
	            <OPTION value=""  <%if (flgDidL68.equals("") || flgDidL68.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>></OPTION>
	            <OPTION value="S" <%if (flgDidL68.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
	        </af:comboBox>
	    </td>
	</tr>
<%} else {%>
	<input type="hidden" name="flgDidL68" value="<%=Utils.notNull(flgDidL68)%>" >
<%}%>

<tr><td><br/></td></tr>

<tr ><td colspan="5"><div class="sezione">Chiusura dell'atto</div></td></tr>
<tr>
    <td class="etichetta">Data fine atto &nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" readonly="<%=String.valueOf(rdOnly)%>" onKeyUp="fieldChanged();" type="data"
                  name="DATFINE" value="<%=Utils.notNull(DATFINE)%>" validateOnPost="true"
                  size="12" maxlength="10"/></td>
</tr>

<tr>
    <td class="etichetta">Motivo fine atto&nbsp;</td>
    <td class="campo">
      <af:comboBox onChange="fieldChanged();" disabled ="<%=String.valueOf(rdOnly)%>"  name="CODMOTIVOFINEATTO"  moduleName="M_MOTFINEATTO" selectedValue="<%=Utils.notNull(CODMOTIVOFINEATTO)%>"  addBlank="true"/></td>
</tr>

<% if (canChiusuraDidMultipla) { %>
<tr>
    <td class="etichetta">Numero determina&nbsp;</td>
    <td class="campo">
					<af:textBox classNameBase="input" 
            					type="integer" 
            					name="NUMDELIBERA" 
            					value="<%=Utils.notNull(NUMDELIBERA)%>" 
            					validateOnPost="true" 
                      			readonly="<%=String.valueOf(rdOnly)%>" 
                      			onKeyUp="fieldChanged();" 
                      			required="false" 
                      			title="Numero determina"
                      			size="45" 
                      			maxlength="25"
                      			disabled="<%=String.valueOf(rdOnly)%>"
                    />
    </td>
</tr>
<% } else { %>
	<input type="hidden" name="NUMDELIBERA" value="<%=Utils.notNull(NUMDELIBERA)%>" />
<% } %>
<% if (checkRiaperturaDid.equalsIgnoreCase("true") && canRiapriDid) { %>
	<tr>
		<td class="etichetta">&nbsp;</td>
		<td class="campo">
			
		</td>
	</tr>
	

	<tr><td colspan="2"><div class="sezione">Riapertura dell'atto</div></td></tr>
	<tr>
	    <td class="etichetta">Motivo riapertura atto</td>
	    <td class="campo" width="20%">
	    	
	    	<af:comboBox  name="codMotivoRiapertura"  moduleName="M_getMotivoRiaperturaDid" selectedValue="<%=codMotivoRiapertura%>"  addBlank="true"/>
	      	&nbsp;*&nbsp;&nbsp;
	      	<input type="button" class="pulsanti" name="RiapriDid" value="Riapri Did" onclick="riapriDid('<%=prgDichDisponibilita%>','<%=(NUMKLODICHDISP.add(new BigDecimal(1)))%>','<%=datDichiarazione%>',document.Frm1.strNote.value)">
	    </td>
	</tr>
	<tr>
		<td colspan="2">
			<table>
			<tr>
				<td width="15%">&nbsp;</td>
				<td width="70%" class="campo"><br>N.B.: Se i movimenti in essere alla data di stipula della did o successivi ad essa non ne permettono (per reddito/durata) la riapertura, la DID rimarrà chiusa. In ogni caso vengono ricalcolati gli impatti e nel caso esposto la DID viene richiusa con motivo "DECADUTO PER AVVIAMENTO" (con data di chiusura opportuna).</td>
				<td width="15%">&nbsp;</td>
			</tr>

			</table>
	</tr>
		<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	

<% } %>
	<tr>
	
		<td class="etichetta">Note&nbsp;</td>
		<td class="campo" colspan="2">
			<% boolean editNote =  !(checkRiaperturaDid.equalsIgnoreCase("true") && canRiapriDid); %>
    			<af:textArea classNameBase="textarea" name="strNote" value="<%=STRNOTE%>"
             cols="60" rows="4" maxlength="100"
             onKeyUp="fieldChanged();" readonly="<%= String.valueOf(editNote) %>"/></td>
			
	</tr>
<%if (!flag_insert) {%>
<tr>
<!--    <td>NUMKLODICHDISP&nbsp;</td> -->
    <td><input type="hidden" name="NUMKLODICHDISP2" value="<%=Utils.notNull(NUMKLODICHDISP)%>" size="10" maxlength="10"  >
        <% BigDecimal keyLock= NUMKLODICHDISP;
           keyLock= keyLock.add(new BigDecimal(1));%>
        <input type="hidden" name="NUMKLODICHDISP"  value="<%=Utils.notNull(keyLock)%>">
    </td>
        
</tr>
<%}%>

</table>

<BR>
<center>
  <% operatoreInfo.showHTML(out); %>
</center>

<table class="main">
<% if (!flag_insert) { %>
<tr>
  <td></td>
  <td align="center"><%/*if(!rdOnly)*/if(false){%><input type="submit" name="Salva"  class="pulsanti"  value="Aggiorna"><%}%></td>
  <td align="right">
  <%--
    <%if(canPrint){%>
      <input name="Invia" type="button" class="pulsanti" value="stampa la dichiarazione"  onclick="sceglipage(2,<%=Utils.notNull(cdnLavoratore)%>,<%=Utils.notNull(prgDichDisponibilita)%>)" ></center>      
      <input  type="hidden" name="prgElencoAnagrafico" value="<%=Utils.notNull(prgElencoAnagrafico)%>">
      <!--<input type="submit" name="Cancella" value="cancella il record">-->
    <%}%>
    --%>
  </td>
</tr>
<tr><td><br/></td></tr>
<tr>
  <td width="33%"></td>
  <td align="center" width="33%"><!--input type="submit" name="Inserisci" value="Inserisci nuovo"-->
  </td>
  <td align="center" width="33%">
      <input type="button" name="infSotriche" class="pulsanti" value="Torna alla lista" onClick="sceglipage(4,'<%= cdnLavoratore %>')">
  </td>
</tr>
<%} else {%>
<tr>
   <td width="33%" align="center"><input type="button" name="infSotriche" value="Torna alla lista" onClick="apriPopUpStoriche()"></td>
   <td width="33%" align="center"><input type="submit" name="insert_dispo" value="Inserisci"></td>
   <td width="33%" align="center"><input class="pulsante" type="button" name="annulla" value="Chiudi senza inserire" onclick="history.back();"></td>
</tr>
<%}%>
</table>

<input  type="hidden" name="CDNLAVORATORE" value="<%=Utils.notNull(cdnLavoratore)%>">    
<input  type="hidden" name="CDNUTINS" value="<%=Utils.notNull(cdnUtIns)%>">    
<input  type="hidden" name="CDNUTMOD" value="<%=Utils.notNull(cdnUtMod)%>">    
<input  type="hidden" name="DTMINS" value="<%=Utils.notNull(dtmIns)%>">
<input type="hidden" name="REPORT" value="">      
<input type="hidden" name="PROMPT0" value=""> 
<input type="hidden" name="PROMPT1" value=""> 
<input type="hidden" name="PAGE" value="DispoLavDettaglioInformazioniStorichePage">

</af:form>
</p>

</body>
</html>
