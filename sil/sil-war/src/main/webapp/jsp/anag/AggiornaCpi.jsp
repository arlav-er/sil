<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../anag/Function_CommonRicercaCPI.inc" %>


<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.tracing.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String codMonoTipoCpi = "";
  String codCpiTit="";
  String strCpiTit="";
  BigDecimal prgLavStoriaInf = null;
  String codCpiOrig="";
  String strCpiOrig="";
  
String response_xml="M_GETLAVORATOREINDIRIZZI.ROW";
SourceBean row=(SourceBean) serviceResponse.getAttribute(response_xml);
if (row.containsAttribute("cdnUtins")) {cdnUtins=row.getAttribute("cdnUtins").toString();}
if (row.containsAttribute("dtmins")) {dtmins=row.getAttribute("dtmins").toString();}
if (row.containsAttribute("cdnUtmod")) {cdnUtmod=row.getAttribute("cdnUtmod").toString();}
if (row.containsAttribute("dtmmod")) {dtmmod=row.getAttribute("dtmmod").toString();}
if (row.containsAttribute("CODMONOTIPOCPI")) {codMonoTipoCpi = (String)row.getAttribute("CODMONOTIPOCPI");}  
if (row.containsAttribute("CODCPITIT")) {codCpiTit=row.getAttribute("CODCPITIT").toString();}   
if (row.containsAttribute("STRDESCRIZIONE")) {strCpiTit=row.getAttribute("STRDESCRIZIONE").toString();}
if (row.containsAttribute("prgLavStoriaInf")) {prgLavStoriaInf = (BigDecimal)row.getAttribute("prgLAvStoriaInf");}
if (row.containsAttribute("CODCPIORIG")) {codCpiOrig=row.getAttribute("CODCPIORIG").toString();}   
if (row.containsAttribute("STRDESCRIZIONEORIG")) {strCpiOrig=row.getAttribute("STRDESCRIZIONEORIG").toString();}


Testata testata = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);

String _funzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");

String codCPI = "";
String codCPIHid = "";
String strCPI = "";
String strCPIHid = "";
String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);


%>

<HTML>
<HEAD>
<title>Aggiorna Cpi competente</title>
<SCRIPT language="javascript">
	var _funzione = '<%=_funzione%>';
	
	var imgChiusa = "../../img/chiuso.gif";
	var imgAperta = "../../img/aperto.gif";
</SCRIPT>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<!--script type="text/javascript" src="../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
    <script type="text/javascript" src="../js/movimenti/common/MovimentiRicercaSoggetto.js" language="JavaScript"></script-->
<af:linkScript path="../../js/"/>
	<SCRIPT language="javascript">
	
		function aggiorna(){
			
			if (isInSubmit()) return;
			if (document.Frm1.codCPI.value == null || document.Frm1.codCPI.value == ""){
					alert("Centro per l'impiego obbligatorio");
					return;
				}
			if(confirm("Attenzione: verrà modificato solo il CPI competente, senza nessun controllo sulla coerenza degli altri dati.\n Continuare?")){
				 
				var value = "aggiornaCpi=true&newCodCPI="+document.Frm1.codCPI.value+"&prgLavStoriaInf=<%=prgLavStoriaInf.toString()%>";
				window.opener.aggiornaCpiComp(value);
				window.close();
			}
		}
		
			
		 function fieldChanged() {} // lasciata per compatibilità
	</SCRIPT>

</HEAD>
<BODY>
	<af:form name="Frm1" action="AdapterHTTP" method="post" >
	<af:textBox name="prgLavStoriaInf" type="hidden" value="<%=prgLavStoriaInf.toString()%>"/>
		<br>
		<p class="titolo">Modifica CPI competente</p>
		<af:showMessages prefix="AggiornaCpi"/>
		<af:showErrors/>

		<table class="main" border="0">
			<TR>	
				<TD>
				<%out.print(htmlStreamTop);%>
					<TABLE width="100%">
						<tr><td>&nbsp;</td></tr>
						<tr><td>&nbsp;</td></tr>
						<TR>
							<td class="etichetta">Centro per l'impiego ai sensi del D. Lgs 150</td>
							<td class="campo">
						      <% if(codMonoTipoCpi.equals("C")){ %>
						        	<af:textBox classNameBase="input" type="text" name="codCPITit" 
						        	value="<%=codCpiTit%>" size="10" maxlength="9" readonly="true"/>&nbsp;	
					        		<af:textBox type="text" classNameBase="input" name="strCPITit" 
					        		value="<%=strCpiTit%>" size="30" maxlength="50" readonly="true" />&nbsp;
						      <%} else { 
						              if(codMonoTipoCpi.equals("T")){ %>
						              <af:textBox classNameBase="input" type="text" name="codCPIOrig" 
						        	  value="<%=codCpiOrig%>" size="10" maxlength="9" readonly="true"/>&nbsp;	
					        		  <af:textBox type="text" classNameBase="input" name="strCPIOrig" 
					        		  value="<%=strCpiOrig%>" size="30" maxlength="50" readonly="true" />&nbsp;  
						        	  <%}%>
						      <%}%>
            				</td>		
						</TR>
						<tr><td>&nbsp;</td></tr>
						<tr><td>&nbsp;</td></tr>
						<tr>
							  <td class="etichetta">Centro per l'impiego</td>
							  <td class="campo">
							      <af:textBox classNameBase="input" type="text" name="codCPI" value="<%=codCPI%>" 
							          onKeyUp="PulisciRicercaCPI(document.Frm1.codCPI, document.Frm1.codCPIHid, document.Frm1.strCPI, document.Frm1.strCPIHid, 'codice');" 
							          size="10" maxlength="9"
							      />&nbsp;
							      <A HREF="javascript:btFindCPI_onclick(  document.Frm1.codCPI, 
							                                              document.Frm1.codCPIHid, 
							                                              document.Frm1.strCPI, 
							                                              document.Frm1.strCPIHid, 
							                                              'codice');">
							          <IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
							      <af:textBox type="hidden" name="codCPIHid" value="<%=codCPIHid%>" />
							      <af:textBox type="text" classNameBase="input" name="strCPI" value="<%=strCPI%>"
							          onKeyUp="PulisciRicercaCPI(document.Frm1.codCPI, document.Frm1.codCPIHid, this, document.Frm1.strCPIHid, 'descrizione');" 
							          size="30" maxlength="50" 
							          inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\""
							      />&nbsp;
							      <A HREF="javascript:btFindCPI_onclick(  document.Frm1.codCPI, 
							                                              document.Frm1.codCPIHid, 
							                                              document.Frm1.strCPI, 
							                                              document.Frm1.strCPIHid, 
							                                              'descrizione');">
							          <IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
							      <af:textBox type="hidden" name="strCPIHid" value="<%=strCPIHid%>" />
						
							  </td>
							 </tr>
						
					</TABLE>
					<%out.print(htmlStreamBottom);%>
				</TD>
			</TR>	
			
		</table>
		
		
		<br>
		<br>
		
		<table class="main">
		  	<tr>
		  		<td>
		  			<input type="button" class="pulsanti" value="Chiudi" onClick="window.close();">
		  			<input type="button" class="pulsanti" value="Aggiorna" onclick="aggiorna();">

				</td>
			</tr>
		</table>

		
	</af:form>
	<% if(testata!=null) { %>
		<div align="center">
			<%testata.showHTML(out);%>
		</div>
	<%}%>

	
</BODY>
</HTML>
