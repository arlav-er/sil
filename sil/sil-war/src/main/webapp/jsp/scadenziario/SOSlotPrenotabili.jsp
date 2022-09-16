<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%
boolean fromPattoAzioni = serviceRequest.containsAttribute("PATTO_AZIONI") && serviceRequest.getAttribute("PATTO_AZIONI").equals("true");
//Vector codLstTab = null;
String statoSezioni = "";
String nonFiltrare = "";
if(fromPattoAzioni){ 
	//codLstTab = serviceRequest.getAttributeAsVector("COD_LST_TAB");
	statoSezioni = StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni");
	nonFiltrare = StringUtils.getAttributeStrNotNull(serviceRequest,"NONFILTRARE");
}
// Savino 19/10/05: modifiche per gestire il ritorno alla pagina di associazione con la presenza oltre della sezione
//      appuntamenti anche quella delle azioni
//if(fromPattoAzioni && codLstTab.size()==0) codLstTab.add("AG_LAV");//In caso di ritorno alla pagina dopo l'inserimento
//if( codLstTab==null) codLstTab = new Vector(0);
int cdnUt = user.getCodut();
String cdnParUtente = Integer.toString(cdnUt);
String _page = (String) serviceRequest.getAttribute("PAGE");
String _pageProvenienza = (String) serviceRequest.getAttribute("PAGEPROVENIENZA");
int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String strPrgAzienda = serviceRequest.containsAttribute("PRGAZIENDA")? serviceRequest.getAttribute("PRGAZIENDA").toString():"";
String strPrgUnita = serviceRequest.containsAttribute("PRGUNITA")? serviceRequest.getAttribute("PRGUNITA").toString():"";
String datascadenza = serviceRequest.containsAttribute("DATASCADENZA")? serviceRequest.getAttribute("DATASCADENZA").toString():"";
String strCdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE")? serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String codCpi = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
//String codice = serviceRequest.containsAttribute("CO")? serviceRequest.getAttribute("CODICE").toString():"";
String data_al = serviceRequest.containsAttribute("DATAAL")? serviceRequest.getAttribute("DATAAL").toString():"";
String data_dal = serviceRequest.containsAttribute("DATADAL")? serviceRequest.getAttribute("DATADAL").toString():"";
String dataDalSlot = serviceRequest.containsAttribute("dataDalSlot") ? ((String) serviceRequest.getAttribute("dataDalSlot")) : "";
String codServizio = serviceRequest.containsAttribute("CODSERVIZIO") ? ((String) serviceRequest.getAttribute("CODSERVIZIO")) : "";
String prgspi = serviceRequest.containsAttribute("PRGSPI") ? ((String) serviceRequest.getAttribute("PRGSPI")) : "";
String codTipoContatto = serviceRequest.containsAttribute("CODCPICONTATTO") ? ((String) serviceRequest.getAttribute("CODCPICONTATTO")) : "";

User userCurr = (User) sessionContainer.getAttribute(User.USERID);
InfCorrentiLav infCorrentiLav= null;
InfCorrentiAzienda infCorrentiAzienda= null;

String varAppoggio;
SourceBean rowsApp = null;
if (strCdnLavoratore.compareTo("") != 0) {
 	infCorrentiLav = new InfCorrentiLav(sessionContainer, strCdnLavoratore, userCurr);
  		infCorrentiLav.setSkipLista(true);
  			if(codTipoContatto.equals("")) {
  				rowsApp = (SourceBean) serviceResponse.getAttribute("M_FILTROCPILAV.ROWS.ROW");
    				if (rowsApp != null && codCpi.equals("")) {
      					varAppoggio = rowsApp.containsAttribute("CODICE") ? rowsApp.getAttribute("CODICE").toString() : "";
      					codCpi = varAppoggio;
    				}
   			}
   			else {codCpi = codTipoContatto;}
}
else {
  infCorrentiAzienda = new InfCorrentiAzienda(strPrgAzienda,strPrgUnita);
  	if(codTipoContatto.equals("")) {
  		rowsApp = (SourceBean) serviceResponse.getAttribute("M_FILTROCPIAZI.ROWS.ROW");
   			if (rowsApp != null && codCpi.equals("")) {
     			varAppoggio = rowsApp.containsAttribute("CODICE") ? rowsApp.getAttribute("CODICE").toString() : "";
     			codCpi = varAppoggio;
    		} 
	}
	else  {codCpi = codTipoContatto;}
}

Calendar today =  Calendar.getInstance();
//String todayDate = today.get(DAY_OF_MONTH+"/"+today.MONTH+"/"+today.YEAR;
String todayDate = "";
if (today.get(Calendar.DAY_OF_MONTH)<10) todayDate +="0";
todayDate += today.get(Calendar.DAY_OF_MONTH)+"/";

if ( (today.get(Calendar.MONTH)+1)<10) todayDate += "0";
todayDate += (today.get(Calendar.MONTH)+1) + "/"+today.get(Calendar.YEAR);

if(dataDalSlot.equals(""))
{ 
	dataDalSlot = todayDate;
}


PageAttribs attributi = new PageAttribs(user, _page);
boolean filtro = true; 

String htmlStreamTop = StyleUtils.roundTopTable(filtro);
String htmlStreamBottom = StyleUtils.roundBottomTable(filtro);

String token = "";
String urlDiLista = "";

String labelServizio = "Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equalsIgnoreCase("1")){
	labelServizio = "Area";
}
%>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  
  <script language="Javascript">
  

	function setParentWindowLocation(newLocation) {
    	if (isInSubmit()) return;
	    window.parent.frames['ScadInferiore'].prepareSubmit();
	    prepareSubmit();
		window.parent.location = newLocation;
	}
	
	function SettaAzione(prgSlot,dataslot,codice) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

      var strCaption1 = "";
      var strCaption2 = "";
     	strCaption1 = "Salvare appuntamento da slot ?";
        strCaption2 = "Data appuntamento maggiore della data scadenza, salvare comunque ?";
      var annoScadenza,meseScadenza,giornoScadenza,dataScadenza;
      var annoSlot = dataslot.substr(6,4);
      var meseSlot = dataslot.substr(3,2);
      var giornoSlot = dataslot.substr(0,2);
      var dataSlot = parseInt(annoSlot + meseSlot + giornoSlot,10);
      if (dataSlot > dataScadenza ) {
        if (confirm(strCaption2)) {
          document.frmNuovoAppuntamento.PAGE.value = "SOScadSalvaAppuntamentoPage";
          document.frmNuovoAppuntamento.prgParSlot.value = prgSlot;
          doFormSubmit(document.frmNuovoAppuntamento); 
        }
      }
      else {
        if (confirm(strCaption1)) {
          document.frmNuovoAppuntamento.PAGE.value = "SOScadSalvaAppuntamentoPage";
		  document.frmNuovoAppuntamento.CODCPI.value = codice;
          document.frmNuovoAppuntamento.prgParSlot.value = prgSlot;
          doFormSubmit(document.frmNuovoAppuntamento);
        }
      }
    }
function toDate(newDate) {
    var tokens = newDate.split('/');
    var usDate= tokens[2]+"/"+tokens[1]+"/"+tokens[0];
    return new Date(usDate);
}

function checkDate(objData1, objData2) {
  ok=true;
  strData1=objData1.value;
  strData2=objData2.value;
	if (toDate(strData2).getTime()<toDate(strData1).getTime()) {
		alert("La "+ objData2.title +" è precedente alla "+ objData1.title);
      objData2.focus();
      ok=false;

	}
	return ok;
}

function filtra()
{ 
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var dataSlot = document.frmNuovoAppuntamento.dataDalSlot;
  if((dataSlot != null) && (dataSlot !=  "")) 
  { 
  	if(!controllaFunzTL()) return;
  /*
    var dataDiOggi = new Date();
    var gg = dataDiOggi.getDate();
    var mm = dataDiOggi.getMonth()+1;
    var yyyy = dataDiOggi.getYear();
    
    var oggi = new Object(gg+"/"+mm+"/"+yyyy);
    */
    dataSlot = document.frmNuovoAppuntamento.dataDalSlot;
    if(!checkDate(document.frmNuovoAppuntamento.dataDiOggi,dataSlot)) {
         undoSubmit();
         return;
    }
  }
  document.frmNuovoAppuntamento.PAGE.value = "SOScadAppuntamentoPage";
  doFormSubmit(document.frmNuovoAppuntamento);
}

</script>
</head>
<body class="gestione">
<%
if (strCdnLavoratore.compareTo("") != 0) {
  infCorrentiLav.show(out); 
}
else {
  infCorrentiAzienda.show(out); 
}
%>
<font color="red">
  <af:showErrors/>
</font>
<font color="green">
</font>
<af:form name="frmNuovoAppuntamento" onSubmit="filtra()" action="AdapterHTTP" method="POST" target="_parent">
<input type="hidden" name="dataDiOggi" value="<%=todayDate%>" title="data di oggi">
<input type="hidden" name="prgParSlot" value="">
<input type="hidden" name="codice" value="">
<input type="hidden" name="PAGE" value="">
<input type="hidden" name="codParCpi" value="<%=codCpi%>">
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="PRGUNITA" value="<%=strPrgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=strPrgAzienda%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=strCdnLavoratore%>">
<input type="hidden" name="cdnParUtente" value="<%=cdnParUtente%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="DATAAL" value="<%=data_al%>">
<input type="hidden" name="DATADAL" value="<%=data_dal%>">
<p class="titolo">Nuovo appuntamento</p>
<%out.print(htmlStreamTop);%>
<table class="main" border="0">
<tr>
 <td class="etichetta2" nowrap>Dalla data </td>
 	<td class="campo2" nowrap><af:textBox type="date" 
 				name="dataDalSlot" 
 				value="<%=dataDalSlot%>" 
 				title=" data di inizio 'Dalla data'" 
 				readonly="<%=String.valueOf(!filtro)%>" 
 				validateOnPost="true" 
 				maxlength="10" 
 				size="11"/></td>
 				
 <td class="etichetta" nowrap ><%=labelServizio %></td>
      <td class="campo"><af:comboBox name="codServizio"
                   moduleName="M_SCADGETSERVIZIO"
                   selectedValue="<%=codServizio%>"
                   title="<%=labelServizio %>"
                   classNameBase="input"
                   required="false" addBlank="true"
                   disabled="<%=String.valueOf(!filtro)%>"/>
    
 </td>
 <%if(filtro) {%>
		<td rowspan="2" valign="center">   
      		&nbsp;<input type="button" class="pulsanti" name="Filtra" value="Filtra" onClick="filtra();">&nbsp;
    	</td>

	<%}%>
</tr>
<tr>
	<td colspan="2">&nbsp;</td>
	<td class="etichetta">Centro per l'Impiego</td>
	<td class="campo">
		<af:comboBox name="CODCPI" title="Centro per l'Impiego" 
		moduleName="M_GetCpiPoloProvinciale" addBlank="true" selectedValue="<%=codCpi%>"
		required="true" disabled="<%=String.valueOf(!filtro)%>"/>
	</td>
</tr> 
</table>

<%out.print(htmlStreamBottom);%>
<af:list moduleName="M_SOGetSlotPrenotabili" jsSelect="SettaAzione"/>
<%if((_pageProvenienza != null) && !_pageProvenienza.equals("") && !_pageProvenienza.equals("null")){%>
    <center><input type="button" class="pulsanti" name="ANNULLA" value="Chiudi"></center>
<%}%>    
</af:form>
</body>
</html>