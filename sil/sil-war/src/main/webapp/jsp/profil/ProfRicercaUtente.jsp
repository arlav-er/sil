<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
   String htmlStreamTop = StyleUtils.roundTopTable(false);   
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);
   
   String cdnFunzione = (String) serviceRequest.getAttribute("cdnfunzione");
%>



<html>
	<head>
		<title>Ricerca sugli iscritti</title>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    <script language="javascript">
      function nuovo(){
      	// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
      
        var url = "AdapterHTTP?PAGE=ProfNuovoUtentePage&cdnfunzione=<%=cdnFunzione%>";
		setWindowLocation(url);
      }
    </script>

	</head>
	<body class="gestione" onload="rinfresca()">
  <br/>
	<p class="titolo">Ricerca utenti</p>
	
			<af:form method="POST" action="AdapterHTTP">
      <af:textBox type="hidden" name="PAGE" value="ProfListaUtentiPage"/>
      <af:textBox type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>

      <p align="center">
       <%out.print(htmlStreamTop);%>
      <table class="main">
        <tr>
          <td class="etichetta">
            Tipo di organizzazione
          </td>
          <td class="campo">
            <af:comboBox name="ComboTipoOrg" moduleName="ComboTipoOrganizzazioni" addBlank="true" blankValue=""/>
          </td>
        </tr>

        <tr>
          <td class="etichetta">
            Organizzazione
          </td>
       
          <td class="campo">
            <af:comboBox name="ComboOrg" moduleName="ComboOrganizzazioni" addBlank="true" blankValue=""/>
          </td>
        </tr>
        
        
        <tr>
          <td class="etichetta">
            Ruolo
          </td>
          <td class="campo">
            <af:comboBox name="ComboRuolo" moduleName="ComboRuoli" addBlank="true" blankValue=""/>
          </td>
        </tr>
        
        
        
        
        <tr>
          <td class="etichetta">
            Nome
          </td>
          <td class="campo">
            <af:textBox name="nome"  />
          </td>
        </tr>
         

        <tr>
          <td class="etichetta">
            Cognome
          </td>
          <td class="campo">
            <af:textBox name="cognome"  />
          </td>
        </tr>
         
      
        
      <tr>
          <td class="etichetta">
              Validità dell'account
          </td>
          <td nowrap class="campo">
            <select name="valAccount">
              <option value=""></option>
              <option value="V">Valido</option>
              <option value="NAV">Non ancora valido</option>
              <option value="S">Scaduto</option>
            </select>
            
          </td>
        </tr>        
        
        
        <tr>
          <td class="etichetta">
            Account abilitato
          </td>
          <td nowrap class="campo">

            <select name="FlagAbilitato">
              <option value="" checked></option>
              <option value="S">Sì</option>
              <option value="N">No</option>
            </select>

 
          </td>
        </tr>

		 <tr>
          <td class="etichetta">
            Account convenzionato
          </td>
          <td nowrap class="campo">

            <select name="FlagUtConvenzione">
              <option value="" checked></option>
              <option value="S">Sì</option>
              <option value="N">No</option>
            </select>

 
          </td>
        </tr>        

        <tr>
          <td class="etichetta">
            Login
          </td>
          <td class="campo">
            <af:textBox name="strLogin"/>
          </td>
        </tr>
         
      
        <%
             // GESTIONE ATTRIBUTI....
             String _page = (String) serviceRequest.getAttribute("PAGE");
             PageAttribs attributi = new PageAttribs(user, _page);
        
        %>
        
        
        
        
        
        
        
        
       <tr>
            <td colspan="2">
             &nbsp;
            </td>
        </tr>
      
          <tr>
            <td>
             &nbsp;
            </td>
          <td nowrap class="campo">
                <input class="pulsante" type="submit" VALUE="Cerca" />
                &nbsp;&nbsp;
                <input class="pulsante" type="reset"  VALUE="Annulla" />
          </td>
        </tr>
          
         <tr>
            <td>
             &nbsp;
            </td>
        </tr>




          <tr>
            <td>
             &nbsp;
            </td>
          <td nowrap class="campo">

            <%if (attributi.containsButton("nuovo")){%>
                <input type="button" class="pulsante" VALUE="Nuovo utente" onClick="nuovo()"/>
            <%}%>
          </td>
        </tr>


        
      </table>
       <%out.print(htmlStreamBottom);%>
    </p>
      </af:form>
		</center>
	</body>
</html>

