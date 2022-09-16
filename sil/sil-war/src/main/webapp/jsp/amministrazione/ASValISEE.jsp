<!-- @author: Giordano Gritti -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %> 
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.sil.util.amministrazione.impatti.Controlli,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                     
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean,
                  it.eng.sil.module.movimenti.InfoLavoratore,
                  com.engiweb.framework.security.*,
                  java.text.*,
                  it.eng.afExt.utils.*"
%> 


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%			
			String _page 			= 	(String) serviceRequest.getAttribute("PAGE");
			int _cdnFunz 			= 	Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
			String cdnFunzione 		= 	(String) serviceRequest.getAttribute("CDNFUNZIONE");
			String cdncomponente 	= 	(String) serviceRequest.getAttribute("CDNCOMPONENTE");
			String cdnLavoratore 	= 	(String) serviceRequest.getAttribute("cdnLavoratore");
			String datfineval 		= 	"";
			String datinizioval  	= 	"";
			BigDecimal numvaloreisee= 	null;
			BigDecimal puntValIsee	=	null;  
			BigDecimal numanno		= 	null;
			String numKloValoreIsee = null;
			String strIbanNazione 	= 	"";
			String strIbanControllo = 	"";
			String strCinLav 		= 	"";
			String strAbiLav 		= 	"";
			String strCabLav 		= 	"";
			String strCCLav 		= 	"";
			String strNota 			= 	"";
			String confDtIn			= 	"";
			String confDtFin		=	"";
			boolean btnInserisci 	= false;
			boolean btnInfStoriche 	= false;
			boolean btnAggiorna		= false;
			boolean btnNuovo		= false;	
			boolean btnCancella		= false;
			boolean btnStoricoModifiche = false;
			
			
			//Art 16 on line
			boolean canASOnline = false;
			SourceBean configAsOnline = (SourceBean)serviceResponse.getAttribute("M_Config_AsOnline.ROWS.ROW");
			if (configAsOnline != null) {
				int configOnline = Integer.parseInt(configAsOnline.getAttribute("NUMVALORECONFIG").toString());
				if (configOnline ==1){
					canASOnline = true;
				}
			}

			
			String skipcontroldatafine = serviceResponse.containsAttribute("M_AS_UpdValISEE.SKIP_CONTROL_DATA_FINE")?
					(String) serviceResponse.getAttribute("M_AS_UpdValISEE.SKIP_CONTROL_DATA_FINE"):"FALSE";
			
			BigDecimal do_update_2_dtfine_prgvaloreisee = (BigDecimal) serviceResponse.getAttribute("M_AS_UpdValISEE.DO_UPDATE_2_DTFINE_PRGVALOREISEE");
			
			String esisteDatafinevaliditaVuota = serviceResponse.getAttribute("M_AS_UpdValISEE.ESISTE_DATAFINEVALIDITA_VUOTA") != null?
					(String) serviceResponse.getAttribute("M_AS_UpdValISEE.ESISTE_DATAFINEVALIDITA_VUOTA"):"";
			
			String esisteRecordDTFinePostNuovoInizio = serviceResponse.getAttribute("M_AS_UpdValISEE.ESISTE_RECORD_DTFINE_POST_NUOVOINIZIO")!=null?
					(String) serviceResponse.getAttribute("M_AS_UpdValISEE.ESISTE_RECORD_DTFINE_POST_NUOVOINIZIO"):"";
					
			btnStoricoModifiche = serviceResponse.containsAttribute("M_EXISTS_STORICO_ISEE.ROWS.ROW");
			
			BigDecimal cdnUtIns 	= 	null;
			String dtmIns 			= 	"";
			BigDecimal cdnUtMod 	= 	null;
			String dtmMod 			= 	"";	
			String dtmModhh			= 	"";	
			String dtmInshh			= 	"";	
			
			//INFORMAZIONI OPERATORE
			Testata operatoreInfo 	= 	null;
			//LINGUETTE
			Linguette l 			= 	null;
			//INFORMAZIONI TESTATA LAVORATORE
			InfCorrentiLav testata 	= 	null;
			//Creo le inf. riassuntive del lavoratore
			testata 				= 	new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
			testata.setPaginaLista(_page);
			//Info sul lavoratore
			InfoLavoratore _lav 	= 	new InfoLavoratore(new BigDecimal(cdnLavoratore));
			//Creo le linguette --
			l 						= 	new Linguette(user, _cdnFunz, _page, new BigDecimal(cdnLavoratore));
			String MODULE_NAME 		= 	"M_Load_ISEE";//"M_AS_GetValISEE";
			
			BigDecimal prgValore 	= null;
			Vector vett 			= serviceResponse.getAttributeAsVector("M_Load_ISEE.ROWS.ROW");
			
			PageAttribs attributi 	= 	new PageAttribs(user, _page);
			
			String apriDivLista = ""; 
		    String apriDivDett = (String) serviceRequest.getAttribute("APRIDIV");
		    
		    if(apriDivDett == null) { 
		    	apriDivDett = "none"; 
		    	apriDivLista = "";
		    } else { 
		    	apriDivDett = ""; 
		    	apriDivLista = "none";
		    }
		    
		    String url_aggiorna = "AdapterHTTP?PAGE=ASValoreIseePage" +
                    "&CDNLAVORATORE=" + cdnLavoratore +
                    "&CDNFUNZIONE=" + _cdnFunz +
                    "&APRIDIVCONFAGG=1";
			
		    String apriDivConfAgg =  (String) serviceRequest.getAttribute("APRIDIVCONFAGG"); 	
		    if(apriDivConfAgg != null) {
		    	apriDivLista = "none";
		    	apriDivDett = "none";
		    } else {
		    	apriDivConfAgg = "none";
		    }
			
			//conttrollo sulla profilatura
			boolean canModify 		= 	true;
			boolean infStorButt     = 	false;
			boolean canInsert 		= 	true;
			boolean readOnlyStr     = 	false; 
			boolean cancella		= 	false;
			boolean nuovo 			=	false;
			boolean updDate 		= 	false;	
			
			infStorButt 			= 	attributi.containsButton("INF_STOR");
    		canInsert 				=	attributi.containsButton("INSERISCI");
    		canModify     			=	attributi.containsButton("AGGIORNA");    		
			cancella				= 	attributi.containsButton("RIMUOVI");
			nuovo					= 	attributi.containsButton("NUOVO");
			readOnlyStr 			=   !attributi.containsButton("INSERISCI"); 
			updDate					= 	!attributi.containsButton("INSERISCI");	 
			
			ProfileDataFilter filter = new ProfileDataFilter(user, _page);
			filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
			boolean canView=filter.canViewLavoratore();
			if (! canView){
				response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
				return;
			}			
			boolean canEdit=filter.canEditLavoratore();
		    if (canInsert){
		      canInsert=canEdit;		      
		    }
		    if (!readOnlyStr){
		      readOnlyStr=!canEdit;
		    }        		    
		    if(canModify){
		       canModify=canEdit;
		    }
			if(cancella){
		       cancella=canEdit;
		    }
		    if(nuovo){
		       nuovo=canEdit;
		    }
		    
						
			btnInserisci 			= 	canInsert 	&& btnInserisci;
			btnAggiorna				=	canInsert	&& canModify && btnAggiorna;
			btnNuovo				= 	canInsert 	&& nuovo && btnNuovo;
			btnCancella				=	canInsert 	&& cancella && btnCancella;
			btnInfStoriche 			= 	infStorButt && btnInfStoriche;
			
			
			if( vett != null && vett.size() > 0 ) {
				SourceBean sbValIsee = (SourceBean) vett.get(0);
				//variabili utili per il confronto fra le date lato client
				confDtIn 	= StringUtils.getAttributeStrNotNull(sbValIsee, "DATINIZIOVAL");					
				confDtFin	= StringUtils.getAttributeStrNotNull(sbValIsee, "DATFINEVAL");
				 					
				dtmIns 		= StringUtils.getAttributeStrNotNull(sbValIsee, "DTMINS");
				dtmMod 		= StringUtils.getAttributeStrNotNull(sbValIsee, "DTMMOD");

				datfineval 	= StringUtils.getAttributeStrNotNull(sbValIsee, "DATFINEVAL");

				//confronto data di sistema con data finale per la comparsa o meno del bottone dello storico 					
				String dataFinalePerConfronto = "";
				
				//se non è settata la data fine, utilizzo una data limite, cioè /30/12/2090
				if( datfineval.equals("") ) dataFinalePerConfronto = "30/12/2090";
				else dataFinalePerConfronto = datfineval; 
				
				String dataOdierna = DateUtils.getNow();
						
				int compDate = DateUtils.compare(dataOdierna, dataFinalePerConfronto);					
				
				//se ci sono più di un valore isee, di sicuro almeno un valore è storicizzato, se invece 
				//ho solo un valore, è storicizzato solo se la data fine è minore della data odierna
				if ( (vett.size() > 1) || (vett.size() == 1 && compDate == 1 ) )
					btnInfStoriche = true;
				
				datfineval 	= StringUtils.getAttributeStrNotNull(sbValIsee, "DATFINEVAL");					
				prgValore = (BigDecimal) sbValIsee.getAttribute("prgvaloreisee");
				datinizioval = StringUtils.getAttributeStrNotNull(sbValIsee, "DATINIZIOVAL");
				numvaloreisee = (BigDecimal) sbValIsee.getAttribute("NUMVALOREISEE");
				puntValIsee = (BigDecimal) sbValIsee.getAttribute("NUMPUNTIISEE");
				numanno = (BigDecimal) sbValIsee.getAttribute("NUMANNO");
				strNota = StringUtils.getAttributeStrNotNull(sbValIsee, "STRNOTA");
				cdnUtIns 	= (BigDecimal) sbValIsee.getAttribute("CDNUTINS"); 
				cdnUtMod	= (BigDecimal) sbValIsee.getAttribute("CDNUTMOD");
				dtmInshh	= StringUtils.getAttributeStrNotNull(sbValIsee, "DTMINSHH");
				dtmModhh	= StringUtils.getAttributeStrNotNull(sbValIsee, "DTMMODHH");
				numKloValoreIsee = Utils.notNull(sbValIsee.getAttribute("NUMKLOVALOREISEE"));
				strIbanNazione = StringUtils.getAttributeStrNotNull(sbValIsee, "STRIBANNAZIONE");
				strIbanControllo = StringUtils.getAttributeStrNotNull(sbValIsee, "STRIBANCONTROLLO");
				strCinLav = StringUtils.getAttributeStrNotNull(sbValIsee, "STRCINLAVORATORE");
				strAbiLav = StringUtils.getAttributeStrNotNull(sbValIsee, "STRABILAVORATORE");
				strCabLav = StringUtils.getAttributeStrNotNull(sbValIsee, "STRCABLAVORATORE");
				strCCLav = StringUtils.getAttributeStrNotNull(sbValIsee, "STRCCLAVORATORE");					
				
				//il pulsante "Inserisci" non deve comparire
				btnInserisci = false;
			}
			
			String strPrgValoreIsee = 	"";
			if (prgValore == null) {
				strPrgValoreIsee = "";
				} else {
					strPrgValoreIsee = prgValore.toString();
				}
						
			String strNumAnno 		= 	"";
			if (numanno == null) {
				strNumAnno = "";
				} else {
					strNumAnno = numanno.toString();
				}	
			
			String strNumValoreIsee = 	"";
			if (numvaloreisee == null) {
				strNumValoreIsee = "";
				} else {
					strNumValoreIsee = numvaloreisee.toString();
				}
				
			String strPuntValoreIsee= 	"";
			if (puntValIsee == null) {
				strPuntValoreIsee = "";
				} else {
					strPuntValoreIsee = puntValIsee.toString();
				}		
	
			// profilatura
			if (btnInserisci || (vett == null || vett.size() == 0)) {
				if(!canInsert) {
					readOnlyStr = true;
					updDate = true;
				}
				else { 
					readOnlyStr = false;
					updDate = false;
				}
			}
			else {			    
				canInsert = false;
				// caso di modifica 
				if (canModify) {
					readOnlyStr = false; 
					updDate = true;
				}
				else {
					// caso di disabilitazione dell'inserimento
					readOnlyStr = true;
					updDate = true;
				}
			}
			
			String configIBAN = serviceResponse.containsAttribute("M_GetConfigIBAN.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigIBAN.ROWS.ROW.NUM").toString():"0";
			
			operatoreInfo= new Testata(cdnUtIns, dtmInshh, cdnUtMod, dtmModhh);			
			
			String htmlStreamTop 	= 	StyleUtils.roundTopTable(!readOnlyStr);
			String htmlStreamBottom = 	StyleUtils.roundBottomTable(!readOnlyStr);
			
			String divStreamTop = StyleUtils.roundLayerTop(canModify);
			String divStreamBottom = StyleUtils.roundLayerBottom(canModify);		

		    Vector codmonomotivomodifica = serviceResponse.getAttributeAsVector("M_AS_ComboMotivoModifica.ROWS.ROW");
		    
		    String strCodmonomotivomodifica = "";
		    if (esisteDatafinevaliditaVuota.equalsIgnoreCase("TRUE") || 
		    		esisteRecordDTFinePostNuovoInizio.equalsIgnoreCase("TRUE")){
		    	
		    	// .inc
		    	datinizioval     = StringUtils.getAttributeStrNotNull(serviceRequest, "inizio");	
		    	datfineval       = StringUtils.getAttributeStrNotNull(serviceRequest, "fine");
		    	strNota          = StringUtils.getAttributeStrNotNull(serviceRequest, "strnota");
		    	dtmInshh         = StringUtils.getAttributeStrNotNull(serviceRequest, "DTMINSHH");
		    	dtmModhh         = StringUtils.getAttributeStrNotNull(serviceRequest, "DTMMODHH");
		    	numKloValoreIsee = StringUtils.getAttributeStrNotNull(serviceRequest, "NUMKLOVALOREISEE");
		    	
		    	strNumValoreIsee  = (String) serviceRequest.getAttribute("numvaloreisee");
		    	strPuntValoreIsee = (String) serviceRequest.getAttribute("numpuntiisee");
		    	strNumAnno        = (String) serviceRequest.getAttribute("numanno");		    	
		    	strPrgValoreIsee  = (String) serviceRequest.getAttribute("prgvaloreisee");		    	
				strCodmonomotivomodifica = (String) serviceRequest.getAttribute("codmonomotivomodifica");
				
		        strIbanNazione   = StringUtils.getAttributeStrNotNull(serviceRequest, "strIbanNazione");
		        strIbanControllo = StringUtils.getAttributeStrNotNull(serviceRequest, "strIbanControllo"); 
		        strCinLav        = StringUtils.getAttributeStrNotNull(serviceRequest, "strCinLav");
		        strAbiLav        = StringUtils.getAttributeStrNotNull(serviceRequest, "strAbiLav");
		        strCabLav        = StringUtils.getAttributeStrNotNull(serviceRequest, "strCabLav");
		        strCCLav         = StringUtils.getAttributeStrNotNull(serviceRequest, "strCCLav");
		    }
		    		
%>
<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<%@ include file="CommonScript.inc" %>
<SCRIPT language="JavaScript" src=" ../../js/layers.js"></SCRIPT>

<af:linkScript path="../../js/" /> 

<title>Valore ISEE</title>

<style>
ul {
	display: inline-block  !important;    
}
</style>

<script language="Javascript">
     window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
<script language="Javascript">
function apriStoricoMod() {
	
	var url = "AdapterHTTP?PAGE=AsValoreISEEStoricoPage";
	url += "&cdnLavoratore=<%=cdnLavoratore%>";
	url += "&prgValoreIsee=<%=Utils.notNull(strPrgValoreIsee)%>";
	url += "&CDNFUNZIONE=<%=_cdnFunz%>";

	window.open(url, "", 'toolbar=NO,statusbar=YES,width=950,height=500,top=100,left=35,scrollbars=YES,resizable=YES');
}
function apriScartiIsee() {
	
	var url = "AdapterHTTP?PAGE=StoricoScartiIseePage";
	url += "&cdnLavoratore=<%=cdnLavoratore%>";
	url += "&CDNFUNZIONE=<%=_cdnFunz%>";

	window.open(url, "", 'toolbar=NO,statusbar=YES,width=950,height=500,top=100,left=35,scrollbars=YES,resizable=YES');
}
<%//Genera il Javascript che si occuperà di inserire i links nel footer
attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);%>
</script>
<script>
	function richiestaConfermaMofificaDataFineValidita(esisteDatafinevaliditaVuota, esisteRecordDTFinePostNuovoInizio ) {
		if (esisteDatafinevaliditaVuota=="TRUE") {
			if (confirm("ATTENZIONE: esiste già un valore ISEE senza data fine.\nVuoi procedere?")) {
				document.Frm1.SKIP_CONTROL_DATA_FINE.value = "TRUE";
				doFormSubmit(document.Frm1);
			}    		
		}
		else {
			if (esisteRecordDTFinePostNuovoInizio=="TRUE") {
				if (confirm("ATTENZIONE:  esiste un record per cui la data fine è successiva alla data inizio del nuovo valore ISEE.\nVuoi procedere?")) {
					document.Frm1.SKIP_CONTROL_DATA_FINE.value = "TRUE";
					doFormSubmit(document.Frm1);
				}   
			}
		}
	}

	function richiestaAperturaLayer(nuovo, nomeDiv, url) {
		if (document.Frm1.inizio.value != document.Frm1.inizioHidden.value ||
			document.Frm1.fine.value != document.Frm1.fineHidden.value ||
			document.Frm1.numvaloreisee.value != document.Frm1.numvaloreiseeHidden.value ||
			document.Frm1.numanno.value != document.Frm1.numannoHidden.value) {
			apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_aggiorna%>');
		}
		else {
			document.Frm1.STORICIZZAZIONE_ISEE.value = "FALSE";
			doFormSubmit(document.Frm1);
		}
	}
	//

    function chiudiLayer() {
	
	  ok=true;
	  if (flagChanged) {
	     if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
	         ok=false;
	     } else { 
	         flagChanged = false;
	     }	     
	  }
	  if (ok) {
	     ChiudiDivLayer('divLayerDett');
	  }
	}
	
    function controllaDate()
    {	 
	  
	  var d     = new Date();
	  var gg    = d.getDate();
	  var m     = d.getMonth() + 1;
	  var aa    = d.getYear();
	  	  
	  if (aa<=99)
	       aa = "19"+ aa;	     
	  if ((aa>99) && (aa<2000))
	  	aa +=1900;
	  if (m<10)
	  	m = "0" + m;
	  if (gg<10){	  	
	  	gg = "0" + gg;	  	
	  }
	  var oggi = gg + '/'+ m + '/'+aa;	  
		

      if(!controllaFunzTL()) return false;  
      
      var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
      var di = document.Frm1.inizio.value;
      var df = document.Frm1.fine.value;
      var diP = document.Frm1.inizioPrec.value;
      var dfP = document.Frm1.finePrec.value; 
      var ok1, ok2, ok3, ok4, ok5;
      var s, g, m, a;
      var dataI, dataF, dataIp, dataFp ,dataOdierna;
        		
  	
      var matchArray = di.match(datePat);
      if(matchArray == null) { 
        ok1 = false; 
        dataI = "";
      } else { 
        ok1 = true; 
        s = matchArray[2];
        var tmp1 = di.split(s);
        g = tmp1[0];
        m = tmp1[1];
        a = tmp1[2];
        dataI = parseInt(a + m + g, 10);
      }

      matchArray = df.match(datePat);
      if(matchArray == null) { 
        ok2 = false; 
        dataF = "";
      } else { 
        ok2 = true;
        s = matchArray[2];
        var tmp2 = df.split(s);
        g = tmp2[0];
        m = tmp2[1];
        a = tmp2[2];
        dataF = parseInt(a + m + g, 10);
      }
      
      matchArray = diP.match(datePat);
      if(matchArray == null) { 
        ok3 = false; 
        dataIp = "";
      } else { 
        ok3 = true;
        s = matchArray[2];
        var tmp2 = diP.split(s);
        g = tmp2[0];
        m = tmp2[1];
        a = tmp2[2];
        dataIp = parseInt(a + m + g, 10);
      }
  
      matchArray = dfP.match(datePat);
      if(matchArray == null) { 
        ok4 = false; 
        dataFp = "";
      } else { 
        ok4 = true;
        s = matchArray[2];
        var tmp2 = dfP.split(s);
        g = tmp2[0];
        m = tmp2[1];
        a = tmp2[2];
        dataFp = parseInt(a + m + g, 10);
      }
      
      matchArray = oggi.match(datePat);
      if(matchArray == null) { 
        ok5 = false; 
        dataOdierna = "";
      } else { 
        ok5 = true;
        s = matchArray[2];
        var tmp2 = oggi.split(s);
        g = tmp2[0];
        m = tmp2[1];
        a = tmp2[2];
        dataOdierna = parseInt(a + m + g, 10);
      }      
      
      var esito = false;
       
      if(ok1) {
        if((dataI <= dataF) || ( dataF == "")) { 
		  if(ok3) {
	        if((dataIp != "" && dataFp == "") || (dataFp > dataI)) {
		        if(confirm ("Esiste già un valore ISEE nel periodo indicato."+"\n"+"Inserire il nuovo valore?")) {
				  esito = true;
				} else {
				  esito = false;
				}
			} else { 
			  esito = true;
			}      
	      } else {
	        esito = true;
	      }
        } else { 
          alert("La data di inizio validità deve essere minore o uguale alla data di fine validità."); 
          esito = false;
        }
      } else {
        esito = false;      
      }
      
     if(ok5){
    	 if((dataI > dataOdierna)) {
      		alert("La data di inizio validità deve essere minore o uguale alla data odierna.");
     		esito = false;
    	 }
     	       	
     }     
        
     <% if (configIBAN.equals("1")) { %>
 	 if (esito == true){
 		if (!ControllaDati()) esito = false;
 	 } 
 	 <% } %> 

 	 if (esito == true){
 	    doFormSubmit(document.Frm1);
 	 } else {
 	    undoSubmit();
 	    return esito;
 	 }	
      	  
    }
    
    function cancellaVal() {
    	
    	if (isInSubmit()) return;
    	
		var ok = confirm("Sicuri di voler rimuovere il valore ISEE?");
		
		if (ok == true){
			document.Frm1.MODULE.value = "M_AS_DelValISEE";
 	   		doFormSubmit(document.Frm1);
 	 	} 
		
    }

    <% if (configIBAN.equals("1")) { %>
    function checkSoloLettere(obj) {
        var regexp = /^[a-zA-Z]+$/;
        if (!regexp.test(obj.value))
        {
            alert("Nel campo " + obj.title + " sono consentiti solo caratteri alfabetici");
            return false;
        }
        else return true;
    }
    
    function ControllaDati(){

    	if (document.Frm1.strIbanNazione.value != '') {
    		if (!checkSoloLettere(document.Frm1.strIbanNazione)) {
    			return false;
    		}
    	}

    	if (document.Frm1.strCinLav.value != '') {
    		if (!checkSoloLettere(document.Frm1.strCinLav)) {
    			return false;
    		}
    	}

    	return true;
    }

    function upperInnerTextCampo(obj) {
    	obj.value=obj.value.toUpperCase();
    }   
    <% } %> 
    
    
</script>

</head>
<body class="gestione" onload="rinfresca();richiestaConfermaMofificaDataFineValidita('<%=esisteDatafinevaliditaVuota%>','<%=esisteRecordDTFinePostNuovoInizio%>' );">

<%if (testata != null)
	testata.show(out);
  if (l != null) {
  	if (l.getSize() > 0) {
		l.show(out);
  	} else {%>
<p class="titolo">Valore Isee</p>
<%}
}
%>
<br>
<font color="red"><af:showErrors/></font>
<font color="green"> 
 <af:showMessages prefix="M_AS_UpdValISEE"/>
 <af:showMessages prefix="M_AS_DelValISEE"/>
 <af:showMessages prefix="M_AS_CalcoloPunteggio"/>
</font>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit='<%=((configIBAN.equals("1"))?"ControllaDati()":"")%>'>    

	<input type="hidden" name="CDNFUNZIONE"      value="<%=cdnFunzione%>"/>
	<input type="hidden" name="PAGE"             value="ASValoreIseePage"/>
	<input type="hidden" name="MODULE"           value=""/>
	<input type="hidden" name="CDNCOMPONENTE"    value="<%=Utils.notNull(cdncomponente)%>"/>
	<input type="hidden" name="cdnLavoratore"    value="<%=Utils.notNull(cdnLavoratore)%>"/>
	<input type="hidden" name="prgValoreIsee"    value="<%=strPrgValoreIsee%>"/>
	<input type="hidden" name="inizioPrec"       value="<%=confDtIn%>"/>
	<input type="hidden" name="finePrec"         value="<%=confDtFin%>"/>
	<input type="hidden" name="NUMKLOVALOREISEE" value="<%=numKloValoreIsee%>"/>
	<input type="hidden" name="inizioHidden" value="<%=datinizioval%>"/>
	<input type="hidden" name="fineHidden" value="<%=datfineval%>"/>
	<input type="hidden" name="numvaloreiseeHidden" value="<%=strNumValoreIsee%>"/>
	<input type="hidden" name="numannoHidden" value="<%=strNumAnno%>"/>
	<input type="hidden" name="STORICIZZAZIONE_ISEE" value="TRUE"/>
	<input type="hidden" name="ESISTE_DATAFINEVALIDITA_VUOTA" value="<%=esisteDatafinevaliditaVuota%>"/>
	<input type="hidden" name="ESISTE_RECORD_DTFINE_POST_NUOVOINIZIO" value="<%=esisteRecordDTFinePostNuovoInizio%>"/>
	<input type="hidden" name="DO_UPDATE_2_DTFINE_PRGVALOREISEE" value="<%= do_update_2_dtfine_prgvaloreisee %>"/>
	<input type="hidden" name="SKIP_CONTROL_DATA_FINE" value="<%=skipcontroldatafine%>"/>

	<p align="center">  
	     
	  <div align="center" style=" display:<%=apriDivLista%>;">    	      
	    <af:list moduleName="M_AS_ListaValISEE" skipNavigationButton="0"  
	             canInsert="<%=canInsert ? \"1\" : \"0\"%>" />  
	             
		<%
		if (canInsert){
		%>
		<table>
			<tr>
				<td align="center" colspan="2">
					<input type="submit" class="pulsante" value="Nuovo Valore ISEE" name="btnNuovo"
					 onClick="openPage('ASNuovoIseePage','&cdnLavoratore=<%=cdnLavoratore%>&prgValoreISEE=<%=strPrgValoreIsee%>&CDNFUNZIONE=<%=_cdnFunz%>')">
				</td>
			</tr>		
		</table>
	<% }	%>	
		<%	if (canASOnline){
		%>
		<table>
			<tr>
				<td align="center" colspan="2">
					<input type="button" class="pulsante" value="Storico scarti istanze" name="btnStoricoScartiIsee"
					 onClick="apriScartiIsee()">
				</td>
			</tr>		
		</table>
	<% }	%>	
	  </div>                

	  <div id="divAggiorna" name="divAggiorna" style=" display:<%=apriDivDett%>;">

         <%out.print(htmlStreamTop);%> 
         
	     <%@  include file="ASValIsee.inc" %> 
		
		<%
		if ( ! canInsert){ 
		%>		
			<table >
				<tr>
					<td>&nbsp;
					</td>
				</tr>		
				<tr>
					<%
		    		if (canModify) {
					%>			
						<td align="center">
						    <input type="button" class="pulsanti" onClick="richiestaAperturaLayer(<%=nuovo%>,'divLayerDett','<%=url_aggiorna%>')" value="Aggiorna"/> 
						</td>			
					<%
					}
					%>
				</tr>
			</table>
		<%
		}
		%> 

		<table>
			<tr>
				<td align="center">
					<input class="pulsante" type="button" name="annulla" value="Chiudi"
	            	 onClick="openPage('ASValoreIseePage','&cdnLavoratore=<%=cdnLavoratore%>&prgValoreIsee=<%=Utils.notNull(strPrgValoreIsee)%>&CDNFUNZIONE=<%=_cdnFunz%>')">
				</td>
			</tr>
		</table>	
	
		<table align="right">
			<tr>
				<td colspan="4" align="right" >
					<input type="button" class="pulsante<%=((btnStoricoModifiche)?"":"Disabled")%>" value="Storico modifiche" 
					name="btnInfStoria" <%=(!btnStoricoModifiche)?"disabled=\"True\"":""%> onClick="apriStoricoMod()">
				</td>
			</tr>
			
			<tr>			
				<td colspan="4"> &nbsp;
				</td>
			</tr>			
		</table>
			
	  </div>
	  
  <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
     style="position:absolute; width:90%; left:50; top:350px; z-index:6; display:<%=apriDivConfAgg%>;">

  <!-- Stondature ELEMENTO TOP -->
  <%out.print(divStreamTop);%>

    <table width="100%">
      <tr>
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">
          Conferma 
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>

    <table width="100%" align="center">
		<tr><td><br/></td></tr>
		<tr>
		 <td colspan="4" align="center">
			 <strong>E' stata modificata almeno una delle seguenti informazioni</strong>
		 </td> 
		</tr>
		<tr>
		 <td colspan="4"  align="center">
			<ul   >
			  <li>Data inizio validit&agrave;</li>
			  <li>Data fine validit&agrave;</li>
			  <li>Valore Isee</li>
			  <li>Anno di riferimento del reddito</li>
			</ul>		 
		 </td> 
		</tr>
		<tr>
		 <td colspan="4" align="center">
			 <strong>La modifica comporta la storicizzazione dei precedenti dati e necessita della specifica sul motivo della modifica</strong>
		 </td> 
		</tr>	
		<tr><td><br/></td></tr>		
		<tr>
		 <td colspan="4" align="center">
		 Motivo modifica 
		 <af:comboBox name="codmonomotivomodifica" title="Motivo modifica" multiple="false" required="true" moduleName="M_AS_ComboMotivoModifica"
		       addBlank="true" selectedValue="<%=strCodmonomotivomodifica%>" />
		 </td> 
		</tr>	
    </table>
    
	<table class="main" width="100%">
	  <tr><td>&nbsp;<br/></td></tr>
	  <tr>
	    <td colspan="2" align="center">

			<input type="submit" class="pulsante" value="Conferma" name="btnAggiorna"/>
			<input type="hidden" name="graduatoria" value="AS"/>		      
		 </td>
	   </tr>
	   <tr>
		  <td>
		      <input type="button" class="pulsanti" name="chiudi" value="Annulla" onClick="chiudiLayer()">
		  </td>
	   </tr>
	      

	  <tr><td>&nbsp;</td></tr>	  
	</table>


  </div>
	  
  <%out.print(divStreamBottom);%>
  		</p>	
  		
  		
	<%out.print(htmlStreamBottom);
		if (operatoreInfo!=null) {
	%>
  	<center>
  		<table>
  			<tr>
  				<td align="center">
	<%  operatoreInfo.showHTML(out);%>
  				</td>
  			</tr>
  		</table>
  	</center>
	<%}%>
</af:form>
</body>
</html>
