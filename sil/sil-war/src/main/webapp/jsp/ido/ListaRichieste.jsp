<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*,
                 java.util.HashSet,
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*,
                 it.eng.sil.util.Sottosistema"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  
  if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }else{

	  HashSet hsRichieste = new HashSet();
	  String moduloListaRichieste=(serviceResponse.containsAttribute("M_DynRicercaRichieste"))?"M_DynRicercaRichieste":"M_GETLISTARICHIESTEUNITA";
	  PageAttribs attributi = new PageAttribs(user, "IdoListaRichiestePage");
	  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	  String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGAZIENDA");
	  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGUNITA");
%>

<html>
<head>
<title>Lista Richieste</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<SCRIPT language="Javascript" type="text/javascript">
<!--
function go(url, alertFlag) {
// Se la pagina è già in submit, ignoro questo nuovo invio!
if (isInSubmit()) return;

var _url = "AdapterHTTP?" + url;
if (alertFlag == 'TRUE' ) {
if (confirm('Confermi operazione'))
setWindowLocation(_url);
}
else
setWindowLocation(_url);
}
// -->
</SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>

 <script language="Javascript">
  function tornaAllaRicerca()
  {  
     // Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
  
     url="AdapterHTTP?PAGE=IdoRichiestaRicercaPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";

     url += "&prgRichiestaAz="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"prgRichiestaAz")%>";
     url += "&ANNO="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"Anno")%>";

     url += "&datRichiestaDal="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"datRichiestaDal")%>";
     url += "&datRichiestaAl="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"datRichiestaAl")%>";
     url += "&datScadenzaDal="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"datScadenzaDal")%>";
     url += "&datScadenzaAl="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"datScadenzaAl")%>";
     url += "&codTipoAzienda="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAzienda")%>";
     url += "&cf="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"cf")%>";
     url += "&piva="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"piva")%>";
     url += "&RagioneSociale="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"RagioneSociale")%>";
     url += "&Indirizzo="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"Indirizzo")%>";
     url += "&codCom="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCom")%>";
     url += "&codComHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codComHid")%>";
     url += "&desComune="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"desComune")%>";
     url += "&desComuneHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"desComuneHid")%>";
     url += "&codCPI="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCPI")%>";
     url += "&codCPIHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIHid")%>";
     url += "&strCPI="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCPI")%>";
     url += "&strCPIHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCPIHid")%>";
     url += "&codCPIifDOMeqRESHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIifDOMeqRESHid")%>";
     url += "&CODMANSIONE="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CODMANSIONE")%>";
     url += "&CODMANSIONEHid="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CODMANSIONEHid")%>";
     url += "&CODTIPOMANSIONE="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CODTIPOMANSIONE")%>";
     url += "&strTipoMansione="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strTipoMansione")%>";
     url += "&DESCMANSIONE="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"DESCMANSIONE")%>";
     url += "&codMonoStatoRich="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoStatoRich")%>";
     url += "&cdnStatoRich="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"cdnStatoRich")%>";
     url += "&utente="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"utente")%>";
     
     //TODO aggiunto per modulo AS : mettere l'interruttore!
     url +=	"&codEvasione="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codEvasione")%>";
  	 url += "&dataChiam="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataChiam")%>";
	 url +=	"&evas="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"evas")%>";
	 url += "&statoev="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"statoev")%>";
	 url += "&statorich="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"statorich")%>";

	 url += "&codMonoTipoGrad="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoTipoGrad")%>";
     url += "&codMonoCMcategoria="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoCMcategoria")%>";
     url += "&flagCresco="+"<%=StringUtils.getAttributeStrNotNull(serviceRequest,"flagCresco")%>";
     setWindowLocation(url);
  }

;

<% if (StringUtils.isFilled(prgAzienda) && StringUtils.isFilled(prgUnita)) {
  out.print("window.top.menu.caricaMenuAzienda("+_funzione+","+prgAzienda+","+prgUnita+");");
}%>

 </script>

</head>
<body class="gestione" onload="rinfresca();">
<p class="titolo">LISTA RICHIESTE</p>

<%
  	// INIT-PARTE-TEMP
	if (Sottosistema.AS.isOff()) {	
	// END-PARTE-TEMP

	// INIT-PARTE-TEMP
	} else {
			
	// END-PARTE-TEMP 

		  String prgRichiestaAz = StringUtils.getAttributeStrNotNull(serviceRequest,"prgRichiestaAz");
		  String anno = StringUtils.getAttributeStrNotNull(serviceRequest,"anno");
		  String datRichiestaDal = StringUtils.getAttributeStrNotNull(serviceRequest,"datRichiestaDal");
		  String datRichiestaAl  = StringUtils.getAttributeStrNotNull(serviceRequest,"datRichiestaAl");
		  String datScadenzaDal = StringUtils.getAttributeStrNotNull(serviceRequest,"datScadenzaDal");
		  String datScadenzaAl = StringUtils.getAttributeStrNotNull(serviceRequest,"datScadenzaAl");
		  String codTipoAzienda= StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAzienda");
		  String cf= StringUtils.getAttributeStrNotNull(serviceRequest,"cf");
		  String piva = StringUtils.getAttributeStrNotNull(serviceRequest,"piva");
		  String RagioneSociale = StringUtils.getAttributeStrNotNull(serviceRequest,"RagioneSociale");
		  String Indirizzo = StringUtils.getAttributeStrNotNull(serviceRequest,"Indirizzo");
		  String codCom = StringUtils.getAttributeStrNotNull(serviceRequest,"codCom");
		  String codComHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codComHid");
		  String desComune = StringUtils.getAttributeStrNotNull(serviceRequest,"desComune");
		  String desComuneHid = StringUtils.getAttributeStrNotNull(serviceRequest,"desComuneHid");
		  String codCPI = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPI");
		  String codCPIHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIHid");
		  String strCPI = StringUtils.getAttributeStrNotNull(serviceRequest,"strCPI");
		  String strCPIHid = StringUtils.getAttributeStrNotNull(serviceRequest,"strCPIHid");
		  String codCPIifDOMeqRESHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIifDOMeqRESHid");
		  String CODMANSIONE = StringUtils.getAttributeStrNotNull(serviceRequest,"CODMANSIONE");
		  String CODMANSIONEHid = StringUtils.getAttributeStrNotNull(serviceRequest,"CODMANSIONEHid");
		  String CODTIPOMANSIONE = StringUtils.getAttributeStrNotNull(serviceRequest,"CODTIPOMANSIONE");
		  String strTipoMansione = StringUtils.getAttributeStrNotNull(serviceRequest,"strTipoMansione");
		  String DESCMANSIONE = StringUtils.getAttributeStrNotNull(serviceRequest,"DESCMANSIONE");
		  String codMonoStatoRich = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoStatoRich");
		  String cdnStatoRich = StringUtils.getAttributeStrNotNull(serviceRequest,"cdnStatoRich");
		  	  
		  //parametri per la sintesi dei filtri usati nella ricerca per la lista richieste dell'Art 16	
		  String codEvasione = StringUtils.getAttributeStrNotNull(serviceRequest,"codEvasione");
  		  String dataChiam = StringUtils.getAttributeStrNotNull(serviceRequest,"dataChiam");
  		  String evas = StringUtils.getAttributeStrNotNull(serviceRequest,"evas");
		  String statoev = StringUtils.getAttributeStrNotNull(serviceRequest,"statoev");
		  String statorich = StringUtils.getAttributeStrNotNull(serviceRequest,"statorich");
		  
		  String codMonoTipoGrad = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoTipoGrad");
		  String codMonoCMcategoria = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoCMcategoria");
		  String flagCresco  = (String) serviceRequest.getAttribute("flagCresco");
		  
		  String attr   = null;
		  String valore = null;
		  String txtOut = "";
		
		       attr= "codMonoTipoGrad";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals("")){
		       		if(valore.equals("D")){
		       			valore = "Avviamento Numerico art. 8";
		       		}else if(valore.equals("A")){
		       			valore = "Avviamento Numerico art. 18";
		       		}else if(valore.equals("G")){
		       			valore = "Graduatoria art. 1";
		       		}
		       		txtOut += "Tipo CM <strong>"+ valore +"</strong>; ";
		       }
		       attr= "prgRichiestaAz";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "Numero richiesta <strong>"+ valore +"</strong>; ";
		       }
		       attr= "anno";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "anno <strong>"+ valore +"</strong>; ";
		       }
		       attr= "datRichiestaDal";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "Dal giorno <strong>"+ valore +"</strong>; ";
		       }
		       attr= "datRichiestaAl";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "Al giorno <strong>"+ valore +"</strong>; ";
		       } 
		       attr= "datScadenzaDal";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "  Data scadenza dal giorno <strong>"+ valore +"</strong>; ";
		       }
		       attr= "datScadenzaAl";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "Al giorno <strong>"+ valore +"</strong>; ";
		       }
		       attr= "codTipoAzienda";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "Tipo Azienda <strong>"+ valore +"</strong>; ";
		       }
		       attr= "cf";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "Codice Fiscale <strong>"+ valore +"</strong>; ";
		       }
		       attr= "piva";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "Partita IVA <strong>"+ valore +"</strong>; ";
		       }
		       attr= "RagioneSociale";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "Ragione Sociale <strong>"+ valore +"</strong>; ";
		       } 
		       attr= "Indirizzo";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "Indirizzo <strong>"+ valore +"</strong>; ";
		       }
		       attr= "codCom";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "Comune <strong>"+ valore +"</strong>; ";
		       }
		       //attr= "codComHid";
		       //valore = (String) serviceRequest.getAttribute(attr);
		       //if(valore != null && !valore.equals(""))
		       //{txtOut += " Comune <strong>" + valore + "</strong>; ";
		       //}
		       attr= "desComune";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += " Comune <strong>" + valore + "</strong>; ";
		       }
		       //attr= "desComuneHid";
		       //valore = (String) serviceRequest.getAttribute(attr);
		       //if(valore != null && !valore.equals(""))
		       //{txtOut += " Comune <strong>" + valore + "</strong>; ";
		       //}
		       attr= "codCPI";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += " Cpi comp.: <strong>" + valore + "</strong>; ";
		       }
		       //attr= "codCPIHid";
		       //valore = (String) serviceRequest.getAttribute(attr);
		       //if(valore != null && !valore.equals(""))
		       //{txtOut += " Cpi comp.: <strong>" + valore + "</strong>; ";
		       //}
		       attr= "strCPI";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += " Cpi comp.: <strong>" + valore + "</strong>; ";
		       }
		       //attr= "strCPIHid";
		       //valore = (String) serviceRequest.getAttribute(attr);
		       //if(valore != null && !valore.equals(""))
		       //{txtOut += " Cpi comp.: <strong>" + valore + "</strong>; ";
		       //}
		       attr= "codCPIifDOMeqRESHid";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += " Cpi comp.: <strong>" + valore + "</strong>; ";
		       }
		       attr= "CODMANSIONE";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += "Codice mansione <strong>" + valore + "</strong>; ";
		       }
		       attr= "CODMANSIONEHid";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += " Codice mansione <strong>" + valore + "</strong>; ";
		       }
		       attr= "strTipoMansione";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += " Codice mansione <strong>" + valore + "</strong>; ";
		       }
		       attr= "DESCMANSIONE";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += " Mansione <strong>" + valore + "</strong>; ";
		       }
		       
		       attr= "dataChiam";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals(""))
		       {txtOut += " Data chiamata <strong>" + valore + "</strong>; ";
		       }
		  	//ricavo le descrizioni dei codici per i parametri usati nella ricerca     
		       attr= "statorich";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals("") && !codMonoStatoRich.equals("") )
		       {txtOut += " Stato richiesta <strong>" + valore + "</strong>; ";
		       }
		       attr= "statoev";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals("") && !cdnStatoRich.equals(""))
		       {txtOut += " Stato di evasione <strong>" + valore + "</strong>; ";
		       }		       
		       attr= "evas";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals("") && !codEvasione.equals("") )
		       {txtOut += " Modalità di evasione <strong>" + valore + "</strong>; ";
		       }
		       attr= "codMonoCMcategoria";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if(valore != null && !valore.equals("") && !codMonoCMcategoria.equals("") )
		       {
		       	String strVal = "";
		       	if( valore.equals("D") ) strVal = "Disabili";
		       	else if( valore.equals("A") ) strVal = "Categoria protetta ex. Art. 18";
		       			else if( valore.equals("E") ) strVal = "Entrambi";
		       	
		       	txtOut += " Categoria CM <strong>" + strVal + "</strong>; ";
		       }
		       attr= "flagCresco";
		       valore = (String) serviceRequest.getAttribute(attr);
		       if (valore != null && !valore.equals("")
		   			&& !valore.equalsIgnoreCase("N")) {
		   		txtOut += "Cresco <strong>Sì</strong>;";
		   	}
		       
%>		
		      
		<p align="center">
		<%if(txtOut.length() > 0)
		  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
		    <table cellpadding="2" cellspacing="10" border="0" width="100%">
		     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
		      <%out.print(txtOut);%>
		     </td></tr>
		    </table>
		<%}%>
<% 
	// INIT-PARTE-TEMP
	}
   	// END-PARTE-TEMP
%>

<af:form dontValidate="true">
<af:list moduleName="<%=moduloListaRichieste%>" configProviderClass="it.eng.sil.module.ido.DynamicRicRichiesteConfig" />
<center><input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/></center>
<br/>
<br/>
<!--a href="AdapterHTTP?PAGE=StampeRosaCandidatiIDOPage&prgRichiestaAz=521&prgAzienda=26&prgUnita=1&prgRosa=25">Stampe rosa dei candidati (prgRosa=25)</a>
<br/>
<br/>
<a href="AdapterHTTP?PAGE=StampeRosaCandidatiIDOPage&prgRichiestaAz=521&prgAzienda=26&prgUnita=1&prgRosa=55">Stampe rosa dei candidati (prgRosa=55)</a>
<br>
<br>
<a href="AdapterHTTP?PAGE=FiltriPerRosaIDOPage&prgRosa=25">Filtri per rosa (prgRosa=25)</a>
<br>
<a href="AdapterHTTP?PAGE=FiltriPerRosaIDOPage&prgRosa=53">Filtri per rosa (prgRosa=53)</a>
<br>
<a href="AdapterHTTP?PAGE=FiltriPerRosaIDOPage&prgRosa=54">Filtri per rosa (prgRosa=54)</a>
<br>
<a href="AdapterHTTP?PAGE=FiltriPerRosaIDOPage&prgRosa=55">Filtri per rosa (prgRosa=55)</a-->

</af:form>
</body>
</html>
<%}%>