<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import=" com.engiweb.framework.base.*,
                  java.util.Vector,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.*" %>

<% 
Vector statoOccRows= (Vector)request.getAttribute("STATO_OCCUPAZIONALE");

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
//   String statoAtDescr     = null;
   BigDecimal redditoStr   = null;
   BigDecimal mesiAnz      = null;
   BigDecimal numMesiSosp  = null;

if(statoOccRows != null && !statoOccRows.isEmpty())
  {   SourceBean row = (SourceBean) statoOccRows.firstElement();
      
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
//      statoAtDescr    = (String)      row.getAttribute("DESCRIZIONERICH");     
  }
  %>

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
                   <tr><td style="text-align:right;text-decoration:underline;font-weight:bold" width="29%"><br/><b>Anziantità di disoccupazione</b></td><td>&nbsp;</td></tr>
                  </table></td>
</tr>
<tr>
  <td class="etichetta" >dal </td><td><b><%=Utils.notNull(dataAnzDisoc)%></b></td>
  <td class="etichetta" >mesi&nbsp;sosp. </td><td><b><%=Utils.notNull(numMesiSosp)%></b></td>
</tr>
<tr>
  <td class="etichetta" >Mesi anzianità </td><td><b><%=Utils.notNull(mesiAnz)%></b></td>
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

