<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.StringUtils,                  
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  com.engiweb.framework.security.*" %>

      
      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% boolean canModify = true;
   boolean readOnlyStr = false;
   //Oggetti per stile grafico
   String htmlStreamTop = StyleUtils.roundTopTable(canModify);
   String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<title>Modifica Gruppo</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<script language="Javascript">

// Rilevazione Modifiche da parte dell'utente
var flagChanged = false;

function fieldChanged() {
 <% if (!readOnlyStr){ %> 
    flagChanged = true;
 <%}%> 
}

// va abilitata SOLO per CPI
function abilitaEmail() {
	
	var e = document.Frm1.CDNTIPOGRUPPO;
	var tipoGruppo= e.value;
	var emailtextbox = document.Frm1.STREMAILPUBBL;
	var emailLab = document.getElementsByName("emailFields")[0];
	if (tipoGruppo == 1) {       		  		
		emailLab.style.display = '';
	} else {
		emailtextbox.value = "";
		emailLab.style.display = 'none';
	}
	
}

function abilitaCodRif() {
	var index = document.Frm1.CDNTIPOGRUPPO.selectedIndex;		
	var selectedID = document.Frm1.CDNTIPOGRUPPO.options[index].value;
	if (selezionatoTipoGruppoPatronatoSogg(selectedID)) {
		document.Frm1.STRCODRIF.disabled = false;
	} else {
		document.Frm1.STRCODRIF.value = "";
		document.Frm1.STRCODRIF.disabled = true;
	}
}

var vTipoGruppoPS = new Array();
var vTipoGruppoID = new Array();
var vTipoGruppoSoggetti = new Array();
var vTipoGruppoIDSoggetti = new Array();

function caricaDati() {
	<%
	int contatoreRiga = 0;
    List listaCdnTipoGruppoPatronatoSogg = new ArrayList();
    Vector listaTipoGruppoPatronato = serviceResponse.getAttributeAsVector("M_ListaTipoGruppoPatronato.ROWS.ROW");
    for (int j = 0; j < listaTipoGruppoPatronato.size(); j++) {
		SourceBean rowTipidoc = (SourceBean)listaTipoGruppoPatronato.get(j);

		String denominazione = (String)rowTipidoc.getAttribute("STRDENOMINAZIONE");
		denominazione = StringUtils.formatValue4Javascript(denominazione);
		listaCdnTipoGruppoPatronatoSogg.add((BigDecimal)rowTipidoc.getAttribute("CDNTIPOGRUPPO"));
		%>				
		vTipoGruppoPS[<%=contatoreRiga%>] = '<%=denominazione%>' ;
		vTipoGruppoID[<%=contatoreRiga%>] = <%=(BigDecimal)rowTipidoc.getAttribute("CDNTIPOGRUPPO")%> ;
		<%
		contatoreRiga = contatoreRiga + 1;
	}
    Vector listaTipoGruppoSoggAcc = serviceResponse.getAttributeAsVector("M_ListaTipoGruppoSogAccreditati.ROWS.ROW");
    for (int j = 0; j < listaTipoGruppoSoggAcc.size(); j++) {
		SourceBean rowTipidoc = (SourceBean)listaTipoGruppoSoggAcc.get(j);

		String denominazione = (String)rowTipidoc.getAttribute("STRDENOMINAZIONE");
		
		denominazione = StringUtils.formatValue4Javascript(denominazione);
		listaCdnTipoGruppoPatronatoSogg.add((BigDecimal)rowTipidoc.getAttribute("CDNTIPOGRUPPO"));
		%>				
		vTipoGruppoPS[<%=contatoreRiga%>] = '<%=denominazione%>' ;
		vTipoGruppoID[<%=contatoreRiga%>] = <%=(BigDecimal)rowTipidoc.getAttribute("CDNTIPOGRUPPO")%> ;
		vTipoGruppoSoggetti[<%=j%>] = '<%=denominazione%>' ;
		vTipoGruppoIDSoggetti[<%=j%>] = <%=(BigDecimal)rowTipidoc.getAttribute("CDNTIPOGRUPPO")%> ;
		<%
		contatoreRiga = contatoreRiga + 1;
	}
	%>
}

function selezionatoTipoGruppoPatronatoSogg(valore){
	for ( ix = 0; ix < vTipoGruppoID.length; ix=ix+1){
		if ( vTipoGruppoID[ix] == valore){
			return true;
		}
	}
	return false;
}

function selezionatoTipoGruppoSoggettoAccreditato(valore){
	for ( ix = 0; ix < vTipoGruppoIDSoggetti.length; ix=ix+1){
		if ( vTipoGruppoIDSoggetti[ix] == valore){
			return true;
		}
	}
	return false;
}

function controllaCampi() {
	var e = document.Frm1.CDNTIPOGRUPPO;
	var selectedID = e.value;
	if ( selezionatoTipoGruppoPatronatoSogg(selectedID) ) {
		if (document.Frm1.STRCODRIF.value == "") {
			alert("Il campo Codice di riferimento è obbligatorio nel caso in cui Tipo gruppo corrisponda ad un Patronato o Soggetto Accreditato.");
			return false;
		}	
	}
	if ( selezionatoTipoGruppoSoggettoAccreditato(selectedID) ) {
		if (document.Frm1.codComNas.value == "") {
			alert("Il campo Comune di riferimento è obbligatorio nel caso in cui Tipo gruppo corrisponda ad un Soggetto Accreditato.");
			return false;
		}
	}
	if (document.Frm1.codComNas.value != "") {
		if (!btFindComuneCAP_onSubmit(document.Frm1.codComNas, document.Frm1.strComNas, null, true)) {
			return false;
		}	
	}
	return true;		
}

<%@ include file="../documenti/RicercaCheck.inc" %>
</script>

<%@ include file="../global/Function_CommonRicercaComune.inc" %>
</head>

<%
 String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
 String mode=StringUtils.getAttributeStrNotNull(serviceRequest,"MODE");
 String denominazioneRic = (String) serviceRequest.getAttribute("STRDENOMINAZIONERIC");
 String tipoGruppoRic = (String) serviceRequest.getAttribute("TIPOGRUPPORIC");
 String flagStandardRic   = (String) serviceRequest.getAttribute("FLGSTANDARDRIC");  

 String strDenominazione="";
 String strTipoGruppo="";
 String strCodRif="";
 String strLuogoRif="";
 String codComRif="";
 String strComRif="";
 String strNota="";
 String email="";
 BigDecimal cdnGruppo=new BigDecimal(0);
 BigDecimal cdnTipoGruppo=new BigDecimal(0);
 String cdnTipoGruppoDisabled="disabled";

  Vector vectDett= serviceResponse.getAttributeAsVector("M_ProfDettaglioGruppo.ROWS.ROW");
  if ( (vectDett != null) && (vectDett.size() > 0) ) {
    SourceBean beanDett = (SourceBean)vectDett.get(0);

    cdnGruppo=(BigDecimal) beanDett.getAttribute("CDNGRUPPO");
    strDenominazione=StringUtils.getAttributeStrNotNull(beanDett,"STRDENOMINAZIONE");
    strTipoGruppo=StringUtils.getAttributeStrNotNull(beanDett,"STRTIPOGRUPPO");
    strCodRif=StringUtils.getAttributeStrNotNull(beanDett,"STRCODRIF");
    strLuogoRif=StringUtils.getAttributeStrNotNull(beanDett,"STRLUOGORIF");  
    codComRif=StringUtils.getAttributeStrNotNull(beanDett,"CODCOMRIF");
    strComRif=StringUtils.getAttributeStrNotNull(beanDett,"STRCOMRIF");
    strNota=StringUtils.getAttributeStrNotNull(beanDett,"STRNOTA");
    email=StringUtils.getAttributeStrNotNull(beanDett,"STREMAILPUBBL");
    cdnTipoGruppo=(BigDecimal) beanDett.getAttribute("CDNTIPOGRUPPO");
  }

    String _page = "ProfVisualizzaGruppoPage"; 
    int cdnfunz = new Integer ( cdnFunzione).intValue();
  	Linguette l = new Linguette(user, cdnfunz , _page, cdnGruppo);
   
    l.setCodiceItem("CDNGRUPPO");


%>

<body class="gestione" onload="rinfresca();caricaDati();abilitaEmail();">

  <%boolean disabilitaCmbCdnTipoGruppo = true; %>
  <%@ include file="testataGruppo.inc" %>

 <% 
 	l.show(out);        
  %>

<br>

 <font><af:showMessages prefix="M_ProfModificaGruppo"/></font>
 <font><af:showMessages prefix="M_ProfNuovoGruppo"/></font>
	    <font><af:showErrors /></font>
   

<p class="titolo">Modifica gruppo</p>
<p align="center">
  <af:form  action="AdapterHTTP" method="POST" name="Frm1" onSubmit="controllaCampi()">
  <input type="hidden" name="MODE" value="EDIT">
  <input type="hidden" name="CDNGRUPPO" value="<%=cdnGruppo%>">
  <input type="hidden" name="STRDENOMINAZIONERIC" value="<%=denominazioneRic%>"/>
  <input type="hidden" name="TIPOGRUPPORIC" value="<%=tipoGruppoRic%>"/>
  <input type="hidden" name="FLGSTANDARDRIC" value="<%=flagStandardRic%>"/>
  <input type="hidden" name="cdnfunzione" value="<%=cdnFunzione%>"/>
  <input type="hidden" name="PAGE" value="ProfSalvaGruppoPage"/>

  <%out.print(htmlStreamTop);%> 
  <table class="main">
  
	<%@ include file="dettaglioGruppo.inc"%>
	
   <tr>
    <td colspan="2" align="center">
    	<input class="pulsante" type="submit" name="BTNAGGIORNA" value="Aggiorna"/>    
      &nbsp;&nbsp;
      <!--<input name="reset" type="reset" class="pulsanti" value="Annulla">-->
    </td>
   </tr>
  </table>
<%out.print(htmlStreamBottom);%> 
  </af:form>
</p>

</body>
</html>
