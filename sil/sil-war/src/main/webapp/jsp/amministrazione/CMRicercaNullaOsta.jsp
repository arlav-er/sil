
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.movimenti.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 PageAttribs attributi = new PageAttribs(user, "CMRicercaNullaOstaPage");
  String fSelLav = "";
  
  String _funzione =(String) serviceRequest.getAttribute("CDNFUNZIONE");
  
  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);

  //Genero informazioni sul lavoratore se sono in un menu contestuale
  
  String strCodiceFiscaleLav = "";
  String strNomeLav = "";
  String strCognomeLav = "";
  
  String codMonoCategoria = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMONOCATEGORIA");

  //Dati per l'azienda
  String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
  String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
  String ragioneSociale = "";
  String pIva = "";
  String codFiscaleAzienda = "";
  String IndirizzoAzienda = "";
  String descrTipoAz = "";
  String codTipoAz = "";
  String codnatGiurAz = "";
  String strFlgCfOk = "";
  String strFlgDatiOk = "";
  boolean infoDatiNOLav = false;
  boolean infoDatiNOAzi = false;
  
  Vector infoLav = serviceResponse.getAttributeAsVector("M_InserisciLavNullaOsta.ROWS.ROW");
	if(infoLav != null && !infoLav.isEmpty()) { 
			SourceBean rowInfoLav = (SourceBean) infoLav.elementAt(0);
			
			strCodiceFiscaleLav = SourceBeanUtils.getAttrStrNotNull(rowInfoLav, "codFiscLav");
			strNomeLav = SourceBeanUtils.getAttrStrNotNull(rowInfoLav, "nome");
		strCognomeLav = SourceBeanUtils.getAttrStrNotNull(rowInfoLav, "cognome");
		strFlgCfOk = SourceBeanUtils.getAttrStrNotNull(rowInfoLav, "flag");
		infoDatiNOLav = true;
	}
  	
	Vector infoAzi = serviceResponse.getAttributeAsVector("M_InserisciAziendaNullaOsta.ROWS.ROW");
	if(infoAzi != null && !infoAzi.isEmpty()) { 
		SourceBean rowInfoAzi = (SourceBean) infoAzi.elementAt(0);

		codFiscaleAzienda = SourceBeanUtils.getAttrStrNotNull(rowInfoAzi, "codFiscAzi");
		ragioneSociale = SourceBeanUtils.getAttrStrNotNull(rowInfoAzi, "ragSoc");
		pIva = SourceBeanUtils.getAttrStrNotNull(rowInfoAzi, "PIva");
		IndirizzoAzienda = SourceBeanUtils.getAttrStrNotNull(rowInfoAzi, "indirizzo");
		strFlgDatiOk = SourceBeanUtils.getAttrStrNotNull(rowInfoAzi, "flagDati");
		infoDatiNOAzi = true;
  			 
}
  
  //Dati per l'azienda utilizzatrice
  String prgAziendaUt = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDAUT");
  String prgUnitaUt = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITAUT");
  String ragioneSocialeUt = "";
  String pIvaUt = "";
  String codFiscaleAziendaUt = "";
  String IndirizzoAziendaUt = "";
  String descrTipoAzUt = "";
  String codTipoAzUt = "";
  String codnatGiurAzUt = "";
  String strFlgCfOkUt = "";
  String strFlgDatiOkUt = "";

  
String PROVINCIA_ISCR = serviceRequest
		.getAttribute("PROVINCIA_ISCR") == null ? ""
		: (String) serviceRequest
		.getAttribute("PROVINCIA_ISCR");

  
  if (!cdnLavoratore.equals("")) {
    //Oggetto per la generazione delle informazioni sul lavoratore
    InfoLavoratore datiLav = new InfoLavoratore(new BigDecimal(cdnLavoratore));
    strCodiceFiscaleLav = datiLav.getCodFisc();
    strNomeLav = datiLav.getNome();
    strCognomeLav = datiLav.getCognome();
    strFlgCfOk = datiLav.getFlgCfOk();
    if (strFlgCfOk!=null){
      if (strFlgCfOk.equalsIgnoreCase("S")){
        strFlgCfOk = "Si";
      }else
          if (strFlgCfOk.equalsIgnoreCase("N")){
            strFlgCfOk = "No";
          }
    }
  }
  if (!prgAzienda.equals("") && !prgUnita.equals("")){
    InfCorrentiAzienda currAz = new InfCorrentiAzienda(prgAzienda,prgUnita);
    ragioneSociale = currAz.getRagioneSociale();
    pIva = currAz.getPIva();
    codFiscaleAzienda = currAz.getCodiceFiscale();
    IndirizzoAzienda = currAz.getIndirizzo();
    descrTipoAz = currAz.getDescrTipoAz();
    codTipoAz = currAz.getTipoAz();
    codnatGiurAz = currAz.getCodNatGiurAz();
    strFlgDatiOk = currAz.getFlgDatiOk();
    if (strFlgDatiOk!=null) {
        if (strFlgDatiOk.equalsIgnoreCase("S")){
          strFlgDatiOk = "Si";
        }else
            if (strFlgDatiOk.equalsIgnoreCase("N")){
              strFlgDatiOk = "No";
            }
    }
  }
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>

<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
<script language="Javascript">
<% 
//Genera il Javascript che si occuperà di inserire i links nel footer
attributi.showHyperLinks(out, requestContainer,responseContainer,"");
%>
</SCRIPT>

<%@ include file="MovimentiRicercaSoggetto.inc" %>
<%@ include file="MovimentiSezioniATendina.inc" %>
<%@ include file="Common_Function_Mov.inc" %>

<script type="text/javascript">
	
	//Variabili per la memorizzazione dell'azienda e lavoratore corrente per le GET
	var prgAzienda = '<%=prgAzienda%>';
	var prgUnita = '<%=prgUnita%>';
	var cdnLavoratore = '<%=cdnLavoratore%>';
	var prgAziendaUt = '<%=prgAziendaUt%>';
	var prgUnitaUt = '<%=prgUnitaUt%>';
	
	
    function aggiornaAzienda(){
        document.Frm1.ragioneSociale.value = opened.dati.ragioneSociale;
        document.Frm1.pIva.value = opened.dati.partitaIva;
        document.Frm1.codFiscaleAzienda.value = opened.dati.codiceFiscaleAzienda;
        document.Frm1.IndirizzoAzienda.value = opened.dati.strIndirizzoAzienda + " (" + opened.dati.comuneAzienda + ")";
        document.Frm1.PRGAZIENDA.value = opened.dati.prgAzienda;
        document.Frm1.PRGUNITA.value = opened.dati.prgUnita;
        document.Frm1.codTipoAz.value = opened.dati.codTipoAz;
        document.Frm1.descrTipoAz.value = opened.dati.descrTipoAz;
        document.Frm1.FLGDATIOK.value = opened.dati.FLGDATIOK;
        prgAzienda = opened.dati.prgAzienda;
        prgUnita = opened.dati.prgUnita;
        if ( document.Frm1.FLGDATIOK.value == "S" ){ 
                    document.Frm1.FLGDATIOK.value = "Si";
        }else 
              if ( document.Frm1.FLGDATIOK.value != "" ){
                document.Frm1.FLGDATIOK.value = "No";
              }
        document.Frm1.CODNATGIURIDICA.value = opened.dati.codNatGiurAz;
        opened.close();
        var imgV = document.getElementById("tendinaAzienda");
        cambiaLavMC("aziendaSez","inline");
        imgV.src=imgAperta;
        
    }
    
    function aggiornaAziendaUt(){
    
        document.Frm1.ragioneSocialeUt.value = opened.dati.ragioneSociale;
        document.Frm1.pIvaUt.value = opened.dati.partitaIva;
        document.Frm1.codFiscaleAziendaUt.value = opened.dati.codiceFiscaleAzienda;
        document.Frm1.IndirizzoAziendaUt.value = opened.dati.strIndirizzoAzienda + " (" + opened.dati.comuneAzienda + ")";
        document.Frm1.PRGAZIENDAUt.value = opened.dati.prgAzienda;
        document.Frm1.PRGUNITAUt.value = opened.dati.prgUnita;        
        document.Frm1.codTipoAzUt.value = opened.dati.codTipoAz;
        document.Frm1.descrTipoAzUt.value = opened.dati.descrTipoAz;
        document.Frm1.FLGDATIOKUt.value = opened.dati.FLGDATIOK;
        prgAziendaUt = opened.dati.prgAzienda;
        prgUnitaUt = opened.dati.prgUnita;
        if ( document.Frm1.FLGDATIOKUt.value == "S" ){ 
                    document.Frm1.FLGDATIOKUt.value = "Si";
        }else 
              if ( document.Frm1.FLGDATIOKUt.value != "" ){
                document.Frm1.FLGDATIOKUt.value = "No";
              }
        document.Frm1.CODNATGIURIDICAUt.value = opened.dati.codNatGiurAz;
        opened.close();
        var imgV = document.getElementById("tendinaAziendaUt");
        cambiaLavMC("aziendaSezUt","inline");
        imgV.src=imgAperta;
    }

    
    function selLavIscrCM(context, funzSetLav)
	{
		var url = "AdapterHTTP?PAGE=SelLavNOPage&fromWhere=" + context + "&AGG_FUNZ=" + funzSetLav + "&cdnFunzione=<%=_funzione%>";
		var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
		     	   "width=600,height=500,top=50,left=100";
    	opened = window.open(url, "_blank", feat);
    	opened.focus();
	}
	
	function riempiDatiLavIscrCM(cdnLavoratore, strCodiceFiscaleLav, strCognome, strNome, flgCfOk) {
	  	document.Frm1.CDNLAVORATORE.value = cdnLavoratore;
	  	document.Frm1.codiceFiscaleLavoratore.value = strCodiceFiscaleLav;
	  	document.Frm1.cognome.value = strCognome;
	  	document.Frm1.nome.value = strNome;
	  	document.Frm1.FLGCFOK.value = flgCfOk;
	  	if ( document.Frm1.FLGCFOK.value == "S" ){ 
              document.Frm1.FLGCFOK.value = "Si";
        }else if ( document.Frm1.FLGCFOK.value != "" ){
              document.Frm1.FLGCFOK.value = "No";
        }
        opened.close();
        var imgV = document.getElementById("tendinaLav");
        cambiaLavMC("lavoratoreSez","inline");
        imgV.src=imgAperta;
    }
	
	function azzeraLavIscrCM(){
		document.Frm1.CDNLAVORATORE.value = "";
      	document.Frm1.codiceFiscaleLavoratore.value = "";
        document.Frm1.cognome.value = "";
        document.Frm1.nome.value = "";
        document.Frm1.FLGCFOK.value = "";
        var imgV = document.getElementById("tendinaLav");
        cambiaLavMC("lavoratoreSez","none");
        imgV.src=imgChiusa;
        imgV.alt = "Apri";
    }

	function ControllaDate(){
    	var dataDa = document.Frm1.Datinizio;
    	var dataA = document.Frm1.DataFine; 
    	if ((dataDa.value != "") && (dataA.value != "")) {
      		if (compareDate(dataDa.value,dataA.value) > 0) {
      			alert(dataDa.title + " maggiore di " + dataA.title);
      			dataDa.focus();
	    		return false;
	  		}	
	  	}
	  	return true;
  	}
	  		
</script>
</head>

	<body class="gestione" onload="rinfresca();">
  <br/>
  <p class="titolo">Ricerca Nulla Osta</p>
  <br/>
	<center>
	<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="ControllaDate()">

   
    <input type="hidden" name="PAGE" value="CMListaNullaOstaPage"/>
    <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
    <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
    <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
    <input type="hidden" name="PRGAZIENDAUt" value="<%=prgAziendaUt%>"/>
    <input type="hidden" name="PRGUNITAUt" value="<%=prgUnitaUt%>"/>
   
    <input type="hidden" name="infoDatiNOLav" value="<%=infoDatiNOLav%>"/>
    <input type="hidden" name="infoDatiNOAzi" value="<%=infoDatiNOAzi%>"/>
    
    <input type="hidden" name="goBackListPage" value="CMRicercaNullaOstaPage" />
   
<%out.print(htmlStreamTop);%>     
	<table class="main" border="0">
    	<!-- sezione lav se si proviene dal menu contestuale del lav. -->
    	<tr class="note">
    		<td colspan="2">
      		<div class="sezione2">
          		<img id='tendinaLav' alt='Chiudi' src='../../img/aperto.gif' onclick="cambiaTendina(this,'lavoratoreSez', document.Frm1.nome);" />&nbsp;&nbsp;&nbsp;Lavoratore
      			&nbsp;&nbsp;
      		<% if (cdnLavoratore.equals("")){ %>
        		<a href="#" onClick="javascript:selLavIscrCM('ricerca','riempiDatiLavIscrCM');return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
      			&nbsp;<a href="#" onClick="javascript:azzeraLavIscrCM();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
      		<%}%>
        	</div>
    		</td>
		</tr>
		<tr>
  			<td colspan="2">
    		<div id="lavoratoreSez" style="display: none;">
      		<table class="main" width="100%" border="0">
          	<tr>
            	<td class="etichetta">Codice Fiscale</td>
            	<td class="campo" valign="bottom">
              		<af:textBox classNameBase="input" type="text" name="codiceFiscaleLavoratore" readonly="true" value="<%=strCodiceFiscaleLav%>" size="30" maxlength="16"/>
              		&nbsp;&nbsp;&nbsp;Validità C.F.&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGCFOK" readonly="true" value="<%=strFlgCfOk%>" size="3" maxlength="3"/>
            	</td>
          	</tr>
          	<tr >
            	<td class="etichetta">Cognome</td>
            	<td class="campo">
              		<af:textBox classNameBase="input" type="text" name="cognome" readonly="true" value="<%=strCognomeLav%>" size="30" maxlength="50"/>
            	</td>
          	</tr>
         	<tr>
            	<td class="etichetta">Nome</td>
            	<td class="campo">
              		<af:textBox classNameBase="input" type="text" name="nome" readonly="true" value="<%=strNomeLav%>" size="30" maxlength="50"/>
            	</td>
          	</tr>
      		</table>
    		</div>
  			</td>
		</tr>
		<!-- sezione azienda -->
        <tr class="note">
          <td colspan="2">
          <div class="sezione2">
            <img id='tendinaAzienda' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendina(this,'aziendaSez',document.Frm1.pIva);"/>&nbsp;&nbsp;&nbsp;Azienda
          &nbsp;&nbsp;
          <% if (prgAzienda.equals("") && prgUnita.equals("")){%>
            <a href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
            &nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
          <%}%>
          </div>
          </td>
        <tr>
          <td colspan="2">
            <div id="aziendaSez" style="display: none;">
              <table class="main" width="100%" border="0">
                  <tr>
                    <td class="etichetta">Codice Fiscale</td>
                    <td class="campo">
                      <af:textBox classNameBase="input" type="text" name="codFiscaleAzienda" readonly="true" value="<%=codFiscaleAzienda%>" size="30" maxlength="16"  />                       
                    </td>
                  </tr>
                  <tr>
                    <td class="etichetta">Partita IVA</td>
                    <td class="campo">
                      <af:textBox classNameBase="input" type="text" name="pIva" readonly="true" value="<%=pIva%>" size="30" maxlength="11"/>
                      &nbsp;&nbsp;&nbsp;Validità C.F./P. IVA&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGDATIOK" readonly="true" value="<%=strFlgDatiOk%>" size="3" maxlength="3"/>
                    </td>
                  </tr>
                  <tr>
                    <td class="etichetta">Ragione Sociale</td>
                    <td class="campo">
                      <af:textBox classNameBase="input" type="text" name="ragioneSociale" readonly="true" value="<%=ragioneSociale%>" size="60" maxlength="100"/>
                    </td>
                  </tr>
                  <tr>
                    <td class="etichetta">Indirizzo (Comune)</td>
                    <td class="campo">
                      <af:textBox classNameBase="input" type="text" name="IndirizzoAzienda" readonly="true" value="<%=IndirizzoAzienda%>" size="60" maxlength="100"/>
                    </td>
                  </tr>
                  <tr>
                    <td class="etichetta">Tipo Azienda</td>
                    <td class="campo">
                      <af:textBox classNameBase="input" type="text" name="descrTipoAz" readonly="true" value="<%=descrTipoAz%>" size="30" maxlength="30"/>
                      <af:textBox classNameBase="input" type="hidden" name="codTipoAz" readonly="true" value="<%=codTipoAz%>" size="10" maxlength="10"/>
                      <af:textBox classNameBase="input" type="hidden" name="CODNATGIURIDICA" readonly="true" value="<%=codnatGiurAz%>" size="10" maxlength="10"/>
                    </td>
                  </tr>
                </table>
            </div>
        </td>
      </tr>
    <tr class="note">
      <td colspan="2">
      <div class="sezione2">
        <img id='tendinaAziendaUt' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendina(this,'aziendaSezUt',document.Frm1.pIvaUt);"/>&nbsp;&nbsp;&nbsp;Azienda utilizzatrice
      &nbsp;&nbsp;
      <% if (prgAziendaUt.equals("") && prgUnitaUt.equals("")){%>
        <a href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAziendaUt','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
      <%}%>
      &nbsp;<a href="#" onClick="javascript:azzeraAziendaUt();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
      </div>
      </td>
	    <tr>
	      <td colspan="2">
	        <div id="aziendaSezUt" style="display: none;">
	          <table class="main" width="100%" border="0">
	              <tr>
	                <td class="etichetta">Codice Fiscale</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="codFiscaleAziendaUt" readonly="true" value="<%=codFiscaleAziendaUt%>" size="30" maxlength="16"/>                       
	                </td>
	              </tr>
	              <tr>
	                <td class="etichetta">Partita IVA</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="pIvaUt" readonly="true" value="<%=pIvaUt%>" size="30" maxlength="11"/>
	                  &nbsp;&nbsp;&nbsp;Validità C.F./P. IVA&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGDATIOKUt" readonly="true" value="<%=strFlgDatiOkUt%>" size="3" maxlength="3"/>
	                </td>
	              </tr>
	              <tr>
	                <td class="etichetta">Ragione Sociale</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="ragioneSocialeUt" readonly="true" value="<%=ragioneSocialeUt%>" size="60" maxlength="100"/>
	                </td>
	              </tr>
	              <tr>
	                <td class="etichetta">Indirizzo (Comune)</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="IndirizzoAziendaUt" readonly="true" value="<%=IndirizzoAziendaUt%>" size="60" maxlength="100"/>
	                </td>
	              </tr>
	              <tr>
	                <td class="etichetta">Tipo Azienda</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="descrTipoAzUt" readonly="true" value="<%=descrTipoAzUt%>" size="30" maxlength="30"/>
	                  <af:textBox classNameBase="input" type="hidden" name="codTipoAzUt" readonly="true" value="<%=codTipoAzUt%>" size="10" maxlength="10"/>
	                  <af:textBox classNameBase="input" type="hidden" name="CODNATGIURIDICAUt" readonly="true" value="<%=codnatGiurAzUt%>" size="10" maxlength="10"/>
	                </td>
	              </tr>
	            </table>
	        </div>
	    </td>
	  </tr>
       <tr><td class="etichetta">&nbsp;&nbsp;</td></tr>   
		<tr>
		    <td class="etichetta">Ambito Territoriale</td>
		    <td colspan=3 class="campo">
		    	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
		        	classNameBase="input" addBlank="true" />
		    </td>
		</tr>	
       <tr>
      	<td class="etichetta">Data dal</td>
		<td class="campo">
			<af:textBox type="date" title="Data dal" name="Datinizio" size="12" maxlength="10" validateOnPost="true"/>
			 &nbsp;&nbsp;al&nbsp;&nbsp;
			 <af:textBox type="date" title="Data al" name="DataFine" size="12" maxlength="10" validateOnPost="true"/>
		</td>
		</tr>  
		 <tr>
          <td class="etichetta">Tipologia nulla osta</td>
          <td class="campo" >
		  	<af:comboBox title="Tipologia nulla osta" name="CODMONOTIPO" addBlank="true">
            	<OPTION value="M">NOMINATIVA</OPTION>
            	<OPTION value="R">NUMERICA</OPTION>
        	</af:comboBox>
		  </td>
        </tr> 
         <tr>
          <td class="etichetta">Stato dell'atto</td>
          <td class="campo" >
		  	<af:comboBox title="Stato dell'atto" name="StatoAtto" addBlank="true">
            	<OPTION value="NP">NON PROTOCOLLATO</OPTION>
            	<OPTION value="PR">PROTOCOLLATO</OPTION>
            	<OPTION value="AN">ANNULLATO</OPTION>
        	</af:comboBox>
		  </td>
        </tr>
        <tr>
			<td class="etichetta" >Categoria del lavoratore</td>
			<td class="campo" >
				<af:comboBox title="Categoria del lavoratore" name="CODMONOCATEGORIA" addBlank="true">
            		<OPTION value="D" <% if (codMonoCategoria.equals("") || codMonoCategoria.equals("D")) {%>selected="true" <%}%> >DISABILE</OPTION>
            		<OPTION value="A" <% if (codMonoCategoria.equals("A")) {%>selected="true" <%}%> >CATEGORIA PROTETTA EX ART.18</OPTION>
            	</af:comboBox>
			</td>
		</tr>        
      	
 		<tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          <td colspan="2" align="center">
          <input type="submit" class="pulsanti"  name="cerca" value="Cerca" />
          &nbsp;&nbsp;
          <input type="reset" class="pulsanti" value="Annulla" />
          &nbsp;&nbsp;
	      </td>
        </tr>
      	</table>
      <%out.print(htmlStreamBottom);%>
      </af:form>
	</center>
	<script language="javascript">
  var imgV = document.getElementById("tendinaLav");
  <% if (!strNomeLav.equals("")){%>
    cambiaLavMC("lavoratoreSez","inline");
    imgV.src = imgAperta;
    imgV.alt="Chiudi";
  <%} else {%>
    cambiaTendina(imgV,"lavoratoreSez",document.Frm1.nome);
  <%}%>
        
  imgV = document.getElementById("tendinaAzienda");
  <% if (!pIva.equals("")){%>
    cambiaLavMC("aziendaSez","inline");
    imgV.src = imgAperta;
    imgV.alt="Chiudi";
  <%} else {%>
    cambiaTendina(imgV,"aziendaSez",document.Frm1.pIva)
  <%}%>

  imgV = document.getElementById("tendinaAziendaUt");
  <% if (!pIvaUt.equals("")){%>
    cambiaLavMC("aziendaSezUt","inline");
    imgV.src = imgAperta;
    imgV.alt="Chiudi";
  <%} else {%>
    cambiaTendina(imgV,"aziendaSezUt",document.Frm1.pIvaUt)
  <%}%>

 
</script> 
</body>
</html>
