<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  java.util.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
	InfoRegioneSingleton regione = InfoRegioneSingleton.getInstance();
    SourceBean profiliDispBean = (SourceBean) serviceResponse.getAttribute("main");
    int prgProfiloUtente = user.getPrgProfilo();
  
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<html lang="ita">
<head>
	<title>Presentazione</title>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <script language="JavaScript">
  <!--
  
     function caricaProfilo(i){
      window.top.menu.location="AdapterHTTP?PAGE=menuCompletoPage&PRG_PROF_UT=" + i ;
     }

    function scegliProfilo(){
      var idx =  document.form.profilature.options.selectedIndex;
      var prg_prof_ut = document.form.profilature.options[idx].value;
      if (prg_prof_ut==""){
        alert("E' necessario selezionare una profilatura")
        document.form.profilature.focus();
        return;
      }
        
      caricaProfilo(prg_prof_ut);
      doFormSubmit(document.form);
     }

  -->
  </script>

  <af:linkScript path="../../js/"/>
  
</head>
<body class="gestione" onLoad="rinfresca()">

<%
if (prgProfiloUtente != 0)
// UN SOLO PROFILO
{

  %>
      <table width="100%" height="100%">
      <tr valign="middle">
        <td align="center" class="presentazione" width="100%" height="100%">
        <img src="../../img/logosil1.gif" alt="SIL" border="0">
        <br>
        Sistema Informativo Lavoro<br>Regione <%=regione.getNome()%>
        </td>
      </tr>
      </table>
      <script language="javascript"><!-- 
      caricaProfilo(<%=prgProfiloUtente%>) 
      --></script>
<%
}

          
else if (profiliDispBean.getContainedAttributes().size()==0 )
// SENZA PROFILO
{
 %>


<table width="80%" class="main" align="center">
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
	<td width="40%">Utente:</td>
	<td width="60%"><b><%=Utils.notNull(user.getNome()) + " " + Utils.notNull(user.getCognome())%></b></td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
	<td colspan="2" align="justify">
	<b>Mi dispiace, ma Lei non ha nessun profilo associato</b>
	</td>
</tr>
</table>

<%
}  
else //if (profiliDispBean !=  null)
// + PROFILI
{
  // Metto in sessione un boolean per controllare il link
  // di cambio profilo
  
  sessionContainer.setAttribute("MANY_PROFS", "true" );

 %>

	<p class="titolo">Scelta della profilatura con la quale lavorare</p>
	
<af:form name="form" action = "AdapterHTTP" >
<af:textBox name="PAGE" type="hidden" value="mainBlankPage" />
     
     <p align="center">
      <table class="main">
        <tr>
          <td class="etichetta">
            Utente
          </td>
          <td class="campo">
              <%=Utils.notNull(user.getNome()) + " " + Utils.notNull(user.getCognome())%>
          </td>
        </tr>

        <tr>
              <td class="etichetta">
                Profilature disponibili
              </td>
          <td class="campo" nowrap>
            <af:comboBox name="profilature" 
                  moduleName="main" addBlank="true" blankValue="" 
                  />
          </td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          <td colspan="2" align="center">
          <input type="button" class="pulsanti" onClick="scegliProfilo();"
          value="&nbsp;Conferma profilatura&nbsp;">
          </td>
        </tr>
    </table>

</af:form>
<%
}  
%>

</body>
</html>
