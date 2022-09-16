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
String configuraLabelPsichica_mentale=(String)Utils.getConfigValue("LABEL").getAttribute("ROW.NUM"); 	//La Valle d'Aosta vuole l'etichetta "mentale" al posto di "psichica"
boolean labelMentale = false;
if (configuraLabelPsichica_mentale.equals("1")) //Siamo in Valle d'Aosta, enjoy skiing!
	 labelMentale = true;	
String configDiagnFunz = serviceResponse.containsAttribute("M_GetConfigDiagnosiFunz.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigDiagnosiFunz.ROWS.ROW.NUM").toString():"0";
int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String prgRichiestaAz    = (String) serviceRequest.getAttribute("prgRichiestaAz");
String prgRosa    = (String) serviceRequest.getAttribute("prgRosa");
String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");
String prgUnita   = (String) serviceRequest.getAttribute("prgUnita");
String cpiRose    = user.getCodRif();
SourceBean row = null;

String rosaFiltro    = "";
String motivoFiltro = "";
boolean rosaFiltrata= false;
BigDecimal kLockRosa = null;
	
row = (SourceBean) serviceResponse.getAttribute("M_IDO2GETGIAFILTRATA.ROWS.ROW");
if( row != null)
{ rosaFiltro = StringUtils.getAttributeStrNotNull(row, "FLGFILTRICMAATTIVATI");
  if (rosaFiltro.equalsIgnoreCase("S")) rosaFiltrata = true;
  kLockRosa = (BigDecimal) row.getAttribute("NUMKLOROSA");
}

String CODMONOTIPOAZIENDA = "";	
String CODMONOCAP_MR5 = "";
String CODMONOCAP_IN2 = "";
String CODMONOCAP_PO1 = "";
String CODMONOCAP_PO2 = "";
String CODMONOCAP_PO6 = "";
String CODMONOCAP_PO7 = "";
String CODMONOCAP_LO2 = "";
String CODMONOCAP_LO3 = "";
String CODMONOCAP_MV1 = "";
String CODMONOCAP_MV2 = "";
String CODMONOCAP_MV3 = "";
String CODMONOCAP_MV4 = "";
String CODMONOCAP_AC1 = "";
String CODMONOCAP_FA2 = "";
String CODMONOCAP_FA3 = "";
String CODMONOCAP_SL1 = "";
String CODMONOCAP_SL2 = "";
String FLGESCFISICA = "";
String FLGESCPSICHICA = "";
String FLGESCNONDETERMINATO = "";
String FLGESCSENSORIALE = "";
String FLGESCINTELLETTIVA = "";

String CODMONOCAP_IN4 = "";
String CODMONOCAP_IN5 = "";
String CODMONOCAP_IN6 = "";
String CODMONOCAP_PO8 = "";
String CODMONOCAP_PO9 = "";
String CODMONOCAP_PO10 = "";
String CODMONOCAP_PO11 = "";
String CODMONOCAP_PO12 = "";
String CODMONOCAP_PO13 = "";
String CODMONOCAP_PO14 = "";
String CODMONOCAP_LO4 = "";
String CODMONOCAP_LO5 = "";
String CODMONOCAP_LO6 = "";
String CODMONOCAP_MV5 = "";
String CODMONOCAP_MV6 = "";
String CODMONOCAP_MV7 = "";
String CODMONOCAP_AC5 = "";
String CODMONOCAP_AC6 = "";
String CODMONOCAP_FA7 = "";
String CODMONOCAP_FA8 = "";
String CODMONOCAP_SL5 = "";
String CODMONOCAP_SL6 = "";
String CODMONOCAP_SL7 = "";
String CODMONOCAP_SL8 = "";
String CODMONOCAP_SL9 = "";
String CODMONOCAP_CA1 = "";
String CODMONOCAP_CA2 = "";
String CODMONOCAP_CA3 = "";
String CODMONOCAP_CA4 = ""; 

SourceBean rowParam = null;

if (configDiagnFunz.equals("2")) {
	rowParam = (SourceBean) serviceResponse.getAttribute("M_RosaParFiltroCMVDA.ROWS.ROW");
	if( rowParam != null) { 
		CODMONOTIPOAZIENDA = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOTIPOAZIENDA");
		CODMONOCAP_IN4 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_IN4");
		CODMONOCAP_IN5 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_IN5");
		CODMONOCAP_IN6 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_IN6");
		CODMONOCAP_PO8 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_PO8");
		CODMONOCAP_PO9 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_PO9");
		CODMONOCAP_PO10 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_PO10");
		CODMONOCAP_PO11 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_PO11");
		CODMONOCAP_PO12 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_PO12");
		CODMONOCAP_PO13 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_PO13");
		CODMONOCAP_PO14 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_PO14");
		CODMONOCAP_LO4 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_LO4");
		CODMONOCAP_LO5 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_LO5");
		CODMONOCAP_LO6 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_LO6");
		CODMONOCAP_MV5 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_MV5");
		CODMONOCAP_MV6 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_MV6");
		CODMONOCAP_MV7 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_MV7");
		CODMONOCAP_AC5 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_AC5");
		CODMONOCAP_AC6 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_AC6");
		CODMONOCAP_FA7 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_FA7");
		CODMONOCAP_FA8 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_FA8");
		CODMONOCAP_SL5 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_SL5");
		CODMONOCAP_SL6 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_SL6");
		CODMONOCAP_SL7 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_SL7");
		CODMONOCAP_SL8 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_SL8");
		CODMONOCAP_SL9 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_SL9");
		CODMONOCAP_CA1 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_CA1");
		CODMONOCAP_CA2 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_CA2");
		CODMONOCAP_CA3 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_CA3");
		CODMONOCAP_CA4 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_CA4");
		FLGESCFISICA = StringUtils.getAttributeStrNotNull(rowParam, "FLGESCFISICA");
		FLGESCPSICHICA = StringUtils.getAttributeStrNotNull(rowParam, "FLGESCPSICHICA");
		FLGESCNONDETERMINATO = StringUtils.getAttributeStrNotNull(rowParam, "FLGESCNONDETERMINATO");
		FLGESCSENSORIALE = StringUtils.getAttributeStrNotNull(rowParam, "FLGESCSENSORIALE");
		FLGESCINTELLETTIVA = StringUtils.getAttributeStrNotNull(rowParam, "FLGESCINTELLETTIVA");
	}
} else {
	rowParam = (SourceBean) serviceResponse.getAttribute("M_RosaParFiltroCM.ROWS.ROW");
	if( rowParam != null) { 
		CODMONOTIPOAZIENDA = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOTIPOAZIENDA");
		CODMONOCAP_MR5 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_MR5");
		CODMONOCAP_IN2 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_IN2");
		CODMONOCAP_PO1 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_PO1");
		CODMONOCAP_PO2 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_PO2");
		CODMONOCAP_PO6 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_PO6");
		CODMONOCAP_PO7 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_PO7");
		CODMONOCAP_LO2 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_LO2");
		CODMONOCAP_LO3 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_LO3");
		CODMONOCAP_MV1 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_MV1");
		CODMONOCAP_MV2 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_MV2");
		CODMONOCAP_MV3 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_MV3");
		CODMONOCAP_MV4 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_MV4");
		CODMONOCAP_AC1 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_AC1");
		CODMONOCAP_FA2 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_FA2");
		CODMONOCAP_FA3 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_FA3");
		CODMONOCAP_SL1 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_SL1");
		CODMONOCAP_SL2 = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_SL2");
		FLGESCFISICA = StringUtils.getAttributeStrNotNull(rowParam, "FLGESCFISICA");
		FLGESCPSICHICA = StringUtils.getAttributeStrNotNull(rowParam, "FLGESCPSICHICA");
		FLGESCNONDETERMINATO = StringUtils.getAttributeStrNotNull(rowParam, "FLGESCNONDETERMINATO");
		FLGESCSENSORIALE = StringUtils.getAttributeStrNotNull(rowParam, "FLGESCSENSORIALE");
		FLGESCINTELLETTIVA = StringUtils.getAttributeStrNotNull(rowParam, "FLGESCINTELLETTIVA");
	}
}

//Le seguenti righe di codice gestiscono la profilatura
//PageAttribs attributi = new PageAttribs(user, "");
boolean readOnly = false; //attributi.containsButton("APPLICA_FILTRO");

if(rosaFiltrata) {
	readOnly = true;
}

//Servono per gestire il layout grafico
String htmlStreamTop = StyleUtils.roundTopTable(!readOnly);
String htmlStreamBottom = StyleUtils.roundBottomTable(!readOnly);

String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");
String listPage = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");

String numKloRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "NUMKLOROSA");
String DaFiltro = StringUtils.getAttributeStrNotNull(serviceRequest, "DAFILTRO");
%>
<html>

<head>
<title>Filtri CM da applicare alla rosa dei candidati</title>

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
   
    doFormSubmit(document.Frm1);  
  }
</script>


</head>


<body class="gestione">

<font color="red"><af:showErrors/></font>
<font color="green"><af:showMessages prefix="M_ApplicaFiltroCM"/></font>

<p align="center">
<af:form name="Frm1" method="POST" action="AdapterHTTP">

<%out.print(htmlStreamTop);%>
<table class="main">
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr><td align="left" colspan="2">
       <b>Rosa num.&nbsp;<%=prgRosa%></b>
       <%if(rosaFiltrata){%>&nbsp;&nbsp;<b>(I seguenti parametri di filtro sono stati applicati alla rosa)</b><%}
         else            {%>&nbsp;&nbsp;(I seguenti parametri di filtro NON sono stati applicati alla rosa)<%}%>
      </td>
  </tr>
  
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr><td colspan="2"><div class="sezione2">Tipologia azienda</div></td></tr>
  <tr valign="top">
    <td class="etichetta">Tipo Azienda</td>
    <td class="campo">
    	<af:comboBox    
		      name="codMonoTipoAzienda"
		      classNameBase="input"
		      disabled="<%= String.valueOf( readOnly )%>">	
		      <option value="" ></option>	     
		      <option value="A" <% if ( "A".equalsIgnoreCase(CODMONOTIPOAZIENDA) ) { %>SELECTED="true"<% } %>>Azienda Privata</option>
		      <option value="E" <% if ( "E".equalsIgnoreCase(CODMONOTIPOAZIENDA) ) { %>SELECTED="true"<% } %>>Enti Pubblici</option>
		 </af:comboBox>
    </td>
  </tr>
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr><td align="left" colspan="2">
       <b>Diagnosi funzionale:&nbsp;</b>
      </td>
  </tr>  
  <tr><td colspan="2"><div class="sezione2">Esclusione tipo disabilità</div></td></tr>
  <tr>
  	<td colspan="2" width="100%">
  		<table width="100%" class="main"> 
			  <tr valign="top">
			  	<td class="etichetta">Fisica</td>
			    <td >
			    	<af:comboBox    
					      name="FLGESCFISICA"
					      classNameBase="input"
					      disabled="<%= String.valueOf( readOnly )%>">	
					      <option value="N"></option>	     
					      <option value="S" <% if ( "S".equalsIgnoreCase(FLGESCFISICA) ) { %>SELECTED="true"<% } %>>Si</option>		      
					 </af:comboBox>					 
			    </td>     			
			    <td class="etichetta"><%=labelMentale?"Mentale":"Psichica"%></td>
			    <td width="100%" align="left" >
					 <af:comboBox    
					      name="FLGESCPSICHICA"
					      classNameBase="input"
					      disabled="<%= String.valueOf( readOnly )%>">	
					      <option value="N"></option>	  
					      <option value="S" <% if ( "S".equalsIgnoreCase(FLGESCPSICHICA) ) { %>SELECTED="true"<% } %>>Si</option>		      
					 </af:comboBox>		
				</td> 	    		  	
			  </tr>
			  <%if (configDiagnFunz.equals("1")) {%>
				  <tr valign="top">
				    <td class="etichetta">Non determinato</td>
				    <td >
				    	<af:comboBox    
						      name="FLGESCNONDETERMINATO"
						      classNameBase="input"
						      disabled="<%= String.valueOf( readOnly )%>">	
						      <option value="N"></option>	  
						      <option value="S" <% if ( "S".equalsIgnoreCase(FLGESCNONDETERMINATO) ) { %>SELECTED="true"<% } %>>Si</option>		     
						 </af:comboBox>
					</td>
				  </tr>
			  <% } else {%>
				  <tr valign="top">
				    <td class="etichetta">Sensoriale</td>
				    <td >
				    	<af:comboBox    
						      name="FLGESCSENSORIALE"
						      classNameBase="input"
						      disabled="<%= String.valueOf( readOnly )%>">	
						      <option value="N"></option>	  
						      <option value="S" <% if ( "S".equalsIgnoreCase(FLGESCSENSORIALE) ) { %>SELECTED="true"<% } %>>Si</option>		     
						 </af:comboBox>
					</td> 					 
					<td class="etichetta">Intellettiva</td> 
					<td width="100%" align="left">
						 <af:comboBox    
						      name="FLGESCINTELLETTIVA"
						      classNameBase="input"
						      disabled="<%= String.valueOf( readOnly )%>">			           
						      <option value="N"></option>	
						      <option value="S" <% if ( "S".equalsIgnoreCase(FLGESCINTELLETTIVA) ) { %>SELECTED="true"<% } %>>Si</option>
						 </af:comboBox>
				    </td>      				  		
				  </tr>
			  <% } %>
		</table>
  	</td>
  </tr>
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr><td colspan="2"><div class="sezione2">Capacità lavorative</div></td></tr>
  <tr>
  	<td colspan="2" width="100%">
  		<table width="100%">  	
		  <%
		  SourceBean rowsCapacita = null;
		  if (configDiagnFunz.equals("2")) {
			rowsCapacita = (SourceBean) serviceResponse.getAttribute("COMBO_CAPACITA_CM_VDA.ROWS");  
		  } else {
		  	rowsCapacita = (SourceBean) serviceResponse.getAttribute("COMBO_FILTRI_CAPACITA_CM.ROWS");
		  }
		  Vector vectCapacita = rowsCapacita.getAttributeAsVector("ROW");
		  for (int i=0; i<vectCapacita.size(); i++) {
		  	SourceBean rowCap = (SourceBean)vectCapacita.get(i); 
		  	String nameCombo = "gradoCapacita_"+(String)rowCap.getAttribute("codcapacita"); 	
		  	String CODMONOCAP = "";
		  	if( rowParam != null) { 		
				CODMONOCAP = StringUtils.getAttributeStrNotNull(rowParam, "CODMONOCAP_"+(String)rowCap.getAttribute("codcapacita"));	
			}
		  	String codMonoTipoGrado = Utils.notNull(rowCap.getAttribute("codMonoTipoGrado"));	
		  %>
		  	<tr valign="top">
		    	<td class="etichetta" style="width:70%"><%= (String)rowCap.getAttribute("strdescrizione")%></td>
		    	<td class="campo">
		    		<% if (configDiagnFunz.equals("2")) { 
		    			if (codMonoTipoGrado.equals("Q") || codMonoTipoGrado.equals("P") ) { %>
		    				<af:comboBox name="<%=nameCombo%>"
			                     size="1"
			                     title="Grado Capacita"
			                     multiple="false"
			                     required="false"
			                     focusOn="false"
			                     moduleName="COMBO_GRADO_FILTRI_CM_SINO"
			                     addBlank="true"
			                     blankValue="0"
			                     disabled="<%= String.valueOf( readOnly )%>"
			                     selectedValue="<%=CODMONOCAP%>"					                     
			                     classNameBase="input"/>
			           <% } else  if (codMonoTipoGrado.equals("R") ) { %>
			           		<af:comboBox name="<%=nameCombo%>"
			                     size="1"
			                     title="Grado Capacita"
			                     multiple="false"
			                     required="false"
			                     focusOn="false"
			                     moduleName="COMBO_GRADO_FILTRI_CM_ARTI"
			                     addBlank="true"
			                     blankValue="0"
			                     disabled="<%= String.valueOf( readOnly )%>"
			                     selectedValue="<%=CODMONOCAP%>"					                     
			                     classNameBase="input"/>
			           <% } 
			        } else { %>
		    		<af:comboBox name="<%=nameCombo%>"
			                     size="1"
			                     title="Grado Capacita"
			                     multiple="false"
			                     required="false"
			                     focusOn="false"
			                     moduleName="COMBO_GRADO_FILTRI_CM"
			                     addBlank="true"
			                     blankValue="0"
			                     disabled="<%= String.valueOf( readOnly )%>"
			                     selectedValue="<%=CODMONOCAP%>"					                     
			                     classNameBase="input"/>
			      <% } %>
		    	</td>
		  	</tr>
		  <% } %>
  		</table>
  	</td>
  </tr>
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr><td colspan="2">
      <%if(!readOnly && !rosaFiltrata) {%>
       <input class="pulsante" type="submit" name="filtro" value="Filtra">&nbsp;&nbsp;&nbsp;&nbsp;
      <%}%>
       <input class="pulsante" type="button" name="chiudi" value="Chiudi" onClick="cameBack('MatchDettRosaPage')">
  </td></tr>
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr><td colspan="2">&nbsp;</td></tr>
 
</table>
<%out.print(htmlStreamBottom);%></p>

<input type="hidden" name="PAGE" value="FiltriCMPerRosaIDOPage"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
<input type="hidden" name="prgRichiestaAz" value="<%=prgRichiestaAz%>"/>
<input type="hidden" name="prgRosa" value="<%=prgRosa%>"/>
<input type="hidden" name="NUMKLOROSA" value="<%=numKloRosa%>"/>
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
<input type="hidden" name="cpiRose" value="<%=cpiRose%>"/>
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


</af:form>

</body>
</html>
