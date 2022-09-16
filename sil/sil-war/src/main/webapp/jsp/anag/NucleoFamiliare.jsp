<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                  
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.message.MessageBundle" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
 	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canInsert = false;
	boolean canDelete = false;
  	boolean readOnlyStr=true;
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
        canInsert = attributi.containsButton("INSERISCI");
        readOnlyStr = !attributi.containsButton("AGGIORNA");
        canDelete   =  attributi.containsButton("CANCELLA");
        

    	if((!canInsert) && (readOnlyStr) && (!canDelete)){
    		//canInsert=false;
        	//canDelete=false;
       		//rdOnly=true;
    	}else{
	        boolean canEdit=filter.canEditLavoratore();
	        if (canInsert){
	          canInsert=canEdit;
	        }
	        if (canDelete){
	          canDelete=canEdit;
	        }        
	        if (!readOnlyStr){
	          readOnlyStr=!canEdit;
        	}        
    	}
	}
%>

<% 
    Vector rows = serviceResponse.getAttributeAsVector("M_LOADNUCLEOFAMILIARE.ROWS.ROW");
	SourceBean row                = null;
	
	//Campi DB modifica/inserimento
	String    	codiceFiscale       = null;
    String		cognome   			= null; 
    String     	nome            	= null; 
    String		luogoNascitaCod     = null; 
    String		luogoNascitaDes     = null; 
    String     	dataNascita         = null; 
    String  	servizioCarico      = null;
    String		gradoParentela      = null;
    String		pattoInclusione		= null; 
    String		requisitoLavoro		= null;
    String     	partitaIVA          = null;
    String     	note             	= null; 
    BigDecimal numKlNucleoFamiliare	= null;
    String     dtmIns             	= null; 
    String     dtmMod             	= null;    
    String 	   cdnUtIns         	= null; 
    String     cdnUtMod      	    = null;
    String     prgNucleoFamiliare  	= null;
    
    //Campi Testata e linguette
    InfCorrentiLav testata= null;
    boolean flag_insert  = false;
    Linguette l  = null;
    Testata operatoreInfo = null;

    prgNucleoFamiliare = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PRGNUCLEOFAMILIARE");

    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));   

    String url_nuovo = "AdapterHTTP?PAGE=NucleoFamiliarePage" +
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + _funzione +
                     "&APRIDIV=1";
    
    //Controllo del layer
    String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
    if(apriDiv == null) { apriDiv = "none"; }
    else { apriDiv = ""; }
    
    //Gestione contenuto in caso di inserimento non accettato per discrepanze con il CF
    if(	serviceResponse.containsAttribute("M_INSERTNUCLEOFAMILIARE.SHOWMESSAGE") ||
    	serviceResponse.containsAttribute("M_UPDATENUCLEOFAMILIARE.SHOWMESSAGE") ){
    	apriDiv = "";
   	 	codiceFiscale			 = StringUtils.getAttributeStrNotNull(serviceRequest , "codiceFiscale");
        cognome         		 = StringUtils.getAttributeStrNotNull(serviceRequest, "Cognome");         
        nome      				 = StringUtils.getAttributeStrNotNull(serviceRequest, "Nome");         
        dataNascita      		 = StringUtils.getAttributeStrNotNull(serviceRequest, "dataNascita");         
        partitaIVA          	 = StringUtils.getAttributeStrNotNull(serviceRequest, "partitaIVA"); 
        note          			 = StringUtils.getAttributeStrNotNull(serviceRequest, "note");  
        pattoInclusione			 = StringUtils.getAttributeStrNotNull(serviceRequest, "pattoInclusione");
        requisitoLavoro			 = StringUtils.getAttributeStrNotNull(serviceRequest, "requisitoLavoro"); 
        luogoNascitaCod        	 = StringUtils.getAttributeStrNotNull(serviceRequest, "luogoNascitaCod");
        luogoNascitaDes			 = StringUtils.getAttributeStrNotNull(serviceRequest, "luogoNascitaDes");
        servizioCarico        	 = StringUtils.getAttributeStrNotNull(serviceRequest, "servizioCarico");
        gradoParentela        	 = StringUtils.getAttributeStrNotNull(serviceRequest, "gradoParentela");
        //Necessari in caso di update per l'informazione di ultima modifica
        dtmMod          		 = StringUtils.getAttributeStrNotNull(serviceRequest, "DTMMOD");
        dtmIns					 = StringUtils.getAttributeStrNotNull(serviceRequest, "DTMINS");    
        cdnUtIns       			 = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNUTINS");         
        cdnUtMod        		 = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNUTMOD");
    } 
    
    //Gestione flag modifica/inserimento
    boolean nuovo = true;
    if(serviceResponse.containsAttribute("M_LOADNUCLEOFAMILIARE") || serviceResponse.containsAttribute("M_UPDATENUCLEOFAMILIARE.SHOWMESSAGE") ) {
    	nuovo = false; } else {
    	nuovo = true; }
    
    //Modalità dettaglio
	if(!nuovo) {
	    if(rows != null && !rows.isEmpty())  { 
	        row  = (SourceBean) rows.elementAt(0);    
			
	        codiceFiscale			 = StringUtils.getAttributeStrNotNull(row,"STRCODICEFISCALE");
	        cognome         		 = StringUtils.getAttributeStrNotNull(row,"STRCOGNOME");         
	        nome      				 = StringUtils.getAttributeStrNotNull(row,"STRNOME");         
	        dataNascita      		 = StringUtils.getAttributeStrNotNull(row,"DATNASCITA");         
	        partitaIVA          	 = StringUtils.getAttributeStrNotNull(row,"STRPARTITAIVA"); 
	        note          			 = StringUtils.getAttributeStrNotNull(row,"STRNOTE");  
	        pattoInclusione			 = StringUtils.getAttributeStrNotNull(row,"FLGPATTO");
	        requisitoLavoro			 = StringUtils.getAttributeStrNotNull(row,"FLGREQUISITOLAVORO"); 
	        luogoNascitaCod        	 = StringUtils.getAttributeStrNotNull(row,"CODCOMNAS");
	        luogoNascitaDes			 = StringUtils.getAttributeStrNotNull(row,"STRDENOMINAZIONE");
	        servizioCarico        	 = StringUtils.getAttributeStrNotNull(row,"CODSERVIZIO");
	        gradoParentela        	 = StringUtils.getAttributeStrNotNull(row,"CODGRADO");
	        dtmMod          		 = "" +          Utils.notNull(row.getAttribute("DTMMOD"));
	        dtmIns					 = "" +          Utils.notNull(row.getAttribute("DTMINS"));    
	        cdnUtIns       			 = "" +          Utils.notNull(row.getAttribute("CDNUTINS"));         
	        cdnUtMod        		 = "" +          Utils.notNull(row.getAttribute("CDNUTMOD"));
	        numKlNucleoFamiliare	 = (BigDecimal)  row.getAttribute("NUMKLNUCLEOFAM");
	       }
	}
	
    //Elementi di contorno
    String htmlStreamTop = StyleUtils.roundTopTable(canInsert);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canInsert);
    testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore,user);
    int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
    
    //Linguette e informazioni di inserimento e modifica
    l  = new Linguette(user,  _cdnFunz, _page, new BigDecimal(cdnLavoratore));
    operatoreInfo= new Testata( cdnUtIns, dtmIns, cdnUtMod, dtmMod);
    
	boolean canModify = !readOnlyStr;
	String divStreamTop = StyleUtils.roundLayerTop(canModify);
	String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>
<!-- HTML ------------------------------------------------------------------------------------------------------------------------------------------ -->

<html>
	<head>
		<title>&nbsp;</title>
	
		<link rel="stylesheet" href="../../css/stili.css" type="text/css">
		<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
	
		<%@ include file="../amministrazione/CommonScript.inc"%>
		<%@ include file="../global/apriControllaCF.inc" %>
		<%@ include file="../global/Function_CommonRicercaComune.inc" %>
		<script language="Javascript" src="../../js/docAssocia.js"></script>
		<SCRIPT language="JavaScript" src=" ../../js/layers.js"></SCRIPT>
	
		<script>
			<%@ include file="../patto/_sezioneDinamica_script.inc"%>
		</script>
	
		<af:linkScript path="../../js/"/>
	
		<script language="Javascript">
			<%
			    attributi.showHyperLinks(out, requestContainer, responseContainer,"CDNLAVORATORE=" + cdnLavoratore);
			%>
		</script>  
	
		<script>
		
			//Funzione chiusura layer di inserimento/modifica
			function chiudiLayer() {
				ok=true;
				//Controlla se qualche valore è stato inserito
			  	if (flagChanged) {
			  		if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
			        	ok=false;
			     		} else { 
			         		flagChanged = false;
			     		}	     
			  		}
			  	if (ok) {
			  		//Reset possibili valori inseriti
			  		document.Frm1.codiceFiscale.value		 = null;
			  		document.Frm1.cognome.value				 = null;
			  		document.Frm1.nome.value				 = null;
			  		document.Frm1.dataNascita.value			 = null;
			  		document.Frm1.partitaIVA.value			 = null;
			  		document.Frm1.note.value				 = null;
			  		document.Frm1.pattoInclusione.value		 = null;
			  		document.Frm1.requisitoLavoro.value		 = null;
			  		document.Frm1.luogoNascitaCod.value		 = null;
			  		document.Frm1.luogoNascitaDes.value		 = null;
			  		document.Frm1.servizioCarico.value		 = null;
			  		document.Frm1.gradoParentela.value 		 = null;
			  					  		
			   		ChiudiDivLayer('divLayerDett');
				}
			}
			
			//Funzione di inserimento
			function Insert() {
				//Controllo valore grado parentela, se altro: controllo valore note
				var gradoParentela = document.Frm1.gradoParentela.value;
				if(gradoParentela == "AL"){
					var note = document.Frm1.note.value;
					console.log(note);
					if(note == null || note == ""){
						alert("Necessario specificare il tipo di parentela nel campo Note");
						return;
					}
				}
				document.Frm1.MODULE.value= "M_InsertNucleoFamiliare";
				if(controllaFunzTL())  {
					doFormSubmit(document.Frm1);
				}else
				   	return;
			}
		
			//Funzione di update
			function Update(){
				//Controllo valore grado parentela, se altro: controllo valore note
				var gradoParentela = document.Frm1.gradoParentela.value;
				if(gradoParentela == "AL"){
					var note = document.Frm1.note.value;
					if(note == null || note == ""){
						alert("Necessario specificare il tipo di parentela nel campo Note");
						return;
					}
				}
				document.Frm1.MODULE.value= "M_UpdateNucleoFamiliare";
				if(controllaFunzTL())  {
					doFormSubmit(document.Frm1);
				}else
				   	return;
			}
			
			// Per rilevare la modifica dei dati da parte dell'utente
			var flgInsert = <%=flag_insert%>;
			var flagChanged = false;  
			function fieldChanged() {
				<% if ((!flag_insert) && (canModify)) { out.print("flagChanged = true;");}%>
			}
			
			var objRifWindow;
			 //messaggio d'errore nel caso in cui si provi ad estrarre data di nascita e comune da un codice fiscale non corretto.
			var errorString = 'Il Codice Fiscale non è formalmente corretto.\nNon è possibile completare l\'operazione richiesta.'
			
			function CaricaDataComune () {
				 //eseguo un controllo sul CF. questo controllo viene replicato anche lato server
				if (checkTempCF('codiceFiscale')) {
					alert('Non è possibile estrarre data di nascita e comune da un codice fiscale temporaneo');		
					return;
				}
			    if (checkCF('codiceFiscale')) {
				    var s= "AdapterHTTP?PAGE=CalcolaDataComunePageAnag";
				    var strCodice = new String();
				    strCodice=escape(document.Frm1.codiceFiscale.value);
				    strCodice=strCodice.toUpperCase(strCodice);
				    var width = 0;
				    var height = 0;
				    var left = parseInt((screen.availWidth/2) - (width/2));
				    var top = parseInt((screen.availHeight/2) - (height/2));
				    s += "&strcodicefiscale=" + strCodice;
				    var windowFeatures = "toolbar=0, scrollbars=0, height="+height+", width="+width;//,left=" + left + ",top=" + top + "screenX=" + left + ",screenY=" + top;
					objRifWindow = window.open(s,"Calcola", windowFeatures );
			    }
			 }
			
			function PrendiValori () {
				var valid = objRifWindow.document.Frm.valid.value;
				//se è fallito il controllo lato server stampo il solito messaggio d'errore
				if (valid == "false") {
					objRifWindow.close();
					alert(errorString);		
				}
				else {
				    document.Frm1.dataNascita.value = objRifWindow.document.Frm.data.value;
				    document.Frm1.luogoNascitaDes.value = objRifWindow.document.Frm.comune.value;
				    document.Frm1.luogoNascitaCod.value = objRifWindow.document.Frm.codicecomune.value;
				    document.Frm1.luogoNascitaDesHid.value = objRifWindow.document.Frm.comune.value;
				    document.Frm1.luogoNascitaCodHid.value = objRifWindow.document.Frm.codicecomune.value;
				    objRifWindow.close();
				}
			}

			function checkCF (inputName){
				var cfObj = eval("document.forms[0]." + inputName);
				cfObj.value=cfObj.value.toUpperCase();
				cf=cfObj.value;
				ok=true;
				msg="";
				//controllo che sia lungo 16 caaratteri  
				if (cf.length==11) {
					//se di 11 caratteri deve essere composto di soli numeri
					msg="<%= MessageBundle.getMessage(Integer.toString(MessageCodes.CodiceFiscale.ERRPROV_NAN))%>";
					for (i=0; i<11 && ok; i++) {
				    	c=cf.charAt(i);
				        ok=isDigit(c);
					}		
				}
				else if (cf.length==16) {
		        //scorro tutti i caratteri
		        	for (i=0; i<16 && ok; i++)
		        	{
			            c=cf.charAt(i);
			            if (i>=0 && i<=5){
			                    ok=!isDigit(c);
			                    msg="Errore nei primi sei caratteri del codice fiscale";
			            } else if  (i==6 || i==7) { 
			                    ok=isDigit(c);
			                    msg="Errore nel settimo o nell'ottavo carattere del codice fiscale";
			            } else if (i==8) {
			                    ok=!isDigit(c);
			                    msg="Errore nel nono carattere del codice fiscale";
			            } else if (i==9 || i==10) {
			                    ok=isDigit(c);
			                    msg="Erore nel decimo o nell'undicesimo carattere del codice fiscale";
			            } else if (i==11) {
			                    ok=!isDigit(c);
			                    msg="Errore nell'undicesimo carattere del codice fiscale";
			            } else if (i==15) {
			                    ok=!isDigit(c);
			                    msg="Errore nell'ultimo carattere del codice fiscale: deve essere una lettera";
			            }
		      	  }           
		   		} else {
		      		ok=false;
		      		msg="<%= MessageBundle.getMessage(Integer.toString(MessageCodes.CodiceFiscale.ERR_LUNGHEZZA))%>";
		   		}
		   		if (!ok) {
		        	alert(msg);
		        	cfObj.focus();
		    	}
			  	return ok;
			}

			function checkTempCF(inputName) {
				var cfObj = eval("document.forms[0]." + inputName);
				cfObj.value=cfObj.value.toUpperCase();
				cf=cfObj.value;
				ok=true;
				if (cf.length != 11)
					return false;
				for (i=0; i<11 && ok; i++) {
				   	c=cf.charAt(i);
				    ok=isDigit(c);	
				}		
				return ok;
			}
			
			function checkAnnoNas(inputName){
				  var dataObj = eval("document.forms[0]." + inputName);
				  datanas=dataObj.value;

				  var anni = calcolaEta(datanas);
				  if (anni != -1)
				  {
				    if(anni > 90) {
				       alert("Attenzione: il soggetto ha un\'età maggiore di 90 anni");
				       return false;
				    }
				    else if (anni < 15) {
				      if ( confirm("Attenzione: il soggetto ha un\'età inferiore di 15 anni.\nInserirlo ugualmente?") ){
				         return true;
				      }
				      else {
				        return false;
				      }
				    }
				  
				  	return true
				  }
				  return false;
			}
			
			function calcolaEta(dataNascita) { 
			    dataOdierna = new Date();
			    var items = dataNascita.split("/");
			    annoNascita = items[2];
			    meseNascita = items[1];
			    giornoNascita = items[0];

			    anni = dataOdierna.getFullYear()-parseInt(annoNascita, 10);
			    mesi = dataOdierna.getMonth()+1-parseInt(meseNascita, 10);// getMonth() restituisce 0..11
			    giorni = dataOdierna.getDate()-parseInt(giornoNascita, 10);
			    if (mesi<0 ||(mesi==0 && giorni<0)) anni--;
			    return anni;
			}
			
			function resetNote() {
				document.Frm1.note.value = null;
			}
			
			function checkPIVA(inputName){
				  var pivaObj = eval("document.Frm1." + inputName);
				  pivaObj.value=pivaObj.value.toUpperCase();
				  pIVA=pivaObj.value;
				  var ok = true;
				  var i = document.Frm1.partitaIVA.value.length;  
				  if ((i==11)){
				     var regEx = /^[0-9]{11}/;
				     if (pIVA.search(regEx)==-1)
				     { alert ("La partita IVA deve essere solo numerica");
				       ok=false;
				     }
				  }
				  else{
				  	if (i>0) {
					    alert ("La Partita IVA deve avere una lunghezza pari a 11 caratteri");
					    ok = false;
				    }
				  }
				  return (ok);
			}

		    window.top.menu.caricaMenuLav( <%=_funzione%>,<%=cdnLavoratore%>);
		</script>
	</head>

	<!-- BODY ------------------------------------------------------------------------------------------->

	<body class="gestione" onload="rinfresca()">
		<%
		   testata.show(out);   
		   l.show(out);
		%>

		<af:form method="POST" action="AdapterHTTP" name="Frm1">
		    <input type="hidden" name="PAGE" value="NucleoFamiliarePage">
		    <input type="hidden" name="MODULE" value=""/>
		    <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
		    <input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
		   	<input type="hidden" name="PRGNUCLEOFAMILIARE" value="<%=prgNucleoFamiliare%>"/>
		    <input type="hidden" name="NUMKLNUCLEOFAM" value="<%=numKlNucleoFamiliare%>"/>
		    
		    <p align="center">
		    	
		    	<center>
					<font color="green">
		       		   	<af:showMessages prefix="M_InsertNucleoFamiliare"/>
		         		<af:showMessages prefix="M_DelNucleoFamiliare"/>
		          		<af:showMessages prefix="M_UpdateNucleoFamiliare"/>
		          	</font>
		        	<font color="red">
		         		<af:showErrors />
		        	</font>
		        </center>
				<div align="center">  
				

		      		<af:list moduleName="M_GetNucleoFamiliare" skipNavigationButton="1" 
			               canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
			               canInsert="<%=canInsert ? \"1\" : \"0\"%>" />

			      	
				   	<% if(canInsert) {%>
				    	<input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>'); 
				     	document.getElementById('divMessage').style.display = 'none';" value="Nuovo Componente"/>  
				  	<%}%>
				</div> 
		    <p/>
		
		
			<!-- LAYER ----------------------------------------------------------------------------------------------------------------------------- -->  
			<div id="divLayerDett" name="divLayerDett" class="t_layerDett" style="position:absolute; width:60%; left:50; top:80px; z-index:6; display:<%=apriDiv%>;">
				
			<!-- Stondature ELEMENTO TOP -->
				<%out.print(divStreamTop);%>
			
			    <table width="100%">
			    	<tr>
			        	<td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
			        	<td height="16" class="azzurro_bianco">
					        <%if(nuovo){%>
					          Nuovo Componente Nucleo Familiare
					        <%} else {%>
			          		Dettaglio Componente Nucleo Familiare
			        		<%}%>   
			        	</td>
			        	<td width="16" height="16" onClick="chiudiLayer()" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
			    	</tr>
			    </table>  
			    
	       			<div id="divMessage" style="display:true">
	       				<center >
							<font color="green">
				       		   	<af:showMessages prefix="M_InsertNucleoFamiliare"/>
				         		<af:showMessages prefix="M_DelNucleoFamiliare"/>
				          		<af:showMessages prefix="M_UpdateNucleoFamiliare"/>
				          	</font>
				        	<font color="red">
				         		<af:showErrors />
				        	</font>
			    		</center>
	       			</div>
	       			
			    <table width="100%">
				    <tr><td><br/></td></tr>
				    <tr>
					     <td class="etichetta">Codice Fiscale</td>
					     <af:textBox type="hidden" name="codiceFiscaleHid" value="<%=codiceFiscale%>" />
					     <td colspan="3"><af:textBox classNameBase="input" title="Codice Fiscale" type="text" onKeyUp="fieldChanged();" name="codiceFiscale" value="<%=codiceFiscale%>" validateWithFunction="checkCF" 
					              required="true" readonly="<%=String.valueOf(readOnlyStr)%>" size="21" maxlength="16" validateOnPost="true"/>
					     	<%if (canModify) { %>
					     		<A HREF="javascript:CaricaDataComune();"><IMG name="image" border="0" src="../../img/calc.gif" alt="calcola sesso,data e comune" title="Calcola Sesso, Data di nascita e Comune"/></a>&nbsp;
						    <% }%>
					     </td> 
				    </tr>
				    <tr>
					     <td class="etichetta">Cognome</td>
					     <td colspan="3"><af:textBox classNameBase="input" title="Cognome" type="text" onKeyUp="fieldChanged();"  name="cognome" value="<%=cognome%>" validateOnPost="true" 
					              required="true" readonly="<%=String.valueOf(readOnlyStr)%>" size="30" maxlength="30" />
					     </td>
				    </tr>
				    <tr>
					     <td class="etichetta">Nome</td>
					     <td colspan="3"><af:textBox classNameBase="input" title="Nome" type="text" onKeyUp="fieldChanged();"  name="nome" value="<%=nome%>" validateOnPost="true" 
					              required="true" readonly="<%=String.valueOf(readOnlyStr)%>" size="30" maxlength="30" />
					     </td>
				    </tr>
				   	<tr>
					    <td class="etichetta">Luogo di nascita&nbsp;</td>
					    <td class="campo">
					    	<af:textBox classNameBase="input" title="Codice comune di nascita" 
					                  type="text" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.luogoNascitaCod, document.Frm1.luogoNascitaCodHid, document.Frm1.luogoNascitaDes, document.Frm1.luogoNascitaDesHid, null, null, 'codice');" 
					                  name="luogoNascitaCod" value="<%=luogoNascitaCod%>" size="4" maxlength="4"
					                  readonly="<%= String.valueOf(!canModify) %>"/>&nbsp; 
					   	 	<%if (canModify) { %>
					      		<A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.luogoNascitaCod, document.Frm1.luogoNascitaDes, null, 'codice','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
					    	<%}%>
						    <af:textBox type="hidden" name="luogoNascitaCodHid" value="<%=luogoNascitaCod%>"/>
						    <af:textBox type="text" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.luogoNascitaCod, document.Frm1.luogoNascitaCodHid, document.Frm1.luogoNascitaDes, document.Frm1.luogoNascitaDesHid, null, null, 'descrizione')" 
						    			classNameBase="input" name="luogoNascitaDes"  value="<%=luogoNascitaDes%>" size="30" maxlength="50" required="true"
						                readonly="<%= String.valueOf(!canModify) %>" title="comune di nascita" />&nbsp;&nbsp;&nbsp;
						    <%if (canModify) { %>   
						    	<A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.luogoNascitaCod, document.Frm1.luogoNascitaDes, null, 'descrizione','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>&nbsp;
						    <%}%>
						    <af:textBox type="hidden"  name="luogoNascitaDesHid" value="<%=luogoNascitaDes%>"/>
					    </td>
					</tr>
				    <tr>
					     <td class="etichetta">Data di Nascita</td>
					     <td colspan="3"><af:textBox classNameBase="input" title="Data di Nascita" type="date" onKeyUp="fieldChanged();" name="dataNascita" value="<%=dataNascita%>" validateOnPost="true" 
					              required="true" readonly="<%=String.valueOf(readOnlyStr)%>" size="11" maxlength="12" validateWithFunction="checkAnnoNas"/>
					     </td> 
				    </tr>
				    <tr>
						<td class="etichetta">Servizio in Carico</td>
					    <td class="campo">
					      <af:comboBox 
					      	title ="Servizio in Carico"
					        name="servizioCarico"
					        classNameBase="input"
					        required="true"
					        disabled="<%= String.valueOf(!canModify) %>" 
					        moduleName="M_ComboServizio" 
					        selectedValue="<%=servizioCarico%>" 
					        onChange="fieldChanged();"
					        addBlank="true"/>
					    </td>
					</tr>
					<tr>
						<td class="etichetta">Grado di parantela</td>
					    <td class="campo">
					    	<af:comboBox 
					    	title ="Grado di parentela"
					        name="gradoParentela"
					        classNameBase="input"
					        required="true"
					        disabled="<%= String.valueOf(!canModify) %>" 
					        moduleName="M_ComboGrado" 
					        selectedValue="<%=gradoParentela%>" 
					        onChange="fieldChanged(); resetNote();"
					        addBlank="true"/>
					    </td>
					</tr>
				    <tr>
						<td class="etichetta">Coinvolgimento Patto di inclusione</td>
					    <td class="campo">
					      	<af:comboBox 
					      	title ="Coinvolgimento Patto di inclusione"
					        name="pattoInclusione"
					        classNameBase="input"
					        onChange="fieldChanged();"
					        addBlank="true"
					        disabled="<%= String.valueOf(!canModify) %>" 
					        required="true">					        
					        <option value="S" <% if ( "S".equalsIgnoreCase(pattoInclusione) ) { %>SELECTED="true"<% } %> >Sì</option>
					        <option value="N" <% if ( "N".equalsIgnoreCase(pattoInclusione) ) { %>SELECTED="true"<% } %> >No</option>
					      	</af:comboBox>
					    </td>
					</tr>
					<tr>
					   	<td class="etichetta">Requisito lavoro</td>
					    <td class="campo">
					      	<af:comboBox 
					      	title ="Requisito lavoro"
					        name="requisitoLavoro"
					        classNameBase="input"
					        onChange="fieldChanged();"
					        disabled="<%= String.valueOf(!canModify) %>"
					        required="true"
					        addBlank="true">
					        <option value="S" <% if ( "S".equalsIgnoreCase(requisitoLavoro) ) { %>SELECTED="true"<% } %> >Sì</option>
					        <option value="N" <% if ( "N".equalsIgnoreCase(requisitoLavoro) ) { %>SELECTED="true"<% } %> >No</option>
					      	</af:comboBox>
					    </td>
					</tr>
					<tr>
					     <td class="etichetta">Partita IVA</td>
					     <td colspan="3"><af:textBox classNameBase="input" title="Partita IVA" type="text" onKeyUp="fieldChanged();"  name="partitaIVA" value="<%=partitaIVA%>" validateOnPost="true" 
					               readonly="<%=String.valueOf(readOnlyStr)%>" size="12" maxlength="11" validateWithFunction="checkPIVA" />
					     </td>
				    </tr>
				    <tr>
					     <td class="etichetta">Note</td>
					     <td colspan="3"><af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" cols="40" rows="1" title="Note"  name="note" value="<%=note%>" validateOnPost="true" 
					               readonly="<%=String.valueOf(readOnlyStr)%>" maxlength="1000" />
					     </td>
				    </tr>
			    </table>
			    
				<table class="main" width="100%">
				  	<tr><td>&nbsp;</td></tr>
				  	<tr>		   
				  		<td></td>  
				  	<tr>
				    	<td colspan="2" align="center">
				      	<%if(nuovo) {%> 
					    	<input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
					    	<input type="reset" class="pulsanti" value="Annulla" onClick=""/>
					 	</td>
				   </tr>
				   <tr>
					  	<td>
					      	<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer()">
					  	</td>
				   </tr>
				   		<%}else{
				       		if(!readOnlyStr) {%> 
				   <tr>
					  	<td>
						 	<input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update()">
							<input type="reset" class="pulsanti" value="Annulla" onClick=""/>
					     <%}%>
					<tr>
					  	<td>   
					      <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer();">
					    </td>
				    </tr>
				      <%}%> 	      
				   		</td>
				  	</tr>
				</table>
				
				<%if(!nuovo) {%> 
					<input type="hidden" name="DTMINS" value="<%=dtmIns%>"/>
					<input type="hidden" name="DTMMOD" value="<%=dtmMod%>"/>
					<input type="hidden" name="CDNUTINS" value="<%=cdnUtIns %>"/>
					<input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>"/>
					
				    <table>
						<tr>
					  		<td colspan="2" align="center">
					  			<% if (operatoreInfo!=null) operatoreInfo.showHTML(out); %>
					    	</td>
					  	</tr>    
				    </table>
		    	<%}%>
		
			</div>
			<!-- Stondature ELEMENTO BOTTOM -->
			<%out.print(divStreamBottom);%>
		</af:form>
		
		<script language="Javascript">
			<% 
		    	//Genera il Javascript che si occuperà di inserire i links nel footer
		       	attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
		  	%>
		</script>

	</body>
</html>