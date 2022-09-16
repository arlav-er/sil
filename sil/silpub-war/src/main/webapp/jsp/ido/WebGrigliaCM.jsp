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
boolean isSimplifiedVersion = false;
try {
	isSimplifiedVersion = ((SourceBean)serviceResponse.getAttribute("M_GETFLAGCMPUB"))
			.getAttribute("ROWS.ROW.FLAGCMPUB")
			.toString().equalsIgnoreCase("S");
} catch (Exception e) { /* nothing to do */ }
//lista dei CPI
SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_PUB_GET_CPI");
Vector cpi = cont.getAttributeAsVector("ROWS.ROW");
//n data chiamata per ogni cpi
cont = (SourceBean) serviceResponse.getAttribute("M_PUB_GrigliaDataChiamataCM");
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
BigDecimal numRich  = new BigDecimal(0);
//per la stampa del protocollo
String numProt 		= "";
String daRipulire  	= "";
String daRipulire2	= "";
String daRipulire3	= "";

String linkBase 	= "";
String link 		= "";
String linkPrint	="";
String hrefPrint	="";

Object num 		= null;

// chiama la pagina che stampa in html i dettagli di una lista di aste
String urlReportBase = "AdapterHTTP?PAGE=SPAMPA_DETTAGLIO_ASTACM_PAGE";


//-------------------------------------------------------------------------------------------

for(i=0; i<nr; i ++) { 


	sumRow = 0;
	row = (SourceBean) dataChiam.elementAt(i);
	data = StringUtils.getAttributeStrNotNull(row, "DATCHIAMATACM");
	
	
		
	for(j=0; j<nc; j++) {	
		col 	= (SourceBean) cpi.elementAt(j);
		txt 	= StringUtils.getAttributeStrNotNull(col, "STRDESCRIZIONE");
		txt2 	= StringUtils.getAttributeStrNotNull(col, "CODCPI");
		
		num 	= row.getAttribute("CM_"+txt2);
		sumRow += Integer.parseInt(num.toString());
		sumCol[j] += Integer.parseInt(num.toString());
		if(num.equals(new BigDecimal(0))) { 
			link = "&nbsp;"; linkPrint="&nbsp;";
		}
		else {
			link = linkBase + num;			
			hrefPrint =urlReportBase + "&CODCPI=" + txt2 + "&DATCHIAMATACM=" + data ;
			//linkPrint=getLinkPrint(hrefPrint);
			//setto i parametri nella request per recuperarli nella query per l'estrazione del numero di protocollo
			numProt += hrefPrint + URLEncoder.encode("|");
			daRipulire  = "AdapterHTTP?PAGE=SPAMPA_DETTAGLIO_ASTACM_PAGE";
			daRipulire2 = "&CODCPI=";
			daRipulire3 = "&DATCHIAMATACM=";	
			numProt = com.engiweb.framework.tags.Util.replace(numProt,daRipulire,"");			
			numProt = com.engiweb.framework.tags.Util.replace(numProt,daRipulire2,"");
			numProt = com.engiweb.framework.tags.Util.replace(numProt,daRipulire3,",");	
		}		
	}
	if(sumRow>0) { 
		link = linkBase + sumRow; 
		hrefPrint = urlReportBase + "&DATCHIAMATACM=" + data + "&NUMPROT=" + numProt;
		//linkPrint = getLinkPrint(hrefPrint);
	}
	else { 
		link = "&nbsp;"; 
		linkPrint="&nbsp;";
	}
}

	int totaleRichieste = 0;
	for(j=0; j<nc; j++){		
		row = (SourceBean) cpi.elementAt(j);
		//txt = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONE");
		txt2 	= StringUtils.getAttributeStrNotNull(row, "CODCPI");
		if(sumCol[j]>0) { 
			link = linkBase + sumCol[j]; 
			totaleRichieste+=sumCol[j];
			hrefPrint = urlReportBase + "&CODCPI=" + txt2 + "&NUMPROT=" + numProt;
			//linkPrint = getLinkPrint(hrefPrint);
		}
		else { 
			link = "&nbsp;"; 
			linkPrint = "&nbsp;"; 
		}
	} 	
	if (totaleRichieste>0){
		link = linkBase + totaleRichieste; 
		hrefPrint = urlReportBase + "&NUMPROT=" + numProt;
		//linkPrint = getLinkPrint(hrefPrint);
	}
	else { 
			link = "&nbsp;"; 
			linkPrint = "&nbsp;"; 
	}

String prova = hrefPrint;

/* old
		for(i=0; i<nr; i ++) { 
			linkPrint = "";
			link = "";
									 
			row = (SourceBean) dataChiam.elementAt(i);
			data = StringUtils.getAttributeStrNotNull(row, "DATCHIAMATACM");
			numRich = (BigDecimal)row.getAttribute("NUMRICH");				
			  
			if(numRich.equals(new BigDecimal(0))) { 
				link = "&nbsp;"; 
				linkPrint="&nbsp;";
			}
			else {	
				int num = numRich.intValue();
				sumRow = sumRow + num;
			
				link = linkBase + num;			
				hrefPrint =urlReportBase + "&DATCHIAMATACM=" + data ;
				//linkPrint=getLinkPrint(hrefPrint);			
			}	
		}
		
		if(sumRow>0) { 
			link = linkBase + sumRow; 
			hrefPrint = urlReportBase + "&DATCHIAMATACM=" + data + "&NUMPROT=" + numProt;
			//linkPrint = getLinkPrint(hrefPrint);
		}
		else { 
			link = "&nbsp;"; 
			linkPrint="&nbsp;";
		}
*/		
				
//-------------------------------------------------------------------------------------------




%>	
<html>
<head>
  <title>Griglia Asta CM</title>
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
		border-bottom:1px;
		border-left:1px;
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
  
  <script language="Javascript">

      function stampaLista(){ 
		 var f;
	     var t = "_blank";
	     var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=900,height=500,top=100,left=100";
 		 //f = '<%--=urlReportBase--%>';
 		 f = '<%=hrefPrint%>';
		 window.open(f, t, feat);
      }
      
  </script>
  
</head>
<body class="gestione">
<br/>




<%-- --%>
<%--
<%out.print(htmlStreamTop);%>
<p class="titolo">Griglia Asta CM</p>
<table cellpadding="0" cellspacing="0" align="left" width="100%" >
<tr>
	<td>
		<table cellpadding="0" cellspacing="0" align="left" width="50%">
		<tr>
			<td class="griglia_tit">Data Chiamata</td>
			<td class="griglia_tit_cpi">Num. Richieste</td>
		<tr>

		<%for(i=0; i<nr; i ++) { 
			linkPrint = "";
			link = "";
									 
			row = (SourceBean) dataChiam.elementAt(i);
			data = StringUtils.getAttributeStrNotNull(row, "DATCHIAMATACM");
			numRich = (BigDecimal)row.getAttribute("NUMRICH");				
			  
			if(numRich.equals(new BigDecimal(0))) { 
				link = "&nbsp;"; 
				linkPrint="&nbsp;";
			}
			else {	
				int num = numRich.intValue();
				sumRow = sumRow + num;
			
				link = linkBase + num;			
				hrefPrint =urlReportBase + "&DATCHIAMATACM=" + data ;
				linkPrint=getLinkPrint(hrefPrint);			
			}
		%>
			<tr>
				<td class="griglia_cpi" align="left"><%=data%></td>
				<td class="griglia_fascia">
					<table cellspacing="0" cellpadding="0" border="0" align="center">
						<tr>
							<td><%=link%></td>
							<td>&nbsp;</td>
							<td><%=linkPrint%></td>
						</tr>
					</table>
				</td>
			</tr>
		<%}%>
	<%	
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

		<tr>
			<td>&nbsp;</td>
			<td class="griglia_sum">
				<table>
					<tr>
						<td><%=sumRow%></td>
						<td>&nbsp;</td>
						<td><%=getLinkPrint(urlReportBase)%></td>
					</tr>
				</table>
			</td>				
		</tr>   
		<tr>
			<td colspan="2">&nbsp;</td>
		<tr>
		</table>
	</td>
</tr>
<tr>
	<td>&nbsp;</td>
</tr>
<tr>
	<td>&nbsp;</td>
</tr>
<tr width="100%">  
	<td  width="100%">
		<table width="100%">
			<tr>
				<td>&nbsp;</td>
				<td align="center" width="100%"><a href="../../index.html">Home Sezione Pubblica del SIL</a></td>
				<td>&nbsp;</td>
			</tr>	
		</table>
	</td>
</tr>
<br>
</table>
<%out.print(htmlStreamBottom);%>
--%>
<%-- --%>





<% if (isSimplifiedVersion) { %> 
	<af:list moduleName="M_PubAstaCM_simplified" />
<% } else { %>
	<af:list moduleName="M_PubAstaCM" />
<% } %> 

<table width="100%">
	<tr>
		<td align="center" width="100%">
			<% String stampaLabel = "Stampa"; if (isSimplifiedVersion) { stampaLabel = "Vedi e stampa dettagli"; } %> 
			<input class="pulsanti" type="button" name="stampa" value="<%=stampaLabel%>" onClick="stampaLista();"/>
		</td>
	</tr>
	<tr>
		<td align="center" width="100%">&nbsp;</td>
	</tr>	
	<tr>
		<td align="center" width="100%">
			<a href="../../index.html">Home Sezione Pubblica del SIL</a>
		</td>
	</tr>	
</table>
<br>
<%@ include file="/jsp/MIT.inc" %>

</body>
</html>
<%!
	private String getLinkPrint(String url) {
		return "<a href='"+url+"' target='_blank'><img src='../../img/stampa.gif' alt='Stampa lista'/></a>";
	}
%>