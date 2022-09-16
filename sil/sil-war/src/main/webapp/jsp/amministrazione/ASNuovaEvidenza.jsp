<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>


<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 

String prgEvidenza 		= "";
String cdnLavoratore 	= StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
String prgTipoEvidenza 	= "";
String datDataScad 		= "";
String strEvidenza 		= "";
String numKloEvidenza 	= "";
String messPrevalEv		= StringUtils.getAttributeStrNotNull(serviceRequest, "TipoMessEv");


String btnSalva = "Inserisci";
String btnChiudi = "Chiudi senza inserire";
boolean canModify = true;

/*
int cdnGruppo=user.getCdnGruppo();
int cdnProfilo=user.getCdnProfilo();
*/

//formattazione pagina jsp
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<af:linkScript path="../../js/"/>
<title>Nuova Evidenza</title>
<script language="JavaScript" src="../../js/script_comuni.js"></script>
<script language="Javascript">
  
function fieldChanged() {
   	<%if (canModify) {out.print("flagChanged = true;");}%>
}  
  
       function checkDataScad()
    {
    	var dataIn = "<%=datDataScad%>";
    	var oggi = new Date();
    	var data = new Date();
    	var dataFrm = document.frmEv.DATDATASCAD.value;
    	
    	g = oggi.getDate();
    	m = oggi.getMonth();
    	a = oggi.getFullYear();
    	oggi = new Date(a, m, g);
    	
    	if(dataFrm != "") {
    		g = parseInt(dataFrm.substr(0,2),10);
    		m = parseInt(dataFrm.substr(3, 2),10)-1;
    		a = parseInt(dataFrm.substr(6,4),10);
  			data = new Date(a, m, g);
  		}
  		    
  		if(dataIn!=dataFrm) {
  			// se ho modificato una data questa non puo' essere inferiore a oggi
  			if(data < oggi) { 
  				// Come prolabor non devo poter inserire una data anteriore a oggi
  				alert("La data di scadenza non puo' essere anteriore al giorno attuale");
  				return(false);  			
  			} else { return(true); }
  		} else { return true; }
  		
    }

	function annulla(){			   
		document.frmEv.STREVIDENZA.value="";
		document.frmEv.DATDATASCAD.value="";
		document.frmEv.PRGTIPOEVIDENZA.value="";
	}   

</script>
</head> 

<body class="gestione">
<br>
<p class="titolo">Nuova Evidenza</p>
<p>
  <font color="green"><af:showMessages prefix="M_AS_INS_EVIDENZA" /></font>
  <font color="red"><af:showErrors /></font>
</p>
<%out.print(htmlStreamTop);%>
<af:form name="frmEv" action="AdapterHTTP" method="POST" onSubmit="checkDataScad()">
<input type="hidden" name="PAGE" value="AS_NuovaEvidenzaPage"/>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>

<table class="main">
	<tr>
	  <td class="etichetta">Data scadenza</td>
	  <td class="campo">
	      <af:textBox name="DATDATASCAD"
	      		  title="Data scadenza"
	      		  type="date"
	              size="11"
	              maxlength="10"
	              required="true"
	              validateOnPost="true"
	              onKeyUp="fieldChanged();"
	              classNameBase="input"
	              readonly="<%=String.valueOf(!canModify)%>"
	              value="<%=datDataScad%>"/>
	  </td>
	</tr>
	<tr>
	  <td class="etichetta">Tipo ev.</td>
	    <td class="campo">
	      <af:comboBox name="PRGTIPOEVIDENZA" size="1" title="Tipo evidenza"
	                     multiple="false" required="true"
	                     focusOn="false" moduleName="MTIPIEVIDENZE"
	                     addBlank="true" blankValue=""
	                     classNameBase="input"
	                     selectedValue=""
	                     disabled="<%= String.valueOf( !canModify ) %>"
	                     onChange="fieldChanged()"/>    
	    </td>
	</tr>
	<tr class="note">
	  <td class="etichetta">Messaggio</td>
	  <td class="campo">
	    <af:textArea name="STREVIDENZA" 
	                 cols="60" 
	                 rows="4" 
	                 required="true" 
	                 title="Messaggio evidenza"
	                 maxlength="3000"
	                 onKeyUp="fieldChanged();"
	                 classNameBase="input"
	                 readonly="<%=String.valueOf(!canModify)%>"
	                 value="<%=messPrevalEv%>"
	    />
	  </td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<%if(canModify) { %>
	  <tr>
	    <td colspan="2" align="center">
	    <input type="submit" class="pulsanti" name="SALVA" value="<%=btnSalva%>">  
	    &nbsp;&nbsp;
	    <input type="reset" class="pulsanti" name="reset" value="Annulla" onClick="annulla()">
	    </td>
	  </tr>
	  <tr><td colspan="2">&nbsp;</td></tr>
	<%}%>
	<tr>
	  	<td align="center" colspan="2">
			<%
				String urlDiLista = (String) sessionContainer.getAttribute("_TOKEN_ASLISTAAVVSELEZIONEPAGE");
				if (urlDiLista != null) {
					out.println("<div align=\"center\"><a href=\"#\" onClick=\"goTo('" + urlDiLista +"')\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></div>");
				}
			%>
		</td>
	</tr>
</table>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
