<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
  //String PROV =  StringUtils.getAttributeStrNotNull(serviceRequest, "PROV");
  String prg = serviceRequest.getAttribute("PRGSLOT").toString();
  String codcpi = serviceRequest.getAttribute("CODCPI").toString();

%>

<%
    String cod_vista = StringUtils.getAttributeStrNotNull(serviceRequest,"cod_vista");
    String mod = StringUtils.getAttributeStrNotNull(serviceRequest,"MOD");
    if(mod.equals("")) { mod = "0"; }
    
    String giorno = StringUtils.getAttributeStrNotNull(serviceRequest,"giorno");
    String mese = StringUtils.getAttributeStrNotNull(serviceRequest,"mese");
    String anno = StringUtils.getAttributeStrNotNull(serviceRequest,"anno");
    String giornoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"giornoDB");
    String meseDB = StringUtils.getAttributeStrNotNull(serviceRequest,"meseDB");
    String annoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"annoDB");
    
    
    String sel_operatore = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore");
    String sel_servizio = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio");
    String sel_aula = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula");
    String dataDal = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDal");
    String dataAl = StringUtils.getAttributeStrNotNull(serviceRequest, "dataAl");
    
    String data_cod = StringUtils.getAttributeStrNotNull(serviceRequest,"DATA_COD");
%>
<%
/*
  NOTA: le pagine di ricerca devono avere lo stile prof_ro -> quindi invece di
  canModify si deve passare il valore false
*/
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
	<head>
		<title>Ricerca sugli Operatori</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<af:linkScript path="../../js/" />
</head>
<body class="gestione">
		<p class="titolo">Ricerca sugli Operatori</p>
		<%out.print(htmlStreamTop);%>
			<af:form method="POST" action="AdapterHTTP" dontValidate="true">

      <input name="MOD" type="hidden" value="<%=mod%>"/>
      <input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
      <input name="DATA_COD" type="hidden" value="<%=data_cod%>"/>
      <%if(mod.equals("0")) {%>
          <input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
          <input name="meseDB" type="hidden" value="<%=meseDB%>"/>
          <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
          <input name="giorno" type="hidden" value="<%=giorno%>"/>
          <input name="mese" type="hidden" value="<%=mese%>"/>
          <input name="anno" type="hidden" value="<%=anno%>"/>
      <%} else {%>
        <%if(mod.equals("2")) {%>
              <input name="sel_operatore" type="hidden" value="<%=sel_operatore%>"/>
              <input name="sel_servizio" type="hidden" value="<%=sel_servizio%>"/>
              <input name="sel_aula" type="hidden" value="<%=sel_aula%>"/>
              <input name="mese" type="hidden" value="<%=mese%>"/>
              <input name="anno" type="hidden" value="<%=anno%>"/>
              <input name="dataDal" type="hidden" value="<%=dataDal%>"/>
              <input name="dataAl" type="hidden" value="<%=dataAl%>"/>
        <%}%>
      <%}%>

      
      <table class="main"> 
      <tr>
        <td class="etichetta">Cognome</td>
        <td class="campo"><input type="text" name="strCognome_ric" value="" size="20" maxlength="50"/></td>
      </tr>
      <tr>
        <td class="etichetta">Nome</td>
        <td class="campo"><input type="text" name="strNome_ric" value="" size="20" maxlength="50"/></td>
      </tr>
      <input type="hidden" name="PAGE" value="AgListaOperatoriPage"/>
          <input type="hidden" name="PRGSLOT" value="<%=prg%>"/>
          <input type="hidden" name="CODCPI" value="<%=codcpi%>"/>
          <tr><td colspan="2">&nbsp;</td></tr>
          <tr>
            <td colspan="2" align="center">
            <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
            &nbsp;&nbsp;
            <input class="pulsanti" type="reset" name="reset" value="Annulla"/>
            </td>
          </tr>
          
   
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          <td colspan="2" align="center">
          <input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
          </td>
        </tr>
        </table>
        </af:form>
		<%out.print(htmlStreamBottom);%>
	</body>
</html>

