<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.dispatching.module.AbstractModule" %>
<%@ page import="com.engiweb.framework.util.QueryExecutor" %>
<%@ page import="com.engiweb.framework.security.*" %>
<%@ page import="com.engiweb.framework.base.*" %>
<%@ page import="it.eng.sap.xml.sap.SchedaAnagraficaProfessionaleDTO"%>
<%@ page import="it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.*"%>
<%@ page import="it.eng.sil.pojo.yg.sap.*" %>
<%@ page import="it.eng.sil.util.*" %>
<%@ page import="it.eng.sil.security.ProfileDataFilter" %>
<%@ page import="it.eng.sil.security.PageAttribs" %>
<%@ page import="it.eng.sil.security.User" %>
<%@ page import="it.eng.afExt.utils.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.*" %>
<%@ page import="java.io.*" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%
	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	String idSap = (String) serviceRequest.getAttribute("idSap");

	PageAttribs attributi = new PageAttribs(user, _current_page);

	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	boolean switchEnable = true; //usato per non stampare sezioni vuote

	SchedaAnagraficaProfessionaleDTO sapPortaleLav = (SchedaAnagraficaProfessionaleDTO) serviceResponse
			.getAttribute("M_SapCallVisualizzaSapPortale.SAPPORTALE");
	
	SourceBean rows = (SourceBean) sessionContainer.getAttribute("ROWS");
	Vector vector = rows.getAttributeAsVector("ROW");
	String strCodiceFiscale = "";
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date dtmDataNascita = sapPortaleLav.getDataNascita().getTime();
    String strDataNascita = sdf.format(dtmDataNascita);
%>

<html>

<head>
<title>Visualizza SAP Portale</title>
<link rel="stylesheet" media="print, screen"
	href="../../css/stiliCoop.css" type="text/css">
<af:linkScript path="../../js/" />
</head>
<body class="gestione" onload="rinfresca()">

	<p class="titolo">
		<br> <b>Visualizza SAP Portale</b>
	</p>

	<%
		out.print(htmlStreamTop);
	%>
	<center>
		<font color="red"> <af:showErrors />
		</font>
	</center>

	<%
		if (sapPortaleLav != null) {
	%>

	<table class="main">

		<tr>
			<td valign=top bgcolor="#000000" colspan="6">
				<p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"> <b>Sezione 1 - Dati Anagrafici</b>
					</font>
				</p>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Codice fiscale</td>
			<td align="left" class="inputView"><b><%=sapPortaleLav.getStrCodiceFiscale()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">email</td>
			<td align="left" class="inputView"><b><%=sapPortaleLav.getEmail()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Comune di nascita</td>
			<td align="left" class="inputView"><b><%=sapPortaleLav.getDescComuneNascita()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Data di nascita</td>
			<td align="left" class="inputView"><b><%=strDataNascita%></b></td>
		</tr>
		<!-- fine dati anagrafici -->
		<tr>
			<td valign=top bgcolor="#000000" colspan="6">
				<p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"> <b>
							Sezione 2 - Titoli di Studio</b>
					</font>
				</p>
			</td>
		</tr>
		<%
			if (sapPortaleLav.getSapTitoloStudioList() != null)
					for (int i = 0; i < sapPortaleLav.getSapTitoloStudioList().length; i++) {
						SapTitoloStudioDTO sapTitoloStudio = sapPortaleLav.getSapTitoloStudioList(i);
		%>
		<tr>
			<td class="etichetta">Titolo</td>
			<td align="left" class="inputView"><b><%=sapTitoloStudio.getCodTitolo() == null ? ""
								: sapTitoloStudio.getCodTitolo().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Tipo Titolo</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapTitoloStudio.getCodTitolo())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Anno di conseguimento</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapTitoloStudio.getNumAnno())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Specifica</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapTitoloStudio.getStrSpecifica())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Principale</td>
			<td align="left" class="inputView"><b><%=sapTitoloStudio.isFlgPrincipale() ? "si" : "no"%></b></td>
		</tr>
		<!-- redmine 4058 -->
		<tr>
			<td class="etichetta">Stato completamento</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapTitoloStudio.getCodMonoStato())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Istituto</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapTitoloStudio.getStrNomeIstituto())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Comune</td>
			<td align="left" class="inputView"><b><%=sapTitoloStudio.getCodComune() == null ? ""
								: sapTitoloStudio.getCodComune().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Votazione</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapTitoloStudio.getStrVotazione())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">&nbsp;</td>
			<td class="inputView">&nbsp;</td>
		</tr>
		<%
			}
		%>
		<!-- fine titoli di studio -->
		<tr>
			<td valign=top bgcolor="#000000" colspan="6">
				<p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"> <b>
							Sezione 3 - Formazione Professionale</b>
					</font>
				</p>
			</td>
		</tr>
		<%
			if (sapPortaleLav.getSapFormazioneList() != null)
					for (int i = 0; i < sapPortaleLav.getSapFormazioneList().length; i++) {
						SapFormazioneDTO sapFormazione = sapPortaleLav.getSapFormazioneList(i);
		%>
		<tr>
			<td class="etichetta">Titolo Corso</td>
			<td align="left" class="inputView"><b><%=sapFormazione.getCodCorso() == null ? "" : sapFormazione.getCodCorso() + ": " +  sapFormazione.getCodCorso().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Tematiche Principali</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapFormazione.getStrTematiche())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Istituto</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapFormazione.getStrNomeIstituto())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Completato</td>
			<td align="left" class="inputView"><b><%=sapFormazione.getFlgCompletato() == null ? "-" : (sapFormazione.getFlgCompletato().booleanValue() ? "si" : "no")%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Anno di conseguimento</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapFormazione.getNumAnnoConseguimento())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">&nbsp;</td>
			<td class="inputView">&nbsp;</td>
		</tr>
		<%
			}
		%>
		<!-- fine formazione -->
		<tr>
			<td valign=top bgcolor="#000000" colspan="6">
				<p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"> <b>
							Sezione 4 - Esperienze di Lavoro</b>
					</font>
				</p>
			</td>
		</tr>
		<%
			if (sapPortaleLav.getSapEsperienzaLavList() != null)
					for (int i = 0; i < sapPortaleLav.getSapEsperienzaLavList().length; i++) {
						SapEsperienzaLavDTO sapEsperienzaLav = sapPortaleLav.getSapEsperienzaLavList(i);
		%>
		<tr>
			<td class="etichetta">Qualifica svolta</td>
			<td align="left" class="inputView"><b><%=sapEsperienzaLav.getCodMansioneMin() == null ? ""
								: sapEsperienzaLav.getCodMansioneMin().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Attivit&agrave;/Responbabilit&agrave;</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapEsperienzaLav.getStrDescrAttivita())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Tipologia di contratto</td>
			<td align="left" class="inputView"><b><%=sapEsperienzaLav.getCodContratto() == null ? ""
								: sapEsperienzaLav.getCodContratto().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Datore di lavoro</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapEsperienzaLav.getStrDatoreLavoro())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Attivit&agrave; del datore di lavoro</td>
			<td align="left" class="inputView"><b><%=sapEsperienzaLav.getCodAttivitaMin() == null ? ""
								: sapEsperienzaLav.getCodAttivitaMin().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Data inizio Rapporto</td>
			<td align="left" class="inputView"><b><%=sapEsperienzaLav.getDtInizio() == null ? ""
								: DateUtils.format(sapEsperienzaLav.getDtInizio().getTime())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Data fine Rapporto</td>
			<td align="left" class="inputView"><b><%=sapEsperienzaLav.getDtFine() == null ? ""
								: DateUtils.format(sapEsperienzaLav.getDtFine().getTime())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">&nbsp;</td>
			<td class="inputView">&nbsp;</td>
		</tr>
		<%
			}
		%>
		<!-- fine eseprienze lavorative -->
		<tr>
			<td valign=top bgcolor="#000000" colspan="6">
				<p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"> <b>
							Sezione 5 - Lingue</b>
					</font>
				</p>
			</td>
		</tr>
		<%
			if (sapPortaleLav.getSapLinguaList() != null)
					for (int i = 0; i < sapPortaleLav.getSapLinguaList().length; i++) {
						SapLinguaDTO sapLingua = sapPortaleLav.getSapLinguaList(i);
		%>
		<tr>
			<td class="etichetta">Lingua</td>
			<td align="left" class="inputView"><b><%=sapLingua.getCodLingua() == null ? "" : sapLingua.getCodLingua().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Madrelingua</td>
			<td align="left" class="inputView"><b><%=sapLingua.getFlgMadrelingua() == null ? "-" : (sapLingua.getFlgMadrelingua().booleanValue() ? "si" : "no")%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Livello Lettura</td>
			<td align="left" class="inputView"><b><%=sapLingua.getCodGradoLetto() == null ? ""
								: sapLingua.getCodGradoLetto().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Livello Scrittura</td>
			<td align="left" class="inputView"><b><%=sapLingua.getCodGradoScritto() == null ? ""
								: sapLingua.getCodGradoScritto().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Livello espressione orale</td>
			<td align="left" class="inputView"><b><%=sapLingua.getCodGradoParlato() == null ? ""
								: sapLingua.getCodGradoParlato().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Conoscenza Certificata</td>
			<td align="left" class="inputView"><b><%=sapLingua.getFlgCertificazione() == null ? "-" : (sapLingua.getFlgCertificazione().booleanValue() ? "si" : "no")%></b></td>
		</tr>
		<tr>
			<td class="etichetta">&nbsp;</td>
			<td class="inputView">&nbsp;</td>
		</tr>
		<%
			}
		%>
		<tr>
			<td valign=top bgcolor="#000000" colspan="6">
				<p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"> <b>
							Sezione 6 - Conoscenze Informatiche</b>
					</font>
				</p>
			</td>
		</tr>
		<!-- fine lingue -->
		<%
			if (sapPortaleLav.getSapConoscenzeInfoList() != null)
					for (int i = 0; i < sapPortaleLav.getSapConoscenzeInfoList().length; i++) {
						SapConoscenzeInfoDTO sapConoscenzeInfo = sapPortaleLav.getSapConoscenzeInfoList(i);
		%>
		<tr>
			<td class="etichetta">Tipo</td>
			<td align="left" class="inputView"><b><%=sapConoscenzeInfo.getCodTipoConInformatica() == null ? ""
								: sapConoscenzeInfo.getCodTipoConInformatica().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Dettaglio</td>
			<td align="left" class="inputView"><b><%=sapConoscenzeInfo.getCodDettaglioConInformatica() == null ? ""
								: sapConoscenzeInfo.getCodDettaglioConInformatica().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Livello Conoscenza</td>
			<td align="left" class="inputView"><b><%=sapConoscenzeInfo.getCodGradoConInformatica() == null ? ""
								: sapConoscenzeInfo.getCodGradoConInformatica().toString()%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Descrizione</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapConoscenzeInfo.getStrDescrizione())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">&nbsp;</td>
			<td class="inputView">&nbsp;</td>
		</tr>
		<%
			}
		%>
		<!-- fine conoscenze informatiche -->
		<tr>
			<td valign=top bgcolor="#000000" colspan="6">
				<p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"> <b>
							Sezione 7 - Abilitazioni</b>
					</font>
				</p>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Patenti</td>
			<td align="left" class="inputView">
		<%
			if (sapPortaleLav.getSapPatenteList() != null)
					for (int i = 0; i < sapPortaleLav.getSapPatenteList().length; i++) {
						SapPatenteDTO sapPatente = sapPortaleLav.getSapPatenteList(i);
		%>
			<b><%=sapPatente.getCodPatente() == null ? "" : sapPatente.getCodPatente().toString() +  "; "%></b>
		<%
			}
		%>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Patentini</td>
			<td align="left" class="inputView">
		<%
			if (sapPortaleLav.getSapPatentinoList() != null)
					for (int i = 0; i < sapPortaleLav.getSapPatentinoList().length; i++) {
						SapPatentinoDTO sapPatentino = sapPortaleLav.getSapPatentinoList(i);
		%>
			<b><%=sapPatentino.getCodPatentino() == null ? "" : sapPatentino.getCodPatentino().toString() +  "; "%></b>
		<%
			}
		%>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Albi</td>
			<td align="left" class="inputView">
		<%
			if (sapPortaleLav.getSapAlboList() != null)
					for (int i = 0; i < sapPortaleLav.getSapAlboList().length; i++) {
						SapAlboDTO sapAlbo = sapPortaleLav.getSapAlboList(i);
		%>
			<b><%=sapAlbo.getCodAlbo() == null ? "" : sapAlbo.getCodAlbo() +  "; "%></b>
		<%
			}
		%>
			</td>
		</tr>
		<!-- fine abilitazioni -->
		<tr>
			<td valign=top bgcolor="#000000" colspan="6">
				<p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"> <b>
							Sezione 8 - Propensioni</b>
					</font>
				</p>
			</td>
		</tr>
		<%
			if (sapPortaleLav.getSapPropensioneList() != null)
					for (int i = 0; i < sapPortaleLav.getSapPropensioneList().length; i++) {
						SapPropensioneDTO sapPropensione = sapPortaleLav.getSapPropensioneList(i);
		%>
		<tr>
			<td class="etichetta">Qualifica desiderata</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapPropensione.getCodMansioneMin())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Descrizione</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapPropensione.getStrDescrizione())%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Esperienza nel settore</td>
			<td align="left" class="inputView"><b><%=sapPropensione.getFlgEsperienza() == null ? "-" : (sapPropensione.getFlgEsperienza().booleanValue() ? "si" : "no")%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Orari</td>
			<td align="left" class="inputView">
				<%
					if (sapPropensione.getSapDisponibilitaOrarioList() != null) {
						for (int j = 0; j < sapPropensione.getSapDisponibilitaOrarioList().length; j++) {
							SapDisponibilitaOrarioDTO orario = sapPropensione.getSapDisponibilitaOrarioList()[j];
								if (orario != null) {
				%> 
				<b><%=orario == null ? "null" : orario.getCodOrario() + "; "%></b>
				<%
 						}
 					}
 				}
 				%>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Turni</td>
			<td align="left" class="inputView">
				<%
					if (sapPropensione.getSapDisponibilitaTurnoList() != null) {
						for (int j = 0; j < sapPropensione.getSapDisponibilitaTurnoList().length; j++) {
							SapDisponibilitaTurnoDTO turno = sapPropensione.getSapDisponibilitaTurnoList()[j];
								if (turno != null) {
				%> 
				<b><%=turno == null ? "null" : turno.getCodTurno() + "; "%></b>
				<%
 						}
 					}
 				}
 				%>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Comuni</td>
			<td align="left" class="inputView">
				<%
					if (sapPropensione.getSapDisponibilitaComuneList() != null) {
						for (int j = 0; j < sapPropensione.getSapDisponibilitaComuneList().length; j++) {
							SapDisponibilitaComuneDTO comune = sapPropensione.getSapDisponibilitaComuneList()[j];
								if (comune != null) {
				%> 
				<b><%=comune == null ? "null" : comune.getCodComune() + "; "%></b>
				<%
 						}
 					}
 				}
 				%>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Province</td>
			<td align="left" class="inputView">
				<%
					if (sapPropensione.getSapDisponibilitaProvinciaList() != null) {
						for (int j = 0; j < sapPropensione.getSapDisponibilitaProvinciaList().length; j++) {
							SapDisponibilitaProvinciaDTO provincia = sapPropensione.getSapDisponibilitaProvinciaList()[j];
								if (provincia != null) {
				%> 
				<b><%=provincia == null ? "null" : provincia.getCodProvincia() + "; "%></b>
				<%
 						}
 					}
 				}
 				%>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Regioni</td>
			<td align="left" class="inputView">
				<%
					if (sapPropensione.getSapDisponibilitaRegioneList() != null) {
						for (int j = 0; j < sapPropensione.getSapDisponibilitaRegioneList().length; j++) {
							SapDisponibilitaRegioneDTO regione = sapPropensione.getSapDisponibilitaRegioneList()[j];
							if (regione != null) {
				%>
				<b><%=regione == null ? "null" : regione.getCodRegione() + "; "%></b>
				<%
 						}
 					}
 				}
 				%>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Stati</td>
			<td align="left" class="inputView">
				<%
					if (sapPropensione.getSapDisponibilitaStatoList() != null) {
						for (int j = 0; j < sapPropensione.getSapDisponibilitaStatoList().length; j++) {
							SapDisponibilitaStatoDTO stato = sapPropensione.getSapDisponibilitaStatoList()[j];
							if (stato != null) {
				%>
				<b><%=stato == null ? "null" : stato.getCodComune() + "; "%></b>
				<%
 						}
 					}
 				}
 				%>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Automunito</td>
			<td align="left" class="inputView"><b><%=sapPropensione.getFlgAutomunito() == null ? "-" : (sapPropensione.getFlgAutomunito().booleanValue() ? "si" : "no")%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Motomunito</td>
			<td align="left" class="inputView"><b><%=sapPropensione.getFlgMotomunito() == null ? "-" : (sapPropensione.getFlgMotomunito().booleanValue() ? "si" : "no")%></b></td>
		</tr>
		<tr>
			<td class="etichetta">Uso mezzi pubblici</td>
			<td align="left" class="inputView"><b><%=sapPropensione.getFlgMezzipub() == null ? "-" : (sapPropensione.getFlgMezzipub().booleanValue() ? "si" : "no")%></b></td>
		</tr>
		<tr>
			<td class="etichetta">&nbsp;</td>
			<td class="inputView">&nbsp;</td>
		</tr>
		<%
			}
		%>
		<!-- fine propensioni -->
			<td valign=top bgcolor="#000000" colspan="6">
				<p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"> <b>Sezione 9 - Altre Informazioni</b>
					</font>
				</p>
			</td>
		<tr>
			<td class="etichetta">Note</td>
			<td align="left" class="inputView"><b><%=Utils.notNull(sapPortaleLav.getStrAnnotazioniColloquio())%></b></td>
		</tr>
	</table>
	<%
		}
		out.print(htmlStreamBottom);
	%>

	<center>
		<input class="pulsante" type="button" name="chiudi" value="Chiudi"
			onclick="window.close()" />
	</center>
</body>

</html>
