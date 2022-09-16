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

	String DATDICHIARAZIONE = "";
	String NUMPERSONE = "";

    String     dtmIns             = null; 
    String     dtmMod             = null;    
    BigDecimal cdnUtIns           = null; 
    BigDecimal cdnUtMod           = null;
    String     prgLavCarico      = null;
     
    InfCorrentiLav testata= null;
    Linguette l  = null;
    Testata operatoreInfo = null;

    prgLavCarico = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PRGLAVCARICO");

    Vector rowsLav = serviceResponse.getAttributeAsVector("M_Load_Lav_Carico.ROWS.ROW");
	boolean isInsert = false;
    if (rowsLav.size()>=1) {
		isInsert = true;
    }

    Vector rows;
    if(!prgLavCarico.equals("")){ //Subito dopo l'inserimento
	    rows = serviceResponse.getAttributeAsVector("M_Load_Carico.ROWS.ROW");
	}else{
		rows = serviceResponse.getAttributeAsVector("M_Load_Lav_Carico.ROWS.ROW");
	}
	
	SourceBean row = null;  
	if (rows.size()==1) {
	    row = (SourceBean)rows.get(0);
			
		prgLavCarico = Utils.notNull(row.getAttribute("PRGLAVCARICO"));
		DATDICHIARAZIONE = Utils.notNull(row.getAttribute("DATDICHIARAZIONE"));
		NUMPERSONE = Utils.notNull(row.getAttribute("NUMPERSONE"));
	        
        cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
        dtmIns = (String) row.getAttribute("DTMINS");

	    cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");		    	    
        dtmMod = (String) row.getAttribute("DTMMOD");

        operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	}		


    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));   

    String htmlStreamTop = StyleUtils.roundTopTable(canInsert);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canInsert);





    testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore,user);
    int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
    l  = new Linguette(user,  _cdnFunz, _page, new BigDecimal(cdnLavoratore));
    operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

	//rimosso controllo pre tutti i lavoratori è possibile inserire il carico familiare
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
    
%>
<html>
<head>
<title>&nbsp;</title>

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

    function isNumeric(val){
  	    return (parseFloat(val,10)==(val*1));
    }
	
	function controllaDati(){		
		var numPers = document.Frm1.NUMPERSONE.value;
		var datDich = document.Frm1.DATDICHIARAZIONE.value;
		var isNum = false;
		
		if(isNumeric(numPers)) isNum = true;
		
		if(isNum){
			return true;
		}else{
			alert("Il campo Numero persone a carico deve avere valore numerico!");
			return false;
		}
	}

    window.top.menu.caricaMenuLav( <%=_funzione%>, <%=cdnLavoratore%>);

</script>

</head>


<body class="gestione" onload="rinfresca()">

<%
   testata.show(out);
   l.show(out);

   boolean canModify = !readOnlyStr;
%>   

<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaDati()" >

    <input type="hidden" name="PAGE" value="CMCaricoPage">
    <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
    <input type="hidden" name="PRGLAVCARICO" value="<%=prgLavCarico%>"/>
    
    <p align="center">
      <center>
        <font color="green">
          <af:showMessages prefix="M_inserisci_carico"/>
          <af:showMessages prefix="M_Save_Carico"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
      
      
      <%=htmlStreamTop%>
 			
	  <table class="main" border="0">
	    	<tr>
	    		<td colspan="2">&nbsp;</td>
	    	</tr>
	    	<tr>
		        <td class="etichetta">Data dichiarazione</td>
			    <td class="campo">
			      <af:textBox title="Data dichiarazione" validateOnPost="true" disabled="false" type="date" name="DATDICHIARAZIONE" value="<%=DATDICHIARAZIONE%>" size="12" maxlength="12" readonly="<%=String.valueOf(readOnlyStr)%>" classNameBase="input" />&nbsp;*&nbsp;
			      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('DATDICHIARAZIONE')"; </SCRIPT>
			    </td>	
	   		</tr> 
	    	<tr>
		        <td class="etichetta">Numero persone a carico</td>
			    <td class="campo">
			      <af:textBox title="Numero persone a carico" value="<%=NUMPERSONE%>" classNameBase="input" name="NUMPERSONE" maxlength="9" size="12" readonly="<%=String.valueOf(readOnlyStr)%>" />&nbsp;*&nbsp;
			      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('NUMPERSONE')"; </SCRIPT>
			    </td>	
    		</tr>   
	    	<tr>
	    		<td colspan="2">&nbsp;</td>
	    	</tr>
	    	<tr>
	    		<td colspan="2">
					<center>
						<%if(!isInsert) {
							  if(canInsert) {%>
						    	<input class="pulsanti" type="submit" name="inserisci" value="Inserisci" >  
							<%}
						  }else{
							  if(canModify) {%>
						    	<input class="pulsanti" type="submit" name="aggiorna" value="Aggiorna" >  
							<%}
						  }%>						
					</center>
	    		</td>
	    	</tr>	    	
	    	<tr>
	    		<td colspan="2">&nbsp;</td>
	    	</tr>
	  </table>

      <%=htmlStreamBottom%>
      	      
      <%if(!isInsert){
	        if(numero < 1){%>	
			  <center>
			    <table>
			   	  <tr>
			   		<td align="center">
			   			Non è possibile inserire il numero di persone a carico.
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
	      <%}
        }%>

	  <%if(isInsert){%>    
	    <center>
	  	 	<table>
	      		<tr>
	      			<td align="center">
	      				<% operatoreInfo.showHTML(out); %>
	      			</td>
	      		</tr>
	      	</table>
	    </center>
	  <%}%>	              	      
    <p/>

</af:form>

<br/>
</body>
</html>