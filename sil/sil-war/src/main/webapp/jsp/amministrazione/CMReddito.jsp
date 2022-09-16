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

	boolean canInsert = false;
	boolean canDelete = false;
  	boolean readOnlyStr=true;
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
        canInsert = attributi.containsButton("INSERISCI");
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
%>

<% 
    Vector rows = serviceResponse.getAttributeAsVector("M_Load_Reddito.ROWS.ROW");
	SourceBean row                = null;

	Vector anni = serviceResponse.getAttributeAsVector("M_Load_anni.ROWS.ROW");
	SourceBean annoCorr =null;
	
	String     annoCorrente       = null;
    String     datDichiarazione   = null; 
    String     numAnno            = null; 
    String     numReddito         = null; 
    String     datFineVal         = null; 
    BigDecimal numKloLavReddito   = null;

    String     dtmIns             = null; 
    String     dtmMod             = null;    
    BigDecimal cdnUtIns           = null; 
    BigDecimal cdnUtMod           = null;
    String     prgLavReddito      = null;
     
    InfCorrentiLav testata= null;
    boolean flag_insert  = false;
    Linguette l  = null;
    Testata operatoreInfo = null;

    prgLavReddito = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PRGLAVREDDITO");

    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));   

    
    String url_nuovo = "AdapterHTTP?PAGE=CMRedditoPage" +
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + _funzione +
                     "&APRIDIV=1";

    
    String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
    if(apriDiv == null) { apriDiv = "none"; }
    else { apriDiv = ""; }

    boolean nuovo = true;
    if(serviceResponse.containsAttribute("M_Load_Reddito")) { nuovo = false; }
    else { nuovo = true; }

    String htmlStreamTop = StyleUtils.roundTopTable(canInsert);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canInsert);
    
    boolean strAnno=false; 

	if(!nuovo) {
	    // Sono in modalità "Dettaglio"
	    if(rows != null && !rows.isEmpty())  { 
	        row  = (SourceBean) rows.elementAt(0);    

	        datDichiarazione = "" +          Utils.notNull(row.getAttribute("datDichiarazione"));
	        numAnno          = "" +          Utils.notNull(row.getAttribute("numAnno"));         
	        numReddito       = "" +          Utils.notNull(row.getAttribute("numReddito"));         
	        datFineVal       = "" +          Utils.notNull(row.getAttribute("datFineVal"));         
	        dtmIns           = "" +          Utils.notNull(row.getAttribute("DTMINS"));         
	        dtmMod           = "" +          Utils.notNull(row.getAttribute("DTMMOD"));         
	        cdnUtIns         = (BigDecimal)  row.getAttribute("CDNUTINS");         
	        cdnUtMod         = (BigDecimal)  row.getAttribute("CDNUTMOD");
	        numKloLavReddito = (BigDecimal)  row.getAttribute("numKloLavReddito");
	       }
		strAnno = true;
	}

    testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore,user);
    int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
    l  = new Linguette(user,  _cdnFunz, _page, new BigDecimal(cdnLavoratore));
    operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

    //rimosso controllo pre tutti i lavoratori è possibile inserire il reddito
	int numero = 1;
    SourceBean row2 = null;
    Vector rows2 = serviceResponse.getAttributeAsVector("M_Controllo_CM.ROWS.ROW");
    if (rows2.size()==1) {
		row2 = (SourceBean)rows2.get(0);
        //numero = Integer.valueOf(""+row2.getAttribute("numero")).intValue();
    }
    if(numero < 1){
    	canInsert = false;
    }
    
    Calendar oggi = Calendar.getInstance();
	String annoAttuale = Integer.toString(oggi.get(1));
    
%>
<html>
<head>
<title>&nbsp;</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

<%@ include file="CommonScript.inc"%>
<script language="Javascript" src="../../js/docAssocia.js"></script>
<SCRIPT language="JavaScript" src=" ../../js/layers.js"></SCRIPT>

<script>
	<%@ include file="../patto/_sezioneDinamica_script.inc"%>
</script>

<af:linkScript path="../../js/"/>

<script language="Javascript">
<%
    attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
%>
</script>  

<script>

	function chiudiLayer() {
	
	  ok=true;
	  if (flagChanged) {
	     if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
	         ok=false;
	     } else { 
	         flagChanged = false;
	     }	     
	  }
	  if (ok) {
	     ChiudiDivLayer('divLayerDett');
	  }
	}

    function isNumeric(val){
  	    return(parseFloat(val,10)==(val*1));
    }
	
	function Insert() {
	    var anno = document.Frm1.numAnno.value;
	    var isAnno = false;
	    var numReddito = document.Frm1.numReddito.value;
	    var isNumReddito = false;
	    var esisteAnno = false;
	    
	    if(isNumeric(numReddito))
	    	isNumReddito = true;
	  	
	  	var now = new Date();
		var thisYear = now.getYear();
	    
	    var numAnno = parseInt(anno);
	    
	    var annoC;
		<%for(int k = 0; k < anni.size(); k++){
	        annoCorr  = (SourceBean) anni.elementAt(k);    
	        annoCorrente = "" + Utils.notNull(annoCorr.getAttribute("numAnno"));%>
			annoC = '<%=annoCorrente%>';
			if(anno == annoC){
				esisteAnno = true;
			}
		<%}%>
		
		if(esisteAnno){
	    	alert("Anno di riferimento già esistente!");
	    	return;			
		}
		
	    if(numReddito!= "" && !isNumReddito){
	    	alert("Il reddito deve avere valore numerico!");
	    	return;
	    }
		
		/*var annoAtt = '<%=annoAttuale%>';
		
		if(anno != "" && document.Frm1.numAnno.value >= annoAtt){
	    	alert("L'Anno di riferimento deve essere inferiore all'anno attuale.");
	    	return;
	    }*/
	    
	    document.Frm1.MODULE.value= "M_inserisci_reddito";
	    if(controllaFunzTL())  {
	        doFormSubmit(document.Frm1);
	    }else
	    	return;
	}

	function Update(){

	    var anno = document.Frm1.numAnno.value;
	    var isAnno = false;
	    var numReddito = document.Frm1.numReddito.value;
	    var isNumReddito = false;
	    var esisteAnno = false;
	    
	    if(isNumeric(numReddito)) isNumReddito = true;

		var now = new Date();
		var thisYear = now.getYear();
	  	if(!isNumReddito){
	    	alert("Il reddito deve avere valore numerico!");
	    	return;
	    }
		
		var datiOk = controllaFunzTL();
	    if (datiOk) {
	        document.Frm1.MODULE.value = "M_SaveReddito";
	        doFormSubmit(document.Frm1);
	    }
	}
    
    window.top.menu.caricaMenuLav( <%=_funzione%>,<%=cdnLavoratore%>);
</script>
</head>

<body class="gestione" onload="rinfresca()">
<%
   testata.show(out);   
   l.show(out);
%>
    

<font color="red"><af:showErrors/></font>
<font color="green">

 <af:showMessages prefix="M_SaveObbligoFormativo"/>
 <af:showMessages prefix="M_InsertObbligoFormativo"/>

</font>

<script language="javascript">
  var flgInsert = <%=flag_insert%>;
</script>


<af:form method="POST" action="AdapterHTTP" name="Frm1">
    <input type="hidden" name="PAGE" value="CMRedditoPage">
    <input type="hidden" name="MODULE" value=""/>
    <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
    <input type="hidden" name="PRGLAVREDDITO" value="<%=prgLavReddito%>"/>
    <input type="hidden" name="numKloLavReddito" value="<%=numKloLavReddito%>"/>
    
    <p align="center">
      <center>
        <font color="green">
          <af:showMessages prefix="M_inserisci_reddito"/>
          <af:showMessages prefix="M_DelReddito"/>
          <af:showMessages prefix="M_SaveReddito"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
      <div align="center">    	      
	      <af:list moduleName="M_ListRedditi" skipNavigationButton="0" 
	               canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
	               canInsert="<%=canInsert ? \"1\" : \"0\"%>" />
	      
	      <%if(canInsert) {%>
	          <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuovo reddito CM"/>  
	      <%}%>
	      
          <%if(numero < 1){%>	
			  <center>
			    <table>
			   	  <tr>
			   		<td align="center">
			   			Non è possibile inserire un nuovo reddito.
			    	</td>
			      </tr>
			   	  <tr>
			   		<td align="center">
			   			<p class="titolo">
							Il lavoratore non è in collocamento mirato (manca iscrizione L. 68/99)!
						</p> 
			    	</td>
			      </tr>
			    </table>
			  </center>
          <%}%>
          	      
      </div> 
    <p/>


<!-- LAYER -->
<%
boolean canModify = !readOnlyStr;
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>   
  <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
     style="position:absolute; width:60%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">

  <!-- Stondature ELEMENTO TOP -->
  <%out.print(divStreamTop);%>

    <table width="100%">
      <tr>
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">
        <%if(nuovo){%>
          Nuovo
        <%} else {%>
          Dettaglio reddito
        <%}%>   
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>

  <%//if(nuovo) {%>          

    <table width="100%">
	    <tr><td><br/></td></tr>
	    <tr>
		     <td class="etichetta">Data dichiarazione</td>
		     <td colspan="3"><af:textBox classNameBase="input" title="Data dichiarazione" type="date" name="datDichiarazione" value="<%=datDichiarazione%>" validateOnPost="true" 
		              required="true" readonly="<%=String.valueOf(readOnlyStr)%>" size="11" maxlength="10"/>
		     </td> 
	    </tr>
	    <tr>
		     <td class="etichetta">Reddito</td>
		     <td colspan="3"><af:textBox classNameBase="input" title="Reddito" type="integer"  name="numReddito" value="<%=numReddito%>" validateOnPost="true" 
		              required="true" readonly="<%=String.valueOf(readOnlyStr)%>" size="20" maxlength="20" />
		     </td>
	    </tr>
	    <tr>
		     <td class="etichetta">Anno di riferimento</td>
		     <td colspan="3"><af:textBox classNameBase="input" title="Anno" type="text" name="numAnno" value="<%=numAnno%>" validateOnPost="true" 
		              required="true" readonly="<%=String.valueOf(strAnno || readOnlyStr)%>" size="4" maxlength="4"/>
		     </td>
	    </tr>
	    <tr>
		     <td class="etichetta">Data fine validita</td>
		     <td colspan="3"><af:textBox classNameBase="input" title="Data fine validita" type="date" name="datFineVal" value="<%=datFineVal%>" validateOnPost="true" 
		              required="false" readonly="<%=String.valueOf(readOnlyStr)%>" size="11" maxlength="10"/>
		     </td> 
	    </tr>
    </table>
    
	<table class="main" width="100%">
	  <tr><td>&nbsp;</td>
	  </tr>
	  <tr>
	   
	   <td></td>  
	  <tr>
	    <td colspan="2" align="center">
	      <%if(nuovo) {%> 
		      <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
		      <input type="reset" class="pulsanti" value="Annulla" onClick=""/>
		 </td>
	   </tr>
	   <tr>
		  <td>
		      <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer()">
		  </td>
	   </tr>
	   <%}else{
	       if(!readOnlyStr) {%> 
	     <tr>
		  	<td>
			 	<input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update()">
				<input type="reset" class="pulsanti" value="Annulla" onClick=""/>
		     <%}%>
		   <tr>
		  	<td>   
		      <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer()">
		    </td>
	     </tr>
	      <%}%> 	      
	    </td>
	  </tr>
	</table>

    <%if(!nuovo) {%> 
    <table>
	  <tr>
	  	<td colspan="2" align="center">
	  		<% if (operatoreInfo!=null) operatoreInfo.showHTML(out); %>
	    </td>
	  </tr>    
    </table>
    <%}%>

  </div>
  <!-- Stondature ELEMENTO BOTTOM -->
  <%out.print(divStreamBottom);%>

</af:form>

<br/>
</body>
</html>