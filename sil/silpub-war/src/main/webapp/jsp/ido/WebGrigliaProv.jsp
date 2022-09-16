<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 java.math.*,
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.util.StyleUtils,
                 it.eng.sil.security.*"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_GETIDOTIPIQUALIFICAPUB");
Vector clmQual = cont.getAttributeAsVector("ROWS.ROW");
cont = (SourceBean) serviceResponse.getAttribute("M_GRIGLIAPROV");
Vector rowsG = cont.getAttributeAsVector("ROWS.ROW");
int nr = rowsG.size();
int nc = clmQual.size();
SourceBean col = null;
SourceBean row = null;
int sumCol[] = new int[nc];
int sumRow = 0;
int i = 0;
int j = 0;

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String txt = "";
String codCpi = "";
BigDecimal num = null;
String linkBase = "<a href=\"AdapterHTTP?PAGE=WebListaPubbPage&PROV=G";
String link = "";
String linkPrint="";
String hrefPrint="";
// chiama la pagina che stampa in html i dettagli di una lista di offerte
String urlReportBase = "AdapterHTTP?PAGE=LISTADETTAGLIOQUADSPAMPAPAGE&PROV=G";

%>

<html>
<head>
  <title>Griglia Provinciale</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <style>
  <%-- stile per la cella del titolo congiunto cpi/qualifica --%>
	  td.griglia_tit_qual {
		border-bottom: 1px; 
		border-left: 0px; 
		border-top:0px;
		border-right: 0;
		border-style: solid; 
		border-color: #3366cc;
		color: #000080; 
		background-color: #e8f3ff;
		font-family: Verdana, Arial, Helvetica, Sans-serif;  
		font-size: 11px;
		font-weight: normal;
		text-align: right;
		padding-left: 1px;
		padding-right: 1px;
	}
	td.griglia_tit_cpi { 
		border-right: 1px; 
		border-top:1px;
		border-bottom:0px;
		border-left:0px;
		border-style: solid; 
		border-color: #3366cc;
		background-color: #e8f3ff;
		color: #000080;
		font-family: Verdana, Arial, Helvetica, Sans-serif; 
		font-size: 11px;
		font-weight: normal;
		padding: 2px;
		margin: 0px;
		text-align: left;
	}
	td.griglia_tit { 
		border-right: 0px; 
		border-top:1px;
		border-bottom:0px;
		border-left:1px;
		border-style: solid; 
		border-color: #3366cc;		
	}
  </style>
</head>
<body class="gestione">
<br/>
<%out.print(htmlStreamTop);%>
<p class="titolo">Offerte di Lavoro</p>
<table class="main" cellspacing="0" cellpadding="0">
<tr>
	<td class="griglia_tit"> 
		<table width=100% height="100%" >
			<tr >
				<td class="griglia_tit_qual" >Categoria</td>
			</tr>
			<tr >
				<td class="griglia_tit_cpi">Cpi di inserimento</td>
			</tr>
		</table>
	</td>
	<%for(j=0; j<nc; j++) {%>
		<%
		row = (SourceBean) clmQual.elementAt(j);
		txt = StringUtils.getAttributeStrNotNull(row, "DESCRIZIONE");
		sumCol[j] = 0;
		%>
		<td class="griglia_qual" width="12%"><%=txt%></td>
	<%}%>
	<td width="5%">&nbsp;</td>
</tr>
<%for(i=0; i<nr; i ++) { %>
	<tr>
<%
	sumRow = 0;
	row = (SourceBean) rowsG.elementAt(i);
	txt = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONE");
	codCpi = StringUtils.getAttributeStrNotNull(row, "CODCPI");
%>
		<td class="griglia_cpi" align="left"><%=txt%></td>
<%		
	for(j=0; j<nc; j++) {	
		col = (SourceBean) clmQual.elementAt(j);
		txt = StringUtils.getAttributeStrNotNull(col, "CODICE");
		num = (BigDecimal) row.getAttribute(txt);
		sumRow += Integer.parseInt(num.toString());
		sumCol[j] += Integer.parseInt(num.toString());
		if(num.equals(new BigDecimal(0))) { 
			link = "&nbsp;"; linkPrint="&nbsp;";
		}
		else {
			//link = "<u>" + num + "</u>";
			link = linkBase + "&codQualifica=" + txt;
			link += "&CODCPI=" + codCpi + "\">" + num  + "</a>";
			hrefPrint =urlReportBase +"&codQualifica=" + txt + "&CODCPI=" + codCpi;
			linkPrint=getLinkPrint(hrefPrint);
		}
%>
			<td class="griglia_fascia"><table cellspacing="0" cellpadding="0" border="0" align="center"><tr><td><%=link%></td><td>&nbsp;</td><td><%=linkPrint%></td></table></td>
<%		
	}				
	if(sumRow>0) { 
		link = linkBase + "&CODCPI=" + codCpi + "\">" + sumRow  + "</a>"; 
		hrefPrint = urlReportBase + "&CODCPI=" + codCpi;
		linkPrint = getLinkPrint(hrefPrint);
	}
	else { 
		link = "&nbsp;"; 
		linkPrint="&nbsp;";
	}
%>
		<td class="griglia_sum">
			<table cellspacing="0" cellpadding="0" border="0" align="center">
				<tr>
					<td><%=link%></td><td>&nbsp;<td><%=linkPrint%></td>
			</table>
		</td>
	</tr>
<%}%>
<tr>
	<td>&nbsp;</td>
<%
	int totaleOfferte = 0;
	for(j=0; j<nc; j++){		
		row = (SourceBean) clmQual.elementAt(j);
		txt = StringUtils.getAttributeStrNotNull(row, "CODICE");
		if(sumCol[j]>0) { 
			link = linkBase + "&codQualifica=" + txt + "\">" + sumCol[j]  + "</a>"; 
			totaleOfferte+=sumCol[j];
			hrefPrint = urlReportBase + "&codQualifica=" + txt;
			linkPrint = getLinkPrint(hrefPrint);
		}
		else { 
			link = "&nbsp;"; 
			linkPrint = "&nbsp;"; 
		}
%>
		<td class="griglia_sum">
			<table cellspacing="0" cellpadding="0" border="0" align="center">
				<tr>
					<td><%=link%></td><td>&nbsp;<td><%=linkPrint%></td>
			</table>
		</td>
<%  } %>
<%		
	if (totaleOfferte>0){
		link = linkBase + "\">" + totaleOfferte + "</a>"; 
		hrefPrint = urlReportBase;
		linkPrint = getLinkPrint(hrefPrint);
	}
	else { 
			link = "&nbsp;"; 
			linkPrint = "&nbsp;"; 
		}
	
%>
	<td class="griglia_sum">
		<table cellspacing="0" cellpadding="0" border="0" align="center">
			<tr>
				<td><%=link%></td><td>&nbsp;<td><%=linkPrint%></td>
		</table>
	</td>
</tr>
</table>
<br/>&nbsp;
<P align="center"><a href="../../index.html">Home Sezione Pubblica del SIL</a></P>
<br/>&nbsp;
<%out.print(htmlStreamBottom);%>
<%@ include file="/jsp/MIT.inc" %>
</body>
</html>
<%!
	private String getLinkPrint(String url) {
		return "<a href='"+url+"' target='_blank'><img src='../../img/stampa.gif' alt='Stampa lista'/></a>";
	}
%>