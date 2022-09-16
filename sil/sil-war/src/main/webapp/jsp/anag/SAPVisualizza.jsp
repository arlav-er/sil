<%@page import="com.itextpdf.text.log.SysoLogger"%>
<%@page import="it.eng.sil.module.agenda.AllineaSlot"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.afExt.utils.*,
  it.eng.sil.pojo.yg.sap.view.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,   
  com.engiweb.framework.security.*"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
  
<%
/*modificato settembre 2019*/
	// cdnLavoratore può essere null quando il lavoratore non è presente in anagrafica sul sil
	String cdnLavoratore = (String) serviceRequest
			.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	
	ProfileDataFilter filter = new ProfileDataFilter(user,
			_current_page);
	
	if (!filter.canView()) {
	  	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	  	return;
	}
	boolean canImportaSAP = false;
	
	String numConfigImporta = serviceResponse.containsAttribute("M_CONFIG_IMPORTAZIONE_SAP.ROWS.ROW.NUM")?
  			serviceResponse.getAttribute("M_CONFIG_IMPORTAZIONE_SAP.ROWS.ROW.NUM").toString():it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
  	if (numConfigImporta.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.CUSTOM_CONFIG)) {
  		canImportaSAP = true;	
  	}
	
	PageAttribs attributi = new PageAttribs(user, _current_page);
	canImportaSAP = canImportaSAP && attributi.containsButton("IMPORTA_SAP");
	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	boolean switchEnable = true; //usato per non stampare sezioni vuote

  	LavoratoreType lavTView =null;
 
  	if(serviceResponse.containsAttribute("M_SAPCALLRICHIESTASAP.SAPWS2_VIEW") && serviceResponse.getAttribute("M_SAPCALLRICHIESTASAP.SAPWS2_VIEW")!=null){
		lavTView =  (LavoratoreType) serviceResponse.getAttribute("M_SAPCALLRICHIESTASAP.SAPWS2_VIEW");
  	}
  	
%>

<html>

<head>
<title>Richiesta SAP</title>
<link rel="stylesheet" media="print, screen"
	href="../../css/stiliCoop.css" type="text/css">
<link rel="stylesheet" media="print, screen" type="text/css"
	href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
</head>

<body class="gestione" onload="rinfresca()">

	<p class="titolo">
		<br>
		<b>Richiesta SAP</b>
	</p>
	
	<%out.print(htmlStreamTop);%>
	<table class="main">
	
	
	<%if (lavTView == null) {  %>
		<center>
			<font color="red"> <af:showErrors />
			</font>
		</center>
	<%} else { %>

	
		<tr>
			<td nowrap class="etichetta">Identificativo SAP</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(lavTView.getDatiinvio().getIdentificativosap())%></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">Data ultimo aggiornamento</td>
			<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(lavTView.getDatiinvio().getDataultimoagg()) %></b>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichetta">Codice ente Titolare</td>
			<td nowrap align="left" class="inputView">
			<b><%=lavTView.getDatiinvio().getCodiceentetit()%>
			- <af:comboBox name="ente_tit" multiple="false"
                        moduleName="COMBO_ENTETIT" disabled="true"
                        classNameBase="input"
                        selectedValue="<%=lavTView.getDatiinvio().getCodiceentetit()%>" /></b>
            </td>
		</tr>
		<tr>
			<td nowrap class="etichetta">Tipo Variazione</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(lavTView.getDatiinvio().getTipovariazione())%>
					- <af:comboBox name="tipo_var" multiple="false"
						moduleName="COMBO_TIPOVARIAZIONE" disabled="true"
						classNameBase="input"
						selectedValue="<%=lavTView.getDatiinvio().getTipovariazione()%>" /></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">Data di nascita</td>
			<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(lavTView.getDatiinvio().getDatadinascita())%></b>
			</td>
		</tr>


		<tr>
			<td valign=top colspan="2"><p align="center">
					<font size="3" face="Arial"><b><u>Modulo - Scheda
								anagrafico-professionale</u></b></font>
				</p></td>
		</tr>

		<tr>
			<td valign=top bgcolor="#000000" colspan="2"><p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
							1 - Dati Personali</b></font>
				</p></td>
		</tr>
		<tr>
			<td valign=top bgcolor="#808080" colspan="2"><p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
							1.1 - Dati Personali</b></font>
				</p></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">codice fiscale</td>
			<td nowrap align="left" class="inputView"><b><%=lavTView.getDatianagrafici().getDatipersonali().getCodicefiscale()%></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">sesso</td>
			<td nowrap align="left" class="inputView"><b><%=lavTView.getDatianagrafici().getDatipersonali().getSesso()%></b>
			</td>
		</tr>

		<tr>
			<td nowrap class="etichetta">cognome</td>
			<td nowrap align="left" class="inputView"><b><%=lavTView.getDatianagrafici().getDatipersonali().getCognome()%></b>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichetta">nome</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(lavTView.getDatianagrafici().getDatipersonali().getNome())%></b>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichetta">comune o in alternativa stato straniero di
				nascita
			</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(lavTView.getDatianagrafici().getDatipersonali().getCodcomune())%> - <af:comboBox
						name="comune_nasc" multiple="false" moduleName="COMBO_COMUNE"
						disabled="true" classNameBase="input"
						selectedValue="<%=Utils.notNull(lavTView.getDatianagrafici().getDatipersonali().getCodcomune())%>" /></b>
			</td>
		</tr>

		<tr>
			<td nowrap class="etichetta">cittadinanza</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(lavTView.getDatianagrafici().getDatipersonali().getCodcittadinanza())%> - <af:comboBox
						name="comune_dom" multiple="false" moduleName="COMBO_CITTADINANZA"
						disabled="true" classNameBase="input"
						selectedValue="<%=Utils.notNull(lavTView.getDatianagrafici().getDatipersonali().getCodcittadinanza())%>" /></b></td>
		</tr>
		
		<tr>
			<td nowrap class="etichetta">data di nascita</td>
			<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(lavTView.getDatianagrafici().getDatipersonali().getDatanascita())%></b></td>
		</tr>
		<tr>
			<td valign=top bgcolor="#808080" colspan="2"><p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
							1.2 - Notizie sui cittadini stranieri</b></font>
				</p></td>
		</tr>
		<%
			Datistranieri ds2 = lavTView.getDatianagrafici().getDatistranieri();
					if (ds2 == null){
						ds2 = new Datistranieri();
					}
		%>
		<tr>
			<td nowrap class="etichetta">titolo di soggiorno</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(ds2.getCodtipodocumento())%>
					- <af:comboBox name="titsogg" multiple="false"
						moduleName="COMBO_TIPODOC" disabled="true" classNameBase="input"
						selectedValue="<%=Utils.notNull(ds2.getCodtipodocumento())%>" /></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">numero titolo di soggiorno</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(ds2.getNumero())%></b>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichetta">motivo titolo di soggiorno</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(ds2.getMotivo())%></b>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichetta">scadenza titolo di soggiorno</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(DateUtils.formatXMLGregorian(ds2.getValidoal()))%></b>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichetta">data ultimo mantenimento iscrizione
			</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(ds2.getDataultimomantiscr())%></b>
			</td>
		</tr>
		<tr>
			<td valign=top bgcolor="#808080" colspan="2"><p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
							1.3 - Residenza o domicilio</b></font>
				</p></td>
		</tr>
		<tr>
			<td valign=top bgcolor="#C0C0C0" colspan="2">
				<p align="center">
					<font size="2" face="sans-serif">Sezione 1.3.1 - Residenza</font>
				</p>
			</td>
		</tr>
		
		<%
 		Residenza residenz2  = lavTView.getDatianagrafici().getResidenza();
			if (residenz2 == null) {
				residenz2 = new Residenza();
			}
		
		%>		
		<tr>
			<td nowrap class="etichetta">comune di residenza</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(residenz2.getCodcomune())%> - <af:comboBox name="comune_res"
						multiple="false" moduleName="COMBO_COMUNE" disabled="true"
						classNameBase="input"
						selectedValue="<%=Utils.notNull(residenz2.getCodcomune())%>" /></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">CAP</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(residenz2.getCap())%></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">indirizzo di residenza</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(residenz2.getIndirizzo())%></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">località</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(residenz2.getLocalita())%></b></td>
		</tr>																		
		<tr>
			<td valign=top bgcolor="#C0C0C0" colspan="2">
				<p align="center">
					<font size="2" face="sans-serif">Sezione 1.3.2 - Domicilio
						eletto</font>
				</p>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichetta">comune di domicilio</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(lavTView.getDatianagrafici().getDomicilio().getCodcomune())%> - <af:comboBox name="comune_dom"
						multiple="false" moduleName="COMBO_COMUNE" disabled="true"
						classNameBase="input"
						selectedValue="<%=Utils.notNull(lavTView.getDatianagrafici().getDomicilio().getCodcomune()) %>" /></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">CAP</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(lavTView.getDatianagrafici().getDomicilio().getCap()) %></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">indirizzo di domicilio</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(lavTView.getDatianagrafici().getDomicilio().getIndirizzo())%></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">località</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(lavTView.getDatianagrafici().getDomicilio().getLocalita())%></b></td>
		</tr>
		<tr>
			<td valign=top bgcolor="#C0C0C0" colspan="2">
				<p align="center">
					<font size="2" face="sans-serif">Sezione 1.3.3 - Recapiti</font>
				</p>
			</td>
		</tr>
		<%
 		Recapiti recap2 =  lavTView.getDatianagrafici().getRecapiti();
			if (recap2 == null) {
				recap2 = new Recapiti();
			}
		%>
		<tr>
			<td nowrap class="etichetta">numero di telefono domicilio</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(recap2.getTelefono())%></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">numero di telefono fax</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(recap2.getFax()) %></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">numero di telefono cellulare</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(recap2.getCellulare())%></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">indirizzo di posta elettronica</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(recap2.getEmail())%></b></td>
		</tr>		
		<tr>
			<td valign=top bgcolor="#000000" colspan="2"><p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
							2 - Dati Amministrativi</b></font>
				</p></td>

		</tr>
		<tr>
			<td valign=top bgcolor="#808080" colspan="2"><p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
							2.1 - Posizione nel mercato del lavoro</b></font>
				</p></td>
		</tr>
		<tr>
			<td valign=top bgcolor="#C0C0C0" colspan="2">
				<p align="center">
					<font size="2" face="sans-serif">Sezione 2.1.1 - Stato in
						Anagrafe</font>
				</p>
			</td>
		</tr>
		<%
 			Statoinanagrafe sta2 = lavTView.getDatiamministrativi().getStatoinanagrafe();
			String indiceProfilingSta = "";
				if (sta2 == null) {
					sta2 = new Statoinanagrafe();	
				}
				
				if (sta2.getIndiceprofiling() != null) {
					indiceProfilingSta = sta2.getIndiceprofiling().toString();	
				}
		%>

		<tr>
			<td nowrap class="etichetta">stato occupazionale</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(sta2.getCodstatooccupazionale())%>
					- <af:comboBox name="mn_statoocc" multiple="false"
						moduleName="COMBO_STATOOCC" disabled="true" classNameBase="input"
						selectedValue="<%=Utils.notNull(sta2.getCodstatooccupazionale())%>" /></b>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichetta">condizione</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(sta2.getCodstatus())%>
					- <af:comboBox name="mn_statoocc_esito" multiple="false"
						moduleName="COMBO_STATOOCC_ESITO" disabled="true"
						classNameBase="input"
						selectedValue="<%=Utils.notNull(sta2.getCodstatus())%>" /></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">categoria dlg. 150 e s.m.</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(sta2.getCategoria297())%></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">anzianità di disoccupazione (mesi)</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(sta2.getAnzianita())%></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">indice di profiling</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(indiceProfilingSta)%></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">data evento</td>
			<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(sta2.getDataevento())%></b></td>
		</tr>
		<tr>
			<td nowrap class="etichetta">data dichiarazione di
				disponibilità</td>
			<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(sta2.getDisponibilita())%></b></td>
		</tr>
		<tr>
			<td valign=top bgcolor="#C0C0C0" colspan="2">
				<p align="center">
					<font size="2" face="sans-serif">Sezione 2.1.2 - Periodi di
						disoccupazione</font>
				</p>
			</td>
		</tr>
		<%
 		Periodidisoccupazione pd2 =  lavTView.getDatiamministrativi().getPeriodidisoccupazione();
			if (pd2 == null){
				pd2 = new Periodidisoccupazione();
			} 
		%>
		<tr>
			<td nowrap class="etichetta">data ingresso</td>
			<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(pd2.getDataingresso())%></b>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichetta">tipo ingresso</td>
			<td nowrap align="left" class="inputView"><b><%=Utils.notNull(pd2.getTipoingresso())%></b>
			</td>
		</tr>


		<%
	 	 if ( lavTView.getDatiamministrativi().getListespecialiLst() != null) {
			List listeSpeciali = lavTView.getDatiamministrativi().getListespecialiLst().getListespeciali();
			if (listeSpeciali.size() > 0)
				switchEnable = true;
			for (int i = 0; i < listeSpeciali.size(); i++) {
				Listespeciali item = (Listespeciali) listeSpeciali.get(i);
			%>

			<%
				if (switchEnable) {
			%>
			<tr>
				<td valign=top bgcolor="#808080" colspan="2"><p align="center">
						<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
								2.2 - Liste speciali e graduatorie</b></font>
					</p></td>
			</tr>

			<%
				}
				switchEnable = false;
			%>

			<tr>
				<td nowrap class="etichetta">lista</td>
				<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getTipolista())%></b>
				</td>
			</tr>
			<tr>
				<td nowrap class="etichetta">data iscrizione lista</td>
				<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(item.getDataiscrizione())%></b></td>
			</tr>
			<tr>
				<td nowrap class="etichetta">data termine iscrizione</td>
				<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(item.getDatafineiscrizione())%></b></td>
			</tr>
			<tr>
				<td nowrap class="etichetta">data massimo deferimento</td>
				<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(item.getDatamaxdeferimento())%></b></td>
			</tr>
			<tr>
				<td nowrap class="etichetta">provincia di iscrizione alla lista</td>
				<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getCodprovincia())%></b>
				</td>
			</tr>
			<%
			}
		}
		%>
		
		
		<tr>
			<td valign=top bgcolor="#808080" colspan="2"><p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
							2.3 - Assolvimento diritto/dovere all'istruzione</b></font>
				</p></td>
		</tr>
		<%
		String asshole ="";
		 
		if(lavTView.getDatiamministrativi()!=null && lavTView.getDatiamministrativi().getAssolvimentoistruzione()!= null && 
				lavTView.getDatiamministrativi().getAssolvimentoistruzione().getObbligoformativo()!=null){
			asshole = Utils.notNull(lavTView.getDatiamministrativi().getAssolvimentoistruzione().getObbligoformativo());
		}
		%>
		<tr>
			<td nowrap class="etichetta">obbligo formativo assolto</td>
			<td nowrap align="left" class="inputView"><b><%=asshole%></b></td>
		</tr>

		<%
 		Altrenotizie an2 = lavTView.getDatiamministrativi().getAltrenotizie();
		
		if ( an2 != null) {
		%>
			<tr>
				<td valign=top bgcolor="#808080" colspan="2"><p align="center">
						<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
								2.4 - Altre Notizie</b></font>
					</p></td>
			</tr>
			<tr>
				<td nowrap class="etichetta">appartenenza a particolari categorie</td>
				<td nowrap align="left" class="inputView"><b><%=Utils.notNull(an2.getCategorieprotette())%></b>
				</td>
			</tr>
			<tr>
				<td nowrap class="etichetta">indice ISEE</td>
				<td nowrap align="left" class="inputView"><b><%=Utils.notNull(an2.getIndiceisee())%></b>
				</td>
			</tr>
		<%
		}
		%>
		<tr>
			<td valign=top bgcolor="#000000" colspan="2"><p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
							4 - Esperienze di lavoro</b></font>
				</p></td>
		</tr>
		<tr>
			<td valign=top bgcolor="#808080" colspan="2"><p align="center">
					<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
							4.1 - Esperienze professionali</b></font>
				</p></td>
		</tr>
		<%
		 if ( lavTView.getEsperienzelavoroLst() != null) {
			int pp = 0;
			List listeEspLav = lavTView.getEsperienzelavoroLst().getEsperienzalavoro();
			for (int j = 0; j < listeEspLav.size(); j++) {
				Esperienzalavoro item = (Esperienzalavoro) listeEspLav.get(j);
				pp++;
				%>
				<tr>
					<td valign=top bgcolor="#C0C0C0" colspan="2">
						<p align="center">
							<font size="2" face="sans-serif">Sezione 4.1.1 - Dati
								generali esperienza</font>
						</p>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">codice fiscale azienda</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getAzienda().getCodicefiscale())%></b></td>
				</tr>
				<tr>
					<td nowrap class="etichetta">denominazione azienda</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getAzienda().getDatorelavoro())%></b></td>
				</tr>
				<tr>
					<td nowrap class="etichetta">indirizzo azienda</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getAzienda().getIndirizzoazienda())%></b></td>
				</tr>
				<tr>
					<td nowrap class="etichetta">settore (ATECOFIN)</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getAzienda().getCodateco())%> - <af:comboBox name="cmb_mansione_<%=pp%>"
								multiple="false" moduleName="COMBO_MN_ATECO" disabled="true"
								classNameBase="input"
								selectedValue="<%=Utils.notNull(item.getAzienda().getCodateco())%>" />
					</b></td>
				</tr>
				<%
				Utilizzatrice uti = item.getUtilizzatrice();
								if (uti == null)
									uti = new Utilizzatrice();
				%>
				<tr>
					<td nowrap class="etichetta">codice fiscale azienda utilizzatrice</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(uti.getCodicefiscale())%></b>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">denominazione azienda utilizzatrice</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(uti.getDatorelavoro())%></b>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">indirizzo azienda utilizzatrice</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(uti.getIndirizzoazienda())%></b>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">settore (ATECOFIN)</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(uti.getCodateco())%>
							- <af:comboBox name="cmb_mansione_<%=pp%>" multiple="false"
								moduleName="COMBO_MN_ATECO" disabled="true" classNameBase="input"
								selectedValue="<%=Utils.notNull(uti.getCodateco())%>" /> </b></td>
				</tr>
				<tr>
					<td nowrap class="etichetta">data inizio</td>
					<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(item.getDatainizio())%></b></td>
				</tr>
				<tr>
					<td nowrap class="etichetta">data fine</td>
					<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(item.getDatafine())%></b></td>
				</tr>
				<tr>
					<td nowrap class="etichetta">data fine periodo formativo</td>
					<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(item.getDataFinePeriodoFormativo())%></b></td>
				</tr>
				<tr>
					<td nowrap class="etichetta">qualifica professionale svolta (ISTAT)</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getCodprofessione())%>
							- <af:comboBox name="cmb_mansione_<%=pp%>" multiple="false"
								moduleName="COMBO_MN_MANSIONE" disabled="true"
								classNameBase="input"
								selectedValue="<%=Utils.notNull(item.getCodprofessione())%>" /> </b></td>
				</tr>
				<tr>
				    <td nowrap class="etichetta">principali mansioni e responsabilità</td>
				    <td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getCompiti())%></b></td>
				
				</tr>
				<tr>
					<td valign=top bgcolor="#C0C0C0" colspan="2">
						<p align="center">
							<font size="2" face="sans-serif">Sezione 4.1.2 - Tipo
								Rapporto</font>
						</p>
					</td>
				</tr>
				<%
				Tiporapporto tr = item.getTiporapporto();
								if (tr == null){
									tr = new Tiporapporto();
								}
				%>
				<tr>
					<td nowrap class="etichetta">tipo di contratto</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(tr.getTipocontratto())%>
							- <af:comboBox name="cmb_mansione_<%=pp%>" multiple="false"
								moduleName="COMBO_MN_TIPO_CONTRATTO" disabled="true"
								classNameBase="input"
								selectedValue="<%=Utils.notNull(tr.getTipocontratto())%>" /> </b></td>
				</tr>
				<tr>
					<td nowrap class="etichetta">assunzione effettuata ai sensi della legge
						L.68/99</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(tr.getAssunzioneLegge68())%></b>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">lavoro stagionale</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(tr.getLavoroStagionale())%></b>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">lavoratore in mobilità</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(tr.getLavInMobilita())%></b>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">lavoro in agricoltura</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(tr.getLavoroInAgricoltura())%></b>
					</td>
				</tr>
				<%
				Modalitalavoro ml = item.getModalitalavoro();
								if (ml == null){
									ml = new Modalitalavoro();
								}
				%>
				<tr>
					<td valign=top bgcolor="#C0C0C0" colspan="2">
						<p align="center">
							<font size="2" face="sans-serif">Sezione 4.1.3 - Modalità</font>
						</p>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">modalità di lavoro</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(ml.getCodmodalitalavoro())%>
							- <af:comboBox name="cmb_modlav_<%=pp%>" multiple="false"
								moduleName="COMBO_MOD_LAVORO" disabled="true"
								classNameBase="input"
								selectedValue="<%=Utils.notNull(ml.getCodmodalitalavoro())%>" />
					</b></td>
				</tr>
				<%
				Luogolavoro ll = item.getLuogolavoro();
								if (ll == null){
									ll = new Luogolavoro();
								}
				%>
				<tr>
					<td valign=top bgcolor="#C0C0C0" colspan="2">
						<p align="center">
							<font size="2" face="sans-serif">Sezione 4.1.4 - Luogo di
								lavoro</font>
						</p>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">indirizzo</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(ll.getIndirizzo())%></b>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">sede di lavoro (comune o stato estero)</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(ll.getCodcomune())%>
							- <af:comboBox name="comune_lav_<%=pp%>" multiple="false"
								moduleName="COMBO_COMUNE" disabled="true" classNameBase="input"
								selectedValue="<%=Utils.notNull(ll.getCodcomune())%>" /> </b></td>
				</tr>
			<%
			}
		}
		
 		Allegato allegato2 =lavTView.getAllegato();
		 
		if ( allegato2 != null) {
		%>
			<tr>
				<td valign=top colspan="2"><p align="center">
						<font size="3" face="Arial"><b><u>ALLEGATO SCHEDA
									ANAGRAFICO - PROFESSIONALE</u></b></font>
					</p></td>
				<br />
			</tr>
	
			<tr>
				<td valign=top bgcolor="#000000" colspan="2"><p align="center">
						<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
								5 - Informazioni curricolari utili all'incontro Domanda/Offerta<br />
								(in collegamento alla Borsa Continua Nazionale del Lavoro)
						</b></font>
					</p></td>
			</tr>
			<% if (  allegato2.getTitolistudioLst() != null) {
				List listeTitStud = allegato2.getTitolistudioLst();
				for (int i = 0; i < listeTitStud.size(); i++) {
					TitolistudioLst item = (TitolistudioLst) listeTitStud.get(i);
					List listaTit = item.getTitolostudio();
					for (int j = 0; j < listaTit.size(); j++) {
						Titolostudio itemTit = (Titolostudio) listaTit.get(j);
						%>
						<tr>
							<td nowrap class="etichetta">livello scolarizzazione</td>
							<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getCodlivelloistruzione())%> - <af:comboBox
										name="lav_livstu<%=itemTit.hashcode()%>" multiple="false"
										moduleName="COMBO_LIVELLO_STUDIO" disabled="true"
										classNameBase="input"
										selectedValue="<%=Utils.notNull(itemTit.getCodlivelloistruzione())%>" />
							</b></td>
						</tr>
						<tr>
							<td nowrap class="etichetta">corso di studio</td>
							<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getCorsostudio())%> - <af:comboBox
										name="lav_corsostu<%=itemTit.hashcode()%>" multiple="false"
										moduleName="COMBO_CORSO_STUDIO" disabled="true"
										classNameBase="input"
										selectedValue="<%=Utils.notNull(itemTit.getCorsostudio())%>" />
							</b></td>
						</tr>
						<tr>
							<td nowrap class="etichetta">descrizione</td>
							<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getDescrizionecorsostudio())%></b></td>
						</tr>
						<tr>
							<td nowrap class="etichetta">frequentato in</td>
							<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getCodcomunefrequenza())%> - <af:comboBox
										name="comune_stu_<%=itemTit.hashcode()%>" multiple="false"
										moduleName="COMBO_COMUNE" disabled="true" classNameBase="input"
										selectedValue="<%=Utils.notNull(itemTit.getCodcomunefrequenza())%>" />
							</b></td>
						</tr>
						<tr>
							<td nowrap class="etichetta">riconosciuto in Italia</td>
							<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getRiconosciutoin())%></b></td>
						</tr>
						<tr>
							<td nowrap class="etichetta"><u>se conseguito</u></td>
							<td nowrap align="left" class="inputView">&nbsp;</td>
						</tr>
						<tr>
							<td nowrap class="etichetta">anno</td>
							<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getAnnotermine())%></b></td>
						</tr>
						<tr>
							<td nowrap class="etichetta">votazione</td>
							<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getVotazione())%></b></td>
						</tr>
						
						<tr>
							<td nowrap class="etichetta"><u>altrimenti</u></td>
							<td nowrap align="left" class="inputView">&nbsp;</td>
						</tr>
						<tr>
							<td nowrap class="etichetta">ultimo anno frequentato</td>
							<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getAnnofrequentato())%></b></td>
						</tr>
						<tr>
							<td nowrap class="etichetta">anno di frequenza (in corso)</td>
							<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getAnnofrequenza())%></b></td>
						</tr>
				<%
					}
				}
			}
			%>
	
			<%
			  if ( allegato2.getFormazioneprofessionaleLst() != null) {
				switchEnable = true;
				List listForm = allegato2.getFormazioneprofessionaleLst();
				for (int i = 0; i < listForm.size(); i++) {
					FormazioneprofessionaleLst item = (FormazioneprofessionaleLst) listForm.get(i);
					List lista = item.getFormazioneprofessionale();
					for (int j = 0; j < lista.size(); j++) {
						Formazioneprofessionale itemTit = (Formazioneprofessionale) lista.get(j);
					%>
						
						<%
						if (switchEnable) {
						%>
						<tr>
						<td valign=top bgcolor="#808080" colspan="2"><p align="center">
								<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
										5.2 - Formazione professionale</b></font>
							</p></td>
						</tr>
						
						<%
							switchEnable = false;
						}
						%>
						<tr>
						<td nowrap class="etichetta">titolo corso di formazione</td>
						<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getTitolocorso())%></b></td>
						</tr>
						<tr>
						<td nowrap class="etichetta">ente erogatore</td>
						<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getEnte())%></b>
						</td>
						</tr>
						<tr>
						<td nowrap class="etichetta">sede (regione o prov. autonoma)</td>
						<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getCodregione())%></b>
						</td>
						</tr>
						<tr>
						<td nowrap class="etichetta">durata</td>
						<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getDurata())%></b>
						</td>
						</tr>
						<tr>
						<td nowrap class="etichetta">(indicare in ore, giorni o mesi)</td>
						<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getCodtipologiadurata())%></b></td>
						</tr>
						<tr>
						<td nowrap class="etichetta">certificazioni ed attestazioni</td>
						<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getCertificazioniattestati())%></b></td>
						</tr>
						<tr>
						<td nowrap class="etichetta">nome azienda stage</td>
						<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getStage())%></b>
						</td>
						</tr>
						<%
					}
				}
			}
			%>
	
			<%
			   if ( allegato2.getLinguestraniereLst() != null) {
				switchEnable = true;
				List listLinStra = allegato2.getLinguestraniereLst();
				for (int i = 0; i < listLinStra.size(); i++) {
					LinguestraniereLst item = (LinguestraniereLst) listLinStra.get(i);
					List lista = item.getLinguastraniera();
					for (int j = 0; j < lista.size(); j++) {
						Linguastraniera itemTit = (Linguastraniera) lista.get(j);
				%>
				
						<%
						if (switchEnable) {
						%>
						<tr>
						<td valign=top bgcolor="#808080" colspan="2"><p align="center">
								<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
										5.3 - Lingue straniere conosciute</b></font>
							</p></td>
						</tr>
						<%
							switchEnable = false;
						}
						%>
						<tr>
						<td nowrap class="etichetta">lingua</td>
						<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getCodlingua())%>
								- <af:comboBox name="lingua_<%=itemTit.hashcode()%>"
									multiple="false" moduleName="COMBO_LINGUA" disabled="true"
									classNameBase="input"
									selectedValue="<%=Utils.notNull(itemTit.getCodlingua())%>" /></b></td>
						</tr>
						<tr>
						<td nowrap class="etichetta">letto</td>
						<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit .getCodlivelloletto())%> - <af:comboBox
									name="lingua_l1_<%=itemTit.hashcode()%>" multiple="false"
									moduleName="COMBO_LIV_LINGUA" disabled="true"
									classNameBase="input"
									selectedValue="<%=Utils.notNull(itemTit .getCodlivelloletto())%>" /></b>
						</td>
						</tr>
						<tr>
						<td nowrap class="etichetta">scritto</td>
						<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit .getCodlivelloscritto())%> - <af:comboBox
									name="lingua_l2_<%=itemTit.hashcode()%>" multiple="false"
									moduleName="COMBO_LIV_LINGUA" disabled="true"
									classNameBase="input"
									selectedValue="<%=Utils.notNull(itemTit .getCodlivelloscritto())%>" /></b>
						</td>
						</tr>
						<tr>
						<td nowrap class="etichetta">parlato</td>
						<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit .getCodlivelloparlato())%> - <af:comboBox
									name="lingua_l3_<%=itemTit.hashcode()%>" multiple="false"
									moduleName="COMBO_LIV_LINGUA" disabled="true"
									classNameBase="input"
									selectedValue="<%=Utils.notNull(itemTit .getCodlivelloparlato())%>" /></b>
						</td>
						</tr>
			
					<%
					}
				}
			}
		  if ( allegato2.getConoscenzeinformaticheLst() != null) {
				switchEnable = true;
				List listConInf = allegato2.getConoscenzeinformaticheLst();
				for (int i = 0; i < listConInf.size(); i++) {
					ConoscenzeinformaticheLst item = (ConoscenzeinformaticheLst) listConInf.get(i);
					List lista = item.getConoscenzainformatica();
					for (int j = 0; j < lista.size(); j++) {
						Conoscenzainformatica itemTit = (Conoscenzainformatica) lista.get(j);
				%>
						<%
						if (switchEnable) {
						%>
						<tr>
							<td valign=top bgcolor="#808080" colspan="2"><p align="center">
									<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
											5.4 - Conoscenze informatiche</b></font>
								</p></td>
						</tr>
						<%
							switchEnable = false;
					 	}
						%>
						<tr>
							<td nowrap class="etichetta">tipo conoscenza</td>
							<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getCodconoscenzainformatica())%> - <af:comboBox
										name="lingua_l3_<%=itemTit.hashcode()%>" multiple="false"
										moduleName="COMBO_CONSC_INF" disabled="true" classNameBase="input"
										selectedValue="<%=Utils.notNull(itemTit.getCodconoscenzainformatica())%>" /></b>
							</td>
						</tr>
						<tr>
							<td nowrap class="etichetta">livello</td>
							<td nowrap align="left" class="inputView"><b><%=Utils.notNull(itemTit.getCodgrado())%>
									- <af:comboBox name="lingua_l3_<%=itemTit.hashcode()%>"
										multiple="false" moduleName="COMBO_LIV_CONSC_INF" disabled="true"
										classNameBase="input"
										selectedValue="<%=Utils.notNull(itemTit.getCodgrado())%>" /></b></td>
						</tr>
						<tr>
							<td nowrap class="etichetta">eventuali specifiche</td>
							<td nowrap align="left" class="inputView"><%=Utils.notNull(itemTit.getSpecificheinformatica())%></td>
						</tr>
			<%
					}
				}
			}
			%>
			<%
			 if ( allegato2.getAltreinformazioniLst() != null) {
				switchEnable = true;
				List listConInf = allegato2.getAltreinformazioniLst();
				for (int i = 0; i < listConInf.size(); i++) {
					AltreinformazioniLst item = (AltreinformazioniLst) listConInf.get(i);
					List lista = item.getAltreinformazioni();
					for (int j = 0; j < lista.size(); j++) {
						Altreinformazioni itemTit = (Altreinformazioni) lista.get(j);
			%>
					<%
						if (switchEnable) {
						%>
						<tr>
							<td valign=top bgcolor="#808080" colspan="2"><p align="center">
									<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
											5.5 - Altre informazioni utili all'incontro tra domanda ed
											offerta di lavoro</b></font>
								</p></td>
						</tr>
						<%
							switchEnable = false;
						}
					 
					%>
	
					<%
						if (itemTit.getCodalbo() != null) {
						%>
						<tr>
							<td nowrap class="etichetta">iscrizione ad albi ed ordini professionali</td>
							<td nowrap align="left" class="inputView"><%=itemTit.getCodalbo()%>
								- <af:comboBox name="albo_<%=itemTit.hashcode()%>" multiple="false"
									moduleName="COMBO_ABIGEN" disabled="true" classNameBase="input"
									selectedValue="<%=Utils.notNull(itemTit.getCodalbo())%>" /></td>
						</tr>
						<%
						}
	
						if (itemTit.getCodpatenteguida() != null) {
						%>
						<tr>
							<td nowrap class="etichetta">possesso patenti</td>
							<td nowrap align="left" class="inputView"><b><%=itemTit.getCodpatenteguida()%>
									- <af:comboBox name="patente_<%=itemTit.hashcode()%>"
										multiple="false" moduleName="COMBO_ABIGEN" disabled="true"
										classNameBase="input"
										selectedValue="<%=Utils.notNull(itemTit.getCodpatenteguida())%>" /></b></td>
						</tr>
						<%
						}
						
						if (itemTit.getCodpatentino() != null) {
						%>
						<tr>
							<td nowrap class="etichetta">possesso patentini</td>
							<td nowrap align="left" class="inputView"><b><%=itemTit.getCodpatentino()%>
									- <af:comboBox name="patentino_<%=itemTit.hashcode()%>"
										multiple="false" moduleName="COMBO_ABIGEN" disabled="true"
										classNameBase="input"
										selectedValue="<%=Utils.notNull(itemTit.getCodpatentino())%>" /></b></td>
						</tr>
						<%
						}
					}
				}
			}
		}
		%>
		<%
			PoliticheAttiveLst allListaP = lavTView.getPoliticheAttiveLst();
			List listaP = null;
			if(allListaP!= null){
				listaP = allListaP.getPoliticheAttive();
			}else{
				listaP = new ArrayList();
			}
			if (listaP.size() > 0){
					switchEnable = true;
			}
			for (int j = 0; j < listaP.size(); j++) {
				PoliticheAttive item = (PoliticheAttive) listaP.get(j);
			%>
			<%
				if (switchEnable) {
				%>
				<tr>
		            <td valign=top bgcolor="#000000" colspan="2"><p align="center">
		                    <font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
		                            6 - Interventi di Politiche Attive </b></font>
		                </p></td>
		        </tr>
				<tr>
					<td valign=top bgcolor="#808080" colspan="2"><p align="center">
							<font size="2" color="#FFFFFF" face="sans-serif"><b>Sezione
									6.1 - Politica Attiva</b></font>
						</p></td>
				</tr>
				<%
					switchEnable = false;
				}
			%>
		
				<%if (j > 0) {%>
					<tr><td colspan="2">&nbsp;</td></tr>
				<%}%>
				<tr>
					<td nowrap class="etichetta">Attività</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getTipoAttivita())%>
		                    - <af:comboBox name="proj_att_<%=itemTit.hashcode()%>" multiple="false"
		                        moduleName="COMBO_ATTIVITA_POLATT" disabled="true"
		                        classNameBase="input"
		                        selectedValue="<%=Utils.notNull(item.getTipoAttivita())%>" /></b></td>
				</tr>
				<tr>
					<td nowrap class="etichetta">Denominazione</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getTitoloDenominazione())%></b>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">Data proposta</td>
					<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(item.getDataProposta())%></b></td>
				</tr>
				<tr>
					<td nowrap class="etichetta">Data inizio</td>
					<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(item.getData())%></b>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">Data fine</td>
					<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(item.getDataFine())%></b></td>
				</tr>
				<tr>
					<td nowrap class="etichetta">Descrizione</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getDescrizione())%></b>
					</td>
				</tr>
				<tr>
					<td nowrap class="etichetta">Titolo Progetto</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getTitoloProgetto())%>
							- <af:comboBox name="proj_<%=itemTit.hashcode()%>" multiple="false"
								moduleName="COMBO_TITOLOPROGETTO" disabled="true"
								classNameBase="input"
								selectedValue="<%=Utils.notNull(item.getTitoloProgetto())%>" /></b></td>
				</tr>
				<tr>
					<td nowrap class="etichetta">Codice Ente Promotore</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getCodiceEntePromotore())%>
							- <af:comboBox name="proj_ente_<%=itemTit.hashcode()%>" multiple="false"
								moduleName="COMBO_ENTETIT" disabled="true"
								classNameBase="input"
								selectedValue="<%=Utils.notNull(item.getCodiceEntePromotore())%>" /></b></td>			
				</tr>
				<%if(item.getUltimoEvento()!=null){
					DatiEventoType datiEvento = item.getUltimoEvento();	
					if(datiEvento.getDataEvento()!=null){
				%>
				<tr>
					<td nowrap class="etichetta">Data ultimo evento</td>
					<td nowrap align="left" class="inputView"><b><%=DateUtils.formatXMLGregorian(datiEvento.getDataEvento())%></b></td>		
				</tr>
				<%	} %>
					<%if(datiEvento.getEvento()!=null){ %>
					<tr> 
						<td nowrap class="etichetta">Ultimo evento</td>
						<td nowrap align="left" class="inputView"><b><%=Utils.notNull(datiEvento.getEvento())%>
								- <af:comboBox name="evento_min_" multiple="false"
									moduleName="COMBO_EVENTO_MINISTERO" disabled="true"
									classNameBase="input"
									selectedValue="<%=Utils.notNull(datiEvento.getEvento())%>" /></b></td>			
							
					</tr>
					<%}
				}%>
				<%if(item.getIdentificativoPolitica()!=null){ %>
				<tr>
					<td nowrap class="etichetta">Identificativo Politica</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getIdentificativoPolitica())%></b></td>		
				</tr>
				<%} %>
				<%if(item.getIndiceProfiling()!=null){ %>
				<tr>
					<td nowrap class="etichetta">Indice di Profiling</td>
					<td nowrap align="left" class="inputView"><b><%= item.getIndiceProfiling().toPlainString()%></b></td>			
				</tr>
				<%} %>
				<%if(item.getIdentificativoPresaInCarico()!=null){ %>
				<tr>
					<td nowrap class="etichetta">Identificativo Presa in carico</td>
					<td nowrap align="left" class="inputView"><b><%=Utils.notNull(item.getIdentificativoPresaInCarico())%></b></td>		
				</tr>
				<%}  
			}
			
	}
		%>
		</table>
		<% out.print(htmlStreamBottom);	 
		
		if (canImportaSAP) {%>
			<af:form method="POST" action="AdapterHTTP" name="Frm1">
			<input type="hidden" name="PAGE" value="ImportaSAPPage">
			<input type="hidden" name="CDNFUNZIONE" value="<%=Utils.notNull(_funzione)%>">
			<input type="hidden" name="CODMINSAP" value="<%=Utils.notNull(lavTView.getDatiinvio().getIdentificativosap())%>">
				<br>
				<center>
			   	<input type="submit" class="pulsante" name="importaSAP" value="Importa Dati">
			  	</center>
	  	 </af:form>
    	<%}%>
   
	<center>
		<input class="pulsante" type="button" name="chiudi" value="Chiudi"
			onclick="window.close()" />
	</center>
</body>

</html>
