<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.bean.menu.*,
			      javax.xml.transform.*,
				  javax.xml.transform.stream.*
	" %>
<%!  
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.DettaglioMenu.jsp");
%>	
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%

	String hashCode = (String) serviceRequest.getAttribute("hashCode");

	    // Attributi della pagina (pulsanti e link) 
/*     PageAttribs attributi = new PageAttribs(user, _page);
     boolean canSave   = attributi.containsButton("salva");
     boolean canInsert = true;//attributi.containsButton("inserisci");
*/

     boolean canSave   = true;
 
    
    String htmlStreamTop = StyleUtils.roundTopTable(canSave);   
    String htmlStreamBottom = StyleUtils.roundBottomTable(canSave);
    String cdnfunzione =  (String) serviceRequest.getAttribute("cdnfunzione");
    String nomeFunzione = null;
	
%>

<html>
	<head>
	  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery-ui.min.css">
      <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">  
	  
    <af:linkScript path="../../js/"/>
	

	<style>
span.sel {
	cursor: pointer;
	border: 1px solid #000080;
	background-color: #f0ffff;
}

span.unsel {
	cursor: pointer;
}

td.intestazione {
	background-color: #000080;
	color: white;
	text-align: center;
	align: center;
}

td.tasti {
	text-align: center;
	font-size: smaller;
}

body {
	overflow-y: hidden;
	margin: 0;
	height: 100%;
	border: 0;
	padding: 0px;
}

#menu {
	right: 3%;
	top: 85px;
	position: fixed;
	border: 2px solid #000080;
}

* html #menu {
	position: absolute;
}

.clear {
	clear: both;
}

#pageX {
	position: absolute;
	top: 0px;
	left: 0px;
	margin: 0px;
	padding: 0px;
	height: 100%;
	width: 100%;
	overflow-y: auto;
}

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
	

	
<script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
  <script src="../../js/jqueryui/jquery-ui.min.js"></script>    
    <script language="javascript">
    var contextPath = "<%=request.getContextPath()%>";
	var precVoce=null;
	
    $(function() {
    	$( "#funzioneAutocomplete" ).autocomplete({
    		//width: 300,
    	    max: 10,
    	    delay: 100,
    	    minLength: 0,
    	    autoFocus: true,
    	    
    	    scroll: true,
    	    highlight: true,
    	    
    		source: function(request, response) {
    			
    	        $.ajax({
    	            url: contextPath + "/services/autocompleteServletComponent?prefixQueryName=COMBO_FUNZIONI_MENU",
    	            dataType: "json",
    	            data: request,
    	            success: function( data, textStatus, jqXHR) {
    	              //  console.log( data);
    	             var items = data.matchingItems;
    	             if(items.length <=0){
    	            	 items = new Array();
    	            	 var noResult ={id: "fake", value:"<nessun risultato>"};
	                	 items[0]=noResult;
    	            	 $( "[name='funzione']" ).val(null);
    	            	 
    	             }
    	                response(items);
    	                
    	             },
    	            error: function(jqXHR, textStatus, errorThrown){
    	                 console.log( textStatus);
    	            }
    	    
    	        });
    	    },
    	    select: function(event, ui) {
    	    	$( "[name='strFunzAutocomplete']" ).val(ui.item.value);
    	    	$( "[name='funzione']" ).val(ui.item.id);
    	    }
    	   
    	  });
    	});
	
		function seleziona(id){
			var vocemenu=document.getElementById(id);
			if (vocemenu!=null){
				if (precVoce!=null){
					precVoce.className='unsel'
				}
				vocemenu.className='sel';
				document.forms[0].nome.value=vocemenu.innerHTML;
				precVoce=vocemenu;
			}
		}

		/////////////////////////
		
		function salva(){
				document.forms[0].PAGE.value="SalvaVociMenuPage"
				document.forms[0].submit();
		}

		
		
		function su(){
			if (precVoce !=null){
				document.forms[0].azione.value= "S"			
				document.forms[0].hashCode.value= precVoce.id			
				document.forms[0].submit();
			}
		}


		function giu(){
			if (precVoce !=null){
				document.forms[0].azione.value= "G"			
				document.forms[0].hashCode.value= precVoce.id			
				document.forms[0].submit();
			}
		
		}


		function suLivello(){
			if (precVoce !=null){
				document.forms[0].azione.value= "U"			
				document.forms[0].hashCode.value= precVoce.id			
				document.forms[0].submit();
			}
		}


		function giuLivello(){
			if (precVoce !=null){
				document.forms[0].azione.value= "D"			
				document.forms[0].hashCode.value= precVoce.id			
				document.forms[0].submit();
			}
		}


		function cancLivello(){
			if (precVoce !=null){
				document.forms[0].azione.value= "X"			
				document.forms[0].hashCode.value= precVoce.id			
				document.forms[0].submit();
			}
		}


		function addFolder(){
			if (precVoce !=null){
				document.forms[0].azione.value= "A"			
				document.forms[0].hashCode.value= precVoce.id			
				document.forms[0].submit();
			}
		}

		function addFunzione(){
			if (precVoce !=null){
				document.forms[0].azione.value= "Z"			
				document.forms[0].hashCode.value= precVoce.id	
			//	var i=document.forms[0].funzione.options.selectedIndex;		
			//	document.forms[0].descFunzione.value= document.forms[0].funzione.options[i].text;
			    if(document.forms[0].funzione.value=="fake"){
			    	alert("Selezionare una voce di funzione valida");
			    	document.forms[0].strFunzAutocomplete.value = null;
			    	document.forms[0].funzione.value = null;
		        	document.getElementById("funzioneAutocomplete").focus();
			    	return false;
			    }
				document.forms[0].descFunzione.value= document.forms[0].strFunzAutocomplete.value;
				document.forms[0].submit();
			}
		}

	   function rinomina(){
			if (precVoce !=null){
				document.forms[0].azione.value= "R"			
				document.forms[0].hashCode.value= precVoce.id	
				document.forms[0].submit();
			}
		}

	   function rinomimaDesc(){
	   			document.forms[0].azione.value= "M"		
	   			document.forms[0].hashCode.value="0"; <%/* non serve in questo caso*/%>	
			    document.forms[0].submit();
	   }
	   
	   function rinfrescaDettMenu(){
		   rinfresca();
		   document.forms[0].strFunzAutocomplete.value = null;
		   document.forms[0].funzione.value = null;
	   }
	   
	   function clona(){
			document.forms[0].PAGE.value="ClonaMenuPage"
			document.forms[0].submit();
		}
	
	</script>
	
</head>	
<body onLoad="rinfrescaDettMenu();<% if (hashCode!=null) out.print("seleziona(" + hashCode + ");"); %>">

<%
	Menu menu = (Menu) sessionContainer.getAttribute("menu");
	StringBuffer xml = menu.toXML();
	StringWriter result = new StringWriter();
	
	//Creo il nome del file di configurazione per il transformer
	String filename = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsl" + 
	File.separator + "menu" + File.separator + "menu.xsl";
	File f = new File(filename);
	try{
		//Creo il transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(new StreamSource(f));
	
		//Trasformo il risultato  
		transformer.transform(new StreamSource(new StringReader(xml.toString())), new StreamResult(result));
	}catch(Exception ex){
		_logger.fatal( 
			"trasformazione XML-HTML per i menu", ex);
	}

%>
<div id="pageX">
 <%out.print(htmlStreamTop);%>
      <table class="main">
                      <tr>
			            <td> 
				            Descrizione: <b><%=menu.getDescrizione()%></b>
			            </td>
			          </tr>
	
	   </table>
	  <%out.print(htmlStreamBottom);%>


      <table width="94%" align="center">
	      <tr>
	      	<td><%	out.print(result.toString()); %></td>
		   </tr>
		    <tr>
	      	<td>&nbsp;</td>
		   </tr>
		     <tr>
	      	<td>&nbsp;</td>
		   </tr>
	  </table>
</div>

<div id="menu" >
<af:form action="AdapterHTTP" method="POST" dontValidate="true">

   <af:textBox type="hidden" name="PAGE" value="DettaglioMenuPage" />
   <af:textBox type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>" />
   <af:textBox type="hidden" name="azione" value="" />
   <af:textBox type="hidden" name="hashCode" value="" />   

   <af:textBox type="hidden" name="descFunzione" value="" />   

	<table border="0" >
	
	<tr>
		<td colspan="2" class="intestazione">Descrizione</td>
	</tr>
	
	
	<tr>
		<td colspan="2 "class="tasti" nowrap>
			<af:textBox name="descrizioneMenu" type="text" size="30" maxlength="100" 
												value="<%=menu.getDescrizione()%>"/>
			
			<input type="button" class="pulsante" value="OK" onClick="rinomimaDesc()" />					
		</td>
	</tr>
	<tr>
		<td colspan="2 "class="tasti" nowrap>Rinomina la descrizione del menu</td>
	</tr>

	
	<tr>
		<td class="intestazione">Ordinamento</td>
    	<td class="intestazione">Livelli</td>
	</tr>
	

	
	<tr>
		<td class="tasti" width="50%">
			<a href="#" onClick="su()"><img src="../../img/su.gif" border="0" title="Sposta in alto la voce selezionata"/></a>
     			&nbsp;&nbsp;
     		<a href="#" onClick="giu()"><img src="../../img/giu.gif" border="0" title="Sposta in basso la voce selezionata"/></a>
		</td>
	
		<td class="tasti">
		<a href="#" onClick="suLivello()"><img src="../../img/indietro.gif" border="0" title="Porta su di un livello la voce selezionata"/></a>
				&nbsp;&nbsp;
     	<a href="#" onClick="giuLivello()"><img src="../../img/avanti.gif" border="0" title="Porta giù di un livello la voce selezionata"/></a></td>
     
	
	</tr>

	
	<tr>
	</tr>
	
	
	<tr>
		<td class="intestazione" colspan="2">Cancellazione</td>
	</tr>
	<tr>
		<td class="tasti" ><a href="#" onClick="cancLivello()"><img src="../../img/del.gif" border="0" title="Cancella un livello"/></a></td>
	<td class="tasti" width="50%">Cancella la voce selezionata</td>
	</tr>
	
	
	<tr>
		<td colspan="2" class="intestazione">Aggiungi cartella</td>
	</tr>
	<tr>
		<td colspan="2 "class="tasti" nowrap><af:textBox name="cartella" type="text" size="30" maxlength="30" />
		<input type="button" class="pulsante" value="OK" onClick="addFolder()" title="Aggiunge una cartella sotto la voce selezionata"/>
		</td>
	</tr>
	
	<tr>
		<td colspan="2" class="intestazione">Aggiungi funzione</td>
	</tr>
	<tr>
		<td colspan="2 "class="tasti" nowrap><%-- <af:comboBox name="funzione" 
                  moduleName="ComboFunzioniMenu" addBlank="true" blankValue="" 
                  /> --%>
                  <input type="text"  id="funzioneAutocomplete" name="strFunzAutocomplete" size="30%"/><input name="funzione" type="hidden">
                  <input type="button" class="pulsante" value="OK" onClick="addFunzione()" title="Aggiunge una funzione (voce) alla cartella selezionata"/>
		</td>
	</tr>
	
	<tr>
		<td colspan="2" class="intestazione">Rinomina</td>
	</tr>
	
	
	<tr>
		<td colspan="2 "class="tasti" nowrap><af:textBox name="nome" type="text" size="30" maxlength="30" />
		<input type="button" class="pulsante" value="OK" onClick="rinomina()" title="Rinomina la voce selezionata"/>
		</td>
	</tr>

	<tr>
		<td colspan="2" class="intestazione">Salvataggio</td>
	</tr>

	<tr>
		<td colspan="1 "class="tasti" nowrap><input type="button" 
			value="Salva menu" class="pulsante" 
			onClick="salva();" title="Salva l'intero menu"/>
		</td><td colspan="1 "class="tasti" nowrap>Salva il menu</td>
	</tr>
	<tr>
		<td colspan="1 "class="tasti" nowrap><input type="button" 
			value="Clona menu" class="pulsante" 
			onClick="clona();" title="Clona l'intero menu"/>
		</td><td colspan="1 "class="tasti" nowrap>Clona il menu</td>
	</tr>

	</table>
</af:form>

</div>
 

<p>&nbsp;</p>


</body>
</html>

