
<%
    //Controllo se in sessione ho il CodCpi di destinazione (cdnTipoGruppo dell'utente == 1)
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	String codCpiUser = user.getCodRif();
	boolean codCpiUserNotFound = false;
	if(cdnTipoGruppo != 1) {
		codCpiUserNotFound = true;
	}
%>
<% 
  it.eng.sil.module.amministrazione.accorpamento.Indirizzi dati1 = null, dati2=null;
 //try {
    dati1 = new it.eng.sil.module.amministrazione.accorpamento.Indirizzi((SourceBean) infoLav1.getAttribute("GET_AN_LAVORATORE_INDIRIZZI.ROWS.ROW"));
    dati2 = new it.eng.sil.module.amministrazione.accorpamento.Indirizzi((SourceBean) infoLav2.getAttribute("GET_AN_LAVORATORE_INDIRIZZI.ROWS.ROW")); 
  //--}
 /*
    // non faccio niente?    
    com.engiweb.framework.tracing.TracerSingleton.log(it.eng.sil.Values.APP_NAME, com.engiweb.framework.tracing.TracerSingleton.CRITICAL, "", e);
  }
  */
  
%>


<table  class="main">
<!-- RESIDENZA E DOMICILIO -->
	<tr><td colspan="3"><div class="sezione">Domicilio</div></td></tr>
	<tr>
	    <td class="colonna1">Indirizzo</td>
	    <td class="colonna2"><af:textBox classNameBase="input"  title="indirizzo domicilio"  type="text" name="strIndirizzodom" value="<%=dati1.strIndirizzodom%>" size="40" maxlength="50" required="true" readonly="<%= String.valueOf(!canModify) %>" /></td>    
	    <td class="colonna2"><af:textBox classNameBase="input"  title="indirizzo domicilio"  type="text" name="strIndirizzodom" value="<%=dati2.strIndirizzodom%>" size="40" maxlength="50" required="true" readonly="<%= String.valueOf(!canModify) %>" /></td>    
	</tr>
	<tr>
	    <td class="colonna1">Località</td>
	    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strLocalitadom" value="<%=dati1.strLocalitadom%>" size="40" maxlength="50" readonly="<%= String.valueOf(!canModify) %>" /></td>    
	    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strLocalitadom" value="<%=dati2.strLocalitadom%>" size="40" maxlength="50" readonly="<%= String.valueOf(!canModify) %>" /></td>    
	</tr>
	<tr>
	    <td class="colonna1">Comune</td>
	    <td class="colonna2">
	        <af:textBox classNameBase="input" type="text" name="codComdom" value="<%=dati1.codComdom%>" title="codice comune domicilio"            
	            size="4" maxlength="4" readonly="<%= String.valueOf(!canModify) %>" 
	        />&nbsp;                
	        <af:textBox classNameBase="input" type="text"  name="strComdom" value="<%=dati1.strComdom%>"            
	            size="30" maxlength="50" title="comune di domicilio" 
	            readonly="<%= String.valueOf(!canModify) %>" 
	        />&nbsp;*&nbsp;&nbsp;
	    </td>
	    <td class="colonna2">
	        <af:textBox classNameBase="input" type="text" name="codComdom" value="<%=dati2.codComdom%>" title="codice comune domicilio"            
	            size="4" maxlength="4" readonly="<%= String.valueOf(!canModify) %>" 
	        />&nbsp;                
	        <af:textBox classNameBase="input" type="text"  name="strComdom" value="<%=dati2.strComdom%>"            
	            size="30" maxlength="50" title="comune di domicilio" 
	            readonly="<%= String.valueOf(!canModify) %>" 
	        />&nbsp;*&nbsp;&nbsp;
	    </td>
	<tr>	
	  <td class="colonna1">Cap</td>
	  <td class="colonna2">
	    <af:textBox classNameBase="input" name="strCapDom" value="<%=dati1.strCapDom%>"           
	          title="Cap del domicilio" type="text"  size="5" maxlength="5" readonly="<%= String.valueOf(!canModify) %>"
	      />    
	  </td>
	  <td class="colonna2">
	    <af:textBox classNameBase="input" name="strCapDom" value="<%=dati2.strCapDom%>"           
	          title="Cap del domicilio" type="text"  size="5" maxlength="5" readonly="<%= String.valueOf(!canModify) %>"
	      />    
	  </td>
	</tr>
	
	<tr>
	    <td class="colonna1">Telefono Domicilio</td>
	    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strTelDom" value="<%=dati1.strTelDom%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
	    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strTelDom" value="<%=dati2.strTelDom%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
	</tr>
	
	<tr>
	    <td class="colonna1">
	      <%-- if(codMonoTipoCpi.equals("C")){ %>
	        Centro per l'impiego ai sensi del D. Lgs 181
	      <%} else { 
	              if(codMonoTipoCpi.equals("T")){ %>
	                Centro per l'impiego titolare
	        <%    }
	          }--%>
	    </td>
	    <td class="colonna2">
	    <% if(dati1.codMonoTipoCpi.equals("C")){ %>
	        Centro per l'impiego ai sensi del D. Lgs 181
	      <%} else { 
	              if(dati1.codMonoTipoCpi.equals("T")){ %>
	                Centro per l'impiego titolare
	        <%    }
	          }%>
	        <af:textBox classNameBase="input" type="text" name="codCPI" value="<%=dati1.codCpiTit%>"             
	            size="10" maxlength="9" 
	            readonly="true"
	        />&nbsp;                
	        <af:textBox type="text" classNameBase="input" name="strCPI" value="<%=dati1.strCpiTit%>"            
	            size="40" maxlength="50"             
	            readonly="true"
	        />&nbsp;                
				<% if(dati1.codMonoTipoCpi.equals("T") && !dati1.codCpiOrig.equals("")){ %>
					<br/><STRONG>(CPI Competente:&nbsp;&nbsp; <%=dati1.codCpiOrig%>&nbsp;<%=dati1.strCpiOrig%>)</STRONG>
				<%}%>                
	  	</td> 
	  	<td class="colonna2">
	  		<% if(dati2.codMonoTipoCpi.equals("C")){ %>
	        	Centro per l'impiego ai sensi del D. Lgs 181
	      	<%} else { 
	              if(dati2.codMonoTipoCpi.equals("T")){ %>
	                Centro per l'impiego titolare
	        <% }
	          }%>
	        <af:textBox classNameBase="input" type="text" name="codCPI" value="<%=dati2.codCpiTit%>"             
	            size="10" maxlength="9" 
	            readonly="true"
	        />&nbsp;                
	        <af:textBox type="text" classNameBase="input" name="strCPI" value="<%=dati2.strCpiTit%>"            
	            size="40" maxlength="50"             
	            readonly="true"
	        />&nbsp;                
				<% if(dati2.codMonoTipoCpi.equals("T") && !dati2.codCpiOrig.equals("")){ %>
					<br/><STRONG>(CPI Competente:&nbsp;&nbsp; <%=dati2.codCpiOrig%>&nbsp;<%=dati2.strCpiOrig%>)</STRONG>
				<%}%>                
	  	</td>
	</tr>			
	<tr>
		<td COLSpan="3">
			<table class='sezione2' cellspacing=0 cellpadding=0>
	            <tr>
	                <td  width=18><img id='IMG_S_RESIDENZA' src='../../img/chiuso.gif' onclick='cambia(this, document.getElementById("S_RESIDENZA"))'></td>
	                <td  class='titolo_sezione'>Residenza</td>    				
	                <td align='right' width='30'></td>
	            </tr>
	        </table>
	    </td>
	</tr>
	<TR>
		<TD colspan="3">
			<TABLE class="main" id='S_RESIDENZA' style='display:none' >  
	        	<script>initSezioni(new Sezione(document.getElementById('S_RESIDENZA'),document.getElementById('IMG_S_REDIDENZA'),false));</script>
				<tr>
				    <td class="colonna1">Indirizzo</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strIndirizzores" value="<%=dati1.strIndirizzores%>" size="40" maxlength="50" required="true" readonly="<%= String.valueOf(!canModify) %>" /><br/></td>    
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strIndirizzores" value="<%=dati2.strIndirizzores%>" size="40" maxlength="50" required="true" readonly="<%= String.valueOf(!canModify) %>" /><br/></td>    
				</tr>
				<tr>
				    <td class="colonna1">Località</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strLocalitares" value="<%=dati1.strLocalitares%>" size="40" maxlength="50" readonly="<%= String.valueOf(!canModify) %>" /></td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strLocalitares" value="<%=dati2.strLocalitares%>" size="40" maxlength="50" readonly="<%= String.valueOf(!canModify) %>" /></td>
				</tr>
				
				<tr>
				    <td class="colonna1">Comune</td>
				    <td class="colonna2"><af:textBox classNameBase="input" 
				                                  type="text" name="codComRes" value="<%=dati1.codComRes%>" title="codice comune di residenza"
				                                  size="4" maxlength="4"  readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;        
				    <af:textBox classNameBase="input" 
				                type="text" name="strComRes" value="<%=dati1.strComRes%>" size="30" maxlength="50" title="comune di residenza" 
				                readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;*&nbsp;&nbsp;    
				    </td>
				    <td class="colonna2"><af:textBox classNameBase="input" 
				                                  type="text" name="codComRes" value="<%=dati2.codComRes%>" title="codice comune di residenza"
				                                  size="4" maxlength="4"  readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;        
				    <af:textBox classNameBase="input" 
				                type="text" name="strComRes" value="<%=dati2.strComRes%>" size="30" maxlength="50" title="comune di residenza" 
				                readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;*&nbsp;&nbsp;    
				    </td>				    
				<tr>    
				    <td class="colonna1">Cap</td>
				    <td class="colonna2">
				      <af:textBox classNameBase="input" type="text" title="Cap di residenza" name="strCapRes" value="<%=dati1.strCapRes%>" size="5" maxlength="5" readonly="<%= String.valueOf(!canModify) %>" />      
				    </td>
				    <td class="colonna2">
				      <af:textBox classNameBase="input" type="text" title="Cap di residenza" name="strCapRes" value="<%=dati2.strCapRes%>" size="5" maxlength="5" readonly="<%= String.valueOf(!canModify) %>" />      
				    </td>
				</tr>
				<tr>
				    <td class="colonna1">Telefono Residenza</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strTelRes" value="<%=dati1.strTelRes%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strTelRes" value="<%=dati2.strTelRes%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
				</tr>
				<tr><td colspan="3"><br/></td></tr>
			</table>	
		</td>
	</tr>		
	<tr><td colspan="3" style="height:5px"></td></tr>
	<tr>
		<td colspan="3">
			<table class='sezione2' cellspacing=0 cellpadding=0 >
	            <tr>
	                <td  width=18><img id='IMG_S_ALTRI_RIF' src='../../img/chiuso.gif' onclick='cambia(this, document.getElementById("S_ALTRI_RIF"))'></td>
	                <td  class='titolo_sezione'>Altri riferimenti</td>    				
	                <td align='right' width='30'></td>
	            </tr>
	        </table>
	    </td>
	</tr>
	<tr>
		<td colspan="3">
			<TABLE class="main" id='S_ALTRI_RIF' style='display:none' >  
        		<script>initSezioni(new Sezione(document.getElementById('S_ALTRI_RIF'),document.getElementById('IMG_S_ALTRI_RIF'),false));</script>				
				<tr>
				    <td class="colonna1">Altro telefono</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strTelAltro" value="<%=dati1.strTelAltro%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>    
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strTelAltro" value="<%=dati2.strTelAltro%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>    
				</tr>
				<tr>
				    <td class="colonna1">Cellulare</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strCell" value="<%=dati1.strCell%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strCell" value="<%=dati2.strCell%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
				</tr>
				<tr>
				    <td class="colonna1">Email</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strEmail" value="<%=dati1.strEmail%>" size="40" maxlength="80" readonly="<%= String.valueOf(!canModify) %>"/></td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strEmail" value="<%=dati2.strEmail%>" size="40" maxlength="80" readonly="<%= String.valueOf(!canModify) %>"/></td>
				</tr>
				<tr>
				    <td class="colonna1">Fax</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strFax" value="<%=dati1.strFax%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="strFax" value="<%=dati2.strFax%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
