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
                  java.text.*"
%> 


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%			
			String _page 			= 	"ASValoreIseePage";
			int _cdnFunz 			= 	Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
			String cdnFunzione 		= 	(String) serviceRequest.getAttribute("CDNFUNZIONE");
			String cdncomponente 	= 	(String) serviceRequest.getAttribute("CDNCOMPONENTE");
			String cdnLavoratore 	= 	(String) serviceRequest.getAttribute("cdnLavoratore");
			String datfineval 		= 	"";
			String datinizioval  	= 	"";
			BigDecimal numvaloreisee= 	null;
			BigDecimal puntValIsee	=	null;  
			BigDecimal numanno		= 	null;
			String strIbanNazione 	= 	"";
			String strIbanControllo = 	"";
			String strCinLav 		= 	"";
			String strAbiLav 		= 	"";
			String strCabLav 		= 	"";
			String strCCLav 		= 	"";			
			String strNote 			= 	"";
			String confDtIn			= 	"";
			String confDtFin		=	"";
			boolean btnInserisci 	= false;
			//boolean btnInfStoriche 	= false;
			boolean btnAggiorna		= false;
			boolean btnNuovo		= false;	
			boolean btnCancella		= false;
			
			
			BigDecimal cdnUtIns 	= 	null;
			String dtmIns 			= 	"";
			BigDecimal cdnUtMod 	= 	null;
			String dtmMod 			= 	"";	
			
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
			String MODULE_NAME 		= 	"M_AS_GetValISEE";
			
			PageAttribs attributi 	= 	new PageAttribs(user, _page);
			
			operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);		  	
			
			BigDecimal prgValore 	= null;
			Vector vett = serviceResponse.getAttributeAsVector("M_AS_GetValISEE.ROWS.ROW");
			
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
			
			if(!nuovo) {
				readOnlyStr = true;
				updDate = true;
			}
			else { 
				readOnlyStr = false;
				updDate = false;
			}
			
			btnInserisci 			= 	canInsert 	&& btnInserisci;
			//btnAggiorna				=	canInsert	&& canModify && btnAggiorna;
			btnNuovo				= 	canInsert 	&& nuovo && btnNuovo;
			btnCancella				=	canInsert 	&& cancella && btnCancella;
			//btnInfStoriche 			= 	infStorButt && btnInfStoriche;
			btnInserisci = true;
			String strPrgValoreIsee = "";
			
			String configIBAN = serviceResponse.containsAttribute("M_GetConfigIBAN.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigIBAN.ROWS.ROW.NUM").toString():"0";
			
			String htmlStreamTop 	= 	StyleUtils.roundTopTable(!readOnlyStr);
			String htmlStreamBottom = 	StyleUtils.roundBottomTable(!readOnlyStr);
%>
<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<%@ include file="CommonScript.inc" %>

<af:linkScript path="../../js/" />

<title>Nuovo Valore ISEE</title>
<script language="Javascript">
     window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
<script language="Javascript">

var vDataInizioVal = new Array();
var vDataFineVal = new Array();

<%
  int contatoreRiga = 0;
  if (vett != null && vett.size()>0) {
	  for (int j = 0; j < vett.size(); j++) {
	  SourceBean rowIsee = (SourceBean)vett.get(j);
	  String dataInizioValid = (String)rowIsee.getAttribute("DATINIZIOVAL");
	  String dataFineValid = (String)rowIsee.getAttribute("DATFINEVAL");
	  if (dataFineValid == null) {
		  dataFineValid = ""; 
	  }
	  %>        
	  vDataInizioVal[<%=contatoreRiga%>] = '<%=dataInizioValid%>';
	  vDataFineVal[<%=contatoreRiga%>] = '<%=dataFineValid%>';
	  <%
	  contatoreRiga = contatoreRiga + 1;
  }
}

//Genera il Javascript che si occuperà di inserire i links nel footer
attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);%>

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
	        	esito = true;
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

      var msgForzatura1 = false;
      var msgForzatura2 = false;
      
      if ( dataF == ""){
	      if (esito == true){
	          for (var jDate = 0; jDate < vDataFineVal.length; jDate++) {
					if( vDataFineVal[jDate] == '') {
						if (!msgForzatura1 &&
							!confirm("ATTENZIONE: esiste già un valore ISEE senza data fine.\nVuoi procedere?")) {
							esito = false;
						}
						else {
							msgForzatura1 = true;
						}
					}
					else {
						if (compareDate(vDataInizioVal[jDate], di) < 0 && compareDate(vDataFineVal[jDate], di) > 0) {
							if (!msgForzatura2 && 
								!confirm("ATTENZIONE:  esiste un record per cui la data fine è successiva alla data inizio del nuovo valore ISEE.\nVuoi procedere?")) {
								esito = false;
							}
							else {
								msgForzatura2 = true;
							}
						}
					}
	          }
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
<body class="gestione" onload="rinfresca();">

<font color="red"><af:showErrors/></font>
<font color="green"> 

 <af:showMessages prefix="M_AS_CalcoloPunteggio"/>
</font>

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

<%out.print(htmlStreamTop);%> 
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
	<%@ include file="ASNuovoValIsee.inc" %>  

	
	<input type="hidden" name="CDNFUNZIONE" 	value=	"<%=cdnFunzione%>"/>
	<input type="hidden" name="PAGE" 			value=	"ASValoreIseePage"/>
	<input type="hidden" name="CDNCOMPONENTE" 	value=	"<%=Utils.notNull(cdncomponente)%>"/>
	<input type="hidden" name="cdnLavoratore" 	value=	"<%=Utils.notNull(cdnLavoratore)%>"/>
	<input type="hidden" name="prgValoreIsee" 	value=	"<%=strPrgValoreIsee%>"/>
	<input type="hidden" name="inizioPrec" 		value=	"<%=confDtIn%>"/>
	<input type="hidden" name="finePrec" 		value=	"<%=confDtFin%>"/>
	
	<table>
		<tr>
			<td align="center">
				
			</td>
		</tr>
	</table>
	<%
	if (nuovo) {
	%>
		<table>
			<tr>
				<td align="center">
					<input type="button" class="pulsante" value="Inserisci"  onClick="controllaDate()">
					<input type="hidden" name="btnInserisci" value=	"Inserisci"/>
					<input type="hidden" name="graduatoria" value="AS"/>
				</td>
			</tr>
		</table>
	<%
	}
	%>
	<table>
		<tr>
			<td align="center">
				<input class="pulsante" type="button" name="annulla" value="Chiudi senza inserire"
            	 onClick="openPage('ASValoreIseePage','&cdnLavoratore=<%=cdnLavoratore%>&prgValoreIsee=<%=Utils.notNull(strPrgValoreIsee)%>&CDNFUNZIONE=<%=_cdnFunz%>')">
			</td>
		</tr>
	</table>	

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
