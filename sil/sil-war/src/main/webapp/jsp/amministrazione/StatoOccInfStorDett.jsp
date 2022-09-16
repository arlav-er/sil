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
<%!
 private String getCat181(int eta, String flgObScolastico, String laurea)
 { if ((eta > 15 && eta < 18) && flgObScolastico.equals("S")) return "Adolescente (Min: Adolescenti)";
   else if ((eta > 18 && eta < 25) || ( (eta > 25 && eta < 29) && laurea.equalsIgnoreCase("S") )) return "Giovane (Min: Giovani)";
   return "Non giovane";
 }
%> 
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
   BigDecimal mesiAnzPrec      = null;
   String mesiAnzPrecInt       = null;
   String mesiAnzInt       = null; 
   BigDecimal numMesiSosp  = null;
   BigDecimal numMesiSospPrec  = null;
   String numMesiSospPrecInt   = null;
   String numMesiSospInt   = null;
   String codMonoCalcAnzPrec   = null;
   String datcalcolomesisosp = null;
   String datcalcoloanzianita = null;
   BigDecimal cdnUtIns     = null;
   BigDecimal cdnUtMod     = null;
   BigDecimal keyLock      = null;
   BigDecimal prgStatoOccupaz = null;
   String cat181           = "";
   BigDecimal numMesiSospFornero2014  = null;
   String numMesiSospFornero2014Int  = null;
   String numGiorniSospFornero2014  = null;
   String infoSospensioneFornero = null;
   boolean readOnlyStr     = false;
   Testata operatoreInfo   = null;   
   String infoMesiRischioDisocc = null;
   String infoMesiRischioDisoccCompleto = null;
   String infoMesiRischioDisoccVis = null;
   BigDecimal numGGRestantiMesiSospFornero2014  = null;
   String numGGRestantiMesiSospFornero2014Int  = null;
   BigDecimal numGGRestantiRischioDisocc  = null;
   String numGGRestantiRischioDisoccInt  = null;
   String giorniAnzInt = null;
   BigDecimal giorniAnzDallaData	= null;
   String giorniAnzDallaDataInt  = null;

   cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
  
     InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
     testata.setMaxLenStatoOcc(80);
     testata.setSkipLista(true);
    
          
      if(statoOccRows != null && !statoOccRows.isEmpty())
      { rowsStatoOcc = (SourceBean) statoOccRows.elementAt(0);      
        dataInizio      = (String)      rowsStatoOcc.getAttribute("DATINIZIO");
        dataFine        = (String)      rowsStatoOcc.getAttribute("DATFINE");
        codStatoOccLetto= (String)      rowsStatoOcc.getAttribute("CODSTATOOCCUPAZ");
        cod181          = (String)      rowsStatoOcc.getAttribute("CODCATEGORIA181");
        indennizzo      = (String)      rowsStatoOcc.getAttribute("FLGINDENNIZZATO");
        pensionato      = (String)      rowsStatoOcc.getAttribute("FLGPENSIONATO");
        numMesiSosp     = (BigDecimal)  rowsStatoOcc.getAttribute("NUMMESISOSP");
        datcalcolomesisosp = (String)   rowsStatoOcc.getAttribute("datcalcolomesisosp");
        datcalcoloanzianita = (String)  rowsStatoOcc.getAttribute("datcalcoloanzianita");
        numMesiSospPrec = (BigDecimal)  rowsStatoOcc.getAttribute("NUMMESISOSPPREC");
        redditoStr      = (BigDecimal)  rowsStatoOcc.getAttribute("NUMREDDITO");
        dataAnzDisoc    = (String)      rowsStatoOcc.getAttribute("DATANZIANITADISOC");
        strNote         = (String)      rowsStatoOcc.getAttribute("STRNOTE");
        cdnUtIns        = (BigDecimal)  rowsStatoOcc.getAttribute("CDNUTINS");
        dtmIns          = (String)      rowsStatoOcc.getAttribute("DTMINS");
        cdnUtMod        = (BigDecimal)  rowsStatoOcc.getAttribute("CDNUTMOD");
        dtmMod          = (String)      rowsStatoOcc.getAttribute("DTMMOD");
        mesiAnz         = (BigDecimal)  rowsStatoOcc.getAttribute("MESI_ANZ");
        giorniAnzDallaData  = (BigDecimal)  rowsStatoOcc.getAttribute("giorni_anz");
        mesiAnzPrec     = (BigDecimal)  rowsStatoOcc.getAttribute("MESI_ANZ_PREC");
        strNumeroAtto   = (String)      rowsStatoOcc.getAttribute("STRNUMATTO");
        dataAtto        = (String)      rowsStatoOcc.getAttribute("DATATTO");
        dataRichRevisione=(String)      rowsStatoOcc.getAttribute("DATRICHREVISIONE");
        dataRicGiurisdz = (String)      rowsStatoOcc.getAttribute("DATRICORSOGIURISDIZ");
        codStatoAt      = (String)      rowsStatoOcc.getAttribute("CODSTATOATTO");
        keyLock         = (BigDecimal)  rowsStatoOcc.getAttribute("NUMKLOSTATOOCCUPAZ");
        prgStatoOccupaz = (BigDecimal)  rowsStatoOcc.getAttribute("PRGSTATOOCCUPAZ");
        codMonoCalcAnzPrec  = (String)  rowsStatoOcc.getAttribute("CODMONOCALCOLOANZIANITAPREC297");
        infoSospensioneFornero = (String)rowsStatoOcc.getAttribute("mesiSospFornero2014");
        infoMesiRischioDisocc = (String)rowsStatoOcc.getAttribute("mesi_rischio_disocc");
        infoMesiRischioDisoccCompleto = (String)rowsStatoOcc.getAttribute("mesi_rischio_disocc_completo");
    }//if
    
    if (infoSospensioneFornero != null && !infoSospensioneFornero.equals("")) {
    	String [] sospFornero = infoSospensioneFornero.split("-");
    	if (sospFornero.length == 4) {
    		numMesiSospFornero2014 = new BigDecimal(sospFornero[0]);
        	numGiorniSospFornero2014 = sospFornero[1];
        	numGGRestantiMesiSospFornero2014 = new BigDecimal(sospFornero[3]);
    	}
    }

	if (numMesiSosp != null) {
      	numMesiSospInt = String.valueOf( numMesiSosp.intValue() );
    }
    else {
    	numMesiSospInt = "0";
   	}

	if (numMesiSospFornero2014 != null) {
    	numMesiSospFornero2014Int = String.valueOf( numMesiSospFornero2014.intValue() );	
    }
    else {
    	numMesiSospFornero2014Int = "0";	
    }
	
   	if (mesiAnz != null) {
   		mesiAnzInt = String.valueOf(mesiAnz.intValue());
   	}
   	else {
   		mesiAnzInt = "0";
   	}
   
   	if (mesiAnzPrec != null) {     
      	mesiAnzPrecInt     = String.valueOf( mesiAnzPrec.intValue() );
   	} 
   	else { 
    	mesiAnzPrecInt = "0";
   	}
   
   	if(numMesiSospPrec != null) {
   		numMesiSospPrecInt = String.valueOf( numMesiSospPrec.intValue() );
   	} 
   	else {
   		numMesiSospPrecInt = "0";
  	}
  	
  	if (mesiAnzPrecInt != null && Integer.parseInt(mesiAnzPrecInt) < 0) {
  		mesiAnzPrecInt = "0";
  	}
   	if (numMesiSospPrecInt != null && Integer.parseInt(numMesiSospPrecInt) < 0) {
   		numMesiSospPrecInt = "0"; 
   	}
   	if (infoMesiRischioDisocc == null || infoMesiRischioDisocc.equals("")) {
   		infoMesiRischioDisocc = "0";
	}
   	if (infoMesiRischioDisoccCompleto != null && !infoMesiRischioDisoccCompleto.equals("")) {
    	String [] mesiRischio = infoMesiRischioDisoccCompleto.split("-");
    	if (mesiRischio.length == 2) {
    	    numGGRestantiRischioDisocc = new BigDecimal(mesiRischio[1]);
    	}	
    }
   	if (giorniAnzDallaData != null) {
    	giorniAnzDallaDataInt = String.valueOf( giorniAnzDallaData.intValue() );
    } else {
    	giorniAnzDallaDataInt = "0";	
    }
   	
   	int ggTotaleRestantiSospensioni = 0;
   	if (numGGRestantiMesiSospFornero2014 != null) {
    	numGGRestantiMesiSospFornero2014Int = String.valueOf( numGGRestantiMesiSospFornero2014.intValue() );
    	ggTotaleRestantiSospensioni = numGGRestantiMesiSospFornero2014.intValue();
    }
    else {
    	numGGRestantiMesiSospFornero2014Int = "0";	
    }
    
    if (numGGRestantiRischioDisocc != null) {
    	numGGRestantiRischioDisoccInt = String.valueOf( numGGRestantiRischioDisocc.intValue() );
    	ggTotaleRestantiSospensioni = ggTotaleRestantiSospensioni + numGGRestantiRischioDisocc.intValue();
    }
    else {
    	numGGRestantiRischioDisoccInt = "0";	
    }
    
    int mesiAggiuntiviSospensioni = ggTotaleRestantiSospensioni/30;
    int ggResiduoTotaleSospensioni = ggTotaleRestantiSospensioni%30;
    
    int meseDiffAnzianitaGiorni = 0;
    giorniAnzInt = "0";
    int ggResiduiAnzianita = Integer.parseInt(giorniAnzDallaDataInt);
    
    if (ggResiduiAnzianita >= ggResiduoTotaleSospensioni) {
    	ggResiduiAnzianita = ggResiduiAnzianita - ggResiduoTotaleSospensioni;
    	giorniAnzInt = String.valueOf(ggResiduiAnzianita);
    }
    else {
    	if (ggResiduoTotaleSospensioni > 0) {
       		ggResiduiAnzianita = ggResiduiAnzianita + (30 - ggResiduoTotaleSospensioni);
       		meseDiffAnzianitaGiorni = 1;
       	}
       	giorniAnzInt = String.valueOf(ggResiduiAnzianita%30);	
    }
    
   	infoMesiRischioDisoccVis = infoMesiRischioDisocc + " e (" + numGGRestantiRischioDisoccInt + " giorni) ";
   	
  	operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
   	// NOTE: Attributi della pagina (pulsanti e link) 
  	PageAttribs attributi = new PageAttribs(user, "StatoOccupazionalePage");
   	readOnlyStr = true; //!attributi.containsButton("AGGIORNA");
    boolean canInsert   =  false; //attributi.containsButton("INSERISCI");
    boolean infStorButt =  attributi.containsButton("INF_STOR");
    String htmlStreamTop = StyleUtils.roundTopTable(canInsert);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canInsert);

%>
<html>
<head>
<title>Amministrazione Stato Occupazionale Dettaglio Storico</title> 

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<%@ include file="CommonScript.inc"%>

</head>
<body class="gestione" onload="isSetVar()">
<% 
   
 testata.show(out);
%>

<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_SaveStatoOcc"/>
 <af:showMessages prefix="M_InsStatoOcc"/>
</font>

<br/><br/>
<%out.print(htmlStreamTop);%>
<af:form name="form1" method="POST" action="AdapterHTTP">
<p align="center">

<table class="main">
<tr>
  <td colspan="4"><p class="titolo">Informazioni storiche relative allo stato occupazionale</p>
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
  <td class="etichetta">Data inizio</td>    
  <td colspan="3">
    <table align="left" border="0" width="100%">
      <tr><td width="25%">
            <af:textBox classNameBase="input" type="date" name="datInizio" value="<%=dataInizio%>" validateOnPost="true" 
                        required="true" readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
          <td class="etichetta">Data fine</td>
          <td><af:textBox classNameBase="input" type="date" name="datFine" value="<%=dataFine%>" validateOnPost="true" 
                          readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="11"/></td>
      </tr>
    </table>              
  </td>
</tr>

<tr>
  <td class="etichetta">Stato occupazionale </td>
  <td colspan="3">
  <table width="100%"><tr><td>
     <af:comboBox name="codStatoOcc" moduleName="M_GETDESTATOOCC" selectedValue="<%=codStatoOccLetto%>" addBlank="true"
                  title="Stato occupazionale" required="true" onChange="fieldChanged();" disabled="<%=String.valueOf(readOnlyStr)%>" />
  </td></tr></table>
  </td>
</tr>
</table>
   <%String sMesiGiorniSosp = String.valueOf(Integer.parseInt(numMesiSospInt) + Integer.parseInt(numMesiSospFornero2014Int) 
		   + Integer.parseInt(infoMesiRischioDisocc) + mesiAggiuntiviSospensioni);
   	 SourceBean row;
     BigDecimal eta = new BigDecimal("-1");
     String oblScol = null;
     String flgLaurea = "";
     if(cat181Rows != null && !cat181Rows.isEmpty())
     { row = (SourceBean) cat181Rows.elementAt(0);
       eta     = (BigDecimal) row.getAttribute("ANNI");
       oblScol = (String)     row.getAttribute("FLGOBBLIGOSCOLASTICO");
     }
     if(laureaRows != null && !laureaRows.isEmpty())
       cat181 = getCat181(eta.intValue(), oblScol, "S");
     else cat181 = getCat181(eta.intValue(), oblScol, "N");
   
   if (dataAnzDisoc != null && !dataAnzDisoc.equals("")) {
	   int calcoloMesiAnzComplessivi = Integer.parseInt(mesiAnzPrecInt) + Integer.parseInt(mesiAnzInt) - 
			   (Integer.parseInt(numMesiSospPrecInt) + Integer.parseInt(numMesiSospInt) + Integer.parseInt(numMesiSospFornero2014Int) + Integer.parseInt(infoMesiRischioDisocc) + mesiAggiuntiviSospensioni);
	   if (calcoloMesiAnzComplessivi > 0 && meseDiffAnzianitaGiorni > 0) {
     		calcoloMesiAnzComplessivi = calcoloMesiAnzComplessivi - meseDiffAnzianitaGiorni;
       }
	   
	   %>
	  <table>
	  <tr>
	  <td class="etichetta" align="top">Anzianità di disoccupazione dal </td>
	  <td colspan="3">
	    <table>
	    <tr><td>
	        <af:textBox classNameBase="input" type="date" name="datAnzianitaDisoc" value="<%=dataAnzDisoc%>" 
	               readonly="true" size="11" maxlength="10" />
	   	</td></tr>
	    </table>              
	  </td>
	 </tr>
	 
	 <tr>
	  <td class="etichetta">Mesi sosp. complessivi</td>
	  <td colspan="3">
	  <table><tr>
	  	<td nowrap>prima del&nbsp;&nbsp;&nbsp;
	  	<b><%=Utils.notNull(datcalcolomesisosp)%></b>
       	</td>
	  	<td nowrap>&nbsp;&nbsp;&nbsp;
          <b><%=numMesiSospPrecInt%></b>
        </td>
	  	<td>&nbsp;&nbsp;&nbsp;+&nbsp;succ.
        </td>
      	<td nowrap>&nbsp;&nbsp;
      	<b><%=sMesiGiorniSosp%>&nbsp;(e&nbsp;<%=String.valueOf(ggTotaleRestantiSospensioni)%>&nbsp;giorni)&nbsp;</b>
      	</td>
      	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	    <td>
	      =
	    </td>
	  	<td nowrap>&nbsp;
	         <b><%=String.valueOf(Integer.parseInt(numMesiSospPrecInt) + Integer.parseInt(numMesiSospInt) + Integer.parseInt(numMesiSospFornero2014Int) + mesiAggiuntiviSospensioni)%></b> 
	    </td>
		</tr>
		</table>
		</td>
	</tr> 
	
	<tr>
	  <td class="etichetta">Mesi anz. complessivi</td>
	  <td colspan="3">
	  	<table><tr>
	  	<td nowrap>prima del&nbsp;&nbsp;&nbsp;
	  	<b><%=Utils.notNull(datcalcoloanzianita)%></b>
       	</td>
	  	
        <td nowrap>&nbsp;&nbsp;&nbsp;
         <b><%=mesiAnzPrecInt%></b>
         <b><%=Utils.notNull(codMonoCalcAnzPrec)%></b>
        </td>
        
        <td nowrap>&nbsp;&nbsp;&nbsp;+&nbsp;succ.
        </td>
        
       <td nowrap>&nbsp;
		<b><%=mesiAnzInt%></b>
		<b>&nbsp;(e&nbsp;<%=giorniAnzDallaDataInt%>&nbsp;giorni)&nbsp;</b>
      </td>
      <td nowrap>
        -&nbsp;sosp.&nbsp;
      </td>
      <td nowrap>
        <b><%=String.valueOf(Integer.parseInt(numMesiSospPrecInt)+Integer.parseInt(numMesiSospInt)+Integer.parseInt(numMesiSospFornero2014Int) + mesiAggiuntiviSospensioni)%></b>
      	<b>&nbsp;(e&nbsp;<%=String.valueOf(ggTotaleRestantiSospensioni)%>&nbsp;giorni)&nbsp;</b>
      </td>
      <td nowrap>
        -&nbsp;rischio disocc.
      </td>
      <td nowrap>
        <b><%=infoMesiRischioDisocc%></b>
      </td>
      <td nowrap>
        =
      </td>
      <td nowrap>
		<b><%=String.valueOf(calcoloMesiAnzComplessivi)%></b>        
      	<b>&nbsp;(e&nbsp;<%=giorniAnzInt%>&nbsp;giorni)&nbsp;</b>
      </td>
	</tr>
	</table>
	</td>
	</table>
 <%
 }
 %>
 <table class="main">
<tr>
  <td colspan="4">
    <a name="aaa" href="#aaa" onClick="onOff()"><br/><br/>
    <table cellpadding="1" cellspacing="0" width="100%">
     <tr><td width="25%"><img align="absmiddle" id="imm1" alt="mostra/nascondi" src=" ../../img/chiuso.gif" border="0"><b>Revisione / Ricorso</b></td>
     </tr>
    </table>
    </a><hr width="90%"/>
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
                      readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="18" maxlength="10"/></td>
          <td class="etichetta">Data Atto</td>
          <td><af:textBox classNameBase="input" type="date" name="datAtto" value="<%=dataAtto%>"  
                  readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
      </tr>
    </table>
   </td>
 </tr>
 <tr>
    <td class="etichetta">Stato Atto</td>
    <td colspan="3">
       <af:comboBox name="codStatoAtto" moduleName="M_GETSTATOATTO" selectedValue="<%=Utils.notNull(codStatoAt)%>"
                    addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>"/>
    </td>
 </tr>
 <tr>
    <td class="etichetta">Data Revisione</td>
    <td colspan="3"><af:textBox classNameBase="input" type="date" name="datRichRevisione" value="<%=dataRichRevisione%>"  
                                readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
 </tr>
 <tr>
    <td class="etichetta">Data Ricorso</td>
    <td colspan="3"><af:textBox classNameBase="input" type="date" name="datRicorsoGiurisdz" value="<%=dataRicGiurisdz%>"  
                  readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/></td>
 </tr>
</table></div>
</td></tr>

<tr ><td colspan="4"><hr width="90%"/></td></tr>
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
<p/>
<p/>
</table>

<center>
  <% operatoreInfo.showHTML(out); %>
</center>

<br/>
<table class="main">
  <tr>
    <td width="33%">&nbsp;</td>
    <td  width="34%" align="center">
    <%if(!readOnlyStr){
      keyLock= keyLock.add(new BigDecimal(1));%>
      <input class="pulsante" type="submit" name="saveStatoOcc" value="Aggiorna">
    <%}%>
    </td>
    <td width="33%" align="center">
        <input class="pulsante" type="button" name="lista" value="Torna alla lista"
               onClick="checkChange('StatoOccInfoStorPage','&cdnLavoratore=<%=cdnLavoratore%>')"/>
    </td>
  </tr>
</table>

<input type="hidden" name="PAGE" value="StatoOccInfoStorDettPage"/>
<input type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>"/>
<input type="hidden" name="cdnUtMod" value="<%=Utils.notNull(cdnUtMod)%>"/>
<input type="hidden" name="keyLockStatoOcc" value="<%=Utils.notNull(keyLock)%>"/>
<input type="hidden" name="prgStatoOccupaz" value="<%=Utils.notNull(prgStatoOccupaz)%>"/>
<p/>
</af:form>
<%out.print(htmlStreamBottom);%>

</body>
</html>
