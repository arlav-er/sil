
<%
String htmlStreamTopInfo = StyleUtils.roundTopTableInfo();
String htmlStreamBottomInfo = StyleUtils.roundBottomTableInfoNobr();

SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_GetTestataGraduatoriaAnnuale");
SourceBean infoGrad = (SourceBean) cont.getAttribute("ROWS.ROW");

Object prgGrad = infoGrad.getAttribute("PRGGRADUATORIA");
Object numAnno = infoGrad.getAttribute("NUMANNO");
String PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(infoGrad, "PROVINCIA_ISCR");
String stato = StringUtils.getAttributeStrNotNull(infoGrad, "STATOGRAD");
String tipo = StringUtils.getAttributeStrNotNull(infoGrad, "CODMONOTIPOGRAD");
String tipoStr = "";
if (("A").equals(tipo)) {
	tipoStr = "Avviamento numerico art.18";
}
else {
	tipoStr = "Avviamento numerico art.8";
}
String datRiferimento = StringUtils.getAttributeStrNotNull(infoGrad, "DATRIFERIMENTO");
Object annoReddito = infoGrad.getAttribute("NUMANNOREDDITO");

%>
      
<%if(infoGrad!=null) {%>      
  <br>
  <%out.print(htmlStreamTopInfo);%>
  <p class="info">  
  Graduatoria anno <strong><%=numAnno%></strong>&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;
  
  Data Riferimento <strong><%=datRiferimento%></strong>&nbsp;&nbsp;&nbsp;  
  <br> 
  Ambito Territoriale <strong><%=PROVINCIA_ISCR%></strong>&nbsp;&nbsp;&nbsp;  
  Tipo Graduatoria <strong><%=tipoStr%></strong>&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;  
  Stato della Graduatoria <strong><%=stato%></strong>&nbsp;&nbsp;&nbsp;  
  <br>
  Anno Riferimento Reddito <strong><%=annoReddito%></strong>
  <br>  
  </p>
  <%out.print(htmlStreamBottomInfo);%>
<%}%>                                                                     






