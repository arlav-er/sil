<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String queryString = (String)requestContainer.getAttribute("HTTP_REQUEST_QUERY_STRING");
  boolean flag_insert = false; //serviceRequest.containsAttribute("inserisci");

  BigDecimal cdnUtIns = null;
  String dtmIns   ="";
  BigDecimal cdnUtMod = null;
  String dtmMod   ="";
  BigDecimal numklo=null;
  Testata operatoreInfo=null;

  String cdnLavoratore = ""; // (String) serviceRequest.getAttribute("cdnLavoratore");
  String prgRosa       = (String) serviceRequest.getAttribute("prgRosa");
  String prgRichiestaAz= (String) serviceRequest.getAttribute("prgRichiestaAz");
  String prgAzienda    = ""; // (String) serviceRequest.getAttribute("prgAzienda");
  String prgUnita      = ""; // (String) serviceRequest.getAttribute("prgUnita");
  String prgOriginale = "";
  
  if (serviceRequest.containsAttribute("_CDNLAVORATORE_")) {
  	cdnLavoratore = (String) serviceRequest.getAttribute("_CDNLAVORATORE_");
  } else {
  	cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");  
  }
  
  if (serviceRequest.containsAttribute("_PRGAZIENDA_")) {
  	prgAzienda = (String) serviceRequest.getAttribute("_PRGAZIENDA_");
  } else {
  	prgAzienda = (String) serviceRequest.getAttribute("PRGAZIENDA");  
  }
  if (serviceRequest.containsAttribute("_PRGUNITA_")) {
  	prgUnita = (String) serviceRequest.getAttribute("_PRGUNITA_");
  } else {
  	prgUnita = (String) serviceRequest.getAttribute("PRGUNITA");  
  }
  
  String pageBack      = (String) serviceRequest.getAttribute("RET_PAGE");
  //if(pageBack==null) pageBack = "pageBack_e_nullo";
  if(pageBack==null) pageBack = "MatchDettRosaPage";

  String _page = (String) serviceRequest.getAttribute("PAGE");
  int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean readOnly = false;//!attributi.containsButton("AGGIORNA"); 
  String  readOnlyStr = readOnly ? "true" : "false";
  boolean canInsert   = true; // attributi.containsButton("INSERISCI");
  boolean canAggiorna = true; // attributi.containsButton("AGGIORNA");
  boolean canModify = false;
  if (canAggiorna || canInsert) {
    canModify=true;  
  }

  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  SourceBean row = null;
  
  String datContatto  = "";
  String codEsitoAz   = ""; 
  String motivazione  = "";
  String codEsitoCand = "";
  String referente    = "";
  String tel          = "";
  String fax          = "";
  String email         = "";

  row= (SourceBean) serviceResponse.getAttribute("M_GetEsitoMatchIDO.ROWS.ROW");
  if( row != null ) 
  { datContatto = StringUtils.getAttributeStrNotNull(row,"DATCONTATTO");
    codEsitoAz  = StringUtils.getAttributeStrNotNull(row,"CODESITODAAZIENDA");
    motivazione = StringUtils.getAttributeStrNotNull(row,"STRMOTIVAZIONE");
    codEsitoCand= StringUtils.getAttributeStrNotNull(row,"CODESITODACANDIDATO");
    referente   = StringUtils.getAttributeStrNotNull(row,"STRREFERENTE");
    tel         = StringUtils.getAttributeStrNotNull(row,"STRTEL");
    fax         = StringUtils.getAttributeStrNotNull(row,"STRFAX");
    email       = StringUtils.getAttributeStrNotNull(row,"STREMAIL");
    cdnUtIns    = (BigDecimal) row.getAttribute("CDNUTINS");
    cdnUtMod    = (BigDecimal) row.getAttribute("CDNUTMOD");
    dtmIns      = StringUtils.getAttributeStrNotNull(row,"DTMINS");
    dtmMod      = StringUtils.getAttributeStrNotNull(row,"DTMMOD");        

    operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
  }
  else flag_insert = true;
   
   
  SourceBean rigaRichOriginale = (SourceBean) serviceResponse.getAttribute("M_GET_PRGORIGINALE.ROW");
    if(rigaRichOriginale != null) {
        prgOriginale = rigaRichOriginale.getAttribute("PRGORIGINALE").toString();
    }
  
//------- Sezione utilizzata per "costruire" le infor. di testata della rosa
SourceBean infoRosa    = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOROSA.ROW");

String numRichiesta    = StringUtils.getAttributeStrNotNull(infoRosa, "NUMRICHIESTA");
String numRichiestaOrig = StringUtils.getAttributeStrNotNull(infoRosa, "NUMRICHIESTAORIG");
String numAnno         = StringUtils.getAttributeStrNotNull(infoRosa, "NUMANNO");
String tipoIncrocio    = StringUtils.getAttributeStrNotNull(infoRosa, "TIPOINCROCIO");
String prgTipoIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "PRGTIPOINCROCIO");
String tipoRosa        = StringUtils.getAttributeStrNotNull(infoRosa, "TIPOROSA");

String prgIncrocio     = StringUtils.getAttributeStrNotNull(infoRosa, "PRGINCROCIO");

String prgAlternativa  = StringUtils.getAttributeStrNotNull(infoRosa, "PRGALTERNATIVA");
String strAlternativa  = "";
if(!prgAlternativa.equals("")) { strAlternativa = "Profilo n. " + prgAlternativa; }

String utMod          = StringUtils.getAttributeStrNotNull(infoRosa, "CDNUTMOD");
String ultimaModifica = StringUtils.getAttributeStrNotNull(infoRosa, "ULTIMAMODIFICA");
String utAttivo       = Integer.toString(user.getCodut());

String prgOrig = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGORIGINALE");
//--------------------------------------------------------------------------------

String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String listPage = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");
%>

<%
// POPUP EVIDENZE
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
if(_cdnFunz>0) { _fun = _cdnFunz; }
EvidenzePopUp jsEvid = null;
boolean bevid = attributi.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid) {
	jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
}	
%> 

<html>

<head>
<title>Stato Elaborazione Richiesta</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>

<script language="JavaScript">

<%//Genera il Javascript che si occuperà di inserire i links nel footer
  attributi.showHyperLinks(out, requestContainer,responseContainer,"");
%>

// Rilevazione Modifiche da parte dell'utente
var flagChanged = false;
        
function fieldChanged() {
<% if (canModify){ %> 
  flagChanged = true;
<%}%> 
}

var back = false;

function cameBack(backPage)
{ 
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  prepareSubmit(); 
  if (flagChanged==true) {
	if (! confirm("I dati sono cambiati.\r\nProcedere lo stesso ?")) {
		undoSubmit();
		return;
	}
  }
  
  back = true;
  document.Frm1.PAGE.value = backPage; 
  doFormSubmit(document.Frm1);  
}

function aggStatoMotiv(){
  if (document.Frm1.codEsitoAz.selectedIndex==0) {
    document.Frm1.motivazione.value="";
    document.Frm1.motivazione.disabled=true;   
  }
  else {
    document.Frm1.motivazione.disabled=false;
  }
}

function checkNoEmpty()
{ if(!back)
  {  var codEsitoAz   = window.document.Frm1.codEsitoAz.value;
     var motivazione  = window.document.Frm1.motivazione.value;
     var codEsitoCand = window.document.Frm1.codEsitoCand.value;
     //alert("checkNoEmpty::"+codEsitoAz+motivazione+codEsitoCand);
     if((codEsitoAz=="") && (codEsitoCand=="")) 
     {alert("La sezione del lavoratore e/o la sezione dell'azienda devono essere completate");
      return false;
     }
  }
  return true;
}

function ApriContattoAzienda(prgazienda,prgunita){
 var s = "AdapterHTTP?PAGE=ScadContattoPage&PageProvenienza=MatchEsitoPage&PRGAZIENDA=" + prgazienda + "&PRGUNITA=" + prgunita;
 	 s+= "&CDNFUNZIONE=<%=_cdnFunz %>&codcpi=<%=user.getCodRif()%>&PRGROSA=<%=prgRosa%>&CPIROSE=<%=user.getCodRif()%>";
 	 s+= "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&RECUPERAINFO=AZIENDA&MESSAGE_ROSA=<%=mess%>&LIST_PAGE_ROSA=<%=listPage%>";
 	 s+= "&_CDNLAVORATORE_=<%=cdnLavoratore%>&_PRGAZIENDA_=<%=prgAzienda%>&_PRGUNITA_=<%=prgUnita%>";

  if (flagChanged==true) {
	if (! confirm("I dati sono cambiati.\r\nProcedere lo stesso ?")) {
		undoSubmit();
		return;
	}
  }

 setWindowLocation(s); 	  	  	 
}

function ApriContattoLavoratore(cdnlavoratore){
 var s = "AdapterHTTP?PAGE=ScadContattoPage&PageProvenienza=MatchEsitoPage&CDNLAVORATORE=" + cdnlavoratore;
 	 s+= "&CDNFUNZIONE=<%=_cdnFunz %>&codcpi=<%= user.getCodRif() %>&PRGROSA=<%=prgRosa%>&CPIROSE=<%=user.getCodRif()%>";
 	 s+= "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&RECUPERAINFO=LAVORATORE&MESSAGE_ROSA=<%=mess%>&LIST_PAGE_ROSA=<%=listPage%>";
 	 s+= "&_CDNLAVORATORE_=<%=cdnLavoratore%>&_PRGAZIENDA_=<%=prgAzienda%>&_PRGUNITA_=<%=prgUnita%>";

  if (flagChanged==true) {
	if (! confirm("I dati sono cambiati.\r\nProcedere lo stesso ?")) {
		undoSubmit();
		return;
	}
  }
 	 
 setWindowLocation(s);
}

//----- Script utilizzati nell info di testata della rosa
  function openPar_Pesato_PopUP() {
     window.open ("AdapterHTTP?PAGE=MatchViewParPesatoPage&PRGINCROCIO=<%=prgIncrocio%>", "Parametri", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES,left=150,top=100');
  }
  
  function openPar_Esatto_PopUP() {
     window.open ("AdapterHTTP?PAGE=MatchViewParEsattoPage&PRGINCROCIO=<%=prgIncrocio%>", "Parametri", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES,left=150,top=100');
  }
  
  function openRich_PopUP(prgRich) {
     window.open ("AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=" +prgRich +"&CDNFUNZIONE=<%=_cdnFunz%>&POPUP=1", "DettaglioSintetico", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
  }
//----------------------------------------------------------
</script>

</head>



<body class="gestione" onload="rinfresca();<%if(strApriEv.equals("1") && bevid) { %> apriEvidenze() <%}%>">

<% 
InfCorrentiLav  testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore,user);
testata.show(out);
      
/* if (!flag_insert) { 
    if(prgRichiestaAz != null && !prgRichiestaAz.equals("") ) {
      Linguette l = new Linguette( user,  _cdnFunz, _page, new BigDecimal(prgRichiestaAz));
      l.setCodiceItem("PRGRICHIESTAAZ");    
      l.show(out);
    }
//}*/

//------"Costruisco la sezione di testata contente le info della rosa
if(infoRosa!=null) {%>                                                                            
<br>                                                                                                
  <%out.print(StyleUtils.roundTopTableInfo());%>                                                                 
  <p class="info_lav">                                                                              
  Identificativo Rosa <b><%=prgRosa%></b><br>                                                       
   <a class="info_lav" href="#" onClick="openRich_PopUP('<%=prgOrig%>')"><img src="../../img/info_soggetto.gif" alt="Dettaglio"/></a>&nbsp;
  Richiesta num. <b><%=numRichiestaOrig%>/<%=numAnno%></b> - 
  <a class="info_lav" href="#" onClick="openRich_PopUP('<%=prgRichiestaAz%>')"><img src="../../img/copiarich.gif" alt="Inf. Copia di Lavoro"/></a>&nbsp;Copia utilizzata
   - Alternativa utilizzata <b><%=strAlternativa%></b> - 
   <%if(prgTipoIncrocio.equals("1")) {%>
    <a href="#" onClick="openPar_Esatto_PopUP()"><img src="../../img/match_par.gif"></a>&nbsp;Parametri utilizzati
  <%} else {%>
    <%if(prgTipoIncrocio.equals("2")) {%>
    <a href="#" onClick="openPar_Pesato_PopUP()"><img src="../../img/match_par.gif"></a>&nbsp;Parametri utilizzati
    <%}%>
  <%}%>
  <br>Tipo di Rosa <b><%=tipoRosa%></b> -  Tipo di Incrocio <b><%=tipoIncrocio%></b><br>                
  <%if(!utAttivo.equals(utMod)){%>                                                                  
    <br>Ultima Modifica <b><%=ultimaModifica%></b>                                                  
  <%}%>                                                                                             
  </p>                                                                                              
  <%out.print(StyleUtils.roundBottomTableInfo());
}//if(infoRosa!=null)
//-------------------------------
%>


<p align="center">
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="checkNoEmpty()">
 <%out.print(htmlStreamTop);%>
 <table class="main">
  <tr><td colspan="2"><p class="titolo">Esito candidato</p></td></tr>
  <tr><td colspan="2">
        <center>
        <font color="green">
        <%if (flag_insert) {%> 
          <af:showMessages prefix="M_InsertEsitoMatchIDO" />
        <%}else{%>
          <af:showMessages prefix="M_SaveEsitoMatchIDO"/>
        <%}%>
        </font>
      </center>
      <center>
      <font color="red"><af:showErrors /></font>
      </center>
  </td></tr>
  <tr><td><br/></td></tr>
  <tr><td colspan="2"><div class="sezione2">Contatto Azienda-Lavoratore</div></td></tr>
    <tr>
      <td class="etichetta">Data del contatto</td>
      <td class="campo">
           <af:textBox name="datContatto" type="date" classNameBase="input" value="<%=datContatto%>" readonly="<%= String.valueOf(!canModify) %>"
                       validateOnPost="true" onKeyUp="fieldChanged();"/>
      </td>
    </tr>
    <tr><td><br/></td></tr>

  <tr><td colspan="2"><div class="sezione2">Lato azienda&nbsp;<a href="javascript:ApriContattoAzienda('<%=prgAzienda%>','<%=prgUnita%>');"><img src="../../img/contatti.gif" alt="Contatto Azienda"></a></div></td></tr>
    <tr>
      <td class="etichetta">Esito del contatto</td>
      <td class="campo">
        <af:comboBox classNameBase="input" name="codEsitoAz" selectedValue="<%=codEsitoAz%>" moduleName="M_DesEsitoXAzienda"  addBlank="true"
                     disabled="<%= String.valueOf(!canModify) %>" onChange="fieldChanged();aggStatoMotiv();" />
      </td>
    </tr>
    <tr>
      <td class="etichetta">Motivazione</td>
      <td class="campo">
      <% if (codEsitoAz=="") { %>
        <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="motivazione" value="<%=motivazione%>"
                     cols="60" rows="4" maxlength="100" inline="disabled='true'" />
      <% } else { %>
        <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="motivazione" value="<%=motivazione%>"
                     cols="60" rows="4" maxlength="100"  readonly="<%=String.valueOf(!canModify)%>" />
      <% } %>
      </td>
    </tr>
    <tr><td><br/></td></tr>
    
  <tr><td colspan="2"><div class="sezione2">Lato lavoratore&nbsp;<a href="javascript:ApriContattoLavoratore('<%=cdnLavoratore%>')"><img src="../../img/contatti.gif" alt="Contatto Lavoratore"></a></div></td></tr>
    <tr>
      <td class="etichetta">Esito del contatto</td>
      <td class="campo">
        <af:comboBox classNameBase="input" name="codEsitoCand" selectedValue="<%=codEsitoCand%>" moduleName="M_DesEsitoXCandidato"  addBlank="true"
                     disabled="<%= String.valueOf(!canModify) %>" onChange="fieldChanged();" />
      </td>
    </tr>
    <tr><td><br/></td></tr>
    
  <tr><td colspan="2"><div class="sezione2">Referente</div></td></tr>
    <tr>
      <td class="etichetta">Referente</td>
      <td class="campo">
           <af:textBox name="referente" type="text" classNameBase="input" value="<%=referente%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="100" onKeyUp="fieldChanged();"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta">Telefono</td>
      <td class="campo">
           <af:textBox name="tel" type="text" classNameBase="input" value="<%=tel%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="20" onKeyUp="fieldChanged();"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta">Fax</td>
      <td class="campo">
           <af:textBox name="fax" type="text" classNameBase="input" value="<%=fax%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="20" onKeyUp="fieldChanged();"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta">e-mail</td>
      <td class="campo">
           <af:textBox name="email" type="text" classNameBase="input" value="<%=email%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="80" onKeyUp="fieldChanged();"/>
      </td>
    </tr>
  
    <tr><td><br/></td></tr>
    <tr><td><br/></td></tr>
    <tr>
      <td align ="center" colspan="2">
      <%
      if (canInsert || canAggiorna) {
        if(!flag_insert) {
        %>
          <input class="pulsante" type="submit" name="salva" value="Aggiorna">
        <%
        } 
        else {
        %>
          <input class="pulsante" type="submit" name="inserisci" value="Inserisci">
        <%
        }
      }
      %>
       <input class="pulsante" type="button" name="chiudi" value="Chiudi" onClick="cameBack('<%=pageBack%>')">
      </td>
    </tr>
    
    <tr><td><br/></td></tr>
    <tr><td colspan="2"><p align="center"><% if (!flag_insert) operatoreInfo.showHTML(out); %></p></td></tr>
    <tr><td><br/></td></tr>
  </table>
  <%out.print(htmlStreamBottom);%>

<input type="hidden" name="PAGE" value="MatchEsitoPage"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="prgRichiestaAz" value="<%=prgRichiestaAz%>"/>
<input type="hidden" name="PRGORIGINALE" value="<%=prgOriginale%>"/>
<input type="hidden" name="prgRosa" value="<%=prgRosa%>"/>
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
<input type="hidden" name="cpiRose" value="<%=user.getCodRif()%>"/>
<%-- <input type="hidden" name="keyLock****" value="<%=Utils.notNull(keyLock)%>"/> --%>
<%if(!mess.equals("")) {%>
	<input type="hidden" name="MESSAGE" value="<%=mess%>"/>
	<%if(!listPage.equals("")) {%>
		<input type="hidden" name="LIST_PAGE" value="<%=listPage%>"/>
	<%}%>
<%}%>
</af:form> 

</body>
</html>