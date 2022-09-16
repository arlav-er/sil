<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  java.util.*, 
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*, it.eng.sil.security.PageAttribs" %>

<%
//Vector rows = serviceResponse.getAttributeAsVector("M_GETTABELLENONLEGATEALPATTO.AM_IND_T.ROWS.ROW");
SourceBean serviceRequest = (SourceBean)request.getAttribute("SERVICE_REQUEST");
SessionContainer sessionContainer= (SessionContainer)request.getAttribute("SESSION_CONTAINER");
SourceBean sb = (SourceBean)request.getAttribute("SOURCE_BEAN");
Vector rows = sb.getAttributeAsVector("PR_MAN.ROWS.ROW");
SourceBean row = null;
//BigInteger countObj = (BigInteger)request.getAttribute("COUNT_IMG");
//int count = countObj.intValue();
PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID),"MansioniPage");
boolean canModify= pageAtts.containsButton("inserisci");
boolean canDelete= pageAtts.containsButton("rimuovi");
boolean canInsert = (canModify && canDelete);

%>
<script>
<%--
function aggiungiMansione() {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var urlpage="";
    urlpage+="AdapterHTTP?";
    urlpage+="cdnLavoratore="+<%=(String)serviceRequest.getAttribute("CDNLAVORATORE")%>+"&";
    // serve per il caricamento della linguetta
    urlpage+="CDNFUNZIONE="+<%=(String)serviceRequest.getAttribute("CDNFUNZIONE")%>+"&";
    // il parametro che attiva il modulo di associazione al patto
    <%
    List p = new ArrayList();
    Object o = serviceRequest.getAttribute("COD_LST_TAB");
    List cods = null;
    if (o instanceof Vector) cods = (List)o;
    else {cods = new ArrayList(1);cods.add(o);}
    for (int i=0;i<cods.size();i++) {
    %>
        urlpage+="COD_LST_TAB=<%=cods.get(i)%>&";
    <%} %>
            
    // serve per determinare se una sezione, al caricamento della pagina, va mostrata aperta o chiusa (cosi' come al momento della 
    //       chiamata di questa pagina)
    urlpage+="statoSezioni=<%=(String)serviceRequest.getAttribute("statoSezioni")%>&";
    urlpage+="PAGE_CHIAMANTE=<%=serviceRequest.getAttribute("pageChiamante")%>&";
    urlpage+="PAGE=MansioniPage&";
    urlpage+="ONLY_INSERT=1";
    setWindowLocation(urlpage);
}
--%>
</script>
<table width=90%>
    <tr>
        <td>
            <table style="border-collapse:collapse" width="100%">
                <tr style="border: 1 solid #000080; color: #000080;  font-size: 12px;font-weight: normal;text-align: center;vertical-align:middle">
                    <td style="border: 1 solid;border-right: none"  align=center>Mansioni</td>
                    <td style="border: 1 solid; border-left:none" align=right>        
                    </td>
                </tr>
            </table>
        </td>
    <tr>
    <tr><td></td></tr>       
    <tr>
        <td> 
<%   
    for (int i=0;i<rows.size(); i++) {
        row = (SourceBean)rows.get(i); 
%>      
            <table width="100%" cellpadding="0" cellspacing="0">
                <tr>
                    <td width="2%" height=25 valign=middle>
                        <input type="checkbox" id="I_PR_MAN<%=i%>">
                        <script>inputs.push(new Riga('PR_MAN','PR_MAN',<%=row.getAttribute("PRGMANSIONE").toString()%>,document.getElementById("I_PR_MAN<%=i%>")));</script>
                    </td>        
                    <td class="etichetta2" width="12%">Mansione</td>
                    <td class="campo3" width="30%"><%= Utils.notNull(row.getAttribute("STRDESCRIZIONE"))%></td>
                    <td class="etichetta2" width="12%">Disp. Lav.</td>
                    <td class="campo3" width="2%"><%= Utils.notNull(row.getAttribute("FLGDISPONIBILE"))%></td>
                    <td class="etichetta2" width="12%">Esp. Lav.</td>
                    <td class="campo3" width="2%"><% String flgEsp = Utils.notNull(row.getAttribute("FLGESPERIENZA")); if (flgEsp.equals("E")) out.print("ND"); else out.print(flgEsp);%></td>
                    <td class="etichetta2" width="12%">Esp. Form.</td>
                    <td class="campo3" width="2%"><%= Utils.notNull(row.getAttribute("FLGPIP"))%></td>
                    <td class="etichetta2" width="12%">Disp. Form.</td>
                    <td class="campo3" width="2%"><%= Utils.notNull(row.getAttribute("FLGDISPFORMAZIONE"))%></td>
                    
                </tr>
            </table>                    
    <%      
    //count++;
    }  %>
        </td>
    </tr>
<% if(rows.size()>0) {%>
    <tr>
        <td>
            <table width="100%">
                <tr>
                    <td align=right><input type="checkbox" id="PR_MAN_GEN" onclick="tutti(this,'PR_MAN')" checked >Seleziona tutti</td> 
                    <script>tutti(document.getElementById('PR_MAN_GEN'),'PR_MAN')</script>
                    <td width=20></td>
                    <td align=center width="30%">
                    <%if (canInsert) {%><button class=pulsanti onclick="aggiungi('MansioniPage')">Aggiungi mansione</button><%}%>
                    </td>
                </tr>    
            </table>
        </td>
    </tr>
<%} else if (canInsert) {%>
<tr>
        <td>
            <table width="100%">
                <tr>
                    <td align=right>&nbsp;</td> 
                    <td width=20></td>
                    <td align=center width="30%">
                    <input type="button" class=pulsanti onclick="aggiungi('MansioniPage')" value="Aggiungi mansione">
                    </td>
                </tr>    
            </table>
        </td>
    </tr>
<%}%>
  <tr> 
      <td align="left">N.B.: per poter associare una mansione al patto deve essere indicata la disponibilità al lavoro e/o alla formazione.</td>
      <td align=right></td>
      <td align=right></td>
  </tr>
</table>
<%
    //request.setAttribute("COUNT_IMG", BigInteger.valueOf(count));
%>