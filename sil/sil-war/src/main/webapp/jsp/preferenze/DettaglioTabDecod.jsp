<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.ga.db.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<html>
	<head>
		<title>Selezione tabella dettaglio</title>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
    <af:linkScript path="../../js/"/>

<script language="JavaScript">
  var flagChanged = false;  

  function fieldChanged()
  { flagChanged = true;
  }

function checkDate() {
  var objData1 = document.getElementsByName("DATINIZIOVAL");
  var objData2 = document.getElementsByName("DATFINEVAL");
  if (objData1.length==0 || objData2.length==0){
	  return true;
  }
  else {
	  strData1=objData1.item(0).value;
	  strData2=objData2.item(0).value;
	
	  //costruisco la data di inizio
	  d1giorno=parseInt(strData1.substr(0,2),10);
	  d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero 
	  d1anno=parseInt(strData1.substr(6,4),10);
	  data1=new Date(d1anno, d1mese, d1giorno);
	
	  //costruisce la data di fine
	  d2giorno=parseInt(strData2.substr(0,2),10);
	  d2mese=parseInt(strData2.substr(3,2),10)-1;
	  d2anno=parseInt(strData2.substr(6,4),10);
	  data2=new Date(d2anno, d2mese, d2giorno);
	  
	  ok=true;
	  if (data2 < data1) {
	      alert("La data inizio è precedente alla data fine");
	      document.getElementsByName("DATINIZIOVAL").item(0).focus();
	      ok=false;
	   }
	  return ok;
  }
}


</script>	
</head>
	<body class="gestione" onload="rinfresca()">

	<%  
		boolean  nuovo  = false;
		String cdnFunzione = (String) serviceRequest.getAttribute("cdnfunzione");
		SourceBean activeMod = (SourceBean) serviceResponse.getAttribute("DETTAGLIOTABDECOD");
		if (activeMod == null){
			nuovo=true;
			activeMod = (SourceBean) serviceResponse.getAttribute("NUOVOTABDECOD");
		}
		
		SourceBean row = (SourceBean) activeMod.getAttribute("ROWS.ROW");
		Tabella tab = (Tabella) activeMod.getAttribute("Tabella");
		List pkCols = new it.eng.sil.module.preferenze.GenQuery(tab).retrievePKs();

		boolean skipComments = false;
		boolean showKeys = false;

		String skipCommentsStr = (String) serviceRequest.getAttribute("SKIP_COMMENTS");
		if (skipCommentsStr != null) {
			skipComments = skipCommentsStr.equalsIgnoreCase("true");
		}else{
			skipCommentsStr="false";
		}

		String showKeysStr = (String) serviceRequest.getAttribute("SHOW_KEYS");
		if (showKeysStr != null) {
			showKeys = showKeysStr.equalsIgnoreCase("true");
		}else{
			showKeysStr="false";
		}

		List colonne = tab.getArrColonne();
		String titolo = tab.getCommento();
		if (titolo.equals(""))
			titolo=tab.getNome();
	
    //Profilatura ------------------------------------------------
    String _page = (String) serviceRequest.getAttribute("PAGE");
    PageAttribs attributi = new PageAttribs(user, _page+"&TABLE_NAME="+tab.getNome() + "&SHOW_KEYS=" +showKeysStr + "&SKIP_COMMENTS=" + skipCommentsStr );
    //boolean canModify= attributi.containsButton("INSERISCI");
    //boolean canDelete= attributi.containsButton("CANCELLA");
    boolean canAggiorna= attributi.containsButton("AGGIORNA");
		
  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(canAggiorna);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canAggiorna);
  
	String labelButton = "";
	if(nuovo){
		labelButton = "Salva";
	}
	else {
		labelButton = "Aggiorna";
	}
	%>
		
     <font color="green"><af:showMessages prefix="SalvaTabDecod"/></font>
     <font color="red"><af:showErrors /></font>
	
	<p class="titolo"><%=titolo%></p>
	<br/>
	 <af:form method="POST" action="AdapterHTTP" onSubmit="checkDate()">
	      <af:textBox type="hidden" name="PAGE" value="DettaglioTabDecodPage"/>
	      <af:textBox type="hidden" name="SALVA" value="true"/>
	      <%if (nuovo){%>
		      <af:textBox type="hidden" name="NUOVO" value="true"/>
	      <%}%>
	      <af:textBox type="hidden" name="TABLE_NAME" value="<%=tab.getNome()%>"/>
	      
	      <af:textBox type="hidden" name="SHOW_KEYS" value="<%=showKeysStr%>"/>
	      <af:textBox type="hidden" name="SKIP_COMMENTS" value="<%=skipCommentsStr%>"/>
	      
	      
	      <af:textBox name="cdnfunzione" type="hidden" value="<%=cdnFunzione%>" />
	     <p align="center">
      <%out.print(htmlStreamTop);%>
      <table class="main">
        

	<% 
		for(Iterator iter=colonne.iterator(); iter.hasNext(); ){
		
			Colonna colonna = (Colonna) iter.next();
			String nome =colonna.getNome();
			String tipo = colonna.getNometipo();
			String label = colonna.getCommento();
			int maxLen = colonna.getDimensione();

			String size=String.valueOf(maxLen);
			String maxLenStr=size;
			if (maxLen>50) {
				size="50"; 
			}
			
			String type="text";

			String readOnlyStr= String.valueOf(!canAggiorna);	

			
			
			if (label.equals(""))
				label=nome;
			String required = new Boolean(!colonna.isNullabile()).toString();
			
			String valore ="";
			Object val=null;
			
			if (row == null){
				val="";	
			}else{
				val = row.getAttribute(nome);
			}
			
			if (val instanceof String){
				valore = (String) val;
			}
			
			if (val instanceof BigDecimal){
				valore = ((BigDecimal) val).toString();
			}

			
			if (tipo.equals("NUMBER")) {
				size="12";
				maxLenStr="12";
			}

			if (tipo.equals("DATE")) {
				 size="12";
				 type="date";
				 maxLenStr="12";
			}
		
			String pk = (colonna.isPK())? "<img alt='Chiave primaria' src=\"../../img/pk.gif\">" : "";
			String fk = (colonna.isFK())? "<img alt='Chiave esterna' src=\"../../img/fk.gif\">" : "";
		

			if (nome.toUpperCase().startsWith("NUMKLO") && !showKeys){
				out.print("<input type=hidden name='" + nome +  "' value='" + valore + "'>");
				type="hidden"; 
				continue;
			}			

			if (colonna.isPK() && (pkCols.size()==1) && !showKeys) {
					if (tipo.equals("NUMBER")) {
						out.print("<input type=hidden name='" + nome +  "' value=\"" + valore +"\">");
						continue;
					}
			}

			if (!nuovo && colonna.isPK()){
				readOnlyStr="true";
			}
		

					
		%>	
        
        <tr>
          <td class="etichetta"><%=label%></td>
          <td class="campo">
          <%if( ((valore==null) || (valore.trim().equals(""))) && (serviceRequest.getAttribute(nome)!=null) ){
          		valore=(String)serviceRequest.getAttribute(nome);
          	}
			%>
            <af:textBox type ="<%=type%>" name="<%=nome%>"  value="<%=valore%>" 
              title="<%=label%>"  required="<%=required%>"
              size="<%=size%>" onKeyUp="fieldChanged()" maxlength="<%=maxLenStr%>"
              classNameBase="input"  validateOnPost="true"
              readonly="<%=readOnlyStr%>"
              /> 
              	<%if (showKeys) {%>
	              	<%=pk%>&nbsp;<%=fk%>
              	<%}%>
          </td>
          
        </tr>

	<%} // end for %>



               
        <tr><td colspan="2">
         <%if(canAggiorna){%>        
            <input class="pulsante" type="submit" name="save" VALUE="<%=labelButton%>" />          
            <input class="pulsante" type="reset" VALUE="Annulla" /><BR/><BR/>
          <%}%>
            <!--center><input class="pulsante" type="button" VALUE="Torna alla lista" onClick="javascript:history.back();" /></center-->
            <%if (sessionContainer!=null){
                  String token = "_TOKEN_ListaTabDecodPage";
                  String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
                  int idx = urlDiLista.indexOf("CANCELLARIGA");
                  if (idx != -1) 
                  { String tmpURLhead = urlDiLista.substring(0,idx-1);
                    String tmpURLtail = urlDiLista.substring(idx);
                    tmpURLtail = tmpURLtail.substring(tmpURLtail.indexOf("&"));
                    //out.println("<br/>tmpURL_HEAD:: "+tmpURLhead); 
                    //out.println("<br/>tmpURL_TAIL:: "+tmpURLtail);
                    urlDiLista = tmpURLhead + tmpURLtail;
                  }
                  
                  if (urlDiLista!=null){
                   %> 
                    <a href="#" onClick="goTo('<%=urlDiLista%>');"><img src="../../img/rit_lista.gif" border="0"></a>
                    <%--La seguente sessione e a PROVA DI ANGELA;) Se il pulsante non viene reputato standard... scommentare e otteniamo un pulsante standard
                    <input class="pulsante" type="button" VALUE="Torna alla lista" onClick="javascript:cameBack('AdapterHTTP?<%=urlDiLista%>');" />
                    --%>
                <%}
              }
            %>


          </td>
        </tr>

  </table>
  <%out.print(htmlStreamBottom);%>
        
 </af:form>

	</body>
</html>

