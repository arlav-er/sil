<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 com.engiweb.framework.tags.Util,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 java.math.*,
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.util.StyleUtils,
                 it.eng.sil.security.*,
                 java.net.URLEncoder"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
//lista dei CPI
SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_PUB_GET_CPI");
Vector cpi = cont.getAttributeAsVector("ROWS.ROW");
//n data chiamata per ogni cpi
cont = (SourceBean) serviceResponse.getAttribute("M_PUB_GrigliaDataChiamata");
Vector dataChiam = cont.getAttributeAsVector("ROWS.ROW");
int nc = cpi.size();
int nr = dataChiam.size();

SourceBean col 	= null;
SourceBean row 	= null;
int sumCol[] 	= new int[nc];
int sumRow 		= 0;
int i 			= 0;
int j 			= 0;

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String txt 			= "";
String txt2			= "";
String data			= "";
String codCpi 		= "";
//per la stampa del protocollo
String numProt 		= "";
String daRipulire  	= "";
String daRipulire2	= "";
String daRipulire3	= "";

Object num  = null;
String linkBase 	= "";
String link 		= "";
String linkPrint	="";
String hrefPrint	="";
// chiama la pagina che stampa in html i dettagli di una lista di aste
String urlReportBase = "AdapterHTTP?PAGE=SPAMPA_DETTAGLIO_ASTA_PAGE";

%>

<html>
<head>
  <title>Griglia Asta art.16 L.56/87</title>
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
		text-align: center;
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
		text-align: center;
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
<p class="titolo">Griglia Asta art.16 L.56/87</p>
<table class="main" cellspacing="0" cellpadding="0">
<tr>
	<td class="griglia_tit"> 
		<table  width=100% height="100%">
			<tr >
				<td class="griglia_tit_qual" >Cpi di inserimento</td>
			</tr>
			<tr >
				<td class="griglia_tit_cpi">Data chiamata</td>
			</tr>
		</table>
	</td>
	<%for(j=0; j<nc; j++) {%>
		<%
		//ottengo l'intestazione della griglia con i CPI
		row = (SourceBean) cpi.elementAt(j);
		txt = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONE");		
		//il SourceBean derivato può contenere alcuni valori(DE_CPI.STRDESCRIZIONE) separati da '_'
		//per una etichettatura standard dell'intestazione della griglia sostituisco '_' con ' '		
		for(int z = 0; z < txt.length(); z++){
			if (txt.charAt(z)=='_') {
				txt = txt.replace('_',' ');
			}		
		}		
		sumCol[j] = 0;
		%>
		<td class="griglia_qual" width="21%"><%=txt%></td>
	<%}%>
	<td width="5%">&nbsp;</td>
</tr>
<%for(i=0; i<nr; i ++) { %>
<tr>
<%
	sumRow = 0;
	row = (SourceBean) dataChiam.elementAt(i);
	data = StringUtils.getAttributeStrNotNull(row, "DATCHIAMATA");
	
%>
		<td class="griglia_cpi" align="left"><%=data%></td>
<%		
	for(j=0; j<nc; j++) {	
		col 	= (SourceBean) cpi.elementAt(j);
		txt 	= StringUtils.getAttributeStrNotNull(col, "STRDESCRIZIONE");
		txt2 	= StringUtils.getAttributeStrNotNull(col, "CODCPI");
		
		num 	= row.getAttribute("AS_"+txt2);
		sumRow += Integer.parseInt(num.toString());
		sumCol[j] += Integer.parseInt(num.toString());
		if(num.equals(new BigDecimal(0))) { 
			link = "&nbsp;"; linkPrint="&nbsp;";
		}
		else {
			//link = "<u>" + num + "</u>";
			link = linkBase + num;			
			hrefPrint =urlReportBase + "&CODCPI=" + txt2 + "&DATCHIAMATA=" + data ;
			linkPrint=getLinkPrint(hrefPrint);
			//setto i parametri nella request per recuperarli nella query per l'estrazione del numero di protocollo
			numProt += hrefPrint + URLEncoder.encode("|");
			daRipulire  = "AdapterHTTP?PAGE=SPAMPA_DETTAGLIO_ASTA_PAGE";
			daRipulire2 = "&CODCPI=";
			daRipulire3 = "&DATCHIAMATA=";	
			numProt = com.engiweb.framework.tags.Util.replace(numProt,daRipulire,"");			
			numProt = com.engiweb.framework.tags.Util.replace(numProt,daRipulire2,"");
			numProt = com.engiweb.framework.tags.Util.replace(numProt,daRipulire3,",");	
		}
%>
			<td class="griglia_fascia"><table cellspacing="0" cellpadding="0" border="0" align="center"><tr><td><%=link%></td><td>&nbsp;</td><td><%=linkPrint%></td></table></td>
<%		
	}				



	if(sumRow>0) { 
		link = linkBase + sumRow; 
		hrefPrint = urlReportBase + "&DATCHIAMATA=" + data + "&NUMPROT=" + numProt;
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
	int totaleRichieste = 0;
	for(j=0; j<nc; j++){		
		row = (SourceBean) cpi.elementAt(j);
		//txt = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONE");
		txt2 	= StringUtils.getAttributeStrNotNull(row, "CODCPI");
		if(sumCol[j]>0) { 
			link = linkBase + sumCol[j]; 
			totaleRichieste+=sumCol[j];
			hrefPrint = urlReportBase + "&CODCPI=" + txt2 + "&NUMPROT=" + numProt;
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
	if (totaleRichieste>0){
		link = linkBase + totaleRichieste; 
		hrefPrint = urlReportBase + "&NUMPROT=" + numProt;
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