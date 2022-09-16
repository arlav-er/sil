<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>

<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,it.eng.sil.security.User,java.util.*,java.math.*,java.io.*,it.eng.afExt.utils.StringUtils,com.engiweb.framework.security.*,com.engiweb.framework.util.JavaScript,it.eng.sil.util.*,it.eng.sil.security.PageAttribs"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%

String cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");
String assMov_EspNonLav = StringUtils.getAttributeStrNotNull(serviceRequest,"AssMov_EspNonLav");

String prgMovimento="",
		prgMovimentoRigaPrec="",
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
        mesefinemov ="",
        Page= "",
        mansione="",
        codMonoMovDich = "";

	int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	int countMov = 0;
	
	SourceBean row_Curr = null;
	Vector rows;
	if(assMov_EspNonLav.equals("")){
		rows = serviceResponse.getAttributeAsVector("AssMov_EspLav.ROWS.ROW");
	}else{
		rows = serviceResponse.getAttributeAsVector("M_AssMov_EspNonLav.ROWS.ROW");
	}
	SourceBean row = null;
    PageAttribs attributi = new PageAttribs(user, "MansioniPage");
    boolean canInsert = attributi.containsButton("inserisci");
    boolean canDelete = attributi.containsButton("rimuovi");
    boolean canModify = attributi.containsButton("salva");
    boolean nuovo = true;


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
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Lista Movimenti</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<af:linkScript path="../../js/" />

<style type="text/css">
img {
	border: 0;
	width: 18;
	heigth: 18
}

td.campo3 {
	font-weight: bold
}
</style>

<script>
<!--
	var inputs = new Array(0);
	var j=0;
	var msgMansione = "Non è stata selezionata nessuna mansione";
	
	function tutti(el) {    
	  for (j=0;j<inputs.length;j++) {
      	inputs[j].checked=el.checked;          
      }
	}
  
	var varMansione = null;
	var varMansioneOld = null;
	var varDesc = null;
	var varTipo = null;
	var indiceRitorno=0;

    function ResetMovimenti() { 
      window.close();
    }
  
    function atLeastOneCheck()
    {   //controllo che almeno una mansione sia selezionata
      var isCheck = false;
      var j=0; //var str="";
      do { 
        if(inputs[j].checked) {            
        	isCheck = true;
        }
        j++;
      } while(j<inputs.length);
   	  if (!isCheck) {
   	   	  msgMansione = "Non è stata selezionata nessuna mansione";
   	  }
      return isCheck;
    }
   
	function carica (page, module, target, isclosing) {
      if (controllaFunzTL()) {
        document.Frm1.PAGE.value =page;
        document.Frm1.MODULE.value =module;
        document.Frm1.target =target;
          
        if(riportaControlloUtente( atLeastOneCheck() ) ) {
        	//riportaMansioneOldInMansNew1();
			doFormSubmit(document.Frm1);
			if (isclosing == 1){
				window.close();
			}
        }
        else
        { alert(msgMansione);
          return false;
        }
      }
      else return false;
	}
	
	function setValues(id, desc, tipo, provenienza) {
	  mansioneName="DESCMANSIONETEXT_"+indiceRitorno;
	  objMansione = eval("document.forms[0]." + mansioneName);
	  //toglie il codice solo se proveniamo dagli alberi
	  //objMansione.value=(provenienza=='albero')?desc.substr(9):desc;
	  codMansioneName="CODMANSIONE_"+indiceRitorno;
	  objCodMansione=eval("document.forms[0]."+codMansioneName);
	 // var countCombo = varMansioneOld.options.length;
	  if (desc != "") {
		  if (desc.length >= 50) {
			  desc = desc.substr(0,47) + "...";
		  }
	  } 
	  varMansione.item(0).value=id;
      varDesc.item(0).value = desc;
      varTipo.item(0).value = tipo;
	//  varMansioneOld.options[countCombo] = new Option(desc, id, true, true);
	//  var indice = vettCodMansOld.length;
	//  vettDescMansOld[indice] = desc;
	//  vettCodMansOld[indice] = id;
    }

  
	function ricercaAvanzataMansioniConCodice2(objCodMan, objDesc, objTipo) {
	  varMansione = document.getElementsByName(objCodMan);
	  varDesc = document.getElementsByName(objDesc);
	  varTipo = document.getElementsByName(objTipo); 
	  var urlPage = "AdapterHTTP?PAGE=RicercaMansioneAvanzataConCodicePage&RICERCA2=2";
	  urlPage +="&_codMansioneItem="+objCodMan;
	  urlPage +="&_descMansioneItem="+objDesc;
	  urlPage +="&_tipoMansioneItem="+objTipo;
	  urlPage +="&FLGIDO=S"; 
	  window.open(urlPage, "Mansioni", 'toolbar=0, scrollbars=1');
	}

	// Per rilevare la modifica dei dati da parte dell'utente
	var flagChanged = false;  

	function fieldChanged() {
	  //non faccio niente!
	}
  
  	function calcolaMesi(index) {
	  var annoInizio = eval("document.forms[0].NUMANNOINIZIO_"+index+".value");
	  var annoFine=eval("document.forms[0].NUMANNOFINE_"+index+".value");
	  var meseInizio=eval("document.forms[0].NUMMESEINIZIO_"+index+".value");
	  var meseFine=eval("document.forms[0].NUMMESEFINE_"+index+".value");
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
	        eval("document.forms[0].numMesi_"+index+".value="+mesi);
	        return true;
	  }
	       
	  return false;
  	}

	function checkLavCm(flagDispo) {
	  <% if (ControllaCdn.equals("")) {%>
      	if (eval("document.Frm1."+flagDispo+".value") == 'L') {
			alert("Il lavoratore non è in collocamento mirato!");
			eval("document.Frm1."+flagDispo+".value=''");
		}	
      <% }%>
    }
  
</script>

<%@ include file="Mansioni_CommonScripts.inc"%>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">

</head>
<body class="gestione">
<br>
<center>
<p class=titolo>Associazione movimenti</p>
<br>
<af:form name="Frm1" action="AdapterHTTP" method="POST">

	<%out.print(htmlStreamTop);%>
	<table class="main">
		<tr>
			<td>
			<table style="border-collapse: collapse" width="100%">
				<tr
					style="border: 1 solid #000080; color: #000080; font-size: 12px; font-weight: normal; text-align: center; vertical-align: middle">
					<td style="border: 1 solid; border-right: none" align=center>Lista	Movimenti</td>
					<td style="border: 1 solid; border-left: none" align=right></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td>
			<table width="100%">
				<tr>
					<td></td>
					<td>Ragione sociale</td>
					<td>Data Inizio</td>
					<td>Data Fine</td>
					<td>Mansione 2011</td>
					<td>&nbsp;</td>
					<td width="1%"></td>
					<td width="5%">Disponibile</td>
					<td>Tipo</td>
					<td></td>
				</tr>
				<%   
    String  codcontratto1 = "";
	
    for (int i=0;i<rows.size(); i++) {
        row = (SourceBean)rows.get(i); 

        mansione       = StringUtils.getAttributeStrNotNull(row, "strdescrizione");
        codMansione    = StringUtils.getAttributeStrNotNull(row, "codMansione");
        codMonoMovDich = StringUtils.getAttributeStrNotNull(row, "CODMONOMOVDICH");
        prgMansione  = it.eng.sil.util.Utils.notNull(row.getAttribute("prgMansione"));
        prgMovimento = it.eng.sil.util.Utils.notNull(row.getAttribute("prgMovimento"));
        datiniziomov = StringUtils.getAttributeStrNotNull(row, "datiniziomov");
        datfinemov   = StringUtils.getAttributeStrNotNull(row, "datfinemov");
        if ( (datfinemov!=null && !datfinemov.equals("") ) ) {
              mesefinemov = datfinemov.substring(3,5);
              annofinemov = datfinemov.substring(6);
        } else {
          mesefinemov ="";
          annofinemov ="";
        }
                
        codcontratto1=StringUtils.getAttributeStrNotNull(row, "codcontratto");
        // Settagglio del codice contratto a LP se non valorizzato nel DB
        if (codcontratto1 == "") {
            codcontratto1 = "LP";
        }
          
        if (prgMovimentoRigaPrec.equals("") || !prgMovimento.equals(prgMovimentoRigaPrec)) {
        	prgMovimentoRigaPrec = prgMovimento;
			%>
				<tr>
					<td width="25" height=25 valign=middle><input type="checkbox"
						name="I_PR_MAN<%=countMov%>"
						onclick="javascript:calcolaMesi('<%=countMov%>');"><script>inputs.push(document.Frm1.I_PR_MAN<%=countMov%>);</script>
					<input type="hidden" name="PRGMOVIMENTO_<%=countMov%>"
						value="<%= prgMovimento %>" /> <input type="hidden"
						name="CODMANSIONE_<%=countMov%>" value="<%= codMansione %>" /> <input
						type="hidden" name="PRGMANSIONE_<%=countMov%>"
						value="<%= prgMansione %>" /> 
					<input type="hidden" name="CODCONTRATTO_<%=countMov%>" value="<%= codcontratto1 %>" />
					<input type="hidden" name="strTipoMansione_<%=countMov%>" value="" />
					<input type="hidden" name="NUMMESEINIZIO_<%=countMov%>" value="<%= datiniziomov.substring(3,5) %>" /> 
					<input type="hidden" name="NUMMESEFINE_<%=countMov%>" value="<%= mesefinemov %>" /> <input
						type="hidden" name="NUMANNOINIZIO_<%=countMov%>"  value="<%= datiniziomov.substring(6) %>" /> 
					<input type="hidden" name="NUMANNOFINE_<%=countMov%>" value="<%= annofinemov %>" /> 
					<input type="hidden" name="desMansione_<%=countMov%>" value="<%= mansione %>" /></td>

					<td class="campo3"><%= Utils.notNull(row.getAttribute("STRRAGIONESOCIALE"))%>
					<input type="hidden" name="STRRAGSOCIALEAZIENDA_<%=countMov%>"
						value="<%= Utils.notNull(row.getAttribute("STRRAGIONESOCIALE"))%>" />
					<input type="hidden" name="STRCODFISCALEAZIENDA_<%=countMov%>"
						value="<%= Utils.notNull(row.getAttribute("CODFISCALEAZIENDA"))%>" />
					<input type="hidden" name="STRPARTITAIVAAZIENDA_<%=countMov%>"
						value="<%= Utils.notNull(row.getAttribute("STRPARTITAIVA"))%>" />
					<input type="hidden" name="CODNATGIURIDICA_<%=countMov%>"
						value="<%= Utils.notNull(row.getAttribute("CODNATGIURIDICA"))%>" />
					<input type="hidden" name="CODATECO_<%=countMov%>"
						value="<%= Utils.notNull(row.getAttribute("CODATECO"))%>" /> 
					<input	type="hidden" name="CODCOMAZIENDA_<%=countMov%>"
						value="<%= Utils.notNull(row.getAttribute("CODCOM"))%>" /> 
					<input type="hidden" name="STRINDIRIZZOAZIENDA_<%=countMov%>"
						value="<%= Utils.notNull(row.getAttribute("STRINDIRIZZO"))%>" /> 
					<input	type="hidden" name="numMesi_<%=countMov%>" value="" /></td>

					<td class="campo3"><%= Utils.notNull(row.getAttribute("DATINIZIOMOV"))%></td>
					<td class="campo3"><%= Utils.notNull(row.getAttribute("DATFINEMOV"))%></td>

					<td class="campo3">
					<%String descVarName = "DESCMANSIONE_"+countMov;
	          	String descMansioneTextName="DESCMANSIONETEXT_"+countMov;%>
					<af:textBox classNameBase="input" name="<%=descMansioneTextName%>"
						value="<%=Utils.notNull(row.getAttribute(\"STRDESCRIZIONE\"))%>"
						readonly="true" size="60" /> <af:textBox classNameBase="input"
						type="hidden" name="<%=descVarName%>"
						value="<%= Utils.notNull(row.getAttribute(\"STRDESCRIZIONE\"))%>"
						size="60" readonly="true" required="false" /></td>
					<td>
					</td>

					<td>
					<button type="button" class="ListButtonChangePage"
						name="CambiaAzineda"
						onclick="javascript:{
	                  							indiceRitorno=<%=countMov%>;
	                  							ricercaAvanzataMansioniConCodice2('CODMANSIONE_<%=countMov%>','DESCMANSIONE_<%=countMov%>', 'strTipoMansione_<%=countMov%>');
	                  						}">
					<span align="center"><img src="../../img/coop_app.gif"
						alt="Modifica mansione" align="middle" /></span></button>
					</td>

					<%   String input = "FLGDISPONIBILE_"+countMov; %>
					<td>
					<table>
						<tr>
							<td class="campo"><af:comboBox
								title="Disponibile a lavorare con la mansione" name="<%=input%>"
								classNameBase="input"
								disabled="<%= String.valueOf( !canModify ) %>"
								onChange="fieldChanged();checkLavCm(this.name);">
								<option value="" <% if ( "".equals(flgDisponibile) )   { out.print("SELECTED=\"true\""); } %>></option>
								<option value="P" <% if ( "P".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %>>Preselezione	ordinaria</option>
								<option value="L" <% if ( "L".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %>>Legge 68</option>
								<option value="S" <% if ( "S".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %>>Entrambe</option>
								<option value="N" <% if ( "N".equals(flgDisponibile) )  { out.print("SELECTED=\"true\""); } %>>Nessuna</option>
							</af:comboBox></td>
						</tr>
					</table>
					</td>
					<td><%=codMonoMovDich.equalsIgnoreCase("C") ? "Non docum." : "Documentato"%>
					<input type="hidden" name="CODMONOMOVDICH_<%=countMov%>"
						value="<%= codMonoMovDich %>" /></td>
				</tr>
				<%countMov ++;
				 }
               }%>
			</table>

			<input type="hidden" name="PAGE" value="CurrEspLavMainPage" /> <input
				type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>" /> <input
				type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>" /> <input
				type="hidden" name="MODULE" value="M_GetPrgMansioni" /> <input
				type="hidden" name="SIZE" value="<%= countMov %>" /> <input
				type="hidden" name="FLAG" value="0" /> <input type="hidden"
				name="prgincrocio" value="65" /> <% if(!assMov_EspNonLav.equals("")){ %>
			<input type="hidden" name="AssMov_EspNonLav"
				value="<%= assMov_EspNonLav %>" /> <%}%>
			</td>
		</tr>

		<tr>
			<td colspan="2">
			<table width="100%">
				<tr>
					<td align="left"><input type="checkbox" onclick="tutti(this)">
					Seleziona tutte</td>
				</tr>
			</table>
			</td>
		</tr>

		<tr>
			<td colspan="2" align="center">
			<% if ( rows.size() > 0 ) {%> <% if(assMov_EspNonLav.equals("")){ %> <input
				class="pulsante" type="button" name="inserisci"
				value="Riporta Movimenti Selezionati"
				onclick="carica('CurrEspLavMainPage', 'M_GetPrgMansioni', 'main', '1')">
			<%}else{%> <input class="pulsante" type="button" name="inserisci"
				value="Riporta Movimenti Selezionati"
				onclick="carica('CurrTirociniMainPage', 'M_GetPrgMansioni', 'main', '1')">
			<%}%> <input class="pulsante" type="button" name="annulla"
				value="Annulla e Chiudi" onclick="javascript:ResetMovimenti();">
			<%} else {%> <input class="pulsante" type="button" name="annulla"
				value="Chiudi" onclick="javascript:ResetMovimenti();"> <%}%>
			</td>
		</tr>

		<tr>
			<td colspan="2" align="center">
			<% if ( rows.size() > 0 ) {%> <input class="pulsante" type="button"
				name="inserisci" value="Compatta movimenti selezionati"
				onclick="carica('CompMovSelPage', 'M_GetMansioniMovimenti', 'Associazioni', '0' )">
			<%}%>
			</td>
		</tr>

	</table>
	<%out.print(htmlStreamBottom);%>
</af:form>
</body>
</html>