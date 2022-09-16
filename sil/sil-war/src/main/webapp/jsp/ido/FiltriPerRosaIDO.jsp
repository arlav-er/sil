<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.security.*,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.sil.security.PageAttribs,
                  java.util.*,
                  java.io.*,
                  java.math.*" %>
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String prgRichiestaAz    = (String) serviceRequest.getAttribute("prgRichiestaAz");
String prgRosa    = (String) serviceRequest.getAttribute("prgRosa");
String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");
String prgUnita   = (String) serviceRequest.getAttribute("prgUnita");
String flagSV = StringUtils.getAttributeStrNotNull(serviceRequest, "flagSV");
String cpiRose    = user.getCodRif();
SourceBean row = null;

String rosaFiltro    = "";
String motivoFiltro = "";
boolean rosaFiltrata= false;
BigDecimal kLockRosa = null;
	
row = (SourceBean) serviceResponse.getAttribute("M_IDO2GETGIAFILTRATA.ROWS.ROW");
if( row != null)
{ rosaFiltro = StringUtils.getAttributeStrNotNull(row, "FLGVALIDACANC");
  if (rosaFiltro.equalsIgnoreCase("S")) rosaFiltrata = true;
  motivoFiltro = StringUtils.getAttributeStrNotNull(row, "STRVALIDACANC");
  kLockRosa = (BigDecimal) row.getAttribute("NUMKLOROSA");
}
// Savino: 30/08/2005 controllo che il filtro non sia fallito; nel caso carico il motivo inserito dall'operatore
if (!rosaFiltrata && responseContainer.getErrorHandler()!=null && !responseContainer.getErrorHandler().isOK()) 
	motivoFiltro = StringUtils.getAttributeStrNotNull(serviceRequest, "STRVALIDACANC");
String sesso     = "";
String codMotGenere="";
String sessoDesc = "";
String motivoSex = "";

row = (SourceBean) serviceResponse.getAttribute("M_FILTROPERSESSO.ROWS.ROW");
if( row != null)
{ sesso     = StringUtils.getAttributeStrNotNull(row, "FILTRO");
  if (sesso.equalsIgnoreCase("F"))      sessoDesc = "FEMMINILE";
  else if (sesso.equalsIgnoreCase("M")) sessoDesc = "MASCHILE";
  codMotGenere = StringUtils.getAttributeStrNotNull(row, "codMotGenere");    
  motivoSex = StringUtils.getAttributeStrNotNull(row, "MOTIVO");
}

row = (SourceBean) serviceResponse.getAttribute("M_FILTROPERETA.ROWS.ROW");
BigDecimal etaMin    = null;
BigDecimal etaMax    = null;
String codMotEta="";
String motivoEta = "";
Calendar dataAttuale = Calendar.getInstance();
String data = DateUtils. getNow();
String dataNascitaMax = "";
String dataNascitaMin = "";
String etaMinStr = "";
String etaMaxStr = "";
String flgSvantaggiati= "";
String strMotSvantaggiati= "";   
String datVerificaSvan= "";

String flgDisNonIscr= "";
String strMotNonIscr= "";   
String datVerificaDis= "";


String configSV = serviceResponse.containsAttribute("M_GetConfigSV.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigSV.ROWS.ROW.NUM").toString():"0";

if( row != null)
{
  codMotEta = StringUtils.getAttributeStrNotNull(row, "codMotEta");
  motivoEta = StringUtils.getAttributeStrNotNull(row, "MOTIVO");
  
  int anno = dataAttuale.get(Calendar.YEAR);
//  etaMin = (BigDecimal) it.eng.sil.util.Utils.notNull(row.getAttribute("FILTROETAMIN"));
 // etaMax = (BigDecimal) it.eng.sil.util.Utils.notNull(row.getAttribute("FILTROETAMAX"));
//  etaMin    = (BigDecimal) row.getAttribute("FILTROETAMIN");
//  etaMin    = (BigDecimal) row.getAttribute("FILTROETAMIN");

String GiornoEMese = data.substring(0,6);
String Giorno = data.substring(0,2);
String Mese = data.substring(3,5);
if ("02".equalsIgnoreCase(Mese)){
	if ("29".equalsIgnoreCase(Giorno)){
		GiornoEMese = "28/02/";
	}
}
etaMinStr    = it.eng.sil.util.Utils.notNull(row.getAttribute("FILTROETAMIN"));
etaMin    = (BigDecimal) row.getAttribute("FILTROETAMIN");
if (etaMin == null) {
   etaMin = new BigDecimal(0);
   dataNascitaMin = "";
} else {
	dataNascitaMin = GiornoEMese +(anno-etaMin.intValue()); 
} 


etaMaxStr    = it.eng.sil.util.Utils.notNull(row.getAttribute("FILTROETAMAX"));

etaMax = (BigDecimal) row.getAttribute("FILTROETAMAX");
if (etaMax == null) {
   etaMax = new BigDecimal(0);
   dataNascitaMax  = "";
}  else {dataNascitaMax  = GiornoEMese+(anno-etaMax.intValue()-1);  
		 dataNascitaMax = DateUtils.giornoSuccessivo(dataNascitaMax);
		}
}

row = (SourceBean) serviceResponse.getAttribute("M_FiltroPerSvantaggiati.ROWS.ROW");
if( row != null){ 
  flgSvantaggiati     = StringUtils.getAttributeStrNotNull(row, "FLGSVANTAGGIATI");
  if("S".equalsIgnoreCase(flgSvantaggiati)) {
  	strMotSvantaggiati = StringUtils.getAttributeStrNotNull(row, "STRMOTSVANTAGGIATI");    
  	datVerificaSvan = StringUtils.getAttributeStrNotNull(row, "DATVERIFICASVAN");
  }
}

row = (SourceBean) serviceResponse.getAttribute("M_FiltroPerDisNonIscritti.ROWS.ROW");
if( row != null){ 
  flgDisNonIscr = StringUtils.getAttributeStrNotNull(row, "FLGDISNONISCR");
  if("S".equalsIgnoreCase(flgDisNonIscr)) {
	strMotNonIscr = StringUtils.getAttributeStrNotNull(row, "STRMOTNONISCR");    
	datVerificaDis = StringUtils.getAttributeStrNotNull(row, "DATVERIFICADIS");
  }
}

//Le seguenti righe di codice gestiscono la profilatura
//PageAttribs attributi = new PageAttribs(user, "");
boolean readOnly = false; //attributi.containsButton("APPLICA_FILTRO");
String htmlStreamTop = "";
String htmlStreamBottom = "";

//Servono per gestire il layout grafico
if (!rosaFiltrata) {
	htmlStreamTop = StyleUtils.roundTopTable(!readOnly);
	htmlStreamBottom = StyleUtils.roundBottomTable(!readOnly);
}
else {
	htmlStreamTop = StyleUtils.roundTopTable(false);
	htmlStreamBottom = StyleUtils.roundBottomTable(false);
}

String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");
String listPage = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");

String numKloRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "NUMKLOROSA");
String DaFiltro = StringUtils.getAttributeStrNotNull(serviceRequest, "DAFILTRO");
%>
<html>

<head>
<title>Filtri da applicare alla rosa dei candidati</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>


<script language="Javascript">
// Rilevazione Modifiche da parte dell'utente
var flagChanged = false;
        
function fieldChanged() {
<% if (!readOnly){ %> 
  flagChanged = true;
<%}%> 
}

  function cameBack(backPage)
  { 
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    document.Frm1.PAGE.value = backPage; 
    <%-- disabilito questi campi per evitare che nel'url utilizzato per la lista ci finiscano descrizioni 
    con possibili caratteri di ritorno a capo che farebbero fallire lo scorrimento della lista --%>
    document.Frm1.strValidaCanc.disabled=true;
    document.Frm1.motivoEta.disabled=true;
    document.Frm1.motivoSex.disabled=true;
    doFormSubmit(document.Frm1);  
  }

</script>
</head>


<body class="gestione">

<font color="red"><af:showErrors/></font>
<font color="green"><af:showMessages prefix="M_ApplicaFiltro"/></font>

<p align="center">
<af:form name="Frm1" method="POST" action="AdapterHTTP">

<%out.print(htmlStreamTop);%>
<table class="main">
  <tr><td>&nbsp;</td></tr>
  <tr><td align="left" colspan="2">
       <b>Rosa num.&nbsp;<%=prgRosa%></b>
       <%if(rosaFiltrata){%>&nbsp;&nbsp;<b>(I seguenti parametri di filtro sono stati applicati alla rosa)</b><%}
         else            {%>&nbsp;&nbsp;(I seguenti parametri di filtro NON sono stati applicati alla rosa)<%}%>
      </td>
  </tr>
  <tr><td>&nbsp;</td></tr>

  <tr><td colspan="2"><div class="sezione2">Sesso</div></td></tr>
  <tr><td class="etichetta">Sesso</td>
      <td class="campo">
        <af:textBox classNameBase="input" name="sessoDesc" value="<%=sessoDesc%>" readonly="true"/>
        <% if (!sesso.equals("")) {%>
            <input type="hidden" name="sesso" value="<%=sesso%>"/>
              <%}%>

      </td>
  </tr>
  <tr><td class="etichetta">Motivazione</td>
	  <td class="campo">
	    <af:comboBox classNameBase="input"
	                          name="codMotGenere"
	                          selectedValue="<%=codMotGenere%>"
	                          disabled="true"
	                          moduleName="COMBO_MOTIVO_SESSO"
	                          addBlank="true" />
	  </td>
  </tr>
  <tr><td class="etichetta"></td>
      <td class="campo">
        <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="motivoSex" cols="25" value="<%=motivoSex%>" readonly="true" />
      </td>
  </tr>

  <tr><td colspan="2"><div class="sezione2">Et&agrave;</div></td></tr>
  <tr><td class="etichetta">da</td>
      <td>
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
         <tr>
          <td align="left" width="5%"><af:textBox classNameBase="input" name="etaMin" value="<%=etaMinStr%>" readonly="true"/></td>
          <td align="right">a&nbsp;&nbsp;&nbsp;</td>
          <td align="left"><af:textBox classNameBase="input" name="etaMax" value="<%=etaMaxStr%>" readonly="true"/></td>
         </tr>
         <%--<tr> <td></td><td align="left"><%=dataNascitaMax%></td> <td></td><td align="left"><%=dataNascitaMin%></td> </tr>--%>
        </table>
  </td></tr>
  <tr><td class="etichetta">Motivazione</td>
      <td class="campo">
	    <af:comboBox classNameBase="input"
	                          name="codMotEta"
	                          selectedValue="<%=codMotEta%>"
	                          disabled="true"
	                          moduleName="COMBO_MOTIVO_ETA"
	                          addBlank="true" />
	  </td>
  </tr>
  <tr><td class="etichetta"></td>
      <td class="campo"><af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="motivoEta" cols="25" value="<%=motivoEta%>" readonly="true" /></td>
  </tr>

  <tr><td colspan="2"><div class="sezione2">Nazionalit&agrave;</div></td></tr>
	  <tr><td class="etichetta">Nazionalit&agrave;</td>
	      <td>
	        <table cellpadding="0" cellspacing="0" border="0" width="100%">
	         <tr>
		  <td align="left" width="5%">Motivazione</td>
	          <td align="right">&nbsp;&nbsp;&nbsp;</td>
	          <td align="left">
	          </td>
	         </tr>
	        </table>
	  </td></tr>
  
  <%
    Vector rows = serviceResponse.getAttributeAsVector("M_FiltroPerNazione.ROWS.ROW");
    if(!rows.isEmpty())
    { String codCittad    = "";
      String cittadinanza = "";
      String motivoCittad = "";
      String codMotNazionalita = "";
      Iterator iter = rows.iterator();
      int i=0;
      while(iter.hasNext())
      { row = (SourceBean) iter.next();
        codCittad    = StringUtils.getAttributeStrNotNull(row, "FILTRO");
        cittadinanza = StringUtils.getAttributeStrNotNull(row, "CITTADINANZA");
        motivoCittad = StringUtils.getAttributeStrNotNull(row, "MOTIVO");
        codMotNazionalita = StringUtils.getAttributeStrNotNull(row, "CODMOTNAZIONALITA");        
        i++;
        %>
	  <tr><td class="etichetta"><b><%=cittadinanza%></b><input type="hidden" name="codCittadinanza_<%=i%>" value="<%=codCittad%>"/></td>
	      <td>
	        <table cellpadding="0" cellspacing="0" border="0" width="100%">
	         <tr>
		  <td align="left" width="5%">
		    <af:comboBox classNameBase="input"
		          name="codMotNazionalita"
		          title="Motivazione"
		          selectedValue="<%=codMotNazionalita%>"
		          disabled="true"
		          moduleName="COMBO_MOTIVO_NAZIONALITA"
		          addBlank="true" 
		          truncAt="60" />	  		  
		  </td>
	          
	          <td align="left"><b><%=motivoCittad%></b>
	          </td>
	         </tr>
	        </table>
	  </td></tr>
        <%        
      }//while
    }//if
  %>
  <tr><td>&nbsp;</td></tr>
  <tr><td colspan="2"><div class="sezione2">Altre Iscrizioni</div></td></tr>
   <tr valign="top">
    <td class="etichetta">Tipo Iscrizione</td>
    <td class="campo" align="left">
    	<%if (!rosaFiltrata) {%>
	    	<af:comboBox classNameBase="input" name="CodAltreIscr" title="Tipo Iscrizione" moduleName="CI_TIPO_ISCR" 
				multiple="true" size="8" addBlank="false" disabled="<%=String.valueOf(readOnly || rosaFiltrata)%>"/>
		<%} else {%>
			<table>
			<%
			Vector vettAltreIscr = serviceResponse.getAttributeAsVector("M_LoadFiltroPerAltreIscr.ROWS.ROW");
		  	if (vettAltreIscr != null && !vettAltreIscr.isEmpty()) {
			  	for (int j=0;j<vettAltreIscr.size();j++) {
			   		SourceBean rowIscr = (SourceBean)vettAltreIscr.get(j);
			   		%><tr><td><b><%=rowIscr.getAttribute("DESCRIZIONE").toString()%></b></td></tr>
				<%}
		  	}
			%>
			</table>
		<%}%>
    </td>
  </tr>
  <tr><td>&nbsp;</td></tr>
  <tr><td colspan="2"><div class="sezione2">Validazione</div></td></tr>
   <tr valign="top">
    <td class="etichetta">Motivazione</td>
    <td class="campo">
      <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strValidaCanc" cols="25" title="'Motivazione validazione'" 
                   value="<%=motivoFiltro%>" readonly="<%=String.valueOf( (readOnly || rosaFiltrata) ? true : false )%>" required="true"/>
    </td>
  </tr>
  <tr><td>&nbsp;</td></tr>
  <%if (configSV.equals("1")) {%>
  <tr><td colspan="2"><div class="sezione2">Svantaggiati</div></td></tr>
   <tr valign="top">
   		<td class="etichetta">Svantaggiati</td>
   		<td class="campo">
   			<input type="checkbox" title="Flag Svantaggiati" name="flgSvantaggiati" value="" <%=flgSvantaggiati.equals("S") ? "CHECKED" : ""%>
   				   disabled = "true"/>
   			<% if (!flgSvantaggiati.equals("")) {%>
            <input type="hidden" name="flgSV" value="<%=flgSvantaggiati%>"/>
            <%}%> 
   		</td>
   </tr>
   <tr>
   		<td class="etichetta">Alla data</td>
      	<td class="campo">
   			<af:textBox classNameBase="input" name="datVerificaSvan" value="<%=datVerificaSvan%>" readonly="true"/>
   		</td>
   </tr>
   <tr>
   <td class="etichetta">Motivazione</td>
    <td class="campo">
      <af:textArea classNameBase="textarea" name="strMotSvantaggiati" cols="25" title="Motivazione Svantaggiati" 
                   value="<%=strMotSvantaggiati%>" readonly="true" />
    </td>
  </tr>
  
  <tr><td colspan="2"><div class="sezione2">Disabili Non Iscritti</div></td></tr>
   <tr valign="top">
   		<td class="etichetta">Disabili Non Iscritti</td>
   		<td class="campo">
   			<input type="checkbox" title="Flag Disabili Non Iscritti" name="flgDisNonIscr" value="" <%=flgDisNonIscr.equals("S") ? "CHECKED" : ""%>
   				   disabled = "true"/>
   			<% if (!flgDisNonIscr.equals("")) {%>
            	<input type="hidden" name="flgDis" value="<%=flgDisNonIscr%>"/>
           <%}%> 
   		</td>
   </tr>
   <tr>
   		<td class="etichetta">Alla data</td>
      	<td class="campo">
   			<af:textBox classNameBase="input" name="datVerificaDis" value="<%=datVerificaDis%>" readonly="true"/>
   		</td>
   </tr>
   <tr>
   <td class="etichetta">Motivazione</td>
    <td class="campo">
      <af:textArea classNameBase="textarea" name="strMotNonIscr" cols="25" title="Motivazione Disabili Non Iscritti" 
                   value="<%=strMotNonIscr%>" readonly="true" />
    </td>
  </tr>
  
  <%}%>
  <tr><td>&nbsp;</td></tr>
  <tr><td colspan="2">
      <%if(!readOnly && !rosaFiltrata) {%>
       <input class="pulsante" type="submit" name="filtro" value="Filtra">&nbsp;&nbsp;&nbsp;&nbsp;
      <%}%>
       <input class="pulsante" type="button" name="chiudi" value="Chiudi" onClick="cameBack('MatchDettRosaPage')">
  </td></tr>
  <tr><td>&nbsp;</td></tr>
  <tr><td>&nbsp;</td></tr>
 
</table>
<%out.print(htmlStreamBottom);%></p>

<input type="hidden" name="PAGE" value="FiltriPerRosaIDOPage"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
<input type="hidden" name="prgRichiestaAz" value="<%=prgRichiestaAz%>"/>
<input type="hidden" name="prgRosa" value="<%=prgRosa%>"/>
<input type="hidden" name="NUMKLOROSA" value="<%=numKloRosa%>"/>
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
<input type="hidden" name="cpiRose" value="<%=cpiRose%>"/>
<input type="hidden" name="dataNascMax" value="<%=dataNascitaMax%>"/>
<input type="hidden" name="dataNascMin" value="<%=dataNascitaMin%>"/>
 <% if (!sesso.equals("")) {%>
<input type="hidden" name="filtroSV" value="1"/>
<%}%>

<input type="hidden" name="DAFILTRO" value="1"/>
<% 
  // Se filtro o chiudo passo i parametri per il ritorno alla corretta pagina dell'elenco candidati
  // Se invece la rosa è stata filtrata è opportuno tornare alla prima pagina della lista 
%>
<%if(!DaFiltro.equals("1")) {%>
	  <%if(!mess.equals("")) {%>
			<input type="hidden" name="MESSAGE" value="<%=mess%>"/>
			<%if(!listPage.equals("")) {%>
				<input type="hidden" name="LIST_PAGE" value="<%=listPage%>"/>
	 		<%}%>
	  <%}%>
<%}%>

<%if (!dataNascitaMax.equals("") || !dataNascitaMin.equals("")) {%>
<input type="hidden" name="eta" value="true"/>
<%}%>




</af:form>

</body>
</html>
