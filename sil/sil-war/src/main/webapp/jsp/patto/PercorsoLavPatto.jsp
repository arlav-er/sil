<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,                  
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  java.lang.*,
                  java.text.*
                  "   %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<% 
   BigDecimal cdnLavoratore        = null;
   String     strCognome           = null;
   String     strNome              = null;
   BigDecimal PRGDICHDISPONIBILITA = null;
   String     DATSTIPULA           = null;
   String     CODSTATOATTO         = null;
   BigDecimal PRGSTATOOCCUPAZ      = null;
   String     FLGCOMUNICAZESITI    = null;
   String     FLGPATTO297          = null;
   String     codCodificaPatto     = null;
   BigDecimal PRGPATTOLAVORATORE   = null;
   String     DATSCADCONFERMA      = null;
   String     STRNOTE              = null;
   String     CODMOTIVOFINEATTO    = null;
   String     DATFINE              = null;
   BigDecimal cdnUtIns             = null;
   String     dtmIns               = null;
   BigDecimal cdnUtMod             = null;
   String     dtmMod               = null;
   BigDecimal NUMKLOPATTOLAVORATORE= new BigDecimal(-1.0);
   String     codCPI               = null;
   String     descCPI              = null;
   String     CognIns              = null;
   String     NomIns               = null;
   String     CognMod              = null;
   String     NomMod               = null;
   String     DATDICHIARAZIONE     = null;
   String     datInizio            = null;
   String     descStato            = null;
   String     codStatoOcc          = null;
   String codStatoAtto = null, codServizio=null, codTipoPatto = null;
  
  String cdnLavoratoreStr = (String) serviceRequest.getAttribute("CDNLAVORATORE");
  cdnLavoratore = new BigDecimal(cdnLavoratoreStr);
  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");

  SourceBean row=(SourceBean) serviceResponse.getAttribute("M_PATTODETTAGLIOSTORICO.ROWS.ROW");
  
  cdnLavoratore        =  (BigDecimal) row.getAttribute("CDNLAVORATORE");
  strCognome           =  (String)     row.getAttribute("strCognome");
  strNome              =  (String)     row.getAttribute("strNome");
  PRGDICHDISPONIBILITA =  (BigDecimal) row.getAttribute("PRGDICHDISPONIBILITA");      
  DATSTIPULA           =  (String)     row.getAttribute("DATSTIPULA");      
  CODSTATOATTO         =  (String)     row.getAttribute("CODSTATOATTO");      
  PRGSTATOOCCUPAZ      =  (BigDecimal) row.getAttribute("PRGSTATOOCCUPAZ");
  FLGCOMUNICAZESITI    =  (String)     row.getAttribute("FLGCOMUNICAZESITI");      
  FLGPATTO297          =  (String)     row.getAttribute("FLGPATTO297");
  codCodificaPatto     =  (String)	   row.getAttribute("codcodificapatto");
  PRGPATTOLAVORATORE   =  (BigDecimal) row.getAttribute("PRGPATTOLAVORATORE");
  DATSCADCONFERMA      =  (String)     row.getAttribute("DATSCADCONFERMA");
  STRNOTE              =  (String)     row.getAttribute("STRNOTE");
  CODMOTIVOFINEATTO    =  (String)     row.getAttribute("CODMOTIVOFINEATTO");
  DATFINE              =  (String)     row.getAttribute("DATFINE");
  cdnUtIns             =  (BigDecimal) row.getAttribute("CDNUTINS");
  dtmIns               =  (String)     row.getAttribute("DTMINS");
  cdnUtMod             =  (BigDecimal) row.getAttribute("CDNUTMOD");
  dtmMod               =  (String)     row.getAttribute("DTMMOD");
  NUMKLOPATTOLAVORATORE=  (BigDecimal) row.getAttribute("NUMKLOPATTOLAVORATORE");
  //codCPI               =  (String)     row.getAttribute("CODCPI");            
  descCPI              =  (String)     row.getAttribute("descCPI");            
  codStatoOcc          =  (String)     row.getAttribute("CODICE");
  CognIns              =  (String)     row.getAttribute("CognIns");            
  NomIns               =  (String)     row.getAttribute("NomIns");            
  CognMod              =  (String)     row.getAttribute("CognMod");                  
  NomMod               =  (String)     row.getAttribute("NomMod");            
  DATDICHIARAZIONE     =  (String)     row.getAttribute("DATDICHIARAZIONE");                  
  datInizio            =  (String)     row.getAttribute("DATINIZIO");                   
  descStato            =  (String)      row.getAttribute("DESCRIZIONESTATO");
  codServizio = (String)row.getAttribute("codServizio");
codTipoPatto =(String)      row.getAttribute("CODTIPOPATTO");
codStatoAtto = (String)      row.getAttribute("codStatoAtto");
/////////////////////  protocollazione     /////////////////////////////////
    SourceBean infoProtocollo = (SourceBean)serviceResponse.getAttribute("M_INFOPROTOCOLLO.ROWS.ROW");
    String numProt  = "";

    String dataProt = "";
    String oraProt  = "";
    String docInOrOut = "";
    String docRif     = "";
    String dataOraProt = "";
    if(infoProtocollo!=null )    { 
        numProt  = SourceBeanUtils.getAttrStrNotNull(infoProtocollo,"NUMPROTOCOLLO");
		dataOraProt = StringUtils.getAttributeStrNotNull(infoProtocollo,"datOraProtocollo");
        dataProt = StringUtils.getAttributeStrNotNull(infoProtocollo,"datProtocollo");
        oraProt  = StringUtils.getAttributeStrNotNull(infoProtocollo,"oraProtocollo");
        docInOrOut  = StringUtils.getAttributeStrNotNull(infoProtocollo,"DESCIO");
        docRif  = StringUtils.getAttributeStrNotNull(infoProtocollo,"DOCAMBITO");        
    }

int size = 11, max = 65;
boolean canModify = false;
boolean canPrint = false;
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String labelServizio = "Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equalsIgnoreCase("1")){
	labelServizio = "Area";
}
%>

<html>
<head>
<title>Percorso lavoratore: patto/accordo lavoratore</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<af:linkScript path="../../js/"/>

<SCRIPT language="JavaScript">
// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


  function fieldChanged() {
    <%if (canModify) {out.print("flagChanged = true;");}%>
  }

function chiudi() {

  ok=true;
  if (flagChanged) {
     if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
         ok=false;
     }
  }
  if (ok) {
      sceglipage(3,'<%= cdnLavoratore %>');
  }
}



function sceglipage(Scelta, cdnLavoratore){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

  if (Scelta == 1) {
      document.Frm1.PAGE.value="PattoLavDettaglioPage";   
      doFormSubmit(document.Frm1);    
  }
  else if (Scelta == 2) {
    document.Frm1.PAGE.value="stampaReportPage";   
    doFormSubmit(document.Frm1);
  }
  else if (Scelta==3) {
    document.Frm1.PAGE.value="PattoInformazioniStorichePage";   
        doFormSubmit(document.Frm1);
  }
}



function mostra(id)
{var div = document.getElementById(id);
 div.style.display="";
}

function nascondi(id)
{var div = document.getElementById(id);
 div.style.display="none";
}

//by Davide                                                                    
function showIfsetVar()                                                        
{ var valoreSel = document.Frm1.flgPatto297.value;                 
                                                                               
  //se patto 150                                                               
  if(valoreSel == "S")                        
  { mostra  ("infoPatto1");                                                        
    mostra  ("infoPatto2");                                                        
    mostra  ("infoPatto3");                                                        
    mostra  ("infoPatto4");                                                        
    mostra  ("labelVisulizza");                                                    
    nascondi("labelNascondi");                                                   
//    nascondi("titoloIni");                                                       
//    mostra  ("titolo297");                                                         
//    nascondi("titoloAcc");                                                       
    document.Frm1.datInizio.validateOnPost = "true";                       
    document.Frm1.datDichDisponibilita.validateOnPost = "true";            
    document.Frm1.PRGDICHDISPONIBILITA.value = document.Frm1.PRGDICHDISP.value;
  }//se accordo                                                                
  else if(valoreSel == "N")                   
  { nascondi("infoPatto1");                                                      
    mostra  ("infoPatto2");                                                      
    nascondi("infoPatto3");                                                      
    mostra  ("infoPatto4");                                                      
    nascondi("labelVisulizza");                                                  
    mostra  ("labelNascondi");                                                   
//    nascondi("titoloIni");                                                       
//    nascondi("titolo297");                                                       
//    mostra  ("titoloAcc");
    document.Frm1.datInizio.validateOnPost = "false";                      
    document.Frm1.datDichDisponibilita.validateOnPost = "false";           
    document.Frm1.PRGDICHDISPONIBILITA.value = "";                             
  }                                                                            
  else                                                                         
  { nascondi("infoPatto1");                                                      
    nascondi("infoPatto2");                                                      
    nascondi("infoPatto3");                                                      
    nascondi("infoPatto4");                                                      
    nascondi("labelVisulizza");                                                  
    nascondi("labelNascondi");           
    /*                                        
    mostra  ("titoloIni");                                                       
    nascondi("titolo297");                                                       
    nascondi("titoloAcc");
    */
  }                                                                            
}                                                                              
function underConstr()
{ alert("Funzionaliltà non ancora attivata.");
}

</SCRIPT>
</head>

<body class="gestione" onLoad="<%= canModify ? "javascript:showIfsetVar()" : ""%>">

<%  
  Testata operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
%>

<br>
<font color="red"><af:showErrors/></font>


<af:form method="POST" action="AdapterHTTP" name="Frm1">
<%out.print(htmlStreamTop);%>
<table class="main">

<tr><td class="titolo" colspan="2"><p class="titolo">Patto/Accordo Lavoratore</p></td></tr></table>
<br>
<table>
	<tr>
		  	<td class="etichetta">Stato patto/accordo</td>
		    <td class="campo">
	            <af:comboBox classNameBase="input" name="CODSTATOATTO"  moduleName="M_STATOATTOPATTO" 
	            		selectedValue="<%=CODSTATOATTO%>"
                        addBlank="true" blankValue=""  title="Stato patto"
                        disabled="<%=String.valueOf(!canModify)%>"/> 
			</td>
	</tr>
<tr>
	<td  class="etichetta2">Data protocollo</td>
	<td class="campo">
		<table width="80%">		
        <tr>
            <td class="campo2"><strong><%=dataProt%></strong></td>
            <td  class="etichetta2">Ora<td class="campo2"><strong><%=oraProt%></strong></td>
            <td  class="etichetta2">Num. protocollo<td class="campo2"><strong><%=numProt%></strong></td>
		  </tr>
	  </table>	
	</td>
</tr>
<tr>
  <td colspan="2">
	
  </td>
</tr>
<tr><td colspan="2"><div class="sezione2">&nbsp;</div></td></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
    <td class="etichetta">Tipologia</td>
    <td class="campo">
	    <input type="hidden" name="flgPatto297" value="<%=FLGPATTO297%>" size="10" maxlength="10">
	    <af:comboBox name="codCodificaPatto" classNameBase="input"  addBlank="false" required="true" title="Tipologia" 
	      	moduleName="M_GetCodificaTipoPatto" selectedValue="<%=codCodificaPatto%>" disabled="true" />
    </td>
</tr>
<%if (!Utils.notNull(codTipoPatto).equalsIgnoreCase(it.eng.sil.util.amministrazione.impatti.PattoBean.DB_ADESIONE_NUOVO_PROGRAMMA)) {%>
<tr>
    <td class=etichetta2>Misure concordate</td>
    <td nowrap>
        <af:comboBox name="codTipoPatto" classNameBase="input"  addBlank="true" required="true" 
        	title="Misure concordate"   truncAt="60" size="60"
            selectedValue="<%=Utils.notNull(codTipoPatto)%>" 
            moduleName="M_GETDETIPOPATTO"  disabled="true" />
                        
    </td>
</tr>
<%}%>
<tr><td>&nbsp;</td></tr>
<tr>
  <td colspan="2"><!-- Le tre etichette potrebbero diventare diverse --->
    <div id="titolo297" style="display:" class="sezione2">Informazioni valide all'atto della stipula</div>
  </td>
</tr>      

<tr><td colspan="2">
<div id="infoPatto1" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td class="etichetta">Data inserimento nell'elenco anagrafico</td>
    <td class="campo">
      <af:textBox classNameBase="input" type="date" validateOnPost="true" name="datInizio" value="<%=it.eng.sil.util.Utils.notNull(datInizio)%>"
                  readonly="true" required="true" title="Data inserimento nell'elenco anagrafico" onKeyUp="fieldChanged();"
                  size="12" maxlength="10"/>
    </td>            
  </tr>
</table></div>
</td></tr>

<tr><td colspan="2">
<div id="infoPatto2" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td class="etichetta"> 
      <div id="labelVisulizza" style="display">Cpi titolare dei dati&nbsp;</div>
      <div id="labelNascondi" style="display:none">Cpi con cui &egrave; stato stipulato l'accordo</div>
    </td>
    <td class="campo">
     <af:textBox  classNameBase="input" type="text" name="codCPI2" value="<%=it.eng.sil.util.Utils.notNull(descCPI)%>"
                 validateOnPost="true" required="true" title="CpI" readonly="true" size="32" maxlength="16"/>
    </td>    
  </tr>
</table></div>
</td></tr>

<tr><td colspan="2">
<div id="infoPatto3" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
  <tr>
    <td  class="etichetta">Data immediata disponibilità</td>
    <td class="campo">    
      <af:textBox classNameBase="input" type="date" name="datDichDisponibilita" value="<%=it.eng.sil.util.Utils.notNull(DATDICHIARAZIONE)%>"
                  required="true" readonly="true" title="Data immediata disponibilità" validateOnPost="true"
                  size="12" maxlength="10"/>
    </td>
  </tr>
</table></div>
</td></tr>

<tr><td colspan="2">
<div id="infoPatto4" style="display:">
<table cellpadding="1" cellspacing="0" border="0" width="100%">
<tr>
  <td class="etichetta">Stato occupazionale</td>
  <td  class="campo">
    <input type="hidden" name="prgStatoOccupaz" value="<%=it.eng.sil.util.Utils.notNull(PRGSTATOOCCUPAZ)%>" size="10" maxlength="10"  >            
    <%
      if (FLGPATTO297 != null && FLGPATTO297.equalsIgnoreCase("N")) {
   	  	if (serviceResponse.containsAttribute("M_GetInfoStatoOccPerAccGen.ROWS.ROW")) {
      		Vector rowsStatoOccAccGen  = serviceResponse.getAttributeAsVector("M_GetInfoStatoOccPerAccGen.ROWS.ROW");
      	    if (rowsStatoOccAccGen != null && !rowsStatoOccAccGen.isEmpty()) {
      	    	SourceBean rowStatoOccAccordo  = (SourceBean) rowsStatoOccAccGen.elementAt(0);
      	    	descStato = StringUtils.getAttributeStrNotNull(rowStatoOccAccordo, "DescrizioneStato");  
      	    }
      	}
      }
      if(descStato != null)
      { size = descStato.length()+3;
        if(size > max) 
        { descStato = descStato.substring(0,max)+"...";
          size = max + 4;
        }
      }
    %><af:textBox classNameBase="input" name="prgStatoOccupaz2" value="<%=it.eng.sil.util.Utils.notNull(descStato)%>"
                  readonly="true" required="false" title="Stato occupazionale" size="<%=size%>" maxlength="100"/>
  </td>
</tr>
</table></div>
</td></tr>

  <tr >
    <td valign="top" colspan="2"><div class="sezione2"/></div></td>
  </tr>

  <tr>
    <td class="etichetta">Data stipula</td>
    <td>
      <table cellpadding="0" cellspacing="0" border="0" frame="box" width="100%">
        <tr>
        	<td width="30%">
            	<af:textBox readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" 
            			classNameBase="input" type="date" validateOnPost="true"
                        required="true" title="Data stipula" name="DATSTIPULA" 
                        value="<%=it.eng.sil.util.Utils.notNull(DATSTIPULA)%>"
                        size="12" maxlength="10"/>
            </td>
            <% if (codServizio == null || codServizio.equals("")){ %>
            	<td colspan="2"><input type="hidden" name="CODSERVIZIO" value=""></td>
            <%} else {%>
            	<%String titoloServ = "Codice " + labelServizio; %>
	            <td class="etichetta"><%=labelServizio %> che stipula il patto/accordo</td>
	            <td class="campo">
	              <af:comboBox name="CODSERVIZIO" size="1" title="<%=titoloServ %>"
	                             multiple="false" disabled="true" 
	                             required="false"
	                             focusOn="false" moduleName="COMBO_SERVIZIO" classNameBase="input"
	                             selectedValue="<%=codServizio%>" addBlank="true" blankValue=""/>    
	            </td>
             <%}%>
         </tr>
      </table>
    </td>    
</tr>
<%if (!it.eng.sil.util.Utils.notNull(DATSCADCONFERMA).equals("")) {%>
	<tr>
	    <td class="etichetta">Data scadenza conferma</td>
	    <td class="campo">
	      <af:textBox readonly="<%=String.valueOf(!canModify)%>"  classNameBase="input" 
	      			  type="date"
	                  validateOnPost="true" name="DATSCADCONFERMA" value="<%=it.eng.sil.util.Utils.notNull(DATSCADCONFERMA)%>"
	                  required="false" title="Data scadenza conferma" size="12" maxlength="10"/>
	    </td>
	</tr>  
	<tr><td colspan="2">&nbsp;</td></tr>
<%} else {%>
  	<input type="hidden" name="DATSCADCONFERMA" value=""/>
<%}%>
  <tr>
    <td class="etichetta">Data fine patto/accordo</td>
    <td class="campo">
      <af:textBox readonly="<%=String.valueOf(!canModify)%>" 
      	 classNameBase="input" type="date" validateOnPost="true"  
      	 name="DATFINE" value="<%=it.eng.sil.util.Utils.notNull(DATFINE)%>" size="12" maxlength="10"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Motivo fine patto/accordo</td>
    <td class="campo">
      <af:comboBox disabled="<%=String.valueOf(!canModify)%>" 
      		classNameBase="input" name="CODMOTIVOFINEATTO"  moduleName="M_MOTFINEATTOPATTO" 
      		selectedValue="<%=CODMOTIVOFINEATTO%>" addBlank="true"/>
    </td>
  </tr>
  
  <tr>
    <td colspan="2"><br></td>
  </tr>
  <tr>
    <td class="etichetta">Note</td>
    <td class="campo">
      <af:textArea classNameBase="textarea" name="strNote" value="<%=it.eng.sil.util.Utils.notNull(STRNOTE)%>"
                   cols="60" rows="4" maxlength="100"
                   readonly="<%=String.valueOf(!canModify)%>" />
    </td>
  </tr>

</table>
<%out.print(htmlStreamBottom);%>


<center>
	<% operatoreInfo.showHTML(out);%>	
	<br>
	<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
</center>

</af:form>


<br>

</body>
</html>

