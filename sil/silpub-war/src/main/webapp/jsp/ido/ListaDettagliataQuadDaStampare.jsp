<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

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
String strRichiesta= null, strMansionePub=null,  codEv = null,  strAziendaRichiedente=null,figuraProfessionale=null,
luogoDiLavoro = null,formazione=null,caratteristicheCandidati = null, contratto = null,  conoscienze = null, 
orario = null, codMonoCMcatPubb = null , contatti = null, emailContatto=null, numAnno=null, numRichiesta=null,
descrizioneAzienda=null, strCollocamentoMirato=null;

StringBuffer strRifMansioni = null;
Vector vMansioniRic  = null; 

Vector elencoPubbl = serviceResponse.getAttributeAsVector("M_ELENCO_PUBBLICAZIONI.LISTA_PUBBLICAZIONI");
strCollocamentoMirato = StringUtils.getAttributeStrNotNull(serviceRequest,"FLAGCM");

boolean flagCM = strCollocamentoMirato.equals("true")?true:false;
String codProv = "";
String strIntestazioneProvinciaRiga = "";
int numConfig = 0;
SourceBean rowNum = (SourceBean) serviceResponse.getAttribute("M_GetConfigLoghiStampa.ROWS.ROW");
if (rowNum != null) {
	String valoreConfig = rowNum.containsAttribute("num")?rowNum.getAttribute("num").toString():"0";
	codProv = rowNum.containsAttribute("strcodrif")?rowNum.getAttribute("strcodrif").toString():"";
	numConfig = new Integer(valoreConfig).intValue();
	if (numConfig == 1) {
		strIntestazioneProvinciaRiga = rowNum.containsAttribute("strvalore")?rowNum.getAttribute("strvalore").toString():"";
	}
}
%>

<html>
<head>
	<% if (flagCM == true) { %>
		<title>RICHIESTE DI PERSONALE PUBBLICATE IN COLLOCAMENTO MIRATO</title>
	<% }else { %>
		<title>Pubblicazioni annunci di lavoro</title>
	<% } %>
	<STYLE type="text/css">
	* {
		font-family: Verdana, Arial, Helvetica, Sans-serif; 
		font-style: normal;
		font-size: 13px;
	}
	td.etichetta {
		vertical-align: top;
	}
	td.campo {
		vertical-align: top;
		font-weight: bold;
	}
	td.pro {font-size: 15px;text-align: center;}
	td.cpi {font-size: 16px;font-weight: bold;text-align: center;}
	td.via {font-size: 11px; font-weight: bold;text-align: center;}
	td.cont { font-size: 11px; font-weight: bold;text-align: center;}
	hr {
		border-bottom: 2px solid black;
		border-top: 0px;
	}	
	table.fontpage  { font-family: Verdana, Arial, Helvetica, Sans-serif;
				font-size: 14px;
	}
	</STYLE>
</head>
<body>

<% if (numConfig == 1) {%>
	<table width="100%" class="fontpage">
		<tr>
			<td align="left" colspan="1" width="50%">
				<img src="../../img/loghi/prov_<%=codProv%>.gif" height="70" >
			</td>
			<td align="right" colspan="1" width="50%">
				<img src="../../img/loghi/prov_<%=codProv%>_cpi.gif" height="70" >
			</td>
		<tr>
	</table>
<% } %>

<table width="100%">
<%
	
	String codCpi = "";
	for (int i = 0; i < elencoPubbl.size(); i++) {
		SourceBean row = (SourceBean)elencoPubbl.get(i);
		numRichiesta = row.getAttribute("NUMRICHIESTAORIG")!=null ?row.getAttribute("NUMRICHIESTAORIG").toString():"";
		if (numRichiesta == null || numRichiesta.equals("")) {
			numRichiesta = row.getAttribute("NUMRICHIESTA")!=null ?row.getAttribute("NUMRICHIESTA").toString():"";
		}
		if (row.getAttribute("numAnno") != null) {
			numAnno = row.getAttribute("numAnno").toString();
		}
		
		if (!((String)row.getAttribute("CODCPI")).equalsIgnoreCase(codCpi)) {	
			boolean mostraSeparatore = !codCpi.equals("");
			codCpi = new String((String)row.getAttribute("CODCPI"));
			// stampa l'intestazione del cpi
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
			String strTitolo = "Offerte di lavoro del " +sdf.format(d); 
			String strProvincia= "Provincia di " +(String) row.getAttribute("STRDENOMINAZIONE");
			String strCPI = "Centro per l'impiego di " + (String) row.getAttribute("STRDESCRIZIONE");
			String strIndirizzo = (String) row.getAttribute("STRINDIRIZZO");
			String strFax = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row,"STRFAX");
			String strTelFax = "tel.: "+(String) it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row,"STRTEL");
			if (!strFax.equals("")) {
				strTelFax = strTelFax + "    fax: " + strFax;
			}
			String strMail=  "e-mail: "+it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(row,"STREMAIL");
%>
<%
		if (mostraSeparatore) {
%>
		<tr>
			<td colspan="2">
				<hr>
			</td>
		</tr>		
		<%}%>
		
		<% if (flagCM == true) { %>
				<tr><td colspan="2">&nbsp;</td></tr>
		<% } else { 
		
			if (strIntestazioneProvinciaRiga.equals("")) {%>
				<tr><td colspan="2" class="pro"><%=strProvincia%></td></tr>
			<%} else {%>
				<tr><td colspan="2" class="pro"><%=strIntestazioneProvinciaRiga%></td></tr>
			<%}%>

		<tr><td colspan="2" class="cpi"><%=strCPI%></td></tr>
		<tr><td colspan="2" class="via"><%=strIndirizzo%></td></tr>
		<tr><td colspan="2" class="cont"><%=strTelFax%></td></tr>
		<tr><td colspan="2" class="cont"><%=strMail%></td></tr>
		<tr><td colspan="2" >&nbsp;</td></tr>
		<% } %>
		
		<%} // if (cpi)
		//
		// riprendi le variabili per stampare la singola richiesta
		strRichiesta =
			"Cod. "
				+ numRichiesta
				+ "/"
				+ (BigDecimal) (row.getAttribute("NUMANNO"))
				+ " valida fino al "
				+ (String) (row.getAttribute("DATSCADENZA"));
		strMansionePub = (String)row.getAttribute("STRMANSIONEPUBB");
		strRifMansioni = new StringBuffer();
		vMansioniRic = row.getAttributeAsVector("RIF_MANSIONI.ROWS.ROW");
		for (int j = 0; j < vMansioniRic.size(); j++) {
			SourceBean sbMansione = (SourceBean)vMansioniRic.get(j);
			strRifMansioni.append((String)sbMansione.getAttribute("CODMANSIONE") + " ");
			strRifMansioni.append((String)sbMansione.getAttribute("STRDESCRIZIONE"));
			strRifMansioni.append("\n");
		}
		codEv = (String)row.getAttribute("CODEVASIONE");
		if ((codEv != null) && (!codEv.equalsIgnoreCase("DFA")) && (!codEv.equalsIgnoreCase("DRA"))&& (!codEv.equalsIgnoreCase("MPA"))) {
			if (row.getAttribute("STRDATIAZIENDAPUBB") != null) 
				strAziendaRichiedente = StringUtils.getAttributeStrNotNull(row, "STRDATIAZIENDAPUBB");
		}
		if (row.getAttribute("TXTFIGURAPROFESSIONALE") != null) {
			// Contenuti e\nContesto del lavoro
			figuraProfessionale = StringUtils.getAttributeStrNotNull(row, "TXTFIGURAPROFESSIONALE");
		}
		if (row.getAttribute("STRLUOGOLAVORO") != null) {
			// Luogo di lavoro 
			luogoDiLavoro=StringUtils.getAttributeStrNotNull(row, "STRLUOGOLAVORO");
		}
		if (row.getAttribute("STRFORMAZIONEPUBB") != null) {
			//Formazione
			formazione=StringUtils.getAttributeStrNotNull(row, "STRFORMAZIONEPUBB");
		}
		if (row.getAttribute("TXTCARATTERISTFIGPROF") != null) {
			//Caratteristiche\ncandidati
			caratteristicheCandidati=StringUtils.getAttributeStrNotNull(row, "TXTCARATTERISTFIGPROF");
		}
		if (row.getAttribute("TXTCONDCONTRATTUALE") != null) {
			//Contratto
			contratto=StringUtils.getAttributeStrNotNull(row, "TXTCONDCONTRATTUALE");
		}
		if (row.getAttribute("STRCONOSCENZEPUBB") != null) {
			// Conoscenze
			conoscienze=StringUtils.getAttributeStrNotNull(row, "STRCONOSCENZEPUBB");
		}
		if (row.getAttribute("STRNOTEORARIOPUBB") != null) {
			//Orario
			orario = StringUtils.getAttributeStrNotNull(row, "STRNOTEORARIOPUBB");
		}
		if ( flagCM && row.getAttribute("CODMONOCMCATPUBB") != null) {
			//Categoria CM
			codMonoCMcatPubb = StringUtils.getAttributeStrNotNull(row, "CODMONOCMCATPUBB");
		}
		if (row.getAttribute("STRRIFCANDIDATURAPUBB") != null) {
			//Per candidarsi 
			contatti = StringUtils.getAttributeStrNotNull(row, "STRRIFCANDIDATURAPUBB");
		}
		if (row.getAttribute("stremailpubbl") != null) {
			//Per candidarsi 
			emailContatto = StringUtils.getAttributeStrNotNull(row, "stremailpubbl");
			if (row.getAttribute("descrizioneAzienda") != null) {
				descrizioneAzienda = StringUtils.getAttributeStrNotNull(row,"descrizioneAzienda");	
			}
		}
	%>
	<tr>
		<td colspan="2">
			<hr>
		</td>
	</tr>
	<tr>
		<td class="campo"  colspan="2"><%=strRichiesta%></td>
	</tr>
<%		if (strAziendaRichiedente!=null) {%>
	<tr>
		<td class="etichetta">Azienda richiedente</td>
		<td class="campo"><%=sostituisciCRLF_LT(strAziendaRichiedente)%></td>
	</tr>
<%		} %>
<%		if (strMansionePub!=null || strRifMansioni!=null) {%>
	<tr>
		<td class="etichetta">Mansione</td>
		<td class="campo">
			<table cellspacing="0" cellpadding="0" border="0">
			<%if (strMansionePub!=null) {%>
				<tr>
					<td colspan="2" class="campo"><%=sostituisciCRLF_LT(strMansionePub)%></td>
				</tr>
			<%}%>
			<%if (strRifMansioni!=null) {%>
				<tr>
					<td class="etichetta">Qualifica ISTAT &nbsp;</td>
					<td class="campo"><%=strRifMansioni%></td>
				</tr>
			<%}%>
			</table>
		</td>
	</tr>
<%		} %>
<%		if (figuraProfessionale!=null) {%>
	<tr>
		<td class="etichetta">Contenuti e contesto del lavoro</td>
		<td class="campo"><%=sostituisciCRLF_LT(figuraProfessionale)%></td>
	</tr>
<%		} %>
<%		if (luogoDiLavoro!=null) {%>
	<tr>
		<td class="etichetta">Luogo di lavoro </td>
		<td class="campo"><%=sostituisciCRLF_LT(luogoDiLavoro)%></td>
	</tr>
<%		} %>
<%		if (formazione!=null) {%>
	<tr>
		<td class="etichetta">Formazione</td>
		<td class="campo"><%=sostituisciCRLF_LT(formazione)%></td>
	</tr>
<%		} %>
<%		if (caratteristicheCandidati!=null) {%>
	<tr>
		<td class="etichetta">Caratteristiche candidati</td>
		<td class="campo"><%=sostituisciCRLF_LT(caratteristicheCandidati)%></td>
	</tr>
<%		} %>
<%		if (contratto!=null) {%>
	<tr>
		<td class="etichetta">Contratto</td>
		<td class="campo"><%=sostituisciCRLF_LT(contratto)%></td>
	</tr>
<%		} %>
<%		if (conoscienze!=null) {%>
	<tr>
		<td class="etichetta">Conoscenze</td>
		<td class="campo"><%=sostituisciCRLF_LT(conoscienze)%></td>
	</tr>
<%		} %>
<%		if (orario!=null) {%>
	<tr>
		<td class="etichetta">Orario</td>
		<td class="campo"><%=sostituisciCRLF_LT(orario)%></td>
	</tr>
<%		} %>
<%		if (codMonoCMcatPubb!=null) {%>
	<tr>
		<td class="etichetta">Categoria CM</td>
		<td class="campo"><%=sostituisciCRLF_LT(codMonoCMcatPubb)%></td>
	</tr>
<%		} %>
<%		if (contatti!=null) {%>
	<tr>
		<td class="etichetta">Per candidarsi</td>
		<td class="campo"><%=sostituisciCRLF_LT(contatti)%></td>
	</tr>
<%		}%>
<%		if (emailContatto!=null) {%>
	<tr>
		<td class="etichetta2"  valign=top colspan="2" >Invia e-mail</td>
		<td> <a href="mailto:<%=emailContatto%>?subject=Mi candido alla domanda di lavoro <%=numRichiesta%>/<%=numAnno%> di/del <%=descrizioneAzienda%>" >
			 <img border="0" src="../../img/icomail.gif" alt="invio e-mail per candidarsi"></a>
		</td>
	</tr>
<%		} %>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>	
<%		
		// resetto le variabili usate nel ciclo
		strRichiesta= strMansionePub= codEv = strAziendaRichiedente=figuraProfessionale=luogoDiLavoro = 
		formazione=caratteristicheCandidati =contratto =  conoscienze = orario =  contatti = numAnno=
		numRichiesta=emailContatto=descrizioneAzienda= null;
		strRifMansioni =null;
		vMansioniRic = null;		

	} // for (elencoPubbl)%>
</table>
<%@ include file="/jsp/MIT.inc" %>
</body>
</html>


<%! 
	private String sostituisciCRLF_LT(String s) {
		return StringUtils.replace(StringUtils.replace(s, "<" ,"&lt;"),"\r\n", "<br>");
	}
%>