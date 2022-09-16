<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
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
  it.eng.sil.security.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
//ecco un sacco di accenti àèìòùé
  BigDecimal cdnUtIns = null;
  String dtmIns = null;
  BigDecimal cdnUtMod = null;
  String dtmMod = null;
  Testata testata = null;
  String numkloDispoL68=null;
  String codMonoDispoL8 = null;
  String codGradoOcc = null;
  String  prgDispoL68 = null;
  String  cdnLavoratore = null;
		
  String  flgDispoEP = null;
  String  flgDispoAzi = null;
  String  flgDispoTirocinio = null;
  String  flgDispoFP = null;
  String  strNoteLimDispo = null;


  boolean inserisciNew = !(serviceRequest.getAttribute("inserisciNew")==null || 
                            ((String)serviceRequest.getAttribute("inserisciNew")).length()==0);

  cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

  boolean read = true;
   boolean piudiuno = false;
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));

  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("inserisci");
  boolean canDelete= attributi.containsButton("rimuovi");
  boolean opzioneCancella = false;
  
  // valutiamo se c'è la disponibilità per quel lavoratore	
 // int numero = 0;
  //SourceBean row = null;
  SourceBean rowLoad = null;
  Vector rows= serviceResponse.getAttributeAsVector("M_Legge68.ROWS.ROW");
  Vector rowsLoad = serviceResponse.getAttributeAsVector("M_Load_DispL68.ROWS.ROW");
  
  // Maggiore di 1 non dovrebbe 
  if (rowsLoad.size()>0) {
      //row = (SourceBean)rows.get(0);
   	  //numero = Integer.valueOf(""+row.getAttribute("numero")).intValue(); 
      
   	   		opzioneCancella = true; //Se esiste il record per il lavoratore, allora si può anche cancellare
   	  
      		//Anche se fosse più d'una prendo la prima
	        rowLoad = (SourceBean)rowsLoad.get(0);
	        
		    prgDispoL68 = Utils.notNull(rowLoad.getAttribute("prgDispoL68"));
		    codMonoDispoL8 = Utils.notNull(rowLoad.getAttribute("codMonoDispoL8"));
		    codGradoOcc = Utils.notNull(rowLoad.getAttribute("codGradoOcc"));
		    flgDispoEP = Utils.notNull(rowLoad.getAttribute("flgDispoEP"));
		    flgDispoAzi = Utils.notNull(rowLoad.getAttribute("flgDispoAzi"));
		    flgDispoTirocinio = Utils.notNull(rowLoad.getAttribute("flgDispoTirocinio"));
		    flgDispoFP = Utils.notNull(rowLoad.getAttribute("flgDispoFP"));
		    strNoteLimDispo = Utils.notNull(rowLoad.getAttribute("strNoteLimDispo"));
		    cdnUtIns = (BigDecimal) rowLoad.getAttribute("cdnUtIns");
		    cdnUtMod = (BigDecimal) rowLoad.getAttribute("cdnUtMod");
		    dtmIns = (String) rowLoad.getAttribute("DTMINS");
		    dtmMod = (String) rowLoad.getAttribute("DTMMOD");
		    numkloDispoL68 = Utils.notNull(rowLoad.getAttribute("numkloDispoL68"));
		    
		    testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod); 
	 	
	 	//Deve esserci una sola disponibilità. Se ce ne sono di più prendo il primo e stampo un Warning
	 	if (rowsLoad.size()!= 1) {
	 	 piudiuno=true;
	  }
  }
  //rows.size()=0 ->  Nuova disponibilità legge 68	
  else {
	  prgDispoL68 = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgDispoL68");
	  flgDispoEP = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"flgDispoEP");
	  flgDispoAzi = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"flgDispoAzi");
	  flgDispoTirocinio = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"flgDispoTirocinio");
	  flgDispoFP = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"flgDispoFP");
	  strNoteLimDispo = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"strNoteLimDispo");
	  codMonoDispoL8 = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codMonoDispoL8");
	  codGradoOcc = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codGradoOcc");

	  //cdnUtIns = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	  //cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	  
	  testata = new Testata(null, null, null, null);	  
  }
  
    Vector vecDispo = serviceResponse.getAttributeAsVector("M_GETLAVCM.ROWS.ROW");
    if (vecDispo.size() == 0) {
    	canModify = false;
    }

%>

<html>

<head>
  <title>Disponibilità legge 68</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>


  <script language="Javascript">
	<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
	  <% 
	  //Genera il Javascript che si occuperà di inserire i links nel footer
	  attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
	  %>
	
	  window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);  

	  
	  function inserisciDispoL68(){
		valorizzaFlagDispo();	
		return true;				
	  }
      
      var flagChanged = false;
        
      function fieldChanged() {
          flagChanged = true;
      }
      
      function controlloReset(){
	  	<%if ( ("S".equalsIgnoreCase(codMonoDispoL8)) ) {%>
			abilitaDisp();
		<%}else{%>
			disabilitaDisp();
		<%}%>
	  }
      
      function valorizzaFlagDispo() {
		if(document.Frm1.codMonoDispoL8.value == "S"){  		  
			if(document.Frm1.flgDispoEP.checked) 
				document.Frm1.flgDispoEP.value = "S";
		  	else 
		  		document.Frm1.flgDispoEP.value = "N";
	
		  	if(document.Frm1.flgDispoAzi.checked) 
		  		document.Frm1.flgDispoAzi.value = "S";
		  	else 
		  		document.Frm1.flgDispoAzi.value = "N";
		}            
      }
      
	  function gestisciDisp(){
	  	if(document.Frm1.codMonoDispoL8.value == "S"){
			abilitaDisp();			  
		}
		else{
			document.Frm1.flgDispoEP.checked = false;
			document.Frm1.flgDispoAzi.checked = false;
			document.Frm1.flgDispoTirocinio.value = "";
			document.Frm1.flgDispoFP.value = "";	
			document.Frm1.strNoteLimDispo.value = "";
			disabilitaDisp();				  
		}
	  }
      
	  function disabilitaDisp(){
		document.Frm1.flgDispoEP.disabled = true;
		document.Frm1.flgDispoAzi.disabled = true;
		document.Frm1.flgDispoTirocinio.disabled = true;
		document.Frm1.flgDispoFP.disabled = true;
		document.Frm1.strNoteLimDispo.disabled = true;	
		document.Frm1.codGradoOcc.disabled = true;				  
	  }      
      
	  function abilitaDisp(){
		document.Frm1.flgDispoEP.disabled = false;
		document.Frm1.flgDispoAzi.disabled = false;
		document.Frm1.flgDispoTirocinio.disabled = false;
		document.Frm1.flgDispoFP.disabled = false;
		document.Frm1.strNoteLimDispo.disabled = false;	
		document.Frm1.codGradoOcc.disabled = false;				  
	  } 

      function aggiornaDispL68(){	  
		  if(!flagChanged){
			  alert("Nessuna modifica effettuata");
			  return true;
		  }
		  else{	
			  valorizzaFlagDispo();
			  return true;
		  }
	  }
	  
	  function mostra(id)
  		{ 
    		var div = document.getElementById(id);
    		<% if (vecDispo.size() == 0) { %>
       			div.style.display="inline";
       		<%}
       		  if ( !("S".equalsIgnoreCase(codMonoDispoL8)) ) {%>
       			disabilitaDisp();
       		<%}%>
  	  }
  	  
  	  function controllaCampi(){
  	  		if( document.Frm1.codMonoDispoL8.value == "S" && document.Frm1.codGradoOcc.value == "" ){
  	  			alert("Indicare il grado di occupabilità");
  	  			document.Frm1.codGradoOcc.focus();
  	  			return false;
  	  		}else return true;
  	  }
          
  </script>

</head>


<body class="gestione" onLoad="javascript:mostra('citComEur');rinfresca()">

  <%
  infCorrentiLav.show(out); 
  linguette.show(out);
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<div align="left" id="citComEur" style="display:none"><font  color="red">
 <UL>
   <LI><strong>Il lavoratore non è in collocamento mirato!</strong></LI>
</UL></font></div>

<%if (piudiuno){%>
<UL>
   <LI>Attenzione sono presenti pi&ugrave; disponibilit&agrave; per questo lavoratore!</LI>
</UL>
<%}%>
  
  <af:form method="POST" action="AdapterHTTP" name="Frm1">
    <input type="hidden" name="PAGE" value="CMDispL68Page">
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>

	<input type="hidden" name="prgDispoL68" value="<%=prgDispoL68%>">
		
	
    

    <p align="center">
	    <font color="red">
	       <af:showErrors />
	    </font>
	    <af:showMessages prefix="M_Insert_DispL68"/>
		<af:showMessages prefix="M_Save_DispL68"/>
	    
	</p>

	 
    
		 <%out.print(htmlStreamTop);%>

<%-- ************************************************* --%>

		      <table class="main" border="0">
		        <tr>
		          <td colspan="3">&nbsp;</td>	
		        </tr>
		        <tr>
		          <td colspan="3">&nbsp;</td>	
		        </tr>
		        <tr>
		          <td class="etichetta" colspan="1">Disponibilità offerte legge 68</td>	
		          <td class="campo" colspan="2">
					
					 <af:comboBox
					      name="codMonoDispoL8"
					      classNameBase="input"
					      disabled="<%= String.valueOf(!canModify) %>"
					      onChange="javaScript:gestisciDisp(); fieldChanged();">
					      <%if (opzioneCancella){ %>
					      		<option value=""  <% if ( "".equalsIgnoreCase(codMonoDispoL8) ) { %>SELECTED="true"<% } %> >Cancella</option>
					      <%} %>
					      <option value="N" <% if ( "N".equalsIgnoreCase(codMonoDispoL8) ) { %>SELECTED="true"<% } %> >Non interessato ad usufruire dei servizi del collocamento mirato</option>
					      <option value="S" <% if ( "S".equalsIgnoreCase(codMonoDispoL8) ) { %>SELECTED="true"<% } %> >Interessato e disponibile offerte lavoro collocamento mirato</option>
					 </af:comboBox>
					
					</td>
		        </tr>
		        <tr>
		          <td colspan="3">&nbsp;</td>	
		        </tr>
		        <tr>
		          <td class="etichetta" colspan="1">Grado di occupabilità</td>	
		          <td class="campo" colspan="2">
					
					 <af:comboBox
					      name="codGradoOcc"
					      classNameBase="input"
					      disabled="<%= String.valueOf(!canModify) %>"
					      onChange="fieldChanged();">
					      <option value=""  <% if ( "".equalsIgnoreCase(codGradoOcc) ) { %>SELECTED="true"<% } %> ></option> 
					      <option value="SEG" <% if ( "SEG".equalsIgnoreCase(codGradoOcc) ) { %>SELECTED="true"<% } %> >Segnalabile</option>
					      <option value="SOS" <% if ( "SOS".equalsIgnoreCase(codGradoOcc) ) { %>SELECTED="true"<% } %> >In Sospeso</option>
					      <option value="PER" <% if ( "PER".equalsIgnoreCase(codGradoOcc) ) { %>SELECTED="true"<% } %> >Percorsi di Sostegno</option>
					      <option value="NSE" <% if ( "NSE".equalsIgnoreCase(codGradoOcc) ) { %>SELECTED="true"<% } %> >Attualmente non Segnalabile</option>
					 </af:comboBox>
					
					</td>
		        </tr>
		        <tr>
		          <td colspan="3">&nbsp;</td>	
		        </tr>		        
		        <tr>
		    	    <td class="etichetta">
		        		Disponibilità avviamento presso
		        	</td>
		    	    <td class="campo" colspan="2">
					
		  			    <%String checkDispoEP = "";	
		  			    if(flgDispoEP.equals("S")) checkDispoEP="CHECKED";
		  			    else checkDispoEP="";%>	    	    
		        		<input type="checkbox" name="flgDispoEP" onChange="fieldChanged();" value="<%=flgDispoEP%>" <%=checkDispoEP%> />
		        		Enti pubblici&nbsp;&nbsp;&nbsp;

		  			    <%String checkDispoAzi = "";	
		  			    if(flgDispoAzi.equals("S")) checkDispoAzi="CHECKED";
		  			    else checkDispoAzi="";%>	    	    		        		
		        		<input type="checkbox" name="flgDispoAzi" onChange="fieldChanged();" value="<%=flgDispoAzi%>" <%=checkDispoAzi%>/>
		        		&nbsp;Enti privati
		        	
		        	</td>
		    	</tr>
		        <tr>
		          <td colspan="3">&nbsp;</td>	
		        </tr>		    	
		        <tr>
		          <td class="etichetta" colspan="1">Disponibilità tirocinio formativo</td>	
				  <td class="campo" colspan="2">
				 
					    <af:comboBox 
					      name="flgDispoTirocinio"
					      classNameBase="input"
					      disabled="<%= String.valueOf(!canModify) %>"
					      onChange="fieldChanged();">
					      <option value=""  <% if ( "".equalsIgnoreCase(flgDispoTirocinio) )  { %>SELECTED="true"<% } %> ></option>
					      <option value="S" <% if ( "S".equalsIgnoreCase(flgDispoTirocinio) ) { %>SELECTED="true"<% } %> >Sì</option>
					      <option value="N" <% if ( "N".equalsIgnoreCase(flgDispoTirocinio) ) { %>SELECTED="true"<% } %> >No</option>
					    </af:comboBox>
					
					&nbsp;&nbsp;&nbsp;
				  </td> 
		        </tr>
		        <tr>
		          <td class="etichetta" colspan="1">Disponibilità formazione professionale</td>	
				  <td class="campo" colspan="2">
				 	
					    <af:comboBox 
					      name="flgDispoFP"
					      classNameBase="input"
					      disabled="<%= String.valueOf(!canModify) %>"
					      onChange="fieldChanged();">
					      <option value=""  <% if ( "".equalsIgnoreCase(flgDispoFP) )  { %>SELECTED="true"<% } %> ></option>
					      <option value="S" <% if ( "S".equalsIgnoreCase(flgDispoFP) ) { %>SELECTED="true"<% } %> >Sì</option>
					      <option value="N" <% if ( "N".equalsIgnoreCase(flgDispoFP) ) { %>SELECTED="true"<% } %> >No</option>
					    </af:comboBox>
					   
					&nbsp;&nbsp;&nbsp;
				  </td>
		        </tr>
		        <tr>
		          <td colspan="3">&nbsp;</td>	
		        </tr>		        		        
		        <tr>
		          <td class="etichetta" colspan="1">Limitazioni delle disponibilità</td>	
		          <td class="campo" colspan="2">
		         	<af:textArea cols="80" rows="5" maxlength="2000" classNameBase="textarea"  
                		name="strNoteLimDispo"  
                		value="<%=strNoteLimDispo%>" validateOnPost="true" 
                        required="false" title="Note" onKeyUp="fieldChanged();" readonly="<%= String.valueOf(!canModify) %>"/>
                 	</td>		        
				</tr>
		        <tr>
		          <td colspan="3">&nbsp;</td>	
		        </tr>
		        <tr>
		          <td colspan="3">&nbsp;</td>	
		        </tr>		        				
		        <tr>
		          <td colspan="3" align="center">
		          	  <%
		          	  if(rowsLoad.size() > 0 && canModify==true){
		          	  %>	
		          	  	<input class="pulsanti" type="submit" name="aggiorna" value="Aggiorna" onClick="return controllaCampi() && aggiornaDispL68()">
		          	  	&nbsp;&nbsp;
			          	<input type="reset" class="pulsanti" value="Annulla" onClick="controlloReset();"/>
		          	  	&nbsp;&nbsp;

		          	  	<input type="hidden" name="aggiornamento" value="1">
		          	  	<input type="hidden" name="numkloDispoL68" value="<%= numkloDispoL68 %>">
		          	  <%
		          	  }else if(rowsLoad.size() == 0 && canModify==true){
		          	  %>
			          	<input class="pulsanti" type="submit" name="inserisci" value="Inserisci" onClick="return controllaCampi() && inserisciDispoL68()">
			          	&nbsp;&nbsp;
			          	<input type="reset" class="pulsanti" value="Annulla" onClick="controlloReset();"/>
			          	&nbsp;&nbsp;
			          	<input type="hidden" name="inserisciNew" value="1">
			          	<input type="hidden" name="numkloDispoL68" value="1">
					  <%}%>
				  </td>
		        </tr>							
			  </table>
			  <br><br>
			  <center>
		        <table align="center">
		          <tr>
		    	    <td>
		    	      <% testata.showHTML(out); %>
		    	    </td>
		    	  </tr>
		        </table>
		      </center>
		      
	</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>