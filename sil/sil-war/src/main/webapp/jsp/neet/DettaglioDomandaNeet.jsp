<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.lang.*,
                  java.text.*"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
String _page = (String) serviceRequest.getAttribute("PAGE");
ProfileDataFilter filter = new ProfileDataFilter(user, "DomandeNeetPage");

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
} else {
	// NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, "DomandeNeetPage");
	
	boolean canAdd = attributi.containsButton("INSERISCI");
	boolean canDelete = attributi.containsButton("CANCELLA");
	boolean canStoricizza = attributi.containsButton("STORICIZZA");
	boolean isCancellata = false;
	boolean isStoricizzata = false;
	
	String strNomeLav = "";
	String strCognomeLav = "";
	String strCodFiscLav = "";
	String datNascLav = "";
	String comNascLav = "";
	String titlePagina = "";
	Testata operatoreInfo = null;
	String codRaggruppamentoPrecNeet = "";
	String cdnUtIns = "";
	String dtmIns = "";
	String cdnUtMod = "";
	String dtmMod = "";
	String dataDichiarazione = "";
	String datCancellazione = "";
	String datStoricizzazione = "";
	String prgLavoratoreNeet = "";
	Vector domandeNeet = null;
	int numDomandeNeet = 0;
	BigDecimal numklolavneet = null;
	
	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
	
	boolean consultaDich = serviceRequest.containsAttribute("PRGLAVORATORENEET");
	if (consultaDich) {
		titlePagina = "Dettaglio dichiarazione";
		prgLavoratoreNeet = serviceRequest.getAttribute("PRGLAVORATORENEET").toString();
	}
	else {
		titlePagina = "Nuova dichiarazione";	
	}
	
	SourceBean rowLav = (SourceBean) serviceResponse.getAttribute("M_Get_Info_Lavoratore.ROWS.ROW");
	
	if (consultaDich) {
		domandeNeet = serviceResponse.getAttributeAsVector("M_DettaglioDomandaNeet.ROWS.ROW");
		numDomandeNeet = domandeNeet.size();
		SourceBean rowDomanda = (SourceBean)domandeNeet.get(0);
		dataDichiarazione = rowDomanda.getAttribute("datdichiarazioneneet").toString();
		datCancellazione = SourceBeanUtils.getAttrStrNotNull(rowDomanda, "datcanc");
		if (!datCancellazione.equals("")) {
			isCancellata = true;
		}
		datStoricizzazione = SourceBeanUtils.getAttrStrNotNull(rowDomanda, "datstoricizzazione");
		if (!datStoricizzazione.equals("")) {
			isStoricizzata = true;
		}
		cdnUtIns = rowDomanda.getAttribute("cdnutins").toString();
		dtmIns = rowDomanda.getAttribute("dtmins").toString();
		cdnUtMod = rowDomanda.getAttribute("cdnutmod").toString();
		dtmMod = rowDomanda.getAttribute("dtmmod").toString();
		operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
		numklolavneet = (BigDecimal) rowDomanda.getAttribute("numklolavneet");
		numklolavneet = numklolavneet.add(new BigDecimal(1));
	}
	else {
		domandeNeet = serviceResponse.getAttributeAsVector("M_GetDomandeDichiarazioneNeet.ROWS.ROW");
		numDomandeNeet = domandeNeet.size();	
	}
	
	strNomeLav = SourceBeanUtils.getAttrStrNotNull(rowLav, "STRNOME");
	strCognomeLav = SourceBeanUtils.getAttrStrNotNull(rowLav, "STRCOGNOME");
	strCodFiscLav = SourceBeanUtils.getAttrStrNotNull(rowLav, "STRCODICEFISCALE");
	datNascLav = SourceBeanUtils.getAttrStrNotNull(rowLav, "DATNASC");
	comNascLav = SourceBeanUtils.getAttrStrNotNull(rowLav, "STRCOMNAS");
	
	String htmlStreamTop = StyleUtils.roundTopTable(!consultaDich);
	String htmlStreamBottom = StyleUtils.roundBottomTable(!consultaDich);
%>

	<html>
	<head>
	<title>Dettaglio dichiarazione</title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
	<af:linkScript path="../../js/" />
	
	<script type="text/Javascript">
		<%//Genera il Javascript che si occuperà di inserire i links nel footer
		if (!cdnLavoratore.equals(""))
			attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);
		%>	

		function mostraMessaggio() {
		  if (document.Frm1.OPERAZIONENEET.value == 'SALVA') {
		  	alert("Per completare la dichiarazione e' necessario caricare nella sezione Documenti la Carta d'Itentita' e la Dichirazione NEET");
		  }
		  return true;
	    }

		function cancellaNeet() {
			document.Frm1.OPERAZIONENEET.value = 'CANCELLA';
	    }

		function inserisciNeet() {
			document.Frm1.OPERAZIONENEET.value = 'SALVA';
		}

		function storicizzaaNeet() {
			document.Frm1.OPERAZIONENEET.value = 'STORICIZZA';
		}
		
	</script>
	
	</head>
	
	<body class="gestione" onload="rinfresca();">
	
	<%
	InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
	testata.show(out);
	%>
	
	<p>
	 	<font color="green">
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>

	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="mostraMessaggio()">
		<p class="titolo"><%=titlePagina%></p>
		<% out.print(htmlStreamTop); %>
		<br>
		<table class="main">
			
			<tr><td colspan="6"><div class="sezione2"/>Dati anagrafici del giovane</td></tr>
			
			<tr>
				<td class="etichetta" nowrap>Nome&nbsp;</td>
				<td class="campo">
					<af:textBox classNameBase="input" type="text" name="strNomeLav" title="Nome" readonly="true" value="<%=strNomeLav%>" />
				</td>
				<td class="etichetta" nowrap>Cognome&nbsp;</td>
				<td class="campo">
					<af:textBox classNameBase="input" type="text" name="strCognomeLav" title="Cognome" readonly="true" value="<%=strCognomeLav%>" />
				</td>
				<td class="etichetta" nowrap>Codice Fiscale&nbsp;</td>
				<td class="campo">
					<af:textBox classNameBase="input" type="text" name="strCFLav" title="Codice Fiscale" readonly="true" value="<%=strCodFiscLav%>" />
				</td>
			</tr>
			
			<tr><td colspan="6">&nbsp;</td><tr>
			
			<tr>
				<td class="etichetta" nowrap>Data di nascita&nbsp;</td>
				<td class="campo">
					<af:textBox classNameBase="input" type="date" name="datNascLav" title="Data di nascita" readonly="true" value="<%=datNascLav%>" />
				</td>
				<td class="etichetta" nowrap>Comune di nascita&nbsp;</td>
				<td class="campo">
					<af:textBox classNameBase="input" type="text" name="comNascLav" title="Comune di nascita" readonly="true" value="<%=comNascLav%>" />
				</td>
				
				<td class="etichetta" nowrap>Data dichiarazione&nbsp;</td>
				<td class="campo">
					<af:textBox classNameBase="input" type="date" name="datDichiarazione" value="<%=dataDichiarazione%>" validateOnPost="true" title="Data dichiarazione"
                    	required="true" size="11" maxlength="10" readonly="<%= String.valueOf(consultaDich) %>" />
				</td>
			</tr>
		
		</table>
		
		<br>
		
		<table class="main">
		
		<%for (int num=0;num<numDomandeNeet;num++) {
			SourceBean domandaBean = (SourceBean)domandeNeet.get(num);
			String codRaggruppamentoNeet = domandaBean.getAttribute("coddomandaneetragg").toString();
			String descRaggruppamentoNeet = domandaBean.getAttribute("descRaggNeet").toString();
			String descDomandaNeet = domandaBean.getAttribute("descDomandaNeet").toString();
			String labelSi = domandaBean.getAttribute("strlabelsi").toString();
			String labelNo = domandaBean.getAttribute("strlabelno").toString();
			String labelNa = domandaBean.getAttribute("strlabelna").toString();
			String codDomandaNeet = domandaBean.getAttribute("codDomandaNeet").toString();
			String campoOsservazioni = "OSSERVAZIONI" + codDomandaNeet;
			String campoRisposta = "RISPOSTA" + codDomandaNeet;
			String rispostaDomanda = "";
			String osservazioniDomanda = "";
			if (consultaDich) {
				rispostaDomanda = SourceBeanUtils.getAttrStrNotNull(domandaBean, "strrisposta");
				osservazioniDomanda = SourceBeanUtils.getAttrStrNotNull(domandaBean, "strosservazione");
			}
			if (codRaggruppamentoPrecNeet.equals("")) {
			%>
				<tr><td colspan="3"><div class="sezione2"/><%=descRaggruppamentoNeet%></td></tr>
				<tr><td colspan="3">&nbsp;</td></tr>
				<tr>
					<td class="etichetta"><center><b>Descrizione delle verifiche da realizzare</b></center></td>
					<td class="etichetta"><center><b>Esito della Verifica</b></center></td>
					<td class="etichetta"><center><b>Osservazioni</b></center></td>
				</tr>
				<tr><td colspan="3">&nbsp;</td></tr>
				<tr>
					<%out.println(descDomandaNeet);%>
					<td class="etichetta">
						<center>
						<input type="radio" required="true" <%if (consultaDich){%>disabled="true" <%}%>
							<%if (consultaDich && "SI".equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
							name="<%=campoRisposta%>" value="SI"/><%=labelSi%>&nbsp;&nbsp;
						<input type="radio" required="true" <%if (consultaDich){%>disabled="true" <%}%>
							<%if (consultaDich && "NO".equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
							name="<%=campoRisposta%>" value="NO"/><%=labelNo%>&nbsp;&nbsp;
						<input type="radio" required="true" <%if (consultaDich){%>disabled="true" <%}%>
							<%if (consultaDich && "NA".equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
							name="<%=campoRisposta%>" value="NA"/><%=labelNa%>
						</center>
					</td>
					<td class="etichetta">
						<center>
						<af:textArea name="<%=campoOsservazioni%>" cols="40" maxlength="2000"
							rows="4" title="Osservazioni" readonly="<%= String.valueOf(consultaDich) %>" classNameBase="input"
							value="<%=osservazioniDomanda%>"/>
						</center>
					</td>
				</tr>
			<%
			}
			else {
				if (!codRaggruppamentoPrecNeet.equalsIgnoreCase(codRaggruppamentoNeet)) {
				%>
					<tr><td colspan="3"><div class="sezione2"/><%=descRaggruppamentoNeet%></td></tr>
					<tr><td colspan="3">&nbsp;</td></tr>
					<tr>
						<td class="etichetta"><center><b>Descrizione delle verifiche da realizzare</b></center></td>
						<td class="etichetta"><center><b>Esito della Verifica</b></center></td>
						<td class="etichetta"><center><b>Osservazioni</b></center></td>
					</tr>
					<tr><td colspan="3">&nbsp;</td></tr>
					<tr>
					<%out.println(descDomandaNeet);%>
						<td class="etichetta">
							<center>
							<input type="radio" required="true" <%if (consultaDich){%>disabled="true" <%}%>
								<%if (consultaDich && "SI".equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=campoRisposta%>" value="SI"/><%=labelSi%>&nbsp;&nbsp;
							<input type="radio" required="true" <%if (consultaDich){%>disabled="true" <%}%>
								<%if (consultaDich && "NO".equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=campoRisposta%>" value="NO"/><%=labelNo%>&nbsp;&nbsp;
							<input type="radio" required="true" <%if (consultaDich){%>disabled="true" <%}%>
								<%if (consultaDich && "NA".equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=campoRisposta%>" value="NA"/><%=labelNa%>
							</center>
						</td>
						<td class="etichetta">
							<center>
							<af:textArea name="<%=campoOsservazioni%>" cols="40" maxlength="2000"
								rows="4" title="Osservazioni" readonly="<%= String.valueOf(consultaDich) %>" classNameBase="input"
								value="<%=osservazioniDomanda%>"/>
							</center>
						</td>
					</tr>
				<%
				}
				else {
				%>
					<tr>
						<%out.println(descDomandaNeet);%>
						<td class="etichetta">
							<center>
							<input type="radio" required="true" <%if (consultaDich){%>disabled="true" <%}%>
								<%if (consultaDich && "SI".equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=campoRisposta%>" value="SI"/><%=labelSi%>&nbsp;&nbsp;
							<input type="radio" required="true" <%if (consultaDich){%>disabled="true" <%}%>
								<%if (consultaDich && "NO".equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=campoRisposta%>" value="NO"/><%=labelNo%>&nbsp;&nbsp;
							<input type="radio" required="true" <%if (consultaDich){%>disabled="true" <%}%>
								<%if (consultaDich && "NA".equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=campoRisposta%>" value="NA"/><%=labelNa%>
							</center>
						</td>
						<td class="etichetta">
							<center>
							<af:textArea name="<%=campoOsservazioni%>" cols="40" maxlength="2000"
								rows="4" title="Osservazioni" readonly="<%= String.valueOf(consultaDich) %>" classNameBase="input"
								value="<%=osservazioniDomanda%>"/>
							</center>
						</td>
					</tr>
				<%
				}
			}
			codRaggruppamentoPrecNeet = codRaggruppamentoNeet;
		}
		%>
		</table>
		
        <%
        if (consultaDich) {
        	%>
        	<br>
        	<table align="center">
        	<tr align="center">
          	<td align="center">
        	<%
        	if (!isCancellata) {
        		if (canStoricizza && !isStoricizzata) {%>
        			<input type="submit" class="pulsanti" name="BTNSTORICIZZA" value="Storicizza" onclick="storicizzaaNeet()">
        		<%}
	        	if (canDelete) {%>
	            	<input type="submit" class="pulsanti" name="BTNCANCELLA" value="Cancella" onclick="cancellaNeet()">
	           	<%}
        	}%>
        	<input type="hidden" name="numklolavneet" value="<%=numklolavneet.toString()%>">
	        </td>
	        </tr>
	        </table>
	       <%
        }
        else {
        	if (canAdd) {%>
              	<br>
          		<table align="center">
            		<tr align="center">
              		<td align="center">
                  	<input type="submit" class="pulsanti" name="BTNSALVA" value="Salva" onclick="inserisciNeet()">
                	</td>
                	<td align="center">
                		<input type="reset" class="pulsanti" name="BTNRESET" value="Annulla" />
              		</td>
              		</tr>
              	</table>
            <%}	
        }
        
        if (operatoreInfo != null) {
        %>
   		<br>
		<center>
		<% 
		operatoreInfo.showHTML(out);
		%>
		</center>
		<%}
		out.print(htmlStreamBottom); 
		%>
		<input type="hidden" name="PAGE" value="DomandeNeetPage">
		<input type="hidden" name="cdnFunzione" value="<%=Utils.notNull(_funzione)%>">
		<input type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>">
		<input type="hidden" name="PRGLAVORATORENEET" value="<%=Utils.notNull(prgLavoratoreNeet)%>">
		<input type="hidden" name="OPERAZIONENEET" value="">
	</af:form>
	
	
	</body>
	</html>
<%
}
%>