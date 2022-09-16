<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ page import=" com.engiweb.framework.base.*,
                  java.util.Vector,java.util.Enumeration,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.*" %>
<%    
    Vector movimentiRows= (Vector)request.getAttribute("MOVIMENTI_PRECEDENTI");
    SourceBean row = null;
    String esperienza   = null;
    String mansione     = null;
    String dataInizMov  = null;
    String dataFineMov  = null;
    BigDecimal annoFine =null;
    BigDecimal annoInizio=null;
    BigDecimal meseInizio=null;
    BigDecimal meseFine=null;
    BigDecimal retribuzione = null;

    

%>

<tr><td colspan="4">
    <br/><div class="sezione2">Ultime esperienze professionali: movimenti precedenti o in corso</div>
    </td>
</tr>

<%
    if (movimentiRows!=null){
        //for (Enumeration _enum = movimentiRows.elements(); _enum.hasMoreElements() ; )
        Enumeration _enum = movimentiRows.elements();
        if(_enum.hasMoreElements())  {
            row  = (SourceBean) _enum.nextElement();
            esperienza   = (String)     row.getAttribute("DESCRIZIONECONTR");
            mansione     = (String)     row.getAttribute("DESCRIZIONEMANS");
            annoInizio  = (BigDecimal)     row.getAttribute("NUMANNOINIZIO");
            annoFine  = (BigDecimal)     row.getAttribute("NUMANNOFINE");
            retribuzione = (BigDecimal) row.getAttribute("RETRIBANNUA");
            meseInizio = (BigDecimal) row.getAttribute("NUMMESEINIZIO");
            meseFine = (BigDecimal) row.getAttribute("NUMMESEFINE");
         }
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
<%
    String n = Utils.notNull(meseInizio);
    if (n.length()==1) n="0"+n;
 %>
  <td class="campo2"><b><%=n%></b>&nbsp;/&nbsp;<b><%=Utils.notNull(annoInizio)%></b></td>
  <td class="etichetta" width="30%">Data fine</td>
  <td class="campo2"><b><%=Utils.notNull(meseFine)%></b>&nbsp;/&nbsp;</b><b><%=Utils.notNull(annoFine)%><b></td>
</tr>
<tr>
  <td class="etichetta">Retribuzione lorda annua </td><td colspan="4"><b><%=Utils.notNull(retribuzione)%></b></td>
</tr>
<%
 while(_enum.hasMoreElements())  {
    row  = (SourceBean) _enum.nextElement();
    esperienza   = (String)     row.getAttribute("DESCRIZIONECONTR");
    mansione     = (String)     row.getAttribute("DESCRIZIONEMANS");
    annoInizio  = (BigDecimal)     row.getAttribute("NUMANNOINIZIO");
    annoFine  = (BigDecimal)     row.getAttribute("NUMANNOFINE");
    retribuzione = (BigDecimal) row.getAttribute("RETRIBANNUA");
    meseInizio = (BigDecimal) row.getAttribute("NUMMESEINIZIO");
    meseFine = (BigDecimal) row.getAttribute("NUMMESEFINE");
%>
<tr><td clospan="3"></td></tr>
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
<%
    n = Utils.notNull(meseInizio);
    if (n.length()==1) n="0"+n;
 %>
  <td class="campo2"><b><%=n%></b>&nbsp;/&nbsp;<b><%=Utils.notNull(annoInizio)%></b></td>
  <td class="etichetta" width="30%">Data fine</td>
  <td class="campo2"><b><%=Utils.notNull(meseFine)%></b>&nbsp;/&nbsp;</b><b><%=Utils.notNull(annoFine)%><b></td>
</tr>
<tr>
  <td class="etichetta">Retribuzione lorda annua </td><td colspan="4"><b><%=Utils.notNull(retribuzione)%></b></td>
</tr>
<%   }//while
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
