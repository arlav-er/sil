
<%
SourceBean cont = (SourceBean) serviceResponse.getAttribute("MDETTAGLIOROSA");
SourceBean infoRosa = (SourceBean) cont.getAttribute("ROW");

String numRichiesta = StringUtils.getAttributeStrNotNull(infoRosa, "NUMRICHIESTA");
String numRichiestaOrig = StringUtils.getAttributeStrNotNull(infoRosa, "NUMRICHIESTAORIG");
String numAnno = StringUtils.getAttributeStrNotNull(infoRosa, "NUMANNO");
String tipoIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "TIPOINCROCIO");
String prgTipoIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "PRGTIPOINCROCIO");
String tipoRosa = StringUtils.getAttributeStrNotNull(infoRosa, "TIPOROSA");

String prgIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "PRGINCROCIO");

String prgAlternativa = StringUtils.getAttributeStrNotNull(infoRosa, "PRGALTERNATIVA");
String strAlternativa = "";
if(!prgAlternativa.equals("")) { strAlternativa = "Profilo n. " + prgAlternativa; }

String utMod = StringUtils.getAttributeStrNotNull(infoRosa, "CDNUTMOD");
String ultimaModifica = StringUtils.getAttributeStrNotNull(infoRosa, "ULTIMAMODIFICA");
String utAttivo = Integer.toString(user.getCodut());

%>

<script language="Javascript">
  function openPar_PopUP() {
     window.open ("AdapterHTTP?PAGE=MatchViewParPesatoPage&PRGINCROCIO=<%=prgIncrocio%>", "Parametri", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
  }
  
  function openRich_PopUP() {
     window.open ("AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&CDNFUNZIONE=<%=_cdnFunzione%>&POPUP=1", "DettaglioSintetico", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
  }
</script>

<%if(infoRosa!=null) {%>                                                                            
<br>                                                                                                
  <%out.print(htmlStreamTopInfo);%>                                                                 
  <p class="info_lav">                                                                              
  Identificativo Rosa <b><%=prgRosa%></b><br>                                                       
  <a href="#" onClick="openRich_PopUP()">Richiesta num. <b><%=numRichiestaOrig%>/<%=numAnno%></b></a> - 
  Alternativa utilizzata per il matching <b><%=strAlternativa%></b><br>                             
  Tipo di Rosa <b><%=tipoRosa%></b> -  Tipo di Incrocio <b><%=tipoIncrocio%></b><br>                
  <%if(prgTipoIncrocio.equals("1")) {%>                                                             
    <u>Parametri</u>                                                                                
  <%} else {%>                                                                                      
    <%if(prgTipoIncrocio.equals("2")) {%>                                                           
    <a href="#" onClick="openPar_PopUP()">Parametri</a>                                             
                                                                                                    
    <%}%>                                                                                           
  <%}%>                                                                                             
  <%if(!utAttivo.equals(utMod)){%>                                                                  
    <br>Ultima Modifica <b><%=ultimaModifica%></b>                                                  
  <%}%>                                                                                             
  </p>                                                                                              
  <%out.print(htmlStreamBottomInfo);%>                                                              
<%}%> 
                                                                                   






