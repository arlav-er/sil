<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  SourceBean row = (SourceBean)serviceResponse.getAttribute("M_GETSPECIFCOLLMIRATO.ROWS.ROW");
  String tipoRaggiunto = "";
  SourceBean listeSpec_Row = null;
   String cdnLavoratore = null, dataInizio = null, dataFine = null;
   String codTipoIscr = null,codTipoInvalidita = null, strNote = null,dtmIns = null,dtmMod= null;
   String datAccSanitario = null,codAccSanitario = null, datUltimaIscr=null, datAnzianita68=null ;
   BigDecimal percInval = null,cdnUtMod = null,cdnUtIns = null,keyLock = null,prgCMIscr= null;
   String numMesiSospEsterni = "";
   String numIscrizione = "";
   String prAutomatica     = "S";
   String docInOut         = "I";
   String docRif           = "Documentazione L68";
   String docTipo          = "ISCRIZIONE LEGGE 68/99";
   BigDecimal numProtV     = null;
   BigDecimal numAnnoProtV = null;
   String dataOraProt      = "";
   String datProtV     = "";
   String oraProtV     = "";
   String CODSTATOATTO = "";
  	
  	
   String prgSpiMod = "";
   String datSospensione="";
   String prgVerbaleAcc = "";
   boolean readOnlyStr      = false;
   boolean canModifyNote    = false;
   Testata operatoreInfo    = null; 
   boolean flag_insert = false;  

   cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
	
	//InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
     
     if(row!=null)  { 
     	dataInizio     = (String)     row.getAttribute("DATINIZIO"); 
		dataFine       = (String)     row.getAttribute("DATFINE");
		codTipoIscr    = (String)     row.getAttribute("CODCMTIPOISCR");
		codTipoInvalidita = (String)  row.getAttribute("CODTIPOINVALIDITA");
		percInval      = (BigDecimal) row.getAttribute("NUMPERCINVALIDITA");
		strNote        = (String)     row.getAttribute("STRNOTE");
        datAnzianita68 = (String)     row.getAttribute("DATANZIANITA68");
        datUltimaIscr  = (String)	  row.getAttribute("DATULTIMAISCR");
		dtmIns         = (String)     row.getAttribute("DTMINS");
		dtmMod         = (String)     row.getAttribute("DTMMOD");
		cdnUtIns       = (BigDecimal) row.getAttribute("CDNUTINS");
		cdnUtMod       = (BigDecimal) row.getAttribute("CDNUTMOD");
		keyLock        = (BigDecimal) row.getAttribute("NUMKLOCMISCR");
		prgCMIscr      = (BigDecimal) row.getAttribute("PRGCMISCR");
		numIscrizione = row.getAttribute("NUMISCRIZIONE") == null? "" : ((BigDecimal)row.getAttribute("NUMISCRIZIONE")).toString();
		String codMonoTipoRagg = (String)row.getAttribute("CODMONOTIPORAGG");
		numMesiSospEsterni = row.getAttribute("NUMMESISOSPESTERNI") == null? "" : ((BigDecimal)row.getAttribute("NUMMESISOSPESTERNI")).toString();
		prgVerbaleAcc = row.getAttribute("verbale") == null? "" : row.getAttribute("verbale").toString();
		datSospensione     = (String)     row.getAttribute("DATSOSPENSIONE");
		prgSpiMod = row.getAttribute("operatore") == null? "" : row.getAttribute("operatore").toString();
		datAccSanitario = (String) row.getAttribute("DATACCERTSANITARIO");
		codAccSanitario = (String) row.getAttribute("codAccSanitario");
		numProtV = (BigDecimal) row.getAttribute("NUMPROTOCOLLO"); 
		dataOraProt = row.getAttribute("DATAORAPROT") == null? "" : (String)row.getAttribute("DATAORAPROT"); 
		numAnnoProtV = (BigDecimal) row.getAttribute("NUMANNOPROT"); 
		
		if (!dataOraProt.equals("")) {
	  		oraProtV = dataOraProt.substring(11,16);
	  		datProtV = dataOraProt.substring(0,10);
  		}  	
  		
  		CODSTATOATTO = row.getAttribute("STATO") == null? "" : (String)row.getAttribute("STATO");
		
		if (codMonoTipoRagg!=null && codMonoTipoRagg.equals("D"))
			tipoRaggiunto = "Disabili";
		 else if (codMonoTipoRagg!=null && codMonoTipoRagg.equals("A"))
		     tipoRaggiunto = "Altri";
     }
     
    String cpiCompLav = "";
	SourceBean cpiCompSb = (SourceBean) serviceResponse.getAttribute("CM_GET_CODCPI.ROWS.ROW");				
	if (cpiCompSb != null) {	
		cpiCompLav = (String) cpiCompSb.getAttribute("DESCRIZIONE");
	} 
	
	operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

   // NOTE: Attributi della pagina (pulsanti e link) 
//   PageAttribs attributi = new PageAttribs(user, "AmstrListeSpecCmPage");
	readOnlyStr = true; //!attributi.containsButton("AGGIORNA");
    String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
   
%>

<html>

<head>
<title>Percorso lavoratore: Colocamento mirato</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<%@ include file="CommonScript.inc"%>

<script  language="JavaScript">

function underConstr(){ 
	alert("Funzionaliltà non ancora attivata.");
}

</script>


</head>
<body class="gestione">
<br/>

<font color="red"><af:showErrors/></font>

<af:form name="form1" method="POST" action="AdapterHTTP">
<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main">
	<tr><td colspan="2"><p class="titolo">Collocamento mirato</p></td></tr>
</table>

<%@ include file="CollMiratoCampiLayOut.inc"%>


<br/>


<br>
<table class="main">
<tr><td><br/></td></tr>

</table>
<%out.print(htmlStreamBottom);%>
  <input type="hidden" name="PAGE" value="CollMiratoInfStorDettPage"/>
  <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
  <input type="hidden" name="cdnUtMod" value="<%=cdnUtMod %>"/>
  <input type="hidden" name="keyLockCMIscr" value="<%=keyLock%>"/>
  <input type="hidden" name="prgCMiscr"     value="<%=Utils.notNull(prgCMIscr)%>"/>

  <!-- valori temporanei che andranno poi inseriti nella JSP-->
  <input type="hidden" name="prgDichDisponibilita"     value="0"/>
  <input type="hidden" name="numBaseDiPartenza"        value="0"/>
  <input type="hidden" name="numPuntiAnziznita"        value="0"/>
  <input type="hidden" name="numPuntiFigliInvaCarico"  value="0"/>
  <input type="hidden" name="numPuntiAltrePersACarico" value="0"/>
  <input type="hidden" name="numPuntiReddito"          value="0"/>

<center>
	<% operatoreInfo.showHTML(out); %>
	<br>
	<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
</center>
</af:form>

</body>
</html>
