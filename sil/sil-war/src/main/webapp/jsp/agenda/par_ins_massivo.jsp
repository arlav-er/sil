<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,it.eng.afExt.utils.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

//leggo il sourcebean della combo slot
  Vector slotRows=null;
  slotRows=serviceResponse.getAttributeAsVector("COMBO_SETTIMANA_TIPO.ROWS.ROW");


String giornoDB = "";
String meseDB = "";
String annoDB = "";
String nrosDB = "";
String giorno = "";
String mese = "";
String anno = "";
String cod_vista = "";
int mod = 0;

if(serviceRequest.containsAttribute("giorno")) { giorno = serviceRequest.getAttribute("giorno").toString(); }
if(serviceRequest.containsAttribute("mese")) { mese = serviceRequest.getAttribute("mese").toString(); }
if(serviceRequest.containsAttribute("anno")) { anno = serviceRequest.getAttribute("anno").toString(); }
if(serviceRequest.containsAttribute("nrosDB")) { nrosDB = serviceRequest.getAttribute("nrosDB").toString(); }
if(serviceRequest.containsAttribute("giornoDB")) { giornoDB = serviceRequest.getAttribute("giornoDB").toString(); }
if(serviceRequest.containsAttribute("meseDB")) { meseDB = serviceRequest.getAttribute("meseDB").toString(); }
if(serviceRequest.containsAttribute("annoDB")) { annoDB = serviceRequest.getAttribute("annoDB").toString(); }
if(serviceRequest.containsAttribute("cod_vista")) { cod_vista = serviceRequest.getAttribute("cod_vista").toString(); }
if(serviceRequest.containsAttribute("MOD")) { mod = Integer.parseInt(serviceRequest.getAttribute("MOD").toString()); }
%>

<%
String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCPI");
//User user = (User) sessionContainer.getAttribute(User.USERID);
int cdnUt = user.getCodut();
String cdnParUtente = Integer.toString(cdnUt);
%>

<%
boolean canModify = true;
%>
<html>
<head>
  <title>Inserimento Slot da Settimana Tipo</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript  path="../../js/"/>
  <script type="text/javascript"  src="../../js/script_comuni.js">
  </script>
  <script type="text/javascript">
	function controllaDate() {
		var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
		var di = document.formCerca.dataParInizio.value;
		var df = document.formCerca.dataParFine.value;
		var ok1, ok2;
		var s, gI, mI, aI, gF, mF, aF;
		var dataI, dataF;

		var matchArray = di.match(datePat);
		if(matchArray == null) { 
        	ok1 = false;
        	dataI = "";
      	}
      	else { 
        	ok1 = true;
        	s = matchArray[2];
        	var tmp1 = di.split(s);
        	gI = tmp1[0];
        	mI = tmp1[1];
        	aI = tmp1[2];
        	dataI = parseInt(aI + mI + gI, 10);
      	}
		matchArray = df.match(datePat);
		if(matchArray == null) { 
			ok2 = false;
			dataF = "";
		}
		else { 
	        ok2 = true;
	        s = matchArray[2];
	        var tmp2 = df.split(s);
	        gF = tmp2[0];
	        mF = tmp2[1];
	        aF = tmp2[2];
	        dataF = parseInt(aF + mF + gF, 10);
      	}
		if(ok1 && ok2) {
			diff = confrontaDate(di,df);
			if(diff < 0) { 
				alert("La data di inizio (data dal) deve essere minore o uguale alla data di termine (data al)."); 
          		return false;
        	}
        	//14/12/2010 rodi - il periodo può essere al massimo di 6 mesi o si rischia di inserire milioni di miliardi di record... 
        	else if (diff > 180) {
        		alert("E' consentito inserire slot per un periodo massimo di 6 mesi.");
          		return false;
        	}
        	else { 
				return true; 
			}
		} 
		else {
        	return true;
      	}
	}
    
    //array per la gestione delle date di validità
    slot_da=new Array();
    slot_a=new Array();
    slot_da[0]="";
    slot_a[0]="";
  <%    SourceBean rowSlot=null; 
  		for(int i=1; i<slotRows.size()+1; i++)  { 
              rowSlot = (SourceBean) slotRows.elementAt(i-1);
              out.print("slot_da["+i+"]=\""+ rowSlot.getAttribute("datInizioVal").toString()+"\";\n");
              out.print("slot_a["+i+"]=\""+ rowSlot.getAttribute("datFineVal").toString()+"\";\n");
        } 
  %>
  
  function dateValidita(comboIndex) {
  	document.formCerca.st_valid_dal.value=slot_da[comboIndex];
  	document.formCerca.st_valid_al.value=slot_a[comboIndex];
  
  }
    
    
  </script>
</head>
<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<body class="gestione">
<br>
<p class="titolo">Inserimento Slot da Settimana Tipo</p>

<%out.print(htmlStreamTop);%>
<af:form action="AdapterHTTP" name="formCerca" method="POST" onSubmit="controllaDate()">

<input name="PAGE" type="hidden" value="InsMassivoSlotPage"/>
<input name="giorno" type="hidden" value="<%=giorno%>"/>
<input name="mese" type="hidden" value="<%=mese%>"/>
<input name="anno" type="hidden" value="<%=anno%>"/>
<input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
<input name="MOD" type="hidden" value="<%=mod%>"/>

<input name="codParCpi" type="hidden" value="<%=codCpi%>"/>
<input name="cdnParUtente" type="hidden" value="<%=cdnParUtente%>"/>
<table class="main">
<tr><td colspan="2"><div class="sezione2">Parametri</div></td></tr>
<tr>
  <td class="etichetta">Settimana Tipo</td>
  <td class="campo">
    <af:comboBox name="codParSettimana"
                 size="1"
                 title="Settimana Tipo"
                 multiple="false"
                 required="true"
                 focusOn="false"
                 moduleName="COMBO_SETTIMANA_TIPO"
                 addBlank="true"
                 blankValue=""
				 inline="onChange=\"javascript:dateValidita(document.formCerca.codParSettimana.selectedIndex);\""
    />
  </td>
</tr>
<tr>
  <td class="etichetta">Valida dal</td>
  <td class="campo">
    <af:textBox   name="st_valid_dal" title="Valida dal"
	              type="date"
	              size="10"
	              maxlength="10"
	              required="false"
	              validateOnPost="false"
	              disabled="false"
	              readonly="true"
	              value="" />&nbsp;<font class="etichetta">al</font>
    <af:textBox   name="st_valid_al" title="Valida al"
	              type="date"
	              size="10"
	              maxlength="10"
	              required="false"
	              validateOnPost="false"
	              disabled="false"
	              readonly="true"
	              value="" />   
   
  </td>
</tr>

<tr>
  <td class="etichetta">Stato Slot</td>
  <td class="campo">
    <af:comboBox name="codParStatoSlot"
                 size="1"
                 title="Stato iniziale degli Slot"
                 multiple="false"
                 required="true"
                 focusOn="false"
                 moduleName="COMBO_STATO_SLOT_SETTIMANA_TIPO"
                 addBlank="true"
                 blankValue=""
    />
  </td>
</tr>
<tr><td colspan="2"><div class="sezione2">Periodo</div></td></tr>
<!--table class="main"-->
<tr>
  <td class="etichetta">Data Dal</td>
  <td class="campo">
  <af:textBox name="dataParInizio" title="Data Dal"
              type="date"
              size="10"
              maxlength="10"
              required="true"
              validateOnPost="true"
              disabled="false"
              value=""
  />
  </td>
</tr>
<tr>
  <td class="etichetta">Data Al</td>
  <td class="campo">
  <af:textBox name="dataParFine" title="Data Al"
              type="date"
              size="10"
              maxlength="10"
              required="true"
              validateOnPost="true"
              disabled="false"
              value=""
  />
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  <input type="submit" class="pulsanti" value="Inserisci Slot">
  </td>
</tr>
</table>
</af:form>



<af:form name="formBack" action="AdapterHTTP" method="POST" dontValidate="true">
  <input name="PAGE" type="hidden" value="GestSlotPage"/>
  <% if(mod==0) {%>
        <input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
        <input name="meseDB" type="hidden" value="<%=meseDB%>"/>
        <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
  <% } else { %>
        <% if(mod==1) { %>
            <input name="nrosDB" type="hidden" value="<%=nrosDB%>"/>
            <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
        <% } else {%>
            <%if(mod==2) { // Ricerca precedente%>
              <input name="sel_operatore" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore")%>"/>
              <input name="sel_servizio" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio")%>"/>
              <input name="sel_aula" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula")%>"/>
              <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDa")%>"/>
              <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
            <%}%>
        <% } %>
  <% } %>
  <input name="giorno" type="hidden" value="<%=giorno%>"/>
  <input name="mese" type="hidden" value="<%=mese%>"/>
  <input name="anno" type="hidden" value="<%=anno%>"/>
  <input name="MOD" type="hidden" value="<%=mod%>"/>
  <input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
  <p align="center"><input type="submit" class="pulsanti" value="Chiudi"/></p>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
