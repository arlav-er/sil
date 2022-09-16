<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String displayMotEta = "";
  String prgRichiestaAz = (String)serviceRequest.getAttribute("prgRichiestaAz");
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  boolean flag_insert= serviceRequest.containsAttribute("insert_alternativa");
  String cdnStatoRich = "";
  
  
  boolean canInsert= false;
  boolean canModify= false;
  boolean canDelete= false;
  boolean canManage = false;    

  Object  prgAlternativa="",
         prgAzienda=serviceRequest.getAttribute("prgAzienda"),
         prgUnita=serviceRequest.getAttribute("prgUnita"),
         cdnUtIns= "",
         dtmIns= "",
         cdnUtMod= "",
         dtmMod= "";
         
  String flgEsperienza= "",
          flgFormazioneProf="",
          strNote="",
          codMotEta="",
          strMotivEta="";

  Object numA="",
         numDa="",
         numAnniEsperienza="";

  SourceBean rigaTestata = null;
  SourceBean contTestata = (SourceBean) serviceResponse.getAttribute("M_GETTESTATARICHIESTA");
  Vector rows_VectorTestata = null;
  rows_VectorTestata = contTestata.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorTestata.size()!=0) {
    rigaTestata=(SourceBean) rows_VectorTestata.elementAt(0);
    cdnStatoRich = rigaTestata.getAttribute("cdnStatoRich").toString();
  }
  SourceBean etaEsperienzaRow= (SourceBean) serviceResponse.getAttribute("M_GETIDOETAESPERIENZA.ROWS.ROW");
  Vector listaAlternativeVector= serviceResponse.getAttributeAsVector("M_GETIDOLISTALTERNATIVE.ROWS.ROW");
  SourceBean alternativa=null;
  if ( (etaEsperienzaRow != null)  ) {

    prgAzienda        = etaEsperienzaRow.getAttribute("prgAzienda");
    prgUnita          = etaEsperienzaRow.getAttribute("prgUnita");
    prgAlternativa    = etaEsperienzaRow.getAttribute("prgAlternativa");
    flgEsperienza     = StringUtils.getAttributeStrNotNull(etaEsperienzaRow, "FLGESPERIENZA");
    flgFormazioneProf = StringUtils.getAttributeStrNotNull(etaEsperienzaRow, "FLGFORMAZIONEPROF");
    strNote           = StringUtils.getAttributeStrNotNull(etaEsperienzaRow, "STRNOTE");
    numA              = etaEsperienzaRow.containsAttribute("NUMA")?etaEsperienzaRow.getAttribute("NUMA"):"";
    numDa             = etaEsperienzaRow.containsAttribute("NUMDA")?etaEsperienzaRow.getAttribute("NUMDA"):"";
    codMotEta         = StringUtils.getAttributeStrNotNull(etaEsperienzaRow, "codMotEta");
    strMotivEta       = StringUtils.getAttributeStrNotNull(etaEsperienzaRow, "STRMOTIVETA");
    numAnniEsperienza = etaEsperienzaRow.containsAttribute("NUMANNIESPERIENZA")?etaEsperienzaRow.getAttribute("NUMANNIESPERIENZA"):"";
    cdnUtIns          = etaEsperienzaRow.getAttribute("CDNUTINS");
    dtmIns            = etaEsperienzaRow.getAttribute("DTMINS");
    cdnUtMod          = etaEsperienzaRow.getAttribute("CDNUTMOD");
    dtmMod            = etaEsperienzaRow.getAttribute("DTMMOD");
  }
  
  if ((numA == "" || numA == null) && (numDa == "" || numDa == null)) {
  	displayMotEta = "none";
  }

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");
  Testata testata= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod); 
  Linguette linguette = new Linguette( user, _funzione, "IdoEtaEsperienzaPage", new BigDecimal(prgRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");

  // NOTE: Attributi della pagina (pulsanti e link) 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  filter.setPrgAzienda( new BigDecimal(prgAzienda.toString()));
  filter.setPrgUnita( new BigDecimal(prgUnita.toString()));
  
	boolean canView=filter.canViewUnitaAzienda();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    PageAttribs attributi = new PageAttribs(user, _page);
    if (cdnStatoRich.compareTo("4")!=0 && cdnStatoRich.compareTo("5")!=0){
    	canInsert= attributi.containsButton("INSERISCI");
      	canModify= attributi.containsButton("AGGIORNA");
      	canDelete= attributi.containsButton("CANCELLA");
    }

  if ( !canModify && !canDelete && !canInsert ) {
      
    } else {
      boolean canEdit = filter.canEditUnitaAzienda();
      if ( !canEdit ) {
      	canInsert = false;
        canModify = false;
        canDelete = false;
      }
    }
    
    if(flag_insert) {
   		canManage = canInsert;
   	}
	else {
		canManage = canModify;
	}
  
  String htmlStreamTop = StyleUtils.roundTopTable( canManage );
  String htmlStreamBottom = StyleUtils.roundBottomTable( canManage );
  //prelevo l'unità dalla request per visualizzare la 
  if (flag_insert) prgUnita=serviceRequest.getAttribute("prgUnita");
  LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda, prgUnita,  prgRichiestaAz, prgAlternativa, flag_insert,  _funzione, _page, (!canInsert));
  //InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);

%>
<% 
//Object strPrgAziendaMenu=prgAzienda;
//Object strPrgUnitaMenu=prgUnita;
%>
<%@ include file="_infCorrentiAzienda.inc" %> 
<html>

<head>
  <title>Età ed esperienze</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">

  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">

  // NOTE: Rilevazione Modifiche da parte dell'utente
  var flagChanged = false;
  
  function fieldChanged() {

    // DEBUG: Scommentare per vedere "field changed !" ad ogni cambiamento
    //alert("field changed !")  
    
    // NOTE: field-check solo se canModify 
    <% if ( canModify ) { %> 
      flagChanged = true;
    <% } %> 
  }


  function toggleVisEsperienza() {
    var divAnniEsperienza = document.getElementById("anniEsperienza");
    divAnniEsperienza.style.display= 
      (document.forms[0].FLGESPERIENZA.value=="S" || document.forms[0].FLGESPERIENZA.value=="P" )?"":"none";

  }

//window.top.menu.caricaMenuAzienda("<%=prgAzienda%>");
window.top.menu.caricaMenuAzienda("<%=_funzione%>","<%=prgAzienda%>","<%=prgUnita%>");

function controllaEta(inputName) {

	objNumA=document.forms[0].numA;
 	objNumDa=document.forms[0].numDa;
  	objMotivazione=document.forms[0].strMotivEta;
  	objMotivazioneCodEta=document.forms[0].codMotEta;
  	ok=true;

	ControllaMotivazioneEta();
	if (objNumDa.value!="") {
		if (!isInteger(objNumDa.value)) {
	    	alert("Numero non corretto nel campo " + objNumDa.title);
	    	ok = false;
	    	objNumDa.focus();
	    	return (ok);
	  	}
	  
	  	if (objNumDa.value > 80) {
      		ok=false;
      		alert("l'età minima eccede gli 80 anni");
      		objNumDa.focus();
      		return (ok);
   		}
	
	    if (objNumDa.value<15) {
	   		ok=false;
	        alert("l'età minima è inferiore a 15 anni");
	        objNumDa.focus();
	        return (ok);
	    }
 	}	
  
  	if (objNumA.value!="") {
  
  		if (!isInteger(objNumA.value)) {
			alert("Numero non corretto nel campo " + objNumA.title);
		    ok = false;
		    objNumA.focus();
		    return (ok);    
		}

  	  	if (objNumA.value>80) {
        	ok=false;
        	alert("l'età limite eccede gli 80 anni");
        	objNumA.focus();
			return (ok);
      	}

	  	if (objNumA.value <15) {
	  		ok=false;
	  		alert("l'età limite è inferiore a 15 anni");
	  		objNumA.focus();
	  		return (ok);
	  	}
  
  	}

  	if (objNumA.value!="" && objNumDa.value!="") {
  
   		if (objNumA.value<objNumDa.value) {
      		ok=false;
          	alert("l'età minima è maggiore dell'età limite");
          	objNumDa.focus();
          	return (ok);
      	}
  	}  

  	if (ok) {
    	if (objNumA.value!="" || objNumDa.value!="") {
      		if (objMotivazioneCodEta.value == "") {
        		ok=false;
        		alert("Inserire motivazione età");
        		objMotivazioneCodEta.focus();
      		}
      		else {
      			if (objMotivazioneCodEta.value == "ALT") {
      				if (objMotivazione.value == "") {
	      				ok=false;
	      				alert("Inserire motivazione per la voce selezionata!");
	      				objMotivazione.focus();
	      			}
      			}
      		}
    	}
 	}
	return ok;
}

function ControllaMotivazioneEta() {
	objNumA=document.forms[0].numA;
 	objNumDa=document.forms[0].numDa;
 	objMotEta=document.forms[0].codMotEta;
 	objDescAltroMotEta=document.forms[0].strMotivEta;
 	var div = document.getElementById("MOTIVAZIONE_ETA");
 	if (objNumA.value!="" || objNumDa.value!="") {
 		div.style.display="";
 	}
 	else {
 		div.style.display = "none";
 	}
}

function AbilitaAltraMotivEta() {
	if (document.Frm1.codMotEta.value == 'ALT') {
		document.Frm1.strMotivEta.disabled = false;
	}
	else {
		document.Frm1.strMotivEta.value = "";
		document.Frm1.strMotivEta.disabled = true;
	}
}

function controllaAnniEsperienza(inputname){
	objEta = document.forms[0].numAnniEsperienza;
  	ok=true;

	if (objEta.value!="") {
		if (!isInteger(objEta.value)) {
	    	ok = false;
	    	alert("Numero non corretto nel campo " + objEta.title);
	    	objEta.focus();
	    	return (ok);
	  	}
	  	if (objEta.value>99) {
        	ok=false;
        	alert("Numero inserito eccede il limite previsto");
        	objEta.focus();
			return (ok);
      	}
	}

	return (ok);
}


function cancellaProfilo(){
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  ok = confirm("Con questa operazione si cancella l'intero profilo\n(Esperienze, Ambito professionale, Studi, Lingue, Informatica, Competenze, Agevolazioni, Tipologia rapporti).\nContinuare? ");
  if(ok){
    //doFormSubmit(document.Frm1);
      s= "AdapterHTTP?PAGE=IdoEtaEsperienzaPage";
      s += "&cancella=true";
      s += "&prgAzienda=<%=prgAzienda%>";
	  s += "&prgUnita=<%=prgUnita%>";
	  s += "&prgRichiestaAz=<%=prgRichiestaAz%>";
	  s += "&prgAlternativa=<%=prgAlternativa%>";
      s += "&CDNFUNZIONE=<%=_funzione%>"; 
      setWindowLocation(s);
  }

}
</SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
      %>
</script>
</head>

<body class="gestione" onload="rinfresca();toggleVisEsperienza()">
<% infCorrentiAzienda.show(out); %>
<% linguettaAlternativa.show(out); %>

<BR/>

  <%if (!flag_insert) linguette.show(out);
  %>

  <af:form method="POST" action="AdapterHTTP" name="Frm1">

    <input type="hidden" name="PAGE" value="IdoEtaEsperienzaPage">
    <input type="hidden" name="MODULE" value="">
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione %>"/>
    <input type="hidden" name="prgRichiestaAz" value="<%=prgRichiestaAz%>" />
    <input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
    <input name="P_CDNUTENTE" type="hidden" value="<%=user.getCodut()%>"/>
    
    <center>
      <font color="green">
        <af:showMessages prefix="M_SaveIdoEtaEsperienza" />
        <af:showMessages prefix="M_DeleteIdoEtaEsperienza" />
        <af:showMessages prefix="M_InsertIdoAlternativa" />
      </font>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>

    <p align="center">
    <%out.print(htmlStreamTop);%>
    <table class="main">
      <tr>
        <td/>
      </tr>
      <tr>
        <td colspan="2">
          <center>
            <font color="green">
             <!-- <af:showMessages prefix="M_UpdateMansione"/> -->
            </font>
          </center>
        </td>
      </tr>
<tr>
    <td class="etichetta">Profilo n.</td>
    <td class="campo"><af:textBox classNameBase="input" type="number" name="prgAlternativa" readonly="true" value="<%=prgAlternativa.toString()%>" size="2" />
    </td>
  </tr>           


<tr valign="top">
  <td class="etichetta">Età</td>
  <td class="campo">
    <af:textBox classNameBase="input" type="number" title="Età" name="numDa" readonly="<%= String.valueOf(!canManage) %>" value="<%= numDa.toString() %>" size="3" maxlength="2" onKeyUp="fieldChanged();ControllaMotivazioneEta();" validateWithFunction="controllaEta" />&nbsp;-&nbsp;
    <af:textBox classNameBase="input" type="number" title="Età" name="numA" readonly="<%= String.valueOf(!canManage) %>" value="<%= numA.toString() %>" size="3" maxlength="2" onKeyUp="fieldChanged();ControllaMotivazioneEta();"/>
  </td>
</tr>

<tr valign="top"><td colspan="2">
<div id="MOTIVAZIONE_ETA" style="display:<%=displayMotEta%>">
<table colspacing="0" cellspacing="0" border="0" width="100%">
  <tr><td class="etichetta">Motivazione</td>
  <td class="campo2" colspan="3">
    <af:comboBox classNameBase="input"
                          onChange="fieldChanged();AbilitaAltraMotivEta();"
                          name="codMotEta"
                          selectedValue="<%=codMotEta%>"
                          disabled="<%= String.valueOf(!canManage) %>"
                          moduleName="COMBO_MOTIVO_ETA"
                          addBlank="true" />
    </td>
	</tr>  

	<tr>  
  	<td class="etichetta"></td>
  	<td class="campo">
    <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" 
    			 name="strMotivEta" cols="25" value="<%=strMotivEta%>" 
    			 maxlength="100" readonly="<%= String.valueOf(!canManage) %>" />
    <script language="javascript">
    	if (<%=codMotEta.equals("ALT")%>) {
        	document.Frm1.strMotivEta.disabled = false;
    	}
        else {
          	document.Frm1.strMotivEta.disabled = true;
       	}
  	</script>
  	</td>
	</tr>
</table>
</div>
</td></tr>
<tr>
  <td class="etichetta">Esperienza</td>
  <td class="campo">
    <af:comboBox
      title="Esperienza"
      name="FLGESPERIENZA"
      classNameBase="input"
      disabled="<%= String.valueOf( !canManage ) %>"
      onChange="toggleVisEsperienza();fieldChanged()">
      <option value=""  <% if ( "".equals(flgEsperienza) )  { out.print(" SELECTED=\"true\""); } %> ></option>
      <option value="S" <% if ( "S".equals(flgEsperienza) ) { out.print(" SELECTED=\"true\""); } %> >Sì</option>
      <option value="N" <% if ( "N".equals(flgEsperienza) ) { out.print(" SELECTED=\"true\""); } %> >No</option>
      <option value="P" <% if ( "P".equals(flgEsperienza) ) { out.print(" SELECTED=\"true\""); } %> >Preferibile</option>
    </af:comboBox>
  </td>
</tr>  
</table>
<table class="main" width="100%" id="anniEsperienza" style="display:none">           
<tr valign="top">
  <td class="etichetta">Anni di esperienza</td>
  <td class="campo">
    <af:textBox classNameBase="input" type="number" title="Anni di esperienza" name="numAnniEsperienza" readonly="<%= String.valueOf(!canManage) %>" value="<%=numAnniEsperienza.toString()%>" size="3" maxlength="2" onKeyUp="fieldChanged();" validateWithFunction="controllaAnniEsperienza"/>
  </td>
</tr>
</table>
<table class="main">
<tr>
  <td class="etichetta">Formazione Professionale</td>
  <td class="campo">
    <af:comboBox
      title="Formazione Professionale"
      name="flgFormazioneProf"
      classNameBase="input"
      disabled="<%= String.valueOf( !canManage ) %>"
      onChange="fieldChanged()">
      <option value=""  <% if ( "".equals(flgFormazioneProf) )  { out.print(" SELECTED=\"true\""); } %> ></option>
      <option value="S" <% if ( "S".equals(flgFormazioneProf) ) { out.print(" SELECTED=\"true\""); } %> >Sì</option>
      <option value="N" <% if ( "N".equals(flgFormazioneProf) ) { out.print(" SELECTED=\"true\""); } %> >No</option>
      <option value="P" <% if ( "P".equals(flgFormazioneProf) ) { out.print(" SELECTED=\"true\""); } %> >Preferibile</option>
    </af:comboBox>
  </td>
</tr>  

<tr valign="top">
  <td class="etichetta">Note</td>
  <td class="campo">
    <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strNote" cols="25" value="<%=strNote%>" readonly="<%= String.valueOf(!canManage) %>" />
  </td>
</tr>

</table>
    <br/>
    <center>
      <table>    	
         <tr>
         	<% if (flag_insert && canInsert) { %> 
          		<td align="center">
          			<input class="pulsante" type="submit" name="inserisci" value="Inserisci">
          		</td>
    		<% } else if (canModify) { %>
          		<td align="center">
          			<input class="pulsante" type="submit" name="salva" value="Aggiorna">
          		</td>
          	<% } %>
          	<% if ( canDelete && !prgAlternativa.toString().equals("1") && !flag_insert) { %>
          		<td align="center">
            		<input class="pulsante" type="button" name="cancella" value="Cancella profilo" onclick="javascript:cancellaProfilo();">
          		</td>
          	<%}%>	
          </tr>     
      </table>
      <%out.print(htmlStreamBottom);%>
    </center>
    <br/>
    <center>
      <% testata.showHTML(out); %>
    </center>
  </af:form> 
</body>

</html>

<% } %>
