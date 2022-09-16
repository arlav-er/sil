<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>


<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 java.math.*,
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.util.StyleUtils,
                 it.eng.sil.security.*"%>
            

<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
String prgAzienda="";
String strAzienda="";
String strMansione = "";
String strContLavoro = "";
String strLuogo = "";
String strCaratteristiche = "";
String strContratto = "";
String strcandidarsi = "";
String descrizione="";
String fax="";
String mail="";
String numeroRichiesta="";
String codProvincia="";
String dataScadenza="";
String descProvincia="";
String numRichiesta="";
String indInternet="";
String codEvasione="";
String strFormazione="";
String strConoscenzePubb="";
String strNoteOrario="";

String strCount="";
int count=0;

int numero=1;

String numSecondi="";


SourceBean rowNum = (SourceBean) serviceResponse.getAttribute("M_GetNumSecondi.ROWS.ROW");
  if (rowNum != null) {
  		numSecondi = rowNum.containsAttribute("num")? rowNum.getAttribute("num").toString():"";
  }

SourceBean rowInd = (SourceBean) serviceResponse.getAttribute("M_GetIndInternet.ROWS.ROW");
  if (rowInd != null) {
  		indInternet = rowInd.containsAttribute("indirizzo")? rowInd.getAttribute("indirizzo").toString():"";
  }  
 

Vector vecPubb = null;
SourceBean row = (SourceBean) serviceResponse.getAttribute("M_GetRichiestaPubb.ROW");	
	if(row!=null){	
		strCount = (String) serviceResponse.getAttribute("M_GetRichiestaPubb.count");
		if(strCount== null) strCount="";
		else {
			Integer calcola = new Integer(strCount);
			count = calcola.intValue()+1;
		}
		strAzienda=(String) row.getAttribute("STRDATIAZIENDAPUBB");
			if (strAzienda == null) strAzienda="";
		strMansione=(String) row.getAttribute("STRMANSIONEPUBB");
			if(strMansione==null) strMansione=""; 
		strContLavoro=(String) row.getAttribute("TXTFIGURAPROFESSIONALE");
			if(strContLavoro==null) strContLavoro=""; 
		strLuogo=(String) row.getAttribute("STRLUOGOLAVORO");
			if(strLuogo==null) strLuogo=""; 
		strFormazione=(String) row.getAttribute("STRFORMAZIONEPUBB");
			if(strFormazione==null) strFormazione=""; 
		strContratto=(String) row.getAttribute("TXTCONDCONTRATTUALE");
			if(strContratto==null) strContratto="";
		strCaratteristiche=(String) row.getAttribute("TXTCARATTERISTFIGPROF");
			if(strCaratteristiche==null) strCaratteristiche="";  
		strConoscenzePubb=(String) row.getAttribute("STRCONOSCENZEPUBB");
			if(strConoscenzePubb==null) strConoscenzePubb=""; 
		strNoteOrario=(String) row.getAttribute("STRNOTEORARIOPUBB");
			if(strNoteOrario==null) strNoteOrario="";
		strcandidarsi=(String) row.getAttribute("STRRIFCANDIDATURAPUBB");
			if(strcandidarsi==null) strcandidarsi="";
		numeroRichiesta=(String) row.getAttribute("NUMRICH");
			if(numeroRichiesta==null) numeroRichiesta="";
		descrizione=(String) row.getAttribute("DESCRIZIONE");
			if(descrizione==null) descrizione="";
		fax=(String) row.getAttribute("FAX");
			if(fax==null) fax="";
		mail=(String) row.getAttribute("MAIL");
			if(mail==null) mail="";  
		codProvincia=(String) row.getAttribute("CODPROVINCIA");
			if(codProvincia==null) codProvincia="";  
		dataScadenza=(String) row.getAttribute("DATSCADENZA");
			if(dataScadenza==null) dataScadenza=""; 
		descProvincia=(String) row.getAttribute("STRDESCPROVINCIA");
			if(descProvincia==null) descProvincia="";
		numRichiesta=(String) row.getAttribute("NUMRICHIESTA");
			if(numRichiesta==null) numRichiesta="";
		codEvasione=(String) row.getAttribute("CODEVASIONE");
			if(codEvasione==null) codEvasione="";
		
	}
Calendar oggi = Calendar.getInstance();
String giornoDB = Integer.toString(oggi.get(5));
String meseDB = Integer.toString(oggi.get(2)+1);
String annoDB = Integer.toString(oggi.get(1));
String dataOdierna = giornoDB + "/" + meseDB + "/" + annoDB;  
%>

<html>
<head>
<script language="Javascript">

function ApriPopUp(){
	var url= '<%=indInternet%>';
	var w=1000; var l=((screen.availWidth)-w)/2;
  	var h=750; var t=((screen.availHeight)-h)/2;
  	var feat = "toolbar=0, scrollbars=1,height="+h+",width="+w+",top="+t+",left="+l;
	var opened=window.open(url, "Attività", feat);
	opened.focus();
}

</script>
<style>

body {
	margin: 0;
	background-color: #f0faff;
}

</style>
</head>

<body>
<br/>

<af:showErrors />
<af:form name="frm1" action="AdapterHTTP" method="POST">
<% if(row!=null){ %>
<table border=0>
	<tr>
		<td width=430 valign=top>
		<br><br>
		<p align="left" style="font-family: Times New Roman; font-size: 12pt; color: black;">
			<font size=3><b>CENTRO PER L'IMPIEGO DI <%=descrizione%></b></font><br>
		</p>
		</td>
		<td align="center" style="font-family: Times New Roman; font-size: 12pt; color: gray;">
		<br><br>
			<img src="../../img/loghi/<%=codProvincia%>_provHome.gif" alt="Provincia di <%=descProvincia%>" border="0">
			<br><font size=4><b>PROVINCIA<br>DI<br> <%=descProvincia%></b></font>
			</td>
		<td width=50></td>
		<td valign=top>	
		<br>
		<p align="left" style="font-family: Times New Roman; font-size: 12pt; color: black;">
			Presentarsi all'ufficio o inviare il curriculum vitae indicando il codice di riferimento 
			dell'offerta di lavoro <b><%=numeroRichiesta%></b>, via fax al numero <b><%=fax%></b>
			o via posta elettronica a <a href="mailto:<%=mail%>"><b><%=mail%></b></a>
			o via Internet: <a href="javascript:ApriPopUp()"><b><%=indInternet%></b></a>  
			 
		</p>
		</td>
	</tr>
</table>
<table border=0>
	<tr>
		<td></td>
		<td width=1000 style="font-family: Times New Roman; font-size: 12pt; color: red;">
		<p align="center"><font size=3>PUBBLICAZIONI DEL <%=dataOdierna%></font></p>
		</td></tr>
</table>

<table width="100%">
<tr><td colspan='2'align='left' width="100%"> 
 <b>Cod. <%=numeroRichiesta%> valida fino al <%=dataScadenza%> </b>
<br><br>
</td></tr>
<%if(!codEvasione.equals("DFA") && !codEvasione.equals("DRA")) {%>
<tr valign=top>
	<td width=250> 
		<font size=4><i>Azienda richiedente</i></font>
			<font size=4></font></td>
	<td align=left>
		<font size=3><%=strAzienda%></font>
	</td>
</tr>
<%}%>
<tr valign=top>
	<td width=250><font size=4><i>Mansione</i></font>
		<font size=4></font></td>
	<td align=left>
		<font size=3><%=strMansione%>
		</font>
	</td>
</tr>
<tr valign=top>
	<td width=250><font size=4><i>Contenuti e contesto del lavoro</i></font>
		<font size=4></font></td>
	<td align=left>
		<font size=3><%=strContLavoro%>
		</font>
	</td>
</tr>
<tr valign=top>
	<td width=250><font size=4><i>Luogo di lavoro</i></font>
		<font size=4></font></td>
	<td align=left>
		<font size=3><%=strLuogo%>
		</font>
	</td>
</tr>
<tr valign=top>
	<td width=250><font size=4><i>Formazione</i></font>
		<font size=4></font></td>
	<td align=left>
		<font size=3><%=strFormazione%>
		</font>
	</td>
</tr>
<tr valign=top>
	<td width=250><font size=4><i>Caratteristiche del candidato</i></font>
		<font size=4></font></td>
	<td align=left>
		<font size=3><%=strCaratteristiche%>
		</font>
	</td>
</tr>
<tr valign=top>
	<td width=250><font size=4><i>Contratto</i></font>
		<font size=4></font></td>
	<td align=left>
		<font size=3><%=strContratto%>
		</font>
	</td>
</tr>
<tr valign=top>
	<td width=250><font size=4><i>Conoscenze</i></font>
		<font size=4></font></td>
	<td align=left>
		<font size=3><%=strConoscenzePubb%>
		</font>
	</td>
</tr>
<tr valign=top>
	<td width=250><font size=4><i>Orario</i></font>
		<font size=4></font></td>
	<td align=left>
		<font size=3><%=strNoteOrario%>
		</font>
	</td>
</tr>
<tr valign=top>
	<td width=250><font size=4><i>Per candidarsi</i></font>
		<font size=4></font></td>
	<td align=left>
		<font size=3><%=strcandidarsi%>
		</font>
	</td>
</tr>

<META HTTP-EQUIV="Refresh" CONTENT="<%=numSecondi%>;URL=AdapterHTTP?PAGE=WebCiclaPubblicazioniPage&count=<%=count%>">  

</table>
<%}%>
</af:form>
<%@ include file="/jsp/MIT.inc" %>
</body>
</html>
