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
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>



<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
 
  
  List profs =  serviceResponse.getAttributeAsVector("PROFPROFILATURAUTENTE.rows.row");
  List profsDisp=   serviceResponse.getAttributeAsVector("ComboProfProfilDisponib.rows.row");

  // Gestione delle linguette
  int cdnFunzione = new Integer ( (String) serviceRequest.getAttribute("cdnfunzione")).intValue();
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  String cdnStr =   (String) serviceRequest.getAttribute("cdut"); 
  BigDecimal cdut = new  BigDecimal(cdnStr);
  Linguette l = new Linguette(user,  cdnFunzione , _page, cdut);
  
  // Utilizzato solo da Franco  
  l.setCodiceItem("cdut");
  
  
  // Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  
  boolean insertBtnON = attributi.containsButton("inserisci");
  boolean removeBtnON = attributi.containsButton("rimuovi");
  
    
  String readonlyStr = "true";
  if (insertBtnON || removeBtnON){
    readonlyStr = "false";
  }
  
    boolean readonly = new Boolean(readonlyStr).booleanValue();

  String htmlStreamTop = StyleUtils.roundTopTable(!readonly);   
    String htmlStreamBottom = StyleUtils.roundBottomTable(!readonly);
  

%>



<html lang="ita">
  <head>
    <title>Presentazione</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
    <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery-ui.min.css">
    <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">  
    <style>
  .ui-autocomplete {
    max-height: 200px;
  
  }
  /* IE 6 doesn't support max-height
   * we use height instead, but this forces the menu to always be this tall
   */
  * html .ui-autocomplete {
    height: 200px;
  }
  </style>
    <af:linkScript path="../../js/" />
  <script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
  <script src="../../js/jqueryui/jquery-ui.min.js"></script>    
    <script language="javascript">
    var contextPath = "<%=request.getContextPath()%>";
    var cdnUtente = "<%=cdnStr%>";
    $(function() {
    	
        $.widget( "custom.catcomplete", $.ui.autocomplete, {
            _create: function() {
              this._super();
              this.widget().menu( "option", "items", "> :not(.ui-autocomplete-category)" );
            },
            _renderMenu: function( ul, items ) {
              var that = this,
                currentCategory = "";
              $.each( items, function( index, item ) {
                var li;
                if ( item.category != currentCategory ) {
                  ul.append( "<li class='ui-autocomplete-category'>" + item.category + "</li>" );
                  currentCategory = item.category;
                }
                li = that._renderItemData( ul, item );
                if ( item.category ) {
                  li.attr( "aria-label", item.category + " : " + item.label );
                }
              });
            }
          });	
        $( "#profiloAutocomplete" ).catcomplete({
            max: 10,
            delay: 100,
            minLength: 1,
            autoFocus: true,
            cacheLength: 1,
            scroll: true,
            highlight: true,
            source: function(request, response) {
	            $.ajax({
	                url: contextPath + "/services/autocompleteServletComponent?prefixQueryName=PROF_SEL_PROFIL_DISPONIB&category=cat&cdnUser="+cdnUtente,
	                dataType: "json",
	                data: request,
	                success: function( data, textStatus, jqXHR) {
	                 console.log( data);
	                 var items = data.matchingItems;
	                 if(items.length <=0){
	                	 items = new Array();
	                	 var noResult ={id: "", value:"<nessun risultato>", category:""};
	                	 items[0]=noResult;
	                	 $( "[name='strProfilUt']" ).val(null);
	                	 
	                 }
	                	response(items);
	                 },
	                error: function(jqXHR, textStatus, errorThrown){
	                     console.log( textStatus);
	                }
	        
	            });
        	},
            select: function(event, ui) {
            	$( "[name='strProfilUt']" ).val(ui.item.id);
            }
          });
 
    });
      function separa(){
        //var selIndex = document.form.strProfilUt.options.selectedIndex;
        //var valore = document.form.strProfilUt.options[selIndex].value;
        var valore = document.form.strProfilUt.value;
        if(valore==""){
        	alert("Il campo Profilature associabili è obbligatorio");
        	document.form.strProfiloAutocomplete.value = null;
        	document.getElementById("profiloAutocomplete").focus();
      		return false;
        }
        var sArray = valore.split("-");  
        document.form.cdnProfilo.value= sArray[0];
        document.form.cdnGruppo.value= sArray[1];
        return true;   
        
      }



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
  
   <%@ include file="./testataUtente.inc" %>
  
  
    <% l.show(out); %>
    
     <font color="red"><af:showErrors /></font>
	     
    <af:form name="form" action="AdapterHTTP" onSubmit="separa()">
      <af:textBox name="PAGE" type="hidden" value="ProfProfilaturaUtentePage" />
      
      <af:textBox name="cdut" type="hidden" value="<%=cdnStr%>" />
      <af:textBox name="cdnGruppo" type="hidden" value="" />
      <af:textBox name="cdnProfilo" type="hidden" value="" />
      <af:textBox name="cdnfunzione" type="hidden" value="<%=String.valueOf(cdnFunzione)%>" />

	  <br/>	
     
      <p align="center">  
      <%out.print(htmlStreamTop);%>
      
             <table class="main">
             	<tr>
             	 	<td>
	             	</td>
             	
	             	<td class="campo">
	             		<span class="sezione">Profilature correntemente associate all'utente</span>
	             	</td>
             	
             	</tr>
             
             	</tr>
                	<tr>
             	 	<td>
	             	</td>
             	
	             	<td class="campo">
	            		&nbsp; 	
	             	</td>
             	</tr>
             
             
              <%
                for (int i=0; i< profs.size(); i++){
                  SourceBean riga = (SourceBean) profs.get(i);
                  String desc= (String) riga.getAttribute("Descrizione"); 
                  BigDecimal codice = (BigDecimal) riga.getAttribute("codice"); 
                  
                  String url ="AdapterHTTP" ;
                  url       += "?PAGE=ProfProfilaturaUtentePage";
                  url       += "&prgProfilatura=" + codice.intValue();
                  url       += "&cdut=" + cdnStr;
                  url       += "&cdnfunzione=" + cdnFunzione;
                  url       += "&CANCELLA=true";
                  
                  %>
                <tr>
                
                    <% if (removeBtnON) {%>
                        <td class="etichetta"><a onClick="cancella('<%=url%>')" href="#"><img src="../../img/del.gif" border=0></a></td>
                    <%}%>  
                
                
                  <td class="campo"><%=desc%></td>
                </tr>
                
                <%
                }
                if( profs.size()==0){%>
                        <tr>
                          <td class="etichetta">&nbsp;</td>
                          <td class="campo">Nessuna profilatura associata</td>
                        </tr>
                <%}%>

				<tr><td>&nbsp;</td></tr>            
               </table>
       </p>
       
       
    <% if (insertBtnON) {%>

      
      <p align="center">  
             <table class="main">

             	<tr>
             	 	<td>
	             	</td>
             	
	             	<td class="campo">
	             		<span class="sezione">Profilature associabili</span>
	             	</td>
             	
             	</tr>
                	<tr>
             	 	<td>
	             	</td>
             	
	             	<td class="campo">
	            		&nbsp; 	
	             	</td>
             	</tr>

          <tr>
          <% if (profsDisp.size() != 0){ 
                  %>
           <%--  <td class="etichetta">Profilatura</td> 
            <td class="campo" nowrap>      
                    <af:comboBox name="strProfilUt" moduleName="ComboProfProfilDisponib" 
                      title="Profilature associabili"   onChange="fieldChanged()"
                      addBlank="true" blankValue="" required="true"
                    />
             </td>        --%>
               	<td class="etichetta">Profilatura</td> 
              	<td class="campo" nowrap=""><input type="text" onChange="fieldChanged()" id="profiloAutocomplete" name="strProfiloAutocomplete" size="70%"/>*
              		<input name="strProfilUt" type="hidden">
              	 </td> 
            <%}else {
                %>
                <td class="etichetta">Profilatura</td>
                <td class="campo">
                Nessuna profilatura associabile</td>
            <%}%>
            
      </tr>
      <tr>
          <td colspan="2" >&nbsp;</td>
        </tr>   
      <% if (profsDisp.size() != 0){  %>
     
                <tr>
                        <td colspan="2" align="center">
                                <input type="submit" name="SALVA" value="Inserisci" class="pulsante">
                        </td>
                </tr>
            <%}%>
    
    </table>
    <%out.print(htmlStreamBottom);%>
    </p>
    
    
    <%}%>
            
    </af:form>
  </body>
</html>
