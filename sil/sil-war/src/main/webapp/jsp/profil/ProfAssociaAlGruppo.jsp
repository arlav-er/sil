<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>


<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,                  
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
  List profs =  serviceResponse.getAttributeAsVector("M_ProfGiaAssociatiGruppo.rows.row");
  //List profsDisp=   serviceResponse.getAttributeAsVector("ComboProfProfilDisponib.rows.row");

 String cdnGruppo = (String) serviceRequest.getAttribute("cdnGruppo");
 
  // Gestione delle linguette
  //int cdnFunzione = new Integer ( (String) serviceRequest.getAttribute("cdnfunzione")).intValue();
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  //String cdnStr =   (String) serviceRequest.getAttribute("cdut"); 
  //BigDecimal cdut = new  BigDecimal(cdnStr);
  
    int cdnFunzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  	Linguette l = new Linguette(user, cdnFunzione , _page, new BigDecimal(cdnGruppo));
   
    l.setCodiceItem("CDNGRUPPO");
  
  
  // Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  
  boolean insertBtnON = attributi.containsButton("inserisci");
  boolean removeBtnON = attributi.containsButton("rimuovi");
  
    
  String readonlyStr = "true";
  if (insertBtnON || removeBtnON){
    readonlyStr = "false";
  }

   String htmlStreamTop    = StyleUtils.roundTopTable(insertBtnON);   
   String htmlStreamBottom = StyleUtils.roundBottomTable(insertBtnON);
  
%>



<html lang="ita">
  <head>
    <title>Associa profilo al gruppo</title>
    <!-- FILE profil/ProfAssociaAlGruppo.jsp -->
    <link rel="stylesheet" href="../../css/stili.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
    <af:linkScript path="../../js/" />
    
    <script language="javascript">
      function cancella(url){
      	// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
      
        if (confirm("Vuoi veramente procedere alla cancellazione ?")){
            setWindowLocation(url);
        }

      }
        

      // Rilevazione Modifiche da parte dell'utente
      var flagChanged = false;
      
      function fieldChanged() {
        <% if (readonlyStr.equalsIgnoreCase("false")){ %> 
        flagChanged = true;
        <%}%> 
      }

      </script>


 
  </head>
  <body class="gestione" onLoad="rinfresca()">

  <%@ include file="testataGruppo.inc" %>
    
    <% l.show(out); %>
    
    <af:showErrors />
	     
    <af:form name="form" action="AdapterHTTP" method="POST">
      <af:textBox name="PAGE" type="hidden" value="ProfAssociaAlGruppoPage" />
      
      <af:textBox name="cdnGruppo" type="hidden" value="<%=cdnGruppo%>" />
      <af:textBox name="cdnfunzione" type="hidden" value="<%=String.valueOf(cdnFunzione)%>" />

      <p align="center">  

      <%out.print(htmlStreamTop);%>
             <table class="main">
             <tr><td>&nbsp;</td></tr>
             <tr><td>&nbsp;</td><td colspan="2" class="campo"><span class="sezione">Profili correntemente associati al gruppo</span></td></tr>
             <tr><td></td><td align="left"><strong>Profilo</strong></td><td align="left"><strong>Men&ugrave;</strong></td>
             </tr>
              <%
                for (int i=0; i< profs.size(); i++){
                  SourceBean riga = (SourceBean) profs.get(i);
                  String profAssociato = (String) riga.getAttribute("PROFILO"); 
                  String menuAssociato = (String) riga.getAttribute("MENU"); 
                  BigDecimal cdnProfAssociato = (BigDecimal) riga.getAttribute("CDNPROFILO"); 
                  BigDecimal cdnMenuAssociato = (BigDecimal) riga.getAttribute("CDNMENU"); 
                  
                  String url ="AdapterHTTP" ;
                  url       += "?PAGE=ProfAssociaAlGruppoPage";
                  url       += "&cdnGruppo=" + cdnGruppo.toString();
                  url       += "&cdnProfilo=" + cdnProfAssociato.toString();
                  url       += "&cdnMenu=" + cdnMenuAssociato.toString();
                  url       += "&cdnfunzione=" + cdnFunzione;
                  url       += "&CANCELLA=true";
                  %>
                <tr>
                  <td class="etichetta">
                    <%-- if (removeBtnON) { --%>
                        <a onClick="cancella('<%=url%>')" href="#"><img src="../../img/del.gif" border=0></a>
                    <%-- } --%>  
                  </td>
                
                  <td class="campo2"><%=profAssociato%></td>
                  <td class="campo2"><%=menuAssociato%></td>
                </tr>
                
                <%
                }
                if( profs.size()==0){%>
                        <tr>
                          <td class="etichetta">&nbsp;</td>
                          <td class="campo2" colspan="2">Nessun profilo associato</td>
                        </tr>
                <%}%>
            
               </table>
       
       
       
    <% if (insertBtnON) 
       {%>

      
      <p align="center">  
         <table class="main">

         <tr> 
            <td class="etichetta"></td> 
            <td class="campo" nowrap><span class="sezione">Profili associabili</span></td>
          </tr>

         <tr> 
            <td class="etichetta">Profili</td> 
            <td class="campo" nowrap>      
                    <af:comboBox name="cdnProfilo" moduleName="M_ComboProfiliXGruppo" 
                      title="Profili"   onChange="fieldChanged()"
                      addBlank="true" blankValue="" required="true"
                    />
            </td>
          </tr>
          <tr>
            <td class="etichetta">Men&ugrave;</td> 
            <td class="campo" nowrap>      
                    <af:comboBox name="cdnMenu" moduleName="M_ComboMenuXGruppo" 
                      title="Menu"   onChange="fieldChanged()"
                      selectedValue="4" required="true"
                    />
             </td>       
      </tr>
 
      <tr>
          <td colspan="2" >&nbsp;</td>
        </tr>   
      <%-- if (profsDisp.size() != 0){  --%>
     
                <tr>
                        <td colspan="2" align="center">
                                <input class="pulsante" type="submit" name="SALVA" value="Inserisci">
                        </td>
                </tr>
            <%-- } --%>
    
             <tr><td>&nbsp;</td></tr>
    </table>
      <%out.print(htmlStreamBottom);%>
    
    
    <%} else {
        out.print(htmlStreamBottom);
      }%>
            
    </af:form>
  </body>
</html>
