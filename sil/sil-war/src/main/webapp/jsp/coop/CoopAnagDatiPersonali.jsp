<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                java.text.*, java.util.*,it.eng.sil.util.*, 
                it.eng.sil.module.coop.GetDatiPersonali,
                it.eng.afExt.utils.*,
                java.math.*, it.eng.sil.security.* "%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
    int _funzione=0;

  //flag booleano che discrimina l'inserimento o la modifica
  boolean flag_insert=false;
  
  //inizializzo i campi
  String cdnLavoratore="";
  String strCodiceFiscale="";
  String strCognome="";
  String strNome="";
  String strSesso="";
  String datNasc="";
  String codComNas="";
  String strComNas="";
  String codCittadinanza="";
  String strCittadinanza="";
  String strNazione="";
  String codCittadinanza2="";
  String strCittadinanza2="";
  String strNazione2="";
  String codstatoCivile="";
  String numFigli="";
  String strNote="";
  String flgMilite="";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String numKloLavoratore="";
  String strFlgcfOk="";
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
  String codCpi = "";
  String inserimentoLav="";
  String cpicomp= "";

	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	//filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
    PageAttribs attributi = new PageAttribs(user, _page);

	boolean canInsertLavInLoc = attributi.containsButton("INSERTLAVINLOC");
	boolean canStampa         = attributi.containsButton("STAMPA");
	boolean canUpdateLavInLoc = attributi.containsButton("AGGIORNA");
	
	boolean canModify = false;
    //boolean canScadenzeLav = false;
    //boolean canVerificheLav = false;
  
	boolean canView=filter.canView();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	// Savino 04/12/2006: se la profilatura non e' coerente non viene consentito ne' l'inserimento ne' l'aggiornamento
	// SE ANCHE UNA SOLA DELLE TRE LINGUETTE DELL'ANAGRAFICA NON E' ACCESSIBILE ALLORA NON VIENE PERMESSO NESSUNA MODIFICA DEI DATI.
	// visibilita' pagina indirizzi linguetta 1.2
	ProfileDataFilter filterPagina2 = new ProfileDataFilter(user, "CoopAnagIndirizziPage");
	// visibilita' pagina contatti linguetta 1.3
	ProfileDataFilter filterPagina3 = new ProfileDataFilter(user, "CoopAnagIndirizzoCorrispondezaPage");
	boolean opInsPossibile = (filterPagina2.canView() && filterPagina3.canView());
  //prelevo SUBITO gli eventuali parametri
  //se è stato richiesto l'inserimento abbiamo nella servicerequest
  //il parametro "inserisci" che corrisponde al pulsante premuto
  //Messo a commento dopo l'implementazione delle nuove page per l'inserimento
  //if (serviceRequest.containsAttribute("inserisci")) flag_insert=true;
  
  //  boolean flag_insert=(boolean) flag_insert_str.(1);

  //Recupero CODCPI
  int cdnUt = user.getCodut();
  int cdnTipoGruppo = user.getCdnTipoGruppo();
  if(cdnTipoGruppo == 1) {
    codCpi =  user.getCodRif();
  }
  
  //---------------
  //mi prelevo in ogni caso gli stati civili
  Vector statiCiviliRows=null;
  statiCiviliRows= serviceResponse.getAttributeAsVector("M_GETSTATICIVILI.ROWS.ROW");

  _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  //se non sto gestendo l'inserimento
  //devo prelevare la risposta che mi è pervenuta dalla get
  if (!flag_insert) {

    String response_xml="M_COOP_DatiPersonali_dalla_cache.ROWS.ROW";
    
    try{
    
      SourceBean row=(SourceBean) serviceResponse.getAttribute(response_xml);
		  if (row!=null) {
		  /* TODO Savino 10/10/2006: cdnLavoratore non usato
	      if (row.containsAttribute("cdnLavoratore")) {cdnLavoratore=row.getAttribute("cdnLavoratore").toString();}      
	      */
	      if (row.containsAttribute("strCodiceFiscale")) {strCodiceFiscale=row.getAttribute("strCodiceFiscale").toString();}
	      if (row.containsAttribute("strCognome")) {strCognome=row.getAttribute("strCognome").toString();}
	      if (row.containsAttribute("strNome")) {strNome=row.getAttribute("strNome").toString();}
	      if (row.containsAttribute("strSesso")) {strSesso=row.getAttribute("strSesso").toString();}
	      if (row.containsAttribute("datNasc")) {datNasc=row.getAttribute("datNasc").toString(); }
	      if (row.containsAttribute("codComNas")) {codComNas=row.getAttribute("codComNas").toString();}
	      if (row.containsAttribute("strComNas")) {strComNas=row.getAttribute("strComNas").toString();}
	      if (row.containsAttribute("codCittadinanza")) {codCittadinanza=row.getAttribute("codCittadinanza").toString();}
	      if (row.containsAttribute("strCittadinanza")) {strCittadinanza=row.getAttribute("strCittadinanza").toString();}
	      if (row.containsAttribute("strNazione")) {strNazione=row.getAttribute("strNazione").toString();}
	      if (row.containsAttribute("codCittadinanza2")) {codCittadinanza2=row.getAttribute("codCittadinanza2").toString();}
	      if (row.containsAttribute("strCittadinanza2")) {strCittadinanza2=row.getAttribute("strCittadinanza2").toString();}
	      if (row.containsAttribute("strNazione2")) {strNazione2=row.getAttribute("strNazione2").toString();}
	      if (row.containsAttribute("codstatoCivile")) {codstatoCivile=row.getAttribute("codstatoCivile").toString();}
	      /* TODO Savino 10/10/2006: numFigli non usato
	      if (row.containsAttribute("numFigli")) {numFigli=row.getAttribute("numFigli").toString();}
	      */
	      /* TODO Savino 10/10/2006: strNote non usato
	      if (row.containsAttribute("strNote")) {strNote=row.getAttribute("strNote").toString();}
	      */
	      if (row.containsAttribute("FLGCFOK")) {strFlgcfOk=row.getAttribute("FLGCFOK").toString();}
	      /* TODO Savino 10/10/2006: cdnUtIns etc. non usati
	      if (row.containsAttribute("cdnUtins")) {cdnUtins=row.getAttribute("cdnUtins").toString();}
	      if (row.containsAttribute("dtmins")) {dtmins=row.getAttribute("dtmins").toString();}
	      if (row.containsAttribute("cdnUtmod")) {cdnUtmod=row.getAttribute("cdnUtmod").toString();}
	      if (row.containsAttribute("dtmmod")) {dtmmod=row.getAttribute("dtmmod").toString();}
	      */
	      /* TODO Savino 10/10/2006: numKloLavoratore non usato
	      if (row.containsAttribute("numKloLavoratore")) {numKloLavoratore=row.getAttribute("numKloLavoratore").toString();}
	      */
	      flgMilite=row.containsAttribute("flgMilite")?row.getAttribute("flgMilite").toString():"";
    	}
    }
    catch(Exception ex){
      // non faccio niente
    } 
    
    //operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  } else { //stiamo provenendo dalla ricerca di un soggetto richiedendo l'inserimento.
		 //ho sicuramente in request alcuni dati fondamentali con i quali precompilare
		 //i campi codicefiscale, nome, cognome, comune di nascita
		strCodiceFiscale=(String) serviceRequest.getAttribute("strCodiceFiscale");
		strCognome=(String) serviceRequest.getAttribute("strCognome");
		strNome=(String) serviceRequest.getAttribute("strNome");
		datNasc   = (String) serviceRequest.getAttribute("datNasc");
		codComNas = (String) serviceRequest.getAttribute("codComNas");
		strComNas = (String) serviceRequest.getAttribute("strComNas");	
    }	

  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  String queryString = "AdapterHTTP&strCodiceFiscale="+strCodiceFiscale+"&page=CoopAnagDatiPersonaliPage&cdnFunzione="+_funzione;
%>


<html>
<head>
<title>Dati personali</title>

<link rel="stylesheet" href="../../css/stiliCoop.css" type="text/css">
<af:linkScript path="../../js/"/>


<%@ include file="../global/confrontaData.inc" %>

<SCRIPT TYPE="text/javascript">
  var objRifWindow;  

<!--
// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  

    
function cfIsChecked() {
  var flg = eval(document.forms[0].FLGCFOK);  
  if((flg.value!=null) && (flg.value=="S"))
  	nascondi("CF_NOcheck");
  else
  	mostra("CF_NOcheck");
}

function mostra(id){ 
  var div = document.getElementById(id);
  div.style.display="";
}

function nascondi(id){ 
  var div = document.getElementById(id);
  div.style.display="none";
}
function inserimentoScheda(){
<% if (opInsPossibile) {%>
	setWindowLocation("AdapterHTTP?page=CoopInserimentoMassivoDaCachePage&cdnFunzione=<%=_funzione%>&inserisci=true");
<% } else {%>
	alert("L'operazione di inserimento non e' possibile in quanto l'operatore non dispone di tutti i permessi necessari.");
<% }%>
}
function aggiornamentoScheda(){
<% if (opInsPossibile) {%>
	setWindowLocation("AdapterHTTP?page=CoopInserimentoMassivoDaCachePage&cdnFunzione=<%=_funzione%>&aggiorna=true&strcodicefiscale=<%=strCodiceFiscale%>");
<% } else {%>
	alert("L'operazione di aggiornamento non e' possibile in quanto l'operatore non dispone di tutti i permessi necessari.");
<% }%>
}
function chiudi(){
	setWindowLocation("AdapterHTTP?page=CoopChiudiSchedaLavoratorePage&cdnFunzione=<%=_funzione%>");
}
//-->
</SCRIPT>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<%@ include file="../global/apriControllaCF.inc" %>

</head>
<body class="gestione" onload="">

<%@ include file="_testataLavoratore.inc" %>
<%@ include file="_linguetta.inc" %>


<br><br>
<font color="red">
     <af:showErrors/>
</font>

<af:form method="POST" action="AdapterHTTP" name="Frm1" >

<%out.print(htmlStreamTop);%>
<table  class="main" border="0">
<tr>
    <td class="etichetta">Codice Fiscale&nbsp;</td>
    <td class="campo">
    	<af:textBox classNameBase="input"  title="Codice fiscale" required="true" type="text" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="21" maxlength="16" readonly="<%= String.valueOf(!canModify) %>" />
    	&nbsp;&nbsp;&nbsp;&nbsp;Validità C.F.&nbsp;
    <af:comboBox name="FLGCFOK" classNameBase="input" disabled="<%= String.valueOf(!canModify) %>" >
      <option value=""  <% if (  "".equalsIgnoreCase(strFlgcfOk) ) { %>SELECTED="true"<% } %> ></option>
      <option value="S" <% if ( "S".equalsIgnoreCase(strFlgcfOk) ) { %>SELECTED="true"<% } %> >Sì</option>
      <option value="N" <% if ( "N".equalsIgnoreCase(strFlgcfOk) ) { %>SELECTED="true"<% } %> >No</option>
    </af:comboBox>
    </td>
    <!-- SUBMIT -->
</tr>
<tr><td></td><td>
<div id="CF_NOcheck" style="display:">
  <table colspacing="0" cellspacing="0" border="0" width="100%"><tr><td align="left">Codice fiscale non controllato</td></tr></table>
</div>
</td></tr>
<script language="javascript">cfIsChecked();</script>

<tr>
    <td class="etichetta">Cognome&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input"  title="Cognome" required="true" type="text" name="strCognome" value="<%=strCognome%>" size="30" maxlength="30" readonly="<%= String.valueOf(!canModify) %>" />    
    </td>
</tr>
<tr>
    <td class="etichetta">Nome&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" title="Nome" required="true" type="text" name="strNome" value="<%=strNome%>" size="30" maxlength="30" readonly="<%= String.valueOf(!canModify) %>" /></td>
</tr>
<tr>
    <td class="etichetta">Sesso&nbsp;</td>
    <td class="campo">
    	<af:comboBox classNameBase="input" name="strSesso" required="true" title="sesso" disabled="<%= String.valueOf(!canModify) %>" >
          <OPTION value="M" <%if (strSesso.equals("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
          <OPTION value="F" <%if (strSesso.equals("F")) out.print("SELECTED=\"true\"");%>>F</OPTION>
        </af:comboBox>
    </td>
</tr>

<tr>
<td class="etichetta">Stato civile&nbsp;</td>
<td class="campo">
  <af:comboBox name="CODSTATOCIVILE"
        title="Stato civile"
        required="false"
        moduleName="M_GetStatiCivili"
        classNameBase="input"
        disabled="<%= String.valueOf( !canModify ) %>"
        selectedValue="<%= codstatoCivile %>" 
        addBlank="true" />
</td>
</tr>

<tr>
    <td class="etichetta">Data di Nascita&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" title="Data di nascita" type="date" required="true" name="datNasc" value="<%=datNasc%>" size="11" maxlength="12" validateOnPost="true" validateWithFunction="checkAnnoNas" readonly="<%= String.valueOf(!canModify) %>" /></td>
</tr>
<tr>
    <td class="etichetta">Comune di nascita&nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" title="Codice comune di nascita" 
                  type="text" name="codComNas" value="<%=codComNas%>" size="4" maxlength="4"
                  readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;    
    <af:textBox type="hidden" name="codComNasHid" value="<%=codComNas%>"/>
    <af:textBox type="text" classNameBase="input" 
                name="strComNas"  value="<%=strComNas%>" size="30" maxlength="50"
                readonly="<%= String.valueOf(!canModify) %>" title="comune di nascita" />&nbsp;*&nbsp;&nbsp;    
    </td>
</tr>
<tr>
    <td class="etichetta">Nazione di cittadinanza&nbsp;</td>
    <td class="campo">
        <af:textBox classNameBase="input" title="Cittadinanza" type="text" name="strNazione" value="<%=strNazione%>" 
       	 size="30" maxlength="40" required="true" readonly="<%= String.valueOf(!canModify) %>" />
	    <af:textBox title="Cittadinanza" classNameBase="input" type="text" name="codCittadinanza" value="<%=codCittadinanza%>" size="5" maxlength="5" readonly="true" />&nbsp;
   		<af:textBox type="text" classNameBase="input" name="strCittadinanza" value="<%=strCittadinanza%>" size="20" readonly="true"  />&nbsp;
    </td>
</tr>
<tr>
    <td class="etichetta">Seconda Nazione di cittadinanza&nbsp;</td>
    <td class="campo">
        <af:textBox classNameBase="input" type="text" name="strNazione2" value="<%=strNazione2%>" size="30" maxlength="40"
                  required="false" readonly="<%= String.valueOf(!canModify) %>" />
	    <af:textBox classNameBase="input" type="text" name="codCittadinanza2" value="<%=codCittadinanza2%>" size="5" maxlength="5" readonly="true" />&nbsp;
	    <af:textBox classNameBase="input" type="text" name="strCittadinanza2" value="<%=strCittadinanza2%>" size="20" readonly="true"  />&nbsp;
    </td>
</tr>

<tr>
   <td class="etichetta">Milite esente/assolto</td>
    <td class="campo">
      <af:comboBox name="flgMilite"  classNameBase="input"  disabled="<%= String.valueOf(!canModify) %>">
        <option value=""  <% if ( "".equalsIgnoreCase(flgMilite) )  { %>SELECTED="true"<% } %> ></option>
        <option value="S" <% if ( "S".equalsIgnoreCase(flgMilite) ) { %>SELECTED="true"<% } %> >Sì</option>
        <option value="N" <% if ( "N".equalsIgnoreCase(flgMilite) ) { %>SELECTED="true"<% } %> >No</option>
      </af:comboBox>
    </td>
</tr>
<tr><td colspan="2">
	<%
	SourceBean cache = (SourceBean)sessionContainer.getAttribute(it.eng.sil.module.coop.GetDatiPersonali.SCHEDA_LAVORATORE_COOP_ID);
	SourceBean testata = (SourceBean)cache.getAttribute("TESTATA");
	String provenienza = (String)testata.getAttribute("provenienza");
		
	if (provenienza.equals("LISTA_LAVORATORE") && canInsertLavInLoc) {
	%>
	<input type="button" class="pulsanti" value="Inserisci scheda lavoratore" onclick="inserimentoScheda()">
	<%} 
	  if (!provenienza.equals("LISTA_LAVORATORE") && canUpdateLavInLoc){%>
	<input type="button" class="pulsanti" value="Aggiorna scheda lavoratore" onclick="aggiornamentoScheda()">
	<%}%>
	<%if (canStampa) {%>
	<input type="button" class="pulsanti" value="Stampa" onclick="apriGestioneDoc('RPT_SCHEDA_LAVORATORE_COOP','','SLCOOP', null, false, true);">
	<%}%>
	<input type="button" class="pulsanti" value="Chiudi" onclick="chiudi()">
</td></tr>
</table>
<%out.print(htmlStreamBottom);%>



</af:form>
</body>
</html>
