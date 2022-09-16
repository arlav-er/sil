<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="patto" prefix="pt" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.Values,               
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  java.lang.*,
                  java.text.*,it.eng.sil.security.PageAttribs, it.eng.sil.module.voucher.*,
                  it.eng.sil.util.patto.PageProperties" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	SourceBean notePatto = (SourceBean)serviceResponse.getAttribute("NotePattoStoricizzato.ROWS.ROW");
	String cdnLavoratore= (String )notePatto.getAttribute("CDNLAVORATORE");

	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	PageAttribs pageAtts = new PageAttribs(user, _current_page);
	String codstatoatto = "";
	String flgPatto297 = "";
	String codCpiUtenteCollegato = user.getCodRif();
	String codCpiProprietario = "";
	
	boolean canModify = false;
	boolean canAnnullaVoucher = false;
	boolean canStampaVoucher = false;
	
	PageAttribs pageAttsPatto = new PageAttribs(user, "PattoAzioniLinguettaPage");
	canAnnullaVoucher = pageAttsPatto.containsButton("ANNULLAVCH");
	canStampaVoucher = pageAttsPatto.containsButton("STAMPAVCH");
	
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    	boolean existsSalva = pageAtts.containsButton("aggiorna");
    	
    	if(!existsSalva){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();
    	}
    }
    canModify=false;
    String readOnlyStr   ="true";//canModify?"false":"true";
%>

<%
    //////////////////////
    Vector azioniRows= serviceResponse.getAttributeAsVector("PattoElencoAzioniStorico.ROWS.ROW");
    /////////////////////////////////
    Vector  appuntamentiRows= serviceResponse.getAttributeAsVector("PattoElencoAppuntamentiStorico.ROWS.ROW");    
    /////////////////////
    String strNote = "";
    String prgPattoLavoratore = "";
    String numklopattolavoratore = "";
    if (notePatto!=null) {
        strNote = Utils.notNull(notePatto.getAttribute("strnoteazioni"));
        prgPattoLavoratore = Utils.notNull(notePatto.getAttribute("prgPattoLavoratore"));
        numklopattolavoratore = String.valueOf(((BigDecimal)notePatto.getAttribute("numklopattolavoratore")).intValue()+1);
        flgPatto297 = Utils.notNull(notePatto.getAttribute("flgPatto297"));
        codstatoatto = Utils.notNull(notePatto.getAttribute("codStatoAtto"));
    }
    /////////////////////////////////
    //String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    StringBuffer args = new StringBuffer();
    String statoSezioni = (String)serviceRequest.getAttribute("statoSezioni");    
    String nonFiltrare = Utils.notNull(serviceRequest.getAttribute("NONFILTRARE"));
    //////////////////////
    PageProperties pageProperties = new PageProperties("PattoAzioniLinguettaPage",null);
    PageAttribs pageAttsPattoAperto = new PageAttribs(user,"PattoAzioniLinguettaPage");
    
    pageContext.setAttribute("pageProperties", pageProperties);
    pageContext.setAttribute("sectionState",Utils.notNull(statoSezioni));
    pageContext.setAttribute("args", args);
    pageContext.setAttribute("isReadOnly", Boolean.valueOf(readOnlyStr));
%>
<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
String queryString = null;
%>
<html>
<head>
<title>Azioni</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<style type="text/css">
table.sezione {
	border-collapse:collapse;
	font-family: Verdana,"Times New Roman", Arial, Sans-serif; 
	font-size: 12px;
	font-weight: bold;
	border-bottom-width: 2px; 
	border-bottom-style: solid;
	border-bottom-color: #000080;
	color:#000080;
	position: relative;
	width: 98%;
	left: 1%;
	text-align: left;
	text-decoration: none;
	
}
td.pulsante_riga{
    text-align: right;
    padding-right:10;
    width:40;
}
</style>
<af:linkScript path="../../js/"/>
<SCRIPT TYPE="text/javascript">
<!--
    function nonImplementato(){alert("funzione non attiva");}
//-->
</SCRIPT>
<script>
<!--

// Rilevazione Modifiche da parte dell'utente
var flagChanged = false;

function fieldChanged() {
 <% if (!readOnlyStr.equalsIgnoreCase("true")){ %> 
    flagChanged = true;
 <%}%> 
}

var sezioni = new Array();
var i=0;

function chiudiSezioni() {
    for (i=0;i<sezioni.length;i++) {
        if (sezioni[i].sezione.aperta) {
            sezioni[i].sezione.style.display="none";
            sezioni[i].sezione.aperta=false;
            sezioni[i].img.src="../../img/chiuso.gif";
        }
    }
}

function cancella(prgLavPattoScelta) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (!confirm("Cancellare la associazione?")) return;
	var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
	urlpage+="cancellaAssociazione=&";
	urlpage+="PRG_LAV_PATTO_SCELTA="+prgLavPattoScelta+"&";
	urlpage+="statoSezioni="+getStatoSezioni()+"&";
	urlpage+="PAGE=PattoAzioniLinguettaPage";
	urlpage+="&NONFILTRARE=<%=nonFiltrare%>";
	setWindowLocation(urlpage);
}

function cancellaTutto() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (!confirm("Cancellare tutte le associazioni della sezione?")) return;
    var urlpage="";
    urlpage+="AdapterHTTP?";
    if (arguments.length<=0) {
        alert('nessun legame col patto da cancellare');
        return;
    }
    for (i=0;i<arguments.length;i++) {
        urlpage+="PRG_LAV_PATTO_SCELTA="+arguments[i]+"&";       
    }
    urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
    urlpage+="cancellaAssociazione=&";
    urlpage+="statoSezioni="+getStatoSezioni()+"&";
	urlpage+="PAGE=PattoAzioniLinguettaPage";
	urlpage+="&NONFILTRARE=<%=nonFiltrare%>";
    setWindowLocation(urlpage);
}
function apriTutte(page) {
    var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";	
    urlpage+="COD_LST_TAB=AG_LAV&";    
    urlpage+="COD_LST_TAB=OR_PER&";    
	urlpage+="statoSezioni="+getStatoSezioni()+"&";
    urlpage+="pageChiamante=PattoAzioniLinguettaPage&";
	urlpage+="PAGE="+page;
	urlpage+="&NONFILTRARE=<%=nonFiltrare%>";
	window.open(urlpage,"Associazioni", 'toolbar=0, scrollbars=1, resizable=1'); 
}
function apri(page, codLstTab) {
	var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
	urlpage+="COD_LST_TAB="+codLstTab+"&";
	urlpage+="statoSezioni="+getStatoSezioni()+"&";
	urlpage+="PAGE="+page+"&";
    urlpage+="pageChiamante=PattoAzioniLinguettaPage&";
    urlpage+="NONFILTRARE=<%=nonFiltrare%>";
	window.open(urlpage,"Associazioni", 'toolbar=0, scrollbars=1, resizable=1'); 
}
function cambia(immagine, sezione) {
	if (sezione.aperta==null) sezione.aperta=true;
	if (sezione.aperta) {
		sezione.style.display="none";
		sezione.aperta=false;
		immagine.src="../../img/chiuso.gif";
	}
	else {
		sezione.style.display="inline";
		sezione.aperta=true;
		immagine.src="../../img/aperto.gif";
	}
}

function filtro(){
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var url = "AdapterHTTP?";
  url+="cdnLavoratore=<%=cdnLavoratore %>&";
  url+="CDNFUNZIONE=<%=cdnFunzione %>&";
  url+="statoSezioni="+getStatoSezioni()+"&";
  url+="PAGE=PattoAzioniLinguettaPage&";
  url+="NONFILTRARE=false";
  setWindowLocation(url);
}

function togliFiltro(){
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
  
  var url = "AdapterHTTP?";
  url+="cdnLavoratore=<%=cdnLavoratore %>&";
  url+="CDNFUNZIONE=<%=cdnFunzione %>&";
  url+="statoSezioni="+getStatoSezioni()+"&";
  url+="PAGE=PattoAzioniLinguettaPage&";
  url+="NONFILTRARE=true";
  setWindowLocation(url);
}

function getStatoSezioni() {
	var s="";    
	for (j=0;j<sezioni.length;j++) {
		if (sezioni[j].sezione.aperta) 
			s+="1";
		else s+="0";
	}
	return s;
}
function Sezione(sezione, img,aperta){    
    this.sezione=sezione;
    this.sezione.aperta=aperta;
    this.img=img;
}

function initSezioni(sezione){
	sezioni.push(sezione);
}

var allArgs="";

function cancellaProprioTutto() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (allArgs.length==0){
        alert('non ci sono dati da slegare al patto');
        return;
    }
    else {
        var keys=allArgs.substring(0,allArgs.length-1);
        var keysArray = keys.split(",");
       if (!confirm("Cancellare tutte le associazioni della sezione?")) return;
        var urlpage="";
        urlpage+="AdapterHTTP?";
        if (keysArray.length<=0) {
            alert('nessun legame col patto da cancellare');
            return;
        }
        for (i=0;i<keysArray.length;i++) {
            urlpage+="PRG_LAV_PATTO_SCELTA="+keysArray[i]+"&";       
        }
        urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
        urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
        urlpage+="cancellaAssociazione=&";
        urlpage+="statoSezioni="+getStatoSezioni()+"&";
        urlpage+="PAGE=PattoAzioniLinguettaPage";
        urlpage+="&NONFILTRARE=<%=nonFiltrare%>";
        setWindowLocation(urlpage);
    }
}

function updateStato(thisIn) {
	thisIn.statoSezioni.value = getStatoSezioni();
	return true;
}
 
function annullaVoucher(prgPercorso, prgColloquio, isVoucherAttivo) {
	var url = "AdapterHTTP?PAGE=AnnullaVoucherPage";
	url += "&CDNLAVORATORE=<%=cdnLavoratore %>";
	url += "&CDNFUNZIONE=<%=cdnFunzione %>";
	url += "&prgPercorso=" + prgPercorso;
	url += "&prgColloquio=" + prgColloquio;
	url += "&vchAttivo=" + isVoucherAttivo;
	url += "&isStorico=storico";
	url += "&prgPattoLavoratore=<%=prgPattoLavoratore%>";
	window.open(url, "Annulla Voucher", 'toolbar=0, scrollbars=1, height=300, width=500');
}



function stampaTDAVoucher(prgPattoLavoratore) {
	if (document.formStampaVCH.codAttivazione.value == "") {
		alert("Selezionare il codice idi attivazione");
	}
	else {
		apriGestioneDoc('RPT_STAMPA_TDA_CODATTIVAZIONE','&cdnLavoratore=<%=cdnLavoratore%>&cdnFunzione=<%=cdnFunzione%>&prgPattoLavoratore=<%=prgPattoLavoratore%>&strchiavetabella=' + document.formStampaVCH.codAttivazione.value, '<%=it.eng.sil.module.voucher.Properties.TIPO_DOC_TDA_ATTIVAZIONE%>');
	}
}

//-->
</script>

</head>
<body class="gestione" onload="rinfresca()">
<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<p>
	<font color="green">
		<af:showMessages prefix="M_ANNULLA_VOUCHER_AZIONE"/>
	</font>
	<font color="red"><af:showErrors /></font>
</p>            
<%

 /*
 * stampa a video l' intestazione delle informazioni utente e 
 * della linguetta di navigazione
 */
User _user = (User) sessionContainer.getAttribute(User.USERID);
String _cdnFun = (String) serviceRequest.getAttribute("CDNFUNZIONE");
String _page = (String) serviceRequest.getAttribute("PAGE"); 
    
InfCorrentiLav _testata= new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer() ,cdnLavoratore, _user);
 
Linguette _linguetta = new Linguette(_user, Integer.parseInt(_cdnFun), _page, new BigDecimal(prgPattoLavoratore));   
_linguetta.setCodiceItem("prgPattoLavoratore");

_testata.setPaginaLista("PattoInformazioniStorichePage");

_testata.show(out);
_linguetta.show(out);

_user=null;
_cdnFun=null;
_page = null;
_testata=null;
_linguetta=null;

String numConfigVoucher = serviceResponse.containsAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM")?
	serviceResponse.getAttribute("M_CONFIG_VOUCHER.ROWS.ROW.NUM").toString():it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;

%>

<font color="red">
   <af:showErrors/>
</font>

<font color="green">
 <af:showMessages prefix="M_InsertAmLavPattoScelta"/>
 <af:showMessages prefix="M_DeleteAmLavPattoScelta"/>
 <af:showMessages prefix="M_UpdateNotePatto"/>
</font>
<%out.print(htmlStreamTop);%>
<center>
  <table  class="main" border="0">
    <tr><td colspan=4>&nbsp;</td></tr>
    <tr>
        <td colspan=4 class="bianco">
            <table width="100%">
                <tr>                
                    <td width="30%"><a href="#" onclick="chiudiSezioni()">Chiudi tutte</a></td> 
                    <td></td> 
                    <%if (canModify) {%>
                    <td align=right>
                        <a  href="#" onclick="apriTutte('AssociazioneAlPattoTemplatePage')"><img src="../../img/patto_ass.gif"></a>&nbsp;(associa informazioni al patto)
                    </td>
                    <td  align="right"><a href="#" onclick="cancellaProprioTutto()"><img src="../../img/patto_elim.gif"></a>&nbsp;(cancella tutti i legami)</td>            
                    <%}%>
                </tr>
            </table>
        </td>
    </tr>    
    <tr><td colspan=4>&nbsp;</td></tr>
      <tr>
        <td colspan=4>
            <pt:sections pageAttribs="<%= pageAttsPattoAperto %>">
				<pt:dynamicSection name="AG_LAV" titolo="Rinvio a servizio/appuntamento" rows="<%= appuntamentiRows %>" filtra="<%=canModify%>">
        		<pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"AG_LAV\")"/>
        		<pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/>
        		<pt:sectionBody>        
	        		<tr>
                    	<td  colspan="2" class="etichetta2">Data protocollo &nbsp;</td>
                    	<td class="campo2" style="font-weight:bold"><%= Utils.notNull(row.getAttribute("datProtocollo"))%></td>
                        <td colspan="5"></td></tr>
                  <tr>
                        <td style="width:30"></td>
                        <td  class="etichetta2" style="width:78">Data  &nbsp;</td>
                        <td class="campo2" style="font-weight:bold;width:30%"><%=row.getAttribute("DATA")%></td>
                        <td  class="etichetta2" width="108">Orario  &nbsp;</td>
                        <td  class="campo2" style="font-weight:bold"><%=row.getAttribute("ORARIO")%></td>
                        <td class="etichetta2">Durata  &nbsp;</td>
                        <td class="campo2" style="font-weight:bold"><%=Utils.notNull(row.getAttribute("DURATA"))%></td>
                        <td class='pulsante_riga'>
                        <% if (Utils.notNull(row.getAttribute("DATA")).length()>0) {
                            // valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                                args.append(Utils.notNull(row.getAttribute("PRGLAVPATTOSCELTA")));
                                args.append(",");                        
                            %>
                            <script>allArgs+="<%=Utils.notNull(row.getAttribute("PRGLAVPATTOSCELTA"))%>,";</script>
                              <%if (canModify){%>
                                <a  href="#" onclick="cancella('<%=row.getAttribute("prgLavPattoScelta")%>')"><img src="../../img/patto_elim.gif" ></a>
                        <%      }
                          }%>
                        </td>
                   </tr>
                   <tr><td colspan=8></td></tr>
                   <tr>
                        <td></td>
                        <td class="etichetta2">Servizio  &nbsp;</td>
                        <td class="campo2" style="font-weight:bold" ><%=row.getAttribute("DESSERVIZIO")%></td>
                        <td class="etichetta2">Esito  &nbsp;</td>
                        <td class="campo2" style="font-weight:bold"><%=row.getAttribute("DESESITO")%></td>
                        <td></td>
                        <td></td>
                        <td></td>
                  </tr>
				</pt:sectionBody>
			</pt:dynamicSection>                  
        </td>
    </tr>
    <tr>
        <td colspan=4>
				<pt:dynamicSection name="OR_PER" titolo="Azioni/Obiettivi concordati" rows="<%= azioniRows %>">
                    <pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"OR_PER\")"/>
                    <pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/>
                    <pt:sectionBody>  

                    <%
                    boolean first=true;
                    codCpiProprietario=Utils.notNull(row.getAttribute("codcpi"));
                    %>
                    <tr>
                    	<td  colspan="2" class="etichetta2">Data protocollo &nbsp;</td>
                    	<td class="campo2" style="font-weight:bold"><%= Utils.notNull(row.getAttribute("datProtocollo"))%></td>
                        <td colspan="4"></td></tr>
                    <tr>
                        <td style="width:30"></td>
                        <td class="etichetta2" style="text-align:right;width:78">Azione &nbsp;</td>
                        <td  class="campo2" style="width:30%;font-weight:bold;"><%= Utils.notNull(row.getAttribute("Descrizione")) %></td>
						<td class="etichetta2" style="text-align:right;width:78">Entro il &nbsp;</td>
                        <td class="campo2" style="font-weight:bold;"><%=Utils.notNull(row.getAttribute("dataPercorso"))%></td>
                        <td class="etichetta2" style="text-align:right;vertical-align:bottom">Misura &nbsp;</td>
                        <td class="campo2" style="font-weight:bold"><%=Utils.notNull(row.getAttribute("azione_ragg"))%></td>
                        <td class='pulsante_riga'>
                        <% if (Utils.notNull(row.getAttribute("Descrizione")).length()>0) {
                                // valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                                    args.append(Utils.notNull(row.getAttribute("PRGLAVPATTOSCELTA")));
                                    args.append(",");
                                    String dataStipula = Utils.notNull(row.getAttribute("datStimata"));
                                    /* controllo eventuale della data in javascript con alert nel caso in cui la data
                                       stipula sia successiva alla data stimata dell' azione */
                                %>
                                <script>allArgs+="<%=Utils.notNull(row.getAttribute("PRGLAVPATTOSCELTA"))%>,";</script>
                                  <%if (canModify){%>
                                      <a  href="#" onclick="cancella('<%=row.getAttribute("prgLavPattoScelta")%>')"><img src="../../img/patto_elim.gif" ></a>
                        <%          }
                           } %>
                        </td>
                    </tr>
                    <tr><td colspan="8"></td></tr>
                    <tr>                        
                        <td style="width:30"></td>                                                                        
                        <td class="etichetta2">Esito &nbsp;</td>
                        <td class="campo2" style="font-weight:bold" ><%= Utils.notNull(row.getAttribute("Esito")) %>&nbsp;</td>
                        <td>&nbsp;</td>

					<% if (row.containsAttribute("dataEffettiva")) { %>
                        <td class="etichetta2">Data di svolgimento &nbsp;</td>
                        <td class="campo2" style="font-weight:bold" ><%= Utils.notNull(row.getAttribute("dataEffettiva")) %>&nbsp;</td>
                    <% } %>
                        <td colspan=3>&nbsp;
                            <%--
                            <% if (first && row.getAttribute("Descrizione")!=null) { first=false;%>
                              <a href="#" onclick="alert('Funzione non implementata')">per le seguenti mansioni</a>
                            <%}%>
                            --%>
                        </td>
                        <td>&nbsp;</td>
                    </tr>
                    <tr><td colspan="8"></td></tr>
                    <tr>
                      <td style="width:30"></td>
                      <td class="etichetta2">Note &nbsp;</td>
                      <td class="campo2" colspan="6" style="font-weight:bold"><%=Utils.notNull(row.getAttribute("STRNOTE"))%></td>
                    </tr>
                    <%
                    int numeroModelloAssociato = 0;
                   	if (row.containsAttribute("numModelloVoucher")) {
                   		numeroModelloAssociato = ((BigDecimal)row.getAttribute("numModelloVoucher")).intValue();
                   	}
                    if (codstatoatto.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.STATO_ATTO_PROTOC) 
                    	&& numeroModelloAssociato > 0 && numConfigVoucher.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.CUSTOM_CONFIG)) {%>
                    	<tr>
                    		<td style="width:30"></td>                                                                        
                        	<td class="etichetta2">VOUCHER &nbsp;</td>
                        	<td class="campo2" style="font-weight:bold" ><%= Utils.notNull(row.getAttribute("descStatoVoucher")) %>&nbsp;</td>
                        	<td>&nbsp;</td>
                        	<% if (row.containsAttribute("codattivazione")) { %>
		                        <td class="etichetta2" nowrap>Codice di attivazione &nbsp;</td>
		                        <td class="campo2" style="font-weight:bold" ><%= Utils.notNull(row.getAttribute("codattivazione")) %>&nbsp;</td>
		                    <%} else {%>
		                    	<td colspan="2">&nbsp;</td>
		                    <%}%>
		                   <% if ((row.containsAttribute("codstatovoucher")) && 
                   		 			row.getAttribute("codstatovoucher").toString().equalsIgnoreCase(StatoEnum.ANNULLATO.getCodice()) )  { %>
		                        <td class="etichetta2" nowrap>Motivo Annullamento &nbsp;</td>
		                        <td class="campo2" style="font-weight:bold" ><%= Utils.notNull(row.getAttribute("descrAnnull")) %>&nbsp;</td>
		                    <%} else {%>
		                    	<td colspan="2">&nbsp;</td>
		                    <%}%>
		                   <%if ( row.containsAttribute("codstatovoucher") && 
	                    		 row.getAttribute("codstatovoucher").toString().equalsIgnoreCase(StatoEnum.ASSEGNATO.getCodice())) {
	                    		if (codCpiUtenteCollegato.equalsIgnoreCase(Utils.notNull(row.getAttribute("codcpi"))) && canAnnullaVoucher) {%>
			                    	<td class="campo2">&nbsp;</td>
                       				<td class="pulsante_riga">
			                    	<input class="pulsante" type="button" name="AnnullaVoucher" value="Annulla" 
			                    		onclick="annullaVoucher('<%=row.getAttribute("prgpercorso")%>', '<%=row.getAttribute("prgcolloquio")%>',null)">
			                    	</td>
		                    	<%}
	                    	}
		                    %>
	                    	<%if ( row.containsAttribute("codstatovoucher") && 
	                    	      row.getAttribute("codstatovoucher").toString().equalsIgnoreCase(StatoEnum.ATTIVATO.getCodice())) {
	                    		if (codCpiUtenteCollegato.equalsIgnoreCase(Utils.notNull(row.getAttribute("codcpi"))) && canAnnullaVoucher) {%>
			                    	<td class="campo2">&nbsp;</td>
                       				<td class="pulsante_riga">
			                    	<input class="pulsante" type="button" name="AnnullaVoucher" value="Annulla" 
			                    		onclick="annullaVoucher('<%=row.getAttribute("prgpercorso")%>', '<%=row.getAttribute("prgcolloquio")%>','<%=StatoEnum.ATTIVATO.getCodice()%>')">
			                    	</td>
		                    	<%}
	                    	}
		                    %>
                    	</tr>
                    <%}%>                                              
                    <tr><td colspan="8">&nbsp;</td></tr>
                </pt:sectionBody>
			</pt:dynamicSection>   
		</pt:sections>
        </td>
    </tr>
    <tr><td colspan="4"><br><br></td></tr>
    <af:form name="formAzioni" method="post" action="AdapterHTTP" onSubmit="updateStato(this)" dontValidate="true">
        <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
        <input type="hidden" name="PAGE" value="PattoAzioniLinguettaPage">
        <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">        
        <input type="hidden" name="statoSezioni" value="">
        <input type="hidden" name="PRGPATTOLAVORATORE" value="<%=prgPattoLavoratore%>">  
        <input type="hidden" name="NUMKLOPATTOLAVORATORE" value="<%=numklopattolavoratore%>">
    <tr>
        <td colspan=3>
            <table width="100%">
                <tr>
                    <td class="etichetta">Nota azioni</td>
                    <td class="campo"><af:textArea onKeyUp="fieldChanged();" name="STRNOTEAZIONI" value="<%=strNote%>"
                     cols="60" rows="5" readonly="<%=readOnlyStr%>" classNameBase="textarea"/></td>
                    <td>
                    <% if (canModify && notePatto!=null) { %>
                        <input type="submit" name="aggiornaNotePatto" class="pulsanti" value="Aggiorna nota">
                    <%}%>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    </af:form>
</table>
<%if (numConfigVoucher.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.CUSTOM_CONFIG) && 
	  canStampaVoucher && codCpiUtenteCollegato.equalsIgnoreCase(codCpiProprietario)) {%>
<af:form name="formStampaVCH">
<table class="main">
<tr>
  <td>
    <div id="titoloStampa" class="sezione2">Stampa Cod. Attivazione TDA</div>
  </td>
</tr>
</table>

<center>
<table>
<tr>
 	<td align="center" class="etichetta" nowrap>Codice attivazione&nbsp;</td>
 	<td align="center" class="campo" nowrap>
	  <af:comboBox name="codAttivazione" moduleName="M_GetCodiceAttivazionePatto"
        	classNameBase="input" title="Codice attivazione" onChange="fieldChanged();"/>&nbsp;&nbsp;&nbsp;
		<input class="pulsante" type="button" name="stampaVch" value="Stampa" onclick="stampaTDAVoucher('<%=prgPattoLavoratore%>')"/>
	</td>
</tr>
</table>
</center>

</af:form>
<%}%>

<%out.print(htmlStreamBottom);%>
</center>

<br>

</body>
</html>