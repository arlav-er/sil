<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"
	import="com.engiweb.framework.base.*,it.eng.sil.security.*,it.eng.afExt.utils.*,it.eng.sil.util.*,java.math.*,java.util.*"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%
	ProfileDataFilter filter = new ProfileDataFilter(user,
			"CMRiepilogoProvinciale");
	boolean canView = filter.canView();
	if (!canView) {
		response
				.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

	String prgAzienda = "";
	String prgUnita = "";

	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"PAGE");
	String codProvincia = "";
	// in fase di inserimento viene selezionata la prov del SIL
	InfoProvinciaSingleton provincia = InfoProvinciaSingleton.getInstance();
	codProvincia = provincia.getCodice();
	String prgProspettoInf = "";
	BigDecimal numkloprospettoinf = new BigDecimal("0");
	String categoria = "";
	String strNote = "";
	String displayDateAss = "none";
	String visualizza_display = "none";

	String codStatoAtto = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codStatoAtto");

	Object cdnUtIns = null;
	Object dtmIns = null;
	Object cdnUtMod = null;
	Object dtmMod = null;
	boolean flag_insert = false;
	String pagina_back = null;
	InfCorrentiAzienda infCorrentiAzienda = null;
	//INFORMAZIONI OPERATORE
	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	Testata operatoreInfo = null;
	Linguette l = null;
	String dataRifQ3 = "";
	String msgRiepilogoData = "";

	SourceBean dat_azienda = (SourceBean) serviceResponse.getAttribute("M_CMPROSP_GETDATAZIENDA.ROWS.ROW");

	if (dat_azienda != null) {
		prgProspettoInf = dat_azienda.getAttribute("prgProspettoInf") == null ? ""
				: ((BigDecimal) dat_azienda
						.getAttribute("prgProspettoInf")).toString();
		//prgProspettoInf = SourceBeanUtils.getAttrBigDecimal(dat_azienda,"PRGPROSPETTOINF").toString();
		prgAzienda = dat_azienda.getAttribute("prgAzienda") == null ? ""
				: ((BigDecimal) dat_azienda.getAttribute("prgAzienda"))
						.toString();
		prgUnita = dat_azienda.getAttribute("prgUnita") == null ? ""
				: ((BigDecimal) dat_azienda.getAttribute("prgUnita"))
						.toString();
		
		
		dataRifQ3 = StringUtils.getAttributeStrNotNull(dat_azienda, "datRifQ3");
		if (!dataRifQ3.equals("")) {
			msgRiepilogoData = "I seguenti dati sono riferiti al " + dataRifQ3;
		}
	}

	l = new Linguette(user, cdnfunzione, _page, new BigDecimal(
			prgProspettoInf), codStatoAtto);
	l.setCodiceItem("PRGPROSPETTOINF");

	String url = "";
	String goBackTitle = "";

	//info dell'unità aziendale	
	if (prgAzienda != null && prgUnita != null
			&& !prgAzienda.equals("") && !prgUnita.equals("")) {
		infCorrentiAzienda = new InfCorrentiAzienda(sessionContainer,
				prgAzienda, prgUnita);
		infCorrentiAzienda.setPaginaLista("CMProspListaPage");
	}

	PageAttribs attributi = new PageAttribs(user, "CMProspDettPage");

	boolean canModify = false;
	boolean readOnlyStr = false;
	String fieldReadOnly = "true";
	String strReadOnly = "true";

	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	boolean checkProspetto2011 = serviceResponse.containsAttribute("M_Check_Prospetti_Inviati_Entro_Il_31122011.ROWS.ROW.PROSPETTO2011") && serviceResponse.getAttribute("M_Check_Prospetti_Inviati_Entro_Il_31122011.ROWS.ROW.PROSPETTO2011").toString().equalsIgnoreCase("TRUE")?true:false;
%>

<html>
<head>
<title>Elenco Riepiloghi Provinciali</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/" />

<script language="Javascript">
<%if (!prgAzienda.equals("") && !prgUnita.equals("")) {%>
	window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
<%}%>
</script>




</head>

<body class="gestione" onload="rinfresca()">

<%
	if (infCorrentiAzienda != null) {
%>
<div id="infoCorrAz" style="display: ">
<%
	infCorrentiAzienda.show(out);
%>
</div>
<%
	}
	l.show(out);
%>

<p class="titolo">Elenco Riepiloghi Provinciali</p>

<center><font color="green"> <!--<af:showMessages prefix="M_CMRiepilogoProvinciale"/> -->
</font></center>
<center><font color="red"><af:showErrors /></font></center>


<input type="hidden" name="PAGE" value="CMRiepilogoProvinciale" />
<input type="hidden" name="prgAziendaApp" value="" />
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>" />
<input type="hidden" name="numkloprospettoinf"
	value="<%=numkloprospettoinf%>" />
<input type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>" />

<%
out.print(htmlStreamTop);
if (!msgRiepilogoData.equals("")) {%>
<br><p class="titolo"><%=msgRiepilogoData%></p>
<%}%>

<table class="lista" align="center" cellpadding="2" cellspacing="2">
		<tbody>
			<tr>
				<th class="lista" align="center" colspan="15" rowspan="1"></th>	
			</tr>
			<tr>
				<th class="lista" align="center" colspan="1" rowspan="3">Provincia</th>
				<%if (checkProspetto2011) { %>		
					<th class="lista" align="center" colspan="1" rowspan="3">BC</th>
				<%} else {%>
					<th class="lista" align="center" colspan="1" rowspan="3">BC Art.3</th>
					<th class="lista" align="center" colspan="1" rowspan="3">BC Art.18</th>	
				<%}%>
				<%if (checkProspetto2011) { %>
					<th class="lista" align="center" colspan="1" rowspan="3">Sosp.</th>
				<%} else {%>
					<th class="lista" align="center" colspan="1" rowspan="3">Sosp.</th>
				<%}%>
				<th class="lista" align="center" colspan="1" rowspan="3">Posizioni
				esonerate</th>
				<%if (checkProspetto2011) { %>
					<th class="lista" align="center" colspan="6" rowspan="1">Disabili</th>
				<%} else {%>
					<th class="lista" align="center" colspan="5" rowspan="1">Disabili</th>
				<%}%>	
				<th class="lista" align="center" colspan="5" rowspan="1">Categoria
				Protetta</th>
				<th width="6" class="prof_ro"></th>
			</tr>
			<tr>
				<th class="lista" align="center" colspan="1" rowspan="2">In
				Forza</th>
				<%if (checkProspetto2011) { %>
					<th class="lista" align="center" colspan="1" rowspan="2"> Cat. Prot. in Forza Conteggiate come disabili</th>
				<%}%>
				<th class="lista" align="center" colspan="2" rowspan="1">Compensazione</th>
				<th class="lista" align="center" colspan="1" rowspan="2">Quota
				di Riserva</th>
				<th class="lista" align="center" colspan="1" rowspan="2">Scoperture</th>
				<th class="lista" align="center" colspan="1" rowspan="2">In
				forza</th>
				<th class="lista" align="center" colspan="2" rowspan="1">Compensazione</th>
				<th class="lista" align="center" colspan="1" rowspan="2">Quota
				di riserva</th>
				<th class="lista" align="center" colspan="1" rowspan="2">Scoperture</th>
				
			</tr>
			<tr>
				<th class="lista" align="center">Saldo</th>
				<th class="lista" align="center">Categoria</th>
				<th class="lista" align="center">Saldo</th>
				<th class="lista" align="center">Categoria</th>
			</tr>

			<%
				String strNomeClasse = "";
				int i = 0;
				String strdenominazione = "";
				String numlavoratoribc = "";
				String numlavoratoribc3 = "";
				String numlavoratoribc18 = "";
				String flgSospensione = "";
				String numsospensioni = "";
				String numdisabiliforza = "";
				String numcatprotforza =  "";
				String numcatprotdisabiliforza =  "";
				String numquotarisdisabili = "";
				String numquotariscatprot = "";
				String numesoneri = "";
				String numscoperturadis = "";
				String numscoperturacatprot = "";
				String numcompcatprot = "";
				String numcompdisabili = "";
				String codmonoeccdiffdisabili = "";
				String codmonoeccdiffcatprot = "";

				Vector rows = (Vector) serviceResponse
						.getAttributeAsVector("M_CMRiepilogoProvinciale.ROWS.ROW");
				if (rows != null) {
					Iterator iterat = rows.iterator();
					while (iterat.hasNext()) {

						SourceBean row = (SourceBean) iterat.next();
						if (i % 2 == 0) {
							strNomeClasse = "lista_dispari";
						} else {
							strNomeClasse = "lista_pari";
						}
						
						
						strdenominazione = row.getAttribute("strdenominazione") == null ? "&nbsp;"
								: row.getAttribute("strdenominazione").toString();

						numlavoratoribc = row.getAttribute("numlavoratoribc") == null ? "&nbsp;"
								: ((BigDecimal) row.getAttribute("numlavoratoribc"))
										.toString();
						
						numlavoratoribc3 = row.getAttribute("numlavoratoribc3") == null ? "&nbsp;"
								: ((BigDecimal) row.getAttribute("numlavoratoribc3"))
										.toString();
						numlavoratoribc18 = row.getAttribute("numlavoratoribc18") == null ? "&nbsp;"
								: ((BigDecimal) row.getAttribute("numlavoratoribc18"))
										.toString();
						flgSospensione = row.getAttribute("flgSospensione") == null ? ""
								: row.getAttribute("flgSospensione").toString();
						if (flgSospensione.equals("") || flgSospensione.equalsIgnoreCase("N")) {
							flgSospensione = "No";	
						}
						else {
							flgSospensione = "Sì";
						}
						numsospensioni = row.getAttribute("numsospensioni") == null ? "&nbsp;"
								: ((BigDecimal) row.getAttribute("numsospensioni"))
										.toString();
						
						numesoneri = row.getAttribute("numesoneri") == null ? "&nbsp;"
								: ((BigDecimal) row.getAttribute("numesoneri"))
										.toString();
						
						numdisabiliforza = row.getAttribute("numdisabiliforza") == null ? "&nbsp;"
								: ((BigDecimal) row
										.getAttribute("numdisabiliforza"))
										.toString();
						numcatprotforza = row.getAttribute("numcatprotforza") == null ? "&nbsp;"
								: ((BigDecimal) row.getAttribute("numcatprotforza"))
										.toString();
					
						numcatprotdisabiliforza = row
								.getAttribute("numcatprotdisabiliforza") == null ? "&nbsp;"
								: ((BigDecimal) row
										.getAttribute("numcatprotdisabiliforza"))
										.toString();
						numquotarisdisabili = row
								.getAttribute("numquotarisdisabili") == null ? "&nbsp;"
								: ((BigDecimal) row
										.getAttribute("numquotarisdisabili"))
										.toString();
						numquotariscatprot = row.getAttribute("numquotariscatprot") == null ? "&nbsp;"
								: ((BigDecimal) row
										.getAttribute("numquotariscatprot"))
										.toString();
						
						numscoperturadis = row.getAttribute("numscoperturadis") == null ? "&nbsp;"
								: ((BigDecimal) row
										.getAttribute("numscoperturadis"))
										.toString();
						numscoperturacatprot = row
								.getAttribute("numscoperturacatprot") == null ? "&nbsp;"
								: ((BigDecimal) row
										.getAttribute("numscoperturacatprot"))
										.toString();
						numcompcatprot = row.getAttribute("numcompcatprot") == null ? "&nbsp;"
								: ((BigDecimal) row.getAttribute("numcompcatprot"))
										.toString();
						numcompdisabili = row.getAttribute("numcompdisabili") == null ? "&nbsp;"
								: ((BigDecimal) row.getAttribute("numcompdisabili"))
										.toString();

						codmonoeccdiffdisabili = row
								.getAttribute("codmonoeccdiffdisabili") == null ? "&nbsp;"
								: row.getAttribute("codmonoeccdiffdisabili")
										.toString();
						codmonoeccdiffcatprot = row
								.getAttribute("codmonoeccdiffcatprot") == null ? "&nbsp;"
								: row.getAttribute("codmonoeccdiffcatprot")
										.toString();
			
						if("D".equals(codmonoeccdiffdisabili)){
							codmonoeccdiffdisabili = "Riduzione";
						} else if ( "E".equals(codmonoeccdiffdisabili) ){
							codmonoeccdiffdisabili = "Eccedenza";
						}
						
						if("D".equals(codmonoeccdiffcatprot)){
							codmonoeccdiffcatprot = "Riduzione";
						} else if ( "E".equals(codmonoeccdiffcatprot) ){
							codmonoeccdiffcatprot = "Eccedenza";
						}
					
			%>
			
			
			<tr>
				<td class="<%=strNomeClasse%>" align="left"><%=strdenominazione%></td>
				<%if (checkProspetto2011) { %>
					<td class="<%=strNomeClasse%>" align="right"><%=numlavoratoribc%></td>
				<%} else {%>
					<td class="<%=strNomeClasse%>" align="right"><%=numlavoratoribc3%></td>
					<td class="<%=strNomeClasse%>" align="right"><%=numlavoratoribc18%></td>
				<%}%>
				<%if (checkProspetto2011) { %>
					<td class="<%=strNomeClasse%>" align="right"><%=numsospensioni%></td>
				<%} else {%>
					<td class="<%=strNomeClasse%>" align="right"><%=flgSospensione%></td>
				<%}%>	
				<td class="<%=strNomeClasse%>" align="right"><%=numesoneri%></td>
				<td class="<%=strNomeClasse%>" align="right"><%=numdisabiliforza%></td>
				<%if (checkProspetto2011) { %>
					<td class="<%=strNomeClasse%>" align="right"><%=numcatprotdisabiliforza%></td>
				<%}%>
				<td class="<%=strNomeClasse%>" align="right"><%=numcompdisabili%></td>
				<td class="<%=strNomeClasse%>" align="right"><%=codmonoeccdiffdisabili%></td>				
				<td class="<%=strNomeClasse%>" align="right"><%=numquotarisdisabili%></td>
				<td class="<%=strNomeClasse%>" align="right"><%=numscoperturadis%></td>
				
				<td class="<%=strNomeClasse%>" align="right"><%=numcatprotforza%></td>
				<td class="<%=strNomeClasse%>" align="right"><%=numcompcatprot%></td>
				<td class="<%=strNomeClasse%>" align="right"><%=codmonoeccdiffcatprot%></td>				
				<td class="<%=strNomeClasse%>" align="right"><%=numquotariscatprot%></td>				
				<td class="<%=strNomeClasse%>" align="right"><%=numscoperturacatprot%></td>		
				

			</tr>
			<%
				i++;
					}
				}
			%>
			<tr valign="bottom"><td width="6" valign="bottom" height="6px" align="left" class="sfondo_lista"></td><td height="6px" class="sfondo_lista">&nbsp;</td><td width="6" valign="bottom" height="6px" align="right" class="sfondo_lista"></td></tr>
		</tbody>
	</table> 
<table class="main">
</table>


<%
	out.print(htmlStreamBottom);
%>

</body>
</html>