<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                  
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
 	PageAttribs attributi = new PageAttribs(user, _page);
 	
 	String apriDiv = "none";
 	
 	apriDiv = (String)serviceRequest.getAttribute("APRIDIV");
 	
 	if(apriDiv == null) { 
 		apriDiv = "none"; 
 	} else { 
 		apriDiv = ""; 
 	}

	boolean canInsert = false;
	boolean canDelete = false;
  	boolean readOnlyStr=true;
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
        canInsert   =  attributi.containsButton("INSERISCI");
        readOnlyStr = !attributi.containsButton("AGGIORNA");
        canDelete   =  attributi.containsButton("RIMUOVI");
		
    	if((!canInsert) && (readOnlyStr) && (!canDelete)){
    	//canInsert=false;
        //canDelete=false;
        //rdOnly=true;
    	}else{
        boolean canEdit=filter.canEditLavoratore();
        if (canInsert){
          canInsert=canEdit;
        }
        if (canDelete){
          canDelete=canEdit;
        }        
        if (!readOnlyStr){
          readOnlyStr=!canEdit;
        }        
    }
  }
	
	// dettaglio
	
	boolean nuovo = true;
	
	if(serviceResponse.containsAttribute("M_LoadServizioCM")) { 
		nuovo = false; 
	} else { 
		nuovo = true; 
	}
	
	String codServizio = "";
	String strSpecifica = "";
	String strDescrizione = "";
	
	String     dtmIns             = null; 
	String     dtmMod             = null;    
	BigDecimal cdnUtIns           = null; 
	BigDecimal cdnUtMod           = null;
	Testata testataDettaglio = null;
	
	if(!nuovo) {
		
		Vector vectServizioInfo = serviceResponse.getAttributeAsVector("M_LoadServizioCM.ROWS.ROW");
		
		if ( (vectServizioInfo != null) && (vectServizioInfo.size() > 0) ) {

			SourceBean bean = (SourceBean)vectServizioInfo.get(0);

		    codServizio  = StringUtils.getAttributeStrNotNull(bean, "CODSERVIZIO");    
		    strSpecifica    = StringUtils.getAttributeStrNotNull(bean, "STRSPECIFICA");
		    strDescrizione = StringUtils.getAttributeStrNotNull(bean, "STRDESCRIZIONE");
		    
		    cdnUtIns        = (BigDecimal)bean.getAttribute("CDNUTINS");
		    dtmIns          = (String)bean.getAttribute("DTMINS");
		    cdnUtMod        = (BigDecimal)bean.getAttribute("CDNUTMOD");
		    dtmMod          = (String)bean.getAttribute("DTMMOD");
		    
		    testataDettaglio = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
		    
		}
	}
	
%>

<% 

	String DATDICHIARAZIONE = "";
	String NUMPERSONE = "";

   
     
    InfCorrentiLav testata= null;
    Linguette l  = null;
    Testata operatoreInfo = null;
    String finestra = null;

	finestra = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"finestra");
	
	Vector tipiServiziUtente = serviceResponse.getAttributeAsVector("ListaServiziSelezionati.ROWS.ROW");
	
    Vector tipiServizi = serviceResponse.getAttributeAsVector("ComboServizi.ROWS.ROW");

    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));   


    testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore,user);
    int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
    l  = new Linguette(user,  _cdnFunz, _page, new BigDecimal(cdnLavoratore));
    //operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);


	int numero = 0;
    SourceBean row2 = null;
    Vector rows2 = serviceResponse.getAttributeAsVector("M_Controllo_CM.ROWS.ROW");
    if (rows2.size()==1) {
		row2 = (SourceBean)rows2.get(0);
        numero = Integer.valueOf(""+row2.getAttribute("numero")).intValue();
    }
    
%>
<html>
<head>
<%if(!finestra.equals("")){%>
   	<title>Lista servizi</title>	
<%}else{%>
   	<title>&nbsp;</title>
<%}%>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

<%@ include file="CommonScript.inc"%>
<script language="Javascript" src="../../js/docAssocia.js"></script>
<SCRIPT language="JavaScript" src=" ../../js/layers.js"></SCRIPT>

<af:linkScript path="../../js/"/>

<script language="Javascript">
<%
    attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
%>

   function aggiornaServizi(nomeDiv){
	   var collDiv = document.getElementsByName(nomeDiv);
  	   var objDiv = collDiv.item(0);
  	   objDiv.style.display = "";
	}

   function showServizioDetails(codServizio) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

	    var s= "AdapterHTTP?PAGE=CMServiziPage";
	    s += "&MODULE=M_LoadServizioCM";
	      s += "&codServizio=" + codServizio;
	    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
	    s += "&CDNFUNZIONE=<%= _funzione %>";
	    s+= "&APRIDIV=1";
	    setWindowLocation(s);
	}

   function showNuovoServizio() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

	    var s= "AdapterHTTP?PAGE=CMServiziPage";
	    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
	    s += "&CDNFUNZIONE=<%= _funzione %>";
	    s+= "&APRIDIV=1";
	    setWindowLocation(s);
	}
	
</script>
     
<script language="Javascript">
	if((window.top != null) && (window.top.menu != null))
		window.top.menu.caricaMenuLav(<%=_funzione%>,<%=cdnLavoratore%>);
</script>
    
</head>


<body class="gestione" onload="rinfresca()">



<%
   testata.show(out);
   l.show(out);

   boolean canModify = !readOnlyStr;
%>   

<font color="green">
	<af:showMessages prefix="M_Inserisci_CM_Servizio"/>
	<af:showMessages prefix="M_Aggiorna_CM_Servizio"/>
	<af:showMessages prefix="M_Delete_Servizio"/>
</font>
<font color="red">
	<af:showErrors />
</font>

				<af:list moduleName="ListaServiziSelezionati"  
						 configProviderClass="it.eng.sil.module.amministrazione.ListaServiziSelezionatiConfig" 
						 skipNavigationButton="1" 
						 canDelete="<%= canDelete ? \"1\" : \"0\" %>" 
						 canInsert="<%= canInsert ? \"1\" : \"0\" %>"   
						 jsSelect="showServizioDetails"/>
		
	<%if(numero < 1){%>	
		
				<p class="titolo">Il lavoratore non è in collocamento mirato (manca iscrizione L. 68/99)!</p> 
				&nbsp;&nbsp;&nbsp;&nbsp;
		
	<%}%>
		
				<center>	
					<%if (canModify || canInsert) { %>				
						<input type="button" onclick="showNuovoServizio();" value = "Nuovo servizio" class="pulsanti">
						<!-- apriNuovoDivLayer -->
						&nbsp;&nbsp;
					<%}%>
				</center>


<%
String divStreamTop = StyleUtils.roundLayerTop(true);
String divStreamBottom = StyleUtils.roundLayerBottom(true);
%>

  <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
   style="position:absolute; width:80%; left:50; top:100px; z-index:6; display:<%=apriDiv%>;">
 <a name="aLayerIns"></a>
   
  <%out.print(divStreamTop);%>
   <af:form method="POST" action="AdapterHTTP" name="Frm2" onSubmit="">	  
	   
   
    <table width="100%">
      <tr>
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">
        	Servizi di riferimento
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>
    <br>
	<table align="center" border="0">
	    <tr>
		    <td colspan="2" align="center">
		    	<p class="titolo">Servizi di riferimento</p>  
			</td>	
    	</tr>
	    <tr>
			    <td colspan="2" align="center">
			      &nbsp;
			    </td>	
    	</tr>
	    <tr>
          <td class="etichetta">
          	<% if (nuovo) { %>
          		Servizi
           	<% } else { %>
           		Servizio
           	<% } %>
          </td>
          <td class="campo">
          	<% if (nuovo) { %>
            <af:comboBox name="servizi" title="Servizi" multiple="true" required="true" moduleName="ComboServizi" addBlank="true" />
            <% } else { %>
            <%=strDescrizione%>
            <% } %>
		  </td>
		</tr>
		  <tr>
		  	<td class="etichetta">Specifica</td>
			<td class="campo">
		    	<af:textArea cols="40" rows="4" maxlength="3000" classNameBase="textarea" name="STRSPECIFICA" 
                			 required="false" title="Specifica" value="<%=strSpecifica%>"/>
            </td>
		</tr>
	</table>
	<table>
		<tr><td colspan="2" align="center"></td></tr>
	    <tr>
			<td colspan="2" align="center">
				<% if (nuovo) { %>
				<input type="button" class="pulsanti" value="Inserisci" onClick="inserisciServizi()">
				<% } else { %>
				<input type="button" class="pulsanti" value="Aggiorna" onClick="aggiornaServizio()">
				<% } %>
			
				<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
			</td>
    	</tr>    
    	<% if (!nuovo) { %>    
          <tr><td colspan="2" width="70%" align="center"><%testataDettaglio.showHTML(out);%></td></tr>      
        <% } %>
	</table>
	    
<script>

	function aggiornaServizio() {

		var url;

		url = "AdapterHTTP?PAGE=CMServiziPage&aggiornaServizio=1";
	    url = url + "&CDNFUNZIONE=<%=_funzione%>";
	    url = url + "&CDNLAVORATORE=<%=cdnLavoratore%>";
	    url = url + "&CODSERVIZIO=<%=codServizio%>";
	    url = url + "&STRSPECIFICA=" +document.Frm2.STRSPECIFICA.value;
		
	    document.location = url;
	    
	  	window.close();
		
	}
    
    function inserisciServizi(){

	    var kk;
	    var current;
	    var doppi = "";
	var url;
	 var count = 0;
	 var countDoppi = 0;
	 var serviziUtente = [];

<%
	
	for (int j = 0; j < tipiServiziUtente.size(); j++) { 
		SourceBean rowTipiServizi = (SourceBean)tipiServiziUtente.get(j);
		String codTipiServizi = (String)rowTipiServizi.getAttribute("CODICE");
		String dscTipiServizi = (String)rowTipiServizi.getAttribute("DESCRIZIONE");
		%>serviziUtente.push({"codice":"<%=codTipiServizi%>","descrizione":"<%=dscTipiServizi%>"});<%
	}
	
%>
	
	      url = "AdapterHTTP?PAGE=CMServiziPage&nuovoServizio=1";
	      url = url + "&CDNFUNZIONE=<%=_funzione%>";
	      url = url + "&CDNLAVORATORE=<%=cdnLavoratore%>";
	      url = url + "&STRSPECIFICA=" +document.Frm2.STRSPECIFICA.value;
	      <%
	      for (int j = 0; j < tipiServizi.size(); j++) { //ciclo nella query
				SourceBean rowTipiServizi = (SourceBean)tipiServizi.get(j);
		
				String codTipiServizi = (String)rowTipiServizi.getAttribute("CODICE");
				String dscTipiServizi = (String)rowTipiServizi.getAttribute("DESCRIZIONE");
			    %>
			    var k = 0;
			   
			    <%
				for (int i = 1; i <= tipiServizi.size(); i++) { //ciclo nel javascript
				%>
					k++;
					if(document.Frm2.servizi.options[<%=i%>].value == "<%=codTipiServizi%>" && document.Frm2.servizi.options[<%=i%>].selected){
						var codice = document.Frm2.servizi.options[<%=i%>].value;
						url = url + "&codServizio"+k+"="+codice;
						count++;

						for (kk = 0; kk<serviziUtente.length;kk += 1) {
							current = serviziUtente[kk].codice;
							if (codice == current) {
								if (countDoppi) {
									doppi += ", ";
								}
								doppi += serviziUtente[kk].descrizione;
								countDoppi++;
							}
						}
						
					}	
				<%
				}
		  }
		  %>

		  if (countDoppi) {
			  alert("Per procedere con l'inserimento occorre deselezionare i servizi già presenti: " + doppi);
			  return;
		  }
		  
		  if(count == 0) {
		  	alert("Il campo Servizi è obbligatorio");
		  	return false;
		  } else {
		  	url = url + "&numRows=" + "<%=tipiServizi.size()%>";
		  	document.location = url;
		  	window.close();
		 }
	
	return true;
}
</script>
</af:form>
<%out.print(divStreamBottom);%>
</div>
<br/>
</body>
</html>