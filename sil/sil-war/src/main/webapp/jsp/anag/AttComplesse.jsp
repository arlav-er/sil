<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
 
  int _funzione=0;
  InfCorrentiLav infCorrentiLav= null;
  	
  Testata operatoreInfo = null;
  String prgDiagnosiFunzionale = null;
  String cdnLavoratore = null;
  String codCapCogn = null;
  String codCapPers = null;
  String codCapDis = null;
  String codCapSquadra = null;
  String codCapAut = null;
  String codCapAtt = null;
  String codCapPres = null;
  String descrGrado = null;
  String codMonoTipoGrado = "";
  String nomeDescrGrado = "";
  String capCogn = "";

  String numklodiagnosifunzionale = null;
  BigDecimal cdnUtIns = null;
  BigDecimal cdnUtMod = null;
  String dtmIns = null;
  String dtmMod = null;
  
  prgDiagnosiFunzionale = "" + RequestContainer.getRequestContainer().getSessionContainer().getAttribute("prgDiagnosiFunzionale");

  cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
  
  //Profilatura ------------------------------------------------
  String _page = (String) serviceRequest.getAttribute("PAGE");   
  PageAttribs attributi = new PageAttribs(user, _page);
  
  boolean canModify= attributi.containsButton("aggiorna");
  boolean canDelete= attributi.containsButton("RIMUOVI");
  boolean canInsert= attributi.containsButton("INSERISCI");
   
  boolean readOnlyStr = !canModify;
  String fieldReadOnly = canModify?"false":"true";
  
  boolean readCodiciScaduti = false;
  
  /*------------------------ inserire il parametro cdnFunzione ---------------------------*/
  _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
 
  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);
  
  String codice = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codice");
  String descrizione = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"descrizione"); 
  
  BigDecimal scad = (BigDecimal) serviceResponse.getAttribute ("M_VerificaCodValidi.ROWS.ROW.scadCodici");
  
  if (scad != null && scad.intValue() > 0) {
	  readCodiciScaduti = true;
  } 

%>

<html>
<head>
    <title>SocioLav.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
    <af:linkScript path="../../js/"/>

	<script language="JavaScript">
          
          var flagChanged = false;
        
          function fieldChanged() {
           <% if (!readCodiciScaduti){ %> 
              flagChanged = true;
           <%}%> 
          }

		  var urlpage="AdapterHTTP?";	

		  function getURLPageBase() {    
		    urlpage+="CDNFUNZIONE=<%=_funzione%>&";
		    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
		    return urlpage;
 		  }

		  function indietro_lista() {
			  if (isInSubmit()) return;
			  if(flagChanged)
			  { if(!confirm("I dati sono cambiati.\nProcedere lo stesso?"))
			    { return false;
			    }
			  }
			  urlpage = getURLPageBase();
			  urlpage+="PAGE=CMDiagnosiListaPage";
			  setWindowLocation(urlpage);
		  }
          		  
	</script>

</head>


<body class="gestione" onload="rinfresca();caricaArea('1')">

	<%
	InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user, true);
	testata.setSkipLista(true);
	
	testata.show(out);
		
	//Linguette l = new Linguette(user, _funzione, "CMAttComplessePage", new BigDecimal(cdnLavoratore));
	//Linguette_Parametro l = new Linguette_Parametro(user, _funzione, "CMAttComplessePage", cdnLavoratore, "1", true);	
	LinguetteConfigurazioneRegione l = new LinguetteConfigurazioneRegione (user, _funzione, "CMAttComplessePage" , cdnLavoratore , "1", true , "LNDGNFNZ");
	
	l.show(out);
    %>  


	<af:showErrors/>
	<af:showMessages prefix="M_Insert_CapMenRel"/>
	
	<p class="titolo"></p>

	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="">

		<input type="hidden" name="PAGE" value="CMAttComplessePage" />		
		<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
		<input type="hidden" name="inserisciCapacita" value="1"/>
		<input type="hidden" name="prgDiagnosiFunzionale" value="<%=prgDiagnosiFunzionale%>"/>

<%
      SourceBean row2 = null;
      Vector rows2= serviceResponse.getAttributeAsVector("M_Load_Diagnosi_Funz_Cap.ROWS.ROW");
      if (rows2.size()==1) {
	        row2 = (SourceBean)rows2.get(0);
	        numklodiagnosifunzionale = String.valueOf(((BigDecimal)row2.getAttribute("NUMKLODIAGNOSIFUNZIONALE")).intValue());
	        cdnUtIns = (BigDecimal) row2.getAttribute("CDNUTINS");
	        dtmIns = (String) row2.getAttribute("DTMINS");
	        cdnUtMod = (BigDecimal) row2.getAttribute("CDNUTMOD");
	        dtmMod = (String) row2.getAttribute("DTMMOD");
	        operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);        
	  }
%>
		<input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">
		<input type="hidden" name="NUMKLODIAGNOSIFUNZIONALE" value="<%= numklodiagnosifunzionale %>">


		<%= htmlStreamTop %>
		<table class="main" border="0">
			<tr>
		    	<td colspan="2"/>&nbsp;</td>
		    </tr>
			<tr>
		    	<td width="75%">&nbsp;</td>
		    	<td width="25%">&nbsp;</td>
		    </tr>

<%
		//Estraggo le diverse capacità
		SourceBean row = null;
		Vector rows= new Vector();
		if(readCodiciScaduti) {
			rows= serviceResponse.getAttributeAsVector("M_Load_Descr_Cap_AttComplesse_Scad.ROWS.ROW");
		} else {
			rows= serviceResponse.getAttributeAsVector("M_Load_Descr_Cap_AttComplesse.ROWS.ROW");
		}
%>
		<input type="hidden" name="numeroCapacita" value="<%=rows.size()%>"/>
<%		
	   for(int i=0; i<rows.size(); i++) {
		    row = (SourceBean)rows.get(i);
			String codCapacita = Utils.notNull(row.getAttribute("codCapacita")); 
			String codCapRaggr = Utils.notNull(row.getAttribute("codCapRaggr")); 
			String descrizioneCap = Utils.notNull(row.getAttribute("descrizioneCap"));
			codMonoTipoGrado = Utils.notNull(row.getAttribute("codMonoTipoGrado"));
%>
			<tr>
		    	<td width="75%" class="etichetta">
		    		<%=descrizioneCap%>
		    		<%String indice = "" + i;
		    		  String codDescr = "codDescr_" + indice; %>
		    		<input type="hidden" name="<%=codDescr%>" value="<%=codCapacita%>"/>  
		    	</td>
<%
			SourceBean row1 = null;
			Vector rows1= serviceResponse.getAttributeAsVector("M_Load_CapMenRel.ROWS.ROW");
			int k=0;
    		for(int j=0; j<rows1.size(); j++) {
		        row1 = (SourceBean)rows1.get(j);
			    String codiceCapacita = Utils.notNull(row1.getAttribute("codCapacita"));
			    String codiceGradoCapacita = Utils.notNull(row1.getAttribute("codGradoCapacita"));

				if(codiceCapacita.equals(codCapacita)){
					codCapCogn = codiceGradoCapacita;
					k++;
				}
			}
			if(k==0){
				codCapCogn = "";
			}

			int p=0;
    		for(int j=0; j<rows1.size(); j++) {
		        row1 = (SourceBean)rows1.get(j);
			    String codiceCapacita = Utils.notNull(row1.getAttribute("codCapacita"));

				String strDescGrado = Utils.notNull(row1.getAttribute("strDescGrado"));

				if(codiceCapacita.equals(codCapacita)){

					descrGrado = strDescGrado;
					p++;
				}
			}
			if(p==0){
				descrGrado = "";				
			}
%>		    	
			<td class="campo" >
				<%capCogn = "capCogn_" + indice;
				  
				  if(codMonoTipoGrado.equals("C")){%>
				  	<af:comboBox name="<%=capCogn%>" title="Capacita cogn" selectedValue="<%=codCapCogn%>" required="false"
				      	  		 moduleName="M_ComboTipoCapacita" disabled="<%= String.valueOf(readCodiciScaduti) %>"
					      		 classNameBase="input" onChange="fieldChanged()" addBlank="true"/>
				  <%}else if(codMonoTipoGrado.equals("P") || codMonoTipoGrado.equals("Q")) {%>
				  	<af:comboBox name="<%=capCogn%>" title="Capacita cogn" selectedValue="<%=codCapCogn%>"
						  		 required="false" moduleName="M_ComboCapacitaSiNo" disabled="<%= String.valueOf(readCodiciScaduti) %>"
					      		 classNameBase="input" onChange="disabilitaArea(this);fieldChanged();" addBlank="true"/>
				  <%} else if(codMonoTipoGrado.equals("S") ) {%>
				  	<af:comboBox name="<%=capCogn%>" title="Capacita cogn" selectedValue="<%=codCapCogn%>" required="false"
				      	  		 moduleName="M_AltriTipiCapacita" disabled="<%= String.valueOf(readCodiciScaduti) %>"
					      		 classNameBase="input" onChange="disabilitaArea(this);fieldChanged();" addBlank="true"/>
				  <%}%>   
	        </td>
		  </tr>
		<% nomeDescrGrado = "descrGrado_" + indice;
		   if(codMonoTipoGrado.equals("P") || codMonoTipoGrado.equals("N") || codMonoTipoGrado.equals("S")){%>
		   		<tr>
					<td class="etichetta"></td>
		    		<td class="campo" >
						<af:textArea cols="20" rows="4" maxlength="2000" readonly="<%= String.valueOf(readCodiciScaduti) %>" 
									 classNameBase="input" name="<%=nomeDescrGrado%>" value="<%=descrGrado%>"  validateOnPost="true" 
                          			 required="false" title="Note" onKeyUp="fieldChanged();"/>								      					    	
		    		</td>
		    	</tr>
		    <%}%> 
		<tr><td colspan="2"/>&nbsp;</td></tr>
	<% } if (canModify && !readCodiciScaduti) { %>
		<tr><td colspan="2"/>&nbsp;</td></tr>    
		<tr>
			<td colspan="2" align="center">
				<input type="submit" class="pulsanti" name="aggiorna" value="Aggiorna" onclick="">&nbsp;&nbsp;
				<input type="reset" class="pulsanti" value="Annulla" />&nbsp;&nbsp;
			</td>
		</tr>
	<%}%>				
	</table>
	<br><br>
		
	<%out.print(htmlStreamBottom);%>

	<center>
		<table>
	    	<tr>
	      		<td align="center">
	      			<% operatoreInfo.showHTML(out); %>
	      		</td>
	      	</tr>
	    </table>
	</center>

</af:form>

<script language="javascript">

	function caricaArea(num){
  		var combo = "";
		var strDescrGrado = "";
		for (i=1;i<=num;i++) {
			combo = "capCogn_" + i;
			strDescrGrado = "descrGrado_" + i;
			if(document.Frm1.elements[strDescrGrado] != undefined) {
				if(document.Frm1.elements[combo].value == "") {
					document.Frm1.elements[strDescrGrado].value = "";
					document.Frm1.elements[strDescrGrado].disabled=true;
				} else { document.Frm1.elements[strDescrGrado].disabled=false; }
			}
		}		
	}

	function disabilitaArea(combo){
		var ultimoCarattere = combo.name.charAt(combo.name.length - 1);
		var strDescrGrado = "descrGrado_" + ultimoCarattere;
		if(document.Frm1.elements[strDescrGrado] != undefined) {
			if(combo.value == "") {
				document.Frm1.elements[strDescrGrado].value = "";
				document.Frm1.elements[strDescrGrado].disabled=true;
			} else { document.Frm1.elements[strDescrGrado].disabled=false; }
		}
	}
	
</script>
</body>
</html>
