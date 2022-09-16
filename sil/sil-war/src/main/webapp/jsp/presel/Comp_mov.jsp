<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.util.Enumeration.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

String prgMovimento="",
        prgAzienda="",
        strRagioneSociale="",
        DataInizio="",
        DataFine="",
        flgDisponibile= "",
        codMansione="",
        desMansione="",
        STRDESCRIZIONE = "",
        codcontratto = "",
        prgMansione = "",
        datiniziomov = "",
        datfinemov = "",
        annofinemov ="",
        Page= "",
        I_PR_MAN = "",
        Mansione="";

  String assMov_EspNonLav = StringUtils.getAttributeStrNotNull(serviceRequest,"AssMov_EspNonLav");
        
  String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
  
  String StringSize= ((String) serviceRequest.getAttribute("SIZE"));
  String check ="";
 
  SourceBean risposta = new SourceBean("ROWS");

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
 

  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canInsert = attributi.containsButton("inserisci");
  boolean canDelete = attributi.containsButton("rimuovi");
  boolean canModify = attributi.containsButton("salva");


    Hashtable combo = new Hashtable();
    String Cod ="";
    
  String ragSociale   = "";
  String cfAzienda    = "";
  String partitaIVA   = "";
  String natGiuridica = "";
  String codAteco     = "";
  String codCaomAz    = "";
  String indirizzoAz  = "";
  
  int SIZE = new Integer (StringSize).intValue();
  boolean unicaAzienda = true;
  boolean documentata  = true;
  String cfPrimaAzienda = "";
  //Creo una tabella in cui inserisco le mansioni selezionate
  int j = 0;
  for (int i=0; i < SIZE; i++) {
      check =  (String) serviceRequest.getAttribute("I_PR_MAN" + i);
      if (check!=null && check.equals("on")) {
         combo.put( serviceRequest.getAttribute("CODMANSIONE_" + i), serviceRequest.getAttribute("DESCMANSIONE_" + i));

         if ( !documentata && !((String) serviceRequest.getAttribute("CODMONOMOVDICH_" + i)).equalsIgnoreCase("C") ) 
            documentata = true;
            
         //controllo se le mansioni selezionate si riferiscono tutte ad una stessa azienda
         if( unicaAzienda)
         { if(cfPrimaAzienda=="") 
           { cfPrimaAzienda = (String) serviceRequest.getAttribute("STRCODFISCALEAZIENDA_" + i);
             j=i;
           }
           if (cfPrimaAzienda.equals( serviceRequest.getAttribute("STRCODFISCALEAZIENDA_" + i) ) ) unicaAzienda = true;
           else unicaAzienda = false;
         }
      }
  }//for
  
  //Se l'azienda è uguale per tutte le mansioni la passiamo come parametro
  if(unicaAzienda) { 
     ragSociale   = Utils.notNull(serviceRequest.getAttribute("STRRAGSOCIALEAZIENDA_"+ j ));
     cfAzienda    = Utils.notNull(serviceRequest.getAttribute("STRCODFISCALEAZIENDA_"+ j ));
     partitaIVA   = Utils.notNull(serviceRequest.getAttribute("STRPARTITAIVAAZIENDA_"+ j ));
     natGiuridica = Utils.notNull(serviceRequest.getAttribute("CODNATGIURIDICA_"+ j ));
     codAteco     = Utils.notNull(serviceRequest.getAttribute("CODATECO_"+ j ));
     codCaomAz    = Utils.notNull(serviceRequest.getAttribute("CODCOMAZIENDA_"+ j ));
     indirizzoAz  = Utils.notNull(serviceRequest.getAttribute("STRINDIRIZZOAZIENDA_"+ j ));
  }


  //Costruisco la combo con le mansioni selezionate creando un sourceBean che chiamo GetMansioni
  //e che poi richiamo nella TAG af:comboBox
  Enumeration listaCodici = combo.keys() ;
  while ( listaCodici.hasMoreElements() ) {
      SourceBean row = new SourceBean("ROW");
      Cod = (String) listaCodici.nextElement();
      row.setAttribute("Codice", Cod);
      row.setAttribute("Descrizione", (String) combo.get(Cod));
      risposta.setAttribute(row);
  }
  serviceResponse.setAttribute ("Get_mansioni" , risposta );

  String ControllaCdn="";
  Vector rowDispo = serviceResponse.getAttributeAsVector("M_GETLAVCM.ROWS.ROW");
  if (rowDispo.size()>0) {
	SourceBean sb1 = (SourceBean)rowDispo.get(0);
	ControllaCdn = sb1.containsAttribute("CONTROLLACDN")? sb1.getAttribute("CONTROLLACDN").toString():"";
  }

  //Servono per caricare lo stile della tabella
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);

%>

<html>
<head>

<title>
Determinazione mansione a periodo
</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>

<style type="text/css">
img {border:0;width:18;heigth:18}
td.campo3 {font-weight: bold}
</style>



<script>
<!--
var inputs = new Array(0);
var j=0;

function tutti(el) {
  for (j=0;j<inputs.length;j++) {
	inputs[j].checked=el.checked;          
  }
}

var varMansione = null;
var varDesc = null;
var varTipo = null;

function ResetMovimenti() {
  window.close();
}
  
function carica(page, module, target) {
  if (controllaFunzTL()) {
    document.Frm1.PAGE.value =page;
    document.Frm1.MODULE.value =module;
    document.Frm1.target =target;
    doFormSubmit(document.Frm1);
    window.close();
  }
  else return false;
}

function carica1 (page, module, target) {
  if (controllaFunzTL()) {
	document.Frm1.PAGE.value =page;
    document.Frm1.MODULE.value =module;
    document.Frm1.target =target;
    doFormSubmit(document.Frm1);
  }
  else return false;
}
function setValues(id, desc, tipo) {
  varMansione.item(0).value=id;
  varDesc.item(0).value = desc;
  varTipo.item(0).value = tipo;
}

function ricercaAvanzataMansioniConCodice2(objCodMan, objDesc, objTipo) {
  varMansione = document.getElementsByName(objCodMan);
  varDesc = document.getElementsByName(objDesc);
  varTipo = document.getElementsByName(objTipo); 
  var urlPage = "AdapterHTTP?PAGE=RicercaMansioneAvanzataConCodicePage&RICERCA2=2";
  urlPage +="&_codMansioneItem="+objCodMan;
  urlPage +="&_descMansioneItem="+objDesc;
  urlPage +="&_tipoMansioneItem="+objTipo;    
  window.open(urlPage, "Mansioni", 'toolbar=0, scrollbars=1');
}

// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


function apriMovimenti(page) {
    var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
	urlpage+="CDNFUNZIONE=<%=_funzione %>&";	
	urlpage+="PAGE="+page;
	var fin = window.open(urlpage,"Associazioni", ' width=980 ,height=450, scrollbars=1, resizable=1'); 
    fin.moveTo (20,100);
}

function apriMovimentiTir(page) {
    var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
	urlpage+="CDNFUNZIONE=<%=_funzione %>&";
	urlpage+="AssMov_EspNonLav=1&";	
	urlpage+="PAGE="+page;
	var fin = window.open(urlpage,"Associazioni", ' width=980 ,height=450, scrollbars=1, resizable=1'); 
    fin.moveTo (20,100);
}


function calcolaMesi(inputName) {

  var annoInizio=document.forms[0].NUMANNOINIZIO_0.value;
  var annoFine=document.forms[0].NUMANNOFINE_0.value;
  var meseInizio=document.forms[0].numMeseInizio_0.value;
  var meseFine=document.forms[0].numMeseFine_0.value;
  //var mesi=document.forms[0].numMesi.value;
  var mesi="";
  
  if (!(annoInizio>=1900 && annoInizio<=2100)){
	alert ("L'anno di inizio deve essere compreso tra 1900 e 2100");
	return false;
  }
  if (!(annoFine>=1900 && annoFine<=2100) && annoFine!=""){
	alert ("L'anno di fine deve essere compreso tra 1900 e 2100");
	return false;
  }
  if (!(meseInizio>=1 && meseInizio<=12) && meseInizio!=""){
	alert ("Il mese di inizio deve essere compreso tra 1 e 12");
	return false;
  }
  if (!(meseFine>=1 && meseFine<=12) && meseFine!=""){
	alert ("Il mese di fine deve essere compreso tra 1 e 12");
	return false;
  }
  if (!(mesi>=0)){
	alert ("La durata in mesi non deve essere negativa");
	return false;
  }

  if (annoInizio!="" && annoFine!="" && meseInizio!="" &&  meseFine!="") {
        var dataInizio=new Date();
        dataInizio.setFullYear(annoInizio, meseInizio-1, 1);
        var dataFine=new Date();
        dataFine.setFullYear(annoFine, meseFine-1, 1);
        var diff=dataFine - dataInizio //differenza in millisecondi
        mesi=Math.round(diff/2592000000);  //diff fratto i millisecondi di un mese (!)      
  }
        
  document.forms[0].numMesi_0.value=mesi;
  return true;
}

function checkLavCm(flagDispo) {
  <% if (ControllaCdn.equals("")) {%>
  	if (eval("document.Frm1."+flagDispo+".value") == 'L') {
		alert("Il lavoratore non è in collocamento mirato!");
		eval("document.Frm1."+flagDispo+".value=''");
	}	
  <% }%>
}
  //-->
  
</script>
  <%@ include file="Mansioni_CommonScripts.inc" %>

</head>
<body class="gestione">
<br>
<center> 



<p class=titolo>Determinazione mansione a periodo</p><br>
<af:form name="Frm1" action="AdapterHTTP" target="main" dontValidate="true">

<%out.print(htmlStreamTop);%>
<table class="main">
    <tr><td colspan="2">
          <table style="border-collapse:collapse" width="100%">
              <tr style="border: 1 solid #000080; color: #000080;  font-size: 12px;font-weight: normal;text-align: center;vertical-align:middle">
                  <td style="border: 1 solid;border-right: none"  align=center>Mansioni</td>
              </tr>
          </table>
      </td>
    <tr>
    

<%if(documentata){%>
    <tr><td colspan="2">Mansione documentata</td></tr> 
<%} else {%>
    <tr><td colspan="2">Mansione non documentata</td></tr> 
<%}%>


<%
  //Se l'azienda è unica allora la passo come parametro
  if(unicaAzienda) { %>
    <tr><td class="etichetta">Unica azienda</td>
        <td class="campo"><strong><%= ragSociale  %></strong></td>
    </tr>
     <af:textBox classNameBase="input" type="hidden" name="STRRAGSOCIALEAZIENDA_0" value="<%=ragSociale%>" readonly="true"/>
     <input type="hidden" name="STRCODFISCALEAZIENDA_0" value="<%= cfAzienda   %>"/>
     <input type="hidden" name="STRPARTITAIVAAZIENDA_0" value="<%= partitaIVA  %>"/>
     <input type="hidden" name="CODNATGIURIDICA_0"      value="<%= natGiuridica%>"/>
     <input type="hidden" name="CODATECO_0"             value="<%= codAteco    %>"/>  
     <input type="hidden" name="CODCOMAZIENDA_0"        value="<%= codCaomAz   %>"/>
     <input type="hidden" name="STRINDIRIZZOAZIENDA_0"  value="<%= indirizzoAz %>"/>
<%}%>

    <tr><td class="etichetta">Mansioni</td>
        <td class="campo">
          <af:comboBox name="CODMANSIONE_0"
                       title="Tipi"
                       moduleName="Get_mansioni" />
        </td>
    </tr>
        
    <tr><td class="etichetta">Tipi di rapporto</td>
        <td class="campo">
          <%if(assMov_EspNonLav.equals("")){%>
          		<af:comboBox name="CODCONTRATTO_0"
                       title="Tipi di rapporto"
                       addBlank="true"
                       required="true"
                       moduleName="M_GetTipiContratto" />
          <%}else{%>
          		<af:comboBox name="CODCONTRATTO_0"
                       title="Tipi di rapporto"
                       addBlank="true"
                       required="true"
                       moduleName="M_GetTipiContrattoTirocinio" />          
          <%}%>
        </td>
    </tr>
    <tr><td class="etichetta">Mese/anno di inizio</td>
        <td class="campo">
            <af:textBox classNameBase="input"  title="Mese di inizio" type="Integer" name="numMeseInizio_0" size="2" maxlength="2" value="" readonly="false" />&nbsp;/&nbsp;
            <af:textBox classNameBase="input"  title="Anno di inizio" type="Integer" name="NUMANNOINIZIO_0" size="4" required="True" maxlength="4"  value=""   readonly="false"  />
        </td>
    </tr>
    <tr><td class="etichetta">Mese/anno di fine</td>
        <td class="campo">
            <af:textBox classNameBase="input" title="Mese di fine" type="Integer" name="numMeseFine_0" size="2" maxlength="2" value=""   readonly="false"   />&nbsp;/&nbsp;
            <af:textBox classNameBase="input" title="Anno di fine" type="Integer" name="NUMANNOFINE_0" size="4" maxlength="4" value=""   readonly="false"   />
            <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="hidden" name="numMesi_0" size="3" value="" validateWithFunction="calcolaMesi" />
        </td>
    </tr>

    <tr><td class="etichetta">Disponibile a lavorare con la mansione ?</td>
		<td class="campo">
          <af:comboBox 
            title="Disponibile a lavorare con la mansione"
            name="FLGDISPONIBILE_0"
            classNameBase="input"
            disabled="false"
            onChange="checkLavCm(this.name)">
	          <option value=""  <% if ( "".equals(flgDisponibile) )   { out.print("SELECTED=\"true\""); } %> ></option>
	          <option value="P" <% if ( "P".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Preselezione ordinaria</option>
	          <option value="L" <% if ( "L".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Legge 68</option>
	          <option value="S" <% if ( "S".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Entrambe</option>
	          <option value="N" <% if ( "N".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %> >Nessuna</option>
          </af:comboBox>
        </td>
    </tr>

      <input type="hidden" name="PAGE" value="CurrEspLavMainPage"/>
      <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
      <input type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>"/>
      <input type="hidden" name="MODULE" value="M_GetPrgMansioni"/>
      <input type="hidden" name="SIZE" value="1"/>
      <input type="hidden" name="I_PR_MAN0" value="on"/>
      <input type="hidden" name="desMansione_0" value=""/>
      <input type="hidden" name="PRGMOVIMENTO_0" value=""/>
      <input type="hidden" name="FLAG" value="1"/>
      <input type="hidden" name="SIZE_ASS" value="<%= SIZE %>"/>
	  <% if(!assMov_EspNonLav.equals("")){ %>
	  	<input type="hidden" name="AssMov_EspNonLav" value="<%= assMov_EspNonLav %>"/>
	  <%}%>

<%  for (int i=1 ; i<SIZE+1; i++) { 
        prgMovimento = (String) serviceRequest.getAttribute("PRGMOVIMENTO_"+(i-1));
        I_PR_MAN = (String) serviceRequest.getAttribute("I_PR_MAN"+(i-1));
%>
        <input type="hidden" name="PRGMOVIMENTO_<%=i%>" value="<%= prgMovimento %>"/>
        <input type="hidden" name="I_PR_MAN<%=i%>" value="<%= I_PR_MAN %>"/>
<% } %>

    <tr><td>&nbsp;</td></tr>
    <tr>
      <td colspan="2" align="center">       
         <%if(assMov_EspNonLav.equals("")){%>
         	<input class="pulsante" type="button" name="inserisci" value="Riporta in una esperienza " onclick="carica('CurrEspLavMainPage', 'M_GetPrgMansioni', 'main')">
         	<input class="pulsanti" type="button" onClick="apriMovimenti('AssMov_EspLavPage')" value="Torna indietro"/>
         <%}else{%>
         	<input class="pulsante" type="button" name="inserisci" value="Riporta in una esperienza " onclick="carica('CurrTirociniMainPage', 'M_GetPrgMansioni', 'main')">
         	<input type="button" class="pulsanti" onClick="apriMovimentiTir('AssMov_EspLavPage')" value="Torna indietro"/>
         <%}%>
      </td>
    </tr>
    <tr>
      <td colspan="2" align="center">
         <input class="pulsante" type="button" name="annulla"  value="Chiudi" onclick="javascript: window.close()">
      </td>
    </tr>

</table>                    
<%out.print(htmlStreamBottom);%>

</af:form>
</body>
</html>
