<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<% 
    SourceBean moduleBean = (SourceBean)serviceResponse.getAttribute("M_CONDIZIONEOCCUPAZIONALE");
    //
   Vector row_InfoCorr      = moduleBean.getAttributeAsVector("INFO_CORRENTI.ROWS.ROW");
   Vector row_obbligoForma  = moduleBean.getAttributeAsVector("OBBLIGO_FORMATIVO.ROWS.ROW");
   Vector statoOccRows      = moduleBean.getAttributeAsVector("STATO_OCCUPAZIONALE.ROWS.ROW");
   Vector permSoggiornoRows = moduleBean.getAttributeAsVector("PERMESSO_SOGGIORNO.ROWS.ROW");
   Vector mobilitaRows      = moduleBean.getAttributeAsVector("MOBILITA.ROWS.ROW");
   Vector collMiratoRows    = moduleBean.getAttributeAsVector("COLLOCAMENTO_MIRATO.ROWS.ROW");
   Vector indispTempRows    = moduleBean.getAttributeAsVector("INDISPONIBILITA_TEMP.ROWS.ROW");
   Vector movimentiRows     = moduleBean.getAttributeAsVector("MOVIMENTI_PRECEDENTI.ROWS.ROW");
   Vector titoloStudioRows  = moduleBean.getAttributeAsVector("TITOLI_LAVORATORE.ROWS.ROW");
   Vector formazProfRows    = moduleBean.getAttributeAsVector("FORMAZIONE_PROFESSIONALE.ROWS.ROW");



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

   String dataInizioCM      = null;
   String dataFineCM        = null;
   String TipoIscrDescr     = null;
   String tipoInvaliditaDescr = null;
   BigDecimal percInval     = null;

   String descrizione       = null;
   String datInizioIndTemp  = null;
   String datFineIndTemp    = null;
   String codIndTempLetto   = null;

   String esperienza   = null;
   String mansione     = null;
   String dataInizMov  = null;
   String dataFineMov  = null;
   BigDecimal retribuzione = null;

   String titoloStud      = null;
   String titoloStudTipo  = null;
   BigDecimal annoTitStud = null;

   String corso         = null;
   String complCorso    = null;
   BigDecimal annoCorso = null;
   
   String readOnlyStr   ="false";

%>

<html>
<head>
<title>Informazioni Correnti</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>

   <script language="JavaScript">
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
         <% if (readOnlyStr.equalsIgnoreCase("false")){ %> 
            flagChanged = true;
         <%}%> 
        }
    </script>
</head>
<body   class="gestione" onload="rinfresca()">

<%@ include  file="_intestazione.inc" %>

<font color="red">
   <af:showErrors/>
</font>

<font color="green">
 <af:showMessages prefix="M_??"/>
</font>


<%
 cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");

 

 if(cdnLavoratore != null)
 {
  if(row_InfoCorr != null && !row_InfoCorr.isEmpty())
  { row   = (SourceBean) row_InfoCorr.elementAt(0);
    datInsElAnagr  = (String) row.getAttribute("DATINIZIO");
    cpiTitolare    = (String) row.getAttribute("STRDESCRIZIONE");
  }

  if(row_obbligoForma != null && !row_obbligoForma.isEmpty())
  { row   = (SourceBean) row_obbligoForma.elementAt(0);
    obblForma_flg      = (String) row.getAttribute("FLGOBBLIGOFORMATIVO");
    obblScolastico_flg = (String) row.getAttribute("FLGOBBLIGOSCOLASTICO");
    descMod            = (String) row.getAttribute("DESCRIZIONE");
  }
   
  if(statoOccRows != null && !statoOccRows.isEmpty())
  {   row = (SourceBean) statoOccRows.firstElement();
      
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

   if( permSoggiornoRows != null && !permSoggiornoRows.isEmpty()) 
   { row  = (SourceBean) permSoggiornoRows.elementAt(0);
     datScad         = (String)      row.getAttribute("DATSCADENZA");
     datRichiesta    = (String)      row.getAttribute("DATRICHIESTA");
     motivoRilDesc   = (String)      row.getAttribute("DESCRIZIONEMOT");
     statoRicDesc    = (String)      row.getAttribute("DESCRIZIONERICH");
   }

  if(mobilitaRows != null && !mobilitaRows.isEmpty())
  { row  = (SourceBean) mobilitaRows.elementAt(0);
    dataInizioMob  = (String)  row.getAttribute("DATINIZIO"); 
    dataFineMob    = (String)  row.getAttribute("DATFINE");
    mobTipoDesc    = (String)  row.getAttribute("DESCRIZIONE");
    indennita_flg  = (String)  row.getAttribute("FLGINDENNITA");
  }

  if(collMiratoRows != null && !collMiratoRows.isEmpty())
  {   row  = (SourceBean) collMiratoRows.elementAt(0);
      dataInizioCM     = (String)     row.getAttribute("DATINIZIO"); 
      dataFineCM       = (String)     row.getAttribute("DATFINE");
      TipoIscrDescr    = (String)     row.getAttribute("DESCRIZIONEISCR");
      tipoInvaliditaDescr = (String)  row.getAttribute("DESCRIZIONEINV");
      percInval        = (BigDecimal) row.getAttribute("NUMPERCINVALIDITA");
  }

  if(titoloStudioRows != null && !titoloStudioRows.isEmpty())
  {   row  = (SourceBean) titoloStudioRows.elementAt(0);
      titoloStud     = (String)     row.getAttribute("destitolo");
      titoloStudTipo = (String)     row.getAttribute("destipotitolo");
      annoTitStud    = (BigDecimal) row.getAttribute("NUMANNO");
  }


%>
<p align="center"> 

<table class="main">
<tr><td width="30%"></td> <td></td> <td  width="30%"></td> <td></td> </tr>
<%--

    AD OGGI 14/11/ QUESTE INFORMAZIONI COMPAIONO SOLO NELLA VIDEATA DELLE INFO BASE  A MESO DI FEEDBACK SUCCESSIVI
    **************************************************************************************************************
<tr>
  <td class="etichetta">Data inserimento nell'elenco anagrafico</td>
  <td colspan="3"><b><%=Utils.notNull(datInsElAnagr)%></b></td>
</tr>
<tr>
  <td class="etichetta">CpI titolare dei dati</td>
  <td colspan="3"><b><%=Utils.notNull(cpiTitolare)%></b></td>
</tr>

<tr><td colspan="4"><br/></td></tr>
--%>
<tr>
    <td colspan="4"><br/><div class="sezione2">Notizie sull'assolvimento dell'obbligo formativo</div>
    </td></tr>
<tr>
    <td class="etichetta" >Obbligo&nbsp;formativo assolto </td><td><b><%=Utils.notNull(obblForma_flg)%></b></td>
    <td class="etichetta" >Obbligo&nbsp;scolastico assolto </td><td><b><%=Utils.notNull(obblScolastico_flg)%></b></td>
</tr>
<tr>
    <td class="etichetta" >Modalita di assolvimento obbligo </td><td colspan="3"><b><%=Utils.notNull(descMod)%></b></td>
</tr>

<tr>
  <td colspan="4"><br/><div class="sezione2">Notizie sullo stato occupazionale</div>
  </td>
</tr>
<tr>
  <td class="etichetta" >Inizio </td><td colspan="3"><b><%=Utils.notNull(dataInizioSO)%></b></td>
</tr>
<tr>
  <td class="etichetta" >Stato occupazionale </td><td colspan="3"><b>&nbsp;<%=Utils.notNull(statoOccDesc)%></b></td>
</tr>
<tr>
  <td class="etichetta"  nowrap>Categoria dlg&nbsp;181 </td><td><b><%=Utils.notNull(cat181Desc)%></b></td>
  <td class="etichetta" >Pensionato </td><td><b><%=Utils.notNull(pensionato)%></b></td>
</tr>
<tr>
  <td class="etichetta" >Reddito </td><td colspan="3"><b><%=Utils.notNull(redditoStr)%></b></td>
</tr>
<tr>
  <td colspan="4"><table cellpadding="0" cellspacing="0" border="0" width="100%">
                   <tr><td style="text-align:right;text-decoration:underline;font-weight:bold" width="29%"><br/><b>Anziantita di disoccupazione</b></td><td>&nbsp;</td></tr>
                  </table></td>
</tr>
<tr>
  <td class="etichetta" >dal </td><td><b><%=Utils.notNull(dataAnzDisoc)%></b></td>
  <td class="etichetta" >mesi&nbsp;sosp. </td><td><b><%=Utils.notNull(numMesiSosp)%></b></td>
</tr>
<tr>
  <td class="etichetta" >Mesi anzianita </td><td><b><%=Utils.notNull(mesiAnz)%></b></td>
  <td colspan="2">
           <%if(mesiAnz != null )
            { if(mesiAnz.compareTo(new BigDecimal(12))>0 || ( mesiAnz.compareTo(new BigDecimal(6)) > 0 && cod181.equalsIgnoreCase("G") ))
                 {%><b>disoccupato/inoccupato di lunga durata</b>
               <%}
               if(mesiAnz.compareTo(new BigDecimal(24)) > 0)
                 {%><b> soggetto alla legge&nbsp;407/90</b>
               <%}
            }%>
    </td>
</tr>
<tr>
  <td class="etichetta" >Indennizzato </td><td colspan="3"><b><%=Utils.notNull(indennizzo)%></b></td>
</tr>

<tr>
    <td colspan="4"><br/><div class="sezione2">Notizie sui cittadini stranieri</div>
    </td>
</tr>
<tr>
  <td class="etichetta">Scadenza permesso soggiorno</td>
  <td colspan="3"><b><%=Utils.notNull(datScad)%></b></td>
</tr>
<tr>
  <td class="etichetta">Data richiesta/Sanatoria</td>
  <td colspan="3"><b><%=Utils.notNull(datRichiesta)%></b></td>
</tr>
<tr>
  <td class="etichetta">Stato richiesta</td>
  <td colspan="3"><b><%=Utils.notNull(statoRicDesc)%></b></td>
</tr>
<tr>
  <td class="etichetta">Motivo permesso di soggiorno</td>
  <td colspan="3"><b><%=Utils.notNull(motivoRilDesc)%></b></td>
</tr>

<tr>
    <td colspan="4"><br/><div class="sezione2">Liste speciali: collocamento mirato</div>
    </td>
</tr>
<tr>
   <td class="etichetta" >Data Inizio </td><td colspan="3"><b><%=Utils.notNull(dataInizioCM)%></b></td>
</tr>
<tr>
   <td class="etichetta" >Tipo lista </td>
   <td colspan="3"><b>Lista ai sensi della L.68/99</b></td>
</tr>
<tr>
   <td class="etichetta" >Tipo invalidità</td>
   <td><b><%=Utils.notNull(tipoInvaliditaDescr)%></b></td>
   <td class="etichetta" >Percentuale invalidità</td>
   <td><b><%=Utils.notNull(percInval)%></b></td>
</tr>

<tr>
    <td colspan="4"><br/><div class="sezione2">Liste speciali: mobilità</div>
    </td></tr>
</tr>
<tr>
   <td class="etichetta" >Data inizio </td><td><b><%=Utils.notNull(dataInizioMob)%></b></td>
   <td class="etichetta" >Data fine </td><td><b><%=Utils.notNull(dataFineMob)%></b></td>
</tr>
<tr>
   <td class="etichetta" >Tipo lista</td>
   <td colspan="3"><b><%=Utils.notNull(mobTipoDesc)%></b></td>
</tr>
<tr>
   <td class="etichetta" >Indennità</td>
   <td colspan="3"><b><%=Utils.notNull(indennita_flg)%></b></td>
</tr>

<tr>
   <td colspan="4"><br/><div class="sezione2">Indisponibilità temporanee</div>
   </td>
</tr>
<%Iterator record = indispTempRows.iterator();
  if(record.hasNext())
  { row  = (SourceBean) record.next();
    descrizione     = (String)      row.getAttribute("DESCRIZIONE");
    codIndTempLetto = (String)      row.getAttribute("CODINDISPTEMP");         
    datInizioIndTemp   = (String)      row.getAttribute("DATINIZIO");         
    dataFine        = (String)      row.getAttribute("DATFINE");         
  }//if%>
<tr>
  <td class="etichetta">Tipo Indisp. </td>
  <td colspan="3">
   <table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td width="30%"><b><%=Utils.notNull(descrizione)%></b></td>
        <td width="20%">dal&nbsp;<b><%=Utils.notNull(datInizioIndTemp)%></b></td>
        <td> &nbsp;&nbsp;al&nbsp;<b><%if (dataFine!=null) out.print(dataFine);%></b></td>
      </tr>
   </table>
<%while(record.hasNext())
  { row  = (SourceBean) record.next();
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
        <td width="30%"><b><%=Utils.notNull(descrizione)%></b></td>
        <td width="20%">dal&nbsp;<b><%=Utils.notNull(datInizioIndTemp)%></b></td>
        <td> &nbsp;&nbsp;al&nbsp;<b><%if (dataFine!=null) out.print(dataFine);%></b></td>
      </tr>
   </table>
<%}//for%>
  </td>
</tr>

<tr><td colspan="4">
    <br/><div class="sezione2">Ultime esperienze professionali: movimenti precedenti o in corso</div>
    </td>
</tr>
<%if (movimentiRows!=null){
  //for (Enumeration _enum = movimentiRows.elements(); _enum.hasMoreElements() ; )
  Enumeration _enum = movimentiRows.elements();
  if(_enum.hasMoreElements())
  {
      row  = (SourceBean) _enum.nextElement();
      esperienza   = (String)     row.getAttribute("DESCRIZIONECONTR");
      mansione     = (String)     row.getAttribute("DESCRIZIONEMANS");
      dataInizMov  = (String)     row.getAttribute("DATINIZIOMOV");
      dataFineMov  = (String)     row.getAttribute("DATFINEMOV");
      retribuzione = (BigDecimal) row.getAttribute("RETRIBANNUA");
  }%>
<tr>
  <td class="etichetta">Tipo esperienza</td>
  <td colspan="3"><b><%=Utils.notNull(esperienza)%></b></td>
</tr>
<tr>
  <td class="etichetta">Mansione</td>
  <td colspan="3"><b><%=Utils.notNull(mansione)%></b></td>
</tr>
<tr>
  <td class="etichetta" width="30%">Data inizio</td>
  <td><b><%=Utils.notNull(dataInizMov)%></b></td>
  <td class="etichetta" width="30%">Data fine</td>
  <td><b><%=Utils.notNull(dataFineMov)%></b></td>
</tr>
<tr>
  <td class="etichetta">Retribuzione lorda annua </td><td colspan="4"><b><%=Utils.notNull(retribuzione)%></b></td>
</tr><%
 while(_enum.hasMoreElements()){
      row  = (SourceBean) _enum.nextElement();
      esperienza   = (String)     row.getAttribute("DESCRIZIONECONTR");
      mansione     = (String)     row.getAttribute("DESCRIZIONEMANS");
      dataInizMov  = (String)     row.getAttribute("DATINIZIOMOV");
      dataFineMov  = (String)     row.getAttribute("DATFINEMOV");
      retribuzione = (BigDecimal) row.getAttribute("RETRIBANNUA");
%>
<tr>
  <td class="etichetta">Tipo esperienza</td>
  <td colspan="3"><b><%=Utils.notNull(esperienza)%></b></td>
</tr>
<tr>
  <td class="etichetta">Mansione</td>
  <td colspan="3"><b><%=Utils.notNull(mansione)%></b></td>
</tr>
<tr>
  <td class="etichetta" width="30%">Data inizio</td>
  <td><b><%=Utils.notNull(dataInizMov)%></b></td>
  <td class="etichetta" width="30%">Data fine</td>
  <td><b><%=Utils.notNull(dataFineMov)%></b></td>
</tr>
<tr>
  <td class="etichetta">Retribuzione lorda annua </td><td colspan="4"><b><%=Utils.notNull(retribuzione)%></b></td>
</tr>
<%}//while
}//if
else {%> sono in else (movimentiRows!=null)
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
  <td class="etichetta">Retribuzione lorda annua </td><td colspan="4"><b><%=Utils.notNull(retribuzione)%></b></td>
</tr>

   <%}%>
</tr>

<tr>
    <td colspan="4"><br/><div class="sezione2">Titolo di studio</div>
    </td></tr>
</tr>
<tr>
  <td class="etichetta">Tipo</td>
  <td colspan="3"><b><%=Utils.notNull(titoloStudTipo)%></b></td>
</tr>
<tr>
  <td class="etichetta">Titolo di studio</td>
  <td><b><%=Utils.notNull(titoloStud)%></b></td>
  <td class="etichetta">Anno</td>
  <td><b><%=Utils.notNull(annoTitStud)%></b></td>
</tr>

<tr>
  <td colspan="4"><br/><div class="sezione2">Ultima formazione professionale: precedente o in corso</div>
  </td>
</tr>
<%if(formazProfRows !=null){
  Enumeration _enum = formazProfRows.elements();
  if (_enum.hasMoreElements())
  { 
    row  = (SourceBean) _enum.nextElement();
    corso      = (String)     row.getAttribute("CORSO");
    annoCorso  = (BigDecimal) row.getAttribute("NUMANNO");
    complCorso = (String)     row.getAttribute("FLGCOMPLETATO");
  }
%><tr>
  <td class="etichetta">Nome del corso</td>
  <td colspan="3"><b><%=Utils.notNull(corso)%></b></td>
  </tr>
  <tr>
  <td class="etichetta">Anno corso</td><td ><b><%=Utils.notNull(annoCorso)%></b></td>
  <td class="etichetta">Percorso completato</td><td ><b><%=Utils.notNull(complCorso)%></b></td>
  </tr>

<%for (; _enum.hasMoreElements() ; )
  { 
    row  = (SourceBean) _enum.nextElement();
    corso      = (String)     row.getAttribute("CORSO");
    annoCorso  = (BigDecimal) row.getAttribute("NUMANNO");
    complCorso = (String)     row.getAttribute("FLGCOMPLETATO");

%><tr>
  <td class="etichetta">Nome del corso</td>
  <td colspan="3"><b><%=Utils.notNull(corso)%></b></td>
  </tr>
  <tr>
  <td class="etichetta">Anno corso</td><td ><b><%=Utils.notNull(annoCorso)%></b></td>
  <td class="etichetta">Percorso completato</td><td ><b><%=Utils.notNull(complCorso)%></b></td>
  </tr>
<%}//for
}//if%>
</table>
<br/>
</p>



<%}//end if ("cdnLavoratore")
else {
    %><h3>L'attributo <i>cdnLavoratore</i> non è presente nella serviceRequest</h3>
      <h4>questo rende impossibile visualizzare o memorizzare i dati!</h4><%
}//else ("cdnLavoratore")
%>

</body>
</html>
