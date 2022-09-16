<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  java.util.*,
                  java.math.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*" %>
      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="patto" prefix="pt" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%!
	// Semplice decodifica Si'/No (se possibile)
	private String decodeFlagSN(String flag) {
		if (flag != null) {
			if (flag.equalsIgnoreCase("N")) return "No";
			if (flag.equalsIgnoreCase("S")) return "S&igrave;";
		}
		return flag;
	}
%>

<% 
   Vector row_InfoCorr      	= serviceResponse.getAttributeAsVector("M_GetInfoCorrPopUp.ROWS.ROW");
   Vector row_obbligoForma  	= serviceResponse.getAttributeAsVector("M_GETOBBLIGOFORMATIVO.ROWS.ROW");
   Vector statoOccRows      	= serviceResponse.getAttributeAsVector("M_GETSTATOOCCUPAZIONALE.ROWS.ROW");
   Vector permSoggiornoRows 	= serviceResponse.getAttributeAsVector("M_GETEXPERMSOGGIORNO.ROWS.ROW");
   Vector mobilitaRows      	= serviceResponse.getAttributeAsVector("M_GETMOBILITAISCR.ROWS.ROW");
   Vector collMiratoRowsTipoA   = serviceResponse.getAttributeAsVector("CM_GET_DETT_AM_CM_ISCR_ULTIMA_TIPO_A.ROWS.ROW");
   Vector collMiratoRowsTipoD   = serviceResponse.getAttributeAsVector("CM_GET_DETT_AM_CM_ISCR_ULTIMA_TIPO_D.ROWS.ROW");
   Vector indispTempRows    	= serviceResponse.getAttributeAsVector("M_GETINDISPTEMP.ROWS.ROW");
   Vector movimentiRows     	= serviceResponse.getAttributeAsVector("M_GETMOVIMENTO.ROWS.ROW");
   Vector titoloStudioRows  	= serviceResponse.getAttributeAsVector("M_GETLAVORATORETITOLOPRINC.ROWS.ROW");
   Vector formazProfRows    	= serviceResponse.getAttributeAsVector("M_GETINFOCORRCORSOLAV.ROWS.ROW");   
   Vector infoAnnotazRows   	= serviceResponse.getAttributeAsVector("M_GetInfoAnnotazioni.ROWS.ROW");
   Vector infoAltreIscrRows   	= serviceResponse.getAttributeAsVector("M_GetInfoCorrAltreIscr.ROWS.ROW");

   String cognomeLav = null;//(String) serviceRequest.getAttribute("cognome");
   String nomeLav    = null;//(String) serviceRequest.getAttribute("nome");



   SourceBean row            =null;
   String     cdnLavoratore  =null;

   String datInsElAnagr     = null;
   String cpiTitolare       = null;
   
   String obblForma_flg      =null; 
   String obblScolastico_flg =null; 
   String descMod            =null;
   
   String statoOccDesc     = null;
   String cod181           = null;
   String cat181Desc       = null;
   String dataInizioSO     = null;
   String dataFine         = null;
   String indennizzo       = null;
   String pensionato       = null;
   String strNumeroAtto    = null;
   String dataAtto         = null;
   String dataRichRevisione= null;
   String dataRicGiurisdz  = null;
   String dataAnzDisoc     = null;
   String statoAtDescr     = null;
   BigDecimal redditoStr   = null;
   BigDecimal mesiAnz      = null;
   BigDecimal numMesiSosp  = null;
   
   String datScad         = null;
   String datRichiesta    = null;
   String motivoRilDesc   = null;
   String statoRicDesc    = null;

   String dataInizioMob   = null;
   String dataFineMob     = null;
   String mobTipoDesc     = null;
   String indennita_flg   = null;

   String dataInizioCMD      = null;
   String tipoIscrDescrD     = null;
   String tipoInvalDescrD    = null;
   BigDecimal percInvalD     = null;
   String catIscrDescrD      = null;

   String dataInizioCMA      = null;
   String tipoIscrDescrA     = null;
   String tipoInvalDescrA    = null;
   BigDecimal percInvalA     = null;
   String catIscrDescrA      = null;

   String descrizione       = null;
   String datInizioIndTemp  = null;
   String datFineIndTemp    = null;
   String codIndTempLetto   = null;

   String esperienza   = null;
   String mansione     = null;
   String dataInizMov  = null;
   String dataFineMov  = null;
   String motivoCessazione  = null;
   BigDecimal retribuzione = null;
   String strRagSociale = null;

   String titoloStud      = null;
   String titoloStudTipo  = null;
   BigDecimal annoTitStud = null;

   String corso         = null;
   String complCorso    = null;
   BigDecimal annoCorso = null;
   
   String annotazCpi    = null;
   
   String readOnlyStr   ="false";

  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
   
String codiceFiscale = null;

String qs = request.getParameter("QUERY_STRING");
%>
<%PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID),(String) serviceRequest.getAttribute("PAGE"));%>
<%
cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
// POPUP EVIDENZE
String cdnFunzione = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNFUNZIONE");
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
if((cdnFunzione!=null) && !cdnFunzione.equals("")) { _fun = Integer.parseInt(cdnFunzione); }
EvidenzePopUp jsEvid = null;

boolean bevid = pageAtts.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid) {
	jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
}	
%> 

<html>
<head>
<title>Informazioni Correnti</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<%if(qs!=null && !qs.equals("")) {%>
	<script language="Javascript">
	function indietro(){
		window.location="AdapterHTTP?<%=qs%>";		
	}
	</script>
<%}%>

<%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>

<af:linkScript path="../../js/"/>

</head>

<body class="gestione" 
	onload="<%if(qs!=null && !qs.equals("")) {%>rinfresca();<%}%><%if(strApriEv.equals("1") && bevid) { %> apriEvidenze() <%}%>"
>
<%
 

 //InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

    if(cdnLavoratore != null) {
        if(row_InfoCorr != null && !row_InfoCorr.isEmpty())  { 
            row   = (SourceBean) row_InfoCorr.elementAt(0);
            datInsElAnagr  = Utils.notNull(row.getAttribute("DATINIZIO"));
            cpiTitolare    = Utils.notNull(row.getAttribute("STRDESCRIZIONE"));
            codiceFiscale = Utils.notNull(row.getAttribute("STRCODICEFISCALE"));
            cognomeLav = Utils.notNull(row.getAttribute("STRCOGNOME"));
            nomeLav = Utils.notNull(row.getAttribute("STRNOME"));
        }
        if(row_obbligoForma != null && !row_obbligoForma.isEmpty())  { 
            row   = (SourceBean) row_obbligoForma.elementAt(0);
            obblForma_flg      = (String) row.getAttribute("FLGOBBLIGOFORMATIVO");
            obblScolastico_flg = (String) row.getAttribute("FLGOBBLIGOSCOLASTICO");
            descMod            = (String) row.getAttribute("DESCRIZIONE");
        }
   
        if(statoOccRows != null && !statoOccRows.isEmpty()) {  
            row = (SourceBean) statoOccRows.firstElement();   
            dataInizioSO    = (String)      row.getAttribute("DATINIZIO");
            dataFine        = (String)      row.getAttribute("DATFINE");
            statoOccDesc    = (String)      row.getAttribute("DESCRIZIONESTATO");
            cod181          = (String)      row.getAttribute("CODCATEGORIA181");
            cat181Desc      = (String)      row.getAttribute("DESCRIZIONE181");
            indennizzo      = (String)      row.getAttribute("FLGINDENNIZZATO");
            pensionato      = (String)      row.getAttribute("FLGPENSIONATO");
            numMesiSosp     = (BigDecimal)  row.getAttribute("NUMMESISOSP");
            redditoStr      = (BigDecimal)  row.getAttribute("NUMREDDITO");
            dataAnzDisoc    = (String)      row.getAttribute("DATANZIANITADISOC");
            mesiAnz         = (BigDecimal)  row.getAttribute("MESI_ANZ");
            strNumeroAtto   = (String)      row.getAttribute("STRNUMATTO");
            dataAtto        = (String)      row.getAttribute("DATATTO");
            dataRichRevisione=(String)      row.getAttribute("DATRICHREVISIONE");
            dataRicGiurisdz = (String)      row.getAttribute("DATRICORSOGIURISDIZ");
            statoAtDescr    = (String)      row.getAttribute("DESCRIZIONERICH");     
        }

        if( permSoggiornoRows != null && !permSoggiornoRows.isEmpty())   { 
            row  = (SourceBean) permSoggiornoRows.elementAt(0);
            datScad         = (String)      row.getAttribute("DATSCADENZA");
            datRichiesta    = (String)      row.getAttribute("DATRICHIESTA");
            motivoRilDesc   = (String)      row.getAttribute("DESCRIZIONEMOT");
            statoRicDesc    = (String)      row.getAttribute("DESCRIZIONERICH");
        }

        if(mobilitaRows != null && !mobilitaRows.isEmpty())  { 
            row  = (SourceBean) mobilitaRows.elementAt(0);
            dataInizioMob  = (String)  row.getAttribute("DATINIZIO"); 
            dataFineMob    = (String)  row.getAttribute("DATFINE");
            mobTipoDesc    = (String)  row.getAttribute("DESCRIZIONE");
            indennita_flg  = (String)  row.getAttribute("FLGINDENNITA");
        }

        if(collMiratoRowsTipoA != null && !collMiratoRowsTipoA.isEmpty())  {   
 			row  = (SourceBean) collMiratoRowsTipoA.elementAt(0); 
            dataInizioCMA     = (String)     row.getAttribute("DATINIZIO"); 
            tipoIscrDescrA    = (String)     row.getAttribute("TIPOISCR");
            tipoInvalDescrA   = (String)     row.getAttribute("TIPOINV");
            percInvalA        = (BigDecimal) row.getAttribute("NUMPERCINVALIDITA");
            catIscrDescrA     = (String)     row.getAttribute("CATEGORIAISCR");
        }

        if(collMiratoRowsTipoD != null && !collMiratoRowsTipoD.isEmpty())  {   
 			row  = (SourceBean) collMiratoRowsTipoD.elementAt(0); 
            dataInizioCMD     = (String)     row.getAttribute("DATINIZIO"); 
            tipoIscrDescrD    = (String)     row.getAttribute("TIPOISCR");
            tipoInvalDescrD   = (String)     row.getAttribute("TIPOINV");
            percInvalD        = (BigDecimal) row.getAttribute("NUMPERCINVALIDITA");
            catIscrDescrD     = (String)     row.getAttribute("CATEGORIAISCR");
        }

        if(titoloStudioRows != null && !titoloStudioRows.isEmpty())  {   
            row  = (SourceBean) titoloStudioRows.elementAt(0);
            titoloStud     = (String)     row.getAttribute("destitolo");
            titoloStudTipo = (String)     row.getAttribute("destipotitolo");
            annoTitStud    = (BigDecimal) row.getAttribute("NUMANNO");
        }
        
        if(infoAnnotazRows != null && !infoAnnotazRows.isEmpty())  {   
            row  = (SourceBean) infoAnnotazRows.elementAt(0);
            annotazCpi      = (String) row.getAttribute("TXTNOTECPI");
        }
        
%>
<p align="center"> 


<table class="main">
<tr><td width="30%"></td> <td></td> <td  width="30%"></td> <td></td> </tr>
<tr>
  <td colspan="4"><%@ include file="_testata_semplice_lavoratore.inc" %></td>
</tr>

 
<tr>
   <td colspan="4">
    <p class="titolo">Situazione corrente lavoratore</p><br/>
   </td></tr>
</table>

<%out.print(htmlStreamTop);%>
<table class="main">
<tr>
  <td class="etichetta">Data inserimento nell'elenco anagrafico</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(datInsElAnagr)%></td>
</tr>
<tr>
  <td class="etichetta">CpI competente</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(cpiTitolare)%></td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>


<pt:sections pageAttribs="<%= pageAtts%>">


    <pt:section name="AM_OBBFO">
<tr>
    <td colspan="4"><br/><div class="sezione2">Notizie sul diritto/dovere di istruzione e formazione</div>
</td></tr>
<%if (row_obbligoForma!=null && row_obbligoForma.size()>0) {%>
<tr>
    <td class="etichetta">Diritto/dovere di istruzione assolto</td>
    <td class="campo_readFree"><%=Utils.notNull(decodeFlagSN(obblScolastico_flg))%></td>
    
    <td class="etichetta">Diritto/dovere di formazione assolto</td>
    <td class="campo_readFree"><%=Utils.notNull(decodeFlagSN(obblForma_flg))%></td>
</tr>
<tr>
    <td class="etichetta" >Modalita di assolvimento obbligo</td>
    <td class="campo_readFree" colspan="3"><%=Utils.notNull(descMod)%></td>
</tr>
<%}%>
    </pt:section>
    <pt:section name="AM_S_OCC">
<tr>
  <td colspan="4"><br/><div class="sezione2">Notizie sullo stato occupazionale</div></td>
</tr>
<%if (statoOccRows!=null && statoOccRows.size()>0){%>
<tr>
  <td class="etichetta">Inizio</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(dataInizioSO)%></td>
</tr>
<tr>
  <td class="etichetta" >Stato occupazionale</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(statoOccDesc)%></td>
</tr>
<!--
<tr>
  <td class="etichetta" nowrap>Categoria dlg&nbsp;181 </td>
  <td class="campo_readFree"><%=Utils.notNull(cat181Desc)%></td>
  <td class="etichetta">Pensionato </td>
  <td class="campo_readFree"><%=Utils.notNull(pensionato)%></td>
</tr>
<tr>
  <td class="etichetta">Redditotd>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(redditoStr)%></td>
</tr>
<tr>
  <td colspan="4">
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td style="text-align:right;font-weight:bold" width="29%"><br/><b>Anzianit&agrave; di disoccupazione</b></td>
			<td>&nbsp;</td>
		</tr>
	</table>
  </td>
</tr>
<tr>
  <td class="etichetta" >dal</td>
  <td class="campo_readFree"><%=Utils.notNull(dataAnzDisoc)%></td>
  <td class="etichetta">mesi&nbsp;sosp.</td>
  <td class="campo_readFree"><%=Utils.notNull(numMesiSosp)%></td>
</tr>
<tr>
  <td class="etichetta">Mesi anzianit&agrave;</td>
  <td class="campo_readFree"><%=Utils.notNull(mesiAnz)%></td>
  <td class="campo_readFree" colspan="2">
           <%if(mesiAnz != null )
            { if(mesiAnz.compareTo(new BigDecimal(12))>0 || ( mesiAnz.compareTo(new BigDecimal(6)) > 0 && (Utils.notNull(cod181)).equalsIgnoreCase("G") ))
//            { if(mesiAnz.compareTo(new BigDecimal(12))>0 || ( mesiAnz.compareTo(new BigDecimal(6)) > 0 && cod181.equalsIgnoreCase("G") ))
                 {%>disoccupato/inoccupato di lunga durata
               <%}
               if(mesiAnz.compareTo(new BigDecimal(24)) > 0)
                 {%>soggetto alla legge&nbsp;407/90
               <%}
            }%>
    </td>
</tr>
<tr>
  <td class="etichetta" >Indennizzato</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(indennizzo)%></td>
</tr>
-->
<%}%>
    </pt:section>
    <pt:section name="AM_EX_PS">
<tr>
    <td colspan="4"><br/><div class="sezione2">Notizie sui cittadini stranieri</div></td>
</tr>
<%if (permSoggiornoRows!=null && permSoggiornoRows.size()>0){
		row= (SourceBean)permSoggiornoRows.get(0);
%>
<tr>
  <td class="etichetta">Tipologia</td>
  <td class="campo_readFree" colspan="3"><%= Utils.notNull(row.getAttribute("statusDescr"))%></td>
</tr>
<tr>
  <td class="etichetta">Scadenza documento</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(datScad)%></td>
</tr>
<tr>
  <td class="etichetta">Data richiesta/Sanatoria</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(datRichiesta)%></td>
</tr>
<tr>
  <td class="etichetta">Stato richiesta</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(statoRicDesc)%></td>
</tr>
<tr>
  <td class="etichetta">Motivo richiesta</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(motivoRilDesc)%></td>
</tr>
<%}%>
    </pt:section>
    <pt:section name="AM_CM_IS">
<tr>
    <td colspan="4"><br/><div class="sezione2">Liste speciali: collocamento mirato</div></td>
</tr>
<%if (collMiratoRowsTipoD!=null &&collMiratoRowsTipoD.size()>0){%>    
<tr>
   <td class="etichetta" >Data Inizio </td>
   <td class="campo_readFree" colspan="3"><%=Utils.notNull(dataInizioCMD)%></td>
</tr>
<tr>
   <td class="etichetta" >Categoria </td>
   <td class="campo_readFree"><%=Utils.notNull(catIscrDescrD)%></td>
   <td class="etichetta" >Tipo </td>
   <td class="campo_readFree"><%=Utils.notNull(tipoIscrDescrD)%></td>   
</tr>
<tr>
   <td class="etichetta" >Tipo invalidit&agrave;</td>
   <td class="campo_readFree"><%=Utils.notNull(tipoInvalDescrD)%></td>
   <td class="etichetta" >Percentuale invalidit&agrave;</td>
   <td class="campo_readFree"><%=Utils.notNull(percInvalD)%></td>
</tr>
<%} 
if ((collMiratoRowsTipoA!=null &&collMiratoRowsTipoA.size()>0) && (collMiratoRowsTipoD!=null &&collMiratoRowsTipoD.size()>0)){ %>
<tr>
	<td>&nbsp;</td>
</tr>
<tr>
	<td>&nbsp;</td>
</tr>
<%} 
if (collMiratoRowsTipoA!=null &&collMiratoRowsTipoA.size()>0){%>    
<tr>
   <td class="etichetta" >Data Inizio </td>
   <td class="campo_readFree" colspan="3"><%=Utils.notNull(dataInizioCMA)%></td>
</tr>
<tr>
   <td class="etichetta" >Categoria </td>
   <td class="campo_readFree"><%=Utils.notNull(catIscrDescrA)%></td> 
   <td class="etichetta" >Tipo </td>
   <td class="campo_readFree"><%=Utils.notNull(tipoIscrDescrA)%></td>  
</tr>
<tr>
   <td class="etichetta" >Tipo invalidit&agrave;</td>
   <td class="campo_readFree"><%=Utils.notNull(tipoInvalDescrA)%></td>
   <td class="etichetta" >Percentuale invalidit&agrave;</td>
   <td class="campo_readFree"><%=Utils.notNull(percInvalA)%></td>
</tr>
<%}%>
    </pt:section>
    <pt:section name="AM_MB_IS">
<tr>
    <td colspan="4"><br/><div class="sezione2">Liste speciali: mobilit&agrave;</div></td>
</tr>
<%if (mobilitaRows!=null && mobilitaRows.size()>0){%>
<tr>
   <td class="etichetta">Data inizio</td>
   <td class="campo_readFree"><%=Utils.notNull(dataInizioMob)%></td>
   <td class="etichetta">Data fine</td>
   <td class="campo_readFree"><%=Utils.notNull(dataFineMob)%></td>
</tr>
<tr>
   <td class="etichetta" >Tipo lista</td>
   <td class="campo_readFree" colspan="3"><%=Utils.notNull(mobTipoDesc)%></td>
</tr>
<tr>
   <td class="etichetta" >Indennit&agrave;</td>
   <td class="campo_readFree" colspan="3"><%=Utils.notNull(decodeFlagSN(indennita_flg))%></td>
</tr>
<%}%>
    </pt:section>
    <pt:section name="AM_IND_T">
<tr>
   <td colspan="4"><br/><div class="sezione2">Condizioni</div></td>
</tr>
<%if (indispTempRows!=null && indispTempRows.size()>0){%>
<%
    Iterator record = indispTempRows.iterator();
    if(record.hasNext()) { 
        row  = (SourceBean) record.next();
        descrizione     = (String)      row.getAttribute("DESCRIZIONE");
        codIndTempLetto = (String)      row.getAttribute("CODINDISPTEMP");         
        datInizioIndTemp   = (String)      row.getAttribute("DATINIZIO");         
        dataFine        = (String)      row.getAttribute("DATFINE");         
    }%>
<tr>
  <td class="etichetta">Tipo Indisp. </td>
  <td colspan="3">
   <table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td width="34%" class="campo_readFree"><%=Utils.notNull(descrizione)%></td>
        <td width="33%" class="campo_readFree">
        	<span class="etichetta">dal&nbsp;</span>
        	<%=Utils.notNull(datInizioIndTemp)%>
        </td>
        <td width="33%" class="campo_readFree">
        	<span class="etichetta">al&nbsp;</span>
        	<%if (dataFine!=null) out.print(dataFine);%>&nbsp;&nbsp;
        </td>
      </tr>
   </table>
<%  while(record.hasNext()) { 
        row  = (SourceBean) record.next();
        descrizione     = (String)      row.getAttribute("DESCRIZIONE");
        codIndTempLetto = (String)      row.getAttribute("CODINDISPTEMP");         
        datInizioIndTemp      = (String)      row.getAttribute("DATINIZIO");         
        dataFine        = (String)      row.getAttribute("DATFINE");         
%>
<tr>
  <td class="etichetta">Tipo Indisp. </td>
  <td colspan="3">
   <table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td width="34%" class="campo_readFree"><%=Utils.notNull(descrizione)%></td>
        <td width="33%" class="campo_readFree">
        	<span class="etichetta">dal&nbsp;</span>
        	<%=Utils.notNull(datInizioIndTemp)%>
        </td>
        <td width="33%" class="campo_readFree">
        	<span class="etichetta">al&nbsp;</span>
        	<%if (dataFine!=null) out.print(dataFine);%>&nbsp;&nbsp;
        </td>
      </tr>
   </table>
    <%}%>
  </td>
</tr>
<%}%>
    </pt:section>
    <pt:section name="PR_ESP_L">
<tr><td colspan="4">
    <br/><div class="sezione2">Ultimi movimenti precedenti o in corso</div></td>
</tr>
<%if (movimentiRows!=null && movimentiRows.size()>0){ 
  Enumeration _enum = movimentiRows.elements();
  if(_enum.hasMoreElements()) {
      row  = (SourceBean) _enum.nextElement();
      esperienza   = (String)     row.getAttribute("DESCRIZIONECONTR");
      mansione     = (String)     row.getAttribute("DESCRIZIONEMANS");
      dataInizMov  = (String)     row.getAttribute("DATINIZIOMOV");
      dataFineMov  = (String)     row.getAttribute("DATFINEMOV");
      motivoCessazione  = (String)     row.getAttribute("motivo_cessazione"); 
      if(motivoCessazione != null && !motivoCessazione.equals("")){
        motivoCessazione=motivoCessazione.trim();
      	motivoCessazione=motivoCessazione.substring(1,motivoCessazione.length()-1);
      }
      retribuzione = (BigDecimal) row.getAttribute("RETRIBANNUA");
      strRagSociale = (String) row.getAttribute("STRRAGIONESOCIALE");
  }%>
<tr>
  <td class="etichetta">Tipo esperienza</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(esperienza)%></td>
</tr>
<tr>
  <td class="etichetta">Mansione</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(mansione)%></td>
</tr>
<tr>
  <td class="etichetta" width="30%">Data inizio</td>
  <td class="campo_readFree"><%=Utils.notNull(dataInizMov)%></td>
  <td class="etichetta" width="30%">Data fine</td>
  <td class="campo_readFree"><%=Utils.notNull(dataFineMov)%></td>
</tr>
<tr>
  <!--
  <td class="etichetta">Retribuzione lorda annua </td>
  <td class="campo_readFree" colspan="4"><%=Utils.notNull(retribuzione)%></td>
  -->
  <td class="etichetta">Presso</td>
  <td class="campo_readFree" colspan="4"><%=Utils.notNull(strRagSociale)%></td>
</tr>
<tr>
  <td class="etichetta">Motivo Cessazione</td>
  <td class="campo_readFree" colspan="4"><%=Utils.notNull(motivoCessazione)%></td>
</tr><%
 while(_enum.hasMoreElements()){
      row  = (SourceBean) _enum.nextElement();
      esperienza   = (String)     row.getAttribute("DESCRIZIONECONTR");
      mansione     = (String)     row.getAttribute("DESCRIZIONEMANS");
      dataInizMov  = (String)     row.getAttribute("DATINIZIOMOV");
      dataFineMov  = (String)     row.getAttribute("DATFINEMOV");
      retribuzione = (BigDecimal) row.getAttribute("RETRIBANNUA");
      strRagSociale = (String) row.getAttribute("STRRAGIONESOCIALE");
%>
<tr>
  <td class="etichetta">Tipo esperienza</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(esperienza)%></td>
</tr>
<tr>
  <td class="etichetta">Mansione</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(mansione)%></td>
</tr>
<tr>
  <td class="etichetta" width="30%">Data inizio</td>
  <td class="campo_readFree"><%=Utils.notNull(dataInizMov)%></td>
  <td class="etichetta" width="30%">Data fine</td>
  <td class="campo_readFree"><%=Utils.notNull(dataFineMov)%></td>
</tr>
<tr>
  <!--
  <td class="etichetta">Retribuzione lorda annua </td>
  <td class="campo_readFree" colspan="4"><%=Utils.notNull(retribuzione)%></td>
  -->
  <td class="etichetta">Presso</td>
  <td class="campo_readFree" colspan="4"><%=Utils.notNull(strRagSociale)%></td>
</tr>
<%  }//while
}//if
%> 
<%--sono in else (movimentiRows!=null)
<tr>
  <td class="etichetta">Tipo esperienza</td><td colspan="3"></td>
</tr>
<tr>
  <td class="etichetta">Mansione</td><td colspan="3"></td>
</tr>
<tr>
  <td class="etichetta" width="30%">Data inizio</td> <td></td>
  <td class="etichetta" width="30%">Data fine</td> <td></td>
</tr>
<tr>
  <td class="etichetta">Retribuzione lorda annua </td>
  <td class="campo_readFree" colspan="4"><%=Utils.notNull(retribuzione)%></td>
</tr>
   <%}%>
<%}--%>
    </pt:section>
    <pt:section name="PR_STU">
<tr>
    <td colspan="4"><br/><div class="sezione2">Titolo di studio</div></td>
</tr>
<%if(titoloStudioRows!=null && titoloStudioRows.size()>0){%>
<tr>
  <td class="etichetta">Tipo</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(titoloStudTipo)%></td>
</tr>
<tr>
  <td class="etichetta">Titolo di studio</td>
  <td class="campo_readFree"><%=Utils.notNull(titoloStud)%></td>
  <td class="etichetta">Anno</td>
  <td class="campo_readFree"><%=Utils.notNull(annoTitStud)%></td>
</tr>
<%}%>
    </pt:section>
    <pt:section name="PR_COR">
<tr>
  <td colspan="4"><br/><div class="sezione2">Ultima formazione professionale: precedente o in corso</div></td>
</tr>
<%if (formazProfRows!=null && formazProfRows.size()>0){
  Enumeration _enum = formazProfRows.elements();
  if (_enum.hasMoreElements())  { 
    row  = (SourceBean) _enum.nextElement();
    corso      = (String)     row.getAttribute("CORSO");
    annoCorso  = (BigDecimal) row.getAttribute("NUMANNO");
    complCorso = (String)     row.getAttribute("FLGCOMPLETATO");
  }
%><tr>
  <td class="etichetta">Nome del corso</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(corso)%></td>
  </tr>
  <tr>
  <td class="etichetta">Anno corso</td>
  <td class="campo_readFree"><%=Utils.notNull(annoCorso)%></td>
  <td class="etichetta">Percorso completato</td>
  <td class="campo_readFree"><%=Utils.notNull(decodeFlagSN(complCorso))%></td>
  </tr>

<%for (; _enum.hasMoreElements() ; )  { 
    row  = (SourceBean) _enum.nextElement();
    corso      = (String)     row.getAttribute("CORSO");
    annoCorso  = (BigDecimal) row.getAttribute("NUMANNO");
    complCorso = (String)     row.getAttribute("FLGCOMPLETATO");

%><tr>
  <td class="etichetta">Nome del corso</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(corso)%></td>
  </tr>
  <tr>
  <td class="etichetta">Anno corso</td>
  <td class="campo_readFree"><%=Utils.notNull(annoCorso)%></td>
  <td class="etichetta">Percorso completato</td>
  <td class="campo_readFree"><%=Utils.notNull(decodeFlagSN(complCorso))%></td>
  </tr>
<%}//for
}//if%>
    </pt:section>

<pt:section name="AM_ALTRE_ISCR">
<tr>
	  <td colspan="4"><br/><div class="sezione2">Altre iscrizioni: ultime iscrizioni in corso</div></td>
</tr>
<%
if(infoAltreIscrRows != null && !infoAltreIscrRows.isEmpty())  {
	Enumeration _enumIscr = infoAltreIscrRows.elements();
	for (; _enumIscr.hasMoreElements() ; )  { 
	    SourceBean rowIscr  = (SourceBean) _enumIscr.nextElement();
	    String dataInizioIscr  = StringUtils.getAttributeStrNotNull(rowIscr, "DATAINIZIO"); 
	    String dataFineIscr  = StringUtils.getAttributeStrNotNull(rowIscr, "DATAFINE");
	    String azienda  = StringUtils.getAttributeStrNotNull(rowIscr, "strragionesociale");
	    String tipoIscr  = StringUtils.getAttributeStrNotNull(rowIscr, "strdescrizione");
	    %>
	    <tr>
	  	<td class="etichetta">Tipo iscrizione</td>
	  	<td class="campo_readFree" colspan="3"><%=tipoIscr%></td>
	  	</tr>
	  	<tr>
	  	<td class="etichetta">data inizio</td>
	  	<td class="campo_readFree"><%=dataInizioIscr%></td>
	  	<td class="etichetta">data fine</td>
	  	<td class="campo_readFree"><%=dataFineIscr%></td>
	  	</tr>
	  	<tr>
	  	<td class="etichetta">azienda</td>
	  	<td class="campo_readFree" colspan="3"><%=azienda%></td>
	  	</tr>
	    <% 
	}
}
%>
</pt:section>
<pt:section name="PR_NOT_L">
<tr>
    <td colspan="4"><br/><div class="sezione2">Annotazioni del CPI</div></td>
</tr>
<%if(infoAnnotazRows!=null && !infoAnnotazRows.isEmpty()) {%>
<tr>
  <td class="etichetta">Annotazioni del CPI</td>
  <td class="campo_readFree" colspan="3"><%=Utils.notNull(annotazCpi)%></td>
</tr>
<tr>
<%}%>
    </pt:section>


</pt:sections>

</table>
<%out.print(htmlStreamBottom);%>

<% if (StringUtils.isEmpty(qs)) { %>
	<span class="bottoni">
		<input type="button" class="pulsanti" value="Chiudi"
		       onClick="window.close()" />
	</span><br/>
<% } %>

<br/>
<%}%>

</body>
</html>
