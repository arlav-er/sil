<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.lang.*,
                  java.text.*
                  "%>
    
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
    String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
    boolean fromDid = serviceRequest.containsAttribute("FROM_DID") && serviceRequest.getAttribute("FROM_DID").equals("S") ? true : false;
	boolean fromPatto = serviceRequest.containsAttribute("fromPattoDettaglio") ? true : false; 
    boolean fromTrasf = serviceRequest.containsAttribute("FROM_TRASF") && serviceRequest.getAttribute("FROM_TRASF").equals("S") ? true : false;

    String pageDocAssociata = (String) serviceRequest.getAttribute("pageDocAssociata");
    String trasferisci = (String) serviceRequest.getAttribute("trasferisci");
    // QUESTO PARAMETRO SERVE ALLA PAGINA DELLA DID PER SAPERE QUALE FRAME AGGIORNARE SULL'ONLOAD (SE CHIAMATA DAL PATTO
    // LA DID NON SI TROVERA' NEL FRAME MAIN BENSI' IN UNA POP-UP DI NOME 'DICHIARAZIONEDISPONIBILITA')
	String target = Utils.notNull(serviceRequest.getAttribute("FRAME_NAME"));
    String fromPage = null;
    boolean isDettaglioStorico = (fromPage=(String)serviceRequest.getAttribute("FROM_PAGE"))!=null && fromPage.equals("PRIVACYRICERCASTORICOPAGE");
    ////////////////////
    String prgPrivacy = null;
    String flagAutorizzazione = null;
    String dataInizio = null;
    String dataFine = null;
    String numKloPrivacy = null;
    String moduleName = isDettaglioStorico?"M_AUTPRIVACYSTORICO":"M_AUTPRIVACY";
    String pageName=isDettaglioStorico?"PrivacyRicercaStoricoPage":"PrivacyDettaglioPage";
    SourceBean row = (SourceBean)serviceResponse.getAttribute(moduleName+".ROWS.ROW");
    BigDecimal cdnUtins = null;
    String dtmins = null;
    BigDecimal cdnUtmod = null;
    String dtmmod = null;
    Testata operatoreInfo = null;
   
    //
    boolean isInsert = row==null;
    if (!isInsert) {
        flagAutorizzazione = StringUtils.getAttributeStrNotNull(row, "flgAutoriz");
        dataFine = StringUtils.getAttributeStrNotNull(row, "datFine");
        dataInizio = StringUtils.getAttributeStrNotNull(row, "datInizio");
        prgPrivacy = Utils.notNull(row.getAttribute("prgPrivacy"));
        BigDecimal klo = (BigDecimal)row.getAttribute("NUMKLOPRIVACY");
        numKloPrivacy = String.valueOf(klo.intValue()+1);
        cdnUtins = (BigDecimal) row.getAttribute("cdnUtins");
	    dtmins = StringUtils.getAttributeStrNotNull(row, "dtmins");
	    cdnUtmod = (BigDecimal) row.getAttribute("cdnUtmod");
	    dtmmod = StringUtils.getAttributeStrNotNull(row, "dtmmod");
	    operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
    }
    else {
        flagAutorizzazione="";
        dataInizio = DateUtils.getNow();
    }
    ////////////////////


  ProfileDataFilter filter = new ProfileDataFilter(user, pageName);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    	/*boolean existsSalva = attributi.containsButton("salva");
    	
    	if(!existsSalva){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();    	
    	}*/
    PageAttribs attributi = new PageAttribs(user, "PrivacyDettaglioPage");
    boolean canModify   = attributi.containsButton("aggiorna");
    boolean canInsert   = attributi.containsButton("INSERISCI") && !isDettaglioStorico;
    boolean canPrint    = attributi.containsButton("stampa");
    boolean infStorButt = attributi.containsButton("STORICO")&& !isDettaglioStorico;
    if ( !canModify && !canInsert ) {
      
    } else {
      boolean canEdit = filter.canEditLavoratore();
      if ( !canEdit ) {
        canModify = false;
        canInsert = false;
      }
    }
    String readOnlyString = String.valueOf(!canModify);
    
    String htmlStreamTop = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

    //}
    

%>
<html>
<head>
<title>Dettaglio dell'elenco anagrafico</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>

<af:linkScript path="../../js/"/>

<script language="JavaScript">
	// Rilevazione Modifiche da parte dell'utente
	var flagChanged = false;

	function fieldChanged() {
 	<% if (!readOnlyString.equalsIgnoreCase("true")){ %> 
    	flagChanged = true;
 	<%}%> 
	}
</script>


<script language="Javascript">
<%
    if (!isDettaglioStorico) {
        attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
    }
%>
</script>
<%if (!isDettaglioStorico) {%>
<script language="Javascript">
   if ( window.top.menu != undefined) {
     window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
   }
</script>
<%}%>
<script language="Javascript">   
<%@ include file="_controlloDate_script.inc"%>
<!--
function apriInfStoriche() {
    var urlpage="AdapterHTTP?PAGE=PrivacyRicercaStoricoPage&";
    urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
    urlpage+="cdnFunzione=<%=cdnFunzione%>";
    open(urlpage);
}
function tornaAllaListaStorica() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var urlpage="AdapterHTTP?PAGE=PrivacyRicercaStoricoPage&";
    urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
    urlpage+="cdnFunzione=<%=cdnFunzione%>";
    setWindowLocation(urlpage);
}

function controllaDataVisione() {
	var dtPrivacy = 0;
    var dataInizio = document.frm.datInizio.value;
    if(dataInizio != ""){
	    dtPrivacy = dataInizio.substr(6,4) + dataInizio.substr(3,2) + dataInizio.substr(0,2);
	    var dtOdierna = "<%= DateUtils.getNow() %>";
	    var dtOdiernaFormat = dtOdierna.substr(6,4) + dtOdierna.substr(3,2) + dtOdierna.substr(0,2);
	    if (dtPrivacy > dtOdiernaFormat) {
	    	alert("La data presa visione della informativa\nrelativa al trattamento dei dati personali\nnon può essere successiva alla data odierna.");
	    	return false;
	    }
	    else {
	    	ricaricaDID();
	    	return true;
	    }
    }
    else {
        return false;
    }
}
//-->
</script>
<script language="javascript">
<!--
function ricaricaDID(){
    <%if(fromDid){%>
      var urlpage="AdapterHTTP?";
      urlpage+="PAGE=DispoDettaglioPage&";
      urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
      urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
      urlpage += "AGGIORNACHIAMANTE=true&";
      urlpage += "datDichiarazione=" + window.opener.document.Frm1.datDichiarazione.value + "&";
      urlpage += "CODTIPODICHDISP=" + window.opener.document.Frm1.CODTIPODICHDISP.value + "&";
      urlpage += "DATSCADCONFERMA=" + window.opener.document.Frm1.DATSCADCONFERMA.value + "&";
      urlpage += "DATSCADEROGAZSERVIZI=" + window.opener.document.Frm1.DATSCADEROGAZSERVIZI.value + "&";
      urlpage += "flgDidL68=" + window.opener.document.Frm1.flgDidL68.value + "&";
      urlpage += "STRNOTE=" + window.opener.document.Frm1.strNote.value + "&";
      <%if (fromPatto) {%>urlpage += "fromPattoDettaglio=1&";<%}%>
      <%if (target!=null  && !target.equals("")) {%>urlpage +="FRAME_NAME=<%=target%>&";<%}%>
      setOpenerWindowLocation(urlpage);
    <%}%>
    <%if(fromTrasf){%>
      var urlpage="AdapterHTTP?";
      urlpage+="PAGE=TrasferimentoPage&";
      urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
      urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
      urlpage+="CODCPIORIG=" + window.opener.document.Frm1.CODCPIORIG.value + "&";
	  urlpage+="CODCPIUTENTE=" + window.opener.document.Frm1.codCPI.value + "&";
      urlpage+="REFRESH=Y&";
      urlpage+="DATTRASFERIMENTO=" + window.opener.document.Frm1.DATTRASFERIMENTO.value + "&";
      urlpage+="STRINDIRIZZODOM=" + window.opener.document.Frm1.STRINDIRIZZODOM.value + "&";
      urlpage+="STRLOCALITADOM=" + window.opener.document.Frm1.STRLOCALITADOM.value + "&";
      urlpage+="codComdom=" + window.opener.document.Frm1.codComdom.value + "&";
      urlpage+="strComdom=" + window.opener.document.Frm1.strComdom.value + "&";
      urlpage+="strCapDom=" + window.opener.document.Frm1.strCapDom.value + "&";
      urlpage+="msgIRLav=" + window.opener.document.Frm1.msgIRLav.value + "&";
      urlpage+="msgXIR=" + window.opener.document.Frm1.msgXIR.value + "&";
      urlpage+="isInterRegione=" + window.opener.document.Frm1.isInterRegione.value + "&";
      urlpage+="isInterProvincia=" + window.opener.document.Frm1.isInterProvincia.value + "&";
      
      urlpage+="CODSTATOOCCUPAZ=" + window.opener.document.Frm1.CODSTATOOCCUPAZ.value + "&";
      urlpage+="codStatoOccupazRagg=" + window.opener.document.Frm1.codStatoOccupazRagg.value + "&";
      urlpage+="datAnzianitaDisoc=" + window.opener.document.Frm1.datAnzianitaDisoc.value + "&";
      urlpage+="datcalcolomesisosp=" + window.opener.document.Frm1.datcalcolomesisosp.value + "&";

      urlpage+="datcalcoloanzianita=" + window.opener.document.Frm1.datcalcoloanzianita.value + "&";
      urlpage+="mesiAnzPrecInt=" + window.opener.document.Frm1.numAnzianitaPrec297.value + "&";
      urlpage+="CODMONOCALCOLOANZIANITAPREC297=" + window.opener.document.Frm1.CODMONOCALCOLOANZIANITAPREC297.value + "&";
      urlpage+="datDichiarazione=" + window.opener.document.Frm1.datDichiarazione.value + "&";


      urlpage+="NUMMESISOSPPREC=" + window.opener.document.Frm1.numMesiSosp.value + "&";
      urlpage+="FLG181=" + window.opener.document.Frm1.FLG181.value + "&";
      urlpage+="STRNOTE=" + window.opener.document.Frm1.STRNOTE.value + "&";
	  urlpage+="pageDocAssociata=<%=pageDocAssociata%>&";
      urlpage+="trasferisci=<%=trasferisci%>";
      setOpenerWindowLocation(urlpage);
    <%}%>


    //close();
}
//-->
</script>
</head>
<body  class="gestione" onload="<%= isDettaglioStorico?"":"rinfresca()"%>">
<%
if (!isDettaglioStorico) {
   InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
   testata.setSkipLista(true);
   testata.show(out);
   }
%>
<br>
<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_UpdatePrivacy"/>
 <af:showMessages prefix="M_InsertPrivacy"/>
</font>
<center><p class="titolo">Presa visione della informativa relativa al trattamento dei dati personali</p></center>
<br><br>
<center>
<af:form name="frm" action="AdapterHTTP" method="post" onSubmit="controllaDataVisione()">

<input type="hidden" name="prgPrivacy" value="<%= prgPrivacy%>">
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>">
<input type="hidden" name="cdnFunzione" value="<%=cdnFunzione%>">
<input type="hidden" name="PAGE" value="<%=pageName%>">
<input type="hidden" name="numKloPrivacy" value="<%=numKloPrivacy%>">
<input type="hidden" name="FRAME_NAME" value="<%=target%>">
<%out.print(htmlStreamTop);%> 
<table class="main">
    <tr>
        <td class="etichetta">Data</td>
        <td class="campo">
            <af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="date" name="datInizio" value="<%=dataInizio%>" validateOnPost="true" required="true"
                          readonly="<%=String.valueOf(!canInsert)%>"  size="11" maxlength="11" title="Data presa visione"/>
                          <!--readonly="<%=String.valueOf(!(isInsert&& canInsert))%>"-->
        </td>
    </tr>
    <tr>
        <td class="etichetta">Presa visione</td>
        <td class="campo">
            <af:comboBox classNameBase="input" name="flgAutoriz" addBlank="false" required="true" title="Autorizzazione"
                   onChange="fieldChanged();" disabled="<%=readOnlyString%>" >
                      <OPTION value="" ></OPTION>          
                      <OPTION value="S" <%= flagAutorizzazione.equalsIgnoreCase("S")? "SELECTED=\"true\"":"" %>>S</OPTION>
                      <OPTION value="N" <%= flagAutorizzazione.equalsIgnoreCase("N")? "SELECTED=\"true\"":"" %>>N</OPTION>
            </af:comboBox>
        </td>
    </tr>
    <%--
    <tr>
        <td class="etichetta">Data fine</td>
        <td class="campo">
            <af:textBox classNameBase="input" type="date" name="datFine" value="<%=dataFine%>" validateOnPost="true" 
                  required="false"  readonly="<%=readOnlyString%>"  size="11" maxlength="11"/>
        </td>
    </tr>
    --%>
</table>
<table>

<tr>
<%if (!isInsert&&canModify){%>
    <td><input type="submit" class="pulsanti" name="salva" value="Aggiorna"></td>
    <%} if (isInsert&& canInsert) {%>
    <td><input type="submit" class="pulsanti" name="inserisci" value="Inserisci"></td>
    <%}%>
    <tr>
      <td colspan="2" align="center">
        <input type="button" class="pulsanti" value="Chiudi" onClick="window.close();">
        <%if(fromDid){%>
          <input type="hidden" name="FROM_DID" value="S">
        <%}%>
        <%if(fromTrasf){%>
          <input type="hidden" name="FROM_TRASF" value="S">
        <%}%>
        <%if (fromPatto) {%>
        	<input type="hidden" name="fromPattoDettaglio" value="1">
        <%}%>
      </td>
    </tr>
    
    <%--
    <%if (infStorButt){%>
    <td><input type="button" name="inf_storiche"  class="pulsanti" value="Informazione storiche" onclick="apriInfStoriche()"></td>
    <%}%>
    --%>
    <%--
    <%if (isDettaglioStorico) {%>
    <td><input type="button" name="inf_storiche"  class="pulsanti" value="Indietro" onclick="tornaAllaListaStorica()"></td>
    <%}%>
    --%>
</tr>
</table>
<%out.print(htmlStreamBottom);%> 

<table>
  <tr><td align="center">
	<%if (operatoreInfo!=null) operatoreInfo.showHTML(out);%>
  </td></tr>
</table>

</af:form>
</center>
</body>
</html>

<% } %>