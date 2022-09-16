<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
Vector statoOccRows = serviceResponse.getAttributeAsVector("M_GETSPECIFSTATOOCC.ROWS.ROW");
   Vector cat181Rows   = serviceResponse.getAttributeAsVector("M_GET181CAT.ROWS.ROW");
   Vector laureaRows   = serviceResponse.getAttributeAsVector("M_GetLaureaPerCat181.ROWS.ROW");


   SourceBean rowsStatoOcc = null;
   String cdnLavoratore    = null;
   String codStatoOccLetto = null;
   String cod181           = null;
   String dataInizio       = null;
   String dataFine         = null;
   String strNote          = null;
   String dtmIns           = null;
   String dtmMod           = null;
   String indennizzo       = null;
   String pensionato       = null;
   String strNumeroAtto    = null;
   String dataAtto         = null;
   String dataRichRevisione= null;
   String dataRicGiurisdz  = null;
   String dataAnzDisoc     = null;
   String codStatoAt       = null;
   BigDecimal redditoStr   = null;
   BigDecimal mesiAnz      = null;
   String mesiAnzInt       = null; 
   BigDecimal numMesiSosp  = null;
   String numMesiSospInt   = null;
   BigDecimal cdnUtIns     = null;
   BigDecimal cdnUtMod     = null;
   BigDecimal keyLock      = null;
   BigDecimal prgStatoOccupaz = null;
   String cat181           = "", codMonoProvenienza=null, monoProvenienza=null;
   boolean readOnlyStr     = false;
   Testata operatoreInfo   = null;   

   cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
  
     InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
     testata.setMaxLenStatoOcc(80);;
    
          
      if(statoOccRows != null && !statoOccRows.isEmpty())
      { rowsStatoOcc = (SourceBean) statoOccRows.elementAt(0);      
        dataInizio      = (String)      rowsStatoOcc.getAttribute("DATINIZIO");
        dataFine        = (String)      rowsStatoOcc.getAttribute("DATFINE");
        codStatoOccLetto= (String)      rowsStatoOcc.getAttribute("CODSTATOOCCUPAZ");
        cod181          = (String)      rowsStatoOcc.getAttribute("CODCATEGORIA181");
        indennizzo      = (String)      rowsStatoOcc.getAttribute("FLGINDENNIZZATO");
        pensionato      = (String)      rowsStatoOcc.getAttribute("FLGPENSIONATO");
        numMesiSosp     = (BigDecimal)  rowsStatoOcc.getAttribute("NUMMESISOSP");
        redditoStr      = (BigDecimal)  rowsStatoOcc.getAttribute("NUMREDDITO");
        dataAnzDisoc    = (String)      rowsStatoOcc.getAttribute("DATANZIANITADISOC");
        strNote         = (String)      rowsStatoOcc.getAttribute("STRNOTE");
        cdnUtIns        = (BigDecimal)  rowsStatoOcc.getAttribute("CDNUTINS");
        dtmIns          = (String)      rowsStatoOcc.getAttribute("DTMINS");
        cdnUtMod        = (BigDecimal)  rowsStatoOcc.getAttribute("CDNUTMOD");
        dtmMod          = (String)      rowsStatoOcc.getAttribute("DTMMOD");
        mesiAnz         = (BigDecimal)  rowsStatoOcc.getAttribute("MESI_ANZ");
        strNumeroAtto   = (String)      rowsStatoOcc.getAttribute("STRNUMATTO");
        dataAtto        = (String)      rowsStatoOcc.getAttribute("DATATTO");
        dataRichRevisione=(String)      rowsStatoOcc.getAttribute("DATRICHREVISIONE");
        dataRicGiurisdz = (String)      rowsStatoOcc.getAttribute("DATRICORSOGIURISDIZ");
        codStatoAt      = (String)      rowsStatoOcc.getAttribute("CODSTATOATTO");
        keyLock         = (BigDecimal)  rowsStatoOcc.getAttribute("NUMKLOSTATOOCCUPAZ");
        prgStatoOccupaz = (BigDecimal)  rowsStatoOcc.getAttribute("PRGSTATOOCCUPAZ");
        codMonoProvenienza = (String )rowsStatoOcc.getAttribute("CODMONOPROVENIENZA");
      }//if
if (codMonoProvenienza!=null) {
	if (codMonoProvenienza.equalsIgnoreCase("A"))
		monoProvenienza = "Da reg. anag.";
	else 
	if (codMonoProvenienza.equalsIgnoreCase("D"))
		monoProvenienza = "Da D.I.D.";
	else 
	if (codMonoProvenienza.equalsIgnoreCase("M"))
		monoProvenienza = "Da movimenti";
	else 
	if (codMonoProvenienza.equalsIgnoreCase("T"))
		monoProvenienza = "Da trasferimento comp.";
	else 
	if (codMonoProvenienza.equalsIgnoreCase("P"))
		monoProvenienza = "Da Porting";
	else 
	if (codMonoProvenienza.equalsIgnoreCase("N"))
		monoProvenienza = "Reg. manuale";
	else
	if (codMonoProvenienza.equalsIgnoreCase("G"))
		monoProvenienza = "Agg. manuale";
	else
	if (codMonoProvenienza.equalsIgnoreCase("O"))
		monoProvenienza = "Reg./Agg. manuale";
}
	
	
  if(numMesiSosp != null) numMesiSospInt = String.valueOf( numMesiSosp.intValue() );
   if(mesiAnz != null)     mesiAnzInt     = String.valueOf( mesiAnz.intValue() );
    
   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
   // NOTE: Attributi della pagina (pulsanti e link) 
   /*
   PageAttribs attributi = new PageAttribs(user, "StatoOccupazionalePage");
   readOnlyStr = true; //!attributi.containsButton("AGGIORNA");
    boolean canInsert   =  false; //attributi.containsButton("INSERISCI");
    boolean infStorButt =  attributi.containsButton("INF_STOR");
    */
   	readOnlyStr = true; 
    boolean canInsert = false;
    boolean infStorButt = false;
    String htmlStreamTop = StyleUtils.roundTopTable(canInsert);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canInsert);

%>
<html>
<head>
<title>Percorso lavoratore: Stato Occupazionale</title> 

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<%@ include file="CommonScript.inc"%>

</head>
<body class="gestione" onload="isSetVar()">
<%-- 
   
 testata.show(out);
--%>

<font color="red"><af:showErrors/></font>

<br/><br/>
<%out.print(htmlStreamTop);%>
<af:form name="form1" method="POST" action="AdapterHTTP">
<div align="center">

<table class="main">
<tr>
  <td colspan="4"><p class="titolo">Stato occupazionale</p>
    <div align="left" id="datiRicorsoInAtto" style="display:none">
    <UL><font color="red"><LI><strong>Revisione/Ricorso in atto</strong></LI></font></UL>
    </div>
  </td>
</tr>
<tr><td><br/></td></tr>
</table>

<script language="JavaScript">

var showButtonImg = new Image();
var hideButtonImg = new Image();
showButtonImg.src=" ../../img/aperto.gif";
hideButtonImg.src=" ../../img/chiuso.gif"


function isSetVar()
{ var opzioneSel = document.form1.codStatoAtto.value;
  var numAtto    = document.form1.strNumAtto.value;

  if((opzioneSel != null && opzioneSel != "") || (numAtto != null && numAtto != ""))
  { onOff();
    mostra("datiRicorsoInAtto");
  }
  
}//isSetVar()

function onOff()
{	var div1 = document.getElementById("dett");
	var idImm = document.getElementById("imm1");
	if (div1.style.display=="")
  {	nascondi("dett");
		idImm.src = hideButtonImg.src
	} 
	else
  {	mostra  ("dett");
		idImm.src = showButtonImg.src;
	}
}//onOff()

function mostra(id)
{ var div = document.getElementById(id);
  div.style.display="";
}

function nascondi(id)
{ var div = document.getElementById(id);
  div.style.display="none";
}


</script>

<table class="main">
<tr>
  <td class="etichetta" nowrap="nowrap">Data inizio</td>    
  <td colspan="3">
    <table align="left" border="0" width="100%">
      <tr><td width="25%">
            <af:textBox classNameBase="input" type="date" name="datInizio" value="<%=dataInizio%>" validateOnPost="true" 
                        required="true" readonly="<%=String.valueOf(readOnlyStr)%>" size="11" maxlength="10"/></td>
          <td class="etichetta">Data fine</td>
          <td><af:textBox classNameBase="input" type="date" name="datFine" value="<%=dataFine%>" validateOnPost="true" 
                          readonly="<%=String.valueOf(readOnlyStr)%>"  size="11" maxlength="11"/></td>
      </tr>
    </table>              
  </td>
</tr>
<tr>

</tr>
	<td class="etichetta">Provenienza</td>
    <td class="campo" colspan="3">
    	<af:textBox name="monoProvenienza" classNameBase="input" readonly="true" value="<%=monoProvenienza%>"/>          
    </td>
<tr>
  <td class="etichetta">Stato occupazionale </td>
  <td colspan="3">
  <table width="100%"><tr><td>
     <af:comboBox name="codStatoOcc" moduleName="M_GETDESTATOOCC" selectedValue="<%=codStatoOccLetto%>" addBlank="true"
            classNameBase="input"      title="Stato occupazionale" required="true"  disabled="<%=String.valueOf(readOnlyStr)%>" />
  </td></tr></table>
  </td>
</tr>
<tr>
  <td class="etichetta" >Anzianità di disoccupazione dal</td>
  <td colspan="3">
    <table border="0" width="100%">
    <tr><td width="25%">
        <af:textBox classNameBase="input" type="date" name="datAnzianitaDisoc" value="<%=dataAnzDisoc%>" validateOnPost="true" 
                    readonly="<%=String.valueOf(readOnlyStr)%>"  size="11" maxlength="10"/></td>
        <td colspan="2">&nbsp;</td>
    </tr>
    </table>              
  </td>
</tr>
<tr>
<td class="etichetta">Pensionato</td>
        <td class="campo">
          <af:comboBox name="flgPensionato" addBlank="false" 
	        	  classNameBase="input"
    		      disabled="true">
              <OPTION value=""  <%if (pensionato == null) out.print("SELECTED=\"true\"");%>></OPTION>
              <OPTION value="S" <%if (pensionato != null && pensionato.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
              <OPTION value="N" <%if (pensionato != null && pensionato.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
          </af:comboBox>     
        </td>
        <td colspan="2">&nbsp;</td>
</tr>        

<tr>
  <td colspan="4">    
    <div class="sezione2">
    	<a name="aaa" href="#aaa" onClick="onOff()"><img align="middle" id="imm1" alt="mostra/nascondi" src=" ../../img/chiuso.gif" border="0"></a>
    		<b>&nbsp;Revisione / Ricorso</b>
    </div>
  </td>
</tr>


<tr><td colspan="4">
<div id="dett" style="display:none">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
 <tr>
   <td class="etichetta">Num. Atto</td>
   <td>
    <table align="left"  cellpadding="0" cellspacing="0" border="0" width="100%">
      <tr><td width="25%">
          <af:textBox classNameBase="input" type="text" name="strNumAtto" value="<%=Utils.notNull(strNumeroAtto)%>"  
                      readonly="<%=String.valueOf(readOnlyStr)%>"  size="18" maxlength="10"/></td>
          <td class="etichetta">Data Atto</td>
          <td><af:textBox classNameBase="input" type="date" name="datAtto" value="<%=dataAtto%>"  
                  readonly="<%=String.valueOf(readOnlyStr)%>"  size="11" maxlength="10"/></td>
      </tr>
    </table>
   </td>
 </tr>
 <tr>
    <td class="etichetta">Stato Atto</td>
    <td colspan="3">
       <af:comboBox name="codStatoAtto" moduleName="M_GETSTATOATTO" selectedValue="<%=Utils.notNull(codStatoAt)%>"
               classNameBase="input"     addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>"/>
    </td>
 </tr>
 <tr>
    <td class="etichetta">Data Revisione</td>
    <td colspan="3"><af:textBox classNameBase="input" type="date" name="datRichRevisione" value="<%=dataRichRevisione%>"  
                                readonly="<%=String.valueOf(readOnlyStr)%>"  size="11" maxlength="10"/></td>
 </tr>
 <tr>
    <td class="etichetta">Data Ricorso</td>
    <td colspan="3"><af:textBox classNameBase="input" type="date" name="datRicorsoGiurisdz" value="<%=dataRicGiurisdz%>"  
                  readonly="<%=String.valueOf(readOnlyStr)%>" size="11" maxlength="10"/></td>
 </tr>
</table></div>
</td></tr>

<tr ><td colspan="4"><div class="sezione2"/></td></tr>
<tr><td colspan="4">
  <table border="0" width="100%">
  <tr>
  <td class="etichetta">Note<br/></td>
  <td class="campo">
    <af:textArea classNameBase="textarea" name="strNote" value="<%=strNote%>"
                 cols="60" rows="4" maxlength="1000"
                 onKeyUp="fieldChanged();" readonly="<%=String.valueOf(readOnlyStr)%>"  />
  </td>
  </tr>
  </table>
</td></tr>
</table>


<br/>

</div>

</af:form>
<%out.print(htmlStreamBottom);%>
<center>
	<% operatoreInfo.showHTML(out);%>	
	<br>
	<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
</center>

</body>
</html>
