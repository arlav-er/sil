<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*,
  it.eng.afExt.utils.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.User,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  java.util.GregorianCalendar,
  java.text.SimpleDateFormat,
  it.eng.sil.security.*

"%>
<%@ include file="../global/noCaching.inc"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%     

int cdnfunzione  	= SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
String strNumero	= "";
String strAnno		= "";

ProfileDataFilter filter = new ProfileDataFilter(user, "CMDatiGenConvPage"); 

PageAttribs attributi = new PageAttribs(user, "CMDatiGenConvPage"); 

boolean canInsert = false; 
String fieldCodConv = "true";

// variabili utile causa l'include del file _protocollazioneconvenzione.inc
boolean canSalvaStato = false;
String noButtonSalvaStato = "true";
  
if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
} else {
	canInsert = attributi.containsButton("INSERISCI"); 
}

SourceBean sb = (SourceBean)serviceResponse.getAttribute("CM_NUMERO_CONV.ROWS.ROW");
//numero della convenzione		
strNumero = ((BigDecimal)sb.getAttribute("NUMCONVENZIONE")).toString();

String prAutomatica     = "S";
String docInOut         = "I";
String docRif           = "Documentazione L68";
String docTipo          = "Convenzione";
BigDecimal numProtV     = null;
BigDecimal numAnnoProtV = null;
String dataOraProt      = ""; 
String datProtV     = "";
String oraProtV     = "";

Calendar oggi = Calendar.getInstance();
String giornoDB = Integer.toString(oggi.get(5));
if (giornoDB.length() == 1){
	giornoDB = '0' + giornoDB;
}
String meseDB = Integer.toString(oggi.get(2)+1);
if (meseDB.length() == 1){
  	meseDB = '0' + meseDB;
}
String annoDB = Integer.toString(oggi.get(1));
String dataOdierna = giornoDB + "/" + meseDB + "/" + annoDB;

String CODSTATOATTO = StringUtils.getAttributeStrNotNull(serviceRequest, "CODSTATOATTO");

//anno di riferimento		
strAnno = StringUtils.getAttributeStrNotNull(serviceRequest, "anno");

String prgAzienda	= StringUtils.getAttributeStrNotNull(serviceRequest, "prgAzienda");
String prgUnita		= StringUtils.getAttributeStrNotNull(serviceRequest, "prgUnita");
String codiceFiscale = "";
String pIva = "";
String ragSociale = "";
String indirizzo = "";
boolean menuContext = false;
   
if (!prgAzienda.equals("")) {
	SourceBean infoAzienda = (SourceBean) serviceResponse.getAttribute("CM_GetInfoUnitaAzienda.ROWS.ROW");
	codiceFiscale  = StringUtils.getAttributeStrNotNull(infoAzienda, "strCodiceFiscale");
	pIva 		   = StringUtils.getAttributeStrNotNull(infoAzienda, "strPartitaIva");
	ragSociale 	   = StringUtils.getAttributeStrNotNull(infoAzienda, "strRagioneSociale");
	indirizzo = StringUtils.getAttributeStrNotNull(infoAzienda, "strIndirizzo");
	menuContext = true;
}   
String CODSTATOCONV = StringUtils.getAttributeStrNotNull(serviceRequest, "CODSTATOCONV");


String PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVINCIA_ISCR");

String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);

String htmlStreamTopAz = StyleUtils.roundTopTable(!menuContext);
String htmlStreamBottomAz = StyleUtils.roundBottomTable(!menuContext);
%>
<html>

<head>
  <title>Inserimento nuova convenzione</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  
<SCRIPT language="Javascript">

  var imgChiusa = "../../img/chiuso.gif";
  var imgAperta = "../../img/aperto.gif";
  var opened;

  function aggiornaAzienda(){
	document.Frm1.RagioneSociale.value = opened.dati.ragioneSociale;
    document.Frm1.piva.value = opened.dati.partitaIva;
    document.Frm1.cf.value = opened.dati.codiceFiscaleAzienda;
    document.Frm1.Indirizzo.value = opened.dati.strIndirizzoAzienda + " (" + opened.dati.comuneAzienda + ")";
    document.Frm1.prgAzienda.value = opened.dati.prgAzienda;
    document.Frm1.prgUnita.value = opened.dati.prgUnita;
	opened.close();
    var imgV = document.getElementById("tendinaAzienda");
	var divAz= document.getElementById("aziendaSez");
	divAz.style.display = "inline";
    imgV.src=imgAperta;
  }

  function calcolaDurata() {

  	var mesi = "";
  	if (!validateDate('dataConv')) {
  	  document.Frm1.durata.value = mesi;
  	  return false;
  	} 
  	var strData1=document.Frm1.dataConv.value;
  	if (!validateDate('dataScad')) {
  	  document.Frm1.durata.value = mesi;
  	  return false;
  	}
  	var strData2=document.Frm1.dataScad.value;
  	
  	if(strData2 != "" && strData1 != "") {
	  
	  //costruisco la data inizio
  	  var d1giorno=parseInt(strData1.substr(0,2),10);
  	  var d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
      var d1anno=parseInt(strData1.substr(6,4),10);
  	  var data1=new Date(d1anno, d1mese, d1giorno);
	  
	  //costruisce la data di scadenza
  	  var d2giorno=parseInt(strData2.substr(0,2),10);
   	  var d2mese=parseInt(strData2.substr(3,2),10)-1;
  	  var d2anno=parseInt(strData2.substr(6,4),10);
      var data2=new Date(d2anno, d2mese, d2giorno);
      
      if (data2 < data1) {
      	document.Frm1.durata.value = mesi;
      	alert("La "+ document.Frm1.dataScad.title +" è precedente alla "+ document.Frm1.dataConv.title);
	    return false;
   	  }
   	  else {
   	  	var mesi;
   	  	var data1time = data1.getTime(); 
  		var data2time = data2.getTime(); 
  		var difftime = Math.abs(data1time-data2time); 
  		mesi = parseInt(difftime/1000/60/60/24/30);
		document.Frm1.durata.value = mesi;
		return true;
   	  }  	
  	}
  	else {
  		document.Frm1.durata.value = mesi;
  		return true;
  	}
  }

  function verificaDati() {
    if( document.Frm1.prgAzienda.value == "" ) {
      alert("Selezionare una Azienda con il pulsante Ricerca!");
      return false;
    }
	else {
		if (calcolaDurata())
			return true;
		else return false;
  	}
  }
  
  function azzeraAzienda(){
  	<%if(!menuContext) {%>
	document.Frm1.RagioneSociale.value = "";
    document.Frm1.piva.value = "";
    document.Frm1.cf.value = "";
    document.Frm1.Indirizzo.value = "";
    document.Frm1.prgAzienda.value = "";
    document.Frm1.prgUnita.value = "";
    var imgV = document.getElementById("tendinaAzienda");
	var divAz= document.getElementById("aziendaSez");
	divAz.style.display = "none";
    imgV.src=imgChiusa;
    <%} %>
  }  


// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


function fieldChanged() {
    <%out.print("flagChanged = true;");%>
}

function goBack() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
<%  	
	String goBackButtonTitle = "";
		
	if (serviceRequest.containsAttribute("goBackListaPage")) {
	
	String goBackListaPage = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "goBackListaPage");
	String token = "_TOKEN_" + goBackListaPage;
	String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
	goBackButtonTitle = "Torna alla lista";
%>
	setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
<%} else if (serviceRequest.containsAttribute("goBackRicercaPage")) {
		String goBackRicercaPage = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "goBackRicercaPage");		
		goBackButtonTitle = "Torna alla ricerca";
%>
		var url = "AdapterHTTP?PAGE=<%=goBackRicercaPage %>" + "&cdnfunzione=<%=cdnfunzione%>";
		<%if (!prgAzienda.equals("")) {%>
			url+="&prgAzienda=<%=prgAzienda%>";
			url+="&prgUnita=<%=prgUnita%>";
		<%}%>
		setWindowLocation(url);
<%} %>
}

function cambia(immagine, sezione) {
	if (sezione.aperta==null) sezione.aperta=true;
	if (sezione.aperta) {
		sezione.style.display="none";
		sezione.aperta=false;
		immagine.src=imgChiusa;
	    immagine.alt="Apri";
	} else {
		sezione.style.display="inline";
		sezione.aperta=true;
		immagine.src=imgAperta;
	    immagine.alt="Chiudi";
	}
}

function apriSelezionaSoggetto(soggetto, funzionediaggiornamento) {
	var f = "AdapterHTTP?PAGE=MovimentiSelezionaSoggettoPage&MOV_SOGG=" + soggetto + "&AGG_FUNZ=" + funzionediaggiornamento + "&CDNFUNZIONE=" + <%=cdnfunzione%>;
 	var t = "_blank";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
    opened = window.open(f, t, feat);
}
	
</script>
</head>

<body class="gestione" onload="rinfresca()">

<p align="center" class="titolo">Inserimento nuova convenzione</p>
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="verificaDati()" onReset="azzeraAzienda()">

<br>  
<%out.print(htmlStreamTopAz);%>
<table class="main" border="0">
	<tr class="note">
    	<td colspan="2">
          <div class="sezione2">
            <img id='tendinaAzienda' alt="Chiudi" src="../../img/aperto.gif" onclick="cambia(this,'aziendaSez');"/>&nbsp;&nbsp;&nbsp;Azienda
          &nbsp;&nbsp;
          <% if (!menuContext){%>
            <a href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
          	&nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
          <%}%>
          </div>
          </td>
    </tr>
    <tr>
        <td colspan="2">
            <div id="aziendaSez" style="display:<% if (!menuContext){%>none<%}%>">
              <table class="main" width="100%" border="0">
				      <tr>
				        <td class="etichetta">Codice Fiscale</td>
				        <td class="campo" colspan="3">
				        	<af:textBox type="text" name="cf" value="<%=codiceFiscale %>"
            				readonly="true" classNameBase="input" size="30" maxlength="16"/>
				        </td>
				      </tr>      
				      <tr>
				        <td class="etichetta">Partita IVA</td>
				        <td class="campo" colspan="3">
				   			<af:textBox type="text" name="piva" value="<%=pIva %>"
            				readonly="true" classNameBase="input" size="30" maxlength="11"/>       
				        </td>
				      </tr>		      
				      <tr>
				        <td class="etichetta">Ragione Sociale</td>
				        <td class="campo" colspan="3">
				        	<af:textBox type="text" name="RagioneSociale" value="<%=ragSociale %>"
            				readonly="true" classNameBase="input" size="60" maxlength="100"/> 
				        </td>
				      </tr>
				      <tr>
				        <td class="etichetta">Indirizzo (Comune)</td>
				        <td class="campo" colspan="3">
				        	<af:textBox type="text" name="Indirizzo" value="<%=indirizzo %>"
            				readonly="true" classNameBase="input" size="60" maxlength="100"/> 
				        </td>
				      </tr>     
				</table>
    		</div>
    	</td>
    </tr>
</table>
<%out.print(htmlStreamBottomAz);%>

<%if (serviceRequest.containsAttribute("messageError")) { %>
	<ul><li>Errore in fase di inserimento</li></ul>
<%} %>

<%out.print(htmlStreamTop);%>
<table class="main" border="0" cellpadding="0" cellspacing="0" width="100%">    
<%@ include file="_protocollazioneconvenzione.inc" %>
	<tr valign="top">
	    <td class="etichetta2">Anno</td>
	    <td class="campo2">
            <af:textBox classNameBase="input" type="integer" 
                        onKeyUp="fieldChanged();"
                        name="anno" value="<%=strAnno%>"
                        readonly="true" disabled="true"
                        maxlength="4" size="10"/>
	    	&nbsp;&nbsp;Numero&nbsp;&nbsp;       
            <af:textBox classNameBase="input" type="integer"
		                onKeyUp="fieldChanged();"
		                name="numero" value="<%=strNumero%>"
		                readonly="true" disabled="true"
		                maxlength="10" size="10"/>
        </td>          
    </tr>
    <tr valign="top">
    	<td>&nbsp;</td>
    </tr>
   	<tr>
	    <td class="etichetta">Ambito Territoriale</td>
	    <td colspan=3 class="campo">
	    	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
	        	classNameBase="input" addBlank="true" required="true" />
	    </td>
	</tr>
    <tr valign="top">
    	<td>&nbsp;</td>
    </tr>
	<tr valign="top">
	    <td class="etichetta2">Data convenzione</td>
	    <td class="campo2" colspan="3">
	        <af:textBox classNameBase="input" title="Data convenzione" type="date" 
			        	validateOnPost="true" onKeyUp="fieldChanged();" 
			        	name="dataConv" size="11" maxlength="10" 
			        	readonly="false" required="true" />
      		&nbsp;&nbsp;Data Scadenza&nbsp;&nbsp;    
	        <af:textBox classNameBase="input"
	                    title="Data scadenza"
	                    readonly="false"
	                    type="date"
	                    validateOnPost="true"
	                    name="dataScad"
	                    size="11"
	                    maxlength="10"
	                    onKeyUp="fieldChanged();"/>
	    	&nbsp;&nbsp;Durata&nbsp;&nbsp;
	        <af:textBox classNameBase="input" type="integer"
		                onKeyUp="fieldChanged();"
		                name="durata"
		                maxlength="10" size="10"/>
		    <A onClick="calcolaDurata();"><IMG name="image" border="0" src="../../img/calc.gif" title="calcola durata convenzione"/></a>
      	</td>
    </tr>
    <tr valign="top">
    	<td>&nbsp;</td>
    </tr>
	<tr valign="top">
	    <td class="etichetta2">Stato convenzione</td>
	    <td class="campo2">
	    	<af:comboBox classNameBase="input" onChange="fieldChanged();" name="statoConv" disabled="<%=fieldCodConv%>" selectedValue="<%=CODSTATOCONV %>" moduleName="CM_MULTI_LIST_CONVENZIONI"/>
	      	&nbsp;&nbsp;Copertura&nbsp;&nbsp; 
	    	<af:comboBox 
	          name="copertura"
	          classNameBase="input"
	          onChange="fieldChanged();">					  
		          <option value=""></option>
		          <option value="I">Copertura dell'intera</option>
		          <option value="P">Quota di riserva o parziale</option>
	        </af:comboBox>
	    </td>
    </tr>   
    <tr valign="top">
    	<td>&nbsp;</td>
    </tr>
	<tr valign="top">
	    <td class="etichetta2">Num. lav. coinvolti</td>
	    <td class="campo2">
            <af:textBox onKeyUp="fieldChanged();"
		                name="lavoratori"
            			title="Num. lav. coinvolti"
            			classNameBase="input" type="integer"
		                maxlength="10" size="10"
						validateOnPost="true"/>
		    &nbsp;&nbsp;Num. rinuncia disabili&nbsp;&nbsp;            
            <af:textBox classNameBase="input" type="integer"
		                onKeyUp="fieldChanged();"
		                title="Num. rinuncia disabili"
		                name="rinunciaDis"
		                maxlength="10" size="10"
						validateOnPost="true"/>
	    	&nbsp;&nbsp;Num. rinuncia Art. 18&nbsp;&nbsp;
            <af:textBox classNameBase="input" type="integer"
		                onKeyUp="fieldChanged();"
		                title="Num. rinuncia Art. 18"
		                name="rinunciaArt18"
		                maxlength="10" size="10"
						validateOnPost="true"/>
	    </td>
    </tr>    
	<tr valign="top">
    	<td>&nbsp;</td>
    </tr>
	<tr valign="top">
      	<td class="etichetta2">Prorogata</td>
      	<td class="campo2">
	        <af:comboBox 
	          name="proroga"
	          classNameBase="input"
	          onChange="fieldChanged()">					  
		          <option value=""></option>
		          <option value="S">Sì</option>
		          <option value="N">No</option>
	        </af:comboBox>
      		&nbsp;&nbsp;Modificata&nbsp;&nbsp;     	
	        <af:comboBox 
	          name="modifica"
	          classNameBase="input"
	          onChange="fieldChanged()">					  
		          <option value=""></option>
		          <option value="S">Sì</option>
		          <option value="N">No</option>
	        </af:comboBox>
      	</td>
    </tr>
	<tr valign="top">
    	<td>&nbsp;</td>
    </tr>    
	<tr valign="top">
		<td class="etichetta2">Note</td>
    	<td class="campo2">
          	<af:textArea cols="70" rows="4" maxlength="1000" readonly="false" classNameBase="input"  
              		name="STRNOTE"
               		value="" validateOnPost="true" 
                    required="false" title="Note"/>    	
    	</td>
    </tr>    
</table>				    
<input type="hidden" name="PAGE" value="CMDatiGenConvPage"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnfunzione%>"/>
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
<input type="hidden" name="prgAziendaApp" value="<%=prgAzienda%>" />

<% if (serviceRequest.containsAttribute("goBackListaPage")) {%>
	<input type="hidden" name="goBackListaPage" value="CMConveListaPage" />
<% } else if (serviceRequest.containsAttribute("goBackRicercaPage")) {%>
	<input type="hidden" name="goBackRicercaPage" value="CMConveRicercaPage" />
<% }%>
<br>
<br>
<table>	
	<% if (canInsert) {%>
	<tr>
	  	<td align="right" colspan="2">
			<input class="pulsanti" type="submit" name="inserisci" value="Inserisci"/>
			&nbsp;&nbsp;&nbsp;&nbsp; 
			<input type="reset" name="reset" class="pulsanti" value="Annulla"/>
		</td>
	</tr>
	<% } %>
	<tr><td>&nbsp;</td></tr>	
	<tr>
	  	<td align="center">
			<input type="button" class="pulsante" name="back" value="<%=goBackButtonTitle%>" onclick="goBack()" />
		</td>
	</tr>
</table>
</af:form>
<%out.print(htmlStreamBottom);%>
<br/>
<p align="center">

</p>
<br/>

</body>
</html>


