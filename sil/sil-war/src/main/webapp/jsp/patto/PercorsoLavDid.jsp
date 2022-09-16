<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import="com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.util.*,
                  java.util.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*,
                  java.math.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
  //flag booleano che discrimina l'inserimento o la modifica
  boolean flag_insert=false;

  //inizializzo i campi
  BigDecimal cdnLavoratore = null;
  String strCognome = null;
  String strNome = null;
  
  BigDecimal prgDichDisponibilita = null;
  String CODSTATOATTO = null;
  
  String datDichiarazione = null;
  BigDecimal prgElencoAnagrafico= null ;
  String CODTIPODICHDISP = null;
  String CODULTIMOCONTRATTO = null;
  BigDecimal PRGSTATOOCCUPAZ = null;
  String CODSTATOOCCUPAZ = null;
  String descStato = null;
  String DATSCADCONFERMA = null;
  String DATSCADEROGAZSERVIZI = null;
  String STRNOTE = null;
  String CODMOTIVOFINEATTO = null;
  String DATFINE = null;
  BigDecimal cdnUtIns = null;
  String dtmIns = null;
  BigDecimal cdnUtMod = null;
  String dtmMod = null;
  BigDecimal NUMKLODICHDISP = null;
  String datInizio = null;
  String codCPI = null;    
  String descCPI = null; 
  Testata operatoreInfo = null;
  
  //se è stato richiesto l'inserimento abbiamo nella servicerequest
  //il parametro "inserisci" che corrisponde al pulsante premuto
  if (serviceRequest.containsAttribute("Inserisci")){
    flag_insert=true;
  } else {
    flag_insert=false;
  }

  if (flag_insert) {
    try{
      cdnUtIns = (BigDecimal)serviceRequest.getAttribute("cdnUtIns");
      prgDichDisponibilita = (BigDecimal)serviceRequest.getAttribute("prgDichDisponibilita");     
    }
    catch(Exception ex){
      // non faccio niente
    }
  }//if

  
  String codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_").toString();

    Vector rows= serviceResponse.getAttributeAsVector("M_DISPODETTAGLIOSTORICO.ROWS.ROW");

    if (rows.isEmpty())    {   
    	flag_insert=true;
        String cdnLavoratoreStr = (String) serviceRequest.getAttribute("CDNLAVORATORE");
        cdnLavoratore = new BigDecimal(cdnLavoratoreStr);
    } 
    
    if(!flag_insert && rows != null && !rows.isEmpty())    {
    SourceBean row = (SourceBean) rows.elementAt(0);
     cdnLavoratore        = (BigDecimal) row.getAttribute("CDNLAVORATORE");
     strCognome           = (String)     row.getAttribute("strCognome");          																																				 
     strNome              = (String)     row.getAttribute("strNome");             																																	 
     prgDichDisponibilita = (BigDecimal) row.getAttribute("prgDichDisponibilita");																																	 
     CODSTATOATTO         = (String)     row.getAttribute("CODSTATOATTO");        																																	 
     PRGSTATOOCCUPAZ      = (BigDecimal) row.getAttribute("prgStatooccupaz");     																																	 
     CODSTATOOCCUPAZ      = (String)     row.getAttribute("codStatoOccupaz");     																																	 
     descStato            = (String)     row.getAttribute("DESCRIZIONESTATO");
     datDichiarazione     = (String)     row.getAttribute("datDichiarazione");    																																	 
     prgElencoAnagrafico  = (BigDecimal) row.getAttribute("prgElencoAnagrafico"); 																																	 
     CODTIPODICHDISP      = (String)     row.getAttribute("CODTIPODICHDISP");     																																	 
     CODULTIMOCONTRATTO   = (String)     row.getAttribute("CODULTIMOCONTRATTO"); 																																	 
     DATSCADCONFERMA      = (String)     row.getAttribute("DATSCADCONFERMA");     																																	 
     DATSCADEROGAZSERVIZI = (String)     row.getAttribute("DATSCADEROGAZSERVIZI");																																		 
     STRNOTE              = (String)     row.getAttribute("STRNOTE");             																																	 
     CODMOTIVOFINEATTO    = (String)     row.getAttribute("CODMOTIVOFINEATTO");   																																	 
     DATFINE              = (String)     row.getAttribute("DATFINE");             																																	 
     NUMKLODICHDISP       = (BigDecimal) row.getAttribute("NUMKLODICHDISP");      																																	 
     datInizio            = (String)     row.getAttribute("datInizio");           																																	 
     codCPI               = (String)     row.getAttribute("codCpi");              																																	 
     descCPI              = (String)     row.getAttribute("descCPI");             																																	 
    
     cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
     dtmIns   = (String)     row.getAttribute("DTMINS");
     cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
     dtmMod   = (String)     row.getAttribute("DTMMOD");
    } 
    

   
//    PageAttribs attributi = new PageAttribs(user, "DispoDettaglioPage");
    boolean rdOnly   = true; //!attributi.containsButton("AGGIORNA");
    boolean canInsert=  false; //attributi.containsButton("INSERISCI");
    boolean canPrint =  false;//attributi.containsButton("STAMPA");

   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

  
  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  
  

	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);


%>




<html>
<head>
<title>Percorso lavoratore: Dichiarazione di immediata disponibilità</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<af:linkScript path="../../js/"/>


</head>
<body  class="gestione">

<font color="red"><af:showErrors/></font>

<af:form name="Frm1" method="POST" action="AdapterHTTP">
<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main">
<tr><td class="titolo" colspan="5"><p class="titolo">Dichiarazione immediata disponibilità</p></td></tr>
<tr><td>&nbsp;</tr>
<tr ><td colspan="5"><div class="sezione">Informazioni valide all'atto della dichiarazione</div></td></tr>
<tr>
   <td class="etichetta"> Data inserimento nell'elenco anagrafico &nbsp;</td>
     <td class="campo"><af:textBox classNameBase="input" type="date" name="datInizio" value="<%=Utils.notNull(datInizio)%>"
                                   validateOnPost="true" readonly="true" 
                                   required="true" title="Data inserimento nell'elenco anagrafico"
                                   size="11" maxlength="10"/></td>
</tr>
<tr>
    <td class="etichetta"> Cpi titolare dei dati&nbsp;</td>
    <td class="campo">
    <%
    	String strCpI = null;
      	if(descCPI != null) {
            strCpI = descCPI;
            strCpI += " - " + codCPI;
        }
    %>
          <af:textBox classNameBase="input" type="text" name="codCPI" value="<%=Utils.notNull(strCpI)%>" validateOnPost="true" 
                      readonly="true" required="true" title="Cpi titolare dei dati"
                      size="45" maxlength="16"/>
    </td>    
</tr>

<tr>
 <td class="etichetta">Stato occupazionale&nbsp;</td>
  <td nowrap class="campo">
    <input type="hidden" name="prgStatoOccupaz" value="<%=Utils.notNull(PRGSTATOOCCUPAZ)%>" >          
    <input type="hidden" name="codStatoOccupaz" value="<%=Utils.notNull(CODSTATOOCCUPAZ)%>" > 
<%	int size = 11, max = 65;
    if(descStato != null) { 
      	size = descStato.length()+3;
    	if(size > max) { 
        	descStato = descStato.substring(0,max)+"...";
          	size = max + 4;
       	}
    }
%>
    <af:textBox classNameBase="input" name="prgStatoOccupaz2" readonly="true" 
    			value="<%=Utils.notNull(descStato)%>"
                required="true" title="Stato occupazionale" size="<%=size%>" maxlength="100"/>
  </td>
</tr>

<tr ><td colspan="5"><div class="sezione"></div></td></tr>

<tr>
  <td colspan="1" class="etichetta">Data dichiarazione
  </td>
  <td  class="campo" colspan="1">
    <af:textBox classNameBase="input" type="date" name="datDichiarazione" value="<%=Utils.notNull(datDichiarazione)%>"
                readonly="<%=String.valueOf(rdOnly)%>" required="true" title="Data dichiarazione/conferma"
                validateOnPost="true" size="12" maxlength="10" />
  </td>
</tr>
<tr>
  <td colspan="1" class="etichetta">
    Stato atto &nbsp;
  </td>
  <td class="campo">
    <af:comboBox disabled ="<%=String.valueOf(rdOnly)%>"  name="CODSTATOATTO"  classNameBase="input"
    		moduleName="M_STATOATTODISPO" selectedValue="<%=Utils.notNull(CODSTATOATTO)%>"  
    		addBlank="true" blankValue="" required="true"/>
  </td>
</tr>  
<tr>
  <td class="etichetta">Tipo dichiarazione&nbsp;</td>
  <td   class="campo">
    <af:comboBox  disabled="<%=String.valueOf(rdOnly)%>"  
    		name="CODTIPODICHDISP"  moduleName="M_TIPODICHDISP" selectedValue="<%=Utils.notNull(CODTIPODICHDISP)%>"
            classNameBase="input"  addBlank="true" blankValue="" required="true" title="Tipo dichiarazione"/>
  </td>
</tr>

<tr>
  <td class="etichetta">Attività lavorativa precedente&nbsp;</td>
  <td   class="campo">
   <af:comboBox  disabled ="<%=String.valueOf(rdOnly)%>"  classNameBase="input"
   		name="CODULTIMOCONTRATTO"  moduleName="M_CONTRATTO" selectedValue="<%=Utils.notNull(CODULTIMOCONTRATTO)%>"  
   		addBlank="true"/>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
    <td class="etichetta">Data scadenza primo colloquio orientamento &nbsp;</td>
    <td class="campo">
        <af:textBox classNameBase="input" readonly="<%=String.valueOf(rdOnly)%>"  type="date"
                    name="DATSCADCONFERMA" value="<%=Utils.notNull(DATSCADCONFERMA)%>" validateOnPost="true"
                    size="12" maxlength="10"/></td>
</tr>

<tr>
    <td class="etichetta">Data scadenza stipula patto &nbsp;</td>
    <td class="campo">
       <af:textBox classNameBase="input" readonly="<%=String.valueOf(rdOnly)%>"  type="date"
                   name="DATSCADEROGAZSERVIZI" value="<%=Utils.notNull(DATSCADEROGAZSERVIZI)%>" validateOnPost="true"
                   size="12" maxlength="10"/></td>
  
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
    <td class="etichetta">Data fine atto &nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" readonly="<%=String.valueOf(rdOnly)%>"  type="data"
                  name="DATFINE" value="<%=Utils.notNull(DATFINE)%>" validateOnPost="true"
                  size="12" maxlength="10"/></td>
</tr>

<tr>
    <td class="etichetta">Motivo fine atto&nbsp;</td>
    <td class="campo">
      <af:comboBox disabled ="<%=String.valueOf(rdOnly)%>"  name="CODMOTIVOFINEATTO"  
      		moduleName="M_MOTFINEATTO" selectedValue="<%=Utils.notNull(CODMOTIVOFINEATTO)%>"  classNameBase="input"
      		addBlank="true"/></td>
</tr>
<tr ><td colspan="5"><div class="sezione"></div></td></tr>
<tr>
    <td class="etichetta">Note&nbsp;</td>
    <td class="campo">
        <af:textArea classNameBase="textarea" name="strNote" value="<%=STRNOTE%>"
                 cols="60" rows="4" maxlength="100"
                 readonly="<%=String.valueOf(rdOnly)%>"/></td>
</tr>

</table>




<%out.print(htmlStreamBottom);%>
<center>
	<% operatoreInfo.showHTML(out);%>	
	<br>
	<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
</center>

<input  type="hidden" name="CDNLAVORATORE" value="<%=Utils.notNull(cdnLavoratore)%>">    
<input  type="hidden" name="CDNUTINS" value="<%=Utils.notNull(cdnUtIns)%>">    
<input  type="hidden" name="CDNUTMOD" value="<%=Utils.notNull(cdnUtMod)%>">    
<input  type="hidden" name="DTMINS" value="<%=Utils.notNull(dtmIns)%>">    


<input type="hidden" name="REPORT" value="">      
<input type="hidden" name="PROMPT0" value=""> 
<input type="hidden" name="PROMPT1" value=""> 
<input type="hidden" name="PAGE" value="DispoLavDettaglioInformazioniStorichePage">


</af:form>


</body>
</html>
