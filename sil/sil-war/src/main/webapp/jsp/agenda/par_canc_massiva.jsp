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
String giornoDB = "";
String meseDB = "";
String annoDB = "";
String nrosDB = "";
String giorno = "";
String mese = "";
String anno = "";
String cod_vista = "";
int mod = 0;
String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCPI");

if(serviceRequest.containsAttribute("giorno")) { giorno = serviceRequest.getAttribute("giorno").toString(); }
if(serviceRequest.containsAttribute("mese")) { mese = serviceRequest.getAttribute("mese").toString(); }
if(serviceRequest.containsAttribute("anno")) { anno = serviceRequest.getAttribute("anno").toString(); }
if(serviceRequest.containsAttribute("nrosDB")) { nrosDB = serviceRequest.getAttribute("nrosDB").toString(); }
if(serviceRequest.containsAttribute("giornoDB")) { giornoDB = serviceRequest.getAttribute("giornoDB").toString(); }
if(serviceRequest.containsAttribute("meseDB")) { meseDB = serviceRequest.getAttribute("meseDB").toString(); }
if(serviceRequest.containsAttribute("annoDB")) { annoDB = serviceRequest.getAttribute("annoDB").toString(); }
if(serviceRequest.containsAttribute("cod_vista")) { cod_vista = serviceRequest.getAttribute("cod_vista").toString(); }
if(serviceRequest.containsAttribute("MOD")) { mod = Integer.parseInt(serviceRequest.getAttribute("MOD").toString()); }

String labelServizio = "Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equalsIgnoreCase("1")){
	labelServizio = "Area";
}
%>

<%
boolean canModify = true;
%>


<html>
<head>
  <title>Parametri Cancellazione Massiva Slot</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script language="javascript">
    function sub(page)
    {
      document.frm.PAGE.value = page;
      if(page=="CancMassivaSlotPage") {
        var datiOk = controllaFunzTL() && riportaControlloUtente( controllaDate() );
        //alert(datiOk);
        if(datiOk) { doFormSubmit(document.frm); }
      } else { doFormSubmit(document.frm); }
    }


    function controllaDate()
    {
      var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
      var di = document.frm.dataParInizioStr.value;
      var df = document.frm.dataParFineStr.value;
      var ok1, ok2;
      var s, g, m, a;
      var dataI, dataF;
  
      var matchArray = di.match(datePat);
      if(matchArray == null) { 
        ok1 = false;
        dataI = "";
      } else { 
        ok1 = true;
        s = matchArray[2];
        var tmp1 = di.split(s);
        g = tmp1[0];
        m = tmp1[1];
        a = tmp1[2];
        dataI = parseInt(a + m + g, 10);
      }

      matchArray = df.match(datePat);
      if(matchArray == null) { 
        ok2 = false;
        dataF = "";
      } else { 
        ok2 = true;
        s = matchArray[2];
        var tmp2 = df.split(s);
        g = tmp2[0];
        m = tmp2[1];
        a = tmp2[2];
        dataF = parseInt(a + m + g,10);
      }
  
      if(ok1 && ok2) {
        if(dataI <= dataF) { return(true); }
        else { 
          alert("La data di inizio (data dal) deve essere maggiore o uguale alla data di termine (data al)."); 
          return(false);
        }
      } else {
        return(true);
      }
    }
  </script>
</head>
<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<body class="gestione">
<br>
<p class="titolo">Parametri Cancellazione Massiva Slot</p>
<%out.print(htmlStreamTop);%>  
<af:form action="AdapterHTTP" name="frm" method="POST">

<input name="PAGE" type="hidden" value="CancMassivaSlotPage"/>
<input name="CODCPI" type="hidden" value="<%=codCpi%>"/>

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
              <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDal")%>"/>
              <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
            <%}%>
        <% } %>
  <% } %>
  <input name="giorno" type="hidden" value="<%=giorno%>"/>
  <input name="mese" type="hidden" value="<%=mese%>"/>
  <input name="anno" type="hidden" value="<%=anno%>"/>
  <input name="MOD" type="hidden" value="<%=mod%>"/>
  <input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>



<table class="main">
<tr><td colspan="2"><div class="sezione2">Periodo</div></td></tr>
<tr>
  <td class="etichetta">Data Dal</td>
  <td class="campo">
  <af:textBox name="dataParInizioStr" title="Data Dal"
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
  <af:textBox name="dataParFineStr" title="Data Al"
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
<tr><td colspan="2"><div class="sezione2">Parametri</div></td></tr>
<tr>
	<%String titleServizio = "Scelta "+labelServizio; %>
  <td class="etichetta"><%=labelServizio %></td>
  <td class="campo">
    <af:comboBox name="codParServizio"
                 size="1"
                 title="<%=titleServizio %>"
                 multiple="false"
                 required="false"
                 focusOn="false"
                 moduleName="COMBO_SERVIZIO"
                 addBlank="true"
                 blankValue=""
    />
  </td>
</tr>
<tr>
  <td class="etichetta">Operatore</td>
  <td class="campo">
    <af:comboBox name="prgSpiOperatore"
                 size="1"
                 title="Scelta Operatore"
                 multiple="false"
                 required="false"
                 focusOn="false"
                 moduleName="COMBO_SPI"
                 addBlank="true"
                 blankValue=""
    />
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  <input type="button" class="pulsanti" value="Cancella Slot" onClick="sub('CancMassivaSlotPage');">
  &nbsp;&nbsp;
  <input type="button" class="pulsanti" value="Chiudi" onClick="sub('GestSlotPage');">
  </td>
</tr>
</table>
</af:form>
<%out.print(htmlStreamBottom);%>

</body>
</html>
