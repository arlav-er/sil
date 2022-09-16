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
<html>
<%


Vector listeSpecRows = serviceResponse.getAttributeAsVector("M_GETSPECIFCOLLMIRATO.ROWS.ROW");
String tipoRaggiunto = "";
   SourceBean listeSpec_Row = null;
   String cdnLavoratore     = null;
   String dataInizio        = null;
   String dataFine          = null;
   String codTipoIscr       = null;
   String codTipoInvalidita = null;
   String strNote           = null;
   String dtmIns            = null;
   String dtmMod            = null;
   String datAccSanitario   = null;
   String codAccSanitario   = null;
   String datAnzianita68	= null;
   String datUltimaIscr	=null;
   
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
   
   BigDecimal percInval     = null;
   BigDecimal cdnUtMod      = null;
   BigDecimal cdnUtIns      = null;
   BigDecimal keyLock       = null;
   BigDecimal prgCMIscr     = null;
   boolean readOnlyStr      = false;
   boolean canModifyNote    = false;
   boolean flag_insert		=false;
   Testata operatoreInfo    = null;   

   cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");

   
     InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
     testata.setPaginaLista("CollMiratoInfStorPage");
     testata.setSkipLista(true);
    
     if(listeSpecRows != null && !listeSpecRows.isEmpty()) 
     { listeSpec_Row  = (SourceBean) listeSpecRows.elementAt(0);
       dataInizio     = (String)     listeSpec_Row.getAttribute("DATINIZIO"); 
       dataFine       = (String)     listeSpec_Row.getAttribute("DATFINE");
       codTipoIscr    = (String)     listeSpec_Row.getAttribute("CODCMTIPOISCR");
       codTipoInvalidita = (String)  listeSpec_Row.getAttribute("CODTIPOINVALIDITA");
       percInval      = (BigDecimal) listeSpec_Row.getAttribute("NUMPERCINVALIDITA");
       strNote        = (String)     listeSpec_Row.getAttribute("STRNOTE");
       datAnzianita68 = (String)     listeSpec_Row.getAttribute("DATANZIANITA68");
       datUltimaIscr	= (String)	   listeSpec_Row.getAttribute("DATULTIMAISCR");       
       dtmIns         = (String)     listeSpec_Row.getAttribute("DTMINS");
       dtmMod         = (String)     listeSpec_Row.getAttribute("DTMMOD");
       cdnUtIns       = (BigDecimal) listeSpec_Row.getAttribute("CDNUTINS");
       cdnUtMod       = (BigDecimal) listeSpec_Row.getAttribute("CDNUTMOD");
       keyLock        = (BigDecimal) listeSpec_Row.getAttribute("NUMKLOCMISCR");
       prgCMIscr      = (BigDecimal) listeSpec_Row.getAttribute("PRGCMISCR");
       numIscrizione = ((BigDecimal) listeSpec_Row.getAttribute("NUMISCRIZIONE")).toString(); 
       String codMonoTipoRagg = (String)listeSpec_Row.getAttribute("CODMONOTIPORAGG");
       numMesiSospEsterni = ((BigDecimal) listeSpec_Row.getAttribute("NUMMESISOSPESTERNI")).toString();  
	   prgVerbaleAcc = (String) listeSpec_Row.getAttribute("verbale"); 
	   datSospensione = (String) listeSpec_Row.getAttribute("DATSOSPENSIONE");  
	   prgSpiMod = (String) listeSpec_Row.getAttribute("operatore");  
	   datAccSanitario = (String) listeSpec_Row.getAttribute("DATACCERTSANITARIO"); 
	   codAccSanitario = (String) listeSpec_Row.getAttribute("codAccSanitario");  
	   numProtV = (BigDecimal) listeSpec_Row.getAttribute("NUMPROTOCOLLO"); 
	   dataOraProt = (String) listeSpec_Row.getAttribute("DATAORAPROT"); 
	   numAnnoProtV = (BigDecimal) listeSpec_Row.getAttribute("NUMANNOPROT"); 
		
		if (!dataOraProt.equals("")) {
	  		oraProtV = dataOraProt.substring(11,16);
	  		datProtV = dataOraProt.substring(0,10);
  		}  	
  		
  		CODSTATOATTO = (String) listeSpec_Row.getAttribute("STATO");  
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
   PageAttribs attributi = new PageAttribs(user, "AmstrListeSpecCmPage");
  
   
  readOnlyStr = true; //!attributi.containsButton("AGGIORNA");

  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);

%>
<head>
<title>Amministrazione - Liste Speciali: Colocamento Mirato</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<%@ include file="CommonScript.inc"%>

<script  language="JavaScript">

function underConstr()
{ alert("Funzionaliltà non ancora attivata.");
}

</script>


</head>
<body class="gestione">
<br/>
<% 
   testata.show(out);
%>

<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_SaveCmIscr"/>
 <af:showMessages prefix="M_InsCmIscr"/>
</font>

<af:form name="form1" method="POST" action="AdapterHTTP">
<p align="center">
<table class="main">
<tr><td colspan="2"><p class="titolo">Informazioni storiche relative al collocamento mirato</p></td></tr>
</table>
<%out.print(htmlStreamTop);%>
<%@ include file="CollMiratoCampiLayOut.inc"%>
<%out.print(htmlStreamBottom);%>

<br/>
<center>
  <% operatoreInfo.showHTML(out); %>
</center>

<br>
<table class="main">
<tr><td><br/></td></tr>
<tr><%if(!readOnlyStr){%>
    <td width="33%"></td>
    <td align="center" width="33%">
           <%keyLock= keyLock.add(new BigDecimal(1));%>
           <input class="pulsante" type="submit" name="save" value="Aggiorna"></td>
    <td align="right" width="33%"> <%} else {%>
    <td align="center"><%}%>
    <input class="pulsante" type="button" name="lista" value="Torna alla lista"
             onClick="checkChange('CollMiratoInfStorPage','&cdnLavoratore=<%=cdnLavoratore%>')"></td>
</tr>
</table>

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


</af:form>

</body>
</html>
