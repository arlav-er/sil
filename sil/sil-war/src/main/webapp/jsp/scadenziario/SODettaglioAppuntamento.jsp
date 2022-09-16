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
Testata operatoreInfo = null;
String _page = (String) serviceRequest.getAttribute("PAGE");
String _pageProvenienza = (String) serviceRequest.getAttribute("PAGEPROVENIENZA");
int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String strPrgAzienda = serviceRequest.containsAttribute("PRGAZIENDA")? serviceRequest.getAttribute("PRGAZIENDA").toString():"";
String strPrgUnita = serviceRequest.containsAttribute("PRGUNITA")? serviceRequest.getAttribute("PRGUNITA").toString():"";
String strCdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE")? serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String codCpi = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
String prova = serviceRequest.containsAttribute("prova")? serviceRequest.getAttribute("prova").toString():"";
String data_al = serviceRequest.containsAttribute("DATAAL")? serviceRequest.getAttribute("DATAAL").toString():"";
String data_dal = serviceRequest.containsAttribute("DATADAL")? serviceRequest.getAttribute("DATADAL").toString():"";
String dataDalSlot   = serviceRequest.containsAttribute("dataDalSlot") ? ((String) serviceRequest.getAttribute("dataDalSlot")) : "";
String codServizio = serviceRequest.containsAttribute("CODSERVIZIO") ? ((String) serviceRequest.getAttribute("CODSERVIZIO")) : "";
String codTipoContatto = serviceRequest.containsAttribute("CODCPICONTATTO") ? ((String) serviceRequest.getAttribute("CODCPICONTATTO")) : "";

User userCurr = (User) sessionContainer.getAttribute(User.USERID);
InfCorrentiLav infCorrentiLav= null;
InfCorrentiAzienda infCorrentiAzienda= null;
if (strCdnLavoratore.compareTo("") != 0) {
  infCorrentiLav = new InfCorrentiLav(sessionContainer, strCdnLavoratore, userCurr);
	infCorrentiLav.setSkipLista(true);
}
else {
  infCorrentiAzienda = new InfCorrentiAzienda(strPrgAzienda,strPrgUnita);
}

String cdnUtins="";
String dtmins="";
String cdnUtmod="";
String dtmmod="";

SourceBean cont = (SourceBean) serviceResponse.getAttribute("SELECT_DETTAGLIO_AGENDA_MOD");
SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
String datApp = row.containsAttribute("DATA") ? row.getAttribute("DATA").toString() : "";
String strOraApp = row.containsAttribute("ORARIO") ? row.getAttribute("ORARIO").toString() : "";
BigDecimal prgSpiApp = null, prgSpiAppEff=null;
prgSpiApp = (BigDecimal) row.getAttribute("PRGSPI");
prgSpiAppEff = (BigDecimal) row.getAttribute("PRGSPIEFF");
String strSpiApp = "", strSpiAppEff="";

if (prgSpiApp != null) {
  strSpiApp = prgSpiApp.toString();
}
if (prgSpiAppEff != null) {
  strSpiAppEff = prgSpiAppEff.toString();
}

String labelServizio = "Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equalsIgnoreCase("1")){
	labelServizio = "Area";
}
String codServizioApp = row.containsAttribute("CODSERVIZIO") ? row.getAttribute("CODSERVIZIO").toString() : "";
BigDecimal prgAmbiente = null;
prgAmbiente = (BigDecimal) row.getAttribute("PRGAMBIENTE");
String strAmbiente = "";
if (prgAmbiente != null) {
  strAmbiente = prgAmbiente.toString();
}

BigDecimal nDurataMinuti = null;
nDurataMinuti = (BigDecimal) row.getAttribute("NUMMINUTI");
String strDurata = "";
if (nDurataMinuti != null) {
  strDurata = nDurataMinuti.toString();
}
String txtNoteApp = row.containsAttribute("TXTNOTE") ? row.getAttribute("TXTNOTE").toString() : "";
String CODEFFETTOAPPUNT = StringUtils.getAttributeStrNotNull(row,"CODEFFETTOAPPUNT");
String CODESITOAPPUNT = StringUtils.getAttributeStrNotNull(row,"CODESITOAPPUNT");
String CODSTATOAPPUNTAMENTO = StringUtils.getAttributeStrNotNull(row, "CODSTATOAPPUNTAMENTO");
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);



String strCodice="";
String strDescrizione="";
Vector codiceCpi = serviceResponse.getAttributeAsVector("M_ELENCOCPI.ROWS.ROW");
SourceBean rowDescrizione = null;   
   	for (int i=0;i<codiceCpi.size();i++)  {
  		rowDescrizione = (SourceBean)codiceCpi.get(i);
	 		strCodice = rowDescrizione.getAttribute("codice").toString();
	 		if (strCodice.equals(codCpi)) {
	 				strDescrizione = rowDescrizione.getAttribute("DESCRIZIONE").toString();
	 				break;
			}
			
	}
%>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title></title>
  <script type="text/javascript">
    function chiudi () { 
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
	  document.frmAppuntamento.PAGE.value = "SOScadAppuntamentoPage";
	  doFormSubmit(document.frmAppuntamento);
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
<af:form name="frmAppuntamento" action="AdapterHTTP" method="POST">
<input type="hidden" name="CODCPICONTATTO" value="<%=codTipoContatto%>">
<input type="hidden" name="CODCPI" value="<%=codTipoContatto%>">
<input type="hidden" name="PAGE" value="ScadSalvaContattoPage">
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="PRGUNITA" value="<%=strPrgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=strPrgAzienda%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=strCdnLavoratore%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="DATAAL" value="<%=data_al%>">
<input type="hidden" name="DATADAL" value="<%=data_dal%>">
<input type="hidden" name="dataDalSlot" value="<%=dataDalSlot%>">
<input type="hidden" name="codServizio" value="<%=codServizio%>">

<p class="titolo">Appuntamenti presi</p>
<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main">
<tr>
  <td class="etichetta">Data</td>
  <td class="campo">
  <af:textBox name="DATAAPP"
              size="11"
              maxlength="10"
              type="date"
              readonly="true"
              classNameBase="input"
              value="<%=datApp%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Orario</td>
  <td class="campo">
  <af:textBox name="ORARIOAPP"
              size="5"
              maxlength="5"
              type="time"
              title="Orario"
              readonly="true"
              classNameBase="input"
              value="<%=strOraApp%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Durata</td>
  <td class="campo">
  <af:textBox name="DurataApp"
              size="5"
              maxlength="5"
              type="text"
              title="Durata"
              readonly="true"
              classNameBase="input"
              value="<%=strDurata%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta"><%= labelServizio %></td>
    <td class="campo">
      <af:comboBox name="SERVIZIOAPP" size="1" title="<%=labelServizio %>"
                     multiple="false" disabled="true" classNameBase="input"
                     focusOn="false" moduleName="COMBO_SERVIZIO"
                     selectedValue="<%=codServizioApp%>" addBlank="true" blankValue=""/>
    </td>
</tr>
<tr>
  <td class="etichetta">Operatore</td>
    <td class="campo">
      <af:comboBox name="PRGSPIAPP" size="1" title="Operatore"
                     multiple="false" disabled="true" classNameBase="input"
                     focusOn="false" moduleName="COMBO_SPI_AG"
                     selectedValue="<%=strSpiApp%>" addBlank="true" blankValue=""/>
    </td>
</tr>
<tr>
  <td class="etichetta">Operatore effettivo</td>
    <td class="campo">
      <af:comboBox name="PRGSPIAPPEFF" size="1" title="Operatore effettivo"
                     multiple="false" disabled="true" classNameBase="input"
                     focusOn="false" moduleName="COMBO_SPI_AG"
                     selectedValue="<%=strSpiAppEff%>" addBlank="true" blankValue=""/>
    </td>
</tr>

 <tr>
  <td class="etichetta">Centro per l'impiego</td>
    <td class="campo">
    <af:textBox name="CODICECPI" title="Centro per l'impiego"
                    readonly="true" size="30" classNameBase="input"
                     value="<%=strDescrizione%>"/>
    </td>
</tr>  

<tr>
  <td class="etichetta">Ambiente</td>
    <td class="campo">
      <af:comboBox name="AMBIENTEAPP" size="1" title="Ambiente"
                     multiple="false" disabled="true" classNameBase="input"
                     focusOn="false" moduleName="COMBO_AMBIENTE"
                     selectedValue="<%=strAmbiente%>" addBlank="true" blankValue=""/>
    </td>
</tr>
<tr class="note">
  <td class="etichetta">Note</td>
  <td class="campo">
    <af:textArea name="TXTCONTATTOAPP" 
                 cols="60" 
                 rows="4" 
                 title="Note"
                 readonly="true"
                 classNameBase="input"
                 value="<%=txtNoteApp%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Effetto Appuntamento</td>
  <td class="campo">
      <af:comboBox name="CODEFFETTOAPPUNT" size="1" title="Effetto appuntamento"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_EFFETTO_APPUNTAMENTO"
                     selectedValue="<%=CODEFFETTOAPPUNT%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="true"/>    
    </td>
</tr>
<tr>
  <td class="etichetta">Esito appuntamento</td>
  <td class="campo">
      <af:comboBox name="CODESITOAPPUNT" size="1" title="Effetto appuntamento"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_ESITO_APPUNTAMENTO"
                     selectedValue="<%=CODESITOAPPUNT%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="true"/>        
  </td>
</tr>
<tr>
  <td class="etichetta">Stato appuntamento</td>
  <td class="campo">
      <af:comboBox name="CODSTATOAPPUNTAMENTO" size="1" title="Stato appuntamento"
                     multiple="false" required="true"
                     focusOn="false" moduleName="COMBO_STATO_APPUNTAMENTO"
                     selectedValue="<%=CODSTATOAPPUNTAMENTO%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="true"/>        
  </td>
</tr>

</table>
<%out.print(htmlStreamBottom);%>
<table align="center">
<tr>
  <td align="center">
  <input type="button" class="pulsanti" name="annulla" value="Chiudi" onClick="javascript:chiudi();">
  </td>
</tr>
</table>
</af:form>
<div align ="center">
<% operatoreInfo.showHTML(out); %>
</div>
</body>
</html>