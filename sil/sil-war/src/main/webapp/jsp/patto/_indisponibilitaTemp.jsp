<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import=" com.engiweb.framework.base.*,
                  java.util.Vector, java.util.Iterator,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.*" %>
<%
    String descrizione       = null;
    String datInizioIndTemp  = null;
    String datFineIndTemp    = null;
    String codIndTempLetto   = null;
    String dataFine = null;
    Vector indispTempRows= (Vector)request.getAttribute("INDISPONIBILITA_TEMP");
    SourceBean row = null;
    Iterator record = indispTempRows.iterator();
    if(record.hasNext()) { 
        row  = (SourceBean) record.next();
        descrizione     = (String)      row.getAttribute("DESCRIZIONE");
        codIndTempLetto = (String)      row.getAttribute("CODINDISPTEMP");         
        datInizioIndTemp   = (String)      row.getAttribute("DATINIZIO");         
        dataFine        = (String)      row.getAttribute("DATFINE");         
    }//if

%>

<tr>
   <td colspan="4"><br/><div class="sezione2">Indisponibilità temporanee</div>
   </td>
</tr>
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
